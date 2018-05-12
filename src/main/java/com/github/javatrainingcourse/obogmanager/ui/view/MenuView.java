/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.domain.model.Attendance;
import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.service.AttendanceService;
import com.github.javatrainingcourse.obogmanager.domain.service.ConvocationService;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
import com.github.javatrainingcourse.obogmanager.ui.layout.AboutWindow;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 会員メニュー画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = MenuView.VIEW_NAME)
public class MenuView extends Wrapper implements View {

    public static final String VIEW_NAME = "menu";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private static final DateFormat FORMATTER = new SimpleDateFormat("MM/dd hh:mm");
    private transient final MembershipService membershipService;
    private transient final ConvocationService convocationService;
    private transient final AttendanceService attendanceService;

    @Autowired
    public MenuView(MembershipService membershipService, ConvocationService convocationService,
                    AttendanceService attendanceService) {
        this.membershipService = membershipService;
        this.convocationService = convocationService;
        this.attendanceService = attendanceService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isLoggedIn()) {
            getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
            return;
        }
        addComponent(new HeadingLabel("会員メニュー", VaadinIcons.USER));

        var membershipMenuArea = new HorizontalLayout();
        addComponent(membershipMenuArea);
        var memberListButton = new Button("会員名簿 (" + membershipService.countMemberships() + ")",
                click -> getUI().getNavigator().navigateTo(MemberListView.VIEW_NAME));
        memberListButton.setIcon(VaadinIcons.BULLETS);
        membershipMenuArea.addComponent(memberListButton);
        var editMembershipButton = new Button("会員情報編集",
                click -> getUI().getNavigator().navigateTo(EditMembershipView.VIEW_NAME));
        editMembershipButton.setIcon(VaadinIcons.EDIT);
        membershipMenuArea.addComponent(editMembershipButton);

        Convocation convocation;
        Attendance attendance;
        try {
            convocation = convocationService.getLatestConvocation();
            if (convocation == null) {
                if (isAdminLoggedIn()) {
                    printAdminMenu();
                }
                return;
            }
            attendance = attendanceService.find(getMembership(), convocation);
        } catch (IllegalStateException e) {
            if (isAdminLoggedIn()) {
                getUI().getNavigator().navigateTo(NewEventView.VIEW_NAME); // 招集がない場合は最初の招集の登録画面へ
            }
            addComponent(new Label("まだイベント招集が登録されていません。"));
            return;
        } catch (RuntimeException e) {
            ErrorView.show("参加情報の取得に失敗しました。", e);
            return;
        }

        var convocationLabel = new Label(convocation.getSubject() + " 参加登録状況");
        convocationLabel.setStyleName(ValoTheme.LABEL_H3);
        addComponent(convocationLabel);

        try {
            var counts = attendanceService.countAttendees(convocation);
            var attendeesButton = new Button("参加予定者一覧 (" + counts.getFirst() + ")",
                    click -> getUI().getNavigator().navigateTo(AttendeeListView.VIEW_NAME + "/" + convocation.getId()));
            attendeesButton.setStyleName(ValoTheme.BUTTON_SMALL);
            addComponent(attendeesButton);
        } catch (RuntimeException e) {
            ErrorView.show("参加数の取得に失敗しました。", e);
            return;
        }

        if (attendance == null) {
            addComponent(new Label("あなたの参加登録はまだありません。"));
            var checkItButton = new Button("最新のイベント招集をチェック",
                    click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
            checkItButton.setStyleName(ValoTheme.BUTTON_SMALL + " " + ValoTheme.BUTTON_FRIENDLY);
            addComponent(checkItButton);
        } else {
            printAttendance(attendance);
        }
        if (isAdminLoggedIn()) {
            printAdminMenu();
        }

        var buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        var homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.HOME);
        buttonArea.addComponent(homeButton);

        var facebookGroupButton = new Button("Facebook グループ", click -> UI.getCurrent().getPage()
                .setLocation("https://www.facebook.com/groups/351472538254785/"));
        facebookGroupButton.setIcon(VaadinIcons.COMMENTS);
        buttonArea.addComponent(facebookGroupButton);

