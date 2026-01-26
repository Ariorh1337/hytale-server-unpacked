/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import org.jline.builtins.ClasspathResourceUtil;
import org.jline.builtins.Source;
import org.jline.builtins.Styles;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Log;
import org.jline.utils.StyleResolver;

public class SyntaxHighlighter {
    public static final String REGEX_TOKEN_NAME = "[A-Z_]+";
    public static final String TYPE_NANORCTHEME = ".nanorctheme";
    public static final String DEFAULT_NANORC_FILE = "jnanorc";
    protected static final String DEFAULT_LESSRC_FILE = "jlessrc";
    protected static final String COMMAND_INCLUDE = "include";
    protected static final String COMMAND_THEME = "theme";
    private static final String TOKEN_NANORC = "NANORC";
    private final Path nanorc;
    private final String syntaxName;
    private final String nanorcUrl;
    private final Map<String, List<HighlightRule>> rules = new HashMap<String, List<HighlightRule>>();
    private Path currentTheme;
    private boolean startEndHighlight;
    private int ruleStartId = 0;
    private Parser parser;

    private SyntaxHighlighter() {
        this(null, null, null);
    }

    private SyntaxHighlighter(String nanorcUrl) {
        this(null, null, nanorcUrl);
    }

    private SyntaxHighlighter(Path nanorc, String syntaxName) {
        this(nanorc, syntaxName, null);
    }

    private SyntaxHighlighter(Path nanorc, String syntaxName, String nanorcUrl) {
        this.nanorc = nanorc;
        this.syntaxName = syntaxName;
        this.nanorcUrl = nanorcUrl;
        HashMap defaultRules = new HashMap();
        defaultRules.put(TOKEN_NANORC, new ArrayList());
        this.rules.putAll(defaultRules);
    }

    protected static SyntaxHighlighter build(List<Path> syntaxFiles, String file, String syntaxName) {
        return SyntaxHighlighter.build(syntaxFiles, file, syntaxName, false);
    }

