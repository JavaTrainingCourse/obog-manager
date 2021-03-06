/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * イベント編集画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = EditEventView.VIEW_NAME)
public class EditEventView extends Wrapper implements View {

    static final String VIEW_NAME = "edit-event";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final ConvocationService convocationService;

    @Autowired
    public EditEventView(ConvocationService convocationService) {
        this.convocationService = convocationService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("イベント編集", VaadinIcons.TEXT_INPUT));

        if (!isAdminLoggedIn() && convocationService.countConvocations() != 0) {
            ErrorView.show("管理者ユーザーでのログインが必要です。", null);
            return;
        }

        // パスパラメーターを取得
        var cId = Stream.of(event.getParameters().split("/")).filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong).findFirst().orElse(-1);

        List<Convocation> convocations;
        try {
            convocations = convocationService.getAll();
        } catch (RuntimeException e) {
            ErrorView.show("イベント一覧の取得に失敗しました。", e);
            return;
        }
        if (convocations.isEmpty()) {
            addComponent(new Label("イベントが1件も登録されていません。"));
            var backButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
            addComponent(backButton);
            setComponentAlignment(backButton, Alignment.MIDDLE_CENTER);
            return;
        }
        var selections = convocations.stream().map(Convocation::getSubject).collect(Collectors.toList());
        var convocationComboBox = new ComboBox<String>();
        convocationComboBox.setEmptySelectionAllowed(false);
        convocationComboBox.setTextInputAllowed(false);
        convocationComboBox.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        convocationComboBox.setItems(selections);
        convocationComboBox.setValue(convocations.stream()
                .filter(c -> c.getId() == cId).map(Convocation::getSubject).findAny().orElse(""));
        convocationComboBox.addValueChangeListener(e -> {
            var id = convocations.get(selections.indexOf(e.getValue())).getId();
            getUI().getNavigator().navigateTo(EditEventView.VIEW_NAME + "/" + id);
        });
        addComponent(convocationComboBox);

        if (cId == -1) {
            addComponent(new Label("イベントを選択してください。"));
            var backButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
            addComponent(backButton);
            setComponentAlignment(backButton, Alignment.MIDDLE_CENTER);
            return;
        }

        var convocation = convocations.stream().filter(c -> c.getId() == cId).findAny().orElse(null);
        if (convocation == null) {
            ErrorView.show("指定されたイベント招集が見つかりません: " + cId, null);
            return;
        }

        var binder = new Binder<Convocation>();
        binder.readBean(convocation);

        var form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        var subjectField = new TextField("件名", convocation.getSubject());
        subjectField.setRequiredIndicatorVisible(true);
        subjectField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(subjectField);
        binder.forField(subjectField).withValidator(new StringLengthValidator("入力が長すぎます", 0, 64))
                .bind(Convocation::getSubject, Convocation::setSubject);

        var targetDateField = new DateField("開催日", convocation.getTargetDate());
        targetDateField.setRequiredIndicatorVisible(true);
        form.addComponent(targetDateField);
        binder.forField(targetDateField).bind(Convocation::getTargetDate, Convocation::setTargetDate);

        var descriptionArea = new TextArea("案内文 (Markdown)", convocation.getDescriptionAsMarkdown());
        descriptionArea.setRequiredIndicatorVisible(true);
        descriptionArea.setWidth(100, Unit.PERCENTAGE);
        descriptionArea.setHeight(400, Unit.PIXELS);
        form.addComponent(descriptionArea);
        binder.forField(descriptionArea).withValidator(new StringLengthValidator("入力が長すぎます", 0, 8192))
                .bind(Convocation::getDescriptionAsMarkdown, Convocation::setDescription);

        var buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);
        var backButton = new Button("会員メニュー", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        backButton.setIcon(VaadinIcons.USER);
        buttonArea.addComponent(backButton);
        var submitButton = new Button("変更反映", click -> {
            if (subjectField.isEmpty() || targetDateField.isEmpty() || descriptionArea.isEmpty()) {
                Notification.show("入力が完了していません");
                return;
            }
            if (!binder.writeBeanIfValid(convocation)) {
                Notification.show("不正な入力があります");
                return;
            }
            try {
                convocationService.update(convocation);
                SuccessNotification.show("イベントの変更が完了しました");
                getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
            } catch (RuntimeException e) {
                ErrorView.show("イベントの変更に失敗しました。", e);
            }
        });
        submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(submitButton);
    }
}
