package com.example.secerrordemo.ui;

import com.example.secerrordemo.app.demo.TimeService;
import com.example.secerrordemo.infra.security.Roles;
import com.example.secerrordemo.ui.io.SerializableBean;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.Instant;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminView extends VerticalLayout {

    private final SerializableBean<TimeService> timeService = SerializableBean.ofType(TimeService.class);
    private final Span timeSpan = new Span();

    private final SerializableConsumer<Instant> onTimeReceived = time -> getUI().ifPresent(ui -> ui.access(() -> timeSpan.setText(time.toString())));

    public AdminView() {
        add(new H1("This is the admin view."));
        add(timeSpan);
        // TODO What happens with this after deserialization?
        timeService.get().subscribeToTimeUpdates(onTimeReceived);
    }
}
