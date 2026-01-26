/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.math.hitdetection.HitDetectionExecutor;
import com.hypixel.hytale.math.hitdetection.LineOfSightProvider;
import com.hypixel.hytale.math.hitdetection.projection.FrustumProjectionProvider;
import com.hypixel.hytale.math.hitdetection.view.DirectionViewProvider;
import com.hypixel.hytale.math.iterator.BlockIterator;
import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.HorizontalSelectorDirection;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.SelectorType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class HorizontalSelector
extends SelectorType {
    @Nonnull
    public static final BuilderCodec<HorizontalSelector> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HorizontalSelector.class, HorizontalSelector::new, BASE_CODEC).documentation("A selector that swings in a horizontal arc over a given period of time.")).appendInherited(new KeyedCodec<Double>("Length", Codec.DOUBLE), (selector, value) -> {
        selector.yawLength = Math.toRadians(value);
    }, selector -> Math.toDegrees(selector.yawLength), (selector, parent) -> {
        selector.yawLength = parent.yawLength;
    }).documentation("The angle length in degrees that the arc will cover.").add()).appendInherited(new KeyedCodec<Direction>("Direction", Direction.CODEC), (selector, value) -> {
        selector.direction = value;
    }, selector -> selector.direction, (selector, parent) -> {
        selector.direction = parent.direction;
    }).documentation("The direction the swing should travel in.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("StartDistance", Codec.DOUBLE), (selector, value) -> {
        selector.startDistance = value;
    }, selector -> selector.startDistance, (selector, parent) -> {
        selector.startDistance = parent.startDistance;
    }).documentation("The distance from the entity that the selector starts its search from.").add()).appendInherited(new KeyedCodec<Double>("EndDistance", Codec.DOUBLE), (selector, value) -> {
        selector.endDistance = value;
    }, selector -> selector.endDistance, (selector, parent) -> {
        selector.endDistance = parent.endDistance;
    }).documentation("The distance from the entity that the selector ends its search at.").add()).appendInherited(new KeyedCodec<Double>("ExtendTop", Codec.DOUBLE), (selector, value) -> {
        selector.extendTop = value;
    }, selector -> selector.extendTop, (selector, parent) -> {
        selector.extendTop = parent.extendTop;
    }).documentation("The amount to extend the top of the selector by.").add()).appendInherited(new KeyedCodec<Double>("ExtendBottom", Codec.DOUBLE), (selector, value) -> {
        selector.extendBottom = value;
    }, selector -> selector.extendBottom, (selector, parent) -> {
        selector.extendBottom = parent.extendBottom;
    }).documentation("The amount to extend the bottom of the selector by.").add()).appendInherited(new KeyedCodec<Double>("YawStartOffset", Codec.DOUBLE), (selector, value) -> {
        selector.yawStartOffset = Math.toRadians(value);
    }, selector -> Math.toDegrees(selector.yawStartOffset), (selector, parent) -> {
        selector.yawStartOffset = parent.yawStartOffset;
    }).documentation("The yaw rotation offset in degrees for this selector").add()).appendInherited(new KeyedCodec<Double>("PitchOffset", Codec.DOUBLE), (selector, value) -> {
        selector.pitchOffset = Math.toRadians(value);
    }, selector -> Math.toDegrees(selector.pitchOffset), (selector, parent) -> {
        selector.pitchOffset = parent.pitchOffset;
    }).documentation("The pitch rotation offset in degrees for this selector").add()).appendInherited(new KeyedCodec<Double>("RollOffset", Codec.DOUBLE), (selector, value) -> {
        selector.rollOffset = Math.toRadians(value);
    }, selector -> Math.toDegrees(selector.rollOffset), (selector, parent) -> {
        selector.rollOffset = parent.rollOffset;
    }).documentation("The roll rotation offset in degrees for this selector").add()).appendInherited(new KeyedCodec<Boolean>("TestLineOfSight", Codec.BOOLEAN), (selector, value) -> {
        selector.testLineOfSight = value;
    }, selector -> selector.testLineOfSight, (selector, parent) -> {
        selector.testLineOfSight = parent.testLineOfSight;
    }).documentation("Whether to test for line of sight between the user and the target before counting a hit").add()).build();
    protected double extendTop = 1.0;
    protected double extendBottom = 1.0;
    protected double yawLength;
    protected double yawStartOffset;
    protected double pitchOffset;
    protected double rollOffset;
    protected double startDistance = 0.01;
    protected double endDistance;
    protected Direction direction;
    protected boolean testLineOfSight = true;

    @Override
    @Nonnull
    public Selector newSelector() {
        return new RuntimeSelector();
    }

    @Override
    public com.hypixel.hytale.protocol.Selector toPacket() {
        com.hypixel.hytale.protocol.HorizontalSelector selector = new com.hypixel.hytale.protocol.HorizontalSelector();
        selector.extendTop = (float)this.extendTop;
        selector.extendBottom = (float)this.extendBottom;
        selector.yawLength = (float)this.yawLength;
        selector.yawStartOffset = (float)this.yawStartOffset;
        selector.pitchOffset = (float)this.pitchOffset;
        selector.rollOffset = (float)this.rollOffset;
        selector.startDistance = (float)this.startDistance;
        selector.endDistance = (float)this.endDistance;
        selector.direction = switch (this.direction.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> HorizontalSelectorDirection.ToLeft;
            case 0 -> HorizontalSelectorDirection.ToRight;
        };
        selector.testLineOfSight = this.testLineOfSight;
        return selector;
    }

    private class RuntimeSelector
    implements Selector {
        @Nonnull
        protected HitDetectionExecutor executor = new HitDetectionExecutor();
        @Nonnull
        protected Matrix4d modelMatrix = new Matrix4d();
        @Nonnull
        protected FrustumProjectionProvider projectionProvider = new FrustumProjectionProvider();
        @Nonnull
        protected DirectionViewProvider viewProvider = new DirectionViewProvider();
        protected float lastTime;
        protected double runTimeDeltaPercentageSum;

        private RuntimeSelector() {
        }

        @Override
        public void tick(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, float time, float runTime) {
            ModelComponent modelComponent = commandBuffer.getComponent(attacker, ModelComponent.getComponentType());
            assert (modelComponent != null);
            float yOffset = modelComponent.getModel().getEyeHeight(attacker, commandBuffer);
            TransformComponent transformComponent = commandBuffer.getComponent(attacker, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            HeadRotation headRotationComponent = commandBuffer.getComponent(attacker, HeadRotation.getComponentType());
            assert (headRotationComponent != null);
            double posX = position.getX();
            double posY = position.getY() + (double)yOffset;
            double posZ = position.getZ();
            float delta = time - this.lastTime;
            this.lastTime = time;
            float runTimeDeltaPercentage = delta / runTime;
            double stretchFactor = HorizontalSelector.this.startDistance / HorizontalSelector.this.endDistance;
            double yawDelta = HorizontalSelector.this.yawLength * (double)runTimeDeltaPercentage;
            double yawDeltaSum = HorizontalSelector.this.yawLength * this.runTimeDeltaPercentageSum;
            double yawArcLength = 2.0 * HorizontalSelector.this.endDistance * yawDelta / 3.1415927410125732;
            double yawOffset = (yawDeltaSum + yawDelta + HorizontalSelector.this.yawStartOffset) * HorizontalSelector.this.direction.yawModifier;
            double extendHorizontal = yawArcLength * stretchFactor;
            this.projectionProvider.setNear(HorizontalSelector.this.startDistance).setFar(HorizontalSelector.this.endDistance).setLeft(extendHorizontal).setRight(extendHorizontal).setBottom(HorizontalSelector.this.extendBottom * stretchFactor).setRotation(yawOffset, HorizontalSelector.this.pitchOffset, HorizontalSelector.this.rollOffset).setTop(HorizontalSelector.this.extendTop * stretchFactor);
            this.viewProvider.setPosition(posX, posY, posZ).setDirection(headRotationComponent.getRotation().getYaw(), headRotationComponent.getRotation().getPitch());
            this.executor.setOrigin(posX, posY, posZ).setProjectionProvider(this.projectionProvider).setViewProvider(this.viewProvider);
            if (HorizontalSelector.this.testLineOfSight) {
                World world = commandBuffer.getStore().getExternalData().getWorld();
                LineOfSightProvider provider = (fromX, fromY, fromZ, toX, toY, toZ) -> {
                    LocalCachedChunkAccessor localAccessor = LocalCachedChunkAccessor.atWorldCoords(world, (int)fromX, (int)fromZ, (int)(HorizontalSelector.this.endDistance + 1.0));
                    return BlockIterator.iterateFromTo(fromX, fromY, fromZ, toX, toY, toZ, (x, y, z, px, py, pz, qx, qy, qz, accessor) -> {
                        long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
                        WorldChunk worldChunk = accessor.getChunkIfInMemory(chunkIndex);
                        if (worldChunk == null) {
                            return true;
                        }
                        BlockType blockType = worldChunk.getBlockType(x, y, z);
                        if (blockType == null) {
                            return true;
                        }
                        if (blockType.getMaterial() == BlockMaterial.Solid) {
                            int hitboxTypeIndex = blockType.getHitboxTypeIndex();
                            BlockBoundingBoxes blockHitboxes = BlockBoundingBoxes.getAssetMap().getAsset(hitboxTypeIndex);
                            if (blockHitboxes == null) {
                                return true;
                            }
                            Vector3d lineFrom = new Vector3d(fromX, fromY, fromZ);
                            Vector3d lineTo = new Vector3d(toX, toY, toZ);
                            BlockBoundingBoxes.RotatedVariantBoxes rotatedHitboxes = blockHitboxes.get(accessor.getBlockRotationIndex(x, y, z));
                            for (Box box : rotatedHitboxes.getDetailBoxes()) {
                                Box offsetBox = box.clone().offset(x, y, z);
                                if (!offsetBox.intersectsLine(lineFrom, lineTo)) continue;
                                return false;
                            }
                        }
                        return true;
                    }, localAccessor);
                };
                this.executor.setLineOfSightProvider(provider);
            } else {
                this.executor.setLineOfSightProvider(LineOfSightProvider.DEFAULT_TRUE);
            }
            if (SelectInteraction.SHOW_VISUAL_DEBUG) {
                Matrix4d tmp = new Matrix4d();
                Matrix4d matrix = new Matrix4d();
                matrix.identity().translate(posX, posY, posZ).rotateAxis(-headRotationComponent.getRotation().getYaw(), 0.0, 1.0, 0.0, tmp).rotateAxis(-headRotationComponent.getRotation().getPitch(), 1.0, 0.0, 0.0, tmp);
                Vector3f color = new Vector3f((float)HashUtil.random(attacker.getIndex(), this.hashCode(), 10L), (float)HashUtil.random(attacker.getIndex(), this.hashCode(), 11L), (float)HashUtil.random(attacker.getIndex(), this.hashCode(), 12L));
                DebugUtils.addFrustum(commandBuffer.getExternalData().getWorld(), matrix, this.projectionProvider.getMatrix(), color, 5.0f, true);
            }
            this.runTimeDeltaPercentageSum += (double)runTimeDeltaPercentage;
        }

        @Override
        public void selectTargetEntities(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, @Nonnull BiConsumer<Ref<EntityStore>, Vector4d> consumer, Predicate<Ref<EntityStore>> filter) {
            Selector.selectNearbyEntities(commandBuffer, attacker, HorizontalSelector.this.endDistance + 3.0, (Ref<EntityStore> entity) -> {
                BoundingBox boundingBoxComponent = commandBuffer.getComponent((Ref<EntityStore>)entity, BoundingBox.getComponentType());
                if (boundingBoxComponent == null) {
                    return;
                }
                Box hitbox = boundingBoxComponent.getBoundingBox();
                TransformComponent transformComponent = commandBuffer.getComponent((Ref<EntityStore>)entity, TransformComponent.getComponentType());
                if (transformComponent == null) {
                    return;
                }
                this.modelMatrix.identity().translate(transformComponent.getPosition()).translate(hitbox.getMin()).scale(hitbox.width(), hitbox.height(), hitbox.depth());
                if (this.executor.test(HitDetectionExecutor.CUBE_QUADS, this.modelMatrix)) {
                    consumer.accept((Ref<EntityStore>)entity, this.executor.getHitLocation());
                }
            }, filter);
        }

        @Override
        public void selectTargetBlocks(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, @Nonnull TriIntConsumer consumer) {
            Selector.selectNearbyBlocks(commandBuffer, attacker, HorizontalSelector.this.startDistance + HorizontalSelector.this.endDistance, (x, y, z) -> {
                World world = ((EntityStore)commandBuffer.getStore().getExternalData()).getWorld();
                WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
                if (chunk == null) {
                    return;
                }
                BlockType blockType = chunk.getBlockType(x, y, z);
                if (blockType == null || blockType == BlockType.EMPTY) {
                    return;
                }
                int rotation = chunk.getRotationIndex(x, y, z);
                int hitboxTypeIndex = blockType.getHitboxTypeIndex();
                BlockBoundingBoxes boundingBoxAsset = BlockBoundingBoxes.getAssetMap().getAsset(hitboxTypeIndex);
                if (boundingBoxAsset == null) {
                    return;
                }
                Box[] hitboxes = boundingBoxAsset.get(rotation).getDetailBoxes();
                for (int i = 0; i < hitboxes.length; ++i) {
                    Box hitbox = hitboxes[i];
                    this.modelMatrix.identity().translate(x, y, z).translate(hitbox.getMin()).scale(hitbox.width(), hitbox.height(), hitbox.depth());
                    if (!this.executor.test(HitDetectionExecutor.CUBE_QUADS, this.modelMatrix)) continue;
                    consumer.accept(x, y, z);
                    break;
                }
            });
        }
    }

    public static enum Direction {
        TO_RIGHT(-1.0),
        TO_LEFT(1.0);

        @Nonnull
        public static final EnumCodec<Direction> CODEC;
        private final double yawModifier;

        private Direction(double yawModifier) {
            this.yawModifier = yawModifier;
        }

        static {
            CODEC = new EnumCodec<Direction>(Direction.class).documentKey(TO_LEFT, "A arc that starts at the right and moves towards the left.").documentKey(TO_RIGHT, "A arc that starts at the left and moves towards the right.");
        }
    }
}

