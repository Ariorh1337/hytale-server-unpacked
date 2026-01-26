/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentSignerBuilder;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSSigner;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSSigner;

public class BcHssLmsContentSignerBuilder
extends BcContentSignerBuilder {
    private static final AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);

    public BcHssLmsContentSignerBuilder() {
        super(sigAlgId, null);
    }

    @Override
    protected Signer createSigner(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) throws OperatorCreationException {
        return new HssSigner();
    }

    static class HssSigner
    implements Signer {
        private MessageSigner signer;
        private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        @Override
        public void init(boolean bl, CipherParameters cipherParameters) {
            if (cipherParameters instanceof HSSPublicKeyParameters || cipherParameters instanceof HSSPrivateKeyParameters) {
                this.signer = new HSSSigner();
            } else if (cipherParameters instanceof LMSPublicKeyParameters || cipherParameters instanceof LMSPrivateKeyParameters) {
                this.signer = new LMSSigner();
            } else {
                throw new IllegalArgumentException("Incorrect Key Parameters");
            }
            this.signer.init(bl, cipherParameters);
        }

        @Override
        public void update(byte by) {
            this.stream.write(by);
        }

        @Override
        public void update(byte[] byArray, int n, int n2) {
            this.stream.write(byArray, n, n2);
        }

        @Override
        public byte[] generateSignature() throws CryptoException, DataLengthException {
            byte[] byArray = this.stream.toByteArray();
            this.stream.reset();
            return this.signer.generateSignature(byArray);
        }

        @Override
        public boolean verifySignature(byte[] byArray) {
            byte[] byArray2 = this.stream.toByteArray();
            this.stream.reset();
            return this.signer.verifySignature(byArray2, byArray);
        }

        @Override
        public void reset() {
            this.stream.reset();
        }
    }
}

