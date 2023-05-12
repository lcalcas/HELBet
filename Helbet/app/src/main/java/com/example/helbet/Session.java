package com.example.helbet;

public class Session {
    private static Session singleton;
    private User currentUser;
    private UpdateTimer lastUpdate;

    private Session() {}

    public synchronized static Session getInstance() {
        if (singleton == null) {
            singleton = new Session();
        }

        return singleton;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
