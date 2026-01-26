/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.navigation;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.navigation.AStarBase;
import com.hypixel.hytale.server.npc.navigation.AStarNode;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AStarDebugBase {
    public static final char CENTER = ' ';
    public static final char CROSS = '\u253c';
    public static final char HLINE = '\u2500';
    public static final char VLINE = '\u2502';
    public static final char OPEN_NODE = '\u25c6';
    public static final char CLOSED_NODE = '\u25c7';
    public static final char CLOSED_PATH_NODE = '\u25ef';
    public static final char OPEN_PATH_NODE = '\u25c9';
    public static final char BLOCKED_NODE = '\u00d7';
    public static final char START_POSITION = '@';
    public static final char END_POSITION = '\u03a9';
    public static final String BORDER_PATTERN = "\u2500\u253c".repeat(1025);
    public static final String CENTER_PATTERN = " \u2502".repeat(1025);
    protected AStarBase aStarBase;
    protected HytaleLogger logger;
    protected HytaleLogger.Api loggerInfo;

    public AStarDebugBase(AStarBase base, @Nonnull HytaleLogger logger) {
        this.aStarBase = base;
        this.logger = logger;
        this.loggerInfo = logger.at(Level.INFO);
    }

    public void dumpOpens(MotionController controller) {
        int i;
        int openCount = this.aStarBase.getOpenCount();
        List<AStarNode> openNodes = this.aStarBase.getOpenNodes();
        int maxLength = -1;
        for (i = 0; i < openCount; ++i) {
            int length = openNodes.get(i).getLength();
            if (length <= maxLength) continue;
            maxLength = length;
        }
        this.loggerInfo.log("== A* iter=%s opens=%s total=%s maxLength=%s %s", this.aStarBase.getIterations(), openCount, this.aStarBase.getVisitedBlocks().size(), maxLength, this.getExtraLogString(controller));
        for (i = 0; i < openCount; ++i) {
            this.loggerInfo.log("%2d %s", i, (Object)openNodes.get(i).toString());
        }
    }

    public void dumpPath() {
        this.loggerInfo.log("== A* Path iter=%s opens=%s total=%s", this.aStarBase.getIterations(), this.aStarBase.getOpenCount(), this.aStarBase.getVisitedBlocks().size());
        for (AStarNode node = this.aStarBase.getPath(); node != null; node = node.getNextPathNode()) {
            this.loggerInfo.log("%s", node.toString());
        }
    }

    public void dumpMap(boolean drawPath, MotionController controller) {
        int openCount = this.aStarBase.getOpenCount();
        List<AStarNode> openNodes = this.aStarBase.getOpenNodes();
        AStarNode start = null;
        boolean finalPath = false;
        if (drawPath) {
            AStarNode path = this.aStarBase.getPath();
            if (path != null) {
                start = path;
                finalPath = true;
            } else if (openCount > 0) {
                start = openNodes.get(openCount - 1);
            }
        }
        this.dumpMap(start, finalPath, controller);
    }

    public void dumpMap(@Nullable AStarNode pathNode, boolean isFinalPath, MotionController controller) {
        int maxLength;
        int maxZ;
        int minZ;
        int maxX;
        int minX;
        int e;
        long startPositionIndex = this.aStarBase.getStartPositionIndex();
        Long2ObjectMap<AStarNode> visitedBlocks = this.aStarBase.getVisitedBlocks();
        int s = AStarBase.xFromIndex(startPositionIndex);
        if (s < (e = this.getDumpMapRegionX(s))) {
            minX = s;
            maxX = e;
        } else {
            minX = e;
            maxX = s;
        }
        s = AStarBase.zFromIndex(startPositionIndex);
        e = this.getDumpMapRegionZ(s);
        if (s < e) {
            minZ = s;
            maxZ = e;
        } else {
            minZ = e;
            maxZ = s;
        }
        ObjectIterator<Long2ObjectMap.Entry<AStarNode>> fastIterator = Long2ObjectMaps.fastIterator(visitedBlocks);
        while (fastIterator.hasNext()) {
            AStarNode node = (AStarNode)((Long2ObjectMap.Entry)fastIterator.next()).getValue();
            int x = AStarBase.xFromIndex(node.getPositionIndex());
            int z = AStarBase.zFromIndex(node.getPositionIndex());
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (z <= maxZ) continue;
            maxZ = z;
        }
        int rows = maxZ - minZ + 1;
        int columns = maxX - minX + 1;
        int offset = minX & 1;
        boolean evenStart = (minZ & 1) == 0;
        String first = "'" + (evenStart ? CENTER_PATTERN : BORDER_PATTERN).substring(offset, offset + columns) + "'";
        String second = "'" + (evenStart ? BORDER_PATTERN : CENTER_PATTERN).substring(offset, offset + columns) + "'";
        StringBuilder[] map = new StringBuilder[rows];
        for (int i = 0; i < rows; i += 2) {
            map[i] = new StringBuilder(first);
            if (i + 1 >= rows) continue;
            map[i + 1] = new StringBuilder(second);
        }
        fastIterator = Long2ObjectMaps.fastIterator(visitedBlocks);
        while (fastIterator.hasNext()) {
            AStarNode node = (AStarNode)((Long2ObjectMap.Entry)fastIterator.next()).getValue();
            this.plot(node.getPositionIndex(), node.isInvalid() ? (char)'\u00d7' : (node.isOpen() ? (char)'\u25c6' : '\u25c7'), map, minX, minZ);
        }
        int openCount = this.aStarBase.getOpenCount();
        if (pathNode != null) {
            maxLength = pathNode.getLength();
            while (pathNode != null) {
                this.plot(pathNode.getPositionIndex(), pathNode.isOpen() ? (char)'\u25c9' : '\u25ef', map, minX, minZ);
                pathNode = isFinalPath ? pathNode.getNextPathNode() : pathNode.getPredecessor();
            }
        } else {
            int pos;
            List<AStarNode> openNodes = this.aStarBase.getOpenNodes();
            int index = openCount;
            maxLength = 0;
            while (--index >= 0 && (pos = openCount - index) <= 51) {
                this.plot(openNodes.get(index).getPositionIndex(), (char)(pos >= 26 ? pos - 26 + 97 : pos + 65), map, minX, minZ);
            }
        }
        this.plot(startPositionIndex, '@', map, minX, minZ);
        this.drawMapFinish(map, minX, minZ);
        this.loggerInfo.log("== A* iter=%s, opens=%s total=%s maxLength=%s %s", this.aStarBase.getIterations(), openCount, visitedBlocks.size(), maxLength, this.getExtraLogString(controller));
        for (StringBuilder stringBuilder : map) {
            this.loggerInfo.log(stringBuilder.toString());
        }
    }

    protected void plot(long positionIndex, char character, @Nonnull StringBuilder[] map, int minX, int minZ) {
        int row = AStarBase.zFromIndex(positionIndex) - minZ;
        int column = AStarBase.xFromIndex(positionIndex) - minX + 1;
        map[row].setCharAt(column, character);
    }

    protected void drawMapFinish(StringBuilder[] map, int minX, int minZ) {
    }

    protected int getDumpMapRegionZ(int def) {
        return def;
    }

    protected int getDumpMapRegionX(int def) {
        return def;
    }

    @Nonnull
    protected String getExtraLogString(MotionController controller) {
        return "";
    }
}

