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
import org.joml.Matrix2d;
import org.joml.Matrix2dc;
import org.joml.Matrix2fc;
import org.joml.Vector2d;
import org.joml.Vector2dc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u00008\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u001a\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u001a\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0012\u0010\r\u001a\u00020\u0004*\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0001\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0011H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0012*\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0013H\u0086\u0002\u001a\u0015\u0010\u0014\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0014\u001a\u00020\t*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0011H\u0086\u0002\u00a8\u0006\u0015"}, d2={"getMatrix2d", "Lorg/joml/Matrix2d;", "Ljava/nio/ByteBuffer;", "v", "Ljava/nio/DoubleBuffer;", "minus", "Lorg/joml/Matrix2dc;", "m", "minusAssign", "", "mulComponentWise", "plus", "plusAssign", "putMatrix2d", "index", "", "times", "Lorg/joml/Matrix2fc;", "Lorg/joml/Vector2d;", "Lorg/joml/Vector2dc;", "timesAssign", "joml"})
public final class Matrix2dKt {
    @NotNull
    public static final Matrix2d plus(@NotNull Matrix2dc $this$plus, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2d matrix2d = $this$plus.add(m, new Matrix2d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"add(m, Matrix2d())");
        return matrix2d;
    }

    public static final void plusAssign(@NotNull Matrix2d $this$plusAssign, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$plusAssign.add(m);
    }

    @NotNull
    public static final Matrix2d minus(@NotNull Matrix2dc $this$minus, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2d matrix2d = $this$minus.sub(m, new Matrix2d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"sub(m, Matrix2d())");
        return matrix2d;
    }

    public static final void minusAssign(@NotNull Matrix2d $this$minusAssign, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$minusAssign.sub(m);
    }

    @NotNull
    public static final Matrix2d times(@NotNull Matrix2dc $this$times, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2d matrix2d = $this$times.mul(m, new Matrix2d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"mul(m, Matrix2d())");
        return matrix2d;
    }

    @NotNull
    public static final Matrix2d times(@NotNull Matrix2dc $this$times, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2d matrix2d = $this$times.mul(m, new Matrix2d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"mul(m, Matrix2d())");
        return matrix2d;
    }

    @NotNull
    public static final Vector2d times(@NotNull Matrix2dc $this$times, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$times.transform(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"transform(v, Vector2d())");
        return vector2d;
    }

    public static final void timesAssign(@NotNull Matrix2d $this$timesAssign, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Matrix2d $this$timesAssign, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Matrix2d mulComponentWise(@NotNull Matrix2dc $this$mulComponentWise, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$mulComponentWise, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Matrix2d matrix2d = $this$mulComponentWise.mulComponentWise(m, new Matrix2d());
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"mulComponentWise(m, Matrix2d())");
        return matrix2d;
    }

    @NotNull
    public static final Matrix2d getMatrix2d(@NotNull ByteBuffer $this$getMatrix2d, @NotNull Matrix2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix2d matrix2d = v.set($this$getMatrix2d);
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"v.set(this)");
        return matrix2d;
    }

    @NotNull
    public static final ByteBuffer putMatrix2d(@NotNull ByteBuffer $this$putMatrix2d, @NotNull Matrix2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putMatrix2d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putMatrix2d(@NotNull ByteBuffer $this$putMatrix2d, int index, @NotNull Matrix2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putMatrix2d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Matrix2d getMatrix2d(@NotNull DoubleBuffer $this$getMatrix2d) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix2d, (String)"<this>");
        return new Matrix2d($this$getMatrix2d);
    }

    @NotNull
    public static final Matrix2d getMatrix2d(@NotNull DoubleBuffer $this$getMatrix2d, @NotNull Matrix2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getMatrix2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Matrix2d matrix2d = v.set($this$getMatrix2d);
        Intrinsics.checkNotNullExpressionValue((Object)matrix2d, (String)"v.set(this)");
        return matrix2d;
    }

    @NotNull
    public static final DoubleBuffer putMatrix2d(@NotNull DoubleBuffer $this$putMatrix2d, @NotNull Matrix2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get($this$putMatrix2d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(this)");
        return doubleBuffer;
    }

    @NotNull
    public static final DoubleBuffer putMatrix2d(@NotNull DoubleBuffer $this$putMatrix2d, int index, @NotNull Matrix2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putMatrix2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get(index, $this$putMatrix2d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(index, this)");
        return doubleBuffer;
    }
}

