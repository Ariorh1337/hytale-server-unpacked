/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.IpSubnetFilterRuleComparator;
import io.netty.util.internal.ObjectUtil;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ChannelHandler.Sharable
public class IpSubnetFilter
extends AbstractRemoteAddressFilter<InetSocketAddress> {
    private final boolean acceptIfNotFound;
    private final IpSubnetFilterRule[] ipv4Rules;
    private final IpSubnetFilterRule[] ipv6Rules;
    private final IpFilterRuleType ipFilterRuleTypeIPv4;
    private final IpFilterRuleType ipFilterRuleTypeIPv6;

    public IpSubnetFilter(IpSubnetFilterRule ... rules) {
        this(true, Arrays.asList(ObjectUtil.checkNotNull(rules, "rules")));
    }

    public IpSubnetFilter(boolean acceptIfNotFound, IpSubnetFilterRule ... rules) {
        this(acceptIfNotFound, Arrays.asList(ObjectUtil.checkNotNull(rules, "rules")));
    }

    public IpSubnetFilter(List<IpSubnetFilterRule> rules) {
        this(true, rules);
    }

    public IpSubnetFilter(boolean acceptIfNotFound, List<IpSubnetFilterRule> rules) {
        ObjectUtil.checkNotNull(rules, "rules");
        this.acceptIfNotFound = acceptIfNotFound;
        int numAcceptIPv4 = 0;
        int numRejectIPv4 = 0;
        int numAcceptIPv6 = 0;
        int numRejectIPv6 = 0;
        ArrayList<IpSubnetFilterRule> unsortedIPv4Rules = new ArrayList<IpSubnetFilterRule>();
        ArrayList<IpSubnetFilterRule> unsortedIPv6Rules = new ArrayList<IpSubnetFilterRule>();
        for (IpSubnetFilterRule ipSubnetFilterRule : rules) {
            ObjectUtil.checkNotNull(ipSubnetFilterRule, "rule");
            if (ipSubnetFilterRule.getFilterRule() instanceof IpSubnetFilterRule.Ip4SubnetFilterRule) {
                unsortedIPv4Rules.add(ipSubnetFilterRule);
                if (ipSubnetFilterRule.ruleType() == IpFilterRuleType.ACCEPT) {
                    ++numAcceptIPv4;
                    continue;
                }
                ++numRejectIPv4;
                continue;
            }
            unsortedIPv6Rules.add(ipSubnetFilterRule);
            if (ipSubnetFilterRule.ruleType() == IpFilterRuleType.ACCEPT) {
                ++numAcceptIPv6;
                continue;
            }
            ++numRejectIPv6;
        }
        this.ipFilterRuleTypeIPv4 = numAcceptIPv4 == 0 && numRejectIPv4 > 0 ? IpFilterRuleType.REJECT : (numAcceptIPv4 > 0 && numRejectIPv4 == 0 ? IpFilterRuleType.ACCEPT : null);
        this.ipFilterRuleTypeIPv6 = numAcceptIPv6 == 0 && numRejectIPv6 > 0 ? IpFilterRuleType.REJECT : (numAcceptIPv6 > 0 && numRejectIPv6 == 0 ? IpFilterRuleType.ACCEPT : null);
        this.ipv4Rules = unsortedIPv4Rules.isEmpty() ? null : IpSubnetFilter.sortAndFilter(unsortedIPv4Rules);
        this.ipv6Rules = unsortedIPv6Rules.isEmpty() ? null : IpSubnetFilter.sortAndFilter(unsortedIPv6Rules);
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {
        int indexOf;
        if (this.ipv4Rules != null && remoteAddress.getAddress() instanceof Inet4Address) {
            int indexOf2 = Arrays.binarySearch(this.ipv4Rules, remoteAddress, IpSubnetFilterRuleComparator.INSTANCE);
            if (indexOf2 >= 0) {
                if (this.ipFilterRuleTypeIPv4 == null) {
                    return this.ipv4Rules[indexOf2].ruleType() == IpFilterRuleType.ACCEPT;
                }
                return this.ipFilterRuleTypeIPv4 == IpFilterRuleType.ACCEPT;
            }
        } else if (this.ipv6Rules != null && (indexOf = Arrays.binarySearch(this.ipv6Rules, remoteAddress, IpSubnetFilterRuleComparator.INSTANCE)) >= 0) {
            if (this.ipFilterRuleTypeIPv6 == null) {
                return this.ipv6Rules[indexOf].ruleType() == IpFilterRuleType.ACCEPT;
            }
            return this.ipFilterRuleTypeIPv6 == IpFilterRuleType.ACCEPT;
        }
        return this.acceptIfNotFound;
    }

    private static IpSubnetFilterRule[] sortAndFilter(List<IpSubnetFilterRule> rules) {
        IpSubnetFilterRule parentRule;
        Collections.sort(rules);
        Iterator<IpSubnetFilterRule> iterator = rules.iterator();
        ArrayList<IpSubnetFilterRule> toKeep = new ArrayList<IpSubnetFilterRule>();
        IpSubnetFilterRule ipSubnetFilterRule = parentRule = iterator.hasNext() ? iterator.next() : null;
        if (parentRule != null) {
            toKeep.add(parentRule);
        }
        while (iterator.hasNext()) {
            IpSubnetFilterRule childRule = iterator.next();
            if (parentRule.matches(new InetSocketAddress(childRule.getIpAddress(), 1))) continue;
            toKeep.add(childRule);
            parentRule = childRule;
        }
        return toKeep.toArray(new IpSubnetFilterRule[0]);
    }
}

