/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import org.jline.builtins.Commands;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.Nano;
import org.jline.builtins.Options;
import org.jline.builtins.Source;
import org.jline.builtins.SyntaxHighlighter;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Status;

public class Less {
    private static final int ESCAPE = 27;
    private static final String MESSAGE_FILE_INFO = "FILE_INFO";
    public boolean quitAtSecondEof;
    public boolean quitAtFirstEof;
    public boolean quitIfOneScreen;
    public boolean printLineNumbers;
    public boolean quiet;
    public boolean veryQuiet;
    public boolean chopLongLines;
    public boolean ignoreCaseCond;
    public boolean ignoreCaseAlways;
    public boolean noKeypad;
    public boolean noInit;
    protected List<Integer> tabs = Collections.singletonList(4);
    protected String syntaxName;
    private String historyLog = null;
    protected final Terminal terminal;
    protected final Display display;
    protected final BindingReader bindingReader;
    protected final Path currentDir;
    protected List<Source> sources;
    protected int sourceIdx;
    protected BufferedReader reader;
    protected KeyMap<Operation> keys;
    protected int firstLineInMemory = 0;
    protected List<AttributedString> lines = new ArrayList<AttributedString>();
    protected int firstLineToDisplay = 0;
    protected int firstColumnToDisplay = 0;
    protected int offsetInLine = 0;
    protected String message;
    protected String errorMessage;
    protected final StringBuilder buffer = new StringBuilder();
    protected final Map<String, Operation> options = new TreeMap<String, Operation>();
    protected int window;
    protected int halfWindow;
    protected int nbEof;
    protected Nano.PatternHistory patternHistory = new Nano.PatternHistory(null);
    protected String pattern;
    protected String displayPattern;
    protected final Size size = new Size();
    SyntaxHighlighter syntaxHighlighter;
    private final List<Path> syntaxFiles = new ArrayList<Path>();
    private boolean highlight = true;
    private boolean nanorcIgnoreErrors;

    public static String[] usage() {
        return new String[]{"less -  file pager", "Usage: less [OPTIONS] [FILES]", "  -? --help                    Show help", "  -e --quit-at-eof             Exit on second EOF", "  -E --QUIT-AT-EOF             Exit on EOF", "  -F --quit-if-one-screen      Exit if entire file fits on first screen", "  -q --quiet --silent          Silent mode", "  -Q --QUIET --SILENT          Completely silent", "  -S --chop-long-lines         Do not fold long lines", "  -i --ignore-case             Search ignores lowercase case", "  -I --IGNORE-CASE             Search ignores all case", "  -x --tabs=N[,...]            Set tab stops", "  -N --LINE-NUMBERS            Display line number for each line", "  -Y --syntax=name             The name of the syntax highlighting to use.", "     --no-init                 Disable terminal initialization", "     --no-keypad               Disable keypad handling", "     --ignorercfiles           Don't look at the system's lessrc nor at the user's lessrc.", "  -H --historylog=name         Log search strings to file, so they can be retrieved in later sessions"};
    }

    public Less(Terminal terminal, Path currentDir) {
        this(terminal, currentDir, null);
    }

    public Less(Terminal terminal, Path currentDir, Options opts) {
        this(terminal, currentDir, opts, null);
    }

