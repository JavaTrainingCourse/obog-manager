/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;

/**
 * ログイン画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends Wrapper implements View {

    public static final String VIEW_NAME = "login";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final MembershipService membershipService;
    private transient final ConvocationService convocationService;
    private transient final AttendanceService attendanceService;
    private static final Logger log = LoggerFactory.getLogger(LoginView.class);

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public LoginView(MembershipService membershipService, ConvocationService convocationService, AttendanceService attendanceService) {
        this.membershipService = membershipService;
        this.convocationService = convocationService;
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("会員ログイン", VaadinIcons.USER));

        var form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        var emailField = new TextField("E-mail アドレス");
        emailField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        emailField.focus();
        form.addComponent(emailField);

        var passwordField = new PasswordField("パスワード");
        passwordField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordField);

        var buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        var backButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        buttonArea.addComponent(backButton);

        var passwordResetButton = new Button("パスワードリセット",
                click -> getUI().getNavigator().navigateTo(ResetPasswordView.VIEW_NAME));
        buttonArea.addComponent(passwordResetButton);

        var loginButton = new Button("ログイン", click -> {
            if (emailField.isEmpty() || passwordField.isEmpty()) {
                Notification.show("入力が完了していません");
                return;
            }

            // Authentication
            Membership membership;
            try {
                membership = membershipService.login(emailField.getValue(), passwordField.getValue());
            } catch (AuthenticationException e) {
                log.info("Authentication failed: " + e.getMessage());
                ErrorView.show("E-mail が存在しないか、パスワードが一致していません。", null);
                return;
            } catch (RuntimeException e) {
                ErrorView.show("ログイン処理に失敗しました。", e);
                return;
            }

            // Find latest convocation
            Convocation latest;
            try {
                latest = convocationService.getLatestConvocation();
            } catch (IllegalStateException e) { // before creating the first convocation
                getUI().getNavigator().navigateTo(MenuView.VIEW_NAME);
                return;
            }

            // Switch transition by attend or not
            var attendance = attendanceService.find(membership, latest);
            if (attendance == null || !attendance.isAttend()) {
                getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
            } else {
                getUI().getNavigator().navigateTo(MenuView.VIEW_NAME);
            }
        });
        loginButton.setIcon(VaadinIcons.SIGN_IN);
        loginButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(loginButton);

        passwordField.addShortcutListener(new ShortcutListener("Enter キーを押すとログインします",
                ShortcutAction.KeyCode.ENTER, null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void handleAction(Object sender, Object target) {
                loginButton.click();
            }
        });
    }
}
