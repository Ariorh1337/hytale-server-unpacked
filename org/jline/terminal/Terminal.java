/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.impl.NativeSignalHandler;
import org.jline.utils.ColorPalette;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public interface Terminal
extends Closeable,
Flushable {
    public static final String TYPE_DUMB = "dumb";
    public static final String TYPE_DUMB_COLOR = "dumb-color";

    public String getName();

    public SignalHandler handle(Signal var1, SignalHandler var2);

    public void raise(Signal var1);

    public NonBlockingReader reader();

    public PrintWriter writer();

    public Charset encoding();

    default public Charset inputEncoding() {
        return this.encoding();
    }

    default public Charset outputEncoding() {
        return this.encoding();
    }

    @Deprecated
    default public Charset stdinEncoding() {
        return this.inputEncoding();
    }

    @Deprecated
    default public Charset stdoutEncoding() {
        return this.outputEncoding();
    }

    @Deprecated
    default public Charset stderrEncoding() {
        return this.outputEncoding();
    }

    public InputStream input();

    public OutputStream output();

    public boolean canPauseResume();

    public void pause();

    public void pause(boolean var1) throws InterruptedException;

    public void resume();

    public boolean paused();

    public Attributes enterRawMode();

    public boolean echo();

    public boolean echo(boolean var1);

    public Attributes getAttributes();

    public void setAttributes(Attributes var1);

    public Size getSize();

    public void setSize(Size var1);

    default public int getWidth() {
        return this.getSize().getColumns();
    }

    default public int getHeight() {
        return this.getSize().getRows();
    }

    default public Size getBufferSize() {
        return this.getSize();
    }

    @Override
    public void flush();

    public String getType();

    public boolean puts(InfoCmp.Capability var1, Object ... var2);

    public boolean getBooleanCapability(InfoCmp.Capability var1);

    public Integer getNumericCapability(InfoCmp.Capability var1);

    public String getStringCapability(InfoCmp.Capability var1);

    public Cursor getCursorPosition(IntConsumer var1);

    public boolean hasMouseSupport();

    public boolean trackMouse(MouseTracking var1);

    public MouseTracking getCurrentMouseTracking();

    public MouseEvent readMouseEvent();

    public MouseEvent readMouseEvent(IntSupplier var1);

    public MouseEvent readMouseEvent(String var1);

    public MouseEvent readMouseEvent(IntSupplier var1, String var2);

    public boolean hasFocusSupport();

    public boolean trackFocus(boolean var1);

    public ColorPalette getPalette();

    default public int getDefaultForegroundColor() {
        return this.getPalette().getDefaultForeground();
    }

    default public int getDefaultBackgroundColor() {
        return this.getPalette().getDefaultBackground();
    }

    public static enum MouseTracking {
        Off,
        Normal,
        Button,
        Any;

    }

    public static interface SignalHandler {
        public static final SignalHandler SIG_DFL = NativeSignalHandler.SIG_DFL;
        public static final SignalHandler SIG_IGN = NativeSignalHandler.SIG_IGN;

        public void handle(Signal var1);
    }

    public static enum Signal {
        INT,
        QUIT,
        TSTP,
        CONT,
        INFO,
        WINCH;

    }
}

