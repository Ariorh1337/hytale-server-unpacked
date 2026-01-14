package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;

@Deprecated
public interface JdkApplicationProtocolNegotiator extends ApplicationProtocolNegotiator {
   JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory();

   JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory();

   JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory();

   abstract class AllocatorAwareSslEngineWrapperFactory implements JdkApplicationProtocolNegotiator.SslEngineWrapperFactory {
      @Override
      public final SSLEngine wrapSslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
         return this.wrapSslEngine(engine, ByteBufAllocator.DEFAULT, applicationNegotiator, isServer);
      }

      abstract SSLEngine wrapSslEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3, boolean var4);
   }

   interface ProtocolSelectionListener {
      void unsupported();

      void selected(String var1) throws Exception;
   }

   interface ProtocolSelectionListenerFactory {
      JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2);
   }

   interface ProtocolSelector {
      void unsupported();

      String select(List<String> var1) throws Exception;
   }

   interface ProtocolSelectorFactory {
      JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine var1, Set<String> var2);
   }

   interface SslEngineWrapperFactory {
      SSLEngine wrapSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3);
   }
}
