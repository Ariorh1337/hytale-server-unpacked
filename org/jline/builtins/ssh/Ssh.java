/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.sshd.client.SshClient
 *  org.apache.sshd.client.auth.keyboard.UserInteraction
 *  org.apache.sshd.client.channel.ChannelShell
 *  org.apache.sshd.client.channel.ClientChannel
 *  org.apache.sshd.client.channel.ClientChannelEvent
 *  org.apache.sshd.client.future.ConnectFuture
 *  org.apache.sshd.client.session.ClientSession
 *  org.apache.sshd.common.NamedResource
 *  org.apache.sshd.common.channel.PtyMode
 *  org.apache.sshd.common.config.keys.FilePasswordProvider
 *  org.apache.sshd.common.keyprovider.KeyPairProvider
 *  org.apache.sshd.common.session.SessionContext
 *  org.apache.sshd.common.util.io.input.NoCloseInputStream
 *  org.apache.sshd.common.util.io.output.NoCloseOutputStream
 *  org.apache.sshd.scp.server.ScpCommandFactory$Builder
 *  org.apache.sshd.server.SshServer
 *  org.apache.sshd.server.command.CommandFactory
 *  org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
 *  org.apache.sshd.server.session.ServerSession
 *  org.apache.sshd.server.shell.ShellFactory
 *  org.apache.sshd.sftp.server.SftpSubsystemFactory$Builder
 */
package org.jline.builtins.ssh;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.common.util.io.input.NoCloseInputStream;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ShellFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.jline.builtins.Options;
import org.jline.builtins.ssh.ShellCommand;
import org.jline.builtins.ssh.ShellFactoryImpl;
import org.jline.reader.LineReader;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;

public class Ssh {
    public static final String[] functions = new String[]{"ssh", "sshd"};
    private static final int defaultPort = 2022;
    private final Consumer<ShellParams> shell;
    private final Consumer<ExecuteParams> execute;
    private final Supplier<SshServer> serverBuilder;
    private final Supplier<SshClient> clientBuilder;
    private SshServer server;
    private int port;
    private String ip;

