/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelConsumer;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelCoordinate;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrayVoxelSpace<T>
implements VoxelSpace<T> {
    protected final int sizeX;
    protected final int sizeY;
    protected final int sizeZ;
    @Nonnull
    protected final T[] contents;
    @Nonnull
    protected String name = "schematic";
    @Nullable
    protected T[] fastReset = null;
    protected VoxelCoordinate origin;

    public ArrayVoxelSpace(@Nonnull Bounds3i bounds) {
        this("", bounds.getSize().x, bounds.getSize().y, bounds.getSize().z, -bounds.min.x, bounds.min.y, -bounds.min.z);
    }

    public ArrayVoxelSpace(@Nonnull String name, int sizeX, int sizeY, int sizeZ, int originX, int originY, int originZ) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (sizeX < 1 || sizeY < 1 || sizeZ < 1) {
            throw new IllegalArgumentException("invalid size " + sizeX + " " + sizeY + " " + sizeZ);
        }
        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.contents = new Object[sizeX * sizeY * sizeZ];
        this.origin = new VoxelCoordinate(originX, originY, originZ);
    }

    public ArrayVoxelSpace(int sizeX, int sizeY, int sizeZ) {
        if (sizeX < 1 || sizeY < 1 || sizeZ < 1) {
            throw new IllegalArgumentException("invalid size " + sizeX + " " + sizeY + " " + sizeZ);
        }
        this.name = this.getClass().getName();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.contents = new Object[sizeX * sizeY * sizeZ];
        this.origin = new VoxelCoordinate(0, 0, 0);
    }

    public ArrayVoxelSpace(@Nonnull VoxelSpace<T> voxelSpace) {
        this(voxelSpace.getName(), voxelSpace.sizeX(), voxelSpace.sizeY(), voxelSpace.sizeZ(), voxelSpace.getOriginX(), voxelSpace.getOriginY(), voxelSpace.getOriginZ());
        voxelSpace.forEach((v, x, y, z) -> this.set(v, x, y, z));
    }

    public void setFastResetTo(T e) {
        this.fastReset = new Object[this.contents.length];
        for (int i = 0; i < this.fastReset.length; ++i) {
            this.fastReset[i] = e;
        }
    }

    public void disableFastReset() {
        this.fastReset = null;
    }

    public boolean hasFastReset() {
        return this.fastReset != null;
    }

    public void fastReset() {
        if (this.fastReset == null) {
            throw new IllegalStateException("no fast-reset");
        }
        System.arraycopy(this.fastReset, 0, this.contents, 0, this.fastReset.length);
    }

    @Override
    public int sizeX() {
        return this.sizeX;
    }

    @Override
    public int sizeY() {
        return this.sizeY;
    }

    @Override
    public int sizeZ() {
        return this.sizeZ;
    }

    @Override
    public void pasteFrom(@Nonnull VoxelSpace<T> source) {
        if (source == null) {
            throw new NullPointerException();
        }
        for (int x = source.minX(); x < source.maxX(); ++x) {
            for (int y = source.minY(); y < source.maxY(); ++y) {
                for (int z = source.minZ(); z < source.maxZ(); ++z) {
                    this.set(source.getContent(x, y, z), x, y, z);
                }
            }
        }
    }

    @Override
    public boolean set(T content, int x, int y, int z) {
        if (!this.isInsideSpace(x, y, z)) {
            return false;
        }
        this.contents[this.arrayIndex((int)(x + this.origin.x), (int)(y + this.origin.y), (int)(z + this.origin.z))] = content;
        return true;
    }

    @Override
    public boolean set(T content, @Nonnull Vector3i position) {
        return this.set(content, position.x, position.y, position.z);
    }

    @Override
    public void set(T content) {
        for (int x = this.minX(); x < this.maxX(); ++x) {
            for (int y = this.minY(); y < this.maxY(); ++y) {
                for (int z = this.minZ(); z < this.maxZ(); ++z) {
                    this.set(content, x, y, z);
                }
            }
        }
    }

    @Override
    public void setOrigin(int x, int y, int z) {
        this.origin.x = x;
        this.origin.y = y;
        this.origin.z = z;
    }

    @Override
    public T getContent(int x, int y, int z) {
        if (!this.isInsideSpace(x, y, z)) {
            throw new IndexOutOfBoundsException("Coordinates outside VoxelSpace: " + x + " " + y + " " + z + " constraints " + this.minX() + " -> " + this.maxX() + " " + this.minY() + " -> " + this.maxY() + " " + this.minZ() + " -> " + this.maxZ() + "\n" + this.toString());
        }
        return this.contents[this.arrayIndex(x + this.origin.x, y + this.origin.y, z + this.origin.z)];
    }

    @Override
    @Nullable
    public T getContent(@Nonnull Vector3i position) {
        return this.getContent(position.x, position.y, position.z);
    }

    @Override
    public boolean replace(T replacement, int x, int y, int z, @Nonnull Predicate<T> mask) {
        if (!this.isInsideSpace(x, y, z)) {
            throw new IllegalArgumentException("outside schematic");
        }
        if (!mask.test(this.getContent(x, y, z))) {
            return false;
        }
        this.set(replacement, x, y, z);
        return true;
    }

    public T[] toArray() {
        return (Object[])this.contents.clone();
    }

    @Nonnull
    VoxelCoordinate getOrigin() {
        return this.origin.clone();
    }

    @Override
    public int getOriginX() {
        return this.origin.x;
    }

    @Override
    public int getOriginY() {
        return this.origin.y;
    }

    @Override
    public int getOriginZ() {
        return this.origin.z;
    }

    @Override
    @Nonnull
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isInsideSpace(int x, int y, int z) {
        return x + this.origin.x >= 0 && x + this.origin.x < this.sizeX && y + this.origin.y >= 0 && y + this.origin.y < this.sizeY && z + this.origin.z >= 0 && z + this.origin.z < this.sizeZ;
    }

    @Override
    public boolean isInsideSpace(@Nonnull Vector3i position) {
        return this.isInsideSpace(position.x, position.y, position.z);
    }

    @Override
    public void forEach(@Nonnull VoxelConsumer<? super T> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        for (int x = this.minX(); x < this.maxX(); ++x) {
            for (int y = this.minY(); y < this.maxY(); ++y) {
                for (int z = this.minZ(); z < this.maxZ(); ++z) {
                    action.accept(this.getContent(x, y, z), x, y, z);
                }
            }
        }
    }

    @Override
    public int minX() {
        return -this.origin.x;
    }

    @Override
    public int maxX() {
        return this.sizeX - this.origin.x;
    }

    @Override
    public int minY() {
        return -this.origin.y;
    }

    @Override
    public int maxY() {
        return this.sizeY - this.origin.y;
    }

    @Override
    public int minZ() {
        return -this.origin.z;
    }

    @Override
    public int maxZ() {
        return this.sizeZ - this.origin.z;
    }

    @Nonnull
    public ArrayVoxelSpace<T> clone() {
        ArrayVoxelSpace clone = new ArrayVoxelSpace(this.name, this.sizeX, this.sizeY, this.sizeZ, this.origin.x, this.origin.y, this.origin.z);
        this.forEach((v, x, y, z) -> clone.set(v, x, y, z));
        return clone;
    }

    private int arrayIndex(int x, int y, int z) {
        return y + x * this.sizeY + z * this.sizeY * this.sizeX;
    }

    @Nonnull
    public String toString() {
        return "ArrayVoxelSpace{sizeX=" + this.sizeX + ", sizeY=" + this.sizeY + ", sizeZ=" + this.sizeZ + ", minX=" + this.minX() + ", minY=" + this.minY() + ", minZ=" + this.minZ() + ", maxX=" + this.maxX() + ", maxY=" + this.maxY() + ", maxZ=" + this.maxZ() + ", name='" + this.name + "', origin=" + String.valueOf(this.origin) + "}";
    }
}

