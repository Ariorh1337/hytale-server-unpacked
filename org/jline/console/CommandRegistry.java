/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jline.console.CmdDesc;
import org.jline.reader.Candidate;
import org.jline.reader.impl.completer.SystemCompleter;
import org.jline.terminal.Terminal;

public interface CommandRegistry {
    public static SystemCompleter aggregateCompleters(CommandRegistry ... commandRegistries) {
        SystemCompleter out = new SystemCompleter();
        for (CommandRegistry r : commandRegistries) {
            out.add(r.compileCompleters());
        }
        return out;
    }

    public static SystemCompleter compileCompleters(CommandRegistry ... commandRegistries) {
        SystemCompleter out = CommandRegistry.aggregateCompleters(commandRegistries);
        out.compile(s -> CommandRegistry.createCandidate(commandRegistries, s));
        return out;
    }

    public static Candidate createCandidate(CommandRegistry[] commandRegistries, String command) {
        String group = null;
        String desc = null;
        for (CommandRegistry registry : commandRegistries) {
            if (!registry.hasCommand(command)) continue;
            group = registry.name();
            desc = registry.commandInfo(command).stream().findFirst().orElse(null);
            break;
        }
        return new Candidate(command, command, group, desc, null, null, true);
    }

    default public String name() {
        return this.getClass().getSimpleName();
    }

    public Set<String> commandNames();

    public Map<String, String> commandAliases();

    public List<String> commandInfo(String var1);

    public boolean hasCommand(String var1);

    public SystemCompleter compileCompleters();

    public CmdDesc commandDescription(List<String> var1);

    default public Object invoke(CommandSession session, String command, Object ... args) throws Exception {
        throw new IllegalStateException("CommandRegistry method invoke(session, command, ... args) is not implemented!");
    }

    public static class CommandSession {
        private final Terminal terminal;
        private final InputStream in;
        private final PrintStream out;
        private final PrintStream err;

        public CommandSession() {
            this.in = System.in;
            this.out = System.out;
            this.err = System.err;
            this.terminal = null;
        }

        public CommandSession(Terminal terminal) {
            this(terminal, terminal.input(), new PrintStream(terminal.output()), new PrintStream(terminal.output()));
        }

        public CommandSession(Terminal terminal, InputStream in, PrintStream out, PrintStream err) {
            this.terminal = terminal;
            this.in = in;
            this.out = out;
            this.err = err;
        }

        public Terminal terminal() {
            return this.terminal;
        }

        public InputStream in() {
            return this.in;
        }

        public PrintStream out() {
            return this.out;
        }

        public PrintStream err() {
            return this.err;
        }
    }
}

