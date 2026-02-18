/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateConfig {
    public static final int DEFAULT_CHECK_INTERVAL_SECONDS = 3600;
    @Nonnull
    public static final Codec<UpdateConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UpdateConfig.class, UpdateConfig::new).append(new KeyedCodec<Boolean>("Enabled", Codec.BOOLEAN), (o, b) -> {
        o.enabled = b;
    }, o -> o.enabled).documentation("Determines whether to enable automatic update checks.").add()).append(new KeyedCodec<Integer>("CheckIntervalSeconds", Codec.INTEGER), (o, i) -> {
        o.checkIntervalSeconds = i;
    }, o -> o.checkIntervalSeconds).documentation("The interval, in seconds, between automatic update checks.").add()).append(new KeyedCodec<Boolean>("NotifyPlayersOnAvailable", Codec.BOOLEAN), (o, b) -> {
        o.notifyPlayersOnAvailable = b;
    }, o -> o.notifyPlayersOnAvailable).documentation("Whether to notify players in-game when an update is available.").add()).append(new KeyedCodec<String>("Patchline", Codec.STRING), (o, s) -> {
        o.patchline = s;
    }, o -> o.patchline).documentation("The patchline to check for updates on.").add()).append(new KeyedCodec<Boolean>("RunBackupBeforeUpdate", Codec.BOOLEAN), (o, b) -> {
        o.runBackupBeforeUpdate = b;
    }, o -> o.runBackupBeforeUpdate).documentation("Determines whether to run a backup before applying an update.").add()).append(new KeyedCodec<Boolean>("BackupConfigBeforeUpdate", Codec.BOOLEAN), (o, b) -> {
        o.backupConfigBeforeUpdate = b;
    }, o -> o.backupConfigBeforeUpdate).documentation("Determines whether to backup the server config before applying an update.").add()).append(new KeyedCodec<AutoApplyMode>("AutoApplyMode", new EnumCodec<AutoApplyMode>(AutoApplyMode.class)), (o, m) -> {
        o.autoApplyMode = m;
    }, o -> o.autoApplyMode).documentation("The mode for automatically applying updates.").add()).append(new KeyedCodec<Integer>("AutoApplyDelayMinutes", Codec.INTEGER), (o, i) -> {
        o.autoApplyDelayMinutes = i;
    }, o -> o.autoApplyDelayMinutes).documentation("The delay in minutes before auto-applying an update while using SCHEDULED mode.").add()).build();
    private Boolean enabled;
    private Integer checkIntervalSeconds;
    private Boolean notifyPlayersOnAvailable;
    private String patchline;
    private Boolean runBackupBeforeUpdate;
    private Boolean backupConfigBeforeUpdate;
    private AutoApplyMode autoApplyMode;
    private Integer autoApplyDelayMinutes;
    @Nullable
    transient HytaleServerConfig hytaleServerConfig;

    public UpdateConfig() {
    }

    public UpdateConfig(@Nonnull HytaleServerConfig hytaleServerConfig) {
        this.hytaleServerConfig = hytaleServerConfig;
    }

    public void setHytaleServerConfig(@Nonnull HytaleServerConfig hytaleServerConfig) {
        this.hytaleServerConfig = hytaleServerConfig;
    }

    public boolean isEnabled() {
        return this.enabled != null ? this.enabled : true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    public int getCheckIntervalSeconds() {
        return this.checkIntervalSeconds != null ? this.checkIntervalSeconds : 3600;
    }

    public void setCheckIntervalSeconds(int checkIntervalSeconds) {
        this.checkIntervalSeconds = checkIntervalSeconds;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    public boolean isNotifyPlayersOnAvailable() {
        return this.notifyPlayersOnAvailable != null ? this.notifyPlayersOnAvailable : true;
    }

    public void setNotifyPlayersOnAvailable(boolean notifyPlayersOnAvailable) {
        this.notifyPlayersOnAvailable = notifyPlayersOnAvailable;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    @Nullable
    public String getPatchline() {
        return this.patchline;
    }

    public void setPatchline(@Nullable String patchline) {
        this.patchline = patchline;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    public boolean isRunBackupBeforeUpdate() {
        return this.runBackupBeforeUpdate != null ? this.runBackupBeforeUpdate : true;
    }

    public void setRunBackupBeforeUpdate(boolean runBackupBeforeUpdate) {
        this.runBackupBeforeUpdate = runBackupBeforeUpdate;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    public boolean isBackupConfigBeforeUpdate() {
        return this.backupConfigBeforeUpdate != null ? this.backupConfigBeforeUpdate : true;
    }

    public void setBackupConfigBeforeUpdate(boolean backupConfigBeforeUpdate) {
        this.backupConfigBeforeUpdate = backupConfigBeforeUpdate;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    @Nonnull
    public AutoApplyMode getAutoApplyMode() {
        return this.autoApplyMode != null ? this.autoApplyMode : AutoApplyMode.DISABLED;
    }

    public void setAutoApplyMode(@Nonnull AutoApplyMode autoApplyMode) {
        this.autoApplyMode = autoApplyMode;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    public int getAutoApplyDelayMinutes() {
        return this.autoApplyDelayMinutes != null ? this.autoApplyDelayMinutes : 30;
    }

    public void setAutoApplyDelayMinutes(int autoApplyDelayMinutes) {
        this.autoApplyDelayMinutes = autoApplyDelayMinutes;
        if (this.hytaleServerConfig != null) {
            this.hytaleServerConfig.markChanged();
        }
    }

    public static enum AutoApplyMode {
        DISABLED,
        WHEN_EMPTY,
        SCHEDULED;

    }
}

