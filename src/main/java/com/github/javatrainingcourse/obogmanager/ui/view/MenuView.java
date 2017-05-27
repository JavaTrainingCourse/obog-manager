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
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 会員メニュー画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = MenuView.VIEW_NAME, ui = MainUI.class)
public class MenuView extends Wrapper implements View {

    public static final String VIEW_NAME = "menu";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label titleLabel = new Label("会員メニュー");
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        Label messageLabel = new Label("まだなにもありません。");
        addComponent(messageLabel);

        // TODO: 会員メニューを実装する
    }
}
