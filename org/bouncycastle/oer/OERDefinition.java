/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.Switch;

public class OERDefinition {
    static final BigInteger[] uIntMax = new BigInteger[]{new BigInteger("256"), new BigInteger("65536"), new BigInteger("4294967296"), new BigInteger("18446744073709551616")};
    static final BigInteger[][] sIntRange = new BigInteger[][]{{new BigInteger("-128"), new BigInteger("127")}, {new BigInteger("-32768"), new BigInteger("32767")}, {new BigInteger("-2147483648"), new BigInteger("2147483647")}, {new BigInteger("-9223372036854775808"), new BigInteger("9223372036854775807")}};

    public static Builder bool() {
        return new Builder(BaseType.BOOLEAN);
    }

    public static Builder integer() {
        return new Builder(BaseType.INT);
    }

    public static Builder integer(long l) {
        return new Builder(BaseType.INT).defaultValue(new ASN1Integer(l));
    }

    public static Builder bitString(long l) {
        return new Builder(BaseType.BIT_STRING).fixedSize(l);
    }

    public static Builder integer(BigInteger bigInteger, BigInteger bigInteger2) {
        return new Builder(BaseType.INT).range(bigInteger, bigInteger2);
    }

    public static Builder integer(long l, long l2) {
        return new Builder(BaseType.INT).range(BigInteger.valueOf(l), BigInteger.valueOf(l2));
    }

    public static Builder integer(long l, long l2, ASN1Encodable aSN1Encodable) {
        return new Builder(BaseType.INT).range(l, l2, aSN1Encodable);
    }

    public static Builder nullValue() {
        return new Builder(BaseType.NULL);
    }

    public static Builder seq() {
        return new Builder(BaseType.SEQ);
    }

    public static Builder seq(Object ... objectArray) {
        return new Builder(BaseType.SEQ).items(objectArray);
    }

    public static Builder aSwitch(Switch switch_) {
        return new Builder(BaseType.Switch).decodeSwitch(switch_);
    }

    public static Builder enumItem(String string) {
        return new Builder(BaseType.ENUM_ITEM).label(string);
    }

    public static Builder enumItem(String string, BigInteger bigInteger) {
        return new Builder(BaseType.ENUM_ITEM).enumValue(bigInteger).label(string);
    }

    public static Builder enumeration(Object ... objectArray) {
        return new Builder(BaseType.ENUM).items(objectArray);
    }

    public static Builder choice(Object ... objectArray) {
        return new Builder(BaseType.CHOICE).items(objectArray);
    }

    public static Builder placeholder() {
        return new Builder(null);
    }

    public static Builder seqof(Object ... objectArray) {
        return new Builder(BaseType.SEQ_OF).items(objectArray);
    }

    public static Builder octets() {
        return new Builder(BaseType.OCTET_STRING).unbounded();
    }

    public static Builder octets(int n) {
        return new Builder(BaseType.OCTET_STRING).fixedSize(n);
    }

    public static Builder octets(int n, int n2) {
        return new Builder(BaseType.OCTET_STRING).range(BigInteger.valueOf(n), BigInteger.valueOf(n2));
    }

    public static Builder ia5String() {
        return new Builder(BaseType.IA5String);
    }

    public static Builder utf8String() {
        return new Builder(BaseType.UTF8_STRING);
    }

    public static Builder utf8String(int n) {
        return new Builder(BaseType.UTF8_STRING).rangeToMAXFrom(n);
    }

    public static Builder utf8String(int n, int n2) {
        return new Builder(BaseType.UTF8_STRING).range(BigInteger.valueOf(n), BigInteger.valueOf(n2));
    }

    public static Builder opaque() {
        return new Builder(BaseType.OPAQUE);
    }

    public static List<Object> optional(Object ... objectArray) {
        return new OptionalList(Arrays.asList(objectArray));
    }

    public static ExtensionList extension(Object ... objectArray) {
        return new ExtensionList(1, Arrays.asList(objectArray));
    }

