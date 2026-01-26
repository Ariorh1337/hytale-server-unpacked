/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSource;
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
public class RemoteJWKSet<C extends SecurityContext>
implements JWKSource<C> {
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 500;
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 500;
    public static final int DEFAULT_HTTP_SIZE_LIMIT = 51200;
    private final URL jwkSetURL;
    private final JWKSource<C> failoverJWKSource;
    private final JWKSetCache jwkSetCache;
    private final ResourceRetriever jwkSetRetriever;

    public static int resolveDefaultHTTPConnectTimeout() {
        return RemoteJWKSet.resolveDefault(RemoteJWKSet.class.getName() + ".defaultHttpConnectTimeout", 500);
    }

    public static int resolveDefaultHTTPReadTimeout() {
        return RemoteJWKSet.resolveDefault(RemoteJWKSet.class.getName() + ".defaultHttpReadTimeout", 500);
    }

    public static int resolveDefaultHTTPSizeLimit() {
        return RemoteJWKSet.resolveDefault(RemoteJWKSet.class.getName() + ".defaultHttpSizeLimit", 51200);
    }

    private static int resolveDefault(String sysPropertyName, int defaultValue) {
        String value = System.getProperty(sysPropertyName);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public RemoteJWKSet(URL jwkSetURL) {
        this(jwkSetURL, (JWKSource)null);
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
        this.jwkSetRetriever = resourceRetriever != null ? resourceRetriever : new DefaultResourceRetriever(RemoteJWKSet.resolveDefaultHTTPConnectTimeout(), RemoteJWKSet.resolveDefaultHTTPReadTimeout(), RemoteJWKSet.resolveDefaultHTTPSizeLimit());
        this.jwkSetCache = jwkSetCache != null ? jwkSetCache : new DefaultJWKSetCache();
    }

    private JWKSet updateJWKSetFromURL() throws RemoteKeySourceException {
        JWKSet jwkSet;
        Resource res;
        try {
            res = this.jwkSetRetriever.retrieveResource(this.jwkSetURL);
        }
        catch (IOException e) {
            throw new RemoteKeySourceException("Couldn't retrieve remote JWK set: " + e.getMessage(), e);
        }
        try {
            jwkSet = JWKSet.parse(res.getContent());
        }
        catch (ParseException e) {
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
        if (keyIDs == null || keyIDs.isEmpty()) {
            return null;
        }
        for (String id : keyIDs) {
            if (id == null) continue;
            return id;
        }
        return null;
    }

    private List<JWK> failover(Exception exception, JWKSelector jwkSelector, C context) throws RemoteKeySourceException {
        if (this.getFailoverJWKSource() == null) {
            return null;
        }
        try {
            return this.getFailoverJWKSource().get(jwkSelector, context);
        }
        catch (KeySourceException kse) {
            throw new RemoteKeySourceException(exception.getMessage() + "; Failover JWK source retrieval failed with: " + kse.getMessage(), kse);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<JWK> get(JWKSelector jwkSelector, C context) throws RemoteKeySourceException {
        List<JWK> matches;
        JWKSet jwkSet;
        block18: {
            jwkSet = this.jwkSetCache.get();
            if (this.jwkSetCache.requiresRefresh() || jwkSet == null) {
                try {
                    RemoteJWKSet remoteJWKSet = this;
                    synchronized (remoteJWKSet) {
                        jwkSet = this.jwkSetCache.get();
                        if (this.jwkSetCache.requiresRefresh() || jwkSet == null) {
                            jwkSet = this.updateJWKSetFromURL();
                        }
                    }
                }
                catch (Exception e) {
                    List<JWK> failoverMatches = this.failover(e, jwkSelector, context);
                    if (failoverMatches != null) {
                        return failoverMatches;
                    }
                    if (jwkSet != null) break block18;
                    throw e;
                }
            }
        }
        if (!(matches = jwkSelector.select(jwkSet)).isEmpty()) {
            return matches;
        }
        String soughtKeyID = RemoteJWKSet.getFirstSpecifiedKeyID(jwkSelector.getMatcher());
        if (soughtKeyID == null) {
            return Collections.emptyList();
        }
        if (jwkSet.getKeyByKeyId(soughtKeyID) != null) {
            return Collections.emptyList();
        }
        try {
            RemoteJWKSet remoteJWKSet = this;
            synchronized (remoteJWKSet) {
                jwkSet = jwkSet == this.jwkSetCache.get() ? this.updateJWKSetFromURL() : this.jwkSetCache.get();
            }
        }
        catch (KeySourceException e) {
            List<JWK> failoverMatches = this.failover(e, jwkSelector, context);
            if (failoverMatches != null) {
                return failoverMatches;
            }
            throw e;
        }
        if (jwkSet == null) {
            return Collections.emptyList();
        }
        return jwkSelector.select(jwkSet);
    }
}

