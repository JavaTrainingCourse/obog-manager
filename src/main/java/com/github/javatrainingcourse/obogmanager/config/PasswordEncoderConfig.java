/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author mikan
 * @since 0.1
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
