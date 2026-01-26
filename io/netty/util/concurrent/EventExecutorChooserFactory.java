/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AutoScalingEventExecutorChooserFactory;
import io.netty.util.concurrent.EventExecutor;
import java.util.List;

public interface EventExecutorChooserFactory {
    public EventExecutorChooser newChooser(EventExecutor[] var1);

    public static interface ObservableEventExecutorChooser
    extends EventExecutorChooser {
        public int activeExecutorCount();

        public List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> executorUtilizations();
    }

    public static interface EventExecutorChooser {
        public EventExecutor next();
    }
}

