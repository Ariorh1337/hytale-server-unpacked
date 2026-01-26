/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.damage;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathItemLoss;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeathComponent
implements Component<EntityStore> {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final BuilderCodec<DeathComponent> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DeathComponent.class, DeathComponent::new).append(new KeyedCodec<String>("DeathCause", Codec.STRING), (o, i) -> {
        o.deathCause = i;
    }, o -> o.deathCause).add()).append(new KeyedCodec("DeathMessage", Message.CODEC), (deathComponent, message) -> {
        deathComponent.deathMessage = message;
    }, deathComponent -> deathComponent.deathMessage).add()).append(new KeyedCodec<Boolean>("ShowDeathMenu", BuilderCodec.BOOLEAN), (deathComponent, showDeathMenu) -> {
        deathComponent.showDeathMenu = showDeathMenu;
    }, deathComponent -> deathComponent.showDeathMenu).add()).append(new KeyedCodec<T[]>("ItemsLostOnDeath", new ArrayCodec<ItemStack>(ItemStack.CODEC, ItemStack[]::new)), (deathComponent, itemStacks) -> {
        deathComponent.itemsLostOnDeath = itemStacks;
    }, deathComponent -> deathComponent.itemsLostOnDeath).add()).append(new KeyedCodec<Double>("ItemsAmountLossPercentage", Codec.DOUBLE), (deathComponent, aDouble) -> {
        deathComponent.itemsAmountLossPercentage = aDouble;
    }, deathComponent -> deathComponent.itemsAmountLossPercentage).add()).append(new KeyedCodec<Double>("ItemsDurabilityLossPercentage", Codec.DOUBLE), (deathComponent, aDouble) -> {
        deathComponent.itemsDurabilityLossPercentage = aDouble;
    }, deathComponent -> deathComponent.itemsDurabilityLossPercentage).add()).append(new KeyedCodec<Boolean>("DisplayDataOnDeathScreen", Codec.BOOLEAN), (deathComponent, aBoolean) -> {
        deathComponent.displayDataOnDeathScreen = aBoolean;
    }, deathComponent -> deathComponent.displayDataOnDeathScreen).add()).build();
    private String deathCause;
    @Nullable
    private Message deathMessage;
    private boolean showDeathMenu = true;
    private ItemStack[] itemsLostOnDeath;
    private double itemsAmountLossPercentage;
    private double itemsDurabilityLossPercentage;
    private boolean displayDataOnDeathScreen;
    @Nullable
    private transient Damage deathInfo;
    private transient DeathConfig.ItemsLossMode itemsLossMode = DeathConfig.ItemsLossMode.ALL;
    @Nullable
    private transient InteractionChain interactionChain;
    private transient CompletableFuture<Void> respawnFuture = null;

    public static ComponentType<EntityStore, DeathComponent> getComponentType() {
        return DamageModule.get().getDeathComponentType();
    }

    protected DeathComponent(@Nonnull Damage deathInfo) {
        this.deathInfo = deathInfo;
        this.deathCause = deathInfo.getCause().getId();
    }

    protected DeathComponent() {
    }

    @Nullable
    public DamageCause getDeathCause() {
        return (DamageCause)((IndexedLookupTableAssetMap)AssetRegistry.getAssetStore(DamageCause.class).getAssetMap()).getAsset(this.deathCause);
    }

    @Nullable
    public Message getDeathMessage() {
        return this.deathMessage;
    }

    public void setDeathMessage(@Nullable Message deathMessage) {
        this.deathMessage = deathMessage;
    }

    public boolean isShowDeathMenu() {
        return this.showDeathMenu;
    }

    public void setShowDeathMenu(boolean showDeathMenu) {
        this.showDeathMenu = showDeathMenu;
    }

    public ItemStack[] getItemsLostOnDeath() {
        return this.itemsLostOnDeath;
    }

    public void setItemsLostOnDeath(List<ItemStack> itemsLostOnDeath) {
        this.itemsLostOnDeath = (ItemStack[])itemsLostOnDeath.toArray(ItemStack[]::new);
    }

    public double getItemsAmountLossPercentage() {
        return this.itemsAmountLossPercentage;
    }

    public void setItemsAmountLossPercentage(double itemsAmountLossPercentage) {
        this.itemsAmountLossPercentage = itemsAmountLossPercentage;
    }

    public double getItemsDurabilityLossPercentage() {
        return this.itemsDurabilityLossPercentage;
    }

    public void setItemsDurabilityLossPercentage(double itemsDurabilityLossPercentage) {
        this.itemsDurabilityLossPercentage = itemsDurabilityLossPercentage;
    }

    public boolean displayDataOnDeathScreen() {
        return this.displayDataOnDeathScreen;
    }

    public void setDisplayDataOnDeathScreen(boolean displayDataOnDeathScreen) {
        this.displayDataOnDeathScreen = displayDataOnDeathScreen;
    }

    @Nullable
    public Damage getDeathInfo() {
        return this.deathInfo;
    }

    public DeathConfig.ItemsLossMode getItemsLossMode() {
        return this.itemsLossMode;
    }

    public void setItemsLossMode(DeathConfig.ItemsLossMode itemsLossMode) {
        this.itemsLossMode = itemsLossMode;
    }

    public DeathItemLoss getDeathItemLoss() {
        return new DeathItemLoss(this.itemsLossMode, this.itemsLostOnDeath, this.itemsAmountLossPercentage, this.itemsDurabilityLossPercentage);
    }

    @Nullable
    public InteractionChain getInteractionChain() {
        return this.interactionChain;
    }

    public void setInteractionChain(@Nullable InteractionChain interactionChain) {
        this.interactionChain = interactionChain;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        DeathComponent death = new DeathComponent();
        death.deathCause = this.deathCause;
        death.deathMessage = this.deathMessage;
        death.showDeathMenu = this.showDeathMenu;
        death.itemsLostOnDeath = this.itemsLostOnDeath;
        death.itemsAmountLossPercentage = this.itemsAmountLossPercentage;
        death.itemsDurabilityLossPercentage = this.itemsDurabilityLossPercentage;
        death.displayDataOnDeathScreen = this.displayDataOnDeathScreen;
        death.deathInfo = this.deathInfo;
        death.itemsLossMode = this.itemsLossMode;
        return death;
    }

    public static void tryAddComponent(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull Damage damage) {
        if (commandBuffer.getArchetype(ref).contains(DeathComponent.getComponentType())) {
            return;
        }
        commandBuffer.run(store -> DeathComponent.tryAddComponent(store, ref, damage));
    }

    public static void tryAddComponent(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Damage damage) {
        if (store.getArchetype(ref).contains(DeathComponent.getComponentType())) {
            return;
        }
        store.addComponent(ref, DeathComponent.getComponentType(), new DeathComponent(damage));
    }

    public static CompletableFuture<Void> respawn(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref) {
        DeathComponent deathComponent = componentAccessor.getComponent(ref, DeathComponent.getComponentType());
        if (deathComponent == null) {
            return CompletableFuture.completedFuture(null);
        }
        if (deathComponent.respawnFuture != null) {
            return deathComponent.respawnFuture;
        }
        World world = componentAccessor.getExternalData().getWorld();
        RespawnController respawnController = world.getDeathConfig().getRespawnController();
        deathComponent.respawnFuture = respawnController.respawnPlayer(world, ref, componentAccessor).whenComplete((ignore, ex) -> {
            if (ex != null) {
                ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atSevere()).withCause((Throwable)ex)).log("Failed to respawn entity");
            }
            Store<EntityStore> store = world.getEntityStore().getStore();
            if (!ref.isValid()) {
                return;
            }
            store.tryRemoveComponent(ref, DeathComponent.getComponentType());
        });
        return deathComponent.respawnFuture;
    }
}

