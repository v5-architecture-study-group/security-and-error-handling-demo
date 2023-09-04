package com.example.secerrordemo.ui;

import com.example.secerrordemo.app.demo.TimeService;
import com.example.secerrordemo.infra.security.Roles;
import com.example.secerrordemo.ui.io.SerializableBean;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.time.Instant;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminView extends VerticalLayout {

    private final SerializableBean<TimeService> timeService = SerializableBean.ofType(TimeService.class);
    private final Span timeSpan = new Span();
    @SuppressWarnings("FieldCanBeLocal")
    private final SerializableConsumer<Instant> onTimeReceived = time -> getUI().ifPresent(ui -> ui.access(() -> timeSpan.setText(time.toString())));
    private transient TimeService.Subscription timeSubscription;

    public AdminView() {
        add(new H1("This is the admin view."));
        add(timeSpan);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        subscribeToTimeService();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        unsubscribe();
    }

    private void subscribeToTimeService() {
        unsubscribe();
        timeSubscription = timeService.get().subscribeToTimeUpdates(onTimeReceived);
    }

    private void unsubscribe() {
        if (timeSubscription != null) {
            timeSubscription.unsubscribe();
            timeSubscription = null;
        }
    }

    @Serial
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        if (in.readBoolean()) {
            subscribeToTimeService();
        }
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeBoolean(timeSubscription != null);
    }
}
