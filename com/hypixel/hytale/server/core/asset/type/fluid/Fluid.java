/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.fluid;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorSectionStart;
import com.hypixel.hytale.codec.schema.metadata.ui.UIPropertyTitle;
import com.hypixel.hytale.codec.schema.metadata.ui.UIRebuildCaches;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.BlockTextures;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Opacity;
import com.hypixel.hytale.protocol.ShaderType;
import com.hypixel.hytale.server.core.asset.type.blockparticle.config.BlockParticleSet;
import com.hypixel.hytale.server.core.asset.type.blocksound.config.BlockSoundSet;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockTypeTextures;
import com.hypixel.hytale.server.core.asset.type.fluid.DefaultFluidTicker;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.asset.type.fluidfx.config.FluidFX;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionTypeUtils;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ISectionPalette;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Fluid
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, Fluid>>,
NetworkSerializable<com.hypixel.hytale.protocol.Fluid> {
    public static final AssetBuilderCodec<String, Fluid> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(Fluid.class, Fluid::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).appendInherited(new KeyedCodec<Integer>("MaxFluidLevel", Codec.INTEGER), (asset, v) -> {
        asset.maxFluidLevel = v;
    }, asset -> asset.maxFluidLevel, (asset, parent) -> {
        asset.maxFluidLevel = parent.maxFluidLevel;
    }).addValidator(Validators.range(0, 15)).add()).appendInherited(new KeyedCodec<T[]>("Textures", new ArrayCodec<BlockTypeTextures>(BlockTypeTextures.CODEC, BlockTypeTextures[]::new)), (fluid, o) -> {
        fluid.textures = o;
    }, fluid -> fluid.textures, (fluid, parent) -> {
        fluid.textures = parent.textures;
    }).metadata(new UIPropertyTitle("Block Textures")).metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODELS, UIRebuildCaches.ClientCache.BLOCK_TEXTURES)).add()).appendInherited(new KeyedCodec<T[]>("Effect", new ArrayCodec<ShaderType>(new EnumCodec<ShaderType>(ShaderType.class), ShaderType[]::new)), (fluid, o) -> {
        fluid.effect = o;
    }, fluid -> fluid.effect, (fluid, parent) -> {
        fluid.effect = parent.effect;
    }).metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODELS)).add()).appendInherited(new KeyedCodec<Opacity>("Opacity", new EnumCodec<Opacity>(Opacity.class)), (fluid, o) -> {
        fluid.opacity = o;
    }, fluid -> fluid.opacity, (fluid, parent) -> {
        fluid.opacity = parent.opacity;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Boolean>("RequiresAlphaBlending", Codec.BOOLEAN), (fluid, o) -> {
        fluid.requiresAlphaBlending = o;
    }, fluid -> fluid.requiresAlphaBlending, (fluid, parent) -> {
        fluid.requiresAlphaBlending = parent.requiresAlphaBlending;
    }).metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODELS)).add()).appendInherited(new KeyedCodec<String>("FluidFXId", Codec.STRING), (fluid, o) -> {
        fluid.fluidFXId = o;
    }, fluid -> fluid.fluidFXId, (fluid, parent) -> {
        fluid.fluidFXId = parent.fluidFXId;
    }).addValidator(FluidFX.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec<FluidTicker>("Ticker", FluidTicker.CODEC), (fluid, o) -> {
        fluid.ticker = o;
    }, fluid -> fluid.ticker, (fluid, parent) -> {
        fluid.ticker = parent.ticker;
    }).add()).appendInherited(new KeyedCodec<ColorLight>("Light", ProtocolCodecs.COLOR_LIGHT), (fluid, o) -> {
        fluid.light = o;
    }, fluid -> fluid.light, (fluid, parent) -> {
        fluid.light = parent.light;
    }).add()).appendInherited(new KeyedCodec<Integer>("DamageToEntities", Codec.INTEGER), (fluid, s) -> {
        fluid.damageToEntities = s;
    }, fluid -> fluid.damageToEntities, (fluid, parent) -> {
        fluid.damageToEntities = parent.damageToEntities;
    }).add()).appendInherited(new KeyedCodec<String>("BlockParticleSetId", Codec.STRING), (fluid, s) -> {
        fluid.blockParticleSetId = s;
    }, fluid -> fluid.blockParticleSetId, (fluid, parent) -> {
        fluid.blockParticleSetId = parent.blockParticleSetId;
    }).documentation("The block particle set defined here defines which particles should be spawned when an entity interacts with this block (like when stepping on it for example").addValidator(BlockParticleSet.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec<Color>("ParticleColor", ProtocolCodecs.COLOR), (fluid, s) -> {
        fluid.particleColor = s;
    }, fluid -> fluid.particleColor, (fluid, parent) -> {
        fluid.particleColor = parent.particleColor;
    }).add()).appendInherited(new KeyedCodec<String>("BlockSoundSetId", Codec.STRING), (fluid, o) -> {
        fluid.blockSoundSetId = o;
    }, fluid -> fluid.blockSoundSetId, (fluid, parent) -> {
        fluid.blockSoundSetId = parent.blockSoundSetId;
    }).documentation("Sets the **BlockSoundSet** that will be used for this block for various events e.g. placement, breaking").addValidator(BlockSoundSet.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec("Interactions", new EnumMapCodec(InteractionType.class, RootInteraction.CHILD_ASSET_CODEC)), (item, v) -> {
        item.interactions = v;
    }, item -> item.interactions, (item, parent) -> {
        item.interactions = parent.interactions;
    }).addValidator(RootInteraction.VALIDATOR_CACHE.getMapValueValidator()).metadata(new UIEditorSectionStart("Interactions")).add()).afterDecode(Fluid::processConfig)).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(Fluid::getAssetStore));
    public static final String UNKNOWN_TEXTURE = "BlockTextures/Unknown.png";
    public static final BlockTextures[] UNKNOWN_BLOCK_TEXTURES = new BlockTextures[]{new BlockTextures("BlockTextures/Unknown.png", "BlockTextures/Unknown.png", "BlockTextures/Unknown.png", "BlockTextures/Unknown.png", "BlockTextures/Unknown.png", "BlockTextures/Unknown.png", 1.0f)};
    public static final ShaderType[] DEFAULT_SHADER_EFFECTS = new ShaderType[]{ShaderType.None};
    public static final ISectionPalette.KeySerializer KEY_SERIALIZER = (buf, id) -> {
        String key = Fluid.getAssetMap().getAssetOrDefault(id, UNKNOWN).getId();
        ByteBufUtil.writeUTF(buf, key);
    };
    public static final ToIntFunction<ByteBuf> KEY_DESERIALIZER = byteBuf -> {
        String fluid = ByteBufUtil.readUTF(byteBuf);
        return Fluid.getFluidIdOrUnknown(fluid, "Failed to find fluid '%s' in chunk section!", fluid);
    };
    public static final int EMPTY_ID = 0;
    public static final String EMPTY_KEY = "Empty";
    public static final Fluid EMPTY = new Fluid("Empty"){
        {
            this.processConfig();
        }
    };
    public static final int UNKNOWN_ID = 1;
    public static final Fluid UNKNOWN = new Fluid("Unknown"){
        {
            this.unknown = true;
            this.processConfig();
        }
    };
    private static AssetStore<String, Fluid, IndexedLookupTableAssetMap<String, Fluid>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected boolean unknown;
    private int maxFluidLevel = 8;
    private BlockTypeTextures[] textures;
    private ShaderType[] effect;
    @Nonnull
    private Opacity opacity = Opacity.Solid;
    private boolean requiresAlphaBlending = true;
    private String fluidFXId = "Empty";
    protected transient int fluidFXIndex = 0;
    private FluidTicker ticker = DefaultFluidTicker.INSTANCE;
    protected int damageToEntities;
    protected ColorLight light;
    protected Color particleColor;
    protected String blockSoundSetId = "EMPTY";
    protected transient int blockSoundSetIndex = 0;
    public String blockParticleSetId;
    protected Map<InteractionType, String> interactions = Collections.emptyMap();
    protected transient boolean isTrigger;

    public static AssetStore<String, Fluid, IndexedLookupTableAssetMap<String, Fluid>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Fluid.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, Fluid> getAssetMap() {
        return Fluid.getAssetStore().getAssetMap();
    }

    public Fluid() {
    }

    public Fluid(String id) {
        this.id = id;
    }

    public Fluid(@Nonnull Fluid other) {
        this.data = other.data;
        this.id = other.id;
        this.unknown = other.unknown;
        this.maxFluidLevel = other.maxFluidLevel;
        this.textures = other.textures;
        this.effect = other.effect;
        this.opacity = other.opacity;
        this.requiresAlphaBlending = other.requiresAlphaBlending;
        this.fluidFXId = other.fluidFXId;
        this.damageToEntities = other.damageToEntities;
        this.light = other.light;
        this.particleColor = other.particleColor;
        this.blockSoundSetId = other.blockSoundSetId;
        this.interactions = other.interactions;
        this.isTrigger = other.isTrigger;
        this.processConfig();
    }

    public AssetExtraInfo.Data getData() {
        return this.data;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public boolean isUnknown() {
        return this.unknown;
    }

    public int getMaxFluidLevel() {
        return this.maxFluidLevel;
    }

    public boolean hasEffect(ShaderType shader) {
        if (this.effect == null) {
            return false;
        }
        for (ShaderType e : this.effect) {
            if (e != shader) continue;
            return true;
        }
        return false;
    }

    public FluidTicker getTicker() {
        return this.ticker;
    }

    public int getDamageToEntities() {
        return this.damageToEntities;
    }

    public String getFluidFXId() {
        return this.fluidFXId;
    }

    public int getFluidFXIndex() {
        return this.fluidFXIndex;
    }

    public ColorLight getLight() {
        return this.light;
    }

    public Color getParticleColor() {
        return this.particleColor;
    }

    public boolean isTrigger() {
        return this.isTrigger;
    }

    public Map<InteractionType, String> getInteractions() {
        return this.interactions;
    }

    protected void processConfig() {
        this.fluidFXIndex = this.fluidFXId.equals(EMPTY_KEY) ? 0 : FluidFX.getAssetMap().getIndex(this.fluidFXId);
        this.blockSoundSetIndex = this.blockSoundSetId.equals("EMPTY") ? 0 : BlockSoundSet.getAssetMap().getIndex(this.blockSoundSetId);
        for (InteractionType type : this.interactions.keySet()) {
            if (!InteractionTypeUtils.isCollisionType(type)) continue;
            this.isTrigger = true;
            break;
        }
    }

    @Nonnull
    public static Fluid getUnknownFor(String key) {
        return UNKNOWN.clone(key);
    }

    @Nonnull
    public Fluid clone(String newKey) {
        if (this.id != null && this.id.equals(newKey)) {
            return this;
        }
        Fluid fluid = new Fluid(this);
        fluid.id = newKey;
        return fluid;
    }

    public static int getFluidIdOrUnknown(String key, String message, Object ... params) {
        return Fluid.getFluidIdOrUnknown(Fluid.getAssetMap(), key, message, params);
    }

    public static int getFluidIdOrUnknown(@Nonnull IndexedLookupTableAssetMap<String, Fluid> assetMap, String key, String message, Object ... params) {
        int fluidId = assetMap.getIndex(key);
        if (fluidId == Integer.MIN_VALUE) {
            HytaleLogger.getLogger().at(Level.WARNING).logVarargs(message, params);
            AssetRegistry.getAssetStore(Fluid.class).loadAssets("Hytale:Hytale", Collections.singletonList(Fluid.getUnknownFor(key)));
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            fluidId = index;
        }
        return fluidId;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.Fluid toPacket() {
        IntSet tags;
        com.hypixel.hytale.protocol.Fluid packet = new com.hypixel.hytale.protocol.Fluid();
        packet.id = this.id;
        packet.maxFluidLevel = this.maxFluidLevel;
        packet.fluidFXIndex = this.fluidFXIndex;
        packet.opacity = this.opacity;
        packet.light = this.light;
        packet.shaderEffect = this.effect != null && this.effect.length > 0 ? this.effect : DEFAULT_SHADER_EFFECTS;
        if (this.textures != null && this.textures.length > 0) {
            int totalWeight = 0;
            for (BlockTypeTextures texture : this.textures) {
                totalWeight = (int)((float)totalWeight + texture.getWeight());
            }
            BlockTextures[] texturePackets = new BlockTextures[this.textures.length];
            for (int i = 0; i < this.textures.length; ++i) {
                texturePackets[i] = this.textures[i].toPacket(totalWeight);
            }
            packet.cubeTextures = texturePackets;
        } else {
            packet.cubeTextures = UNKNOWN_BLOCK_TEXTURES;
        }
        packet.requiresAlphaBlending = this.requiresAlphaBlending;
        packet.blockSoundSetIndex = this.blockSoundSetIndex;
        packet.blockParticleSetId = this.blockParticleSetId;
        packet.particleColor = this.particleColor;
        packet.fluidFXIndex = this.fluidFXIndex;
        if (this.data != null && (tags = this.data.getExpandedTagIndexes()) != null) {
            packet.tagIndexes = tags.toIntArray();
        }
        return packet;
    }

    @Nullable
    @Deprecated(forRemoval=true)
    public static String convertLegacyName(@Nonnull String fluidName, byte level) {
        return switch (fluidName) {
            case "Fluid_Water" -> {
                if (level == 0) {
                    yield "Water_Source";
                }
                yield "Water";
            }
            case "Fluid_Water_Test" -> "Water_Finite";
            case "Fluid_Lava" -> {
                if (level == 0) {
                    yield "Lava_Source";
                }
                yield "Lava";
            }
            case "Fluid_Tar" -> {
                if (level == 0) {
                    yield "Tar_Source";
                }
                yield "Tar";
            }
            case "Fluid_Slime" -> {
                if (level == 0) {
                    yield "Slime_Source";
                }
                yield "Slime";
            }
            case "Fluid_Poison" -> {
                if (level == 0) {
                    yield "Poison_Source";
                }
                yield "Poison";
            }
            default -> null;
        };
    }

    @Nullable
    @Deprecated(forRemoval=true)
    public static ConversionResult convertBlockToFluid(@Nonnull String blockTypeStr) {
        int fluidPos = ((String)blockTypeStr).indexOf("|Fluid=");
        if (fluidPos != -1) {
            byte fluidLevel;
            String fluidName;
            int fluidNameEnd = ((String)blockTypeStr).indexOf(124, fluidPos + 2);
            if (fluidNameEnd != -1) {
                fluidName = ((String)blockTypeStr).substring(fluidPos + "|Fluid=".length(), fluidNameEnd);
                blockTypeStr = ((String)blockTypeStr).substring(0, fluidPos) + ((String)blockTypeStr).substring(fluidNameEnd);
            } else {
                fluidName = ((String)blockTypeStr).substring(fluidPos + "|Fluid=".length());
                blockTypeStr = ((String)blockTypeStr).substring(0, fluidPos);
            }
            int fluidLevelStart = ((String)blockTypeStr).indexOf("|FluidLevel=");
            if (fluidLevelStart != -1) {
                String fluidLevelStr;
                int fluidLevelEnd = ((String)blockTypeStr).indexOf(124, fluidLevelStart + 2);
                if (fluidLevelEnd != -1) {
                    fluidLevelStr = ((String)blockTypeStr).substring(fluidLevelStart + "|FluidLevel=".length(), fluidLevelEnd);
                    blockTypeStr = ((String)blockTypeStr).substring(0, fluidLevelStart) + ((String)blockTypeStr).substring(fluidLevelEnd);
                } else {
                    fluidLevelStr = ((String)blockTypeStr).substring(fluidLevelStart + "|FluidLevel=".length());
                    blockTypeStr = ((String)blockTypeStr).substring(0, fluidLevelStart);
                }
                fluidLevel = Byte.parseByte(fluidLevelStr);
            } else {
                fluidLevel = 0;
            }
            fluidName = Fluid.convertLegacyName(fluidName, fluidLevel);
            int fluidId = Fluid.getFluidIdOrUnknown(fluidName, "Failed to find fluid '%s'", fluidName);
            fluidLevel = fluidLevel == 0 ? (byte)Fluid.getAssetMap().getAsset(fluidId).getMaxFluidLevel() : fluidLevel;
            return new ConversionResult((String)blockTypeStr, fluidId, fluidLevel);
        }
        if (((String)blockTypeStr).startsWith("Fluid_")) {
            byte fluidLevel;
            int fluidLevelStart = ((String)blockTypeStr).indexOf("|FluidLevel=");
            if (fluidLevelStart != -1) {
                String fluidLevelStr;
                int fluidLevelEnd = ((String)blockTypeStr).indexOf(124, fluidLevelStart + 2);
                if (fluidLevelEnd != -1) {
                    fluidLevelStr = ((String)blockTypeStr).substring(fluidLevelStart + "|FluidLevel=".length(), fluidLevelEnd);
                    blockTypeStr = ((String)blockTypeStr).substring(0, fluidLevelStart) + ((String)blockTypeStr).substring(fluidLevelEnd);
                } else {
                    fluidLevelStr = ((String)blockTypeStr).substring(fluidLevelStart + "|FluidLevel=".length());
                    blockTypeStr = ((String)blockTypeStr).substring(0, fluidLevelStart);
                }
                fluidLevel = Byte.parseByte(fluidLevelStr);
            } else {
                fluidLevel = 0;
            }
            String newFluidName = Fluid.convertLegacyName((String)blockTypeStr, fluidLevel);
            int fluidId = Fluid.getFluidIdOrUnknown(newFluidName, "Failed to find fluid '%s'", newFluidName);
            fluidLevel = fluidLevel == 0 ? (byte)Fluid.getAssetMap().getAsset(fluidId).getMaxFluidLevel() : fluidLevel;
            return new ConversionResult(null, fluidId, fluidLevel);
        }
        return null;
    }

    @Deprecated(forRemoval=true)
    public static class ConversionResult {
        public String blockTypeStr;
        public int fluidId;
        public byte fluidLevel;

        public ConversionResult(String blockTypeStr, int fluidId, byte fluidLevel) {
            this.blockTypeStr = blockTypeStr;
            this.fluidId = fluidId;
            this.fluidLevel = fluidLevel;
        }
    }
}

