package com.hypixel.hytale.server.core.asset.type.musiccontainer.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerPlacement {
   @Nonnull
   public static final BuilderCodec<LayerPlacement> CODEC = BuilderCodec.builder(LayerPlacement.class, LayerPlacement::new)
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (lp, s) -> lp.name = s, lp -> lp.name)
      .addValidator(Validators.nonNull())
      .documentation("Identifier used by Segment.States to reference this layer. Must be unique within the Segment's Layers array.")
      .add()
      .<String>append(new KeyedCodec<>("Container", MusicContainer.CHILD_ASSET_CODEC, true), (lp, s) -> lp.container = s, lp -> lp.container)
      .addValidator(Validators.nonNull())
      .documentation("The MusicContainer asset providing this layer's audio.")
      .add()
      .<BarBeatDuration>append(new KeyedCodec<>("ClipStart", BarBeatDuration.CODEC), (lp, v) -> lp.clipStart = v, lp -> lp.clipStart)
      .documentation(
         "Position on the Segment's timeline where this clip's file byte 0 plays. Negative = pre-entry (earlier than the downbeat). 0 = at the downbeat."
      )
      .add()
      .build();
   @Nullable
   protected String name;
   @Nullable
   protected String container;
   @Nullable
   protected BarBeatDuration clipStart;

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getContainer() {
      return this.container;
   }

   @Nullable
   public BarBeatDuration getClipStart() {
      return this.clipStart;
   }
}
