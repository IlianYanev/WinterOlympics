package com.olympics.controller;

import com.olympics.dto.AthleteRegistrationDto;
import com.olympics.service.AthleteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AthleteService athleteService;

    public AuthController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("athleteDto", new AthleteRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerAthlete(@ModelAttribute AthleteRegistrationDto athleteDto) {
        athleteService.registerAthlete(athleteDto);
        return "redirect:/login?registered";
    }
}