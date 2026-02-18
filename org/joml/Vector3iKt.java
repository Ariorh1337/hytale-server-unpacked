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
import org.joml.Vector3i;
import org.joml.Vector3ic;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000<\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0003\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0004\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\n\u001a\u00020\u000b*\u00020\u00022\u0006\u0010\f\u001a\u00020\rH\u0086\u0002\u001a\u0015\u0010\n\u001a\u00020\u000b*\u00020\u00022\u0006\u0010\f\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000e\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0086\u0002\u001a\u0015\u0010\u000e\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0001H\u0086\u0002\u001a\n\u0010\u0010\u001a\u00020\u000b*\u00020\u0011\u001a\u0012\u0010\u0010\u001a\u00020\u000b*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0001\u001a\u001a\u0010\u0010\u001a\u00020\u000b*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u0012\u0010\u0010\u001a\u00020\u000b*\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u000b\u001a\n\u0010\u0010\u001a\u00020\u000b*\u00020\u0013\u001a\u0012\u0010\u0010\u001a\u00020\u000b*\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0001\u001a\u001a\u0010\u0010\u001a\u00020\u000b*\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u0012\u0010\u0010\u001a\u00020\u000b*\u00020\u00132\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u0015\u0010\u0014\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0015\u001a\u00020\u000b*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0016\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\u000b*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0002\u001a\u001a\u0010\u0019\u001a\u00020\u0011*\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u0012\u0010\u0019\u001a\u00020\u0011*\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u001a\u0010\u0019\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u0012\u0010\u0019\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\u0007\u001a\u00020\u000b\u001a\u0015\u0010\u001a\u001a\u00020\u000b*\u00020\u00022\u0006\u0010\f\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\u000b*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\u0002H\u0086\u0002\u001a\r\u0010\u001c\u001a\u00020\u000b*\u00020\u0002H\u0086\u0002\u00a8\u0006\u001d"}, d2={"component1", "", "Lorg/joml/Vector3ic;", "component2", "component3", "distance", "", "v", "distanceSquared", "", "div", "Lorg/joml/Vector3i;", "s", "", "divAssign", "", "getVector3i", "Ljava/nio/ByteBuffer;", "index", "Ljava/nio/IntBuffer;", "gridDistance", "minus", "minusAssign", "plus", "plusAssign", "putVector3i", "times", "timesAssign", "unaryMinus", "joml"})
public final class Vector3iKt {
    public static final int component1(@NotNull Vector3ic $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final int component2(@NotNull Vector3ic $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    public static final int component3(@NotNull Vector3ic $this$component3) {
        Intrinsics.checkNotNullParameter((Object)$this$component3, (String)"<this>");
        return $this$component3.z();
    }

    @NotNull
    public static final Vector3i plus(@NotNull Vector3ic $this$plus, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = $this$plus.add(v, new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"add(v, Vector3i())");
        return vector3i;
    }

    public static final void plusAssign(@NotNull Vector3i $this$plusAssign, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector3i minus(@NotNull Vector3ic $this$minus, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = $this$minus.sub(v, new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"sub(v, Vector3i())");
        return vector3i;
    }

    public static final void minusAssign(@NotNull Vector3i $this$minusAssign, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector3i times(@NotNull Vector3ic $this$times, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = $this$times.mul(v, new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"mul(v, Vector3i())");
        return vector3i;
    }

    @NotNull
    public static final Vector3i times(@NotNull Vector3ic $this$times, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector3i vector3i = $this$times.mul(s, new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"mul(s, Vector3i())");
        return vector3i;
    }

    public static final void timesAssign(@NotNull Vector3i $this$timesAssign, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector3i $this$timesAssign, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    @NotNull
    public static final Vector3i div(@NotNull Vector3ic $this$div, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector3i vector3i = $this$div.div(s, new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"div(s, Vector3i())");
        return vector3i;
    }

    @NotNull
    public static final Vector3i div(@NotNull Vector3ic $this$div, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector3i vector3i = $this$div.div(s, new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"div(s, Vector3i())");
        return vector3i;
    }

    public static final void divAssign(@NotNull Vector3i $this$divAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    public static final void divAssign(@NotNull Vector3i $this$divAssign, int s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Vector3i unaryMinus(@NotNull Vector3ic $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector3i vector3i = $this$unaryMinus.negate(new Vector3i());
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"negate(Vector3i())");
        return vector3i;
    }

    public static final double distance(@NotNull Vector3ic $this$distance, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final long distanceSquared(@NotNull Vector3ic $this$distanceSquared, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final long gridDistance(@NotNull Vector3ic $this$gridDistance, @NotNull Vector3ic v) {
        Intrinsics.checkNotNullParameter((Object)$this$gridDistance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$gridDistance.gridDistance(v);
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull ByteBuffer $this$getVector3i) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        return new Vector3i($this$getVector3i);
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull ByteBuffer $this$getVector3i, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        return new Vector3i(index, $this$getVector3i);
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull ByteBuffer $this$getVector3i, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = v.set($this$getVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"v.set(this)");
        return vector3i;
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull ByteBuffer $this$getVector3i, int index, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = v.set(index, $this$getVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"v.set(index, this)");
        return vector3i;
    }

    @NotNull
    public static final ByteBuffer putVector3i(@NotNull ByteBuffer $this$putVector3i, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector3i(@NotNull ByteBuffer $this$putVector3i, int index, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull IntBuffer $this$getVector3i) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        return new Vector3i($this$getVector3i);
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull IntBuffer $this$getVector3i, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        return new Vector3i(index, $this$getVector3i);
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull IntBuffer $this$getVector3i, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = v.set($this$getVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"v.set(this)");
        return vector3i;
    }

    @NotNull
    public static final Vector3i getVector3i(@NotNull IntBuffer $this$getVector3i, int index, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3i vector3i = v.set(index, $this$getVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)vector3i, (String)"v.set(index, this)");
        return vector3i;
    }

    @NotNull
    public static final IntBuffer putVector3i(@NotNull IntBuffer $this$putVector3i, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        IntBuffer intBuffer = v.get($this$putVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)intBuffer, (String)"v.get(this)");
        return intBuffer;
    }

    @NotNull
    public static final IntBuffer putVector3i(@NotNull IntBuffer $this$putVector3i, int index, @NotNull Vector3i v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3i, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        IntBuffer intBuffer = v.get(index, $this$putVector3i);
        Intrinsics.checkNotNullExpressionValue((Object)intBuffer, (String)"v.get(index, this)");
        return intBuffer;
    }
}