    protected static SyntaxHighlighter build(List<Path> syntaxFiles, String file, String syntaxName, boolean ignoreErrors) {
        SyntaxHighlighter out;
        block15: {
            out = new SyntaxHighlighter();
            HashMap<String, String> colorTheme = new HashMap<String, String>();
            try {
                if (syntaxName == null || !syntaxName.equals("none")) {
                    for (Path p : syntaxFiles) {
                        try {
                            if (colorTheme.isEmpty() && p.getFileName().toString().endsWith(TYPE_NANORCTHEME)) {
                                out.setCurrentTheme(p);
                                BufferedReader reader = Files.newBufferedReader(p);
                                try {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if ((line = line.trim()).isEmpty() || line.startsWith("#")) continue;
                                        List<String> parts = Arrays.asList(line.split("\\s+", 2));
                                        colorTheme.put(parts.get(0), parts.get(1));
                                    }
                                    continue;
                                }
                                finally {
                                    if (reader != null) {
                                        reader.close();
                                    }
                                    continue;
                                }
                            }
                            NanorcParser nanorcParser = new NanorcParser(p, syntaxName, file, colorTheme);
                            nanorcParser.parse();
                            if (nanorcParser.matches()) {
                                out.addRules(nanorcParser.getHighlightRules());
                                out.setParser(nanorcParser.getParser());
                                return out;
                            }
                            if (!nanorcParser.isDefault()) continue;
                            out.addRules(nanorcParser.getHighlightRules());
                        }
                        catch (IOException iOException) {}
                    }
                }
            }
            catch (PatternSyntaxException e) {
                if (ignoreErrors) break block15;
                throw e;
            }
        }
        return out;
    }

    public static SyntaxHighlighter build(Path nanorc, String syntaxName) {
        SyntaxHighlighter out = new SyntaxHighlighter(nanorc, syntaxName);
        ArrayList<Path> syntaxFiles = new ArrayList<Path>();
        try {
            try (BufferedReader reader = Files.newBufferedReader(nanorc);){
                String line;
                while ((line = reader.readLine()) != null) {
                    if ((line = line.trim()).isEmpty() || line.startsWith("#")) continue;
                    List<String> parts = RuleSplitter.split(line);
                    if (parts.get(0).equals(COMMAND_INCLUDE)) {
                        SyntaxHighlighter.nanorcInclude(nanorc, parts.get(1), syntaxFiles);
                        continue;
                    }
                    if (!parts.get(0).equals(COMMAND_THEME)) continue;
                    SyntaxHighlighter.nanorcTheme(nanorc, parts.get(1), syntaxFiles);
                }
            }
            SyntaxHighlighter sh = SyntaxHighlighter.build(syntaxFiles, null, syntaxName);
            out.addRules(sh.rules);
            out.setParser(sh.parser);
            out.setCurrentTheme(sh.currentTheme);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return out;
    }

    protected static void nanorcInclude(Path nanorc, String parameter, List<Path> syntaxFiles) throws IOException {
        SyntaxHighlighter.addFiles(nanorc, parameter, s -> s.forEach(syntaxFiles::add));
    }

    protected static void nanorcTheme(Path nanorc, String parameter, List<Path> syntaxFiles) throws IOException {
        SyntaxHighlighter.addFiles(nanorc, parameter, s -> s.findFirst().ifPresent(p -> syntaxFiles.add(0, (Path)p)));
    }

    protected static void addFiles(Path nanorc, String parameter, Consumer<Stream<Path>> consumer) throws IOException {
        PathParts parts = SyntaxHighlighter.extractPathParts(parameter);
        Path searchRoot = nanorc.resolveSibling(parts.staticPrefix);
        if (Files.exists(searchRoot, new LinkOption[0])) {
            if (parts.globPattern.isEmpty()) {
                consumer.accept(Stream.of(searchRoot));
            } else {
                PathMatcher pathMatcher = searchRoot.getFileSystem().getPathMatcher("glob:" + parts.globPattern);
                try (Stream<Path> pathStream = Files.walk(searchRoot, new FileVisitOption[0]);){
                    consumer.accept(pathStream.filter(p -> pathMatcher.matches(searchRoot.relativize((Path)p))));
                }
            }
        }
    }

    private static PathParts extractPathParts(String pattern) {
        int firstWildcard = Math.min(pattern.indexOf(42) == -1 ? Integer.MAX_VALUE : pattern.indexOf(42), pattern.indexOf(63) == -1 ? Integer.MAX_VALUE : pattern.indexOf(63));
        if (firstWildcard == Integer.MAX_VALUE) {
            return new PathParts(pattern, "");
        }
        int lastSlashBeforeWildcard = -1;
        for (int i = firstWildcard - 1; i >= 0; --i) {
            char c = pattern.charAt(i);
            if (c != '/' && c != '\\') continue;
            lastSlashBeforeWildcard = i;
            break;
        }
        if (lastSlashBeforeWildcard == -1) {
            return new PathParts("", pattern);
        }
        String staticPrefix = pattern.substring(0, lastSlashBeforeWildcard);
        String globPattern = pattern.substring(lastSlashBeforeWildcard + 1);
        return new PathParts(staticPrefix, globPattern);
    }

    public static SyntaxHighlighter build(String nanorcUrl) {
        SyntaxHighlighter out = new SyntaxHighlighter(nanorcUrl);
        try {
            InputStream inputStream;
            if (nanorcUrl.startsWith("classpath:")) {
                String resourcePath = nanorcUrl.substring(10);
                try {
                    Path resourceAsPath = ClasspathResourceUtil.getResourcePath(resourcePath);
                    inputStream = Files.newInputStream(resourceAsPath, new OpenOption[0]);
                }
                catch (Exception e) {
                    inputStream = new Source.ResourceSource(resourcePath, null).read();
                }
            } else {
                inputStream = new Source.URLSource(new URI(nanorcUrl).toURL(), null).read();
            }
            NanorcParser parser = new NanorcParser(inputStream, null, null);
            parser.parse();
            out.addRules(parser.getHighlightRules());
        }
        catch (IOException | URISyntaxException exception) {
            // empty catch block
        }
        return out;
    }

    private void addRules(Map<String, List<HighlightRule>> rules) {
        this.rules.putAll(rules);
    }

    public void setCurrentTheme(Path currentTheme) {
        this.currentTheme = currentTheme;
    }

    public Path getCurrentTheme() {
        return this.currentTheme;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public SyntaxHighlighter reset() {
        this.ruleStartId = 0;
        this.startEndHighlight = false;
        if (this.parser != null) {
            this.parser.reset();
        }
        return this;
    }

    public void refresh() {
        SyntaxHighlighter sh;
        if (this.nanorc != null && this.syntaxName != null) {
            sh = SyntaxHighlighter.build(this.nanorc, this.syntaxName);
        } else if (this.nanorcUrl != null) {
            sh = SyntaxHighlighter.build(this.nanorcUrl);
        } else {
            throw new IllegalStateException("Not possible to refresh highlighter!");
        }
        this.rules.clear();
        this.addRules(sh.rules);
        this.parser = sh.parser;
        this.currentTheme = sh.currentTheme;
    }

    public AttributedString highlight(String string) {
        return this.splitAndHighlight(new AttributedString(string));
    }

    public AttributedString highlight(AttributedStringBuilder asb) {
        return this.splitAndHighlight(asb.toAttributedString());
    }

    public AttributedString highlight(AttributedString attributedString) {
        return this.splitAndHighlight(attributedString);
    }

    private AttributedString splitAndHighlight(AttributedString attributedString) {
        AttributedStringBuilder asb = new AttributedStringBuilder();
        boolean first = true;
        for (AttributedString line : attributedString.columnSplitLength(Integer.MAX_VALUE)) {
            if (!first) {
                asb.append("\n");
            }
            List<Object> tokens = new ArrayList();
            if (this.parser != null) {
                this.parser.parse(line);
                tokens = this.parser.getTokens();
            }
            if (tokens.isEmpty()) {
                asb.append(this._highlight(line, this.rules.get(TOKEN_NANORC)));
            } else {
                int pos = 0;
                for (ParsedToken t : tokens) {
                    if (t.getStart() > pos) {
                        AttributedStringBuilder head = this._highlight(line.columnSubSequence(pos, t.getStart() + 1), this.rules.get(TOKEN_NANORC));
                        asb.append(head.columnSubSequence(0, head.length() - 1));
                    }
                    asb.append(this._highlight(line.columnSubSequence(t.getStart(), t.getEnd()), this.rules.get(t.getName()), t.getStartWith(), line.columnSubSequence(t.getEnd(), line.length())));
                    pos = t.getEnd();
                }
                if (pos < line.length()) {
                    asb.append(this._highlight(line.columnSubSequence(pos, line.length()), this.rules.get(TOKEN_NANORC)));
                }
            }
            first = false;
        }
        return asb.toAttributedString();
    }

    private AttributedStringBuilder _highlight(AttributedString line, List<HighlightRule> rules) {
        return this._highlight(line, rules, null, null);
    }

    private AttributedStringBuilder _highlight(AttributedString line, List<HighlightRule> rules, CharSequence startWith, CharSequence continueAs) {
        AttributedStringBuilder asb = new AttributedStringBuilder();
        asb.append(line);
        if (rules.isEmpty()) {
            return asb;
        }
        int startId = this.ruleStartId;
        boolean endHighlight = this.startEndHighlight;
        block6: for (int i = startId; i < (endHighlight ? startId + 1 : rules.size()); ++i) {
            HighlightRule rule = rules.get(i);
            switch (rule.getType().ordinal()) {
                case 0: {
                    asb.styleMatches(rule.getPattern(), rule.getStyle());
                    continue block6;
                }
                case 1: {
                    boolean done = false;
                    Matcher start = rule.getStart().matcher(asb.toAttributedString());
                    Matcher end = rule.getEnd().matcher(asb.toAttributedString());
                    while (!done) {
                        AttributedStringBuilder a = new AttributedStringBuilder();
                        if (this.startEndHighlight && this.ruleStartId == i) {
                            if (end.find()) {
                                this.ruleStartId = 0;
                                this.startEndHighlight = false;
                                a.append(asb.columnSubSequence(0, end.end()), rule.getStyle());
                                a.append(this._highlight(asb.columnSubSequence(end.end(), asb.length()).toAttributedString(), rules));
                            } else {
                                a.append(asb, rule.getStyle());
                                done = true;
                            }
                            asb = a;
                            continue;
                        }
                        if (start.find()) {
                            a.append(asb.columnSubSequence(0, start.start()));
                            if (end.find()) {
                                a.append(asb.columnSubSequence(start.start(), end.end()), rule.getStyle());
                                a.append(asb.columnSubSequence(end.end(), asb.length()));
                            } else {
                                this.ruleStartId = i;
                                this.startEndHighlight = true;
                                a.append(asb.columnSubSequence(start.start(), asb.length()), rule.getStyle());
                                done = true;
                            }
                            asb = a;
                            continue;
                        }
                        done = true;
                    }
                    continue block6;
                }
                case 2: {
                    if (startWith == null || !startWith.toString().startsWith(rule.getStartWith())) continue block6;
                    asb.styleMatches(rule.getPattern(), rule.getStyle());
                    continue block6;
                }
                case 3: {
                    if (continueAs == null || !continueAs.toString().matches(rule.getContinueAs() + ".*")) continue block6;
                    asb.styleMatches(rule.getPattern(), rule.getStyle());
                }
            }
        }
        return asb;
    }

    static class NanorcParser {
        private static final String DEFAULT_SYNTAX = "default";
        private final String name;
        private final String target;
        private final Map<String, List<HighlightRule>> highlightRules = new HashMap<String, List<HighlightRule>>();
        private final BufferedReader reader;
        private Map<String, String> colorTheme = new HashMap<String, String>();
        private boolean matches = false;
        private String syntaxName = "unknown";
        private Parser parser;

        public NanorcParser(Path file, String name, String target, Map<String, String> colorTheme) throws IOException {
            this(new Source.PathSource(file, null).read(), name, target);
            this.colorTheme = colorTheme;
        }

        public NanorcParser(InputStream in, String name, String target) {
            this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            this.name = name;
            this.target = target;
            this.highlightRules.put(SyntaxHighlighter.TOKEN_NANORC, new ArrayList());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void parse() throws IOException {
            int idx = 0;
            try {
                String line;
                block3: while ((line = this.reader.readLine()) != null) {
                    int n;
                    String key;
                    ++idx;
                    if ((line = line.trim()).isEmpty() || line.startsWith("#")) continue;
                    List<String> parts = RuleSplitter.split(line);
                    if (parts.get(0).equals("syntax")) {
                        this.syntaxName = parts.get(1);
                        ArrayList<Pattern> filePatterns = new ArrayList<Pattern>();
                        if (this.name != null) {
                            if (!this.name.equals(this.syntaxName)) return;
                            this.matches = true;
                            continue;
                        }
                        if (this.target != null) {
                            for (int i = 2; i < parts.size(); ++i) {
                                filePatterns.add(Pattern.compile(parts.get(i)));
                            }
                            for (Pattern p : filePatterns) {
                                if (!p.matcher(this.target).find()) continue;
                                this.matches = true;
                                break;
                            }
                            if (this.matches || this.syntaxName.equals(DEFAULT_SYNTAX)) continue;
                            return;
                        }
                        this.matches = true;
                        continue;
                    }
                    if (parts.get(0).startsWith("$")) {
                        key = this.themeKey(parts.get(0));
                        if (this.colorTheme.containsKey(key)) {
                            if (this.parser == null) {
                                this.parser = new Parser();
                            }
                            String[] args = parts.get(1).split(",\\s*");
                            boolean validKey = true;
                            if (key.startsWith("$BLOCK_COMMENT")) {
                                this.parser.setBlockCommentDelimiters(key, args);
                            } else if (key.startsWith("$LINE_COMMENT")) {
                                this.parser.setLineCommentDelimiters(key, args);
                            } else if (key.startsWith("$BALANCED_DELIMITERS")) {
                                this.parser.setBalancedDelimiters(key, args);
                            } else {
                                Log.warn("Unknown token type: ", key);
                                validKey = false;
                            }
                            if (!validKey) continue;
                            if (!this.highlightRules.containsKey(key)) {
                                this.highlightRules.put(key, new ArrayList());
                            }
                            String[] stringArray = this.colorTheme.get(key).split("\\\\n");
                            n = stringArray.length;
                            int n2 = 0;
                            while (true) {
                                if (n2 >= n) continue block3;
                                String l = stringArray[n2];
                                this.addHighlightRule(RuleSplitter.split(l), ++idx, key);
                                ++n2;
                            }
                        }
                        Log.warn("Unknown token type: ", key);
                        continue;
                    }
                    if (this.addHighlightRule(parts, idx, SyntaxHighlighter.TOKEN_NANORC) || !parts.get(0).matches("\\+[A-Z_]+")) continue;
                    key = this.themeKey(parts.get(0));
                    String theme = this.colorTheme.get(key);
                    if (theme != null) {
                        String[] stringArray = theme.split("\\\\n");
                        int n3 = stringArray.length;
                        n = 0;
                        while (true) {
                            if (n >= n3) continue block3;
                            String l = stringArray[n];
                            this.addHighlightRule(RuleSplitter.split(l), ++idx, SyntaxHighlighter.TOKEN_NANORC);
                            ++n;
                        }
                    }
                    Log.warn("Unknown token type: ", key);
                }
                return;
            }
            finally {
                this.reader.close();
            }
        }

        private boolean addHighlightRule(List<String> parts, int idx, String tokenName) {
            boolean out = true;
            if (parts.get(0).equals("color")) {
                this.addHighlightRule(this.syntaxName + idx, parts, false, tokenName);
            } else if (parts.get(0).equals("icolor")) {
                this.addHighlightRule(this.syntaxName + idx, parts, true, tokenName);
            } else if (parts.get(0).matches("[A-Z_]+[:]?")) {
                String key = this.themeKey(parts.get(0));
                String theme = this.colorTheme.get(key);
                if (theme != null) {
                    parts.set(0, "color");
                    parts.add(1, theme);
                    this.addHighlightRule(this.syntaxName + idx, parts, false, tokenName);
                } else {
                    Log.warn("Unknown token type: ", key);
                }
            } else if (parts.get(0).matches("~[A-Z_]+[:]?")) {
                String key = this.themeKey(parts.get(0));
                String theme = this.colorTheme.get(key);
                if (theme != null) {
                    parts.set(0, "icolor");
                    parts.add(1, theme);
                    this.addHighlightRule(this.syntaxName + idx, parts, true, tokenName);
                } else {
                    Log.warn("Unknown token type: ", key);
                }
            } else {
                out = false;
            }
            return out;
        }

        private String themeKey(String key) {
            int keyEnd;
            if (key.startsWith("+")) {
                return key;
            }
            int n = keyEnd = key.endsWith(":") ? key.length() - 1 : key.length();
            if (key.startsWith("~")) {
                return key.substring(1, keyEnd);
            }
            return key.substring(0, keyEnd);
        }

        public boolean matches() {
            return this.matches;
        }

        public Parser getParser() {
            return this.parser;
        }

        public Map<String, List<HighlightRule>> getHighlightRules() {
            return this.highlightRules;
        }

        public boolean isDefault() {
            return this.syntaxName.equals(DEFAULT_SYNTAX);
        }

        private void addHighlightRule(String reference, List<String> parts, boolean caseInsensitive, String tokenName) {
            HashMap<String, String> spec = new HashMap<String, String>();
            spec.put(reference, parts.get(1));
            Styles.StyleCompiler sh = new Styles.StyleCompiler(spec, true);
            AttributedStyle style = new StyleResolver(sh::getStyle).resolve("." + reference);
            try {
                if (HighlightRule.evalRuleType(parts) == HighlightRule.RuleType.PATTERN) {
                    if (parts.size() == 2) {
                        this.highlightRules.get(tokenName).add(new HighlightRule(style, this.doPattern(".*", caseInsensitive)));
                    } else {
                        for (int i = 2; i < parts.size(); ++i) {
                            this.highlightRules.get(tokenName).add(new HighlightRule(style, this.doPattern(parts.get(i), caseInsensitive)));
                        }
                    }
                } else if (HighlightRule.evalRuleType(parts) == HighlightRule.RuleType.START_END) {
                    String s = parts.get(2);
                    String e = parts.get(3);
                    this.highlightRules.get(tokenName).add(new HighlightRule(style, this.doPattern(s.substring(7, s.length() - 1), caseInsensitive), this.doPattern(e.substring(5, e.length() - 1), caseInsensitive)));
                } else if (HighlightRule.evalRuleType(parts) == HighlightRule.RuleType.PARSER_START_WITH) {
                    this.highlightRules.get(tokenName).add(new HighlightRule(HighlightRule.RuleType.PARSER_START_WITH, style, parts.get(2).substring(10)));
                } else if (HighlightRule.evalRuleType(parts) == HighlightRule.RuleType.PARSER_CONTINUE_AS) {
                    this.highlightRules.get(tokenName).add(new HighlightRule(HighlightRule.RuleType.PARSER_CONTINUE_AS, style, parts.get(2).substring(11)));
                }
            }
            catch (PatternSyntaxException e) {
                Log.warn("Invalid highlight regex", reference, parts, e);
            }
            catch (Exception e) {
                Log.warn("Failure while handling highlight regex", reference, parts, e);
            }
        }

        private Pattern doPattern(String regex, boolean caseInsensitive) {
            regex = Parser.fixRegexes(regex);
            return caseInsensitive ? Pattern.compile(regex, 2) : Pattern.compile(regex);
        }
    }

    static class Parser {
        private static final char escapeChar = '\\';
        private String blockCommentTokenName;
        private BlockCommentDelimiters blockCommentDelimiters;
        private String lineCommentTokenName;
        private String[] lineCommentDelimiters;
        private String balancedDelimiterTokenName;
        private String[] balancedDelimiters;
        private String balancedDelimiter;
        private List<ParsedToken> tokens;
        private CharSequence startWith;
        private int tokenStart = 0;
        private boolean blockComment;
        private boolean lineComment;
        private boolean balancedQuoted;

        public void setBlockCommentDelimiters(String tokenName, String[] args) {
            try {
                this.blockCommentTokenName = tokenName;
                this.blockCommentDelimiters = new BlockCommentDelimiters(args);
            }
            catch (Exception e) {
                Log.warn(e.getMessage());
            }
        }

        public void setLineCommentDelimiters(String tokenName, String[] args) {
            this.lineCommentTokenName = tokenName;
            this.lineCommentDelimiters = args;
        }

        public void setBalancedDelimiters(String tokenName, String[] args) {
            this.balancedDelimiterTokenName = tokenName;
            this.balancedDelimiters = args;
        }

        public void reset() {
            this.startWith = null;
            this.blockComment = false;
            this.lineComment = false;
            this.balancedQuoted = false;
            this.tokenStart = 0;
        }

        public void parse(CharSequence line) {
            if (line == null) {
                return;
            }
            this.tokens = new ArrayList<ParsedToken>();
            if (this.blockComment || this.balancedQuoted) {
                this.tokenStart = 0;
            }
            for (int i = 0; i < line.length(); ++i) {
                if (this.isEscapeChar(line, i) || this.isEscaped(line, i)) continue;
                if (!(this.blockComment || this.lineComment || this.balancedQuoted)) {
                    if (this.blockCommentDelimiters != null && this.isDelimiter(line, i, this.blockCommentDelimiters.getStart())) {
                        this.blockComment = true;
                        this.tokenStart = i;
                        this.startWith = this.startWithSubstring(line, i);
                        i = i + this.blockCommentDelimiters.getStart().length() - 1;
                        continue;
                    }
                    if (this.isLineCommentDelimiter(line, i)) {
                        this.lineComment = true;
                        this.tokenStart = i;
                        this.startWith = this.startWithSubstring(line, i);
                        break;
                    }
                    this.balancedDelimiter = this.balancedDelimiter(line, i);
                    if (this.balancedDelimiter == null) continue;
                    this.balancedQuoted = true;
                    this.tokenStart = i;
                    this.startWith = this.startWithSubstring(line, i);
                    i = i + this.balancedDelimiter.length() - 1;
                    continue;
                }
                if (this.blockComment) {
                    if (!this.isDelimiter(line, i, this.blockCommentDelimiters.getEnd())) continue;
                    this.blockComment = false;
                    i = i + this.blockCommentDelimiters.getEnd().length() - 1;
                    this.tokens.add(new ParsedToken(this.blockCommentTokenName, this.startWith, this.tokenStart, i + 1));
                    continue;
                }
                if (!this.balancedQuoted || !this.isDelimiter(line, i, this.balancedDelimiter)) continue;
                this.balancedQuoted = false;
                if ((i = i + this.balancedDelimiter.length() - 1) - this.tokenStart + 1 <= 2 * this.balancedDelimiter.length()) continue;
                this.tokens.add(new ParsedToken(this.balancedDelimiterTokenName, this.startWith, this.tokenStart, i + 1));
            }
            if (this.blockComment) {
                this.tokens.add(new ParsedToken(this.blockCommentTokenName, this.startWith, this.tokenStart, line.length()));
            } else if (this.lineComment) {
                this.lineComment = false;
                this.tokens.add(new ParsedToken(this.lineCommentTokenName, this.startWith, this.tokenStart, line.length()));
            } else if (this.balancedQuoted) {
                this.tokens.add(new ParsedToken(this.balancedDelimiterTokenName, this.startWith, this.tokenStart, line.length()));
            }
        }

        private CharSequence startWithSubstring(CharSequence line, int pos) {
            return line.subSequence(pos, Math.min(pos + 5, line.length()));
        }

        public List<ParsedToken> getTokens() {
            return this.tokens;
        }

        private String balancedDelimiter(CharSequence buffer, int pos) {
            if (this.balancedDelimiters != null) {
                for (String delimiter : this.balancedDelimiters) {
                    if (!this.isDelimiter(buffer, pos, delimiter)) continue;
                    return delimiter;
                }
            }
            return null;
        }

        private boolean isDelimiter(CharSequence buffer, int pos, String delimiter) {
            if (pos < 0 || delimiter == null) {
                return false;
            }
            int length = delimiter.length();
            if (length <= buffer.length() - pos) {
                for (int i = 0; i < length; ++i) {
                    if (delimiter.charAt(i) == buffer.charAt(pos + i)) continue;
                    return false;
                }
                return true;
            }
            return false;
        }

        private boolean isLineCommentDelimiter(CharSequence buffer, int pos) {
            if (this.lineCommentDelimiters != null) {
                for (String delimiter : this.lineCommentDelimiters) {
                    if (!this.isDelimiter(buffer, pos, delimiter)) continue;
                    return true;
                }
            }
            return false;
        }

        private boolean isEscapeChar(char ch) {
            return '\\' == ch;
        }

        private boolean isEscapeChar(CharSequence buffer, int pos) {
            if (pos < 0) {
                return false;
            }
            char ch = buffer.charAt(pos);
            return this.isEscapeChar(ch) && !this.isEscaped(buffer, pos);
        }

        private boolean isEscaped(CharSequence buffer, int pos) {
            if (pos <= 0) {
                return false;
            }
            return this.isEscapeChar(buffer, pos - 1);
        }

        static String fixRegexes(String posix) {
            int i;
            int len = posix.length();
            StringBuilder java = new StringBuilder();
            boolean inBracketExpression = false;
            try {
                block7: for (i = 0; i < len; ++i) {
                    char c = posix.charAt(i);
                    switch (c) {
                        case '\\': {
                            char next = posix.charAt(++i);
                            if (inBracketExpression && next == ']') {
                                inBracketExpression = false;
                                java.append("\\\\").append(next);
                                continue block7;
                            }
                            if (next == '<' || next == '>') {
                                next = 'b';
                            }
                            java.append(c).append(next);
                            continue block7;
                        }
                        case '[': {
                            if (i == len - 1) {
                                throw new IllegalArgumentException("Lone [ at the end of (index " + i + "): " + posix);
                            }
                            if (posix.charAt(i + 1) == ':') {
                                int afterClass = Parser.nextAfterClass(posix, i + 2);
                                if (!posix.regionMatches(afterClass, ":]", 0, 2)) {
                                    java.append("[:");
                                    ++i;
                                    inBracketExpression = true;
                                    continue block7;
                                }
                                String className = posix.substring(i + 2, afterClass);
                                java.append(Parser.replaceClass(className));
                                i = afterClass + 1;
                                continue block7;
                            }
                            if (inBracketExpression) {
                                java.append('\\').append(c);
                                continue block7;
                            }
                            inBracketExpression = true;
                            java.append(c);
                            char next = posix.charAt(i + 1);
                            if (next == ']') {
                                ++i;
                                java.append("\\]");
                                continue block7;
                            }
                            if (next != 94 || posix.charAt(i + 2) != ']') continue block7;
                            i += 2;
                            java.append("^\\]");
                            continue block7;
                        }
                        case ']': {
                            inBracketExpression = false;
                            java.append(c);
                            continue block7;
                        }
                        default: {
                            java.append(c);
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Posix-to-Java regex translation failed around index " + i + " of: " + posix, e);
            }
            return java.toString();
        }

        private static String replaceClass(String className) {
            switch (className) {
                case "alnum": {
                    return "\\p{Alnum}";
                }
                case "alpha": {
                    return "\\p{Alpha}";
                }
                case "blank": {
                    return "\\p{Blank}";
                }
                case "cntrl": {
                    return "\\p{Cntrl}";
                }
                case "digit": {
                    return "\\p{Digit}";
                }
                case "graph": {
                    return "\\p{Graph}";
                }
                case "lower": {
                    return "\\p{Lower}";
                }
                case "print": {
                    return "\\p{Print}";
                }
                case "punct": {
                    return "\\p{Punct}";
                }
                case "space": {
                    return "\\s";
                }
                case "upper": {
                    return "\\p{Upper}";
                }
                case "xdigit": {
                    return "\\p{XDigit}";
                }
            }
            throw new IllegalArgumentException("Unknown class '" + className + "'");
        }

        private static int nextAfterClass(String s, int idx) {
            char c;
            if (s.charAt(idx) == ':') {
                ++idx;
            }
            while (Character.isLetterOrDigit(c = s.charAt(idx))) {
                ++idx;
            }
            return idx;
        }
    }

    protected static class RuleSplitter {
        protected RuleSplitter() {
        }

        protected static List<String> split(String s) {
            ArrayList<String> out = new ArrayList<String>();
            if (s.length() == 0) {
                return out;
            }
            boolean depth = false;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); ++i) {
                char c;
                block6: {
                    block4: {
                        int nextChar;
                        block5: {
                            c = s.charAt(i);
                            if (c != '\"') break block4;
                            if (depth) break block5;
                            depth = true;
                            break block6;
                        }
                        int n = nextChar = i < s.length() - 1 ? (int)s.charAt(i + 1) : 32;
                        if (nextChar != 32) break block6;
                        depth = false;
                        break block6;
                    }
                    if (c == ' ' && !depth && sb.length() > 0) {
                        out.add(RuleSplitter.stripQuotes(sb.toString()));
                        sb = new StringBuilder();
                        continue;
                    }
                }
                if (sb.length() <= 0 && (c == ' ' || c == '\t')) continue;
                sb.append(c);
            }
            if (sb.length() > 0) {
                out.add(RuleSplitter.stripQuotes(sb.toString()));
            }
            return out;
        }

        private static String stripQuotes(String s) {
            String out = s.trim();
            if (s.startsWith("\"") && s.endsWith("\"")) {
                out = s.substring(1, s.length() - 1);
            }
            return out;
        }
    }

    private static class PathParts {
        final String staticPrefix;
        final String globPattern;

        PathParts(String staticPrefix, String globPattern) {
            this.staticPrefix = staticPrefix;
            this.globPattern = globPattern;
        }
    }

    private static class ParsedToken {
        private final String name;
        private final CharSequence startWith;
        private final int start;
        private final int end;

        public ParsedToken(String name, CharSequence startWith, int start, int end) {
            this.name = name;
            this.startWith = startWith;
            this.start = start;
            this.end = end;
        }

        public String getName() {
            return this.name;
        }

        public CharSequence getStartWith() {
            return this.startWith;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }
    }

    static class HighlightRule {
        private final RuleType type;
        private Pattern pattern;
        private final AttributedStyle style;
        private Pattern start;
        private Pattern end;
        private String startWith;
        private String continueAs;

        public HighlightRule(AttributedStyle style, Pattern pattern) {
            this.type = RuleType.PATTERN;
            this.pattern = pattern;
            this.style = style;
        }

        public HighlightRule(AttributedStyle style, Pattern start, Pattern end) {
            this.type = RuleType.START_END;
            this.style = style;
            this.start = start;
            this.end = end;
        }

        public HighlightRule(RuleType parserRuleType, AttributedStyle style, String value) {
            this.type = parserRuleType;
            this.style = style;
            this.pattern = Pattern.compile(".*");
            if (parserRuleType == RuleType.PARSER_START_WITH) {
                this.startWith = value;
            } else if (parserRuleType == RuleType.PARSER_CONTINUE_AS) {
                this.continueAs = value;
            } else {
                throw new IllegalArgumentException("Bad RuleType: " + (Object)((Object)parserRuleType));
            }
        }

        public RuleType getType() {
            return this.type;
        }

        public AttributedStyle getStyle() {
            return this.style;
        }

        public Pattern getPattern() {
            if (this.type == RuleType.START_END) {
                throw new IllegalAccessError();
            }
            return this.pattern;
        }

        public Pattern getStart() {
            if (this.type == RuleType.PATTERN) {
                throw new IllegalAccessError();
            }
            return this.start;
        }

        public Pattern getEnd() {
            if (this.type == RuleType.PATTERN) {
                throw new IllegalAccessError();
            }
            return this.end;
        }

        public String getStartWith() {
            return this.startWith;
        }

        public String getContinueAs() {
            return this.continueAs;
        }

        public static RuleType evalRuleType(List<String> colorCfg) {
            RuleType out = null;
            if (colorCfg.get(0).equals("color") || colorCfg.get(0).equals("icolor")) {
                out = RuleType.PATTERN;
                if (colorCfg.size() == 3) {
                    if (colorCfg.get(2).startsWith("startWith=")) {
                        out = RuleType.PARSER_START_WITH;
                    } else if (colorCfg.get(2).startsWith("continueAs=")) {
                        out = RuleType.PARSER_CONTINUE_AS;
                    }
                } else if (colorCfg.size() == 4 && colorCfg.get(2).startsWith("start=") && colorCfg.get(3).startsWith("end=")) {
                    out = RuleType.START_END;
                }
            }
            return out;
        }

        public String toString() {
            return "{type:" + (Object)((Object)this.type) + ", startWith: " + this.startWith + ", continueAs: " + this.continueAs + ", start: " + this.start + ", end: " + this.end + ", pattern: " + this.pattern + "}";
        }

        public static enum RuleType {
            PATTERN,
            START_END,
            PARSER_START_WITH,
            PARSER_CONTINUE_AS;

        }
    }

    private static class BlockCommentDelimiters {
        private final String start;
        private final String end;

        public BlockCommentDelimiters(String[] args) {
            if (args.length != 2 || args[0] == null || args[1] == null || args[0].isEmpty() || args[1].isEmpty() || args[0].equals(args[1])) {
                throw new IllegalArgumentException("Bad block comment delimiters!");
            }
            this.start = args[0];
            this.end = args[1];
        }

        public String getStart() {
            return this.start;
        }

        public String getEnd() {
            return this.end;
        }
    }
}

