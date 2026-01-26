/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.jline.console.CommandRegistry;
import org.jline.console.SystemRegistry;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.Widget;

public interface ConsoleEngine
extends CommandRegistry {
    public static final String VAR_NANORC = "NANORC";

    public static String plainCommand(String command) {
        return command.startsWith(":") ? command.substring(1) : command;
    }

    public void setLineReader(LineReader var1);

    public void setSystemRegistry(SystemRegistry var1);

    public Object[] expandParameters(String[] var1) throws Exception;

    public String expandCommandLine(String var1);

    public String expandToList(List<String> var1);

    public Map<String, Boolean> scripts();

    public void setScriptExtension(String var1);

    public boolean hasAlias(String var1);

    public String getAlias(String var1);

    public Map<String, List<String>> getPipes();

    public List<String> getNamedPipes();

    public List<Completer> scriptCompleters();

    public void persist(Path var1, Object var2);

    public Object slurp(Path var1) throws IOException;

    public <T> T consoleOption(String var1, T var2);

    public void setConsoleOption(String var1, Object var2);

    public Object execute(String var1, String var2, String[] var3) throws Exception;

    default public Object execute(File script) throws Exception {
        return this.execute(script, "", new String[0]);
    }

    default public Object execute(File script, String rawLine, String[] args) throws Exception {
        return this.execute(script != null ? script.toPath() : null, rawLine, args);
    }

    public Object execute(Path var1, String var2, String[] var3) throws Exception;

    public ExecutionResult postProcess(String var1, Object var2, String var3);

    public ExecutionResult postProcess(Object var1);

    public void trace(Object var1);

    public void println(Object var1);

    public void putVariable(String var1, Object var2);

    public Object getVariable(String var1);

    public boolean hasVariable(String var1);

    public void purge();

    public boolean executeWidget(Object var1);

    public boolean isExecuting();

    public static class WidgetCreator
    implements Widget {
        private final ConsoleEngine consoleEngine;
        private final Object function;
        private final String name;

        public WidgetCreator(ConsoleEngine consoleEngine, String function) {
            this.consoleEngine = consoleEngine;
            this.name = function;
            this.function = consoleEngine.getVariable(function);
        }

        @Override
        public boolean apply() {
            return this.consoleEngine.executeWidget(this.function);
        }

        public String toString() {
            return this.name;
        }
    }

    public static class ExecutionResult {
        final int status;
        final Object result;

        public ExecutionResult(int status, Object result) {
            this.status = status;
            this.result = result;
        }

        public int status() {
            return this.status;
        }

        public Object result() {
            return this.result;
        }
    }
}

