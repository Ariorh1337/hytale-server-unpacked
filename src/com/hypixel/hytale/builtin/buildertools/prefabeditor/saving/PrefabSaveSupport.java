package com.hypixel.hytale.builtin.buildertools.prefabeditor.saving;

import com.hypixel.hytale.builtin.blockphysics.BlockSelectionSupportUtil;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import javax.annotation.Nonnull;

public final class PrefabSaveSupport {
   private PrefabSaveSupport() {
   }

   public static void apply(@Nonnull BlockSelection selection, @Nonnull PrefabSaverSettings settings) {
      apply(selection, settings.getSupportMode());
   }

   public static void apply(@Nonnull BlockSelection selection, @Nonnull SupportMode supportMode) {
      switch (supportMode) {
         case KEEP_EXISTING:
         default:
            break;
         case REMOVE:
            selection.setAllSupportValues(15);
            break;
         case CALCULATE:
            selection.clearAllSupportValues();
            BlockSelectionSupportUtil.applySupportValues(selection);
      }
   }
}
