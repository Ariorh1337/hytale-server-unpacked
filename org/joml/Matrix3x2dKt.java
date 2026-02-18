/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2d;
import org.joml.Matrix3x2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\u0005\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\u0005\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\u0005\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\u0005\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\b\u001a\u00020\u0001*\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\u000b*\u00020\t2\u0006\u0010\u0003\u001a\u00020\fH\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\u00012\u0006\u0010\n\u001a\u00020\tH\u0086\u0002\u00a8\u0006\u000f"}, d2={"getMatrix3x2d", "Lorg/joml/Matrix3x2d;", "Ljava/nio/ByteBuffer;", "v", "Ljava/nio/DoubleBuffer;", "putMatrix3x2d", "index", "", "times", "Lorg/joml/Matrix3x2dc;", "m", "Lorg/joml/Vector3d;", "Lorg/joml/Vector3dc;", "timesAssign", "", "joml"})
public final class Matrix3x2dKt {
    @NotNull
    public static final Matrix3x2d times(@NotNull Matrix3x2dc $this$times, @NotNull Matrix3x2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix3x2d matrix3x2d = $this$times.mul(m, new Matrix3x2d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2d, (String)"mul(m, Matrix3x2d())");
        return matrix3x2d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Matrix3x2dc $this$times, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$times.transform(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"transform(v, Vector3d())");
        return vector3d;
    }

    public static final void timesAssign(@NotNull Matrix3x2d $this$timesAssign, @NotNull Matrix3x2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Matrix3x2d getMatrix3x2d(@NotNull ByteBuffer $this$getMatrix3x2d, @NotNull Matrix3x2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3x2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix3x2d matrix3x2d = v.set($this$getMatrix3x2d);
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2d, (String)"v.set(this)");
        return matrix3x2d;
    }

    @NotNull
    public static final ByteBuffer putMatrix3x2d(@NotNull ByteBuffer $this$putMatrix3x2d, @NotNull Matrix3x2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putMatrix3x2d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putMatrix3x2d(@NotNull ByteBuffer $this$putMatrix3x2d, int index, @NotNull Matrix3x2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putMatrix3x2d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Matrix3x2d getMatrix3x2d(@NotNull DoubleBuffer $this$getMatrix3x2d) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3x2d, (String)"<this>");
        return new Matrix3x2d($this$getMatrix3x2d);
    }

    @NotNull
    public static final Matrix3x2d getMatrix3x2d(@NotNull DoubleBuffer $this$getMatrix3x2d, @NotNull Matrix3x2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3x2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix3x2d matrix3x2d = v.set($this$getMatrix3x2d);
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2d, (String)"v.set(this)");
        return matrix3x2d;
    }

    @NotNull
    public static final DoubleBuffer putMatrix3x2d(@NotNull DoubleBuffer $this$putMatrix3x2d, @NotNull Matrix3x2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get($this$putMatrix3x2d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(this)");
        return doubleBuffer;
    }

    @NotNull
    public static final DoubleBuffer putMatrix3x2d(@NotNull DoubleBuffer $this$putMatrix3x2d, int index, @NotNull Matrix3x2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get(index, $this$putMatrix3x2d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(index, this)");
        return doubleBuffer;
    }
}

