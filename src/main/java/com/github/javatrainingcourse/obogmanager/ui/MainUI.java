/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui;

import com.github.javatrainingcourse.obogmanager.App;
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
@Viewport("user-scalable=no,width=500")
public class MainUI extends UI {

    public static final int FIELD_WIDTH_WIDE = 300;
    public static final int FIELD_WIDTH_SHORT = 50;
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;

    @Override
    protected void init(VaadinRequest request) {
        getReconnectDialogConfiguration().setDialogText("サーバーとの接続が切れました。再接続しています...");
        if (VaadinService.getCurrent() != null) {
            VaadinService.getCurrent().setSystemMessagesProvider(new JapaneseSystemMessageProvider());
        }
        getNavigator().setErrorView(ErrorView.class);
    }

    private static class JapaneseSystemMessageProvider implements SystemMessagesProvider {

        private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
        private static final CustomizedSystemMessages MESSAGES = new CustomizedSystemMessages();
        private static final String MSG1 = "保存していないデータがあれば書き留めた上で、";
        private static final String MSG2 = "<u>ここをクリック</u>するか ESC キーを押して続行してください。";

        static {
            MESSAGES.setSessionExpiredCaption("セッションが切れました");
            MESSAGES.setSessionExpiredMessage(MSG1 + MSG2);
            MESSAGES.setSessionExpiredNotificationEnabled(true);
            MESSAGES.setCommunicationErrorCaption("通信に失敗しました");
            MESSAGES.setCommunicationErrorMessage(MSG1 + MSG2);
            MESSAGES.setCommunicationErrorNotificationEnabled(true);
            MESSAGES.setAuthenticationErrorCaption("認証に失敗しました");
            MESSAGES.setAuthenticationErrorMessage(MSG1 + MSG2);
            MESSAGES.setAuthenticationErrorNotificationEnabled(true);
            MESSAGES.setInternalErrorCaption("内部エラーが発生しました");
            MESSAGES.setInternalErrorMessage(MSG1 + MSG2);
            MESSAGES.setInternalErrorNotificationEnabled(true);
            MESSAGES.setCookiesDisabledCaption("Cookie が無効です");
            MESSAGES.setCookiesDisabledMessage("このアプリケーションは Cookie 機能を利用しています。<br/>" +
                    "ブラウザで Cookie を有効にした後で、" + MSG2);
            MESSAGES.setCookiesDisabledNotificationEnabled(true);
        }

        @Override
        public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
            return MESSAGES;
        }
    }
}
