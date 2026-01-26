/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSSignature;
import org.bouncycastle.pqc.crypto.lms.LMSSignedPubKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.io.Streams;

class HSSSignature
implements Encodable {
    private final int lMinus1;
    private final LMSSignedPubKey[] signedPubKey;
    private final LMSSignature signature;

    public HSSSignature(int n, LMSSignedPubKey[] lMSSignedPubKeyArray, LMSSignature lMSSignature) {
        this.lMinus1 = n;
        this.signedPubKey = lMSSignedPubKeyArray;
        this.signature = lMSSignature;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static HSSSignature getInstance(Object object, int n) throws IOException {
        if (object instanceof HSSSignature) {
            return (HSSSignature)object;
        }
        if (object instanceof DataInputStream) {
            int n2 = ((DataInputStream)object).readInt();
            if (n2 != n - 1) {
                throw new IllegalStateException("nspk exceeded maxNspk");
            }
            LMSSignedPubKey[] lMSSignedPubKeyArray = new LMSSignedPubKey[n2];
            if (n2 != 0) {
                for (int i = 0; i < lMSSignedPubKeyArray.length; ++i) {
                    lMSSignedPubKeyArray[i] = new LMSSignedPubKey(LMSSignature.getInstance(object), LMSPublicKeyParameters.getInstance(object));
                }
            }
            LMSSignature lMSSignature = LMSSignature.getInstance(object);
            return new HSSSignature(n2, lMSSignedPubKeyArray, lMSSignature);
        }
        if (object instanceof byte[]) {
            try (InputStream inputStream = null;){
                inputStream = new DataInputStream(new ByteArrayInputStream((byte[])object));
                HSSSignature hSSSignature = HSSSignature.getInstance(inputStream, n);
                return hSSSignature;
            }
        }
        if (object instanceof InputStream) {
            return HSSSignature.getInstance(Streams.readAll((InputStream)object), n);
        }
        throw new IllegalArgumentException("cannot parse " + object);
    }

    public int getlMinus1() {
        return this.lMinus1;
    }

    public LMSSignedPubKey[] getSignedPubKey() {
        return this.signedPubKey;
    }

    public LMSSignature getSignature() {
        return this.signature;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        HSSSignature hSSSignature = (HSSSignature)object;
        return this.lMinus1 == hSSSignature.lMinus1 && Arrays.areEqual(this.signedPubKey, hSSSignature.signedPubKey) && Objects.areEqual(this.signature, hSSSignature.signature);
    }

    public int hashCode() {
        int n = this.lMinus1;
        n = 31 * n + Arrays.hashCode(this.signedPubKey);
        n = 31 * n + Objects.hashCode(this.signature);
        return n;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        Composer composer = Composer.compose();
        composer.u32str(this.lMinus1);
        if (this.signedPubKey != null) {
            for (LMSSignedPubKey lMSSignedPubKey : this.signedPubKey) {
                composer.bytes(lMSSignedPubKey);
            }
        }
        composer.bytes(this.signature);
        return composer.build();
    }
}

