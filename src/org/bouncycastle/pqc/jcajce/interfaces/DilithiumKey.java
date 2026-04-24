package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;

@Deprecated
public interface DilithiumKey extends Key {
   DilithiumParameterSpec getParameterSpec();
}
