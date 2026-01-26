/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ipfilter;

import io.netty.handler.ipfilter.IpSubnetFilterRule;
import java.net.InetSocketAddress;
import java.util.Comparator;

final class IpSubnetFilterRuleComparator
implements Comparator<Object> {
    static final IpSubnetFilterRuleComparator INSTANCE = new IpSubnetFilterRuleComparator();

    private IpSubnetFilterRuleComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        return ((IpSubnetFilterRule)o1).compareTo((InetSocketAddress)o2);
    }
}

