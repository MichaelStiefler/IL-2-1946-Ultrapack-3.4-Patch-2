/*4.10.1 class + CTO Mod*/

// Code Cleaned by SAS~Storebror
package com.maddox.il2.fm;

import java.util.AbstractCollection;
import java.util.List;

import com.maddox.JGP.Geom;
import com.maddox.JGP.Line2f;
import com.maddox.JGP.Matrix4d;
import com.maddox.JGP.Point2f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.CellAirField;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mesh;
import com.maddox.il2.engine.MsgCollision;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.ZutiSupportMethods_Engine;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiAirfieldPoint;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.Scheme1;
import com.maddox.il2.objects.buildings.Plate;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.Ship;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.RocketBombGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;

public class Gear {
    // TODO: CTO Mod
    // ----------------------------------------
    private double  dCatapultOffsetX;
    private double  dCatapultOffsetY;
    private double  dCatapultOffsetX2;
    private double  dCatapultOffsetY2;
    private boolean bCatapultAllow;
    private boolean bCatapultBoost;
    private boolean bCatapultAI;
    private boolean bCatapultAllowAI;
    private boolean bCatapultSet;
    private boolean bAlreadySetCatapult;
    private boolean bCatapultAI_CVE;
    private boolean bCatapultAI_EssexClass;
    private boolean bCatapultAI_Illustrious;
    private boolean bCatapultAI_GrafZep;
    private boolean bCatapultAI_CVL;
    private boolean bStandardDeckCVL;
    // ----------------------------------------

    public int[]               pnti;
    boolean                    onGround                     = false;
    boolean                    nearGround                   = false;
    public float               H;
    public float               Pitch;
    public float               sinkFactor;
    public float               springsStiffness;
    public float               tailStiffness;
    public boolean             bIsSail;
    public int                 nOfGearsOnGr                 = 0;
    public int                 nOfPoiOnGr                   = 0;
    private int                oldNOfGearsOnGr              = 0;
    private int                oldNOfPoiOnGr                = 0;
    private int                nP                           = 0;
    public boolean             gearsChanged                 = false;
    HierMesh                   HM;
    public boolean             bFlatTopGearCheck;
    public static final int    MP                           = 64;
    public static final double maxVf_gr                     = 65.0;
    public static final double maxVn_gr                     = 7.0;
    public static final double maxVf_wt                     = 50.0;
    public static final double maxVn_wt                     = 30.0;
    public static final double maxVf_wl                     = 110.0;
    public static final double maxVn_wl                     = 7.0;
    public static final double _1_maxVf_gr2                 = 2.3668639053254438E-4;
    public static final double _1_maxVn_gr2                 = 0.02040816326530612;
    public static final double _1_maxVf_wt2                 = 4.0E-4;
    public static final double _1_maxVn_wt2                 = 0.0011111111111111111;
    public static final double _1_maxVf_wl2                 = 8.264462809917356E-5;
    public static final double _1_maxVn_wl2                 = 0.02040816326530612;
    private static Point3f[]   Pnt                          = new Point3f[64];
    private static boolean[]   clp                          = new boolean[64];
    private Eff3DActor[]       clpEff                       = new Eff3DActor[64];
    public Eff3DActor[][]      clpGearEff                   = { { null, null }, { null, null }, { null, null } };
    public Eff3DActor[][]      clpEngineEff                 = new Eff3DActor[8][2];
    private String             effectName                   = new String();
    private boolean            bTheBabysGonnaBlow           = false;
    public boolean             lgear                        = true;
    public boolean             rgear                        = true;
    public boolean             cgear                        = true;
    public boolean             bIsHydroOperable             = true;
    private boolean            bIsOperable                  = true;
    public boolean             bTailwheelLocked             = false;
    public double[]            gVelocity                    = { 0.0, 0.0, 0.0 };
    public float[]             gWheelAngles                 = { 0.0F, 0.0F, 0.0F };
    public float[]             gWheelSinking                = { 0.0F, 0.0F, 0.0F };
    public float               steerAngle                   = 0.0F;
    public double              roughness                    = 0.5;
    public float               arrestorVAngle               = 0.0F;
    public float               arrestorVSink                = 0.0F;
    public HookNamed           arrestorHook                 = null;
    public int[]               waterList                    = null;
    private boolean            isGearColl                   = false;
    private double             MassCoeff                    = 1.0;
    public boolean             bFrontWheel                  = false;
    private static AnglesFork  steerAngleFork               = new AnglesFork();
    private double             d;
    private double             D;
    private double             Vnorm;
    private boolean            isWater;
    private boolean            bUnderDeck                   = false;
    private boolean            bIsGear;
    private FlightModel        FM;
    private boolean            bIsMaster                    = true;
    private int[]              fatigue                      = new int[2];
    private Point3d            p0                           = new Point3d();
    private Point3d            p1                           = new Point3d();
    private Loc                l0                           = new Loc();
    private Vector3d           v0                           = new Vector3d();
    private Vector3d           tmpV                         = new Vector3d();
    private double             fric                         = 0.0;
    private double             fricF                        = 0.0;
    private double             fricR                        = 0.0;
    private double             maxFric                      = 0.0;
    public double              screenHQ                     = 0.0;
    static ClipFilter          clipFilter                   = new ClipFilter();
    private boolean            canDoEffect                  = true;
    private static Vector3d    Normal;
    private static Vector3d    Forward;
    private static Vector3d    Right;
    private static Vector3d    nwForward;
    private static Vector3d    nwRight;
    private static double      NormalVPrj;
    private static double      ForwardVPrj;
    private static double      RightVPrj;
    private static Vector3d    Vnf;
    private static Vector3d    Fd;
    private static Vector3d    Fx;
    private static Vector3d    Vship;
    private static Vector3d    Fv;
    private static Vector3d    Tn;
    private static Point3d     Pn;
    private static Point3d     PnT;
    private static Point3d     Pship;
    private static Vector3d    Vs;
    private static Matrix4d    M4;
    private static PlateFilter plateFilter;
    private static Point3d     corn;
    private static Point3d     corn1;
    private static Loc         L;
    private static float[]     plateBox;
    private static boolean     bPlateExist;
    private static boolean     bPlateGround;
    private static String[]    ZUTI_SKIS_AC_CLASSES;
    private boolean            zutiHasPlaneSkisOnWinterCamo = false;

    // TODO: +++ TD AI code backport from 4.13 +++
    public float mgx;
    public float mgy;
    public float mgh;
    public float mgalpha;
    public float sgx;
    // TODO: --- TD AI code backport from 4.13 ---
    
    // TODO: +++ Shock Absorber backport from EngineMod 2.8 +++
    public float shockAbsorber;
    // TODO: --- Shock Absorber backport from EngineMod 2.8 ---

    // TODO: +++ By SAS~Storebror: Picked out of nowhere, undocumented changes found in BAT
    public boolean bHeavyAC;
    // ------------------------------------------------------------------------------------

    private static class PlateFilter implements ActorFilter {
        private PlateFilter() {
        }

        public boolean isUse(Actor actor, double d) {
            if (!(actor instanceof Plate)) return true;
            Mesh mesh = ((ActorMesh) actor).mesh();
            mesh.getBoundBox(Gear.plateBox);
            Gear.corn1.set(Gear.corn);
            Loc loc = actor.pos.getAbs();
            loc.transformInv(Gear.corn1);
            if (Gear.plateBox[0] - 2.5F < Gear.corn1.x && Gear.corn1.x < Gear.plateBox[3] + 2.5F && Gear.plateBox[1] - 2.5F < Gear.corn1.y && Gear.corn1.y < Gear.plateBox[4] + 2.5F) {
                Gear.bPlateExist = true;
                Gear.bPlateGround = ((Plate) actor).isGround();
            }
            return true;
        }
    }

    static class ClipFilter implements ActorFilter {
        public boolean isUse(Actor actor, double d) {
            return actor instanceof BigshipGeneric;
        }
    }

