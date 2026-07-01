package com.olympics.controller;

import com.olympics.model.Athlete;
import com.olympics.repository.AthleteRepository;
import com.olympics.repository.BiathlonResultRepository;
import com.olympics.repository.SkiSlalomResultRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/athlete")
public class AthleteController {

    private final AthleteRepository athleteRepository;
    private final SkiSlalomResultRepository slalomRepository;
    private final BiathlonResultRepository biathlonRepository;

    public AthleteController(AthleteRepository athleteRepository,
                             SkiSlalomResultRepository slalomRepository,
                             BiathlonResultRepository biathlonRepository) {
        this.athleteRepository = athleteRepository;
        this.slalomRepository = slalomRepository;
        this.biathlonRepository = biathlonRepository;
    }

    @GetMapping("/profile")
    public String viewProfile(@RequestParam(required = false) Long athleteId, Authentication authentication, Model model) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Athlete selectedAthlete = null;

        if (isAdmin) {
            model.addAttribute("athletes", athleteRepository.findAll());
            if (athleteId != null) {
                selectedAthlete = athleteRepository.findById(athleteId).orElse(null);
            }
        } else {
            String currentUsername = authentication.getName();
            selectedAthlete = athleteRepository.findByUsername(currentUsername).orElse(null);
        }

        if (selectedAthlete != null) {
            model.addAttribute("selectedAthlete", selectedAthlete);
            model.addAttribute("slalomResults", slalomRepository.findByAthleteId(selectedAthlete.getId()));
            model.addAttribute("biathlonResults", biathlonRepository.findByAthleteId(selectedAthlete.getId()));
        }

        return "athlete-profile";
    }
}