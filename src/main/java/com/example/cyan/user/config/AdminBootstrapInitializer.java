package com.example.cyan.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.cyan.user.model.User;
import com.example.cyan.user.service.UserService;

@Component
public class AdminBootstrapInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapInitializer.class);

    private final AdminBootstrapProperties properties;
    private final UserService userService;

    public AdminBootstrapInitializer(AdminBootstrapProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("Admin bootstrap is disabled");
            return;
        }

        User user = userService.ensureAdminUser(
                properties.getEmail(),
                properties.getPassword(),
                properties.getFullName());

        log.info("Admin bootstrap ready for email {}", user.getEmail());
    }
}
