/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsa;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum SlhDsaHashType implements ProtocolMessageEnum
{
    SLH_DSA_HASH_TYPE_UNSPECIFIED(0),
    SHA2(1),
    SHAKE(2),
    UNRECOGNIZED(-1);

    public static final int SLH_DSA_HASH_TYPE_UNSPECIFIED_VALUE = 0;
    public static final int SHA2_VALUE = 1;
    public static final int SHAKE_VALUE = 2;
    private static final Internal.EnumLiteMap<SlhDsaHashType> internalValueMap;
    private static final SlhDsaHashType[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static SlhDsaHashType valueOf(int value) {
        return SlhDsaHashType.forNumber(value);
    }

    public static SlhDsaHashType forNumber(int value) {
        switch (value) {
            case 0: {
                return SLH_DSA_HASH_TYPE_UNSPECIFIED;
            }
            case 1: {
                return SHA2;
            }
            case 2: {
                return SHAKE;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<SlhDsaHashType> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        if (this == UNRECOGNIZED) {
            throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
        }
        return SlhDsaHashType.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return SlhDsaHashType.getDescriptor();
    }

    public static Descriptors.EnumDescriptor getDescriptor() {
        return SlhDsa.getDescriptor().getEnumTypes().get(0);
    }

    public static SlhDsaHashType valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != SlhDsaHashType.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private SlhDsaHashType(int value) {
        this.value = value;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsaHashType.class.getName());
        internalValueMap = new Internal.EnumLiteMap<SlhDsaHashType>(){

            @Override
            public SlhDsaHashType findValueByNumber(int number) {
                return SlhDsaHashType.forNumber(number);
            }
        };
        VALUES = SlhDsaHashType.values();
    }
}

