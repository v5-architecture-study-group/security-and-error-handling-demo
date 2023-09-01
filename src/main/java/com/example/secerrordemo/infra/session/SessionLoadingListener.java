package com.example.secerrordemo.infra.session;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

class SessionLoadingListener implements HttpSessionListener {

    private final SessionStore sessionStore;

    public SessionLoadingListener(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        var session = se.getSession();
        sessionStore.load(session.getId(), session::setAttribute);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        var session = se.getSession();
        sessionStore.delete(session.getId());
    }
}
