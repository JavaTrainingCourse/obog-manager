/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.repository.AttendanceRepository;
import com.github.javatrainingcourse.obogmanager.domain.repository.MembershipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会員に関する操作を提供します。
 *
 * @author mikan
 * @since 0.1
 */
@Service
@Slf4j
public class AttendanceService {

    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.reply}")
    private String appReply;

    @Autowired
    public AttendanceService(MembershipRepository membershipRepository, AttendanceRepository attendanceRepository,
                             PasswordEncoder passwordEncoder, MailSender mailSender) {
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public boolean isEmailTaken(String email) {
        return membershipRepository.findByEmail(email) != null;
    }

    public List<Attendance> getResponses(Convocation convocation) {
        return attendanceRepository.findByConvocation(convocation);
    }

    public void register(Membership membership, Convocation convocation, String comment) {
        Attendance attendance = new Attendance(convocation, membership, comment);
        attendanceRepository.saveAndFlush(attendance);
        sendAttendMail(membership, convocation);
    }

    public void register(Membership membership, String password, Convocation convocation, String comment) {
        membership.setHashedPassword(passwordEncoder.encode(password));
        membership = membershipRepository.saveAndFlush(membership);
        System.out.println("membership id=" + membership.getId());
        Attendance attendance = new Attendance(convocation, membership, comment);
        attendanceRepository.saveAndFlush(attendance);
        sendAttendMail(membership, convocation);
    }

    public Attendance find(Membership membership, Convocation convocation) {
        return attendanceRepository.findOne(Attendance.AttendanceId.builder()
                .membershipId(membership.getId()).convocationId(convocation.getId()).build());
    }

    public void update(Membership membership, Convocation convocation, Attendance attendance) {
        attendance.setLastUpdateDate(new Date());
        log.info("参加更新: " + membership.getName() + ": " + attendance.getAttend() +
                " [" + attendance.getComment() + "]");
        attendanceRepository.saveAndFlush(attendance);
        if (attendance.getAttend()) {
            sendAttendMail(membership, convocation);
        } else {
            sendCancelMail(membership, convocation);
        }
    }

    /**
     * 登録完了メール送信します。
     *
     * @param membership メンバー情報
     * @throws MailException メール送信に失敗した場合
     */
    private void sendAttendMail(Membership membership, Convocation convocation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo(appReply);
        message.setBcc(appReply);
        message.setTo(membership.getEmail());
        message.setSubject("【登録完了】" + convocation.getSubject());
        message.setText(membership.getName() + " さん\n\n" +
                convocation.getSubject() + "の登録が完了しました。\n\n" +
                "詳細の確認・登録内容の変更は以下 URL より行ってください。\n" +
                appUrl + "\n\n" +
                "本メールに関するお問合せ先: " + appReply + "\n" +
                "Java研修 Go研修 OB・OG会");
        mailSender.send(message);
    }

    private void sendCancelMail(Membership membership, Convocation convocation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo(appReply);
        message.setBcc(appReply);
        message.setTo(membership.getEmail());
        message.setSubject("【キャンセル完了】" + convocation.getSubject());
        message.setText(membership.getName() + " さん\n\n" +
                convocation.getSubject() + "の登録キャンセルが完了しました。\n\n" +
                "詳細の確認・登録内容の変更は以下 URL より行ってください。\n" +
                appUrl + "\n\n" +
                "本メールに関するお問合せ先: " + appReply + "\n" +
                "Java研修 Go研修 OB・OG会");
        mailSender.send(message);
    }
}
