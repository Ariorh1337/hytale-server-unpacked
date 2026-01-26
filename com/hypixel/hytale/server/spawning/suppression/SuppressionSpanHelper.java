/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.suppression;

import com.hypixel.hytale.server.spawning.suppression.component.ChunkSuppressionEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SuppressionSpanHelper {
    private static final ThreadLocal<ArrayDeque<Span>> SPAN_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private final List<Span> optimisedSuppressionSpans = new ObjectArrayList<Span>();
    private int currentSpanIndex = 0;

    public void optimiseSuppressedSpans(int roleIndex, @Nullable ChunkSuppressionEntry entry) {
        if (entry == null) {
            return;
        }
        ArrayDeque<Span> spanPool = SPAN_POOL.get();
        List<ChunkSuppressionEntry.SuppressionSpan> suppressionSpans = entry.getSuppressionSpans();
        Span initialSpan = SuppressionSpanHelper.allocateSpan(spanPool);
        initialSpan.init(0, 0);
        this.optimisedSuppressionSpans.add(initialSpan);
        boolean matchedRole = false;
        for (ChunkSuppressionEntry.SuppressionSpan suppressionSpan : suppressionSpans) {
            if (!suppressionSpan.includesRole(roleIndex)) continue;
            matchedRole = true;
            int minY = suppressionSpan.getMinY();
            int maxY = suppressionSpan.getMaxY();
            Span latestSpan = this.optimisedSuppressionSpans.getLast();
            if (latestSpan.includes(minY)) {
                if (latestSpan.includes(maxY)) continue;
                latestSpan.expandTo(maxY);
                continue;
            }
            Span span = SuppressionSpanHelper.allocateSpan(spanPool);
            span.init(minY, maxY);
            this.optimisedSuppressionSpans.add(span);
        }
        if (!matchedRole) {
            Span span = this.optimisedSuppressionSpans.removeFirst();
            span.reset();
            spanPool.push(span);
        }
    }

    public int adjustSpawnRangeMin(int min) {
        if (this.optimisedSuppressionSpans.isEmpty()) {
            return min;
        }
        int maxSpanIndex = this.optimisedSuppressionSpans.size() - 1;
        Span currentSpan = this.optimisedSuppressionSpans.get(this.currentSpanIndex);
        while (min >= currentSpan.max && this.currentSpanIndex < maxSpanIndex) {
            ++this.currentSpanIndex;
            currentSpan = this.optimisedSuppressionSpans.get(this.currentSpanIndex);
        }
        if (currentSpan.includes(min)) {
            if (this.currentSpanIndex < maxSpanIndex) {
                ++this.currentSpanIndex;
            }
            return currentSpan.max;
        }
        return min;
    }

    public int adjustSpawnRangeMax(int min, int max) {
        if (this.optimisedSuppressionSpans.isEmpty()) {
            return max;
        }
        Span currentSpan = this.optimisedSuppressionSpans.get(this.currentSpanIndex);
        if (max < currentSpan.min) {
            return max;
        }
        if (currentSpan.includes(max)) {
            return currentSpan.min;
        }
        if (min < currentSpan.min && max >= currentSpan.max) {
            return currentSpan.min;
        }
        return max;
    }

    public void reset() {
        ArrayDeque<Span> spanPool = SPAN_POOL.get();
        for (int i = this.optimisedSuppressionSpans.size() - 1; i >= 0; --i) {
            Span span = this.optimisedSuppressionSpans.remove(i);
            span.reset();
            spanPool.push(span);
        }
        this.currentSpanIndex = 0;
    }

    @Nonnull
    private static Span allocateSpan(@Nonnull ArrayDeque<Span> spanPool) {
        if (spanPool.isEmpty()) {
            return new Span();
        }
        return spanPool.pop();
    }

    private static class Span {
        private int min = -1;
        private int max = -1;

        private Span() {
        }

        public void init(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public void expandTo(int max) {
            this.max = max;
        }

        public boolean includes(int value) {
            return value >= this.min && value <= this.max;
        }

        public void reset() {
            this.max = -1;
            this.min = -1;
        }
    }
}

