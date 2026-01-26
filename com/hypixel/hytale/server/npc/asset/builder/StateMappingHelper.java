/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.StateStringValidator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StateMappingHelper {
    public static final String DEFAULT_STATE = "start";
    public static final String DEFAULT_SUB_STATE = "Default";
    public static final String DEFAULT_STATE_PARAMETER = "DefaultState";
    public static final String STATE_CHANGE_RESET_PARAMETER = "ResetOnStateChange";
    @Nullable
    private StateMap mainStateMap = new StateMap();
    private int[] allMainStates;
    @Nullable
    private Int2ObjectOpenHashMap<IStateMap> subStateMap = new Int2ObjectOpenHashMap();
    private int depth;
    @Nullable
    private ArrayDeque<StateDepth> currentParentState = new ArrayDeque();
    private boolean component = true;
    private boolean hasStateEvaluator;
    private boolean requiresStateEvaluator;
    private String defaultSubState;
    private String defaultComponentLocalState;
    private int defaultComponentLocalStateIndex;
    private boolean componentLocalStateAutoReset;
    private Object2IntOpenHashMap<String> componentImportStateMappings;
    private SingletonStateMap singletonDefaultStateMap;

    public int[] getAllMainStates() {
        return this.allMainStates;
    }

    public int getHighestSubStateIndex(int mainStateIndex) {
        return this.subStateMap.get(mainStateIndex).size() - 1;
    }

    public void getAndPutSensorIndex(String state, String subState, @Nonnull BiConsumer<Integer, Integer> setter) {
        this.currentParentState.push(new StateDepth(this.depth, state));
        this.getAndPutIndex(state, subState, setter, this.mainStateMap::getAndPutSensorIndex, (i, s) -> {
            IStateMap helper = this.initialiseDefaultSubStates((int)i);
            return helper.getAndPutSensorIndex((String)s);
        });
    }

    public void getAndPutSetterIndex(String state, String subState, @Nonnull BiConsumer<Integer, Integer> setter) {
        this.getAndPutIndex(state, subState, setter, this.mainStateMap::getAndPutSetterIndex, (i, s) -> {
            IStateMap helper = this.initialiseDefaultSubStates((int)i);
            return helper.getAndPutSetterIndex((String)s);
        });
    }

    public void getAndPutStateRequirerIndex(String state, String subState, @Nonnull BiConsumer<Integer, Integer> setter) {
        this.getAndPutIndex(state, subState, setter, this.mainStateMap::getAndPutRequirerIndex, (i, s) -> {
            IStateMap helper = this.initialiseDefaultSubStates((int)i);
            return helper.getAndPutRequirerIndex((String)s);
        });
    }

    private void getAndPutIndex(String state, @Nullable String subState, @Nonnull BiConsumer<Integer, Integer> setter, @Nonnull Function<String, Integer> mainStateFunction, @Nonnull BiFunction<Integer, String, Integer> subStateFunction) {
        Integer index = mainStateFunction.apply(state);
        if (subState == null) {
            setter.accept(index, -1);
            return;
        }
        Integer subStateIndex = subStateFunction.apply(index, subState);
        setter.accept(index, subStateIndex);
    }

    @Nonnull
    private IStateMap initialiseDefaultSubStates(int index) {
        return this.subStateMap.computeIfAbsent(index, v -> {
            StateMap map = new StateMap();
            map.getAndPutSensorIndex(this.defaultSubState);
            map.getAndPutSetterIndex(this.defaultSubState);
            return map;
        });
    }

    public void validate(String configName, @Nonnull List<String> errors) {
        this.mainStateMap.validate(configName, null, errors);
        this.subStateMap.forEach((i, v) -> v.validate(configName, this.mainStateMap.getStateName((int)i), errors));
        if (!this.hasStateEvaluator && this.requiresStateEvaluator) {
            errors.add(String.format("%s: Expects a state evaluator but does not have one defined", configName));
        }
    }

    public int getStateIndex(String state) {
        return this.mainStateMap.getStateIndex(state);
    }

    public int getSubStateIndex(int index, String subState) {
        return this.subStateMap.get(index).getStateIndex(subState);
    }

    public String getStateName(int index) {
        return this.mainStateMap.getStateName(index);
    }

    public String getSubStateName(int index, int subState) {
        return this.subStateMap.get(index).getStateName(subState);
    }

    @Nullable
    public String getCurrentParentState() {
        if (this.currentParentState.isEmpty()) {
            return null;
        }
        return this.currentParentState.peek().state;
    }

    public void increaseDepth() {
        ++this.depth;
    }

    public void decreaseDepth() {
        --this.depth;
        if (!this.currentParentState.isEmpty() && this.depth < this.currentParentState.peek().depth) {
            this.currentParentState.pop();
        }
    }

    public void setDefaultSubState(String subState) {
        this.defaultSubState = subState;
    }

    public String getDefaultSubState() {
        return this.defaultSubState;
    }

    public void setNotComponent() {
        this.mainStateMap.getAndPutSensorIndex(DEFAULT_STATE);
        this.mainStateMap.getAndPutSetterIndex(DEFAULT_STATE);
        this.component = false;
    }

    public boolean isComponent() {
        return this.component;
    }

    public boolean hasComponentStates() {
        return this.component && this.mainStateMap != null;
    }

    public void initialiseComponentState(@Nonnull BuilderSupport support) {
        support.setToNewComponent();
        support.addComponentLocalStateMachine(this.defaultComponentLocalStateIndex);
        if (this.componentLocalStateAutoReset) {
            support.setLocalStateMachineAutoReset();
        }
    }

    public void popComponentState(@Nonnull BuilderSupport support) {
        support.popComponent();
    }

    public void readComponentDefaultLocalState(@Nonnull JsonObject data) {
        JsonElement resetValue;
        String state = BuilderBase.readString(data, DEFAULT_STATE_PARAMETER, null);
        if (state != null) {
            StateStringValidator validator = StateStringValidator.get();
            if (!validator.test(state)) {
                throw new IllegalStateException(validator.errorMessage(state));
            }
            if (validator.hasMainState()) {
                throw new IllegalStateException(String.format("Default component local state must be defined with a '.' prefix: %s", validator.getMainState()));
            }
            this.defaultComponentLocalState = validator.getSubState();
            this.defaultComponentLocalStateIndex = this.mainStateMap.getAndPutSetterIndex(this.defaultComponentLocalState);
        }
        if ((resetValue = data.get(STATE_CHANGE_RESET_PARAMETER)) != null) {
            this.componentLocalStateAutoReset = BuilderBase.expectBooleanElement(resetValue, STATE_CHANGE_RESET_PARAMETER);
        }
    }

    public boolean hasDefaultLocalState() {
        return this.defaultComponentLocalState != null;
    }

    public String getDefaultLocalState() {
        return this.defaultComponentLocalState;
    }

    public void setComponentImportStateMappings(@Nonnull JsonArray states) {
        this.componentImportStateMappings = new Object2IntOpenHashMap();
        this.componentImportStateMappings.defaultReturnValue(Integer.MIN_VALUE);
        StateStringValidator validator = StateStringValidator.mainStateOnly();
        for (int i = 0; i < states.size(); ++i) {
            String string = states.get(i).getAsString();
            if (!validator.test(string)) {
                throw new IllegalStateException(validator.errorMessage(string));
            }
            this.getAndPutSensorIndex(validator.getMainState(), null, (m, s) -> {});
            this.componentImportStateMappings.put(validator.getMainState(), i);
        }
        this.componentImportStateMappings.trim();
    }

    public int getComponentImportStateIndex(String state) {
        return this.componentImportStateMappings == null ? Integer.MIN_VALUE : this.componentImportStateMappings.getInt(state);
    }

    public int importedStateCount() {
        return this.componentImportStateMappings == null ? 0 : this.componentImportStateMappings.size();
    }

    public void setRequiresStateEvaluator() {
        this.requiresStateEvaluator = true;
    }

    public void setHasStateEvaluator() {
        this.hasStateEvaluator = true;
    }

    public void optimise() {
        this.currentParentState = null;
        if (this.mainStateMap.isEmpty()) {
            this.mainStateMap = null;
            this.subStateMap = null;
            return;
        }
        ObjectIterator<Int2ObjectMap.Entry<IStateMap>> iterator = Int2ObjectMaps.fastIterator(this.subStateMap);
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry next = (Int2ObjectMap.Entry)iterator.next();
            IStateMap map = (IStateMap)next.getValue();
            if (map.size() == 1) {
                if (this.singletonDefaultStateMap == null) {
                    this.singletonDefaultStateMap = new SingletonStateMap(this.defaultSubState);
                }
                next.setValue(this.singletonDefaultStateMap);
                continue;
            }
            map.optimise();
        }
        this.subStateMap.trim();
        this.mainStateMap.optimise();
        this.allMainStates = this.mainStateMap.stateNameMap.keySet().toIntArray();
    }

    private static class StateMap
    implements IStateMap {
        private final Int2ObjectOpenHashMap<String> stateNameMap = new Int2ObjectOpenHashMap();
        @Nonnull
        private final Object2IntOpenHashMap<String> stateIndexMap;
        private int stateIndexSource;
        @Nullable
        private BitSet stateSensors = new BitSet();
        @Nullable
        private BitSet stateSetters = new BitSet();
        @Nullable
        private BitSet stateRequirers = new BitSet();

        private StateMap() {
            this.stateIndexMap = new Object2IntOpenHashMap();
            this.stateIndexMap.defaultReturnValue(Integer.MIN_VALUE);
        }

        private int getOrCreateIndex(String name) {
            int index = this.stateIndexMap.getInt(name);
            if (index == Integer.MIN_VALUE) {
                index = this.stateIndexSource++;
                this.stateIndexMap.put(name, index);
                this.stateNameMap.put(index, name);
            }
            return index;
        }

        @Override
        public int getAndPutSensorIndex(String state) {
            int index = this.getOrCreateIndex(state);
            this.stateSensors.set(index);
            return index;
        }

        @Override
        public int getAndPutSetterIndex(String targetState) {
            int index = this.getOrCreateIndex(targetState);
            this.stateSetters.set(index);
            return index;
        }

        @Override
        public int getAndPutRequirerIndex(String targetState) {
            int index = this.getOrCreateIndex(targetState);
            this.stateRequirers.set(index);
            return index;
        }

        @Override
        public int getStateIndex(String state) {
            Objects.requireNonNull(state, "State must not be null when fetching index");
            return this.stateIndexMap.getInt(state);
        }

        @Override
        public String getStateName(int index) {
            return this.stateNameMap.get(index);
        }

        @Override
        public void validate(String configName, @Nullable String parent, @Nonnull List<String> errors) {
            this.stateSetters.xor(this.stateSensors);
            if (this.stateSetters.cardinality() > 0) {
                errors.add(String.format("%s: State sensor or State setter action/motion exists without accompanying state/setter: %s%s", configName, parent != null ? parent + "." : "", this.stateNameMap.get(this.stateSetters.nextSetBit(0))));
            }
            this.stateRequirers.andNot(this.stateSensors);
            if (this.stateRequirers.cardinality() > 0) {
                errors.add(String.format("%s: State required by a parameter does not exist: %s%s", configName, parent != null ? parent + "." : "", this.stateNameMap.get(this.stateRequirers.nextSetBit(0))));
            }
        }

        @Override
        public boolean isEmpty() {
            return this.stateNameMap.isEmpty();
        }

        @Override
        public int size() {
            return this.stateNameMap.size();
        }

        @Override
        public void optimise() {
            this.stateSensors = null;
            this.stateSetters = null;
            this.stateRequirers = null;
            this.stateNameMap.trim();
            this.stateIndexMap.trim();
        }
    }

    private static interface IStateMap {
        public int getAndPutSensorIndex(String var1);

        public int getAndPutSetterIndex(String var1);

        public int getAndPutRequirerIndex(String var1);

        public int getStateIndex(String var1);

        public String getStateName(int var1);

        public void validate(String var1, String var2, List<String> var3);

        public boolean isEmpty();

        public int size();

        public void optimise();
    }

    private static class StateDepth {
        private final int depth;
        private final String state;

        private StateDepth(int depth, String state) {
            this.depth = depth;
            this.state = state;
        }
    }

    private static class SingletonStateMap
    implements IStateMap {
        private final String stateName;

        private SingletonStateMap(String name) {
            this.stateName = name;
        }

        @Override
        public int getAndPutSensorIndex(String state) {
            return 0;
        }

        @Override
        public int getAndPutSetterIndex(String targetState) {
            return 0;
        }

        @Override
        public int getAndPutRequirerIndex(String targetState) {
            return 0;
        }

        @Override
        public int getStateIndex(@Nonnull String state) {
            if (!state.equals(this.stateName)) {
                return Integer.MIN_VALUE;
            }
            return 0;
        }

        @Override
        public String getStateName(int index) {
            return this.stateName;
        }

        @Override
        public void validate(String configName, String parent, List<String> errors) {
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void optimise() {
        }
    }
}

