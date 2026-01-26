/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.SecretWithEncapsulationImpl;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECIESKEMGenerator
implements EncapsulatedSecretGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private final int keySize;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKEMGenerator(int n, DerivationFunction derivationFunction, SecureRandom secureRandom) {
        this.keySize = n;
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKEMGenerator(int n, DerivationFunction derivationFunction, SecureRandom secureRandom, boolean bl, boolean bl2, boolean bl3) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.keySize = n;
        this.CofactorMode = bl;
        this.OldCofactorMode = bl ? false : bl2;
        this.SingleHashMode = bl3;
    }

    private ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        if (!(asymmetricKeyParameter instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("EC public key required");
        }
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricKeyParameter;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(eCPublicKeyParameters.getParameters().getCurve()), asymmetricKeyParameter, CryptoServicePurpose.ENCRYPTION));
        ECDomainParameters eCDomainParameters = eCPublicKeyParameters.getParameters();
        ECCurve eCCurve = eCDomainParameters.getCurve();
        BigInteger bigInteger = eCDomainParameters.getN();
        BigInteger bigInteger2 = eCDomainParameters.getH();
        BigInteger bigInteger3 = BigIntegers.createRandomInRange(ONE, bigInteger, this.rnd);
        BigInteger bigInteger4 = this.OldCofactorMode ? bigInteger3.multiply(bigInteger2).mod(bigInteger) : bigInteger3;
        ECMultiplier eCMultiplier = this.createBasePointMultiplier();
        ECPoint[] eCPointArray = new ECPoint[]{eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger3), eCPublicKeyParameters.getQ().multiply(bigInteger4)};
        eCCurve.normalizeAll(eCPointArray);
        ECPoint eCPoint = eCPointArray[0];
        ECPoint eCPoint2 = eCPointArray[1];
        byte[] byArray = eCPoint.getEncoded(false);
        byte[] byArray2 = new byte[byArray.length];
        System.arraycopy(byArray, 0, byArray2, 0, byArray.length);
        byte[] byArray3 = eCPoint2.getAffineXCoord().getEncoded();
        return new SecretWithEncapsulationImpl(ECIESKEMGenerator.deriveKey(this.SingleHashMode, this.kdf, this.keySize, byArray, byArray3), byArray2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static byte[] deriveKey(boolean bl, DerivationFunction derivationFunction, int n, byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = byArray2;
        if (!bl) {
            byArray3 = Arrays.concatenate(byArray, byArray2);
            Arrays.fill(byArray2, (byte)0);
        }
        try {
            derivationFunction.init(new KDFParameters(byArray3, null));
            byte[] byArray4 = new byte[n];
            derivationFunction.generateBytes(byArray4, 0, byArray4.length);
            byte[] byArray5 = byArray4;
            return byArray5;
        }
        finally {
            Arrays.fill(byArray3, (byte)0);
        }
    }
}

