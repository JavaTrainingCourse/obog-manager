/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author mikan
 * @since 0.1
 */
@Configuration
public class MailConfig {

    @Bean
    MailSender mailSender() {
        return new MailSender() {

            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
                // do nothing
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                // do nothing
            }
        };
    }
}
