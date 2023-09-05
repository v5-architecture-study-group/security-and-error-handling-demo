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
            log.trace("Found session key cookie {}, making sure there is a session before proceeding", cookie.get().getValue());
            CurrentKey.setCurrent(SessionKey.fromString(cookie.get().getValue()));
            // If we don't have a session at this stage, we can assume that we have just been switched over to
            // another server instance by the load balancer. To prevent session authentication errors, we have to
            // create a new session here so that the SessionLoadingListener can populate it before we move on.
            var oldId = request.getRequestedSessionId();
            var session = request.getSession(true);
            if (oldId != null && !oldId.equals(session.getId())) {
                // This is in practice what we have done in this case, so let's fire an event to get the audit logging
                // straight.
                log.trace("Firing HttpSessionIdChangedEvent, oldId = {}, newId = {}", oldId, session.getId());
                applicationEventPublisher.publishEvent(new HttpSessionIdChangedEvent(session, oldId));
            }
            session.setAttribute(SessionConstants.COOKIE_NAME, CurrentKey.current().orElseThrow());
        } else {
            log.trace("No session key cookie present");
        }
    }

    public void storeCurrentKey(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        var cookie = getCookie(request);
        var session = request.getSession(false);
        if (cookie.isEmpty() && session != null) {
            var key = SessionKey.randomKey();
            CurrentKey.setCurrent(key);
            log.trace("Generated new session key {} for session {}", key, session.getId());
            response.addCookie(createCookie(key, request.getContextPath() + "/"));
            session.setAttribute(SessionConstants.COOKIE_NAME, key);
        }
    }

    public @Nonnull Optional<SessionKey> getCurrentKey(@Nonnull HttpSession session) {
        try {
            var key = (SessionKey) session.getAttribute(SessionConstants.COOKIE_NAME);
            if (key != null) {
                log.trace("Found session key in HTTP session {}", session.getId());
                return Optional.of(key);
            } else {
                log.trace("Found no session key in HTTP session {}, resorting to the current key", session.getId());
                return CurrentKey.current();
            }
        } catch (Exception ex) {
            log.trace("Error getting session key, acting as if there is no key at all", ex);
            return Optional.empty();
        }
    }

    private @Nonnull Optional<Cookie> getCookie(@Nonnull HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Stream::of)
                .filter(c -> c.getName().equals(SessionConstants.COOKIE_NAME))
                .findAny();
    }

    private @Nonnull Cookie createCookie(@Nonnull SessionKey sessionKey, @Nonnull String path) {
        var cookie = new Cookie(SessionConstants.COOKIE_NAME, sessionKey.toString());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath(path);
        // TODO Cookie should be secure only
        return cookie;
    }
}
