package com.olympics.service;

import com.olympics.dto.CountryMedalDto;
import com.olympics.model.Athlete;
import com.olympics.model.Competition;
import com.olympics.model.SkiSlalomResult;
import com.olympics.model.BiathlonResult;
import com.olympics.repository.CompetitionRepository;
import com.olympics.repository.SkiSlalomResultRepository;
import com.olympics.repository.BiathlonResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OlympicsService {

    private final CompetitionRepository competitionRepository;
    private final SkiSlalomResultRepository slalomRepository;
    private final BiathlonResultRepository biathlonRepository;

    public OlympicsService(CompetitionRepository competitionRepository,
                           SkiSlalomResultRepository slalomRepository,
                           BiathlonResultRepository biathlonRepository) {
        this.competitionRepository = competitionRepository;
        this.slalomRepository = slalomRepository;
        this.biathlonRepository = biathlonRepository;
    }

    public List<CountryMedalDto> getMedalTally() {
        Map<String, CountryMedalDto> tallyMap = new HashMap<>();
        List<Competition> competitions = competitionRepository.findAll();

        for (Competition comp : competitions) {
            List<Athlete> medalists = getTop3ForCompetition(comp);
            for (int i = 0; i < medalists.size(); i++) {
                Athlete athlete = medalists.get(i);
                tallyMap.putIfAbsent(athlete.getCountry(), new CountryMedalDto(athlete.getCountry()));
                CountryMedalDto dto = tallyMap.get(athlete.getCountry());
                if (i == 0) dto.setGold(dto.getGold() + 1);
                else if (i == 1) dto.setSilver(dto.getSilver() + 1);
                else if (i == 2) dto.setBronze(dto.getBronze() + 1);
            }
        }

        return tallyMap.values().stream()
                .sorted((a, b) -> {
                    if (b.getGold() != a.getGold()) return Integer.compare(b.getGold(), a.getGold());
                    if (b.getSilver() != a.getSilver()) return Integer.compare(b.getSilver(), a.getSilver());
                    if (b.getBronze() != a.getBronze()) return Integer.compare(b.getBronze(), a.getBronze());
                    return Integer.compare(b.getTotal(), a.getTotal());
                })
                .collect(Collectors.toList());
    }

    public List<Athlete> getTop3ForCompetition(Competition comp) {
        List<Athlete> medalists = new ArrayList<>();
        if ("Ski Slalom".equals(comp.getType())) {
            List<SkiSlalomResult> results = slalomRepository.findByCompetitionId(comp.getId());
            results.sort((a, b) -> {
                if (a.getTotalTime() != null && b.getTotalTime() != null) return a.getTotalTime().compareTo(b.getTotalTime());
                if (a.getTotalTime() != null) return -1;
                if (b.getTotalTime() != null) return 1;
                return 0;
            });
            for (SkiSlalomResult res : results) {
                if (res.getTotalTime() != null && res.isFinished()) {
                    medalists.add(res.getAthlete());
                    if (medalists.size() == 3) break;
                }
            }
        } else if ("Biathlon".equals(comp.getType())) {
            List<BiathlonResult> results = biathlonRepository.findByCompetitionIdOrderByTotalTimeAsc(comp.getId());
            for (BiathlonResult res : results) {
                if (res.isFinished()) {
                    medalists.add(res.getAthlete());
                    if (medalists.size() == 3) break;
                }
            }
        }
        return medalists;
    }

    public double getAverageAgeOfParticipants() {
        Set<Athlete> participants = getAllParticipants();
        if (participants.isEmpty()) return 0.0;

        double totalAge = 0;
        for (Athlete athlete : participants) {
            totalAge += Period.between(athlete.getBirthDate(), LocalDate.now()).getYears();
        }
        return totalAge / participants.size();
    }

    public Athlete getYoungestMedalist() {
        return getAllMedalists().stream()
                .max(Comparator.comparing(Athlete::getBirthDate))
                .orElse(null);
    }

    public Athlete getOldestMedalist() {
        return getAllMedalists().stream()
                .min(Comparator.comparing(Athlete::getBirthDate))
                .orElse(null);
    }

    public int getAge(Athlete athlete) {
        if (athlete == null) return 0;
        return Period.between(athlete.getBirthDate(), LocalDate.now()).getYears();
    }

    private Set<Athlete> getAllParticipants() {
        Set<Athlete> participants = new HashSet<>();
        slalomRepository.findAll().forEach(r -> participants.add(r.getAthlete()));
        biathlonRepository.findAll().forEach(r -> participants.add(r.getAthlete()));
        return participants;
    }

    private Set<Athlete> getAllMedalists() {
        Set<Athlete> medalists = new HashSet<>();
        for (Competition comp : competitionRepository.findAll()) {
            medalists.addAll(getTop3ForCompetition(comp));
        }
        return medalists;
    }
}