/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.repository.AttendanceRepository;
import com.github.javatrainingcourse.obogmanager.domain.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 会員に関する操作を提供します。
 *
 * @author mikan
 * @since 0.1
 */
@Service
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

    public void register(Membership membership, String password, Convocation convocation, String comment) {
        membership.setHashedPassword(passwordEncoder.encode(password));
        membership = membershipRepository.saveAndFlush(membership);
        System.out.println("membership id=" + membership.getId());
        Attendance attendance = new Attendance(convocation, membership, comment);
        attendanceRepository.saveAndFlush(attendance);
        sendMail(membership);
    }

    /**
     * 登録完了メール送信します。
     *
     * @param membership メンバー情報
     * @throws MailException メール送信に失敗した場合
     */
    private void sendMail(Membership membership) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo(appReply);
        message.setTo(membership.getEmail());
        message.setSubject("【登録完了】Java研修 Go研修 OB・OG会");
        message.setText(membership.getName() + " さん\n\n" +
                "Java研修 Go研修 OB・OG会の登録が完了しました。\n\n" +
                "詳細の確認・登録内容の変更は以下 URL より行ってください。\n" +
                appUrl + "\n\n" +
                "本メールに関するお問合せ先: " + appReply);
        mailSender.send(message);
    }
}
