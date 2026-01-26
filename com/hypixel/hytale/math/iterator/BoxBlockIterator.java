/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.iterator;

import com.hypixel.hytale.math.iterator.BlockIterator;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public final class BoxBlockIterator {
    @Nonnull
    private static ThreadLocal<BoxIterationBuffer> THREAD_LOCAL_BUFFER = ThreadLocal.withInitial(BoxIterationBuffer::new);

    private BoxBlockIterator() {
        throw new UnsupportedOperationException("This is a utility class. Do not instantiate.");
    }

    public static BoxIterationBuffer getBuffer() {
        return THREAD_LOCAL_BUFFER.get();
    }

    public static boolean iterate(@Nonnull Box box, @Nonnull Vector3d position, @Nonnull Vector3d d, double maxDistance, @Nonnull BoxIterationConsumer consumer) {
        return BoxBlockIterator.iterate(box, position, d, maxDistance, consumer, BoxBlockIterator.getBuffer());
    }

    public static boolean iterate(@Nonnull Box box, @Nonnull Vector3d pos, @Nonnull Vector3d d, double maxDistance, @Nonnull BoxIterationConsumer consumer, @Nonnull BoxIterationBuffer buffer) {
        return BoxBlockIterator.iterate(box.min, box.max, pos, d, maxDistance, consumer, buffer);
    }

    public static boolean iterate(@Nonnull Box box, double px, double py, double pz, double dx, double dy, double dz, double maxDistance, @Nonnull BoxIterationConsumer consumer) {
        return BoxBlockIterator.iterate(box, px, py, pz, dx, dy, dz, maxDistance, consumer, BoxBlockIterator.getBuffer());
    }

    public static boolean iterate(@Nonnull Box box, double px, double py, double pz, double dx, double dy, double dz, double maxDistance, @Nonnull BoxIterationConsumer consumer, @Nonnull BoxIterationBuffer buffer) {
        return BoxBlockIterator.iterate(box.min, box.max, px, py, pz, dx, dy, dz, maxDistance, consumer, buffer);
    }

    public static boolean iterate(@Nonnull Vector3d min, @Nonnull Vector3d max, double px, double py, double pz, double dx, double dy, double dz, double maxDistance, @Nonnull BoxIterationConsumer consumer) {
        return BoxBlockIterator.iterate(min, max, px, py, pz, dx, dy, dz, maxDistance, consumer, BoxBlockIterator.getBuffer());
    }

    public static boolean iterate(@Nonnull Vector3d min, @Nonnull Vector3d max, double px, double py, double pz, double dx, double dy, double dz, double maxDistance, @Nonnull BoxIterationConsumer consumer, @Nonnull BoxIterationBuffer buffer) {
        return BoxBlockIterator.iterate(min.x, min.y, min.z, max.x, max.y, max.z, px, py, pz, dx, dy, dz, maxDistance, consumer, buffer);
    }

    public static boolean iterate(@Nonnull Vector3d min, @Nonnull Vector3d max, @Nonnull Vector3d pos, @Nonnull Vector3d d, double maxDistance, @Nonnull BoxIterationConsumer consumer) {
        return BoxBlockIterator.iterate(min, max, pos, d, maxDistance, consumer, BoxBlockIterator.getBuffer());
    }

    public static boolean iterate(@Nonnull Vector3d min, @Nonnull Vector3d max, @Nonnull Vector3d pos, @Nonnull Vector3d d, double maxDistance, @Nonnull BoxIterationConsumer consumer, @Nonnull BoxIterationBuffer buffer) {
        return BoxBlockIterator.iterate(min.x, min.y, min.z, max.x, max.y, max.z, pos.x, pos.y, pos.z, d.x, d.y, d.z, maxDistance, consumer, buffer);
    }

    public static boolean iterate(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double px, double py, double pz, double dx, double dy, double dz, double maxDistance, @Nonnull BoxIterationConsumer consumer) {
        return BoxBlockIterator.iterate(minX, minY, minZ, maxX, maxY, maxZ, px, py, pz, dx, dy, dz, maxDistance, consumer, BoxBlockIterator.getBuffer());
    }

    public static boolean iterate(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double px, double py, double pz, double dx, double dy, double dz, double maxDistance, @Nonnull BoxIterationConsumer consumer, @Nonnull BoxIterationBuffer buffer) {
        if (minX > maxX) {
            throw new IllegalArgumentException("minX is larger than maxX! Given: " + minX + " > " + maxX);
        }
        if (minY > maxY) {
            throw new IllegalArgumentException("minY is larger than maxY! Given: " + minY + " > " + maxY);
        }
        if (minZ > maxZ) {
            throw new IllegalArgumentException("minZ is larger than maxZ! Given: " + minZ + " > " + maxZ);
        }
        if (consumer == null) {
            throw new NullPointerException("consumer is null!");
        }
        if (buffer == null) {
            throw new NullPointerException("buffer is null!");
        }
        return BoxBlockIterator.iterate0(minX, minY, minZ, maxX, maxY, maxZ, px, py, pz, dx, dy, dz, maxDistance, consumer, buffer);
    }

    private static boolean iterate0(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double posX, double posY, double posZ, double dx, double dy, double dz, double maxDistance, BoxIterationConsumer consumer, @Nonnull BoxIterationBuffer buffer) {
        buffer.consumer = consumer;
        buffer.mx = maxX - minX;
        buffer.my = maxY - minY;
        buffer.mz = maxZ - minZ;
        buffer.signX = dx > 0.0 ? -1 : 1;
        buffer.signY = dy > 0.0 ? -1 : 1;
        buffer.signZ = dz > 0.0 ? -1 : 1;
        double bx = posX + (dx > 0.0 ? maxX : minX);
        double by = posY + (dy > 0.0 ? maxY : minY);
        double bz = posZ + (dz > 0.0 ? maxZ : minZ);
        buffer.posX = (long)bx;
        buffer.posY = (long)by;
        buffer.posZ = (long)bz;
        return BlockIterator.iterate(bx, by, bz, dx, dy, dz, maxDistance, (x, y, z, px, py, pz, qx, qy, qz, buf) -> {
            int tx = (int)MathUtil.fastCeil((buf.signX < 0 ? 1.0 - px : px) + buf.mx);
            int ty = (int)MathUtil.fastCeil((buf.signY < 0 ? 1.0 - py : py) + buf.my);
            int tz = (int)MathUtil.fastCeil((buf.signZ < 0 ? 1.0 - pz : pz) + buf.mz);
            if ((long)x != buf.posX) {
                for (int iy = 0; iy < ty; ++iy) {
                    for (int iz = 0; iz < tz; ++iz) {
                        if (buf.consumer.accept(x, (long)y + (long)iy * (long)buf.signY, (long)z + (long)iz * (long)buf.signZ)) continue;
                        return false;
                    }
                }
                buf.posX = x;
            }
            if ((long)y != buf.posY) {
                for (int iz = 0; iz < tz; ++iz) {
                    for (int ix = 0; ix < tx; ++ix) {
                        if (buf.consumer.accept((long)x + (long)ix * (long)buf.signX, y, (long)z + (long)iz * (long)buf.signZ)) continue;
                        return false;
                    }
                }
                buf.posY = y;
            }
            if ((long)z != buf.posZ) {
                for (int ix = 0; ix < tx; ++ix) {
                    for (int iy = 0; iy < ty; ++iy) {
                        if (buf.consumer.accept((long)x + (long)ix * (long)buf.signX, (long)y + (long)iy * (long)buf.signY, z)) continue;
                        return false;
                    }
                }
                buf.posZ = z;
            }
            return buf.consumer.next();
        }, buffer);
    }

    public static class BoxIterationBuffer {
        BoxIterationConsumer consumer;
        double mx;
        double my;
        double mz;
        int signX;
        int signY;
        int signZ;
        long posX;
        long posY;
        long posZ;
    }

    public static interface BoxIterationConsumer {
        public boolean next();

        public boolean accept(long var1, long var3, long var5);
    }
}

