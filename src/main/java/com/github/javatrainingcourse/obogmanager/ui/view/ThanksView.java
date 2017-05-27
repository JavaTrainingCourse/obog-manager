/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 登録完了画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = ThanksView.VIEW_NAME, ui = MainUI.class)
public class ThanksView extends Wrapper implements View {

    static final String VIEW_NAME = "thanks";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label titleLabel = new Label("参加登録完了");
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        Label messageLabel = new Label("メールを送信しました。ご確認ください。");
        addComponent(messageLabel);

        Button homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }
}
