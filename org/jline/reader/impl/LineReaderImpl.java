/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.CompletionMatcher;
import org.jline.reader.EOFError;
import org.jline.reader.Editor;
import org.jline.reader.EndOfFileException;
import org.jline.reader.Expander;
import org.jline.reader.Highlighter;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.SyntaxError;
import org.jline.reader.UserInterruptException;
import org.jline.reader.Widget;
import org.jline.reader.impl.BufferImpl;
import org.jline.reader.impl.CompletionMatcherImpl;
import org.jline.reader.impl.DefaultExpander;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.InputRC;
import org.jline.reader.impl.KillRing;
import org.jline.reader.impl.ReaderUtils;
import org.jline.reader.impl.SimpleMaskingCallback;
import org.jline.reader.impl.UndoTree;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.MouseSupport;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Curses;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.Status;
import org.jline.utils.StyleResolver;
import org.jline.utils.WCWidth;

public class LineReaderImpl
implements LineReader,
Flushable {
    public static final char NULL_MASK = '\u0000';
    @Deprecated
    public static final int TAB_WIDTH = 4;
    public static final int DEFAULT_TAB_WIDTH = 4;
    public static final String DEFAULT_WORDCHARS = "*?_-.[]~=/&;!#$%^(){}<>";
    public static final String DEFAULT_REMOVE_SUFFIX_CHARS = " \t\n;&|";
    public static final String DEFAULT_COMMENT_BEGIN = "#";
    public static final String DEFAULT_SEARCH_TERMINATORS = "\u001b\n";
    public static final String DEFAULT_BELL_STYLE = "";
    public static final int DEFAULT_LIST_MAX = 100;
    public static final int DEFAULT_MENU_LIST_MAX = Integer.MAX_VALUE;
    public static final int DEFAULT_ERRORS = 2;
    public static final long DEFAULT_BLINK_MATCHING_PAREN = 500L;
    public static final long DEFAULT_AMBIGUOUS_BINDING = 1000L;
    public static final String DEFAULT_SECONDARY_PROMPT_PATTERN = "%M> ";
    public static final String DEFAULT_OTHERS_GROUP_NAME = "others";
    public static final String DEFAULT_ORIGINAL_GROUP_NAME = "original";
    public static final String DEFAULT_COMPLETION_STYLE_STARTING = "fg:cyan";
    public static final String DEFAULT_COMPLETION_STYLE_DESCRIPTION = "fg:bright-black";
    public static final String DEFAULT_COMPLETION_STYLE_GROUP = "fg:bright-magenta,bold";
    public static final String DEFAULT_COMPLETION_STYLE_SELECTION = "inverse";
    public static final String DEFAULT_COMPLETION_STYLE_BACKGROUND = "bg:default";
    public static final String DEFAULT_COMPLETION_STYLE_LIST_STARTING = "fg:cyan";
    public static final String DEFAULT_COMPLETION_STYLE_LIST_DESCRIPTION = "fg:bright-black";
    public static final String DEFAULT_COMPLETION_STYLE_LIST_GROUP = "fg:black,bold";
    public static final String DEFAULT_COMPLETION_STYLE_LIST_SELECTION = "inverse";
    public static final String DEFAULT_COMPLETION_STYLE_LIST_BACKGROUND = "bg:bright-magenta";
    public static final int DEFAULT_INDENTATION = 0;
    public static final int DEFAULT_FEATURES_MAX_BUFFER_SIZE = 1000;
    public static final int DEFAULT_SUGGESTIONS_MIN_BUFFER_SIZE = 1;
    public static final String DEFAULT_SYSTEM_PROPERTY_PREFIX = "org.jline.reader.props.";
    private static final int MIN_ROWS = 3;
    public static final String BRACKETED_PASTE_ON = "\u001b[?2004h";
    public static final String BRACKETED_PASTE_OFF = "\u001b[?2004l";
    public static final String BRACKETED_PASTE_BEGIN = "\u001b[200~";
    public static final String BRACKETED_PASTE_END = "\u001b[201~";
    public static final String FOCUS_IN_SEQ = "\u001b[I";
    public static final String FOCUS_OUT_SEQ = "\u001b[O";
    public static final int DEFAULT_MAX_REPEAT_COUNT = 9999;
    protected final Terminal terminal;
    protected final String appName;
    protected final Map<String, KeyMap<Binding>> keyMaps;
    protected final Map<String, Object> variables;
    protected History history = new DefaultHistory();
    protected Completer completer = null;
    protected Highlighter highlighter = new DefaultHighlighter();
    protected Parser parser = new DefaultParser();
    protected Expander expander = new DefaultExpander();
    protected CompletionMatcher completionMatcher = new CompletionMatcherImpl();
    protected final Map<LineReader.Option, Boolean> options = new HashMap<LineReader.Option, Boolean>();
    protected Thread maskThread = null;
    protected final Buffer buf = new BufferImpl();
    protected String tailTip = "";
    protected LineReader.SuggestionType autosuggestion = LineReader.SuggestionType.NONE;
    protected final Size size = new Size();
    protected AttributedString prompt = AttributedString.EMPTY;
    protected AttributedString rightPrompt = AttributedString.EMPTY;
    protected MaskingCallback maskingCallback;
    protected Map<Integer, String> modifiedHistory = new HashMap<Integer, String>();
    protected Buffer historyBuffer = null;
    protected CharSequence searchBuffer;
    protected StringBuffer searchTerm = null;
    protected boolean searchFailing;
    protected boolean searchBackward;
    protected int searchIndex = -1;
    protected boolean doAutosuggestion;
    protected final BindingReader bindingReader;
    protected int findChar;
    protected int findDir;
    protected int findTailAdd;
    private int searchDir;
    private String searchString;
    protected int regionMark;
    protected LineReader.RegionType regionActive;
    private boolean forceChar;
    private boolean forceLine;
    protected String yankBuffer = "";
    protected ViMoveMode viMoveMode = ViMoveMode.NORMAL;
    protected KillRing killRing = new KillRing();
    protected UndoTree<Buffer> undo;
    protected boolean isUndo;
    protected final ReentrantLock lock = new ReentrantLock();
    protected State state = State.DONE;
    protected final AtomicBoolean startedReading = new AtomicBoolean();
    protected boolean reading;
    protected Supplier<AttributedString> post;
    protected Map<String, Widget> builtinWidgets;
    protected Map<String, Widget> widgets;
    protected int count;
    protected int mult;
    protected int universal = 4;
    protected int repeatCount;
    protected boolean isArgDigit;
    protected ParsedLine parsedLine;
    protected boolean skipRedisplay;
    protected Display display;
    protected boolean overTyping = false;
    protected String keyMap;
    protected int smallTerminalOffset = 0;
    protected boolean nextCommandFromHistory = false;
    protected int nextHistoryId = -1;
    protected List<String> commandsBuffer = new ArrayList<String>();
    protected int candidateStartPosition = 0;
    protected String alternateIn;
    protected String alternateOut;
    protected int currentLine;
    private static final String DESC_PREFIX = "(";
    private static final String DESC_SUFFIX = ")";
    private static final int MARGIN_BETWEEN_DISPLAY_AND_DESC = 1;
    private static final int MARGIN_BETWEEN_COLUMNS = 3;
    private static final int MENU_LIST_WIDTH = 25;

    public LineReaderImpl(Terminal terminal) throws IOException {
        this(terminal, terminal.getName(), null);
    }

    public LineReaderImpl(Terminal terminal, String appName) throws IOException {
        this(terminal, appName, null);
    }

    public LineReaderImpl(Terminal terminal, String appName, Map<String, Object> variables) {
        Path inputRcPath;
        Objects.requireNonNull(terminal, "terminal can not be null");
        this.terminal = terminal;
        if (appName == null) {
            appName = "JLine";
        }
        this.appName = appName;
        this.variables = variables != null ? variables : new HashMap<String, Object>();
        String prefix = this.getString("system-property-prefix", DEFAULT_SYSTEM_PROPERTY_PREFIX);
        if (prefix != null) {
            Properties sysProps = System.getProperties();
            for (String s : sysProps.stringPropertyNames()) {
                if (!s.startsWith(prefix)) continue;
                String key = s.substring(prefix.length());
                InputRC.setVar(this, key, sysProps.getProperty(s));
            }
        }
        this.keyMaps = this.defaultKeyMaps();
        if (!Boolean.getBoolean("org.jline.utils.disableAlternateCharset")) {
            this.alternateIn = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.enter_alt_charset_mode), new Object[0]);
            this.alternateOut = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.exit_alt_charset_mode), new Object[0]);
        }
        this.undo = new UndoTree<Buffer>(this::setBuffer);
        this.builtinWidgets = this.builtinWidgets();
        this.widgets = new HashMap<String, Widget>(this.builtinWidgets);
        this.bindingReader = new BindingReader(terminal.reader());
        String inputRc = this.getString("input-rc-file-name", null);
        if (inputRc != null && Files.exists(inputRcPath = Paths.get(inputRc, new String[0]), new LinkOption[0])) {
            try (InputStream is = Files.newInputStream(inputRcPath, new OpenOption[0]);){
                InputRC.configure((LineReader)this, is);
            }
            catch (Exception e) {
                Log.debug("Error reading inputRc config file: ", inputRc, e);
            }
        }
        this.doDisplay();
    }

    @Override
    public Terminal getTerminal() {
        return this.terminal;
    }

    @Override
    public String getAppName() {
        return this.appName;
    }

    @Override
    public Map<String, KeyMap<Binding>> getKeyMaps() {
        return this.keyMaps;
    }

    @Override
    public KeyMap<Binding> getKeys() {
        return this.keyMaps.get(this.keyMap);
    }

    @Override
    public Map<String, Widget> getWidgets() {
        return this.widgets;
    }

    @Override
    public Map<String, Widget> getBuiltinWidgets() {
        return Collections.unmodifiableMap(this.builtinWidgets);
    }

    @Override
    public Buffer getBuffer() {
        return this.buf;
    }

    @Override
    public void setAutosuggestion(LineReader.SuggestionType type) {
        this.autosuggestion = type;
    }

    @Override
    public LineReader.SuggestionType getAutosuggestion() {
        return this.autosuggestion;
    }

    @Override
    public String getTailTip() {
        return this.tailTip;
    }

    @Override
    public void setTailTip(String tailTip) {
        this.tailTip = tailTip;
    }

    @Override
    public void runMacro(String macro) {
        this.bindingReader.runMacro(macro);
    }

    @Override
    public MouseEvent readMouseEvent() {
        return this.terminal.readMouseEvent(this.bindingReader::readCharacter, this.bindingReader.getLastBinding());
    }

    public void setCompleter(Completer completer) {
        this.completer = completer;
    }

    public Completer getCompleter() {
        return this.completer;
    }

    public void setHistory(History history) {
        Objects.requireNonNull(history);
        this.history = history;
    }

    @Override
    public History getHistory() {
        return this.history;
    }

    public void setHighlighter(Highlighter highlighter) {
        this.highlighter = highlighter;
    }

    @Override
    public Highlighter getHighlighter() {
        return this.highlighter;
    }

    @Override
    public Parser getParser() {
        return this.parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Expander getExpander() {
        return this.expander;
    }

    public void setExpander(Expander expander) {
        this.expander = expander;
    }

    public void setCompletionMatcher(CompletionMatcher completionMatcher) {
        this.completionMatcher = completionMatcher;
    }

    @Override
    public String readLine() throws UserInterruptException, EndOfFileException {
        return this.readLine(null, null, (MaskingCallback)null, null);
    }

    @Override
    public String readLine(Character mask) throws UserInterruptException, EndOfFileException {
        return this.readLine(null, null, mask, null);
    }

    @Override
    public String readLine(String prompt) throws UserInterruptException, EndOfFileException {
        return this.readLine(prompt, null, (MaskingCallback)null, null);
    }

    @Override
    public String readLine(String prompt, Character mask) throws UserInterruptException, EndOfFileException {
        return this.readLine(prompt, null, mask, null);
    }

    @Override
    public String readLine(String prompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
        return this.readLine(prompt, null, mask, buffer);
    }

    @Override
    public String readLine(String prompt, String rightPrompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
        return this.readLine(prompt, rightPrompt, mask != null ? new SimpleMaskingCallback(mask) : null, buffer);
    }

    /*
     * Exception decompiling
     */
    @Override
    public String readLine(String prompt, String rightPrompt, MaskingCallback maskingCallback, String buffer) throws UserInterruptException, EndOfFileException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [3[TRYBLOCK], 1[TRYBLOCK]], but top level block is 13[CASE]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private boolean isTerminalDumb() {
        return "dumb".equals(this.terminal.getType()) || "dumb-color".equals(this.terminal.getType());
    }

    private void doDisplay() {
        this.size.copy(this.terminal.getBufferSize());
        this.display = new Display(this.terminal, false);
        this.display.resize(this.size.getRows(), this.size.getColumns());
        if (this.isSet(LineReader.Option.DELAY_LINE_WRAP)) {
            this.display.setDelayLineWrap(true);
        }
    }

    private void setupMaskThread(String prompt) {
        if (this.isTerminalDumb() && this.maskThread == null) {
            final String fullPrompt = "\r" + prompt + "                                                   \r" + prompt;
            this.maskThread = new Thread(this, "JLine Mask Thread"){
                final /* synthetic */ LineReaderImpl this$0;
                {
                    this.this$0 = this$0;
                    super(arg0);
                }

                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            this.this$0.terminal.writer().write(fullPrompt);
                            this.this$0.terminal.writer().flush();
                            1.sleep(3L);
                        }
                        catch (InterruptedException ie) {
                            return;
                        }
                    }
                }
            };
            this.maskThread.setPriority(10);
            this.maskThread.setDaemon(true);
            this.maskThread.start();
        }
    }

    private void stopMaskThread() {
        if (this.maskThread != null && this.maskThread.isAlive()) {
            this.maskThread.interrupt();
        }
        this.maskThread = null;
    }

    @Override
    public void printAbove(String str) {
        try {
            this.lock.lock();
            boolean reading = this.reading;
            if (reading) {
                this.display.update(Collections.emptyList(), 0);
            }
            if (str.endsWith("\n") || str.endsWith("\n\u001b[m") || str.endsWith("\n\u001b[0m")) {
                this.terminal.writer().print(str);
            } else {
                this.terminal.writer().println(str);
            }
            if (reading) {
                this.redisplay(false);
            }
            this.terminal.flush();
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void printAbove(AttributedString str) {
        this.printAbove(str.toAnsi(this.terminal));
    }

    @Override
    public boolean isReading() {
        try {
            this.lock.lock();
            boolean bl = this.reading;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    protected boolean freshLine() {
        boolean wrapAtEol = this.terminal.getBooleanCapability(InfoCmp.Capability.auto_right_margin);
        boolean delayedWrapAtEol = wrapAtEol && this.terminal.getBooleanCapability(InfoCmp.Capability.eat_newline_glitch);
        AttributedStringBuilder sb = new AttributedStringBuilder();
        sb.style(AttributedStyle.DEFAULT.foreground(8));
        sb.append("~");
        sb.style(AttributedStyle.DEFAULT);
        if (!wrapAtEol || delayedWrapAtEol) {
            for (int i = 0; i < this.size.getColumns() - 1; ++i) {
                sb.append(" ");
            }
            sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
            sb.append(" ");
            sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
        } else {
            String el = this.terminal.getStringCapability(InfoCmp.Capability.clr_eol);
            if (el != null) {
                Curses.tputs(sb, el, new Object[0]);
            }
            for (int i = 0; i < this.size.getColumns() - 2; ++i) {
                sb.append(" ");
            }
            sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
            sb.append(" ");
            sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
        }
        sb.print(this.terminal);
        return true;
    }

    @Override
    public void callWidget(String name) {
        try {
            this.lock.lock();
            if (!this.reading) {
                throw new IllegalStateException("Widgets can only be called during a `readLine` call");
            }
            try {
                Widget w = name.startsWith(".") ? this.builtinWidgets.get(name.substring(1)) : this.widgets.get(name);
                if (w != null) {
                    w.apply();
                }
            }
            catch (Throwable t) {
                Log.debug("Error executing widget '", name, "'", t);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean redrawLine() {
        this.display.reset();
        return true;
    }

    public void putString(CharSequence str) {
        this.buf.write(str, this.overTyping);
    }

    @Override
    public void flush() {
        this.terminal.flush();
    }

    public boolean isKeyMap(String name) {
        return this.keyMap.equals(name);
    }

    public int readCharacter() {
        if (this.lock.isHeldByCurrentThread()) {
            try {
                this.lock.unlock();
                int n = this.bindingReader.readCharacter();
                return n;
            }
            finally {
                this.lock.lock();
            }
        }
        return this.bindingReader.readCharacter();
    }

    public int peekCharacter(long timeout) {
        return this.bindingReader.peekCharacter(timeout);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <T> T doReadBinding(KeyMap<T> keys, KeyMap<T> local) {
        if (this.lock.isHeldByCurrentThread()) {
            try {
                this.lock.unlock();
                T t = this.bindingReader.readBinding(keys, local);
                return t;
            }
            finally {
                this.lock.lock();
            }
        }
        return this.bindingReader.readBinding(keys, local);
    }

    protected String doReadStringUntil(String sequence) {
        if (this.lock.isHeldByCurrentThread()) {
            try {
                this.lock.unlock();
                String string = this.bindingReader.readStringUntil(sequence);
                return string;
            }
            finally {
                this.lock.lock();
            }
        }
        return this.bindingReader.readStringUntil(sequence);
    }

    public Binding readBinding(KeyMap<Binding> keys) {
        return this.readBinding(keys, null);
    }

    public Binding readBinding(KeyMap<Binding> keys, KeyMap<Binding> local) {
        Binding o = this.doReadBinding(keys, local);
        if (o instanceof Reference) {
            String ref = ((Reference)o).name();
            if (!"yank-pop".equals(ref) && !"yank".equals(ref)) {
                this.killRing.resetLastYank();
            }
            if (!("kill-line".equals(ref) || "kill-whole-line".equals(ref) || "backward-kill-word".equals(ref) || "kill-word".equals(ref))) {
                this.killRing.resetLastKill();
            }
        }
        return o;
    }

    @Override
    public ParsedLine getParsedLine() {
        return this.parsedLine;
    }

    @Override
    public String getLastBinding() {
        return this.bindingReader.getLastBinding();
    }

    @Override
    public String getSearchTerm() {
        return this.searchTerm != null ? this.searchTerm.toString() : null;
    }

    @Override
    public LineReader.RegionType getRegionActive() {
        return this.regionActive;
    }

    @Override
    public int getRegionMark() {
        return this.regionMark;
    }

    @Override
    public boolean setKeyMap(String name) {
        KeyMap<Binding> map = this.keyMaps.get(name);
        if (map == null) {
            return false;
        }
        this.keyMap = name;
        if (this.reading) {
            this.callWidget("callback-keymap");
        }
        return true;
    }

    @Override
    public String getKeyMap() {
        return this.keyMap;
    }

    @Override
    public LineReader variable(String name, Object value) {
        this.variables.put(name, value);
        return this;
    }

    @Override
    public Map<String, Object> getVariables() {
        return this.variables;
    }

    @Override
    public Object getVariable(String name) {
        return this.variables.get(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        this.variables.put(name, value);
    }

    @Override
    public LineReader option(LineReader.Option option, boolean value) {
        this.options.put(option, value);
        return this;
    }

    @Override
    public boolean isSet(LineReader.Option option) {
        return option.isSet(this.options);
    }

    @Override
    public void setOpt(LineReader.Option option) {
        this.options.put(option, Boolean.TRUE);
    }

    @Override
    public void unsetOpt(LineReader.Option option) {
        this.options.put(option, Boolean.FALSE);
    }

    @Override
    public void addCommandsInBuffer(Collection<String> commands) {
        this.commandsBuffer.addAll(commands);
    }

    @Override
    public void editAndAddInBuffer(Path file) throws Exception {
        if (this.isSet(LineReader.Option.BRACKETED_PASTE)) {
            this.terminal.writer().write(BRACKETED_PASTE_OFF);
        }
        Constructor<?> ctor = Class.forName("org.jline.builtins.Nano").getConstructor(Terminal.class, Path.class);
        Editor editor = (Editor)ctor.newInstance(this.terminal, file.getParent());
        editor.setRestricted(true);
        editor.open(Collections.singletonList(file.getFileName().toString()));
        editor.run();
        try (BufferedReader br = Files.newBufferedReader(file);){
            String line;
            this.commandsBuffer.clear();
            while ((line = br.readLine()) != null) {
                this.commandsBuffer.add(line);
            }
        }
    }

    protected int getTabWidth() {
        return this.getInt("tab-width", 4);
    }

    protected String finishBuffer() {
        return this.finish(this.buf.toString());
    }

    protected String finish(String str) {
        String historyLine = str;
        if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
            StringBuilder sb = new StringBuilder();
            boolean escaped = false;
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (escaped) {
                    escaped = false;
                    if (ch == '\n') continue;
                    sb.append(ch);
                    continue;
                }
                if (this.parser.isEscapeChar(ch)) {
                    escaped = true;
                    continue;
                }
                sb.append(ch);
            }
            str = sb.toString();
        }
        if (this.maskingCallback != null) {
            historyLine = this.maskingCallback.history(historyLine);
        }
        if (historyLine != null && historyLine.length() > 0) {
            this.history.add(Instant.now(), historyLine);
        }
        return str;
    }

    protected synchronized void handleSignal(Terminal.Signal signal) {
        this.doAutosuggestion = false;
        if (signal == Terminal.Signal.WINCH) {
            this.size.copy(this.terminal.getBufferSize());
            this.display.resize(this.size.getRows(), this.size.getColumns());
            Status status = Status.getStatus(this.terminal, false);
            if (status != null) {
                status.resize(this.size);
                status.reset();
            }
            this.terminal.puts(InfoCmp.Capability.carriage_return, new Object[0]);
            this.terminal.puts(InfoCmp.Capability.clr_eos, new Object[0]);
            this.redrawLine();
            this.redisplay();
        } else if (signal == Terminal.Signal.CONT) {
            this.terminal.enterRawMode();
            this.size.copy(this.terminal.getBufferSize());
            this.display.resize(this.size.getRows(), this.size.getColumns());
            this.terminal.puts(InfoCmp.Capability.keypad_xmit, new Object[0]);
            this.redrawLine();
            this.redisplay();
        }
    }

    protected Widget getWidget(Object binding) {
        Widget w;
        if (binding instanceof Widget) {
            w = (Widget)binding;
        } else if (binding instanceof Macro) {
            String macro = ((Macro)binding).getSequence();
            w = () -> {
                this.bindingReader.runMacro(macro);
                return true;
            };
        } else if (binding instanceof Reference) {
            String name = ((Reference)binding).name();
            w = this.widgets.get(name);
            if (w == null) {
                w = () -> {
                    this.post = () -> new AttributedString("No such widget `" + name + "'");
                    return false;
                };
            }
        } else {
            w = () -> {
                this.post = () -> new AttributedString("Unsupported widget");
                return false;
            };
        }
        return w;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt == null ? AttributedString.EMPTY : this.expandPromptPattern(prompt, 0, DEFAULT_BELL_STYLE, 0);
    }

    public void setRightPrompt(String rightPrompt) {
        this.rightPrompt = rightPrompt == null ? AttributedString.EMPTY : this.expandPromptPattern(rightPrompt, 0, DEFAULT_BELL_STYLE, 0);
    }

    protected void setBuffer(Buffer buffer) {
        this.buf.copyFrom(buffer);
    }

    protected void setBuffer(String buffer) {
        this.buf.clear();
        this.buf.write(buffer);
    }

    protected String viDeleteChangeYankToRemap(String op) {
        switch (op) {
            case "abort": 
            case "backward-char": 
            case "forward-char": 
            case "end-of-line": 
            case "vi-match-bracket": 
            case "vi-digit-or-beginning-of-line": 
            case "neg-argument": 
            case "digit-argument": 
            case "vi-backward-char": 
            case "vi-backward-word": 
            case "vi-forward-char": 
            case "vi-forward-word": 
            case "vi-forward-word-end": 
            case "vi-first-non-blank": 
            case "vi-goto-column": 
            case "vi-delete": 
            case "vi-yank": 
            case "vi-change-to": 
            case "vi-find-next-char": 
            case "vi-find-next-char-skip": 
            case "vi-find-prev-char": 
            case "vi-find-prev-char-skip": 
            case "vi-repeat-find": 
            case "vi-rev-repeat-find": {
                return op;
            }
        }
        return "vi-cmd-mode";
    }

    protected int switchCase(int ch) {
        if (Character.isUpperCase(ch)) {
            return Character.toLowerCase(ch);
        }
        if (Character.isLowerCase(ch)) {
            return Character.toUpperCase(ch);
        }
        return ch;
    }

    protected boolean isInViMoveOperation() {
        return this.viMoveMode != ViMoveMode.NORMAL;
    }

    protected boolean isInViChangeOperation() {
        return this.viMoveMode == ViMoveMode.CHANGE;
    }

    protected boolean isInViCmdMode() {
        return "vicmd".equals(this.keyMap);
    }

    protected boolean viForwardChar() {
        if (this.count < 0) {
            return this.callNeg(this::viBackwardChar);
        }
        int lim = this.findeol();
        if (this.isInViCmdMode() && !this.isInViMoveOperation()) {
            --lim;
        }
        if (this.buf.cursor() >= lim) {
            return false;
        }
        while (this.count-- > 0 && this.buf.cursor() < lim) {
            this.buf.move(1);
        }
        return true;
    }

    protected boolean viBackwardChar() {
        if (this.count < 0) {
            return this.callNeg(this::viForwardChar);
        }
        int lim = this.findbol();
        if (this.buf.cursor() == lim) {
            return false;
        }
        while (this.count-- > 0 && this.buf.cursor() > 0) {
            this.buf.move(-1);
            if (this.buf.currChar() != 10) continue;
            this.buf.move(1);
            break;
        }
        return true;
    }

    protected boolean forwardWord() {
        if (this.count < 0) {
            return this.callNeg(this::backwardWord);
        }
        while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
                this.buf.move(1);
            }
            if (this.isInViChangeOperation() && this.count == 0) break;
            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
                this.buf.move(1);
            }
        }
        return true;
    }

    protected boolean viForwardWord() {
        if (this.count < 0) {
            return this.callNeg(this::viBackwardWord);
        }
        while (this.count-- > 0) {
            int nl;
            if (this.isViAlphaNum(this.buf.currChar())) {
                while (this.buf.cursor() < this.buf.length() && this.isViAlphaNum(this.buf.currChar())) {
                    this.buf.move(1);
                }
            } else {
                while (this.buf.cursor() < this.buf.length() && !this.isViAlphaNum(this.buf.currChar()) && !this.isWhitespace(this.buf.currChar())) {
                    this.buf.move(1);
                }
            }
            if (this.isInViChangeOperation() && this.count == 0) {
                return true;
            }
            int n = nl = this.buf.currChar() == 10 ? 1 : 0;
            while (this.buf.cursor() < this.buf.length() && nl < 2 && this.isWhitespace(this.buf.currChar())) {
                this.buf.move(1);
                nl += this.buf.currChar() == 10 ? 1 : 0;
            }
        }
        return true;
    }

    protected boolean viForwardBlankWord() {
        if (this.count < 0) {
            return this.callNeg(this::viBackwardBlankWord);
        }
        while (this.count-- > 0) {
            int nl;
            while (this.buf.cursor() < this.buf.length() && !this.isWhitespace(this.buf.currChar())) {
                this.buf.move(1);
            }
            if (this.isInViChangeOperation() && this.count == 0) {
                return true;
            }
            int n = nl = this.buf.currChar() == 10 ? 1 : 0;
            while (this.buf.cursor() < this.buf.length() && nl < 2 && this.isWhitespace(this.buf.currChar())) {
                this.buf.move(1);
                nl += this.buf.currChar() == 10 ? 1 : 0;
            }
        }
        return true;
    }

    protected boolean emacsForwardWord() {
        return this.forwardWord();
    }

    protected boolean viForwardBlankWordEnd() {
        if (this.count < 0) {
            return false;
        }
        block0: while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length()) {
                this.buf.move(1);
                if (this.isWhitespace(this.buf.currChar())) continue;
            }
            while (this.buf.cursor() < this.buf.length()) {
                this.buf.move(1);
                if (!this.isWhitespace(this.buf.currChar())) continue;
                continue block0;
            }
        }
        return true;
    }

    protected boolean viForwardWordEnd() {
        if (this.count < 0) {
            return this.callNeg(this::backwardWord);
        }
        while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && this.isWhitespace(this.buf.nextChar())) {
                this.buf.move(1);
            }
            if (this.buf.cursor() >= this.buf.length()) continue;
            if (this.isViAlphaNum(this.buf.nextChar())) {
                this.buf.move(1);
                while (this.buf.cursor() < this.buf.length() && this.isViAlphaNum(this.buf.nextChar())) {
                    this.buf.move(1);
                }
                continue;
            }
            this.buf.move(1);
            while (this.buf.cursor() < this.buf.length() && !this.isViAlphaNum(this.buf.nextChar()) && !this.isWhitespace(this.buf.nextChar())) {
                this.buf.move(1);
            }
        }
        if (this.buf.cursor() < this.buf.length() && this.isInViMoveOperation()) {
            this.buf.move(1);
        }
        return true;
    }

    protected boolean backwardWord() {
        if (this.count < 0) {
            return this.callNeg(this::forwardWord);
        }
        while (this.count-- > 0) {
            while (this.buf.cursor() > 0 && !this.isWord(this.buf.atChar(this.buf.cursor() - 1))) {
                this.buf.move(-1);
            }
            while (this.buf.cursor() > 0 && this.isWord(this.buf.atChar(this.buf.cursor() - 1))) {
                this.buf.move(-1);
            }
        }
        return true;
    }

    protected boolean viBackwardWord() {
        if (this.count < 0) {
            return this.callNeg(this::viForwardWord);
        }
        while (this.count-- > 0) {
            int nl = 0;
            while (this.buf.cursor() > 0) {
                this.buf.move(-1);
                if (!this.isWhitespace(this.buf.currChar())) break;
                if ((nl += this.buf.currChar() == 10 ? 1 : 0) != 2) continue;
                this.buf.move(1);
                break;
            }
            if (this.buf.cursor() <= 0) continue;
            if (this.isViAlphaNum(this.buf.currChar())) {
                while (this.buf.cursor() > 0 && this.isViAlphaNum(this.buf.prevChar())) {
                    this.buf.move(-1);
                }
                continue;
            }
            while (this.buf.cursor() > 0 && !this.isViAlphaNum(this.buf.prevChar()) && !this.isWhitespace(this.buf.prevChar())) {
                this.buf.move(-1);
            }
        }
        return true;
    }

    protected boolean viBackwardBlankWord() {
        if (this.count < 0) {
            return this.callNeg(this::viForwardBlankWord);
        }
        block0: while (this.count-- > 0) {
            while (this.buf.cursor() > 0) {
                this.buf.move(-1);
                if (this.isWhitespace(this.buf.currChar())) continue;
            }
            while (this.buf.cursor() > 0) {
                this.buf.move(-1);
                if (!this.isWhitespace(this.buf.currChar())) continue;
                continue block0;
            }
        }
        return true;
    }

    protected boolean viBackwardWordEnd() {
        if (this.count < 0) {
            return this.callNeg(this::viForwardWordEnd);
        }
        while (this.count-- > 0 && this.buf.cursor() > 1) {
            int start = this.isViAlphaNum(this.buf.currChar()) ? 1 : (!this.isWhitespace(this.buf.currChar()) ? 2 : 0);
            while (this.buf.cursor() > 0) {
                boolean same;
                boolean bl = same = start != 1 && this.isWhitespace(this.buf.currChar());
                if (start != 0) {
                    same |= this.isViAlphaNum(this.buf.currChar());
                }
                if (same == (start == 2)) break;
                this.buf.move(-1);
            }
            while (this.buf.cursor() > 0 && this.isWhitespace(this.buf.currChar())) {
                this.buf.move(-1);
            }
        }
        return true;
    }

    protected boolean viBackwardBlankWordEnd() {
        if (this.count < 0) {
            return this.callNeg(this::viForwardBlankWordEnd);
        }
        while (this.count-- > 0) {
            while (this.buf.cursor() > 0 && !this.isWhitespace(this.buf.currChar())) {
                this.buf.move(-1);
            }
            while (this.buf.cursor() > 0 && this.isWhitespace(this.buf.currChar())) {
                this.buf.move(-1);
            }
        }
        return true;
    }

    protected boolean emacsBackwardWord() {
        return this.backwardWord();
    }

    protected boolean backwardDeleteWord() {
        if (this.count < 0) {
            return this.callNeg(this::deleteWord);
        }
        int cursor = this.buf.cursor();
        while (this.count-- > 0) {
            while (cursor > 0 && !this.isWord(this.buf.atChar(cursor - 1))) {
                --cursor;
            }
            while (cursor > 0 && this.isWord(this.buf.atChar(cursor - 1))) {
                --cursor;
            }
        }
        this.buf.backspace(this.buf.cursor() - cursor);
        return true;
    }

    protected boolean viBackwardKillWord() {
        if (this.count < 0) {
            return false;
        }
        int lim = this.findbol();
        int x = this.buf.cursor();
        while (this.count-- > 0) {
            while (x > lim && this.isWhitespace(this.buf.atChar(x - 1))) {
                --x;
            }
            if (x <= lim) continue;
            if (this.isViAlphaNum(this.buf.atChar(x - 1))) {
                while (x > lim && this.isViAlphaNum(this.buf.atChar(x - 1))) {
                    --x;
                }
                continue;
            }
            while (x > lim && !this.isViAlphaNum(this.buf.atChar(x - 1)) && !this.isWhitespace(this.buf.atChar(x - 1))) {
                --x;
            }
        }
        this.killRing.addBackwards(this.buf.substring(x, this.buf.cursor()));
        this.buf.backspace(this.buf.cursor() - x);
        return true;
    }

    protected boolean backwardKillWord() {
        if (this.count < 0) {
            return this.callNeg(this::killWord);
        }
        int x = this.buf.cursor();
        while (this.count-- > 0) {
            while (x > 0 && !this.isWord(this.buf.atChar(x - 1))) {
                --x;
            }
            while (x > 0 && this.isWord(this.buf.atChar(x - 1))) {
                --x;
            }
        }
        this.killRing.addBackwards(this.buf.substring(x, this.buf.cursor()));
        this.buf.backspace(this.buf.cursor() - x);
        return true;
    }

    protected boolean copyPrevWord() {
        int t1;
        int t0;
        block4: {
            if (this.count <= 0) {
                return false;
            }
            t0 = this.buf.cursor();
            do {
                t1 = t0;
                while (t0 > 0 && !this.isWord(this.buf.atChar(t0 - 1))) {
                    --t0;
                }
                while (t0 > 0 && this.isWord(this.buf.atChar(t0 - 1))) {
                    --t0;
                }
                if (--this.count == 0) break block4;
            } while (t0 != 0);
            return false;
        }
        this.buf.write(this.buf.substring(t0, t1));
        return true;
    }

    protected boolean upCaseWord() {
        int count = Math.abs(this.count);
        int cursor = this.buf.cursor();
        while (count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
                this.buf.move(1);
            }
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
                this.buf.currChar(Character.toUpperCase(this.buf.currChar()));
                this.buf.move(1);
            }
        }
        if (this.count < 0) {
            this.buf.cursor(cursor);
        }
        return true;
    }

    protected boolean downCaseWord() {
        int count = Math.abs(this.count);
        int cursor = this.buf.cursor();
        while (count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
                this.buf.move(1);
            }
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
                this.buf.currChar(Character.toLowerCase(this.buf.currChar()));
                this.buf.move(1);
            }
        }
        if (this.count < 0) {
            this.buf.cursor(cursor);
        }
        return true;
    }

    protected boolean capitalizeWord() {
        int count = Math.abs(this.count);
        int cursor = this.buf.cursor();
        while (count-- > 0) {
            boolean first = true;
            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
                this.buf.move(1);
            }
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar()) && !this.isAlpha(this.buf.currChar())) {
                this.buf.move(1);
            }
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
                this.buf.currChar(first ? Character.toUpperCase(this.buf.currChar()) : Character.toLowerCase(this.buf.currChar()));
                this.buf.move(1);
                first = false;
            }
        }
        if (this.count < 0) {
            this.buf.cursor(cursor);
        }
        return true;
    }

    protected boolean deleteWord() {
        if (this.count < 0) {
            return this.callNeg(this::backwardDeleteWord);
        }
        int x = this.buf.cursor();
        while (this.count-- > 0) {
            while (x < this.buf.length() && !this.isWord(this.buf.atChar(x))) {
                ++x;
            }
            while (x < this.buf.length() && this.isWord(this.buf.atChar(x))) {
                ++x;
            }
        }
        this.buf.delete(x - this.buf.cursor());
        return true;
    }

    protected boolean killWord() {
        if (this.count < 0) {
            return this.callNeg(this::backwardKillWord);
        }
        int x = this.buf.cursor();
        while (this.count-- > 0) {
            while (x < this.buf.length() && !this.isWord(this.buf.atChar(x))) {
                ++x;
            }
            while (x < this.buf.length() && this.isWord(this.buf.atChar(x))) {
                ++x;
            }
        }
        this.killRing.add(this.buf.substring(this.buf.cursor(), x));
        this.buf.delete(x - this.buf.cursor());
        return true;
    }

    protected boolean transposeWords() {
        int lstart = this.buf.cursor() - 1;
        int lend = this.buf.cursor();
        while (this.buf.atChar(lstart) != 0 && this.buf.atChar(lstart) != 10) {
            --lstart;
        }
        ++lstart;
        while (this.buf.atChar(lend) != 0 && this.buf.atChar(lend) != 10) {
            ++lend;
        }
        if (lend - lstart < 2) {
            return false;
        }
        int words = 0;
        boolean inWord = false;
        if (!this.isDelimiter(this.buf.atChar(lstart))) {
            ++words;
            inWord = true;
        }
        for (int i = lstart; i < lend; ++i) {
            if (this.isDelimiter(this.buf.atChar(i))) {
                inWord = false;
                continue;
            }
            if (!inWord) {
                ++words;
            }
            inWord = true;
        }
        if (words < 2) {
            return false;
        }
        boolean neg = this.count < 0;
        for (int count = Math.max(this.count, -this.count); count > 0; --count) {
            String res;
            int sta2;
            int end2;
            int sta1;
            for (sta1 = this.buf.cursor(); sta1 > lstart && !this.isDelimiter(this.buf.atChar(sta1 - 1)); --sta1) {
            }
            int end1 = sta1;
            while (end1 < lend && !this.isDelimiter(this.buf.atChar(++end1))) {
            }
            if (neg) {
                for (end2 = sta1 - 1; end2 > lstart && this.isDelimiter(this.buf.atChar(end2 - 1)); --end2) {
                }
                if (end2 < lstart) {
                    sta2 = end1;
                    while (this.isDelimiter(this.buf.atChar(++sta2))) {
                    }
                    end2 = sta2;
                    while (end2 < lend && !this.isDelimiter(this.buf.atChar(++end2))) {
                    }
                } else {
                    for (sta2 = end2; sta2 > lstart && !this.isDelimiter(this.buf.atChar(sta2 - 1)); --sta2) {
                    }
                }
            } else {
                sta2 = end1;
                while (sta2 < lend && this.isDelimiter(this.buf.atChar(++sta2))) {
                }
                if (sta2 == lend) {
                    end2 = sta1;
                    while (this.isDelimiter(this.buf.atChar(end2 - 1))) {
                        --end2;
                    }
                    for (sta2 = end2; sta2 > lstart && !this.isDelimiter(this.buf.atChar(sta2 - 1)); --sta2) {
                    }
                } else {
                    end2 = sta2;
                    while (end2 < lend && !this.isDelimiter(this.buf.atChar(++end2))) {
                    }
                }
            }
            if (sta1 < sta2) {
                res = this.buf.substring(0, sta1) + this.buf.substring(sta2, end2) + this.buf.substring(end1, sta2) + this.buf.substring(sta1, end1) + this.buf.substring(end2);
                this.buf.clear();
                this.buf.write(res);
                this.buf.cursor(neg ? end1 : end2);
                continue;
            }
            res = this.buf.substring(0, sta2) + this.buf.substring(sta1, end1) + this.buf.substring(end2, sta1) + this.buf.substring(sta2, end2) + this.buf.substring(end1);
            this.buf.clear();
            this.buf.write(res);
            this.buf.cursor(neg ? end2 : end1);
        }
        return true;
    }

    private int findbol() {
        int x;
        for (x = this.buf.cursor(); x > 0 && this.buf.atChar(x - 1) != 10; --x) {
        }
        return x;
    }

    private int findeol() {
        int x;
        for (x = this.buf.cursor(); x < this.buf.length() && this.buf.atChar(x) != 10; ++x) {
        }
        return x;
    }

    protected boolean insertComment() {
        return this.doInsertComment(false);
    }

    protected boolean viInsertComment() {
        return this.doInsertComment(true);
    }

    protected boolean doInsertComment(boolean isViMode) {
        String comment = this.getString("comment-begin", DEFAULT_COMMENT_BEGIN);
        this.beginningOfLine();
        this.putString(comment);
        if (isViMode) {
            this.setKeyMap("viins");
        }
        return this.acceptLine();
    }

    protected boolean viFindNextChar() {
        this.findChar = this.vigetkey();
        if (this.findChar > 0) {
            this.findDir = 1;
            this.findTailAdd = 0;
            return this.vifindchar(false);
        }
        return false;
    }

    protected boolean viFindPrevChar() {
        this.findChar = this.vigetkey();
        if (this.findChar > 0) {
            this.findDir = -1;
            this.findTailAdd = 0;
            return this.vifindchar(false);
        }
        return false;
    }

    protected boolean viFindNextCharSkip() {
        this.findChar = this.vigetkey();
        if (this.findChar > 0) {
            this.findDir = 1;
            this.findTailAdd = -1;
            return this.vifindchar(false);
        }
        return false;
    }

    protected boolean viFindPrevCharSkip() {
        this.findChar = this.vigetkey();
        if (this.findChar > 0) {
            this.findDir = -1;
            this.findTailAdd = 1;
            return this.vifindchar(false);
        }
        return false;
    }

    protected boolean viRepeatFind() {
        return this.vifindchar(true);
    }

    protected boolean viRevRepeatFind() {
        if (this.count < 0) {
            return this.callNeg(() -> this.vifindchar(true));
        }
        this.findTailAdd = -this.findTailAdd;
        this.findDir = -this.findDir;
        boolean ret = this.vifindchar(true);
        this.findTailAdd = -this.findTailAdd;
        this.findDir = -this.findDir;
        return ret;
    }

    private int vigetkey() {
        String func;
        Binding b;
        int ch = this.readCharacter();
        KeyMap<Binding> km = this.keyMaps.get("main");
        if (km != null && (b = km.getBound(new String(Character.toChars(ch)))) instanceof Reference && "abort".equals(func = ((Reference)b).name())) {
            return -1;
        }
        return ch;
    }

    private boolean vifindchar(boolean repeat) {
        if (this.findDir == 0) {
            return false;
        }
        if (this.count < 0) {
            return this.callNeg(this::viRevRepeatFind);
        }
        if (repeat && this.findTailAdd != 0) {
            if (this.findDir > 0) {
                if (this.buf.cursor() < this.buf.length() && this.buf.nextChar() == this.findChar) {
                    this.buf.move(1);
                }
            } else if (this.buf.cursor() > 0 && this.buf.prevChar() == this.findChar) {
                this.buf.move(-1);
            }
        }
        int cursor = this.buf.cursor();
        while (this.count-- > 0) {
            do {
                this.buf.move(this.findDir);
            } while (this.buf.cursor() > 0 && this.buf.cursor() < this.buf.length() && this.buf.currChar() != this.findChar && this.buf.currChar() != 10);
            if (this.buf.cursor() > 0 && this.buf.cursor() < this.buf.length() && this.buf.currChar() != 10) continue;
            this.buf.cursor(cursor);
            return false;
        }
        if (this.findTailAdd != 0) {
            this.buf.move(this.findTailAdd);
        }
        if (this.findDir == 1 && this.isInViMoveOperation()) {
            this.buf.move(1);
        }
        return true;
    }

    private boolean callNeg(Widget widget) {
        this.count = -this.count;
        boolean ret = widget.apply();
        this.count = -this.count;
        return ret;
    }

    protected boolean viHistorySearchForward() {
        this.searchDir = 1;
        this.searchIndex = 0;
        return this.getViSearchString() && this.viRepeatSearch();
    }

    protected boolean viHistorySearchBackward() {
        this.searchDir = -1;
        this.searchIndex = this.history.size() - 1;
        return this.getViSearchString() && this.viRepeatSearch();
    }

    protected boolean viRepeatSearch() {
        int si;
        if (this.searchDir == 0) {
            return false;
        }
        int n = si = this.searchDir < 0 ? this.searchBackwards(this.searchString, this.searchIndex, false) : this.searchForwards(this.searchString, this.searchIndex, false);
        if (si == -1 || si == this.history.index()) {
            return false;
        }
        this.searchIndex = si;
        this.buf.clear();
        this.history.moveTo(this.searchIndex);
        this.buf.write(this.history.get(this.searchIndex));
        if ("vicmd".equals(this.keyMap)) {
            this.buf.move(-1);
        }
        return true;
    }

    protected boolean viRevRepeatSearch() {
        this.searchDir = -this.searchDir;
        boolean ret = this.viRepeatSearch();
        this.searchDir = -this.searchDir;
        return ret;
    }

    private boolean getViSearchString() {
        if (this.searchDir == 0) {
            return false;
        }
        String searchPrompt = this.searchDir < 0 ? "?" : "/";
        BufferImpl searchBuffer = new BufferImpl();
        KeyMap<Binding> keyMap = this.keyMaps.get("main");
        if (keyMap == null) {
            keyMap = this.keyMaps.get(".safe");
        }
        block28: while (true) {
            String func;
            this.post = () -> new AttributedString(searchPrompt + searchBuffer.toString() + "_");
            this.redisplay();
            Binding b = this.doReadBinding(keyMap, null);
            if (!(b instanceof Reference)) continue;
            switch (func = ((Reference)b).name()) {
                case "abort": {
                    this.post = null;
                    return false;
                }
                case "accept-line": 
                case "vi-cmd-mode": {
                    this.searchString = searchBuffer.toString();
                    this.post = null;
                    return true;
                }
                case "magic-space": {
                    searchBuffer.write(32);
                    continue block28;
                }
                case "redisplay": {
                    this.redisplay();
                    continue block28;
                }
                case "clear-screen": {
                    this.clearScreen();
                    continue block28;
                }
                case "self-insert": {
                    searchBuffer.write(this.getLastBinding());
                    continue block28;
                }
                case "self-insert-unmeta": {
                    if (this.getLastBinding().charAt(0) != '\u001b') continue block28;
                    String s = this.getLastBinding().substring(1);
                    if ("\r".equals(s)) {
                        s = "\n";
                    }
                    searchBuffer.write(s);
                    continue block28;
                }
                case "backward-delete-char": 
                case "vi-backward-delete-char": {
                    if (searchBuffer.length() <= 0) continue block28;
                    searchBuffer.backspace();
                    continue block28;
                }
                case "backward-kill-word": 
                case "vi-backward-kill-word": {
                    if (searchBuffer.length() > 0 && !this.isWhitespace(searchBuffer.prevChar())) {
                        searchBuffer.backspace();
                    }
                    if (searchBuffer.length() <= 0 || !this.isWhitespace(searchBuffer.prevChar())) continue block28;
                    searchBuffer.backspace();
                    continue block28;
                }
                case "quoted-insert": 
                case "vi-quoted-insert": {
                    int c = this.readCharacter();
                    if (c >= 0) {
                        searchBuffer.write(c);
                        continue block28;
                    }
                    this.beep();
                    continue block28;
                }
            }
            this.beep();
        }
    }

    protected boolean insertCloseCurly() {
        return this.insertClose("}");
    }

    protected boolean insertCloseParen() {
        return this.insertClose(DESC_SUFFIX);
    }

    protected boolean insertCloseSquare() {
        return this.insertClose("]");
    }

    protected boolean insertClose(String s) {
        this.putString(s);
        long blink = this.getLong("blink-matching-paren", 500L);
        if (blink <= 0L) {
            this.removeIndentation();
            return true;
        }
        int closePosition = this.buf.cursor();
        this.buf.move(-1);
        this.doViMatchBracket();
        this.redisplay();
        this.peekCharacter(blink);
        int blinkPosition = this.buf.cursor();
        this.buf.cursor(closePosition);
        if (blinkPosition != closePosition - 1) {
            this.removeIndentation();
        }
        return true;
    }

    private void removeIndentation() {
        int indent = this.getInt("indentation", 0);
        if (indent > 0) {
            this.buf.move(-1);
            for (int i = 0; i < indent; ++i) {
                this.buf.move(-1);
                if (this.buf.currChar() != 32) {
                    this.buf.move(1);
                    break;
                }
                this.buf.delete();
            }
            this.buf.move(1);
        }
    }

    protected boolean viMatchBracket() {
        return this.doViMatchBracket();
    }

    protected boolean undefinedKey() {
        return false;
    }

    protected boolean doViMatchBracket() {
        int pos = this.buf.cursor();
        if (pos == this.buf.length()) {
            return false;
        }
        int type = this.getBracketType(this.buf.atChar(pos));
        int move = type < 0 ? -1 : 1;
        int count = 1;
        if (type == 0) {
            return false;
        }
        while (count > 0) {
            if ((pos += move) < 0 || pos >= this.buf.length()) {
                return false;
            }
            int curType = this.getBracketType(this.buf.atChar(pos));
            if (curType == type) {
                ++count;
                continue;
            }
            if (curType != -type) continue;
            --count;
        }
        if (move > 0 && this.isInViMoveOperation()) {
            ++pos;
        }
        this.buf.cursor(pos);
        return true;
    }

    protected int getBracketType(int ch) {
        switch (ch) {
            case 91: {
                return 1;
            }
            case 93: {
                return -1;
            }
            case 123: {
                return 2;
            }
            case 125: {
                return -2;
            }
            case 40: {
                return 3;
            }
            case 41: {
                return -3;
            }
        }
        return 0;
    }

    protected boolean transposeChars() {
        int lstart = this.buf.cursor() - 1;
        int lend = this.buf.cursor();
        while (this.buf.atChar(lstart) != 0 && this.buf.atChar(lstart) != 10) {
            --lstart;
        }
        ++lstart;
        while (this.buf.atChar(lend) != 0 && this.buf.atChar(lend) != 10) {
            ++lend;
        }
        if (lend - lstart < 2) {
            return false;
        }
        boolean neg = this.count < 0;
        for (int count = Math.max(this.count, -this.count); count > 0; --count) {
            while (this.buf.cursor() <= lstart) {
                this.buf.move(1);
            }
            while (this.buf.cursor() >= lend) {
                this.buf.move(-1);
            }
            int c = this.buf.currChar();
            this.buf.currChar(this.buf.prevChar());
            this.buf.move(-1);
            this.buf.currChar(c);
            this.buf.move(neg ? 0 : 2);
        }
        return true;
    }

    protected boolean undo() {
        this.isUndo = true;
        if (this.undo.canUndo()) {
            this.undo.undo();
            return true;
        }
        return false;
    }

    protected boolean redo() {
        this.isUndo = true;
        if (this.undo.canRedo()) {
            this.undo.redo();
            return true;
        }
        return false;
    }

    protected boolean sendBreak() {
        if (this.searchTerm == null) {
            this.buf.clear();
            this.println();
            this.redrawLine();
            return false;
        }
        return true;
    }

    protected boolean backwardChar() {
        return this.buf.move(-this.count) != 0;
    }

    protected boolean forwardChar() {
        return this.buf.move(this.count) != 0;
    }

    protected boolean viDigitOrBeginningOfLine() {
        if (this.repeatCount > 0) {
            return this.digitArgument();
        }
        return this.beginningOfLine();
    }

    protected boolean universalArgument() {
        this.mult *= this.universal;
        this.isArgDigit = true;
        return true;
    }

    protected boolean argumentBase() {
        if (this.repeatCount > 0 && this.repeatCount < 32) {
            this.universal = this.repeatCount;
            this.isArgDigit = true;
            return true;
        }
        return false;
    }

    protected boolean negArgument() {
        this.mult *= -1;
        this.isArgDigit = true;
        return true;
    }

    protected boolean digitArgument() {
        String s = this.getLastBinding();
        this.repeatCount = this.repeatCount * 10 + s.charAt(s.length() - 1) - 48;
        int maxRepeatCount = this.getInt("max-repeat-count", 9999);
        if (this.repeatCount > maxRepeatCount) {
            throw new IllegalArgumentException("digit argument should be less than " + maxRepeatCount);
        }
        this.isArgDigit = true;
        return true;
    }

    protected boolean viDelete() {
        int cursorStart = this.buf.cursor();
        Binding o = this.readBinding(this.getKeys());
        if (o instanceof Reference) {
            String op = this.viDeleteChangeYankToRemap(((Reference)o).name());
            if ("vi-delete".equals(op)) {
                this.killWholeLine();
            } else {
                this.viMoveMode = ViMoveMode.DELETE;
                Widget widget = this.widgets.get(op);
                if (widget != null && !widget.apply()) {
                    this.viMoveMode = ViMoveMode.NORMAL;
                    return false;
                }
                this.viMoveMode = ViMoveMode.NORMAL;
            }
            return this.viDeleteTo(cursorStart, this.buf.cursor());
        }
        this.pushBackBinding();
        return false;
    }

    protected boolean viYankTo() {
        int cursorStart = this.buf.cursor();
        Binding o = this.readBinding(this.getKeys());
        if (o instanceof Reference) {
            String op = this.viDeleteChangeYankToRemap(((Reference)o).name());
            if ("vi-yank".equals(op)) {
                this.yankBuffer = this.buf.toString();
                return true;
            }
            this.viMoveMode = ViMoveMode.YANK;
            Widget widget = this.widgets.get(op);
            if (widget != null && !widget.apply()) {
                return false;
            }
            this.viMoveMode = ViMoveMode.NORMAL;
            return this.viYankTo(cursorStart, this.buf.cursor());
        }
        this.pushBackBinding();
        return false;
    }

    protected boolean viYankWholeLine() {
        int p = this.buf.cursor();
        while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
        }
        int s = this.buf.cursor();
        for (int i = 0; i < this.repeatCount; ++i) {
            while (this.buf.move(1) == 1 && this.buf.prevChar() != 10) {
            }
        }
        int e = this.buf.cursor();
        this.yankBuffer = this.buf.substring(s, e);
        if (!this.yankBuffer.endsWith("\n")) {
            this.yankBuffer = this.yankBuffer + "\n";
        }
        this.buf.cursor(p);
        return true;
    }

    protected boolean viChange() {
        int cursorStart = this.buf.cursor();
        Binding o = this.readBinding(this.getKeys());
        if (o instanceof Reference) {
            String op = this.viDeleteChangeYankToRemap(((Reference)o).name());
            if ("vi-change-to".equals(op)) {
                this.killWholeLine();
            } else {
                this.viMoveMode = ViMoveMode.CHANGE;
                Widget widget = this.widgets.get(op);
                if (widget != null && !widget.apply()) {
                    this.viMoveMode = ViMoveMode.NORMAL;
                    return false;
                }
                this.viMoveMode = ViMoveMode.NORMAL;
            }
            boolean res = this.viChange(cursorStart, this.buf.cursor());
            this.setKeyMap("viins");
            return res;
        }
        this.pushBackBinding();
        return false;
    }

    protected void cleanup() {
        if (this.isSet(LineReader.Option.ERASE_LINE_ON_FINISH)) {
            Buffer oldBuffer = this.buf.copy();
            AttributedString oldPrompt = this.prompt;
            this.buf.clear();
            this.prompt = new AttributedString(DEFAULT_BELL_STYLE);
            this.doCleanup(false);
            this.prompt = oldPrompt;
            this.buf.copyFrom(oldBuffer);
        } else {
            this.doCleanup(true);
        }
    }

    protected void doCleanup(boolean nl) {
        this.buf.cursor(this.buf.length());
        this.post = null;
        if (this.size.getColumns() > 0 || this.size.getRows() > 0) {
            this.doAutosuggestion = false;
            this.redisplay(false);
            if (nl) {
                this.println();
            }
            this.terminal.puts(InfoCmp.Capability.keypad_local, new Object[0]);
            this.terminal.trackMouse(Terminal.MouseTracking.Off);
            if (this.isSet(LineReader.Option.BRACKETED_PASTE) && !this.isTerminalDumb()) {
                this.terminal.writer().write(BRACKETED_PASTE_OFF);
            }
            this.stopMaskThread();
            this.flush();
        }
        this.history.moveToEnd();
    }

    protected boolean historyIncrementalSearchForward() {
        return this.doSearchHistory(false);
    }

    protected boolean historyIncrementalSearchBackward() {
        return this.doSearchHistory(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected boolean doSearchHistory(boolean backward) {
        if (this.history.isEmpty()) {
            return false;
        }
        KeyMap<Binding> terminators = new KeyMap<Binding>();
        this.getString("search-terminators", DEFAULT_SEARCH_TERMINATORS).codePoints().forEach(c -> this.bind(terminators, "accept-line", new String(Character.toChars(c))));
        Buffer originalBuffer = this.buf.copy();
        this.searchIndex = -1;
        this.searchTerm = new StringBuffer();
        this.searchBackward = backward;
        this.searchFailing = false;
        this.post = () -> new AttributedString((this.searchFailing ? "failing " : DEFAULT_BELL_STYLE) + (this.searchBackward ? "bck-i-search" : "fwd-i-search") + ": " + this.searchTerm + "_");
        this.redisplay();
        try {
            while (true) {
                int prevSearchIndex = this.searchIndex;
                Binding operation = this.readBinding(this.getKeys(), terminators);
                String ref = operation instanceof Reference ? ((Reference)operation).name() : DEFAULT_BELL_STYLE;
                boolean next = false;
                switch (ref) {
                    case "abort": {
                        this.beep();
                        this.buf.copyFrom(originalBuffer);
                        boolean bl = true;
                        return bl;
                    }
                    case "history-incremental-search-backward": {
                        this.searchBackward = true;
                        next = true;
                        break;
                    }
                    case "history-incremental-search-forward": {
                        this.searchBackward = false;
                        next = true;
                        break;
                    }
                    case "backward-delete-char": {
                        if (this.searchTerm.length() <= 0) break;
                        this.searchTerm.deleteCharAt(this.searchTerm.length() - 1);
                        break;
                    }
                    case "self-insert": {
                        this.searchTerm.append(this.getLastBinding());
                        break;
                    }
                    default: {
                        if (this.searchIndex != -1) {
                            this.history.moveTo(this.searchIndex);
                        }
                        this.pushBackBinding();
                        boolean bl = true;
                        return bl;
                    }
                }
                String pattern = this.doGetSearchPattern();
                if (pattern.length() == 0) {
                    this.buf.copyFrom(originalBuffer);
                    this.searchFailing = false;
                } else {
                    boolean nextOnly;
                    boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
                    Pattern pat = Pattern.compile(pattern, caseInsensitive ? 66 : 64);
                    Pair pair = null;
                    if (this.searchBackward) {
                        nextOnly = next;
                        pair = this.matches(pat, this.buf.toString(), this.searchIndex).stream().filter(p -> nextOnly ? (Integer)p.v < this.buf.cursor() : (Integer)p.v <= this.buf.cursor()).max(Comparator.comparing(Pair::getV)).orElse(null);
                        if (pair == null) {
                            pair = StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.history.reverseIterator(this.searchIndex < 0 ? this.history.last() : this.searchIndex - 1), 16), false).flatMap(e -> this.matches(pat, e.line(), e.index()).stream()).findFirst().orElse(null);
                        }
                    } else {
                        nextOnly = next;
                        pair = this.matches(pat, this.buf.toString(), this.searchIndex).stream().filter(p -> nextOnly ? (Integer)p.v > this.buf.cursor() : (Integer)p.v >= this.buf.cursor()).min(Comparator.comparing(Pair::getV)).orElse(null);
                        if (pair == null && (pair = (Pair)StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.history.iterator((this.searchIndex < 0 ? this.history.last() : this.searchIndex) + 1), 16), false).flatMap(e -> this.matches(pat, e.line(), e.index()).stream()).findFirst().orElse(null)) == null && this.searchIndex >= 0) {
                            pair = this.matches(pat, originalBuffer.toString(), -1).stream().min(Comparator.comparing(Pair::getV)).orElse(null);
                        }
                    }
                    if (pair != null) {
                        this.searchIndex = (Integer)pair.u;
                        this.buf.clear();
                        if (this.searchIndex >= 0) {
                            this.buf.write(this.history.get(this.searchIndex));
                        } else {
                            this.buf.write(originalBuffer.toString());
                        }
                        this.buf.cursor((Integer)pair.v);
                        this.searchFailing = false;
                    } else {
                        this.searchFailing = true;
                        this.beep();
                    }
                }
                this.redisplay();
                continue;
                break;
            }
        }
        catch (IOError e2) {
            if (!(e2.getCause() instanceof InterruptedException)) {
                throw e2;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.searchTerm = null;
            this.searchIndex = -1;
            this.post = null;
        }
    }

    private List<Pair<Integer, Integer>> matches(Pattern p, String line, int index) {
        ArrayList<Pair<Integer, Integer>> starts = new ArrayList<Pair<Integer, Integer>>();
        Matcher m = p.matcher(line);
        while (m.find()) {
            starts.add(new Pair<Integer, Integer>(index, m.start()));
        }
        return starts;
    }

    private String doGetSearchPattern() {
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < this.searchTerm.length(); ++i) {
            char c = this.searchTerm.charAt(i);
            if (Character.isLowerCase(c)) {
                if (inQuote) {
                    sb.append("\\E");
                    inQuote = false;
                }
                sb.append("[").append(Character.toLowerCase(c)).append(Character.toUpperCase(c)).append("]");
                continue;
            }
            if (!inQuote) {
                sb.append("\\Q");
                inQuote = true;
            }
            sb.append(c);
        }
        if (inQuote) {
            sb.append("\\E");
        }
        return sb.toString();
    }

    private void pushBackBinding() {
        this.pushBackBinding(false);
    }

    private void pushBackBinding(boolean skip) {
        String s = this.getLastBinding();
        if (s != null) {
            this.bindingReader.runMacro(s);
            this.skipRedisplay = skip;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    protected boolean historySearchForward() {
        int index;
        if (this.historyBuffer == null || this.buf.length() == 0 || !this.buf.toString().equals(this.history.current())) {
            this.historyBuffer = this.buf.copy();
            this.searchBuffer = this.getFirstWord();
        }
        if ((index = this.history.index() + 1) >= this.history.last() + 1) {
            this.history.moveToEnd();
            if (this.buf.toString().equals(this.historyBuffer.toString())) return false;
            this.setBuffer(this.historyBuffer.toString());
            this.historyBuffer = null;
            return true;
        }
        int searchIndex = this.searchForwards(this.searchBuffer.toString(), index, true);
        if (searchIndex == -1) {
            this.history.moveToEnd();
            if (this.buf.toString().equals(this.historyBuffer.toString())) return false;
            this.setBuffer(this.historyBuffer.toString());
            this.historyBuffer = null;
            return true;
        }
        if (this.history.moveTo(searchIndex)) {
            this.setBuffer(this.history.current());
            return true;
        }
        this.history.moveToEnd();
        this.setBuffer(this.historyBuffer.toString());
        return false;
    }

    private CharSequence getFirstWord() {
        int i;
        String s = this.buf.toString();
        for (i = 0; i < s.length() && !Character.isWhitespace(s.charAt(i)); ++i) {
        }
        return s.substring(0, i);
    }

    protected boolean historySearchBackward() {
        int searchIndex;
        if (this.historyBuffer == null || this.buf.length() == 0 || !this.buf.toString().equals(this.history.current())) {
            this.historyBuffer = this.buf.copy();
            this.searchBuffer = this.getFirstWord();
        }
        if ((searchIndex = this.searchBackwards(this.searchBuffer.toString(), this.history.index(), true)) == -1) {
            return false;
        }
        if (!this.history.moveTo(searchIndex)) {
            return false;
        }
        this.setBuffer(this.history.current());
        return true;
    }

    public int searchBackwards(String searchTerm, int startIndex) {
        return this.searchBackwards(searchTerm, startIndex, false);
    }

    public int searchBackwards(String searchTerm) {
        return this.searchBackwards(searchTerm, this.history.index(), false);
    }

    public int searchBackwards(String searchTerm, int startIndex, boolean startsWith) {
        boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
        if (caseInsensitive) {
            searchTerm = searchTerm.toLowerCase();
        }
        ListIterator<History.Entry> it = this.history.iterator(startIndex);
        while (it.hasPrevious()) {
            History.Entry e = it.previous();
            String line = e.line();
            if (caseInsensitive) {
                line = line.toLowerCase();
            }
            int idx = line.indexOf(searchTerm);
            if ((!startsWith || idx != 0) && (startsWith || idx < 0)) continue;
            return e.index();
        }
        return -1;
    }

    public int searchForwards(String searchTerm, int startIndex, boolean startsWith) {
        boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
        if (caseInsensitive) {
            searchTerm = searchTerm.toLowerCase();
        }
        if (startIndex > this.history.last()) {
            startIndex = this.history.last();
        }
        ListIterator<History.Entry> it = this.history.iterator(startIndex);
        if (this.searchIndex != -1 && it.hasNext()) {
            it.next();
        }
        while (it.hasNext()) {
            History.Entry e = it.next();
            String line = e.line();
            if (caseInsensitive) {
                line = line.toLowerCase();
            }
            int idx = line.indexOf(searchTerm);
            if ((!startsWith || idx != 0) && (startsWith || idx < 0)) continue;
            return e.index();
        }
        return -1;
    }

    public int searchForwards(String searchTerm, int startIndex) {
        return this.searchForwards(searchTerm, startIndex, false);
    }

    public int searchForwards(String searchTerm) {
        return this.searchForwards(searchTerm, this.history.index());
    }

    protected boolean quit() {
        this.getBuffer().clear();
        return this.acceptLine();
    }

    protected boolean acceptAndHold() {
        this.nextCommandFromHistory = false;
        this.acceptLine();
        if (!this.buf.toString().isEmpty()) {
            this.nextHistoryId = Integer.MAX_VALUE;
            this.nextCommandFromHistory = true;
        }
        return this.nextCommandFromHistory;
    }

    protected boolean acceptLineAndDownHistory() {
        this.nextCommandFromHistory = false;
        this.acceptLine();
        if (this.nextHistoryId < 0) {
            this.nextHistoryId = this.history.index();
        }
        if (this.history.size() > this.nextHistoryId + 1) {
            ++this.nextHistoryId;
            this.nextCommandFromHistory = true;
        }
        return this.nextCommandFromHistory;
    }

    protected boolean acceptAndInferNextHistory() {
        this.nextCommandFromHistory = false;
        this.acceptLine();
        if (!this.buf.toString().isEmpty()) {
            this.nextHistoryId = this.searchBackwards(this.buf.toString(), this.history.last());
            if (this.nextHistoryId >= 0 && this.history.size() > this.nextHistoryId + 1) {
                ++this.nextHistoryId;
                this.nextCommandFromHistory = true;
            }
        }
        return this.nextCommandFromHistory;
    }

    protected boolean acceptLine() {
        this.parsedLine = null;
        int curPos = 0;
        if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
            try {
                String str = this.buf.toString();
                String exp = this.expander.expandHistory(this.history, str);
                if (!exp.equals(str)) {
                    this.buf.clear();
                    this.buf.write(exp);
                    if (this.isSet(LineReader.Option.HISTORY_VERIFY)) {
                        return true;
                    }
                }
            }
            catch (IllegalArgumentException str) {
                // empty catch block
            }
        }
        try {
            curPos = this.buf.cursor();
            this.parsedLine = this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.ACCEPT_LINE);
        }
        catch (EOFError e) {
            StringBuilder sb = new StringBuilder("\n");
            this.indention(e.getOpenBrackets(), sb);
            int curMove = sb.length();
            if (this.isSet(LineReader.Option.INSERT_BRACKET) && e.getOpenBrackets() > 1 && e.getNextClosingBracket() != null) {
                sb.append('\n');
                this.indention(e.getOpenBrackets() - 1, sb);
                sb.append(e.getNextClosingBracket());
            }
            this.buf.write(sb.toString());
            this.buf.cursor(curPos + curMove);
            return true;
        }
        catch (SyntaxError syntaxError) {
            // empty catch block
        }
        this.callWidget("callback-finish");
        this.state = State.DONE;
        return true;
    }

    void indention(int nb, StringBuilder sb) {
        int indent = this.getInt("indentation", 0) * nb;
        for (int i = 0; i < indent; ++i) {
            sb.append(' ');
        }
    }

    protected boolean selfInsert() {
        for (int count = this.count; count > 0; --count) {
            this.putString(this.getLastBinding());
        }
        return true;
    }

    protected boolean selfInsertUnmeta() {
        if (this.getLastBinding().charAt(0) == '\u001b') {
            String s = this.getLastBinding().substring(1);
            if ("\r".equals(s)) {
                s = "\n";
            }
            for (int count = this.count; count > 0; --count) {
                this.putString(s);
            }
            return true;
        }
        return false;
    }

    protected boolean overwriteMode() {
        this.overTyping = !this.overTyping;
        return true;
    }

    protected boolean beginningOfBufferOrHistory() {
        if (this.findbol() != 0) {
            this.buf.cursor(0);
            return true;
        }
        return this.beginningOfHistory();
    }

    protected boolean beginningOfHistory() {
        if (this.history.moveToFirst()) {
            this.setBuffer(this.history.current());
            return true;
        }
        return false;
    }

    protected boolean endOfBufferOrHistory() {
        if (this.findeol() != this.buf.length()) {
            this.buf.cursor(this.buf.length());
            return true;
        }
        return this.endOfHistory();
    }

    protected boolean endOfHistory() {
        if (this.history.moveToLast()) {
            this.setBuffer(this.history.current());
            return true;
        }
        return false;
    }

    protected boolean beginningOfLineHist() {
        if (this.count < 0) {
            return this.callNeg(this::endOfLineHist);
        }
        while (this.count-- > 0) {
            int bol = this.findbol();
            if (bol != this.buf.cursor()) {
                this.buf.cursor(bol);
                continue;
            }
            this.moveHistory(false);
            this.buf.cursor(0);
        }
        return true;
    }

    protected boolean endOfLineHist() {
        if (this.count < 0) {
            return this.callNeg(this::beginningOfLineHist);
        }
        while (this.count-- > 0) {
            int eol = this.findeol();
            if (eol != this.buf.cursor()) {
                this.buf.cursor(eol);
                continue;
            }
            this.moveHistory(true);
        }
        return true;
    }

    protected boolean upHistory() {
        while (this.count-- > 0) {
            if (this.moveHistory(false)) continue;
            return !this.isSet(LineReader.Option.HISTORY_BEEP);
        }
        return true;
    }

    protected boolean downHistory() {
        while (this.count-- > 0) {
            if (this.moveHistory(true)) continue;
            return !this.isSet(LineReader.Option.HISTORY_BEEP);
        }
        return true;
    }

    protected boolean viUpLineOrHistory() {
        return this.upLine() || this.upHistory() && this.viFirstNonBlank();
    }

    protected boolean viDownLineOrHistory() {
        return this.downLine() || this.downHistory() && this.viFirstNonBlank();
    }

    protected boolean upLine() {
        return this.buf.up();
    }

    protected boolean downLine() {
        return this.buf.down();
    }

    protected boolean upLineOrHistory() {
        return this.upLine() || this.upHistory();
    }

    protected boolean upLineOrSearch() {
        return this.upLine() || this.historySearchBackward();
    }

    protected boolean downLineOrHistory() {
        return this.downLine() || this.downHistory();
    }

    protected boolean downLineOrSearch() {
        return this.downLine() || this.historySearchForward();
    }

    protected boolean viCmdMode() {
        if (this.state == State.NORMAL) {
            this.buf.move(-1);
        }
        return this.setKeyMap("vicmd");
    }

    protected boolean viInsert() {
        return this.setKeyMap("viins");
    }

    protected boolean viAddNext() {
        this.buf.move(1);
        return this.setKeyMap("viins");
    }

    protected boolean viAddEol() {
        return this.endOfLine() && this.setKeyMap("viins");
    }

    protected boolean emacsEditingMode() {
        return this.setKeyMap("emacs");
    }

    protected boolean viChangeWholeLine() {
        return this.viFirstNonBlank() && this.viChangeEol();
    }

    protected boolean viChangeEol() {
        return this.viChange(this.buf.cursor(), this.buf.length()) && this.setKeyMap("viins");
    }

    protected boolean viKillEol() {
        int eol = this.findeol();
        if (this.buf.cursor() == eol) {
            return false;
        }
        this.killRing.add(this.buf.substring(this.buf.cursor(), eol));
        this.buf.delete(eol - this.buf.cursor());
        return true;
    }

    protected boolean quotedInsert() {
        int c = this.readCharacter();
        while (this.count-- > 0) {
            this.putString(new String(Character.toChars(c)));
        }
        return true;
    }

    protected boolean viJoin() {
        if (this.buf.down()) {
            while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
            }
            this.buf.backspace();
            this.buf.write(32);
            this.buf.move(-1);
            return true;
        }
        return false;
    }

    protected boolean viKillWholeLine() {
        return this.killWholeLine() && this.setKeyMap("viins");
    }

    protected boolean viInsertBol() {
        return this.beginningOfLine() && this.setKeyMap("viins");
    }

    protected boolean backwardDeleteChar() {
        if (this.count < 0) {
            return this.callNeg(this::deleteChar);
        }
        if (this.buf.cursor() == 0) {
            return false;
        }
        this.buf.backspace(this.count);
        return true;
    }

    protected boolean viFirstNonBlank() {
        this.beginningOfLine();
        while (this.buf.cursor() < this.buf.length() && this.isWhitespace(this.buf.currChar())) {
            this.buf.move(1);
        }
        return true;
    }

    protected boolean viBeginningOfLine() {
        this.buf.cursor(this.findbol());
        return true;
    }

    protected boolean viEndOfLine() {
        if (this.count < 0) {
            return false;
        }
        while (this.count-- > 0) {
            this.buf.cursor(this.findeol() + 1);
        }
        this.buf.move(-1);
        return true;
    }

    protected boolean beginningOfLine() {
        while (this.count-- > 0) {
            while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
            }
        }
        return true;
    }

    protected boolean endOfLine() {
        while (this.count-- > 0) {
            while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
            }
        }
        return true;
    }

    protected boolean deleteChar() {
        if (this.count < 0) {
            return this.callNeg(this::backwardDeleteChar);
        }
        if (this.buf.cursor() == this.buf.length()) {
            return false;
        }
        this.buf.delete(this.count);
        return true;
    }

    protected boolean viBackwardDeleteChar() {
        for (int i = 0; i < this.count; ++i) {
            if (this.buf.backspace()) continue;
            return false;
        }
        return true;
    }

    protected boolean viDeleteChar() {
        for (int i = 0; i < this.count; ++i) {
            if (this.buf.delete()) continue;
            return false;
        }
        return true;
    }

    protected boolean viSwapCase() {
        for (int i = 0; i < this.count; ++i) {
            if (this.buf.cursor() >= this.buf.length()) {
                return false;
            }
            int ch = this.buf.atChar(this.buf.cursor());
            ch = this.switchCase(ch);
            this.buf.currChar(ch);
            this.buf.move(1);
        }
        return true;
    }

    protected boolean viReplaceChars() {
        int c = this.readCharacter();
        if (c < 0 || c == 27 || c == 3) {
            return true;
        }
        for (int i = 0; i < this.count; ++i) {
            if (this.buf.currChar((char)c)) {
                if (i >= this.count - 1) continue;
                this.buf.move(1);
                continue;
            }
            return false;
        }
        return true;
    }

    protected boolean viChange(int startPos, int endPos) {
        return this.doViDeleteOrChange(startPos, endPos, true);
    }

    protected boolean viDeleteTo(int startPos, int endPos) {
        return this.doViDeleteOrChange(startPos, endPos, false);
    }

    protected boolean doViDeleteOrChange(int startPos, int endPos, boolean isChange) {
        if (startPos == endPos) {
            return true;
        }
        if (endPos < startPos) {
            int tmp = endPos;
            endPos = startPos;
            startPos = tmp;
        }
        this.buf.cursor(startPos);
        this.buf.delete(endPos - startPos);
        if (!isChange && startPos > 0 && startPos == this.buf.length()) {
            this.buf.move(-1);
        }
        return true;
    }

    protected boolean viYankTo(int startPos, int endPos) {
        int cursorPos = startPos;
        if (endPos < startPos) {
            int tmp = endPos;
            endPos = startPos;
            startPos = tmp;
        }
        if (startPos == endPos) {
            this.yankBuffer = DEFAULT_BELL_STYLE;
            return true;
        }
        this.yankBuffer = this.buf.substring(startPos, endPos);
        this.buf.cursor(cursorPos);
        return true;
    }

    protected boolean viOpenLineAbove() {
        while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
        }
        this.buf.write(10);
        this.buf.move(-1);
        return this.setKeyMap("viins");
    }

    protected boolean viOpenLineBelow() {
        while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
        }
        this.buf.write(10);
        return this.setKeyMap("viins");
    }

    protected boolean viPutAfter() {
        if (this.yankBuffer.indexOf(10) >= 0) {
            while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
            }
            this.buf.move(1);
            this.putString(this.yankBuffer);
            this.buf.move(-this.yankBuffer.length());
        } else if (!this.yankBuffer.isEmpty()) {
            if (this.buf.cursor() < this.buf.length()) {
                this.buf.move(1);
            }
            for (int i = 0; i < this.count; ++i) {
                this.putString(this.yankBuffer);
            }
            this.buf.move(-1);
        }
        return true;
    }

    protected boolean viPutBefore() {
        if (this.yankBuffer.indexOf(10) >= 0) {
            while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
            }
            this.putString(this.yankBuffer);
            this.buf.move(-this.yankBuffer.length());
        } else if (!this.yankBuffer.isEmpty()) {
            if (this.buf.cursor() > 0) {
                this.buf.move(-1);
            }
            for (int i = 0; i < this.count; ++i) {
                this.putString(this.yankBuffer);
            }
            this.buf.move(-1);
        }
        return true;
    }

    protected boolean doLowercaseVersion() {
        this.bindingReader.runMacro(this.getLastBinding().toLowerCase());
        return true;
    }

    protected boolean setMarkCommand() {
        if (this.count < 0) {
            this.regionActive = LineReader.RegionType.NONE;
            return true;
        }
        this.regionMark = this.buf.cursor();
        this.regionActive = LineReader.RegionType.CHAR;
        return true;
    }

    protected boolean exchangePointAndMark() {
        if (this.count == 0) {
            this.regionActive = LineReader.RegionType.CHAR;
            return true;
        }
        int x = this.regionMark;
        this.regionMark = this.buf.cursor();
        this.buf.cursor(x);
        if (this.buf.cursor() > this.buf.length()) {
            this.buf.cursor(this.buf.length());
        }
        if (this.count > 0) {
            this.regionActive = LineReader.RegionType.CHAR;
        }
        return true;
    }

    protected boolean visualMode() {
        if (this.isInViMoveOperation()) {
            this.isArgDigit = true;
            this.forceLine = false;
            this.forceChar = true;
            return true;
        }
        if (this.regionActive == LineReader.RegionType.NONE) {
            this.regionMark = this.buf.cursor();
            this.regionActive = LineReader.RegionType.CHAR;
        } else if (this.regionActive == LineReader.RegionType.CHAR) {
            this.regionActive = LineReader.RegionType.NONE;
        } else if (this.regionActive == LineReader.RegionType.LINE) {
            this.regionActive = LineReader.RegionType.CHAR;
        }
        return true;
    }

    protected boolean visualLineMode() {
        if (this.isInViMoveOperation()) {
            this.isArgDigit = true;
            this.forceLine = true;
            this.forceChar = false;
            return true;
        }
        if (this.regionActive == LineReader.RegionType.NONE) {
            this.regionMark = this.buf.cursor();
            this.regionActive = LineReader.RegionType.LINE;
        } else if (this.regionActive == LineReader.RegionType.CHAR) {
            this.regionActive = LineReader.RegionType.LINE;
        } else if (this.regionActive == LineReader.RegionType.LINE) {
            this.regionActive = LineReader.RegionType.NONE;
        }
        return true;
    }

    protected boolean deactivateRegion() {
        this.regionActive = LineReader.RegionType.NONE;
        return true;
    }

    protected boolean whatCursorPosition() {
        this.post = () -> {
            AttributedStringBuilder sb = new AttributedStringBuilder();
            if (this.buf.cursor() < this.buf.length()) {
                int c = this.buf.currChar();
                sb.append("Char: ");
                if (c == 32) {
                    sb.append("SPC");
                } else if (c == 10) {
                    sb.append("LFD");
                } else if (c < 32) {
                    sb.append('^');
                    sb.append((char)(c + 65 - 1));
                } else if (c == 127) {
                    sb.append("^?");
                } else {
                    sb.append((char)c);
                }
                sb.append(" (");
                sb.append("0").append(Integer.toOctalString(c)).append(" ");
                sb.append(Integer.toString(c)).append(" ");
                sb.append("0x").append(Integer.toHexString(c)).append(" ");
                sb.append(DESC_SUFFIX);
            } else {
                sb.append("EOF");
            }
            sb.append("   ");
            sb.append("point ");
            sb.append(Integer.toString(this.buf.cursor() + 1));
            sb.append(" of ");
            sb.append(Integer.toString(this.buf.length() + 1));
            sb.append(" (");
            sb.append(Integer.toString(this.buf.length() == 0 ? 100 : 100 * this.buf.cursor() / this.buf.length()));
            sb.append("%)");
            sb.append("   ");
            sb.append("column ");
            sb.append(Integer.toString(this.buf.cursor() - this.findbol()));
            return sb.toAttributedString();
        };
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean editAndExecute() {
        boolean out = true;
        File file = null;
        try {
            file = File.createTempFile("jline-execute-", null);
            try (FileWriter writer = new FileWriter(file);){
                writer.write(this.buf.toString());
            }
            this.editAndAddInBuffer(file);
        }
        catch (Exception e) {
            e.printStackTrace(this.terminal.writer());
            out = false;
        }
        finally {
            this.state = State.IGNORE;
            if (file != null && file.exists()) {
                file.delete();
            }
        }
        return out;
    }

    protected Map<String, Widget> builtinWidgets() {
        HashMap<String, Widget> widgets = new HashMap<String, Widget>();
        this.addBuiltinWidget(widgets, "accept-and-infer-next-history", this::acceptAndInferNextHistory);
        this.addBuiltinWidget(widgets, "accept-and-hold", this::acceptAndHold);
        this.addBuiltinWidget(widgets, "accept-line", this::acceptLine);
        this.addBuiltinWidget(widgets, "accept-line-and-down-history", this::acceptLineAndDownHistory);
        this.addBuiltinWidget(widgets, "argument-base", this::argumentBase);
        this.addBuiltinWidget(widgets, "backward-char", this::backwardChar);
        this.addBuiltinWidget(widgets, "backward-delete-char", this::backwardDeleteChar);
        this.addBuiltinWidget(widgets, "backward-delete-word", this::backwardDeleteWord);
        this.addBuiltinWidget(widgets, "backward-kill-line", this::backwardKillLine);
        this.addBuiltinWidget(widgets, "backward-kill-word", this::backwardKillWord);
        this.addBuiltinWidget(widgets, "backward-word", this::backwardWord);
        this.addBuiltinWidget(widgets, "beep", this::beep);
        this.addBuiltinWidget(widgets, "beginning-of-buffer-or-history", this::beginningOfBufferOrHistory);
        this.addBuiltinWidget(widgets, "beginning-of-history", this::beginningOfHistory);
        this.addBuiltinWidget(widgets, "beginning-of-line", this::beginningOfLine);
        this.addBuiltinWidget(widgets, "beginning-of-line-hist", this::beginningOfLineHist);
        this.addBuiltinWidget(widgets, "capitalize-word", this::capitalizeWord);
        this.addBuiltinWidget(widgets, "clear", this::clear);
        this.addBuiltinWidget(widgets, "clear-screen", this::clearScreen);
        this.addBuiltinWidget(widgets, "complete-prefix", this::completePrefix);
        this.addBuiltinWidget(widgets, "complete-word", this::completeWord);
        this.addBuiltinWidget(widgets, "copy-prev-word", this::copyPrevWord);
        this.addBuiltinWidget(widgets, "copy-region-as-kill", this::copyRegionAsKill);
        this.addBuiltinWidget(widgets, "delete-char", this::deleteChar);
        this.addBuiltinWidget(widgets, "delete-char-or-list", this::deleteCharOrList);
        this.addBuiltinWidget(widgets, "delete-word", this::deleteWord);
        this.addBuiltinWidget(widgets, "digit-argument", this::digitArgument);
        this.addBuiltinWidget(widgets, "do-lowercase-version", this::doLowercaseVersion);
        this.addBuiltinWidget(widgets, "down-case-word", this::downCaseWord);
        this.addBuiltinWidget(widgets, "down-line", this::downLine);
        this.addBuiltinWidget(widgets, "down-line-or-history", this::downLineOrHistory);
        this.addBuiltinWidget(widgets, "down-line-or-search", this::downLineOrSearch);
        this.addBuiltinWidget(widgets, "down-history", this::downHistory);
        this.addBuiltinWidget(widgets, "edit-and-execute-command", this::editAndExecute);
        this.addBuiltinWidget(widgets, "emacs-editing-mode", this::emacsEditingMode);
        this.addBuiltinWidget(widgets, "emacs-backward-word", this::emacsBackwardWord);
        this.addBuiltinWidget(widgets, "emacs-forward-word", this::emacsForwardWord);
        this.addBuiltinWidget(widgets, "end-of-buffer-or-history", this::endOfBufferOrHistory);
        this.addBuiltinWidget(widgets, "end-of-history", this::endOfHistory);
        this.addBuiltinWidget(widgets, "end-of-line", this::endOfLine);
        this.addBuiltinWidget(widgets, "end-of-line-hist", this::endOfLineHist);
        this.addBuiltinWidget(widgets, "exchange-point-and-mark", this::exchangePointAndMark);
        this.addBuiltinWidget(widgets, "expand-history", this::expandHistory);
        this.addBuiltinWidget(widgets, "expand-or-complete", this::expandOrComplete);
        this.addBuiltinWidget(widgets, "expand-or-complete-prefix", this::expandOrCompletePrefix);
        this.addBuiltinWidget(widgets, "expand-word", this::expandWord);
        this.addBuiltinWidget(widgets, "fresh-line", this::freshLine);
        this.addBuiltinWidget(widgets, "forward-char", this::forwardChar);
        this.addBuiltinWidget(widgets, "forward-word", this::forwardWord);
        this.addBuiltinWidget(widgets, "history-incremental-search-backward", this::historyIncrementalSearchBackward);
        this.addBuiltinWidget(widgets, "history-incremental-search-forward", this::historyIncrementalSearchForward);
        this.addBuiltinWidget(widgets, "history-search-backward", this::historySearchBackward);
        this.addBuiltinWidget(widgets, "history-search-forward", this::historySearchForward);
        this.addBuiltinWidget(widgets, "insert-close-curly", this::insertCloseCurly);
        this.addBuiltinWidget(widgets, "insert-close-paren", this::insertCloseParen);
        this.addBuiltinWidget(widgets, "insert-close-square", this::insertCloseSquare);
        this.addBuiltinWidget(widgets, "insert-comment", this::insertComment);
        this.addBuiltinWidget(widgets, "kill-buffer", this::killBuffer);
        this.addBuiltinWidget(widgets, "kill-line", this::killLine);
        this.addBuiltinWidget(widgets, "kill-region", this::killRegion);
        this.addBuiltinWidget(widgets, "kill-whole-line", this::killWholeLine);
        this.addBuiltinWidget(widgets, "kill-word", this::killWord);
        this.addBuiltinWidget(widgets, "list-choices", this::listChoices);
        this.addBuiltinWidget(widgets, "menu-complete", this::menuComplete);
        this.addBuiltinWidget(widgets, "menu-expand-or-complete", this::menuExpandOrComplete);
        this.addBuiltinWidget(widgets, "neg-argument", this::negArgument);
        this.addBuiltinWidget(widgets, "overwrite-mode", this::overwriteMode);
        this.addBuiltinWidget(widgets, "quoted-insert", this::quotedInsert);
        this.addBuiltinWidget(widgets, "redisplay", this::redisplay);
        this.addBuiltinWidget(widgets, "redraw-line", this::redrawLine);
        this.addBuiltinWidget(widgets, "redo", this::redo);
        this.addBuiltinWidget(widgets, "self-insert", this::selfInsert);
        this.addBuiltinWidget(widgets, "self-insert-unmeta", this::selfInsertUnmeta);
        this.addBuiltinWidget(widgets, "abort", this::sendBreak);
        this.addBuiltinWidget(widgets, "set-mark-command", this::setMarkCommand);
        this.addBuiltinWidget(widgets, "transpose-chars", this::transposeChars);
        this.addBuiltinWidget(widgets, "transpose-words", this::transposeWords);
        this.addBuiltinWidget(widgets, "undefined-key", this::undefinedKey);
        this.addBuiltinWidget(widgets, "universal-argument", this::universalArgument);
        this.addBuiltinWidget(widgets, "undo", this::undo);
        this.addBuiltinWidget(widgets, "up-case-word", this::upCaseWord);
        this.addBuiltinWidget(widgets, "up-history", this::upHistory);
        this.addBuiltinWidget(widgets, "up-line", this::upLine);
        this.addBuiltinWidget(widgets, "up-line-or-history", this::upLineOrHistory);
        this.addBuiltinWidget(widgets, "up-line-or-search", this::upLineOrSearch);
        this.addBuiltinWidget(widgets, "vi-add-eol", this::viAddEol);
        this.addBuiltinWidget(widgets, "vi-add-next", this::viAddNext);
        this.addBuiltinWidget(widgets, "vi-backward-char", this::viBackwardChar);
        this.addBuiltinWidget(widgets, "vi-backward-delete-char", this::viBackwardDeleteChar);
        this.addBuiltinWidget(widgets, "vi-backward-blank-word", this::viBackwardBlankWord);
        this.addBuiltinWidget(widgets, "vi-backward-blank-word-end", this::viBackwardBlankWordEnd);
        this.addBuiltinWidget(widgets, "vi-backward-kill-word", this::viBackwardKillWord);
        this.addBuiltinWidget(widgets, "vi-backward-word", this::viBackwardWord);
        this.addBuiltinWidget(widgets, "vi-backward-word-end", this::viBackwardWordEnd);
        this.addBuiltinWidget(widgets, "vi-beginning-of-line", this::viBeginningOfLine);
        this.addBuiltinWidget(widgets, "vi-cmd-mode", this::viCmdMode);
        this.addBuiltinWidget(widgets, "vi-digit-or-beginning-of-line", this::viDigitOrBeginningOfLine);
        this.addBuiltinWidget(widgets, "vi-down-line-or-history", this::viDownLineOrHistory);
        this.addBuiltinWidget(widgets, "vi-change-to", this::viChange);
        this.addBuiltinWidget(widgets, "vi-change-eol", this::viChangeEol);
        this.addBuiltinWidget(widgets, "vi-change-whole-line", this::viChangeWholeLine);
        this.addBuiltinWidget(widgets, "vi-delete-char", this::viDeleteChar);
        this.addBuiltinWidget(widgets, "vi-delete", this::viDelete);
        this.addBuiltinWidget(widgets, "vi-end-of-line", this::viEndOfLine);
        this.addBuiltinWidget(widgets, "vi-kill-eol", this::viKillEol);
        this.addBuiltinWidget(widgets, "vi-first-non-blank", this::viFirstNonBlank);
        this.addBuiltinWidget(widgets, "vi-find-next-char", this::viFindNextChar);
        this.addBuiltinWidget(widgets, "vi-find-next-char-skip", this::viFindNextCharSkip);
        this.addBuiltinWidget(widgets, "vi-find-prev-char", this::viFindPrevChar);
        this.addBuiltinWidget(widgets, "vi-find-prev-char-skip", this::viFindPrevCharSkip);
        this.addBuiltinWidget(widgets, "vi-forward-blank-word", this::viForwardBlankWord);
        this.addBuiltinWidget(widgets, "vi-forward-blank-word-end", this::viForwardBlankWordEnd);
        this.addBuiltinWidget(widgets, "vi-forward-char", this::viForwardChar);
        this.addBuiltinWidget(widgets, "vi-forward-word", this::viForwardWord);
        this.addBuiltinWidget(widgets, "vi-forward-word", this::viForwardWord);
        this.addBuiltinWidget(widgets, "vi-forward-word-end", this::viForwardWordEnd);
        this.addBuiltinWidget(widgets, "vi-history-search-backward", this::viHistorySearchBackward);
        this.addBuiltinWidget(widgets, "vi-history-search-forward", this::viHistorySearchForward);
        this.addBuiltinWidget(widgets, "vi-insert", this::viInsert);
        this.addBuiltinWidget(widgets, "vi-insert-bol", this::viInsertBol);
        this.addBuiltinWidget(widgets, "vi-insert-comment", this::viInsertComment);
        this.addBuiltinWidget(widgets, "vi-join", this::viJoin);
        this.addBuiltinWidget(widgets, "vi-kill-line", this::viKillWholeLine);
        this.addBuiltinWidget(widgets, "vi-match-bracket", this::viMatchBracket);
        this.addBuiltinWidget(widgets, "vi-open-line-above", this::viOpenLineAbove);
        this.addBuiltinWidget(widgets, "vi-open-line-below", this::viOpenLineBelow);
        this.addBuiltinWidget(widgets, "vi-put-after", this::viPutAfter);
        this.addBuiltinWidget(widgets, "vi-put-before", this::viPutBefore);
        this.addBuiltinWidget(widgets, "vi-repeat-find", this::viRepeatFind);
        this.addBuiltinWidget(widgets, "vi-repeat-search", this::viRepeatSearch);
        this.addBuiltinWidget(widgets, "vi-replace-chars", this::viReplaceChars);
        this.addBuiltinWidget(widgets, "vi-rev-repeat-find", this::viRevRepeatFind);
        this.addBuiltinWidget(widgets, "vi-rev-repeat-search", this::viRevRepeatSearch);
        this.addBuiltinWidget(widgets, "vi-swap-case", this::viSwapCase);
        this.addBuiltinWidget(widgets, "vi-up-line-or-history", this::viUpLineOrHistory);
        this.addBuiltinWidget(widgets, "vi-yank", this::viYankTo);
        this.addBuiltinWidget(widgets, "vi-yank-whole-line", this::viYankWholeLine);
        this.addBuiltinWidget(widgets, "visual-line-mode", this::visualLineMode);
        this.addBuiltinWidget(widgets, "visual-mode", this::visualMode);
        this.addBuiltinWidget(widgets, "what-cursor-position", this::whatCursorPosition);
        this.addBuiltinWidget(widgets, "yank", this::yank);
        this.addBuiltinWidget(widgets, "yank-pop", this::yankPop);
        this.addBuiltinWidget(widgets, "mouse", this::mouse);
        this.addBuiltinWidget(widgets, "begin-paste", this::beginPaste);
        this.addBuiltinWidget(widgets, "terminal-focus-in", this::focusIn);
        this.addBuiltinWidget(widgets, "terminal-focus-out", this::focusOut);
        return widgets;
    }

    private void addBuiltinWidget(Map<String, Widget> widgets, String name, Widget widget) {
        widgets.put(name, this.namedWidget("." + name, widget));
    }

    private Widget namedWidget(final String name, final Widget widget) {
        return new Widget(){
            final /* synthetic */ LineReaderImpl this$0;
            {
                this.this$0 = this$0;
            }

            public String toString() {
                return name;
            }

            @Override
            public boolean apply() {
                return widget.apply();
            }
        };
    }

    public boolean redisplay() {
        this.redisplay(true);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void redisplay(boolean flush) {
        try {
            List<Object> newLines;
            this.lock.lock();
            if (this.skipRedisplay) {
                this.skipRedisplay = false;
                return;
            }
            Status status = Status.getStatus(this.terminal, false);
            if (status != null) {
                status.redraw();
            }
            if (this.size.getRows() > 0 && this.size.getRows() < 3) {
                int length;
                AttributedStringBuilder sb = new AttributedStringBuilder().tabs(this.getTabWidth());
                sb.append(this.prompt);
                this.concat(this.getHighlightedBuffer(this.buf.toString()).columnSplitLength(Integer.MAX_VALUE), sb);
                AttributedString full = sb.toAttributedString();
                sb.setLength(0);
                sb.append(this.prompt);
                String line = this.buf.upToCursor();
                if (this.maskingCallback != null) {
                    line = this.maskingCallback.display(line);
                }
                this.concat(new AttributedString(line).columnSplitLength(Integer.MAX_VALUE), sb);
                AttributedString toCursor = sb.toAttributedString();
                int w = WCWidth.wcwidth(8230);
                int width = this.size.getColumns();
                int cursor = toCursor.columnLength();
                int inc = width / 2 + 1;
                while (cursor <= this.smallTerminalOffset + w) {
                    this.smallTerminalOffset -= inc;
                }
                while (cursor >= this.smallTerminalOffset + width - w) {
                    this.smallTerminalOffset += inc;
                }
                if (this.smallTerminalOffset > 0) {
                    sb.setLength(0);
                    sb.append("\u2026");
                    sb.append(full.columnSubSequence(this.smallTerminalOffset + w, Integer.MAX_VALUE));
                    full = sb.toAttributedString();
                }
                if ((length = full.columnLength()) >= this.smallTerminalOffset + width) {
                    sb.setLength(0);
                    sb.append(full.columnSubSequence(0, width - w));
                    sb.append("\u2026");
                    full = sb.toAttributedString();
                }
                this.display.update(Collections.singletonList(full), cursor - this.smallTerminalOffset, flush);
                return;
            }
            ArrayList<AttributedString> secondaryPrompts = new ArrayList<AttributedString>();
            AttributedString full = this.getDisplayedBufferWithPrompts(secondaryPrompts);
            if (this.size.getColumns() <= 0) {
                newLines = new ArrayList<AttributedString>();
                newLines.add(full);
            } else {
                newLines = full.columnSplitLength(this.size.getColumns(), true, this.display.delayLineWrap());
            }
            List<Object> rightPromptLines = this.rightPrompt.length() == 0 || this.size.getColumns() <= 0 ? new ArrayList() : this.rightPrompt.columnSplitLength(this.size.getColumns());
            while (newLines.size() < rightPromptLines.size()) {
                newLines.add(new AttributedString(DEFAULT_BELL_STYLE));
            }
            for (int i = 0; i < rightPromptLines.size(); ++i) {
                AttributedString line = (AttributedString)rightPromptLines.get(i);
                newLines.set(i, this.addRightPrompt(line, (AttributedString)newLines.get(i)));
            }
            int cursorPos = -1;
            int cursorNewLinesId = -1;
            int cursorColPos = -1;
            if (this.size.getColumns() > 0) {
                AttributedStringBuilder sb = new AttributedStringBuilder().tabs(this.getTabWidth());
                sb.append(this.prompt);
                String buffer = this.buf.upToCursor();
                if (this.maskingCallback != null) {
                    buffer = this.maskingCallback.display(buffer);
                }
                sb.append(this.insertSecondaryPrompts(new AttributedString(buffer), secondaryPrompts, false));
                List<AttributedString> promptLines = sb.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
                if (!promptLines.isEmpty()) {
                    cursorNewLinesId = promptLines.size() - 1;
                    cursorColPos = promptLines.get(promptLines.size() - 1).columnLength();
                    cursorPos = this.size.cursorPos(cursorNewLinesId, cursorColPos);
                }
            }
            ArrayList<AttributedString> newLinesToDisplay = new ArrayList();
            int displaySize = this.displayRows(status);
            if (newLines.size() > displaySize && !this.isTerminalDumb()) {
                StringBuilder sb = new StringBuilder(">....");
                for (int i = sb.toString().length(); i < this.size.getColumns(); ++i) {
                    sb.append(" ");
                }
                AttributedString partialCommandInfo = new AttributedString(sb.toString());
                int lineId = newLines.size() - displaySize + 1;
                int endId = displaySize;
                int startId = 1;
                if (lineId > cursorNewLinesId) {
                    lineId = cursorNewLinesId;
                    endId = displaySize - 1;
                    startId = 0;
                } else {
                    newLinesToDisplay.add(partialCommandInfo);
                }
                int cursorRowPos = 0;
                for (int i = startId; i < endId; ++i) {
                    if (cursorNewLinesId == lineId) {
                        cursorRowPos = i;
                    }
                    newLinesToDisplay.add((AttributedString)newLines.get(lineId++));
                }
                if (startId == 0) {
                    newLinesToDisplay.add(partialCommandInfo);
                }
                cursorPos = this.size.cursorPos(cursorRowPos, cursorColPos);
            } else {
                newLinesToDisplay = newLines;
            }
            this.display.update(newLinesToDisplay, cursorPos, flush);
        }
        finally {
            this.lock.unlock();
        }
    }

    private void concat(List<AttributedString> lines, AttributedStringBuilder sb) {
        if (lines.size() > 1) {
            for (int i = 0; i < lines.size() - 1; ++i) {
                sb.append(lines.get(i));
                sb.style(sb.style().inverse());
                sb.append("\\n");
                sb.style(sb.style().inverseOff());
            }
        }
        sb.append(lines.get(lines.size() - 1));
    }

    private String matchPreviousCommand(String buffer) {
        if (buffer.length() == 0) {
            return DEFAULT_BELL_STYLE;
        }
        History history = this.getHistory();
        StringBuilder sb = new StringBuilder();
        for (char c : buffer.replace("\\", "\\\\").toCharArray()) {
            if (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '^' || c == '*' || c == '$' || c == '.' || c == '?' || c == '+' || c == '|' || c == '<' || c == '>' || c == '!' || c == '-') {
                sb.append('\\');
            }
            sb.append(c);
        }
        Pattern pattern = Pattern.compile(sb.toString() + ".*", 32);
        Iterator<History.Entry> iter = history.reverseIterator(history.last());
        String suggestion = DEFAULT_BELL_STYLE;
        int tot = 0;
        while (iter.hasNext()) {
            History.Entry entry = iter.next();
            Matcher matcher = pattern.matcher(entry.line());
            if (matcher.matches()) {
                suggestion = entry.line().substring(buffer.length());
                break;
            }
            if (tot > 200) break;
            ++tot;
        }
        return suggestion;
    }

    public AttributedString getDisplayedBufferWithPrompts(List<AttributedString> secondaryPrompts) {
        AttributedString attBuf = this.getHighlightedBuffer(this.buf.toString());
        AttributedString tNewBuf = this.insertSecondaryPrompts(attBuf, secondaryPrompts);
        AttributedStringBuilder full = new AttributedStringBuilder().tabs(this.getTabWidth());
        full.append(this.prompt);
        full.append(tNewBuf);
        if (this.doAutosuggestion && !this.isTerminalDumb()) {
            String lastBinding;
            String string = lastBinding = this.getLastBinding() != null ? this.getLastBinding() : DEFAULT_BELL_STYLE;
            if (this.autosuggestion == LineReader.SuggestionType.HISTORY) {
                AttributedStringBuilder sb = new AttributedStringBuilder();
                this.tailTip = this.matchPreviousCommand(this.buf.toString());
                sb.styled(AttributedStyle::faint, (CharSequence)this.tailTip);
                full.append(sb.toAttributedString());
            } else if (this.autosuggestion == LineReader.SuggestionType.COMPLETER) {
                if (!(this.buf.length() < this.getInt("suggestions-min-buffer-size", 1) || this.buf.length() != this.buf.cursor() || lastBinding.equals("\t") && this.buf.prevChar() != 32 && this.buf.prevChar() != 61)) {
                    this.clearChoices();
                    this.listChoices(true);
                } else if (!lastBinding.equals("\t")) {
                    this.clearChoices();
                }
            } else if (this.autosuggestion == LineReader.SuggestionType.TAIL_TIP && this.buf.length() == this.buf.cursor()) {
                if (!lastBinding.equals("\t") || this.buf.prevChar() == 32) {
                    this.clearChoices();
                }
                AttributedStringBuilder sb = new AttributedStringBuilder();
                if (this.buf.prevChar() != 32) {
                    if (!this.tailTip.startsWith("[")) {
                        int idx = this.tailTip.indexOf(32);
                        int idb = this.buf.toString().lastIndexOf(32);
                        int idd = this.buf.toString().lastIndexOf(45);
                        if (idx > 0 && (idb == -1 && idb == idd || idb >= 0 && idb > idd)) {
                            this.tailTip = this.tailTip.substring(idx);
                        } else if (idb >= 0 && idb < idd) {
                            sb.append(" ");
                        }
                    } else {
                        sb.append(" ");
                    }
                }
                sb.styled(AttributedStyle::faint, (CharSequence)this.tailTip);
                full.append(sb.toAttributedString());
            }
        }
        if (this.post != null) {
            full.append("\n");
            full.append(this.post.get());
        }
        this.doAutosuggestion = true;
        return full.toAttributedString();
    }

    private AttributedString getHighlightedBuffer(String buffer) {
        if (this.maskingCallback != null) {
            buffer = this.maskingCallback.display(buffer);
        }
        if (this.highlighter != null && !this.isSet(LineReader.Option.DISABLE_HIGHLIGHTER) && buffer.length() < this.getInt("features-max-buffer-size", 1000)) {
            return this.highlighter.highlight(this, buffer);
        }
        return new AttributedString(buffer);
    }

    private AttributedString expandPromptPattern(String pattern, int padToWidth, String message, int line) {
        ArrayList<AttributedString> parts = new ArrayList<AttributedString>();
        boolean isHidden = false;
        int padPartIndex = -1;
        StringBuilder padPartString = null;
        StringBuilder sb = new StringBuilder();
        pattern = pattern + "%{";
        int plen = pattern.length();
        int padChar = -1;
        int padPos = -1;
        int cols = 0;
        int i = 0;
        block9: while (i < plen) {
            char ch;
            if ((ch = pattern.charAt(i++)) == '%' && i < plen) {
                int count = 0;
                boolean countSeen = false;
                block10: while (true) {
                    ch = pattern.charAt(i++);
                    switch (ch) {
                        case '{': 
                        case '}': {
                            AttributedString astr;
                            String str = sb.toString();
                            if (!isHidden) {
                                astr = this.fromAnsi(str);
                                cols += astr.columnLength();
                            } else {
                                astr = new AttributedString(str, AttributedStyle.HIDDEN);
                            }
                            if (padPartIndex == parts.size()) {
                                padPartString = sb;
                                if (i < plen) {
                                    sb = new StringBuilder();
                                }
                            } else {
                                sb.setLength(0);
                            }
                            parts.add(astr);
                            isHidden = ch == '{';
                            break block10;
                        }
                        case '%': {
                            sb.append(ch);
                            break block10;
                        }
                        case 'N': {
                            sb.append(this.getInt("line-offset", 0) + line);
                            break block10;
                        }
                        case '*': {
                            if (this.currentLine == line) {
                                sb.append("*");
                                break block10;
                            }
                            sb.append(" ");
                            break block10;
                        }
                        case 'M': {
                            if (message == null) continue block9;
                            sb.append(message);
                            break block10;
                        }
                        case 'P': {
                            if (countSeen && count >= 0) {
                                padToWidth = count;
                            }
                            if (i < plen) {
                                padChar = pattern.charAt(i++);
                            }
                            padPos = sb.length();
                            padPartIndex = parts.size();
                            break block10;
                        }
                        case '-': 
                        case '0': 
                        case '1': 
                        case '2': 
                        case '3': 
                        case '4': 
                        case '5': 
                        case '6': 
                        case '7': 
                        case '8': 
                        case '9': {
                            boolean neg = false;
                            if (ch == '-') {
                                neg = true;
                                ch = pattern.charAt(i++);
                            }
                            countSeen = true;
                            count = 0;
                            while (ch >= '0' && ch <= '9') {
                                count = (count < 0 ? 0 : 10 * count) + (ch - 48);
                                ch = pattern.charAt(i++);
                            }
                            if (neg) {
                                count = -count;
                            }
                            --i;
                            continue block10;
                        }
                    }
                    break;
                }
                continue;
            }
            sb.append(ch);
        }
        if (padToWidth > cols) {
            int padCharCols = WCWidth.wcwidth(padChar);
            int padCount = (padToWidth - cols) / padCharCols;
            sb = padPartString;
            while (--padCount >= 0) {
                sb.insert(padPos, (char)padChar);
            }
            parts.set(padPartIndex, this.fromAnsi(sb.toString()));
        }
        return AttributedString.join(null, parts);
    }

    private AttributedString fromAnsi(String str) {
        return AttributedString.fromAnsi(str, Collections.singletonList(0), this.alternateIn, this.alternateOut);
    }

    private AttributedString insertSecondaryPrompts(AttributedString str, List<AttributedString> prompts) {
        return this.insertSecondaryPrompts(str, prompts, true);
    }

    private AttributedString insertSecondaryPrompts(AttributedString strAtt, List<AttributedString> prompts, boolean computePrompts) {
        int line;
        Objects.requireNonNull(prompts);
        List<AttributedString> lines = strAtt.columnSplitLength(Integer.MAX_VALUE);
        AttributedStringBuilder sb = new AttributedStringBuilder();
        String secondaryPromptPattern = this.getString("secondary-prompt-pattern", DEFAULT_SECONDARY_PROMPT_PATTERN);
        boolean needsMessage = secondaryPromptPattern.contains("%M") && strAtt.length() < this.getInt("features-max-buffer-size", 1000);
        AttributedStringBuilder buf = new AttributedStringBuilder();
        int width = 0;
        ArrayList<String> missings = new ArrayList<String>();
        if (computePrompts && secondaryPromptPattern.contains("%P")) {
            width = this.prompt.columnLength();
            if (width > this.size.getColumns() || this.prompt.contains('\n')) {
                width = new TerminalLine(this.prompt.toString(), 0, this.size.getColumns()).getEndLine().length();
            }
            for (line = 0; line < lines.size() - 1; ++line) {
                buf.append(lines.get(line)).append("\n");
                String missing = DEFAULT_BELL_STYLE;
                if (needsMessage) {
                    try {
                        this.parser.parse(buf.toString(), buf.length(), Parser.ParseContext.SECONDARY_PROMPT);
                    }
                    catch (EOFError e) {
                        missing = e.getMissing();
                    }
                    catch (SyntaxError e) {
                        // empty catch block
                    }
                }
                missings.add(missing);
                AttributedString prompt = this.expandPromptPattern(secondaryPromptPattern, 0, missing, line + 1);
                width = Math.max(width, prompt.columnLength());
            }
            buf.setLength(0);
        }
        line = 0;
        this.currentLine = -1;
        int cursor = this.buf.cursor();
        int start = 0;
        for (int l = 0; l < lines.size(); ++l) {
            int end = start + lines.get(l).length();
            if (cursor >= start && cursor <= end) {
                this.currentLine = l;
                break;
            }
            start = end + 1;
        }
        while (line < lines.size() - 1) {
            AttributedString prompt;
            sb.append(lines.get(line)).append("\n");
            buf.append(lines.get(line)).append("\n");
            if (computePrompts) {
                String missing = DEFAULT_BELL_STYLE;
                if (needsMessage) {
                    if (missings.isEmpty()) {
                        try {
                            this.parser.parse(buf.toString(), buf.length(), Parser.ParseContext.SECONDARY_PROMPT);
                        }
                        catch (EOFError e) {
                            missing = e.getMissing();
                        }
                        catch (SyntaxError syntaxError) {}
                    } else {
                        missing = (String)missings.get(line);
                    }
                }
                prompt = this.expandPromptPattern(secondaryPromptPattern, width, missing, line + 1);
            } else {
                prompt = prompts.get(line);
            }
            prompts.add(prompt);
            sb.append(prompt);
            ++line;
        }
        sb.append(lines.get(line));
        buf.append(lines.get(line));
        return sb.toAttributedString();
    }

    private AttributedString addRightPrompt(AttributedString prompt, AttributedString line) {
        int width = prompt.columnLength();
        boolean endsWithNl = line.length() > 0 && line.charAt(line.length() - 1) == '\n';
        int nb = this.size.getColumns() - width - (line.columnLength() + (endsWithNl ? 1 : 0));
        if (nb >= 3) {
            AttributedStringBuilder sb = new AttributedStringBuilder(this.size.getColumns());
            sb.append(line, 0, endsWithNl ? line.length() - 1 : line.length());
            for (int j = 0; j < nb; ++j) {
                sb.append(' ');
            }
            sb.append(prompt);
            if (endsWithNl) {
                sb.append('\n');
            }
            line = sb.toAttributedString();
        }
        return line;
    }

    protected boolean insertTab() {
        return this.isSet(LineReader.Option.INSERT_TAB) && this.getLastBinding().equals("\t") && this.buf.toString().matches("(^|[\\s\\S]*\n)[\r\n\t ]*");
    }

    protected boolean expandHistory() {
        String str = this.buf.toString();
        String exp = this.expander.expandHistory(this.history, str);
        if (!exp.equals(str)) {
            this.buf.clear();
            this.buf.write(exp);
            return true;
        }
        return false;
    }

    protected boolean expandWord() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.Expand, this.isSet(LineReader.Option.MENU_COMPLETE), false);
    }

    protected boolean expandOrComplete() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.ExpandComplete, this.isSet(LineReader.Option.MENU_COMPLETE), false);
    }

    protected boolean expandOrCompletePrefix() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.ExpandComplete, this.isSet(LineReader.Option.MENU_COMPLETE), true);
    }

    protected boolean completeWord() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.Complete, this.isSet(LineReader.Option.MENU_COMPLETE), false);
    }

    protected boolean menuComplete() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.Complete, true, false);
    }

    protected boolean menuExpandOrComplete() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.ExpandComplete, true, false);
    }

    protected boolean completePrefix() {
        if (this.insertTab()) {
            return this.selfInsert();
        }
        return this.doComplete(CompletionType.Complete, this.isSet(LineReader.Option.MENU_COMPLETE), true);
    }

    protected boolean listChoices() {
        return this.listChoices(false);
    }

    private boolean listChoices(boolean forSuggestion) {
        return this.doComplete(CompletionType.List, this.isSet(LineReader.Option.MENU_COMPLETE), false, forSuggestion);
    }

    protected boolean deleteCharOrList() {
        if (this.buf.cursor() != this.buf.length() || this.buf.length() == 0) {
            return this.deleteChar();
        }
        return this.doComplete(CompletionType.List, this.isSet(LineReader.Option.MENU_COMPLETE), false);
    }

    protected boolean doComplete(CompletionType lst, boolean useMenu, boolean prefix) {
        return this.doComplete(lst, useMenu, prefix, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean doComplete(CompletionType lst, boolean useMenu, boolean prefix, boolean forSuggestion) {
        CompletingParsedLine line;
        if (this.getBoolean("disable-completion", false)) {
            return true;
        }
        if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
            try {
                if (this.expandHistory()) {
                    return true;
                }
            }
            catch (Exception e) {
                Log.info("Error while expanding history", e);
                return false;
            }
        }
        try {
            line = LineReaderImpl.wrap(this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.COMPLETE));
        }
        catch (Exception e) {
            Log.info("Error while parsing line", e);
            return false;
        }
        ArrayList<Candidate> candidates = new ArrayList<Candidate>();
        try {
            if (this.completer != null) {
                this.completer.complete(this, line, candidates);
            }
        }
        catch (Exception e) {
            Log.info("Error while finding completion candidates", e);
            if (Log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return false;
        }
        if (lst == CompletionType.ExpandComplete || lst == CompletionType.Expand) {
            String w = this.expander.expandVar(line.word());
            if (!line.word().equals(w)) {
                if (prefix) {
                    this.buf.backspace(line.wordCursor());
                } else {
                    this.buf.move(line.word().length() - line.wordCursor());
                    this.buf.backspace(line.word().length());
                }
                this.buf.write(w);
                return true;
            }
            if (lst == CompletionType.Expand) {
                return false;
            }
            lst = CompletionType.Complete;
        }
        boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE);
        int errors = this.getInt("errors", 2);
        this.completionMatcher.compile(this.options, prefix, line, caseInsensitive, errors, this.getOriginalGroupName());
        List<Candidate> possible = this.completionMatcher.matches(candidates);
        if (possible.isEmpty()) {
            return false;
        }
        this.size.copy(this.terminal.getSize());
        try {
            boolean hasUnambiguous;
            String current;
            if (lst == CompletionType.List) {
                this.doList(possible, line.word(), false, line::escape, forSuggestion);
                boolean bl = !possible.isEmpty();
                return bl;
            }
            Candidate completion = null;
            if (possible.size() == 1) {
                completion = possible.get(0);
            } else if (this.isSet(LineReader.Option.RECOGNIZE_EXACT)) {
                completion = this.completionMatcher.exactMatch();
            }
            if (completion != null && !completion.value().isEmpty()) {
                if (prefix) {
                    this.buf.backspace(line.rawWordCursor());
                } else {
                    this.buf.move(line.rawWordLength() - line.rawWordCursor());
                    this.buf.backspace(line.rawWordLength());
                }
                this.buf.write(line.escape(completion.value(), completion.complete()));
                if (completion.complete()) {
                    if (this.buf.currChar() != 32) {
                        this.buf.write(" ");
                    } else {
                        this.buf.move(1);
                    }
                }
                if (completion.suffix() != null) {
                    if (this.autosuggestion == LineReader.SuggestionType.COMPLETER) {
                        this.listChoices(true);
                    }
                    this.redisplay();
                    Binding op = this.readBinding(this.getKeys());
                    if (op != null) {
                        String ref;
                        String chars = this.getString("REMOVE_SUFFIX_CHARS", DEFAULT_REMOVE_SUFFIX_CHARS);
                        String string = ref = op instanceof Reference ? ((Reference)op).name() : null;
                        if ("self-insert".equals(ref) && chars.indexOf(this.getLastBinding().charAt(0)) >= 0 || "accept-line".equals(ref)) {
                            this.buf.backspace(completion.suffix().length());
                            if (this.getLastBinding().charAt(0) != ' ') {
                                this.buf.write(32);
                            }
                        }
                        this.pushBackBinding(true);
                    }
                }
                boolean op = true;
                return op;
            }
            if (useMenu) {
                this.buf.move(line.word().length() - line.wordCursor());
                this.buf.backspace(line.word().length());
                this.doMenu(possible, line.word(), line::escape);
                boolean op = true;
                return op;
            }
            if (prefix) {
                current = line.word().substring(0, line.wordCursor());
            } else {
                current = line.word();
                this.buf.move(line.rawWordLength() - line.rawWordCursor());
            }
            String commonPrefix = this.completionMatcher.getCommonPrefix();
            boolean bl = hasUnambiguous = commonPrefix.startsWith(current) && !commonPrefix.equals(current);
            if (hasUnambiguous) {
                this.buf.backspace(line.rawWordLength());
                this.buf.write(line.escape(commonPrefix, false));
                this.callWidget("redisplay");
                current = commonPrefix;
                if ((!this.isSet(LineReader.Option.AUTO_LIST) && this.isSet(LineReader.Option.AUTO_MENU) || this.isSet(LineReader.Option.AUTO_LIST) && this.isSet(LineReader.Option.LIST_AMBIGUOUS)) && !this.nextBindingIsComplete()) {
                    boolean bl2 = true;
                    return bl2;
                }
            }
            if (this.isSet(LineReader.Option.AUTO_LIST)) {
                if (!this.doList(possible, current, true, line::escape)) {
                    boolean bl3 = true;
                    return bl3;
                }
            }
            if (this.isSet(LineReader.Option.AUTO_MENU)) {
                this.buf.backspace(current.length());
                this.doMenu(possible, line.word(), line::escape);
            }
            boolean bl4 = true;
            return bl4;
        }
        finally {
            this.size.copy(this.terminal.getBufferSize());
        }
    }

    protected static CompletingParsedLine wrap(final ParsedLine line) {
        if (line instanceof CompletingParsedLine) {
            return (CompletingParsedLine)line;
        }
        return new CompletingParsedLine(){

            @Override
            public String word() {
                return line.word();
            }

            @Override
            public int wordCursor() {
                return line.wordCursor();
            }

            @Override
            public int wordIndex() {
                return line.wordIndex();
            }

            @Override
            public List<String> words() {
                return line.words();
            }

            @Override
            public String line() {
                return line.line();
            }

            @Override
            public int cursor() {
                return line.cursor();
            }

            @Override
            public CharSequence escape(CharSequence candidate, boolean complete) {
                return candidate;
            }

            @Override
            public int rawWordCursor() {
                return this.wordCursor();
            }

            @Override
            public int rawWordLength() {
                return this.word().length();
            }
        };
    }

    protected Comparator<Candidate> getCandidateComparator(boolean caseInsensitive, String word) {
        String wdi = caseInsensitive ? word.toLowerCase() : word;
        ToIntFunction<String> wordDistance = w -> ReaderUtils.distance(wdi, caseInsensitive ? w.toLowerCase() : w);
        return Comparator.comparing(Candidate::value, Comparator.comparingInt(wordDistance)).thenComparing(Comparator.naturalOrder());
    }

    protected String getOthersGroupName() {
        return this.getString("OTHERS_GROUP_NAME", DEFAULT_OTHERS_GROUP_NAME);
    }

    protected String getOriginalGroupName() {
        return this.getString("ORIGINAL_GROUP_NAME", DEFAULT_ORIGINAL_GROUP_NAME);
    }

    protected Comparator<String> getGroupComparator() {
        return Comparator.comparingInt(s -> this.getOthersGroupName().equals(s) ? 1 : (this.getOriginalGroupName().equals(s) ? -1 : 0)).thenComparing(String::toLowerCase, Comparator.naturalOrder());
    }

    private void mergeCandidates(List<Candidate> possible) {
        HashMap<String, List> keyedCandidates = new HashMap<String, List>();
        for (Candidate candidate : possible) {
            if (candidate.key() == null) continue;
            List cands = keyedCandidates.computeIfAbsent(candidate.key(), s -> new ArrayList());
            cands.add(candidate);
        }
        if (!keyedCandidates.isEmpty()) {
            for (List candidates : keyedCandidates.values()) {
                if (candidates.size() < 1) continue;
                possible.removeAll(candidates);
                candidates.sort(Comparator.comparing(Candidate::value));
                Candidate first = (Candidate)candidates.get(0);
                String disp = candidates.stream().map(Candidate::displ).collect(Collectors.joining(" "));
                possible.add(new Candidate(first.value(), disp, first.group(), first.descr(), first.suffix(), null, first.complete()));
            }
        }
    }

    protected boolean nextBindingIsComplete() {
        this.redisplay();
        KeyMap<Binding> keyMap = this.keyMaps.get("menu");
        Binding operation = this.readBinding(this.getKeys(), keyMap);
        if (operation instanceof Reference && "menu-complete".equals(((Reference)operation).name())) {
            return true;
        }
        this.pushBackBinding();
        return false;
    }

    private int displayRows() {
        return this.displayRows(Status.getStatus(this.terminal, false));
    }

    private int displayRows(Status status) {
        return this.size.getRows() - (status != null ? status.size() : 0);
    }

    private int visibleDisplayRows() {
        Status status = Status.getStatus(this.terminal, false);
        return this.terminal.getSize().getRows() - (status != null ? status.size() : 0);
    }

    private int promptLines() {
        AttributedString text = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<AttributedString>());
        return text.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
    }

    protected boolean doMenu(List<Candidate> original, String completed, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
        Binding operation;
        MenuSupport menuSupport;
        ArrayList<Candidate> possible = new ArrayList<Candidate>();
        boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE);
        original.sort(this.getCandidateComparator(caseInsensitive, completed));
        this.mergeCandidates(original);
        this.computePost(original, null, possible, completed);
        boolean defaultAutoGroup = this.isSet(LineReader.Option.AUTO_GROUP);
        boolean defaultGroup = this.isSet(LineReader.Option.GROUP);
        if (!this.isSet(LineReader.Option.GROUP_PERSIST)) {
            this.option(LineReader.Option.AUTO_GROUP, false);
            this.option(LineReader.Option.GROUP, false);
        }
        this.post = menuSupport = new MenuSupport(original, completed, escaper);
        this.callWidget("redisplay");
        KeyMap<Binding> keyMap = this.keyMaps.get("menu");
        while ((operation = this.readBinding(this.getKeys(), keyMap)) != null) {
            String ref;
            switch (ref = operation instanceof Reference ? ((Reference)operation).name() : DEFAULT_BELL_STYLE) {
                case "menu-complete": {
                    menuSupport.next();
                    break;
                }
                case "reverse-menu-complete": {
                    menuSupport.previous();
                    break;
                }
                case "up-line-or-history": 
                case "up-line-or-search": {
                    menuSupport.up();
                    break;
                }
                case "down-line-or-history": 
                case "down-line-or-search": {
                    menuSupport.down();
                    break;
                }
                case "forward-char": {
                    menuSupport.right();
                    break;
                }
                case "backward-char": {
                    menuSupport.left();
                    break;
                }
                case "clear-screen": {
                    this.clearScreen();
                    break;
                }
                default: {
                    Candidate completion = menuSupport.completion();
                    if (completion.suffix() != null) {
                        String chars = this.getString("REMOVE_SUFFIX_CHARS", DEFAULT_REMOVE_SUFFIX_CHARS);
                        if ("self-insert".equals(ref) && chars.indexOf(this.getLastBinding().charAt(0)) >= 0 || "backward-delete-char".equals(ref)) {
                            this.buf.backspace(completion.suffix().length());
                        }
                    }
                    if (completion.complete() && this.getLastBinding().charAt(0) != ' ' && ("self-insert".equals(ref) || this.getLastBinding().charAt(0) != ' ')) {
                        this.buf.write(32);
                    }
                    if (!("accept-line".equals(ref) || "self-insert".equals(ref) && completion.suffix() != null && completion.suffix().startsWith(this.getLastBinding()))) {
                        this.pushBackBinding(true);
                    }
                    this.post = null;
                    this.option(LineReader.Option.AUTO_GROUP, defaultAutoGroup);
                    this.option(LineReader.Option.GROUP, defaultGroup);
                    return true;
                }
            }
            this.doAutosuggestion = false;
            this.callWidget("redisplay");
        }
        this.option(LineReader.Option.AUTO_GROUP, defaultAutoGroup);
        this.option(LineReader.Option.GROUP, defaultGroup);
        return false;
    }

    protected boolean clearChoices() {
        return this.doList(new ArrayList<Candidate>(), DEFAULT_BELL_STYLE, false, null, false);
    }

    protected boolean doList(List<Candidate> possible, String completed, boolean runLoop, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
        return this.doList(possible, completed, runLoop, escaper, false);
    }

    protected boolean doList(List<Candidate> possible, String completed, boolean runLoop, BiFunction<CharSequence, Boolean, CharSequence> escaper, boolean forSuggestion) {
        this.mergeCandidates(possible);
        AttributedString text = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<AttributedString>());
        int promptLines = text.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
        PostResult postResult = this.computePost(possible, null, null, completed);
        int lines = postResult.lines;
        int listMax = this.getInt("list-max", 100);
        int possibleSize = possible.size();
        if (possibleSize == 0 || this.size.getRows() == 0) {
            return false;
        }
        if (listMax > 0 && possibleSize >= listMax || lines >= this.size.getRows() - promptLines) {
            if (!forSuggestion) {
                this.post = () -> new AttributedString(this.getAppName() + ": do you wish to see all " + possibleSize + " possibilities (" + lines + " lines)?");
                this.redisplay(true);
                int c = this.readCharacter();
                if (c != 121 && c != 89 && c != 9) {
                    this.post = null;
                    return false;
                }
            } else {
                return false;
            }
        }
        boolean caseInsensitive = this.isSet(LineReader.Option.CASE_INSENSITIVE);
        StringBuilder sb = new StringBuilder();
        this.candidateStartPosition = 0;
        while (true) {
            List<Candidate> cands;
            String current = completed + sb.toString();
            if (sb.length() > 0) {
                this.completionMatcher.compile(this.options, false, new CompletingWord(current), caseInsensitive, 0, null);
                cands = this.completionMatcher.matches(possible).stream().sorted(this.getCandidateComparator(caseInsensitive, current)).collect(Collectors.toList());
            } else {
                cands = possible.stream().sorted(this.getCandidateComparator(caseInsensitive, current)).collect(Collectors.toList());
            }
            if (this.isSet(LineReader.Option.AUTO_MENU_LIST) && this.candidateStartPosition == 0) {
                this.candidateStartPosition = this.candidateStartPosition(cands);
            }
            this.post = () -> {
                AttributedString t = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<AttributedString>());
                int pl = t.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
                PostResult pr = this.computePost(cands, null, null, current);
                if (pr.lines >= this.size.getRows() - pl) {
                    this.post = null;
                    int oldCursor = this.buf.cursor();
                    this.buf.cursor(this.buf.length());
                    this.redisplay(false);
                    this.buf.cursor(oldCursor);
                    this.println();
                    List<AttributedString> ls = pr.post.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
                    Display d = new Display(this.terminal, false);
                    d.resize(this.size.getRows(), this.size.getColumns());
                    d.update(ls, -1);
                    this.println();
                    this.redrawLine();
                    return new AttributedString(DEFAULT_BELL_STYLE);
                }
                return pr.post;
            };
            if (!runLoop) {
                return false;
            }
            this.redisplay();
            Binding b = this.doReadBinding(this.getKeys(), null);
            if (b instanceof Reference) {
                String name = ((Reference)b).name();
                if ("backward-delete-char".equals(name) || "vi-backward-delete-char".equals(name)) {
                    if (sb.length() == 0) {
                        this.pushBackBinding();
                        this.post = null;
                        return false;
                    }
                    sb.setLength(sb.length() - 1);
                    this.buf.backspace();
                    continue;
                }
                if ("self-insert".equals(name)) {
                    sb.append(this.getLastBinding());
                    this.callWidget(name);
                    if (!cands.isEmpty()) continue;
                    this.post = null;
                    return false;
                }
                if ("\t".equals(this.getLastBinding())) {
                    if (cands.size() == 1 || sb.length() > 0) {
                        this.post = null;
                        this.pushBackBinding();
                    } else if (this.isSet(LineReader.Option.AUTO_MENU)) {
                        this.buf.backspace(escaper.apply(current, false).length());
                        this.doMenu(cands, current, escaper);
                    }
                    return false;
                }
                this.pushBackBinding();
                this.post = null;
                return false;
            }
            if (b == null) break;
        }
        this.post = null;
        return false;
    }

    protected PostResult computePost(List<Candidate> possible, Candidate selection, List<Candidate> ordered, String completed) {
        return this.computePost(possible, selection, ordered, completed, this.display::wcwidth, this.size.getColumns(), this.isSet(LineReader.Option.AUTO_GROUP), this.isSet(LineReader.Option.GROUP), this.isSet(LineReader.Option.LIST_ROWS_FIRST));
    }

    protected PostResult computePost(List<Candidate> possible, Candidate selection, List<Candidate> ordered, String completed, Function<String, Integer> wcwidth, int width, boolean autoGroup, boolean groupName, boolean rowsFirst) {
        ArrayList<Object> strings = new ArrayList<Object>();
        if (groupName) {
            String group;
            Comparator<String> groupComparator = this.getGroupComparator();
            AbstractMap sorted = groupComparator != null ? new TreeMap(groupComparator) : new LinkedHashMap();
            for (Candidate candidate : possible) {
                group = candidate.group();
                sorted.computeIfAbsent(group != null ? group : DEFAULT_BELL_STYLE, s -> new ArrayList()).add(candidate);
            }
            for (Map.Entry entry : sorted.entrySet()) {
                group = (String)entry.getKey();
                if (group.isEmpty() && sorted.size() > 1) {
                    group = this.getOthersGroupName();
                }
                if (!group.isEmpty() && autoGroup) {
                    strings.add(group);
                }
                List candidates = (List)entry.getValue();
                Collections.sort(candidates);
                strings.add(candidates);
                if (ordered == null) continue;
                ordered.addAll(candidates);
            }
        } else {
            LinkedHashSet<String> groups = new LinkedHashSet<String>();
            ArrayList<Candidate> sorted = new ArrayList<Candidate>();
            for (Candidate candidate : possible) {
                String group = candidate.group();
                if (group != null) {
                    groups.add(group);
                }
                sorted.add(candidate);
            }
            if (autoGroup) {
                strings.addAll(groups);
            }
            Collections.sort(sorted);
            strings.add(sorted);
            if (ordered != null) {
                ordered.addAll(sorted);
            }
        }
        return this.toColumns(strings, selection, completed, wcwidth, width, rowsFirst);
    }

    private int candidateStartPosition(List<Candidate> cands) {
        int promptLength;
        List<String> values = cands.stream().map(c -> AttributedString.stripAnsi(c.displ())).filter(c -> !c.matches("\\w+") && c.length() > 1).collect(Collectors.toList());
        HashSet notDelimiters = new HashSet();
        values.forEach(v -> v.substring(0, v.length() - 1).chars().filter(c -> !Character.isDigit(c) && !Character.isAlphabetic(c)).forEach(c -> notDelimiters.add(Character.toString((char)c))));
        int width = this.size.getColumns();
        int n = promptLength = this.prompt != null ? this.prompt.length() : 0;
        if (promptLength > 0) {
            TerminalLine tp = new TerminalLine(this.prompt.toString(), 0, width);
            promptLength = tp.getEndLine().length();
        }
        TerminalLine tl = new TerminalLine(this.buf.substring(0, this.buf.cursor()), promptLength, width);
        int out = tl.getStartPos();
        String buffer = tl.getEndLine();
        for (int i = buffer.length(); i > 0; --i) {
            if (!buffer.substring(0, i).matches(".*\\W") || notDelimiters.contains(buffer.substring(i - 1, i))) continue;
            out += i;
            break;
        }
        return out;
    }

    protected PostResult toColumns(List<Object> items, Candidate selection, String completed, Function<String, Integer> wcwidth, int width, boolean rowsFirst) {
        int[] out = new int[2];
        int maxWidth = 0;
        int listSize = 0;
        for (Object item : items) {
            if (item instanceof String) {
                int len = wcwidth.apply((String)item);
                maxWidth = Math.max(maxWidth, len);
                continue;
            }
            if (!(item instanceof List)) continue;
            for (Candidate cand : (List)item) {
                ++listSize;
                int len = wcwidth.apply(cand.displ());
                if (cand.descr() != null) {
                    ++len;
                    len += DESC_PREFIX.length();
                    len += wcwidth.apply(cand.descr()).intValue();
                    len += DESC_SUFFIX.length();
                }
                maxWidth = Math.max(maxWidth, len);
            }
        }
        AttributedStringBuilder sb = new AttributedStringBuilder();
        if (listSize > 0) {
            if (this.isSet(LineReader.Option.AUTO_MENU_LIST) && listSize < Math.min(this.getInt("menu-list-max", Integer.MAX_VALUE), this.visibleDisplayRows() - this.promptLines())) {
                maxWidth = Math.max(maxWidth, 25);
                sb.tabs(Math.max(Math.min(this.candidateStartPosition, width - maxWidth - 1), 1));
                width = maxWidth + 2;
                if (!this.isSet(LineReader.Option.GROUP_PERSIST)) {
                    List<Object> list = new ArrayList();
                    for (Object o : items) {
                        if (!(o instanceof Collection)) continue;
                        list.addAll((Collection)o);
                    }
                    list = list.stream().sorted(this.getCandidateComparator(this.isSet(LineReader.Option.CASE_INSENSITIVE), DEFAULT_BELL_STYLE)).collect(Collectors.toList());
                    this.toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, true, out);
                } else {
                    for (Object list : items) {
                        this.toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, true, out);
                    }
                }
            } else {
                for (Object list : items) {
                    this.toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, false, out);
                }
            }
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        return new PostResult(sb.toAttributedString(), out[0], out[1]);
    }

    protected void toColumns(Object items, int width, int maxWidth, AttributedStringBuilder sb, Candidate selection, String completed, boolean rowsFirst, boolean doMenuList, int[] out) {
        if (maxWidth <= 0 || width <= 0) {
            return;
        }
        if (items instanceof String) {
            if (doMenuList) {
                sb.style(AttributedStyle.DEFAULT);
                sb.append('\t');
            }
            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.style(this.getCompletionStyleGroup(doMenuList)).append((String)items).style(AttributedStyle.DEFAULT);
            if (doMenuList) {
                for (int k = ((String)items).length(); k < maxWidth + 1; ++k) {
                    asb.append(' ');
                }
            }
            sb.style(this.getCompletionStyleBackground(doMenuList));
            sb.append(asb);
            sb.append("\n");
            out[0] = out[0] + 1;
        } else if (items instanceof List) {
            int c;
            List candidates = (List)items;
            maxWidth = Math.min(width, maxWidth);
            for (c = width / maxWidth; c > 1 && c * maxWidth + (c - 1) * 3 >= width; --c) {
            }
            int lines = (candidates.size() + c - 1) / c;
            int columns = (candidates.size() + lines - 1) / lines;
            IntBinaryOperator index = rowsFirst ? (i, j) -> i * columns + j : (i, j) -> j * lines + i;
            for (int i2 = 0; i2 < lines; ++i2) {
                if (doMenuList) {
                    sb.style(AttributedStyle.DEFAULT);
                    sb.append('\t');
                }
                AttributedStringBuilder asb = new AttributedStringBuilder();
                for (int j2 = 0; j2 < columns; ++j2) {
                    int k;
                    int idx = index.applyAsInt(i2, j2);
                    if (idx >= candidates.size()) continue;
                    Candidate cand = (Candidate)candidates.get(idx);
                    boolean hasRightItem = j2 < columns - 1 && index.applyAsInt(i2, j2 + 1) < candidates.size();
                    AttributedString left = this.fromAnsi(cand.displ());
                    AttributedString right = this.fromAnsi(cand.descr());
                    int lw = left.columnLength();
                    int rw = 0;
                    if (right != null) {
                        int rem = maxWidth - (lw + 1 + DESC_PREFIX.length() + DESC_SUFFIX.length());
                        rw = right.columnLength();
                        if (rw > rem) {
                            right = AttributedStringBuilder.append(right.columnSubSequence(0, rem - WCWidth.wcwidth(8230)), "\u2026");
                            rw = right.columnLength();
                        }
                        right = AttributedStringBuilder.append(DESC_PREFIX, right, DESC_SUFFIX);
                        rw += DESC_PREFIX.length() + DESC_SUFFIX.length();
                    }
                    if (cand == selection) {
                        out[1] = i2;
                        asb.style(this.getCompletionStyleSelection(doMenuList));
                        if (left.toString().regionMatches(this.isSet(LineReader.Option.CASE_INSENSITIVE), 0, completed, 0, completed.length())) {
                            asb.append(left.toString(), 0, completed.length());
                            asb.append(left.toString(), completed.length(), left.length());
                        } else {
                            asb.append(left.toString());
                        }
                        for (k = 0; k < maxWidth - lw - rw; ++k) {
                            asb.append(' ');
                        }
                        if (right != null) {
                            asb.append(right);
                        }
                        asb.style(AttributedStyle.DEFAULT);
                    } else {
                        if (left.toString().regionMatches(this.isSet(LineReader.Option.CASE_INSENSITIVE), 0, completed, 0, completed.length())) {
                            asb.style(this.getCompletionStyleStarting(doMenuList));
                            asb.append(left, 0, completed.length());
                            asb.style(AttributedStyle.DEFAULT);
                            asb.append(left, completed.length(), left.length());
                        } else {
                            asb.append(left);
                        }
                        if (right != null || hasRightItem) {
                            for (k = 0; k < maxWidth - lw - rw; ++k) {
                                asb.append(' ');
                            }
                        }
                        if (right != null) {
                            asb.style(this.getCompletionStyleDescription(doMenuList));
                            asb.append(right);
                            asb.style(AttributedStyle.DEFAULT);
                        } else if (doMenuList) {
                            for (k = lw; k < maxWidth; ++k) {
                                asb.append(' ');
                            }
                        }
                    }
                    if (hasRightItem) {
                        for (k = 0; k < 3; ++k) {
                            asb.append(' ');
                        }
                    }
                    if (!doMenuList) continue;
                    asb.append(' ');
                }
                sb.style(this.getCompletionStyleBackground(doMenuList));
                sb.append(asb);
                sb.append('\n');
            }
            out[0] = out[0] + lines;
        }
    }

    protected AttributedStyle getCompletionStyleStarting(boolean menuList) {
        return menuList ? this.getCompletionStyleListStarting() : this.getCompletionStyleStarting();
    }

    protected AttributedStyle getCompletionStyleDescription(boolean menuList) {
        return menuList ? this.getCompletionStyleListDescription() : this.getCompletionStyleDescription();
    }

    protected AttributedStyle getCompletionStyleGroup(boolean menuList) {
        return menuList ? this.getCompletionStyleListGroup() : this.getCompletionStyleGroup();
    }

    protected AttributedStyle getCompletionStyleSelection(boolean menuList) {
        return menuList ? this.getCompletionStyleListSelection() : this.getCompletionStyleSelection();
    }

    protected AttributedStyle getCompletionStyleBackground(boolean menuList) {
        return menuList ? this.getCompletionStyleListBackground() : this.getCompletionStyleBackground();
    }

    protected AttributedStyle getCompletionStyleStarting() {
        return this.getCompletionStyle("COMPLETION_STYLE_STARTING", "fg:cyan");
    }

    protected AttributedStyle getCompletionStyleDescription() {
        return this.getCompletionStyle("COMPLETION_STYLE_DESCRIPTION", "fg:bright-black");
    }

    protected AttributedStyle getCompletionStyleGroup() {
        return this.getCompletionStyle("COMPLETION_STYLE_GROUP", DEFAULT_COMPLETION_STYLE_GROUP);
    }

    protected AttributedStyle getCompletionStyleSelection() {
        return this.getCompletionStyle("COMPLETION_STYLE_SELECTION", "inverse");
    }

    protected AttributedStyle getCompletionStyleBackground() {
        return this.getCompletionStyle("COMPLETION_STYLE_BACKGROUND", DEFAULT_COMPLETION_STYLE_BACKGROUND);
    }

    protected AttributedStyle getCompletionStyleListStarting() {
        return this.getCompletionStyle("COMPLETION_STYLE_LIST_STARTING", "fg:cyan");
    }

    protected AttributedStyle getCompletionStyleListDescription() {
        return this.getCompletionStyle("COMPLETION_STYLE_LIST_DESCRIPTION", "fg:bright-black");
    }

    protected AttributedStyle getCompletionStyleListGroup() {
        return this.getCompletionStyle("COMPLETION_STYLE_LIST_GROUP", DEFAULT_COMPLETION_STYLE_LIST_GROUP);
    }

    protected AttributedStyle getCompletionStyleListSelection() {
        return this.getCompletionStyle("COMPLETION_STYLE_LIST_SELECTION", "inverse");
    }

    protected AttributedStyle getCompletionStyleListBackground() {
        return this.getCompletionStyle("COMPLETION_STYLE_LIST_BACKGROUND", DEFAULT_COMPLETION_STYLE_LIST_BACKGROUND);
    }

    protected AttributedStyle getCompletionStyle(String name, String value) {
        return new StyleResolver(s -> this.getString((String)s, null)).resolve("." + name, value);
    }

    protected AttributedStyle buildStyle(String str) {
        return this.fromAnsi("\u001b[" + str + "m ").styleAt(0);
    }

    protected boolean moveHistory(boolean next, int count) {
        boolean ok = true;
        for (int i = 0; i < count && (ok = this.moveHistory(next)); ++i) {
        }
        return ok;
    }

    protected boolean moveHistory(boolean next) {
        if (!this.buf.toString().equals(this.history.current())) {
            this.modifiedHistory.put(this.history.index(), this.buf.toString());
        }
        if (next && !this.history.next()) {
            return false;
        }
        if (!next && !this.history.previous()) {
            return false;
        }
        this.setBuffer(this.modifiedHistory.containsKey(this.history.index()) ? this.modifiedHistory.get(this.history.index()) : this.history.current());
        return true;
    }

    void print(String str) {
        this.terminal.writer().write(str);
    }

    void println(String s) {
        this.print(s);
        this.println();
    }

    void println() {
        this.terminal.puts(InfoCmp.Capability.carriage_return, new Object[0]);
        this.print("\n");
        this.redrawLine();
    }

    protected boolean killBuffer() {
        this.killRing.add(this.buf.toString());
        this.buf.clear();
        return true;
    }

    protected boolean killWholeLine() {
        int start;
        int end;
        if (this.buf.length() == 0) {
            return false;
        }
        if (this.count < 0) {
            end = this.buf.cursor();
            while (this.buf.atChar(end) != 0 && this.buf.atChar(end) != 10) {
                ++end;
            }
            start = end;
            for (int count = -this.count; count > 0; --count) {
                while (start > 0 && this.buf.atChar(start - 1) != 10) {
                    --start;
                }
                --start;
            }
        } else {
            for (start = this.buf.cursor(); start > 0 && this.buf.atChar(start - 1) != 10; --start) {
            }
            end = start;
            while (this.count-- > 0) {
                while (end < this.buf.length() && this.buf.atChar(end) != 10) {
                    ++end;
                }
                if (end >= this.buf.length()) continue;
                ++end;
            }
        }
        String killed = this.buf.substring(start, end);
        this.buf.cursor(start);
        this.buf.delete(end - start);
        this.killRing.add(killed);
        return true;
    }

    public boolean killLine() {
        int cp;
        if (this.count < 0) {
            return this.callNeg(this::backwardKillLine);
        }
        if (this.buf.cursor() == this.buf.length()) {
            return false;
        }
        int len = cp = this.buf.cursor();
        while (this.count-- > 0) {
            if (this.buf.atChar(len) == 10) {
                ++len;
                continue;
            }
            while (this.buf.atChar(len) != 0 && this.buf.atChar(len) != 10) {
                ++len;
            }
        }
        int num = len - cp;
        String killed = this.buf.substring(cp, cp + num);
        this.buf.delete(num);
        this.killRing.add(killed);
        return true;
    }

    public boolean backwardKillLine() {
        int cp;
        if (this.count < 0) {
            return this.callNeg(this::killLine);
        }
        if (this.buf.cursor() == 0) {
            return false;
        }
        int beg = cp = this.buf.cursor();
        while (this.count-- > 0 && beg != 0) {
            if (this.buf.atChar(beg - 1) == 10) {
                --beg;
                continue;
            }
            while (beg > 0 && this.buf.atChar(beg - 1) != 0 && this.buf.atChar(beg - 1) != 10) {
                --beg;
            }
        }
        int num = cp - beg;
        String killed = this.buf.substring(cp - beg, cp);
        this.buf.cursor(beg);
        this.buf.delete(num);
        this.killRing.add(killed);
        return true;
    }

    public boolean killRegion() {
        return this.doCopyKillRegion(true);
    }

    public boolean copyRegionAsKill() {
        return this.doCopyKillRegion(false);
    }

    private boolean doCopyKillRegion(boolean kill) {
        if (this.regionMark > this.buf.length()) {
            this.regionMark = this.buf.length();
        }
        if (this.regionActive == LineReader.RegionType.LINE) {
            int start = this.regionMark;
            int end = this.buf.cursor();
            if (start < end) {
                while (start > 0 && this.buf.atChar(start - 1) != 10) {
                    --start;
                }
                while (end < this.buf.length() - 1 && this.buf.atChar(end + 1) != 10) {
                    ++end;
                }
                if (this.isInViCmdMode()) {
                    ++end;
                }
                this.killRing.add(this.buf.substring(start, end));
                if (kill) {
                    this.buf.backspace(end - start);
                }
            } else {
                while (end > 0 && this.buf.atChar(end - 1) != 10) {
                    --end;
                }
                while (start < this.buf.length() && this.buf.atChar(start) != 10) {
                    ++start;
                }
                if (this.isInViCmdMode()) {
                    ++start;
                }
                this.killRing.addBackwards(this.buf.substring(end, start));
                if (kill) {
                    this.buf.cursor(end);
                    this.buf.delete(start - end);
                }
            }
        } else if (this.regionMark > this.buf.cursor()) {
            if (this.isInViCmdMode()) {
                ++this.regionMark;
            }
            this.killRing.add(this.buf.substring(this.buf.cursor(), this.regionMark));
            if (kill) {
                this.buf.delete(this.regionMark - this.buf.cursor());
            }
        } else {
            if (this.isInViCmdMode()) {
                this.buf.move(1);
            }
            this.killRing.add(this.buf.substring(this.regionMark, this.buf.cursor()));
            if (kill) {
                this.buf.backspace(this.buf.cursor() - this.regionMark);
            }
        }
        if (kill) {
            this.regionActive = LineReader.RegionType.NONE;
        }
        return true;
    }

    public boolean yank() {
        String yanked = this.killRing.yank();
        if (yanked == null) {
            return false;
        }
        this.putString(yanked);
        return true;
    }

    public boolean yankPop() {
        if (!this.killRing.lastYank()) {
            return false;
        }
        String current = this.killRing.yank();
        if (current == null) {
            return false;
        }
        this.buf.backspace(current.length());
        String yanked = this.killRing.yankPop();
        if (yanked == null) {
            return false;
        }
        this.putString(yanked);
        return true;
    }

    public boolean mouse() {
        MouseEvent event = this.readMouseEvent();
        if (event.getType() == MouseEvent.Type.Released && event.getButton() == MouseEvent.Button.Button1) {
            StringBuilder tsb = new StringBuilder();
            Cursor cursor = this.terminal.getCursorPosition(c -> tsb.append((char)c));
            this.bindingReader.runMacro(tsb.toString());
            ArrayList<AttributedString> secondaryPrompts = new ArrayList<AttributedString>();
            this.getDisplayedBufferWithPrompts(secondaryPrompts);
            AttributedStringBuilder sb = new AttributedStringBuilder().tabs(this.getTabWidth());
            sb.append(this.prompt);
            sb.append(this.insertSecondaryPrompts(new AttributedString(this.buf.upToCursor()), secondaryPrompts, false));
            List<AttributedString> promptLines = sb.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
            int currentLine = promptLines.size() - 1;
            int wantedLine = Math.max(0, Math.min(currentLine + event.getY() - cursor.getY(), secondaryPrompts.size()));
            int pl0 = currentLine == 0 ? this.prompt.columnLength() : ((AttributedString)secondaryPrompts.get(currentLine - 1)).columnLength();
            int pl1 = wantedLine == 0 ? this.prompt.columnLength() : ((AttributedString)secondaryPrompts.get(wantedLine - 1)).columnLength();
            int adjust = pl1 - pl0;
            this.buf.moveXY(event.getX() - cursor.getX() - adjust, event.getY() - cursor.getY());
        }
        return true;
    }

    public boolean beginPaste() {
        String str = this.doReadStringUntil(BRACKETED_PASTE_END);
        this.regionActive = LineReader.RegionType.PASTE;
        this.regionMark = this.getBuffer().cursor();
        this.getBuffer().write(str.replace('\r', '\n'));
        return true;
    }

    public boolean focusIn() {
        return false;
    }

    public boolean focusOut() {
        return false;
    }

    public boolean clear() {
        this.display.update(Collections.emptyList(), 0);
        return true;
    }

    public boolean clearScreen() {
        if (this.terminal.puts(InfoCmp.Capability.clear_screen, new Object[0])) {
            Status status;
            if ("windows-conemu".equals(this.terminal.getType()) && !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate")) {
                this.terminal.writer().write("\u001b[9999E");
            }
            if ((status = Status.getStatus(this.terminal, false)) != null) {
                status.reset();
            }
            this.redrawLine();
        } else {
            this.println();
        }
        return true;
    }

    public boolean beep() {
        BellType bell_preference = BellType.AUDIBLE;
        switch (this.getString("bell-style", DEFAULT_BELL_STYLE).toLowerCase()) {
            case "none": 
            case "off": {
                bell_preference = BellType.NONE;
                break;
            }
            case "audible": {
                bell_preference = BellType.AUDIBLE;
                break;
            }
            case "visible": {
                bell_preference = BellType.VISIBLE;
                break;
            }
            case "on": {
                BellType bellType = bell_preference = this.getBoolean("prefer-visible-bell", false) ? BellType.VISIBLE : BellType.AUDIBLE;
            }
        }
        if (bell_preference == BellType.VISIBLE) {
            if (this.terminal.puts(InfoCmp.Capability.flash_screen, new Object[0]) || this.terminal.puts(InfoCmp.Capability.bell, new Object[0])) {
                this.flush();
            }
        } else if (bell_preference == BellType.AUDIBLE && this.terminal.puts(InfoCmp.Capability.bell, new Object[0])) {
            this.flush();
        }
        return true;
    }

    protected boolean isDelimiter(int c) {
        return !Character.isLetterOrDigit(c);
    }

    protected boolean isWhitespace(int c) {
        return Character.isWhitespace(c);
    }

    protected boolean isViAlphaNum(int c) {
        return c == 95 || Character.isLetterOrDigit(c);
    }

    protected boolean isAlpha(int c) {
        return Character.isLetter(c);
    }

    protected boolean isWord(int c) {
        String wordchars = this.getString("WORDCHARS", DEFAULT_WORDCHARS);
        return Character.isLetterOrDigit(c) || c < 128 && wordchars.indexOf((char)c) >= 0;
    }

    String getString(String name, String def) {
        return ReaderUtils.getString(this, name, def);
    }

    boolean getBoolean(String name, boolean def) {
        return ReaderUtils.getBoolean(this, name, def);
    }

    int getInt(String name, int def) {
        return ReaderUtils.getInt(this, name, def);
    }

    long getLong(String name, long def) {
        return ReaderUtils.getLong(this, name, def);
    }

    @Override
    public Map<String, KeyMap<Binding>> defaultKeyMaps() {
        HashMap<String, KeyMap<Binding>> keyMaps = new HashMap<String, KeyMap<Binding>>();
        keyMaps.put("emacs", this.emacs());
        keyMaps.put("vicmd", this.viCmd());
        keyMaps.put("viins", this.viInsertion());
        keyMaps.put("menu", this.menu());
        keyMaps.put("viopp", this.viOpp());
        keyMaps.put("visual", this.visual());
        keyMaps.put(".safe", this.safe());
        keyMaps.put("dumb", this.dumb());
        if (this.getBoolean("bind-tty-special-chars", true)) {
            Attributes attr = this.terminal.getAttributes();
            this.bindConsoleChars((KeyMap)keyMaps.get("emacs"), attr);
            this.bindConsoleChars((KeyMap)keyMaps.get("viins"), attr);
        }
        for (KeyMap keyMap : keyMaps.values()) {
            keyMap.setUnicode(new Reference("self-insert"));
            keyMap.setAmbiguousTimeout(this.getLong("ambiguous-binding", 1000L));
        }
        keyMaps.put("main", (KeyMap)keyMaps.get(this.isTerminalDumb() ? "dumb" : "emacs"));
        return keyMaps;
    }

    public KeyMap<Binding> emacs() {
        KeyMap<Binding> emacs = new KeyMap<Binding>();
        this.bindKeys(emacs);
        this.bind(emacs, "set-mark-command", KeyMap.ctrl('@'));
        this.bind(emacs, "beginning-of-line", KeyMap.ctrl('A'));
        this.bind(emacs, "backward-char", KeyMap.ctrl('B'));
        this.bind(emacs, "delete-char-or-list", KeyMap.ctrl('D'));
        this.bind(emacs, "end-of-line", KeyMap.ctrl('E'));
        this.bind(emacs, "forward-char", KeyMap.ctrl('F'));
        this.bind(emacs, "abort", KeyMap.ctrl('G'));
        this.bind(emacs, "backward-delete-char", KeyMap.ctrl('H'));
        this.bind(emacs, "expand-or-complete", KeyMap.ctrl('I'));
        this.bind(emacs, "accept-line", KeyMap.ctrl('J'));
        this.bind(emacs, "kill-line", KeyMap.ctrl('K'));
        this.bind(emacs, "clear-screen", KeyMap.ctrl('L'));
        this.bind(emacs, "accept-line", KeyMap.ctrl('M'));
        this.bind(emacs, "down-line-or-history", KeyMap.ctrl('N'));
        this.bind(emacs, "accept-line-and-down-history", KeyMap.ctrl('O'));
        this.bind(emacs, "up-line-or-history", KeyMap.ctrl('P'));
        this.bind(emacs, "history-incremental-search-backward", KeyMap.ctrl('R'));
        this.bind(emacs, "history-incremental-search-forward", KeyMap.ctrl('S'));
        this.bind(emacs, "transpose-chars", KeyMap.ctrl('T'));
        this.bind(emacs, "kill-whole-line", KeyMap.ctrl('U'));
        this.bind(emacs, "quoted-insert", KeyMap.ctrl('V'));
        this.bind(emacs, "backward-kill-word", KeyMap.ctrl('W'));
        this.bind(emacs, "yank", KeyMap.ctrl('Y'));
        this.bind(emacs, "character-search", KeyMap.ctrl(']'));
        this.bind(emacs, "undo", KeyMap.ctrl('_'));
        this.bind(emacs, "self-insert", KeyMap.range(" -~"));
        this.bind(emacs, "insert-close-paren", DESC_SUFFIX);
        this.bind(emacs, "insert-close-square", "]");
        this.bind(emacs, "insert-close-curly", "}");
        this.bind(emacs, "backward-delete-char", KeyMap.del());
        this.bind(emacs, "vi-match-bracket", KeyMap.translate("^X^B"));
        this.bind(emacs, "abort", KeyMap.translate("^X^G"));
        this.bind(emacs, "edit-and-execute-command", KeyMap.translate("^X^E"));
        this.bind(emacs, "vi-find-next-char", KeyMap.translate("^X^F"));
        this.bind(emacs, "vi-join", KeyMap.translate("^X^J"));
        this.bind(emacs, "kill-buffer", KeyMap.translate("^X^K"));
        this.bind(emacs, "infer-next-history", KeyMap.translate("^X^N"));
        this.bind(emacs, "overwrite-mode", KeyMap.translate("^X^O"));
        this.bind(emacs, "redo", KeyMap.translate("^X^R"));
        this.bind(emacs, "undo", KeyMap.translate("^X^U"));
        this.bind(emacs, "vi-cmd-mode", KeyMap.translate("^X^V"));
        this.bind(emacs, "exchange-point-and-mark", KeyMap.translate("^X^X"));
        this.bind(emacs, "do-lowercase-version", KeyMap.translate("^XA-^XZ"));
        this.bind(emacs, "what-cursor-position", KeyMap.translate("^X="));
        this.bind(emacs, "kill-line", KeyMap.translate("^X^?"));
        this.bind(emacs, "abort", KeyMap.alt(KeyMap.ctrl('G')));
        this.bind(emacs, "backward-kill-word", KeyMap.alt(KeyMap.ctrl('H')));
        this.bind(emacs, "self-insert-unmeta", KeyMap.alt(KeyMap.ctrl('M')));
        this.bind(emacs, "complete-word", KeyMap.alt(KeyMap.esc()));
        this.bind(emacs, "character-search-backward", KeyMap.alt(KeyMap.ctrl(']')));
        this.bind(emacs, "copy-prev-word", KeyMap.alt(KeyMap.ctrl('_')));
        this.bind(emacs, "set-mark-command", KeyMap.alt(' '));
        this.bind(emacs, "neg-argument", KeyMap.alt('-'));
        this.bind(emacs, "digit-argument", KeyMap.range("\\E0-\\E9"));
        this.bind(emacs, "beginning-of-history", KeyMap.alt('<'));
        this.bind(emacs, "list-choices", KeyMap.alt('='));
        this.bind(emacs, "end-of-history", KeyMap.alt('>'));
        this.bind(emacs, "list-choices", KeyMap.alt('?'));
        this.bind(emacs, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
        this.bind(emacs, "accept-and-hold", KeyMap.alt('a'));
        this.bind(emacs, "backward-word", KeyMap.alt('b'));
        this.bind(emacs, "capitalize-word", KeyMap.alt('c'));
        this.bind(emacs, "kill-word", KeyMap.alt('d'));
        this.bind(emacs, "kill-word", KeyMap.translate("^[[3;5~"));
        this.bind(emacs, "forward-word", KeyMap.alt('f'));
        this.bind(emacs, "down-case-word", KeyMap.alt('l'));
        this.bind(emacs, "history-search-forward", KeyMap.alt('n'));
        this.bind(emacs, "history-search-backward", KeyMap.alt('p'));
        this.bind(emacs, "transpose-words", KeyMap.alt('t'));
        this.bind(emacs, "up-case-word", KeyMap.alt('u'));
        this.bind(emacs, "yank-pop", KeyMap.alt('y'));
        this.bind(emacs, "backward-kill-word", KeyMap.alt(KeyMap.del()));
        this.bindArrowKeys(emacs);
        this.bind(emacs, "forward-word", KeyMap.translate("^[[1;5C"));
        this.bind(emacs, "backward-word", KeyMap.translate("^[[1;5D"));
        this.bind(emacs, "forward-word", KeyMap.alt(this.key(InfoCmp.Capability.key_right)));
        this.bind(emacs, "backward-word", KeyMap.alt(this.key(InfoCmp.Capability.key_left)));
        this.bind(emacs, "forward-word", KeyMap.alt(KeyMap.translate("^[[C")));
        this.bind(emacs, "backward-word", KeyMap.alt(KeyMap.translate("^[[D")));
        return emacs;
    }

    public KeyMap<Binding> viInsertion() {
        KeyMap<Binding> viins = new KeyMap<Binding>();
        this.bindKeys(viins);
        this.bind(viins, "self-insert", KeyMap.range("^@-^_"));
        this.bind(viins, "list-choices", KeyMap.ctrl('D'));
        this.bind(viins, "abort", KeyMap.ctrl('G'));
        this.bind(viins, "backward-delete-char", KeyMap.ctrl('H'));
        this.bind(viins, "expand-or-complete", KeyMap.ctrl('I'));
        this.bind(viins, "accept-line", KeyMap.ctrl('J'));
        this.bind(viins, "clear-screen", KeyMap.ctrl('L'));
        this.bind(viins, "accept-line", KeyMap.ctrl('M'));
        this.bind(viins, "menu-complete", KeyMap.ctrl('N'));
        this.bind(viins, "reverse-menu-complete", KeyMap.ctrl('P'));
        this.bind(viins, "history-incremental-search-backward", KeyMap.ctrl('R'));
        this.bind(viins, "history-incremental-search-forward", KeyMap.ctrl('S'));
        this.bind(viins, "transpose-chars", KeyMap.ctrl('T'));
        this.bind(viins, "kill-whole-line", KeyMap.ctrl('U'));
        this.bind(viins, "quoted-insert", KeyMap.ctrl('V'));
        this.bind(viins, "backward-kill-word", KeyMap.ctrl('W'));
        this.bind(viins, "yank", KeyMap.ctrl('Y'));
        this.bind(viins, "vi-cmd-mode", KeyMap.ctrl('['));
        this.bind(viins, "undo", KeyMap.ctrl('_'));
        this.bind(viins, "history-incremental-search-backward", KeyMap.ctrl('X') + "r");
        this.bind(viins, "history-incremental-search-forward", KeyMap.ctrl('X') + "s");
        this.bind(viins, "self-insert", KeyMap.range(" -~"));
        this.bind(viins, "insert-close-paren", DESC_SUFFIX);
        this.bind(viins, "insert-close-square", "]");
        this.bind(viins, "insert-close-curly", "}");
        this.bind(viins, "backward-delete-char", KeyMap.del());
        this.bindArrowKeys(viins);
        return viins;
    }

    public KeyMap<Binding> viCmd() {
        KeyMap<Binding> vicmd = new KeyMap<Binding>();
        this.bind(vicmd, "list-choices", KeyMap.ctrl('D'));
        this.bind(vicmd, "emacs-editing-mode", KeyMap.ctrl('E'));
        this.bind(vicmd, "abort", KeyMap.ctrl('G'));
        this.bind(vicmd, "vi-backward-char", KeyMap.ctrl('H'));
        this.bind(vicmd, "accept-line", KeyMap.ctrl('J'));
        this.bind(vicmd, "kill-line", KeyMap.ctrl('K'));
        this.bind(vicmd, "clear-screen", KeyMap.ctrl('L'));
        this.bind(vicmd, "accept-line", KeyMap.ctrl('M'));
        this.bind(vicmd, "vi-down-line-or-history", KeyMap.ctrl('N'));
        this.bind(vicmd, "vi-up-line-or-history", KeyMap.ctrl('P'));
        this.bind(vicmd, "quoted-insert", KeyMap.ctrl('Q'));
        this.bind(vicmd, "history-incremental-search-backward", KeyMap.ctrl('R'));
        this.bind(vicmd, "history-incremental-search-forward", KeyMap.ctrl('S'));
        this.bind(vicmd, "transpose-chars", KeyMap.ctrl('T'));
        this.bind(vicmd, "kill-whole-line", KeyMap.ctrl('U'));
        this.bind(vicmd, "quoted-insert", KeyMap.ctrl('V'));
        this.bind(vicmd, "backward-kill-word", KeyMap.ctrl('W'));
        this.bind(vicmd, "yank", KeyMap.ctrl('Y'));
        this.bind(vicmd, "history-incremental-search-backward", KeyMap.ctrl('X') + "r");
        this.bind(vicmd, "history-incremental-search-forward", KeyMap.ctrl('X') + "s");
        this.bind(vicmd, "abort", KeyMap.alt(KeyMap.ctrl('G')));
        this.bind(vicmd, "backward-kill-word", KeyMap.alt(KeyMap.ctrl('H')));
        this.bind(vicmd, "self-insert-unmeta", KeyMap.alt(KeyMap.ctrl('M')));
        this.bind(vicmd, "complete-word", KeyMap.alt(KeyMap.esc()));
        this.bind(vicmd, "character-search-backward", KeyMap.alt(KeyMap.ctrl(']')));
        this.bind(vicmd, "set-mark-command", KeyMap.alt(' '));
        this.bind(vicmd, "digit-argument", KeyMap.alt('-'));
        this.bind(vicmd, "beginning-of-history", KeyMap.alt('<'));
        this.bind(vicmd, "list-choices", KeyMap.alt('='));
        this.bind(vicmd, "end-of-history", KeyMap.alt('>'));
        this.bind(vicmd, "list-choices", KeyMap.alt('?'));
        this.bind(vicmd, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
        this.bind(vicmd, "backward-word", KeyMap.alt('b'));
        this.bind(vicmd, "capitalize-word", KeyMap.alt('c'));
        this.bind(vicmd, "kill-word", KeyMap.alt('d'));
        this.bind(vicmd, "forward-word", KeyMap.alt('f'));
        this.bind(vicmd, "down-case-word", KeyMap.alt('l'));
        this.bind(vicmd, "history-search-forward", KeyMap.alt('n'));
        this.bind(vicmd, "history-search-backward", KeyMap.alt('p'));
        this.bind(vicmd, "transpose-words", KeyMap.alt('t'));
        this.bind(vicmd, "up-case-word", KeyMap.alt('u'));
        this.bind(vicmd, "yank-pop", KeyMap.alt('y'));
        this.bind(vicmd, "backward-kill-word", KeyMap.alt(KeyMap.del()));
        this.bind(vicmd, "forward-char", " ");
        this.bind(vicmd, "vi-insert-comment", DEFAULT_COMMENT_BEGIN);
        this.bind(vicmd, "end-of-line", "$");
        this.bind(vicmd, "vi-match-bracket", "%");
        this.bind(vicmd, "vi-down-line-or-history", "+");
        this.bind(vicmd, "vi-rev-repeat-find", ",");
        this.bind(vicmd, "vi-up-line-or-history", "-");
        this.bind(vicmd, "vi-repeat-change", ".");
        this.bind(vicmd, "vi-history-search-backward", "/");
        this.bind(vicmd, "vi-digit-or-beginning-of-line", "0");
        this.bind(vicmd, "digit-argument", KeyMap.range("1-9"));
        this.bind(vicmd, "vi-repeat-find", ";");
        this.bind(vicmd, "list-choices", "=");
        this.bind(vicmd, "vi-history-search-forward", "?");
        this.bind(vicmd, "vi-add-eol", "A");
        this.bind(vicmd, "vi-backward-blank-word", "B");
        this.bind(vicmd, "vi-change-eol", "C");
        this.bind(vicmd, "vi-kill-eol", "D");
        this.bind(vicmd, "vi-forward-blank-word-end", "E");
        this.bind(vicmd, "vi-find-prev-char", "F");
        this.bind(vicmd, "vi-fetch-history", "G");
        this.bind(vicmd, "vi-insert-bol", "I");
        this.bind(vicmd, "vi-join", "J");
        this.bind(vicmd, "vi-rev-repeat-search", "N");
        this.bind(vicmd, "vi-open-line-above", "O");
        this.bind(vicmd, "vi-put-before", "P");
        this.bind(vicmd, "vi-replace", "R");
        this.bind(vicmd, "vi-kill-line", "S");
        this.bind(vicmd, "vi-find-prev-char-skip", "T");
        this.bind(vicmd, "redo", "U");
        this.bind(vicmd, "visual-line-mode", "V");
        this.bind(vicmd, "vi-forward-blank-word", "W");
        this.bind(vicmd, "vi-backward-delete-char", "X");
        this.bind(vicmd, "vi-yank-whole-line", "Y");
        this.bind(vicmd, "vi-first-non-blank", "^");
        this.bind(vicmd, "vi-add-next", "a");
        this.bind(vicmd, "vi-backward-word", "b");
        this.bind(vicmd, "vi-change-to", "c");
        this.bind(vicmd, "vi-delete", "d");
        this.bind(vicmd, "vi-forward-word-end", "e");
        this.bind(vicmd, "vi-find-next-char", "f");
        this.bind(vicmd, "what-cursor-position", "ga");
        this.bind(vicmd, "vi-backward-blank-word-end", "gE");
        this.bind(vicmd, "vi-backward-word-end", "ge");
        this.bind(vicmd, "vi-backward-char", "h");
        this.bind(vicmd, "vi-insert", "i");
        this.bind(vicmd, "down-line-or-history", "j");
        this.bind(vicmd, "up-line-or-history", "k");
        this.bind(vicmd, "vi-forward-char", "l");
        this.bind(vicmd, "vi-repeat-search", "n");
        this.bind(vicmd, "vi-open-line-below", "o");
        this.bind(vicmd, "vi-put-after", "p");
        this.bind(vicmd, "vi-replace-chars", "r");
        this.bind(vicmd, "vi-substitute", "s");
        this.bind(vicmd, "vi-find-next-char-skip", "t");
        this.bind(vicmd, "undo", "u");
        this.bind(vicmd, "visual-mode", "v");
        this.bind(vicmd, "vi-forward-word", "w");
        this.bind(vicmd, "vi-delete-char", "x");
        this.bind(vicmd, "vi-yank", "y");
        this.bind(vicmd, "vi-goto-column", "|");
        this.bind(vicmd, "vi-swap-case", "~");
        this.bind(vicmd, "vi-backward-char", KeyMap.del());
        this.bindArrowKeys(vicmd);
        return vicmd;
    }

    public KeyMap<Binding> menu() {
        KeyMap<Binding> menu = new KeyMap<Binding>();
        this.bind(menu, "menu-complete", "\t");
        this.bind(menu, "reverse-menu-complete", this.key(InfoCmp.Capability.back_tab));
        this.bind(menu, "accept-line", "\r", "\n");
        this.bindArrowKeys(menu);
        return menu;
    }

    public KeyMap<Binding> safe() {
        KeyMap<Binding> safe = new KeyMap<Binding>();
        this.bind(safe, "self-insert", KeyMap.range("^@-^?"));
        this.bind(safe, "accept-line", "\r", "\n");
        this.bind(safe, "abort", KeyMap.ctrl('G'));
        return safe;
    }

    public KeyMap<Binding> dumb() {
        KeyMap<Binding> dumb = new KeyMap<Binding>();
        this.bind(dumb, "self-insert", KeyMap.range("^@-^?"));
        this.bind(dumb, "accept-line", "\r", "\n");
        this.bind(dumb, "beep", KeyMap.ctrl('G'));
        return dumb;
    }

    public KeyMap<Binding> visual() {
        KeyMap<Binding> visual = new KeyMap<Binding>();
        this.bind(visual, "up-line", this.key(InfoCmp.Capability.key_up), "k");
        this.bind(visual, "down-line", this.key(InfoCmp.Capability.key_down), "j");
        this.bind(visual, this::deactivateRegion, KeyMap.esc());
        this.bind(visual, "exchange-point-and-mark", "o");
        this.bind(visual, "put-replace-selection", "p");
        this.bind(visual, "vi-delete", "x");
        this.bind(visual, "vi-oper-swap-case", "~");
        return visual;
    }

    public KeyMap<Binding> viOpp() {
        KeyMap<Binding> viOpp = new KeyMap<Binding>();
        this.bind(viOpp, "up-line", this.key(InfoCmp.Capability.key_up), "k");
        this.bind(viOpp, "down-line", this.key(InfoCmp.Capability.key_down), "j");
        this.bind(viOpp, "vi-cmd-mode", KeyMap.esc());
        return viOpp;
    }

    private void bind(KeyMap<Binding> map, String widget, Iterable<? extends CharSequence> keySeqs) {
        map.bind((Binding)new Reference(widget), keySeqs);
    }

    private void bind(KeyMap<Binding> map, String widget, CharSequence ... keySeqs) {
        map.bind((Binding)new Reference(widget), keySeqs);
    }

    private void bind(KeyMap<Binding> map, Widget widget, CharSequence ... keySeqs) {
        map.bind((Binding)widget, keySeqs);
    }

    private String key(InfoCmp.Capability capability) {
        return KeyMap.key(this.terminal, capability);
    }

    private void bindKeys(KeyMap<Binding> emacs) {
        Widget beep = this.namedWidget("beep", this::beep);
        Stream.of(InfoCmp.Capability.values()).filter(c -> c.name().startsWith("key_")).map(this::key).forEach(k -> this.bind(emacs, beep, (CharSequence)k));
    }

    private void bindArrowKeys(KeyMap<Binding> map) {
        this.bind(map, "up-line-or-search", this.key(InfoCmp.Capability.key_up));
        this.bind(map, "down-line-or-search", this.key(InfoCmp.Capability.key_down));
        this.bind(map, "backward-char", this.key(InfoCmp.Capability.key_left));
        this.bind(map, "forward-char", this.key(InfoCmp.Capability.key_right));
        this.bind(map, "beginning-of-line", this.key(InfoCmp.Capability.key_home));
        this.bind(map, "end-of-line", this.key(InfoCmp.Capability.key_end));
        this.bind(map, "delete-char", this.key(InfoCmp.Capability.key_dc));
        this.bind(map, "kill-whole-line", this.key(InfoCmp.Capability.key_dl));
        this.bind(map, "overwrite-mode", this.key(InfoCmp.Capability.key_ic));
        this.bind(map, "mouse", (CharSequence[])MouseSupport.keys(this.terminal));
        this.bind(map, "begin-paste", BRACKETED_PASTE_BEGIN);
        this.bind(map, "terminal-focus-in", FOCUS_IN_SEQ);
        this.bind(map, "terminal-focus-out", FOCUS_OUT_SEQ);
    }

    private void bindConsoleChars(KeyMap<Binding> keyMap, Attributes attr) {
        if (attr != null) {
            this.rebind(keyMap, "backward-delete-char", KeyMap.del(), (char)attr.getControlChar(Attributes.ControlChar.VERASE));
            this.rebind(keyMap, "backward-kill-word", KeyMap.ctrl('W'), (char)attr.getControlChar(Attributes.ControlChar.VWERASE));
            this.rebind(keyMap, "kill-whole-line", KeyMap.ctrl('U'), (char)attr.getControlChar(Attributes.ControlChar.VKILL));
            this.rebind(keyMap, "quoted-insert", KeyMap.ctrl('V'), (char)attr.getControlChar(Attributes.ControlChar.VLNEXT));
        }
    }

    private void rebind(KeyMap<Binding> keyMap, String operation, String prevBinding, char newBinding) {
        if (newBinding > '\u0000' && newBinding < '\u0080') {
            Reference ref = new Reference(operation);
            this.bind(keyMap, "self-insert", prevBinding);
            keyMap.bind((Binding)ref, (CharSequence)Character.toString(newBinding));
        }
    }

    @Override
    public void zeroOut() {
        this.buf.zeroOut();
        this.parsedLine = null;
    }

    private static /* synthetic */ void lambda$readLine$1(AtomicBoolean interrupted, Terminal.Signal s) {
        interrupted.set(true);
    }

    private static /* synthetic */ void lambda$readLine$0(Thread readLineThread, Terminal.Signal signal) {
        readLineThread.interrupt();
    }

    protected static enum ViMoveMode {
        NORMAL,
        YANK,
        DELETE,
        CHANGE;

    }

    protected static enum State {
        NORMAL,
        DONE,
        IGNORE,
        EOF,
        INTERRUPT;

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

    private static class TerminalLine {
        private String endLine;
        private int startPos;

        public TerminalLine(String line, int startPos, int width) {
            this.startPos = startPos;
            this.endLine = line.substring(line.lastIndexOf(10) + 1);
            boolean first = true;
            while (this.endLine.length() + (first ? startPos : 0) > width && width > 0) {
                this.endLine = first ? this.endLine.substring(width - startPos) : this.endLine.substring(width);
                first = false;
            }
            if (!first) {
                this.startPos = 0;
            }
        }

        public int getStartPos() {
            return this.startPos;
        }

        public String getEndLine() {
            return this.endLine;
        }
    }

    protected static enum CompletionType {
        Expand,
        ExpandComplete,
        Complete,
        List;

    }

    protected static class PostResult {
        final AttributedString post;
        final int lines;
        final int selectedLine;

        public PostResult(AttributedString post, int lines, int selectedLine) {
            this.post = post;
            this.lines = lines;
            this.selectedLine = selectedLine;
        }
    }

    private class MenuSupport
    implements Supplier<AttributedString> {
        final List<Candidate> possible = new ArrayList<Candidate>();
        final BiFunction<CharSequence, Boolean, CharSequence> escaper;
        int selection;
        int topLine;
        String word;
        AttributedString computed;
        int lines;
        int columns;
        String completed;

        public MenuSupport(List<Candidate> original, String completed, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
            this.escaper = escaper;
            this.selection = -1;
            this.topLine = 0;
            this.word = LineReaderImpl.DEFAULT_BELL_STYLE;
            this.completed = completed;
            LineReaderImpl.this.computePost(original, null, this.possible, completed);
            this.next();
        }

        public Candidate completion() {
            return this.possible.get(this.selection);
        }

        public void next() {
            this.selection = (this.selection + 1) % this.possible.size();
            this.update();
        }

        public void previous() {
            this.selection = (this.selection + this.possible.size() - 1) % this.possible.size();
            this.update();
        }

        private void major(int step) {
            int axis = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
            int sel = this.selection + step * axis;
            if (sel < 0) {
                int pos = (sel + axis) % axis;
                int remainders = this.possible.size() % axis;
                sel = this.possible.size() - remainders + pos;
                if (sel >= this.possible.size()) {
                    sel -= axis;
                }
            } else if (sel >= this.possible.size()) {
                sel %= axis;
            }
            this.selection = sel;
            this.update();
        }

        private void minor(int step) {
            int options;
            int axis = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
            int row = this.selection % axis;
            if (this.selection - row + axis > (options = this.possible.size())) {
                axis = options % axis;
            }
            this.selection = this.selection - row + (axis + row + step) % axis;
            this.update();
        }

        public void up() {
            if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
                this.major(-1);
            } else {
                this.minor(-1);
            }
        }

        public void down() {
            if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
                this.major(1);
            } else {
                this.minor(1);
            }
        }

        public void left() {
            if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
                this.minor(-1);
            } else {
                this.major(-1);
            }
        }

        public void right() {
            if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
                this.minor(1);
            } else {
                this.major(1);
            }
        }

        private void update() {
            LineReaderImpl.this.buf.backspace(this.word.length());
            this.word = this.escaper.apply(this.completion().value(), true).toString();
            LineReaderImpl.this.buf.write(this.word);
            PostResult pr = LineReaderImpl.this.computePost(this.possible, this.completion(), null, this.completed);
            int displaySize = LineReaderImpl.this.displayRows() - LineReaderImpl.this.promptLines();
            if (pr.lines > displaySize) {
                AttributedString post;
                int displayed = displaySize - 1;
                if (pr.selectedLine >= 0) {
                    if (pr.selectedLine < this.topLine) {
                        this.topLine = pr.selectedLine;
                    } else if (pr.selectedLine >= this.topLine + displayed) {
                        this.topLine = pr.selectedLine - displayed + 1;
                    }
                }
                if ((post = pr.post).length() > 0 && post.charAt(post.length() - 1) != '\n') {
                    post = new AttributedStringBuilder(post.length() + 1).append(post).append("\n").toAttributedString();
                }
                List<AttributedString> lines = post.columnSplitLength(LineReaderImpl.this.size.getColumns(), true, LineReaderImpl.this.display.delayLineWrap());
                ArrayList<AttributedString> sub = new ArrayList<AttributedString>(lines.subList(this.topLine, this.topLine + displayed));
                sub.add(new AttributedStringBuilder().style(AttributedStyle.DEFAULT.foreground(6)).append("rows ").append(Integer.toString(this.topLine + 1)).append(" to ").append(Integer.toString(this.topLine + displayed)).append(" of ").append(Integer.toString(lines.size())).append("\n").style(AttributedStyle.DEFAULT).toAttributedString());
                this.computed = AttributedString.join(AttributedString.EMPTY, sub);
            } else {
                this.computed = pr.post;
            }
            this.lines = pr.lines;
            this.columns = (this.possible.size() + this.lines - 1) / this.lines;
        }

        @Override
        public AttributedString get() {
            return this.computed;
        }
    }

    private static class CompletingWord
    implements CompletingParsedLine {
        private final String word;

        public CompletingWord(String word) {
            this.word = word;
        }

        @Override
        public CharSequence escape(CharSequence candidate, boolean complete) {
            return null;
        }

        @Override
        public int rawWordCursor() {
            return this.word.length();
        }

        @Override
        public int rawWordLength() {
            return this.word.length();
        }

        @Override
        public String word() {
            return this.word;
        }

        @Override
        public int wordCursor() {
            return this.word.length();
        }

        @Override
        public int wordIndex() {
            return 0;
        }

        @Override
        public List<String> words() {
            return null;
        }

        @Override
        public String line() {
            return this.word;
        }

        @Override
        public int cursor() {
            return this.word.length();
        }
    }

    protected static enum BellType {
        NONE,
        AUDIBLE,
        VISIBLE;

    }
}

