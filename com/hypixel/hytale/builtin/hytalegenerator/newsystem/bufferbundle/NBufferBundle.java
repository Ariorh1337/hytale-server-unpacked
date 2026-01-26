/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.performanceinstruments.MemInstrument;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NBufferBundle
implements MemInstrument {
    private final Map<NBufferType, Grid> grids = new HashMap<NBufferType, Grid>();

    @Nonnull
    public Grid createGrid(@Nonnull NBufferType bufferType, int capacity) {
        assert (capacity >= 0);
        assert (!this.grids.containsKey(bufferType));
        assert (!this.existingGridHasBufferTypeIndex(bufferType.index));
        Grid grid = new Grid(bufferType, capacity);
        this.grids.put(bufferType, grid);
        return grid;
    }

    @Nonnull
    public Access createBufferAccess(@Nonnull NBufferType bufferType, @Nonnull Bounds3i bounds_bufferGrid) {
        assert (bounds_bufferGrid.isCorrect());
        return this.getGrid(bufferType).openAccess(bounds_bufferGrid);
    }

    public void closeALlAccesses() {
        for (Grid grid : this.grids.values()) {
            grid.closeAllAccesses();
        }
    }

    @Nonnull
    public Grid getGrid(@Nonnull NBufferType contentType) {
        Grid grid = this.grids.get(contentType);
        assert (grid != null);
        return grid;
    }

    @Override
    @Nonnull
    public MemInstrument.Report getMemoryUsage() {
        long size_bytes = 16L;
        for (Map.Entry<NBufferType, Grid> entry : this.grids.entrySet()) {
            size_bytes += entry.getValue().getMemoryUsage().size_bytes();
        }
        return new MemInstrument.Report(size_bytes);
    }

    private boolean existingGridHasBufferTypeIndex(int bufferTypeIndex) {
        for (Grid grid : this.grids.values()) {
            if (grid.bufferType.index != bufferTypeIndex) continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public MemoryReport createMemoryReport() {
        MemoryReport memoryReport = new MemoryReport();
        for (Grid grid : this.grids.values()) {
            MemInstrument.Report gridUsage = grid.getMemoryUsage();
            int gridBufferCount = grid.buffers.size();
            memoryReport.gridEntries.add(new MemoryReport.GridEntry(gridUsage, gridBufferCount, grid.bufferType));
        }
        return memoryReport;
    }

    public static class Grid
    implements MemInstrument {
        private final NBufferType bufferType;
        private final Map<Vector3i, TrackedBuffer> buffers;
        private final Deque<Vector3i> oldestColumnEntryDeque_bufferGrid;
        private final int capacity;
        private final List<Access> accessors;

        private Grid(@Nonnull NBufferType bufferType, int capacity) {
            this.bufferType = bufferType;
            this.buffers = new HashMap<Vector3i, TrackedBuffer>();
            this.oldestColumnEntryDeque_bufferGrid = new ArrayDeque<Vector3i>();
            this.capacity = Math.max(capacity, 0);
            this.accessors = new ArrayList<Access>();
        }

        @Nonnull
        public NBufferType getBufferType() {
            return this.bufferType;
        }

        @Nonnull
        public Access openAccess(@Nonnull Bounds3i bounds_bufferGrid) {
            Access access = new Access(this, bounds_bufferGrid);
            this.accessors.add(access);
            access.loadGrid();
            return access;
        }

        public void closeAllAccesses() {
            for (int i = this.accessors.size() - 1; i >= 0; --i) {
                Access access = this.accessors.get(i);
                access.close();
            }
        }

        @Override
        @Nonnull
        public MemInstrument.Report getMemoryUsage() {
            long size_bytes = 68L;
            size_bytes += 28L * (long)this.buffers.size();
            size_bytes += 4L * (long)this.buffers.size();
            size_bytes += 32L * (long)this.buffers.size();
            for (TrackedBuffer buffer : this.buffers.values()) {
                size_bytes += buffer.getMemoryUsage().size_bytes();
            }
            size_bytes += 8L * (long)this.accessors.size();
            for (Access access : this.accessors) {
                size_bytes += access.getMemoryUsage().size_bytes();
            }
            return new MemInstrument.Report(size_bytes);
        }

        private void ensureBufferColumnExists(@Nonnull Vector3i position_bufferGrid, @Nonnull TrackedBuffer[] trackedBuffersOut) {
            assert (position_bufferGrid.y == 0);
            assert (trackedBuffersOut.length == 40);
            TrackedBuffer buffer = this.buffers.get(position_bufferGrid);
            if (buffer == null) {
                this.createBufferColumn(position_bufferGrid, trackedBuffersOut);
                return;
            }
            Vector3i positionClone_bufferGrid = new Vector3i(position_bufferGrid);
            for (int i = 0; i < trackedBuffersOut.length; ++i) {
                positionClone_bufferGrid.setY(i + 0);
                trackedBuffersOut[i] = this.buffers.get(positionClone_bufferGrid);
                assert (trackedBuffersOut[i] != null);
            }
        }

        private void createBufferColumn(@Nonnull Vector3i position_bufferGrid, @Nonnull TrackedBuffer[] trackedBuffersOut) {
            assert (!this.buffers.containsKey(position_bufferGrid));
            assert (trackedBuffersOut.length == 40);
            this.tryTrimSurplus(40);
            int i = 0;
            for (int y = 0; y < 40; ++y) {
                Vector3i finalPosition_bufferGrid = new Vector3i(position_bufferGrid.x, y, position_bufferGrid.z);
                Tracker tracker = new Tracker();
                NBuffer buffer = this.bufferType.bufferSupplier.get();
                assert (this.bufferType.isValid(buffer));
                trackedBuffersOut[i] = new TrackedBuffer(tracker, buffer);
                this.buffers.put(finalPosition_bufferGrid, trackedBuffersOut[i]);
                ++i;
            }
            Vector3i tilePosition_bufferGrid = new Vector3i(position_bufferGrid.x, 0, position_bufferGrid.z);
            this.oldestColumnEntryDeque_bufferGrid.addLast(tilePosition_bufferGrid);
        }

        private void tryTrimSurplus(int extraRoom) {
            int surplusCount = Math.max(0, this.buffers.size() - this.capacity - extraRoom);
            int surplusColumnsCount = surplusCount == 0 ? 0 : surplusCount / 40 + 1;
            for (int i = 0; i < surplusColumnsCount; ++i) {
                if (this.destroyOldestBufferColumn()) continue;
                return;
            }
        }

        private boolean destroyOldestBufferColumn() {
            assert (!this.oldestColumnEntryDeque_bufferGrid.isEmpty());
            for (int i = 0; i < this.oldestColumnEntryDeque_bufferGrid.size(); ++i) {
                Vector3i oldest_bufferGrid = this.oldestColumnEntryDeque_bufferGrid.removeFirst();
                if (!this.isBufferColumnInAccess(oldest_bufferGrid)) {
                    this.removeBufferColumn(oldest_bufferGrid);
                    return true;
                }
                this.oldestColumnEntryDeque_bufferGrid.addLast(oldest_bufferGrid);
            }
            return false;
        }

        private void removeBufferColumn(@Nonnull Vector3i position_bufferGrid) {
            assert (position_bufferGrid.y == 0);
            Vector3i removalPosition_bufferGrid = new Vector3i(position_bufferGrid);
            for (int y = 0; y < 40; ++y) {
                removalPosition_bufferGrid.setY(y);
                this.buffers.remove(removalPosition_bufferGrid);
            }
        }

        private boolean isBufferColumnInAccess(@Nonnull Vector3i position_bufferGrid) {
            assert (position_bufferGrid.y == 0);
            for (Access access : this.accessors) {
                if (!access.bounds_bufferGrid.contains(position_bufferGrid)) continue;
                return true;
            }
            return false;
        }

        public record TrackedBuffer(@Nonnull Tracker tracker, @Nonnull NBuffer buffer) implements MemInstrument
        {
            @Override
            @Nonnull
            public MemInstrument.Report getMemoryUsage() {
                long size_bytes = 16L + this.tracker.getMemoryUsage().size_bytes() + this.buffer.getMemoryUsage().size_bytes();
                return new MemInstrument.Report(size_bytes);
            }
        }
    }

    public static class Access
    implements MemInstrument {
        private final Grid grid;
        private final Bounds3i bounds_bufferGrid;
        private final Grid.TrackedBuffer[] buffers;
        private boolean isClosed;

        private Access(@Nonnull Grid grid, @Nonnull Bounds3i bounds_bufferGrid) {
            assert (bounds_bufferGrid.isCorrect());
            this.grid = grid;
            this.bounds_bufferGrid = bounds_bufferGrid.clone();
            this.bounds_bufferGrid.min.y = 0;
            this.bounds_bufferGrid.max.y = 40;
            Vector3i boundsSize_bufferGrid = this.bounds_bufferGrid.getSize();
            int bufferCount = boundsSize_bufferGrid.x * boundsSize_bufferGrid.y * boundsSize_bufferGrid.z;
            this.buffers = new Grid.TrackedBuffer[bufferCount];
            this.isClosed = false;
        }

        @Nonnull
        public View createView(@Nonnull Bounds3i viewBounds_bufferGrid) {
            assert (this.bounds_bufferGrid.contains(viewBounds_bufferGrid));
            return new View(this, viewBounds_bufferGrid);
        }

        @Nonnull
        public View createView() {
            return new View(this, this.bounds_bufferGrid);
        }

        @Nonnull
        public Grid.TrackedBuffer getBuffer(@Nonnull Vector3i position_bufferGrid) {
            assert (!this.isClosed);
            assert (this.bounds_bufferGrid.contains(position_bufferGrid));
            int index = GridUtils.toIndexFromPositionYXZ(position_bufferGrid, this.bounds_bufferGrid);
            return this.buffers[index];
        }

        @Nonnull
        public Bounds3i getBounds_bufferGrid() {
            return this.bounds_bufferGrid.clone();
        }

        public void close() {
            this.grid.accessors.remove(this);
            this.isClosed = true;
            Arrays.fill(this.buffers, null);
        }

        @Override
        @Nonnull
        public MemInstrument.Report getMemoryUsage() {
            long size_bytes = 8L + this.bounds_bufferGrid.getMemoryUsage().size_bytes();
            return new MemInstrument.Report(size_bytes);
        }

        private void loadGrid() {
            assert (!this.isClosed);
            assert (this.bounds_bufferGrid.min.y == 0 && this.bounds_bufferGrid.max.y == 40);
            Vector3i position_bufferGrid = this.bounds_bufferGrid.min.clone();
            position_bufferGrid.setY(0);
            Grid.TrackedBuffer[] trackedBuffersOutput = new Grid.TrackedBuffer[40];
            position_bufferGrid.z = this.bounds_bufferGrid.min.z;
            while (position_bufferGrid.z < this.bounds_bufferGrid.max.z) {
                position_bufferGrid.x = this.bounds_bufferGrid.min.x;
                while (position_bufferGrid.x < this.bounds_bufferGrid.max.x) {
                    position_bufferGrid.setY(0);
                    this.grid.ensureBufferColumnExists(position_bufferGrid, trackedBuffersOutput);
                    int i = 0;
                    position_bufferGrid.y = 0;
                    while (position_bufferGrid.y < 40) {
                        position_bufferGrid.dropHash();
                        int index = GridUtils.toIndexFromPositionYXZ(position_bufferGrid, this.bounds_bufferGrid);
                        this.buffers[index] = trackedBuffersOutput[i];
                        ++i;
                        ++position_bufferGrid.y;
                    }
                    ++position_bufferGrid.x;
                }
                ++position_bufferGrid.z;
            }
        }

        public static class View {
            private final Access access;
            private final Bounds3i bounds_bufferGrid;

            private View(@Nonnull Access access, @Nonnull Bounds3i bounds_bufferGrid) {
                assert (access.bounds_bufferGrid.contains(bounds_bufferGrid));
                this.access = access;
                this.bounds_bufferGrid = bounds_bufferGrid;
            }

            @Nonnull
            public Grid.TrackedBuffer getBuffer(@Nonnull Vector3i position_bufferGrid) {
                assert (!this.access.isClosed);
                assert (this.bounds_bufferGrid.contains(position_bufferGrid));
                return this.access.getBuffer(position_bufferGrid);
            }

            @Nonnull
            public Bounds3i getBounds_bufferGrid() {
                return this.bounds_bufferGrid.clone();
            }
        }
    }

    public static class MemoryReport {
        public final List<GridEntry> gridEntries = new ArrayList<GridEntry>();

        @Nonnull
        public String toString() {
            this.gridEntries.sort((o1, o2) -> {
                if (o1.bufferType().index > o2.bufferType().index) {
                    return 1;
                }
                if (o1.bufferType().index < o2.bufferType().index) {
                    return -1;
                }
                return 0;
            });
            StringBuilder builder = new StringBuilder();
            long total_mb = 0L;
            for (GridEntry entry : this.gridEntries) {
                total_mb += entry.report.size_bytes();
            }
            builder.append("Memory Usage Report\n");
            builder.append("Buffers Memory Usage: ").append(total_mb /= 1000000L).append(" mb\n");
            for (GridEntry entry : this.gridEntries) {
                builder.append(entry.toString(1));
            }
            return builder.toString();
        }

        public record GridEntry(MemInstrument.Report report, int bufferCount, @Nonnull NBufferType bufferType) {
            @Nonnull
            public String toString(int indentation) {
                long size_mb = this.report.size_bytes() / 1000000L;
                StringBuilder builder = new StringBuilder();
                builder.append("\t".repeat(indentation)).append(this.bufferType.name + " Grid (Index ").append(this.bufferType().index).append("):\n");
                builder.append("\t".repeat(indentation + 1)).append("Memory Footprint: ").append(size_mb).append(" mb\n");
                builder.append("\t".repeat(indentation + 1)).append("Buffer Count: ").append(this.bufferCount).append("\n");
                return builder.toString();
            }
        }
    }

    public static class Tracker
    implements MemInstrument {
        public final int INITIAL_STAGE_INDEX = -1;
        public int stageIndex = -1;

        @Override
        @Nonnull
        public MemInstrument.Report getMemoryUsage() {
            return new MemInstrument.Report(4L);
        }
    }
}

