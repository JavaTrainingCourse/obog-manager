/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.ui.view.ErrorView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Vaadin UI を定義します。
 *
 * @author mikan
 * @since 0.1
 */
@SpringUI
@SpringViewDisplay
@Theme(ValoTheme.THEME_NAME)
@Title("Java研修 Go研修 OB・OG会")
@Viewport("user-scalable=no,width=600")
public class MainUI extends UI {

    public static final int FIELD_WIDTH_WIDE = 300;
    public static final int FIELD_WIDTH_SHORT = 50;
    private static final long serialVersionUID = Version.INSTANCE.getOBOG_MANAGER_SERIAL_VERSION_UID();
    private static final SystemMessagesProvider SYSTEM_MESSAGES = new JapaneseSystemMessageProvider();

    @Override
    protected void init(VaadinRequest request) {
        getReconnectDialogConfiguration().setDialogText("サーバーとの接続が切れました。再接続しています...");
        if (VaadinService.getCurrent() != null) {
            VaadinService.getCurrent().setSystemMessagesProvider(SYSTEM_MESSAGES);
        }
        getNavigator().setErrorView(ErrorView.class);
    }

    private static class JapaneseSystemMessageProvider implements SystemMessagesProvider {

        private static final long serialVersionUID = Version.INSTANCE.getOBOG_MANAGER_SERIAL_VERSION_UID();
        private final CustomizedSystemMessages messages = new CustomizedSystemMessages();
        private final String message1 = "保存していないデータがあれば書き留めた上で、";
        private final String message2 = "<u>ここをクリック</u>するか ESC キーを押して続行してください。";

        JapaneseSystemMessageProvider() {
            messages.setSessionExpiredCaption("セッションが切れました");
            messages.setSessionExpiredMessage(message1 + message2);
            messages.setSessionExpiredNotificationEnabled(true);
            messages.setCommunicationErrorCaption("通信に失敗しました");
            messages.setCommunicationErrorMessage(message1 + message2);
            messages.setCommunicationErrorNotificationEnabled(true);
            messages.setAuthenticationErrorCaption("認証に失敗しました");
            messages.setAuthenticationErrorMessage(message1 + message2);
            messages.setAuthenticationErrorNotificationEnabled(true);
            messages.setInternalErrorCaption("内部エラーが発生しました");
            messages.setInternalErrorMessage(message1 + message2);
            messages.setInternalErrorNotificationEnabled(true);
            messages.setCookiesDisabledCaption("Cookie が無効です");
            messages.setCookiesDisabledMessage("このアプリケーションは Cookie 機能を利用しています。<br/>" +
                    "ブラウザで Cookie を有効にした後で、" + message2);
            messages.setCookiesDisabledNotificationEnabled(true);
        }

        @Override
        public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
            return messages;
        }
    }
}
