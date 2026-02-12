package org.joml.sampling;

import java.nio.FloatBuffer;

public class Convolution {
   public static void gaussianKernel(int rows, int cols, float sigma, FloatBuffer dest) {
      if ((rows & 1) == 0) {
         throw new IllegalArgumentException("rows must be an odd number");
      }

      if ((cols & 1) == 0) {
         throw new IllegalArgumentException("cols must be an odd number");
      }

      if (dest == null) {
         throw new IllegalArgumentException("dest must not be null");
      }

      if (dest.remaining() < rows * cols) {
         throw new IllegalArgumentException("dest must have at least " + rows * cols + " remaining values");
      }

      float sum = 0.0F;
      int pos = dest.position();
      float a = (float)(1.0 / (2.0 * sigma * sigma));
      int i = 0;

      for (int y = -(rows - 1) / 2; y <= (rows - 1) / 2; y++) {
         for (int x = -(cols - 1) / 2; x <= (cols - 1) / 2; i++) {
            float k = (float)org.joml.Math.exp(-(y * y + x * x) * a);
            dest.put(pos + i, k);
            sum += k;
            x++;
         }
      }

      sum = 1.0F / sum;

      for (int ix = 0; ix < rows * cols; ix++) {
         dest.put(pos + ix, dest.get(pos + ix) * sum);
      }
   }

   public static void gaussianKernel(int rows, int cols, float sigma, float[] dest) {
      if ((rows & 1) == 0) {
         throw new IllegalArgumentException("rows must be an odd number");
      }

      if ((cols & 1) == 0) {
         throw new IllegalArgumentException("cols must be an odd number");
      }

      if (dest == null) {
         throw new IllegalArgumentException("dest must not be null");
      }

      if (dest.length < rows * cols) {
         throw new IllegalArgumentException("dest must have a size of at least " + rows * cols);
      }

      float sum = 0.0F;
      float a = (float)(1.0 / (2.0 * sigma * sigma));
      int i = 0;

      for (int y = -(rows - 1) / 2; y <= (rows - 1) / 2; y++) {
         for (int x = -(cols - 1) / 2; x <= (cols - 1) / 2; i++) {
            float k = (float)org.joml.Math.exp(-(y * y + x * x) * a);
            dest[i] = k;
            sum += k;
            x++;
         }
      }

      sum = 1.0F / sum;

      for (int ix = 0; ix < rows * cols; ix++) {
         dest[ix] *= sum;
      }
   }
}
