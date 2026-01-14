package io.sentry;

import java.net.URI;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class DsnUtil {
   public static boolean urlContainsDsnHost(@Nullable SentryOptions options, @Nullable String url) {
      if (options == null) {
         return false;
      }

      if (url == null) {
         return false;
      }

      String dsnString = options.getDsn();
      if (dsnString == null) {
         return false;
      }

      Dsn dsn = options.retrieveParsedDsn();
      URI sentryUri = dsn.getSentryUri();
      String dsnHost = sentryUri.getHost();
      if (dsnHost == null) {
         return false;
      }

      String lowerCaseHost = dsnHost.toLowerCase(Locale.ROOT);
      int dsnPort = sentryUri.getPort();
      return dsnPort > 0 ? url.toLowerCase(Locale.ROOT).contains(lowerCaseHost + ":" + dsnPort) : url.toLowerCase(Locale.ROOT).contains(lowerCaseHost);
   }
}
