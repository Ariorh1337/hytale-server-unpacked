/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import java.security.SecureRandom;
import java.util.logging.Logger;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.raw.Bits;
import org.bouncycastle.pqc.crypto.picnic.KMatricesWithPointer;
import org.bouncycastle.pqc.crypto.picnic.LowmcConstants;
import org.bouncycastle.pqc.crypto.picnic.Msg;
import org.bouncycastle.pqc.crypto.picnic.Signature;
import org.bouncycastle.pqc.crypto.picnic.Signature2;
import org.bouncycastle.pqc.crypto.picnic.Tape;
import org.bouncycastle.pqc.crypto.picnic.Tree;
import org.bouncycastle.pqc.crypto.picnic.Utils;
import org.bouncycastle.pqc.crypto.picnic.View;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class PicnicEngine {
    private static final Logger LOG = Logger.getLogger(PicnicEngine.class.getName());
    protected static final int saltSizeBytes = 32;
    private static final int MAX_DIGEST_SIZE = 64;
    private static final int WORD_SIZE_BITS = 32;
    private static final int LOWMC_MAX_STATE_SIZE = 64;
    protected static final int LOWMC_MAX_WORDS = 16;
    protected static final int LOWMC_MAX_KEY_BITS = 256;
    protected static final int LOWMC_MAX_AND_GATES = 1144;
    private static final int MAX_AUX_BYTES = 176;
    private static final int PICNIC_MAX_LOWMC_BLOCK_SIZE = 32;
    private static final int TRANSFORM_FS = 0;
    private static final int TRANSFORM_UR = 1;
    private static final int TRANSFORM_INVALID = 255;
    private final int CRYPTO_SECRETKEYBYTES;
    private final int CRYPTO_PUBLICKEYBYTES;
    private final int CRYPTO_BYTES;
    protected final int numRounds;
    protected final int numSboxes;
    protected final int stateSizeBits;
    protected final int stateSizeBytes;
    protected final int stateSizeWords;
    protected final int andSizeBytes;
    protected final int UnruhGWithoutInputBytes;
    protected final int UnruhGWithInputBytes;
    protected final int numMPCRounds;
    protected final int numOpenedRounds;
    protected final int numMPCParties;
    protected final int seedSizeBytes;
    protected final int digestSizeBytes;
    protected final int pqSecurityLevel;
    protected final Xof digest;
    private final int transform;
    private final int parameters;
    private int signatureLength;
    protected final LowmcConstants lowmcConstants;

    public int getSecretKeySize() {
        return this.CRYPTO_SECRETKEYBYTES;
    }

    public int getPublicKeySize() {
        return this.CRYPTO_PUBLICKEYBYTES;
    }

    public int getSignatureSize(int n) {
        return this.CRYPTO_BYTES + n;
    }

    public int getTrueSignatureSize() {
        return this.signatureLength;
    }

    PicnicEngine(int n, LowmcConstants lowmcConstants) {
        this.lowmcConstants = lowmcConstants;
        this.parameters = n;
        switch (this.parameters) {
            case 1: 
            case 2: {
                this.pqSecurityLevel = 64;
                this.stateSizeBits = 128;
                this.numMPCRounds = 219;
                this.numMPCParties = 3;
                this.numSboxes = 10;
                this.numRounds = 20;
                this.digestSizeBytes = 32;
                this.numOpenedRounds = 0;
                break;
            }
            case 3: 
            case 4: {
                this.pqSecurityLevel = 96;
                this.stateSizeBits = 192;
                this.numMPCRounds = 329;
                this.numMPCParties = 3;
                this.numSboxes = 10;
                this.numRounds = 30;
                this.digestSizeBytes = 48;
                this.numOpenedRounds = 0;
                break;
            }
            case 5: 
            case 6: {
                this.pqSecurityLevel = 128;
                this.stateSizeBits = 256;
                this.numMPCRounds = 438;
                this.numMPCParties = 3;
                this.numSboxes = 10;
                this.numRounds = 38;
                this.digestSizeBytes = 64;
                this.numOpenedRounds = 0;
                break;
            }
            case 7: {
                this.pqSecurityLevel = 64;
                this.stateSizeBits = 129;
                this.numMPCRounds = 250;
                this.numOpenedRounds = 36;
                this.numMPCParties = 16;
                this.numSboxes = 43;
                this.numRounds = 4;
                this.digestSizeBytes = 32;
                break;
            }
            case 8: {
                this.pqSecurityLevel = 96;
                this.stateSizeBits = 192;
                this.numMPCRounds = 419;
                this.numOpenedRounds = 52;
                this.numMPCParties = 16;
                this.numSboxes = 64;
                this.numRounds = 4;
                this.digestSizeBytes = 48;
                break;
            }
            case 9: {
                this.pqSecurityLevel = 128;
                this.stateSizeBits = 255;
                this.numMPCRounds = 601;
                this.numOpenedRounds = 68;
                this.numMPCParties = 16;
                this.numSboxes = 85;
                this.numRounds = 4;
                this.digestSizeBytes = 64;
                break;
            }
            case 10: {
                this.pqSecurityLevel = 64;
                this.stateSizeBits = 129;
                this.numMPCRounds = 219;
                this.numMPCParties = 3;
                this.numSboxes = 43;
                this.numRounds = 4;
                this.digestSizeBytes = 32;
                this.numOpenedRounds = 0;
                break;
            }
            case 11: {
                this.pqSecurityLevel = 96;
                this.stateSizeBits = 192;
                this.numMPCRounds = 329;
                this.numMPCParties = 3;
                this.numSboxes = 64;
                this.numRounds = 4;
                this.digestSizeBytes = 48;
                this.numOpenedRounds = 0;
                break;
            }
            case 12: {
                this.pqSecurityLevel = 128;
                this.stateSizeBits = 255;
                this.numMPCRounds = 438;
                this.numMPCParties = 3;
                this.numSboxes = 85;
                this.numRounds = 4;
                this.digestSizeBytes = 64;
                this.numOpenedRounds = 0;
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown parameter set " + this.parameters);
            }
        }
        switch (this.parameters) {
            case 1: {
                this.CRYPTO_SECRETKEYBYTES = 49;
                this.CRYPTO_PUBLICKEYBYTES = 33;
                this.CRYPTO_BYTES = 34036;
                break;
            }
            case 2: {
                this.CRYPTO_SECRETKEYBYTES = 49;
                this.CRYPTO_PUBLICKEYBYTES = 33;
                this.CRYPTO_BYTES = 53965;
                break;
            }
            case 3: {
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 76784;
                break;
            }
            case 4: {
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 121857;
                break;
            }
            case 5: {
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 132876;
                break;
            }
            case 6: {
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 209526;
                break;
            }
            case 7: {
                this.CRYPTO_SECRETKEYBYTES = 52;
                this.CRYPTO_PUBLICKEYBYTES = 35;
                this.CRYPTO_BYTES = 14612;
                break;
            }
            case 8: {
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 35028;
                break;
            }
            case 9: {
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 61028;
                break;
            }
            case 10: {
                this.CRYPTO_SECRETKEYBYTES = 52;
                this.CRYPTO_PUBLICKEYBYTES = 35;
                this.CRYPTO_BYTES = 32061;
                break;
            }
            case 11: {
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 71179;
                break;
            }
            case 12: {
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 126286;
                break;
            }
            default: {
                this.CRYPTO_SECRETKEYBYTES = -1;
                this.CRYPTO_PUBLICKEYBYTES = -1;
                this.CRYPTO_BYTES = -1;
            }
        }
        this.andSizeBytes = Utils.numBytes(this.numSboxes * 3 * this.numRounds);
        this.stateSizeBytes = Utils.numBytes(this.stateSizeBits);
        this.seedSizeBytes = Utils.numBytes(2 * this.pqSecurityLevel);
        this.stateSizeWords = (this.stateSizeBits + 32 - 1) / 32;
        switch (this.parameters) {
            case 1: 
            case 3: 
            case 5: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                this.transform = 0;
                break;
            }
            case 2: 
            case 4: 
            case 6: {
                this.transform = 1;
                break;
            }
            default: {
                this.transform = 255;
            }
        }
        if (this.transform == 1) {
            this.UnruhGWithoutInputBytes = this.seedSizeBytes + this.andSizeBytes;
            this.UnruhGWithInputBytes = this.UnruhGWithoutInputBytes + this.stateSizeBytes;
        } else {
            this.UnruhGWithoutInputBytes = 0;
            this.UnruhGWithInputBytes = 0;
        }
        this.digest = this.stateSizeBits == 128 || this.stateSizeBits == 129 ? new SHAKEDigest(128) : new SHAKEDigest(256);
    }

    public boolean crypto_sign_open(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n = Pack.littleEndianToInt(byArray2, 0);
        byte[] byArray4 = Arrays.copyOfRange(byArray2, 4, 4 + byArray.length);
        int n2 = this.picnic_verify(byArray3, byArray4, byArray2, n);
        if (n2 == -1) {
            return false;
        }
        System.arraycopy(byArray2, 4, byArray, 0, byArray.length);
        return true;
    }

    private int picnic_verify(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        int[] nArray = new int[this.stateSizeWords];
        int[] nArray2 = new int[this.stateSizeWords];
        this.picnic_read_public_key(nArray, nArray2, byArray);
        if (PicnicEngine.is_picnic3(this.parameters)) {
            Signature2 signature2 = new Signature2(this);
            int n2 = this.deserializeSignature2(signature2, byArray3, n, byArray2.length + 4);
            if (n2 != 0) {
                LOG.fine("Error couldn't deserialize signature (2)!");
                return -1;
            }
            return this.verify_picnic3(signature2, nArray, nArray2, byArray2);
        }
        Signature signature = new Signature(this);
        int n3 = this.deserializeSignature(signature, byArray3, n, byArray2.length + 4);
        if (n3 != 0) {
            LOG.fine("Error couldn't deserialize signature!");
            return -1;
        }
        return this.verify(signature, nArray, nArray2, byArray2);
    }

    private int verify(Signature signature, int[] nArray, int[] nArray2, byte[] byArray) {
        byte[][][] byArray2 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        byte[][][] byArray3 = new byte[this.numMPCRounds][3][this.UnruhGWithInputBytes];
        int[][][] nArray3 = new int[this.numMPCRounds][3][this.stateSizeBytes];
        Signature.Proof[] proofArray = signature.proofs;
        byte[] byArray4 = signature.challengeBits;
        int n = 0;
        byte[] byArray5 = null;
        byte[] byArray6 = new byte[Math.max(6 * this.stateSizeBytes, this.stateSizeBytes + this.andSizeBytes)];
        Tape tape = new Tape(this);
        View[] viewArray = new View[this.numMPCRounds];
        View[] viewArray2 = new View[this.numMPCRounds];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            viewArray[i] = new View(this);
            viewArray2[i] = new View(this);
            if (!this.verifyProof(proofArray[i], viewArray[i], viewArray2[i], this.getChallenge(byArray4, i), signature.salt, i, byArray6, nArray2, tape)) {
                LOG.fine("Invalid signature. Did not verify");
                return -1;
            }
            int n2 = this.getChallenge(byArray4, i);
            this.Commit(proofArray[i].seed1, 0, viewArray[i], byArray2[i][n2]);
            this.Commit(proofArray[i].seed2, 0, viewArray2[i], byArray2[i][(n2 + 1) % 3]);
            System.arraycopy(proofArray[i].view3Commitment, 0, byArray2[i][(n2 + 2) % 3], 0, this.digestSizeBytes);
            if (this.transform == 1) {
                this.G(n2, proofArray[i].seed1, 0, viewArray[i], byArray3[i][n2]);
                this.G((n2 + 1) % 3, proofArray[i].seed2, 0, viewArray2[i], byArray3[i][(n2 + 1) % 3]);
                int n3 = n2 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                System.arraycopy(proofArray[i].view3UnruhG, 0, byArray3[i][(n2 + 2) % 3], 0, n3);
            }
            nArray3[i][n2] = viewArray[i].outputShare;
            nArray3[i][(n2 + 1) % 3] = viewArray2[i].outputShare;
            int[] nArray4 = new int[this.stateSizeWords];
            this.xor_three(nArray4, viewArray[i].outputShare, viewArray2[i].outputShare, nArray);
            nArray3[i][(n2 + 2) % 3] = nArray4;
        }
        byArray5 = new byte[Utils.numBytes(2 * this.numMPCRounds)];
        this.H3(nArray, nArray2, nArray3, byArray2, byArray5, signature.salt, byArray, byArray3);
        if (!PicnicEngine.subarrayEquals(byArray4, byArray5, Utils.numBytes(2 * this.numMPCRounds))) {
            LOG.fine("Invalid signature. Did not verify");
            n = -1;
        }
        return n;
    }

    boolean verifyProof(Signature.Proof proof, View view, View view2, int n, byte[] byArray, int n2, byte[] byArray2, int[] nArray, Tape tape) {
        System.arraycopy(proof.communicatedBits, 0, view2.communicatedBits, 0, this.andSizeBytes);
        tape.pos = 0;
        boolean bl = false;
        switch (n) {
            case 0: {
                bl = this.createRandomTape(proof.seed1, 0, byArray, n2, 0, byArray2, this.stateSizeBytes + this.andSizeBytes);
                Pack.littleEndianToInt(byArray2, 0, view.inputShare);
                System.arraycopy(byArray2, this.stateSizeBytes, tape.tapes[0], 0, this.andSizeBytes);
                boolean bl2 = bl = bl && this.createRandomTape(proof.seed2, 0, byArray, n2, 1, byArray2, this.stateSizeBytes + this.andSizeBytes);
                if (!bl) break;
                Pack.littleEndianToInt(byArray2, 0, view2.inputShare);
                System.arraycopy(byArray2, this.stateSizeBytes, tape.tapes[1], 0, this.andSizeBytes);
                break;
            }
            case 1: {
                bl = this.createRandomTape(proof.seed1, 0, byArray, n2, 1, byArray2, this.stateSizeBytes + this.andSizeBytes);
                Pack.littleEndianToInt(byArray2, 0, view.inputShare);
                System.arraycopy(byArray2, this.stateSizeBytes, tape.tapes[0], 0, this.andSizeBytes);
                boolean bl3 = bl = bl && this.createRandomTape(proof.seed2, 0, byArray, n2, 2, tape.tapes[1], this.andSizeBytes);
                if (!bl) break;
                System.arraycopy(proof.inputShare, 0, view2.inputShare, 0, this.stateSizeWords);
                break;
            }
            case 2: {
                bl = this.createRandomTape(proof.seed1, 0, byArray, n2, 2, tape.tapes[0], this.andSizeBytes);
                System.arraycopy(proof.inputShare, 0, view.inputShare, 0, this.stateSizeWords);
                boolean bl4 = bl = bl && this.createRandomTape(proof.seed2, 0, byArray, n2, 0, byArray2, this.stateSizeBytes + this.andSizeBytes);
                if (!bl) break;
                Pack.littleEndianToInt(byArray2, 0, view2.inputShare);
                System.arraycopy(byArray2, this.stateSizeBytes, tape.tapes[1], 0, this.andSizeBytes);
                break;
            }
            default: {
                LOG.fine("Invalid Challenge!");
            }
        }
        if (!bl) {
            LOG.fine("Failed to generate random tapes, signature verification will fail (but signature may actually be valid)");
            return false;
        }
        Utils.zeroTrailingBits(view.inputShare, this.stateSizeBits);
        Utils.zeroTrailingBits(view2.inputShare, this.stateSizeBits);
        int[] nArray2 = Pack.littleEndianToInt(byArray2, 0, byArray2.length / 4);
        this.mpc_LowMC_verify(view, view2, tape, nArray2, nArray, n);
        return true;
    }

    void mpc_LowMC_verify(View view, View view2, Tape tape, int[] nArray, int[] nArray2, int n) {
        Arrays.fill(nArray, 0, nArray.length, 0);
        this.mpc_xor_constant_verify(nArray, nArray2, 0, this.stateSizeWords, n);
        KMatricesWithPointer kMatricesWithPointer = this.lowmcConstants.KMatrix(this, 0);
        this.matrix_mul_offset(nArray, 0, view.inputShare, 0, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
        this.matrix_mul_offset(nArray, this.stateSizeWords, view2.inputShare, 0, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
        this.mpc_xor(nArray, nArray, 2);
        for (int i = 1; i <= this.numRounds; ++i) {
            kMatricesWithPointer = this.lowmcConstants.KMatrix(this, i);
            this.matrix_mul_offset(nArray, 0, view.inputShare, 0, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            this.matrix_mul_offset(nArray, this.stateSizeWords, view2.inputShare, 0, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            this.mpc_substitution_verify(nArray, tape, view, view2);
            kMatricesWithPointer = this.lowmcConstants.LMatrix(this, i - 1);
            this.mpc_matrix_mul(nArray, 2 * this.stateSizeWords, nArray, 2 * this.stateSizeWords, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer(), 2);
            kMatricesWithPointer = this.lowmcConstants.RConstant(this, i - 1);
            this.mpc_xor_constant_verify(nArray, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer(), this.stateSizeWords, n);
            this.mpc_xor(nArray, nArray, 2);
        }
        System.arraycopy(nArray, 2 * this.stateSizeWords, view.outputShare, 0, this.stateSizeWords);
        System.arraycopy(nArray, 3 * this.stateSizeWords, view2.outputShare, 0, this.stateSizeWords);
    }

    void mpc_substitution_verify(int[] nArray, Tape tape, View view, View view2) {
        int[] nArray2 = new int[2];
        int[] nArray3 = new int[2];
        int[] nArray4 = new int[2];
        int[] nArray5 = new int[2];
        int[] nArray6 = new int[2];
        int[] nArray7 = new int[2];
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int n;
            int n2;
            for (n2 = 0; n2 < 2; ++n2) {
                n = (2 + n2) * this.stateSizeWords * 32;
                nArray2[n2] = Utils.getBitFromWordArray(nArray, n + i + 2);
                nArray3[n2] = Utils.getBitFromWordArray(nArray, n + i + 1);
                nArray4[n2] = Utils.getBitFromWordArray(nArray, n + i);
            }
            this.mpc_AND_verify(nArray2, nArray3, nArray5, tape, view, view2);
            this.mpc_AND_verify(nArray3, nArray4, nArray6, tape, view, view2);
            this.mpc_AND_verify(nArray4, nArray2, nArray7, tape, view, view2);
            for (n2 = 0; n2 < 2; ++n2) {
                n = (2 + n2) * this.stateSizeWords * 32;
                Utils.setBitInWordArray(nArray, n + i + 2, nArray2[n2] ^ nArray6[n2]);
                Utils.setBitInWordArray(nArray, n + i + 1, nArray2[n2] ^ nArray3[n2] ^ nArray7[n2]);
                Utils.setBitInWordArray(nArray, n + i, nArray2[n2] ^ nArray3[n2] ^ nArray4[n2] ^ nArray5[n2]);
            }
        }
    }

    void mpc_AND_verify(int[] nArray, int[] nArray2, int[] nArray3, Tape tape, View view, View view2) {
        byte by = Utils.getBit(tape.tapes[0], tape.pos);
        byte by2 = Utils.getBit(tape.tapes[1], tape.pos);
        int n = nArray[0];
        int n2 = nArray[1];
        int n3 = nArray2[0];
        int n4 = nArray2[1];
        nArray3[0] = n & n4 ^ n2 & n3 ^ n & n3 ^ by ^ by2;
        Utils.setBit(view.communicatedBits, tape.pos, (byte)nArray3[0]);
        nArray3[1] = Utils.getBit(view2.communicatedBits, tape.pos);
        ++tape.pos;
    }

    private void mpc_xor_constant_verify(int[] nArray, int[] nArray2, int n, int n2, int n3) {
        int n4 = 0;
        if (n3 == 0) {
            n4 = 2 * this.stateSizeWords;
        } else if (n3 == 2) {
            n4 = 3 * this.stateSizeWords;
        } else {
            return;
        }
        for (int i = 0; i < n2; ++i) {
            int n5 = i + n4;
            nArray[n5] = nArray[n5] ^ nArray2[i + n];
        }
    }

    private int deserializeSignature(Signature signature, byte[] byArray, int n, int n2) {
        Signature.Proof[] proofArray = signature.proofs;
        byte[] byArray2 = signature.challengeBits;
        int n3 = Utils.numBytes(2 * this.numMPCRounds);
        if (n < n3) {
            return -1;
        }
        int n4 = this.countNonZeroChallenges(byArray, n2);
        if (n4 < 0) {
            return -1;
        }
        int n5 = n4 * this.stateSizeBytes;
        int n6 = n3 + 32 + this.numMPCRounds * (2 * this.seedSizeBytes + this.andSizeBytes + this.digestSizeBytes) + n5;
        if (this.transform == 1) {
            n6 += this.UnruhGWithInputBytes * (this.numMPCRounds - n4);
            n6 += this.UnruhGWithoutInputBytes * n4;
        }
        if (n != n6) {
            LOG.fine("sigBytesLen = " + n + ", expected bytesRequired = " + n6);
            return -1;
        }
        System.arraycopy(byArray, n2, byArray2, 0, n3);
        System.arraycopy(byArray, n2 += n3, signature.salt, 0, 32);
        n2 += 32;
        for (int i = 0; i < this.numMPCRounds; ++i) {
            int n7 = this.getChallenge(byArray2, i);
            System.arraycopy(byArray, n2, proofArray[i].view3Commitment, 0, this.digestSizeBytes);
            n2 += this.digestSizeBytes;
            if (this.transform == 1) {
                int n8 = n7 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                System.arraycopy(byArray, n2, proofArray[i].view3UnruhG, 0, n8);
                n2 += n8;
            }
            System.arraycopy(byArray, n2, proofArray[i].communicatedBits, 0, this.andSizeBytes);
            System.arraycopy(byArray, n2 += this.andSizeBytes, proofArray[i].seed1, 0, this.seedSizeBytes);
            System.arraycopy(byArray, n2 += this.seedSizeBytes, proofArray[i].seed2, 0, this.seedSizeBytes);
            n2 += this.seedSizeBytes;
            if (n7 != 1 && n7 != 2) continue;
            Pack.littleEndianToInt(byArray, n2, proofArray[i].inputShare, 0, this.stateSizeBytes / 4);
            if (this.stateSizeBits == 129) {
                proofArray[i].inputShare[this.stateSizeWords - 1] = byArray[n2 + this.stateSizeBytes - 1] & 0xFF;
            }
            n2 += this.stateSizeBytes;
            if (this.arePaddingBitsZero(proofArray[i].inputShare, this.stateSizeBits)) continue;
            return -1;
        }
        return 0;
    }

    private int countNonZeroChallenges(byte[] byArray, int n) {
        int n2;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        while (n5 + 16 <= this.numMPCRounds) {
            n2 = Pack.littleEndianToInt(byArray, n + (n5 >>> 2));
            n4 |= n2 & n2 >>> 1;
            n3 += Integers.bitCount((n2 ^ n2 >>> 1) & 0x55555555);
            n5 += 16;
        }
        n2 = (this.numMPCRounds - n5) * 2;
        if (n2 > 0) {
            int n6 = (n2 + 7) / 8;
            int n7 = Pack.littleEndianToInt_Low(byArray, n + (n5 >>> 2), n6);
            n4 |= (n7 &= Utils.getTrailingBitsMask(n2)) & n7 >>> 1;
            n3 += Integers.bitCount((n7 ^ n7 >>> 1) & 0x55555555);
        }
        return (n4 & 0x55555555) == 0 ? n3 : -1;
    }

    private void picnic_read_public_key(int[] nArray, int[] nArray2, byte[] byArray) {
        int n = 1;
        int n2 = 1 + this.stateSizeBytes;
        int n3 = this.stateSizeBytes / 4;
        Pack.littleEndianToInt(byArray, n, nArray, 0, n3);
        Pack.littleEndianToInt(byArray, n2, nArray2, 0, n3);
        if (n3 < this.stateSizeWords) {
            int n4 = n3 * 4;
            int n5 = this.stateSizeBytes - n4;
            nArray[n3] = Pack.littleEndianToInt_Low(byArray, n + n4, n5);
            nArray2[n3] = Pack.littleEndianToInt_Low(byArray, n2 + n4, n5);
        }
    }

    private int verify_picnic3(Signature2 signature2, int[] nArray, int[] nArray2, byte[] byArray) {
        int n;
        int n2;
        int n3;
        int n4;
        byte[][][] byArray2 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        byte[][] byArray3 = new byte[this.numMPCRounds][this.digestSizeBytes];
        byte[][] byArray4 = new byte[this.numMPCRounds][this.digestSizeBytes];
        Msg[] msgArray = new Msg[this.numMPCRounds];
        Tree tree = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
        byte[] byArray5 = new byte[64];
        Tree[] treeArray = new Tree[this.numMPCRounds];
        Tape[] tapeArray = new Tape[this.numMPCRounds];
        Tree tree2 = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
        int n5 = tree2.reconstructSeeds(signature2.challengeC, this.numOpenedRounds, signature2.iSeedInfo, signature2.iSeedInfoLen, signature2.salt, 0);
        if (n5 != 0) {
            return -1;
        }
        for (n4 = 0; n4 < this.numMPCRounds; ++n4) {
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, n4)) {
                treeArray[n4] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
                treeArray[n4].generateSeeds(tree2.getLeaf(n4), signature2.salt, n4);
                continue;
            }
            treeArray[n4] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
            int n6 = PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n4);
            int[] nArray3 = new int[]{signature2.challengeP[n6]};
            n5 = treeArray[n4].reconstructSeeds(nArray3, 1, signature2.proofs[n4].seedInfo, signature2.proofs[n4].seedInfoLen, signature2.salt, n4);
            if (n5 == 0) continue;
            LOG.fine("Failed to reconstruct seeds for round " + n4);
            return -1;
        }
        n4 = this.numMPCParties - 1;
        byte[] byArray6 = new byte[176];
        for (n3 = 0; n3 < this.numMPCRounds; ++n3) {
            tapeArray[n3] = new Tape(this);
            this.createRandomTapes(tapeArray[n3], treeArray[n3].getLeaves(), treeArray[n3].getLeavesOffset(), signature2.salt, n3);
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, n3)) {
                tapeArray[n3].computeAuxTape(null);
                for (n2 = 0; n2 < n4; ++n2) {
                    this.commit(byArray2[n3][n2], treeArray[n3].getLeaf(n2), null, signature2.salt, n3, n2);
                }
                this.getAuxBits(byArray6, tapeArray[n3]);
                this.commit(byArray2[n3][n4], treeArray[n3].getLeaf(n4), byArray6, signature2.salt, n3, n4);
                continue;
            }
            n2 = signature2.challengeP[PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n3)];
            for (n = 0; n < n4; ++n) {
                if (n == n2) continue;
                this.commit(byArray2[n3][n], treeArray[n3].getLeaf(n), null, signature2.salt, n3, n);
            }
            if (n4 != n2) {
                this.commit(byArray2[n3][n4], treeArray[n3].getLeaf(n4), signature2.proofs[n3].aux, signature2.salt, n3, n4);
            }
            System.arraycopy(signature2.proofs[n3].C, 0, byArray2[n3][n2], 0, this.digestSizeBytes);
        }
        for (n3 = 0; n3 < this.numMPCRounds; ++n3) {
            this.commit_h(byArray3[n3], byArray2[n3]);
        }
        int[] nArray4 = new int[this.stateSizeBits];
        for (n2 = 0; n2 < this.numMPCRounds; ++n2) {
            msgArray[n2] = new Msg(this);
            if (this.contains(signature2.challengeC, this.numOpenedRounds, n2)) {
                n = signature2.challengeP[PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n2)];
                if (n != n4) {
                    tapeArray[n2].setAuxBits(signature2.proofs[n2].aux);
                }
                System.arraycopy(signature2.proofs[n2].msgs, 0, msgArray[n2].msgs[n], 0, this.andSizeBytes);
                Arrays.fill(tapeArray[n2].tapes[n], (byte)0);
                msgArray[n2].unopened = n;
                byte[] byArray7 = new byte[this.stateSizeWords * 4];
                System.arraycopy(signature2.proofs[n2].input, 0, byArray7, 0, signature2.proofs[n2].input.length);
                int[] nArray5 = new int[this.stateSizeWords];
                Pack.littleEndianToInt(byArray7, 0, nArray5, 0, this.stateSizeWords);
                int n7 = this.simulateOnline(nArray5, tapeArray[n2], nArray4, msgArray[n2], nArray2, nArray);
                if (n7 != 0) {
                    LOG.fine("MPC simulation failed for round " + n2 + ", signature invalid");
                    return -1;
                }
                this.commit_v(byArray4[n2], signature2.proofs[n2].input, msgArray[n2]);
                continue;
            }
            byArray4[n2] = null;
        }
        n2 = this.numMPCRounds - this.numOpenedRounds;
        int[] nArray6 = this.getMissingLeavesList(signature2.challengeC);
        n5 = tree.addMerkleNodes(nArray6, n2, signature2.cvInfo, signature2.cvInfoLen);
        if (n5 != 0) {
            return -1;
        }
        n5 = tree.verifyMerkleTree(byArray4, signature2.salt);
        if (n5 != 0) {
            return -1;
        }
        this.HCP(byArray5, null, null, byArray3, tree.nodes[0], signature2.salt, nArray, nArray2, byArray);
        if (!PicnicEngine.subarrayEquals(signature2.challengeHash, byArray5, this.digestSizeBytes)) {
            LOG.fine("Challenge does not match, signature invalid");
            return -1;
        }
        return n5;
    }

    private int deserializeSignature2(Signature2 signature2, byte[] byArray, int n, int n2) {
        int n3;
        int n4;
        int n5 = this.digestSizeBytes + 32;
        if (byArray.length < n5) {
            return -1;
        }
        System.arraycopy(byArray, n2, signature2.challengeHash, 0, this.digestSizeBytes);
        System.arraycopy(byArray, n2 += this.digestSizeBytes, signature2.salt, 0, 32);
        n2 += 32;
        this.expandChallengeHash(signature2.challengeHash, signature2.challengeC, signature2.challengeP);
        Tree tree = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
        signature2.iSeedInfoLen = tree.revealSeedsSize(signature2.challengeC, this.numOpenedRounds);
        n5 += signature2.iSeedInfoLen;
        int n6 = this.numMPCRounds - this.numOpenedRounds;
        int[] nArray = this.getMissingLeavesList(signature2.challengeC);
        tree = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
        signature2.cvInfoLen = tree.openMerkleTreeSize(nArray, n6);
        n5 += signature2.cvInfoLen;
        int[] nArray2 = new int[1];
        tree = new Tree(this, this.numMPCParties, this.seedSizeBytes);
        int n7 = tree.revealSeedsSize(nArray2, 1);
        for (n4 = 0; n4 < this.numMPCRounds; ++n4) {
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, n4)) continue;
            n3 = signature2.challengeP[PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n4)];
            if (n3 != this.numMPCParties - 1) {
                n5 += this.andSizeBytes;
            }
            n5 += n7;
            n5 += this.stateSizeBytes;
            n5 += this.andSizeBytes;
            n5 += this.digestSizeBytes;
        }
        if (n != n5) {
            LOG.fine("sigLen = " + n + ", expected bytesRequired = " + n5);
            return -1;
        }
        signature2.iSeedInfo = new byte[signature2.iSeedInfoLen];
        System.arraycopy(byArray, n2, signature2.iSeedInfo, 0, signature2.iSeedInfoLen);
        signature2.cvInfo = new byte[signature2.cvInfoLen];
        System.arraycopy(byArray, n2 += signature2.iSeedInfoLen, signature2.cvInfo, 0, signature2.cvInfoLen);
        n2 += signature2.cvInfoLen;
        for (n4 = 0; n4 < this.numMPCRounds; ++n4) {
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, n4)) continue;
            signature2.proofs[n4] = new Signature2.Proof2(this);
            signature2.proofs[n4].seedInfoLen = n7;
            signature2.proofs[n4].seedInfo = new byte[signature2.proofs[n4].seedInfoLen];
            System.arraycopy(byArray, n2, signature2.proofs[n4].seedInfo, 0, signature2.proofs[n4].seedInfoLen);
            n2 += signature2.proofs[n4].seedInfoLen;
            n3 = signature2.challengeP[PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n4)];
            if (n3 != this.numMPCParties - 1) {
                System.arraycopy(byArray, n2, signature2.proofs[n4].aux, 0, this.andSizeBytes);
                n2 += this.andSizeBytes;
                if (!this.arePaddingBitsZero(signature2.proofs[n4].aux, 3 * this.numRounds * this.numSboxes)) {
                    LOG.fine("failed while deserializing aux bits");
                    return -1;
                }
            }
            System.arraycopy(byArray, n2, signature2.proofs[n4].input, 0, this.stateSizeBytes);
            int n8 = this.andSizeBytes;
            System.arraycopy(byArray, n2 += this.stateSizeBytes, signature2.proofs[n4].msgs, 0, n8);
            n2 += n8;
            int n9 = 3 * this.numRounds * this.numSboxes;
            if (!this.arePaddingBitsZero(signature2.proofs[n4].msgs, n9)) {
                LOG.fine("failed while deserializing msgs bits");
                return -1;
            }
            System.arraycopy(byArray, n2, signature2.proofs[n4].C, 0, this.digestSizeBytes);
            n2 += this.digestSizeBytes;
        }
        return 0;
    }

    private boolean arePaddingBitsZero(byte[] byArray, int n) {
        int n2 = Utils.numBytes(n);
        for (int i = n; i < n2 * 8; ++i) {
            byte by = Utils.getBit(byArray, i);
            if (by == 0) continue;
            return false;
        }
        return true;
    }

    private boolean arePaddingBitsZero(int[] nArray, int n) {
        int n2 = n & 0x1F;
        if (n2 == 0) {
            return true;
        }
        int n3 = Utils.getTrailingBitsMask(n);
        return (nArray[n >>> 5] & ~n3) == 0;
    }

    public void crypto_sign(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        boolean bl = this.picnic_sign(byArray3, byArray2, byArray);
        if (!bl) {
            return;
        }
        System.arraycopy(byArray2, 0, byArray, 4, byArray2.length);
    }

    private boolean picnic_sign(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n;
        int[] nArray = new int[this.stateSizeWords];
        int[] nArray2 = new int[this.stateSizeWords];
        int[] nArray3 = new int[this.stateSizeWords];
        int n2 = 1;
        int n3 = 1 + this.stateSizeBytes;
        int n4 = 1 + 2 * this.stateSizeBytes;
        int n5 = this.stateSizeBytes / 4;
        Pack.littleEndianToInt(byArray, n2, nArray, 0, n5);
        Pack.littleEndianToInt(byArray, n3, nArray2, 0, n5);
        Pack.littleEndianToInt(byArray, n4, nArray3, 0, n5);
        if (n5 < this.stateSizeWords) {
            int n6 = n5 * 4;
            n = this.stateSizeBytes - n6;
            nArray[n5] = Pack.littleEndianToInt_Low(byArray, n2 + n6, n);
            nArray2[n5] = Pack.littleEndianToInt_Low(byArray, n3 + n6, n);
            nArray3[n5] = Pack.littleEndianToInt_Low(byArray, n4 + n6, n);
        }
        if (!PicnicEngine.is_picnic3(this.parameters)) {
            Signature signature = new Signature(this);
            n = this.sign_picnic1(nArray, nArray2, nArray3, byArray2, signature);
            if (n != 0) {
                LOG.fine("Failed to create signature");
                return false;
            }
            int n7 = this.serializeSignature(signature, byArray3, byArray2.length + 4);
            if (n7 < 0) {
                LOG.fine("Failed to serialize signature");
                return false;
            }
            this.signatureLength = n7;
            Pack.intToLittleEndian(n7, byArray3, 0);
            return true;
        }
        Signature2 signature2 = new Signature2(this);
        n = this.sign_picnic3(nArray, nArray2, nArray3, byArray2, signature2) ? 1 : 0;
        if (n == 0) {
            LOG.fine("Failed to create signature");
            return false;
        }
        int n8 = this.serializeSignature2(signature2, byArray3, byArray2.length + 4);
        if (n8 < 0) {
            LOG.fine("Failed to serialize signature");
            return false;
        }
        this.signatureLength = n8;
        Pack.intToLittleEndian(n8, byArray3, 0);
        return true;
    }

    int serializeSignature(Signature signature, byte[] byArray, int n) {
        Signature.Proof[] proofArray = signature.proofs;
        byte[] byArray2 = signature.challengeBits;
        int n2 = Utils.numBytes(2 * this.numMPCRounds) + 32 + this.numMPCRounds * (2 * this.seedSizeBytes + this.stateSizeBytes + this.andSizeBytes + this.digestSizeBytes);
        if (this.transform == 1) {
            n2 += this.UnruhGWithoutInputBytes * this.numMPCRounds;
        }
        if (this.CRYPTO_BYTES < n2) {
            return -1;
        }
        int n3 = n;
        System.arraycopy(byArray2, 0, byArray, n3, Utils.numBytes(2 * this.numMPCRounds));
        System.arraycopy(signature.salt, 0, byArray, n3 += Utils.numBytes(2 * this.numMPCRounds), 32);
        n3 += 32;
        for (int i = 0; i < this.numMPCRounds; ++i) {
            int n4 = this.getChallenge(byArray2, i);
            System.arraycopy(proofArray[i].view3Commitment, 0, byArray, n3, this.digestSizeBytes);
            n3 += this.digestSizeBytes;
            if (this.transform == 1) {
                int n5 = n4 == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                System.arraycopy(proofArray[i].view3UnruhG, 0, byArray, n3, n5);
                n3 += n5;
            }
            System.arraycopy(proofArray[i].communicatedBits, 0, byArray, n3, this.andSizeBytes);
            System.arraycopy(proofArray[i].seed1, 0, byArray, n3 += this.andSizeBytes, this.seedSizeBytes);
            System.arraycopy(proofArray[i].seed2, 0, byArray, n3 += this.seedSizeBytes, this.seedSizeBytes);
            n3 += this.seedSizeBytes;
            if (n4 != 1 && n4 != 2) continue;
            Pack.intToLittleEndian(proofArray[i].inputShare, 0, this.stateSizeWords, byArray, n3);
            n3 += this.stateSizeBytes;
        }
        return n3 - n;
    }

    int getChallenge(byte[] byArray, int n) {
        return Utils.getCrumbAligned(byArray, n);
    }

    private int serializeSignature2(Signature2 signature2, byte[] byArray, int n) {
        int n2;
        int n3;
        int n4 = this.digestSizeBytes + 32;
        n4 += signature2.iSeedInfoLen;
        n4 += signature2.cvInfoLen;
        for (n3 = 0; n3 < this.numMPCRounds; ++n3) {
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, n3)) continue;
            n2 = signature2.challengeP[PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n3)];
            n4 += signature2.proofs[n3].seedInfoLen;
            if (n2 != this.numMPCParties - 1) {
                n4 += this.andSizeBytes;
            }
            n4 += this.stateSizeBytes;
            n4 += this.andSizeBytes;
            n4 += this.digestSizeBytes;
        }
        if (byArray.length < n4) {
            return -1;
        }
        n3 = n;
        System.arraycopy(signature2.challengeHash, 0, byArray, n3, this.digestSizeBytes);
        System.arraycopy(signature2.salt, 0, byArray, n3 += this.digestSizeBytes, 32);
        System.arraycopy(signature2.iSeedInfo, 0, byArray, n3 += 32, signature2.iSeedInfoLen);
        System.arraycopy(signature2.cvInfo, 0, byArray, n3 += signature2.iSeedInfoLen, signature2.cvInfoLen);
        n3 += signature2.cvInfoLen;
        for (n2 = 0; n2 < this.numMPCRounds; ++n2) {
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, n2)) continue;
            System.arraycopy(signature2.proofs[n2].seedInfo, 0, byArray, n3, signature2.proofs[n2].seedInfoLen);
            n3 += signature2.proofs[n2].seedInfoLen;
            int n5 = signature2.challengeP[PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, n2)];
            if (n5 != this.numMPCParties - 1) {
                System.arraycopy(signature2.proofs[n2].aux, 0, byArray, n3, this.andSizeBytes);
                n3 += this.andSizeBytes;
            }
            System.arraycopy(signature2.proofs[n2].input, 0, byArray, n3, this.stateSizeBytes);
            System.arraycopy(signature2.proofs[n2].msgs, 0, byArray, n3 += this.stateSizeBytes, this.andSizeBytes);
            System.arraycopy(signature2.proofs[n2].C, 0, byArray, n3 += this.andSizeBytes, this.digestSizeBytes);
            n3 += this.digestSizeBytes;
        }
        return n3 - n;
    }

    private int sign_picnic1(int[] nArray, int[] nArray2, int[] nArray3, byte[] byArray, Signature signature) {
        Object object;
        int n;
        View[][] viewArray = new View[this.numMPCRounds][3];
        byte[][][] byArray2 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        byte[][][] byArray3 = new byte[this.numMPCRounds][3][this.UnruhGWithInputBytes];
        byte[] byArray4 = this.computeSeeds(nArray, nArray2, nArray3, byArray);
        int n2 = this.numMPCParties * this.seedSizeBytes;
        System.arraycopy(byArray4, n2 * this.numMPCRounds, signature.salt, 0, 32);
        Tape tape = new Tape(this);
        byte[] byArray5 = new byte[Math.max(9 * this.stateSizeBytes, this.stateSizeBytes + this.andSizeBytes)];
        for (n = 0; n < this.numMPCRounds; ++n) {
            int[] nArray4;
            boolean bl;
            viewArray[n][0] = new View(this);
            viewArray[n][1] = new View(this);
            viewArray[n][2] = new View(this);
            for (int i = 0; i < 2; ++i) {
                bl = this.createRandomTape(byArray4, n2 * n + i * this.seedSizeBytes, signature.salt, n, i, byArray5, this.stateSizeBytes + this.andSizeBytes);
                if (!bl) {
                    LOG.fine("createRandomTape failed");
                    return -1;
                }
                nArray4 = viewArray[n][i].inputShare;
                Pack.littleEndianToInt(byArray5, 0, nArray4);
                Utils.zeroTrailingBits(nArray4, this.stateSizeBits);
                System.arraycopy(byArray5, this.stateSizeBytes, tape.tapes[i], 0, this.andSizeBytes);
            }
            bl = this.createRandomTape(byArray4, n2 * n + 2 * this.seedSizeBytes, signature.salt, n, 2, tape.tapes[2], this.andSizeBytes);
            if (!bl) {
                LOG.fine("createRandomTape failed");
                return -1;
            }
            this.xor_three(viewArray[n][2].inputShare, nArray, viewArray[n][0].inputShare, viewArray[n][1].inputShare);
            tape.pos = 0;
            object = Pack.littleEndianToInt(byArray5, 0, byArray5.length / 4);
            this.mpc_LowMC(tape, viewArray[n], nArray3, (int[])object);
            Pack.intToLittleEndian((int[])object, byArray5, 0);
            nArray4 = new int[16];
            this.xor_three(nArray4, viewArray[n][0].outputShare, viewArray[n][1].outputShare, viewArray[n][2].outputShare);
            if (!PicnicEngine.subarrayEquals(nArray4, nArray2, this.stateSizeWords)) {
                LOG.fine("Simulation failed; output does not match public key (round = " + n + ")");
                return -1;
            }
            this.Commit(byArray4, n2 * n + 0 * this.seedSizeBytes, viewArray[n][0], byArray2[n][0]);
            this.Commit(byArray4, n2 * n + 1 * this.seedSizeBytes, viewArray[n][1], byArray2[n][1]);
            this.Commit(byArray4, n2 * n + 2 * this.seedSizeBytes, viewArray[n][2], byArray2[n][2]);
            if (this.transform != 1) continue;
            this.G(0, byArray4, n2 * n + 0 * this.seedSizeBytes, viewArray[n][0], byArray3[n][0]);
            this.G(1, byArray4, n2 * n + 1 * this.seedSizeBytes, viewArray[n][1], byArray3[n][1]);
            this.G(2, byArray4, n2 * n + 2 * this.seedSizeBytes, viewArray[n][2], byArray3[n][2]);
        }
        this.H3(nArray2, nArray3, viewArray, byArray2, signature.challengeBits, signature.salt, byArray, byArray3);
        for (n = 0; n < this.numMPCRounds; ++n) {
            object = signature.proofs[n];
            this.prove((Signature.Proof)object, this.getChallenge(signature.challengeBits, n), byArray4, n2 * n, viewArray[n], byArray2[n], this.transform != 1 ? null : byArray3[n]);
        }
        return 0;
    }

    void prove(Signature.Proof proof, int n, byte[] byArray, int n2, View[] viewArray, byte[][] byArray2, byte[][] byArray3) {
        if (n == 0) {
            System.arraycopy(byArray, n2 + 0 * this.seedSizeBytes, proof.seed1, 0, this.seedSizeBytes);
            System.arraycopy(byArray, n2 + 1 * this.seedSizeBytes, proof.seed2, 0, this.seedSizeBytes);
        } else if (n == 1) {
            System.arraycopy(byArray, n2 + 1 * this.seedSizeBytes, proof.seed1, 0, this.seedSizeBytes);
            System.arraycopy(byArray, n2 + 2 * this.seedSizeBytes, proof.seed2, 0, this.seedSizeBytes);
        } else if (n == 2) {
            System.arraycopy(byArray, n2 + 2 * this.seedSizeBytes, proof.seed1, 0, this.seedSizeBytes);
            System.arraycopy(byArray, n2 + 0 * this.seedSizeBytes, proof.seed2, 0, this.seedSizeBytes);
        } else {
            LOG.fine("Invalid challenge");
            throw new IllegalArgumentException("challenge");
        }
        if (n == 1 || n == 2) {
            System.arraycopy(viewArray[2].inputShare, 0, proof.inputShare, 0, this.stateSizeWords);
        }
        System.arraycopy(viewArray[(n + 1) % 3].communicatedBits, 0, proof.communicatedBits, 0, this.andSizeBytes);
        System.arraycopy(byArray2[(n + 2) % 3], 0, proof.view3Commitment, 0, this.digestSizeBytes);
        if (this.transform == 1) {
            int n3 = n == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
            System.arraycopy(byArray3[(n + 2) % 3], 0, proof.view3UnruhG, 0, n3);
        }
    }

    private void H3(int[] nArray, int[] nArray2, View[][] viewArray, byte[][][] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[][][] byArray5) {
        this.digest.update((byte)1);
        byte[] byArray6 = new byte[this.stateSizeWords * 4];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            for (int j = 0; j < 3; ++j) {
                Pack.intToLittleEndian(viewArray[i][j].outputShare, byArray6, 0);
                this.digest.update(byArray6, 0, this.stateSizeBytes);
            }
        }
        this.implH3(nArray, nArray2, byArray, byArray2, byArray3, byArray4, byArray5);
    }

    private void H3(int[] nArray, int[] nArray2, int[][][] nArray3, byte[][][] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[][][] byArray5) {
        this.digest.update((byte)1);
        byte[] byArray6 = new byte[this.stateSizeWords * 4];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            for (int j = 0; j < 3; ++j) {
                Pack.intToLittleEndian(nArray3[i][j], byArray6, 0);
                this.digest.update(byArray6, 0, this.stateSizeBytes);
            }
        }
        this.implH3(nArray, nArray2, byArray, byArray2, byArray3, byArray4, byArray5);
    }

    private void implH3(int[] nArray, int[] nArray2, byte[][][] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[][][] byArray5) {
        int n;
        int n2;
        int n3;
        byte[] byArray6 = new byte[this.digestSizeBytes];
        byArray2[Utils.numBytes((int)(this.numMPCRounds * 2)) - 1] = 0;
        for (n3 = 0; n3 < this.numMPCRounds; ++n3) {
            for (n2 = 0; n2 < 3; ++n2) {
                this.digest.update(byArray[n3][n2], 0, this.digestSizeBytes);
            }
        }
        if (this.transform == 1) {
            for (n3 = 0; n3 < this.numMPCRounds; ++n3) {
                for (n2 = 0; n2 < 3; ++n2) {
                    n = n2 == 2 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                    this.digest.update(byArray5[n3][n2], 0, n);
                }
            }
        }
        this.digest.update(Pack.intToLittleEndian(nArray), 0, this.stateSizeBytes);
        this.digest.update(Pack.intToLittleEndian(nArray2), 0, this.stateSizeBytes);
        this.digest.update(byArray3, 0, 32);
        this.digest.update(byArray4, 0, byArray4.length);
        this.digest.doFinal(byArray6, 0, this.digestSizeBytes);
        n3 = 0;
        n2 = 1;
        while (n2 != 0) {
            for (n = 0; n < this.digestSizeBytes; ++n) {
                byte by = byArray6[n];
                for (int i = 0; i < 8; i += 2) {
                    int n4 = by >>> 6 - i & 3;
                    if (n4 >= 3) continue;
                    this.setChallenge(byArray2, n3, n4);
                    if (++n3 != this.numMPCRounds) continue;
                    n2 = 0;
                    break;
                }
                if (n2 == 0) break;
            }
            if (n2 == 0) break;
            this.digest.update((byte)1);
            this.digest.update(byArray6, 0, this.digestSizeBytes);
            this.digest.doFinal(byArray6, 0, this.digestSizeBytes);
        }
    }

    private void setChallenge(byte[] byArray, int n, int n2) {
        Utils.setBit(byArray, 2 * n, (byte)(n2 & 1));
        Utils.setBit(byArray, 2 * n + 1, (byte)(n2 >>> 1 & 1));
    }

    private void G(int n, byte[] byArray, int n2, View view, byte[] byArray2) {
        int n3 = this.seedSizeBytes + this.andSizeBytes;
        this.digest.update((byte)5);
        this.digest.update(byArray, n2, this.seedSizeBytes);
        this.digest.doFinal(byArray2, 0, this.digestSizeBytes);
        this.digest.update(byArray2, 0, this.digestSizeBytes);
        if (n == 2) {
            this.digest.update(Pack.intToLittleEndian(view.inputShare), 0, this.stateSizeBytes);
            n3 += this.stateSizeBytes;
        }
        this.digest.update(view.communicatedBits, 0, this.andSizeBytes);
        this.digest.update(Pack.intToLittleEndian(n3), 0, 2);
        this.digest.doFinal(byArray2, 0, n3);
    }

    private void mpc_LowMC(Tape tape, View[] viewArray, int[] nArray, int[] nArray2) {
        int n;
        Arrays.fill(nArray2, 0, nArray2.length, 0);
        this.mpc_xor_constant(nArray2, 3 * this.stateSizeWords, nArray, 0, this.stateSizeWords);
        KMatricesWithPointer kMatricesWithPointer = this.lowmcConstants.KMatrix(this, 0);
        for (n = 0; n < 3; ++n) {
            this.matrix_mul_offset(nArray2, n * this.stateSizeWords, viewArray[n].inputShare, 0, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
        }
        this.mpc_xor(nArray2, nArray2, 3);
        for (n = 1; n <= this.numRounds; ++n) {
            kMatricesWithPointer = this.lowmcConstants.KMatrix(this, n);
            for (int i = 0; i < 3; ++i) {
                this.matrix_mul_offset(nArray2, i * this.stateSizeWords, viewArray[i].inputShare, 0, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            }
            this.mpc_substitution(nArray2, tape, viewArray);
            kMatricesWithPointer = this.lowmcConstants.LMatrix(this, n - 1);
            this.mpc_matrix_mul(nArray2, 3 * this.stateSizeWords, nArray2, 3 * this.stateSizeWords, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer(), 3);
            kMatricesWithPointer = this.lowmcConstants.RConstant(this, n - 1);
            this.mpc_xor_constant(nArray2, 3 * this.stateSizeWords, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer(), this.stateSizeWords);
            this.mpc_xor(nArray2, nArray2, 3);
        }
        for (n = 0; n < 3; ++n) {
            System.arraycopy(nArray2, (3 + n) * this.stateSizeWords, viewArray[n].outputShare, 0, this.stateSizeWords);
        }
    }

    private void Commit(byte[] byArray, int n, View view, byte[] byArray2) {
        this.digest.update((byte)4);
        this.digest.update(byArray, n, this.seedSizeBytes);
        this.digest.doFinal(byArray2, 0, this.digestSizeBytes);
        this.digest.update((byte)0);
        this.digest.update(byArray2, 0, this.digestSizeBytes);
        this.digest.update(Pack.intToLittleEndian(view.inputShare), 0, this.stateSizeBytes);
        this.digest.update(view.communicatedBits, 0, this.andSizeBytes);
        this.digest.update(Pack.intToLittleEndian(view.outputShare), 0, this.stateSizeBytes);
        this.digest.doFinal(byArray2, 0, this.digestSizeBytes);
    }

    private void mpc_substitution(int[] nArray, Tape tape, View[] viewArray) {
        int[] nArray2 = new int[3];
        int[] nArray3 = new int[3];
        int[] nArray4 = new int[3];
        int[] nArray5 = new int[3];
        int[] nArray6 = new int[3];
        int[] nArray7 = new int[3];
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int n;
            int n2;
            for (n2 = 0; n2 < 3; ++n2) {
                n = (3 + n2) * this.stateSizeWords * 32;
                nArray2[n2] = Utils.getBitFromWordArray(nArray, n + i + 2);
                nArray3[n2] = Utils.getBitFromWordArray(nArray, n + i + 1);
                nArray4[n2] = Utils.getBitFromWordArray(nArray, n + i);
            }
            this.mpc_AND(nArray2, nArray3, nArray5, tape, viewArray);
            this.mpc_AND(nArray3, nArray4, nArray6, tape, viewArray);
            this.mpc_AND(nArray4, nArray2, nArray7, tape, viewArray);
            for (n2 = 0; n2 < 3; ++n2) {
                n = (3 + n2) * this.stateSizeWords * 32;
                Utils.setBitInWordArray(nArray, n + i + 2, nArray2[n2] ^ nArray6[n2]);
                Utils.setBitInWordArray(nArray, n + i + 1, nArray2[n2] ^ nArray3[n2] ^ nArray7[n2]);
                Utils.setBitInWordArray(nArray, n + i, nArray2[n2] ^ nArray3[n2] ^ nArray4[n2] ^ nArray5[n2]);
            }
        }
    }

    private void mpc_AND(int[] nArray, int[] nArray2, int[] nArray3, Tape tape, View[] viewArray) {
        byte by = Utils.getBit(tape.tapes[0], tape.pos);
        byte by2 = Utils.getBit(tape.tapes[1], tape.pos);
        byte by3 = Utils.getBit(tape.tapes[2], tape.pos);
        nArray3[0] = nArray[0] & nArray2[1] ^ nArray[1] & nArray2[0] ^ nArray[0] & nArray2[0] ^ by ^ by2;
        nArray3[1] = nArray[1] & nArray2[2] ^ nArray[2] & nArray2[1] ^ nArray[1] & nArray2[1] ^ by2 ^ by3;
        nArray3[2] = nArray[2] & nArray2[0] ^ nArray[0] & nArray2[2] ^ nArray[2] & nArray2[2] ^ by3 ^ by;
        Utils.setBit(viewArray[0].communicatedBits, tape.pos, (byte)nArray3[0]);
        Utils.setBit(viewArray[1].communicatedBits, tape.pos, (byte)nArray3[1]);
        Utils.setBit(viewArray[2].communicatedBits, tape.pos, (byte)nArray3[2]);
        ++tape.pos;
    }

    private void mpc_xor(int[] nArray, int[] nArray2, int n) {
        int n2 = this.stateSizeWords * n;
        for (int i = 0; i < n2; ++i) {
            int n3 = n * this.stateSizeWords + i;
            nArray[n3] = nArray[n3] ^ nArray2[i];
        }
    }

    private void mpc_matrix_mul(int[] nArray, int n, int[] nArray2, int n2, int[] nArray3, int n3, int n4) {
        for (int i = 0; i < n4; ++i) {
            this.matrix_mul_offset(nArray, n + i * this.stateSizeWords, nArray2, n2 + i * this.stateSizeWords, nArray3, n3);
        }
    }

    private void mpc_xor_constant(int[] nArray, int n, int[] nArray2, int n2, int n3) {
        for (int i = 0; i < n3; ++i) {
            int n4 = i + n;
            nArray[n4] = nArray[n4] ^ nArray2[i + n2];
        }
    }

    private boolean createRandomTape(byte[] byArray, int n, byte[] byArray2, int n2, int n3, byte[] byArray3, int n4) {
        if (n4 < this.digestSizeBytes) {
            return false;
        }
        this.digest.update((byte)2);
        this.digest.update(byArray, n, this.seedSizeBytes);
        this.digest.doFinal(byArray3, 0, this.digestSizeBytes);
        this.digest.update(byArray3, 0, this.digestSizeBytes);
        this.digest.update(byArray2, 0, 32);
        this.digest.update(Pack.intToLittleEndian(n2), 0, 2);
        this.digest.update(Pack.intToLittleEndian(n3), 0, 2);
        this.digest.update(Pack.intToLittleEndian(n4), 0, 2);
        this.digest.doFinal(byArray3, 0, n4);
        return true;
    }

    private byte[] computeSeeds(int[] nArray, int[] nArray2, int[] nArray3, byte[] byArray) {
        byte[] byArray2 = new byte[this.seedSizeBytes * (this.numMPCParties * this.numMPCRounds) + 32];
        byte[] byArray3 = new byte[32];
        this.updateDigest(nArray, byArray3);
        this.digest.update(byArray, 0, byArray.length);
        this.updateDigest(nArray2, byArray3);
        this.updateDigest(nArray3, byArray3);
        this.digest.update(Pack.intToLittleEndian(this.stateSizeBits), 0, 2);
        this.digest.doFinal(byArray2, 0, this.seedSizeBytes * (this.numMPCParties * this.numMPCRounds) + 32);
        return byArray2;
    }

    private boolean sign_picnic3(int[] nArray, int[] nArray2, int[] nArray3, byte[] byArray, Signature2 signature2) {
        int n;
        Object object;
        byte[] byArray2 = new byte[32 + this.seedSizeBytes];
        this.computeSaltAndRootSeed(byArray2, nArray, nArray2, nArray3, byArray);
        byte[] byArray3 = Arrays.copyOfRange(byArray2, 32, byArray2.length);
        signature2.salt = Arrays.copyOfRange(byArray2, 0, 32);
        Tree tree = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
        tree.generateSeeds(byArray3, signature2.salt, 0);
        byte[][] byArray4 = tree.getLeaves();
        int n2 = tree.getLeavesOffset();
        Tape[] tapeArray = new Tape[this.numMPCRounds];
        Tree[] treeArray = new Tree[this.numMPCRounds];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            tapeArray[i] = new Tape(this);
            treeArray[i] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
            treeArray[i].generateSeeds(byArray4[i + n2], signature2.salt, i);
            this.createRandomTapes(tapeArray[i], treeArray[i].getLeaves(), treeArray[i].getLeavesOffset(), signature2.salt, i);
        }
        byte[][] byArray5 = new byte[this.numMPCRounds][this.stateSizeWords * 4];
        byte[] byArray6 = new byte[176];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            tapeArray[i].computeAuxTape(byArray5[i]);
        }
        byte[][][] byArray7 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            int n3;
            for (n3 = 0; n3 < this.numMPCParties - 1; ++n3) {
                this.commit(byArray7[i][n3], treeArray[i].getLeaf(n3), null, signature2.salt, i, n3);
            }
            n3 = this.numMPCParties - 1;
            this.getAuxBits(byArray6, tapeArray[i]);
            this.commit(byArray7[i][n3], treeArray[i].getLeaf(n3), byArray6, signature2.salt, i, n3);
        }
        Msg[] msgArray = new Msg[this.numMPCRounds];
        int[] nArray4 = new int[this.stateSizeBits];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            msgArray[i] = new Msg(this);
            object = Pack.littleEndianToInt(byArray5[i], 0, this.stateSizeWords);
            this.xor_array((int[])object, (int[])object, nArray, 0);
            n = this.simulateOnline((int[])object, tapeArray[i], nArray4, msgArray[i], nArray3, nArray2);
            if (n != 0) {
                LOG.fine("MPC simulation failed, aborting signature");
                return false;
            }
            Pack.intToLittleEndian((int[])object, byArray5[i], 0);
        }
        byte[][] byArray8 = new byte[this.numMPCRounds][this.digestSizeBytes];
        object = new byte[this.numMPCRounds][this.digestSizeBytes];
        for (n = 0; n < this.numMPCRounds; ++n) {
            this.commit_h(byArray8[n], byArray7[n]);
            this.commit_v(object[n], byArray5[n], msgArray[n]);
        }
        Tree tree2 = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
        tree2.buildMerkleTree((byte[][])object, signature2.salt);
        signature2.challengeC = new int[this.numOpenedRounds];
        signature2.challengeP = new int[this.numOpenedRounds];
        signature2.challengeHash = new byte[this.digestSizeBytes];
        this.HCP(signature2.challengeHash, signature2.challengeC, signature2.challengeP, byArray8, tree2.nodes[0], signature2.salt, nArray2, nArray3, byArray);
        int n4 = this.numMPCRounds - this.numOpenedRounds;
        int[] nArray5 = this.getMissingLeavesList(signature2.challengeC);
        int[] nArray6 = new int[1];
        signature2.cvInfo = tree2.openMerkleTree(nArray5, n4, nArray6);
        signature2.cvInfoLen = nArray6[0];
        signature2.iSeedInfo = new byte[this.numMPCRounds * this.seedSizeBytes];
        signature2.iSeedInfoLen = tree.revealSeeds(signature2.challengeC, this.numOpenedRounds, signature2.iSeedInfo, this.numMPCRounds * this.seedSizeBytes);
        signature2.proofs = new Signature2.Proof2[this.numMPCRounds];
        for (int i = 0; i < this.numMPCRounds; ++i) {
            if (!this.contains(signature2.challengeC, this.numOpenedRounds, i)) continue;
            signature2.proofs[i] = new Signature2.Proof2(this);
            int n5 = PicnicEngine.indexOf(signature2.challengeC, this.numOpenedRounds, i);
            int[] nArray7 = new int[]{signature2.challengeP[n5]};
            signature2.proofs[i].seedInfo = new byte[this.numMPCParties * this.seedSizeBytes];
            signature2.proofs[i].seedInfoLen = treeArray[i].revealSeeds(nArray7, 1, signature2.proofs[i].seedInfo, this.numMPCParties * this.seedSizeBytes);
            int n6 = this.numMPCParties - 1;
            if (signature2.challengeP[n5] != n6) {
                this.getAuxBits(signature2.proofs[i].aux, tapeArray[i]);
            }
            System.arraycopy(byArray5[i], 0, signature2.proofs[i].input, 0, this.stateSizeBytes);
            System.arraycopy(msgArray[i].msgs[signature2.challengeP[n5]], 0, signature2.proofs[i].msgs, 0, this.andSizeBytes);
            System.arraycopy(byArray7[i][signature2.challengeP[n5]], 0, signature2.proofs[i].C, 0, this.digestSizeBytes);
        }
        return true;
    }

    static int indexOf(int[] nArray, int n, int n2) {
        for (int i = 0; i < n; ++i) {
            if (nArray[i] != n2) continue;
            return i;
        }
        return -1;
    }

    private int[] getMissingLeavesList(int[] nArray) {
        int n = this.numMPCRounds - this.numOpenedRounds;
        int[] nArray2 = new int[n];
        int n2 = 0;
        for (int i = 0; i < this.numMPCRounds; ++i) {
            if (this.contains(nArray, this.numOpenedRounds, i)) continue;
            nArray2[n2] = i;
            ++n2;
        }
        return nArray2;
    }

    private void HCP(byte[] byArray, int[] nArray, int[] nArray2, byte[][] byArray2, byte[] byArray3, byte[] byArray4, int[] nArray3, int[] nArray4, byte[] byArray5) {
        for (int i = 0; i < this.numMPCRounds; ++i) {
            this.digest.update(byArray2[i], 0, this.digestSizeBytes);
        }
        byte[] byArray6 = new byte[32];
        this.digest.update(byArray3, 0, this.digestSizeBytes);
        this.digest.update(byArray4, 0, 32);
        this.updateDigest(nArray3, byArray6);
        this.updateDigest(nArray4, byArray6);
        this.digest.update(byArray5, 0, byArray5.length);
        this.digest.doFinal(byArray, 0, this.digestSizeBytes);
        if (nArray != null && nArray2 != null) {
            this.expandChallengeHash(byArray, nArray, nArray2);
        }
    }

    static int bitsToChunks(int n, byte[] byArray, int n2, int[] nArray) {
        if (n > n2 * 8) {
            return 0;
        }
        int n3 = n2 * 8 / n;
        for (int i = 0; i < n3; ++i) {
            nArray[i] = 0;
            for (int j = 0; j < n; ++j) {
                int n4 = i;
                nArray[n4] = nArray[n4] + (Utils.getBit(byArray, i * n + j) << j);
            }
        }
        return n3;
    }

    static int appendUnique(int[] nArray, int n, int n2) {
        if (n2 == 0) {
            nArray[n2] = n;
            return n2 + 1;
        }
        for (int i = 0; i < n2; ++i) {
            if (nArray[i] != n) continue;
            return n2;
        }
        nArray[n2] = n;
        return n2 + 1;
    }

    private void expandChallengeHash(byte[] byArray, int[] nArray, int[] nArray2) {
        int n;
        int n2;
        int n3 = Utils.ceil_log2(this.numMPCRounds);
        int n4 = Utils.ceil_log2(this.numMPCParties);
        int[] nArray3 = new int[this.digestSizeBytes * 8 / Math.min(n3, n4)];
        byte[] byArray2 = new byte[64];
        System.arraycopy(byArray, 0, byArray2, 0, this.digestSizeBytes);
        int n5 = 0;
        while (n5 < this.numOpenedRounds) {
            n2 = PicnicEngine.bitsToChunks(n3, byArray2, this.digestSizeBytes, nArray3);
            for (n = 0; n < n2; ++n) {
                if (nArray3[n] < this.numMPCRounds) {
                    n5 = PicnicEngine.appendUnique(nArray, nArray3[n], n5);
                }
                if (n5 == this.numOpenedRounds) break;
            }
            this.digest.update((byte)1);
            this.digest.update(byArray2, 0, this.digestSizeBytes);
            this.digest.doFinal(byArray2, 0, this.digestSizeBytes);
        }
        n2 = 0;
        while (n2 < this.numOpenedRounds) {
            n = PicnicEngine.bitsToChunks(n4, byArray2, this.digestSizeBytes, nArray3);
            for (int i = 0; i < n; ++i) {
                if (nArray3[i] < this.numMPCParties) {
                    nArray2[n2] = nArray3[i];
                    ++n2;
                }
                if (n2 == this.numOpenedRounds) break;
            }
            this.digest.update((byte)1);
            this.digest.update(byArray2, 0, this.digestSizeBytes);
            this.digest.doFinal(byArray2, 0, this.digestSizeBytes);
        }
    }

    private void commit_h(byte[] byArray, byte[][] byArray2) {
        for (int i = 0; i < this.numMPCParties; ++i) {
            this.digest.update(byArray2[i], 0, this.digestSizeBytes);
        }
        this.digest.doFinal(byArray, 0, this.digestSizeBytes);
    }

    private void commit_v(byte[] byArray, byte[] byArray2, Msg msg) {
        this.digest.update(byArray2, 0, this.stateSizeBytes);
        for (int i = 0; i < this.numMPCParties; ++i) {
            int n = Utils.numBytes(msg.pos);
            this.digest.update(msg.msgs[i], 0, n);
        }
        this.digest.doFinal(byArray, 0, this.digestSizeBytes);
    }

    private int simulateOnline(int[] nArray, Tape tape, int[] nArray2, Msg msg, int[] nArray3, int[] nArray4) {
        int n = 0;
        int[] nArray5 = new int[16];
        int[] nArray6 = new int[16];
        KMatricesWithPointer kMatricesWithPointer = this.lowmcConstants.KMatrix(this, 0);
        this.matrix_mul(nArray5, nArray, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
        this.xor_array(nArray6, nArray5, nArray3, 0);
        for (int i = 1; i <= this.numRounds; ++i) {
            this.tapesToWords(nArray2, tape);
            this.mpc_sbox(nArray6, nArray2, tape, msg);
            kMatricesWithPointer = this.lowmcConstants.LMatrix(this, i - 1);
            this.matrix_mul(nArray6, nArray6, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            kMatricesWithPointer = this.lowmcConstants.RConstant(this, i - 1);
            this.xor_array(nArray6, nArray6, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            kMatricesWithPointer = this.lowmcConstants.KMatrix(this, i);
            this.matrix_mul(nArray5, nArray, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            this.xor_array(nArray6, nArray5, nArray6, 0);
        }
        if (!PicnicEngine.subarrayEquals(nArray6, nArray4, this.stateSizeWords)) {
            n = -1;
        }
        return n;
    }

    private void createRandomTapes(Tape tape, byte[][] byArray, int n, byte[] byArray2, int n2) {
        int n3 = 2 * this.andSizeBytes;
        for (int i = 0; i < this.numMPCParties; ++i) {
            this.digest.update(byArray[i + n], 0, this.seedSizeBytes);
            this.digest.update(byArray2, 0, 32);
            this.digest.update(Pack.intToLittleEndian(n2), 0, 2);
            this.digest.update(Pack.intToLittleEndian(i), 0, 2);
            this.digest.doFinal(tape.tapes[i], 0, n3);
        }
    }

    private static boolean subarrayEquals(byte[] byArray, byte[] byArray2, int n) {
        if (byArray.length < n || byArray2.length < n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (byArray[i] == byArray2[i]) continue;
            return false;
        }
        return true;
    }

    private static boolean subarrayEquals(int[] nArray, int[] nArray2, int n) {
        if (nArray.length < n || nArray2.length < n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (nArray[i] == nArray2[i]) continue;
            return false;
        }
        return true;
    }

    static int extend(int n) {
        return ~(n - 1);
    }

    private void wordToMsgs(int n, Msg msg) {
        for (int i = 0; i < this.numMPCParties; ++i) {
            int n2 = Utils.getBit(n, i);
            Utils.setBit(msg.msgs[i], msg.pos, (byte)n2);
        }
        ++msg.pos;
    }

    private int mpc_AND(int n, int n2, int n3, int n4, Tape tape, Msg msg) {
        int n5 = tape.tapesToWord();
        int n6 = PicnicEngine.extend(n) & n4 ^ PicnicEngine.extend(n2) & n3 ^ n5;
        if (msg.unopened >= 0) {
            byte by = Utils.getBit(msg.msgs[msg.unopened], msg.pos);
            n6 = Utils.setBit(n6, msg.unopened, (int)by);
        }
        this.wordToMsgs(n6, msg);
        return Utils.parity16(n6) ^ n & n2;
    }

    private void mpc_sbox(int[] nArray, int[] nArray2, Tape tape, Msg msg) {
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int n = Utils.getBitFromWordArray(nArray, i + 2);
            int n2 = nArray2[i + 2];
            int n3 = Utils.getBitFromWordArray(nArray, i + 1);
            int n4 = nArray2[i + 1];
            int n5 = Utils.getBitFromWordArray(nArray, i);
            int n6 = nArray2[i];
            int n7 = this.mpc_AND(n, n3, n2, n4, tape, msg);
            int n8 = this.mpc_AND(n3, n5, n4, n6, tape, msg);
            int n9 = this.mpc_AND(n5, n, n6, n2, tape, msg);
            int n10 = n ^ n8;
            int n11 = n ^ n3 ^ n9;
            int n12 = n ^ n3 ^ n5 ^ n7;
            Utils.setBitInWordArray(nArray, i + 2, n10);
            Utils.setBitInWordArray(nArray, i + 1, n11);
            Utils.setBitInWordArray(nArray, i, n12);
        }
    }

    protected void aux_mpc_sbox(int[] nArray, int[] nArray2, Tape tape) {
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int n = Utils.getBitFromWordArray(nArray, i + 2);
            int n2 = Utils.getBitFromWordArray(nArray, i + 1);
            int n3 = Utils.getBitFromWordArray(nArray, i);
            int n4 = Utils.getBitFromWordArray(nArray2, i + 2);
            int n5 = Utils.getBitFromWordArray(nArray2, i + 1);
            int n6 = Utils.getBitFromWordArray(nArray2, i);
            int n7 = n6 ^ n ^ n2 ^ n3;
            int n8 = n4 ^ n;
            int n9 = n5 ^ n ^ n2;
            this.aux_mpc_AND(n, n2, n7, tape);
            this.aux_mpc_AND(n2, n3, n8, tape);
            this.aux_mpc_AND(n3, n, n9, tape);
        }
    }

    private void aux_mpc_AND(int n, int n2, int n3, Tape tape) {
        int n4 = this.numMPCParties - 1;
        int n5 = tape.tapesToWord();
        n5 = Utils.parity16(n5) ^ Utils.getBit(tape.tapes[n4], tape.pos - 1);
        int n6 = n & n2 ^ n5 ^ n3;
        Utils.setBit(tape.tapes[n4], tape.pos - 1, (byte)(n6 & 0xFF));
    }

    private boolean contains(int[] nArray, int n, int n2) {
        for (int i = 0; i < n; ++i) {
            if (nArray[i] != n2) continue;
            return true;
        }
        return false;
    }

    private void tapesToWords(int[] nArray, Tape tape) {
        for (int i = 0; i < this.stateSizeBits; ++i) {
            nArray[i] = tape.tapesToWord();
        }
    }

    private void getAuxBits(byte[] byArray, Tape tape) {
        byte[] byArray2 = tape.tapes[this.numMPCParties - 1];
        int n = this.stateSizeBits;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < this.numRounds; ++i) {
            n3 += n;
            for (int j = 0; j < n; ++j) {
                Utils.setBit(byArray, n2++, Utils.getBit(byArray2, n3++));
            }
        }
    }

    private void commit(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, int n, int n2) {
        this.digest.update(byArray2, 0, this.seedSizeBytes);
        if (byArray3 != null) {
            this.digest.update(byArray3, 0, this.andSizeBytes);
        }
        this.digest.update(byArray4, 0, 32);
        this.digest.update(Pack.intToLittleEndian(n), 0, 2);
        this.digest.update(Pack.intToLittleEndian(n2), 0, 2);
        this.digest.doFinal(byArray, 0, this.digestSizeBytes);
    }

    private void computeSaltAndRootSeed(byte[] byArray, int[] nArray, int[] nArray2, int[] nArray3, byte[] byArray2) {
        byte[] byArray3 = new byte[32];
        this.updateDigest(nArray, byArray3);
        this.digest.update(byArray2, 0, byArray2.length);
        this.updateDigest(nArray2, byArray3);
        this.updateDigest(nArray3, byArray3);
        Pack.shortToLittleEndian((short)this.stateSizeBits, byArray3, 0);
        this.digest.update(byArray3, 0, 2);
        this.digest.doFinal(byArray, 0, byArray.length);
    }

    private void updateDigest(int[] nArray, byte[] byArray) {
        Pack.intToLittleEndian(nArray, byArray, 0);
        this.digest.update(byArray, 0, this.stateSizeBytes);
    }

    static boolean is_picnic3(int n) {
        return n == 7 || n == 8 || n == 9;
    }

    public void crypto_sign_keypair(byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        byte[] byArray3 = new byte[this.stateSizeWords * 4];
        byte[] byArray4 = new byte[this.stateSizeWords * 4];
        byte[] byArray5 = new byte[this.stateSizeWords * 4];
        this.picnic_keygen(byArray3, byArray4, byArray5, secureRandom);
        this.picnic_write_public_key(byArray4, byArray3, byArray);
        this.picnic_write_private_key(byArray5, byArray4, byArray3, byArray2);
    }

    private int picnic_write_private_key(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        int n = 1 + 3 * this.stateSizeBytes;
        if (byArray4.length < n) {
            LOG.fine("Failed writing private key!");
            return -1;
        }
        byArray4[0] = (byte)this.parameters;
        System.arraycopy(byArray, 0, byArray4, 1, this.stateSizeBytes);
        System.arraycopy(byArray2, 0, byArray4, 1 + this.stateSizeBytes, this.stateSizeBytes);
        System.arraycopy(byArray3, 0, byArray4, 1 + 2 * this.stateSizeBytes, this.stateSizeBytes);
        return n;
    }

    private int picnic_write_public_key(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n = 1 + 2 * this.stateSizeBytes;
        if (byArray3.length < n) {
            LOG.fine("Failed writing public key!");
            return -1;
        }
        byArray3[0] = (byte)this.parameters;
        System.arraycopy(byArray, 0, byArray3, 1, this.stateSizeBytes);
        System.arraycopy(byArray2, 0, byArray3, 1 + this.stateSizeBytes, this.stateSizeBytes);
        return n;
    }

    private void picnic_keygen(byte[] byArray, byte[] byArray2, byte[] byArray3, SecureRandom secureRandom) {
        int[] nArray = new int[byArray3.length / 4];
        int[] nArray2 = new int[byArray.length / 4];
        int[] nArray3 = new int[byArray2.length / 4];
        secureRandom.nextBytes(byArray3);
        Pack.littleEndianToInt(byArray3, 0, nArray);
        Utils.zeroTrailingBits(nArray, this.stateSizeBits);
        secureRandom.nextBytes(byArray);
        Pack.littleEndianToInt(byArray, 0, nArray2);
        Utils.zeroTrailingBits(nArray2, this.stateSizeBits);
        this.LowMCEnc(nArray2, nArray3, nArray);
        Pack.intToLittleEndian(nArray, byArray3, 0);
        Pack.intToLittleEndian(nArray2, byArray, 0);
        Pack.intToLittleEndian(nArray3, byArray2, 0);
    }

    private void LowMCEnc(int[] nArray, int[] nArray2, int[] nArray3) {
        int[] nArray4 = new int[16];
        if (nArray != nArray2) {
            System.arraycopy(nArray, 0, nArray2, 0, this.stateSizeWords);
        }
        KMatricesWithPointer kMatricesWithPointer = this.lowmcConstants.KMatrix(this, 0);
        this.matrix_mul(nArray4, nArray3, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
        this.xor_array(nArray2, nArray2, nArray4, 0);
        for (int i = 1; i <= this.numRounds; ++i) {
            kMatricesWithPointer = this.lowmcConstants.KMatrix(this, i);
            this.matrix_mul(nArray4, nArray3, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            this.substitution(nArray2);
            kMatricesWithPointer = this.lowmcConstants.LMatrix(this, i - 1);
            this.matrix_mul(nArray2, nArray2, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            kMatricesWithPointer = this.lowmcConstants.RConstant(this, i - 1);
            this.xor_array(nArray2, nArray2, kMatricesWithPointer.getData(), kMatricesWithPointer.getMatrixPointer());
            this.xor_array(nArray2, nArray2, nArray4, 0);
        }
    }

    private void substitution(int[] nArray) {
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int n = Utils.getBitFromWordArray(nArray, i + 2);
            int n2 = Utils.getBitFromWordArray(nArray, i + 1);
            int n3 = Utils.getBitFromWordArray(nArray, i);
            Utils.setBitInWordArray(nArray, i + 2, n ^ n2 & n3);
            Utils.setBitInWordArray(nArray, i + 1, n ^ n2 ^ n & n3);
            Utils.setBitInWordArray(nArray, i, n ^ n2 ^ n3 ^ n & n2);
        }
    }

    private void xor_three(int[] nArray, int[] nArray2, int[] nArray3, int[] nArray4) {
        for (int i = 0; i < this.stateSizeWords; ++i) {
            nArray[i] = nArray2[i] ^ nArray3[i] ^ nArray4[i];
        }
    }

    protected void xor_array(int[] nArray, int[] nArray2, int[] nArray3, int n) {
        for (int i = 0; i < this.stateSizeWords; ++i) {
            nArray[i] = nArray2[i] ^ nArray3[i + n];
        }
    }

    protected void matrix_mul(int[] nArray, int[] nArray2, int[] nArray3, int n) {
        this.matrix_mul_offset(nArray, 0, nArray2, 0, nArray3, n);
    }

    protected void matrix_mul_offset(int[] nArray, int n, int[] nArray2, int n2, int[] nArray3, int n3) {
        int[] nArray4 = new int[16];
        nArray4[this.stateSizeWords - 1] = 0;
        int n4 = this.stateSizeBits / 32;
        int n5 = this.stateSizeWords * 32 - this.stateSizeBits;
        int n6 = -1 >>> n5;
        n6 = Bits.bitPermuteStepSimple(n6, 0x55555555, 1);
        n6 = Bits.bitPermuteStepSimple(n6, 0x33333333, 2);
        n6 = Bits.bitPermuteStepSimple(n6, 0xF0F0F0F, 4);
        for (int i = 0; i < this.stateSizeBits; ++i) {
            int n7;
            int n8 = 0;
            for (n7 = 0; n7 < n4; ++n7) {
                int n9 = i * this.stateSizeWords + n7;
                n8 ^= nArray2[n2 + n7] & nArray3[n3 + n9];
            }
            if (n5 > 0) {
                n7 = i * this.stateSizeWords + n4;
                n8 ^= nArray2[n2 + n4] & nArray3[n3 + n7] & n6;
            }
            Utils.setBit(nArray4, i, Utils.parity32(n8));
        }
        System.arraycopy(nArray4, 0, nArray, n, this.stateSizeWords);
    }
}

