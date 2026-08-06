package com.example.ratelimiter.config;

import com.example.ratelimiter.entity.AppUser;
import com.example.ratelimiter.entity.Role;
import com.example.ratelimiter.repository.AppUserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(AppUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .roles(Set.of(Role.ROLE_ADMIN))
                    .active(true)
                    .build());
            log.info("Default admin user created");
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(AppUser.builder()
                    .username("user")
                    .password(passwordEncoder.encode("userpass"))
                    .roles(Set.of(Role.ROLE_USER))
                    .active(true)
                    .build());
            log.info("Default user created");
        }
    }
}
