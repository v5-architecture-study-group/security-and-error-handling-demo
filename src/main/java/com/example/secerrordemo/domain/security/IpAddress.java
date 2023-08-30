package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

public sealed abstract class IpAddress permits IpAddress.Ipv4Address, IpAddress.Ipv6Address {

    private final String ipAddress;

    IpAddress(@Nonnull String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress1 = (IpAddress) o;
        return Objects.equals(ipAddress, ipAddress1.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress);
    }

    public static @Nonnull IpAddress fromString(@Nonnull String ipAddress) {
        if (Ipv4Address.isValid(ipAddress)) {
            return new Ipv4Address(ipAddress);
        } else if (Ipv6Address.isValid(ipAddress)) {
            return new Ipv6Address(ipAddress);
        } else {
            throw new IllegalArgumentException("Invalid IP-address");
        }
    }

    public static final class Ipv4Address extends IpAddress {

        public static final Ipv4Address UNKNOWN = new Ipv4Address("0.0.0.0");
        public static final int MIN_LENGTH = 7; // 0.0.0.0
        public static final int MAX_LENGTH = 15; // 255.255.255.255
        private static final Pattern REGEX = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"); // OWASP Regex for IPv4

        private Ipv4Address(@Nonnull String ipAddress) {
            super(ipAddress);
        }

        public static boolean isValid(@Nonnull String ipAddress) {
            return ipAddress.length() >= MIN_LENGTH
                    && ipAddress.length() <= MAX_LENGTH
                    && StringUtils.containsOnly(ipAddress, "0123456789.")
                    && REGEX.matcher(ipAddress).matches();
        }
    }

    public static final class Ipv6Address extends IpAddress {

        // Note! This does not work for dual addresses!
        public static final Ipv6Address UNKNOWN = new Ipv6Address("::");

        public static final int MIN_LENGTH = 2; // ::
        public static final int MAX_LENGTH = 39; // ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff

        private Ipv6Address(@Nonnull String ipAddress) {
            super(ipAddress);
        }

        public static boolean isValid(@Nonnull String ipAddress) {
            return ipAddress.length() >= MIN_LENGTH
                    && ipAddress.length() <= MAX_LENGTH
                    && StringUtils.containsOnly(ipAddress, "0123456789abcdefABCDEF:");
            // TODO In a real app, you should also check the format of the address but that's a bit more complicated than for Ipv4.
        }
    }
}
