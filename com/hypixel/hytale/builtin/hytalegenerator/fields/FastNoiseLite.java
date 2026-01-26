/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.fields;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class FastNoiseLite {
    private int mSeed = 1337;
    private float mFrequency = 0.01f;
    private NoiseType mNoiseType = NoiseType.OpenSimplex2;
    private RotationType3D mRotationType3D = RotationType3D.None;
    @Nonnull
    private TransformType3D mTransformType3D = TransformType3D.DefaultOpenSimplex2;
    private FractalType mFractalType = FractalType.None;
    private int mOctaves = 3;
    private float mLacunarity = 2.0f;
    private float mGain = 0.5f;
    private float mWeightedStrength = 0.0f;
    private float mPingPongStrength = 2.0f;
    private float mFractalBounding = 0.5714286f;
    private CellularDistanceFunction mCellularDistanceFunction = CellularDistanceFunction.EuclideanSq;
    private CellularReturnType mCellularReturnType = CellularReturnType.Distance;
    private float mCellularJitterModifier = 1.0f;
    private DomainWarpType mDomainWarpType = DomainWarpType.OpenSimplex2;
    @Nonnull
    private TransformType3D mWarpTransformType3D = TransformType3D.DefaultOpenSimplex2;
    private float mDomainWarpAmp = 1.0f;
    private float mDomainWarpFreq = 1.0f;
    private static final float[] Gradients2D = new float[]{0.13052619f, 0.9914449f, 0.38268343f, 0.9238795f, 0.6087614f, 0.7933533f, 0.7933533f, 0.6087614f, 0.9238795f, 0.38268343f, 0.9914449f, 0.13052619f, 0.9914449f, -0.13052619f, 0.9238795f, -0.38268343f, 0.7933533f, -0.6087614f, 0.6087614f, -0.7933533f, 0.38268343f, -0.9238795f, 0.13052619f, -0.9914449f, -0.13052619f, -0.9914449f, -0.38268343f, -0.9238795f, -0.6087614f, -0.7933533f, -0.7933533f, -0.6087614f, -0.9238795f, -0.38268343f, -0.9914449f, -0.13052619f, -0.9914449f, 0.13052619f, -0.9238795f, 0.38268343f, -0.7933533f, 0.6087614f, -0.6087614f, 0.7933533f, -0.38268343f, 0.9238795f, -0.13052619f, 0.9914449f, 0.13052619f, 0.9914449f, 0.38268343f, 0.9238795f, 0.6087614f, 0.7933533f, 0.7933533f, 0.6087614f, 0.9238795f, 0.38268343f, 0.9914449f, 0.13052619f, 0.9914449f, -0.13052619f, 0.9238795f, -0.38268343f, 0.7933533f, -0.6087614f, 0.6087614f, -0.7933533f, 0.38268343f, -0.9238795f, 0.13052619f, -0.9914449f, -0.13052619f, -0.9914449f, -0.38268343f, -0.9238795f, -0.6087614f, -0.7933533f, -0.7933533f, -0.6087614f, -0.9238795f, -0.38268343f, -0.9914449f, -0.13052619f, -0.9914449f, 0.13052619f, -0.9238795f, 0.38268343f, -0.7933533f, 0.6087614f, -0.6087614f, 0.7933533f, -0.38268343f, 0.9238795f, -0.13052619f, 0.9914449f, 0.13052619f, 0.9914449f, 0.38268343f, 0.9238795f, 0.6087614f, 0.7933533f, 0.7933533f, 0.6087614f, 0.9238795f, 0.38268343f, 0.9914449f, 0.13052619f, 0.9914449f, -0.13052619f, 0.9238795f, -0.38268343f, 0.7933533f, -0.6087614f, 0.6087614f, -0.7933533f, 0.38268343f, -0.9238795f, 0.13052619f, -0.9914449f, -0.13052619f, -0.9914449f, -0.38268343f, -0.9238795f, -0.6087614f, -0.7933533f, -0.7933533f, -0.6087614f, -0.9238795f, -0.38268343f, -0.9914449f, -0.13052619f, -0.9914449f, 0.13052619f, -0.9238795f, 0.38268343f, -0.7933533f, 0.6087614f, -0.6087614f, 0.7933533f, -0.38268343f, 0.9238795f, -0.13052619f, 0.9914449f, 0.13052619f, 0.9914449f, 0.38268343f, 0.9238795f, 0.6087614f, 0.7933533f, 0.7933533f, 0.6087614f, 0.9238795f, 0.38268343f, 0.9914449f, 0.13052619f, 0.9914449f, -0.13052619f, 0.9238795f, -0.38268343f, 0.7933533f, -0.6087614f, 0.6087614f, -0.7933533f, 0.38268343f, -0.9238795f, 0.13052619f, -0.9914449f, -0.13052619f, -0.9914449f, -0.38268343f, -0.9238795f, -0.6087614f, -0.7933533f, -0.7933533f, -0.6087614f, -0.9238795f, -0.38268343f, -0.9914449f, -0.13052619f, -0.9914449f, 0.13052619f, -0.9238795f, 0.38268343f, -0.7933533f, 0.6087614f, -0.6087614f, 0.7933533f, -0.38268343f, 0.9238795f, -0.13052619f, 0.9914449f, 0.13052619f, 0.9914449f, 0.38268343f, 0.9238795f, 0.6087614f, 0.7933533f, 0.7933533f, 0.6087614f, 0.9238795f, 0.38268343f, 0.9914449f, 0.13052619f, 0.9914449f, -0.13052619f, 0.9238795f, -0.38268343f, 0.7933533f, -0.6087614f, 0.6087614f, -0.7933533f, 0.38268343f, -0.9238795f, 0.13052619f, -0.9914449f, -0.13052619f, -0.9914449f, -0.38268343f, -0.9238795f, -0.6087614f, -0.7933533f, -0.7933533f, -0.6087614f, -0.9238795f, -0.38268343f, -0.9914449f, -0.13052619f, -0.9914449f, 0.13052619f, -0.9238795f, 0.38268343f, -0.7933533f, 0.6087614f, -0.6087614f, 0.7933533f, -0.38268343f, 0.9238795f, -0.13052619f, 0.9914449f, 0.38268343f, 0.9238795f, 0.9238795f, 0.38268343f, 0.9238795f, -0.38268343f, 0.38268343f, -0.9238795f, -0.38268343f, -0.9238795f, -0.9238795f, -0.38268343f, -0.9238795f, 0.38268343f, -0.38268343f, 0.9238795f};
    private static final float[] RandVecs2D = new float[]{-0.2700222f, -0.9628541f, 0.38630927f, -0.9223693f, 0.04444859f, -0.9990117f, -0.59925234f, -0.80056024f, -0.781928f, 0.62336874f, 0.9464672f, 0.32279992f, -0.6514147f, -0.7587219f, 0.93784726f, 0.34704837f, -0.8497876f, -0.52712524f, -0.87904257f, 0.47674325f, -0.8923003f, -0.45144236f, -0.37984443f, -0.9250504f, -0.9951651f, 0.09821638f, 0.7724398f, -0.635088f, 0.75732833f, -0.6530343f, -0.9928005f, -0.119780056f, -0.05326657f, 0.99858034f, 0.97542536f, -0.22033007f, -0.76650184f, 0.64224213f, 0.9916367f, 0.12906061f, -0.99469686f, 0.10285038f, -0.53792053f, -0.8429955f, 0.50228155f, -0.86470413f, 0.45598215f, -0.8899889f, -0.8659131f, -0.50019443f, 0.08794584f, -0.9961253f, -0.5051685f, 0.8630207f, 0.7753185f, -0.6315704f, -0.69219446f, 0.72171104f, -0.51916593f, -0.85467345f, 0.8978623f, -0.4402764f, -0.17067741f, 0.98532695f, -0.935343f, -0.35374206f, -0.99924046f, 0.038967468f, -0.2882064f, -0.9575683f, -0.96638113f, 0.2571138f, -0.87597144f, -0.48236302f, -0.8303123f, -0.55729836f, 0.051101338f, -0.99869347f, -0.85583735f, -0.51724505f, 0.098870255f, 0.9951003f, 0.9189016f, 0.39448678f, -0.24393758f, -0.96979094f, -0.81214094f, -0.5834613f, -0.99104315f, 0.13354214f, 0.8492424f, -0.52800316f, -0.9717839f, -0.23587295f, 0.9949457f, 0.10041421f, 0.6241065f, -0.7813392f, 0.6629103f, 0.74869883f, -0.7197418f, 0.6942418f, -0.8143371f, -0.58039224f, 0.10452105f, -0.9945227f, -0.10659261f, -0.99430275f, 0.44579968f, -0.8951328f, 0.105547406f, 0.99441427f, -0.9927903f, 0.11986445f, -0.83343667f, 0.55261505f, 0.9115562f, -0.4111756f, 0.8285545f, -0.55990845f, 0.7217098f, -0.6921958f, 0.49404928f, -0.8694339f, -0.36523214f, -0.9309165f, -0.9696607f, 0.24445485f, 0.089255095f, -0.9960088f, 0.5354071f, -0.8445941f, -0.10535762f, 0.9944344f, -0.98902845f, 0.1477251f, 0.004856105f, 0.9999882f, 0.98855984f, 0.15082914f, 0.92861295f, -0.37104982f, -0.5832394f, -0.8123003f, 0.30152076f, 0.9534596f, -0.95751107f, 0.28839657f, 0.9715802f, -0.23671055f, 0.2299818f, 0.97319496f, 0.9557638f, -0.2941352f, 0.7409561f, 0.67155343f, -0.9971514f, -0.07542631f, 0.69057107f, -0.7232645f, -0.2907137f, -0.9568101f, 0.5912778f, -0.80646795f, -0.94545925f, -0.3257405f, 0.66644555f, 0.7455537f, 0.6236135f, 0.78173286f, 0.9126994f, -0.40863165f, -0.8191762f, 0.57354194f, -0.8812746f, -0.4726046f, 0.99533135f, 0.09651673f, 0.98556507f, -0.16929697f, -0.8495981f, 0.52743065f, 0.6174854f, -0.78658235f, 0.85081565f, 0.5254643f, 0.99850327f, -0.0546925f, 0.19713716f, -0.98037595f, 0.66078556f, -0.7505747f, -0.030974941f, 0.9995202f, -0.6731661f, 0.73949134f, -0.71950185f, -0.69449055f, 0.97275114f, 0.2318516f, 0.9997059f, -0.02425069f, 0.44217876f, -0.89692694f, 0.9981351f, -0.061043672f, -0.9173661f, -0.39804456f, -0.81500566f, -0.579453f, -0.87893313f, 0.476945f, 0.015860584f, 0.99987423f, -0.8095465f, 0.5870558f, -0.9165899f, -0.39982867f, -0.8023543f, 0.5968481f, -0.5176738f, 0.85557806f, -0.8154407f, -0.57884055f, 0.40220103f, -0.91555136f, -0.9052557f, -0.4248672f, 0.7317446f, 0.681579f, -0.56476325f, -0.825253f, -0.8403276f, -0.54207885f, -0.93142813f, 0.36392525f, 0.52381986f, 0.85182905f, 0.7432804f, -0.66898f, -0.9853716f, -0.17041974f, 0.46014687f, 0.88784283f, 0.8258554f, 0.56388193f, 0.6182366f, 0.785992f, 0.83315027f, -0.55304664f, 0.15003075f, 0.9886813f, -0.6623304f, -0.7492119f, -0.66859865f, 0.74362344f, 0.7025606f, 0.7116239f, -0.54193896f, -0.84041786f, -0.33886164f, 0.9408362f, 0.833153f, 0.55304253f, -0.29897207f, -0.95426184f, 0.2638523f, 0.9645631f, 0.12410874f, -0.9922686f, -0.7282649f, -0.6852957f, 0.69625f, 0.71779937f, -0.91835356f, 0.395761f, -0.6326102f, -0.7744703f, -0.9331892f, -0.35938552f, -0.11537793f, -0.99332166f, 0.9514975f, -0.30765656f, -0.08987977f, -0.9959526f, 0.6678497f, 0.7442962f, 0.79524004f, -0.6062947f, -0.6462007f, -0.7631675f, -0.27335986f, 0.96191186f, 0.966959f, -0.25493184f, -0.9792895f, 0.20246519f, -0.5369503f, -0.84361386f, -0.27003646f, -0.9628501f, -0.6400277f, 0.76835185f, -0.78545374f, -0.6189204f, 0.060059056f, -0.9981948f, -0.024557704f, 0.9996984f, -0.65983623f, 0.7514095f, -0.62538946f, -0.7803128f, -0.6210409f, -0.7837782f, 0.8348889f, 0.55041856f, -0.15922752f, 0.9872419f, 0.83676225f, 0.54756635f, -0.8675754f, -0.4973057f, -0.20226626f, -0.97933054f, 0.939919f, 0.34139755f, 0.98774046f, -0.1561049f, -0.90344554f, 0.42870283f, 0.12698042f, -0.9919052f, -0.3819601f, 0.92417884f, 0.9754626f, 0.22016525f, -0.32040158f, -0.94728184f, -0.9874761f, 0.15776874f, 0.025353484f, -0.99967855f, 0.4835131f, -0.8753371f, -0.28508f, -0.9585037f, -0.06805516f, -0.99768156f, -0.7885244f, -0.61500347f, 0.3185392f, -0.9479097f, 0.8880043f, 0.45983514f, 0.64769214f, -0.76190215f, 0.98202413f, 0.18875542f, 0.93572754f, -0.35272372f, -0.88948953f, 0.45695552f, 0.7922791f, 0.6101588f, 0.74838185f, 0.66326815f, -0.728893f, -0.68462765f, 0.8729033f, -0.48789328f, 0.8288346f, 0.5594937f, 0.08074567f, 0.99673474f, 0.97991484f, -0.1994165f, -0.5807307f, -0.81409574f, -0.47000498f, -0.8826638f, 0.2409493f, 0.9705377f, 0.9437817f, -0.33056942f, -0.89279985f, -0.45045355f, -0.80696225f, 0.59060305f, 0.062589735f, 0.99803936f, -0.93125975f, 0.36435598f, 0.57774496f, 0.81621736f, -0.3360096f, -0.9418586f, 0.69793206f, -0.71616393f, -0.0020081573f, -0.999998f, -0.18272944f, -0.98316324f, -0.6523912f, 0.7578824f, -0.43026268f, -0.9027037f, -0.9985126f, -0.054520912f, -0.010281022f, -0.99994713f, -0.49460712f, 0.86911666f, -0.299935f, 0.95395964f, 0.8165472f, 0.5772787f, 0.26974604f, 0.9629315f, -0.7306287f, -0.68277496f, -0.7590952f, -0.65097964f, -0.9070538f, 0.4210146f, -0.5104861f, -0.859886f, 0.86133504f, 0.5080373f, 0.50078815f, -0.8655699f, -0.6541582f, 0.7563578f, -0.83827555f, -0.54524684f, 0.6940071f, 0.7199682f, 0.06950936f, 0.9975813f, 0.17029423f, -0.9853933f, 0.26959732f, 0.9629731f, 0.55196124f, -0.83386976f, 0.2256575f, -0.9742067f, 0.42152628f, -0.9068162f, 0.48818734f, -0.87273884f, -0.3683855f, -0.92967314f, -0.98253906f, 0.18605645f, 0.81256473f, 0.582871f, 0.3196461f, -0.947537f, 0.9570914f, 0.28978625f, -0.6876655f, -0.7260276f, -0.9988771f, -0.04737673f, -0.1250179f, 0.9921545f, -0.82801336f, 0.56070834f, 0.93248636f, -0.36120513f, 0.63946533f, 0.7688199f, -0.016238471f, -0.99986815f, -0.99550146f, -0.094746135f, -0.8145332f, 0.580117f, 0.4037328f, -0.91487694f, 0.9944263f, 0.10543368f, -0.16247116f, 0.9867133f, -0.9949488f, -0.10038388f, -0.69953024f, 0.714603f, 0.5263415f, -0.85027325f, -0.5395222f, 0.8419714f, 0.65793705f, 0.7530729f, 0.014267588f, -0.9998982f, -0.6734384f, 0.7392433f, 0.6394121f, -0.7688642f, 0.9211571f, 0.38919085f, -0.14663722f, -0.98919034f, -0.7823181f, 0.6228791f, -0.5039611f, -0.8637264f, -0.774312f, -0.632804f};
    private static final float[] Gradients3D = new float[]{0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f};
    private static final float[] randVecs3D = new float[]{-0.7292737f, -0.66184396f, 0.17355819f, 0.0f, 0.7902921f, -0.5480887f, -0.2739291f, 0.0f, 0.7217579f, 0.62262124f, -0.3023381f, 0.0f, 0.5656831f, -0.8208298f, -0.079000026f, 0.0f, 0.76004905f, -0.55559796f, -0.33709997f, 0.0f, 0.37139457f, 0.50112647f, 0.78162545f, 0.0f, -0.12770624f, -0.4254439f, -0.8959289f, 0.0f, -0.2881561f, -0.5815839f, 0.7607406f, 0.0f, 0.5849561f, -0.6628202f, -0.4674352f, 0.0f, 0.33071712f, 0.039165374f, 0.94291687f, 0.0f, 0.8712122f, -0.41133744f, -0.26793817f, 0.0f, 0.580981f, 0.7021916f, 0.41156778f, 0.0f, 0.5037569f, 0.6330057f, -0.5878204f, 0.0f, 0.44937122f, 0.6013902f, 0.6606023f, 0.0f, -0.6878404f, 0.090188906f, -0.7202372f, 0.0f, -0.59589565f, -0.64693505f, 0.47579765f, 0.0f, -0.5127052f, 0.1946922f, -0.83619875f, 0.0f, -0.99115074f, -0.054102764f, -0.12121531f, 0.0f, -0.21497211f, 0.9720882f, -0.09397608f, 0.0f, -0.7518651f, -0.54280573f, 0.37424695f, 0.0f, 0.5237069f, 0.8516377f, -0.021078179f, 0.0f, 0.6333505f, 0.19261672f, -0.74951047f, 0.0f, -0.06788242f, 0.39983058f, 0.9140719f, 0.0f, -0.55386287f, -0.47298968f, -0.6852129f, 0.0f, -0.72614557f, -0.5911991f, 0.35099334f, 0.0f, -0.9229275f, -0.17828088f, 0.34120494f, 0.0f, -0.6968815f, 0.65112746f, 0.30064803f, 0.0f, 0.96080446f, -0.20983632f, -0.18117249f, 0.0f, 0.068171464f, -0.9743405f, 0.21450691f, 0.0f, -0.3577285f, -0.6697087f, -0.65078455f, 0.0f, -0.18686211f, 0.7648617f, -0.61649746f, 0.0f, -0.65416974f, 0.3967915f, 0.64390874f, 0.0f, 0.699334f, -0.6164538f, 0.36182392f, 0.0f, -0.15466657f, 0.6291284f, 0.7617583f, 0.0f, -0.6841613f, -0.2580482f, -0.68215424f, 0.0f, 0.5383981f, 0.4258655f, 0.727163f, 0.0f, -0.5026988f, -0.7939833f, -0.3418837f, 0.0f, 0.32029718f, 0.28344154f, 0.9039196f, 0.0f, 0.86832273f, -3.7626564E-4f, -0.49599952f, 0.0f, 0.79112005f, -0.085110456f, 0.60571057f, 0.0f, -0.04011016f, -0.43972486f, 0.8972364f, 0.0f, 0.914512f, 0.35793462f, -0.18854876f, 0.0f, -0.96120393f, -0.27564842f, 0.010246669f, 0.0f, 0.65103614f, -0.28777993f, -0.70237786f, 0.0f, -0.20417863f, 0.73652375f, 0.6448596f, 0.0f, -0.7718264f, 0.37906268f, 0.5104856f, 0.0f, -0.30600828f, -0.7692988f, 0.56083715f, 0.0f, 0.45400733f, -0.5024843f, 0.73578995f, 0.0f, 0.48167956f, 0.6021208f, -0.636738f, 0.0f, 0.69619805f, -0.32221973f, 0.6414692f, 0.0f, -0.65321606f, -0.6781149f, 0.33685157f, 0.0f, 0.50893015f, -0.61546624f, -0.60182345f, 0.0f, -0.16359198f, -0.9133605f, -0.37284088f, 0.0f, 0.5240802f, -0.8437664f, 0.11575059f, 0.0f, 0.5902587f, 0.4983818f, -0.63498837f, 0.0f, 0.5863228f, 0.49476475f, 0.6414308f, 0.0f, 0.6779335f, 0.23413453f, 0.6968409f, 0.0f, 0.7177054f, -0.68589795f, 0.12017863f, 0.0f, -0.532882f, -0.5205125f, 0.6671608f, 0.0f, -0.8654874f, -0.07007271f, -0.4960054f, 0.0f, -0.286181f, 0.79520893f, 0.53454953f, 0.0f, -0.048495296f, 0.98108363f, -0.18741156f, 0.0f, -0.63585216f, 0.60583484f, 0.47818002f, 0.0f, 0.62547946f, -0.28616196f, 0.72586966f, 0.0f, -0.258526f, 0.50619495f, -0.8227582f, 0.0f, 0.021363068f, 0.50640166f, -0.862033f, 0.0f, 0.20011178f, 0.85992634f, 0.46955505f, 0.0f, 0.47435614f, 0.6014985f, -0.6427953f, 0.0f, 0.6622994f, -0.52024746f, -0.539168f, 0.0f, 0.08084973f, -0.65327203f, 0.7527941f, 0.0f, -0.6893687f, 0.059286036f, 0.7219805f, 0.0f, -0.11218871f, -0.96731853f, 0.22739525f, 0.0f, 0.7344116f, 0.59796685f, -0.3210533f, 0.0f, 0.5789393f, -0.24888498f, 0.776457f, 0.0f, 0.69881827f, 0.35571697f, -0.6205791f, 0.0f, -0.86368454f, -0.27487713f, -0.4224826f, 0.0f, -0.4247028f, -0.46408808f, 0.77733505f, 0.0f, 0.5257723f, -0.84270173f, 0.11583299f, 0.0f, 0.93438303f, 0.31630248f, -0.16395439f, 0.0f, -0.10168364f, -0.8057303f, -0.58348876f, 0.0f, -0.6529239f, 0.50602126f, -0.5635893f, 0.0f, -0.24652861f, -0.9668206f, -0.06694497f, 0.0f, -0.9776897f, -0.20992506f, -0.0073688254f, 0.0f, 0.7736893f, 0.57342446f, 0.2694238f, 0.0f, -0.6095088f, 0.4995679f, 0.6155737f, 0.0f, 0.5794535f, 0.7434547f, 0.33392924f, 0.0f, -0.8226211f, 0.081425816f, 0.56272936f, 0.0f, -0.51038545f, 0.47036678f, 0.719904f, 0.0f, -0.5764972f, -0.072316565f, -0.81389266f, 0.0f, 0.7250629f, 0.39499715f, -0.56414634f, 0.0f, -0.1525424f, 0.48608407f, -0.8604958f, 0.0f, -0.55509764f, -0.49578208f, 0.6678823f, 0.0f, -0.18836144f, 0.91458696f, 0.35784173f, 0.0f, 0.76255566f, -0.54144084f, -0.35404897f, 0.0f, -0.5870232f, -0.3226498f, -0.7424964f, 0.0f, 0.30511242f, 0.2262544f, -0.9250488f, 0.0f, 0.63795763f, 0.57724243f, -0.50970703f, 0.0f, -0.5966776f, 0.14548524f, -0.7891831f, 0.0f, -0.65833056f, 0.65554875f, -0.36994147f, 0.0f, 0.74348927f, 0.23510846f, 0.6260573f, 0.0f, 0.5562114f, 0.82643604f, -0.08736329f, 0.0f, -0.302894f, -0.8251527f, 0.47684193f, 0.0f, 0.11293438f, -0.9858884f, -0.123571075f, 0.0f, 0.5937653f, -0.5896814f, 0.5474657f, 0.0f, 0.6757964f, -0.58357584f, -0.45026484f, 0.0f, 0.7242303f, -0.11527198f, 0.67985505f, 0.0f, -0.9511914f, 0.0753624f, -0.29925808f, 0.0f, 0.2539471f, -0.18863393f, 0.9486454f, 0.0f, 0.5714336f, -0.16794509f, -0.8032796f, 0.0f, -0.06778235f, 0.39782694f, 0.9149532f, 0.0f, 0.6074973f, 0.73306f, -0.30589226f, 0.0f, -0.54354787f, 0.16758224f, 0.8224791f, 0.0f, -0.5876678f, -0.3380045f, -0.7351187f, 0.0f, -0.79675627f, 0.040978227f, -0.60290986f, 0.0f, -0.19963509f, 0.8706295f, 0.4496111f, 0.0f, -0.027876602f, -0.91062325f, -0.4122962f, 0.0f, -0.7797626f, -0.6257635f, 0.019757755f, 0.0f, -0.5211233f, 0.74016446f, -0.42495546f, 0.0f, 0.8575425f, 0.4053273f, -0.31675017f, 0.0f, 0.10452233f, 0.8390196f, -0.53396744f, 0.0f, 0.3501823f, 0.9242524f, -0.15208502f, 0.0f, 0.19878499f, 0.076476134f, 0.9770547f, 0.0f, 0.78459966f, 0.6066257f, -0.12809642f, 0.0f, 0.09006737f, -0.97509897f, -0.20265691f, 0.0f, -0.82743436f, -0.54229957f, 0.14582036f, 0.0f, -0.34857976f, -0.41580227f, 0.8400004f, 0.0f, -0.2471779f, -0.730482f, -0.6366311f, 0.0f, -0.3700155f, 0.8577948f, 0.35675845f, 0.0f, 0.59133947f, -0.54831195f, -0.59133035f, 0.0f, 0.120487355f, -0.7626472f, -0.6354935f, 0.0f, 0.6169593f, 0.03079648f, 0.7863923f, 0.0f, 0.12581569f, -0.664083f, -0.73699677f, 0.0f, -0.6477565f, -0.17401473f, -0.74170774f, 0.0f, 0.6217889f, -0.7804431f, -0.06547655f, 0.0f, 0.6589943f, -0.6096988f, 0.44044736f, 0.0f, -0.26898375f, -0.6732403f, -0.68876356f, 0.0f, -0.38497752f, 0.56765425f, 0.7277094f, 0.0f, 0.57544446f, 0.81104714f, -0.10519635f, 0.0f, 0.91415936f, 0.3832948f, 0.13190056f, 0.0f, -0.10792532f, 0.9245494f, 0.36545935f, 0.0f, 0.3779771f, 0.30431488f, 0.87437165f, 0.0f, -0.21428852f, -0.8259286f, 0.5214617f, 0.0f, 0.58025444f, 0.41480985f, -0.7008834f, 0.0f, -0.19826609f, 0.85671616f, -0.47615966f, 0.0f, -0.033815537f, 0.37731808f, -0.9254661f, 0.0f, -0.68679225f, -0.6656598f, 0.29191336f, 0.0f, 0.7731743f, -0.28757936f, -0.565243f, 0.0f, -0.09655942f, 0.91937083f, -0.3813575f, 0.0f, 0.27157024f, -0.957791f, -0.09426606f, 0.0f, 0.24510157f, -0.6917999f, -0.6792188f, 0.0f, 0.97770077f, -0.17538553f, 0.115503654f, 0.0f, -0.522474f, 0.8521607f, 0.029036159f, 0.0f, -0.77348804f, -0.52612925f, 0.35341796f, 0.0f, -0.71344924f, -0.26954725f, 0.6467878f, 0.0f, 0.16440372f, 0.5105846f, -0.84396374f, 0.0f, 0.6494636f, 0.055856112f, 0.7583384f, 0.0f, -0.4711971f, 0.50172806f, -0.7254256f, 0.0f, -0.63357645f, -0.23816863f, -0.7361091f, 0.0f, -0.9021533f, -0.2709478f, -0.33571818f, 0.0f, -0.3793711f, 0.8722581f, 0.3086152f, 0.0f, -0.68555987f, -0.32501432f, 0.6514394f, 0.0f, 0.29009423f, -0.7799058f, -0.5546101f, 0.0f, -0.20983194f, 0.8503707f, 0.48253515f, 0.0f, -0.45926037f, 0.6598504f, -0.5947077f, 0.0f, 0.87159455f, 0.09616365f, -0.48070312f, 0.0f, -0.6776666f, 0.71185046f, -0.1844907f, 0.0f, 0.7044378f, 0.3124276f, 0.637304f, 0.0f, -0.7052319f, -0.24010932f, -0.6670798f, 0.0f, 0.081921004f, -0.72073364f, -0.68835455f, 0.0f, -0.6993681f, -0.5875763f, -0.4069869f, 0.0f, -0.12814544f, 0.6419896f, 0.75592864f, 0.0f, -0.6337388f, -0.67854714f, -0.3714147f, 0.0f, 0.5565052f, -0.21688876f, -0.8020357f, 0.0f, -0.57915545f, 0.7244372f, -0.3738579f, 0.0f, 0.11757791f, -0.7096451f, 0.69467926f, 0.0f, -0.613462f, 0.13236311f, 0.7785528f, 0.0f, 0.69846356f, -0.029805163f, -0.7150247f, 0.0f, 0.83180827f, -0.3930172f, 0.39195976f, 0.0f, 0.14695764f, 0.055416517f, -0.98758924f, 0.0f, 0.70886856f, -0.2690504f, 0.65201014f, 0.0f, 0.27260533f, 0.67369765f, -0.68688995f, 0.0f, -0.65912956f, 0.30354586f, -0.68804663f, 0.0f, 0.48151314f, -0.752827f, 0.4487723f, 0.0f, 0.943001f, 0.16756473f, -0.28752613f, 0.0f, 0.43480295f, 0.7695305f, -0.46772778f, 0.0f, 0.39319962f, 0.5944736f, 0.70142365f, 0.0f, 0.72543365f, -0.60392565f, 0.33018148f, 0.0f, 0.75902355f, -0.6506083f, 0.024333132f, 0.0f, -0.8552769f, -0.3430043f, 0.38839358f, 0.0f, -0.6139747f, 0.6981725f, 0.36822575f, 0.0f, -0.74659055f, -0.575201f, 0.33428493f, 0.0f, 0.5730066f, 0.8105555f, -0.12109168f, 0.0f, -0.92258775f, -0.3475211f, -0.16751404f, 0.0f, -0.71058166f, -0.47196922f, -0.5218417f, 0.0f, -0.0856461f, 0.35830015f, 0.9296697f, 0.0f, -0.8279698f, -0.2043157f, 0.5222271f, 0.0f, 0.42794403f, 0.278166f, 0.8599346f, 0.0f, 0.539908f, -0.78571206f, -0.3019204f, 0.0f, 0.5678404f, -0.5495414f, -0.61283076f, 0.0f, -0.9896071f, 0.13656391f, -0.045034185f, 0.0f, -0.6154343f, -0.64408755f, 0.45430374f, 0.0f, 0.10742044f, -0.79463404f, 0.59750944f, 0.0f, -0.359545f, -0.888553f, 0.28495783f, 0.0f, -0.21804053f, 0.1529889f, 0.9638738f, 0.0f, -0.7277432f, -0.61640507f, -0.30072346f, 0.0f, 0.7249729f, -0.0066971947f, 0.68874484f, 0.0f, -0.5553659f, -0.5336586f, 0.6377908f, 0.0f, 0.5137558f, 0.79762083f, -0.316f, 0.0f, -0.3794025f, 0.92456084f, -0.035227515f, 0.0f, 0.82292485f, 0.27453658f, -0.49741766f, 0.0f, -0.5404114f, 0.60911417f, 0.5804614f, 0.0f, 0.8036582f, -0.27030295f, 0.5301602f, 0.0f, 0.60443187f, 0.68329686f, 0.40959433f, 0.0f, 0.06389989f, 0.96582085f, -0.2512108f, 0.0f, 0.10871133f, 0.74024713f, -0.6634878f, 0.0f, -0.7134277f, -0.6926784f, 0.10591285f, 0.0f, 0.64588976f, -0.57245487f, -0.50509584f, 0.0f, -0.6553931f, 0.73814714f, 0.15999562f, 0.0f, 0.39109614f, 0.91888714f, -0.05186756f, 0.0f, -0.48790225f, -0.5904377f, 0.64291114f, 0.0f, 0.601479f, 0.77074414f, -0.21018201f, 0.0f, -0.5677173f, 0.7511361f, 0.33688518f, 0.0f, 0.7858574f, 0.22667466f, 0.5753667f, 0.0f, -0.45203456f, -0.6042227f, -0.65618575f, 0.0f, 0.0022721163f, 0.4132844f, -0.9105992f, 0.0f, -0.58157516f, -0.5162926f, 0.6286591f, 0.0f, -0.03703705f, 0.8273786f, 0.5604221f, 0.0f, -0.51196927f, 0.79535437f, -0.324498f, 0.0f, -0.26824173f, -0.957229f, -0.10843876f, 0.0f, -0.23224828f, -0.9679131f, -0.09594243f, 0.0f, 0.3554329f, -0.8881506f, 0.29130062f, 0.0f, 0.73465204f, -0.4371373f, 0.5188423f, 0.0f, 0.998512f, 0.046590112f, -0.028339446f, 0.0f, -0.37276876f, -0.9082481f, 0.19007573f, 0.0f, 0.9173738f, -0.3483642f, 0.19252984f, 0.0f, 0.2714911f, 0.41475296f, -0.86848867f, 0.0f, 0.5131763f, -0.71163344f, 0.4798207f, 0.0f, -0.87373537f, 0.18886992f, -0.44823506f, 0.0f, 0.84600437f, -0.3725218f, 0.38145f, 0.0f, 0.89787275f, -0.17802091f, -0.40265754f, 0.0f, 0.21780656f, -0.9698323f, -0.10947895f, 0.0f, -0.15180314f, -0.7788918f, -0.6085091f, 0.0f, -0.2600385f, -0.4755398f, -0.840382f, 0.0f, 0.5723135f, -0.7474341f, -0.33734185f, 0.0f, -0.7174141f, 0.16990171f, -0.67561114f, 0.0f, -0.6841808f, 0.021457076f, -0.72899675f, 0.0f, -0.2007448f, 0.06555606f, -0.9774477f, 0.0f, -0.11488037f, -0.8044887f, 0.5827524f, 0.0f, -0.787035f, 0.03447489f, 0.6159443f, 0.0f, -0.20155965f, 0.68598723f, 0.69913894f, 0.0f, -0.085810825f, -0.10920836f, -0.99030805f, 0.0f, 0.5532693f, 0.73252505f, -0.39661077f, 0.0f, -0.18424894f, -0.9777375f, -0.100407675f, 0.0f, 0.07754738f, -0.9111506f, 0.40471104f, 0.0f, 0.13998385f, 0.7601631f, -0.63447344f, 0.0f, 0.44844192f, -0.84528923f, 0.29049253f, 0.0f};
    private static final int primeX = 501125321;
    private static final int primeY = 1136930381;
    private static final int primeZ = 1720413743;

    public FastNoiseLite() {
    }

    public FastNoiseLite(int seed) {
        this.setSeed(seed);
    }

    public void setSeed(int seed) {
        this.mSeed = seed;
    }

    public void setFrequency(float frequency) {
        this.mFrequency = frequency;
    }

    public void setNoiseType(NoiseType noiseType) {
        this.mNoiseType = noiseType;
        this.UpdateTransformType3D();
    }

    public void SetRotationType3D(RotationType3D rotationType3D) {
        this.mRotationType3D = rotationType3D;
        this.UpdateTransformType3D();
        this.UpdateWarpTransformType3D();
    }

    public void setFractalType(FractalType fractalType) {
        this.mFractalType = fractalType;
    }

    public void setFractalOctaves(int octaves) {
        this.mOctaves = octaves;
        this.CalculateFractalBounding();
    }

    public void SetFractalLacunarity(float lacunarity) {
        this.mLacunarity = lacunarity;
    }

    public void SetFractalGain(float gain) {
        this.mGain = gain;
        this.CalculateFractalBounding();
    }

    public void SetFractalWeightedStrength(float weightedStrength) {
        this.mWeightedStrength = weightedStrength;
    }

    public void SetFractalPingPongStrength(float pingPongStrength) {
        this.mPingPongStrength = pingPongStrength;
    }

    public void setCellularDistanceFunction(CellularDistanceFunction cellularDistanceFunction) {
        this.mCellularDistanceFunction = cellularDistanceFunction;
    }

    public void setCellularReturnType(CellularReturnType cellularReturnType) {
        this.mCellularReturnType = cellularReturnType;
    }

    public void setCellularJitter(float cellularJitter) {
        this.mCellularJitterModifier = cellularJitter;
    }

    public void setDomainWarpType(DomainWarpType domainWarpType) {
        this.mDomainWarpType = domainWarpType;
        this.UpdateWarpTransformType3D();
    }

    public void setDomainWarpAmp(float domainWarpAmp) {
        this.mDomainWarpAmp = domainWarpAmp;
    }

    public void setDomainWarpFreq(float domainWarpFreq) {
        this.mDomainWarpFreq = domainWarpFreq;
    }

    public float getNoise(double x, double y) {
        x *= (double)this.mFrequency;
        y *= (double)this.mFrequency;
        switch (this.mNoiseType.ordinal()) {
            case 0: 
            case 1: {
                double SQRT3 = 1.7320508075688772;
                double F2 = 0.3660254037844386;
                double t = (x + y) * 0.3660254037844386;
                x += t;
                y += t;
                break;
            }
        }
        return switch (this.mFractalType.ordinal()) {
            case 1 -> this.GenFractalFBm(x, y);
            case 2 -> this.GenFractalRidged(x, y);
            case 3 -> this.GenFractalPingPong(x, y);
            default -> this.GenNoiseSingle(this.mSeed, x, y);
        };
    }

    public float getNoise(double x, double y, double z) {
        x *= (double)this.mFrequency;
        y *= (double)this.mFrequency;
        z *= (double)this.mFrequency;
        switch (this.mTransformType3D.ordinal()) {
            case 1: {
                double xy = x + y;
                double s2 = xy * -0.211324865405187;
                x += s2 - (z *= 0.577350269189626);
                y = y + s2 - z;
                z += xy * 0.577350269189626;
                break;
            }
            case 2: {
                double xz = x + z;
                double s2 = xz * -0.211324865405187;
                x += s2 - (y *= 0.577350269189626);
                z += s2 - y;
                y += xz * 0.577350269189626;
                break;
            }
            case 3: {
                double R3 = 0.6666666666666666;
                double r = (x + y + z) * 0.6666666666666666;
                x = r - x;
                y = r - y;
                z = r - z;
                break;
            }
        }
        return switch (this.mFractalType.ordinal()) {
            case 1 -> this.GenFractalFBm(x, y, z);
            case 2 -> this.GenFractalRidged(x, y, z);
            case 3 -> this.GenFractalPingPong(x, y, z);
            default -> this.GenNoiseSingle(this.mSeed, x, y, z);
        };
    }

    public void DomainWarp(@Nonnull Vector2 coord) {
        switch (this.mFractalType.ordinal()) {
            default: {
                this.DomainWarpSingle(coord);
                break;
            }
            case 4: {
                this.DomainWarpFractalProgressive(coord);
                break;
            }
            case 5: {
                this.DomainWarpFractalIndependent(coord);
            }
        }
    }

    public void DomainWarp(@Nonnull Vector3 coord) {
        switch (this.mFractalType.ordinal()) {
            default: {
                this.DomainWarpSingle(coord);
                break;
            }
            case 4: {
                this.DomainWarpFractalProgressive(coord);
                break;
            }
            case 5: {
                this.DomainWarpFractalIndependent(coord);
            }
        }
    }

    private static float FastMin(float a, float b) {
        return a < b ? a : b;
    }

    private static float FastMax(float a, float b) {
        return a > b ? a : b;
    }

    private static float FastAbs(float f) {
        return f < 0.0f ? -f : f;
    }

    private static float FastSqrt(float f) {
        return (float)Math.sqrt(f);
    }

    private static int FastFloor(double f) {
        return f >= 0.0 ? (int)f : (int)f - 1;
    }

    public static int fastRound(double f) {
        return f >= 0.0 ? (int)(f + 0.5) : (int)(f - 0.5);
    }

    private static float Lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private static float InterpHermite(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    private static float InterpQuintic(float t) {
        return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
    }

    private static float CubicLerp(float a, float b, float c, float d, float t) {
        float p = d - c - (a - b);
        return t * t * t * p + t * t * (a - b - p) + t * (c - a) + b;
    }

    private static float PingPong(float t) {
        return (t -= (float)((int)(t * 0.5f) * 2)) < 1.0f ? t : 2.0f - t;
    }

    private void CalculateFractalBounding() {
        float gain;
        float amp = gain = FastNoiseLite.FastAbs(this.mGain);
        float ampFractal = 1.0f;
        for (int i = 1; i < this.mOctaves; ++i) {
            ampFractal += amp;
            amp *= gain;
        }
        this.mFractalBounding = 1.0f / ampFractal;
    }

    private static int hash(int seed, int xPrimed, int yPrimed) {
        int hash = seed ^ xPrimed ^ yPrimed;
        return hash *= 668265261;
    }

    private static int hash(int seed, int xPrimed, int yPrimed, int zPrimed) {
        int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed;
        return hash *= 668265261;
    }

    private static float ValCoord(int seed, int xPrimed, int yPrimed) {
        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed);
        hash *= hash;
        hash ^= hash << 19;
        return (float)hash * 4.656613E-10f;
    }

    private static float ValCoord(int seed, int xPrimed, int yPrimed, int zPrimed) {
        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed, zPrimed);
        hash *= hash;
        hash ^= hash << 19;
        return (float)hash * 4.656613E-10f;
    }

    private static float GradCoord(int seed, int xPrimed, int yPrimed, float xd, float yd) {
        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed);
        hash ^= hash >> 15;
        float xg = Gradients2D[hash &= 0xFE];
        float yg = Gradients2D[hash | 1];
        return xd * xg + yd * yg;
    }

    private static float GradCoord(int seed, int xPrimed, int yPrimed, int zPrimed, float xd, float yd, float zd) {
        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed, zPrimed);
        hash ^= hash >> 15;
        float xg = Gradients3D[hash &= 0xFC];
        float yg = Gradients3D[hash | 1];
        float zg = Gradients3D[hash | 2];
        return xd * xg + yd * yg + zd * zg;
    }

    private float GenNoiseSingle(int seed, double x, double y) {
        return switch (this.mNoiseType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.SingleSimplex(seed, x, y);
            case 1 -> this.SingleOpenSimplex2S(seed, x, y);
            case 2 -> this.SingleCellular(seed, x, y);
            case 3 -> this.SinglePerlin(seed, x, y);
            case 4 -> this.SingleValueCubic(seed, x, y);
            case 5 -> this.SingleValue(seed, x, y);
        };
    }

    private float GenNoiseSingle(int seed, double x, double y, double z) {
        return switch (this.mNoiseType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.SingleOpenSimplex2(seed, x, y, z);
            case 1 -> this.SingleOpenSimplex2S(seed, x, y, z);
            case 2 -> this.SingleCellular(seed, x, y, z);
            case 3 -> this.SinglePerlin(seed, x, y, z);
            case 4 -> this.SingleValueCubic(seed, x, y, z);
            case 5 -> this.SingleValue(seed, x, y, z);
        };
    }

    private void UpdateTransformType3D() {
        block0 : switch (this.mRotationType3D.ordinal()) {
            case 1: {
                this.mTransformType3D = TransformType3D.ImproveXYPlanes;
                break;
            }
            case 2: {
                this.mTransformType3D = TransformType3D.ImproveXZPlanes;
                break;
            }
            default: {
                switch (this.mNoiseType.ordinal()) {
                    case 0: 
                    case 1: {
                        this.mTransformType3D = TransformType3D.DefaultOpenSimplex2;
                        break block0;
                    }
                }
                this.mTransformType3D = TransformType3D.None;
            }
        }
    }

    private void UpdateWarpTransformType3D() {
        block0 : switch (this.mRotationType3D.ordinal()) {
            case 1: {
                this.mWarpTransformType3D = TransformType3D.ImproveXYPlanes;
                break;
            }
            case 2: {
                this.mWarpTransformType3D = TransformType3D.ImproveXZPlanes;
                break;
            }
            default: {
                switch (this.mDomainWarpType.ordinal()) {
                    case 0: 
                    case 1: {
                        this.mWarpTransformType3D = TransformType3D.DefaultOpenSimplex2;
                        break block0;
                    }
                }
                this.mWarpTransformType3D = TransformType3D.None;
            }
        }
    }

    private float GenFractalFBm(double x, double y) {
        int seed = this.mSeed;
        float sum = 0.0f;
        float amp = this.mFractalBounding;
        for (int i = 0; i < this.mOctaves; ++i) {
            float noise = this.GenNoiseSingle(seed++, x, y);
            sum += noise * amp;
            amp *= FastNoiseLite.Lerp(1.0f, FastNoiseLite.FastMin(noise + 1.0f, 2.0f) * 0.5f, this.mWeightedStrength);
            x *= (double)this.mLacunarity;
            y *= (double)this.mLacunarity;
            amp *= this.mGain;
        }
        return sum;
    }

    private float GenFractalFBm(double x, double y, double z) {
        int seed = this.mSeed;
        float sum = 0.0f;
        float amp = this.mFractalBounding;
        for (int i = 0; i < this.mOctaves; ++i) {
            float noise = this.GenNoiseSingle(seed++, x, y, z);
            sum += noise * amp;
            amp *= FastNoiseLite.Lerp(1.0f, (noise + 1.0f) * 0.5f, this.mWeightedStrength);
            x *= (double)this.mLacunarity;
            y *= (double)this.mLacunarity;
            z *= (double)this.mLacunarity;
            amp *= this.mGain;
        }
        return sum;
    }

    private float GenFractalRidged(double x, double y) {
        int seed = this.mSeed;
        float sum = 0.0f;
        float amp = this.mFractalBounding;
        for (int i = 0; i < this.mOctaves; ++i) {
            float noise = FastNoiseLite.FastAbs(this.GenNoiseSingle(seed++, x, y));
            sum += (noise * -2.0f + 1.0f) * amp;
            amp *= FastNoiseLite.Lerp(1.0f, 1.0f - noise, this.mWeightedStrength);
            x *= (double)this.mLacunarity;
            y *= (double)this.mLacunarity;
            amp *= this.mGain;
        }
        return sum;
    }

    private float GenFractalRidged(double x, double y, double z) {
        int seed = this.mSeed;
        float sum = 0.0f;
        float amp = this.mFractalBounding;
        for (int i = 0; i < this.mOctaves; ++i) {
            float noise = FastNoiseLite.FastAbs(this.GenNoiseSingle(seed++, x, y, z));
            sum += (noise * -2.0f + 1.0f) * amp;
            amp *= FastNoiseLite.Lerp(1.0f, 1.0f - noise, this.mWeightedStrength);
            x *= (double)this.mLacunarity;
            y *= (double)this.mLacunarity;
            z *= (double)this.mLacunarity;
            amp *= this.mGain;
        }
        return sum;
    }

    private float GenFractalPingPong(double x, double y) {
        int seed = this.mSeed;
        float sum = 0.0f;
        float amp = this.mFractalBounding;
        for (int i = 0; i < this.mOctaves; ++i) {
            float noise = FastNoiseLite.PingPong((this.GenNoiseSingle(seed++, x, y) + 1.0f) * this.mPingPongStrength);
            sum += (noise - 0.5f) * 2.0f * amp;
            amp *= FastNoiseLite.Lerp(1.0f, noise, this.mWeightedStrength);
            x *= (double)this.mLacunarity;
            y *= (double)this.mLacunarity;
            amp *= this.mGain;
        }
        return sum;
    }

    private float GenFractalPingPong(double x, double y, double z) {
        int seed = this.mSeed;
        float sum = 0.0f;
        float amp = this.mFractalBounding;
        for (int i = 0; i < this.mOctaves; ++i) {
            float noise = FastNoiseLite.PingPong((this.GenNoiseSingle(seed++, x, y, z) + 1.0f) * this.mPingPongStrength);
            sum += (noise - 0.5f) * 2.0f * amp;
            amp *= FastNoiseLite.Lerp(1.0f, noise, this.mWeightedStrength);
            x *= (double)this.mLacunarity;
            y *= (double)this.mLacunarity;
            z *= (double)this.mLacunarity;
            amp *= this.mGain;
        }
        return sum;
    }

    private float SingleSimplex(int seed, double x, double y) {
        float y1;
        float x1;
        float b;
        float n2;
        float SQRT3 = 1.7320508f;
        float G2 = 0.21132487f;
        int i = FastNoiseLite.FastFloor(x);
        int j = FastNoiseLite.FastFloor(y);
        float xi = (float)(x - (double)i);
        float yi = (float)(y - (double)j);
        float t = (xi + yi) * 0.21132487f;
        float x0 = xi - t;
        float y0 = yi - t;
        float a = 0.5f - x0 * x0 - y0 * y0;
        float n0 = a <= 0.0f ? 0.0f : a * a * (a * a) * FastNoiseLite.GradCoord(seed, i *= 501125321, j *= 1136930381, x0, y0);
        float c = 3.1547005f * t + (-0.6666666f + a);
        if (c <= 0.0f) {
            n2 = 0.0f;
        } else {
            float x2 = x0 + -0.57735026f;
            float y2 = y0 + -0.57735026f;
            n2 = c * c * (c * c) * FastNoiseLite.GradCoord(seed, i + 501125321, j + 1136930381, x2, y2);
        }
        float n1 = y0 > x0 ? ((b = 0.5f - (x1 = x0 + 0.21132487f) * x1 - (y1 = y0 + -0.7886751f) * y1) <= 0.0f ? 0.0f : b * b * (b * b) * FastNoiseLite.GradCoord(seed, i, j + 1136930381, x1, y1)) : ((b = 0.5f - (x1 = x0 + -0.7886751f) * x1 - (y1 = y0 + 0.21132487f) * y1) <= 0.0f ? 0.0f : b * b * (b * b) * FastNoiseLite.GradCoord(seed, i + 501125321, j, x1, y1));
        return (n0 + n1 + n2) * 99.83685f;
    }

    private float SingleOpenSimplex2(int seed, double x, double y, double z) {
        int i = FastNoiseLite.fastRound(x);
        int j = FastNoiseLite.fastRound(y);
        int k = FastNoiseLite.fastRound(z);
        float x0 = (float)(x - (double)i);
        float y0 = (float)(y - (double)j);
        float z0 = (float)(z - (double)k);
        int xNSign = (int)(-1.0f - x0) | 1;
        int yNSign = (int)(-1.0f - y0) | 1;
        int zNSign = (int)(-1.0f - z0) | 1;
        float ax0 = (float)xNSign * -x0;
        float ay0 = (float)yNSign * -y0;
        float az0 = (float)zNSign * -z0;
        i *= 501125321;
        j *= 1136930381;
        k *= 1720413743;
        float value = 0.0f;
        float a = 0.6f - x0 * x0 - (y0 * y0 + z0 * z0);
        int l = 0;
        while (true) {
            if (a > 0.0f) {
                value += a * a * (a * a) * FastNoiseLite.GradCoord(seed, i, j, k, x0, y0, z0);
            }
            if (ax0 >= ay0 && ax0 >= az0) {
                b = a + ax0 + ax0;
                if (b > 1.0f) {
                    value += (b -= 1.0f) * b * (b * b) * FastNoiseLite.GradCoord(seed, i - xNSign * 501125321, j, k, x0 + (float)xNSign, y0, z0);
                }
            } else if (ay0 > ax0 && ay0 >= az0) {
                b = a + ay0 + ay0;
                if (b > 1.0f) {
                    value += (b -= 1.0f) * b * (b * b) * FastNoiseLite.GradCoord(seed, i, j - yNSign * 1136930381, k, x0, y0 + (float)yNSign, z0);
                }
            } else {
                b = a + az0 + az0;
                if (b > 1.0f) {
                    value += (b -= 1.0f) * b * (b * b) * FastNoiseLite.GradCoord(seed, i, j, k - zNSign * 1720413743, x0, y0, z0 + (float)zNSign);
                }
            }
            if (l == 1) break;
            ax0 = 0.5f - ax0;
            ay0 = 0.5f - ay0;
            az0 = 0.5f - az0;
            x0 = (float)xNSign * ax0;
            y0 = (float)yNSign * ay0;
            z0 = (float)zNSign * az0;
            a += 0.75f - ax0 - (ay0 + az0);
            i += xNSign >> 1 & 0x1DDE90C9;
            j += yNSign >> 1 & 0x43C42E4D;
            k += zNSign >> 1 & 0x668B6E2F;
            xNSign = -xNSign;
            yNSign = -yNSign;
            zNSign = -zNSign;
            seed ^= 0xFFFFFFFF;
            ++l;
        }
        return value * 32.694283f;
    }

    private float SingleOpenSimplex2S(int seed, double x, double y) {
        double SQRT3 = 1.7320508075688772;
        double G2 = 0.21132486540518713;
        int i = FastNoiseLite.FastFloor(x);
        int j = FastNoiseLite.FastFloor(y);
        float xi = (float)(x - (double)i);
        float yi = (float)(y - (double)j);
        int i1 = (i *= 501125321) + 501125321;
        int j1 = (j *= 1136930381) + 1136930381;
        float t = (xi + yi) * 0.21132487f;
        float x0 = xi - t;
        float y0 = yi - t;
        float a0 = 0.6666667f - x0 * x0 - y0 * y0;
        float value = a0 * a0 * (a0 * a0) * FastNoiseLite.GradCoord(seed, i, j, x0, y0);
        float a1 = 3.1547005f * t + (-0.6666667f + a0);
        float x1 = x0 - 0.57735026f;
        float y1 = y0 - 0.57735026f;
        value += a1 * a1 * (a1 * a1) * FastNoiseLite.GradCoord(seed, i1, j1, x1, y1);
        float xmyi = xi - yi;
        if ((double)t > 0.21132486540518713) {
            if (xi + xmyi > 1.0f) {
                x2 = x0 + -1.3660254f;
                y2 = y0 + -0.36602542f;
                a2 = 0.6666667f - x2 * x2 - y2 * y2;
                if (a2 > 0.0f) {
                    value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i + 1002250642, j + 1136930381, x2, y2);
                }
            } else {
                x2 = x0 + 0.21132487f;
                y2 = y0 + -0.7886751f;
                a2 = 0.6666667f - x2 * x2 - y2 * y2;
                if (a2 > 0.0f) {
                    value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i, j + 1136930381, x2, y2);
                }
            }
            if (yi - xmyi > 1.0f) {
                x3 = x0 + -0.36602542f;
                y3 = y0 + -1.3660254f;
                a3 = 0.6666667f - x3 * x3 - y3 * y3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * FastNoiseLite.GradCoord(seed, i + 501125321, j + -2021106534, x3, y3);
                }
            } else {
                x3 = x0 + -0.7886751f;
                y3 = y0 + 0.21132487f;
                a3 = 0.6666667f - x3 * x3 - y3 * y3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * FastNoiseLite.GradCoord(seed, i + 501125321, j, x3, y3);
                }
            }
        } else {
            float a2;
            float y2;
            float x2;
            if (xi + xmyi < 0.0f) {
                x2 = x0 + 0.7886751f;
                y2 = y0 - 0.21132487f;
                a2 = 0.6666667f - x2 * x2 - y2 * y2;
                if (a2 > 0.0f) {
                    value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i - 501125321, j, x2, y2);
                }
            } else {
                x2 = x0 + -0.7886751f;
                y2 = y0 + 0.21132487f;
                a2 = 0.6666667f - x2 * x2 - y2 * y2;
                if (a2 > 0.0f) {
                    value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i + 501125321, j, x2, y2);
                }
            }
            if (yi < xmyi) {
                x2 = x0 - 0.21132487f;
                y2 = y0 - -0.7886751f;
                a2 = 0.6666667f - x2 * x2 - y2 * y2;
                if (a2 > 0.0f) {
                    value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i, j - 1136930381, x2, y2);
                }
            } else {
                x2 = x0 + 0.21132487f;
                y2 = y0 + -0.7886751f;
                a2 = 0.6666667f - x2 * x2 - y2 * y2;
                if (a2 > 0.0f) {
                    value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i, j + 1136930381, x2, y2);
                }
            }
        }
        return value * 18.241962f;
    }

    private float SingleOpenSimplex2S(int seed, double x, double y, double z) {
        float aD;
        float a9;
        float a5;
        int i = FastNoiseLite.FastFloor(x);
        int j = FastNoiseLite.FastFloor(y);
        int k = FastNoiseLite.FastFloor(z);
        float xi = (float)(x - (double)i);
        float yi = (float)(y - (double)j);
        float zi = (float)(z - (double)k);
        i *= 501125321;
        j *= 1136930381;
        k *= 1720413743;
        int seed2 = seed + 1293373;
        int xNMask = (int)(-0.5f - xi);
        int yNMask = (int)(-0.5f - yi);
        int zNMask = (int)(-0.5f - zi);
        float x0 = xi + (float)xNMask;
        float y0 = yi + (float)yNMask;
        float z0 = zi + (float)zNMask;
        float a0 = 0.75f - x0 * x0 - y0 * y0 - z0 * z0;
        float value = a0 * a0 * (a0 * a0) * FastNoiseLite.GradCoord(seed, i + (xNMask & 0x1DDE90C9), j + (yNMask & 0x43C42E4D), k + (zNMask & 0x668B6E2F), x0, y0, z0);
        float x1 = xi - 0.5f;
        float y1 = yi - 0.5f;
        float z1 = zi - 0.5f;
        float a1 = 0.75f - x1 * x1 - y1 * y1 - z1 * z1;
        value += a1 * a1 * (a1 * a1) * FastNoiseLite.GradCoord(seed2, i + 501125321, j + 1136930381, k + 1720413743, x1, y1, z1);
        float xAFlipMask0 = (float)((xNMask | 1) << 1) * x1;
        float yAFlipMask0 = (float)((yNMask | 1) << 1) * y1;
        float zAFlipMask0 = (float)((zNMask | 1) << 1) * z1;
        float xAFlipMask1 = (float)(-2 - (xNMask << 2)) * x1 - 1.0f;
        float yAFlipMask1 = (float)(-2 - (yNMask << 2)) * y1 - 1.0f;
        float zAFlipMask1 = (float)(-2 - (zNMask << 2)) * z1 - 1.0f;
        boolean skip5 = false;
        float a2 = xAFlipMask0 + a0;
        if (a2 > 0.0f) {
            float x2 = x0 - (float)(xNMask | 1);
            float y2 = y0;
            float z2 = z0;
            value += a2 * a2 * (a2 * a2) * FastNoiseLite.GradCoord(seed, i + (~xNMask & 0x1DDE90C9), j + (yNMask & 0x43C42E4D), k + (zNMask & 0x668B6E2F), x2, y2, z2);
        } else {
            float a4;
            float a3 = yAFlipMask0 + zAFlipMask0 + a0;
            if (a3 > 0.0f) {
                float x3 = x0;
                float y3 = y0 - (float)(yNMask | 1);
                float z3 = z0 - (float)(zNMask | 1);
                value += a3 * a3 * (a3 * a3) * FastNoiseLite.GradCoord(seed, i + (xNMask & 0x1DDE90C9), j + (~yNMask & 0x43C42E4D), k + (~zNMask & 0x668B6E2F), x3, y3, z3);
            }
            if ((a4 = xAFlipMask1 + a1) > 0.0f) {
                float x4 = (float)(xNMask | 1) + x1;
                float y4 = y1;
                float z4 = z1;
                value += a4 * a4 * (a4 * a4) * FastNoiseLite.GradCoord(seed2, i + (xNMask & 0x3BBD2192), j + 1136930381, k + 1720413743, x4, y4, z4);
                skip5 = true;
            }
        }
        boolean skip9 = false;
        float a6 = yAFlipMask0 + a0;
        if (a6 > 0.0f) {
            float x6 = x0;
            float y6 = y0 - (float)(yNMask | 1);
            float z6 = z0;
            value += a6 * a6 * (a6 * a6) * FastNoiseLite.GradCoord(seed, i + (xNMask & 0x1DDE90C9), j + (~yNMask & 0x43C42E4D), k + (zNMask & 0x668B6E2F), x6, y6, z6);
        } else {
            float a8;
            float a7 = xAFlipMask0 + zAFlipMask0 + a0;
            if (a7 > 0.0f) {
                float x7 = x0 - (float)(xNMask | 1);
                float y7 = y0;
                float z7 = z0 - (float)(zNMask | 1);
                value += a7 * a7 * (a7 * a7) * FastNoiseLite.GradCoord(seed, i + (~xNMask & 0x1DDE90C9), j + (yNMask & 0x43C42E4D), k + (~zNMask & 0x668B6E2F), x7, y7, z7);
            }
            if ((a8 = yAFlipMask1 + a1) > 0.0f) {
                float x8 = x1;
                float y8 = (float)(yNMask | 1) + y1;
                float z8 = z1;
                value += a8 * a8 * (a8 * a8) * FastNoiseLite.GradCoord(seed2, i + 501125321, j + (yNMask & 0x87885C9A), k + 1720413743, x8, y8, z8);
                skip9 = true;
            }
        }
        boolean skipD = false;
        float aA = zAFlipMask0 + a0;
        if (aA > 0.0f) {
            float xA = x0;
            float yA = y0;
            float zA = z0 - (float)(zNMask | 1);
            value += aA * aA * (aA * aA) * FastNoiseLite.GradCoord(seed, i + (xNMask & 0x1DDE90C9), j + (yNMask & 0x43C42E4D), k + (~zNMask & 0x668B6E2F), xA, yA, zA);
        } else {
            float aC;
            float aB = xAFlipMask0 + yAFlipMask0 + a0;
            if (aB > 0.0f) {
                float xB = x0 - (float)(xNMask | 1);
                float yB = y0 - (float)(yNMask | 1);
                float zB = z0;
                value += aB * aB * (aB * aB) * FastNoiseLite.GradCoord(seed, i + (~xNMask & 0x1DDE90C9), j + (~yNMask & 0x43C42E4D), k + (zNMask & 0x668B6E2F), xB, yB, zB);
            }
            if ((aC = zAFlipMask1 + a1) > 0.0f) {
                float xC = x1;
                float yC = y1;
                float zC = (float)(zNMask | 1) + z1;
                value += aC * aC * (aC * aC) * FastNoiseLite.GradCoord(seed2, i + 501125321, j + 1136930381, k + (zNMask & 0xCD16DC5E), xC, yC, zC);
                skipD = true;
            }
        }
        if (!skip5 && (a5 = yAFlipMask1 + zAFlipMask1 + a1) > 0.0f) {
            float x5 = x1;
            float y5 = (float)(yNMask | 1) + y1;
            float z5 = (float)(zNMask | 1) + z1;
            value += a5 * a5 * (a5 * a5) * FastNoiseLite.GradCoord(seed2, i + 501125321, j + (yNMask & 0x87885C9A), k + (zNMask & 0xCD16DC5E), x5, y5, z5);
        }
        if (!skip9 && (a9 = xAFlipMask1 + zAFlipMask1 + a1) > 0.0f) {
            float x9 = (float)(xNMask | 1) + x1;
            float y9 = y1;
            float z9 = (float)(zNMask | 1) + z1;
            value += a9 * a9 * (a9 * a9) * FastNoiseLite.GradCoord(seed2, i + (xNMask & 0x3BBD2192), j + 1136930381, k + (zNMask & 0xCD16DC5E), x9, y9, z9);
        }
        if (!skipD && (aD = xAFlipMask1 + yAFlipMask1 + a1) > 0.0f) {
            float xD = (float)(xNMask | 1) + x1;
            float yD = (float)(yNMask | 1) + y1;
            float zD = z1;
            value += aD * aD * (aD * aD) * FastNoiseLite.GradCoord(seed2, i + (xNMask & 0x3BBD2192), j + (yNMask & 0x87885C9A), k + 1720413743, xD, yD, zD);
        }
        return value * 9.046026f;
    }

    @Nonnull
    public Vector3d pointFor(int seed, double jitter, double x, double y, double z) {
        int xr = FastNoiseLite.fastRound(x);
        int yr = FastNoiseLite.fastRound(y);
        int zr = FastNoiseLite.fastRound(z);
        int hash = FastNoiseLite.hash(seed, xr, yr, zr);
        int idx = hash & 0x3FC;
        double vecX = (double)randVecs3D[idx] * jitter + (double)xr;
        double vecY = (double)randVecs3D[idx | 1] * jitter + (double)yr;
        double vecZ = (double)randVecs3D[idx | 2] * jitter + (double)zr;
        return new Vector3d(vecX, vecY, vecZ);
    }

    @Nonnull
    public Vector2d pointFor(int seed, double jitter, double x, double y) {
        int xr = FastNoiseLite.fastRound(x);
        int yr = FastNoiseLite.fastRound(y);
        int hash = FastNoiseLite.hash(seed, xr, yr);
        int idx = hash & 0x3FC;
        double vecX = (double)randVecs3D[idx] * jitter + (double)xr;
        double vecY = (double)randVecs3D[idx | 1] * jitter + (double)yr;
        return new Vector2d(vecX, vecY);
    }

    public double pointFor(int seed, double jitter, double x) {
        int xr = FastNoiseLite.fastRound(x);
        int hash = FastNoiseLite.hash(seed, xr, 0);
        int idx = hash & 0x3FC;
        return (double)randVecs3D[idx] * jitter + (double)xr;
    }

    public float SingleCellular(int seed, double x, double y) {
        int xr = FastNoiseLite.fastRound(x);
        int yr = FastNoiseLite.fastRound(y);
        float distance0 = Float.MAX_VALUE;
        float distance1 = Float.MAX_VALUE;
        int closestHash = 0;
        float cellularJitter = 0.5f * this.mCellularJitterModifier;
        int xPrimed = (xr - 1) * 501125321;
        int yPrimedBase = (yr - 1) * 1136930381;
        switch (this.mCellularDistanceFunction.ordinal()) {
            default: {
                for (int xi = xr - 1; xi <= xr + 1; ++xi) {
                    int yPrimed = yPrimedBase;
                    for (int yi = yr - 1; yi <= yr + 1; ++yi) {
                        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed);
                        int idx = hash & 0x1FE;
                        float vecX = (float)((double)xi - x) + RandVecs2D[idx] * cellularJitter;
                        float vecY = (float)((double)yi - y) + RandVecs2D[idx | 1] * cellularJitter;
                        float newDistance = vecX * vecX + vecY * vecY;
                        distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                        if (newDistance < distance0) {
                            distance0 = newDistance;
                            closestHash = hash;
                        }
                        yPrimed += 1136930381;
                    }
                    xPrimed += 501125321;
                }
                break;
            }
            case 2: {
                for (int xi = xr - 1; xi <= xr + 1; ++xi) {
                    int yPrimed = yPrimedBase;
                    for (int yi = yr - 1; yi <= yr + 1; ++yi) {
                        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed);
                        int idx = hash & 0x1FE;
                        float vecX = (float)((double)xi - x) + RandVecs2D[idx] * cellularJitter;
                        float vecY = (float)((double)yi - y) + RandVecs2D[idx | 1] * cellularJitter;
                        float newDistance = FastNoiseLite.FastAbs(vecX) + FastNoiseLite.FastAbs(vecY);
                        distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                        if (newDistance < distance0) {
                            distance0 = newDistance;
                            closestHash = hash;
                        }
                        yPrimed += 1136930381;
                    }
                    xPrimed += 501125321;
                }
                break;
            }
            case 3: {
                for (int xi = xr - 1; xi <= xr + 1; ++xi) {
                    int yPrimed = yPrimedBase;
                    for (int yi = yr - 1; yi <= yr + 1; ++yi) {
                        int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed);
                        int idx = hash & 0x1FE;
                        float vecX = (float)((double)xi - x) + RandVecs2D[idx] * cellularJitter;
                        float vecY = (float)((double)yi - y) + RandVecs2D[idx | 1] * cellularJitter;
                        float newDistance = FastNoiseLite.FastAbs(vecX) + FastNoiseLite.FastAbs(vecY) + (vecX * vecX + vecY * vecY);
                        distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                        if (newDistance < distance0) {
                            distance0 = newDistance;
                            closestHash = hash;
                        }
                        yPrimed += 1136930381;
                    }
                    xPrimed += 501125321;
                }
            }
        }
        if (this.mCellularDistanceFunction == CellularDistanceFunction.Euclidean && this.mCellularReturnType != CellularReturnType.CellValue) {
            distance0 = FastNoiseLite.FastSqrt(distance0);
            if (this.mCellularReturnType != CellularReturnType.Distance) {
                distance1 = FastNoiseLite.FastSqrt(distance1);
            }
        }
        return switch (this.mCellularReturnType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> (float)closestHash * 4.656613E-10f;
            case 1 -> distance0 - 1.0f;
            case 2 -> distance1 - 1.0f;
            case 3 -> (distance1 + distance0) * 0.5f - 1.0f;
            case 4 -> distance1 - distance0 - 1.0f;
            case 5 -> distance1 * distance0 * 0.5f - 1.0f;
            case 6 -> distance0 / distance1 - 1.0f;
        };
    }

    private float SingleCellular(int seed, double x, double y, double z) {
        int xr = FastNoiseLite.fastRound(x);
        int yr = FastNoiseLite.fastRound(y);
        int zr = FastNoiseLite.fastRound(z);
        float distance0 = Float.MAX_VALUE;
        float distance1 = Float.MAX_VALUE;
        int closestHash = 0;
        float cellularJitter = 0.5f * this.mCellularJitterModifier;
        int xPrimed = (xr - 1) * 501125321;
        int yPrimedBase = (yr - 1) * 1136930381;
        int zPrimedBase = (zr - 1) * 1720413743;
        switch (this.mCellularDistanceFunction.ordinal()) {
            case 0: 
            case 1: {
                for (int xi = xr - 1; xi <= xr + 1; ++xi) {
                    int yPrimed = yPrimedBase;
                    for (int yi = yr - 1; yi <= yr + 1; ++yi) {
                        int zPrimed = zPrimedBase;
                        for (int zi = zr - 1; zi <= zr + 1; ++zi) {
                            int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed, zPrimed);
                            int idx = hash & 0x3FC;
                            float vecX = (float)((double)xi - x) + randVecs3D[idx] * cellularJitter;
                            float vecY = (float)((double)yi - y) + randVecs3D[idx | 1] * cellularJitter;
                            float vecZ = (float)((double)zi - z) + randVecs3D[idx | 2] * cellularJitter;
                            float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;
                            distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                            if (newDistance < distance0) {
                                distance0 = newDistance;
                                closestHash = hash;
                            }
                            zPrimed += 1720413743;
                        }
                        yPrimed += 1136930381;
                    }
                    xPrimed += 501125321;
                }
                break;
            }
            case 2: {
                for (int xi = xr - 1; xi <= xr + 1; ++xi) {
                    int yPrimed = yPrimedBase;
                    for (int yi = yr - 1; yi <= yr + 1; ++yi) {
                        int zPrimed = zPrimedBase;
                        for (int zi = zr - 1; zi <= zr + 1; ++zi) {
                            int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed, zPrimed);
                            int idx = hash & 0x3FC;
                            float vecX = (float)((double)xi - x) + randVecs3D[idx] * cellularJitter;
                            float vecY = (float)((double)yi - y) + randVecs3D[idx | 1] * cellularJitter;
                            float vecZ = (float)((double)zi - z) + randVecs3D[idx | 2] * cellularJitter;
                            float newDistance = FastNoiseLite.FastAbs(vecX) + FastNoiseLite.FastAbs(vecY) + FastNoiseLite.FastAbs(vecZ);
                            distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                            if (newDistance < distance0) {
                                distance0 = newDistance;
                                closestHash = hash;
                            }
                            zPrimed += 1720413743;
                        }
                        yPrimed += 1136930381;
                    }
                    xPrimed += 501125321;
                }
                break;
            }
            case 3: {
                for (int xi = xr - 1; xi <= xr + 1; ++xi) {
                    int yPrimed = yPrimedBase;
                    for (int yi = yr - 1; yi <= yr + 1; ++yi) {
                        int zPrimed = zPrimedBase;
                        for (int zi = zr - 1; zi <= zr + 1; ++zi) {
                            int hash = FastNoiseLite.hash(seed, xPrimed, yPrimed, zPrimed);
                            int idx = hash & 0x3FC;
                            float vecX = (float)((double)xi - x) + randVecs3D[idx] * cellularJitter;
                            float vecY = (float)((double)yi - y) + randVecs3D[idx | 1] * cellularJitter;
                            float vecZ = (float)((double)zi - z) + randVecs3D[idx | 2] * cellularJitter;
                            float newDistance = FastNoiseLite.FastAbs(vecX) + FastNoiseLite.FastAbs(vecY) + FastNoiseLite.FastAbs(vecZ) + (vecX * vecX + vecY * vecY + vecZ * vecZ);
                            distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                            if (newDistance < distance0) {
                                distance0 = newDistance;
                                closestHash = hash;
                            }
                            zPrimed += 1720413743;
                        }
                        yPrimed += 1136930381;
                    }
                    xPrimed += 501125321;
                }
                break;
            }
        }
        if (this.mCellularDistanceFunction == CellularDistanceFunction.Euclidean && this.mCellularReturnType != CellularReturnType.CellValue) {
            distance0 = FastNoiseLite.FastSqrt(distance0);
            if (this.mCellularReturnType != CellularReturnType.Distance) {
                distance1 = FastNoiseLite.FastSqrt(distance1);
            }
        }
        return switch (this.mCellularReturnType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> (float)closestHash * 4.656613E-10f;
            case 1 -> distance0 - 1.0f;
            case 2 -> distance1 - 1.0f;
            case 3 -> (distance1 + distance0) * 0.5f - 1.0f;
            case 4 -> distance1 - distance0 - 1.0f;
            case 5 -> distance1 * distance0 * 0.5f - 1.0f;
            case 6 -> distance0 / distance1 - 1.0f;
        };
    }

    private float SinglePerlin(int seed, double x, double y) {
        int x0 = FastNoiseLite.FastFloor(x);
        int y0 = FastNoiseLite.FastFloor(y);
        float xd0 = (float)(x - (double)x0);
        float yd0 = (float)(y - (double)y0);
        float xd1 = xd0 - 1.0f;
        float yd1 = yd0 - 1.0f;
        float xs = FastNoiseLite.InterpQuintic(xd0);
        float ys = FastNoiseLite.InterpQuintic(yd0);
        int x1 = (x0 *= 501125321) + 501125321;
        int y1 = (y0 *= 1136930381) + 1136930381;
        float xf0 = FastNoiseLite.Lerp(FastNoiseLite.GradCoord(seed, x0, y0, xd0, yd0), FastNoiseLite.GradCoord(seed, x1, y0, xd1, yd0), xs);
        float xf1 = FastNoiseLite.Lerp(FastNoiseLite.GradCoord(seed, x0, y1, xd0, yd1), FastNoiseLite.GradCoord(seed, x1, y1, xd1, yd1), xs);
        return FastNoiseLite.Lerp(xf0, xf1, ys) * 1.4247692f;
    }

    private float SinglePerlin(int seed, double x, double y, double z) {
        int x0 = FastNoiseLite.FastFloor(x);
        int y0 = FastNoiseLite.FastFloor(y);
        int z0 = FastNoiseLite.FastFloor(z);
        float xd0 = (float)(x - (double)x0);
        float yd0 = (float)(y - (double)y0);
        float zd0 = (float)(z - (double)z0);
        float xd1 = xd0 - 1.0f;
        float yd1 = yd0 - 1.0f;
        float zd1 = zd0 - 1.0f;
        float xs = FastNoiseLite.InterpQuintic(xd0);
        float ys = FastNoiseLite.InterpQuintic(yd0);
        float zs = FastNoiseLite.InterpQuintic(zd0);
        int x1 = (x0 *= 501125321) + 501125321;
        int y1 = (y0 *= 1136930381) + 1136930381;
        int z1 = (z0 *= 1720413743) + 1720413743;
        float xf00 = FastNoiseLite.Lerp(FastNoiseLite.GradCoord(seed, x0, y0, z0, xd0, yd0, zd0), FastNoiseLite.GradCoord(seed, x1, y0, z0, xd1, yd0, zd0), xs);
        float xf10 = FastNoiseLite.Lerp(FastNoiseLite.GradCoord(seed, x0, y1, z0, xd0, yd1, zd0), FastNoiseLite.GradCoord(seed, x1, y1, z0, xd1, yd1, zd0), xs);
        float xf01 = FastNoiseLite.Lerp(FastNoiseLite.GradCoord(seed, x0, y0, z1, xd0, yd0, zd1), FastNoiseLite.GradCoord(seed, x1, y0, z1, xd1, yd0, zd1), xs);
        float xf11 = FastNoiseLite.Lerp(FastNoiseLite.GradCoord(seed, x0, y1, z1, xd0, yd1, zd1), FastNoiseLite.GradCoord(seed, x1, y1, z1, xd1, yd1, zd1), xs);
        float yf0 = FastNoiseLite.Lerp(xf00, xf10, ys);
        float yf1 = FastNoiseLite.Lerp(xf01, xf11, ys);
        return FastNoiseLite.Lerp(yf0, yf1, zs) * 0.9649214f;
    }

    private float SingleValueCubic(int seed, double x, double y) {
        int x1 = FastNoiseLite.FastFloor(x);
        int y1 = FastNoiseLite.FastFloor(y);
        float xs = (float)(x - (double)x1);
        float ys = (float)(y - (double)y1);
        int x0 = (x1 *= 501125321) - 501125321;
        int y0 = (y1 *= 1136930381) - 1136930381;
        int x2 = x1 + 501125321;
        int y2 = y1 + 1136930381;
        int x3 = x1 + 1002250642;
        int y3 = y1 + -2021106534;
        return FastNoiseLite.CubicLerp(FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y0), FastNoiseLite.ValCoord(seed, x1, y0), FastNoiseLite.ValCoord(seed, x2, y0), FastNoiseLite.ValCoord(seed, x3, y0), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y1), FastNoiseLite.ValCoord(seed, x1, y1), FastNoiseLite.ValCoord(seed, x2, y1), FastNoiseLite.ValCoord(seed, x3, y1), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y2), FastNoiseLite.ValCoord(seed, x1, y2), FastNoiseLite.ValCoord(seed, x2, y2), FastNoiseLite.ValCoord(seed, x3, y2), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y3), FastNoiseLite.ValCoord(seed, x1, y3), FastNoiseLite.ValCoord(seed, x2, y3), FastNoiseLite.ValCoord(seed, x3, y3), xs), ys) * 0.44444445f;
    }

    private float SingleValueCubic(int seed, double x, double y, double z) {
        int x1 = FastNoiseLite.FastFloor(x);
        int y1 = FastNoiseLite.FastFloor(y);
        int z1 = FastNoiseLite.FastFloor(z);
        float xs = (float)(x - (double)x1);
        float ys = (float)(y - (double)y1);
        float zs = (float)(z - (double)z1);
        int x0 = (x1 *= 501125321) - 501125321;
        int y0 = (y1 *= 1136930381) - 1136930381;
        int z0 = (z1 *= 1720413743) - 1720413743;
        int x2 = x1 + 501125321;
        int y2 = y1 + 1136930381;
        int z2 = z1 + 1720413743;
        int x3 = x1 + 1002250642;
        int y3 = y1 + -2021106534;
        int z3 = z1 + -854139810;
        return FastNoiseLite.CubicLerp(FastNoiseLite.CubicLerp(FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y0, z0), FastNoiseLite.ValCoord(seed, x1, y0, z0), FastNoiseLite.ValCoord(seed, x2, y0, z0), FastNoiseLite.ValCoord(seed, x3, y0, z0), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y1, z0), FastNoiseLite.ValCoord(seed, x1, y1, z0), FastNoiseLite.ValCoord(seed, x2, y1, z0), FastNoiseLite.ValCoord(seed, x3, y1, z0), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y2, z0), FastNoiseLite.ValCoord(seed, x1, y2, z0), FastNoiseLite.ValCoord(seed, x2, y2, z0), FastNoiseLite.ValCoord(seed, x3, y2, z0), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y3, z0), FastNoiseLite.ValCoord(seed, x1, y3, z0), FastNoiseLite.ValCoord(seed, x2, y3, z0), FastNoiseLite.ValCoord(seed, x3, y3, z0), xs), ys), FastNoiseLite.CubicLerp(FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y0, z1), FastNoiseLite.ValCoord(seed, x1, y0, z1), FastNoiseLite.ValCoord(seed, x2, y0, z1), FastNoiseLite.ValCoord(seed, x3, y0, z1), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y1, z1), FastNoiseLite.ValCoord(seed, x1, y1, z1), FastNoiseLite.ValCoord(seed, x2, y1, z1), FastNoiseLite.ValCoord(seed, x3, y1, z1), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y2, z1), FastNoiseLite.ValCoord(seed, x1, y2, z1), FastNoiseLite.ValCoord(seed, x2, y2, z1), FastNoiseLite.ValCoord(seed, x3, y2, z1), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y3, z1), FastNoiseLite.ValCoord(seed, x1, y3, z1), FastNoiseLite.ValCoord(seed, x2, y3, z1), FastNoiseLite.ValCoord(seed, x3, y3, z1), xs), ys), FastNoiseLite.CubicLerp(FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y0, z2), FastNoiseLite.ValCoord(seed, x1, y0, z2), FastNoiseLite.ValCoord(seed, x2, y0, z2), FastNoiseLite.ValCoord(seed, x3, y0, z2), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y1, z2), FastNoiseLite.ValCoord(seed, x1, y1, z2), FastNoiseLite.ValCoord(seed, x2, y1, z2), FastNoiseLite.ValCoord(seed, x3, y1, z2), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y2, z2), FastNoiseLite.ValCoord(seed, x1, y2, z2), FastNoiseLite.ValCoord(seed, x2, y2, z2), FastNoiseLite.ValCoord(seed, x3, y2, z2), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y3, z2), FastNoiseLite.ValCoord(seed, x1, y3, z2), FastNoiseLite.ValCoord(seed, x2, y3, z2), FastNoiseLite.ValCoord(seed, x3, y3, z2), xs), ys), FastNoiseLite.CubicLerp(FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y0, z3), FastNoiseLite.ValCoord(seed, x1, y0, z3), FastNoiseLite.ValCoord(seed, x2, y0, z3), FastNoiseLite.ValCoord(seed, x3, y0, z3), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y1, z3), FastNoiseLite.ValCoord(seed, x1, y1, z3), FastNoiseLite.ValCoord(seed, x2, y1, z3), FastNoiseLite.ValCoord(seed, x3, y1, z3), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y2, z3), FastNoiseLite.ValCoord(seed, x1, y2, z3), FastNoiseLite.ValCoord(seed, x2, y2, z3), FastNoiseLite.ValCoord(seed, x3, y2, z3), xs), FastNoiseLite.CubicLerp(FastNoiseLite.ValCoord(seed, x0, y3, z3), FastNoiseLite.ValCoord(seed, x1, y3, z3), FastNoiseLite.ValCoord(seed, x2, y3, z3), FastNoiseLite.ValCoord(seed, x3, y3, z3), xs), ys), zs) * 0.2962963f;
    }

    private float SingleValue(int seed, double x, double y) {
        int x0 = FastNoiseLite.FastFloor(x);
        int y0 = FastNoiseLite.FastFloor(y);
        float xs = FastNoiseLite.InterpHermite((float)(x - (double)x0));
        float ys = FastNoiseLite.InterpHermite((float)(y - (double)y0));
        int x1 = (x0 *= 501125321) + 501125321;
        int y1 = (y0 *= 1136930381) + 1136930381;
        float xf0 = FastNoiseLite.Lerp(FastNoiseLite.ValCoord(seed, x0, y0), FastNoiseLite.ValCoord(seed, x1, y0), xs);
        float xf1 = FastNoiseLite.Lerp(FastNoiseLite.ValCoord(seed, x0, y1), FastNoiseLite.ValCoord(seed, x1, y1), xs);
        return FastNoiseLite.Lerp(xf0, xf1, ys);
    }

    private float SingleValue(int seed, double x, double y, double z) {
        int x0 = FastNoiseLite.FastFloor(x);
        int y0 = FastNoiseLite.FastFloor(y);
        int z0 = FastNoiseLite.FastFloor(z);
        float xs = FastNoiseLite.InterpHermite((float)(x - (double)x0));
        float ys = FastNoiseLite.InterpHermite((float)(y - (double)y0));
        float zs = FastNoiseLite.InterpHermite((float)(z - (double)z0));
        int x1 = (x0 *= 501125321) + 501125321;
        int y1 = (y0 *= 1136930381) + 1136930381;
        int z1 = (z0 *= 1720413743) + 1720413743;
        float xf00 = FastNoiseLite.Lerp(FastNoiseLite.ValCoord(seed, x0, y0, z0), FastNoiseLite.ValCoord(seed, x1, y0, z0), xs);
        float xf10 = FastNoiseLite.Lerp(FastNoiseLite.ValCoord(seed, x0, y1, z0), FastNoiseLite.ValCoord(seed, x1, y1, z0), xs);
        float xf01 = FastNoiseLite.Lerp(FastNoiseLite.ValCoord(seed, x0, y0, z1), FastNoiseLite.ValCoord(seed, x1, y0, z1), xs);
        float xf11 = FastNoiseLite.Lerp(FastNoiseLite.ValCoord(seed, x0, y1, z1), FastNoiseLite.ValCoord(seed, x1, y1, z1), xs);
        float yf0 = FastNoiseLite.Lerp(xf00, xf10, ys);
        float yf1 = FastNoiseLite.Lerp(xf01, xf11, ys);
        return FastNoiseLite.Lerp(yf0, yf1, zs);
    }

    private void DoSingleDomainWarp(int seed, float amp, float freq, double x, double y, @Nonnull Vector2 coord) {
        switch (this.mDomainWarpType.ordinal()) {
            case 0: {
                this.SingleDomainWarpSimplexGradient(seed, amp * 38.283688f, freq, x, y, coord, false);
                break;
            }
            case 1: {
                this.SingleDomainWarpSimplexGradient(seed, amp * 16.0f, freq, x, y, coord, true);
                break;
            }
            case 2: {
                this.SingleDomainWarpBasicGrid(seed, amp, freq, x, y, coord);
            }
        }
    }

    private void DoSingleDomainWarp(int seed, float amp, float freq, double x, double y, double z, @Nonnull Vector3 coord) {
        switch (this.mDomainWarpType.ordinal()) {
            case 0: {
                this.SingleDomainWarpOpenSimplex2Gradient(seed, amp * 32.694283f, freq, x, y, z, coord, false);
                break;
            }
            case 1: {
                this.SingleDomainWarpOpenSimplex2Gradient(seed, amp * 7.716049f, freq, x, y, z, coord, true);
                break;
            }
            case 2: {
                this.SingleDomainWarpBasicGrid(seed, amp, freq, x, y, z, coord);
            }
        }
    }

    private void DomainWarpSingle(@Nonnull Vector2 coord) {
        int seed = this.mSeed;
        float amp = this.mDomainWarpAmp * this.mFractalBounding;
        float freq = this.mDomainWarpFreq;
        double xs = coord.x;
        double ys = coord.y;
        switch (this.mDomainWarpType.ordinal()) {
            case 0: 
            case 1: {
                double SQRT3 = 1.7320508075688772;
                double F2 = 0.3660254037844386;
                double t = (xs + ys) * 0.3660254037844386;
                xs += t;
                ys += t;
                break;
            }
        }
        this.DoSingleDomainWarp(seed, amp, freq, xs, ys, coord);
    }

    public void DomainWarpSingle(@Nonnull Vector3 coord) {
        int seed = this.mSeed;
        float amp = this.mDomainWarpAmp * this.mFractalBounding;
        float freq = this.mDomainWarpFreq;
        double xs = coord.x;
        double ys = coord.y;
        double zs = coord.z;
        switch (this.mWarpTransformType3D.ordinal()) {
            case 1: {
                double xy = xs + ys;
                double s2 = xy * -0.211324865405187;
                xs += s2 - (zs *= 0.577350269189626);
                ys = ys + s2 - zs;
                zs += xy * 0.577350269189626;
                break;
            }
            case 2: {
                double xz = xs + zs;
                double s2 = xz * -0.211324865405187;
                xs += s2 - (ys *= 0.577350269189626);
                zs += s2 - ys;
                ys += xz * 0.577350269189626;
                break;
            }
            case 3: {
                double R3 = 0.6666666666666666;
                double r = (xs + ys + zs) * 0.6666666666666666;
                xs = r - xs;
                ys = r - ys;
                zs = r - zs;
                break;
            }
        }
        this.DoSingleDomainWarp(seed, amp, freq, xs, ys, zs, coord);
    }

    public void DomainWarpFractalProgressive(@Nonnull Vector2 coord) {
        int seed = this.mSeed;
        float amp = this.mDomainWarpAmp * this.mFractalBounding;
        float freq = this.mDomainWarpFreq;
        for (int i = 0; i < this.mOctaves; ++i) {
            double xs = coord.x;
            double ys = coord.y;
            switch (this.mDomainWarpType.ordinal()) {
                case 0: 
                case 1: {
                    double SQRT3 = 1.7320508075688772;
                    double F2 = 0.3660254037844386;
                    double t = (xs + ys) * 0.3660254037844386;
                    xs += t;
                    ys += t;
                    break;
                }
            }
            this.DoSingleDomainWarp(seed, amp, freq, xs, ys, coord);
            ++seed;
            amp *= this.mGain;
            freq *= this.mLacunarity;
        }
    }

    public void DomainWarpFractalProgressive(@Nonnull Vector3 coord) {
        int seed = this.mSeed;
        float amp = this.mDomainWarpAmp * this.mFractalBounding;
        float freq = this.mDomainWarpFreq;
        for (int i = 0; i < this.mOctaves; ++i) {
            double xs = coord.x;
            double ys = coord.y;
            double zs = coord.z;
            switch (this.mWarpTransformType3D.ordinal()) {
                case 1: {
                    double xy = xs + ys;
                    double s2 = xy * -0.211324865405187;
                    xs += s2 - (zs *= 0.577350269189626);
                    ys = ys + s2 - zs;
                    zs += xy * 0.577350269189626;
                    break;
                }
                case 2: {
                    double xz = xs + zs;
                    double s2 = xz * -0.211324865405187;
                    xs += s2 - (ys *= 0.577350269189626);
                    zs += s2 - ys;
                    ys += xz * 0.577350269189626;
                    break;
                }
                case 3: {
                    double R3 = 0.6666666666666666;
                    double r = (xs + ys + zs) * 0.6666666666666666;
                    xs = r - xs;
                    ys = r - ys;
                    zs = r - zs;
                    break;
                }
            }
            this.DoSingleDomainWarp(seed, amp, freq, xs, ys, zs, coord);
            ++seed;
            amp *= this.mGain;
            freq *= this.mLacunarity;
        }
    }

    private void DomainWarpFractalIndependent(@Nonnull Vector2 coord) {
        double xs = coord.x;
        double ys = coord.y;
        switch (this.mDomainWarpType.ordinal()) {
            case 0: 
            case 1: {
                double SQRT3 = 1.7320508075688772;
                double F2 = 0.3660254037844386;
                double t = (xs + ys) * 0.3660254037844386;
                xs += t;
                ys += t;
                break;
            }
        }
        int seed = this.mSeed;
        float amp = this.mDomainWarpAmp * this.mFractalBounding;
        float freq = this.mFrequency;
        for (int i = 0; i < this.mOctaves; ++i) {
            this.DoSingleDomainWarp(seed, amp, freq, xs, ys, coord);
            ++seed;
            amp *= this.mGain;
            freq *= this.mLacunarity;
        }
    }

    private void DomainWarpFractalIndependent(@Nonnull Vector3 coord) {
        double xs = coord.x;
        double ys = coord.y;
        double zs = coord.z;
        switch (this.mWarpTransformType3D.ordinal()) {
            case 1: {
                double xy = xs + ys;
                double s2 = xy * -0.211324865405187;
                xs += s2 - (zs *= 0.577350269189626);
                ys = ys + s2 - zs;
                zs += xy * 0.577350269189626;
                break;
            }
            case 2: {
                double xz = xs + zs;
                double s2 = xz * -0.211324865405187;
                xs += s2 - (ys *= 0.577350269189626);
                zs += s2 - ys;
                ys += xz * 0.577350269189626;
                break;
            }
            case 3: {
                double R3 = 0.6666666666666666;
                double r = (xs + ys + zs) * 0.6666666666666666;
                xs = r - xs;
                ys = r - ys;
                zs = r - zs;
                break;
            }
        }
        int seed = this.mSeed;
        float amp = this.mDomainWarpAmp * this.mFractalBounding;
        float freq = this.mFrequency;
        for (int i = 0; i < this.mOctaves; ++i) {
            this.DoSingleDomainWarp(seed, amp, freq, xs, ys, zs, coord);
            ++seed;
            amp *= this.mGain;
            freq *= this.mLacunarity;
        }
    }

    private void SingleDomainWarpBasicGrid(int seed, float warpAmp, float frequency, double x, double y, @Nonnull Vector2 coord) {
        double xf = x * (double)frequency;
        double yf = y * (double)frequency;
        int x0 = FastNoiseLite.FastFloor(xf);
        int y0 = FastNoiseLite.FastFloor(yf);
        float xs = FastNoiseLite.InterpHermite((float)(xf - (double)x0));
        float ys = FastNoiseLite.InterpHermite((float)(yf - (double)y0));
        int x1 = (x0 *= 501125321) + 501125321;
        int y1 = (y0 *= 1136930381) + 1136930381;
        int hash0 = FastNoiseLite.hash(seed, x0, y0) & 0x1FE;
        int hash1 = FastNoiseLite.hash(seed, x1, y0) & 0x1FE;
        float lx0x = FastNoiseLite.Lerp(RandVecs2D[hash0], RandVecs2D[hash1], xs);
        float ly0x = FastNoiseLite.Lerp(RandVecs2D[hash0 | 1], RandVecs2D[hash1 | 1], xs);
        hash0 = FastNoiseLite.hash(seed, x0, y1) & 0x1FE;
        hash1 = FastNoiseLite.hash(seed, x1, y1) & 0x1FE;
        float lx1x = FastNoiseLite.Lerp(RandVecs2D[hash0], RandVecs2D[hash1], xs);
        float ly1x = FastNoiseLite.Lerp(RandVecs2D[hash0 | 1], RandVecs2D[hash1 | 1], xs);
        coord.x += (double)(FastNoiseLite.Lerp(lx0x, lx1x, ys) * warpAmp);
        coord.y += (double)(FastNoiseLite.Lerp(ly0x, ly1x, ys) * warpAmp);
    }

    private void SingleDomainWarpBasicGrid(int seed, float warpAmp, float frequency, double x, double y, double z, @Nonnull Vector3 coord) {
        double xf = x * (double)frequency;
        double yf = y * (double)frequency;
        double zf = z * (double)frequency;
        int x0 = FastNoiseLite.FastFloor(xf);
        int y0 = FastNoiseLite.FastFloor(yf);
        int z0 = FastNoiseLite.FastFloor(zf);
        float xs = FastNoiseLite.InterpHermite((float)(xf - (double)x0));
        float ys = FastNoiseLite.InterpHermite((float)(yf - (double)y0));
        float zs = FastNoiseLite.InterpHermite((float)(zf - (double)z0));
        int x1 = (x0 *= 501125321) + 501125321;
        int y1 = (y0 *= 1136930381) + 1136930381;
        int z1 = (z0 *= 1720413743) + 1720413743;
        int hash0 = FastNoiseLite.hash(seed, x0, y0, z0) & 0x3FC;
        int hash1 = FastNoiseLite.hash(seed, x1, y0, z0) & 0x3FC;
        float lx0x = FastNoiseLite.Lerp(randVecs3D[hash0], randVecs3D[hash1], xs);
        float ly0x = FastNoiseLite.Lerp(randVecs3D[hash0 | 1], randVecs3D[hash1 | 1], xs);
        float lz0x = FastNoiseLite.Lerp(randVecs3D[hash0 | 2], randVecs3D[hash1 | 2], xs);
        hash0 = FastNoiseLite.hash(seed, x0, y1, z0) & 0x3FC;
        hash1 = FastNoiseLite.hash(seed, x1, y1, z0) & 0x3FC;
        float lx1x = FastNoiseLite.Lerp(randVecs3D[hash0], randVecs3D[hash1], xs);
        float ly1x = FastNoiseLite.Lerp(randVecs3D[hash0 | 1], randVecs3D[hash1 | 1], xs);
        float lz1x = FastNoiseLite.Lerp(randVecs3D[hash0 | 2], randVecs3D[hash1 | 2], xs);
        float lx0y = FastNoiseLite.Lerp(lx0x, lx1x, ys);
        float ly0y = FastNoiseLite.Lerp(ly0x, ly1x, ys);
        float lz0y = FastNoiseLite.Lerp(lz0x, lz1x, ys);
        hash0 = FastNoiseLite.hash(seed, x0, y0, z1) & 0x3FC;
        hash1 = FastNoiseLite.hash(seed, x1, y0, z1) & 0x3FC;
        lx0x = FastNoiseLite.Lerp(randVecs3D[hash0], randVecs3D[hash1], xs);
        ly0x = FastNoiseLite.Lerp(randVecs3D[hash0 | 1], randVecs3D[hash1 | 1], xs);
        lz0x = FastNoiseLite.Lerp(randVecs3D[hash0 | 2], randVecs3D[hash1 | 2], xs);
        hash0 = FastNoiseLite.hash(seed, x0, y1, z1) & 0x3FC;
        hash1 = FastNoiseLite.hash(seed, x1, y1, z1) & 0x3FC;
        lx1x = FastNoiseLite.Lerp(randVecs3D[hash0], randVecs3D[hash1], xs);
        ly1x = FastNoiseLite.Lerp(randVecs3D[hash0 | 1], randVecs3D[hash1 | 1], xs);
        lz1x = FastNoiseLite.Lerp(randVecs3D[hash0 | 2], randVecs3D[hash1 | 2], xs);
        coord.x += (double)(FastNoiseLite.Lerp(lx0y, FastNoiseLite.Lerp(lx0x, lx1x, ys), zs) * warpAmp);
        coord.y += (double)(FastNoiseLite.Lerp(ly0y, FastNoiseLite.Lerp(ly0x, ly1x, ys), zs) * warpAmp);
        coord.z += (double)(FastNoiseLite.Lerp(lz0y, FastNoiseLite.Lerp(lz0x, lz1x, ys), zs) * warpAmp);
    }

    private void SingleDomainWarpSimplexGradient(int seed, float warpAmp, float frequency, double x, double y, @Nonnull Vector2 coord, boolean outGradOnly) {
        float c;
        float SQRT3 = 1.7320508f;
        float G2 = 0.21132487f;
        int i = FastNoiseLite.FastFloor(x *= (double)frequency);
        int j = FastNoiseLite.FastFloor(y *= (double)frequency);
        float xi = (float)(x - (double)i);
        float yi = (float)(y - (double)j);
        float t = (xi + yi) * 0.21132487f;
        float x0 = xi - t;
        float y0 = yi - t;
        i *= 501125321;
        j *= 1136930381;
        float vy = 0.0f;
        float vx = 0.0f;
        float a = 0.5f - x0 * x0 - y0 * y0;
        if (a > 0.0f) {
            float yo;
            float xo;
            float aaaa = a * a * (a * a);
            if (outGradOnly) {
                hash = FastNoiseLite.hash(seed, i, j) & 0x1FE;
                xo = RandVecs2D[hash];
                yo = RandVecs2D[hash | 1];
            } else {
                hash = FastNoiseLite.hash(seed, i, j);
                int index1 = hash & 0xFE;
                int index2 = hash >> 7 & 0x1FE;
                float xg = Gradients2D[index1];
                float yg = Gradients2D[index1 | 1];
                float value = x0 * xg + y0 * yg;
                float xgo = RandVecs2D[index2];
                float ygo = RandVecs2D[index2 | 1];
                xo = value * xgo;
                yo = value * ygo;
            }
            vx += aaaa * xo;
            vy += aaaa * yo;
        }
        if ((c = 3.1547005f * t + (-0.6666666f + a)) > 0.0f) {
            float yo;
            float xo;
            float x2 = x0 + -0.57735026f;
            float y2 = y0 + -0.57735026f;
            float cccc = c * c * (c * c);
            if (outGradOnly) {
                hash = FastNoiseLite.hash(seed, i + 501125321, j + 1136930381) & 0x1FE;
                xo = RandVecs2D[hash];
                yo = RandVecs2D[hash | 1];
            } else {
                hash = FastNoiseLite.hash(seed, i + 501125321, j + 1136930381);
                int index1 = hash & 0xFE;
                int index2 = hash >> 7 & 0x1FE;
                float xg = Gradients2D[index1];
                float yg = Gradients2D[index1 | 1];
                float value = x2 * xg + y2 * yg;
                float xgo = RandVecs2D[index2];
                float ygo = RandVecs2D[index2 | 1];
                xo = value * xgo;
                yo = value * ygo;
            }
            vx += cccc * xo;
            vy += cccc * yo;
        }
        if (y0 > x0) {
            x1 = x0 + 0.21132487f;
            y1 = y0 + -0.7886751f;
            float b = 0.5f - x1 * x1 - y1 * y1;
            if (b > 0.0f) {
                float xo;
                float bbbb = b * b * (b * b);
                if (outGradOnly) {
                    int hash = FastNoiseLite.hash(seed, i, j + 1136930381) & 0x1FE;
                    xo = RandVecs2D[hash];
                    yo = RandVecs2D[hash | 1];
                } else {
                    int hash = FastNoiseLite.hash(seed, i, j + 1136930381);
                    int index1 = hash & 0xFE;
                    int index2 = hash >> 7 & 0x1FE;
                    xg = Gradients2D[index1];
                    yg = Gradients2D[index1 | 1];
                    value = x1 * xg + y1 * yg;
                    xgo = RandVecs2D[index2];
                    float ygo = RandVecs2D[index2 | 1];
                    xo = value * xgo;
                    yo = value * ygo;
                }
                vx += bbbb * xo;
                vy += bbbb * yo;
            }
        } else {
            x1 = x0 + -0.7886751f;
            y1 = y0 + 0.21132487f;
            float b = 0.5f - x1 * x1 - y1 * y1;
            if (b > 0.0f) {
                float xo;
                float bbbb = b * b * (b * b);
                if (outGradOnly) {
                    int hash = FastNoiseLite.hash(seed, i + 501125321, j) & 0x1FE;
                    xo = RandVecs2D[hash];
                    yo = RandVecs2D[hash | 1];
                } else {
                    int hash = FastNoiseLite.hash(seed, i + 501125321, j);
                    int index1 = hash & 0xFE;
                    int index2 = hash >> 7 & 0x1FE;
                    xg = Gradients2D[index1];
                    yg = Gradients2D[index1 | 1];
                    value = x1 * xg + y1 * yg;
                    xgo = RandVecs2D[index2];
                    float ygo = RandVecs2D[index2 | 1];
                    xo = value * xgo;
                    yo = value * ygo;
                }
                vx += bbbb * xo;
                vy += bbbb * yo;
            }
        }
        coord.x += (double)(vx * warpAmp);
        coord.y += (double)(vy * warpAmp);
    }

    private void SingleDomainWarpOpenSimplex2Gradient(int seed, float warpAmp, float frequency, double x, double y, double z, @Nonnull Vector3 coord, boolean outGradOnly) {
        int i = FastNoiseLite.fastRound(x *= (double)frequency);
        int j = FastNoiseLite.fastRound(y *= (double)frequency);
        int k = FastNoiseLite.fastRound(z *= (double)frequency);
        float x0 = (float)x - (float)i;
        float y0 = (float)y - (float)j;
        float z0 = (float)z - (float)k;
        int xNSign = (int)(-x0 - 1.0f) | 1;
        int yNSign = (int)(-y0 - 1.0f) | 1;
        int zNSign = (int)(-z0 - 1.0f) | 1;
        float ax0 = (float)xNSign * -x0;
        float ay0 = (float)yNSign * -y0;
        float az0 = (float)zNSign * -z0;
        i *= 501125321;
        j *= 1136930381;
        k *= 1720413743;
        float vz = 0.0f;
        float vy = 0.0f;
        float vx = 0.0f;
        float a = 0.6f - x0 * x0 - (y0 * y0 + z0 * z0);
        int l = 0;
        while (true) {
            if (a > 0.0f) {
                float zo;
                float yo;
                float xo;
                float aaaa = a * a * (a * a);
                if (outGradOnly) {
                    hash = FastNoiseLite.hash(seed, i, j, k) & 0x3FC;
                    xo = randVecs3D[hash];
                    yo = randVecs3D[hash | 1];
                    zo = randVecs3D[hash | 2];
                } else {
                    hash = FastNoiseLite.hash(seed, i, j, k);
                    int index1 = hash & 0xFC;
                    int index2 = hash >> 6 & 0x3FC;
                    float xg = Gradients3D[index1];
                    float yg = Gradients3D[index1 | 1];
                    float zg = Gradients3D[index1 | 2];
                    float value = x0 * xg + y0 * yg + z0 * zg;
                    float xgo = randVecs3D[index2];
                    float ygo = randVecs3D[index2 | 1];
                    float zgo = randVecs3D[index2 | 2];
                    xo = value * xgo;
                    yo = value * ygo;
                    zo = value * zgo;
                }
                vx += aaaa * xo;
                vy += aaaa * yo;
                vz += aaaa * zo;
            }
            float b = a;
            int i1 = i;
            int j1 = j;
            int k1 = k;
            float x1 = x0;
            float y1 = y0;
            float z1 = z0;
            if (ax0 >= ay0 && ax0 >= az0) {
                x1 += (float)xNSign;
                b = b + ax0 + ax0;
                i1 -= xNSign * 501125321;
            } else if (ay0 > ax0 && ay0 >= az0) {
                y1 += (float)yNSign;
                b = b + ay0 + ay0;
                j1 -= yNSign * 1136930381;
            } else {
                z1 += (float)zNSign;
                b = b + az0 + az0;
                k1 -= zNSign * 1720413743;
            }
            if (b > 1.0f) {
                float zo;
                float yo;
                float xo;
                float bbbb = (b -= 1.0f) * b * (b * b);
                if (outGradOnly) {
                    hash = FastNoiseLite.hash(seed, i1, j1, k1) & 0x3FC;
                    xo = randVecs3D[hash];
                    yo = randVecs3D[hash | 1];
                    zo = randVecs3D[hash | 2];
                } else {
                    hash = FastNoiseLite.hash(seed, i1, j1, k1);
                    int index1 = hash & 0xFC;
                    int index2 = hash >> 6 & 0x3FC;
                    float xg = Gradients3D[index1];
                    float yg = Gradients3D[index1 | 1];
                    float zg = Gradients3D[index1 | 2];
                    float value = x1 * xg + y1 * yg + z1 * zg;
                    float xgo = randVecs3D[index2];
                    float ygo = randVecs3D[index2 | 1];
                    float zgo = randVecs3D[index2 | 2];
                    xo = value * xgo;
                    yo = value * ygo;
                    zo = value * zgo;
                }
                vx += bbbb * xo;
                vy += bbbb * yo;
                vz += bbbb * zo;
            }
            if (l == 1) break;
            ax0 = 0.5f - ax0;
            ay0 = 0.5f - ay0;
            az0 = 0.5f - az0;
            x0 = (float)xNSign * ax0;
            y0 = (float)yNSign * ay0;
            z0 = (float)zNSign * az0;
            a += 0.75f - ax0 - (ay0 + az0);
            i += xNSign >> 1 & 0x1DDE90C9;
            j += yNSign >> 1 & 0x43C42E4D;
            k += zNSign >> 1 & 0x668B6E2F;
            xNSign = -xNSign;
            yNSign = -yNSign;
            zNSign = -zNSign;
            seed += 1293373;
            ++l;
        }
        coord.x += (double)(vx * warpAmp);
        coord.y += (double)(vy * warpAmp);
        coord.z += (double)(vz * warpAmp);
    }

    public static enum NoiseType {
        OpenSimplex2,
        OpenSimplex2S,
        Cellular,
        Perlin,
        ValueCubic,
        Value;

    }

    public static enum RotationType3D {
        None,
        ImproveXYPlanes,
        ImproveXZPlanes;

    }

    private static enum TransformType3D {
        None,
        ImproveXYPlanes,
        ImproveXZPlanes,
        DefaultOpenSimplex2;

    }

    public static enum FractalType {
        None,
        FBm,
        Ridged,
        PingPong,
        DomainWarpProgressive,
        DomainWarpIndependent;

    }

    public static enum CellularDistanceFunction {
        Euclidean,
        EuclideanSq,
        Manhattan,
        Hybrid;

    }

    public static enum CellularReturnType {
        CellValue,
        Distance,
        Distance2,
        Distance2Add,
        Distance2Sub,
        Distance2Mul,
        Distance2Div;

        public static final Codec<CellularReturnType> CODEC;

        static {
            CODEC = new EnumCodec<CellularReturnType>(CellularReturnType.class);
        }
    }

    public static enum DomainWarpType {
        OpenSimplex2,
        OpenSimplex2Reduced,
        BasicGrid;

    }

    public static class Vector2 {
        public double x;
        public double y;

        public Vector2(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Vector3 {
        public double x;
        public double y;
        public double z;

        public Vector3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

