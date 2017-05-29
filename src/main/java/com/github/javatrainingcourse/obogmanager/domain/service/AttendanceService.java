/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest;
import com.github.javatrainingcourse.obogmanager.domain.repository.AttendanceRepository;
import com.github.javatrainingcourse.obogmanager.domain.repository.MembershipRepository;
import com.github.javatrainingcourse.obogmanager.domain.repository.PasswordResetRequestRepository;
import com.github.javatrainingcourse.obogmanager.ui.view.ResetPasswordView;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.reply}")
    private String appReply;

    @Autowired
    public AttendanceService(MembershipRepository membershipRepository, AttendanceRepository attendanceRepository,
                             PasswordResetRequestRepository passwordResetRequestRepository,
                             PasswordEncoder passwordEncoder, MailSender mailSender) {
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public boolean isEmailTaken(String email) {
        return membershipRepository.findByEmail(email) != null;
    }

    public List<Attendance> getResponses(Convocation convocation) {
        return attendanceRepository.findByConvocation(convocation);
    }

    public long countAttendees(Convocation convocation) {
        return attendanceRepository.findByConvocation(convocation).stream()
                .filter(Attendance::isAttend).count();
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

    @Nullable
    public Attendance find(Membership membership, Convocation convocation) {
        if (membership == null || convocation == null) {
            return null;
        }
        return attendanceRepository.findById(Attendance.AttendanceId.builder()
                .membershipId(membership.getId()).convocationId(convocation.getId()).build());
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
            sendAttendMail(attendance.getMembership(), attendance.getConvocation());
        } else {
            sendCancelMail(attendance.getMembership(), attendance.getConvocation());
        }
    }

    public void requestPasswordReset(String email) {
        Membership membership = membershipRepository.findByEmail(email);
        PasswordResetRequest request = new PasswordResetRequest(membership);
        passwordResetRequestRepository.saveAndFlush(request);
        sendPasswordResetMail(request);
    }

    public PasswordResetRequest getPasswordResetRequest(String token) {
        PasswordResetRequest request = passwordResetRequestRepository.findByToken(token);
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
        Membership membership = request.getMembership();
        membership.setHashedPassword(passwordEncoder.encode(newPassword));
        membership.setLastLoginDate(new Date());
        membershipRepository.save(membership);
        passwordResetRequestRepository.delete(request);
        membershipRepository.flush();
    }

    /**
     * 登録完了メール送信します。
     *
     * @param membership  メンバー情報
     * @param convocation 対象のイベント招待
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

    /**
     * キャンセル完了メール送信します。
     *
     * @param membership  メンバー情報
     * @param convocation 対象のイベント招待
     * @throws MailException メール送信に失敗した場合
     */
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

    /**
     * パスワードリセット案内メール送信します。
     *
     * @param request パスワードリセット要求
     * @throws MailException メール送信に失敗した場合
     */
    private void sendPasswordResetMail(PasswordResetRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo(appReply);
        message.setTo(request.getMembership().getEmail());
        message.setSubject("【パスワードリセット】Java研修 Go研修 OB・OG会");
        message.setText(request.getMembership().getName() + " さん\n\n" +
                "パスワードリセットの要求を受け付けました。\n" +
                "下記 URL から 24 時間以内にパスワードリセットを行ってください。\n\n" +
                appUrl + "/#!" + ResetPasswordView.VIEW_NAME + "/" + request.getToken() + "\n" +
                "※トップページにリダイレクトされてしまう場合は、トップページを開いた画面 (タブ) のアドレス欄に" +
                "上記 URL を張り付けて移動してください。\n\n" +
                "本メールに関するお問合せ先: " + appReply + "\n" +
                "Java研修 Go研修 OB・OG会");
        mailSender.send(message);
    }
}
