package com.example.secerrordemo.ui.error;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;

class CustomErrorHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorHandler.class);
    private final SecureRandom rnd;

    public CustomErrorHandler() {
        try {
            this.rnd = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not initialize random number generator", ex);
        }
    }

    @Override
    public void error(ErrorEvent event) {
        var errorId = generateEntryId();
        log.error("Unhandled exception [" + errorId + "]", event.getThrowable());
        Optional.ofNullable(UI.getCurrent()).ifPresent(ui -> ui.access(() -> showNotification(errorId)));
    }

    private void showNotification(String errorId) {
        var notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        var icon = VaadinIcon.WARNING.create();
        var header = new Div(new Text("An unexpected error has occurred"));
        header.getStyle().set("font-weight", "600");
        var errorSpan = new Span(errorId);
        errorSpan.getStyle()
                .set("background-color", "var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)")
                .set("padding", "var(--lumo-space-xs)");
        var text = new Div(new Text("Please contact the administrator and give them this error ID: "), errorSpan);
        text.getStyle().set("font-size", "var(--lumo-font-size-s)");
        var info = new Div(header, text);
        var closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        var layout = new HorizontalLayout(icon, info, closeBtn);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

    private String generateEntryId() {
        var buf = new byte[8];
        rnd.nextBytes(buf);
        return HexFormat.ofDelimiter(":").withUpperCase().formatHex(buf);
    }
}
