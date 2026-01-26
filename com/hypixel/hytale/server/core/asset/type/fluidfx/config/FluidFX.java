/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.fluidfx.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.FluidFXMovementSettings;
import com.hypixel.hytale.protocol.FluidFog;
import com.hypixel.hytale.protocol.NearFar;
import com.hypixel.hytale.server.core.asset.type.fluidfx.config.FluidParticle;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidFX
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, FluidFX>>,
NetworkSerializable<com.hypixel.hytale.protocol.FluidFX> {
    public static final AssetBuilderCodec<String, FluidFX> CODEC;
    public static final Color DEFAULT_FOG_COLOR;
    public static final float[] DEFAULT_FOG_DISTANCE;
    public static final float[] DEFAULT_COLORS_FILTER;
    public static final int EMPTY_ID = 0;
    public static final String EMPTY = "Empty";
    public static final FluidFX EMPTY_FLUID_FX;
    public static final ValidatorCache<String> VALIDATOR_CACHE;
    private static AssetStore<String, FluidFX, IndexedLookupTableAssetMap<String, FluidFX>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    @Nonnull
    protected FluidFog fog = FluidFog.Color;
    protected Color fogColor = DEFAULT_FOG_COLOR;
    protected float[] fogDistance = DEFAULT_FOG_DISTANCE;
    protected float fogDepthStart = 40.0f;
    protected float fogDepthFalloff = 10.0f;
    protected float colorsSaturation = 1.0f;
    protected float[] colorsFilter = DEFAULT_COLORS_FILTER;
    protected float distortionAmplitude = 8.0f;
    protected float distortionFrequency = 4.0f;
    protected FluidParticle particle;
    protected FluidFXMovementSettings movementSettings;
    private SoftReference<com.hypixel.hytale.protocol.FluidFX> cachedPacket;

    public static AssetStore<String, FluidFX, IndexedLookupTableAssetMap<String, FluidFX>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(FluidFX.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, FluidFX> getAssetMap() {
        return FluidFX.getAssetStore().getAssetMap();
    }

    public FluidFX(String id, FluidFog fog, Color fogColor, float[] fogDistance, float fogDepthStart, float fogDepthFalloff, float colorsSaturation, float[] colorsFilter, float distortionAmplitude, float distortionFrequency, FluidParticle particle, FluidFXMovementSettings movementSettings) {
        this.id = id;
        this.fog = fog;
        this.fogColor = fogColor;
        this.fogDistance = fogDistance;
        this.fogDepthStart = fogDepthStart;
        this.fogDepthFalloff = fogDepthFalloff;
        this.colorsSaturation = colorsSaturation;
        this.colorsFilter = colorsFilter;
        this.distortionAmplitude = distortionAmplitude;
        this.distortionFrequency = distortionFrequency;
        this.particle = particle;
        this.movementSettings = movementSettings;
    }

    public FluidFX(String id) {
        this.id = id;
    }

    protected FluidFX() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.FluidFX toPacket() {
        com.hypixel.hytale.protocol.FluidFX cached;
        com.hypixel.hytale.protocol.FluidFX fluidFX = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.FluidFX packet = new com.hypixel.hytale.protocol.FluidFX();
        packet.id = this.id;
        packet.fogMode = this.fog;
        packet.fogColor = this.fogColor;
        packet.fogDistance = new NearFar(this.fogDistance[0], this.fogDistance[1]);
        packet.fogDepthStart = this.fogDepthStart;
        packet.fogDepthFalloff = this.fogDepthFalloff;
        packet.colorFilter = new Color((byte)(this.colorsFilter[0] * 255.0f), (byte)(this.colorsFilter[1] * 255.0f), (byte)(this.colorsFilter[2] * 255.0f));
        packet.colorSaturation = this.colorsSaturation;
        packet.distortionAmplitude = this.distortionAmplitude;
        packet.distortionFrequency = this.distortionFrequency;
        if (this.particle != null) {
            packet.particle = this.particle.toPacket();
        }
        if (this.movementSettings != null) {
            packet.movementSettings = this.movementSettings;
        }
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.FluidFX>(packet);
        return packet;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public FluidFog getFog() {
        return this.fog;
    }

    public Color getFogColor() {
        return this.fogColor;
    }

    public float[] getFogDistance() {
        return this.fogDistance;
    }

    public float getColorsSaturation() {
        return this.colorsSaturation;
    }

    public float[] getColorsFilter() {
        return this.colorsFilter;
    }

    public float getDistortionAmplitude() {
        return this.distortionAmplitude;
    }

    public float getDistortionFrequency() {
        return this.distortionFrequency;
    }

    public float getFogDepthStart() {
        return this.fogDepthStart;
    }

    public float getFogDepthFalloff() {
        return this.fogDepthFalloff;
    }

    public FluidParticle getParticle() {
        return this.particle;
    }

    public FluidFXMovementSettings getMovementSettings() {
        return this.movementSettings;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FluidFX fluidFX = (FluidFX)o;
        return this.id != null ? this.id.equals(fluidFX.id) : fluidFX.id == null;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    @Nonnull
    public String toString() {
        return "FluidFX{id='" + this.id + "', fog=" + String.valueOf((Object)this.fog) + ", fogColor='" + String.valueOf(this.fogColor) + "', fogDistance=" + Arrays.toString(this.fogDistance) + ", fogDepthStart=" + this.fogDepthStart + ", fogDepthFalloff=" + this.fogDepthFalloff + ", colorsSaturation=" + this.colorsSaturation + ", colorsFilter=" + Arrays.toString(this.colorsFilter) + ", distortionAmplitude=" + this.distortionAmplitude + ", distortionFrequency=" + this.distortionFrequency + ", particle=" + String.valueOf(this.particle) + ", movementSettings=" + String.valueOf(this.movementSettings) + "}";
    }

    @Nonnull
    public static FluidFX getUnknownFor(String unknownId) {
        return new FluidFX(unknownId);
    }

    private static /* synthetic */ void lambda$static$48(FluidFX fluidFX, FluidFX parent) {
        fluidFX.movementSettings = parent.movementSettings;
    }

    private static /* synthetic */ FluidFXMovementSettings lambda$static$47(FluidFX fluidFX) {
        return fluidFX.movementSettings;
    }

    private static /* synthetic */ void lambda$static$46(FluidFX fluidFX, FluidFXMovementSettings movementSettings) {
        fluidFX.movementSettings = movementSettings;
    }

    private static /* synthetic */ Double lambda$static$45(FluidFXMovementSettings movementSettings) {
        return movementSettings.entryVelocityMultiplier;
    }

    private static /* synthetic */ void lambda$static$44(FluidFXMovementSettings movementSettings, Double val) {
        movementSettings.entryVelocityMultiplier = val.floatValue();
    }

    private static /* synthetic */ Double lambda$static$43(FluidFXMovementSettings movementSettings) {
        return movementSettings.fieldOfViewMultiplier;
    }

    private static /* synthetic */ void lambda$static$42(FluidFXMovementSettings movementSettings, Double val) {
        movementSettings.fieldOfViewMultiplier = val.floatValue();
    }

    private static /* synthetic */ Double lambda$static$41(FluidFXMovementSettings movementSettings) {
        return movementSettings.horizontalSpeedMultiplier;
    }

    private static /* synthetic */ void lambda$static$40(FluidFXMovementSettings movementSettings, Double val) {
        movementSettings.horizontalSpeedMultiplier = val.floatValue();
    }

    private static /* synthetic */ Double lambda$static$39(FluidFXMovementSettings movementSettings) {
        return movementSettings.sinkSpeed;
    }

    private static /* synthetic */ void lambda$static$38(FluidFXMovementSettings movementSettings, Double val) {
        movementSettings.sinkSpeed = val.floatValue();
    }

    private static /* synthetic */ Double lambda$static$37(FluidFXMovementSettings movementSettings) {
        return movementSettings.swimDownSpeed;
    }

    private static /* synthetic */ void lambda$static$36(FluidFXMovementSettings movementSettings, Double val) {
        movementSettings.swimDownSpeed = val.floatValue();
    }

    private static /* synthetic */ Double lambda$static$35(FluidFXMovementSettings movementSettings) {
        return movementSettings.swimUpSpeed;
    }

    private static /* synthetic */ void lambda$static$34(FluidFXMovementSettings movementSettings, Double val) {
        movementSettings.swimUpSpeed = val.floatValue();
    }

    private static /* synthetic */ void lambda$static$33(FluidFX fluidFX, FluidFX parent) {
        fluidFX.particle = parent.particle;
    }

    private static /* synthetic */ FluidParticle lambda$static$32(FluidFX fluidFX) {
        return fluidFX.particle;
    }

    private static /* synthetic */ void lambda$static$31(FluidFX fluidFX, FluidParticle s) {
        fluidFX.particle = s;
    }

    private static /* synthetic */ void lambda$static$30(FluidFX fluidFX, FluidFX parent) {
        fluidFX.distortionFrequency = parent.distortionFrequency;
    }

    private static /* synthetic */ Double lambda$static$29(FluidFX fluidFX) {
        return fluidFX.distortionFrequency;
    }

    private static /* synthetic */ void lambda$static$28(FluidFX fluidFX, Double s) {
        fluidFX.distortionFrequency = s.floatValue();
    }

    private static /* synthetic */ void lambda$static$27(FluidFX fluidFX, FluidFX parent) {
        fluidFX.distortionAmplitude = parent.distortionAmplitude;
    }

    private static /* synthetic */ Double lambda$static$26(FluidFX fluidFX) {
        return fluidFX.distortionAmplitude;
    }

    private static /* synthetic */ void lambda$static$25(FluidFX fluidFX, Double s) {
        fluidFX.distortionAmplitude = s.floatValue();
    }

    private static /* synthetic */ void lambda$static$24(FluidFX weather, FluidFX parent) {
        weather.colorsFilter = parent.colorsFilter;
    }

    private static /* synthetic */ double[] lambda$static$23(FluidFX weather) {
        return new double[]{weather.colorsFilter[0], weather.colorsFilter[1], weather.colorsFilter[2]};
    }

    private static /* synthetic */ void lambda$static$22(FluidFX weather, double[] o) {
        weather.colorsFilter = new float[3];
        weather.colorsFilter[0] = (float)o[0];
        weather.colorsFilter[1] = (float)o[1];
        weather.colorsFilter[2] = (float)o[2];
    }

    private static /* synthetic */ void lambda$static$21(FluidFX fluidFX, FluidFX parent) {
        fluidFX.colorsSaturation = parent.colorsSaturation;
    }

    private static /* synthetic */ Double lambda$static$20(FluidFX fluidFX) {
        return fluidFX.colorsSaturation;
    }

    private static /* synthetic */ void lambda$static$19(FluidFX fluidFX, Double s) {
        fluidFX.colorsSaturation = s.floatValue();
    }

    private static /* synthetic */ void lambda$static$18(FluidFX fluidFX, FluidFX parent) {
        fluidFX.fogDepthFalloff = parent.fogDepthFalloff;
    }

    private static /* synthetic */ Double lambda$static$17(FluidFX fluidFX) {
        return fluidFX.fogDepthFalloff;
    }

    private static /* synthetic */ void lambda$static$16(FluidFX fluidFX, Double s) {
        fluidFX.fogDepthFalloff = s.floatValue();
    }

    private static /* synthetic */ void lambda$static$15(FluidFX fluidFX, FluidFX parent) {
        fluidFX.fogDepthStart = parent.fogDepthStart;
    }

    private static /* synthetic */ Double lambda$static$14(FluidFX fluidFX) {
        return fluidFX.fogDepthStart;
    }

    private static /* synthetic */ void lambda$static$13(FluidFX fluidFX, Double s) {
        fluidFX.fogDepthStart = s.floatValue();
    }

    private static /* synthetic */ void lambda$static$12(FluidFX weather, FluidFX parent) {
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

