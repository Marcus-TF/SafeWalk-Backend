package com.safewalk.service;

import com.safewalk.dto.OccurrenceRequest;
import com.safewalk.dto.OccurrenceResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface OccurrenceService {
    OccurrenceResponse create(OccurrenceRequest request, Long userId);

    List<OccurrenceResponse> findAll();

    List<OccurrenceResponse> findByUserId(Long userId);

    OccurrenceResponse findById(Long id);

    void delete(Long id, Long userId);

    OccurrenceResponse update(Long id, OccurrenceRequest request, Long userId);
}
