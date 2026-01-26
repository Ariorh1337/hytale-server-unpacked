/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.X509CRLStoreSelector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

abstract class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    static Set findCRLs(X509CRLStoreSelector x509CRLStoreSelector, PKIXParameters pKIXParameters) throws AnnotatedException {
        return PKIXCRLUtil.findCRLs(new PKIXCRLStoreSelector.Builder(x509CRLStoreSelector).build(), pKIXParameters);
    }

    static Set findCRLs(PKIXCRLStoreSelector pKIXCRLStoreSelector, PKIXParameters pKIXParameters) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        try {
            PKIXCRLUtil.findCRLs(hashSet, pKIXCRLStoreSelector, pKIXParameters.getCertStores());
        }
        catch (AnnotatedException annotatedException) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", annotatedException);
        }
        return hashSet;
    }

    static Set findCRLs(PKIXCRLStoreSelector pKIXCRLStoreSelector, Date date, List list, List list2) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        try {
            PKIXCRLUtil.findCRLs(hashSet, pKIXCRLStoreSelector, list2);
            PKIXCRLUtil.findCRLs(hashSet, pKIXCRLStoreSelector, list);
        }
        catch (AnnotatedException annotatedException) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", annotatedException);
        }
        HashSet<X509CRL> hashSet2 = new HashSet<X509CRL>();
        for (Object e : hashSet) {
            X509Certificate x509Certificate;
            X509CRL x509CRL = (X509CRL)e;
            Date date2 = x509CRL.getNextUpdate();
            if (date2 != null && !date2.after(date) || null != (x509Certificate = pKIXCRLStoreSelector.getCertificateChecking()) && !x509CRL.getThisUpdate().before(x509Certificate.getNotAfter())) continue;
            hashSet2.add(x509CRL);
        }
        return hashSet2;
    }

    private static void findCRLs(Set set, PKIXCRLStoreSelector pKIXCRLStoreSelector, List list) throws AnnotatedException {
        AnnotatedException annotatedException = null;
        boolean bl = false;
        for (Object e : list) {
            Object object;
            if (e instanceof Store) {
                object = (Store)e;
                try {
                    set.addAll(object.getMatches(pKIXCRLStoreSelector));
                    bl = true;
                }
                catch (StoreException storeException) {
                    annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", storeException);
                }
                continue;
            }
            object = (CertStore)e;
            try {
                set.addAll(PKIXCRLStoreSelector.getCRLs(pKIXCRLStoreSelector, (CertStore)object));
                bl = true;
            }
            catch (CertStoreException certStoreException) {
                annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", certStoreException);
            }
        }
        if (!bl && annotatedException != null) {
            throw annotatedException;
        }
    }
}

