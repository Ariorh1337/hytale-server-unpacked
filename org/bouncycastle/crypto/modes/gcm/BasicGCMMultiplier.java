/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;

public class BasicGCMMultiplier
implements GCMMultiplier {
    private long[] H;

    @Override
    public void init(byte[] byArray) {
        this.H = GCMUtil.asLongs(byArray);
    }

    @Override
    public void multiplyH(byte[] byArray) {
        GCMUtil.multiply(byArray, this.H);
    }
}

