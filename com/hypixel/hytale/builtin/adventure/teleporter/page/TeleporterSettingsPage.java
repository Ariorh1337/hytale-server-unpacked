/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.page;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.adventure.teleporter.system.CreateWarpWhenTeleporterPlacedSystem;
import com.hypixel.hytale.builtin.adventure.teleporter.util.CannedWarpNames;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeleporterSettingsPage
extends InteractiveCustomUIPage<PageEventData> {
    @Nonnull
    private final Ref<ChunkStore> blockRef;
    private final Mode mode;
    @Nullable
    private final String activeState;

    public TeleporterSettingsPage(@Nonnull PlayerRef playerRef, @Nonnull Ref<ChunkStore> blockRef, Mode mode, @Nullable String activeState) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageEventData.CODEC);
        this.blockRef = blockRef;
        this.mode = mode;
        this.activeState = activeState;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        String language = this.playerRef.getLanguage();
        Teleporter teleporter = this.blockRef.getStore().getComponent(this.blockRef, Teleporter.getComponentType());
        commandBuilder.append("Pages/Teleporter.ui");
        if (teleporter == null) {
            commandBuilder.set("#ErrorScreen.Visible", true);
            commandBuilder.set("#FullSettings.Visible", false);
            commandBuilder.set("#WarpSettings.Visible", false);
            commandBuilder.set("#Buttons.Visible", false);
            return;
        }
        commandBuilder.set("#ErrorScreen.Visible", false);
        commandBuilder.set("#FullSettings.Visible", this.mode == Mode.FULL);
        switch (this.mode.ordinal()) {
            case 0: {
                byte relativeMask = teleporter.getRelativeMask();
                commandBuilder.set("#BlockRelative #CheckBox.Value", (relativeMask & 0x40) != 0);
                Transform transform = teleporter.getTransform();
                if (transform != null) {
                    commandBuilder.set("#X #Input.Value", transform.getPosition().getX());
                    commandBuilder.set("#Y #Input.Value", transform.getPosition().getY());
                    commandBuilder.set("#Z #Input.Value", transform.getPosition().getZ());
                }
                commandBuilder.set("#X #CheckBox.Value", (relativeMask & 1) != 0);
                commandBuilder.set("#Y #CheckBox.Value", (relativeMask & 2) != 0);
                commandBuilder.set("#Z #CheckBox.Value", (relativeMask & 4) != 0);
                if (transform != null) {
                    commandBuilder.set("#Yaw #Input.Value", transform.getRotation().getYaw());
                    commandBuilder.set("#Pitch #Input.Value", transform.getRotation().getPitch());
                    commandBuilder.set("#Roll #Input.Value", transform.getRotation().getRoll());
                }
                commandBuilder.set("#Yaw #CheckBox.Value", (relativeMask & 8) != 0);
                commandBuilder.set("#Pitch #CheckBox.Value", (relativeMask & 0x10) != 0);
                commandBuilder.set("#Roll #CheckBox.Value", (relativeMask & 0x20) != 0);
                ObjectArrayList<DropdownEntryInfo> worlds = new ObjectArrayList<DropdownEntryInfo>();
                worlds.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.teleporter.noWorld"), ""));
                for (World world : Universe.get().getWorlds().values()) {
                    worlds.add(new DropdownEntryInfo(LocalizableString.fromString(world.getName()), world.getWorldConfig().getUuid().toString()));
                }
                commandBuilder.set("#WorldDropdown.Entries", worlds);
                UUID worldUuid = teleporter.getWorldUuid();
                commandBuilder.set("#WorldDropdown.Value", worldUuid != null ? worldUuid.toString() : "");
                ObjectArrayList<DropdownEntryInfo> warps = new ObjectArrayList<DropdownEntryInfo>();
                warps.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.teleporter.noWarp"), ""));
                for (Warp warp : TeleportPlugin.get().getWarps().values()) {
                    if (warp.getId().equalsIgnoreCase(teleporter.getOwnedWarp())) continue;
                    warps.add(new DropdownEntryInfo(LocalizableString.fromString(warp.getId()), warp.getId().toLowerCase()));
                }
                commandBuilder.set("#WarpDropdown.Entries", warps);
                commandBuilder.set("#WarpDropdown.Value", teleporter.getWarp() != null ? teleporter.getWarp() : "");
                commandBuilder.set("#NewWarp.Value", teleporter.getOwnedWarp() != null ? teleporter.getOwnedWarp() : "");
                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", new EventData().append("@BlockRelative", "#BlockRelative #CheckBox.Value").append("@X", "#X #Input.Value").append("@Y", "#Y #Input.Value").append("@Z", "#Z #Input.Value").append("@XIsRelative", "#X #CheckBox.Value").append("@YIsRelative", "#Y #CheckBox.Value").append("@ZIsRelative", "#Z #CheckBox.Value").append("@Yaw", "#Yaw #Input.Value").append("@Pitch", "#Pitch #Input.Value").append("@Roll", "#Roll #Input.Value").append("@YawIsRelative", "#Yaw #CheckBox.Value").append("@PitchIsRelative", "#Pitch #CheckBox.Value").append("@RollIsRelative", "#Roll #CheckBox.Value").append("@World", "#WorldDropdown.Value").append("@Warp", "#WarpDropdown.Value").append("@NewWarp", "#NewWarp.Value"));
                break;
            }
            case 1: {
                String cannedName;
                ObjectArrayList warps = new ObjectArrayList();
                warps.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.teleporter.noWarp"), ""));
                for (Warp warp : TeleportPlugin.get().getWarps().values()) {
                    if (!warp.getWorld().equals(store.getExternalData().getWorld().getName()) || warp.getId().equalsIgnoreCase(teleporter.getOwnedWarp())) continue;
                    warps.add(new DropdownEntryInfo(LocalizableString.fromString(warp.getId()), warp.getId().toLowerCase()));
                }
                commandBuilder.set("#WarpDropdown.Entries", warps);
                commandBuilder.set("#WarpDropdown.Value", teleporter.getWarp() != null ? teleporter.getWarp() : "");
                Message placeholder = teleporter.hasOwnedWarp() && !teleporter.isCustomName() ? Message.translation(teleporter.getOwnedWarp()) : ((cannedName = CannedWarpNames.generateCannedWarpNameKey(this.blockRef, language)) == null ? Message.translation("server.customUI.teleporter.warpName") : Message.translation(cannedName));
                commandBuilder.set("#NewWarp.PlaceholderText", placeholder);
                String value = teleporter.isCustomName() && teleporter.getOwnedWarp() != null ? teleporter.getOwnedWarp() : "";
                commandBuilder.set("#NewWarp.Value", value);
                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", new EventData().append("@Warp", "#WarpDropdown.Value").append("@NewWarp", "#NewWarp.Value"));
            }
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        BlockType variantBlockType;
        BlockType blockType;
        String currentState;
        boolean alreadyExists;
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        String language = this.playerRef.getLanguage();
        BlockModule.BlockStateInfo blockStateInfo = this.blockRef.getStore().getComponent(this.blockRef, BlockModule.BlockStateInfo.getComponentType());
        if (blockStateInfo == null) {
            playerComponent.getPageManager().setPage(ref, store, Page.None);
            return;
        }
        Ref<ChunkStore> chunkRef = blockStateInfo.getChunkRef();
        if (!chunkRef.isValid()) {
            playerComponent.getPageManager().setPage(ref, store, Page.None);
            return;
        }
        WorldChunk worldChunkComponent = chunkRef.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
        assert (worldChunkComponent != null);
        int index = blockStateInfo.getIndex();
        int targetX = ChunkUtil.xFromBlockInColumn(index);
        int targetY = ChunkUtil.yFromBlockInColumn(index);
        int targetZ = ChunkUtil.zFromBlockInColumn(index);
        Vector3i targetBlockPosition = new Vector3i(targetX, targetY, targetZ);
        Teleporter teleporter = this.blockRef.getStore().getComponent(this.blockRef, Teleporter.getComponentType());
        String oldOwnedWarp = teleporter.getOwnedWarp();
        boolean customName = true;
        if (data.ownedWarp == null || data.ownedWarp.isEmpty()) {
            data.ownedWarp = CannedWarpNames.generateCannedWarpName(this.blockRef, language);
            customName = false;
            if (data.ownedWarp == null) {
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#NewWarp.PlaceholderText", Message.translation("server.customUI.teleporter.warpNameRightHereHint"));
                commandBuilder.set("#ErrorLabel.Text", Message.translation("server.customUI.teleporter.errorMissingWarpName"));
                commandBuilder.set("#ErrorLabel.Visible", true);
                this.sendUpdate(commandBuilder);
                return;
            }
        }
        if (!data.ownedWarp.equalsIgnoreCase(oldOwnedWarp) && (alreadyExists = TeleportPlugin.get().getWarps().containsKey(data.ownedWarp.toLowerCase()))) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#ErrorLabel.Text", Message.translation("server.customUI.teleporter.errorWarpAlreadyExists"));
            commandBuilder.set("#ErrorLabel.Visible", true);
            this.sendUpdate(commandBuilder);
            return;
        }
        if (oldOwnedWarp != null && !oldOwnedWarp.isEmpty()) {
            TeleportPlugin.get().getWarps().remove(oldOwnedWarp.toLowerCase());
        }
        playerComponent.getPageManager().setPage(ref, store, Page.None);
        CreateWarpWhenTeleporterPlacedSystem.createWarp(worldChunkComponent, blockStateInfo, data.ownedWarp);
        teleporter.setOwnedWarp(data.ownedWarp);
        teleporter.setIsCustomName(customName);
        switch (this.mode.ordinal()) {
            case 0: {
                teleporter.setWorldUuid(data.world == null || data.world.isEmpty() ? null : UUID.fromString(data.world));
                Transform transform = new Transform();
                transform.getPosition().setX(data.x);
                transform.getPosition().setY(data.y);
                transform.getPosition().setZ(data.z);
                transform.getRotation().setYaw(data.yaw);
                transform.getRotation().setPitch(data.pitch);
                transform.getRotation().setRoll(data.roll);
                teleporter.setTransform(transform);
                teleporter.setRelativeMask((byte)((data.xIsRelative ? 1 : 0) | (data.yIsRelative ? 2 : 0) | (data.zIsRelative ? 4 : 0) | (data.yawIsRelative ? 8 : 0) | (data.pitchIsRelative ? 16 : 0) | (data.rollIsRelative ? 32 : 0) | (data.isBlockRelative ? 64 : 0)));
                teleporter.setWarp(data.warp == null || data.warp.isEmpty() ? null : data.warp);
                break;
            }
            case 1: {
                teleporter.setWorldUuid(null);
                teleporter.setTransform(null);
                teleporter.setWarp(data.warp == null || data.warp.isEmpty() ? null : data.warp);
            }
        }
        String newState = "default";
        if (teleporter.isValid()) {
            String string = newState = this.activeState != null ? this.activeState : "default";
        }
        if (!((currentState = (blockType = worldChunkComponent.getBlockType(targetX, targetY, targetZ)).getStateForBlock(blockType)) != null && currentState.equals(newState) || (variantBlockType = blockType.getBlockForState(newState)) == null)) {
            worldChunkComponent.setBlockInteractionState(targetX, targetY, targetZ, variantBlockType, newState, true);
        }
        blockStateInfo.markNeedsSaving();
    }

    public static class PageEventData {
        public static final String KEY_BLOCK_RELATIVE = "@BlockRelative";
        public static final String KEY_X = "@X";
        public static final String KEY_Y = "@Y";
        public static final String KEY_Z = "@Z";
        public static final String KEY_X_IS_RELATIVE = "@XIsRelative";
        public static final String KEY_Y_IS_RELATIVE = "@YIsRelative";
        public static final String KEY_Z_IS_RELATIVE = "@ZIsRelative";
        public static final String KEY_YAW = "@Yaw";
        public static final String KEY_PITCH = "@Pitch";
        public static final String KEY_ROLL = "@Roll";
        public static final String KEY_YAW_IS_RELATIVE = "@YawIsRelative";
        public static final String KEY_PITCH_IS_RELATIVE = "@PitchIsRelative";
        public static final String KEY_ROLL_IS_RELATIVE = "@RollIsRelative";
        public static final String KEY_WORLD = "@World";
        public static final String KEY_WARP = "@Warp";
        public static final String KEY_NEW_WARP = "@NewWarp";
        public static final BuilderCodec<PageEventData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageEventData.class, PageEventData::new).append(new KeyedCodec<Boolean>("@BlockRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.isBlockRelative = o;
        }, pageEventData -> pageEventData.isBlockRelative).add()).append(new KeyedCodec<Double>("@X", Codec.DOUBLE), (pageEventData, o) -> {
            pageEventData.x = o;
        }, pageEventData -> pageEventData.x).add()).append(new KeyedCodec<Double>("@Y", Codec.DOUBLE), (pageEventData, o) -> {
            pageEventData.y = o;
        }, pageEventData -> pageEventData.y).add()).append(new KeyedCodec<Double>("@Z", Codec.DOUBLE), (pageEventData, o) -> {
            pageEventData.z = o;
        }, pageEventData -> pageEventData.z).add()).append(new KeyedCodec<Boolean>("@XIsRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.xIsRelative = o;
        }, pageEventData -> pageEventData.xIsRelative).add()).append(new KeyedCodec<Boolean>("@YIsRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.yIsRelative = o;
        }, pageEventData -> pageEventData.yIsRelative).add()).append(new KeyedCodec<Boolean>("@ZIsRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.zIsRelative = o;
        }, pageEventData -> pageEventData.zIsRelative).add()).append(new KeyedCodec<Float>("@Yaw", Codec.FLOAT), (pageEventData, o) -> {
            pageEventData.yaw = o.floatValue();
        }, pageEventData -> Float.valueOf(pageEventData.yaw)).add()).append(new KeyedCodec<Float>("@Pitch", Codec.FLOAT), (pageEventData, o) -> {
            pageEventData.pitch = o.floatValue();
        }, pageEventData -> Float.valueOf(pageEventData.pitch)).add()).append(new KeyedCodec<Float>("@Roll", Codec.FLOAT), (pageEventData, o) -> {
            pageEventData.roll = o.floatValue();
        }, pageEventData -> Float.valueOf(pageEventData.roll)).add()).append(new KeyedCodec<Boolean>("@YawIsRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.yawIsRelative = o;
        }, pageEventData -> pageEventData.yawIsRelative).add()).append(new KeyedCodec<Boolean>("@PitchIsRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.pitchIsRelative = o;
        }, pageEventData -> pageEventData.pitchIsRelative).add()).append(new KeyedCodec<Boolean>("@RollIsRelative", Codec.BOOLEAN), (pageEventData, o) -> {
            pageEventData.rollIsRelative = o;
        }, pageEventData -> pageEventData.pitchIsRelative).add()).append(new KeyedCodec<String>("@World", Codec.STRING), (pageEventData, o) -> {
            pageEventData.world = o;
        }, pageEventData -> pageEventData.world).add()).append(new KeyedCodec<String>("@Warp", Codec.STRING), (pageEventData, o) -> {
            pageEventData.warp = o;
        }, pageEventData -> pageEventData.warp).add()).append(new KeyedCodec<String>("@NewWarp", Codec.STRING), (pageEventData, o) -> {
            pageEventData.ownedWarp = o;
        }, pageEventData -> pageEventData.ownedWarp).add()).build();
        public boolean isBlockRelative;
        public double x;
        public double y;
        public double z;
        public boolean xIsRelative;
        public boolean yIsRelative;
        public boolean zIsRelative;
        public float yaw;
        public float pitch;
        public float roll;
        public boolean yawIsRelative;
        public boolean pitchIsRelative;
        public boolean rollIsRelative;
        public String world;
        public String warp;
        @Nullable
        public String ownedWarp;
    }

    public static enum Mode {
        FULL,
        WARP;

        public static final Codec<Mode> CODEC;

        static {
            CODEC = new EnumCodec<Mode>(Mode.class);
        }
    }
}

