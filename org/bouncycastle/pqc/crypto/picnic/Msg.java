/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;

class Msg {
    byte[][] msgs;
    int pos;
    int unopened;

    public Msg(PicnicEngine picnicEngine) {
        this.msgs = new byte[picnicEngine.numMPCParties][picnicEngine.andSizeBytes];
        this.pos = 0;
        this.unopened = -1;
    }
}

