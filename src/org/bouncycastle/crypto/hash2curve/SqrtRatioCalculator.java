package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;
import org.bouncycastle.crypto.hash2curve.impl.SqrtRatio;

public interface SqrtRatioCalculator {
   SqrtRatio sqrtRatio(BigInteger var1, BigInteger var2);
}
