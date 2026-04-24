package org.bouncycastle.jcajce.spec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class SM2KeyExchangeSpec implements AlgorithmParameterSpec {
   private final PrivateKey ephemeralPrivateKey;
   private final PublicKey otherPartyEphemeralKey;
   private final byte[] id;
   private final byte[] otherPartyId;
   private final boolean initiator;

   public SM2KeyExchangeSpec(boolean var1, PrivateKey var2, PublicKey var3, byte[] var4, byte[] var5) {
      this.initiator = var1;
      this.ephemeralPrivateKey = var2;
      this.otherPartyEphemeralKey = var3;
      this.id = Arrays.clone(var4);
      this.otherPartyId = Arrays.clone(var5);
   }

   public PrivateKey getEphemeralPrivateKey() {
      return this.ephemeralPrivateKey;
   }

   public PublicKey getOtherPartyEphemeralKey() {
      return this.otherPartyEphemeralKey;
   }

   public byte[] getId() {
      return Arrays.clone(this.id);
   }

   public byte[] getOtherPartyId() {
      return Arrays.clone(this.otherPartyId);
   }

   public boolean isInitiator() {
      return this.initiator;
   }
}
