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
import org.joml.Vector4i;
import org.joml.Vector4ic;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000>\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0003\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0004\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0005\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0006\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\f2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\f2\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0011\u001a\u00020\n*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0004\u001a\n\u0010\u0012\u001a\u00020\f*\u00020\u0013\u001a\u0012\u0010\u0012\u001a\u00020\f*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0001\u001a\u001a\u0010\u0012\u001a\u00020\f*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\f\u001a\u0012\u0010\u0012\u001a\u00020\f*\u00020\u00132\u0006\u0010\b\u001a\u00020\f\u001a\n\u0010\u0012\u001a\u00020\f*\u00020\u0015\u001a\u0012\u0010\u0012\u001a\u00020\f*\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u0001\u001a\u001a\u0010\u0012\u001a\u00020\f*\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\f\u001a\u0012\u0010\u0012\u001a\u00020\f*\u00020\u00152\u0006\u0010\b\u001a\u00020\f\u001a\u0015\u0010\u0016\u001a\u00020\n*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0017\u001a\u00020\f*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\u0010*\u00020\f2\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0019\u001a\u00020\f*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\u0010*\u00020\f2\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u001a\u0010\u001b\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\f\u001a\u0012\u0010\u001b\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\b\u001a\u00020\f\u001a\u001a\u0010\u001b\u001a\u00020\u0015*\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\f\u001a\u0012\u0010\u001b\u001a\u00020\u0015*\u00020\u00152\u0006\u0010\b\u001a\u00020\f\u001a\u0015\u0010\u001c\u001a\u00020\f*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\f*\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\u0010*\u00020\f2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\u0010*\u00020\f2\u0006\u0010\b\u001a\u00020\u0002H\u0086\u0002\u001a\r\u0010\u001e\u001a\u00020\f*\u00020\u0002H\u0086\u0002\u00a8\u0006\u001f"}, d2={"component1", "", "Lorg/joml/Vector4ic;", "component2", "component3", "component4", "distance", "", "v", "distanceSquared", "", "div", "Lorg/joml/Vector4i;", "s", "", "divAssign", "", "dot", "getVector4i", "Ljava/nio/ByteBuffer;", "index", "Ljava/nio/IntBuffer;", "gridDistance", "minus", "minusAssign", "plus", "plusAssign", "putVector4i", "times", "timesAssign", "unaryMinus", "joml"})
public final class Vector4iKt {
    public static final int component1(@NotNull Vector4ic $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final int component2(@NotNull Vector4ic $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    public static final int component3(@NotNull Vector4ic $this$component3) {
        Intrinsics.checkNotNullParameter((Object)$this$component3, (String)"<this>");
        return $this$component3.z();
    }

    public static final int component4(@NotNull Vector4ic $this$component4) {
        Intrinsics.checkNotNullParameter((Object)$this$component4, (String)"<this>");
        return $this$component4.w();
    }

    @NotNull
    public static final Vector4i plus(@NotNull Vector4ic $this$plus, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = $this$plus.add(v, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"add(v, Vector4i())");
        return vector4i;
    }

    public static final void plusAssign(@NotNull Vector4i $this$plusAssign, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector4i minus(@NotNull Vector4ic $this$minus, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = $this$minus.sub(v, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"sub(v, Vector4i())");
        return vector4i;
    }

    public static final void minusAssign(@NotNull Vector4i $this$minusAssign, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector4i times(@NotNull Vector4ic $this$times, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = $this$times.mul(v, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"mul(v, Vector4i())");
        return vector4i;
    }

    @NotNull
    public static final Vector4i times(@NotNull Vector4ic $this$times, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector4i vector4i = $this$times.mul(s, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"mul(s, Vector4i())");
        return vector4i;
    }

    public static final void timesAssign(@NotNull Vector4i $this$timesAssign, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector4i $this$timesAssign, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    @NotNull
    public static final Vector4i div(@NotNull Vector4ic $this$div, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = $this$div.div(v, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"div(v, Vector4i())");
        return vector4i;
    }

    @NotNull
    public static final Vector4i div(@NotNull Vector4ic $this$div, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector4i vector4i = $this$div.div(s, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"div(s, Vector4i())");
        return vector4i;
    }

    @NotNull
    public static final Vector4i div(@NotNull Vector4ic $this$div, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector4i vector4i = $this$div.div(s, new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"div(s, Vector4i())");
        return vector4i;
    }

    public static final void divAssign(@NotNull Vector4i $this$divAssign, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    public static final void divAssign(@NotNull Vector4i $this$divAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    public static final void divAssign(@NotNull Vector4i $this$divAssign, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Vector4i unaryMinus(@NotNull Vector4ic $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector4i vector4i = $this$unaryMinus.negate(new Vector4i());
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"negate(Vector4i())");
        return vector4i;
    }

    public static final long dot(@NotNull Vector4ic $this$dot, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$dot, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$dot.dot(v);
    }

    public static final double distance(@NotNull Vector4ic $this$distance, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final long distanceSquared(@NotNull Vector4ic $this$distanceSquared, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final long gridDistance(@NotNull Vector4ic $this$gridDistance, @NotNull Vector4ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$gridDistance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$gridDistance.gridDistance(v);
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull ByteBuffer $this$getVector4i) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        return new Vector4i($this$getVector4i);
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull ByteBuffer $this$getVector4i, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        return new Vector4i(index, $this$getVector4i);
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull ByteBuffer $this$getVector4i, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = v.set($this$getVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"v.set(this)");
        return vector4i;
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull ByteBuffer $this$getVector4i, int index, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = v.set(index, $this$getVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"v.set(index, this)");
        return vector4i;
    }

    @NotNull
    public static final ByteBuffer putVector4i(@NotNull ByteBuffer $this$putVector4i, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector4i(@NotNull ByteBuffer $this$putVector4i, int index, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull IntBuffer $this$getVector4i) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        return new Vector4i($this$getVector4i);
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull IntBuffer $this$getVector4i, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        return new Vector4i(index, $this$getVector4i);
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull IntBuffer $this$getVector4i, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = v.set($this$getVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"v.set(this)");
        return vector4i;
    }

    @NotNull
    public static final Vector4i getVector4i(@NotNull IntBuffer $this$getVector4i, int index, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4i vector4i = v.set(index, $this$getVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)vector4i, (String)"v.set(index, this)");
        return vector4i;
    }

    @NotNull
    public static final IntBuffer putVector4i(@NotNull IntBuffer $this$putVector4i, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        IntBuffer intBuffer = v.get($this$putVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)intBuffer, (String)"v.get(this)");
        return intBuffer;
    }

    @NotNull
    public static final IntBuffer putVector4i(@NotNull IntBuffer $this$putVector4i, int index, @NotNull Vector4i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        IntBuffer intBuffer = v.get(index, $this$putVector4i);
        Intrinsics.checkNotNullExpressionValue((Object)intBuffer, (String)"v.get(index, this)");
        return intBuffer;
    }
}

