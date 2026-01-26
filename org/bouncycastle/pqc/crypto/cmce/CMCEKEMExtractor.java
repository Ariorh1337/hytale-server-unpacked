/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.cmce.CMCEEngine;
import org.bouncycastle.pqc.crypto.cmce.CMCEKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;

public class CMCEKEMExtractor
implements EncapsulatedSecretExtractor {
    private CMCEEngine engine;
    private CMCEKeyParameters key;

    public CMCEKEMExtractor(CMCEPrivateKeyParameters cMCEPrivateKeyParameters) {
        this.key = cMCEPrivateKeyParameters;
        this.initCipher(this.key.getParameters());
    }

    private void initCipher(CMCEParameters cMCEParameters) {
        this.engine = cMCEParameters.getEngine();
        CMCEPrivateKeyParameters cMCEPrivateKeyParameters = (CMCEPrivateKeyParameters)this.key;
        if (cMCEPrivateKeyParameters.getPrivateKey().length < this.engine.getPrivateKeySize()) {
            this.key = new CMCEPrivateKeyParameters(cMCEPrivateKeyParameters.getParameters(), this.engine.decompress_private_key(cMCEPrivateKeyParameters.getPrivateKey()));
        }
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        return this.extractSecret(byArray, this.engine.getDefaultSessionKeySize());
    }

    public byte[] extractSecret(byte[] byArray, int n) {
        byte[] byArray2 = new byte[n / 8];
        this.engine.kem_dec(byArray2, byArray, ((CMCEPrivateKeyParameters)this.key).getPrivateKey());
        return byArray2;
    }

    @Override
    public int getEncapsulationLength() {
        return this.engine.getCipherTextSize();
    }
}

