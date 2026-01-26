/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jline.console.CommandInput;
import org.jline.reader.Completer;

public class CommandMethods {
    Function<CommandInput, ?> execute;
    Function<String, List<Completer>> compileCompleter;

    public CommandMethods(Function<CommandInput, ?> execute, Function<String, List<Completer>> compileCompleter) {
        this.execute = execute;
        this.compileCompleter = compileCompleter;
    }

    public CommandMethods(Consumer<CommandInput> execute, Function<String, List<Completer>> compileCompleter) {
        this.execute = i -> {
            execute.accept((CommandInput)i);
            return null;
        };
        this.compileCompleter = compileCompleter;
    }

    public Function<CommandInput, ?> execute() {
        return this.execute;
    }

    public Function<String, List<Completer>> compileCompleter() {
        return this.compileCompleter;
    }
}

