/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.repository.MembershipRepository;
import com.vaadin.server.VaadinSession;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mikan
 * @since 0.1
 */
@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.reply}")
    private String appReply;

    @Autowired
    public MembershipService(MembershipRepository membershipRepository, PasswordEncoder passwordEncoder,
                             MailSender mailSender) {
        this.membershipRepository = membershipRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Nullable
    public static Membership getCurrentMembership() {
        return VaadinSession.getCurrent().getAttribute(Membership.class);
    }

    public List<Membership> getAll() {
        return membershipRepository.findAll();
    }

    /**
     * E-mail とパスワードでログインします。
     *
     * @param email    登録済 E-mail
     * @param password 登録済パスワード
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     * @throws BadCredentialsException   パスワードがマッチしない場合
     */
    public void login(String email, String password) {
        Membership membership = membershipRepository.findByEmail(email);
        if (membership == null) {
            throw new UsernameNotFoundException(email);
        }
        if (!passwordEncoder.matches(password, membership.getHashedPassword())) {
            throw new BadCredentialsException("invalid password for " + email);
        }
        beginSession(membership);
    }

    public void logout() {
        endSession();
    }

    public long countMemberships() {
        return membershipRepository.count();
    }

    public void update(Membership membership) {
        if (!membershipRepository.exists(membership.getId())) {
            throw new IllegalArgumentException("No such membership: " + membership.getId() + " " +
                    membership.getName());
        }
        membershipRepository.saveAndFlush(membership);
        sendUpdateMail(membership);
    }

    private void beginSession(Membership membership) {
        if (membership == null) {
            throw new NullPointerException("No membership");
        }
        VaadinSession.getCurrent().setAttribute(Membership.class, membership);
    }

    private void endSession() {
        VaadinSession.getCurrent().setAttribute(Membership.class, null);
    }

    private void sendUpdateMail(Membership membership) {
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
        mailSender.send(message);
    }
}
