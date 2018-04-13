/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.component

import com.github.javatrainingcourse.obogmanager.Version
import com.vaadin.shared.Position
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme

/**
 * 成功通知。
 *
 * @author mikan
 * @since 0.1
 */
class SuccessNotification private constructor(caption: String) : Notification(caption, Notification.Type.TRAY_NOTIFICATION) {

    init {
        position = Position.TOP_CENTER
        styleName = ValoTheme.NOTIFICATION_SUCCESS
    }

    companion object {
        private const val serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID

        fun show(caption: String) {
            val notification = SuccessNotification(caption)
            notification.show(UI.getCurrent().page)
        }
    }
}
