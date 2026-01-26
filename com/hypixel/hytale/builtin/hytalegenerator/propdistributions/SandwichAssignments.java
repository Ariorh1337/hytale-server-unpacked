/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class SandwichAssignments
extends Assignments {
    @Nonnull
    private final List<VerticalDelimiter> verticalDelimiters;
    private final int runtime;

    public SandwichAssignments(@Nonnull List<VerticalDelimiter> verticalDelimiters, int runtime) {
        this.runtime = runtime;
        this.verticalDelimiters = new ArrayList<VerticalDelimiter>(verticalDelimiters);
    }

    @Override
    public Prop propAt(@Nonnull Vector3d position, @Nonnull WorkerIndexer.Id id, double distanceTOBiomeEdge) {
        if (this.verticalDelimiters.isEmpty()) {
            return Prop.noProp();
        }
        for (VerticalDelimiter fd : this.verticalDelimiters) {
            if (!fd.isInside(position.y)) continue;
            return fd.assignments.propAt(position, id, distanceTOBiomeEdge);
        }
        return Prop.noProp();
    }

    @Override
    public int getRuntime() {
        return this.runtime;
    }

    @Override
    @Nonnull
    public List<Prop> getAllPossibleProps() {
        ArrayList<Prop> list = new ArrayList<Prop>();
        for (VerticalDelimiter f : this.verticalDelimiters) {
            list.addAll(f.assignments.getAllPossibleProps());
        }
        return list;
    }

    public static class VerticalDelimiter {
        double maxY;
        double minY;
        Assignments assignments;

        public VerticalDelimiter(@Nonnull Assignments propDistributions, double minY, double maxY) {
            this.minY = minY;
            this.maxY = maxY;
            this.assignments = propDistributions;
        }

        boolean isInside(double fieldValue) {
            return fieldValue < this.maxY && fieldValue >= this.minY;
        }
    }
}

