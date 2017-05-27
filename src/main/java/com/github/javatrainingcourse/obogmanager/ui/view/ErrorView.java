/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.App;
import com.github.javatrainingcourse.obogmanager.ui.MainUI;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * エラー画面です。
 *
 * @author mikan
 * @since 0.1
 */
@SpringView(name = ErrorView.VIEW_NAME, ui = MainUI.class)
@Slf4j
public class ErrorView extends Wrapper implements View {

    public static final String VIEW_NAME = "error";
    private static final long serialVersionUID = App.OBOG_MANAGER_SERIAL_VERSION_UID;
    private static final String PARAM_MESSAGE = "error.message";
    private static final String PARAM_THROWABLE = "error.throwable";

    static void show(String message, Throwable throwable) {
        VaadinSession session = VaadinSession.getCurrent();
        session.setAttribute(ErrorView.PARAM_MESSAGE, message);
        session.setAttribute(ErrorView.PARAM_THROWABLE, throwable);
        UI.getCurrent().getNavigator().navigateTo(ErrorView.VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Label errorLabel = new Label("エラーが発生しました。");
        errorLabel.setStyleName(ValoTheme.LABEL_FAILURE);

        VaadinSession session = VaadinSession.getCurrent();
        String paramMessage = (String) session.getAttribute(PARAM_MESSAGE);
        if (paramMessage != null) {
            Label paramMessageLabel = new Label(paramMessage);
            addComponent(paramMessageLabel);
        }
        session.setAttribute(PARAM_MESSAGE, null);
        Throwable paramThrowable = (Throwable) session.getAttribute(PARAM_THROWABLE);
        if (paramThrowable != null) {
            Label paramThrowableLabel = new Label(throwable2html(paramThrowable), ContentMode.HTML);
            addComponent(paramThrowableLabel);
        }
        session.setAttribute(PARAM_THROWABLE, null);
        log.error(paramMessage, paramThrowable);

        Button homeButton = new Button("ホーム", click -> getUI().getNavigator().navigateTo(FrontView.VIEW_NAME));
        addComponent(homeButton);
        setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
    }

    private String throwable2html(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return "<small><pre>" + stringWriter.toString() + "</pre></small>";
    }
}
