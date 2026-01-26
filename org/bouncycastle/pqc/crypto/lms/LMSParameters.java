/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

public class LMSParameters {
    private final LMSigParameters lmSigParam;
    private final LMOtsParameters lmOTSParam;

    public LMSParameters(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters) {
        this.lmSigParam = lMSigParameters;
        this.lmOTSParam = lMOtsParameters;
    }

    public LMSigParameters getLMSigParam() {
        return this.lmSigParam;
    }

    public LMOtsParameters getLMOTSParam() {
        return this.lmOTSParam;
    }
}

