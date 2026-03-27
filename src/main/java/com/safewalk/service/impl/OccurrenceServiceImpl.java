package com.safewalk.service.impl;

import com.safewalk.dto.OccurrenceRequest;
import com.safewalk.dto.OccurrenceResponse;
import com.safewalk.exception.ResourceNotFoundException;
import com.safewalk.exception.UnauthorizedException;
import com.safewalk.model.Occurrence;
import com.safewalk.model.User;
import com.safewalk.model.enums.OccurrenceEnum;
import com.safewalk.model.enums.RiskLevelEnum;
import com.safewalk.repository.OccurrenceRepository;
import com.safewalk.repository.UserRepository;
import com.safewalk.service.OccurrenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OccurrenceServiceImpl implements OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    @Transactional
    public OccurrenceResponse create(OccurrenceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));


        Occurrence occurrence = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao(request.getType()))
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .risk(RiskLevelEnum.fromDescricao(request.getRisk()))
                .user(user)
                .anonymous(request.getAnonymous())
                .build();

        occurrence = occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OccurrenceResponse> findAll() {
        return occurrenceRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OccurrenceResponse> findByUserId(Long userId) {
        return occurrenceRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OccurrenceResponse findById(Long id) {
        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));
        return mapToResponse(occurrence);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));

        if (!occurrence.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para deletar esta ocorrência");
        }

        occurrenceRepository.delete(occurrence);
    }

    @Override
    @Transactional
    public OccurrenceResponse update(Long id, OccurrenceRequest request, Long userId) {

        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));

        if (!occurrence.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Sem permissão");
        }

        occurrence.setType(OccurrenceEnum.fromDescricao(request.getType()));
        occurrence.setDescription(request.getDescription());
        occurrence.setLocation(request.getLocation());
        occurrence.setRisk(RiskLevelEnum.fromDescricao(request.getRisk()));
        occurrence.setAnonymous(request.getAnonymous());

        occurrence = occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    private OccurrenceResponse mapToResponse(Occurrence occurrence) {
        return OccurrenceResponse.builder()
                .id(occurrence.getId())
                .type(occurrence.getType().getDescricao())
                .description(occurrence.getDescription())
                .latitude(occurrence.getLatitude())
                .longitude(occurrence.getLongitude())
                .location(occurrence.getLocation())
                .risk(occurrence.getRisk().getDescricao())
                .userId(occurrence.getUser().getId())
                .userName(occurrence.getAnonymous() ? "Anônimo" : occurrence.getUser().getName())
                .createdAt(occurrence.getCreatedAt().format(FORMATTER))
                .build();
    }
}
