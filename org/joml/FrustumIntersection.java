/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Math;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class FrustumIntersection {
    public static final int PLANE_NX = 0;
    public static final int PLANE_PX = 1;
    public static final int PLANE_NY = 2;
    public static final int PLANE_PY = 3;
    public static final int PLANE_NZ = 4;
    public static final int PLANE_PZ = 5;
    public static final int INTERSECT = -1;
    public static final int INSIDE = -2;
    public static final int OUTSIDE = -3;
    public static final int PLANE_MASK_NX = 1;
    public static final int PLANE_MASK_PX = 2;
    public static final int PLANE_MASK_NY = 4;
    public static final int PLANE_MASK_PY = 8;
    public static final int PLANE_MASK_NZ = 16;
    public static final int PLANE_MASK_PZ = 32;
    private float nxX;
    private float nxY;
    private float nxZ;
    private float nxW;
    private float pxX;
    private float pxY;
    private float pxZ;
    private float pxW;
    private float nyX;
    private float nyY;
    private float nyZ;
    private float nyW;
    private float pyX;
    private float pyY;
    private float pyZ;
    private float pyW;
    private float nzX;
    private float nzY;
    private float nzZ;
    private float nzW;
    private float pzX;
    private float pzY;
    private float pzZ;
    private float pzW;
    private final Vector4f[] planes = new Vector4f[6];

    public FrustumIntersection() {
        for (int i = 0; i < 6; ++i) {
            this.planes[i] = new Vector4f();
        }
    }

    public FrustumIntersection(Matrix4fc m) {
        for (int i = 0; i < 6; ++i) {
            this.planes[i] = new Vector4f();
        }
        this.set(m, true);
    }

    public FrustumIntersection(Matrix4fc m, boolean allowTestSpheres) {
        for (int i = 0; i < 6; ++i) {
            this.planes[i] = new Vector4f();
        }
        this.set(m, allowTestSpheres);
    }

    public FrustumIntersection set(Matrix4fc m) {
        return this.set(m, true);
    }

    public FrustumIntersection set(Matrix4fc m, boolean allowTestSpheres) {
        float invl;
        this.nxX = m.m03() + m.m00();
        this.nxY = m.m13() + m.m10();
        this.nxZ = m.m23() + m.m20();
        this.nxW = m.m33() + m.m30();
        if (allowTestSpheres) {
            invl = Math.invsqrt(this.nxX * this.nxX + this.nxY * this.nxY + this.nxZ * this.nxZ);
            this.nxX *= invl;
            this.nxY *= invl;
            this.nxZ *= invl;
            this.nxW *= invl;
        }
        this.planes[0].set(this.nxX, this.nxY, this.nxZ, this.nxW);
        this.pxX = m.m03() - m.m00();
        this.pxY = m.m13() - m.m10();
        this.pxZ = m.m23() - m.m20();
        this.pxW = m.m33() - m.m30();
        if (allowTestSpheres) {
            invl = Math.invsqrt(this.pxX * this.pxX + this.pxY * this.pxY + this.pxZ * this.pxZ);
            this.pxX *= invl;
            this.pxY *= invl;
            this.pxZ *= invl;
            this.pxW *= invl;
        }
        this.planes[1].set(this.pxX, this.pxY, this.pxZ, this.pxW);
        this.nyX = m.m03() + m.m01();
        this.nyY = m.m13() + m.m11();
        this.nyZ = m.m23() + m.m21();
        this.nyW = m.m33() + m.m31();
        if (allowTestSpheres) {
            invl = Math.invsqrt(this.nyX * this.nyX + this.nyY * this.nyY + this.nyZ * this.nyZ);
            this.nyX *= invl;
            this.nyY *= invl;
            this.nyZ *= invl;
            this.nyW *= invl;
        }
        this.planes[2].set(this.nyX, this.nyY, this.nyZ, this.nyW);
        this.pyX = m.m03() - m.m01();
        this.pyY = m.m13() - m.m11();
        this.pyZ = m.m23() - m.m21();
        this.pyW = m.m33() - m.m31();
        if (allowTestSpheres) {
            invl = Math.invsqrt(this.pyX * this.pyX + this.pyY * this.pyY + this.pyZ * this.pyZ);
            this.pyX *= invl;
            this.pyY *= invl;
            this.pyZ *= invl;
            this.pyW *= invl;
        }
        this.planes[3].set(this.pyX, this.pyY, this.pyZ, this.pyW);
        this.nzX = m.m03() + m.m02();
        this.nzY = m.m13() + m.m12();
        this.nzZ = m.m23() + m.m22();
        this.nzW = m.m33() + m.m32();
        if (allowTestSpheres) {
            invl = Math.invsqrt(this.nzX * this.nzX + this.nzY * this.nzY + this.nzZ * this.nzZ);
            this.nzX *= invl;
            this.nzY *= invl;
            this.nzZ *= invl;
            this.nzW *= invl;
        }
        this.planes[4].set(this.nzX, this.nzY, this.nzZ, this.nzW);
        this.pzX = m.m03() - m.m02();
        this.pzY = m.m13() - m.m12();
        this.pzZ = m.m23() - m.m22();
        this.pzW = m.m33() - m.m32();
        if (allowTestSpheres) {
            invl = Math.invsqrt(this.pzX * this.pzX + this.pzY * this.pzY + this.pzZ * this.pzZ);
            this.pzX *= invl;
            this.pzY *= invl;
            this.pzZ *= invl;
            this.pzW *= invl;
        }
        this.planes[5].set(this.pzX, this.pzY, this.pzZ, this.pzW);
        return this;
    }

    public boolean testPoint(Vector3fc point) {
        return this.testPoint(point.x(), point.y(), point.z());
    }

    public boolean testPoint(float x, float y, float z) {
        return this.nxX * x + this.nxY * y + this.nxZ * z + this.nxW >= 0.0f && this.pxX * x + this.pxY * y + this.pxZ * z + this.pxW >= 0.0f && this.nyX * x + this.nyY * y + this.nyZ * z + this.nyW >= 0.0f && this.pyX * x + this.pyY * y + this.pyZ * z + this.pyW >= 0.0f && this.nzX * x + this.nzY * y + this.nzZ * z + this.nzW >= 0.0f && this.pzX * x + this.pzY * y + this.pzZ * z + this.pzW >= 0.0f;
    }

    public boolean testSphere(Vector3fc center, float radius) {
        return this.testSphere(center.x(), center.y(), center.z(), radius);
    }

    public boolean testSphere(float x, float y, float z, float r) {
        return this.nxX * x + this.nxY * y + this.nxZ * z + this.nxW >= -r && this.pxX * x + this.pxY * y + this.pxZ * z + this.pxW >= -r && this.nyX * x + this.nyY * y + this.nyZ * z + this.nyW >= -r && this.pyX * x + this.pyY * y + this.pyZ * z + this.pyW >= -r && this.nzX * x + this.nzY * y + this.nzZ * z + this.nzW >= -r && this.pzX * x + this.pzY * y + this.pzZ * z + this.pzW >= -r;
    }

    public int intersectSphere(Vector3fc center, float radius) {
        return this.intersectSphere(center.x(), center.y(), center.z(), radius);
    }

    public int intersectSphere(float x, float y, float z, float r) {
        boolean inside = true;
        float dist = this.nxX * x + this.nxY * y + this.nxZ * z + this.nxW;
        if (dist >= -r) {
            inside &= dist >= r;
            dist = this.pxX * x + this.pxY * y + this.pxZ * z + this.pxW;
            if (dist >= -r) {
                inside &= dist >= r;
                dist = this.nyX * x + this.nyY * y + this.nyZ * z + this.nyW;
                if (dist >= -r) {
                    inside &= dist >= r;
                    dist = this.pyX * x + this.pyY * y + this.pyZ * z + this.pyW;
                    if (dist >= -r) {
                        inside &= dist >= r;
                        dist = this.nzX * x + this.nzY * y + this.nzZ * z + this.nzW;
                        if (dist >= -r) {
                            inside &= dist >= r;
                            dist = this.pzX * x + this.pzY * y + this.pzZ * z + this.pzW;
                            if (dist >= -r) {
                                return (inside &= dist >= r) ? -2 : -1;
                            }
                        }
                    }
                }
            }
        }
        return -3;
    }

    public boolean testAab(Vector3fc min, Vector3fc max) {
        return this.testAab(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean testAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        float f = this.nxX * (this.nxX < 0.0f ? minX : maxX) + this.nxY * (this.nxY < 0.0f ? minY : maxY);
        float f2 = this.nxZ < 0.0f ? minZ : maxZ;
        if (!(f + this.nxZ * f2 >= -this.nxW)) return false;
        float f3 = this.pxX * (this.pxX < 0.0f ? minX : maxX) + this.pxY * (this.pxY < 0.0f ? minY : maxY);
        float f4 = this.pxZ < 0.0f ? minZ : maxZ;
        if (!(f3 + this.pxZ * f4 >= -this.pxW)) return false;
        float f5 = this.nyX * (this.nyX < 0.0f ? minX : maxX) + this.nyY * (this.nyY < 0.0f ? minY : maxY);
        float f6 = this.nyZ < 0.0f ? minZ : maxZ;
        if (!(f5 + this.nyZ * f6 >= -this.nyW)) return false;
        float f7 = this.pyX * (this.pyX < 0.0f ? minX : maxX) + this.pyY * (this.pyY < 0.0f ? minY : maxY);
        float f8 = this.pyZ < 0.0f ? minZ : maxZ;
        if (!(f7 + this.pyZ * f8 >= -this.pyW)) return false;
        float f9 = this.nzX * (this.nzX < 0.0f ? minX : maxX) + this.nzY * (this.nzY < 0.0f ? minY : maxY);
        float f10 = this.nzZ < 0.0f ? minZ : maxZ;
        if (!(f9 + this.nzZ * f10 >= -this.nzW)) return false;
        float f11 = this.pzX * (this.pzX < 0.0f ? minX : maxX) + this.pzY * (this.pzY < 0.0f ? minY : maxY);
        float f12 = this.pzZ < 0.0f ? minZ : maxZ;
        if (!(f11 + this.pzZ * f12 >= -this.pzW)) return false;
        return true;
    }

    public boolean testPlaneXY(Vector2fc min, Vector2fc max) {
        return this.testPlaneXY(min.x(), min.y(), max.x(), max.y());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean testPlaneXY(float minX, float minY, float maxX, float maxY) {
        float f = this.nxX * (this.nxX < 0.0f ? minX : maxX);
        float f2 = this.nxY < 0.0f ? minY : maxY;
        if (!(f + this.nxY * f2 >= -this.nxW)) return false;
        float f3 = this.pxX * (this.pxX < 0.0f ? minX : maxX);
        float f4 = this.pxY < 0.0f ? minY : maxY;
        if (!(f3 + this.pxY * f4 >= -this.pxW)) return false;
        float f5 = this.nyX * (this.nyX < 0.0f ? minX : maxX);
        float f6 = this.nyY < 0.0f ? minY : maxY;
        if (!(f5 + this.nyY * f6 >= -this.nyW)) return false;
        float f7 = this.pyX * (this.pyX < 0.0f ? minX : maxX);
        float f8 = this.pyY < 0.0f ? minY : maxY;
        if (!(f7 + this.pyY * f8 >= -this.pyW)) return false;
        float f9 = this.nzX * (this.nzX < 0.0f ? minX : maxX);
        float f10 = this.nzY < 0.0f ? minY : maxY;
        if (!(f9 + this.nzY * f10 >= -this.nzW)) return false;
        float f11 = this.pzX * (this.pzX < 0.0f ? minX : maxX);
        float f12 = this.pzY < 0.0f ? minY : maxY;
        if (!(f11 + this.pzY * f12 >= -this.pzW)) return false;
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean testPlaneXZ(float minX, float minZ, float maxX, float maxZ) {
        float f = this.nxX * (this.nxX < 0.0f ? minX : maxX);
        float f2 = this.nxZ < 0.0f ? minZ : maxZ;
        if (!(f + this.nxZ * f2 >= -this.nxW)) return false;
        float f3 = this.pxX * (this.pxX < 0.0f ? minX : maxX);
        float f4 = this.pxZ < 0.0f ? minZ : maxZ;
        if (!(f3 + this.pxZ * f4 >= -this.pxW)) return false;
        float f5 = this.nyX * (this.nyX < 0.0f ? minX : maxX);
        float f6 = this.nyZ < 0.0f ? minZ : maxZ;
        if (!(f5 + this.nyZ * f6 >= -this.nyW)) return false;
        float f7 = this.pyX * (this.pyX < 0.0f ? minX : maxX);
        float f8 = this.pyZ < 0.0f ? minZ : maxZ;
        if (!(f7 + this.pyZ * f8 >= -this.pyW)) return false;
        float f9 = this.nzX * (this.nzX < 0.0f ? minX : maxX);
        float f10 = this.nzZ < 0.0f ? minZ : maxZ;
        if (!(f9 + this.nzZ * f10 >= -this.nzW)) return false;
        float f11 = this.pzX * (this.pzX < 0.0f ? minX : maxX);
        float f12 = this.pzZ < 0.0f ? minZ : maxZ;
        if (!(f11 + this.pzZ * f12 >= -this.pzW)) return false;
        return true;
    }

    public int intersectAab(Vector3fc min, Vector3fc max) {
        return this.intersectAab(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public int intersectAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        int plane = 0;
        boolean inside = true;
        float f = this.nxX * (this.nxX < 0.0f ? minX : maxX) + this.nxY * (this.nxY < 0.0f ? minY : maxY);
        float f2 = this.nxZ < 0.0f ? minZ : maxZ;
        if (f + this.nxZ * f2 >= -this.nxW) {
            plane = 1;
            inside &= this.nxX * (this.nxX < 0.0f ? maxX : minX) + this.nxY * (this.nxY < 0.0f ? maxY : minY) + this.nxZ * (this.nxZ < 0.0f ? maxZ : minZ) >= -this.nxW;
            float f3 = this.pxX * (this.pxX < 0.0f ? minX : maxX) + this.pxY * (this.pxY < 0.0f ? minY : maxY);
            float f4 = this.pxZ < 0.0f ? minZ : maxZ;
            if (f3 + this.pxZ * f4 >= -this.pxW) {
                plane = 2;
                inside &= this.pxX * (this.pxX < 0.0f ? maxX : minX) + this.pxY * (this.pxY < 0.0f ? maxY : minY) + this.pxZ * (this.pxZ < 0.0f ? maxZ : minZ) >= -this.pxW;
                float f5 = this.nyX * (this.nyX < 0.0f ? minX : maxX) + this.nyY * (this.nyY < 0.0f ? minY : maxY);
                float f6 = this.nyZ < 0.0f ? minZ : maxZ;
                if (f5 + this.nyZ * f6 >= -this.nyW) {
                    plane = 3;
                    inside &= this.nyX * (this.nyX < 0.0f ? maxX : minX) + this.nyY * (this.nyY < 0.0f ? maxY : minY) + this.nyZ * (this.nyZ < 0.0f ? maxZ : minZ) >= -this.nyW;
                    float f7 = this.pyX * (this.pyX < 0.0f ? minX : maxX) + this.pyY * (this.pyY < 0.0f ? minY : maxY);
                    float f8 = this.pyZ < 0.0f ? minZ : maxZ;
                    if (f7 + this.pyZ * f8 >= -this.pyW) {
                        plane = 4;
                        inside &= this.pyX * (this.pyX < 0.0f ? maxX : minX) + this.pyY * (this.pyY < 0.0f ? maxY : minY) + this.pyZ * (this.pyZ < 0.0f ? maxZ : minZ) >= -this.pyW;
                        float f9 = this.nzX * (this.nzX < 0.0f ? minX : maxX) + this.nzY * (this.nzY < 0.0f ? minY : maxY);
                        float f10 = this.nzZ < 0.0f ? minZ : maxZ;
                        if (f9 + this.nzZ * f10 >= -this.nzW) {
                            plane = 5;
                            inside &= this.nzX * (this.nzX < 0.0f ? maxX : minX) + this.nzY * (this.nzY < 0.0f ? maxY : minY) + this.nzZ * (this.nzZ < 0.0f ? maxZ : minZ) >= -this.nzW;
                            float f11 = this.pzX * (this.pzX < 0.0f ? minX : maxX) + this.pzY * (this.pzY < 0.0f ? minY : maxY);
                            float f12 = this.pzZ < 0.0f ? minZ : maxZ;
                            if (f11 + this.pzZ * f12 >= -this.pzW) {
                                return (inside &= this.pzX * (this.pzX < 0.0f ? maxX : minX) + this.pzY * (this.pzY < 0.0f ? maxY : minY) + this.pzZ * (this.pzZ < 0.0f ? maxZ : minZ) >= -this.pzW) ? -2 : -1;
                            }
                        }
                    }
                }
            }
        }
        return plane;
    }

    public float distanceToPlane(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int plane) {
        return this.planes[plane].x * (this.planes[plane].x < 0.0f ? maxX : minX) + this.planes[plane].y * (this.planes[plane].y < 0.0f ? maxY : minY) + this.planes[plane].z * (this.planes[plane].z < 0.0f ? maxZ : minZ) + this.planes[plane].w;
    }

    public int intersectAab(Vector3fc min, Vector3fc max, int mask) {
        return this.intersectAab(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), mask);
    }

    public int intersectAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int mask) {
        int plane;
        block3: {
            block8: {
                block7: {
                    block6: {
                        block5: {
                            block4: {
                                block2: {
                                    plane = 0;
                                    boolean inside = true;
                                    if ((mask & 1) == 0) break block2;
                                    float f = this.nxX * (this.nxX < 0.0f ? minX : maxX) + this.nxY * (this.nxY < 0.0f ? minY : maxY);
                                    float f2 = this.nxZ < 0.0f ? minZ : maxZ;
                                    if (!(f + this.nxZ * f2 >= -this.nxW)) break block3;
                                }
                                plane = 1;
                                inside &= this.nxX * (this.nxX < 0.0f ? maxX : minX) + this.nxY * (this.nxY < 0.0f ? maxY : minY) + this.nxZ * (this.nxZ < 0.0f ? maxZ : minZ) >= -this.nxW;
                                if ((mask & 2) == 0) break block4;
                                float f = this.pxX * (this.pxX < 0.0f ? minX : maxX) + this.pxY * (this.pxY < 0.0f ? minY : maxY);
                                float f3 = this.pxZ < 0.0f ? minZ : maxZ;
                                if (!(f + this.pxZ * f3 >= -this.pxW)) break block3;
                            }
                            plane = 2;
                            inside &= this.pxX * (this.pxX < 0.0f ? maxX : minX) + this.pxY * (this.pxY < 0.0f ? maxY : minY) + this.pxZ * (this.pxZ < 0.0f ? maxZ : minZ) >= -this.pxW;
                            if ((mask & 4) == 0) break block5;
                            float f = this.nyX * (this.nyX < 0.0f ? minX : maxX) + this.nyY * (this.nyY < 0.0f ? minY : maxY);
                            float f4 = this.nyZ < 0.0f ? minZ : maxZ;
                            if (!(f + this.nyZ * f4 >= -this.nyW)) break block3;
                        }
                        plane = 3;
                        inside &= this.nyX * (this.nyX < 0.0f ? maxX : minX) + this.nyY * (this.nyY < 0.0f ? maxY : minY) + this.nyZ * (this.nyZ < 0.0f ? maxZ : minZ) >= -this.nyW;
                        if ((mask & 8) == 0) break block6;
                        float f = this.pyX * (this.pyX < 0.0f ? minX : maxX) + this.pyY * (this.pyY < 0.0f ? minY : maxY);
                        float f5 = this.pyZ < 0.0f ? minZ : maxZ;
                        if (!(f + this.pyZ * f5 >= -this.pyW)) break block3;
                    }
                    plane = 4;
                    inside &= this.pyX * (this.pyX < 0.0f ? maxX : minX) + this.pyY * (this.pyY < 0.0f ? maxY : minY) + this.pyZ * (this.pyZ < 0.0f ? maxZ : minZ) >= -this.pyW;
                    if ((mask & 0x10) == 0) break block7;
                    float f = this.nzX * (this.nzX < 0.0f ? minX : maxX) + this.nzY * (this.nzY < 0.0f ? minY : maxY);
                    float f6 = this.nzZ < 0.0f ? minZ : maxZ;
                    if (!(f + this.nzZ * f6 >= -this.nzW)) break block3;
                }
                plane = 5;
                inside &= this.nzX * (this.nzX < 0.0f ? maxX : minX) + this.nzY * (this.nzY < 0.0f ? maxY : minY) + this.nzZ * (this.nzZ < 0.0f ? maxZ : minZ) >= -this.nzW;
                if ((mask & 0x20) == 0) break block8;
                float f = this.pzX * (this.pzX < 0.0f ? minX : maxX) + this.pzY * (this.pzY < 0.0f ? minY : maxY);
                float f7 = this.pzZ < 0.0f ? minZ : maxZ;
                if (!(f + this.pzZ * f7 >= -this.pzW)) break block3;
            }
            return (inside &= this.pzX * (this.pzX < 0.0f ? maxX : minX) + this.pzY * (this.pzY < 0.0f ? maxY : minY) + this.pzZ * (this.pzZ < 0.0f ? maxZ : minZ) >= -this.pzW) ? -2 : -1;
        }
        return plane;
    }

    public int intersectAab(Vector3fc min, Vector3fc max, int mask, int startPlane) {
        return this.intersectAab(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), mask, startPlane);
    }

    public int intersectAab(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int mask, int startPlane) {
        int plane;
        block6: {
            block11: {
                block10: {
                    block9: {
                        block8: {
                            block7: {
                                block5: {
                                    plane = startPlane;
                                    boolean inside = true;
                                    Vector4f p = this.planes[startPlane];
                                    if ((mask & 1 << startPlane) != 0) {
                                        float f = p.x * (p.x < 0.0f ? minX : maxX) + p.y * (p.y < 0.0f ? minY : maxY);
                                        float f2 = p.z;
                                        float f3 = p.z < 0.0f ? minZ : maxZ;
                                        if (f + f2 * f3 < -p.w) {
                                            return plane;
                                        }
                                    }
                                    if ((mask & 1) == 0) break block5;
                                    float f = this.nxX * (this.nxX < 0.0f ? minX : maxX) + this.nxY * (this.nxY < 0.0f ? minY : maxY);
                                    float f4 = this.nxZ < 0.0f ? minZ : maxZ;
                                    if (!(f + this.nxZ * f4 >= -this.nxW)) break block6;
                                }
                                plane = 1;
                                inside &= this.nxX * (this.nxX < 0.0f ? maxX : minX) + this.nxY * (this.nxY < 0.0f ? maxY : minY) + this.nxZ * (this.nxZ < 0.0f ? maxZ : minZ) >= -this.nxW;
                                if ((mask & 2) == 0) break block7;
                                float f = this.pxX * (this.pxX < 0.0f ? minX : maxX) + this.pxY * (this.pxY < 0.0f ? minY : maxY);
                                float f5 = this.pxZ < 0.0f ? minZ : maxZ;
                                if (!(f + this.pxZ * f5 >= -this.pxW)) break block6;
                            }
                            plane = 2;
                            inside &= this.pxX * (this.pxX < 0.0f ? maxX : minX) + this.pxY * (this.pxY < 0.0f ? maxY : minY) + this.pxZ * (this.pxZ < 0.0f ? maxZ : minZ) >= -this.pxW;
                            if ((mask & 4) == 0) break block8;
                            float f = this.nyX * (this.nyX < 0.0f ? minX : maxX) + this.nyY * (this.nyY < 0.0f ? minY : maxY);
                            float f6 = this.nyZ < 0.0f ? minZ : maxZ;
                            if (!(f + this.nyZ * f6 >= -this.nyW)) break block6;
                        }
                        plane = 3;
                        inside &= this.nyX * (this.nyX < 0.0f ? maxX : minX) + this.nyY * (this.nyY < 0.0f ? maxY : minY) + this.nyZ * (this.nyZ < 0.0f ? maxZ : minZ) >= -this.nyW;
                        if ((mask & 8) == 0) break block9;
                        float f = this.pyX * (this.pyX < 0.0f ? minX : maxX) + this.pyY * (this.pyY < 0.0f ? minY : maxY);
                        float f7 = this.pyZ < 0.0f ? minZ : maxZ;
                        if (!(f + this.pyZ * f7 >= -this.pyW)) break block6;
                    }
                    plane = 4;
                    inside &= this.pyX * (this.pyX < 0.0f ? maxX : minX) + this.pyY * (this.pyY < 0.0f ? maxY : minY) + this.pyZ * (this.pyZ < 0.0f ? maxZ : minZ) >= -this.pyW;
                    if ((mask & 0x10) == 0) break block10;
                    float f = this.nzX * (this.nzX < 0.0f ? minX : maxX) + this.nzY * (this.nzY < 0.0f ? minY : maxY);
                    float f8 = this.nzZ < 0.0f ? minZ : maxZ;
                    if (!(f + this.nzZ * f8 >= -this.nzW)) break block6;
                }
                plane = 5;
                inside &= this.nzX * (this.nzX < 0.0f ? maxX : minX) + this.nzY * (this.nzY < 0.0f ? maxY : minY) + this.nzZ * (this.nzZ < 0.0f ? maxZ : minZ) >= -this.nzW;
                if ((mask & 0x20) == 0) break block11;
                float f = this.pzX * (this.pzX < 0.0f ? minX : maxX) + this.pzY * (this.pzY < 0.0f ? minY : maxY);
                float f9 = this.pzZ < 0.0f ? minZ : maxZ;
                if (!(f + this.pzZ * f9 >= -this.pzW)) break block6;
            }
            return (inside &= this.pzX * (this.pzX < 0.0f ? maxX : minX) + this.pzY * (this.pzY < 0.0f ? maxY : minY) + this.pzZ * (this.pzZ < 0.0f ? maxZ : minZ) >= -this.pzW) ? -2 : -1;
        }
        return plane;
    }

    public boolean testLineSegment(Vector3fc a, Vector3fc b) {
        return this.testLineSegment(a.x(), a.y(), a.z(), b.x(), b.y(), b.z());
    }

    public boolean testLineSegment(float aX, float aY, float aZ, float bX, float bY, float bZ) {
        float dz;
        float dy;
        float dx;
        float p;
        float da = Math.fma(this.nxX, aX, Math.fma(this.nxY, aY, Math.fma(this.nxZ, aZ, this.nxW)));
        float db = Math.fma(this.nxX, bX, Math.fma(this.nxY, bY, Math.fma(this.nxZ, bZ, this.nxW)));
        if (da < 0.0f && db < 0.0f) {
            return false;
        }
        if (da * db < 0.0f) {
            p = Math.abs(da) / Math.abs(db - da);
            dx = Math.fma(bX - aX, p, aX);
            dy = Math.fma(bY - aY, p, aY);
            dz = Math.fma(bZ - aZ, p, aZ);
            if (da < 0.0f) {
                aX = dx;
                aY = dy;
                aZ = dz;
            } else {
                bX = dx;
                bY = dy;
                bZ = dz;
            }
        }
        da = Math.fma(this.pxX, aX, Math.fma(this.pxY, aY, Math.fma(this.pxZ, aZ, this.pxW)));
        db = Math.fma(this.pxX, bX, Math.fma(this.pxY, bY, Math.fma(this.pxZ, bZ, this.pxW)));
        if (da < 0.0f && db < 0.0f) {
            return false;
        }
        if (da * db < 0.0f) {
            p = Math.abs(da) / Math.abs(db - da);
            dx = Math.fma(bX - aX, p, aX);
            dy = Math.fma(bY - aY, p, aY);
            dz = Math.fma(bZ - aZ, p, aZ);
            if (da < 0.0f) {
                aX = dx;
                aY = dy;
                aZ = dz;
            } else {
                bX = dx;
                bY = dy;
                bZ = dz;
            }
        }
        da = Math.fma(this.nyX, aX, Math.fma(this.nyY, aY, Math.fma(this.nyZ, aZ, this.nyW)));
        db = Math.fma(this.nyX, bX, Math.fma(this.nyY, bY, Math.fma(this.nyZ, bZ, this.nyW)));
        if (da < 0.0f && db < 0.0f) {
            return false;
        }
        if (da * db < 0.0f) {
            p = Math.abs(da) / Math.abs(db - da);
            dx = Math.fma(bX - aX, p, aX);
            dy = Math.fma(bY - aY, p, aY);
            dz = Math.fma(bZ - aZ, p, aZ);
            if (da < 0.0f) {
                aX = dx;
                aY = dy;
                aZ = dz;
            } else {
                bX = dx;
                bY = dy;
                bZ = dz;
            }
        }
        da = Math.fma(this.pyX, aX, Math.fma(this.pyY, aY, Math.fma(this.pyZ, aZ, this.pyW)));
        db = Math.fma(this.pyX, bX, Math.fma(this.pyY, bY, Math.fma(this.pyZ, bZ, this.pyW)));
        if (da < 0.0f && db < 0.0f) {
            return false;
        }
        if (da * db < 0.0f) {
            p = Math.abs(da) / Math.abs(db - da);
            dx = Math.fma(bX - aX, p, aX);
            dy = Math.fma(bY - aY, p, aY);
            dz = Math.fma(bZ - aZ, p, aZ);
            if (da < 0.0f) {
                aX = dx;
                aY = dy;
                aZ = dz;
            } else {
                bX = dx;
                bY = dy;
                bZ = dz;
            }
        }
        da = Math.fma(this.nzX, aX, Math.fma(this.nzY, aY, Math.fma(this.nzZ, aZ, this.nzW)));
        db = Math.fma(this.nzX, bX, Math.fma(this.nzY, bY, Math.fma(this.nzZ, bZ, this.nzW)));
        if (da < 0.0f && db < 0.0f) {
            return false;
        }
        if (da * db < 0.0f) {
            p = Math.abs(da) / Math.abs(db - da);
            dx = Math.fma(bX - aX, p, aX);
            dy = Math.fma(bY - aY, p, aY);
            dz = Math.fma(bZ - aZ, p, aZ);
            if (da < 0.0f) {
                aX = dx;
                aY = dy;
                aZ = dz;
            } else {
                bX = dx;
                bY = dy;
                bZ = dz;
            }
        }
        da = Math.fma(this.pzX, aX, Math.fma(this.pzY, aY, Math.fma(this.pzZ, aZ, this.pzW)));
        db = Math.fma(this.pzX, bX, Math.fma(this.pzY, bY, Math.fma(this.pzZ, bZ, this.pzW)));
        return da >= 0.0f || db >= 0.0f;
    }
}

