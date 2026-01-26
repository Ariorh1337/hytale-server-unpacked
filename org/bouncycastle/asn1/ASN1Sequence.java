/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Sequence
extends ASN1Primitive
implements Iterable<ASN1Encodable> {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Sequence.class, 16){

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence;
        }
    };
    ASN1Encodable[] elements;

    public static ASN1Sequence getInstance(Object object) {
        if (object == null || object instanceof ASN1Sequence) {
            return (ASN1Sequence)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1Sequence) {
                return (ASN1Sequence)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1Sequence)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + object.getClass().getName());
    }

    public static ASN1Sequence getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Sequence)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1Sequence getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Sequence)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    protected ASN1Sequence() {
        this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
    }

    protected ASN1Sequence(ASN1Encodable aSN1Encodable) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'element' cannot be null");
        }
        this.elements = new ASN1Encodable[]{aSN1Encodable};
    }

    protected ASN1Sequence(ASN1Encodable aSN1Encodable, ASN1Encodable aSN1Encodable2) {
        if (null == aSN1Encodable) {
            throw new NullPointerException("'element1' cannot be null");
        }
        if (null == aSN1Encodable2) {
            throw new NullPointerException("'element2' cannot be null");
        }
        this.elements = new ASN1Encodable[]{aSN1Encodable, aSN1Encodable2};
    }

    protected ASN1Sequence(ASN1EncodableVector aSN1EncodableVector) {
        if (null == aSN1EncodableVector) {
            throw new NullPointerException("'elementVector' cannot be null");
        }
        this.elements = aSN1EncodableVector.takeElements();
    }

    protected ASN1Sequence(ASN1Encodable[] aSN1EncodableArray) {
        if (Arrays.isNullOrContainsNull(aSN1EncodableArray)) {
            throw new NullPointerException("'elements' cannot be null, or contain null");
        }
        this.elements = ASN1EncodableVector.cloneElements(aSN1EncodableArray);
    }

    ASN1Sequence(ASN1Encodable[] aSN1EncodableArray, boolean bl) {
        this.elements = bl ? ASN1EncodableVector.cloneElements(aSN1EncodableArray) : aSN1EncodableArray;
    }

    public ASN1Encodable[] toArray() {
        return ASN1EncodableVector.cloneElements(this.elements);
    }

    ASN1Encodable[] toArrayInternal() {
        return this.elements;
    }

    public Enumeration getObjects() {
        return new Enumeration(){
            private int pos = 0;

            @Override
            public boolean hasMoreElements() {
                return this.pos < ASN1Sequence.this.elements.length;
            }

            public Object nextElement() {
                if (this.pos < ASN1Sequence.this.elements.length) {
                    return ASN1Sequence.this.elements[this.pos++];
                }
                throw new NoSuchElementException();
            }
        };
    }

    public ASN1SequenceParser parser() {
        int n = this.size();
        return new ASN1SequenceParser(){
            private int pos = 0;
            final /* synthetic */ ASN1Sequence this$0;
            {
                this.this$0 = aSN1Sequence;
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

    public ASN1Encodable getObjectAt(int n) {
        return this.elements[n];
    }

    public int size() {
        return this.elements.length;
    }

    @Override
    public int hashCode() {
        int n = this.elements.length;
        int n2 = n + 1;
        while (--n >= 0) {
            n2 *= 257;
            n2 ^= this.elements[n].toASN1Primitive().hashCode();
        }
        return n2;
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Sequence)) {
            return false;
        }
        ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Primitive;
        int n = this.size();
        if (aSN1Sequence.size() != n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            ASN1Primitive aSN1Primitive2;
            ASN1Primitive aSN1Primitive3 = this.elements[i].toASN1Primitive();
            if (aSN1Primitive3 == (aSN1Primitive2 = aSN1Sequence.elements[i].toASN1Primitive()) || aSN1Primitive3.asn1Equals(aSN1Primitive2)) continue;
            return false;
        }
        return true;
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERSequence(this.elements, false);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLSequence(this.elements, false);
    }

    abstract ASN1BitString toASN1BitString();

    abstract ASN1External toASN1External();

    abstract ASN1OctetString toASN1OctetString();

    abstract ASN1Set toASN1Set();

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
        return new Arrays.Iterator<ASN1Encodable>(this.elements);
    }

    ASN1BitString[] getConstructedBitStrings() {
        int n = this.size();
        ASN1BitString[] aSN1BitStringArray = new ASN1BitString[n];
        for (int i = 0; i < n; ++i) {
            aSN1BitStringArray[i] = ASN1BitString.getInstance(this.elements[i]);
        }
        return aSN1BitStringArray;
    }

    ASN1OctetString[] getConstructedOctetStrings() {
        int n = this.size();
        ASN1OctetString[] aSN1OctetStringArray = new ASN1OctetString[n];
        for (int i = 0; i < n; ++i) {
            aSN1OctetStringArray[i] = ASN1OctetString.getInstance(this.elements[i]);
        }
        return aSN1OctetStringArray;
    }
}

