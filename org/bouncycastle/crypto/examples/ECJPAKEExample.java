/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.examples;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKECurve;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKECurves;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKEParticipant;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKERound1Payload;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKERound2Payload;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKERound3Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.math.ec.ECPoint;

public class ECJPAKEExample {
    public static void main(String[] stringArray) throws CryptoException {
        ECJPAKECurve eCJPAKECurve = ECJPAKECurves.NIST_P256;
        BigInteger bigInteger = eCJPAKECurve.getA();
        BigInteger bigInteger2 = eCJPAKECurve.getB();
        ECPoint eCPoint = eCJPAKECurve.getG();
        BigInteger bigInteger3 = eCJPAKECurve.getH();
        BigInteger bigInteger4 = eCJPAKECurve.getN();
        BigInteger bigInteger5 = eCJPAKECurve.getQ();
        String string = "password";
        String string2 = "password";
        System.out.println("********* Initialization **********");
        System.out.println("Public parameters for the elliptic curve over prime field:");
        System.out.println("Curve param a (" + bigInteger.bitLength() + " bits): " + bigInteger.toString(16));
        System.out.println("Curve param b (" + bigInteger2.bitLength() + " bits): " + bigInteger2.toString(16));
        System.out.println("Co-factor h (" + bigInteger3.bitLength() + " bits): " + bigInteger3.toString(16));
        System.out.println("Base point G (" + eCPoint.getEncoded(true).length + " bytes): " + new BigInteger(eCPoint.getEncoded(true)).toString(16));
        System.out.println("X coord of G (G not normalised) (" + eCPoint.getXCoord().toBigInteger().bitLength() + " bits): " + eCPoint.getXCoord().toBigInteger().toString(16));
        System.out.println("y coord of G (G not normalised) (" + eCPoint.getYCoord().toBigInteger().bitLength() + " bits): " + eCPoint.getYCoord().toBigInteger().toString(16));
        System.out.println("Order of the base point n (" + bigInteger4.bitLength() + " bits): " + bigInteger4.toString(16));
        System.out.println("Prime field q (" + bigInteger5.bitLength() + " bits): " + bigInteger5.toString(16));
        System.out.println("");
        System.out.println("(Secret passwords used by Alice and Bob: \"" + string + "\" and \"" + string2 + "\")\n");
        SavableDigest savableDigest = SHA256Digest.newInstance();
        SecureRandom secureRandom = new SecureRandom();
        ECJPAKEParticipant eCJPAKEParticipant = new ECJPAKEParticipant("alice", string.toCharArray(), eCJPAKECurve, savableDigest, secureRandom);
        ECJPAKEParticipant eCJPAKEParticipant2 = new ECJPAKEParticipant("bob", string2.toCharArray(), eCJPAKECurve, savableDigest, secureRandom);
        ECJPAKERound1Payload eCJPAKERound1Payload = eCJPAKEParticipant.createRound1PayloadToSend();
        ECJPAKERound1Payload eCJPAKERound1Payload2 = eCJPAKEParticipant2.createRound1PayloadToSend();
        System.out.println("************ Round 1 **************");
        System.out.println("Alice sends to Bob: ");
        System.out.println("g^{x1}=" + new BigInteger(eCJPAKERound1Payload.getGx1().getEncoded(true)).toString(16));
        System.out.println("g^{x2}=" + new BigInteger(eCJPAKERound1Payload.getGx2().getEncoded(true)).toString(16));
        System.out.println("KP{x1}: {V=" + new BigInteger(eCJPAKERound1Payload.getKnowledgeProofForX1().getV().getEncoded(true)).toString(16) + "; r=" + eCJPAKERound1Payload.getKnowledgeProofForX1().getr().toString(16) + "}");
        System.out.println("KP{x2}: {V=" + new BigInteger(eCJPAKERound1Payload.getKnowledgeProofForX2().getV().getEncoded(true)).toString(16) + "; r=" + eCJPAKERound1Payload.getKnowledgeProofForX2().getr().toString(16) + "}");
        System.out.println("");
        System.out.println("Bob sends to Alice: ");
        System.out.println("g^{x3}=" + new BigInteger(eCJPAKERound1Payload2.getGx1().getEncoded(true)).toString(16));
        System.out.println("g^{x4}=" + new BigInteger(eCJPAKERound1Payload2.getGx2().getEncoded(true)).toString(16));
        System.out.println("KP{x3}: {V=" + new BigInteger(eCJPAKERound1Payload2.getKnowledgeProofForX1().getV().getEncoded(true)).toString(16) + "; r=" + eCJPAKERound1Payload2.getKnowledgeProofForX1().getr().toString(16) + "}");
        System.out.println("KP{x4}: {V=" + new BigInteger(eCJPAKERound1Payload2.getKnowledgeProofForX2().getV().getEncoded(true)).toString(16) + "; r=" + eCJPAKERound1Payload2.getKnowledgeProofForX2().getr().toString(16) + "}");
        System.out.println("");
        eCJPAKEParticipant.validateRound1PayloadReceived(eCJPAKERound1Payload2);
        System.out.println("Alice checks g^{x4}!=1: OK");
        System.out.println("Alice checks KP{x3}: OK");
        System.out.println("Alice checks KP{x4}: OK");
        System.out.println("");
        eCJPAKEParticipant2.validateRound1PayloadReceived(eCJPAKERound1Payload);
        System.out.println("Bob checks g^{x2}!=1: OK");
        System.out.println("Bob checks KP{x1},: OK");
        System.out.println("Bob checks KP{x2},: OK");
        System.out.println("");
        ECJPAKERound2Payload eCJPAKERound2Payload = eCJPAKEParticipant.createRound2PayloadToSend();
        ECJPAKERound2Payload eCJPAKERound2Payload2 = eCJPAKEParticipant2.createRound2PayloadToSend();
        System.out.println("************ Round 2 **************");
        System.out.println("Alice sends to Bob: ");
        System.out.println("A=" + new BigInteger(eCJPAKERound2Payload.getA().getEncoded(true)).toString(16));
        System.out.println("KP{x2*s}: {V=" + new BigInteger(eCJPAKERound2Payload.getKnowledgeProofForX2s().getV().getEncoded(true)).toString(16) + ", r=" + eCJPAKERound2Payload.getKnowledgeProofForX2s().getr().toString(16) + "}");
        System.out.println("");
        System.out.println("Bob sends to Alice");
        System.out.println("B=" + new BigInteger(eCJPAKERound2Payload2.getA().getEncoded(true)).toString(16));
        System.out.println("KP{x4*s}: {V=" + new BigInteger(eCJPAKERound2Payload2.getKnowledgeProofForX2s().getV().getEncoded(true)).toString(16) + ", r=" + eCJPAKERound2Payload2.getKnowledgeProofForX2s().getr().toString(16) + "}");
        System.out.println("");
        eCJPAKEParticipant.validateRound2PayloadReceived(eCJPAKERound2Payload2);
        System.out.println("Alice checks KP{x4*s}: OK\n");
        eCJPAKEParticipant2.validateRound2PayloadReceived(eCJPAKERound2Payload);
        System.out.println("Bob checks KP{x2*s}: OK\n");
        BigInteger bigInteger6 = eCJPAKEParticipant.calculateKeyingMaterial();
        BigInteger bigInteger7 = eCJPAKEParticipant2.calculateKeyingMaterial();
        System.out.println("********* After round 2 ***********");
        System.out.println("Alice computes key material \t K=" + bigInteger6.toString(16));
        System.out.println("Bob computes key material \t K=" + bigInteger7.toString(16));
        System.out.println();
        BigInteger bigInteger8 = ECJPAKEExample.deriveSessionKey(bigInteger6);
        BigInteger bigInteger9 = ECJPAKEExample.deriveSessionKey(bigInteger7);
        ECJPAKERound3Payload eCJPAKERound3Payload = eCJPAKEParticipant.createRound3PayloadToSend(bigInteger6);
        ECJPAKERound3Payload eCJPAKERound3Payload2 = eCJPAKEParticipant2.createRound3PayloadToSend(bigInteger7);
        System.out.println("************ Round 3 **************");
        System.out.println("Alice sends to Bob: ");
        System.out.println("MacTag=" + eCJPAKERound3Payload.getMacTag().toString(16));
        System.out.println("");
        System.out.println("Bob sends to Alice: ");
        System.out.println("MacTag=" + eCJPAKERound3Payload2.getMacTag().toString(16));
        System.out.println("");
        eCJPAKEParticipant.validateRound3PayloadReceived(eCJPAKERound3Payload2, bigInteger6);
        System.out.println("Alice checks MacTag: OK\n");
        eCJPAKEParticipant2.validateRound3PayloadReceived(eCJPAKERound3Payload, bigInteger7);
        System.out.println("Bob checks MacTag: OK\n");
        System.out.println();
        System.out.println("MacTags validated, therefore the keying material matches.");
    }

    private static BigInteger deriveSessionKey(BigInteger bigInteger) {
        SavableDigest savableDigest = SHA256Digest.newInstance();
        byte[] byArray = bigInteger.toByteArray();
        byte[] byArray2 = new byte[savableDigest.getDigestSize()];
        savableDigest.update(byArray, 0, byArray.length);
        savableDigest.doFinal(byArray2, 0);
        return new BigInteger(byArray2);
    }
}

