/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.component;

import com.hypixel.hytale.builtin.adventure.teleporter.TeleporterPlugin;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.wordlist.WordList;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Teleporter
implements Component<ChunkStore> {
    public static final BuilderCodec<Teleporter> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Teleporter.class, Teleporter::new).append(new KeyedCodec<UUID>("World", Codec.UUID_BINARY), (teleporter, uuid) -> {
        teleporter.worldUuid = uuid;
    }, teleporter -> teleporter.worldUuid).add()).append(new KeyedCodec<Transform>("Transform", Transform.CODEC), (teleporter, transform) -> {
        teleporter.transform = transform;
    }, teleporter -> teleporter.transform).add()).append(new KeyedCodec<Byte>("Relative", Codec.BYTE), (teleporter, b) -> {
        teleporter.relativeMask = b;
    }, teleporter -> teleporter.relativeMask).add()).append(new KeyedCodec<String>("Warp", Codec.STRING), (teleporter, s) -> {
        teleporter.warp = s;
    }, teleporter -> teleporter.warp).add()).append(new KeyedCodec<String>("OwnedWarp", Codec.STRING), (teleporter, s) -> {
        teleporter.ownedWarp = s;
    }, teleporter -> teleporter.ownedWarp).add()).append(new KeyedCodec<Boolean>("IsCustomName", Codec.BOOLEAN), (teleporter, s) -> {
        teleporter.isCustomName = s;
    }, teleporter -> teleporter.isCustomName).add()).append(new KeyedCodec<String>("WarpNameWordList", Codec.STRING), (teleporter, s) -> {
        teleporter.warpNameWordListKey = s;
    }, teleporter -> teleporter.warpNameWordListKey).documentation("The ID of the Word list to select default warp names from").add()).build();
    @Nullable
    private UUID worldUuid;
    @Nullable
    private Transform transform;
    private byte relativeMask = 0;
    @Nullable
    private String warp;
    @Deprecated
    private String ownedWarp;
    private boolean isCustomName;
    private String warpNameWordListKey;

    public static ComponentType<ChunkStore, Teleporter> getComponentType() {
        return TeleporterPlugin.get().getTeleporterComponentType();
    }

    @Nullable
    public UUID getWorldUuid() {
        return this.worldUuid;
    }

    public void setWorldUuid(@Nullable UUID worldUuid) {
        this.worldUuid = worldUuid;
    }

    @Nullable
    public Transform getTransform() {
        return this.transform;
    }

    public void setTransform(@Nullable Transform transform) {
        this.transform = transform;
    }

    public byte getRelativeMask() {
        return this.relativeMask;
    }

    public void setRelativeMask(byte relativeMask) {
        this.relativeMask = relativeMask;
    }

    @Nullable
    public String getWarp() {
        return this.warp;
    }

    public void setWarp(@Nullable String warp) {
        this.warp = warp == null || warp.isEmpty() ? null : warp;
    }

    public String getOwnedWarp() {
        return this.ownedWarp;
    }

    public void setOwnedWarp(String ownedWarp) {
        this.ownedWarp = ownedWarp;
    }

    public boolean hasOwnedWarp() {
        return this.ownedWarp != null && !this.ownedWarp.isEmpty();
    }

    public void setWarpNameWordListKey(String warpNameWordListKey) {
        this.warpNameWordListKey = warpNameWordListKey;
    }

    public boolean isCustomName() {
        return this.isCustomName;
    }

    public void setIsCustomName(boolean customName) {
        this.isCustomName = customName;
    }

    @Nullable
    public String getWarpNameWordListKey() {
        return this.warpNameWordListKey;
    }

    @Nullable
    public WordList getWarpNameWordList() {
        return WordList.getWordList(this.warpNameWordListKey);
    }

    public boolean isValid() {
        if (this.warp != null && !this.warp.isEmpty()) {
            return TeleportPlugin.get().getWarps().get(this.warp.toLowerCase()) != null;
        }
        if (this.transform != null) {
            if (this.worldUuid != null) {
                return Universe.get().getWorld(this.worldUuid) != null;
            }
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public Component<ChunkStore> clone() {
        Teleporter teleporter = new Teleporter();
        teleporter.worldUuid = this.worldUuid;
        teleporter.transform = this.transform != null ? this.transform.clone() : null;
        teleporter.relativeMask = this.relativeMask;
        teleporter.warp = this.warp;
        teleporter.ownedWarp = this.ownedWarp;
        teleporter.isCustomName = this.isCustomName;
        teleporter.warpNameWordListKey = this.warpNameWordListKey;
        return teleporter;
    }

    @Nullable
    public Teleport toTeleport(@Nonnull Vector3d currentPosition, @Nonnull Vector3f currentRotation, @Nonnull Vector3i blockPosition) {
        if (this.warp != null && !this.warp.isEmpty()) {
            Warp targetWarp = TeleportPlugin.get().getWarps().get(this.warp.toLowerCase());
            return targetWarp != null ? targetWarp.toTeleport() : null;
        }
        if (this.transform != null) {
            World world;
            if (this.worldUuid != null && (world = Universe.get().getWorld(this.worldUuid)) != null) {
                if (this.relativeMask != 0) {
                    Transform teleportTransform = this.transform.clone();
                    Transform.applyMaskedRelativeTransform(teleportTransform, this.relativeMask, currentPosition, currentRotation, blockPosition);
                    return Teleport.createForPlayer(world, teleportTransform);
                }
                return Teleport.createForPlayer(world, this.transform);
            }
            if (this.relativeMask != 0) {
                Transform teleportTransform = this.transform.clone();
                Transform.applyMaskedRelativeTransform(teleportTransform, this.relativeMask, currentPosition, currentRotation, blockPosition);
                return Teleport.createForPlayer(teleportTransform);
            }
            return Teleport.createForPlayer(this.transform);
        }
        return null;
    }
}

