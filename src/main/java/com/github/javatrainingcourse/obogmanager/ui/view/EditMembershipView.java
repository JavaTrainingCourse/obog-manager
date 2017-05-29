/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 会員情報編集画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = EditMembershipView.VIEW_NAME, ui = MainUI.class)
@Slf4j
public class EditMembershipView extends Wrapper implements View {

    static final String VIEW_NAME = "edit-member";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final MembershipService membershipService;
    private transient final AttendanceService attendanceService;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public EditMembershipView(MembershipService membershipService, AttendanceService attendanceService) {
        this.membershipService = membershipService;
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label titleLabel = new Label(VaadinIcons.EDIT.getHtml() + " 会員情報編集", ContentMode.HTML);
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        Membership membership = getMembership();
        if (membership == null) {
            getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
            return;
        }
        final String currentEmail = membership.getEmail();

        addComponent(new Label("必要な個所を編集してください。編集完了を押すと、確認メールが送信されます。"));

        Binder<Membership> binder = new Binder<>();
        binder.readBean(membership);

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        Label realName = new Label(membership.getName());
        realName.setCaption("名前");
        form.addComponent(realName);

        TextField emailField = new TextField("E-mail (受信可能なもの)", membership.getEmail());
        emailField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(emailField);
        binder.forField(emailField).withValidator(new EmailValidator("不正なアドレスです"))
                .bind(Membership::getEmail, Membership::setEmail);

        TextField javaTermField = new TextField("Java 研修 n期 (0 = 期不明, -1 = 対象外)", String.valueOf(membership.getJavaTerm()));
        form.addComponent(javaTermField);
        binder.forField(javaTermField)
                .withConverter(new StringToIntegerConverter("数値を入力してください"))
                .withValidator(new IntegerRangeValidator("不正な数値です", -1, 100))
                .bind(Membership::getJavaTerm, Membership::setJavaTerm);

        TextField java8TermField = new TextField("Java 8 研修 n期 (0 = 期不明, -1 = 対象外)", String.valueOf(membership.getJava8Term()));
        form.addComponent(java8TermField);
        binder.forField(java8TermField)
                .withConverter(new StringToIntegerConverter("数値を入力してください"))
                .withValidator(new IntegerRangeValidator("不正な数値です", -1, 100))
                .bind(Membership::getJava8Term, Membership::setJava8Term);

        TextField goTermField = new TextField("Go 研修 n期 (0 = 期不明, -1 = 対象外)", String.valueOf(membership.getGoTerm()));
        form.addComponent(goTermField);
        binder.forField(goTermField)
                .withConverter(new StringToIntegerConverter("数値を入力してください"))
                .withValidator(new IntegerRangeValidator("不正な数値です", -1, 100))
                .bind(Membership::getGoTerm, Membership::setGoTerm);

        Label noticeLabel = new Label("※管理者が上記の登録内容を記入・修正することがあります");
        noticeLabel.setStyleName(ValoTheme.LABEL_TINY);
        addComponent(noticeLabel);

        HorizontalLayout buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        Button backButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        buttonArea.addComponent(backButton);

        Button submitButton = new Button("編集完了", click -> {
            if (emailField.isEmpty()) {
                Notification.show("入力が完了していません", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (!binder.writeBeanIfValid(membership)) {
                Notification.show("入力が完了していません", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (!currentEmail.equals(membership.getEmail())) {
                try {
                    if (attendanceService.isEmailTaken(membership.getEmail())) {
                        Notification.show("他の人が登録した E-mail が指定されています",
                                Notification.Type.WARNING_MESSAGE);
                        return;
                    }
                } catch (RuntimeException e) {
                    log.error("Cannot check email exists", e);
                    Notification.show("現在 E-mail の修正ができません。E-mail を元に戻してください",
                            Notification.Type.WARNING_MESSAGE);
                    return;
                }
            }
            try {
                membershipService.update(membership);
                SuccessNotification.show("会員情報の編集が完了しました");
                getUI().getNavigator().navigateTo(MenuView.VIEW_NAME);
            } catch (RuntimeException e) {
                ErrorView.show("会員情報の編集に失敗しました。", e);
            }
        });
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(submitButton);
    }
}
