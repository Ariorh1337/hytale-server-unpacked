/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor;

import com.hypixel.hytale.server.core.Message;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabLoadingState {
    @Nonnull
    private Phase currentPhase = Phase.INITIALIZING;
    private int totalPrefabs;
    private int loadedPrefabs;
    private int pastedPrefabs;
    @Nullable
    private Path currentPrefabPath;
    @Nonnull
    private final List<LoadingError> errors = new ObjectArrayList<LoadingError>();
    private long startTimeNanos;
    private long lastUpdateTimeNanos = this.startTimeNanos = System.nanoTime();
    private long lastNotifyTimeNanos;

    public void setTotalPrefabs(int totalPrefabs) {
        this.totalPrefabs = totalPrefabs;
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public void setPhase(@Nonnull Phase phase) {
        this.currentPhase = phase;
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public void onPrefabLoaded(@Nullable Path path) {
        ++this.loadedPrefabs;
        this.currentPrefabPath = path;
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public void onPrefabPasted(@Nullable Path path) {
        ++this.pastedPrefabs;
        this.currentPrefabPath = path;
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public void addError(@Nonnull LoadingError error) {
        this.errors.add(error);
        this.currentPhase = Phase.ERROR;
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public void addError(@Nonnull String translationKey) {
        this.addError(new LoadingError(translationKey));
    }

    public void addError(@Nonnull String translationKey, @Nullable String details) {
        this.addError(new LoadingError(translationKey, details));
    }

    @Nonnull
    public Phase getCurrentPhase() {
        return this.currentPhase;
    }

    public int getTotalPrefabs() {
        return this.totalPrefabs;
    }

    public int getLoadedPrefabs() {
        return this.loadedPrefabs;
    }

    public int getPastedPrefabs() {
        return this.pastedPrefabs;
    }

    @Nullable
    public Path getCurrentPrefabPath() {
        return this.currentPrefabPath;
    }

    @Nonnull
    public List<LoadingError> getErrors() {
        return this.errors;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public boolean isShuttingDown() {
        return this.currentPhase == Phase.CANCELLING || this.currentPhase == Phase.SHUTTING_DOWN_WORLD || this.currentPhase == Phase.DELETING_WORLD;
    }

    public boolean isShutdownComplete() {
        return this.currentPhase == Phase.SHUTDOWN_COMPLETE;
    }

    public float getProgressPercentage() {
        if (this.totalPrefabs == 0) {
            return switch (this.currentPhase.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> 0.0f;
                case 1 -> 0.1f;
                case 2 -> 0.2f;
                case 3 -> 0.5f;
                case 4 -> 0.99f;
                case 5 -> 1.0f;
                case 6 -> 0.0f;
                case 7 -> 0.1f;
                case 8 -> 0.4f;
                case 9 -> 0.8f;
                case 10 -> 1.0f;
            };
        }
        return switch (this.currentPhase.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 0.0f;
            case 1 -> 0.02f;
            case 2 -> 0.02f + 0.08f * (float)this.loadedPrefabs / (float)this.totalPrefabs;
            case 3 -> 0.1f + 0.89f * (float)this.pastedPrefabs / (float)this.totalPrefabs;
            case 4 -> 0.99f;
            case 5 -> 1.0f;
            case 6 -> 0.0f;
            case 7 -> 0.1f;
            case 8 -> 0.4f;
            case 9 -> 0.8f;
            case 10 -> 1.0f;
        };
    }

    public long getElapsedTimeMillis() {
        return (System.nanoTime() - this.startTimeNanos) / 1000000L;
    }

    public long getLastNotifyTimeNanos() {
        return this.lastNotifyTimeNanos;
    }

    public void setLastNotifyTimeNanos(long nanos) {
        this.lastNotifyTimeNanos = nanos;
    }

    @Nonnull
    public Message getStatusMessage() {
        if (this.hasErrors()) {
            return this.errors.getLast().toMessage();
        }
        Message message = Message.translation(this.currentPhase.getTranslationKey());
        if (this.currentPhase == Phase.LOADING_PREFABS && this.totalPrefabs > 0) {
            message = message.param("current", this.loadedPrefabs).param("total", this.totalPrefabs);
        } else if (this.currentPhase == Phase.PASTING_PREFABS && this.totalPrefabs > 0) {
            message = message.param("current", this.pastedPrefabs).param("total", this.totalPrefabs);
        }
        if (this.currentPrefabPath != null) {
            message = message.param("path", this.currentPrefabPath.getFileName().toString());
        }
        return message;
    }

    public void markComplete() {
        this.currentPhase = Phase.COMPLETE;
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public static enum Phase {
        INITIALIZING("server.commands.editprefab.loading.phase.initializing"),
        CREATING_WORLD("server.commands.editprefab.loading.phase.creatingWorld"),
        LOADING_PREFABS("server.commands.editprefab.loading.phase.loadingPrefabs"),
        PASTING_PREFABS("server.commands.editprefab.loading.phase.pastingPrefabs"),
        FINALIZING("server.commands.editprefab.loading.phase.finalizing"),
        COMPLETE("server.commands.editprefab.loading.phase.complete"),
        ERROR("server.commands.editprefab.loading.phase.error"),
        CANCELLING("server.commands.editprefab.loading.phase.cancelling"),
        SHUTTING_DOWN_WORLD("server.commands.editprefab.loading.phase.shuttingDownWorld"),
        DELETING_WORLD("server.commands.editprefab.loading.phase.deletingWorld"),
        SHUTDOWN_COMPLETE("server.commands.editprefab.loading.phase.shutdownComplete");

        private final String translationKey;

        private Phase(String translationKey) {
            this.translationKey = translationKey;
        }

        @Nonnull
        public String getTranslationKey() {
            return this.translationKey;
        }
    }

    public record LoadingError(@Nonnull String translationKey, @Nullable String details) {
        public LoadingError(@Nonnull String translationKey) {
            this(translationKey, null);
        }

        @Nonnull
        public Message toMessage() {
            Message message = Message.translation(this.translationKey);
            if (this.details != null) {
                message = message.param("details", this.details);
            }
            return message;
        }
    }
}

