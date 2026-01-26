/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockNeighbor;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum BlockFace {
    UP(FaceConnectionType.FLIP, BlockNeighbor.Up, Vector3i.UP),
    DOWN(FaceConnectionType.FLIP, BlockNeighbor.Down, Vector3i.DOWN),
    NORTH(FaceConnectionType.FLIP, BlockNeighbor.North, Vector3i.NORTH),
    EAST(FaceConnectionType.FLIP, BlockNeighbor.East, Vector3i.EAST),
    SOUTH(FaceConnectionType.FLIP, BlockNeighbor.South, Vector3i.SOUTH),
    WEST(FaceConnectionType.FLIP, BlockNeighbor.West, Vector3i.WEST),
    UP_NORTH(FaceConnectionType.ROTATE_X, BlockNeighbor.UpNorth, UP, NORTH),
    UP_SOUTH(FaceConnectionType.ROTATE_X, BlockNeighbor.UpSouth, UP, SOUTH),
    UP_EAST(FaceConnectionType.ROTATE_Z, BlockNeighbor.UpEast, UP, EAST),
    UP_WEST(FaceConnectionType.ROTATE_Z, BlockNeighbor.UpWest, UP, WEST),
    DOWN_NORTH(FaceConnectionType.ROTATE_X, BlockNeighbor.DownNorth, DOWN, NORTH),
    DOWN_SOUTH(FaceConnectionType.ROTATE_X, BlockNeighbor.DownSouth, DOWN, SOUTH),
    DOWN_EAST(FaceConnectionType.ROTATE_Z, BlockNeighbor.DownEast, DOWN, EAST),
    DOWN_WEST(FaceConnectionType.ROTATE_Z, BlockNeighbor.DownWest, DOWN, WEST),
    NORTH_EAST(FaceConnectionType.ROTATE_Y, BlockNeighbor.NorthEast, NORTH, EAST),
    SOUTH_EAST(FaceConnectionType.ROTATE_Y, BlockNeighbor.SouthEast, SOUTH, EAST),
    SOUTH_WEST(FaceConnectionType.ROTATE_Y, BlockNeighbor.SouthWest, SOUTH, WEST),
    NORTH_WEST(FaceConnectionType.ROTATE_Y, BlockNeighbor.NorthWest, NORTH, WEST),
    UP_NORTH_EAST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.UpNorthEast, UP, NORTH, EAST),
    UP_SOUTH_EAST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.UpSouthEast, UP, SOUTH, EAST),
    UP_SOUTH_WEST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.UpSouthWest, UP, SOUTH, WEST),
    UP_NORTH_WEST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.UpNorthWest, UP, NORTH, WEST),
    DOWN_NORTH_EAST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.DownNorthEast, DOWN, NORTH, EAST),
    DOWN_SOUTH_EAST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.DownSouthEast, DOWN, SOUTH, EAST),
    DOWN_SOUTH_WEST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.DownSouthWest, DOWN, SOUTH, WEST),
    DOWN_NORTH_WEST(FaceConnectionType.ROTATE_ALL, BlockNeighbor.DownNorthWest, DOWN, NORTH, WEST);

    public static final EnumCodec<BlockFace> CODEC;
    public static final BlockFace[] VALUES;
    @Nonnull
    private static final Map<Vector3i, BlockFace> DIRECTION_MAP;
    private final FaceConnectionType faceConnectionType;
    @Nonnull
    private final BlockFace[] components;
    private final Vector3i direction;
    private final BlockNeighbor blockNeighbor;
    private BlockFace[] connectingFaces;
    private Vector3i[] connectingFaceOffsets;

    private BlockFace(FaceConnectionType faceConnectionType, BlockNeighbor blockNeighbor, Vector3i direction) {
        this.faceConnectionType = faceConnectionType;
        this.direction = direction;
        this.blockNeighbor = blockNeighbor;
        this.components = new BlockFace[0];
    }

    private BlockFace(@Nonnull FaceConnectionType faceConnectionType, BlockNeighbor blockNeighbor, BlockFace ... components) {
        this.faceConnectionType = faceConnectionType;
        this.components = components;
        for (BlockFace component : components) {
            if (component.components.length <= 0) continue;
            throw new IllegalArgumentException("Only the base BlockFace's can be used as components to make other block faces");
        }
        this.direction = new Vector3i();
        for (BlockFace component : components) {
            this.direction.add(component.direction);
        }
        this.blockNeighbor = blockNeighbor;
    }

    public FaceConnectionType getFaceConnectionType() {
        return this.faceConnectionType;
    }

    @Nonnull
    public BlockFace[] getComponents() {
        return this.components;
    }

    public Vector3i getDirection() {
        return this.direction;
    }

    public BlockFace[] getConnectingFaces() {
        return this.connectingFaces;
    }

    public Vector3i[] getConnectingFaceOffsets() {
        return this.connectingFaceOffsets;
    }

    @Nonnull
    private BlockFace[] getConnectingFaces0() {
        switch (this.faceConnectionType.ordinal()) {
            case 0: {
                return new BlockFace[]{BlockFace.flip(this)};
            }
            case 1: {
                BlockFace[] blockFaces = new BlockFace[]{BlockFace.rotate(this, Rotation.Ninety, Rotation.None, Rotation.None), BlockFace.rotate(this, Rotation.OneEighty, Rotation.None, Rotation.None), BlockFace.rotate(this, Rotation.TwoSeventy, Rotation.None, Rotation.None)};
                return blockFaces;
            }
            case 2: {
                BlockFace[] blockFaces = new BlockFace[]{BlockFace.rotate(this, Rotation.None, Rotation.Ninety, Rotation.None), BlockFace.rotate(this, Rotation.None, Rotation.OneEighty, Rotation.None), BlockFace.rotate(this, Rotation.None, Rotation.TwoSeventy, Rotation.None)};
                return blockFaces;
            }
            case 3: {
                BlockFace[] blockFaces = new BlockFace[]{BlockFace.rotate(this, Rotation.None, Rotation.None, Rotation.Ninety), BlockFace.rotate(this, Rotation.None, Rotation.None, Rotation.OneEighty), BlockFace.rotate(this, Rotation.None, Rotation.None, Rotation.TwoSeventy)};
                return blockFaces;
            }
            case 4: {
                BlockFace[] blockFaces = new BlockFace[]{BlockFace.rotate(this, Rotation.Ninety, Rotation.None, Rotation.None), BlockFace.rotate(this, Rotation.OneEighty, Rotation.None, Rotation.None), BlockFace.rotate(this, Rotation.None, Rotation.Ninety, Rotation.None), BlockFace.rotate(this, Rotation.None, Rotation.OneEighty, Rotation.None), BlockFace.rotate(this, Rotation.None, Rotation.TwoSeventy, Rotation.None), BlockFace.rotate(this, Rotation.None, Rotation.None, Rotation.OneEighty), BlockFace.flip(this)};
                return blockFaces;
            }
        }
        throw new IllegalArgumentException("Unknown FaceConnectionType " + String.valueOf((Object)this.faceConnectionType));
    }

    @Nonnull
    private Vector3i directionTo(@Nonnull BlockFace connectingFace) {
        Vector3i vector3i = new Vector3i();
        if (this.direction.getX() == -connectingFace.direction.getX()) {
            vector3i.setX(this.direction.getX());
        }
        if (this.direction.getY() == -connectingFace.direction.getY()) {
            vector3i.setY(this.direction.getY());
        }
        if (this.direction.getZ() == -connectingFace.direction.getZ()) {
            vector3i.setZ(this.direction.getZ());
        }
        return vector3i;
    }

    public static BlockFace lookup(Vector3i direction) {
        return DIRECTION_MAP.get(direction);
    }

    public static BlockFace rotate(@Nonnull BlockFace blockFace, @Nonnull Rotation rotationYaw, @Nonnull Rotation rotationPitch) {
        Vector3i rotate = Rotation.rotate(blockFace.direction, rotationYaw, rotationPitch);
        return BlockFace.lookup(rotate);
    }

    public static BlockFace rotate(@Nonnull BlockFace blockFace, @Nonnull Rotation rotationX, @Nonnull Rotation rotationY, @Nonnull Rotation rotationZ) {
        Vector3i rotate = Rotation.rotate(blockFace.direction, rotationX, rotationY, rotationZ);
        return BlockFace.lookup(rotate);
    }

    public static BlockFace flip(@Nonnull BlockFace blockFace) {
        Vector3i flipped = blockFace.direction.clone().scale(-1);
        return BlockFace.lookup(flipped);
    }

    public BlockNeighbor toProtocolBlockNeighbor() {
        return this.blockNeighbor;
    }

    @Nullable
    public static BlockFace fromProtocolFace(@Nonnull com.hypixel.hytale.protocol.BlockFace face) {
        return switch (face) {
            default -> throw new MatchException(null, null);
            case com.hypixel.hytale.protocol.BlockFace.Up -> UP;
            case com.hypixel.hytale.protocol.BlockFace.Down -> DOWN;
            case com.hypixel.hytale.protocol.BlockFace.North -> NORTH;
            case com.hypixel.hytale.protocol.BlockFace.South -> SOUTH;
            case com.hypixel.hytale.protocol.BlockFace.East -> EAST;
            case com.hypixel.hytale.protocol.BlockFace.West -> WEST;
            case com.hypixel.hytale.protocol.BlockFace.None -> null;
        };
    }

    @Nonnull
    public static com.hypixel.hytale.protocol.BlockFace toProtocolFace(@Nullable BlockFace face) {
        if (face == null) {
            return com.hypixel.hytale.protocol.BlockFace.None;
        }
        return switch (face.ordinal()) {
            case 0 -> com.hypixel.hytale.protocol.BlockFace.Up;
            case 1 -> com.hypixel.hytale.protocol.BlockFace.Down;
            case 2 -> com.hypixel.hytale.protocol.BlockFace.North;
            case 4 -> com.hypixel.hytale.protocol.BlockFace.South;
            case 3 -> com.hypixel.hytale.protocol.BlockFace.East;
            case 5 -> com.hypixel.hytale.protocol.BlockFace.West;
            default -> throw new IllegalArgumentException("Invalid BlockFace");
        };
    }

    static {
        CODEC = new EnumCodec<BlockFace>(BlockFace.class);
        VALUES = BlockFace.values();
        DIRECTION_MAP = new Object2ObjectOpenHashMap<Vector3i, BlockFace>();
        for (BlockFace blockFace : VALUES) {
            DIRECTION_MAP.put(blockFace.direction, blockFace);
        }
        for (BlockFace blockFace : VALUES) {
            blockFace.connectingFaces = blockFace.getConnectingFaces0();
            blockFace.connectingFaceOffsets = new Vector3i[blockFace.connectingFaces.length];
            for (int i = 0; i < blockFace.connectingFaces.length; ++i) {
                blockFace.connectingFaceOffsets[i] = blockFace.directionTo(blockFace.connectingFaces[i]);
            }
        }
    }

    static enum FaceConnectionType {
        FLIP,
        ROTATE_X,
        ROTATE_Y,
        ROTATE_Z,
        ROTATE_ALL;

    }
}

