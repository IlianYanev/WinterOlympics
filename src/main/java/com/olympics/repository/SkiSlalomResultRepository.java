package com.olympics.repository;

import com.olympics.model.SkiSlalomResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkiSlalomResultRepository extends JpaRepository<SkiSlalomResult, Long> {
    List<SkiSlalomResult> findByCompetitionId(Long competitionId);
    List<SkiSlalomResult> findByAthleteId(Long athleteId);
    List<SkiSlalomResult> findByCompetitionIdOrderByFirstRunTimeAsc(Long competitionId);
}