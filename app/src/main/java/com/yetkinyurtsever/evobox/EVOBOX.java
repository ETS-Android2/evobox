package com.yetkinyurtsever.evobox;

import android.app.Application;

// Stores logined user's id.
public class EVOBOX extends Application {
    private int user_id;

    public int getUserID() {
        return user_id;
    }

    public void setUserID(int user_id) {
        this.user_id = user_id;
    }
}
