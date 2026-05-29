package com.hypixel.hytale.server.core.asset.util;

import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ColorAlpha;
import com.hypixel.hytale.protocol.ColorLight;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public class ColorParseUtil {
   public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^\\s*#([0-9a-fA-F]{3}){1,2}\\s*$");
   public static final Pattern HEX_ALPHA_COLOR_PATTERN = Pattern.compile("^\\s*#([0-9a-fA-F]{4}){1,2}\\s*$");
   public static final Pattern RGB_COLOR_PATTERN = Pattern.compile("^\\s*rgb\\((\\s*[0-9]{1,3}\\s*,){2}\\s*[0-9]{1,3}\\s*\\)\\s*$");
   public static final Pattern RGBA_COLOR_PATTERN = Pattern.compile("^\\s*rgba\\((\\s*[0-9]{1,3}\\s*,){3}\\s*[0,1](.[0-9]*)?\\s*\\)\\s*$");
   public static final Pattern RGBA_HEX_COLOR_PATTERN = Pattern.compile("^\\s*rgba\\(\\s*#([0-9a-fA-F]{3}){1,2}\\s*,\\s*[0,1](.[0-9]*)?\\s*\\)\\s*$");

   @Nullable
   public static ColorAlpha readColorAlpha(@Nonnull RawJsonReader reader) throws IOException {
      reader.consumeWhiteSpace();

      return switch (reader.peek()) {
         case 35 -> readHexStringToColorAlpha(reader);
         case 114 -> readRgbaStringToColorAlpha(reader);
         default -> null;
      };
   }

   @Nullable
   public static ColorAlpha parseColorAlpha(@Nonnull String stringValue) {
      if (HEX_ALPHA_COLOR_PATTERN.matcher(stringValue).matches()) {
         return hexStringToColorAlpha(stringValue);
      } else if (RGBA_HEX_COLOR_PATTERN.matcher(stringValue).matches()) {
         return rgbaHexStringToColor(stringValue);
      } else {
         return RGBA_COLOR_PATTERN.matcher(stringValue).matches() ? rgbaDecimalStringToColor(stringValue) : null;
      }
   }

   @Nullable
   public static Color readColor(@Nonnull RawJsonReader reader) throws IOException {
      reader.consumeWhiteSpace();

      return switch (reader.peek()) {
         case 35 -> readHexStringToColor(reader);
         case 114 -> readRgbStringToColor(reader);
         default -> null;
      };
   }

   @Nullable
   public static Color parseColor(@Nonnull String stringValue) {
      if (HEX_COLOR_PATTERN.matcher(stringValue).matches()) {
         return hexStringToColor(stringValue);
      } else {
         return RGB_COLOR_PATTERN.matcher(stringValue).matches() ? rgbStringToColor(stringValue) : null;
      }
   }

   @Nonnull
   public static Color readHexStringToColor(@Nonnull RawJsonReader reader) throws IOException {
      int rgba = readHexAlphaStringToRGBAInt(reader);
      return new Color((byte)(rgba >> 24 & 0xFF), (byte)(rgba >> 16 & 0xFF), (byte)(rgba >> 8 & 0xFF));
   }

   @Nonnull
   public static Color hexStringToColor(String color) {
      int rgba = hexAlphaStringToRGBAInt(color);
      return new Color((byte)(rgba >> 24 & 0xFF), (byte)(rgba >> 16 & 0xFF), (byte)(rgba >> 8 & 0xFF));
   }

   @Nonnull
   public static ColorAlpha readHexStringToColorAlpha(@Nonnull RawJsonReader reader) throws IOException {
      int rgba = readHexAlphaStringToRGBAInt(reader);
      return new ColorAlpha((byte)(rgba & 0xFF), (byte)(rgba >> 24 & 0xFF), (byte)(rgba >> 16 & 0xFF), (byte)(rgba >> 8 & 0xFF));
   }

   @Nonnull
   public static ColorAlpha hexStringToColorAlpha(String color) {
      int rgba = hexAlphaStringToRGBAInt(color);
      return new ColorAlpha((byte)(rgba & 0xFF), (byte)(rgba >> 24 & 0xFF), (byte)(rgba >> 16 & 0xFF), (byte)(rgba >> 8 & 0xFF));
   }

   public static int readHexAlphaStringToRGBAInt(@Nonnull RawJsonReader reader) throws IOException {
      reader.consumeWhiteSpace();
      reader.expect('#');
      reader.mark();

      try {
         int value = reader.readIntValue(16);
         int size = reader.getMarkDistance();
         switch (size) {
            case 3:
               value <<= 4;
               value |= 15;
            case 4:
               int red = value >> 12 & 15;
               int green = value >> 8 & 15;
               int blue = value >> 4 & 15;
               int alpha = value & 15;
               return red << 28 | red << 24 | green << 20 | green << 16 | blue << 12 | blue << 8 | alpha << 4 | alpha;
            case 5:
            case 7:
            default:
               throw new IllegalArgumentException("Invalid hex color size: " + size);
            case 6:
               return value << 8 | 0xFF;
            case 8:
               return value;
         }
      } finally {
         reader.unmark();
         reader.consumeWhiteSpace();
      }
   }

   public static int hexAlphaStringToRGBAInt(String color) {
      Objects.requireNonNull(color, "Color must not be null");
      color = color.trim();
      if (!color.isEmpty() && color.charAt(0) == '#') {
         color = color.substring(1);
         int value = (int)Long.parseLong(color, 16);
         switch (color.length()) {
            case 3:
               value <<= 4;
               value |= 15;
            case 4:
               int red = value >> 12 & 15;
               int green = value >> 8 & 15;
               int blue = value >> 4 & 15;
               int alpha = value & 15;
               return red << 28 | red << 24 | green << 20 | green << 16 | blue << 12 | blue << 8 | alpha << 4 | alpha;
            case 5:
            case 7:
            default:
               throw new IllegalArgumentException("Invalid hex color format: '" + color + "'");
            case 6:
               return value << 8 | 0xFF;
            case 8:
               return value;
         }
      } else {
         throw new IllegalArgumentException("Hex color must start with '#'");
      }
   }

   public static int readHexStringToRGBInt(@Nonnull RawJsonReader reader) throws IOException {
      return readHexAlphaStringToRGBAInt(reader) >>> 8;
   }

   public static int hexStringToRGBInt(String color) {
      return hexAlphaStringToRGBAInt(color) >>> 8;
   }

   @Nonnull
   public static String colorToHexString(@Nullable Color color) {
      return color == null ? "#FFFFFF" : toHexString(color.red, color.green, color.blue);
   }

   @Nonnull
   public static String colorToHexAlphaString(@Nullable ColorAlpha color) {
      return color == null ? "#FFFFFFFF" : toHexAlphaString(color.red, color.green, color.blue, color.alpha);
   }

   @Nonnull
   public static Color readRgbStringToColor(@Nonnull RawJsonReader reader) throws IOException {
      reader.consumeWhiteSpace();
      reader.expect("rgb(", 0);
      reader.consumeWhiteSpace();
      byte red = reader.readByteValue();
      reader.consumeWhiteSpace();
      reader.expect(',');
      reader.consumeWhiteSpace();
      byte green = reader.readByteValue();
      reader.consumeWhiteSpace();
      reader.expect(',');
      reader.consumeWhiteSpace();
      byte blue = reader.readByteValue();
      reader.consumeWhiteSpace();
      reader.expect(')');
      reader.consumeWhiteSpace();
      return new Color(red, green, blue);
   }

   @Nonnull
   public static Color rgbStringToColor(String color) {
      Objects.requireNonNull(color, "Color must not be null");
      color = color.trim();
      if (color.startsWith("rgb(") && color.charAt(color.length() - 1) == ')') {
         color = color.substring(4, color.length() - 1);
         String[] channels = color.split(",");
         int channelLength = channels.length;
         if (channelLength != 3) {
            throw new IllegalArgumentException("rgb() but contain all 3 channels; r, g and b");
         }

         byte red = (byte)Integer.parseInt(channels[0].trim());
         byte green = (byte)Integer.parseInt(channels[1].trim());
         byte blue = (byte)Integer.parseInt(channels[2].trim());
         return new Color(red, green, blue);
      } else {
         throw new IllegalArgumentException("Color must start with 'rgb(' and end with ')'");
      }
   }

   @Nonnull
   public static ColorAlpha readRgbaStringToColorAlpha(@Nonnull RawJsonReader reader) throws IOException {
      reader.consumeWhiteSpace();
      reader.expect("rgba(", 0);
      reader.consumeWhiteSpace();
      return reader.peek() == 35 ? readRgbaHexStringToColor(reader, false) : readRgbaDecimalStringToColor(reader, false);
   }

   @Nonnull
   public static ColorAlpha readRgbaDecimalStringToColor(@Nonnull RawJsonReader reader) throws IOException {
      return readRgbaDecimalStringToColor(reader, true);
   }

   @Nonnull
   public static ColorAlpha readRgbaDecimalStringToColor(@Nonnull RawJsonReader reader, boolean readStart) throws IOException {
      if (readStart) {
         reader.consumeWhiteSpace();
         reader.expect("rgba(", 0);
         reader.consumeWhiteSpace();
      }

      byte red = reader.readByteValue();
      reader.consumeWhiteSpace();
      reader.expect(',');
      reader.consumeWhiteSpace();
      byte green = reader.readByteValue();
      reader.consumeWhiteSpace();
      reader.expect(',');
      reader.consumeWhiteSpace();
      byte blue = reader.readByteValue();
      reader.consumeWhiteSpace();
      reader.expect(',');
      reader.consumeWhiteSpace();
      byte alpha = (byte)MathUtil.clamp(reader.readFloatValue() * 255.0F, 0.0F, 255.0F);
      reader.consumeWhiteSpace();
      reader.expect(')');
      reader.consumeWhiteSpace();
      return new ColorAlpha(alpha, red, green, blue);
   }

   @Nonnull
   public static ColorAlpha rgbaDecimalStringToColor(String color) {
      Objects.requireNonNull(color, "Color must not be null");
      color = color.trim();
      if (color.startsWith("rgba(") && color.charAt(color.length() - 1) == ')') {
         color = color.substring(5, color.length() - 1);
         String[] channels = color.split(",");
         int channelLength = channels.length;
         if (channelLength != 4) {
            throw new IllegalArgumentException("rgba() but contain all 4 channels; r, g, b and a");
         }

         byte red = (byte)MathUtil.clamp(Integer.parseInt(channels[0].trim()), 0, 255);
         byte green = (byte)MathUtil.clamp(Integer.parseInt(channels[1].trim()), 0, 255);
         byte blue = (byte)MathUtil.clamp(Integer.parseInt(channels[2].trim()), 0, 255);
         byte alpha = (byte)MathUtil.clamp(Float.parseFloat(channels[3]) * 255.0F, 0.0F, 255.0F);
         return new ColorAlpha(alpha, red, green, blue);
      } else {
         throw new IllegalArgumentException("Color must start with 'rgba(' and end with ')'");
      }
   }

   @Nonnull
   public static ColorAlpha readRgbaHexStringToColor(@Nonnull RawJsonReader reader) throws IOException {
      return readRgbaHexStringToColor(reader, true);
   }

   @Nonnull
   public static ColorAlpha readRgbaHexStringToColor(@Nonnull RawJsonReader reader, boolean readStart) throws IOException {
      if (readStart) {
         reader.consumeWhiteSpace();
         reader.expect("rgba(", 0);
         reader.consumeWhiteSpace();
      }

      long val = readHexAlphaStringToRGBAInt(reader);
      reader.consumeWhiteSpace();
      reader.expect(',');
      reader.consumeWhiteSpace();
      byte alpha = (byte)MathUtil.clamp(reader.readFloatValue() * 255.0F, 0.0F, 255.0F);
      reader.consumeWhiteSpace();
      reader.expect(')');
      reader.consumeWhiteSpace();
      return new ColorAlpha(alpha, (byte)(val >> 24 & 255L), (byte)(val >> 16 & 255L), (byte)(val >> 8 & 255L));
   }

   @Nonnull
   public static ColorAlpha rgbaHexStringToColor(String color) {
      Objects.requireNonNull(color, "Color must not be null");
      color = color.trim();
      if (color.startsWith("rgba(") && color.charAt(color.length() - 1) == ')') {
         color = color.substring(5, color.length() - 1);
         String[] channels = color.split(",");
         int channelLength = channels.length;
         if (channelLength != 2) {
            throw new IllegalArgumentException("rgba() but contain both #rgb and a");
         }

         long val = hexAlphaStringToRGBAInt(channels[0].trim());
         byte alpha = (byte)MathUtil.clamp(Float.parseFloat(channels[1]) * 255.0F, 0.0F, 255.0F);
         return new ColorAlpha(alpha, (byte)(val >> 24 & 255L), (byte)(val >> 16 & 255L), (byte)(val >> 8 & 255L));
      } else {
         throw new IllegalArgumentException("Color must start with 'rgba(' and end with ')'");
      }
   }

   @Nonnull
   public static String colorToHex(@Nullable java.awt.Color color) {
      if (color == null) {
         return "#FFFFFF";
      }

      int argb = color.getRGB();
      int rgb = argb & 16777215;
      return toHexString(rgb);
   }

   @Nonnull
   public static String colorToHexAlpha(@Nullable java.awt.Color color) {
      if (color == null) {
         return "#FFFFFFFF";
      }

      int argb = color.getRGB();
      int alpha = argb >> 24 & 0xFF;
      int rgb = argb & 16777215;
      int rgba = rgb << 8 | alpha;
      return toHexAlphaString(rgba);
   }

   public static int colorToARGBInt(@Nullable Color color) {
      return color == null ? -1 : 0xFF000000 | (color.red & 0xFF) << 16 | (color.green & 0xFF) << 8 | color.blue & 0xFF;
   }

   public static void hexStringToColorLightDirect(@Nonnull ColorLight colorLight, @Nonnull String color) {
      if (color.length() == 4) {
         colorLight.red = Byte.parseByte(color.substring(1, 2), 16);
         colorLight.green = Byte.parseByte(color.substring(2, 3), 16);
         colorLight.blue = Byte.parseByte(color.substring(3, 4), 16);
      } else {
         colorLight.red = (byte)(Integer.parseInt(color.substring(1, 3), 16) / 17);
         colorLight.green = (byte)(Integer.parseInt(color.substring(3, 5), 16) / 17);
         colorLight.blue = (byte)(Integer.parseInt(color.substring(5, 7), 16) / 17);
      }
   }

   @Nonnull
   public static String colorLightToHexString(@Nonnull ColorLight colorLight) {
      return toHexString((byte)(colorLight.red * 17), (byte)(colorLight.green * 17), (byte)(colorLight.blue * 17));
   }

   @Nonnull
   public static String toHexString(byte red, byte green, byte blue) {
      return toHexString((red & 255) << 16 | (green & 255) << 8 | blue & 255);
   }

   @Nonnull
   public static String toHexString(int rgb) {
      String hexString = Integer.toHexString(rgb);
      return "#" + "0".repeat(6 - hexString.length()) + hexString;
   }

   @Nonnull
   public static String toHexAlphaString(byte red, byte green, byte blue, byte alpha) {
      return toHexAlphaString((red & 255) << 24 | (green & 255) << 16 | (blue & 255) << 8 | alpha & 255);
   }

   @Nonnull
   public static String toHexAlphaString(int rgba) {
      String hexString = Integer.toHexString(rgba);
      return "#" + "0".repeat(8 - hexString.length()) + hexString;
   }

   @Nullable
   public static String computeDominantColor(@Nonnull Path pngPath) throws Exception {
      BufferedImage img = ImageIO.read(pngPath.toFile());
      if (img == null) {
         return null;
      }

      ArrayList<int[]> pixels = new ArrayList<>();

      for (int y = 0; y < img.getHeight(); y++) {
         for (int x = 0; x < img.getWidth(); x++) {
            int argb = img.getRGB(x, y);
            int alpha = argb >> 24 & 0xFF;
            if (alpha >= 128) {
               pixels.add(new int[]{argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF});
            }
         }
      }

      if (pixels.isEmpty()) {
         return null;
      }

      if (pixels.size() < 8) {
         double r = 0.0;
         double g = 0.0;
         double b = 0.0;

         for (int[] p : pixels) {
            r += p[0];
            g += p[1];
            b += p[2];
         }

         r /= pixels.size();
         g /= pixels.size();
         b /= pixels.size();
         return String.format("#%02X%02X%02X", (int)r, (int)g, (int)b);
      } else {
         Random rng = new Random(42L);
         if (pixels.size() > 512) {
            Collections.shuffle(pixels, rng);
            pixels = new ArrayList<>(pixels.subList(0, 512));
         }

         List<ColorParseUtil.KMeansCluster> result = kmeans(pixels, 4, rng);
         if (result.isEmpty()) {
            return null;
         }

         int total = 0;

         for (ColorParseUtil.KMeansCluster cluster : result) {
            total += cluster.count;
         }

         double exponent = 2.0;
         double[] weights = new double[result.size()];
         double weightSum = 0.0;

         for (int i = 0; i < result.size(); i++) {
            weights[i] = Math.pow((double)result.get(i).count / total, exponent);
            weightSum += weights[i];
         }

         double finalR = 0.0;
         double finalG = 0.0;
         double finalB = 0.0;

         for (int i = 0; i < result.size(); i++) {
            double w = weights[i] / weightSum;
            finalR += result.get(i).centroid[0] * w;
            finalG += result.get(i).centroid[1] * w;
            finalB += result.get(i).centroid[2] * w;
         }

         int r = Math.clamp((int)finalR, 0, 255);
         int g = Math.clamp((int)finalG, 0, 255);
         int b = Math.clamp((int)finalB, 0, 255);
         return String.format("#%02X%02X%02X", r, g, b);
      }
   }

   private static List<ColorParseUtil.KMeansCluster> kmeans(List<int[]> pixels, int numClusters, Random rng) {
      int numPixels = pixels.size();
      ArrayList<double[]> centroids = new ArrayList<>();
      int[] first = pixels.get(rng.nextInt(numPixels));
      centroids.add(new double[]{first[0], first[1], first[2]});

      for (int i = 1; i < numClusters; i++) {
         double[] distances = new double[numPixels];
         double totalDistance = 0.0;

         for (int j = 0; j < numPixels; j++) {
            double minDistanceSq = Double.MAX_VALUE;

            for (double[] c : centroids) {
               double distanceSq = distSq(pixels.get(j), c);
               if (distanceSq < minDistanceSq) {
                  minDistanceSq = distanceSq;
               }
            }

            distances[j] = minDistanceSq;
            totalDistance += minDistanceSq;
         }

         if (totalDistance == 0.0) {
            break;
         }

         double pick = rng.nextDouble() * totalDistance;
         double cumulative = 0.0;

         for (int j = 0; j < numPixels; j++) {
            cumulative += distances[j];
            if (cumulative >= pick) {
               int[] p = pixels.get(j);
               centroids.add(new double[]{p[0], p[1], p[2]});
               break;
            }
         }
      }

      int actualClusters = centroids.size();
      int[] labels = new int[numPixels];
      int numRefineIterations = 10;

      for (int iter = 0; iter < numRefineIterations; iter++) {
         for (int j = 0; j < numPixels; j++) {
            double minDistance = Double.MAX_VALUE;

            for (int c = 0; c < actualClusters; c++) {
               double distance = distSq(pixels.get(j), centroids.get(c));
               if (distance < minDistance) {
                  minDistance = distance;
                  labels[j] = c;
               }
            }
         }

         boolean converged = true;

         for (int c = 0; c < actualClusters; c++) {
            double sr = 0.0;
            double sg = 0.0;
            double sb = 0.0;
            int count = 0;

            for (int j = 0; j < numPixels; j++) {
               if (labels[j] == c) {
                  sr += ((int[])pixels.get(j))[0];
                  sg += ((int[])pixels.get(j))[1];
                  sb += ((int[])pixels.get(j))[2];
                  count++;
               }
            }

            if (count != 0) {
               double nr = sr / count;
               double ng = sg / count;
               double nb = sb / count;
               double[] old = centroids.get(c);
               if (Math.abs(nr - old[0]) > 1.0 || Math.abs(ng - old[1]) > 1.0 || Math.abs(nb - old[2]) > 1.0) {
                  converged = false;
               }

               centroids.set(c, new double[]{nr, ng, nb});
            }
         }

         if (converged) {
            break;
         }
      }

      ArrayList<ColorParseUtil.KMeansCluster> results = new ArrayList<>();

      for (int c = 0; c < actualClusters; c++) {
         int count = 0;

         for (int j = 0; j < numPixels; j++) {
            if (labels[j] == c) {
               count++;
            }
         }

         results.add(new ColorParseUtil.KMeansCluster(centroids.get(c), count));
      }

      return results;
   }

   private static double distSq(int[] pixel, double[] centroid) {
      double dr = pixel[0] - centroid[0];
      double dg = pixel[1] - centroid[1];
      double db = pixel[2] - centroid[2];
      return dr * dr + dg * dg + db * db;
   }

   private record KMeansCluster(double[] centroid, int count) {
   }
}
