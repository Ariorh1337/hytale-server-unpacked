/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.kqueue;

import io.netty.channel.unix.IovArray;

final class NativeArrays {
    private IovArray iovArray;

    NativeArrays() {
    }

    IovArray cleanIovArray() {
        if (this.iovArray == null) {
            this.iovArray = new IovArray();
        } else {
            this.iovArray.clear();
        }
        return this.iovArray;
    }

    void free() {
        if (this.iovArray != null) {
            this.iovArray.release();
            this.iovArray = null;
        }
    }
}

