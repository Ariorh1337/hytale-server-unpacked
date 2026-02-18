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
import java.nio.IntBuffer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000<\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0003\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0004\u001a\u00020\u0005*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0007\u001a\u00020\b*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\fH\u0086\u0002\u001a\u0015\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0001H\u0086\u0002\u001a\n\u0010\u000f\u001a\u00020\n*\u00020\u0010\u001a\u0012\u0010\u000f\u001a\u00020\n*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0001\u001a\u001a\u0010\u000f\u001a\u00020\n*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\n\u001a\u0012\u0010\u000f\u001a\u00020\n*\u00020\u00102\u0006\u0010\u0006\u001a\u00020\n\u001a\n\u0010\u000f\u001a\u00020\n*\u00020\u0012\u001a\u0012\u0010\u000f\u001a\u00020\n*\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0001\u001a\u001a\u0010\u000f\u001a\u00020\n*\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\n\u001a\u0012\u0010\u000f\u001a\u00020\n*\u00020\u00122\u0006\u0010\u0006\u001a\u00020\n\u001a\u0015\u0010\u0013\u001a\u00020\b*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0014\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0015\u001a\u00020\u000e*\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0016\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\u000e*\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0002\u001a\u001a\u0010\u0018\u001a\u00020\u0010*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\n\u001a\u0012\u0010\u0018\u001a\u00020\u0010*\u00020\u00102\u0006\u0010\u0006\u001a\u00020\n\u001a\u001a\u0010\u0018\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\n\u001a\u0012\u0010\u0018\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0006\u001a\u00020\n\u001a\u0015\u0010\u0019\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u0019\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\u000e*\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\u000e*\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0002H\u0086\u0002\u001a\r\u0010\u001b\u001a\u00020\n*\u00020\u0002H\u0086\u0002\u00a8\u0006\u001c"}, d2={"component1", "", "Lorg/joml/Vector2ic;", "component2", "distance", "", "v", "distanceSquared", "", "div", "Lorg/joml/Vector2i;", "s", "", "divAssign", "", "getVector2i", "Ljava/nio/ByteBuffer;", "index", "Ljava/nio/IntBuffer;", "gridDistance", "minus", "minusAssign", "plus", "plusAssign", "putVector2i", "times", "timesAssign", "unaryMinus", "joml"})
public final class Vector2iKt {
    public static final int component1(@NotNull Vector2ic $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final int component2(@NotNull Vector2ic $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    @NotNull
    public static final Vector2i plus(@NotNull Vector2ic $this$plus, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = $this$plus.add(v, new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"add(v, Vector2i())");
        return vector2i;
    }

    public static final void plusAssign(@NotNull Vector2i $this$plusAssign, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector2i minus(@NotNull Vector2ic $this$minus, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = $this$minus.sub(v, new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"sub(v, Vector2i())");
        return vector2i;
    }

    public static final void minusAssign(@NotNull Vector2i $this$minusAssign, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector2i times(@NotNull Vector2ic $this$times, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = $this$times.mul(v, new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"mul(v, Vector2i())");
        return vector2i;
    }

    @NotNull
    public static final Vector2i times(@NotNull Vector2ic $this$times, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector2i vector2i = $this$times.mul(s, new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"mul(s, Vector2i())");
        return vector2i;
    }

    public static final void timesAssign(@NotNull Vector2i $this$timesAssign, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector2i $this$timesAssign, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    @NotNull
    public static final Vector2i div(@NotNull Vector2ic $this$div, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector2i vector2i = $this$div.div(s, new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"div(s, Vector2i())");
        return vector2i;
    }

    @NotNull
    public static final Vector2i div(@NotNull Vector2ic $this$div, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector2i vector2i = $this$div.div(s, new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"div(s, Vector2i())");
        return vector2i;
    }

    public static final void divAssign(@NotNull Vector2i $this$divAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    public static final void divAssign(@NotNull Vector2i $this$divAssign, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Vector2i unaryMinus(@NotNull Vector2ic $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector2i vector2i = $this$unaryMinus.negate(new Vector2i());
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"negate(Vector2i())");
        return vector2i;
    }

    public static final double distance(@NotNull Vector2ic $this$distance, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final long distanceSquared(@NotNull Vector2ic $this$distanceSquared, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final long gridDistance(@NotNull Vector2ic $this$gridDistance, @NotNull Vector2ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$gridDistance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$gridDistance.gridDistance(v);
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull ByteBuffer $this$getVector2i) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        return new Vector2i($this$getVector2i);
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull ByteBuffer $this$getVector2i, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        return new Vector2i(index, $this$getVector2i);
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull ByteBuffer $this$getVector2i, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = v.set($this$getVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"v.set(this)");
        return vector2i;
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull ByteBuffer $this$getVector2i, int index, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = v.set(index, $this$getVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"v.set(index, this)");
        return vector2i;
    }

    @NotNull
    public static final ByteBuffer putVector2i(@NotNull ByteBuffer $this$putVector2i, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector2i(@NotNull ByteBuffer $this$putVector2i, int index, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull IntBuffer $this$getVector2i) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        return new Vector2i($this$getVector2i);
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull IntBuffer $this$getVector2i, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        return new Vector2i(index, $this$getVector2i);
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull IntBuffer $this$getVector2i, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = v.set($this$getVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"v.set(this)");
        return vector2i;
    }

    @NotNull
    public static final Vector2i getVector2i(@NotNull IntBuffer $this$getVector2i, int index, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector2i vector2i = v.set(index, $this$getVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)vector2i, (String)"v.set(index, this)");
        return vector2i;
    }

    @NotNull
    public static final IntBuffer putVector2i(@NotNull IntBuffer $this$putVector2i, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        IntBuffer intBuffer = v.get($this$putVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)intBuffer, (String)"v.get(this)");
        return intBuffer;
    }

    @NotNull
    public static final IntBuffer putVector2i(@NotNull IntBuffer $this$putVector2i, int index, @NotNull Vector2i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector2i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        IntBuffer intBuffer = v.get(index, $this$putVector2i);
        Intrinsics.checkNotNullExpressionValue((Object)intBuffer, (String)"v.get(index, this)");
        return intBuffer;
    }
}

