/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jline.builtins.ConsoleOptionGetter;
import org.jline.console.CmdDesc;
import org.jline.console.CmdLine;
import org.jline.console.CommandRegistry;
import org.jline.reader.Completer;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;

public interface SystemRegistry
extends CommandRegistry,
ConsoleOptionGetter {
    public void setCommandRegistries(CommandRegistry ... var1);

    public void register(String var1, CommandRegistry var2);

    public void initialize(File var1);

    public Collection<String> getPipeNames();

    public Completer completer();

    public CmdDesc commandDescription(CmdLine var1);

    public Object execute(String var1) throws Exception;

    public void cleanUp();

    public void trace(Throwable var1);

    public void trace(boolean var1, Throwable var2);

    @Override
    public Object consoleOption(String var1);

    @Override
    public <T> T consoleOption(String var1, T var2);

    public void setConsoleOption(String var1, Object var2);

    public Terminal terminal();

    public Object invoke(String var1, Object ... var2) throws Exception;

    public boolean isCommandOrScript(ParsedLine var1);

    public boolean isCommandOrScript(String var1);

    public boolean isCommandAlias(String var1);

    public void close();

    public static SystemRegistry get() {
        return Registeries.getInstance().getSystemRegistry();
    }

    public static void add(SystemRegistry systemRegistry) {
        Registeries.getInstance().addRegistry(systemRegistry);
    }

    public static void remove() {
        Registeries.getInstance().removeRegistry();
    }

    public static class Registeries {
        private static final Registeries instance = new Registeries();
        private final Map<Long, SystemRegistry> systemRegisteries = new HashMap<Long, SystemRegistry>();

        private Registeries() {
        }

        protected static Registeries getInstance() {
            return instance;
        }

        protected void addRegistry(SystemRegistry systemRegistry) {
            this.systemRegisteries.put(Registeries.getThreadId(), systemRegistry);
        }

        protected SystemRegistry getSystemRegistry() {
            return this.systemRegisteries.getOrDefault(Registeries.getThreadId(), null);
        }

        protected void removeRegistry() {
            this.systemRegisteries.remove(Registeries.getThreadId());
        }

        private static long getThreadId() {
            return Thread.currentThread().getId();
        }
    }
}

