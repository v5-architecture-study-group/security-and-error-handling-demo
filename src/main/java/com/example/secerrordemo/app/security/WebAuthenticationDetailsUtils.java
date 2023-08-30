package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.IpAddress;
import com.example.secerrordemo.domain.security.SessionId;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Optional;

final class WebAuthenticationDetailsUtils {
    private WebAuthenticationDetailsUtils() {
    }

    public static @Nonnull IpAddress extractIpAddress(@Nonnull WebAuthenticationDetails details) {
        return Optional.ofNullable(details.getRemoteAddress())
                .map(IpAddress::fromString)
                .orElse(IpAddress.Ipv6Address.UNKNOWN);
    }

    public static @Nonnull IpAddress extractIpAddress(@Nullable Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof WebAuthenticationDetails wad) {
            return extractIpAddress(wad);
        } else {
            return IpAddress.Ipv6Address.UNKNOWN;
        }
    }

    public static @Nonnull SessionId extractSessionId(@Nonnull WebAuthenticationDetails details) {
        return Optional.ofNullable(details.getSessionId())
                .map(SessionId::fromString)
                .orElse(SessionId.UNKNOWN);
    }

    public static @Nonnull SessionId extractSessionId(@Nullable Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof WebAuthenticationDetails wad) {
            return extractSessionId(wad);
        } else {
            return SessionId.UNKNOWN;
        }
    }
}
