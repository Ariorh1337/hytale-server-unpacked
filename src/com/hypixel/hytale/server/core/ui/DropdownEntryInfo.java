package com.hypixel.hytale.server.core.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.record.RecordCodec;
import javax.annotation.Nullable;

public record DropdownEntryInfo(LocalizableString label, String value, @Nullable LocalizableString tooltip) {
   public static final RecordCodec<DropdownEntryInfo> CODEC = RecordCodec.builder(DropdownEntryInfo.class)
      .append(new KeyedCodec<>("Label", LocalizableString.CODEC), DropdownEntryInfo::label)
      .append(new KeyedCodec<>("Value", Codec.STRING), DropdownEntryInfo::value)
      .append(new KeyedCodec<>("Tooltip", LocalizableString.CODEC, false), DropdownEntryInfo::tooltip)
      .build(DropdownEntryInfo::new);

   public DropdownEntryInfo(LocalizableString label, String value) {
      this(label, value, null);
   }
}
