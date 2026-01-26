/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.ConnectedBlockRuleSetType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin.ConnectedBlockOutput;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin.StairLikeConnectedBlockRuleSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import java.util.Optional;
import javax.annotation.Nullable;

public class StairConnectedBlockRuleSet
extends ConnectedBlockRuleSet
implements StairLikeConnectedBlockRuleSet {
    public static final String DEFAULT_MATERIAL_NAME = "Stair";
    public static final BuilderCodec<StairConnectedBlockRuleSet> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StairConnectedBlockRuleSet.class, StairConnectedBlockRuleSet::new).append(new KeyedCodec<ConnectedBlockOutput>("Straight", ConnectedBlockOutput.CODEC), (ruleSet, output) -> {
        ruleSet.straight = output;
    }, ruleSet -> ruleSet.straight).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<ConnectedBlockOutput>("Corner_Left", ConnectedBlockOutput.CODEC), (ruleSet, output) -> {
        ruleSet.cornerLeft = output;
    }, ruleSet -> ruleSet.cornerLeft).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<ConnectedBlockOutput>("Corner_Right", ConnectedBlockOutput.CODEC), (ruleSet, output) -> {
        ruleSet.cornerRight = output;
    }, ruleSet -> ruleSet.cornerRight).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<ConnectedBlockOutput>("Inverted_Corner_Left", ConnectedBlockOutput.CODEC), (ruleSet, output) -> {
        ruleSet.invertedCornerLeft = output;
    }, ruleSet -> ruleSet.invertedCornerLeft).add()).append(new KeyedCodec<ConnectedBlockOutput>("Inverted_Corner_Right", ConnectedBlockOutput.CODEC), (ruleSet, output) -> {
        ruleSet.invertedCornerRight = output;
    }, ruleSet -> ruleSet.invertedCornerRight).add()).append(new KeyedCodec<String>("MaterialName", Codec.STRING), (ruleSet, materialName) -> {
        ruleSet.materialName = materialName;
    }, ruleSet -> ruleSet.materialName).add()).build();
    private ConnectedBlockOutput straight;
    private ConnectedBlockOutput cornerLeft;
    private ConnectedBlockOutput cornerRight;
    private ConnectedBlockOutput invertedCornerLeft;
    private ConnectedBlockOutput invertedCornerRight;
    private String materialName = "Stair";
    private Int2ObjectMap<StairType> blockIdToStairType;
    protected Object2IntMap<StairType> stairTypeToBlockId;

    @Override
    public boolean onlyUpdateOnPlacement() {
        return false;
    }

    @Override
    public void updateCachedBlockTypes(BlockType baseBlockType, BlockTypeAssetMap<String, BlockType> assetMap) {
        int baseIndex = assetMap.getIndex(baseBlockType.getId());
        Int2ObjectOpenHashMap<StairType> blockIdToStairType = new Int2ObjectOpenHashMap<StairType>();
        Object2IntOpenHashMap<StairType> stairTypeToBlockId = new Object2IntOpenHashMap<StairType>();
        stairTypeToBlockId.defaultReturnValue(baseIndex);
        ConnectedBlockOutput[] outputs = new ConnectedBlockOutput[]{this.straight, this.cornerLeft, this.cornerRight, this.invertedCornerLeft, this.invertedCornerRight};
        StairType[] stairTypes = StairType.VALUES;
        for (int i = 0; i < outputs.length; ++i) {
            int index;
            ConnectedBlockOutput output = outputs[i];
            if (output == null || (index = output.resolve(baseBlockType, assetMap)) == -1) continue;
            blockIdToStairType.put(index, stairTypes[i]);
            stairTypeToBlockId.put(stairTypes[i], index);
        }
        this.blockIdToStairType = blockIdToStairType;
        this.stairTypeToBlockId = stairTypeToBlockId;
    }

    @Nullable
    protected static ObjectIntPair<StairType> getStairData(World world, Vector3i coordinate, @Nullable String requiredMaterialName) {
        WorldChunk chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(coordinate.x, coordinate.z));
        if (chunk == null) {
            return null;
        }
        int filler = chunk.getFiller(coordinate.x, coordinate.y, coordinate.z);
        if (filler != 0) {
            return null;
        }
        int blockId = chunk.getBlock(coordinate);
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (blockType == null) {
            return null;
        }
        ConnectedBlockRuleSet ruleSet = blockType.getConnectedBlockRuleSet();
        if (!(ruleSet instanceof StairLikeConnectedBlockRuleSet)) {
            return null;
        }
        StairLikeConnectedBlockRuleSet stairRuleSet = (StairLikeConnectedBlockRuleSet)((Object)ruleSet);
        String otherMaterialName = stairRuleSet.getMaterialName();
        if (requiredMaterialName != null && otherMaterialName != null && !requiredMaterialName.equals(otherMaterialName)) {
            return null;
        }
        StairType stairType = stairRuleSet.getStairType(blockId);
        if (stairType == null) {
            return null;
        }
        int rotation = chunk.getRotationIndex(coordinate.x, coordinate.y, coordinate.z);
        return new ObjectIntImmutablePair<StairType>(stairType, rotation);
    }

    @Override
    public StairType getStairType(int blockId) {
        return (StairType)((Object)this.blockIdToStairType.get(blockId));
    }

    @Override
    @Nullable
    public String getMaterialName() {
        return this.materialName;
    }

    public BlockType getStairBlockType(StairType stairType) {
        if (this.stairTypeToBlockId == null) {
            return null;
        }
        int resultingBlockTypeIndex = this.stairTypeToBlockId.getInt((Object)stairType);
        return BlockType.getAssetMap().getAsset(resultingBlockTypeIndex);
    }

    @Override
    public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(World world, Vector3i coordinate, BlockType currentBlockType, int rotation, Vector3i placementNormal, boolean isPlacement) {
        StairConnection backConnection;
        boolean upsideDown;
        RotationTuple currentRotation = RotationTuple.get(rotation);
        Rotation currentYaw = currentRotation.yaw();
        Rotation currentPitch = currentRotation.pitch();
        boolean bl = upsideDown = currentPitch != Rotation.None;
        if (upsideDown) {
            currentYaw = currentYaw.flip();
        }
        Vector3i mutablePos = new Vector3i();
        StairType resultingStair = StairType.STRAIGHT;
        StairConnection frontConnection = StairConnectedBlockRuleSet.getInvertedCornerConnection(world, this, coordinate, mutablePos, currentYaw, upsideDown);
        if (frontConnection != null) {
            resultingStair = frontConnection.getStairType(true);
        }
        if ((backConnection = StairConnectedBlockRuleSet.getCornerConnection(world, this, coordinate, mutablePos, rotation, currentYaw, upsideDown, 1)) != null) {
            resultingStair = backConnection.getStairType(false);
        }
        if (upsideDown) {
            resultingStair = switch (resultingStair.ordinal()) {
                case 1 -> StairType.CORNER_RIGHT;
                case 2 -> StairType.CORNER_LEFT;
                case 3 -> StairType.INVERTED_CORNER_RIGHT;
                case 4 -> StairType.INVERTED_CORNER_LEFT;
                default -> resultingStair;
            };
        }
        int resultingBlockTypeIndex = this.stairTypeToBlockId.getInt((Object)resultingStair);
        BlockType resultingBlockType = BlockType.getAssetMap().getAsset(resultingBlockTypeIndex);
        if (resultingBlockType == null) {
            return Optional.empty();
        }
        String resultingBlockTypeKey = resultingBlockType.getId();
        return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(resultingBlockTypeKey, rotation));
    }

    protected static StairConnection getCornerConnection(World world, StairLikeConnectedBlockRuleSet currentRuleSet, Vector3i coordinate, Vector3i mutablePos, int rotation, Rotation currentYaw, boolean upsideDown, int width) {
        StairConnection backConnection = null;
        mutablePos.assign(Vector3i.NORTH).scale(width);
        currentYaw.rotateY(mutablePos, mutablePos);
        mutablePos.add(coordinate.x, coordinate.y, coordinate.z);
        ObjectIntPair<StairType> backStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
        if (backStair == null && width > 1) {
            mutablePos.assign(Vector3i.NORTH).scale(width + 1);
            currentYaw.rotateY(mutablePos, mutablePos);
            mutablePos.add(coordinate.x, coordinate.y, coordinate.z);
            backStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
            if (backStair != null && backStair.first() == StairType.STRAIGHT) {
                backStair = null;
            }
        }
        if (backStair != null) {
            boolean otherUpsideDown;
            StairType otherStairType = (StairType)((Object)backStair.left());
            RotationTuple otherStairRotation = RotationTuple.get(backStair.rightInt());
            Rotation otherYaw = otherStairRotation.yaw();
            boolean bl = otherUpsideDown = otherStairRotation.pitch() != Rotation.None;
            if (otherUpsideDown) {
                otherYaw = otherYaw.flip();
            }
            if (StairConnectedBlockRuleSet.canConnectTo(currentYaw, otherYaw, upsideDown, otherUpsideDown)) {
                mutablePos.assign(Vector3i.SOUTH);
                otherYaw.rotateY(mutablePos, mutablePos);
                mutablePos.add(coordinate.x, coordinate.y, coordinate.z);
                ObjectIntPair<StairType> sidewaysStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
                if (sidewaysStair == null || sidewaysStair.rightInt() != rotation) {
                    backConnection = StairConnectedBlockRuleSet.getConnection(currentYaw, otherYaw, otherStairType, false);
                }
            }
        }
        return backConnection;
    }

    protected static StairConnection getInvertedCornerConnection(World world, StairLikeConnectedBlockRuleSet currentRuleSet, Vector3i coordinate, Vector3i mutablePos, Rotation currentYaw, boolean upsideDown) {
        StairConnection frontConnection = null;
        mutablePos.assign(Vector3i.SOUTH);
        currentYaw.rotateY(mutablePos, mutablePos);
        mutablePos.add(coordinate.x, coordinate.y, coordinate.z);
        ObjectIntPair<StairType> frontStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
        if (frontStair != null) {
            boolean otherUpsideDown;
            StairType otherStairType = (StairType)((Object)frontStair.left());
            RotationTuple otherStairRotation = RotationTuple.get(frontStair.rightInt());
            Rotation otherYaw = otherStairRotation.yaw();
            boolean bl = otherUpsideDown = otherStairRotation.pitch() != Rotation.None;
            if (otherUpsideDown) {
                otherYaw = otherYaw.flip();
            }
            if (StairConnectedBlockRuleSet.canConnectTo(currentYaw, otherYaw, upsideDown, otherUpsideDown)) {
                frontConnection = StairConnectedBlockRuleSet.getConnection(currentYaw, otherYaw, otherStairType, true);
            }
        }
        return frontConnection;
    }

    private static boolean canConnectTo(Rotation currentYaw, Rotation otherYaw, boolean upsideDown, boolean otherUpsideDown) {
        return otherUpsideDown == upsideDown && otherYaw != currentYaw && otherYaw.add(Rotation.OneEighty) != currentYaw;
    }

    private static StairConnection getConnection(Rotation currentYaw, Rotation otherYaw, StairType otherStairType, boolean inverted) {
        if (otherYaw == currentYaw.add(Rotation.Ninety) && otherStairType != StairType.invertedCorner(inverted) && otherStairType != StairType.corner(!inverted)) {
            return StairConnection.CORNER_LEFT;
        }
        if (otherYaw == currentYaw.subtract(Rotation.Ninety) && otherStairType != StairType.invertedCorner(!inverted) && otherStairType != StairType.corner(inverted)) {
            return StairConnection.CORNER_RIGHT;
        }
        return null;
    }

    @Override
    @Nullable
    public com.hypixel.hytale.protocol.ConnectedBlockRuleSet toPacket(BlockTypeAssetMap<String, BlockType> assetMap) {
        com.hypixel.hytale.protocol.ConnectedBlockRuleSet packet = new com.hypixel.hytale.protocol.ConnectedBlockRuleSet();
        packet.type = ConnectedBlockRuleSetType.Stair;
        packet.stair = this.toProtocol(assetMap);
        return packet;
    }

    public com.hypixel.hytale.protocol.StairConnectedBlockRuleSet toProtocol(BlockTypeAssetMap<String, BlockType> assetMap) {
        com.hypixel.hytale.protocol.StairConnectedBlockRuleSet stairPacket = new com.hypixel.hytale.protocol.StairConnectedBlockRuleSet();
        stairPacket.straightBlockId = this.getBlockIdForStairType(StairType.STRAIGHT, assetMap);
        stairPacket.cornerLeftBlockId = this.getBlockIdForStairType(StairType.CORNER_LEFT, assetMap);
        stairPacket.cornerRightBlockId = this.getBlockIdForStairType(StairType.CORNER_RIGHT, assetMap);
        stairPacket.invertedCornerLeftBlockId = this.getBlockIdForStairType(StairType.INVERTED_CORNER_LEFT, assetMap);
        stairPacket.invertedCornerRightBlockId = this.getBlockIdForStairType(StairType.INVERTED_CORNER_RIGHT, assetMap);
        stairPacket.materialName = this.materialName;
        return stairPacket;
    }

    private int getBlockIdForStairType(StairType stairType, BlockTypeAssetMap<String, BlockType> assetMap) {
        BlockType blockType = this.getStairBlockType(stairType);
        if (blockType == null) {
            return -1;
        }
        return assetMap.getIndex(blockType.getId());
    }

    public static enum StairType {
        STRAIGHT,
        CORNER_LEFT,
        CORNER_RIGHT,
        INVERTED_CORNER_LEFT,
        INVERTED_CORNER_RIGHT;

        private static final StairType[] VALUES;

        public static StairType corner(boolean right) {
            return right ? CORNER_RIGHT : CORNER_LEFT;
        }

        public static StairType invertedCorner(boolean right) {
            return right ? INVERTED_CORNER_RIGHT : INVERTED_CORNER_LEFT;
        }

        public boolean isCorner() {
            return this == CORNER_LEFT || this == CORNER_RIGHT;
        }

        public boolean isInvertedCorner() {
            return this == INVERTED_CORNER_LEFT || this == INVERTED_CORNER_RIGHT;
        }

        public boolean isLeft() {
            return this == CORNER_LEFT || this == INVERTED_CORNER_LEFT;
        }

        public boolean isRight() {
            return this == CORNER_RIGHT || this == INVERTED_CORNER_RIGHT;
        }

        static {
            VALUES = StairType.values();
        }
    }

    protected static enum StairConnection {
        CORNER_LEFT,
        CORNER_RIGHT;


        public StairType getStairType(boolean inverted) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    if (inverted) {
                        yield StairType.INVERTED_CORNER_LEFT;
                    }
                    yield StairType.CORNER_LEFT;
                }
                case 1 -> inverted ? StairType.INVERTED_CORNER_RIGHT : StairType.CORNER_RIGHT;
            };
        }
    }
}

