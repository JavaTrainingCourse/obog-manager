/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model;

import lombok.*;
import org.pegdown.PegDownProcessor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "convocations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Convocation {

    @Id
    @Column(name = "event_id")
    @GeneratedValue
    @Getter
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date targetDate;

    @Column(nullable = false, length = 64)
    @Getter
    @Setter
    private String subject;

    @Column(nullable = false, length = 1024)
    @Setter
    private String description;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date lastUpdateDate;

    public LocalDate getTargetDate() {
        return new java.sql.Date(targetDate.getTime()).toLocalDate();
    }

    public void setTargetDate(LocalDate date) {
        targetDate = java.sql.Date.valueOf(date);
    }

    public String getDescription() {
        if (description == null) {
            return null;
        }
        return new PegDownProcessor().markdownToHtml(description);
    }
}
