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
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000J\n\u0000\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0004\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0006\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\r\u0010\u0007\u001a\u00020\u0001*\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\n\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\t2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u0011\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\n\u0010\u0012\u001a\u00020\t*\u00020\u0013\u001a\u0012\u0010\u0012\u001a\u00020\t*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015\u001a\u001a\u0010\u0012\u001a\u00020\t*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u0012\u001a\u00020\t*\u00020\u00132\u0006\u0010\u0003\u001a\u00020\t\u001a\n\u0010\u0012\u001a\u00020\t*\u00020\u0016\u001a\u0012\u0010\u0012\u001a\u00020\t*\u00020\u00162\u0006\u0010\u0014\u001a\u00020\u0015\u001a\u001a\u0010\u0012\u001a\u00020\t*\u00020\u00162\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u0012\u001a\u00020\t*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\t\u001a\u0015\u0010\u0017\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0017\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0018\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u0019\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0019\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001a\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u001a\u0010\u001b\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u001b\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\u0003\u001a\u00020\t\u001a\u001a\u0010\u001b\u001a\u00020\u0016*\u00020\u00162\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0003\u001a\u00020\t\u001a\u0012\u0010\u001b\u001a\u00020\u0016*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\t\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u001eH\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u001fH\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001d\u001a\u00020 H\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\u001d\u001a\u00020!H\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u001c\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\r\u001a\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u001d\u001a\u00020\u001eH\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u001d\u001a\u00020\u001fH\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u001d\u001a\u00020 H\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u001d\u001a\u00020!H\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\"\u001a\u00020\u0010*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u000eH\u0086\u0002\u001a\r\u0010#\u001a\u00020\t*\u00020\u0002H\u0086\u0002\u00a8\u0006$"}, d2={"angle", "", "Lorg/joml/Vector3dc;", "v", "angleCos", "component1", "component2", "component3", "cross", "Lorg/joml/Vector3d;", "distance", "distanceSquared", "div", "s", "Lorg/joml/Vector3fc;", "divAssign", "", "dot", "getVector3d", "Ljava/nio/ByteBuffer;", "index", "", "Ljava/nio/DoubleBuffer;", "minus", "minusAssign", "plus", "plusAssign", "putVector3d", "times", "m", "Lorg/joml/Matrix3dc;", "Lorg/joml/Matrix3fc;", "Lorg/joml/Matrix3x2dc;", "Lorg/joml/Matrix3x2fc;", "timesAssign", "unaryMinus", "joml"})
public final class Vector3dKt {
    public static final double component1(@NotNull Vector3dc $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.x();
    }

    public static final double component2(@NotNull Vector3dc $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.y();
    }

    public static final double component3(@NotNull Vector3dc $this$component3) {
        Intrinsics.checkNotNullParameter((Object)$this$component3, (String)"<this>");
        return $this$component3.z();
    }

