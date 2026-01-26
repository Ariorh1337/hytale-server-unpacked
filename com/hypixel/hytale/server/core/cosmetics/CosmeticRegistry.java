/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.cosmetics;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.server.core.cosmetics.CosmeticType;
import com.hypixel.hytale.server.core.cosmetics.Emote;
import com.hypixel.hytale.server.core.cosmetics.PlayerSkinGradientSet;
import com.hypixel.hytale.server.core.cosmetics.PlayerSkinPart;
import com.hypixel.hytale.server.core.cosmetics.PlayerSkinTintColor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class CosmeticRegistry {
    public static final String MODEL = "Characters/Player.blockymodel";
    public static final String SKIN_GRADIENTSET_ID = "Skin";
    @Nonnull
    private final Map<String, Emote> emotes;
    @Nonnull
    private final Map<String, PlayerSkinTintColor> eyeColors;
    @Nonnull
    private final Map<String, PlayerSkinGradientSet> gradientSets;
    @Nonnull
    private final Map<String, PlayerSkinPart> bodyCharacteristics;
    @Nonnull
    private final Map<String, PlayerSkinPart> underwear;
    @Nonnull
    private final Map<String, PlayerSkinPart> eyebrows;
    @Nonnull
    private final Map<String, PlayerSkinPart> ears;
    @Nonnull
    private final Map<String, PlayerSkinPart> eyes;
    @Nonnull
    private final Map<String, PlayerSkinPart> faces;
    @Nonnull
    private final Map<String, PlayerSkinPart> mouths;
    @Nonnull
    private final Map<String, PlayerSkinPart> facialHair;
    @Nonnull
    private final Map<String, PlayerSkinPart> pants;
    @Nonnull
    private final Map<String, PlayerSkinPart> overpants;
    @Nonnull
    private final Map<String, PlayerSkinPart> undertops;
    @Nonnull
    private final Map<String, PlayerSkinPart> overtops;
    @Nonnull
    private final Map<String, PlayerSkinPart> haircuts;
    @Nonnull
    private final Map<String, PlayerSkinPart> shoes;
    @Nonnull
    private final Map<String, PlayerSkinPart> headAccessory;
    @Nonnull
    private final Map<String, PlayerSkinPart> faceAccessory;
    @Nonnull
    private final Map<String, PlayerSkinPart> earAccessory;
    @Nonnull
    private final Map<String, PlayerSkinPart> gloves;
    @Nonnull
    private final Map<String, PlayerSkinPart> capes;
    @Nonnull
    private final Map<String, PlayerSkinPart> skinFeatures;

    public CosmeticRegistry(@Nonnull AssetPack pack) {
        Path assetsDirectory = pack.getRoot();
        this.emotes = this.load(assetsDirectory, "Emotes.json", Emote::new);
        this.eyeColors = this.load(assetsDirectory, "EyeColors.json", PlayerSkinTintColor::new);
        this.gradientSets = this.load(assetsDirectory, "GradientSets.json", PlayerSkinGradientSet::new);
        this.bodyCharacteristics = this.load(assetsDirectory, "BodyCharacteristics.json", PlayerSkinPart::new);
        this.underwear = this.load(assetsDirectory, "Underwear.json", PlayerSkinPart::new);
        this.eyes = this.load(assetsDirectory, "Eyes.json", PlayerSkinPart::new);
        this.faces = this.load(assetsDirectory, "Faces.json", PlayerSkinPart::new);
        this.eyebrows = this.load(assetsDirectory, "Eyebrows.json", PlayerSkinPart::new);
        this.ears = this.load(assetsDirectory, "Ears.json", PlayerSkinPart::new);
        this.mouths = this.load(assetsDirectory, "Mouths.json", PlayerSkinPart::new);
        this.facialHair = this.load(assetsDirectory, "FacialHair.json", PlayerSkinPart::new);
        this.pants = this.load(assetsDirectory, "Pants.json", PlayerSkinPart::new);
        this.overpants = this.load(assetsDirectory, "Overpants.json", PlayerSkinPart::new);
        this.undertops = this.load(assetsDirectory, "Undertops.json", PlayerSkinPart::new);
        this.overtops = this.load(assetsDirectory, "Overtops.json", PlayerSkinPart::new);
        this.haircuts = this.load(assetsDirectory, "Haircuts.json", PlayerSkinPart::new);
        this.shoes = this.load(assetsDirectory, "Shoes.json", PlayerSkinPart::new);
        this.headAccessory = this.load(assetsDirectory, "HeadAccessory.json", PlayerSkinPart::new);
        this.faceAccessory = this.load(assetsDirectory, "FaceAccessory.json", PlayerSkinPart::new);
        this.earAccessory = this.load(assetsDirectory, "EarAccessory.json", PlayerSkinPart::new);
        this.gloves = this.load(assetsDirectory, "Gloves.json", PlayerSkinPart::new);
        this.capes = this.load(assetsDirectory, "Capes.json", PlayerSkinPart::new);
        this.skinFeatures = this.load(assetsDirectory, "SkinFeatures.json", PlayerSkinPart::new);
    }

    @Nonnull
    private <T> Map<String, T> load(@Nonnull Path assetsDirectory, @Nonnull String file, @Nonnull Function<BsonDocument, T> func) {
        Object2ObjectOpenHashMap<String, T> map = new Object2ObjectOpenHashMap<String, T>();
        Path path = assetsDirectory.resolve("Cosmetics").resolve("CharacterCreator").resolve(file);
        try {
            BsonArray bsonArray = BsonArray.parse(Files.readString(path));
            for (BsonValue bsonValue : bsonArray) {
                BsonDocument doc = bsonValue.asDocument();
                map.put(doc.getString("Id").getValue(), func.apply(doc));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableMap(map);
    }

    @Nonnull
    public Map<String, Emote> getEmotes() {
        return this.emotes;
    }

    @Nonnull
    public Map<String, PlayerSkinTintColor> getEyeColors() {
        return this.eyeColors;
    }

    @Nonnull
    public Map<String, PlayerSkinGradientSet> getGradientSets() {
        return this.gradientSets;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getBodyCharacteristics() {
        return this.bodyCharacteristics;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getUnderwear() {
        return this.underwear;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getEyebrows() {
        return this.eyebrows;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getEars() {
        return this.ears;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getEyes() {
        return this.eyes;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getFaces() {
        return this.faces;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getMouths() {
        return this.mouths;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getFacialHairs() {
        return this.facialHair;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getPants() {
        return this.pants;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getOverpants() {
        return this.overpants;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getUndertops() {
        return this.undertops;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getOvertops() {
        return this.overtops;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getHaircuts() {
        return this.haircuts;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getShoes() {
        return this.shoes;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getHeadAccessories() {
        return this.headAccessory;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getFaceAccessories() {
        return this.faceAccessory;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getEarAccessories() {
        return this.earAccessory;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getGloves() {
        return this.gloves;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getSkinFeatures() {
        return this.skinFeatures;
    }

    @Nonnull
    public Map<String, PlayerSkinPart> getCapes() {
        return this.capes;
    }

    public Map<String, ?> getByType(@Nonnull CosmeticType type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case CosmeticType.EMOTES -> this.getEmotes();
            case CosmeticType.SKIN_TONES -> this.getGradientSets().get(SKIN_GRADIENTSET_ID).getGradients();
            case CosmeticType.EYE_COLORS -> this.getEyeColors();
            case CosmeticType.GRADIENT_SETS -> this.getGradientSets();
            case CosmeticType.BODY_CHARACTERISTICS -> this.getBodyCharacteristics();
            case CosmeticType.UNDERWEAR -> this.getUnderwear();
            case CosmeticType.EYEBROWS -> this.getEyebrows();
            case CosmeticType.EARS -> this.getEars();
            case CosmeticType.EYES -> this.getEyes();
            case CosmeticType.FACE -> this.getFaces();
            case CosmeticType.MOUTHS -> this.getMouths();
            case CosmeticType.FACIAL_HAIR -> this.getFacialHairs();
            case CosmeticType.PANTS -> this.getPants();
            case CosmeticType.OVERPANTS -> this.getOverpants();
            case CosmeticType.UNDERTOPS -> this.getUndertops();
            case CosmeticType.OVERTOPS -> this.getOvertops();
            case CosmeticType.HAIRCUTS -> this.getHaircuts();
            case CosmeticType.SHOES -> this.getShoes();
            case CosmeticType.HEAD_ACCESSORY -> this.getHeadAccessories();
            case CosmeticType.FACE_ACCESSORY -> this.getFaceAccessories();
            case CosmeticType.EAR_ACCESSORY -> this.getEarAccessories();
            case CosmeticType.GLOVES -> this.getGloves();
            case CosmeticType.CAPES -> this.getCapes();
            case CosmeticType.SKIN_FEATURES -> this.getSkinFeatures();
        };
    }
}

