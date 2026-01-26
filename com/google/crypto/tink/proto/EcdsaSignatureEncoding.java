/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Ecdsa;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum EcdsaSignatureEncoding implements ProtocolMessageEnum
{
    UNKNOWN_ENCODING(0),
    IEEE_P1363(1),
    DER(2),
    UNRECOGNIZED(-1);

    public static final int UNKNOWN_ENCODING_VALUE = 0;
    public static final int IEEE_P1363_VALUE = 1;
    public static final int DER_VALUE = 2;
    private static final Internal.EnumLiteMap<EcdsaSignatureEncoding> internalValueMap;
    private static final EcdsaSignatureEncoding[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static EcdsaSignatureEncoding valueOf(int value) {
        return EcdsaSignatureEncoding.forNumber(value);
    }

    public static EcdsaSignatureEncoding forNumber(int value) {
        switch (value) {
            case 0: {
                return UNKNOWN_ENCODING;
            }
            case 1: {
                return IEEE_P1363;
            }
            case 2: {
                return DER;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<EcdsaSignatureEncoding> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        if (this == UNRECOGNIZED) {
            throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
        }
        return EcdsaSignatureEncoding.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return EcdsaSignatureEncoding.getDescriptor();
    }

    public static Descriptors.EnumDescriptor getDescriptor() {
        return Ecdsa.getDescriptor().getEnumTypes().get(0);
    }

    public static EcdsaSignatureEncoding valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != EcdsaSignatureEncoding.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private EcdsaSignatureEncoding(int value) {
        this.value = value;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EcdsaSignatureEncoding.class.getName());
        internalValueMap = new Internal.EnumLiteMap<EcdsaSignatureEncoding>(){

            @Override
            public EcdsaSignatureEncoding findValueByNumber(int number) {
                return EcdsaSignatureEncoding.forNumber(number);
            }
        };
        VALUES = EcdsaSignatureEncoding.values();
    }
}

