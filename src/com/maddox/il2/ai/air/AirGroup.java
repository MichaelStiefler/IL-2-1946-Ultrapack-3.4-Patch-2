/*
 * 4.13.1 Class
 * Backport by SAS~Storebror for TD AI code implementation
 */

package com.maddox.il2.ai.air;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector2d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Chief;
import com.maddox.il2.ai.Formation;
import com.maddox.il2.ai.Squadron;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.Way;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ground.Aim;
import com.maddox.il2.ai.ground.NearestEnemies;
import com.maddox.il2.ai.ground.Prey;
import com.maddox.il2.ai.ground.TgtShip;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiAcWithReleasedOrdinance;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.D3A;
import com.maddox.il2.objects.air.F4U;
import com.maddox.il2.objects.air.G4M2E;
import com.maddox.il2.objects.air.I_16TYPE24DRONE;
import com.maddox.il2.objects.air.JU_87;
import com.maddox.il2.objects.air.MXY_7;
import com.maddox.il2.objects.air.Mig_17;
import com.maddox.il2.objects.air.R_5xyz;
import com.maddox.il2.objects.air.Scheme4;
import com.maddox.il2.objects.air.TB_3_4M_34R_SPB;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeDiveBomber;
import com.maddox.il2.objects.air.TypeDockable;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeGlider;
import com.maddox.il2.objects.air.TypeGuidedBombCarrier;
import com.maddox.il2.objects.air.TypeHasToKG;
import com.maddox.il2.objects.air.TypeStormovik;
import com.maddox.il2.objects.bridges.Bridge;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.BombGunNull;
import com.maddox.il2.objects.weapons.BombGunPara;
import com.maddox.il2.objects.weapons.BombGunParafrag8;
import com.maddox.il2.objects.weapons.ParaTorpedoGun;
import com.maddox.il2.objects.weapons.RocketGunFritzX;
import com.maddox.il2.objects.weapons.RocketGunHS_293;
import com.maddox.il2.objects.weapons.TorpedoGun;
import com.maddox.rts.Time;

public class AirGroup {
    public int                nOfAirc;
    public Aircraft[]         airc;
    private Squadron          sq;
    public Way                w;
    public Vector3d           Pos;
    public AirGroupList[]     enemies;
    private AirGroupList[]    friends;
    public AirGroup           clientGroup;
    public AirGroup           targetGroup;
    public AirGroup           leaderGroup;
    public AirGroup           rejoinGroup;
    private int               grAttached;
    public int                gTargetPreference;
    public int                aTargetPreference;
    private boolean           enemyFighters;
    private boolean           gTargWasFound;
    private boolean           gTargDestroyed;
    private int               gTargMode;
    private Actor             gTargActor;
    private Point3d           gTargPoint;
    private float             gTargRadius;
    private boolean           aTargWasFound;
    private boolean           aTargDestroyed;
    private boolean           weWereInGAttack;
    private boolean           weWereInAttack;
    public byte               formationType;
    private byte              oldFType;
    private float             oldFScale;
    private boolean           oldFInterp;
    private boolean           fInterpolation;
    private int               oldEnemyNum;
    public int                timeOutForTaskSwitch;
    public int                grTask;
    private static Point3d    point3dWP;
    private static Point3d    point3dNewWP;
    private static Point3f    pfcf;
    private int               grSA;
    private int               grSubSkill;
    private int               grCourage;
    private int               grSkill;
    public boolean            bSee;
    public boolean            bInitAttack;
    public int                outNum;
    public static final byte  W_FREE_HUNT          = 0;
    public static final byte  W_COVER              = 1;
    public static final byte  W_CLOUDS             = 2;
    public static final byte  W_SECTION_BRACKET    = 3;
    public static final byte  W_EVADE              = 4;
    public static final int   FLY_WAYPOINT         = 1;
    public static final int   DEFENDING            = 2;
    public static final int   ATTACK_AIR           = 3;
    public static final int   ATTACK_GROUND        = 4;
    private static final int  TAKEOFF              = 5;
    private static final int  LANDING              = 6;
    public static final int   TAKEOFF_AND_HANG_ON  = 7;
    private static final int  ARTILLERY_SPOTTING   = 8;
    private static final int  GT_MODE_NONE         = 0;
    private static final int  GT_MODE_CHIEF        = 1;
    private static final int  GT_MODE_AROUND_POINT = 2;
    private static final byte TRIANGLE             = 0;
    private static final byte SQUARE               = 1;
    private static final byte PENTAGON             = 2;
    private static final byte HEXAGON              = 3;
    private static final byte RANDOM               = 4;
    private int               underAAA;
    private long              timeNextAAACheck;
    public int                bracketSideGr;
    private HashSet           aaaShooters;
    public float              aaaNum;
    private static String[]   GTList;
    private static float[]    xtriangle;
    private static float[]    ytriangle;
    private static float[]    xr;
    private static float[]    yr;
    private static float[]    xh;
    private static float[]    yh;
    private static float[]    xp;
    private static float[]    yp;
    private int               capFirstWP;
    private int               capLastWP;
    private int               capCycles;
    private int               capAltitude;
    private boolean           capTimerSet;
    private static Vector3d   tmpV;
    private static Vector3d   tmpV1;
    private static Point3d    tmpP;
    private static Point3d    tmpP3d;
    private static Vector2d   P1P2vector;
    private static Vector2d   norm1;
    private static Vector2d   myPoint;
    private HashMap           convPoint;

    public native int[] groupLove(float p0, float p1, float p2, float p3, float p4, float p5, float p6, int p7, int p8, int p9, int p10);

    public void setUnderAAA(boolean b, Aim aim) {
        if (this.aaaNum < 30.0f) {
            this.aaaShooters.add(aim);
            this.aaaNum = this.aaaShooters.size();
        }
    }

    public float getAaaNum() {
        return this.aaaNum;
    }

    boolean setCAP(int capShape, int waypointType, int n2, int n3, float nextFloat, float n4, boolean flag) {
//        System.out.println("setCAP(" + capShape + ", " + waypointType + ", " + n2 + ", " + n3 + ", " + nextFloat + ", " + n4 + ", " + flag + ")");
        Maneuver maneuver = (Maneuver) this.airc[n2].FM;
        maneuver.AP.way.curr().getP(AirGroup.point3dWP);
        this.capAltitude = (int) maneuver.AP.way.curr().z();
        int curWP = maneuver.AP.way.Cur();
        int capShapeSize = capShape + 3;
        float baseSize = maneuver.AP.way.curr().baseSize;
        float capStartOrient = (maneuver.AP.way.curr().orient * (float) Math.PI) / 180.0f;
        float altDiff = maneuver.AP.way.curr().altDifference;
//        System.out.println("capAltitude=" + this.capAltitude + ", curWP=" + curWP + ", n5=" + capShapeSize + ", baseSize=" + baseSize + ", capStartOrient=" + capStartOrient + ", altDiff=" + altDiff);
        if (capShape == 4) {
            if (!flag) {
                capShape = World.Rnd().nextInt(0, 3); // Attention, IL-2's "nextInt" implementation _includes_ the upper boundary!
                n3 = capShape + 3;
//                n3 = World.Rnd().nextInt(0, 4) + 3;
                nextFloat = World.Rnd().nextFloat(0.0f, 360.0f);
                n4 = (int) World.Rnd().nextFloat(baseSize / 2.0f, baseSize);
            }
            capShapeSize = n3;
            capStartOrient = nextFloat;
            baseSize = (int) n4;
//            System.out.println("n3=" + n3 + ", nextFloat=" + nextFloat + ", n4=" + n4 + ", n5=" + capShapeSize + ", n6=" + baseSize + ", n7=" + capStartOrient);
        }
        this.capFirstWP = curWP + 1;
        this.capLastWP = this.capFirstWP + capShapeSize;
        this.capCycles = maneuver.AP.way.curr().cycles;
//        System.out.println("capFirstWP=" + this.capFirstWP + ", capLastWP=" + this.capLastWP + ", capCycles=" + this.capCycles);
        for (int i = capShapeSize; i >= 0; --i) {
            this.w.curr().waypointType = 0;
            WayPoint wayPoint = new WayPoint();
//            System.out.println("i=" + i + ", n=" + capShape);
            switch (capShape) {
                case 1: {
                    AirGroup.point3dNewWP.set(AirGroup.rotCoord(capStartOrient, AirGroup.xr[i], AirGroup.yr[i], true), AirGroup.rotCoord(capStartOrient, AirGroup.xr[i], AirGroup.yr[i], false), 0.0);
                    break;
                }
                case 2: {
                    AirGroup.point3dNewWP.set(AirGroup.rotCoord(capStartOrient, AirGroup.xp[i], AirGroup.yp[i], true), AirGroup.rotCoord(capStartOrient, AirGroup.xp[i], AirGroup.yp[i], false), 0.0);
                    break;
                }
                case 3: {
                    AirGroup.point3dNewWP.set(AirGroup.rotCoord(capStartOrient, AirGroup.xh[i], AirGroup.yh[i], true), AirGroup.rotCoord(capStartOrient, AirGroup.xh[i], AirGroup.yh[i], false), 0.0);
                    break;
                }
                case 0:
                default: {
                    AirGroup.point3dNewWP.set(AirGroup.rotCoord(capStartOrient, AirGroup.xtriangle[i], AirGroup.ytriangle[i], true), AirGroup.rotCoord(capStartOrient, AirGroup.xtriangle[i], AirGroup.ytriangle[i], false), 0.0);
                    break;
                }
            }
            wayPoint.set(maneuver.AP.way.curr().getV());
            wayPoint.setRadioSilence(maneuver.AP.way.curr().isRadioSilence());
            AirGroup.point3dNewWP.scale(baseSize);
            AirGroup.point3dNewWP.add(AirGroup.point3dWP);
            AirGroup.pfcf.set(AirGroup.point3dNewWP);
            AirGroup.pfcf.add(0.0f, 0.0f, (altDiff / capShapeSize) * i);
            if (AirGroup.pfcf.z < 100.0f) {
                AirGroup.pfcf.z = 100.0f;
            }
            wayPoint.set(AirGroup.pfcf);
            wayPoint.waypointType = waypointType;
            wayPoint.targetTrigger = maneuver.AP.way.curr().targetTrigger;
            for (int j = 0; j < this.nOfAirc; ++j) {
                this.airc[j].netSendNewWayPoint(wayPoint, curWP);
            }
            this.w.insert(curWP + 1, wayPoint);
        }
        this.w.setCur(curWP);
        this.rejoinToGroup(this);
        for (int k = 0; k < this.nOfAirc; ++k) {
            this.airc[k].FM.AP.way.curr().waypointType = 0;
        }
        return true;
    }

    private static float rotCoord(float n, float n2, float n3, boolean b) {
        return b ? (n2 * (float) Math.cos(n)) - (n3 * (float) Math.sin(n)) : (n2 * (float) Math.sin(n)) + (n3 * (float) Math.cos(n));
    }

    public String grTaskName() {
        return AirGroup.GTList[this.grTask];
    }

    public AirGroup() {
        this.Pos = new Vector3d();
        this.bSee = false;
        this.bInitAttack = true;
        this.outNum = 0;
        this.underAAA = -1;
        this.bracketSideGr = 1;
        this.aaaShooters = new HashSet();
        this.capFirstWP = 0;
        this.capLastWP = 0;
        this.capCycles = 0;
        this.capAltitude = 500;
        this.capTimerSet = false;
        this.convPoint = new HashMap();
        this.initVars();
    }

    public AirGroup(Squadron sq, Way w) {
        this.Pos = new Vector3d();
        this.bSee = false;
        this.bInitAttack = true;
        this.outNum = 0;
        this.underAAA = -1;
        this.bracketSideGr = 1;
        this.aaaShooters = new HashSet();
        this.capFirstWP = 0;
        this.capLastWP = 0;
        this.capCycles = 0;
        this.capAltitude = 500;
        this.capTimerSet = false;
        this.convPoint = new HashMap();
        this.initVars();
        this.sq = sq;
        this.w = w;
    }

