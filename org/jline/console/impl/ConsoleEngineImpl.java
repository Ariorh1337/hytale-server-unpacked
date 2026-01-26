/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console.impl;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.builtins.Completers;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.Options;
import org.jline.builtins.Styles;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.console.ConsoleEngine;
import org.jline.console.Printer;
import org.jline.console.ScriptEngine;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.JlineCommandRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EOFError;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.SyntaxError;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public class ConsoleEngineImpl
extends JlineCommandRegistry
implements ConsoleEngine {
    private static final String VAR_CONSOLE_OPTIONS = "CONSOLE_OPTIONS";
    private static final String VAR_PATH = "PATH";
    private static final String[] OPTION_HELP = new String[]{"-?", "--help"};
    private static final String OPTION_VERBOSE = "-v";
    private static final String SLURP_FORMAT_TEXT = "TEXT";
    private static final String END_HELP = "END_HELP";
    private static final int HELP_MAX_SIZE = 30;
    protected final ScriptEngine engine;
    private Exception exception;
    private SystemRegistry systemRegistry;
    private String scriptExtension = "jline";
    private final Supplier<Path> workDir;
    private final Map<String, String> aliases = new HashMap<String, String>();
    private final Map<String, List<String>> pipes = new HashMap<String, List<String>>();
    private Path aliasFile;
    private LineReader reader;
    private boolean executing = false;
    private final Printer printer;

    public ConsoleEngineImpl(ScriptEngine engine, Printer printer, Supplier<Path> workDir, ConfigurationPath configPath) throws IOException {
        this(null, engine, printer, workDir, configPath);
    }

    public ConsoleEngineImpl(Set<Command> commands, ScriptEngine engine, Printer printer, Supplier<Path> workDir, ConfigurationPath configPath) throws IOException {
        this.engine = engine;
        this.workDir = workDir;
        this.printer = printer;
        HashMap<Command, String> commandName = new HashMap<Command, String>();
        HashMap<Command, CommandMethods> commandExecute = new HashMap<Command, CommandMethods>();
        HashSet<Command> cmds = commands == null ? new HashSet<Command>(EnumSet.allOf(Command.class)) : new HashSet<Command>(commands);
        for (Command c : cmds) {
            commandName.put(c, c.name().toLowerCase());
        }
        commandExecute.put(Command.DEL, new CommandMethods(this::del, this::variableCompleter));
        commandExecute.put(Command.SHOW, new CommandMethods(this::show, this::variableCompleter));
        commandExecute.put(Command.PRNT, new CommandMethods(this::prnt, this::prntCompleter));
        commandExecute.put(Command.SLURP, new CommandMethods(this::slurpcmd, this::slurpCompleter));
        commandExecute.put(Command.ALIAS, new CommandMethods(this::aliascmd, this::aliasCompleter));
        commandExecute.put(Command.UNALIAS, new CommandMethods(this::unalias, this::unaliasCompleter));
        commandExecute.put(Command.DOC, new CommandMethods(this::doc, this::docCompleter));
        commandExecute.put(Command.PIPE, new CommandMethods(this::pipe, this::defaultCompleter));
        this.aliasFile = configPath.getUserConfig("aliases.json");
        if (this.aliasFile == null) {
            this.aliasFile = configPath.getUserConfig("aliases.json", true);
            if (this.aliasFile == null) {
                Log.warn("Failed to write in user config path!");
                this.aliasFile = OSUtils.IS_WINDOWS ? Paths.get("NUL", new String[0]) : Paths.get("/dev/null", new String[0]);
            }
            this.persist(this.aliasFile, this.aliases);
        } else {
            this.aliases.putAll((Map)this.slurp(this.aliasFile));
        }
        this.registerCommands(commandName, commandExecute);
    }

    @Override
    public void setLineReader(LineReader reader) {
        this.reader = reader;
    }

    private Parser parser() {
        return this.reader.getParser();
    }

    private Terminal terminal() {
        return this.systemRegistry.terminal();
    }

    @Override
    public boolean isExecuting() {
        return this.executing;
    }

    @Override
    public void setSystemRegistry(SystemRegistry systemRegistry) {
        this.systemRegistry = systemRegistry;
    }

    @Override
    public void setScriptExtension(String extension) {
        this.scriptExtension = extension;
    }

    @Override
    public boolean hasAlias(String name) {
        return this.aliases.containsKey(name);
    }

    @Override
    public String getAlias(String name) {
        return this.aliases.getOrDefault(name, null);
    }

    @Override
    public Map<String, List<String>> getPipes() {
        return this.pipes;
    }

    @Override
    public List<String> getNamedPipes() {
        ArrayList<String> out = new ArrayList<String>();
        ArrayList<String> opers = new ArrayList<String>();
        for (String string : this.pipes.keySet()) {
            if (string.matches("[a-zA-Z0-9]+")) {
                out.add(string);
                continue;
            }
            opers.add(string);
        }
        opers.addAll(this.systemRegistry.getPipeNames());
        for (Map.Entry entry : this.aliases.entrySet()) {
            if (!opers.contains(((String)entry.getValue()).split(" ")[0])) continue;
            out.add((String)entry.getKey());
        }
        return out;
    }

    @Override
    public List<Completer> scriptCompleters() {
        ArrayList<Completer> out = new ArrayList<Completer>();
        out.add(new ArgumentCompleter(new StringsCompleter(this::scriptNames), new Completers.OptionCompleter((Completer)NullCompleter.INSTANCE, this::commandOptions, 1)));
        out.add(new ArgumentCompleter(new StringsCompleter(this::commandAliasNames), NullCompleter.INSTANCE));
        return out;
    }

    private Set<String> commandAliasNames() {
        Set opers = this.pipes.keySet().stream().filter(p -> !p.matches("\\w+")).collect(Collectors.toSet());
        opers.addAll(this.systemRegistry.getPipeNames());
        return this.aliases.entrySet().stream().filter(e -> !opers.contains(((String)e.getValue()).split(" ")[0])).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    private Set<String> scriptNames() {
        return this.scripts().keySet();
    }

    @Override
    public Map<String, Boolean> scripts() {
        HashMap<String, Boolean> out = new HashMap<String, Boolean>();
        try {
            ArrayList scripts = new ArrayList();
            if (this.engine.hasVariable(VAR_PATH)) {
                ArrayList<String> dirs = new ArrayList<String>();
                for (String file : (List)this.engine.get(VAR_PATH)) {
                    file = file.startsWith("~") ? file.replace("~", System.getProperty("user.home")) : file;
                    File dir = new File(file);
                    if (!dir.exists() || !dir.isDirectory()) continue;
                    dirs.add(file);
                }
                for (String pp : dirs) {
                    for (String e : this.scriptExtensions()) {
                        String regex = pp + "/*." + e;
                        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + regex);
                        Stream<Path> pathStream = Files.walk(new File(regex).getParentFile().toPath(), new FileVisitOption[0]);
                        try {
                            pathStream.filter(pathMatcher::matches).forEach(scripts::add);
                        }
                        finally {
                            if (pathStream == null) continue;
                            pathStream.close();
                        }
                    }
                }
            }
            for (Path p : scripts) {
                String name = p.getFileName().toString();
                int idx = name.lastIndexOf(".");
                out.put(name.substring(0, idx), name.substring(idx + 1).equals(this.scriptExtension));
            }
        }
        catch (NoSuchFileException e) {
            this.error("Failed reading PATH. No file found: " + e.getMessage());
        }
        catch (InvalidPathException e) {
            this.error("Failed reading PATH. Invalid path:");
            this.error(e.toString());
        }
        catch (Exception e) {
            this.error("Failed reading PATH:");
            this.trace(e);
            this.engine.put("exception", e);
        }
        return out;
    }

    @Override
    public Object[] expandParameters(String[] args) throws Exception {
        Object[] out = new Object[args.length];
        String regexPath = "(.*)\\$\\{(.*?)}(/.*)";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].matches(regexPath)) {
                Matcher matcher = Pattern.compile(regexPath).matcher(args[i]);
                if (matcher.find()) {
                    out[i] = matcher.group(1) + this.engine.get(matcher.group(2)) + matcher.group(3);
                    continue;
                }
                throw new IllegalArgumentException();
            }
            if (args[i].startsWith("${")) {
                String expanded = this.expandName(args[i]);
                String statement = expanded.startsWith("$") ? args[i].substring(2, args[i].length() - 1) : expanded;
                out[i] = this.engine.execute(statement);
                continue;
            }
            out[i] = args[i].startsWith("$") ? this.engine.get(this.expandName(args[i])) : this.engine.deserialize(args[i]);
        }
        return out;
    }

    private String expandToList(String[] args) {
        return this.expandToList(Arrays.asList(args));
    }

    @Override
    public String expandToList(List<String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (String param : params) {
            if (!first) {
                sb.append(",");
            }
            if (param.equalsIgnoreCase("true") || param.equalsIgnoreCase("false") || param.equalsIgnoreCase("null")) {
                sb.append(param.toLowerCase());
            } else if (this.isNumber(param)) {
                sb.append(param);
            } else {
                sb.append(param.startsWith("$") ? param.substring(1) : this.quote(param));
            }
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    protected String expandName(String name) {
        String regexVar = "[a-zA-Z_]+[a-zA-Z0-9_-]*";
        String out = name;
        if (name.matches("^\\$" + regexVar)) {
            out = name.substring(1);
        } else if (name.matches("^\\$\\{" + regexVar + "}.*")) {
            Matcher matcher = Pattern.compile("^\\$\\{(" + regexVar + ")}(.*)").matcher(name);
            if (matcher.find()) {
                out = matcher.group(1) + matcher.group(2);
            } else {
                throw new IllegalArgumentException();
            }
        }
        return out;
    }

    private boolean isNumber(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isCodeBlock(String line) {
        return line.contains("\n") && line.trim().endsWith("}");
    }

    private boolean isCommandLine(String line) {
        String command = this.parser().getCommand(line);
        boolean out = false;
        if (command != null && command.startsWith(":")) {
            if (this.hasAlias(command = command.substring(1))) {
                command = this.getAlias(command);
            }
            if (this.systemRegistry.hasCommand(command)) {
                out = true;
            } else {
                ScriptFile sf = new ScriptFile(command, "", new String[0]);
                if (sf.isScript()) {
                    out = true;
                }
            }
        }
        return out;
    }

    private String quote(String var) {
        if (var.startsWith("\"") && var.endsWith("\"") || var.startsWith("'") && var.endsWith("'")) {
            return var;
        }
        if (var.contains("\\\"")) {
            return "'" + var + "'";
        }
        return "\"" + var + "\"";
    }

    private List<String> scriptExtensions() {
        ArrayList<String> extensions = new ArrayList<String>(this.engine.getExtensions());
        extensions.add(this.scriptExtension);
        return extensions;
    }

    @Override
    public Object execute(Path script, String cmdLine, String[] args) throws Exception {
        ScriptFile file = new ScriptFile(script, cmdLine, args);
        file.execute();
        return file.getResult();
    }

    @Override
    public String expandCommandLine(String line) {
        String out;
        if (this.isCommandLine(line)) {
            StringBuilder sb = new StringBuilder();
            List<String> ws = this.parser().parse(line, 0, Parser.ParseContext.COMPLETE).words();
            int idx = ws.get(0).lastIndexOf(":");
            if (idx > 0) {
                sb.append(ws.get(0).substring(0, idx));
            }
            String[] argv = new String[ws.size()];
            for (int i = 1; i < ws.size(); ++i) {
                argv[i] = ws.get(i);
                if (argv[i].startsWith("${")) {
                    Matcher argvMatcher = Pattern.compile("\\$\\{(.*)}").matcher(argv[i]);
                    if (!argvMatcher.find()) continue;
                    argv[i] = argv[i].replace(argv[i], argvMatcher.group(1));
                    continue;
                }
                argv[i] = argv[i].startsWith("$") ? argv[i].substring(1) : this.quote(argv[i]);
            }
            String cmd = this.hasAlias(ws.get(0).substring(idx + 1)) ? this.getAlias(ws.get(0).substring(idx + 1)) : ws.get(0).substring(idx + 1);
            sb.append(SystemRegistry.class.getCanonicalName()).append(".get().invoke('").append(cmd).append("'");
            for (int i = 1; i < argv.length; ++i) {
                sb.append(", ");
                sb.append(argv[i]);
            }
            sb.append(")");
            out = sb.toString();
        } else {
            out = line;
        }
        return out;
    }

    @Override
    public Object execute(String cmd, String line, String[] args) throws Exception {
        if (line.trim().startsWith("#")) {
            return null;
        }
        Object out = null;
        ScriptFile file = new ScriptFile(cmd, line, args);
        if (file.execute()) {
            out = file.getResult();
        } else {
            if (this.isCodeBlock(line = line.trim())) {
                StringBuilder sb = new StringBuilder();
                for (String s : line.split("\\r?\\n")) {
                    sb.append(this.expandCommandLine(s));
                    sb.append("\n");
                }
                line = sb.toString();
            }
            if (this.engine.hasVariable(line)) {
                out = this.engine.get(line);
            } else if (this.parser().getVariable(line) == null) {
                out = this.engine.execute(line);
                this.engine.put("_", out);
            } else {
                this.engine.execute(line);
            }
        }
        return out;
    }

    @Override
    public void purge() {
        this.engine.del("_*");
    }

    @Override
    public void putVariable(String name, Object value) {
        this.engine.put(name, value);
    }

    @Override
    public Object getVariable(String name) {
        if (!this.engine.hasVariable(name)) {
            throw new IllegalArgumentException("Variable " + name + " does not exists!");
        }
        return this.engine.get(name);
    }

    @Override
    public boolean hasVariable(String name) {
        return this.engine.hasVariable(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean executeWidget(Object function) {
        this.engine.put("_reader", this.reader);
        this.engine.put("_widgetFunction", function);
        try {
            if (this.engine.getEngineName().equals("GroovyEngine")) {
                this.engine.execute("def _buffer() {_reader.getBuffer()}");
                this.engine.execute("def _widget(w) {_reader.callWidget(w)}");
            }
            this.engine.execute("_widgetFunction()");
        }
        catch (Exception e) {
            this.trace(e);
            boolean bl = false;
            return bl;
        }
        finally {
            this.purge();
        }
        return true;
    }

    private Map<String, Object> consoleOptions() {
        return this.engine.hasVariable(VAR_CONSOLE_OPTIONS) ? (Map)this.engine.get(VAR_CONSOLE_OPTIONS) : new HashMap<String, Object>();
    }

    @Override
    public <T> T consoleOption(String option, T defval) {
        Object out = defval;
        try {
            out = this.consoleOptions().getOrDefault(option, defval);
        }
        catch (Exception e) {
            this.trace(new Exception("Bad CONSOLE_OPTION value: " + e.getMessage()));
        }
        return out;
    }

    @Override
    public void setConsoleOption(String name, Object value) {
        this.consoleOptions().put(name, value);
    }

    private boolean consoleOption(String option) {
        boolean out = false;
        try {
            out = this.consoleOptions().containsKey(option);
        }
        catch (Exception e) {
            this.trace(new Exception("Bad CONSOLE_OPTION value: " + e.getMessage()));
        }
        return out;
    }

    @Override
    public ConsoleEngine.ExecutionResult postProcess(String line, Object result, String output) {
        ConsoleEngine.ExecutionResult out;
        String[] _output = output != null && !output.trim().isEmpty() && !this.consoleOption("no-splittedOutput") ? output.split("\\r?\\n") : output;
        String consoleVar = this.parser().getVariable(line);
        if (consoleVar != null && result != null) {
            this.engine.put("output", _output);
        }
        if (this.systemRegistry.hasCommand(this.parser().getCommand(line))) {
            out = this.postProcess(line, consoleVar != null && result == null ? _output : result);
        } else {
            String[] _result = result == null ? _output : result;
            int status = this.saveResult(consoleVar, _result);
            out = new ConsoleEngine.ExecutionResult(status, consoleVar != null && !consoleVar.startsWith("_") ? null : _result);
        }
        return out;
    }

    private ConsoleEngine.ExecutionResult postProcess(String line, Object result) {
        int status = 0;
        Object out = result instanceof String && ((String)result).trim().isEmpty() ? null : result;
        String consoleVar = this.parser().getVariable(line);
        if (consoleVar != null) {
            status = this.saveResult(consoleVar, result);
            out = null;
        } else if (!this.parser().getCommand(line).equals("show")) {
            status = result != null ? this.saveResult("_", result) : 1;
        }
        return new ConsoleEngine.ExecutionResult(status, out);
    }

    @Override
    public ConsoleEngine.ExecutionResult postProcess(Object result) {
        return new ConsoleEngine.ExecutionResult(this.saveResult(null, result), result);
    }

    private int saveResult(String var, Object result) {
        int out;
        try {
            this.engine.put("_executionResult", result);
            if (var != null) {
                if (var.contains(".") || var.contains("[")) {
                    this.engine.execute(var + " = _executionResult");
                } else {
                    this.engine.put(var, result);
                }
            }
            out = (Integer)this.engine.execute("_executionResult ? 0 : 1");
        }
        catch (Exception e) {
            this.trace(e);
            out = 1;
        }
        return out;
    }

    @Override
    public Object invoke(CommandRegistry.CommandSession session, String command, Object ... args) throws Exception {
        this.exception = null;
        Object out = null;
        if (this.hasCommand(command)) {
            out = this.getCommandMethods(command).execute().apply(new CommandInput(command, args, session));
        } else {
            String[] _args = new String[args.length];
            for (int i = 0; i < args.length; ++i) {
                if (!(args[i] instanceof String)) {
                    throw new IllegalArgumentException();
                }
                _args[i] = args[i].toString();
            }
            ScriptFile sf = new ScriptFile(command, "", _args);
            if (sf.execute()) {
                out = sf.getResult();
            }
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return out;
    }

    @Override
    public void trace(Object object) {
        Object toPrint = object;
        int level = this.consoleOption("trace", 0);
        HashMap<String, Object> options = new HashMap<String, Object>();
        if (level < 2) {
            options.put("exception", "message");
        }
        if (level == 0) {
            if (!(object instanceof Throwable)) {
                toPrint = null;
            }
        } else if (level == 1) {
            if (object instanceof SystemRegistryImpl.CommandData) {
                toPrint = ((SystemRegistryImpl.CommandData)object).rawLine();
            }
        } else if (level > 1 && object instanceof SystemRegistryImpl.CommandData) {
            toPrint = object.toString();
        }
        this.printer.println(options, toPrint);
    }

    private void error(String message) {
        AttributedStringBuilder asb = new AttributedStringBuilder();
        asb.styled(Styles.prntStyle().resolve(".em"), (CharSequence)message);
        asb.println(this.terminal());
    }

    @Override
    public void println(Object object) {
        this.printer.println(object);
    }

    private Object show(CommandInput input) {
        String[] usage = new String[]{"show -  list console variables", "Usage: show [VARIABLE]", "  -? --help                       Displays command help"};
        try {
            this.parseOptions(usage, input.args());
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("maxDepth", 0);
            this.printer.println(options, this.engine.find(input.args().length > 0 ? input.args()[0] : null));
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private Object del(CommandInput input) {
        String[] usage = new String[]{"del -  delete console variables, methods, classes and imports", "Usage: del [var1] ...", "  -? --help                       Displays command help"};
        try {
            this.parseOptions(usage, input.args());
            this.engine.del(input.args());
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private Object prnt(CommandInput input) {
        Exception result = this.printer.prntCommand(input);
        if (result != null) {
            this.exception = result;
        }
        return null;
    }

    private Object slurpcmd(CommandInput input) {
        Object out;
        block7: {
            String[] usage = new String[]{"slurp -  slurp file or string variable context to object", "Usage: slurp [OPTIONS] file|variable", "  -? --help                       Displays command help", "  -e --encoding=ENCODING          Encoding (default UTF-8)", "  -f --format=FORMAT              Serialization format"};
            out = null;
            try {
                Options opt = this.parseOptions(usage, input.xargs());
                if (opt.args().isEmpty()) break block7;
                Object _arg = opt.argObjects().get(0);
                if (!(_arg instanceof String)) {
                    throw new IllegalArgumentException("Invalid parameter type: " + _arg.getClass().getSimpleName());
                }
                String arg = (String)_arg;
                Charset encoding = opt.isSet("encoding") ? Charset.forName(opt.get("encoding")) : StandardCharsets.UTF_8;
                String format = opt.isSet("format") ? opt.get("format") : this.engine.getSerializationFormats().get(0);
                try {
                    Path path = Paths.get(arg, new String[0]);
                    if (Files.exists(path, new LinkOption[0])) {
                        out = !format.equals(SLURP_FORMAT_TEXT) ? this.slurp(path, encoding, format) : Files.readAllLines(Paths.get(arg, new String[0]), encoding);
                        break block7;
                    }
                    if (!format.equals(SLURP_FORMAT_TEXT)) {
                        out = this.engine.deserialize(arg, format);
                        break block7;
                    }
                    out = arg.split("\n");
                }
                catch (Exception e) {
                    out = this.engine.deserialize(arg, format);
                }
            }
            catch (Exception e) {
                this.exception = e;
            }
        }
        return out;
    }

    @Override
    public void persist(Path file, Object object) {
        this.engine.persist(file, object);
    }

    @Override
    public Object slurp(Path file) throws IOException {
        return this.slurp(file, StandardCharsets.UTF_8, this.engine.getSerializationFormats().get(0));
    }

    private Object slurp(Path file, Charset encoding, String format) throws IOException {
        byte[] encoded = Files.readAllBytes(file);
        return this.engine.deserialize(new String(encoded, encoding), format);
    }

    private Object aliascmd(CommandInput input) {
        String[] usage = new String[]{"alias -  create command alias", "Usage: alias [ALIAS] [COMMANDLINE]", "  -? --help                       Displays command help"};
        Map<String, String> out = null;
        try {
            Options opt = this.parseOptions(usage, input.args());
            List<String> args = opt.args();
            if (args.isEmpty()) {
                out = this.aliases;
            } else if (args.size() == 1) {
                out = this.aliases.getOrDefault(args.get(0), null);
            } else {
                String alias = String.join((CharSequence)" ", args.subList(1, args.size()));
                for (int j = 0; j < 10; ++j) {
                    alias = alias.replaceAll("%" + j, "\\$" + j);
                    alias = alias.replaceAll("%\\{" + j + "}", "\\$\\{" + j + "\\}");
                    alias = alias.replaceAll("%\\{" + j + ":-", "\\$\\{" + j + ":-");
                }
                alias = alias.replaceAll("%@", "\\$@");
                alias = alias.replaceAll("%\\{@}", "\\${@}");
                this.aliases.put(args.get(0), alias);
                this.persist(this.aliasFile, this.aliases);
            }
        }
        catch (Exception e) {
            this.exception = e;
        }
        return out;
    }

    private Object unalias(CommandInput input) {
        String[] usage = new String[]{"unalias -  remove command alias", "Usage: unalias [ALIAS...]", "  -? --help                       Displays command help"};
        try {
            Options opt = this.parseOptions(usage, input.args());
            for (String a : opt.args()) {
                this.aliases.remove(a);
            }
            this.persist(this.aliasFile, this.aliases);
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private Object pipe(CommandInput input) {
        String[] usage = new String[]{"pipe -  create/delete pipe operator", "Usage: pipe [OPERATOR] [PREFIX] [POSTFIX]", "       pipe --list", "       pipe --delete [OPERATOR...]", "  -? --help                       Displays command help", "  -d --delete                     Delete pipe operators", "  -l --list                       List pipe operators"};
        try {
            Options opt = this.parseOptions(usage, input.args());
            HashMap<String, Object> options = new HashMap<String, Object>();
            if (opt.isSet("delete")) {
                if (opt.args().size() == 1 && opt.args().get(0).equals("*")) {
                    this.pipes.clear();
                } else {
                    for (String p : opt.args()) {
                        this.pipes.remove(p.trim());
                    }
                }
            } else if (opt.isSet("list") || opt.args().isEmpty()) {
                options.put("maxDepth", 0);
                this.printer.println(options, this.pipes);
            } else if (opt.args().size() != 3) {
                this.exception = new IllegalArgumentException("Bad number of arguments!");
            } else if (this.systemRegistry.getPipeNames().contains(opt.args().get(0))) {
                this.exception = new IllegalArgumentException("Reserved pipe operator");
            } else {
                ArrayList<String> fixes = new ArrayList<String>();
                fixes.add(opt.args().get(1));
                fixes.add(opt.args().get(2));
                this.pipes.put(opt.args().get(0), fixes);
            }
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private Object doc(CommandInput input) {
        String[] usage = new String[]{"doc -  open document on browser", "Usage: doc [OBJECT]", "  -? --help                       Displays command help"};
        try {
            String address;
            Map docs;
            this.parseOptions(usage, input.xargs());
            if (input.xargs().length == 0) {
                return null;
            }
            if (!Desktop.isDesktopSupported()) {
                throw new IllegalStateException("Desktop is not supported!");
            }
            try {
                docs = this.consoleOption("docs", null);
            }
            catch (Exception e) {
                IllegalStateException exception = new IllegalStateException("Bad documents configuration!");
                exception.addSuppressed(e);
                throw exception;
            }
            if (docs == null) {
                throw new IllegalStateException("No documents configuration!");
            }
            boolean done = false;
            Object arg = input.xargs()[0];
            if (arg instanceof String && (address = (String)docs.get(input.args()[0])) != null) {
                done = true;
                if (this.urlExists(address)) {
                    Desktop.getDesktop().browse(new URI(address));
                } else {
                    throw new IllegalArgumentException("Document not found: " + address);
                }
            }
            if (!done) {
                String name = arg instanceof String && ((String)arg).matches("([a-z]+\\.)+[A-Z][a-zA-Z]+") ? (String)arg : arg.getClass().getCanonicalName();
                name = name.replaceAll("\\.", "/") + ".html";
                Object doc = null;
                for (Map.Entry entry : docs.entrySet()) {
                    if (!name.matches((String)entry.getKey())) continue;
                    doc = entry.getValue();
                    break;
                }
                if (doc == null) {
                    throw new IllegalArgumentException("No document configuration for " + name);
                }
                String url = name;
                if (doc instanceof Collection) {
                    for (Object o : (Collection)doc) {
                        url = o + name;
                        if (!this.urlExists(url)) continue;
                        Desktop.getDesktop().browse(new URI(url));
                        done = true;
                    }
                } else {
                    url = doc + name;
                    if (this.urlExists(url)) {
                        Desktop.getDesktop().browse(new URI(url));
                        done = true;
                    }
                }
                if (!done) {
                    throw new IllegalArgumentException("Document not found: " + url);
                }
            }
        }
        catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    private boolean urlExists(String weburl) {
        try {
            URL url = URI.create(weburl).toURL();
            HttpURLConnection huc = (HttpURLConnection)url.openConnection();
            huc.setRequestMethod("HEAD");
            return huc.getResponseCode() == 200;
        }
        catch (Exception e) {
            return false;
        }
    }

    private List<Completer> slurpCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        List<Completers.OptDesc> optDescs = this.commandOptions(command);
        for (Completers.OptDesc o : optDescs) {
            if (o.shortOption() == null || !o.shortOption().equals("-f")) continue;
            ArrayList<String> formats = new ArrayList<String>(this.engine.getDeserializationFormats());
            formats.add(SLURP_FORMAT_TEXT);
            o.setValueCompleter(new StringsCompleter((Iterable<String>)formats));
            break;
        }
        AggregateCompleter argCompleter = new AggregateCompleter(new Completers.FilesCompleter(this.workDir), new VariableReferenceCompleter(this.engine));
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter(Arrays.asList(argCompleter, NullCompleter.INSTANCE), optDescs, 1)));
        return completers;
    }

    private List<Completer> variableCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new StringsCompleter(() -> this.engine.find().keySet()));
        return completers;
    }

    private List<Completer> prntCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter(Arrays.asList(new VariableReferenceCompleter(this.engine), NullCompleter.INSTANCE), this::commandOptions, 1)));
        return completers;
    }

    private List<Completer> aliasCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        ArrayList<Completer> params = new ArrayList<Completer>();
        params.add(new StringsCompleter(this.aliases::keySet));
        params.add(new AliasValueCompleter(this.aliases));
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter(params, this::commandOptions, 1)));
        return completers;
    }

    private List<Completer> unaliasCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        Completer[] completerArray = new Completer[2];
        completerArray[0] = NullCompleter.INSTANCE;
        completerArray[1] = new Completers.OptionCompleter((Completer)new StringsCompleter(this.aliases::keySet), this::commandOptions, 1);
        completers.add(new ArgumentCompleter(completerArray));
        return completers;
    }

    private List<String> docs() {
        ArrayList<String> out = new ArrayList<String>();
        Map docs = this.consoleOption("docs", null);
        if (docs == null) {
            return out;
        }
        for (String v : this.engine.find().keySet()) {
            out.add("$" + v);
        }
        if (!docs.isEmpty()) {
            for (String d : docs.keySet()) {
                if (!d.matches("\\w+")) continue;
                out.add(d);
            }
        }
        return out;
    }

    private List<Completer> docCompleter(String command) {
        ArrayList<Completer> completers = new ArrayList<Completer>();
        completers.add(new ArgumentCompleter(NullCompleter.INSTANCE, new Completers.OptionCompleter(Arrays.asList(new StringsCompleter(this::docs), NullCompleter.INSTANCE), this::commandOptions, 1)));
        return completers;
    }

    public static enum Command {
        SHOW,
        DEL,
        PRNT,
        ALIAS,
        PIPE,
        UNALIAS,
        DOC,
        SLURP;

    }

    private class ScriptFile {
        private Path script;
        private String extension = "";
        private String cmdLine;
        private String[] args;
        private boolean verbose;
        private Object result;

        public ScriptFile(String command, String cmdLine, String[] args) {
            this.cmdLine = cmdLine;
            try {
                block9: {
                    block8: {
                        if (ConsoleEngineImpl.this.parser().validCommandName(command)) break block8;
                        command = cmdLine.split("\\s+")[0];
                        this.extension = this.fileExtension(command);
                        if (!this.isScript()) break block9;
                        this.extension = "";
                        this.script = Paths.get(command, new String[0]);
                        if (Files.exists(this.script, new LinkOption[0])) {
                            this.scriptExtension(command);
                        }
                        break block9;
                    }
                    this.script = Paths.get(command, new String[0]);
                    if (Files.exists(this.script, new LinkOption[0])) {
                        this.scriptExtension(command);
                    } else if (ConsoleEngineImpl.this.engine.hasVariable(ConsoleEngineImpl.VAR_PATH)) {
                        boolean found = false;
                        for (String p : (List)ConsoleEngineImpl.this.engine.get(ConsoleEngineImpl.VAR_PATH)) {
                            for (String e : ConsoleEngineImpl.this.scriptExtensions()) {
                                String file = command + "." + e;
                                Path path = Paths.get(p, file);
                                if (!Files.exists(path, new LinkOption[0])) continue;
                                this.script = path;
                                this.extension = e;
                                found = true;
                                break;
                            }
                            if (!found) continue;
                            break;
                        }
                    }
                }
                this.doArgs(args);
            }
            catch (Exception e) {
                Log.trace("Not a script file: " + command);
            }
        }

        public ScriptFile(Path script, String cmdLine, String[] args) {
            if (!Files.exists(script, new LinkOption[0])) {
                throw new IllegalArgumentException("Script file not found!");
            }
            this.script = script;
            this.cmdLine = cmdLine;
            this.scriptExtension(script.getFileName().toString());
            this.doArgs(args);
        }

        private String fileExtension(String fileName) {
            return fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
        }

        private void scriptExtension(String command) {
            this.extension = this.fileExtension(this.script.getFileName().toString());
            if (!this.isEngineScript() && !this.isConsoleScript()) {
                throw new IllegalArgumentException("Command not found: " + command);
            }
        }

        private void doArgs(String[] args) {
            ArrayList<String> _args = new ArrayList<String>();
            if (this.isConsoleScript()) {
                _args.add(this.script.toAbsolutePath().toString());
            }
            for (String a : args) {
                if (this.isConsoleScript()) {
                    if (!a.equals(ConsoleEngineImpl.OPTION_VERBOSE)) {
                        _args.add(a);
                        continue;
                    }
                    this.verbose = true;
                    continue;
                }
                _args.add(a);
            }
            this.args = _args.toArray(new String[0]);
        }

        private boolean isEngineScript() {
            return ConsoleEngineImpl.this.engine.getExtensions().contains(this.extension);
        }

        private boolean isConsoleScript() {
            return ConsoleEngineImpl.this.scriptExtension.equals(this.extension);
        }

        private boolean isScript() {
            return ConsoleEngineImpl.this.engine.getExtensions().contains(this.extension) || ConsoleEngineImpl.this.scriptExtension.equals(this.extension);
        }

        public boolean execute() throws Exception {
            if (!this.isScript()) {
                return false;
            }
            this.result = null;
            if (Arrays.asList(this.args).contains(OPTION_HELP[0]) || Arrays.asList(this.args).contains(OPTION_HELP[1])) {
                try (BufferedReader br = Files.newBufferedReader(this.script);){
                    String l;
                    int size = 0;
                    StringBuilder usage = new StringBuilder();
                    boolean helpEnd = false;
                    boolean headComment = false;
                    while ((l = br.readLine()) != null) {
                        String line = l = l.replaceAll("\\s+$", "");
                        if (++size > 30 || line.endsWith(ConsoleEngineImpl.END_HELP)) {
                            helpEnd = line.endsWith(ConsoleEngineImpl.END_HELP);
                            break;
                        }
                        if (headComment || size < 3) {
                            String ltr = l.trim();
                            if (ltr.startsWith("*") || ltr.startsWith("#")) {
                                headComment = true;
                                line = ltr.length() > 1 ? ltr.substring(2) : "";
                            } else if (ltr.startsWith("/*") || ltr.startsWith("//")) {
                                headComment = true;
                                line = ltr.length() > 2 ? ltr.substring(3) : "";
                            }
                        }
                        usage.append(line).append('\n');
                    }
                    if (usage.length() > 0) {
                        usage.append("\n");
                        if (!helpEnd) {
                            usage.insert(0, "\n");
                        }
                        throw new Options.HelpException(usage.toString());
                    }
                    this.internalExecute();
                }
            } else {
                this.internalExecute();
            }
            return true;
        }

        private String expandParameterName(String parameter) {
            if (parameter.startsWith("$")) {
                return ConsoleEngineImpl.this.expandName(parameter);
            }
            if (ConsoleEngineImpl.this.isNumber(parameter)) {
                return parameter;
            }
            return ConsoleEngineImpl.this.quote(parameter);
        }

        private void internalExecute() throws Exception {
            if (this.isEngineScript()) {
                this.result = ConsoleEngineImpl.this.engine.execute(this.script, ConsoleEngineImpl.this.expandParameters(this.args));
            } else if (this.isConsoleScript()) {
                ConsoleEngineImpl.this.executing = true;
                boolean done = true;
                String line = "";
                try (BufferedReader br = Files.newBufferedReader(this.script);){
                    String l;
                    while ((l = br.readLine()) != null) {
                        if (l.trim().isEmpty() || l.trim().startsWith("#")) {
                            done = true;
                            continue;
                        }
                        try {
                            line = line + l;
                            ConsoleEngineImpl.this.parser().parse(line, line.length() + 1, Parser.ParseContext.ACCEPT_LINE);
                            done = true;
                            for (int i = 1; i < this.args.length; ++i) {
                                line = line.replaceAll("\\s\\$" + i + "\\b", " " + this.expandParameterName(this.args[i]) + " ");
                                line = line.replaceAll("\\$\\{" + i + "(|:-.*)}", this.expandParameterName(this.args[i]));
                            }
                            line = line.replaceAll("\\$\\{@}", ConsoleEngineImpl.this.expandToList(this.args));
                            line = line.replaceAll("\\$@", ConsoleEngineImpl.this.expandToList(this.args));
                            line = line.replaceAll("\\s\\$\\d\\b", "");
                            line = line.replaceAll("\\$\\{\\d+}", "");
                            Matcher matcher = Pattern.compile("\\$\\{\\d+:-(.*?)}").matcher(line);
                            if (matcher.find()) {
                                line = matcher.replaceAll(this.expandParameterName(matcher.group(1)));
                            }
                            if (this.verbose) {
                                AttributedStringBuilder asb = new AttributedStringBuilder();
                                asb.styled(Styles.prntStyle().resolve(".vs"), (CharSequence)line);
                                asb.toAttributedString().println(ConsoleEngineImpl.this.terminal());
                                ConsoleEngineImpl.this.terminal().flush();
                            }
                            ConsoleEngineImpl.this.println(ConsoleEngineImpl.this.systemRegistry.execute(line));
                            line = "";
                        }
                        catch (EOFError e) {
                            done = false;
                            line = line + "\n";
                        }
                        catch (SyntaxError e) {
                            throw e;
                        }
                        catch (EndOfFileException e) {
                            done = true;
                            this.result = ConsoleEngineImpl.this.engine.get("_return");
                            ConsoleEngineImpl.this.postProcess(this.cmdLine, this.result);
                            break;
                        }
                        catch (Exception e) {
                            ConsoleEngineImpl.this.executing = false;
                            throw new IllegalArgumentException(line + "\n" + e.getMessage());
                        }
                    }
                    if (!done) {
                        ConsoleEngineImpl.this.executing = false;
                        throw new IllegalArgumentException("Incompleted command: \n" + line);
                    }
                    ConsoleEngineImpl.this.executing = false;
                }
            }
        }

        public Object getResult() {
            return this.result;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            try {
                sb.append("script:").append(this.script.normalize());
            }
            catch (Exception e) {
                sb.append(e.getMessage());
            }
            sb.append(", ");
            sb.append("extension:").append(this.extension);
            sb.append(", ");
            sb.append("cmdLine:").append(this.cmdLine);
            sb.append(", ");
            sb.append("args:").append(Arrays.asList(this.args));
            sb.append(", ");
            sb.append("verbose:").append(this.verbose);
            sb.append(", ");
            sb.append("result:").append(this.result);
            sb.append("]");
            return sb.toString();
        }
    }

    protected static class VariableReferenceCompleter
    implements Completer {
        private final ScriptEngine engine;

        public VariableReferenceCompleter(ScriptEngine engine) {
            this.engine = engine;
        }

        @Override
        public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
            assert (commandLine != null);
            assert (candidates != null);
            String word = commandLine.word();
            try {
                String var;
                if (!word.contains(".") && !word.contains("}")) {
                    for (String v : this.engine.find().keySet()) {
                        String c = "${" + v + "}";
                        candidates.add(new Candidate(AttributedString.stripAnsi(c), c, null, null, null, null, false));
                    }
                } else if (word.startsWith("${") && word.contains("}") && word.contains(".") && this.engine.hasVariable(var = word.substring(2, word.indexOf(125)))) {
                    String curBuf = word.substring(0, word.lastIndexOf("."));
                    String objStatement = curBuf.replace("${", "").replace("}", "");
                    Object obj = curBuf.contains(".") ? this.engine.execute(objStatement) : this.engine.get(var);
                    Map map = obj instanceof Map ? (Map)obj : null;
                    Set<Object> identifiers = new HashSet();
                    if (map != null && !map.isEmpty() && map.keySet().iterator().next() instanceof String) {
                        identifiers = map.keySet();
                    } else if (map == null && obj != null) {
                        identifiers = this.getClassMethodIdentifiers(obj.getClass());
                    }
                    for (String key : identifiers) {
                        candidates.add(new Candidate(AttributedString.stripAnsi(curBuf + "." + key), key, null, null, null, null, false));
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        private Set<String> getClassMethodIdentifiers(Class<?> clazz) {
            HashSet<String> out = new HashSet<String>();
            do {
                for (Method m : clazz.getMethods()) {
                    String name;
                    if (m.isSynthetic() || m.getParameterCount() != 0 || !(name = m.getName()).matches("get[A-Z].*")) continue;
                    out.add(this.convertGetMethod2identifier(name));
                }
            } while ((clazz = clazz.getSuperclass()) != null);
            return out;
        }

        private String convertGetMethod2identifier(String name) {
            char[] c = name.substring(3).toCharArray();
            c[0] = Character.toLowerCase(c[0]);
            return new String(c);
        }
    }

    private static class AliasValueCompleter
    implements Completer {
        private final Map<String, String> aliases;

        public AliasValueCompleter(Map<String, String> aliases) {
            this.aliases = aliases;
        }

        @Override
        public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
            String v;
            String h;
            assert (commandLine != null);
            assert (candidates != null);
            List<String> words = commandLine.words();
            if (words.size() > 1 && (h = words.get(words.size() - 2)) != null && !h.isEmpty() && (v = this.aliases.get(h)) != null) {
                candidates.add(new Candidate(AttributedString.stripAnsi(v), v, null, null, null, null, true));
            }
        }
    }
}

