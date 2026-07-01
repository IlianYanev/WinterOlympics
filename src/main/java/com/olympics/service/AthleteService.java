package com.olympics.service;

import com.olympics.dto.AthleteRegistrationDto;
import com.olympics.model.Athlete;
import com.olympics.model.User;
import com.olympics.model.UserRole;
import com.olympics.repository.AthleteRepository;
import com.olympics.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Set;

@Service
public class AthleteService {

    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;
    private final PasswordEncoder passwordEncoder;

    public AthleteService(UserRepository userRepository, AthleteRepository athleteRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.athleteRepository = athleteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerAthlete(AthleteRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(UserRole.ATHLETE));
        userRepository.save(user);

        Athlete athlete = new Athlete();
        athlete.setName(dto.getName());
        athlete.setCountry(dto.getCountry());
        athlete.setGender(dto.getGender());
        athlete.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        athlete.setUsername(dto.getUsername());
        athleteRepository.save(athlete);
    }
}