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
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
@SpringView(name = RequestListView.VIEW_NAME)
public class RequestListView extends Wrapper implements View {

    static final String VIEW_NAME = "request-list";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
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
        addComponent(new HeadingLabel("会員・イベント参加者一覧", VaadinIcons.USERS));

        // パスパラメーターを取得
        var cId = Stream.of(event.getParameters().split("/")).filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong).findFirst().orElse(-1);

        List<Convocation> convocations;
        try {
            convocations = convocationService.getAll();
        } catch (RuntimeException e) {
            ErrorView.show("イベント招集一覧の取得に失敗しました。", e);
            return;
        }
        var selections = convocations.stream().map(Convocation::getSubject).collect(Collectors.toList());
        var allMembersItem = "会員一覧";
        selections.add(0, allMembersItem);

        var comboBoxArea = new HorizontalLayout();
        comboBoxArea.setSpacing(true);
        addComponent(comboBoxArea);

        var convocationComboBox = new ComboBox<String>();
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
                var id = convocations.get(selections.indexOf(e.getValue()) - 1).getId();
                getUI().getNavigator().navigateTo(RequestListView.VIEW_NAME + "/" + id);
            }
        });
        comboBoxArea.addComponent(convocationComboBox);

        if (cId == -1) {
            List<Membership> memberships;
            try {
                memberships = membershipService.getAll();
            } catch (RuntimeException e) {
                ErrorView.show("会員一覧の取得に失敗しました。", e);
                return;
            }
            // TXT
            var downloadTxtButton = new Button("TXT (名前)");
            downloadTxtButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
            var txtDownloader = new FileDownloader(createTXT(allMembersItem, memberships));
            txtDownloader.extend(downloadTxtButton);
            comboBoxArea.addComponent(downloadTxtButton);
            // CSV
            var downloadCsvButton = new Button("CSV (名前,Java,Java8,Go)");
            downloadCsvButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
            var csvDownloader = new FileDownloader(createCSV4(allMembersItem, memberships));
            csvDownloader.extend(downloadCsvButton);
            comboBoxArea.addComponent(downloadCsvButton);
            // Grid
            printAllMembers(memberships);
        } else {
            var convocation = convocations.stream().filter(c -> c.getId() == cId).findAny().orElse(null);
            if (convocation == null) {
                ErrorView.show("指定されたイベント招集が見つかりません: " + cId, null);
                return;
            }
            List<Attendance> attendances;
            try {
                attendances = attendanceService.getResponses(convocation);
            } catch (RuntimeException e) {
                ErrorView.show("申込一覧の取得に失敗しました。", e);
                return;
            }
            // TXT
            var downloadTxtButton = new Button("TXT (名前)");
            downloadTxtButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
            var txtDownloader = new FileDownloader(createTXT(convocation.getSubject(), attendances.stream()
                    .filter(Attendance::isAttend).map(Attendance::getMembership).collect(Collectors.toList())));
            txtDownloader.extend(downloadTxtButton);
            comboBoxArea.addComponent(downloadTxtButton);
            // CSV
            var downloadCsvButton = new Button("CSV (名前,コメント)");
            downloadCsvButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
            var csvDownloader = new FileDownloader(createCSV2(convocation.getSubject(), attendances));
            csvDownloader.extend(downloadCsvButton);
            comboBoxArea.addComponent(downloadCsvButton);
            // Grid
            printConvocationResponses(attendances);
        }
        var homeButton = new Button("会員メニュー", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.USER);
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }

    private void printAllMembers(List<Membership> memberships) {
        var membershipGrid = new Grid<MemberInfo>();
        membershipGrid.setItems(memberships.stream().map(MemberInfo::from).collect(Collectors.toList()));
        membershipGrid.addColumn(MemberInfo::getName).setCaption("名前");
        membershipGrid.addColumn(MemberInfo::getAdmin).setCaption("Admin");
        membershipGrid.addColumn(MemberInfo::getEmail).setCaption("E-mail");
        membershipGrid.addColumn(MemberInfo::getJavaTerm).setCaption("Java研修");
        membershipGrid.addColumn(MemberInfo::getJava8Term).setCaption("Java8研修");
        membershipGrid.addColumn(MemberInfo::getGoTerm).setCaption("Go研修");
        membershipGrid.setWidth(100, Unit.PERCENTAGE);
        if (!memberships.isEmpty()) {
            membershipGrid.setHeightByRows(memberships.size());
        }
        addComponent(membershipGrid);
    }

    private void printConvocationResponses(List<Attendance> attendances) {
        var membershipGrid = new Grid<MemberInfo>();
        membershipGrid.setItems(attendances.stream().map(MemberInfo::from).collect(Collectors.toList()));
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

    private StreamResource createCSV2(String subject, List<Attendance> attendances) {
        return new StreamResource(() -> {
            var csv = "名前,コメント\r\n" + attendances.stream().filter(Attendance::isAttend)
                    .map(a -> a.getMembership().getName() + "," +
                            a.getComment().replaceAll("\r", "").replaceAll("\n", ""))
                    .collect(Collectors.joining("\r\n"));
            try {
                return new ByteArrayInputStream(csv.getBytes("Shift_JIS"));
            } catch (UnsupportedEncodingException e) {
                return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
            }
        }, subject + ".csv");
    }

    private StreamResource createCSV4(String subject, List<Membership> memberships) {
        return new StreamResource(() -> {
            var csv = "名前,Java 研修,Java 8 研修,Go 研修\r\n" + memberships.stream()
                    .map(m -> m.getName() + "," + MemberInfo.safeTerm2Text(m.getJavaTerm()) + "," +
                            MemberInfo.safeTerm2Text(m.getJava8Term()) + "," + MemberInfo.safeTerm2Text(m.getGoTerm()))
                    .collect(Collectors.joining("\r\n"));
            try {
                return new ByteArrayInputStream(csv.getBytes("Shift_JIS"));
            } catch (UnsupportedEncodingException e) {
                return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
            }
        }, subject + ".csv");
    }

    private StreamResource createTXT(String subject, List<Membership> memberships) {
        return new StreamResource(() -> {
            var list = memberships.stream()
                    .map(Membership::getName)
                    .collect(Collectors.joining("\r\n"));
            try {
                return new ByteArrayInputStream(list.getBytes("Shift_JIS"));
            } catch (UnsupportedEncodingException e) {
                return new ByteArrayInputStream(list.getBytes(StandardCharsets.UTF_8));
            }
        }, subject + ".txt");
    }

    static class MemberInfo {
        private String name;
        private String admin;
        private String email;
        private String attend;

        public String getName() {
            return name;
        }

        String getAdmin() {
            return admin;
        }

        String getEmail() {
            return email;
        }

        String getAttend() {
            return attend;
        }

        String getComment() {
            return comment;
        }

        Date getEntryDate() {
            return entryDate;
        }

        String getJavaTerm() {
            return javaTerm;
        }

        String getJava8Term() {
            return java8Term;
        }

        String getGoTerm() {
            return goTerm;
        }

        private String comment;
        private Date entryDate;
        private String javaTerm;
        private String java8Term;
        private String goTerm;

        static MemberInfo from(Membership membership) {
            var info = new MemberInfo();
            info.name = membership.getName();
            info.admin = membership.isAdmin() ? "✔" : "";
            info.email = membership.getEmail();
            info.javaTerm = term2Text(membership.getJavaTerm());
            info.java8Term = term2Text(membership.getJava8Term());
            info.goTerm = term2Text(membership.getGoTerm());
            return info;
        }

        static MemberInfo from(Attendance attendance) {
            var info = new MemberInfo();
            var membership = attendance.getMembership();
            info.name = membership.getName();
            info.admin = membership.isAdmin() ? "✔" : "";
            info.email = membership.getEmail();
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
                    return "✔ " + term + "期";
            }
        }

        private static String safeTerm2Text(int term) {
            switch (term) {
                case -1:
                    return "";
                case 0:
                    return "?";
                default:
                    return String.valueOf(term);
            }
        }
    }
}
