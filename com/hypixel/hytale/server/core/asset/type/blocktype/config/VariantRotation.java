/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;

public enum VariantRotation implements NetworkSerializable<com.hypixel.hytale.protocol.VariantRotation>
{
    None(com.hypixel.hytale.protocol.VariantRotation.None, RotationTuple.EMPTY_ARRAY, pair -> RotationTuple.NONE, (pair, rotation) -> RotationTuple.NONE, (pair, rotation) -> RotationTuple.NONE),
    Wall(com.hypixel.hytale.protocol.VariantRotation.Wall, new RotationTuple[]{RotationTuple.of(Rotation.Ninety, Rotation.None)}, pair -> {
        if (pair.yaw() == Rotation.Ninety || pair.yaw() == Rotation.TwoSeventy) {
            return RotationTuple.of(Rotation.Ninety, Rotation.None);
        }
        return RotationTuple.of(Rotation.None, Rotation.None);
    }, (pair, rotation) -> pair, (pair, rotation) -> pair),
    UpDown(com.hypixel.hytale.protocol.VariantRotation.UpDown, new RotationTuple[]{RotationTuple.of(Rotation.None, Rotation.OneEighty)}, pair -> {
        if (pair.pitch() == Rotation.OneEighty) {
            return RotationTuple.of(Rotation.None, Rotation.OneEighty);
        }
        return RotationTuple.of(Rotation.None, Rotation.None);
    }, (pair, rotation) -> RotationTuple.of(pair.yaw(), pair.pitch().add((Rotation)rotation)), (pair, rotation) -> {
        if (pair.pitch().add((Rotation)rotation) == Rotation.OneEighty) {
            return RotationTuple.of(pair.yaw(), Rotation.OneEighty);
        }
        return RotationTuple.of(pair.yaw(), Rotation.None);
    }),
    Pipe(com.hypixel.hytale.protocol.VariantRotation.Pipe, new RotationTuple[]{RotationTuple.of(Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.Ninety)}, pair -> {
        if (pair.pitch() == Rotation.Ninety || pair.pitch() == Rotation.TwoSeventy) {
            return RotationTuple.of(VariantRotation.validatePipe(pair.yaw()), VariantRotation.validatePipe(pair.pitch()));
        }
        return RotationTuple.of(Rotation.None, VariantRotation.validatePipe(pair.pitch()));
    }, (pair, rotation) -> RotationTuple.of(pair.yaw(), pair.pitch().add((Rotation)rotation)), (pair, rotation) -> {
        if (pair.yaw() == Rotation.None && pair.pitch() == Rotation.Ninety) {
            return pair;
        }
        return switch (pair.yaw().add((Rotation)rotation)) {
            default -> throw new MatchException(null, null);
            case Rotation.None, Rotation.OneEighty -> RotationTuple.of(Rotation.None, Rotation.None);
            case Rotation.Ninety, Rotation.TwoSeventy -> RotationTuple.of(Rotation.Ninety, Rotation.Ninety);
        };
    }),
    DoublePipe(com.hypixel.hytale.protocol.VariantRotation.DoublePipe, new RotationTuple[]{RotationTuple.of(Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.OneEighty)}, pair -> switch (pair.pitch()) {
        case Rotation.TwoSeventy -> RotationTuple.of(pair.yaw().flip(), Rotation.Ninety);
        case Rotation.Ninety -> pair;
        case Rotation.OneEighty -> RotationTuple.of(Rotation.None, Rotation.OneEighty);
        default -> RotationTuple.NONE;
    }, (pair, rotation) -> {
        if ((pair.yaw() == Rotation.Ninety || pair.yaw() == Rotation.TwoSeventy) && pair.pitch() == Rotation.Ninety) {
            return pair;
        }
        return RotationTuple.getRotation(new RotationTuple[]{RotationTuple.NONE, RotationTuple.of(Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety)}, pair, rotation);
    }, (pair, rotation) -> {
        if (pair.yaw() == Rotation.None && (pair.pitch() == Rotation.Ninety || pair.pitch() == Rotation.TwoSeventy)) {
            return pair;
        }
        return RotationTuple.getRotation(new RotationTuple[]{RotationTuple.NONE, RotationTuple.of(Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety)}, pair, rotation);
    }),
    NESW(com.hypixel.hytale.protocol.VariantRotation.NESW, new RotationTuple[]{RotationTuple.of(Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.None)}, pair -> RotationTuple.of(pair.yaw(), Rotation.None), (pair, rotation) -> pair, (pair, rotation) -> pair),
    UpDownNESW(com.hypixel.hytale.protocol.VariantRotation.UpDownNESW, new RotationTuple[]{RotationTuple.of(Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.None), RotationTuple.of(Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.Ninety, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.OneEighty)}, pair -> {
        if (pair.pitch() == Rotation.OneEighty) {
            return RotationTuple.of(pair.yaw(), Rotation.OneEighty);
        }
        return RotationTuple.of(pair.yaw(), Rotation.None);
    }, (pair, rotation) -> RotationTuple.of(pair.yaw(), pair.pitch().add((Rotation)rotation)), (pair, rotation) -> {
        if (pair.pitch().add((Rotation)rotation) == Rotation.OneEighty) {
            return RotationTuple.of(pair.yaw(), Rotation.OneEighty);
        }
        return pair;
    }),
    Debug(com.hypixel.hytale.protocol.VariantRotation.UpDownNESW, new RotationTuple[]{RotationTuple.of(Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.None), RotationTuple.of(Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.Ninety, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.OneEighty), RotationTuple.of(Rotation.None, Rotation.TwoSeventy), RotationTuple.of(Rotation.Ninety, Rotation.TwoSeventy), RotationTuple.of(Rotation.OneEighty, Rotation.TwoSeventy), RotationTuple.of(Rotation.TwoSeventy, Rotation.TwoSeventy)}, Function.identity(), (pair, rotation) -> RotationTuple.of(pair.yaw(), pair.pitch().add((Rotation)rotation)), (pair, rotation) -> pair),
    All(com.hypixel.hytale.protocol.VariantRotation.All, new RotationTuple[]{RotationTuple.of(Rotation.None, Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.None, Rotation.None, Rotation.TwoSeventy), RotationTuple.of(Rotation.None, Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.None, Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.Ninety, Rotation.OneEighty), RotationTuple.of(Rotation.None, Rotation.Ninety, Rotation.TwoSeventy), RotationTuple.of(Rotation.None, Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.None, Rotation.OneEighty, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.OneEighty, Rotation.OneEighty), RotationTuple.of(Rotation.None, Rotation.OneEighty, Rotation.TwoSeventy), RotationTuple.of(Rotation.None, Rotation.TwoSeventy, Rotation.None), RotationTuple.of(Rotation.None, Rotation.TwoSeventy, Rotation.Ninety), RotationTuple.of(Rotation.None, Rotation.TwoSeventy, Rotation.OneEighty), RotationTuple.of(Rotation.None, Rotation.TwoSeventy, Rotation.TwoSeventy), RotationTuple.of(Rotation.Ninety, Rotation.None, Rotation.None), RotationTuple.of(Rotation.Ninety, Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.Ninety, Rotation.None, Rotation.TwoSeventy), RotationTuple.of(Rotation.Ninety, Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.Ninety, Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.Ninety, Rotation.OneEighty), RotationTuple.of(Rotation.Ninety, Rotation.Ninety, Rotation.TwoSeventy), RotationTuple.of(Rotation.Ninety, Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.Ninety, Rotation.OneEighty, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.OneEighty, Rotation.OneEighty), RotationTuple.of(Rotation.Ninety, Rotation.OneEighty, Rotation.TwoSeventy), RotationTuple.of(Rotation.Ninety, Rotation.TwoSeventy, Rotation.None), RotationTuple.of(Rotation.Ninety, Rotation.TwoSeventy, Rotation.Ninety), RotationTuple.of(Rotation.Ninety, Rotation.TwoSeventy, Rotation.OneEighty), RotationTuple.of(Rotation.Ninety, Rotation.TwoSeventy, Rotation.TwoSeventy), RotationTuple.of(Rotation.OneEighty, Rotation.None, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.OneEighty, Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.None, Rotation.TwoSeventy), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.Ninety, Rotation.TwoSeventy), RotationTuple.of(Rotation.OneEighty, Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.OneEighty, Rotation.Ninety), RotationTuple.of(Rotation.OneEighty, Rotation.OneEighty, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.OneEighty, Rotation.TwoSeventy), RotationTuple.of(Rotation.OneEighty, Rotation.TwoSeventy, Rotation.None), RotationTuple.of(Rotation.OneEighty, Rotation.TwoSeventy, Rotation.Ninety), RotationTuple.of(Rotation.OneEighty, Rotation.TwoSeventy, Rotation.OneEighty), RotationTuple.of(Rotation.OneEighty, Rotation.TwoSeventy, Rotation.TwoSeventy), RotationTuple.of(Rotation.TwoSeventy, Rotation.None, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.None, Rotation.Ninety), RotationTuple.of(Rotation.TwoSeventy, Rotation.None, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.None, Rotation.TwoSeventy), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety, Rotation.Ninety), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.Ninety, Rotation.TwoSeventy), RotationTuple.of(Rotation.TwoSeventy, Rotation.OneEighty, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.OneEighty, Rotation.Ninety), RotationTuple.of(Rotation.TwoSeventy, Rotation.OneEighty, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.OneEighty, Rotation.TwoSeventy), RotationTuple.of(Rotation.TwoSeventy, Rotation.TwoSeventy, Rotation.None), RotationTuple.of(Rotation.TwoSeventy, Rotation.TwoSeventy, Rotation.Ninety), RotationTuple.of(Rotation.TwoSeventy, Rotation.TwoSeventy, Rotation.OneEighty), RotationTuple.of(Rotation.TwoSeventy, Rotation.TwoSeventy, Rotation.TwoSeventy)}, Function.identity(), (pair, rotation) -> RotationTuple.of(pair.yaw(), pair.pitch().add((Rotation)rotation)), (pair, rotation) -> pair);

    public static final VariantRotation[] EMPTY_ARRAY;
    private final com.hypixel.hytale.protocol.VariantRotation protocolType;
    private final RotationTuple[] rotations;
    private final Function<RotationTuple, RotationTuple> verify;
    private final BiFunction<RotationTuple, Rotation, RotationTuple> rotateX;
    private final BiFunction<RotationTuple, Rotation, RotationTuple> rotateZ;

    @Nonnull
    private static Rotation validatePipe(@Nonnull Rotation yaw) {
        return switch (yaw) {
            default -> throw new MatchException(null, null);
            case Rotation.None, Rotation.Ninety -> yaw;
            case Rotation.OneEighty -> Rotation.None;
            case Rotation.TwoSeventy -> Rotation.Ninety;
        };
    }

    private VariantRotation(com.hypixel.hytale.protocol.VariantRotation protocolType, RotationTuple[] rotations, Function<RotationTuple, RotationTuple> verify, BiFunction<RotationTuple, Rotation, RotationTuple> rotateX, BiFunction<RotationTuple, Rotation, RotationTuple> rotateZ) {
        this.protocolType = protocolType;
        this.rotations = rotations;
        this.verify = verify;
        this.rotateX = rotateX;
        this.rotateZ = rotateZ;
    }

    public RotationTuple[] getRotations() {
        return this.rotations;
    }

    public RotationTuple rotateX(RotationTuple pair, Rotation rotation) {
        return this.rotateX.apply(pair, rotation);
    }

    public RotationTuple rotateZ(RotationTuple pair, Rotation rotation) {
        return this.rotateZ.apply(pair, rotation);
    }

    public RotationTuple verify(RotationTuple pair) {
        return this.verify.apply(pair);
    }

    @Override
    public com.hypixel.hytale.protocol.VariantRotation toPacket() {
        return this.protocolType;
    }

    static {
        EMPTY_ARRAY = new VariantRotation[0];
    }
}

