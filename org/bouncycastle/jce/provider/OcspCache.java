/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.ProvOcspRevocationChecker;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

class OcspCache {
    private static final int DEFAULT_TIMEOUT = 15000;
    private static final int DEFAULT_MAX_RESPONSE_SIZE = 32768;
    private static Map<URI, WeakReference<Map<CertID, OCSPResponse>>> cache = Collections.synchronizedMap(new WeakHashMap());

    OcspCache() {
    }

    static OCSPResponse getOcspResponse(CertID certID, PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters, URI uRI, X509Certificate x509Certificate, List<Extension> list, JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
        Object object;
        Object object2;
        Extension extension;
        Object object3;
        Object object4;
        HashMap<CertID, OCSPResponse> hashMap = null;
        WeakReference<Map<CertID, OCSPResponse>> weakReference = cache.get(uRI);
        if (weakReference != null) {
            hashMap = (HashMap<CertID, OCSPResponse>)weakReference.get();
        }
        if (hashMap != null && (object4 = (OCSPResponse)hashMap.get(certID)) != null) {
            object3 = BasicOCSPResponse.getInstance(ASN1OctetString.getInstance(((OCSPResponse)object4).getResponseBytes().getResponse()).getOctets());
            boolean bl = OcspCache.isCertIDFoundAndCurrent((BasicOCSPResponse)object3, pKIXCertRevocationCheckerParameters.getValidDate(), certID);
            if (bl) {
                return object4;
            }
            hashMap.remove(certID);
        }
        try {
            object4 = uRI.toURL();
        }
        catch (MalformedURLException malformedURLException) {
            throw new CertPathValidatorException("configuration error: " + malformedURLException.getMessage(), (Throwable)malformedURLException, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        }
        object3 = new ASN1EncodableVector();
        ((ASN1EncodableVector)object3).add(new Request(certID, null));
        List<Extension> list2 = list;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        byte[] byArray = null;
        for (int i = 0; i != list2.size(); ++i) {
            extension = list2.get(i);
            object2 = new ASN1ObjectIdentifier(extension.getId());
            object = new DEROctetString(extension.getValue());
            if (OCSPObjectIdentifiers.id_pkix_ocsp_nonce.equals((ASN1Primitive)object2)) {
                byArray = Arrays.clone(((ASN1OctetString)object).getOctets());
            }
            aSN1EncodableVector.add(new org.bouncycastle.asn1.x509.Extension((ASN1ObjectIdentifier)object2, extension.isCritical(), (ASN1OctetString)object));
        }
        TBSRequest tBSRequest = aSN1EncodableVector.size() != 0 ? new TBSRequest(null, (ASN1Sequence)new DERSequence((ASN1EncodableVector)object3), Extensions.getInstance(new DERSequence(aSN1EncodableVector))) : new TBSRequest(null, (ASN1Sequence)new DERSequence((ASN1EncodableVector)object3), (Extensions)null);
        extension = null;
        try {
            OCSPResponse oCSPResponse;
            object2 = new OCSPRequest(tBSRequest, (Signature)((Object)extension)).getEncoded();
            object = (HttpURLConnection)((URL)object4).openConnection();
            ((URLConnection)object).setConnectTimeout(15000);
            ((URLConnection)object).setReadTimeout(15000);
            ((URLConnection)object).setDoOutput(true);
            ((URLConnection)object).setDoInput(true);
            ((HttpURLConnection)object).setRequestMethod("POST");
            ((URLConnection)object).setRequestProperty("Content-type", "application/ocsp-request");
            ((URLConnection)object).setRequestProperty("Content-length", String.valueOf(((Object)object2).length));
            OutputStream outputStream = ((URLConnection)object).getOutputStream();
            outputStream.write((byte[])object2);
            outputStream.flush();
            InputStream inputStream = ((URLConnection)object).getInputStream();
            int n = ((URLConnection)object).getContentLength();
            if (n < 0) {
                n = 32768;
            }
            if (0 == (oCSPResponse = OCSPResponse.getInstance(Streams.readAllLimited(inputStream, n))).getResponseStatus().getIntValue()) {
                boolean bl = false;
                ResponseBytes responseBytes = ResponseBytes.getInstance(oCSPResponse.getResponseBytes());
                if (responseBytes.getResponseType().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
                    BasicOCSPResponse basicOCSPResponse = BasicOCSPResponse.getInstance(responseBytes.getResponse().getOctets());
                    boolean bl2 = bl = ProvOcspRevocationChecker.validatedOcspResponse(basicOCSPResponse, pKIXCertRevocationCheckerParameters, byArray, x509Certificate, jcaJceHelper) && OcspCache.isCertIDFoundAndCurrent(basicOCSPResponse, pKIXCertRevocationCheckerParameters.getValidDate(), certID);
                }
                if (!bl) {
                    throw new CertPathValidatorException("OCSP response failed to validate", null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
                }
                weakReference = cache.get(uRI);
                if (weakReference != null) {
                    hashMap = (Map)weakReference.get();
                }
                if (hashMap != null) {
                    hashMap.put(certID, oCSPResponse);
                } else {
                    hashMap = new HashMap<CertID, OCSPResponse>();
                    hashMap.put(certID, oCSPResponse);
                    cache.put(uRI, new WeakReference(hashMap));
                }
                return oCSPResponse;
            }
            throw new CertPathValidatorException("OCSP responder failed: " + oCSPResponse.getResponseStatus().getValue(), null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        }
        catch (IOException iOException) {
            throw new CertPathValidatorException("configuration error: " + iOException.getMessage(), (Throwable)iOException, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        }
    }

    private static boolean isCertIDFoundAndCurrent(BasicOCSPResponse basicOCSPResponse, Date date, CertID certID) {
        ResponseData responseData = ResponseData.getInstance(basicOCSPResponse.getTbsResponseData());
        ASN1Sequence aSN1Sequence = responseData.getResponses();
        for (int i = 0; i != aSN1Sequence.size(); ++i) {
            SingleResponse singleResponse = SingleResponse.getInstance(aSN1Sequence.getObjectAt(i));
            if (!certID.equals(singleResponse.getCertID())) continue;
            ASN1GeneralizedTime aSN1GeneralizedTime = singleResponse.getNextUpdate();
            try {
                if (aSN1GeneralizedTime != null && date.after(aSN1GeneralizedTime.getDate())) {
                    return false;
                }
            }
            catch (ParseException parseException) {
                return false;
            }
            return true;
        }
        return false;
    }
}

