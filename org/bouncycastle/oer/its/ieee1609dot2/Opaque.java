/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.util.Arrays;

public class Opaque
extends ASN1Object {
    private final byte[] content;

    public Opaque(byte[] byArray) {
        this.content = Arrays.clone(byArray);
    }

    private Opaque(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static Opaque getInstance(Object object) {
        if (object instanceof Opaque) {
            return (Opaque)object;
        }
        if (object != null) {
            return new Opaque(ASN1OctetString.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.content);
    }

    public byte[] getContent() {
        return this.content;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public static <T> T getValue(final Class<T> clazz, final Element element, final Opaque opaque) {
        return AccessController.doPrivileged(new PrivilegedAction<T>(){

            @Override
            public T run() {
                try {
                    ASN1Encodable aSN1Encodable = OERInputStream.parse(opaque.content, element);
                    Method method = clazz.getMethod("getInstance", Object.class);
                    return clazz.cast(method.invoke(null, aSN1Encodable));
                }
                catch (Exception exception) {
                    throw new IllegalStateException("could not invoke getInstance on type " + exception.getMessage(), exception);
                }
            }
        });
    }
}

