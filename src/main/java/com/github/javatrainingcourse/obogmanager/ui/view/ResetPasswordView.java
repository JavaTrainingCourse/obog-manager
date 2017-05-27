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
 * パスワードリセット画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = ResetPasswordView.VIEW_NAME, ui = MainUI.class)
public class ResetPasswordView extends Wrapper implements View {

    static final String VIEW_NAME = "reset-password";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label titleLabel = new Label("パスワードリセット");
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        Label messageLabel = new Label("ごめんなさい。未実装です。");
        addComponent(messageLabel);

        // TODO: パスワードリセット機能を実装する

        Button homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }
}
