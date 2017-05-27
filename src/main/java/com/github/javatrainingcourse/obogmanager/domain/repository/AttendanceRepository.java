/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.repository;

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mikan
 * @since 0.1
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Attendance.AttendanceId> {
}