    public Gear() {
        // TODO: CTO Mod
        // ----------------------------------------
        this.dCatapultOffsetX = 0.0D;
        this.dCatapultOffsetY = 0.0D;
        this.dCatapultOffsetX2 = 0.0D;
        this.dCatapultOffsetY2 = 0.0D;
        this.bCatapultAllow = true;
        this.bCatapultBoost = false;
        this.bCatapultAI = true;
        this.bCatapultAllowAI = true;
        this.bCatapultSet = false;
        this.bAlreadySetCatapult = false;
        this.bCatapultAI_CVE = true;
        this.bCatapultAI_EssexClass = true;
        this.bCatapultAI_Illustrious = true;
        this.bCatapultAI_GrafZep = true;
        this.bCatapultAI_CVL = true;
        this.bStandardDeckCVL = false;
        // ----------------------------------------

        this.onGround = false;
        this.nearGround = false;
        this.nOfGearsOnGr = 0;
        this.nOfPoiOnGr = 0;
        this.oldNOfGearsOnGr = 0;
        this.oldNOfPoiOnGr = 0;
        this.nP = 0;
        this.gearsChanged = false;
        this.clpEff = new Eff3DActor[64];
        this.clpEngineEff = new Eff3DActor[8][2];
        this.effectName = new String();
        this.bTheBabysGonnaBlow = false;
        this.lgear = true;
        this.rgear = true;
        this.cgear = true;
        this.bIsHydroOperable = true;
        this.bIsOperable = true;
        this.bTailwheelLocked = false;
        this.steerAngle = 0.0F;
        this.roughness = 0.5D;
        this.arrestorVAngle = 0.0F;
        this.arrestorVSink = 0.0F;
        this.arrestorHook = null;
        this.waterList = null;
        this.isGearColl = false;
        this.MassCoeff = 1.0D;
        this.bFrontWheel = false;
        this.bUnderDeck = false;
        this.bIsMaster = true;
        this.fatigue = new int[2];
        this.p0 = new Point3d();
        this.p1 = new Point3d();
        this.l0 = new Loc();
        this.v0 = new Vector3d();
        this.tmpV = new Vector3d();
        this.fric = 0.0D;
        this.fricF = 0.0D;
        this.fricR = 0.0D;
        this.maxFric = 0.0D;
        this.screenHQ = 0.0D;
        this.canDoEffect = true;

        // TODO: CTO Mod
        // ----------------------------------------
        if (Mission.cur().sectFile().get("Mods", "CatapultAllow", 1) == 0) {
            this.bCatapultAllow = false;
            this.bCatapultAllowAI = false;
        }
        if (Mission.cur().sectFile().get("Mods", "CatapultBoost", 0) == 1 && !Mission.isCoop()) this.bCatapultBoost = true;
        if (Config.cur.ini.get("Mods", "CatapultAllowAI", 1) == 0) this.bCatapultAllowAI = false;
        if (Mission.cur().sectFile().get("Mods", "CatapultAllowAI", 1) == 0) this.bCatapultAllowAI = false;
        if (Config.cur.ini.get("Mods", "CatapultAI_CVE", 1) == 0) this.bCatapultAI_CVE = false;
        if (Mission.cur().sectFile().get("Mods", "CatapultAI_CVE", 1) == 0) this.bCatapultAI_CVE = false;
        if (Config.cur.ini.get("Mods", "CatapultAI_EssexClass", 1) == 0) this.bCatapultAI_EssexClass = false;
        if (Mission.cur().sectFile().get("Mods", "CatapultAI_EssexClass", 1) == 0) this.bCatapultAI_EssexClass = false;
        if (Config.cur.ini.get("Mods", "CatapultAI_Illustrious", 1) == 0) this.bCatapultAI_Illustrious = false;
        if (Mission.cur().sectFile().get("Mods", "CatapultAI_Illustrious", 1) == 0) this.bCatapultAI_Illustrious = false;
        if (Config.cur.ini.get("Mods", "CatapultAI_GrafZep", 1) == 0) this.bCatapultAI_GrafZep = false;
        if (Mission.cur().sectFile().get("Mods", "CatapultAI_GrafZep", 1) == 0) this.bCatapultAI_GrafZep = false;
        if (Config.cur.ini.get("Mods", "CatapultAI_CVL", 1) == 0) this.bCatapultAI_CVL = false;
        if (Mission.cur().sectFile().get("Mods", "CatapultAI_CVL", 1) == 0) this.bCatapultAI_CVL = false;
        if (Config.cur.ini.get("Mods", "StandardDeckCVL", 0) == 1) this.bStandardDeckCVL = true;
        if (Mission.cur().sectFile().get("Mods", "StandardDeckCVL", 0) == 1) this.bStandardDeckCVL = true;
        // ----------------------------------------
        
        // TODO: +++ By SAS~Storebror: Picked out of nowhere, undocumented changes found in BAT
        this.bHeavyAC = false;
        // ------------------------------------------------------------------------------------
    }

    public boolean onGround() {
        return this.onGround;
    }

    public boolean nearGround() {
        return this.nearGround;
    }

    public boolean isHydroOperable() {
        return this.bIsHydroOperable;
    }

    public void setHydroOperable(boolean bool) {
        this.bIsHydroOperable = bool;
    }

    public boolean isOperable() {
        return this.bIsOperable;
    }

    public void setOperable(boolean bool) {
        this.bIsOperable = bool;
    }

    public float getSteeringAngle() {
        return this.steerAngle;
    }

    public boolean isUnderDeck() {
        return this.bUnderDeck;
    }

    public boolean getWheelsOnGround() {
        boolean bool = this.isGearColl;
        this.isGearColl = false;
        return bool;
    }