    public Less(Terminal terminal, Path currentDir, Options opts, ConfigurationPath configPath) {
        boolean ignorercfiles;
        this.terminal = terminal;
        this.display = new Display(terminal, true);
        this.bindingReader = new BindingReader(terminal.reader());
        this.currentDir = currentDir;
        Path lessrc = configPath != null ? configPath.getConfig("jlessrc") : null;
        boolean bl = ignorercfiles = opts != null && opts.isSet("ignorercfiles");
        if (lessrc != null && !ignorercfiles) {
            try {
                this.parseConfig(lessrc);
            }
            catch (IOException e) {
                this.errorMessage = "Encountered error while reading config file: " + lessrc;
            }
        } else if (new File("/usr/share/nano").exists() && !ignorercfiles) {
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:/usr/share/nano/*.nanorc");
            try (Stream<Path> pathStream = Files.walk(Paths.get("/usr/share/nano", new String[0]), new FileVisitOption[0]);){
                pathStream.filter(pathMatcher::matches).forEach(this.syntaxFiles::add);
                this.nanorcIgnoreErrors = true;
            }
            catch (IOException e) {
                this.errorMessage = "Encountered error while reading nanorc files";
            }
        }
        if (opts != null) {
            if (opts.isSet("QUIT-AT-EOF")) {
                this.quitAtFirstEof = true;
            }
            if (opts.isSet("quit-at-eof")) {
                this.quitAtSecondEof = true;
            }
            if (opts.isSet("quit-if-one-screen")) {
                this.quitIfOneScreen = true;
            }
            if (opts.isSet("quiet")) {
                this.quiet = true;
            }
            if (opts.isSet("QUIET")) {
                this.veryQuiet = true;
            }
            if (opts.isSet("chop-long-lines")) {
                this.chopLongLines = true;
            }
            if (opts.isSet("IGNORE-CASE")) {
                this.ignoreCaseAlways = true;
            }
            if (opts.isSet("ignore-case")) {
                this.ignoreCaseCond = true;
            }
            if (opts.isSet("LINE-NUMBERS")) {
                this.printLineNumbers = true;
            }
            if (opts.isSet("tabs")) {
                this.doTabs(opts.get("tabs"));
            }
            if (opts.isSet("syntax")) {
                this.syntaxName = opts.get("syntax");
                this.nanorcIgnoreErrors = false;
            }
            if (opts.isSet("no-init")) {
                this.noInit = true;
            }
            if (opts.isSet("no-keypad")) {
                this.noKeypad = true;
            }
            if (opts.isSet("historylog")) {
                this.historyLog = opts.get("historylog");
            }
        }
        if (configPath != null && this.historyLog != null) {
            try {
                this.patternHistory = new Nano.PatternHistory(configPath.getUserConfig(this.historyLog, true));
            }
            catch (IOException e) {
                this.errorMessage = "Encountered error while reading pattern-history file: " + this.historyLog;
            }
        }
    }

    private void parseConfig(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file);){
            String line = reader.readLine();
            while (line != null) {
                if (!(line = line.trim()).isEmpty() && !line.startsWith("#")) {
                    String option;
                    List<String> parts = SyntaxHighlighter.RuleSplitter.split(line);
                    if (parts.get(0).equals("include")) {
                        SyntaxHighlighter.nanorcInclude(file, parts.get(1), this.syntaxFiles);
                    } else if (parts.get(0).equals("theme")) {
                        SyntaxHighlighter.nanorcTheme(file, parts.get(1), this.syntaxFiles);
                    } else if (parts.size() == 2 && (parts.get(0).equals("set") || parts.get(0).equals("unset"))) {
                        option = parts.get(1);
                        boolean val = parts.get(0).equals("set");
                        if (option.equals("QUIT-AT-EOF")) {
                            this.quitAtFirstEof = val;
                        } else if (option.equals("quit-at-eof")) {
                            this.quitAtSecondEof = val;
                        } else if (option.equals("quit-if-one-screen")) {
                            this.quitIfOneScreen = val;
                        } else if (option.equals("quiet") || option.equals("silent")) {
                            this.quiet = val;
                        } else if (option.equals("QUIET") || option.equals("SILENT")) {
                            this.veryQuiet = val;
                        } else if (option.equals("chop-long-lines")) {
                            this.chopLongLines = val;
                        } else if (option.equals("IGNORE-CASE")) {
                            this.ignoreCaseAlways = val;
                        } else if (option.equals("ignore-case")) {
                            this.ignoreCaseCond = val;
                        } else if (option.equals("LINE-NUMBERS")) {
                            this.printLineNumbers = val;
                        } else {
                            this.errorMessage = "Less config: Unknown or unsupported configuration option " + option;
                        }
                    } else if (parts.size() == 3 && parts.get(0).equals("set")) {
                        option = parts.get(1);
                        String val = parts.get(2);
                        if (option.equals("tabs")) {
                            this.doTabs(val);
                        } else if (option.equals("historylog")) {
                            this.historyLog = val;
                        } else {
                            this.errorMessage = "Less config: Unknown or unsupported configuration option " + option;
                        }
                    } else {
                        this.errorMessage = parts.get(0).equals("bind") || parts.get(0).equals("unbind") ? "Less config: Key bindings can not be changed!" : "Less config: Bad configuration '" + line + "'";
                    }
                }
                line = reader.readLine();
            }
        }
    }

    private void doTabs(String val) {
        this.tabs = new ArrayList<Integer>();
        for (String s : val.split(",")) {
            try {
                this.tabs.add(Integer.parseInt(s));
            }
            catch (Exception ex) {
                this.errorMessage = "Less config: tabs option error parsing number: " + s;
            }
        }
    }

    public Less tabs(List<Integer> tabs) {
        this.tabs = tabs;
        return this;
    }

    public void handle(Terminal.Signal signal) {
        this.size.copy(this.terminal.getSize());
        try {
            this.display.clear();
            this.display(false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(Source ... sources) throws IOException, InterruptedException {
        this.run(new ArrayList<Source>(Arrays.asList(sources)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void run(List<Source> sources) throws IOException, InterruptedException {
        block100: {
            if (sources == null || sources.isEmpty()) {
                throw new IllegalArgumentException("No sources");
            }
            sources.add(0, new Source.ResourceSource("less-help.txt", "HELP -- Press SPACE for more, or q when done"));
            this.sources = sources;
            this.sourceIdx = 1;
            this.openSource();
            if (this.errorMessage != null) {
                this.message = this.errorMessage;
                this.errorMessage = null;
            }
            Status status = Status.getStatus(this.terminal, false);
            try {
                block99: {
                    if (status != null) {
                        status.suspend();
                    }
                    this.size.copy(this.terminal.getSize());
                    if (this.quitIfOneScreen && sources.size() == 2 && this.display(true)) {
                        return;
                    }
                    Terminal.SignalHandler prevHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handle);
                    Attributes attr = this.terminal.enterRawMode();
                    try {
                        Enum op;
                        this.window = this.size.getRows() - 1;
                        this.halfWindow = this.window / 2;
                        this.keys = new KeyMap();
                        this.bindKeys(this.keys);
                        if (!this.noInit) {
                            this.terminal.puts(InfoCmp.Capability.enter_ca_mode, new Object[0]);
                        }
                        if (!this.noKeypad) {
                            this.terminal.puts(InfoCmp.Capability.keypad_xmit, new Object[0]);
                        }
                        this.terminal.writer().flush();
                        this.display.clear();
                        this.display(false);
                        Less.checkInterrupted();
                        this.options.put("-e", Operation.OPT_QUIT_AT_SECOND_EOF);
                        this.options.put("--quit-at-eof", Operation.OPT_QUIT_AT_SECOND_EOF);
                        this.options.put("-E", Operation.OPT_QUIT_AT_FIRST_EOF);
                        this.options.put("-QUIT-AT-EOF", Operation.OPT_QUIT_AT_FIRST_EOF);
                        this.options.put("-N", Operation.OPT_PRINT_LINES);
                        this.options.put("--LINE-NUMBERS", Operation.OPT_PRINT_LINES);
                        this.options.put("-q", Operation.OPT_QUIET);
                        this.options.put("--quiet", Operation.OPT_QUIET);
                        this.options.put("--silent", Operation.OPT_QUIET);
                        this.options.put("-Q", Operation.OPT_VERY_QUIET);
                        this.options.put("--QUIET", Operation.OPT_VERY_QUIET);
                        this.options.put("--SILENT", Operation.OPT_VERY_QUIET);
                        this.options.put("-S", Operation.OPT_CHOP_LONG_LINES);
                        this.options.put("--chop-long-lines", Operation.OPT_CHOP_LONG_LINES);
                        this.options.put("-i", Operation.OPT_IGNORE_CASE_COND);
                        this.options.put("--ignore-case", Operation.OPT_IGNORE_CASE_COND);
                        this.options.put("-I", Operation.OPT_IGNORE_CASE_ALWAYS);
                        this.options.put("--IGNORE-CASE", Operation.OPT_IGNORE_CASE_ALWAYS);
                        this.options.put("-Y", Operation.OPT_SYNTAX_HIGHLIGHT);
                        this.options.put("--syntax", Operation.OPT_SYNTAX_HIGHLIGHT);
                        boolean forward = true;
                        do {
                            Less.checkInterrupted();
                            op = null;
                            if (this.buffer.length() > 0 && this.buffer.charAt(0) == '-') {
                                int c = this.terminal.reader().read();
                                this.message = null;
                                if (this.buffer.length() == 1) {
                                    this.buffer.append((char)c);
                                    if (c != 45 && (op = this.options.get(this.buffer.toString())) == null) {
                                        this.message = "There is no " + this.printable(this.buffer.toString()) + " option";
                                        this.buffer.setLength(0);
                                    }
                                } else if (c == 13) {
                                    op = this.options.get(this.buffer.toString());
                                    if (op == null) {
                                        this.message = "There is no " + this.printable(this.buffer.toString()) + " option";
                                        this.buffer.setLength(0);
                                    }
                                } else {
                                    this.buffer.append((char)c);
                                    HashMap<String, Operation> matching = new HashMap<String, Operation>();
                                    for (Map.Entry<String, Operation> entry : this.options.entrySet()) {
                                        if (!entry.getKey().startsWith(this.buffer.toString())) continue;
                                        matching.put(entry.getKey(), entry.getValue());
                                    }
                                    switch (matching.size()) {
                                        case 0: {
                                            this.buffer.setLength(0);
                                            break;
                                        }
                                        case 1: {
                                            this.buffer.setLength(0);
                                            this.buffer.append((String)matching.keySet().iterator().next());
                                        }
                                    }
                                }
                            } else if (this.buffer.length() > 0 && (this.buffer.charAt(0) == '/' || this.buffer.charAt(0) == '?' || this.buffer.charAt(0) == '&')) {
                                forward = this.search();
                            } else {
                                Operation obj = this.bindingReader.readBinding(this.keys, null, false);
                                if (obj == Operation.CHAR) {
                                    char c = this.bindingReader.getLastBinding().charAt(0);
                                    if (c == '-' || c == '/' || c == '?' || c == '&') {
                                        this.buffer.setLength(0);
                                    }
                                    this.buffer.append(c);
                                } else if (obj == Operation.BACKSPACE) {
                                    if (this.buffer.length() > 0) {
                                        this.buffer.deleteCharAt(this.buffer.length() - 1);
                                    }
                                } else {
                                    op = obj;
                                }
                            }
                            if (op != null) {
                                this.message = null;
                                switch (op.ordinal()) {
                                    case 2: {
                                        this.moveForward(this.getStrictPositiveNumberInBuffer(1));
                                        break;
                                    }
                                    case 3: {
                                        this.moveBackward(this.getStrictPositiveNumberInBuffer(1));
                                        break;
                                    }
                                    case 4: {
                                        this.moveForward(this.getStrictPositiveNumberInBuffer(this.window));
                                        break;
                                    }
                                    case 6: {
                                        this.window = this.getStrictPositiveNumberInBuffer(this.window);
                                        this.moveForward(this.window);
                                        break;
                                    }
                                    case 8: {
                                        this.moveForward(this.window);
                                        break;
                                    }
                                    case 9: {
                                        this.halfWindow = this.getStrictPositiveNumberInBuffer(this.halfWindow);
                                        this.moveForward(this.halfWindow);
                                        break;
                                    }
                                    case 7: {
                                        this.window = this.getStrictPositiveNumberInBuffer(this.window);
                                        this.moveBackward(this.window);
                                        break;
                                    }
                                    case 5: {
                                        this.moveBackward(this.getStrictPositiveNumberInBuffer(this.window));
                                        break;
                                    }
                                    case 10: {
                                        this.halfWindow = this.getStrictPositiveNumberInBuffer(this.halfWindow);
                                        this.moveBackward(this.halfWindow);
                                        break;
                                    }
                                    case 21: {
                                        this.moveTo(this.getStrictPositiveNumberInBuffer(1) - 1);
                                        break;
                                    }
                                    case 22: {
                                        int lineNum = this.getStrictPositiveNumberInBuffer(0) - 1;
                                        if (lineNum < 0) {
                                            this.moveForward(Integer.MAX_VALUE);
                                            break;
                                        }
                                        this.moveTo(lineNum);
                                        break;
                                    }
                                    case 49: {
                                        this.moveTo(0);
                                        break;
                                    }
                                    case 50: {
                                        this.moveForward(Integer.MAX_VALUE);
                                        break;
                                    }
                                    case 11: {
                                        this.firstColumnToDisplay = Math.max(0, this.firstColumnToDisplay - this.size.getColumns() / 2);
                                        break;
                                    }
                                    case 12: {
                                        this.firstColumnToDisplay += this.size.getColumns() / 2;
                                        break;
                                    }
                                    case 19: {
                                        this.moveToMatch(!forward, true);
                                        break;
                                    }
                                    case 17: {
                                        this.moveToMatch(!forward, false);
                                        break;
                                    }
                                    case 18: {
                                        this.moveToMatch(forward, true);
                                        break;
                                    }
                                    case 16: {
                                        this.moveToMatch(forward, false);
                                        break;
                                    }
                                    case 20: {
                                        this.pattern = null;
                                        break;
                                    }
                                    case 28: {
                                        this.buffer.setLength(0);
                                        this.printLineNumbers = !this.printLineNumbers;
                                        this.message = this.printLineNumbers ? "Constantly display line numbers" : "Don't use line numbers";
                                        break;
                                    }
                                    case 32: {
                                        this.buffer.setLength(0);
                                        this.quiet = !this.quiet;
                                        this.veryQuiet = false;
                                        this.message = this.quiet ? "Ring the bell for errors but not at eof/bof" : "Ring the bell for errors AND at eof/bof";
                                        break;
                                    }
                                    case 33: {
                                        this.buffer.setLength(0);
                                        this.veryQuiet = !this.veryQuiet;
                                        this.quiet = false;
                                        this.message = this.veryQuiet ? "Never ring the bell" : "Ring the bell for errors AND at eof/bof";
                                        break;
                                    }
                                    case 29: {
                                        this.buffer.setLength(0);
                                        this.offsetInLine = 0;
                                        this.chopLongLines = !this.chopLongLines;
                                        this.message = this.chopLongLines ? "Chop long lines" : "Fold long lines";
                                        this.display.clear();
                                        break;
                                    }
                                    case 34: {
                                        this.ignoreCaseCond = !this.ignoreCaseCond;
                                        this.ignoreCaseAlways = false;
                                        this.message = this.ignoreCaseCond ? "Ignore case in searches" : "Case is significant in searches";
                                        break;
                                    }
                                    case 35: {
                                        this.ignoreCaseAlways = !this.ignoreCaseAlways;
                                        this.ignoreCaseCond = false;
                                        this.message = this.ignoreCaseAlways ? "Ignore case in searches and in patterns" : "Case is significant in searches";
                                        break;
                                    }
                                    case 36: {
                                        this.highlight = !this.highlight;
                                        this.message = "Highlight " + (this.highlight ? "enabled" : "disabled");
                                        break;
                                    }
                                    case 37: {
                                        this.addFile();
                                        break;
                                    }
                                    case 38: {
                                        int next = this.getStrictPositiveNumberInBuffer(1);
                                        if (this.sourceIdx < sources.size() - next) {
                                            SavedSourcePositions ssp = new SavedSourcePositions();
                                            this.sourceIdx += next;
                                            String newSource = sources.get(this.sourceIdx).getName();
                                            try {
                                                this.openSource();
                                            }
                                            catch (FileNotFoundException exp) {
                                                ssp.restore(newSource);
                                            }
                                            break;
                                        }
                                        this.message = "No next file";
                                        break;
                                    }
                                    case 39: {
                                        int prev = this.getStrictPositiveNumberInBuffer(1);
                                        if (this.sourceIdx > prev) {
                                            SavedSourcePositions ssp = new SavedSourcePositions(-1);
                                            this.sourceIdx -= prev;
                                            String newSource = sources.get(this.sourceIdx).getName();
                                            try {
                                                this.openSource();
                                            }
                                            catch (FileNotFoundException exp) {
                                                ssp.restore(newSource);
                                            }
                                            break;
                                        }
                                        this.message = "No previous file";
                                        break;
                                    }
                                    case 40: {
                                        int tofile = this.getStrictPositiveNumberInBuffer(1);
                                        if (tofile < sources.size()) {
                                            SavedSourcePositions ssp = new SavedSourcePositions(tofile < this.sourceIdx ? -1 : 0);
                                            this.sourceIdx = tofile;
                                            String newSource = sources.get(this.sourceIdx).getName();
                                            try {
                                                this.openSource();
                                            }
                                            catch (FileNotFoundException exp) {
                                                ssp.restore(newSource);
                                            }
                                            break;
                                        }
                                        this.message = "No such file";
                                        break;
                                    }
                                    case 41: {
                                        this.message = MESSAGE_FILE_INFO;
                                        break;
                                    }
                                    case 42: {
                                        if (sources.size() <= 2) break;
                                        sources.remove(this.sourceIdx);
                                        if (this.sourceIdx >= sources.size()) {
                                            this.sourceIdx = sources.size() - 1;
                                        }
                                        this.openSource();
                                        break;
                                    }
                                    case 14: {
                                        this.size.copy(this.terminal.getSize());
                                        this.display.clear();
                                        break;
                                    }
                                    case 15: {
                                        this.message = null;
                                        this.size.copy(this.terminal.getSize());
                                        this.display.clear();
                                        break;
                                    }
                                    case 0: {
                                        this.help();
                                    }
                                }
                                this.buffer.setLength(0);
                            }
                            if (this.quitAtFirstEof && this.nbEof > 0 || this.quitAtSecondEof && this.nbEof > 1) {
                                if (this.sourceIdx < sources.size() - 1) {
                                    ++this.sourceIdx;
                                    this.openSource();
                                } else {
                                    op = Operation.EXIT;
                                }
                            }
                            this.display(false);
                        } while (op != Operation.EXIT);
                        this.terminal.setAttributes(attr);
                        if (prevHandler == null) break block99;
                        this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
                    }
                    catch (InterruptedException interruptedException) {
                        this.terminal.setAttributes(attr);
                        if (prevHandler != null) {
                            this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
                        }
                        if (!this.noInit) {
                            this.terminal.puts(InfoCmp.Capability.exit_ca_mode, new Object[0]);
                        }
                        if (!this.noKeypad) {
                            this.terminal.puts(InfoCmp.Capability.keypad_local, new Object[0]);
                        }
                        this.terminal.writer().flush();
                        break block100;
                        catch (Throwable throwable) {
                            this.terminal.setAttributes(attr);
                            if (prevHandler != null) {
                                this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
                            }
                            if (!this.noInit) {
                                this.terminal.puts(InfoCmp.Capability.exit_ca_mode, new Object[0]);
                            }
                            if (!this.noKeypad) {
                                this.terminal.puts(InfoCmp.Capability.keypad_local, new Object[0]);
                            }
                            this.terminal.writer().flush();
                            throw throwable;
                        }
                    }
                }
                if (!this.noInit) {
                    this.terminal.puts(InfoCmp.Capability.exit_ca_mode, new Object[0]);
                }
                if (!this.noKeypad) {
                    this.terminal.puts(InfoCmp.Capability.keypad_local, new Object[0]);
                }
                this.terminal.writer().flush();
            }
            finally {
                if (this.reader != null) {
                    this.reader.close();
                }
                if (status != null) {
                    status.restore();
                }
                this.patternHistory.persist();
            }
        }
    }

    private void moveToMatch(boolean forward, boolean spanFiles) throws IOException {
        if (forward) {
            this.moveToNextMatch(spanFiles);
        } else {
            this.moveToPreviousMatch(spanFiles);
        }
    }

    private void addSource(String file) throws IOException {
        if (file.contains("*") || file.contains("?")) {
            for (Path p : Commands.findFiles(this.currentDir, file)) {
                this.sources.add(new Source.URLSource(p.toUri().toURL(), p.toString()));
            }
        } else {
            this.sources.add(new Source.URLSource(this.currentDir.resolve(file).toUri().toURL(), file));
        }
        this.sourceIdx = this.sources.size() - 1;
    }

    private void addFile() throws IOException, InterruptedException {
        int curPos;
        KeyMap<Operation> fileKeyMap = new KeyMap<Operation>();
        fileKeyMap.setUnicode(Operation.INSERT);
        for (char i = ' '; i < '\u0100'; i = (char)(i + '\u0001')) {
            fileKeyMap.bind(Operation.INSERT, (CharSequence)Character.toString(i));
        }
        fileKeyMap.bind(Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right), KeyMap.alt('l'));
        fileKeyMap.bind(Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left), KeyMap.alt('h'));
        fileKeyMap.bind(Operation.HOME, KeyMap.key(this.terminal, InfoCmp.Capability.key_home), KeyMap.alt('0'));
        fileKeyMap.bind(Operation.END, KeyMap.key(this.terminal, InfoCmp.Capability.key_end), KeyMap.alt('$'));
        fileKeyMap.bind(Operation.BACKSPACE, (CharSequence)KeyMap.del());
        fileKeyMap.bind(Operation.DELETE, (CharSequence)KeyMap.alt('x'));
        fileKeyMap.bind(Operation.DELETE_WORD, (CharSequence)KeyMap.alt('X'));
        fileKeyMap.bind(Operation.DELETE_LINE, (CharSequence)KeyMap.ctrl('U'));
        fileKeyMap.bind(Operation.ACCEPT, (CharSequence)"\r");
        SavedSourcePositions ssp = new SavedSourcePositions();
        this.message = null;
        this.buffer.append("Examine: ");
        int begPos = curPos = this.buffer.length();
        this.display(false, curPos);
        LineEditor lineEditor = new LineEditor(begPos);
        while (true) {
            Less.checkInterrupted();
            Operation op = (Operation)((Object)this.bindingReader.readBinding(fileKeyMap));
            if (op == Operation.ACCEPT) {
                String name = this.buffer.substring(begPos);
                this.addSource(name);
                try {
                    this.openSource();
                }
                catch (Exception exp) {
                    ssp.restore(name);
                }
                return;
            }
            if (op != null) {
                curPos = lineEditor.editBuffer(op, curPos);
            }
            if (curPos <= begPos) break;
            this.display(false, curPos);
        }
        this.buffer.setLength(0);
    }

    private boolean search() throws IOException, InterruptedException {
        int curPos;
        KeyMap<Operation> searchKeyMap = new KeyMap<Operation>();
        searchKeyMap.setUnicode(Operation.INSERT);
        for (char i = ' '; i < '\u0100'; i = (char)(i + '\u0001')) {
            searchKeyMap.bind(Operation.INSERT, (CharSequence)Character.toString(i));
        }
        searchKeyMap.bind(Operation.RIGHT, KeyMap.key(this.terminal, InfoCmp.Capability.key_right), KeyMap.alt('l'));
        searchKeyMap.bind(Operation.LEFT, KeyMap.key(this.terminal, InfoCmp.Capability.key_left), KeyMap.alt('h'));
        searchKeyMap.bind(Operation.NEXT_WORD, (CharSequence)KeyMap.alt('w'));
        searchKeyMap.bind(Operation.PREV_WORD, (CharSequence)KeyMap.alt('b'));
        searchKeyMap.bind(Operation.HOME, KeyMap.key(this.terminal, InfoCmp.Capability.key_home), KeyMap.alt('0'));
        searchKeyMap.bind(Operation.END, KeyMap.key(this.terminal, InfoCmp.Capability.key_end), KeyMap.alt('$'));
        searchKeyMap.bind(Operation.BACKSPACE, (CharSequence)KeyMap.del());
        searchKeyMap.bind(Operation.DELETE, (CharSequence)KeyMap.alt('x'));
        searchKeyMap.bind(Operation.DELETE_WORD, (CharSequence)KeyMap.alt('X'));
        searchKeyMap.bind(Operation.DELETE_LINE, (CharSequence)KeyMap.ctrl('U'));
        searchKeyMap.bind(Operation.UP, KeyMap.key(this.terminal, InfoCmp.Capability.key_up), KeyMap.alt('k'));
        searchKeyMap.bind(Operation.DOWN, KeyMap.key(this.terminal, InfoCmp.Capability.key_down), KeyMap.alt('j'));
        searchKeyMap.bind(Operation.ACCEPT, (CharSequence)"\r");
        boolean forward = true;
        this.message = null;
        int begPos = curPos = this.buffer.length();
        char type = this.buffer.charAt(0);
        String currentBuffer = this.buffer.toString();
        LineEditor lineEditor = new LineEditor(begPos);
        while (true) {
            Less.checkInterrupted();
            Operation op = (Operation)((Object)this.bindingReader.readBinding(searchKeyMap));
            switch (op.ordinal()) {
                case 56: {
                    this.buffer.setLength(0);
                    this.buffer.append(type);
                    this.buffer.append(this.patternHistory.up(currentBuffer.substring(1)));
                    curPos = this.buffer.length();
                    break;
                }
                case 57: {
                    this.buffer.setLength(0);
                    this.buffer.append(type);
                    this.buffer.append(this.patternHistory.down(currentBuffer.substring(1)));
                    curPos = this.buffer.length();
                    break;
                }
                case 55: {
                    try {
                        String _pattern = this.buffer.substring(1);
                        if (type == '&') {
                            this.displayPattern = !_pattern.isEmpty() ? _pattern : null;
                            this.getPattern(true);
                        } else {
                            this.pattern = _pattern;
                            this.getPattern();
                            if (type == '/') {
                                this.moveToNextMatch();
                            } else {
                                if (this.lines.size() - this.firstLineToDisplay <= this.size.getRows()) {
                                    this.firstLineToDisplay = this.lines.size();
                                } else {
                                    this.moveForward(this.size.getRows() - 1);
                                }
                                this.moveToPreviousMatch();
                                forward = false;
                            }
                        }
                        this.patternHistory.add(_pattern);
                        this.buffer.setLength(0);
                    }
                    catch (PatternSyntaxException e) {
                        String str = e.getMessage();
                        if (str.indexOf(10) > 0) {
                            str = str.substring(0, str.indexOf(10));
                        }
                        if (type == '&') {
                            this.displayPattern = null;
                        } else {
                            this.pattern = null;
                        }
                        this.buffer.setLength(0);
                        this.message = "Invalid pattern: " + str + " (Press a key)";
                        this.display(false);
                        this.terminal.reader().read();
                        this.message = null;
                    }
                    return forward;
                }
                default: {
                    curPos = lineEditor.editBuffer(op, curPos);
                    currentBuffer = this.buffer.toString();
                }
            }
            if (curPos < begPos) {
                this.buffer.setLength(0);
                return forward;
            }
            this.display(false, curPos);
        }
    }

    private void help() throws IOException {
        SavedSourcePositions ssp = new SavedSourcePositions();
        this.printLineNumbers = false;
        this.sourceIdx = 0;
        try {
            Operation op;
            this.openSource();
            this.display(false);
            do {
                Less.checkInterrupted();
                op = this.bindingReader.readBinding(this.keys, null, false);
                if (op != null) {
                    switch (op.ordinal()) {
                        case 4: {
                            this.moveForward(this.getStrictPositiveNumberInBuffer(this.window));
                            break;
                        }
                        case 5: {
                            this.moveBackward(this.getStrictPositiveNumberInBuffer(this.window));
                        }
                    }
                }
                this.display(false);
            } while (op != Operation.EXIT);
        }
        catch (IOException | InterruptedException exception) {
        }
        finally {
            ssp.restore(null);
        }
    }

    protected void openSource() throws IOException {
        boolean open;
        boolean wasOpen = false;
        if (this.reader != null) {
            this.reader.close();
            wasOpen = true;
        }
        boolean displayMessage = false;
        do {
            AttributedStringBuilder asb;
            Source source = this.sources.get(this.sourceIdx);
            try {
                InputStream in = source.read();
                this.message = this.sources.size() == 2 || this.sourceIdx == 0 ? source.getName() : source.getName() + " (file " + this.sourceIdx + " of " + (this.sources.size() - 1) + ")";
                this.reader = new BufferedReader(new InputStreamReader(new InterruptibleInputStream(in)));
                this.firstLineInMemory = 0;
                this.lines = new ArrayList<AttributedString>();
                this.firstLineToDisplay = 0;
                this.firstColumnToDisplay = 0;
                this.offsetInLine = 0;
                this.display.clear();
                this.syntaxHighlighter = this.sourceIdx == 0 ? SyntaxHighlighter.build(this.syntaxFiles, null, "none") : SyntaxHighlighter.build(this.syntaxFiles, source.getName(), this.syntaxName, this.nanorcIgnoreErrors);
                open = true;
                if (!displayMessage) continue;
                asb = new AttributedStringBuilder();
                asb.style(AttributedStyle.INVERSE);
                asb.append(source.getName()).append(" (press RETURN)");
                asb.toAttributedString().println(this.terminal);
                this.terminal.writer().flush();
                this.terminal.reader().read();
            }
            catch (FileNotFoundException exp) {
                this.sources.remove(this.sourceIdx);
                if (this.sourceIdx > this.sources.size() - 1) {
                    this.sourceIdx = this.sources.size() - 1;
                }
                if (wasOpen) {
                    throw exp;
                }
                asb = new AttributedStringBuilder();
                asb.append(source.getName()).append(" not found!");
                asb.toAttributedString().println(this.terminal);
                this.terminal.writer().flush();
                open = false;
                displayMessage = true;
            }
        } while (!open && this.sourceIdx > 0);
        if (!open) {
            throw new FileNotFoundException();
        }
    }

    void moveTo(int lineNum) throws IOException {
        AttributedString line = this.getLine(lineNum);
        if (line != null) {
            this.display.clear();
            if (this.firstLineInMemory > lineNum) {
                this.openSource();
            }
            this.firstLineToDisplay = lineNum;
            this.offsetInLine = 0;
        } else {
            this.message = "Cannot seek to line number " + (lineNum + 1);
        }
    }

    private void moveToNextMatch() throws IOException {
        this.moveToNextMatch(false);
    }

    private void moveToNextMatch(boolean spanFiles) throws IOException {
        Pattern compiled = this.getPattern();
        Pattern dpCompiled = this.getPattern(true);
        if (compiled != null) {
            AttributedString line;
            int lineNumber = this.firstLineToDisplay + 1;
            while ((line = this.getLine(lineNumber)) != null) {
                if (this.toBeDisplayed(line, dpCompiled) && compiled.matcher(line).find()) {
                    this.display.clear();
                    this.firstLineToDisplay = lineNumber;
                    this.offsetInLine = 0;
                    return;
                }
                ++lineNumber;
            }
        }
        if (spanFiles) {
            if (this.sourceIdx < this.sources.size() - 1) {
                SavedSourcePositions ssp = new SavedSourcePositions();
                String newSource = this.sources.get(++this.sourceIdx).getName();
                try {
                    this.openSource();
                    this.moveToNextMatch(true);
                }
                catch (FileNotFoundException exp) {
                    ssp.restore(newSource);
                }
            } else {
                this.message = "Pattern not found";
            }
        } else {
            this.message = "Pattern not found";
        }
    }

    private void moveToPreviousMatch() throws IOException {
        this.moveToPreviousMatch(false);
    }

    private void moveToPreviousMatch(boolean spanFiles) throws IOException {
        Pattern compiled = this.getPattern();
        Pattern dpCompiled = this.getPattern(true);
        if (compiled != null) {
            AttributedString line;
            for (int lineNumber = this.firstLineToDisplay - 1; lineNumber >= this.firstLineInMemory && (line = this.getLine(lineNumber)) != null; --lineNumber) {
                if (!this.toBeDisplayed(line, dpCompiled) || !compiled.matcher(line).find()) continue;
                this.display.clear();
                this.firstLineToDisplay = lineNumber;
                this.offsetInLine = 0;
                return;
            }
        }
        if (spanFiles) {
            if (this.sourceIdx > 1) {
                SavedSourcePositions ssp = new SavedSourcePositions(-1);
                String newSource = this.sources.get(--this.sourceIdx).getName();
                try {
                    this.openSource();
                    this.moveTo(Integer.MAX_VALUE);
                    this.moveToPreviousMatch(true);
                }
                catch (FileNotFoundException exp) {
                    ssp.restore(newSource);
                }
            } else {
                this.message = "Pattern not found";
            }
        } else {
            this.message = "Pattern not found";
        }
    }

    private String printable(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\u001b') {
                sb.append("ESC");
                continue;
            }
            if (c < ' ') {
                sb.append('^').append((char)(c + 64));
                continue;
            }
            if (c < '\u0080') {
                sb.append(c);
                continue;
            }
            sb.append('\\').append(String.format("%03o", c));
        }
        return sb.toString();
    }

    void moveForward(int lines) throws IOException {
        boolean doOffsets;
        Pattern dpCompiled = this.getPattern(true);
        int width = this.size.getColumns() - (this.printLineNumbers ? 8 : 0);
        int height = this.size.getRows();
        boolean bl = doOffsets = this.firstColumnToDisplay == 0 && !this.chopLongLines;
        if (lines >= this.size.getRows() - 1) {
            this.display.clear();
        }
        if (lines == Integer.MAX_VALUE) {
            this.moveTo(Integer.MAX_VALUE);
            this.firstLineToDisplay = height - 1;
            for (int l = 0; l < height - 1; ++l) {
                this.firstLineToDisplay = this.prevLine2display(this.firstLineToDisplay, dpCompiled).getU();
            }
        }
        while (--lines >= 0) {
            int lastLineToDisplay = this.firstLineToDisplay;
            if (!doOffsets) {
                for (int l = 0; l < height - 1; ++l) {
                    lastLineToDisplay = this.nextLine2display(lastLineToDisplay, dpCompiled).getU();
                }
            } else {
                int off = this.offsetInLine;
                for (int l = 0; l < height - 1; ++l) {
                    Pair<Integer, AttributedString> nextLine = this.nextLine2display(lastLineToDisplay, dpCompiled);
                    AttributedString line = nextLine.getV();
                    if (line == null) {
                        lastLineToDisplay = nextLine.getU();
                        break;
                    }
                    if (line.columnLength() > off + width) {
                        off += width;
                        continue;
                    }
                    off = 0;
                    lastLineToDisplay = nextLine.getU();
                }
            }
            if (this.getLine(lastLineToDisplay) == null) {
                this.eof();
                return;
            }
            Pair<Integer, AttributedString> nextLine = this.nextLine2display(this.firstLineToDisplay, dpCompiled);
            AttributedString line = nextLine.getV();
            if (doOffsets && line.columnLength() > width + this.offsetInLine) {
                this.offsetInLine += width;
                continue;
            }
            this.offsetInLine = 0;
            this.firstLineToDisplay = nextLine.getU();
        }
    }

    void moveBackward(int lines) throws IOException {
        Pattern dpCompiled = this.getPattern(true);
        int width = this.size.getColumns() - (this.printLineNumbers ? 8 : 0);
        if (lines >= this.size.getRows() - 1) {
            this.display.clear();
        }
        while (--lines >= 0) {
            if (this.offsetInLine > 0) {
                this.offsetInLine = Math.max(0, this.offsetInLine - width);
                continue;
            }
            if (this.firstLineInMemory < this.firstLineToDisplay) {
                Pair<Integer, AttributedString> prevLine = this.prevLine2display(this.firstLineToDisplay, dpCompiled);
                this.firstLineToDisplay = prevLine.getU();
                AttributedString line = prevLine.getV();
                if (line == null || this.firstColumnToDisplay != 0 || this.chopLongLines) continue;
                int length = line.columnLength();
                this.offsetInLine = length - length % width;
                continue;
            }
            this.bof();
            return;
        }
    }

    private void eof() {
        ++this.nbEof;
        this.message = this.sourceIdx > 0 && this.sourceIdx < this.sources.size() - 1 ? "(END) - Next: " + this.sources.get(this.sourceIdx + 1).getName() : "(END)";
        if (!(this.quiet || this.veryQuiet || this.quitAtFirstEof || this.quitAtSecondEof)) {
            this.terminal.puts(InfoCmp.Capability.bell, new Object[0]);
            this.terminal.writer().flush();
        }
    }

    private void bof() {
        if (!this.quiet && !this.veryQuiet) {
            this.terminal.puts(InfoCmp.Capability.bell, new Object[0]);
            this.terminal.writer().flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getStrictPositiveNumberInBuffer(int def) {
        try {
            int n = Integer.parseInt(this.buffer.toString());
            int n2 = n > 0 ? n : def;
            return n2;
        }
        catch (NumberFormatException e) {
            int n = def;
            return n;
        }
        finally {
            this.buffer.setLength(0);
        }
    }

    private Pair<Integer, AttributedString> nextLine2display(int line, Pattern dpCompiled) throws IOException {
        AttributedString curLine;
        while (!this.toBeDisplayed(curLine = this.getLine(line++), dpCompiled)) {
        }
        return new Pair<Integer, AttributedString>(line, curLine);
    }

    private Pair<Integer, AttributedString> prevLine2display(int line, Pattern dpCompiled) throws IOException {
        AttributedString curLine;
        do {
            curLine = this.getLine(line--);
        } while (line > 0 && !this.toBeDisplayed(curLine, dpCompiled));
        if (line == 0 && !this.toBeDisplayed(curLine, dpCompiled)) {
            curLine = null;
        }
        return new Pair<Integer, AttributedString>(line, curLine);
    }

    private boolean toBeDisplayed(AttributedString curLine, Pattern dpCompiled) {
        return curLine == null || dpCompiled == null || this.sourceIdx == 0 || dpCompiled.matcher(curLine).find();
    }

    synchronized boolean display(boolean oneScreen) throws IOException {
        return this.display(oneScreen, null);
    }

    synchronized boolean display(boolean oneScreen, Integer curPos) throws IOException {
        ArrayList<AttributedString> newLines = new ArrayList<AttributedString>();
        int width = this.size.getColumns() - (this.printLineNumbers ? 8 : 0);
        int height = this.size.getRows();
        int inputLine = this.firstLineToDisplay;
        AttributedCharSequence curLine = null;
        Pattern compiled = this.getPattern();
        Pattern dpCompiled = this.getPattern(true);
        boolean fitOnOneScreen = false;
        boolean eof = false;
        if (this.highlight) {
            this.syntaxHighlighter.reset();
            for (int i = Math.max(0, inputLine - height); i < inputLine; ++i) {
                this.syntaxHighlighter.highlight(this.getLine(i));
            }
        }
        for (int terminalLine = 0; terminalLine < height - 1; ++terminalLine) {
            AttributedString toDisplay;
            if (curLine == null) {
                Pair<Integer, AttributedString> nextLine = this.nextLine2display(inputLine, dpCompiled);
                inputLine = nextLine.getU();
                curLine = nextLine.getV();
                if (curLine == null) {
                    if (oneScreen) {
                        fitOnOneScreen = true;
                        break;
                    }
                    eof = true;
                    curLine = new AttributedString("~");
                } else if (this.highlight) {
                    curLine = this.syntaxHighlighter.highlight((AttributedString)curLine);
                }
                if (compiled != null) {
                    curLine = ((AttributedString)curLine).styleMatches(compiled, AttributedStyle.DEFAULT.inverse());
                }
            }
            if (this.firstColumnToDisplay > 0 || this.chopLongLines) {
                int off = this.firstColumnToDisplay;
                if (terminalLine == 0 && this.offsetInLine > 0) {
                    off = Math.max(this.offsetInLine, off);
                }
                toDisplay = curLine.columnSubSequence(off, off + width);
                curLine = null;
            } else {
                if (terminalLine == 0 && this.offsetInLine > 0) {
                    curLine = curLine.columnSubSequence(this.offsetInLine, Integer.MAX_VALUE);
                }
                toDisplay = curLine.columnSubSequence(0, width);
                if (((AttributedString)(curLine = curLine.columnSubSequence(width, Integer.MAX_VALUE))).length() == 0) {
                    curLine = null;
                }
            }
            if (this.printLineNumbers && !eof) {
                AttributedStringBuilder sb = new AttributedStringBuilder();
                sb.append(String.format("%7d ", inputLine));
                sb.append(toDisplay);
                newLines.add(sb.toAttributedString());
                continue;
            }
            newLines.add(toDisplay);
        }
        if (oneScreen) {
            if (fitOnOneScreen) {
                newLines.forEach(l -> l.println(this.terminal));
            }
            return fitOnOneScreen;
        }
        AttributedStringBuilder msg = new AttributedStringBuilder();
        if (MESSAGE_FILE_INFO.equals(this.message)) {
            Source source = this.sources.get(this.sourceIdx);
            Long allLines = source.lines();
            this.message = source.getName() + (this.sources.size() > 2 ? " (file " + this.sourceIdx + " of " + (this.sources.size() - 1) + ")" : "") + " lines " + (this.firstLineToDisplay + 1) + "-" + inputLine + "/" + (allLines != null ? allLines : (long)this.lines.size()) + (eof ? " (END)" : "");
        }
        if (this.buffer.length() > 0) {
            msg.append(" ").append(this.buffer);
        } else if (!this.bindingReader.getCurrentBuffer().isEmpty() && this.terminal.reader().peek(1L) == -2) {
            msg.append(" ").append(this.printable(this.bindingReader.getCurrentBuffer()));
        } else if (this.message != null) {
            msg.style(AttributedStyle.INVERSE);
            msg.append(this.message);
            msg.style(AttributedStyle.INVERSE.inverseOff());
        } else if (this.displayPattern != null) {
            msg.append("&");
        } else {
            msg.append(":");
        }
        newLines.add(msg.toAttributedString());
        this.display.resize(this.size.getRows(), this.size.getColumns());
        if (curPos == null) {
            this.display.update(newLines, -1);
        } else {
            this.display.update(newLines, this.size.cursorPos(this.size.getRows() - 1, curPos + 1));
        }
        return false;
    }

    private Pattern getPattern() {
        return this.getPattern(false);
    }

    private Pattern getPattern(boolean doDisplayPattern) {
        String _pattern;
        Pattern compiled = null;
        String string = _pattern = doDisplayPattern ? this.displayPattern : this.pattern;
        if (_pattern != null) {
            boolean insensitive = this.ignoreCaseAlways || this.ignoreCaseCond && _pattern.toLowerCase().equals(_pattern);
            compiled = Pattern.compile("(" + _pattern + ")", insensitive ? 66 : 0);
        }
        return compiled;
    }

    AttributedString getLine(int line) throws IOException {
        String str;
        while (line >= this.lines.size() && (str = this.reader.readLine()) != null) {
            this.lines.add(AttributedString.fromAnsi(str, this.tabs));
        }
        if (line < this.lines.size()) {
            return this.lines.get(line);
        }
        return null;
    }

    public static void checkInterrupted() throws InterruptedException {
        Thread.yield();
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private void bindKeys(KeyMap<Operation> map) {
        map.bind(Operation.HELP, "h", "H");
        map.bind(Operation.EXIT, "q", ":q", "Q", ":Q", "ZZ");
        map.bind(Operation.FORWARD_ONE_LINE, "e", KeyMap.ctrl('E'), "j", KeyMap.ctrl('N'), "\r", KeyMap.key(this.terminal, InfoCmp.Capability.key_down));
        map.bind(Operation.BACKWARD_ONE_LINE, "y", KeyMap.ctrl('Y'), "k", KeyMap.ctrl('K'), KeyMap.ctrl('P'), KeyMap.key(this.terminal, InfoCmp.Capability.key_up));
        map.bind(Operation.FORWARD_ONE_WINDOW_OR_LINES, "f", KeyMap.ctrl('F'), KeyMap.ctrl('V'), " ", KeyMap.key(this.terminal, InfoCmp.Capability.key_npage));
        map.bind(Operation.BACKWARD_ONE_WINDOW_OR_LINES, "b", KeyMap.ctrl('B'), KeyMap.alt('v'), KeyMap.key(this.terminal, InfoCmp.Capability.key_ppage));
        map.bind(Operation.FORWARD_ONE_WINDOW_AND_SET, (CharSequence)"z");
        map.bind(Operation.BACKWARD_ONE_WINDOW_AND_SET, (CharSequence)"w");
        map.bind(Operation.FORWARD_ONE_WINDOW_NO_STOP, (CharSequence)KeyMap.alt(' '));
        map.bind(Operation.FORWARD_HALF_WINDOW_AND_SET, "d", KeyMap.ctrl('D'));
        map.bind(Operation.BACKWARD_HALF_WINDOW_AND_SET, "u", KeyMap.ctrl('U'));
        map.bind(Operation.RIGHT_ONE_HALF_SCREEN, KeyMap.alt(')'), KeyMap.key(this.terminal, InfoCmp.Capability.key_right));
        map.bind(Operation.LEFT_ONE_HALF_SCREEN, KeyMap.alt('('), KeyMap.key(this.terminal, InfoCmp.Capability.key_left));
        map.bind(Operation.FORWARD_FOREVER, (CharSequence)"F");
        map.bind(Operation.REPAINT, "r", KeyMap.ctrl('R'), KeyMap.ctrl('L'));
        map.bind(Operation.REPAINT_AND_DISCARD, (CharSequence)"R");
        map.bind(Operation.REPEAT_SEARCH_FORWARD, (CharSequence)"n");
        map.bind(Operation.REPEAT_SEARCH_BACKWARD, (CharSequence)"N");
        map.bind(Operation.REPEAT_SEARCH_FORWARD_SPAN_FILES, (CharSequence)KeyMap.alt('n'));
        map.bind(Operation.REPEAT_SEARCH_BACKWARD_SPAN_FILES, (CharSequence)KeyMap.alt('N'));
        map.bind(Operation.UNDO_SEARCH, (CharSequence)KeyMap.alt('u'));
        map.bind(Operation.GO_TO_FIRST_LINE_OR_N, "g", "<", KeyMap.alt('<'));
        map.bind(Operation.GO_TO_LAST_LINE_OR_N, "G", ">", KeyMap.alt('>'));
        map.bind(Operation.HOME, (CharSequence)KeyMap.key(this.terminal, InfoCmp.Capability.key_home));
        map.bind(Operation.END, (CharSequence)KeyMap.key(this.terminal, InfoCmp.Capability.key_end));
        map.bind(Operation.ADD_FILE, ":e", KeyMap.ctrl('X') + KeyMap.ctrl('V'));
        map.bind(Operation.NEXT_FILE, (CharSequence)":n");
        map.bind(Operation.PREV_FILE, (CharSequence)":p");
        map.bind(Operation.GOTO_FILE, (CharSequence)":x");
        map.bind(Operation.INFO_FILE, "=", ":f", KeyMap.ctrl('G'));
        map.bind(Operation.DELETE_FILE, (CharSequence)":d");
        map.bind(Operation.BACKSPACE, (CharSequence)KeyMap.del());
        "-/0123456789?&".chars().forEach(c -> map.bind(Operation.CHAR, (CharSequence)Character.toString((char)c)));
    }

    protected static enum Operation {
        HELP,
        EXIT,
        FORWARD_ONE_LINE,
        BACKWARD_ONE_LINE,
        FORWARD_ONE_WINDOW_OR_LINES,
        BACKWARD_ONE_WINDOW_OR_LINES,
        FORWARD_ONE_WINDOW_AND_SET,
        BACKWARD_ONE_WINDOW_AND_SET,
        FORWARD_ONE_WINDOW_NO_STOP,
        FORWARD_HALF_WINDOW_AND_SET,
        BACKWARD_HALF_WINDOW_AND_SET,
        LEFT_ONE_HALF_SCREEN,
        RIGHT_ONE_HALF_SCREEN,
        FORWARD_FOREVER,
        REPAINT,
        REPAINT_AND_DISCARD,
        REPEAT_SEARCH_FORWARD,
        REPEAT_SEARCH_BACKWARD,
        REPEAT_SEARCH_FORWARD_SPAN_FILES,
        REPEAT_SEARCH_BACKWARD_SPAN_FILES,
        UNDO_SEARCH,
        GO_TO_FIRST_LINE_OR_N,
        GO_TO_LAST_LINE_OR_N,
        GO_TO_PERCENT_OR_N,
        GO_TO_NEXT_TAG,
        GO_TO_PREVIOUS_TAG,
        FIND_CLOSE_BRACKET,
        FIND_OPEN_BRACKET,
        OPT_PRINT_LINES,
        OPT_CHOP_LONG_LINES,
        OPT_QUIT_AT_FIRST_EOF,
        OPT_QUIT_AT_SECOND_EOF,
        OPT_QUIET,
        OPT_VERY_QUIET,
        OPT_IGNORE_CASE_COND,
        OPT_IGNORE_CASE_ALWAYS,
        OPT_SYNTAX_HIGHLIGHT,
        ADD_FILE,
        NEXT_FILE,
        PREV_FILE,
        GOTO_FILE,
        INFO_FILE,
        DELETE_FILE,
        CHAR,
        INSERT,
        RIGHT,
        LEFT,
        NEXT_WORD,
        PREV_WORD,
        HOME,
        END,
        BACKSPACE,
        DELETE,
        DELETE_WORD,
        DELETE_LINE,
        ACCEPT,
        UP,
        DOWN;

    }

    private class SavedSourcePositions {
        int saveSourceIdx;
        int saveFirstLineToDisplay;
        int saveFirstColumnToDisplay;
        int saveOffsetInLine;
        boolean savePrintLineNumbers;

        public SavedSourcePositions() {
            this(0);
        }

        public SavedSourcePositions(int dec) {
            this.saveSourceIdx = Less.this.sourceIdx + dec;
            this.saveFirstLineToDisplay = Less.this.firstLineToDisplay;
            this.saveFirstColumnToDisplay = Less.this.firstColumnToDisplay;
            this.saveOffsetInLine = Less.this.offsetInLine;
            this.savePrintLineNumbers = Less.this.printLineNumbers;
        }

        public void restore(String failingSource) throws IOException {
            Less.this.sourceIdx = this.saveSourceIdx;
            Less.this.openSource();
            Less.this.firstLineToDisplay = this.saveFirstLineToDisplay;
            Less.this.firstColumnToDisplay = this.saveFirstColumnToDisplay;
            Less.this.offsetInLine = this.saveOffsetInLine;
            Less.this.printLineNumbers = this.savePrintLineNumbers;
            if (failingSource != null) {
                Less.this.message = failingSource + " not found!";
            }
        }
    }

    private class LineEditor {
        private final int begPos;

        public LineEditor(int begPos) {
            this.begPos = begPos;
        }

        public int editBuffer(Operation op, int curPos) {
            block0 : switch (op.ordinal()) {
                case 44: {
                    Less.this.buffer.insert(curPos++, Less.this.bindingReader.getLastBinding());
                    break;
                }
                case 51: {
                    if (curPos <= this.begPos - 1) break;
                    Less.this.buffer.deleteCharAt(--curPos);
                    break;
                }
                case 47: {
                    int newPos = Less.this.buffer.length();
                    for (int i = curPos; i < Less.this.buffer.length(); ++i) {
                        if (Less.this.buffer.charAt(i) != ' ') continue;
                        newPos = i + 1;
                        break;
                    }
                    curPos = newPos;
                    break;
                }
                case 48: {
                    int newPos = this.begPos;
                    for (int i = curPos - 2; i > this.begPos; --i) {
                        if (Less.this.buffer.charAt(i) != ' ') continue;
                        newPos = i + 1;
                        break;
                    }
                    curPos = newPos;
                    break;
                }
                case 49: {
                    curPos = this.begPos;
                    break;
                }
                case 50: {
                    curPos = Less.this.buffer.length();
                    break;
                }
                case 52: {
                    if (curPos < this.begPos || curPos >= Less.this.buffer.length()) break;
                    Less.this.buffer.deleteCharAt(curPos);
                    break;
                }
                case 53: {
                    while (curPos < Less.this.buffer.length() && Less.this.buffer.charAt(curPos) != ' ') {
                        Less.this.buffer.deleteCharAt(curPos);
                    }
                    while (curPos - 1 >= this.begPos) {
                        if (Less.this.buffer.charAt(curPos - 1) != ' ') {
                            Less.this.buffer.deleteCharAt(--curPos);
                            continue;
                        }
                        Less.this.buffer.deleteCharAt(--curPos);
                        break block0;
                    }
                    break;
                }
                case 54: {
                    Less.this.buffer.setLength(this.begPos);
                    curPos = 1;
                    break;
                }
                case 46: {
                    if (curPos <= this.begPos) break;
                    --curPos;
                    break;
                }
                case 45: {
                    if (curPos >= Less.this.buffer.length()) break;
                    ++curPos;
                }
            }
            return curPos;
        }
    }

    static class InterruptibleInputStream
    extends FilterInputStream {
        InterruptibleInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedIOException();
            }
            return super.read(b, off, len);
        }
    }

    static class Pair<U, V> {
        final U u;
        final V v;

        public Pair(U u, V v) {
            this.u = u;
            this.v = v;
        }

        public U getU() {
            return this.u;
        }

        public V getV() {
            return this.v;
        }
    }
}

