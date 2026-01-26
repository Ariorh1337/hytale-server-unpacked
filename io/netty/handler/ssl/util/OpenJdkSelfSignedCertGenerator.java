/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class OpenJdkSelfSignedCertGenerator {
    private static final InternalLogger logger;
    private static final MethodHandle CERT_INFO_SET_HANDLE;
    private static final MethodHandle ISSUER_NAME_CONSTRUCTOR;
    private static final MethodHandle CERT_IMPL_CONSTRUCTOR;
    private static final MethodHandle X509_CERT_INFO_CONSTRUCTOR;
    private static final MethodHandle CERTIFICATE_VERSION_CONSTRUCTOR;
    private static final MethodHandle CERTIFICATE_SUBJECT_NAME_CONSTRUCTOR;
    private static final MethodHandle X500_NAME_CONSTRUCTOR;
    private static final MethodHandle CERTIFICATE_SERIAL_NUMBER_CONSTRUCTOR;
    private static final MethodHandle CERTIFICATE_VALIDITY_CONSTRUCTOR;
    private static final MethodHandle CERTIFICATE_X509_KEY_CONSTRUCTOR;
    private static final MethodHandle CERTIFICATE_ALORITHM_ID_CONSTRUCTOR;
    private static final MethodHandle CERT_IMPL_GET_HANDLE;
    private static final MethodHandle CERT_IMPL_SIGN_HANDLE;
    private static final MethodHandle ALGORITHM_ID_GET_HANDLE;
    private static final boolean SUPPORTED;

    static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter, String algorithm) throws Exception {
        if (!SUPPORTED) {
            throw new UnsupportedOperationException(OpenJdkSelfSignedCertGenerator.class.getSimpleName() + " not supported on the used JDK version");
        }
        try {
            PrivateKey key = keypair.getPrivate();
            Object info = X509_CERT_INFO_CONSTRUCTOR.invoke();
            Object owner = X500_NAME_CONSTRUCTOR.invoke("CN=" + fqdn);
            CERT_INFO_SET_HANDLE.invoke(info, "version", CERTIFICATE_VERSION_CONSTRUCTOR.invoke(2));
            CERT_INFO_SET_HANDLE.invoke(info, "serialNumber", CERTIFICATE_SERIAL_NUMBER_CONSTRUCTOR.invoke(new BigInteger(64, random)));
            try {
                CERT_INFO_SET_HANDLE.invoke(info, "subject", CERTIFICATE_SUBJECT_NAME_CONSTRUCTOR.invoke(owner));
            }
            catch (CertificateException ex) {
                CERT_INFO_SET_HANDLE.invoke(info, "subject", owner);
            }
            try {
                CERT_INFO_SET_HANDLE.invoke(info, "issuer", ISSUER_NAME_CONSTRUCTOR.invoke(owner));
            }
            catch (CertificateException ex) {
                CERT_INFO_SET_HANDLE.invoke(info, "issuer", owner);
            }
            CERT_INFO_SET_HANDLE.invoke(info, "validity", CERTIFICATE_VALIDITY_CONSTRUCTOR.invoke(notBefore, notAfter));
            CERT_INFO_SET_HANDLE.invoke(info, "key", CERTIFICATE_X509_KEY_CONSTRUCTOR.invoke(keypair.getPublic()));
            CERT_INFO_SET_HANDLE.invoke(info, "algorithmID", CERTIFICATE_ALORITHM_ID_CONSTRUCTOR.invoke(ALGORITHM_ID_GET_HANDLE.invoke("1.2.840.113549.1.1.11")));
            Object cert = CERT_IMPL_CONSTRUCTOR.invoke(info);
            CERT_IMPL_SIGN_HANDLE.invoke(cert, key, algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA");
            CERT_INFO_SET_HANDLE.invoke(info, "algorithmID.algorithm", CERT_IMPL_GET_HANDLE.invoke(cert, "x509.algorithm"));
            cert = CERT_IMPL_CONSTRUCTOR.invoke(info);
            CERT_IMPL_SIGN_HANDLE.invoke(cert, key, algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA");
            X509Certificate x509Cert = (X509Certificate)cert;
            x509Cert.verify(keypair.getPublic());
            return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, x509Cert);
        }
        catch (Throwable cause) {
            if (cause instanceof Exception) {
                throw (Exception)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new IllegalStateException(cause);
        }
    }

    private OpenJdkSelfSignedCertGenerator() {
    }

    static {
        boolean supported;
        logger = InternalLoggerFactory.getInstance(OpenJdkSelfSignedCertGenerator.class);
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle certInfoSetHandle = null;
        MethodHandle x509CertInfoConstructor = null;
        MethodHandle issuerNameConstructor = null;
        MethodHandle certImplConstructor = null;
        MethodHandle x500NameConstructor = null;
        MethodHandle certificateVersionConstructor = null;
        MethodHandle certificateSubjectNameConstructor = null;
        MethodHandle certificateSerialNumberConstructor = null;
        MethodHandle certificateValidityConstructor = null;
        MethodHandle certificateX509KeyConstructor = null;
        MethodHandle certificateAlgorithmIdConstructor = null;
        MethodHandle certImplGetHandle = null;
        MethodHandle certImplSignHandle = null;
        MethodHandle algorithmIdGetHandle = null;
        try {
            Object maybeClasses = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        ArrayList classes = new ArrayList();
                        classes.add(Class.forName("sun.security.x509.X509CertInfo", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.X500Name", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateIssuerName", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.X509CertImpl", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateVersion", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateSubjectName", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateSerialNumber", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateValidity", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateX509Key", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.AlgorithmId", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        classes.add(Class.forName("sun.security.x509.CertificateAlgorithmId", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class)));
                        return classes;
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeClasses instanceof List)) {
                throw (Throwable)maybeClasses;
            }
            List classes = (List)maybeClasses;
            final Class x509CertInfoClass = (Class)classes.get(0);
            final Class x500NameClass = (Class)classes.get(1);
            final Class certificateIssuerNameClass = (Class)classes.get(2);
            final Class x509CertImplClass = (Class)classes.get(3);
            final Class certificateVersionClass = (Class)classes.get(4);
            final Class certificateSubjectNameClass = (Class)classes.get(5);
            final Class certificateSerialNumberClass = (Class)classes.get(6);
            final Class certificateValidityClass = (Class)classes.get(7);
            final Class certificateX509KeyClass = (Class)classes.get(8);
            final Class algorithmIdClass = (Class)classes.get(9);
            final Class certificateAlgorithmIdClass = (Class)classes.get(10);
            Object maybeConstructors = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        ArrayList<MethodHandle> constructors = new ArrayList<MethodHandle>();
                        constructors.add(lookup.unreflectConstructor(x509CertInfoClass.getConstructor(new Class[0])).asType(MethodType.methodType(x509CertInfoClass)));
                        constructors.add(lookup.unreflectConstructor(certificateIssuerNameClass.getConstructor(x500NameClass)).asType(MethodType.methodType(certificateIssuerNameClass, x500NameClass)));
                        constructors.add(lookup.unreflectConstructor(x509CertImplClass.getConstructor(x509CertInfoClass)).asType(MethodType.methodType(x509CertImplClass, x509CertInfoClass)));
                        constructors.add(lookup.unreflectConstructor(x500NameClass.getConstructor(String.class)).asType(MethodType.methodType(x500NameClass, String.class)));
                        constructors.add(lookup.unreflectConstructor(certificateVersionClass.getConstructor(Integer.TYPE)).asType(MethodType.methodType(certificateVersionClass, Integer.TYPE)));
                        constructors.add(lookup.unreflectConstructor(certificateSubjectNameClass.getConstructor(x500NameClass)).asType(MethodType.methodType(certificateSubjectNameClass, x500NameClass)));
                        constructors.add(lookup.unreflectConstructor(certificateSerialNumberClass.getConstructor(BigInteger.class)).asType(MethodType.methodType(certificateSerialNumberClass, BigInteger.class)));
                        constructors.add(lookup.unreflectConstructor(certificateValidityClass.getConstructor(Date.class, Date.class)).asType(MethodType.methodType(certificateValidityClass, Date.class, Date.class)));
                        constructors.add(lookup.unreflectConstructor(certificateX509KeyClass.getConstructor(PublicKey.class)).asType(MethodType.methodType(certificateX509KeyClass, PublicKey.class)));
                        constructors.add(lookup.unreflectConstructor(certificateAlgorithmIdClass.getConstructor(algorithmIdClass)).asType(MethodType.methodType(certificateAlgorithmIdClass, algorithmIdClass)));
                        return constructors;
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeConstructors instanceof List)) {
                throw (Throwable)maybeConstructors;
            }
            List constructorList = (List)maybeConstructors;
            x509CertInfoConstructor = (MethodHandle)constructorList.get(0);
            issuerNameConstructor = (MethodHandle)constructorList.get(1);
            certImplConstructor = (MethodHandle)constructorList.get(2);
            x500NameConstructor = (MethodHandle)constructorList.get(3);
            certificateVersionConstructor = (MethodHandle)constructorList.get(4);
            certificateSubjectNameConstructor = (MethodHandle)constructorList.get(5);
            certificateSerialNumberConstructor = (MethodHandle)constructorList.get(6);
            certificateValidityConstructor = (MethodHandle)constructorList.get(7);
            certificateX509KeyConstructor = (MethodHandle)constructorList.get(8);
            certificateAlgorithmIdConstructor = (MethodHandle)constructorList.get(9);
            Object maybeMethodHandles = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        ArrayList<MethodHandle> methods = new ArrayList<MethodHandle>();
                        methods.add(lookup.findVirtual(x509CertInfoClass, "set", MethodType.methodType(Void.TYPE, String.class, Object.class)));
                        methods.add(lookup.findVirtual(x509CertImplClass, "get", MethodType.methodType(Object.class, String.class)));
                        methods.add(lookup.findVirtual(x509CertImplClass, "sign", MethodType.methodType(Void.TYPE, PrivateKey.class, String.class)));
                        methods.add(lookup.findStatic(algorithmIdClass, "get", MethodType.methodType(algorithmIdClass, String.class)));
                        return methods;
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeMethodHandles instanceof List)) {
                throw (Throwable)maybeMethodHandles;
            }
            List methodHandles = (List)maybeMethodHandles;
            certInfoSetHandle = (MethodHandle)methodHandles.get(0);
            certImplGetHandle = (MethodHandle)methodHandles.get(1);
            certImplSignHandle = (MethodHandle)methodHandles.get(2);
            algorithmIdGetHandle = (MethodHandle)methodHandles.get(3);
            supported = true;
        }
        catch (Throwable cause) {
            supported = false;
            logger.debug(OpenJdkSelfSignedCertGenerator.class.getSimpleName() + " not supported", cause);
        }
        CERT_INFO_SET_HANDLE = certInfoSetHandle;
        X509_CERT_INFO_CONSTRUCTOR = x509CertInfoConstructor;
        ISSUER_NAME_CONSTRUCTOR = issuerNameConstructor;
        CERTIFICATE_VERSION_CONSTRUCTOR = certificateVersionConstructor;
        CERTIFICATE_SUBJECT_NAME_CONSTRUCTOR = certificateSubjectNameConstructor;
        CERT_IMPL_CONSTRUCTOR = certImplConstructor;
        X500_NAME_CONSTRUCTOR = x500NameConstructor;
        CERTIFICATE_SERIAL_NUMBER_CONSTRUCTOR = certificateSerialNumberConstructor;
        CERTIFICATE_VALIDITY_CONSTRUCTOR = certificateValidityConstructor;
        CERTIFICATE_X509_KEY_CONSTRUCTOR = certificateX509KeyConstructor;
        CERT_IMPL_GET_HANDLE = certImplGetHandle;
        CERT_IMPL_SIGN_HANDLE = certImplSignHandle;
        ALGORITHM_ID_GET_HANDLE = algorithmIdGetHandle;
        CERTIFICATE_ALORITHM_ID_CONSTRUCTOR = certificateAlgorithmIdConstructor;
        SUPPORTED = supported;
    }
}