    public void set(HierMesh hiermesh) {
        this.HM = hiermesh;
        if (this.pnti == null) {
            int i;
            for (i = 0; i < 61 && this.HM.hookFind("_Clip" + this.s(i)) >= 0; i++) {
                /* empty */
            }
            this.pnti = new int[i + 3];
            this.pnti[0] = this.HM.hookFind("_ClipLGear");
            this.pnti[1] = this.HM.hookFind("_ClipRGear");
            this.pnti[2] = this.HM.hookFind("_ClipCGear");
            for (i = 3; i < this.pnti.length; i++)
                this.pnti[i] = this.HM.hookFind("_Clip" + this.s(i - 3));
        }
        if (this.arrestorHook == null && hiermesh.hookFind("_ClipAGear") != -1) this.arrestorHook = new HookNamed(hiermesh, "_ClipAGear");
        int i = this.pnti[2];
        if (i > 0) {
            this.HM.hookMatrix(i, M4);
            if (M4.m03 > -1.0) this.bFrontWheel = true;
        }
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void computePlaneLandPose(FlightModel flightmodel) {
        this.FM = flightmodel;
        for (int i = 0; i < 3; i++)
            if (this.pnti[i] < 0) return;

        this.HM.hookMatrix(this.pnti[0], M4);
        double d1 = M4.m03;
        double d2 = M4.m23;
        double d3 = M4.m13;
        this.HM.hookMatrix(this.pnti[2], M4);
        double d4 = M4.m03;
        double d5 = M4.m23;
        this.HM.hookMatrix(this.pnti[1], M4);
        d1 = (d1 + M4.m03) * 0.5D;
        d2 = (d2 + M4.m23) * 0.5D;
        double d6 = d1 - d4;
        double d7 = d2 - d5;
        if (this.H == 0.0F || this.Pitch == 0.0F) {
            this.Pitch = -Geom.RAD2DEG((float) Math.atan2(d7, d6));
            if (d6 < 0.0D) this.Pitch += 180F;
            Line2f line2f = new Line2f();
            line2f.set(new Point2f((float) d1, (float) d2), new Point2f((float) d4, (float) d5));
            this.H = line2f.distance(new Point2f(0.0F, 0.0F));
            // TODO: By SAS~Storebror: Mass Factors +++
            // this.H -= (this.FM.M.massEmpty + this.FM.M.maxFuel + this.FM.M.maxNitro) * Atmosphere.g() / 2700000D;
            this.H -= (this.FM.M.massEmpty * this.FM.M.getMassFactor() + (this.FM.M.maxFuel + this.FM.M.maxNitro) * this.FM.M.getFuelFactor()) * Atmosphere.g() / 2700000D;
            // TODO: By SAS~Storebror: Mass Factors ---
        }
        double d8 = Math.sqrt(d6 * d6 + d7 * d7);
        d1 = d6 / d8 * d1 + d7 / d8 * d2;
        if (d6 < 0.0D) d1 = -d1;
        this.mgx = (float) d1;
        this.mgy = (float) d3;
        this.sgx = (float) d4;
        this.mgh = (float) Math.sqrt(this.mgx * this.mgx + this.mgy * this.mgy);
        this.mgalpha = (float) Math.atan2(this.mgx, this.mgy);
    }
    // TODO: --- TD AI code backport from 4.13 ---

    public void set(Gear gear) {
        if (gear.pnti == null)
            return;
        this.pnti = new int[gear.pnti.length];
        if (gear.waterList != null) {
            this.waterList = new int[gear.waterList.length];
            for (int i = 0; i < this.waterList.length; i++)
                this.waterList[i] = gear.waterList[i];
        }
        for (int i = 0; i < this.pnti.length; i++)
            this.pnti[i] = gear.pnti[i];
        this.bIsSail = gear.bIsSail;
        this.sinkFactor = gear.sinkFactor;
        this.springsStiffness = gear.springsStiffness;
        this.H = gear.H;
        this.Pitch = gear.Pitch;
        this.bFrontWheel = gear.bFrontWheel;
     // TODO: +++ Shock Absorber backport from EngineMod 2.8 +++
        shockAbsorber = gear.shockAbsorber;
     // TODO: --- Shock Absorber backport from EngineMod 2.8 ---
        // TODO: +++ By SAS~Storebror: Picked out of nowhere, undocumented changes found in BAT
        this.bHeavyAC = gear.bHeavyAC;
        // ------------------------------------------------------------------------------------
    }

    public void ground(FlightModel flightmodel, boolean bool) {
        this.ground(flightmodel, bool, false);
    }

    public void ground(FlightModel flightmodel, boolean flag, boolean flag1) {
        this.FM = flightmodel;
        this.bIsMaster = flag;
        this.onGround = flag1;
        this.FM.Vrel.x = -this.FM.Vwld.x;
        this.FM.Vrel.y = -this.FM.Vwld.y;
        this.FM.Vrel.z = -this.FM.Vwld.z;
        for (int i = 0; i < 2; i++)
            if (this.fatigue[i] > 0) this.fatigue[i]--;

        Pn.set(this.FM.Loc);
        Pn.z = Engine.cur.land.HQ(Pn.x, Pn.y);
        double d1 = Pn.z;
        this.screenHQ = d1;
        if (this.FM.Loc.z - d1 > 50D && !this.bFlatTopGearCheck) {
            this.nearGround = false;
            this.turnOffEffects();
            this.arrestorVSink = -50F;
            return;
        }
        this.isWater = Engine.cur.land.isWater(Pn.x, Pn.y);
        if (this.isWater) this.roughness = 0.5D;
        Engine.cur.land.EQN(Pn.x, Pn.y, Normal);
        this.bUnderDeck = false;
        BigshipGeneric bigshipgeneric = null;
        if (this.bFlatTopGearCheck) {
            corn.set(this.FM.Loc);
            corn1.set(this.FM.Loc);
            corn1.z -= 20D;
            Actor actor = Engine.collideEnv().getLine(corn, corn1, false, clipFilter, Pship);
            if (actor instanceof BigshipGeneric) {
                Pn.z = Pship.z + 0.5D;
                d1 = Pn.z;
                this.isWater = false;
                this.bUnderDeck = true;
                actor.getSpeed(Vship);
                this.FM.Vrel.add(Vship);
                bigshipgeneric = (BigshipGeneric) actor;
                bigshipgeneric.addRockingSpeed(this.FM.Vrel, Normal, this.FM.Loc);
                if (flightmodel.AS.isMaster() && bigshipgeneric.getAirport() != null && flightmodel.CT.bHasArrestorControl) {
                    if (!bigshipgeneric.isTowAircraft((Aircraft) flightmodel.actor) && this.FM.Vrel.lengthSquared() > 10D && flightmodel.CT.getArrestor() > 0.1F) {
                        bigshipgeneric.requestTowAircraft((Aircraft) flightmodel.actor);
                        if (bigshipgeneric.isTowAircraft((Aircraft) flightmodel.actor)) {
                            flightmodel.AS.setFlatTopString(bigshipgeneric, bigshipgeneric.towPortNum);
                            if (this.FM instanceof RealFlightModel && this.bIsMaster && ((RealFlightModel) this.FM).isRealMode()) ((RealFlightModel) this.FM).producedShakeLevel = 5F;
                            ((Aircraft) flightmodel.actor).sfxTow();
                        }
                    }
                    if (bigshipgeneric.isTowAircraft((Aircraft) flightmodel.actor) && this.FM.Vrel.lengthSquared() < 1.0D && World.Rnd().nextFloat() < 0.008F) {
                        bigshipgeneric.requestDetowAircraft((Aircraft) flightmodel.actor);
                        flightmodel.AS.setFlatTopString(bigshipgeneric, -1);
                    }
                }
                if (bigshipgeneric.isTowAircraft((Aircraft) flightmodel.actor)) {
                    int k = bigshipgeneric.towPortNum;
                    Point3d apoint3d[] = bigshipgeneric.getShipProp().propAirport.towPRel;
                    bigshipgeneric.pos.getAbs(this.l0);
                    this.l0.transform(apoint3d[k * 2], this.p0);
                    this.l0.transform(apoint3d[k * 2 + 1], this.p1);
                    this.p0.x = 0.5D * (this.p0.x + this.p1.x);
                    this.p0.y = 0.5D * (this.p0.y + this.p1.y);
                    this.p0.z = 0.5D * (this.p0.z + this.p1.z);
                    flightmodel.actor.pos.getAbs(this.l0);
                    this.l0.transformInv(this.p0);
                    this.l0.set(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                    bigshipgeneric.towHook.computePos(flightmodel.actor, new Loc(this.l0), this.l0);
                    this.v0.sub(this.p0, this.l0.getPoint());
                    if (this.v0.x > 0.0D) {
                        if (bigshipgeneric.isTowAircraft((Aircraft) flightmodel.actor)) {
                            bigshipgeneric.requestDetowAircraft((Aircraft) flightmodel.actor);
                            flightmodel.AS.setFlatTopString(bigshipgeneric, -1);
                        }
                    } else {
                        this.tmpV.set(this.FM.Vrel);
                        flightmodel.actor.pos.getAbsOrient().transformInv(this.tmpV);
                        if (this.tmpV.x < 0.0D) {
                            double d3 = this.v0.length();
                            this.v0.normalize();
                            this.arrestorVAngle = (float) Math.toDegrees(Math.asin(this.v0.z));
                            this.v0.scale(1000D * d3);
                            flightmodel.GF.add(this.v0);
                            this.v0.scale(0.3D);
                            this.v0.cross(this.l0.getPoint(), this.v0);
                            flightmodel.GM.add(this.v0);
                        }
                    }
                } else this.arrestorVAngle = 0.0F;
            }
        }
        if (flightmodel.CT.bHasArrestorControl) {
            flightmodel.actor.pos.getAbs(Aircraft.tmpLoc1);
            Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
            this.arrestorHook.computePos(flightmodel.actor, Aircraft.tmpLoc1, loc);
            this.arrestorVSink = (float) (Pn.z - loc.getPoint().z);
        }
        Fd.set(this.FM.Vrel);
        Vnf.set(Normal);
        this.FM.Or.transformInv(Normal);
        this.FM.Or.transformInv(Fd);
        Fd.normalize();
        Pn.x = 0.0D;
        Pn.y = 0.0D;
        Pn.z -= this.FM.Loc.z;
        this.FM.Or.transformInv(Pn);
        this.D = -Normal.dot(Pn);
        if (!this.bIsMaster) this.D -= 0.015D;
        if (this.D > 50D) {
            this.nearGround = false;
            return;
        }
        this.nearGround = true;
        this.gWheelSinking[0] = this.gWheelSinking[1] = this.gWheelSinking[2] = 0.0F;
        for (int j = 0; j < this.pnti.length; j++) {
            int l = this.pnti[j];
            if (l <= 0) Pnt[j].set(0.0F, 0.0F, 0.0F);
            else {
                this.HM.hookMatrix(l, M4);
                Pnt[j].set((float) M4.m03, (float) M4.m13, (float) M4.m23);
            }
        }

        this.nP = 0;
        this.nOfGearsOnGr = 0;
        this.nOfPoiOnGr = 0;
        this.tmpV.set(0.0D, 1.0D, 0.0D);
        Forward.cross(this.tmpV, Normal);
        Forward.normalize();
        Right.cross(Normal, Forward);
//        System.out.println("pnti.length=" + this.pnti.length);
        for (int i1 = 0; i1 < this.pnti.length; i1++) {
            clp[i1] = false;
            if (i1 <= 2) {
                this.bIsGear = true;
                if (i1 == 0 && (!this.lgear || this.FM.CT.getGear() < 0.01F) || i1 == 1 && (!this.rgear || this.FM.CT.getGear() < 0.01F) || i1 == 2 && !this.cgear) continue;
            } else this.bIsGear = false;
            PnT.set(Pnt[i1]);
            this.d = Normal.dot(PnT) + this.D;
            Fx.set(Fd);
            this.MassCoeff = 0.0004D * this.FM.M.getFullMass();
            if (this.MassCoeff > 1.0D) this.MassCoeff = 1.0D;
//			if (i1 <= 2) System.out.println("ground d=" + d);
//            System.out.println("d=" + d + ", Normal.dot(PnT)=" + Normal.dot(PnT) + ", D=" + this.D);
            if (this.d < 0.0D) {
                if (!this.testPropellorCollision(i1) || (this.isWater ? !this.testWaterCollision(i1) : this.bIsGear ? !this.testGearCollision(i1) : !this.testNonGearCollision(i1))) continue;
                clp[i1] = true;
                this.nP++;
            } else {
                if (this.d >= 0.1D || this.isWater || this.bIsGear || !this.testNonGearCollision(i1)) continue;
                clp[i1] = true;
                this.nP++;
            }
            PnT.x += this.FM.Arms.GC_GEAR_SHIFT;
            Fx.cross(PnT, Tn);
            Fv.set(Fx);
            if (this.bIsSail && this.bInWaterList(i1)) {
                this.tmpV.scale(this.fricF * 0.5D, Forward);
                Tn.add(this.tmpV);
                this.tmpV.scale(this.fricR * 0.5D, Right);
                Tn.add(this.tmpV);
            }
            if (this.bIsMaster) {
                this.FM.GF.add(Tn);
                this.FM.GM.add(Fx);
            }
        }

        if (this.oldNOfGearsOnGr != this.nOfGearsOnGr || this.oldNOfPoiOnGr != this.nOfPoiOnGr) this.gearsChanged = true;
        else this.gearsChanged = false;
        this.oldNOfGearsOnGr = this.nOfGearsOnGr;
        this.oldNOfPoiOnGr = this.nOfPoiOnGr;
        this.onGround = this.nP > 0;
        if (Config.isUSE_RENDER()) this.drawEffects();
        if (this.bIsMaster) {
            this.FM.canChangeBrakeShoe = false;
            BigshipGeneric bigshipgeneric1 = bigshipgeneric;
            if (bigshipgeneric1 != null) this.FM.brakeShoeLastCarrier = bigshipgeneric1;
            else if (Mission.isCoop() && !Mission.isServer() && Actor.isAlive(this.FM.brakeShoeLastCarrier) && Time.current() < 60000L) bigshipgeneric1 = (BigshipGeneric) this.FM.brakeShoeLastCarrier;
//			System.out.println("nOfGearsOnGr == " + nOfGearsOnGr + ", nP == " + nP + ", FM.Vrel.lengthSquared() = " + FM.Vrel.lengthSquared());
            if (bigshipgeneric1 != null) {
                if (this.FM.brakeShoe) {
                    if (!this.isAnyDamaged()) {
                        L.set(this.FM.brakeShoeLoc);
                        L.add(this.FM.brakeShoeLastCarrier.pos.getAbs());

                        // TODO: CTO Mod
                        // ----------------------------------------
                        this.FM.Loc.set(L.getPoint());
                        this.FM.Or.set(L.getOrient());
                        if (!this.bAlreadySetCatapult) {
                            this.bCatapultSet = this.setCatapultOffset(bigshipgeneric1);
                            this.bAlreadySetCatapult = true;
                        }
                        if (this.bCatapultAllow && !this.FM.EI.getCatapult() && this.bCatapultSet) {
                            ((Actor) bigshipgeneric1.getAirport()).pos.getCurrent();
                            Point3d point3d = L.getPoint();
                            CellAirField cellairfield = bigshipgeneric1.getCellTO();
                            double d4 = -cellairfield.leftUpperCorner().x - this.dCatapultOffsetX;
                            double d6 = cellairfield.leftUpperCorner().y - this.dCatapultOffsetY;
                            Loc loc2 = new Loc(d6, d4, 0.0D, 0.0F, 0.0F, 0.0F);
                            loc2.add(this.FM.brakeShoeLastCarrier.pos.getAbs());
                            Point3d point3d1 = loc2.getPoint();
                            double d8 = (point3d.x - point3d1.x) * (point3d.x - point3d1.x) + (point3d.y - point3d1.y) * (point3d.y - point3d1.y);
                            if (d8 <= 100D) {
                                L.set(d6, d4, this.FM.brakeShoeLoc.getZ(), this.FM.brakeShoeLoc.getAzimut(), this.FM.brakeShoeLoc.getTangage(), this.FM.brakeShoeLoc.getKren());
                                L.add(this.FM.brakeShoeLastCarrier.pos.getAbs());
                                this.FM.Loc.set(L.getPoint());
                                this.FM.Or.setYPR(this.FM.brakeShoeLastCarrier.pos.getAbsOrient().getYaw(), this.Pitch, this.FM.brakeShoeLastCarrier.pos.getAbsOrient().getRoll());
                                this.FM.actor.pos.setAbs(this.FM.Loc, this.FM.Or);
                                this.FM.EI.setCatapult(this.FM.M.mass, this.bCatapultBoost);
                                this.FM.brakeShoeLoc.set(this.FM.actor.pos.getAbs());
                                this.FM.brakeShoeLoc.sub(this.FM.brakeShoeLastCarrier.pos.getAbs());
                            } else if (this.dCatapultOffsetX2 > 0.0D) {
                                double d5 = -cellairfield.leftUpperCorner().x - this.dCatapultOffsetX2;
                                double d7 = cellairfield.leftUpperCorner().y - this.dCatapultOffsetY2;
                                loc2.set(d7, d5, 0.0D, 0.0F, 0.0F, 0.0F);
                                loc2.add(this.FM.brakeShoeLastCarrier.pos.getAbs());
                                Point3d point3d2 = loc2.getPoint();
                                double d9 = (point3d.x - point3d2.x) * (point3d.x - point3d2.x) + (point3d.y - point3d2.y) * (point3d.y - point3d2.y);
                                if (d9 <= 100D) {
                                    L.set(d7, d5, this.FM.brakeShoeLoc.getZ(), this.FM.brakeShoeLoc.getAzimut(), this.FM.brakeShoeLoc.getTangage(), this.FM.brakeShoeLoc.getKren());
                                    L.add(this.FM.brakeShoeLastCarrier.pos.getAbs());
                                    this.FM.Loc.set(L.getPoint());
                                    this.FM.Or.setYPR(this.FM.brakeShoeLastCarrier.pos.getAbsOrient().getYaw(), this.Pitch, this.FM.brakeShoeLastCarrier.pos.getAbsOrient().getRoll());
                                    this.FM.actor.pos.setAbs(this.FM.Loc, this.FM.Or);
                                    this.FM.EI.setCatapult(this.FM.M.mass, this.bCatapultBoost);
                                    this.FM.brakeShoeLoc.set(this.FM.actor.pos.getAbs());
                                    this.FM.brakeShoeLoc.sub(this.FM.brakeShoeLastCarrier.pos.getAbs());
                                }
                            }
                        } else if (this.FM.EI.getCatapult()) this.FM.EI.resetCatapultTime();
                        this.FM.brakeShoeLastCarrier.getSpeed(this.FM.Vwld);
                        // ----------------------------------------

                        this.FM.Vrel.set(0.0D, 0.0D, 0.0D);
                        for (int j1 = 0; j1 < 3; j1++)
                            this.gVelocity[j1] = 0.0D;

                        this.onGround = true;
                        this.FM.canChangeBrakeShoe = true;
                    } else this.FM.brakeShoe = false;
                } else {
                    // TODO: CTO Mod
                    // ----------------------------------------
                    if (this.FM.EI.getCatapult()) this.FM.Or.setYPR(this.FM.brakeShoeLastCarrier.pos.getAbsOrient().getYaw(), this.FM.Or.getPitch(), this.FM.brakeShoeLastCarrier.pos.getAbsOrient().getRoll());
                    if (this.nOfGearsOnGr == 3 && this.nP == 3 && this.FM.Vrel.lengthSquared() < 1.0D) {
                        this.FM.brakeShoeLoc.set(this.FM.actor.pos.getCurrent());
                        this.FM.brakeShoeLoc.sub(this.FM.brakeShoeLastCarrier.pos.getCurrent());
                        this.FM.canChangeBrakeShoe = true;
                    }
                    this.bAlreadySetCatapult = false;
                    // ----------------------------------------
                }
            }
            // TODO: Added by |ZUTI|: enables usage of chocks on ground airfields too. And compensate if player fires during chocks-in period - no back thrust.
            // -----------------------------------------------------------------------
            else if (this.nOfGearsOnGr == 3 && this.nP == 3 && this.FM.Vrel.lengthSquared() < 1.5) {
                // TODO: +++ Slowly rolling wheels Mod by SAS~Storebror +++
                if (this.FM.Vrel.lengthSquared() < 0.0001) for (int k1 = 0; k1 < 3; k1++)
                    this.gVelocity[k1] = 0.0D;
                // TODO: --- Slowly rolling wheels Mod by SAS~Storebror ---
                this.FM.brakeShoeLoc.set(this.FM.actor.pos.getCurrent());
                this.FM.Vrel.set(0.0D, 0.0D, 0.0D);
                // TODO: +++ Slowly rolling wheels Mod by SAS~Storebror +++
//                for(int k1 = 0; k1 < 3; k1++)
//                    gVelocity[k1] = 0.0D;
                // TODO: --- Slowly rolling wheels Mod by SAS~Storebror ---

                this.FM.canChangeBrakeShoe = true;
                this.onGround = true;

                if (this.FM.brakeShoe) {
                    this.FM.GF.set(0.0D, 0.0D, 0.0D);
                    this.FM.GM.set(0.0D, 0.0D, 0.0D);
                    this.FM.Vwld.set(0.0D, 0.0D, 0.0D);
                }
            }
            // -----------------------------------------------------------------------
        }
        if (!this.bIsMaster) return;
        if (this.onGround && !this.isWater) this.processingCollisionEffect();
        double d2 = Engine.cur.land.HQ_ForestHeightHere(this.FM.Loc.x, this.FM.Loc.y);
        if (d2 > 0.0D && this.FM.Loc.z <= d1 + d2 && ((Aircraft) this.FM.actor).isEnablePostEndAction(0.0D)) ((Aircraft) this.FM.actor).postEndAction(0.0D, Engine.actorLand(), 2, null);
    }

    private boolean testNonGearCollision(int i) {
        this.nOfPoiOnGr++;
        Vs.set(this.FM.Vrel);
        Vs.scale(-1.0);
        this.FM.Or.transformInv(Vs);
        this.tmpV.set(Pnt[i]);
        this.tmpV.cross(this.FM.getW(), this.tmpV);
        Vs.add(this.tmpV);
        ForwardVPrj = Forward.dot(Vs);
        NormalVPrj = Normal.dot(Vs);
        RightVPrj = Right.dot(Vs);
        if (NormalVPrj > 0.0) {
            NormalVPrj -= 3.0;
            if (NormalVPrj < 0.0) NormalVPrj = 0.0;
        }
        double d = 1.0;
        double d_11_ = this.d - 0.06;
        double d_12_ = this.d + 0.04;
        if (d_11_ > 0.0) d_11_ = 0.0;
        if (d_11_ < -2.0) d_11_ = -2.0;
        if (d_12_ > 0.0) d_12_ = 0.0;
        if (d_12_ < -0.25) d_12_ = -0.25;
        d = Math.max(-120000.0 * d_11_, -360000.0 * d_12_);
        d *= this.MassCoeff;
        Tn.scale(d, Normal);
        this.fric = -40000.0 * NormalVPrj;
        if (this.fric > 100000.0) this.fric = 100000.0;
        if (this.fric < -100000.0) this.fric = -100000.0;
        this.tmpV.scale(this.fric, Normal);
        Tn.add(this.tmpV);
        double d_13_ = 1.0 - 0.5 * Math.abs(Pnt[i].y) / this.FM.Arms.WING_END;
        this.fricF = -8000.0 * ForwardVPrj;
        this.fricR = -50000.0 * RightVPrj;
        this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
        if (this.fric > 20000.0 * d_13_) {
            this.fric = 20000.0 * d_13_ / this.fric;
            this.fricF *= this.fric;
            this.fricR *= this.fric;
        }
        this.tmpV.scale(this.fricF, Forward);
        Tn.add(this.tmpV);
        this.tmpV.scale(this.fricR, Right);
        Tn.add(this.tmpV);
        if (i > 6 && this.bIsMaster) {
            Actor actor = this.FM.actor;
            World.cur();
            if (actor == World.getPlayerAircraft() && this.FM.Loc.z - Engine.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y) < 2.0 && !this.bTheBabysGonnaBlow) {
                for (int i_14_ = 0; i_14_ < this.FM.CT.Weapons.length; i_14_++)
                    if (this.FM.CT.Weapons[i_14_] != null && this.FM.CT.Weapons[i_14_].length > 0) for (int i_15_ = 0; i_15_ < this.FM.CT.Weapons[i_14_].length; i_15_++)
                        if ((this.FM.CT.Weapons[i_14_][i_15_] instanceof BombGun || this.FM.CT.Weapons[i_14_][i_15_] instanceof RocketGun || this.FM.CT.Weapons[i_14_][i_15_] instanceof RocketBombGun) && this.FM.CT.Weapons[i_14_][i_15_].haveBullets()
                                && this.FM.getSpeed() > 38.0F && this.FM.CT.Weapons[i_14_][i_15_].getHookName().startsWith("_External"))
                            this.bTheBabysGonnaBlow = true;
                if (this.bTheBabysGonnaBlow && (!this.FM.isPlayers() || World.cur().diffCur.Vulnerability) && ((Aircraft) this.FM.actor).isEnablePostEndAction(0.0)) {
                    ((Aircraft) this.FM.actor).postEndAction(0.0, Engine.actorLand(), 2, null);
                    if (this.FM.isPlayers()) HUD.log("FailedBombsDetonate");
                }
            }
        }
        if (this.bIsMaster && NormalVPrj < 0.0) {
            double d_16_ = (ForwardVPrj * ForwardVPrj + RightVPrj * RightVPrj) * 2.3668639053254438E-4 + NormalVPrj * NormalVPrj * 0.02040816326530612;
            if (d_16_ > 1.0) this.landHit(i, (float) d_16_);
        }
        return true;
    }

    private boolean testGearCollision(int i) {
        //this.MassCoeff = 2.0D; // TEST
        
        if (this.FM.CT.getGear() < 0.01) return false;
        double d1 = 1.0;
        this.gWheelSinking[i] = (float) -this.d;
        Vs.set(this.FM.Vrel);
        Vs.scale(-1.0);
        this.FM.Or.transformInv(Vs);
        this.tmpV.set(Pnt[i]);
        this.tmpV.cross(this.FM.getW(), this.tmpV);
        Vs.add(this.tmpV);
        ForwardVPrj = Forward.dot(Vs);
        NormalVPrj = Normal.dot(Vs);
        RightVPrj = Right.dot(Vs);
        if (NormalVPrj > 0.0) NormalVPrj = 0.0;
        double d2 = this.FM.Vrel.x * this.FM.Vrel.x + this.FM.Vrel.y * this.FM.Vrel.y - 2.0;
        if (d2 < 0.0) d2 = 0.0;
        double d3 = 0.01 * d2;
        if (d3 < 0.0) d3 = 0.0;
        if (d3 > 4.5) d3 = 4.5;
        double d4 = 0.4 * Math.max(this.roughness * this.roughness, this.roughness);
        if (d3 > d4) d3 = d4;
        if (this.roughness > d3) this.roughness = d3;
        if (this.roughness < 0.2) this.roughness = 0.2;
        // TODO: +++ By SAS~Storebror: Picked out of nowhere, undocumented changes found in BAT
        if ((this.FM.M.maxWeight > 50000.0F) && (this.bFrontWheel)) {
            this.bHeavyAC = true;
        }
        // ------------------------------------------------------------------------------------
        if (i < 2) {
            this.d += World.Rnd().nextFloat(-2.0F, 1.0F) * 0.04 * d3 * this.MassCoeff;
            d1 = Math.max(-9500.0 * (this.d - 0.1), -950000.0 * this.d);
            d1 *= this.springsStiffness;
        } else {
            this.d += World.Rnd().nextFloat(-2.0F, 1.0F) * 0.04F * d3 * this.MassCoeff;
            d1 = Math.max(-9500.0 * (this.d - 0.1), -950000.0 * this.d);
            // TODO: +++ By SAS~Storebror: Picked out of nowhere, undocumented changes found in BAT
            // if (Pnt[i].x > 0.0F && Fd.dot(Normal) >= 0.0D) d1 *= 0.45D;
            if (Pnt[i].x > 0.0F && Fd.dot(Normal) >= 0.0D && !this.bHeavyAC) d1 *= 0.45D;
            // ------------------------------------------------------------------------------------
            else d1 *= this.tailStiffness;
        }
        d1 -= 40000.0 * NormalVPrj;
     // TODO: +++ Shock Absorber backport from EngineMod 2.8 +++
        if (Vs.z > 0.0F && shockAbsorber != 0F) {
            float abs = (shockAbsorber > 0F) ? gWheelSinking[i] * shockAbsorber : (1F - gWheelSinking[i]) * -shockAbsorber;
            if (abs > 1.0F) abs = 1.0F;
            Tn.scale(d1 * (1.0F - abs), Normal);
        } else
     // TODO: --- Shock Absorber backport from EngineMod 2.8 ---
        Tn.scale(d1, Normal);
        double d6 = this.FM.CT.getBrake();
        double d7 = this.FM.CT.getRudder();
        // TAK++
        double db1 = this.FM.CT.getBrakeRight();
        double db2 = this.FM.CT.getBrakeLeft();
        double da = db1 - db2;
        double db = Math.max(d6, Math.max(db1, db2)); // applied pedal brakes
        // TAK--
        double d5 = 0.0001D * d1;
        if (this.FM.getSpeedKMH() <= 10F - 10F * Math.min(db, 1F) && d5 > 3.3D)
            d5 = 3.3D;  // TODO: Limiter to avoid heavy planes bouncing on the ground
//		System.out.println("testGearCollision(" + i+ ")");
        switch (i) {
            case 0:
            case 1: {
                double d8 = 1.2;
                if (i == 0) d8 = -1.2;
                this.nOfGearsOnGr++;
                this.isGearColl = true;
                this.gVelocity[i] = ForwardVPrj;
                if (d6 > 0.1 || db > 0.1D) {
                    if (d7 > 0.1) d6 += d8 * d6 * (d7 - 0.1);
                    if (d7 < -0.1) d6 += d8 * d6 * (d7 + 0.1);
                    // TAK
                    if (da > 0.1) d6 += d8 * 4.0F * (da - 0.1);
                    if (da < -0.1) d6 += d8 * 4.0F * (da + 0.1);
                    // TAK--
                    if (d6 > 1.0) d6 = 1.0;
                    if (d6 < 0.0) d6 = 0.0;
                }
                double d9 = Math.sqrt(ForwardVPrj * ForwardVPrj + RightVPrj * RightVPrj);
                if (d9 < 0.01) d9 = 0.01;
                double d10 = 1.0 / d9;
                double d11 = ForwardVPrj * d10;
                if (d11 < 0.0) d11 *= -1.0;
                double d12 = RightVPrj * d10;
                if (d12 < 0.0) d12 *= -1.0;
                double d13 = 5.0;
                if (PnT.y * RightVPrj > 0.0) {
                    if (PnT.y > 0.0) d13 += 7.0 * RightVPrj;
                    else d13 -= 7.0 * RightVPrj;
                    if (d13 > 20.0) d13 = 20.0;
                }
                double d14 = 15000.0;
                if (d9 < 3.0) {
                    double d_30_ = -0.333333 * (d9 - 3.0);
                    d14 += 3000.0 * d_30_ * d_30_;
                }
                this.fricR = -d13 * 100000.0 * RightVPrj * d5;
                this.maxFric = d14 * d5 * d12;
                if (this.fricR > this.maxFric) this.fricR = this.maxFric;
                if (this.fricR < -this.maxFric) this.fricR = -this.maxFric;
                this.fricF = -d13 * 600.0 * ForwardVPrj * d5;
                this.maxFric = d13 * Math.max(200.0 * (1.0 - 0.04 * d9), 5.0) * d5 * d11;
                if (this.fricF > this.maxFric) this.fricF = this.maxFric;
                if (this.fricF < -this.maxFric) this.fricF = -this.maxFric;
                double d16 = 0.03;
                if (Pnt[2].x > 0.0F) d16 = 0.06;
                double d17 = Math.abs(ForwardVPrj);
                if (d17 < 1.0) d16 += 3.0 * (1.0 - d17);
                d16 *= 0.03 * d6;
                this.fricF += -300000.0 * d16 * ForwardVPrj * d5;
                this.maxFric = d14 * d5 * d11;
                if (this.fricF > this.maxFric) this.fricF = this.maxFric;
                if (this.fricF < -this.maxFric) this.fricF = -this.maxFric;
                this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
                if (this.fric > this.maxFric) {
                    this.fric = this.maxFric / this.fric;
                    this.fricF *= this.fric;
                    this.fricR *= this.fric;
                }
                this.tmpV.scale(this.fricF, Forward);
                Tn.add(this.tmpV);
                this.tmpV.scale(this.fricR, Right);
                Tn.add(this.tmpV);
                if (this.bIsMaster && NormalVPrj < 0.0) {
                    double d18 = ForwardVPrj * ForwardVPrj * 8.0E-5 + RightVPrj * RightVPrj * 0.0068;
                    if (this.FM.CT.bHasArrestorControl) d18 += NormalVPrj * NormalVPrj * 0.025;
                    else d18 += NormalVPrj * NormalVPrj * 0.07;
                    if (d18 > 1.0) {
                        this.fatigue[i] += 10;
                        // TODO: By SAS~Storebror: Mass Factors +++
//                        double d21 = 38000.0 + this.FM.M.massEmpty * 6.0;
                        double d21 = 38000.0 + this.FM.M.massEmpty * this.FM.M.getMassFactor() * 6.0;
                        // TODO: By SAS~Storebror: Mass Factors ---
                        double d23 = (Tn.x * Tn.x * 0.15 + Tn.y * Tn.y * 0.15 + Tn.z * Tn.z * 0.08) / (d21 * d21);
                        if (this.fatigue[i] > 100 || d23 > 1.0) {
                            this.landHit(i, (float) d18);
                            Aircraft aircraft = (Aircraft) this.FM.actor;
                            if (i == 0) aircraft.msgCollision(aircraft, "GearL2_D0", "GearL2_D0");
                            if (i == 1) aircraft.msgCollision(aircraft, "GearR2_D0", "GearR2_D0");
                        }
                    }
                }
                break;
            }
            case 2:
                this.nOfGearsOnGr++;
                if (this.bTailwheelLocked && this.steerAngle > -5.0F && this.steerAngle < 5.0F) {
                    this.gVelocity[i] = ForwardVPrj;
                    this.steerAngle = 0.0F;
                    this.fric = -400.0 * ForwardVPrj;
                    this.maxFric = 400.0;
                    if (this.fric > this.maxFric) this.fric = this.maxFric;
                    if (this.fric < -this.maxFric) this.fric = -this.maxFric;
                    this.tmpV.scale(this.fric, Forward);
                    Tn.add(this.tmpV);
                    this.fric = -10000.0 * RightVPrj;
                    this.maxFric = 40000.0;
                    if (this.fric > this.maxFric) this.fric = this.maxFric;
                    if (this.fric < -this.maxFric) this.fric = -this.maxFric;
                    this.tmpV.scale(this.fric, Right);
                    Tn.add(this.tmpV);
                } else if (this.bFrontWheel) {
                    this.gVelocity[i] = ForwardVPrj;
                    // tmpV.set(1.0, -0.5 * (double)FM.CT.getRudder(), 0.0);
                    // TAK++
                    this.tmpV.set(1.0, -0.5 * (db1 - db2 + this.FM.CT.getRudder()), 0.0);
                    // TAK--
                    steerAngleFork.setDeg(this.steerAngle, (float) Math.toDegrees(Math.atan2(this.tmpV.y, this.tmpV.x)));
                    this.steerAngle = steerAngleFork.getDeg(0.115F);
                    nwRight.cross(Normal, this.tmpV);
                    nwRight.normalize();
                    nwForward.cross(nwRight, Normal);
                    ForwardVPrj = nwForward.dot(Vs);
                    RightVPrj = nwRight.dot(Vs);
                    double d19 = Math.sqrt(ForwardVPrj * ForwardVPrj + RightVPrj * RightVPrj);
                    if (d19 < 0.01) d19 = 0.01;
                    this.fricF = -100.0 * ForwardVPrj;
                    this.maxFric = 4000.0;
                    if (this.fricF > this.maxFric) this.fricF = this.maxFric;
                    if (this.fricF < -this.maxFric) this.fricF = -this.maxFric;
                    this.fricR = -500.0 * RightVPrj;
                    this.maxFric = 4000.0;
                    if (this.fricR > this.maxFric) this.fricR = this.maxFric;
                    if (this.fricR < -this.maxFric) this.fricR = -this.maxFric;
                    this.maxFric = 1.0 - 0.02 * d19;
                    if (this.maxFric < 0.1) this.maxFric = 0.1;
                    this.maxFric = 5000.0 * this.maxFric;
                    this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
                    if (this.fric > this.maxFric) {
                        this.fric = this.maxFric / this.fric;
                        this.fricF *= this.fric;
                        this.fricR *= this.fric;
                    }
                    this.tmpV.scale(this.fricF, Forward);
                    Tn.add(this.tmpV);
                    this.tmpV.scale(this.fricR, Right);
                    Tn.add(this.tmpV);
                } else {
                    this.gVelocity[i] = Vs.length();
                    if (Vs.lengthSquared() > 0.04) {
                        steerAngleFork.setDeg(this.steerAngle, (float) Math.toDegrees(Math.atan2(Vs.y, Vs.x)));
                        this.steerAngle = steerAngleFork.getDeg(0.115F);
                    }
                    this.fricF = -1000.0 * ForwardVPrj;
                    this.fricR = -1000.0 * RightVPrj;
                    this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
                    this.maxFric = 1500.0;
                    if (this.fric > this.maxFric) {
                        this.fric = this.maxFric / this.fric;
                        this.fricF *= this.fric;
                        this.fricR *= this.fric;
                    }
                    this.tmpV.scale(this.fricF, Forward);
                    Tn.add(this.tmpV);
                    this.tmpV.scale(this.fricR, Right);
                    Tn.add(this.tmpV);
                }
                if (this.bIsMaster && NormalVPrj < 0.0) {
                    double d20 = (ForwardVPrj * ForwardVPrj + RightVPrj * RightVPrj) * 0.0001D;
                    if (this.FM.CT.bHasArrestorControl) d20 += NormalVPrj * NormalVPrj * 0.004D;
                    else d20 += NormalVPrj * NormalVPrj * 0.02;
                    if (d20 > 1.0) {
                        this.landHit(i, d20);
                        Aircraft aircraft = (Aircraft) this.FM.actor;
                        aircraft.msgCollision(aircraft, "GearC2_D0", "GearC2_D0");
                    }
                }
                break;
            default:
                this.fricF = -4000.0 * ForwardVPrj;
                this.fricR = -4000.0 * RightVPrj;
                this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
                if (this.fric > 10000.0) {
                    this.fric = 10000.0 / this.fric;
                    this.fricF *= this.fric;
                    this.fricR *= this.fric;
                }
                this.tmpV.scale(this.fricF, Forward);
                Tn.add(this.tmpV);
                this.tmpV.scale(this.fricR, Right);
                Tn.add(this.tmpV);
        }
        Tn.scale(this.MassCoeff);
        return true;
    }

    private boolean testWaterCollision(int i) {
        Vs.set(this.FM.Vrel);
        Vs.scale(-1.0);
        this.FM.Or.transformInv(Vs);
        this.tmpV.set(Pnt[i]);
        this.tmpV.cross(this.FM.getW(), this.tmpV);
        Vs.add(this.tmpV);
        ForwardVPrj = Forward.dot(Vs);
        NormalVPrj = Normal.dot(Vs);
        RightVPrj = Right.dot(Vs);
        double d = ForwardVPrj;
        if (d < 0.0) d = 0.0;
        if ((!this.bIsSail || !this.bInWaterList(i)) && this.d < -2.0) this.d = -2.0;
        double d_39_ = -(1.0 + 0.3 * d) * this.sinkFactor * this.d * Math.abs(this.d) * (1.0 + 0.3 * Math.sin((1 + i % 3) * Time.current() * 0.0010));
        if (this.bIsSail && this.bInWaterList(i)) {
            if (NormalVPrj > 0.0) NormalVPrj = 0.0;
            Tn.scale(d_39_, Normal);
            this.fric = -1000.0 * NormalVPrj;
            if (this.fric > 4000.0) this.fric = 4000.0;
            if (this.fric < -4000.0) this.fric = -4000.0;
            this.tmpV.scale(this.fric, Normal);
            Tn.add(this.tmpV);
            this.fricF = -40.0 * ForwardVPrj;
            this.fricR = -300.0 * RightVPrj;
            this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
            if (this.fric > 50000.0) {
                this.fric = 50000.0 / this.fric;
                this.fricF *= this.fric;
                this.fricR *= this.fric;
            }
            this.tmpV.scale(this.fricF * 0.5, Forward);
            Tn.add(this.tmpV);
            this.tmpV.scale(this.fricR * 0.5, Right);
            Tn.add(this.tmpV);
        } else {
            Tn.scale(d_39_, Normal);
            this.fric = -1000.0 * NormalVPrj;
            if (this.fric > 4000.0) this.fric = 4000.0;
            if (this.fric < -4000.0) this.fric = -4000.0;
            this.tmpV.scale(this.fric, Normal);
            Tn.add(this.tmpV);
            this.fricF = -500.0 * ForwardVPrj;
            this.fricR = -800.0 * RightVPrj;
            this.fric = Math.sqrt(this.fricF * this.fricF + this.fricR * this.fricR);
            if (this.fric > 50000.0) {
                this.fric = 50000.0 / this.fric;
                this.fricF *= this.fric;
                this.fricR *= this.fric;
            }
            this.tmpV.scale(this.fricF, Forward);
            Tn.add(this.tmpV);
            this.tmpV.scale(this.fricR, Right);
            Tn.add(this.tmpV);
            if (this.sinkFactor > 1.0F && !this.bIsSail) {
                this.sinkFactor -= 0.4F * Time.tickLenFs();
                if (this.sinkFactor < 1.0F) this.sinkFactor = 1.0F;
            }
        }
        if (this.bIsMaster && NormalVPrj < 0.0) {
            double d_41_ = (ForwardVPrj * ForwardVPrj + RightVPrj * RightVPrj) * 4.0E-4 + NormalVPrj * NormalVPrj * 0.0011111111111111111;
            if (d_41_ > 1.0) this.landHit(i, (float) d_41_);
        }
        return true;
    }

    private boolean testPropellorCollision(int i) {
        if (this.bIsMaster && i >= 3 && i <= 6) {
            if (this.FM.actor == World.getPlayerAircraft() && !World.cur().diffCur.Realistic_Landings) return false;
            this.FM.setCapableOfTaxiing(false);
            switch (this.FM.Scheme) {
                default:
                    break;
                case 1:
                    ((Aircraft) this.FM.actor).hitProp(0, 0, Engine.actorLand());
                    break;
                case 2:
                case 3:
                    if (i < 5) ((Aircraft) this.FM.actor).hitProp(0, 0, Engine.actorLand());
                    else((Aircraft) this.FM.actor).hitProp(1, 0, Engine.actorLand());
                    break;
                case 4:
                case 5:
                    ((Aircraft) this.FM.actor).hitProp(i - 3, 0, Engine.actorLand());
                    break;
                case 6:
                    switch (i) {
                        case 3:
                            ((Aircraft) this.FM.actor).hitProp(0, 0, Engine.actorLand());
                            break;
                        case 4:
                        case 5:
                            ((Aircraft) this.FM.actor).hitProp(1, 0, Engine.actorLand());
                            break;
                        case 6:
                            ((Aircraft) this.FM.actor).hitProp(2, 0, Engine.actorLand());
                            break;
                    }
            }
            return false;
        }
        return true;
    }

    private void landHit(int i, double d) {
        if (!(this.FM.Vrel.length() < 13.0) && this.pnti[i] >= 0 && (this.FM.actor != World.getPlayerAircraft() || World.cur().diffCur.Realistic_Landings)) {
            ActorHMesh actorhmesh = (ActorHMesh) this.FM.actor;
            if (Actor.isValid(actorhmesh)) {
                Mesh mesh = actorhmesh.mesh();
                long l = Time.tick();
                String string = actorhmesh.findHook(mesh.hookName(this.pnti[i])).chunkName();
                if (string.compareTo("CF_D0") == 0) {
                    if (d > 2.0) MsgCollision.post(l, actorhmesh, Engine.actorLand(), string, "Body");
                } else if (string.compareTo("Tail1_D0") == 0) {
                    if (d > 1.3) MsgCollision.post(l, actorhmesh, Engine.actorLand(), string, "Body");
                } else if (this.FM.actor instanceof Scheme1 && string.compareTo("Engine1_D0") == 0) {
                    MsgCollision.post(l, actorhmesh, Engine.actorLand(), string, "Body");
                    if (d > 5.0) MsgCollision.post(l, actorhmesh, Engine.actorLand(), "CF_D0", "Body");
                } else MsgCollision.post(l, actorhmesh, Engine.actorLand(), string, "Body");
            }
        }
    }

    public void hitLeftGear() {
        this.lgear = false;
        this.FM.brakeShoe = false;
    }

    public void hitRightGear() {
        this.rgear = false;
        this.FM.brakeShoe = false;
    }

    public void hitCentreGear() {
        this.cgear = false;
        this.FM.brakeShoe = false;
    }

    public boolean isAnyDamaged() {
        return !this.lgear || !this.rgear || !this.cgear;
    }

    private void drawEffects() {
        boolean bool = this.FM.isTick(16, 0);
        for (int i = 0; i < 3; i++)
            if (this.bIsSail && bool && this.isWater && clp[i] && this.FM.getSpeedKMH() > 10.0F) {
                if (this.clpGearEff[i][0] == null) {
                    this.clpGearEff[i][0] = Eff3DActor.New(this.FM.actor, null, new Loc(new Point3d(Pnt[i]), new Orient(0.0F, 0.0F, 0.0F)), 1.0F, "3DO/Effects/Tracers/ShipTrail/WakeSmaller.eff", -1.0F);
                    this.clpGearEff[i][1] = Eff3DActor.New(this.FM.actor, null, new Loc(new Point3d(Pnt[i]), new Orient(0.0F, 0.0F, 0.0F)), 1.0F, "3DO/Effects/Tracers/ShipTrail/WaveSmaller.eff", -1.0F);
                }
            } else if (bool && this.clpGearEff[i][0] != null) {
                Eff3DActor.finish(this.clpGearEff[i][0]);
                Eff3DActor.finish(this.clpGearEff[i][1]);
                this.clpGearEff[i][0] = null;
                this.clpGearEff[i][1] = null;
            }
        for (int i = 0; i < this.pnti.length; i++)
            if (clp[i] && this.FM.Vrel.length() > 16.66666667 && !this.isUnderDeck()) {
                if (this.clpEff[i] == null) {
                    if (this.isWater) this.effectName = "EFFECTS/Smokes/SmokeAirSplat.eff";
                    else if (World.cur().camouflage == 1) this.effectName = "EFFECTS/Smokes/SmokeAirTouchW.eff";
                    else this.effectName = "EFFECTS/Smokes/SmokeAirTouch.eff";
                    this.clpEff[i] = Eff3DActor.New(this.FM.actor, null, new Loc(new Point3d(Pnt[i]), new Orient(0.0F, 90.0F, 0.0F)), 1.0F, this.effectName, -1.0F);
                }
            } else if (bool && this.clpEff[i] != null) {
                Eff3DActor.finish(this.clpEff[i]);
                this.clpEff[i] = null;
            }
        if (this.FM.EI.getNum() > 0) {
            int i = 0;
            for (/**/; i < this.FM.EI.getNum(); i++) {
                this.FM.actor.pos.getAbs(Aircraft.tmpLoc1);
                Pn.set(this.FM.EI.engines[i].getPropPos());
                Aircraft.tmpLoc1.transform(Pn, PnT);
                float f = (float) (PnT.z - Engine.cur.land.HQ(PnT.x, PnT.y));
                if (f < 16.2F && this.FM.EI.engines[i].getThrustOutput() > 0.5F) {
                    Pn.x -= f * Aircraft.cvt(this.FM.Or.getTangage(), -30.0F, 30.0F, 8.0F, 2.0F);
                    Aircraft.tmpLoc1.transform(Pn, PnT);
                    PnT.z = Engine.cur.land.HQ(PnT.x, PnT.y);
                    if (this.clpEngineEff[i][0] == null) {
                        Aircraft.tmpLoc1.transformInv(PnT);
                        if (this.isWater) {
                            this.clpEngineEff[i][0] = Eff3DActor.New(this.FM.actor, null, new Loc(PnT), 1.0F, "3DO/Effects/Aircraft/GrayGroundDust2.eff", -1.0F);
                            this.clpEngineEff[i][1] = Eff3DActor.New(new Loc(PnT), 1.0F, "3DO/Effects/Aircraft/WhiteEngineWaveTSPD.eff", -1.0F);
                        } else this.clpEngineEff[i][0] = Eff3DActor.New(this.FM.actor, null, new Loc(PnT), 1.0F, "3DO/Effects/Aircraft/GrayGroundDust" + (World.cur().camouflage == 1 ? "2" : "1") + ".eff", -1.0F);
                    } else {
                        if (this.isWater) {
                            if (this.clpEngineEff[i][1] == null) {
                                Eff3DActor.finish(this.clpEngineEff[i][0]);
                                this.clpEngineEff[i][0] = null;
                                continue;
                            }
                        } else if (this.clpEngineEff[i][1] != null) {
                            Eff3DActor.finish(this.clpEngineEff[i][0]);
                            this.clpEngineEff[i][0] = null;
                            Eff3DActor.finish(this.clpEngineEff[i][1]);
                            this.clpEngineEff[i][1] = null;
                            continue;
                        }
                        Aircraft.tmpOr.set(this.FM.Or.getAzimut() + 180.0F, 0.0F, 0.0F);
                        this.clpEngineEff[i][0].pos.setAbs(PnT);
                        this.clpEngineEff[i][0].pos.setAbs(Aircraft.tmpOr);
                        this.clpEngineEff[i][0].pos.resetAsBase();
                        if (this.clpEngineEff[i][1] != null) {
                            PnT.z = 0.0;
                            this.clpEngineEff[i][1].pos.setAbs(PnT);
                        }
                    }
                } else {
                    if (this.clpEngineEff[i][0] != null) {
                        Eff3DActor.finish(this.clpEngineEff[i][0]);
                        this.clpEngineEff[i][0] = null;
                    }
                    if (this.clpEngineEff[i][1] != null) {
                        Eff3DActor.finish(this.clpEngineEff[i][1]);
                        this.clpEngineEff[i][1] = null;
                    }
                }
            }
        }
    }

    private void turnOffEffects() {
        if (this.FM.isTick(69, 0)) {
            for (int i = 0; i < this.pnti.length; i++)
                if (this.clpEff[i] != null) {
                    Eff3DActor.finish(this.clpEff[i]);
                    this.clpEff[i] = null;
                }
            for (int i = 0; i < this.FM.EI.getNum(); i++) {
                if (this.clpEngineEff[i][0] != null) {
                    Eff3DActor.finish(this.clpEngineEff[i][0]);
                    this.clpEngineEff[i][0] = null;
                }
                if (this.clpEngineEff[i][1] != null) {
                    Eff3DActor.finish(this.clpEngineEff[i][1]);
                    this.clpEngineEff[i][1] = null;
                }
            }
        }
    }

    private void processingCollisionEffect() {
        if (this.canDoEffect) {
            this.Vnorm = this.FM.Vwld.dot(Normal);
            if (this.FM.actor == World.getPlayerAircraft() && World.cur().diffCur.Realistic_Landings && this.Vnorm < -20.0 && World.Rnd().nextFloat() < 0.02) {
                this.canDoEffect = false;
                int i = 20 + (int) (30.0F * World.Rnd().nextFloat());
                if (this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0] != null && this.FM.CT.Weapons[3][0].countBullets() != 0) i = 0;
                if (((Aircraft) this.FM.actor).isEnablePostEndAction(i)) {
                    Eff3DActor eff3dactor = null;
                    if (i > 0) {
                        Eff3DActor.New(this.FM.actor, null, new Loc(new Point3d(0.0, 0.0, 0.0), new Orient(0.0F, 90.0F, 0.0F)), 1.0F, "3DO/Effects/Aircraft/FireGND.eff", i);
                        eff3dactor = Eff3DActor.New(this.FM.actor, null, new Loc(new Point3d(0.0, 0.0, 0.0), new Orient(0.0F, 90.0F, 0.0F)), 1.0F, "3DO/Effects/Aircraft/BlackHeavyGND.eff", i + 10);
                        ((NetAircraft) this.FM.actor).sfxSmokeState(0, 0, true);
                    }
                    ((Aircraft) this.FM.actor).postEndAction(i, Engine.actorLand(), 2, eff3dactor);
                }
            }
        }
    }

