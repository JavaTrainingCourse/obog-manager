/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.repository;

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mikan
 * @since 0.1
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Attendance.AttendanceId> {
    Attendance findById(Attendance.AttendanceId id);
    List<Attendance> findByConvocation(Convocation convocation);
}
