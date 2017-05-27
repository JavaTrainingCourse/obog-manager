/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class App {

    public static final long OBOG_MANAGER_SERIAL_VERSION_UID = 1L;
    public static final String OBOG_MANAGER_VERSION = "0.1";
    public static final List<String> AUTHORS = Arrays.asList(
            "Yutaka Kato",
            ""
    );

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
