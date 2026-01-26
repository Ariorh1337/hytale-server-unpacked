/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.hpke.AEAD;
import org.bouncycastle.crypto.hpke.DHKEM;
import org.bouncycastle.crypto.hpke.HKDF;
import org.bouncycastle.crypto.hpke.HPKEContext;
import org.bouncycastle.crypto.hpke.HPKEContextWithEncapsulation;
import org.bouncycastle.crypto.hpke.KEM;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class HPKE {
    public static final byte mode_base = 0;
    public static final byte mode_psk = 1;
    public static final byte mode_auth = 2;
    public static final byte mode_auth_psk = 3;
    public static final short kem_P256_SHA256 = 16;
    public static final short kem_P384_SHA348 = 17;
    public static final short kem_P384_SHA384 = 17;
    public static final short kem_P521_SHA512 = 18;
    public static final short kem_X25519_SHA256 = 32;
    public static final short kem_X448_SHA512 = 33;
    public static final short kdf_HKDF_SHA256 = 1;
    public static final short kdf_HKDF_SHA384 = 2;
    public static final short kdf_HKDF_SHA512 = 3;
    public static final short aead_AES_GCM128 = 1;
    public static final short aead_AES_GCM256 = 2;
    public static final short aead_CHACHA20_POLY1305 = 3;
    public static final short aead_EXPORT_ONLY = -1;
    private final byte[] default_psk = null;
    private final byte[] default_psk_id = null;
    private final byte mode;
    private final short kemId;
    private final short kdfId;
    private final short aeadId;
    private final KEM kem;
    private final HKDF hkdf;
    private final int encSize;
    short Nk;

    public HPKE(byte by, short s, short s2, short s3) {
        this.mode = by;
        this.kemId = s;
        this.kdfId = s2;
        this.aeadId = s3;
        this.hkdf = new HKDF(s2);
        this.kem = new DHKEM(s);
        this.Nk = s3 == 1 ? (short)16 : (short)32;
        this.encSize = this.kem.getEncryptionSize();
    }

    public HPKE(byte by, short s, short s2, short s3, KEM kEM, int n) {
        this.mode = by;
        this.kemId = s;
        this.kdfId = s2;
        this.aeadId = s3;
        this.hkdf = new HKDF(s2);
        this.kem = kEM;
        this.Nk = s3 == 1 ? (short)16 : (short)32;
        this.encSize = n;
    }

    public int getEncSize() {
        return this.encSize;
    }

    public short getAeadId() {
        return this.aeadId;
    }

    private void VerifyPSKInputs(byte by, byte[] byArray, byte[] byArray2) {
        boolean bl;
        boolean bl2 = !Arrays.areEqual(byArray, this.default_psk);
        boolean bl3 = bl = !Arrays.areEqual(byArray2, this.default_psk_id);
        if (bl2 != bl) {
            throw new IllegalArgumentException("Inconsistent PSK inputs");
        }
        if (bl2 && by % 2 == 0) {
            throw new IllegalArgumentException("PSK input provided when not needed");
        }
        if (!bl2 && by % 2 == 1) {
            throw new IllegalArgumentException("Missing required PSK input");
        }
    }

    private HPKEContext keySchedule(byte by, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        this.VerifyPSKInputs(by, byArray3, byArray4);
        byte[] byArray5 = Arrays.concatenate(Strings.toByteArray("HPKE"), Pack.shortToBigEndian(this.kemId), Pack.shortToBigEndian(this.kdfId), Pack.shortToBigEndian(this.aeadId));
        byte[] byArray6 = this.hkdf.LabeledExtract(null, byArray5, "psk_id_hash", byArray4);
        byte[] byArray7 = this.hkdf.LabeledExtract(null, byArray5, "info_hash", byArray2);
        byte[] byArray8 = new byte[]{by};
        byte[] byArray9 = Arrays.concatenate(byArray8, byArray6, byArray7);
        byte[] byArray10 = this.hkdf.LabeledExtract(byArray, byArray5, "secret", byArray3);
        byte[] byArray11 = this.hkdf.LabeledExpand(byArray10, byArray5, "key", byArray9, this.Nk);
        byte[] byArray12 = this.hkdf.LabeledExpand(byArray10, byArray5, "base_nonce", byArray9, 12);
        byte[] byArray13 = this.hkdf.LabeledExpand(byArray10, byArray5, "exp", byArray9, this.hkdf.getHashSize());
        return new HPKEContext(new AEAD(this.aeadId, byArray11, byArray12), this.hkdf, byArray13, byArray5);
    }

    public AsymmetricCipherKeyPair generatePrivateKey() {
        return this.kem.GeneratePrivateKey();
    }

    public byte[] serializePublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.kem.SerializePublicKey(asymmetricKeyParameter);
    }

    public byte[] serializePrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.kem.SerializePrivateKey(asymmetricKeyParameter);
    }

    public AsymmetricKeyParameter deserializePublicKey(byte[] byArray) {
        return this.kem.DeserializePublicKey(byArray);
    }

    public AsymmetricCipherKeyPair deserializePrivateKey(byte[] byArray, byte[] byArray2) {
        return this.kem.DeserializePrivateKey(byArray, byArray2);
    }

    public AsymmetricCipherKeyPair deriveKeyPair(byte[] byArray) {
        return this.kem.DeriveKeyPair(byArray);
    }

    public byte[][] sendExport(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray, byte[] byArray2, int n, byte[] byArray3, byte[] byArray4, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        HPKEContextWithEncapsulation hPKEContextWithEncapsulation;
        byte[][] byArrayArray = new byte[2][];
        switch (this.mode) {
            case 0: {
                hPKEContextWithEncapsulation = this.setupBaseS(asymmetricKeyParameter, byArray);
                break;
            }
            case 2: {
                hPKEContextWithEncapsulation = this.setupAuthS(asymmetricKeyParameter, byArray, asymmetricCipherKeyPair);
                break;
            }
            case 1: {
                hPKEContextWithEncapsulation = this.SetupPSKS(asymmetricKeyParameter, byArray, byArray3, byArray4);
                break;
            }
            case 3: {
                hPKEContextWithEncapsulation = this.setupAuthPSKS(asymmetricKeyParameter, byArray, byArray3, byArray4, asymmetricCipherKeyPair);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        byArrayArray[0] = hPKEContextWithEncapsulation.encapsulation;
        byArrayArray[1] = hPKEContextWithEncapsulation.export(byArray2, n);
        return byArrayArray;
    }

    public byte[] receiveExport(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] byArray2, byte[] byArray3, int n, byte[] byArray4, byte[] byArray5, AsymmetricKeyParameter asymmetricKeyParameter) {
        HPKEContext hPKEContext;
        switch (this.mode) {
            case 0: {
                hPKEContext = this.setupBaseR(byArray, asymmetricCipherKeyPair, byArray2);
                break;
            }
            case 2: {
                hPKEContext = this.setupAuthR(byArray, asymmetricCipherKeyPair, byArray2, asymmetricKeyParameter);
                break;
            }
            case 1: {
                hPKEContext = this.setupPSKR(byArray, asymmetricCipherKeyPair, byArray2, byArray4, byArray5);
                break;
            }
            case 3: {
                hPKEContext = this.setupAuthPSKR(byArray, asymmetricCipherKeyPair, byArray2, byArray4, byArray5, asymmetricKeyParameter);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        return hPKEContext.export(byArray3, n);
    }

    public byte[][] seal(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, AsymmetricCipherKeyPair asymmetricCipherKeyPair) throws InvalidCipherTextException {
        HPKEContextWithEncapsulation hPKEContextWithEncapsulation;
        byte[][] byArrayArray = new byte[2][];
        switch (this.mode) {
            case 0: {
                hPKEContextWithEncapsulation = this.setupBaseS(asymmetricKeyParameter, byArray);
                break;
            }
            case 2: {
                hPKEContextWithEncapsulation = this.setupAuthS(asymmetricKeyParameter, byArray, asymmetricCipherKeyPair);
                break;
            }
            case 1: {
                hPKEContextWithEncapsulation = this.SetupPSKS(asymmetricKeyParameter, byArray, byArray4, byArray5);
                break;
            }
            case 3: {
                hPKEContextWithEncapsulation = this.setupAuthPSKS(asymmetricKeyParameter, byArray, byArray4, byArray5, asymmetricCipherKeyPair);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        byArrayArray[0] = hPKEContextWithEncapsulation.seal(byArray2, byArray3);
        byArrayArray[1] = hPKEContextWithEncapsulation.getEncapsulation();
        return byArrayArray;
    }

    public byte[] open(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, AsymmetricKeyParameter asymmetricKeyParameter) throws InvalidCipherTextException {
        HPKEContext hPKEContext;
        switch (this.mode) {
            case 0: {
                hPKEContext = this.setupBaseR(byArray, asymmetricCipherKeyPair, byArray2);
                break;
            }
            case 2: {
                hPKEContext = this.setupAuthR(byArray, asymmetricCipherKeyPair, byArray2, asymmetricKeyParameter);
                break;
            }
            case 1: {
                hPKEContext = this.setupPSKR(byArray, asymmetricCipherKeyPair, byArray2, byArray5, byArray6);
                break;
            }
            case 3: {
                hPKEContext = this.setupAuthPSKR(byArray, asymmetricCipherKeyPair, byArray2, byArray5, byArray6, asymmetricKeyParameter);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        return hPKEContext.open(byArray3, byArray4);
    }

    public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray) {
        byte[][] byArray2 = this.kem.Encap(asymmetricKeyParameter);
        HPKEContext hPKEContext = this.keySchedule((byte)0, byArray2[0], byArray, this.default_psk, this.default_psk_id);
        return new HPKEContextWithEncapsulation(hPKEContext, byArray2[1]);
    }

    public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] byArray2 = this.kem.Encap(asymmetricKeyParameter, asymmetricCipherKeyPair);
        HPKEContext hPKEContext = this.keySchedule((byte)0, byArray2[0], byArray, this.default_psk, this.default_psk_id);
        return new HPKEContextWithEncapsulation(hPKEContext, byArray2[1]);
    }

    public HPKEContext setupBaseR(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] byArray2) {
        byte[] byArray3 = this.kem.Decap(byArray, asymmetricCipherKeyPair);
        return this.keySchedule((byte)0, byArray3, byArray2, this.default_psk, this.default_psk_id);
    }

    public HPKEContextWithEncapsulation SetupPSKS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        byte[][] byArray4 = this.kem.Encap(asymmetricKeyParameter);
        HPKEContext hPKEContext = this.keySchedule((byte)1, byArray4[0], byArray, byArray2, byArray3);
        return new HPKEContextWithEncapsulation(hPKEContext, byArray4[1]);
    }

    public HPKEContext setupPSKR(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        byte[] byArray5 = this.kem.Decap(byArray, asymmetricCipherKeyPair);
        return this.keySchedule((byte)1, byArray5, byArray2, byArray3, byArray4);
    }

    public HPKEContextWithEncapsulation setupAuthS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] byArray2 = this.kem.AuthEncap(asymmetricKeyParameter, asymmetricCipherKeyPair);
        HPKEContext hPKEContext = this.keySchedule((byte)2, byArray2[0], byArray, this.default_psk, this.default_psk_id);
        return new HPKEContextWithEncapsulation(hPKEContext, byArray2[1]);
    }

    public HPKEContext setupAuthR(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] byArray2, AsymmetricKeyParameter asymmetricKeyParameter) {
        byte[] byArray3 = this.kem.AuthDecap(byArray, asymmetricCipherKeyPair, asymmetricKeyParameter);
        return this.keySchedule((byte)2, byArray3, byArray2, this.default_psk, this.default_psk_id);
    }

    public HPKEContextWithEncapsulation setupAuthPSKS(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray, byte[] byArray2, byte[] byArray3, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] byArray4 = this.kem.AuthEncap(asymmetricKeyParameter, asymmetricCipherKeyPair);
        HPKEContext hPKEContext = this.keySchedule((byte)3, byArray4[0], byArray, byArray2, byArray3);
        return new HPKEContextWithEncapsulation(hPKEContext, byArray4[1]);
    }

    public HPKEContext setupAuthPSKR(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, byte[] byArray2, byte[] byArray3, byte[] byArray4, AsymmetricKeyParameter asymmetricKeyParameter) {
        byte[] byArray5 = this.kem.AuthDecap(byArray, asymmetricCipherKeyPair, asymmetricKeyParameter);
        return this.keySchedule((byte)3, byArray5, byArray2, byArray3, byArray4);
    }
}

