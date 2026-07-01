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

import java.util.List;

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

        if ("Ski Slalom".equals(comp.getType())) {
            List<SkiSlalomResult> allResults = slalomRepository.findByCompetitionIdOrderByFirstRunTimeAsc(compId);
            List<Long> athletesWithRun1 = allResults.stream().map(r -> r.getAthlete().getId()).toList();

            model.addAttribute("athletes", athleteRepository.findAll().stream()
                    .filter(a -> !athletesWithRun1.contains(a.getId())).toList());


            List<SkiSlalomResult> pendingRun2 = allResults.stream()
                    .filter(SkiSlalomResult::isFinished)
                    .limit(5)
                    .filter(r -> r.getSecondRunTime() == null)
                    .sorted((a, b) -> b.getFirstRunTime().compareTo(a.getFirstRunTime()))
                    .toList();

            model.addAttribute("pendingRun2", pendingRun2);
        } else {
            model.addAttribute("athletes", athleteRepository.findAll());
        }

        return "add-result";
    }

    @PostMapping("/results/slalom/run1")
    public String addSlalomRun1(@RequestParam Long competitionId,
                                @RequestParam Long athleteId,
                                @RequestParam Double firstRunTime,
                                @RequestParam(defaultValue = "false") boolean dnf) {
        SkiSlalomResult result = new SkiSlalomResult();
        result.setCompetition(competitionRepository.findById(competitionId).get());
        result.setAthlete(athleteRepository.findById(athleteId).get());
        result.setFirstRunTime(firstRunTime);
        result.setFinished(!dnf);
        slalomRepository.save(result);
        return "redirect:/admin/results/add?compId=" + competitionId;
    }

    @PostMapping("/results/slalom/run2")
    public String addSlalomRun2(@RequestParam Long resultId,
                                @RequestParam Long competitionId,
                                @RequestParam Double secondRunTime,
                                @RequestParam(defaultValue = "false") boolean dnf) {
        SkiSlalomResult result = slalomRepository.findById(resultId).get();
        result.setSecondRunTime(secondRunTime);
        if (!dnf && result.isFinished()) {
            result.setTotalTime(result.getFirstRunTime() + secondRunTime);
        } else {
            result.setFinished(false);
        }
        slalomRepository.save(result);
        return "redirect:/admin/results/add?compId=" + competitionId;
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
        result.setPenaltyTime(misses * 1.0);
        result.setTotalTime(skiingTime + result.getPenaltyTime());
        biathlonRepository.save(result);
        return "redirect:/admin/dashboard?success";
    }

    @PostMapping("/competitions/delete")
    public String deleteCompetition(@RequestParam Long id) {
        List<SkiSlalomResult> slalomResults = slalomRepository.findByCompetitionIdOrderByFirstRunTimeAsc(id);
        slalomRepository.deleteAll(slalomResults);

        List<BiathlonResult> biathlonResults = biathlonRepository.findByCompetitionIdOrderByTotalTimeAsc(id);
        biathlonRepository.deleteAll(biathlonResults);

        competitionRepository.deleteById(id);
        return "redirect:/admin/results?deleted";
    }

    @PostMapping("/results/slalom/delete")
    public String deleteSlalomResult(@RequestParam Long resultId, @RequestParam Long compId) {
        slalomRepository.deleteById(resultId);
        return "redirect:/public/competitions/" + compId;
    }

    @PostMapping("/results/biathlon/delete")
    public String deleteBiathlonResult(@RequestParam Long resultId, @RequestParam Long compId) {
        biathlonRepository.deleteById(resultId);
        return "redirect:/public/competitions/" + compId;
    }

    @GetMapping("/results/edit")
    public String showEditResultForm(@RequestParam Long resultId, @RequestParam String type, Model model) {
        if ("slalom".equals(type)) {
            SkiSlalomResult result = slalomRepository.findById(resultId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid result Id"));
            model.addAttribute("result", result);
            model.addAttribute("competition", result.getCompetition());
            model.addAttribute("type", type);
        } else if ("biathlon".equals(type)) {
            BiathlonResult result = biathlonRepository.findById(resultId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid result Id"));
            model.addAttribute("result", result);
            model.addAttribute("competition", result.getCompetition());
            model.addAttribute("type", type);
        }
        return "edit-result";
    }

    @PostMapping("/results/slalom/edit")
    public String editSlalomResult(@RequestParam Long resultId,
                                   @RequestParam Double firstRunTime,
                                   @RequestParam(required = false) Double secondRunTime,
                                   @RequestParam(defaultValue = "false") boolean dnf) {
        SkiSlalomResult result = slalomRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid result Id"));

        result.setFirstRunTime(firstRunTime);
        result.setFinished(!dnf);

        if (dnf) {
            result.setSecondRunTime(null);
            result.setTotalTime(null);
        } else {
            result.setSecondRunTime(secondRunTime);
            if (secondRunTime != null) {
                result.setTotalTime(firstRunTime + secondRunTime);
            } else {
                result.setTotalTime(null);
            }
        }

        slalomRepository.save(result);
        return "redirect:/public/competitions/" + result.getCompetition().getId();
    }

    @PostMapping("/results/biathlon/edit")
    public String editBiathlonResult(@RequestParam Long resultId,
                                     @RequestParam Double skiingTime,
                                     @RequestParam Integer misses) {
        BiathlonResult result = biathlonRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid result Id"));

        result.setSkiingTime(skiingTime);
        result.setMisses(misses);
        result.setPenaltyTime(misses * 1.0);
        result.setTotalTime(skiingTime + result.getPenaltyTime());

        biathlonRepository.save(result);
        return "redirect:/public/competitions/" + result.getCompetition().getId();
    }

    @GetMapping("/competitions/edit")
    public String showEditCompetitionForm(@RequestParam("id") Long id, Model model) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid competition Id"));
        model.addAttribute("competition", competition);
        return "edit-competition";
    }

    @PostMapping("/competitions/edit")
    public String editCompetition(@ModelAttribute Competition competition) {
        competitionRepository.save(competition);
        return "redirect:/admin/results?updated";
    }
}