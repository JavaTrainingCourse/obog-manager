/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui

import com.github.javatrainingcourse.obogmanager.Version
import com.github.javatrainingcourse.obogmanager.ui.view.ErrorView
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.annotations.Viewport
import com.vaadin.server.*
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.spring.annotation.SpringViewDisplay
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme

/**
 * Vaadin UI を定義します。

 * @author mikan
 * *
 * @since 0.1
 */
@SpringUI
@SpringViewDisplay
@Theme(ValoTheme.THEME_NAME)
@Title("Java研修 Go研修 OB・OG会")
@Viewport("user-scalable=no,width=600")
class MainUI : UI() {

    override fun init(request: VaadinRequest) {
        reconnectDialogConfiguration.dialogText = "サーバーとの接続が切れました。再接続しています..."
        if (VaadinService.getCurrent() != null) {
            VaadinService.getCurrent().systemMessagesProvider = JapaneseSystemMessageProvider()
        }
        navigator.setErrorView(ErrorView::class.java)
    }

    private class JapaneseSystemMessageProvider internal constructor() : SystemMessagesProvider {
        private val messages = CustomizedSystemMessages()
        private val message1 = "保存していないデータがあれば書き留めた上で、"
        private val message2 = "<u>ここをクリック</u>するか ESC キーを押して続行してください。"

        init {
            messages.sessionExpiredCaption = "セッションが切れました"
            messages.sessionExpiredMessage = message1 + message2
            messages.isSessionExpiredNotificationEnabled = true
            messages.communicationErrorCaption = "通信に失敗しました"
            messages.communicationErrorMessage = message1 + message2
            messages.isCommunicationErrorNotificationEnabled = true
            messages.authenticationErrorCaption = "認証に失敗しました"
            messages.authenticationErrorMessage = message1 + message2
            messages.isAuthenticationErrorNotificationEnabled = true
            messages.internalErrorCaption = "内部エラーが発生しました"
            messages.internalErrorMessage = message1 + message2
            messages.isInternalErrorNotificationEnabled = true
            messages.cookiesDisabledCaption = "Cookie が無効です"
            messages.cookiesDisabledMessage = "このアプリケーションは Cookie 機能を利用しています。<br/>" +
                    "ブラウザで Cookie を有効にした後で、" + message2
            messages.isCookiesDisabledNotificationEnabled = true
        }

        override fun getSystemMessages(systemMessagesInfo: SystemMessagesInfo): SystemMessages {
            return messages
        }

        companion object {
            private const val serialVersionUID: Long = Version.OBOG_MANAGER_SERIAL_VERSION_UID
        }
    }

    companion object {
        const val FIELD_WIDTH_WIDE = 300
        const val FIELD_WIDTH_SHORT = 50
        private const val serialVersionUID: Long = Version.OBOG_MANAGER_SERIAL_VERSION_UID
    }
}
