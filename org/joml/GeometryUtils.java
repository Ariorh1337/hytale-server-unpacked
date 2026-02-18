/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Math;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class GeometryUtils {
    public static void perpendicular(float x, float y, float z, Vector3f dest1, Vector3f dest2) {
        float mag;
        float magX = z * z + y * y;
        float magY = z * z + x * x;
        float magZ = y * y + x * x;
        if (magX > magY && magX > magZ) {
            dest1.x = 0.0f;
            dest1.y = z;
            dest1.z = -y;
            mag = magX;
        } else if (magY > magZ) {
            dest1.x = -z;
            dest1.y = 0.0f;
            dest1.z = x;
            mag = magY;
        } else {
            dest1.x = y;
            dest1.y = -x;
            dest1.z = 0.0f;
            mag = magZ;
        }
        float len = Math.invsqrt(mag);
        dest1.x *= len;
        dest1.y *= len;
        dest1.z *= len;
        dest2.x = y * dest1.z - z * dest1.y;
        dest2.y = z * dest1.x - x * dest1.z;
        dest2.z = x * dest1.y - y * dest1.x;
    }

    public static void perpendicular(Vector3fc v, Vector3f dest1, Vector3f dest2) {
        GeometryUtils.perpendicular(v.x(), v.y(), v.z(), dest1, dest2);
    }

    public static void normal(Vector3fc v0, Vector3fc v1, Vector3fc v2, Vector3f dest) {
        GeometryUtils.normal(v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), dest);
    }

    public static void normal(float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, Vector3f dest) {
        dest.x = (v1Y - v0Y) * (v2Z - v0Z) - (v1Z - v0Z) * (v2Y - v0Y);
        dest.y = (v1Z - v0Z) * (v2X - v0X) - (v1X - v0X) * (v2Z - v0Z);
        dest.z = (v1X - v0X) * (v2Y - v0Y) - (v1Y - v0Y) * (v2X - v0X);
        dest.normalize();
    }

    public static void tangent(Vector3fc v1, Vector2fc uv1, Vector3fc v2, Vector2fc uv2, Vector3fc v3, Vector2fc uv3, Vector3f dest) {
        float DeltaV1 = uv2.y() - uv1.y();
        float DeltaV2 = uv3.y() - uv1.y();
        float f = 1.0f / ((uv2.x() - uv1.x()) * DeltaV2 - (uv3.x() - uv1.x()) * DeltaV1);
        dest.x = f * (DeltaV2 * (v2.x() - v1.x()) - DeltaV1 * (v3.x() - v1.x()));
        dest.y = f * (DeltaV2 * (v2.y() - v1.y()) - DeltaV1 * (v3.y() - v1.y()));
        dest.z = f * (DeltaV2 * (v2.z() - v1.z()) - DeltaV1 * (v3.z() - v1.z()));
        dest.normalize();
    }

    public static void bitangent(Vector3fc v1, Vector2fc uv1, Vector3fc v2, Vector2fc uv2, Vector3fc v3, Vector2fc uv3, Vector3f dest) {
        float DeltaU1 = uv2.x() - uv1.x();
        float DeltaU2 = uv3.x() - uv1.x();
        float f = 1.0f / (DeltaU1 * (uv3.y() - uv1.y()) - DeltaU2 * (uv2.y() - uv1.y()));
        dest.x = f * (-DeltaU2 * (v2.x() - v1.x()) + DeltaU1 * (v3.x() - v1.x()));
        dest.y = f * (-DeltaU2 * (v2.y() - v1.y()) + DeltaU1 * (v3.y() - v1.y()));
        dest.z = f * (-DeltaU2 * (v2.z() - v1.z()) + DeltaU1 * (v3.z() - v1.z()));
        dest.normalize();
    }

    public static void tangentBitangent(Vector3fc v1, Vector2fc uv1, Vector3fc v2, Vector2fc uv2, Vector3fc v3, Vector2fc uv3, Vector3f destTangent, Vector3f destBitangent) {
        float DeltaV1 = uv2.y() - uv1.y();
        float DeltaV2 = uv3.y() - uv1.y();
        float DeltaU1 = uv2.x() - uv1.x();
        float DeltaU2 = uv3.x() - uv1.x();
        float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);
        destTangent.x = f * (DeltaV2 * (v2.x() - v1.x()) - DeltaV1 * (v3.x() - v1.x()));
        destTangent.y = f * (DeltaV2 * (v2.y() - v1.y()) - DeltaV1 * (v3.y() - v1.y()));
        destTangent.z = f * (DeltaV2 * (v2.z() - v1.z()) - DeltaV1 * (v3.z() - v1.z()));
        destTangent.normalize();
        destBitangent.x = f * (-DeltaU2 * (v2.x() - v1.x()) + DeltaU1 * (v3.x() - v1.x()));
        destBitangent.y = f * (-DeltaU2 * (v2.y() - v1.y()) + DeltaU1 * (v3.y() - v1.y()));
        destBitangent.z = f * (-DeltaU2 * (v2.z() - v1.z()) + DeltaU1 * (v3.z() - v1.z()));
        destBitangent.normalize();
    }
}

