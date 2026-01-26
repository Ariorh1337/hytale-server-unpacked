/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;

public class CMCEKeyParameters
extends AsymmetricKeyParameter {
    private CMCEParameters params;

    public CMCEKeyParameters(boolean bl, CMCEParameters cMCEParameters) {
        super(bl);
        this.params = cMCEParameters;
    }

    public CMCEParameters getParameters() {
        return this.params;
    }
}

