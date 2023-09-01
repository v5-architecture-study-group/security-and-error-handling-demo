package com.example.secerrordemo.infra.session;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SessionConfig {

    // TODO Add code for loading the session if not found

    // TODO Add code for deleting the session when invalidated

    @Bean
    FilterRegistrationBean<SessionStoringFilter> sessionStoringFilter(SessionStore sessionStore) {
        var registration = new FilterRegistrationBean<SessionStoringFilter>();
        registration.setFilter(new SessionStoringFilter(sessionStore));
        registration.addUrlPatterns("/*");
        registration.setOrder(-200); // Spring security filter chain is -100
        return registration;
    }

    @Bean
    SessionLoadingListener sessionLoadingListener(SessionStore sessionStore) {
        return new SessionLoadingListener(sessionStore);
    }
}
