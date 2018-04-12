/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.repository

import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author mikan
 * @since 0.1
 */
interface PasswordResetRequestRepository : JpaRepository<PasswordResetRequest, String> {
    fun findByToken(token: String): PasswordResetRequest?
}
