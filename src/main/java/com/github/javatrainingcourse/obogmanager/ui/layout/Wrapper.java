/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.layout;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.view.LogoutConfirmView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 各 {@link com.vaadin.navigator.View} に共通の表示を含むラッパーを提供します。
 *
 * @author mikan
 * @since 0.1
 */
public class Wrapper extends VerticalLayout {

    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

    public Wrapper() {
        Label titleLabel = new Label("Java研修 Go研修 OB/OG会");
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);
        setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER);

        Label alphaTestingLabel = new Label("本システムは現在パブリックアルファテスト中です。入力データは正式運用までに破棄される可能性があります。");
        alphaTestingLabel.setStyleName(ValoTheme.LABEL_FAILURE);
        addComponent(alphaTestingLabel);
        setComponentAlignment(alphaTestingLabel, Alignment.MIDDLE_CENTER);

        Membership membership = MembershipService.getCurrentMembership();
        if (membership != null) {
            HorizontalLayout userInfoArea = new HorizontalLayout();
            userInfoArea.setSpacing(true);
            addComponent(userInfoArea);

            Label userLabel = new Label("ようこそ! " + membership.getName() + " さん");
            userInfoArea.addComponent(userLabel);
            userInfoArea.setComponentAlignment(userLabel, Alignment.MIDDLE_LEFT);

            Button logoutButton = new Button("ログアウト",
                    click -> getUI().getNavigator().navigateTo(LogoutConfirmView.VIEW_NAME));
            logoutButton.setStyleName(ValoTheme.BUTTON_SMALL);
            userInfoArea.addComponent(logoutButton);
            userInfoArea.setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT);
        }
    }

    protected final boolean isLoggedIn() {
        return MembershipService.getCurrentMembership() != null;
    }

    protected final boolean isAdminLoggedIn() {
        Membership membership = getMembership();
        return membership != null && membership.isAdmin();
    }

    protected final Membership getMembership() {
        return MembershipService.getCurrentMembership();
    }
}
