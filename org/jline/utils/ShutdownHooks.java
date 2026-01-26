/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jline.utils.Log;

public final class ShutdownHooks {
    private static final List<Task> tasks = new ArrayList<Task>();
    private static Thread hook;

    public static synchronized <T extends Task> T add(T task) {
        Objects.requireNonNull(task);
        if (hook == null) {
            hook = ShutdownHooks.addHook(new Thread("JLine Shutdown Hook"){

                @Override
                public void run() {
                    ShutdownHooks.runTasks();
                }
            });
        }
        Log.debug("Adding shutdown-hook task: ", task);
        tasks.add(task);
        return task;
    }

    private static synchronized void runTasks() {
        Log.debug("Running all shutdown-hook tasks");
        for (Task task : tasks.toArray(new Task[tasks.size()])) {
            Log.debug("Running task: ", task);
            try {
                task.run();
            }
            catch (Throwable e) {
                Log.warn("Task failed", e);
            }
        }
        tasks.clear();
    }

    private static Thread addHook(Thread thread) {
        Log.debug("Registering shutdown-hook: ", thread);
        Runtime.getRuntime().addShutdownHook(thread);
        return thread;
    }

    public static synchronized void remove(Task task) {
        Objects.requireNonNull(task);
        if (hook == null) {
            return;
        }
        tasks.remove(task);
        if (tasks.isEmpty()) {
            ShutdownHooks.removeHook(hook);
            hook = null;
        }
    }

    private static void removeHook(Thread thread) {
        Log.debug("Removing shutdown-hook: ", thread);
        try {
            Runtime.getRuntime().removeShutdownHook(thread);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    public static interface Task {
        public void run() throws Exception;
    }
}

