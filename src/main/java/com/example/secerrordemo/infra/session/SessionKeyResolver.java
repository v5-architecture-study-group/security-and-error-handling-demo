package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.web.session.HttpSessionIdChangedEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

final class SessionKeyResolver {

    private static final Logger log = LoggerFactory.getLogger(SessionKeyResolver.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    SessionKeyResolver(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setCurrentKey(@Nonnull HttpServletRequest request) {
        var cookie = getCookie(request);
        if (cookie.isPresent()) {
            log.trace("Found session key cookie");
            CurrentKey.setCurrent(SessionKey.fromString(cookie.get().getValue()));
            // If we don't have a session at this stage, we can assume that we have just been switched over to
            // another server instance by the load balancer. To prevent session authentication errors, we have to
            // create a new session here so that the SessionLoadingListener can populate it before we move on.
            var oldId = request.getRequestedSessionId();
            var session = request.getSession(true);
            if (!Objects.equals(oldId, session.getId())) {
                // This is in practice what we have done in this case, so let's fire an event to get the audit logging
                // straight.
                applicationEventPublisher.publishEvent(new HttpSessionIdChangedEvent(session, oldId));
            }
        } else {
            log.trace("No session key cookie present, generating new session key");
            CurrentKey.setCurrent(SessionKey.randomKey());
        }
        request.setAttribute(SessionConstants.COOKIE_NAME, CurrentKey.current().orElseThrow());
    }

    public void storeCurrentKey(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        var key = (SessionKey) request.getAttribute(SessionConstants.COOKIE_NAME);
        if (key != null) {
            log.trace("Setting session key cookie for {}", key);
            response.addCookie(createCookie(key));
            var session = request.getSession(false);
            if (session != null) {
                session.setAttribute(SessionConstants.COOKIE_NAME, key);
            }
        }
    }

    public @Nonnull Optional<SessionKey> getCurrentKey(@Nonnull HttpSession session) {
        var key = (SessionKey) session.getAttribute(SessionConstants.COOKIE_NAME);
        if (key != null) {
            log.trace("Found session key in HTTP session {}", session.getId());
            return Optional.of(key);
        } else {
            log.trace("Found no session key in HTTP session {}, resorting to the current key", session.getId());
            return CurrentKey.current();
        }
    }

    private @Nonnull Optional<Cookie> getCookie(@Nonnull HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Stream::of)
                .filter(c -> c.getName().equals(SessionConstants.COOKIE_NAME))
                .findAny();
    }

    private @Nonnull Cookie createCookie(@Nonnull SessionKey sessionKey) {
        var cookie = new Cookie(SessionConstants.COOKIE_NAME, sessionKey.toString());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        // TODO Cookie should be secure only
        return cookie;
    }
}
