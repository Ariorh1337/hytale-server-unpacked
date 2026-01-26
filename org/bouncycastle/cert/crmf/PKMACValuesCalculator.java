/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;

public interface PKMACValuesCalculator {
    public void setup(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws CRMFException;

    public byte[] calculateDigest(byte[] var1) throws CRMFException;

    public byte[] calculateMac(byte[] var1, byte[] var2) throws CRMFException;
}

