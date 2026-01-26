/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.weather.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ColorAlpha;
import com.hypixel.hytale.protocol.NearFar;
import com.hypixel.hytale.protocol.WeatherParticle;
import com.hypixel.hytale.server.core.asset.type.weather.config.Cloud;
import com.hypixel.hytale.server.core.asset.type.weather.config.DayTexture;
import com.hypixel.hytale.server.core.asset.type.weather.config.FogOptions;
import com.hypixel.hytale.server.core.asset.type.weather.config.TimeColor;
import com.hypixel.hytale.server.core.asset.type.weather.config.TimeColorAlpha;
import com.hypixel.hytale.server.core.asset.type.weather.config.TimeFloat;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class Weather
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, Weather>>,
NetworkSerializable<com.hypixel.hytale.protocol.Weather> {
    public static final BuilderCodec<WeatherParticle> PARTICLE_CODEC;
    public static final AssetBuilderCodec<String, Weather> CODEC;
    public static final ValidatorCache<String> VALIDATOR_CACHE;
    private static AssetStore<String, Weather, IndexedLookupTableAssetMap<String, Weather>> ASSET_STORE;
    public static final float[] DEFAULT_FOG_DISTANCE;
    public static final int UNKNOWN_ID = 0;
    public static final Weather UNKNOWN;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected DayTexture[] moons;
    protected Cloud[] clouds;
    protected TimeFloat[] sunlightDampingMultiplier;
    protected TimeColor[] sunlightColors;
    protected TimeColor[] sunColors;
    protected TimeColorAlpha[] moonColors;
    protected TimeColorAlpha[] sunGlowColors;
    protected TimeColorAlpha[] moonGlowColors;
    protected TimeFloat[] sunScales;
    protected TimeFloat[] moonScales;
    protected TimeColorAlpha[] skyTopColors;
    protected TimeColorAlpha[] skyBottomColors;
    protected TimeColorAlpha[] skySunsetColors;
    protected TimeColor[] fogColors;
    protected TimeFloat[] fogHeightFalloffs;
    protected TimeFloat[] fogDensities;
    protected TimeColor[] waterTints;
    protected float[] fogDistance = DEFAULT_FOG_DISTANCE;
    protected FogOptions fogOptions;
    protected String screenEffect;
    protected TimeColorAlpha[] screenEffectColors;
    protected TimeColor[] colorFilters;
    protected String stars;
    protected WeatherParticle particle;
    private SoftReference<com.hypixel.hytale.protocol.Weather> cachedPacket;

    public static AssetStore<String, Weather, IndexedLookupTableAssetMap<String, Weather>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Weather.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, Weather> getAssetMap() {
        return Weather.getAssetStore().getAssetMap();
    }

    public Weather(String id, DayTexture[] moons, Cloud[] clouds, TimeFloat[] sunlightDampingMultiplier, TimeColor[] sunlightColors, TimeColor[] sunColors, TimeColorAlpha[] moonColors, TimeColorAlpha[] sunGlowColors, TimeColorAlpha[] moonGlowColors, TimeFloat[] sunScales, TimeFloat[] moonScales, TimeColorAlpha[] skyTopColors, TimeColorAlpha[] skyBottomColors, TimeColorAlpha[] skySunsetColors, TimeColor[] fogColors, TimeFloat[] fogHeightFalloffs, TimeFloat[] fogDensities, TimeColor[] waterTints, float[] fogDistance, FogOptions fogOptions, String screenEffect, TimeColorAlpha[] screenEffectColors, TimeColor[] colorFilters, String stars, WeatherParticle particle) {
        this.id = id;
        this.moons = moons;
        this.clouds = clouds;
        this.sunlightDampingMultiplier = sunlightDampingMultiplier;
        this.sunlightColors = sunlightColors;
        this.sunColors = sunColors;
        this.moonColors = moonColors;
        this.sunGlowColors = sunGlowColors;
        this.moonGlowColors = moonGlowColors;
        this.sunScales = sunScales;
        this.moonScales = moonScales;
        this.skyTopColors = skyTopColors;
        this.skyBottomColors = skyBottomColors;
        this.skySunsetColors = skySunsetColors;
        this.fogColors = fogColors;
        this.fogHeightFalloffs = fogHeightFalloffs;
        this.fogDensities = fogDensities;
        this.waterTints = waterTints;
        this.fogDistance = fogDistance;
        this.fogOptions = fogOptions;
        this.screenEffect = screenEffect;
        this.screenEffectColors = screenEffectColors;
        this.colorFilters = colorFilters;
        this.stars = stars;
        this.particle = particle;
    }

    public Weather(String id) {
        this.id = id;
    }

    protected Weather() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.Weather toPacket() {
        com.hypixel.hytale.protocol.Weather cached;
        com.hypixel.hytale.protocol.Weather weather = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.Weather packet = new com.hypixel.hytale.protocol.Weather();
        packet.id = this.id;
        if (this.moons != null && this.moons.length > 0) {
            packet.moons = Weather.toStringMap(this.moons);
        }
        if (this.clouds != null && this.clouds.length > 0) {
            packet.clouds = ArrayUtil.copyAndMutate(this.clouds, Cloud::toPacket, com.hypixel.hytale.protocol.Cloud[]::new);
        }
        if (this.sunlightDampingMultiplier != null && this.sunlightDampingMultiplier.length > 0) {
            packet.sunlightDampingMultiplier = Weather.toFloatMap(this.sunlightDampingMultiplier);
        }
        if (this.sunlightColors != null && this.sunlightColors.length > 0) {
            packet.sunlightColors = Weather.toColorMap(this.sunlightColors);
        }
        if (this.sunColors != null && this.sunColors.length > 0) {
            packet.sunColors = Weather.toColorMap(this.sunColors);
        }
        if (this.sunGlowColors != null && this.sunGlowColors.length > 0) {
            packet.sunGlowColors = Weather.toColorAlphaMap(this.sunGlowColors);
        }
        if (this.sunScales != null && this.sunScales.length > 0) {
            packet.sunScales = Weather.toFloatMap(this.sunScales);
        }
        if (this.moonColors != null && this.moonColors.length > 0) {
            packet.moonColors = Weather.toColorAlphaMap(this.moonColors);
        }
        if (this.moonGlowColors != null && this.moonGlowColors.length > 0) {
            packet.moonGlowColors = Weather.toColorAlphaMap(this.moonGlowColors);
        }
        if (this.moonScales != null && this.moonScales.length > 0) {
            packet.moonScales = Weather.toFloatMap(this.moonScales);
        }
        if (this.skyTopColors != null && this.skyTopColors.length > 0) {
            packet.skyTopColors = Weather.toColorAlphaMap(this.skyTopColors);
        }
        if (this.skyBottomColors != null && this.skyBottomColors.length > 0) {
            packet.skyBottomColors = Weather.toColorAlphaMap(this.skyBottomColors);
        }
        if (this.skySunsetColors != null && this.skySunsetColors.length > 0) {
            packet.skySunsetColors = Weather.toColorAlphaMap(this.skySunsetColors);
        }
        if (this.fogColors != null && this.fogColors.length > 0) {
            packet.fogColors = Weather.toColorMap(this.fogColors);
        }
        if (this.fogHeightFalloffs != null && this.fogHeightFalloffs.length > 0) {
            packet.fogHeightFalloffs = Weather.toFloatMap(this.fogHeightFalloffs);
        }
        if (this.fogDensities != null && this.fogDensities.length > 0) {
            packet.fogDensities = Weather.toFloatMap(this.fogDensities);
        }
        packet.screenEffect = this.screenEffect;
        if (this.screenEffectColors != null && this.screenEffectColors.length > 0) {
            packet.screenEffectColors = Weather.toColorAlphaMap(this.screenEffectColors);
        }
        if (this.colorFilters != null && this.colorFilters.length > 0) {
            packet.colorFilters = Weather.toColorMap(this.colorFilters);
        }
        if (this.waterTints != null && this.waterTints.length > 0) {
            packet.waterTints = Weather.toColorMap(this.waterTints);
        }
        if (this.fogOptions != null) {
            packet.fogOptions = this.fogOptions.toPacket();
        }
        packet.fog = new NearFar(this.fogDistance[0], this.fogDistance[1]);
        packet.stars = this.stars;
        if (this.particle != null) {
            packet.particle = this.particle;
        }
        if (this.data != null) {
            IntSet tags = this.data.getExpandedTagIndexes();
            packet.tagIndexes = tags.toIntArray();
        }
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.Weather>(packet);
        return packet;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public DayTexture[] getMoons() {
        return this.moons;
    }

    public Cloud[] getClouds() {
        return this.clouds;
    }

    public TimeFloat[] getSunlightDampingMultiplier() {
        return this.sunlightDampingMultiplier;
    }

    public TimeColor[] getSunlightColors() {
        return this.sunlightColors;
    }

    public TimeColor[] getSunColors() {
        return this.sunColors;
    }

    public TimeColorAlpha[] getMoonColors() {
        return this.moonColors;
    }

    public TimeColorAlpha[] getSunGlowColors() {
        return this.sunGlowColors;
    }

    public TimeColorAlpha[] getMoonGlowColors() {
        return this.moonGlowColors;
    }

    public TimeFloat[] getSunScales() {
        return this.sunScales;
    }

    public TimeFloat[] getMoonScales() {
        return this.moonScales;
    }

    public TimeColorAlpha[] getSkyTopColors() {
        return this.skyTopColors;
    }

    public TimeColorAlpha[] getSkyBottomColors() {
        return this.skyBottomColors;
    }

    public TimeColorAlpha[] getSkySunsetColors() {
        return this.skySunsetColors;
    }

    public TimeColor[] getFogColors() {
        return this.fogColors;
    }

    public TimeFloat[] getFogHeightFalloffs() {
        return this.fogHeightFalloffs;
    }

    public TimeFloat[] getFogDensities() {
        return this.fogDensities;
    }

    public TimeColor[] getWaterTints() {
        return this.waterTints;
    }

    public float[] getFogDistance() {
        return this.fogDistance;
    }

    public FogOptions getFogOptions() {
        return this.fogOptions;
    }

    public String getScreenEffect() {
        return this.screenEffect;
    }

    public TimeColorAlpha[] getScreenEffectColors() {
        return this.screenEffectColors;
    }

    public TimeColor[] getColorFilters() {
        return this.colorFilters;
    }

    public String getStars() {
        return this.stars;
    }

    public WeatherParticle getParticle() {
        return this.particle;
    }

    @Nonnull
    public String toString() {
        return "Weather{id='" + this.id + "', moons=" + Arrays.toString(this.moons) + ", clouds=" + Arrays.toString(this.clouds) + ", sunlightDampingMultiplier=" + Arrays.toString(this.sunlightDampingMultiplier) + ", sunlightColors=" + Arrays.toString(this.sunlightColors) + ", sunColors=" + Arrays.toString(this.sunColors) + ", sunGlowColors=" + Arrays.toString(this.sunGlowColors) + ", sunScales=" + Arrays.toString(this.sunScales) + ", moonColors=" + Arrays.toString(this.moonColors) + ", moonGlowColors=" + Arrays.toString(this.moonGlowColors) + ", moonScales=" + Arrays.toString(this.moonScales) + ", skyTopColors=" + Arrays.toString(this.skyTopColors) + ", skyBottomColors=" + Arrays.toString(this.skyBottomColors) + ", skySunsetColors=" + Arrays.toString(this.skySunsetColors) + ", fogColors=" + Arrays.toString(this.fogColors) + ", fogHeightFalloffs=" + Arrays.toString(this.fogHeightFalloffs) + ", fogDensities=" + Arrays.toString(this.fogDensities) + ", fogDistance=" + Arrays.toString(this.fogDistance) + ", fogOptions=" + String.valueOf(this.fogOptions) + ", screenEffect=" + this.screenEffect + ", screenEffectColors=" + Arrays.toString(this.screenEffectColors) + ", colorFilters=" + Arrays.toString(this.colorFilters) + ", waterTints=" + Arrays.toString(this.waterTints) + ", stars=" + this.stars + ", particle=" + String.valueOf(this.particle) + "}";
    }

    @Nonnull
    public static Map<Integer, String> toStringMap(@Nonnull DayTexture[] dayTexture) {
        return Arrays.stream(dayTexture).collect(Collectors.toMap(DayTexture::getDay, DayTexture::getTexture));
    }

    @Nonnull
    public static Map<Float, Float> toFloatMap(@Nonnull TimeFloat[] timeFloat) {
        return Arrays.stream(timeFloat).collect(Collectors.toMap(TimeFloat::getHour, TimeFloat::getValue));
    }

    @Nonnull
    public static Map<Float, Color> toColorMap(@Nonnull TimeColor[] timeColor) {
        return Arrays.stream(timeColor).collect(Collectors.toMap(TimeColor::getHour, TimeColor::getColor));
    }

    @Nonnull
    public static Map<Float, ColorAlpha> toColorAlphaMap(@Nonnull TimeColorAlpha[] timeColorAlpha) {
        return Arrays.stream(timeColorAlpha).collect(Collectors.toMap(TimeColorAlpha::getHour, TimeColorAlpha::getColor));
    }

    private static /* synthetic */ void lambda$static$85(Weather weather, Weather parent) {
        weather.clouds = parent.clouds;
    }

    private static /* synthetic */ Cloud[] lambda$static$84(Weather weather) {
        return weather.clouds;
    }

    private static /* synthetic */ void lambda$static$83(Weather weather, Cloud[] o) {
        weather.clouds = o;
    }

    private static /* synthetic */ Cloud[] lambda$CODEC$6(int x$0) {
        return new Cloud[x$0];
    }

    private static /* synthetic */ void lambda$static$82(Weather weather, Weather parent) {
        weather.moons = parent.moons;
    }

    private static /* synthetic */ DayTexture[] lambda$static$81(Weather weather) {
        return weather.moons;
    }

    private static /* synthetic */ void lambda$static$80(Weather weather, DayTexture[] o) {
        weather.moons = o;
    }

    private static /* synthetic */ DayTexture[] lambda$CODEC$5(int x$0) {
        return new DayTexture[x$0];
    }

    private static /* synthetic */ void lambda$static$79(Weather weather, Weather parent) {
        weather.colorFilters = parent.colorFilters;
    }

    private static /* synthetic */ TimeColor[] lambda$static$78(Weather weather) {
        return weather.colorFilters;
    }

    private static /* synthetic */ void lambda$static$77(Weather weather, TimeColor[] o) {
        weather.colorFilters = o;
    }

    private static /* synthetic */ void lambda$static$76(Weather weather, Weather parent) {
        weather.waterTints = parent.waterTints;
    }

    private static /* synthetic */ TimeColor[] lambda$static$75(Weather weather) {
        return weather.waterTints;
    }

    private static /* synthetic */ void lambda$static$74(Weather weather, TimeColor[] o) {
        weather.waterTints = o;
    }

    private static /* synthetic */ void lambda$static$73(Weather weather, Weather parent) {
        weather.fogDensities = parent.fogDensities;
    }

    private static /* synthetic */ TimeFloat[] lambda$static$72(Weather weather) {
        return weather.fogDensities;
    }

    private static /* synthetic */ void lambda$static$71(Weather weather, TimeFloat[] o) {
        weather.fogDensities = o;
    }

    private static /* synthetic */ TimeFloat[] lambda$CODEC$4(int x$0) {
        return new TimeFloat[x$0];
    }

    private static /* synthetic */ void lambda$static$70(Weather weather, Weather parent) {
        weather.fogHeightFalloffs = parent.fogHeightFalloffs;
    }

    private static /* synthetic */ TimeFloat[] lambda$static$69(Weather weather) {
        return weather.fogHeightFalloffs;
    }

    private static /* synthetic */ void lambda$static$68(Weather weather, TimeFloat[] o) {
        weather.fogHeightFalloffs = o;
    }

    private static /* synthetic */ TimeFloat[] lambda$CODEC$3(int x$0) {
        return new TimeFloat[x$0];
    }

    private static /* synthetic */ void lambda$static$67(Weather weather, Weather parent) {
        weather.fogColors = parent.fogColors;
    }

    private static /* synthetic */ TimeColor[] lambda$static$66(Weather weather) {
        return weather.fogColors;
    }

    private static /* synthetic */ void lambda$static$65(Weather weather, TimeColor[] o) {
        weather.fogColors = o;
    }

    private static /* synthetic */ void lambda$static$64(Weather weather, Weather parent) {
        weather.skySunsetColors = parent.skySunsetColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$63(Weather weather) {
        return weather.skySunsetColors;
    }

    private static /* synthetic */ void lambda$static$62(Weather weather, TimeColorAlpha[] o) {
        weather.skySunsetColors = o;
    }

    private static /* synthetic */ void lambda$static$61(Weather weather, Weather parent) {
        weather.skyBottomColors = parent.skyBottomColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$60(Weather weather) {
        return weather.skyBottomColors;
    }

    private static /* synthetic */ void lambda$static$59(Weather weather, TimeColorAlpha[] o) {
        weather.skyBottomColors = o;
    }

    private static /* synthetic */ void lambda$static$58(Weather weather, Weather parent) {
        weather.skyTopColors = parent.skyTopColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$57(Weather weather) {
        return weather.skyTopColors;
    }

    private static /* synthetic */ void lambda$static$56(Weather weather, TimeColorAlpha[] o) {
        weather.skyTopColors = o;
    }

    private static /* synthetic */ void lambda$static$55(Weather weather, Weather parent) {
        weather.moonScales = parent.moonScales;
    }

    private static /* synthetic */ TimeFloat[] lambda$static$54(Weather weather) {
        return weather.moonScales;
    }

    private static /* synthetic */ void lambda$static$53(Weather weather, TimeFloat[] o) {
        weather.moonScales = o;
    }

    private static /* synthetic */ TimeFloat[] lambda$CODEC$2(int x$0) {
        return new TimeFloat[x$0];
    }

    private static /* synthetic */ void lambda$static$52(Weather weather, Weather parent) {
        weather.sunScales = parent.sunScales;
    }

    private static /* synthetic */ TimeFloat[] lambda$static$51(Weather weather) {
        return weather.sunScales;
    }

    private static /* synthetic */ void lambda$static$50(Weather weather, TimeFloat[] o) {
        weather.sunScales = o;
    }

    private static /* synthetic */ TimeFloat[] lambda$CODEC$1(int x$0) {
        return new TimeFloat[x$0];
    }

    private static /* synthetic */ void lambda$static$49(Weather weather, Weather parent) {
        weather.moonGlowColors = parent.moonGlowColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$48(Weather weather) {
        return weather.moonGlowColors;
    }

    private static /* synthetic */ void lambda$static$47(Weather weather, TimeColorAlpha[] o) {
        weather.moonGlowColors = o;
    }

    private static /* synthetic */ void lambda$static$46(Weather weather, Weather parent) {
        weather.sunGlowColors = parent.sunGlowColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$45(Weather weather) {
        return weather.sunGlowColors;
    }

    private static /* synthetic */ void lambda$static$44(Weather weather, TimeColorAlpha[] o) {
        weather.sunGlowColors = o;
    }

    private static /* synthetic */ void lambda$static$43(Weather weather, Weather parent) {
        weather.moonColors = parent.moonColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$42(Weather weather) {
        return weather.moonColors;
    }

    private static /* synthetic */ void lambda$static$41(Weather weather, TimeColorAlpha[] o) {
        weather.moonColors = o;
    }

    private static /* synthetic */ void lambda$static$40(Weather weather, Weather parent) {
        weather.sunColors = parent.sunColors;
    }

    private static /* synthetic */ TimeColor[] lambda$static$39(Weather weather) {
        return weather.sunColors;
    }

    private static /* synthetic */ void lambda$static$38(Weather weather, TimeColor[] o) {
        weather.sunColors = o;
    }

    private static /* synthetic */ void lambda$static$37(Weather weather, Weather parent) {
        weather.sunlightColors = parent.sunlightColors;
    }

    private static /* synthetic */ TimeColor[] lambda$static$36(Weather weather) {
        return weather.sunlightColors;
    }

    private static /* synthetic */ void lambda$static$35(Weather weather, TimeColor[] o) {
        weather.sunlightColors = o;
    }

    private static /* synthetic */ void lambda$static$34(Weather weather, Weather parent) {
        weather.sunlightDampingMultiplier = parent.sunlightDampingMultiplier;
    }

    private static /* synthetic */ TimeFloat[] lambda$static$33(Weather weather) {
        return weather.sunlightDampingMultiplier;
    }

    private static /* synthetic */ void lambda$static$32(Weather weather, TimeFloat[] o) {
        weather.sunlightDampingMultiplier = o;
    }

    private static /* synthetic */ TimeFloat[] lambda$CODEC$0(int x$0) {
        return new TimeFloat[x$0];
    }

    private static /* synthetic */ void lambda$static$31(Weather weather, Weather parent) {
        weather.screenEffectColors = parent.screenEffectColors;
    }

    private static /* synthetic */ TimeColorAlpha[] lambda$static$30(Weather weather) {
        return weather.screenEffectColors;
    }

    private static /* synthetic */ void lambda$static$29(Weather weather, TimeColorAlpha[] o) {
        weather.screenEffectColors = o;
    }

    private static /* synthetic */ void lambda$static$28(Weather weather, Weather parent) {
        weather.particle = parent.particle;
    }

    private static /* synthetic */ WeatherParticle lambda$static$27(Weather weather) {
        return weather.particle;
    }

    private static /* synthetic */ void lambda$static$26(Weather weather, WeatherParticle o) {
        weather.particle = o;
    }

    private static /* synthetic */ void lambda$static$25(Weather weather, Weather parent) {
        weather.fogOptions = parent.fogOptions;
    }

    private static /* synthetic */ FogOptions lambda$static$24(Weather weather) {
        return weather.fogOptions;
    }

    private static /* synthetic */ void lambda$static$23(Weather weather, FogOptions o) {
        weather.fogOptions = o;
    }

    private static /* synthetic */ void lambda$static$22(Weather weather, Weather parent) {
        weather.fogDistance = parent.fogDistance;
    }

    /*
     * Exception decompiling
     */
    static {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.UnsupportedOperationException
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.NewAnonymousArray.getDimSize(NewAnonymousArray.java:142)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.isNewArrayLambda(LambdaRewriter.java:455)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:409)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:167)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:105)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredAssignment.rewriteExpressions(StructuredAssignment.java:146)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

