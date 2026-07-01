package com.olympics.controller;

import com.olympics.model.Athlete;
import com.olympics.service.OlympicsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OlympicsController {

    private final OlympicsService olympicsService;

    public OlympicsController(OlympicsService olympicsService) {
        this.olympicsService = olympicsService;
    }

    @GetMapping("/public/olympics")
    public String viewOlympicsDashboard(Model model) {
        model.addAttribute("medalTally", olympicsService.getMedalTally());
        model.addAttribute("avgAge", String.format("%.1f", olympicsService.getAverageAgeOfParticipants()));

        Athlete youngest = olympicsService.getYoungestMedalist();
        Athlete oldest = olympicsService.getOldestMedalist();

        model.addAttribute("youngestMedalist", youngest);
        model.addAttribute("youngestAge", olympicsService.getAge(youngest));

        model.addAttribute("oldestMedalist", oldest);
        model.addAttribute("oldestAge", olympicsService.getAge(oldest));

        return "olympics-dashboard";
    }
}