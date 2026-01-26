/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import org.jline.builtins.Options;
import org.jline.builtins.telnet.Connection;
import org.jline.builtins.telnet.ConnectionData;
import org.jline.builtins.telnet.ConnectionEvent;
import org.jline.builtins.telnet.ConnectionListener;
import org.jline.builtins.telnet.ConnectionManager;
import org.jline.builtins.telnet.PortListener;
import org.jline.builtins.telnet.TelnetIO;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Telnet {
    public static final String[] functions = new String[]{"telnetd"};
    private static final int defaultPort = 2019;
    private final Terminal terminal;
    private final ShellProvider provider;
    private PortListener portListener;
    private ConnectionManager connectionManager;
    private int port;
    private String ip;

    public Telnet(Terminal terminal, ShellProvider provider) {
        this.terminal = terminal;
        this.provider = provider;
    }

    public void telnetd(String[] argv) throws Exception {
        String[] usage = new String[]{"telnetd - start simple telnet server", "Usage: telnetd [-i ip] [-p port] start | stop | status", "  -i --ip=INTERFACE        listen interface (default=127.0.0.1)", "  -p --port=PORT           listen port (default=2019)", "  -? --help                show help"};
        Options opt = Options.compile(usage).parse(argv, true);
        List<String> args = opt.args();
        if (opt.isSet("help") || args.isEmpty()) {
            throw new Options.HelpException(opt.usage());
        }
        String command = args.get(0);
        if ("start".equals(command)) {
            if (this.portListener != null) {
                throw new IllegalStateException("telnetd is already running on port " + this.port);
            }
            this.ip = opt.get("ip");
            this.port = opt.getNumber("port");
            this.start();
            this.status();
        } else if ("stop".equals(command)) {
            if (this.portListener == null) {
                throw new IllegalStateException("telnetd is not running.");
            }
            this.stop();
        } else if ("status".equals(command)) {
            this.status();
        } else {
            throw opt.usageError("bad command: " + command);
        }
    }

    private void status() {
        if (this.portListener != null) {
            System.out.println("telnetd is running on " + this.ip + ":" + this.port);
        } else {
            System.out.println("telnetd is not running.");
        }
    }

    private void start() throws IOException {
        this.connectionManager = new ConnectionManager(1000, 300000, 300000, 60000, null, null, false){

            @Override
            protected Connection createConnection(ThreadGroup threadGroup, ConnectionData newCD) {
                return new Connection(threadGroup, newCD){
                    TelnetIO telnetIO;

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    protected void doRun() throws Exception {
                        this.telnetIO = new TelnetIO();
                        this.telnetIO.setConnection(this);
                        this.telnetIO.initIO();
                        InputStream in = new InputStream(){

                            @Override
                            public int read() throws IOException {
                                return telnetIO.read();
                            }

                            @Override
                            public int read(byte[] b, int off, int len) throws IOException {
                                int r = this.read();
                                if (r >= 0) {
                                    b[off] = (byte)r;
                                    return 1;
                                }
                                return -1;
                            }
                        };
                        PrintStream out = new PrintStream(new OutputStream(){

                            @Override
                            public void write(int b) throws IOException {
                                telnetIO.write(b);
                            }

                            @Override
                            public void flush() throws IOException {
                                telnetIO.flush();
                            }
                        });
                        final Terminal terminal = TerminalBuilder.builder().type(this.getConnectionData().getNegotiatedTerminalType().toLowerCase()).streams(in, out).system(false).name("telnet").build();
                        terminal.setSize(new Size(this.getConnectionData().getTerminalColumns(), this.getConnectionData().getTerminalRows()));
                        terminal.setAttributes(Telnet.this.terminal.getAttributes());
                        this.addConnectionListener(new ConnectionListener(){
                            final /* synthetic */ 1 this$2;
                            {
                                this.this$2 = this$2;
                            }

                            @Override
                            public void connectionTerminalGeometryChanged(ConnectionEvent ce) {
                                terminal.setSize(new Size(this.this$2.getConnectionData().getTerminalColumns(), this.this$2.getConnectionData().getTerminalRows()));
                                terminal.raise(Terminal.Signal.WINCH);
                            }
                        });
                        try {
                            Telnet.this.provider.shell(terminal, this.getConnectionData().getEnvironment());
                        }
                        finally {
                            this.close();
                        }
                    }

                    @Override
                    protected void doClose() throws Exception {
                        this.telnetIO.closeOutput();
                        this.telnetIO.closeInput();
                    }
                };
            }
        };
        this.connectionManager.start();
        this.portListener = new PortListener("gogo", this.ip, this.port, 10);
        this.portListener.setConnectionManager(this.connectionManager);
        this.portListener.start();
    }

    private void stop() throws IOException {
        this.portListener.stop();
        this.portListener = null;
        this.connectionManager.stop();
        this.connectionManager = null;
    }

    public static interface ShellProvider {
        public void shell(Terminal var1, Map<String, String> var2);
    }
}

