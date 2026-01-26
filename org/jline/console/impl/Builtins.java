/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jline.builtins.Commands;
import org.jline.builtins.Completers;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.SyntaxHighlighter;
import org.jline.builtins.TTop;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.console.impl.JlineCommandRegistry;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.Widget;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

public class Builtins
extends JlineCommandRegistry
implements CommandRegistry {
    private final ConfigurationPath configPath;
    private final Function<String, Widget> widgetCreator;
    private final Supplier<Path> workDir;
    private LineReader reader;

    public Builtins(Path workDir, ConfigurationPath configPath, Function<String, Widget> widgetCreator) {
        this(null, () -> workDir, configPath, widgetCreator);
    }

    public Builtins(Set<Command> commands, Path workDir, ConfigurationPath configpath, Function<String, Widget> widgetCreator) {
        this(commands, () -> workDir, configpath, widgetCreator);
    }

    public Builtins(Supplier<Path> workDir, ConfigurationPath configPath, Function<String, Widget> widgetCreator) {
        this(null, workDir, configPath, widgetCreator);
    }

    public Builtins(Set<Command> commands, Supplier<Path> workDir, ConfigurationPath configpath, Function<String, Widget> widgetCreator) {
        Objects.requireNonNull(configpath);
        this.configPath = configpath;
        this.widgetCreator = widgetCreator;
        this.workDir = workDir;
        HashMap<Command, String> commandName = new HashMap<Command, String>();
        HashMap<Command, CommandMethods> commandExecute = new HashMap<Command, CommandMethods>();
        HashSet<Command> cmds = commands == null ? new HashSet<Command>(EnumSet.allOf(Command.class)) : new HashSet<Command>(commands);
        for (Command c : cmds) {
            commandName.put(c, c.name().toLowerCase());
        }
        commandExecute.put(Command.NANO, new CommandMethods(this::nano, this::nanoCompleter));
        commandExecute.put(Command.LESS, new CommandMethods(this::less, this::lessCompleter));
        commandExecute.put(Command.HISTORY, new CommandMethods(this::history, this::historyCompleter));
        commandExecute.put(Command.WIDGET, new CommandMethods(this::widget, this::widgetCompleter));
        commandExecute.put(Command.KEYMAP, new CommandMethods(this::keymap, this::defaultCompleter));
        commandExecute.put(Command.SETOPT, new CommandMethods(this::setopt, this::setoptCompleter));
        commandExecute.put(Command.SETVAR, new CommandMethods(this::setvar, this::setvarCompleter));
        commandExecute.put(Command.UNSETOPT, new CommandMethods(this::unsetopt, this::unsetoptCompleter));
        commandExecute.put(Command.TTOP, new CommandMethods(this::ttop, this::defaultCompleter));
        commandExecute.put(Command.COLORS, new CommandMethods(this::colors, this::defaultCompleter));
        commandExecute.put(Command.HIGHLIGHTER, new CommandMethods(this::highlighter, this::highlighterCompleter));
        this.registerCommands(commandName, commandExecute);
    }

    public void setLineReader(LineReader reader) {
        this.reader = reader;
    }

    private void less(CommandInput input) {
        try {
            Commands.less(input.terminal(), input.in(), input.out(), input.err(), this.workDir.get(), input.xargs(), this.configPath);
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void nano(CommandInput input) {
        try {
            Commands.nano(input.terminal(), input.out(), input.err(), this.workDir.get(), input.args(), this.configPath);
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void history(CommandInput input) {
        try {
            Commands.history(this.reader, input.out(), input.err(), this.workDir.get(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void widget(CommandInput input) {
        try {
            Commands.widget(this.reader, input.out(), input.err(), this.widgetCreator, input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void keymap(CommandInput input) {
        try {
            Commands.keymap(this.reader, input.out(), input.err(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void setopt(CommandInput input) {
        try {
            Commands.setopt(this.reader, input.out(), input.err(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void setvar(CommandInput input) {
        try {
            Commands.setvar(this.reader, input.out(), input.err(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void unsetopt(CommandInput input) {
        try {
            Commands.unsetopt(this.reader, input.out(), input.err(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void ttop(CommandInput input) {
        try {
            TTop.ttop(input.terminal(), input.out(), input.err(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void colors(CommandInput input) {
        try {
            Commands.colors(input.terminal(), input.out(), input.args());
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private void highlighter(CommandInput input) {
        try {
            Commands.highlighter(this.reader, input.terminal(), input.out(), input.err(), input.args(), this.configPath);
        }
        catch (Exception e) {
            this.saveException(e);
        }
    }

    private List<String> unsetOptions(boolean set) {
        ArrayList<String> out = new ArrayList<String>();
        for (LineReader.Option option : LineReader.Option.values()) {
            if (set != (this.reader.isSet(option) == option.isDef())) continue;
            out.add((option.isDef() ? "no-" : "") + option.toString().toLowerCase().replace('_', '-'));
        }
        return out;
    }

    private List<Completer> highlighterCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        List<Completers.OptDesc> optDescs = this.commandOptions(name);
        for (Completers.OptDesc o : optDescs) {
            Path ct;
            if (o.shortOption() == null || !o.shortOption().equals("-v") && !o.shortOption().equals("-s")) continue;
            Path userConfig = null;
            if (o.shortOption().equals("-s")) {
                try {
                    userConfig = this.configPath.getUserConfig("jnanorc");
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            if (!o.shortOption().equals("-v") && userConfig == null || (ct = SyntaxHighlighter.build(this.configPath.getConfig("jnanorc"), null).getCurrentTheme()) == null) continue;
            o.setValueCompleter(new Completers.FilesCompleter(ct.getParent(), "*.nanorctheme"));
        }
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter((Completer)NullCompleter.INSTANCE, optDescs, 1)));
        return completers;
    }

    private Set<String> allWidgets() {
        HashSet<String> out = new HashSet<String>();
        for (String s : this.reader.getWidgets().keySet()) {
            out.add(s);
            out.add(this.reader.getWidgets().get(s).toString());
        }
        return out;
    }

    private List<Completer> nanoCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter((Completer)new Completers.FilesCompleter(this.workDir), this::commandOptions, 1)));
        return completers;
    }

    private List<Completer> lessCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter((Completer)new Completers.FilesCompleter(this.workDir), this::commandOptions, 1)));
        return completers;
    }

    private List<Completer> historyCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        List<Completers.OptDesc> optDescs = this.commandOptions(name);
        for (Completers.OptDesc o : optDescs) {
            if (o.shortOption() == null || !o.shortOption().equals("-A") && !o.shortOption().equals("-W") && !o.shortOption().equals("-R")) continue;
            o.setValueCompleter(new Completers.FilesCompleter(this.workDir));
        }
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter((Completer)NullCompleter.INSTANCE, optDescs, 1)));
        return completers;
    }

    private List<Completer> widgetCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        List<Completers.OptDesc> optDescs = this.commandOptions(name);
        Candidate aliasOption = new Candidate("-A", "-A", null, null, null, null, true);
        Iterator<Completers.OptDesc> i = optDescs.iterator();
        while (i.hasNext()) {
            Completers.OptDesc o = i.next();
            if (o.shortOption() == null) continue;
            if (o.shortOption().equals("-D")) {
                o.setValueCompleter(new StringsCompleter(() -> this.reader.getWidgets().keySet()));
                continue;
            }
            if (!o.shortOption().equals("-A")) continue;
            aliasOption = new Candidate(o.shortOption(), o.shortOption(), null, o.description(), null, null, true);
            i.remove();
        }
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter((Completer)NullCompleter.INSTANCE, optDescs, 1)));
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new StringsCompleter(aliasOption), new StringsCompleter(this::allWidgets), new StringsCompleter(() -> this.reader.getWidgets().keySet()), NullCompleter.INSTANCE));
        return completers;
    }

    private List<Completer> setvarCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new StringsCompleter(() -> this.reader.getVariables().keySet()), NullCompleter.INSTANCE));
        return completers;
    }

    private List<Completer> setoptCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new StringsCompleter(() -> this.unsetOptions(true))));
        return completers;
    }

    private List<Completer> unsetoptCompleter(String name) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new StringsCompleter(() -> this.unsetOptions(false))));
        return completers;
    }

    public static enum Command {
        NANO,
        LESS,
        HISTORY,
        WIDGET,
        KEYMAP,
        SETOPT,
        SETVAR,
        UNSETOPT,
        TTOP,
        COLORS,
        HIGHLIGHTER;

    }
}

