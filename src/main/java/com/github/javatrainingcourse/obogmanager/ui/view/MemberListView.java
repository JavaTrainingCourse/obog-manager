/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.github.javatrainingcourse.obogmanager.ui.view.RequestListView.MemberInfo;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会員名簿画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = MemberListView.VIEW_NAME)
public class MemberListView extends Wrapper implements View {

    static final String VIEW_NAME = "member-list";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final MembershipService membershipService;

    @Autowired
    public MemberListView(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isLoggedIn()) {
            getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
            return;
        }
        addComponent(new HeadingLabel("会員名簿", VaadinIcons.BULLETS));

        printAllMembers();

        var homeButton = new Button("会員メニュー", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.USER);
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }

    private void printAllMembers() {
        List<Membership> memberships;
        try {
            memberships = membershipService.getAll();
        } catch (RuntimeException e) {
            ErrorView.show("会員一覧の取得に失敗しました。", e);
            return;
        }
        var membershipGrid = new Grid<MemberInfo>();
        membershipGrid.setItems(memberships.stream().map(MemberInfo::from).collect(Collectors.toList()));
        membershipGrid.addColumn(MemberInfo::getName).setCaption("名前");
        membershipGrid.addColumn(MemberInfo::getJavaTerm).setCaption("Java研修");
        membershipGrid.addColumn(MemberInfo::getJava8Term).setCaption("Java8研修");
        membershipGrid.addColumn(MemberInfo::getGoTerm).setCaption("Go研修");
        membershipGrid.setWidth(100, Unit.PERCENTAGE);
        if (!memberships.isEmpty()) {
            membershipGrid.setHeightByRows(memberships.size());
        }
        addComponent(membershipGrid);
    }
}
