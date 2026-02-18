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
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector4d;
import org.joml.Vector4dc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000L\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u001a\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0013H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0014H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u0016H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u0017H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0018*\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0019H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0012H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0013H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0014H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0016H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0017H\u0086\u0002\u00a8\u0006\u001b"}, d2={"getMatrix4x3d", "Lorg/joml/Matrix4x3d;", "Ljava/nio/ByteBuffer;", "v", "Ljava/nio/DoubleBuffer;", "minus", "Lorg/joml/Matrix4x3dc;", "m", "minusAssign", "", "mulComponentWise", "plus", "plusAssign", "putMatrix4x3d", "index", "", "times", "a", "Lorg/joml/AxisAngle4d;", "Lorg/joml/AxisAngle4f;", "Lorg/joml/Matrix4x3fc;", "q", "Lorg/joml/Quaterniondc;", "Lorg/joml/Quaternionfc;", "Lorg/joml/Vector4d;", "Lorg/joml/Vector4dc;", "timesAssign", "joml"})
public final class Matrix4x3dKt {
    @NotNull
    public static final Matrix4x3d plus(@NotNull Matrix4x3dc $this$plus, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix4x3d matrix4x3d = $this$plus.add(m, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"add(m, Matrix4x3d())");
        return matrix4x3d;
    }

    public static final void plusAssign(@NotNull Matrix4x3d $this$plusAssign, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$plusAssign.add(m);
    }

    @NotNull
    public static final Matrix4x3d minus(@NotNull Matrix4x3dc $this$minus, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix4x3d matrix4x3d = $this$minus.sub(m, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"sub(m, Matrix4x3d())");
        return matrix4x3d;
    }

    public static final void minusAssign(@NotNull Matrix4x3d $this$minusAssign, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$minusAssign.sub(m);
    }

    @NotNull
    public static final Matrix4x3d times(@NotNull Matrix4x3dc $this$times, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix4x3d matrix4x3d = $this$times.mul(m, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"mul(m, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Matrix4x3d times(@NotNull Matrix4x3dc $this$times, @NotNull Matrix4x3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix4x3d matrix4x3d = $this$times.mul(m, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"mul(m, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Matrix4x3d times(@NotNull Matrix4x3dc $this$times, @NotNull Quaterniondc q) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Matrix4x3d matrix4x3d = $this$times.rotate(q, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"rotate(q, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Matrix4x3d times(@NotNull Matrix4x3dc $this$times, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Matrix4x3d matrix4x3d = $this$times.rotate(q, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"rotate(q, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Matrix4x3d times(@NotNull Matrix4x3dc $this$times, @NotNull AxisAngle4d a) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        Matrix4x3d matrix4x3d = $this$times.rotate(a, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"rotate(a, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Matrix4x3d times(@NotNull Matrix4x3dc $this$times, @NotNull AxisAngle4f a) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        Matrix4x3d matrix4x3d = $this$times.rotate(a, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"rotate(a, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Vector4d times(@NotNull Matrix4x3dc $this$times, @NotNull Vector4dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4d vector4d = $this$times.transform(v, new Vector4d());
        Intrinsics.checkNotNullExpressionValue((Object)vector4d, (String)"transform(v, Vector4d())");
        return vector4d;
    }

    public static final void timesAssign(@NotNull Matrix4x3d $this$timesAssign, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Matrix4x3d $this$timesAssign, @NotNull Matrix4x3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Matrix4x3d $this$timesAssign, @NotNull Quaterniondc q) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$timesAssign.rotate(q);
    }

    public static final void timesAssign(@NotNull Matrix4x3d $this$timesAssign, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$timesAssign.rotate(q);
    }

    public static final void timesAssign(@NotNull Matrix4x3d $this$timesAssign, @NotNull AxisAngle4d a) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        $this$timesAssign.rotate(a);
    }

    public static final void timesAssign(@NotNull Matrix4x3d $this$timesAssign, @NotNull AxisAngle4f a) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        $this$timesAssign.rotate(a);
    }

    @NotNull
    public static final Matrix4x3d mulComponentWise(@NotNull Matrix4x3dc $this$mulComponentWise, @NotNull Matrix4x3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$mulComponentWise, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix4x3d matrix4x3d = $this$mulComponentWise.mulComponentWise(m, new Matrix4x3d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"mulComponentWise(m, Matrix4x3d())");
        return matrix4x3d;
    }

    @NotNull
    public static final Matrix4x3d getMatrix4x3d(@NotNull ByteBuffer $this$getMatrix4x3d, @NotNull Matrix4x3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix4x3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix4x3d matrix4x3d = v.set($this$getMatrix4x3d);
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"v.set(this)");
        return matrix4x3d;
    }

    @NotNull
    public static final ByteBuffer putMatrix4x3d(@NotNull ByteBuffer $this$putMatrix4x3d, @NotNull Matrix4x3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix4x3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putMatrix4x3d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putMatrix4x3d(@NotNull ByteBuffer $this$putMatrix4x3d, int index, @NotNull Matrix4x3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix4x3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putMatrix4x3d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Matrix4x3d getMatrix4x3d(@NotNull DoubleBuffer $this$getMatrix4x3d) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix4x3d, (String)"<this>");
        return new Matrix4x3d($this$getMatrix4x3d);
    }

    @NotNull
    public static final Matrix4x3d getMatrix4x3d(@NotNull DoubleBuffer $this$getMatrix4x3d, @NotNull Matrix4x3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix4x3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix4x3d matrix4x3d = v.set($this$getMatrix4x3d);
        Intrinsics.checkNotNullExpressionValue((Object)matrix4x3d, (String)"v.set(this)");
        return matrix4x3d;
    }

    @NotNull
    public static final DoubleBuffer putMatrix4x3d(@NotNull DoubleBuffer $this$putMatrix4x3d, @NotNull Matrix4x3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix4x3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get($this$putMatrix4x3d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(this)");
        return doubleBuffer;
    }

    @NotNull
    public static final DoubleBuffer putMatrix4x3d(@NotNull DoubleBuffer $this$putMatrix4x3d, int index, @NotNull Matrix4x3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix4x3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get(index, $this$putMatrix4x3d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(index, this)");
        return doubleBuffer;
    }
}

