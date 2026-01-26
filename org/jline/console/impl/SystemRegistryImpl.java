/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.builtins.Completers;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.Options;
import org.jline.builtins.Styles;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.console.CmdLine;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.console.ConsoleEngine;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.ConsoleEngineImpl;
import org.jline.console.impl.JlineCommandRegistry;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.SystemCompleter;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;
import org.jline.utils.StyleResolver;

public class SystemRegistryImpl
implements SystemRegistry {
    private static final Class<?>[] BUILTIN_REGISTRIES = new Class[]{Builtins.class, ConsoleEngineImpl.class};
    private CommandRegistry[] commandRegistries;
    private Integer consoleId;
    protected final Parser parser;
    protected final ConfigurationPath configPath;
    protected final Supplier<Path> workDir;
    private final Map<String, CommandRegistry> subcommands = new HashMap<String, CommandRegistry>();
    private final Map<Pipe, String> pipeName = new HashMap<Pipe, String>();
    private final Map<String, CommandMethods> commandExecute = new HashMap<String, CommandMethods>();
    private final Map<String, List<String>> commandInfos = new HashMap<String, List<String>>();
    private Exception exception;
    private final CommandOutputStream outputStream;
    private ScriptStore scriptStore = new ScriptStore();
    private NamesAndValues names = new NamesAndValues();
    private final SystemCompleter customSystemCompleter = new SystemCompleter();
    private final AggregateCompleter customAggregateCompleter = new AggregateCompleter(new ArrayList<Completer>());
    private boolean commandGroups = true;
    private Function<CmdLine, CmdDesc> scriptDescription;

    public SystemRegistryImpl(Parser parser, Terminal terminal, Supplier<Path> workDir, ConfigurationPath configPath) {
        this.parser = parser;
        this.workDir = workDir;
        this.configPath = configPath;
        this.outputStream = new CommandOutputStream(terminal);
        this.pipeName.put(Pipe.FLIP, "|;");
        this.pipeName.put(Pipe.NAMED, "|");
        this.pipeName.put(Pipe.AND, "&&");
        this.pipeName.put(Pipe.OR, "||");
        this.commandExecute.put("exit", new CommandMethods(this::exit, this::exitCompleter));
        this.commandExecute.put("help", new CommandMethods(this::help, this::helpCompleter));
    }

    public void rename(Pipe pipe, String name) {
        if (name.matches("/w+") || this.pipeName.containsValue(name)) {
            throw new IllegalArgumentException();
        }
        this.pipeName.put(pipe, name);
    }

    public void renameLocal(String command, String newName) {
        CommandMethods old = this.commandExecute.remove(command);
        if (old != null) {
            this.commandExecute.put(newName, old);
        }
    }

    @Override
    public Collection<String> getPipeNames() {
        return this.pipeName.values();
    }

    @Override
    public void setCommandRegistries(CommandRegistry ... commandRegistries) {
        this.commandRegistries = commandRegistries;
        for (int i = 0; i < commandRegistries.length; ++i) {
            if (commandRegistries[i] instanceof ConsoleEngine) {
                if (this.consoleId != null) {
                    throw new IllegalArgumentException();
                }
                this.consoleId = i;
                ((ConsoleEngine)commandRegistries[i]).setSystemRegistry(this);
                this.scriptStore = new ScriptStore((ConsoleEngine)commandRegistries[i]);
                this.names = new NamesAndValues(this.configPath);
                continue;
            }
            if (!(commandRegistries[i] instanceof SystemRegistry)) continue;
            throw new IllegalArgumentException();
        }
        SystemRegistry.add(this);
    }

    @Override
    public void initialize(File script) {
        if (this.consoleId != null) {
            try {
                this.consoleEngine().execute(script);
            }
            catch (Exception e) {
                this.trace(e);
            }
        }
    }

    @Override
    public Set<String> commandNames() {
        HashSet<String> out = new HashSet<String>();
        for (CommandRegistry r : this.commandRegistries) {
            out.addAll(r.commandNames());
        }
        out.addAll(this.localCommandNames());
        return out;
    }

    private Set<String> localCommandNames() {
        return this.commandExecute.keySet();
    }

    @Override
    public Map<String, String> commandAliases() {
        HashMap<String, String> out = new HashMap<String, String>();
        for (CommandRegistry r : this.commandRegistries) {
            out.putAll(r.commandAliases());
        }
        return out;
    }

    @Override
    public Object consoleOption(String name) {
        return this.consoleOption(name, null);
    }

    @Override
    public <T> T consoleOption(String name, T defVal) {
        T out = defVal;
        if (this.consoleId != null) {
            out = this.consoleEngine().consoleOption(name, defVal);
        }
        return out;
    }

    @Override
    public void setConsoleOption(String name, Object value) {
        if (this.consoleId != null) {
            this.consoleEngine().setConsoleOption(name, value);
        }
    }

    @Override
    public void register(String command, CommandRegistry subcommandRegistry) {
        this.subcommands.put(command, subcommandRegistry);
        this.commandExecute.put(command, new CommandMethods(this::subcommand, this::emptyCompleter));
    }

    private List<String> localCommandInfo(String command) {
        try {
            CommandRegistry subCommand = this.subcommands.get(command);
            if (subCommand != null) {
                this.registryHelp(subCommand);
            } else {
                this.localExecute(command, new String[]{"--help"});
            }
        }
        catch (Options.HelpException e) {
            this.exception = null;
            return JlineCommandRegistry.compileCommandInfo(e.getMessage());
        }
        catch (Exception e) {
            this.trace(e);
        }
        return new ArrayList<String>();
    }

    @Override
    public List<String> commandInfo(String command) {
        int id = this.registryId(command);
        List<String> out = new ArrayList<String>();
        if (id > -1) {
            if (!this.commandInfos.containsKey(command)) {
                this.commandInfos.put(command, this.commandRegistries[id].commandInfo(command));
            }
            out = this.commandInfos.get(command);
        } else if (this.scriptStore.hasScript(command) && this.consoleEngine() != null) {
            out = this.consoleEngine().commandInfo(command);
        } else if (this.isLocalCommand(command)) {
            out = this.localCommandInfo(command);
        }
        return out;
    }

    @Override
    public boolean hasCommand(String command) {
        return this.registryId(command) > -1 || this.isLocalCommand(command);
    }

    public void setGroupCommandsInHelp(boolean commandGroups) {
        this.commandGroups = commandGroups;
    }

    public SystemRegistryImpl groupCommandsInHelp(boolean commandGroups) {
        this.commandGroups = commandGroups;
        return this;
    }

    private boolean isLocalCommand(String command) {
        return this.commandExecute.containsKey(command);
    }

    @Override
    public boolean isCommandOrScript(ParsedLine line) {
        return this.isCommandOrScript(this.parser.getCommand(line.words().get(0)));
    }

    @Override
    public boolean isCommandOrScript(String command) {
        if (this.hasCommand(command)) {
            return true;
        }
        return this.scriptStore.hasScript(command);
    }

    public void addCompleter(Completer completer) {
        if (completer instanceof SystemCompleter) {
            SystemCompleter sc = (SystemCompleter)completer;
            if (sc.isCompiled()) {
                this.customAggregateCompleter.getCompleters().add(sc);
            } else {
                this.customSystemCompleter.add(sc);
            }
        } else {
            this.customAggregateCompleter.getCompleters().add(completer);
        }
    }

    @Override
    public SystemCompleter compileCompleters() {
        throw new IllegalStateException("Use method completer() to retrieve Completer!");
    }

    private SystemCompleter _compileCompleters() {
        SystemCompleter out = CommandRegistry.aggregateCompleters(this.commandRegistries);
        SystemCompleter local = new SystemCompleter();
        for (String command : this.commandExecute.keySet()) {
            CommandRegistry subCommand = this.subcommands.get(command);
            if (subCommand != null) {
                for (Map.Entry<String, List<Completer>> entry : subCommand.compileCompleters().getCompleters().entrySet()) {
                    for (Completer cc : entry.getValue()) {
                        if (!(cc instanceof ArgumentCompleter)) {
                            throw new IllegalArgumentException();
                        }
                        List<Completer> cmps = ((ArgumentCompleter)cc).getCompleters();
                        cmps.add(0, NullCompleter.INSTANCE);
                        cmps.set(1, new StringsCompleter(entry.getKey()));
                        Completer last = cmps.get(cmps.size() - 1);
                        if (last instanceof Completers.OptionCompleter) {
                            ((Completers.OptionCompleter)last).setStartPos(cmps.size() - 1);
                            cmps.set(cmps.size() - 1, last);
                        }
                        local.add(command, (Completer)new ArgumentCompleter(cmps));
                    }
                }
                continue;
            }
            local.add(command, this.commandExecute.get(command).compileCompleter().apply(command));
        }
        local.add(this.customSystemCompleter);
        out.add(local);
        out.compile(s -> CommandRegistry.createCandidate(this.commandRegistries, s));
        return out;
    }

    @Override
    public Completer completer() {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(this._compileCompleters());
        completers.add(this.customAggregateCompleter);
        if (this.consoleId != null) {
            completers.addAll(this.consoleEngine().scriptCompleters());
            completers.add(new PipelineCompleter(this.workDir, this.pipeName, this.names).doCompleter());
        }
        return new AggregateCompleter(completers);
    }

    private CmdDesc localCommandDescription(String command) {
        if (!this.isLocalCommand(command)) {
            throw new IllegalArgumentException();
        }
        try {
            this.localExecute(command, new String[]{"--help"});
        }
        catch (Options.HelpException e) {
            this.exception = null;
            return JlineCommandRegistry.compileCommandDescription(e.getMessage());
        }
        catch (Exception e) {
            this.trace(e);
        }
        return null;
    }

    @Override
    public CmdDesc commandDescription(List<String> args) {
        CmdDesc out = new CmdDesc(false);
        String command = args.get(0);
        int id = this.registryId(command);
        if (id > -1) {
            out = this.commandRegistries[id].commandDescription(args);
        } else if (this.scriptStore.hasScript(command) && this.consoleEngine() != null) {
            out = this.consoleEngine().commandDescription(args);
        } else if (this.isLocalCommand(command)) {
            out = this.localCommandDescription(command);
        }
        return out;
    }

    private CmdDesc commandDescription(CommandRegistry subreg) {
        ArrayList<AttributedString> main = new ArrayList<AttributedString>();
        HashMap<String, List<AttributedString>> options = new HashMap<String, List<AttributedString>>();
        StyleResolver helpStyle = Styles.helpStyle();
        for (String sc : new TreeSet<String>(subreg.commandNames())) {
            Iterator<String> iterator = subreg.commandInfo(sc).iterator();
            if (!iterator.hasNext()) continue;
            String info = iterator.next();
            main.add(Options.HelpException.highlightSyntax(sc + " -  " + info, helpStyle, true));
        }
        return new CmdDesc(main, ArgDesc.doArgNames(Collections.singletonList("")), options);
    }

    public void setScriptDescription(Function<CmdLine, CmdDesc> scriptDescription) {
        this.scriptDescription = scriptDescription;
    }

    @Override
    public CmdDesc commandDescription(CmdLine line) {
        Object out = null;
        String cmd = this.parser.getCommand(line.getArgs().get(0));
        switch (line.getDescriptionType()) {
            case COMMAND: {
                if (!this.isCommandOrScript(cmd) || this.names.hasPipes(line.getArgs())) break;
                List<String> args = line.getArgs();
                CommandRegistry subCommand = this.subcommands.get(cmd);
                if (subCommand != null) {
                    String c;
                    String string = c = args.size() > 1 ? args.get(1) : null;
                    out = c == null || subCommand.hasCommand(c) ? (c != null && c.equals("help") ? null : (c != null ? subCommand.commandDescription(Collections.singletonList(c)) : this.commandDescription(subCommand))) : this.commandDescription(subCommand);
                    if (out == null) break;
                    ((CmdDesc)out).setSubcommand(true);
                    break;
                }
                args.set(0, cmd);
                out = this.commandDescription(args);
                break;
            }
            case METHOD: 
            case SYNTAX: {
                if (this.isCommandOrScript(cmd) || this.scriptDescription == null) break;
                out = this.scriptDescription.apply(line);
            }
        }
        return out;
    }

    @Override
    public Object invoke(String command, Object ... args) throws Exception {
        Object[] objectArray;
        Object out = null;
        command = ConsoleEngine.plainCommand(command);
        if (args == null) {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = null;
        } else {
            objectArray = args;
        }
        args = objectArray;
        int id = this.registryId(command);
        if (id > -1) {
            out = this.commandRegistries[id].invoke(this.commandSession(), command, args);
        } else if (this.isLocalCommand(command)) {
            out = this.localExecute(command, args);
        } else if (this.consoleId != null) {
            out = this.consoleEngine().invoke(this.commandSession(), command, args);
        }
        return out;
    }

    private Object localExecute(String command, Object[] args) throws Exception {
        if (!this.isLocalCommand(command)) {
            throw new IllegalArgumentException();
        }
        Object out = this.commandExecute.get(command).execute().apply(new CommandInput(command, args, this.commandSession()));
        if (this.exception != null) {
            throw this.exception;
        }
        return out;
    }

    @Override
    public Terminal terminal() {
        return this.commandSession().terminal();
    }

    private CommandRegistry.CommandSession commandSession() {
        return this.outputStream.getCommandSession();
    }

    @Override
    public boolean isCommandAlias(String command) {
        if (this.consoleEngine() == null) {
            return false;
        }
        ConsoleEngine consoleEngine = this.consoleEngine();
        if (!this.parser.validCommandName(command) || !consoleEngine.hasAlias(command)) {
            return false;
        }
        String value = consoleEngine.getAlias(command).split("\\s+")[0];
        return !this.names.isPipe(value);
    }

    private String replaceCommandAlias(String variable, String command, String rawLine) {
        ConsoleEngine consoleEngine = this.consoleEngine();
        assert (consoleEngine != null);
        return variable == null ? rawLine.replaceFirst(command + "(\\b|$)", consoleEngine.getAlias(command)) : rawLine.replaceFirst("=" + command + "(\\b|$)", "=" + consoleEngine.getAlias(command));
    }

    private String replacePipeAlias(ArgsParser ap, String pipeAlias, List<String> args, Map<String, List<String>> customPipes) {
        ConsoleEngine consoleEngine = this.consoleEngine();
        assert (consoleEngine != null);
        String alias = pipeAlias;
        for (int j = 0; j < args.size(); ++j) {
            alias = alias.replaceAll("\\s\\$" + j + "\\b", " " + args.get(j));
            alias = alias.replaceAll("\\$\\{" + j + "(|:-.*)}", args.get(j));
        }
        alias = alias.replaceAll("\\$\\{@}", consoleEngine.expandToList(args));
        alias = alias.replaceAll("\\$@", consoleEngine.expandToList(args));
        alias = alias.replaceAll("\\s+\\$\\d\\b", "");
        alias = alias.replaceAll("\\s+\\$\\{\\d+}", "");
        alias = alias.replaceAll("\\$\\{\\d+}", "");
        Matcher matcher = Pattern.compile("\\$\\{\\d+:-(.*?)}").matcher(alias);
        if (matcher.find()) {
            alias = matcher.replaceAll("$1");
        }
        ap.parse(alias);
        List<String> ws = ap.args();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ws.size(); ++i) {
            if (ws.get(i).equals(this.pipeName.get((Object)Pipe.NAMED))) {
                if (i + 1 < ws.size() && consoleEngine.hasAlias(ws.get(i + 1))) {
                    args.clear();
                    String innerPipe = consoleEngine.getAlias(ws.get(++i));
                    while (i < ws.size() - 1 && !this.names.isPipe(ws.get(i + 1), customPipes.keySet())) {
                        args.add(ws.get(++i));
                    }
                    sb.append(this.replacePipeAlias(ap, innerPipe, args, customPipes));
                    continue;
                }
                sb.append(ws.get(i)).append(' ');
                continue;
            }
            sb.append(ws.get(i)).append(' ');
        }
        return sb.toString();
    }

    private void replacePipeAliases(ConsoleEngine consoleEngine, Map<String, List<String>> customPipes, ArgsParser ap) {
        List<String> words = ap.args();
        if (consoleEngine != null && words.contains(this.pipeName.get((Object)Pipe.NAMED))) {
            StringBuilder sb = new StringBuilder();
            boolean trace = false;
            for (int i = 0; i < words.size(); ++i) {
                if (words.get(i).equals(this.pipeName.get((Object)Pipe.NAMED))) {
                    if (i + 1 < words.size() && consoleEngine.hasAlias(words.get(i + 1))) {
                        trace = true;
                        ArrayList<String> args = new ArrayList<String>();
                        String pipeAlias = consoleEngine.getAlias(words.get(++i));
                        while (i < words.size() - 1 && !this.names.isPipe(words.get(i + 1), customPipes.keySet())) {
                            args.add(words.get(++i));
                        }
                        sb.append(this.replacePipeAlias(ap, pipeAlias, args, customPipes));
                        continue;
                    }
                    sb.append(words.get(i)).append(' ');
                    continue;
                }
                sb.append(words.get(i)).append(' ');
            }
            ap.parse(sb.toString());
            if (trace) {
                consoleEngine.trace(ap.line());
            }
        }
    }

    private List<CommandData> compileCommandLine(String commandLine) {
        ArrayList<CommandData> out = new ArrayList<CommandData>();
        ArgsParser ap = new ArgsParser(this.parser);
        ap.parse(commandLine);
        ConsoleEngine consoleEngine = this.consoleEngine();
        HashMap<String, List<String>> customPipes = consoleEngine != null ? consoleEngine.getPipes() : new HashMap<String, List<String>>();
        this.replacePipeAliases(consoleEngine, customPipes, ap);
        List<String> words = ap.args();
        String nextRawLine = ap.line();
        int first = 0;
        ArrayList<String> pipes = new ArrayList<String>();
        String pipeSource = null;
        String rawLine = null;
        String pipeResult = null;
        if (this.isCommandAlias(ap.command())) {
            ap.parse(this.replaceCommandAlias(ap.variable(), ap.command(), nextRawLine));
            this.replacePipeAliases(consoleEngine, customPipes, ap);
            nextRawLine = ap.line();
            words = ap.args();
        }
        if (!this.names.hasPipes(words)) {
            out.add(new CommandData(ap, false, nextRawLine, ap.variable(), null, false, ""));
        } else {
            do {
                String subLine;
                String rawCommand = this.parser.getCommand(words.get(first));
                String command = ConsoleEngine.plainCommand(rawCommand);
                String variable = this.parser.getVariable(words.get(first));
                if (this.isCommandAlias(command)) {
                    ap.parse(this.replaceCommandAlias(variable, command, nextRawLine));
                    this.replacePipeAliases(consoleEngine, customPipes, ap);
                    rawCommand = ap.rawCommand();
                    command = ap.command();
                    words = ap.args();
                    first = 0;
                }
                if (this.scriptStore.isConsoleScript(command) && !rawCommand.startsWith(":")) {
                    throw new IllegalArgumentException("Commands must be used in pipes with colon prefix!");
                }
                int last = words.size();
                File file = null;
                boolean append = false;
                boolean pipeStart = false;
                boolean skipPipe = false;
                ArrayList<String> _words = new ArrayList<String>();
                for (int i = first; i < last; ++i) {
                    if (words.get(i).equals(">") || words.get(i).equals(">>")) {
                        pipes.add(words.get(i));
                        append = words.get(i).equals(">>");
                        if (i + 1 >= last) {
                            throw new IllegalArgumentException();
                        }
                        file = this.redirectFile(words.get(i + 1));
                        last = i + 1;
                        break;
                    }
                    if (this.consoleId == null) {
                        _words.add(words.get(i));
                        continue;
                    }
                    if (words.get(i).equals(this.pipeName.get((Object)Pipe.FLIP))) {
                        if (variable != null || file != null || pipeResult != null || this.consoleId == null) {
                            throw new IllegalArgumentException();
                        }
                        pipes.add(words.get(i));
                        last = i;
                        variable = "_pipe" + (pipes.size() - 1);
                        break;
                    }
                    if (words.get(i).equals(this.pipeName.get((Object)Pipe.NAMED)) || words.get(i).matches("^.*[^a-zA-Z0-9 ].*$") && customPipes.containsKey(words.get(i))) {
                        String pipe = words.get(i);
                        if (pipe.equals(this.pipeName.get((Object)Pipe.NAMED))) {
                            if (i + 1 >= last) {
                                throw new IllegalArgumentException("Pipe is NULL!");
                            }
                            pipe = words.get(i + 1);
                            if (!pipe.matches("\\w+") || !customPipes.containsKey(pipe)) {
                                throw new IllegalArgumentException("Unknown or illegal pipe name: " + pipe);
                            }
                        }
                        pipes.add(pipe);
                        last = i;
                        if (pipeSource != null) break;
                        pipeSource = "_pipe" + (pipes.size() - 1);
                        pipeResult = variable;
                        variable = pipeSource;
                        pipeStart = true;
                        break;
                    }
                    if (words.get(i).equals(this.pipeName.get((Object)Pipe.OR)) || words.get(i).equals(this.pipeName.get((Object)Pipe.AND))) {
                        if (variable != null || pipeSource != null) {
                            pipes.add(words.get(i));
                        } else if (pipes.size() > 0 && (((String)pipes.get(pipes.size() - 1)).equals(">") || ((String)pipes.get(pipes.size() - 1)).equals(">>"))) {
                            pipes.remove(pipes.size() - 1);
                            ((CommandData)out.get(out.size() - 1)).setPipe(words.get(i));
                            skipPipe = true;
                        } else {
                            pipes.add(words.get(i));
                            pipeSource = "_pipe" + (pipes.size() - 1);
                            pipeResult = variable;
                            variable = pipeSource;
                            pipeStart = true;
                        }
                        last = i;
                        break;
                    }
                    _words.add(words.get(i));
                }
                if (last == words.size()) {
                    pipes.add("END_PIPE");
                } else if (skipPipe) {
                    first = last + 1;
                    continue;
                }
                String string = subLine = last < words.size() || first > 0 ? String.join((CharSequence)" ", _words) : ap.line();
                if (last + 1 < words.size()) {
                    nextRawLine = String.join((CharSequence)" ", words.subList(last + 1, words.size()));
                }
                boolean done = true;
                boolean statement = false;
                ArrayList<String> arglist = new ArrayList<String>();
                if (!_words.isEmpty()) {
                    arglist.addAll(_words.subList(1, _words.size()));
                }
                if (rawLine != null || pipes.size() > 1 && customPipes.containsKey(pipes.get(pipes.size() - 2))) {
                    done = false;
                    if (rawLine == null) {
                        rawLine = pipeSource;
                    }
                    if (customPipes.containsKey(pipes.get(pipes.size() - 2))) {
                        List fixes = (List)customPipes.get(pipes.get(pipes.size() - 2));
                        if (((String)pipes.get(pipes.size() - 2)).matches("\\w+")) {
                            int idx = subLine.indexOf(" ");
                            subLine = idx > 0 ? subLine.substring(idx + 1) : "";
                        }
                        rawLine = rawLine + (String)fixes.get(0) + (this.consoleId != null ? this.consoleEngine().expandCommandLine(subLine) : subLine) + (String)fixes.get(1);
                        statement = true;
                    }
                    if (((String)pipes.get(pipes.size() - 1)).equals(this.pipeName.get((Object)Pipe.FLIP)) || ((String)pipes.get(pipes.size() - 1)).equals(this.pipeName.get((Object)Pipe.AND)) || ((String)pipes.get(pipes.size() - 1)).equals(this.pipeName.get((Object)Pipe.OR))) {
                        done = true;
                        pipeSource = null;
                        if (variable != null) {
                            rawLine = variable + " = " + rawLine;
                        }
                    }
                    if (last + 1 >= words.size() || file != null) {
                        done = true;
                        pipeSource = null;
                        if (pipeResult != null) {
                            rawLine = pipeResult + " = " + rawLine;
                        }
                    }
                } else if (((String)pipes.get(pipes.size() - 1)).equals(this.pipeName.get((Object)Pipe.FLIP)) || pipeStart) {
                    if (pipeStart && pipeResult != null) {
                        subLine = subLine.substring(subLine.indexOf("=") + 1);
                    }
                    rawLine = this.flipArgument(command, subLine, pipes, arglist);
                    rawLine = variable + "=" + rawLine;
                } else {
                    rawLine = this.flipArgument(command, subLine, pipes, arglist);
                }
                if (done) {
                    out.add(new CommandData(ap, statement, rawLine, variable, file, append, (String)pipes.get(pipes.size() - 1)));
                    if (((String)pipes.get(pipes.size() - 1)).equals(this.pipeName.get((Object)Pipe.AND)) || ((String)pipes.get(pipes.size() - 1)).equals(this.pipeName.get((Object)Pipe.OR))) {
                        pipeSource = null;
                        pipeResult = null;
                    }
                    rawLine = null;
                }
                first = last + 1;
            } while (first < words.size());
        }
        return out;
    }

    private File redirectFile(String name) {
        File out = name.equals("null") ? (OSUtils.IS_WINDOWS ? new File("NUL") : new File("/dev/null")) : new File(name);
        return out;
    }

    private String flipArgument(String command, String subLine, List<String> pipes, List<String> arglist) {
        String out;
        if (pipes.size() > 1 && pipes.get(pipes.size() - 2).equals(this.pipeName.get((Object)Pipe.FLIP))) {
            String s = this.isCommandOrScript(command) ? "$" : "";
            out = subLine + " " + s + "_pipe" + (pipes.size() - 2);
            if (!command.isEmpty()) {
                arglist.add(s + "_pipe" + (pipes.size() - 2));
            }
        } else {
            out = subLine;
        }
        return out;
    }

    private Object execute(String command, String rawLine, String[] args) throws Exception {
        Object out;
        if (!this.parser.validCommandName(command)) {
            throw new UnknownCommandException("Invalid command: " + rawLine);
        }
        int id = this.registryId(command);
        if (id > -1) {
            Object[] _args = this.consoleId != null ? this.consoleEngine().expandParameters(args) : args;
            out = this.commandRegistries[id].invoke(this.outputStream.getCommandSession(), command, _args);
        } else if (this.scriptStore.hasScript(command) && this.consoleEngine() != null) {
            out = this.consoleEngine().execute(command, rawLine, args);
        } else if (this.isLocalCommand(command)) {
            out = this.localExecute(command, this.consoleId != null ? this.consoleEngine().expandParameters(args) : args);
        } else {
            throw new UnknownCommandException("Unknown command: " + command);
        }
        return out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public Object execute(String line) throws Exception {
        if (line.trim().isEmpty() || line.trim().startsWith("#")) {
            return null;
        }
        long start = new Date().getTime();
        Object out = null;
        boolean statement = false;
        boolean postProcessed = false;
        int errorCount = 0;
        this.scriptStore.refresh();
        List<CommandData> cmds = this.compileCommandLine(line);
        ConsoleEngine consoleEngine = this.consoleEngine();
        for (CommandData cmd : cmds) {
            if (cmd.file() != null && this.scriptStore.isConsoleScript(cmd.command())) {
                throw new IllegalArgumentException("Console script output cannot be redirected!");
            }
            try {
                this.outputStream.close();
                if (consoleEngine != null && !consoleEngine.isExecuting()) {
                    this.trace(cmd);
                }
                this.exception = null;
                statement = false;
                postProcessed = false;
                if (cmd.variable() != null || cmd.file() != null) {
                    if (cmd.file() != null) {
                        this.outputStream.redirect(cmd.file(), cmd.append());
                    } else if (this.consoleId != null) {
                        this.outputStream.redirect();
                    }
                    this.outputStream.open(this.consoleOption("redirectColor", false));
                }
                boolean consoleScript = false;
                try {
                    out = this.execute(cmd.command(), cmd.rawLine(), cmd.args());
                }
                catch (UnknownCommandException e) {
                    if (consoleEngine == null) {
                        throw e;
                    }
                    consoleScript = true;
                }
                if (consoleEngine != null) {
                    if (consoleScript) {
                        boolean bl = statement = cmd.command().isEmpty() || !this.scriptStore.hasScript(cmd.command());
                        if (statement && this.outputStream.isByteOutputStream()) {
                            this.outputStream.close();
                        }
                        out = consoleEngine.execute(cmd.command(), cmd.rawLine(), cmd.args());
                    }
                    if (cmd.pipe().equals(this.pipeName.get((Object)Pipe.OR)) || cmd.pipe().equals(this.pipeName.get((Object)Pipe.AND))) {
                        boolean success;
                        ConsoleEngine.ExecutionResult er = this.postProcess(cmd, statement, consoleEngine, out);
                        postProcessed = true;
                        consoleEngine.println(er.result());
                        out = null;
                        boolean bl = success = er.status() == 0;
                        if (cmd.pipe().equals(this.pipeName.get((Object)Pipe.OR)) && success || cmd.pipe().equals(this.pipeName.get((Object)Pipe.AND)) && !success) {
                            if (postProcessed || consoleEngine == null) break;
                            out = this.postProcess(cmd, statement, consoleEngine, out).result();
                            break;
                        }
                    }
                }
                if (postProcessed || consoleEngine == null) continue;
                out = this.postProcess(cmd, statement, consoleEngine, out).result();
            }
            catch (Options.HelpException e) {
                this.trace(e);
                if (postProcessed || consoleEngine == null) continue;
                out = this.postProcess(cmd, statement, consoleEngine, out).result();
            }
            catch (Exception e2) {
                ++errorCount;
                if (!cmd.pipe().equals(this.pipeName.get((Object)Pipe.OR))) {
                    throw e2;
                }
                this.trace(e2);
                postProcessed = true;
                if (postProcessed || consoleEngine == null) continue;
                out = this.postProcess(cmd, statement, consoleEngine, out).result();
                continue;
                {
                    catch (Throwable throwable) {
                        if (!postProcessed && consoleEngine != null) {
                            out = this.postProcess(cmd, statement, consoleEngine, out).result();
                        }
                        throw throwable;
                    }
                }
            }
        }
        if (errorCount == 0) {
            this.names.extractNames(line);
        }
        Log.debug("execute: ", new Date().getTime() - start, " msec");
        return out;
    }

    private ConsoleEngine.ExecutionResult postProcess(CommandData cmd, boolean statement, ConsoleEngine consoleEngine, Object result) {
        ConsoleEngine.ExecutionResult out;
        if (cmd.file() != null) {
            int status = 1;
            if (cmd.file().exists()) {
                long delta = new Date().getTime() - cmd.file().lastModified();
                status = delta < 100L ? 0 : 1;
            }
            out = new ConsoleEngine.ExecutionResult(status, result);
        } else if (!statement) {
            this.outputStream.close();
            out = consoleEngine.postProcess(cmd.rawLine(), result, this.outputStream.getOutput());
        } else if (cmd.variable() != null) {
            out = consoleEngine.hasVariable(cmd.variable()) ? consoleEngine.postProcess(consoleEngine.getVariable(cmd.variable())) : consoleEngine.postProcess(result);
            out = new ConsoleEngine.ExecutionResult(out.status(), null);
        } else {
            out = consoleEngine.postProcess(result);
        }
        return out;
    }

    @Override
    public void cleanUp() {
        this.outputStream.close();
        this.outputStream.resetOutput();
        if (this.consoleEngine() != null) {
            this.consoleEngine().purge();
        }
    }

    private void trace(CommandData commandData) {
        if (this.consoleEngine() != null) {
            this.consoleEngine().trace(commandData);
        } else {
            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.append(commandData.rawLine(), AttributedStyle.DEFAULT.foreground(3)).println(this.terminal());
        }
    }

    @Override
    public void trace(Throwable exception) {
        this.outputStream.close();
        ConsoleEngine consoleEngine = this.consoleEngine();
        if (consoleEngine != null) {
            if (!(exception instanceof Options.HelpException)) {
                consoleEngine.putVariable("exception", exception);
            }
            consoleEngine.trace(exception);
        } else {
            this.trace(false, exception);
        }
    }

    @Override
    public void trace(boolean stack, Throwable exception) {
        if (exception instanceof Options.HelpException) {
            Options.HelpException.highlight(exception.getMessage(), Styles.helpStyle()).print(this.terminal());
        } else if (exception instanceof UnknownCommandException) {
            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.append(exception.getMessage(), Styles.prntStyle().resolve(".em"));
            asb.toAttributedString().println(this.terminal());
        } else if (stack) {
            exception.printStackTrace();
        } else {
            String message = exception.getMessage();
            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.style(Styles.prntStyle().resolve(".em"));
            if (message != null) {
                asb.append(exception.getClass().getSimpleName()).append(": ").append(message);
            } else {
                asb.append("Caught exception: ");
                asb.append(exception.getClass().getCanonicalName());
            }
            asb.toAttributedString().println(this.terminal());
            Log.debug("Stack: ", exception);
        }
    }

    @Override
    public void close() {
        this.names.save();
    }

    public ConsoleEngine consoleEngine() {
        return this.consoleId != null ? (ConsoleEngine)this.commandRegistries[this.consoleId] : null;
    }

    private boolean isBuiltinRegistry(CommandRegistry registry) {
        for (Class<?> c : BUILTIN_REGISTRIES) {
            if (c != registry.getClass()) continue;
            return true;
        }
        return false;
    }

    private void printHeader(String header) {
        AttributedStringBuilder asb = new AttributedStringBuilder().tabs(2);
        asb.append("\t");
        asb.append(header, Options.HelpException.defaultStyle().resolve(".ti"));
        asb.append(":");
        asb.toAttributedString().println(this.terminal());
    }

    private void printCommandInfo(String command, String info, int max) {
        AttributedStringBuilder asb = new AttributedStringBuilder().tabs(Arrays.asList(4, max + 4));
        asb.append("\t");
        asb.append(command, Options.HelpException.defaultStyle().resolve(".co"));
        asb.append("\t");
        asb.append(info, Options.HelpException.defaultStyle().resolve(".de"));
        asb.setLength(this.terminal().getWidth());
        asb.toAttributedString().println(this.terminal());
    }

    private void printCommands(Collection<String> commands, int max) {
        AttributedStringBuilder asb = new AttributedStringBuilder().tabs(Arrays.asList(4, max + 4));
        int col = 0;
        asb.append("\t");
        col += 4;
        boolean done = false;
        for (String c : commands) {
            asb.append(c, Options.HelpException.defaultStyle().resolve(".co"));
            asb.append("\t");
            if ((col += max) + max > this.terminal().getWidth()) {
                asb.toAttributedString().println(this.terminal());
                asb = new AttributedStringBuilder().tabs(Arrays.asList(4, max + 4));
                col = 0;
                asb.append("\t");
                col += 4;
                done = true;
                continue;
            }
            done = false;
        }
        if (!done) {
            asb.toAttributedString().println(this.terminal());
        }
        this.terminal().flush();
    }

    private String doCommandInfo(List<String> info) {
        return info != null && !info.isEmpty() ? info.get(0) : " ";
    }

    private boolean isInTopics(List<String> args, String name) {
        return args.isEmpty() || args.contains(name);
    }

    private Options parseOptions(String[] usage, Object[] args) throws Options.HelpException {
        Options opt = Options.compile(usage).parse(args);
        if (opt.isSet("help")) {
            throw new Options.HelpException(opt.usage());
        }
        return opt;
    }

    private Object help(CommandInput input) {
        String groupsOption = this.commandGroups ? "nogroups" : "groups";
        String groupsHelp = this.commandGroups ? "     --nogroups                   Commands are not grouped by registries" : "     --groups                     Commands are grouped by registries";
        String[] usage = new String[]{"help -  command help", "Usage: help [TOPIC...]", "  -? --help                       Displays command help", groupsHelp, "  -i --info                       List commands with a short command info"};
        try {
            Options opt = this.parseOptions(usage, input.args());
            boolean doTopic = false;
            boolean cg = this.commandGroups;
            boolean info = false;
            if (!opt.args().isEmpty() && opt.args().size() == 1) {
                try {
                    String[] args = new String[]{"--help"};
                    String command = opt.args().get(0);
                    this.execute(command, command + " " + args[0], args);
                }
                catch (UnknownCommandException e) {
                    doTopic = true;
                }
                catch (Exception e) {
                    this.exception = e;
                }
            } else {
                doTopic = true;
                if (opt.isSet(groupsOption)) {
                    boolean bl = cg = !cg;
                }
                if (opt.isSet("info")) {
                    info = true;
                }
            }
            if (doTopic) {
                this.helpTopic(opt.args(), cg, info);
            }
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private void helpTopic(List<String> topics, boolean commandGroups, boolean info) {
        Set<String> commands = this.commandNames();
        commands.addAll(this.scriptStore.getScripts());
        boolean withInfo = commands.size() < this.terminal().getHeight() || !topics.isEmpty() || info;
        int max = Collections.max(commands, Comparator.comparing(String::length)).length() + 1;
        TreeMap<String, String> builtinCommands = new TreeMap<String, String>();
        TreeMap<String, String> systemCommands = new TreeMap<String, String>();
        if (!commandGroups && topics.isEmpty()) {
            TreeSet<String> ordered = new TreeSet<String>(commands);
            if (withInfo) {
                for (String c : ordered) {
                    List<String> list = this.commandInfo(c);
                    String cmdInfo = list.isEmpty() ? "" : list.get(0);
                    this.printCommandInfo(c, cmdInfo, max);
                }
            } else {
                this.printCommands(ordered, max);
            }
        } else {
            for (CommandRegistry commandRegistry : this.commandRegistries) {
                if (!this.isBuiltinRegistry(commandRegistry)) continue;
                for (String c : commandRegistry.commandNames()) {
                    builtinCommands.put(c, this.doCommandInfo(this.commandInfo(c)));
                }
            }
            for (String string : this.localCommandNames()) {
                systemCommands.put(string, this.doCommandInfo(this.commandInfo(string)));
                this.exception = null;
            }
            if (this.isInTopics(topics, "System")) {
                this.printHeader("System");
                if (withInfo) {
                    for (Map.Entry entry : systemCommands.entrySet()) {
                        this.printCommandInfo((String)entry.getKey(), (String)entry.getValue(), max);
                    }
                } else {
                    this.printCommands(systemCommands.keySet(), max);
                }
            }
            if (this.isInTopics(topics, "Builtins") && !builtinCommands.isEmpty()) {
                this.printHeader("Builtins");
                if (withInfo) {
                    for (Map.Entry entry : builtinCommands.entrySet()) {
                        this.printCommandInfo((String)entry.getKey(), (String)entry.getValue(), max);
                    }
                } else {
                    this.printCommands(builtinCommands.keySet(), max);
                }
            }
            for (CommandRegistry commandRegistry : this.commandRegistries) {
                if (this.isBuiltinRegistry(commandRegistry) || !this.isInTopics(topics, commandRegistry.name()) || commandRegistry.commandNames().isEmpty()) continue;
                TreeSet<String> cmds = new TreeSet<String>(commandRegistry.commandNames());
                this.printHeader(commandRegistry.name());
                if (withInfo) {
                    for (String c : cmds) {
                        this.printCommandInfo(c, this.doCommandInfo(this.commandInfo(c)), max);
                    }
                    continue;
                }
                this.printCommands(cmds, max);
            }
            if (this.consoleId != null && this.isInTopics(topics, "Scripts") && !this.scriptStore.getScripts().isEmpty()) {
                this.printHeader("Scripts");
                if (withInfo) {
                    for (String string : this.scriptStore.getScripts()) {
                        this.printCommandInfo(string, this.doCommandInfo(this.commandInfo(string)), max);
                    }
                } else {
                    this.printCommands(this.scriptStore.getScripts(), max);
                }
            }
        }
        this.terminal().flush();
    }

    private Object exit(CommandInput input) {
        String[] usage = new String[]{"exit -  exit from app/script", "Usage: exit [OBJECT]", "  -? --help                       Displays command help"};
        try {
            Options opt = this.parseOptions(usage, input.xargs());
            ConsoleEngine consoleEngine = this.consoleEngine();
            if (!opt.argObjects().isEmpty() && consoleEngine != null) {
                try {
                    consoleEngine.putVariable("_return", opt.argObjects().size() == 1 ? opt.argObjects().get(0) : opt.argObjects());
                }
                catch (Exception e) {
                    this.trace(e);
                }
            }
            this.exception = new EndOfFileException();
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private void registryHelp(CommandRegistry registry) throws Exception {
        ArrayList<Integer> tabs = new ArrayList<Integer>();
        tabs.add(0);
        tabs.add(9);
        int max = registry.commandNames().stream().map(String::length).max(Integer::compareTo).get();
        tabs.add(10 + max);
        AttributedStringBuilder sb = new AttributedStringBuilder().tabs(tabs);
        sb.append(" -  ");
        sb.append(registry.name());
        sb.append(" registry");
        sb.append("\n");
        boolean first = true;
        for (String c : new TreeSet<String>(registry.commandNames())) {
            if (first) {
                sb.append("Summary:");
                first = false;
            }
            sb.append("\t");
            sb.append(c);
            sb.append("\t");
            sb.append(registry.commandInfo(c).get(0));
            sb.append("\n");
        }
        throw new Options.HelpException(sb.toString());
    }

    private Object subcommand(CommandInput input) {
        Object out = null;
        try {
            if (input.args().length > 0 && this.subcommands.get(input.command()).hasCommand(input.args()[0])) {
                out = this.subcommands.get(input.command()).invoke(input.session(), input.args()[0], input.xargs().length > 1 ? Arrays.copyOfRange(input.xargs(), 1, input.xargs().length) : new Object[]{});
            } else {
                this.registryHelp(this.subcommands.get(input.command()));
            }
        }
        catch (Exception e) {
            this.exception = e;
        }
        return out;
    }

    private List<Completers.OptDesc> commandOptions(String command) {
        try {
            this.localExecute(command, new String[]{"--help"});
        }
        catch (Options.HelpException e) {
            this.exception = null;
            return JlineCommandRegistry.compileCommandOptions(e.getMessage());
        }
        catch (Exception e) {
            this.trace(e);
        }
        return null;
    }

    private List<String> registryNames() {
        ArrayList<String> out = new ArrayList<String>();
        out.add("System");
        out.add("Builtins");
        if (this.consoleId != null) {
            out.add("Scripts");
        }
        for (CommandRegistry r : this.commandRegistries) {
            if (this.isBuiltinRegistry(r)) continue;
            out.add(r.name());
        }
        out.addAll(this.commandNames());
        out.addAll(this.scriptStore.getScripts());
        return out;
    }

    private List<Completer> emptyCompleter(String command) {
        return new ArrayList<Completer>();
    }

    private List<Completer> helpCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        ArrayList<Completer> params = new ArrayList<Completer>();
        params.add(new StringsCompleter(this::registryNames));
        params.add(NullCompleter.INSTANCE);
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter(params, this::commandOptions, 1)));
        return completers;
    }

    private List<Completer> exitCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter((Completer)NullCompleter.INSTANCE, this::commandOptions, 1)));
        return completers;
    }

    private int registryId(String command) {
        for (int i = 0; i < this.commandRegistries.length; ++i) {
            if (!this.commandRegistries[i].hasCommand(command)) continue;
            return i;
        }
        return -1;
    }

    private static class ScriptStore {
        ConsoleEngine engine;
        Map<String, Boolean> scripts = new HashMap<String, Boolean>();

        public ScriptStore() {
        }

        public ScriptStore(ConsoleEngine engine) {
            this.engine = engine;
        }

        public void refresh() {
            if (this.engine != null) {
                this.scripts = this.engine.scripts();
            }
        }

        public boolean hasScript(String name) {
            return this.scripts.containsKey(name);
        }

        public boolean isConsoleScript(String name) {
            return this.scripts.getOrDefault(name, false);
        }

        public Set<String> getScripts() {
            return this.scripts.keySet();
        }
    }

    private class NamesAndValues {
        private final String[] delims = new String[]{"&", "\\|", "\\{", "\\}", "\\[", "\\]", "\\(", "\\)", "\\+", "-", "\\*", "=", ">", "<", "~", "!", ":", ",", ";"};
        private Path fileNames;
        private final Map<String, List<String>> names = new HashMap<String, List<String>>();
        private List<String> namedPipes;

        public NamesAndValues() {
            this(null);
        }

        public NamesAndValues(ConfigurationPath configPath) {
            this.names.put("fields", new ArrayList());
            this.names.put("values", new ArrayList());
            this.names.put("quoted", new ArrayList());
            this.names.put("options", new ArrayList());
            ConsoleEngine consoleEngine = SystemRegistryImpl.this.consoleEngine();
            if (configPath != null && consoleEngine != null) {
                try {
                    this.fileNames = configPath.getUserConfig("pipeline-names.json", true);
                    Map temp = (Map)consoleEngine.slurp(this.fileNames);
                    for (Map.Entry entry : temp.entrySet()) {
                        this.names.get(entry.getKey()).addAll((Collection)entry.getValue());
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }

        public boolean isPipe(String arg) {
            Map<Object, Object> customPipes = SystemRegistryImpl.this.consoleEngine() != null ? SystemRegistryImpl.this.consoleEngine().getPipes() : new HashMap();
            return this.isPipe(arg, customPipes.keySet());
        }

        public boolean hasPipes(Collection<String> args) {
            Map<Object, Object> customPipes = SystemRegistryImpl.this.consoleEngine() != null ? SystemRegistryImpl.this.consoleEngine().getPipes() : new HashMap();
            for (String a : args) {
                if (!this.isPipe(a, customPipes.keySet()) && !a.contains(">") && !a.contains(">>")) continue;
                return true;
            }
            return false;
        }

        private boolean isPipe(String arg, Set<String> pipes) {
            return SystemRegistryImpl.this.pipeName.containsValue(arg) || pipes.contains(arg);
        }

        public void extractNames(String line) {
            if (SystemRegistryImpl.this.parser.getCommand(line).equals("pipe")) {
                return;
            }
            ArgsParser ap = new ArgsParser(SystemRegistryImpl.this.parser);
            ap.parse(line);
            List<String> args = ap.args();
            int pipeId = 0;
            for (String a : args) {
                if (this.isPipe(a)) break;
                ++pipeId;
            }
            if (pipeId < args.size()) {
                StringBuilder sb = new StringBuilder();
                int redirectPipe = -1;
                for (int i = pipeId + 1; i < args.size(); ++i) {
                    String[] arg = args.get(i);
                    if (!(this.isPipe((String)arg) || this.namedPipes().contains(arg) || arg.matches("\\d+") || redirectPipe == i - 1)) {
                        if (arg.equals(">") || arg.equals(">>")) {
                            redirectPipe = i;
                            continue;
                        }
                        if (arg.matches("\\w+(\\(\\))?")) {
                            this.addValues((String)arg);
                            continue;
                        }
                        if (arg.matches("--\\w+(=.*|)$") && arg.length() > 4) {
                            int idx = arg.indexOf(61);
                            if (idx > 0) {
                                if (idx > 4) {
                                    this.addOptions(arg.substring(2, idx));
                                }
                                sb.append(arg.substring(idx + 1));
                                sb.append(" ");
                                continue;
                            }
                            if (idx != -1) continue;
                            this.addOptions(arg.substring(2));
                            continue;
                        }
                        sb.append((String)arg);
                        sb.append(" ");
                        continue;
                    }
                    redirectPipe = -1;
                }
                if (sb.length() > 0) {
                    String[] words;
                    String rest = sb.toString();
                    for (String d : this.delims) {
                        rest = rest.replaceAll(d, " ");
                    }
                    for (String w : words = rest.split("\\s+")) {
                        if (w.length() < 3 || w.matches("\\d+")) continue;
                        if (this.isQuoted(w)) {
                            this.addQuoted(w.substring(1, w.length() - 1));
                            continue;
                        }
                        if (w.contains(".")) {
                            for (String f : w.split("\\.")) {
                                if (f.matches("\\d+") || !f.matches("\\w+")) continue;
                                this.addFields(f);
                            }
                            continue;
                        }
                        if (!w.matches("\\w+")) continue;
                        this.addValues(w);
                    }
                }
            }
            this.namedPipes = null;
        }

        public String encloseBy(String param) {
            boolean quoted;
            boolean bl = quoted = !param.isEmpty() && (param.startsWith("\"") || param.startsWith("'") || param.startsWith("/"));
            if (quoted && param.length() > 1) {
                quoted = !param.endsWith(Character.toString(param.charAt(0)));
            }
            return quoted ? Character.toString(param.charAt(0)) : "";
        }

        private boolean isQuoted(String word) {
            return word.length() > 1 && (word.startsWith("\"") && word.endsWith("\"") || word.startsWith("'") && word.endsWith("'") || word.startsWith("/") && word.endsWith("/"));
        }

        public int indexOfLastDelim(String word) {
            int out = -1;
            for (String d : this.delims) {
                int x = word.lastIndexOf(d.replace("\\", ""));
                if (x <= out) continue;
                out = x;
            }
            return out;
        }

        private void addFields(String field) {
            this.add("fields", field);
        }

        private void addValues(String arg) {
            this.add("values", arg);
        }

        private void addQuoted(String arg) {
            this.add("quoted", arg);
        }

        private void addOptions(String arg) {
            this.add("options", arg);
        }

        private void add(String where, String value) {
            if (value.length() < 3) {
                return;
            }
            this.names.get(where).remove(value);
            this.names.get(where).add(0, value);
        }

        public List<String> namedPipes() {
            if (this.namedPipes == null) {
                this.namedPipes = SystemRegistryImpl.this.consoleId != null ? SystemRegistryImpl.this.consoleEngine().getNamedPipes() : new ArrayList();
            }
            return this.namedPipes;
        }

        public List<String> values() {
            return this.names.get("values");
        }

        public List<String> fields() {
            return this.names.get("fields");
        }

        public List<String> quoted() {
            return this.names.get("quoted");
        }

        public List<String> options() {
            return this.names.get("options");
        }

        private Set<String> fieldsAndValues() {
            HashSet<String> out = new HashSet<String>();
            out.addAll(this.fields());
            out.addAll(this.values());
            return out;
        }

        private void truncate(String where, int maxSize) {
            if (this.names.get(where).size() > maxSize) {
                this.names.put(where, this.names.get(where).subList(0, maxSize));
            }
        }

        public void save() {
            ConsoleEngine consoleEngine = SystemRegistryImpl.this.consoleEngine();
            if (consoleEngine != null && this.fileNames != null) {
                int maxSize = consoleEngine.consoleOption("maxValueNames", 100);
                this.truncate("fields", maxSize);
                this.truncate("values", maxSize);
                this.truncate("quoted", maxSize);
                this.truncate("options", maxSize);
                consoleEngine.persist(this.fileNames, this.names);
            }
        }
    }

    private static class CommandOutputStream {
        private final PrintStream origOut = System.out;
        private final PrintStream origErr = System.err;
        private final Terminal origTerminal;
        private OutputStream outputStream;
        private Terminal terminal;
        private String output;
        private CommandRegistry.CommandSession commandSession;
        private boolean redirecting = false;

        public CommandOutputStream(Terminal terminal) {
            this.origTerminal = terminal;
            this.terminal = terminal;
            PrintStream ps = new PrintStream(terminal.output());
            this.commandSession = new CommandRegistry.CommandSession(terminal, terminal.input(), ps, ps);
        }

        public void redirect() {
            this.outputStream = new ByteArrayOutputStream();
        }

        public void redirect(File file, boolean append) throws IOException {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    new File(file.getParent()).mkdirs();
                    file.createNewFile();
                }
            }
            this.outputStream = new FileOutputStream(file, append);
        }

        public void open(boolean redirectColor) throws IOException {
            if (this.redirecting || this.outputStream == null) {
                return;
            }
            this.output = null;
            PrintStream out = new PrintStream(this.outputStream);
            System.setOut(out);
            System.setErr(out);
            this.commandSession = new CommandRegistry.CommandSession(this.origTerminal, this.origTerminal.input(), out, out);
            this.redirecting = true;
        }

        public void close() {
            if (!this.redirecting) {
                return;
            }
            try {
                this.origTerminal.flush();
                if (this.outputStream instanceof ByteArrayOutputStream) {
                    this.output = this.outputStream.toString();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.reset();
        }

        public void resetOutput() {
            this.output = null;
        }

        private void reset() {
            this.outputStream = null;
            System.setOut(this.origOut);
            System.setErr(this.origErr);
            this.terminal = this.origTerminal;
            PrintStream ps = new PrintStream(this.terminal.output());
            this.commandSession = new CommandRegistry.CommandSession(this.terminal, this.terminal.input(), ps, ps);
            this.redirecting = false;
        }

        public CommandRegistry.CommandSession getCommandSession() {
            return this.commandSession;
        }

        public String getOutput() {
            return this.output;
        }

        public boolean isRedirecting() {
            return this.redirecting;
        }

        public boolean isByteOutputStream() {
            return this.outputStream instanceof ByteArrayOutputStream;
        }
    }

    public static enum Pipe {
        FLIP,
        NAMED,
        AND,
        OR;

    }

    private static class PipelineCompleter
    implements Completer {
        private final NamesAndValues names;
        private final Supplier<Path> workDir;
        private final Map<Pipe, String> pipeName;

        public PipelineCompleter(Supplier<Path> workDir, Map<Pipe, String> pipeName, NamesAndValues names) {
            this.workDir = workDir;
            this.pipeName = pipeName;
            this.names = names;
        }

        public Completer doCompleter() {
            ArgumentCompleter out = new ArgumentCompleter(this);
            out.setStrict(false);
            return out;
        }

        @Override
        public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
            assert (commandLine != null);
            assert (candidates != null);
            ArgsParser ap = new ArgsParser(reader.getParser());
            ap.parse(commandLine.line().substring(0, commandLine.cursor()));
            List<String> args = ap.args();
            if (args.size() < 2 || !this.names.hasPipes(args)) {
                return;
            }
            boolean enclosed = ap.isEnclosed(args.get(args.size() - 1));
            String pWord = commandLine.words().get(commandLine.wordIndex() - 1);
            if (enclosed && pWord.equals(this.pipeName.get((Object)Pipe.NAMED))) {
                for (String name : this.names.namedPipes()) {
                    candidates.add(new Candidate(name, name, null, null, null, null, true));
                }
            } else if (enclosed && pWord.equals(">") || pWord.equals(">>")) {
                Completers.FilesCompleter c = new Completers.FilesCompleter(this.workDir);
                c.complete(reader, commandLine, candidates);
            } else {
                String buffer;
                String param = buffer = commandLine.word().substring(0, commandLine.wordCursor());
                String curBuf = "";
                int lastDelim = this.names.indexOfLastDelim(buffer);
                if (lastDelim > -1) {
                    param = buffer.substring(lastDelim + 1);
                    curBuf = buffer.substring(0, lastDelim + 1);
                }
                if (curBuf.startsWith("--") && !curBuf.contains("=")) {
                    this.doCandidates(candidates, this.names.options(), curBuf, "", param);
                } else if (param.isEmpty()) {
                    this.doCandidates(candidates, this.names.fieldsAndValues(), curBuf, "", "");
                } else if (param.contains(".")) {
                    int point = buffer.lastIndexOf(".");
                    param = buffer.substring(point + 1);
                    curBuf = buffer.substring(0, point + 1);
                    this.doCandidates(candidates, this.names.fields(), curBuf, "", param);
                } else if (this.names.encloseBy(param).length() == 1) {
                    String postFix = this.names.encloseBy(param);
                    param = buffer.substring(++lastDelim + 1);
                    curBuf = buffer.substring(0, lastDelim + 1);
                    this.doCandidates(candidates, this.names.quoted(), curBuf, postFix, param);
                } else {
                    this.doCandidates(candidates, this.names.fieldsAndValues(), curBuf, "", param);
                }
            }
        }

        private void doCandidates(List<Candidate> candidates, Collection<String> fields, String curBuf, String postFix, String hint) {
            if (fields == null) {
                return;
            }
            for (String s : fields) {
                if (s == null || !s.startsWith(hint)) continue;
                candidates.add(new Candidate(AttributedString.stripAnsi(curBuf + s + postFix), s, null, null, null, null, false));
            }
        }
    }

    private static class ArgsParser {
        private int round = 0;
        private int curly = 0;
        private int square = 0;
        private boolean quoted;
        private boolean doubleQuoted;
        private String line;
        private String command = "";
        private String variable = "";
        private List<String> args;
        private final Parser parser;

        public ArgsParser(Parser parser) {
            this.parser = parser;
        }

        private void reset() {
            this.round = 0;
            this.curly = 0;
            this.square = 0;
            this.quoted = false;
            this.doubleQuoted = false;
        }

        private void next(String arg) {
            char prevChar = ' ';
            for (int i = 0; i < arg.length(); ++i) {
                char c = arg.charAt(i);
                if (!this.parser.isEscapeChar(prevChar)) {
                    if (!this.quoted && !this.doubleQuoted) {
                        if (c == '(') {
                            ++this.round;
                        } else if (c == ')') {
                            --this.round;
                        } else if (c == '{') {
                            ++this.curly;
                        } else if (c == '}') {
                            --this.curly;
                        } else if (c == '[') {
                            ++this.square;
                        } else if (c == ']') {
                            --this.square;
                        } else if (c == '\"') {
                            this.doubleQuoted = true;
                        } else if (c == '\'') {
                            this.quoted = true;
                        }
                    } else if (this.quoted && c == '\'') {
                        this.quoted = false;
                    } else if (this.doubleQuoted && c == '\"') {
                        this.doubleQuoted = false;
                    }
                }
                prevChar = c;
            }
        }

        private boolean isEnclosed() {
            return this.round == 0 && this.curly == 0 && this.square == 0 && !this.quoted && !this.doubleQuoted;
        }

        public boolean isEnclosed(String arg) {
            this.reset();
            this.next(arg);
            return this.isEnclosed();
        }

        private void enclosedArgs(List<String> words) {
            this.args = new ArrayList<String>();
            this.reset();
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for (String a : words) {
                this.next(a);
                if (!first) {
                    sb.append(" ");
                }
                if (this.isEnclosed()) {
                    sb.append(a);
                    this.args.add(sb.toString());
                    sb = new StringBuilder();
                    first = true;
                    continue;
                }
                sb.append(a);
                first = false;
            }
            if (!first) {
                this.args.add(sb.toString());
            }
        }

        public void parse(String line) {
            this.line = line;
            ParsedLine pl = this.parser.parse(line, 0, Parser.ParseContext.SPLIT_LINE);
            this.enclosedArgs(pl.words());
            if (!this.args.isEmpty()) {
                this.command = this.parser.getCommand(this.args.get(0));
                if (!this.parser.validCommandName(this.command)) {
                    this.command = "";
                }
                this.variable = this.parser.getVariable(this.args.get(0));
            } else {
                this.line = "";
            }
        }

        public String line() {
            return this.line;
        }

        public String command() {
            return ConsoleEngine.plainCommand(this.command);
        }

        public String rawCommand() {
            return this.command;
        }

        public String variable() {
            return this.variable;
        }

        public List<String> args() {
            return this.args;
        }

        private int closingQuote(String arg) {
            int out = -1;
            char prevChar = ' ';
            for (int i = 1; i < arg.length(); ++i) {
                char c = arg.charAt(i);
                if (!this.parser.isEscapeChar(prevChar) && c == arg.charAt(0)) {
                    out = i;
                    break;
                }
                prevChar = c;
            }
            return out;
        }

        private String unquote(String arg) {
            if ((arg.length() > 1 && arg.startsWith("\"") && arg.endsWith("\"") || arg.startsWith("'") && arg.endsWith("'")) && this.closingQuote(arg) == arg.length() - 1) {
                return arg.substring(1, arg.length() - 1);
            }
            return arg;
        }

        /*
         * Enabled aggressive block sorting
         */
        private String unescape(String arg) {
            if (arg == null || !this.parser.isEscapeChar('\\')) {
                return arg;
            }
            StringBuilder sb = new StringBuilder(arg.length());
            int i = 0;
            while (true) {
                block20: {
                    int ch;
                    block18: {
                        char nextChar;
                        block19: {
                            if (i >= arg.length()) {
                                return sb.toString();
                            }
                            ch = arg.charAt(i);
                            if (ch != 92) break block18;
                            char c = nextChar = i == arg.length() - 1 ? (char)'\\' : (char)arg.charAt(i + 1);
                            if (nextChar < 48 || nextChar > 55) break block19;
                            String code = "" + nextChar;
                            if (++i < arg.length() - 1 && arg.charAt(i + 1) >= '0' && arg.charAt(i + 1) <= '7') {
                                code = code + arg.charAt(i + 1);
                                if (++i < arg.length() - 1 && arg.charAt(i + 1) >= '0' && arg.charAt(i + 1) <= '7') {
                                    code = code + arg.charAt(i + 1);
                                    ++i;
                                }
                            }
                            sb.append((char)Integer.parseInt(code, 8));
                            break block20;
                        }
                        switch (nextChar) {
                            case '\\': {
                                ch = 92;
                                break;
                            }
                            case 'b': {
                                ch = 8;
                                break;
                            }
                            case 'f': {
                                ch = 12;
                                break;
                            }
                            case 'n': {
                                ch = 10;
                                break;
                            }
                            case 'r': {
                                ch = 13;
                                break;
                            }
                            case 't': {
                                ch = 9;
                                break;
                            }
                            case '\"': {
                                ch = 34;
                                break;
                            }
                            case '\'': {
                                ch = 39;
                                break;
                            }
                            case ' ': {
                                ch = 32;
                                break;
                            }
                            case 'u': {
                                if (i >= arg.length() - 5) {
                                    ch = 117;
                                    break;
                                }
                                int code = Integer.parseInt("" + arg.charAt(i + 2) + arg.charAt(i + 3) + arg.charAt(i + 4) + arg.charAt(i + 5), 16);
                                sb.append(Character.toChars(code));
                                i += 5;
                                break block20;
                            }
                        }
                        ++i;
                    }
                    sb.append((char)ch);
                }
                ++i;
            }
        }
    }

    protected static class CommandData {
        private final String rawLine;
        private String command;
        private String[] args;
        private final File file;
        private final boolean append;
        private final String variable;
        private String pipe;

        public CommandData(ArgsParser parser, boolean statement, String rawLine, String variable, File file, boolean append, String pipe) {
            this.rawLine = rawLine;
            this.variable = variable;
            this.file = file;
            this.append = append;
            this.pipe = pipe;
            this.args = new String[0];
            this.command = "";
            if (!statement) {
                parser.parse(rawLine);
                this.command = parser.command();
                if (parser.args().size() > 1) {
                    this.args = new String[parser.args().size() - 1];
                    for (int i = 1; i < parser.args().size(); ++i) {
                        this.args[i - 1] = parser.unescape(parser.unquote(parser.args().get(i)));
                    }
                }
            }
        }

        public void setPipe(String pipe) {
            this.pipe = pipe;
        }

        public File file() {
            return this.file;
        }

        public boolean append() {
            return this.append;
        }

        public String variable() {
            return this.variable;
        }

        public String command() {
            return this.command;
        }

        public String[] args() {
            return this.args;
        }

        public String rawLine() {
            return this.rawLine;
        }

        public String pipe() {
            return this.pipe;
        }

        public String toString() {
            return "[rawLine:" + this.rawLine + ", command:" + this.command + ", args:" + Arrays.asList(this.args) + ", variable:" + this.variable + ", file:" + this.file + ", append:" + this.append + ", pipe:" + this.pipe + "]";
        }
    }

    public static class UnknownCommandException
    extends Exception {
        public UnknownCommandException(String message) {
            super(message);
        }
    }
}

