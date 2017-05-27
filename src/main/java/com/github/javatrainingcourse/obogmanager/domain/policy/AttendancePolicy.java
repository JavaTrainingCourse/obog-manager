/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.policy;

/**
 * OB/OG会参加可否ポリシーを定義します。
 *
 * @author mikan
 * @since 0.1
 */
public final class AttendancePolicy {

    private AttendancePolicy() {
    }

    /**
     * Java, Java 8, Go の修了生か、または現行 Java 受講生かによって参加可否を決定します。
     * <p>
     * ありえない組み合わせ (Java 修了生かつ Java 受講生の場合など) が指定された場合は拒否します。
     * </p>
     *
     * @param javaCompleted  Java 修了生
     * @param java8Completed Java 8 修了生
     * @param goCompleted    Go 修了生
     * @param javaIncomplete 現行 Java 受講生
     * @return 可能な場合 {@code true}, それ以外の場合 {@code false}
     */
    public static boolean allows(boolean javaCompleted, boolean java8Completed, boolean goCompleted,
                                 boolean javaIncomplete) {
        if (javaCompleted && javaIncomplete) {
            return false;
        }
        if ((!javaCompleted) && java8Completed) {
            return false;
        }
        return javaCompleted || goCompleted || javaIncomplete;
    }
}
