/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class IpAddressUtils {
    public static final String DEFAULT_IP_ADDRESS = "{{auto}}";
    private static final List<String> DEFAULT_IP_ADDRESS_VALID_VALUES = Arrays.asList("{{auto}}", "{{ auto }}");

    private IpAddressUtils() {
    }

    public static boolean isDefault(@Nullable String ipAddress) {
        return ipAddress != null && DEFAULT_IP_ADDRESS_VALID_VALUES.contains(ipAddress);
    }
}