    public static ExtensionList extension(int n, Object ... objectArray) {
        return new ExtensionList(n, Arrays.asList(objectArray));
    }

    public static Builder deferred(ElementSupplier elementSupplier) {
        return new Builder(BaseType.Supplier).elementSupplier(elementSupplier);
    }

    public static enum BaseType {
        SEQ,
        SEQ_OF,
        CHOICE,
        ENUM,
        INT,
        OCTET_STRING,
        OPAQUE,
        UTF8_STRING,
        BIT_STRING,
        NULL,
        EXTENSION,
        ENUM_ITEM,
        BOOLEAN,
        IS0646String,
        PrintableString,
        NumericString,
        BMPString,
        UniversalString,
        IA5String,
        VisibleString,
        Switch,
        Supplier;

    }

    public static class Builder {
        protected final BaseType baseType;
        protected ArrayList<Builder> children = new ArrayList();
        protected boolean explicit = true;
        protected String typeName;
        protected String label;
        protected BigInteger upperBound;
        protected BigInteger lowerBound;
        protected BigInteger enumValue;
        protected ASN1Encodable defaultValue;
        protected Builder placeholderValue;
        protected Boolean inScope;
        protected Switch aSwitch;
        protected ArrayList<ASN1Encodable> validSwitchValues = new ArrayList();
        protected ElementSupplier elementSupplier;
        protected boolean mayRecurse;
        protected Map<String, ElementSupplier> supplierMap = new HashMap<String, ElementSupplier>();
        protected int block;
        private final ItemProvider defaultItemProvider = new ItemProvider(){

            @Override
            public Builder existingChild(int n, Builder builder) {
                return builder.copy(defaultItemProvider);
            }
        };

        public Builder(BaseType baseType) {
            this.baseType = baseType;
        }

        private Builder copy(ItemProvider itemProvider) {
            Builder builder = new Builder(this.baseType);
            int n = 0;
            for (Builder builder2 : this.children) {
                builder.children.add(itemProvider.existingChild(n++, builder2));
            }
            builder.explicit = this.explicit;
            builder.label = this.label;
            builder.upperBound = this.upperBound;
            builder.lowerBound = this.lowerBound;
            builder.defaultValue = this.defaultValue;
            builder.enumValue = this.enumValue;
            builder.inScope = this.inScope;
            builder.aSwitch = this.aSwitch;
            builder.validSwitchValues = new ArrayList<ASN1Encodable>(this.validSwitchValues);
            builder.elementSupplier = this.elementSupplier;
            builder.mayRecurse = this.mayRecurse;
            builder.typeName = this.typeName;
            builder.supplierMap = new HashMap<String, ElementSupplier>(this.supplierMap);
            builder.block = this.block;
            return builder;
        }

        protected Builder block(int n) {
            Builder builder = this.copy();
            builder.block = n;
            return builder;
        }

        public Builder copy() {
            return this.copy(this.defaultItemProvider);
        }

        public Builder elementSupplier(ElementSupplier elementSupplier) {
            Builder builder = this.copy();
            builder.elementSupplier = elementSupplier;
            return builder;
        }

        public Builder validSwitchValue(ASN1Encodable ... aSN1EncodableArray) {
            Builder builder = this.copy();
            builder.validSwitchValues.addAll(Arrays.asList(aSN1EncodableArray));
            return builder;
        }

        public Builder inScope(boolean bl) {
            Builder builder = this.copy();
            builder.inScope = bl;
            return builder;
        }

        public Builder limitScopeTo(String ... stringArray) {
            Builder builder = this.copy();
            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(Arrays.asList(stringArray));
            ArrayList<Builder> arrayList = new ArrayList<Builder>();
            for (Builder builder2 : this.children) {
                arrayList.add(builder2.copy().inScope(hashSet.contains(builder2.label)));
            }
            builder.children = arrayList;
            return builder;
        }

