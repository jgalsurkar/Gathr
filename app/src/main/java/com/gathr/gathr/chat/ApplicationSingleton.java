package com.gathr.gathr.chat;

import android.app.Application;
import com.quickblox.users.model.QBUser;

public class ApplicationSingleton extends Application {

    private QBUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public QBUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(QBUser currentUser) {
        this.currentUser = currentUser;
    }


}
