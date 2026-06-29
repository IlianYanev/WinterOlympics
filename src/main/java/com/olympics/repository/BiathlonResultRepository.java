package com.olympics.repository;

import com.olympics.model.BiathlonResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BiathlonResultRepository extends JpaRepository<BiathlonResult, Long> {

    List<BiathlonResult> findByCompetitionIdAndIsFinishedTrueOrderByTotalTimeAsc(Long competitionId);
}