    public void load(SectFile sectfile) {
        this.bIsSail = sectfile.get("Aircraft", "Seaplane", 0) != 0;
        this.sinkFactor = sectfile.get("Gear", "SinkFactor", 1.0F);
        this.springsStiffness = sectfile.get("Gear", "SpringsStiffness", 1.0F);
        this.tailStiffness = sectfile.get("Gear", "TailStiffness", 0.6F);
     // TODO: +++ Shock Absorber backport from EngineMod 2.8 +++
        shockAbsorber = sectfile.get("Gear", "ShockAbsorber", 0F, -1F, 1F);
     // TODO: --- Shock Absorber backport from EngineMod 2.8 ---
        // TODO: +++ By SAS~Storebror: Picked out of nowhere, undocumented changes found in BAT
        this.bHeavyAC = (sectfile.get("Gear", "HeavyAC", 0) != 0);
        // ------------------------------------------------------------------------------------
        if (sectfile.get("Gear", "FromIni", 0) == 1) {
            this.H = sectfile.get("Gear", "H", 2.0F);
            this.Pitch = sectfile.get("Gear", "Pitch", 10.0F);
        } else this.H = this.Pitch = 0.0F;
        String string = sectfile.get("Gear", "WaterClipList", "-");
        if (!string.startsWith("-")) {
            this.waterList = new int[3 + string.length() / 2];
            this.waterList[0] = 0;
            this.waterList[1] = 1;
            this.waterList[2] = 2;
            for (int i = 0; i < this.waterList.length - 3; i++) {
                this.waterList[3 + i] = 10 * (string.charAt(i + i) - 48) + 1 * (string.charAt(i + i + 1) - 48);
                this.waterList[3 + i] += 3;
            }
        }
    }

