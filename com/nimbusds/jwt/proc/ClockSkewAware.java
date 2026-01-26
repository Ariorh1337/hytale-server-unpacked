/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

public interface ClockSkewAware {
    public int getMaxClockSkew();

    public void setMaxClockSkew(int var1);
}

