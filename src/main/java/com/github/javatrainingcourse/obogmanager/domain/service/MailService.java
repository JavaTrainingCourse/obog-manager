/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest;
import com.github.javatrainingcourse.obogmanager.ui.view.ResetPasswordView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 各種メールを送信するサービスです。
 *
 * @author mikan
 * @since 0.1
 */
@Service
@Slf4j
public class MailService {

    private final MailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.reply}")
    private String appReply;

    private Consumer<MailException> exceptionHandler = e -> log.error("Mail Error", e);

    @Autowired
    public MailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 登録完了メールを送信します。
     *
     * @param membership  メンバー情報
     * @param convocation 対象のイベント招待
     * @throws MailException メール送信に失敗した場合
     */
    @Async
    void sendAttendMail(Membership membership, Convocation convocation) {
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
        try {
            mailSender.send(message);
        } catch (MailException e) {
            exceptionHandler.accept(e);
        }
    }

    /**
     * キャンセル完了メールを送信します。
     *
     * @param membership  メンバー情報
     * @param convocation 対象のイベント招待
     * @throws MailException メール送信に失敗した場合
     */
    @Async
    void sendCancelMail(Membership membership, Convocation convocation) {
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
        try {
            mailSender.send(message);
        } catch (MailException e) {
            exceptionHandler.accept(e);
        }
    }

    /**
     * パスワードリセット案内メールを送信します。
     *
     * @param request パスワードリセット要求
     * @throws MailException メール送信に失敗した場合
     */
    @Async
    void sendPasswordResetMail(PasswordResetRequest request) {
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
        try {
            mailSender.send(message);
        } catch (MailException e) {
            exceptionHandler.accept(e);
        }
    }

    /**
     * メンバー情報更新完了メールを送信します。
     *
     * @param membership メンバー情報
     * @throws MailException メール送信に失敗した場合
     */
    @Async
    void sendUpdateMail(Membership membership) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo(appReply);
        message.setBcc(appReply);
        message.setTo(membership.getEmail());
        message.setSubject("【会員情報編集完了】Java研修 Go研修 OB・OG会");
        message.setText(membership.getName() + " さん\n\n" +
                "会員情報の編集が完了しました。\n\n" +
                "詳細の確認・登録内容の変更は以下 URL より行ってください。\n" +
                appUrl + "\n\n" +
                "本メールに関するお問合せ先: " + appReply + "\n" +
                "Java研修 Go研修 OB・OG会");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            exceptionHandler.accept(e);
        }
    }
}
