package com.example.secerrordemo.infra.session;

import com.example.secerrordemo.infra.spring.ApplicationContextHolder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.communication.AtmospherePushConnection;
import com.vaadin.flow.server.communication.PushConnection;
import com.vaadin.flow.server.communication.PushConnectionFactory;
import jakarta.annotation.Nonnull;
import org.slf4j.LoggerFactory;


class SessionStoringPushConnection extends AtmospherePushConnection {

    SessionStoringPushConnection(@Nonnull UI ui) {
        super(ui);
    }

    @Override
    protected void sendMessage(String message) {
        super.sendMessage(message);
        var session = getResource().session(false);
        if (session != null) {
            ApplicationContextHolder.getApplicationContext().getBean(SessionSerde.class).serialize(session);
        } else {
            LoggerFactory.getLogger(SessionStoringPushConnection.class).warn("Sent message without a session");
        }
    }

    public static class Factory implements PushConnectionFactory {

        @Override
        public PushConnection apply(UI ui) {
            return new SessionStoringPushConnection(ui);
        }
    }
}
