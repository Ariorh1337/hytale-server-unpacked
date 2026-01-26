/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.pqc.crypto.KEMParameters;
import org.bouncycastle.pqc.crypto.saber.SABEREngine;

public class SABERParameters
implements KEMParameters {
    public static final SABERParameters lightsaberkem128r3 = new SABERParameters("lightsaberkem128r3", 2, 128, false, false);
    public static final SABERParameters saberkem128r3 = new SABERParameters("saberkem128r3", 3, 128, false, false);
    public static final SABERParameters firesaberkem128r3 = new SABERParameters("firesaberkem128r3", 4, 128, false, false);
    public static final SABERParameters lightsaberkem192r3 = new SABERParameters("lightsaberkem192r3", 2, 192, false, false);
    public static final SABERParameters saberkem192r3 = new SABERParameters("saberkem192r3", 3, 192, false, false);
    public static final SABERParameters firesaberkem192r3 = new SABERParameters("firesaberkem192r3", 4, 192, false, false);
    public static final SABERParameters lightsaberkem256r3 = new SABERParameters("lightsaberkem256r3", 2, 256, false, false);
    public static final SABERParameters saberkem256r3 = new SABERParameters("saberkem256r3", 3, 256, false, false);
    public static final SABERParameters firesaberkem256r3 = new SABERParameters("firesaberkem256r3", 4, 256, false, false);
    public static final SABERParameters lightsaberkem90sr3 = new SABERParameters("lightsaberkem90sr3", 2, 256, true, false);
    public static final SABERParameters saberkem90sr3 = new SABERParameters("saberkem90sr3", 3, 256, true, false);
    public static final SABERParameters firesaberkem90sr3 = new SABERParameters("firesaberkem90sr3", 4, 256, true, false);
    public static final SABERParameters ulightsaberkemr3 = new SABERParameters("ulightsaberkemr3", 2, 256, false, true);
    public static final SABERParameters usaberkemr3 = new SABERParameters("usaberkemr3", 3, 256, false, true);
    public static final SABERParameters ufiresaberkemr3 = new SABERParameters("ufiresaberkemr3", 4, 256, false, true);
    public static final SABERParameters ulightsaberkem90sr3 = new SABERParameters("ulightsaberkem90sr3", 2, 256, true, true);
    public static final SABERParameters usaberkem90sr3 = new SABERParameters("usaberkem90sr3", 3, 256, true, true);
    public static final SABERParameters ufiresaberkem90sr3 = new SABERParameters("ufiresaberkem90sr3", 4, 256, true, true);
    private final String name;
    private final int l;
    private final int defaultKeySize;
    private final SABEREngine engine;

    public SABERParameters(String string, int n, int n2, boolean bl, boolean bl2) {
        this.name = string;
        this.l = n;
        this.defaultKeySize = n2;
        this.engine = new SABEREngine(n, n2, bl, bl2);
    }

    public String getName() {
        return this.name;
    }

    public int getL() {
        return this.l;
    }

    public int getSessionKeySize() {
        return this.defaultKeySize;
    }

    public SABEREngine getEngine() {
        return this.engine;
    }
}

