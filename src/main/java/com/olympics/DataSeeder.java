package com.olympics;

import com.olympics.model.Athlete;
import com.olympics.model.User;
import com.olympics.model.UserRole;
import com.olympics.repository.AthleteRepository;
import com.olympics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, AthleteRepository athleteRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.athleteRepository = athleteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(UserRole.ADMIN));
            userRepository.save(admin);

            User athleteUser = new User();
            athleteUser.setUsername("seeded_athlete");
            athleteUser.setPassword(passwordEncoder.encode("athlete123"));
            athleteUser.setRoles(Set.of(UserRole.ATHLETE));
            userRepository.save(athleteUser);
        }

        if (athleteRepository.count() == 0) {
            Athlete athlete = new Athlete();
            athlete.setName("Seeded Athlete");
            athlete.setCountry("Bulgaria");
            athlete.setGender("M");
            athlete.setBirthDate(LocalDate.of(2000, 3, 15));
            athleteRepository.save(athlete);
        }
    }
}