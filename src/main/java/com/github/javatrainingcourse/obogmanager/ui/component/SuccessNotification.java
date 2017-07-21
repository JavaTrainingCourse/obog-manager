/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.component;

import com.github.javatrainingcourse.obogmanager.Version;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 成功通知。
 *
 * @author mikan
 * @since 0.1
 */
public class SuccessNotification extends Notification {

    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;

    private SuccessNotification(String caption) {
        super(caption, Type.TRAY_NOTIFICATION);
        setPosition(Position.TOP_CENTER);
        setStyleName(ValoTheme.NOTIFICATION_SUCCESS);
    }

    public static void show(String caption) {
        new SuccessNotification(caption).show(UI.getCurrent().getPage());
    }
}
