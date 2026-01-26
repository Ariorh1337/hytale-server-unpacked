/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.builtins.Less;
import org.jline.builtins.Nano;
import org.jline.builtins.Options;
import org.jline.builtins.Source;
import org.jline.builtins.TTop;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.OSUtils;
import org.jline.utils.StyleResolver;

public class PosixCommands {
    public static final String DEFAULT_LS_COLORS = "dr=1;91:ex=1;92:sl=1;96:ot=34;43";
    public static final String DEFAULT_GREP_COLORS = "mt=1;31:fn=35:ln=32:se=36";
    private static final LinkOption[] NO_FOLLOW_OPTIONS = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
    private static final List<String> WINDOWS_EXECUTABLE_EXTENSIONS = Collections.unmodifiableList(Arrays.asList(".bat", ".exe", ".cmd"));
    private static final LinkOption[] EMPTY_LINK_OPTIONS = new LinkOption[0];

    public static void cd(Context context, String[] argv) throws Exception {
        PosixCommands.cd(context, argv, null);
    }

    public static void cd(Context context, String[] argv, Consumer<Path> directoryChanger) throws Exception {
        String target;
        String home;
        String[] usage = new String[]{"cd - change directory", "Usage: cd [OPTIONS] [DIRECTORY]", "  -? --help                show help", "  -P                       use physical directory structure", "  -L                       follow symbolic links (default)"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        if (opt.args().size() != 1) {
            throw new IllegalArgumentException("usage: cd DIRECTORY");
        }
        Path cwd = context.currentDir();
        Path newDir = opt.args().isEmpty() ? ((home = System.getProperty("user.home")) != null ? Paths.get(home, new String[0]) : cwd) : ("-".equals(target = opt.args().get(0)) ? cwd : cwd.resolve(target));
        newDir = opt.isSet("P") ? newDir.toRealPath(new LinkOption[0]) : newDir.toAbsolutePath().normalize();
        if (!Files.exists(newDir, new LinkOption[0])) {
            throw new IOException("cd: no such file or directory: " + opt.args().get(0));
        }
        if (!Files.isDirectory(newDir, new LinkOption[0])) {
            throw new IOException("cd: not a directory: " + opt.args().get(0));
        }
        if (directoryChanger != null) {
            directoryChanger.accept(newDir);
        }
    }

    public static void pwd(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"pwd - print working directory", "Usage: pwd [OPTIONS]", "  -? --help                show help"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        if (!opt.args().isEmpty()) {
            throw new IllegalArgumentException("usage: pwd");
        }
        context.out().println(context.currentDir());
    }

    public static void echo(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"echo - display text", "Usage: echo [OPTIONS] [ARGUMENTS]", "  -? --help                show help", "  -n                       no trailing new line"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        List<String> args = opt.args();
        StringBuilder buf = new StringBuilder();
        if (args != null) {
            for (String arg : args) {
                if (buf.length() > 0) {
                    buf.append(' ');
                }
                for (int i = 0; i < arg.length(); ++i) {
                    int c = arg.charAt(i);
                    if (c == 92) {
                        c = i < arg.length() - 1 ? (int)arg.charAt(++i) : 92;
                        switch (c) {
                            case 97: {
                                buf.append('\u0007');
                                break;
                            }
                            case 110: {
                                buf.append('\n');
                                break;
                            }
                            case 116: {
                                buf.append('\t');
                                break;
                            }
                            case 114: {
                                buf.append('\r');
                                break;
                            }
                            case 92: {
                                buf.append('\\');
                                break;
                            }
                            case 48: 
                            case 49: 
                            case 50: 
                            case 51: 
                            case 52: 
                            case 53: 
                            case 54: 
                            case 55: 
                            case 56: 
                            case 57: {
                                int j;
                                int ch = 0;
                                for (j = 0; j < 3; ++j) {
                                    int n = c = i < arg.length() - 1 ? (int)arg.charAt(++i) : -1;
                                    if (c < 0) continue;
                                    ch = ch * 8 + (c - 48);
                                }
                                buf.append((char)ch);
                                break;
                            }
                            case 117: {
                                int j;
                                int ch = 0;
                                for (j = 0; j < 4; ++j) {
                                    int n = c = i < arg.length() - 1 ? (int)arg.charAt(++i) : -1;
                                    if (c < 0) continue;
                                    if (c >= 65 && c <= 90) {
                                        ch = ch * 16 + (c - 65 + 10);
                                        continue;
                                    }
                                    if (c >= 97 && c <= 122) {
                                        ch = ch * 16 + (c - 97 + 10);
                                        continue;
                                    }
                                    if (c < 48 || c > 57) break;
                                    ch = ch * 16 + (c - 48);
                                }
                                buf.append((char)ch);
                                break;
                            }
                            default: {
                                buf.append((char)c);
                                break;
                            }
                        }
                        continue;
                    }
                    buf.append((char)c);
                }
            }
        }
        if (opt.isSet("n")) {
            context.out().print(buf);
        } else {
            context.out().println(buf);
        }
    }

    public static void echo(Context context, Object[] argv) throws Exception {
        String[] stringArgv = new String[argv.length];
        for (int i = 0; i < argv.length; ++i) {
            stringArgv[i] = argv[i] != null ? argv[i].toString() : "";
        }
        PosixCommands.echo(context, stringArgv);
    }

    public static void cat(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"cat - concatenate and print FILES", "Usage: cat [OPTIONS] [FILES]", "  -? --help                show help", "  -n                       number the output lines, starting at 1"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        List<String> args = opt.args();
        if (args.isEmpty()) {
            args = Collections.singletonList("-");
        }
        Path cwd = context.currentDir();
        for (String arg : args) {
            InputStream is = "-".equals(arg) ? context.in() : cwd.toUri().resolve(arg).toURL().openStream();
            PosixCommands.cat(context, new BufferedReader(new InputStreamReader(is)), opt.isSet("n"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void cat(Context context, BufferedReader reader, boolean numbered) throws IOException {
        int lineno = 1;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (numbered) {
                    context.out().printf("%6d\t%s%n", lineno++, line);
                    continue;
                }
                context.out().println(line);
            }
        }
        finally {
            reader.close();
        }
    }

    public static void date(Context context, String[] argv) throws Exception {
        String arg;
        List<String> args;
        String[] usage = new String[]{"date - display date", "Usage: date [OPTIONS] [+FORMAT]", "  -? --help                    Show help", "  -u --utc                     Use UTC timezone", "  -r --reference=SECONDS       Print the date represented by 'seconds' since January 1, 1970", "  -d --date=STRING             Display time described by STRING", "  -f --file=DATEFILE           Like --date once for each line of DATEFILE", "  -I --iso-8601[=TIMESPEC]     Output date/time in ISO 8601 format", "  -R --rfc-2822                Output date and time in RFC 2822 format", "     --rfc-3339=TIMESPEC       Output date and time in RFC 3339 format"};
        Options opt = Options.compile(usage).parse(argv);
        if (opt.isSet("help")) {
            throw new Options.HelpException(opt.usage());
        }
        Date input = new Date();
        String output = null;
        boolean useUtc = opt.isSet("utc");
        if (opt.isSet("reference")) {
            long seconds = Long.parseLong(opt.get("reference"));
            input = new Date(seconds * 1000L);
        }
        if (opt.isSet("date")) {
            String dateStr = opt.get("date");
            try {
                SimpleDateFormat[] formats = new SimpleDateFormat[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd"), new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("dd-MM-yyyy"), new SimpleDateFormat("yyyy/MM/dd")};
                boolean parsed = false;
                for (SimpleDateFormat format : formats) {
                    try {
                        input = format.parse(dateStr);
                        parsed = true;
                        break;
                    }
                    catch (Exception exception) {
                    }
                }
                if (!parsed) {
                    throw new IllegalArgumentException("Unable to parse date: " + dateStr);
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Invalid date string: " + dateStr);
            }
        }
        if (opt.isSet("iso-8601")) {
            String timespec = opt.get("iso-8601");
            if (timespec == null || "date".equals(timespec)) {
                output = "%Y-%m-%d";
            } else if ("hours".equals(timespec)) {
                output = "%Y-%m-%dT%H%z";
            } else if ("minutes".equals(timespec)) {
                output = "%Y-%m-%dT%H:%M%z";
            } else if ("seconds".equals(timespec)) {
                output = "%Y-%m-%dT%H:%M:%S%z";
            } else if ("ns".equals(timespec)) {
                output = "%Y-%m-%dT%H:%M:%S,%N%z";
            }
        }
        if (opt.isSet("rfc-2822")) {
            output = "%a, %d %b %Y %H:%M:%S %z";
        }
        if (opt.isSet("rfc-3339")) {
            String timespec = opt.get("rfc-3339");
            if ("date".equals(timespec)) {
                output = "%Y-%m-%d";
            } else if ("seconds".equals(timespec)) {
                output = "%Y-%m-%d %H:%M:%S%z";
            } else if ("ns".equals(timespec)) {
                output = "%Y-%m-%d %H:%M:%S.%N%z";
            }
        }
        if (!(args = opt.args()).isEmpty() && (arg = args.get(0)).startsWith("+")) {
            output = arg.substring(1);
        }
        if (output == null) {
            output = "%c";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(PosixCommands.toJavaDateFormat(output));
        if (useUtc) {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        context.out().println(formatter.format(input));
    }

    private static String toJavaDateFormat(String format) {
        StringBuilder sb = new StringBuilder();
        boolean quote = false;
        for (int i = 0; i < format.length(); ++i) {
            char c = format.charAt(i);
            if (c == '%') {
                if (i + 1 < format.length()) {
                    if (quote) {
                        sb.append('\'');
                        quote = false;
                    }
                    c = format.charAt(++i);
                    switch (c) {
                        case '+': 
                        case 'A': {
                            sb.append("MMM EEE d HH:mm:ss yyyy");
                            break;
                        }
                        case 'a': {
                            sb.append("EEE");
                            break;
                        }
                        case 'B': {
                            sb.append("MMMMMMM");
                            break;
                        }
                        case 'b': {
                            sb.append("MMM");
                            break;
                        }
                        case 'C': {
                            sb.append("yy");
                            break;
                        }
                        case 'c': {
                            sb.append("MMM EEE d HH:mm:ss yyyy");
                            break;
                        }
                        case 'D': {
                            sb.append("MM/dd/yy");
                            break;
                        }
                        case 'd': {
                            sb.append("dd");
                            break;
                        }
                        case 'e': {
                            sb.append("dd");
                            break;
                        }
                        case 'F': {
                            sb.append("yyyy-MM-dd");
                            break;
                        }
                        case 'G': {
                            sb.append("YYYY");
                            break;
                        }
                        case 'g': {
                            sb.append("YY");
                            break;
                        }
                        case 'H': {
                            sb.append("HH");
                            break;
                        }
                        case 'h': {
                            sb.append("MMM");
                            break;
                        }
                        case 'I': {
                            sb.append("hh");
                            break;
                        }
                        case 'j': {
                            sb.append("DDD");
                            break;
                        }
                        case 'k': {
                            sb.append("HH");
                            break;
                        }
                        case 'l': {
                            sb.append("hh");
                            break;
                        }
                        case 'M': {
                            sb.append("mm");
                            break;
                        }
                        case 'm': {
                            sb.append("MM");
                            break;
                        }
                        case 'N': {
                            sb.append("S");
                            break;
                        }
                        case 'n': {
                            sb.append("\n");
                            break;
                        }
                        case 'P': {
                            sb.append("aa");
                            break;
                        }
                        case 'p': {
                            sb.append("aa");
                            break;
                        }
                        case 'r': {
                            sb.append("hh:mm:ss aa");
                            break;
                        }
                        case 'R': {
                            sb.append("HH:mm");
                            break;
                        }
                        case 'S': {
                            sb.append("ss");
                            break;
                        }
                        case 's': {
                            sb.append("S");
                            break;
                        }
                        case 'T': {
                            sb.append("HH:mm:ss");
                            break;
                        }
                        case 't': {
                            sb.append("\t");
                            break;
                        }
                        case 'U': {
                            sb.append("w");
                            break;
                        }
                        case 'u': {
                            sb.append("u");
                            break;
                        }
                        case 'V': {
                            sb.append("W");
                            break;
                        }
                        case 'v': {
                            sb.append("dd-MMM-yyyy");
                            break;
                        }
                        case 'W': {
                            sb.append("w");
                            break;
                        }
                        case 'w': {
                            sb.append("u");
                            break;
                        }
                        case 'X': {
                            sb.append("HH:mm:ss");
                            break;
                        }
                        case 'x': {
                            sb.append("MM/dd/yy");
                            break;
                        }
                        case 'Y': {
                            sb.append("yyyy");
                            break;
                        }
                        case 'y': {
                            sb.append("yy");
                            break;
                        }
                        case 'Z': {
                            sb.append("z");
                            break;
                        }
                        case 'z': {
                            sb.append("X");
                            break;
                        }
                        case '%': {
                            sb.append("%");
                        }
                    }
                    continue;
                }
                if (!quote) {
                    sb.append('\'');
                }
                sb.append(c);
                sb.append('\'');
                continue;
            }
            if ((c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') && !quote) {
                sb.append('\'');
                quote = true;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static void sleep(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"sleep - suspend execution for an interval of time", "Usage: sleep seconds", "  -? --help                    show help"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        List<String> args = opt.args();
        if (args.size() != 1) {
            throw new IllegalArgumentException("usage: sleep seconds");
        }
        int s = Integer.parseInt(args.get(0));
        Thread.sleep((long)s * 1000L);
    }

    public static void watch(Context context, String[] argv) throws Exception {
        PosixCommands.watch(context, argv, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void watch(Context context, String[] argv, CommandExecutor executor) throws Exception {
        String[] usage = new String[]{"watch - watches & refreshes the output of a command", "Usage: watch [OPTIONS] COMMAND", "  -? --help                    Show help", "  -n --interval=SECONDS        Interval between executions of the command in seconds", "  -a --append                  The output should be appended but not clear the console"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        List<String> args = opt.args();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("usage: watch COMMAND");
        }
        int intervalValue = 1;
        if (opt.isSet("interval") && (intervalValue = opt.getNumber("interval")) < 1) {
            intervalValue = 1;
        }
        int interval = intervalValue;
        boolean append = opt.isSet("append");
        String command = String.join((CharSequence)" ", args);
        ArrayList<String> finalArgs = new ArrayList<String>(args);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            Runnable task = () -> {
                try {
                    if (!append && context.isTty()) {
                        context.terminal().puts(InfoCmp.Capability.clear_screen, new Object[0]);
                        context.terminal().flush();
                    } else if (!append) {
                        context.out().println();
                    }
                    context.out().println("Every " + interval + "s: " + command + "    " + LocalDateTime.now());
                    context.out().println();
                    if (executor != null) {
                        try {
                            String output = executor.execute(finalArgs);
                            context.out().print(output);
                        }
                        catch (Exception e) {
                            context.err().println("Command execution failed: " + e.getMessage());
                        }
                    } else {
                        try {
                            Process process = new ProcessBuilder(finalArgs).start();
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    context.out().println(line);
                                }
                            }
                            process.waitFor();
                        }
                        catch (Exception e) {
                            context.out().println("Command: " + command);
                            context.out().println("(Command execution requires shell integration - use gogo implementation for full functionality)");
                        }
                    }
                    context.out().flush();
                }
                catch (Exception e) {
                    context.err().println("Error executing command: " + e.getMessage());
                }
            };
            executorService.scheduleAtFixedRate(task, 0L, interval, TimeUnit.SECONDS);
            if (context.isTty()) {
                context.out().println("Press any key to stop...");
                context.in().read();
            } else {
                Thread.sleep(10000L);
            }
        }
        finally {
            executorService.shutdownNow();
        }
    }

    public static void ttop(Context context, String[] argv) throws Exception {
        TTop.ttop(context.terminal(), context.out(), context.err(), argv);
    }

    public static void nano(Context context, String[] argv) throws Exception {
        Options opt = PosixCommands.parseOptions(context, Nano.usage(), argv);
        Nano nano = new Nano(context.terminal(), context.currentDir(), opt);
        nano.open(opt.args());
        nano.run();
    }

    public static void less(Context context, String[] argv) throws Exception {
        Options opt = PosixCommands.parseOptions(context, Less.usage(), argv);
        ArrayList<Source> sources = new ArrayList<Source>();
        if (opt.args().isEmpty()) {
            opt.args().add("-");
        }
        for (String arg : opt.args()) {
            if ("-".equals(arg)) {
                sources.add(new Source.StdInSource(context.in()));
                continue;
            }
            if (arg.contains("*") || arg.contains("?")) {
                PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + arg);
                Stream<Path> pathStream = Files.walk(context.currentDir(), new FileVisitOption[0]);
                try {
                    pathStream.filter(pathMatcher::matches).forEach(p -> {
                        try {
                            sources.add(new Source.URLSource(p.toUri().toURL(), p.toString()));
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    continue;
                }
                finally {
                    if (pathStream != null) {
                        pathStream.close();
                    }
                    continue;
                }
            }
            try {
                Path path = context.currentDir().resolve(arg);
                sources.add(new Source.URLSource(path.toUri().toURL(), arg));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!context.isTty()) {
            for (Source source : sources) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(source.read()));){
                    String line;
                    while ((line = reader.readLine()) != null) {
                        context.out().println(line);
                    }
                }
            }
            return;
        }
        Less less = new Less(context.terminal(), context.currentDir(), opt);
        less.run(sources);
    }

    public static void clear(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"clear - clear screen", "Usage: clear [OPTIONS]", "  -? --help                    Show help"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        if (context.isTty()) {
            context.terminal().puts(InfoCmp.Capability.clear_screen, new Object[0]);
            context.terminal().flush();
        }
    }

    public static void wc(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"wc - word, line, character, and byte count", "Usage: wc [OPTIONS] [FILES]", "  -? --help                    Show help", "  -l --lines                   Print line counts", "  -c --bytes                   Print byte counts", "  -m --chars                   Print character counts", "  -w --words                   Print word counts"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        ArrayList<Source> sources = new ArrayList<Source>();
        if (opt.args().isEmpty()) {
            opt.args().add("-");
        }
        for (String arg : opt.args()) {
            if ("-".equals(arg)) {
                sources.add(new Source.StdInSource(context.in()));
                continue;
            }
            sources.add(new Source.URLSource(context.currentDir().resolve(arg).toUri().toURL(), arg));
        }
        boolean showLines = opt.isSet("lines");
        boolean showWords = opt.isSet("words");
        boolean showChars = opt.isSet("chars");
        boolean showBytes = opt.isSet("bytes");
        if (!(showLines || showWords || showChars || showBytes)) {
            showBytes = true;
            showWords = true;
            showLines = true;
        }
        long totalLines = 0L;
        long totalWords = 0L;
        long totalChars = 0L;
        long totalBytes = 0L;
        for (Source source : sources) {
            long lines = 0L;
            long words = 0L;
            long chars = 0L;
            long bytes = 0L;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(source.read()));){
                String line;
                while ((line = reader.readLine()) != null) {
                    ++lines;
                    chars += (long)(line.length() + 1);
                    bytes += (long)(line.getBytes().length + 1);
                    String[] wordArray = line.trim().split("\\s+");
                    if (wordArray.length == 1 && wordArray[0].isEmpty()) continue;
                    words += (long)wordArray.length;
                }
            }
            totalLines += lines;
            totalWords += words;
            totalChars += chars;
            totalBytes += bytes;
            StringBuilder result = new StringBuilder();
            if (showLines) {
                result.append(String.format("%8d", lines));
            }
            if (showWords) {
                result.append(String.format("%8d", words));
            }
            if (showChars) {
                result.append(String.format("%8d", chars));
            }
            if (showBytes) {
                result.append(String.format("%8d", bytes));
            }
            result.append(" ").append(source.getName());
            context.out().println(result);
        }
        if (sources.size() > 1) {
            StringBuilder result = new StringBuilder();
            if (showLines) {
                result.append(String.format("%8d", totalLines));
            }
            if (showWords) {
                result.append(String.format("%8d", totalWords));
            }
            if (showChars) {
                result.append(String.format("%8d", totalChars));
            }
            if (showBytes) {
                result.append(String.format("%8d", totalBytes));
            }
            result.append(" total");
            context.out().println(result);
        }
    }

    public static void head(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"head - display first lines of files", "Usage: head [-n lines | -c bytes] [file ...]", "  -? --help                    Show help", "  -n --lines=LINES             Print line counts", "  -c --bytes=BYTES             Print byte counts"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        if (opt.isSet("lines") && opt.isSet("bytes")) {
            throw new IllegalArgumentException("usage: head [-n # | -c #] [file ...]");
        }
        int nbLines = Integer.MAX_VALUE;
        int nbBytes = Integer.MAX_VALUE;
        if (opt.isSet("lines")) {
            nbLines = opt.getNumber("lines");
        } else if (opt.isSet("bytes")) {
            nbBytes = opt.getNumber("bytes");
        } else {
            nbLines = 10;
        }
        List<String> args = opt.args();
        if (args.isEmpty()) {
            args = Collections.singletonList("-");
        }
        boolean first = true;
        for (String arg : args) {
            if (!first && args.size() > 1) {
                context.out().println();
            }
            if (args.size() > 1) {
                context.out().println("==> " + arg + " <==");
            }
            InputStream is = "-".equals(arg) ? context.in() : context.currentDir().resolve(arg).toUri().toURL().openStream();
            if (nbLines != Integer.MAX_VALUE) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));){
                    String line;
                    for (int count = 0; (line = reader.readLine()) != null && count < nbLines; ++count) {
                        context.out().println(line);
                    }
                }
            } else {
                byte[] buffer = new byte[nbBytes];
                int bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    context.out().write(buffer, 0, bytesRead);
                }
                is.close();
            }
            first = false;
        }
    }

    public static void tail(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"tail - display last lines of files", "Usage: tail [-f] [-q] [-c # | -n #] [file ...]", "  -? --help                    Show help", "  -f --follow                  Do not stop at end of file", "  -F --FOLLOW                  Follow and check for file renaming or rotation", "  -n --lines=LINES             Number of lines to print", "  -c --bytes=BYTES             Number of bytes to print"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        if (opt.isSet("lines") && opt.isSet("bytes")) {
            throw new IllegalArgumentException("usage: tail [-f] [-q] [-c # | -n #] [file ...]");
        }
        int lines = opt.isSet("lines") ? opt.getNumber("lines") : 10;
        int bytes = opt.isSet("bytes") ? opt.getNumber("bytes") : -1;
        boolean follow = opt.isSet("follow") || opt.isSet("FOLLOW");
        List<String> args = opt.args();
        if (args.isEmpty()) {
            args = Collections.singletonList("-");
        }
        for (String arg : args) {
            if (args.size() > 1) {
                context.out().println("==> " + arg + " <==");
            }
            if ("-".equals(arg)) {
                PosixCommands.tailInputStream(context, context.in(), lines, bytes);
                continue;
            }
            Path path = context.currentDir().resolve(arg);
            if (bytes > 0) {
                PosixCommands.tailFileBytes(context, path, bytes, follow);
                continue;
            }
            PosixCommands.tailFileLines(context, path, lines, follow);
        }
    }

    private static void tailInputStream(Context context, InputStream is, int lines, int bytes) throws IOException {
        if (bytes > 0) {
            int n;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            while ((n = is.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            byte[] data = baos.toByteArray();
            int start = Math.max(0, data.length - bytes);
            context.out().write(data, start, data.length - start);
        } else {
            int start;
            ArrayList<String> allLines = new ArrayList<String>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));){
                String line;
                while ((line = reader.readLine()) != null) {
                    allLines.add(line);
                }
            }
            for (int i = start = Math.max(0, allLines.size() - lines); i < allLines.size(); ++i) {
                context.out().println((String)allLines.get(i));
            }
        }
    }

    private static void tailFileLines(Context context, Path path, int lines, boolean follow) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            context.err().println("tail: " + path + ": No such file or directory");
            return;
        }
        ArrayList<String> lastLines = new ArrayList<String>();
        try (BufferedReader reader = Files.newBufferedReader(path);){
            String line;
            while ((line = reader.readLine()) != null) {
                lastLines.add(line);
                if (lastLines.size() <= lines) continue;
                lastLines.remove(0);
            }
        }
        for (String line : lastLines) {
            context.out().println(line);
        }
        if (follow) {
            context.err().println("tail: follow mode not yet implemented");
        }
    }

