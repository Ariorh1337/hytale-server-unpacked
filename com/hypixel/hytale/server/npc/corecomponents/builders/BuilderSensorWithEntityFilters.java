/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectListHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.ComponentContext;
import com.hypixel.hytale.server.npc.asset.builder.InstructionContextHelper;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.ISensorEntityPrioritiser;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BuilderSensorWithEntityFilters
extends BuilderSensorBase {
    protected final BuilderObjectListHelper<IEntityFilter> filters = new BuilderObjectListHelper(IEntityFilter.class, this);

    @Override
    public boolean validate(String configName, @Nonnull NPCLoadTimeValidationHelper validationHelper, @Nonnull ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        boolean result = super.validate(configName, validationHelper, context, globalScope, errors);
        validationHelper.pushFilterSet();
        validationHelper.popFilterSet();
        return result &= this.filters.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
    }

    @Nonnull
    public IEntityFilter[] getFilters(@Nonnull BuilderSupport support, @Nullable ISensorEntityPrioritiser prioritiser, ComponentContext context) {
        if (!this.filters.isPresent() || this.filters.isEmpty()) {
            if (prioritiser == null || !prioritiser.providesFilters()) {
                return IEntityFilter.EMPTY_ARRAY;
            }
            ObjectArrayList<IEntityFilter> builtFilters = new ObjectArrayList<IEntityFilter>();
            prioritiser.buildProvidedFilters(builtFilters);
            return (IEntityFilter[])builtFilters.toArray(IEntityFilter[]::new);
        }
        support.setCurrentComponentContext(context);
        Object builtFilters = this.filters.build(support);
        if (prioritiser != null) {
            prioritiser.buildProvidedFilters((List<IEntityFilter>)builtFilters);
        }
        support.setCurrentComponentContext(null);
        return (IEntityFilter[])builtFilters.toArray(IEntityFilter[]::new);
    }

    @Nonnull
    protected BuilderValidationHelper createFilterValidationHelper(ComponentContext context) {
        InstructionContextHelper instructionContextHelper = new InstructionContextHelper(this.isCreatingDescriptor() ? null : this.getInstructionContextHelper().getInstructionContext());
        instructionContextHelper.setComponentContext(context);
        return new BuilderValidationHelper(this.fileName, null, this.internalReferenceResolver, null, instructionContextHelper, this.extraInfo, this.evaluators, this.readErrors);
    }
}

