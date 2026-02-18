/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Math;
import org.joml.Runtime;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4d;

public class Intersectiond {
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

    public static boolean testPlaneSphere(double a, double b, double c, double d, double centerX, double centerY, double centerZ, double radius) {
        double denom = Math.sqrt(a * a + b * b + c * c);
        double dist = (a * centerX + b * centerY + c * centerZ + d) / denom;
        return -radius <= dist && dist <= radius;
    }

    public static boolean intersectPlaneSphere(double a, double b, double c, double d, double centerX, double centerY, double centerZ, double radius, Vector4d intersectionCenterAndRadius) {
        double invDenom = Math.invsqrt(a * a + b * b + c * c);
        double dist = (a * centerX + b * centerY + c * centerZ + d) * invDenom;
        if (-radius <= dist && dist <= radius) {
            intersectionCenterAndRadius.x = centerX + dist * a * invDenom;
            intersectionCenterAndRadius.y = centerY + dist * b * invDenom;
            intersectionCenterAndRadius.z = centerZ + dist * c * invDenom;
            intersectionCenterAndRadius.w = Math.sqrt(radius * radius - dist * dist);
            return true;
        }
        return false;
    }

    public static boolean intersectPlaneSweptSphere(double a, double b, double c, double d, double cX, double cY, double cZ, double radius, double vX, double vY, double vZ, Vector4d pointAndTime) {
        double dist = a * cX + b * cY + c * cZ - d;
        if (Math.abs(dist) <= radius) {
            pointAndTime.set(cX, cY, cZ, 0.0);
            return true;
        }
        double denom = a * vX + b * vY + c * vZ;
        if (denom * dist >= 0.0) {
            return false;
        }
        double r = dist > 0.0 ? radius : -radius;
        double t = (r - dist) / denom;
        pointAndTime.set(cX + t * vX - r * a, cY + t * vY - r * b, cZ + t * vZ - r * c, t);
        return true;
    }

    public static boolean testPlaneSweptSphere(double a, double b, double c, double d, double t0X, double t0Y, double t0Z, double r, double t1X, double t1Y, double t1Z) {
        double adist = t0X * a + t0Y * b + t0Z * c - d;
        double bdist = t1X * a + t1Y * b + t1Z * c - d;
        if (adist * bdist < 0.0) {
            return true;
        }
        return Math.abs(adist) <= r || Math.abs(bdist) <= r;
    }

    public static boolean testAabPlane(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double a, double b, double c, double d) {
        double nZ;
        double pZ;
        double nY;
        double pY;
        double nX;
        double pX;
        if (a > 0.0) {
            pX = maxX;
            nX = minX;
        } else {
            pX = minX;
            nX = maxX;
        }
        if (b > 0.0) {
            pY = maxY;
            nY = minY;
        } else {
            pY = minY;
            nY = maxY;
        }
        if (c > 0.0) {
            pZ = maxZ;
            nZ = minZ;
        } else {
            pZ = minZ;
            nZ = maxZ;
        }
        double distN = d + a * nX + b * nY + c * nZ;
        double distP = d + a * pX + b * pY + c * pZ;
        return distN <= 0.0 && distP >= 0.0;
    }

