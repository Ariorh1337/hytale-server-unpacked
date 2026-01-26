/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class ConcatKDF
implements JCAAware<JCAContext> {
    private final String jcaHashAlg;
    private final JCAContext jcaContext = new JCAContext();

    public ConcatKDF(String jcaHashAlg) {
        if (jcaHashAlg == null) {
            throw new IllegalArgumentException("The JCA hash algorithm must not be null");
        }
        this.jcaHashAlg = jcaHashAlg;
    }

    public String getHashAlgorithm() {
        return this.jcaHashAlg;
    }

    @Override
    public JCAContext getJCAContext() {
        return this.jcaContext;
    }

    public SecretKey deriveKey(SecretKey sharedSecret, int keyLengthBits, byte[] otherInfo) throws JOSEException {
        int keyLengthBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessageDigest md = this.getMessageDigest();
        for (int i = 1; i <= ConcatKDF.computeDigestCycles(ByteUtils.safeBitLength(md.getDigestLength()), keyLengthBits); ++i) {
            byte[] counterBytes = IntegerUtils.toBytes(i);
            md.update(counterBytes);
            md.update(sharedSecret.getEncoded());
            if (otherInfo != null) {
                md.update(otherInfo);
            }
            try {
                baos.write(md.digest());
                continue;
            }
            catch (IOException e) {
                throw new JOSEException("Couldn't write derived key: " + e.getMessage(), e);
            }
        }
        byte[] derivedKeyMaterial = baos.toByteArray();
        if (derivedKeyMaterial.length == (keyLengthBytes = ByteUtils.byteLength(keyLengthBits))) {
            return new SecretKeySpec(derivedKeyMaterial, "AES");
        }
        return new SecretKeySpec(ByteUtils.subArray(derivedKeyMaterial, 0, keyLengthBytes), "AES");
    }

    public SecretKey deriveKey(SecretKey sharedSecret, int keyLength, byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo) throws JOSEException {
        byte[] otherInfo = ConcatKDF.composeOtherInfo(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo);
        return this.deriveKey(sharedSecret, keyLength, otherInfo);
    }

    public SecretKey deriveKey(SecretKey sharedSecret, int keyLength, byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo, byte[] tag) throws JOSEException {
        byte[] otherInfo = ConcatKDF.composeOtherInfo(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo, tag);
        return this.deriveKey(sharedSecret, keyLength, otherInfo);
    }

    public static byte[] composeOtherInfo(byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo) {
        return ByteUtils.concat(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo);
    }

    public static byte[] composeOtherInfo(byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo, byte[] tag) {
        return ByteUtils.concat(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo, tag);
    }

    private MessageDigest getMessageDigest() throws JOSEException {
        Provider provider = this.getJCAContext().getProvider();
        try {
            if (provider == null) {
                return MessageDigest.getInstance(this.jcaHashAlg);
            }
            return MessageDigest.getInstance(this.jcaHashAlg, provider);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't get message digest for KDF: " + e.getMessage(), e);
        }
    }

    public static int computeDigestCycles(int digestLengthBits, int keyLengthBits) {
        return (keyLengthBits + digestLengthBits - 1) / digestLengthBits;
    }

    public static byte[] encodeNoData() {
        return new byte[0];
    }

    public static byte[] encodeIntData(int data) {
        return IntegerUtils.toBytes(data);
    }

    public static byte[] encodeStringData(String data) {
        byte[] bytes = data != null ? data.getBytes(StandardCharset.UTF_8) : null;
        return ConcatKDF.encodeDataWithLength(bytes);
    }

    public static byte[] encodeDataWithLength(byte[] data) {
        byte[] bytes = data != null ? data : new byte[]{};
        byte[] length = IntegerUtils.toBytes(bytes.length);
        return ByteUtils.concat(length, bytes);
    }

    public static byte[] encodeDataWithLength(Base64URL data) {
        byte[] bytes = data != null ? data.decode() : null;
        return ConcatKDF.encodeDataWithLength(bytes);
    }
}

