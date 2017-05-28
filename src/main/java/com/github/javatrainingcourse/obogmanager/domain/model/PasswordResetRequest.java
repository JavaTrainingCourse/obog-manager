/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * パスワードリセット要求のトークンを提供するエンティティです。
 * トークンの有効期限はとりあえず24時間です。
 *
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "password_resets")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    @Id
    @Column
    @Getter
    private String token;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.DETACH)
    @Getter
    private Membership membership;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date generatedTime;

    public PasswordResetRequest(Membership membership) {
        token = UUID.randomUUID().toString();
        generatedTime = new Date();
        this.membership = membership;
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime generated = LocalDateTime.ofInstant(generatedTime.toInstant(), ZoneId.systemDefault());
        return now.isAfter(generated.plusDays(1));
    }
}