    public AirGroup(AirGroup rejoinGroup) {
        this.Pos = new Vector3d();
        this.bSee = false;
        this.bInitAttack = true;
        this.outNum = 0;
        this.underAAA = -1;
        this.bracketSideGr = 1;
        this.aaaShooters = new HashSet();
        this.capFirstWP = 0;
        this.capLastWP = 0;
        this.capCycles = 0;
        this.capAltitude = 500;
        this.capTimerSet = false;
        this.convPoint = new HashMap();
        this.initVars();
        if (rejoinGroup == null) {
            return;
        }
        this.sq = rejoinGroup.sq;
        if (rejoinGroup.w != null) {
            (this.w = new Way(rejoinGroup.w)).setCur(rejoinGroup.w.Cur());
        } else {
            (this.w = new Way()).add(new WayPoint((float) rejoinGroup.Pos.x, (float) rejoinGroup.Pos.y, (float) rejoinGroup.Pos.z));
        }
        this.Pos.set(rejoinGroup.Pos);
        for (int length = AirGroupList.length(rejoinGroup.enemies[0]), i = 0; i < length; ++i) {
            AirGroupList.addAirGroup(this.enemies, 0, AirGroupList.getGroup(rejoinGroup.enemies[0], i));
        }
        for (int length2 = AirGroupList.length(rejoinGroup.friends[0]), j = 0; j < length2; ++j) {
            AirGroupList.addAirGroup(this.friends, 0, AirGroupList.getGroup(rejoinGroup.friends[0], j));
        }
        this.rejoinGroup = rejoinGroup;
        this.gTargetPreference = rejoinGroup.gTargetPreference;
        this.aTargetPreference = rejoinGroup.aTargetPreference;
        this.enemyFighters = rejoinGroup.enemyFighters;
        this.oldEnemyNum = rejoinGroup.oldEnemyNum;
        if (AirGroupList.groupInList(War.Groups[0], rejoinGroup)) {
            AirGroupList.addAirGroup(War.Groups, 0, this);
        } else {
            AirGroupList.addAirGroup(War.Groups, 1, this);
        }
    }

    void initVars() {
        this.nOfAirc = 0;
        this.airc = new Aircraft[16];
        this.sq = null;
        this.w = null;
        this.Pos = new Vector3d(0.0, 0.0, 0.0);
        this.enemies = new AirGroupList[1];
        this.friends = new AirGroupList[1];
        this.clientGroup = null;
        this.targetGroup = null;
        this.leaderGroup = null;
        this.rejoinGroup = null;
        this.grAttached = 0;
        this.gTargetPreference = 0;
        this.aTargetPreference = 9;
        this.enemyFighters = false;
        this.gTargWasFound = false;
        this.gTargDestroyed = false;
        this.gTargMode = 0;
        this.gTargActor = null;
        this.gTargPoint = new Point3d();
        this.gTargRadius = 0.0f;
        this.aTargWasFound = false;
        this.aTargDestroyed = false;
        this.weWereInGAttack = false;
        this.weWereInAttack = false;
        this.formationType = -1;
        this.fInterpolation = false;
        this.oldFType = -1;
        this.oldFScale = 0.0f;
        this.oldFInterp = false;
        this.oldEnemyNum = 0;
        this.timeOutForTaskSwitch = 0;
        this.grTask = 1;
        this.bracketSideGr = World.Rnd().nextBoolean() ? -1 : 1;
    }

    public void release() {
        for (int i = 0; i < this.nOfAirc; ++i) {
            if (this.airc[i] != null) {
                ((Maneuver) this.airc[i].FM).Group = null;
            }
            this.airc[i] = null;
        }
        this.nOfAirc = 0;
        this.sq = null;
        this.w = null;
        this.Pos = null;
        if (this.enemies[0] != null) {
            this.enemies[0].release();
        }
        if (this.friends[0] != null) {
            this.friends[0].release();
        }
        this.enemies = null;
        this.friends = null;
        this.clientGroup = null;
        this.targetGroup = null;
        this.leaderGroup = null;
        this.rejoinGroup = null;
        this.gTargPoint = null;
        this.convPoint.clear();
    }

    public void addAircraft(Aircraft aircraft) {
        if (this.nOfAirc >= 16) {
            System.out.print("Group > 16 in squadron " + this.sq.name());
            return;
        }
        int i;
        if (aircraft.getSquadron() == this.sq) {
            for (i = 0; i < this.nOfAirc; ++i) {
                if ((this.airc[i].getSquadron() != this.sq) || (((this.airc[i].getWing().indexInSquadron() * 4) + this.airc[i].aircIndex()) > ((aircraft.getWing().indexInSquadron() * 4) + aircraft.aircIndex()))) {
                    break;
                }
            }
        } else {
            i = this.nOfAirc;
        }
        for (int j = this.nOfAirc - 1; j >= i; --j) {
            this.airc[j + 1] = this.airc[j];
        }
        this.airc[i] = aircraft;
        if (this.w != null) {
            (aircraft.FM.AP.way = new Way(this.w)).setCur(this.w.Cur());
        }
        ++this.nOfAirc;
        this.grSA += aircraft.FM.sight;
        if (this.grSA > 20) {
            this.grSA = 20;
        }
        this.grCourage += aircraft.FM.courage;
        if (this.grCourage > 20) {
            this.grCourage = 20;
        }
        this.grSubSkill += aircraft.FM.subSkill;
        if (this.grSubSkill > 60) {
            this.grSubSkill = 60;
        }
        this.grSkill += aircraft.FM.Skill;
        if (this.grSkill > 12) {
            this.grSkill = 12;
        }
        if (aircraft == World.getPlayerAircraft()) {
            aircraft.initPlayerTaxingWay();
        }
        if (aircraft.FM instanceof Maneuver) {
            ((Maneuver) aircraft.FM).Group = this;
        }
    }

    public void delAircraft(Aircraft aircraft) {
        for (int i = 0; i < this.nOfAirc; ++i) {
            if (aircraft == this.airc[i]) {
                ((Maneuver) this.airc[i].FM).Group = null;
                for (int j = i; j < (this.nOfAirc - 1); ++j) {
                    this.airc[j] = this.airc[j + 1];
                }
                --this.nOfAirc;
                this.grSA -= aircraft.FM.sight;
                this.grCourage -= aircraft.FM.courage;
                this.grSubSkill -= aircraft.FM.subSkill;
                this.grSkill -= aircraft.FM.Skill;
                break;
            }
        }
        if ((this.grTask == 1) || (this.grTask == 2)) {
            this.setTaskAndManeuver(0);
        }
    }

    public void changeAircraft(Aircraft aircraft, Aircraft aircraft2) {
        for (int i = 0; i < this.nOfAirc; ++i) {
            if (aircraft == this.airc[i]) {
                ((Maneuver) aircraft.FM).Group = null;
                ((Maneuver) aircraft2.FM).Group = this;
                ((Maneuver) aircraft2.FM).setBusy(false);
                this.airc[i] = aircraft2;
                return;
            }
        }
    }

    public void rejoinToGroup(AirGroup airGroup) {
        if (airGroup == null) {
            return;
        }
        for (int i = this.nOfAirc - 1; i >= 0; --i) {
            Aircraft aircraft = this.airc[i];
            this.delAircraft(aircraft);
            airGroup.addAircraft(aircraft);
        }
        this.rejoinGroup = null;
    }

    public void attachGroup(AirGroup leaderGroup) {
        if (leaderGroup == null) {
            return;
        }
        for (int i = 0; i < this.nOfAirc; ++i) {
            if (this.airc[i].FM instanceof Maneuver) {
                Maneuver maneuver = (Maneuver) this.airc[i].FM;
                if (!(maneuver instanceof RealFlightModel) || !((RealFlightModel) maneuver).isRealMode()) {
                    if ((maneuver.get_maneuver() == 26) || (maneuver.get_maneuver() == 64) || (maneuver.get_maneuver() == 102)) {
                        return;
                    }
                }
            }
        }
        this.w = null;
        (this.w = new Way(leaderGroup.w)).setCur(leaderGroup.w.Cur());
        for (int j = 0; j < this.nOfAirc; ++j) {
            this.airc[j].FM.AP.way = null;
            (this.airc[j].FM.AP.way = new Way(leaderGroup.w)).setCur(leaderGroup.w.Cur());
        }
        Formation.leaderOffset(this.airc[0].FM, this.formationType, this.airc[0].FM.Offset);
        this.leaderGroup = leaderGroup;
        AirGroup leaderGroup2 = this.leaderGroup;
        ++leaderGroup2.grAttached;
        this.grTask = 1;
        this.setFormationAndScale(leaderGroup.formationType, 1.0f, true);
    }

    void detachGroup(AirGroup airGroup) {
        if (airGroup == null) {
            return;
        }
        AirGroup leaderGroup = this.leaderGroup;
        --leaderGroup.grAttached;
        if (this.leaderGroup.grAttached < 0) {
            this.leaderGroup.grAttached = 0;
        }
        this.leaderGroup = null;
        this.grTask = 1;
        this.setTaskAndManeuver(0);
    }

    public int numInGroup(Aircraft aircraft) {
        for (int i = 0; i < this.nOfAirc; ++i) {
            if (aircraft == this.airc[i]) {
                return i;
            }
        }
        return -1;
    }

    public void setEnemyFighters() {
        int length = AirGroupList.length(this.enemies[0]);
        this.outNum = 0;
        this.enemyFighters = false;
        for (int i = 0; i < length; ++i) {
            AirGroup group = AirGroupList.getGroup(this.enemies[0], i);
            if ((group.nOfAirc > 0) && (group.airc[0] instanceof TypeFighter)) {
                this.enemyFighters = true;
                this.outNum += group.nOfAirc - this.nOfAirc;
                return;
            }
        }
    }

    public void setFormationAndScale(byte oldFType, float n, boolean b) {
//        System.out.println("setFormationAndScale(" + oldFType + ", " + n + ", " + b + ")");
        if ((this.oldFType == oldFType) && (this.oldFScale == n) && (this.oldFInterp == b)) {
            return;
        }
        this.fInterpolation = b;
        for (int i = 1; i < this.nOfAirc; ++i) {
            if (this.airc[i] instanceof TypeGlider) {
                return;
            }
            ((Maneuver) this.airc[i].FM).formationScale = n;
            Formation.gather(this.airc[i].FM, oldFType, AirGroup.tmpV);
            if (!b) {
                this.airc[i].FM.Offset.set(AirGroup.tmpV);
            }
            this.formationType = ((Maneuver) this.airc[i].FM).formationType;
        }
        if ((this.grTask == 1) || (this.grTask == 2)) {
            this.setTaskAndManeuver(0);
        }
        this.oldFType = oldFType;
        this.oldFScale = n;
        this.oldFInterp = b;
    }

    public void formationUpdate() {
        if (this.fInterpolation) {
            boolean b = true;
            for (int i = 1; i < this.nOfAirc; ++i) {
                if (Actor.isAlive(this.airc[i])) {
                    if (this.airc[i] instanceof TypeGlider) {
                        return;
                    }
                    Formation.gather(this.airc[i].FM, this.formationType, AirGroup.tmpV);
                    AirGroup.tmpV1.sub(AirGroup.tmpV, this.airc[i].FM.Offset);
                    float n = (float) AirGroup.tmpV1.length();
                    if (n != 0.0f) {
                        b = false;
                        if (n < 0.1f) {
                            this.airc[i].FM.Offset.set(AirGroup.tmpV);
                        } else {
                            double n2 = 4.0E-4 * AirGroup.tmpV1.length();
                            if (this.oldFScale > 0.0f) {
                                n2 *= 10.0f / this.oldFScale;
                            }
                            if (n2 > 1.5) {
                                n2 = 1.5;
                            }
                            AirGroup.tmpV1.normalize();
                            AirGroup.tmpV1.scale(n2);
                            this.airc[i].FM.Offset.add(AirGroup.tmpV1);
                        }
                    }
                }
            }
            if (b) {
                this.fInterpolation = false;
            }
            if ((this.grTask == 1) || (this.grTask == 2)) {
                this.setTaskAndManeuver(0);
            }
        }
    }

    public boolean inCorridor(Point3d point3d) {
        if (this.w == null) {
            return true;
        }
        float n = 25000.0f;
        if (this.w.curr().Action == 2) {
            n = 3000.0f;
        }
        int cur = this.w.Cur();
        if (cur == 0) {
            return true;
        }
        this.w.prev();
        AirGroup.tmpP = this.w.curr().getP();
        this.w.setCur(cur);
        AirGroup.tmpV.sub(this.w.curr().getP(), AirGroup.tmpP);
        AirGroup.P1P2vector.set(AirGroup.tmpV);
        float n2 = (float) AirGroup.P1P2vector.length();
        if (n2 > 1.0E-4f) {
            AirGroup.P1P2vector.scale(1.0f / n2);
        } else {
            AirGroup.P1P2vector.set(1.0, 0.0);
        }
        AirGroup.tmpV.sub(point3d, AirGroup.tmpP);
        AirGroup.myPoint.set(AirGroup.tmpV);
        if (AirGroup.P1P2vector.dot(AirGroup.myPoint) < -n) {
            return false;
        }
        AirGroup.norm1.set(-AirGroup.P1P2vector.y, AirGroup.P1P2vector.x);
        float n3 = (float) AirGroup.norm1.dot(AirGroup.myPoint);
        if ((n3 > n) || (n3 < -n)) {
            return false;
        }
        AirGroup.tmpV.sub(point3d, this.w.curr().getP());
        AirGroup.myPoint.set(AirGroup.tmpV);
        return AirGroup.P1P2vector.dot(AirGroup.myPoint) <= n;
    }

