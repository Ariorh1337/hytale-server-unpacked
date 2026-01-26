/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharSet;

public final class CharSetOfNone
implements CharSet {
    @Override
    public boolean containsKey(char ch) {
        return false;
    }
}