    public float getLandingState() {
        if (!this.onGround) return 0.0F;
        float f = 0.4F + ((float) this.roughness - 0.2F) * 0.5F;
        if (f > 1.0F) f = 1.0F;
        return f;
    }

    public double plateFriction(FlightModel flightmodel) {
        // TODO: Added by |ZUTI|: has to be called here because this is the point
        // that actually refreshes RRR timers. As as long as we are on the ground, this
        // is refreshing.
        // -------------------------------------
        ZutiSupportMethods_FM.resetRRRTimers();
        ZutiSupportMethods_FM.updateDeckTimer();
        ZutiSupportMethods_FM.updateVulnerabilityTimer();
        // -------------------------------------

        Actor actor = flightmodel.actor;
        if (this.bUnderDeck) return 0.0;
        if (!Actor.isValid(actor)) return 0.2;
        if (!World.cur().diffCur.Realistic_Landings) return 0.2;
        float f = 200.0F;
        actor.pos.getAbs(corn);
        bPlateExist = false;
        bPlateGround = false;
        Engine.drawEnv().getFiltered((AbstractCollection) null, corn.x - f, corn.y - f, corn.x + f, corn.y + f, 1, plateFilter);
        if (bPlateExist) {
            if (bPlateGround) return 0.8;
            return 0.0;
        }
        int i = Engine.cur.land.HQ_RoadTypeHere(flightmodel.Loc.x, flightmodel.Loc.y);
        switch (i) {
            case 1:
                return 0.8;
            case 2:
                return 0.0;
            case 3:
                return 5.0;
            default:
                // TODO: Edited by |ZUTI|: default value, means nothing is on this point. Check if the point is in defined airfields areas on the map...
                // World.land().config
                // ------------------------------------------------------------
                if (this.zutiCurrentZAP != null) {
                    double result = this.zutiCurrentZAP.isInZAPArea(flightmodel.Loc.x, flightmodel.Loc.y);
                    if (result > -1) {
                        if (this.zutiHasPlaneSkisOnWinterCamo && result > 2.4) return 2.4;

                        return result;
                    }
                }

                List airports = ZutiSupportMethods_Engine.AIRFIELDS;
                int size = airports.size();
                for (int z = 0; z < size; z++) {
                    ZutiAirfieldPoint point = (ZutiAirfieldPoint) airports.get(z);
                    {
                        double result = point.isInZAPArea(flightmodel.Loc.x, flightmodel.Loc.y);
                        if (result > -1) {
                            this.zutiCurrentZAP = point;

                            if (this.zutiHasPlaneSkisOnWinterCamo) return 2.4;

                            return result;
                        }
                    }
                }
                // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                return 3.8;
        }
    }

