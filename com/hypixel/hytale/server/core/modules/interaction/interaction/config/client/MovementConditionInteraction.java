/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementDirection;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MovementConditionInteraction
extends SimpleInteraction {
    @Nonnull
    public static final BuilderCodec<MovementConditionInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MovementConditionInteraction.class, MovementConditionInteraction::new, SimpleInteraction.CODEC).documentation("An interaction that runs different interactions based on the movement the user is current performing.")).appendInherited(new KeyedCodec<String>("Forward", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.forward = s;
    }, interaction -> interaction.forward, (interaction, parent) -> {
        interaction.forward = parent.forward;
    }).documentation("The interaction to run if the player is moving forward.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("Back", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.back = s;
    }, interaction -> interaction.back, (interaction, parent) -> {
        interaction.back = parent.back;
    }).documentation("The interaction to run if the player is moving backwards.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("Left", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.left = s;
    }, interaction -> interaction.left, (interaction, parent) -> {
        interaction.left = parent.left;
    }).documentation("The interaction to run if the player is moving left.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("Right", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.right = s;
    }, interaction -> interaction.right, (interaction, parent) -> {
        interaction.right = parent.right;
    }).documentation("The interaction to run if the player is moving right.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("ForwardLeft", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.forwardLeft = s;
    }, interaction -> interaction.forwardLeft, (interaction, parent) -> {
        interaction.forwardLeft = parent.forwardLeft;
    }).documentation("The interaction to run if the player is moving forward and left.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("ForwardRight", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.forwardRight = s;
    }, interaction -> interaction.forwardRight, (interaction, parent) -> {
        interaction.forwardRight = parent.forwardRight;
    }).documentation("The interaction to run if the player is moving forward and right.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("BackLeft", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.backLeft = s;
    }, interaction -> interaction.backLeft, (interaction, parent) -> {
        interaction.backLeft = parent.backLeft;
    }).documentation("The interaction to run if the player is moving backwards and left.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("BackRight", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.backRight = s;
    }, interaction -> interaction.backRight, (interaction, parent) -> {
        interaction.backRight = parent.backRight;
    }).documentation("The interaction to run if the player is moving backwards and right.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
    private static final int FAILED_LABEL_INDEX = 0;
    private static final int FORWARD_LABEL_INDEX = 1;
    private static final int BACK_LABEL_INDEX = 2;
    private static final int LEFT_LABEL_INDEX = 3;
    private static final int RIGHT_LABEL_INDEX = 4;
    private static final int FORWARD_LEFT_LABEL_INDEX = 5;
    private static final int FORWARD_RIGHT_LABEL_INDEX = 6;
    private static final int BACK_LEFT_LABEL_INDEX = 7;
    private static final int BACK_RIGHT_LABEL_INDEX = 8;
    @Nullable
    private String forward;
    @Nullable
    private String back;
    @Nullable
    private String left;
    @Nullable
    private String right;
    @Nullable
    private String forwardLeft;
    @Nullable
    private String forwardRight;
    @Nullable
    private String backLeft;
    @Nullable
    private String backRight;

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        context.getState().state = InteractionState.Finished;
        context.jump(context.getLabel(switch (context.getClientState().movementDirection) {
            default -> throw new MatchException(null, null);
            case MovementDirection.None -> 0;
            case MovementDirection.Forward -> 1;
            case MovementDirection.Back -> 2;
            case MovementDirection.Left -> 3;
            case MovementDirection.Right -> 4;
            case MovementDirection.ForwardLeft -> 5;
            case MovementDirection.ForwardRight -> 6;
            case MovementDirection.BackLeft -> 7;
            case MovementDirection.BackRight -> 8;
        }));
    }

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        context.getState().movementDirection = MovementDirection.None;
        context.jump(context.getLabel(0));
    }

    @Override
    public void compile(@Nonnull OperationsBuilder builder) {
        Label[] labels = new Label[9];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = builder.createUnresolvedLabel();
        }
        builder.addOperation(this, labels);
        Label endLabel = builder.createUnresolvedLabel();
        MovementConditionInteraction.resolve(builder, this.failed, labels[0], endLabel);
        MovementConditionInteraction.resolve(builder, this.forward, labels[1], endLabel);
        MovementConditionInteraction.resolve(builder, this.back, labels[2], endLabel);
        MovementConditionInteraction.resolve(builder, this.left, labels[3], endLabel);
        MovementConditionInteraction.resolve(builder, this.right, labels[4], endLabel);
        MovementConditionInteraction.resolve(builder, this.forwardLeft, labels[5], endLabel);
        MovementConditionInteraction.resolve(builder, this.forwardRight, labels[6], endLabel);
        MovementConditionInteraction.resolve(builder, this.backLeft, labels[7], endLabel);
        MovementConditionInteraction.resolve(builder, this.backRight, labels[8], endLabel);
        builder.resolveLabel(endLabel);
    }

    private static void resolve(@Nonnull OperationsBuilder builder, @Nullable String id, @Nonnull Label label, @Nonnull Label endLabel) {
        builder.resolveLabel(label);
        if (id != null) {
            Interaction interaction = Interaction.getInteractionOrUnknown(id);
            interaction.compile(builder);
        }
        builder.jump(endLabel);
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.MovementConditionInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.MovementConditionInteraction p = (com.hypixel.hytale.protocol.MovementConditionInteraction)packet;
        p.forward = Interaction.getInteractionIdOrUnknown(this.forward);
        p.back = Interaction.getInteractionIdOrUnknown(this.back);
        p.left = Interaction.getInteractionIdOrUnknown(this.left);
        p.right = Interaction.getInteractionIdOrUnknown(this.right);
        p.forwardLeft = Interaction.getInteractionIdOrUnknown(this.forwardLeft);
        p.forwardRight = Interaction.getInteractionIdOrUnknown(this.forwardRight);
        p.backLeft = Interaction.getInteractionIdOrUnknown(this.backLeft);
        p.backRight = Interaction.getInteractionIdOrUnknown(this.backRight);
    }
}