    public Ssh(Consumer<ShellParams> shell, Consumer<ExecuteParams> execute, Supplier<SshServer> serverBuilder, Supplier<SshClient> clientBuilder) {
        this.shell = shell;
        this.execute = execute;
        this.serverBuilder = serverBuilder;
        this.clientBuilder = clientBuilder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ssh(Terminal terminal, LineReader reader, String user, InputStream stdin, PrintStream stdout, PrintStream stderr, String[] argv) throws Exception {
        block23: {
            String[] usage = new String[]{"ssh - connect to a server using ssh", "Usage: ssh [user@]hostname [command]", "  -? --help                show help"};
            Options opt = Options.compile(usage).parse(argv, true);
            List<String> args = opt.args();
            if (opt.isSet("help") || args.isEmpty()) {
                throw new Options.HelpException(opt.usage());
            }
            String username = user;
            String hostname = args.remove(0);
            int port = this.port;
            String command = null;
            int idx = hostname.indexOf(64);
            if (idx >= 0) {
                username = hostname.substring(0, idx);
                hostname = hostname.substring(idx + 1);
            }
            if ((idx = hostname.indexOf(58)) >= 0) {
                port = Integer.parseInt(hostname.substring(idx + 1));
                hostname = hostname.substring(0, idx);
            }
            if (!args.isEmpty()) {
                command = String.join((CharSequence)" ", args);
            }
            try (SshClient client = this.clientBuilder.get();){
                JLineUserInteraction ui = new JLineUserInteraction(terminal, reader, stderr);
                client.setFilePasswordProvider((FilePasswordProvider)ui);
                client.setUserInteraction((UserInteraction)ui);
                client.start();
                try (ClientSession sshSession = this.connectWithRetries(terminal.writer(), client, username, hostname, port, 3);){
                    sshSession.auth().verify();
                    if (command != null) {
                        ClientChannel channel = sshSession.createChannel("exec", command + "\n");
                        channel.setIn((InputStream)new ByteArrayInputStream(new byte[0]));
                        channel.setOut((OutputStream)new NoCloseOutputStream((OutputStream)stdout));
                        channel.setErr((OutputStream)new NoCloseOutputStream((OutputStream)stderr));
                        channel.open().verify();
                        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
                        break block23;
                    }
                    ChannelShell channel = sshSession.createShellChannel();
                    Attributes attributes = terminal.enterRawMode();
                    try {
                        HashMap<PtyMode, Integer> modes = new HashMap<PtyMode, Integer>();
                        Ssh.setMode(modes, PtyMode.VINTR, attributes.getControlChar(Attributes.ControlChar.VINTR));
                        Ssh.setMode(modes, PtyMode.VQUIT, attributes.getControlChar(Attributes.ControlChar.VQUIT));
                        Ssh.setMode(modes, PtyMode.VERASE, attributes.getControlChar(Attributes.ControlChar.VERASE));
                        Ssh.setMode(modes, PtyMode.VKILL, attributes.getControlChar(Attributes.ControlChar.VKILL));
                        Ssh.setMode(modes, PtyMode.VEOF, attributes.getControlChar(Attributes.ControlChar.VEOF));
                        Ssh.setMode(modes, PtyMode.VEOL, attributes.getControlChar(Attributes.ControlChar.VEOL));
                        Ssh.setMode(modes, PtyMode.VEOL2, attributes.getControlChar(Attributes.ControlChar.VEOL2));
                        Ssh.setMode(modes, PtyMode.VSTART, attributes.getControlChar(Attributes.ControlChar.VSTART));
                        Ssh.setMode(modes, PtyMode.VSTOP, attributes.getControlChar(Attributes.ControlChar.VSTOP));
                        Ssh.setMode(modes, PtyMode.VSUSP, attributes.getControlChar(Attributes.ControlChar.VSUSP));
                        Ssh.setMode(modes, PtyMode.VDSUSP, attributes.getControlChar(Attributes.ControlChar.VDSUSP));
                        Ssh.setMode(modes, PtyMode.VREPRINT, attributes.getControlChar(Attributes.ControlChar.VREPRINT));
                        Ssh.setMode(modes, PtyMode.VWERASE, attributes.getControlChar(Attributes.ControlChar.VWERASE));
                        Ssh.setMode(modes, PtyMode.VLNEXT, attributes.getControlChar(Attributes.ControlChar.VLNEXT));
                        Ssh.setMode(modes, PtyMode.VSTATUS, attributes.getControlChar(Attributes.ControlChar.VSTATUS));
                        Ssh.setMode(modes, PtyMode.VDISCARD, attributes.getControlChar(Attributes.ControlChar.VDISCARD));
                        Ssh.setMode(modes, PtyMode.IGNPAR, Ssh.getFlag(attributes, Attributes.InputFlag.IGNPAR));
                        Ssh.setMode(modes, PtyMode.PARMRK, Ssh.getFlag(attributes, Attributes.InputFlag.PARMRK));
                        Ssh.setMode(modes, PtyMode.INPCK, Ssh.getFlag(attributes, Attributes.InputFlag.INPCK));
                        Ssh.setMode(modes, PtyMode.ISTRIP, Ssh.getFlag(attributes, Attributes.InputFlag.ISTRIP));
                        Ssh.setMode(modes, PtyMode.INLCR, Ssh.getFlag(attributes, Attributes.InputFlag.INLCR));
                        Ssh.setMode(modes, PtyMode.IGNCR, Ssh.getFlag(attributes, Attributes.InputFlag.IGNCR));
                        Ssh.setMode(modes, PtyMode.ICRNL, Ssh.getFlag(attributes, Attributes.InputFlag.ICRNL));
                        Ssh.setMode(modes, PtyMode.IXON, Ssh.getFlag(attributes, Attributes.InputFlag.IXON));
                        Ssh.setMode(modes, PtyMode.IXANY, Ssh.getFlag(attributes, Attributes.InputFlag.IXANY));
                        Ssh.setMode(modes, PtyMode.IXOFF, Ssh.getFlag(attributes, Attributes.InputFlag.IXOFF));
                        Ssh.setMode(modes, PtyMode.ISIG, Ssh.getFlag(attributes, Attributes.LocalFlag.ISIG));
                        Ssh.setMode(modes, PtyMode.ICANON, Ssh.getFlag(attributes, Attributes.LocalFlag.ICANON));
                        Ssh.setMode(modes, PtyMode.ECHO, Ssh.getFlag(attributes, Attributes.LocalFlag.ECHO));
                        Ssh.setMode(modes, PtyMode.ECHOE, Ssh.getFlag(attributes, Attributes.LocalFlag.ECHOE));
                        Ssh.setMode(modes, PtyMode.ECHOK, Ssh.getFlag(attributes, Attributes.LocalFlag.ECHOK));
                        Ssh.setMode(modes, PtyMode.ECHONL, Ssh.getFlag(attributes, Attributes.LocalFlag.ECHONL));
                        Ssh.setMode(modes, PtyMode.NOFLSH, Ssh.getFlag(attributes, Attributes.LocalFlag.NOFLSH));
                        Ssh.setMode(modes, PtyMode.TOSTOP, Ssh.getFlag(attributes, Attributes.LocalFlag.TOSTOP));
                        Ssh.setMode(modes, PtyMode.IEXTEN, Ssh.getFlag(attributes, Attributes.LocalFlag.IEXTEN));
                        Ssh.setMode(modes, PtyMode.OPOST, Ssh.getFlag(attributes, Attributes.OutputFlag.OPOST));
                        Ssh.setMode(modes, PtyMode.ONLCR, Ssh.getFlag(attributes, Attributes.OutputFlag.ONLCR));
                        Ssh.setMode(modes, PtyMode.OCRNL, Ssh.getFlag(attributes, Attributes.OutputFlag.OCRNL));
                        Ssh.setMode(modes, PtyMode.ONOCR, Ssh.getFlag(attributes, Attributes.OutputFlag.ONOCR));
                        Ssh.setMode(modes, PtyMode.ONLRET, Ssh.getFlag(attributes, Attributes.OutputFlag.ONLRET));
                        channel.setPtyModes(modes);
                        channel.setPtyColumns(terminal.getWidth());
                        channel.setPtyLines(terminal.getHeight());
                        channel.setAgentForwarding(true);
                        channel.setEnv("TERM", (Object)terminal.getType());
                        channel.setIn((InputStream)new NoCloseInputStream(stdin));
                        channel.setOut((OutputStream)new NoCloseOutputStream((OutputStream)stdout));
                        channel.setErr((OutputStream)new NoCloseOutputStream((OutputStream)stderr));
                        channel.open().verify();
                        Terminal.SignalHandler prevWinchHandler = terminal.handle(Terminal.Signal.WINCH, signal -> {
                            try {
                                Size size = terminal.getSize();
                                channel.sendWindowChange(size.getColumns(), size.getRows());
                            }
                            catch (IOException iOException) {
                                // empty catch block
                            }
                        });
                        Terminal.SignalHandler prevQuitHandler = terminal.handle(Terminal.Signal.QUIT, signal -> {
                            try {
                                channel.getInvertedIn().write(attributes.getControlChar(Attributes.ControlChar.VQUIT));
                                channel.getInvertedIn().flush();
                            }
                            catch (IOException iOException) {
                                // empty catch block
                            }
                        });
                        Terminal.SignalHandler prevIntHandler = terminal.handle(Terminal.Signal.INT, signal -> {
                            try {
                                channel.getInvertedIn().write(attributes.getControlChar(Attributes.ControlChar.VINTR));
                                channel.getInvertedIn().flush();
                            }
                            catch (IOException iOException) {
                                // empty catch block
                            }
                        });
                        Terminal.SignalHandler prevStopHandler = terminal.handle(Terminal.Signal.TSTP, signal -> {
                            try {
                                channel.getInvertedIn().write(attributes.getControlChar(Attributes.ControlChar.VDSUSP));
                                channel.getInvertedIn().flush();
                            }
                            catch (IOException iOException) {
                                // empty catch block
                            }
                        });
                        try {
                            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
                        }
                        finally {
                            terminal.handle(Terminal.Signal.WINCH, prevWinchHandler);
                            terminal.handle(Terminal.Signal.INT, prevIntHandler);
                            terminal.handle(Terminal.Signal.TSTP, prevStopHandler);
                            terminal.handle(Terminal.Signal.QUIT, prevQuitHandler);
                        }
                    }
                    finally {
                        terminal.setAttributes(attributes);
                    }
                }
            }
        }
    }

    private static void setMode(Map<PtyMode, Integer> modes, PtyMode vintr, int attributes) {
        if (attributes >= 0) {
            modes.put(vintr, attributes);
        }
    }

    private static int getFlag(Attributes attributes, Attributes.InputFlag flag) {
        return attributes.getInputFlag(flag) ? 1 : 0;
    }

    private static int getFlag(Attributes attributes, Attributes.OutputFlag flag) {
        return attributes.getOutputFlag(flag) ? 1 : 0;
    }

    private static int getFlag(Attributes attributes, Attributes.LocalFlag flag) {
        return attributes.getLocalFlag(flag) ? 1 : 0;
    }

    private ClientSession connectWithRetries(PrintWriter stdout, SshClient client, String username, String host, int port, int maxAttempts) throws Exception {
        ClientSession session = null;
        int retries = 0;
        do {
            ConnectFuture future = client.connect(username, host, port);
            future.await();
            try {
                session = (ClientSession)future.getSession();
            }
            catch (Exception ex) {
                if (retries++ < maxAttempts) {
                    Thread.sleep(2000L);
                    stdout.println("retrying (attempt " + retries + ") ...");
                    continue;
                }
                throw ex;
            }
        } while (session == null);
        return session;
    }

    public void sshd(PrintStream stdout, PrintStream stderr, String[] argv) throws Exception {
        String[] usage = new String[]{"sshd - start an ssh server", "Usage: sshd [-i ip] [-p port] start | stop | status", "  -i --ip=INTERFACE        listen interface (default=127.0.0.1)", "  -p --port=PORT           listen port (default=2022)", "  -? --help                show help"};
        Options opt = Options.compile(usage).parse(argv, true);
        List<String> args = opt.args();
        if (opt.isSet("help") || args.isEmpty()) {
            throw new Options.HelpException(opt.usage());
        }
        String command = args.get(0);
        if ("start".equals(command)) {
            if (this.server != null) {
                throw new IllegalStateException("sshd is already running on port " + this.port);
            }
            this.ip = opt.get("ip");
            this.port = opt.getNumber("port");
            this.start();
            this.status(stdout);
        } else if ("stop".equals(command)) {
            if (this.server == null) {
                throw new IllegalStateException("sshd is not running.");
            }
            this.stop();
        } else if ("status".equals(command)) {
            this.status(stdout);
        } else {
            throw opt.usageError("bad command: " + command);
        }
    }

    private void status(PrintStream stdout) {
        if (this.server != null) {
            stdout.println("sshd is running on " + this.ip + ":" + this.port);
        } else {
            stdout.println("sshd is not running.");
        }
    }

    private void start() throws IOException {
        this.server = this.serverBuilder.get();
        this.server.setPort(this.port);
        this.server.setHost(this.ip);
        this.server.setShellFactory((ShellFactory)new ShellFactoryImpl(this.shell));
        this.server.setCommandFactory((CommandFactory)new ScpCommandFactory.Builder().withDelegate((channel, command) -> new ShellCommand(this.execute, command)).build());
        this.server.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory.Builder().build()));
        this.server.setKeyPairProvider((KeyPairProvider)new SimpleGeneratorHostKeyProvider());
        this.server.start();
    }

    private void stop() throws IOException {
        try {
            this.server.stop();
        }
        finally {
            this.server = null;
        }
    }

    private static class JLineUserInteraction
    implements UserInteraction,
    FilePasswordProvider {
        private final Terminal terminal;
        private final LineReader reader;
        private final PrintStream stderr;

        public JLineUserInteraction(Terminal terminal, LineReader reader, PrintStream stderr) {
            this.terminal = terminal;
            this.reader = reader;
            this.stderr = stderr;
        }

        public String getPassword(SessionContext session, NamedResource resourceKey, int retryIndex) throws IOException {
            return this.readLine("Enter password for " + resourceKey + ":", false);
        }

        public void welcome(ClientSession session, String banner, String lang) {
            this.terminal.writer().println(banner);
        }

        public String[] interactive(ClientSession s, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
            String[] answers = new String[prompt.length];
            try {
                for (int i = 0; i < prompt.length; ++i) {
                    answers[i] = this.readLine(prompt[i], echo[i]);
                }
            }
            catch (Exception e) {
                this.stderr.append(e.getClass().getSimpleName()).append(" while read prompts: ").println(e.getMessage());
            }
            return answers;
        }

        public boolean isInteractionAllowed(ClientSession session) {
            return true;
        }

        public void serverVersionInfo(ClientSession session, List<String> lines) {
            for (String l : lines) {
                this.terminal.writer().append('\t').println(l);
            }
        }

        public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
            try {
                return this.readLine(prompt, false);
            }
            catch (Exception e) {
                this.stderr.append(e.getClass().getSimpleName()).append(" while reading password: ").println(e.getMessage());
                return null;
            }
        }

        private String readLine(String prompt, boolean echo) {
            return this.reader.readLine(prompt + " ", echo ? null : Character.valueOf('\u0000'));
        }
    }

    public static class ExecuteParams {
        private final String command;
        private final Map<String, String> env;
        private final ServerSession session;
        private final InputStream in;
        private final OutputStream out;
        private final OutputStream err;

        public ExecuteParams(String command, Map<String, String> env, ServerSession session, InputStream in, OutputStream out, OutputStream err) {
            this.command = command;
            this.session = session;
            this.env = env;
            this.in = in;
            this.out = out;
            this.err = err;
        }

        public String getCommand() {
            return this.command;
        }

        public Map<String, String> getEnv() {
            return this.env;
        }

        public ServerSession getSession() {
            return this.session;
        }

        public InputStream getIn() {
            return this.in;
        }

        public OutputStream getOut() {
            return this.out;
        }

        public OutputStream getErr() {
            return this.err;
        }
    }

    public static class ShellParams {
        private final Map<String, String> env;
        private final Terminal terminal;
        private final Runnable closer;
        private final ServerSession session;

        public ShellParams(Map<String, String> env, ServerSession session, Terminal terminal, Runnable closer) {
            this.env = env;
            this.session = session;
            this.terminal = terminal;
            this.closer = closer;
        }

        public Map<String, String> getEnv() {
            return this.env;
        }

        public ServerSession getSession() {
            return this.session;
        }

        public Terminal getTerminal() {
            return this.terminal;
        }

        public Runnable getCloser() {
            return this.closer;
        }
    }
}

