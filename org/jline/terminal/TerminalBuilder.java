/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractPosixTerminal;
import org.jline.terminal.impl.AbstractTerminal;
import org.jline.terminal.impl.DumbTerminalProvider;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalExt;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public final class TerminalBuilder {
    public static final String PROP_ENCODING = "org.jline.terminal.encoding";
    public static final String PROP_STDIN_ENCODING = "org.jline.terminal.stdin.encoding";
    public static final String PROP_STDOUT_ENCODING = "org.jline.terminal.stdout.encoding";
    public static final String PROP_STDERR_ENCODING = "org.jline.terminal.stderr.encoding";
    public static final String PROP_CODEPAGE = "org.jline.terminal.codepage";
    public static final String PROP_TYPE = "org.jline.terminal.type";
    public static final String PROP_PROVIDER = "org.jline.terminal.provider";
    public static final String PROP_PROVIDERS = "org.jline.terminal.providers";
    public static final String PROP_PROVIDER_FFM = "ffm";
    public static final String PROP_PROVIDER_JNI = "jni";
    public static final String PROP_PROVIDER_JANSI = "jansi";
    public static final String PROP_PROVIDER_JNA = "jna";
    public static final String PROP_PROVIDER_EXEC = "exec";
    public static final String PROP_PROVIDER_DUMB = "dumb";
    public static final String PROP_PROVIDERS_DEFAULT = String.join((CharSequence)",", "ffm", "jni", "jansi", "jna", "exec");
    public static final String PROP_FFM = "org.jline.terminal.ffm";
    public static final String PROP_JNI = "org.jline.terminal.jni";
    public static final String PROP_JANSI = "org.jline.terminal.jansi";
    public static final String PROP_JNA = "org.jline.terminal.jna";
    public static final String PROP_EXEC = "org.jline.terminal.exec";
    public static final String PROP_DUMB = "org.jline.terminal.dumb";
    public static final String PROP_DUMB_COLOR = "org.jline.terminal.dumb.color";
    public static final String PROP_OUTPUT = "org.jline.terminal.output";
    public static final String PROP_OUTPUT_OUT = "out";
    public static final String PROP_OUTPUT_ERR = "err";
    public static final String PROP_OUTPUT_OUT_ERR = "out-err";
    public static final String PROP_OUTPUT_ERR_OUT = "err-out";
    public static final String PROP_OUTPUT_FORCED_OUT = "forced-out";
    public static final String PROP_OUTPUT_FORCED_ERR = "forced-err";
    public static final String PROP_NON_BLOCKING_READS = "org.jline.terminal.pty.nonBlockingReads";
    public static final String PROP_COLOR_DISTANCE = "org.jline.utils.colorDistance";
    public static final String PROP_DISABLE_ALTERNATE_CHARSET = "org.jline.utils.disableAlternateCharset";
    public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE = "org.jline.terminal.pty.fileDescriptorCreationMode";
    public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE_NATIVE = "native";
    public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE_REFLECTION = "reflection";
    public static final String PROP_FILE_DESCRIPTOR_CREATION_MODE_DEFAULT = String.join((CharSequence)",", "reflection", "native");
    public static final String PROP_REDIRECT_PIPE_CREATION_MODE = "org.jline.terminal.exec.redirectPipeCreationMode";
    public static final String PROP_REDIRECT_PIPE_CREATION_MODE_NATIVE = "native";
    public static final String PROP_REDIRECT_PIPE_CREATION_MODE_REFLECTION = "reflection";
    public static final String PROP_REDIRECT_PIPE_CREATION_MODE_DEFAULT = String.join((CharSequence)",", "reflection", "native");
    public static final Set<String> DEPRECATED_PROVIDERS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("jna", "jansi")));
    public static final String PROP_DISABLE_DEPRECATED_PROVIDER_WARNING = "org.jline.terminal.disableDeprecatedProviderWarning";
    private static final AtomicReference<Terminal> SYSTEM_TERMINAL = new AtomicReference();
    private static final AtomicReference<Terminal> TERMINAL_OVERRIDE = new AtomicReference();
    private String name;
    private InputStream in;
    private OutputStream out;
    private String type;
    private Charset encoding;
    private Charset stdinEncoding;
    private Charset stdoutEncoding;
    private Charset stderrEncoding;
    private int codepage;
    private Boolean system;
    private SystemOutput systemOutput;
    private String provider;
    private String providers;
    private Boolean jna;
    private Boolean jansi;
    private Boolean jni;
    private Boolean exec;
    private Boolean ffm;
    private Boolean dumb;
    private Boolean color;
    private Attributes attributes;
    private Size size;
    private boolean nativeSignals = true;
    private Terminal.SignalHandler signalHandler = Terminal.SignalHandler.SIG_DFL;
    private boolean paused = false;
    private static final int UTF8_CODE_PAGE = 65001;

    public static Terminal terminal() throws IOException {
        return TerminalBuilder.builder().build();
    }

    public static TerminalBuilder builder() {
        return new TerminalBuilder();
    }

    private TerminalBuilder() {
    }

    public TerminalBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TerminalBuilder streams(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        return this;
    }

    public TerminalBuilder system(boolean system) {
        this.system = system;
        return this;
    }

    public TerminalBuilder systemOutput(SystemOutput systemOutput) {
        this.systemOutput = systemOutput;
        return this;
    }

    public TerminalBuilder provider(String provider) {
        this.provider = provider;
        return this;
    }

    public TerminalBuilder providers(String providers) {
        this.providers = providers;
        return this;
    }

    public TerminalBuilder jna(boolean jna) {
        this.jna = jna;
        return this;
    }

    public TerminalBuilder jansi(boolean jansi) {
        this.jansi = jansi;
        return this;
    }

    public TerminalBuilder jni(boolean jni) {
        this.jni = jni;
        return this;
    }

    public TerminalBuilder exec(boolean exec) {
        this.exec = exec;
        return this;
    }

    public TerminalBuilder ffm(boolean ffm) {
        this.ffm = ffm;
        return this;
    }

    public TerminalBuilder dumb(boolean dumb) {
        this.dumb = dumb;
        return this;
    }

    public TerminalBuilder type(String type) {
        this.type = type;
        return this;
    }

    public TerminalBuilder color(boolean color) {
        this.color = color;
        return this;
    }

    public TerminalBuilder encoding(String encoding) throws UnsupportedCharsetException {
        return this.encoding(encoding != null ? Charset.forName(encoding) : null);
    }

    public TerminalBuilder encoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public TerminalBuilder stdinEncoding(String encoding) throws UnsupportedCharsetException {
        return this.stdinEncoding(encoding != null ? Charset.forName(encoding) : null);
    }

    public TerminalBuilder stdinEncoding(Charset encoding) {
        this.stdinEncoding = encoding;
        return this;
    }

    public TerminalBuilder stdoutEncoding(String encoding) throws UnsupportedCharsetException {
        return this.stdoutEncoding(encoding != null ? Charset.forName(encoding) : null);
    }

    public TerminalBuilder stdoutEncoding(Charset encoding) {
        this.stdoutEncoding = encoding;
        return this;
    }

    public TerminalBuilder stderrEncoding(String encoding) throws UnsupportedCharsetException {
        return this.stderrEncoding(encoding != null ? Charset.forName(encoding) : null);
    }

    public TerminalBuilder stderrEncoding(Charset encoding) {
        this.stderrEncoding = encoding;
        return this;
    }

    @Deprecated
    public TerminalBuilder codepage(int codepage) {
        this.codepage = codepage;
        return this;
    }

    public TerminalBuilder attributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    public TerminalBuilder size(Size size) {
        this.size = size;
        return this;
    }

    public TerminalBuilder nativeSignals(boolean nativeSignals) {
        this.nativeSignals = nativeSignals;
        return this;
    }

    public TerminalBuilder signalHandler(Terminal.SignalHandler signalHandler) {
        this.signalHandler = signalHandler;
        return this;
    }

    public TerminalBuilder paused(boolean paused) {
        this.paused = paused;
        return this;
    }

    public Terminal build() throws IOException {
        Terminal terminal;
        Terminal override = TERMINAL_OVERRIDE.get();
        Terminal terminal2 = terminal = override != null ? override : this.doBuild();
        if (override != null) {
            Log.debug(() -> "Overriding terminal with global value set by TerminalBuilder.setTerminalOverride");
        }
        Log.debug(() -> "Using terminal " + terminal.getClass().getSimpleName());
        if (terminal instanceof AbstractPosixTerminal) {
            Log.debug(() -> "Using pty " + ((AbstractPosixTerminal)terminal).getPty().getClass().getSimpleName());
        }
        return terminal;
    }

    private Terminal doBuild() throws IOException {
        TerminalExt te;
        Terminal terminal;
        IllegalStateException exception;
        block24: {
            List<TerminalProvider> providers;
            String type;
            Charset stdoutEncoding;
            Charset stdinEncoding;
            Charset encoding;
            String name;
            block23: {
                name = this.name;
                if (name == null) {
                    name = "JLine terminal";
                }
                encoding = this.computeEncoding();
                stdinEncoding = this.computeStdinEncoding();
                stdoutEncoding = this.computeStdoutEncoding();
                Charset stderrEncoding = this.computeStderrEncoding();
                type = this.computeType();
                String provider = this.provider;
                if (provider == null) {
                    provider = System.getProperty(PROP_PROVIDER, null);
                }
                boolean forceDumb = PROP_PROVIDER_DUMB.equals(type) || type != null && type.startsWith("dumb-color") || provider != null && provider.equals(PROP_PROVIDER_DUMB);
                Boolean dumb = this.dumb;
                if (dumb == null) {
                    dumb = TerminalBuilder.getBoolean(PROP_DUMB, null);
                }
                exception = new IllegalStateException("Unable to create a terminal");
                providers = this.getProviders(provider, exception);
                terminal = null;
                if ((this.system == null || !this.system.booleanValue()) && (this.system != null || this.in != null || this.out != null)) break block23;
                if (this.system != null && (this.in != null && !this.in.equals(System.in) || this.out != null && !this.out.equals(System.out) && !this.out.equals(System.err))) {
                    throw new IllegalArgumentException("Cannot create a system terminal using non System streams");
                }
                if (this.attributes != null || this.size != null) {
                    Log.warn("Attributes and size fields are ignored when creating a system terminal");
                }
                Object systemOutput = this.computeSystemOutput();
                Map<SystemStream, Boolean> system = Stream.of(SystemStream.values()).collect(Collectors.toMap(stream -> stream, stream -> providers.stream().anyMatch(p -> p.isSystemStream((SystemStream)((Object)stream)))));
                SystemStream systemStream = this.select(system, (SystemOutput)((Object)systemOutput));
                if (!forceDumb && system.get((Object)SystemStream.Input).booleanValue() && systemStream != null) {
                    if (this.attributes != null || this.size != null) {
                        Log.warn("Attributes and size fields are ignored when creating a system terminal");
                    }
                    boolean ansiPassThrough = OSUtils.IS_CONEMU;
                    if ((OSUtils.IS_CYGWIN || OSUtils.IS_MSYSTEM) && "xterm".equals(type) && this.type == null && System.getProperty(PROP_TYPE) == null) {
                        type = "xterm-256color";
                    }
                    for (TerminalProvider prov : providers) {
                        if (terminal != null) continue;
                        try {
                            Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
                            terminal = prov.sysTerminal(name, type, ansiPassThrough, encoding, stdinEncoding, outputEncoding, this.nativeSignals, this.signalHandler, this.paused, systemStream);
                        }
                        catch (Throwable t) {
                            Log.debug("Error creating " + prov.name() + " based terminal: ", t.getMessage(), t);
                            exception.addSuppressed(t);
                        }
                    }
                    if (terminal == null && OSUtils.IS_WINDOWS && providers.isEmpty() && (dumb == null || !dumb.booleanValue())) {
                        throw new IllegalStateException("Unable to create a system terminal. On Windows, either JLine's native libraries, JNA or Jansi library is required.  Make sure to add one of those in the classpath.", exception);
                    }
                }
                if (terminal instanceof AbstractTerminal) {
                    AbstractTerminal t = (AbstractTerminal)terminal;
                    if (SYSTEM_TERMINAL.compareAndSet(null, t)) {
                        t.setOnClose(() -> SYSTEM_TERMINAL.compareAndSet(t, null));
                    } else {
                        exception.addSuppressed(new IllegalStateException("A system terminal is already running. Make sure to use the created system Terminal on the LineReaderBuilder if you're using one or that previously created system Terminals have been correctly closed."));
                        terminal.close();
                        terminal = null;
                    }
                }
                if (terminal != null || !forceDumb && dumb != null && !dumb.booleanValue()) break block24;
                if (!forceDumb && dumb == null) {
                    if (Log.isDebugEnabled()) {
                        Log.warn("input is tty: " + system.get((Object)SystemStream.Input));
                        Log.warn("output is tty: " + system.get((Object)SystemStream.Output));
                        Log.warn("error is tty: " + system.get((Object)SystemStream.Error));
                        Log.warn("Creating a dumb terminal", exception);
                    } else {
                        Log.warn("Unable to create a system terminal, creating a dumb terminal (enable debug logging for more information)");
                    }
                }
                type = this.getDumbTerminalType(dumb, systemStream);
                Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
                terminal = new DumbTerminalProvider().sysTerminal(name, type, false, encoding, stdinEncoding, outputEncoding, this.nativeSignals, this.signalHandler, this.paused, systemStream);
                if (!OSUtils.IS_WINDOWS) break block24;
                Attributes attr = terminal.getAttributes();
                attr.setInputFlag(Attributes.InputFlag.IGNCR, true);
                terminal.setAttributes(attr);
                break block24;
            }
            for (TerminalProvider prov : providers) {
                if (terminal != null) continue;
                try {
                    terminal = prov.newTerminal(name, type, this.in, this.out, encoding, stdinEncoding, stdoutEncoding, this.signalHandler, this.paused, this.attributes, this.size);
                }
                catch (Throwable t) {
                    Log.debug("Error creating " + prov.name() + " based terminal: ", t.getMessage(), t);
                    exception.addSuppressed(t);
                }
            }
        }
        if (terminal == null) {
            throw exception;
        }
        if (terminal instanceof TerminalExt && DEPRECATED_PROVIDERS.contains((te = (TerminalExt)terminal).getProvider().name()) && !TerminalBuilder.getBoolean(PROP_DISABLE_DEPRECATED_PROVIDER_WARNING, false).booleanValue()) {
            Log.warn("The terminal provider " + te.getProvider().name() + " has been deprecated, check your configuration. This warning can be disabled by setting the system property " + PROP_DISABLE_DEPRECATED_PROVIDER_WARNING + " to true.");
        }
        return terminal;
    }

    private String getDumbTerminalType(Boolean dumb, SystemStream systemStream) {
        Boolean color = this.color;
        if (color == null) {
            color = TerminalBuilder.getBoolean(PROP_DUMB_COLOR, null);
        }
        if (dumb == null) {
            String emacs;
            if (color == null && (emacs = System.getenv("INSIDE_EMACS")) != null && emacs.contains("comint")) {
                color = true;
            }
            if (color == null) {
                String ideHome = System.getenv("IDE_HOME");
                if (ideHome != null) {
                    color = true;
                } else {
                    String command = TerminalBuilder.getParentProcessCommand();
                    if (command != null && command.endsWith("/idea")) {
                        color = true;
                    }
                }
            }
            if (color == null) {
                color = systemStream != null && System.getenv("TERM") != null;
            }
        } else if (color == null) {
            color = false;
        }
        return color != false ? "dumb-color" : PROP_PROVIDER_DUMB;
    }

    public SystemOutput computeSystemOutput() {
        String str;
        SystemOutput systemOutput = null;
        if (this.out != null) {
            if (this.out.equals(System.out)) {
                systemOutput = SystemOutput.SysOut;
            } else if (this.out.equals(System.err)) {
                systemOutput = SystemOutput.SysErr;
            }
        }
        if (systemOutput == null) {
            systemOutput = this.systemOutput;
        }
        if (systemOutput == null && (str = System.getProperty(PROP_OUTPUT)) != null) {
            switch (str.trim().toLowerCase(Locale.ROOT)) {
                case "out": {
                    systemOutput = SystemOutput.SysOut;
                    break;
                }
                case "err": {
                    systemOutput = SystemOutput.SysErr;
                    break;
                }
                case "out-err": {
                    systemOutput = SystemOutput.SysOutOrSysErr;
                    break;
                }
                case "err-out": {
                    systemOutput = SystemOutput.SysErrOrSysOut;
                    break;
                }
                case "forced-out": {
                    systemOutput = SystemOutput.ForcedSysOut;
                    break;
                }
                case "forced-err": {
                    systemOutput = SystemOutput.ForcedSysErr;
                    break;
                }
                default: {
                    Log.debug("Unsupported value for org.jline.terminal.output: " + str + ". Supported values are: " + String.join((CharSequence)", ", PROP_OUTPUT_OUT, PROP_OUTPUT_ERR, PROP_OUTPUT_OUT_ERR, PROP_OUTPUT_ERR_OUT) + ".");
                }
            }
        }
        if (systemOutput == null) {
            systemOutput = SystemOutput.SysOutOrSysErr;
        }
        return systemOutput;
    }

    public String computeType() {
        String type = this.type;
        if (type == null) {
            type = System.getProperty(PROP_TYPE);
        }
        if (type == null) {
            type = System.getenv("TERM");
        }
        return type;
    }

    public Charset computeEncoding() {
        String charsetName;
        Charset encoding = this.encoding;
        if (encoding == null && (charsetName = System.getProperty(PROP_ENCODING)) != null && Charset.isSupported(charsetName)) {
            encoding = Charset.forName(charsetName);
        }
        if (encoding == null) {
            String str;
            int codepage = this.codepage;
            if (codepage <= 0 && (str = System.getProperty(PROP_CODEPAGE)) != null) {
                codepage = Integer.parseInt(str);
            }
            encoding = codepage >= 0 ? TerminalBuilder.getCodepageCharset(codepage) : StandardCharsets.UTF_8;
        }
        return encoding;
    }

    public Charset computeStdinEncoding() {
        return this.computeSpecificEncoding(this.stdinEncoding, PROP_STDIN_ENCODING, "stdin.encoding");
    }

    public Charset computeStdoutEncoding() {
        return this.computeSpecificEncoding(this.stdoutEncoding, PROP_STDOUT_ENCODING, "stdout.encoding");
    }

    public Charset computeStderrEncoding() {
        return this.computeSpecificEncoding(this.stderrEncoding, PROP_STDERR_ENCODING, "stderr.encoding");
    }

    private Charset computeSpecificEncoding(Charset specificEncoding, String jlineProperty, String standardProperty) {
        Charset encoding = specificEncoding;
        if (encoding == null) {
            String charsetName = System.getProperty(jlineProperty);
            if (charsetName != null && Charset.isSupported(charsetName)) {
                encoding = Charset.forName(charsetName);
            }
            if (encoding == null && (charsetName = System.getProperty(standardProperty)) != null && Charset.isSupported(charsetName)) {
                encoding = Charset.forName(charsetName);
            }
        }
        if (encoding == null) {
            encoding = this.computeEncoding();
        }
        return encoding;
    }

    public List<TerminalProvider> getProviders(String provider, IllegalStateException exception) {
        ArrayList<TerminalProvider> providers = new ArrayList<TerminalProvider>();
        this.checkProvider(provider, exception, providers, this.ffm, PROP_FFM, PROP_PROVIDER_FFM);
        this.checkProvider(provider, exception, providers, this.jni, PROP_JNI, PROP_PROVIDER_JNI);
        this.checkProvider(provider, exception, providers, this.jansi, PROP_JANSI, PROP_PROVIDER_JANSI);
        this.checkProvider(provider, exception, providers, this.jna, PROP_JNA, PROP_PROVIDER_JNA);
        this.checkProvider(provider, exception, providers, this.exec, PROP_EXEC, PROP_PROVIDER_EXEC);
        List<String> order = Arrays.asList((this.providers != null ? this.providers : System.getProperty(PROP_PROVIDERS, PROP_PROVIDERS_DEFAULT)).split(","));
        providers.sort(Comparator.comparing(l -> {
            int idx = order.indexOf(l.name());
            return idx >= 0 ? idx : Integer.MAX_VALUE;
        }));
        String names = providers.stream().map(TerminalProvider::name).collect(Collectors.joining(", "));
        Log.debug("Available providers: " + names);
        return providers;
    }

    private void checkProvider(String provider, IllegalStateException exception, List<TerminalProvider> providers, Boolean load, String property, String name) {
        Boolean doLoad;
        Boolean bl = doLoad = provider != null ? Boolean.valueOf(name.equals(provider)) : load;
        if (doLoad == null) {
            doLoad = TerminalBuilder.getBoolean(property, true);
        }
        if (doLoad.booleanValue()) {
            try {
                TerminalProvider prov = TerminalProvider.load(name);
                prov.isSystemStream(SystemStream.Output);
                providers.add(prov);
            }
            catch (Throwable t) {
                Log.debug("Unable to load " + name + " provider: ", t);
                exception.addSuppressed(t);
            }
        }
    }

    private SystemStream select(Map<SystemStream, Boolean> system, SystemOutput systemOutput) {
        switch (systemOutput.ordinal()) {
            case 0: {
                return TerminalBuilder.select(system, SystemStream.Output);
            }
            case 1: {
                return TerminalBuilder.select(system, SystemStream.Error);
            }
            case 2: {
                return TerminalBuilder.select(system, SystemStream.Output, SystemStream.Error);
            }
            case 3: {
                return TerminalBuilder.select(system, SystemStream.Error, SystemStream.Output);
            }
            case 4: {
                return SystemStream.Output;
            }
            case 5: {
                return SystemStream.Error;
            }
        }
        return null;
    }

    private static SystemStream select(Map<SystemStream, Boolean> system, SystemStream ... streams) {
        for (SystemStream s : streams) {
            if (!system.get((Object)s).booleanValue()) continue;
            return s;
        }
        return null;
    }

    private static String getParentProcessCommand() {
        try {
            Class<?> phClass = Class.forName("java.lang.ProcessHandle");
            Object current = phClass.getMethod("current", new Class[0]).invoke(null, new Object[0]);
            Object parent = ((Optional)phClass.getMethod("parent", new Class[0]).invoke(current, new Object[0])).orElse(null);
            Method infoMethod = phClass.getMethod("info", new Class[0]);
            Object info = infoMethod.invoke(parent, new Object[0]);
            Object command = ((Optional)infoMethod.getReturnType().getMethod("command", new Class[0]).invoke(info, new Object[0])).orElse(null);
            return command;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Boolean getBoolean(String name, Boolean def) {
        try {
            String str = System.getProperty(name);
            if (str != null) {
                return Boolean.parseBoolean(str);
            }
        }
        catch (IllegalArgumentException | NullPointerException runtimeException) {
            // empty catch block
        }
        return def;
    }

    private static <S> S load(Class<S> clazz) {
        return ServiceLoader.load(clazz, clazz.getClassLoader()).iterator().next();
    }

    private static Charset getCodepageCharset(int codepage) {
        if (codepage == 65001) {
            return StandardCharsets.UTF_8;
        }
        String charsetMS = "ms" + codepage;
        if (Charset.isSupported(charsetMS)) {
            return Charset.forName(charsetMS);
        }
        String charsetCP = "cp" + codepage;
        if (Charset.isSupported(charsetCP)) {
            return Charset.forName(charsetCP);
        }
        return Charset.defaultCharset();
    }

    @Deprecated
    public static void setTerminalOverride(Terminal terminal) {
        TERMINAL_OVERRIDE.set(terminal);
    }

    public static enum SystemOutput {
        SysOut,
        SysErr,
        SysOutOrSysErr,
        SysErrOrSysOut,
        ForcedSysOut,
        ForcedSysErr;

    }
}

