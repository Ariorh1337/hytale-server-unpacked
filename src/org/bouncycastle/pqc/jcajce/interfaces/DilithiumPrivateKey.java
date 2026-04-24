package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

@Deprecated
public interface DilithiumPrivateKey extends PrivateKey, DilithiumKey {
   DilithiumPublicKey getPublicKey();
}
