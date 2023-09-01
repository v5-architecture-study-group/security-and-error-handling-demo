package com.example.secerrordemo.ui;

import com.example.secerrordemo.infra.security.Roles;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "", layout = MainLayout.class)
@RolesAllowed(Roles.ROLE_USER)
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("This is the main view"));
        add(new Button("Throw an unhandled exception", event -> {
            throw new IllegalStateException("This is an unhandled exception!");
        }));
    }
}
