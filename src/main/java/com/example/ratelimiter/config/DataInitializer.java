package com.example.ratelimiter.config;

import com.example.ratelimiter.entity.AppUser;
import com.example.ratelimiter.entity.Role;
import com.example.ratelimiter.repository.AppUserRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new AppUser(
                    null,
                    "admin",
                    passwordEncoder.encode("adminpass"),
                    Set.of(Role.ROLE_ADMIN),
                    true
            ));
            log.info("Default admin user created");
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(new AppUser(
                    null,
                    "user",
                    passwordEncoder.encode("userpass"),
                    Set.of(Role.ROLE_USER),
                    true
            ));
            log.info("Default user created");
        }
    }
}