    @NotNull
    public static final Vector3d plus(@NotNull Vector3dc $this$plus, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$plus.add(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"add(v, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d plus(@NotNull Vector3dc $this$plus, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$plus.add(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"add(v, Vector3d())");
        return vector3d;
    }

    public static final void plusAssign(@NotNull Vector3d $this$plusAssign, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    public static final void plusAssign(@NotNull Vector3d $this$plusAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$plusAssign.add(v);
    }

    @NotNull
    public static final Vector3d minus(@NotNull Vector3dc $this$minus, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$minus.sub(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"sub(v, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d minus(@NotNull Vector3dc $this$minus, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$minus.sub(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"sub(v, Vector3d())");
        return vector3d;
    }

    public static final void minusAssign(@NotNull Vector3d $this$minusAssign, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    public static final void minusAssign(@NotNull Vector3d $this$minusAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$minusAssign.sub(v);
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$times.mul(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(v, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$times.mul(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(v, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, double s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Vector3d vector3d = $this$times.mul(s, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(s, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, @NotNull Matrix3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3d vector3d = $this$times.mul(m, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(m, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, @NotNull Matrix3x2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3d vector3d = $this$times.mul(m, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(m, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3d vector3d = $this$times.mul(m, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(m, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d times(@NotNull Vector3dc $this$times, @NotNull Matrix3x2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        Vector3d vector3d = $this$times.mul(m, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"mul(m, Vector3d())");
        return vector3d;
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$timesAssign.mul(v);
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, double s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, @NotNull Matrix3dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, @NotNull Matrix3x2dc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, @NotNull Matrix3fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    public static final void timesAssign(@NotNull Vector3d $this$timesAssign, @NotNull Matrix3x2fc m) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)m, (String)"m");
        $this$timesAssign.mul(m);
    }

    @NotNull
    public static final Vector3d div(@NotNull Vector3dc $this$div, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$div.div(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"div(v, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d div(@NotNull Vector3dc $this$div, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$div.div(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"div(v, Vector3d())");
        return vector3d;
    }

    @NotNull
    public static final Vector3d div(@NotNull Vector3dc $this$div, double s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Vector3d vector3d = $this$div.div(s, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"div(s, Vector3d())");
        return vector3d;
    }

    public static final void divAssign(@NotNull Vector3d $this$divAssign, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    public static final void divAssign(@NotNull Vector3d $this$divAssign, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        $this$divAssign.div(v);
    }

    public static final void divAssign(@NotNull Vector3d $this$divAssign, double s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Vector3d unaryMinus(@NotNull Vector3dc $this$unaryMinus) {
        Intrinsics.checkNotNullParameter((Object)$this$unaryMinus, (String)"<this>");
        Vector3d vector3d = $this$unaryMinus.negate(new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"negate(Vector3d())");
        return vector3d;
    }

    public static final double dot(@NotNull Vector3dc $this$dot, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$dot, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$dot.dot(v);
    }

    @NotNull
    public static final Vector3d cross(@NotNull Vector3dc $this$cross, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$cross, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = $this$cross.cross(v, new Vector3d());
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"cross(v, Vector3d())");
        return vector3d;
    }

    public static final double distance(@NotNull Vector3dc $this$distance, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distance, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distance.distance(v);
    }

    public static final double distanceSquared(@NotNull Vector3dc $this$distanceSquared, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$distanceSquared, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$distanceSquared.distanceSquared(v);
    }

    public static final double angleCos(@NotNull Vector3dc $this$angleCos, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angleCos, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angleCos.angleCos(v);
    }

    public static final double angle(@NotNull Vector3dc $this$angle, @NotNull Vector3dc v) {
        Intrinsics.checkNotNullParameter((Object)$this$angle, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        return $this$angle.angle(v);
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull ByteBuffer $this$getVector3d) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        return new Vector3d($this$getVector3d);
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull ByteBuffer $this$getVector3d, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        return new Vector3d(index, $this$getVector3d);
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull ByteBuffer $this$getVector3d, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = v.set($this$getVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"v.set(this)");
        return vector3d;
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull ByteBuffer $this$getVector3d, int index, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = v.set(index, $this$getVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"v.set(index, this)");
        return vector3d;
    }

    @NotNull
    public static final ByteBuffer putVector3d(@NotNull ByteBuffer $this$putVector3d, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get($this$putVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(this)");
        return byteBuffer;
    }

    @NotNull
    public static final ByteBuffer putVector3d(@NotNull ByteBuffer $this$putVector3d, int index, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        ByteBuffer byteBuffer = v.get(index, $this$putVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)byteBuffer, (String)"v.get(index, this)");
        return byteBuffer;
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull DoubleBuffer $this$getVector3d) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        return new Vector3d($this$getVector3d);
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull DoubleBuffer $this$getVector3d, int index) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        return new Vector3d(index, $this$getVector3d);
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull DoubleBuffer $this$getVector3d, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = v.set($this$getVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"v.set(this)");
        return vector3d;
    }

    @NotNull
    public static final Vector3d getVector3d(@NotNull DoubleBuffer $this$getVector3d, int index, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$getVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3d vector3d = v.set(index, $this$getVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)vector3d, (String)"v.set(index, this)");
        return vector3d;
    }

    @NotNull
    public static final DoubleBuffer putVector3d(@NotNull DoubleBuffer $this$putVector3d, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get($this$putVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(this)");
        return doubleBuffer;
    }

    @NotNull
    public static final DoubleBuffer putVector3d(@NotNull DoubleBuffer $this$putVector3d, int index, @NotNull Vector3d v) {
        Intrinsics.checkNotNullParameter((Object)$this$putVector3d, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        DoubleBuffer doubleBuffer = v.get(index, $this$putVector3d);
        Intrinsics.checkNotNullExpressionValue((Object)doubleBuffer, (String)"v.get(index, this)");
        return doubleBuffer;
    }
}

