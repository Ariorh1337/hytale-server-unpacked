package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@ThreadSafe
@Deprecated
public class RemoteJWKSet<C extends SecurityContext> implements JWKSource<C> {
   public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 500;
   public static final int DEFAULT_HTTP_READ_TIMEOUT = 500;
   public static final int DEFAULT_HTTP_SIZE_LIMIT = 51200;
   private final URL jwkSetURL;
   private final JWKSource<C> failoverJWKSource;
   private final JWKSetCache jwkSetCache;
   private final ResourceRetriever jwkSetRetriever;

   public static int resolveDefaultHTTPConnectTimeout() {
      return resolveDefault(RemoteJWKSet.class.getName() + ".defaultHttpConnectTimeout", 500);
   }

   public static int resolveDefaultHTTPReadTimeout() {
      return resolveDefault(RemoteJWKSet.class.getName() + ".defaultHttpReadTimeout", 500);
   }

   public static int resolveDefaultHTTPSizeLimit() {
      return resolveDefault(RemoteJWKSet.class.getName() + ".defaultHttpSizeLimit", 51200);
   }

   private static int resolveDefault(String sysPropertyName, int defaultValue) {
      String value = System.getProperty(sysPropertyName);
      if (value == null) {
         return defaultValue;
      }

      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException e) {
         return defaultValue;
      }
   }

   public RemoteJWKSet(URL jwkSetURL) {
      this(jwkSetURL, (JWKSource<C>)null);
   }

   public RemoteJWKSet(URL jwkSetURL, JWKSource<C> failoverJWKSource) {
      this(jwkSetURL, failoverJWKSource, null, null);
   }

   public RemoteJWKSet(URL jwkSetURL, ResourceRetriever resourceRetriever) {
      this(jwkSetURL, resourceRetriever, null);
   }

   public RemoteJWKSet(URL jwkSetURL, ResourceRetriever resourceRetriever, JWKSetCache jwkSetCache) {
      this(jwkSetURL, null, resourceRetriever, jwkSetCache);
   }

   public RemoteJWKSet(URL jwkSetURL, JWKSource<C> failoverJWKSource, ResourceRetriever resourceRetriever, JWKSetCache jwkSetCache) {
      this.jwkSetURL = Objects.requireNonNull(jwkSetURL);
      this.failoverJWKSource = failoverJWKSource;
      if (resourceRetriever != null) {
         this.jwkSetRetriever = resourceRetriever;
      } else {
         this.jwkSetRetriever = new DefaultResourceRetriever(resolveDefaultHTTPConnectTimeout(), resolveDefaultHTTPReadTimeout(), resolveDefaultHTTPSizeLimit());
      }

      if (jwkSetCache != null) {
         this.jwkSetCache = jwkSetCache;
      } else {
         this.jwkSetCache = new DefaultJWKSetCache();
      }
   }

   private JWKSet updateJWKSetFromURL() throws RemoteKeySourceException {
      Resource res;
      try {
         res = this.jwkSetRetriever.retrieveResource(this.jwkSetURL);
      } catch (IOException e) {
         throw new RemoteKeySourceException("Couldn't retrieve remote JWK set: " + e.getMessage(), e);
      }

      JWKSet jwkSet;
      try {
         jwkSet = JWKSet.parse(res.getContent());
      } catch (ParseException e) {
         throw new RemoteKeySourceException("Couldn't parse remote JWK set: " + e.getMessage(), e);
      }

      this.jwkSetCache.put(jwkSet);
      return jwkSet;
   }

   public URL getJWKSetURL() {
      return this.jwkSetURL;
   }

   public JWKSource<C> getFailoverJWKSource() {
      return this.failoverJWKSource;
   }

   public ResourceRetriever getResourceRetriever() {
      return this.jwkSetRetriever;
   }

   public JWKSetCache getJWKSetCache() {
      return this.jwkSetCache;
   }

   public JWKSet getCachedJWKSet() {
      return this.jwkSetCache.get();
   }

   protected static String getFirstSpecifiedKeyID(JWKMatcher jwkMatcher) {
      Set<String> keyIDs = jwkMatcher.getKeyIDs();
      if (keyIDs != null && !keyIDs.isEmpty()) {
         for (String id : keyIDs) {
            if (id != null) {
               return id;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private List<JWK> failover(Exception exception, JWKSelector jwkSelector, C context) throws RemoteKeySourceException {
      if (this.getFailoverJWKSource() == null) {
         return null;
      }

      try {
         return this.getFailoverJWKSource().get(jwkSelector, context);
      } catch (KeySourceException kse) {
         throw new RemoteKeySourceException(exception.getMessage() + "; Failover JWK source retrieval failed with: " + kse.getMessage(), kse);
      }
   }

   @Override
   public List<JWK> get(JWKSelector jwkSelector, C context) throws RemoteKeySourceException {
      JWKSet jwkSet = this.jwkSetCache.get();
      if (this.jwkSetCache.requiresRefresh() || jwkSet == null) {
         try {
            synchronized (this) {
               jwkSet = this.jwkSetCache.get();
               if (this.jwkSetCache.requiresRefresh() || jwkSet == null) {
                  jwkSet = this.updateJWKSetFromURL();
               }
            }
         } catch (Exception e) {
            List<JWK> failoverMatches = this.failover(e, jwkSelector, context);
            if (failoverMatches != null) {
               return failoverMatches;
            }

            if (jwkSet == null) {
               throw e;
            }
         }
      }

      List<JWK> matches = jwkSelector.select(jwkSet);
      if (!matches.isEmpty()) {
         return matches;
      }

      String soughtKeyID = getFirstSpecifiedKeyID(jwkSelector.getMatcher());
      if (soughtKeyID == null) {
         return Collections.emptyList();
      }

      if (jwkSet.getKeyByKeyId(soughtKeyID) != null) {
         return Collections.emptyList();
      }

      try {
         synchronized (this) {
            if (jwkSet == this.jwkSetCache.get()) {
               jwkSet = this.updateJWKSetFromURL();
            } else {
               jwkSet = this.jwkSetCache.get();
            }
         }
      } catch (KeySourceException e) {
         List<JWK> failoverMatches = this.failover(e, jwkSelector, context);
         if (failoverMatches != null) {
            return failoverMatches;
         }

         throw e;
      }

      return jwkSet == null ? Collections.emptyList() : jwkSelector.select(jwkSet);
   }
}
