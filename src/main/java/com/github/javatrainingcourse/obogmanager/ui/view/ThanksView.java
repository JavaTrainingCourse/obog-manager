/*
 * Copyright (c) 2017-2018 mikan
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
 * 登録完了画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = ThanksView.VIEW_NAME)
public class ThanksView extends Wrapper implements View {

    static final String VIEW_NAME = "thanks";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Value("${app.reply}")
    private String appReply;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("参加登録完了", VaadinIcons.CHECK));
        addComponent(new Label("参加登録が完了し、確認メールを送信しました。"));
        var addressLabel = new Label("しばらく待ってもメールが来ない場合は、お手数ですが " + appReply + " までご連絡ください。");
        addressLabel.setCaption("お願い");
        addressLabel.setIcon(VaadinIcons.LIGHTBULB);
        addComponent(addressLabel);
        var homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.HOME);
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }
}