    private String s(int i) {
        return i < 10 ? "0" + i : "" + i;
    }

    private boolean bInWaterList(int i) {
        if (this.waterList != null) for (int i_42_ = 0; i_42_ < this.waterList.length; i_42_++)
            if (this.waterList[i_42_] == i) return true;
        return false;
    }

    public void zutiCheckPlaneForSkisAndWinterCamo(String string) {
        for (int i = 0; i < ZUTI_SKIS_AC_CLASSES.length; i++)
            if (string.endsWith(ZUTI_SKIS_AC_CLASSES[i])) {
                if ("WINTER".equals(Engine.land().config.camouflage)) this.zutiHasPlaneSkisOnWinterCamo = true;
                else this.zutiHasPlaneSkisOnWinterCamo = false;
                return;
            }
        this.zutiHasPlaneSkisOnWinterCamo = false;
    }

    static {
        for (int i = 0; i < Pnt.length; i++)
            Pnt[i] = new Point3f();
        Normal = new Vector3d();
        Forward = new Vector3d();
        Right = new Vector3d();
        nwForward = new Vector3d();
        nwRight = new Vector3d();
        Vnf = new Vector3d();
        Fd = new Vector3d();
        Fx = new Vector3d();
        Vship = new Vector3d();
        Fv = new Vector3d();
        Tn = new Vector3d();
        Pn = new Point3d();
        PnT = new Point3d();
        Pship = new Point3d();
        Vs = new Vector3d();
        M4 = new Matrix4d();
        plateFilter = new PlateFilter();
        corn = new Point3d();
        corn1 = new Point3d();
        L = new Loc();
        plateBox = new float[6];
        bPlateExist = false;
        bPlateGround = false;
        ZUTI_SKIS_AC_CLASSES = new String[] { "DXXI_SARJA3_EARLY", "DXXI_SARJA3_LATE", "DXXI_SARJA3_SARVANTO", "DXXI_SARJA4", "R_5_SKIS", "BLENHEIM1", "BLENHEIM4", "GLADIATOR1", "GLADIATOR1J8A", "GLADIATOR2", "I_15BIS_SKIS", "I_16TYPE5_SKIS",
                "I_16TYPE6_SKIS" };
    }

