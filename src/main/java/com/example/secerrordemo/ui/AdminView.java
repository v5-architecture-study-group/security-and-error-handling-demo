package com.example.secerrordemo.ui;

import com.example.secerrordemo.domain.security.Roles;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminView extends VerticalLayout {

    public AdminView() {
        add(new H1("This is the admin view."));
        // TODO Add server push demo
    }
}
