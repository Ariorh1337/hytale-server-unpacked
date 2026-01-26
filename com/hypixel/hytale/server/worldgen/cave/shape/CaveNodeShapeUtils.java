/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave.shape;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.function.function.BiDoubleToDoubleFunction;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.procedurallib.condition.ConstantBlockFluidCondition;
import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RequiredBlockFaceSupport;
import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.cave.element.CaveNode;
import com.hypixel.hytale.server.worldgen.cave.shape.CaveNodeShape;
import com.hypixel.hytale.server.worldgen.cave.shape.CylinderCaveNodeShape;
import com.hypixel.hytale.server.worldgen.cave.shape.DistortedCaveNodeShape;
import com.hypixel.hytale.server.worldgen.cave.shape.PipeCaveNodeShape;
import com.hypixel.hytale.server.worldgen.cave.shape.PrefabCaveNodeShape;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGeneratorExecution;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.bounds.IWorldBounds;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CaveNodeShapeUtils {
    public static final BiDoubleToDoubleFunction LEFT = (l, r) -> l;
    public static final BiDoubleToDoubleFunction RIGHT = (l, r) -> r;
    public static final BiDoubleToDoubleFunction MIN = Math::min;
    public static final BiDoubleToDoubleFunction MAX = Math::max;

    @Nonnull
    public static Vector3d getBoxAnchor(@Nonnull Vector3d vector, @Nonnull IWorldBounds bounds, double tx, double ty, double tz) {
        double x = bounds.fractionX(tx);
        double y = bounds.fractionY(ty);
        double z = bounds.fractionZ(tz);
        return vector.assign(x, y, z);
    }

    @Nonnull
    public static Vector3d getLineAnchor(@Nonnull Vector3d vector, @Nonnull Vector3d o, @Nonnull Vector3d v, double t) {
        double x = o.x + v.x * t;
        double y = o.y + v.y * t;
        double z = o.z + v.z * t;
        return vector.assign(x, y, z);
    }

    @Nonnull
    public static Vector3d getSphereAnchor(@Nonnull Vector3d vector, @Nonnull Vector3d origin, double rx, double ry, double rz, double tx, double ty, double tz) {
        double fx = tx * 2.0 - 1.0;
        double fy = ty * 2.0 - 1.0;
        double fz = tz * 2.0 - 1.0;
        return CaveNodeShapeUtils.getRadialProjection(vector, origin.x, origin.y, origin.z, rx, ry, rz, fx, fy, fz);
    }

    @Nonnull
    public static Vector3d getPipeAnchor(@Nonnull Vector3d vector, @Nonnull Vector3d o, @Nonnull Vector3d v, double rx, double ry, double rz, double t, double tv, double th) {
        double x = o.x + v.x * t;
        double y = o.y + v.y * t;
        double z = o.z + v.z * t;
        double len = v.length();
        double nx = v.x / len;
        double ny = v.y / len;
        double nz = v.z / len;
        double fv = 2.0 * tv - 1.0;
        double fh = 2.0 * th - 1.0;
        double fx = -ny * fv - nz * fh;
        double fy = nx * fv;
        double fz = nx * fh;
        return CaveNodeShapeUtils.getRadialProjection(vector, x, y, z, rx, ry, rz, fx, fy, fz);
    }

    @Nonnull
    public static Vector3d getOffset(@Nullable CaveNode parent, @Nonnull CaveNodeType.CaveNodeChildEntry childEntry) {
        Vector3d offset = childEntry.getOffset();
        if (offset == Vector3d.ZERO) {
            return offset;
        }
        if (parent != null && parent.getShape() instanceof PrefabCaveNodeShape) {
            offset = offset.clone();
            ((PrefabCaveNodeShape)parent.getShape()).getPrefabRotation().rotate(offset);
        }
        return offset;
    }

    public static double getEndRadius(@Nullable CaveNode node, @Nonnull IDoubleRange range, Random random) {
        double radius;
        if (node != null && (radius = CaveNodeShapeUtils.getEndRadius(node.getShape(), MIN)) != -1.0) {
            return radius;
        }
        return range.getValue(random);
    }

    public static double getEndWidth(@Nullable CaveNode node, @Nonnull IDoubleRange range, Random random) {
        double radius;
        if (node != null && (radius = CaveNodeShapeUtils.getEndRadius(node.getShape(), LEFT)) != -1.0) {
            return radius;
        }
        return range.getValue(random);
    }

    public static double getEndHeight(@Nullable CaveNode node, @Nonnull IDoubleRange range, Random random) {
        double radius;
        if (node != null && (radius = CaveNodeShapeUtils.getEndRadius(node.getShape(), RIGHT)) != -1.0) {
            return radius;
        }
        return range.getValue(random);
    }

    public static double getEndRadius(@Nonnull CaveNodeShape shape, @Nonnull BiDoubleToDoubleFunction widthHeightSelector) {
        if (shape instanceof CylinderCaveNodeShape) {
            return ((CylinderCaveNodeShape)shape).getRadius2();
        }
        if (shape instanceof PipeCaveNodeShape) {
            return ((PipeCaveNodeShape)shape).getRadius2();
        }
        if (shape instanceof DistortedCaveNodeShape) {
            double width = ((DistortedCaveNodeShape)shape).getShape().getWidthAt(1.0);
            double height = ((DistortedCaveNodeShape)shape).getShape().getHeightAt(1.0);
            return widthHeightSelector.apply(width, height);
        }
        return -1.0;
    }

    @Nullable
    public static BlockFluidEntry getFillingBlock(@Nonnull CaveType cave, @Nonnull CaveNodeType node, int y, @Nonnull Random random) {
        if (cave.getFluidLevel().getHeight() >= y) {
            return cave.getFluidLevel().getBlockEntry();
        }
        return node.getFilling(random);
    }

    protected static int getCoverHeight(int lowest, int lowestPossible, int highest, int highestPossible, boolean heightLimited, @Nonnull CaveNodeType.CaveNodeCoverEntry cover, @Nonnull CaveNodeType.CaveNodeCoverEntry.Entry entry) {
        switch (cover.getType()) {
            case FLOOR: {
                if (lowest == Integer.MAX_VALUE || lowestPossible != lowest) {
                    return -1;
                }
                return lowest - 1 + entry.getOffset();
            }
            case CEILING: {
                if (heightLimited) {
                    return -1;
                }
                if (highest == Integer.MIN_VALUE || highestPossible != highest) {
                    return -1;
                }
                return highest + 1 - entry.getOffset();
            }
        }
        throw new AssertionError((Object)"Not all cases covered!");
    }

    public static boolean isCoverMatchingParent(int cx, int cz, int y, @Nonnull ChunkGeneratorExecution execution, @Nonnull CaveNodeType.CaveNodeCoverEntry cover) {
        int parentY = y + cover.getType().parentOffset;
        if (parentY < 0 || parentY > 319) {
            return false;
        }
        IBlockFluidCondition parentCondition = cover.getParentCondition();
        if (parentCondition == ConstantBlockFluidCondition.DEFAULT_TRUE) {
            return true;
        }
        if (parentCondition == ConstantBlockFluidCondition.DEFAULT_FALSE) {
            return false;
        }
        int parent = execution.getBlock(cx, parentY, cz);
        int parentFluid = execution.getFluid(cx, parentY, cz);
        return parentCondition.eval(parent, parentFluid);
    }

    public static boolean invalidateCover(int x, int y, int z, CaveNodeType.CaveNodeCoverType type, @Nonnull ChunkGeneratorExecution execution, @Nonnull BlockTypeAssetMap<String, BlockType> blockTypeMap) {
        if (y < 0 || y > 319) {
            return false;
        }
        byte priority = execution.getPriorityChunk().get(x, y, z);
        if (priority == 3) {
            return true;
        }
        if (priority != 5) {
            return false;
        }
        int block = execution.getBlock(x, y, z);
        BlockType blockType = blockTypeMap.getAsset(block);
        Map<BlockFace, RequiredBlockFaceSupport[]> supportsMap = blockType.getSupport(execution.getRotationIndex(x, y, z));
        if (supportsMap == null) {
            return false;
        }
        return switch (type) {
            default -> throw new MatchException(null, null);
            case CaveNodeType.CaveNodeCoverType.FLOOR -> supportsMap.containsKey((Object)BlockFace.DOWN);
            case CaveNodeType.CaveNodeCoverType.CEILING -> supportsMap.containsKey((Object)BlockFace.UP);
        };
    }

    @Nonnull
    protected static Vector3d getRadialProjection(@Nonnull Vector3d vector, double x, double y, double z, double rx, double ry, double rz, double tx, double ty, double tz) {
        double len2 = tx * tx + ty * ty + tz * tz;
        if (len2 == 0.0) {
            return vector.assign(x, y, z);
        }
        double invLen = Math.sqrt(1.0 / len2);
        double dx = Math.abs(tx) * rx * invLen;
        double dy = Math.abs(ty) * ry * invLen;
        double dz = Math.abs(tz) * rz * invLen;
        return vector.assign(x += dx * tx, y += dy * ty, z += dz * tz);
    }
}

