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

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.reply}")
    private String appReply;

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
