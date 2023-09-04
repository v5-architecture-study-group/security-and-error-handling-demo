package com.example.secerrordemo.infra.session;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

class SessionStoringFilter extends OncePerRequestFilter {

    private final SessionKeyResolver sessionKeyResolver;
    private final SessionSerde sessionSerde;

    SessionStoringFilter(@Nonnull SessionKeyResolver sessionKeyResolver, @Nonnull SessionSerde sessionSerde) {
        this.sessionKeyResolver = sessionKeyResolver;
        this.sessionSerde = sessionSerde;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        sessionKeyResolver.setCurrentKey(request);
        try {
            filterChain.doFilter(request, response);
            sessionKeyResolver.storeCurrentKey(request, response);
            var session = request.getSession(false);
            if (session != null && request.isRequestedSessionIdValid() && isUIDLRequest(request)) {
                sessionSerde.serialize(session);
            }
        } finally {
            CurrentKey.setCurrent(null);
        }
    }

    private boolean isUIDLRequest(@Nonnull HttpServletRequest request) {
        return HandlerHelper.RequestType.UIDL.getIdentifier()
                .equals(request.getParameter(
                        ApplicationConstants.REQUEST_TYPE_PARAMETER));
    }
}
