/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.assets.spawns.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.spawning.assets.spawns.LightType;
import com.hypixel.hytale.server.spawning.assets.spawns.config.RoleSpawnParameters;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public abstract class NPCSpawn {
    public static final float HOURS_PER_DAY = 24.0f;
    public static final BuilderCodec<NPCSpawn> BASE_CODEC;
    public static final double[] DEFAULT_DAY_TIME_RANGE;
    public static final int[] DEFAULT_MOON_PHASE_RANGE;
    public static final double[] FULL_LIGHT_RANGE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected RoleSpawnParameters[] npcs;
    protected DespawnParameters despawnParameters;
    protected String[] environments;
    protected IntSet environmentIds = IntSets.EMPTY_SET;
    protected double[] dayTimeRange = DEFAULT_DAY_TIME_RANGE;
    protected int[] moonPhaseRange = DEFAULT_MOON_PHASE_RANGE;
    protected Map<LightType, double[]> lightTypeMap;
    protected boolean scaleDayTimeRange = true;

    private static void validateLightRange(@Nonnull ValidationResults results, String parameter, @Nonnull double[] lightRange) {
        for (int i = 0; i < lightRange.length; ++i) {
            double value = lightRange[i];
            if (value < 0.0 || value > 100.0) {
                results.fail(String.format("%s must be between 0 and 100 (inclusive)", parameter));
            }
            if (i <= 0 || !(value < lightRange[i - 1])) continue;
            results.fail(String.format("Values in %s must be weakly monotonic", parameter));
        }
    }

    public NPCSpawn(String id, RoleSpawnParameters[] npcs, DespawnParameters despawnParameters, String[] environments, IntSet environmentIds, double[] dayTimeRange, int[] moonPhaseRange, Map<LightType, double[]> lightTypeMap, boolean scaleDayTimeRange) {
        this.id = id;
        this.npcs = npcs;
        this.despawnParameters = despawnParameters;
        this.environments = environments;
        this.environmentIds = environmentIds;
        this.dayTimeRange = dayTimeRange;
        this.moonPhaseRange = moonPhaseRange;
        this.lightTypeMap = lightTypeMap;
        this.scaleDayTimeRange = scaleDayTimeRange;
    }

    protected NPCSpawn(String id) {
        this.id = id;
    }

    protected NPCSpawn() {
    }

    public abstract String getId();

    public RoleSpawnParameters[] getNPCs() {
        return this.npcs;
    }

    public DespawnParameters getDespawnParameters() {
        return this.despawnParameters;
    }

    public String[] getEnvironments() {
        return this.environments;
    }

    public IntSet getEnvironmentIds() {
        return this.environmentIds;
    }

    public double[] getDayTimeRange() {
        return this.dayTimeRange;
    }

    public int[] getMoonPhaseRange() {
        return this.moonPhaseRange;
    }

    public double[] getLightRange(LightType lightType) {
        double[] array;
        if (this.lightTypeMap != null && !this.lightTypeMap.isEmpty() && (array = this.lightTypeMap.get((Object)lightType)) != null) {
            return array;
        }
        return FULL_LIGHT_RANGE;
    }

    public boolean isScaleDayTimeRange() {
        return this.scaleDayTimeRange;
    }

    @Nonnull
    public String toString() {
        return "NPCSpawn{id='" + this.id + "', npcs=" + Arrays.deepToString(this.npcs) + ", despawnParameters=" + (this.despawnParameters != null ? this.despawnParameters.toString() : "Null") + ", environments=" + Arrays.toString(this.environments) + ", dayTimeRange=" + Arrays.toString(this.dayTimeRange) + ", moonPhaseRange=" + Arrays.toString(this.moonPhaseRange) + ", lightTypeMap=" + (this.lightTypeMap != null ? this.lightTypeMap.entrySet().stream().map(entry -> String.valueOf(entry.getKey()) + "=" + Arrays.toString((double[])entry.getValue())).collect(Collectors.joining(", ", "{", "}")) : "Null") + ", scaleDayTimeRange=" + this.scaleDayTimeRange + "}";
    }

    private static /* synthetic */ void lambda$static$19(NPCSpawn asset, ValidationResults results) {
        int[] moonPhaseRange;
        double[] dayTimeRange = asset.getDayTimeRange();
        if (dayTimeRange[0] < 0.0 || dayTimeRange[1] < 0.0) {
            results.fail("DayTimeRange values must be >=0");
        }
        if ((moonPhaseRange = asset.getMoonPhaseRange())[0] < 0 || moonPhaseRange[1] < 0) {
            results.fail("MoonPhaseRange values must be >=0");
        }
        for (LightType lightType : LightType.VALUES) {
            NPCSpawn.validateLightRange(results, lightType.name(), asset.getLightRange(lightType));
        }
        DespawnParameters despawnParameters = asset.getDespawnParameters();
        if (despawnParameters != null) {
            dayTimeRange = despawnParameters.getDayTimeRange();
            if (dayTimeRange[0] < 0.0 || dayTimeRange[1] < 0.0) {
                results.fail("Despawn DayTimeRange values must be >=0");
            }
            if ((moonPhaseRange = despawnParameters.getMoonPhaseRange())[0] < 0 || moonPhaseRange[1] < 0) {
                results.fail("Despawn MoonPhaseRange values must be >=0");
            }
        }
    }

    private static /* synthetic */ void lambda$static$18(NPCSpawn spawn) {
        if (spawn.environments == null || spawn.environments.length == 0) {
            spawn.environmentIds = IntSets.EMPTY_SET;
        } else {
            IntOpenHashSet environmentSet = new IntOpenHashSet();
            IndexedLookupTableAssetMap<String, Environment> environments = Environment.getAssetMap();
            for (String environment : spawn.environments) {
                int index = environments.getIndex(environment);
                if (index == Integer.MIN_VALUE) continue;
                environmentSet.add(index);
            }
            environmentSet.trim();
            spawn.environmentIds = IntSets.unmodifiable(environmentSet);
        }
    }

    private static /* synthetic */ void lambda$static$17(NPCSpawn spawn, NPCSpawn parent) {
        spawn.scaleDayTimeRange = parent.scaleDayTimeRange;
    }

    private static /* synthetic */ Boolean lambda$static$16(NPCSpawn spawn) {
        return spawn.scaleDayTimeRange;
    }

    private static /* synthetic */ void lambda$static$15(NPCSpawn spawn, Boolean b) {
        spawn.scaleDayTimeRange = b;
    }

    private static /* synthetic */ void lambda$static$14(NPCSpawn spawn, NPCSpawn parent) {
        spawn.lightTypeMap = parent.lightTypeMap;
    }

    private static /* synthetic */ Map lambda$static$13(NPCSpawn spawn) {
        return spawn.lightTypeMap;
    }

    private static /* synthetic */ void lambda$static$12(NPCSpawn spawn, Map o) {
        spawn.lightTypeMap = o;
    }

    private static /* synthetic */ void lambda$static$11(NPCSpawn spawn, NPCSpawn parent) {
        spawn.moonPhaseRange = parent.moonPhaseRange;
    }

    private static /* synthetic */ int[] lambda$static$10(NPCSpawn spawn) {
        return new int[]{spawn.moonPhaseRange[0], spawn.moonPhaseRange[1]};
    }

    private static /* synthetic */ void lambda$static$9(NPCSpawn spawn, int[] o) {
        spawn.moonPhaseRange = o;
    }

    private static /* synthetic */ void lambda$static$8(NPCSpawn spawn, NPCSpawn parent) {
        spawn.dayTimeRange = parent.dayTimeRange;
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
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
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

    public static class DespawnParameters {
        public static final BuilderCodec<DespawnParameters> CODEC;
        protected double[] dayTimeRange = DEFAULT_DAY_TIME_RANGE;
        protected int[] moonPhaseRange = DEFAULT_MOON_PHASE_RANGE;

        public DespawnParameters(double[] dayTimeRange, int[] moonPhaseRange) {
            this.dayTimeRange = dayTimeRange;
            this.moonPhaseRange = moonPhaseRange;
        }

        protected DespawnParameters() {
        }

        public double[] getDayTimeRange() {
            return this.dayTimeRange;
        }

        public int[] getMoonPhaseRange() {
            return this.moonPhaseRange;
        }

        @Nonnull
        public String toString() {
            return "DespawnParameters{dayTimeRange=" + Arrays.toString(this.dayTimeRange) + ", moonPhaseRange=" + Arrays.toString(this.moonPhaseRange) + "}";
        }

        private static /* synthetic */ int[] lambda$static$3(DespawnParameters parameters) {
            return new int[]{parameters.moonPhaseRange[0], parameters.moonPhaseRange[1]};
        }

        private static /* synthetic */ void lambda$static$2(DespawnParameters parameters, int[] o) {
            parameters.moonPhaseRange = o;
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
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }
}

