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
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000<\n\u0000\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0004\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0006\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0007\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\b\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\t\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000b\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u000e\u001a\u00020\u000f*\u00020\f2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000e\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\n\u0010\u0011\u001a\u00020\f*\u00020\u0012\u001a\u0012\u0010\u0011\u001a\u00020\f*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014\u001a\u001a\u0010\u0011\u001a\u00020\f*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\f\u001a\u0012\u0010\u0011\u001a\u00020\f*\u00020\u00122\u0006\u0010\u0003\u001a\u00020\f\u001a\n\u0010\u0011\u001a\u00020\f*\u00020\u0015\u001a\u0012\u0010\u0011\u001a\u00020\f*\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u0014\u001a\u001a\u0010\u0011\u001a\u00020\f*\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\f\u001a\u0012\u0010\u0011\u001a\u00020\f*\u00020\u00152\u0006\u0010\u0003\u001a\u00020\f\u001a\u0015\u0010\u0016\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0019\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u001a\u0010\u001a\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\f\u001a\u0012\u0010\u001a\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0003\u001a\u00020\f\u001a\u001a\u0010\u001a\u001a\u00020\u0015*\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\f\u001a\u0012\u0010\u001a\u001a\u00020\u0015*\u00020\u00152\u0006\u0010\u0003\u001a\u00020\f\u001a\u0015\u0010\u001b\u001a\u00020\f*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\f*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u001dH\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\f*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u001eH\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001f\u001a\u00020\u000f*\u00020\f2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001f\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u001c\u001a\u00020\u001dH\u0086\u0002\u001a\u0015\u0010\u001f\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u001c\u001a\u00020\u001eH\u0086\u0002\u001a\u0015\u0010\u001f\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\r\u0010 \u001a\u00020\f*\u00020\u0002H\u0086\u0002\u00a8\u0006!"}, d2={"angle", "", "Lorg/joml/Vector4fc;", "v", "angleCos", "component1", "component2", "component3", "component4", "distance", "distanceSquared", "div", "Lorg/joml/Vector4f;", "s", "divAssign", "", "dot", "getVector4f", "Ljava/nio/ByteBuffer;", "index", "", "Ljava/nio/FloatBuffer;", "minus", "minusAssign", "plus", "plusAssign", "putVector4f", "times", "m", "Lorg/joml/Matrix4fc;", "Lorg/joml/Matrix4x3fc;", "timesAssign", "unaryMinus", "joml"})
public final class Vector4fKt {
    public static final float component1(@NotNull Vector4fc $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final float component2(@NotNull Vector4fc $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    public static final float component3(@NotNull Vector4fc $this$component3) {
        Intrinsics.checkNotNullParameter((Object)$this$component3, (String)"<this>");
        return $this$component3.z();
    }

    public static final float component4(@NotNull Vector4fc $this$component4) {
        Intrinsics.checkNotNullParameter((Object)$this$component4, (String)"<this>");
        return $this$component4.w();
    }

    @NotNull
    public static final Vector4f plus(@NotNull Vector4fc $this$plus, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = $this$plus.add(v, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"add(v, Vector4f())");
        return vector4f;
    }

    public static final void plusAssign(@NotNull Vector4f $this$plusAssign, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector4f minus(@NotNull Vector4fc $this$minus, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = $this$minus.sub(v, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"sub(v, Vector4f())");
        return vector4f;
    }

    public static final void minusAssign(@NotNull Vector4f $this$minusAssign, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector4f times(@NotNull Vector4fc $this$times, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = $this$times.mul(v, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"mul(v, Vector4f())");
        return vector4f;
    }

    @NotNull
    public static final Vector4f times(@NotNull Vector4fc $this$times, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector4f vector4f = $this$times.mul(s, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"mul(s, Vector4f())");
        return vector4f;
    }

    @NotNull
    public static final Vector4f times(@NotNull Vector4fc $this$times, @NotNull Matrix4fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector4f vector4f = $this$times.mul(m, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"mul(m, Vector4f())");
        return vector4f;
    }

    @NotNull
    public static final Vector4f times(@NotNull Vector4fc $this$times, @NotNull Matrix4x3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector4f vector4f = $this$times.mul(m, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"mul(m, Vector4f())");
        return vector4f;
    }

    public static final void timesAssign(@NotNull Vector4f $this$timesAssign, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector4f $this$timesAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    public static final void timesAssign(@NotNull Vector4f $this$timesAssign, @NotNull Matrix4fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector4f $this$timesAssign, @NotNull Matrix4x3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Vector4f div(@NotNull Vector4fc $this$div, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = $this$div.div(v, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"div(v, Vector4f())");
        return vector4f;
    }

    @NotNull
    public static final Vector4f div(@NotNull Vector4fc $this$div, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector4f vector4f = $this$div.div(s, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"div(s, Vector4f())");
        return vector4f;
    }

    public static final void divAssign(@NotNull Vector4f $this$divAssign, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    public static final void divAssign(@NotNull Vector4f $this$divAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Vector4f unaryMinus(@NotNull Vector4fc $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector4f vector4f = $this$unaryMinus.negate(new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"negate(Vector4f())");
        return vector4f;
    }

    public static final float dot(@NotNull Vector4fc $this$dot, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$dot, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$dot.dot(v);
    }

    public static final float distance(@NotNull Vector4fc $this$distance, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final float distanceSquared(@NotNull Vector4fc $this$distanceSquared, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final float angleCos(@NotNull Vector4fc $this$angleCos, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angleCos, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angleCos.angleCos(v);
    }

    public static final float angle(@NotNull Vector4fc $this$angle, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angle, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angle.angle(v);
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull ByteBuffer $this$getVector4f) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        return new Vector4f($this$getVector4f);
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull ByteBuffer $this$getVector4f, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        return new Vector4f(index, $this$getVector4f);
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull ByteBuffer $this$getVector4f, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = v.set($this$getVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"v.set(this)");
        return vector4f;
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull ByteBuffer $this$getVector4f, int index, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = v.set(index, $this$getVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"v.set(index, this)");
        return vector4f;
    }

    @NotNull
    public static final ByteBuffer putVector4f(@NotNull ByteBuffer $this$putVector4f, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector4f(@NotNull ByteBuffer $this$putVector4f, int index, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull FloatBuffer $this$getVector4f) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        return new Vector4f($this$getVector4f);
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull FloatBuffer $this$getVector4f, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        return new Vector4f(index, $this$getVector4f);
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull FloatBuffer $this$getVector4f, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = v.set($this$getVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"v.set(this)");
        return vector4f;
    }

    @NotNull
    public static final Vector4f getVector4f(@NotNull FloatBuffer $this$getVector4f, int index, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = v.set(index, $this$getVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"v.set(index, this)");
        return vector4f;
    }

    @NotNull
    public static final FloatBuffer putVector4f(@NotNull FloatBuffer $this$putVector4f, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get($this$putVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(this)");
        return floatBuffer;
    }

    @NotNull
    public static final FloatBuffer putVector4f(@NotNull FloatBuffer $this$putVector4f, int index, @NotNull Vector4f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector4f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get(index, $this$putVector4f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(index, this)");
        return floatBuffer;
    }
}

