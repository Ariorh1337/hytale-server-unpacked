/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jline.builtins.NfaMatcher;
import org.jline.builtins.Styles;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.OSUtils;
import org.jline.utils.StyleResolver;

public class Completers {

    public static class AnyCompleter
    implements org.jline.reader.Completer {
        public static final AnyCompleter INSTANCE = new AnyCompleter();

        @Override
        public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
            assert (commandLine != null);
            assert (candidates != null);
            String buffer = commandLine.word().substring(0, commandLine.wordCursor());
            candidates.add(new Candidate(AttributedString.stripAnsi(buffer), buffer, null, null, null, null, true));
        }
    }

    public static class OptionCompleter
    implements org.jline.reader.Completer {
        private Function<String, Collection<OptDesc>> commandOptions;
        private Collection<OptDesc> options;
        private List<org.jline.reader.Completer> argsCompleters = new ArrayList<org.jline.reader.Completer>();
        private int startPos;

        public OptionCompleter(org.jline.reader.Completer completer, Function<String, Collection<OptDesc>> commandOptions, int startPos) {
            this.startPos = startPos;
            this.commandOptions = commandOptions;
            this.argsCompleters.add(completer);
        }

        public OptionCompleter(List<org.jline.reader.Completer> completers, Function<String, Collection<OptDesc>> commandOptions, int startPos) {
            this.startPos = startPos;
            this.commandOptions = commandOptions;
            this.argsCompleters = new ArrayList<org.jline.reader.Completer>(completers);
        }

        public OptionCompleter(List<org.jline.reader.Completer> completers, Map<String, List<String>> optionValues, Collection<String> options, int startPos) {
            this(optionValues, options, startPos);
            this.argsCompleters = new ArrayList<org.jline.reader.Completer>(completers);
        }

        public OptionCompleter(org.jline.reader.Completer completer, Map<String, List<String>> optionValues, Collection<String> options, int startPos) {
            this(optionValues, options, startPos);
            this.argsCompleters.add(completer);
        }

        public OptionCompleter(Map<String, List<String>> optionValues, Collection<String> options, int startPos) {
            this(OptDesc.compile(optionValues, options), startPos);
        }

        public OptionCompleter(org.jline.reader.Completer completer, Collection<OptDesc> options, int startPos) {
            this(options, startPos);
            this.argsCompleters.add(completer);
        }

        public OptionCompleter(List<org.jline.reader.Completer> completers, Collection<OptDesc> options, int startPos) {
            this(options, startPos);
            this.argsCompleters = new ArrayList<org.jline.reader.Completer>(completers);
        }

        public OptionCompleter(Collection<OptDesc> options, int startPos) {
            this.options = options;
            this.startPos = startPos;
        }

        public void setStartPos(int startPos) {
            this.startPos = startPos;
        }

        @Override
        public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
            assert (commandLine != null);
            assert (candidates != null);
            List<String> words = commandLine.words();
            String buffer = commandLine.word().substring(0, commandLine.wordCursor());
            if (this.startPos >= words.size()) {
                candidates.add(new Candidate(buffer, buffer, null, null, null, null, true));
                return;
            }
            String command = reader.getParser().getCommand(words.get(this.startPos - 1));
            if (buffer.startsWith("-")) {
                int eq;
                boolean addbuff = true;
                boolean valueCandidates = false;
                boolean longOption = buffer.startsWith("--");
                int n = eq = buffer.matches("-[a-zA-Z][a-zA-Z0-9]+") ? 2 : buffer.indexOf(61);
                if (eq < 0) {
                    ArrayList<String> usedOptions = new ArrayList<String>();
                    for (int i = this.startPos; i < words.size(); ++i) {
                        if (!words.get(i).startsWith("-")) continue;
                        String w = words.get(i);
                        int ind = w.indexOf(61);
                        if (ind < 0) {
                            usedOptions.add(w);
                            continue;
                        }
                        usedOptions.add(w.substring(0, ind));
                    }
                    for (OptDesc o : this.commandOptions == null ? this.options : this.commandOptions.apply(command)) {
                        if (usedOptions.contains(o.shortOption()) || usedOptions.contains(o.longOption())) continue;
                        if (o.startsWith(buffer)) {
                            addbuff = false;
                        }
                        o.completeOption(reader, commandLine, candidates, longOption);
                    }
                } else {
                    addbuff = false;
                    int nb = buffer.contains("=") ? 1 : 0;
                    String value = buffer.substring(eq + nb);
                    String curBuf = buffer.substring(0, eq + nb);
                    String opt = buffer.substring(0, eq);
                    OptDesc option = this.findOptDesc(command, opt);
                    if (option.hasValue()) {
                        valueCandidates = option.completeValue(reader, commandLine, candidates, curBuf, value);
                    }
                }
                if (buffer.contains("=") && !buffer.endsWith("=") && !valueCandidates || addbuff) {
                    candidates.add(new Candidate(buffer, buffer, null, null, null, null, true));
                }
            } else if (words.size() > 1 && this.shortOptionValueCompleter(command, words.get(words.size() - 2)) != null) {
                this.shortOptionValueCompleter(command, words.get(words.size() - 2)).complete(reader, commandLine, candidates);
            } else if (words.size() > 1 && this.longOptionValueCompleter(command, words.get(words.size() - 2)) != null) {
                this.longOptionValueCompleter(command, words.get(words.size() - 2)).complete(reader, commandLine, candidates);
            } else if (!this.argsCompleters.isEmpty()) {
                int args = -1;
                for (int i = this.startPos; i < words.size(); ++i) {
                    if (words.get(i).startsWith("-") || i <= 0 || this.shortOptionValueCompleter(command, words.get(i - 1)) != null || this.longOptionValueCompleter(command, words.get(i - 1)) != null) continue;
                    ++args;
                }
                if (args == -1) {
                    candidates.add(new Candidate(buffer, buffer, null, null, null, null, true));
                } else if (args < this.argsCompleters.size()) {
                    this.argsCompleters.get(args).complete(reader, commandLine, candidates);
                } else {
                    this.argsCompleters.get(this.argsCompleters.size() - 1).complete(reader, commandLine, candidates);
                }
            }
        }

        private org.jline.reader.Completer longOptionValueCompleter(String command, String opt) {
            if (!opt.matches("--[a-zA-Z]+")) {
                return null;
            }
            Collection<OptDesc> optDescs = this.commandOptions == null ? this.options : this.commandOptions.apply(command);
            OptDesc option = this.findOptDesc(optDescs, opt);
            return option.hasValue() ? option.valueCompleter() : null;
        }

        private org.jline.reader.Completer shortOptionValueCompleter(String command, String opt) {
            Collection<OptDesc> optDescs;
            if (!opt.matches("-[a-zA-Z]+")) {
                return null;
            }
            org.jline.reader.Completer out = null;
            Collection<OptDesc> collection = optDescs = this.commandOptions == null ? this.options : this.commandOptions.apply(command);
            if (opt.length() == 2) {
                out = this.findOptDesc(optDescs, opt).valueCompleter();
            } else if (opt.length() > 2) {
                for (int i = 1; i < opt.length(); ++i) {
                    OptDesc o = this.findOptDesc(optDescs, "-" + opt.charAt(i));
                    if (o.shortOption() == null) {
                        return null;
                    }
                    if (out != null) continue;
                    out = o.valueCompleter();
                }
            }
            return out;
        }

        private OptDesc findOptDesc(String command, String opt) {
            return this.findOptDesc(this.commandOptions == null ? this.options : this.commandOptions.apply(command), opt);
        }

        private OptDesc findOptDesc(Collection<OptDesc> optDescs, String opt) {
            for (OptDesc o : optDescs) {
                if (!o.match(opt)) continue;
                return o;
            }
            return new OptDesc();
        }
    }

    public static class OptDesc {
        private String shortOption;
        private String longOption;
        private String description;
        private org.jline.reader.Completer valueCompleter;

        protected static List<OptDesc> compile(Map<String, List<String>> optionValues, Collection<String> options) {
            ArrayList<OptDesc> out = new ArrayList<OptDesc>();
            for (Map.Entry<String, List<String>> entry : optionValues.entrySet()) {
                if (entry.getKey().startsWith("--")) {
                    out.add(new OptDesc(null, entry.getKey(), new StringsCompleter((Iterable<String>)entry.getValue())));
                    continue;
                }
                if (!entry.getKey().matches("-[a-zA-Z]")) continue;
                out.add(new OptDesc(entry.getKey(), null, new StringsCompleter((Iterable<String>)entry.getValue())));
            }
            for (String o : options) {
                if (o.startsWith("--")) {
                    out.add(new OptDesc(null, o));
                    continue;
                }
                if (!o.matches("-[a-zA-Z]")) continue;
                out.add(new OptDesc(o, null));
            }
            return out;
        }

        public OptDesc(String shortOption, String longOption, String description, org.jline.reader.Completer valueCompleter) {
            this.shortOption = shortOption;
            this.longOption = longOption;
            this.description = description;
            this.valueCompleter = valueCompleter;
        }

        public OptDesc(String shortOption, String longOption, org.jline.reader.Completer valueCompleter) {
            this(shortOption, longOption, null, valueCompleter);
        }

        public OptDesc(String shortOption, String longOption, String description) {
            this(shortOption, longOption, description, null);
        }

        public OptDesc(String shortOption, String longOption) {
            this(shortOption, longOption, null, null);
        }

        protected OptDesc() {
        }

        public void setValueCompleter(org.jline.reader.Completer valueCompleter) {
            this.valueCompleter = valueCompleter;
        }

        public String longOption() {
            return this.longOption;
        }

        public String shortOption() {
            return this.shortOption;
        }

        public String description() {
            return this.description;
        }

        protected boolean hasValue() {
            return this.valueCompleter != null && this.valueCompleter != NullCompleter.INSTANCE;
        }

        protected org.jline.reader.Completer valueCompleter() {
            return this.valueCompleter;
        }

        protected void completeOption(LineReader reader, ParsedLine commandLine, List<Candidate> candidates, boolean longOpt) {
            if (!longOpt) {
                if (this.shortOption != null) {
                    candidates.add(new Candidate(this.shortOption, this.shortOption, null, this.description, null, null, false));
                }
            } else if (this.longOption != null) {
                if (this.hasValue()) {
                    candidates.add(new Candidate(this.longOption + "=", this.longOption, null, this.description, null, null, false));
                } else {
                    candidates.add(new Candidate(this.longOption, this.longOption, null, this.description, null, null, true));
                }
            }
        }

        protected boolean completeValue(LineReader reader, ParsedLine commandLine, List<Candidate> candidates, String curBuf, String partialValue) {
            boolean out = false;
            ArrayList<Candidate> temp = new ArrayList<Candidate>();
            ParsedLine pl = reader.getParser().parse(partialValue, partialValue.length());
            this.valueCompleter.complete(reader, pl, temp);
            for (Candidate c : temp) {
                String v = c.value();
                if (!v.startsWith(partialValue)) continue;
                out = true;
                String val = c.value();
                if (this.valueCompleter instanceof FileNameCompleter) {
                    FileNameCompleter cc = (FileNameCompleter)this.valueCompleter;
                    String sep = cc.getSeparator(reader.isSet(LineReader.Option.USE_FORWARD_SLASH));
                    val = cc.getDisplay(reader.getTerminal(), Paths.get(c.value(), new String[0]), Styles.lsStyle(), sep);
                }
                candidates.add(new Candidate(curBuf + v, val, null, null, null, null, c.complete()));
            }
            return out;
        }

        protected boolean match(String option) {
            return this.shortOption != null && this.shortOption.equals(option) || this.longOption != null && this.longOption.equals(option);
        }

        protected boolean startsWith(String option) {
            return this.shortOption != null && this.shortOption.startsWith(option) || this.longOption != null && this.longOption.startsWith(option);
        }
    }

    public static class RegexCompleter
    implements org.jline.reader.Completer {
        private final NfaMatcher<String> matcher;
        private final Function<String, org.jline.reader.Completer> completers;
        private final ThreadLocal<LineReader> reader = new ThreadLocal();

        public RegexCompleter(String syntax, Function<String, org.jline.reader.Completer> completers) {
            this.matcher = new NfaMatcher<String>(syntax, this::doMatch);
            this.completers = completers;
        }

        @Override
        public synchronized void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            List<String> words = line.words().subList(0, line.wordIndex());
            this.reader.set(reader);
            Set<String> next = this.matcher.matchPartial(words);
            for (String n : next) {
                this.completers.apply(n).complete(reader, new ArgumentLine(line.word(), line.wordCursor()), candidates);
            }
            this.reader.set(null);
        }

        private boolean doMatch(String arg, String name) {
            ArrayList<Candidate> candidates = new ArrayList<Candidate>();
            LineReader r = this.reader.get();
            boolean caseInsensitive = r != null && r.isSet(LineReader.Option.CASE_INSENSITIVE);
            this.completers.apply(name).complete(r, new ArgumentLine(arg, arg.length()), candidates);
            return candidates.stream().anyMatch(c -> caseInsensitive ? c.value().equalsIgnoreCase(arg) : c.value().equals(arg));
        }

        public static class ArgumentLine
        implements ParsedLine {
            private final String word;
            private final int cursor;

            public ArgumentLine(String word, int cursor) {
                this.word = word;
                this.cursor = cursor;
            }

            @Override
            public String word() {
                return this.word;
            }

            @Override
            public int wordCursor() {
                return this.cursor;
            }

            @Override
            public int wordIndex() {
                return 0;
            }

            @Override
            public List<String> words() {
                return Collections.singletonList(this.word);
            }

            @Override
            public String line() {
                return this.word;
            }

            @Override
            public int cursor() {
                return this.cursor;
            }
        }
    }

    public static class TreeCompleter
    implements org.jline.reader.Completer {
        final Map<String, org.jline.reader.Completer> completers = new HashMap<String, org.jline.reader.Completer>();
        final RegexCompleter completer;

        public TreeCompleter(Node ... nodes) {
            this(Arrays.asList(nodes));
        }

        public TreeCompleter(List<Node> nodes) {
            StringBuilder sb = new StringBuilder();
            this.addRoots(sb, nodes);
            this.completer = new RegexCompleter(sb.toString(), this.completers::get);
        }

        public static Node node(Object ... objs) {
            org.jline.reader.Completer comp = null;
            ArrayList<Candidate> cands = new ArrayList<Candidate>();
            ArrayList<Node> nodes = new ArrayList<Node>();
            for (Object obj : objs) {
                if (obj instanceof String) {
                    cands.add(new Candidate((String)obj));
                    continue;
                }
                if (obj instanceof Candidate) {
                    cands.add((Candidate)obj);
                    continue;
                }
                if (obj instanceof Node) {
                    nodes.add((Node)obj);
                    continue;
                }
                if (obj instanceof org.jline.reader.Completer) {
                    comp = (org.jline.reader.Completer)obj;
                    continue;
                }
                throw new IllegalArgumentException();
            }
            if (comp != null) {
                if (!cands.isEmpty()) {
                    throw new IllegalArgumentException();
                }
                return new Node(comp, nodes);
            }
            if (!cands.isEmpty()) {
                return new Node((r, l, c) -> c.addAll(cands), nodes);
            }
            throw new IllegalArgumentException();
        }

        void addRoots(StringBuilder sb, List<Node> nodes) {
            if (!nodes.isEmpty()) {
                sb.append(" ( ");
                boolean first = true;
                for (Node n : nodes) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(" | ");
                    }
                    String name = "c" + this.completers.size();
                    this.completers.put(name, n.completer);
                    sb.append(name);
                    this.addRoots(sb, n.nodes);
                }
                sb.append(" ) ");
            }
        }

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            this.completer.complete(reader, line, candidates);
        }

        public static class Node {
            final org.jline.reader.Completer completer;
            final List<Node> nodes;

            public Node(org.jline.reader.Completer completer, List<Node> nodes) {
                this.completer = completer;
                this.nodes = nodes;
            }
        }
    }

    public static class FileNameCompleter
    implements org.jline.reader.Completer {
        @Override
        public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
            assert (commandLine != null);
            assert (candidates != null);
            String buffer = commandLine.word().substring(0, commandLine.wordCursor());
            String sep = this.getSeparator(reader.isSet(LineReader.Option.USE_FORWARD_SLASH));
            int lastSep = buffer.lastIndexOf(sep);
            try {
                Path current;
                String curBuf;
                if (lastSep >= 0) {
                    curBuf = buffer.substring(0, lastSep + 1);
                    current = curBuf.startsWith("~") ? (curBuf.startsWith("~" + sep) ? this.getUserHome().resolve(curBuf.substring(2)) : this.getUserHome().getParent().resolve(curBuf.substring(1))) : this.getUserDir().resolve(curBuf);
                } else {
                    curBuf = "";
                    current = this.getUserDir();
                }
                StyleResolver resolver = Styles.lsStyle();
                try (DirectoryStream<Path> directory = Files.newDirectoryStream(current, this::accept);){
                    directory.forEach(p -> {
                        String value = curBuf + p.getFileName().toString();
                        if (Files.isDirectory(p, new LinkOption[0])) {
                            candidates.add(new Candidate(value + (reader.isSet(LineReader.Option.AUTO_PARAM_SLASH) ? sep : ""), this.getDisplay(reader.getTerminal(), (Path)p, resolver, sep), null, null, reader.isSet(LineReader.Option.AUTO_REMOVE_SLASH) ? sep : null, null, false));
                        } else {
                            candidates.add(new Candidate(value, this.getDisplay(reader.getTerminal(), (Path)p, resolver, sep), null, null, null, null, true));
                        }
                    });
                }
                catch (IOException iOException) {}
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        protected boolean accept(Path path) {
            try {
                return !Files.isHidden(path);
            }
            catch (IOException e) {
                return false;
            }
        }

        protected Path getUserDir() {
            return Paths.get(System.getProperty("user.dir"), new String[0]);
        }

        protected Path getUserHome() {
            return Paths.get(System.getProperty("user.home"), new String[0]);
        }

        protected String getSeparator(boolean useForwardSlash) {
            return useForwardSlash ? "/" : this.getUserDir().getFileSystem().getSeparator();
        }

        protected String getDisplay(Terminal terminal, Path p, StyleResolver resolver, String separator) {
            String type;
            AttributedStringBuilder sb = new AttributedStringBuilder();
            String name = p.getFileName().toString();
            int idx = name.lastIndexOf(".");
            String string = type = idx != -1 ? ".*" + name.substring(idx) : null;
            if (Files.isSymbolicLink(p)) {
                sb.styled(resolver.resolve(".ln"), (CharSequence)name).append("@");
            } else if (Files.isDirectory(p, new LinkOption[0])) {
                sb.styled(resolver.resolve(".di"), (CharSequence)name).append(separator);
            } else if (Files.isExecutable(p) && !OSUtils.IS_WINDOWS) {
                sb.styled(resolver.resolve(".ex"), (CharSequence)name).append("*");
            } else if (type != null && resolver.resolve(type).getStyle() != 0L) {
                sb.styled(resolver.resolve(type), (CharSequence)name);
            } else if (Files.isRegularFile(p, new LinkOption[0])) {
                sb.styled(resolver.resolve(".fi"), (CharSequence)name);
            } else {
                sb.append(name);
            }
            return sb.toAnsi(terminal);
        }
    }

    public static class FilesCompleter
    extends FileNameCompleter {
        private final Supplier<Path> currentDir;
        private final String namePattern;

        public FilesCompleter(File currentDir) {
            this(currentDir.toPath(), null);
        }

        public FilesCompleter(File currentDir, String namePattern) {
            this(currentDir.toPath(), namePattern);
        }

        public FilesCompleter(Path currentDir) {
            this(currentDir, null);
        }

        public FilesCompleter(Path currentDir, String namePattern) {
            this.currentDir = () -> currentDir;
            this.namePattern = this.compilePattern(namePattern);
        }

        public FilesCompleter(Supplier<Path> currentDir) {
            this(currentDir, null);
        }

        public FilesCompleter(Supplier<Path> currentDir, String namePattern) {
            this.currentDir = currentDir;
            this.namePattern = this.compilePattern(namePattern);
        }

        private String compilePattern(String pattern) {
            if (pattern == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pattern.length(); ++i) {
                char ch = pattern.charAt(i);
                if (ch == '\\') {
                    ch = pattern.charAt(++i);
                    sb.append(ch);
                    continue;
                }
                if (ch == '.') {
                    sb.append('\\').append('.');
                    continue;
                }
                if (ch == '*') {
                    sb.append('.').append('*');
                    continue;
                }
                sb.append(ch);
            }
            return sb.toString();
        }

        @Override
        protected Path getUserDir() {
            return this.currentDir.get();
        }

        @Override
        protected boolean accept(Path path) {
            if (this.namePattern == null || Files.isDirectory(path, new LinkOption[0])) {
                return super.accept(path);
            }
            return path.getFileName().toString().matches(this.namePattern) && super.accept(path);
        }
    }

    public static class DirectoriesCompleter
    extends FileNameCompleter {
        private final Supplier<Path> currentDir;

        public DirectoriesCompleter(File currentDir) {
            this(currentDir.toPath());
        }

        public DirectoriesCompleter(Path currentDir) {
            this.currentDir = () -> currentDir;
        }

        public DirectoriesCompleter(Supplier<Path> currentDir) {
            this.currentDir = currentDir;
        }

        @Override
        protected Path getUserDir() {
            return this.currentDir.get();
        }

        @Override
        protected boolean accept(Path path) {
            return Files.isDirectory(path, new LinkOption[0]) && super.accept(path);
        }
    }

    public static class Completer
    implements org.jline.reader.Completer {
        private final CompletionEnvironment environment;

        public Completer(CompletionEnvironment environment) {
            this.environment = environment;
        }

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            if (line.wordIndex() == 0) {
                this.completeCommand(candidates);
            } else {
                this.tryCompleteArguments(reader, line, candidates);
            }
        }

        protected void tryCompleteArguments(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            List<CompletionData> cmd;
            String command = line.words().get(0);
            String resolved = this.environment.resolveCommand(command);
            Map<String, List<CompletionData>> comp = this.environment.getCompletions();
            if (comp != null && (cmd = comp.get(resolved)) != null) {
                this.completeCommandArguments(reader, line, candidates, cmd);
            }
        }

        protected void completeCommandArguments(LineReader reader, ParsedLine line, List<Candidate> candidates, List<CompletionData> completions) {
            for (CompletionData completion : completions) {
                Object res;
                boolean isOption = line.word().startsWith("-");
                String prevOption = line.wordIndex() >= 2 && line.words().get(line.wordIndex() - 1).startsWith("-") ? line.words().get(line.wordIndex() - 1) : null;
                String key = UUID.randomUUID().toString();
                boolean conditionValue = true;
                if (completion.condition != null) {
                    res = Boolean.FALSE;
                    try {
                        res = this.environment.evaluate(reader, line, completion.condition);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    conditionValue = this.isTrue(res);
                }
                if (conditionValue && isOption && completion.options != null) {
                    for (String opt : completion.options) {
                        candidates.add(new Candidate(opt, opt, "options", completion.description, null, key, true));
                    }
                    continue;
                }
                if (!isOption && prevOption != null && completion.argument != null && completion.options != null && completion.options.contains(prevOption)) {
                    res = null;
                    try {
                        res = this.environment.evaluate(reader, line, completion.argument);
                    }
                    catch (Throwable opt) {
                        // empty catch block
                    }
                    if (res instanceof Candidate) {
                        candidates.add((Candidate)res);
                        continue;
                    }
                    if (res instanceof String) {
                        candidates.add(new Candidate((String)res, (String)res, null, null, null, null, true));
                        continue;
                    }
                    if (res instanceof Collection) {
                        for (Object s : (Collection)res) {
                            if (s instanceof Candidate) {
                                candidates.add((Candidate)s);
                                continue;
                            }
                            if (!(s instanceof String)) continue;
                            candidates.add(new Candidate((String)s, (String)s, null, null, null, null, true));
                        }
                        continue;
                    }
                    if (res == null || !res.getClass().isArray()) continue;
                    int l = Array.getLength(res);
                    for (int i = 0; i < l; ++i) {
                        Object s = Array.get(res, i);
                        if (s instanceof Candidate) {
                            candidates.add((Candidate)s);
                            continue;
                        }
                        if (!(s instanceof String)) continue;
                        candidates.add(new Candidate((String)s, (String)s, null, null, null, null, true));
                    }
                    continue;
                }
                if (isOption || completion.argument == null) continue;
                res = null;
                try {
                    res = this.environment.evaluate(reader, line, completion.argument);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (res instanceof Candidate) {
                    candidates.add((Candidate)res);
                    continue;
                }
                if (res instanceof String) {
                    candidates.add(new Candidate((String)res, (String)res, null, completion.description, null, null, true));
                    continue;
                }
                if (!(res instanceof Collection)) continue;
                for (Object s : (Collection)res) {
                    if (s instanceof Candidate) {
                        candidates.add((Candidate)s);
                        continue;
                    }
                    if (!(s instanceof String)) continue;
                    candidates.add(new Candidate((String)s, (String)s, null, completion.description, null, null, true));
                }
            }
        }

        protected void completeCommand(List<Candidate> candidates) {
            Set<String> commands = this.environment.getCommands();
            for (String command : commands) {
                List<CompletionData> completions;
                String name = this.environment.commandName(command);
                boolean resolved = command.equals(this.environment.resolveCommand(name));
                if (name.startsWith("_")) continue;
                String desc = null;
                Map<String, List<CompletionData>> comp = this.environment.getCompletions();
                if (comp != null && (completions = comp.get(command)) != null) {
                    for (CompletionData completion : completions) {
                        if (completion.description == null || completion.options != null || completion.argument != null || completion.condition != null) continue;
                        desc = completion.description;
                    }
                }
                String key = UUID.randomUUID().toString();
                if (desc != null) {
                    candidates.add(new Candidate(command, command, null, desc, null, key, true));
                    if (!resolved) continue;
                    candidates.add(new Candidate(name, name, null, desc, null, key, true));
                    continue;
                }
                candidates.add(new Candidate(command, command, null, null, null, key, true));
                if (!resolved) continue;
                candidates.add(new Candidate(name, name, null, null, null, key, true));
            }
        }

        private boolean isTrue(Object result) {
            if (result == null) {
                return false;
            }
            if (result instanceof Boolean) {
                return (Boolean)result;
            }
            if (result instanceof Number && 0 == ((Number)result).intValue()) {
                return false;
            }
            return !"".equals(result) && !"0".equals(result);
        }
    }

    public static class CompletionData {
        public final List<String> options;
        public final String description;
        public final String argument;
        public final String condition;

        public CompletionData(List<String> options, String description, String argument, String condition) {
            this.options = options;
            this.description = description;
            this.argument = argument;
            this.condition = condition;
        }
    }

    public static interface CompletionEnvironment {
        public Map<String, List<CompletionData>> getCompletions();

        public Set<String> getCommands();

        public String resolveCommand(String var1);

        public String commandName(String var1);

        public Object evaluate(LineReader var1, ParsedLine var2, String var3) throws Exception;
    }
}

