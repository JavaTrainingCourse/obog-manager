/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.view;

import com.github.javatrainingcourse.obogmanager.Version;
import com.github.javatrainingcourse.obogmanager.ui.layout.Wrapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * エラー画面です。
 *
 * @author mikan
 * @since 0.1
 */
@UIScope
@SpringView(name = ErrorView.VIEW_NAME)
public class ErrorView extends Wrapper implements View {

    public static final String VIEW_NAME = "error";
    private static final long serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID;
    private static final String PARAM_MESSAGE = "error.message";
    private static final String PARAM_THROWABLE = "error.throwable";
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ErrorView.class);

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
            addComponent(new Label(paramMessage));
        }
        session.setAttribute(PARAM_MESSAGE, null);
        Throwable paramThrowable = (Throwable) session.getAttribute(PARAM_THROWABLE);
        if (paramThrowable != null) {
            addComponent(new Label(throwable2html(paramThrowable), ContentMode.HTML));
        }
        session.setAttribute(PARAM_THROWABLE, null);
        log.error(paramMessage, paramThrowable);

        if (paramThrowable instanceof AuthenticationException) {
            Button loginButton = new Button("ログイン", click -> getUI().getNavigator().navigateTo(LoginView.VIEW_NAME));
            addComponent(loginButton);
            setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
        }

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
