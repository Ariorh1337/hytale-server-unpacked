/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Transform {
    @Nonnull
    public static final BuilderCodec<Transform> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Transform.class, Transform::new).appendInherited(new KeyedCodec<Double>("X", Codec.DOUBLE), (o, i) -> {
        o.getPosition().x = i;
    }, o -> o.getPosition().x, (o, p) -> {
        o.getPosition().x = p.getPosition().x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Y", Codec.DOUBLE), (o, i) -> {
        o.getPosition().y = i;
    }, o -> o.getPosition().y, (o, p) -> {
        o.getPosition().y = p.getPosition().y;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Z", Codec.DOUBLE), (o, i) -> {
        o.getPosition().z = i;
    }, o -> o.getPosition().z, (o, p) -> {
        o.getPosition().z = p.getPosition().z;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Float>("Pitch", Codec.FLOAT), (o, i) -> {
        o.getRotation().x = i.floatValue();
    }, o -> Float.isNaN(o.getRotation().x) ? null : Float.valueOf(o.getRotation().x), (o, p) -> {
        o.getRotation().x = p.getRotation().x;
    }).add()).appendInherited(new KeyedCodec<Float>("Yaw", Codec.FLOAT), (o, i) -> {
        o.getRotation().y = i.floatValue();
    }, o -> Float.isNaN(o.getRotation().y) ? null : Float.valueOf(o.getRotation().y), (o, p) -> {
        o.getRotation().y = p.getRotation().y;
    }).add()).appendInherited(new KeyedCodec<Float>("Roll", Codec.FLOAT), (o, i) -> {
        o.getRotation().z = i.floatValue();
    }, o -> Float.isNaN(o.getRotation().z) ? null : Float.valueOf(o.getRotation().z), (o, p) -> {
        o.getRotation().z = p.getRotation().z;
    }).add()).build();
    @Nonnull
    public static final BuilderCodec<Transform> CODEC_DEGREES = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Transform.class, Transform::new).appendInherited(new KeyedCodec<Double>("X", Codec.DOUBLE), (o, i) -> {
        o.getPosition().x = i;
    }, o -> o.getPosition().x, (o, p) -> {
        o.getPosition().x = p.getPosition().x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Y", Codec.DOUBLE), (o, i) -> {
        o.getPosition().y = i;
    }, o -> o.getPosition().y, (o, p) -> {
        o.getPosition().y = p.getPosition().y;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Z", Codec.DOUBLE), (o, i) -> {
        o.getPosition().z = i;
    }, o -> o.getPosition().z, (o, p) -> {
        o.getPosition().z = p.getPosition().z;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Float>("Pitch", Codec.FLOAT), (o, i) -> {
        o.getRotation().x = (float)Math.toRadians(i.floatValue());
    }, o -> Float.isNaN(o.getRotation().x) ? null : Float.valueOf((float)Math.toDegrees(o.getRotation().x)), (o, p) -> {
        o.getRotation().x = p.getRotation().x;
    }).add()).appendInherited(new KeyedCodec<Float>("Yaw", Codec.FLOAT), (o, i) -> {
        o.getRotation().y = (float)Math.toRadians(i.floatValue());
    }, o -> Float.isNaN(o.getRotation().y) ? null : Float.valueOf((float)Math.toDegrees(o.getRotation().y)), (o, p) -> {
        o.getRotation().y = p.getRotation().y;
    }).add()).appendInherited(new KeyedCodec<Float>("Roll", Codec.FLOAT), (o, i) -> {
        o.getRotation().z = (float)Math.toRadians(i.floatValue());
    }, o -> Float.isNaN(o.getRotation().z) ? null : Float.valueOf((float)Math.toDegrees(o.getRotation().z)), (o, p) -> {
        o.getRotation().z = p.getRotation().z;
    }).add()).build();
    @Nonnull
    protected Vector3d position;
    @Nonnull
    protected Vector3f rotation;
    public static final int X_IS_RELATIVE = 1;
    public static final int Y_IS_RELATIVE = 2;
    public static final int Z_IS_RELATIVE = 4;
    public static final int YAW_IS_RELATIVE = 8;
    public static final int PITCH_IS_RELATIVE = 16;
    public static final int ROLL_IS_RELATIVE = 32;
    public static final int RELATIVE_TO_BLOCK = 64;

    public Transform() {
        this(new Vector3d(), new Vector3f(Float.NaN, Float.NaN, Float.NaN));
    }

    public Transform(@Nonnull Vector3i position) {
        this(new Vector3d(position), new Vector3f(Float.NaN, Float.NaN, Float.NaN));
    }

    public Transform(@Nonnull Vector3d position) {
        this(new Vector3d(position), new Vector3f(Float.NaN, Float.NaN, Float.NaN));
    }

    public Transform(double x, double y, double z) {
        this(new Vector3d(x, y, z), new Vector3f(Float.NaN, Float.NaN, Float.NaN));
    }

    public Transform(double x, double y, double z, float pitch, float yaw, float roll) {
        this(new Vector3d(x, y, z), new Vector3f(pitch, yaw, roll));
    }

    public Transform(@Nonnull Vector3d position, @Nonnull Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void assign(@Nonnull Transform transform) {
        this.position.assign(transform.getPosition());
        this.rotation.assign(transform.getRotation());
    }

    @Nonnull
    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(@Nonnull Vector3d position) {
        this.position = position;
    }

    @Nonnull
    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(@Nonnull Vector3f rotation) {
        this.rotation = rotation;
    }

    @Nonnull
    public Vector3d getDirection() {
        return Transform.getDirection(this.rotation.getPitch(), this.rotation.getYaw());
    }

    @Nonnull
    public static Vector3d getDirection(float pitch, float yaw) {
        if (Float.isNaN(pitch)) {
            throw new IllegalStateException("Pitch can't be NaN");
        }
        if (Float.isNaN(yaw)) {
            throw new IllegalStateException("Yaw can't be NaN");
        }
        double len = TrigMathUtil.cos(pitch);
        double x = len * (double)(-TrigMathUtil.sin(yaw));
        double y = TrigMathUtil.sin(pitch);
        double z = len * (double)(-TrigMathUtil.cos(yaw));
        return new Vector3d(x, y, z);
    }

    @Nonnull
    public Vector3i getAxisDirection() {
        return this.getAxisDirection(this.rotation.getPitch(), this.rotation.getYaw());
    }

    @Nonnull
    public Vector3i getAxisDirection(float pitch, float yaw) {
        if (Float.isNaN(pitch)) {
            throw new IllegalStateException("Pitch can't be NaN");
        }
        if (Float.isNaN(yaw)) {
            throw new IllegalStateException("Yaw can't be NaN");
        }
        float len = TrigMathUtil.cos(pitch);
        float x = len * -TrigMathUtil.sin(yaw);
        float y = TrigMathUtil.sin(pitch);
        float z = len * -TrigMathUtil.cos(yaw);
        return new Vector3i(MathUtil.fastRound(x), MathUtil.fastRound(y), MathUtil.fastRound(z));
    }

    @Nonnull
    public Axis getAxis() {
        Vector3i axisDirection = this.getAxisDirection();
        if (axisDirection.getX() != 0) {
            return Axis.X;
        }
        if (axisDirection.getY() != 0) {
            return Axis.Y;
        }
        return Axis.Z;
    }

    @Nonnull
    public Transform clone() {
        return new Transform(this.position.clone(), this.rotation.clone());
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Transform transform = (Transform)o;
        if (!Objects.equals(this.position, transform.position)) {
            return false;
        }
        return Objects.equals(this.rotation, transform.rotation);
    }

    public int hashCode() {
        int result = this.position.hashCode();
        result = 31 * result + this.rotation.hashCode();
        return result;
    }

    @Nonnull
    public String toString() {
        return "Transform{position=" + String.valueOf(this.position) + ", rotation=" + String.valueOf(this.rotation) + "}";
    }

    public static void applyMaskedRelativeTransform(@Nonnull Transform transform, byte relativeMask, @Nonnull Vector3d sourcePosition, @Nonnull Vector3f sourceRotation, @Nonnull Vector3i blockPosition) {
        if (relativeMask == 0) {
            return;
        }
        if ((relativeMask & 0x40) != 0) {
            if ((relativeMask & 1) != 0) {
                transform.getPosition().setX(transform.getPosition().getX() + (double)blockPosition.getX());
            }
            if ((relativeMask & 2) != 0) {
                transform.getPosition().setY(transform.getPosition().getY() + (double)blockPosition.getY());
            }
            if ((relativeMask & 4) != 0) {
                transform.getPosition().setZ(transform.getPosition().getZ() + (double)blockPosition.getZ());
            }
        } else {
            if ((relativeMask & 1) != 0) {
                transform.getPosition().setX(transform.getPosition().getX() + sourcePosition.getX());
            }
            if ((relativeMask & 2) != 0) {
                transform.getPosition().setY(transform.getPosition().getY() + sourcePosition.getY());
            }
            if ((relativeMask & 4) != 0) {
                transform.getPosition().setZ(transform.getPosition().getZ() + sourcePosition.getZ());
            }
        }
        if ((relativeMask & 8) != 0) {
            transform.getRotation().setYaw(transform.getRotation().getYaw() + sourceRotation.getYaw());
        }
        if ((relativeMask & 0x10) != 0) {
            transform.getRotation().setPitch(transform.getRotation().getPitch() + sourceRotation.getPitch());
        }
        if ((relativeMask & 0x20) != 0) {
            transform.getRotation().setRoll(transform.getRotation().getRoll() + sourceRotation.getRoll());
        }
    }
}

