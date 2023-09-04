package com.example.secerrordemo.infra.session;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SessionConfig {

    @Bean
    FilterRegistrationBean<SessionStoringFilter> sessionStoringFilter(SessionKeyResolver sessionKeyResolver, SessionSerde sessionSerde) {
        var registration = new FilterRegistrationBean<SessionStoringFilter>();
        registration.setFilter(new SessionStoringFilter(sessionKeyResolver, sessionSerde));
        registration.addUrlPatterns("/*");
        registration.setOrder(-200); // Spring security filter chain is -100
        return registration;
    }

    @Bean
    SessionLoadingListener sessionLoadingListener(SessionSerde sessionSerde) {
        return new SessionLoadingListener(sessionSerde);
    }

    @Bean
    SessionKeyResolver sessionKeyResolver(ApplicationEventPublisher applicationEventPublisher) {
        return new SessionKeyResolver(applicationEventPublisher);
    }

    @Bean
    SessionSerde sessionSerde(SessionKeyResolver sessionKeyResolver, SessionStore sessionStore) {
        return new SessionSerde(sessionKeyResolver, sessionStore);
    }
}
