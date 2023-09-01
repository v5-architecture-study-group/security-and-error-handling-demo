package com.example.secerrordemo.ui.error;

import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ErrorConfig {

    @Bean
    CustomErrorHandler customErrorHandler() {
        return new CustomErrorHandler();
    }

    @Bean
    VaadinServiceInitListener vaadinServiceInitListener() {
        return serviceInitEvent -> serviceInitEvent.getSource().addSessionInitListener(sessionInitEvent -> sessionInitEvent.getSession().setErrorHandler(customErrorHandler()));
    }
}
