/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.its.ETSIKeyWrapper;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

public class JceETSIKeyWrapper
implements ETSIKeyWrapper {
    private final ECPublicKey recipientKey;
    private final byte[] recipientHash;
    private final JcaJceHelper helper;

    private JceETSIKeyWrapper(ECPublicKey eCPublicKey, byte[] byArray, JcaJceHelper jcaJceHelper) {
        this.recipientKey = eCPublicKey;
        this.recipientHash = byArray;
        this.helper = jcaJceHelper;
    }

    @Override
    public EncryptedDataEncryptionKey wrap(byte[] byArray) {
        try {
            Cipher cipher = this.helper.createCipher("ETSIKEMwithSHA256");
            cipher.init(3, (Key)this.recipientKey, new IESKEMParameterSpec(this.recipientHash, true));
            byte[] byArray2 = cipher.wrap(new SecretKeySpec(byArray, "AES"));
            int n = (this.recipientKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
            n = byArray2[0] == 4 ? 2 * n + 1 : ++n;
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(this.recipientKey.getEncoded());
            ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            EciesP256EncryptedKey eciesP256EncryptedKey = EciesP256EncryptedKey.builder().setV(EccP256CurvePoint.createEncodedPoint(Arrays.copyOfRange(byArray2, 0, n))).setC(Arrays.copyOfRange(byArray2, n, n + byArray.length)).setT(Arrays.copyOfRange(byArray2, n + byArray.length, byArray2.length)).createEciesP256EncryptedKey();
            if (aSN1ObjectIdentifier.equals(SECObjectIdentifiers.secp256r1)) {
                return EncryptedDataEncryptionKey.eciesNistP256(eciesP256EncryptedKey);
            }
            if (aSN1ObjectIdentifier.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
                return EncryptedDataEncryptionKey.eciesBrainpoolP256r1(eciesP256EncryptedKey);
            }
            throw new IllegalStateException("recipient key curve is not P-256 or Brainpool P256r1");
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    public static class Builder {
        private final ECPublicKey recipientKey;
        private final byte[] recipientHash;
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder(ECPublicKey eCPublicKey, byte[] byArray) {
            this.recipientKey = eCPublicKey;
            this.recipientHash = byArray;
        }

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String string) {
            this.helper = new NamedJcaJceHelper(string);
            return this;
        }

        public JceETSIKeyWrapper build() {
            return new JceETSIKeyWrapper(this.recipientKey, this.recipientHash, this.helper);
        }
    }
}

