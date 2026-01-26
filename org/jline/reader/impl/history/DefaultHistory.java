/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl.history;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.ReaderUtils;
import org.jline.utils.Log;

public class DefaultHistory
implements History {
    public static final int DEFAULT_HISTORY_SIZE = 500;
    public static final int DEFAULT_HISTORY_FILE_SIZE = 10000;
    private final LinkedList<History.Entry> items = new LinkedList();
    private LineReader reader;
    private Map<String, HistoryFileData> historyFiles = new HashMap<String, HistoryFileData>();
    private int offset = 0;
    private int index = 0;

    public DefaultHistory() {
    }

    public DefaultHistory(LineReader reader) {
        this.attach(reader);
    }

    private Path getPath() {
        Object obj;
        Object object = obj = this.reader != null ? this.reader.getVariables().get("history-file") : null;
        if (obj instanceof Path) {
            return (Path)obj;
        }
        if (obj instanceof File) {
            return ((File)obj).toPath();
        }
        if (obj != null) {
            return Paths.get(obj.toString(), new String[0]);
        }
        return null;
    }

    @Override
    public void attach(LineReader reader) {
        if (this.reader != reader) {
            this.reader = reader;
            try {
                this.load();
            }
            catch (IOException | IllegalArgumentException e) {
                Log.warn("Failed to load history", e);
            }
        }
    }

    @Override
    public void load() throws IOException {
        block13: {
            Path path = this.getPath();
            if (path != null) {
                try {
                    if (!Files.exists(path, new LinkOption[0])) break block13;
                    Log.trace("Loading history from: ", path);
                    this.internalClear();
                    boolean hasErrors = false;
                    try (BufferedReader reader = Files.newBufferedReader(path);){
                        List lines = reader.lines().collect(Collectors.toList());
                        for (String line : lines) {
                            try {
                                this.addHistoryLine(path, line);
                            }
                            catch (IllegalArgumentException e) {
                                Log.debug("Skipping invalid history line: " + line, e);
                                hasErrors = true;
                            }
                        }
                    }
                    this.setHistoryFileData(path, new HistoryFileData(this.items.size(), this.offset + this.items.size()));
                    this.maybeResize();
                    if (hasErrors) {
                        Log.info("History file contained errors, rewriting with valid entries");
                        this.write(path, false);
                    }
                }
                catch (IOException e) {
                    Log.debug("Failed to load history; clearing", e);
                    this.internalClear();
                    throw e;
                }
            }
        }
    }

    @Override
    public void read(Path file, boolean checkDuplicates) throws IOException {
        block13: {
            Path path;
            Path path2 = path = file != null ? file : this.getPath();
            if (path != null) {
                try {
                    if (!Files.exists(path, new LinkOption[0])) break block13;
                    Log.trace("Reading history from: ", path);
                    boolean hasErrors = false;
                    try (BufferedReader reader = Files.newBufferedReader(path);){
                        List lines = reader.lines().collect(Collectors.toList());
                        for (String line : lines) {
                            try {
                                this.addHistoryLine(path, line, checkDuplicates);
                            }
                            catch (IllegalArgumentException e) {
                                Log.debug("Skipping invalid history line: " + line, e);
                                hasErrors = true;
                            }
                        }
                    }
                    this.setHistoryFileData(path, new HistoryFileData(this.items.size(), this.offset + this.items.size()));
                    this.maybeResize();
                    if (hasErrors) {
                        Log.info("History file contained errors, rewriting with valid entries");
                        this.write(path, false);
                    }
                }
                catch (IOException e) {
                    Log.debug("Failed to read history; clearing", e);
                    this.internalClear();
                    throw e;
                }
            }
        }
    }

    private String doHistoryFileDataKey(Path path) {
        return path != null ? path.toAbsolutePath().toString() : null;
    }

    private HistoryFileData getHistoryFileData(Path path) {
        String key = this.doHistoryFileDataKey(path);
        if (!this.historyFiles.containsKey(key)) {
            this.historyFiles.put(key, new HistoryFileData());
        }
        return this.historyFiles.get(key);
    }

    private void setHistoryFileData(Path path, HistoryFileData historyFileData) {
        this.historyFiles.put(this.doHistoryFileDataKey(path), historyFileData);
    }

    private boolean isLineReaderHistory(Path path) throws IOException {
        Path lrp = this.getPath();
        if (lrp == null) {
            return path == null;
        }
        return Files.isSameFile(lrp, path);
    }

    private void setLastLoaded(Path path, int lastloaded) {
        this.getHistoryFileData(path).setLastLoaded(lastloaded);
    }

    private void setEntriesInFile(Path path, int entriesInFile) {
        this.getHistoryFileData(path).setEntriesInFile(entriesInFile);
    }

    private void incEntriesInFile(Path path, int amount) {
        this.getHistoryFileData(path).incEntriesInFile(amount);
    }

    private int getLastLoaded(Path path) {
        return this.getHistoryFileData(path).getLastLoaded();
    }

    private int getEntriesInFile(Path path) {
        return this.getHistoryFileData(path).getEntriesInFile();
    }

    protected void addHistoryLine(Path path, String line) {
        this.addHistoryLine(path, line, false);
    }

    protected void addHistoryLine(Path path, String line, boolean checkDuplicates) {
        if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
            Instant time;
            int idx = line.indexOf(58);
            String badHistoryFileSyntax = "Bad history file syntax! The history file `" + path + "` may be an older history: please remove it or use a different history file.";
            if (idx < 0) {
                throw new IllegalArgumentException(badHistoryFileSyntax);
            }
            try {
                time = Instant.ofEpochMilli(Long.parseLong(line.substring(0, idx)));
            }
            catch (NumberFormatException | DateTimeException e) {
                throw new IllegalArgumentException(badHistoryFileSyntax);
            }
            String unescaped = DefaultHistory.unescape(line.substring(idx + 1));
            this.internalAdd(time, unescaped, checkDuplicates);
        } else {
            this.internalAdd(Instant.now(), DefaultHistory.unescape(line), checkDuplicates);
        }
    }

    @Override
    public void purge() throws IOException {
        this.internalClear();
        Path path = this.getPath();
        if (path != null) {
            Log.trace("Purging history from: ", path);
            Files.deleteIfExists(path);
        }
    }

    @Override
    public void write(Path file, boolean incremental) throws IOException {
        Path path;
        Path path2 = path = file != null ? file : this.getPath();
        if (path != null && Files.exists(path, new LinkOption[0])) {
            Files.deleteIfExists(path);
        }
        this.internalWrite(path, incremental ? this.getLastLoaded(path) : 0);
    }

    @Override
    public void append(Path file, boolean incremental) throws IOException {
        this.internalWrite(file != null ? file : this.getPath(), incremental ? this.getLastLoaded(file) : 0);
    }

    @Override
    public void save() throws IOException {
        this.internalWrite(this.getPath(), this.getLastLoaded(this.getPath()));
    }

    private void internalWrite(Path path, int from) throws IOException {
        if (path != null) {
            Log.trace("Saving history to: ", path);
            Path parent = path.toAbsolutePath().getParent();
            if (!Files.exists(parent, new LinkOption[0])) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path.toAbsolutePath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE);){
                for (History.Entry entry : this.items.subList(from, this.items.size())) {
                    if (!this.isPersistable(entry)) continue;
                    writer.append(this.format(entry));
                }
            }
            this.incEntriesInFile(path, this.items.size() - from);
            int max = ReaderUtils.getInt(this.reader, "history-file-size", 10000);
            if (this.getEntriesInFile(path) > max + max / 4) {
                this.trimHistory(path, max);
            }
        }
        this.setLastLoaded(path, this.items.size());
    }

    protected void trimHistory(Path path, int max) throws IOException {
        Log.trace("Trimming history path: ", path);
        LinkedList<History.Entry> allItems = new LinkedList<History.Entry>();
        try (BufferedReader historyFileReader = Files.newBufferedReader(path);){
            List lines = historyFileReader.lines().collect(Collectors.toList());
            for (String l : lines) {
                try {
                    if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
                        int idx = l.indexOf(58);
                        if (idx < 0) {
                            Log.debug("Skipping invalid history line: " + l);
                            continue;
                        }
                        try {
                            Instant time = Instant.ofEpochMilli(Long.parseLong(l.substring(0, idx)));
                            String line = DefaultHistory.unescape(l.substring(idx + 1));
                            allItems.add(this.createEntry(allItems.size(), time, line));
                        }
                        catch (NumberFormatException | DateTimeException e) {
                            Log.debug("Skipping invalid history timestamp: " + l);
                        }
                        continue;
                    }
                    allItems.add(this.createEntry(allItems.size(), Instant.now(), DefaultHistory.unescape(l)));
                }
                catch (Exception e) {
                    Log.debug("Skipping invalid history line: " + l, e);
                }
            }
        }
        List<History.Entry> trimmedItems = DefaultHistory.doTrimHistory(allItems, max);
        Path temp = Files.createTempFile(path.toAbsolutePath().getParent(), path.getFileName().toString(), ".tmp", new FileAttribute[0]);
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardOpenOption.WRITE);){
            for (History.Entry entry : trimmedItems) {
                writer.append(this.format(entry));
            }
        }
        Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
        if (this.isLineReaderHistory(path)) {
            this.internalClear();
            this.offset = trimmedItems.get(0).index();
            this.items.addAll(trimmedItems);
            this.setHistoryFileData(path, new HistoryFileData(this.items.size(), this.items.size()));
        } else {
            this.setEntriesInFile(path, allItems.size());
        }
        this.maybeResize();
    }

    protected EntryImpl createEntry(int index, Instant time, String line) {
        return new EntryImpl(index, time, line);
    }

    private void internalClear() {
        this.offset = 0;
        this.index = 0;
        this.historyFiles = new HashMap<String, HistoryFileData>();
        this.items.clear();
    }

    static List<History.Entry> doTrimHistory(List<History.Entry> allItems, int max) {
        for (int idx = 0; idx < allItems.size(); ++idx) {
            int ridx = allItems.size() - idx - 1;
            String line = allItems.get(ridx).line().trim();
            ListIterator<History.Entry> iterator = allItems.listIterator(ridx);
            while (iterator.hasPrevious()) {
                String l = iterator.previous().line();
                if (!line.equals(l.trim())) continue;
                iterator.remove();
            }
        }
        while (allItems.size() > max) {
            allItems.remove(0);
        }
        int index = allItems.get(allItems.size() - 1).index() - allItems.size() + 1;
        ArrayList<History.Entry> out = new ArrayList<History.Entry>();
        for (History.Entry e : allItems) {
            out.add(new EntryImpl(index++, e.time(), e.line()));
        }
        return out;
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public int index() {
        return this.offset + this.index;
    }

    @Override
    public int first() {
        return this.offset;
    }

    @Override
    public int last() {
        return this.offset + this.items.size() - 1;
    }

    private String format(History.Entry entry) {
        if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
            return entry.time().toEpochMilli() + ":" + DefaultHistory.escape(entry.line()) + "\n";
        }
        return DefaultHistory.escape(entry.line()) + "\n";
    }

    @Override
    public String get(int index) {
        int idx = index - this.offset;
        if (idx >= this.items.size() || idx < 0) {
            throw new IllegalArgumentException("IndexOutOfBounds: Index:" + idx + ", Size:" + this.items.size());
        }
        return this.items.get(idx).line();
    }

    @Override
    public void add(Instant time, String line) {
        Objects.requireNonNull(time);
        Objects.requireNonNull(line);
        if (ReaderUtils.getBoolean(this.reader, "disable-history", false)) {
            return;
        }
        if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_SPACE) && line.startsWith(" ")) {
            return;
        }
        if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_REDUCE_BLANKS)) {
            line = line.trim();
        }
        if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_DUPS) && !this.items.isEmpty() && line.equals(this.items.getLast().line())) {
            return;
        }
        if (this.matchPatterns(ReaderUtils.getString(this.reader, "history-ignore", ""), line)) {
            return;
        }
        this.internalAdd(time, line);
        if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_INCREMENTAL)) {
            try {
                this.save();
            }
            catch (IOException e) {
                Log.warn("Failed to save history", e);
            }
        }
    }

    protected boolean matchPatterns(String patterns, String line) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patterns.length(); ++i) {
            char ch = patterns.charAt(i);
            if (ch == '\\') {
                ch = patterns.charAt(++i);
                sb.append(ch);
                continue;
            }
            if (ch == ':') {
                sb.append('|');
                continue;
            }
            if (ch == '*') {
                sb.append('.').append('*');
                continue;
            }
            sb.append(ch);
        }
        return line.matches(sb.toString());
    }

    protected void internalAdd(Instant time, String line) {
        this.internalAdd(time, line, false);
    }

    protected void internalAdd(Instant time, String line, boolean checkDuplicates) {
        EntryImpl entry = new EntryImpl(this.offset + this.items.size(), time, line);
        if (checkDuplicates) {
            for (History.Entry e : this.items) {
                if (!e.line().trim().equals(line.trim())) continue;
                return;
            }
        }
        this.items.add(entry);
        this.maybeResize();
    }

    private void maybeResize() {
        while (this.size() > ReaderUtils.getInt(this.reader, "history-size", 500)) {
            this.items.removeFirst();
            for (HistoryFileData hfd : this.historyFiles.values()) {
                hfd.decLastLoaded();
            }
            ++this.offset;
        }
        this.index = this.size();
    }

    @Override
    public ListIterator<History.Entry> iterator(int index) {
        return this.items.listIterator(index - this.offset);
    }

    @Override
    public Spliterator<History.Entry> spliterator() {
        return this.items.spliterator();
    }

    @Override
    public void resetIndex() {
        this.index = Math.min(this.index, this.items.size());
    }

    @Override
    public boolean moveToLast() {
        int lastEntry = this.size() - 1;
        if (lastEntry >= 0 && lastEntry != this.index) {
            this.index = this.size() - 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveTo(int index) {
        if ((index -= this.offset) >= 0 && index < this.size()) {
            this.index = index;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToFirst() {
        if (this.size() > 0 && this.index != 0) {
            this.index = 0;
            return true;
        }
        return false;
    }

    @Override
    public void moveToEnd() {
        this.index = this.size();
    }

    @Override
    public String current() {
        if (this.index >= this.size()) {
            return "";
        }
        return this.items.get(this.index).line();
    }

    @Override
    public boolean previous() {
        if (this.index <= 0) {
            return false;
        }
        --this.index;
        return true;
    }

    @Override
    public boolean next() {
        if (this.index >= this.size()) {
            return false;
        }
        ++this.index;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (History.Entry e : this) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        block5: for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\n': {
                    sb.append('\\');
                    sb.append('n');
                    continue block5;
                }
                case '\r': {
                    sb.append('\\');
                    sb.append('r');
                    continue block5;
                }
                case '\\': {
                    sb.append('\\');
                    sb.append('\\');
                    continue block5;
                }
                default: {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        block3: for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\\': {
                    ch = s.charAt(++i);
                    if (ch == 'n') {
                        sb.append('\n');
                        continue block3;
                    }
                    if (ch == 'r') {
                        sb.append('\r');
                        continue block3;
                    }
                    sb.append(ch);
                    continue block3;
                }
                default: {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    private static class HistoryFileData {
        private int lastLoaded = 0;
        private int entriesInFile = 0;

        public HistoryFileData() {
        }

        public HistoryFileData(int lastLoaded, int entriesInFile) {
            this.lastLoaded = lastLoaded;
            this.entriesInFile = entriesInFile;
        }

        public int getLastLoaded() {
            return this.lastLoaded;
        }

        public void setLastLoaded(int lastLoaded) {
            this.lastLoaded = lastLoaded;
        }

        public void decLastLoaded() {
            --this.lastLoaded;
            if (this.lastLoaded < 0) {
                this.lastLoaded = 0;
            }
        }

        public int getEntriesInFile() {
            return this.entriesInFile;
        }

        public void setEntriesInFile(int entriesInFile) {
            this.entriesInFile = entriesInFile;
        }

        public void incEntriesInFile(int amount) {
            this.entriesInFile += amount;
        }
    }

    protected static class EntryImpl
    implements History.Entry {
        private final int index;
        private final Instant time;
        private final String line;

        public EntryImpl(int index, Instant time, String line) {
            this.index = index;
            this.time = time;
            this.line = line;
        }

        @Override
        public int index() {
            return this.index;
        }

        @Override
        public Instant time() {
            return this.time;
        }

        @Override
        public String line() {
            return this.line;
        }

        public String toString() {
            return String.format("%d: %s", this.index, this.line);
        }
    }
}

