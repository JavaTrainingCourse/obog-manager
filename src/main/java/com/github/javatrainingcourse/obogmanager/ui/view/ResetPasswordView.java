/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.PasswordResetRequest;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

/**
 * パスワードリセット画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = ResetPasswordView.VIEW_NAME, ui = MainUI.class)
@Slf4j
public class ResetPasswordView extends Wrapper implements View {

    public static final String VIEW_NAME = "reset-password";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final AttendanceService attendanceService;

    @Autowired
    public ResetPasswordView(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label titleLabel = new Label("パスワードリセット");
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        // パスパラメーターを取得
        String token = Stream.of(event.getParameters().split("/")).filter(s -> !s.isEmpty()).findFirst().orElse("");
        if (token.isEmpty()) {
            printRequestSection();
            return;
        }

        try {
            PasswordResetRequest request = attendanceService.getPasswordResetRequest(token);
            printResetSection(request);
        } catch (IllegalArgumentException e) {
            log.info("トークン検証失敗", e);
            ErrorView.show("無効なパラメーターです。", null);
        }
    }

    private void printRequestSection() {
        Label messageLabel = new Label("登録済 E-mail アドレスを入力してください。" +
                "パスワードリセット用リンクを含む E-mail を送信します。");
        addComponent(messageLabel);

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        TextField emailField = new TextField("E-mail");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(emailField);

        HorizontalLayout buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        Button homeButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        buttonArea.addComponent(homeButton);

        Button submitButton = new Button("送信", click -> {
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
        Label messageLabel = new Label("E-mail アドレスが確認できました。新しいパスワードを入力してください。");
        addComponent(messageLabel);

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        Label userLabel = new Label(request.getMembership().getName());
        userLabel.setCaption("名前");
        form.addComponent(userLabel);

        Label emailLabel = new Label(request.getMembership().getEmail());
        emailLabel.setCaption("E-mail");
        form.addComponent(emailLabel);

        PasswordField passwordField = new PasswordField("パスワード");
        passwordField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordField);

        PasswordField passwordConfirmField = new PasswordField("確認用");
        passwordConfirmField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordConfirmField);

        Button submitButton = new Button("パスワード変更", click -> {
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
