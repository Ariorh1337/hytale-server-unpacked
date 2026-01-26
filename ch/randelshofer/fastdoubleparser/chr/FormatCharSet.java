/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharSet;

public class FormatCharSet
implements CharSet {
    @Override
    public boolean containsKey(char ch) {
        return Character.getType(ch) == 16;
    }
}

