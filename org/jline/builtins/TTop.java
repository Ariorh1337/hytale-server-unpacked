/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jline.builtins.Options;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;

public class TTop {
    public static final String STAT_UPTIME = "uptime";
    public static final String STAT_TID = "tid";
    public static final String STAT_NAME = "name";
    public static final String STAT_STATE = "state";
    public static final String STAT_BLOCKED_TIME = "blocked_time";
    public static final String STAT_BLOCKED_COUNT = "blocked_count";
    public static final String STAT_WAITED_TIME = "waited_time";
    public static final String STAT_WAITED_COUNT = "waited_count";
    public static final String STAT_LOCK_NAME = "lock_name";
    public static final String STAT_LOCK_OWNER_ID = "lock_owner_id";
    public static final String STAT_LOCK_OWNER_NAME = "lock_owner_name";
    public static final String STAT_USER_TIME = "user_time";
    public static final String STAT_USER_TIME_PERC = "user_time_perc";
    public static final String STAT_CPU_TIME = "cpu_time";
    public static final String STAT_CPU_TIME_PERC = "cpu_time_perc";
    public List<String> sort;
    public long delay;
    public List<String> stats;
    public int nthreads;
    private final Map<String, Column> columns = new LinkedHashMap<String, Column>();
    private final Terminal terminal;
    private final Display display;
    private final BindingReader bindingReader;
    private final KeyMap<Operation> keys;
    private final Size size = new Size();
    private Comparator<Map<String, Comparable<?>>> comparator;
    private Map<Long, Map<String, Object>> previous = new HashMap<Long, Map<String, Object>>();
    private Map<Long, Map<String, Long>> changes = new HashMap<Long, Map<String, Long>>();
    private Map<String, Integer> widths = new HashMap<String, Integer>();

    public static void ttop(Terminal terminal, PrintStream out, PrintStream err, String[] argv) throws Exception {
        String[] usage = new String[]{"ttop -  display and update sorted information about threads", "Usage: ttop [OPTIONS]", "  -? --help                    Show help", "  -o --order=ORDER             Comma separated list of sorting keys", "  -t --stats=STATS             Comma separated list of stats to display", "  -s --seconds=SECONDS         Delay between updates in seconds", "  -m --millis=MILLIS           Delay between updates in milliseconds", "  -n --nthreads=NTHREADS       Only display up to NTHREADS threads"};
        Options opt = Options.compile(usage).parse(argv);
        if (opt.isSet("help")) {
            throw new Options.HelpException(opt.usage());
        }
        TTop ttop = new TTop(terminal);
        ttop.sort = opt.isSet("order") ? Arrays.asList(opt.get("order").split(",")) : null;
        ttop.delay = opt.isSet("seconds") ? (long)(opt.getNumber("seconds") * 1000) : ttop.delay;
        ttop.delay = opt.isSet("millis") ? (long)opt.getNumber("millis") : ttop.delay;
        ttop.stats = opt.isSet("stats") ? Arrays.asList(opt.get("stats").split(",")) : null;
        ttop.nthreads = opt.isSet("nthreads") ? opt.getNumber("nthreads") : ttop.nthreads;
        ttop.run();
    }

    public TTop(Terminal terminal) {
        this.terminal = terminal;
        this.display = new Display(terminal, true);
        this.bindingReader = new BindingReader(terminal.reader());
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        DecimalFormat perc = new DecimalFormat("0.00%", dfs);
        this.register(STAT_TID, Align.Right, "TID", o -> String.format("%3d", (Long)o));
        this.register(STAT_NAME, Align.Left, "NAME", TTop.padcut(40));
        this.register(STAT_STATE, Align.Left, "STATE", o -> o.toString().toLowerCase());
        this.register(STAT_BLOCKED_TIME, Align.Right, "T-BLOCKED", o -> TTop.millis((Long)o));
        this.register(STAT_BLOCKED_COUNT, Align.Right, "#-BLOCKED", Object::toString);
        this.register(STAT_WAITED_TIME, Align.Right, "T-WAITED", o -> TTop.millis((Long)o));
        this.register(STAT_WAITED_COUNT, Align.Right, "#-WAITED", Object::toString);
        this.register(STAT_LOCK_NAME, Align.Left, "LOCK-NAME", Object::toString);
        this.register(STAT_LOCK_OWNER_ID, Align.Right, "LOCK-OWNER-ID", id -> (Long)id >= 0L ? id.toString() : "");
        this.register(STAT_LOCK_OWNER_NAME, Align.Left, "LOCK-OWNER-NAME", name -> name != null ? name.toString() : "");
        this.register(STAT_USER_TIME, Align.Right, "T-USR", o -> TTop.nanos((Long)o));
        this.register(STAT_CPU_TIME, Align.Right, "T-CPU", o -> TTop.nanos((Long)o));
        this.register(STAT_USER_TIME_PERC, Align.Right, "%-USR", perc::format);
        this.register(STAT_CPU_TIME_PERC, Align.Right, "%-CPU", perc::format);
        this.keys = new KeyMap();
        this.bindKeys(this.keys);
    }

