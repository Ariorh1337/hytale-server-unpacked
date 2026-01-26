/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cmc.CMCException;
import org.bouncycastle.cmc.SimplePKIResponse;
import org.bouncycastle.est.CACertsResponse;
import org.bouncycastle.est.CSRAttributesResponse;
import org.bouncycastle.est.CSRRequestResponse;
import org.bouncycastle.est.ESTAuth;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.ESTSourceConnectionListener;
import org.bouncycastle.est.EnrollmentResponse;
import org.bouncycastle.est.Source;
import org.bouncycastle.est.TLSUniqueProvider;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.ConstantMimeContext;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.MimeParserListener;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

public class ESTService {
    protected static final String CACERTS = "/cacerts";
    protected static final String SIMPLE_ENROLL = "/simpleenroll";
    protected static final String SIMPLE_REENROLL = "/simplereenroll";
    protected static final String FULLCMC = "/fullcmc";
    protected static final String SERVERGEN = "/serverkeygen";
    protected static final String CSRATTRS = "/csrattrs";
    protected static final Set<String> illegalParts = new HashSet<String>();
    private final String server;
    private final ESTClientProvider clientProvider;
    private static final Pattern pathInValid;

    ESTService(String string, String string2, ESTClientProvider eSTClientProvider) {
        string = this.verifyServer(string);
        if (string2 != null) {
            string2 = this.verifyLabel(string2);
            this.server = "https://" + string + "/.well-known/est/" + string2;
        } else {
            this.server = "https://" + string + "/.well-known/est";
        }
        this.clientProvider = eSTClientProvider;
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store) {
        return ESTService.storeToArray(store, null);
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store, Selector<X509CertificateHolder> selector) {
        Collection<X509CertificateHolder> collection = store.getMatches(selector);
        return collection.toArray(new X509CertificateHolder[collection.size()]);
    }

