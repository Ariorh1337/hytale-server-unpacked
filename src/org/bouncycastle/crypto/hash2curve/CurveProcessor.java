package org.bouncycastle.crypto.hash2curve;

import org.bouncycastle.crypto.hash2curve.data.AffineXY;
import org.bouncycastle.math.ec.ECPoint;

public interface CurveProcessor {
   ECPoint add(ECPoint var1, ECPoint var2);

   ECPoint clearCofactor(ECPoint var1);

   AffineXY mapToAffineXY(ECPoint var1);
}
