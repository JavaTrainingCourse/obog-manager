/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 管理者用会員・参加者一覧画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = RequestListView.VIEW_NAME, ui = MainUI.class)
public class RequestListView extends Wrapper implements View {

    static final String VIEW_NAME = "request-list";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final MembershipService membershipService;
    private transient final ConvocationService convocationService;
    private transient final AttendanceService attendanceService;

    @Autowired
    public RequestListView(MembershipService membershipService, ConvocationService convocationService,
                           AttendanceService attendanceService) {
        this.membershipService = membershipService;
        this.convocationService = convocationService;
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isAdminLoggedIn()) {
            ErrorView.show("管理者ユーザーでのログインが必要です。", null);
            return;
        }

        Label titleLabel = new Label(VaadinIcons.USERS.getHtml() + " 会員・イベント参加者一覧", ContentMode.HTML);
        titleLabel.setStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);

        // パスパラメーターを取得
        long cId = Stream.of(event.getParameters().split("/")).filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong).findFirst().orElse(-1);

        List<Convocation> convocations;
        try {
            convocations = convocationService.getAll();
        } catch (RuntimeException e) {
            ErrorView.show("イベント招集一覧の取得に失敗しました。", e);
            return;
        }
        String allMembersItem = "会員一覧";
        List<String> selections = convocations.stream().map(Convocation::getSubject).collect(Collectors.toList());
        selections.add(0, allMembersItem);
        ComboBox<String> convocationComboBox = new ComboBox<>();
        convocationComboBox.setEmptySelectionAllowed(false);
        convocationComboBox.setTextInputAllowed(false);
        convocationComboBox.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        convocationComboBox.setItems(selections);
        convocationComboBox.setValue(cId == -1 ? allMembersItem : convocations.stream()
                .filter(c -> c.getId() == cId).map(Convocation::getSubject).findAny().orElse(""));
        convocationComboBox.addValueChangeListener(e -> {
            if (e.getValue().equals(allMembersItem)) {
                getUI().getNavigator().navigateTo(RequestListView.VIEW_NAME);
            } else {
                long id = convocations.get(selections.indexOf(e.getValue()) - 1).getId();
                getUI().getNavigator().navigateTo(RequestListView.VIEW_NAME + "/" + id);
            }
        });
        addComponent(convocationComboBox);

        if (cId == -1) {
            printAllMembers();
        } else {
            Convocation convocation = convocations.stream().filter(c -> c.getId() == cId).findAny().orElse(null);
            if (convocation == null) {
                ErrorView.show("指定されたイベント招集が見つかりません: " + cId, null);
                return;
            }
            printConvocationResponses(convocation);
        }

        Button homeButton = new Button("会員メニュー", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
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
        Grid<MemberInfo> membershipGrid = new Grid<>();
        membershipGrid.setItems(memberships.stream().map(MemberInfo::from).collect(Collectors.toList()));
        membershipGrid.addColumn(MemberInfo::getMembershipId).setCaption("#");
        membershipGrid.addColumn(MemberInfo::getName).setCaption("名前");
        membershipGrid.addColumn(MemberInfo::getEmail).setCaption("E-mail");
        membershipGrid.addColumn(MemberInfo::getJavaTerm).setCaption("Java研修");
        membershipGrid.addColumn(MemberInfo::getJava8Term).setCaption("Java8研修");
        membershipGrid.addColumn(MemberInfo::getGoTerm).setCaption("Go研修");
        membershipGrid.setWidth(100, Unit.PERCENTAGE);
        membershipGrid.setHeightByRows(memberships.size());
        addComponent(membershipGrid);
    }

    private void printConvocationResponses(Convocation convocation) {
        List<Attendance> attendances;
        try {
            attendances = attendanceService.getResponses(convocation);
        } catch (RuntimeException e) {
            ErrorView.show("申込一覧の取得に失敗しました。", e);
            return;
        }
        Grid<MemberInfo> membershipGrid = new Grid<>();
        membershipGrid.setItems(attendances.stream().map(MemberInfo::from).collect(Collectors.toList()));
        membershipGrid.addColumn(MemberInfo::getMembershipId).setCaption("#");
        membershipGrid.addColumn(MemberInfo::getName).setCaption("名前");
        membershipGrid.addColumn(MemberInfo::getEmail).setCaption("E-mail");
        membershipGrid.addColumn(MemberInfo::getAttend).setCaption("参加");
        membershipGrid.addColumn(MemberInfo::getComment).setCaption("コメント");
        membershipGrid.addColumn(MemberInfo::getEntryDate).setCaption("更新日時");
        membershipGrid.setWidth(100, Unit.PERCENTAGE);
        if (!attendances.isEmpty()) {
            membershipGrid.setHeightByRows(attendances.size());
        }
        addComponent(membershipGrid);
    }

    @Getter
    static class MemberInfo {

        private Long membershipId;
        private String name;
        private String email;
        private String attend;
        private String comment;
        private Date entryDate;
        private String javaTerm;
        private String java8Term;
        private String goTerm;

        static MemberInfo from(Membership membership) {
            MemberInfo info = new MemberInfo();
            info.membershipId = membership.getId();
            info.name = membership.getName();
            info.email = membership.getEmail();
            info.javaTerm = term2Text(membership.getJavaTerm());
            info.java8Term = term2Text(membership.getJava8Term());
            info.goTerm = term2Text(membership.getGoTerm());
            return info;
        }

        static MemberInfo from(Attendance attendance) {
            MemberInfo info = new MemberInfo();
            info.membershipId = attendance.getMembership().getId();
            info.name = attendance.getMembership().getName();
            info.email = attendance.getMembership().getEmail();
            info.attend = attendance.getAttend() ? "✔" : "";
            info.comment = attendance.getComment();
            info.entryDate = attendance.getLastUpdateDate();
            return info;
        }

        private static String term2Text(int term) {
            switch (term) {
                case -1:
                    return "";
                case 0:
                    return "✔";
                default:
                    return "✔ " + String.valueOf(term) + "期";
            }
        }
    }
}
