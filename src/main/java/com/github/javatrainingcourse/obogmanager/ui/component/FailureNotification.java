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
public class FailureNotification extends Notification {

    private static final long serialVersionUID = Version.INSTANCE.getOBOG_MANAGER_SERIAL_VERSION_UID();

    private FailureNotification(String caption) {
        super(caption, Type.TRAY_NOTIFICATION);
        setPosition(Position.TOP_CENTER);
        setStyleName(ValoTheme.NOTIFICATION_FAILURE);
        setDelayMsec(-1);
    }

    public static void show(String caption) {
        new FailureNotification(caption).show(UI.getCurrent().getPage());
    }
}
