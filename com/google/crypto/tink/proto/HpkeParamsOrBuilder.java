/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HpkeAead;
import com.google.crypto.tink.proto.HpkeKdf;
import com.google.crypto.tink.proto.HpkeKem;
import com.google.protobuf.MessageOrBuilder;

public interface HpkeParamsOrBuilder
extends MessageOrBuilder {
    public int getKemValue();

    public HpkeKem getKem();

    public int getKdfValue();

    public HpkeKdf getKdf();

    public int getAeadValue();

    public HpkeAead getAead();
}

