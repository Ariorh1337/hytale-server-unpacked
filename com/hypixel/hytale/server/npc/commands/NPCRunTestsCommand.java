/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.commands;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.group.EntityGroup;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCRunTestsCommand
extends AbstractPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_NPC_RUN_TESTS_SPECIFY_ROLES = Message.translation("server.commands.npc.runtests.specifyroles");
    @Nonnull
    private final OptionalArg<String> rolesArg = this.withOptionalArg("roles", "server.commands.npc.runtests.roles.desc", ArgTypes.STRING);
    @Nonnull
    private final FlagArg presetArg = this.withFlagArg("preset", "server.commands.npc.runtests.preset.desc");
    @Nonnull
    private final FlagArg passArg = this.withFlagArg("pass", "server.commands.npc.runtests.pass.desc");
    @Nonnull
    private final FlagArg failArg = this.withFlagArg("fail", "server.commands.npc.runtests.fail.desc");
    @Nonnull
    private final FlagArg abortArg = this.withFlagArg("abort", "server.commands.npc.runtests.abort.desc");

    public NPCRunTestsCommand() {
        super("runtests", "server.commands.npc.runtests.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String[] roles;
        NPCTestData testDataComponent = store.ensureAndGetComponent(ref, NPCTestData.getComponentType());
        TransformComponent playerTransformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert (playerTransformComponent != null);
        if (!testDataComponent.npcRoles.isEmpty()) {
            if (((Boolean)this.passArg.get(context)).booleanValue()) {
                NPCRunTestsCommand.setNextRole(testDataComponent, ref, store, world);
                return;
            }
            if (((Boolean)this.failArg.get(context)).booleanValue()) {
                testDataComponent.failedRoles.add(testDataComponent.npcRoles.getInt(testDataComponent.index));
                NPCRunTestsCommand.setNextRole(testDataComponent, ref, store, world);
                return;
            }
            if (((Boolean)this.abortArg.get(context)).booleanValue()) {
                NPCRunTestsCommand.reportResults(ref, testDataComponent, store);
                Ref<EntityStore> npcRef = world.getEntityRef(testDataComponent.targetUUID);
                if (npcRef != null) {
                    NPCRunTestsCommand.cleanupNPC(npcRef, store);
                }
                store.removeComponent(ref, NPCTestData.getComponentType());
                return;
            }
        }
        if (((Boolean)this.presetArg.get(context)).booleanValue()) {
            roles = NPCPlugin.get().getPresetCoverageTestNPCs();
        } else {
            if (!this.rolesArg.provided(context)) {
                context.sendMessage(MESSAGE_COMMANDS_NPC_RUN_TESTS_SPECIFY_ROLES);
                store.removeComponent(ref, NPCTestData.getComponentType());
                return;
            }
            String roleString = (String)this.rolesArg.get(context);
            if (roleString == null || roleString.isEmpty()) {
                context.sendMessage(MESSAGE_COMMANDS_NPC_RUN_TESTS_SPECIFY_ROLES);
                store.removeComponent(ref, NPCTestData.getComponentType());
                return;
            }
            roles = roleString.split(",");
        }
        for (String role : roles) {
            int flockSize;
            block15: {
                try {
                    int idx = role.indexOf(35);
                    int n = flockSize = idx < 0 ? 1 : Integer.parseInt(role.substring(idx + 1));
                    if (idx <= 0) break block15;
                    role = role.substring(0, idx);
                }
                catch (NumberFormatException e) {
                    context.sendMessage(Message.translation("server.commands.npc.runtests.invalidflocksize").param("role", role));
                    continue;
                }
            }
            int builderIndex = NPCPlugin.get().getIndex(role);
            if (builderIndex == Integer.MIN_VALUE) {
                context.sendMessage(Message.translation("server.commands.npc.spawn.templateNotFound").param("template", role));
                continue;
            }
            testDataComponent.npcRoles.add(builderIndex);
            testDataComponent.flockSizes.add(flockSize);
        }
        if (testDataComponent.targetUUID == null) {
            NPCRunTestsCommand.spawnNPC(ref, testDataComponent, 0, playerTransformComponent.getPosition(), playerTransformComponent.getRotation(), store);
        }
    }

    private static void setNextRole(@Nonnull NPCTestData testData, @Nonnull Ref<EntityStore> reference, @Nonnull Store<EntityStore> store, @Nonnull World world) {
        Vector3f rotation;
        Vector3d position;
        Ref<EntityStore> npcReference = world.getEntityRef(testData.targetUUID);
        ++testData.index;
        if (testData.index >= testData.npcRoles.size()) {
            NPCRunTestsCommand.reportResults(reference, testData, store);
            if (npcReference != null) {
                NPCRunTestsCommand.cleanupNPC(npcReference, store);
            }
            store.removeComponent(reference, NPCTestData.getComponentType());
            return;
        }
        if (npcReference != null) {
            TransformComponent npcTransformComponent = store.getComponent(npcReference, TransformComponent.getComponentType());
            assert (npcTransformComponent != null);
            position = npcTransformComponent.getPosition();
            rotation = npcTransformComponent.getRotation();
            NPCRunTestsCommand.cleanupNPC(npcReference, store);
        } else {
            TransformComponent transformComponent = store.getComponent(reference, TransformComponent.getComponentType());
            assert (transformComponent != null);
            position = transformComponent.getPosition();
            rotation = transformComponent.getRotation();
        }
        NPCRunTestsCommand.spawnNPC(reference, testData, testData.index, position, rotation, store);
    }

    private static void cleanupNPC(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        Ref<EntityStore> flockReference = FlockPlugin.getFlockReference(ref, store);
        if (flockReference != null) {
            ObjectArrayList members = new ObjectArrayList();
            EntityGroup entityGroupComponent = store.getComponent(flockReference, EntityGroup.getComponentType());
            assert (entityGroupComponent != null);
            entityGroupComponent.forEachMember((index, member, list) -> list.add(member), members);
            for (Ref member2 : members) {
                store.removeEntity(member2, RemoveReason.REMOVE);
            }
        }
        store.removeEntity(ref, RemoveReason.REMOVE);
    }

    private static void spawnNPC(@Nonnull Ref<EntityStore> playerReference, @Nonnull NPCTestData testData, int index, @Nonnull Vector3d position, @Nullable Vector3f rotation, @Nonnull Store<EntityStore> store) {
        Pair<Ref<EntityStore>, NPCEntity> npcPair = NPCPlugin.get().spawnEntity(store, testData.npcRoles.getInt(index), position, rotation, null, null);
        Ref<EntityStore> npcRef = npcPair.first();
        NPCEntity npcComponent = npcPair.second();
        int flockSize = testData.flockSizes.getInt(index);
        if (flockSize > 1) {
            TransformComponent npcTransformComponent = store.getComponent(npcRef, TransformComponent.getComponentType());
            assert (npcTransformComponent != null);
            FlockPlugin.trySpawnFlock(npcRef, npcComponent, store, npcComponent.getRoleIndex(), npcTransformComponent.getPosition(), npcTransformComponent.getRotation(), flockSize, null);
        }
        String roleName = npcComponent.getRoleName();
        PlayerRef playerRefComponent = store.getComponent(playerReference, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        playerRefComponent.sendMessage(Message.translation("server.commands.npc.runtests.testing").param("role", roleName).insert("\n").insert(Message.translation("server.npc.tests." + roleName)));
        UUIDComponent npcUUIDComponent = store.getComponent(npcRef, UUIDComponent.getComponentType());
        assert (npcUUIDComponent != null);
        testData.targetUUID = npcUUIDComponent.getUuid();
    }

    private static void reportResults(@Nonnull Ref<EntityStore> playerReference, @Nonnull NPCTestData testData, @Nonnull Store<EntityStore> store) {
        NPCPlugin npcPlugin = NPCPlugin.get();
        Message msg = Message.translation("server.commands.npc.runtests.results");
        for (int i = 0; i < testData.npcRoles.size(); ++i) {
            int index = testData.npcRoles.getInt(i);
            msg.insert("  " + npcPlugin.getName(index) + ": ");
            String result = i >= testData.index ? "server.commands.npc.runtests.notrun" : (testData.failedRoles.contains(index) ? "server.commands.npc.runtests.fail" : "server.commands.npc.runtests.pass");
            msg.insert(Message.translation(result));
            msg.insert("\n");
        }
        PlayerRef playerRef = store.getComponent(playerReference, PlayerRef.getComponentType());
        assert (playerRef != null);
        playerRef.sendMessage(msg);
        npcPlugin.getLogger().at(Level.INFO).log(msg.getRawText());
    }

    public static class NPCTestData
    implements Component<EntityStore> {
        private final IntList npcRoles = new IntArrayList();
        private final IntList flockSizes = new IntArrayList();
        private final IntSet failedRoles = new IntOpenHashSet();
        private int index;
        private UUID targetUUID;

        public static ComponentType<EntityStore, NPCTestData> getComponentType() {
            return NPCPlugin.get().getNpcTestDataComponentType();
        }

        @Override
        @Nonnull
        public Component<EntityStore> clone() {
            NPCTestData data = new NPCTestData();
            data.npcRoles.addAll(this.npcRoles);
            data.index = this.index;
            data.failedRoles.addAll(this.failedRoles);
            return data;
        }
    }
}

