/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteTrie;

final class ByteTrieOfNone
implements ByteTrie {
    ByteTrieOfNone() {
    }

    @Override
    public int match(byte[] str) {
        return 0;
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        return 0;
    }
}