    // TODO: CTO Mod
    // ----------------------------------------
    public boolean setCatapultOffset(BigshipGeneric bigshipgeneric) {
        printDebugMessage(DEBUG_DETAILED, "setCatapultOffset(" + bigshipgeneric.getClass().getName() + ")"); // TODO: Graf Zeppelin Debug
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        this.dCatapultOffsetX = 0.0D;
        this.dCatapultOffsetY = 0.0D;
        this.dCatapultOffsetX2 = 0.0D;
        this.dCatapultOffsetY2 = 0.0D;
        this.bCatapultAI = false;
        if (bigshipgeneric instanceof Ship.USSKitkunBayCVE71 || bigshipgeneric instanceof Ship.USSCasablancaCVE55 || bigshipgeneric instanceof Ship.USSShamrockBayCVE84) {
            this.dCatapultOffsetX = 4.2D;
            this.dCatapultOffsetY = -64D;
            flag2 = true;
            if (!this.bCatapultAllowAI || !this.bCatapultAI_CVE) this.bCatapultAI = false;
            else this.bCatapultAI = true;
        } else if (bigshipgeneric instanceof Ship.USSEssexCV9 || bigshipgeneric instanceof Ship.USSIntrepidCV11) {
            this.dCatapultOffsetX = 9D;
            this.dCatapultOffsetY = -130D;
            this.dCatapultOffsetX2 = 27.6D;
            this.dCatapultOffsetY2 = -137.5D;
            flag2 = true;
            if (!this.bCatapultAllowAI || !this.bCatapultAI_EssexClass) this.bCatapultAI = false;
            else this.bCatapultAI = true;
        } else if (bigshipgeneric instanceof Ship.HMSIllustriousCV) {
            this.dCatapultOffsetX = 7D;
            this.dCatapultOffsetY = -68D;
            flag2 = true;
            if (!this.bCatapultAllowAI || !this.bCatapultAI_Illustrious) this.bCatapultAI = false;
            else this.bCatapultAI = true;
        }
        try {
            Class.forName("com.maddox.il2.objects.ships.Ship$Carrier1"); // TODO: Patch Pack 107, fix missing catapult on german carriers
//            Class.forName("com.maddox.il2.objects.ships.Ship$Carrier0");
            flag = true;
        } catch (ClassNotFoundException classnotfoundexception) {}
        try {
            Class.forName("com.maddox.il2.objects.ships.Ship$USSBelleauWoodCVL24");
            flag1 = true;
        } catch (ClassNotFoundException classnotfoundexception1) {}
        printDebugMessage(DEBUG_DETAILED, "setCatapultOffset(" + bigshipgeneric.getClass().getName() + ") flag2 = " + flag2 + ", flag = " + flag); // TODO: Graf Zeppelin Debug
        if (!flag2 && flag) if (bigshipgeneric instanceof Ship.GrafZeppelin || bigshipgeneric instanceof Ship.PeterStrasser) {
            printDebugMessage(DEBUG_NORMAL, "### GRAF ZEPPELIN / PETER STRASSER CATAPULT ENABLED!!! ###"); // TODO: Graf Zeppelin Debug
            this.dCatapultOffsetX = 4.8D;
            this.dCatapultOffsetY = -113.5D;
            this.dCatapultOffsetX2 = 19.1D;
            this.dCatapultOffsetY2 = -113.5D;
            flag2 = true;
            if (!this.bCatapultAllowAI || !this.bCatapultAI_GrafZep) this.bCatapultAI = false;
            else this.bCatapultAI = true;
        } else if (bigshipgeneric instanceof Ship.Carrier1) {
            this.dCatapultOffsetX = 4.2D;
            this.dCatapultOffsetY = -64D;
            flag2 = true;
            if (!this.bCatapultAllowAI || !this.bCatapultAI_CVE) this.bCatapultAI = false;
            else this.bCatapultAI = true;
        }
        if (!flag2 && flag1 && (bigshipgeneric instanceof Ship.USSSanJacintoCVL30 || bigshipgeneric instanceof Ship.USSBelleauWoodCVL24 || bigshipgeneric instanceof Ship.USSPrincetonCVL23)) {
            if (this.bStandardDeckCVL) {
                this.dCatapultOffsetX = 6D;
                this.dCatapultOffsetY = -52D;
                flag2 = true;
            } else {
                this.dCatapultOffsetX = 5D;
                this.dCatapultOffsetY = -28D;
                flag2 = true;
            }
            if (!this.bCatapultAllowAI || !this.bCatapultAI_CVL) this.bCatapultAI = false;
            else this.bCatapultAI = true;
        }
        return flag2;
    }

    public double getCatapultOffsetX() {
        return this.dCatapultOffsetX;
    }

    public double getCatapultOffsetY() {
        return this.dCatapultOffsetY;
    }

    public boolean getCatapultAI() {
        return this.bCatapultAI;
    }
    // ----------------------------------------

    // TODO: |ZUTI| Variables and Methods
    // ---------------------------------------------------------------------------------
    public ZutiAirfieldPoint zutiCurrentZAP = null;
    // ---------------------------------------------------------------------------------

    private static int       debugLevel     = Integer.MIN_VALUE;
    private static final int DEBUG_NONE     = 0;
    private static final int DEBUG_NORMAL   = 1;
    private static final int DEBUG_DETAILED = 2;
    private static final int DEBUG_DEFAULT  = DEBUG_NONE;

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_GEAR", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(int minLogLevel, String theMessage) {
        if (curDebugLevel() < minLogLevel) return;
        System.out.println(theMessage);
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void setSteeringAngle(float f) {
        this.steerAngle = f;
    }
    // TODO: --- TD AI code backport from 4.13 ---

}
