/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.store.CodecStore;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.ThrowingValidationResults;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.logger.util.GithubMessageUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class ExtraInfo {
    public static final ThreadLocal<ExtraInfo> THREAD_LOCAL = ThreadLocal.withInitial(ExtraInfo::new);
    public static final String GENERATED_ID_PREFIX = "*";
    public static final int UNSET_VERSION = Integer.MAX_VALUE;
    private final int legacyVersion;
    private final int keysInitialSize = this instanceof EmptyExtraInfo ? 0 : 128;
    @Nonnull
    private String[] stringKeys = new String[this.keysInitialSize];
    @Nonnull
    private int[] intKeys = new int[this.keysInitialSize];
    private int[] lineNumbers = GithubMessageUtil.isGithub() ? new int[this.keysInitialSize] : null;
    private int[] columnNumbers = GithubMessageUtil.isGithub() ? new int[this.keysInitialSize] : null;
    private int keysSize;
    @Nonnull
    private String[] ignoredUnknownKeys = new String[this.keysInitialSize];
    private int ignoredUnknownSize;
    private final List<String> unknownKeys = new ObjectArrayList<String>();
    private final ValidationResults validationResults;
    private final CodecStore codecStore;
    @Deprecated
    private final Map<String, Object> metadata = new Object2ObjectOpenHashMap<String, Object>();

    public ExtraInfo() {
        this.legacyVersion = Integer.MAX_VALUE;
        this.validationResults = new ThrowingValidationResults(this);
        this.codecStore = CodecStore.STATIC;
    }

    @Deprecated
    public ExtraInfo(int version) {
        this.legacyVersion = version;
        this.validationResults = new ThrowingValidationResults(this);
        this.codecStore = CodecStore.STATIC;
    }

    @Deprecated
    public ExtraInfo(int version, @Nonnull Function<ExtraInfo, ValidationResults> validationResultsSupplier) {
        this.legacyVersion = version;
        this.validationResults = validationResultsSupplier.apply(this);
        this.codecStore = CodecStore.STATIC;
    }

    public int getVersion() {
        return Integer.MAX_VALUE;
    }

    @Deprecated
    public int getLegacyVersion() {
        return this.legacyVersion;
    }

    public int getKeysSize() {
        return this.keysSize;
    }

    public CodecStore getCodecStore() {
        return this.codecStore;
    }

    private int nextKeyIndex() {
        int index;
        if (this.stringKeys.length <= (index = this.keysSize++)) {
            int newLength = ExtraInfo.grow(index);
            this.stringKeys = Arrays.copyOf(this.stringKeys, newLength);
            this.intKeys = Arrays.copyOf(this.intKeys, newLength);
            if (GithubMessageUtil.isGithub()) {
                this.lineNumbers = Arrays.copyOf(this.lineNumbers, newLength);
                this.columnNumbers = Arrays.copyOf(this.columnNumbers, newLength);
            }
        }
        return index;
    }

    public void pushKey(String key) {
        int index = this.nextKeyIndex();
        this.stringKeys[index] = key;
    }

    public void pushIntKey(int key) {
        int index = this.nextKeyIndex();
        this.intKeys[index] = key;
    }

    public void pushKey(String key, RawJsonReader reader) {
        int index = this.nextKeyIndex();
        this.stringKeys[index] = key;
        if (GithubMessageUtil.isGithub()) {
            this.lineNumbers[index] = reader.getLine();
            this.columnNumbers[index] = reader.getColumn();
        }
    }

    public void pushIntKey(int key, RawJsonReader reader) {
        int index = this.nextKeyIndex();
        this.intKeys[index] = key;
        if (GithubMessageUtil.isGithub()) {
            this.lineNumbers[index] = reader.getLine();
            this.columnNumbers[index] = reader.getColumn();
        }
    }

    public void popKey() {
        this.stringKeys[this.keysSize] = null;
        --this.keysSize;
    }

    private int nextIgnoredUnknownIndex() {
        int index;
        if (this.ignoredUnknownKeys.length <= (index = this.ignoredUnknownSize++)) {
            this.ignoredUnknownKeys = Arrays.copyOf(this.ignoredUnknownKeys, ExtraInfo.grow(index));
        }
        return index;
    }

    public void ignoreUnusedKey(String key) {
        int index = this.nextIgnoredUnknownIndex();
        this.ignoredUnknownKeys[index] = key;
    }

    public void popIgnoredUnusedKey() {
        this.ignoredUnknownKeys[this.ignoredUnknownSize] = null;
        --this.ignoredUnknownSize;
    }

    public boolean consumeIgnoredUnknownKey(@Nonnull RawJsonReader reader) throws IOException {
        if (this.ignoredUnknownSize <= 0) {
            return false;
        }
        int lastIndex = this.ignoredUnknownSize - 1;
        String ignoredUnknownKey = this.ignoredUnknownKeys[lastIndex];
        if (ignoredUnknownKey == null) {
            return false;
        }
        if (!reader.tryConsumeString(ignoredUnknownKey)) {
            return false;
        }
        this.ignoredUnknownKeys[lastIndex] = null;
        return true;
    }

    public boolean consumeIgnoredUnknownKey(@Nonnull String key) {
        if (this.ignoredUnknownSize <= 0) {
            return false;
        }
        int lastIndex = this.ignoredUnknownSize - 1;
        if (!key.equals(this.ignoredUnknownKeys[lastIndex])) {
            return false;
        }
        this.ignoredUnknownKeys[lastIndex] = null;
        return true;
    }

    public void readUnknownKey(@Nonnull RawJsonReader reader) throws IOException {
        if (this.consumeIgnoredUnknownKey(reader)) {
            return;
        }
        String key = reader.readString();
        if (this.keysSize == 0) {
            this.unknownKeys.add(key);
        } else {
            this.unknownKeys.add(this.peekKey() + "." + key);
        }
    }

    public void addUnknownKey(@Nonnull String key) {
        switch (key) {
            case "$Title": 
            case "$Comment": 
            case "$TODO": 
            case "$Author": 
            case "$Position": 
            case "$FloatingFunctionNodes": 
            case "$Groups": 
            case "$WorkspaceID": 
            case "$NodeEditorMetadata": 
            case "$NodeId": {
                return;
            }
        }
        if (this.consumeIgnoredUnknownKey(key)) {
            return;
        }
        if (this.keysSize == 0) {
            if ("Parent".equals(key)) {
                return;
            }
            this.unknownKeys.add(key);
        } else {
            this.unknownKeys.add(this.peekKey() + "." + key);
        }
    }

    public String peekKey() {
        return this.peekKey('.');
    }

    public String peekKey(char separator) {
        if (this.keysSize == 0) {
            return "";
        }
        if (this.keysSize == 1) {
            String str = this.stringKeys[0];
            if (str != null) {
                return str;
            }
            return String.valueOf(this.intKeys[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.keysSize; ++i) {
            String str;
            if (i > 0) {
                sb.append(separator);
            }
            if ((str = this.stringKeys[i]) != null) {
                sb.append(str);
                continue;
            }
            sb.append(this.intKeys[i]);
        }
        return sb.toString();
    }

    public int peekLine() {
        if (GithubMessageUtil.isGithub() && this.keysSize > 0) {
            return this.lineNumbers[this.keysSize - 1];
        }
        return -1;
    }

    public int peekColumn() {
        if (GithubMessageUtil.isGithub() && this.keysSize > 0) {
            return this.columnNumbers[this.keysSize - 1];
        }
        return -1;
    }

    public List<String> getUnknownKeys() {
        return this.unknownKeys;
    }

    public ValidationResults getValidationResults() {
        return this.validationResults;
    }

    @Deprecated
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public void appendDetailsTo(@Nonnull StringBuilder sb) {
        sb.append("ExtraInfo\n");
    }

    @Nonnull
    public String toString() {
        return "ExtraInfo{version=" + this.legacyVersion + ", stringKeys=" + Arrays.toString(this.stringKeys) + ", intKeys=" + Arrays.toString(this.intKeys) + ", keysSize=" + this.keysSize + ", ignoredUnknownKeys=" + Arrays.toString(this.ignoredUnknownKeys) + ", ignoredUnknownSize=" + this.ignoredUnknownSize + ", unknownKeys=" + String.valueOf(this.unknownKeys) + ", validationResults=" + String.valueOf(this.validationResults) + "}";
    }

    private static int grow(int oldSize) {
        return oldSize + (oldSize >> 1);
    }
}

