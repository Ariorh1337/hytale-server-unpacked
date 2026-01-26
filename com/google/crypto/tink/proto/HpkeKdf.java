/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Hpke;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum HpkeKdf implements ProtocolMessageEnum
{
    KDF_UNKNOWN(0),
    HKDF_SHA256(1),
    HKDF_SHA384(2),
    HKDF_SHA512(3),
    UNRECOGNIZED(-1);

    public static final int KDF_UNKNOWN_VALUE = 0;
    public static final int HKDF_SHA256_VALUE = 1;
    public static final int HKDF_SHA384_VALUE = 2;
    public static final int HKDF_SHA512_VALUE = 3;
    private static final Internal.EnumLiteMap<HpkeKdf> internalValueMap;
    private static final HpkeKdf[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static HpkeKdf valueOf(int value) {
        return HpkeKdf.forNumber(value);
    }

    public static HpkeKdf forNumber(int value) {
        switch (value) {
            case 0: {
                return KDF_UNKNOWN;
            }
            case 1: {
                return HKDF_SHA256;
            }
            case 2: {
                return HKDF_SHA384;
            }
            case 3: {
                return HKDF_SHA512;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<HpkeKdf> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        if (this == UNRECOGNIZED) {
            throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
        }
        return HpkeKdf.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return HpkeKdf.getDescriptor();
    }

    public static Descriptors.EnumDescriptor getDescriptor() {
        return Hpke.getDescriptor().getEnumTypes().get(1);
    }

    public static HpkeKdf valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != HpkeKdf.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private HpkeKdf(int value) {
        this.value = value;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HpkeKdf.class.getName());
        internalValueMap = new Internal.EnumLiteMap<HpkeKdf>(){

            @Override
            public HpkeKdf findValueByNumber(int number) {
                return HpkeKdf.forNumber(number);
            }
        };
        VALUES = HpkeKdf.values();
    }
}

