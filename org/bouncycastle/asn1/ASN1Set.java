/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Set
extends ASN1Primitive
implements Iterable<ASN1Encodable> {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Set.class, 17){

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence.toASN1Set();
        }
    };
    protected final ASN1Encodable[] elements;
    protected ASN1Encodable[] sortedElements;

    public static ASN1Set getInstance(Object object) {
        if (object == null || object instanceof ASN1Set) {
            return (ASN1Set)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1Set) {
                return (ASN1Set)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1Set)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct set from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + object.getClass().getName());
    }

    public static ASN1Set getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Set)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1Set getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Set)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    protected ASN1Set() {
        this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
        this.sortedElements = this.elements;
    }

    protected ASN1Set(ASN1Encodable aSN1Encodable) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'element' cannot be null");
        }
        this.elements = new ASN1Encodable[]{aSN1Encodable};
        this.sortedElements = this.elements;
    }

    protected ASN1Set(ASN1EncodableVector aSN1EncodableVector, boolean bl) {
        ASN1Encodable[] aSN1EncodableArray;
        if (null == aSN1EncodableVector) {
            throw new NullPointerException("'elementVector' cannot be null");
        }
        if (bl && aSN1EncodableVector.size() >= 2) {
            aSN1EncodableArray = aSN1EncodableVector.copyElements();
            ASN1Set.sort(aSN1EncodableArray);
        } else {
            aSN1EncodableArray = aSN1EncodableVector.takeElements();
        }
        this.elements = aSN1EncodableArray;
        this.sortedElements = bl || aSN1EncodableArray.length < 2 ? this.elements : null;
    }

    protected ASN1Set(ASN1Encodable[] aSN1EncodableArray, boolean bl) {
        if (Arrays.isNullOrContainsNull(aSN1EncodableArray)) {
            throw new NullPointerException("'elements' cannot be null, or contain null");
        }
        ASN1Encodable[] aSN1EncodableArray2 = ASN1EncodableVector.cloneElements(aSN1EncodableArray);
        if (bl && aSN1EncodableArray2.length >= 2) {
            ASN1Set.sort(aSN1EncodableArray2);
        }
        this.elements = aSN1EncodableArray2;
        this.sortedElements = bl || aSN1EncodableArray2.length < 2 ? aSN1EncodableArray : null;
    }

    ASN1Set(boolean bl, ASN1Encodable[] aSN1EncodableArray) {
        this.elements = aSN1EncodableArray;
        this.sortedElements = bl || aSN1EncodableArray.length < 2 ? aSN1EncodableArray : null;
    }

    ASN1Set(ASN1Encodable[] aSN1EncodableArray, ASN1Encodable[] aSN1EncodableArray2) {
        this.elements = aSN1EncodableArray;
        this.sortedElements = aSN1EncodableArray2;
    }

    public Enumeration getObjects() {
        return new Enumeration(){
            private int pos = 0;

            @Override
            public boolean hasMoreElements() {
                return this.pos < ASN1Set.this.elements.length;
            }

            public Object nextElement() {
                if (this.pos < ASN1Set.this.elements.length) {
                    return ASN1Set.this.elements[this.pos++];
                }
                throw new NoSuchElementException();
            }
        };
    }

    public ASN1Encodable getObjectAt(int n) {
        return this.elements[n];
    }

    public int size() {
        return this.elements.length;
    }

    public ASN1Encodable[] toArray() {
        return ASN1EncodableVector.cloneElements(this.elements);
    }

    public ASN1SetParser parser() {
        int n = this.size();
        return new ASN1SetParser(){
            private int pos = 0;
            final /* synthetic */ ASN1Set this$0;
            {
                this.this$0 = aSN1Set;
            }

            @Override
            public ASN1Encodable readObject() throws IOException {
                ASN1Encodable aSN1Encodable;
                if (n == this.pos) {
                    return null;
                }
                if ((aSN1Encodable = this.this$0.elements[this.pos++]) instanceof ASN1Sequence) {
                    return ((ASN1Sequence)aSN1Encodable).parser();
                }
                if (aSN1Encodable instanceof ASN1Set) {
                    return ((ASN1Set)aSN1Encodable).parser();
                }
                return aSN1Encodable;
            }

            @Override
            public ASN1Primitive getLoadedObject() {
                return this.this$0;
            }

            @Override
            public ASN1Primitive toASN1Primitive() {
                return this.this$0;
            }
        };
    }

    @Override
    public int hashCode() {
        int n = this.elements.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 += this.elements[n].toASN1Primitive().hashCode();
        }
        return n2;
    }

    @Override
    ASN1Primitive toDERObject() {
        if (this.sortedElements == null) {
            this.sortedElements = (ASN1Encodable[])this.elements.clone();
            ASN1Set.sort(this.sortedElements);
        }
        return new DERSet(true, this.sortedElements);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLSet(this.elements, this.sortedElements);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Set)) {
            return false;
        }
        ASN1Set aSN1Set = (ASN1Set)aSN1Primitive;
        int n = this.size();
        if (aSN1Set.size() != n) {
            return false;
        }
        DERSet dERSet = (DERSet)this.toDERObject();
        DERSet dERSet2 = (DERSet)aSN1Set.toDERObject();
        for (int i = 0; i < n; ++i) {
            ASN1Primitive aSN1Primitive2;
            ASN1Primitive aSN1Primitive3 = dERSet.elements[i].toASN1Primitive();
            if (aSN1Primitive3 == (aSN1Primitive2 = dERSet2.elements[i].toASN1Primitive()) || aSN1Primitive3.asn1Equals(aSN1Primitive2)) continue;
            return false;
        }
        return true;
    }

    @Override
    boolean encodeConstructed() {
        return true;
    }

    public String toString() {
        int n = this.size();
        if (0 == n) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        int n2 = 0;
        while (true) {
            stringBuilder.append(this.elements[n2]);
            if (++n2 >= n) break;
            stringBuilder.append(", ");
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    @Override
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator<ASN1Encodable>(this.toArray());
    }

    private static byte[] getDEREncoded(ASN1Encodable aSN1Encodable) {
        try {
            return aSN1Encodable.toASN1Primitive().getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new IllegalArgumentException("cannot encode object added to SET");
        }
    }

    private static boolean lessThanOrEqual(byte[] byArray, byte[] byArray2) {
        int n = byArray[0] & 0xDF;
        int n2 = byArray2[0] & 0xDF;
        if (n != n2) {
            return n < n2;
        }
        int n3 = Math.min(byArray.length, byArray2.length) - 1;
        for (int i = 1; i < n3; ++i) {
            if (byArray[i] == byArray2[i]) continue;
            return (byArray[i] & 0xFF) < (byArray2[i] & 0xFF);
        }
        return (byArray[n3] & 0xFF) <= (byArray2[n3] & 0xFF);
    }

    private static void sort(ASN1Encodable[] aSN1EncodableArray) {
        Object object;
        int n = aSN1EncodableArray.length;
        if (n < 2) {
            return;
        }
        Object object2 = aSN1EncodableArray[0];
        Object object3 = aSN1EncodableArray[1];
        byte[] byArray = ASN1Set.getDEREncoded((ASN1Encodable)object2);
        byte[] byArray2 = ASN1Set.getDEREncoded((ASN1Encodable)object3);
        if (ASN1Set.lessThanOrEqual(byArray2, byArray)) {
            ASN1Encodable aSN1Encodable = object3;
            object3 = object2;
            object2 = aSN1Encodable;
            object = byArray2;
            byArray2 = byArray;
            byArray = object;
        }
        for (int i = 2; i < n; ++i) {
            ASN1Encodable aSN1Encodable;
            byte[] byArray3;
            object = aSN1EncodableArray[i];
            byte[] byArray4 = ASN1Set.getDEREncoded((ASN1Encodable)object);
            if (ASN1Set.lessThanOrEqual(byArray2, byArray4)) {
                aSN1EncodableArray[i - 2] = object2;
                object2 = object3;
                byArray = byArray2;
                object3 = object;
                byArray2 = byArray4;
                continue;
            }
            if (ASN1Set.lessThanOrEqual(byArray, byArray4)) {
                aSN1EncodableArray[i - 2] = object2;
                object2 = object;
                byArray = byArray4;
                continue;
            }
            int n2 = i - 1;
            while (--n2 > 0 && !ASN1Set.lessThanOrEqual(byArray3 = ASN1Set.getDEREncoded(aSN1Encodable = aSN1EncodableArray[n2 - 1]), byArray4)) {
                aSN1EncodableArray[n2] = aSN1Encodable;
            }
            aSN1EncodableArray[n2] = object;
        }
        aSN1EncodableArray[n - 2] = object2;
        aSN1EncodableArray[n - 1] = object3;
    }
}

