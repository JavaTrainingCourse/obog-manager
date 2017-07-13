/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import java.util.Arrays

@SpringBootApplication
class App

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}
