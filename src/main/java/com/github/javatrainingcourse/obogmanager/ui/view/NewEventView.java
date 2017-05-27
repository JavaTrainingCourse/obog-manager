/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.data.Binder;
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
@SpringView(name = NewEventView.VIEW_NAME, ui = MainUI.class)
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
        Label titleLabel = new Label("イベント登録");
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        // TODO: 管理者ログイン & チェック

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
        subjectField.setWidth(300, Unit.PIXELS);
        form.addComponent(subjectField);
        binder.forField(subjectField).bind(Convocation::getSubject, Convocation::setSubject);

        DateField targetDateField = new DateField("開催日", newConvocation.getTargetDate());
        targetDateField.setRequiredIndicatorVisible(true);
        form.addComponent(targetDateField);
        binder.forField(targetDateField).bind(Convocation::getTargetDate, Convocation::setTargetDate);

        TextArea descriptionArea = new TextArea("案内文 (Markdown)");
        descriptionArea.setRequiredIndicatorVisible(true);
        descriptionArea.setWidth(300, Unit.PIXELS);
        form.addComponent(descriptionArea);
        binder.forField(descriptionArea).bind(Convocation::getDescription, Convocation::setDescription);

        Button submitButton = new Button("イベント登録", click -> {
            if (!binder.writeBeanIfValid(newConvocation)) {
                Notification.show("入力が完了していません");
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
        addComponent(submitButton);
        setComponentAlignment(submitButton, Alignment.MIDDLE_CENTER);
    }
}
