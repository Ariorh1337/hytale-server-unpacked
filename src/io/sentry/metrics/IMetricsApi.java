package io.sentry.metrics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMetricsApi {
   void count(@NotNull String var1);

   void count(@NotNull String var1, @Nullable Double var2);

   void count(@NotNull String var1, @Nullable String var2);

   void count(@NotNull String var1, @Nullable Double var2, @Nullable String var3);

   void count(@NotNull String var1, @Nullable Double var2, @Nullable String var3, @NotNull SentryMetricsParameters var4);

   void distribution(@NotNull String var1, @Nullable Double var2);

   void distribution(@NotNull String var1, @Nullable Double var2, @Nullable String var3);

   void distribution(@NotNull String var1, @Nullable Double var2, @Nullable String var3, @NotNull SentryMetricsParameters var4);

   void gauge(@NotNull String var1, @Nullable Double var2);

   void gauge(@NotNull String var1, @Nullable Double var2, @Nullable String var3);

   void gauge(@NotNull String var1, @Nullable Double var2, @Nullable String var3, @NotNull SentryMetricsParameters var4);
}
