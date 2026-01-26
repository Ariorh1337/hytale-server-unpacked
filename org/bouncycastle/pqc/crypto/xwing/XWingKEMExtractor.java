/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.xwing.XWingKEMGenerator;
import org.bouncycastle.pqc.crypto.xwing.XWingPrivateKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingKEMExtractor
implements EncapsulatedSecretExtractor {
    private static final int MLKEM_CIPHERTEXT_SIZE = 1088;
    private final XWingPrivateKeyParameters key;
    private final MLKEMExtractor mlkemExtractor;

    public XWingKEMExtractor(XWingPrivateKeyParameters xWingPrivateKeyParameters) {
        this.key = xWingPrivateKeyParameters;
        this.mlkemExtractor = new MLKEMExtractor(this.key.getKyberPrivateKey());
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        byte[] byArray2 = Arrays.copyOfRange(byArray, 0, 1088);
        byte[] byArray3 = Arrays.copyOfRange(byArray, 1088, byArray.length);
        byte[] byArray4 = XWingKEMGenerator.computeSSX(new X25519PublicKeyParameters(byArray3, 0), this.key.getXDHPrivateKey());
        byte[] byArray5 = XWingKEMGenerator.computeSharedSecret(this.key.getXDHPublicKey().getEncoded(), this.mlkemExtractor.extractSecret(byArray2), byArray3, byArray4);
        Arrays.clear(byArray4);
        return byArray5;
    }

    @Override
    public int getEncapsulationLength() {
        return this.mlkemExtractor.getEncapsulationLength() + 32;
    }
}

