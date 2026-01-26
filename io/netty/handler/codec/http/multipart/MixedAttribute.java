/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractMixedHttpData;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import java.io.IOException;
import java.nio.charset.Charset;

public class MixedAttribute
extends AbstractMixedHttpData<Attribute>
implements Attribute {
    public MixedAttribute(String name, long limitSize) {
        this(name, limitSize, HttpConstants.DEFAULT_CHARSET);
    }

    public MixedAttribute(String name, long definedSize, long limitSize) {
        this(name, definedSize, limitSize, HttpConstants.DEFAULT_CHARSET);
    }

    public MixedAttribute(String name, long limitSize, Charset charset) {
        this(name, limitSize, charset, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
    }

    public MixedAttribute(String name, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
        this(name, 0L, limitSize, charset, baseDir, deleteOnExit);
    }

    public MixedAttribute(String name, long definedSize, long limitSize, Charset charset) {
        this(name, definedSize, limitSize, charset, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
    }

    public MixedAttribute(String name, long definedSize, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
        super(limitSize, baseDir, deleteOnExit, new MemoryAttribute(name, definedSize, charset));
    }

    public MixedAttribute(String name, String value, long limitSize) {
        this(name, value, limitSize, HttpConstants.DEFAULT_CHARSET, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
    }

    public MixedAttribute(String name, String value, long limitSize, Charset charset) {
        this(name, value, limitSize, charset, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
    }

    private static Attribute makeInitialAttributeFromValue(String name, String value, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
        if ((long)value.length() > limitSize) {
            try {
                return new DiskAttribute(name, value, charset, baseDir, deleteOnExit);
            }
            catch (IOException e) {
                try {
                    return new MemoryAttribute(name, value, charset);
                }
                catch (IOException ignore) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        try {
            return new MemoryAttribute(name, value, charset);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MixedAttribute(String name, String value, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
        super(limitSize, baseDir, deleteOnExit, MixedAttribute.makeInitialAttributeFromValue(name, value, limitSize, charset, baseDir, deleteOnExit));
    }

    @Override
    public String getValue() throws IOException {
        return ((Attribute)this.wrapped).getValue();
    }

    @Override
    public void setValue(String value) throws IOException {
        ((Attribute)this.wrapped).setValue(value);
    }

    @Override
    Attribute makeDiskData() {
        DiskAttribute diskAttribute = new DiskAttribute(this.getName(), this.definedLength(), this.baseDir, this.deleteOnExit);
        diskAttribute.setMaxSize(this.getMaxSize());
        return diskAttribute;
    }

    @Override
    public Attribute copy() {
        return (Attribute)super.copy();
    }

    @Override
    public Attribute duplicate() {
        return (Attribute)super.duplicate();
    }

    @Override
    public Attribute replace(ByteBuf content) {
        return (Attribute)super.replace(content);
    }

    @Override
    public Attribute retain() {
        return (Attribute)super.retain();
    }

    @Override
    public Attribute retain(int increment) {
        return (Attribute)super.retain(increment);
    }

    @Override
    public Attribute retainedDuplicate() {
        return (Attribute)super.retainedDuplicate();
    }

    @Override
    public Attribute touch() {
        return (Attribute)super.touch();
    }

    @Override
    public Attribute touch(Object hint) {
        return (Attribute)super.touch(hint);
    }
}

