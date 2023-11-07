/* 4.10.1 class */
package com.maddox.il2.objects.air;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Explosion;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.MsgExplosionListener;
import com.maddox.il2.ai.MsgShotListener;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.Squadron;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.CellAirPlane;
import com.maddox.il2.ai.air.CellObject;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.ai.ground.Predator;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.BmpUtils;
import com.maddox.il2.engine.BulletProperties;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.FObj;
import com.maddox.il2.engine.GunProperties;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.LightPointWorld;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Mesh;
import com.maddox.il2.engine.MsgCollisionListener;
import com.maddox.il2.engine.MsgCollisionRequestListener;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.Autopilotage;
import com.maddox.il2.fm.Controls;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.fm.ZutiSupportMethods_FM;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.TimeSkip;
import com.maddox.il2.net.Chat;
import com.maddox.il2.net.NetMissionTrack;
import com.maddox.il2.net.NetWing;
import com.maddox.il2.objects.ActorCrater;
import com.maddox.il2.objects.ActorSimpleMesh;
import com.maddox.il2.objects.Wreck;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.vehicles.artillery.RocketryRocket;
import com.maddox.il2.objects.weapons.BallisticProjectile;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.il2.objects.weapons.GunEmpty;
import com.maddox.il2.objects.weapons.MGunAircraftGeneric;
import com.maddox.il2.objects.weapons.Pylon;
import com.maddox.il2.objects.weapons.PylonRO_WfrGr21;
import com.maddox.il2.objects.weapons.PylonRO_WfrGr21Dual;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.il2.objects.weapons.RocketGunWfrGr21;
import com.maddox.rts.Finger;
import com.maddox.rts.HomePath;
import com.maddox.rts.KryptoInputFilter;
import com.maddox.rts.Message;
import com.maddox.rts.MsgAction;
import com.maddox.rts.MsgDestroy;
import com.maddox.rts.MsgEndAction;
import com.maddox.rts.MsgEndActionListener;
import com.maddox.rts.NetChannel;
import com.maddox.rts.ObjIO;
import com.maddox.rts.ObjState;
import com.maddox.rts.Property;
import com.maddox.rts.SFSInputStream;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.sas1946.il2.util.TrueRandom;
import com.maddox.util.HashMapExt;
import com.maddox.util.HashMapInt;
import com.maddox.util.NumberTokenizer;

public abstract class Aircraft extends NetAircraft implements MsgCollisionListener, MsgCollisionRequestListener, MsgExplosionListener, MsgShotListener, MsgEndActionListener, Predator {
    private boolean                      wfrGr21dropped;
    public float                         headingBug;
    public int                           idleTimeOnCarrier;
    private static final java.lang.Class planesWithZBReceiver[];
    public int                           armingSeed;
    public RangeRandom                   armingRnd;

    static {
        planesWithZBReceiver = new java.lang.Class[] { F4U.class, F4F.class, TBF.class, SBD.class, PBYX.class, F6F.class, F2A2.class, SEAFIRE3.class, SEAFIRE3F.class, Fulmar.class, Swordfish.class, MOSQUITO.class, B_25.class, A_20.class, B_17.class,
                B_24.class, B_29.class, BEAU.class, P_80.class, P_39.class, P_51.class, P_47.class, P_40.class, P_38.class, P_36.class, A_20.class, C_47.class };
    }

    public long                    tmSearchlighted;
    public static final float      MINI_HIT          = 5.0000006E-7F;
    public static final float      defaultUnitHit    = 0.01F;
    public static final float      powerPerMM        = 1700.0F;
    public static final int        HIT_COLLISION     = 0;
    public static final int        HIT_EXPLOSION     = 1;
    public static final int        HIT_SHOT          = 2;
    protected static float[]       ypr               = { 0.0F, 0.0F, 0.0F };
    protected static float[]       xyz               = { 0.0F, 0.0F, 0.0F };
    public static final int        _AILERON_L        = 0;
    public static final int        _AILERON_R        = 1;
    public static final int        _FUSELAGE         = 2;
    public static final int        _ENGINE_1         = 3;
    public static final int        _ENGINE_2         = 4;
    public static final int        _ENGINE_3         = 5;
    public static final int        _ENGINE_4         = 6;
    public static final int        _GEAR_C           = 7;
    public static final int        _FLAP_R           = 8;
    public static final int        _GEAR_L           = 9;
    public static final int        _GEAR_R           = 10;
    public static final int        _VER_STAB_1       = 11;
    public static final int        _VER_STAB_2       = 12;
    public static final int        _NOSE             = 13;
    public static final int        _OIL              = 14;
    public static final int        _RUDDER_1         = 15;
    public static final int        _RUDDER_2         = 16;
    public static final int        _HOR_STAB_L       = 17;
    public static final int        _HOR_STAB_R       = 18;
    public static final int        _TAIL_1           = 19;
    public static final int        _TAIL_2           = 20;
    public static final int        _TANK_1           = 21;
    public static final int        _TANK_2           = 22;
    public static final int        _TANK_3           = 23;
    public static final int        _TANK_4           = 24;
    public static final int        _TURRET_1         = 25;
    public static final int        _TURRET_2         = 26;
    public static final int        _TURRET_3         = 27;
    public static final int        _TURRET_4         = 28;
    public static final int        _TURRET_5         = 29;
    public static final int        _TURRET_6         = 30;
    public static final int        _ELEVATOR_L       = 31;
    public static final int        _ELEVATOR_R       = 32;
    public static final int        _WING_ROOT_L      = 33;
    public static final int        _WING_MIDDLE_L    = 34;
    public static final int        _WING_END_L       = 35;
    public static final int        _WING_ROOT_R      = 36;
    public static final int        _WING_MIDDLE_R    = 37;
    public static final int        _WING_END_R       = 38;
    public static final int        _FLAP_01          = 39;
    public static final int        _FLAP_02          = 40;
    public static final int        _FLAP_03          = 41;
    public static final int        _FLAP_04          = 42;
    public static final int        _NULLPART         = 43;
    public static final int        _NOMOREPARTS      = 44;
    private static final String[]  partNames         = { "AroneL", "AroneR", "CF", "Engine1", "Engine2", "Engine3", "Engine4", "GearC2", "FlapR", "GearL2", "GearR2", "Keel1", "Keel2", "Nose", "Oil", "Rudder1", "Rudder2", "StabL", "StabR", "Tail1",
            "Tail2", "Tank1", "Tank2", "Tank3", "Tank4", "Turret1B", "Turret2B", "Turret3B", "Turret4B", "Turret5B", "Turret6B", "VatorL", "VatorR", "WingLIn", "WingLMid", "WingLOut", "WingRIn", "WingRMid", "WingROut", "Flap01", "Flap02", "Flap03",
            "Flap04", "NullPart", "EXPIRED" };
    private static final String[]  partNamesForAll   = { "AroneL", "AroneR", "CF", "GearL2", "GearR2", "Keel1", "Oil", "Rudder1", "StabL", "StabR", "Tail1", "VatorL", "VatorR", "WingLIn", "WingLMid", "WingLOut", "WingRIn", "WingRMid", "WingROut" };
    public static final int        END_EXPLODE       = 2;
    public static final int        END_FM_DESTROY    = 3;
    public static final int        END_DISAPPEAR     = 4;
    private long                   timePostEndAction = -1L;
    public boolean                 buried            = false;
    private float                  EpsCoarse_        = 0.03F;
    private float                  EpsSmooth_        = 0.0030F;
    private float                  EpsVerySmooth_    = 5.0E-4F;
    private float                  Gear_;
    private float                  Rudder_;
    private float                  Elevator_;
    private float                  Aileron_;
    private float                  Flap_;
    private float                  BayDoor_          = 0.0F;
    private float                  AirBrake_         = 0.0F;
    private float                  Steering_         = 0.0F;
    public float                   wingfold_         = 0.0F;
    public float                   cockpitDoor_      = 0.0F;
    public float                   arrestor_         = 0.0F;
    protected float[]              propPos           = { 0.0F, 21.6F, 45.9F, 66.9F, 45.0F, 9.2F, 10.0F, 31.6F, 55.9F, 76.9F, 55.0F, 19.2F };
    // TODO: Edited by |ZUTI|
    // Changed from protected to public
    // ---------------------------------------------------------------------------------------------------------
    public int[]                   oldProp           = new int[AircraftState.MAX_ENGINES];
    public static final String[][] Props             = {
            { "Prop1_D0", "PropRot1_D0", "Prop1_D1" },
            { "Prop2_D0", "PropRot2_D0", "Prop2_D1" },
            { "Prop3_D0", "PropRot3_D0", "Prop3_D1" },
            { "Prop4_D0", "PropRot4_D0", "Prop4_D1" },
            { "Prop5_D0", "PropRot5_D0", "Prop5_D1" },
            { "Prop6_D0", "PropRot6_D0", "Prop6_D1" },
            { "Prop7_D0", "PropRot7_D0", "Prop7_D1" },
            { "Prop8_D0", "PropRot8_D0", "Prop8_D1" },
            { "Prop9_D0", "PropRot9_D0", "Prop9_D1" },
            { "Prop10_D0", "PropRot10_D0", "Prop10_D1" },
            { "Prop11_D0", "PropRot11_D0", "Prop11_D1" },
            { "Prop12_D0", "PropRot12_D0", "Prop12_D1" }
            };
    // ---------------------------------------------------------------------------------------------------------
    private LightPointWorld[]      lLight;
    private Hook[]                 lLightHook        = { null, null, null, null };
    private static Loc             lLightLoc1        = new Loc();
    private static Point3d         lLightP1          = new Point3d();
    private static Point3d         lLightP2          = new Point3d();
    private static Point3d         lLightPL          = new Point3d();
    private String                 _loadingCountry;
    private String                 typedName         = "UNKNOWN";
    private static String[]        _skinMat          = { "prop", "Gloss1D0o", "Gloss1D1o", "Gloss1D2o", "Gloss2D0o", "Gloss2D1o", "Gloss2D2o", "Gloss1D0p", "Gloss1D1p", "Gloss1D2p", "Gloss2D0p", "Gloss2D1p", "Gloss2D2p", "Gloss1D0q", "Gloss1D1q",
            "Gloss1D2q", "Gloss2D0q", "Gloss2D1q", "Gloss2D2q", "Matt1D0o", "Matt1D1o", "Matt1D2o", "Matt2D0o", "Matt2D1o", "Matt2D2o", "Matt1D0p", "Matt1D1p", "Matt1D2p", "Matt2D0p", "Matt2D1p", "Matt2D2p", "Matt1D0q", "Matt1D1q", "Matt1D2q", "Matt2D0q",
            "Matt2D1q", "Matt2D2q", "Gloss1aD0o", "Gloss1aD0p", "Gloss1aD0q", "Gloss1aD1o", "Gloss1aD1p", "Gloss1aD1q", "Gloss1aD2o", "Gloss1aD2p", "Gloss1aD2q", "Matt1aD0o", "MattaD0p", "Matt1aD0q", "Matt1aD1o", "Matt1aD1p", "Matt1aD1q", "Matt1aD2o",
            "Matt1aD2p", "Matt1aD2q" };
    private static final String[]  _curSkin          = { "skin1o.tga", "skin1p.tga", "skin1q.tga" };
    private static HashMapExt      meshCache         = new HashMapExt();
    private static HashMapExt      airCache          = new HashMapExt();
    protected static Loc           tmpLocCell        = new Loc();
    protected static Vector3d      v1                = new Vector3d();
    private static Vector3d        Vd                = new Vector3d();
    protected static Point3d       Pd                = new Point3d();
    protected static Point3d       tmpP1             = new Point3d();
    protected static Point3d       tmpP2             = new Point3d();
    public static Loc              tmpLoc1           = new Loc();
    protected static Loc           tmpLoc2           = new Loc();
    protected static Loc           tmpLoc3           = new Loc();
    protected static Loc           tmpLocExp         = new Loc();
    public static Orient           tmpOr             = new Orient();
    private static int             tmpBonesHit;
    private static boolean         bWasAlive         = true;

    static class EndActionParam {
        Actor      initiator;
        Eff3DActor smoke;

        public EndActionParam(Actor actor, Eff3DActor eff3dactor) {
            this.initiator = actor;
            this.smoke = eff3dactor;
        }
    }

    private static class MsgExplosionPostVarSet {
        Actor   THIS;
        String  chunkName;
        Point3d p = new Point3d();
        Actor   initiator;
        float   power;
        float   radius;

        private MsgExplosionPostVarSet() {
        }
    }

    public static class _WeaponSlot {
        public int   trigger;
        public Class clazz;
        public int   bullets;

        public _WeaponSlot(int i, String string, int i_0_) throws Exception {
            this.trigger = i;
            this.clazz = ObjIO.classForName("weapons." + string);
            this.bullets = i_0_;
        }
    }

    static class CacheItem {
        HierMesh mesh;
        boolean  bExistTextures;
        int      loaded;
        long     time;
    }

    public static String[] partNames() {
        return partNames;
    }

    public int part(String string) {
        if (string == null) return 43;
        int i = 0;
//		long l = 1L;
        while (i < 44) {
            if (string.startsWith(partNames[i])) return i;
            i++;
//			l <<= 1;
        }
        return 43;
    }

