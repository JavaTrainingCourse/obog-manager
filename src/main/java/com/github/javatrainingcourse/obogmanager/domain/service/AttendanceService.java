/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest;
import com.github.javatrainingcourse.obogmanager.domain.repository.AttendanceRepository;
import com.github.javatrainingcourse.obogmanager.domain.repository.MembershipRepository;
import com.github.javatrainingcourse.obogmanager.domain.repository.PasswordResetRequestRepository;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会員に関する操作を提供します。
 *
 * @author mikan
 * @since 0.1
 */
@Service
public class AttendanceService {

    private final MailService mailService;
    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    @Autowired
    public AttendanceService(MailService mailService, MembershipRepository membershipRepository,
                             AttendanceRepository attendanceRepository,
                             PasswordResetRequestRepository passwordResetRequestRepository,
                             PasswordEncoder passwordEncoder) {
        this.mailService = mailService;
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isEmailTaken(String email) {
        return membershipRepository.findByEmail(email) != null;
    }

    public List<Attendance> getResponses(Convocation convocation) {
        return attendanceRepository.findByConvocationOrderByCreatedDateDesc(convocation);
    }

    public Pair<Integer, Integer> countAttendees(Convocation convocation) {
        var result = attendanceRepository.findByConvocationOrderByCreatedDateDesc(convocation).stream()
                .collect(Collectors.groupingBy(Attendance::isAttend));
        var attendees = result.get(true);
        var cancels = result.get(false);
        return Pair.of(attendees != null ? attendees.size() : 0, cancels != null ? cancels.size() : 0); // yes, no
    }

    /**
     * 指定された登録済会員でイベント招集に参加します。
     *
     * @param membership  登録済会員
     * @param convocation イベント招集
     * @param comment     コメント
     * @throws DataAccessException データの登録に失敗した場合
     * @throws MailException       メール送信に失敗した場合
     */
    public void register(Membership membership, Convocation convocation, String comment) {
        var attendance = Attendance.Companion.newAttendance(convocation, membership, comment);
        attendanceRepository.saveAndFlush(attendance);
        mailService.sendAttendMail(membership, convocation);
    }

    /**
     * 会員登録とイベント招集を同時に実行します。
     *
     * @param membership  登録予定の会員情報
     * @param password    登録予定の生パスワード
     * @param convocation 参加するイベント招集
     * @param comment     コメント
     * @throws DataAccessException データの登録に失敗した場合
     * @throws MailException       メール送信に失敗した場合
     */
    @Transactional
    public void register(Membership membership, String password, Convocation convocation, String comment) {
        membership.setHashedPassword(passwordEncoder.encode(password));
        membership.setCreatedDate(new Date());
        membership = membershipRepository.saveAndFlush(membership);
        log.info("membership registered=" + membership.getId());
        var attendance = Attendance.Companion.newAttendance(convocation, membership, comment);
        attendanceRepository.saveAndFlush(attendance);
        mailService.sendAttendMail(membership, convocation);
    }

    @Nullable
    public Attendance find(Membership membership, Convocation convocation) {
        if (membership == null || convocation == null) {
            return null;
        }
        var id = new Attendance.AttendanceId();
        id.setMembershipId(membership.getId());
        id.setConvocationId(convocation.getId());
        return attendanceRepository.findById(id);
    }

    public void updateComment(Attendance attendance) {
        attendance.setLastUpdateDate(new Date());
        log.info("参加更新: " + attendance.getMembership().getName() + ": " + attendance.getAttend() +
                " [" + attendance.getComment() + "]");
        attendanceRepository.saveAndFlush(attendance);
    }

    public void update(Attendance attendance) {
        attendance.setLastUpdateDate(new Date());
        log.info("参加更新: " + attendance.getMembership().getName() + ": " + attendance.getAttend() +
                " [" + attendance.getComment() + "]");
        attendanceRepository.saveAndFlush(attendance);
        if (attendance.getAttend()) {
            mailService.sendAttendMail(attendance.getMembership(), attendance.getConvocation());
        } else {
            mailService.sendCancelMail(attendance.getMembership(), attendance.getConvocation());
        }
    }

    public void requestPasswordReset(String email) {
        var membership = membershipRepository.findByEmail(email);
        var request = PasswordResetRequest.Companion.newRequest(membership);
        passwordResetRequestRepository.saveAndFlush(request);
        mailService.sendPasswordResetMail(request);
    }

    public PasswordResetRequest getPasswordResetRequest(String token) {
        var request = passwordResetRequestRepository.findByToken(token);
        if (request == null) {
            throw new IllegalArgumentException("トークンがありません。");
        }
        if (request.isExpired()) {
            throw new IllegalArgumentException("トークンの有効期限が切れています。");
        }
        return request;
    }

    @Transactional
    public void updatePassword(PasswordResetRequest request, String newPassword) {
        var membership = request.getMembership();
        membership.setHashedPassword(passwordEncoder.encode(newPassword));
        membership.setLastLoginDate(new Date());
        membershipRepository.save(membership);
        passwordResetRequestRepository.delete(request);
        membershipRepository.flush();
    }
}
