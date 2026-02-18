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
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000D\n\u0000\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0004\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0006\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0007\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u000e\u001a\u00020\u000f*\u00020\t2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000e\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0010\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\n\u0010\u0011\u001a\u00020\t*\u00020\u0012\u001a\u0012\u0010\u0011\u001a\u00020\t*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014\u001a\u001a\u0010\u0011\u001a\u00020\t*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u0011\u001a\u00020\t*\u00020\u00122\u0006\u0010\u0003\u001a\u00020\t\u001a\n\u0010\u0011\u001a\u00020\t*\u00020\u0015\u001a\u0012\u0010\u0011\u001a\u00020\t*\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u0014\u001a\u001a\u0010\u0011\u001a\u00020\t*\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u0011\u001a\u00020\t*\u00020\u00152\u0006\u0010\u0003\u001a\u00020\t\u001a\u0015\u0010\u0016\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0019\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u001a\u0010\u001a\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u001a\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0003\u001a\u00020\t\u001a\u001a\u0010\u001a\u001a\u00020\u0015*\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u001a\u001a\u00020\u0015*\u00020\u00152\u0006\u0010\u0003\u001a\u00020\t\u001a\u0015\u0010\u001b\u001a\u00020\t*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u001dH\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u001eH\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u001fH\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001c\u001a\u00020 H\u0086\u0002\u001a\u0015\u0010\u001b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010!\u001a\u00020\u000f*\u00020\t2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010!\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u001c\u001a\u00020\u001dH\u0086\u0002\u001a\u0015\u0010!\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u001c\u001a\u00020\u001eH\u0086\u0002\u001a\u0015\u0010!\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u001c\u001a\u00020\u001fH\u0086\u0002\u001a\u0015\u0010!\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u001c\u001a\u00020 H\u0086\u0002\u001a\u0015\u0010!\u001a\u00020\u000f*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\r\u0010\"\u001a\u00020\t*\u00020\u0002H\u0086\u0002\u00a8\u0006#"}, d2={"angle", "", "Lorg/joml/Vector3fc;", "v", "angleCos", "component1", "component2", "component3", "cross", "Lorg/joml/Vector3f;", "distance", "distanceSquared", "div", "s", "divAssign", "", "dot", "getVector3f", "Ljava/nio/ByteBuffer;", "index", "", "Ljava/nio/FloatBuffer;", "minus", "minusAssign", "plus", "plusAssign", "putVector3f", "times", "m", "Lorg/joml/Matrix3dc;", "Lorg/joml/Matrix3fc;", "Lorg/joml/Matrix3x2dc;", "Lorg/joml/Matrix3x2fc;", "timesAssign", "unaryMinus", "joml"})
public final class Vector3fKt {
    public static final float component1(@NotNull Vector3fc $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final float component2(@NotNull Vector3fc $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    public static final float component3(@NotNull Vector3fc $this$component3) {
        Intrinsics.checkNotNullParameter((Object)$this$component3, (String)"<this>");
        return $this$component3.z();
    }

    @NotNull
    public static final Vector3f plus(@NotNull Vector3fc $this$plus, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$plus.add(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"add(v, Vector3f())");
        return vector3f;
    }

    public static final void plusAssign(@NotNull Vector3f $this$plusAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector3f minus(@NotNull Vector3fc $this$minus, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$minus.sub(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"sub(v, Vector3f())");
        return vector3f;
    }

    public static final void minusAssign(@NotNull Vector3f $this$minusAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector3f times(@NotNull Vector3fc $this$times, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$times.mul(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"mul(v, Vector3f())");
        return vector3f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Vector3fc $this$times, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector3f vector3f = $this$times.mul(s, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"mul(s, Vector3f())");
        return vector3f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Vector3fc $this$times, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3f vector3f = $this$times.mul(m, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"mul(m, Vector3f())");
        return vector3f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Vector3fc $this$times, @NotNull Matrix3x2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3f vector3f = $this$times.mul(m, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"mul(m, Vector3f())");
        return vector3f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Vector3fc $this$times, @NotNull Matrix3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3f vector3f = $this$times.mul(m, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"mul(m, Vector3f())");
        return vector3f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Vector3fc $this$times, @NotNull Matrix3x2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3f vector3f = $this$times.mul(m, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"mul(m, Vector3f())");
        return vector3f;
    }

    public static final void timesAssign(@NotNull Vector3f $this$timesAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector3f $this$timesAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    public static final void timesAssign(@NotNull Vector3f $this$timesAssign, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector3f $this$timesAssign, @NotNull Matrix3x2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector3f $this$timesAssign, @NotNull Matrix3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector3f $this$timesAssign, @NotNull Matrix3x2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Vector3f div(@NotNull Vector3fc $this$div, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$div.div(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"div(v, Vector3f())");
        return vector3f;
    }

    @NotNull
    public static final Vector3f div(@NotNull Vector3fc $this$div, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector3f vector3f = $this$div.div(s, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"div(s, Vector3f())");
        return vector3f;
    }

    public static final void divAssign(@NotNull Vector3f $this$divAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    public static final void divAssign(@NotNull Vector3f $this$divAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Vector3f unaryMinus(@NotNull Vector3fc $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector3f vector3f = $this$unaryMinus.negate(new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"negate(Vector3f())");
        return vector3f;
    }

    public static final float dot(@NotNull Vector3fc $this$dot, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$dot, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$dot.dot(v);
    }

    @NotNull
    public static final Vector3f cross(@NotNull Vector3fc $this$cross, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$cross, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$cross.cross(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"cross(v, Vector3f())");
        return vector3f;
    }

    public static final float distance(@NotNull Vector3fc $this$distance, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final float distanceSquared(@NotNull Vector3fc $this$distanceSquared, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final float angleCos(@NotNull Vector3fc $this$angleCos, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angleCos, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angleCos.angleCos(v);
    }

    public static final float angle(@NotNull Vector3fc $this$angle, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angle, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angle.angle(v);
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull ByteBuffer $this$getVector3f) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        return new Vector3f($this$getVector3f);
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull ByteBuffer $this$getVector3f, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        return new Vector3f(index, $this$getVector3f);
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull ByteBuffer $this$getVector3f, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = v.set($this$getVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"v.set(this)");
        return vector3f;
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull ByteBuffer $this$getVector3f, int index, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = v.set(index, $this$getVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"v.set(index, this)");
        return vector3f;
    }

    @NotNull
    public static final ByteBuffer putVector3f(@NotNull ByteBuffer $this$putVector3f, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector3f(@NotNull ByteBuffer $this$putVector3f, int index, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull FloatBuffer $this$getVector3f) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        return new Vector3f($this$getVector3f);
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull FloatBuffer $this$getVector3f, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        return new Vector3f(index, $this$getVector3f);
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull FloatBuffer $this$getVector3f, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = v.set($this$getVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"v.set(this)");
        return vector3f;
    }

    @NotNull
    public static final Vector3f getVector3f(@NotNull FloatBuffer $this$getVector3f, int index, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = v.set(index, $this$getVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"v.set(index, this)");
        return vector3f;
    }

    @NotNull
    public static final FloatBuffer putVector3f(@NotNull FloatBuffer $this$putVector3f, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get($this$putVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(this)");
        return floatBuffer;
    }

    @NotNull
    public static final FloatBuffer putVector3f(@NotNull FloatBuffer $this$putVector3f, int index, @NotNull Vector3f v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3f, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        FloatBuffer floatBuffer = v.get(index, $this$putVector3f);
        Intrinsics.checkNotNullExpressionValue((Object)floatBuffer, (String)"v.get(index, this)");
        return floatBuffer;
    }
}