    public void setGroupTask(int grTask) {
        this.grTask = grTask;
        this.bracketSideGr = World.Rnd().nextBoolean() ? -1 : 1;
        if ((this.grTask == 1) || (this.grTask == 2) || (this.grTask == 8)) {
            this.setTaskAndManeuver(0);
        } else if ((this.grTask == 3) && this.bInitAttack) {
            this.setTaskAndManeuver(0);
            this.bInitAttack = false;
        } else {
            for (int i = 0; i < this.nOfAirc; ++i) {
                if (!((Maneuver) this.airc[i].FM).isBusy()) {
                    this.setTaskAndManeuver(i);
                }
            }
        }
    }

    public void dropBombs() {
        // TODO: Added by |ZUTI|: temp variablea
        // ------------------------
        String acName = null;
        ZutiAcWithReleasedOrdinance aircraft = null;
        // ------------------------

        for (int i = 0; i < this.nOfAirc; ++i) {
            if (!((Maneuver) this.airc[i].FM).isBusy()) {
                ((Maneuver) this.airc[i].FM).bombsOut = true;
                // TODO: Added by |ZUTI|: remember which AC dropped bombs for later synchronization over net
                // -----------------------------------------------------------
                acName = this.airc[i].name();
                aircraft = new ZutiAcWithReleasedOrdinance();
                aircraft.setAcName(acName);
                ZutiAcWithReleasedOrdinance.AC_THAT_RELEASED_BOMBS.put(acName, aircraft);
                // -----------------------------------------------------------
            }
        }
        if (this.friends[0] != null) {
            for (int length = AirGroupList.length(this.friends[0]), j = 0; j < length; ++j) {
                AirGroup group = AirGroupList.getGroup(this.friends[0], j);
                if ((group != null) && (group.leaderGroup == this)) {
                    group.dropBombs();
                }
            }
        }
    }

    Aircraft firstOkAirc(int n) {
        for (int i = 0; i < this.nOfAirc; ++i) {
            if ((n >= 0) && (n < this.nOfAirc)) {
                if ((i == n) || ((i == (n + 1)) && (this.airc[i].aircIndex() == (this.airc[n].aircIndex() + 1)))) {
                    continue;
                }
            }
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            if (((maneuver.get_task() == 7) || (maneuver.get_task() == 6) || (maneuver.get_task() == 4)) && maneuver.isOk()) {
                return this.airc[i];
            }
        }
        return null;
    }

    public boolean waitGroup(int n) {
        Aircraft firstOkAirc = this.firstOkAirc(n);
        Maneuver maneuver = (Maneuver) this.airc[n].FM;
        if (firstOkAirc != null) {
            maneuver.airClient = firstOkAirc.FM;
            maneuver.set_task(1);
            maneuver.clear_stack();
            maneuver.set_maneuver(59);
            return true;
        }
        maneuver.set_task(3);
        maneuver.clear_stack();
        maneuver.set_maneuver(21);
        return false;
    }

    public void setGTargMode(int gTargetPreference) {
        this.gTargetPreference = gTargetPreference;
    }

    public void setGTargMode(Actor actor) {
        if ((actor != null) && Actor.isAlive(actor)) {
            if ((actor instanceof BigshipGeneric) || (actor instanceof ShipGeneric) || (actor instanceof Chief) || (actor instanceof Bridge)) {
                this.gTargMode = 1;
                this.gTargActor = actor;
            } else {
                this.gTargMode = 2;
                this.gTargActor = actor;
                this.gTargPoint.set(actor.pos.getAbsPoint());
                this.gTargRadius = 200.0f;
                if (actor instanceof BigshipGeneric) {
                    this.gTargRadius = 20.0f;
                    this.setGTargMode(6);
                }
            }
        } else {
            this.gTargMode = 0;
        }
    }

    public void setGTargMode(Point3d point3d, float gTargRadius) {
        this.gTargMode = 2;
        this.gTargPoint.set(point3d);
        this.gTargRadius = gTargRadius;
    }

    public Actor setGAttackObject(int n) {
        if ((n > (this.nOfAirc - 1)) || (n < 0)) {
            return null;
        }
        Actor actor = null;
        if (this.gTargMode == 1) {
            actor = War.GetRandomFromChief(this.airc[n], this.gTargActor);
        } else if (this.gTargMode == 2) {
            actor = War.GetNearestEnemy(this.airc[n], this.gTargetPreference, this.gTargPoint, this.gTargRadius);
        }
        if (actor != null) {
            this.gTargWasFound = true;
            this.gTargDestroyed = false;
        }
        if ((actor == null) && this.gTargWasFound) {
            this.gTargDestroyed = true;
            this.gTargWasFound = false;
        }
        return actor;
    }

    public void setATargMode(int aTargetPreference) {
        this.aTargetPreference = aTargetPreference;
    }

    public AirGroup chooseTargetGroup() {
        if (this.enemies == null) {
            return null;
        }
        int length = AirGroupList.length(this.enemies[0]);
        AirGroup airGroup = null;
        float n = 1.0E12f;
        for (int i = 0; i < length; ++i) {
            AirGroup group = AirGroupList.getGroup(this.enemies[0], i);
            int n2 = 0;
            if ((group != null) && (group.nOfAirc > 0)) {
                if (this.aTargetPreference == 9) {
                    n2 = 1;
                } else if ((this.aTargetPreference == 7) && (group.airc[0] instanceof TypeFighter)) {
                    n2 = 1;
                } else if ((this.aTargetPreference == 8) && !(group.airc[0] instanceof TypeFighter)) {
                    n2 = 1;
                }
                if (n2 != 0) {
                    for (int j = 0; j < group.nOfAirc; ++j) {
                        if (Actor.isAlive(group.airc[j]) && group.airc[j].FM.isCapableOfBMP() && !group.airc[j].FM.isTakenMortalDamage()) {
                            n2 = 1;
                            break;
                        }
                    }
                }
                if (n2 != 0) {
                    AirGroup.tmpV.sub(this.Pos, group.Pos);
                    if (AirGroup.tmpV.lengthSquared() < n) {
                        airGroup = group;
                        n = (float) AirGroup.tmpV.lengthSquared();
                    }
                }
            } else {
                AirGroupList.delAirGroup(this.enemies, 0, group);
            }
        }
        return airGroup;
    }

    boolean somebodyAttacks() {
        boolean b = false;
        for (int i = 0; i < this.nOfAirc; ++i) {
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            if ((maneuver instanceof RealFlightModel) && ((RealFlightModel) maneuver).isRealMode() && (this.airc[i].aircIndex() == 0)) {
                b = true;
                break;
            }
            if (!this.isWingman(i) && maneuver.isOk() && maneuver.hasCourseWeaponBullets()) {
                b = true;
                break;
            }
        }
        return b;
    }

    boolean somebodyGAttacks() {
        boolean b = false;
        for (int i = 0; i < this.nOfAirc; ++i) {
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            if ((maneuver instanceof RealFlightModel) && ((RealFlightModel) maneuver).isRealMode() && (this.airc[i].aircIndex() == 0)) {
                b = true;
                break;
            }
            if (maneuver.isOk() && (maneuver.get_task() != 1)) {
                b = true;
                break;
            }
        }
        return b;
    }

    void switchWayPoint() {
        Maneuver maneuver = (Maneuver) this.airc[0].FM;
        AirGroup.tmpV.sub(this.w.curr().getP(), maneuver.Loc);
        float n = (float) AirGroup.tmpV.lengthSquared();
        int cur = this.w.Cur();
        this.w.next();
        AirGroup.tmpV.sub(this.w.curr().getP(), maneuver.Loc);
        float n2 = (float) AirGroup.tmpV.lengthSquared();
        this.w.setCur(cur);
        if (n > n2) {
            String targetName = this.airc[0].FM.AP.way.curr().getTargetName();
            this.airc[0].FM.AP.way.next();
            this.w.next();
            if ((this.airc[0].FM.AP.way.curr().Action == 0) && (this.airc[0].FM.AP.way.curr().getTarget() == null)) {
                this.airc[0].FM.AP.way.curr().setTarget(targetName);
            }
            if (this.w.curr().getTarget() == null) {
                this.w.curr().setTarget(targetName);
            }
        }
    }

    public boolean isWingman(int n) {
        if (n < 0) {
            return false;
        }
        Maneuver maneuver = (Maneuver) this.airc[n].FM;
        if (((this.airc[n].aircIndex() & 0x1) != 0x0) && !maneuver.aggressiveWingman) {
            if (n <= 0) {
                return false;
            }
            maneuver.Leader = this.airc[n - 1].FM;
            if ((maneuver.Leader != null) && (this.airc[n - 1].aircIndex() == (this.airc[n].aircIndex() - 1)) && this.enemyFighters && Actor.isAlive(this.airc[n - 1]) && maneuver.Leader.isOk()) {
                return true;
            }
        }
        return false;
    }

    private static Aircraft chooseTarget(AirGroup airGroup) {
        Aircraft aircraft = null;
        if ((airGroup != null) && (airGroup.nOfAirc > 0)) {
            aircraft = airGroup.airc[World.Rnd().nextInt(0, airGroup.nOfAirc - 1)];
        }
        if ((aircraft != null) && (!Actor.isAlive(aircraft) || !aircraft.FM.isOk()) && (airGroup != null)) {
            for (int i = 0; i < airGroup.nOfAirc; ++i) {
                if (Actor.isAlive(airGroup.airc[i]) && aircraft.FM.isOk()) {
                    aircraft = airGroup.airc[i];
                }
            }
        }
        return aircraft;
    }

