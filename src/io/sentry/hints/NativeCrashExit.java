package io.sentry.hints;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface NativeCrashExit {
   @NotNull
   Long timestamp();
}
