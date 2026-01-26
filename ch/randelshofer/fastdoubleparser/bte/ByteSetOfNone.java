/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteSet;

final class ByteSetOfNone
implements ByteSet {
    ByteSetOfNone() {
    }

    @Override
    public boolean containsKey(byte b) {
        return false;
    }
}

