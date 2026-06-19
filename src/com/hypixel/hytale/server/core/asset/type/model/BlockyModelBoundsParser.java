package com.hypixel.hytale.server.core.asset.type.model;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.server.core.asset.common.CommonAsset;
import com.hypixel.hytale.server.core.asset.common.CommonAssetRegistry;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockyModelBoundsParser {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final float BLOCK_SCALE = 0.03125F;
   private static final Vector3f[] BOX_CORNERS = new Vector3f[]{
      new Vector3f(-0.5F, 0.5F, -0.5F),
      new Vector3f(0.5F, 0.5F, -0.5F),
      new Vector3f(0.5F, -0.5F, -0.5F),
      new Vector3f(-0.5F, -0.5F, -0.5F),
      new Vector3f(0.5F, 0.5F, 0.5F),
      new Vector3f(-0.5F, 0.5F, 0.5F),
      new Vector3f(-0.5F, -0.5F, 0.5F),
      new Vector3f(0.5F, -0.5F, 0.5F)
   };

   @Nullable
   public static Box computeBounds(@Nonnull String modelPath) {
      CommonAsset asset = CommonAssetRegistry.getByName(modelPath);
      return asset == null ? null : computeBounds(asset);
   }

   @Nullable
   public static Box computeBounds(@Nonnull CommonAsset asset) {
      try {
         byte[] bytes = asset.getBlob().join();
         if (bytes == null) {
            return null;
         }

         BsonDocument json = BsonDocument.parse(new String(bytes, StandardCharsets.UTF_8));
         return computeBoundsFromJson(json);
      } catch (Exception e) {
         LOGGER.at(Level.WARNING).withCause(e).log("Failed to compute bounds for blockymodel: %s", asset.getName());
         return null;
      }
   }

   @Nullable
   private static Box computeBoundsFromJson(@Nonnull BsonDocument root) {
      BsonArray nodesArray = getArray(root, "nodes");
      if (nodesArray != null && !nodesArray.isEmpty()) {
         float minX = Float.MAX_VALUE;
         float minY = Float.MAX_VALUE;
         float minZ = Float.MAX_VALUE;
         float maxX = -Float.MAX_VALUE;
         float maxY = -Float.MAX_VALUE;
         float maxZ = -Float.MAX_VALUE;
         boolean hasPoints = false;
         float[] minMax = new float[]{minX, minY, minZ, maxX, maxY, maxZ};

         for (BsonValue nodeElement : nodesArray) {
            if (nodeElement.isDocument()) {
               hasPoints |= accumulateNodeBounds(nodeElement.asDocument(), new Vector3f(0.0F, 0.0F, 0.0F), new Quaternionf(), minMax);
            }
         }

         return !hasPoints
            ? null
            : new Box(minMax[0] * 0.03125F, minMax[1] * 0.03125F, minMax[2] * 0.03125F, minMax[3] * 0.03125F, minMax[4] * 0.03125F, minMax[5] * 0.03125F);
      } else {
         return null;
      }
   }

   private static boolean accumulateNodeBounds(
      @Nonnull BsonDocument node, @Nonnull Vector3f parentPosition, @Nonnull Quaternionf parentOrientation, @Nonnull float[] minMax
   ) {
      BsonDocument shape = getDocument(node, "shape");
      boolean visible = shape == null || !shape.containsKey("visible") || shape.get("visible").asBoolean().getValue();
      if (!visible) {
         return false;
      }

      Vector3f position = readVec3(getDocument(node, "position"), 0.0F, 0.0F, 0.0F);
      Quaternionf orientation = readQuat(getDocument(node, "orientation"));
      Vector3f offset = shape != null ? readVec3(getDocument(shape, "offset"), 0.0F, 0.0F, 0.0F) : new Vector3f();
      Vector3f localPosition = new Vector3f(offset);
      localPosition.rotate(orientation);
      localPosition.add(position);
      Vector3f worldPosition = new Vector3f(localPosition);
      worldPosition.rotate(parentOrientation);
      worldPosition.add(parentPosition);
      Quaternionf worldOrientation = new Quaternionf(parentOrientation);
      worldOrientation.mul(orientation);
      boolean hasPoints = false;
      if (shape != null) {
         String type = shape.containsKey("type") ? shape.get("type").asString().getValue() : "none";
         if ("box".equals(type) || "quad".equals(type)) {
            BsonDocument settings = getDocument(shape, "settings");
            Vector3f size = settings != null ? readVec3(getDocument(settings, "size"), 0.0F, 0.0F, 0.0F) : new Vector3f();
            Vector3f stretch = readVec3(getDocument(shape, "stretch"), 1.0F, 1.0F, 1.0F);
            float sx = size.x * stretch.x;
            float sy = size.y * stretch.y;
            float sz = size.z * stretch.z;
            Vector3f[] corners = "box".equals(type) ? BOX_CORNERS : getQuadCorners(shape);

            for (Vector3f corner : corners) {
               Vector3f scaled = new Vector3f(corner.x * sx, corner.y * sy, corner.z * sz);
               scaled.rotate(worldOrientation);
               scaled.add(worldPosition);
               minMax[0] = Math.min(minMax[0], scaled.x);
               minMax[1] = Math.min(minMax[1], scaled.y);
               minMax[2] = Math.min(minMax[2], scaled.z);
               minMax[3] = Math.max(minMax[3], scaled.x);
               minMax[4] = Math.max(minMax[4], scaled.y);
               minMax[5] = Math.max(minMax[5], scaled.z);
            }

            hasPoints = true;
         }
      }

      BsonArray children = getArray(node, "children");
      if (children != null) {
         for (BsonValue childElement : children) {
            if (childElement.isDocument()) {
               hasPoints |= accumulateNodeBounds(childElement.asDocument(), worldPosition, worldOrientation, minMax);
            }
         }
      }

      return hasPoints;
   }

   private static Vector3f[] getQuadCorners(@Nonnull BsonDocument shape) {
      BsonDocument settings = getDocument(shape, "settings");
      String normal = settings != null && settings.containsKey("normal") ? settings.get("normal").asString().getValue() : "+Z";

      return switch (normal) {
         case "+X", "-X" -> new Vector3f[]{
            new Vector3f(0.0F, -0.5F, -0.5F), new Vector3f(0.0F, 0.5F, -0.5F), new Vector3f(0.0F, 0.5F, 0.5F), new Vector3f(0.0F, -0.5F, 0.5F)
         };
         case "+Y", "-Y" -> new Vector3f[]{
            new Vector3f(-0.5F, 0.0F, -0.5F), new Vector3f(0.5F, 0.0F, -0.5F), new Vector3f(0.5F, 0.0F, 0.5F), new Vector3f(-0.5F, 0.0F, 0.5F)
         };
         default -> new Vector3f[]{
            new Vector3f(-0.5F, -0.5F, 0.0F), new Vector3f(0.5F, -0.5F, 0.0F), new Vector3f(0.5F, 0.5F, 0.0F), new Vector3f(-0.5F, 0.5F, 0.0F)
         };
      };
   }

   @Nonnull
   private static Vector3f readVec3(@Nullable BsonDocument obj, float defX, float defY, float defZ) {
      if (obj == null) {
         return new Vector3f(defX, defY, defZ);
      }

      float x = obj.containsKey("x") ? readFloat(obj.get("x")) : defX;
      float y = obj.containsKey("y") ? readFloat(obj.get("y")) : defY;
      float z = obj.containsKey("z") ? readFloat(obj.get("z")) : defZ;
      return new Vector3f(x, y, z);
   }

   @Nonnull
   private static Quaternionf readQuat(@Nullable BsonDocument obj) {
      if (obj == null) {
         return new Quaternionf();
      }

      float x = obj.containsKey("x") ? readFloat(obj.get("x")) : 0.0F;
      float y = obj.containsKey("y") ? readFloat(obj.get("y")) : 0.0F;
      float z = obj.containsKey("z") ? readFloat(obj.get("z")) : 0.0F;
      float w = obj.containsKey("w") ? readFloat(obj.get("w")) : 1.0F;
      return new Quaternionf(x, y, z, w);
   }

   private static float readFloat(@Nonnull BsonValue value) {
      return (float)value.asNumber().doubleValue();
   }

   @Nullable
   private static BsonDocument getDocument(@Nonnull BsonDocument obj, @Nonnull String key) {
      BsonValue value = obj.get(key);
      return value != null && value.isDocument() ? value.asDocument() : null;
   }

   @Nullable
   private static BsonArray getArray(@Nonnull BsonDocument obj, @Nonnull String key) {
      BsonValue value = obj.get(key);
      return value != null && value.isArray() ? value.asArray() : null;
   }
}
