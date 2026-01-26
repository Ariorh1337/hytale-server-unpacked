/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache;

import io.sentry.Breadcrumb;
import io.sentry.IScope;
import io.sentry.ScopeObserverAdapter;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SpanContext;
import io.sentry.cache.CacheUtils;
import io.sentry.cache.tape.ObjectQueue;
import io.sentry.cache.tape.QueueFile;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.LazyEvaluator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PersistingScopeObserver
extends ScopeObserverAdapter {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String SCOPE_CACHE = ".scope-cache";
    public static final String USER_FILENAME = "user.json";
    public static final String BREADCRUMBS_FILENAME = "breadcrumbs.json";
    public static final String TAGS_FILENAME = "tags.json";
    public static final String EXTRAS_FILENAME = "extras.json";
    public static final String CONTEXTS_FILENAME = "contexts.json";
    public static final String REQUEST_FILENAME = "request.json";
    public static final String LEVEL_FILENAME = "level.json";
    public static final String FINGERPRINT_FILENAME = "fingerprint.json";
    public static final String TRANSACTION_FILENAME = "transaction.json";
    public static final String TRACE_FILENAME = "trace.json";
    public static final String REPLAY_FILENAME = "replay.json";
    @NotNull
    private SentryOptions options;
    @NotNull
    private final LazyEvaluator<ObjectQueue<Breadcrumb>> breadcrumbsQueue = new LazyEvaluator<ObjectQueue>(() -> {
        File cacheDir = CacheUtils.ensureCacheDir(this.options, SCOPE_CACHE);
        if (cacheDir == null) {
            this.options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot store in scope cache", new Object[0]);
            return ObjectQueue.createEmpty();
        }
        QueueFile queueFile = null;
        File file = new File(cacheDir, BREADCRUMBS_FILENAME);
        try {
            try {
                queueFile = new QueueFile.Builder(file).size(this.options.getMaxBreadcrumbs()).build();
            }
            catch (IOException e) {
                file.delete();
                queueFile = new QueueFile.Builder(file).size(this.options.getMaxBreadcrumbs()).build();
            }
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to create breadcrumbs queue", e);
            return ObjectQueue.createEmpty();
        }
        return ObjectQueue.create(queueFile, new ObjectQueue.Converter<Breadcrumb>(){

            @Override
            @Nullable
            public Breadcrumb from(byte[] source) {
                Breadcrumb breadcrumb;
                BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(source), UTF_8));
                try {
                    breadcrumb = PersistingScopeObserver.this.options.getSerializer().deserialize(reader, Breadcrumb.class);
                }
                catch (Throwable throwable) {
                    try {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                    catch (Throwable e) {
                        PersistingScopeObserver.this.options.getLogger().log(SentryLevel.ERROR, e, "Error reading entity from scope cache", new Object[0]);
                        return null;
                    }
                }
                ((Reader)reader).close();
                return breadcrumb;
            }

            @Override
            public void toStream(Breadcrumb value, OutputStream sink) throws IOException {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sink, UTF_8));){
                    PersistingScopeObserver.this.options.getSerializer().serialize(value, writer);
                }
            }
        });
    });

    public PersistingScopeObserver(@NotNull SentryOptions options) {
        this.options = options;
    }

    @Override
    public void setUser(@Nullable User user) {
        this.serializeToDisk(() -> {
            if (user == null) {
                this.delete(USER_FILENAME);
            } else {
                this.store(user, USER_FILENAME);
            }
        });
    }

    @Override
    public void addBreadcrumb(@NotNull Breadcrumb crumb) {
        this.serializeToDisk(() -> {
            try {
                this.breadcrumbsQueue.getValue().add(crumb);
            }
            catch (IOException e) {
                this.options.getLogger().log(SentryLevel.ERROR, "Failed to add breadcrumb to file queue", e);
            }
        });
    }

    @Override
    public void setBreadcrumbs(@NotNull Collection<Breadcrumb> breadcrumbs) {
        if (breadcrumbs.isEmpty()) {
            this.serializeToDisk(() -> {
                try {
                    this.breadcrumbsQueue.getValue().clear();
                }
                catch (IOException e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Failed to clear breadcrumbs from file queue", e);
                }
            });
        }
    }

    @Override
    public void setTags(@NotNull @NotNull Map<String, @NotNull String> tags) {
        this.serializeToDisk(() -> this.store(tags, TAGS_FILENAME));
    }

    @Override
    public void setExtras(@NotNull @NotNull Map<String, @NotNull Object> extras) {
        this.serializeToDisk(() -> this.store(extras, EXTRAS_FILENAME));
    }

    @Override
    public void setRequest(@Nullable Request request) {
        this.serializeToDisk(() -> {
            if (request == null) {
                this.delete(REQUEST_FILENAME);
            } else {
                this.store(request, REQUEST_FILENAME);
            }
        });
    }

    @Override
    public void setFingerprint(@NotNull Collection<String> fingerprint) {
        this.serializeToDisk(() -> this.store(fingerprint, FINGERPRINT_FILENAME));
    }

    @Override
    public void setLevel(@Nullable SentryLevel level) {
        this.serializeToDisk(() -> {
            if (level == null) {
                this.delete(LEVEL_FILENAME);
            } else {
                this.store(level, LEVEL_FILENAME);
            }
        });
    }

    @Override
    public void setTransaction(@Nullable String transaction) {
        this.serializeToDisk(() -> {
            if (transaction == null) {
                this.delete(TRANSACTION_FILENAME);
            } else {
                this.store(transaction, TRANSACTION_FILENAME);
            }
        });
    }

    @Override
    public void setTrace(@Nullable SpanContext spanContext, @NotNull IScope scope) {
        this.serializeToDisk(() -> {
            if (spanContext == null) {
                this.store(scope.getPropagationContext().toSpanContext(), TRACE_FILENAME);
            } else {
                this.store(spanContext, TRACE_FILENAME);
            }
        });
    }

    @Override
    public void setContexts(@NotNull Contexts contexts) {
        this.serializeToDisk(() -> this.store(contexts, CONTEXTS_FILENAME));
    }

    @Override
    public void setReplayId(@NotNull SentryId replayId) {
        this.serializeToDisk(() -> this.store(replayId, REPLAY_FILENAME));
    }

    private void serializeToDisk(@NotNull Runnable task) {
        if (!this.options.isEnableScopePersistence()) {
            return;
        }
        if (Thread.currentThread().getName().contains("SentryExecutor")) {
            try {
                task.run();
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "Serialization task failed", e);
            }
            return;
        }
        try {
            this.options.getExecutorService().submit(() -> {
                try {
                    task.run();
                }
                catch (Throwable e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Serialization task failed", e);
                }
            });
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Serialization task could not be scheduled", e);
        }
    }

    private <T> void store(@NotNull T entity, @NotNull String fileName) {
        PersistingScopeObserver.store(this.options, entity, fileName);
    }

    private void delete(@NotNull String fileName) {
        CacheUtils.delete(this.options, SCOPE_CACHE, fileName);
    }

    public static <T> void store(@NotNull SentryOptions options, @NotNull T entity, @NotNull String fileName) {
        CacheUtils.store(options, entity, SCOPE_CACHE, fileName);
    }

    @Nullable
    public <T> T read(@NotNull SentryOptions options, @NotNull String fileName, @NotNull Class<T> clazz) {
        if (fileName.equals(BREADCRUMBS_FILENAME)) {
            try {
                return clazz.cast(this.breadcrumbsQueue.getValue().asList());
            }
            catch (IOException e) {
                options.getLogger().log(SentryLevel.ERROR, "Unable to read serialized breadcrumbs from QueueFile", new Object[0]);
                return null;
            }
        }
        return CacheUtils.read(options, SCOPE_CACHE, fileName, clazz, null);
    }

    public void resetCache() {
        try {
            this.breadcrumbsQueue.getValue().clear();
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to clear breadcrumbs from file queue", e);
        }
        this.delete(USER_FILENAME);
        this.delete(LEVEL_FILENAME);
        this.delete(REQUEST_FILENAME);
        this.delete(FINGERPRINT_FILENAME);
        this.delete(CONTEXTS_FILENAME);
        this.delete(EXTRAS_FILENAME);
        this.delete(TAGS_FILENAME);
        this.delete(TRACE_FILENAME);
        this.delete(TRANSACTION_FILENAME);
    }
}

