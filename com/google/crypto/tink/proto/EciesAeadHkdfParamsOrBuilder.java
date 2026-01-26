/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EcPointFormat;
import com.google.crypto.tink.proto.EciesAeadDemParams;
import com.google.crypto.tink.proto.EciesAeadDemParamsOrBuilder;
import com.google.crypto.tink.proto.EciesHkdfKemParams;
import com.google.crypto.tink.proto.EciesHkdfKemParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfParamsOrBuilder
extends MessageOrBuilder {
    public boolean hasKemParams();

    public EciesHkdfKemParams getKemParams();

    public EciesHkdfKemParamsOrBuilder getKemParamsOrBuilder();

    public boolean hasDemParams();

    public EciesAeadDemParams getDemParams();

    public EciesAeadDemParamsOrBuilder getDemParamsOrBuilder();

    public int getEcPointFormatValue();

    public EcPointFormat getEcPointFormat();
}

