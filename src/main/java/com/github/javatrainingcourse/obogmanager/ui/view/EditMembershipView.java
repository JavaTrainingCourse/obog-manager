/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.data.*;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
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
@SpringView(name = EditMembershipView.VIEW_NAME)
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
        addComponent(new HeadingLabel("会員情報編集", VaadinIcons.EDIT));

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

        TermSelectLayout javaTermSelect = new TermSelectLayout("Java 研修", membership.getJavaTerm());
        form.addComponent(javaTermSelect);
        binder.forField(javaTermSelect.getTextField())
                .withConverter(javaTermSelect.converter())
                .withValidator(javaTermSelect.validator())
                .bind(m -> javaTermSelect.getValue(), (m, v) -> m.setJavaTerm(javaTermSelect.setValue(v)));

        TermSelectLayout java8TermSelect = new TermSelectLayout("Java 8 研修", membership.getJava8Term());
        form.addComponent(java8TermSelect);
        binder.forField(java8TermSelect.getTextField())
                .withConverter(java8TermSelect.converter())
                .withValidator(java8TermSelect.validator())
                .bind(m -> java8TermSelect.getValue(), (m, v) -> m.setJava8Term(java8TermSelect.setValue(v)));

        TermSelectLayout goTermSelect = new TermSelectLayout("Go 研修", membership.getGoTerm());
        form.addComponent(goTermSelect);
        binder.forField(goTermSelect.getTextField())
                .withConverter(goTermSelect.converter())
                .withValidator(goTermSelect.validator())
                .bind(m -> goTermSelect.getValue(), (m, v) -> m.setGoTerm(goTermSelect.setValue(v)));

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

    private static class TermSelectLayout extends HorizontalLayout {

        private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
        private final RadioButtonGroup<TermSelect> radioGroup;
        private final TextField textField;

        private enum TermSelect {
            NOT_A_MEMBER("対象外"), FORGOT("不明"), INPUT("期");
            final String label;

            TermSelect(String label) {
                this.label = label;
            }
        }

        TermSelectLayout(String caption, int initialValue) {
            super();
            setCaption(caption);
            textField = new TextField();
            textField.setWidth(MainUI.FIELD_WIDTH_SHORT, Unit.PIXELS);
            radioGroup = new RadioButtonGroup<>();
            radioGroup.setItemCaptionGenerator(t -> t.label);
            radioGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
            radioGroup.setItems(TermSelect.values());
            switch (initialValue) {
                case -1:
                    radioGroup.setValue(TermSelect.NOT_A_MEMBER);
                    textField.setValue("0");
                    textField.setEnabled(false);
                    break;
                case 0:
                    radioGroup.setValue(TermSelect.FORGOT);
                    textField.setValue("0");
                    textField.setEnabled(false);
                    break;
                default:
                    radioGroup.setValue(TermSelect.INPUT);
                    textField.setValue(String.valueOf(initialValue));
                    textField.setEnabled(true);
                    break;
            }
            radioGroup.addValueChangeListener(e -> textField.setEnabled(e.getValue() == TermSelect.INPUT));
            addComponents(radioGroup, textField);
        }

        TextField getTextField() {
            return textField;
        }

        Converter<String, Integer> converter() {
            return new StringToIntegerConverter("数値を入力してください");
        }

        Validator<Integer> validator() {
            return new AbstractValidator<Integer>("不正な数値です") {
                private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

                @Override
                public ValidationResult apply(Integer value, ValueContext context) {
                    if (radioGroup.getValue() != TermSelect.INPUT) {
                        return ValidationResult.ok();
                    }
                    return value > 0 && value < 100 ? ValidationResult.ok() : ValidationResult.error(getMessage(value));
                }
            };
        }

        int setValue(int value) {
            switch (radioGroup.getValue()) {
                case FORGOT:
                    return 0;
                case NOT_A_MEMBER:
                    return -1;
                default:
                    return value;
            }
        }

        int getValue() {
            switch (radioGroup.getValue()) {
                case FORGOT:
                    return 0;
                case NOT_A_MEMBER:
                    return -1;
                default:
                    return Integer.valueOf(textField.getValue());
            }
        }
    }
}
