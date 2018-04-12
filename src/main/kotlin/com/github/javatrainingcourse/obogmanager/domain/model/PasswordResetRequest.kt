/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.*

/**
 * パスワードリセット要求のトークンを提供するエンティティです。
 * トークンの有効期限はとりあえず24時間です。
 *
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "password_resets")
class PasswordResetRequest {

    @Id
    @Column(length = 64)
    var token: String? = null

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = [(CascadeType.DETACH)])
    var membership: Membership? = null

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var generatedTime: Date? = null

    fun isExpired(): Boolean {
        val now = LocalDateTime.now()
        val generated = LocalDateTime.ofInstant(generatedTime!!.toInstant(), ZoneId.systemDefault())
        return now.isAfter(generated.plusDays(1))
    }

    companion object {
        fun newRequest(membership: Membership): PasswordResetRequest {
            val request = PasswordResetRequest()
            request.token = UUID.randomUUID().toString()
            request.generatedTime = Date()
            request.membership = membership
            return request
        }
    }
}
