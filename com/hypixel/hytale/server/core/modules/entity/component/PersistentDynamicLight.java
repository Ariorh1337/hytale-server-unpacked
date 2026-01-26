/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class PersistentDynamicLight
implements Component<EntityStore> {
    public static final BuilderCodec<PersistentDynamicLight> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PersistentDynamicLight.class, PersistentDynamicLight::new).addField(new KeyedCodec<ColorLight>("Light", ProtocolCodecs.COLOR_LIGHT), (o, light) -> {
        o.colorLight = light;
    }, o -> o.colorLight)).build();
    private ColorLight colorLight;

    public static ComponentType<EntityStore, PersistentDynamicLight> getComponentType() {
        return EntityModule.get().getPersistentDynamicLightComponentType();
    }

    private PersistentDynamicLight() {
    }

    public PersistentDynamicLight(ColorLight colorLight) {
        this.colorLight = colorLight;
    }

    public ColorLight getColorLight() {
        return this.colorLight;
    }

    public void setColorLight(ColorLight colorLight) {
        this.colorLight = colorLight;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new PersistentDynamicLight(new ColorLight(this.colorLight));
    }
}

