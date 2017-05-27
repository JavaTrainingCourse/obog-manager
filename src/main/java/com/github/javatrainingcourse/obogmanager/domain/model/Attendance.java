/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.model;

import com.github.javatrainingcourse.obogmanager.App;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author mikan
 * @since 0.1
 */
@Entity
@Table(name = "attendances")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @AttributeOverrides({
            @AttributeOverride(name = "convocationId", column = @Column(name = "convocation_id")),
            @AttributeOverride(name = "membershipId", column = @Column(name = "member_id"))
    })
    @EmbeddedId
    private AttendanceId id;

    @MapsId("convocationId")
    @ManyToOne
    private Convocation convocation;

    @MapsId("membershipId")
    @ManyToOne
    private Membership membership;

    @Column(nullable = false)
    @Getter
    @Setter
    private Boolean attend;

    @Column(length = 256)
    @Getter
    @Setter
    private String comment;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date lastUpdateDate;

    public Attendance(Convocation convocation, Membership membership, String comment) {
        id = AttendanceId.builder().convocationId(convocation.getId()).membershipId(membership.getId()).build();
        this.convocation = convocation;
        this.membership = membership;
        this.comment = comment;
        attend = true;
        createdDate = new Date();
        lastUpdateDate = new Date();
    }

    @Embeddable
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceId implements Serializable {
        private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
        private Long convocationId;
        private Long membershipId;
    }
}