    public KeyMap<Operation> getKeys() {
        return this.keys;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() throws IOException, InterruptedException {
        this.comparator = this.buildComparator(this.sort);
        long l = this.delay = this.delay > 0L ? Math.max(this.delay, 100L) : 1000L;
        if (this.stats == null || this.stats.isEmpty()) {
            this.stats = new ArrayList<String>(Arrays.asList(STAT_TID, STAT_NAME, STAT_STATE, STAT_CPU_TIME, STAT_LOCK_OWNER_ID));
        }
        Boolean isThreadContentionMonitoringEnabled = null;
        ThreadMXBean threadsBean = ManagementFactory.getThreadMXBean();
        if (this.stats.contains(STAT_BLOCKED_TIME) || this.stats.contains(STAT_BLOCKED_COUNT) || this.stats.contains(STAT_WAITED_TIME) || this.stats.contains(STAT_WAITED_COUNT)) {
            if (threadsBean.isThreadContentionMonitoringSupported()) {
                isThreadContentionMonitoringEnabled = threadsBean.isThreadContentionMonitoringEnabled();
                if (!isThreadContentionMonitoringEnabled.booleanValue()) {
                    threadsBean.setThreadContentionMonitoringEnabled(true);
                }
            } else {
                this.stats.removeAll(Arrays.asList(STAT_BLOCKED_TIME, STAT_BLOCKED_COUNT, STAT_WAITED_TIME, STAT_WAITED_COUNT));
            }
        }
        Boolean isThreadCpuTimeEnabled = null;
        if (this.stats.contains(STAT_USER_TIME) || this.stats.contains(STAT_CPU_TIME)) {
            if (threadsBean.isThreadCpuTimeSupported()) {
                isThreadCpuTimeEnabled = threadsBean.isThreadCpuTimeEnabled();
                if (!isThreadCpuTimeEnabled.booleanValue()) {
                    threadsBean.setThreadCpuTimeEnabled(true);
                }
            } else {
                this.stats.removeAll(Arrays.asList(STAT_USER_TIME, STAT_CPU_TIME));
            }
        }
        this.size.copy(this.terminal.getSize());
        Terminal.SignalHandler prevHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handle);
        Attributes attr = this.terminal.enterRawMode();
        try {
            Operation op;
            if (!this.terminal.puts(InfoCmp.Capability.enter_ca_mode, new Object[0])) {
                this.terminal.puts(InfoCmp.Capability.clear_screen, new Object[0]);
            }
            this.terminal.puts(InfoCmp.Capability.keypad_xmit, new Object[0]);
            this.terminal.puts(InfoCmp.Capability.cursor_invisible, new Object[0]);
            this.terminal.writer().flush();
            long t0 = System.currentTimeMillis();
            do {
                this.display();
                this.checkInterrupted();
                op = null;
                long delta = ((System.currentTimeMillis() - t0) / this.delay + 1L) * this.delay + t0 - System.currentTimeMillis();
                int ch = this.bindingReader.peekCharacter(delta);
                if (ch == -1) {
                    op = Operation.EXIT;
                } else if (ch != -2) {
                    op = this.bindingReader.readBinding(this.keys, null, false);
                }
                if (op == null) continue;
                switch (op.ordinal()) {
                    case 0: {
                        this.delay *= 2L;
                        t0 = System.currentTimeMillis();
                        break;
                    }
                    case 1: {
                        this.delay = Math.max(this.delay / 2L, 16L);
                        t0 = System.currentTimeMillis();
                        break;
                    }
                    case 4: {
                        this.display.clear();
                        break;
                    }
                    case 5: {
                        this.comparator = this.comparator.reversed();
                    }
                }
            } while (op != Operation.EXIT);
        }
        catch (InterruptedException t0) {
        }
        catch (Error err) {
            Log.info("Error: ", err);
            return;
        }
        finally {
            this.terminal.setAttributes(attr);
            if (prevHandler != null) {
                this.terminal.handle(Terminal.Signal.WINCH, prevHandler);
            }
            if (!this.terminal.puts(InfoCmp.Capability.exit_ca_mode, new Object[0])) {
                this.terminal.puts(InfoCmp.Capability.clear_screen, new Object[0]);
            }
            this.terminal.puts(InfoCmp.Capability.keypad_local, new Object[0]);
            this.terminal.puts(InfoCmp.Capability.cursor_visible, new Object[0]);
            this.terminal.writer().flush();
            if (isThreadContentionMonitoringEnabled != null) {
                threadsBean.setThreadContentionMonitoringEnabled(isThreadContentionMonitoringEnabled);
            }
            if (isThreadCpuTimeEnabled != null) {
                threadsBean.setThreadCpuTimeEnabled(isThreadCpuTimeEnabled);
            }
        }
    }

