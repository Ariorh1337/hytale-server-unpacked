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
import org.joml.Matrix2f;
import org.joml.Matrix2fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u00004\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u001a\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0011*\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0012H\u0086\u0002\u001a\u0015\u0010\u0013\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u00a8\u0006\u0014"}, d2={"getMatrix2f", "Lorg/joml/Matrix2f;", "Ljava/nio/ByteBuffer;", "v", "Ljava/nio/FloatBuffer;", "minus", "Lorg/joml/Matrix2fc;", "m", "minusAssign", "", "mulComponentWise", "plus", "plusAssign", "putMatrix2f", "index", "", "times", "Lorg/joml/Vector2f;", "Lorg/joml/Vector2fc;", "timesAssign", "joml"})
public final class Matrix2fKt {
    @NotNull
    public static final Matrix2f plus(@NotNull Matrix2fc $this$plus, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2f matrix2f = $this$plus.add(m, new Matrix2f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2f, (String)"add(m, Matrix2f())");
        return matrix2f;
    }

    public static final void plusAssign(@NotNull Matrix2f $this$plusAssign, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$plusAssign.add(m);
    }

    @NotNull
    public static final Matrix2f minus(@NotNull Matrix2fc $this$minus, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2f matrix2f = $this$minus.sub(m, new Matrix2f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2f, (String)"sub(m, Matrix2f())");
        return matrix2f;
    }

    public static final void minusAssign(@NotNull Matrix2f $this$minusAssign, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$minusAssign.sub(m);
    }

    @NotNull
    public static final Matrix2f times(@NotNull Matrix2fc $this$times, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2f matrix2f = $this$times.mul(m, new Matrix2f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2f, (String)"mul(m, Matrix2f())");
        return matrix2f;
    }

    @NotNull
    public static final Vector2f times(@NotNull Matrix2fc $this$times, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2f vector2f = $this$times.transform(v, new Vector2f());
        Intrinsics.checkNotNullExpressionValue((Object)vector2f, (String)"transform(v, Vector2f())");
        return vector2f;
    }

    public static final void timesAssign(@NotNull Matrix2f $this$timesAssign, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Matrix2f mulComponentWise(@NotNull Matrix2fc $this$mulComponentWise, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$mulComponentWise, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2f matrix2f = $this$mulComponentWise.mulComponentWise(m, new Matrix2f());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2f, (String)"mulComponentWise(m, Matrix2f())");
        return matrix2f;
    }

    @NotNull
    public static final Matrix2f getMatrix2f(@NotNull ByteBuffer $this$getMatrix2f, @NotNull Matrix2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix2f matrix2f = v.set($this$getMatrix2f);
        Intrinsics.checkNotNullExpressionValue((Object)matrix2f, (String)"v.set(this)");
        return matrix2f;
    }

    @NotNull
    public static final ByteBuffer putMatrix2f(@NotNull ByteBuffer $this$putMatrix2f, @NotNull Matrix2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putMatrix2f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putMatrix2f(@NotNull ByteBuffer $this$putMatrix2f, int index, @NotNull Matrix2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putMatrix2f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Matrix2f getMatrix2f(@NotNull FloatBuffer $this$getMatrix2f) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix2f, (String)"<this>");
        return new Matrix2f($this$getMatrix2f);
    }

    @NotNull
    public static final Matrix2f getMatrix2f(@NotNull FloatBuffer $this$getMatrix2f, @NotNull Matrix2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix2f matrix2f = v.set($this$getMatrix2f);
        Intrinsics.checkNotNullExpressionValue((Object)matrix2f, (String)"v.set(this)");
        return matrix2f;
    }

    @NotNull
    public static final FloatBuffer putMatrix2f(@NotNull FloatBuffer $this$putMatrix2f, @NotNull Matrix2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get($this$putMatrix2f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(this)");
        return floatBuffer;
    }

    @NotNull
    public static final FloatBuffer putMatrix2f(@NotNull FloatBuffer $this$putMatrix2f, int index, @NotNull Matrix2f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get(index, $this$putMatrix2f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(index, this)");
        return floatBuffer;
    }
}

