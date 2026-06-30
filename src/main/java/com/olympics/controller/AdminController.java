package com.olympics.controller;

import com.olympics.model.Competition;
import com.olympics.model.SkiSlalomResult;
import com.olympics.model.BiathlonResult;
import com.olympics.repository.AthleteRepository;
import com.olympics.repository.CompetitionRepository;
import com.olympics.repository.SkiSlalomResultRepository;
import com.olympics.repository.BiathlonResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CompetitionRepository competitionRepository;
    private final AthleteRepository athleteRepository;
    private final SkiSlalomResultRepository slalomRepository;
    private final BiathlonResultRepository biathlonRepository;

    public AdminController(CompetitionRepository competitionRepository,
                           AthleteRepository athleteRepository,
                           SkiSlalomResultRepository slalomRepository,
                           BiathlonResultRepository biathlonRepository) {
        this.competitionRepository = competitionRepository;
        this.athleteRepository = athleteRepository;
        this.slalomRepository = slalomRepository;
        this.biathlonRepository = biathlonRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/competitions/new")
    public String showAddCompetitionForm(Model model) {
        model.addAttribute("competition", new Competition());
        return "add-competition";
    }

    @PostMapping("/competitions/new")
    public String addCompetition(@ModelAttribute Competition competition) {
        competitionRepository.save(competition);
        return "redirect:/admin/dashboard?success";
    }

    @GetMapping("/results")
    public String manageResults(Model model) {
        model.addAttribute("competitions", competitionRepository.findAll());
        return "admin-results";
    }

    @GetMapping("/results/add")
    public String showAddResultForm(@RequestParam("compId") Long compId, Model model) {
        Competition comp = competitionRepository.findById(compId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid competition Id"));
        model.addAttribute("competition", comp);
        model.addAttribute("athletes", athleteRepository.findAll());
        return "add-result";
    }

    @PostMapping("/results/slalom")
    public String addSlalomResult(@RequestParam Long competitionId,
                                  @RequestParam Long athleteId,
                                  @RequestParam Double firstRunTime,
                                  @RequestParam Double secondRunTime) {
        SkiSlalomResult result = new SkiSlalomResult();
        result.setCompetition(competitionRepository.findById(competitionId).get());
        result.setAthlete(athleteRepository.findById(athleteId).get());
        result.setFirstRunTime(firstRunTime);
        result.setSecondRunTime(secondRunTime);
        result.setTotalTime(firstRunTime + secondRunTime);
        slalomRepository.save(result);
        return "redirect:/admin/dashboard?success";
    }

    @PostMapping("/results/biathlon")
    public String addBiathlonResult(@RequestParam Long competitionId,
                                    @RequestParam Long athleteId,
                                    @RequestParam Double skiingTime,
                                    @RequestParam Integer misses) {
        BiathlonResult result = new BiathlonResult();
        result.setCompetition(competitionRepository.findById(competitionId).get());
        result.setAthlete(athleteRepository.findById(athleteId).get());
        result.setSkiingTime(skiingTime);
        result.setMisses(misses);
        result.setPenaltyTime(misses * 1.0); // 1 минута наказание за пропуск
        result.setTotalTime(skiingTime + result.getPenaltyTime());
        biathlonRepository.save(result);
        return "redirect:/admin/dashboard?success";
    }
}