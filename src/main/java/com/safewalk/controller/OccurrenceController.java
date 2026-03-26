package com.safewalk.controller;

import com.safewalk.dto.OccurrenceRequest;
import com.safewalk.dto.OccurrenceResponse;
import com.safewalk.security.UserPrincipal;
import com.safewalk.service.impl.OccurrenceServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {

    private final OccurrenceServiceImpl occurrenceService;

    @PostMapping
    public ResponseEntity<OccurrenceResponse> create(
            @Valid @RequestBody OccurrenceRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OccurrenceResponse response = occurrenceService.create(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OccurrenceResponse>> findAll() {
        List<OccurrenceResponse> occurrences = occurrenceService.findAll();
        return ResponseEntity.ok(occurrences);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OccurrenceResponse>> findMy(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OccurrenceResponse> occurrences = occurrenceService.findByUserId(userPrincipal.getId());
        System.out.println("CHEGOU:" + occurrences);
        return ResponseEntity.ok(occurrences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OccurrenceResponse> findById(@PathVariable Long id) {
        OccurrenceResponse occurrence = occurrenceService.findById(id);
        return ResponseEntity.ok(occurrence);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        occurrenceService.delete(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<OccurrenceResponse> update(@Valid @RequestBody OccurrenceRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OccurrenceResponse response = occurrenceService.update(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
