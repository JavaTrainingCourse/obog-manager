/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.annotation.Value;

/**
 * パスワードリセット要求送信完了画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = TokenSentView.VIEW_NAME)
public class TokenSentView extends Wrapper implements View {

    static final String VIEW_NAME = "token-sent";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Value("${app.reply}")
    private String appReply;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("パスワードリセット要求送信完了", VaadinIcons.INFO_CIRCLE));
        addComponent(new Label("入力された E-mail アドレスへパスワードリセットの案内メールを送信しました。"));
        Label addressLabel = new Label("しばらく待ってもメールが来ない場合は、お手数ですが " + appReply + " までご連絡ください。");
        addressLabel.setCaption("お願い");
        addressLabel.setIcon(VaadinIcons.LIGHTBULB);
        addComponent(addressLabel);
        Button homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.HOME);
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }
}