    public CACertsResponse getCACerts() throws ESTException {
        ESTResponse eSTResponse = null;
        Exception exception = null;
        CACertsResponse cACertsResponse = null;
        URL uRL = null;
        boolean bl = false;
        try {
            uRL = new URL(this.server + CACERTS);
            ESTClient eSTClient = this.clientProvider.makeClient();
            ESTRequest eSTRequest = new ESTRequestBuilder("GET", uRL).withClient(eSTClient).build();
            eSTResponse = eSTClient.doRequest(eSTRequest);
            Store<X509CertificateHolder> store = null;
            Store<X509CRLHolder> store2 = null;
            if (eSTResponse.getStatusCode() == 200) {
                String string = eSTResponse.getHeaders().getFirstValue("Content-Type");
                if (string == null || !string.startsWith("application/pkcs7-mime")) {
                    String string2 = string != null ? " got " + string : " but was not present.";
                    throw new ESTException("Response : " + uRL.toString() + "Expecting application/pkcs7-mime " + string2, null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
                }
                try {
                    ASN1InputStream aSN1InputStream = this.getASN1InputStream(eSTResponse.getInputStream(), eSTResponse.getContentLength());
                    SimplePKIResponse simplePKIResponse = new SimplePKIResponse(ContentInfo.getInstance(aSN1InputStream.readObject()));
                    store = simplePKIResponse.getCertificates();
                    store2 = simplePKIResponse.getCRLs();
                }
                catch (Throwable throwable) {
                    throw new ESTException("Decoding CACerts: " + uRL.toString() + " " + throwable.getMessage(), throwable, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
                }
            } else if (eSTResponse.getStatusCode() != 204) {
                throw new ESTException("Get CACerts: " + uRL.toString(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
            }
            cACertsResponse = new CACertsResponse(store, store2, eSTRequest, eSTResponse.getSource(), this.clientProvider.isTrusted());
        }
        catch (Throwable throwable) {
            bl = true;
            if (throwable instanceof ESTException) {
                throw (ESTException)throwable;
            }
            throw new ESTException(throwable.getMessage(), throwable);
        }
        finally {
            if (eSTResponse != null) {
                try {
                    eSTResponse.close();
                }
                catch (Exception exception2) {
                    exception = exception2;
                }
            }
        }
        if (exception != null) {
            if (exception instanceof ESTException) {
                throw (ESTException)exception;
            }
            throw new ESTException("Get CACerts: " + uRL.toString(), (Throwable)exception, eSTResponse.getStatusCode(), null);
        }
        return cACertsResponse;
    }

    private ASN1InputStream getASN1InputStream(InputStream inputStream, Long l) {
        if (l == null) {
            return new ASN1InputStream(inputStream);
        }
        if ((long)l.intValue() == l) {
            return new ASN1InputStream(inputStream, l.intValue());
        }
        return new ASN1InputStream(inputStream);
    }

    public EnrollmentResponse simpleEnroll(EnrollmentResponse enrollmentResponse) throws Exception {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        try (ESTResponse eSTResponse = null;){
            ESTClient eSTClient = this.clientProvider.makeClient();
            eSTResponse = eSTClient.doRequest(new ESTRequestBuilder(enrollmentResponse.getRequestToRetry()).withClient(eSTClient).build());
            EnrollmentResponse enrollmentResponse2 = this.handleEnrollResponse(eSTResponse);
            return enrollmentResponse2;
        }
    }

    protected EnrollmentResponse enroll(boolean bl, PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth, boolean bl2) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        try (ESTResponse eSTResponse = null;){
            byte[] byArray = this.annotateRequest(pKCS10CertificationRequest.getEncoded()).getBytes();
            URL uRL = new URL(this.server + (bl2 ? SERVERGEN : (bl ? SIMPLE_REENROLL : SIMPLE_ENROLL)));
            ESTClient eSTClient = this.clientProvider.makeClient();
            ESTRequestBuilder eSTRequestBuilder = new ESTRequestBuilder("POST", uRL).withData(byArray).withClient(eSTClient);
            eSTRequestBuilder.addHeader("Content-Type", "application/pkcs10");
            eSTRequestBuilder.addHeader("Content-Length", "" + byArray.length);
            eSTRequestBuilder.addHeader("Content-Transfer-Encoding", "base64");
            if (eSTAuth != null) {
                eSTAuth.applyAuth(eSTRequestBuilder);
            }
            eSTResponse = eSTClient.doRequest(eSTRequestBuilder.build());
            EnrollmentResponse enrollmentResponse = this.handleEnrollResponse(eSTResponse);
            return enrollmentResponse;
        }
    }

    public EnrollmentResponse simpleEnroll(boolean bl, PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth) throws IOException {
        return this.enroll(bl, pKCS10CertificationRequest, eSTAuth, false);
    }

    public EnrollmentResponse simpleEnrollWithServersideCreation(PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth) throws IOException {
        return this.enroll(false, pKCS10CertificationRequest, eSTAuth, true);
    }

    public EnrollmentResponse enrollPop(boolean bl, final PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, final ContentSigner contentSigner, ESTAuth eSTAuth, boolean bl2) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        try (ESTResponse eSTResponse = null;){
            URL uRL = new URL(this.server + (bl ? SIMPLE_REENROLL : SIMPLE_ENROLL));
            ESTClient eSTClient = this.clientProvider.makeClient();
            ESTRequestBuilder eSTRequestBuilder = new ESTRequestBuilder("POST", uRL).withClient(eSTClient).withConnectionListener(new ESTSourceConnectionListener(){
                final /* synthetic */ ESTService this$0;
                {
                    this.this$0 = eSTService;
                }

                public ESTRequest onConnection(Source source, ESTRequest eSTRequest) throws IOException {
                    if (source instanceof TLSUniqueProvider && ((TLSUniqueProvider)((Object)source)).isTLSUniqueAvailable()) {
                        PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder2 = new PKCS10CertificationRequestBuilder(pKCS10CertificationRequestBuilder);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] byArray = ((TLSUniqueProvider)((Object)source)).getTLSUnique();
                        pKCS10CertificationRequestBuilder2.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, new DERPrintableString(Base64.toBase64String(byArray)));
                        byteArrayOutputStream.write(this.this$0.annotateRequest(pKCS10CertificationRequestBuilder2.build(contentSigner).getEncoded()).getBytes());
                        byteArrayOutputStream.flush();
                        ESTRequestBuilder eSTRequestBuilder = new ESTRequestBuilder(eSTRequest).withData(byteArrayOutputStream.toByteArray());
                        eSTRequestBuilder.setHeader("Content-Type", "application/pkcs10");
                        eSTRequestBuilder.setHeader("Content-Transfer-Encoding", "base64");
                        eSTRequestBuilder.setHeader("Content-Length", Long.toString(byteArrayOutputStream.size()));
                        return eSTRequestBuilder.build();
                    }
                    throw new IOException("Source does not supply TLS unique.");
                }
            });
            if (eSTAuth != null) {
                eSTAuth.applyAuth(eSTRequestBuilder);
            }
            eSTResponse = eSTClient.doRequest(eSTRequestBuilder.build());
            EnrollmentResponse enrollmentResponse = this.handleEnrollResponse(eSTResponse);
            return enrollmentResponse;
        }
    }

