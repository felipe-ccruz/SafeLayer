package com.felp.frontvm.session;

import com.felp.frontvm.model.UserModel;

public final class Session {

    private static UserModel current;

    private Session() {
    }

    public static void set(UserModel user) {
        current = user;
    }

    public static UserModel get() {
        return current;
    }

    public static void clear() {
        current = null;
    }

    public static Long getUserId() {
        return current == null ? null : current.getId();
    }
}
