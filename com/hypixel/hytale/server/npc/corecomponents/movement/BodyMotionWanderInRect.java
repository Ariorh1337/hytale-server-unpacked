/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.movement.BodyMotionWanderBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderBodyMotionWanderInRect;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nonnull;

public class BodyMotionWanderInRect
extends BodyMotionWanderBase {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 4;
    public static final int TOP = 8;
    public static final int VERTICAL_MASK = 12;
    public static final int HORIZONTAL_MASK = 3;
    protected final double width;
    protected final double depth;
    protected final double halfWidth;
    protected final double halfDepth;

    public BodyMotionWanderInRect(@Nonnull BuilderBodyMotionWanderInRect builder, @Nonnull BuilderSupport builderSupport) {
        super(builder, builderSupport);
        this.width = builder.getWidth();
        this.halfWidth = this.width / 2.0;
        this.depth = builder.getDepth();
        this.halfDepth = this.depth / 2.0;
    }

    @Override
    protected double constrainMove(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nonnull Vector3d probePosition, @Nonnull Vector3d targetPosition, double moveDist, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        double leashZ;
        double endZ;
        NPCEntity npcComponent = componentAccessor.getComponent(ref, NPCEntity.getComponentType());
        assert (npcComponent != null);
        Vector3d leash = npcComponent.getLeashPoint();
        double leashX = leash.getX();
        double endX = targetPosition.x - leashX;
        int endCode = this.sectorCode(endX, endZ = targetPosition.z - (leashZ = leash.getZ()));
        if (endCode == 0) {
            return moveDist;
        }
        double startX = probePosition.x - leashX;
        double startZ = probePosition.z - leashZ;
        int startCode = this.sectorCode(startX, startZ);
        if (startCode != 0) {
            if ((startCode & endCode) != 0) {
                if (this.distanceSquared(endX, endZ, endCode) < this.distanceSquared(startX, startZ, startCode)) {
                    return moveDist;
                }
                return 0.0;
            }
            int or = startCode | endCode;
            if ((or & 0xC) == 12 || (or & 3) == 3) {
                return 0.0;
            }
            if (this.distanceSquared(endX, endZ, endCode) < this.distanceSquared(startX, startZ, startCode)) {
                return moveDist;
            }
            return 0.0;
        }
        double dx = endX - startX;
        double dz = endZ - startZ;
        double scaleX = (endCode & 1) == 1 ? (-this.halfWidth - startX) / dx : ((endCode & 2) == 2 ? (this.halfWidth - startX) / dx : 1.0);
        double scaleZ = (endCode & 4) == 4 ? (-this.halfDepth - startZ) / dz : ((endCode & 8) == 8 ? (this.halfDepth - startZ) / dz : 1.0);
        if (scaleX < 0.0 || scaleX > 1.0) {
            throw new IllegalArgumentException("WanderInRect: Constrained X outside of allowed range!");
        }
        if (scaleZ < 0.0 || scaleZ > 1.0) {
            throw new IllegalArgumentException("WanderInRect: Constrained Z outside of allowed range!");
        }
        return moveDist * Math.min(scaleX, scaleZ);
    }

    protected int sectorCode(double x, double z) {
        int code = 0;
        if (x < -this.halfWidth) {
            code |= 1;
        } else if (x > this.halfWidth) {
            code |= 2;
        }
        if (z < -this.halfDepth) {
            code |= 4;
        } else if (z > this.halfDepth) {
            code |= 8;
        }
        return code;
    }

    protected double distanceSquared(double x, double z, int sector) {
        return switch (sector) {
            case 1 -> (x + this.halfWidth) * (x + this.halfWidth);
            case 2 -> (x - this.halfWidth) * (x - this.halfWidth);
            case 4 -> (z + this.halfDepth) * (z + this.halfDepth);
            case 8 -> (z - this.halfDepth) * (z - this.halfDepth);
            case 9 -> (x + this.halfWidth) * (x + this.halfWidth) + (z - this.halfDepth) * (z - this.halfDepth);
            case 5 -> (x + this.halfWidth) * (x + this.halfWidth) + (z + this.halfDepth) * (z + this.halfDepth);
            case 10 -> (x - this.halfWidth) * (x - this.halfWidth) + (z - this.halfDepth) * (z - this.halfDepth);
            case 6 -> (x - this.halfWidth) * (x - this.halfWidth) + (z + this.halfDepth) * (z + this.halfDepth);
            default -> 0.0;
        };
    }
}

