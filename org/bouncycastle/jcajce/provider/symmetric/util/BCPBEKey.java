/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.Destroyable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class BCPBEKey
implements PBEKey,
Destroyable {
    private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
    String algorithm;
    ASN1ObjectIdentifier oid;
    int type;
    int digest;
    int keySize;
    int ivSize;
    private final char[] password;
    private final byte[] salt;
    private final int iterationCount;
    private final CipherParameters param;
    boolean tryWrong = false;

    public BCPBEKey(String string, ASN1ObjectIdentifier aSN1ObjectIdentifier, int n, int n2, int n3, int n4, PBEKeySpec pBEKeySpec, CipherParameters cipherParameters) {
        this.algorithm = string;
        this.oid = aSN1ObjectIdentifier;
        this.type = n;
        this.digest = n2;
        this.keySize = n3;
        this.ivSize = n4;
        this.password = pBEKeySpec.getPassword();
        this.iterationCount = pBEKeySpec.getIterationCount();
        this.salt = pBEKeySpec.getSalt();
        this.param = cipherParameters;
    }

    public BCPBEKey(String string, CipherParameters cipherParameters) {
        this.algorithm = string;
        this.param = cipherParameters;
        this.password = null;
        this.iterationCount = -1;
        this.salt = null;
    }

    @Override
    public String getAlgorithm() {
        String string = this.algorithm;
        BCPBEKey.checkDestroyed(this);
        return string;
    }

    @Override
    public String getFormat() {
        BCPBEKey.checkDestroyed(this);
        return "RAW";
    }

    @Override
    public byte[] getEncoded() {
        byte[] byArray;
        if (this.param != null) {
            KeyParameter keyParameter = this.param instanceof ParametersWithIV ? (KeyParameter)((ParametersWithIV)this.param).getParameters() : (KeyParameter)this.param;
            byArray = keyParameter.getKey();
        } else {
            byArray = this.type == 2 ? PBEParametersGenerator.PKCS12PasswordToBytes(this.password) : (this.type == 5 ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password) : PBEParametersGenerator.PKCS5PasswordToBytes(this.password));
        }
        BCPBEKey.checkDestroyed(this);
        return byArray;
    }

    int getType() {
        int n = this.type;
        BCPBEKey.checkDestroyed(this);
        return n;
    }

    int getDigest() {
        int n = this.digest;
        BCPBEKey.checkDestroyed(this);
        return n;
    }

    int getKeySize() {
        int n = this.keySize;
        BCPBEKey.checkDestroyed(this);
        return n;
    }

    public int getIvSize() {
        int n = this.ivSize;
        BCPBEKey.checkDestroyed(this);
        return n;
    }

    public CipherParameters getParam() {
        CipherParameters cipherParameters = this.param;
        BCPBEKey.checkDestroyed(this);
        return cipherParameters;
    }

    @Override
    public char[] getPassword() {
        char[] cArray = Arrays.clone(this.password);
        BCPBEKey.checkDestroyed(this);
        if (cArray == null) {
            throw new IllegalStateException("no password available");
        }
        return cArray;
    }

    @Override
    public byte[] getSalt() {
        byte[] byArray = Arrays.clone(this.salt);
        BCPBEKey.checkDestroyed(this);
        return byArray;
    }

    @Override
    public int getIterationCount() {
        int n = this.iterationCount;
        BCPBEKey.checkDestroyed(this);
        return n;
    }

    public ASN1ObjectIdentifier getOID() {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.oid;
        BCPBEKey.checkDestroyed(this);
        return aSN1ObjectIdentifier;
    }

    public void setTryWrongPKCS12Zero(boolean bl) {
        this.tryWrong = bl;
    }

    boolean shouldTryWrongPKCS12() {
        return this.tryWrong;
    }

    @Override
    public void destroy() {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            if (this.password != null) {
                Arrays.fill(this.password, '\u0000');
            }
            if (this.salt != null) {
                Arrays.fill(this.salt, (byte)0);
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    static void checkDestroyed(Destroyable destroyable) {
        if (destroyable.isDestroyed()) {
            throw new IllegalStateException("key has been destroyed");
        }
    }
}

