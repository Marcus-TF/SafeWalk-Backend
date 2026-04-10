package com.safewalk.service.impl;

import com.safewalk.dto.HotspotDTO;
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
import java.util.ArrayList;
import java.util.Iterator;
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
                .type(OccurrenceEnum.fromDescription(request.getType()))
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .risk(RiskLevelEnum.fromDescription(request.getRisk()))
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

        occurrence.setType(OccurrenceEnum.fromDescription(request.getType()));
        occurrence.setDescription(request.getDescription());
        occurrence.setLocation(request.getLocation());
        occurrence.setRisk(RiskLevelEnum.fromDescription(request.getRisk()));
        occurrence.setAnonymous(request.getAnonymous());

        occurrence = occurrenceRepository.save(occurrence);

        return mapToResponse(occurrence);
    }

    private OccurrenceResponse mapToResponse(Occurrence occurrence) {
        return OccurrenceResponse.builder()
                .id(occurrence.getId())
                .type(occurrence.getType().getDescription())
                .description(occurrence.getDescription())
                .latitude(occurrence.getLatitude())
                .longitude(occurrence.getLongitude())
                .location(occurrence.getLocation())
                .risk(occurrence.getRisk().getDescription())
                .userId(occurrence.getUser().getId())
                .userName(occurrence.getAnonymous() ? "Anônimo" : occurrence.getUser().getName())
                .createdAt(occurrence.getCreatedAt().format(FORMATTER))
                .isActive(occurrence.getIsActive() != null ? occurrence.getIsActive() : false)
                .build();
    }

    @Transactional
    public OccurrenceResponse validateOccurrence(Long id) {
        Occurrence occurrence = occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada"));
        
        occurrence.setIsActive(true);
        occurrence = occurrenceRepository.save(occurrence);
        
        return mapToResponse(occurrence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotspotDTO> getHotspots() {
        List<OccurrenceResponse> allResponses = findAll();
        List<HotspotDTO> hotspots = new ArrayList<>();
        
        // Filter out occurrences with missing coordinates to prevent NullPointerException
        List<OccurrenceResponse> available = allResponses.stream()
                .filter(occ -> occ.getLatitude() != null && occ.getLongitude() != null)
                .collect(Collectors.toList());

        while (!available.isEmpty()) {
            OccurrenceResponse current = available.remove(0);
            List<OccurrenceResponse> cluster = new ArrayList<>();
            cluster.add(current);

            Iterator<OccurrenceResponse> iter = available.iterator();
            while (iter.hasNext()) {
                OccurrenceResponse other = iter.next();
                if (haversineDistance(current.getLatitude(), current.getLongitude(),
                        other.getLatitude(), other.getLongitude()) <= 0.2) {
                    cluster.add(other);
                    iter.remove();
                }
            }

            if (cluster.size() >= 5) {
                hotspots.add(buildHotspot(cluster));
            }
        }

        return hotspots;
    }

    private HotspotDTO buildHotspot(List<OccurrenceResponse> cluster) {
        double sumLat = 0;
        double sumLon = 0;
        int countHigh = 0;
        int countMedium = 0;
        int countLow = 0;

        for (OccurrenceResponse occ : cluster) {
            sumLat += occ.getLatitude();
            sumLon += occ.getLongitude();

            if ("Alto".equalsIgnoreCase(occ.getRisk())) {
                countHigh++;
            } else if ("Médio".equalsIgnoreCase(occ.getRisk())) {
                countMedium++;
            } else {
                countLow++;
            }
        }

        String dominantRisk = "Baixo";
        if (countHigh >= countMedium && countHigh >= countLow) {
            dominantRisk = "Alto";
        } else if (countMedium >= countLow) {
            dominantRisk = "Médio";
        }

        return HotspotDTO.builder()
                .id((long) cluster.hashCode()) 
                .latitude(sumLat / cluster.size())
                .longitude(sumLon / cluster.size())
                .risk(dominantRisk)
                .occurrencesList(cluster)
                .build();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
