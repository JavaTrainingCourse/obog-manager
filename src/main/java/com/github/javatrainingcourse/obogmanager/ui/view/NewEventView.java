/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

/**
 * イベント作成画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = NewEventView.VIEW_NAME)
public class NewEventView extends Wrapper implements View {

    static final String VIEW_NAME = "new-event";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final ConvocationService convocationService;

    @Autowired
    public NewEventView(ConvocationService convocationService) {
        this.convocationService = convocationService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("イベント登録", VaadinIcons.PLUS));

        if (!isAdminLoggedIn() && convocationService.countConvocations() != 0) {
            ErrorView.show("管理者ユーザーでのログインが必要です。", null);
            return;
        }

        Convocation newConvocation = new Convocation();
        newConvocation.setTargetDate(LocalDate.now());
        Binder<Convocation> binder = new Binder<>();
        binder.readBean(newConvocation);

        FormLayout form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        TextField subjectField = new TextField("件名");
        subjectField.setRequiredIndicatorVisible(true);
        subjectField.setPlaceholder("第4回 OB/OG会");
        subjectField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(subjectField);
        binder.forField(subjectField).withValidator(new StringLengthValidator("入力が長すぎます", 0, 64))
                .bind(Convocation::getSubject, Convocation::setSubject);

        DateField targetDateField = new DateField("開催日", newConvocation.getTargetDate());
        targetDateField.setRequiredIndicatorVisible(true);
        form.addComponent(targetDateField);
        binder.forField(targetDateField).bind(Convocation::getTargetDate, Convocation::setTargetDate);

        TextArea descriptionArea = new TextArea("案内文 (Markdown)");
        descriptionArea.setRequiredIndicatorVisible(true);
        descriptionArea.setWidth(100, Unit.PERCENTAGE);
        descriptionArea.setHeight(400, Unit.PIXELS);
        form.addComponent(descriptionArea);
        binder.forField(descriptionArea).withValidator(new StringLengthValidator("入力が長すぎます", 0, 1024))
                .bind(Convocation::getDescriptionAsMarkdown, Convocation::setDescription);

        HorizontalLayout buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        Button backButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        buttonArea.addComponent(backButton);

        Button submitButton = new Button("イベント登録", click -> {
            if (subjectField.isEmpty() || targetDateField.isEmpty() || descriptionArea.isEmpty()) {
                Notification.show("入力が完了していません");
                return;
            }
            if (!binder.writeBeanIfValid(newConvocation)) {
                Notification.show("不正な入力があります");
                return;
            }
            try {
                convocationService.register(newConvocation);
                Notification.show("イベントの登録が完了しました", Notification.Type.ASSISTIVE_NOTIFICATION);
                getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
            } catch (RuntimeException e) {
                ErrorView.show("イベントの登録に失敗しました。", e);
            }
        });
        submitButton.setIcon(VaadinIcons.PLUS);
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(submitButton);
    }
}