    public boolean cut(String string) {
        this.FM.dryFriction = 1.0F;
        this.debugprintln("" + string + " goes off..");
        if (World.Rnd().nextFloat() < this.bailProbabilityOnCut(string)) {
            this.debugprintln("BAILING OUT - " + string + " gone, can't keep on..");
            this.hitDaSilk();
        }
        if (!this.isChunkAnyDamageVisible(string)) {
            this.debugprintln("" + string + " is already cut off - operation rejected..");
            return false;
        }
        int[] is = this.hideSubTrees(string + "_D");
        if (is == null) return false;
        for (int i = 0; i < is.length; i++) {
            Wreckage wreckage = new Wreckage(this, is[i]);
            for (int i_1_ = 0; i_1_ < 4; i_1_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_1_ + 0])) this.FM.AS.changeTankEffectBase(i_1_, wreckage);
            for (int i_2_ = 0; i_2_ < this.FM.EI.getNum(); i_2_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_2_ + 4])) {
                    this.FM.AS.changeEngineEffectBase(i_2_, wreckage);
                    this.FM.AS.changeSootEffectBase(i_2_, wreckage);
                }
            for (int i_3_ = 0; i_3_ < 6; i_3_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_3_ + 12])) this.FM.AS.changeNavLightEffectBase(i_3_, wreckage);
            for (int i_4_ = 0; i_4_ < 4; i_4_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_4_ + 18])) this.FM.AS.changeLandingLightEffectBase(i_4_, wreckage);
            for (int i_5_ = 0; i_5_ < this.FM.EI.getNum(); i_5_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_5_ + 22])) this.FM.AS.changeOilEffectBase(i_5_, wreckage);
            if (this.hierMesh().chunkName().startsWith(string) && World.Rnd().nextInt(0, 99) < 50) Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.SMOKE, 3.0F);
            Vd.set(this.FM.Vwld);
            wreckage.setSpeed(Vd);
        }
        is = this.hierMesh().getSubTrees(string + "_D");
        for (int i = 0; i < is.length; i++)
            this.detachGun(is[i]);
        String string_6_ = string + "_CAP";
        if (this.hierMesh().chunkFindCheck(string_6_) >= 0) this.hierMesh().chunkVisible(string_6_, true);
        for (int i = 0; i < is.length; i++)
            for (int i_7_ = 3; i_7_ < this.FM.Gears.pnti.length; i_7_++)
                try {
                    if (this.FM.Gears.pnti[i_7_] != -1 && is[i] == this.hierMesh().chunkByHookNamed(this.FM.Gears.pnti[i_7_])) this.FM.Gears.pnti[i_7_] = -1;
                } catch (Exception exception) {
                    System.out.println("FATAL ERROR: Gear pnti[] cut failed on tt[] = " + i_7_ + " - " + this.FM.Gears.pnti.length);
                }
        this.hierMesh().setCurChunk(is[0]);
        this.hierMesh().getChunkLocObj(tmpLoc1);
        this.sfxCrash(tmpLoc1.getPoint());
        return true;
    }

    public boolean cut_Subtrees(String string) {
        this.debugprintln("" + string + " goes off..");
        if (World.Rnd().nextFloat() < this.bailProbabilityOnCut(string)) {
            this.debugprintln("BAILING OUT - " + string + " gone, can't keep on..");
            this.hitDaSilk();
        }
        if (!this.isChunkAnyDamageVisible(string)) {
            this.debugprintln("" + string + " is already cut off - operation rejected..");
            return false;
        }
        int i = this.hierMesh().chunkFindCheck(string + "_D0");
        if (i >= 0) {
            int i_8_;
            for (i_8_ = 0; i_8_ <= 9; i_8_++) {
                int i_9_ = this.hierMesh().chunkFindCheck(string + "_D" + i_8_);
                if (i_9_ >= 0) {
                    this.hierMesh().setCurChunk(i_9_);
                    if (this.hierMesh().isChunkVisible()) break;
                }
            }
            if (i_8_ > 9) i = -1;
        }
        ActorMesh actormesh = null;
        if (i >= 0) {
            actormesh = Wreckage.makeWreck(this, i);
            actormesh.setOwner(this, false, false, false);
        }
        int[] is = this.hideSubTrees(string + "_D");
        if (is == null) return false;
        for (int i_10_ = 0; i_10_ < is.length; i_10_++) {
            if (i < 0) actormesh = new Wreckage(this, is[i_10_]);
            else this.hierMesh().setCurChunk(is[i_10_]);
            for (int i_11_ = 0; i_11_ < 4; i_11_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_11_ + 0])) this.FM.AS.changeTankEffectBase(i_11_, actormesh);
            for (int i_12_ = 0; i_12_ < 4; i_12_++)
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[i_12_ + 4])) {
                    this.FM.AS.changeEngineEffectBase(i_12_, actormesh);
                    this.FM.AS.changeSootEffectBase(i_12_, actormesh);
                }
            if (this.hierMesh().chunkName().startsWith(string) && World.Rnd().nextInt(0, 99) < 50) Eff3DActor.New(actormesh, null, null, 1.0F, Wreckage.SMOKE, 3.0F);
            Vd.set(this.FM.Vwld);
            if (i < 0) ((Wreckage) actormesh).setSpeed(Vd);
            else((Wreck) actormesh).setSpeed(Vd);
        }
        is = this.hierMesh().getSubTrees(string + "_D");
        for (int i_13_ = 0; i_13_ < is.length; i_13_++)
            this.detachGun(is[i_13_]);
        String string_14_ = string + "_CAP";
        if (this.hierMesh().chunkFindCheck(string_14_) >= 0) this.hierMesh().chunkVisible(string_14_, true);
        for (int i_15_ = 0; i_15_ < is.length; i_15_++)
            for (int i_16_ = 3; i_16_ < this.FM.Gears.pnti.length; i_16_++)
                try {
                    if (this.FM.Gears.pnti[i_16_] != -1 && is[i_15_] == this.hierMesh().chunkByHookNamed(this.FM.Gears.pnti[i_16_])) this.FM.Gears.pnti[i_16_] = -1;
                } catch (Exception exception) {
                    System.out.println("FATAL ERROR: Gear pnti[] cut failed on tt[] = " + i_16_ + " - " + this.FM.Gears.pnti.length);
                }
        this.hierMesh().setCurChunk(is[0]);
        this.hierMesh().getChunkLocObj(tmpLoc1);
        this.sfxCrash(tmpLoc1.getPoint());
        return true;
    }

    protected boolean cutFM(int i, int i_17_, Actor actor) {
        this.FM.dryFriction = 1.0F;
        switch (i) {
            case 2:
                if (this.isEnablePostEndAction(0.0)) this.postEndAction(0.0, actor, 2, null);
                return false;
            case 3:
                if (this.FM.EI.engines.length > 0) {
                    this.hitProp(0, i_17_, actor);
                    this.FM.EI.engines[0].setEngineStuck(actor);
                }
                break;
            case 4:
                if (this.FM.EI.engines.length > 1) {
                    this.hitProp(1, i_17_, actor);
                    this.FM.EI.engines[1].setEngineStuck(actor);
                }
                break;
            case 5:
                if (this.FM.EI.engines.length > 2) {
                    this.hitProp(2, i_17_, actor);
                    this.FM.EI.engines[2].setEngineStuck(actor);
                }
                break;
            case 6:
                if (this.FM.EI.engines.length > 3) {
                    this.hitProp(3, i_17_, actor);
                    this.FM.EI.engines[3].setEngineStuck(actor);
                }
                break;
//            case 7:
//                if (this.FM.EI.engines.length > 4) {
//                    this.hitProp(4, i_17_, actor);
//                    this.FM.EI.engines[4].setEngineStuck(actor);
//                }
//                break;
//            case 8:
//                if (this.FM.EI.engines.length > 5) {
//                    this.hitProp(5, i_17_, actor);
//                    this.FM.EI.engines[5].setEngineStuck(actor);
//                }
//                break;
//            case 9:
//                if (this.FM.EI.engines.length > 6) {
//                    this.hitProp(6, i_17_, actor);
//                    this.FM.EI.engines[6].setEngineStuck(actor);
//                }
//                break;
//            case 10:
//                if (this.FM.EI.engines.length > 7) {
//                    this.hitProp(7, i_17_, actor);
//                    this.FM.EI.engines[7].setEngineStuck(actor);
//                }
//                break;
//            case 11:
//                if (this.FM.EI.engines.length > 8) {
//                    this.hitProp(8, i_17_, actor);
//                    this.FM.EI.engines[8].setEngineStuck(actor);
//                }
//                break;
//            case 12:
//                if (this.FM.EI.engines.length > 9) {
//                    this.hitProp(9, i_17_, actor);
//                    this.FM.EI.engines[9].setEngineStuck(actor);
//                }
//                break;
//            case 13:
//                if (this.FM.EI.engines.length > 10) {
//                    this.hitProp(10, i_17_, actor);
//                    this.FM.EI.engines[10].setEngineStuck(actor);
//                }
//                break;
//            case 14:
//                if (this.FM.EI.engines.length > 11) {
//                    this.hitProp(11, i_17_, actor);
//                    this.FM.EI.engines[11].setEngineStuck(actor);
//                }
//                break;
        }
        return this.cut(partNames[i]);
    }

    protected int curDMGLevel(int i) {
        return this.curDMGLevel(partNames[i] + "_D0");
    }

    private int curDMGLevel(String string) {
        int i = string.length() - 1;
        if (i < 2) return 0;
        boolean bool = string.charAt(i - 2) == '_' && Character.toUpperCase(string.charAt(i - 1)) == 'D' && Character.isDigit(string.charAt(i));
        if (!bool) return 0;
        HierMesh hiermesh = this.hierMesh();
        String string_18_ = string.substring(0, i);
        int i_19_;
        for (i_19_ = 0; i_19_ < 10; i_19_++) {
            String string_20_ = string_18_ + i_19_;
            if (hiermesh.chunkFindCheck(string_20_) < 0) return 0;
            if (hiermesh.isChunkVisible(string_20_)) break;
        }
        if (i_19_ == 10) return 0;
        return i_19_;
    }

    protected void nextDMGLevel(String string, int i, Actor actor) {
        int i_21_ = string.length() - 1;
        HierMesh hiermesh = this.hierMesh();
        String string_22_ = string;
        boolean bool = string.charAt(i_21_ - 2) == '_' && Character.toUpperCase(string.charAt(i_21_ - 1)) == 'D' && Character.isDigit(string.charAt(i_21_));
        this.FM.dryFriction = 1.0F;
        String string_23_;
        if (bool) {
            int i_24_ = string.charAt(i_21_) - 48;
            String string_25_ = string.substring(0, i_21_);
            while_0_: do {
                do {
                    if (hiermesh.isChunkVisible(string_22_)) break while_0_;
                    if (i_24_ >= 9) break;
                    i_24_++;
                    string_22_ = string_25_ + i_24_;
                } while (hiermesh.chunkFindCheck(string_22_) >= 0);
                return;
            } while (false);
            if (i_24_ < 9) {
                i_24_++;
                string_23_ = string_25_ + i_24_;
                if (hiermesh.chunkFindCheck(string_23_) < 0) string_23_ = null;
            } else string_23_ = null;
            string_25_ = string.substring(0, i_21_ - 2);
        } else {
            if (!hiermesh.isChunkVisible(string_22_)) return;
            string_23_ = null;
        }
        if (string_23_ == null) {
            if (!this.isNet() || this.isNetMaster()) this.nextCUTLevel(string, i, actor);
        } else {
            int i_27_ = this.part(string);
            this.FM.hit(i_27_);
            hiermesh.chunkVisible(string_22_, false);
            hiermesh.chunkVisible(string_23_, true);
        }
    }

    protected void nextDMGLevels(int i, int i_28_, String string, Actor actor) {
        if (i > 0) {
            if (i > 4) i = 4;
            if (this != World.getPlayerAircraft() || World.cur().diffCur.Vulnerability) {
                if (this.isNet()) {
                    if (this.isNetPlayer() && !World.cur().diffCur.Vulnerability || !Actor.isValid(actor)) return;

                    // TODO: Added by |ZUTI|: manage player vulnerability
                    // ---------------------------------------------------
                    if (!ZutiSupportMethods_FM.IS_PLAYER_VULNERABLE)
                        // System.out.println("Player is not vulnerable!");
                        return;

                    int i_29_ = this.part(string);
                    if (!this.isNetMaster()) this.netPutHits(true, null, i, i_28_, i_29_, actor);
                    this.netPutHits(false, null, i, i_28_, i_29_, actor);
                    if (actor != this && this.FM.isPlayers() && actor instanceof Aircraft && ((Aircraft) actor).isNetPlayer() && i_28_ != 0 && i > 3) if (string.startsWith("Wing")) {
                        if (!this.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_blowwing", (Aircraft) actor, this);
                        this.FM.setSentWingNote(true);
                    } else if (string.startsWith("Tail") && !this.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_blowtail", (Aircraft) actor, this);
                }
                while (i-- > 0)
                    this.nextDMGLevel(string, i_28_, actor);
            }
        }
    }

    protected void nextCUTLevel(String string, int i, Actor actor) {
        this.FM.dryFriction = 1.0F;
        this.debugprintln("Detected NCL in " + string + "..");
        if (this != World.getPlayerAircraft() || World.cur().diffCur.Vulnerability) {
            int i_30_ = string.length() - 1;
            HierMesh hiermesh = this.hierMesh();
            String string_31_ = string;
            boolean bool = string.charAt(i_30_ - 2) == '_' && Character.toUpperCase(string.charAt(i_30_ - 1)) == 'D' && Character.isDigit(string.charAt(i_30_));
            if (!bool && !hiermesh.isChunkVisible(string_31_)) return;
            int i_34_ = this.part(string);
            if (this.cutFM(i_34_, i, actor)) {
                this.FM.cut(i_34_, i, actor);
                this.netPutCut(i_34_, i, actor);
                if (this.FM.isPlayers() && this != actor && actor instanceof Aircraft && ((Aircraft) actor).isNetPlayer() && i == 2 && !this.FM.isSentWingNote() && !this.FM.isSentBuryNote() && (i_34_ == 34 || i_34_ == 37 || i_34_ == 33 || i_34_ == 36)) {
                    Chat.sendLogRnd(3, "gore_sawwing", (Aircraft) actor, this);
                    this.FM.setSentWingNote(true);
                }
            }
        }
    }

    public boolean isEnablePostEndAction(double d) {
        if (this.timePostEndAction < 0L) return true;
        long l = Time.current() + (int) (d * 1000.0);
        if (l < this.timePostEndAction) return true;
        return false;
    }

    public void postEndAction(double d, Actor actor, int i, Eff3DActor eff3dactor) {
        if (this.isEnablePostEndAction(d)) {
            this.timePostEndAction = Time.current() + (int) (d * 1000.0);
            MsgEndAction.post(0, d, this, new EndActionParam(actor, eff3dactor), i);
        }
    }

    public void msgEndAction(Object object, int i) {
        EndActionParam endactionparam = (EndActionParam) object;
        if (this.isAlive()) {
            if (this.FM.isPlayers() && !this.FM.isSentBuryNote()) switch (i) {
                case 2:
                    if (Actor.isAlive(endactionparam.initiator) && endactionparam.initiator instanceof Aircraft && ((Aircraft) endactionparam.initiator).isNetPlayer() && this.FM.Loc.z - Engine.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y) > 100.0)
                        Chat.sendLogRnd(1, "gore_blowup", (Aircraft) endactionparam.initiator, this);
                    break;
            }
            switch (i) {
                case 2: {
                    this.netExplode();
                    if (endactionparam.smoke != null) {
                        Eff3DActor.finish(endactionparam.smoke);
                        this.sfxSmokeState(0, 0, false);
                    }
                    this.doExplosion();
                    for (int i_35_ = 0; i_35_ < this.FM.AS.astateEngineStates.length; i_35_++)
                        this.FM.AS.hitEngine(this, i_35_, 1000);
                    for (int i_36_ = 0; i_36_ < this.FM.AS.astateTankStates.length; i_36_++)
                        this.FM.AS.hitTank(this, i_36_, 1000);
                    float f = 50.0F;
                    Actor actor = null;
                    String string = null;
                    if (this.FM.Gears.onGround() && this.FM.Vrel.lengthSquared() < 70.0) f = 0.0F;
                    else {
                        Point3d point3d = new Point3d(this.FM.Loc);
                        Point3d point3d_37_ = new Point3d(this.FM.Loc);
                        Point3d point3d_38_ = new Point3d();
                        this.FM.Vrel.set(this.FM.Vwld);
                        this.FM.Vrel.normalize();
                        this.FM.Vrel.scale(20.0);
                        point3d_37_.add(this.FM.Vrel);
                        actor = Engine.collideEnv().getLine(point3d, point3d_37_, false, this, point3d_38_);
                        if (Actor.isAlive(actor) && actor instanceof ActorHMesh) {
                            Mesh mesh = ((ActorMesh) actor).mesh();
                            Loc loc = actor.pos.getAbs();
                            float f_39_ = mesh.detectCollisionLine(loc, point3d, point3d_37_);
                            if (f_39_ >= 0.0F) string = Mesh.collisionChunk(0);
                            if (actor instanceof BigshipGeneric || actor instanceof ShipGeneric) {
                                float f_40_ = 0.018F * (float) this.FM.Vwld.length();
                                if (f_40_ > 1.0F) f_40_ = 1.0F;
                                if (f_40_ < 0.1F) f_40_ = 0.1F;
                                float f_41_ = this.FM.M.fuel;
                                if (f_41_ > 300.0F) f_41_ = 300.0F;
                                f = f_40_ * (50.0F + 0.7F * this.FM.CT.getWeaponMass() + 0.3F * f_41_);
                            }
                        }
                    }
                    float f_42_ = 0.5F * f;
                    if (f_42_ < 50.0F) f_42_ = 50.0F;
                    if (f_42_ > 300.0F) f_42_ = 300.0F;
                    float f_43_ = 0.7F * f;
                    if (f_43_ < 70.0F) f_43_ = 70.0F;
                    if (f_43_ > 350.0F) f_43_ = 350.0F;
                    MsgExplosion.send(actor, string, this.FM.Loc, this, f, 0.9F * f, 0, f_42_);
                    MsgExplosion.send(actor, string, this.FM.Loc, this, 0.5F * f, 0.25F * f, 1, f_43_);
                }
                /* fall through */
                case 3:
                    this.explode();
                    /* fall through */
                default:
                    for (int i_44_ = 0; i_44_ < Math.min(this.FM.crew, 9); i_44_++)
                        if (!this.FM.AS.isPilotDead(i_44_)) this.FM.AS.hitPilot(this.FM.actor, i_44_, 100);
                    this.setDamager(endactionparam.initiator, 4);
                    World.onActorDied(this, this.getDamager());
            }
        }
        MsgDestroy.Post(Time.current(), this);
    }

    protected void doExplosion() {
        if (this.FM.Loc.z < Engine.cur.land.HQ_Air(this.FM.Loc.x, this.FM.Loc.y) + 3.0) {
            World.cur();
            if (World.land().isWater(this.FM.Loc.x, this.FM.Loc.y)) Explosions.AirDrop_Water(this.FM.Loc);
            else {
                Explosions.AirDrop_Land(this.FM.Loc);
                Loc loc = new Loc(this.FM.Loc);
                Point3d point3d = loc.getPoint();
                World.cur();
                point3d.z = World.land().HQ(this.FM.Loc.x, this.FM.Loc.y);
                Eff3DActor.New(loc, 1.0F, "EFFECTS/Smokes/SmokeBoiling.eff", 1200.0F);
                Eff3DActor.New(loc, 1.0F, "3DO/Effects/Aircraft/FireGND.eff", 1200.0F);
                Eff3DActor.New(loc, 1.0F, "3DO/Effects/Aircraft/BlackHeavyGND.eff", 1200.0F);
            }
        } else Explosions.ExplodeFuel(this.FM.Loc);
    }

    public void msgCollisionRequest(Actor actor, boolean[] bools) {
        boolean bool = Engine.collideEnv().isDoCollision() && World.getPlayerAircraft() != this;
        if (actor instanceof BigshipGeneric) {
            this.FM.Gears.bFlatTopGearCheck = true;
            if (bool && (Time.tickCounter() + this.hashCode() & 0xf) != 0) bools[0] = false;
        } else if (bool && (Time.tickCounter() & 0xf) != 0 && actor instanceof Aircraft && this.FM.Gears.isUnderDeck()) bools[0] = !((Aircraft) actor).FM.Gears.isUnderDeck();
        if (Engine.collideEnv().isDoCollision() && actor instanceof Aircraft && Mission.isCoop() && actor.isNetMirror() && (this.isMirrorUnderDeck() || this.FM.Gears.isUnderDeck() || Time.tickCounter() <= 2)) bools[0] = false;
    }

    public void msgCollision(Actor actor, String string, String string_45_) {
        if ((!this.isNet() || !this.isNetMirror()) && (!(actor instanceof ActorCrater) || string.startsWith("Gear") && (this.netUser() == null || this.netUser() != ((ActorCrater) actor).netOwner))) {
            if (this == World.getPlayerAircraft()) TimeSkip.airAction(1);
            this.FM.dryFriction = 1.0F;
            // TODO: Added by SAS~Storebror: Fix possible null pointer dereference
            if (string == null) return;
            if (string.startsWith("Pilot")) {
                if (this != World.getPlayerAircraft() || World.cur().diffCur.Vulnerability) {
                    int i = string.charAt(5) - 49;
                    this.killPilot(this, i);
                }
            } else if (string.startsWith("Head")) {
                if (this != World.getPlayerAircraft() || World.cur().diffCur.Vulnerability) {
                    int i = string.charAt(4) - 49;
                    this.killPilot(this, i);
                }
            } else if (actor instanceof Wreckage) {
                if (!string.startsWith("CF_") && actor.getOwner() != this && (this.netUser() == null || this.netUser() != ((Wreckage) actor).netOwner)) {
                    actor.collide(false);
                    this.nextDMGLevels(3, 0, string, this);
                }
            } else if (actor instanceof Paratrooper) {
                this.FM.getSpeed(v1);
                actor.getSpeed(Vd);
                Vd.x -= v1.x;
                Vd.y -= v1.y;
                Vd.z -= v1.z;
                if (Vd.length() > 30.0) {
                    this.setDamager(actor, 4);
                    this.nextDMGLevels(4, 0, string, actor);
                }
            } else {
                // TODO: Added by SAS~Storebror: Fix possible null pointer dereference
                if (actor instanceof RocketryRocket && string_45_ != null && string_45_.startsWith("Wing")) {
                    RocketryRocket rocketryrocket = (RocketryRocket) actor;
                    Loc loc = new Loc();
                    Point3d point3d = new Point3d();
                    Vector3d vector3d = new Vector3d();
                    Vector3d vector3d_46_ = new Vector3d();
                    this.pos.getAbs(loc);
                    point3d.set(actor.pos.getAbsPoint());
                    loc.transformInv(point3d);
                    boolean bool = point3d.y > 0.0;
                    vector3d.set(0.0, bool ? this.hierMesh().collisionR() : -this.hierMesh().collisionR(), 0.0);
                    loc.transform(vector3d);
                    point3d.set(this.FM.Loc);
                    point3d.add(vector3d);
                    actor.pos.getAbs(loc);
                    loc.transformInv(point3d);
                    vector3d.set(this.FM.Vwld);
                    actor.pos.speed(vector3d_46_);
                    vector3d.sub(vector3d_46_);
                    loc.transformInv(vector3d);
                    Vector3d vector3d_47_ = vector3d;
                    vector3d_47_.z = vector3d_47_.z + (bool ? 1.0 : -1.0) * this.FM.getW().x * this.hierMesh().collisionR();
                    if (vector3d.x * vector3d.x + vector3d.y * vector3d.y < 4.0) {
                        if (point3d.y * vector3d.z > 0.0) rocketryrocket.sendRocketStateChange('a', this);
                        else rocketryrocket.sendRocketStateChange('b', this);
                        return;
                    }
                    rocketryrocket.sendRocketStateChange(bool ? 'l' : 'r', this);
                }
                if (!this.FM.turnOffCollisions || !string.startsWith("Wing") && !string.startsWith("Arone") && !string.startsWith("Keel") && !string.startsWith("Rudder") && !string.startsWith("Stab") && !string.startsWith("Vator")
                        && !string.startsWith("Nose") && !string.startsWith("Tail")) {
                    if (actor instanceof Aircraft && Actor.isValid(actor) && this.getArmy() == actor.getArmy()) {
                        double d = Engine.cur.land.HQ(this.FM.Loc.x, this.FM.Loc.y);
                        Aircraft aircraft_48_ = (Aircraft) actor;
                        if (this.FM.Loc.z - 2.0F * this.FM.Gears.H < d && aircraft_48_.FM.Loc.z - 2.0F * aircraft_48_.FM.Gears.H < d) this.setDamagerExclude(actor);
                    }
                    if (/* string != null && */this.hierMesh().chunkFindCheck(string) != -1) {
                        this.hierMesh().setCurChunk(string);
                        this.hierMesh().getChunkLocObj(tmpLoc1);
                        Vd.set(this.FM.Vwld);
                        this.FM.Or.transformInv(Vd);
                        Vd.normalize();
                        Vd.negate();
                        Vd.scale(2000.0F / this.FM.M.mass);
                        Vd.cross(tmpLoc1.getPoint(), Vd);
                        this.FM.getW().x += (float) Vd.x;
                        this.FM.getW().y += (float) Vd.y;
                        this.FM.getW().z += (float) Vd.z;
                    }
                    this.setDamager(actor, 4);
                    this.nextDMGLevels(4, 0, string, actor);
                }
            }
        }
    }

    private void splintersHit(Explosion explosion) {
        float[] fs = new float[2];
        float f = this.mesh().collisionR();
        float f_49_ = 1.0F;
        this.pos.getTime(Time.current(), tmpLocExp);
        tmpLocExp.get(Pd);
        explosion.computeSplintersHit(Pd, f, 1.0F, fs);
        Shot shot = new Shot();
        shot.chunkName = "CF_D0";
        shot.initiator = explosion.initiator;
        shot.tickOffset = Time.tickOffset();
        int i = (int) (fs[0] * 2.0F + 0.5F);
        if (i > 0) {
            while (i > 192) {
                i *= 0.5F;
                f_49_ *= 2.0F;
            }
            for (int i_50_ = 0; i_50_ < i; i_50_++) {
                tmpP1.set(explosion.p);
                tmpLocExp.get(tmpP2);
                double d = tmpP1.distance(tmpP2);
                tmpP2.add(World.Rnd().nextDouble(-f, f), World.Rnd().nextDouble(-f, f), World.Rnd().nextDouble(-f, f));
                if (d > f) tmpP1.interpolate(tmpP1, tmpP2, 1.0 - f / d);
                tmpP2.interpolate(tmpP1, tmpP2, 2.0);
                int i_51_ = this.hierMesh().detectCollisionLineMulti(tmpLocExp, tmpP1, tmpP2);
                if (i_51_ > 0) {
                    Shot shot_52_ = shot;
                    shot_52_.mass = 0.015F * World.Rnd().nextFloat(0.25F, 1.75F) * f_49_;
                    if (World.Rnd().nextFloat() < 0.1F) {
                        Shot shot_53_ = shot;
                        shot_53_.mass = 0.015F * World.Rnd().nextFloat(0.1F, 10.0F) * f_49_;
                    }
                    float f_54_ = explosion.power * 10.0F;
                    if (shot.mass > f_54_) shot.mass = f_54_;
                    Point3d point3d = shot.p;
                    Point3d point3d_55_ = tmpP1;
                    Point3d point3d_56_ = tmpP2;
                    this.hierMesh();
                    point3d.interpolate(point3d_55_, point3d_56_, Mesh.collisionDistMulti(0));
                    if (World.Rnd().nextFloat() < 0.333333F) shot.powerType = 2;
                    else if (World.Rnd().nextFloat() < 0.5F) shot.powerType = 3;
                    else shot.powerType = 0;
                    shot.v.x = (float) (tmpP2.x - tmpP1.x);
                    shot.v.y = (float) (tmpP2.y - tmpP1.y);
                    shot.v.z = (float) (tmpP2.z - tmpP1.z);
                    shot.v.normalize();
                    if (World.Rnd().nextFloat() < 0.02F) shot.v.scale(fs[1] * World.Rnd().nextFloat(0.1F, 10.0F));
                    else shot.v.scale(fs[1] * World.Rnd().nextFloat(0.9F, 1.1F));
                    this.msgShot(shot);
                }
            }
        }
    }

    public void msgExplosion(Explosion explosion) {
        if (this == World.getPlayerAircraft()) TimeSkip.airAction(3);
        this.setExplosion(explosion);
        this.FM.dryFriction = 1.0F;
        if (explosion.power <= 0.0F || explosion.chunkName != null && explosion.chunkName.equals(partNames[43]))
            this.debugprintln("Splash hit from " + (explosion.initiator instanceof Aircraft ? ((Aircraft) explosion.initiator).typedName() : explosion.initiator.name()) + " in " + explosion.chunkName + " is Nill..");
        else {
            int i = explosion.powerType;
            if (i == 1) this.splintersHit(explosion);
            else {
                float f = explosion.power;
                float f_57_ = 0.0F;
                int i_58_ = explosion.powerType;
                if (i_58_ == 0) {
                    f *= 0.5F;
                    f_57_ = f;
                }
                if (explosion.chunkName != null) {
                    if (explosion.chunkName.startsWith("Wing") && explosion.chunkName.endsWith("_D3")) this.FM.setCapableOfACM(false);
                    if (explosion.chunkName.startsWith("Wing") && explosion.power > 0.017F) {
                        if (explosion.chunkName.startsWith("WingL")) {
                            this.debugprintln("Large Shockwave Hits the Left Wing - Wing Stalls.");
                            this.FM.AS.setFMSFX(explosion.initiator, 2, 20);
                        }
                        if (explosion.chunkName.startsWith("WingR")) {
                            this.debugprintln("Large Shockwave Hits the Right Wing - Wing Stalls.");
                            this.FM.AS.setFMSFX(explosion.initiator, 3, 20);
                        }
                    }
                }
                float f_59_;
                if (explosion.chunkName == null) f_59_ = explosion.receivedTNT_1meter(this);
                else f_59_ = f;
                if (!(f_59_ <= 5.0000006E-7F)) {
                    this.debugprintln("Splash hit from " + (explosion.initiator instanceof Aircraft ? ((Aircraft) explosion.initiator).typedName() : explosion.initiator.name()) + " in " + explosion.chunkName + " for "
                            + (int) (100.0F * f_59_ / (0.01F + 3.0F * this.FM.Sq.getToughness(this.part(explosion.chunkName)))) + " % ( " + f_59_ + " kg)..");
                    if (explosion.chunkName == null) f_59_ /= 0.01F;
                    else {
                        if (explosion.chunkName.endsWith("_D0") && !explosion.chunkName.startsWith("Gear")) {
                            if (f_59_ > 0.01F) f_59_ = 1.0F + (f_59_ - 0.01F) / this.FM.Sq.getToughness(this.part(explosion.chunkName));
                            else f_59_ /= 0.01F;
                        } else f_59_ /= this.FM.Sq.getToughness(this.part(explosion.chunkName));
                        f_59_ += this.FM.Sq.eAbsorber[this.part(explosion.chunkName)];
                    }
                    if (f_59_ >= 1.0F) this.setDamager(explosion.initiator, (int) f_59_);
                    if (explosion.chunkName != null) {
                        if ((int) f_59_ > 0) {
                            this.setDamager(explosion.initiator, 1);
                            if (explosion.chunkName.startsWith("Pilot")) {
                                this.killPilot(explosion.initiator, explosion.chunkName.charAt(5) - '1');
                                return;
                            }
                            if (explosion.chunkName.startsWith("Head")) {
                                this.killPilot(explosion.initiator, explosion.chunkName.charAt(4) - '1');
                                return;
                            }
                        }
                        this.nextDMGLevels((int) f_59_, 1, explosion.chunkName, explosion.initiator);
                    } else for (int i_60_ = 0; i_60_ < partNamesForAll.length; i_60_++) {
                        int i_61_ = World.Rnd().nextInt(partNamesForAll.length);
                        if (this.isChunkAnyDamageVisible(partNamesForAll[i_61_])) {
                            this.nextDMGLevels((int) f_59_, 1, partNamesForAll[i_61_] + "_D0", explosion.initiator);
                            break;
                        }
                    }
                    if (explosion.chunkName != null) this.FM.Sq.eAbsorber[this.part(explosion.chunkName)] = f_59_ - (int) f_59_;
                    if (f_59_ > 8.0F) if (f_59_ / partNamesForAll.length > 1.5F) {
                        for (int i_62_ = 0; i_62_ < partNamesForAll.length; i_62_++)
                            if (this.isChunkAnyDamageVisible(partNamesForAll[i_62_])) this.nextDMGLevels(3, 1, partNamesForAll[i_62_] + "_D0", explosion.initiator);
                    } else {
                        int i_63_ = (int) f_59_ / 3 - 1;
                        if (i_63_ > partNamesForAll.length * 2) i_63_ = partNamesForAll.length * 2;
                        for (int i_64_ = 0; i_64_ < i_63_; i_64_++) {
                            int i_65_ = World.Rnd().nextInt(partNamesForAll.length);
                            if (this.isChunkAnyDamageVisible(partNamesForAll[i_65_])) this.nextDMGLevels(3, 1, partNamesForAll[i_65_] + "_D0", explosion.initiator);
                        }
                    }
                    if (bWasAlive && this.FM.isTakenMortalDamage() && this.getDamager() instanceof Aircraft && this.FM.actor.getArmy() != this.getDamager().getArmy() && World.Rnd().nextInt(0, 99) < 66) {
                        if (!this.buried) Voice.speakNiceKill((Aircraft) this.getDamager());
                        this.buried = true;
                    }
                    bWasAlive = true;
                    if (f_57_ > 0.0F) {
                        MsgExplosionPostVarSet msgexplosionpostvarset = new MsgExplosionPostVarSet();
                        msgexplosionpostvarset.THIS = this;
                        msgexplosionpostvarset.chunkName = explosion.chunkName;
                        msgexplosionpostvarset.p.set(explosion.p);
                        msgexplosionpostvarset.initiator = explosion.initiator;
                        msgexplosionpostvarset.power = f_57_;
                        msgexplosionpostvarset.radius = explosion.radius;
                        new MsgAction(false, msgexplosionpostvarset) {
                            public void doAction(Object object) {
                                MsgExplosionPostVarSet msgexplosionpostvarset_67_ = (MsgExplosionPostVarSet) object;
                                if (Actor.isValid(msgexplosionpostvarset_67_.THIS)) MsgExplosion.send(msgexplosionpostvarset_67_.THIS, msgexplosionpostvarset_67_.chunkName, msgexplosionpostvarset_67_.p, msgexplosionpostvarset_67_.initiator,
                                        48.0F * msgexplosionpostvarset_67_.power, msgexplosionpostvarset_67_.power, 1, Math.max(msgexplosionpostvarset_67_.radius, 30.0F));
                            }
                        };
                    }
                }
            }
        }
    }

    protected void doRicochet(Shot shot) {
        v1.x *= World.Rnd().nextFloat(0.25F, 1.0F);
        v1.y *= World.Rnd().nextFloat(-1.0F, -0.25F);
        v1.z *= World.Rnd().nextFloat(-1.0F, -0.25F);
        v1.normalize();
        v1.scale(World.Rnd().nextFloat(10.0F, 600.0F));
        this.FM.Or.transform(v1);
        this.doRicochet(shot.p, v1);
        shot.power = 0.0F;
    }

    protected void doRicochetBack(Shot shot) {
        v1.x *= -1.0;
        v1.y *= -1.0;
        v1.z *= -1.0;
        v1.scale(World.Rnd().nextFloat(0.25F, 1.0F));
        this.FM.Or.transform(v1);
        this.doRicochet(shot.p, v1);
    }

    protected void doRicochet(Point3d point3d, Vector3d vector3d) {
        BallisticProjectile ballisticprojectile = new BallisticProjectile(point3d, vector3d, 1.0F);
        Eff3DActor.New(ballisticprojectile, null, null, 4.0F, "3DO/Effects/Tracers/TrailRicochet.eff", 1.0F);
        this.pos.getAbs(tmpLoc1);
        tmpLoc1.transformInv(point3d);
        Eff3DActor.New(this, null, new Loc(point3d), 1.0F, "3DO/Effects/Fireworks/12mmRicochet.eff", 0.2F);
        Eff3DActor.New(this, null, new Loc(point3d), 0.5F, "3DO/Effects/Fireworks/20_Sparks.eff", -1.0F);
    }

    protected void setShot(Shot shot) {
        if ((this == World.getPlayerAircraft() || this.isNetPlayer()) && !World.cur().diffCur.Vulnerability) {
            shot.chunkName = partNames[43];
            shot.power = 0.0F;
            shot.mass = 0.0F;
        }
        if (bWasAlive) bWasAlive = !this.FM.isTakenMortalDamage();
        v1.sub(shot.v, this.FM.Vwld);
        double d = v1.length();
        shot.power = (float) (shot.mass * d * d) * 0.5F;
        if (shot.powerType == 0) shot.power *= 0.666F;
        this.FM.Or.transformInv(v1);
        v1.normalize();
        tmpLoc1.set(shot.p);
        this.pos.getAbs(tmpLoc2);
        this.pos.getCurrent(tmpLoc3);
        tmpLoc3.interpolate(tmpLoc2, shot.tickOffset);
        tmpLoc1.sub(tmpLoc3);
        tmpLoc1.get(Pd);
        Vd.set(shot.v);
        Vd.normalize();
        Vd.scale(0.10000000149011612);
        tmpP1.set(shot.p);
        tmpP1.sub(Vd);
        Vd.normalize();
        Vd.scale(48.900001525878906);
        tmpP2.set(shot.p);
        tmpP2.add(Vd);
        tmpBonesHit = this.hierMesh().detectCollisionLineMulti(tmpLoc3, tmpP1, tmpP2);
        if (Config.isUSE_RENDER() && World.cur().isArcade()) {
            ActorSimpleMesh actorsimplemesh = new ActorSimpleMesh("3DO/Arms/MatrixXX/mono.sim");
            actorsimplemesh.pos.setBase(this, null, false);
            tmpOr.setAT0(v1);
            actorsimplemesh.pos.setRel(Pd, tmpOr);
            float f = (float) Math.sqrt(Math.sqrt(shot.mass));
            actorsimplemesh.mesh().setScaleXYZ(0.75F * f, f, f);
            actorsimplemesh.drawing(true);
            actorsimplemesh.postDestroy(Time.current() + 30000L);
        }
    }

    protected void setExplosion(Explosion explosion) {
        if ((this == World.getPlayerAircraft() || this.isNetPlayer()) && !World.cur().diffCur.Vulnerability) explosion.chunkName = partNames[43];
        if (explosion.chunkName == null && !this.isChunkAnyDamageVisible("CF")) explosion.chunkName = partNames[43];
        if (bWasAlive) bWasAlive = !this.FM.isTakenMortalDamage();
    }

    protected void msgSndShot(float f, double d, double d_68_, double d_69_) {
        if (Config.isUSE_RENDER()) {
            Actor._tmpPoint.set(d, d_68_, d_69_);
            this.sfxHit(f, Actor._tmpPoint);
            if (this.isNet() && this.FM.isPlayers() && this.FM instanceof RealFlightModel) {
                this.FM.dryFriction = 1.0F;
                ((RealFlightModel) this.FM).producedShakeLevel = 1.0F;
                float f_70_ = 2000.0F * f / this.FM.M.mass;
                this.FM.getW().add(World.Rnd().nextFloat(-f_70_, f_70_), World.Rnd().nextFloat(-f_70_, f_70_), World.Rnd().nextFloat(-f_70_, f_70_));
            }
        }
    }

    public void msgShot(Shot shot) {
        if (this == World.getPlayerAircraft()) TimeSkip.airAction(2);
        this.setShot(shot);
        if (!this.isNet()) {
            this.FM.dryFriction = 1.0F;
            if (this.FM.isPlayers() && this.FM instanceof RealFlightModel) ((RealFlightModel) this.FM).producedShakeLevel = 1.0F;
            float f = 2000.0F * shot.mass / this.FM.M.mass;
            this.FM.getW().add(World.Rnd().nextFloat(-f, f), World.Rnd().nextFloat(-f, f), World.Rnd().nextFloat(-f, f));
        }
        if (shot.chunkName != null) if (shot.chunkName == partNames[43]) {
            if (World.Rnd().nextFloat() < 0.25F) this.doRicochet(shot);
        } else {
            if (shot.chunkName.startsWith("Wing") && (shot.chunkName.endsWith("_D3") || shot.chunkName.endsWith("_D2") && this.FM.Skill >= 2)) this.FM.setCapableOfACM(false);
            if (this.FM instanceof Pilot && World.Rnd().nextInt(-1, 8) < this.FM.Skill) ((Pilot) this.FM).setAsDanger(shot.initiator);
            if (Config.isUSE_RENDER() && this.FM instanceof RealFlightModel) {
                Actor._tmpPoint.set(this.pos.getAbsPoint());
                Actor._tmpPoint.sub(shot.p);
                this.msgSndShot(shot.mass, Actor._tmpPoint.x, Actor._tmpPoint.y, Actor._tmpPoint.z);
            }
            shot.bodyMaterial = 2;
            if (this.isNetPlayer()) this.sendMsgSndShot(shot);
            if (tmpBonesHit > 0) {
                this.debuggunnery("");
                this.debuggunnery("New Bullet: E = " + (int) shot.power + " [J], M = " + (int) (1000.0F * shot.mass) + " [g], Type = (" + this.sttp(shot.powerType) + ")");
                if (shot.powerType == 1) tmpBonesHit = Math.min(tmpBonesHit, 2);
                for (int i = 0; i < tmpBonesHit; i++) {
                    this.hierMesh();
                    String string = Mesh.collisionNameMulti(i, 1);
                    if (string.length() == 0) {
                        this.hierMesh();
                        string = Mesh.collisionNameMulti(i, 0);
                    }
                    if (shot.power > 0.0F) {
                        Point3d point3d = Pd;
                        Point3d point3d_71_ = tmpP1;
                        Point3d point3d_72_ = tmpP2;
                        this.hierMesh();
                        point3d.interpolate(point3d_71_, point3d_72_, Mesh.collisionDistMulti(i));
                        tmpLoc3.transformInv(Pd);
                        this.debuggunnery("Hit Bone [" + string + "], E = " + (int) shot.power);
                        this.hitBone(string, shot, Pd);
                        if (!string.startsWith("xx")) {
                            Aircraft aircraft_73_ = this;
                            float f = 33.333F;
                            float f_74_;
                            if (i == tmpBonesHit - 1) f_74_ = 0.02F;
                            else {
                                this.hierMesh();
                                float f_75_ = Mesh.collisionDistMulti(i + 1);
                                this.hierMesh();
                                f_74_ = f_75_ - Mesh.collisionDistMulti(i);
                            }
                            aircraft_73_.getEnergyPastArmor(f * f_74_, shot);
                            if (World.Rnd().nextFloat() < 0.05F) {
                                shot.power = 0.0F;
                                this.debuggunnery("Inner Ricochet");
                            }
                        }
                    }
                }
            }
            boolean bool = false;
            for (int i = 0; i < tmpBonesHit; i++) {
                this.hierMesh();
                if (Mesh.collisionNameMulti(i, 1) != null) {
                    this.hierMesh();
                    String string = Mesh.collisionNameMulti(i, 1);
                    this.hierMesh();
                    if (!string.equals(Mesh.collisionNameMulti(i, 0))) continue;
                }
                bool = true;
            }
            if (bool) {
                this.debuggunnery("[+++ PROCESS OLD +++]");
                Shot shot_76_ = shot;
                this.hierMesh();
                shot_76_.chunkName = Mesh.collisionNameMulti(0, 0);
                if (shot.chunkName.startsWith("WingLOut") && World.Rnd().nextInt(0, 99) < 20) shot.chunkName = "AroneL_D0";
                if (shot.chunkName.startsWith("WingROut") && World.Rnd().nextInt(0, 99) < 20) shot.chunkName = "AroneR_D0";
                if (shot.chunkName.startsWith("StabL") && World.Rnd().nextInt(0, 99) < 45) shot.chunkName = "VatorL_D0";
                if (shot.chunkName.startsWith("StabR") && World.Rnd().nextInt(0, 99) < 45) shot.chunkName = "VatorR_D0";
                if (shot.chunkName.startsWith("Keel1") && World.Rnd().nextInt(0, 99) < 33) shot.chunkName = "Rudder1_D0";
                if (shot.chunkName.startsWith("Keel2") && World.Rnd().nextInt(0, 99) < 33) shot.chunkName = "Rudder2_D0";
                float f = shot.powerToTNT();
                this.debugprintln("Bullet hit from " + (shot.initiator instanceof Aircraft ? ((Aircraft) shot.initiator).typedName() : shot.initiator.name()) + " in " + shot.chunkName + " for "
                        + (int) (100.0F * f / (0.01F + 3.0F * this.FM.Sq.getToughness(this.part(shot.chunkName)))) + " %..");
                shot.bodyMaterial = 2;
                if (this.FM instanceof Pilot && World.Rnd().nextInt(-1, 8) < this.FM.Skill) ((Pilot) this.FM).setAsDanger(shot.initiator);
                if (f <= 5.0000006E-7F) return;
                if (shot.chunkName.endsWith("_D0") && !shot.chunkName.startsWith("Gear")) {
                    if (f > 0.01F) f = 1.0F + (f - 0.01F) / this.FM.Sq.getToughness(this.part(shot.chunkName));
                    else f /= 0.01F;
                } else f /= this.FM.Sq.getToughness(this.part(shot.chunkName));
                f += this.FM.Sq.eAbsorber[this.part(shot.chunkName)];
                int i = (int) f;
                this.FM.Sq.eAbsorber[this.part(shot.chunkName)] = f - i;
                if (i > 0) {
                    this.setDamager(shot.initiator, i);
                    if (shot.chunkName.startsWith("Pilot")) {
                        this.killPilot(shot.initiator, shot.chunkName.charAt(5) - '1');
                        return;
                    }
                    if (shot.chunkName.startsWith("Head")) {
                        this.killPilot(shot.initiator, shot.chunkName.charAt(4) - '1');
                        return;
                    }
                }
                this.nextDMGLevels(i, 2, shot.chunkName, shot.initiator);
            }
            if (bWasAlive && this.FM.isTakenMortalDamage() && this.getDamager() instanceof Aircraft && this.FM.actor.getArmy() != this.getDamager().getArmy() && World.Rnd().nextInt(0, 99) < 66) {
                if (!this.buried) Voice.speakNiceKill((Aircraft) this.getDamager());
                this.buried = true;
            }
            bWasAlive = true;
        }
    }

    private String sttp(int i) {
        switch (i) {
            case 2:
                return "AP";
            case 3:
                return "API/APIT";
            case 1:
                return "CUMULATIVE";
            case 0:
                return "HE";
            default:
                return null;
        }
    }

    protected void hitBone(String string, Shot shot, Point3d point3d) {
        /* empty */
    }

    protected void hitChunk(String string, Shot shot) {
        if (string.lastIndexOf("_") == -1) string += "_D" + this.chunkDamageVisible(string);
        float f = shot.powerToTNT();
        if (string.endsWith("_D0") && !string.startsWith("Gear")) {
            if (f > 0.01F) f = 1.0F + (f - 0.01F) / this.FM.Sq.getToughness(this.part(string));
            else f /= 0.01F;
        } else f /= this.FM.Sq.getToughness(this.part(string));
        f += this.FM.Sq.eAbsorber[this.part(string)];
        int i = (int) f;
        this.FM.Sq.eAbsorber[this.part(string)] = f - i;
        if (i > 0) this.setDamager(shot.initiator, i);
        this.nextDMGLevels(i, 2, string, shot.initiator);
    }

    protected void hitFlesh(int i, Shot shot, int i_77_) {
        int i_78_ = (int) (shot.power * 0.0035F * World.Rnd().nextFloat(0.5F, 1.5F));
        switch (i_77_) {
            case 0:
                if (!(World.Rnd().nextFloat() < 0.05F)) {
                    if (shot.initiator == World.getPlayerAircraft() && World.cur().isArcade()) HUD.logCenter("H E A D S H O T");
                    i_78_ *= 30.0F;
                } else return;
                break;
            case 1:
                break;
            case 2:
                i_78_ /= 3.0F;
                break;
        }
        this.debuggunnery("*** Pilot " + i + " hit for " + i_78_ + "% (" + (int) shot.power + " J)");
        this.FM.AS.hitPilot(shot.initiator, i, i_78_);
        if (this.FM.AS.astatePilotStates[i] > 95 && i_77_ == 0) this.debuggunnery("*** Headshot!.");
    }

    protected float getEnergyPastArmor(float f, float f_79_, Shot shot) {
        Shot shot_80_ = shot;
        shot_80_.power = (float) (shot_80_.power - (shot.powerType == 0 ? 2.0F : 1.0F) * (f * 1700.0F * Math.cos(f_79_)));
        return shot.power;
    }

    protected float getEnergyPastArmor(float f, Shot shot) {
        Shot shot_81_ = shot;
        shot_81_.power = shot_81_.power - (shot.powerType == 0 ? 2.0F : 1.0F) * (f * 1700.0F);
        return shot.power;
    }

    public static boolean isArmorPenetrated(float f, Shot shot) {
        return shot.power > (shot.powerType == 0 ? 2.0F : 1.0F) * (f * 1700.0F);
    }

    protected float getEnergyPastArmor(double d, float f, Shot shot) {
        Shot shot_82_ = shot;
        shot_82_.power = (float) (shot_82_.power - (shot.powerType == 0 ? 2.0F : 1.0F) * (d * 1700.0 * Math.cos(f)));
        return shot.power;
    }

    protected float getEnergyPastArmor(double d, Shot shot) {
        Shot shot_83_ = shot;
        shot_83_.power = (float) (shot_83_.power - (shot.powerType == 0 ? 2.0F : 1.0F) * (d * 1700.0));
        return shot.power;
    }

    public static boolean isArmorPenetrated(double d, Shot shot) {
        return shot.power > (shot.powerType == 0 ? 2.0F : 1.0F) * (d * 1700.0);
    }

    protected void netHits(int i, int i_84_, int i_85_, Actor actor) {
        if (this.isNetMaster()) this.setDamager(actor, i);
        while (i-- > 0)
            this.nextDMGLevel(partNames[i_85_] + "_D0", i_84_, actor);
    }

    public int curDMGProp(int i) {
        String string = "Prop" + (i + 1) + "_D1";
        HierMesh hiermesh = this.hierMesh();
        if (hiermesh.chunkFindCheck(string) < 0) return 0;
        if (hiermesh.isChunkVisible(string)) return 1;
        return 0;
    }

    protected void addGun(BulletEmitter bulletemitter, int i) {
        if (this == World.getPlayerAircraft() && !World.cur().diffCur.Limited_Ammo) bulletemitter.loadBullets(-1);
        String string = bulletemitter.getHookName();
        if (string != null) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i];
            int i_86_;
            if (bulletemitters == null) i_86_ = 0;
            else i_86_ = bulletemitters.length;
            BulletEmitter[] bulletemitters_87_ = new BulletEmitter[i_86_ + 1];
            int i_88_;
            for (i_88_ = 0; i_88_ < i_86_; i_88_++)
                bulletemitters_87_[i_88_] = bulletemitters[i_88_];
            bulletemitters_87_[i_88_] = bulletemitter;
            this.FM.CT.Weapons[i] = bulletemitters_87_;
            if (bulletemitter.isEnablePause()) this.bGunPodsExist = true;
        }
    }

    public void detachGun(int i) {
        if (this.FM == null || this.FM.CT == null || this.FM.CT.Weapons == null) return;

        for (int i_89_ = 0; i_89_ < this.FM.CT.Weapons.length; i_89_++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i_89_];
            if (bulletemitters != null) for (int i_90_ = 0; i_90_ < bulletemitters.length; i_90_++)
                bulletemitters[i_90_] = bulletemitters[i_90_].detach(this.hierMesh(), i);
        }
    }

    public Gun getGunByHookName(String string) {
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i];
            if (bulletemitters != null) for (int i_91_ = 0; i_91_ < bulletemitters.length; i_91_++)
                if (bulletemitters[i_91_] instanceof Gun) {
                    Gun gun = (Gun) bulletemitters[i_91_];
                    if (string.equals(gun.getHookName())) return gun;
                }
        }
        return GunEmpty.get();
    }

    public BulletEmitter getBulletEmitterByHookName(String string) {
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i];
            if (bulletemitters != null) for (int i_92_ = 0; i_92_ < bulletemitters.length; i_92_++)
                if (string.equals(bulletemitters[i_92_].getHookName())) return bulletemitters[i_92_];
        }
        return GunEmpty.get();
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        /* empty */
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    public void forceGear(float f) {
        this.moveGear(f);
    }

    public static void forceGear(Class var_class, HierMesh hiermesh, float f) {
        try {
            Method method = var_class.getMethod("moveGear", new Class[] { com.maddox.il2.engine.HierMesh.class, Float.TYPE });
            method.invoke(null, new Object[] { hiermesh, new Float(f) });
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void moveArrestorHook(float f) {
        /* empty */
    }

    protected void moveWingFold(HierMesh hiermesh, float f) {
        /* empty */
    }

    public void moveWingFold(float f) {
        /* empty */
    }

    public void moveCockpitDoor(float f) {
        /* empty */
    }

    protected void moveRudder(float f) {
        /* empty */
    }

    protected void moveElevator(float f) {
        /* empty */
    }

    protected void moveAileron(float f) {
        /* empty */
    }

    protected void moveFlap(float f) {
        /* empty */
    }

    protected void moveBayDoor(float f) {
        /* empty */
    }

    protected void moveAirBrake(float f) {
        /* empty */
    }

    public void moveSteering(float f) {
        /* empty */
    }

    public void moveWheelSink() {
        /* empty */
    }

    public void rareAction(float f, boolean bool) {
        /* empty */
    }

    protected void moveFan(float f) {
        int i = 0;
        for (int engineIndex = 0; engineIndex < this.FM.EI.getNum(); engineIndex++) {
            if (engineIndex >= this.oldProp.length || 
                    engineIndex >= this.FM.EI.engines.length || 
                    engineIndex >= Props.length || 
                    engineIndex >= this.propPos.length) break;
            if (this.oldProp[engineIndex] < 2) {
                i = Math.abs((int) (this.FM.EI.engines[engineIndex].getw() * 0.06F));
                if (i >= 1) i = 1;
                if (i != this.oldProp[engineIndex] && this.hierMesh().isChunkVisible(Props[engineIndex][this.oldProp[engineIndex]])) {
                    this.hierMesh().chunkVisible(Props[engineIndex][this.oldProp[engineIndex]], false);
                    this.oldProp[engineIndex] = i;
                    this.hierMesh().chunkVisible(Props[engineIndex][i], true);
                }
            }
            if (i == 0) this.propPos[engineIndex] = (this.propPos[engineIndex] + 57.3F * this.FM.EI.engines[engineIndex].getw() * f) % 360.0F;
            else {
                float newPropPos = 57.3F * this.FM.EI.engines[engineIndex].getw();
                newPropPos %= 2880.0F;
                newPropPos /= 2880.0F;
                if (newPropPos <= 0.5F) newPropPos *= 2.0F;
                else newPropPos = newPropPos * 2.0F - 2.0F;
                newPropPos *= 1200.0F;
                this.propPos[engineIndex] = (this.propPos[engineIndex] + newPropPos * f) % 360.0F;
            }
            this.hierMesh().chunkSetAngles(Props[engineIndex][0], 0.0F, -this.propPos[engineIndex], 0.0F);
        }
    }

    public void hitProp(int i, int i_95_, Actor actor) {
        if (i <= this.FM.EI.getNum() - 1 && this.oldProp[i] != 2) {
            super.hitProp(i, i_95_, actor);
            this.FM.cut(this.part("Engine" + (i + 1)), i_95_, actor);
            if (this.isChunkAnyDamageVisible("Prop" + (i + 1)) || this.isChunkAnyDamageVisible("PropRot" + (i + 1))) {
                this.hierMesh().chunkVisible(Props[i][0], false);
                this.hierMesh().chunkVisible(Props[i][1], false);
                this.hierMesh().chunkVisible(Props[i][2], true);
            }
            this.FM.EI.engines[i].setFricCoeffT(1.0F);
            this.oldProp[i] = 2;
        }
    }

    public void updateLLights() {
        this.pos.getRender(Actor._tmpLoc);
        if (this.lLight == null) {
            if (!(Actor._tmpLoc.getX() < 1.0)) {
                this.lLight = new LightPointWorld[] { null, null, null, null };
                for (int i = 0; i < 4; i++) {
                    this.lLight[i] = new LightPointWorld();
                    this.lLight[i].setColor(0.49411765F, 0.9098039F, 0.9607843F);
                    this.lLight[i].setEmit(0.0F, 0.0F);
                    try {
                        this.lLightHook[i] = new HookNamed(this, "_LandingLight0" + i);
                    } catch (Exception exception) {
                        /* empty */
                    }
                }
            }
        } else for (int i = 0; i < 4; i++)
            if (this.FM.AS.astateLandingLightEffects[i] != null) {
                lLightLoc1.set(0.0, 0.0, 0.0, 0.0F, 0.0F, 0.0F);
                this.lLightHook[i].computePos(this, Actor._tmpLoc, lLightLoc1);
                lLightLoc1.get(lLightP1);
                lLightLoc1.set(1000.0, 0.0, 0.0, 0.0F, 0.0F, 0.0F);
                this.lLightHook[i].computePos(this, Actor._tmpLoc, lLightLoc1);
                lLightLoc1.get(lLightP2);
                Engine.land();
                if (Landscape.rayHitHQ(lLightP1, lLightP2, lLightPL)) {
                    lLightPL.z++;
                    lLightP2.interpolate(lLightP1, lLightPL, 0.95F);
                    this.lLight[i].setPos(lLightP2);
                    float f = (float) lLightP1.distance(lLightPL);
                    float f_96_ = f * 0.5F + 30.0F;
                    float f_97_ = 0.5F - 0.5F * f / 1000.0F;
                    this.lLight[i].setEmit(f_97_, f_96_);
                } else this.lLight[i].setEmit(0.0F, 0.0F);
            } else if (this.lLight[i].getR() != 0.0F) this.lLight[i].setEmit(0.0F, 0.0F);
    }

    public boolean isUnderWater() {
        Point3d point3d = this.pos.getAbsPoint();
        if (!Engine.land().isWater(point3d.x, point3d.y)) return false;
        return point3d.z < 0.0;
    }

    public void update(float f) {
        if (Config.cur.ini.get("Mods", "debugPropHit", 0) == 1) {
            if (this.FM.AS.bNavLightsOn) {
                this.FM.AS.setNavLightsState(false);
                this.hitProp(0, 0, Engine.actorLand());
            }
        }
            
        super.update(f);
        if (this == World.getPlayerAircraft()) {
            if (Config.hudEngineParams) HUD.training("R:" + (int)this.FM.EI.engines[0].getRPM()
                    + ", F:" + Config.twoDigits.format(this.FM.EI.engines[0].propForce)
                    + ", W:" + Config.twoDigits.format(this.FM.EI.engines[0].tWaterOut)
                    + "/" + Config.twoDigits.format(this.FM.EI.engines[0].tWaterCritMax)
                    + ", O:" + Config.twoDigits.format(this.FM.EI.engines[0].tOilOut)
                    + "/" + Config.twoDigits.format(this.FM.EI.engines[0].tOilCritMax)
                    + ", Fuel:" + Config.twoDigits.format(this.FM.M.fuel)
                    );
            
            // Flight Model Debugging
            if (Config.logTakeoffDistanceOver50ftObstacle && this.spawnPoint != null && !this.airborne && (this.FM.getAltitude() - Landscape.HQ((float)this.pos.getAbsPoint().x, (float)this.pos.getAbsPoint().y) > 15F)) {
                double distance = this.pos.getAbsPoint().distance(this.spawnPoint);
                if (distance > 100D) {
                    this.airborne = true;
                    HUD.log("TO:" + Config.twoDigits.format(distance));
                    System.out.println("##### TO: " + Config.twoDigits.format(distance) + " #####");
                }
            }
            if (this.isUnderWater()) World.doPlayerUnderWater();
            EventLog.flyPlayer(this.pos.getAbsPoint());
            if (this instanceof TypeBomber) ((TypeBomber) this).typeBomberUpdate(f);
        }
        Controls controls = this.FM.CT;
        this.moveFan(f);
        if (controls.bHasGearControl) {
            float f_98_ = controls.getGear();
            if (Math.abs(this.Gear_ - f_98_) > this.EpsSmooth_) {
                if (!(this instanceof I_16)) if (Math.abs(f_98_ - controls.GearControl) <= this.EpsSmooth_) this.sfxGear(false);
                else this.sfxGear(true);
                this.moveGear(this.Gear_ = f_98_);
            }
        }
        if (controls.bHasArrestorControl) {
            float f_99_ = controls.getArrestor();
            if (Math.abs(this.arrestor_ - f_99_) > this.EpsSmooth_) this.moveArrestorHook(this.arrestor_ = f_99_);
        }
        if (controls.bHasWingControl) {
            float f_100_ = controls.getWing();
            if (Math.abs(this.wingfold_ - f_100_) > this.EpsVerySmooth_) this.moveWingFold(this.wingfold_ = f_100_);
        }
        if (controls.bHasCockpitDoorControl) {
            float f_101_ = controls.getCockpitDoor();
            if (Math.abs(this.cockpitDoor_ - f_101_) > this.EpsVerySmooth_) this.moveCockpitDoor(this.cockpitDoor_ = f_101_);
        }
        if (controls.bHasFlapsControl) {
            float f_102_ = controls.getFlap();
            if (Math.abs(this.Flap_ - f_102_) > this.EpsSmooth_) {
                if (Math.abs(f_102_ - controls.FlapsControl) <= this.EpsSmooth_) this.sfxFlaps(false);
                else this.sfxFlaps(true);
                this.moveFlap(this.Flap_ = f_102_);
            }
        }
        float f_103_ = controls.getRudder();
        if (Math.abs(this.Rudder_ - f_103_) > this.EpsCoarse_) this.moveRudder(this.Rudder_ = f_103_);
        f_103_ = controls.getElevator();
        if (Math.abs(this.Elevator_ - f_103_) > this.EpsCoarse_) this.moveElevator(this.Elevator_ = f_103_);
        f_103_ = controls.getAileron();
        if (Math.abs(this.Aileron_ - f_103_) > this.EpsCoarse_) this.moveAileron(this.Aileron_ = f_103_);
        f_103_ = controls.getBayDoor();
        if (Math.abs(this.BayDoor_ - f_103_) > 0.025F) {
            Aircraft aircraft_104_ = this;
            aircraft_104_.BayDoor_ = aircraft_104_.BayDoor_ + 0.025F * (f_103_ > this.BayDoor_ ? 2.0F : -1.0F);
            this.moveBayDoor(this.BayDoor_);
        }
        f_103_ = controls.getAirBrake();
        if (Math.abs(this.AirBrake_ - f_103_) > this.EpsSmooth_) {
            this.moveAirBrake(this.AirBrake_ = f_103_);
            if (Math.abs(this.AirBrake_ - 0.5F) >= 0.48F) this.sfxAirBrake();
        }
        f_103_ = this.FM.Gears.getSteeringAngle();
        if (Math.abs(this.Steering_ - f_103_) > this.EpsSmooth_) this.moveSteering(this.Steering_ = f_103_);
        if (this.FM.Gears.nearGround()) this.moveWheelSink();

        // TODO: Added by |ZUTI|
        // -----------------------------------------------
        ZutiSupportMethods_Air.unfoldAircraftWings(this);
        // -----------------------------------------------
    }

    public void setFM(int i, boolean bool) {
        this.setFM(Property.stringValue(this.getClass(), "FlightModel", null), i, bool);
    }

    public void setFM(String string, int i, boolean bool) {
        if (this instanceof JU_88MSTL) i = 1;
        switch (i) {
            default:
                this.FM = new Pilot(string);
                break;
            case 1:
                this.FM = new RealFlightModel(string);
                break;
            case 2:
                this.FM = new FlightModel(string);
                this.FM.AP = new Autopilotage();
        }
        this.FM.actor = this;
        this.FM.AS.set(this, bool && !NetMissionTrack.isPlaying());
        this.FM.EI.setNotMirror(bool && !NetMissionTrack.isPlaying());
        SectFile sectfile = FlightModelMain.sectFile(string);
        int i_105_ = 0;
        String string_106_ = sectfile.get("SOUND", "FeedType", "PNEUMATIC");
        if (string_106_.compareToIgnoreCase("PNEUMATIC") == 0) i_105_ = 0;
        else if (string_106_.compareToIgnoreCase("ELECTRIC") == 0) i_105_ = 1;
        else if (string_106_.compareToIgnoreCase("HYDRAULIC") == 0) i_105_ = 2;
        else System.out.println("ERROR: Invalid feed type" + string_106_);
        this.FM.set(this.hierMesh());
        forceGear(this.getClass(), this.hierMesh(), 1.0F);
        this.FM.Gears.computePlaneLandPose(this.FM);
        forceGear(this.getClass(), this.hierMesh(), 0.0F);
        this.FM.EI.set(this);
        this.initSound(sectfile);
        this.sfxInit(i_105_);
        this.interpPut(this.FM, "FlightModel", Time.current(), null);
    }

    public void checkTurretSkill() {
        /* empty */
    }

    public void destroy() {
        if (this.isAlive() && Mission.isPlaying() && this.name().charAt(0) != ' ' && this.FM != null) {
            Front.checkAircraftCaptured(this);
            World.onActorDied(this, World.remover);
        }
        if (this.lLight != null) for (int i = 0; i < 4; i++)
            ObjState.destroy(this.lLight[i]);
        if (World.getPlayerAircraft() == this) this.deleteCockpits();
        Wing wing = this.getWing();
        if (Actor.isValid(wing) && wing instanceof NetWing) wing.destroy();
        this.detachGun(-1);
        super.destroy();
        if (World.getPlayerAircraft() == this) World.setPlayerAircraft(null);
        this._removeMesh();

        // TODO: Added by |ZUTI|
        // ---------------------------------------
        ZutiSupportMethods_Air.executeWhenAircraftWasDestroyed(this);
        // ---------------------------------------
    }

    public Object getSwitchListener(Message message) {
        return this;
    }

    public Aircraft() {
        this.wfrGr21dropped = false;
        this.headingBug = 0.0F;
        this.idleTimeOnCarrier = 0;

        // TODO: Storebror: Make Arming Random "real" random!
        // ------------------------------------
        this.armingSeed = TrueRandom.nextInt(65536);
        this.armingRnd = new RangeRandom(this.armingSeed);
        // ------------------------------------

        this.checkLoadingCountry();
        if (this._loadingCountry == null) this._setMesh(Property.stringValue(this.getClass(), "meshName", null));
        else this._setMesh(Property.stringValue(this.getClass(), "meshName_" + this._loadingCountry, null));
        this.collide(true);
        this.drawing(true);
        this.dreamFire(true);
        // TODO: +++ TD AI code backport from 4.13 +++
        this.bSpotter = false;
        this.bombScoreOwner = null;
        // TODO: --- TD AI code backport from 4.13 ---

        // TODO: +++ Enhanced Ordnance View by SAS~Storebror +++
        this.activeOrdnance = new LinkedList();
        this.lastSelectedOrdnance = null;
        // TODO: --- Enhanced Ordnance View by SAS~Storebror ---
    }

    private void checkLoadingCountry() {
        this._loadingCountry = null;
        if (NetAircraft.loadingCountry != null) {
            Class var_class = this.getClass();
            if (Property.value(var_class, "PaintScheme_" + NetAircraft.loadingCountry) != null && Property.stringValue(var_class, "meshName_" + NetAircraft.loadingCountry, null) != null) this._loadingCountry = NetAircraft.loadingCountry;
        }
    }

    public static String getPropertyMeshDemo(Class var_class, String string) {
        String string_107_ = "meshNameDemo";
        String string_108_ = Property.stringValue((Object) var_class, string_107_, null);
        if (string_108_ != null) return string_108_;
        return getPropertyMesh(var_class, string);
    }

    public static String getPropertyMesh(Class var_class, String string) {
        String string_109_ = "meshName";
        String string_110_ = null;
        if (string != null) string_110_ = Property.stringValue(var_class, string_109_ + "_" + string, null);
        if (string_110_ == null) string_110_ = Property.stringValue(var_class, string_109_);
        return string_110_;
    }

    public static PaintScheme getPropertyPaintScheme(Class var_class, String string) {
        String string_111_ = "PaintScheme";
        PaintScheme paintscheme = null;
        if (string != null) paintscheme = (PaintScheme) Property.value(var_class, string_111_ + "_" + string, null);
        if (paintscheme == null) paintscheme = (PaintScheme) Property.value(var_class, string_111_);
        return paintscheme;
    }

    public String typedName() {
        return this.typedName;
    }

    private void correctTypedName() {
        if (this.typedName != null && this.typedName.indexOf('_') >= 0) {
            StringBuffer stringbuffer = new StringBuffer();
            int i = this.typedName.length();
            for (int i_112_ = 0; i_112_ < i; i_112_++) {
                char c = this.typedName.charAt(i_112_);
                if (c != '_') stringbuffer.append(c);
            }
            this.typedName = stringbuffer.toString();
        }
    }

    public void preparePaintScheme() {
        PaintScheme paintscheme = getPropertyPaintScheme(this.getClass(), this._loadingCountry);
        if (paintscheme != null) {
            paintscheme.prepare(this, this.bPaintShemeNumberOn);
            this.typedName = paintscheme.typedName(this);
            this.correctTypedName();
        }
    }

    public void preparePaintScheme(int i) {
        PaintScheme paintscheme = getPropertyPaintScheme(this.getClass(), this._loadingCountry);
        if (paintscheme != null) {
            paintscheme.prepareNum(this, i, this.bPaintShemeNumberOn);
            this.typedName = paintscheme.typedNameNum(this, i);
            this.correctTypedName();
        }
    }

    public void prepareCamouflage() {
        String s = getPropertyMesh(this.getClass(), this._loadingCountry);
//        prepareMeshCamouflage(s, this.hierMesh());
        prepareMeshCamouflage(s, hierMesh(), getClass(), getRegiment());
    }

    public static void prepareMeshCamouflage(String s, HierMesh hiermesh) {
        prepareMeshCamouflage(s, hiermesh, null);
    }

    public static void prepareMeshCamouflage(String s, HierMesh hiermesh, Class class1, Regiment regiment)
    {
        prepareMeshCamouflage(s, hiermesh, null, class1, regiment);
    }
   
    public static void prepareMeshCamouflage(String s, HierMesh hiermesh, String s1) {
        prepareMeshCamouflage(s, hiermesh, s1, null);
    }

    public static void prepareMeshCamouflage(String s, HierMesh hiermesh, String s1, Class class1, Regiment regiment)
    {
        prepareMeshCamouflage(s, hiermesh, s1, null, class1, regiment);
    }

    public static void prepareMeshCamouflage(String s, HierMesh hiermesh, String s1, Mat[] amat) {
        prepareMeshCamouflage(s, hiermesh, s1, amat, null, null);
    }

    public static void prepareMeshCamouflage(String s, HierMesh hiermesh, String s1,  Mat[] amat, Class class1, Regiment regiment) {
        if (Config.isUSE_RENDER()) {
            String s2 = s.substring(0, s.lastIndexOf('/') + 1);
            String s3 = "";
            if(class1 != null)
                try
                {
                    Method method = class1.getMethod("getSkinPrefix", new Class[] {
                        java.lang.String.class, com.maddox.il2.ai.Regiment.class
                    });
                    s3 = (String)method.invoke(null, new Object[] {
                        new String(s), regiment
                    });
                }
                catch(Exception exception)
                {
                    System.out.println(exception.getMessage());
                    exception.printStackTrace();
                }
            if (s1 == null) {
                String s5;
                switch (World.cur().camouflage) {
                    case 0:
                        s5 = "summer";
                        break;
                    case 1:
                        s5 = "winter";
                        break;
                    case 2:
                        s5 = "desert";
                        break;
                    case 3:
                        s5 = "pacific";
                        break;
                    case 4:
                        s5 = "eto";
                        break;
                    case 5:
                        s5 = "mto";
                        break;
                    case 6:
                        s5 = "cbi";
                        break;
                    default:
                        s5 = "summer";
                }
                // TODO: +++ Backport from 4.13
//                if (!existSFSFile(s2 + s5 + "/skin1o.tga")) {
//                    s5 = "summer";
//                    if (!existSFSFile(s2 + s5 + "/skin1o.tga")) return;
//                }
//                s1 = s2 + s5;
                String s4 = s3 + s5;
                if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                    if(World.cur().camouflage == 5)
                    {
                        s4 = s3 + "desert";
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            s4 = s5;
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            s4 = "desert";
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            s4 = "summer";
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            return;
                    } else
                    {
                        s4 = s3 + "summer";
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            s4 = s5;
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            s4 = "summer";
                        if(!existSFSFile(s2 + s4 + "/skin1o.tga"))
                            return;
                    }
                s1 = s2 + s4;
                // ---
            }
            String[] as = { s1 + "/skin1o.tga", s1 + "/skin1p.tga", s1 + "/skin1q.tga" };
            int[] ai = new int[4];
            for (int i = 0; i < _skinMat.length; i++) {
                int j = hiermesh.materialFind(_skinMat[i]);
                if (j >= 0) {
                    Mat mat = hiermesh.material(j);
                    if (mat == null) continue; // TODO: By SAS~Storebror: Avoid further null pointer exceptions.
                    boolean flag = false;
                    for (int k = 0; k < 4; k++) {
                        ai[k] = -1;
                        if (mat.isValidLayer(k)) {
                            mat.setLayer(k);
                            String s7 = mat.get('\0');
                            for (int l = 0; l < 3; l++)
                                if (s7.regionMatches(true, s7.length() - 10, _curSkin[l], 0, 10)) {
                                    ai[k] = l;
                                    flag = true;
                                    break;
                                }
                        }
                    }
                    if (flag) {
                        String s6 = s1 + "/" + _skinMat[i] + ".mat";
                        Mat mat1;
                        if (FObj.Exist(s6)) mat1 = (Mat) FObj.Get(s6);
                        else {
                            mat1 = (Mat) mat.Clone();
                            mat1.Rename(s6);
                            for (int i1 = 0; i1 < 4; i1++)
                                if (ai[i1] >= 0) {
                                    mat1.setLayer(i1);
                                    mat1.set('\0', as[ai[i1]]);
                                }
                        }
                        if (amat != null) for (int j1 = 0; j1 < 4; j1++)
                            if (ai[j1] >= 0) amat[ai[j1]] = mat1;
                        hiermesh.materialReplace(_skinMat[i], mat1);
                    }
                }
            }
        }
    }

    public static String getSkinPrefix(String s, Regiment regiment)
    {
        return "";
    }

    public static void prepareMeshSkin(String s, HierMesh hiermesh, String s1, String s2) {
        prepareMeshSkin(s, hiermesh, s1, s2, null);
    }

    public static boolean prepareMeshSkin(String s, HierMesh hiermesh, String s1, String s2, Regiment regiment) {
        String s3 = s;
        int i = s3.lastIndexOf('/');
        if (i >= 0) s3 = s3.substring(0, i + 1) + "summer";
        else s3 += "summer";
        try {
            File file = new File(HomePath.toFileSystemName(s2, 0));
            if (!file.isDirectory()) file.mkdir();
        } catch (Exception exception) {
            return false;
        }
//        if (BmpUtils.bmp8PalTo4TGA4(s1, s3, s2) && s2 != null) prepareMeshCamouflage(s, hiermesh, s2, null);
        if (!BmpUtils.bmp8PalTo4TGA4(s1, s3, s2)) return false;
        if (s2 == null) return false;
        prepareMeshCamouflage(s, hiermesh, s2, null, regiment);
        return true;
    }

    public static void prepareMeshPilot(HierMesh hiermesh, int i, String string, String string_128_) {
        prepareMeshPilot(hiermesh, i, string, string_128_, null);
    }

    public static void prepareMeshPilot(HierMesh hiermesh, int i, String string, String string_129_, Mat[] mats) {
        if (Config.isUSE_RENDER()) {
            String string_130_ = "Pilot" + (1 + i);
            int i_131_ = hiermesh.materialFind(string_130_);
            if (i_131_ >= 0) {
                Mat mat;
                if (FObj.Exist(string)) mat = (Mat) FObj.Get(string);
                else {
                    Mat mat_132_ = hiermesh.material(i_131_);
                    mat = (Mat) mat_132_.Clone();
                    mat.Rename(string);
                    mat.setLayer(0);
                    mat.set('\0', string_129_);
                }
                if (mats != null) mats[0] = mat;
                hiermesh.materialReplace(string_130_, mat);
            }
        }
    }

    public static void prepareMeshNoseart(HierMesh hiermesh, String string, String string_133_, String string_134_, String string_135_) {
        prepareMeshNoseart(hiermesh, string, string_133_, string_134_, string_135_, null);
    }

    public static void prepareMeshNoseart(HierMesh hiermesh, String string, String string_136_, String string_137_, String string_138_, Mat[] mats) {
        if (Config.isUSE_RENDER()) {
            String string_139_ = "Overlay9";
            int i = hiermesh.materialFind(string_139_);
            if (i >= 0) {
                Mat mat;
                if (FObj.Exist(string)) mat = (Mat) FObj.Get(string);
                else {
                    Mat mat_140_ = hiermesh.material(i);
                    mat = (Mat) mat_140_.Clone();
                    mat.Rename(string);
                    mat.setLayer(0);
                    mat.set('\0', string_137_);
                }
                if (mats != null) mats[0] = mat;
                hiermesh.materialReplace(string_139_, mat);
                string_139_ = "OverlayA";
                i = hiermesh.materialFind(string_139_);
                if (i >= 0) {
                    if (FObj.Exist(string_136_)) mat = (Mat) FObj.Get(string_136_);
                    else {
                        Mat mat_141_ = hiermesh.material(i);
                        mat = (Mat) mat_141_.Clone();
                        mat.Rename(string_136_);
                        mat.setLayer(0);
                        mat.set('\0', string_138_);
                    }
                    if (mats != null) mats[1] = mat;
                    hiermesh.materialReplace(string_139_, mat);
                }
            }
        }
    }

    private static boolean existSFSFile(String string) {
        boolean bool;
        try {
            SFSInputStream sfsinputstream = new SFSInputStream(string);
            sfsinputstream.close();
            bool = true;
        } catch (Exception exception) {
            return false;
        }
        return bool;
    }

    public double getSpeed(Vector3d vector3d) {
        if (this.FM == null) {
            if (vector3d != null) vector3d.set(0.0, 0.0, 0.0);
            return 0.0;
        }
        if (vector3d != null) vector3d.set(this.FM.Vwld);
        return this.FM.Vwld.length();
    }

    public void setSpeed(Vector3d vector3d) {
        super.setSpeed(vector3d);
        this.FM.Vwld.set(vector3d);
    }

    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        this.FM.CT.setLanded();
        forceGear(this.getClass(), this.hierMesh(), this.FM.CT.getGear());
        if (point3d != null && orient != null) {
            this.pos.setAbs(point3d, orient);
            this.pos.reset();
        }
        if (vector3d != null) this.setSpeed(vector3d);
        // TODO: Added by SAS~Storebror: Open canopy on ground start for player aircraft!
        if (this.FM.isPlayers() && this.FM.CT.bHasCockpitDoorControl) {
            this.FM.CT.cockpitDoorControl = 1F;
            this.FM.CT.forceCockpitDoor(1F);
        }
        // Flight Model Debugging
        if (Config.logTakeoffDistanceOver50ftObstacle) {
            spawnPoint = new Point3d();
            spawnPoint.set(point3d);
            airborne = false;
        }
    }
    
    public void load(SectFile sectfile, String string, int i, NetChannel netchannel, int i_142_) throws Exception {
        if (this == World.getPlayerAircraft()) {
            this.setFM(1, true);
            World.setPlayerFM();
        } else if (netchannel != null) this.setFM(2, false);
        else this.setFM(0, true);
        if (sectfile.exist(string, "Skill" + i)) this.FM.setSkill(sectfile.get(string, "Skill" + i, 1));
        else this.FM.setSkill(sectfile.get(string, "Skill", 1));
        this.FM.M.fuel = sectfile.get(string, "Fuel", 100.0F, 0.0F, 100.0F) * 0.01F * this.FM.M.maxFuel;
        if (sectfile.exist(string, "numberOn" + i)) this.bPaintShemeNumberOn = sectfile.get(string, "numberOn" + i, 1, 0, 1) == 1;
        this.FM.AS.bIsEnableToBailout = sectfile.get(string, "Parachute", 1, 0, 1) == 1;
        if (Mission.isServer()) this.createNetObject(null, 0);
        else if (netchannel != null) this.createNetObject(netchannel, i_142_);
        if (this.net != null) {
            ((NetAircraft.AircraftNet) this.net).netName = this.name();
            ((NetAircraft.AircraftNet) this.net).netUser = null;
        }
        String string_143_ = string + "_weapons";
        int i_144_ = sectfile.sectionIndex(string_143_);
        if (i_144_ >= 0) {
            int i_145_ = sectfile.vars(i_144_);
            for (int i_146_ = 0; i_146_ < i_145_; i_146_++) {
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(i_144_, i_146_));
                int i_147_ = numbertokenizer.next(9, 0, 19);
                String string_148_ = numbertokenizer.next();
                String string_149_ = numbertokenizer.next();
                Class var_class = ObjIO.classForName("weapons." + string_149_);
                Object object = var_class.newInstance();
                if (object instanceof BulletEmitter) {
                    BulletEmitter bulletemitter = (BulletEmitter) object;
                    bulletemitter.set(this, string_148_, dumpName(string_148_));
                    int i_150_ = numbertokenizer.next(-12345);
                    if (i_150_ == -12345) bulletemitter.loadBullets();
                    else bulletemitter._loadBullets(i_150_);
                    this.addGun(bulletemitter, i_147_);
                }
            }
        } else {
            this.thisWeaponsName = sectfile.get(string, "weapons", (String) null);
            if (this.thisWeaponsName != null) weaponsLoad(this, this.thisWeaponsName);
        }
        if (this == World.getPlayerAircraft()) this.createCockpits();
        this.onAircraftLoaded();
    }

    private static String dumpName(String string) {
        int i;
        for (i = string.length() - 1; i >= 0 && Character.isDigit(string.charAt(i)); i--) {
            /* empty */
        }
        i++;
        return string.substring(0, i) + "Dump" + string.substring(i);
    }

    public boolean turretAngles(int i, float[] fs) {
        for (int i_151_ = 0; i_151_ < 2; i_151_++) {
            fs[i_151_] = (fs[i_151_] + 3600.0F) % 360.0F;
            if (fs[i_151_] > 180.0F) fs[i_151_] -= 360.0F;
        }
        fs[2] = 0.0F;
        return true;
    }

    public int WeaponsMask() {
        return -1;
    }

    public int HitbyMask() {
        // TODO: Add null checks
        if (this.FM == null) return -25;
        if (this.FM.Vwld == null) return -25;
        return this.FM.Vwld.length() < 2.0 ? -1 : -25;
    }

    public int chooseBulletType(BulletProperties[] bulletpropertieses) {
        if (this.FM.isTakenMortalDamage()) return -1;
        if (bulletpropertieses.length == 1) return 0;
        if (bulletpropertieses.length <= 0) return -1;
        if (bulletpropertieses[0].power <= 0.0F) return 1;
        if (bulletpropertieses[1].power <= 0.0F) return 0;
        if (bulletpropertieses[0].powerType == 1) return 0;
        if (bulletpropertieses[1].powerType == 1) return 1;
        if (bulletpropertieses[0].powerType == 0) return 0;
        if (bulletpropertieses[1].powerType == 0) return 1;
        if (bulletpropertieses[0].powerType == 2) return 1;
        return 0;
    }

    public int chooseShotpoint(BulletProperties bulletproperties) {
        if (this.FM.isTakenMortalDamage()) return -1;
        return 0;
    }

    public boolean getShotpointOffset(int i, Point3d point3d) {
        if (this.FM.isTakenMortalDamage()) return false;
        if (i != 0) return false;
        if (point3d != null) point3d.set(0.0, 0.0, 0.0);
        return true;
    }

    public float AttackMaxDistance() {
        return 1500.0F;
    }

    private static int[] getSwTbl(int i) {
        if (i < 0) i = -i;
        int i_152_ = i % 16 + 11;
        int i_153_ = i % Finger.kTable.length;
        if (i_152_ < 0) i_152_ = -i_152_ % 16;
        if (i_152_ < 10) i_152_ = 10;
        if (i_153_ < 0) i_153_ = -i_153_ % Finger.kTable.length;
        int[] is = new int[i_152_];
        for (int i_154_ = 0; i_154_ < i_152_; i_154_++)
            is[i_154_] = Finger.kTable[(i_153_ + i_154_) % Finger.kTable.length];
        return is;
    }

