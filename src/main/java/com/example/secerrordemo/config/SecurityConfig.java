package com.example.secerrordemo.config;

import com.example.secerrordemo.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@EnableWebSecurity
@Configuration
class SecurityConfig extends VaadinWebSecurity {

    private static final int MAXIMUM_SESSIONS_PER_USER = 1;
    private final FindByIndexNameSessionRepository<?> sessionRepository;

    SecurityConfig(FindByIndexNameSessionRepository<?> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.sessionManagement(sessionManagement -> {
            // TODO Continue here
            sessionManagement.maximumSessions(MAXIMUM_SESSIONS_PER_USER).sessionRegistry(sessionRegistry());
        });
        setLoginView(http, LoginView.class);
    }

    @Bean
    SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }

    @Override
    protected void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**")); // TODO For development only!
    }
}
