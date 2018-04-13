/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.model.Membership;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 参加者一覧画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = AttendeeListView.VIEW_NAME)
public class AttendeeListView extends Wrapper implements View {

    static final String VIEW_NAME = "attendees";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final ConvocationService convocationService;
    private transient final AttendanceService attendanceService;

    @Autowired
    public AttendeeListView(ConvocationService convocationService, AttendanceService attendanceService) {
        this.convocationService = convocationService;
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isLoggedIn()) {
            getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
            return;
        }
        addComponent(new HeadingLabel("イベント参加者一覧", VaadinIcons.USERS));

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
        List<String> selections = convocations.stream().map(Convocation::getSubject).collect(Collectors.toList());
        String allMembersItem = "参加者一覧";
        selections.add(0, allMembersItem);

        if (cId == -1) {
            addComponent(new Label("パラメーターがありません。"));
        } else {
            Convocation convocation = convocations.stream().filter(c -> c.getId() == cId).findAny().orElse(null);
            if (convocation == null) {
                ErrorView.show("指定されたイベント招集が見つかりません: " + cId, null);
                return;
            }
            addComponent(new Label("イベント名: " + convocation.getSubject()));
            List<Attendance> attendances;
            try {
                attendances = attendanceService.getResponses(convocation);
            } catch (RuntimeException e) {
                ErrorView.show("申込一覧の取得に失敗しました。", e);
                return;
            }
            // Grid
            printConvocationResponses(attendances);
        }
        Button homeButton = new Button("会員メニュー", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.USER);
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }

    private void printConvocationResponses(List<Attendance> attendances) {
        List<MemberInfo> attends = attendances.stream()
                .filter(Attendance::isAttend)
                .map(MemberInfo::from)
                .collect(Collectors.toList());
        Grid<MemberInfo> attendsGrid = new Grid<>("参加者一覧 (" + attends.size() + ")");
        attendsGrid.setItems(attends);
        attendsGrid.addColumn(MemberInfo::getName).setCaption("名前");
        attendsGrid.addColumn(MemberInfo::getComment).setCaption("コメント");
        attendsGrid.addColumn(MemberInfo::getCreated).setCaption("登録日時");
        attendsGrid.addColumn(MemberInfo::getUpdated).setCaption("更新日時");
        attendsGrid.sort(attendsGrid.getColumns().get(3), SortDirection.DESCENDING);
        attendsGrid.setWidth(100, Unit.PERCENTAGE);
        if (!attends.isEmpty()) {
            attendsGrid.setHeightByRows(attends.size());
        }
        addComponent(attendsGrid);
        List<MemberInfo> cancels = attendances.stream()
                .filter(a -> !a.isAttend())
                .map(MemberInfo::from)
                .collect(Collectors.toList());
        Grid<MemberInfo> cancelsGrid = new Grid<>("キャンセル一覧 (" + cancels.size() + ")");
        cancelsGrid.setItems(cancels);
        cancelsGrid.addColumn(MemberInfo::getName).setCaption("名前");
        cancelsGrid.addColumn(MemberInfo::getComment).setCaption("コメント");
        cancelsGrid.addColumn(MemberInfo::getCreated).setCaption("登録日時");
        cancelsGrid.addColumn(MemberInfo::getUpdated).setCaption("更新日時");
        cancelsGrid.sort(cancelsGrid.getColumns().get(3), SortDirection.DESCENDING);
        cancelsGrid.setWidth(100, Unit.PERCENTAGE);
        if (cancels.isEmpty()) {
            cancelsGrid.setVisible(false);
        } else {
            cancelsGrid.setHeightByRows(cancels.size());
        }
        addComponent(cancelsGrid);
    }

    static class MemberInfo {
        private String name;
        private String comment;
        private Date created;
        private Date updated;

        static MemberInfo from(Attendance attendance) {
            MemberInfo info = new MemberInfo();
            Membership membership = attendance.getMembership();
            info.name = membership.getName();
            info.comment = attendance.getComment();
            info.created = attendance.getCreatedDate();
            info.updated = attendance.getLastUpdateDate();
            return info;
        }

        String getName() {
            return name;
        }

        String getComment() {
            return comment;
        }

        Date getCreated() {
            return created;
        }

        Date getUpdated() {
            return updated;
        }
    }
}
