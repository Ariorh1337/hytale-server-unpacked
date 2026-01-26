/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.HytaleType;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AccumulationMode;
import com.hypixel.hytale.protocol.ChangeStatBehaviour;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.EasingType;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InitialVelocity;
import com.hypixel.hytale.protocol.IntersectionHighlight;
import com.hypixel.hytale.protocol.ItemAnimation;
import com.hypixel.hytale.protocol.RailConfig;
import com.hypixel.hytale.protocol.RailPoint;
import com.hypixel.hytale.protocol.Range;
import com.hypixel.hytale.protocol.RangeVector2f;
import com.hypixel.hytale.protocol.RangeVector3f;
import com.hypixel.hytale.protocol.Rangeb;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.Size;
import com.hypixel.hytale.protocol.UVMotion;
import com.hypixel.hytale.protocol.UVMotionCurveType;
import com.hypixel.hytale.protocol.Vector2f;
import com.hypixel.hytale.protocol.packets.worldmap.ContextMenuItem;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.common.BlockyAnimationCache;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.codec.protocol.ColorAlphaCodec;
import com.hypixel.hytale.server.core.codec.protocol.ColorCodec;
import com.hypixel.hytale.server.core.util.PositionUtil;

public final class ProtocolCodecs {
    public static final BuilderCodec<Direction> DIRECTION = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Direction.class, Direction::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Float>("Yaw", Codec.FLOAT), (o, i) -> {
        o.yaw = i.floatValue();
    }, o -> Float.valueOf(o.yaw), (o, p) -> {
        o.yaw = p.yaw;
    }).add()).appendInherited(new KeyedCodec<Float>("Pitch", Codec.FLOAT), (o, i) -> {
        o.pitch = i.floatValue();
    }, o -> Float.valueOf(o.pitch), (o, p) -> {
        o.pitch = p.pitch;
    }).add()).appendInherited(new KeyedCodec<Float>("Roll", Codec.FLOAT), (o, i) -> {
        o.roll = i.floatValue();
    }, o -> Float.valueOf(o.roll), (o, p) -> {
        o.roll = p.roll;
    }).add()).build();
    public static final BuilderCodec<Vector2f> VECTOR2F = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector2f.class, Vector2f::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Float>("X", Codec.FLOAT), (o, i) -> {
        o.x = i.floatValue();
    }, o -> Float.valueOf(o.x), (o, p) -> {
        o.x = p.x;
    }).add()).appendInherited(new KeyedCodec<Float>("Y", Codec.FLOAT), (o, i) -> {
        o.y = i.floatValue();
    }, o -> Float.valueOf(o.y), (o, p) -> {
        o.y = p.y;
    }).add()).build();
    public static final BuilderCodec<com.hypixel.hytale.protocol.Vector3f> VECTOR3F = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(com.hypixel.hytale.protocol.Vector3f.class, com.hypixel.hytale.protocol.Vector3f::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Float>("X", Codec.FLOAT), (o, i) -> {
        o.x = i.floatValue();
    }, o -> Float.valueOf(o.x), (o, p) -> {
        o.x = p.x;
    }).add()).appendInherited(new KeyedCodec<Float>("Y", Codec.FLOAT), (o, i) -> {
        o.y = i.floatValue();
    }, o -> Float.valueOf(o.y), (o, p) -> {
        o.y = p.y;
    }).add()).appendInherited(new KeyedCodec<Float>("Z", Codec.FLOAT), (o, i) -> {
        o.z = i.floatValue();
    }, o -> Float.valueOf(o.z), (o, p) -> {
        o.z = p.z;
    }).add()).build();
    public static final BuilderCodec<ColorLight> COLOR_LIGHT = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ColorLight.class, ColorLight::new).appendInherited(new KeyedCodec<String>("Color", Codec.STRING), ColorParseUtil::hexStringToColorLightDirect, ColorParseUtil::colorLightToHexString, (o, p) -> {
        o.red = p.red;
        o.green = p.green;
        o.blue = p.blue;
    }).metadata(new HytaleType("ColorShort")).add()).appendInherited(new KeyedCodec<Byte>("Radius", Codec.BYTE), (o, i) -> {
        o.radius = i;
    }, o -> o.radius, (o, p) -> {
        o.radius = p.radius;
    }).add()).build();
    public static final ColorCodec COLOR = new ColorCodec();
    public static final ArrayCodec<Color> COLOR_ARRAY = new ArrayCodec<Color>(COLOR, Color[]::new);
    public static final ColorAlphaCodec COLOR_AlPHA = new ColorAlphaCodec();
    public static final EnumCodec<GameMode> GAMEMODE = new EnumCodec<GameMode>(GameMode.class).documentKey(GameMode.Creative, "Makes the player invulnerable and grants them the ability to fly.").documentKey(GameMode.Adventure, "The normal gamemode for players playing the game.");
    public static final EnumCodec<GameMode> GAMEMODE_LEGACY = new EnumCodec<GameMode>(GameMode.class, EnumCodec.EnumStyle.LEGACY);
    public static final BuilderCodec<Size> SIZE = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Size.class, Size::new).metadata(UIDisplayMode.COMPACT)).addField(new KeyedCodec<Integer>("Width", Codec.INTEGER), (size, i) -> {
        size.width = i;
    }, size -> size.width)).addField(new KeyedCodec<Integer>("Height", Codec.INTEGER), (size, i) -> {
        size.height = i;
    }, size -> size.height)).build();
    public static final BuilderCodec<Range> RANGE = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Range.class, Range::new).metadata(UIDisplayMode.COMPACT)).addField(new KeyedCodec<Integer>("Min", Codec.INTEGER), (rangeb, i) -> {
        rangeb.min = i;
    }, rangeb -> rangeb.min)).addField(new KeyedCodec<Integer>("Max", Codec.INTEGER), (rangeb, i) -> {
        rangeb.max = i;
    }, rangeb -> rangeb.max)).build();
    public static final BuilderCodec<Rangeb> RANGEB = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Rangeb.class, Rangeb::new).metadata(UIDisplayMode.COMPACT)).addField(new KeyedCodec<Byte>("Min", Codec.BYTE), (rangeb, i) -> {
        rangeb.min = i;
    }, rangeb -> rangeb.min)).addField(new KeyedCodec<Byte>("Max", Codec.BYTE), (rangeb, i) -> {
        rangeb.max = i;
    }, rangeb -> rangeb.max)).build();
    public static final BuilderCodec<Rangef> RANGEF = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Rangef.class, Rangef::new).metadata(UIDisplayMode.COMPACT)).addField(new KeyedCodec<Double>("Min", Codec.DOUBLE), (rangef, d) -> {
        rangef.min = d.floatValue();
    }, rangeb -> rangeb.min)).addField(new KeyedCodec<Double>("Max", Codec.DOUBLE), (rangef, d) -> {
        rangef.max = d.floatValue();
    }, rangeb -> rangeb.max)).build();
    public static final BuilderCodec<RangeVector2f> RANGE_VECTOR2F = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RangeVector2f.class, RangeVector2f::new).addField(new KeyedCodec<Rangef>("X", RANGEF), (rangeVector2f, d) -> {
        rangeVector2f.x = d;
    }, rangeVector2f -> rangeVector2f.x)).addField(new KeyedCodec<Rangef>("Y", RANGEF), (rangeVector2f, d) -> {
        rangeVector2f.y = d;
    }, rangeVector2f -> rangeVector2f.y)).build();
    public static final BuilderCodec<RangeVector3f> RANGE_VECTOR3F = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RangeVector3f.class, RangeVector3f::new).addField(new KeyedCodec<Rangef>("X", RANGEF), (rangeVector3f, d) -> {
        rangeVector3f.x = d;
    }, rangeVector3f -> rangeVector3f.x)).addField(new KeyedCodec<Rangef>("Y", RANGEF), (rangeVector3f, d) -> {
        rangeVector3f.y = d;
    }, rangeVector3f -> rangeVector3f.y)).addField(new KeyedCodec<Rangef>("Z", RANGEF), (rangeVector3f, d) -> {
        rangeVector3f.z = d;
    }, rangeVector3f -> rangeVector3f.z)).build();
    public static final BuilderCodec<InitialVelocity> INITIAL_VELOCITY = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InitialVelocity.class, InitialVelocity::new).addField(new KeyedCodec<Rangef>("Yaw", RANGEF), (rangeVector3f, d) -> {
        rangeVector3f.yaw = d;
    }, rangeVector3f -> rangeVector3f.yaw)).addField(new KeyedCodec<Rangef>("Pitch", RANGEF), (rangeVector3f, d) -> {
        rangeVector3f.pitch = d;
    }, rangeVector3f -> rangeVector3f.pitch)).addField(new KeyedCodec<Rangef>("Speed", RANGEF), (rangeVector3f, d) -> {
        rangeVector3f.speed = d;
    }, rangeVector3f -> rangeVector3f.speed)).build();
    public static final BuilderCodec<UVMotion> UV_MOTION = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UVMotion.class, UVMotion::new).append(new KeyedCodec<String>("Texture", Codec.STRING), (uvMotion, s) -> {
        uvMotion.texture = s;
    }, uvMotion -> uvMotion.texture).addValidator(CommonAssetValidator.TEXTURE_PARTICLES).add()).append(new KeyedCodec<Boolean>("AddRandomUVOffset", Codec.BOOLEAN), (uvMotion, b) -> {
        uvMotion.addRandomUVOffset = b;
    }, uvMotion -> uvMotion.addRandomUVOffset).add()).append(new KeyedCodec<Double>("SpeedX", Codec.DOUBLE), (uvMotion, s) -> {
        uvMotion.speedX = s.floatValue();
    }, uvMotion -> uvMotion.speedX).addValidator(Validators.range(-10.0, 10.0)).add()).append(new KeyedCodec<Double>("SpeedY", Codec.DOUBLE), (uvMotion, s) -> {
        uvMotion.speedY = s.floatValue();
    }, uvMotion -> uvMotion.speedY).addValidator(Validators.range(-10.0, 10.0)).add()).append(new KeyedCodec<Double>("Strength", Codec.DOUBLE), (uvMotion, s) -> {
        uvMotion.strength = s.floatValue();
    }, uvMotion -> uvMotion.strength).addValidator(Validators.range(0.0, 50.0)).add()).append(new KeyedCodec<UVMotionCurveType>("StrengthCurveType", new EnumCodec<UVMotionCurveType>(UVMotionCurveType.class)), (uvMotion, s) -> {
        uvMotion.strengthCurveType = s;
    }, uvMotion -> uvMotion.strengthCurveType).add()).append(new KeyedCodec<Double>("Scale", Codec.DOUBLE), (uvMotion, s) -> {
        uvMotion.scale = s.floatValue();
    }, uvMotion -> uvMotion.scale).addValidator(Validators.range(0.0, 10.0)).add()).build();
    public static final BuilderCodec<IntersectionHighlight> INTERSECTION_HIGHLIGHT = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IntersectionHighlight.class, IntersectionHighlight::new).append(new KeyedCodec<Float>("HighlightThreshold", Codec.FLOAT), (intersectionHighlight, s) -> {
        intersectionHighlight.highlightThreshold = s.floatValue();
    }, intersectionHighlight -> Float.valueOf(intersectionHighlight.highlightThreshold)).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).addField(new KeyedCodec<Color>("HighlightColor", COLOR), (intersectionHighlight, s) -> {
        intersectionHighlight.highlightColor = s;
    }, intersectionHighlight -> intersectionHighlight.highlightColor)).build();
    public static final BuilderCodec<SavedMovementStates> SAVED_MOVEMENT_STATES = ((BuilderCodec.Builder)BuilderCodec.builder(SavedMovementStates.class, SavedMovementStates::new).addField(new KeyedCodec<Boolean>("Flying", Codec.BOOLEAN), (movementStates, flying) -> {
        movementStates.flying = flying;
    }, movementStates -> movementStates.flying)).build();
    public static final BuilderCodec<ContextMenuItem> CONTEXT_MENU_ITEM = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ContextMenuItem.class, ContextMenuItem::new).addField(new KeyedCodec<String>("Name", Codec.STRING), (item, s) -> {
        item.name = s;
    }, item -> item.name)).addField(new KeyedCodec<String>("Command", Codec.STRING), (item, s) -> {
        item.command = s;
    }, item -> item.command)).build();
    public static final ArrayCodec<ContextMenuItem> CONTEXT_MENU_ITEM_ARRAY = new ArrayCodec<ContextMenuItem>(CONTEXT_MENU_ITEM, ContextMenuItem[]::new);
    public static final BuilderCodec<MapMarker> MARKER = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MapMarker.class, MapMarker::new).addField(new KeyedCodec<String>("Id", Codec.STRING), (marker, s) -> {
        marker.id = s;
    }, marker -> marker.id)).addField(new KeyedCodec<String>("Name", Codec.STRING), (marker, s) -> {
        marker.name = s;
    }, marker -> marker.name)).addField(new KeyedCodec<String>("Image", Codec.STRING), (marker, s) -> {
        marker.markerImage = s;
    }, marker -> marker.markerImage)).append(new KeyedCodec<Transform>("Transform", Transform.CODEC), (marker, s) -> {
        marker.transform = PositionUtil.toTransformPacket(s);
    }, marker -> PositionUtil.toTransform(marker.transform)).addValidator(Validators.nonNull()).add()).addField(new KeyedCodec<T[]>("ContextMenuItems", CONTEXT_MENU_ITEM_ARRAY), (marker, items) -> {
        marker.contextMenuItems = items;
    }, marker -> marker.contextMenuItems)).build();
    public static final ArrayCodec<MapMarker> MARKER_ARRAY = new ArrayCodec<MapMarker>(MARKER, MapMarker[]::new);
    public static final BuilderCodec<ItemAnimation> ITEM_ANIMATION_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemAnimation.class, ItemAnimation::new).append(new KeyedCodec<String>("ThirdPerson", Codec.STRING), (itemAnimation, s) -> {
        itemAnimation.thirdPerson = s;
    }, itemAnimation -> itemAnimation.thirdPerson).addValidator(CommonAssetValidator.ANIMATION_ITEM_CHARACTER).add()).append(new KeyedCodec<String>("ThirdPersonMoving", Codec.STRING), (itemAnimation, s) -> {
        itemAnimation.thirdPersonMoving = s;
    }, itemAnimation -> itemAnimation.thirdPersonMoving).addValidator(CommonAssetValidator.ANIMATION_ITEM_CHARACTER).add()).append(new KeyedCodec<String>("ThirdPersonFace", Codec.STRING), (itemAnimation, s) -> {
        itemAnimation.thirdPersonFace = s;
    }, itemAnimation -> itemAnimation.thirdPersonFace).addValidator(CommonAssetValidator.ANIMATION_ITEM_CHARACTER).add()).append(new KeyedCodec<String>("FirstPerson", Codec.STRING), (itemAnimation, s) -> {
        itemAnimation.firstPerson = s;
    }, itemAnimation -> itemAnimation.firstPerson).addValidator(CommonAssetValidator.ANIMATION_ITEM_CHARACTER).add()).append(new KeyedCodec<String>("FirstPersonOverride", Codec.STRING), (itemAnimation, s) -> {
        itemAnimation.firstPersonOverride = s;
    }, itemAnimation -> itemAnimation.firstPersonOverride).addValidator(CommonAssetValidator.ANIMATION_ITEM_CHARACTER).add()).addField(new KeyedCodec<Boolean>("KeepPreviousFirstPersonAnimation", Codec.BOOLEAN), (itemAnimation, s) -> {
        itemAnimation.keepPreviousFirstPersonAnimation = s;
    }, itemAnimation -> itemAnimation.keepPreviousFirstPersonAnimation)).addField(new KeyedCodec<Double>("Speed", Codec.DOUBLE), (itemAnimation, s) -> {
        itemAnimation.speed = s.floatValue();
    }, itemAnimation -> itemAnimation.speed)).addField(new KeyedCodec<Double>("BlendingDuration", Codec.DOUBLE), (itemAnimation, s) -> {
        itemAnimation.blendingDuration = s.floatValue();
    }, itemAnimation -> itemAnimation.blendingDuration)).addField(new KeyedCodec<Boolean>("Looping", Codec.BOOLEAN), (itemAnimation, s) -> {
        itemAnimation.looping = s;
    }, itemAnimation -> itemAnimation.looping)).addField(new KeyedCodec<Boolean>("ClipsGeometry", Codec.BOOLEAN), (itemAnimation, s) -> {
        itemAnimation.clipsGeometry = s;
    }, itemAnimation -> itemAnimation.clipsGeometry)).afterDecode(itemAnimation -> {
        if (itemAnimation.firstPerson != null) {
            BlockyAnimationCache.get(itemAnimation.firstPerson);
        }
        if (itemAnimation.firstPersonOverride != null) {
            BlockyAnimationCache.get(itemAnimation.firstPersonOverride);
        }
    })).build();
    public static final EnumCodec<ChangeStatBehaviour> CHANGE_STAT_BEHAVIOUR_CODEC = new EnumCodec<ChangeStatBehaviour>(ChangeStatBehaviour.class).documentKey(ChangeStatBehaviour.Add, "Adds the value to the stat").documentKey(ChangeStatBehaviour.Set, "Changes the stat to the given value");
    public static final EnumCodec<AccumulationMode> ACCUMULATION_MODE_CODEC = new EnumCodec<AccumulationMode>(AccumulationMode.class).documentKey(AccumulationMode.Set, "Set the current value to the new one").documentKey(AccumulationMode.Sum, "Add the new value to the current one").documentKey(AccumulationMode.Average, "Average the new value with current one");
    public static final EnumCodec<EasingType> EASING_TYPE_CODEC = new EnumCodec<EasingType>(EasingType.class);
    public static final EnumCodec<ChangeVelocityType> CHANGE_VELOCITY_TYPE_CODEC = new EnumCodec<ChangeVelocityType>(ChangeVelocityType.class).documentKey(ChangeVelocityType.Add, "Adds the velocity to any existing velocity").documentKey(ChangeVelocityType.Set, "Changes the velocity to the given value. Overriding existing values.");
    public static final BuilderCodec<RailPoint> RAIL_POINT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RailPoint.class, RailPoint::new).appendInherited(new KeyedCodec<com.hypixel.hytale.protocol.Vector3f>("Point", VECTOR3F), (o, v) -> {
        o.point = v;
    }, o -> o.point, (o, p) -> {
        o.point = p.point;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<com.hypixel.hytale.protocol.Vector3f>("Normal", VECTOR3F), (o, v) -> {
        o.normal = v;
    }, o -> o.normal, (o, p) -> {
        o.normal = p.normal;
    }).addValidator(Validators.nonNull()).add()).afterDecode(o -> {
        if (o.normal != null) {
            Vector3f v = new Vector3f(o.normal.x, o.normal.y, o.normal.z);
            v = v.normalize();
            o.normal.x = v.x;
            o.normal.y = v.y;
            o.normal.z = v.z;
        }
    })).build();
    public static final BuilderCodec<RailConfig> RAIL_CONFIG_CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(RailConfig.class, RailConfig::new).appendInherited(new KeyedCodec<T[]>("Points", new ArrayCodec<RailPoint>(RAIL_POINT_CODEC, RailPoint[]::new)), (o, v) -> {
        o.points = v;
    }, o -> o.points, (o, p) -> {
        o.points = p.points;
    }).addValidator(Validators.nonNull()).addValidator(Validators.arraySizeRange(2, 16)).add()).build();
}

