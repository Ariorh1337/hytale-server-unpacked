/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsa;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum SlhDsaSignatureType implements ProtocolMessageEnum
{
    SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED(0),
    FAST_SIGNING(1),
    SMALL_SIGNATURE(2),
    UNRECOGNIZED(-1);

    public static final int SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED_VALUE = 0;
    public static final int FAST_SIGNING_VALUE = 1;
    public static final int SMALL_SIGNATURE_VALUE = 2;
    private static final Internal.EnumLiteMap<SlhDsaSignatureType> internalValueMap;
    private static final SlhDsaSignatureType[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static SlhDsaSignatureType valueOf(int value) {
        return SlhDsaSignatureType.forNumber(value);
    }

    public static SlhDsaSignatureType forNumber(int value) {
        switch (value) {
            case 0: {
                return SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED;
            }
            case 1: {
                return FAST_SIGNING;
            }
            case 2: {
                return SMALL_SIGNATURE;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<SlhDsaSignatureType> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        if (this == UNRECOGNIZED) {
            throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
        }
        return SlhDsaSignatureType.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return SlhDsaSignatureType.getDescriptor();
    }

    public static Descriptors.EnumDescriptor getDescriptor() {
        return SlhDsa.getDescriptor().getEnumTypes().get(1);
    }

    public static SlhDsaSignatureType valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != SlhDsaSignatureType.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private SlhDsaSignatureType(int value) {
        this.value = value;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsaSignatureType.class.getName());
        internalValueMap = new Internal.EnumLiteMap<SlhDsaSignatureType>(){

            @Override
            public SlhDsaSignatureType findValueByNumber(int number) {
                return SlhDsaSignatureType.forNumber(number);
            }
        };
        VALUES = SlhDsaSignatureType.values();
    }
}