        public Builder typeName(String string) {
            Builder builder = this.copy();
            builder.typeName = string;
            if (builder.label == null) {
                builder.label = string;
            }
            return builder;
        }

        public Builder unbounded() {
            Builder builder = this.copy();
            builder.lowerBound = null;
            builder.upperBound = null;
            return builder;
        }

        public Builder decodeSwitch(Switch switch_) {
            Builder builder = this.copy();
            builder.aSwitch = switch_;
            return builder;
        }

        public Builder labelPrefix(String string) {
            Builder builder = this.copy();
            builder.label = string + " " + this.label;
            return builder;
        }

        public Builder explicit(boolean bl) {
            Builder builder = this.copy();
            builder.explicit = bl;
            return builder;
        }

        public Builder defaultValue(ASN1Encodable aSN1Encodable) {
            Builder builder = this.copy();
            builder.defaultValue = aSN1Encodable;
            return builder;
        }

        protected Builder wrap(boolean bl, Object object) {
            if (object instanceof Builder) {
                return ((Builder)object).explicit(bl);
            }
            if (object instanceof BaseType) {
                return new Builder((BaseType)((Object)object)).explicit(bl);
            }
            if (object instanceof String) {
                return OERDefinition.enumItem((String)object);
            }
            throw new IllegalStateException("Unable to wrap item in builder");
        }

        protected void addExtensions(Builder builder, ExtensionList extensionList) {
            if (extensionList.isEmpty()) {
                Builder builder2 = new Builder(BaseType.EXTENSION);
                builder2.block = extensionList.block;
                builder.children.add(builder2);
                return;
            }
            for (Object e : extensionList) {
                if (e instanceof OptionalList) {
                    this.addOptionals(builder, extensionList.block, (OptionalList)e);
                    continue;
                }
                Builder builder3 = this.wrap(true, e);
                builder3.block = extensionList.block;
                builder.children.add(builder3);
            }
        }

        protected void addOptionals(Builder builder, int n, OptionalList optionalList) {
            for (Object e : optionalList) {
                if (e instanceof ExtensionList) {
                    this.addExtensions(builder, (ExtensionList)e);
                    continue;
                }
                Builder builder2 = this.wrap(false, e);
                builder2.block = n;
                builder.children.add(builder2);
            }
        }

        public Builder items(Object ... objectArray) {
            Builder builder = this.copy();
            for (int i = 0; i != objectArray.length; ++i) {
                Object object = objectArray[i];
                if (object instanceof ExtensionList) {
                    this.addExtensions(builder, (ExtensionList)object);
                    continue;
                }
                if (object instanceof OptionalList) {
                    this.addOptionals(builder, builder.block, (OptionalList)object);
                    continue;
                }
                if (object.getClass().isArray()) {
                    for (int j = 0; j < ((Object[])object).length; ++j) {
                        builder.children.add(this.wrap(true, ((Object[])object)[j]));
                    }
                    continue;
                }
                builder.children.add(this.wrap(true, object));
            }
            return builder;
        }

        public Builder label(String string) {
            Builder builder = this.copy();
            builder.label = string;
            return builder;
        }

        public Builder mayRecurse(boolean bl) {
            Builder builder = this.copy();
            builder.mayRecurse = bl;
            return builder;
        }