        var aboutAppButton = new Button("このアプリについて", click -> {
            AboutWindow aboutWindow = new AboutWindow();
            UI.getCurrent().addWindow(aboutWindow);
        });
        aboutAppButton.setIcon(VaadinIcons.INFO_CIRCLE);
        buttonArea.addComponent(aboutAppButton);
    }

    private void printAttendance(Attendance attendance) {
        if (attendance.isAttend()) {
            Label latestAttendanceLabel = new Label("あなたは " + FORMATTER.format(attendance.getCreatedDate()) + " に申込みが完了しています。");
            addComponent(latestAttendanceLabel);
        } else {
            Label latestAttendanceLabel = new Label("あなたは " + FORMATTER.format(attendance.getLastUpdateDate()) + " に申込みをキャンセルしました。");
            addComponent(latestAttendanceLabel);
        }

        var form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        var commentArea = new TextArea("コメント", attendance.getComment());
        commentArea.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        form.addComponent(commentArea);

        var attendeeOperationsArea = new HorizontalLayout();
        attendeeOperationsArea.setSpacing(true);
        addComponent(attendeeOperationsArea);

        var commentUpdateButton = new Button("コメント修正", click -> {
            attendance.setComment(commentArea.getValue());
            try {
                attendanceService.updateComment(attendance);
            } catch (RuntimeException e) {
                ErrorView.show("コメント修正に失敗しました。", e);
                return;
            }
            getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
            SuccessNotification.show("コメント修正が完了しました");
        });
        commentUpdateButton.setStyleName(ValoTheme.BUTTON_SMALL + " " + ValoTheme.BUTTON_FRIENDLY);
        attendeeOperationsArea.addComponent(commentUpdateButton);

        if (attendance.isAttend()) {
            var cancelButton = new Button("キャンセル申込", click -> {
                attendance.setAttend(false);
                attendance.setComment(commentArea.getValue());
                try {
                    attendanceService.update(attendance);
                } catch (RuntimeException e) {
                    ErrorView.show("キャンセル申込に失敗しました。", e);
                    return;
                }
                getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
                SuccessNotification.show("キャンセル申込が完了しました");
            });
            cancelButton.setStyleName(ValoTheme.BUTTON_SMALL + " " + ValoTheme.BUTTON_DANGER);
            attendeeOperationsArea.addComponent(cancelButton);
        } else {
            var reEntryButton = new Button("再申込", click -> {
                attendance.setAttend(true);
                attendance.setComment(commentArea.getValue());
                try {
                    attendanceService.update(attendance);
                } catch (RuntimeException e) {
                    ErrorView.show("再申込に失敗しました。", e);
                    return;
                }
                getUI().getNavigator().navigateTo(FrontView.VIEW_NAME);
                SuccessNotification.show("再申込が完了しました");
            });
            reEntryButton.setStyleName(ValoTheme.BUTTON_SMALL + " " + ValoTheme.BUTTON_FRIENDLY);
            attendeeOperationsArea.addComponent(reEntryButton);
        }
    }

    private void printAdminMenu() {
        addComponent(new HeadingLabel("管理者メニュー", VaadinIcons.USER_STAR));

        var nOfMembershipsLabel = new Label("登録会員数: " + membershipService.countMemberships());
        addComponent(nOfMembershipsLabel);

        var nOfConvocationsLabel = new Label("登録イベント数: " + convocationService.countConvocations());
        addComponent(nOfConvocationsLabel);

        var requestListButton = new Button("会員・参加者一覧 (管理者用)",
                click -> getUI().getNavigator().navigateTo(RequestListView.VIEW_NAME));
        requestListButton.setIcon(VaadinIcons.USERS);
        addComponent(requestListButton);

        var mailButton = new Button("一括メール送信",
                click -> getUI().getNavigator().navigateTo(MailFormView.VIEW_NAME));
        mailButton.setIcon(VaadinIcons.ENVELOPES_O);
        addComponent(mailButton);

        var updateEventButton = new Button("登録済イベントの編集",
                click -> getUI().getNavigator().navigateTo(EditEventView.VIEW_NAME));
        updateEventButton.setIcon(VaadinIcons.TEXT_INPUT);
        addComponent(updateEventButton);

        var newEventButton = new Button("新規イベントの登録",
                click -> getUI().getNavigator().navigateTo(NewEventView.VIEW_NAME));
        newEventButton.setIcon(VaadinIcons.PLUS);
        addComponent(newEventButton);
    }
}
