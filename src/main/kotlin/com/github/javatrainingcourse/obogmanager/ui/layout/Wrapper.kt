/*
 * Copyright (c) 2017-2018 mikan
 */

package com.github.javatrainingcourse.obogmanager.ui.layout

import com.github.javatrainingcourse.obogmanager.Version
import com.github.javatrainingcourse.obogmanager.domain.model.Membership
import com.github.javatrainingcourse.obogmanager.domain.service.MembershipService
import com.github.javatrainingcourse.obogmanager.ui.view.LoginView
import com.github.javatrainingcourse.obogmanager.ui.view.LogoutConfirmView
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

/**
 * 各 [com.vaadin.navigator.View] に共通の表示を含むラッパーを提供します。
 *
 * @author mikan
 * @since 0.1
 */
open class Wrapper : VerticalLayout() {

    protected val isLoggedIn: Boolean
        get() = MembershipService.getCurrentMembership() != null

    protected val isAdminLoggedIn: Boolean
        get() {
            val membership = membership
            return membership != null && membership.isAdmin()
        }

    protected val membership: Membership?
        get() = MembershipService.getCurrentMembership()

    init {
        val titleLabel = Label("Java研修 Go研修 OB/OG会")
        titleLabel.styleName = ValoTheme.LABEL_H2
        addComponent(titleLabel)
        setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER)

        val membership = MembershipService.getCurrentMembership()
        if (membership != null) {
            val userInfoArea = HorizontalLayout()
            userInfoArea.isSpacing = true
            addComponent(userInfoArea)

            val userLabel = Label("ようこそ! " + membership.getName() + " さん")
            userInfoArea.addComponent(userLabel)
            userInfoArea.setComponentAlignment(userLabel, Alignment.MIDDLE_LEFT)

            val logoutButton = Button("ログアウト"
            ) { _ -> ui.navigator.navigateTo(LogoutConfirmView.VIEW_NAME) }
            logoutButton.styleName = ValoTheme.BUTTON_SMALL
            logoutButton.icon = VaadinIcons.SIGN_OUT
            userInfoArea.addComponent(logoutButton)
            userInfoArea.setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT)
        } else {
            val userInfoArea = HorizontalLayout()
            userInfoArea.isSpacing = true
            addComponent(userInfoArea)

            val logoutButton = Button("会員ログイン"
            ) { _ -> ui.navigator.navigateTo(LoginView.VIEW_NAME) }
            logoutButton.styleName = ValoTheme.BUTTON_SMALL
            logoutButton.icon = VaadinIcons.USER
            userInfoArea.addComponent(logoutButton)
            userInfoArea.setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT)
        }
    }

    companion object {
        private const val serialVersionUID = Version.OBOG_MANAGER_SERIAL_VERSION_UID
    }
}