    public FlightModel setAAttackObject(int n) {
        if ((n > (this.nOfAirc - 1)) || (n < 0)) {
            return null;
        }
        AirGroup airGroup = this.targetGroup;
        if ((airGroup == null) || (airGroup.nOfAirc == 0)) {
            airGroup = this.chooseTargetGroup();
        }
        Aircraft chooseTarget = AirGroup.chooseTarget(airGroup);
        if (chooseTarget != null) {
            this.aTargWasFound = true;
            this.aTargDestroyed = false;
        }
        if ((chooseTarget == null) && this.aTargWasFound) {
            this.aTargDestroyed = true;
            this.aTargWasFound = false;
        }
        return chooseTarget != null ? chooseTarget.FM : null;
    }

//    public void setTaskAndManeuver(final int n) {
//        if (n > this.nOfAirc - 1) {
//            return;
//        }
//        if (n < 0) {
//            return;
//        }
//        final Maneuver maneuver = (Maneuver)this.airc[n].FM;
//        Label_3414: {
//            switch (this.grTask) {
//                case 1: {
//                    FlightModel leader = null;
//                    AirGroup.tmpV.set(0.0, 0.0, 0.0);
//                    for (int i = 0; i < this.nOfAirc; ++i) {
//                        final Maneuver leader2 = (Maneuver)this.airc[i].FM;
//                        if (this.airc[i] instanceof TypeGlider) {
//                            leader2.accurate_set_FOLLOW();
//                        }
//                        else {
//                            AirGroup.tmpV.add(leader2.Offset);
//                            if (!leader2.isBusy() || (leader2 instanceof RealFlightModel && ((RealFlightModel)leader2).isRealMode() && leader2.isOk())) {
//                                leader2.Leader = null;
//                                if (this.leaderGroup == null || this.leaderGroup.nOfAirc == 0) {
//                                    leader2.accurate_set_task_maneuver(3, 21);
//                                }
//                                else if (((Maneuver)this.leaderGroup.airc[0].FM).isBusy()) {
//                                    leader2.accurate_set_task_maneuver(3, 21);
//                                }
//                                else {
//                                    leader2.accurate_set_FOLLOW();
//                                    leader2.followOffset.set(AirGroup.tmpV);
//                                    leader2.Leader = this.leaderGroup.airc[0].FM;
//                                }
//                                AirGroup.tmpV.set(0.0, 0.0, 0.0);
//                                for (int j = i + 1; j < this.nOfAirc; ++j) {
//                                    final Maneuver maneuver2 = (Maneuver)this.airc[j].FM;
//                                    AirGroup.tmpV.add(maneuver2.Offset);
//                                    if (!maneuver2.isBusy()) {
//                                        maneuver2.accurate_set_FOLLOW();
//                                        if (this.airc[j] instanceof TypeGlider) {
//                                            continue;
//                                        }
//                                        if (leader == null) {
//                                            maneuver2.followOffset.set(AirGroup.tmpV);
//                                            maneuver2.Leader = leader2;
//                                        }
//                                        else {
//                                            maneuver2.followOffset.set(maneuver2.Offset);
//                                            maneuver2.Leader = leader;
//                                        }
//                                    }
//                                    if (maneuver2 instanceof RealFlightModel) {
//                                        if ((this.airc[j].aircIndex() & 0x1) == 0x0) {
//                                            leader = maneuver2;
//                                        }
//                                    }
//                                    else {
//                                        leader = null;
//                                    }
//                                }
//                                break;
//                            }
//                        }
//                    }
//                    break;
//                }
//                case 4: {
//                    if (maneuver.isBusy()) {
//                        break;
//                    }
//                    if (maneuver.target_ground == null || !Actor.isAlive(maneuver.target_ground) || maneuver.Loc.distance(maneuver.target_ground.pos.getAbsPoint()) > 4000.0) {
//                        maneuver.target_ground = this.setGAttackObject(n);
//                    }
//                    if (maneuver.target_ground == null) {
//                        if (!this.waitGroup(n) && n == 0) {
//                            if (maneuver.AP.way.curr().Action == 3) {
//                                maneuver.AP.way.next();
//                            }
//                            this.setGroupTask(1);
//                            break;
//                        }
//                        break;
//                    }
//                    else {
//                        if (this.airc[n] instanceof TypeDockable) {
//                            if (this.airc[n] instanceof I_16TYPE24DRONE) {
//                                ((I_16TYPE24DRONE)this.airc[n]).typeDockableAttemptDetach();
//                            }
//                            if (this.airc[n] instanceof MXY_7) {
//                                ((MXY_7)this.airc[n]).typeDockableAttemptDetach();
//                            }
//                            if (this.airc[n] instanceof G4M2E) {
//                                ((G4M2E)this.airc[n]).typeDockableAttemptDetach();
//                            }
//                            if (this.airc[n] instanceof TB_3_4M_34R_SPB) {
//                                ((TB_3_4M_34R_SPB)this.airc[n]).typeDockableAttemptDetach();
//                            }
//                        }
//                        if ((maneuver.AP.way.Cur() == maneuver.AP.way.size() - 1 && maneuver.AP.way.curr().Action == 3) || this.airc[n] instanceof MXY_7) {
//                            maneuver.kamikaze = true;
//                            maneuver.set_task(7);
//                            maneuver.clear_stack();
//                            maneuver.set_maneuver(46);
//                            break;
//                        }
//                        boolean b = true;
//                        if (maneuver.hasRockets()) {
//                            b = false;
//                        }
//                        if (maneuver.CT.Weapons[0] != null && maneuver.CT.Weapons[0][0] != null && maneuver.CT.Weapons[0][0].bulletMassa() > 0.05f && maneuver.CT.Weapons[0][0].countBullets() > 0) {
//                            b = false;
//                        }
//                        if ((b && maneuver.CT.getWeaponMass() < 7.0f) || maneuver.CT.getWeaponMass() < 1.0f) {
//                            Voice.speakEndOfAmmo(this.airc[n]);
//                            if (!this.waitGroup(n) && n == 0) {
//                                if (maneuver.AP.way.curr().Action == 3) {
//                                    maneuver.AP.way.next();
//                                }
//                                this.setGroupTask(1);
//                                break;
//                            }
//                            break;
//                        }
//                        else {
//                            if (maneuver.target_ground instanceof Prey && (((Prey)maneuver.target_ground).HitbyMask() & 0x1) == 0x0) {
//                                float bulletMassa = 0.0f;
//                                for (int k = 0; k < 4; ++k) {
//                                    if (maneuver.CT.Weapons[k] != null) {
//                                        for (int l = 0; l < maneuver.CT.Weapons[k].length; ++l) {
//                                            if (maneuver.CT.Weapons[k][l] != null && maneuver.CT.Weapons[k][l].countBullets() != 0 && maneuver.CT.Weapons[k][l].bulletMassa() > bulletMassa) {
//                                                bulletMassa = maneuver.CT.Weapons[k][l].bulletMassa();
//                                            }
//                                        }
//                                    }
//                                }
//                                if (bulletMassa < 0.08f || (maneuver.target_ground instanceof TgtShip && bulletMassa < 0.55f)) {
//                                    maneuver.AP.way.next();
//                                    maneuver.set_task(1);
//                                    maneuver.clear_stack();
//                                    maneuver.set_maneuver(21);
//                                    maneuver.target_ground = null;
//                                    break;
//                                }
//                            }
//                            if (maneuver.CT.Weapons[3] != null) {
//                                for (int n2 = 0; n2 < maneuver.CT.Weapons[3].length; ++n2) {
//                                    if (maneuver.CT.Weapons[3][n2] != null && !(maneuver.CT.Weapons[3][n2] instanceof BombGunNull) && maneuver.CT.Weapons[3][n2].countBullets() != 0) {
//                                        if (maneuver.CT.Weapons[3][n2] instanceof ParaTorpedoGun) {
//                                            maneuver.set_task(7);
//                                            maneuver.clear_stack();
//                                            maneuver.set_maneuver(43);
//                                            return;
//                                        }
//                                        if (maneuver.CT.Weapons[3][n2] instanceof TorpedoGun) {
//                                            if (!(maneuver.target_ground instanceof TgtShip)) {
//                                                maneuver.set_task(7);
//                                                maneuver.clear_stack();
//                                                maneuver.set_maneuver(43);
//                                                return;
//                                            }
//                                            maneuver.set_task(7);
//                                            maneuver.clear_stack();
//                                            if (!(maneuver.target_ground instanceof BigshipGeneric)) {
//                                                maneuver.set_maneuver(51);
//                                                return;
//                                            }
//                                            final BigshipGeneric bigshipGeneric = (BigshipGeneric)maneuver.target_ground;
//                                            if (this.airc[n] instanceof TypeHasToKG && !bigshipGeneric.zutiIsStatic() && this.airc[n].FM.Skill >= 2 && bigshipGeneric.collisionR() > 45.0f) {
//                                                maneuver.set_maneuver(73);
//                                                return;
//                                            }
//                                            maneuver.set_maneuver(51);
//                                            return;
//                                        }
//                                        else {
//                                            if (this.airc[n] instanceof TypeGuidedBombCarrier && maneuver.CT.Weapons[3][n2] instanceof RocketGunHS_293) {
//                                                maneuver.set_task(7);
//                                                maneuver.clear_stack();
//                                                maneuver.set_maneuver(71);
//                                                return;
//                                            }
//                                            if (this.airc[n] instanceof TypeGuidedBombCarrier && maneuver.CT.Weapons[3][n2] instanceof RocketGunFritzX) {
//                                                maneuver.set_task(7);
//                                                maneuver.clear_stack();
//                                                maneuver.set_maneuver(72);
//                                                return;
//                                            }
//                                            if (maneuver.CT.Weapons[3][n2] instanceof BombGunPara) {
//                                                this.w.curr().setTarget(null);
//                                                maneuver.target_ground = null;
//                                                this.grTask = 1;
//                                                this.setTaskAndManeuver(n);
//                                                return;
//                                            }
//                                            if (maneuver.CT.Weapons[3][n2].bulletMassa() < 10.0f && !(maneuver.CT.Weapons[3][n2] instanceof BombGunParafrag8)) {
//                                                maneuver.set_task(7);
//                                                maneuver.clear_stack();
//                                                maneuver.set_maneuver(52);
//                                                return;
//                                            }
//                                            if (this.airc[n] instanceof TypeDiveBomber && maneuver.Alt > 1200.0f) {
//                                                maneuver.set_task(7);
//                                                maneuver.clear_stack();
//                                                maneuver.set_maneuver(50);
//                                                return;
//                                            }
//                                            if (n2 == maneuver.CT.Weapons[3].length - 1) {
//                                                maneuver.set_task(7);
//                                                maneuver.clear_stack();
//                                                maneuver.set_maneuver(43);
//                                                return;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            if (maneuver.target_ground instanceof BridgeSegment && !maneuver.hasRockets()) {
//                                maneuver.set_task(1);
//                                maneuver.clear_stack();
//                                maneuver.set_maneuver(59);
//                                maneuver.target_ground = null;
//                                break;
//                            }
//                            if (this.airc[n] instanceof F4U && maneuver.CT.Weapons[2] != null && maneuver.CT.Weapons[2][0].bulletMassa() > 100.0 && maneuver.CT.Weapons[2][0].countBullets() > 0) {
//                                maneuver.set_task(7);
//                                maneuver.clear_stack();
//                                maneuver.set_maneuver(47);
//                                break;
//                            }
//                            if (this.airc[n] instanceof R_5xyz) {
//                                if (((R_5xyz)this.airc[n]).strafeWithGuns) {
//                                    maneuver.set_task(7);
//                                    maneuver.clear_stack();
//                                    maneuver.set_maneuver(43);
//                                    break;
//                                }
//                                this.w.curr().setTarget(null);
//                                maneuver.target_ground = null;
//                                this.grTask = 1;
//                                this.setTaskAndManeuver(n);
//                                this.grTask = 4;
//                                break;
//                            }
//                            else {
////                                if (this.airc[n] instanceof TBD) {
////                                    this.w.curr().setTarget(null);
////                                    maneuver.target_ground = null;
////                                    this.grTask = 1;
////                                    this.setTaskAndManeuver(n);
////                                    this.grTask = 4;
////                                    break;
////                                }
//                                if (this.airc[n] instanceof TypeFighter || this.airc[n] instanceof TypeStormovik) {
//                                    maneuver.set_task(7);
//                                    maneuver.clear_stack();
//                                    maneuver.set_maneuver(43);
//                                    break;
//                                }
//                                this.w.curr().setTarget(null);
//                                maneuver.target_ground = null;
//                                this.grTask = 1;
//                                this.setTaskAndManeuver(n);
//                                this.grTask = 4;
//                                break;
//                            }
//                        }
//                    }
////                    break;
//                }
//                case 3: {
//                    if (maneuver.isBusy()) {
//                        break;
//                    }
//                    if ((!(maneuver instanceof RealFlightModel) || !((RealFlightModel)maneuver).isRealMode()) && !maneuver.bKeepOrdnance) {
//                        maneuver.bombsOut = true;
//                        maneuver.CT.dropFuelTanks();
//                    }
//                    if (this.isWingman(n) && !this.bInitAttack) {
//                        maneuver.airClient = maneuver.Leader;
//                        maneuver.set_task(5);
//                        maneuver.clear_stack();
//                        switch (maneuver.Skill) {
//                            case 0: {
//                                ((RealFlightModel)maneuver).wingmanAttacks(1);
//                                break Label_3414;
//                            }
//                            case 1: {
//                                ((RealFlightModel)maneuver).wingmanAttacks(1);
//                                break Label_3414;
//                            }
//                            case 2: {
//                                ((RealFlightModel)maneuver).wingmanAttacks(1);
//                                break Label_3414;
//                            }
//                            case 3: {
//                                ((RealFlightModel)maneuver).wingmanAttacks(1);
//                                break Label_3414;
//                            }
//                            default: {
//                                maneuver.followOffset.set(200.0, 0.0, 20.0);
//                                ((RealFlightModel)maneuver).wingmanAttacks(1);
//                                break Label_3414;
//                            }
//                        }
//                    }
//                    else {
//                        maneuver.airClient = null;
//                        int n3 = 1;
//                        if (this.inCorridor(maneuver.Loc)) {
//                            n3 = 0;
//                        }
//                        if (n3 != 0) {
//                            final int cur = this.w.Cur();
//                            this.w.next();
//                            if (this.inCorridor(maneuver.Loc)) {
//                                n3 = 0;
//                            }
//                            this.w.setCur(cur);
//                            if (n3 != 0) {
//                                final int cur2 = this.w.Cur();
//                                this.w.prev();
//                                if (this.inCorridor(maneuver.Loc)) {
//                                    n3 = 0;
//                                }
//                                this.w.setCur(cur2);
//                            }
//                        }
//                        if (n3 != 0) {
//                            maneuver.set_task(3);
//                            maneuver.clear_stack();
//                            maneuver.set_maneuver(21);
//                            break;
//                        }
//                        if (maneuver.target == null || !maneuver.target.isOk() || maneuver.Loc.distance(maneuver.target.Loc) > 4000.0) {
//                            maneuver.target = this.setAAttackObject(n);
//                        }
//                        if (maneuver.target == null || !maneuver.hasCourseWeaponBullets()) {
//                            if (!this.waitGroup(n) && n == 0) {
//                                this.setGroupTask(1);
//                                break;
//                            }
//                            break;
//                        }
//                        else {
//                            if (this.w.curr().Action == 2 && maneuver.Loc.distance(maneuver.target.Loc) > 2500.0) {
//                                maneuver.set_task(3);
//                                maneuver.clear_stack();
//                                maneuver.set_maneuver(21);
//                                break;
//                            }
//                            maneuver.set_task(6);
//                            if (maneuver.target.actor instanceof TypeFighter) {
//                                if (n == 0 && this.bInitAttack) {
//                                    maneuver.clear_stack();
//                                    final Vector3d vector3d = new Vector3d();
//                                    final Vector3d vector3d2 = new Vector3d();
//                                    vector3d.sub(maneuver.target.Loc, maneuver.Loc);
//                                    maneuver.Or.transformInv(vector3d);
//                                    vector3d.z = 0.0;
//                                    vector3d.normalize();
//                                    vector3d2.set(vector3d);
//                                    maneuver.target.Or.transformInv(vector3d2);
//                                    vector3d2.z = 0.0;
//                                    vector3d2.normalize();
//                                    final float n4 = (float)maneuver.Vwld.length();
//                                    final float n5 = (float)maneuver.target.Vwld.length();
//                                    final float n6 = (float)maneuver.Loc.distance(maneuver.target.Loc);
//                                    if (n5 > 30.0f && maneuver.target.Loc.z - Engine.land().HQ_Air(maneuver.target.Loc.x, maneuver.target.Loc.y) > 50.0) {
//                                        final int[] groupLove = this.groupLove((float)vector3d.x, (float)vector3d2.y, n4, n5, (float)maneuver.Loc.z, (float)maneuver.target.Loc.z, n6, this.outNum, this.grSubSkill, Mission.curCloudsType(), this.nOfAirc);
//                                        maneuver.set_maneuver(groupLove[0]);
//                                        for (int n7 = 1; n7 < groupLove.length; ++n7) {
//                                            maneuver.push(groupLove[n7]);
//                                        }
//                                        break;
//                                    }
//                                    maneuver.set_maneuver(27);
//                                    this.coverFree();
//                                    break;
//                                }
//                                else {
//                                    if (this.bInitAttack) {
//                                        break;
//                                    }
//                                    if (maneuver.VmaxH * 0.94f > maneuver.target.VmaxH && (maneuver.Loc.distance(maneuver.target.Loc) > 1000.0 || maneuver.Vwld.length() > maneuver.target.Vwld.length() + 10.0)) {
//                                        maneuver.clear_stack();
//                                        maneuver.set_maneuver(62);
//                                        break;
//                                    }
//                                    maneuver.clear_stack();
//                                    maneuver.set_maneuver(27);
//                                    break;
//                                }
//                            }
//                            else {
//                                if (maneuver.target.actor instanceof TypeStormovik) {
//                                    ((RealFlightModel)maneuver).attackStormoviks();
//                                    break;
//                                }
//                                ((RealFlightModel)maneuver).attackBombers();
//                                break;
//                            }
//                        }
//                    }
////                    break;
//                }
//                case 2: {
//                    FlightModel leader3 = null;
//                    AirGroup.tmpV.set(0.0, 0.0, 0.0);
//                    for (int n8 = 0; n8 < this.nOfAirc; ++n8) {
//                        final Maneuver leader4 = (Maneuver)this.airc[n8].FM;
//                        AirGroup.tmpV.add(leader4.Offset);
//                        if (!leader4.isBusy() || n8 == this.nOfAirc - 1 || (leader4 instanceof RealFlightModel && ((RealFlightModel)leader4).isRealMode())) {
//                            leader4.Leader = null;
//                            if (this.clientGroup != null && this.clientGroup.nOfAirc > 0 && this.clientGroup.airc[0] != null) {
//                                leader4.airClient = this.clientGroup.airc[0].FM;
//                                leader4.accurate_set_task_maneuver(5, 59);
//                            }
//                            else {
//                                leader4.accurate_set_task_maneuver(3, 21);
//                            }
//                            AirGroup.tmpV.set(0.0, 0.0, 0.0);
//                            for (int n9 = n8 + 1; n9 < this.nOfAirc; ++n9) {
//                                final Maneuver maneuver3 = (Maneuver)this.airc[n9].FM;
//                                AirGroup.tmpV.add(maneuver3.Offset);
//                                if (!maneuver3.isBusy()) {
//                                    maneuver3.accurate_set_FOLLOW();
//                                    if (leader3 == null) {
//                                        maneuver3.followOffset.set(AirGroup.tmpV);
//                                        maneuver3.Leader = leader4;
//                                    }
//                                    else {
//                                        maneuver3.followOffset.set(maneuver3.Offset);
//                                        maneuver3.Leader = leader3;
//                                    }
//                                }
//                                if (maneuver3 instanceof RealFlightModel) {
//                                    if ((this.airc[n9].aircIndex() & 0x1) == 0x0) {
//                                        leader3 = maneuver3;
//                                    }
//                                }
//                                else {
//                                    leader3 = null;
//                                }
//                            }
//                            break;
//                        }
//                    }
//                    break;
//                }
//                case 5: {
//                    if (maneuver.isBusy()) {
//                        return;
//                    }
//                    break;
//                }
//                case 7: {
//                    if (maneuver.isBusy()) {
//                        return;
//                    }
//                    final Maneuver maneuver4 = (Maneuver)this.airc[0].FM;
//                    if (n > 0 && (maneuver4.getSpeed() < 30.0f || maneuver4.Loc.z - Engine.land().HQ_Air(maneuver4.Loc.x, maneuver4.Loc.y) < 51.5)) {
//                        maneuver.airClient = this.airc[n - 1].FM;
//                        maneuver.push();
//                        maneuver.push(82);
//                        maneuver.pop();
//                        break;
//                    }
//                    break;
//                }
//                case 6: {
//                    if (maneuver.isBusy()) {
//                        return;
//                    }
//                    break;
//                }
//                case 8: {
//                    if (maneuver.isBusy()) {
//                        return;
//                    }
//                    maneuver.set_maneuver(106);
//                    maneuver.actionTimerStop = maneuver.AP.way.curr().delayTimer * 60000L;
//                    break;
//                }
//                default: {
//                    if (maneuver.isBusy()) {
//                        return;
//                    }
//                    maneuver.set_maneuver(21);
//                    break;
//                }
//            }
//        }
//    }