    private static void tailFileBytes(Context context, Path path, int bytes, boolean follow) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            context.err().println("tail: " + path + ": No such file or directory");
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r");){
            int n;
            long fileLength = raf.length();
            long start = Math.max(0L, fileLength - (long)bytes);
            raf.seek(start);
            byte[] buffer = new byte[8192];
            while ((n = raf.read(buffer)) != -1) {
                context.out().write(buffer, 0, n);
            }
        }
        if (follow) {
            context.err().println("tail: follow mode not yet implemented");
        }
    }

    public static void grep(Context context, String[] argv) throws Exception {
        Map<String, String> colors;
        boolean colored;
        String color;
        String lineFmt;
        Pattern p2;
        Pattern p;
        String regex;
        String[] usage = new String[]{"grep -  search for PATTERN in each FILE or standard input.", "Usage: grep [OPTIONS] PATTERN [FILES]", "  -? --help                Show help", "  -i --ignore-case         Ignore case distinctions", "  -n --line-number         Prefix each line with line number within its input file", "  -q --quiet, --silent     Suppress all normal output", "  -v --invert-match        Select non-matching lines", "  -w --word-regexp         Select only whole words", "  -x --line-regexp         Select only whole lines", "  -c --count               Only print a count of matching lines per file", "     --color=WHEN          Use markers to distinguish the matching string, may be `always', `never' or `auto'", "  -B --before-context=NUM  Print NUM lines of leading context before matching lines", "  -A --after-context=NUM   Print NUM lines of trailing context after matching lines", "  -C --context=NUM         Print NUM lines of output context", "     --pad-lines           Pad line numbers"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        Map<String, String> colorMap = PosixCommands.getColorMap(context, "GREP", DEFAULT_GREP_COLORS);
        List<String> args = opt.args();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("no pattern supplied");
        }
        String regexp = regex = args.remove(0);
        if (opt.isSet("word-regexp")) {
            regexp = "\\b" + regexp + "\\b";
        }
        regexp = opt.isSet("line-regexp") ? "^" + regexp + "$" : ".*" + regexp + ".*";
        if (opt.isSet("ignore-case")) {
            p = Pattern.compile(regexp, 2);
            p2 = Pattern.compile(regex, 2);
        } else {
            p = Pattern.compile(regexp);
            p2 = Pattern.compile(regex);
        }
        int after = opt.isSet("after-context") ? opt.getNumber("after-context") : -1;
        int before = opt.isSet("before-context") ? opt.getNumber("before-context") : -1;
        int contextLines = opt.isSet("context") ? opt.getNumber("context") : 0;
        String string = lineFmt = opt.isSet("pad-lines") ? "%6d" : "%d";
        if (after < 0) {
            after = contextLines;
        }
        if (before < 0) {
            before = contextLines;
        }
        boolean count = opt.isSet("count");
        boolean quiet = opt.isSet("quiet");
        boolean invert = opt.isSet("invert-match");
        boolean lineNumber = opt.isSet("line-number");
        switch (color = opt.isSet("color") ? opt.get("color") : "auto") {
            case "always": 
            case "yes": 
            case "force": {
                colored = true;
                break;
            }
            case "never": 
            case "no": 
            case "none": {
                colored = false;
                break;
            }
            case "auto": 
            case "tty": 
            case "if-tty": {
                colored = context.isTty();
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid argument '" + color + "' for '--color'");
            }
        }
        Map<String, String> map = colored ? (colorMap != null ? colorMap : PosixCommands.getColorMap(DEFAULT_GREP_COLORS)) : (colors = Collections.emptyMap());
        if (args.isEmpty()) {
            args.add("-");
        }
        ArrayList<GrepSource> sources = new ArrayList<GrepSource>();
        for (String arg : args) {
            if ("-".equals(arg)) {
                sources.add(new GrepSource(context.in(), "(standard input)"));
                continue;
            }
            Path path = context.currentDir().resolve(arg);
            sources.add(new GrepSource(path, arg));
        }
        boolean match = false;
        for (GrepSource src : sources) {
            ArrayList<String> lines = new ArrayList<String>();
            boolean firstPrint = true;
            int nb = 0;
            InputStream is = src.getInputStream();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(is));){
                String line;
                int lineno = 1;
                int lineMatch = 0;
                while ((line = r.readLine()) != null) {
                    boolean matches = p.matcher(line).matches();
                    if (invert) {
                        matches = !matches;
                    }
                    AttributedStringBuilder sbl = new AttributedStringBuilder();
                    if (matches) {
                        ++nb;
                        if (!count && !quiet) {
                            if (sources.size() > 1) {
                                if (colored) {
                                    PosixCommands.applyStyle(sbl, colors, "fn");
                                }
                                sbl.append(src.getName());
                                if (colored) {
                                    PosixCommands.applyStyle(sbl, colors, "se");
                                }
                                sbl.append(":");
                            }
                            if (lineNumber) {
                                if (colored) {
                                    PosixCommands.applyStyle(sbl, colors, "ln");
                                }
                                sbl.append(String.format(lineFmt, lineno));
                                if (colored) {
                                    PosixCommands.applyStyle(sbl, colors, "se");
                                }
                                sbl.append(":");
                            }
                            if (colored) {
                                Matcher matcher2 = p2.matcher(line);
                                int cur = 0;
                                while (matcher2.find()) {
                                    sbl.append(line, cur, matcher2.start());
                                    PosixCommands.applyStyle(sbl, colors, "ms");
                                    sbl.append(line, matcher2.start(), matcher2.end());
                                    PosixCommands.applyStyle(sbl, colors, "se");
                                    cur = matcher2.end();
                                }
                                sbl.append(line, cur, line.length());
                            } else {
                                sbl.append(line);
                            }
                            lineMatch = before + 1;
                        }
                    } else if (lineMatch > 0) {
                        --lineMatch;
                        if (sources.size() > 1) {
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "fn");
                            }
                            sbl.append(src.getName());
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "se");
                            }
                            sbl.append("-");
                        }
                        if (lineNumber) {
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "ln");
                            }
                            sbl.append(String.format(lineFmt, lineno));
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "se");
                            }
                            sbl.append("-");
                        }
                        sbl.append(line);
                    } else {
                        if (sources.size() > 1) {
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "fn");
                            }
                            sbl.append(src.getName());
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "se");
                            }
                            sbl.append("-");
                        }
                        if (lineNumber) {
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "ln");
                            }
                            sbl.append(String.format(lineFmt, lineno));
                            if (colored) {
                                PosixCommands.applyStyle(sbl, colors, "se");
                            }
                            sbl.append("-");
                        }
                        sbl.append(line);
                        while (lines.size() > before) {
                            lines.remove(0);
                        }
                        lineMatch = 0;
                    }
                    lines.add(sbl.toAnsi(context.terminal()));
                    while (lineMatch == 0 && lines.size() > before) {
                        lines.remove(0);
                    }
                    ++lineno;
                }
                if (!count && lineMatch > 0) {
                    if (!firstPrint && before + after > 0) {
                        AttributedStringBuilder sbl2 = new AttributedStringBuilder();
                        if (colored) {
                            PosixCommands.applyStyle(sbl2, colors, "se");
                        }
                        sbl2.append("--");
                        context.out().println(sbl2.toAnsi(context.terminal()));
                    } else {
                        firstPrint = false;
                    }
                    for (int i = 0; i < lineMatch + after && i < lines.size(); ++i) {
                        context.out().println((String)lines.get(i));
                    }
                }
                if (count) {
                    context.out().println(nb);
                }
                match |= nb > 0;
            }
            finally {
                if (is == null) continue;
                is.close();
            }
        }
    }

    public static void sort(Context context, String[] argv) throws Exception {
        String[] usage = new String[]{"sort -  writes sorted standard input to standard output.", "Usage: sort [OPTIONS] [FILES]", "  -? --help                    show help", "  -f --ignore-case             fold lower case to upper case characters", "  -r --reverse                 reverse the result of comparisons", "  -u --unique                  output only the first of an equal run", "  -t --field-separator=SEP     use SEP instead of non-blank to blank transition", "  -b --ignore-leading-blanks   ignore leading blancks", "     --numeric-sort            compare according to string numerical value", "  -k --key=KEY                 fields to use for sorting separated by whitespaces"};
        Options opt = PosixCommands.parseOptions(context, usage, argv);
        List<String> args = opt.args();
        ArrayList<String> lines = new ArrayList<String>();
        if (!args.isEmpty()) {
            for (String filename : args) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.currentDir().toUri().resolve(filename).toURL().openStream()));){
                    PosixCommands.readLines(reader, lines);
                }
            }
        } else {
            BufferedReader r = new BufferedReader(new InputStreamReader(context.in()));
            PosixCommands.readLines(r, lines);
        }
        String separator = opt.get("field-separator");
        boolean caseInsensitive = opt.isSet("ignore-case");
        boolean reverse = opt.isSet("reverse");
        boolean ignoreBlanks = opt.isSet("ignore-leading-blanks");
        boolean numeric = opt.isSet("numeric-sort");
        boolean unique = opt.isSet("unique");
        List<String> sortFields = opt.getList("key");
        char sep = separator == null || separator.length() == 0 ? (char)'\u0000' : separator.charAt(0);
        lines.sort(new SortComparator(caseInsensitive, reverse, ignoreBlanks, numeric, sep, sortFields));
        String last = null;
        for (String s : lines) {
            if (!unique || last == null || !s.equals(last)) {
                context.out().println(s);
            }
            last = s;
        }
    }

    public static void ls(Context context, String[] argv) throws Exception {
        class PathEntry
        implements Comparable<PathEntry> {
            final Path abs;
            final Path path;
            final Map<String, Object> attributes;

            public PathEntry(Path abs, Path root) {
                this.abs = abs;
                this.path = abs.startsWith(root) ? root.relativize(abs) : abs;
                this.attributes = this.readAttributes(abs);
            }

            @Override
            public int compareTo(PathEntry o) {
                int c = this.doCompare(o);
                return opt.isSet("r") ? -c : c;
            }

            private int doCompare(PathEntry o) {
                if (opt.isSet("f")) {
                    return -1;
                }
                if (opt.isSet("S")) {
                    long s1;
                    long s0 = this.attributes.get("size") != null ? ((Number)this.attributes.get("size")).longValue() : 0L;
                    long l = s1 = o.attributes.get("size") != null ? ((Number)o.attributes.get("size")).longValue() : 0L;
                    return s0 > s1 ? -1 : (s0 < s1 ? 1 : this.path.toString().compareTo(o.path.toString()));
                }
                if (opt.isSet("t")) {
                    long t1;
                    long t0 = this.attributes.get("lastModifiedTime") != null ? ((FileTime)this.attributes.get("lastModifiedTime")).toMillis() : 0L;
                    long l = t1 = o.attributes.get("lastModifiedTime") != null ? ((FileTime)o.attributes.get("lastModifiedTime")).toMillis() : 0L;
                    return t0 > t1 ? -1 : (t0 < t1 ? 1 : this.path.toString().compareTo(o.path.toString()));
                }
                return this.path.toString().compareTo(o.path.toString());
            }

            boolean isNotDirectory() {
                return this.is("isRegularFile") || this.is("isSymbolicLink") || this.is("isOther");
            }

            boolean isDirectory() {
                return this.is("isDirectory");
            }

            private boolean is(String attr) {
                Object d = this.attributes.get(attr);
                return d instanceof Boolean && (Boolean)d != false;
            }

            String display() {
                String suffix;
                String type;
                String link = "";
                if (this.is("isSymbolicLink")) {
                    type = "sl";
                    suffix = "@";
                    try {
                        Path l = Files.readSymbolicLink(this.abs);
                        link = " -> " + l.toString();
                    }
                    catch (IOException l) {}
                } else if (this.is("isDirectory")) {
                    type = "dr";
                    suffix = "/";
                } else if (this.is("isExecutable")) {
                    type = "ex";
                    suffix = "*";
                } else if (this.is("isOther")) {
                    type = "ot";
                    suffix = "";
                } else {
                    type = "";
                    suffix = "";
                }
                boolean addSuffix = opt.isSet("F");
                return PosixCommands.applyStyle(this.path.toString(), (Map<String, String>)colors, type) + (addSuffix ? suffix : "") + link;
            }

            String longDisplay() {
                String lengthString;
                String username = this.attributes.containsKey("owner") ? Objects.toString(this.attributes.get("owner"), null) : "owner";
                if (username.length() > 8) {
                    username = username.substring(0, 8);
                } else {
                    for (int i = username.length(); i < 8; ++i) {
                        username = username + " ";
                    }
                }
                String group = this.attributes.containsKey("group") ? Objects.toString(this.attributes.get("group"), null) : "group";
                if (group.length() > 8) {
                    group = group.substring(0, 8);
                } else {
                    for (int i = group.length(); i < 8; ++i) {
                        group = group + " ";
                    }
                }
                Number length = (Number)this.attributes.get("size");
                if (length == null) {
                    length = 0L;
                }
                if (opt.isSet("h")) {
                    double l = length.longValue();
                    String unit = "B";
                    if (l >= 1000.0) {
                        l /= 1024.0;
                        unit = "K";
                        if (l >= 1000.0) {
                            l /= 1024.0;
                            unit = "M";
                            if (l >= 1000.0) {
                                l /= 1024.0;
                                unit = "T";
                            }
                        }
                    }
                    lengthString = l < 10.0 && length.longValue() > 1000L ? String.format("%.1f", l) + unit : String.format("%3.0f", l) + unit;
                } else {
                    lengthString = String.format("%1$8s", length);
                }
                EnumSet<PosixFilePermission> perms = (EnumSet<PosixFilePermission>)this.attributes.get("permissions");
                if (perms == null) {
                    perms = EnumSet.noneOf(PosixFilePermission.class);
                }
                return (this.is("isDirectory") ? "d" : (this.is("isSymbolicLink") ? "l" : (this.is("isOther") ? "o" : "-"))) + PosixFilePermissions.toString(perms) + " " + String.format("%3s", this.attributes.containsKey("nlink") ? this.attributes.get("nlink").toString() : "1") + " " + username + " " + group + " " + lengthString + " " + this.toString((FileTime)this.attributes.get("lastModifiedTime")) + " " + this.display();
            }

            protected String toString(FileTime time) {
                long millis;
                long l = millis = time != null ? time.toMillis() : -1L;
                if (millis < 0L) {
                    return "------------";
                }
                ZonedDateTime dt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault());
                if (System.currentTimeMillis() - millis < 15811200000L) {
                    return DateTimeFormatter.ofPattern("MMM ppd HH:mm").format(dt);
                }
                return DateTimeFormatter.ofPattern("MMM ppd  yyyy").format(dt);
            }

            protected Map<String, Object> readAttributes(Path path) {
                TreeMap<String, Object> attrs = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
                for (String view : path.getFileSystem().supportedFileAttributeViews()) {
                    try {
                        Map<String, Object> ta = Files.readAttributes(path, view + ":*", PosixCommands.getLinkOptions(opt.isSet("L")));
                        ta.forEach(attrs::putIfAbsent);
                    }
                    catch (IOException iOException) {}
                }
                attrs.computeIfAbsent("isExecutable", s -> Files.isExecutable(path));
                attrs.computeIfAbsent("permissions", s -> PosixCommands.getPermissionsFromFile(path));
                return attrs;
            }
        }
        boolean colored;
        String color;
        String[] usage = new String[]{"ls - list files", "Usage: ls [OPTIONS] [PATTERNS...]", "  -? --help                show help", "  -1                       list one entry per line", "  -C                       multi-column output", "     --color=WHEN          colorize the output, may be `always', `never' or `auto'", "  -a                       list entries starting with .", "  -F                       append file type indicators", "  -m                       comma separated", "  -l                       long listing", "  -S                       sort by size", "  -f                       output is not sorted", "  -r                       reverse sort order", "  -t                       sort by modification time", "  -x                       sort horizontally", "  -L                       list referenced file for links", "  -h                       print sizes in human readable form"};
        final Options opt = PosixCommands.parseOptions(context, usage, argv);
        Map<String, String> colorMap = PosixCommands.getLsColorMap(context);
        switch (color = opt.isSet("color") ? opt.get("color") : "auto") {
            case "always": 
            case "yes": 
            case "force": {
                colored = true;
                break;
            }
            case "never": 
            case "no": 
            case "none": {
                colored = false;
                break;
            }
            case "auto": 
            case "tty": 
            case "if-tty": {
                colored = context.isTty();
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid argument '" + color + "' for '--color'");
            }
        }
        final Map colors = colored ? (colorMap != null ? colorMap : PosixCommands.getLsColorMap(DEFAULT_LS_COLORS)) : Collections.emptyMap();
        Path currentDir = context.currentDir();
        ArrayList<Path> expanded = new ArrayList<Path>();
        if (opt.args().isEmpty()) {
            expanded.add(currentDir);
        } else {
            opt.args().forEach(s -> expanded.add(currentDir.resolve((String)s)));
        }
        boolean listAll = opt.isSet("a");
        Predicate<Path> filter = p -> listAll || p.getFileName().toString().equals(".") || p.getFileName().toString().equals("..") || !p.getFileName().toString().startsWith(".");
        List all = expanded.stream().filter(filter).map(p -> new PathEntry((Path)p, currentDir)).sorted().collect(Collectors.toList());
        List files = all.stream().filter(PathEntry::isNotDirectory).collect(Collectors.toList());
        PrintStream out = context.out();
        Consumer<Stream> display = s -> {
            boolean optLine = opt.isSet("1");
            boolean optComma = opt.isSet("m");
            boolean optLong = opt.isSet("l");
            boolean optCol = opt.isSet("C");
            if (!(optLine || optComma || optLong || optCol)) {
                if (context.isTty()) {
                    optCol = true;
                } else {
                    optLine = true;
                }
            }
            if (optLine) {
                s.map(PathEntry::display).forEach(out::println);
            } else if (optComma) {
                out.println(s.map(PathEntry::display).collect(Collectors.joining(", ")));
            } else if (optLong) {
                s.map(PathEntry::longDisplay).forEach(out::println);
            } else if (optCol) {
                PosixCommands.toColumn(context, out, s.map(PathEntry::display), opt.isSet("x"));
            }
        };
        boolean space = false;
        if (!files.isEmpty()) {
            display.accept(files.stream());
            space = true;
        }
        List directories = all.stream().filter(PathEntry::isDirectory).collect(Collectors.toList());
        for (PathEntry entry : directories) {
            if (space) {
                out.println();
            }
            space = true;
            Path path = currentDir.resolve(entry.path);
            if (expanded.size() > 1) {
                out.println(currentDir.relativize(path).toString() + ":");
            }
            Stream<Path> pathStream = Files.list(path);
            try {
                display.accept(Stream.concat(Stream.of(".", "..").map(path::resolve), pathStream).filter(filter).map(p -> new PathEntry((Path)p, path)).sorted());
            }
            finally {
                if (pathStream == null) continue;
                pathStream.close();
            }
        }
    }

    private static void toColumn(Context context, PrintStream out, Stream<String> ansi, boolean horizontal) {
        Terminal terminal = context.terminal();
        int width = context.isTty() ? terminal.getWidth() : 80;
        List strings = ansi.map(AttributedString::fromAnsi).collect(Collectors.toList());
        if (!strings.isEmpty()) {
            int c;
            int max = strings.stream().mapToInt(AttributedCharSequence::columnLength).max().getAsInt();
            for (c = Math.max(1, width / max); c > 1 && c * max + (c - 1) >= width; --c) {
            }
            int columns = c;
            int lines = (strings.size() + columns - 1) / columns;
            IntBinaryOperator index = horizontal ? (i, j) -> i * columns + j : (i, j) -> j * lines + i;
            AttributedStringBuilder sb = new AttributedStringBuilder();
            for (int i2 = 0; i2 < lines; ++i2) {
                for (int j2 = 0; j2 < columns; ++j2) {
                    int idx = index.applyAsInt(i2, j2);
                    if (idx >= strings.size()) continue;
                    AttributedString str = (AttributedString)strings.get(idx);
                    boolean hasRightItem = j2 < columns - 1 && index.applyAsInt(i2, j2 + 1) < strings.size();
                    sb.append(str);
                    if (!hasRightItem) continue;
                    for (int k = 0; k <= max - str.length(); ++k) {
                        sb.append(' ');
                    }
                }
                sb.append('\n');
            }
            out.print(sb.toAnsi(terminal));
        }
    }

    private static String formatHumanReadable(long bytes) {
        if (bytes < 1024L) {
            return bytes + "B";
        }
        int exp = (int)(Math.log(bytes) / Math.log(1024.0));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f%s", (double)bytes / Math.pow(1024.0, exp), pre);
    }

    private static void readLines(BufferedReader reader, List<String> lines) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }

    public static Map<String, String> getLsColorMap(String colorString) {
        return PosixCommands.getColorMap(colorString != null ? colorString : DEFAULT_LS_COLORS);
    }

    public static Map<String, String> getColorMap(String colorString) {
        String str;
        String string = str = colorString != null ? colorString : "";
        if (str.isEmpty()) {
            return Collections.emptyMap();
        }
        String sep = str.matches("[a-z]{2}=[0-9]*(;[0-9]+)*(:[a-z]{2}=[0-9]*(;[0-9]+)*)*") ? ":" : " ";
        return Arrays.stream(str.split(sep)).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(61)), s -> s.substring(s.indexOf(61) + 1)));
    }

    public static Map<String, String> getLsColorMap(Context session) {
        return PosixCommands.getColorMap(session, "LS", DEFAULT_LS_COLORS);
    }

    public static Map<String, String> getColorMap(Context session, String name, String def) {
        return PosixCommands.getColorMap(session::get, name, def);
    }

    public static Map<String, String> getColorMap(Function<String, Object> variables, String name, String def) {
        String str;
        Object obj = variables.apply(name + "_COLORS");
        String string = str = obj != null ? obj.toString() : null;
        if (str == null) {
            str = def;
        }
        String sep = str.matches("[a-z]{2}=[0-9]*(;[0-9]+)*(:[a-z]{2}=[0-9]*(;[0-9]+)*)*") ? ":" : " ";
        return Arrays.stream(str.split(sep)).collect(Collectors.toMap(s -> s.substring(0, s.indexOf(61)), s -> s.substring(s.indexOf(61) + 1)));
    }

    public static String applyStyle(String text, Map<String, String> colors, String ... types) {
        String t = null;
        for (String type : types) {
            if (colors.get(type) == null) continue;
            t = type;
            break;
        }
        return new AttributedString(text, new StyleResolver(colors::get).resolve("." + t)).toAnsi();
    }

    public static void applyStyle(AttributedStringBuilder sb, Map<String, String> colors, String ... types) {
        String t = null;
        for (String type : types) {
            if (colors.get(type) == null) continue;
            t = type;
            break;
        }
        sb.style(new StyleResolver(colors::get).resolve("." + t));
    }

    private static LinkOption[] getLinkOptions(boolean followLinks) {
        if (followLinks) {
            return EMPTY_LINK_OPTIONS;
        }
        return (LinkOption[])NO_FOLLOW_OPTIONS.clone();
    }

    private static boolean isWindowsExecutable(String fileName) {
        if (fileName == null || fileName.length() <= 0) {
            return false;
        }
        for (String suffix : WINDOWS_EXECUTABLE_EXTENSIONS) {
            if (!fileName.endsWith(suffix)) continue;
            return true;
        }
        return false;
    }

    private static Set<PosixFilePermission> getPermissionsFromFile(Path f) {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(f, new LinkOption[0]);
            if (OSUtils.IS_WINDOWS && PosixCommands.isWindowsExecutable(f.getFileName().toString())) {
                perms = new HashSet<PosixFilePermission>(perms);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                perms.add(PosixFilePermission.GROUP_EXECUTE);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);
            }
            return perms;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Options parseOptions(Context context, String[] usage, Object[] argv) throws Exception {
        Options opt = Options.compile(usage, s -> PosixCommands.get(context, s)).parse(argv, true);
        if (opt.isSet("help")) {
            throw new Options.HelpException(opt.usage());
        }
        return opt;
    }

    protected static String get(Context context, String name) {
        Object o = context.get(name);
        return o != null ? o.toString() : null;
    }

    public static class Context {
        private final InputStream in;
        private final PrintStream out;
        private final PrintStream err;
        private final Path currentDir;
        private final Terminal terminal;
        private final Function<String, Object> variables;

        public Context(InputStream in, PrintStream out, PrintStream err, Path currentDir, Terminal terminal, Function<String, Object> variables) {
            this.in = in;
            this.out = out;
            this.err = err;
            this.currentDir = currentDir;
            this.terminal = terminal;
            this.variables = variables;
        }

        public InputStream in() {
            return this.in;
        }

        public PrintStream out() {
            return this.out;
        }

        public PrintStream err() {
            return this.err;
        }

        public Path currentDir() {
            return this.currentDir;
        }

        public Terminal terminal() {
            return this.terminal;
        }

        public boolean isTty() {
            return this.terminal != null;
        }

        public Object get(String name) {
            return this.variables.apply(name);
        }
    }

    public static interface CommandExecutor {
        public String execute(List<String> var1) throws Exception;
    }

    private static class GrepSource {
        private final InputStream inputStream;
        private final Path path;
        private final String name;

        public GrepSource(InputStream inputStream, String name) {
            this.inputStream = inputStream;
            this.path = null;
            this.name = name;
        }

        public GrepSource(Path path, String name) {
            this.inputStream = null;
            this.path = path;
            this.name = name;
        }

        public InputStream getInputStream() throws IOException {
            if (this.inputStream != null) {
                return this.inputStream;
            }
            return this.path.toUri().toURL().openStream();
        }

        public String getName() {
            return this.name;
        }
    }

    public static class SortComparator
    implements Comparator<String> {
        private static Pattern fpPattern;
        private boolean caseInsensitive;
        private boolean reverse;
        private boolean ignoreBlanks;
        private boolean numeric;
        private char separator;
        private List<Key> sortKeys;

        public SortComparator(boolean caseInsensitive, boolean reverse, boolean ignoreBlanks, boolean numeric, char separator, List<String> sortFields) {
            this.caseInsensitive = caseInsensitive;
            this.reverse = reverse;
            this.separator = separator;
            this.ignoreBlanks = ignoreBlanks;
            this.numeric = numeric;
            if (sortFields == null || sortFields.isEmpty()) {
                sortFields = new ArrayList<String>();
                sortFields.add("1");
            }
            this.sortKeys = sortFields.stream().map(x$0 -> new Key((String)x$0)).collect(Collectors.toList());
        }

        @Override
        public int compare(String o1, String o2) {
            int res = 0;
            List<Integer> fi1 = this.getFieldIndexes(o1);
            List<Integer> fi2 = this.getFieldIndexes(o2);
            for (Key key : this.sortKeys) {
                int[] k1 = this.getSortKey(o1, fi1, key);
                int[] k2 = this.getSortKey(o2, fi2, key);
                if (key.numeric) {
                    Double d1 = this.getDouble(o1, k1[0], k1[1]);
                    Double d2 = this.getDouble(o2, k2[0], k2[1]);
                    res = d1.compareTo(d2);
                } else {
                    res = this.compareRegion(o1, k1[0], k1[1], o2, k2[0], k2[1], key.caseInsensitive);
                }
                if (res == 0) continue;
                if (!key.reverse) break;
                res = -res;
                break;
            }
            return res;
        }

        protected Double getDouble(String s, int start, int end) {
            String field = s.substring(start, end);
            Matcher m = fpPattern.matcher(field);
            if (m.find()) {
                return Double.valueOf(field.substring(m.start(1), m.end(1)));
            }
            return 0.0;
        }

        protected int compareRegion(String s1, int start1, int end1, String s2, int start2, int end2, boolean caseInsensitive) {
            int i1 = start1;
            for (int i2 = start2; i1 < end1 && i2 < end2; ++i1, ++i2) {
                char c2;
                char c1 = s1.charAt(i1);
                if (c1 == (c2 = s2.charAt(i2))) continue;
                if (caseInsensitive) {
                    if ((c1 = Character.toUpperCase(c1)) == (c2 = Character.toUpperCase(c2)) || (c1 = Character.toLowerCase(c1)) == (c2 = Character.toLowerCase(c2))) continue;
                    return c1 - c2;
                }
                return c1 - c2;
            }
            return end1 - end2;
        }

        protected int[] getSortKey(String str, List<Integer> fields, Key key) {
            int end;
            int start;
            if (key.startField * 2 <= fields.size()) {
                if (key.ignoreBlanksStart) {
                    for (start = fields.get((key.startField - 1) * 2).intValue(); start < fields.get((key.startField - 1) * 2 + 1) && Character.isWhitespace(str.charAt(start)); ++start) {
                    }
                }
                if (key.startChar > 0) {
                    start = Math.min(start + key.startChar - 1, fields.get((key.startField - 1) * 2 + 1));
                }
            } else {
                start = 0;
            }
            if (key.endField > 0 && key.endField * 2 <= fields.size()) {
                if (key.ignoreBlanksEnd) {
                    for (end = fields.get((key.endField - 1) * 2).intValue(); end < fields.get((key.endField - 1) * 2 + 1) && Character.isWhitespace(str.charAt(end)); ++end) {
                    }
                }
                if (key.endChar > 0) {
                    end = Math.min(end + key.endChar - 1, fields.get((key.endField - 1) * 2 + 1));
                }
            } else {
                end = str.length();
            }
            return new int[]{start, end};
        }

        protected List<Integer> getFieldIndexes(String o) {
            ArrayList<Integer> fields = new ArrayList<Integer>();
            if (o.length() > 0) {
                if (this.separator == '\u0000') {
                    fields.add(0);
                    for (int idx = 1; idx < o.length(); ++idx) {
                        if (!Character.isWhitespace(o.charAt(idx)) || Character.isWhitespace(o.charAt(idx - 1))) continue;
                        fields.add(idx - 1);
                        fields.add(idx);
                    }
                    fields.add(o.length() - 1);
                } else {
                    int last = -1;
                    int idx = o.indexOf(this.separator);
                    while (idx >= 0) {
                        if (last >= 0) {
                            fields.add(last);
                            fields.add(idx - 1);
                        } else if (idx > 0) {
                            fields.add(0);
                            fields.add(idx - 1);
                        }
                        last = idx + 1;
                        idx = o.indexOf(this.separator, idx + 1);
                    }
                    if (last < o.length()) {
                        fields.add(Math.max(last, 0));
                        fields.add(o.length() - 1);
                    }
                }
            }
            return fields;
        }

        static {
            String Digits = "(\\p{Digit}+)";
            String HexDigits = "(\\p{XDigit}+)";
            String Exp = "[eE][+-]?(\\p{Digit}+)";
            String fpRegex = "([\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*)(.*)";
            fpPattern = Pattern.compile("([\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*)(.*)");
        }

        public class Key {
            int startField;
            int startChar;
            int endField;
            int endChar;
            boolean ignoreBlanksStart;
            boolean ignoreBlanksEnd;
            boolean caseInsensitive;
            boolean reverse;
            boolean numeric;

            public Key(String str) {
                boolean modifiers = false;
                boolean startPart = true;
                boolean inField = true;
                boolean inChar = false;
                block9: for (char c : str.toCharArray()) {
                    switch (c) {
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
                            if (!inField && !inChar) {
                                throw new IllegalArgumentException("Bad field syntax: " + str);
                            }
                            if (startPart) {
                                if (inChar) {
                                    this.startChar = this.startChar * 10 + (c - 48);
                                    continue block9;
                                }
                                this.startField = this.startField * 10 + (c - 48);
                                continue block9;
                            }
                            if (inChar) {
                                this.endChar = this.endChar * 10 + (c - 48);
                                continue block9;
                            }
                            this.endField = this.endField * 10 + (c - 48);
                            continue block9;
                        }
                        case '.': {
                            if (!inField) {
                                throw new IllegalArgumentException("Bad field syntax: " + str);
                            }
                            inField = false;
                            inChar = true;
                            continue block9;
                        }
                        case 'n': {
                            inField = false;
                            inChar = false;
                            modifiers = true;
                            this.numeric = true;
                            continue block9;
                        }
                        case 'f': {
                            inField = false;
                            inChar = false;
                            modifiers = true;
                            this.caseInsensitive = true;
                            continue block9;
                        }
                        case 'r': {
                            inField = false;
                            inChar = false;
                            modifiers = true;
                            this.reverse = true;
                            continue block9;
                        }
                        case 'b': {
                            inField = false;
                            inChar = false;
                            modifiers = true;
                            if (startPart) {
                                this.ignoreBlanksStart = true;
                                continue block9;
                            }
                            this.ignoreBlanksEnd = true;
                            continue block9;
                        }
                        case ',': {
                            inField = true;
                            inChar = false;
                            startPart = false;
                            continue block9;
                        }
                        default: {
                            throw new IllegalArgumentException("Bad field syntax: " + str);
                        }
                    }
                }
                if (!modifiers) {
                    this.ignoreBlanksStart = this.ignoreBlanksEnd = SortComparator.this.ignoreBlanks;
                    this.reverse = SortComparator.this.reverse;
                    this.caseInsensitive = SortComparator.this.caseInsensitive;
                    this.numeric = SortComparator.this.numeric;
                }
                if (this.startField < 1) {
                    throw new IllegalArgumentException("Bad field syntax: " + str);
                }
            }
        }
    }
}