    public EnrollmentResponse simpleEnrollPoP(boolean bl, PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, ContentSigner contentSigner, ESTAuth eSTAuth) throws IOException {
        return this.enrollPop(bl, pKCS10CertificationRequestBuilder, contentSigner, eSTAuth, false);
    }

    public EnrollmentResponse simpleEnrollPopWithServersideCreation(PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, ContentSigner contentSigner, ESTAuth eSTAuth) throws IOException {
        return this.enrollPop(false, pKCS10CertificationRequestBuilder, contentSigner, eSTAuth, true);
    }

    protected EnrollmentResponse handleEnrollResponse(ESTResponse eSTResponse) throws IOException {
        ESTRequest eSTRequest = eSTResponse.getOriginalRequest();
        Store<X509CertificateHolder> store = null;
        if (eSTResponse.getStatusCode() == 202) {
            String string = eSTResponse.getHeader("Retry-After");
            if (string == null) {
                throw new ESTException("Got Status 202 but not Retry-After header from: " + eSTRequest.getURL().toString());
            }
            long l = -1L;
            try {
                l = System.currentTimeMillis() + Long.parseLong(string) * 1000L;
            }
            catch (NumberFormatException numberFormatException) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    l = simpleDateFormat.parse(string).getTime();
                }
                catch (Exception exception) {
                    throw new ESTException("Unable to parse Retry-After header:" + eSTRequest.getURL().toString() + " " + exception.getMessage(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
                }
            }
            return new EnrollmentResponse(null, l, eSTRequest, eSTResponse.getSource());
        }
        if (eSTResponse.getStatusCode() == 200 && eSTResponse.getHeaderOrEmpty("content-type").contains("multipart/mixed")) {
            Headers headers = new Headers(eSTResponse.getHeaderOrEmpty("content-type"), "base64");
            BasicMimeParser basicMimeParser = new BasicMimeParser(headers, eSTResponse.getInputStream());
            final Object[] objectArray = new Object[2];
            basicMimeParser.parse(new MimeParserListener(){
                final /* synthetic */ ESTService this$0;
                {
                    this.this$0 = eSTService;
                }

                @Override
                public MimeContext createContext(MimeParserContext mimeParserContext, Headers headers) {
                    return ConstantMimeContext.Instance;
                }

                @Override
                public void object(MimeParserContext mimeParserContext, Headers headers, InputStream inputStream) throws IOException {
                    if (headers.getContentType().contains("application/pkcs8")) {
                        ASN1InputStream aSN1InputStream = new ASN1InputStream(inputStream);
                        objectArray[0] = PrivateKeyInfo.getInstance(aSN1InputStream.readObject());
                        if (aSN1InputStream.readObject() != null) {
                            throw new ESTException("Unexpected ASN1 object after private key info");
                        }
                    } else if (headers.getContentType().contains("application/pkcs7-mime")) {
                        ASN1InputStream aSN1InputStream = new ASN1InputStream(inputStream);
                        try {
                            objectArray[1] = new SimplePKIResponse(ContentInfo.getInstance(aSN1InputStream.readObject()));
                        }
                        catch (CMCException cMCException) {
                            throw new IOException(cMCException.getMessage());
                        }
                        if (aSN1InputStream.readObject() != null) {
                            throw new ESTException("Unexpected ASN1 object after reading certificates");
                        }
                    }
                }
            });
            if (objectArray[0] == null || objectArray[1] == null) {
                throw new ESTException("received neither private key info and certificates");
            }
            store = ((SimplePKIResponse)objectArray[1]).getCertificates();
            return new EnrollmentResponse(store, -1L, null, eSTResponse.getSource(), PrivateKeyInfo.getInstance(objectArray[0]));
        }
        if (eSTResponse.getStatusCode() == 200) {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(eSTResponse.getInputStream());
            SimplePKIResponse simplePKIResponse = null;
            try {
                simplePKIResponse = new SimplePKIResponse(ContentInfo.getInstance(aSN1InputStream.readObject()));
            }
            catch (CMCException cMCException) {
                throw new ESTException(cMCException.getMessage(), cMCException.getCause());
            }
            store = simplePKIResponse.getCertificates();
            return new EnrollmentResponse(store, -1L, null, eSTResponse.getSource());
        }
        throw new ESTException("Simple Enroll: " + eSTRequest.getURL().toString(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
    }

    /*
     * Unable to fully structure code
     */
    public CSRRequestResponse getCSRAttributes() throws ESTException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        var1_1 = null;
        var2_2 = null;
        var3_3 = null;
        var4_4 = null;
        try {
            var4_4 = new URL(this.server + "/csrattrs");
            var5_5 = this.clientProvider.makeClient();
            var6_8 = new ESTRequestBuilder("GET", var4_4).withClient(var5_5).build();
            var1_1 = var5_5.doRequest(var6_8);
            switch (var1_1.getStatusCode()) {
                case 200: {
                    try {
                        var7_9 = this.getASN1InputStream(var1_1.getInputStream(), var1_1.getContentLength());
                        var8_11 = ASN1Sequence.getInstance(var7_9.readObject());
                        var2_2 = new CSRAttributesResponse(CsrAttrs.getInstance(var8_11));
                        ** break;
lbl19:
                        // 1 sources

                        break;
                    }
                    catch (Throwable var7_10) {
                        throw new ESTException("Decoding CACerts: " + var4_4.toString() + " " + var7_10.getMessage(), var7_10, var1_1.getStatusCode(), var1_1.getInputStream());
                    }
                }
                case 204: {
                    var2_2 = null;
                    ** break;
lbl25:
                    // 1 sources

                    break;
                }
                case 404: {
                    var2_2 = null;
                    ** break;
lbl29:
                    // 1 sources

                    break;
                }
                default: {
                    throw new ESTException("CSR Attribute request: " + var6_8.getURL().toString(), null, var1_1.getStatusCode(), var1_1.getInputStream());
                }
            }
        }
        catch (Throwable var5_7) {
            if (var5_7 instanceof ESTException) {
                throw (ESTException)var5_7;
            }
            throw new ESTException(var5_7.getMessage(), var5_7);
        }
        finally {
            if (var1_1 != null) {
                try {
                    var1_1.close();
                }
                catch (Exception var5_6) {
                    var3_3 = var5_6;
                }
            }
        }
        if (var3_3 != null) {
            if (var3_3 instanceof ESTException) {
                throw (ESTException)var3_3;
            }
            throw new ESTException(var3_3.getMessage(), (Throwable)var3_3, var1_1.getStatusCode(), null);
        }
        return new CSRRequestResponse(var2_2, var1_1.getSource());
    }

    private String annotateRequest(byte[] byArray) {
        int n = 0;
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        do {
            if (n + 48 < byArray.length) {
                printWriter.print(Base64.toBase64String(byArray, n, 48));
                n += 48;
            } else {
                printWriter.print(Base64.toBase64String(byArray, n, byArray.length - n));
                n = byArray.length;
            }
            printWriter.print('\n');
        } while (n < byArray.length);
        printWriter.flush();
        return stringWriter.toString();
    }

    private String verifyLabel(String string) {
        while (string.endsWith("/") && string.length() > 0) {
            string = string.substring(0, string.length() - 1);
        }
        while (string.startsWith("/") && string.length() > 0) {
            string = string.substring(1);
        }
        if (string.length() == 0) {
            throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
        }
        if (!pathInValid.matcher(string).matches()) {
            throw new IllegalArgumentException("Server path " + string + " contains invalid characters");
        }
        if (illegalParts.contains(string)) {
            throw new IllegalArgumentException("Label " + string + " is a reserved path segment.");
        }
        return string;
    }

    private String verifyServer(String string) {
        try {
            while (string.endsWith("/") && string.length() > 0) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.contains("://")) {
                throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
            }
            URL uRL = new URL("https://" + string);
            if (uRL.getPath().length() == 0 || uRL.getPath().equals("/")) {
                return string;
            }
            throw new IllegalArgumentException("Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily.");
        }
        catch (Exception exception) {
            if (exception instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)exception;
            }
            throw new IllegalArgumentException("Scheme and host is invalid: " + exception.getMessage(), exception);
        }
    }

    static {
        illegalParts.add(CACERTS.substring(1));
        illegalParts.add(SIMPLE_ENROLL.substring(1));
        illegalParts.add(SIMPLE_REENROLL.substring(1));
        illegalParts.add(FULLCMC.substring(1));
        illegalParts.add(SERVERGEN.substring(1));
        illegalParts.add(CSRATTRS.substring(1));
        pathInValid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;:=]+");
    }
}

