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
import java.nio.FloatBuffer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000@\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u001a\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0014H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0015*\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0016H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0012H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u0014H\u0086\u0002\u00a8\u0006\u0018"}, d2={"getMatrix3f", "Lorg/joml/Matrix3f;", "Ljava/nio/ByteBuffer;", "v", "Ljava/nio/FloatBuffer;", "minus", "Lorg/joml/Matrix3fc;", "m", "minusAssign", "", "mulComponentWise", "plus", "plusAssign", "putMatrix3f", "index", "", "times", "a", "Lorg/joml/AxisAngle4f;", "q", "Lorg/joml/Quaternionfc;", "Lorg/joml/Vector3f;", "Lorg/joml/Vector3fc;", "timesAssign", "joml"})
public final class Matrix3fKt {
    @NotNull
    public static final Matrix3f plus(@NotNull Matrix3fc $this$plus, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix3f matrix3f = $this$plus.add(m, new Matrix3f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"add(m, Matrix3f())");
        return matrix3f;
    }

    public static final void plusAssign(@NotNull Matrix3f $this$plusAssign, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$plusAssign.add(m);
    }

    @NotNull
    public static final Matrix3f minus(@NotNull Matrix3fc $this$minus, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix3f matrix3f = $this$minus.sub(m, new Matrix3f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"sub(m, Matrix3f())");
        return matrix3f;
    }

    public static final void minusAssign(@NotNull Matrix3f $this$minusAssign, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$minusAssign.sub(m);
    }

    @NotNull
    public static final Matrix3f times(@NotNull Matrix3fc $this$times, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix3f matrix3f = $this$times.mul(m, new Matrix3f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"mul(m, Matrix3f())");
        return matrix3f;
    }

    @NotNull
    public static final Matrix3f times(@NotNull Matrix3fc $this$times, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Matrix3f matrix3f = $this$times.rotate(q, new Matrix3f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"rotate(q, Matrix3f())");
        return matrix3f;
    }

    @NotNull
    public static final Matrix3f times(@NotNull Matrix3fc $this$times, @NotNull AxisAngle4f a) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        Matrix3f matrix3f = $this$times.rotate(a, new Matrix3f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"rotate(a, Matrix3f())");
        return matrix3f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Matrix3fc $this$times, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$times.transform(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"transform(v, Vector3f())");
        return vector3f;
    }

    public static final void timesAssign(@NotNull Matrix3f $this$timesAssign, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Matrix3f $this$timesAssign, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$timesAssign.rotate(q);
    }

    public static final void timesAssign(@NotNull Matrix3f $this$timesAssign, @NotNull AxisAngle4f a) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        $this$timesAssign.rotate(a);
    }

    @NotNull
    public static final Matrix3f mulComponentWise(@NotNull Matrix3fc $this$mulComponentWise, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$mulComponentWise, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix3f matrix3f = $this$mulComponentWise.mulComponentWise(m, new Matrix3f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"mulComponentWise(m, Matrix3f())");
        return matrix3f;
    }

    @NotNull
    public static final Matrix3f getMatrix3f(@NotNull ByteBuffer $this$getMatrix3f, @NotNull Matrix3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix3f matrix3f = v.set($this$getMatrix3f);
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"v.set(this)");
        return matrix3f;
    }

    @NotNull
    public static final ByteBuffer putMatrix3f(@NotNull ByteBuffer $this$putMatrix3f, @NotNull Matrix3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putMatrix3f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putMatrix3f(@NotNull ByteBuffer $this$putMatrix3f, int index, @NotNull Matrix3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putMatrix3f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Matrix3f getMatrix3f(@NotNull FloatBuffer $this$getMatrix3f) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3f, (String)"<this>");
        return new Matrix3f($this$getMatrix3f);
    }

    @NotNull
    public static final Matrix3f getMatrix3f(@NotNull FloatBuffer $this$getMatrix3f, @NotNull Matrix3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix3f matrix3f = v.set($this$getMatrix3f);
        Intrinsics.checkNotNullExpressionValue((Object)matrix3f, (String)"v.set(this)");
        return matrix3f;
    }

    @NotNull
    public static final FloatBuffer putMatrix3f(@NotNull FloatBuffer $this$putMatrix3f, @NotNull Matrix3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get($this$putMatrix3f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(this)");
        return floatBuffer;
    }

    @NotNull
    public static final FloatBuffer putMatrix3f(@NotNull FloatBuffer $this$putMatrix3f, int index, @NotNull Matrix3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get(index, $this$putMatrix3f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(index, this)");
        return floatBuffer;
    }
}

