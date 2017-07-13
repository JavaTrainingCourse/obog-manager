/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * ログアウト確認画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = LogoutConfirmView.VIEW_NAME)
@Slf4j
public class LogoutConfirmView extends Wrapper implements View {

    public static final String VIEW_NAME = "logout";
    private static final long serialVersionUID = Version.INSTANCE.getOBOG_MANAGER_SERIAL_VERSION_UID();
    private transient final MembershipService membershipService;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public LogoutConfirmView(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new HeadingLabel("ログアウト確認", VaadinIcons.INFO_CIRCLE));
        addComponent(new Label("ログアウトします。"));

        HorizontalLayout buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        Button cancelButton = new Button("キャンセル", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        buttonArea.addComponent(cancelButton);

        Button logoutButton = new Button("ログアウト", click -> {
            membershipService.logout();
            getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
        });
        logoutButton.setIcon(VaadinIcons.SIGN_OUT);
        logoutButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(logoutButton);
    }
}
