package com.hypixel.hytale.server.core.modules.interaction.breakshape;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import javax.annotation.Nonnull;

public class CylinderBreakShape extends BreakShape {
   @Nonnull
   public static final BuilderCodec<CylinderBreakShape> CODEC = BuilderCodec.builder(CylinderBreakShape.class, CylinderBreakShape::new, BreakShape.BASE_CODEC)
      .documentation("Breaks an elliptical disc of blocks in the user's view plane, extruded into the surface.")
      .afterDecode(BreakShape::validateShape)
      .build();

   @Override
   public void forEachLocalOffset(@Nonnull TriIntConsumer consumer) {
      int uLow = lowBound(this.width, this.centered);
      int uHigh = uLow + this.width - 1;
      int vLow = lowBound(this.height, this.centered);
      int vHigh = vLow + this.height - 1;
      double radiusU = this.width / 2.0;
      double radiusV = this.height / 2.0;
      double centreU = (uLow + uHigh) / 2.0;
      double centreV = (vLow + vHigh) / 2.0;

      for (int w = 0; w < this.depth; w++) {
         for (int u = uLow; u <= uHigh; u++) {
            for (int v = vLow; v <= vHigh; v++) {
               double normU = radiusU > 0.0 ? (u - centreU) / radiusU : 0.0;
               double normV = radiusV > 0.0 ? (v - centreV) / radiusV : 0.0;
               if (normU * normU + normV * normV <= 1.0) {
                  consumer.accept(u, v, w);
               }
            }
         }
      }
   }

   public com.hypixel.hytale.protocol.BreakShape toPacket() {
      com.hypixel.hytale.protocol.CylinderBreakShape packet = new com.hypixel.hytale.protocol.CylinderBreakShape();
      packet.width = this.width;
      packet.height = this.height;
      packet.depth = this.depth;
      packet.centered = this.centered;
      packet.offset = this.getOffset();
      packet.orientation = this.getOrientation();
      return packet;
   }
}
