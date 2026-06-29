package com.olympics.controller;

import com.olympics.model.Competition;
import com.olympics.repository.CompetitionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class CompetitionController {

    private final CompetitionRepository competitionRepository;

    public CompetitionController(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @GetMapping("/public/competitions")
    public String listCompetitions(Model model) {
        List<Competition> competitions = competitionRepository.findAll();
        model.addAttribute("competitions", competitions);
        return "competitions";
    }
}