    public static boolean testAabPlane(Vector3dc min, Vector3dc max, double a, double b, double c, double d) {
        return Intersectiond.testAabPlane(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), a, b, c, d);
    }

    public static boolean testAabAab(double minXA, double minYA, double minZA, double maxXA, double maxYA, double maxZA, double minXB, double minYB, double minZB, double maxXB, double maxYB, double maxZB) {
        return maxXA >= minXB && maxYA >= minYB && maxZA >= minZB && minXA <= maxXB && minYA <= maxYB && minZA <= maxZB;
    }

    public static boolean testAabAab(Vector3dc minA, Vector3dc maxA, Vector3dc minB, Vector3dc maxB) {
        return Intersectiond.testAabAab(minA.x(), minA.y(), minA.z(), maxA.x(), maxA.y(), maxA.z(), minB.x(), minB.y(), minB.z(), maxB.x(), maxB.y(), maxB.z());
    }

    public static boolean testObOb(Vector3d b0c, Vector3d b0uX, Vector3d b0uY, Vector3d b0uZ, Vector3d b0hs, Vector3d b1c, Vector3d b1uX, Vector3d b1uY, Vector3d b1uZ, Vector3d b1hs) {
        return Intersectiond.testObOb(b0c.x, b0c.y, b0c.z, b0uX.x, b0uX.y, b0uX.z, b0uY.x, b0uY.y, b0uY.z, b0uZ.x, b0uZ.y, b0uZ.z, b0hs.x, b0hs.y, b0hs.z, b1c.x, b1c.y, b1c.z, b1uX.x, b1uX.y, b1uX.z, b1uY.x, b1uY.y, b1uY.z, b1uZ.x, b1uZ.y, b1uZ.z, b1hs.x, b1hs.y, b1hs.z);
    }

    public static boolean testObOb(double b0cX, double b0cY, double b0cZ, double b0uXx, double b0uXy, double b0uXz, double b0uYx, double b0uYy, double b0uYz, double b0uZx, double b0uZy, double b0uZz, double b0hsX, double b0hsY, double b0hsZ, double b1cX, double b1cY, double b1cZ, double b1uXx, double b1uXy, double b1uXz, double b1uYx, double b1uYy, double b1uYz, double b1uZx, double b1uZy, double b1uZz, double b1hsX, double b1hsY, double b1hsZ) {
        double rm00 = b0uXx * b1uXx + b0uYx * b1uYx + b0uZx * b1uZx;
        double rm10 = b0uXx * b1uXy + b0uYx * b1uYy + b0uZx * b1uZy;
        double rm20 = b0uXx * b1uXz + b0uYx * b1uYz + b0uZx * b1uZz;
        double rm01 = b0uXy * b1uXx + b0uYy * b1uYx + b0uZy * b1uZx;
        double rm11 = b0uXy * b1uXy + b0uYy * b1uYy + b0uZy * b1uZy;
        double rm21 = b0uXy * b1uXz + b0uYy * b1uYz + b0uZy * b1uZz;
        double rm02 = b0uXz * b1uXx + b0uYz * b1uYx + b0uZz * b1uZx;
        double rm12 = b0uXz * b1uXy + b0uYz * b1uYy + b0uZz * b1uZy;
        double rm22 = b0uXz * b1uXz + b0uYz * b1uYz + b0uZz * b1uZz;
        double EPSILON = 1.0E-8;
        double arm00 = Math.abs(rm00) + EPSILON;
        double arm01 = Math.abs(rm01) + EPSILON;
        double arm02 = Math.abs(rm02) + EPSILON;
        double arm10 = Math.abs(rm10) + EPSILON;
        double arm11 = Math.abs(rm11) + EPSILON;
        double arm12 = Math.abs(rm12) + EPSILON;
        double arm20 = Math.abs(rm20) + EPSILON;
        double arm21 = Math.abs(rm21) + EPSILON;
        double arm22 = Math.abs(rm22) + EPSILON;
        double tx = b1cX - b0cX;
        double ty = b1cY - b0cY;
        double tz = b1cZ - b0cZ;
        double tax = tx * b0uXx + ty * b0uXy + tz * b0uXz;
        double tay = tx * b0uYx + ty * b0uYy + tz * b0uYz;
        double taz = tx * b0uZx + ty * b0uZy + tz * b0uZz;
        double ra = b0hsX;
        double rb = b1hsX * arm00 + b1hsY * arm01 + b1hsZ * arm02;
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

    public static boolean intersectSphereSphere(double aX, double aY, double aZ, double radiusSquaredA, double bX, double bY, double bZ, double radiusSquaredB, Vector4d centerAndRadiusOfIntersectionCircle) {
        double dX = bX - aX;
        double dY = bY - aY;
        double dZ = bZ - aZ;
        double distSquared = dX * dX + dY * dY + dZ * dZ;
        double h = 0.5 + (radiusSquaredA - radiusSquaredB) / (2.0 * distSquared);
        double r_i = radiusSquaredA - h * h * distSquared;
        if (r_i >= 0.0) {
            centerAndRadiusOfIntersectionCircle.x = aX + h * dX;
            centerAndRadiusOfIntersectionCircle.y = aY + h * dY;
            centerAndRadiusOfIntersectionCircle.z = aZ + h * dZ;
            centerAndRadiusOfIntersectionCircle.w = Math.sqrt(r_i);
            return true;
        }
        return false;
    }

    public static boolean intersectSphereSphere(Vector3dc centerA, double radiusSquaredA, Vector3dc centerB, double radiusSquaredB, Vector4d centerAndRadiusOfIntersectionCircle) {
        return Intersectiond.intersectSphereSphere(centerA.x(), centerA.y(), centerA.z(), radiusSquaredA, centerB.x(), centerB.y(), centerB.z(), radiusSquaredB, centerAndRadiusOfIntersectionCircle);
    }

    public static int intersectSphereTriangle(double sX, double sY, double sZ, double sR, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, Vector3d result) {
        int closest = Intersectiond.findClosestPointOnTriangle(v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, sX, sY, sZ, result);
        double vX = result.x - sX;
        double vY = result.y - sY;
        double vZ = result.z - sZ;
        double dot = vX * vX + vY * vY + vZ * vZ;
        if (dot <= sR * sR) {
            return closest;
        }
        return 0;
    }

    public static boolean testSphereSphere(double aX, double aY, double aZ, double radiusSquaredA, double bX, double bY, double bZ, double radiusSquaredB) {
        double dX = bX - aX;
        double dY = bY - aY;
        double dZ = bZ - aZ;
        double distSquared = dX * dX + dY * dY + dZ * dZ;
        double h = 0.5 + (radiusSquaredA - radiusSquaredB) / (2.0 * distSquared);
        double r_i = radiusSquaredA - h * h * distSquared;
        return r_i >= 0.0;
    }

    public static boolean testSphereSphere(Vector3dc centerA, double radiusSquaredA, Vector3dc centerB, double radiusSquaredB) {
        return Intersectiond.testSphereSphere(centerA.x(), centerA.y(), centerA.z(), radiusSquaredA, centerB.x(), centerB.y(), centerB.z(), radiusSquaredB);
    }

    public static double distancePointPlane(double pointX, double pointY, double pointZ, double a, double b, double c, double d) {
        double denom = Math.sqrt(a * a + b * b + c * c);
        return (a * pointX + b * pointY + c * pointZ + d) / denom;
    }

    public static double distancePointPlane(double pointX, double pointY, double pointZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z) {
        double v1Y0Y = v1Y - v0Y;
        double v2Z0Z = v2Z - v0Z;
        double v2Y0Y = v2Y - v0Y;
        double v1Z0Z = v1Z - v0Z;
        double v2X0X = v2X - v0X;
        double v1X0X = v1X - v0X;
        double a = v1Y0Y * v2Z0Z - v2Y0Y * v1Z0Z;
        double b = v1Z0Z * v2X0X - v2Z0Z * v1X0X;
        double c = v1X0X * v2Y0Y - v2X0X * v1Y0Y;
        double d = -(a * v0X + b * v0Y + c * v0Z);
        return Intersectiond.distancePointPlane(pointX, pointY, pointZ, a, b, c, d);
    }

    public static double intersectRayPlane(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double pointX, double pointY, double pointZ, double normalX, double normalY, double normalZ, double epsilon) {
        double t;
        double denom = normalX * dirX + normalY * dirY + normalZ * dirZ;
        if (denom < epsilon && (t = ((pointX - originX) * normalX + (pointY - originY) * normalY + (pointZ - originZ) * normalZ) / denom) >= 0.0) {
            return t;
        }
        return -1.0;
    }

    public static double intersectRayPlane(Vector3dc origin, Vector3dc dir, Vector3dc point, Vector3dc normal, double epsilon) {
        return Intersectiond.intersectRayPlane(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), point.x(), point.y(), point.z(), normal.x(), normal.y(), normal.z(), epsilon);
    }

    public static double intersectRayPlane(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double a, double b, double c, double d, double epsilon) {
        double t;
        double denom = a * dirX + b * dirY + c * dirZ;
        if (denom < epsilon && (t = -(a * originX + b * originY + c * originZ + d) / denom) >= 0.0) {
            return t;
        }
        return -1.0;
    }

    public static boolean testAabSphere(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double centerX, double centerY, double centerZ, double radiusSquared) {
        double d;
        double radius2 = radiusSquared;
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
        return radius2 >= 0.0;
    }

    public static boolean testAabSphere(Vector3dc min, Vector3dc max, Vector3dc center, double radiusSquared) {
        return Intersectiond.testAabSphere(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), center.x(), center.y(), center.z(), radiusSquared);
    }

    public static Vector3d findClosestPointOnPlane(double aX, double aY, double aZ, double nX, double nY, double nZ, double pX, double pY, double pZ, Vector3d result) {
        double d = -(nX * aX + nY * aY + nZ * aZ);
        double t = nX * pX + nY * pY + nZ * pZ - d;
        result.x = pX - t * nX;
        result.y = pY - t * nY;
        result.z = pZ - t * nZ;
        return result;
    }

    public static Vector3d findClosestPointOnLineSegment(double aX, double aY, double aZ, double bX, double bY, double bZ, double pX, double pY, double pZ, Vector3d result) {
        double abX = bX - aX;
        double abY = bY - aY;
        double abZ = bZ - aZ;
        double t = ((pX - aX) * abX + (pY - aY) * abY + (pZ - aZ) * abZ) / (abX * abX + abY * abY + abZ * abZ);
        if (t < 0.0) {
            t = 0.0;
        }
        if (t > 1.0) {
            t = 1.0;
        }
        result.x = aX + t * abX;
        result.y = aY + t * abY;
        result.z = aZ + t * abZ;
        return result;
    }

    public static double findClosestPointsLineSegments(double a0X, double a0Y, double a0Z, double a1X, double a1Y, double a1Z, double b0X, double b0Y, double b0Z, double b1X, double b1Y, double b1Z, Vector3d resultA, Vector3d resultB) {
        double t;
        double s;
        double d1x = a1X - a0X;
        double d1y = a1Y - a0Y;
        double d1z = a1Z - a0Z;
        double d2x = b1X - b0X;
        double d2y = b1Y - b0Y;
        double d2z = b1Z - b0Z;
        double rX = a0X - b0X;
        double rY = a0Y - b0Y;
        double rZ = a0Z - b0Z;
        double a = d1x * d1x + d1y * d1y + d1z * d1z;
        double invA = 1.0 / a;
        double e = d2x * d2x + d2y * d2y + d2z * d2z;
        double f = d2x * rX + d2y * rY + d2z * rZ;
        double EPSILON = 1.0E-8;
        if (a <= EPSILON && e <= EPSILON) {
            resultA.set(a0X, a0Y, a0Z);
            resultB.set(b0X, b0Y, b0Z);
            return resultA.dot(resultB);
        }
        if (a <= EPSILON) {
            s = 0.0;
            t = f / e;
            t = Math.min(Math.max(t, 0.0), 1.0);
        } else {
            double c = d1x * rX + d1y * rY + d1z * rZ;
            if (e <= EPSILON) {
                t = 0.0;
                s = Math.min(Math.max(-c * invA, 0.0), 1.0);
            } else {
                double b;
                double denom = a * e - (b = d1x * d2x + d1y * d2y + d1z * d2z) * b;
                s = denom != 0.0 ? Math.min(Math.max((b * f - c * e) / denom, 0.0), 1.0) : 0.0;
                t = (b * s + f) / e;
                if (t < 0.0) {
                    t = 0.0;
                    s = Math.min(Math.max(-c * invA, 0.0), 1.0);
                } else if (t > 1.0) {
                    t = 1.0;
                    s = Math.min(Math.max((b - c) * invA, 0.0), 1.0);
                }
            }
        }
        resultA.set(a0X + d1x * s, a0Y + d1y * s, a0Z + d1z * s);
        resultB.set(b0X + d2x * t, b0Y + d2y * t, b0Z + d2z * t);
        double dX = resultA.x - resultB.x;
        double dY = resultA.y - resultB.y;
        double dZ = resultA.z - resultB.z;
        return dX * dX + dY * dY + dZ * dZ;
    }

    public static double findClosestPointsLineSegmentTriangle(double aX, double aY, double aZ, double bX, double bY, double bZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, Vector3d lineSegmentResult, Vector3d triangleResult) {
        double invLen;
        double v1X0X;
        double v2X0X;
        double v1Z0Z;
        double v2Y0Y;
        double v2Z0Z;
        double v1Y0Y;
        double d;
        double min = d = Intersectiond.findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, lineSegmentResult, triangleResult);
        double minlsX = lineSegmentResult.x;
        double minlsY = lineSegmentResult.y;
        double minlsZ = lineSegmentResult.z;
        double mintX = triangleResult.x;
        double mintY = triangleResult.y;
        double mintZ = triangleResult.z;
        d = Intersectiond.findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v1X, v1Y, v1Z, v2X, v2Y, v2Z, lineSegmentResult, triangleResult);
        if (d < min) {
            min = d;
            minlsX = lineSegmentResult.x;
            minlsY = lineSegmentResult.y;
            minlsZ = lineSegmentResult.z;
            mintX = triangleResult.x;
            mintY = triangleResult.y;
            mintZ = triangleResult.z;
        }
        if ((d = Intersectiond.findClosestPointsLineSegments(aX, aY, aZ, bX, bY, bZ, v2X, v2Y, v2Z, v0X, v0Y, v0Z, lineSegmentResult, triangleResult)) < min) {
            min = d;
            minlsX = lineSegmentResult.x;
            minlsY = lineSegmentResult.y;
            minlsZ = lineSegmentResult.z;
            mintX = triangleResult.x;
            mintY = triangleResult.y;
            mintZ = triangleResult.z;
        }
        boolean computed = false;
        double a = Double.NaN;
        double b = Double.NaN;
        double c = Double.NaN;
        double nd = Double.NaN;
        if (Intersectiond.testPointInTriangle(aX, aY, aZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)) {
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
            double l = d = a * aX + b * aY + c * aZ + nd;
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
        if (Intersectiond.testPointInTriangle(bX, bY, bZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z)) {
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
            double l = d = a * bX + b * bY + c * bZ + nd;
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

    public static int findClosestPointOnTriangle(double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double pX, double pY, double pZ, Vector3d result) {
        double abX = v1X - v0X;
        double abY = v1Y - v0Y;
        double abZ = v1Z - v0Z;
        double acX = v2X - v0X;
        double acY = v2Y - v0Y;
        double acZ = v2Z - v0Z;
        double apX = pX - v0X;
        double apY = pY - v0Y;
        double apZ = pZ - v0Z;
        double d1 = abX * apX + abY * apY + abZ * apZ;
        double d2 = acX * apX + acY * apY + acZ * apZ;
        if (d1 <= 0.0 && d2 <= 0.0) {
            result.x = v0X;
            result.y = v0Y;
            result.z = v0Z;
            return 1;
        }
        double bpX = pX - v1X;
        double bpY = pY - v1Y;
        double bpZ = pZ - v1Z;
        double d3 = abX * bpX + abY * bpY + abZ * bpZ;
        double d4 = acX * bpX + acY * bpY + acZ * bpZ;
        if (d3 >= 0.0 && d4 <= d3) {
            result.x = v1X;
            result.y = v1Y;
            result.z = v1Z;
            return 2;
        }
        double vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0 && d1 >= 0.0 && d3 <= 0.0) {
            double v = d1 / (d1 - d3);
            result.x = v0X + v * abX;
            result.y = v0Y + v * abY;
            result.z = v0Z + v * abZ;
            return 4;
        }
        double cpX = pX - v2X;
        double cpY = pY - v2Y;
        double cpZ = pZ - v2Z;
        double d5 = abX * cpX + abY * cpY + abZ * cpZ;
        double d6 = acX * cpX + acY * cpY + acZ * cpZ;
        if (d6 >= 0.0 && d5 <= d6) {
            result.x = v2X;
            result.y = v2Y;
            result.z = v2Z;
            return 3;
        }
        double vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0 && d2 >= 0.0 && d6 <= 0.0) {
            double w = d2 / (d2 - d6);
            result.x = v0X + w * acX;
            result.y = v0Y + w * acY;
            result.z = v0Z + w * acZ;
            return 6;
        }
        double va = d3 * d6 - d5 * d4;
        if (va <= 0.0 && d4 - d3 >= 0.0 && d5 - d6 >= 0.0) {
            double w = (d4 - d3) / (d4 - d3 + d5 - d6);
            result.x = v1X + w * (v2X - v1X);
            result.y = v1Y + w * (v2Y - v1Y);
            result.z = v1Z + w * (v2Z - v1Z);
            return 5;
        }
        double denom = 1.0 / (va + vb + vc);
        double v = vb * denom;
        double w = vc * denom;
        result.x = v0X + abX * v + acX * w;
        result.y = v0Y + abY * v + acY * w;
        result.z = v0Z + abZ * v + acZ * w;
        return 7;
    }

    public static int findClosestPointOnTriangle(Vector3dc v0, Vector3dc v1, Vector3dc v2, Vector3dc p, Vector3d result) {
        return Intersectiond.findClosestPointOnTriangle(v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), p.x(), p.y(), p.z(), result);
    }

    public static Vector3d findClosestPointOnRectangle(double aX, double aY, double aZ, double bX, double bY, double bZ, double cX, double cY, double cZ, double pX, double pY, double pZ, Vector3d res) {
        double distTimesInvMaxDist;
        double abX = bX - aX;
        double abY = bY - aY;
        double abZ = bZ - aZ;
        double acX = cX - aX;
        double acY = cY - aY;
        double acZ = cZ - aZ;
        double dX = pX - aX;
        double dY = pY - aY;
        double dZ = pZ - aZ;
        double qX = aX;
        double qY = aY;
        double qZ = aZ;
        double dist = dX * abX + dY * abY + dZ * abZ;
        double maxdist = abX * abX + abY * abY + abZ * abZ;
        double invMaxdist = 1.0 / maxdist;
        if (dist >= maxdist) {
            qX += abX;
            qY += abY;
            qZ += abZ;
        } else if (dist > 0.0) {
            distTimesInvMaxDist = dist * invMaxdist;
            qX += distTimesInvMaxDist * abX;
            qY += distTimesInvMaxDist * abY;
            qZ += distTimesInvMaxDist * abZ;
        }
        dist = dX * acX + dY * acY + dZ * acZ;
        maxdist = acX * acX + acY * acY + acZ * acZ;
        invMaxdist = 1.0 / maxdist;
        if (dist >= maxdist) {
            qX += acX;
            qY += acY;
            qZ += acZ;
        } else if (dist > 0.0) {
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

    public static int intersectSweptSphereTriangle(double centerX, double centerY, double centerZ, double radius, double velX, double velY, double velZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon, double maxT, Vector4d pointAndTime) {
        double baseTo1Len;
        double C21;
        double v21BaseTo1;
        double velBaseTo1;
        double B21;
        double len21;
        double A21;
        double root21;
        double v21Z;
        double v21Y;
        double v21X;
        double v21Vel;
        double f21;
        double C20;
        double v20BaseTo0;
        double B20;
        double len20;
        double A20;
        double root20;
        double v20Vel;
        double f20;
        double C10;
        double v10BaseTo0;
        double velBaseTo0;
        double B10;
        double velLen;
        double len10;
        double A10;
        double root10;
        double v10Vel;
        double f10;
        double C2;
        double centerV2Z;
        double centerV2Y;
        double centerV2X;
        double B2;
        double root2;
        double centerV1Len;
        double C1;
        double centerV1Z;
        double centerV1Y;
        double centerV1X;
        double B1;
        double root1;
        double v10X = v1X - v0X;
        double v10Y = v1Y - v0Y;
        double v10Z = v1Z - v0Z;
        double v20X = v2X - v0X;
        double v20Y = v2Y - v0Y;
        double v20Z = v2Z - v0Z;
        double a = v10Y * v20Z - v20Y * v10Z;
        double b = v10Z * v20X - v20Z * v10X;
        double c = v10X * v20Y - v20X * v10Y;
        double d = -(a * v0X + b * v0Y + c * v0Z);
        double invLen = Math.invsqrt(a * a + b * b + c * c);
        double signedDist = (a * centerX + b * centerY + c * centerZ + d) * invLen;
        double dot = (a * velX + b * velY + c * velZ) * invLen;
        double invDot = 1.0 / dot;
        if (dot < epsilon && dot > -epsilon) {
            return 0;
        }
        double pt0 = (radius - signedDist) * invDot;
        if (pt0 > maxT) {
            return 0;
        }
        double pt1 = (-radius - signedDist) * invDot;
        double p0X = centerX - radius * a * invLen + velX * pt0;
        double p0Y = centerY - radius * b * invLen + velY * pt0;
        double p0Z = centerZ - radius * c * invLen + velZ * pt0;
        boolean insideTriangle = Intersectiond.testPointInTriangle(p0X, p0Y, p0Z, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z);
        if (insideTriangle) {
            pointAndTime.x = p0X;
            pointAndTime.y = p0Y;
            pointAndTime.z = p0Z;
            pointAndTime.w = pt0;
            return 7;
        }
        int isect = 0;
        double A = velX * velX + velY * velY + velZ * velZ;
        double centerV0X = centerX - v0X;
        double centerV0Y = centerY - v0Y;
        double centerV0Z = centerZ - v0Z;
        double B0 = 2.0 * (velX * centerV0X + velY * centerV0Y + velZ * centerV0Z);
        double baseTo0Len = centerV0X * centerV0X + centerV0Y * centerV0Y + centerV0Z * centerV0Z;
        double radius2 = radius * radius;
        double C0 = baseTo0Len - radius2;
        double t0 = maxT;
        double root0 = Intersectiond.computeLowestRoot(A, B0, C0, t0);
        if (root0 < t0) {
            pointAndTime.x = v0X;
            pointAndTime.y = v0Y;
            pointAndTime.z = v0Z;
            pointAndTime.w = root0;
            t0 = root0;
            isect = 1;
        }
        if ((root1 = Intersectiond.computeLowestRoot(A, B1 = 2.0 * (velX * (centerV1X = centerX - v1X) + velY * (centerV1Y = centerY - v1Y) + velZ * (centerV1Z = centerZ - v1Z)), C1 = (centerV1Len = centerV1X * centerV1X + centerV1Y * centerV1Y + centerV1Z * centerV1Z) - radius2, t0)) < t0) {
            pointAndTime.x = v1X;
            pointAndTime.y = v1Y;
            pointAndTime.z = v1Z;
            pointAndTime.w = root1;
            t0 = root1;
            isect = 2;
        }
        if ((root2 = Intersectiond.computeLowestRoot(A, B2 = 2.0 * (velX * (centerV2X = centerX - v2X) + velY * (centerV2Y = centerY - v2Y) + velZ * (centerV2Z = centerZ - v2Z)), C2 = centerV2X * centerV2X + centerV2Y * centerV2Y + centerV2Z * centerV2Z - radius2, t0)) < t0) {
            pointAndTime.x = v2X;
            pointAndTime.y = v2Y;
            pointAndTime.z = v2Z;
            pointAndTime.w = root2;
            t0 = root2;
            isect = 3;
        }
        if ((f10 = ((v10Vel = v10X * velX + v10Y * velY + v10Z * velZ) * (root10 = Intersectiond.computeLowestRoot(A10 = (len10 = v10X * v10X + v10Y * v10Y + v10Z * v10Z) * -(velLen = A) + v10Vel * v10Vel, B10 = len10 * 2.0 * (velBaseTo0 = velX * -centerV0X + velY * -centerV0Y + velZ * -centerV0Z) - 2.0 * v10Vel * (v10BaseTo0 = v10X * -centerV0X + v10Y * -centerV0Y + v10Z * -centerV0Z), C10 = len10 * (radius2 - baseTo0Len) + v10BaseTo0 * v10BaseTo0, t0)) - v10BaseTo0) / len10) >= 0.0 && f10 <= 1.0 && root10 < t0) {
            pointAndTime.x = v0X + f10 * v10X;
            pointAndTime.y = v0Y + f10 * v10Y;
            pointAndTime.z = v0Z + f10 * v10Z;
            pointAndTime.w = root10;
            t0 = root10;
            isect = 4;
        }
        if ((f20 = ((v20Vel = v20X * velX + v20Y * velY + v20Z * velZ) * (root20 = Intersectiond.computeLowestRoot(A20 = (len20 = v20X * v20X + v20Y * v20Y + v20Z * v20Z) * -velLen + v20Vel * v20Vel, B20 = len20 * 2.0 * velBaseTo0 - 2.0 * v20Vel * (v20BaseTo0 = v20X * -centerV0X + v20Y * -centerV0Y + v20Z * -centerV0Z), C20 = len20 * (radius2 - baseTo0Len) + v20BaseTo0 * v20BaseTo0, t0)) - v20BaseTo0) / len20) >= 0.0 && f20 <= 1.0 && root20 < pt1) {
            pointAndTime.x = v0X + f20 * v20X;
            pointAndTime.y = v0Y + f20 * v20Y;
            pointAndTime.z = v0Z + f20 * v20Z;
            pointAndTime.w = root20;
            t0 = root20;
            isect = 6;
        }
        if ((f21 = ((v21Vel = (v21X = v2X - v1X) * velX + (v21Y = v2Y - v1Y) * velY + (v21Z = v2Z - v1Z) * velZ) * (root21 = Intersectiond.computeLowestRoot(A21 = (len21 = v21X * v21X + v21Y * v21Y + v21Z * v21Z) * -velLen + v21Vel * v21Vel, B21 = len21 * 2.0 * (velBaseTo1 = velX * -centerV1X + velY * -centerV1Y + velZ * -centerV1Z) - 2.0 * v21Vel * (v21BaseTo1 = v21X * -centerV1X + v21Y * -centerV1Y + v21Z * -centerV1Z), C21 = len21 * (radius2 - (baseTo1Len = centerV1Len)) + v21BaseTo1 * v21BaseTo1, t0)) - v21BaseTo1) / len21) >= 0.0 && f21 <= 1.0 && root21 < t0) {
            pointAndTime.x = v1X + f21 * v21X;
            pointAndTime.y = v1Y + f21 * v21Y;
            pointAndTime.z = v1Z + f21 * v21Z;
            pointAndTime.w = root21;
            isect = 5;
        }
        return isect;
    }

    private static double computeLowestRoot(double a, double b, double c, double maxR) {
        double r2;
        double invA2;
        double determinant = b * b - 4.0 * a * c;
        if (determinant < 0.0) {
            return Double.POSITIVE_INFINITY;
        }
        double sqrtD = Math.sqrt(determinant);
        double r1 = (-b - sqrtD) * (invA2 = 1.0 / (2.0 * a));
        if (r1 > (r2 = (-b + sqrtD) * invA2)) {
            double temp = r2;
            r2 = r1;
            r1 = temp;
        }
        if (r1 > 0.0 && r1 < maxR) {
            return r1;
        }
        if (r2 > 0.0 && r2 < maxR) {
            return r2;
        }
        return Double.POSITIVE_INFINITY;
    }

    public static boolean testPointInTriangle(double pX, double pY, double pZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z) {
        double vpX = pX - v0X;
        double e10X = v1X - v0X;
        double vpY = pY - v0Y;
        double e10Y = v1Y - v0Y;
        double vpZ = pZ - v0Z;
        double e10Z = v1Z - v0Z;
        double d = vpX * e10X + vpY * e10Y + vpZ * e10Z;
        double e20X = v2X - v0X;
        double e20Y = v2Y - v0Y;
        double e20Z = v2Z - v0Z;
        double c = e20X * e20X + e20Y * e20Y + e20Z * e20Z;
        double e = vpX * e20X + vpY * e20Y + vpZ * e20Z;
        double b = e10X * e20X + e10Y * e20Y + e10Z * e20Z;
        double x = d * c - e * b;
        double a = e10X * e10X + e10Y * e10Y + e10Z * e10Z;
        double y = e * a - d * b;
        double ac_bb = a * c - b * b;
        double z = x + y - ac_bb;
        return (Runtime.doubleToLongBits(z) & ((Runtime.doubleToLongBits(x) | Runtime.doubleToLongBits(y)) ^ 0xFFFFFFFFFFFFFFFFL) & Long.MIN_VALUE) != 0L;
    }

    public static boolean intersectRaySphere(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double centerX, double centerY, double centerZ, double radiusSquared, Vector2d result) {
        double t1;
        double Lx = centerX - originX;
        double Ly = centerY - originY;
        double Lz = centerZ - originZ;
        double tca = Lx * dirX + Ly * dirY + Lz * dirZ;
        double d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        double thc = Math.sqrt(radiusSquared - d2);
        double t0 = tca - thc;
        if (t0 < (t1 = tca + thc) && t1 >= 0.0) {
            result.x = t0;
            result.y = t1;
            return true;
        }
        return false;
    }

    public static boolean intersectRaySphere(Vector3dc origin, Vector3dc dir, Vector3dc center, double radiusSquared, Vector2d result) {
        return Intersectiond.intersectRaySphere(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), center.x(), center.y(), center.z(), radiusSquared, result);
    }

    public static boolean testRaySphere(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double centerX, double centerY, double centerZ, double radiusSquared) {
        double t1;
        double Lx = centerX - originX;
        double Ly = centerY - originY;
        double Lz = centerZ - originZ;
        double tca = Lx * dirX + Ly * dirY + Lz * dirZ;
        double d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        double thc = Math.sqrt(radiusSquared - d2);
        double t0 = tca - thc;
        return t0 < (t1 = tca + thc) && t1 >= 0.0;
    }

    public static boolean testRaySphere(Vector3dc origin, Vector3dc dir, Vector3dc center, double radiusSquared) {
        return Intersectiond.testRaySphere(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), center.x(), center.y(), center.z(), radiusSquared);
    }

    public static boolean testLineSegmentSphere(double p0X, double p0Y, double p0Z, double p1X, double p1Y, double p1Z, double centerX, double centerY, double centerZ, double radiusSquared) {
        double dX = p1X - p0X;
        double dY = p1Y - p0Y;
        double dZ = p1Z - p0Z;
        double nom = (centerX - p0X) * dX + (centerY - p0Y) * dY + (centerZ - p0Z) * dZ;
        double den = dX * dX + dY * dY + dZ * dZ;
        double u = nom / den;
        if (u < 0.0) {
            dX = p0X - centerX;
            dY = p0Y - centerY;
            dZ = p0Z - centerZ;
        } else if (u > 1.0) {
            dX = p1X - centerX;
            dY = p1Y - centerY;
            dZ = p1Z - centerZ;
        } else {
            double pX = p0X + u * dX;
            double pY = p0Y + u * dY;
            double pZ = p0Z + u * dZ;
            dX = pX - centerX;
            dY = pY - centerY;
            dZ = pZ - centerZ;
        }
        double dist = dX * dX + dY * dY + dZ * dZ;
        return dist <= radiusSquared;
    }

    public static boolean testLineSegmentSphere(Vector3dc p0, Vector3dc p1, Vector3dc center, double radiusSquared) {
        return Intersectiond.testLineSegmentSphere(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), center.x(), center.y(), center.z(), radiusSquared);
    }

    public static boolean intersectRayAab(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vector2d result) {
        double tzmax;
        double tzmin;
        double tymax;
        double tymin;
        double tFar;
        double tNear;
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        double invDirZ = 1.0 / dirZ;
        if (invDirX >= 0.0) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return false;
        }
        if (invDirZ >= 0.0) {
            tzmin = (minZ - originZ) * invDirZ;
            tzmax = (maxZ - originZ) * invDirZ;
        } else {
            tzmin = (maxZ - originZ) * invDirZ;
            tzmax = (minZ - originZ) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar) {
            return false;
        }
        tNear = tymin > tNear || Double.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Double.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        double d = tFar = tzmax < tFar ? tzmax : tFar;
        if (tNear < tFar && tFar >= 0.0) {
            result.x = tNear;
            result.y = tFar;
            return true;
        }
        return false;
    }

    public static boolean intersectRayAab(Vector3dc origin, Vector3dc dir, Vector3dc min, Vector3dc max, Vector2d result) {
        return Intersectiond.intersectRayAab(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), result);
    }

    public static int intersectLineSegmentAab(double p0X, double p0Y, double p0Z, double p1X, double p1Y, double p1Z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vector2d result) {
        double tzmax;
        double tzmin;
        double tymax;
        double tymin;
        double tFar;
        double tNear;
        double dirX = p1X - p0X;
        double dirY = p1Y - p0Y;
        double dirZ = p1Z - p0Z;
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        double invDirZ = 1.0 / dirZ;
        if (invDirX >= 0.0) {
            tNear = (minX - p0X) * invDirX;
            tFar = (maxX - p0X) * invDirX;
        } else {
            tNear = (maxX - p0X) * invDirX;
            tFar = (minX - p0X) * invDirX;
        }
        if (invDirY >= 0.0) {
            tymin = (minY - p0Y) * invDirY;
            tymax = (maxY - p0Y) * invDirY;
        } else {
            tymin = (maxY - p0Y) * invDirY;
            tymax = (minY - p0Y) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return -1;
        }
        if (invDirZ >= 0.0) {
            tzmin = (minZ - p0Z) * invDirZ;
            tzmax = (maxZ - p0Z) * invDirZ;
        } else {
            tzmin = (maxZ - p0Z) * invDirZ;
            tzmax = (minZ - p0Z) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar) {
            return -1;
        }
        tNear = tymin > tNear || Double.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Double.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        tFar = tzmax < tFar ? tzmax : tFar;
        int type = -1;
        if (tNear <= tFar && tNear <= 1.0 && tFar >= 0.0) {
            if (tNear >= 0.0 && tFar > 1.0) {
                tFar = tNear;
                type = 1;
            } else if (tNear < 0.0 && tFar <= 1.0) {
                tNear = tFar;
                type = 1;
            } else {
                type = tNear < 0.0 && tFar > 1.0 ? 3 : 2;
            }
            result.x = tNear;
            result.y = tFar;
        }
        return type;
    }

    public static int intersectLineSegmentAab(Vector3dc p0, Vector3dc p1, Vector3dc min, Vector3dc max, Vector2d result) {
        return Intersectiond.intersectLineSegmentAab(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), result);
    }

    public static boolean testRayAab(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double tzmax;
        double tzmin;
        double tymax;
        double tymin;
        double tFar;
        double tNear;
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        double invDirZ = 1.0 / dirZ;
        if (invDirX >= 0.0) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return false;
        }
        if (invDirZ >= 0.0) {
            tzmin = (minZ - originZ) * invDirZ;
            tzmax = (maxZ - originZ) * invDirZ;
        } else {
            tzmin = (maxZ - originZ) * invDirZ;
            tzmax = (minZ - originZ) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar) {
            return false;
        }
        tNear = tymin > tNear || Double.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Double.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        tFar = tzmax < tFar ? tzmax : tFar;
        return tNear < tFar && tFar >= 0.0;
    }

    public static boolean testRayAab(Vector3dc origin, Vector3dc dir, Vector3dc min, Vector3dc max) {
        return Intersectiond.testRayAab(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public static boolean testRayTriangleFront(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon) {
        double edge1X = v1X - v0X;
        double edge2Z = v2Z - v0Z;
        double edge2Y = v2Y - v0Y;
        double pvecX = dirY * edge2Z - dirZ * edge2Y;
        double edge1Y = v1Y - v0Y;
        double edge2X = v2X - v0X;
        double pvecY = dirZ * edge2X - dirX * edge2Z;
        double edge1Z = v1Z - v0Z;
        double pvecZ = dirX * edge2Y - dirY * edge2X;
        double det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det < epsilon) {
            return false;
        }
        double tvecX = originX - v0X;
        double tvecY = originY - v0Y;
        double tvecZ = originZ - v0Z;
        double u = tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ;
        if (u < 0.0 || u > det) {
            return false;
        }
        double qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        double qvecY = tvecZ * edge1X - tvecX * edge1Z;
        double qvecZ = tvecX * edge1Y - tvecY * edge1X;
        double v = dirX * qvecX + dirY * qvecY + dirZ * qvecZ;
        if (v < 0.0 || u + v > det) {
            return false;
        }
        double t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) / det;
        return t >= epsilon;
    }

    public static boolean testRayTriangleFront(Vector3dc origin, Vector3dc dir, Vector3dc v0, Vector3dc v1, Vector3dc v2, double epsilon) {
        return Intersectiond.testRayTriangleFront(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static boolean testRayTriangle(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon) {
        double edge1X = v1X - v0X;
        double edge2Z = v2Z - v0Z;
        double edge2Y = v2Y - v0Y;
        double pvecX = dirY * edge2Z - dirZ * edge2Y;
        double edge1Y = v1Y - v0Y;
        double edge2X = v2X - v0X;
        double pvecY = dirZ * edge2X - dirX * edge2Z;
        double edge1Z = v1Z - v0Z;
        double pvecZ = dirX * edge2Y - dirY * edge2X;
        double det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det > -epsilon && det < epsilon) {
            return false;
        }
        double tvecX = originX - v0X;
        double tvecY = originY - v0Y;
        double tvecZ = originZ - v0Z;
        double invDet = 1.0 / det;
        double u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet;
        if (u < 0.0 || u > 1.0) {
            return false;
        }
        double qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        double qvecY = tvecZ * edge1X - tvecX * edge1Z;
        double qvecZ = tvecX * edge1Y - tvecY * edge1X;
        double v = (dirX * qvecX + dirY * qvecY + dirZ * qvecZ) * invDet;
        if (v < 0.0 || u + v > 1.0) {
            return false;
        }
        double t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet;
        return t >= epsilon;
    }

    public static boolean testRayTriangle(Vector3dc origin, Vector3dc dir, Vector3dc v0, Vector3dc v1, Vector3dc v2, double epsilon) {
        return Intersectiond.testRayTriangle(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static double intersectRayTriangleFront(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon) {
        double edge1X = v1X - v0X;
        double edge2Z = v2Z - v0Z;
        double edge2Y = v2Y - v0Y;
        double pvecX = dirY * edge2Z - dirZ * edge2Y;
        double edge1Y = v1Y - v0Y;
        double edge2X = v2X - v0X;
        double pvecY = dirZ * edge2X - dirX * edge2Z;
        double edge1Z = v1Z - v0Z;
        double pvecZ = dirX * edge2Y - dirY * edge2X;
        double det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det <= epsilon) {
            return -1.0;
        }
        double tvecX = originX - v0X;
        double tvecY = originY - v0Y;
        double tvecZ = originZ - v0Z;
        double u = tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ;
        if (u < 0.0 || u > det) {
            return -1.0;
        }
        double qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        double qvecY = tvecZ * edge1X - tvecX * edge1Z;
        double qvecZ = tvecX * edge1Y - tvecY * edge1X;
        double v = dirX * qvecX + dirY * qvecY + dirZ * qvecZ;
        if (v < 0.0 || u + v > det) {
            return -1.0;
        }
        return (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) / det;
    }

    public static double intersectRayTriangleFront(Vector3dc origin, Vector3dc dir, Vector3dc v0, Vector3dc v1, Vector3dc v2, double epsilon) {
        return Intersectiond.intersectRayTriangleFront(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static double intersectRayTriangle(double originX, double originY, double originZ, double dirX, double dirY, double dirZ, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon) {
        double edge1X = v1X - v0X;
        double edge2Z = v2Z - v0Z;
        double edge2Y = v2Y - v0Y;
        double pvecX = dirY * edge2Z - dirZ * edge2Y;
        double edge1Y = v1Y - v0Y;
        double edge2X = v2X - v0X;
        double pvecY = dirZ * edge2X - dirX * edge2Z;
        double edge1Z = v1Z - v0Z;
        double pvecZ = dirX * edge2Y - dirY * edge2X;
        double det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
        if (det > -epsilon && det < epsilon) {
            return -1.0;
        }
        double tvecX = originX - v0X;
        double tvecY = originY - v0Y;
        double tvecZ = originZ - v0Z;
        double invDet = 1.0 / det;
        double u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet;
        if (u < 0.0 || u > 1.0) {
            return -1.0;
        }
        double qvecX = tvecY * edge1Z - tvecZ * edge1Y;
        double qvecY = tvecZ * edge1X - tvecX * edge1Z;
        double qvecZ = tvecX * edge1Y - tvecY * edge1X;
        double v = (dirX * qvecX + dirY * qvecY + dirZ * qvecZ) * invDet;
        if (v < 0.0 || u + v > 1.0) {
            return -1.0;
        }
        return (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet;
    }

    public static double intersectRayTriangle(Vector3dc origin, Vector3dc dir, Vector3dc v0, Vector3dc v1, Vector3dc v2, double epsilon) {
        return Intersectiond.intersectRayTriangle(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static boolean testLineSegmentTriangle(double p0X, double p0Y, double p0Z, double p1X, double p1Y, double p1Z, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon) {
        double dirX = p1X - p0X;
        double dirY = p1Y - p0Y;
        double dirZ = p1Z - p0Z;
        double t = Intersectiond.intersectRayTriangle(p0X, p0Y, p0Z, dirX, dirY, dirZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, epsilon);
        return t >= 0.0 && t <= 1.0;
    }

    public static boolean testLineSegmentTriangle(Vector3dc p0, Vector3dc p1, Vector3dc v0, Vector3dc v1, Vector3dc v2, double epsilon) {
        return Intersectiond.testLineSegmentTriangle(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon);
    }

    public static boolean intersectLineSegmentTriangle(double p0X, double p0Y, double p0Z, double p1X, double p1Y, double p1Z, double v0X, double v0Y, double v0Z, double v1X, double v1Y, double v1Z, double v2X, double v2Y, double v2Z, double epsilon, Vector3d intersectionPoint) {
        double dirX = p1X - p0X;
        double dirY = p1Y - p0Y;
        double dirZ = p1Z - p0Z;
        double t = Intersectiond.intersectRayTriangle(p0X, p0Y, p0Z, dirX, dirY, dirZ, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, epsilon);
        if (t >= 0.0 && t <= 1.0) {
            intersectionPoint.x = p0X + dirX * t;
            intersectionPoint.y = p0Y + dirY * t;
            intersectionPoint.z = p0Z + dirZ * t;
            return true;
        }
        return false;
    }

    public static boolean intersectLineSegmentTriangle(Vector3dc p0, Vector3dc p1, Vector3dc v0, Vector3dc v1, Vector3dc v2, double epsilon, Vector3d intersectionPoint) {
        return Intersectiond.intersectLineSegmentTriangle(p0.x(), p0.y(), p0.z(), p1.x(), p1.y(), p1.z(), v0.x(), v0.y(), v0.z(), v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), epsilon, intersectionPoint);
    }

    public static boolean intersectLineSegmentPlane(double p0X, double p0Y, double p0Z, double p1X, double p1Y, double p1Z, double a, double b, double c, double d, Vector3d intersectionPoint) {
        double dirX = p1X - p0X;
        double dirY = p1Y - p0Y;
        double dirZ = p1Z - p0Z;
        double denom = a * dirX + b * dirY + c * dirZ;
        double t = -(a * p0X + b * p0Y + c * p0Z + d) / denom;
        if (t >= 0.0 && t <= 1.0) {
            intersectionPoint.x = p0X + t * dirX;
            intersectionPoint.y = p0Y + t * dirY;
            intersectionPoint.z = p0Z + t * dirZ;
            return true;
        }
        return false;
    }

    public static boolean testLineCircle(double a, double b, double c, double centerX, double centerY, double radius) {
        double denom = Math.sqrt(a * a + b * b);
        double dist = (a * centerX + b * centerY + c) / denom;
        return -radius <= dist && dist <= radius;
    }

    public static boolean intersectLineCircle(double a, double b, double c, double centerX, double centerY, double radius, Vector3d intersectionCenterAndHL) {
        double invDenom = Math.invsqrt(a * a + b * b);
        double dist = (a * centerX + b * centerY + c) * invDenom;
        if (-radius <= dist && dist <= radius) {
            intersectionCenterAndHL.x = centerX + dist * a * invDenom;
            intersectionCenterAndHL.y = centerY + dist * b * invDenom;
            intersectionCenterAndHL.z = Math.sqrt(radius * radius - dist * dist);
            return true;
        }
        return false;
    }

    public static boolean intersectLineCircle(double x0, double y0, double x1, double y1, double centerX, double centerY, double radius, Vector3d intersectionCenterAndHL) {
        return Intersectiond.intersectLineCircle(y0 - y1, x1 - x0, (x0 - x1) * y0 + (y1 - y0) * x0, centerX, centerY, radius, intersectionCenterAndHL);
    }

    public static boolean testAarLine(double minX, double minY, double maxX, double maxY, double a, double b, double c) {
        double nY;
        double pY;
        double nX;
        double pX;
        if (a > 0.0) {
            pX = maxX;
            nX = minX;
        } else {
            pX = minX;
            nX = maxX;
        }
        if (b > 0.0) {
            pY = maxY;
            nY = minY;
        } else {
            pY = minY;
            nY = maxY;
        }
        double distN = c + a * nX + b * nY;
        double distP = c + a * pX + b * pY;
        return distN <= 0.0 && distP >= 0.0;
    }

    public static boolean testAarLine(Vector2dc min, Vector2dc max, double a, double b, double c) {
        return Intersectiond.testAarLine(min.x(), min.y(), max.x(), max.y(), a, b, c);
    }

    public static boolean testAarLine(double minX, double minY, double maxX, double maxY, double x0, double y0, double x1, double y1) {
        double a = y0 - y1;
        double b = x1 - x0;
        double c = -b * y0 - a * x0;
        return Intersectiond.testAarLine(minX, minY, maxX, maxY, a, b, c);
    }

    public static boolean testAarAar(double minXA, double minYA, double maxXA, double maxYA, double minXB, double minYB, double maxXB, double maxYB) {
        return maxXA >= minXB && maxYA >= minYB && minXA <= maxXB && minYA <= maxYB;
    }

    public static boolean testAarAar(Vector2dc minA, Vector2dc maxA, Vector2dc minB, Vector2dc maxB) {
        return Intersectiond.testAarAar(minA.x(), minA.y(), maxA.x(), maxA.y(), minB.x(), minB.y(), maxB.x(), maxB.y());
    }

    public static boolean testMovingCircleCircle(double aX, double aY, double maX, double maY, double aR, double bX, double bY, double bR) {
        double aRbR2;
        double aRbR = aR + bR;
        double dist = Math.sqrt((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY)) - aRbR;
        double mLen = Math.sqrt(maX * maX + maY * maY);
        if (mLen < dist) {
            return false;
        }
        double invMLen = 1.0 / mLen;
        double nX = maX * invMLen;
        double cX = bX - aX;
        double nY = maY * invMLen;
        double cY = bY - aY;
        double nDotC = nX * cX + nY * cY;
        if (nDotC <= 0.0) {
            return false;
        }
        double cLen = Math.sqrt(cX * cX + cY * cY);
        double cLenNdotC = cLen * cLen - nDotC * nDotC;
        if (cLenNdotC >= (aRbR2 = aRbR * aRbR)) {
            return false;
        }
        double t = aRbR2 - cLenNdotC;
        if (t < 0.0) {
            return false;
        }
        double mag = mLen;
        double distance = nDotC - Math.sqrt(t);
        return !(mag < distance);
    }

    public static boolean testMovingCircleCircle(Vector2d centerA, Vector2d moveA, double aR, Vector2d centerB, double bR) {
        return Intersectiond.testMovingCircleCircle(centerA.x, centerA.y, moveA.x, moveA.y, aR, centerB.x, centerB.y, bR);
    }

    public static boolean intersectCircleCircle(double aX, double aY, double radiusSquaredA, double bX, double bY, double radiusSquaredB, Vector3d intersectionCenterAndHL) {
        double dX = bX - aX;
        double dY = bY - aY;
        double distSquared = dX * dX + dY * dY;
        double h = 0.5 + (radiusSquaredA - radiusSquaredB) / distSquared;
        double r_i = Math.sqrt(radiusSquaredA - h * h * distSquared);
        if (r_i >= 0.0) {
            intersectionCenterAndHL.x = aX + h * dX;
            intersectionCenterAndHL.y = aY + h * dY;
            intersectionCenterAndHL.z = r_i;
            return true;
        }
        return false;
    }

    public static boolean intersectCircleCircle(Vector2dc centerA, double radiusSquaredA, Vector2dc centerB, double radiusSquaredB, Vector3d intersectionCenterAndHL) {
        return Intersectiond.intersectCircleCircle(centerA.x(), centerA.y(), radiusSquaredA, centerB.x(), centerB.y(), radiusSquaredB, intersectionCenterAndHL);
    }

    public static boolean testCircleCircle(double aX, double aY, double rA, double bX, double bY, double rB) {
        double d = (aX - bX) * (aX - bX) + (aY - bY) * (aY - bY);
        return d <= (rA + rB) * (rA + rB);
    }

    public static boolean testCircleCircle(Vector2dc centerA, double radiusSquaredA, Vector2dc centerB, double radiusSquaredB) {
        return Intersectiond.testCircleCircle(centerA.x(), centerA.y(), radiusSquaredA, centerB.x(), centerB.y(), radiusSquaredB);
    }

    public static double distancePointLine(double pointX, double pointY, double a, double b, double c) {
        double denom = Math.sqrt(a * a + b * b);
        return (a * pointX + b * pointY + c) / denom;
    }

    public static double distancePointLine(double pointX, double pointY, double x0, double y0, double x1, double y1) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        double denom = Math.sqrt(dx * dx + dy * dy);
        return (dx * (y0 - pointY) - (x0 - pointX) * dy) / denom;
    }

    public static double distancePointLine(double pX, double pY, double pZ, double x0, double y0, double z0, double x1, double y1, double z1) {
        double d21x = x1 - x0;
        double d21y = y1 - y0;
        double d21z = z1 - z0;
        double d10x = x0 - pX;
        double d10y = y0 - pY;
        double d10z = z0 - pZ;
        double cx = d21y * d10z - d21z * d10y;
        double cy = d21z * d10x - d21x * d10z;
        double cz = d21x * d10y - d21y * d10x;
        return Math.sqrt((cx * cx + cy * cy + cz * cz) / (d21x * d21x + d21y * d21y + d21z * d21z));
    }

    public static double intersectRayLine(double originX, double originY, double dirX, double dirY, double pointX, double pointY, double normalX, double normalY, double epsilon) {
        double t;
        double denom = normalX * dirX + normalY * dirY;
        if (denom < epsilon && (t = ((pointX - originX) * normalX + (pointY - originY) * normalY) / denom) >= 0.0) {
            return t;
        }
        return -1.0;
    }

    public static double intersectRayLine(Vector2dc origin, Vector2dc dir, Vector2dc point, Vector2dc normal, double epsilon) {
        return Intersectiond.intersectRayLine(origin.x(), origin.y(), dir.x(), dir.y(), point.x(), point.y(), normal.x(), normal.y(), epsilon);
    }

    public static double intersectRayLineSegment(double originX, double originY, double dirX, double dirY, double aX, double aY, double bX, double bY) {
        double v1X = originX - aX;
        double v1Y = originY - aY;
        double v2X = bX - aX;
        double v2Y = bY - aY;
        double invV23 = 1.0 / (v2Y * dirX - v2X * dirY);
        double t1 = (v2X * v1Y - v2Y * v1X) * invV23;
        double t2 = (v1Y * dirX - v1X * dirY) * invV23;
        if (t1 >= 0.0 && t2 >= 0.0 && t2 <= 1.0) {
            return t1;
        }
        return -1.0;
    }

    public static double intersectRayLineSegment(Vector2dc origin, Vector2dc dir, Vector2dc a, Vector2dc b) {
        return Intersectiond.intersectRayLineSegment(origin.x(), origin.y(), dir.x(), dir.y(), a.x(), a.y(), b.x(), b.y());
    }

    public static boolean testAarCircle(double minX, double minY, double maxX, double maxY, double centerX, double centerY, double radiusSquared) {
        double d;
        double radius2 = radiusSquared;
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
        return radius2 >= 0.0;
    }

    public static boolean testAarCircle(Vector2dc min, Vector2dc max, Vector2dc center, double radiusSquared) {
        return Intersectiond.testAarCircle(min.x(), min.y(), max.x(), max.y(), center.x(), center.y(), radiusSquared);
    }

    public static int findClosestPointOnTriangle(double v0X, double v0Y, double v1X, double v1Y, double v2X, double v2Y, double pX, double pY, Vector2d result) {
        double abX = v1X - v0X;
        double abY = v1Y - v0Y;
        double acX = v2X - v0X;
        double acY = v2Y - v0Y;
        double apX = pX - v0X;
        double apY = pY - v0Y;
        double d1 = abX * apX + abY * apY;
        double d2 = acX * apX + acY * apY;
        if (d1 <= 0.0 && d2 <= 0.0) {
            result.x = v0X;
            result.y = v0Y;
            return 1;
        }
        double bpX = pX - v1X;
        double bpY = pY - v1Y;
        double d3 = abX * bpX + abY * bpY;
        double d4 = acX * bpX + acY * bpY;
        if (d3 >= 0.0 && d4 <= d3) {
            result.x = v1X;
            result.y = v1Y;
            return 2;
        }
        double vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0 && d1 >= 0.0 && d3 <= 0.0) {
            double v = d1 / (d1 - d3);
            result.x = v0X + v * abX;
            result.y = v0Y + v * abY;
            return 4;
        }
        double cpX = pX - v2X;
        double cpY = pY - v2Y;
        double d5 = abX * cpX + abY * cpY;
        double d6 = acX * cpX + acY * cpY;
        if (d6 >= 0.0 && d5 <= d6) {
            result.x = v2X;
            result.y = v2Y;
            return 3;
        }
        double vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0 && d2 >= 0.0 && d6 <= 0.0) {
            double w = d2 / (d2 - d6);
            result.x = v0X + w * acX;
            result.y = v0Y + w * acY;
            return 6;
        }
        double va = d3 * d6 - d5 * d4;
        if (va <= 0.0 && d4 - d3 >= 0.0 && d5 - d6 >= 0.0) {
            double w = (d4 - d3) / (d4 - d3 + d5 - d6);
            result.x = v1X + w * (v2X - v1X);
            result.y = v1Y + w * (v2Y - v1Y);
            return 5;
        }
        double denom = 1.0 / (va + vb + vc);
        double v = vb * denom;
        double w = vc * denom;
        result.x = v0X + abX * v + acX * w;
        result.y = v0Y + abY * v + acY * w;
        return 7;
    }

    public static int findClosestPointOnTriangle(Vector2dc v0, Vector2dc v1, Vector2dc v2, Vector2dc p, Vector2d result) {
        return Intersectiond.findClosestPointOnTriangle(v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y(), p.x(), p.y(), result);
    }

    public static boolean intersectRayCircle(double originX, double originY, double dirX, double dirY, double centerX, double centerY, double radiusSquared, Vector2d result) {
        double t1;
        double Lx = centerX - originX;
        double Ly = centerY - originY;
        double tca = Lx * dirX + Ly * dirY;
        double d2 = Lx * Lx + Ly * Ly - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        double thc = Math.sqrt(radiusSquared - d2);
        double t0 = tca - thc;
        if (t0 < (t1 = tca + thc) && t1 >= 0.0) {
            result.x = t0;
            result.y = t1;
            return true;
        }
        return false;
    }

    public static boolean intersectRayCircle(Vector2dc origin, Vector2dc dir, Vector2dc center, double radiusSquared, Vector2d result) {
        return Intersectiond.intersectRayCircle(origin.x(), origin.y(), dir.x(), dir.y(), center.x(), center.y(), radiusSquared, result);
    }

    public static boolean testRayCircle(double originX, double originY, double dirX, double dirY, double centerX, double centerY, double radiusSquared) {
        double t1;
        double Lx = centerX - originX;
        double Ly = centerY - originY;
        double tca = Lx * dirX + Ly * dirY;
        double d2 = Lx * Lx + Ly * Ly - tca * tca;
        if (d2 > radiusSquared) {
            return false;
        }
        double thc = Math.sqrt(radiusSquared - d2);
        double t0 = tca - thc;
        return t0 < (t1 = tca + thc) && t1 >= 0.0;
    }

    public static boolean testRayCircle(Vector2dc origin, Vector2dc dir, Vector2dc center, double radiusSquared) {
        return Intersectiond.testRayCircle(origin.x(), origin.y(), dir.x(), dir.y(), center.x(), center.y(), radiusSquared);
    }

    public static int intersectRayAar(double originX, double originY, double dirX, double dirY, double minX, double minY, double maxX, double maxY, Vector2d result) {
        double tymax;
        double tymin;
        double tFar;
        double tNear;
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        if (invDirX >= 0.0) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return -1;
        }
        tNear = tymin > tNear || Double.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Double.isNaN(tFar) ? tymax : tFar;
        int side = -1;
        if (tNear < tFar && tFar >= 0.0) {
            double px = originX + tNear * dirX;
            double py = originY + tNear * dirY;
            result.x = tNear;
            result.y = tFar;
            double daX = Math.abs(px - minX);
            double daY = Math.abs(py - minY);
            double dbX = Math.abs(px - maxX);
            double dbY = Math.abs(py - maxY);
            side = 0;
            double min = daX;
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

    public static int intersectRayAar(Vector2dc origin, Vector2dc dir, Vector2dc min, Vector2dc max, Vector2d result) {
        return Intersectiond.intersectRayAar(origin.x(), origin.y(), dir.x(), dir.y(), min.x(), min.y(), max.x(), max.y(), result);
    }

    public static int intersectLineSegmentAar(double p0X, double p0Y, double p1X, double p1Y, double minX, double minY, double maxX, double maxY, Vector2d result) {
        double tymax;
        double tymin;
        double tFar;
        double tNear;
        double dirX = p1X - p0X;
        double dirY = p1Y - p0Y;
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        if (invDirX >= 0.0) {
            tNear = (minX - p0X) * invDirX;
            tFar = (maxX - p0X) * invDirX;
        } else {
            tNear = (maxX - p0X) * invDirX;
            tFar = (minX - p0X) * invDirX;
        }
        if (invDirY >= 0.0) {
            tymin = (minY - p0Y) * invDirY;
            tymax = (maxY - p0Y) * invDirY;
        } else {
            tymin = (maxY - p0Y) * invDirY;
            tymax = (minY - p0Y) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return -1;
        }
        tNear = tymin > tNear || Double.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Double.isNaN(tFar) ? tymax : tFar;
        int type = -1;
        if (tNear <= tFar && tNear <= 1.0 && tFar >= 0.0) {
            if (tNear >= 0.0 && tFar > 1.0) {
                tFar = tNear;
                type = 1;
            } else if (tNear < 0.0 && tFar <= 1.0) {
                tNear = tFar;
                type = 1;
            } else {
                type = tNear < 0.0 && tFar > 1.0 ? 3 : 2;
            }
            result.x = tNear;
            result.y = tFar;
        }
        return type;
    }

    public static int intersectLineSegmentAar(Vector2dc p0, Vector2dc p1, Vector2dc min, Vector2dc max, Vector2d result) {
        return Intersectiond.intersectLineSegmentAar(p0.x(), p0.y(), p1.x(), p1.y(), min.x(), min.y(), max.x(), max.y(), result);
    }

    public static boolean testRayAar(double originX, double originY, double dirX, double dirY, double minX, double minY, double maxX, double maxY) {
        double tymax;
        double tymin;
        double tFar;
        double tNear;
        double invDirX = 1.0 / dirX;
        double invDirY = 1.0 / dirY;
        if (invDirX >= 0.0) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar) {
            return false;
        }
        tNear = tymin > tNear || Double.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Double.isNaN(tFar) ? tymax : tFar;
        return tNear < tFar && tFar >= 0.0;
    }

    public static boolean testRayAar(Vector2dc origin, Vector2dc dir, Vector2dc min, Vector2dc max) {
        return Intersectiond.testRayAar(origin.x(), origin.y(), dir.x(), dir.y(), min.x(), min.y(), max.x(), max.y());
    }

    public static boolean testPointTriangle(double pX, double pY, double v0X, double v0Y, double v1X, double v1Y, double v2X, double v2Y) {
        boolean b2;
        boolean b1 = (pX - v1X) * (v0Y - v1Y) - (v0X - v1X) * (pY - v1Y) < 0.0;
        boolean bl = b2 = (pX - v2X) * (v1Y - v2Y) - (v1X - v2X) * (pY - v2Y) < 0.0;
        if (b1 != b2) {
            return false;
        }
        boolean b3 = (pX - v0X) * (v2Y - v0Y) - (v2X - v0X) * (pY - v0Y) < 0.0;
        return b2 == b3;
    }

    public static boolean testPointTriangle(Vector2dc point, Vector2dc v0, Vector2dc v1, Vector2dc v2) {
        return Intersectiond.testPointTriangle(point.x(), point.y(), v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y());
    }

    public static boolean testPointAar(double pX, double pY, double minX, double minY, double maxX, double maxY) {
        return pX >= minX && pY >= minY && pX <= maxX && pY <= maxY;
    }

    public static boolean testPointCircle(double pX, double pY, double centerX, double centerY, double radiusSquared) {
        double dx = pX - centerX;
        double dx2 = dx * dx;
        double dy = pY - centerY;
        double dy2 = dy * dy;
        return dx2 + dy2 <= radiusSquared;
    }

    public static boolean testCircleTriangle(double centerX, double centerY, double radiusSquared, double v0X, double v0Y, double v1X, double v1Y, double v2X, double v2Y) {
        double len;
        double c1x = centerX - v0X;
        double c1y = centerY - v0Y;
        double c1sqr = c1x * c1x + c1y * c1y - radiusSquared;
        if (c1sqr <= 0.0) {
            return true;
        }
        double c2x = centerX - v1X;
        double c2y = centerY - v1Y;
        double c2sqr = c2x * c2x + c2y * c2y - radiusSquared;
        if (c2sqr <= 0.0) {
            return true;
        }
        double c3x = centerX - v2X;
        double c3y = centerY - v2Y;
        double c3sqr = c3x * c3x + c3y * c3y - radiusSquared;
        if (c3sqr <= 0.0) {
            return true;
        }
        double e1x = v1X - v0X;
        double e1y = v1Y - v0Y;
        double e2x = v2X - v1X;
        double e2y = v2Y - v1Y;
        double e3x = v0X - v2X;
        double e3y = v0Y - v2Y;
        if (e1x * c1y - e1y * c1x >= 0.0 && e2x * c2y - e2y * c2x >= 0.0 && e3x * c3y - e3y * c3x >= 0.0) {
            return true;
        }
        double k = c1x * e1x + c1y * e1y;
        if (k >= 0.0 && k <= (len = e1x * e1x + e1y * e1y) && c1sqr * len <= k * k) {
            return true;
        }
        k = c2x * e2x + c2y * e2y;
        if (k > 0.0 && k <= (len = e2x * e2x + e2y * e2y) && c2sqr * len <= k * k) {
            return true;
        }
        k = c3x * e3x + c3y * e3y;
        return k >= 0.0 && k < (len = e3x * e3x + e3y * e3y) && c3sqr * len <= k * k;
    }

    public static boolean testCircleTriangle(Vector2dc center, double radiusSquared, Vector2dc v0, Vector2dc v1, Vector2dc v2) {
        return Intersectiond.testCircleTriangle(center.x(), center.y(), radiusSquared, v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y());
    }

    public static int intersectPolygonRay(double[] verticesXY, double originX, double originY, double dirX, double dirY, Vector2d p) {
        double nearestT = Double.POSITIVE_INFINITY;
        int count = verticesXY.length >> 1;
        int edgeIndex = -1;
        double aX = verticesXY[count - 1 << 1];
        double aY = verticesXY[(count - 1 << 1) + 1];
        for (int i = 0; i < count; ++i) {
            double t2;
            double bX = verticesXY[i << 1];
            double dbaX = bX - aX;
            double doaY = originY - aY;
            double bY = verticesXY[(i << 1) + 1];
            double dbaY = bY - aY;
            double doaX = originX - aX;
            double invDbaDir = 1.0 / (dbaY * dirX - dbaX * dirY);
            double t = (dbaX * doaY - dbaY * doaX) * invDbaDir;
            if (t >= 0.0 && t < nearestT && (t2 = (doaY * dirX - doaX * dirY) * invDbaDir) >= 0.0 && t2 <= 1.0) {
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

    public static int intersectPolygonRay(Vector2dc[] vertices, double originX, double originY, double dirX, double dirY, Vector2d p) {
        double nearestT = Double.POSITIVE_INFINITY;
        int count = vertices.length;
        int edgeIndex = -1;
        double aX = vertices[count - 1].x();
        double aY = vertices[count - 1].y();
        for (int i = 0; i < count; ++i) {
            double t2;
            double invDbaDir;
            double doaX;
            double bY;
            double dbaY;
            double doaY;
            Vector2dc b = vertices[i];
            double bX = b.x();
            double dbaX = bX - aX;
            double t = (dbaX * (doaY = originY - aY) - (dbaY = (bY = b.y()) - aY) * (doaX = originX - aX)) * (invDbaDir = 1.0 / (dbaY * dirX - dbaX * dirY));
            if (t >= 0.0 && t < nearestT && (t2 = (doaY * dirX - doaX * dirY) * invDbaDir) >= 0.0 && t2 <= 1.0) {
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

    public static boolean intersectLineLine(double ps1x, double ps1y, double pe1x, double pe1y, double ps2x, double ps2y, double pe2x, double pe2y, Vector2d p) {
        double d1x = ps1x - pe1x;
        double d1y = pe1y - ps1y;
        double d1ps1 = d1y * ps1x + d1x * ps1y;
        double d2x = ps2x - pe2x;
        double d2y = pe2y - ps2y;
        double d2ps2 = d2y * ps2x + d2x * ps2y;
        double det = d1y * d2x - d2y * d1x;
        if (det == 0.0) {
            return false;
        }
        p.x = (d2x * d1ps1 - d1x * d2ps2) / det;
        p.y = (d1y * d2ps2 - d2y * d1ps1) / det;
        return true;
    }

    private static boolean separatingAxis(Vector2d[] v1s, Vector2d[] v2s, double aX, double aY) {
        double minA = Double.POSITIVE_INFINITY;
        double maxA = Double.NEGATIVE_INFINITY;
        double minB = Double.POSITIVE_INFINITY;
        double maxB = Double.NEGATIVE_INFINITY;
        int maxLen = Math.max(v1s.length, v2s.length);
        for (int k = 0; k < maxLen; ++k) {
            double d;
            if (k < v1s.length) {
                Vector2d v1 = v1s[k];
                d = v1.x * aX + v1.y * aY;
                if (d < minA) {
                    minA = d;
                }
                if (d > maxA) {
                    maxA = d;
                }
            }
            if (k < v2s.length) {
                Vector2d v2 = v2s[k];
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

    public static boolean testPolygonPolygon(Vector2d[] v1s, Vector2d[] v2s) {
        Vector2d t;
        Vector2d s;
        int i = 0;
        int j = v1s.length - 1;
        while (i < v1s.length) {
            s = v1s[i];
            t = v1s[j];
            if (Intersectiond.separatingAxis(v1s, v2s, s.y - t.y, t.x - s.x)) {
                return false;
            }
            j = i++;
        }
        i = 0;
        j = v2s.length - 1;
        while (i < v2s.length) {
            s = v2s[i];
            t = v2s[j];
            if (Intersectiond.separatingAxis(v1s, v2s, s.y - t.y, t.x - s.x)) {
                return false;
            }
            j = i++;
        }
        return true;
    }
}

