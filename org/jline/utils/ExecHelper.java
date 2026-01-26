/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public final class ExecHelper {
    private ExecHelper() {
    }

    public static String exec(boolean redirectInput, String ... cmd) throws IOException {
        Objects.requireNonNull(cmd);
        try {
            Log.trace("Running: ", cmd);
            ProcessBuilder pb = new ProcessBuilder(cmd);
            if (OSUtils.IS_AIX) {
                Map<String, String> env = pb.environment();
                env.put("PATH", "/opt/freeware/bin:" + env.get("PATH"));
                env.put("LANG", "C");
                env.put("LC_ALL", "C");
            }
            if (redirectInput) {
                pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            }
            Process p = pb.start();
            String result = ExecHelper.waitAndCapture(p);
            Log.trace("Result: ", result);
            if (p.exitValue() != 0) {
                if (result.endsWith("\n")) {
                    result = result.substring(0, result.length() - 1);
                }
                throw new IOException("Error executing '" + String.join((CharSequence)" ", cmd) + "': " + result);
            }
            return result;
        }
        catch (InterruptedException e) {
            throw (IOException)new InterruptedIOException("Command interrupted").initCause(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String waitAndCapture(Process p) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        InputStream in = null;
        InputStream err = null;
        OutputStream out = null;
        try {
            int c;
            in = p.getInputStream();
            while ((c = in.read()) != -1) {
                bout.write(c);
            }
            err = p.getErrorStream();
            while ((c = err.read()) != -1) {
                bout.write(c);
            }
            out = p.getOutputStream();
            p.waitFor();
        }
        catch (Throwable throwable) {
            ExecHelper.close(in, out, err);
            throw throwable;
        }
        ExecHelper.close(in, out, err);
        return bout.toString();
    }

    private static void close(Closeable ... closeables) {
        for (Closeable c : closeables) {
            if (c == null) continue;
            try {
                c.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

