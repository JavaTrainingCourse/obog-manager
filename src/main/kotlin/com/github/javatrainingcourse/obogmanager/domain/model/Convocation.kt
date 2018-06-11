/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model

import org.pegdown.PegDownProcessor
import java.time.LocalDate
import java.util.*
import javax.persistence.*

/**
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "convocations")
class Convocation {

    @Id
    @Column(name = "event_id")
    @GeneratedValue
    var id: Long? = null

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private var targetDate: Date? = null

    @Column(nullable = false, length = 64)
    var subject: String? = null

    @Column(nullable = false, length = 8192)
    var description: String? = null

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date? = null

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var lastUpdateDate: Date? = null

    fun getTargetDate(): LocalDate {
        return java.sql.Date(targetDate!!.time).toLocalDate()
    }

    fun setTargetDate(date: LocalDate) {
        targetDate = java.sql.Date.valueOf(date)
    }

    fun getDescriptionAsMarkdown(): String? {
        return description
    }

    fun getDescriptionAsHtml(): String? {
        return if (description == null) {
            null
        } else PegDownProcessor().markdownToHtml(description)
    }
}
