/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.PBEParametersGenerator;

public enum PasswordConverter implements CharToByteConverter
{
    ASCII{

        @Override
        public String getType() {
            return "ASCII";
        }

        @Override
        public byte[] convert(char[] cArray) {
            return PBEParametersGenerator.PKCS5PasswordToBytes(cArray);
        }
    }
    ,
    UTF8{

        @Override
        public String getType() {
            return "UTF8";
        }

        @Override
        public byte[] convert(char[] cArray) {
            return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(cArray);
        }
    }
    ,
    PKCS12{

        @Override
        public String getType() {
            return "PKCS12";
        }

        @Override
        public byte[] convert(char[] cArray) {
            return PBEParametersGenerator.PKCS12PasswordToBytes(cArray);
        }
    };

}

