/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

interface Http3RequestStreamCodecState {
    public static final Http3RequestStreamCodecState NO_STATE = new Http3RequestStreamCodecState(){

        @Override
        public boolean started() {
            return false;
        }

        @Override
        public boolean receivedFinalHeaders() {
            return false;
        }

        @Override
        public boolean terminated() {
            return false;
        }
    };

    public boolean started();

    public boolean receivedFinalHeaders();

    public boolean terminated();
}

