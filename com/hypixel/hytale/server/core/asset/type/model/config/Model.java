/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.model.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.protocol.AnimationSet;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.ModelTrail;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.Phobia;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.DetailBox;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelParticle;
import com.hypixel.hytale.server.core.asset.type.model.config.camera.CameraSettings;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.io.NetworkSerializers;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Model
implements NetworkSerializable<com.hypixel.hytale.protocol.Model> {
    public static final String UNKNOWN_TEXTURE = "textures/Unknown.png";
    private final String modelAssetId;
    private final float scale;
    private final Map<String, String> randomAttachmentIds;
    private final ModelAttachment[] attachments;
    @Nullable
    private final Box boundingBox;
    @Nullable
    private final Box crouchBoundingBox;
    private final String model;
    private final String texture;
    private final String gradientSet;
    private final String gradientId;
    private final float eyeHeight;
    private final float crouchOffset;
    private final Map<String, ModelAsset.AnimationSet> animationSetMap;
    private final CameraSettings camera;
    private final ColorLight light;
    private final ModelParticle[] particles;
    private final ModelTrail[] trails;
    private final PhysicsValues physicsValues;
    private final Map<String, DetailBox[]> detailBoxes;
    private final Phobia phobia;
    private final String phobiaModelAssetId;
    private transient SoftReference<com.hypixel.hytale.protocol.Model> cachedPacket;

    public Model(String modelAssetId, float scale, Map<String, String> randomAttachmentIds, ModelAttachment[] attachments, @Nullable Box boundingBox, String model, String texture, String gradientSet, String gradientId, float eyeHeight, float crouchOffset, Map<String, ModelAsset.AnimationSet> animationSetMap, CameraSettings camera, ColorLight light, ModelParticle[] particles, ModelTrail[] trails, PhysicsValues physicsValues, Map<String, DetailBox[]> detailBoxes, Phobia phobia, String phobiaModelAssetId) {
        this.modelAssetId = modelAssetId;
        this.scale = scale;
        this.randomAttachmentIds = randomAttachmentIds;
        this.attachments = attachments;
        this.boundingBox = boundingBox;
        this.model = model;
        this.texture = texture;
        this.gradientSet = gradientSet;
        this.gradientId = gradientId;
        this.eyeHeight = eyeHeight;
        this.crouchOffset = crouchOffset;
        this.animationSetMap = animationSetMap;
        this.camera = camera;
        this.light = light;
        this.particles = particles;
        this.trails = trails;
        this.physicsValues = physicsValues;
        this.detailBoxes = detailBoxes;
        this.crouchBoundingBox = boundingBox == null ? null : new Box(boundingBox.min.clone(), boundingBox.max.clone().add(0.0, crouchOffset, 0.0));
        this.phobia = phobia;
        this.phobiaModelAssetId = phobiaModelAssetId;
    }

    public Model(@Nonnull Model other) {
        this.modelAssetId = other.modelAssetId;
        this.scale = other.scale;
        this.randomAttachmentIds = other.randomAttachmentIds;
        this.attachments = other.attachments;
        this.boundingBox = other.boundingBox;
        this.model = other.model;
        this.texture = other.texture;
        this.gradientSet = other.gradientSet;
        this.gradientId = other.gradientId;
        this.eyeHeight = other.eyeHeight;
        this.crouchOffset = other.crouchOffset;
        this.animationSetMap = other.animationSetMap;
        this.camera = other.camera;
        this.light = other.light;
        this.particles = other.particles;
        this.trails = other.trails;
        this.physicsValues = other.physicsValues;
        this.crouchBoundingBox = other.crouchBoundingBox;
        this.detailBoxes = other.detailBoxes;
        this.phobia = other.phobia;
        this.phobiaModelAssetId = other.phobiaModelAssetId;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.Model toPacket() {
        ModelAsset modelAsset;
        Object2ObjectOpenHashMap<String, AnimationSet> map;
        com.hypixel.hytale.protocol.Model cached;
        com.hypixel.hytale.protocol.Model model = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.Model packet = new com.hypixel.hytale.protocol.Model();
        if (this.modelAssetId != null) {
            packet.assetId = this.modelAssetId;
        }
        if (this.model != null) {
            packet.path = this.model;
        }
        packet.texture = this.texture != null ? this.texture : (this.model == null ? UNKNOWN_TEXTURE : this.model.replace(".blockymodel", ".png"));
        packet.gradientSet = this.gradientSet;
        packet.gradientId = this.gradientId;
        if (this.scale > 0.0f) {
            packet.scale = this.scale;
        }
        if (this.eyeHeight > 0.0f) {
            packet.eyeHeight = this.eyeHeight;
        }
        if (this.crouchOffset != 0.0f) {
            packet.crouchOffset = this.crouchOffset;
        }
        if (this.animationSetMap != null) {
            map = new Object2ObjectOpenHashMap<String, AnimationSet>(this.animationSetMap.size());
            for (Map.Entry<String, ModelAsset.AnimationSet> entry : this.animationSetMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toPacket(entry.getKey()));
            }
            packet.animationSets = map;
        }
        if (this.attachments != null && this.attachments.length > 0) {
            packet.attachments = new com.hypixel.hytale.protocol.ModelAttachment[this.attachments.length];
            for (int i = 0; i < this.attachments.length; ++i) {
                packet.attachments[i] = this.attachments[i].toPacket();
            }
        }
        if (this.boundingBox != null) {
            packet.hitbox = NetworkSerializers.BOX.toPacket(this.boundingBox);
        }
        packet.light = this.light;
        if (this.particles != null && this.particles.length > 0) {
            packet.particles = new com.hypixel.hytale.protocol.ModelParticle[this.particles.length];
            for (int i = 0; i < this.particles.length; ++i) {
                packet.particles[i] = this.particles[i].toPacket();
            }
        }
        packet.trails = this.trails;
        if (this.camera != null) {
            packet.camera = this.camera.toPacket();
        }
        if (this.detailBoxes != null) {
            packet.detailBoxes = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.DetailBox[]>(this.detailBoxes.size());
            map = packet.detailBoxes;
            for (Map.Entry<String, ModelAsset.AnimationSet> entry : this.detailBoxes.entrySet()) {
                map.put(entry.getKey(), (AnimationSet)((com.hypixel.hytale.protocol.DetailBox[])Arrays.stream((DetailBox[])entry.getValue()).map(NetworkSerializable::toPacket).toArray(com.hypixel.hytale.protocol.DetailBox[]::new)));
            }
        }
        if (this.phobia != Phobia.None && this.phobiaModelAssetId != null && (modelAsset = ModelAsset.getAssetMap().getAsset(this.phobiaModelAssetId)) != null) {
            Model model2 = Model.createUnitScaleModel(modelAsset, this.boundingBox);
            packet.phobiaModel = model2.toPacket();
            packet.phobia = this.phobia;
        }
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.Model>(packet);
        return packet;
    }

    public String getModelAssetId() {
        return this.modelAssetId;
    }

    public float getScale() {
        return this.scale;
    }

    public Map<String, String> getRandomAttachmentIds() {
        return this.randomAttachmentIds;
    }

    public ModelAttachment[] getAttachments() {
        return this.attachments;
    }

    @Nullable
    public Box getBoundingBox(@Nullable MovementStates movementStates) {
        if (movementStates == null) {
            return this.boundingBox;
        }
        return movementStates.crouching || movementStates.forcedCrouching ? this.crouchBoundingBox : this.boundingBox;
    }

    @Nullable
    public Box getBoundingBox() {
        return this.boundingBox;
    }

    @Nullable
    public Box getCrouchBoundingBox() {
        return this.crouchBoundingBox;
    }

    public String getModel() {
        return this.model;
    }

    public String getTexture() {
        return this.texture;
    }

    public String getGradientSet() {
        return this.gradientSet;
    }

    public String getGradientId() {
        return this.gradientId;
    }

    public float getEyeHeight() {
        return this.eyeHeight;
    }

    public float getCrouchOffset() {
        return this.crouchOffset;
    }

    public Map<String, ModelAsset.AnimationSet> getAnimationSetMap() {
        return this.animationSetMap != null ? this.animationSetMap : Collections.emptyMap();
    }

    @Nullable
    public String getFirstBoundAnimationId(@Nullable String id, @Nullable String fallbackId) {
        if (id != null && this.animationSetMap.containsKey(id)) {
            return id;
        }
        if (fallbackId != null && this.animationSetMap.containsKey(fallbackId)) {
            return fallbackId;
        }
        return null;
    }

    @Nullable
    public String getFirstBoundAnimationId(String ... preferenceOrder) {
        for (String animationId : preferenceOrder) {
            if (animationId == null || !this.animationSetMap.containsKey(animationId)) continue;
            return animationId;
        }
        return null;
    }

    public CameraSettings getCamera() {
        return this.camera;
    }

    public ColorLight getLight() {
        return this.light;
    }

    public ModelParticle[] getParticles() {
        return this.particles;
    }

    public ModelTrail[] getTrails() {
        return this.trails;
    }

    public PhysicsValues getPhysicsValues() {
        return this.physicsValues;
    }

    public Map<String, DetailBox[]> getDetailBoxes() {
        return this.detailBoxes;
    }

    public Phobia getPhobia() {
        return this.phobia;
    }

    public String getPhobiaModelAssetId() {
        return this.phobiaModelAssetId;
    }

    @Nonnull
    public ModelReference toReference() {
        if ("Player".equals(this.modelAssetId)) {
            return ModelReference.DEFAULT_PLAYER_MODEL;
        }
        return new ModelReference(this.modelAssetId, this.scale, this.randomAttachmentIds, this.animationSetMap == null);
    }

    public float getEyeHeight(@Nullable Ref<EntityStore> ref, @Nullable ComponentAccessor<EntityStore> componentAccessor) {
        if (ref == null || componentAccessor == null || !ref.isValid()) {
            return this.getEyeHeight();
        }
        MovementStatesComponent movementStatesComponent = componentAccessor.getComponent(ref, MovementStatesComponent.getComponentType());
        if (movementStatesComponent == null) {
            return this.getEyeHeight();
        }
        MovementStates movementStates = movementStatesComponent.getMovementStates();
        return movementStates.crouching ? this.getEyeHeight() + this.getCrouchOffset() : this.getEyeHeight();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Model model1 = (Model)o;
        if (Float.compare(model1.scale, this.scale) != 0) {
            return false;
        }
        if (Float.compare(model1.eyeHeight, this.eyeHeight) != 0) {
            return false;
        }
        if (Float.compare(model1.crouchOffset, this.crouchOffset) != 0) {
            return false;
        }
        if (!Objects.equals(this.modelAssetId, model1.modelAssetId)) {
            return false;
        }
        if (!Objects.equals(this.randomAttachmentIds, model1.randomAttachmentIds)) {
            return false;
        }
        if (!Arrays.equals(this.attachments, model1.attachments)) {
            return false;
        }
        if (!Objects.equals(this.boundingBox, model1.boundingBox)) {
            return false;
        }
        if (!Objects.equals(this.model, model1.model)) {
            return false;
        }
        if (!Objects.equals(this.texture, model1.texture)) {
            return false;
        }
        if (!Objects.equals(this.gradientSet, model1.gradientSet)) {
            return false;
        }
        if (!Objects.equals(this.gradientId, model1.gradientId)) {
            return false;
        }
        if (!Objects.equals(this.animationSetMap, model1.animationSetMap)) {
            return false;
        }
        if (!Objects.equals(this.camera, model1.camera)) {
            return false;
        }
        if (!Objects.equals(this.light, model1.light)) {
            return false;
        }
        if (!Arrays.equals(this.particles, model1.particles)) {
            return false;
        }
        if (!Arrays.equals(this.trails, model1.trails)) {
            return false;
        }
        return Objects.equals(this.physicsValues, model1.physicsValues);
    }

    public int hashCode() {
        int result = this.modelAssetId != null ? this.modelAssetId.hashCode() : 0;
        result = 31 * result + (this.scale != 0.0f ? Float.floatToIntBits(this.scale) : 0);
        result = 31 * result + (this.randomAttachmentIds != null ? this.randomAttachmentIds.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.attachments);
        result = 31 * result + (this.boundingBox != null ? this.boundingBox.hashCode() : 0);
        result = 31 * result + (this.model != null ? this.model.hashCode() : 0);
        result = 31 * result + (this.texture != null ? this.texture.hashCode() : 0);
        result = 31 * result + (this.gradientSet != null ? this.gradientSet.hashCode() : 0);
        result = 31 * result + (this.gradientId != null ? this.gradientId.hashCode() : 0);
        result = 31 * result + (this.eyeHeight != 0.0f ? Float.floatToIntBits(this.eyeHeight) : 0);
        result = 31 * result + (this.crouchOffset != 0.0f ? Float.floatToIntBits(this.crouchOffset) : 0);
        result = 31 * result + (this.animationSetMap != null ? this.animationSetMap.hashCode() : 0);
        result = 31 * result + (this.camera != null ? this.camera.hashCode() : 0);
        result = 31 * result + (this.light != null ? this.light.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Arrays.hashCode(this.trails);
        result = 31 * result + (this.physicsValues != null ? this.physicsValues.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Model{modelAssetId='" + this.modelAssetId + "', scale=" + this.scale + ", randomAttachmentIds=" + String.valueOf(this.randomAttachmentIds) + ", attachments=" + Arrays.toString(this.attachments) + ", boundingBox=" + String.valueOf(this.boundingBox) + ", crouchBoundingBox=" + String.valueOf(this.crouchBoundingBox) + ", model='" + this.model + "', texture='" + this.texture + "', gradientSet='" + this.gradientSet + "', gradientId='" + this.gradientId + "', eyeHeight=" + this.eyeHeight + ", crouchOffset=" + this.crouchOffset + ", animationSetMap=" + String.valueOf(this.animationSetMap) + ", camera=" + String.valueOf(this.camera) + ", light=" + String.valueOf(this.light) + ", particles=" + Arrays.toString(this.particles) + ", trails=" + Arrays.toString(this.trails) + ", physicsValues=" + String.valueOf(this.physicsValues) + ", detailBoxes=" + String.valueOf(this.detailBoxes) + ", phobia=" + String.valueOf((Object)this.phobia) + ", phobiaModelAssetId='" + this.phobiaModelAssetId + "'}";
    }

    @Nonnull
    public static Model createRandomScaleModel(@Nonnull ModelAsset modelAsset) {
        return Model.createScaledModel(modelAsset, modelAsset.generateRandomScale());
    }

    @Nonnull
    public static Model createStaticScaledModel(@Nonnull ModelAsset modelAsset, float scale) {
        return Model.createScaledModel(modelAsset, scale, modelAsset.generateRandomAttachmentIds(), null, true);
    }

    @Nonnull
    public static Model createUnitScaleModel(@Nonnull ModelAsset modelAsset) {
        return Model.createScaledModel(modelAsset, 1.0f, null);
    }

    @Nonnull
    public static Model createUnitScaleModel(@Nonnull ModelAsset modelAsset, @Nullable Box boundingBox) {
        return Model.createScaledModel(modelAsset, 1.0f, null, boundingBox);
    }

    @Nonnull
    public static Model createScaledModel(@Nonnull ModelAsset modelAsset, float scale) {
        return Model.createScaledModel(modelAsset, scale, modelAsset.generateRandomAttachmentIds());
    }

    @Nonnull
    public static Model createScaledModel(@Nonnull ModelAsset modelAsset, float scale, @Nullable Map<String, String> randomAttachmentIds) {
        return Model.createScaledModel(modelAsset, scale, randomAttachmentIds, null, false);
    }

    @Nonnull
    public static Model createScaledModel(@Nonnull ModelAsset modelAsset, float scale, @Nullable Map<String, String> randomAttachmentIds, @Nullable Box overrideBoundingBox) {
        return Model.createScaledModel(modelAsset, scale, randomAttachmentIds, overrideBoundingBox, false);
    }

    @Nonnull
    public static Model createScaledModel(@Nonnull ModelAsset modelAsset, float scale, @Nullable Map<String, String> randomAttachmentIds, @Nullable Box overrideBoundingBox, boolean staticModel) {
        Objects.requireNonNull(modelAsset, "ModelAsset can't be null");
        if (scale <= 0.0f) {
            throw new IllegalArgumentException("Scale must be greater than 0");
        }
        Box boundingBox = overrideBoundingBox != null ? overrideBoundingBox : modelAsset.getBoundingBox();
        Map<String, DetailBox[]> detailBoxes = modelAsset.getDetailBoxes();
        float eyeHeight = modelAsset.getEyeHeight();
        float crouchOffset = modelAsset.getCrouchOffset();
        CameraSettings camera = modelAsset.getCamera();
        PhysicsValues physicsValues = modelAsset.getPhysicsValues();
        ModelParticle[] particles = modelAsset.getParticles();
        ModelTrail[] trails = modelAsset.getTrails();
        if (scale != 1.0f) {
            boundingBox = boundingBox.clone().scale(scale);
            if (detailBoxes != null) {
                HashMap<String, DetailBox[]> scaledDetailBoxes = new HashMap<String, DetailBox[]>(detailBoxes.size());
                for (Map.Entry<String, DetailBox[]> entry : detailBoxes.entrySet()) {
                    scaledDetailBoxes.put(entry.getKey(), (DetailBox[])Arrays.stream(entry.getValue()).map(v -> v.scaled(scale)).toArray(DetailBox[]::new));
                }
                detailBoxes = scaledDetailBoxes;
            }
            eyeHeight *= scale;
            crouchOffset *= scale;
            if (camera != null) {
                camera = camera.clone().scale(scale);
            }
            if (physicsValues != null) {
                physicsValues = new PhysicsValues(physicsValues);
                physicsValues.scale(scale);
            }
            if (particles != null) {
                ModelParticle[] scaledParticules = new ModelParticle[particles.length];
                for (int i = 0; i < particles.length; ++i) {
                    scaledParticules[i] = particles[i].clone().scale(scale);
                }
                particles = scaledParticules;
            }
            if (trails != null) {
                ModelTrail[] scaledTrails = new ModelTrail[trails.length];
                for (int i = 0; i < trails.length; ++i) {
                    ModelTrail trail = trails[i];
                    ModelTrail scaledTrail = new ModelTrail(trail);
                    if (trail.positionOffset != null) {
                        scaledTrail.positionOffset = new Vector3f();
                        scaledTrail.positionOffset.x = trail.positionOffset.x * scale;
                        scaledTrail.positionOffset.y = trail.positionOffset.y * scale;
                        scaledTrail.positionOffset.z = trail.positionOffset.z * scale;
                    }
                    scaledTrails[i] = scaledTrail;
                }
                trails = scaledTrails;
            }
        }
        ModelAttachment[] attachments = modelAsset.getAttachments(randomAttachmentIds);
        Map<String, ModelAsset.AnimationSet> animationSetMap = staticModel ? null : modelAsset.getAnimationSetMap();
        return new Model(modelAsset.getId(), scale, randomAttachmentIds, attachments, boundingBox, modelAsset.getModel(), modelAsset.getTexture(), modelAsset.getGradientSet(), modelAsset.getGradientId(), eyeHeight, crouchOffset, animationSetMap, camera, modelAsset.getLight(), particles, trails, physicsValues, detailBoxes, modelAsset.getPhobia(), modelAsset.getPhobiaModelAssetId());
    }

    public static class ModelReference {
        public static final BuilderCodec<ModelReference> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ModelReference.class, ModelReference::new).addField(new KeyedCodec<String>("Id", Codec.STRING), (modelReference, s) -> {
            modelReference.modelAssetId = s;
        }, modelReference -> modelReference.modelAssetId)).addField(new KeyedCodec<Double>("Scale", Codec.DOUBLE), (modelReference, aDouble) -> {
            modelReference.scale = aDouble.floatValue();
        }, modelReference -> modelReference.scale)).addField(new KeyedCodec("RandomAttachments", MapCodec.STRING_HASH_MAP_CODEC), (modelReference, stringStringMap) -> {
            modelReference.randomAttachmentIds = stringStringMap;
        }, modelReference -> modelReference.randomAttachmentIds)).addField(new KeyedCodec<Boolean>("Static", Codec.BOOLEAN), (modelReference, b) -> {
            modelReference.staticModel = b;
        }, modelReference -> modelReference.staticModel)).build();
        public static final ModelReference DEFAULT_PLAYER_MODEL = new ModelReference("Player", -1.0f, null, false);
        private String modelAssetId;
        private float scale;
        private Map<String, String> randomAttachmentIds;
        private boolean staticModel;

        public ModelReference(String modelAssetId, float scale, Map<String, String> randomAttachmentIds) {
            this(modelAssetId, scale, randomAttachmentIds, false);
        }

        public ModelReference(String modelAssetId, float scale, Map<String, String> randomAttachmentIds, boolean staticModel) {
            this.modelAssetId = modelAssetId;
            this.scale = scale;
            this.randomAttachmentIds = randomAttachmentIds;
            this.staticModel = staticModel;
        }

        protected ModelReference() {
        }

        public String getModelAssetId() {
            return this.modelAssetId;
        }

        public float getScale() {
            return this.scale;
        }

        public Map<String, String> getRandomAttachmentIds() {
            return this.randomAttachmentIds;
        }

        public boolean isStaticModel() {
            return this.staticModel;
        }

        @Nullable
        public Model toModel() {
            if (this.modelAssetId == null) {
                return null;
            }
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(this.modelAssetId);
            if (modelAsset == null) {
                modelAsset = ModelAsset.DEBUG;
            }
            return Model.createScaledModel(modelAsset, this.scale, this.randomAttachmentIds, null, this.staticModel);
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ModelReference that = (ModelReference)o;
            if (Float.compare(that.scale, this.scale) != 0) {
                return false;
            }
            if (this.staticModel != that.staticModel) {
                return false;
            }
            if (!Objects.equals(this.modelAssetId, that.modelAssetId)) {
                return false;
            }
            return Objects.equals(this.randomAttachmentIds, that.randomAttachmentIds);
        }

        public int hashCode() {
            int result = this.modelAssetId != null ? this.modelAssetId.hashCode() : 0;
            result = 31 * result + (this.scale != 0.0f ? Float.floatToIntBits(this.scale) : 0);
            result = 31 * result + (this.randomAttachmentIds != null ? this.randomAttachmentIds.hashCode() : 0);
            result = 31 * result + (this.staticModel ? 1 : 0);
            return result;
        }

        @Nonnull
        public String toString() {
            return "ModelReference{modelAssetId='" + this.modelAssetId + "', scale=" + this.scale + ", randomAttachmentIds=" + String.valueOf(this.randomAttachmentIds) + ", staticModel=" + this.staticModel + "}";
        }
    }
}

