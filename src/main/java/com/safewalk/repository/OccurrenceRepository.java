package com.safewalk.repository;

import com.safewalk.model.Occurrence;
import com.safewalk.model.enums.RiskLevelEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
    List<Occurrence> findByUserId(Long userId);
    List<Occurrence> findByRisk(RiskLevelEnum risk);
    List<Occurrence> findAllByOrderByCreatedAtDesc();
}
