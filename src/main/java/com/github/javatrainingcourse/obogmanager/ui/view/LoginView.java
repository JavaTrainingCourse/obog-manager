/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LoginView extends Wrapper implements View {

    public static final String VIEW_NAME = "login";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final MembershipService membershipService;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public LoginView(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("会員ログイン", VaadinIcons.USER));

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        TextField emailField = new TextField("E-mail アドレス");
        emailField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        emailField.focus();
        form.addComponent(emailField);

        PasswordField passwordField = new PasswordField("パスワード");
        passwordField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordField);

        HorizontalLayout buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        Button backButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        buttonArea.addComponent(backButton);

        Button passwordResetButton = new Button("パスワードリセット",
                click -> getUI().getNavigator().navigateTo(ResetPasswordView.VIEW_NAME));
        buttonArea.addComponent(passwordResetButton);

        Button loginButton = new Button("ログイン", click -> {
            if (emailField.isEmpty() || passwordField.isEmpty()) {
                Notification.show("入力が完了していません");
                return;
            }
            try {
                membershipService.login(emailField.getValue(), passwordField.getValue());
                getUI().getNavigator().navigateTo(MenuView.VIEW_NAME);
            } catch (AuthenticationException e) {
                log.info("Authentication failed: " + e.getMessage());
                ErrorView.show("E-mail が存在しないか、パスワードが一致していません。", null);
            } catch (RuntimeException e) {
                ErrorView.show("ログイン処理に失敗しました。", e);
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
