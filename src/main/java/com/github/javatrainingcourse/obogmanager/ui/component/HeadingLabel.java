/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.component;

import com.github.javatrainingcourse.obogmanager.Version;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * アイコン付き見出しラベル。
 *
 * @author mikan
 * @since 0.1
 */
public class HeadingLabel extends Label {

    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;

    public HeadingLabel(String text) {
        super(text);
        setStyleName(ValoTheme.LABEL_H2);
    }

    public HeadingLabel(String text, VaadinIcons icon) {
        super(icon.getHtml() + " " + text, ContentMode.HTML);
        setStyleName(ValoTheme.LABEL_H2);
    }
}
