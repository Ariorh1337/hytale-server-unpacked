/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl.completer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

@Deprecated
public class FileNameCompleter
implements Completer {
    @Override
    public void complete(LineReader reader, ParsedLine commandLine, List<Candidate> candidates) {
        Path current;
        String curBuf;
        String sep;
        assert (commandLine != null);
        assert (candidates != null);
        String buffer = commandLine.word().substring(0, commandLine.wordCursor());
        int lastSep = buffer.lastIndexOf(sep = this.getUserDir().getFileSystem().getSeparator());
        if (lastSep >= 0) {
            curBuf = buffer.substring(0, lastSep + 1);
            current = curBuf.startsWith("~") ? (curBuf.startsWith("~" + sep) ? this.getUserHome().resolve(curBuf.substring(2)) : this.getUserHome().getParent().resolve(curBuf.substring(1))) : this.getUserDir().resolve(curBuf);
        } else {
            curBuf = "";
            current = this.getUserDir();
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(current, this::accept);){
            directoryStream.forEach(p -> {
                String value = curBuf + p.getFileName().toString();
                if (Files.isDirectory(p, new LinkOption[0])) {
                    candidates.add(new Candidate(value + (reader.isSet(LineReader.Option.AUTO_PARAM_SLASH) ? sep : ""), this.getDisplay(reader.getTerminal(), (Path)p), null, null, reader.isSet(LineReader.Option.AUTO_REMOVE_SLASH) ? sep : null, null, false));
                } else {
                    candidates.add(new Candidate(value, this.getDisplay(reader.getTerminal(), (Path)p), null, null, null, null, true));
                }
            });
        }
        catch (IOException iOException) {
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

    protected String getDisplay(Terminal terminal, Path p) {
        String name = p.getFileName().toString();
        if (Files.isDirectory(p, new LinkOption[0])) {
            AttributedStringBuilder sb = new AttributedStringBuilder();
            sb.styled(AttributedStyle.BOLD.foreground(1), (CharSequence)name);
            sb.append("/");
            name = sb.toAnsi(terminal);
        } else if (Files.isSymbolicLink(p)) {
            AttributedStringBuilder sb = new AttributedStringBuilder();
            sb.styled(AttributedStyle.BOLD.foreground(1), (CharSequence)name);
            sb.append("@");
            name = sb.toAnsi(terminal);
        }
        return name;
    }
}

