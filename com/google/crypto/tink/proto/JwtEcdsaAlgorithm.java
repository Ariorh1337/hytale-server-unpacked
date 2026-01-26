/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtEcdsa;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum JwtEcdsaAlgorithm implements ProtocolMessageEnum
{
    ES_UNKNOWN(0),
    ES256(1),
    ES384(2),
    ES512(3),
    UNRECOGNIZED(-1);

    public static final int ES_UNKNOWN_VALUE = 0;
    public static final int ES256_VALUE = 1;
    public static final int ES384_VALUE = 2;
    public static final int ES512_VALUE = 3;
    private static final Internal.EnumLiteMap<JwtEcdsaAlgorithm> internalValueMap;
    private static final JwtEcdsaAlgorithm[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static JwtEcdsaAlgorithm valueOf(int value) {
        return JwtEcdsaAlgorithm.forNumber(value);
    }

    public static JwtEcdsaAlgorithm forNumber(int value) {
        switch (value) {
            case 0: {
                return ES_UNKNOWN;
            }
            case 1: {
                return ES256;
            }
            case 2: {
                return ES384;
            }
            case 3: {
                return ES512;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<JwtEcdsaAlgorithm> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        if (this == UNRECOGNIZED) {
            throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
        }
        return JwtEcdsaAlgorithm.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return JwtEcdsaAlgorithm.getDescriptor();
    }

    public static Descriptors.EnumDescriptor getDescriptor() {
        return JwtEcdsa.getDescriptor().getEnumTypes().get(0);
    }

    public static JwtEcdsaAlgorithm valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != JwtEcdsaAlgorithm.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private JwtEcdsaAlgorithm(int value) {
        this.value = value;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtEcdsaAlgorithm.class.getName());
        internalValueMap = new Internal.EnumLiteMap<JwtEcdsaAlgorithm>(){

            @Override
            public JwtEcdsaAlgorithm findValueByNumber(int number) {
                return JwtEcdsaAlgorithm.forNumber(number);
            }
        };
        VALUES = JwtEcdsaAlgorithm.values();
    }
}

