/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model

import java.util.*
import javax.persistence.*

/**
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "members")
class Membership {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
    var id: Long? = null

    @Column(nullable = false, length = 16)
    var firstName: String? = null

    @Column(nullable = false, length = 16)
    var lastName: String? = null

    @Column(nullable = false, length = 128, unique = true)
    var email: String? = null

    @Column(length = 256)
    var hashedPassword: String? = null

    @Column
    var admin: Boolean = false

    @Column(length = 128)
    var github: String? = null

    @Column(length = 128)
    var twitter: String? = null

    @Column(length = 128)
    var facebook: String? = null

    @Column
    var javaTerm: Int = 0 // -1: not graduate, 0: term unknown, 1~: number of term

    @Column
    var java8Term: Int = 0 // -1: not graduate, 0: term unknown, 1~: number of term

    @Column
    var goTerm: Int = 0 // -1: not graduate, 0: term unknown, 1~: number of term

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date? = null

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var lastLoginDate: Date? = null

    fun getName(): String {
        return "$lastName $firstName"
    }

    fun isAdmin(): Boolean {
        return id ?: 0 < 3 || admin
    }
}
