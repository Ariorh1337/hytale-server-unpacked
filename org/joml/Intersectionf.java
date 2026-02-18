/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Math;
import org.joml.Runtime;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class Intersectionf {
    public static final int POINT_ON_TRIANGLE_VERTEX_0 = 1;
    public static final int POINT_ON_TRIANGLE_VERTEX_1 = 2;
    public static final int POINT_ON_TRIANGLE_VERTEX_2 = 3;
    public static final int POINT_ON_TRIANGLE_EDGE_01 = 4;
    public static final int POINT_ON_TRIANGLE_EDGE_12 = 5;
    public static final int POINT_ON_TRIANGLE_EDGE_20 = 6;
    public static final int POINT_ON_TRIANGLE_FACE = 7;
    public static final int AAR_SIDE_MINX = 0;
    public static final int AAR_SIDE_MINY = 1;
    public static final int AAR_SIDE_MAXX = 2;
    public static final int AAR_SIDE_MAXY = 3;
    public static final int OUTSIDE = -1;
    public static final int ONE_INTERSECTION = 1;
    public static final int TWO_INTERSECTION = 2;
    public static final int INSIDE = 3;

    public static boolean testPlaneSphere(float a, float b, float c, float d, float centerX, float centerY, float centerZ, float radius) {
        float denom = Math.sqrt(a * a + b * b + c * c);
        float dist = (a * centerX + b * centerY + c * centerZ + d) / denom;
        return -radius <= dist && dist <= radius;
    }

    public static boolean intersectPlaneSphere(float a, float b, float c, float d, float centerX, float centerY, float centerZ, float radius, Vector4f intersectionCenterAndRadius) {
        float invDenom = Math.invsqrt(a * a + b * b + c * c);
        float dist = (a * centerX + b * centerY + c * centerZ + d) * invDenom;
        if (-radius <= dist && dist <= radius) {
            intersectionCenterAndRadius.x = centerX + dist * a * invDenom;
            intersectionCenterAndRadius.y = centerY + dist * b * invDenom;
            intersectionCenterAndRadius.z = centerZ + dist * c * invDenom;
            intersectionCenterAndRadius.w = Math.sqrt(radius * radius - dist * dist);
            return true;
        }
        return false;
    }

    public static boolean intersectPlaneSweptSphere(float a, float b, float c, float d, float cX, float cY, float cZ, float radius, float vX, float vY, float vZ, Vector4f pointAndTime) {
        float dist = a * cX + b * cY + c * cZ - d;
        if (Math.abs(dist) <= radius) {
            pointAndTime.set(cX, cY, cZ, 0.0f);
            return true;
        }
        float denom = a * vX + b * vY + c * vZ;
        if (denom * dist >= 0.0f) {
            return false;
        }
        float r = dist > 0.0f ? radius : -radius;
        float t = (r - dist) / denom;
        pointAndTime.set(cX + t * vX - r * a, cY + t * vY - r * b, cZ + t * vZ - r * c, t);
        return true;
    }

    public static boolean testPlaneSweptSphere(float a, float b, float c, float d, float t0X, float t0Y, float t0Z, float r, float t1X, float t1Y, float t1Z) {
        float adist = t0X * a + t0Y * b + t0Z * c - d;
        float bdist = t1X * a + t1Y * b + t1Z * c - d;
        if (adist * bdist < 0.0f) {
            return true;
        }
        return Math.abs(adist) <= r || Math.abs(bdist) <= r;
    }

    public static boolean testAabPlane(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float a, float b, float c, float d) {
        float nZ;
        float pZ;
        float nY;
        float pY;
        float nX;
        float pX;
        if (a > 0.0f) {
            pX = maxX;
            nX = minX;
        } else {
            pX = minX;
            nX = maxX;
        }
        if (b > 0.0f) {
            pY = maxY;
            nY = minY;
        } else {
            pY = minY;
            nY = maxY;
        }
        if (c > 0.0f) {
            pZ = maxZ;
            nZ = minZ;
        } else {
            pZ = minZ;
            nZ = maxZ;
        }
        float distN = d + a * nX + b * nY + c * nZ;
        float distP = d + a * pX + b * pY + c * pZ;
        return distN <= 0.0f && distP >= 0.0f;
    }

    public static boolean testAabPlane(Vector3fc min, Vector3fc max, float a, float b, float c, float d) {
        return Intersectionf.testAabPlane(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), a, b, c, d);
    }

    public static boolean testAabAab(float minXA, float minYA, float minZA, float maxXA, float maxYA, float maxZA, float minXB, float minYB, float minZB, float maxXB, float maxYB, float maxZB) {
        return maxXA >= minXB && maxYA >= minYB && maxZA >= minZB && minXA <= maxXB && minYA <= maxYB && minZA <= maxZB;
    }

    public static boolean testAabAab(Vector3fc minA, Vector3fc maxA, Vector3fc minB, Vector3fc maxB) {
        return Intersectionf.testAabAab(minA.x(), minA.y(), minA.z(), maxA.x(), maxA.y(), maxA.z(), minB.x(), minB.y(), minB.z(), maxB.x(), maxB.y(), maxB.z());
    }

    public static boolean testObOb(Vector3f b0c, Vector3f b0uX, Vector3f b0uY, Vector3f b0uZ, Vector3f b0hs, Vector3f b1c, Vector3f b1uX, Vector3f b1uY, Vector3f b1uZ, Vector3f b1hs) {
        return Intersectionf.testObOb(b0c.x, b0c.y, b0c.z, b0uX.x, b0uX.y, b0uX.z, b0uY.x, b0uY.y, b0uY.z, b0uZ.x, b0uZ.y, b0uZ.z, b0hs.x, b0hs.y, b0hs.z, b1c.x, b1c.y, b1c.z, b1uX.x, b1uX.y, b1uX.z, b1uY.x, b1uY.y, b1uY.z, b1uZ.x, b1uZ.y, b1uZ.z, b1hs.x, b1hs.y, b1hs.z);
    }

    public static boolean testObOb(float b0cX, float b0cY, float b0cZ, float b0uXx, float b0uXy, float b0uXz, float b0uYx, float b0uYy, float b0uYz, float b0uZx, float b0uZy, float b0uZz, float b0hsX, float b0hsY, float b0hsZ, float b1cX, float b1cY, float b1cZ, float b1uXx, float b1uXy, float b1uXz, float b1uYx, float b1uYy, float b1uYz, float b1uZx, float b1uZy, float b1uZz, float b1hsX, float b1hsY, float b1hsZ) {
        float rm00 = b0uXx * b1uXx + b0uYx * b1uYx + b0uZx * b1uZx;
        float rm10 = b0uXx * b1uXy + b0uYx * b1uYy + b0uZx * b1uZy;
        float rm20 = b0uXx * b1uXz + b0uYx * b1uYz + b0uZx * b1uZz;
        float rm01 = b0uXy * b1uXx + b0uYy * b1uYx + b0uZy * b1uZx;
        float rm11 = b0uXy * b1uXy + b0uYy * b1uYy + b0uZy * b1uZy;
        float rm21 = b0uXy * b1uXz + b0uYy * b1uYz + b0uZy * b1uZz;
        float rm02 = b0uXz * b1uXx + b0uYz * b1uYx + b0uZz * b1uZx;
        float rm12 = b0uXz * b1uXy + b0uYz * b1uYy + b0uZz * b1uZy;
        float rm22 = b0uXz * b1uXz + b0uYz * b1uYz + b0uZz * b1uZz;
        float EPSILON = 1.0E-5f;
        float arm00 = Math.abs(rm00) + EPSILON;
        float arm01 = Math.abs(rm01) + EPSILON;
        float arm02 = Math.abs(rm02) + EPSILON;
        float arm10 = Math.abs(rm10) + EPSILON;
        float arm11 = Math.abs(rm11) + EPSILON;
        float arm12 = Math.abs(rm12) + EPSILON;
        float arm20 = Math.abs(rm20) + EPSILON;
        float arm21 = Math.abs(rm21) + EPSILON;
        float arm22 = Math.abs(rm22) + EPSILON;
        float tx = b1cX - b0cX;
        float ty = b1cY - b0cY;
        float tz = b1cZ - b0cZ;
        float tax = tx * b0uXx + ty * b0uXy + tz * b0uXz;
        float tay = tx * b0uYx + ty * b0uYy + tz * b0uYz;
        float taz = tx * b0uZx + ty * b0uZy + tz * b0uZz;
        float ra = b0hsX;
        float rb = b1hsX * arm00 + b1hsY * arm01 + b1hsZ * arm02;
        if (Math.abs(tax) > ra + rb) {
            return false;
        }
        ra = b0hsY;
        rb = b1hsX * arm10 + b1hsY * arm11 + b1hsZ * arm12;
        if (Math.abs(tay) > ra + rb) {
            return false;
        }
        ra = b0hsZ;
        rb = b1hsX * arm20 + b1hsY * arm21 + b1hsZ * arm22;
        if (Math.abs(taz) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm00 + b0hsY * arm10 + b0hsZ * arm20;
        rb = b1hsX;
        if (Math.abs(tax * rm00 + tay * rm10 + taz * rm20) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm01 + b0hsY * arm11 + b0hsZ * arm21;
        rb = b1hsY;
        if (Math.abs(tax * rm01 + tay * rm11 + taz * rm21) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm02 + b0hsY * arm12 + b0hsZ * arm22;
        rb = b1hsZ;
        if (Math.abs(tax * rm02 + tay * rm12 + taz * rm22) > ra + rb) {
            return false;
        }
        ra = b0hsY * arm20 + b0hsZ * arm10;
        rb = b1hsY * arm02 + b1hsZ * arm01;
        if (Math.abs(taz * rm10 - tay * rm20) > ra + rb) {
            return false;
        }
        ra = b0hsY * arm21 + b0hsZ * arm11;
        rb = b1hsX * arm02 + b1hsZ * arm00;
        if (Math.abs(taz * rm11 - tay * rm21) > ra + rb) {
            return false;
        }
        ra = b0hsY * arm22 + b0hsZ * arm12;
        rb = b1hsX * arm01 + b1hsY * arm00;
        if (Math.abs(taz * rm12 - tay * rm22) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm20 + b0hsZ * arm00;
        rb = b1hsY * arm12 + b1hsZ * arm11;
        if (Math.abs(tax * rm20 - taz * rm00) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm21 + b0hsZ * arm01;
        rb = b1hsX * arm12 + b1hsZ * arm10;
        if (Math.abs(tax * rm21 - taz * rm01) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm22 + b0hsZ * arm02;
        rb = b1hsX * arm11 + b1hsY * arm10;
        if (Math.abs(tax * rm22 - taz * rm02) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm10 + b0hsY * arm00;
        rb = b1hsY * arm22 + b1hsZ * arm21;
        if (Math.abs(tay * rm00 - tax * rm10) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm11 + b0hsY * arm01;
        rb = b1hsX * arm22 + b1hsZ * arm20;
        if (Math.abs(tay * rm01 - tax * rm11) > ra + rb) {
            return false;
        }
        ra = b0hsX * arm12 + b0hsY * arm02;
        rb = b1hsX * arm21 + b1hsY * arm20;
        return !(Math.abs(tay * rm02 - tax * rm12) > ra + rb);
    }

    public static boolean intersectSphereSphere(float aX, float aY, float aZ, float radiusSquaredA, float bX, float bY, float bZ, float radiusSquaredB, Vector4f centerAndRadiusOfIntersectionCircle) {
        float dX = bX - aX;
        float dY = bY - aY;
        float dZ = bZ - aZ;
        float distSquared = dX * dX + dY * dY + dZ * dZ;
        float h = 0.5f + (radiusSquaredA - radiusSquaredB) / (2.0f * distSquared);
        float r_i = radiusSquaredA - h * h * distSquared;
        if (r_i >= 0.0f) {
            centerAndRadiusOfIntersectionCircle.x = aX + h * dX;
            centerAndRadiusOfIntersectionCircle.y = aY + h * dY;
            centerAndRadiusOfIntersectionCircle.z = aZ + h * dZ;
            centerAndRadiusOfIntersectionCircle.w = Math.sqrt(r_i);
            return true;
        }
        return false;
    }

    public static boolean intersectSphereSphere(Vector3fc centerA, float radiusSquaredA, Vector3fc centerB, float radiusSquaredB, Vector4f centerAndRadiusOfIntersectionCircle) {
        return Intersectionf.intersectSphereSphere(centerA.x(), centerA.y(), centerA.z(), radiusSquaredA, centerB.x(), centerB.y(), centerB.z(), radiusSquaredB, centerAndRadiusOfIntersectionCircle);
    }

    public static int intersectSphereTriangle(float sX, float sY, float sZ, float sR, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, Vector3f result) {
        int closest = Intersectionf.findClosestPointOnTriangle(v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, sX, sY, sZ, result);
        float vX = result.x - sX;
        float vY = result.y - sY;
        float vZ = result.z - sZ;
        float dot = vX * vX + vY * vY + vZ * vZ;
        if (dot <= sR * sR) {
            return closest;
        }
        return 0;
    }

    public static boolean testSphereSphere(float aX, float aY, float aZ, float radiusSquaredA, float bX, float bY, float bZ, float radiusSquaredB) {
        float dX = bX - aX;
        float dY = bY - aY;
        float dZ = bZ - aZ;
        float distSquared = dX * dX + dY * dY + dZ * dZ;
        float h = 0.5f + (radiusSquaredA - radiusSquaredB) / (2.0f * distSquared);
        float r_i = radiusSquaredA - h * h * distSquared;
        return r_i >= 0.0f;
    }

    public static boolean testSphereSphere(Vector3fc centerA, float radiusSquaredA, Vector3fc centerB, float radiusSquaredB) {
        return Intersectionf.testSphereSphere(centerA.x(), centerA.y(), centerA.z(), radiusSquaredA, centerB.x(), centerB.y(), centerB.z(), radiusSquaredB);
    }

    public static float distancePointPlane(float pointX, float pointY, float pointZ, float a, float b, float c, float d) {
        float denom = Math.sqrt(a * a + b * b + c * c);
        return (a * pointX + b * pointY + c * pointZ + d) / denom;
    }

    public static float distancePointPlane(float pointX, float pointY, float pointZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z) {
        float v1Y0Y = v1Y - v0Y;
        float v2Z0Z = v2Z - v0Z;
        float v2Y0Y = v2Y - v0Y;
        float v1Z0Z = v1Z - v0Z;
        float v2X0X = v2X - v0X;
        float v1X0X = v1X - v0X;
        float a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z;
        float b = v1Z0Z * v2X0X - v2Z0Z * v1X0X;
        float c = v1X0X * v2Y0Y - v2X0X * v1Y0Y;
        float d = -(a * v0X + b * v0Y + c * v0Z);
        return Intersectionf.distancePointPlane(pointX, pointY, pointZ, a, b, c, d);
    }

    public static float intersectRayPlane(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float pointX, float pointY, float pointZ, float normalX, float normalY, float normalZ, float epsilon) {
        float t;
        float denom = normalX * dirX + normalY * dirY + normalZ * dirZ;
        if (denom < epsilon && (t = ((pointX - originX) * normalX + (pointY - originY) * normalY + (pointZ - originZ) * normalZ) / denom) >= 0.0f) {
            return t;
        }
        return -1.0f;
    }

    public static float intersectRayPlane(Vector3fc origin, Vector3fc dir, Vector3fc point, Vector3fc normal, float epsilon) {
        return Intersectionf.intersectRayPlane(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), point.x(), point.y(), point.z(), normal.x(), normal.y(), normal.z(), epsilon);
    }

    public static float intersectRayPlane(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float a, float b, float c, float d, float epsilon) {
        float t;
        float denom = a * dirX + b * dirY + c * dirZ;
        if (denom < epsilon && (t = -(a * originX + b * originY + c * originZ + d) / denom) >= 0.0f) {
            return t;
        }
        return -1.0f;
    }

    public static boolean testAabSphere(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float centerX, float centerY, float centerZ, float radiusSquared) {
        float d;
        float radius2 = radiusSquared;
        if (centerX < minX) {
            d = centerX - minX;
            radius2 -= d * d;
        } else if (centerX > maxX) {
            d = centerX - maxX;
            radius2 -= d * d;
        }
        if (centerY < minY) {
            d = centerY - minY;
            radius2 -= d * d;
        } else if (centerY > maxY) {
            d = centerY - maxY;
            radius2 -= d * d;
        }
        if (centerZ < minZ) {
            d = centerZ - minZ;
            radius2 -= d * d;
        } else if (centerZ > maxZ) {
            d = centerZ - maxZ;
            radius2 -= d * d;
        }
        return radius2 >= 0.0f;
    }

    public static boolean testAabSphere(Vector3fc min, Vector3fc max, Vector3fc center, float radiusSquared) {
        return Intersectionf.testAabSphere(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), center.x(), center.y(), center.z(), radiusSquared);
    }

    public static Vector3f findClosestPointOnPlane(float aX, float aY, float aZ, float nX, float nY, float nZ, float pX, float pY, float pZ, Vector3f result) {
        float d = -(nX * aX + nY * aY + nZ * aZ);
        float t = nX * pX + nY * pY + nZ * pZ - d;
        result.x = pX - t * nX;
        result.y = pY - t * nY;
        result.z = pZ - t * nZ;
        return result;
    }

    public static Vector3f findClosestPointOnLineSegment(float aX, float aY, float aZ, float bX, float bY, float bZ, float pX, float pY, float pZ, Vector3f result) {
        float abX = bX - aX;
        float abY = bY - aY;
        float abZ = bZ - aZ;
        float t = ((pX - aX) * abX + (pY - aY) * abY + (pZ - aZ) * abZ) / (abX * abX + abY * abY + abZ * abZ);
        if (t < 0.0f) {
            t = 0.0f;
        }
        if (t > 1.0f) {
            t = 1.0f;
        }
        result.x = aX + t * abX;
        result.y = aY + t * abY;
        result.z = aZ + t * abZ;
        return result;
    }

    public static float findClosestPointsLineSegments(float a0X, float a0Y, float a0Z, float a1X, float a1Y, float a1Z, float b0X, float b0Y, float b0Z, float b1X, float b1Y, float b1Z, Vector3f resultA, Vector3f resultB) {
        float t;
        float s;
        float d1x = a1X - a0X;
        float d1y = a1Y - a0Y;
        float d1z = a1Z - a0Z;
        float d2x = b1X - b0X;
        float d2y = b1Y - b0Y;
        float d2z = b1Z - b0Z;
        float rX = a0X - b0X;
        float rY = a0Y - b0Y;
        float rZ = a0Z - b0Z;
        float a = d1x * d1x + d1y * d1y + d1z * d1z;
        float invA = 1.0f / a;
        float e = d2x * d2x + d2y * d2y + d2z * d2z;
        float f = d2x * rX + d2y * rY + d2z * rZ;
        float EPSILON = 1.0E-5f;
        if (a <= EPSILON && e <= EPSILON) {
            resultA.set(a0X, a0Y, a0Z);
            resultB.set(b0X, b0Y, b0Z);
            return resultA.dot(resultB);
        }
        if (a <= EPSILON) {
            s = 0.0f;
            t = f / e;
            t = Math.min(Math.max(t, 0.0f), 1.0f);
        } else {
            float c = d1x * rX + d1y * rY + d1z * rZ;
            if (e <= EPSILON) {
                t = 0.0f;
                s = Math.min(Math.max(-c * invA, 0.0f), 1.0f);
            } else {
                float b;
                float denom = a * e - (b = d1x * d2x + d1y * d2y + d1z * d2z) * b;
                s = (double)denom != 0.0 ? Math.min(Math.max((b * f - c * e) / denom, 0.0f), 1.0f) : 0.0f;
                t = (b * s + f) / e;
                if ((double)t < 0.0) {
                    t = 0.0f;
                    s = Math.min(Math.max(-c * invA, 0.0f), 1.0f);
                } else if ((double)t > 1.0) {
                    t = 1.0f;
                    s = Math.min(Math.max((b - c) * invA, 0.0f), 1.0f);
                }
            }
        }
        resultA.set(a0X + d1x * s, a0Y + d1y * s, a0Z + d1z * s);
        resultB.set(b0X + d2x * t, b0Y + d2y * t, b0Z + d2z * t);
        float dX = resultA.x - resultB.x;
        float dY = resultA.y - resultB.y;
        float dZ = resultA.z - resultB.z;
        return dX * dX + dY * dY + dZ * dZ;
    }

    public static float findClosestPointsLineSegmentTriangle(float aX, float aY, float aZ, float bX, float bY, float bZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, Vector3f lineSegmentResult, Vector3f triangleResult) {
        float invLen;
        float v1X0X;
        float v2X0X;
        float v1Z0Z;
        float v2Y0Y;
        float v2Z0Z;
        float v1Y0Y;
        float d;
        float min = d = Intersectionf.findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, lineSegmentResult, triangleResult);
        float minlsX = lineSegmentResult.x;
        float minlsY = lineSegmentResult.y;
        float minlsZ = lineSegmentResult.z;
        float mintX = triangleResult.x;
        float mintY = triangleResult.y;
        float mintZ = triangleResult.z;
        d = Intersectionf.findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v1X, v1Y, v1Z, v2X, v2Y, v2Z, lineSegmentResult, triangleResult);
        if (d < min) {
            min = d;
            minlsX = lineSegmentResult.x;
            minlsY = lineSegmentResult.y;
            minlsZ = lineSegmentResult.z;
            mintX = triangleResult.x;
            mintY = triangleResult.y;
            mintZ = triangleResult.z;
        }
        if ((d = Intersectionf.findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v2X, v2Y, v2Z, v0X, v0Y, v0Z, lineSegmentResult, triangleResult)) < min) {
            min = d;
            minlsX = lineSegmentResult.x;
            minlsY = lineSegmentResult.y;
            minlsZ = lineSegmentResult.z;
            mintX = triangleResult.x;
            mintY = triangleResult.y;
            mintZ = triangleResult.z;
        }
        boolean computed = false;
        float a = Float.NaN;
        float b = Float.NaN;
        float c = Float.NaN;
        float nd = Float.NaN;
        if (Intersectionf.testPointInTriangle(aX, aY, aZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)) {
            v1Y0Y = v1Y - v0Y;
            v2Z0Z = v2Z - v0Z;
            v2Y0Y = v2Y - v0Y;
            v1Z0Z = v1Z - v0Z;
            v2X0X = v2X - v0X;
            v1X0X = v1X - v0X;
            a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z;
            b = v1Z0Z * v2X0X - v2Z0Z * v1X0X;
            c = v1X0X * v2Y0Y - v2X0X * v1Y0Y;
            computed = true;
            invLen = Math.invsqrt(a * a + b * b + c * c);
            nd = -((a *= invLen) * v0X + (b *= invLen) * v0Y + (c *= invLen) * v0Z);
            float l = d = a * aX + b * aY + c * aZ + nd;
            if ((d *= d) < min) {
                min = d;
                minlsX = aX;
                minlsY = aY;
                minlsZ = aZ;
                mintX = aX - a * l;
                mintY = aY - b * l;
                mintZ = aZ - c * l;
            }
        }
        if (Intersectionf.testPointInTriangle(bX, bY, bZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)) {
            if (!computed) {
                v1Y0Y = v1Y - v0Y;
                v2Z0Z = v2Z - v0Z;
                v2Y0Y = v2Y - v0Y;
                v1Z0Z = v1Z - v0Z;
                v2X0X = v2X - v0X;
                v1X0X = v1X - v0X;
                a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z;
                b = v1Z0Z * v2X0X - v2Z0Z * v1X0X;
                c = v1X0X * v2Y0Y - v2X0X * v1Y0Y;
                invLen = Math.invsqrt(a * a + b * b + c * c);
                nd = -((a *= invLen) * v0X + (b *= invLen) * v0Y + (c *= invLen) * v0Z);
            }
            float l = d = a * bX + b * bY + c * bZ + nd;
            if ((d *= d) < min) {
                min = d;
                minlsX = bX;
                minlsY = bY;
                minlsZ = bZ;
                mintX = bX - a * l;
                mintY = bY - b * l;
                mintZ = bZ - c * l;
            }
        }
        lineSegmentResult.set(minlsX, minlsY, minlsZ);
        triangleResult.set(mintX, mintY, mintZ);
        return min;
    }

    public static int findClosestPointOnTriangle(float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float pX, float pY, float pZ, Vector3f result) {
        float abX = v1X - v0X;
        float abY = v1Y - v0Y;
        float abZ = v1Z - v0Z;
        float acX = v2X - v0X;
        float acY = v2Y - v0Y;
        float acZ = v2Z - v0Z;
        float apX = pX - v0X;
        float apY = pY - v0Y;
        float apZ = pZ - v0Z;
        float d1 = abX * apX + abY * apY + abZ * apZ;
        float d2 = acX * apX + acY * apY + acZ * apZ;
        if (d1 <= 0.0f && d2 <= 0.0f) {
            result.x = v0X;
            result.y = v0Y;
            result.z = v0Z;
            return 1;
        }
        float bpX = pX - v1X;
        float bpY = pY - v1Y;
        float bpZ = pZ - v1Z;
        float d3 = abX * bpX + abY * bpY + abZ * bpZ;
        float d4 = acX * bpX + acY * bpY + acZ * bpZ;
        if (d3 >= 0.0f && d4 <= d3) {
            result.x = v1X;
            result.y = v1Y;
            result.z = v1Z;
            return 2;
        }
        float vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0f && d1 >= 0.0f && d3 <= 0.0f) {
            float v = d1 / (d1 - d3);
            result.x = v0X + v * abX;
            result.y = v0Y + v * abY;
            result.z = v0Z + v * abZ;
            return 4;
        }
        float cpX = pX - v2X;
        float cpY = pY - v2Y;
        float cpZ = pZ - v2Z;
        float d5 = abX * cpX + abY * cpY + abZ * cpZ;
        float d6 = acX * cpX + acY * cpY + acZ * cpZ;
        if (d6 >= 0.0f && d5 <= d6) {
            result.x = v2X;
            result.y = v2Y;
            result.z = v2Z;
            return 3;
        }
        float vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f) {
            float w = d2 / (d2 - d6);
            result.x = v0X + w * acX;
            result.y = v0Y + w * acY;
            result.z = v0Z + w * acZ;
            return 6;
        }
        float va = d3 * d6 - d5 * d4;
        if (va <= 0.0f && d4 - d3 >= 0.0f && d5 - d6 >= 0.0f) {
            float w = (d4 - d3) / (d4 - d3 + d5 - d6);
            result.x = v1X + w * (v2X - v1X);
            result.y = v1Y + w * (v2Y - v1Y);
            result.z = v1Z + w * (v2Z - v1Z);
            return 5;
        }
        float denom = 1.0f / (va + vb + vc);
        float v = vb * denom;
        float w = vc * denom;
        result.x = v0X + abX * v + acX * w;
        result.y = v0Y + abY * v + acY * w;
        result.z = v0Z + abZ * v + acZ * w;
        return 7;
    }

    public static int findClosestPointOnTriangle(Vector3fc v0, Vector3fc v1, Vector3fc v2, Vector3fc p, Vector3f result) {
        return Intersectionf.findClosestPointOnTriangle(v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), p.x(), p.y(), p.z(), result);
    }

    public static Vector3f findClosestPointOnRectangle(float aX, float aY, float aZ, float bX, float bY, float bZ, float cX, float cY, float cZ, float pX, float pY, float pZ, Vector3f res) {
        float distTimesInvMaxDist;
        float abX = bX - aX;
        float abY = bY - aY;
        float abZ = bZ - aZ;
        float acX = cX - aX;
        float acY = cY - aY;
        float acZ = cZ - aZ;
        float dX = pX - aX;
        float dY = pY - aY;
        float dZ = pZ - aZ;
        float qX = aX;
        float qY = aY;
        float qZ = aZ;
        float dist = dX * abX + dY * abY + dZ * abZ;
        float maxdist = abX * abX + abY * abY + abZ * abZ;
        float invMaxdist = 1.0f / maxdist;
        if (dist >= maxdist) {
            qX += abX;
            qY += abY;
            qZ += abZ;
        } else if (dist > 0.0f) {
            distTimesInvMaxDist = dist * invMaxdist;
            qX += distTimesInvMaxDist * abX;
            qY += distTimesInvMaxDist * abY;
            qZ += distTimesInvMaxDist * abZ;
        }
        dist = dX * acX + dY * acY + dZ * acZ;
        maxdist = acX * acX + acY * acY + acZ * acZ;
        invMaxdist = 1.0f / maxdist;
        if (dist >= maxdist) {
            qX += acX;
            qY += acY;
            qZ += acZ;
        } else if (dist > 0.0f) {
            distTimesInvMaxDist = dist * invMaxdist;
            qX += distTimesInvMaxDist * acX;
            qY += distTimesInvMaxDist * acY;
            qZ += distTimesInvMaxDist * acZ;
        }
        res.x = qX;
        res.y = qY;
        res.z = qZ;
        return res;
    }

    public static int intersectSweptSphereTriangle(float centerX, float centerY, float centerZ, float radius, float velX, float velY, float velZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon, float maxT, Vector4f pointAndTime) {
        float baseTo1Len;
        float C21;
        float v21BaseTo1;
        float velBaseTo1;
        float B21;
        float len21;
        float A21;
        float root21;
        float v21Z;
        float v21Y;
        float v21X;
        float v21Vel;
        float f21;
        float C20;
        float v20BaseTo0;
        float B20;
        float len20;
        float A20;
        float root20;
        float v20Vel;
        float f20;
        float C10;
        float v10BaseTo0;
        float velBaseTo0;
        float B10;
        float velLen;
        float len10;
        float A10;
        float root10;
        float v10Vel;
        float f10;
        float C2;
        float centerV2Z;
        float centerV2Y;
        float centerV2X;
        float B2;
        float root2;
        float centerV1Len;
        float C1;
        float centerV1Z;
        float centerV1Y;
        float centerV1X;
        float B1;
        float root1;
        float v10X = v1X - v0X;
        float v10Y = v1Y - v0Y;
        float v10Z = v1Z - v0Z;
        float v20X = v2X - v0X;
        float v20Y = v2Y - v0Y;
        float v20Z = v2Z - v0Z;
        float a = v10Y * v20Z - v20Y * v10Z;
        float b = v10Z * v20X - v20Z * v10X;
        float c = v10X * v20Y - v20X * v10Y;
        float d = -(a * v0X + b * v0Y + c * v0Z);
        float invLen = Math.invsqrt(a * a + b * b + c * c);
        float signedDist = (a * centerX + b * centerY + c * centerZ + d) * invLen;
        float dot = (a * velX + b * velY + c * velZ) * invLen;
        float invDot = 1.0f / dot;
        if (dot < epsilon && dot > -epsilon) {
            return 0;
        }
        float pt0 = (radius - signedDist) * invDot;
        if (pt0 > maxT) {
            return 0;
        }
        float pt1 = (-radius - signedDist) * invDot;
        float p0X = centerX - radius * a * invLen + velX * pt0;
        float p0Y = centerY - radius * b * invLen + velY * pt0;
        float p0Z = centerZ - radius * c * invLen + velZ * pt0;
        boolean insideTriangle = Intersectionf.testPointInTriangle(p0X, p0Y, p0Z, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z);
        if (insideTriangle) {
            pointAndTime.x = p0X;
            pointAndTime.y = p0Y;
            pointAndTime.z = p0Z;
            pointAndTime.w = pt0;
            return 7;
        }
        int isect = 0;
        float A = velX * velX + velY * velY + velZ * velZ;
        float centerV0X = centerX - v0X;
        float centerV0Y = centerY - v0Y;
        float centerV0Z = centerZ - v0Z;
        float B0 = 2.0f * (velX * centerV0X + velY * centerV0Y + velZ * centerV0Z);
        float baseTo0Len = centerV0X * centerV0X + centerV0Y * centerV0Y + centerV0Z * centerV0Z;
        float radius2 = radius * radius;
        float C0 = baseTo0Len - radius2;
        float t0 = maxT;
        float root0 = Intersectionf.computeLowestRoot(A, B0, C0, t0);
        if (root0 < t0) {
            pointAndTime.x = v0X;
            pointAndTime.y = v0Y;
            pointAndTime.z = v0Z;
            pointAndTime.w = root0;
            t0 = root0;
            isect = 1;
        }
        if ((root1 = Intersectionf.computeLowestRoot(A, B1 = 2.0f * (velX * (centerV1X = centerX - v1X) + velY * (centerV1Y = centerY - v1Y) + velZ * (centerV1Z = centerZ - v1Z)), C1 = (centerV1Len = centerV1X * centerV1X + centerV1Y * centerV1Y + centerV1Z * centerV1Z) - radius2, t0)) < t0) {
            pointAndTime.x = v1X;
            pointAndTime.y = v1Y;
            pointAndTime.z = v1Z;
            pointAndTime.w = root1;
            t0 = root1;
            isect = 2;
        }
        if ((root2 = Intersectionf.computeLowestRoot(A, B2 = 2.0f * (velX * (centerV2X = centerX - v2X) + velY * (centerV2Y = centerY - v2Y) + velZ * (centerV2Z = centerZ - v2Z)), C2 = centerV2X * centerV2X + centerV2Y * centerV2Y + centerV2Z * centerV2Z - radius2, t0)) < t0) {
            pointAndTime.x = v2X;
            pointAndTime.y = v2Y;
            pointAndTime.z = v2Z;
            pointAndTime.w = root2;
            t0 = root2;
            isect = 3;
        }
        if ((f10 = ((v10Vel = v10X * velX + v10Y * velY + v10Z * velZ) * (root10 = Intersectionf.computeLowestRoot(A10 = (len10 = v10X * v10X + v10Y * v10Y + v10Z * v10Z) * -(velLen = A) + v10Vel * v10Vel, B10 = len10 * 2.0f * (velBaseTo0 = velX * -centerV0X + velY * -centerV0Y + velZ * -centerV0Z) - 2.0f * v10Vel * (v10BaseTo0 = v10X * -centerV0X + v10Y * -centerV0Y + v10Z * -centerV0Z), C10 = len10 * (radius2 - baseTo0Len) + v10BaseTo0 * v10BaseTo0, t0)) - v10BaseTo0) / len10) >= 0.0f && f10 <= 1.0f && root10 < t0) {
            pointAndTime.x = v0X + f10 * v10X;
            pointAndTime.y = v0Y + f10 * v10Y;
            pointAndTime.z = v0Z + f10 * v10Z;
            pointAndTime.w = root10;
            t0 = root10;
            isect = 4;
        }
        if ((f20 = ((v20Vel = v20X * velX + v20Y * velY + v20Z * velZ) * (root20 = Intersectionf.computeLowestRoot(A20 = (len20 = v20X * v20X + v20Y * v20Y + v20Z * v20Z) * -velLen + v20Vel * v20Vel, B20 = len20 * 2.0f * velBaseTo0 - 2.0f * v20Vel * (v20BaseTo0 = v20X * -centerV0X + v20Y * -centerV0Y + v20Z * -centerV0Z), C20 = len20 * (radius2 - baseTo0Len) + v20BaseTo0 * v20BaseTo0, t0)) - v20BaseTo0) / len20) >= 0.0f && f20 <= 1.0f && root20 < pt1) {
            pointAndTime.x = v0X + f20 * v20X;
            pointAndTime.y = v0Y + f20 * v20Y;
            pointAndTime.z = v0Z + f20 * v20Z;
            pointAndTime.w = root20;
            t0 = root20;
            isect = 6;
        }
        if ((f21 = ((v21Vel = (v21X = v2X - v1X) * velX + (v21Y = v2Y - v1Y) * velY + (v21Z = v2Z - v1Z) * velZ) * (root21 = Intersectionf.computeLowestRoot(A21 = (len21 = v21X * v21X + v21Y * v21Y + v21Z * v21Z) * -velLen + v21Vel * v21Vel, B21 = len21 * 2.0f * (velBaseTo1 = velX * -centerV1X + velY * -centerV1Y + velZ * -centerV1Z) - 2.0f * v21Vel * (v21BaseTo1 = v21X * -centerV1X + v21Y * -centerV1Y + v21Z * -centerV1Z), C21 = len21 * (radius2 - (baseTo1Len = centerV1Len)) + v21BaseTo1 * v21BaseTo1, t0)) - v21BaseTo1) / len21) >= 0.0f && f21 <= 1.0f && root21 < t0) {
            pointAndTime.x = v1X + f21 * v21X;
            pointAndTime.y = v1Y + f21 * v21Y;
            pointAndTime.z = v1Z + f21 * v21Z;
            pointAndTime.w = root21;
            isect = 5;
        }
        return isect;
    }

    private static float computeLowestRoot(float a, float b, float c, float maxR) {
        float r2;
        float invA2;
        float determinant = b * b - 4.0f * a * c;
        if (determinant < 0.0f) {
            return Float.POSITIVE_INFINITY;
        }
        float sqrtD = Math.sqrt(determinant);
        float r1 = (-b - sqrtD) * (invA2 = 1.0f / (2.0f * a));
        if (r1 > (r2 = (-b + sqrtD) * invA2)) {
            float temp = r2;
            r2 = r1;
            r1 = temp;
        }
        if (r1 > 0.0f && r1 < maxR) {
            return r1;
        }
        if (r2 > 0.0f && r2 < maxR) {
            return r2;
        }
        return Float.POSITIVE_INFINITY;
    }

    public static boolean testPointInTriangle(float pX, float pY, float pZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z) {
        float vpX = pX - v0X;
        float e10X = v1X - v0X;
        float vpY = pY - v0Y;
        float e10Y = v1Y - v0Y;
        float vpZ = pZ - v0Z;
        float e10Z = v1Z - v0Z;
        float d = vpX * e10X + vpY * e10Y + vpZ * e10Z;
        float e20X = v2X - v0X;
        float e20Y = v2Y - v0Y;
        float e20Z = v2Z - v0Z;
        float c = e20X * e20X + e20Y * e20Y + e20Z * e20Z;
        float e = vpX * e20X + vpY * e20Y + vpZ * e20Z;
        float b = e10X * e20X + e10Y * e20Y + e10Z * e20Z;
        float x = d * c - e * b;
        float a = e10X * e10X + e10Y * e10Y + e10Z * e10Z;
        float y = e * a - d * b;
        float ac_bb = a * c - b * b;
        float z = x + y - ac_bb;
        return ((long)(Runtime.floatToIntBits(z) & ~(Runtime.floatToIntBits(x) | Runtime.floatToIntBits(y))) & Long.MIN_VALUE) != 0L;
    }

    public static boolean intersectRaySphere(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float centerX, float centerY, float centerZ, float radiusSquared, Vector2f result) {
        float t1;
        float Lx = centerX - originX;
        float Ly = centerY - originY;
        float Lz = centerZ - originZ;
        float tca = Lx * dirX + Ly * dirY + Lz * dirZ;
        float d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        float thc = Math.sqrt(radiusSquared - d2);
        float t0 = tca - thc;
        if (t0 < (t1 = tca + thc) && t1 >= 0.0f) {
            result.x = t0;
            result.y = t1;
            return true;
        }
        return false;
    }

    public static boolean intersectRaySphere(Vector3fc origin, Vector3fc dir, Vector3fc center, float radiusSquared, Vector2f result) {
        return Intersectionf.intersectRaySphere(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), center.x(), center.y(), center.z(), radiusSquared, result);
    }

    public static boolean testRaySphere(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float centerX, float centerY, float centerZ, float radiusSquared) {
        float t1;
        float Lx = centerX - originX;
        float Ly = centerY - originY;
        float Lz = centerZ - originZ;
        float tca = Lx * dirX + Ly * dirY + Lz * dirZ;
        float d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        float thc = Math.sqrt(radiusSquared - d2);
        float t0 = tca - thc;
        return t0 < (t1 = tca + thc) && t1 >= 0.0f;
    }

    public static boolean testRaySphere(Vector3fc origin, Vector3fc dir, Vector3fc center, float radiusSquared) {
        return Intersectionf.testRaySphere(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), center.x(), center.y(), center.z(), radiusSquared);
    }

    public static boolean testLineSegmentSphere(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, float centerX, float centerY, float centerZ, float radiusSquared) {
        float dX = p1X - p0X;
        float dY = p1Y - p0Y;
        float dZ = p1Z - p0Z;
        float nom = (centerX - p0X) * dX + (centerY - p0Y) * dY + (centerZ - p0Z) * dZ;
        float den = dX * dX + dY * dY + dZ * dZ;
        float u = nom / den;
        if (u < 0.0f) {
            dX = p0X - centerX;
            dY = p0Y - centerY;
            dZ = p0Z - centerZ;
        } else if (u > 1.0f) {
            dX = p1X - centerX;
            dY = p1Y - centerY;
            dZ = p1Z - centerZ;
        } else {
            float pX = p0X + u * dX;
            float pY = p0Y + u * dY;
            float pZ = p0Z + u * dZ;
            dX = pX - centerX;
            dY = pY - centerY;
            dZ = pZ - centerZ;
        }
        float dist = dX * dX + dY * dY + dZ * dZ;
        return dist <= radiusSquared;
    }

    public static boolean testLineSegmentSphere(Vector3fc p0, Vector3fc p1, Vector3fc center, float radiusSquared) {
        return Intersectionf.testLineSegmentSphere(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), center.x(), center.y(), center.z(), radiusSquared);
    }

    public static boolean intersectRayAab(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vector2f result) {
        float tzmax;
        float tzmin;
        float tymax;
        float tymin;
        float tFar;
        float tNear;
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        float invDirZ = 1.0f / dirZ;
        if (invDirX >= 0.0f) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return false;
        }
        if (invDirZ >= 0.0f) {
            tzmin = (minZ - originZ) * invDirZ;
            tzmax = (maxZ - originZ) * invDirZ;
        } else {
            tzmin = (maxZ - originZ) * invDirZ;
            tzmax = (minZ - originZ) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar) {
            return false;
        }
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        float f = tFar = tzmax < tFar ? tzmax : tFar;
        if (tNear < tFar && tFar >= 0.0f) {
            result.x = tNear;
            result.y = tFar;
            return true;
        }
        return false;
    }

    public static boolean intersectRayAab(Vector3fc origin, Vector3fc dir, Vector3fc min, Vector3fc max, Vector2f result) {
        return Intersectionf.intersectRayAab(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), result);
    }

    public static int intersectLineSegmentAab(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vector2f result) {
        float tzmax;
        float tzmin;
        float tymax;
        float tymin;
        float tFar;
        float tNear;
        float dirX = p1X - p0X;
        float dirY = p1Y - p0Y;
        float dirZ = p1Z - p0Z;
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        float invDirZ = 1.0f / dirZ;
        if (invDirX >= 0.0f) {
            tNear = (minX - p0X) * invDirX;
            tFar = (maxX - p0X) * invDirX;
        } else {
            tNear = (maxX - p0X) * invDirX;
            tFar = (minX - p0X) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - p0Y) * invDirY;
            tymax = (maxY - p0Y) * invDirY;
        } else {
            tymin = (maxY - p0Y) * invDirY;
            tymax = (minY - p0Y) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return -1;
        }
        if (invDirZ >= 0.0f) {
            tzmin = (minZ - p0Z) * invDirZ;
            tzmax = (maxZ - p0Z) * invDirZ;
        } else {
            tzmin = (maxZ - p0Z) * invDirZ;
            tzmax = (minZ - p0Z) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar) {
            return -1;
        }
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        tFar = tzmax < tFar ? tzmax : tFar;
        int type = -1;
        if (tNear <= tFar && tNear <= 1.0f && tFar >= 0.0f) {
            if (tNear >= 0.0f && tFar > 1.0f) {
                tFar = tNear;
                type = 1;
            } else if (tNear < 0.0f && tFar <= 1.0f) {
                tNear = tFar;
                type = 1;
            } else {
                type = tNear < 0.0f && tFar > 1.0f ? 3 : 2;
            }
            result.x = tNear;
            result.y = tFar;
        }
        return type;
    }

    public static int intersectLineSegmentAab(Vector3fc p0, Vector3fc p1, Vector3fc min, Vector3fc max, Vector2f result) {
        return Intersectionf.intersectLineSegmentAab(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), result);
    }

    public static boolean testRayAab(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        float tzmax;
        float tzmin;
        float tymax;
        float tymin;
        float tFar;
        float tNear;
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        float invDirZ = 1.0f / dirZ;
        if (invDirX >= 0.0f) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return false;
        }
        if (invDirZ >= 0.0f) {
            tzmin = (minZ - originZ) * invDirZ;
            tzmax = (maxZ - originZ) * invDirZ;
        } else {
            tzmin = (maxZ - originZ) * invDirZ;
            tzmax = (minZ - originZ) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar) {
            return false;
        }
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        tFar = tzmax < tFar ? tzmax : tFar;
        return tNear < tFar && tFar >= 0.0f;
    }

    public static boolean testRayAab(Vector3fc origin, Vector3fc dir, Vector3fc min, Vector3fc max) {
        return Intersectionf.testRayAab(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public static boolean testRayTriangleFront(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon) {
        float edge1X = v1X - v0X;
        float edge2Z = v2Z - v0Z;
        float edge2Y = v2Y - v0Y;
        float pvecX = dirY * edge2Z - dirZ * edge2Y;
        float edge1Y = v1Y - v0Y;
        float edge2X = v2X - v0X;
        float pvecY = dirZ * edge2X - dirX * edge2Z;
        float edge1Z = v1Z - v0Z;
        float pvecZ = dirX * edge2Y - dirY * edge2X;
        float det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det < epsilon) {
            return false;
        }
        float tvecX = originX - v0X;
        float tvecY = originY - v0Y;
        float tvecZ = originZ - v0Z;
        float u = tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ;
        if (u < 0.0f || u > det) {
            return false;
        }
        float qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        float qvecY = tvecZ * edge1X - tvecX * edge1Z;
        float qvecZ = tvecX * edge1Y - tvecY * edge1X;
        float v = dirX * qvecX + dirY * qvecY + dirZ * qvecZ;
        if (v < 0.0f || u + v > det) {
            return false;
        }
        float t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) / det;
        return t >= epsilon;
    }

    public static boolean testRayTriangleFront(Vector3fc origin, Vector3fc dir, Vector3fc v0, Vector3fc v1, Vector3fc v2, float epsilon) {
        return Intersectionf.testRayTriangleFront(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static boolean testRayTriangle(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon) {
        float edge1X = v1X - v0X;
        float edge2Z = v2Z - v0Z;
        float edge2Y = v2Y - v0Y;
        float pvecX = dirY * edge2Z - dirZ * edge2Y;
        float edge1Y = v1Y - v0Y;
        float edge2X = v2X - v0X;
        float pvecY = dirZ * edge2X - dirX * edge2Z;
        float edge1Z = v1Z - v0Z;
        float pvecZ = dirX * edge2Y - dirY * edge2X;
        float det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det > -epsilon && det < epsilon) {
            return false;
        }
        float tvecX = originX - v0X;
        float tvecY = originY - v0Y;
        float tvecZ = originZ - v0Z;
        float invDet = 1.0f / det;
        float u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet;
        if (u < 0.0f || u > 1.0f) {
            return false;
        }
        float qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        float qvecY = tvecZ * edge1X - tvecX * edge1Z;
        float qvecZ = tvecX * edge1Y - tvecY * edge1X;
        float v = (dirX * qvecX + dirY * qvecY + dirZ * qvecZ) * invDet;
        if (v < 0.0f || u + v > 1.0f) {
            return false;
        }
        float t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet;
        return t >= epsilon;
    }

    public static boolean testRayTriangle(Vector3fc origin, Vector3fc dir, Vector3fc v0, Vector3fc v1, Vector3fc v2, float epsilon) {
        return Intersectionf.testRayTriangle(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static float intersectRayTriangleFront(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon) {
        float edge1X = v1X - v0X;
        float edge2Z = v2Z - v0Z;
        float edge2Y = v2Y - v0Y;
        float pvecX = dirY * edge2Z - dirZ * edge2Y;
        float edge1Y = v1Y - v0Y;
        float edge2X = v2X - v0X;
        float pvecY = dirZ * edge2X - dirX * edge2Z;
        float edge1Z = v1Z - v0Z;
        float pvecZ = dirX * edge2Y - dirY * edge2X;
        float det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det <= epsilon) {
            return -1.0f;
        }
        float tvecX = originX - v0X;
        float tvecY = originY - v0Y;
        float tvecZ = originZ - v0Z;
        float u = tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ;
        if (u < 0.0f || u > det) {
            return -1.0f;
        }
        float qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        float qvecY = tvecZ * edge1X - tvecX * edge1Z;
        float qvecZ = tvecX * edge1Y - tvecY * edge1X;
        float v = dirX * qvecX + dirY * qvecY + dirZ * qvecZ;
        if (v < 0.0f || u + v > det) {
            return -1.0f;
        }
        return (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) / det;
    }

    public static float intersectRayTriangleFront(Vector3fc origin, Vector3fc dir, Vector3fc v0, Vector3fc v1, Vector3fc v2, float epsilon) {
        return Intersectionf.intersectRayTriangleFront(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static float intersectRayTriangle(float originX, float originY, float originZ, float dirX, float dirY, float dirZ, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon) {
        float edge1X = v1X - v0X;
        float edge2Z = v2Z - v0Z;
        float edge2Y = v2Y - v0Y;
        float pvecX = dirY * edge2Z - dirZ * edge2Y;
        float edge1Y = v1Y - v0Y;
        float edge2X = v2X - v0X;
        float pvecY = dirZ * edge2X - dirX * edge2Z;
        float edge1Z = v1Z - v0Z;
        float pvecZ = dirX * edge2Y - dirY * edge2X;
        float det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det > -epsilon && det < epsilon) {
            return -1.0f;
        }
        float tvecX = originX - v0X;
        float tvecY = originY - v0Y;
        float tvecZ = originZ - v0Z;
        float invDet = 1.0f / det;
        float u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet;
        if (u < 0.0f || u > 1.0f) {
            return -1.0f;
        }
        float qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        float qvecY = tvecZ * edge1X - tvecX * edge1Z;
        float qvecZ = tvecX * edge1Y - tvecY * edge1X;
        float v = (dirX * qvecX + dirY * qvecY + dirZ * qvecZ) * invDet;
        if (v < 0.0f || u + v > 1.0f) {
            return -1.0f;
        }
        return (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet;
    }

    public static float intersectRayTriangle(Vector3fc origin, Vector3fc dir, Vector3fc v0, Vector3fc v1, Vector3fc v2, float epsilon) {
        return Intersectionf.intersectRayTriangle(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static boolean testLineSegmentTriangle(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon) {
        float dirX = p1X - p0X;
        float dirY = p1Y - p0Y;
        float dirZ = p1Z - p0Z;
        float t = Intersectionf.intersectRayTriangle(p0X, p0Y, p0Z, dirX, dirY, dirZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, epsilon);
        return t >= 0.0f && t <= 1.0f;
    }

    public static boolean testLineSegmentTriangle(Vector3fc p0, Vector3fc p1, Vector3fc v0, Vector3fc v1, Vector3fc v2, float epsilon) {
        return Intersectionf.testLineSegmentTriangle(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static boolean intersectLineSegmentTriangle(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon, Vector3f intersectionPoint) {
        float dirX = p1X - p0X;
        float dirY = p1Y - p0Y;
        float dirZ = p1Z - p0Z;
        float t = Intersectionf.intersectRayTriangle(p0X, p0Y, p0Z, dirX, dirY, dirZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, epsilon);
        if (t >= 0.0f && t <= 1.0f) {
            intersectionPoint.x = p0X + dirX * t;
            intersectionPoint.y = p0Y + dirY * t;
            intersectionPoint.z = p0Z + dirZ * t;
            return true;
        }
        return false;
    }

    public static boolean intersectLineSegmentTriangle(Vector3fc p0, Vector3fc p1, Vector3fc v0, Vector3fc v1, Vector3fc v2, float epsilon, Vector3f intersectionPoint) {
        return Intersectionf.intersectLineSegmentTriangle(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon, intersectionPoint);
    }

    public static boolean intersectLineSegmentPlane(float p0X, float p0Y, float p0Z, float p1X, float p1Y, float p1Z, float a, float b, float c, float d, Vector3f intersectionPoint) {
        float dirX = p1X - p0X;
        float dirY = p1Y - p0Y;
        float dirZ = p1Z - p0Z;
        float denom = a * dirX + b * dirY + c * dirZ;
        float t = -(a * p0X + b * p0Y + c * p0Z + d) / denom;
        if (t >= 0.0f && t <= 1.0f) {
            intersectionPoint.x = p0X + t * dirX;
            intersectionPoint.y = p0Y + t * dirY;
            intersectionPoint.z = p0Z + t * dirZ;
            return true;
        }
        return false;
    }

    public static boolean testLineCircle(float a, float b, float c, float centerX, float centerY, float radius) {
        float denom = Math.sqrt(a * a + b * b);
        float dist = (a * centerX + b * centerY + c) / denom;
        return -radius <= dist && dist <= radius;
    }

    public static boolean intersectLineCircle(float a, float b, float c, float centerX, float centerY, float radius, Vector3f intersectionCenterAndHL) {
        float invDenom = Math.invsqrt(a * a + b * b);
        float dist = (a * centerX + b * centerY + c) * invDenom;
        if (-radius <= dist && dist <= radius) {
            intersectionCenterAndHL.x = centerX + dist * a * invDenom;
            intersectionCenterAndHL.y = centerY + dist * b * invDenom;
            intersectionCenterAndHL.z = Math.sqrt(radius * radius - dist * dist);
            return true;
        }
        return false;
    }

    public static boolean intersectLineCircle(float x0, float y0, float x1, float y1, float centerX, float centerY, float radius, Vector3f intersectionCenterAndHL) {
        return Intersectionf.intersectLineCircle(y0 - y1, x1 - x0, (x0 - x1) * y0 + (y1 - y0) * x0, centerX, centerY, radius, intersectionCenterAndHL);
    }

    public static boolean testAarLine(float minX, float minY, float maxX, float maxY, float a, float b, float c) {
        float nY;
        float pY;
        float nX;
        float pX;
        if (a > 0.0f) {
            pX = maxX;
            nX = minX;
        } else {
            pX = minX;
            nX = maxX;
        }
        if (b > 0.0f) {
            pY = maxY;
            nY = minY;
        } else {
            pY = minY;
            nY = maxY;
        }
        float distN = c + a * nX + b * nY;
        float distP = c + a * pX + b * pY;
        return distN <= 0.0f && distP >= 0.0f;
    }

    public static boolean testAarLine(Vector2fc min, Vector2fc max, float a, float b, float c) {
        return Intersectionf.testAarLine(min.x(), min.y(), max.x(), max.y(), a, b, c);
    }

    public static boolean testAarLine(float minX, float minY, float maxX, float maxY, float x0, float y0, float x1, float y1) {
        float a = y0 - y1;
        float b = x1 - x0;
        float c = -b * y0 - a * x0;
        return Intersectionf.testAarLine(minX, minY, maxX, maxY, a, b, c);
    }

    public static boolean testAarAar(float minXA, float minYA, float maxXA, float maxYA, float minXB, float minYB, float maxXB, float maxYB) {
        return maxXA >= minXB && maxYA >= minYB && minXA <= maxXB && minYA <= maxYB;
    }

    public static boolean testAarAar(Vector2fc minA, Vector2fc maxA, Vector2fc minB, Vector2fc maxB) {
        return Intersectionf.testAarAar(minA.x(), minA.y(), maxA.x(), maxA.y(), minB.x(), minB.y(), maxB.x(), maxB.y());
    }

    public static boolean testMovingCircleCircle(float aX, float aY, float maX, float maY, float aR, float bX, float bY, float bR) {
        float aRbR2;
        float aRbR = aR + bR;
        float dist = Math.sqrt((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY)) - aRbR;
        float mLen = Math.sqrt(maX * maX + maY * maY);
        if (mLen < dist) {
            return false;
        }
        float invMLen = 1.0f / mLen;
        float nX = maX * invMLen;
        float cX = bX - aX;
        float nY = maY * invMLen;
        float cY = bY - aY;
        float nDotC = nX * cX + nY * cY;
        if (nDotC <= 0.0f) {
            return false;
        }
        float cLen = Math.sqrt(cX * cX + cY * cY);
        float cLenNdotC = cLen * cLen - nDotC * nDotC;
        if (cLenNdotC >= (aRbR2 = aRbR * aRbR)) {
            return false;
        }
        float t = aRbR2 - cLenNdotC;
        if (t < 0.0f) {
            return false;
        }
        float mag = mLen;
        float distance = nDotC - Math.sqrt(t);
        return !(mag < distance);
    }

    public static boolean testMovingCircleCircle(Vector2f centerA, Vector2f moveA, float aR, Vector2f centerB, float bR) {
        return Intersectionf.testMovingCircleCircle(centerA.x, centerA.y, moveA.x, moveA.y, aR, centerB.x, centerB.y, bR);
    }

    public static boolean intersectCircleCircle(float aX, float aY, float radiusSquaredA, float bX, float bY, float radiusSquaredB, Vector3f intersectionCenterAndHL) {
        float dX = bX - aX;
        float dY = bY - aY;
        float distSquared = dX * dX + dY * dY;
        float h = 0.5f + (radiusSquaredA - radiusSquaredB) / distSquared;
        float r_i = Math.sqrt(radiusSquaredA - h * h * distSquared);
        if (r_i >= 0.0f) {
            intersectionCenterAndHL.x = aX + h * dX;
            intersectionCenterAndHL.y = aY + h * dY;
            intersectionCenterAndHL.z = r_i;
            return true;
        }
        return false;
    }

    public static boolean intersectCircleCircle(Vector2fc centerA, float radiusSquaredA, Vector2fc centerB, float radiusSquaredB, Vector3f intersectionCenterAndHL) {
        return Intersectionf.intersectCircleCircle(centerA.x(), centerA.y(), radiusSquaredA, centerB.x(), centerB.y(), radiusSquaredB, intersectionCenterAndHL);
    }

    public static boolean testCircleCircle(float aX, float aY, float rA, float bX, float bY, float rB) {
        float d = (aX - bX) * (aX - bX) + (aY - bY) * (aY - bY);
        return d <= (rA + rB) * (rA + rB);
    }

    public static boolean testCircleCircle(Vector2fc centerA, float radiusSquaredA, Vector2fc centerB, float radiusSquaredB) {
        return Intersectionf.testCircleCircle(centerA.x(), centerA.y(), radiusSquaredA, centerB.x(), centerB.y(), radiusSquaredB);
    }

    public static float distancePointLine(float pointX, float pointY, float a, float b, float c) {
        float denom = Math.sqrt(a * a + b * b);
        return (a * pointX + b * pointY + c) / denom;
    }

    public static float distancePointLine(float pointX, float pointY, float x0, float y0, float x1, float y1) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float denom = Math.sqrt(dx * dx + dy * dy);
        return (dx * (y0 - pointY) - (x0 - pointX) * dy) / denom;
    }

    public static float distancePointLine(float pX, float pY, float pZ, float x0, float y0, float z0, float x1, float y1, float z1) {
        float d21x = x1 - x0;
        float d21y = y1 - y0;
        float d21z = z1 - z0;
        float d10x = x0 - pX;
        float d10y = y0 - pY;
        float d10z = z0 - pZ;
        float cx = d21y * d10z - d21z * d10y;
        float cy = d21z * d10x - d21x * d10z;
        float cz = d21x * d10y - d21y * d10x;
        return Math.sqrt((cx * cx + cy * cy + cz * cz) / (d21x * d21x + d21y * d21y + d21z * d21z));
    }

    public static float intersectRayLine(float originX, float originY, float dirX, float dirY, float pointX, float pointY, float normalX, float normalY, float epsilon) {
        float t;
        float denom = normalX * dirX + normalY * dirY;
        if (denom < epsilon && (t = ((pointX - originX) * normalX + (pointY - originY) * normalY) / denom) >= 0.0f) {
            return t;
        }
        return -1.0f;
    }

    public static float intersectRayLine(Vector2fc origin, Vector2fc dir, Vector2fc point, Vector2fc normal, float epsilon) {
        return Intersectionf.intersectRayLine(origin.x(), origin.y(), dir.x(), dir.y(), point.x(), point.y(), normal.x(), normal.y(), epsilon);
    }

    public static float intersectRayLineSegment(float originX, float originY, float dirX, float dirY, float aX, float aY, float bX, float bY) {
        float v1X = originX - aX;
        float v1Y = originY - aY;
        float v2X = bX - aX;
        float v2Y = bY - aY;
        float invV23 = 1.0f / (v2Y * dirX - v2X * dirY);
        float t1 = (v2X * v1Y - v2Y * v1X) * invV23;
        float t2 = (v1Y * dirX - v1X * dirY) * invV23;
        if (t1 >= 0.0f && t2 >= 0.0f && t2 <= 1.0f) {
            return t1;
        }
        return -1.0f;
    }

    public static float intersectRayLineSegment(Vector2fc origin, Vector2fc dir, Vector2fc a, Vector2fc b) {
        return Intersectionf.intersectRayLineSegment(origin.x(), origin.y(), dir.x(), dir.y(), a.x(), a.y(), b.x(), b.y());
    }

    public static boolean testAarCircle(float minX, float minY, float maxX, float maxY, float centerX, float centerY, float radiusSquared) {
        float d;
        float radius2 = radiusSquared;
        if (centerX < minX) {
            d = centerX - minX;
            radius2 -= d * d;
        } else if (centerX > maxX) {
            d = centerX - maxX;
            radius2 -= d * d;
        }
        if (centerY < minY) {
            d = centerY - minY;
            radius2 -= d * d;
        } else if (centerY > maxY) {
            d = centerY - maxY;
            radius2 -= d * d;
        }
        return radius2 >= 0.0f;
    }

    public static boolean testAarCircle(Vector2fc min, Vector2fc max, Vector2fc center, float radiusSquared) {
        return Intersectionf.testAarCircle(min.x(), min.y(), max.x(), max.y(), center.x(), center.y(), radiusSquared);
    }

    public static int findClosestPointOnTriangle(float v0X, float v0Y, float v1X, float v1Y, float v2X, float v2Y, float pX, float pY, Vector2f result) {
        float abX = v1X - v0X;
        float abY = v1Y - v0Y;
        float acX = v2X - v0X;
        float acY = v2Y - v0Y;
        float apX = pX - v0X;
        float apY = pY - v0Y;
        float d1 = abX * apX + abY * apY;
        float d2 = acX * apX + acY * apY;
        if (d1 <= 0.0f && d2 <= 0.0f) {
            result.x = v0X;
            result.y = v0Y;
            return 1;
        }
        float bpX = pX - v1X;
        float bpY = pY - v1Y;
        float d3 = abX * bpX + abY * bpY;
        float d4 = acX * bpX + acY * bpY;
        if (d3 >= 0.0f && d4 <= d3) {
            result.x = v1X;
            result.y = v1Y;
            return 2;
        }
        float vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0f && d1 >= 0.0f && d3 <= 0.0f) {
            float v = d1 / (d1 - d3);
            result.x = v0X + v * abX;
            result.y = v0Y + v * abY;
            return 4;
        }
        float cpX = pX - v2X;
        float cpY = pY - v2Y;
        float d5 = abX * cpX + abY * cpY;
        float d6 = acX * cpX + acY * cpY;
        if (d6 >= 0.0f && d5 <= d6) {
            result.x = v2X;
            result.y = v2Y;
            return 3;
        }
        float vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f) {
            float w = d2 / (d2 - d6);
            result.x = v0X + w * acX;
            result.y = v0Y + w * acY;
            return 6;
        }
        float va = d3 * d6 - d5 * d4;
        if (va <= 0.0f && d4 - d3 >= 0.0f && d5 - d6 >= 0.0f) {
            float w = (d4 - d3) / (d4 - d3 + d5 - d6);
            result.x = v1X + w * (v2X - v1X);
            result.y = v1Y + w * (v2Y - v1Y);
            return 5;
        }
        float denom = 1.0f / (va + vb + vc);
        float v = vb * denom;
        float w = vc * denom;
        result.x = v0X + abX * v + acX * w;
        result.y = v0Y + abY * v + acY * w;
        return 7;
    }

    public static int findClosestPointOnTriangle(Vector2fc v0, Vector2fc v1, Vector2fc v2, Vector2fc p, Vector2f result) {
        return Intersectionf.findClosestPointOnTriangle(v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y(), p.x(), p.y(), result);
    }

    public static boolean intersectRayCircle(float originX, float originY, float dirX, float dirY, float centerX, float centerY, float radiusSquared, Vector2f result) {
        float t1;
        float Lx = centerX - originX;
        float Ly = centerY - originY;
        float tca = Lx * dirX + Ly * dirY;
        float d2 = Lx * Lx + Ly * Ly - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        float thc = Math.sqrt(radiusSquared - d2);
        float t0 = tca - thc;
        if (t0 < (t1 = tca + thc) && t1 >= 0.0f) {
            result.x = t0;
            result.y = t1;
            return true;
        }
        return false;
    }

    public static boolean intersectRayCircle(Vector2fc origin, Vector2fc dir, Vector2fc center, float radiusSquared, Vector2f result) {
        return Intersectionf.intersectRayCircle(origin.x(), origin.y(), dir.x(), dir.y(), center.x(), center.y(), radiusSquared, result);
    }

    public static boolean testRayCircle(float originX, float originY, float dirX, float dirY, float centerX, float centerY, float radiusSquared) {
        float t1;
        float Lx = centerX - originX;
        float Ly = centerY - originY;
        float tca = Lx * dirX + Ly * dirY;
        float d2 = Lx * Lx + Ly * Ly - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        float thc = Math.sqrt(radiusSquared - d2);
        float t0 = tca - thc;
        return t0 < (t1 = tca + thc) && t1 >= 0.0f;
    }

    public static boolean testRayCircle(Vector2fc origin, Vector2fc dir, Vector2fc center, float radiusSquared) {
        return Intersectionf.testRayCircle(origin.x(), origin.y(), dir.x(), dir.y(), center.x(), center.y(), radiusSquared);
    }

    public static int intersectRayAar(float originX, float originY, float dirX, float dirY, float minX, float minY, float maxX, float maxY, Vector2f result) {
        float tymax;
        float tymin;
        float tFar;
        float tNear;
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        if (invDirX >= 0.0f) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return -1;
        }
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        int side = -1;
        if (tNear <= tFar && tFar >= 0.0f) {
            float px = originX + tNear * dirX;
            float py = originY + tNear * dirY;
            result.x = tNear;
            result.y = tFar;
            float daX = Math.abs(px - minX);
            float daY = Math.abs(py - minY);
            float dbX = Math.abs(px - maxX);
            float dbY = Math.abs(py - maxY);
            side = 0;
            float min = daX;
            if (daY < min) {
                min = daY;
                side = 1;
            }
            if (dbX < min) {
                min = dbX;
                side = 2;
            }
            if (dbY < min) {
                side = 3;
            }
        }
        return side;
    }

    public static int intersectRayAar(Vector2fc origin, Vector2fc dir, Vector2fc min, Vector2fc max, Vector2f result) {
        return Intersectionf.intersectRayAar(origin.x(), origin.y(), dir.x(), dir.y(), min.x(), min.y(), max.x(), max.y(), result);
    }

    public static int intersectLineSegmentAar(float p0X, float p0Y, float p1X, float p1Y, float minX, float minY, float maxX, float maxY, Vector2f result) {
        float tymax;
        float tymin;
        float tFar;
        float tNear;
        float dirX = p1X - p0X;
        float dirY = p1Y - p0Y;
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        if (invDirX >= 0.0f) {
            tNear = (minX - p0X) * invDirX;
            tFar = (maxX - p0X) * invDirX;
        } else {
            tNear = (maxX - p0X) * invDirX;
            tFar = (minX - p0X) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - p0Y) * invDirY;
            tymax = (maxY - p0Y) * invDirY;
        } else {
            tymin = (maxY - p0Y) * invDirY;
            tymax = (minY - p0Y) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return -1;
        }
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        int type = -1;
        if (tNear <= tFar && tNear <= 1.0f && tFar >= 0.0f) {
            if (tNear >= 0.0f && tFar > 1.0f) {
                tFar = tNear;
                type = 1;
            } else if (tNear < 0.0f && tFar <= 1.0f) {
                tNear = tFar;
                type = 1;
            } else {
                type = tNear < 0.0f && tFar > 1.0f ? 3 : 2;
            }
            result.x = tNear;
            result.y = tFar;
        }
        return type;
    }

    public static int intersectLineSegmentAar(Vector2fc p0, Vector2fc p1, Vector2fc min, Vector2fc max, Vector2f result) {
        return Intersectionf.intersectLineSegmentAar(p0.x(), p0.y(), p1.x(), p1.y(), min.x(), min.y(), max.x(), max.y(), result);
    }

    public static boolean testRayAar(float originX, float originY, float dirX, float dirY, float minX, float minY, float maxX, float maxY) {
        float tymax;
        float tymin;
        float tFar;
        float tNear;
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        if (invDirX >= 0.0f) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return false;
        }
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        return tNear < tFar && tFar >= 0.0f;
    }

    public static boolean testRayAar(Vector2fc origin, Vector2fc dir, Vector2fc min, Vector2fc max) {
        return Intersectionf.testRayAar(origin.x(), origin.y(), dir.x(), dir.y(), min.x(), min.y(), max.x(), max.y());
    }

    public static boolean testPointTriangle(float pX, float pY, float v0X, float v0Y, float v1X, float v1Y, float v2X, float v2Y) {
        boolean b2;
        boolean b1 = (pX - v1X) * (v0Y - v1Y) - (v0X - v1X) * (pY - v1Y) < 0.0f;
        boolean bl = b2 = (pX - v2X) * (v1Y - v2Y) - (v1X - v2X) * (pY - v2Y) < 0.0f;
        if (b1 != b2) {
            return false;
        }
        boolean b3 = (pX - v0X) * (v2Y - v0Y) - (v2X - v0X) * (pY - v0Y) < 0.0f;
        return b2 == b3;
    }

    public static boolean testPointTriangle(Vector2fc point, Vector2fc v0, Vector2fc v1, Vector2fc v2) {
        return Intersectionf.testPointTriangle(point.x(), point.y(), v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y());
    }

    public static boolean testPointAar(float pX, float pY, float minX, float minY, float maxX, float maxY) {
        return pX >= minX && pY >= minY && pX <= maxX && pY <= maxY;
    }

    public static boolean testPointCircle(float pX, float pY, float centerX, float centerY, float radiusSquared) {
        float dx = pX - centerX;
        float dx2 = dx * dx;
        float dy = pY - centerY;
        float dy2 = dy * dy;
        return dx2 + dy2 <= radiusSquared;
    }

    public static boolean testCircleTriangle(float centerX, float centerY, float radiusSquared, float v0X, float v0Y, float v1X, float v1Y, float v2X, float v2Y) {
        float len;
        float c1x = centerX - v0X;
        float c1y = centerY - v0Y;
        float c1sqr = c1x * c1x + c1y * c1y - radiusSquared;
        if (c1sqr <= 0.0f) {
            return true;
        }
        float c2x = centerX - v1X;
        float c2y = centerY - v1Y;
        float c2sqr = c2x * c2x + c2y * c2y - radiusSquared;
        if (c2sqr <= 0.0f) {
            return true;
        }
        float c3x = centerX - v2X;
        float c3y = centerY - v2Y;
        float c3sqr = c3x * c3x + c3y * c3y - radiusSquared;
        if (c3sqr <= 0.0f) {
            return true;
        }
        float e1x = v1X - v0X;
        float e1y = v1Y - v0Y;
        float e2x = v2X - v1X;
        float e2y = v2Y - v1Y;
        float e3x = v0X - v2X;
        float e3y = v0Y - v2Y;
        if (e1x * c1y - e1y * c1x >= 0.0f && e2x * c2y - e2y * c2x >= 0.0f && e3x * c3y - e3y * c3x >= 0.0f) {
            return true;
        }
        float k = c1x * e1x + c1y * e1y;
        if (k >= 0.0f && k <= (len = e1x * e1x + e1y * e1y) && c1sqr * len <= k * k) {
            return true;
        }
        k = c2x * e2x + c2y * e2y;
        if (k > 0.0f && k <= (len = e2x * e2x + e2y * e2y) && c2sqr * len <= k * k) {
            return true;
        }
        k = c3x * e3x + c3y * e3y;
        return k >= 0.0f && k < (len = e3x * e3x + e3y * e3y) && c3sqr * len <= k * k;
    }

    public static boolean testCircleTriangle(Vector2fc center, float radiusSquared, Vector2fc v0, Vector2fc v1, Vector2fc v2) {
        return Intersectionf.testCircleTriangle(center.x(), center.y(), radiusSquared, v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y());
    }

    public static int intersectPolygonRay(float[] verticesXY, float originX, float originY, float dirX, float dirY, Vector2f p) {
        float nearestT = Float.POSITIVE_INFINITY;
        int count = verticesXY.length >> 1;
        int edgeIndex = -1;
        float aX = verticesXY[count - 1 << 1];
        float aY = verticesXY[(count - 1 << 1) + 1];
        for (int i = 0; i < count; ++i) {
            float t2;
            float bX = verticesXY[i << 1];
            float dbaX = bX - aX;
            float doaY = originY - aY;
            float bY = verticesXY[(i << 1) + 1];
            float dbaY = bY - aY;
            float doaX = originX - aX;
            float invDbaDir = 1.0f / (dbaY * dirX - dbaX * dirY);
            float t = (dbaX * doaY - dbaY * doaX) * invDbaDir;
            if (t >= 0.0f && t < nearestT && (t2 = (doaY * dirX - doaX * dirY) * invDbaDir) >= 0.0f && t2 <= 1.0f) {
                edgeIndex = (i - 1 + count) % count;
                nearestT = t;
                p.x = originX + t * dirX;
                p.y = originY + t * dirY;
            }
            aX = bX;
            aY = bY;
        }
        return edgeIndex;
    }

    public static int intersectPolygonRay(Vector2fc[] vertices, float originX, float originY, float dirX, float dirY, Vector2f p) {
        float nearestT = Float.POSITIVE_INFINITY;
        int count = vertices.length;
        int edgeIndex = -1;
        float aX = vertices[count - 1].x();
        float aY = vertices[count - 1].y();
        for (int i = 0; i < count; ++i) {
            float t2;
            float invDbaDir;
            float doaX;
            float bY;
            float dbaY;
            float doaY;
            Vector2fc b = vertices[i];
            float bX = b.x();
            float dbaX = bX - aX;
            float t = (dbaX * (doaY = originY - aY) - (dbaY = (bY = b.y()) - aY) * (doaX = originX - aX)) * (invDbaDir = 1.0f / (dbaY * dirX - dbaX * dirY));
            if (t >= 0.0f && t < nearestT && (t2 = (doaY * dirX - doaX * dirY) * invDbaDir) >= 0.0f && t2 <= 1.0f) {
                edgeIndex = (i - 1 + count) % count;
                nearestT = t;
                p.x = originX + t * dirX;
                p.y = originY + t * dirY;
            }
            aX = bX;
            aY = bY;
        }
        return edgeIndex;
    }

    public static boolean intersectLineLine(float ps1x, float ps1y, float pe1x, float pe1y, float ps2x, float ps2y, float pe2x, float pe2y, Vector2f p) {
        float d1x = ps1x - pe1x;
        float d1y = pe1y - ps1y;
        float d1ps1 = d1y * ps1x + d1x * ps1y;
        float d2x = ps2x - pe2x;
        float d2y = pe2y - ps2y;
        float d2ps2 = d2y * ps2x + d2x * ps2y;
        float det = d1y * d2x - d2y * d1x;
        if (det == 0.0f) {
            return false;
        }
        p.x = (d2x * d1ps1 - d1x * d2ps2) / det;
        p.y = (d1y * d2ps2 - d2y * d1ps1) / det;
        return true;
    }

    private static boolean separatingAxis(Vector2f[] v1s, Vector2f[] v2s, float aX, float aY) {
        float minA = Float.POSITIVE_INFINITY;
        float maxA = Float.NEGATIVE_INFINITY;
        float minB = Float.POSITIVE_INFINITY;
        float maxB = Float.NEGATIVE_INFINITY;
        int maxLen = Math.max(v1s.length, v2s.length);
        for (int k = 0; k < maxLen; ++k) {
            float d;
            if (k < v1s.length) {
                Vector2f v1 = v1s[k];
                d = v1.x * aX + v1.y * aY;
                if (d < minA) {
                    minA = d;
                }
                if (d > maxA) {
                    maxA = d;
                }
            }
            if (k < v2s.length) {
                Vector2f v2 = v2s[k];
                d = v2.x * aX + v2.y * aY;
                if (d < minB) {
                    minB = d;
                }
                if (d > maxB) {
                    maxB = d;
                }
            }
            if (!(minA <= maxB) || !(minB <= maxA)) continue;
            return false;
        }
        return true;
    }

    public static boolean testPolygonPolygon(Vector2f[] v1s, Vector2f[] v2s) {
        Vector2f t;
        Vector2f s;
        int i = 0;
        int j = v1s.length - 1;
        while (i < v1s.length) {
            s = v1s[i];
            t = v1s[j];
            if (Intersectionf.separatingAxis(v1s, v2s, s.y - t.y, t.x - s.x)) {
                return false;
            }
            j = i++;
        }
        i = 0;
        j = v2s.length - 1;
        while (i < v2s.length) {
            s = v2s[i];
            t = v2s[j];
            if (Intersectionf.separatingAxis(v1s, v2s, s.y - t.y, t.x - s.x)) {
                return false;
            }
            j = i++;
        }
        return true;
    }
}