    public void setTaskAndManeuver(int i) {
        if ((i > (this.nOfAirc - 1)) || (i < 0)) {
            return;
        }
        Maneuver maneuver = (Maneuver) this.airc[i].FM;
//label0:
        switch (this.grTask) {
            case 1: // '\001'
                Object obj4 = null;
                AirGroup.tmpV.set(0.0D, 0.0D, 0.0D);
                for (int j = 0; j < this.nOfAirc; j++) {
                    Maneuver maneuver3 = (Maneuver) this.airc[j].FM;
                    if (this.airc[j] instanceof TypeGlider) {
                        maneuver3.accurate_set_FOLLOW();
                        continue;
                    }
                    AirGroup.tmpV.add(maneuver3.Offset);
                    if (maneuver3.isBusy() && (!(maneuver3 instanceof RealFlightModel) || !((RealFlightModel) maneuver3).isRealMode() || !maneuver3.isOk())) {
                        continue;
                    }
                    maneuver3.Leader = null;
                    if ((this.leaderGroup == null) || (this.leaderGroup.nOfAirc == 0)) {
                        maneuver3.accurate_set_task_maneuver(3, 21);
                    } else if (((Maneuver) this.leaderGroup.airc[0].FM).isBusy()) {
                        maneuver3.accurate_set_task_maneuver(3, 21);
                    } else {
                        maneuver3.accurate_set_FOLLOW();
                        maneuver3.followOffset.set(AirGroup.tmpV);
                        maneuver3.Leader = this.leaderGroup.airc[0].FM;
                    }
                    AirGroup.tmpV.set(0.0D, 0.0D, 0.0D);
                    for (int k = j + 1; k < this.nOfAirc; k++) {
                        Maneuver maneuver1 = (Maneuver) this.airc[k].FM;
                        AirGroup.tmpV.add(maneuver1.Offset);
                        if (!maneuver1.isBusy()) {
                            maneuver1.accurate_set_FOLLOW();
                            if (this.airc[k] instanceof TypeGlider) {
                                continue;
                            }
                            if (obj4 == null) {
                                maneuver1.followOffset.set(AirGroup.tmpV);
                                maneuver1.Leader = maneuver3;
                            } else {
                                maneuver1.followOffset.set(maneuver1.Offset);
                                maneuver1.Leader = (FlightModel) obj4;
                            }
                        }
                        if (maneuver1 instanceof RealFlightModel) {
                            if ((this.airc[k].aircIndex() & 1) == 0) {
                                obj4 = maneuver1;
                            }
                        } else {
                            obj4 = null;
                        }
                    }

                    break;
                }

                break;

            case 4: // '\004'
                if (maneuver.isBusy()) {
                    break;
                }
                if ((maneuver.target_ground == null) || !Actor.isAlive(maneuver.target_ground) || (maneuver.Loc.distance(maneuver.target_ground.pos.getAbsPoint()) > 4000D)) {
                    maneuver.target_ground = this.setGAttackObject(i);
                }
                if (maneuver.target_ground == null) {
                    if (this.waitGroup(i) || (i != 0)) {
                        break;
                    }
                    if (maneuver.AP.way.curr().Action == 3) {
                        maneuver.AP.way.next();
                    }
                    this.setGroupTask(1);
                    break;
                }
                if (this.airc[i] instanceof TypeDockable) {
                    if (this.airc[i] instanceof I_16TYPE24DRONE) {
                        ((I_16TYPE24DRONE) this.airc[i]).typeDockableAttemptDetach();
                    }
                    if (this.airc[i] instanceof MXY_7) {
                        ((MXY_7) this.airc[i]).typeDockableAttemptDetach();
                    }
                    if (this.airc[i] instanceof G4M2E) {
                        ((G4M2E) this.airc[i]).typeDockableAttemptDetach();
                    }
                    if (this.airc[i] instanceof TB_3_4M_34R_SPB) {
                        ((TB_3_4M_34R_SPB) this.airc[i]).typeDockableAttemptDetach();
                    }
                }
                if (((maneuver.AP.way.Cur() == (maneuver.AP.way.size() - 1)) && (maneuver.AP.way.curr().Action == 3)) || (this.airc[i] instanceof MXY_7)) {
                    maneuver.kamikaze = true;
                    maneuver.set_task(7);
                    maneuver.clear_stack();
                    maneuver.set_maneuver(46);
                    break;
                }
                boolean flag = true;
                if (maneuver.hasRockets()) {
                    flag = false;
                }
                if ((maneuver.CT.Weapons[0] != null) && (maneuver.CT.Weapons[0][0] != null) && (maneuver.CT.Weapons[0][0].bulletMassa() > 0.05F) && (maneuver.CT.Weapons[0][0].countBullets() > 0)) {
                    flag = false;
                }
                if ((flag && (maneuver.CT.getWeaponMass() < 7F)) || (maneuver.CT.getWeaponMass() < 1.0F)) {
                    Voice.speakEndOfAmmo(this.airc[i]);
                    if (this.waitGroup(i) || (i != 0)) {
                        break;
                    }
                    if (maneuver.AP.way.curr().Action == 3) {
                        maneuver.AP.way.next();
                    }
                    this.setGroupTask(1);
                    break;
                }
                if ((maneuver.target_ground instanceof Prey) && ((((Prey) maneuver.target_ground).HitbyMask() & 1) == 0)) {
                    float f = 0.0F;
                    for (int i1 = 0; i1 < 4; i1++) {
                        if (maneuver.CT.Weapons[i1] != null) {
                            for (int j1 = 0; j1 < maneuver.CT.Weapons[i1].length; j1++) {
                                if ((maneuver.CT.Weapons[i1][j1] != null) && (maneuver.CT.Weapons[i1][j1].countBullets() != 0) && (maneuver.CT.Weapons[i1][j1].bulletMassa() > f)) {
                                    f = maneuver.CT.Weapons[i1][j1].bulletMassa();
                                }
                            }
                        }
                    }

                    if ((f < 0.08F) || ((maneuver.target_ground instanceof TgtShip) && (f < 0.55F))) {
                        maneuver.AP.way.next();
                        maneuver.set_task(1);
                        maneuver.clear_stack();
                        maneuver.set_maneuver(21);
                        maneuver.target_ground = null;
                        break;
                    }
                }
                if (maneuver.CT.Weapons[3] != null) {
                    for (int l = 0; l < maneuver.CT.Weapons[3].length; l++) {
                        if ((maneuver.CT.Weapons[3][l] != null) && !(maneuver.CT.Weapons[3][l] instanceof BombGunNull) && (maneuver.CT.Weapons[3][l].countBullets() != 0)) {
                            if (maneuver.CT.Weapons[3][l] instanceof ParaTorpedoGun) {
                                maneuver.set_task(7);
                                maneuver.clear_stack();
                                maneuver.set_maneuver(43);
                                return;
                            }
                            if (maneuver.CT.Weapons[3][l] instanceof TorpedoGun) {
                                if (maneuver.target_ground instanceof TgtShip) {
                                    maneuver.set_task(7);
                                    maneuver.clear_stack();
                                    if (maneuver.target_ground instanceof BigshipGeneric) {
                                        BigshipGeneric bigshipgeneric = (BigshipGeneric) maneuver.target_ground;
                                        if ((this.airc[i] instanceof TypeHasToKG) && !bigshipgeneric.zutiIsStatic() && (this.airc[i].FM.Skill >= 2) && (bigshipgeneric.collisionR() > 45F)) {
                                            maneuver.set_maneuver(73);
                                            return;
                                        } else {
                                            maneuver.set_maneuver(51);
                                            return;
                                        }
                                    } else {
                                        maneuver.set_maneuver(51);
                                        return;
                                    }
                                } else {
                                    maneuver.set_task(7);
                                    maneuver.clear_stack();
                                    maneuver.set_maneuver(43);
                                    return;
                                }
                            }
                            if ((this.airc[i] instanceof TypeGuidedBombCarrier) && (maneuver.CT.Weapons[3][l] instanceof RocketGunHS_293)) {
                                maneuver.set_task(7);
                                maneuver.clear_stack();
                                maneuver.set_maneuver(71);
                                return;
                            }
                            if ((this.airc[i] instanceof TypeGuidedBombCarrier) && (maneuver.CT.Weapons[3][l] instanceof RocketGunFritzX)) {
                                maneuver.set_task(7);
                                maneuver.clear_stack();
                                maneuver.set_maneuver(72);
                                return;
                            }
                            if (maneuver.CT.Weapons[3][l] instanceof BombGunPara) {
                                this.w.curr().setTarget(null);
                                maneuver.target_ground = null;
                                this.grTask = 1;
                                this.setTaskAndManeuver(i);
                                return;
                            }
                            if ((maneuver.CT.Weapons[3][l].bulletMassa() < 10F) && !(maneuver.CT.Weapons[3][l] instanceof BombGunParafrag8)) {
                                maneuver.set_task(7);
                                maneuver.clear_stack();
                                maneuver.set_maneuver(52);
                                return;
                            }
                            if ((this.airc[i] instanceof TypeDiveBomber) && (maneuver.Alt > 1200F)) {
                                maneuver.set_task(7);
                                maneuver.clear_stack();
                                maneuver.set_maneuver(50);
                                return;
                            }
                            if (l == (maneuver.CT.Weapons[3].length - 1)) {
                                maneuver.set_task(7);
                                maneuver.clear_stack();
                                maneuver.set_maneuver(43);
                                return;
                            }
                        }
                    }
                }
                if ((maneuver.target_ground instanceof BridgeSegment) && !maneuver.hasRockets()) {
                    maneuver.set_task(1);
                    maneuver.clear_stack();
                    maneuver.set_maneuver(59);
                    maneuver.target_ground = null;
                    break;
                }
                if ((this.airc[i] instanceof F4U) && (maneuver.CT.Weapons[2] != null) && (maneuver.CT.Weapons[2][0].bulletMassa() > 100D) && (maneuver.CT.Weapons[2][0].countBullets() > 0)) {
                    maneuver.set_task(7);
                    maneuver.clear_stack();
                    maneuver.set_maneuver(47);
                    break;
                }
                if (this.airc[i] instanceof R_5xyz) {
                    if (((R_5xyz) this.airc[i]).strafeWithGuns) {
                        maneuver.set_task(7);
                        maneuver.clear_stack();
                        maneuver.set_maneuver(43);
                    } else {
                        this.w.curr().setTarget(null);
                        maneuver.target_ground = null;
                        this.grTask = 1;
                        this.setTaskAndManeuver(i);
                        this.grTask = 4;
                    }
                    break;
                }
//            if(airc[i] instanceof TBD)
//            {
//                w.curr().setTarget(null);
//                maneuver.target_ground = null;
//                grTask = 1;
//                setTaskAndManeuver(i);
//                grTask = 4;
//                break;
//            }
                if ((this.airc[i] instanceof TypeFighter) || (this.airc[i] instanceof TypeStormovik)) {
                    maneuver.set_task(7);
                    maneuver.clear_stack();
                    maneuver.set_maneuver(43);
                } else {
                    this.w.curr().setTarget(null);
                    maneuver.target_ground = null;
                    this.grTask = 1;
                    this.setTaskAndManeuver(i);
                    this.grTask = 4;
                }
                break;

            case 3: // '\003'
                if (maneuver.isBusy()) {
                    break;
                }
                if ((!(maneuver instanceof RealFlightModel) || !((RealFlightModel) maneuver).isRealMode()) && !maneuver.bKeepOrdnance) {
                    maneuver.bombsOut = true;
                    maneuver.CT.dropFuelTanks();
                }
                if (this.isWingman(i) && !this.bInitAttack) {
                    maneuver.airClient = maneuver.Leader;
                    maneuver.set_task(5);
                    maneuver.clear_stack();
                    switch (maneuver.Skill) {
                        case 0: // '\0'
                            ((Pilot) maneuver).wingmanAttacks(1);
                            break;

                        case 1: // '\001'
                            ((Pilot) maneuver).wingmanAttacks(1);
                            break;

                        case 2: // '\002'
                            ((Pilot) maneuver).wingmanAttacks(1);
                            break;

                        case 3: // '\003'
                            ((Pilot) maneuver).wingmanAttacks(1);
                            break;

                        default:
                            maneuver.followOffset.set(200D, 0.0D, 20D);
                            ((Pilot) maneuver).wingmanAttacks(1);
                            break;
                    }
                    break;
                }
                maneuver.airClient = null;
                boolean flag1 = true;
                if (this.inCorridor(maneuver.Loc)) {
                    flag1 = false;
                }
                if (flag1) {
                    int k1 = this.w.Cur();
                    this.w.next();
                    if (this.inCorridor(maneuver.Loc)) {
                        flag1 = false;
                    }
                    this.w.setCur(k1);
                    if (flag1) {
                        int l1 = this.w.Cur();
                        this.w.prev();
                        if (this.inCorridor(maneuver.Loc)) {
                            flag1 = false;
                        }
                        this.w.setCur(l1);
                    }
                }
                if (flag1) {
                    maneuver.set_task(3);
                    maneuver.clear_stack();
                    maneuver.set_maneuver(21);
                    break;
                }
                if ((maneuver.target == null) || !maneuver.target.isOk() || (maneuver.Loc.distance(maneuver.target.Loc) > 4000D)) {
                    maneuver.target = this.setAAttackObject(i);
                }
                if ((maneuver.target == null) || !maneuver.hasCourseWeaponBullets()) {
                    if (!this.waitGroup(i) && (i == 0)) {
                        this.setGroupTask(1);
                    }
                    break;
                }
                if ((this.w.curr().Action == 2) && (maneuver.Loc.distance(maneuver.target.Loc) > 2500D)) {
                    maneuver.set_task(3);
                    maneuver.clear_stack();
                    maneuver.set_maneuver(21);
                    break;
                }
                maneuver.set_task(6);
                if (maneuver.target.actor instanceof TypeFighter) {
                    if ((i == 0) && this.bInitAttack) {
                        maneuver.clear_stack();
                        Vector3d vector3d = new Vector3d();
                        Vector3d vector3d1 = new Vector3d();
                        vector3d.sub(maneuver.target.Loc, maneuver.Loc);
                        maneuver.Or.transformInv(vector3d);
                        vector3d.z = 0.0D;
                        vector3d.normalize();
                        vector3d1.set(vector3d);
                        maneuver.target.Or.transformInv(vector3d1);
                        vector3d1.z = 0.0D;
                        vector3d1.normalize();
                        float f1 = (float) maneuver.Vwld.length();
                        float f2 = (float) maneuver.target.Vwld.length();
                        float f3 = (float) maneuver.Loc.distance(maneuver.target.Loc);
                        if ((f2 > 30F) && ((maneuver.target.Loc.z - Engine.land().HQ_Air(maneuver.target.Loc.x, maneuver.target.Loc.y)) > 50D)) {
                            int ai[] = this.groupLove((float) vector3d.x, (float) vector3d1.y, f1, f2, (float) maneuver.Loc.z, (float) maneuver.target.Loc.z, f3, this.outNum, this.grSubSkill, Mission.curCloudsType(), this.nOfAirc);
                            maneuver.set_maneuver(ai[0]);
                            for (int k2 = 1; k2 < ai.length; k2++) {
                                maneuver.push(ai[k2]);
                            }

                        } else {
                            maneuver.set_maneuver(27);
                            this.coverFree();
                        }
                        break;
                    }
                    if (this.bInitAttack) {
                        break;
                    }
                    if (((maneuver.VmaxH * 0.94F) > maneuver.target.VmaxH) && ((maneuver.Loc.distance(maneuver.target.Loc) > 1000D) || (maneuver.Vwld.length() > (maneuver.target.Vwld.length() + 10D)))) {
                        maneuver.clear_stack();
                        maneuver.set_maneuver(62);
                    } else {
                        maneuver.clear_stack();
                        maneuver.set_maneuver(27);
                    }
                    break;
                }
                if (maneuver.target.actor instanceof TypeStormovik) {
                    ((Pilot) maneuver).attackStormoviks();
                } else {
                    ((Pilot) maneuver).attackBombers();
                }
                break;

            case 2: // '\002'
                Object obj5 = null;
                AirGroup.tmpV.set(0.0D, 0.0D, 0.0D);
                for (int i2 = 0; i2 < this.nOfAirc; i2++) {
                    Maneuver maneuver4 = (Maneuver) this.airc[i2].FM;
                    AirGroup.tmpV.add(maneuver4.Offset);
                    if (!maneuver4.isBusy() || (i2 == (this.nOfAirc - 1)) || ((maneuver4 instanceof RealFlightModel) && ((RealFlightModel) maneuver4).isRealMode())) {
                        maneuver4.Leader = null;
                        if ((this.clientGroup != null) && (this.clientGroup.nOfAirc > 0) && (this.clientGroup.airc[0] != null)) {
                            maneuver4.airClient = this.clientGroup.airc[0].FM;
                            maneuver4.accurate_set_task_maneuver(5, 59);
                        } else {
                            maneuver4.accurate_set_task_maneuver(3, 21);
                        }
                        AirGroup.tmpV.set(0.0D, 0.0D, 0.0D);
                        for (int j2 = i2 + 1; j2 < this.nOfAirc; j2++) {
                            Maneuver maneuver2 = (Maneuver) this.airc[j2].FM;
                            AirGroup.tmpV.add(maneuver2.Offset);
                            if (!maneuver2.isBusy()) {
                                maneuver2.accurate_set_FOLLOW();
                                if (obj5 == null) {
                                    maneuver2.followOffset.set(AirGroup.tmpV);
                                    maneuver2.Leader = maneuver4;
                                } else {
                                    maneuver2.followOffset.set(maneuver2.Offset);
                                    maneuver2.Leader = (FlightModel) obj5;
                                }
                            }
                            if (maneuver2 instanceof RealFlightModel) {
                                if ((this.airc[j2].aircIndex() & 1) == 0) {
                                    obj5 = maneuver2;
                                }
                            } else {
                                obj5 = null;
                            }
                        }
                        break;
                    }
                }

                break;

            case 5: // '\005'
                if (maneuver.isBusy()) {
                    return;
                }
                break;

            case 7: // '\007'
                if (maneuver.isBusy()) {
                    return;
                }
                Maneuver maneuver5 = (Maneuver) this.airc[0].FM;
                if ((i > 0) && ((maneuver5.getSpeed() < 30F) || ((maneuver5.Loc.z - Engine.land().HQ_Air(maneuver5.Loc.x, maneuver5.Loc.y)) < 51.5D))) {
                    maneuver.airClient = this.airc[i - 1].FM;
                    maneuver.push();
                    maneuver.push(82);
                    maneuver.pop();
                }
                break;

            case 6: // '\006'
                if (maneuver.isBusy()) {
                    return;
                }
                break;

            case 8: // '\b'
                if (maneuver.isBusy()) {
                    return;
                }
                maneuver.set_maneuver(106);
                maneuver.actionTimerStop = maneuver.AP.way.curr().delayTimer * 60000L;
                break;

            default:
                if (maneuver.isBusy()) {
                    return;
                }
                maneuver.set_maneuver(21);
                break;
        }
    }

