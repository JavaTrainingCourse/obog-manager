/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.layout

import com.github.javatrainingcourse.obogmanager.Version
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.Sizeable
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.*

/**
 * このアプリについて説明する画面です。
 *
 * @author mikan
 * @since 0.1
 */
class AboutWindow : Window("このアプリについて") {
    init {
        center()
        isModal = true
        setWidth(400f, Sizeable.Unit.PIXELS)
        setHeight(300f, Sizeable.Unit.PIXELS)

        val layout = VerticalLayout()
        layout.isSpacing = true
        layout.setSizeFull()
        layout.defaultComponentAlignment = Alignment.MIDDLE_CENTER
        content = layout

        layout.addComponent(Label("<b><a href=\"https://github.com/JavaTrainingCourse/obog-manager\">" +
                "obog-manager</a></b> v" + Version.OBOG_MANAGER_VERSION, ContentMode.HTML))
        layout.addComponent(Label("<b>AUTHORS</b><br/>" + Version.authors.joinToString("<br/>"), ContentMode.HTML))
        layout.addComponent(Label("<div align=\"center\">Powered by " + VaadinIcons.VAADIN_V.html +
                " Vaadin Framework<br/>(<a href=\"https://goo.gl/IIztDT\">紹介スライド</a>)</div>",
                ContentMode.HTML))

        val closeButton = Button("閉じる") { _ -> close() }
        layout.addComponent(closeButton)
        layout.setComponentAlignment(closeButton, Alignment.BOTTOM_CENTER)
    }

    companion object {
        private const val serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID
    }
}
