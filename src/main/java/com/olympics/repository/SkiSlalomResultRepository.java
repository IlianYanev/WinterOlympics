package com.olympics.repository;

import com.olympics.model.SkiSlalomResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkiSlalomResultRepository extends JpaRepository<SkiSlalomResult, Long> {

    List<SkiSlalomResult> findByCompetitionIdAndIsFinishedTrueOrderByTotalTimeAsc(Long competitionId);

    List<SkiSlalomResult> findByCompetitionIdAndIsFinishedTrueOrderByFirstRunTimeAsc(Long competitionId);
}