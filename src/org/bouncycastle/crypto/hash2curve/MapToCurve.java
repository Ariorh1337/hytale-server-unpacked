package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

public interface MapToCurve {
   ECPoint process(BigInteger var1);
}
