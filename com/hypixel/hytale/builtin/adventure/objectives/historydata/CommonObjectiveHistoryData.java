/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.historydata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import java.time.Instant;
import javax.annotation.Nonnull;

public abstract class CommonObjectiveHistoryData {
    public static final CodecMapCodec<CommonObjectiveHistoryData> CODEC = new CodecMapCodec("Type");
    public static final BuilderCodec<CommonObjectiveHistoryData> BASE_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(CommonObjectiveHistoryData.class).append(new KeyedCodec<String>("Id", Codec.STRING), (commonObjectiveHistoryData, s) -> {
        commonObjectiveHistoryData.id = s;
    }, commonObjectiveHistoryData -> commonObjectiveHistoryData.id).add()).append(new KeyedCodec<Integer>("TimesCompleted", Codec.INTEGER), (commonObjectiveHistoryData, integer) -> {
        commonObjectiveHistoryData.timesCompleted = integer;
    }, commonObjectiveHistoryData -> commonObjectiveHistoryData.timesCompleted).add()).append(new KeyedCodec<Long>("LastCompletionTimestamp", Codec.LONG), (o, i) -> {
        o.lastCompletionTimestamp = Instant.ofEpochMilli(i);
    }, o -> o.lastCompletionTimestamp == null ? null : Long.valueOf(o.lastCompletionTimestamp.toEpochMilli())).add()).append(new KeyedCodec<String>("Category", Codec.STRING), (commonObjectiveHistoryData, s) -> {
        commonObjectiveHistoryData.category = s;
    }, commonObjectiveHistoryData -> commonObjectiveHistoryData.category).add()).build();
    protected String id;
    protected int timesCompleted;
    protected Instant lastCompletionTimestamp;
    protected String category;

    public CommonObjectiveHistoryData(String id, String category) {
        this.id = id;
        this.timesCompleted = 1;
        this.lastCompletionTimestamp = Instant.now();
        this.category = category;
    }

    protected CommonObjectiveHistoryData() {
    }

    public String getId() {
        return this.id;
    }

    public int getTimesCompleted() {
        return this.timesCompleted;
    }

    public Instant getLastCompletionTimestamp() {
        return this.lastCompletionTimestamp;
    }

    public String getCategory() {
        return this.category;
    }

    protected void completed() {
        ++this.timesCompleted;
        this.lastCompletionTimestamp = Instant.now();
    }

    @Nonnull
    public String toString() {
        return "CommonObjectiveHistoryData{id='" + this.id + "', timesCompleted=" + this.timesCompleted + ", lastCompletionTimestamp=" + String.valueOf(this.lastCompletionTimestamp) + ", category='" + this.category + "'}";
    }
}

