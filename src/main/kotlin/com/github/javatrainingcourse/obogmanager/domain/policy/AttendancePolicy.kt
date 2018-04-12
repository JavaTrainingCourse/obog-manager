/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.policy

/**
 * OB/OG会参加可否ポリシーを定義します。
 *
 * @author mikan
 * @since 0.1
 */
object AttendancePolicy {

    /**
     * Java, Java 8, Go の修了生か、または現行 Java 受講生かによって参加可否を決定します。
     *
     *
     * ありえない組み合わせ (Java 修了生かつ Java 受講生の場合など) が指定された場合は拒否します。
     *
     *
     * @param javaCompleted  Java 修了生
     * @param java8Completed Java 8 修了生
     * @param goCompleted    Go 修了生
     * @param incomplete     現行講習受講生
     * @return 可能な場合 `true`, それ以外の場合 `false`
     */
    fun allows(javaCompleted: Boolean, java8Completed: Boolean, goCompleted: Boolean, incomplete: Boolean): Boolean {
        if (javaCompleted && incomplete) {
            return false
        }
        return if (!javaCompleted && java8Completed) false else javaCompleted || goCompleted || incomplete
    }
}
