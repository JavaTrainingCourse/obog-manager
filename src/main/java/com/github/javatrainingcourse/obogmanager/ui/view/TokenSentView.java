/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Value;

/**
 * パスワードリセット要求送信完了画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = TokenSentView.VIEW_NAME, ui = MainUI.class)
public class TokenSentView extends Wrapper implements View {

    static final String VIEW_NAME = "token-sent";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Value("${app.reply}")
    private String appReply;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label titleLabel = new Label(VaadinIcons.CHECK.getHtml() + " パスワードリセット要求送信完了", ContentMode.HTML);
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        Label messageLabel = new Label("入力された E-mail アドレスへパスワードリセットの案内メールを送信しました。");
        addComponent(messageLabel);

        Label addressLabel = new Label("しばらく待ってもメールが来ない場合は、お手数ですが " + appReply + " までご連絡ください。");
        addressLabel.setCaption("お願い");
        addressLabel.setIcon(VaadinIcons.LIGHTBULB);
        addComponent(messageLabel);

        Button homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }
}
