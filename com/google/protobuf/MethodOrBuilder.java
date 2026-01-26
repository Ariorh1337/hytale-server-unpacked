/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Option;
import com.google.protobuf.OptionOrBuilder;
import com.google.protobuf.Syntax;
import java.util.List;

public interface MethodOrBuilder
extends MessageOrBuilder {
    public String getName();

    public ByteString getNameBytes();

    public String getRequestTypeUrl();

    public ByteString getRequestTypeUrlBytes();

    public boolean getRequestStreaming();

    public String getResponseTypeUrl();

    public ByteString getResponseTypeUrlBytes();

    public boolean getResponseStreaming();

    public List<Option> getOptionsList();

    public Option getOptions(int var1);

    public int getOptionsCount();

    public List<? extends OptionOrBuilder> getOptionsOrBuilderList();

    public OptionOrBuilder getOptionsOrBuilder(int var1);

    @Deprecated
    public int getSyntaxValue();

    @Deprecated
    public Syntax getSyntax();

    @Deprecated
    public String getEdition();

    @Deprecated
    public ByteString getEditionBytes();
}

