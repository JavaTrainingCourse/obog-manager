/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.policy.AttendancePolicy;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.AboutWindow;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

/**
 * 表紙画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = FrontView.VIEW_NAME)
public class FrontView extends Wrapper implements View {

    public static final String VIEW_NAME = "";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final AttendanceService attendanceService;
    private transient final ConvocationService convocationService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FrontView.class);

    @Autowired
    public FrontView(AttendanceService attendanceService, ConvocationService convocationService) {
        this.attendanceService = attendanceService;
        this.convocationService = convocationService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Convocation latestEvent;
        try {
            latestEvent = convocationService.getLatestConvocation();
            Label subjectLabel = new Label(VaadinIcons.MEGAFONE.getHtml() + " " + latestEvent.getSubject() + "のご案内",
                    ContentMode.HTML);
            subjectLabel.setStyleName(ValoTheme.LABEL_H2);
            addComponent(subjectLabel);
            addComponent(new Label(latestEvent.getDescriptionAsHtml(), ContentMode.HTML));
        } catch (IllegalStateException e) {
            getUI().getNavigator().navigateTo(NewEventView.VIEW_NAME);
            return;
        } catch (RuntimeException e) {
            ErrorView.show("イベント情報の取得に失敗しました。", e);
            return;
        }

        // 最新イベントがすでに終了している場合はその旨を表示し受付を行わない
        if (latestEvent.getTargetDate().isBefore(LocalDate.now())) {
            log.info("latest: " + latestEvent.getTargetDate());
            log.info("now:    " + LocalDate.now());
            addComponent(new Label(latestEvent.getSubject() + "は終了しました。次回の開催をお待ちください。"));
            printMenuSection();
            return;
        }

        if (!isLoggedIn()) {
            printRegistrationSection(latestEvent);
        } else {
            printRegistrationSection(latestEvent, getMembership());
        }
        printMenuSection();
    }

    private void printRegistrationSection(Convocation convocation) {
        addComponent(new HeadingLabel("参加登録", VaadinIcons.PENCIL));

        addComponent(new Label("過去に参加した方は<a href=\"/#!" + LoginView.VIEW_NAME + "\">会員ログイン</a>してから申し込んでください。", ContentMode.HTML));

        Membership membership = new Membership();
        Binder<Membership> binder = new Binder<>();
        binder.readBean(membership);

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        TextField lastNameField = new TextField("姓");
        lastNameField.setRequiredIndicatorVisible(true);
        form.addComponent(lastNameField);
        binder.forField(lastNameField).withValidator(new StringLengthValidator("入力が長すぎます", 0, 16))
                .bind(Membership::getLastName, Membership::setLastName);

        TextField firstNameField = new TextField("名");
        firstNameField.setRequiredIndicatorVisible(true);
        form.addComponent(firstNameField);
        binder.forField(firstNameField).withValidator(new StringLengthValidator("入力が長すぎます", 0, 16))
                .bind(Membership::getFirstName, Membership::setFirstName);

        TextField emailField = new TextField("E-mail (受信可能なもの)");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(emailField);
        binder.forField(emailField).withValidator(new EmailValidator("不正なアドレスです"))
                .bind(Membership::getEmail, Membership::setEmail);

        PasswordField passwordField = new PasswordField("変更用パスワード");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordField);

        PasswordField passwordConfirmField = new PasswordField("もう一度入力");
        passwordConfirmField.setRequiredIndicatorVisible(true);
        passwordConfirmField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(passwordConfirmField);

        TextArea commentArea = new TextArea("コメント");
        commentArea.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(commentArea);

        CheckBox javaCheckBox = new CheckBox("私は Java 研修の修了生です");
        addComponent(javaCheckBox);
        CheckBox java8CheckBox = new CheckBox("私は Java 8 研修の修了生です");
        addComponent(java8CheckBox);
        CheckBox goCheckBox = new CheckBox("私は Go 研修の修了生です");
        addComponent(goCheckBox);
        CheckBox incompleteCheckBox = new CheckBox("私は現在開講中の Go 研修の受講生です");
        addComponent(incompleteCheckBox);

        Button submitButton = new Button("参加登録", click -> {
            if (!binder.writeBeanIfValid(membership)) {
                Notification.show("入力が完了していません", Type.WARNING_MESSAGE);
                return;
            }
            if (passwordField.isEmpty() || passwordConfirmField.isEmpty()) {
                Notification.show("パスワードが入力されていません", Type.WARNING_MESSAGE);
                return;
            }
            if (!passwordField.getValue().equals(passwordConfirmField.getValue())) {
                Notification.show("確認用パスワードが一致していません", Type.WARNING_MESSAGE);
                return;
            }
            if (!AttendancePolicy.INSTANCE.allows(javaCheckBox.getValue(), java8CheckBox.getValue(), goCheckBox.getValue(),
                    incompleteCheckBox.getValue())) {
                Notification.show("参加資格がないか、ありえない組み合わせを指定しています", Type.WARNING_MESSAGE);
                return;
            }
            if (attendanceService.isEmailTaken(emailField.getValue())) {
                Notification.show("その E-mail アドレスは登録済です。先に会員ログインしてください。",
                        Type.WARNING_MESSAGE);
                return;
            }
            try {
                membership.setJavaTerm(javaCheckBox.getValue() ? 0 : -1);
                membership.setJava8Term(java8CheckBox.getValue() ? 0 : -1);
                membership.setGoTerm(goCheckBox.getValue() ? 0 : -1);
                attendanceService.register(membership, passwordField.getValue(), convocation, commentArea.getValue());
                getUI().getNavigator().navigateTo(ThanksView.VIEW_NAME);
            } catch (RuntimeException e) {
                ErrorView.show("登録に失敗しました。", e);
            }
        });
        submitButton.setIcon(VaadinIcons.CHECK);
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addComponent(submitButton);
        setComponentAlignment(submitButton, Alignment.MIDDLE_CENTER);
    }

    private void printRegistrationSection(Convocation convocation, Membership membership) {
        Label registrationLabel = new Label(VaadinIcons.PENCIL.getHtml() + " 参加登録", ContentMode.HTML);
        registrationLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(registrationLabel);

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        Label nameLabel = new Label(membership.getName());
        nameLabel.setCaption("名前");
        form.addComponent(nameLabel);

        Label emailLabel = new Label(membership.getEmail());
        emailLabel.setCaption("E-mail");
        form.addComponent(emailLabel);

        TextArea commentArea = new TextArea("コメント");
        commentArea.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(commentArea);

        Button submitButton = new Button("参加登録", click -> {
            try {
                attendanceService.register(membership, convocation, commentArea.getValue());
                getUI().getNavigator().navigateTo(ThanksView.VIEW_NAME);
            } catch (RuntimeException e) {
                ErrorView.show("登録に失敗しました。", e);
            }
        });
        submitButton.setIcon(VaadinIcons.CHECK);
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addComponent(submitButton);
        setComponentAlignment(submitButton, Alignment.MIDDLE_CENTER);

        Attendance attendance;
        try {
            attendance = attendanceService.find(membership, convocation);
        } catch (RuntimeException e) {
            form.setEnabled(false);
            submitButton.setEnabled(false);
            Label noticeLabel = new Label("参加登録情報の取得に失敗しました。<br/>" +
                    "管理者にお問合せください。", ContentMode.HTML);
            noticeLabel.setStyleName(ValoTheme.LABEL_FAILURE);
            addComponent(noticeLabel);
            setComponentAlignment(noticeLabel, Alignment.MIDDLE_CENTER);
            return;
        }
        if (attendance != null && attendance.isAttend()) {
            commentArea.setValue(attendance.getComment());
            form.setEnabled(false);
            submitButton.setEnabled(false);
            Label noticeLabel = new Label("参加登録は完了しています。<br/>" +
                    "変更するには会員メニューを開いてください。", ContentMode.HTML);
            noticeLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
            addComponent(noticeLabel);
            setComponentAlignment(noticeLabel, Alignment.MIDDLE_CENTER);
        }
    }

    private void printMenuSection() {
        addComponent(new HeadingLabel("会員メニュー"));

        HorizontalLayout buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        if (isLoggedIn()) {
            Button memberLoginButton = new Button("会員メニュー",
                    click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
            memberLoginButton.setIcon(VaadinIcons.USER);
            buttonArea.addComponent(memberLoginButton);
        } else {
            Button memberLoginButton = new Button("会員ログイン",
                    click -> getUI().getNavigator().navigateTo(LoginView.VIEW_NAME));
            memberLoginButton.setIcon(VaadinIcons.USER);
            buttonArea.addComponent(memberLoginButton);
        }

        Button facebookGroupButton = new Button("Facebook グループ", click -> UI.getCurrent().getPage()
                .setLocation("https://www.facebook.com/groups/351472538254785/"));
        facebookGroupButton.setIcon(VaadinIcons.COMMENTS);
        buttonArea.addComponent(facebookGroupButton);

        Button aboutAppButton = new Button("このアプリについて", click -> {
            AboutWindow aboutWindow = new AboutWindow();
            UI.getCurrent().addWindow(aboutWindow);
        });
        aboutAppButton.setIcon(VaadinIcons.INFO_CIRCLE);
        buttonArea.addComponent(aboutAppButton);
    }
}
