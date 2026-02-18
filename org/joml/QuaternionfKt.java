/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.joml;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u00002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0004\u001a\u0015\u0010\u0004\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0004\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u0007\u001a\u00020\b*\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0007\u001a\u00020\b*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\t\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\n\u001a\u00020\b*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\u000b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\f\u001a\u00020\b*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u0010H\u0086\u0002\u001a\u0015\u0010\r\u001a\u00020\u0011*\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u0012H\u0086\u0002\u001a\u0015\u0010\u0013\u001a\u00020\b*\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0086\u0002\u001a\u0015\u0010\u0013\u001a\u00020\b*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0086\u0002\u00a8\u0006\u0014"}, d2={"difference", "Lorg/joml/Quaternionf;", "Lorg/joml/Quaternionfc;", "q", "div", "s", "", "divAssign", "", "minus", "minusAssign", "plus", "plusAssign", "times", "Lorg/joml/Vector3f;", "v", "Lorg/joml/Vector3fc;", "Lorg/joml/Vector4f;", "Lorg/joml/Vector4fc;", "timesAssign", "joml"})
public final class QuaternionfKt {
    @NotNull
    public static final Quaternionf plus(@NotNull Quaternionfc $this$plus, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Quaternionf quaternionf = $this$plus.add(q, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"add(q, Quaternionf())");
        return quaternionf;
    }

    public static final void plusAssign(@NotNull Quaternionf $this$plusAssign, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$plusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$plusAssign.add(q);
    }

    @NotNull
    public static final Quaternionf minus(@NotNull Quaternionfc $this$minus, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$minus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Quaternionf quaternionf = $this$minus.sub(q, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"sub(q, Quaternionf())");
        return quaternionf;
    }

    public static final void minusAssign(@NotNull Quaternionf $this$minusAssign, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$minusAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$minusAssign.sub(q);
    }

    @NotNull
    public static final Quaternionf times(@NotNull Quaternionfc $this$times, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Quaternionf quaternionf = $this$times.mul(q, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"mul(q, Quaternionf())");
        return quaternionf;
    }

    @NotNull
    public static final Quaternionf times(@NotNull Quaternionfc $this$times, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Quaternionf quaternionf = $this$times.mul(s, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"mul(s, Quaternionf())");
        return quaternionf;
    }

    @NotNull
    public static final Vector4f times(@NotNull Quaternionfc $this$times, @NotNull Vector4fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector4f vector4f = $this$times.transform(v, new Vector4f());
        Intrinsics.checkNotNullExpressionValue((Object)vector4f, (String)"transform(v, Vector4f())");
        return vector4f;
    }

    @NotNull
    public static final Vector3f times(@NotNull Quaternionfc $this$times, @NotNull Vector3fc v) {
        Intrinsics.checkNotNullParameter((Object)$this$times, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        Vector3f vector3f = $this$times.transform(v, new Vector3f());
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"transform(v, Vector3f())");
        return vector3f;
    }

    public static final void timesAssign(@NotNull Quaternionf $this$timesAssign, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$timesAssign.mul(q);
    }

    public static final void timesAssign(@NotNull Quaternionf $this$timesAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$timesAssign, (String)"<this>");
        $this$timesAssign.mul(s);
    }

    @NotNull
    public static final Quaternionf div(@NotNull Quaternionfc $this$div, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Quaternionf quaternionf = $this$div.div(q, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"div(q, Quaternionf())");
        return quaternionf;
    }

    @NotNull
    public static final Quaternionf div(@NotNull Quaternionfc $this$div, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$div, (String)"<this>");
        Quaternionf quaternionf = $this$div.div(s, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"div(s, Quaternionf())");
        return quaternionf;
    }

    public static final void divAssign(@NotNull Quaternionf $this$divAssign, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        $this$divAssign.div(q);
    }

    public static final void divAssign(@NotNull Quaternionf $this$divAssign, float s) {
        Intrinsics.checkNotNullParameter((Object)$this$divAssign, (String)"<this>");
        $this$divAssign.div(s);
    }

    @NotNull
    public static final Quaternionf difference(@NotNull Quaternionfc $this$difference, @NotNull Quaternionfc q) {
        Intrinsics.checkNotNullParameter((Object)$this$difference, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)q, (String)"q");
        Quaternionf quaternionf = $this$difference.difference(q, new Quaternionf());
        Intrinsics.checkNotNullExpressionValue((Object)quaternionf, (String)"difference(q, Quaternionf())");
        return quaternionf;
    }
}

