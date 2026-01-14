package io.netty.util.concurrent;

import java.util.List;

public interface EventExecutorChooserFactory {
   EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] var1);

   interface EventExecutorChooser {
      EventExecutor next();
   }

   interface ObservableEventExecutorChooser extends EventExecutorChooserFactory.EventExecutorChooser {
      int activeExecutorCount();

      List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> executorUtilizations();
   }
}
