/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.layout;

import com.github.javatrainingcourse.obogmanager.Version;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

/**
 * このアプリについて説明する画面です。
 *
 * @author mikan
 * @since 0.1
 */
public class AboutWindow extends Window {

    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;

    public AboutWindow() {
        super("このアプリについて");
        center();
        setModal(true);
        setWidth(400, Unit.PIXELS);
        setHeight(300, Unit.PIXELS);

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeFull();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(layout);

        layout.addComponent(new Label("<b><a href=\"https://github.com/JavaTrainingCourse/obog-manager\">" +
                "obog-manager</a></b> v" + Version.OBOG_MANAGER_VERSION, ContentMode.HTML));
        layout.addComponent(new Label("<b>AUTHORS</b><br/>" + String.join("<br/>", Version.INSTANCE.getAuthors()), ContentMode.HTML));
        layout.addComponent(new Label("<div align=\"center\">Powered by " + VaadinIcons.VAADIN_V.getHtml() +
                " Vaadin Framework<br/>(<a href=\"https://goo.gl/IIztDT\">紹介スライド</a>)</div>",
                ContentMode.HTML));

        Button closeButton = new Button("閉じる", e -> close());
        layout.addComponent(closeButton);
        layout.setComponentAlignment(closeButton, Alignment.BOTTOM_CENTER);
    }
}
