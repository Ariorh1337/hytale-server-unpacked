/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.InputStreamWithMAC;
import org.bouncycastle.cms.jcajce.JceAADStream;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputAEADDecryptor;

class CMSInputAEADDecryptor
implements InputAEADDecryptor {
    private final AlgorithmIdentifier contentEncryptionAlgorithm;
    private final Cipher dataCipher;
    private InputStream inputStream;

    CMSInputAEADDecryptor(AlgorithmIdentifier algorithmIdentifier, Cipher cipher) {
        this.contentEncryptionAlgorithm = algorithmIdentifier;
        this.dataCipher = cipher;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.contentEncryptionAlgorithm;
    }

    @Override
    public InputStream getInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return new CipherInputStream(inputStream, this.dataCipher);
    }

    @Override
    public OutputStream getAADStream() {
        if (CMSInputAEADDecryptor.checkForAEAD()) {
            return new JceAADStream(this.dataCipher);
        }
        return null;
    }

    @Override
    public byte[] getMAC() {
        if (this.inputStream instanceof InputStreamWithMAC) {
            return ((InputStreamWithMAC)this.inputStream).getMAC();
        }
        return null;
    }

    private static boolean checkForAEAD() {
        return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    return Cipher.class.getMethod("updateAAD", byte[].class) != null;
                }
                catch (Exception exception) {
                    return Boolean.FALSE;
                }
            }
        });
    }
}