//	public static void weapons(Class var_class) {
//		try {
//			int i = Finger.Int("ce" + var_class.getName() + "vd");
//			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new KryptoInputFilter(new SFSInputStream(Finger.LongFN(0L, "cod/" + Finger.incInt(i, "adt"))), getSwTbl(i))));
//			ArrayList arraylist = weaponsListProperty(var_class);
//			HashMapInt hashmapint = weaponsMapProperty(var_class);
//			for (;;) {
//				String string = bufferedreader.readLine();
//				if (string == null) { break; }
//				StringTokenizer stringtokenizer = new StringTokenizer(string, ",");
//				int i_155_ = stringtokenizer.countTokens() - 1;
//				String string_156_ = stringtokenizer.nextToken();
//				_WeaponSlot[] var__WeaponSlots = new _WeaponSlot[i_155_];
//				for (int i_157_ = 0; i_157_ < i_155_; i_157_++) {
//					String string_158_ = stringtokenizer.nextToken();
//					if (string_158_ != null && string_158_.length() > 3) {
//						NumberTokenizer numbertokenizer = new NumberTokenizer(string_158_);
//						var__WeaponSlots[i_157_] = new _WeaponSlot(numbertokenizer.next(0), numbertokenizer.next((String) null), numbertokenizer.next(-12345));
//					}
//				}
//				arraylist.add(string_156_);
//				hashmapint.put(Finger.Int(string_156_), var__WeaponSlots);
//			}
//			bufferedreader.close();
//		} catch (Exception exception) {
//			System.out.println("Error ");
//			exception.printStackTrace();
//		}
//	}

    public static void weapons(Class class1) {
        String codLine = "unknown";
        int codLineNumber = 0;
        try {
//	          ArrayList arraylist = weaponsListProperty(class1);
//	          HashMapInt hashmapint = weaponsMapProperty(class1);

            // By western: ignoring cod/ weapon loadout flag, added in Engine mod 2.8.12w
            boolean bIgnoreCodWeapon = Property.intValue(class1, "IgnoreCodWeapon", 0) == 1;
            if (bIgnoreCodWeapon) return;
            int i = Finger.Int("ce" + class1.getName() + "vd");

            // FIXME: By SAS~Storebror: Avoid duplicate Loadout Lists
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new KryptoInputFilter(new SFSInputStream(Finger.LongFN(0L, "cod/" + Finger.incInt(i, "adt"))), getSwTbl(i))));
            ArrayList arraylist = new ArrayList();
            Property.set(class1, "weaponsList", arraylist);
            HashMapInt hashmapint = new HashMapInt();
            Property.set(class1, "weaponsMap", hashmapint);

