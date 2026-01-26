/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Method;
import com.google.protobuf.MethodOrBuilder;
import com.google.protobuf.Mixin;
import com.google.protobuf.MixinOrBuilder;
import com.google.protobuf.Option;
import com.google.protobuf.OptionOrBuilder;
import com.google.protobuf.SourceContext;
import com.google.protobuf.SourceContextOrBuilder;
import com.google.protobuf.Syntax;
import java.util.List;

public interface ApiOrBuilder
extends MessageOrBuilder {
    public String getName();

    public ByteString getNameBytes();

    public List<Method> getMethodsList();

    public Method getMethods(int var1);

    public int getMethodsCount();

    public List<? extends MethodOrBuilder> getMethodsOrBuilderList();

    public MethodOrBuilder getMethodsOrBuilder(int var1);

    public List<Option> getOptionsList();

    public Option getOptions(int var1);

    public int getOptionsCount();

    public List<? extends OptionOrBuilder> getOptionsOrBuilderList();

    public OptionOrBuilder getOptionsOrBuilder(int var1);

    public String getVersion();

    public ByteString getVersionBytes();

    public boolean hasSourceContext();

    public SourceContext getSourceContext();

    public SourceContextOrBuilder getSourceContextOrBuilder();

    public List<Mixin> getMixinsList();

    public Mixin getMixins(int var1);

    public int getMixinsCount();

    public List<? extends MixinOrBuilder> getMixinsOrBuilderList();

    public MixinOrBuilder getMixinsOrBuilder(int var1);

    public int getSyntaxValue();

    public Syntax getSyntax();

    public String getEdition();

    public ByteString getEditionBytes();
}

