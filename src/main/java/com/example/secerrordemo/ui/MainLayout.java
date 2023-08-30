package com.example.secerrordemo.ui;

import com.example.secerrordemo.app.security.CurrentUser;
import com.example.secerrordemo.ui.io.SerializableBean;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    private final SerializableBean<AuthenticationContext> authenticationContext = SerializableBean.ofType(AuthenticationContext.class);
    private final SerializableBean<CurrentUser> currentUser = SerializableBean.ofType(CurrentUser.class);

    public MainLayout() {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        var title = new H1("Security and Error Handling Demo");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        header.add(title);

        var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);

        var avatar = new Avatar(currentUser.get().username().toString());
        var userMenu = menuBar.addItem(avatar).getSubMenu();

        currentUser.get().passwordExpiresIn().map(expires -> {
            var div = new Div(new Text("Password expires in %d days".formatted(expires.toDays())));
            div.addClassName(LumoUtility.Padding.Horizontal.LARGE);
            return div;
        }).ifPresent(userMenu::add);

        userMenu.addItem("Change Password", evt -> getUI().ifPresent(ui -> ui.navigate(ChangePasswordView.class)));
        userMenu.add(new Hr());
        userMenu.addItem("Logout", evt -> authenticationContext.get().logout()); // TODO Logout does not work!

        header.add(menuBar);

        addToNavbar(new DrawerToggle(), header);

        var nav = new SideNav();
        nav.addItem(new SideNavItem("Main View", MainView.class));
        nav.addItem(new SideNavItem("Admin View", AdminView.class));
        addToDrawer(nav);
    }
}
