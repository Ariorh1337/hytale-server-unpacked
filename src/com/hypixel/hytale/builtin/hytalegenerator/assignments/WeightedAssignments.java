package com.hypixel.hytale.builtin.hytalegenerator.assignments;

import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.props.EmptyProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class WeightedAssignments extends Assignments {
   @Nonnull
   private final WeightedMap<Assignments> weightedDistributions;
   @Nonnull
   private final RngField rngField;
   private final double noneProbability;

   public WeightedAssignments(@Nonnull WeightedMap<Assignments> props, int seed, double noneProbability) {
      this.weightedDistributions = new WeightedMap<>(props);
      this.rngField = new RngField(seed);
      this.noneProbability = noneProbability;
   }

   @Override
   public Prop propAt(@Nonnull Vector3d position, @Nonnull WorkerIndexer.Id id, double distanceTOBiomeEdge) {
      if (this.weightedDistributions.size() == 0) {
         return EmptyProp.INSTANCE;
      }

      long x = (long)(position.x * 10000.0);
      long y = (long)(position.y * 10000.0);
      long z = (long)(position.z * 10000.0);
      FastRandom rand = new FastRandom(this.rngField.get(x, y, z));
      return rand.nextDouble() < this.noneProbability ? EmptyProp.INSTANCE : this.weightedDistributions.pick(rand).propAt(position, id, distanceTOBiomeEdge);
   }

   @Nonnull
   @Override
   public List<Prop> getAllPossibleProps() {
      ArrayList<Prop> list = new ArrayList<>();

      for (Assignments d : this.weightedDistributions.allElements()) {
         list.addAll(d.getAllPossibleProps());
      }

      return list;
   }
}
