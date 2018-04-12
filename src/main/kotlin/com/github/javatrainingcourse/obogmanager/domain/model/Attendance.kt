/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model

import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "attendances")
class Attendance {

    @AttributeOverrides(AttributeOverride(name = "convocationId", column = Column(name = "convocation_id")), AttributeOverride(name = "membershipId", column = Column(name = "member_id")))
    @EmbeddedId
    private var id: AttendanceId? = null

    @MapsId("convocationId")
    @ManyToOne
    var convocation: Convocation? = null

    @MapsId("membershipId")
    @ManyToOne
    var membership: Membership? = null

    @Column(nullable = false)
    var attend: Boolean? = null

    @Column(length = 256)
    var comment: String? = null

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date? = null

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var lastUpdateDate: Date? = null

    companion object {
        fun newAttendance(convocation: Convocation, membership: Membership, comment: String): Attendance {
            val id = AttendanceId()
            id.convocationId = convocation.id
            id.membershipId = membership.id
            val attendance = Attendance()
            attendance.id = id
            attendance.convocation = convocation
            attendance.membership = membership
            attendance.comment = comment
            attendance.attend = true
            attendance.createdDate = Date()
            attendance.lastUpdateDate = Date()
            return attendance
        }
    }

    class AttendanceId : Serializable {
        var convocationId: Long? = null
        var membershipId: Long? = null

        companion object {
            private const val serialVersionUID = com.github.javatrainingcourse.obogmanager.Version.OBOG_MANAGER_SERIAL_VERSION_UID
        }
    }

    fun isAttend(): Boolean {
        return attend ?: true
    }
}
