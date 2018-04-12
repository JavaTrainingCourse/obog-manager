/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.component

import com.github.javatrainingcourse.obogmanager.Version
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.Label
import com.vaadin.ui.themes.ValoTheme

/**
 * アイコン付き見出しラベル。
 *
 * @author mikan
 * @since 0.1
 */
class HeadingLabel : Label {

    constructor(text: String) : super(text) {
        styleName = ValoTheme.LABEL_H2
    }

    constructor(text: String, icon: VaadinIcons) : super(icon.html + " " + text, ContentMode.HTML) {
        styleName = ValoTheme.LABEL_H2
    }

    companion object {
        private val serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID
    }
}