    public void coverClose() {
        for (int i = 1; i < this.nOfAirc; ++i) {
            Maneuver maneuver2;
            Maneuver maneuver = maneuver2 = (Maneuver) this.airc[i].FM;
            FlightModel fm = this.airc[i - 1].FM;
            maneuver.airClient = fm;
            maneuver2.Leader = fm;
            maneuver.set_maneuver(65);
        }
    }

    public void coverStd() {
        Maneuver airClient = (Maneuver) this.airc[0].FM;
        for (int i = 1; i < this.nOfAirc; ++i) {
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            if ((this.airc[i].aircIndex() & 0x1) == 0x0) {
                maneuver.airClient = airClient;
                ((Pilot) maneuver).wingmanAttacks(3);
            } else {
                Pilot pilot = (Pilot) maneuver;
                Pilot pilot2 = (Pilot) maneuver;
                FlightModel fm = this.airc[i - 1].FM;
                pilot2.airClient = fm;
                pilot.Leader = fm;
                ((Pilot) maneuver).wingmanAttacks(1);
            }
        }
    }

    void coverFree() {
        for (int i = 1; i < this.nOfAirc; ++i) {
            ((Pilot) this.airc[i].FM).wingmanAttacks(0);
        }
    }

    public void attackBox() {
        for (int i = 1; i < this.nOfAirc; ++i) {
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            maneuver.target = this.setAAttackObject(i);
            maneuver.set_maneuver(93);
        }
    }

