/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SpanDataConvention {
    public static final String DB_SYSTEM_KEY = "db.system";
    public static final String DB_NAME_KEY = "db.name";
    public static final String HTTP_QUERY_KEY = "http.query";
    public static final String HTTP_FRAGMENT_KEY = "http.fragment";
    public static final String HTTP_METHOD_KEY = "http.request.method";
    public static final String HTTP_STATUS_CODE_KEY = "http.response.status_code";
    public static final String HTTP_RESPONSE_CONTENT_LENGTH_KEY = "http.response_content_length";
    public static final String BLOCKED_MAIN_THREAD_KEY = "blocked_main_thread";
    public static final String CALL_STACK_KEY = "call_stack";
    public static final String THREAD_ID = "thread.id";
    public static final String THREAD_NAME = "thread.name";
    public static final String FRAMES_TOTAL = "frames.total";
    public static final String FRAMES_SLOW = "frames.slow";
    public static final String FRAMES_FROZEN = "frames.frozen";
    public static final String FRAMES_DELAY = "frames.delay";
    public static final String CONTRIBUTES_TTID = "ui.contributes_to_ttid";
    public static final String CONTRIBUTES_TTFD = "ui.contributes_to_ttfd";
    public static final String HTTP_START_TIMESTAMP = "http.start_timestamp";
    public static final String HTTP_END_TIMESTAMP = "http.end_timestamp";
    public static final String PROFILER_ID = "profiler_id";
}

