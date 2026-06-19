package com.hypixel.hytale.server.core.modules.interaction.breakshape;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import javax.annotation.Nonnull;

public class BoxBreakShape extends BreakShape {
   @Nonnull
   public static final BuilderCodec<BoxBreakShape> CODEC = BuilderCodec.builder(BoxBreakShape.class, BoxBreakShape::new, BreakShape.BASE_CODEC)
      .documentation("Breaks a solid rectangular cuboid of blocks oriented to the user's view direction.")
      .afterDecode(BreakShape::validateShape)
      .build();

   @Override
   public void forEachLocalOffset(@Nonnull TriIntConsumer consumer) {
      int uLow = lowBound(this.width, this.centered);
      int uHigh = uLow + this.width - 1;
      int vLow = lowBound(this.height, this.centered);
      int vHigh = vLow + this.height - 1;

      for (int w = 0; w < this.depth; w++) {
         for (int u = uLow; u <= uHigh; u++) {
            for (int v = vLow; v <= vHigh; v++) {
               consumer.accept(u, v, w);
            }
         }
      }
   }

   public com.hypixel.hytale.protocol.BreakShape toPacket() {
      com.hypixel.hytale.protocol.BoxBreakShape packet = new com.hypixel.hytale.protocol.BoxBreakShape();
      packet.width = this.width;
      packet.height = this.height;
      packet.depth = this.depth;
      packet.centered = this.centered;
      packet.offset = this.getOffset();
      packet.orientation = this.getOrientation();
      return packet;
   }
}
