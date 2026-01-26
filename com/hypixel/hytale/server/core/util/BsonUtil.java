/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.common.util.ExceptionUtil;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import io.netty.buffer.ByteBuf;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public class BsonUtil {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final JsonWriterSettings SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.STRICT).indent(true).newLineCharacters("\n").int64Converter((value, writer) -> writer.writeNumber(Long.toString(value))).build();
    private static final BsonDocumentCodec codec = new BsonDocumentCodec();
    private static final DecoderContext decoderContext = DecoderContext.builder().build();
    private static final EncoderContext encoderContext = EncoderContext.builder().build();
    public static final BsonDocumentCodec BSON_DOCUMENT_CODEC = new BsonDocumentCodec();

    public static byte[] writeToBytes(@Nullable BsonDocument document) {
        if (document == null) {
            return ArrayUtil.EMPTY_BYTE_ARRAY;
        }
        try (BasicOutputBuffer buffer = new BasicOutputBuffer();){
            codec.encode((BsonWriter)new BsonBinaryWriter(buffer), document, encoderContext);
            byte[] byArray = buffer.toByteArray();
            return byArray;
        }
    }

    public static BsonDocument readFromBytes(@Nullable byte[] buf) {
        if (buf == null || buf.length == 0) {
            return null;
        }
        return codec.decode(new BsonBinaryReader(ByteBuffer.wrap(buf)), decoderContext);
    }

    public static BsonDocument readFromBuffer(@Nullable ByteBuffer buf) {
        if (buf == null || !buf.hasRemaining()) {
            return null;
        }
        return codec.decode(new BsonBinaryReader(buf), decoderContext);
    }

    public static BsonDocument readFromBinaryStream(@Nonnull ByteBuf buf) {
        return BsonUtil.readFromBytes(ByteBufUtil.readByteArray(buf));
    }

    public static void writeToBinaryStream(@Nonnull ByteBuf buf, BsonDocument doc) {
        ByteBufUtil.writeByteArray(buf, BsonUtil.writeToBytes(doc));
    }

    @Nonnull
    public static CompletableFuture<Void> writeDocumentBytes(@Nonnull Path file, BsonDocument document) {
        try {
            byte[] bytes;
            if (Files.isRegularFile(file, new LinkOption[0])) {
                Path resolve = file.resolveSibling(String.valueOf(file.getFileName()) + ".bak");
                Files.move(file, resolve, StandardCopyOption.REPLACE_EXISTING);
            }
            try (BasicOutputBuffer bob = new BasicOutputBuffer();){
                codec.encode((BsonWriter)new BsonBinaryWriter(bob), document, encoderContext);
                bytes = bob.toByteArray();
            }
            return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> Files.write(file, bytes, new OpenOption[0])));
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Nonnull
    public static CompletableFuture<Void> writeDocument(@Nonnull Path file, BsonDocument document) {
        return BsonUtil.writeDocument(file, document, true);
    }

    @Nonnull
    public static CompletableFuture<Void> writeDocument(@Nonnull Path file, BsonDocument document, boolean backup) {
        try {
            Path parent = PathUtil.getParent(file);
            if (!Files.exists(parent, new LinkOption[0])) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            if (backup && Files.isRegularFile(file, new LinkOption[0])) {
                Path resolve = file.resolveSibling(String.valueOf(file.getFileName()) + ".bak");
                Files.move(file, resolve, StandardCopyOption.REPLACE_EXISTING);
            }
            String json = BsonUtil.toJson(document);
            return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> Files.writeString(file, (CharSequence)json, new OpenOption[0])));
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Nonnull
    public static CompletableFuture<BsonDocument> readDocument(@Nonnull Path file) {
        return BsonUtil.readDocument(file, true);
    }

    @Nonnull
    public static CompletableFuture<BsonDocument> readDocument(@Nonnull Path file, boolean backup) {
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(file, BasicFileAttributes.class, new LinkOption[0]);
        }
        catch (IOException ignored) {
            if (backup) {
                return BsonUtil.readDocumentBak(file);
            }
            return CompletableFuture.completedFuture(null);
        }
        if (attributes.size() == 0L) {
            LOGGER.at(Level.WARNING).log("Error loading file %s, file was found to be entirely empty", file);
            if (backup) {
                return BsonUtil.readDocumentBak(file);
            }
            return CompletableFuture.completedFuture(null);
        }
        CompletionStage future = CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> Files.readString(file))).thenApply(BsonDocument::parse);
        return backup ? ((CompletableFuture)future).exceptionallyCompose(t -> BsonUtil.readDocumentBak(file)) : future;
    }

    @Nullable
    public static BsonDocument readDocumentNow(@Nonnull Path file) {
        String contentsString;
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(file, BasicFileAttributes.class, new LinkOption[0]);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log(ExceptionUtil.toStringWithStack(e));
            return null;
        }
        if (attributes.size() == 0L) {
            return null;
        }
        try {
            contentsString = Files.readString(file);
        }
        catch (IOException e) {
            return null;
        }
        return BsonDocument.parse(contentsString);
    }

    @Nonnull
    public static CompletableFuture<BsonDocument> readDocumentBak(@Nonnull Path fileOrig) {
        BasicFileAttributes attributes;
        Path file = fileOrig.resolveSibling(String.valueOf(fileOrig.getFileName()) + ".bak");
        try {
            attributes = Files.readAttributes(file, BasicFileAttributes.class, new LinkOption[0]);
        }
        catch (IOException ignored) {
            return CompletableFuture.completedFuture(null);
        }
        if (attributes.size() == 0L) {
            LOGGER.at(Level.WARNING).log("Error loading backup file %s, file was found to be entirely empty", file);
            return CompletableFuture.completedFuture(null);
        }
        LOGGER.at(Level.WARNING).log("Loading %s backup file for %s!", (Object)file, (Object)fileOrig);
        return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> Files.readString(file))).thenApply(BsonDocument::parse);
    }

    public static BsonValue translateJsonToBson(@Nonnull JsonElement element) {
        if (element.isJsonObject()) {
            return BsonDocument.parse(element.toString());
        }
        return new BsonString(element.getAsString());
    }

    public static JsonElement translateBsonToJson(BsonDocument value) {
        JsonElement jsonElement;
        StringWriter writer = new StringWriter();
        try {
            codec.encode((BsonWriter)new JsonWriter(writer, SETTINGS), value, encoderContext);
            jsonElement = JsonParser.parseString(writer.toString());
        }
        catch (Throwable throwable) {
            try {
                try {
                    writer.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writer.close();
        return jsonElement;
    }

    public static String toJson(BsonDocument document) {
        StringWriter writer = new StringWriter();
        BSON_DOCUMENT_CODEC.encode((BsonWriter)new JsonWriter(writer, SETTINGS), document, encoderContext);
        return writer.toString();
    }

    public static <T> void writeSync(@Nonnull Path path, @Nonnull Codec<T> codec, T value, @Nonnull HytaleLogger logger) throws IOException {
        Path parent = PathUtil.getParent(path);
        if (!Files.exists(parent, new LinkOption[0])) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        if (Files.isRegularFile(path, new LinkOption[0])) {
            Path resolve = path.resolveSibling(String.valueOf(path.getFileName()) + ".bak");
            Files.move(path, resolve, StandardCopyOption.REPLACE_EXISTING);
        }
        ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
        BsonValue bsonValue = codec.encode(value, extraInfo);
        extraInfo.getValidationResults().logOrThrowValidatorExceptions(logger);
        BsonDocument document = bsonValue.asDocument();
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);){
            BSON_DOCUMENT_CODEC.encode((BsonWriter)new JsonWriter(writer, SETTINGS), document, encoderContext);
        }
    }
}

