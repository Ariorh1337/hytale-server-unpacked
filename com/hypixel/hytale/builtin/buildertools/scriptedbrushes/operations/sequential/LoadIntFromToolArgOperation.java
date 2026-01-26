/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential;

import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfig;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigCommandExecutor;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.system.SequenceBrushOperation;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class LoadIntFromToolArgOperation
extends SequenceBrushOperation {
    public static final BuilderCodec<LoadIntFromToolArgOperation> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LoadIntFromToolArgOperation.class, LoadIntFromToolArgOperation::new).append(new KeyedCodec<String>("ArgName", Codec.STRING, true), (op, val) -> {
        op.argNameArg = val;
    }, op -> op.argNameArg).documentation("The name of the Int tool arg to load the value from").add()).append(new KeyedCodec<TargetField>("TargetField", new EnumCodec<TargetField>(TargetField.class)), (op, val) -> {
        op.targetFieldArg = val;
    }, op -> op.targetFieldArg).documentation("The brush config field to set (Width, Height, Density, Thickness, OffsetX, OffsetY, OffsetZ)").add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (op, val) -> {
        op.relativeArg = val;
    }, op -> op.relativeArg).documentation("When true, adds the loaded value to the current field value instead of replacing it").add()).append(new KeyedCodec<Boolean>("Negate", Codec.BOOLEAN), (op, val) -> {
        op.negateArg = val;
    }, op -> op.negateArg).documentation("When true, turns the sign of the value to negative").add()).documentation("Load an integer from an Int tool arg and apply it to a brush config field")).build();
    @Nonnull
    public String argNameArg = "";
    @Nonnull
    public TargetField targetFieldArg = TargetField.Width;
    public boolean relativeArg;
    public boolean negateArg;

    public LoadIntFromToolArgOperation() {
        super("Load Int", "Load an integer from an Int tool arg and apply it to a brush config field", false);
    }

    @Override
    public void modifyBrushConfig(@Nonnull Ref<EntityStore> ref, @Nonnull BrushConfig brushConfig, @Nonnull BrushConfigCommandExecutor brushConfigCommandExecutor, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
        if (builderTool == null) {
            brushConfig.setErrorFlag("LoadInt: No active builder tool");
            return;
        }
        ItemStack itemStack = playerComponent.getInventory().getItemInHand();
        if (itemStack == null) {
            brushConfig.setErrorFlag("LoadInt: No item in hand");
            return;
        }
        BuilderTool.ArgData argData = builderTool.getItemArgData(itemStack);
        Map<String, Object> toolArgs = argData.tool();
        if (toolArgs == null || !toolArgs.containsKey(this.argNameArg)) {
            brushConfig.setErrorFlag("LoadInt: Tool arg '" + this.argNameArg + "' not found");
            return;
        }
        Object argValue = toolArgs.get(this.argNameArg);
        if (argValue instanceof Integer) {
            Integer intValue = (Integer)argValue;
            if (this.negateArg) {
                intValue = intValue * -1;
            }
            if (this.relativeArg) {
                int currentValue = this.targetFieldArg.getValue(brushConfig);
                this.targetFieldArg.setValue(brushConfig, currentValue + intValue);
            } else {
                this.targetFieldArg.setValue(brushConfig, intValue);
            }
        } else {
            brushConfig.setErrorFlag("LoadInt: Tool arg '" + this.argNameArg + "' is not an Int type (found " + argValue.getClass().getSimpleName() + ")");
        }
    }

    public static enum TargetField {
        None(null, null),
        Width(BrushConfig::getShapeWidth, BrushConfig::setShapeWidth),
        Height(BrushConfig::getShapeHeight, BrushConfig::setShapeHeight),
        Density(BrushConfig::getDensity, BrushConfig::setDensity),
        Thickness(BrushConfig::getShapeThickness, BrushConfig::setShapeThickness),
        OffsetX(bc -> bc.getOriginOffset().x, (bc, val) -> bc.setOriginOffset(new Vector3i((int)val, bc.getOriginOffset().y, bc.getOriginOffset().z))),
        OffsetY(bc -> bc.getOriginOffset().y, (bc, val) -> bc.setOriginOffset(new Vector3i(bc.getOriginOffset().x, (int)val, bc.getOriginOffset().z))),
        OffsetZ(bc -> bc.getOriginOffset().z, (bc, val) -> bc.setOriginOffset(new Vector3i(bc.getOriginOffset().x, bc.getOriginOffset().y, (int)val)));

        private final Function<BrushConfig, Integer> getter;
        private final BiConsumer<BrushConfig, Integer> setter;

        private TargetField(Function<BrushConfig, Integer> getter, BiConsumer<BrushConfig, Integer> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        public int getValue(BrushConfig brushConfig) {
            return this.getter.apply(brushConfig);
        }

        public void setValue(BrushConfig brushConfig, int value) {
            this.setter.accept(brushConfig, value);
        }
    }
}