    private void handle(Terminal.Signal signal) {
        int prevw = this.size.getColumns();
        this.size.copy(this.terminal.getSize());
        try {
            if (this.size.getColumns() < prevw) {
                this.display.clear();
            }
            this.display();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private List<Map<String, Comparable<?>>> infos() {
        long ctime = ManagementFactory.getRuntimeMXBean().getUptime();
        Long ptime = this.previous.computeIfAbsent(-1L, id -> new HashMap()).put(STAT_UPTIME, ctime);
        long delta = ptime != null ? ctime - ptime : 0L;
        ThreadMXBean threadsBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] infos = threadsBean.dumpAllThreads(false, false);
        ArrayList threads = new ArrayList();
        for (ThreadInfo ti : infos) {
            HashMap<String, Object> t = new HashMap<String, Object>();
            t.put(STAT_TID, ti.getThreadId());
            t.put(STAT_NAME, ti.getThreadName());
            t.put(STAT_STATE, (Object)ti.getThreadState());
            if (threadsBean.isThreadContentionMonitoringEnabled()) {
                t.put(STAT_BLOCKED_TIME, ti.getBlockedTime());
                t.put(STAT_BLOCKED_COUNT, ti.getBlockedCount());
                t.put(STAT_WAITED_TIME, ti.getWaitedTime());
                t.put(STAT_WAITED_COUNT, ti.getWaitedCount());
            }
            t.put(STAT_LOCK_NAME, ti.getLockName());
            t.put(STAT_LOCK_OWNER_ID, ti.getLockOwnerId());
            t.put(STAT_LOCK_OWNER_NAME, ti.getLockOwnerName());
            if (threadsBean.isThreadCpuTimeSupported() && threadsBean.isThreadCpuTimeEnabled()) {
                long tid = ti.getThreadId();
                long t1 = threadsBean.getThreadCpuTime(tid);
                long t0 = this.previous.computeIfAbsent(tid, id -> new HashMap()).getOrDefault(STAT_CPU_TIME, t1);
                t.put(STAT_CPU_TIME, t1);
                t.put(STAT_CPU_TIME_PERC, delta != 0L ? (double)(t1 - t0) / ((double)delta * 1000000.0) : 0.0);
                t1 = threadsBean.getThreadUserTime(tid);
                t0 = this.previous.computeIfAbsent(tid, id -> new HashMap()).getOrDefault(STAT_USER_TIME, t1);
                t.put(STAT_USER_TIME, t1);
                t.put(STAT_USER_TIME_PERC, delta != 0L ? (double)(t1 - t0) / ((double)delta * 1000000.0) : 0.0);
            }
            threads.add(t);
        }
        return threads;
    }

    private void align(AttributedStringBuilder sb, String val, int width, Align align) {
        if (align == Align.Left) {
            sb.append(val);
            for (int i = 0; i < width - val.length(); ++i) {
                sb.append(' ');
            }
        } else {
            for (int i = 0; i < width - val.length(); ++i) {
                sb.append(' ');
            }
            sb.append(val);
        }
    }

    private synchronized void display() throws IOException {
        List<String> cstats;
        long now = System.currentTimeMillis();
        this.display.resize(this.size.getRows(), this.size.getColumns());
        ArrayList<AttributedString> lines = new ArrayList<AttributedString>();
        AttributedStringBuilder sb = new AttributedStringBuilder(this.size.getColumns());
        sb.style(sb.style().bold());
        sb.append("ttop");
        sb.style(sb.style().boldOff());
        sb.append(" - ");
        sb.append(String.format("%8tT", new Date()));
        sb.append(".");
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        String osinfo = "OS: " + os.getName() + " " + os.getVersion() + ", " + os.getArch() + ", " + os.getAvailableProcessors() + " cpus.";
        if (sb.length() + 1 + osinfo.length() < this.size.getColumns()) {
            sb.append(" ");
        } else {
            lines.add(sb.toAttributedString());
            sb.setLength(0);
        }
        sb.append(osinfo);
        ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
        String clsinfo = "Classes: " + cl.getLoadedClassCount() + " loaded, " + cl.getUnloadedClassCount() + " unloaded, " + cl.getTotalLoadedClassCount() + " loaded total.";
        if (sb.length() + 1 + clsinfo.length() < this.size.getColumns()) {
            sb.append(" ");
        } else {
            lines.add(sb.toAttributedString());
            sb.setLength(0);
        }
        sb.append(clsinfo);
        ThreadMXBean th = ManagementFactory.getThreadMXBean();
        String thinfo = "Threads: " + th.getThreadCount() + ", peak: " + th.getPeakThreadCount() + ", started: " + th.getTotalStartedThreadCount() + ".";
        if (sb.length() + 1 + thinfo.length() < this.size.getColumns()) {
            sb.append(" ");
        } else {
            lines.add(sb.toAttributedString());
            sb.setLength(0);
        }
        sb.append(thinfo);
        MemoryMXBean me = ManagementFactory.getMemoryMXBean();
        String meinfo = "Memory: heap: " + TTop.memory(me.getHeapMemoryUsage().getUsed(), me.getHeapMemoryUsage().getMax()) + ", non heap: " + TTop.memory(me.getNonHeapMemoryUsage().getUsed(), me.getNonHeapMemoryUsage().getMax()) + ".";
        if (sb.length() + 1 + meinfo.length() < this.size.getColumns()) {
            sb.append(" ");
        } else {
            lines.add(sb.toAttributedString());
            sb.setLength(0);
        }
        sb.append(meinfo);
        StringBuilder sbc = new StringBuilder();
        sbc.append("GC: ");
        boolean first = true;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (first) {
                first = false;
            } else {
                sbc.append(", ");
            }
            long count = gc.getCollectionCount();
            long time = gc.getCollectionTime();
            sbc.append(gc.getName()).append(": ").append(count).append(" col. / ").append(String.format("%d", time / 1000L)).append(".").append(String.format("%03d", time % 1000L)).append(" s");
        }
        sbc.append(".");
        if (sb.length() + 1 + sbc.length() < this.size.getColumns()) {
            sb.append(" ");
        } else {
            lines.add(sb.toAttributedString());
            sb.setLength(0);
        }
        sb.append(sbc);
        lines.add(sb.toAttributedString());
        sb.setLength(0);
        lines.add(sb.toAttributedString());
        List<Map<String, Comparable<?>>> threads = this.infos();
        Collections.sort(threads, this.comparator);
        int nb = Math.min(this.size.getRows() - lines.size() - 2, this.nthreads > 0 ? this.nthreads : threads.size());
        List values = threads.subList(0, nb).stream().map(thread -> this.stats.stream().collect(Collectors.toMap(Function.identity(), key -> this.columns.get((Object)key).format.apply(thread.get(key))))).collect(Collectors.toList());
        for (String key : this.stats) {
            int width = values.stream().mapToInt(map -> ((String)map.get(key)).length()).max().orElse(0);
            this.widths.put(key, Math.max(this.columns.get((Object)key).header.length(), Math.max(width, this.widths.getOrDefault(key, 0))));
        }
        if (this.widths.values().stream().mapToInt(Integer::intValue).sum() + this.stats.size() - 1 < this.size.getColumns()) {
            cstats = this.stats;
        } else {
            cstats = new ArrayList<String>();
            int sz = 0;
            for (String stat : this.stats) {
                int nsz = sz;
                if (nsz > 0) {
                    ++nsz;
                }
                if ((nsz += this.widths.get(stat).intValue()) >= this.size.getColumns()) break;
                sz = nsz;
                cstats.add(stat);
            }
        }
        for (String key : cstats) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            Column col = this.columns.get(key);
            this.align(sb, col.header, this.widths.get(key), col.align);
        }
        lines.add(sb.toAttributedString());
        sb.setLength(0);
        for (int i = 0; i < nb; ++i) {
            Map<String, Comparable<?>> thread2 = threads.get(i);
            long tid = (Long)thread2.get(STAT_TID);
            for (String key : cstats) {
                long last;
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                Comparable<?> cur = thread2.get(key);
                Comparable<?> prv = this.previous.computeIfAbsent(tid, id -> new HashMap()).put(key, cur);
                if (prv != null && !prv.equals(cur)) {
                    this.changes.computeIfAbsent(tid, id -> new HashMap()).put(key, now);
                    last = now;
                } else {
                    last = this.changes.computeIfAbsent(tid, id -> new HashMap()).getOrDefault(key, 0L);
                }
                long fade = this.delay * 24L;
                if (now - last < fade) {
                    int r = (int)((now - last) / (fade / 24L));
                    sb.style(sb.style().foreground(255 - r).background(9));
                }
                this.align(sb, (String)((Map)values.get(i)).get(key), this.widths.get(key), this.columns.get((Object)key).align);
                sb.style(sb.style().backgroundOff().foregroundOff());
            }
            lines.add(sb.toAttributedString());
            sb.setLength(0);
        }
        this.display.update(lines, 0);
    }

    private Comparator<Map<String, Comparable<?>>> buildComparator(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            sort = Collections.singletonList(STAT_TID);
        }
        Comparator<Map> comparator = null;
        for (String key : sort) {
            boolean asc;
            String fkey;
            if (key.startsWith("+")) {
                fkey = key.substring(1);
                asc = true;
            } else if (key.startsWith("-")) {
                fkey = key.substring(1);
                asc = false;
            } else {
                fkey = key;
                asc = true;
            }
            if (!this.columns.containsKey(fkey)) {
                throw new IllegalArgumentException("Unsupported sort key: " + fkey);
            }
            Comparator<Map> comp = Comparator.comparing(m -> (Comparable)m.get(fkey));
            if (asc) {
                comp = comp.reversed();
            }
            if (comparator != null) {
                comparator = comparator.thenComparing(comp);
                continue;
            }
            comparator = comp;
        }
        return comparator;
    }

    private void register(String name, Align align, String header, Function<Object, String> format) {
        this.columns.put(name, new Column(name, align, header, format));
    }

    private static String nanos(long nanos) {
        return TTop.millis(nanos / 1000000L);
    }

    private static String millis(long millis) {
        long secs = millis / 1000L;
        millis %= 1000L;
        long mins = secs / 60L;
        secs %= 60L;
        long hours = mins / 60L;
        mins %= 60L;
        if (hours > 0L) {
            return String.format("%d:%02d:%02d.%03d", hours, mins, secs, millis);
        }
        if (mins > 0L) {
            return String.format("%d:%02d.%03d", mins, secs, millis);
        }
        return String.format("%d.%03d", secs, millis);
    }

    private static Function<Object, String> padcut(int nb) {
        return o -> TTop.padcut(o.toString(), nb);
    }

    private static String padcut(String str, int nb) {
        if (str.length() <= nb) {
            StringBuilder sb = new StringBuilder(nb);
            sb.append(str);
            while (sb.length() < nb) {
                sb.append(' ');
            }
            return sb.toString();
        }
        return str.substring(0, nb - 3) + "...";
    }

    private static String memory(long cur, long max) {
        if (max > 0L) {
            String smax = TTop.humanReadableByteCount(max, false);
            String cmax = TTop.humanReadableByteCount(cur, false);
            StringBuilder sb = new StringBuilder(smax.length() * 2 + 3);
            for (int i = cmax.length(); i < smax.length(); ++i) {
                sb.append(' ');
            }
            sb.append(cmax).append(" / ").append(smax);
            return sb.toString();
        }
        return TTop.humanReadableByteCount(cur, false);
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit;
        int n = unit = si ? 1000 : 1024;
        if (bytes < 1024L) {
            return bytes + " B";
        }
        int exp = (int)(Math.log(bytes) / Math.log(1024.0));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", (double)bytes / Math.pow(unit, exp), pre);
    }

    private void checkInterrupted() throws InterruptedException {
        Thread.yield();
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private void bindKeys(KeyMap<Operation> map) {
        map.bind(Operation.HELP, "h", "?");
        map.bind(Operation.EXIT, "q", ":q", "Q", ":Q", "ZZ");
        map.bind(Operation.INCREASE_DELAY, (CharSequence)"+");
        map.bind(Operation.DECREASE_DELAY, (CharSequence)"-");
        map.bind(Operation.CLEAR, (CharSequence)KeyMap.ctrl('L'));
        map.bind(Operation.REVERSE, (CharSequence)"r");
    }

    public static enum Align {
        Left,
        Right;

    }

    public static enum Operation {
        INCREASE_DELAY,
        DECREASE_DELAY,
        HELP,
        EXIT,
        CLEAR,
        REVERSE;

    }

    private static class Column {
        final String name;
        final Align align;
        final String header;
        final Function<Object, String> format;

        Column(String name, Align align, String header, Function<Object, String> format) {
            this.name = name;
            this.align = align;
            this.header = header;
            this.format = format;
        }
    }
}

