package com.olympics.controller;

import com.olympics.model.Competition;
import com.olympics.model.SkiSlalomResult;
import com.olympics.repository.BiathlonResultRepository;
import com.olympics.repository.CompetitionRepository;
import com.olympics.repository.SkiSlalomResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CompetitionController {

    private final CompetitionRepository competitionRepository;
    private final SkiSlalomResultRepository slalomRepository;
    private final BiathlonResultRepository biathlonRepository;

    public CompetitionController(CompetitionRepository competitionRepository,
                                 SkiSlalomResultRepository slalomRepository,
                                 BiathlonResultRepository biathlonRepository) {
        this.competitionRepository = competitionRepository;
        this.slalomRepository = slalomRepository;
        this.biathlonRepository = biathlonRepository;
    }

    @GetMapping("/public/competitions")
    public String listCompetitions(Model model) {
        List<Competition> competitions = competitionRepository.findAll();
        model.addAttribute("competitions", competitions);
        return "competitions";
    }

    @GetMapping("/public/competitions/{id}")
    public String viewCompetitionResults(@PathVariable Long id, Model model) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid competition Id"));

        model.addAttribute("competition", competition);

        if ("Ski Slalom".equals(competition.getType())) {
            List<SkiSlalomResult> slalomResults = slalomRepository.findByCompetitionId(id);


            slalomResults.sort((a, b) -> {

                if (a.getTotalTime() != null && b.getTotalTime() != null) {
                    return a.getTotalTime().compareTo(b.getTotalTime());
                }
                if (a.getTotalTime() != null) return -1;
                if (b.getTotalTime() != null) return 1;

                if (!a.isFinished() && b.isFinished()) return 1;
                if (a.isFinished() && !b.isFinished()) return -1;

                // ако двама имат DNQ сравняваме 1ви манш
                if (a.getFirstRunTime() != null && b.getFirstRunTime() != null) {
                    return a.getFirstRunTime().compareTo(b.getFirstRunTime());
                }
                return 0;
            });

            model.addAttribute("results", slalomResults);
        } else if ("Biathlon".equals(competition.getType())) {
            model.addAttribute("results", biathlonRepository.findByCompetitionIdOrderByTotalTimeAsc(id));
        }

        return "competition-results";
    }
}