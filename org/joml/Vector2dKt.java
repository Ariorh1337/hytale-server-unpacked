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
import org.joml.Matrix2dc;
import org.joml.Matrix2fc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000B\n\u0000\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\r\u0010\u0004\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0005\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0006\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0006\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0004\u001a\u0015\u0010\b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0004\u001a\u0015\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0002\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\n\u0010\u000e\u001a\u00020\n*\u00020\u000f\u001a\u0012\u0010\u000e\u001a\u00020\n*\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011\u001a\u001a\u0010\u000e\u001a\u00020\n*\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0003\u001a\u00020\n\u001a\u0012\u0010\u000e\u001a\u00020\n*\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\n\u001a\n\u0010\u000e\u001a\u00020\n*\u00020\u0012\u001a\u0012\u0010\u000e\u001a\u00020\n*\u00020\u00122\u0006\u0010\u0010\u001a\u00020\u0011\u001a\u001a\u0010\u000e\u001a\u00020\n*\u00020\u00122\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0003\u001a\u00020\n\u001a\u0012\u0010\u000e\u001a\u00020\n*\u00020\u00122\u0006\u0010\u0003\u001a\u00020\n\u001a\u0015\u0010\u0013\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0013\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0002\u001a\u0015\u0010\u0014\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0014\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0002\u001a\u0015\u0010\u0015\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0015\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0002\u001a\u0015\u0010\u0016\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0016\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0007H\u0086\u0002\u001a\u001a\u0010\u0017\u001a\u00020\u000f*\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0003\u001a\u00020\n\u001a\u0012\u0010\u0017\u001a\u00020\u000f*\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\n\u001a\u001a\u0010\u0017\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0003\u001a\u00020\n\u001a\u0012\u0010\u0017\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0003\u001a\u00020\n\u001a\u0015\u0010\u0018\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\n*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u001bH\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\n*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u001cH\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\f*\u00020\n2\u0006\u0010\u0019\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\f*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u001bH\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\f*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u001cH\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\f*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\r\u0010\u001e\u001a\u00020\n*\u00020\u0002H\u0086\u0002\u00a8\u0006\u001f"}, d2={"angle", "", "Lorg/joml/Vector2dc;", "v", "component1", "component2", "distance", "Lorg/joml/Vector2fc;", "distanceSquared", "div", "Lorg/joml/Vector2d;", "divAssign", "", "dot", "getVector2d", "Ljava/nio/ByteBuffer;", "index", "", "Ljava/nio/DoubleBuffer;", "minus", "minusAssign", "plus", "plusAssign", "putVector2d", "times", "s", "m", "Lorg/joml/Matrix2dc;", "Lorg/joml/Matrix2fc;", "timesAssign", "unaryMinus", "joml"})
public final class Vector2dKt {
    public static final double component1(@NotNull Vector2dc $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final double component2(@NotNull Vector2dc $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    @NotNull
    public static final Vector2d plus(@NotNull Vector2dc $this$plus, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$plus.add(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"add(v, Vector2d())");
        return vector2d;
    }

    @NotNull
    public static final Vector2d plus(@NotNull Vector2dc $this$plus, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$plus.add(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"add(v, Vector2d())");
        return vector2d;
    }

    public static final void plusAssign(@NotNull Vector2d $this$plusAssign, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    public static final void plusAssign(@NotNull Vector2d $this$plusAssign, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector2d minus(@NotNull Vector2dc $this$minus, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$minus.sub(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"sub(v, Vector2d())");
        return vector2d;
    }

    @NotNull
    public static final Vector2d minus(@NotNull Vector2dc $this$minus, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$minus.sub(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"sub(v, Vector2d())");
        return vector2d;
    }

    public static final void minusAssign(@NotNull Vector2d $this$minusAssign, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    public static final void minusAssign(@NotNull Vector2d $this$minusAssign, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector2d times(@NotNull Vector2dc $this$times, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$times.mul(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"mul(v, Vector2d())");
        return vector2d;
    }

    @NotNull
    public static final Vector2d times(@NotNull Vector2dc $this$times, double s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector2d vector2d = $this$times.mul(s, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"mul(s, Vector2d())");
        return vector2d;
    }

    @NotNull
    public static final Vector2d times(@NotNull Vector2dc $this$times, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector2d vector2d = $this$times.mul(m, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"mul(m, Vector2d())");
        return vector2d;
    }

    @NotNull
    public static final Vector2d times(@NotNull Vector2dc $this$times, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector2d vector2d = $this$times.mul(m, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"mul(m, Vector2d())");
        return vector2d;
    }

    public static final void timesAssign(@NotNull Vector2d $this$timesAssign, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector2d $this$timesAssign, double s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    public static final void timesAssign(@NotNull Vector2d $this$timesAssign, @NotNull Matrix2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector2d $this$timesAssign, @NotNull Matrix2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Vector2d div(@NotNull Vector2dc $this$div, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$div.div(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"div(v, Vector2d())");
        return vector2d;
    }

    @NotNull
    public static final Vector2d div(@NotNull Vector2dc $this$div, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = $this$div.div(v, new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"div(v, Vector2d())");
        return vector2d;
    }

    public static final void divAssign(@NotNull Vector2d $this$divAssign, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    public static final void divAssign(@NotNull Vector2d $this$divAssign, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    @NotNull
    public static final Vector2d unaryMinus(@NotNull Vector2dc $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector2d vector2d = $this$unaryMinus.negate(new Vector2d());
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"negate(Vector2d())");
        return vector2d;
    }

    public static final double dot(@NotNull Vector2dc $this$dot, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$dot, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$dot.dot(v);
    }

    public static final double angle(@NotNull Vector2dc $this$angle, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angle, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angle.angle(v);
    }

    public static final double distance(@NotNull Vector2dc $this$distance, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final double distance(@NotNull Vector2dc $this$distance, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final double distanceSquared(@NotNull Vector2dc $this$distanceSquared, @NotNull Vector2dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final double distanceSquared(@NotNull Vector2dc $this$distanceSquared, @NotNull Vector2fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull ByteBuffer $this$getVector2d) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        return new Vector2d($this$getVector2d);
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull ByteBuffer $this$getVector2d, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        return new Vector2d(index, $this$getVector2d);
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull ByteBuffer $this$getVector2d, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = v.set($this$getVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"v.set(this)");
        return vector2d;
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull ByteBuffer $this$getVector2d, int index, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = v.set(index, $this$getVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"v.set(index, this)");
        return vector2d;
    }

    @NotNull
    public static final ByteBuffer putVector2d(@NotNull ByteBuffer $this$putVector2d, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector2d(@NotNull ByteBuffer $this$putVector2d, int index, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull DoubleBuffer $this$getVector2d) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        return new Vector2d($this$getVector2d);
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull DoubleBuffer $this$getVector2d, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        return new Vector2d(index, $this$getVector2d);
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull DoubleBuffer $this$getVector2d, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = v.set($this$getVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"v.set(this)");
        return vector2d;
    }

    @NotNull
    public static final Vector2d getVector2d(@NotNull DoubleBuffer $this$getVector2d, int index, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2d vector2d = v.set(index, $this$getVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)vector2d, (String)"v.set(index, this)");
        return vector2d;
    }

    @NotNull
    public static final DoubleBuffer putVector2d(@NotNull DoubleBuffer $this$putVector2d, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get($this$putVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(this)");
        return doubleBuffer;
    }

    @NotNull
    public static final DoubleBuffer putVector2d(@NotNull DoubleBuffer $this$putVector2d, int index, @NotNull Vector2d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get(index, $this$putVector2d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(index, this)");
        return doubleBuffer;
    }
}

