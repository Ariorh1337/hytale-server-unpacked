/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jline.console.CommandInput;

public interface Printer {
    public static final String ALL = "all";
    public static final String COLUMNS = "columns";
    public static final String EXCLUDE = "exclude";
    public static final String INCLUDE = "include";
    public static final String INDENTION = "indention";
    public static final String MAX_COLUMN_WIDTH = "maxColumnWidth";
    public static final String MAX_DEPTH = "maxDepth";
    public static final String MAXROWS = "maxrows";
    public static final String ONE_ROW_TABLE = "oneRowTable";
    public static final String ROWNUM = "rownum";
    public static final String SHORT_NAMES = "shortNames";
    public static final String SKIP_DEFAULT_OPTIONS = "skipDefaultOptions";
    public static final String STRUCT_ON_TABLE = "structsOnTable";
    public static final String STYLE = "style";
    public static final String TO_STRING = "toString";
    public static final String VALUE_STYLE = "valueStyle";
    public static final String WIDTH = "width";
    public static final String BORDER = "border";
    public static final String ROW_HIGHLIGHT = "rowHighlight";
    public static final String COLUMNS_IN = "columnsIn";
    public static final String COLUMNS_OUT = "columnsOut";
    public static final String HIGHLIGHT_VALUE = "highlightValue";
    public static final String MAP_SIMILARITY = "mapSimilarity";
    public static final String OBJECT_TO_MAP = "objectToMap";
    public static final String OBJECT_TO_STRING = "objectToString";
    public static final String VALUE_STYLE_ALL = "valueStyleAll";
    public static final String MULTI_COLUMNS = "multiColumns";
    public static final List<String> BOOLEAN_KEYS = Arrays.asList("all", "oneRowTable", "rownum", "shortNames", "skipDefaultOptions", "structsOnTable", "toString", "valueStyleAll", "multiColumns");

    default public void println(Object object) {
        this.println(new HashMap<String, Object>(), object);
    }

    public void println(Map<String, Object> var1, Object var2);

    default public Exception prntCommand(CommandInput input) {
        return null;
    }

    public boolean refresh();

    public static enum TableRows {
        EVEN,
        ODD,
        ALL;

    }
}

