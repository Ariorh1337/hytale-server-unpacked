package com.hypixel.hytale.builtin.encountermanager;

import com.google.gson.JsonElement;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectListHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionContextHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.SupportConfigBuilder;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderEncounterManager extends SupportConfigBuilder<EncounterManager> implements EncounterBuilder {
   protected final BuilderObjectListHelper<Instruction> instructionList = new BuilderObjectListHelper<>(Instruction.class, this);

   @Nonnull
   @Override
   public String getShortDescription() {
      return "An encounter manager";
   }

   @Nonnull
   @Override
   public String getLongDescription() {
      return "An entity which uses instruction lists to drive an encounter.";
   }

   @Nonnull
   @Override
   public BuilderDescriptorState getBuilderDescriptorState() {
      return BuilderDescriptorState.WorkInProgress;
   }

   @Nullable
   public EncounterManager build(@Nonnull BuilderSupport builderSupport) {
      throw new UnsupportedOperationException("Use BuilderEncounterManager.createAndAttach(holder, builderSupport) - EncounterManager requires entity context");
   }

   @Nonnull
   @Override
   public EncounterManager createAndAttach(@Nonnull Holder<EntityStore> holder, @Nonnull BuilderSupport builderSupport) {
      return EncounterManager.createAndAttach(holder, this, builderSupport);
   }

   @Nonnull
   public BuilderEncounterManager readConfig(@Nonnull JsonElement data) {
      super.readConfig(data);
      this.setNotComponent();
      this.getArray(
         data,
         "Instructions",
         this.instructionList,
         null,
         BuilderDescriptorState.Stable,
         "List of instructions the encounter runs",
         null,
         new BuilderValidationHelper(
            this.fileName,
            null,
            this.internalReferenceResolver,
            this.stateHelper,
            new InstructionContextHelper(InstructionType.Encounter),
            this.extraInfo,
            this.evaluators,
            this.readErrors
         )
      );
      this.getObject(
         data,
         "StateTransitions",
         this.stateTransitionController,
         BuilderDescriptorState.Stable,
         "State transition actions",
         "A set of state transitions and the actions that will be executed during them",
         new BuilderValidationHelper(
            this.fileName,
            new FeatureEvaluatorHelper().lock(),
            this.internalReferenceResolver,
            this.stateHelper,
            new InstructionContextHelper(InstructionType.EncounterStateTransitions),
            this.extraInfo,
            this.evaluators,
            this.readErrors
         )
      );
      return this;
   }

   @Override
   public boolean validate(
      String configName,
      @Nonnull NPCLoadTimeValidationHelper validationHelper,
      @Nonnull ExecutionContext context,
      Scope globalScope,
      @Nonnull List<String> errors
   ) {
      validationHelper.pushPrecedingScope();
      boolean instructionListValid = this.instructionList.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
      validationHelper.popPrecedingScope();
      return super.validate(configName, validationHelper, context, globalScope, errors)
         & instructionListValid
         & this.stateTransitionController.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
   }

   @Nonnull
   @Override
   public Class<EncounterManager> category() {
      return EncounterManager.class;
   }

   @Override
   public final boolean isEnabled(ExecutionContext context) {
      return true;
   }

   @Nullable
   public List<Instruction> getInstructionList(@Nonnull BuilderSupport support) {
      support.setCurrentInstructionContext(InstructionType.Encounter);
      return this.instructionList.build(support);
   }
}
