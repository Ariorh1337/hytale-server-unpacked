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
import io.sentry.CheckIn;
import io.sentry.ILogger;
import io.sentry.IProfileConverter;
import io.sentry.ISerializer;
import io.sentry.JsonSerializable;
import io.sentry.NoOpProfileConverter;
import io.sentry.ProfileChunk;
import io.sentry.ProfilingTraceData;
import io.sentry.ReplayRecording;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryEnvelopeItemHeader;
import io.sentry.SentryEvent;
import io.sentry.SentryItemType;
import io.sentry.SentryLogEvents;
import io.sentry.SentryReplayEvent;
import io.sentry.Session;
import io.sentry.UserFeedback;
import io.sentry.clientreport.ClientReport;
import io.sentry.exception.SentryEnvelopeException;
import io.sentry.protocol.SentryTransaction;
import io.sentry.util.FileUtils;
import io.sentry.util.JsonSerializationUtils;
import io.sentry.util.Objects;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SentryEnvelopeItem {
    private static final long MAX_PROFILE_CHUNK_SIZE = 0x3200000L;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final SentryEnvelopeItemHeader header;
    @Nullable
    private final Callable<byte[]> dataFactory;
    @Nullable
    private byte[] data;

    SentryEnvelopeItem(@NotNull SentryEnvelopeItemHeader header, byte[] data) {
        this.header = Objects.requireNonNull(header, "SentryEnvelopeItemHeader is required.");
        this.data = data;
        this.dataFactory = null;
    }

    SentryEnvelopeItem(@NotNull SentryEnvelopeItemHeader header, @Nullable Callable<byte[]> dataFactory) {
        this.header = Objects.requireNonNull(header, "SentryEnvelopeItemHeader is required.");
        this.dataFactory = Objects.requireNonNull(dataFactory, "DataFactory is required.");
        this.data = null;
    }

    @NotNull
    public byte[] getData() throws Exception {
        if (this.data == null && this.dataFactory != null) {
            this.data = this.dataFactory.call();
        }
        return this.data;
    }

    @NotNull
    public SentryEnvelopeItemHeader getHeader() {
        return this.header;
    }

    @NotNull
    public static SentryEnvelopeItem fromSession(@NotNull ISerializer serializer, @NotNull Session session) throws IOException {
        Objects.requireNonNull(serializer, "ISerializer is required.");
        Objects.requireNonNull(session, "Session is required.");
        CachedItem cachedItem = new CachedItem(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
                byte[] byArray;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                    serializer.serialize(session, writer);
                    byArray = stream.toByteArray();
                }
                return byArray;
            }
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.Session, () -> cachedItem.getBytes().length, "application/json", null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    @Nullable
    public SentryEvent getEvent(@NotNull ISerializer serializer) throws Exception {
        if (this.header == null || this.header.getType() != SentryItemType.Event) {
            return null;
        }
        try (BufferedReader eventReader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(this.getData()), UTF_8));){
            SentryEvent sentryEvent = serializer.deserialize(eventReader, SentryEvent.class);
            return sentryEvent;
        }
    }

    @NotNull
    public static SentryEnvelopeItem fromEvent(@NotNull ISerializer serializer, @NotNull SentryBaseEvent event) {
        Objects.requireNonNull(serializer, "ISerializer is required.");
        Objects.requireNonNull(event, "SentryEvent is required.");
        CachedItem cachedItem = new CachedItem(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
                byte[] byArray;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                    serializer.serialize(event, writer);
                    byArray = stream.toByteArray();
                }
                return byArray;
            }
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.resolve(event), () -> cachedItem.getBytes().length, "application/json", null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    @Nullable
    public SentryTransaction getTransaction(@NotNull ISerializer serializer) throws Exception {
        if (this.header == null || this.header.getType() != SentryItemType.Transaction) {
            return null;
        }
        try (BufferedReader eventReader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(this.getData()), UTF_8));){
            SentryTransaction sentryTransaction = serializer.deserialize(eventReader, SentryTransaction.class);
            return sentryTransaction;
        }
    }

    @Nullable
    public SentryLogEvents getLogs(@NotNull ISerializer serializer) throws Exception {
        if (this.header == null || this.header.getType() != SentryItemType.Log) {
            return null;
        }
        try (BufferedReader eventReader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(this.getData()), UTF_8));){
            SentryLogEvents sentryLogEvents = serializer.deserialize(eventReader, SentryLogEvents.class);
            return sentryLogEvents;
        }
    }

    public static SentryEnvelopeItem fromUserFeedback(@NotNull ISerializer serializer, @NotNull UserFeedback userFeedback) {
        Objects.requireNonNull(serializer, "ISerializer is required.");
        Objects.requireNonNull(userFeedback, "UserFeedback is required.");
        CachedItem cachedItem = new CachedItem(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
                byte[] byArray;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                    serializer.serialize(userFeedback, writer);
                    byArray = stream.toByteArray();
                }
                return byArray;
            }
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.UserFeedback, () -> cachedItem.getBytes().length, "application/json", null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    public static SentryEnvelopeItem fromCheckIn(@NotNull ISerializer serializer, @NotNull CheckIn checkIn) {
        Objects.requireNonNull(serializer, "ISerializer is required.");
        Objects.requireNonNull(checkIn, "CheckIn is required.");
        CachedItem cachedItem = new CachedItem(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
                byte[] byArray;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                    serializer.serialize(checkIn, writer);
                    byArray = stream.toByteArray();
                }
                return byArray;
            }
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.CheckIn, () -> cachedItem.getBytes().length, "application/json", null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    public static SentryEnvelopeItem fromAttachment(@NotNull ISerializer serializer, @NotNull ILogger logger, @NotNull Attachment attachment, long maxAttachmentSize) {
        CachedItem cachedItem = new CachedItem(() -> {
            if (attachment.getBytes() != null) {
                byte[] data = attachment.getBytes();
                SentryEnvelopeItem.ensureAttachmentSizeLimit(data.length, maxAttachmentSize, attachment.getFilename());
                return data;
            }
            if (attachment.getSerializable() != null) {
                JsonSerializable serializable = attachment.getSerializable();
                @Nullable byte[] data = JsonSerializationUtils.bytesFrom(serializer, logger, serializable);
                if (data != null) {
                    SentryEnvelopeItem.ensureAttachmentSizeLimit(data.length, maxAttachmentSize, attachment.getFilename());
                    return data;
                }
            } else {
                byte[] data;
                if (attachment.getPathname() != null) {
                    return FileUtils.readBytesFromFile(attachment.getPathname(), maxAttachmentSize);
                }
                if (attachment.getByteProvider() != null && (data = attachment.getByteProvider().call()) != null) {
                    SentryEnvelopeItem.ensureAttachmentSizeLimit(data.length, maxAttachmentSize, attachment.getFilename());
                    return data;
                }
            }
            throw new SentryEnvelopeException(String.format("Couldn't attach the attachment %s.\nPlease check that either bytes, serializable, path or provider is set.", attachment.getFilename()));
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.Attachment, () -> cachedItem.getBytes().length, attachment.getContentType(), attachment.getFilename(), attachment.getAttachmentType());
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    private static void ensureAttachmentSizeLimit(long size, long maxAttachmentSize, @NotNull String filename) throws SentryEnvelopeException {
        if (size > maxAttachmentSize) {
            throw new SentryEnvelopeException(String.format("Dropping attachment with filename '%s', because the size of the passed bytes with %d bytes is bigger than the maximum allowed attachment size of %d bytes.", filename, size, maxAttachmentSize));
        }
    }

    @NotNull
    public static SentryEnvelopeItem fromProfileChunk(@NotNull ProfileChunk profileChunk, @NotNull ISerializer serializer) throws SentryEnvelopeException {
        return SentryEnvelopeItem.fromProfileChunk(profileChunk, serializer, NoOpProfileConverter.getInstance());
    }

    @NotNull
    public static SentryEnvelopeItem fromProfileChunk(@NotNull ProfileChunk profileChunk, @NotNull ISerializer serializer, @NotNull IProfileConverter profileConverter) throws SentryEnvelopeException {
        @NotNull File traceFile = profileChunk.getTraceFile();
        CachedItem cachedItem = new CachedItem(() -> {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.ProfileChunk, () -> cachedItem.getBytes().length, "application-json", traceFile.getName(), null, profileChunk.getPlatform(), null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    @NotNull
    public static SentryEnvelopeItem fromProfilingTrace(@NotNull ProfilingTraceData profilingTraceData, long maxTraceFileSize, @NotNull ISerializer serializer) throws SentryEnvelopeException {
        @NotNull File traceFile = profilingTraceData.getTraceFile();
        CachedItem cachedItem = new CachedItem(() -> {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.Profile, () -> cachedItem.getBytes().length, "application-json", traceFile.getName());
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    @NotNull
    public static SentryEnvelopeItem fromClientReport(@NotNull ISerializer serializer, @NotNull ClientReport clientReport) throws IOException {
        Objects.requireNonNull(serializer, "ISerializer is required.");
        Objects.requireNonNull(clientReport, "ClientReport is required.");
        CachedItem cachedItem = new CachedItem(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
                byte[] byArray;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                    serializer.serialize(clientReport, writer);
                    byArray = stream.toByteArray();
                }
                return byArray;
            }
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.resolve(clientReport), () -> cachedItem.getBytes().length, "application/json", null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    @Nullable
    public ClientReport getClientReport(@NotNull ISerializer serializer) throws Exception {
        if (this.header == null || this.header.getType() != SentryItemType.ClientReport) {
            return null;
        }
        try (BufferedReader eventReader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(this.getData()), UTF_8));){
            ClientReport clientReport = serializer.deserialize(eventReader, ClientReport.class);
            return clientReport;
        }
    }

    public static SentryEnvelopeItem fromReplay(@NotNull ISerializer serializer, @NotNull ILogger logger, @NotNull SentryReplayEvent replayEvent, @Nullable ReplayRecording replayRecording, boolean cleanupReplayFolder) {
        File replayVideo = replayEvent.getVideoFile();
        CachedItem cachedItem = new CachedItem(() -> {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.ReplayVideo, () -> cachedItem.getBytes().length, null, null);
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    public static SentryEnvelopeItem fromLogs(@NotNull ISerializer serializer, @NotNull SentryLogEvents logEvents) {
        Objects.requireNonNull(serializer, "ISerializer is required.");
        Objects.requireNonNull(logEvents, "SentryLogEvents is required.");
        CachedItem cachedItem = new CachedItem(() -> {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
                byte[] byArray;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                    serializer.serialize(logEvents, writer);
                    byArray = stream.toByteArray();
                }
                return byArray;
            }
        });
        SentryEnvelopeItemHeader itemHeader = new SentryEnvelopeItemHeader(SentryItemType.Log, () -> cachedItem.getBytes().length, "application/vnd.sentry.items.log+json", null, null, null, (Integer)logEvents.getItems().size());
        return new SentryEnvelopeItem(itemHeader, () -> cachedItem.getBytes());
    }

    private static byte[] serializeToMsgpack(@NotNull Map<String, byte[]> map) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            baos.write((byte)(0x80 | map.size()));
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                byte[] keyBytes = entry.getKey().getBytes(UTF_8);
                int keyLength = keyBytes.length;
                baos.write(-39);
                baos.write((byte)keyLength);
                baos.write(keyBytes);
                byte[] valueBytes = entry.getValue();
                int valueLength = valueBytes.length;
                baos.write(-58);
                baos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(valueLength).array());
                baos.write(valueBytes);
            }
            Object object = baos.toByteArray();
            return object;
        }
    }

    private static class CachedItem {
        @Nullable
        private byte[] bytes;
        @Nullable
        private final Callable<byte[]> dataFactory;

        public CachedItem(@Nullable Callable<byte[]> dataFactory) {
            this.dataFactory = dataFactory;
        }

        @NotNull
        public byte[] getBytes() throws Exception {
            if (this.bytes == null && this.dataFactory != null) {
                this.bytes = this.dataFactory.call();
            }
            return CachedItem.orEmptyArray(this.bytes);
        }

        @NotNull
        private static byte[] orEmptyArray(@Nullable byte[] bytes) {
            return bytes != null ? bytes : new byte[]{};
        }
    }
}

