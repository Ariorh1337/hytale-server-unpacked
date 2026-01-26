/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.EnumValue;
import com.google.protobuf.EnumValueOrBuilder;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Option;
import com.google.protobuf.OptionOrBuilder;
import com.google.protobuf.SourceContext;
import com.google.protobuf.SourceContextOrBuilder;
import com.google.protobuf.Syntax;
import java.util.List;

public interface EnumOrBuilder
extends MessageOrBuilder {
    public String getName();

    public ByteString getNameBytes();

    public List<EnumValue> getEnumvalueList();

    public EnumValue getEnumvalue(int var1);

    public int getEnumvalueCount();

    public List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList();

    public EnumValueOrBuilder getEnumvalueOrBuilder(int var1);

    public List<Option> getOptionsList();

    public Option getOptions(int var1);

    public int getOptionsCount();

    public List<? extends OptionOrBuilder> getOptionsOrBuilderList();

    public OptionOrBuilder getOptionsOrBuilder(int var1);

    public boolean hasSourceContext();

    public SourceContext getSourceContext();

    public SourceContextOrBuilder getSourceContextOrBuilder();

    public int getSyntaxValue();

    public Syntax getSyntax();

    public String getEdition();

    public ByteString getEditionBytes();
}

