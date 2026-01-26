/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.ResourceRetriever;
import java.util.List;
import java.util.Map;

public interface RestrictedResourceRetriever
extends ResourceRetriever {
    public int getConnectTimeout();

    public void setConnectTimeout(int var1);

    public int getReadTimeout();

    public void setReadTimeout(int var1);

    public int getSizeLimit();

    public void setSizeLimit(int var1);

    public Map<String, List<String>> getHeaders();

    public void setHeaders(Map<String, List<String>> var1);
}

