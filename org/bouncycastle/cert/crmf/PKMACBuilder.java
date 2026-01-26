/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACValuesCalculator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.Strings;

public class PKMACBuilder
implements PBEMacCalculatorProvider {
    private AlgorithmIdentifier owf;
    private int iterationCount;
    private AlgorithmIdentifier mac;
    private int saltLength = 20;
    private SecureRandom random;
    private PKMACValuesCalculator calculator;
    private PBMParameter parameters;
    private int maxIterations;

    public PKMACBuilder(PKMACValuesCalculator pKMACValuesCalculator) {
        this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, DERNull.INSTANCE), pKMACValuesCalculator);
    }

    public PKMACBuilder(PKMACValuesCalculator pKMACValuesCalculator, int n) {
        this.maxIterations = n;
        this.calculator = pKMACValuesCalculator;
    }

    private PKMACBuilder(AlgorithmIdentifier algorithmIdentifier, int n, AlgorithmIdentifier algorithmIdentifier2, PKMACValuesCalculator pKMACValuesCalculator) {
        this.owf = algorithmIdentifier;
        this.iterationCount = n;
        this.mac = algorithmIdentifier2;
        this.calculator = pKMACValuesCalculator;
    }

    public PKMACBuilder setSaltLength(int n) {
        if (n < 8) {
            throw new IllegalArgumentException("salt length must be at least 8 bytes");
        }
        this.saltLength = n;
        return this;
    }

    public PKMACBuilder setIterationCount(int n) {
        if (n < 100) {
            throw new IllegalArgumentException("iteration count must be at least 100");
        }
        this.checkIterationCountCeiling(n);
        this.iterationCount = n;
        return this;
    }

    public PKMACBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public PKMACBuilder setParameters(PBMParameter pBMParameter) {
        this.checkIterationCountCeiling(pBMParameter.getIterationCount().intValueExact());
        this.parameters = pBMParameter;
        return this;
    }

    @Override
    public MacCalculator get(AlgorithmIdentifier algorithmIdentifier, char[] cArray) throws OperatorCreationException {
        if (!CMPObjectIdentifiers.passwordBasedMac.equals(algorithmIdentifier.getAlgorithm())) {
            throw new OperatorCreationException("protection algorithm not mac based");
        }
        this.setParameters(PBMParameter.getInstance(algorithmIdentifier.getParameters()));
        try {
            return this.build(cArray);
        }
        catch (CRMFException cRMFException) {
            throw new OperatorCreationException(cRMFException.getMessage(), cRMFException.getCause());
        }
    }

    public MacCalculator build(char[] cArray) throws CRMFException {
        PBMParameter pBMParameter = this.parameters;
        if (pBMParameter == null) {
            pBMParameter = this.genParameters();
        }
        return this.genCalculator(pBMParameter, cArray);
    }

    private void checkIterationCountCeiling(int n) {
        if (this.maxIterations > 0 && n > this.maxIterations) {
            throw new IllegalArgumentException("iteration count exceeds limit (" + n + " > " + this.maxIterations + ")");
        }
    }

    private MacCalculator genCalculator(final PBMParameter pBMParameter, char[] cArray) throws CRMFException {
        byte[] byArray = Strings.toUTF8ByteArray(cArray);
        byte[] byArray2 = pBMParameter.getSalt().getOctets();
        byte[] byArray3 = new byte[byArray.length + byArray2.length];
        System.arraycopy(byArray, 0, byArray3, 0, byArray.length);
        System.arraycopy(byArray2, 0, byArray3, byArray.length, byArray2.length);
        this.calculator.setup(pBMParameter.getOwf(), pBMParameter.getMac());
        int n = pBMParameter.getIterationCount().intValueExact();
        do {
            byArray3 = this.calculator.calculateDigest(byArray3);
        } while (--n > 0);
        final byte[] byArray4 = byArray3;
        return new MacCalculator(){
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final /* synthetic */ PKMACBuilder this$0;
            {
                this.this$0 = pKMACBuilder;
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, pBMParameter);
            }

            @Override
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), byArray4);
            }

            @Override
            public OutputStream getOutputStream() {
                return this.bOut;
            }

            @Override
            public byte[] getMac() {
                try {
                    return this.this$0.calculator.calculateMac(byArray4, this.bOut.toByteArray());
                }
                catch (CRMFException cRMFException) {
                    throw new RuntimeOperatorException("exception calculating mac: " + cRMFException.getMessage(), cRMFException);
                }
            }
        };
    }

    private PBMParameter genParameters() {
        byte[] byArray = new byte[this.saltLength];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(byArray);
        return new PBMParameter(byArray, this.owf, this.iterationCount, this.mac);
    }
}

