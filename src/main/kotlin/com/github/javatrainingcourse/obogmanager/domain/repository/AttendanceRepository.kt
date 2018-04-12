/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.repository

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author mikan
 * @since 0.1
 */
@Repository
interface AttendanceRepository : JpaRepository<Attendance, Attendance.AttendanceId> {
    fun findById(id: Attendance.AttendanceId): Attendance?
    fun findByConvocation(convocation: Convocation): List<Attendance>
}
