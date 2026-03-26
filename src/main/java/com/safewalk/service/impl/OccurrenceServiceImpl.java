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
import jakarta.validation.Valid;
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

    @Transactional
    @Override
    public OccurrenceResponse create(OccurrenceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));


        Occurrence occurrence = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao(request.getType()))
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .risk(RiskLevelEnum.fromDescricao(request.getRisk().toUpperCase()))
                .user(user)
                .anonymous(request.getAnonymous())
                .build();

        occurrence = occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    @Transactional
    @Override
    public List<OccurrenceResponse> findAll() {
        return occurrenceRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<OccurrenceResponse> findByUserId(Long userId) {
        return occurrenceRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OccurrenceResponse findById(Long id) {
        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));
        return mapToResponse(occurrence);
    }

    @Transactional
    @Override
    public void delete(Long id, Long userId) {
        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));

        if (!occurrence.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para deletar esta ocorrência");
        }

        occurrenceRepository.delete(occurrence);
    }

    @Override
    public OccurrenceResponse update(@Valid OccurrenceRequest request, Long id) {
        occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));


        Occurrence occurrence = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao(request.getType()))
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .risk(RiskLevelEnum.fromDescricao(request.getRisk().toUpperCase()))
                .anonymous(request.getAnonymous())
                .build();

       occurrenceRepository.save(occurrence);
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
