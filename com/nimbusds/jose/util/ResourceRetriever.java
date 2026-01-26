/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.Resource;
import java.io.IOException;
import java.net.URL;

public interface ResourceRetriever {
    public Resource retrieveResource(URL var1) throws IOException;
}

