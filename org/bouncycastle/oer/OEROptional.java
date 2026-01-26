/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Absent;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class OEROptional
extends ASN1Object {
    public static final OEROptional ABSENT = new OEROptional(false, null);
    private final boolean defined;
    private final ASN1Encodable value;

    private OEROptional(boolean bl, ASN1Encodable aSN1Encodable) {
        this.defined = bl;
        this.value = aSN1Encodable;
    }

    public static OEROptional getInstance(Object object) {
        if (object instanceof OEROptional) {
            return (OEROptional)object;
        }
        if (object instanceof ASN1Encodable) {
            return new OEROptional(true, (ASN1Encodable)object);
        }
        return ABSENT;
    }

    public static <T> T getValue(Class<T> clazz, Object object) {
        OEROptional oEROptional = OEROptional.getInstance(object);
        if (!oEROptional.defined) {
            return null;
        }
        return oEROptional.getObject(clazz);
    }

    public <T> T getObject(final Class<T> clazz) {
        if (this.defined) {
            if (this.value.getClass().isInstance(clazz)) {
                return clazz.cast(this.value);
            }
            return AccessController.doPrivileged(new PrivilegedAction<T>(){
                final /* synthetic */ OEROptional this$0;
                {
                    this.this$0 = oEROptional;
                }

                @Override
                public T run() {
                    try {
                        Method method = clazz.getMethod("getInstance", Object.class);
                        return clazz.cast(method.invoke(null, this.this$0.value));
                    }
                    catch (Exception exception) {
                        throw new IllegalStateException("could not invoke getInstance on type " + exception.getMessage(), exception);
                    }
                }
            });
        }
        return null;
    }

    public ASN1Encodable get() {
        if (!this.defined) {
            return ABSENT;
        }
        return this.value;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        if (!this.defined) {
            return ASN1Absent.INSTANCE;
        }
        return this.get().toASN1Primitive();
    }

    public boolean isDefined() {
        return this.defined;
    }

    public String toString() {
        if (this.defined) {
            return "OPTIONAL(" + this.value + ")";
        }
        return "ABSENT";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }
        OEROptional oEROptional = (OEROptional)object;
        if (this.defined != oEROptional.defined) {
            return false;
        }
        return this.value != null ? this.value.equals(oEROptional.value) : oEROptional.value == null;
    }

    @Override
    public int hashCode() {
        int n = super.hashCode();
        n = 31 * n + (this.defined ? 1 : 0);
        n = 31 * n + (this.value != null ? this.value.hashCode() : 0);
        return n;
    }
}

