/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Attachment;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ReplayRecording;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Hint {
    @NotNull
    private static final Map<String, Class<?>> PRIMITIVE_MAPPINGS = new HashMap();
    @NotNull
    private final Map<String, Object> internalStorage = new HashMap<String, Object>();
    @NotNull
    private final List<Attachment> attachments = new ArrayList<Attachment>();
    @NotNull
    private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
    @Nullable
    private Attachment screenshot = null;
    @Nullable
    private Attachment viewHierarchy = null;
    @Nullable
    private Attachment threadDump = null;
    @Nullable
    private ReplayRecording replayRecording = null;

    @NotNull
    public static Hint withAttachment(@Nullable Attachment attachment) {
        @NotNull Hint hint = new Hint();
        hint.addAttachment(attachment);
        return hint;
    }

    @NotNull
    public static Hint withAttachments(@Nullable List<Attachment> attachments) {
        @NotNull Hint hint = new Hint();
        hint.addAttachments(attachments);
        return hint;
    }

    public void set(@NotNull String name, @Nullable Object hint) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            this.internalStorage.put(name, hint);
        }
    }

    @Nullable
    public Object get(@NotNull String name) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object object = this.internalStorage.get(name);
            return object;
        }
    }

    @Nullable
    public <T> T getAs(@NotNull String name, @NotNull Class<T> clazz) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object hintValue = this.internalStorage.get(name);
            if (clazz.isInstance(hintValue)) {
                Object object = hintValue;
                return (T)object;
            }
            if (this.isCastablePrimitive(hintValue, clazz)) {
                Object object = hintValue;
                return (T)object;
            }
            T t = null;
            return t;
        }
    }

    public void remove(@NotNull String name) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            this.internalStorage.remove(name);
        }
    }

    public void addAttachment(@Nullable Attachment attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
        }
    }

    public void addAttachments(@Nullable List<Attachment> attachments) {
        if (attachments != null) {
            this.attachments.addAll(attachments);
        }
    }

    @NotNull
    public List<Attachment> getAttachments() {
        return new ArrayList<Attachment>(this.attachments);
    }

    public void replaceAttachments(@Nullable List<Attachment> attachments) {
        this.clearAttachments();
        this.addAttachments(attachments);
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    @ApiStatus.Internal
    public void clear() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Iterator<Map.Entry<String, Object>> iterator = this.internalStorage.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                if (entry.getKey() != null && entry.getKey().startsWith("sentry:")) continue;
                iterator.remove();
            }
        }
    }

    public void setScreenshot(@Nullable Attachment screenshot) {
        this.screenshot = screenshot;
    }

    @Nullable
    public Attachment getScreenshot() {
        return this.screenshot;
    }

    public void setViewHierarchy(@Nullable Attachment viewHierarchy) {
        this.viewHierarchy = viewHierarchy;
    }

    @Nullable
    public Attachment getViewHierarchy() {
        return this.viewHierarchy;
    }

    public void setThreadDump(@Nullable Attachment threadDump) {
        this.threadDump = threadDump;
    }

    @Nullable
    public Attachment getThreadDump() {
        return this.threadDump;
    }

    @Nullable
    public ReplayRecording getReplayRecording() {
        return this.replayRecording;
    }

    public void setReplayRecording(@Nullable ReplayRecording replayRecording) {
        this.replayRecording = replayRecording;
    }

    private boolean isCastablePrimitive(@Nullable Object hintValue, @NotNull Class<?> clazz) {
        Class<?> nonPrimitiveClass = PRIMITIVE_MAPPINGS.get(clazz.getCanonicalName());
        return hintValue != null && clazz.isPrimitive() && nonPrimitiveClass != null && nonPrimitiveClass.isInstance(hintValue);
    }

    static {
        PRIMITIVE_MAPPINGS.put("boolean", Boolean.class);
        PRIMITIVE_MAPPINGS.put("char", Character.class);
        PRIMITIVE_MAPPINGS.put("byte", Byte.class);
        PRIMITIVE_MAPPINGS.put("short", Short.class);
        PRIMITIVE_MAPPINGS.put("int", Integer.class);
        PRIMITIVE_MAPPINGS.put("long", Long.class);
        PRIMITIVE_MAPPINGS.put("float", Float.class);
        PRIMITIVE_MAPPINGS.put("double", Double.class);
    }
}

