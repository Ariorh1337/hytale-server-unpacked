package io.netty.channel;

public interface MessageSizeEstimator {
   MessageSizeEstimator.Handle newHandle();

   interface Handle {
      int size(Object var1);
   }
}
