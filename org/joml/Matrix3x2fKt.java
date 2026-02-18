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
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\u0005\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\u0005\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\u0005\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\u0005\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\b\u001a\u00020\u0001*\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\u000b*\u00020\t2\u0006\u0010\u0003\u001a\u00020\fH\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\u00012\u0006\u0010\n\u001a\u00020\tH\u0086\u0002\u00a8\u0006\u000f"}, d2={"getMatrix3x2f", "Lorg/joml/Matrix3x2f;", "Ljava/nio/ByteBuffer;", "v", "Ljava/nio/FloatBuffer;", "putMatrix3x2f", "index", "", "times", "Lorg/joml/Matrix3x2fc;", "m", "Lorg/joml/Vector3f;", "Lorg/joml/Vector3fc;", "timesAssign", "", "joml"})
public final class Matrix3x2fKt {
    @NotNull
    public static final Matrix3x2f times(@NotNull Matrix3x2fc $this$times, @NotNull Matrix3x2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix3x2f matrix3x2f = $this$times.mul(m, new Matrix3x2f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2f, (String)"mul(m, Matrix3x2f())");
        return matrix3x2f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Matrix3x2fc $this$times, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$times.transform(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"transform(v, Vector3f())");
        return vector3f;
    }

    public static final void timesAssign(@NotNull Matrix3x2f $this$timesAssign, @NotNull Matrix3x2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Matrix3x2f getMatrix3x2f(@NotNull ByteBuffer $this$getMatrix3x2f, @NotNull Matrix3x2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3x2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix3x2f matrix3x2f = v.set($this$getMatrix3x2f);
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2f, (String)"v.set(this)");
        return matrix3x2f;
    }

    @NotNull
    public static final ByteBuffer putMatrix3x2f(@NotNull ByteBuffer $this$putMatrix3x2f, @NotNull Matrix3x2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putMatrix3x2f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putMatrix3x2f(@NotNull ByteBuffer $this$putMatrix3x2f, int index, @NotNull Matrix3x2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putMatrix3x2f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Matrix3x2f getMatrix3x2f(@NotNull FloatBuffer $this$getMatrix3x2f) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3x2f, (String)"<this>");
        return new Matrix3x2f($this$getMatrix3x2f);
    }

    @NotNull
    public static final Matrix3x2f getMatrix3x2f(@NotNull FloatBuffer $this$getMatrix3x2f, @NotNull Matrix3x2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix3x2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix3x2f matrix3x2f = v.set($this$getMatrix3x2f);
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2f, (String)"v.set(this)");
        return matrix3x2f;
    }

    @NotNull
    public static final FloatBuffer putMatrix3x2f(@NotNull FloatBuffer $this$putMatrix3x2f, @NotNull Matrix3x2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get($this$putMatrix3x2f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(this)");
        return floatBuffer;
    }

    @NotNull
    public static final FloatBuffer putMatrix3x2f(@NotNull FloatBuffer $this$putMatrix3x2f, int index, @NotNull Matrix3x2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix3x2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get(index, $this$putMatrix3x2f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(index, this)");
        return floatBuffer;
    }
}

