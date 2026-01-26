/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl.bc;

import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;
import org.bouncycastle.openssl.bc.PEMUtilities;

public class BcPEMDecryptorProvider
implements PEMDecryptorProvider {
    private final char[] password;

    public BcPEMDecryptorProvider(char[] cArray) {
        this.password = cArray;
    }

    @Override
    public PEMDecryptor get(final String string) {
        return new PEMDecryptor(){
            final /* synthetic */ BcPEMDecryptorProvider this$0;
            {
                this.this$0 = bcPEMDecryptorProvider;
            }

            @Override
            public byte[] decrypt(byte[] byArray, byte[] byArray2) throws PEMException {
                if (this.this$0.password == null) {
                    throw new PasswordException("Password is null, but a password is required");
                }
                return PEMUtilities.crypt(false, byArray, this.this$0.password, string, byArray2);
            }
        };
    }
}

