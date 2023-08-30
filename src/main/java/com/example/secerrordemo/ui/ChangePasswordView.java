package com.example.secerrordemo.ui;

import com.example.secerrordemo.app.security.ChangePasswordService;
import com.example.secerrordemo.domain.security.ReadOnceRawPassword;
import com.example.secerrordemo.ui.io.SerializableBean;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.Nonnull;
import jakarta.annotation.security.PermitAll;

import java.util.Objects;
import java.util.Optional;

@Route(value = "changePassword", layout = MainLayout.class)
@PermitAll
public class ChangePasswordView extends VerticalLayout {

    private final SerializableBean<ChangePasswordService> changePasswordService = SerializableBean.ofType(ChangePasswordService.class);
    private final PasswordField currentPassword;
    private final PasswordField newPassword;
    private final PasswordField confirmPassword;

    public ChangePasswordView() {
        var title = new H3("Change Password");
        add(title);

        currentPassword = new PasswordField("Current password");
        newPassword = new PasswordField("New password");
        confirmPassword = new PasswordField("Repeat new password");

        add(currentPassword, newPassword, confirmPassword);

        var change = new Button("Change Password", event -> changePassword());

        add(change);
    }

    private void changePassword() {
        validateCurrentPassword().ifPresent(currentPassword -> {
            validateNewPassword().ifPresent(newPassword -> {
                clearForm();
                try {
                    changePasswordService.get().changePassword(currentPassword, newPassword);
                    Notification.show("Password successfully changed");
                } catch (Exception ex) {
                    var notification = new Notification("The password could not be changed. Please try again later");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.open();
                }
            });
        });
    }

    private void clearForm() {
        currentPassword.clear();
        currentPassword.setInvalid(false);
        newPassword.clear();
        confirmPassword.clear();
        confirmPassword.setInvalid(false);
    }

    private @Nonnull Optional<ReadOnceRawPassword> validateCurrentPassword() {
        try {
            currentPassword.setInvalid(false);
            return Optional.of(ReadOnceRawPassword.wrap(currentPassword.getValue()));
        } catch (IllegalArgumentException ex) {
            currentPassword.setErrorMessage(ex.getMessage());
            currentPassword.setInvalid(true);
            return Optional.empty();
        }
    }

    private @Nonnull Optional<ReadOnceRawPassword> validateNewPassword() {
        if (!Objects.equals(newPassword.getValue(), confirmPassword.getValue())) {
            confirmPassword.setErrorMessage("The passwords do not match");
            confirmPassword.setInvalid(true);
            return Optional.empty();
        } else {
            try {
                confirmPassword.setInvalid(false);
                return Optional.of(ReadOnceRawPassword.wrap(confirmPassword.getValue()));
            } catch (IllegalArgumentException ex) {
                confirmPassword.setErrorMessage(ex.getMessage());
                confirmPassword.setInvalid(true);
                return Optional.empty();
            }
        }
    }

    // TODO Fix this view, it is ugly!
}
