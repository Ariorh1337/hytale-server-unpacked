/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.oracle.svm.core.annotate.Alias
 *  com.oracle.svm.core.annotate.RecomputeFieldValue
 *  com.oracle.svm.core.annotate.RecomputeFieldValue$Kind
 *  com.oracle.svm.core.annotate.TargetClass
 */
package io.netty.util.internal.svm;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className="io.netty.util.internal.RefCnt$UnsafeRefCnt")
final class RefCntSubstitution {
    @Alias
    @RecomputeFieldValue(kind=RecomputeFieldValue.Kind.FieldOffset, declClassName="io.netty.util.internal.RefCnt", name="value")
    public static long VALUE_OFFSET;

    private RefCntSubstitution() {
    }
}

