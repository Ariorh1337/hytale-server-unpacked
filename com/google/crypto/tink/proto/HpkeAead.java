/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Hpke;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum HpkeAead implements ProtocolMessageEnum
{
    AEAD_UNKNOWN(0),
    AES_128_GCM(1),
    AES_256_GCM(2),
    CHACHA20_POLY1305(3),
    UNRECOGNIZED(-1);

    public static final int AEAD_UNKNOWN_VALUE = 0;
    public static final int AES_128_GCM_VALUE = 1;
    public static final int AES_256_GCM_VALUE = 2;
    public static final int CHACHA20_POLY1305_VALUE = 3;
    private static final Internal.EnumLiteMap<HpkeAead> internalValueMap;
    private static final HpkeAead[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static HpkeAead valueOf(int value) {
        return HpkeAead.forNumber(value);
    }

    public static HpkeAead forNumber(int value) {
        switch (value) {
            case 0: {
                return AEAD_UNKNOWN;
            }
            case 1: {
                return AES_128_GCM;
            }
            case 2: {
                return AES_256_GCM;
            }
            case 3: {
                return CHACHA20_POLY1305;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<HpkeAead> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        if (this == UNRECOGNIZED) {
            throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
        }
        return HpkeAead.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return HpkeAead.getDescriptor();
    }

    public static Descriptors.EnumDescriptor getDescriptor() {
        return Hpke.getDescriptor().getEnumTypes().get(2);
    }

    public static HpkeAead valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != HpkeAead.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private HpkeAead(int value) {
        this.value = value;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HpkeAead.class.getName());
        internalValueMap = new Internal.EnumLiteMap<HpkeAead>(){

            @Override
            public HpkeAead findValueByNumber(int number) {
                return HpkeAead.forNumber(number);
            }
        };
        VALUES = HpkeAead.values();
    }
}

