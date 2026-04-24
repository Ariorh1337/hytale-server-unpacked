package io.sentry.metrics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpMetricsApi implements IMetricsApi {
   private static final NoOpMetricsApi instance = new NoOpMetricsApi();

   private NoOpMetricsApi() {
   }

   public static NoOpMetricsApi getInstance() {
      return instance;
   }

   @Override
   public void count(@NotNull String name) {
   }

   @Override
   public void count(@NotNull String name, @Nullable Double value) {
   }

   @Override
   public void count(@NotNull String name, @Nullable String unit) {
   }

   @Override
   public void count(@NotNull String name, @Nullable Double value, @Nullable String unit) {
   }

   @Override
   public void count(@NotNull String name, @Nullable Double value, @Nullable String unit, @NotNull SentryMetricsParameters params) {
   }

   @Override
   public void distribution(@NotNull String name, @Nullable Double value) {
   }

   @Override
   public void distribution(@NotNull String name, @Nullable Double value, @Nullable String unit) {
   }

   @Override
   public void distribution(@NotNull String name, @Nullable Double value, @Nullable String unit, @NotNull SentryMetricsParameters params) {
   }

   @Override
   public void gauge(@NotNull String name, @Nullable Double value) {
   }

   @Override
   public void gauge(@NotNull String name, @Nullable Double value, @Nullable String unit) {
   }

   @Override
   public void gauge(@NotNull String name, @Nullable Double value, @Nullable String unit, @NotNull SentryMetricsParameters params) {
   }
}
