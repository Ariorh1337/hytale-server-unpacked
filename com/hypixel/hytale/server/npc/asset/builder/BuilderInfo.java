/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.Builder;
import java.nio.file.Path;

public class BuilderInfo {
    private final String keyName;
    private final int index;
    private final Builder<?> builder;
    private final Path path;
    private State status;

    public BuilderInfo(int index, String keyName, Builder<?> builder, Path path) {
        this.index = index;
        this.keyName = keyName;
        this.builder = builder;
        this.path = path;
        this.status = State.NEEDS_VALIDATION;
    }

    public int getIndex() {
        return this.index;
    }

    public String getKeyName() {
        return this.keyName;
    }

    public Builder<?> getBuilder() {
        return this.builder;
    }

    public Path getPath() {
        return this.path;
    }

    public boolean isValidated() {
        return this.status == State.VALID || this.status == State.INVALID;
    }

    public boolean isValid() {
        return this.status == State.VALID;
    }

    public boolean setValidated(boolean success) {
        this.status = success ? State.VALID : State.INVALID;
        return success;
    }

    public void setForceValidation() {
        this.status = State.NEEDS_VALIDATION;
    }

    public void setNeedsValidation() {
        if (this.status != State.REMOVED) {
            this.status = State.NEEDS_VALIDATION;
        }
    }

    public void setNeedsReload() {
        if (this.status != State.REMOVED) {
            this.status = State.NEEDS_RELOAD;
        }
    }

    public boolean canBeValidated() {
        return this.status != State.NEEDS_RELOAD && this.status != State.REMOVED;
    }

    public boolean needsValidation() {
        return this.status == State.NEEDS_VALIDATION;
    }

    public void setRemoved() {
        this.status = State.REMOVED;
    }

    public boolean isRemoved() {
        return this.status == State.REMOVED;
    }

    protected static enum State {
        NEEDS_RELOAD,
        NEEDS_VALIDATION,
        VALID,
        INVALID,
        REMOVED;

    }
}

