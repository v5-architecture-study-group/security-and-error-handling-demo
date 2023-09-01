package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SessionStoringFilter extends OncePerRequestFilter {

    private final SessionStore sessionStore;

    public SessionStoringFilter(@Nonnull SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            var session = request.getSession(false);
            if (session != null) {
                sessionStore.save(session.getId(), sink -> session.getAttributeNames().asIterator().forEachRemaining(name -> sink.write(name, session.getAttribute(name))));
            }
        }
    }
}
