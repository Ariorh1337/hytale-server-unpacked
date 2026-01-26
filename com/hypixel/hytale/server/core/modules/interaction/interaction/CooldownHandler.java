/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction;

import com.hypixel.hytale.common.thread.ticking.Tickable;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CooldownHandler
implements Tickable {
    @Nonnull
    private final Map<String, Cooldown> cooldowns = new ConcurrentHashMap<String, Cooldown>();

    public boolean isOnCooldown(@Nonnull RootInteraction root, @Nonnull String id, float maxTime, @Nonnull float[] chargeTimes, boolean interruptRecharge) {
        if (maxTime <= 0.0f) {
            return false;
        }
        Cooldown cooldown = this.getCooldown(id, maxTime, chargeTimes, root.resetCooldownOnStart(), interruptRecharge);
        return cooldown != null && cooldown.hasCooldown(true);
    }

    public void resetCooldown(@Nonnull String id, float maxTime, @Nonnull float[] chargeTimes, boolean interruptRecharge) {
        Cooldown cooldown = this.getCooldown(id, maxTime, chargeTimes, true, interruptRecharge);
        if (cooldown != null) {
            cooldown.resetCooldown();
            cooldown.resetCharges();
        }
    }

    @Nullable
    public Cooldown getCooldown(@Nonnull String id, float maxTime, @Nonnull float[] chargeTimes, boolean force, boolean interruptRecharge) {
        return force ? this.cooldowns.computeIfAbsent(id, k -> new Cooldown(maxTime, chargeTimes, interruptRecharge)) : this.cooldowns.get(id);
    }

    @Nullable
    public Cooldown getCooldown(@Nonnull String id) {
        return this.cooldowns.get(id);
    }

    @Override
    public void tick(float dt) {
        this.cooldowns.values().removeIf(cooldown -> cooldown.tick(dt));
    }

    public static class Cooldown {
        private float cooldownMax;
        private float[] charges;
        private float remainingCooldown = 0.0f;
        private float chargeTimer;
        private int chargeCount;
        private final boolean interruptRecharge;

        public Cooldown(float cooldownMax, @Nonnull float[] charges, boolean interruptRecharge) {
            this.setCooldownMax(cooldownMax);
            this.setCharges(charges);
            this.resetCharges();
            this.interruptRecharge = interruptRecharge;
        }

        public void setCooldownMax(float cooldownMax) {
            this.cooldownMax = cooldownMax;
            if (this.remainingCooldown > cooldownMax) {
                this.remainingCooldown = cooldownMax;
            }
        }

        public void setCharges(@Nonnull float[] charges) {
            this.charges = charges;
            if (this.chargeCount > charges.length) {
                this.chargeCount = charges.length;
            }
        }

        public boolean hasCooldown(boolean deduct) {
            if (this.remainingCooldown <= 0.0f && this.chargeCount > 0) {
                if (deduct) {
                    this.deductCharge();
                }
                return false;
            }
            return true;
        }

        public boolean hasMaxCharges() {
            return this.chargeCount >= this.charges.length;
        }

        public void resetCharges() {
            this.chargeCount = this.charges.length;
        }

        public void resetCooldown() {
            this.remainingCooldown = this.cooldownMax;
        }

        public void deductCharge() {
            if (this.chargeCount > 0) {
                --this.chargeCount;
            }
            if (this.interruptRecharge) {
                this.chargeTimer = 0.0f;
            }
            this.resetCooldown();
        }

        public boolean tick(float dt) {
            if (!this.hasMaxCharges()) {
                float chargeTimeMax = this.charges[this.chargeCount];
                this.chargeTimer += dt;
                if (this.chargeTimer >= chargeTimeMax) {
                    ++this.chargeCount;
                    this.chargeTimer = 0.0f;
                }
            }
            this.remainingCooldown -= dt;
            return (this.hasMaxCharges() || this.charges.length <= 1) && this.remainingCooldown <= 0.0f;
        }

        public float getCooldown() {
            return this.cooldownMax;
        }

        public float[] getCharges() {
            return this.charges;
        }

        public boolean interruptRecharge() {
            return this.interruptRecharge;
        }

        public void replenishCharge(int amount, boolean interrupt) {
            this.chargeCount = MathUtil.clamp(this.chargeCount + amount, 0, this.charges.length);
            if (interrupt && amount != 0) {
                this.chargeTimer = 0.0f;
            }
        }

        public void increaseTime(float time) {
            this.remainingCooldown = MathUtil.clamp(this.remainingCooldown + time, 0.0f, this.cooldownMax);
        }

        public void increaseChargeTime(float time) {
            if (this.hasMaxCharges()) {
                return;
            }
            if (this.charges.length <= 1) {
                return;
            }
            float chargeTimeMax = this.charges[this.chargeCount];
            this.chargeTimer = MathUtil.clamp(this.chargeTimer + time, 0.0f, chargeTimeMax);
        }
    }
}