        public Element build() {
            int n;
            ArrayList<Element> arrayList = new ArrayList<Element>();
            boolean bl = false;
            if (this.baseType == BaseType.ENUM) {
                n = 0;
                HashSet<BigInteger> hashSet = new HashSet<BigInteger>();
                for (int i = 0; i < this.children.size(); ++i) {
                    Builder builder = this.children.get(i);
                    if (builder.enumValue == null) {
                        builder.enumValue = BigInteger.valueOf(n);
                        ++n;
                    }
                    if (hashSet.contains(builder.enumValue)) {
                        throw new IllegalStateException("duplicate enum value at index " + i);
                    }
                    hashSet.add(builder.enumValue);
                }
            }
            n = 0;
            boolean bl2 = false;
            for (Builder builder : this.children) {
                if (!bl && builder.block > 0) {
                    bl = true;
                }
                if (!builder.explicit) {
                    ++n;
                }
                if (!bl2 && builder.defaultValue != null) {
                    bl2 = true;
                }
                arrayList.add(builder.build());
            }
            return new Element(this.baseType, arrayList, this.defaultValue == null && this.explicit, this.label, this.lowerBound, this.upperBound, bl, this.enumValue, this.defaultValue, this.aSwitch, this.validSwitchValues.isEmpty() ? null : this.validSwitchValues, this.elementSupplier, this.mayRecurse, this.typeName, this.supplierMap.isEmpty() ? null : this.supplierMap, this.block, n, bl2);
        }

        public Builder range(BigInteger bigInteger, BigInteger bigInteger2) {
            Builder builder = this.copy();
            builder.lowerBound = bigInteger;
            builder.upperBound = bigInteger2;
            return builder;
        }

        public Builder rangeToMAXFrom(long l) {
            Builder builder = this.copy();
            builder.lowerBound = BigInteger.valueOf(l);
            builder.upperBound = null;
            return builder;
        }

        public Builder rangeZeroTo(long l) {
            Builder builder = this.copy();
            builder.upperBound = BigInteger.valueOf(l);
            builder.lowerBound = BigInteger.ZERO;
            return builder;
        }

        public Builder fixedSize(long l) {
            Builder builder = this.copy();
            builder.upperBound = BigInteger.valueOf(l);
            builder.lowerBound = BigInteger.valueOf(l);
            return builder;
        }

        public Builder range(long l, long l2, ASN1Encodable aSN1Encodable) {
            Builder builder = this.copy();
            builder.lowerBound = BigInteger.valueOf(l);
            builder.upperBound = BigInteger.valueOf(l2);
            builder.defaultValue = aSN1Encodable;
            return builder;
        }

        public Builder enumValue(BigInteger bigInteger) {
            Builder builder = this.copy();
            this.enumValue = bigInteger;
            return builder;
        }

        public Builder replaceChild(final int n, final Builder builder) {
            return this.copy(new ItemProvider(){
                final /* synthetic */ Builder this$0;
                {
                    this.this$0 = builder3;
                }

                @Override
                public Builder existingChild(int n2, Builder builder2) {
                    return n == n2 ? builder : builder2;
                }
            });
        }
    }

    private static class ExtensionList
    extends ArrayList<Object> {
        protected final int block;

        public ExtensionList(int n, List<Object> list) {
            this.block = n;
            this.addAll(list);
        }
    }

    public static interface ItemProvider {
        public Builder existingChild(int var1, Builder var2);
    }

    public static class MutableBuilder
    extends Builder {
        private boolean frozen = false;

        public MutableBuilder(BaseType baseType) {
            super(baseType);
        }

        @Override
        public MutableBuilder label(String string) {
            this.label = string;
            return this;
        }

        public MutableBuilder addItemsAndFreeze(Builder ... builderArray) {
            if (this.frozen) {
                throw new IllegalStateException("build cannot be modified and must be copied only");
            }
            for (int i = 0; i != builderArray.length; ++i) {
                Builder builder = builderArray[i];
                if (builder instanceof OptionalList) {
                    Object[] objectArray = ((List)((Object)builder)).iterator();
                    while (objectArray.hasNext()) {
                        this.children.add(this.wrap(false, objectArray.next()));
                    }
                    continue;
                }
                if (builder.getClass().isArray()) {
                    for (Object object : (Object[])builder) {
                        this.children.add(this.wrap(true, object));
                    }
                    continue;
                }
                this.children.add(this.wrap(true, builder));
            }
            this.frozen = true;
            return this;
        }
    }

    private static class OptionalList
    extends ArrayList<Object> {
        public OptionalList(List<Object> list) {
            this.addAll(list);
        }
    }
}

