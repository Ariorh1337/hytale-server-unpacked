package com.hypixel.hytale.server.npc.corecomponents.entity;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.components.messaging.BeaconReceiverProvider;
import com.hypixel.hytale.server.npc.components.messaging.BeaconSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.builders.BuilderActionBeacon;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.role.support.PositionCache;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ActionBeacon extends ActionBase {
   private static final ThreadLocal<List<Ref<EntityStore>>> PROVIDED_RECEIVERS = ThreadLocal.withInitial(ReferenceArrayList::new);
   protected final String message;
   protected final double range;
   protected final int[] targetGroups;
   protected final int targetToSendSlot;
   protected final double expirationTime;
   protected final int sendCount;
   @Nullable
   protected final List<Ref<EntityStore>> sendList;

   public ActionBeacon(@Nonnull BuilderActionBeacon builderActionBeacon, @Nonnull BuilderSupport support) {
      super(builderActionBeacon);
      this.message = builderActionBeacon.getMessage(support);
      this.range = builderActionBeacon.getRange(support);
      this.targetGroups = builderActionBeacon.getTargetGroups(support);
      this.targetToSendSlot = builderActionBeacon.getTargetToSendSlot(support);
      this.expirationTime = builderActionBeacon.getExpirationTime();
      this.sendCount = builderActionBeacon.getSendCount();
      this.sendList = this.sendCount > 0 ? new ReferenceArrayList<>(this.sendCount) : null;
   }

   @Override
   public void registerWithSupport(@Nonnull ExecutionSupport executionSupport) {
      executionSupport.getPositionCache().requireEntityDistanceUnsorted(this.range);
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return !super.canExecute(ref, executionSupport, sensorInfo, dt, store)
         ? false
         : this.targetToSendSlot == Integer.MIN_VALUE || executionSupport.getMarkedEntitySupport().hasMarkedEntityInSlot(this.targetToSendSlot);
   }

   @Override
   public boolean execute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      super.execute(ref, executionSupport, sensorInfo, dt, store);
      Ref<EntityStore> target = this.targetToSendSlot >= 0 ? executionSupport.getMarkedEntitySupport().getMarkedEntityRef(this.targetToSendSlot) : ref;
      PositionCache positionCache = executionSupport.getPositionCache();
      if (this.sendCount <= 0) {
         positionCache.forEachNPCUnordered(
            this.range,
            ActionBeacon::filterNPCs,
            (_ref, _this, _target, _self) -> _this.sendNPCMessage(_self, _ref, _target, _self.getStore()),
            this,
            executionSupport,
            target,
            ref,
            store
         );
         this.sendToProvidedReceivers(ref, executionSupport, target, store);
         return true;
      }

      positionCache.forEachNPCUnordered(
         this.range,
         ActionBeacon::filterNPCs,
         (npcEntity, _this, _sendList, _self) -> RandomExtra.reservoirSample(npcEntity, _this.sendCount, _sendList),
         this,
         executionSupport,
         this.sendList,
         ref,
         store
      );
      this.sendToProvidedReceivers(ref, executionSupport, target, store);

      for (int i = 0; i < this.sendList.size(); i++) {
         this.sendNPCMessage(ref, this.sendList.get(i), target, store);
      }

      this.sendList.clear();
      return true;
   }

   private void sendToProvidedReceivers(
      @Nonnull Ref<EntityStore> self, @Nonnull ExecutionSupport executionSupport, @Nonnull Ref<EntityStore> target, @Nonnull Store<EntityStore> store
   ) {
      List<BeaconReceiverProvider> providers = NPCPlugin.get().getBeaconReceiverProviders();
      if (!providers.isEmpty()) {
         TransformComponent transformComponent = store.getComponent(self, TransformComponent.getComponentType());
         if (transformComponent != null) {
            Vector3d origin = transformComponent.getPosition();
            List<Ref<EntityStore>> receivers = PROVIDED_RECEIVERS.get();
            receivers.clear();

            for (int i = 0; i < providers.size(); i++) {
               providers.get(i).collectInRange(origin, this.range, store, receivers);
            }

            for (int i = 0; i < receivers.size(); i++) {
               Ref<EntityStore> candidate = receivers.get(i);
               if (!candidate.equals(self) && filterNPCs(candidate, this, executionSupport, store)) {
                  if (this.sendCount <= 0) {
                     this.sendNPCMessage(self, candidate, target, store);
                  } else {
                     RandomExtra.reservoirSample(candidate, this.sendCount, this.sendList);
                  }
               }
            }

            receivers.clear();
         }
      }
   }

   protected static boolean filterNPCs(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ActionBeacon _this,
      @Nonnull ExecutionSupport executionSupport,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      return ref.getStore().getComponent(ref, BeaconSupport.getComponentType()) != null
         && WorldSupport.isGroupMember(executionSupport.getRoleIndex(), ref, _this.targetGroups, componentAccessor);
   }

   protected void sendNPCMessage(
      @Nonnull Ref<EntityStore> self,
      @Nonnull Ref<EntityStore> targetRef,
      @Nonnull Ref<EntityStore> target,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (DebugSupport.get(self, componentAccessor).isDebugFlagSet(RoleDebugFlags.BeaconMessages)) {
         NPCPlugin.get()
            .getLogger()
            .atInfo()
            .log("ID %d sent message '%s' with target ID %d to ID %d", self.getIndex(), this.message, target.getIndex(), targetRef.getIndex());
         ThreadLocalRandom random = ThreadLocalRandom.current();
         Vector3f color = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
         Matrix4d matrix = new Matrix4d();
         matrix.identity();
         TransformComponent transformComponent = componentAccessor.getComponent(self, TransformComponent.getComponentType());
         assert transformComponent != null;
         Vector3d pos = transformComponent.getPosition();
         ModelComponent modelComponent = componentAccessor.getComponent(self, ModelComponent.getComponentType());
         assert modelComponent != null;
         Model model = modelComponent.getModel();
         double x = pos.x;
         double y = pos.y + (model != null ? model.getEyeHeight(self, componentAccessor) : 0.0F);
         double z = pos.z;
         matrix.translate(x, y + random.nextFloat() * 0.5 - 0.25, z);
         TransformComponent targetTransformComponent = componentAccessor.getComponent(targetRef, TransformComponent.getComponentType());
         assert targetTransformComponent != null;
         Vector3d targetPos = targetTransformComponent.getPosition();
         ModelComponent targetModelComponent = componentAccessor.getComponent(targetRef, ModelComponent.getComponentType());
         float targetEyeHeight = targetModelComponent != null ? targetModelComponent.getModel().getEyeHeight(targetRef, componentAccessor) : 0.0F;
         x -= targetPos.x();
         y -= targetPos.y() + targetEyeHeight;
         z -= targetPos.z();
         double angleY = Math.atan2(-z, -x);
         matrix.rotate(-(angleY + (float) (Math.PI / 2)), 0.0, 1.0, 0.0);
         double angleX = Math.atan2(Math.sqrt(x * x + z * z), -y);
         matrix.rotate(-angleX, 1.0, 0.0, 0.0);
         DebugUtils.addArrow(componentAccessor.getExternalData().getWorld(), matrix, color, pos.distance(targetPos), 5.0F, DebugUtils.FLAG_FADE);
      }

      BeaconSupport beaconSupportComponent = componentAccessor.getComponent(targetRef, BeaconSupport.getComponentType());
      if (beaconSupportComponent != null) {
         beaconSupportComponent.postMessage(this.message, target, this.expirationTime);
      }
   }
}
