/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.tooloperations;

import com.hypixel.hytale.builtin.buildertools.tooloperations.ToolOperation;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class LayersOperation
extends ToolOperation {
    private final Vector3i depthDirection;
    private final int layerOneLength;
    private final int layerTwoLength;
    private final boolean enableLayerTwo;
    private final int layerThreeLength;
    private final boolean enableLayerThree;
    private final BlockPattern layerOneBlockPattern;
    private final BlockPattern layerTwoBlockPattern;
    private final BlockPattern layerThreeBlockPattern;
    private final int brushDensity;
    private final int maxDepthNecessary;
    private boolean failed;
    private final int layerTwoDepthEnd;
    private final int layerThreeDepthEnd;
    private final boolean skipLayerOne;
    private final boolean skipLayerTwo;
    private final boolean skipLayerThree;

    public LayersOperation(@Nonnull Ref<EntityStore> ref, @Nonnull Player player, @Nonnull BuilderToolOnUseInteraction packet, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        super(ref, packet, componentAccessor);
        HeadRotation headRotationComponent = componentAccessor.getComponent(ref, HeadRotation.getComponentType());
        assert (headRotationComponent != null);
        switch ((String)this.args.tool().get("aDirection")) {
            case "Up": {
                this.depthDirection = Vector3i.UP;
                break;
            }
            case "Down": {
                this.depthDirection = Vector3i.DOWN;
                break;
            }
            case "North": {
                this.depthDirection = Vector3i.NORTH;
                break;
            }
            case "South": {
                this.depthDirection = Vector3i.SOUTH;
                break;
            }
            case "East": {
                this.depthDirection = Vector3i.EAST;
                break;
            }
            case "West": {
                this.depthDirection = Vector3i.WEST;
                break;
            }
            case "Camera": {
                this.depthDirection = headRotationComponent.getAxisDirection();
                break;
            }
            default: {
                this.depthDirection = Vector3i.DOWN;
            }
        }
        this.brushDensity = (Integer)this.args.tool().get("jBrushDensity");
        this.layerOneLength = (Integer)this.args.tool().get("bLayerOneLength");
        this.layerTwoLength = (Integer)this.args.tool().get("eLayerTwoLength");
        this.layerThreeLength = (Integer)this.args.tool().get("hLayerThreeLength");
        this.layerOneBlockPattern = (BlockPattern)this.args.tool().get("cLayerOneMaterial");
        this.layerTwoBlockPattern = (BlockPattern)this.args.tool().get("fLayerTwoMaterial");
        this.layerThreeBlockPattern = (BlockPattern)this.args.tool().get("iLayerThreeMaterial");
        this.enableLayerTwo = (Boolean)this.args.tool().get("dEnableLayerTwo");
        this.enableLayerThree = (Boolean)this.args.tool().get("gEnableLayerThree");
        this.skipLayerOne = (Boolean)this.args.tool().getOrDefault("kSkipLayerOne", false);
        this.skipLayerTwo = (Boolean)this.args.tool().getOrDefault("lSkipLayerTwo", false);
        this.skipLayerThree = (Boolean)this.args.tool().getOrDefault("mSkipLayerThree", false);
        this.maxDepthNecessary = this.layerOneLength + (this.enableLayerTwo ? this.layerTwoLength : 0) + (this.enableLayerThree ? this.layerThreeLength : 0);
        this.layerTwoDepthEnd = this.layerOneLength + this.layerTwoLength;
        this.layerThreeDepthEnd = this.layerTwoDepthEnd + this.layerThreeLength;
        if (this.enableLayerThree && !this.enableLayerTwo) {
            player.sendMessage(Message.translation("server.builderTools.layerOperation.layerTwoRequired"));
            this.failed = true;
        }
    }

    @Override
    boolean execute0(int x, int y, int z) {
        block10: {
            block14: {
                block13: {
                    block12: {
                        block11: {
                            block9: {
                                if (this.failed) {
                                    return false;
                                }
                                if (this.random.nextInt(100) > this.brushDensity) {
                                    return true;
                                }
                                int currentBlock = this.edit.getBlock(x, y, z);
                                if (currentBlock <= 0) {
                                    return true;
                                }
                                if (this.depthDirection.x != 1) break block9;
                                for (int i = 0; i < this.maxDepthNecessary; ++i) {
                                    if (this.edit.getBlock(x - i - 1, y, z) > 0 || !this.attemptSetBlock(x, y, z, i)) continue;
                                    return true;
                                }
                                break block10;
                            }
                            if (this.depthDirection.x != -1) break block11;
                            for (int i = 0; i < this.maxDepthNecessary; ++i) {
                                if (this.edit.getBlock(x + i + 1, y, z) > 0 || !this.attemptSetBlock(x, y, z, i)) continue;
                                return true;
                            }
                            break block10;
                        }
                        if (this.depthDirection.y != 1) break block12;
                        for (int i = 0; i < this.maxDepthNecessary; ++i) {
                            if (this.edit.getBlock(x, y - i - 1, z) > 0 || !this.attemptSetBlock(x, y, z, i)) continue;
                            return true;
                        }
                        break block10;
                    }
                    if (this.depthDirection.y != -1) break block13;
                    for (int i = 0; i < this.maxDepthNecessary; ++i) {
                        if (this.edit.getBlock(x, y + i + 1, z) > 0 || !this.attemptSetBlock(x, y, z, i)) continue;
                        return true;
                    }
                    break block10;
                }
                if (this.depthDirection.z != 1) break block14;
                for (int i = 0; i < this.maxDepthNecessary; ++i) {
                    if (this.edit.getBlock(x, y, z - i - 1) > 0 || !this.attemptSetBlock(x, y, z, i)) continue;
                    return true;
                }
                break block10;
            }
            if (this.depthDirection.z != -1) break block10;
            for (int i = 0; i < this.maxDepthNecessary; ++i) {
                if (this.edit.getBlock(x, y, z + i + 1) > 0 || !this.attemptSetBlock(x, y, z, i)) continue;
                return true;
            }
        }
        return true;
    }

    public boolean attemptSetBlock(int x, int y, int z, int depth) {
        if (depth < this.layerOneLength) {
            if (!this.skipLayerOne) {
                this.edit.setBlock(x, y, z, this.layerOneBlockPattern.nextBlock(this.random));
            }
            return true;
        }
        if (this.enableLayerTwo && depth < this.layerTwoDepthEnd) {
            if (!this.skipLayerTwo && !this.layerTwoBlockPattern.isEmpty()) {
                this.edit.setBlock(x, y, z, this.layerTwoBlockPattern.nextBlock(this.random));
            }
            return true;
        }
        if (this.enableLayerThree && depth < this.layerThreeDepthEnd) {
            if (!this.skipLayerThree && !this.layerThreeBlockPattern.isEmpty()) {
                this.edit.setBlock(x, y, z, this.layerThreeBlockPattern.nextBlock(this.random));
            }
            return true;
        }
        return false;
    }
}

