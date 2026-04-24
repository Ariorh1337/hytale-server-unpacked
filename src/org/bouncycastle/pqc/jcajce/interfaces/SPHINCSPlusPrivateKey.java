package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

@Deprecated
public interface SPHINCSPlusPrivateKey extends PrivateKey, SPHINCSPlusKey {
   SPHINCSPlusPublicKey getPublicKey();
}
