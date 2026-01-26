/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MingwSupport {
    private final String sttyCommand;
    private final String ttyCommand;
    private final Pattern columnsPatterns;

    public MingwSupport() {
        String tty = null;
        String stty = null;
        String path = System.getenv("PATH");
        if (path != null) {
            String[] paths;
            for (String p : paths = path.split(File.pathSeparator)) {
                File ttyFile = new File(p, "tty.exe");
                if (tty == null && ttyFile.canExecute()) {
                    tty = ttyFile.getAbsolutePath();
                }
                File sttyFile = new File(p, "stty.exe");
                if (stty != null || !sttyFile.canExecute()) continue;
                stty = sttyFile.getAbsolutePath();
            }
        }
        if (tty == null) {
            tty = "tty.exe";
        }
        if (stty == null) {
            stty = "stty.exe";
        }
        this.ttyCommand = tty;
        this.sttyCommand = stty;
        this.columnsPatterns = Pattern.compile("\\bcolumns\\s+(\\d+)\\b");
    }

    public String getConsoleName(boolean stdout) {
        block3: {
            try {
                Process p = new ProcessBuilder(this.ttyCommand).redirectInput(this.getRedirect(stdout ? FileDescriptor.out : FileDescriptor.err)).start();
                String result = MingwSupport.waitAndCapture(p);
                if (p.exitValue() == 0) {
                    return result.trim();
                }
            }
            catch (Throwable t) {
                if (!"java.lang.reflect.InaccessibleObjectException".equals(t.getClass().getName())) break block3;
                System.err.println("MINGW support requires --add-opens java.base/java.lang=ALL-UNNAMED");
            }
        }
        return null;
    }

    public int getTerminalWidth(String name) {
        try {
            Process p = new ProcessBuilder(this.sttyCommand, "-F", name, "-a").start();
            String result = MingwSupport.waitAndCapture(p);
            if (p.exitValue() != 0) {
                throw new IOException("Error executing '" + this.sttyCommand + "': " + result);
            }
            Matcher matcher = this.columnsPatterns.matcher(result);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
            throw new IOException("Unable to parse columns");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String waitAndCapture(Process p) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (InputStream in = p.getInputStream();
             InputStream err = p.getErrorStream();){
            int c;
            while ((c = in.read()) != -1) {
                bout.write(c);
            }
            while ((c = err.read()) != -1) {
                bout.write(c);
            }
            p.waitFor();
        }
        return bout.toString();
    }

    private ProcessBuilder.Redirect getRedirect(FileDescriptor fd) throws ReflectiveOperationException {
        Class<?> rpi = Class.forName("java.lang.ProcessBuilder$RedirectPipeImpl");
        Constructor<?> cns = rpi.getDeclaredConstructor(new Class[0]);
        cns.setAccessible(true);
        ProcessBuilder.Redirect input = (ProcessBuilder.Redirect)cns.newInstance(new Object[0]);
        Field f = rpi.getDeclaredField("fd");
        f.setAccessible(true);
        f.set(input, fd);
        return input;
    }
}

