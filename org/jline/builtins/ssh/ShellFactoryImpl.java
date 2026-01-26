/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.sshd.common.channel.PtyMode
 *  org.apache.sshd.server.Environment
 *  org.apache.sshd.server.ExitCallback
 *  org.apache.sshd.server.Signal
 *  org.apache.sshd.server.channel.ChannelSession
 *  org.apache.sshd.server.command.Command
 *  org.apache.sshd.server.shell.ShellFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.jline.builtins.ssh;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;
import org.jline.builtins.ssh.Ssh;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellFactoryImpl
implements ShellFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShellFactoryImpl.class);
    private final Consumer<Ssh.ShellParams> shell;

    public ShellFactoryImpl(Consumer<Ssh.ShellParams> shell) {
        this.shell = shell;
    }

    public Command createShell(ChannelSession session) {
        return new ShellImpl();
    }

    static void flush(OutputStream ... streams) {
        for (OutputStream s : streams) {
            try {
                s.flush();
            }
            catch (IOException e) {
                LOGGER.debug("Error flushing " + s, (Throwable)e);
            }
        }
    }

    static void close(Closeable ... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            }
            catch (IOException e) {
                LOGGER.debug("Error closing " + c, (Throwable)e);
            }
        }
    }

    public class ShellImpl
    implements Command {
        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;
        private boolean closed;

        public void setInputStream(InputStream in) {
            this.in = in;
        }

        public void setOutputStream(OutputStream out) {
            this.out = out;
        }

        public void setErrorStream(OutputStream err) {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        public void start(ChannelSession session, Environment env) throws IOException {
            try {
                new Thread(() -> this.run(session, env)).start();
            }
            catch (Exception e) {
                throw new IOException("Unable to start shell", e);
            }
        }

        public void run(ChannelSession session, Environment env) {
            block31: {
                try {
                    Attributes attributes = new Attributes();
                    for (Map.Entry e : env.getPtyModes().entrySet()) {
                        switch ((PtyMode)e.getKey()) {
                            case VINTR: {
                                attributes.setControlChar(Attributes.ControlChar.VINTR, (Integer)e.getValue());
                                break;
                            }
                            case VQUIT: {
                                attributes.setControlChar(Attributes.ControlChar.VQUIT, (Integer)e.getValue());
                                break;
                            }
                            case VERASE: {
                                attributes.setControlChar(Attributes.ControlChar.VERASE, (Integer)e.getValue());
                                break;
                            }
                            case VKILL: {
                                attributes.setControlChar(Attributes.ControlChar.VKILL, (Integer)e.getValue());
                                break;
                            }
                            case VEOF: {
                                attributes.setControlChar(Attributes.ControlChar.VEOF, (Integer)e.getValue());
                                break;
                            }
                            case VEOL: {
                                attributes.setControlChar(Attributes.ControlChar.VEOL, (Integer)e.getValue());
                                break;
                            }
                            case VEOL2: {
                                attributes.setControlChar(Attributes.ControlChar.VEOL2, (Integer)e.getValue());
                                break;
                            }
                            case VSTART: {
                                attributes.setControlChar(Attributes.ControlChar.VSTART, (Integer)e.getValue());
                                break;
                            }
                            case VSTOP: {
                                attributes.setControlChar(Attributes.ControlChar.VSTOP, (Integer)e.getValue());
                                break;
                            }
                            case VSUSP: {
                                attributes.setControlChar(Attributes.ControlChar.VSUSP, (Integer)e.getValue());
                                break;
                            }
                            case VDSUSP: {
                                attributes.setControlChar(Attributes.ControlChar.VDSUSP, (Integer)e.getValue());
                                break;
                            }
                            case VREPRINT: {
                                attributes.setControlChar(Attributes.ControlChar.VREPRINT, (Integer)e.getValue());
                                break;
                            }
                            case VWERASE: {
                                attributes.setControlChar(Attributes.ControlChar.VWERASE, (Integer)e.getValue());
                                break;
                            }
                            case VLNEXT: {
                                attributes.setControlChar(Attributes.ControlChar.VLNEXT, (Integer)e.getValue());
                                break;
                            }
                            case VSTATUS: {
                                attributes.setControlChar(Attributes.ControlChar.VSTATUS, (Integer)e.getValue());
                                break;
                            }
                            case VDISCARD: {
                                attributes.setControlChar(Attributes.ControlChar.VDISCARD, (Integer)e.getValue());
                                break;
                            }
                            case ECHO: {
                                attributes.setLocalFlag(Attributes.LocalFlag.ECHO, (Integer)e.getValue() != 0);
                                break;
                            }
                            case ICANON: {
                                attributes.setLocalFlag(Attributes.LocalFlag.ICANON, (Integer)e.getValue() != 0);
                                break;
                            }
                            case ISIG: {
                                attributes.setLocalFlag(Attributes.LocalFlag.ISIG, (Integer)e.getValue() != 0);
                                break;
                            }
                            case ICRNL: {
                                attributes.setInputFlag(Attributes.InputFlag.ICRNL, (Integer)e.getValue() != 0);
                                break;
                            }
                            case INLCR: {
                                attributes.setInputFlag(Attributes.InputFlag.INLCR, (Integer)e.getValue() != 0);
                                break;
                            }
                            case IGNCR: {
                                attributes.setInputFlag(Attributes.InputFlag.IGNCR, (Integer)e.getValue() != 0);
                                break;
                            }
                            case OCRNL: {
                                attributes.setOutputFlag(Attributes.OutputFlag.OCRNL, (Integer)e.getValue() != 0);
                                break;
                            }
                            case ONLCR: {
                                attributes.setOutputFlag(Attributes.OutputFlag.ONLCR, (Integer)e.getValue() != 0);
                                break;
                            }
                            case ONLRET: {
                                attributes.setOutputFlag(Attributes.OutputFlag.ONLRET, (Integer)e.getValue() != 0);
                                break;
                            }
                            case OPOST: {
                                attributes.setOutputFlag(Attributes.OutputFlag.OPOST, (Integer)e.getValue() != 0);
                            }
                        }
                    }
                    Terminal terminal = TerminalBuilder.builder().name("JLine SSH").type((String)env.getEnv().get("TERM")).system(false).streams(this.in, this.out).attributes(attributes).size(new Size(Integer.parseInt((String)env.getEnv().get("COLUMNS")), Integer.parseInt((String)env.getEnv().get("LINES")))).build();
                    env.addSignalListener((channel, signals) -> {
                        terminal.setSize(new Size(Integer.parseInt((String)env.getEnv().get("COLUMNS")), Integer.parseInt((String)env.getEnv().get("LINES"))));
                        terminal.raise(Terminal.Signal.WINCH);
                    }, new Signal[]{Signal.WINCH});
                    ShellFactoryImpl.this.shell.accept(new Ssh.ShellParams(env.getEnv(), session.getSession(), terminal, () -> this.destroy(session)));
                }
                catch (Throwable t) {
                    if (this.closed) break block31;
                    LOGGER.error("Error occured while executing shell", t);
                }
            }
        }

        public void destroy(ChannelSession session) {
            if (!this.closed) {
                this.closed = true;
                ShellFactoryImpl.flush(this.out, this.err);
                ShellFactoryImpl.close(this.in, this.out, this.err);
                this.callback.onExit(0);
            }
        }
    }
}

