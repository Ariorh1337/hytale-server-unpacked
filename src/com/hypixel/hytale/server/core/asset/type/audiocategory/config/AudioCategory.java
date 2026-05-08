package com.hypixel.hytale.server.core.asset.type.audiocategory.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.event.RemovedAssetsEvent;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.AudioUtil;
import com.hypixel.hytale.server.core.asset.type.audiostate.config.AudioState;
import com.hypixel.hytale.server.core.asset.type.audiostate.config.AudioStateResolver;
import com.hypixel.hytale.server.core.asset.type.audiostate.config.StateBindingConfig;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AudioCategory
   implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, AudioCategory>>,
   NetworkSerializable<com.hypixel.hytale.protocol.AudioCategory> {
   public static final int EMPTY_ID = 0;
   public static final String EMPTY = "EMPTY";
   public static final AudioCategory EMPTY_AUDIO_CATEGORY = new AudioCategory("EMPTY");
   private static final int MAX_PARENT_DEPTH = 128;
   public static final AssetBuilderCodec<String, AudioCategory> CODEC = AssetBuilderCodec.<String, AudioCategory>builder(
         AudioCategory.class, AudioCategory::new, Codec.STRING, (t, k) -> t.id = k, t -> t.id, (asset, data) -> asset.data = data, asset -> asset.data
      )
      .documentation(
         "An asset used to define an audio category. Can be used to adjust the volume of all sound events that reference a given category. Note: categories form a hierarchy via the generic JSON parent mechanism, acting as an audio bus. e.g. if the category's volume is 4dB and the parent is -2dB, the final volume will be 2dB."
      )
      .<Float>append(
         new KeyedCodec<>("Volume", Codec.FLOAT),
         (category, f) -> category.volume = AudioUtil.decibelsToLinearGain(f),
         category -> AudioUtil.linearGainToDecibels(category.volume)
      )
      .metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null)))
      .addValidator(Validators.range(-100.0F, 10.0F))
      .documentation("Volume adjustment for this audio category in decibels.")
      .add()
      .<StateBindingConfig[]>append(
         new KeyedCodec<>("StateBindings", StateBindingConfig.CODEC_ARRAY), (category, v) -> category.stateBindings = v, category -> category.stateBindings
      )
      .documentation(
         "Subscribe this AudioCategory to one or more AudioState axes. Per-state volume deltas drive the category's volume modifier and cascade to voices in descendant categories."
      )
      .add()
      .afterDecode(category -> AudioStateResolver.resolveBindings(category.stateBindings))
      .validator((category, results) -> {
         validateParentChain(category, results);
         AudioStateResolver.validateBindings(category.stateBindings, "AudioCategory '" + category.id + "'", results);
      })
      .build();
   public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(AudioCategory::getAssetStore));
   private static AssetStore<String, AudioCategory, IndexedLookupTableAssetMap<String, AudioCategory>> ASSET_STORE;
   protected AssetExtraInfo.Data data;
   protected String id;
   protected float volume = AudioUtil.decibelsToLinearGain(0.0F);
   @Nullable
   protected StateBindingConfig[] stateBindings;

   private static void validateParentChain(@Nonnull AudioCategory category, @Nonnull ValidationResults results) {
      if (category.data != null) {
         HashSet<String> visited = new HashSet<>();
         visited.add(category.id);
         AssetExtraInfo.Data current = category.data;
         IndexedLookupTableAssetMap<String, AudioCategory> assetMap = getAssetMap();

         for (int depth = 0; depth < 128; depth++) {
            String parentKey = getAssetStore().transformKey(current.getParentKey());
            if (parentKey == null) {
               return;
            }

            if (!visited.add(parentKey)) {
               results.fail("AudioCategory '" + category.id + "' parent chain contains a cycle through '" + parentKey + "'");
               return;
            }

            AudioCategory parent = assetMap.getAsset(parentKey);
            if (parent == null) {
               return;
            }

            current = parent.data;
            if (current == null) {
               return;
            }
         }

         results.fail("AudioCategory '" + category.id + "' parent chain exceeds max depth 128 - likely a cycle or pathological hierarchy");
      }
   }

   public static AssetStore<String, AudioCategory, IndexedLookupTableAssetMap<String, AudioCategory>> getAssetStore() {
      if (ASSET_STORE == null) {
         ASSET_STORE = AssetRegistry.getAssetStore(AudioCategory.class);
      }

      return ASSET_STORE;
   }

   public static IndexedLookupTableAssetMap<String, AudioCategory> getAssetMap() {
      return (IndexedLookupTableAssetMap<String, AudioCategory>)getAssetStore().getAssetMap();
   }

   public AudioCategory(String id) {
      this.id = id;
   }

   protected AudioCategory() {
   }

   public String getId() {
      return this.id;
   }

   public void refreshAudioStateResolution() {
      AudioStateResolver.resolveBindings(this.stateBindings);
   }

   public static void onAudioStateLoaded(@Nonnull LoadedAssetsEvent<String, AudioState, IndexedLookupTableAssetMap<String, AudioState>> event) {
      if (!event.isInitial()) {
         refreshAllAudioStateResolutions();
      }
   }

   public static void onAudioStateRemoved(@Nonnull RemovedAssetsEvent<String, AudioState, IndexedLookupTableAssetMap<String, AudioState>> event) {
      refreshAllAudioStateResolutions();
   }

   private static void refreshAllAudioStateResolutions() {
      for (AudioCategory cat : getAssetMap().getAssetMap().values()) {
         if (cat != null) {
            cat.refreshAudioStateResolution();
         }
      }
   }

   public float getVolume() {
      return this.volume;
   }

   @Nonnull
   @Override
   public String toString() {
      return "AudioCategory{id='" + this.id + "', volume=" + this.volume + "}";
   }

   @Nonnull
   public com.hypixel.hytale.protocol.AudioCategory toPacket() {
      com.hypixel.hytale.protocol.AudioCategory packet = new com.hypixel.hytale.protocol.AudioCategory();
      packet.id = this.id;
      packet.volume = this.volume;
      packet.parentAudioCategoryIndex = this.resolveParentIndex();
      packet.stateBindings = AudioStateResolver.toPacketArray(this.stateBindings);
      return packet;
   }

   private int resolveParentIndex() {
      if (this.data == null) {
         return -1;
      }

      AssetStore<String, AudioCategory, IndexedLookupTableAssetMap<String, AudioCategory>> store = getAssetStore();
      String parentKey = store.transformKey(this.data.getParentKey());
      if (parentKey == null) {
         return -1;
      }

      int index = ((IndexedLookupTableAssetMap)store.getAssetMap()).getIndex(parentKey);
      return index == Integer.MIN_VALUE ? -1 : index;
   }
}
