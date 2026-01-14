package io.netty.channel.socket.nio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.nio.file.Path;

final class NioDomainSocketUtil {
   private static final Method OF_METHOD;
   private static final Method GET_PATH_METHOD;

   static SocketAddress newUnixDomainSocketAddress(String path) {
      if (OF_METHOD == null) {
         throw new IllegalStateException();
      }

      try {
         return (SocketAddress)OF_METHOD.invoke(null, path);
      } catch (IllegalAccessException | InvocationTargetException e) {
         throw new IllegalStateException(e);
      }
   }

   static void deleteSocketFile(SocketAddress address) {
      if (GET_PATH_METHOD == null) {
         throw new IllegalStateException();
      }

      try {
         Path path = (Path)GET_PATH_METHOD.invoke(address);
         if (path != null) {
            path.toFile().delete();
         }
      } catch (IllegalAccessException | InvocationTargetException e) {
         throw new IllegalStateException(e);
      }
   }

   private NioDomainSocketUtil() {
   }

   static {
      Method ofMethod;
      Method getPathMethod;
      try {
         Class<?> clazz = Class.forName("java.net.UnixDomainSocketAddress");
         ofMethod = clazz.getMethod("of", String.class);
         getPathMethod = clazz.getMethod("getPath");
      } catch (Throwable error) {
         ofMethod = null;
         getPathMethod = null;
      }

      OF_METHOD = ofMethod;
      GET_PATH_METHOD = getPathMethod;
   }
}