    public void attackLine() {
        for (int i = 1; i < this.nOfAirc; ++i) {
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            maneuver.target = this.setAAttackObject(i);
            maneuver.set_maneuver(92);
        }
    }

    public void attackFluid(int n) {
        Maneuver airClient = (Maneuver) this.airc[0].FM;
        for (int i = 1; i < this.nOfAirc; ++i) {
            Maneuver maneuver = (Maneuver) this.airc[i].FM;
            if ((this.airc[i].aircIndex() & 0x1) != 0x0) {
                Maneuver maneuver2 = maneuver;
                Maneuver maneuver3 = maneuver;
                FlightModel fm = this.airc[i - 1].FM;
                maneuver3.airClient = fm;
                maneuver2.Leader = fm;
                ((Pilot) maneuver).wingmanAttacks(1);
            } else {
                maneuver.airClient = airClient;
                maneuver.push(27);
                maneuver.set_maneuver(n);
            }
        }
    }

    public void update() {
        if ((this.nOfAirc == 0) || (this.airc[0] == null)) {
            return;
        }
        for (int i = 1; i < this.nOfAirc; ++i) {
            if (!Actor.isAlive(this.airc[i])) {
                this.delAircraft(this.airc[i]);
                --i;
            }
        }
        Maneuver maneuver = (Maneuver) this.airc[0].FM;
        if (Time.current() > this.timeNextAAACheck) {
            this.aaaShooters.clear();
            this.aaaNum /= 1.5f;
            if (this.aaaNum < 0.5f) {
                this.aaaNum = 0.0f;
            }
            this.timeNextAAACheck += 10000L;
        }
        if ((maneuver.AP.way.curr().waypointType < 406) && (maneuver.AP.way.curr().waypointType >= 401)) {
            this.setCAP(maneuver.AP.way.curr().waypointType - 401, 406, 0, 0, 0.0f, 0.0f, false);
            if (maneuver.AP.way.curr().delayTimer > 0) {
                this.capTimerSet = true;
            }
        }
        if ((maneuver.AP.way.curr().waypointType == 406) && this.capTimerSet) {
            for (int j = this.capFirstWP; j < (this.capLastWP + 1); ++j) {
                this.w.look_at_point(j).delayTimer = this.w.look_at_point(this.capFirstWP - 1).delayTimer + (int) (Time.current() / 60000L);
            }
            this.capTimerSet = false;
        }
        if ((maneuver.AP.way.curr().waypointType == 406) && (maneuver.AP.way.curr().delayTimer > 0) && (maneuver.AP.way.curr().delayTimer <= (int) (Time.current() / 60000L))) {
            this.w.setCur(this.capLastWP + 1);
            this.rejoinToGroup(this);
            maneuver.AP.way.setCur(this.capLastWP + 1);
            this.capCycles = 0;
            this.capFirstWP = 0;
            this.capLastWP = 0;
            this.capTimerSet = false;
        }
        if ((maneuver.AP.way.Cur() == this.capLastWP) && (this.capCycles > 0)) {
            this.w.setCur(this.capFirstWP);
            for (int k = this.capFirstWP; k < (this.capLastWP + 1); ++k) {
                Point3d p = this.w.look_at_point(k).getP();
                p.add(0.0, 0.0, this.w.look_at_point(this.capFirstWP - 1).altDifference);
                if (p.z < 100.0) {
                    p.z = 100.0;
                }
                this.w.look_at_point(k).set(p);
            }
            this.rejoinToGroup(this);
            maneuver.AP.way.setCur(this.capFirstWP);
            --this.capCycles;
            if (this.capCycles == 0) {
                this.capFirstWP = 0;
                this.capLastWP = 0;
                this.capTimerSet = false;
            }
        }
        if ((maneuver.AP.way.curr().waypointType == 407) && (this.grTask != 8)) {
            this.setGroupTask(8);
            this.airc[0].bSpotter = true;
        }
        if (this.leaderGroup != null) {
            if (this.leaderGroup.nOfAirc == 0) {
                this.detachGroup(this.leaderGroup);
            } else if (this.leaderGroup.airc[0] == null) {
                this.detachGroup(this.leaderGroup);
            } else if (this.leaderGroup.airc[0].FM.AP.way.isLanding()) {
                this.detachGroup(this.leaderGroup);
            } else {
                maneuver.AP.way.setCur(this.leaderGroup.w.Cur());
                if ((maneuver.get_maneuver() == 21) && !((Maneuver) this.leaderGroup.airc[0].FM).isBusy()) {
                    this.setTaskAndManeuver(0);
                }
            }
        }
        if ((this.grTask == 7) && (maneuver.isCrashedOnGround() || !maneuver.isOk() || ((maneuver.Loc.z - Engine.land().HQ_Air(maneuver.Loc.x, maneuver.Loc.y)) > 400.0))) {
            this.setGroupTask(1);
        }
        if (this.w == null) {
            this.w = new Way(maneuver.AP.way);
        }
        if (!maneuver.AP.way.isLanding() && maneuver.isOk()) {
            this.w.setCur(maneuver.AP.way.Cur());
        }
        if (!maneuver.AP.way.isLanding()) {
            for (int l = 1; l < this.nOfAirc; ++l) {
                if (!((Maneuver) this.airc[l].FM).AP.way.isLanding() && !((Maneuver) this.airc[l].FM).isBusy()) {
                    ((Maneuver) this.airc[l].FM).AP.way.setCur(this.w.Cur());
                }
            }
        }
        if (maneuver.AP.way.curr().isRadioSilence() || Mission.MDS_VARIABLES().zutiMisc_DisableAIRadioChatter /* || Main.cur().mission.zutiMisc_DisableAIRadioChatter */) {
            for (int n = 0; n < this.nOfAirc; ++n) {
                ((Maneuver) this.airc[n].FM).silence = true;
            }
        } else {
            for (int n2 = 0; n2 < this.nOfAirc; ++n2) {
                ((Maneuver) this.airc[n2].FM).silence = false;
            }
        }
        this.Pos.set(maneuver.Loc);
        if (this.formationType == -1) {
            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, true);
        }
        if (this.timeOutForTaskSwitch == 0) {
            if (this.airc[0] instanceof Mig_17) {
//                System.out.println("Action=" + this.w.curr().Action + ", grTask=" + this.grTask + ", target=" + (this.w.curr().getTarget() == null?"null":this.w.curr().getTarget().getClass().getName()) + ", enemyFighters=" + this.enemyFighters + ", length=" + AirGroupList.length(this.enemies[0]));
            }
            switch (this.w.curr().Action) {
                case 3: {
                    boolean b = (this.w.curr().getTarget() != null) || (this.airc[0] instanceof TypeFighter) || ((this.airc[0] instanceof TypeStormovik) && (!(this.airc[0] instanceof TypeBomber) || (this.airc[0].FM.getAltitude() < 2500.0f))) || (this.airc[0] instanceof D3A) || (this.airc[0] instanceof MXY_7) || (this.airc[0] instanceof JU_87);
                    if (this.grTask == 4) {
                        this.weWereInGAttack = true;
                        boolean somebodyGAttacks = this.somebodyGAttacks();
                        boolean b2 = false;
                        for (int n3 = 0; n3 < this.nOfAirc; ++n3) {
                            if (((Maneuver) this.airc[n3].FM).gattackCounter >= 7) {
                                b2 = true;
                            }
                        }
                        if (!somebodyGAttacks || b2 || this.gTargDestroyed) {
                            this.airc[0].FM.AP.way.next();
                            this.w.next();
                            this.setGroupTask(1);
                            for (int n4 = 1; n4 < this.nOfAirc; ++n4) {
                                Maneuver maneuver2 = (Maneuver) this.airc[n4].FM;
                                maneuver2.push(57);
                                maneuver2.pop();
                            }
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, true);
                            if (b2) {
                                for (int n5 = 0; n5 < this.nOfAirc; ++n5) {
                                    ((Maneuver) this.airc[n5].FM).gattackCounter = 0;
                                }
                            }
                        }
                    } else if (this.grTask == 3) {
                        this.switchWayPoint();
                        this.weWereInGAttack = true;
                        if (AirGroupList.length(this.enemies[0]) != this.oldEnemyNum) {
                            this.setGroupTask(3);
                        }
                        boolean somebodyAttacks = this.somebodyAttacks();
                        if (!somebodyAttacks || this.aTargDestroyed) {
                            this.setGroupTask(1);
                            if (!somebodyAttacks) {
                                this.timeOutForTaskSwitch = 90;
                            }
                            for (int n6 = 1; n6 < this.nOfAirc; ++n6) {
                                Maneuver maneuver3 = (Maneuver) this.airc[n6].FM;
                                if (!maneuver3.isBusy()) {
                                    maneuver3.push(57);
                                    maneuver3.pop();
                                }
                            }
                        }
                    }
                    if ((this.grTask != 1) || (this.w.curr().Action != 3)) {
                        break;
                    }
                    this.gTargWasFound = false;
                    this.gTargDestroyed = false;
                    this.gTargMode = 0;
                    if (!b) {
                        break;
                    }
                    this.setFormationAndScale((byte) 10, 8.0f, true);
                    if (maneuver.AP.getWayPointDistance() >= 5000.0f) {
                        break;
                    }
                    boolean b3 = false;
                    if (this.w.curr().getTarget() != null) {
                        this.setGTargMode(this.w.curr().getTarget());
                        if (this.gTargMode == 0) {
                            b3 = true;
                        } else {
                            maneuver.target_ground = this.setGAttackObject(0);
                            if ((maneuver.target_ground != null) && (maneuver.target_ground.distance(this.airc[0]) < 12000.0)) {
                                this.setGroupTask(4);
                                Voice.speakBeginGattack(this.airc[0]);
                            } else if (maneuver.AP.getWayPointDistance() < 1500.0f) {
                                b3 = true;
                            }
                        }
                    } else {
                        b3 = true;
                    }
                    if (!b3) {
                        break;
                    }
                    Point3d tmpP3d = AirGroup.tmpP3d;
                    double n7 = this.w.curr().x();
                    double n8 = this.w.curr().y();
                    Engine.land();
                    tmpP3d.set(n7, n8, Landscape.HQ(this.w.curr().x(), this.w.curr().y()));
                    this.setGTargMode(AirGroup.tmpP3d, 800.0f);
                    maneuver.target_ground = this.setGAttackObject(0);
                    if (maneuver.target_ground != null) {
                        this.setGroupTask(4);
                        Voice.speakBeginGattack(this.airc[0]);
                        break;
                    }
                    break;
                }
                case 0:
                case 2: {
                    if (this.grTask == 2) {
                        if (this.enemyFighters) {
                            int length = AirGroupList.length(this.enemies[0]);
                            for (int n9 = 0; n9 < length; ++n9) {
                                AirGroup group = AirGroupList.getGroup(this.enemies[0], n9);
//                                if (this.airc[0] instanceof F_86F) {
//                                    System.out.println("group.nOfAirc=" + group.nOfAirc + ", TypeFighter=" + (group.airc[0] instanceof TypeFighter));
//                                }
                                if ((group.nOfAirc > 0) && (group.airc[0] instanceof TypeFighter)) {
                                    this.bInitAttack = false;
                                    this.targetGroup = group;
                                    this.grTask = 3;
                                    this.setTaskAndManeuver(0);
                                    break;
                                }
                            }
                        }
                        if (this.w.Cur() >= (this.w.size() - 1)) {
                            this.setGroupTask(1);
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, true);
                        }
                        if ((this.clientGroup == null) || (this.clientGroup.nOfAirc == 0) || (this.clientGroup.w.Cur() >= (this.clientGroup.w.size() - 1)) || this.clientGroup.airc[0].FM.AP.way.isLanding()) {
                            maneuver.AP.way.next();
                            this.w.setCur(maneuver.AP.way.Cur());
                            for (int n10 = 1; n10 < this.nOfAirc; ++n10) {
                                ((Maneuver) this.airc[n10].FM).AP.way.setCur(this.w.Cur());
                            }
                            this.setGroupTask(1);
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, true);
                        }
                        this.switchWayPoint();
                        break;
                    }
                    if (this.grTask == 3) {
                        this.switchWayPoint();
                        this.weWereInGAttack = true;
                        if (AirGroupList.length(this.enemies[0]) != this.oldEnemyNum) {
                            this.setGroupTask(3);
                        }
                        boolean somebodyAttacks2 = this.somebodyAttacks();
                        if (!somebodyAttacks2 || this.aTargDestroyed) {
                            this.setGroupTask(1);
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, false);
                            if (!somebodyAttacks2) {
                                this.timeOutForTaskSwitch = 90;
                            }
                            for (int n11 = 1; n11 < this.nOfAirc; ++n11) {
                                Maneuver maneuver4 = (Maneuver) this.airc[n11].FM;
                                if (!maneuver4.isBusy()) {
                                    maneuver4.push(57);
                                    maneuver4.pop();
                                }
                            }
                            break;
                        }
                        break;
                    } else if (this.grTask == 4) {
                        this.weWereInGAttack = true;
                        boolean somebodyGAttacks2 = this.somebodyGAttacks();
                        boolean b4 = false;
                        for (int n12 = 0; n12 < this.nOfAirc; ++n12) {
                            if (((Maneuver) this.airc[n12].FM).gattackCounter >= 7) {
                                b4 = true;
                            }
                        }
                        if (somebodyGAttacks2 && !b4 && !this.gTargDestroyed) {
                            break;
                        }
                        this.setGroupTask(1);
                        this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, true);
                        for (int n13 = 1; n13 < this.nOfAirc; ++n13) {
                            Maneuver maneuver5 = (Maneuver) this.airc[n13].FM;
                            maneuver5.push(57);
                            maneuver5.pop();
                        }
                        if (b4) {
                            for (int n14 = 0; n14 < this.nOfAirc; ++n14) {
                                ((Maneuver) this.airc[n14].FM).gattackCounter = 0;
                            }
                            break;
                        }
                        break;
                    } else if (this.grTask == 8) {
                        if (maneuver.target_ground == null) {
                            Point3d tmpP3d2 = AirGroup.tmpP3d;
                            double n15 = this.w.curr().x();
                            double n16 = this.w.curr().y();
                            Engine.land();
                            tmpP3d2.set(n15, n16, Landscape.HQ(this.w.curr().x(), this.w.curr().y()));
                            maneuver.target_ground = NearestEnemies.getAFoundEnemy(AirGroup.tmpP3d, 800.0, maneuver.actor.getArmy());
                            break;
                        }
                        break;
                    } else {
                        if (this.grTask != 1) {
                            break;
                        }
                        if (this.weWereInGAttack || (this.gTargMode != 0)) {
                            this.weWereInGAttack = false;
                            this.gTargMode = 0;
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, false);
                            ((Maneuver) this.airc[0].FM).WeWereInGAttack = true;
                        }
                        if (this.weWereInAttack) {
                            this.weWereInAttack = false;
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, false);
                            ((Maneuver) this.airc[0].FM).WeWereInAttack = true;
                        }
                        if ((this.w.Cur() > 0) && (this.grAttached == 0) && (this.oldFType == 0)) {
                            this.w.curr().getP(AirGroup.tmpP);
                            AirGroup.tmpV.sub(AirGroup.tmpP, this.Pos);
                            this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 1.0f, true);
                        }
                        int cur = this.w.Cur();
                        this.w.next();
                        if ((this.w.curr().Action == 2) || ((this.w.curr().Action == 3) && (((this.w.curr().getTarget() != null) && !(this.airc[0] instanceof Scheme4)) || (this.airc[0] instanceof TypeStormovik) || (this.airc[0] instanceof JU_87)))) {
                            this.w.curr().getP(AirGroup.tmpP);
                            AirGroup.tmpV.sub(AirGroup.tmpP, this.Pos);
                            if ((float) AirGroup.tmpV.length() < 20000.0f) {
                                if (this.w.curr().waypointType == 104) {
                                    this.setFormationAndScale((byte) 11, 8.0f, true);
                                } else if (this.airc[0].FM.getAltitude() > 1200.0f) {
                                    this.setFormationAndScale((byte) 10, 8.0f, true);
                                }
                            }
                        }
                        this.w.setCur(cur);
                        if (this.w.curr().getTarget() != null) {
                            Actor targetActorRandom = this.w.curr().getTargetActorRandom();
                            if (targetActorRandom instanceof Aircraft) {
                                AirGroup.tmpV.sub(((Aircraft) targetActorRandom).FM.Loc, this.Pos);
//                                System.out.println("tmpV.lengthSquared=" + AirGroup.tmpV.lengthSquared() + ", w.Cur()=" + this.w.Cur() + ", w.size()=" + this.w.size() + ", maneuver.hasBombs()=" + maneuver.hasBombs() + ", maneuver.hasRockets()=" + maneuver.hasRockets());
                                if (AirGroup.tmpV.lengthSquared() < 1.44E8) {
                                    if (targetActorRandom.getArmy() == this.airc[0].getArmy()) {
                                        // TODO: Fixed by SAS~Storebror: Fighters with Air-To-Air Missiles/Rockets can escort other aircraft!
                                        if ((this.airc[0] instanceof TypeFighter) && !maneuver.hasBombs() && !maneuver.hasGroundAttackRockets()) {
//                                        if ((this.airc[0] instanceof TypeFighter) && !maneuver.hasBombs() && !maneuver.hasRockets()) {
                                            if (this.w.Cur() < (this.w.size() - 2)) {
                                                this.clientGroup = ((Maneuver) ((Aircraft) targetActorRandom).FM).Group;
                                                this.setGroupTask(2);
                                                this.setFormationAndScale((byte) maneuver.AP.way.curr().formation, 2.5f, true);
                                            }
                                        } else {
                                            this.attachGroup(((Maneuver) ((Aircraft) targetActorRandom).FM).Group);
                                        }
                                    } else if ((this.airc[0] instanceof TypeFighter) || (this.airc[0] instanceof TypeStormovik)) {
                                        this.targetGroup = ((Maneuver) ((Aircraft) targetActorRandom).FM).Group;
                                        this.setGroupTask(3);
                                    }
                                }
                            }
                        } else if (AirGroupList.length(this.enemies[0]) > 0) {
//                            if (this.airc[0] instanceof Mig_17) System.out.println("01...");
                            int n17 = 0;
                            if (this.airc[0] instanceof TypeStormovik) {
                                for (int length2 = AirGroupList.length(this.enemies[0]), n18 = 0; n18 < length2; ++n18) {
                                    AirGroup group2 = AirGroupList.getGroup(this.enemies[0], n18);
                                    if ((group2 != null) && (group2.nOfAirc != 0) && !(group2.airc[0] instanceof TypeFighter)) {
                                        n17 = 1;
                                        this.targetGroup = group2;
                                        break;
                                    }
                                }
                                if (n17 != 0) {
                                    if (maneuver.hasBombs()) {
                                        n17 = 0;
                                    }
                                    int cur2 = this.w.Cur();
                                    while ((n17 != 0) && (this.w.Cur() < (this.w.size() - 1))) {
                                        if (this.w.curr().Action == 3) {
                                            n17 = 0;
                                        }
                                        this.w.next();
                                    }
                                    this.w.setCur(cur2);
                                }
                            }
//                            if (this.airc[0] instanceof Mig_17) System.out.println("02...");
                            if ((n17 == 0) && (this.airc[0] instanceof TypeFighter)) {
//                                if (this.airc[0] instanceof Mig_17) System.out.println("03...");
                                for (int n19 = 0; n19 < this.nOfAirc; ++n19) {
//                                    if (this.airc[0] instanceof Mig_17) System.out.println("airc[" + n19 + "] canAttack()=" + ((Maneuver) this.airc[n19].FM).canAttack());
                                    if (((Maneuver) this.airc[n19].FM).canAttack()) {
                                        n17 = 1;
                                    }
                                }
                                // +++ TODO: By SAS~Storebror: When deciding whether or not to attack as a fighter, take bombs/torps into account only!
                                //                            if (n17 != 0 && maneuver.CT.getWeaponMass() > 220.0f) n17 = 0;
                                if ((n17 != 0) && (maneuver.CT.getBombMass() > 220.0f)) {
//                                    if (this.airc[0] instanceof Mig_17) System.out.println("getBombMass()=" + maneuver.CT.getBombMass());
                                    n17 = 0;
                                    // ---
                                }
                            }
                            if (n17 != 0) {
//                                if (this.airc[0] instanceof Mig_17) System.out.println("04...");
                                this.setGroupTask(3);
                            }
                        }
                        if (this.rejoinGroup != null) {
                            this.rejoinToGroup(this.rejoinGroup);
                            break;
                        }
                        break;
                    }
                    //                    break;
                }
                default: {
                    this.grTask = 1;
                    break;
                }
            }
        }
        this.oldEnemyNum = AirGroupList.length(this.enemies[0]);
        if (this.timeOutForTaskSwitch > 0) {
            --this.timeOutForTaskSwitch;
        }
    }

    public void setEnemyConvPoint(FlightModel flightModel, Actor actor) {
        Point3d point3d = new Point3d();
        point3d.set(flightModel.Loc);
        this.convPoint.put(flightModel, new Object[] { point3d, actor, new Long(Time.current()) });
    }

    public Point3d getEnemyConvPoint(FlightModel flightmodel, Actor actor) {
        this.cleanConvPointMap();
        Object obj = this.convPoint.get(flightmodel);
        if (obj != null) {
            Object aobj[] = (Object[]) obj;
            if (aobj[1] == actor) {
                return null;
            } else {
                return (Point3d) aobj[0];
            }
        } else {
            return null;
        }
    }

    private void cleanConvPointMap() {
        Set set = this.convPoint.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            Object obj1 = this.convPoint.get(obj);
            Object aobj[] = (Object[]) obj1;
            long l = ((Long) aobj[2]).longValue();
            if ((Time.current() - l) > 60000L) {
                iterator.remove();
            }
        }

    }

    static {
        AirGroup.point3dWP = new Point3d();
        AirGroup.point3dNewWP = new Point3d();
        AirGroup.pfcf = new Point3f();
        AirGroup.GTList = new String[] { "NO_TASK.", "FLY_WAYPOINT", "DEFENDING", "ATTACK_AIR", "ATTACK_GROUND", "TAKEOFF", "LANDING", "TAKEOFF_AND_HANG_ON", "ARTILLERY_SPOTTING" };
        AirGroup.xtriangle = new float[] { 577.35f, -288.675f, -288.675f, 577.35f };
        AirGroup.ytriangle = new float[] { 0.0f, 500.0f, -500.0f, 0.0f };
        AirGroup.xr = new float[] { 707.11f, 0.0f, -707.11f, 0.0f, 707.11f };
        AirGroup.yr = new float[] { 0.0f, 707.11f, 0.0f, -707.11f, 0.0f };
        AirGroup.xh = new float[] { 1000.0f, 500.0f, -500.0f, -1000.0f, -500.0f, 500.0f, 1000.0f };
        AirGroup.yh = new float[] { -0.0f, 866.025f, 866.025f, 0.0f, -866.025f, -866.025f, 0.0f };
        AirGroup.xp = new float[] { 850.651f, 262.866f, -688.191f, -688.191f, 262.866f, 850.651f };
        AirGroup.yp = new float[] { 0.0f, 809.017f, 500.0f, -500.0f, -809.017f, 0.0f };
        AirGroup.tmpV = new Vector3d();
        AirGroup.tmpV1 = new Vector3d();
        AirGroup.tmpP = new Point3d();
        AirGroup.tmpP3d = new Point3d();
        AirGroup.P1P2vector = new Vector2d();
        AirGroup.norm1 = new Vector2d();
        AirGroup.myPoint = new Vector2d();
    }

    // TODO: |ZUTI| variables
    // --------------------------------------
    public boolean zutiIsDogfightGroup = false;
    // --------------------------------------
}
