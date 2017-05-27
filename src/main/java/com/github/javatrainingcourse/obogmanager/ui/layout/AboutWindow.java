/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.layout;

import com.github.javatrainingcourse.obogmanager.App;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

/**
 * @author mikan
 * @since 0.1
 */
public class AboutWindow extends Window {

    public AboutWindow() {
        super("このアプリについて");
        center();
        setModal(true);
        setWidth(400, Unit.PIXELS);
        setHeight(300, Unit.PIXELS);

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(layout);

        Label descriptionLabel = new Label("<b><a href=\"https://github.com/JavaTrainingCourse/obog-manager\">" +
                "obog-manager</a></b> v" + App.OBOG_MANAGER_VERSION, ContentMode.HTML);
        layout.addComponent(descriptionLabel);

        Label authorsLabel = new Label("<b>AUTHORS</b><br/>" + String.join("<br/>", App.AUTHORS), ContentMode.HTML);
        layout.addComponent(authorsLabel);

        Label presentationLinkLabel = new Label("Powered by Vaadin Framework " +
                "(<a href=\"https://goo.gl/IIztDT\">紹介スライド</a>)", ContentMode.HTML);
        layout.addComponent(presentationLinkLabel);

        Button closeButton = new Button("閉じる", e -> close());
        layout.addComponent(closeButton);
        layout.setComponentAlignment(closeButton, Alignment.BOTTOM_CENTER);
    }
}
