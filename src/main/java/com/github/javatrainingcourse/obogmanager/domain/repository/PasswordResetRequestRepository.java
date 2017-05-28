/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.repository;

import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author mikan
 * @since 0.1
 */
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, String> {
    PasswordResetRequest findByToken(String token);
}
