package com.example.secerrordemo.infra.security;

import com.example.secerrordemo.infra.session.SessionConstants;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
class SecurityConfig extends VaadinWebSecurity {

    private static final int MAXIMUM_SESSIONS_PER_USER = 1;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement(sessionManagement -> {
            sessionManagement.sessionFixation().newSession();
            sessionManagement.maximumSessions(MAXIMUM_SESSIONS_PER_USER).sessionRegistry(sessionRegistry()).expiredUrl(LoginController.LOGIN_FORM_URL);
        });
        setLoginView(http, LoginController.LOGIN_FORM_URL, LoginController.LOGOUT_SUCCESS_URL);

        // Although one would think setLoginView should take care of it, it apparently does not, and we have to permit access to these URLs:
        http.formLogin(formLogin -> formLogin.loginProcessingUrl(LoginController.LOGIN_PROCESSING_URL).permitAll());
        http.logout(logout -> {
            logout.deleteCookies("JSESSIONID", SessionConstants.COOKIE_NAME);
            logout.logoutSuccessUrl(LoginController.LOGOUT_SUCCESS_URL).permitAll();
        });

        // Allow access to static resources used by the login page, and to the health endpoint:
        http.authorizeHttpRequests(requests -> requests.requestMatchers(new AntPathRequestMatcher("/images/**"), new AntPathRequestMatcher("/css/**"), new AntPathRequestMatcher("/actuator/health/**")).permitAll());
        super.configure(http);
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
