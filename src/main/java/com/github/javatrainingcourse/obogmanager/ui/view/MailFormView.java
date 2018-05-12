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
import com.github.javatrainingcourse.obogmanager.domain.service.MailService;
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.component.HeadingLabel;
import com.github.javatrainingcourse.obogmanager.ui.component.SuccessNotification;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 会員または参加者にメールを送信する画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = MailFormView.VIEW_NAME)
public class MailFormView extends Wrapper implements View {

    static final String VIEW_NAME = "mail";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private transient final MembershipService membershipService;
    private transient final ConvocationService convocationService;
    private transient final AttendanceService attendanceService;
    private transient final MailService mailService;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.reply}")
    private String appReply;

    @Autowired
    public MailFormView(MembershipService membershipService, ConvocationService convocationService,
                        AttendanceService attendanceService, MailService mailService) {
        this.membershipService = membershipService;
        this.convocationService = convocationService;
        this.attendanceService = attendanceService;
        this.mailService = mailService;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isAdminLoggedIn()) {
            ErrorView.show("管理者ユーザーでのログインが必要です。", null);
            return;
        }
        addComponent(new HeadingLabel("一括メール送信", VaadinIcons.ENVELOPES_O));

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
        var allMembersItem = "全会員";
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
                getUI().getNavigator().navigateTo(MailFormView.VIEW_NAME);
            } else {
                long id = convocations.get(selections.indexOf(e.getValue()) - 1).getId();
                getUI().getNavigator().navigateTo(MailFormView.VIEW_NAME + "/" + id);
            }
        });
        comboBoxArea.addComponent(convocationComboBox);

        List<Membership> recipients;
        if (cId == -1) {
            try {
                recipients = membershipService.getAll();
            } catch (RuntimeException e) {
                ErrorView.show("会員一覧の取得に失敗しました。", e);
                return;
            }
        } else {
            var convocation = convocations.stream().filter(c -> c.getId() == cId).findAny().orElse(null);
            if (convocation == null) {
                ErrorView.show("指定されたイベント招集が見つかりません: " + cId, null);
                return;
            }
            try {
                recipients = attendanceService.getResponses(convocation).stream()
                        .filter(Attendance::isAttend)
                        .map(Attendance::getMembership)
                        .collect(Collectors.toList());
            } catch (RuntimeException e) {
                ErrorView.show("申込一覧の取得に失敗しました。", e);
                return;
            }
        }
        if (recipients.size() == 0) {
            addComponent(new Label(VaadinIcons.WARNING.getHtml() + " 送信対象者が一人もいません。", ContentMode.HTML));
            var homeButton = new Button("会員メニュー", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
            homeButton.setIcon(VaadinIcons.USER);
            addComponent(homeButton);
            setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
        } else {
            printForm(recipients);
        }
    }

    private void printForm(List<Membership> recipients) {
        addComponent(new Label("宛先を確認し、件名と内容を記入してください。"));
        addComponent(new Label("メールは BCC で一括送信されます。"));

        var form = new FormLayout();
        form.setMargin(false);
        addComponent(form);

        var recipientsCountLabel = new Label(recipients.size() + " 名");
        recipientsCountLabel.setCaption("宛先数");
        form.addComponent(recipientsCountLabel);

        var recipientsLabel = new Label(recipients.stream()
                .map(m -> m.getName() + " <" + m.getEmail() + ">")
                .collect(Collectors.joining(", ")));
        recipientsLabel.setCaption("宛先一覧");
        recipientsLabel.setSizeFull();
        form.addComponent(recipientsLabel);

        var subjectField = new TextField("件名");
        subjectField.setWidth(MainUI.FIELD_WIDTH_WIDE, Unit.PIXELS);
        subjectField.setValue("【お知らせ】Java研修 Go研修 OB・OG会");
        form.addComponent(subjectField);

        var textArea = new TextArea("本文");
        textArea.setWidth(100, Unit.PERCENTAGE);
        textArea.setHeight(400, Unit.PIXELS);
        textArea.setValue("Java研修 Go研修 OB・OG会員各位\n\n" +
                "幹事の●●よりお知らせです。\n\n" +
                "詳細の確認・登録内容の変更は以下 URL より行ってください。\n" +
                appUrl + "\n\n" +
                "本メールに関するお問合せ先: " + appReply + "\n" +
                "Java研修 Go研修 OB・OG会");
        form.addComponent(textArea);

        var buttonArea = new HorizontalLayout();
        buttonArea.setSpacing(true);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        var homeButton = new Button("戻る", click -> getUI().getNavigator().navigateTo(MenuView.VIEW_NAME));
        homeButton.setIcon(VaadinIcons.USER);
        buttonArea.addComponent(homeButton);

        var sendButton = new Button("送信", click -> {
            if (subjectField.getValue().isEmpty()) {
                Notification.show("件名を入力してください", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (textArea.getValue().isEmpty()) {
                Notification.show("件名を入力してください", Notification.Type.WARNING_MESSAGE);
                return;
            }
            try {
                mailService.sendMailAsBcc(recipients.stream().map(Membership::getEmail).collect(Collectors.toList()),
                        subjectField.getValue(), textArea.getValue());
                getUI().getNavigator().navigateTo(MenuView.VIEW_NAME);
                SuccessNotification.show("メール送信に成功しました");
            } catch (Exception e) {
                ErrorView.show("メール送信に失敗しました。", e);
            }
        });
        sendButton.setIcon(VaadinIcons.BOLT);
        sendButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonArea.addComponent(sendButton);
    }
}
