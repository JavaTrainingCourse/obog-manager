/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.repository.MembershipRepository;
import com.vaadin.server.VaadinSession;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final MailService mailService;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MembershipService(MailService mailService, MembershipRepository membershipRepository,
                             PasswordEncoder passwordEncoder) {
        this.mailService = mailService;
        this.membershipRepository = membershipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Nullable
    public static Membership getCurrentMembership() {
        return VaadinSession.getCurrent().getAttribute(Membership.class);
    }

    public List<Membership> getAll() {
        return membershipRepository.findAll();
    }

    /**
     * 登録済パスワードを検証します。
     *
     * @param hashedPassword 登録済パスワードのハッシュ
     * @param password       登録済パスワード
     * @return 正しければ {@code true}, それ以外は {@code false}
     */
    public boolean validatePassword(String hashedPassword, String password) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    /**
     * パスワードを更新します。
     * この変更はフラッシュされないため、更新を実際に反映するには {@link #update(Membership)} が必要です。
     *
     * @param membership  会員情報
     * @param newPassword 新しいパスワード
     */
    public void updatePassword(Membership membership, String newPassword) {
        membership.setHashedPassword(passwordEncoder.encode(newPassword));
    }

    /**
     * E-mail とパスワードでログインします。
     *
     * @param email    登録済 E-mail
     * @param password 登録済パスワード
     * @return ログインした会員の会員情報
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     * @throws BadCredentialsException   パスワードがマッチしない場合
     */
    public Membership login(String email, String password) {
        var membership = membershipRepository.findByEmail(email);
        if (membership == null) {
            throw new UsernameNotFoundException(email);
        }
        if (!validatePassword(membership.getHashedPassword(), password)) {
            throw new BadCredentialsException("invalid password for " + email);
        }
        beginSession(membership);
        return membership;
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
        mailService.sendUpdateMail(membership);
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
}
