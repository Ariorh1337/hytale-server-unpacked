/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.shop.barter;

import com.hypixel.hytale.builtin.adventure.shop.barter.BarterShopAsset;
import com.hypixel.hytale.builtin.adventure.shop.barter.BarterTrade;
import com.hypixel.hytale.builtin.adventure.shop.barter.RefreshInterval;
import com.hypixel.hytale.builtin.adventure.shop.barter.TradeSlot;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class BarterShopState {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static BarterShopState instance;
    private static Path saveDirectory;
    public static final BuilderCodec<ShopInstanceState> SHOP_INSTANCE_CODEC;
    public static final BuilderCodec<BarterShopState> CODEC;
    private final Map<String, ShopInstanceState> shopStates = new ConcurrentHashMap<String, ShopInstanceState>();

    public static void initialize(@Nonnull Path dataDirectory) {
        saveDirectory = dataDirectory;
        BarterShopState.load();
    }

    @Nonnull
    public static BarterShopState get() {
        if (instance == null) {
            instance = new BarterShopState();
        }
        return instance;
    }

    public static void load() {
        if (saveDirectory == null) {
            LOGGER.at(Level.WARNING).log("Cannot load barter shop state: save directory not set");
            instance = new BarterShopState();
            return;
        }
        Path file = saveDirectory.resolve("barter_shop_state.json");
        if (!Files.exists(file, new LinkOption[0])) {
            LOGGER.at(Level.INFO).log("No saved barter shop state found, starting fresh");
            instance = new BarterShopState();
            return;
        }
        try {
            BsonDocument document = BsonUtil.readDocumentNow(file);
            if (document != null) {
                ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                instance = CODEC.decode(document, extraInfo);
                extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
                LOGGER.at(Level.INFO).log("Loaded barter shop state with %d shops", BarterShopState.instance.shopStates.size());
            } else {
                instance = new BarterShopState();
            }
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to load barter shop state, starting fresh");
            instance = new BarterShopState();
        }
    }

    public static void save() {
        if (saveDirectory == null || instance == null) {
            return;
        }
        try {
            if (!Files.exists(saveDirectory, new LinkOption[0])) {
                Files.createDirectories(saveDirectory, new FileAttribute[0]);
            }
            Path file = saveDirectory.resolve("barter_shop_state.json");
            BsonUtil.writeSync(file, CODEC, instance, LOGGER);
            LOGGER.at(Level.FINE).log("Saved barter shop state");
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to save barter shop state");
        }
    }

    public static void shutdown() {
        BarterShopState.save();
        instance = null;
    }

    private static Instant calculateNextScheduledRestock(@Nonnull Instant gameTime, int intervalDays, int restockHour) {
        boolean isTodayRestockDay;
        LocalDateTime dateTime = LocalDateTime.ofInstant(gameTime, ZoneOffset.UTC);
        long daysSinceEpoch = Duration.between(WorldTimeResource.ZERO_YEAR, gameTime).toDays();
        long currentCycle = daysSinceEpoch / (long)intervalDays;
        long nextRestockDaySinceEpoch = (currentCycle + 1L) * (long)intervalDays;
        boolean bl = isTodayRestockDay = daysSinceEpoch % (long)intervalDays == 0L;
        if (isTodayRestockDay && dateTime.getHour() < restockHour) {
            nextRestockDaySinceEpoch = daysSinceEpoch;
        }
        Instant nextRestockInstant = WorldTimeResource.ZERO_YEAR.plus(Duration.ofDays(nextRestockDaySinceEpoch)).plus(Duration.ofHours(restockHour));
        return nextRestockInstant;
    }

    @Nonnull
    public ShopInstanceState getOrCreateShopState(BarterShopAsset asset, @Nonnull Instant gameTime) {
        return this.shopStates.computeIfAbsent(asset.getId(), id -> {
            ShopInstanceState state = new ShopInstanceState();
            state.resetStockAndResolve(asset);
            RefreshInterval interval = asset.getRefreshInterval();
            if (interval != null) {
                state.setNextRefreshTime(BarterShopState.calculateNextScheduledRestock(gameTime, interval.getDays(), asset.getRestockHour()));
            }
            return state;
        });
    }

    public void checkRefresh(BarterShopAsset asset, @Nonnull Instant gameTime) {
        RefreshInterval interval = asset.getRefreshInterval();
        if (interval == null) {
            return;
        }
        ShopInstanceState state = this.getOrCreateShopState(asset, gameTime);
        Instant nextRefresh = state.getNextRefreshTime();
        if (nextRefresh == null) {
            state.setNextRefreshTime(BarterShopState.calculateNextScheduledRestock(gameTime, interval.getDays(), asset.getRestockHour()));
            BarterShopState.save();
            return;
        }
        if (!gameTime.isBefore(nextRefresh)) {
            state.resetStockAndResolve(asset);
            state.setNextRefreshTime(BarterShopState.calculateNextScheduledRestock(gameTime, interval.getDays(), asset.getRestockHour()));
            BarterShopState.save();
        }
    }

    public int[] getStockArray(BarterShopAsset asset, @Nonnull Instant gameTime) {
        this.checkRefresh(asset, gameTime);
        ShopInstanceState state = this.getOrCreateShopState(asset, gameTime);
        if (state.expandStockIfNeeded(asset)) {
            BarterShopState.save();
        }
        return (int[])state.getCurrentStock().clone();
    }

    @Nonnull
    public BarterTrade[] getResolvedTrades(BarterShopAsset asset, @Nonnull Instant gameTime) {
        this.checkRefresh(asset, gameTime);
        ShopInstanceState state = this.getOrCreateShopState(asset, gameTime);
        return state.getResolvedTrades(asset);
    }

    public boolean executeTrade(BarterShopAsset asset, int tradeIndex, int quantity, @Nonnull Instant gameTime) {
        this.checkRefresh(asset, gameTime);
        ShopInstanceState state = this.getOrCreateShopState(asset, gameTime);
        boolean success = state.decrementStock(tradeIndex, quantity);
        if (success) {
            BarterShopState.save();
        }
        return success;
    }

    static {
        SHOP_INSTANCE_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ShopInstanceState.class, ShopInstanceState::new).append(new KeyedCodec<int[]>("Stock", Codec.INT_ARRAY), (state, stock) -> {
            state.currentStock = stock;
        }, state -> state.currentStock).add()).append(new KeyedCodec("NextRefresh", Codec.INSTANT, true), (state, instant) -> {
            state.nextRefreshTime = instant;
        }, state -> state.nextRefreshTime).add()).append(new KeyedCodec<Long>("ResolveSeed", Codec.LONG, true), (state, seed) -> {
            state.resolveSeed = seed;
        }, state -> state.resolveSeed).add()).build();
        CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BarterShopState.class, BarterShopState::new).append(new KeyedCodec("Shops", new MapCodec<ShopInstanceState, Object2ObjectOpenHashMap>(SHOP_INSTANCE_CODEC, Object2ObjectOpenHashMap::new, false)), (state, shops) -> state.shopStates.putAll((Map<String, ShopInstanceState>)shops), state -> new Object2ObjectOpenHashMap<String, ShopInstanceState>(state.shopStates)).add()).build();
    }

    public static class ShopInstanceState {
        private int[] currentStock = new int[0];
        private Instant nextRefreshTime;
        private Long resolveSeed;
        private transient BarterTrade[] resolvedTrades;

        public ShopInstanceState() {
        }

        public ShopInstanceState(int tradeCount) {
            this.currentStock = new int[tradeCount];
            this.nextRefreshTime = null;
        }

        public int[] getCurrentStock() {
            return this.currentStock;
        }

        @Nullable
        public Instant getNextRefreshTime() {
            return this.nextRefreshTime;
        }

        public void setNextRefreshTime(Instant time) {
            this.nextRefreshTime = time;
        }

        @Nullable
        public Long getResolveSeed() {
            return this.resolveSeed;
        }

        public void setResolveSeed(Long seed) {
            this.resolveSeed = seed;
        }

        @Nonnull
        public BarterTrade[] getResolvedTrades(@Nonnull BarterShopAsset asset) {
            if (!asset.hasTradeSlots()) {
                return asset.getTrades() != null ? asset.getTrades() : new BarterTrade[]{};
            }
            if (this.resolvedTrades != null) {
                return this.resolvedTrades;
            }
            if (this.resolveSeed == null) {
                this.resolveSeed = ThreadLocalRandom.current().nextLong();
            }
            this.resolvedTrades = ShopInstanceState.resolveTradeSlots(asset, this.resolveSeed);
            return this.resolvedTrades;
        }

        @Nonnull
        private static BarterTrade[] resolveTradeSlots(@Nonnull BarterShopAsset asset, long seed) {
            TradeSlot[] slots = asset.getTradeSlots();
            if (slots == null || slots.length == 0) {
                return new BarterTrade[0];
            }
            Random random = new Random(seed);
            ObjectArrayList result = new ObjectArrayList();
            for (TradeSlot slot : slots) {
                result.addAll(slot.resolve(random));
            }
            return result.toArray(new BarterTrade[0]);
        }

        public void resetStockAndResolve(@Nonnull BarterShopAsset asset) {
            if (asset.hasTradeSlots()) {
                this.resolveSeed = ThreadLocalRandom.current().nextLong();
                this.resolvedTrades = ShopInstanceState.resolveTradeSlots(asset, this.resolveSeed);
            } else {
                this.resolvedTrades = null;
            }
            BarterTrade[] trades = this.getResolvedTrades(asset);
            this.currentStock = new int[trades.length];
            for (int i = 0; i < trades.length; ++i) {
                this.currentStock[i] = trades[i].getMaxStock();
            }
        }

        public void resetStock(BarterShopAsset asset) {
            BarterTrade[] trades = this.getResolvedTrades(asset);
            if (this.currentStock.length != trades.length) {
                this.currentStock = new int[trades.length];
            }
            for (int i = 0; i < trades.length; ++i) {
                this.currentStock[i] = trades[i].getMaxStock();
            }
        }

        public boolean expandStockIfNeeded(BarterShopAsset asset) {
            BarterTrade[] trades = this.getResolvedTrades(asset);
            if (this.currentStock.length >= trades.length) {
                return false;
            }
            int[] newStock = new int[trades.length];
            System.arraycopy(this.currentStock, 0, newStock, 0, this.currentStock.length);
            for (int i = this.currentStock.length; i < trades.length; ++i) {
                newStock[i] = trades[i].getMaxStock();
            }
            this.currentStock = newStock;
            return true;
        }

        public boolean hasStock(int tradeIndex, int quantity) {
            if (tradeIndex < 0 || tradeIndex >= this.currentStock.length) {
                return false;
            }
            return this.currentStock[tradeIndex] >= quantity;
        }

        public boolean decrementStock(int tradeIndex, int quantity) {
            if (!this.hasStock(tradeIndex, quantity)) {
                return false;
            }
            int n = tradeIndex;
            this.currentStock[n] = this.currentStock[n] - quantity;
            return true;
        }

        public int getStock(int tradeIndex) {
            if (tradeIndex < 0 || tradeIndex >= this.currentStock.length) {
                return 0;
            }
            return this.currentStock[tradeIndex];
        }
    }
}

