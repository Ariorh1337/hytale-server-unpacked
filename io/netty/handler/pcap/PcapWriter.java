/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;
import io.netty.handler.pcap.PcapHeaders;
import io.netty.handler.pcap.PcapWriteHandler;
import io.netty.handler.pcap.State;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

final class PcapWriter
implements Closeable {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PcapWriter.class);
    private final PcapWriteHandler pcapWriteHandler;
    private final OutputStream outputStream;

    PcapWriter(PcapWriteHandler pcapWriteHandler) throws IOException {
        this.pcapWriteHandler = pcapWriteHandler;
        this.outputStream = pcapWriteHandler.outputStream();
        if (pcapWriteHandler.writePcapGlobalHeader() && !pcapWriteHandler.sharedOutputStream()) {
            PcapHeaders.writeGlobalHeader(pcapWriteHandler.outputStream());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writePacket(ByteBuf packetHeaderBuf, ByteBuf packet) throws IOException {
        if (this.pcapWriteHandler.state() == State.CLOSED) {
            logger.debug("Pcap Write attempted on closed PcapWriter");
        }
        long timestamp = System.currentTimeMillis();
        PcapHeaders.writePacketHeader(packetHeaderBuf, (int)(timestamp / 1000L), (int)(timestamp % 1000L * 1000L), packet.readableBytes(), packet.readableBytes());
        if (this.pcapWriteHandler.sharedOutputStream()) {
            OutputStream outputStream = this.outputStream;
            synchronized (outputStream) {
                packetHeaderBuf.readBytes(this.outputStream, packetHeaderBuf.readableBytes());
                packet.readBytes(this.outputStream, packet.readableBytes());
            }
        } else {
            packetHeaderBuf.readBytes(this.outputStream, packetHeaderBuf.readableBytes());
            packet.readBytes(this.outputStream, packet.readableBytes());
        }
    }

    public String toString() {
        return "PcapWriter{outputStream=" + this.outputStream + '}';
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.pcapWriteHandler.state() == State.CLOSED) {
            logger.debug("PcapWriter is already closed");
        } else {
            if (this.pcapWriteHandler.sharedOutputStream()) {
                OutputStream outputStream = this.outputStream;
                synchronized (outputStream) {
                    this.outputStream.flush();
                }
            } else {
                this.outputStream.flush();
                this.outputStream.close();
            }
            this.pcapWriteHandler.markClosed();
            logger.debug("PcapWriter is now closed");
        }
    }
}