//	          BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new KryptoInputFilter(new SFSInputStream(Finger.LongFN(0L, "cod/" + Finger.incInt(i, "adt"))), getSwTbl(i))));

            do {
                codLineNumber++;
                codLine = bufferedreader.readLine();
                if (codLine == null) break;
                if (codLine.length() < 1) continue;
                StringTokenizer stringtokenizer = new StringTokenizer(codLine, ",");
                int j = stringtokenizer.countTokens() - 1;
                String s1 = stringtokenizer.nextToken();
                _WeaponSlot a_lweaponslot[] = new _WeaponSlot[j];
                int slotNr = -1;
                String weaponName = "unknown";
                int numBullets = -1;
                for (int k = 0; k < j; k++) {
                    String s2 = stringtokenizer.nextToken();
                    if (s2 != null && s2.length() > 3) {
                        NumberTokenizer numbertokenizer = new NumberTokenizer(s2);
                        try {
                            slotNr = numbertokenizer.next(0);
                            weaponName = numbertokenizer.next(null);
                            numBullets = numbertokenizer.next(-12345);
                            a_lweaponslot[k] = new _WeaponSlot(slotNr, weaponName, numBullets);
                            //a_lweaponslot[k] = new _WeaponSlot(numbertokenizer.next(0), numbertokenizer.next(null), numbertokenizer.next(-12345));
                        } catch (Exception e) {
                            System.out.println("Error while creating WeaponSlot Nr. " + slotNr + ": " + weaponName + " (" + numBullets + ") for Aircraft " + class1.getName());
                            //e.printStackTrace();
                        }
                    }
                }

                arraylist.add(s1);
                hashmapint.put(Finger.Int(s1), a_lweaponslot);
            } while (true);
            bufferedreader.close();

            // FIXME: By SAS~Storebror: Don't hide potential errors inside cod files
        } catch (FileNotFoundException fnfe) {
            System.out.println("No cod'ded weapons list found for " + class1.getName());
        } catch (Exception exception) {
            int codNumber = Finger.incInt(Finger.Int("ce" + class1.getName() + "vd"), "adt");
            System.out.println("Weapon Declaration Error for class " + class1.getName());
            System.out.println("Weapon Declaration Line: " + codLine + "(Line Numer: " + codLineNumber + ", Length: " + codLine.length() + ", cod Number: " + codNumber + ")");
            exception.printStackTrace();
        }
    }

    public long finger(long l) {
        Class var_class = this.getClass();
        l = FlightModelMain.finger(l, Property.stringValue(var_class, "FlightModel", null));
        l = Finger.incLong(l, Property.stringValue(var_class, "meshName", null));
        Object object = Property.value(var_class, "cockpitClass", null);
        if (object != null) if (object instanceof Class) l = Finger.incLong(l, ((Class) object).getName());
        else {
            Class[] var_classes = (Class[]) object;
            for (int i = 0; i < var_classes.length; i++)
                l = Finger.incLong(l, var_classes[i].getName());
        }
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i];
            if (bulletemitters != null) for (int i_159_ = 0; i_159_ < bulletemitters.length; i_159_++) {
                BulletEmitter bulletemitter = bulletemitters[i_159_];
                l = Finger.incLong(l, Property.intValue(bulletemitter, "_count", 0));
                if (bulletemitter instanceof Gun) {
                    GunProperties gunproperties = ((Gun) bulletemitter).prop;
                    l = Finger.incLong(l, gunproperties.shotFreq);
                    l = Finger.incLong(l, gunproperties.shotFreqDeviation);
                    l = Finger.incLong(l, gunproperties.maxDeltaAngle);
                    l = Finger.incLong(l, gunproperties.bullets);
                    BulletProperties[] bulletpropertieses = gunproperties.bullet;
                    if (bulletpropertieses != null) for (int i_160_ = 0; i_160_ < bulletpropertieses.length; i_160_++) {
                        l = Finger.incLong(l, bulletpropertieses[i_160_].massa);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].kalibr);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].speed);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].cumulativePower);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].power);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].powerType);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].powerRadius);
                        l = Finger.incLong(l, bulletpropertieses[i_160_].timeLife);
                    }
                } else if (bulletemitter instanceof RocketGun) {
                    RocketGun rocketgun = (RocketGun) bulletemitter;
                    Class var_class_161_ = (Class) Property.value(rocketgun.getClass(), "bulletClass", null);
                    l = Finger.incLong(l, Property.intValue(rocketgun.getClass(), "bullets", 1));
                    l = Finger.incLong(l, Property.floatValue(rocketgun.getClass(), "shotFreq", 0.5F));
                    if (var_class_161_ != null) {
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "radius", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "timeLife", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "timeFire", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "force", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "power", 1.0F));
                        l = Finger.incLong(l, Property.intValue(var_class_161_, "powerType", 1));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "kalibr", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "massa", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_161_, "massaEnd", 1.0F));
                    }
                } else if (bulletemitter instanceof BombGun) {
                    BombGun bombgun = (BombGun) bulletemitter;
                    Class var_class_162_ = (Class) Property.value(bombgun.getClass(), "bulletClass", null);
                    l = Finger.incLong(l, Property.intValue(bombgun.getClass(), "bullets", 1));
                    l = Finger.incLong(l, Property.floatValue(bombgun.getClass(), "shotFreq", 0.5F));
                    if (var_class_162_ != null) {
                        l = Finger.incLong(l, Property.floatValue(var_class_162_, "radius", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_162_, "power", 1.0F));
                        l = Finger.incLong(l, Property.intValue(var_class_162_, "powerType", 1));
                        l = Finger.incLong(l, Property.floatValue(var_class_162_, "kalibr", 1.0F));
                        l = Finger.incLong(l, Property.floatValue(var_class_162_, "massa", 1.0F));
                    }
                }
            }
        }
        return l;
    }

    protected static void weaponTriggersRegister(Class var_class, int[] is) {
        Property.set(var_class, "weaponTriggers", is);
    }

    public static int[] getWeaponTriggersRegistered(Class var_class) {
        return (int[]) Property.value(var_class, "weaponTriggers", null);
    }

    protected static void weaponHooksRegister(Class var_class, String[] strings) {
        if (strings.length != getWeaponTriggersRegistered(var_class).length) throw new RuntimeException("Sizeof 'weaponHooks' != sizeof 'weaponTriggers'");
        Property.set(var_class, "weaponHooks", strings);
    }

    public static String[] getWeaponHooksRegistered(Class var_class) {
        return (String[]) Property.value(var_class, "weaponHooks", null);
    }

    protected static void weaponsRegister(Class var_class, String string, String[] strings) {
        /* empty */
    }

    protected static void weaponsUnRegister(Class var_class, String string) {
        ArrayList arraylist = weaponsListProperty(var_class);
        HashMapInt hashmapint = weaponsMapProperty(var_class);
        int i = arraylist.indexOf(string);
        if (i >= 0) {
            arraylist.remove(i);
            hashmapint.remove(Finger.Int(string));
        }
    }

    public static String[] getWeaponsRegistered(Class var_class) {
        ArrayList arraylist = weaponsListProperty(var_class);
        String[] strings = new String[arraylist.size()];
        for (int i = 0; i < strings.length; i++)
            strings[i] = (String) arraylist.get(i);
        return strings;
    }

    public static _WeaponSlot[] getWeaponSlotsRegistered(Class var_class, String string) {
        HashMapInt hashmapint = weaponsMapProperty(var_class);
        return (_WeaponSlot[]) hashmapint.get(Finger.Int(string));
    }

    public static boolean weaponsExist(Class class1, String s) {
        Object obj = Property.value(class1, "weaponsMap", null);
        if (obj == null) return false;
        else {
            HashMapInt hashmapint = (HashMapInt) obj;
            int i = com.maddox.rts.Finger.Int(s);
            boolean flag = Aircraft.isWeaponDateOk(class1, s);
            return hashmapint.containsKey(i) && flag;
        }
    }

    public static boolean isWeaponDateOk(Class class1, String s) {
        com.maddox.util.HashMapInt hashmapint = Aircraft.weaponsMapProperty(class1);
        int i = Finger.Int(s);
        if (!hashmapint.containsKey(i)) return true;
        int j = Mission.getMissionDate(false);
        if (j == 0) return true;
        java.lang.String s1 = "";
        try {
            s1 = class1.toString().substring(class1.toString().lastIndexOf(".") + 1, class1.toString().length());
        } catch (java.lang.Exception exception) {
            return true;
        }
        java.lang.String as[] = Aircraft.getWeaponHooksRegistered(class1);
        _WeaponSlot a_lweaponslot[] = (_WeaponSlot[]) hashmapint.get(i);
        for (int k = 0; k < as.length; k++) {
            if (a_lweaponslot[k] == null) continue;
            int l = com.maddox.rts.Property.intValue(a_lweaponslot[k].clazz, "dateOfUse_" + s1, 0);
            if (l == 0) l = com.maddox.rts.Property.intValue(a_lweaponslot[k].clazz, "dateOfUse", 0);
            if (l != 0 && j < l) return false;
        }

        return true;
    }

    // TODO: Added by SAS~Storebror: If an old mission attempts to load a non-existing loadout for a
    // certain aircraft, try to load the most similar one (according to the loadout name)
    // instead.

    protected void weaponsLoad(String string) throws Exception {
        weaponsLoad(this, string);
    }

    protected static void weaponsLoad(Aircraft aircraft, String string) throws Exception {
        Class var_class = aircraft.getClass();
        HashMapInt hashmapint = weaponsMapProperty(var_class);
        int i = Finger.Int(string);

        // TODO: Added by SAS~Storebror: If an old mission attempts to load a non-existing loadout for a
        // certain aircraft, try to load the most similar one (according to the loadout name)
        // instead.
        // if (!hashmapint.containsKey(i)) throw new RuntimeException("Weapon set '" + string + "' not registered in " + ObjIO.classGetName(var_class));
        if (!hashmapint.containsKey(i)) {
            System.out.println("Weapon set '" + string + "' not registered in " + ObjIO.classGetName(var_class) + ", trying to find the best matching set.");
            int bestDistance = Integer.MAX_VALUE;
            String bestMatch = "";
            ArrayList loadouts = weaponsListProperty(var_class);
            for (int loadoutsIndex = 0; loadoutsIndex < loadouts.size(); loadoutsIndex++) {
                String loadout = (String) loadouts.get(loadoutsIndex);
                int distance = CommonTools.calculateDistance(loadout.toLowerCase(), string.toLowerCase());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestMatch = loadout;
                }
            }
            if (bestMatch.length() == 0) throw new RuntimeException("Weapon set '" + string + "' not registered in " + ObjIO.classGetName(var_class) + ", no similar matching weapon set found either!");
            i = Finger.Int(bestMatch);
            if (!hashmapint.containsKey(i)) throw new RuntimeException("Weapon set of best match '" + string + "' not registered in " + ObjIO.classGetName(var_class));
            System.out.println("Best matching weapon set '" + bestMatch + "' will be loaded instead now.");
        }
        // ---

        weaponsLoad(aircraft, i, hashmapint);
    }

    protected static void weaponsLoad(Aircraft aircraft, int i) throws Exception {
        HashMapInt hashmapint = weaponsMapProperty(aircraft.getClass());
        if (!hashmapint.containsKey(i)) throw new RuntimeException("Weapon set '" + i + "' not registered in " + ObjIO.classGetName(aircraft.getClass()));
        weaponsLoad(aircraft, i, hashmapint);
    }

    protected static void weaponsLoad(Aircraft aircraft, int i, HashMapInt hashmapint) throws Exception {
        String[] strings = getWeaponHooksRegistered(aircraft.getClass());
        _WeaponSlot[] var__WeaponSlots = (_WeaponSlot[]) hashmapint.get(i);
        for (int hookIndex = 0; hookIndex < strings.length; hookIndex++)
            if (var__WeaponSlots[hookIndex] != null) if (aircraft.mesh().hookFind(strings[hookIndex]) != -1) {
                BulletEmitter bulletemitter = (BulletEmitter) var__WeaponSlots[hookIndex].clazz.newInstance();
                bulletemitter.set(aircraft, strings[hookIndex], dumpName(strings[hookIndex]));
                if (aircraft.isNet() && aircraft.isNetMirror()) {
                    if (!World.cur().diffCur.Limited_Ammo) bulletemitter.loadBullets(-1);
                    else if (var__WeaponSlots[hookIndex].trigger == 2 || var__WeaponSlots[hookIndex].trigger == 3 || var__WeaponSlots[hookIndex].trigger >= 10) {
                        if (var__WeaponSlots[hookIndex].bullets == -12345) bulletemitter.loadBullets();
                        else bulletemitter._loadBullets(var__WeaponSlots[hookIndex].bullets);
                    } else bulletemitter.loadBullets(-1);
                } else if (var__WeaponSlots[hookIndex].bullets == -12345) bulletemitter.loadBullets();
                else bulletemitter.loadBullets(var__WeaponSlots[hookIndex].bullets);
                aircraft.addGun(bulletemitter, var__WeaponSlots[hookIndex].trigger);
                Property.set(bulletemitter, "_count", var__WeaponSlots[hookIndex].bullets);
                switch (var__WeaponSlots[hookIndex].trigger) {
                    case 0:
                        if (bulletemitter instanceof MGunAircraftGeneric)
                            if (World.getPlayerAircraft() == aircraft) ((MGunAircraftGeneric) bulletemitter).setConvDistance(World.cur().userCoverMashineGun, Property.floatValue(aircraft.getClass(), "LOSElevation", 0.75F));
                        else if (aircraft.isNet() && aircraft.isNetPlayer()) ((MGunAircraftGeneric) bulletemitter).setConvDistance(400.0F, Property.floatValue(aircraft.getClass(), "LOSElevation", 0.75F));
                        else((MGunAircraftGeneric) bulletemitter).setConvDistance(400.0F, 0.0F);
                        break;
                    case 1:
                        if (bulletemitter instanceof MGunAircraftGeneric)
                            if (World.getPlayerAircraft() == aircraft) ((MGunAircraftGeneric) bulletemitter).setConvDistance(World.cur().userCoverCannon, Property.floatValue(aircraft.getClass(), "LOSElevation", 0.75F));
                        else if (aircraft.isNet() && aircraft.isNetPlayer()) ((MGunAircraftGeneric) bulletemitter).setConvDistance(400.0F, Property.floatValue(aircraft.getClass(), "LOSElevation", 0.75F));
                        else((MGunAircraftGeneric) bulletemitter).setConvDistance(400.0F, 0.0F);
                        break;
                    case 2:
                        if (bulletemitter instanceof RocketGun) if (World.getPlayerAircraft() == aircraft) {
                            ((RocketGun) bulletemitter).setRocketTimeLife(World.cur().userRocketDelay);
                            ((RocketGun) bulletemitter).setConvDistance(World.cur().userCoverRocket, Property.floatValue(aircraft.getClass(), "LOSElevation", 0.75F) - 2.81F);
                        } else if (aircraft.isNet() && aircraft.isNetPlayer()) ((RocketGun) bulletemitter).setConvDistance(400.0F, Property.floatValue(aircraft.getClass(), "LOSElevation", 0.75F) - 2.81F);
                        else if (aircraft instanceof TypeFighter) ((RocketGun) bulletemitter).setConvDistance(400.0F, -1.8F);
                        else if (((RocketGun) bulletemitter).bulletMassa() > 10.0F) {
                            if (aircraft instanceof IL_2) ((RocketGun) bulletemitter).setConvDistance(400.0F, -2.0F);
                            else((RocketGun) bulletemitter).setConvDistance(400.0F, -1.65F);
                        } else if (aircraft instanceof IL_2) ((RocketGun) bulletemitter).setConvDistance(400.0F, -2.1F);
                        else((RocketGun) bulletemitter).setConvDistance(400.0F, -1.9F);
                        break;
                    case 3:
                        if (bulletemitter instanceof BombGun && World.getPlayerAircraft() == aircraft) // TODO: +++ Bomb Fuze Setting by SAS~Storebror +++
                            // ((BombGun) bulletemitter).setBombDelay(World.cur().userBombDelay);
                            ((BombGun) bulletemitter).setBombDelay(World.cur().userBombDelay, World.cur().userBombFuze);
                        // TODO: --- Bomb Fuze Setting by SAS~Storebror ---
                        break;
                }
            } else System.err.println("Hook '" + strings[hookIndex] + "' NOT found in mesh of " + aircraft.getClass());
    }

    private static ArrayList weaponsListProperty(Class var_class) {
        Object object = Property.value((Object) var_class, "weaponsList", null);
        if (object != null) return (ArrayList) object;
        ArrayList arraylist = new ArrayList();
        Property.set(var_class, "weaponsList", arraylist);
        return arraylist;
    }

    // TODO: Edited by |ZUTI|: changed from private to public - called from
    // ZutiTimer_ChangeLoadout
    public static HashMapInt weaponsMapProperty(Class var_class) {
        Object object = Property.value((Object) var_class, "weaponsMap", null);
        if (object != null) return (HashMapInt) object;
        HashMapInt hashmapint = new HashMapInt();
        Property.set(var_class, "weaponsMap", hashmapint);
        return hashmapint;
    }

    public void hideWingWeapons(boolean bool) {
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            BulletEmitter[] bulletemitters = this.FM.CT.Weapons[i];
            if (bulletemitters != null) for (int i_164_ = 0; i_164_ < bulletemitters.length; i_164_++)
                if (bulletemitters[i_164_] instanceof BombGun) ((BombGun) bulletemitters[i_164_]).hide(bool);
                else if (bulletemitters[i_164_] instanceof RocketGun) ((RocketGun) bulletemitters[i_164_]).hide(bool);
                else if (bulletemitters[i_164_] instanceof Pylon) ((Pylon) bulletemitters[i_164_]).drawing(!bool);
        }
    }

    public void createCockpits() {
        if (Config.isUSE_RENDER()) {
            this.deleteCockpits();
            Object object = Property.value(this.getClass(), "cockpitClass");
            if (object != null) {
                Cockpit._newAircraft = this;
                if (object instanceof Class) {
                    Class var_class = (Class) object;
                    try {
                        Main3D.cur3D().cockpits = new Cockpit[1];
                        Main3D.cur3D().cockpits[0] = (Cockpit) var_class.newInstance();
                        Main3D.cur3D().cockpitCur = Main3D.cur3D().cockpits[0];
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                        exception.printStackTrace();
                    }
                } else {
                    Class[] var_classes = (Class[]) object;
                    try {
                        Main3D.cur3D().cockpits = new Cockpit[var_classes.length];
                        for (int i = 0; i < var_classes.length; i++)
                            Main3D.cur3D().cockpits[i] = (Cockpit) var_classes[i].newInstance();
                        Main3D.cur3D().cockpitCur = Main3D.cur3D().cockpits[0];
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                        exception.printStackTrace();
                    }
                }
                Cockpit._newAircraft = null;
            }
        }
    }

    protected void deleteCockpits() {
        if (Config.isUSE_RENDER()) {
            Cockpit[] cockpits = Main3D.cur3D().cockpits;
            if (cockpits != null) {
                for (int i = 0; i < cockpits.length; i++) {
                    cockpits[i].destroy();
                    cockpits[i] = null;
                }
                Main3D.cur3D().cockpits = null;
                Main3D.cur3D().cockpitCur = null;
            }
        }
    }

    private void explode() {
        if (this.FM.Wingman != null) this.FM.Wingman.Leader = this.FM.Leader;
        if (this.FM.Leader != null) this.FM.Leader.Wingman = this.FM.Wingman;
        this.FM.Wingman = null;
        this.FM.Leader = null;
        HierMesh hiermesh = this.hierMesh();
        int i = -1;
        float f = 30.0F;
        for (int i_165_ = 9; i_165_ >= 0 && (i = hiermesh.chunkFindCheck("CF_D" + i_165_)) < 0; i_165_--) {
            /* empty */
        }
        int[] is = this.hideSubTrees("");
        if (is != null) {
            int[] is_166_ = is;
            is = new int[is_166_.length + 1];
            int i_167_;
            for (i_167_ = 0; i_167_ < is_166_.length; i_167_++)
                is[i_167_] = is_166_[i_167_];
            is[i_167_] = i;
            for (i_167_ = 0; i_167_ < is.length; i_167_++) {
                Wreckage wreckage = new Wreckage(this, is[i_167_]);
                if (World.Rnd().nextInt(0, 99) < 20) {
                    Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.FIRE, 2.5F);
                    if (World.Rnd().nextInt(0, 99) < 50) Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.SMOKE_EXPLODE, 3.0F);
                }
                this.getSpeed(Vd);
                Vd.x += f * (World.Rnd().nextDouble(0.0, 1.0) - 0.5);
                Vd.y += f * (World.Rnd().nextDouble(0.0, 1.0) - 0.5);
                Vd.z += f * (World.Rnd().nextDouble(0.0, 1.0) - 0.5);
                wreckage.setSpeed(Vd);
            }
        }
    }

    public int aircNumber() {
        Wing wing = (Wing) this.getOwner();
        if (wing == null) return -1;
        return wing.aircReady();
    }

    public int aircIndex() {
        Wing wing = (Wing) this.getOwner();
        if (wing == null) return -1;
        return wing.aircIndex(this);
    }

    public boolean isInPlayerWing() {
        if (!Actor.isValid(World.getPlayerAircraft())) return false;
        return this.getWing() == World.getPlayerAircraft().getWing();
    }

    public boolean isInPlayerSquadron() {
        if (!Actor.isValid(World.getPlayerAircraft())) return false;
        return this.getSquadron() == World.getPlayerAircraft().getSquadron();
    }

    public boolean isInPlayerRegiment() {
        return this.getRegiment() == World.getPlayerRegiment();
    }

    public boolean isChunkAnyDamageVisible(String string) {
        if (string.lastIndexOf("_") == -1) string += "_D";
        for (int i = 0; i < 4; i++)
            if (this.hierMesh().chunkFindCheck(string + i) != -1 && this.hierMesh().isChunkVisible(string + i)) return true;
        return false;
    }

    protected int chunkDamageVisible(String string) {
        if (string.lastIndexOf("_") == -1) string += "_D";
        for (int i = 0; i < 4; i++)
            if (this.hierMesh().chunkFindCheck(string + i) != -1 && this.hierMesh().isChunkVisible(string + i)) return i;
        return 0;
    }

    public Wing getWing() {
        return (Wing) this.getOwner();
    }

    public Squadron getSquadron() {
        Wing wing = this.getWing();
        if (wing == null) return null;
        return wing.squadron();
    }

    public Regiment getRegiment() {
        Wing wing = this.getWing();
        if (wing == null) return null;
        return wing.regiment();
    }

    public void hitDaSilk() {
        this.FM.AS.hitDaSilk();
        this.FM.setReadyToDie(true);
        if (this.FM.Loc.z - Engine.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y) > 20.0) Voice.speakBailOut(this);
    }

    protected void killPilot(Actor actor, int i) {
        this.FM.AS.hitPilot(actor, i, 100);
    }

    public void doWoundPilot(int i, float f) {
    }

    public void doMurderPilot(int i) {
        /* empty */
    }

    public void doRemoveBodyFromPlane(int i) {
        this.doRemoveBodyChunkFromPlane("Pilot" + i);
        this.doRemoveBodyChunkFromPlane("Head" + i);
        this.doRemoveBodyChunkFromPlane("HMask" + i);
        this.doRemoveBodyChunkFromPlane("Pilot" + i + "a");
        this.doRemoveBodyChunkFromPlane("Head" + i + "a");
        this.doRemoveBodyChunkFromPlane("Pilot" + i + "FAK");
        this.doRemoveBodyChunkFromPlane("Head" + i + "FAK");
        this.doRemoveBodyChunkFromPlane("Pilot" + i + "FAL");
        this.doRemoveBodyChunkFromPlane("Head" + i + "FAL");
    }

    protected void doRemoveBodyChunkFromPlane(String string) {
        if (this.hierMesh().chunkFindCheck(string + "_D0") != -1) this.hierMesh().chunkVisible(string + "_D0", false);
        if (this.hierMesh().chunkFindCheck(string + "_D1") != -1) this.hierMesh().chunkVisible(string + "_D1", false);
    }

    public void doSetSootState(int i, int j) {
        for (int k = 0; k < 2; k++) {
            if (this.FM.AS.astateSootEffects[i][k] != null) Eff3DActor.finish(this.FM.AS.astateSootEffects[i][k]);
            this.FM.AS.astateSootEffects[i][k] = null;
        }
        switch (j) {
            case 0:
                break;
            case 1:
                this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "ES_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1.0F);
                if (this.mesh().hookFind("_Engine" + (i + 1) + "ES_02") != -1) this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "ES_02"), null, 1.0F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1.0F);
                break;
            case 3:
                this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1.0F);
                /* fall through */
            case 2:
                this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboZippo.eff", -1.0F);
                break;
            case 5:
                this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 3.0F, "3DO/Effects/Aircraft/TurboJRD1100F.eff", -1.0F);
                /* fall through */
            case 4:
                this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1.0F);
                break;
        }
    }

    public void onAircraftLoaded() {
        // TODO: Added by |ZUTI|: execute only for LIVE player AC
        // -------------------------
        ZutiSupportMethods_Air.executeWhenAircraftIsCreated(this);
        // -------------------------

        if (this.FM instanceof Maneuver) {
            Maneuver maneuver = (Maneuver) this.FM;
            float[] fs = maneuver.takeIntoAccount;

            fs[0] = 1.0F;
            float[] fs_170_ = maneuver.takeIntoAccount;

            fs_170_[1] = 1.0F;
            float[] fs_171_ = maneuver.takeIntoAccount;

            fs_171_[2] = 0.7F;
            if (this instanceof TypeFighter) {
                if (this.aircIndex() % 2 == 0) {
                    float[] fs_172_ = maneuver.takeIntoAccount;
                    fs_172_[3] = 0.0F;
                    float[] fs_173_ = maneuver.takeIntoAccount;
                    fs_173_[4] = 1.0F;
                } else {
                    float[] fs_174_ = maneuver.takeIntoAccount;
                    fs_174_[2] = 0.1F;
                    float[] fs_175_ = maneuver.takeIntoAccount;
                    fs_175_[3] = 1.0F;
                    float[] fs_176_ = maneuver.takeIntoAccount;
                    fs_176_[4] = 0.0F;
                }
                float[] fs_177_ = maneuver.takeIntoAccount;
                fs_177_[5] = 0.3F;
                float[] fs_178_ = maneuver.takeIntoAccount;
                fs_178_[6] = 0.3F;
                float[] fs_179_ = maneuver.takeIntoAccount;
                fs_179_[7] = 0.1F;
            } else if (this instanceof TypeStormovik) {
                if (this.aircIndex() != 0) {
                    float[] fs_180_ = maneuver.takeIntoAccount;
                    fs_180_[2] = 0.5F;
                }
                float[] fs_181_ = maneuver.takeIntoAccount;
                fs_181_[3] = 0.4F;
                float[] fs_182_ = maneuver.takeIntoAccount;
                fs_182_[4] = 0.2F;
                float[] fs_183_ = maneuver.takeIntoAccount;
                fs_183_[5] = 0.1F;
                float[] fs_184_ = maneuver.takeIntoAccount;
                fs_184_[6] = 0.1F;
                float[] fs_185_ = maneuver.takeIntoAccount;
                fs_185_[7] = 0.6F;
            } else {
                if (this.aircIndex() != 0) {
                    float[] fs_186_ = maneuver.takeIntoAccount;
                    fs_186_[2] = 0.4F;
                }
                float[] fs_187_ = maneuver.takeIntoAccount;
                fs_187_[3] = 0.2F;
                float[] fs_188_ = maneuver.takeIntoAccount;
                fs_188_[4] = 0.0F;
                float[] fs_189_ = maneuver.takeIntoAccount;
                fs_189_[5] = 0.0F;
                float[] fs_190_ = maneuver.takeIntoAccount;
                fs_190_[6] = 0.0F;
                float[] fs_191_ = maneuver.takeIntoAccount;
                fs_191_[7] = 1.0F;
            }
            int i = 0;
            for (;;) {
                int i_192_ = i;
                if (maneuver == null) {
                    /* empty */
                }
                if (i_192_ >= 8) break;
                maneuver.AccountCoeff[i] = 0.0F;
                i++;
            }
        }
    }

    public final static float cvt(float inputValue, float inMin, float inMax, float outMin, float outMax) {
        return CommonTools.cvt(inputValue, inMin, inMax, outMin, outMax);
//        inputValue = Math.min(Math.max(inputValue, inMin), inMax);
//        return outMin + (outMax - outMin) * (inputValue - inMin) / (inMax - inMin);
    }

    protected void debugprintln(String string) {
        if (World.cur().isDebugFM()) System.out.println("<" + this.name() + "> (" + this.typedName() + ") " + string);
    }

    public static void debugprintln(Actor actor, String string) {
        if (World.cur().isDebugFM()) {
            if (Actor.isValid(actor)) {
                System.out.print("<" + actor.name() + ">");
                if (actor instanceof Aircraft) System.out.print(" (" + ((Aircraft) actor).typedName() + ")");
            } else System.out.print("<INVALIDACTOR>");
            System.out.println(" " + string);
        }
    }

    public void debuggunnery(String string) {
        if (World.cur().isDebugFM()) System.out.println("<" + this.name() + "> (" + this.typedName() + ") *** BULLET *** : " + string);
    }

    protected float bailProbabilityOnCut(String string) {
        if (string.startsWith("Nose")) return 0.5F;
        if (string.startsWith("Wing")) return 0.99F;
        if (string.startsWith("Aroone")) return 0.05F;
        if (string.startsWith("Tail")) return 0.99F;
        if (string.startsWith("StabL") && !this.isChunkAnyDamageVisible("VatorR")) return 0.99F;
        if (string.startsWith("StabR") && !this.isChunkAnyDamageVisible("VatorL")) return 0.99F;
        if (string.startsWith("Stab")) return 0.33F;
        if (string.startsWith("VatorL") && !this.isChunkAnyDamageVisible("VatorR")) return 0.99F;
        if (string.startsWith("VatorR") && !this.isChunkAnyDamageVisible("VatorL")) return 0.99F;
        if (string.startsWith("Vator")) return 0.01F;
        if (string.startsWith("Keel")) return 0.5F;
        if (string.startsWith("Rudder")) return 0.05F;
        if (string.startsWith("Engine")) return 0.12F;
        return -0.0F;
    }

    private void _setMesh(String string) {
        this.setMesh(string);
        CacheItem cacheitem = (CacheItem) meshCache.get(string);
        if (cacheitem == null) {
            cacheitem = new CacheItem();
            cacheitem.mesh = new HierMesh(string);
            prepareMeshCamouflage(string, cacheitem.mesh);
            cacheitem.bExistTextures = true;
            cacheitem.loaded = 1;
            meshCache.put(string, cacheitem);
        } else {
            cacheitem.loaded++;
            if (!cacheitem.bExistTextures) {
                cacheitem.mesh.destroy();
                cacheitem.mesh = new HierMesh(string);
                prepareMeshCamouflage(string, cacheitem.mesh);
                cacheitem.bExistTextures = true;
            }
        }
        airCache.put(this, cacheitem);
        checkMeshCache();
    }

    private void _removeMesh() {
        CacheItem cacheitem = (CacheItem) airCache.get(this);
        if (cacheitem != null) {
            airCache.remove(this);
            cacheitem.loaded--;
            if (cacheitem.loaded == 0) cacheitem.time = Time.real();
            checkMeshCache();
        }
    }

    public static void checkMeshCache() {
        if (Config.isUSE_RENDER()) {
            long l = Time.real();
            for (Map.Entry entry = meshCache.nextEntry(null); entry != null; entry = meshCache.nextEntry(entry)) {
                CacheItem cacheitem = (CacheItem) entry.getValue();
                if (cacheitem.loaded == 0 && cacheitem.bExistTextures && l - cacheitem.time > 180000L) {
                    HierMesh hiermesh = cacheitem.mesh;
                    int i = hiermesh.materials();
                    Mat mat = Mat.New("3do/textures/clear.mat");
                    for (int i_197_ = 0; i_197_ < i; i_197_++)
                        hiermesh.materialReplace(i_197_, mat);
                    cacheitem.bExistTextures = false;
                }
            }
        }
    }

    public static void resetGameClear() {
        meshCache.clear();
        airCache.clear();
    }

    public void setCockpitState(int i) {
        if (this.FM.isPlayers() && World.cur().diffCur.Vulnerability && Actor.isValid(Main3D.cur3D().cockpitCur)) Main3D.cur3D().cockpitCur.doReflectCockitState();
    }

    protected void resetYPRmodifier() {
        ypr[0] = ypr[1] = ypr[2] = xyz[0] = xyz[1] = xyz[2] = 0.0F;
    }

    public CellAirPlane getCellAirPlane() {
        CellAirPlane cellairplane = (CellAirPlane) Property.value(this, "CellAirPlane", null);
        if (cellairplane != null) return cellairplane;
        cellairplane = (CellAirPlane) Property.value((Object) this.getClass(), "CellObject", null);
        if (cellairplane == null) {
            tmpLocCell.set(0.0, 0.0, this.FM.Gears.H, 0.0F, this.FM.Gears.Pitch, 0.0F);
            cellairplane = new CellAirPlane(new CellObject[1][1], this.hierMesh(), tmpLocCell, 1.0);
            cellairplane.blurSiluet8x();
            cellairplane.clampCells();
            Property.set(this.getClass(), "CellObject", cellairplane);
        }
        cellairplane = (CellAirPlane) cellairplane.getClone();
        Property.set(this, "CellObject", cellairplane);
        return cellairplane;
    }

    public static CellAirPlane getCellAirPlane(Class var_class) {
        tmpLocCell.set(0.0, 0.0, 0.0, 0.0F, 0.0F, 0.0F);
        HierMesh hiermesh = new HierMesh(getPropertyMesh(var_class, null));
        CellAirPlane cellairplane = new CellAirPlane(new CellObject[1][1], hiermesh, tmpLocCell, 1.0);
        cellairplane.blurSiluet8x();
        cellairplane.clampCells();
        return cellairplane;
    }

    public boolean dropExternalStores(boolean flag) {
        boolean flag1 = false;
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            com.maddox.il2.ai.BulletEmitter abulletemitter[] = this.FM.CT.Weapons[i];
            if (abulletemitter == null) continue;
            for (int j = 0; j < abulletemitter.length; j++) {
                com.maddox.il2.ai.BulletEmitter bulletemitter = abulletemitter[j];
                if (!(bulletemitter instanceof com.maddox.il2.objects.weapons.BombGun) || bulletemitter instanceof com.maddox.il2.objects.weapons.FuelTankGun) continue;
                ((com.maddox.il2.objects.weapons.BombGun) bulletemitter).setArmingTime(0xfffffffL);
                if (bulletemitter.countBullets() <= 0) continue;
                flag1 = true;
                bulletemitter.shots(99);
                if (bulletemitter.getHookName().startsWith("_BombSpawn")) this.FM.CT.BayDoorControl = 1.0F;
            }

        }

        if (!flag1) return this.dropWfrGr21();
        else return flag1;
    }

    private boolean dropWfrGr21() {
        if (!this.wfrGr21dropped) {
            java.lang.Object aobj[] = this.pos.getBaseAttached();
            if (aobj != null) for (int i = 0; i < aobj.length; i++) {
                if (aobj[i] instanceof com.maddox.il2.objects.weapons.PylonRO_WfrGr21) {
                    com.maddox.il2.objects.weapons.PylonRO_WfrGr21 pylonro_wfrgr21 = (com.maddox.il2.objects.weapons.PylonRO_WfrGr21) aobj[i];
                    pylonro_wfrgr21.drawing(false);
                    pylonro_wfrgr21.visibilityAsBase(false);
                    this.wfrGr21dropped = true;
                    if (com.maddox.il2.ai.World.getPlayerAircraft() == this) com.maddox.il2.ai.World.cur().scoreCounter.playerDroppedExternalStores(2);
                }
                if (!(aobj[i] instanceof com.maddox.il2.objects.weapons.PylonRO_WfrGr21Dual)) continue;
                com.maddox.il2.objects.weapons.PylonRO_WfrGr21Dual pylonro_wfrgr21dual = (com.maddox.il2.objects.weapons.PylonRO_WfrGr21Dual) aobj[i];
                pylonro_wfrgr21dual.drawing(false);
                pylonro_wfrgr21dual.visibilityAsBase(false);
                this.wfrGr21dropped = true;
                if (com.maddox.il2.ai.World.getPlayerAircraft() == this) com.maddox.il2.ai.World.cur().scoreCounter.playerDroppedExternalStores(4);
            }
            if (this.wfrGr21dropped) {
                for (int j = 0; j < this.FM.CT.Weapons.length; j++) {
                    BulletEmitter abulletemitter[] = this.FM.CT.Weapons[j];
                    if (abulletemitter == null) continue;
                    for (int k = 0; k < abulletemitter.length; k++) {
                        Object obj = abulletemitter[k];
                        if (obj instanceof RocketGunWfrGr21) {
                            RocketGunWfrGr21 rocketgunwfrgr21 = (RocketGunWfrGr21) obj;
                            this.FM.CT.Weapons[j][k] = GunEmpty.get();
                            rocketgunwfrgr21.setHookToRel(true);
                            rocketgunwfrgr21.shots(0);
                            rocketgunwfrgr21.hide(true);
                            obj = GunEmpty.get();
                            rocketgunwfrgr21.doDestroy();
                        }
                        if (obj instanceof PylonRO_WfrGr21 || obj instanceof PylonRO_WfrGr21Dual) {
                            ((Pylon) obj).destroy();
                            this.FM.CT.Weapons[j][k] = GunEmpty.get();
                            obj = GunEmpty.get();
                        }
                    }

                }

                this.sfxHit(1.0F, new Point3d(0.0D, 0.0D, -1D));
                com.maddox.JGP.Vector3d vector3d = new Vector3d();
                vector3d.set(this.FM.Vwld);
                vector3d.z = vector3d.z - 6D;
                com.maddox.il2.objects.Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind("WfrGr21L"));
                vector3d.x += java.lang.Math.random() + 0.5D;
                vector3d.y += java.lang.Math.random() + 0.5D;
                vector3d.z += java.lang.Math.random() + 0.5D;
                wreckage.setSpeed(vector3d);
                wreckage.collide(true);
                com.maddox.JGP.Vector3d vector3d1 = new Vector3d();
                vector3d1.set(this.FM.Vwld);
                vector3d1.z = vector3d1.z - 7D;
                com.maddox.il2.objects.Wreckage wreckage1 = new Wreckage(this, this.hierMesh().chunkFind("WfrGr21R"));
                vector3d1.x += java.lang.Math.random() + 0.5D;
                vector3d1.y += java.lang.Math.random() + 0.5D;
                vector3d1.z += java.lang.Math.random() + 0.5D;
                wreckage1.setSpeed(vector3d1);
                wreckage1.collide(true);
                return true;
            } else return false;
        } else return false;
    }

    public void blisterRemoved(int i) {
    }

    public static boolean hasPlaneZBReceiver(Aircraft aircraft) {
        for (int i = 0; i < planesWithZBReceiver.length; i++) {
            if (!planesWithZBReceiver[i].isInstance(aircraft)) continue;
            java.lang.String s = aircraft.getRegiment().country();
            if (s.equals(PaintScheme.countryBritain) || s.equals(PaintScheme.countryUSA) || s.equals(PaintScheme.countryNewZealand)) return true;
        }

        return false;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public boolean isAircraftTaxing() {
        return this.FM.Gears.isUnderDeck() || this.FM.Gears.getWheelsOnGround() || this.FM.Gears.onGround();
    }

    public static boolean isPlayerTaxing() {
        Aircraft aircraft = World.getPlayerAircraft();
        return aircraft.isDestroyed() || aircraft.FM.Gears.isUnderDeck() || aircraft.FM.Gears.getWheelsOnGround() || aircraft.FM.Gears.onGround();
    }

    public void setHumanControlledTurretAngels(Turret turret, float af[], HierMesh hiermesh, ActorHMesh actorhmesh) {
        af[0] = af[1] = af[2] = 0.0F;
        af[1] = turret.tu[0];
        hiermesh.setCurChunk(turret.indexA);
        actorhmesh.hierMesh().chunkSetAngles(af);
        af[1] = turret.tu[1];
        hiermesh.setCurChunk(turret.indexB);
        actorhmesh.hierMesh().chunkSetAngles(af);
    }

    public void initPlayerTaxingWay() {
        showTaxingWay = false;
        Aircraft aircraft = World.getPlayerAircraft();
        playerTaxingWay = new ArrayList();
        if (aircraft.FM == null || aircraft.FM.AP == null) return;
        int i = 0;
        do {
            if (i >= aircraft.FM.AP.way.size()) break;
            WayPoint waypoint = aircraft.FM.AP.way.look_at_point(i);
            // TODO: Fixed by SAS~Storebror to avoid null dereference
//            if ((waypoint == null || waypoint.waypointType != 4) && waypoint.waypointType != 5) break;
            if (waypoint == null || waypoint.waypointType != 4 && waypoint.waypointType != 5) break;
//            World.cur();
//            World.land();
            double d = Landscape.HQ_Air(waypoint.x(), waypoint.y()) + 0.1F;
            Point3d point3d = new Point3d(waypoint.x(), waypoint.y(), d);
            playerTaxingWay.add(point3d);
            i++;
        } while (true);
    }

    public float getEyeLevelCorrection() {
        return 0.0F;
    }

    public void setBombScoreOwner(Aircraft aircraft) {
        this.bombScoreOwner = aircraft;
    }

    public Aircraft getBombScoreOwner() {
        return this.bombScoreOwner;
    }

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn
    // Effects +++
    public Point3d getTankBurnLightPoint(int i, Hook hook) {
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        Loc loc1 = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        if (hook.chunkName().toLowerCase().startsWith("cf")) {
            hook.computePos(this, loc, loc1);
            Point3d point3d = loc1.getPoint();
            if (point3d.z > 0.0D) loc = new Loc(0.0D, 0.0D, 0.5D, 0.0F, 0.0F, 0.0F);
            else loc = new Loc(0.0D, 0.0D, -0.75D, 0.0F, 0.0F, 0.0F);
            loc1 = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        } else loc = new Loc(-1D, 0.0D, 0.5D, 0.0F, 0.0F, 0.0F);
        hook.computePos(this, loc, loc1);
        Point3d point3d1 = loc1.getPoint();
        return point3d1;
    }

    public Loc getEngineBurnLightLoc(int i) {
        return engineBurnLightLoc;
    }
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn
    // Effects ---

    // TODO: +++ Enhanced Ordnance View by SAS~Storebror +++
    public Actor getNextOrdnance() {
        if (this.activeOrdnance.isEmpty()) return null; // No Ordnance available
        if (this.lastSelectedOrdnance == null) this.lastSelectedOrdnance = (Actor) this.activeOrdnance.getFirst(); // Select first available Ordnance if
        // nothing was selected before
        else {
            int lastSelectedOrdnanceIndex = this.activeOrdnance.indexOf(this.lastSelectedOrdnance);
            if (lastSelectedOrdnanceIndex == -1) this.lastSelectedOrdnance = (Actor) this.activeOrdnance.getFirst(); // Select first available Ordnance
            // if last selected doesn't exist
            // anymore
            else {
                if (lastSelectedOrdnanceIndex < this.activeOrdnance.size() - 1) lastSelectedOrdnanceIndex++; // Select next Ordnance after last selected if available
                else lastSelectedOrdnanceIndex = 0; // Otherwise, switch to first one
                this.lastSelectedOrdnance = (Actor) this.activeOrdnance.get(lastSelectedOrdnanceIndex);
            }
        }
        return this.lastSelectedOrdnance;
    }

    public void setLastSelectedOrdnance(Actor newLastSelectedOrdnance) {
        this.lastSelectedOrdnance = newLastSelectedOrdnance;
    }

    public void addOrdnance(Actor newOrdnance) {
//        System.out.println("addOrdnance " + newOrdnance.getClass().getName());
        this.activeOrdnance.addLast(newOrdnance);
    }

    public void removeOrdnance(Actor ordnanceToRemove) {
        this.activeOrdnance.remove(ordnanceToRemove);
    }
    // TODO: --- Enhanced Ordnance View by SAS~Storebror ---

    public float[] getTurretRestOrient(int paramInt) {
        return defTurretRest;
    }

    public static float floatindex(float f, float[] fs) {
        int i = (int) f;
        if (i >= fs.length - 1) return fs[fs.length - 1];
        if (i < 0) return fs[0];
        if (i == 0) {
            if (f > 0.0F) return fs[0] + f * (fs[1] - fs[0]);
            return fs[0];
        }
        return fs[i] + f % i * (fs[i + 1] - fs[i]);
    }
    
    public float getBayDoor()
    {
      return this.BayDoor_;
    }

    private static final float[] defTurretRest   = { 0.0F, 0.0F };
    private Aircraft             bombScoreOwner;
    public static boolean        showTaxingWay   = false;
    public static List           playerTaxingWay = null;
    public boolean               bSpotter;
    // TODO: --- TD AI code backport from 4.13 ---

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn
    // Effects +++
    private static final Loc engineBurnLightLoc = new Loc(0.0D, 0.0D, 1.5D, 0.0F, 0.0F, 0.0F);
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn
    // Effects ---

    // TODO: |ZUTI| variables
    // -------------------------------------------------------------------------------------------
    public float        zutiAircraftFuel           = 0.0F;
    protected ArrayList zutiMotorsArray            = null;
    protected boolean   zutiWingsUnfolded          = false;
    protected long      zutiLastUnfoldCheck        = Time.current();
    // This repeat timer we need because we could join the game when AC is just
    // lining up the carrier deck
    // This case messes things up because AC got command to expand it's wings but
    // client does not now
    // that and speed difference is not there to trigger auto unfolding
    protected int       zutiUnfoldCheckRepeatCount = 10;
    // -------------------------------------------------------------------------------------------

    // TODO: +++ Enhanced Ordnance View by SAS~Storebror +++
    private LinkedList activeOrdnance;
    private Actor      lastSelectedOrdnance;
    // TODO: --- Enhanced Ordnance View by SAS~Storebror ---
    
    // Flight Model Debugging
    Point3d spawnPoint = null;
    boolean airborne = false;
}
