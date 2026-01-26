/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.lookup.MapProvidedMapCodec;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgGroup;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolState;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BrushData;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderToolData;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArgException;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class BuilderTool
implements JsonAssetWithMap<String, DefaultAssetMap<String, BuilderTool>>,
NetworkSerializable<BuilderToolState> {
    public static final String TOOL_DATA_KEY = "ToolData";
    public static final KeyedCodec<BrushData.Values> BRUSH_DATA_KEY_CODEC = new KeyedCodec<BrushData.Values>("BrushData", BrushData.Values.CODEC);
    public static final BuilderTool DEFAULT = new BuilderTool();
    public static final AssetBuilderCodec<String, BuilderTool> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(BuilderTool.class, BuilderTool::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).addField(new KeyedCodec<String>("Id", Codec.STRING), (builderTool, o) -> {
        builderTool.id = o;
    }, builderTool -> builderTool.id)).addField(new KeyedCodec<Boolean>("IsBrush", Codec.BOOLEAN), (builderTool, o) -> {
        builderTool.isBrush = o;
    }, builderTool -> builderTool.isBrush)).addField(new KeyedCodec<String>("BrushConfigurationCommand", Codec.STRING), (builderTool, o) -> {
        builderTool.brushConfigurationCommand = o;
    }, builderTool -> builderTool.brushConfigurationCommand)).addField(new KeyedCodec("Args", new MapCodec<ToolArg, HashMap>(ToolArg.CODEC, HashMap::new)), (builderTool, s) -> {
        builderTool.args = s;
    }, builderTool -> builderTool.args)).addField(new KeyedCodec<BrushData>("BrushData", BrushData.CODEC), (builderTool, o) -> {
        builderTool.brushData = o;
    }, builderTool -> builderTool.brushData)).afterDecode(builderTool -> {
        if (!builderTool.args.isEmpty()) {
            builderTool.argsCodec = new MapProvidedMapCodec(builderTool.args, ToolArg::getCodec, HashMap::new);
        }
    })).build();
    private static DefaultAssetMap<String, BuilderTool> ASSET_MAP;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected boolean isBrush;
    protected String brushConfigurationCommand;
    protected BrushData brushData = BrushData.DEFAULT;
    protected Map<String, ToolArg> args = Collections.emptyMap();
    protected Map<String, Object> defaultToolArgs;
    private MapProvidedMapCodec<Object, ToolArg> argsCodec;
    private SoftReference<BuilderToolState> cachedPacket;

    public static DefaultAssetMap<String, BuilderTool> getAssetMap() {
        if (ASSET_MAP == null) {
            ASSET_MAP = (DefaultAssetMap)AssetRegistry.getAssetStore(BuilderTool.class).getAssetMap();
        }
        return ASSET_MAP;
    }

    @Nullable
    public static BuilderTool getActiveBuilderTool(@Nonnull Player player) {
        ItemStack activeItemStack = player.getInventory().getItemInHand();
        if (activeItemStack == null) {
            return null;
        }
        Item item = activeItemStack.getItem();
        BuilderToolData builderToolData = item.getBuilderToolData();
        if (builderToolData == null) {
            return null;
        }
        return builderToolData.getTools()[0];
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getBrushConfigurationCommand() {
        return this.brushConfigurationCommand;
    }

    public boolean isBrush() {
        return this.isBrush;
    }

    public BrushData getBrushData() {
        return this.brushData;
    }

    public Map<String, ToolArg> getArgs() {
        return this.args;
    }

    public MapProvidedMapCodec<Object, ToolArg> getArgsCodec() {
        return this.argsCodec;
    }

    @Nonnull
    private Map<String, Object> getDefaultToolArgs(@Nonnull ItemStack itemStack) {
        BuilderTool builderToolAsset = itemStack.getItem().getBuilderToolData().getTools()[0];
        Object2ObjectOpenHashMap<String, Object> map = new Object2ObjectOpenHashMap<String, Object>(builderToolAsset.args.size());
        for (Map.Entry<String, ToolArg> entry : builderToolAsset.args.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }

    @Nonnull
    private BrushData.Values getDefaultBrushArgs(@Nonnull ItemStack itemStack) {
        BuilderTool builderToolAsset = itemStack.getItem().getBuilderToolData().getTools()[0];
        return new BrushData.Values(builderToolAsset.brushData);
    }

    @Nonnull
    public ArgData getItemArgData(@Nonnull ItemStack itemStack) {
        Map<String, Object> toolArgs = null;
        if (!this.args.isEmpty()) {
            Map<String, Object> toolData = (Map<String, Object>)itemStack.getFromMetadataOrNull(TOOL_DATA_KEY, this.argsCodec);
            toolArgs = toolData == null ? this.getDefaultToolArgs(itemStack) : toolData;
        }
        BrushData.Values brushArgs = null;
        if (this.isBrush) {
            BrushData.Values brushData = itemStack.getFromMetadataOrNull(BRUSH_DATA_KEY_CODEC);
            brushArgs = brushData == null ? this.getDefaultBrushArgs(itemStack) : brushData;
        }
        return new ArgData(toolArgs, brushArgs);
    }

    @Nonnull
    public ItemStack createItemStack(@Nonnull String itemId, int quantity, @Nonnull ArgData argData) {
        BsonDocument meta = new BsonDocument();
        if (argData.tool() != null) {
            meta.put(TOOL_DATA_KEY, this.argsCodec.encode(argData.tool()));
        }
        if (this.isBrush) {
            BRUSH_DATA_KEY_CODEC.put(meta, argData.brush);
        }
        return new ItemStack(itemId, quantity, meta);
    }

    @Nonnull
    public ItemStack updateArgMetadata(@Nonnull ItemStack itemStack, BuilderToolArgGroup group, @Nonnull String id, @Nullable String value) throws ToolArgException {
        ArgData argData = this.getItemArgData(itemStack);
        if (group == BuilderToolArgGroup.Brush) {
            this.brushData.updateArgValue(argData.brush, id, value);
        } else {
            ToolArg arg = this.args.get(id);
            if (arg == null) {
                throw new ToolArgException(Message.translation("server.builderTools.toolUnknownArg").param("arg", id));
            }
            if (value == null) {
                if (arg.isRequired()) {
                    throw new ToolArgException(Message.translation("server.builderTools.toolArgMissing").param("arg", id));
                }
                argData = ArgData.removeToolArg(argData, id);
            } else {
                Object newValue = arg.fromString(value);
                argData = ArgData.setToolArg(argData, id, newValue);
            }
        }
        return this.createItemStack(itemStack.getItemId(), itemStack.getQuantity(), argData);
    }

    @Override
    @Nonnull
    public BuilderToolState toPacket() {
        BuilderToolState cached;
        BuilderToolState builderToolState = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        BuilderToolState packet = new BuilderToolState();
        packet.id = this.id;
        packet.isBrush = this.isBrush;
        if (this.brushData != null) {
            packet.brushData = this.brushData.toPacket();
        }
        Object2ObjectOpenHashMap<String, BuilderToolArg> map = new Object2ObjectOpenHashMap<String, BuilderToolArg>(this.args.size());
        for (Map.Entry<String, ToolArg> entry : this.args.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toPacket());
        }
        packet.args = map;
        this.cachedPacket = new SoftReference<BuilderToolState>(packet);
        return packet;
    }

    @Nonnull
    public String toString() {
        return "BuilderTool{id='" + this.id + "', isBrush=" + this.isBrush + ", brushData=" + String.valueOf(this.brushData) + ", args=" + String.valueOf(this.args) + "}";
    }

    public record ArgData(Map<String, Object> tool, BrushData.Values brush) {
        @Nonnull
        public static ArgData setToolArg(@Nonnull ArgData argData, String argId, Object value) {
            Map<String, Object> tool = argData.tool();
            if (tool == null) {
                return argData;
            }
            Object2ObjectOpenHashMap<String, Object> newToolArgs = new Object2ObjectOpenHashMap<String, Object>(tool);
            newToolArgs.put(argId, value);
            return new ArgData(newToolArgs, argData.brush());
        }

        @Nonnull
        public static ArgData removeToolArg(@Nonnull ArgData argData, String argId) {
            Map<String, Object> tool = argData.tool();
            if (tool == null) {
                return argData;
            }
            Object2ObjectOpenHashMap<String, Object> newToolArgs = new Object2ObjectOpenHashMap<String, Object>(tool);
            newToolArgs.remove(argId);
            return new ArgData(newToolArgs, argData.brush());
        }

        @Override
        @Nonnull
        public String toString() {
            return "ArgData{tool=" + String.valueOf(this.tool) + ", brush=" + String.valueOf(this.brush) + "}";
        }
    }
}

