/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

/**
 * パスワードリセット画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = ResetPasswordView.VIEW_NAME)
public class ResetPasswordView extends Wrapper implements View {

    public static final String VIEW_NAME = "reset-password";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final AttendanceService attendanceService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResetPasswordView.class);

    @Autowired
    public ResetPasswordView(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("パスワードリセット"));

        // パスパラメーターを取得
        var token = Stream.of(event.getParameters().split("/")).filter(s -> !s.isEmpty()).findFirst().orElse("");
        if (token.isEmpty()) {
            printRequestSection();
            return;
        }

        try {
            var request = attendanceService.getPasswordResetRequest(token);
            printResetSection(request);
        } catch (IllegalArgumentException e) {
            log.info("トークン検証失敗", e);
            ErrorView.show("無効なパラメーターです。", null);
        }
    }

    private void printRequestSection() {
        var messageLabel = new Label("登録済 E-mail アドレスを入力してください。" +
                "パスワードリセット用リンクを含む E-mail を送信します。");
        addComponent(messageLabel);

        var form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        var emailField = new TextField("E-mail");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        emailField.focus();
        form.addComponent(emailField);

        var buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        var homeButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        buttonArea.addComponent(homeButton);

        var submitButton = new Button("送信", click -> {
            if (emailField.isEmpty()) {
                Notification.show("E-mail が入力されていません", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (!attendanceService.isEmailTaken(emailField.getValue())) {
                Notification.show("登録されていない E-mail アドレスです", Notification.Type.WARNING_MESSAGE);
                return;
            }
            try {
                attendanceService.requestPasswordReset(emailField.getValue());
                getUI().getNavigator().navigateTo(TokenSentView.VIEW_NAME);
            } catch (RuntimeException e) {
                ErrorView.show("パスワードリセット要求の送信に失敗しました。", e);
            }
        });
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(submitButton);
    }

    private void printResetSection(PasswordResetRequest request) {
        var messageLabel = new Label("E-mail アドレスが確認できました。新しいパスワードを入力してください。");
        addComponent(messageLabel);

        var form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        var userLabel = new Label(request.getMembership().getName());
        userLabel.setCaption("名前");
        form.addComponent(userLabel);

        var emailLabel = new Label(request.getMembership().getEmail());
        emailLabel.setCaption("E-mail");
        form.addComponent(emailLabel);

        var passwordField = new PasswordField("パスワード");
        passwordField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordField);

        var passwordConfirmField = new PasswordField("確認用");
        passwordConfirmField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordConfirmField);

        var submitButton = new Button("パスワード変更", click -> {
            if (passwordField.isEmpty()) {
                Notification.show("パスワードを入力してください", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (!passwordField.getValue().equals(passwordConfirmField.getValue())) {
                Notification.show("パスワードが一致していません", Notification.Type.WARNING_MESSAGE);
                return;
            }
            try {
                attendanceService.updatePassword(request, passwordField.getValue());
                getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
                SuccessNotification.show("パスワード変更が完了しました");
            } catch (RuntimeException e) {
                ErrorView.show("パスワード変更に失敗しました。", e);
            }
        });
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addComponent(submitButton);
        setComponentAlignment(submitButton, Alignment.MIDDLE_CENTER);
    }
}
