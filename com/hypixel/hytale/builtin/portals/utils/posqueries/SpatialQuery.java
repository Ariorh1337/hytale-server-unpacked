/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.utils.posqueries;

import com.hypixel.hytale.builtin.portals.utils.posqueries.PositionPredicate;
import com.hypixel.hytale.builtin.portals.utils.posqueries.SpatialQueryDebug;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.generic.FilterQuery;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.generic.FlatMapQuery;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface SpatialQuery {
    public Stream<Vector3d> createCandidates(World var1, Vector3d var2, @Nullable SpatialQueryDebug var3);

    default public SpatialQuery then(SpatialQuery expand) {
        return new FlatMapQuery(this, expand);
    }

    default public SpatialQuery filter(PositionPredicate predicate) {
        return new FilterQuery(this, predicate);
    }

    default public Optional<Vector3d> execute(World world, Vector3d origin) {
        return this.createCandidates(world, origin, null).findFirst();
    }

    default public Optional<Vector3d> debug(World world, Vector3d origin) {
        try {
            SpatialQueryDebug debug = new SpatialQueryDebug();
            Optional<Vector3d> output = this.createCandidates(world, origin, debug).findFirst();
            debug.appendLine("-> OUTPUT: " + output.map(debug::fmt).orElse("<null>"));
            HytaleLogger.getLogger().at(Level.INFO).log(debug.toString());
            return output;
        }
        catch (Throwable t) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(t)).log("Error in SpatialQuery");
            throw new RuntimeException("Error in SpatialQuery", t);
        }
    }
}

