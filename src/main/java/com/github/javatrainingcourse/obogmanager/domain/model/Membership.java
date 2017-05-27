/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "members")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
    @Getter
    private Long id;

    @Column(nullable = false, length = 16)
    @Getter
    @Setter
    private String firstName;

    @Column(nullable = false, length = 16)
    @Getter
    @Setter
    private String lastName;

    @Column(nullable = false, length = 128, unique = true)
    @Getter
    @Setter
    private String email;

    @Column(length = 256)
    @Getter
    @Setter
    private String hashedPassword;

    @Column
    @Getter
    @Setter
    private boolean admin;

    @Column(length = 128)
    @Getter
    @Setter
    private String github;

    @Column(length = 128)
    @Getter
    @Setter
    private String twitter;

    @Column(length = 128)
    @Getter
    @Setter
    private String facebook;

    @Column
    @Getter
    @Setter
    private int javaTerm; // -1: not graduate, 0: term unknown, 1~: number of term

    @Column
    @Getter
    @Setter
    private int java8Term; // -1: not graduate, 0: term unknown, 1~: number of term

    @Column
    @Getter
    @Setter
    private int goTerm; // -1: not graduate, 0: term unknown, 1~: number of term

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date lastLoginDate;

    public String getName() {
        return lastName + " " + firstName;
    }
}
