/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public class BlockPlacementSettings
implements NetworkSerializable<com.hypixel.hytale.protocol.BlockPlacementSettings> {
    public static final BuilderCodec<BlockPlacementSettings> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockPlacementSettings.class, BlockPlacementSettings::new).append(new KeyedCodec<Boolean>("AllowRotationKey", Codec.BOOLEAN), (placementSettings, o) -> {
        placementSettings.allowRotationKey = o;
    }, placementSettings -> placementSettings.allowRotationKey).add()).append(new KeyedCodec<Boolean>("PlaceInEmptyBlocks", Codec.BOOLEAN), (placementSettings, o) -> {
        placementSettings.placeInEmptyBlocks = o;
    }, placementSettings -> placementSettings.placeInEmptyBlocks).documentation("If this block is allowed to be placed inside other blocks with an Empty Material (destroying them).").add()).append(new KeyedCodec<RotationMode>("RotationMode", RotationMode.CODEC), (placementSettings, o) -> {
        placementSettings.rotationMode = o;
    }, placementSettings -> placementSettings.rotationMode).documentation("The mode determining the rotation of this block when placed.").add()).append(new KeyedCodec<BlockPreviewVisibility>("BlockPreviewVisibility", BlockPreviewVisibility.CODEC), (placementSettings, o) -> {
        placementSettings.previewVisibility = o;
    }, placementSettings -> placementSettings.previewVisibility).documentation("An override for the block preview visibility").add()).append(new KeyedCodec<String>("WallPlacementOverrideBlockId", Codec.STRING), (placementSettings, o) -> {
        placementSettings.wallPlacementOverrideBlockId = o;
    }, placementSettings -> placementSettings.wallPlacementOverrideBlockId).add()).append(new KeyedCodec<String>("FloorPlacementOverrideBlockId", Codec.STRING), (placementSettings, o) -> {
        placementSettings.floorPlacementOverrideBlockId = o;
    }, placementSettings -> placementSettings.floorPlacementOverrideBlockId).add()).append(new KeyedCodec<String>("CeilingPlacementOverrideBlockId", Codec.STRING), (placementSettings, o) -> {
        placementSettings.ceilingPlacementOverrideBlockId = o;
    }, placementSettings -> placementSettings.ceilingPlacementOverrideBlockId).add()).build();
    protected String wallPlacementOverrideBlockId;
    protected String floorPlacementOverrideBlockId;
    protected String ceilingPlacementOverrideBlockId;
    private boolean allowRotationKey = true;
    private boolean placeInEmptyBlocks;
    private BlockPreviewVisibility previewVisibility = BlockPreviewVisibility.DEFAULT;
    private RotationMode rotationMode = RotationMode.DEFAULT;

    protected BlockPlacementSettings() {
    }

    /*
     * Exception decompiling
     */
    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.BlockPlacementSettings toPacket() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Can't turn ConstantPoolEntry into Literal - got DynamicInfo value=18,277
         *     at org.benf.cfr.reader.bytecode.analysis.parse.literal.TypedLiteral.getConstantPoolEntry(TypedLiteral.java:340)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.getBootstrapArg(Op02WithProcessedDataAndRefs.java:538)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.getVarArgs(Op02WithProcessedDataAndRefs.java:671)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.buildInvokeBootstrapArgs(Op02WithProcessedDataAndRefs.java:630)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.buildInvokeDynamic(Op02WithProcessedDataAndRefs.java:411)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.buildInvokeDynamic(Op02WithProcessedDataAndRefs.java:392)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.createStatement(Op02WithProcessedDataAndRefs.java:1215)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.access$100(Op02WithProcessedDataAndRefs.java:57)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs$11.call(Op02WithProcessedDataAndRefs.java:2080)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs$11.call(Op02WithProcessedDataAndRefs.java:2077)
         *     at org.benf.cfr.reader.util.graph.AbstractGraphVisitorFI.process(AbstractGraphVisitorFI.java:60)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.convertToOp03List(Op02WithProcessedDataAndRefs.java:2089)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:469)
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

    public String getWallPlacementOverrideBlockId() {
        return this.wallPlacementOverrideBlockId;
    }

    public String getFloorPlacementOverrideBlockId() {
        return this.floorPlacementOverrideBlockId;
    }

    public String getCeilingPlacementOverrideBlockId() {
        return this.ceilingPlacementOverrideBlockId;
    }

    public static enum BlockPreviewVisibility {
        ALWAYS_VISIBLE,
        ALWAYS_HIDDEN,
        DEFAULT;

        public static final EnumCodec<BlockPreviewVisibility> CODEC;

        static {
            CODEC = new EnumCodec<BlockPreviewVisibility>(BlockPreviewVisibility.class);
        }
    }

    public static enum RotationMode {
        FACING_PLAYER,
        BLOCK_NORMAL,
        STAIR_FACING_PLAYER,
        DEFAULT;

        public static final EnumCodec<RotationMode> CODEC;

        static {
            CODEC = new EnumCodec<RotationMode>(RotationMode.class);
        }
    }
}

