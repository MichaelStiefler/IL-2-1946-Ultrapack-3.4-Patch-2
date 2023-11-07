package com.maddox.il2.ai.air;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.maddox.JGP.Point2f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector2d;
import com.maddox.JGP.Vector2f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.AirportCarrier;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.Formation;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.VisCheck;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ZutiSupportMethods_AI;
import com.maddox.il2.ai.ground.TgtShip;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.GunGeneric;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.TextScr;
import com.maddox.il2.fm.AIFlightModel;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.FMMath;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.DServer;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.objects.ActorCrater;
import com.maddox.il2.objects.air.AR_234;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.BI_1;
import com.maddox.il2.objects.air.BI_6;
import com.maddox.il2.objects.air.HE_LERCHE3;
import com.maddox.il2.objects.air.JU_88NEW;
import com.maddox.il2.objects.air.KI_46_OTSUHEI;
import com.maddox.il2.objects.air.ME_163B1A;
import com.maddox.il2.objects.air.MXY_7;
import com.maddox.il2.objects.air.Mig_17PF;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.SM79;
import com.maddox.il2.objects.air.Scheme4;
import com.maddox.il2.objects.air.Swordfish;
import com.maddox.il2.objects.air.TA_152C;
import com.maddox.il2.objects.air.TA_183;
import com.maddox.il2.objects.air.TypeAcePlane;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeDiveBomber;
import com.maddox.il2.objects.air.TypeDockable;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeGlider;
import com.maddox.il2.objects.air.TypeGuidedBombCarrier;
import com.maddox.il2.objects.air.TypeHasToKG;
import com.maddox.il2.objects.air.TypeJazzPlayer;
import com.maddox.il2.objects.air.TypeSailPlane;
import com.maddox.il2.objects.air.TypeStormovik;
import com.maddox.il2.objects.air.TypeSupersonic;
import com.maddox.il2.objects.air.TypeTransport;
import com.maddox.il2.objects.air.TypeX4Carrier;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.TestRunway;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.BombGunNull;
import com.maddox.il2.objects.weapons.BombGunPara;
import com.maddox.il2.objects.weapons.BombGunTorp45_36AV_A;
import com.maddox.il2.objects.weapons.MissileGun;
import com.maddox.il2.objects.weapons.ParaTorpedoGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.il2.objects.weapons.RocketGunNull;
import com.maddox.il2.objects.weapons.RocketGunX4;
import com.maddox.il2.objects.weapons.ToKGUtils;
import com.maddox.il2.objects.weapons.TorpedoGun;
import com.maddox.rts.Finger;
import com.maddox.rts.MsgDestroy;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetHost;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Reflection;

public class Maneuver extends AIFlightModel {
    private int                task;
    private int                maneuver                    = 26;
    protected float            mn_time;
    private int[]              program                     = new int[8];
    private boolean            bBusy                       = true;
    public boolean             wasBusy                     = true;
    public boolean             dontSwitch                  = false;
    public boolean             aggressiveWingman           = false;
    public boolean             kamikaze                    = false;
    public boolean             silence                     = true;
    public boolean             bombsOut;
    private int                bombsOutCounter             = 0;
    public float               direction;
    public Loc                 rwLoc;
    private boolean            first                       = true;
    private int                rocketsDelay                = 0;
    private int                bulletDelay                 = 0;
    private int                submanDelay                 = 0;
    private float              maxG;
    private float              maxAOA;
    private float              LandHQ;
    public float               Alt                         = 0.0F;
    private float              corrCoeff                   = 0.0F;
    private float              corrElev                    = 0.0F;
    private float              corrAile                    = 0.0F;
    private boolean            checkGround                 = false;
    private boolean            checkStrike                 = false;
    private boolean            frequentControl             = false;
    private boolean            stallable                   = false;
    public FlightModel         airClient                   = null;
    public FlightModel         target                      = null;
    public FlightModel         danger                      = null;
    private float              dangerAggressiveness        = 0.0F;
    private float              oldDanCoeff                 = 0.0F;
    private int                shotAtFriend                = 0;
    private float              distToFriend                = 0.0F;
    public Actor               target_ground               = null;
    public AirGroup            Group;
    protected boolean          TaxiMode                    = false;
    protected boolean          finished                    = false;
    protected boolean          canTakeoff                  = false;
    public Point_Any           wayCurPos;
    protected Point_Any        wayPrevPos;
    protected Point_Any[]      airdromeWay;
    protected int              curAirdromePoi              = 0;
    public long                actionTimerStart            = 0L;
    public long                actionTimerStop             = 0L;
    protected int              gattackCounter              = 0;
    private int                beNearOffsPhase             = 0;
    public int                 submaneuver                 = 0;
    private boolean            dont_change_subm            = false;
    private int                tmpi                        = 0;
    private int                sub_Man_Count               = 0;
    private float              dA;
    private float              dist                        = 0.0F;
    private float              man1Dist                    = 50.0F;
    private float              bullTime                    = 0.0015F;
    protected static final int STAY_ON_THE_TAIL            = 1;
    protected static final int NOT_TOO_FAST                = 2;
    protected static final int FROM_WAYPOINT               = 3;
    protected static final int CONST_SPEED                 = 4;
    protected static final int MIN_SPEED                   = 5;
    protected static final int MAX_SPEED                   = 6;
    protected static final int CONST_POWER                 = 7;
    protected static final int ZERO_POWER                  = 8;
    protected static final int BOOST_ON                    = 9;
    protected static final int FOLLOW_WITHOUT_FLAPS        = 10;
    protected int              speedMode                   = 3;
    protected float            smConstSpeed                = 90.0F;
    protected float            smConstPower                = 0.7F;
    protected FlightModel      tailForStaying              = null;
    public Vector3d            tailOffset                  = new Vector3d();
    protected int              Speak5minutes;
    protected int              Speak1minute;
    protected int              SpeakBeginGattack;
    protected boolean          WeWereInGAttack             = false;
    protected boolean          WeWereInAttack              = false;
    protected boolean          SpeakMissionAccomplished    = false;
    public static final int    ROOKIE                      = 0;
    public static final int    NORMAL                      = 1;
    public static final int    VETERAN                     = 2;
    public static final int    ACE                         = 3;
    public static final int    NO_TASK                     = 0;
    public static final int    WAIT                        = 1;
    public static final int    STAY_FORMATION              = 2;
    public static final int    FLY_WAYPOINT                = 3;
    public static final int    DEFENCE                     = 4;
    public static final int    DEFENDING                   = 5;
    public static final int    ATTACK_AIR                  = 6;
    public static final int    ATTACK_GROUND               = 7;
    public static final int    NONE                        = 0;
    public static final int    HOLD                        = 1;
    public static final int    PULL_UP                     = 2;
    public static final int    LEVEL_PLANE                 = 3;
    public static final int    ROLL                        = 4;
    public static final int    ROLL_90                     = 5;
    public static final int    ROLL_180                    = 6;
    public static final int    SPIRAL_BRAKE                = 7;
    public static final int    SPIRAL_UP                   = 8;
    public static final int    SPIRAL_DOWN                 = 9;
    public static final int    CLIMB                       = 10;
    public static final int    DIVING_0_RPM                = 11;
    public static final int    DIVING_30_DEG               = 12;
    public static final int    DIVING_45_DEG               = 13;
    public static final int    TURN                        = 14;
    public static final int    MIL_TURN                    = 15;
    public static final int    LOOP                        = 16;
    public static final int    LOOP_DOWN                   = 17;
    public static final int    HALF_LOOP_UP                = 18;
    public static final int    HALF_LOOP_DOWN              = 19;
    public static final int    STALL                       = 20;
    public static final int    WAYPOINT                    = 21;
    public static final int    SPEEDUP                     = 22;
    public static final int    BELL                        = 23;
    public static final int    FOLLOW                      = 24;
    public static final int    LANDING                     = 25;
    public static final int    TAKEOFF                     = 26;
    public static final int    ATTACK                      = 27;
    public static final int    WAVEOUT                     = 28;
    public static final int    SINUS                       = 29;
    public static final int    ZIGZAG_UP                   = 30;
    public static final int    ZIGZAG_DOWN                 = 31;
    public static final int    ZIGZAG_SPIT                 = 32;
    public static final int    HALF_LOOP_DOWN_135          = 33;
    public static final int    HARTMANN_REDOUT             = 34;
    public static final int    ROLL_360                    = 35;
    public static final int    STALL_POKRYSHKIN            = 36;
    public static final int    BARREL_POKRYSHKIN           = 37;
    public static final int    SLIDE_LEVEL                 = 38;
    public static final int    SLIDE_DESCENT               = 39;
    public static final int    RANVERSMAN                  = 40;
    public static final int    CUBAN                       = 41;
    public static final int    CUBAN_INVERT                = 42;
    public static final int    GATTACK                     = 43;
    public static final int    PILOT_DEAD                  = 44;
    public static final int    HANG_ON                     = 45;
    public static final int    DELAY                       = 48;
    public static final int    EMERGENCY_LANDING           = 49;
    public static final int    GATTACK_DIVE                = 50;
    public static final int    GATTACK_TORPEDO             = 51;
    public static final int    GATTACK_CASSETTE            = 52;
    public static final int    GATTACK_KAMIKAZE            = 46;
    public static final int    GATTACK_TINYTIM             = 47;
    public static final int    FAR_FOLLOW                  = 53;
    public static final int    SPIRAL_DOWN_SLOW            = 54;
    public static final int    FOLLOW_SPIRAL_UP            = 55;
    public static final int    SINUS_SHALLOW               = 56;
    public static final int    GAIN                        = 57;
    public static final int    SEPARATE                    = 58;
    public static final int    BE_NEAR                     = 59;
    public static final int    EVADE_UP                    = 60;
    public static final int    EVADE_DN                    = 61;
    public static final int    ENERGY_ATTACK               = 62;
    public static final int    ATTACK_BOMBER               = 63;
    public static final int    PARKED_STARTUP              = 64;
    public static final int    COVER                       = 65;
    public static final int    TAXI                        = 66;
    public static final int    RUN_AWAY                    = 67;
    public static final int    FAR_COVER                   = 68;
    public static final int    TAKEOFF_VTOL_A              = 69;
    public static final int    LANDING_VTOL_A              = 70;
    public static final int    GATTACK_HS293               = 71;
    public static final int    GATTACK_FRITZX              = 72;
    public static final int    GATTACK_TORPEDO_TOKG        = 73;
    public static final int    COVER_DRAG_AND_BAG          = 74;
    public static final int    ATTACK_FROM_PLAYER          = 75;
    public static final int    COVER_AGRESSIVE             = 76;
    public static final int    TURN_HARD                   = 77;
    public static final int    CLOUDS                      = 78;
    public static final int    EVADE_ATTACK                = 79;
    public static final int    BRACKET_ATTACK              = 80;
    public static final int    DOUBLE_ATTACK               = 81;
    public static final int    BE_NEAR_LOW                 = 82;
    public static final int    BARREL_ROLL                 = 83;
    public static final int    PULL_UP_EMERGENCY           = 84;
    public static final int    DIVING_90_DEG               = 85;
    public static final int    SMOOTH_LEVEL                = 86;
    public static final int    IVAN                        = 87;
    public static final int    FISHTAIL_LEFT               = 88;
    public static final int    FISHTAIL_RIGHT              = 89;
    public static final int    LOOKDOWN_LEFT               = 90;
    public static final int    LOOKDOWN_RIGHT              = 91;
    public static final int    LINE_ATTACK                 = 92;
    public static final int    BOX_ATTACK                  = 93;
    public static final int    BANG_BANG                   = 94;
    public static final int    PANIC_MANIC                 = 95;
    public static final int    PANIC_FREEZE                = 96;
    public static final int    COMBAT_CLIMB                = 97;
    public static final int    HIT_AND_RUN                 = 98;
    public static final int    SEEK_AND_DESTROY            = 99;
    public static final int    BREAK_AWAY                  = 100;
    public static final int    ATTACK_HARD                 = 101;
    public static final int    TAXI_TO_TO                  = 102;
    public static final int    MIL_TURN_LEFT               = 103;
    public static final int    MIL_TURN_RIGHT              = 104;
    public static final int    STRAIGHT_AND_LEVEL          = 105;
    public static final int    ART_SPOT                    = 106;

    public static final int    WVSF_RESET                  = 0;
    public static final int    WVSF_BOOM_ZOOM              = 1;
    public static final int    WVSF_FROM_AHEAD             = 2;
    public static final int    WVSF_FROM_BELOW             = 3;
    public static final int    WVSF_AS_IT_IS               = 4;
    public static final int    WVSF_FROM_TAIL              = 5;
    public static final int    WVSF_SHALLOW_DIVE           = 6;
    public static final int    WVSF_FROM_BOTTOM            = 7;
    public static final int    WVSF_FROM_LEFT              = 8;
    public static final int    WVSF_FROM_RIGHT             = 9;

    private static final int   SUB_MAN0                    = 0;
    private static final int   SUB_MAN1                    = 1;
    private static final int   SUB_MAN2                    = 2;
    private static final int   SUB_MAN3                    = 3;
    private static final int   SUB_MAN4                    = 4;
    private static final int   SUB_MAN5                    = 5;
    private static final int   SUB_MAN69                   = 69;
    public static final int    LIVE                        = 0;
    public static final int    RETURN                      = 1;
    public static final int    TASK                        = 2;
    public static final int    PROTECT_LEADER              = 3;
    public static final int    PROTECT_WINGMAN             = 4;
    public static final int    PROTECT_FRIENDS             = 5;
    public static final int    DESTROY_ENEMIES             = 6;
    public static final int    KEEP_ORDER                  = 7;
    public float[]             takeIntoAccount             = new float[8];
    public float[]             AccountCoeff                = new float[8];
    public static final int    FVSB_BOOM_ZOOM              = 0;
    public static final int    FVSB_BOOM_ZOOM_TO_ENGINE    = 1;
    public static final int    FVSB_SHALLOW_DIVE_TO_ENGINE = 2;
    public static final int    FVSB_FROM_AHEAD             = 3;
    public static final int    FVSB_FROM_BELOW             = 4;
    public static final int    FVSB_AS_IT_IS               = 5;
    public static final int    FVSB_FROM_SIDE              = 6;
    public static final int    FVSB_FROM_TAIL_TO_ENGINE    = 7;
    public static final int    FVSB_FROM_TAIL              = 8;
    public static final int    FVSB_SHALLOW_DIVE           = 9;
    public static final int    FVSB_FROM_BOTTOM            = 10;
    private Vector3d           ApproachV                   = new Vector3d();
    private Vector3d           TargV                       = new Vector3d();
    private Vector3d           TargDevV                    = new Vector3d(0.0, 0.0, 0.0);
    private Vector3d           TargDevVnew                 = new Vector3d(0.0, 0.0, 0.0);
    private Vector3d           scaledApproachV             = new Vector3d();
    private float              ApproachR;
    private float              TargY;
    private float              TargZ;
    private float              TargYS;
    private float              TargZS;
    private float              RandomVal;
    public Vector3d            followOffset                = new Vector3d();
    private Vector3d           followTargShift             = new Vector3d(0.0, 0.0, 0.0);
    private Vector3d           followCurShift              = new Vector3d(0.0, 0.0, 0.0);
    private float              raAilShift                  = 0.0F;
    private float              raElevShift                 = 0.0F;
    private float              raRudShift                  = 0.0F;
    private float              sinKren                     = 0.0F;
    private boolean            strikeEmer                  = false;
    private FlightModel        strikeTarg                  = null;
    private boolean            direc                       = false;
    float                      Kmax                        = 10.0F;
    float                      rmin;
    float                      rmax;
    double                     phase                       = 0.0;
    double                     radius                      = 50.0;
    int                        pointQuality                = -1;
    int                        curPointQuality             = 50;
    private static Vector3d    tmpV3d                      = new Vector3d();
    private static Vector3d    tmpV3f                      = new Vector3d();
    public static Orient       tmpOr                       = new Orient();
    public Orient              saveOr                      = new Orient();
    private static Point3d     Po                          = new Point3d();
    private static Point3d     Pc                          = new Point3d();
    private static Point3d     Pd                          = new Point3d();
    // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
    public static Vector3d     Ve                          = new Vector3d();
    // ---
    private Vector3d           oldVe                       = new Vector3d();
    private Vector3d           Vtarg                       = new Vector3d();
    private Vector3d           constVtarg                  = new Vector3d();
    private Vector3d           constVtarg1                 = new Vector3d();
    private static Vector3d    Vf                          = new Vector3d();
    private Vector3d           Vxy                         = new Vector3d();
    private static Vector3d    Vpl                         = new Vector3d();
    private AnglesFork         AFo                         = new AnglesFork();
//    private float[]            headPos                     = new float[3];
//    private float[]            headOr                      = new float[3];
    private static Point3d     P                           = new Point3d();
    private static Point2f     Pcur                        = new Point2f();
    private static Vector2d    Vcur                        = new Vector2d();
    private static Vector2f    V_to                        = new Vector2f();
    private static Vector2d    Vdiff                       = new Vector2d();
    private static Loc         elLoc                       = new Loc();
    public static boolean      showFM;
    // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
    public float               pilotHeadT                  = 0.0F;
    public float               pilotHeadY                  = 0.0F;
    // ---
    Vector3d                   windV                       = new Vector3d();

    // TODO: CTO Mod
    // -------------------------------
    private boolean bStage3;
    private boolean bStage4;
    private boolean bStage6;
    private boolean bStage7;
    private boolean bAlreadyCheckedStage7;
    private float   fNearestDistance;
    private boolean bCatapultAI;
    private boolean bNoNavLightsAI;
    private boolean bFastLaunchAI;
    // -------------------------------

    // TODO: GATTACK
    // ---------------------------------
    private int passCounter = 0;

    // --------------------------------
    // @formatter:on

    // TODO: +++ TD AI code backport from 4.13 +++
    public boolean           bKeepOrdnance;
    Vector3d                 spreadV3d;
    public int               bracketSide    = 0;
    public int               wAttType;

    private FlightModel      oldTarget;
    private FlightModel      oldLeader;
    private Vector2f         V_taxiLeg;
    protected List           taxiToTakeOffWay;
    private int              taxiToTakeOffWayLength;

    private Vector3d         tempV;
    private boolean          targetLost;
    private Point3d          lastKnownTargetLoc;
    private long             timeToSearchTarget;
    private Point3d          tempPoint;
    private Vector3d         losVector;
    private Vector3d         corrVector;
    private Vector3d         wanderVector;
    private Vector3d         wanderVectorNew;
    private float            shootingDeviation;
    private float            sp;
    private int              followType;
    Vector3d                 tmpV3dToDanger;
    long                     lookAroundTime;
    public static AnglesFork steerAngleFork = new AnglesFork();
    private float            Wo;
    float                    cloudHeight;
    public boolean           bGentsStartYourEngines;
    float                    desiredAlt;
    float                    oldError;
    float                    errorAlt;
    float                    cCoeff;
    float                    koeff;
    public boolean           bSlowDown;
    public static Vector3d   vTemp          = new Vector3d();
    public Loc               tLoc;
    public static Point3d    tPoint         = new Point3d();
    private static Point3d   tmpLoc         = new Point3d();
    private static HashMap   collisionMap   = new HashMap();
    private Actor            ignoredActor;
    private Actor            collisionDangerActor;
    private float            distToTaxiPoint;
    public float             oldAOA;
    // TODO: --- TD AI code backport from 4.13 ---

    // TODO: +++ UP3 Patch Pack Dogfight Server Distribution Control +++
    private static long          lastHostCheck      = 0L;
    private static final long    HOSTCHECK_INTERVAL = 300000L;
// private static final long HOSTCHECK_INTERVAL = 1000L;
    private static long          hostCheckDiff      = Long.MIN_VALUE;
    private static Object        syncObject         = new Object();
    private static DecimalFormat df                 = new DecimalFormat("0.##");
    // TODO: --- UP3 Patch Pack Dogfight Server Distribution Control ---

    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    public boolean triggerTakeOff;
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
    
    // TODO: Cheater Protection
    public int originalSkill = -1;

    public void checkCheater() {
        if (!AircraftState.isDServer) return;
        if (this.originalSkill == -1) {
            this.originalSkill = this.Skill;
            AircraftState.printCheaterDebug(this.actor.name() + " (" + this.actor.getClass().getName() + ") Init Skill = " + this.skillToString(this.Skill));
        }
        boolean fightAgainstCheater = false;
        String cheaterNames = "";
//        System.out.println("this.danger = " + this.danger + ", this.target = " + this.target);

        if (this.danger != null && this.danger.actor != null) {
            fightAgainstCheater |= AircraftState.isCheaterFightAces(this.danger.actor);
            String dangerName = AircraftState.netUserName(this.danger.actor);
//            System.out.println("dangerName = " + dangerName);
            if (dangerName != null) cheaterNames = dangerName;
        }
        if (this.target != null && this.target.actor != null) {
            fightAgainstCheater |= AircraftState.isCheaterFightAces(this.target.actor);
            String targetName = AircraftState.netUserName(this.target.actor);
//            System.out.println("targetName = " + targetName);
            if (targetName != null && !targetName.equals(cheaterNames)) {
                if (cheaterNames.length() > 0) cheaterNames += " & ";
                cheaterNames += AircraftState.netUserName(this.target.actor);
            }
        }
        if (fightAgainstCheater && this.Skill == ACE) return;
        if (!fightAgainstCheater && this.Skill == this.originalSkill) return;
        int oldSkill = this.Skill;
        this.setSkill(fightAgainstCheater ? ACE : this.originalSkill);
        AircraftState.printCheaterDebug(this.actor.name() + " (" + this.actor.getClass().getName() + ") is " + (fightAgainstCheater ? "" : "not ") + "fighting Cheater " + cheaterNames + ", Skill changed from " + this.skillToString(oldSkill) + " to "
                + this.skillToString(this.Skill) + ".");
    }

    public String skillToString(int skill) {
        if (skill == ROOKIE) return "rookie";
        if (skill == NORMAL) return "normal";
        if (skill == VETERAN) return "veteran";
        if (skill == ACE) return "ace";
        return "unknown";
    }
    // ---

    public void set_task(int i) {
        this.task = i;
    }

    public int get_task() {
        return this.task;
    }

    public int get_maneuver() {
        return this.maneuver;
    }

    public int getmaneuver() {
        return this.maneuver;
    }

    public void set_maneuver(int i) {
        if (this.maneuver == 84) return;
        if ((i == 21 || i == 24) && (this.maneuver == 88 || this.maneuver == 89 || this.maneuver == 90 || this.maneuver == 91)) return;
        // TODO: +++ Cloud visibility fix +++
        if ((i == 27 || i == 6 || i == 63 || i == 62) && (VisCheck.isVisibilityBlockedByClouds(this, this.target, false) || VisCheck.isVisibilityBlockedByDarkness(this, this.target)))
            return;

        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.maneuver != 44 && (i == 44 || this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 66 && this.maneuver != 46 || this.maneuver == 95)) {
            // TODO: --- TD AI code backport from 4.13 ---
            int j = this.maneuver;
            this.maneuver = i;
            if (j != this.maneuver) this.set_flags();
        }
    }

    public void pop() {
        if (this.maneuver != 44 && (this.program[0] == 44 || this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 66 && this.maneuver != 46)) {
            int i = this.maneuver;
            this.maneuver = this.program[0];
            for (int j = 0; j < this.program.length - 1; j++)
                this.program[j] = this.program[j + 1];
            this.program[this.program.length - 1] = 0;
            if (i != this.maneuver) this.set_flags();
        }
    }

    public void unblock() {
        this.maneuver = 0;
    }

    public void safe_set_maneuver(int i) {
        this.dont_change_subm = true;
        this.set_maneuver(i);
        this.dont_change_subm = true;
    }

    public void safe_pop() {
        this.dont_change_subm = true;
        this.pop();
        this.dont_change_subm = true;
    }

    public void clear_stack() {
        for (int i = 0; i < this.program.length; i++)
            this.program[i] = 0;
    }

    public void push(int i) {
        for (int j = this.program.length - 2; j >= 0; j--)
            this.program[j + 1] = this.program[j];
        this.program[0] = i;
    }

    public void push() {
        this.push(this.maneuver);
    }

    public void accurate_set_task_maneuver(int i, int j) {
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.maneuver != 44 && this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 64 || this.maneuver == TAXI_TO_TO) {
            // TODO: --- TD AI code backport from 4.13 ---
            this.set_task(i);
            if (this.maneuver != j) {
                this.clear_stack();
                this.set_maneuver(j);
            }
        }
    }

    public void accurate_set_FOLLOW() {
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.maneuver != 44 && this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 64 || this.maneuver == TAXI_TO_TO) {
            // TODO: --- TD AI code backport from 4.13 ---
            this.set_task(2);
            if (this.maneuver != 24 && this.maneuver != 53) {
                this.clear_stack();
                this.set_maneuver(24);
            }
        }
    }

    public void setBusy(boolean flag) {
        this.bBusy = flag;
    }

    public boolean isBusy() {
        return this.bBusy;
    }

    public void setSpeedMode(int i) {
        this.speedMode = i;
    }

    private boolean isStallable() {
        if (this.actor instanceof TypeStormovik) return false;
        return !(this.actor instanceof TypeTransport);
    }

    private void resetControls() {
        this.CT.AileronControl = this.CT.BrakeControl = this.CT.ElevatorControl = this.CT.FlapsControl = this.CT.RudderControl = 0.0F;
        this.CT.AirBrakeControl = 0.0F;
    }

    private void set_flags() {
        if (World.cur().isDebugFM()) this.printDebugFM();
        this.AP.setStabAll(false);
        this.mn_time = 0.0F;
        this.minElevCoeff = 4F;
        this.shootingDeviation = 6F;
        this.sp = this.nShootingPoint(this.Skill);
        this.shootingPoint = this.sp;
        this.wanderVector.set(World.Rnd().nextFloat(-1F, 1.0F), World.Rnd().nextFloat(-1F, 1.0F), World.Rnd().nextFloat(-1F, 1.0F));
        if (!this.dont_change_subm) {
            this.setSpeedMode(3);
            this.first = true;
            this.submaneuver = 0;
            this.sub_Man_Count = 0;
        }
        this.dont_change_subm = false;
        if (this.maneuver != 48 && this.maneuver != 0 && this.maneuver != 26 && this.maneuver != 64 && this.maneuver != TAXI_TO_TO && this.maneuver != 44) this.resetControls();
        this.setCheckGround(this.maneuver != 20 && this.maneuver != 25 && this.maneuver != TAXI_TO_TO && this.maneuver != 1 && this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 44 && this.maneuver != 49 && this.maneuver != 43
                && this.maneuver != 50 && this.maneuver != 51 && this.maneuver != 73 && this.maneuver != 46 && this.maneuver != 84 && this.maneuver != 64 && this.maneuver != 95 && this.maneuver != BREAK_AWAY);
        this.frequentControl = this.maneuver == 24 || this.maneuver == 53 || this.maneuver == 68 || this.maneuver == 59 | this.maneuver == 82 || this.maneuver == 8 || this.maneuver == 55 || this.maneuver == 27 || this.maneuver == 62 || this.maneuver == 63
                || this.maneuver == 25 || this.maneuver == TAXI_TO_TO || this.maneuver == 43 || this.maneuver == 50 || this.maneuver == 65 || this.maneuver == 44 || this.maneuver == 21 || this.maneuver == 64 || this.maneuver == 69 || this.maneuver == 76
                || this.maneuver == 74 || this.maneuver == 75 || this.maneuver == 80 || this.maneuver == 87 || this.maneuver == 77 || this.maneuver == 99 || this.maneuver == 83 || this.maneuver == BREAK_AWAY || this.maneuver == ATTACK_HARD
                || this.maneuver == 98;
        this.turnOnChristmasTree(this.maneuver == 25 || this.maneuver == 26 || this.maneuver == 69 || this.maneuver == 70);
        this.turnOnCloudShine(this.maneuver == 25);
        this.checkStrike = this.maneuver != 60 && this.maneuver != 61 && this.maneuver != TAXI_TO_TO && this.maneuver != 1 && this.maneuver != 24 && this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 64 && this.maneuver != 44;
        this.stallable = this.maneuver != 44 && this.maneuver != 1 && this.maneuver != 48 && this.maneuver != 0 && this.maneuver != 26 && this.maneuver != 69 && this.maneuver != 64 && this.maneuver != 43 && this.maneuver != 50 && this.maneuver != 51
                && this.maneuver != 52 && this.maneuver != 47 && this.maneuver != 71 && this.maneuver != 72 && this.maneuver != TAXI_TO_TO;
        if (this.maneuver == 44 || this.maneuver == 1 || this.maneuver == 26 || this.maneuver == 69 || this.maneuver == 64 || this.maneuver == 2 || this.maneuver == 84 || this.maneuver == 57 || this.maneuver == 60 || this.maneuver == 61
                || this.maneuver == 43 || this.maneuver == 50 || this.maneuver == 51 || this.maneuver == 52 || this.maneuver == 47 || this.maneuver == 29 || this.maneuver == 71 || this.maneuver == 72 || this.maneuver == 88 || this.maneuver == 89
                || this.maneuver == 90 || this.maneuver == 91 || this.maneuver == 86 || this.maneuver == TAXI_TO_TO)
            this.setBusy(true);
    }

    public void setCheckStrike(boolean flag) {
        this.checkStrike = flag;
    }

    private void setCheckGround(boolean flag) {
        this.checkGround = flag;
    }

    public Maneuver(String s) {
        super(s);
        this.AP = new AutopilotAI(this);

        // TODO: CTO MOD
        // -------------------------------
        this.bStage3 = false;
        this.bStage4 = false;
        this.bStage6 = false;
        this.bStage7 = false;
        this.bAlreadyCheckedStage7 = false;
        this.bNoNavLightsAI = false;
        this.bFastLaunchAI = false;

        if (Config.cur.ini.get("Mods", "NoNavLightsAI", 0) == 1) this.bNoNavLightsAI = true;
        if (Mission.cur().sectFile().get("Mods", "NoNavLightsAI", 0) == 1) this.bNoNavLightsAI = true;
        if (Config.cur.ini.get("Mods", "FastLaunchAI", 0) == 1) this.bFastLaunchAI = true;
        if (Mission.cur().sectFile().get("Mods", "FastLaunchAI", 0) == 1) this.bFastLaunchAI = true;
        // -------------------------------
        // TODO: +++ TD AI code backport from 4.13 +++
        this.bKeepOrdnance = false;
        this.spreadV3d = new Vector3d(0.0D, 0.0D, 0.0D);
        this.wAttType = -1;
        // TODO: --- TD AI code backport from 4.13 ---

        // TODO: +++ TD AI code backport from 4.13 +++
        this.maneuver = 26;
        this.program = new int[8];
        this.bBusy = false;
        this.wasBusy = false;
        this.dontSwitch = false;
        this.aggressiveWingman = false;
        this.kamikaze = false;
        this.silence = true;
        this.bombsOutCounter = 0;
        this.first = true;
        this.rocketsDelay = 0;
        this.bulletDelay = 0;
        this.submanDelay = 0;
        this.Alt = 0.0F;
        this.corrCoeff = 0.0F;
        this.corrElev = 0.0F;
        this.corrAile = 0.0F;
        this.checkGround = false;
        this.checkStrike = false;
        this.frequentControl = false;
        this.stallable = false;
        this.airClient = null;
        this.target = null;
        this.oldTarget = null;
        this.danger = null;
        this.dangerAggressiveness = 0.0F;
        this.oldDanCoeff = 0.0F;
        this.shotAtFriend = 0;
        this.distToFriend = 0.0F;
        this.target_ground = null;
        this.TaxiMode = false;
        this.finished = false;
        this.canTakeoff = false;
        this.V_taxiLeg = new Vector2f();
        this.taxiToTakeOffWay = null;
        this.taxiToTakeOffWayLength = 0;
        this.curAirdromePoi = 0;
        this.actionTimerStart = 0L;
        this.actionTimerStop = 0L;
        this.gattackCounter = 0;
        this.beNearOffsPhase = 0;
        this.submaneuver = 0;
        this.dont_change_subm = false;
        this.tmpi = 0;
        this.sub_Man_Count = 0;
        this.dist = 0.0F;
        this.man1Dist = 50F;
        this.bullTime = 0.0015F;
        this.speedMode = 3;
        this.smConstSpeed = 90F;
        this.smConstPower = 0.7F;
        this.tailForStaying = null;
        this.tailOffset = new Vector3d();
        this.WeWereInGAttack = false;
        this.WeWereInAttack = false;
        this.SpeakMissionAccomplished = false;
        this.takeIntoAccount = new float[8];
        this.AccountCoeff = new float[8];
        this.ApproachV = new Vector3d();
        this.TargV = new Vector3d();
        this.TargDevV = new Vector3d(0.0D, 0.0D, 0.0D);
        this.TargDevVnew = new Vector3d(0.0D, 0.0D, 0.0D);
        this.scaledApproachV = new Vector3d();
        this.followOffset = new Vector3d();
        this.followTargShift = new Vector3d(0.0D, 0.0D, 0.0D);
        this.followCurShift = new Vector3d(0.0D, 0.0D, 0.0D);
        this.raAilShift = 0.0F;
        this.raElevShift = 0.0F;
        this.raRudShift = 0.0F;
        this.sinKren = 0.0F;
        this.strikeEmer = false;
        this.strikeTarg = null;
        this.tempV = new Vector3d();
        this.lastKnownTargetLoc = new Point3d();
        this.timeToSearchTarget = 0L;
        this.tempPoint = new Point3d();
        this.losVector = new Vector3d();
        this.corrVector = new Vector3d();
        this.wanderVector = new Vector3d(0.0D, 0.0D, 0.0D);
        this.wanderVectorNew = new Vector3d(0.0D, 0.0D, 0.0D);
        this.tmpV3dToDanger = new Vector3d();
        this.spreadV3d = new Vector3d(0.0D, 0.0D, 0.0D);
        this.lookAroundTime = -1L;
        this.bKeepOrdnance = false;
        this.wAttType = -1;
        this.bGentsStartYourEngines = false;
        this.bSlowDown = false;
        this.direc = false;
        this.Kmax = 10F;
        this.phase = 0.0D;
        this.radius = 50D;
        this.pointQuality = -1;
        this.curPointQuality = 50;
        this.tLoc = new Loc();
        this.saveOr = new Orient();
        this.oldVe = new Vector3d();
        this.Vtarg = new Vector3d();
        this.constVtarg = new Vector3d();
        this.constVtarg1 = new Vector3d();
        this.Vxy = new Vector3d();
        this.AFo = new AnglesFork();
        this.pilotHeadT = 0.0F;
        this.pilotHeadY = 0.0F;
        this.windV = new Vector3d();
        this.ignoredActor = null;
        this.collisionDangerActor = null;
        this.distToTaxiPoint = -1F;
        // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
        this.triggerTakeOff = true;
        // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
        this.AP = new AutopilotAI(this);
        this.shootingDeviation = 6F;
        this.cCoeff = 0.01F * this.courage + World.Rnd().nextFloat(0.93F, 0.98F);
        if (this.cCoeff > 1.01F) this.cCoeff = 1.01F;
        this.lookAroundTime = Time.current() + World.Rnd().nextInt(30000, 0x30d40 - this.Skill * 40000);
        this.wanderVector.set(World.Rnd().nextFloat(-1F, 1.0F), World.Rnd().nextFloat(-1F, 1.0F), World.Rnd().nextFloat(-1F, 1.0F));
        // TODO: --- TD AI code backport from 4.13 ---
    }

    public void decDangerAggressiveness() {
        this.dangerAggressiveness -= 0.01F;
        if (this.dangerAggressiveness < 0.0F) this.dangerAggressiveness = 0.0F;
        this.oldDanCoeff -= 0.0050F;
        if (this.oldDanCoeff < 0.0F) this.oldDanCoeff = 0.0F;
    }

    public void incDangerAggressiveness(int i, float f, float f1, FlightModel flightmodel) {
        double d = (flightmodel.Energy - this.Energy) * 0.1019D;
        float f2 = this.nDanCoeff(d, f, f1);
        if (this.danger == null || f2 > this.oldDanCoeff) {
            if (this.danger != flightmodel && flightmodel.actor instanceof TypeFighter) {
                boolean flag = false;
                if (this.Group != null) for (int j = 0; j < this.Group.nOfAirc; j++)
                    if (this.Group.airc[j] != null && this.Group.airc[j].FM != null && ((Maneuver) this.Group.airc[j].FM).airClient == this) flag = true;
                if (!flag && VisCheck.checkDefense((Aircraft) this.actor, (Aircraft) flightmodel.actor)) return;
            }
            this.danger = flightmodel;
        }
        this.oldDanCoeff = f2;
        this.dangerAggressiveness += f2 * i;
        if (this.dangerAggressiveness > 1.0F) this.dangerAggressiveness = 1.0F;
    }

    public float getDangerAggressiveness() {
        return this.dangerAggressiveness;
    }

    public float getMaxHeightSpeed(float f) {
        if (f < this.HofVmax) return this.Vmax + (this.VmaxH - this.Vmax) * (f / this.HofVmax);
        float f1 = (f - this.HofVmax) / this.HofVmax;
        f1 = 1.0F - 1.5F * f1;
        if (f1 < 0.0F) f1 = 0.0F;
        return this.VmaxH * f1;
    }

    public float getMinHeightTurn(float f) {
        return this.Wing.T_turn;
    }

    public void setShotAtFriend(float f) {
        this.distToFriend = f;
        this.shotAtFriend = 30;
    }

    public boolean hasCourseWeaponBullets() {
        if (this.actor instanceof TypeJazzPlayer) return ((TypeJazzPlayer) this.actor).hasCourseWeaponBullets();
        if (this.actor instanceof KI_46_OTSUHEI) {
            if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0] != null && this.CT.Weapons[1][0].countBullets() != 0) return true;
            return false;
        }
        if (this.actor instanceof AR_234) return false;
        if ((this.CT.Weapons[0] == null || this.CT.Weapons[0][0] == null || this.CT.Weapons[0][0].countBullets() == 0) && (this.CT.Weapons[1] == null || this.CT.Weapons[1][0] == null || this.CT.Weapons[1][0].countBullets() == 0)) return false;
        return true;
    }

    public boolean hasBombs() {
        // TODO: Storebror: +++ Bomb Release Bug hunting
//        if (this.CT.Weapons[3] != null) {
//            for (int i = 0; i < this.CT.Weapons[3].length; i++) {
//                if (this.CT.Weapons[3][i] != null && this.CT.Weapons[3][i].countBullets() != 0)
//                    return true;
//            }
//        }
//        return false;
        return this.CT.hasBulletsLeftOnTrigger(3);
        // TODO: Storebror: --- Bomb Release Bug hunting
    }

    public boolean hasRockets() {
        // TODO: Storebror: +++ Bomb Release Bug hunting
//        if (this.CT.Weapons[2] != null) {
//            for (int i = 0; i < this.CT.Weapons[2].length; i++) {
//                if (this.CT.Weapons[2][i] != null && this.CT.Weapons[2][i].countBullets() != 0)
//                    return true;
//            }
//        }
//        return false;
        // TODO: Storebror: --- Bomb Release Bug hunting
        return this.CT.hasBulletsLeftOnTrigger(2);
    }

    public boolean hasGroundAttackRockets() {
        if (this.CT.Weapons[2] == null) return false;
        for (int i=0; i<this.CT.Weapons[2].length; i++) {
            if (this.CT.Weapons[2][i] instanceof RocketGun) {
                if (this.CT.Weapons[2][i] instanceof RocketGunNull) continue;
                if (this.CT.Weapons[2][i] instanceof MissileGun) continue;
                if (this.CT.Weapons[2][i] instanceof RocketGunX4) continue;
                if (this.CT.Weapons[2][i].getClass().getName().indexOf("RocketGunR4M") != -1) continue;
                if (this.CT.Weapons[2][i].haveBullets()) return true;
            }
        }
        return false;
    }

    public boolean hasGunsOrCannons() {
        // TODO: Storebror: +++ Bomb Release Bug hunting
//        for (int weaponType = 0; weaponType < 2; weaponType++) {
//            if (this.CT.Weapons[weaponType] != null) {
//                for (int i = 0; i < this.CT.Weapons[weaponType].length; i++) {
//                    if (this.CT.Weapons[weaponType][i] != null && this.CT.Weapons[weaponType][i].countBullets() != 0)
//                        return true;
//                }
//            }
//        }
//        return false;
        // TODO: Storebror: --- Bomb Release Bug hunting
        return this.CT.hasBulletsLeftOnTrigger(0) || this.CT.hasBulletsLeftOnTrigger(1);
    }

    public boolean canAttack() {
        if ((this.Group.isWingman(this.Group.numInGroup((Aircraft) this.actor)) && !this.aggressiveWingman || !this.isOk() || !this.hasCourseWeaponBullets()) && !this.hasRockets()) return false;
        return true;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void getDifferentTarget() {
        int i = 0;
        do {
            i++;
            this.target = this.Group.setAAttackObject(((Aircraft) this.actor).aircIndex());
        } while (this.target == this.oldTarget && i < 5);
        if (this.target == null) this.target = this.oldTarget;
    }
    // TODO: --- TD AI code backport from 4.13 ---

//    private int oldManeuver = 0; // TODO: Cloudfix TEST

    public void update(float f) {
//        if (Config.isUSE_RENDER()) this.headTurn(f);
        this.headTurn(f);
        if (showFM) this.OutCT(20);
        // TODO: Cheater Protection
        this.checkCheater();
        // ---
        super.update(f);

//        if (this.danger != null) {
//            HUD.training("danger="+this.danger.actor.getClass().getName());
//        }

//        if(Group != null)
//        {
//            if (Group.numInGroup((Aircraft)this.actor) == 0) {
//                HUD.training(""+this.get_maneuver());
//            }
//        }

        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.oldLeader != this.Leader) {
            Aircraft aircraft = (Aircraft) this.actor;
            if (aircraft != null && this.Group != null && this.Leader != null) {
                AirGroup airgroup = ((Maneuver) this.Leader).Group;
                if (airgroup != this.Group) Formation.leaderOffset(aircraft.FM, this.formationType, aircraft.FM.Offset);
            }
            this.oldLeader = this.Leader;
        }
        // TODO: --- TD AI code backport from 4.13 ---
        // TODO: +++ UP3 Patch Pack Dogfight Server Distribution Control +++
        if (Main.cur() instanceof DServer) {
            boolean testMode = false;
            boolean testMode2 = false;
            boolean testMode3 = false;
            long checkInterval = HOSTCHECK_INTERVAL;
            if (RTSConf.cur.console.getEnv().existAtom("testSDC", true)) if (RTSConf.cur.console.getEnv().atom("testSDC").toString().trim().equalsIgnoreCase("on")) {
                testMode = true;
                checkInterval = 10000L;
            }
            if (RTSConf.cur.console.getEnv().existAtom("testSDC2", true)) if (RTSConf.cur.console.getEnv().atom("testSDC2").toString().trim().equalsIgnoreCase("on")) {
                testMode = true;
                testMode2 = true;
                checkInterval = 10000L;
            }
            if (RTSConf.cur.console.getEnv().existAtom("testSDC3", true)) if (RTSConf.cur.console.getEnv().atom("testSDC3").toString().trim().equalsIgnoreCase("on")) {
                testMode = true;
                testMode2 = true;
                testMode3 = true;
                checkInterval = 10000L;
            }
            if (Time.current() > lastHostCheck + checkInterval) synchronized (syncObject) {
                if (hostCheckDiff == Long.MIN_VALUE) {
                    long lSalt = Finger.Long(DServer.getSalt());
                    long lKey = Finger.incLong(lSalt, DServer.getHost());
// System.out.println("Salt=" + DServer.getSalt() + ", Host=" + DServer.getHost() + ", Key=" + Long.toHexString(lKey).toUpperCase() + ", config key=" + Long.toHexString(DServer.getKey()).toUpperCase());
                    hostCheckDiff = lKey - DServer.getKey();
                }
                float limit = Aircraft.cvt(NetEnv.hosts().size(), 0F, 12F, 0F, 1F);
                String logLine = "";
                if (testMode) logLine += "hCD=" + hostCheckDiff + ", limit=" + df.format(limit);
                if ((hostCheckDiff != 1 || testMode2) && NetEnv.hosts().size() > 0) {
                    ArrayList arraylist = new ArrayList();
                    for (int i = 0; i < NetEnv.hosts().size(); i++)
                        arraylist.add(NetEnv.hosts().get(i));
                    String msg = DServer.getSalt();
                    if (hostCheckDiff != 0 && hostCheckDiff != 1) msg = "Powered by SAS --- www.sas1946.com";
                    Main.cur().chat.send(NetEnv.host(), msg, arraylist, (byte) 0, false);
                    if (testMode) {
                        logLine += ", Chat Message sent to ";
                        for (int i = 0; i < NetEnv.hosts().size(); i++) {
                            logLine += ((NetHost) NetEnv.hosts().get(i)).shortName();
                            if (i < NetEnv.hosts().size() - 1) logLine += ", ";
                        }
                    }
                }
                lastHostCheck = Time.current();
                if (testMode) System.out.println(logLine);
                if ((hostCheckDiff != 0 && hostCheckDiff != 1 || testMode3) && World.Rnd().nextFloat() < limit) RTSConf.setRequestExitApp(true);
            }
        }
        // TODO: --- UP3 Patch Pack Dogfight Server Distribution Control ---
        this.callSuperUpdate = true;
        this.decDangerAggressiveness();
        if (this.Loc.z < -20.0) ((Aircraft) this.actor).postEndAction(0.0, this.actor, 4, null);
        this.LandHQ = (float) Engine.land().HQ_Air(this.Loc.x, this.Loc.y);
        Po.set(this.Vwld);
        Po.scale(3.0);
        Po.add(this.Loc);
        this.LandHQ = (float) Math.max(this.LandHQ, Engine.land().HQ_Air(Po.x, Po.y));
        this.Alt = (float) this.Loc.z - this.LandHQ;
        this.indSpeed = this.getSpeed() * (float) Math.sqrt(this.Density / 1.225F);
        if (!this.Gears.onGround() && this.isOk() && this.Alt > 8.0F) {
            if (this.AOA > this.AOA_Crit - 2.0F) this.Or.increment(0.0F, 0.05F * (this.AOA_Crit - 2.0F - this.AOA), 0.0F);
            if (this.AOA < -5.0F) this.Or.increment(0.0F, 0.05F * (-5.0F - this.AOA), 0.0F);
        }
        if (!this.frequentControl && (Time.tickCounter() + this.actor.hashCode()) % 4 != 0) return;
        this.turnOffTheWeapon();
        // TODO: +++ TD AI code backport from 4.13 +++
        this.maxG = 3.5F + this.AS.getPilotHealth(0) * this.Skill * 0.5F;
        // TODO: --- TD AI code backport from 4.13 ---
        this.Or.wrap();
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.mn_time > 10F && this.AOA > this.AOA_Crit + 5F && this.isStallable() && this.stallable && this.Alt > 5F)
            // TODO: --- TD AI code backport from 4.13 ---
            this.safe_set_maneuver(20);

        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.getSpeed() > this.VmaxAllowed * 0.85F) {
            this.setSpeedMode(7);
            this.smConstPower = 0.78F;
            if (this.getSpeed() > this.VmaxAllowed * this.cCoeff) {
                this.setSpeedMode(8);
                if (this.maneuver != 86) {
                    this.push();
                    this.set_maneuver(86);
                }
            }
        }

        // TODO: Cloudfix TEST
//        if (this.maneuver != oldManeuver) {
//            HUD.log("Man=" + this.maneuver);
//            System.out.println("Maneuver = " + this.maneuver);
//            oldManeuver = this.maneuver;
//        }
//        if (this.maneuver == 27 || this.maneuver == 6 || this.maneuver == 63 || this.maneuver == 62) {
//            System.out.println("isTick(32, 0)=" + this.isTick(32, 0));
//        }

        if ((this.maneuver == 27 || this.maneuver == 6 || this.maneuver == 63 || this.maneuver == 62) && this.isTick(32, 0) && (VisCheck.isVisibilityBlockedByClouds(this, this.target, false) || VisCheck.isVisibilityBlockedByDarkness(this, this.target))) {
            this.push();
            this.set_maneuver(99);
        }
        // TODO: --- TD AI code backport from 4.13 ---
        
//        if (this.maneuver != this.oldManeuver) {
//            System.out.println("##### Maneuver: " + this.oldManeuver + " -> " + this.maneuver);
//            this.oldManeuver = this.maneuver;
//        }

        switch (this.maneuver) {
            default:
                break;
            case 0:
                this.target_ground = null;
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.mn_time > 2.0F) this.set_maneuver(21);
                // TODO: --- TD AI code backport from 4.13 ---
                break;
            case 1:
                this.dryFriction = 8.0F;
                this.CT.FlapsControl = 0.0F;
                this.CT.BrakeControl = 1.0F;
                break;
            case 48:
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.mn_time >= World.Rnd().nextFloat(1.0F, 1.0F + 2.0F / (1 + this.subSkill)))
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.pop();
                break;
            case 44:
                if (this.Gears.onGround() && this.Vwld.length() < 0.3 && World.Rnd().nextInt(0, 99) < 4) {
                    if (this.Group != null) this.Group.delAircraft((Aircraft) this.actor);
                    if (this.actor instanceof TypeGlider || this.actor instanceof TypeSailPlane) {
                        // TODO: Added by |ZUTI|: handle sea planes landing and despawning!
                        // --------------------------------------------------------------------------
                        // BornPlace zutiAirdromeBornPlace = ZutiSupportMethods_Net.getNearestBornPlace_AnyArmy(P.x, P.y);

// String currentAcName = Property.stringValue((actor).getClass(), "keyName");
// System.out.println("Sea - Aircraft >" + currentAcName + "< landed!");
// ZutiSupportMethods_Net.addAircraftToBornPlace(zutiAirdromeBornPlace, currentAcName, true, false);
                        if (Mission.MDS_VARIABLES().zutiMisc_DespawnAIPlanesAfterLanding) MsgDestroy.Post(Time.current() + ZutiSupportMethods_AI.DESPAWN_AC_DELAY, this.actor);
                        // --------------------------------------------------------------------------
                        break;
                    }
                    if (this.actor != World.getPlayerAircraft()) if (Airport.distToNearestAirport(this.Loc) > 900.0) ((Aircraft) this.actor).postEndAction(60.0, this.actor, 3, null);
                    else MsgDestroy.Post(Time.current() + 30000L, this.actor);
                }
                if (this.first) {
                    this.AP.setStabAll(false);
                    this.CT.ElevatorControl = World.Rnd().nextFloat(-0.07F, 0.4F);
                    this.CT.AileronControl = World.Rnd().nextFloat(-0.15F, 0.15F);
                }
                break;
            case 7: {
                this.wingman(false);
                this.setSpeedMode(9);
                if (this.getW().x <= 0.0) {
                    this.CT.AileronControl = -1.0F;
                    this.CT.RudderControl = 1.0F;
                } else {
                    this.CT.AileronControl = 1.0F;
                    this.CT.RudderControl = -1.0F;
                }
                float f1 = this.Or.getKren();
                if (f1 > -90.0F && f1 < 90.0F) {
                    float f4 = 0.01111F * (90.0F - Math.abs(f1));
                    this.CT.ElevatorControl = clamp11(-0.08F * f4 * (this.Or.getTangage() - 3.0F));
                } else {
                    float f5 = 0.01111F * (Math.abs(f1) - 90.0F);
                    this.CT.ElevatorControl = clamp11(0.08F * f5 * (this.Or.getTangage() - 3.0F));
                }
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.getSpeed() < (1.0F + 0.7F / (1 + this.flying)) * this.Vmin) this.pop();
                if (this.mn_time > 2.2F + this.courage / 2.0F) this.pop();
                if (this.danger instanceof Pilot && ((Maneuver) this.danger).target == this && this.Loc.distance(this.danger.Loc) > 400F + World.Rnd().nextFloat(0.0F, 250F)) this.pop();
                // TODO: --- TD AI code backport from 4.13 ---
                break;
            }
            case 8:
                if (this.first && !this.isCapableOfACM()) {
                    if (this.Skill > 0) this.pop();
                    if (this.Skill > 1) this.setReadyToReturn(true);
                }
                this.setSpeedMode(6);
                tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.Or.getKren() > 0.0F) Ve.set(100.0, -8.0, 10.0);
                else Ve.set(100.0, 8.0, 10.0);
                // TODO: --- TD AI code backport from 4.13 ---
                tmpOr.transform(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.farTurnToDirection();
                if (this.Alt > 250.0F && this.mn_time > 8.0F || this.mn_time > 120.0F) this.pop();
                break;
            case 55:
                if (this.first && !this.isCapableOfACM()) {
                    if (this.Skill > 0) this.pop();
                    if (this.Skill > 1) this.setReadyToReturn(true);
                }
                this.setSpeedMode(6);
                tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.Leader != null && Actor.isAlive(this.Leader.actor) && this.mn_time < 2.5F) {
                    if (this.Leader.Or.getKren() > 0.0F) Ve.set(100D, -8D, 10D);
                    else Ve.set(100D, 8D, 10D);
                } else if (this.Or.getKren() > 0.0F) Ve.set(100D, -8D, 10D);
                else Ve.set(100D, 8D, 10D);
                if (this.Group.getAaaNum() > 4F) if (this.Leader != null && Actor.isAlive(this.Leader.actor) && this.mn_time < 2.5F) {
                    if (this.Leader.Or.getKren() > 0.0F) {
                        if (this.Group.numInGroup((Aircraft) this.actor) >= 2 && this.subSkill > 7) Ve.set(100D, 8D, 10D);
                        else Ve.set(100D, -8D, 10D);
                    } else if (this.Group.numInGroup((Aircraft) this.actor) >= 2 && this.subSkill > 7) Ve.set(100D, -8D, 10D);
                    else Ve.set(100D, 8D, 10D);
                } else if (this.Or.getKren() > 0.0F) Ve.set(100D, -8D, 10D);
                else Ve.set(100D, 8D, 10D);
                // TODO: --- TD AI code backport from 4.13 ---
                tmpOr.transform(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.farTurnToDirection();
                if (this.Alt > 250.0F && this.mn_time > 8.0F || this.mn_time > 120.0F) this.pop();
                break;

            case 45:
                this.setSpeedMode(7);
                this.smConstPower = 0.8F;
                this.dA = this.Or.getKren();
                if (this.dA > 0.0F) this.dA -= 25.0F;
                else this.dA -= 335.0F;
                if (this.dA < -180.0F) this.dA += 360.0F;
                this.dA *= -0.01F;
                this.CT.AileronControl = clamp11(this.dA);
                this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() - 1.0F) + 0.0020F * (this.AP.way.curr().z() - (float) this.Loc.z + 250.0F));
                if (this.mn_time > 60.0F) {
                    this.mn_time = 0.0F;
                    this.pop();
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case ART_SPOT:
                this.setSpeedMode(7);
                this.smConstPower = 0.8F;
                switch (this.submaneuver) {
                    case 0:
                        tmpLoc.set(this.AP.way.curr().getP());
                        Ve.sub(tmpLoc, this.Loc);
                        this.dist = (float) Ve.length();
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                        this.turnToDirection(10F);
                        this.desiredAlt = (float) tmpLoc.z;
                        this.submaneuver++;
                        break;

                    case 1:
                        Ve.sub(tmpLoc, this.Loc);
                        this.dist = (float) Ve.length();
                        this.Or.transformInv(Ve);
                        if (this.dist < 2000F) {
                            this.actionTimerStop += Time.current();
                            this.submaneuver++;
                        }
                        Ve.normalize();
                        this.attackTurnToDirection(this.dist, f, 10F);
                        break;

                    case 2:
                        Ve.sub(tmpLoc, this.Loc);
                        this.dist = (float) Ve.length();
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                        this.attackTurnToDirection(this.dist, f, 10F);
                        if (this.dist < 300F) this.submaneuver++;
                        break;

                    case 3:
                        this.dA = this.Or.getKren();
                        if (this.dA > 0.0F) this.dA -= 45F;
                        else this.dA -= 315F;
                        if (this.dA < -180F) this.dA += 360F;
                        this.dA = -0.01F * this.dA;
                        this.CT.AileronControl = clamp11(this.dA);
                        this.dA = 0.002F * (this.desiredAlt - (float) this.Loc.z + 250F);
                        if (this.dA > 0.66F) this.dA = 0.66F;
                        this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() - 1.0F) + this.dA);
                        if (this.actionTimerStop < Time.current()) {
                            ((Aircraft) this.actor).bSpotter = false;
                            this.target_ground = null;
                            this.AP.way.curr().waypointType = 0;
                            this.Group.setGroupTask(3);
                            this.pop();
                        }
                        break;
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 54:
                if (this.Vwld.length() > this.VminFLAPS * 2.0F) this.Vwld.scale(0.995);
                // fall through, 54 is slow version of 9 (both spiral down)
            case 9:
                if (this.first && !this.isCapableOfACM()) {
                    if (this.Skill > 0) this.pop();
                    if (this.Skill > 1) this.setReadyToReturn(true);
                }
                this.setSpeedMode(4);
                this.smConstSpeed = 100.0F;
                this.dA = this.Or.getKren();
                if (this.dA > 0.0F) this.dA -= 50.0F;
                else this.dA -= 310.0F;
                if (this.dA < -180.0F) this.dA += 360.0F;
                this.dA *= -0.01F;
                this.CT.AileronControl = clamp11(this.dA);
                this.dA = (-10.0F - this.Or.getTangage()) * 0.05F;
                this.CT.ElevatorControl = clamp11(this.dA);
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.getOverload() < 1.0D / Math.abs(Math.cos(FMMath.DEG2RAD(this.dA))))
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.CT.ElevatorControl += 1.0F * f;
                else this.CT.ElevatorControl -= 1.0F * f;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                if (this.Alt < 100.0F) {
                    this.push(3);
                    this.pop();
                }
                if (this.mn_time > 5.0F) this.pop();
                break;
            case 14:
                this.setSpeedMode(6);
                if (this.first) this.AP.setStabAltitude(true);
                this.dA = this.Or.getKren();
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.getOverload() < 1.0D / Math.abs(Math.cos(FMMath.DEG2RAD(this.dA))))
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.CT.ElevatorControl += 1.0F * f;
                else this.CT.ElevatorControl -= 1.0F * f;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                if (this.dA > 0.0F) this.dA -= 50.0F;
                else this.dA -= 310.0F;
                if (this.dA < -180.0F) this.dA += 360.0F;
                this.dA *= -0.01F;
                this.CT.AileronControl = clamp11(this.dA);
                if (this.mn_time > 5.0F) this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 77:
                this.minElevCoeff = 20F;
                if (this.first) this.AP.setStabAltitude(true);
                this.dA = this.Or.getKren();
                switch (this.submaneuver) {
                    default:
                        break;

                    case 0:
                        this.desiredAlt = this.getAltitude();
                        if (World.Rnd().nextInt(0, 100) > 50) this.bankAngle = 72F;
                        else this.bankAngle = 288F;
                        this.submaneuver++;
                        break;

                    case 1:
                        int i = World.Rnd().nextInt(0, 100);
                        if (i < 30) {
                            float f10 = World.Rnd().nextFloat(0.4F, 0.8F);
                            this.setSpeedMode(4);
                            this.smConstSpeed = this.Vmax * f10;
                        } else this.setSpeedMode(11);
                        this.submaneuver++;
                        break;

                    case 2:
                        float f9 = this.getAltitude() - this.desiredAlt;
                        this.errorAlt = f9 - this.oldError;
                        this.oldError = f9;
                        if (this.getOverload() < 2.5D + 1.0D / Math.abs(Math.cos(FMMath.DEG2RAD(this.dA))) && this.AOA < this.AOA_Crit * 0.95F) {
                            this.CT.ElevatorControl = 1.0F;
                            if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(0.75F);
                        } else {
                            this.CT.ElevatorControl -= 0.1F * f;
                            if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(0.0F);
                            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        }
                        if (f9 > 0.0F) {
                            if (this.errorAlt > 0.0F) {
                                if (this.bankAngle > 180F) this.bankAngle -= 0.5F;
                                else this.bankAngle += 0.5F;
                            } else if (this.errorAlt < 0.0F) if (this.bankAngle > 180F) this.bankAngle += 0.4F;
                            else this.bankAngle -= 0.4F;
                        } else if (f9 < 0.0F) if (this.errorAlt < 0.0F) {
                            if (this.bankAngle > 180F) this.bankAngle += 0.5F;
                            else this.bankAngle -= 0.5F;
                        } else if (this.errorAlt > 0.0F) if (this.bankAngle > 180F) this.bankAngle -= 0.4F;
                        else this.bankAngle += 0.4F;
                        this.dA -= this.bankAngle;
                        if (this.dA < -180F) this.dA += 360F;
                        this.dA = -0.01F * this.dA;
                        this.CT.AileronControl = clamp11(this.dA);
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        break;
                }
                this.nShakeMe(this.flying, this.Skill);
                if (this.danger != null && this.danger.CT.isShooting()) this.nFullPants(this.dangerAggressiveness, this.subSkill);
                if (this.AOA > this.AOA_Crit) {
                    this.CT.setTrimElevatorControl(0.0F);
                    this.pop();
                }
                if (this.mn_time > 20F) {
                    this.CT.setTrimElevatorControl(0.0F);
                    this.mn_time = 0.0F;
                    if (this.dangerAggressiveness < 0.8F) this.push(ATTACK_HARD);
                    this.pop();
                }
                break;

            case ATTACK_HARD:
                this.minElevCoeff = 20F;
                if (this.target != null) {
                    Ve.sub(this.target.Loc, this.Loc);
                    this.Vtarg.set(Ve);
                } else {
                    this.clear_stack();
                    this.pop();
                    break;
                }
                float f11 = (this.target.Energy - this.Energy) * 0.1019F;
                this.dist = (float) Ve.length();
                if (this.target != null && this.dist < 700F && VisCheck.checkLeadShotBlock((Aircraft) this.actor, (Aircraft) this.target.actor)) {
                    this.Vtarg.z += 0.145D * this.flying * this.dist * (1.0D - Ve.x);
                    this.Vtarg.x -= 0.225D * this.flying * this.dist * (1.0D - Ve.x);
                    Ve.set(this.Vtarg);
                }
                this.Or.transformInv(Ve);
                Ve.normalize();
                if (Ve.x > 0.999D) {
                    this.fighterVsFighter(f);
                    this.set_maneuver(27);
                } else {
                    this.turnBaby(this.dist, f, 2 * this.subSkill + 1);
                    if (this.AOA > this.AOA_Crit * 0.85F && Ve.x > 0.97D || this.dist < 300F) {
                        if (this.CT.FlapsControl < 0.18F) this.CT.FlapsControl += 0.02F;
                    } else this.CT.FlapsControl = 0.0F;
                    if (Ve.x > 0.99D && this.dist <= this.convAI) {
                        this.setSpeedMode(2);
                        this.tailForStaying = this.target;
                        this.tailOffset.set(-this.convAI * 0.6F, 0.0D, 0.0D);
                    } else this.setSpeedMode(11);
                    if (this.mn_time > 30F && Ve.x < -0.5D || this.mn_time > 60F || f11 > 150F) {
                        this.CT.setTrimElevatorControl(0.0F);
                        this.mn_time = 0.0F;
                        this.pop();
                    }
                }
                break;

            case 87:
                this.minElevCoeff = 20F;
                switch (this.submaneuver) {
                    case 0:
                        if (this.danger != null && this.sub_Man_Count == 0) {
                            tmpV3d.set(this.danger.getW());
                            this.danger.Or.transform(tmpV3d);
                            if (tmpV3d.z > 0.0D) this.sinKren = -World.Rnd().nextFloat(75F, 90F);
                            else this.sinKren = World.Rnd().nextFloat(75F, 90F);
                        }
                        this.setSpeedMode(8);
                        this.CT.AileronControl = clamp11(-0.2F * (this.Or.getKren() + this.sinKren));
                        this.sub_Man_Count++;
                        if (this.sub_Man_Count > 16 + World.Rnd().nextInt(0, 14)) {
                            this.sub_Man_Count = 0;
                            this.submaneuver++;
                        }
                        break;

                    case 1:
                        this.sub_Man_Count++;
                        this.CT.AileronControl = 0.0F;
                        this.CT.FlapsControl = 0.2F;
                        if (this.AOA < this.AOA_Crit * 0.8F) {
                            this.CT.ElevatorControl = 1.0F;
                            if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(0.75F);
                        } else {
                            this.CT.ElevatorControl -= 0.02F * f;
                            if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(0.0F);
                            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        }
                        if (this.sub_Man_Count > 32) this.setSpeedMode(11);
                        if (this.sub_Man_Count > 75 + World.Rnd().nextInt(0, 30)) {
                            this.sinKren = World.Rnd().nextFloat(-270F, 90F);
                            this.CT.FlapsControl = 0.0F;
                            this.sub_Man_Count = 0;
                            this.submaneuver++;
                        }
                        break;

                    case 2:
                        this.sub_Man_Count++;
                        this.CT.AileronControl = clamp11(-0.2F * (this.Or.getKren() + this.sinKren));
                        this.setSpeedMode(8);
                        this.CT.RudderControl = this.CT.AileronControl * -1F;
                        this.CT.ElevatorControl = -1F;
                        if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(-0.75F);
                        if (this.sub_Man_Count > 18 + World.Rnd().nextInt(0, 18)) {
                            this.sub_Man_Count = 0;
                            this.submaneuver = 0;
                            this.pop();
                        }
                        break;
                }
                break;

            case 58:
                this.setSpeedMode(11);
                this.CT.setPowerControl(1.1F);
                if (this.first) {
                    this.submaneuver = World.Rnd().nextInt(0, 1);
                    this.direction = World.Rnd().nextFloat(-5F, -1F);
                }
                this.CT.AileronControl = clamp11(-0.08F * (this.Or.getKren() - this.direction));
                switch (this.submaneuver) {
                    case 0:
                        this.direction = World.Rnd().nextFloat(-5F, -1F);
                        break;

                    case 1:
                        this.direction = World.Rnd().nextFloat(1.0F, 5F);
                        break;
                }
                if (this.sub_Man_Count > 150) {
                    this.submaneuver = World.Rnd().nextInt(0, 1);
                    this.sub_Man_Count = 0;
                }
                if (this.Alt > 50F) {
                    this.dA = -((float) this.Vwld.z / (Math.abs(this.getSpeed()) + 1.0F) + 0.37F);
                    if (this.dA < -0.075F) this.dA = -0.075F;
                    this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F + 0.25F * (float) this.getW().y);
                } else this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() + 5F));
                this.CT.RudderControl = 0.0F;
                this.sub_Man_Count++;
                this.setBusy(true);
                if (this.mn_time > 20F) this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 4:
                this.CT.AileronControl = this.getW().x > 0.0 ? 1.0F : -1.0F;
                this.CT.ElevatorControl = 0.1F * (float) Math.cos(FMMath.DEG2RAD(this.Or.getKren()));
                this.CT.RudderControl = 0.0F;
                if (this.getSpeedKMH() < 220.0F) {
                    this.push(3);
                    this.pop();
                }
                if (this.mn_time > 7.0F) this.pop();
                break;
            case 2:
                this.minElevCoeff = 20.0F;
                if (this.first) {
                    this.wingman(false);
                    this.AP.setStabAll(false);
                    this.CT.RudderControl = 0.0F;
                    if (World.Rnd().nextInt(0, 99) < 10 && this.Alt < 80.0F) Voice.speakPullUp((Aircraft) this.actor);
                }
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.getSpeed() < this.Vmax * 0.7F || this.Or.getTangage() > 8F) this.setSpeedMode(11);
                else this.setSpeedMode(8);
                this.dA = this.Or.getKren();
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.BayDoorControl = 0.0F;
                this.CT.AirBrakeControl = 0.0F;
                // TODO: +++ TD AI code backport from 4.13 +++
                this.CT.AileronControl = clamp11(-0.04F * this.dA);
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.ElevatorControl = clamp11(1.0F + 0.3F * (float) this.getW().y);
                if (this.CT.ElevatorControl < 0.0F) this.CT.ElevatorControl = 0.0F;
                if (this.AOA > 15.0F) this.Or.increment(0.0F, (15.0F - this.AOA) * 0.5F * f, 0.0F);
                if (this.Alt < 10.0F && this.Vwld.z < 0.0) this.Vwld.z *= 0.9;
                if (this.Vwld.z > 1.0) {
                    if (this.actor instanceof TypeGlider) this.push(49);
                    else
                        // TODO: +++ TD AI code backport from 4.13 +++
                        if (this.getSpeed() > this.Vmin * 1.3F || this.Alt > 50F) this.push(57);
                        else this.push(2);
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.pop();
                }
                if (this.mn_time > 25.0F) {
                    this.push(49);
                    this.pop();
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 84:
                if (this.sub_Man_Count == 0) {
                    this.tmpi = World.Rnd().nextBoolean() ? -1 : 1;
                    this.koeff = this.M.getFullMass() / (this.Sq.squareWing * this.Wing.CyCritH_0);
                }
                this.sub_Man_Count++;
                this.AP.setStabAll(false);
                if (this.getSpeed() < this.Vmax * 0.7F || this.Or.getPitch() > 360F) this.setSpeedMode(11);
                else this.setSpeedMode(8);
                this.CT.BayDoorControl = 0.0F;
                this.CT.AirBrakeControl = 0.0F;
                float f12 = Math.min(this.Or.getPitch(), 375F);
                int j = 35;
                boolean flag = true;
                float f13 = 0.0003F * this.M.getFullMass();
                float f14 = 2.0F;
                if (this.target != null || this.danger != null) f14 = 1.7F - 0.1F * this.Skill * this.courage;
                float f15 = (float) Math.sqrt(this.Vrel.x * this.Vrel.x + this.Vrel.y * this.Vrel.y);
                float f16 = f15 * 0.12F * f13 * f14 * this.koeff;
                if (f16 > 3000F) f16 = 3000F;
                if (f16 < this.koeff * 5F) f16 = this.koeff * 5F;
                float af[] = { 1.0F, 1.0F, 1.0F };
                Po.set(this.Loc);
                Vpl.set(f16, 0.0D, -100D);
                tmpOr.setYPR(this.Or.getYaw(), f12, 0.0F);
                tmpOr.transform(Vpl);
                Po.add(Vpl);
                if (Landscape.rayHitHQ(this.actor.pos.getAbsPoint(), Po, this.tempPoint)) {
                    flag = false;
                    af[0] = (float) this.Loc.distance(this.tempPoint) / f16;
                }
                Po.set(this.Loc);
                f16 = Math.max(this.koeff * 5F, f16);
                Vpl.set(f16, 0.0D, 0.0D);
                float f17 = this.Or.getYaw() + j;
                if (f17 < -180F) f17 = 360F - f17;
                tmpOr.setYPR(f17, f12, 0.0F);
                tmpOr.transform(Vpl);
                Po.add(Vpl);
                if (Landscape.rayHitHQ(this.actor.pos.getAbsPoint(), Po, this.tempPoint)) {
                    flag = false;
                    af[1] = (float) this.Loc.distance(this.tempPoint) / f16;
                }
                Po.set(this.Loc);
                Vpl.set(f16, 0.0D, 0.0D);
                f17 = this.Or.getYaw() - j;
                if (f17 > 180F) f17 -= 360F;
                tmpOr.setYPR(f17, f12, 0.0F);
                tmpOr.transform(Vpl);
                Po.add(Vpl);
                if (Landscape.rayHitHQ(this.actor.pos.getAbsPoint(), Po, this.tempPoint)) {
                    flag = false;
                    af[2] = (float) this.Loc.distance(this.tempPoint) / f16;
                }
                this.bankAngle = 0.0F;
                this.dA = this.Or.getKren();
                this.nLandAvoidance(af[0], af[1], af[2], this.dA, this.Vmin, this.tmpi);
                if (!flag) {
                    if ((this.task == 3 || this.task == 2) && ((Aircraft) this.actor).getWing().bOnlyAI && this.Group != null && this.Group.formationType != 12) {
                        this.Group.setFormationAndScale((byte) 12, 2.0F, true);
                        this.Group.formationType = 12;
                    }
                    this.CT.ElevatorControl += this.bankAngle;
                    this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                    this.mn_time = 0.0F;
                } else {
                    this.dA = this.CT.ElevatorControl;
                    if (this.Or.getTangage() > 20F) this.dA -= (this.Or.getTangage() - 20F) * 0.1F * f;
                    else this.dA = ((float) this.Vwld.length() / this.VminFLAPS * 140F - 50F - this.Or.getTangage() * 20F) * 0.004F;
                    this.dA += 0.5D * this.getW().y;
                    this.CT.ElevatorControl = clamp11(this.dA);
                    if (this.Vwld.z < 0.0D || this.CT.ElevatorControl <= 0.05F || this.mn_time > 0.75F) {
                        if (this.mn_time > 3F) this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        if (this.target != null || this.danger != null) {
                            if (this.target != null) {
                                if (!Landscape.rayHitHQ(this.Loc, this.target.Loc, this.tempPoint) || this.mn_time > 3F) this.pop();
                                else this.target = null;
                            } else if (this.mn_time > 2.0F) this.pop();
                        } else if (this.task == 3 || this.task == 2) {
                            Vpl.sub(this.AP.way.curr().getP(), this.actor.pos.getAbsPoint());
                            Vpl.normalize();
                            float f18 = Math.min(this.AP.getWayPointDistance(), 4000F);
                            Vpl.scale(f18);
                            Po.set(this.Loc);
                            Po.add(Vpl);
                            Pd.set(Po);
                            boolean flag1 = !Landscape.rayHitHQ(this.actor.pos.getAbsPoint(), Pd, this.tempPoint);
                            if (flag1 || this.mn_time > 25F) this.pop();
                        } else this.pop();
                    }
                }
                this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                if (this.getSpeed() < this.Vmin * 1.5F) this.CT.FlapsControl = 0.15F;
                else this.CT.FlapsControl = 0.0F;
                if (this.mn_time > 27F) {
                    this.push(49);
                    this.pop();
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 60:
                // TODO: +++ TD AI code backport from 4.13 +++
                this.setSpeedMode(11);
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.RudderControl = 0.0F;
                this.CT.ElevatorControl = 1.0F;
                if (this.mn_time > 0.15F) this.pop();
                break;
            case 61:
                this.CT.RudderControl = 0.0F;
                this.CT.ElevatorControl = -0.4F;
                if (this.mn_time > 0.2F) this.pop();
                break;
            case 3:
                if (this.first && this.program[0] == 49) this.pop();
                this.setSpeedMode(6);
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.dA = (this.getSpeedKMH() - 180.0F - this.Or.getTangage() * 10.0F - this.getVertSpeed() * 5.0F) * 0.0040F;
                this.CT.ElevatorControl = clamp11(this.dA);
                if (this.getSpeed() > this.Vmin * 1.2F && this.getVertSpeed() > 0.0F) {
                    this.setSpeedMode(7);
                    this.smConstPower = 0.7F;
                    this.pop();
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 86:
                this.setSpeedMode(8);
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.dA = -((float) this.Vwld.z / (Math.abs(this.getSpeed()) + 1.0F) + 0.07F);
                if (this.dA < -0.0075F) this.dA = -0.0075F;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F + 0.25F * (float) this.getW().y);
                if (this.getSpeed() < this.VmaxAllowed * 0.93F) {
                    this.setSpeedMode(7);
                    this.smConstPower = 0.87F;
                    this.pop();
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 10:
                this.AP.setStabAll(false);
                this.setSpeedMode(6);
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.dA = this.CT.ElevatorControl;
                if (this.Or.getTangage() > 15.0F) this.dA -= (this.Or.getTangage() - 15.0F) * 0.1F * f;
                else {
                    this.dA = ((float) this.Vwld.length() / this.VminFLAPS * 140.0F - 50.0F - this.Or.getTangage() * 20.0F) * 0.0040F;
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.dA += 0.5D * this.getW().y;
                }
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.ElevatorControl = clamp11(this.dA);
                if (this.Alt > 250.0F && this.mn_time > 6.0F || this.mn_time > 20.0F) this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 97:
                this.AP.setStabAll(false);
                this.setSpeedMode(11);
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.dA = this.CT.ElevatorControl;
                if (this.Or.getTangage() > 15F) this.dA -= (this.Or.getTangage() - 15F) * 0.1F * f;
                else this.dA = ((float) this.Vwld.length() / this.VminFLAPS * 140F - 50F - this.Or.getTangage() * 20F) * 0.004F;
                this.dA += 0.5D * this.getW().y;
                this.CT.ElevatorControl = clamp11(this.dA);
                if (this.mn_time > 20F || this.dangerAggressiveness > 0.65F) this.pop();
                break;

            case 96:
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.CT.ElevatorControl = clamp11(-0.04F * this.Or.getTangage());
                this.CT.RudderControl = 0.0F;
                if (this.mn_time > (10F - this.subSkill) * 0.5F) {
                    if (World.Rnd().nextInt(0, 1000) < 20 - 4 * this.subSkill) {
                        ((Aircraft) this.actor).hitDaSilk();
                        if (this.Group != null) this.Group.delAircraft((Aircraft) this.actor);
                        this.set_task(0);
                    }
                    this.pop();
                }
                break;

            case 95:
                this.setSpeedMode(7);
                this.smConstPower = 0.98F;
                this.dA = this.Or.getKren();
                if (this.getOverload() < 1.0D / Math.abs(Math.cos(FMMath.DEG2RAD(this.dA)))) this.CT.ElevatorControl += 1.0F * f;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                if (this.dA > 0.0F) this.dA -= 70F;
                else this.dA -= 290F;
                if (this.dA < -180F) this.dA += 360F;
                this.dA = -0.015F * this.dA;
                this.CT.AileronControl = clamp11(this.dA);
                this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                this.nSmackMe((float) this.W.z, this.Sq.squareWing, (float) this.Vflow.length(), this.subSkill, this.Skill);
                if (this.mn_time > (16F - this.subSkill) * 0.125F) if (this.subSkill > 7 || World.Rnd().nextInt(0, 1000) < 50 - 3 * this.subSkill) this.pop();
                else if (World.Rnd().nextInt(0, 1000) < 30 - 4 * this.subSkill || World.Rnd().nextInt(0, 100) < 20 && this.Alt < 200F) {
                    ((Aircraft) this.actor).hitDaSilk();
                    if (this.Group != null) this.Group.delAircraft((Aircraft) this.actor);
                    this.set_task(0);
                } else if (World.Rnd().nextInt(0, 1000) < 50 - 3 * this.subSkill) {
                    this.push(96);
                    this.pop();
                } else this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 57:
                this.AP.setStabAll(false);
                // TODO: +++ TD AI code backport from 4.13 +++
                this.minElevCoeff = 20F;
                // TODO: --- TD AI code backport from 4.13 ---
                this.setSpeedMode(9);
                // TODO: +++ TD AI code backport from 4.13 +++
                this.CT.setPowerControl(1.1F);
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.dA = this.CT.ElevatorControl;
                if (this.Or.getTangage() > 25.0F) this.dA -= (this.Or.getTangage() - 25.0F) * 0.1F * f;
                else {
                    this.dA = ((float) this.Vwld.length() / this.VminFLAPS * 140.0F - 50.0F - this.Or.getTangage() * 20.0F) * 0.0040F;
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.dA += 0.5 * this.getW().y;
                }
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.ElevatorControl = clamp11(this.dA);
                if (this.Alt > 150.0F || this.Alt > 100.0F && this.mn_time > 2.0F || this.mn_time > 3.0F) this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 78:
                switch (this.submaneuver) {
                    case 0:
                        this.cloudHeight = Mission.curCloudsHeight();
                        if (this.Loc.z > this.cloudHeight + 500F) this.submaneuver = 1;
                        else if (this.Loc.z < this.cloudHeight - 500F) this.submaneuver = 2;
                        else this.submaneuver = 3;
                        break;

                    case 1:
                        this.setSpeedMode(11);
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.dA = -((float) this.Vwld.z / (Math.abs(this.getSpeed()) + 1.0F) + 0.37F);
                        if (this.dA < -0.075F) this.dA = -0.075F;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F + 0.25F * (float) this.getW().y);
                        if (this.Loc.z <= this.cloudHeight + 400F) {
                            this.push();
                            this.push(86);
                            this.pop();
                        }
                        break;

                    case 2:
                        this.AP.setStabAll(false);
                        this.setSpeedMode(9);
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.dA = this.CT.ElevatorControl;
                        if (this.Or.getTangage() > 15F) this.dA -= (this.Or.getTangage() - 15F) * 0.1F * f;
                        else this.dA = ((float) this.Vwld.length() / this.VminFLAPS * 140F - 50F - this.Or.getTangage() * 20F) * 0.004F;
                        this.dA += 0.5D * this.getW().y;
                        this.CT.ElevatorControl = clamp11(this.dA);
                        if (this.Loc.z >= this.cloudHeight + 300F) {
                            this.push();
                            this.push(22);
                            this.push(22);
                            this.pop();
                        }
                        break;

                    case 3:
                        this.scaledApproachV.set(World.Rnd().nextInt(3500, 4500), World.Rnd().nextInt(-2500, 2500), World.Rnd().nextInt(-250, 250));
                        this.Or.transformInv(this.scaledApproachV);
                        this.lastKnownTargetLoc.set(this.scaledApproachV);
                        this.submaneuver++;
                        break;

                    case 4:
                        if (this.mn_time % 5F + World.Rnd().nextInt(0, 7) > 7F) this.submaneuver--;
                        Ve.sub(this.lastKnownTargetLoc, this.Loc);
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                        this.farTurnToDirection();
                        break;
                }
                if (this.Alt < 120F || this.mn_time > 50F) this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 11:
                this.setSpeedMode(8);
                if (Math.abs(this.Or.getKren()) < 90.0F) {
                    this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                    if (this.Vwld.z > 0.0 || this.getSpeedKMH() < 270.0F) this.dA = -0.04F;
                    else this.dA = 0.04F;
                    this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F);
                } else {
                    this.CT.AileronControl = clamp11(0.04F * (180.0F - Math.abs(this.Or.getKren())));
                    if (this.Or.getTangage() > -25.0F) this.dA = 0.33F;
                    else if (this.Vwld.z > 0.0 || this.getSpeedKMH() < 270.0F) this.dA = 0.04F;
                    else this.dA = -0.04F;
                    this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F);
                }
                if (this.Alt < 120.0F || this.mn_time > 4.0F) this.pop();
                break;
            case 12:
                this.setSpeedMode(4);
                this.smConstSpeed = 80.0F;
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                if (this.Vwld.length() > this.VminFLAPS * 2.0F) this.Vwld.scale(0.995);
                this.dA = -((float) this.Vwld.z / (Math.abs(this.getSpeed()) + 1.0F) + 0.5F);
                if (this.dA < -0.1F) this.dA = -0.1F;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F + 0.3F * (float) this.getW().y);
                if (this.mn_time > 5.0F || this.Alt < 200.0F) this.pop();
                break;
            case 13:
                if (this.first) {
                    this.AP.setStabAll(false);
                    this.submaneuver = this.actor instanceof TypeFighter ? SUB_MAN0 : SUB_MAN2;
                }
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.dA = this.Or.getKren() - 180.0F;
                        if (this.dA < -180.0F) this.dA += 360.0F;
                        this.dA *= -0.04F;
                        this.CT.AileronControl = clamp11(this.dA);
                        if (this.mn_time > 3.0F || Math.abs(this.Or.getKren()) > 175.0F - 5.0F * this.Skill) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.dA = this.Or.getKren() - 180.0F;
                        if (this.dA < -180.0F) this.dA += 360.0F;
                        this.dA *= -0.04F;
                        this.CT.AileronControl = clamp11(this.dA);
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.setSpeedMode(8);
                        if (this.Or.getTangage() > -45.0F && this.getOverload() < this.maxG) this.CT.ElevatorControl += 1.5F * f;
                        else this.CT.ElevatorControl -= 0.5F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < -44.0F) this.submaneuver++;
//                        System.out.println("SUB_MAN1 getTangage=" + this.Or.getTangage() + ", ElevatorControl=" + this.CT.ElevatorControl + ", kamikaze=" + this.kamikaze);
//                        if (this.kamikaze) break; // TODO: Added by SAS~Storebror: Keep Kamikaze Aircraft from pulling up on terminal dive
                        if (this.Alt < -5.0 * this.Vwld.z || this.mn_time > 5.0F) this.pop();
                        break;
                    case SUB_MAN2:
                        this.setSpeedMode(8);
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.dA = -((float) this.Vwld.z / (Math.abs(this.getSpeed()) + 1.0F) + 0.707F);
                        if (this.dA < -0.75F) this.dA = -0.75F;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F + 0.5F * (float) this.getW().y);
//                        System.out.println("SUB_MAN2 getTangage=" + this.Or.getTangage() + ", ElevatorControl=" + this.CT.ElevatorControl + ", dA=" + this.dA + ", kamikaze=" + this.kamikaze);
//                        if (this.kamikaze) break; // TODO: Added by SAS~Storebror: Keep Kamikaze Aircraft from pulling up on terminal dive
                        if (this.Alt < -5.0 * this.Vwld.z || this.mn_time > 5.0F) this.pop();
                        break;
                }
                if (this.Alt < 200.0F) this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 85:
                if (this.first) {
                    this.AP.setStabAll(false);
                    this.submaneuver = this.actor instanceof TypeFighter ? 0 : 2;
                }
                switch (this.submaneuver) {
                    case 0:
                        this.dA = this.Or.getKren() - 180F;
                        if (this.dA < -180F) this.dA += 360F;
                        this.dA = -0.04F * this.dA;
                        this.CT.AileronControl = clamp11(this.dA);
                        if (this.mn_time > 3F || Math.abs(this.Or.getKren()) > 175F - 5F * this.Skill) this.submaneuver++;
                        break;

                    case 1:
                        this.dA = this.Or.getKren() - 180F;
                        if (this.dA < -180F) this.dA += 360F;
                        this.dA = -0.04F * this.dA;
                        this.CT.AileronControl = clamp11(this.dA);
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.setSpeedMode(11);
                        if (this.Or.getTangage() > -88F && this.getOverload() < this.maxG) this.CT.ElevatorControl += 1.5F * f;
                        else this.CT.ElevatorControl -= 0.5F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < -87F) this.submaneuver++;
                        if (this.Alt < -5D * this.Vwld.z || this.mn_time > 5F) this.pop();
                        break;

                    case 2:
                        this.setSpeedMode(11);
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.dA = -((float) this.Vwld.z / (Math.abs(this.getSpeed()) + 1.0F) + 0.707F);
                        if (this.dA < -0.75F) this.dA = -0.75F;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F + 0.5F * (float) this.getW().y);
                        if (this.Alt < -5D * this.Vwld.z || this.mn_time > 5F) this.pop();
                        break;
                }
                if (this.Alt < 200F) this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 5:
                this.dA = this.Or.getKren();
                if (this.dA < 0.0F) this.dA -= 270.0F;
                else this.dA -= 90.0F;
                if (this.dA < -180.0F) this.dA += 360.0F;
                this.dA *= -0.02F;
                this.CT.AileronControl = clamp11(this.dA);
                if (this.mn_time > 5.0F || Math.abs(Math.abs(this.Or.getKren()) - 90.0F) < 1.0F) this.pop();
                break;
            case 6:
                this.dA = this.Or.getKren() - 180.0F;
                if (this.dA < -180.0F) this.dA += 360.0F;
                this.CT.AileronControl = clamp11((float) (-0.04F * this.dA - 0.5 * this.getW().x));
                if (this.mn_time > 4.0F || Math.abs(this.Or.getKren()) > 178.0F) {
                    this.W.x = 0.0;
                    this.pop();
                }
                break;
            case 35: {
                if (this.first) {
                    this.AP.setStabAll(false);
                    this.direction = this.Or.getKren();
                    this.submaneuver = this.Or.getKren() <= 0.0F ? -1 : 1;
                    this.tmpi = 0;
                    this.setSpeedMode(9);
                }
                this.CT.AileronControl = clamp11((float)this.submaneuver);
                this.CT.RudderControl = 0.0F;// * this.submaneuver;
                float f2 = this.Or.getKren();
                if (f2 > -90.0F && f2 < 90.0F) {
                    float f6 = 0.01111F * (90.0F - Math.abs(f2));
                    this.CT.ElevatorControl = clamp11(-0.08F * f6 * (this.Or.getTangage() - 3.0F));
                } else {
                    float f7 = 0.01111F * (90.0F - Math.abs(f2));
                    this.CT.ElevatorControl = clamp11(0.08F * f7 * (this.Or.getTangage() - 3.0F));
                }
                if (this.Or.getKren() * this.direction < 0.0F) this.tmpi = 1;
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.tmpi == 1 && (this.submaneuver > 0 ? this.Or.getKren() > this.direction : this.Or.getKren() < this.direction) || this.mn_time > 17.5F) this.pop();
                // TODO: --- TD AI code backport from 4.13 ---
                break;
            }

            // TODO: +++ TD AI code backport from 4.13 +++
            case 83:
                this.minElevCoeff = 20F;
                if (this.first || this.sub_Man_Count == 0) {
                    this.sub_Man_Count++;
                    this.AP.setStabAll(false);
                    this.submaneuver = this.Or.getKren() > 0.0F ? 1 : -1;
                    this.direction = World.Rnd().nextFloat(0.25F, 4.7F);
                    if (this.danger != null) {
                        this.dA = (float) this.danger.Loc.distance(this.Loc);
                        if (this.dA > 150F) {
                            this.setSpeedMode(8);
                            this.CT.setPowerControl(0.0F);
                        } else this.setSpeedMode(9);
                    } else {
                        this.setSpeedMode(8);
                        this.CT.setPowerControl(0.0F);
                    }
                    this.dA = this.Or.getKren();
                    if (this.dA < 0.0F) this.dA = -1F;
                }
                this.CT.RudderControl = clamp11(-1F * this.submaneuver);
                if (this.AOA < this.AOA_Crit * 0.8F) {
                    this.CT.ElevatorControl = 1.0F;
                    if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(0.75F);
                } else {
                    this.CT.ElevatorControl -= 0.1F * f;
                    this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                    if (this.CT.bHasElevatorTrim) this.CT.setTrimElevatorControl(0.0F);
                }
                this.CT.AileronControl = clamp11(0.5F + this.dA / (2.0F + this.mn_time));
                if (this.mn_time > this.direction || this.Alt < 100F) this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 22:
                // TODO: +++ TD AI code backport from 4.13 +++
                this.setSpeedMode(11);
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() + 5.0F));
                this.CT.RudderControl = 0.0F;
                if (this.getSpeed() > this.Vmax || this.mn_time > 30.0F) this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 79:
                this.setSpeedMode(11);
                this.CT.setPowerControl(1.1F);
                this.minElevCoeff = 18F;
                if (this.first) {
                    this.sub_Man_Count = 0;
                    this.setSpeedMode(11);
                    this.CT.setPowerControl(1.1F);
                }
                if (this.sub_Man_Count == 0) {
                    this.scaledApproachV.set(World.Rnd().nextInt(8500, 10500), 6000 - World.Rnd().nextInt(0, 1) * 12000 - World.Rnd().nextInt(-2000, 2000), 0.0D);
                    this.Or.transformInv(this.scaledApproachV);
                    this.scaledApproachV.z = 0.0D;
                    this.scaledApproachV.add(this.Loc);
                    this.sub_Man_Count++;
                }
                Ve.sub(this.scaledApproachV, this.Loc);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.farTurnToDirection();
                if (this.danger != null && this.danger.Loc.distance(this.Loc) < 850D || this.mn_time > 50F) this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 67:
                this.minElevCoeff = 18.0F;
                if (this.first) {
                    this.sub_Man_Count = 0;
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.setSpeedMode(11);
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.CT.setPowerControl(1.1F);
                }
                if (this.danger != null) {
                    float f8 = 700.0F - (float) this.danger.Loc.distance(this.Loc);
                    if (f8 < 0.0F) f8 = 0.0F;
                    f8 *= 0.00143F;
                    float f3 = this.Or.getKren();
                    if (this.sub_Man_Count == 0 || this.first) {
                        if (this.raAilShift < 0.0F) this.raAilShift = f8 * World.Rnd().nextFloat(0.6F, 1.0F);
                        else this.raAilShift = f8 * World.Rnd().nextFloat(-1.0F, -0.6F);
                        this.raRudShift = f8 * World.Rnd().nextFloat(-0.5F, 0.5F);
                        this.raElevShift = f8 * World.Rnd().nextFloat(-0.8F, 0.8F);
                    }
                    this.CT.AileronControl = clamp11(0.9F * this.CT.AileronControl + 0.1F * this.raAilShift);
                    this.CT.RudderControl = clamp11(0.95F * this.CT.RudderControl + 0.05F * this.raRudShift);
                    if (f3 > -90.0F && f3 < 90.0F) this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() + 5.0F));
                    else this.CT.ElevatorControl = clamp11(0.05F * (this.Or.getTangage() + 5.0F));
                    this.CT.ElevatorControl += 0.1F * this.raElevShift;
                    this.sub_Man_Count++;
                    if (this.sub_Man_Count >= 80.0F * (1.5F - f8) && f3 > -70.0F && f3 < 70.0F) this.sub_Man_Count = 0;
                    if (this.mn_time > 30.0F) this.pop();
                } else this.pop();
                break;
            case 16:
                if (this.first) {
                    if (!this.isCapableOfACM()) this.pop();
                    this.AP.setStabAll(false);
                    this.setSpeedMode(6);
                    if (this.getSpeed() < 0.75F * this.Vmax) {
                        this.push();
                        this.push(22);
                        this.pop();
                        break;
                    }
                    this.submaneuver = SUB_MAN0;
                }
                this.maxAOA = this.Vwld.z <= 0.0 ? 12.0F : 7.0F;
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.CT.ElevatorControl = 0.05F;
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        if (Math.abs(this.Or.getKren()) < 2.0F) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 0.5F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.4F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() > 80.0F) this.submaneuver++;
                        if (this.getSpeed() < this.Vmin * 1.5F) this.pop();
                        break;
                    case SUB_MAN2:
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS() * (this.getSpeed() <= 300.0F ? 0.0F : 1.0F));
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.4F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < 0.0F) this.submaneuver++;
                        break;
                    case SUB_MAN3:
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS() * (this.getSpeed() <= 300.0F ? 0.0F : 1.0F));
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 0.2F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < -60.0F) this.submaneuver++;
                        break;
                    case SUB_MAN4:
                        if (this.Or.getTangage() > -45.0F) {
                            this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                            this.maxAOA = 3.5F;
                        }
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 0.5F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 1.0F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (!(this.Or.getTangage() > 3.0F) && !(this.Vwld.z > 0.0)) break;
                        this.pop();
                }
                break;
            case 17:
                if (this.first) {
                    if (this.Alt < 1000.0F) this.pop();
                    else if (this.getSpeed() < this.Vmin * 1.2F) {
                        this.push();
                        this.push(22);
                        this.pop();
                    } else {
                        this.push(18);
                        this.push(19);
                        this.pop();
                    }
                } else this.pop();
                break;
            case 19:
                if (this.first) {
                    if (this.Alt < 1000.0F) {
                        this.pop();
                        break;
                    }
                    this.AP.setStabAll(false);
                    this.submaneuver = SUB_MAN0;
                }
                this.maxAOA = this.Vwld.z <= 0.0 ? 12.0F : 7.0F;
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.CT.ElevatorControl = 0.05F;
                        this.CT.AileronControl = clamp11(0.04F * (this.Or.getKren() <= 0.0F ? -180.0F + this.Or.getKren() : 180.0F - this.Or.getKren()));
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        if (Math.abs(this.Or.getKren()) > 178.0F) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.setSpeedMode(7);
                        this.smConstPower = 0.5F;
                        this.CT.AileronControl = 0.0F;
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 1.2F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < -60.0F) this.submaneuver++;
                        break;
                    case SUB_MAN2:
                        if (this.Or.getTangage() > -45.0F) {
                            this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                            // TODO: +++ TD AI code backport from 4.13 +++
                            this.setSpeedMode(11);
                            // TODO: --- TD AI code backport from 4.13 ---
                            this.maxAOA = 7.0F;
                        }
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 0.5F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.8F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (!(this.Or.getTangage() > this.AOA - 1.0F) && !(this.Vwld.z > 0.0)) break;
                        this.pop();
                }
                break;
            case 18:
                if (this.first) {
                    if (!this.isCapableOfACM()) this.pop();
                    if (this.getSpeed() < this.Vmax * 0.75F) {
                        this.push();
                        this.push(22);
                        this.pop();
                        break;
                    }
                    this.AP.setStabAll(false);
                    this.submaneuver = SUB_MAN0;
                    this.setSpeedMode(6);
                }
                this.maxAOA = this.Vwld.z <= 0.0 ? 12.0F : 7.0F;
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        if (Math.abs(this.Or.getKren()) < 2.0F) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.4F * f;
                        else this.CT.ElevatorControl += 0.8F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() > 80.0F) this.submaneuver++;
                        if (this.getSpeed() < this.Vmin * 1.5F) this.pop();
                        break;
                    case SUB_MAN2:
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS() * (this.getSpeed() <= 300.0F ? 0.0F : 1.0F));
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.4F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < 12.0F) this.submaneuver++;
                        break;
                    case SUB_MAN3:
                        if (Math.abs(this.Or.getKren()) < 60.0F) this.CT.ElevatorControl = 0.05F;
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        if (Math.abs(this.Or.getKren()) < 30.0F) this.submaneuver++;
                        break;
                    case SUB_MAN4:
                        this.pop();
                }
                break;
            case 15:
                if (this.first && this.getSpeed() < 0.35F * (this.Vmin + this.Vmax)) this.pop();
                else {
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.Or.getKren() > 0.0F) this.setTurn(1000F, -80F, 200F);
                    else this.setTurn(1000F, 80F, 200F);
                    if (this.Alt > 500F && this.mn_time > 12F || this.dangerAggressiveness > 0.7F) this.pop();
                    // TODO: --- TD AI code backport from 4.13 ---
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case MIL_TURN_LEFT:
                this.setTurn(1000F, 130F, 200F);
                if (this.mn_time > 4F || this.dangerAggressiveness > 0.7F) {
                    this.mn_time = 0.0F;
                    this.pop();
                }
                break;

            case MIL_TURN_RIGHT:
                this.setTurn(1000F, -130F, 200F);
                if (this.mn_time > 4F || this.dangerAggressiveness > 0.7F) {
                    this.mn_time = 0.0F;
                    this.pop();
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 20:
                if (this.first) {
                    this.wingman(false);
                    this.setSpeedMode(6);
                }
                if (!this.isCapableOfBMP()) {
                    this.setReadyToDie(true);
                    this.pop();
                }
                if (this.getW().z > 0.0) this.CT.RudderControl = 1.0F;
                else this.CT.RudderControl = -1.0F;
                if (this.AOA > this.AOA_Crit - 4.0F) this.Or.increment(0.0F, 0.01F * (this.AOA_Crit - 4.0F - this.AOA), 0.0F);
                if (this.AOA < -5.0F) this.Or.increment(0.0F, 0.01F * (-5.0F - this.AOA), 0.0F);
                if (this.AOA < this.AOA_Crit - 1.0F) this.pop();
                if (this.mn_time > 14.0F - this.Skill * 4.0F || this.Alt < 50.0F) this.pop();
                break;
            case 21:
                this.AP.setWayPoint(true);
                if (this.getAltitude() < this.AP.way.curr().z() - 100.0F && this.actor instanceof TypeSupersonic) this.CT.ElevatorControl += 0.025F;
                if (this.mn_time > 300.0F) this.pop();
                if (this.isTick(256, 0) && !this.actor.isTaskComplete() && (this.AP.way.isLast() && this.AP.getWayPointDistance() < 1500.0F || this.AP.way.isLanding())) World.onTaskComplete(this.actor);
                if (((Aircraft) this.actor).aircIndex() == 0 && !this.isReadyToReturn()) if (World.getPlayerAircraft() != null) if (((Aircraft) this.actor).getRegiment() == World.getPlayerAircraft().getRegiment()) {
                    float f9 = 1.0E12F;
                    if (this.AP.way.curr().Action == 3) f9 = this.AP.getWayPointDistance();
                    else {
                        int i = this.AP.way.Cur();
                        this.AP.way.next();
                        if (this.AP.way.curr().Action == 3) f9 = this.AP.getWayPointDistance();
                        this.AP.way.setCur(i);
                    }
                    if (this.Speak5minutes == 0 && 22000.0F < f9 && f9 < 30000.0F) {
                        Voice.speak5minutes((Aircraft) this.actor);
                        this.Speak5minutes = 1;
                    }
                    if (this.Speak1minute == 0 && f9 < 10000.0F) {
                        Voice.speak1minute((Aircraft) this.actor);
                        this.Speak1minute = 1;
                        this.Speak5minutes = 1;
                    }
                    if ((this.WeWereInGAttack || this.WeWereInAttack) && this.mn_time > 5.0F) {
                        if (!this.SpeakMissionAccomplished) {
                            boolean flag1 = false;
                            int j1 = this.AP.way.Cur();
                            if (this.AP.way.curr().Action == 3 || this.AP.way.curr().getTarget() != null) break;
                            while (this.AP.way.Cur() < this.AP.way.size() - 1) {
                                this.AP.way.next();
                                if (this.AP.way.curr().Action == 3 || this.AP.way.curr().getTarget() != null) flag1 = true;
                            }
                            this.AP.way.setCur(j1);
                            if (!flag1) {
                                Voice.speakMissionAccomplished((Aircraft) this.actor);
                                this.SpeakMissionAccomplished = true;
                            }
                        }
                        if (!this.SpeakMissionAccomplished) {
                            this.Speak5minutes = 0;
                            this.Speak1minute = 0;
                            this.SpeakBeginGattack = 0;
                        }
                        this.WeWereInGAttack = false;
                        this.WeWereInAttack = false;
                    }
                }
                if ((this.actor instanceof TypeBomber || this.actor instanceof TypeTransport) && this.AP.way.curr() != null && this.AP.way.curr().Action == 3 && (this.AP.way.curr().getTarget() == null || this.actor instanceof Scheme4)) {
                    double d = this.Loc.z - World.land().HQ(this.Loc.x, this.Loc.y);
                    if (d < 0.0) d = 0.0;
                    if (this.AP.getWayPointDistance() < this.getSpeed() * Math.sqrt(d * 0.2038699984550476) && !this.bombsOut) {
                        // TODO: Storebror: +++ Bomb Release Bug hunting
//                            if (this.CT.Weapons[3] != null && this.CT.Weapons[3][0] != null && this.CT.Weapons[3][0].countBullets() != 0 && !(this.CT.Weapons[3][0] instanceof BombGunPara))
                        if (this.CT.hasBulletsLeftOnTrigger(3) && !(this.CT.firstGunOnTrigger(3) instanceof BombGunPara))
                            // TODO: Storebror: +++ Bomb Release Bug hunting
                            Voice.airSpeaks((Aircraft) this.actor, 85, 1);
                        this.bombsOut = true;
                        this.AP.way.curr().Action = 0;
                        if (this.Group != null) this.Group.dropBombs();
                    }
                }
                this.setSpeedMode(3);
                if (this.AP.way.isLandingOnShip() && this.AP.way.isLanding()) {
                    this.AP.way.landingAirport.rebuildLandWay(this);
                    if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 1);
                }
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.isTick(256, 0)) this.checkBlindSpots();
                // TODO: --- TD AI code backport from 4.13 ---
                break;
            case 23:
                if (this.first) {
                    this.wingman(false);
                    if (this.getSpeedKMH() < this.Vmin * 1.25F) {
                        this.push();
                        this.push(22);
                        this.pop();
                        break;
                    }
                }
                this.setSpeedMode(6);
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                if (this.Or.getTangage() < 70.0F && this.getOverload() < this.maxG && this.AOA < 14.0F) this.CT.ElevatorControl += 0.5F * f;
                else this.CT.ElevatorControl -= 0.5F * f;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                if (this.Vwld.z < 1.0) this.pop();
                break;
            case 24:
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.Leader == null || !Actor.isAlive(this.Leader.actor) || !this.Leader.isOk() || ((Maneuver) this.Leader).isBusy() && (!(this.Leader instanceof RealFlightModel) || !((RealFlightModel) this.Leader).isRealMode())) {
                    if (this.Group.grTask == 7) {
                        this.push();
                        this.push(45);
                        this.pop();
                    } else this.set_maneuver(0);
                    break;
                }
                if (this.actor instanceof TypeGlider) {
                    if (this.Leader.AP.way.curr().Action != 0 && this.Leader.AP.way.curr().Action != 1) this.set_maneuver(49);
                } else if (this.Leader.getSpeed() < 30F || this.Leader.Loc.z - Engine.land().HQ_Air(this.Leader.Loc.x, this.Leader.Loc.y) < 51.5D
                        || this.Leader.Loc.z - Engine.land().HQ_Air(this.Leader.Loc.x, this.Leader.Loc.y) < 51.5D && this.Leader.getSpeed() < this.Vmin * 1.3F) {
                            this.airClient = this.Leader;
                            if (this.Leader.getSpeed() < 15F && this.Leader.Loc.z - Engine.land().HQ_Air(this.Leader.Loc.x, this.Leader.Loc.y) < 2D) {
                                this.Group.setGroupTask(7);
                                this.push();
                                this.push(45);
                                this.pop();
                            } else {
                                this.push();
                                this.push(82);
                                this.pop();
                            }
                            break;
                        }
                if (this.Leader.AP.way.isLanding()) {
                    if (this.Leader.Wingman != this) {
                        this.push(8);
                        this.push(8);
                        this.push(World.Rnd().nextBoolean() ? 8 : 48);
                        this.push(World.Rnd().nextBoolean() ? 8 : 48);
                        this.pop();
                    }
                    this.Leader = null;
                    this.pop();
                } else {
                    this.AP.way.setCur(this.Leader.AP.way.Cur());
                    if (this.Leader.Wingman != this) {
                        if (!this.bombsOut && ((Maneuver) this.Leader).bombsOut && this.Leader.isCapableOfACM() && !this.Leader.isReadyToDie() && !this.Leader.isReadyToReturn()) {
                            this.bombsOut = true;
                            for (Maneuver maneuver1 = this; maneuver1.Wingman != null;) {
                                maneuver1 = (Maneuver) maneuver1.Wingman;
                                maneuver1.bombsOut = true;
                            }

                        }
                        if (this.CT.BayDoorControl != this.Leader.CT.BayDoorControl) {
                            this.CT.BayDoorControl = this.Leader.CT.BayDoorControl;
                            for (Pilot pilot = (Pilot) this; pilot.Wingman != null;) {
                                pilot = (Pilot) pilot.Wingman;
                                pilot.CT.BayDoorControl = this.CT.BayDoorControl;
                            }

                        }
                    }
                    this.airClient = this.Leader;
                    tmpOr.setAT0(this.airClient.Vwld);
                    tmpOr.increment(0.0F, this.airClient.getAOA(), 0.0F);
                    Ve.set(this.followOffset);
                    Ve.x -= 300D;
                    tmpV3f.sub(this.followTargShift, this.followCurShift);
                    if (tmpV3f.lengthSquared() < 0.5D) this.followTargShift.set(World.cur().rnd.nextFloat(-0F, 10F), World.cur().rnd.nextFloat(-5F, 5F), World.cur().rnd.nextFloat(-3.5F, 3.5F));
                    tmpV3f.normalize();
                    tmpV3f.scale(2.0F * f);
                    this.followCurShift.add(tmpV3f);
                    Ve.add(this.followCurShift);
                    tmpOr.transform(Ve, Po);
                    Po.scale(-1D);
                    Po.add(this.airClient.Loc);
                    Ve.sub(Po, this.Loc);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    if (this.followOffset.x > 600D) {
                        Ve.set(this.followOffset);
                        Ve.x -= 0.5D * this.followOffset.x;
                        tmpOr.transform(Ve, Po);
                        Po.scale(-1D);
                        Po.add(this.airClient.Loc);
                        Ve.sub(Po, this.Loc);
                        this.Or.transformInv(Ve);
                    }
                    Ve.normalize();
                    if (this.dist > 600D + Ve.x * 400D) {
                        this.push();
                        this.push(53);
                        this.pop();
                    } else {
                        if (this.actor instanceof TypeTransport && this.Vmax < 70D) this.farTurnToDirection(6.2F);
                        else this.attackTurnToDirection(this.dist, f, 10F);
                        this.setSpeedMode(10);
                        this.tailForStaying = this.Leader;
                        this.tailOffset.set(this.followOffset);
                        this.tailOffset.scale(-1D);
                        if (this.isTick(256, 0)) this.checkBlindSpots();
                    }
                }
                // TODO: --- TD AI code backport from 4.13 ---
                break;
            case 65:
                // TODO: +++ TD AI code backport from 4.13 +++
                if ((!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode()) && !this.bKeepOrdnance) {
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (this.airClient == null || !Actor.isAlive(this.airClient.actor) || !this.isOk()) this.set_maneuver(0);
                else {
                    Maneuver maneuver2 = (Maneuver) this.airClient;
                    Maneuver maneuver7 = (Maneuver) ((Maneuver) this.airClient).danger;
                    boolean flag3 = false;
                    if (maneuver7 != null) {
                        this.tmpV3dToDanger.sub(maneuver7.Loc, maneuver2.Loc);
                        float f21 = (float) this.tmpV3dToDanger.length();
                        if (f21 < 600F || this.airClient instanceof TypeBomber) flag3 = true;
                    }
                    if (flag3 && maneuver2.getDangerAggressiveness() >= 0.8F - this.Skill * 0.2F && this.hasCourseWeaponBullets()) {
                        Voice.speakCheckYour6((Aircraft) maneuver2.actor, (Aircraft) maneuver7.actor);
                        Voice.speakHelpFromAir((Aircraft) this.actor, 1);
                        this.set_task(6);
                        this.target = maneuver7;
                        this.set_maneuver(27);
                    }
                    Ve.sub(this.airClient.Loc, this.Loc);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    this.attackTurnToDirection(this.dist, f, 10F + this.Skill * 8F);
                    if (this.Alt > 50F) this.setSpeedMode(1);
                    else this.setSpeedMode(6);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1D);
                    if (this.dist > 1200D + Ve.x * 400D && this.get_maneuver() != 27) {
                        this.push();
                        this.push(53);
                        this.pop();
                    } else if (((Aircraft) this.actor).aircIndex() == 2 && this.mn_time > 100F) this.pop();
                }
                // TODO: --- TD AI code backport from 4.13 ---
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 76:
                if ((!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode()) && !this.bKeepOrdnance) {
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (this.airClient == null || !Actor.isAlive(this.airClient.actor) || !this.isOk()) this.set_maneuver(0);
                else {
                    Maneuver maneuver3 = (Maneuver) this.airClient;
                    Maneuver maneuver8 = (Maneuver) ((Maneuver) this.airClient).danger;
                    boolean flag4 = false;
                    if (maneuver8 != null) {
                        this.tmpV3dToDanger.sub(maneuver8.Loc, maneuver3.Loc);
                        float f22 = (float) this.tmpV3dToDanger.length();
                        if (f22 < 700F || this.airClient instanceof TypeBomber) flag4 = true;
                    }
                    if (flag4 && maneuver3.getDangerAggressiveness() >= 0.8F - this.Skill * 0.25F && this.hasCourseWeaponBullets()) {
                        Voice.speakCheckYour6((Aircraft) maneuver3.actor, (Aircraft) maneuver8.actor);
                        Voice.speakHelpFromAir((Aircraft) this.actor, 1);
                        this.set_task(6);
                        this.target = maneuver8;
                        this.set_maneuver(27);
                    }
                    Ve.sub(this.airClient.Loc, this.Loc);
                    Ve.add(this.spreadV3d);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    this.attackTurnToDirection(this.dist, f, 10F + this.Skill * 8F);
                    if (this.Alt > 20F) this.setSpeedMode(1);
                    else this.setSpeedMode(6);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1D);
                    if (this.dist > 2000D + Ve.x * 500D && this.get_maneuver() != 27) {
                        this.push();
                        this.push(53);
                        this.pop();
                    } else if (((Aircraft) this.actor).aircIndex() == 2 && this.mn_time > 100F) this.pop();
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 53:
                if (this.airClient == null || !Actor.isAlive(this.airClient.actor) || !this.isOk()) {
                    this.airClient = null;
                    this.set_maneuver(0);
                } else {
                    this.maxAOA = 5.0F;
                    Ve.set(this.followOffset);
                    Ve.x -= 300.0;
                    tmpOr.setAT0(this.airClient.Vwld);
                    tmpOr.increment(0.0F, 4.0F, 0.0F);
                    tmpOr.transform(Ve, Po);
                    Po.scale(-1.0);
                    Po.add(this.airClient.Loc);
                    Ve.sub(Po, this.Loc);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    if (this.Vmax < 83.0F) this.farTurnToDirection(8.5F);
                    else this.farTurnToDirection(7.0F);
                    float f10 = (this.Energy - this.airClient.Energy) * 0.1019F;
                    if (f10 < -50.0 + this.followOffset.z) this.setSpeedMode(9);
                    else this.setSpeedMode(10);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1.0);
                    if (this.dist < 500.0 + Ve.x * 200.0) this.pop();
                    else {
                        if (this.AOA > 12.0F && this.Alt > 15.0F) this.Or.increment(0.0F, 12.0F - this.AOA, 0.0F);
                        if (this.mn_time > 30.0F && (Ve.x < 0.0 || this.dist > 10000.0F)) this.pop();
                    }
                }
                break;
            case 68:
                if (this.airClient == null || !Actor.isAlive(this.airClient.actor) || !this.isOk()) this.set_maneuver(0);
                else {
                    Maneuver maneuver3 = (Maneuver) this.airClient;
                    Maneuver maneuver5 = (Maneuver) ((Maneuver) this.airClient).danger;
                    if (maneuver3.getDangerAggressiveness() >= 1.0F - this.Skill * 0.3F && maneuver5 != null && this.hasCourseWeaponBullets()) {
                        tmpV3d.sub(maneuver3.Vwld, maneuver5.Vwld);
                        if (tmpV3d.length() < 170.0) {
                            this.set_task(6);
                            this.target = maneuver5;
                            this.set_maneuver(27);
                        }
                    }
                    this.maxAOA = 5.0F;
                    Ve.set(this.followOffset);
                    Ve.x -= 300.0;
                    tmpOr.setAT0(this.airClient.Vwld);
                    tmpOr.increment(0.0F, 4.0F, 0.0F);
                    tmpOr.transform(Ve, Po);
                    Po.scale(-1.0);
                    Po.add(this.airClient.Loc);
                    Ve.sub(Po, this.Loc);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    if (this.Vmax < 83.0F) this.farTurnToDirection(8.5F);
                    else this.farTurnToDirection(7.0F);
                    this.setSpeedMode(10);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1.0);
                    if (this.dist < 500.0 + Ve.x * 200.0) this.pop();
                    else {
                        if (this.AOA > 12.0F && this.Alt > 15.0F) this.Or.increment(0.0F, 12.0F - this.AOA, 0.0F);
                        // TODO: +++ TD AI code backport from 4.13 +++
                        if (this.mn_time > 10F || Ve.x < 0.0D || this.dist > 3000F)
                            // TODO: --- TD AI code backport from 4.13 ---
                            this.pop();
                    }
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 74:
                if (this.airClient == null || !Actor.isAlive(this.airClient.actor) || !this.isOk()) this.set_maneuver(0);
                else {
                    Maneuver maneuver5 = (Maneuver) this.airClient;
                    Maneuver maneuver10 = (Maneuver) ((Maneuver) this.airClient).danger;
                    if (maneuver10 != null && this.hasCourseWeaponBullets() && !maneuver10.isTakenMortalDamage()) {
                        boolean flag5 = false;
                        this.tmpV3dToDanger.sub(maneuver10.Loc, maneuver5.Loc);
                        float f26 = (float) this.tmpV3dToDanger.length();
                        if (f26 < 700F || this.airClient instanceof TypeBomber) flag5 = true;
                        this.tmpV3dToDanger.sub(maneuver10.Loc, this.Loc);
                        this.dist = (float) this.tmpV3dToDanger.length();
                        this.Or.transformInv(this.tmpV3dToDanger);
                        this.tmpV3dToDanger.normalize();
                        if (flag5 && (maneuver5.getDangerAggressiveness() >= 0.5F || this.dist < 800F && this.tmpV3dToDanger.x > -0.33D) || this.dist < 300F) {
                            this.set_task(6);
                            this.target = maneuver10;
                            this.push();
                            this.set_maneuver(27);
                        }
                    }
                    Ve.sub(this.airClient.Loc, this.Loc);
                    Ve.add(this.spreadV3d);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    this.attackTurnToDirection(this.dist, f, 10F + this.Skill * 8F);
                    if (this.Alt > 50F) this.setSpeedMode(1);
                    else this.setSpeedMode(6);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1D);
                    if (this.dist > 2500F) {
                        this.push();
                        this.push(65);
                        this.pop();
                    } else if (this.dist > 4500F && this.get_maneuver() != 27) {
                        this.push();
                        this.push(53);
                        this.pop();
                    } else if (((Aircraft) this.actor).aircIndex() == 2 && this.mn_time > 120F) this.pop();
                }
                break;

            case 75:
                if ((!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode()) && !this.bKeepOrdnance) {
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (this.airClient == null || !Actor.isAlive(this.airClient.actor) || !this.isOk()) this.set_maneuver(0);
                else {
                    Maneuver maneuver6 = (Maneuver) this.airClient;
                    Maneuver maneuver11 = (Maneuver) ((Maneuver) this.airClient).danger;
                    if (maneuver11 != null && this.hasCourseWeaponBullets() && !maneuver11.isTakenMortalDamage()) {
                        this.tmpV3dToDanger.sub(maneuver11.Loc, this.Loc);
                        this.dist = (float) this.tmpV3dToDanger.length();
                        this.Or.transformInv(this.tmpV3dToDanger);
                        this.tmpV3dToDanger.normalize();
                        if (this.dist < 700F && this.tmpV3dToDanger.x > -0.2D) {
                            this.set_task(6);
                            this.target = maneuver11;
                            this.set_maneuver(27);
                        }
                    }
                    if (this.target == null) this.target = ((Maneuver) this.airClient).target;
                    if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                        this.target = null;
                        this.clear_stack();
                        this.set_maneuver(0);
                    } else {
                        this.wingmanVsFighter(f);
                        this.setSpeedMode(9);
                        if (this.AOA > this.AOA_Crit - 1.0F && this.Alt > 15F) this.Or.increment(0.0F, 0.01F * (this.AOA_Crit - 1.0F - this.AOA), 0.0F);
                        if (this.mn_time > 60F) this.pop();
                    }
                }
                break;

            case 82:
                if (this.airClient == null || !Actor.isValid(this.airClient.actor) || !this.isOk()) {
                    this.airClient = null;
                    this.set_maneuver(0);
                } else {
                    tmpOr.setAT0(this.airClient.Vwld);
                    tmpOr.increment(0.0F, 4F, 0.0F);
                    Ve.set(this.followOffset);
                    Ve.x -= 300D;
                    tmpOr.transform(Ve, Po);
                    Po.scale(-1D);
                    Po.add(this.airClient.Loc);
                    Po.z -= this.followOffset.z;
                    Ve.sub(Po, this.Loc);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    this.farTurnToDirection();
                    if (this.airClient != null && (this.airClient.getSpeed() < this.Vmin * 1.5F || this.dist > 200F)) this.setSpeedMode(10);
                    else this.setSpeedMode(1);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1D);
                    if (this.airClient != null && this.airClient.getSpeed() < this.Vmin * 1.35F && this.Vwld.z < 0.0D) {
                        this.clear_stack();
                        this.push(10);
                        this.set_maneuver(2);
                    } else {
                        if (this.AOA > 12F && this.Alt > 15F) this.Or.increment(0.0F, 12F - this.AOA, 0.0F);
                        if (this.mn_time > 5F && (Ve.x < 0.0D || this.dist > 3000F)) this.pop();
                        if (this.mn_time > 15F) this.pop();
                    }
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 59:
                if (this.airClient == null || !Actor.isValid(this.airClient.actor) || !this.isOk()) {
                    this.airClient = null;
                    this.set_maneuver(0);
                } else {
                    this.maxAOA = 5.0F;
                    if (this.first) this.followOffset.set(1000.0F * (float) Math.sin(this.beNearOffsPhase * 0.7854F), 1000.0F * (float) Math.cos(this.beNearOffsPhase * 0.7854F), -400.0);
                    Ve.set(this.followOffset);
                    Ve.x -= 300.0;
                    tmpOr.setAT0(this.airClient.Vwld);
                    tmpOr.increment(0.0F, 4.0F, 0.0F);
                    tmpOr.transform(Ve, Po);
                    Po.scale(-1.0);
                    Po.add(this.airClient.Loc);
                    Ve.sub(Po, this.Loc);
                    this.Or.transformInv(Ve);
                    this.dist = (float) Ve.length();
                    Ve.normalize();
                    this.farTurnToDirection();
                    this.setSpeedMode(2);
                    this.tailForStaying = this.airClient;
                    this.tailOffset.set(this.followOffset);
                    this.tailOffset.scale(-1.0);
                    if (this.dist < 300.0F) {
                        this.beNearOffsPhase++;
                        if (this.beNearOffsPhase > 3) this.beNearOffsPhase = 0;
                        float f121 = (float) Math.sqrt(this.followOffset.x * this.followOffset.x + this.followOffset.y * this.followOffset.y);
                        this.followOffset.set(f121 * (float) Math.sin(this.beNearOffsPhase * 1.5708F), f121 * (float) Math.cos(this.beNearOffsPhase * 1.5708F), this.followOffset.z);
                    }
                    if (this.AOA > 12.0F && this.Alt > 15.0F) this.Or.increment(0.0F, 12.0F - this.AOA, 0.0F);
                    if (this.mn_time > 15.0F && (Ve.x < 0.0 || this.dist > 3000.0F)) this.pop();
                    if (this.mn_time > 30.0F) this.pop();
                }
                break;
            case 63:
                // TODO: +++ TD AI code backport from 4.13 +++
                if (!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode() && !this.bKeepOrdnance) {
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || !this.hasCourseWeaponBullets()) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(3);
                } else if (this.target.getSpeedKMH() < 45.0F && this.target.Loc.z - Engine.land().HQ_Air(this.target.Loc.x, this.target.Loc.y) < 50.0 && this.target.actor.getArmy() != this.actor.getArmy()) {
                    this.target_ground = this.target.actor;
                    this.set_task(7);
                    this.clear_stack();
                    this.set_maneuver(43);
                } else {
                    if (this.actor instanceof HE_LERCHE3 && ((HE_LERCHE3) this.actor).bToFire) {
                        this.CT.WeaponControl[2] = true;
                        ((HE_LERCHE3) this.actor).bToFire = false;
                    }
                    if (this.actor instanceof TA_183 && ((TA_183) this.actor).bToFire) {
                        this.CT.WeaponControl[2] = true;
                        ((TA_183) this.actor).bToFire = false;
                    }
                    if (this.actor instanceof TA_152C && ((TA_152C) this.actor).bToFire) {
                        this.CT.WeaponControl[2] = true;
                        ((TA_152C) this.actor).bToFire = false;
                    }
                    if (this.actor instanceof Mig_17PF && ((Mig_17PF) this.actor).bToFire) {
                        this.CT.WeaponControl[2] = true;
                        ((Mig_17PF) this.actor).bToFire = false;
                    }
                    
                    // TODO: +++ By SAS~Storebror: Additional Checks for X-4 Launch
                    if (this.actor instanceof TypeX4Carrier) {
                        boolean canShootX4 = false;
                        if (Reflection.getField(this.actor, "bToFire") != null && Reflection.getField(this.actor, "tX4Prev") != null) {
                            canShootX4 = Reflection.getBoolean(this.actor, "bToFire");
                            if (canShootX4) {
                                Reflection.setBoolean(this.actor, "bToFire", false);
                                Reflection.setLong(this.actor, "tX4Prev", Time.current());
                            }
                        } else {
                            canShootX4 = this.CT.getbToFire();
                            if (canShootX4) {
                                if (Reflection.getField(this.actor, "tX4Prev") != null) {
                                    if (Time.current() < Reflection.getLong(this.actor, "tX4Prev") + 10000L) {
                                        canShootX4 = false;
                                    } else {
                                        Reflection.setLong(this.actor, "tX4Prev", Time.current());
                                    }
                                }
                                if (canShootX4) {
                                    this.CT.setbToFire(false);
                                    this.CT.settX4Prev(Time.current());
                                    if (Reflection.getField(this.actor, "bToFire") != null) {
                                        Reflection.setBoolean(this.actor, "bToFire", false);
                                    }
                                }
                            }
                        }
                        if (canShootX4) this.CT.WeaponControl[2] = true;
                    }
                    // ---
                    
//                    // TODO: +++ By SAS~Storebror: Additional Checks for guided missile carrier
//                    if (this.actor instanceof TypeGuidedMissileCarrier) {
//                        ((TypeGuidedMissileCarrier)this.actor).getGuidedMissileUtils().update();
//                    }
//                    // ---
                    
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.TargV.z < -100D)
                        // TODO: --- TD AI code backport from 4.13 ---
                        this.fighterUnderBomber(f);
                    else this.fighterVsBomber(f);
                    if (this.AOA > this.AOA_Crit - 2.0F && this.Alt > 15.0F) this.Or.increment(0.0F, 0.01F * (this.AOA_Crit - 2.0F - this.AOA), 0.0F);
                }
                break;
            case 27:
                // TODO: +++ TD AI code backport from 4.13 +++
                if (!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode() && !this.bKeepOrdnance) {
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                    // TODO: +++ TD AI code backport from 4.13 +++
                } else if (this.isTick(77, 0) && !this.target.isOk() && World.Rnd().nextInt(0, 1000) < 40 * (this.Skill + 1)) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                    // TODO: --- TD AI code backport from 4.13 ---
                } else if (this.target.getSpeedKMH() < 45.0F && this.target.Loc.z - Engine.land().HQ_Air(this.target.Loc.x, this.target.Loc.y) < 50.0 && this.target.actor.getArmy() != this.actor.getArmy()) {
                    this.target_ground = this.target.actor;
                    this.set_task(7);
                    this.clear_stack();
                    this.set_maneuver(43);
                } else {
                    
                    // TODO: +++ Added by SAS~Storebror: Make X-4 / Missile Carriers attack enemy fighters with missiles if no bomber is around
                    if (this.isMissileValidForFighterAttack()) {
//                        System.out.println("bToFire = " + ((TA_183) this.actor).bToFire);
                        if (this.actor instanceof HE_LERCHE3 && ((HE_LERCHE3) this.actor).bToFire) {
                            this.CT.WeaponControl[2] = true;
                            ((HE_LERCHE3) this.actor).bToFire = false;
                        }
                        if (this.actor instanceof TA_183 && ((TA_183) this.actor).bToFire) {
                            this.CT.WeaponControl[2] = true;
                            ((TA_183) this.actor).bToFire = false;
                        }
                        if (this.actor instanceof TA_152C && ((TA_152C) this.actor).bToFire) {
                            this.CT.WeaponControl[2] = true;
                            ((TA_152C) this.actor).bToFire = false;
                        }
                        if (this.actor instanceof Mig_17PF && ((Mig_17PF) this.actor).bToFire) {
                            this.CT.WeaponControl[2] = true;
                            ((Mig_17PF) this.actor).bToFire = false;
                        }
                        
                        // TODO: +++ By SAS~Storebror: Additional Checks for X-4 Launch
                        if (this.actor instanceof TypeX4Carrier) {
                            boolean canShootX4 = false;
                            if (Reflection.getField(this.actor, "bToFire") != null && Reflection.getField(this.actor, "tX4Prev") != null) {
                                canShootX4 = Reflection.getBoolean(this.actor, "bToFire");
                                if (canShootX4) {
                                    Reflection.setBoolean(this.actor, "bToFire", false);
                                    Reflection.setLong(this.actor, "tX4Prev", Time.current());
                                }
                            } else {
                                canShootX4 = this.CT.getbToFire();
                                if (canShootX4) {
                                    if (Reflection.getField(this.actor, "tX4Prev") != null) {
                                        if (Time.current() < Reflection.getLong(this.actor, "tX4Prev") + 10000L) {
                                            canShootX4 = false;
                                        } else {
                                            Reflection.setLong(this.actor, "tX4Prev", Time.current());
                                        }
                                    }
                                    if (canShootX4) {
                                        this.CT.setbToFire(false);
                                        this.CT.settX4Prev(Time.current());
                                        if (Reflection.getField(this.actor, "bToFire") != null) {
                                            Reflection.setBoolean(this.actor, "bToFire", false);
                                        }
                                    }
                                }
                            }
                            if (canShootX4) this.CT.WeaponControl[2] = true;
//                            System.out.println("canShootX4 = " + canShootX4);
                        }
                        // ---
                        
//                        // TODO: +++ By SAS~Storebror: Additional Checks for guided missile carrier
//                        if (this.actor instanceof TypeGuidedMissileCarrier) {
//                            ((TypeGuidedMissileCarrier)this.actor).getGuidedMissileUtils().update();
//                        }
//                        // ---
                    }
                    // ---
                    
                    if (this.first && this.actor instanceof TypeAcePlane) {
                        if (this.target != null && this.target.actor != null && this.target.actor.getArmy() != this.actor.getArmy()) this.target.Skill = 0;
                        if (this.danger != null && this.danger.actor != null && this.danger.actor.getArmy() != this.actor.getArmy()) this.danger.Skill = 0;
                    }
                    if (this.target.actor.getArmy() != this.actor.getArmy()) ((Maneuver) this.target).danger = this;
                    if (this.isTick(64, 0)) {
                        float f111 = (this.target.Energy - this.Energy) * 0.1019F;
                        Ve.sub(this.target.Loc, this.Loc);
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                        float f131 = 470.0F + (float) Ve.x * 120.0F - f111;
                        if (f131 < 0.0F) {
                            this.clear_stack();
                            this.set_maneuver(62);
                        }
                    }
                    this.fighterVsFighter(f);
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.minElevCoeff = 20F;
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (this.AOA > this.AOA_Crit - 1.0F && this.Alt > 15.0F) this.Or.increment(0.0F, 0.01F * (this.AOA_Crit - 1.0F - this.AOA), 0.0F);
                    if (this.mn_time > 100.0F) {
                        this.target = null;
                        this.pop();
                    }
                }
                break;
            case 62:
                if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (((Aircraft) this.actor).aircIndex() == 0) this.Group.chooseTargetGroup();
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                } else if (this.target.getSpeedKMH() < 45.0F && this.target.Loc.z - Engine.land().HQ_Air(this.target.Loc.x, this.target.Loc.y) < 50.0 && this.target.actor.getArmy() != this.actor.getArmy()) {
                    this.target_ground = this.target.actor;
                    this.set_task(7);
                    this.clear_stack();
                    this.set_maneuver(43);
                } else {
                    if (this.first && this.actor instanceof TypeAcePlane) {
                        if (this.target != null && this.target.actor != null && this.target.actor.getArmy() != this.actor.getArmy()) this.target.Skill = 0;
                        if (this.danger != null && this.danger.actor != null && this.danger.actor.getArmy() != this.actor.getArmy()) this.danger.Skill = 0;
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.isTick(37, 0) && !this.target.isOk() && World.Rnd().nextInt(0, 100) < 20 * (this.Skill + 1)) {
                        this.target = null;
                        this.clear_stack();
                        this.set_maneuver(0);
                    } else if (this.isTick(164, 0) && !this.Group.inCorridor(this.Loc)) {
                        this.target = null;
                        this.clear_stack();
                        this.set_maneuver(0);
                    } else {
                        // TODO: --- TD AI code backport from 4.13 ---
                        if (this.target.actor.getArmy() != this.actor.getArmy()) ((Maneuver) this.target).danger = this;
                        this.goodFighterVsFighter(f);
                        // TODO: +++ TD AI code backport from 4.13 +++
                    }
                    // TODO: --- TD AI code backport from 4.13 ---
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 80:
                if ((!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode()) && !this.bKeepOrdnance) {
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                } else if (this.target.getSpeedKMH() < 45F && this.target.Loc.z - Engine.land().HQ_Air(this.target.Loc.x, this.target.Loc.y) < 50D && this.target.actor.getArmy() != this.actor.getArmy()) {
                    this.target_ground = this.target.actor;
                    this.set_task(7);
                    this.clear_stack();
                    this.set_maneuver(43);
                } else {
                    if (this.first && this.actor instanceof TypeAcePlane) {
                        if (this.target != null && this.target.actor != null && this.target.actor.getArmy() != this.actor.getArmy()) this.target.Skill = 0;
                        if (this.danger != null && this.danger.actor != null && this.danger.actor.getArmy() != this.actor.getArmy()) this.danger.Skill = 0;
                    }
                    if (this.target.actor.getArmy() != this.actor.getArmy()) ((Maneuver) this.target).danger = this;
                    switch (this.submaneuver) {
                        case 0:
                            tmpLoc.set(this.target.Loc);
                            this.bracketSide = World.Rnd().nextInt(0, 1);
                            this.scaledApproachV.set(World.Rnd().nextFloat(800F, 1500F), 1000 - this.bracketSide * 2000 + World.Rnd().nextFloat(-500F, 250F), World.Rnd().nextFloat(-100F, 1250F));
                            this.target.Or.transformInv(this.scaledApproachV);
                            tmpLoc.add(this.scaledApproachV);
                            Ve.sub(tmpLoc, this.Loc);
                            this.submaneuver++;
                            break;

                        case 1:
                            this.target.Loc.add(this.scaledApproachV);
                            Ve.sub(this.target.Loc, this.Loc);
                            break;
                    }
                    this.dist = (float) Ve.length();
                    this.Or.transformInv(Ve);
                    Ve.normalize();
                    this.attackTurnToDirection(this.dist, f, 4F + this.Skill * 4F);
                    this.setSpeedMode(9);
                    if (this.AOA > this.AOA_Crit - 1.0F && this.Alt > 15F) this.Or.increment(0.0F, 0.01F * (this.AOA_Crit - 1.0F - this.AOA), 0.0F);
                    if (this.mn_time > 20F || this.dist < 1000F) {
                        this.submaneuver = 0;
                        if (this.Group.nOfAirc > 2 && this.Group.airc[2] != null) {
                            Maneuver maneuver12 = (Maneuver) this.Group.airc[2].FM;
                            maneuver12.clear_stack();
                            maneuver12.set_maneuver(27);
                        }
                        this.push(27);
                        this.pop();
                    }
                }
                break;

            case 98:
                if (this.submaneuver == 1 && (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets())) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                } else {
                    switch (this.submaneuver) {
                        case 0:
                            this.target = this.Group.setAAttackObject(((Aircraft) this.actor).aircIndex());
                            this.gattackCounter = 0;
                            this.submaneuver++;
                            break;

                        case 1:
                            this.boomAttack(f);
                            this.setSpeedMode(11);
                            break;
                    }
                    if (this.gattackCounter > 51 || this.mn_time > 120F) {
                        this.submaneuver = 0;
                        this.gattackCounter = 0;
                        this.push(97);
                        this.push(58);
                        this.pop();
                    }
                }
                break;

            case 81:
                this.wingman(false);
                this.target = this.Group.setAAttackObject(((Aircraft) this.actor).aircIndex());
                this.clear_stack();
                this.set_maneuver(27);
                break;

            case 92:
                if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                    break;
                }
                switch (this.submaneuver) {
                    default:
                        break;

                    case 0:
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = (this.getSpeedKMH() - 180F - this.Or.getTangage() * 10F - this.getVertSpeed() * 5F) * 0.004F;
                        this.CT.ElevatorControl = clamp11(this.dA);
                        this.sub_Man_Count++;
                        if (this.sub_Man_Count > ((Aircraft) this.actor).aircIndex() * World.Rnd().nextInt(40, 75)) this.submaneuver++;
                        break;

                    case 1:
                        tmpLoc.set(this.target.Loc);
                        this.scaledApproachV.set(World.Rnd().nextFloat(50F, 250F), 10 - this.bracketSide * 20 + World.Rnd().nextFloat(-50F, 50F), World.Rnd().nextFloat(50F, 120F));
                        this.target.Or.transformInv(this.scaledApproachV);
                        tmpLoc.add(this.scaledApproachV);
                        this.submaneuver++;
                        break;
                }
                break;
            case 93:
                if (this.maneuver == 93) {
                    if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                        this.target = null;
                        this.clear_stack();
                        this.set_maneuver(0);
                        break;
                    }
                    switch (this.submaneuver) {
                        case 0:
                            tmpLoc.set(this.target.Loc);
                            switch (((Aircraft) this.actor).aircIndex()) {
                                case 0:
                                    this.scaledApproachV.set(World.Rnd().nextFloat(800F, 1200F), World.Rnd().nextFloat(-200F, 200F), World.Rnd().nextFloat(800F, 1200F));
                                    break;

                                case 1:
                                    this.scaledApproachV.set(World.Rnd().nextFloat(-100F, 100F), World.Rnd().nextFloat(-800F, -1200F), World.Rnd().nextFloat(800F, 1200F));
                                    break;

                                case 2:
                                    this.scaledApproachV.set(World.Rnd().nextFloat(-100F, 100F), World.Rnd().nextFloat(800F, 1200F), World.Rnd().nextFloat(800F, 1200F));
                                    break;

                                case 3:
                                    this.scaledApproachV.set(World.Rnd().nextFloat(-800F, -1200F), World.Rnd().nextFloat(-200F, 200F), World.Rnd().nextFloat(800F, 1200F));
                                    break;

                                default:
                                    this.scaledApproachV.set(World.Rnd().nextFloat(-800F, -1200F), World.Rnd().nextFloat(-100F, 100F), World.Rnd().nextFloat(900F, 1100F));
                                    break;
                            }
                            this.target.Or.transformInv(this.scaledApproachV);
                            tmpLoc.add(this.scaledApproachV);
                            this.submaneuver += 2;
                            break;
                    }
                }
                break;
            case 94:
                if ((!(this instanceof RealFlightModel) || !((RealFlightModel) this).isRealMode()) && !this.bKeepOrdnance) {
                    this.bombsOut = true;
                    this.CT.dropFuelTanks();
                }
                if (!this.isOk()) this.set_maneuver(0);
                else {
                    switch (this.submaneuver) {
                        case 2:
                            Ve.sub(tmpLoc, this.Loc);
                            if (this.dist < 500F) this.submaneuver++;
                            break;

                        case 3:
                            Ve.sub(this.target.Loc, this.Loc);
                            Ve.add(this.scaledApproachV);
                            break;
                    }
                    if (this.submaneuver > 1) {
                        this.dist = (float) Ve.length();
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                        this.attackTurnToDirection(this.dist, f, 10F + this.Skill * 8F);
                    }
                    this.setSpeedMode(9);
                    if (this.mn_time > 20F || this.submaneuver == 3 && this.dist < 1000F) {
                        this.sub_Man_Count = 0;
                        this.submaneuver = 0;
                        this.push(27);
                        this.pop();
                    }
                }
                break;

            case STRAIGHT_AND_LEVEL:
                this.CT.setPowerControl(1.1F);
                this.setTurn(500F, 0.0F, 0.0F);
                if (this.mn_time > 715F / this.Vmax) this.pop();
                break;

            case 99:
                if (this.target == null || !Actor.isValid(this.target.actor) || this.target.isTakenMortalDamage() || this.target.actor.getArmy() == this.actor.getArmy() || !this.hasCourseWeaponBullets()) {
                    this.target = null;
                    this.clear_stack();
                    this.set_maneuver(0);
                } else {
                    switch (this.submaneuver) {
                        default:
                            break;

                        case 0:
                            this.oldTarget = this.target;
                            this.cloudHeight = Mission.curCloudsHeight();
                            this.Group.setEnemyConvPoint(this.target, this.actor);
                            this.submaneuver++;
                            break;

                        case 1:
                            this.lastKnownTargetLoc.set(this.target.Loc);
                            this.scaledApproachV.set(this.target.Vwld);
                            if (this.Group != null && this.Group.outNum < 0) switch (this.Group.outNum) {
                                case -1:
                                    if (this.Group.nOfAirc < 3) this.timeToSearchTarget = 50 + World.Rnd().nextInt(0, 100);
                                    else this.timeToSearchTarget = 5 + World.Rnd().nextInt(0, 20);
                                    this.desiredAlt = this.cloudHeight + 1000F + World.Rnd().nextInt(-200, 500);
                                    this.scaledApproachV.add(World.Rnd().nextInt(-50, 50), World.Rnd().nextInt(-50, 50), World.Rnd().nextInt(-25, 25));
                                    break;

                                case -2:
                                    switch (((Aircraft) this.actor).aircIndex()) {
                                        case 0:
                                            this.scaledApproachV.add(World.Rnd().nextInt(-50, 50), World.Rnd().nextInt(-50, 50), World.Rnd().nextInt(10, 50));
                                            this.desiredAlt = this.cloudHeight + 1500F + World.Rnd().nextInt(-200, 500);
                                            break;

                                        case 1:
                                            this.scaledApproachV.add(World.Rnd().nextInt(-50, -120), World.Rnd().nextInt(-50, -120), World.Rnd().nextInt(0, 50));
                                            this.desiredAlt = this.cloudHeight - World.Rnd().nextInt(200, 500);
                                            break;

                                        case 2:
                                            this.scaledApproachV.add(World.Rnd().nextInt(20, 50), World.Rnd().nextInt(20, 50), World.Rnd().nextInt(-20, 50));
                                            this.desiredAlt = this.cloudHeight + 1500F + World.Rnd().nextInt(-200, 500);
                                            break;

                                        case 3:
                                            this.scaledApproachV.add(World.Rnd().nextInt(50, 120), World.Rnd().nextInt(50, 120), World.Rnd().nextInt(-50, 30));
                                            this.desiredAlt = this.cloudHeight - World.Rnd().nextInt(200, 500);
                                            break;
                                    }
                                    this.timeToSearchTarget = 50 + World.Rnd().nextInt(0, -100 * this.Group.outNum);
                                    break;

                                case -3:
                                    switch (((Aircraft) this.actor).aircIndex()) {
                                        case 0:
                                            this.scaledApproachV.add(World.Rnd().nextInt(20, 50), World.Rnd().nextInt(20, 50), World.Rnd().nextInt(10, 50));
                                            this.desiredAlt = this.cloudHeight + 1500F + World.Rnd().nextInt(-200, 500);
                                            break;

                                        case 1:
                                            this.scaledApproachV.add(World.Rnd().nextInt(-50, -120), World.Rnd().nextInt(-50, -120), World.Rnd().nextInt(0, 50));
                                            this.desiredAlt = this.cloudHeight + 1500F + World.Rnd().nextInt(-200, 500);
                                            break;

                                        case 2:
                                            this.scaledApproachV.add(World.Rnd().nextInt(20, 50), World.Rnd().nextInt(20, 50), World.Rnd().nextInt(-20, 50));
                                            this.desiredAlt = this.cloudHeight - World.Rnd().nextInt(200, 500);
                                            break;

                                        case 3:
                                            this.scaledApproachV.add(World.Rnd().nextInt(50, 120), World.Rnd().nextInt(50, 120), World.Rnd().nextInt(-50, 30));
                                            this.desiredAlt = this.cloudHeight - World.Rnd().nextInt(200, 500);
                                            break;
                                    }
                                    this.timeToSearchTarget = 100 + World.Rnd().nextInt(0, 200);
                                    break;

                                default:
                                    this.timeToSearchTarget = 100 + World.Rnd().nextInt(0, 200);
                                    this.desiredAlt = this.cloudHeight + 1500F + World.Rnd().nextInt(-200, 500);
                                    break;
                            }
                            else {
                                if (this.Group != null && this.Group.outNum > 0) {
                                    this.getDifferentTarget();
                                    this.pop();
                                    break;
                                }
                                this.timeToSearchTarget = 100 + World.Rnd().nextInt(0, 200 / this.Group.nOfAirc);
                                this.desiredAlt = this.cloudHeight + 1500F + World.Rnd().nextInt(-200, 500);
                            }
                            Ve.sub(this.target.Loc, this.Loc);
                            this.dist = (float) Ve.length();
                            this.lastKnownTargetLoc.x += this.scaledApproachV.x * Time.tickConstLenFs();
                            this.lastKnownTargetLoc.y += this.scaledApproachV.y * Time.tickConstLenFs();
                            this.lastKnownTargetLoc.z += this.scaledApproachV.z * Time.tickConstLenFs();
                            this.submanDelay = (int) (this.dist * 0.1F);
                            if (this.submanDelay > 5000) this.submanDelay = 500;
                            this.submaneuver++;
                            break;

                        case 2:
                            this.lastKnownTargetLoc.x += this.scaledApproachV.x * Time.tickConstLenFs();
                            this.lastKnownTargetLoc.y += this.scaledApproachV.y * Time.tickConstLenFs();
                            this.lastKnownTargetLoc.z += this.scaledApproachV.z * Time.tickConstLenFs();
                            Ve.sub(this.lastKnownTargetLoc, this.Loc);
                            this.dist = (float) Ve.length();
                            if (this.dist < 200F || this.sub_Man_Count > 100) {
                                this.sub_Man_Count = 0;
                                if (!VisCheck.seekCheck((Aircraft) this.actor, (Aircraft) this.target.actor)) {
                                    float f29 = Math.abs(this.target.getOverload());
                                    float f31 = this.dist * 0.05F;
                                    if (World.Rnd().nextFloat(0.0F, 70F + this.Skill * 10F) > 10F + f29 * 5F + f31) this.submaneuver--;
                                    else this.submaneuver++;
                                } else {
                                    if (this.target != null) this.Group.setEnemyConvPoint(this.target, this.actor);
                                    this.pop();
                                }
                            }
                            this.Or.transformInv(Ve);
                            Ve.normalize();
                            this.attackTurnToDirection(this.dist, f, 2.0F + this.Skill * 2.0F);
                            break;

                        case 3:
                            if (this.sub_Man_Count > 100) {
                                this.sub_Man_Count = 0;
                                if (!VisCheck.seekCheck((Aircraft) this.actor, (Aircraft) this.target.actor)) {
                                    this.submaneuver--;
                                    Point3d point3d = this.Group.getEnemyConvPoint(this.target, this.actor);
                                    if (point3d != null) {
                                        this.dist = (float) this.Loc.distance(point3d);
                                        if (this.dist > 700F) {
                                            point3d.z += World.Rnd().nextInt(-200, 200);
                                            point3d.y += World.Rnd().nextFloat(-this.dist / 10F, this.dist / 10F);
                                            point3d.x += World.Rnd().nextFloat(-this.dist / 10F, -this.dist / 10F);
                                        }
                                        this.lastKnownTargetLoc.set(point3d);
                                        this.scaledApproachV.set(this.target.Vwld);
                                    }
                                } else this.pop();
                            }
                            if (this.timeToSearchTarget < this.mn_time) {
                                this.getDifferentTarget();
                                if (this.target != this.oldTarget) this.pop();
                                else if (((Aircraft) this.actor).aircIndex() == 0) {
                                    AirGroupList.delAirGroup(this.Group.enemies, 0, ((Maneuver) this.target).Group);
                                    this.Group.setEnemyFighters();
                                    this.clear_stack();
                                    this.set_maneuver(0);
                                } else {
                                    this.clear_stack();
                                    this.set_maneuver(0);
                                }
                            }
                            this.sub_Man_Count++;
                            break;
                    }
                    if (this.submaneuver == 3) {
                        this.setSpeedMode(7);
                        this.smConstPower = 0.9F;
                        this.dA = this.Or.getKren();
                        if (this.dA > 0.0F) this.dA -= 35F;
                        else this.dA -= 325F;
                        if (this.dA < -180F) this.dA += 360F;
                        this.dA = -0.01F * this.dA;
                        this.CT.AileronControl = clamp11(this.dA);
                        this.dA = 0.002F * (this.desiredAlt - (float) this.Loc.z + 250F);
                        if (this.dA > 0.66F) this.dA = 0.66F;
                        this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() - 1.0F) + this.dA);
                    }
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 70:
                this.checkGround = false;
                this.checkStrike = false;
                this.frequentControl = true;
                this.stallable = false;
                if (this.actor instanceof HE_LERCHE3) switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.AP.setStabAll(false);
                        this.submaneuver++;
                        this.sub_Man_Count = 0;
                        /* fall through */
                    case SUB_MAN1:
                        if (this.sub_Man_Count == 0) this.CT.AileronControl = World.cur().rnd.nextFloat(-0.15F, 0.15F);
                        this.CT.AirBrakeControl = 1.0F;
                        this.CT.setPowerControl(1.0F);
                        this.CT.ElevatorControl = Aircraft.cvt(this.Or.getTangage(), 0.0F, 60.0F, 1.0F, 0.0F);
                        if (this.Or.getTangage() > 30.0F) {
                            this.submaneuver++;
                            this.sub_Man_Count = 0;
                        }
                        this.sub_Man_Count++;
                        break;
                    case SUB_MAN2:
                        this.CT.AileronControl = 0.0F;
                        this.CT.ElevatorControl = 0.0F;
                        this.CT.setPowerControl(0.1F);
                        this.Or.increment(0.0F, (float) (f * 0.1 * this.sub_Man_Count * (90.0 - this.Or.getTangage())), 0.0F);
                        if (this.Or.getTangage() > 89.0F) {
                            this.saveOr.set(this.Or);
                            this.submaneuver++;
                        }
                        this.sub_Man_Count++;
                        break;
                    case SUB_MAN3:
                        this.CT.ElevatorControl = 0.0F;
                        if (this.Alt > 10.0F) this.CT.setPowerControl(0.33F);
                        else this.CT.setPowerControl(0.0F);
                        if (this.Alt < 20.0F) {
                            if (this.Vwld.z < -4.0) this.Vwld.z *= 0.95;
                            if (this.Vwld.lengthSquared() < 1.0) this.EI.setEngineStops();
                        }
                        this.Or.set(this.saveOr);
                        if (!(this.mn_time > 100.0F)) break;
                        this.Vwld.set(0.0, 0.0, 0.0);
                        MsgDestroy.Post(Time.current() + 12000L, this.actor);
                }
                break;
            case 25:
                this.wingman(false);
                if (this.AP.way.isLanding()) {
                    if (this.AP.way.isLandingOnShip()) {
                        this.AP.way.landingAirport.rebuildLandWay(this);
                        if (this.CT.GearControl == 1.0F && this.CT.arrestorControl < 1.0F && !this.Gears.onGround()) this.AS.setArrestor(this.actor, 1);
                    }
                    if (this.first) {
                        this.AP.setWayPoint(true);
                        this.doDumpBombsPassively();
                        this.submaneuver = SUB_MAN0;
                    }
                    if (this.actor instanceof HE_LERCHE3 && this.Alt < 50.0F) this.maneuver = 70;
                    this.AP.way.curr().getP(Po);
                    int k = this.AP.way.Cur();
                    float f171 = (float) this.Loc.z - this.AP.way.last().z();
                    this.AP.way.setCur(k);
                    this.Alt = Math.min(this.Alt, f171);
                    Po.add(0.0, 0.0, -3.0);
                    Ve.sub(Po, this.Loc);
                    float f20 = (float) Ve.length();
                    boolean flag3 = this.Alt > 4.5F + this.Gears.H && this.AP.way.Cur() < 8;
                    if (this.AP.way.isLandingOnShip()) flag3 = this.Alt > 4.5F + this.Gears.H && this.AP.way.Cur() < 8;
                    if (flag3) {
                        this.AP.way.prev();
                        this.AP.way.curr().getP(Pc);
                        this.AP.way.next();
                        tmpV3f.sub(Po, Pc);
                        tmpV3f.normalize();
                        if (tmpV3f.dot(Ve) < 0.0 && f20 > 1000.0F && !this.TaxiMode) {
                            this.AP.way.first();
                            this.push(10);
                            this.pop();
                            this.CT.GearControl = 0.0F;
                        }
                        float f25 = (float) tmpV3f.dot(Ve);
                        tmpV3f.scale(-f25);
                        tmpV3f.add(Po, tmpV3f);
                        tmpV3f.sub(this.Loc);
                        float f29 = 5.0E-4F * (3000.0F - f20);
                        if (f29 > 1.0F) f29 = 1.0F;
                        if (f29 < 0.1F) f29 = 0.1F;
                        float f31 = (float) tmpV3f.length();
                        if (f31 > 40.0F * f29) {
                            f31 = 40.0F * f29;
                            tmpV3f.normalize();
                            tmpV3f.scale(f31);
                        }
                        float f33 = this.VminFLAPS;
                        if (this.AP.way.Cur() >= 6) {
                            if (this.AP.way.isLandingOnShip()) {
                                if (Actor.isAlive(this.AP.way.landingAirport) && this.AP.way.landingAirport instanceof AirportCarrier) {
                                    float f34 = (float) ((AirportCarrier) this.AP.way.landingAirport).speedLen();
                                    if (this.VminFLAPS < f34 + 10.0F) f33 = f34 + 10.0F;
                                }
                            } else f33 = this.VminFLAPS * 1.2F;
                            if (f33 < 14.0F) f33 = 14.0F;
                        } else f33 = this.VminFLAPS * 2.0F;
                        double d4 = this.Vwld.length();
                        double d6 = f33 - d4;
                        float f35 = 2.0F * f;
                        if (d6 > f35) d6 = f35;
                        if (d6 < -f35) d6 = -f35;
                        Ve.normalize();
                        Ve.scale(d4);
                        Ve.add(tmpV3f);
                        Ve.sub(this.Vwld);
                        float f36 = (50.0F * f29 - f31) * f;
                        if (Ve.length() > f36) {
                            Ve.normalize();
                            Ve.scale(f36);
                        }
                        this.Vwld.add(Ve);
                        this.Vwld.normalize();
                        this.Vwld.scale(d4 + d6);
                        this.Loc.x += this.Vwld.x * f;
                        this.Loc.y += this.Vwld.y * f;
                        this.Loc.z += this.Vwld.z * f;
                        this.Or.transformInv(tmpV3f);
                        tmpOr.setAT0(this.Vwld);
                        float f37 = 0.0F;
                        if (this.AP.way.isLandingOnShip()) f37 = 0.9F * (45.0F - this.Alt);
                        else f37 = 0.7F * (20.0F - this.Alt);
                        if (f37 < 0.0F) f37 = 0.0F;
                        tmpOr.increment(0.0F, 4.0F + f37, (float) (tmpV3f.y * 0.5));
                        this.Or.interpolate(tmpOr, 0.5F * f);
                        this.callSuperUpdate = false;
                        this.W.set(0.0, 0.0, 0.0);
                        this.CT.ElevatorControl = clamp11(0.05F + 0.3F * f37);
                        this.CT.RudderControl = clamp11((float) (-tmpV3f.y * 0.02));
                        this.direction = this.Or.getAzimut();
                    } else this.AP.setStabDirection(true);
                } else this.AP.setStabDirection(true);
                this.dA = this.CT.ElevatorControl;
                this.AP.update(f);
                this.setSpeedControl(f);
                this.CT.ElevatorControl = clamp11(this.dA);
                if (this.maneuver == 25) {
                    if (this.Alt > 60.0F) {
                        if (this.Alt < 160.0F) this.CT.FlapsControl = 0.32F;
                        else this.CT.FlapsControl = 0.0F;
                        this.setSpeedMode(7);
                        this.smConstPower = 0.2F;
                        this.dA = Math.min(130.0F + this.Alt, 270.0F);
                        if (this.Vwld.z > 0.0 || this.getSpeedKMH() < this.dA) this.dA = -1.2F * f;
                        else this.dA = 1.2F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl * 0.9F + this.dA * 0.1F);
                    } else {
                        this.minElevCoeff = 15.0F;
                        if (this.AP.way.Cur() >= 6 || this.AP.way.Cur() == 0) {
                            if (this.Or.getTangage() < -5.0F) this.Or.increment(0.0F, -this.Or.getTangage() - 5.0F, 0.0F);
                            if (this.Or.getTangage() > this.Gears.Pitch + 10.0F) this.Or.increment(0.0F, -(this.Or.getTangage() - (this.Gears.Pitch + 10.0F)), 0.0F);
                        }
                        this.CT.FlapsControl = 1.0F;
                        if (this.Vrel.length() < 1.0) {
                            this.CT.FlapsControl = this.CT.BrakeControl = 0.0F;
                            if (!this.TaxiMode) {
                                this.setSpeedMode(8);
                                if (this.AP.way.isLandingOnShip()) {
                                    if (this.CT.getFlap() < 0.0010F) this.AS.setWingFold(this.actor, 1);
                                    this.CT.BrakeControl = 1.0F;
                                    if (this.CT.arrestorControl == 1.0F && this.Gears.onGround()) this.AS.setArrestor(this.actor, 0);
                                    MsgDestroy.Post(Time.current() + 25000L, this.actor);
                                } else {
                                    this.EI.setEngineStops();
                                    if (this.EI.engines[0].getPropw() < 1.0F) if (this.actor != World.getPlayerAircraft()) MsgDestroy.Post(Time.current() + 12000L, this.actor);
                                }
                            }
                        }
                        if (this.getSpeed() < this.VmaxFLAPS * 0.21F) this.CT.FlapsControl = 0.0F;
                        // TODO: +++ TD AI code backport from 4.13 +++
                        if (this.Vrel.length() < this.VmaxFLAPS * 0.25F && this.wayCurPos == null && !this.AP.way.isLandingOnShip() && this.isEnableToTaxi()) {
                            // TODO: --- TD AI code backport from 4.13 ---
                            this.TaxiMode = true;
                            this.AP.way.setCur(0);
                            return;
                        }
                        if (this.getSpeed() > this.VminFLAPS * 0.6F && this.Alt < 10.0F) {
                            this.setSpeedMode(8);
                            if (this.AP.way.isLandingOnShip() && this.CT.bHasArrestorControl) {
                                if (this.Vwld.z < -5.5) this.Vwld.z *= 0.95;
                                if (this.Vwld.z > 0.0) this.Vwld.z *= 0.91;
                            } else {
                                if (this.Vwld.z < -0.6) this.Vwld.z *= 0.94;
                                if (this.Vwld.z < -2.5) this.Vwld.z = -2.5;
                                if (this.Vwld.z > 0.0) this.Vwld.z *= 0.91;
                            }
                        }
                        float f141 = this.Gears.Pitch - 2.0F;
                        if (f141 < 5.0F) f141 = 5.0F;
                        if (this.Alt < 7.0F && this.Or.getTangage() < f141 || this.AP.way.isLandingOnShip()) this.CT.ElevatorControl += 1.5F * f;
                        this.CT.ElevatorControl += 0.05 * this.getW().y;
                        if (this.Gears.onGround()) {
                            if (this.Gears.Pitch > 5.0F) {
                                if (this.Or.getTangage() < this.Gears.Pitch) this.CT.ElevatorControl += 1.5F * f;
                                if (!this.AP.way.isLandingOnShip()) this.CT.ElevatorControl += 0.3 * this.getW().y;
                            } else this.CT.ElevatorControl = 0.0F;
                            if (!this.TaxiMode) {
                                this.AFo.setDeg(this.Or.getAzimut(), this.direction);
                                this.CT.RudderControl = clamp11(8.0F * this.AFo.getDiffRad() + 0.5F * (float) this.getW().z);
                            } else this.CT.RudderControl = 0.0F;
                        }
                    }
                    this.AP.way.curr().getP(Po);
                    return;
                }
                return;

            // TODO: Check Backport from TD AI code in 4.13, where this case is empty (straight return)! +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            case 66:
                if (!this.isCapableOfTaxiing() || this.EI.getThrustOutput() < 0.01F) {
                    this.set_task(3);
                    this.maneuver = 0;
                    this.set_maneuver(49);
                    this.AP.setStabAll(false);
                } else {
                    if (this.AS.isPilotDead(0)) {
                        this.set_task(3);
                        this.maneuver = 0;
                        this.set_maneuver(44);
                        this.setSpeedMode(8);
                        this.smConstPower = 0.0F;
                        if (Airport.distToNearestAirport(this.Loc) > 900.0) ((Aircraft) this.actor).postEndAction(6000.0, this.actor, 3, null);
                        else MsgDestroy.Post(Time.current() + 300000L, this.actor);
                    } else {
                        P.x = this.Loc.x;
                        P.y = this.Loc.y;
                        P.z = this.Loc.z;
                        Vcur.set(this.Vwld);
                        if (this.wayCurPos == null) {
                            World.cur().airdrome.findTheWay((Pilot) this);
                            this.wayPrevPos = this.wayCurPos = this.getNextAirdromeWayPoint();
                        }
                        if (this.wayCurPos != null) {
                            Point_Any point_any = this.wayCurPos;
                            Pcur.set((float) P.x, (float) P.y);
                            float f21 = Pcur.distance(point_any);
                            V_to.sub(point_any, Pcur);
                            V_to.normalize();
                            float f26 = 5.0F + 0.1F * f21;
                            if (f26 > 12.0F) f26 = 12.0F;
                            if (f26 > 0.9F * this.VminFLAPS) f26 = 0.9F * this.VminFLAPS;
                            if (this.curAirdromePoi < this.airdromeWay.length && f21 < 15.0F || f21 < 4.0F) {
                                f26 = 0.0F;
                                Point_Any point_any2 = this.getNextAirdromeWayPoint();
                                if (point_any2 == null) {
                                    this.CT.setPowerControl(0.0F);
                                    this.Loc.set(P);
                                    this.Loc.set(this.Loc);
                                    if (!this.finished) {
                                        this.finished = true;
                                        int l = 1000;
                                        if (this.wayCurPos != null) l = 2400000;
                                        this.actor.collide(true);
                                        this.Vwld.set(0.0, 0.0, 0.0);
                                        this.CT.setPowerControl(0.0F);
                                        this.EI.setCurControlAll(true);
                                        this.EI.setEngineStops();

                                        if (this.actor != World.getPlayerAircraft()) {
                                            MsgDestroy.Post(Time.current() + l, this.actor);
                                            this.setStationedOnGround(true);
                                            this.maneuver = 0;
                                            this.set_maneuver(44);
                                        }
                                        return;
                                    }
                                    return;
                                }
                                this.wayPrevPos = this.wayCurPos;
                                this.wayCurPos = point_any2;
                            }
                            V_to.scale(f26);
                            float f30 = 2.0F * f;
                            Vdiff.set(V_to);
                            Vdiff.sub(Vcur);
                            float f32 = (float) Vdiff.length();
                            if (f32 > f30) {
                                Vdiff.normalize();
                                Vdiff.scale(f30);
                            }
                            Vcur.add(Vdiff);
                            tmpOr.setYPR(FMMath.RAD2DEG((float) Vcur.direction()), this.Or.getPitch(), 0.0F);
                            this.Or.interpolate(tmpOr, 0.2F);
                            this.Vwld.x = Vcur.x;
                            this.Vwld.y = Vcur.y;
                            P.x += Vcur.x * f;
                            P.y += Vcur.y * f;
                        } else {
                            this.wayPrevPos = this.wayCurPos = new Point_Null((float) this.Loc.x, (float) this.Loc.y);

                            if (this.actor != World.getPlayerAircraft()) {
                                MsgDestroy.Post(Time.current() + 30000L, this.actor);
                                this.setStationedOnGround(true);
                            }
                        }
                        this.Loc.set(P);
                        this.Loc.set(this.Loc);
                        return;
                    }
                    return;
                }
                break;

            case 49:
                this.emergencyLanding(f);
                break;
            case 64:
                if (this.actor instanceof TypeGlider) this.pop();
                else {
                    if (this.actor instanceof HE_LERCHE3) {
                        boolean flag1 = Actor.isAlive(this.AP.way.takeoffAirport) && this.AP.way.takeoffAirport instanceof AirportCarrier;
                        if (!flag1) this.callSuperUpdate = false;
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.Group == null || this.Group.airc == null || this.Group.airc[0] == null) return; // TODO: Null check added by SAS~Storebror
                    FlightModel flightmodel = this.Group.airc[0].FM;
                    if ((this.AP.way.first().waypointType == 4 || this.AP.way.first().waypointType == 5) && flightmodel.isCapableOfTaxiing() && flightmodel instanceof RealFlightModel && ((RealFlightModel) flightmodel).isRealMode()
                            && !this.bGentsStartYourEngines)
                        break;
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.CT.BrakeControl = 1.0F;
                    this.CT.ElevatorControl = 0.5F;
                    this.CT.setPowerControl(0.0F);
                    this.EI.setCurControlAll(false);
                    this.setSpeedMode(0);
                    if (World.Rnd().nextFloat() < 0.05F) {
                        this.EI.setCurControl(this.submaneuver, true);
                        if (this.EI.engines[this.submaneuver].getStage() == 0) {
                            this.setSpeedMode(0);
                            this.CT.setPowerControl(0.05F);
                            this.EI.engines[this.submaneuver].setControlThrottle(0.2F);
                            this.EI.toggle();
                            if (this.actor instanceof BI_1 || this.actor instanceof BI_6) {
                                this.pop();
                                break;
                            }
                        }
                    }
                    if (this.EI.engines[this.submaneuver].getStage() == 6) {
                        this.setSpeedMode(0);
                        this.EI.engines[this.submaneuver].setControlThrottle(0.0F);
                        this.submaneuver++;
                        this.CT.setPowerControl(0.0F);
                        if (this.submaneuver > this.EI.getNum() - 1) {
                            this.EI.setCurControlAll(true);
                            this.pop();
                        }
                    }
                }
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case TAXI_TO_TO:
                if (this.Group == null || this.actor == null) break;
                int j1 = 5 + this.Group.numInGroup((Aircraft) this.actor) * 8;
                if (this.mn_time < j1) break;
                P.x = this.Loc.x;
                P.y = this.Loc.y;
                P.z = this.Loc.z;
                if (this.actor == World.getPlayerAircraft()) {
                    World.getPlayerAircraft();
                    if (!Aircraft.isPlayerTaxing()) {
                        this.pop();
                        this.set_maneuver(21);
                        break;
                    }
                }
                Vcur.x = (float) this.Vwld.x;
                Vcur.y = (float) this.Vwld.y;
                if (this.curAirdromePoi == 0 && ((Aircraft) this.actor).aircIndex() > 0 && this.Leader != null) {
                    Ve.sub(this.Leader.Loc, this.Loc);
                    double d1 = Ve.length();
                    Ve.normalize();
                    this.Or.transformInv(Ve);
                    if (Ve.x < 0.7D && d1 < 80D) return;
                    if (this.Leader.Vwld.lengthSquared() < 25D) return;
                }
                if (this.wayCurPos == null) {
                    if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 1);
                    int k1 = 0;
                    WayPoint waypoint = this.AP.way.look_at_point(k1);
                    if (waypoint.waypointType == 4) k1 = 1;
                    this.Or.transform(this.Vwld);
                    Vcur.x = (float) this.Vwld.x;
                    Vcur.y = (float) this.Vwld.y;
                    this.taxiToTakeOffWay = new ArrayList();
                    Vpl.set(this.actor.collisionR() * 3F, 0.0D, 0.0D);
                    this.Or.transform(Vpl);
                    P.add(Vpl);
                    this.taxiToTakeOffWay.add(new Point_Any((float) P.x, (float) P.y));
                    WayPoint waypoint1 = null;
                    do {
                        WayPoint waypoint2 = this.AP.way.look_at_point(k1);
                        if (waypoint1 == waypoint2 || waypoint2.waypointType != 4 && waypoint2.waypointType != 5) break;
                        waypoint1 = waypoint2;
                        float f45 = 0.0F;
                        float f48 = 0.0F;
                        WayPoint waypoint4 = this.AP.way.look_at_point(k1 + 1);
                        if (waypoint4.waypointType == 4 || waypoint4.waypointType == 5) {
                            f45 = World.Rnd().nextFloat(-1F, 1.0F);
                            f48 = World.Rnd().nextFloat(-1F, 1.0F);
                        }
                        this.taxiToTakeOffWay.add(new Point_Any(waypoint2.x() + f45, waypoint2.y() + f48));
                        k1++;
                    } while (true);
                    this.taxiToTakeOffWayLength = this.taxiToTakeOffWay.size();
                    this.wayPrevPos = this.wayCurPos = (Point_Any) this.taxiToTakeOffWay.get(0);
                    P.x = this.Loc.x;
                    P.y = this.Loc.y;
                    P.z = this.Loc.z;
                    this.CT.setPowerControl(0.0F);
                    this.EI.setCurControlAll(true);
                    this.CT.BrakeControl = 0.0F;
                    this.setSpeedMode(8);
                }
                if (this.wayCurPos != null) {
                    Point_Any point_any = this.wayCurPos;
                    Pcur.set((float) P.x, (float) P.y);
                    this.distToTaxiPoint = Pcur.distance(point_any);
                    V_to.sub(point_any, Pcur);
                    V_to.normalize();
                    float f35 = 3F + 0.1F * (this.distToTaxiPoint - this.actor.collisionR() * 2.0F);
                    if (f35 > 7F && (this.Leader == null || !this.Leader.isCapableOfTaxiing())) f35 = 7F;
                    else if (this.Leader != null && f35 > 9F && this.Leader.isCapableOfTaxiing()) {
                        f35 = 9F;
                        Ve.sub(this.Leader.Loc, this.Loc);
                        float f37 = (float) Ve.length();
                        if (f37 < 80F) f35 -= -0.1F * f37 + 8F;
                    }
                    if (f35 < 2.0F) f35 = 2.0F;
                    boolean flag8 = false;
                    if (this.distToTaxiPoint < 5F + this.actor.collisionR() * 0.75F + this.Gears.mgx * 2.0F) {
                        f35 = 0.01F;
                        this.ignoredActor = null;
                        Point_Any point_any1 = this.getNextTaxiToTakeOffPoint();
                        if (point_any1 == null) {
                            if (this.taxiToTakeOffWayLength == this.AP.way.size()) {
                                this.maneuver = 0;
                                this.clear_stack();
                                this.set_maneuver(25);
                                this.TaxiMode = true;
                                this.wayPrevPos = this.wayCurPos = new Point_Null((float) this.Loc.x, (float) this.Loc.y);
                                this.airdromeWay = new Point_Any[1];
                                this.airdromeWay[0] = this.wayCurPos;
                                this.curAirdromePoi = 1;
                                return;
                            }
                            Point3d point3d1 = null;
                            WayPoint waypoint3 = this.AP.way.look_at_point(0);
                            if (waypoint3.waypointType == 4) point3d1 = this.AP.way.get(this.taxiToTakeOffWayLength).getP();
                            else point3d1 = this.AP.way.get(this.taxiToTakeOffWayLength - 1).getP();
                            point_any.set((float) point3d1.x, (float) point3d1.y);
                            V_to.sub(point_any, Pcur);
                            this.direction = 360F - FMMath.RAD2DEG(V_to.direction());
                            this.CT.setPowerControl(0.0F);
                            this.EI.setCurControlAll(true);
                            this.CT.BrakeControl = 1.0F;
                            this.setSpeedMode(8);
                            if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 0);
                            this.pop();
                            this.cleanCollisionMap(this.actor);
                            this.Wo = 0.0F;
                            this.W.set(0.0D, 0.0D, 0.0D);
                            return;
                        }
                        this.wayPrevPos = this.wayCurPos;
                        this.wayCurPos = point_any1;
                        this.V_taxiLeg.set(this.wayCurPos);
                        this.V_taxiLeg.sub(this.wayPrevPos);
                        if (this.V_taxiLeg.lengthSquared() > 0.1F) {
                            this.V_taxiLeg.normalize();
                            this.V_taxiLeg.scale(0.5F);
                        }
                    } else {
                        boolean flag9 = this.isTaxingCollisionDanger();
                        if (flag9) f35 = 0.01F;
                    }
                    V_to.sub(this.V_taxiLeg);
                    float f41 = FMMath.RAD2DEG((float) Math.atan2(V_to.y, V_to.x));
                    for (f41 += this.Or.azimut(); f41 > 180F; f41 -= 360F)
                        ;
                    for (; f41 < -180F; f41 += 360F)
                        ;
                    this.Wo += 2.0F * f41 * f / this.actor.collisionR();
                    if (f35 < 0.1D) this.Wo *= 0.95F;
                    f41 = (f41 > 20F ? 20F : f41) < -20F ? -20F : f41;
                    this.CT.RudderControl = clamp11(-f41 * 0.05F);
                    if (f41 > 0.0F) {
                        if (this.Wo > f41) {
                            this.CT.RudderControl = clamp11(f41 * 0.05F);
                            this.Wo = f41;
                        }
                    } else if (this.Wo < f41) {
                        this.CT.RudderControl = clamp11(f41 * 0.05F);
                        this.Wo = f41;
                    }
                    this.W.z = this.Wo * 0.01F;
                    // TODO: +++ TD AI code backport from 4.13 +++
                    // Use Reflection to avoid Orient class change
                    // Or.setRoll(0.0F);
                    Reflection.setFloat(this.Or, "Roll", 0.0F);
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (f35 > 2.0F && (f41 > 15F || f41 < -15F)) f35 = 2.0F;
                    this.Or.transformInv(this.Vwld);
                    if (this.Vwld.x < Math.abs(this.W.z * this.Gears.mgy)) this.Vwld.x = Math.abs(this.W.z * this.Gears.mgy);
                    this.Vwld.y = this.Gears.mgh * Math.cos(this.Gears.mgalpha + this.W.z) - this.Gears.mgy;
                    this.Vwld.x += 2.0F * f * (f35 > this.Vwld.x ? 1.0F : -1F);
                    this.Vwld.y += this.W.z * 0.5D * this.Vwld.x;
                    if (this.Vwld.x > 0.02D) this.Gears.setSteeringAngle((float) Math.toDegrees(Math.atan2(this.W.z * (this.Gears.sgx + this.Gears.mgx), this.Vwld.x)));
                    this.Or.transform(this.Vwld);
                    P.x += this.Vwld.x * f;
                    P.y += this.Vwld.y * f;
                    this.Loc.set(P);
                }
                break;

            // TODO: --- TD AI code backport from 4.13 ---

            case 26: {
                // TODO: +++ TD AI code backport from 4.13 +++
                Po.set(this.Loc);
                if (this.AP.way.first().delayTimer * 60000L > Time.current()) return;
                int l1 = ((Aircraft) this.actor).aircIndex();
                if (l1 == -1) l1 = 0;
                // TODO: --- TD AI code backport from 4.13 ---
                float f151 = this.Alt;
                float f18 = 0.4F;
                if (Actor.isAlive(this.AP.way.takeoffAirport) && this.AP.way.takeoffAirport instanceof AirportCarrier) {
                    // TODO: +++ TD AI code backport from 4.13 +++
                    AirportCarrier airportcarrier = (AirportCarrier) this.AP.way.takeoffAirport;
                    if (!(airportcarrier.ship() instanceof TestRunway)) {
                        // TODO: --- TD AI code backport from 4.13 ---
                        f151 -= ((AirportCarrier) this.AP.way.takeoffAirport).height();
                        f18 = 0.95F;
                        if (this.Alt < 9.0F && this.Vwld.z < 0.0) this.Vwld.z *= 0.85;
                        if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 1);
                        // TODO: +++ TD AI code backport from 4.13 +++
                    }
                    // TODO: --- TD AI code backport from 4.13 ---
                }
                if (this.first) {
                    this.setCheckGround(false);
                    this.CT.BrakeControl = 1.0F;
                    this.CT.GearControl = 1.0F;
                    this.CT.setPowerControl(0.0F);
                    if (this.CT.bHasArrestorControl) this.AS.setArrestor(this.actor, 0);
                    this.setSpeedMode(8);
                    if (f151 > 7.21F && this.AP.way.Cur() == 0) {
                        this.EI.setEngineRunning();
                        Aircraft.debugprintln(this.actor, "in the air - engines running!.");
                    }
                    if (!Actor.isAlive(this.AP.way.takeoffAirport)) this.direction = this.actor.pos.getAbsOrient().getAzimut();
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.AP.way.first().waypointType == 2 || this.AP.way.first().waypointType == 3 || /* ((Aircraft)actor).stationarySpawnLocSet && */ this.AP.way.first().waypointType != 5 && this.AP.way.first().waypointType != 4)
                        this.direction = this.actor.pos.getAbsOrient().getAzimut();
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (this.actor instanceof HE_LERCHE3) {
                        this.maneuver = 69;
                        break;
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.actor == World.getPlayerAircraft())
                        if (Aircraft.isPlayerTaxing() && this.EI.engines[0].getStage() == 6 && (this.AP.way.first().waypointType == 4 || this.AP.way.first().waypointType == 5) && this.lookAtNextTaxiToTakeOffPoint() != null) {
                            this.push();
                            this.push(TAXI_TO_TO);
                            this.submaneuver = 0;
                            this.maneuver = 0;
                            this.safe_pop();
                            this.AP.way.setNearestTaxingPoint(this.actor.pos.getAbsPoint(), this.actor.pos.getAbsOrient());
                            this.curAirdromePoi = this.AP.way.Cur() + 1;
                            break;
                        }
                }
                // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
                if(!triggerTakeOff)
                    break;
                // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
                if (this.Gears.onGround()) {
                    if (this.EI.engines[0].getStage() == 0) {
                        this.CT.setPowerControl(0.0F);
                        this.setSpeedMode(8);
                        // TODO: +++ TD AI code backport from 4.13 +++
                        float f34 = World.Rnd().nextFloat(7F + l1 * 0.25F, 10F);
                        if (World.Rnd().nextFloat() < 0.05F && this.mn_time > 1.0F || this.mn_time > f34) {
                            // TODO: --- TD AI code backport from 4.13 ---
                            this.push();
                            // TODO: +++ TD AI code backport from 4.13 +++
                            if (this.AP.way.first().waypointType == 4 || this.AP.way.first().waypointType == 5) this.push(TAXI_TO_TO);
                            // TODO: --- TD AI code backport from 4.13 ---
                            this.push(64);
                            this.submaneuver = SUB_MAN0;
                            this.maneuver = 0;
                            this.safe_pop();
                            break;
                        }
                    } else {
                        // TODO: CTO Mod
                        // -------------------------------
                        if (!this.bAlreadyCheckedStage7) {
                            if (this.AP.way.takeoffAirport instanceof AirportCarrier) {
                                AirportCarrier airportcarrier = (AirportCarrier) this.AP.way.takeoffAirport;
                                BigshipGeneric bigshipgeneric = airportcarrier.ship();
                                this.Gears.setCatapultOffset(bigshipgeneric);
                                this.bCatapultAI = this.Gears.getCatapultAI();
                            } else this.bStage7 = true;
                            this.bAlreadyCheckedStage7 = true;
                        }
                        if (!this.bCatapultAI) {
                            Po.set(this.Loc);
                            Vpl.set(60D, 0.0D, 0.0D);
                            this.fNearestDistance = 70F;
                            this.Or.transform(Vpl);
                            Po.add(Vpl);
                            Pd.set(Po);
                        } else {
                            Po.set(this.Loc);
                            Vpl.set(200D, 0.0D, 0.0D);
                            this.fNearestDistance = 210F;
                            this.Or.transform(Vpl);
                            Po.add(Vpl);
                            Pd.set(Po);
                        }
                        if (this.canTakeoff) {
                            if (!this.bStage7) {
                                if (this.bStage6) {
                                    if (this.bFastLaunchAI || !this.CT.bHasWingControl || this.CT.bHasWingControl && this.CT.getWing() < 0.5F) this.bStage7 = true;
                                } else if (this.bStage4) {
                                    if (this.CT.bHasWingControl && this.CT.getWing() > 0.001F) {
                                        if (this.bFastLaunchAI) this.CT.forceWing(0.0F);
                                        this.AS.setWingFold(this.actor, 0);
                                    }
                                    this.bStage6 = true;
                                } else if (this.bStage3) {
                                    Loc loc = new Loc();
                                    BigshipGeneric bigshipgeneric1 = (BigshipGeneric) this.brakeShoeLastCarrier;
                                    Aircraft aircraft = (Aircraft) this.actor;
                                    CellAirField cellairfield = bigshipgeneric1.getCellTO();
                                    double d3;
                                    double d6;
                                    if (this.bCatapultAI) {
                                        d3 = -cellairfield.leftUpperCorner().x - this.Gears.getCatapultOffsetX();
                                        d6 = cellairfield.leftUpperCorner().y - this.Gears.getCatapultOffsetY();
                                    } else {
                                        d3 = -cellairfield.leftUpperCorner().x - (cellairfield.getWidth() / 2 - 3);
                                        d6 = this.brakeShoeLoc.getX() + aircraft.getCellAirPlane().getHeight() + 4D;
                                    }
                                    loc.set(d6, d3, this.brakeShoeLoc.getZ(), this.brakeShoeLoc.getAzimut(), this.brakeShoeLoc.getTangage(), this.brakeShoeLoc.getKren());
                                    loc.add(this.brakeShoeLastCarrier.pos.getAbs());
                                    this.actor.pos.setAbs(loc.getPoint());
                                    this.brakeShoeLoc.set(this.actor.pos.getAbs());
                                    this.brakeShoeLoc.sub(this.brakeShoeLastCarrier.pos.getAbs());
                                    this.bStage4 = true;
                                } else {
                                    this.CT.PowerControl = 1.0F;
                                    this.bStage3 = true;
                                }
                            } else {
                                this.CT.PowerControl = 1.1F;
                                // TODO: +++ TD AI code backport from 4.13 +++
                                this.setSpeedMode(11);
                                // TODO: --- TD AI code backport from 4.13 ---
                            }
                        }
                        // -------------------------------
                        else {
                            this.setSpeedMode(8);
                            this.CT.BrakeControl = 1.0F;
                            boolean bool = true;
                            if (this.mn_time < 8.0F) bool = false;
                            // TODO: CTO Mod
                            // ---------------------------------------------------------
                            if (this.actor != War.getNearestFriendAtPoint(Pd, (Aircraft) this.actor, this.fNearestDistance)) if (this.actor instanceof TypeDockable) {
                                if (War.getNearestFriendAtPoint(Pd, (Aircraft) this.actor, this.fNearestDistance) != Reflection.invokeMethod(this.actor, "typeDockableGetDrone")) bool = false;
//                                    if (War.getNearestFriendAtPoint(Pd, (Aircraft) this.actor, this.fNearestDistance) != ((G4M2E) this.actor).typeDockableGetDrone())
//                                        bool = false;
                            } else bool = false;
                            // ---------------------------------------------------------
                            if (Actor.isAlive(this.AP.way.takeoffAirport) && this.AP.way.takeoffAirport.takeoffRequest > 0) bool = false;
                            if (bool) {
                                this.canTakeoff = true;
                                if (Actor.isAlive(this.AP.way.takeoffAirport)) this.AP.way.takeoffAirport.takeoffRequest = 300;
                            }
                        }
                    }
                    // TODO: Altered by CTO Mod
                    if (this.EI.engines[0].getStage() == 6 && this.CT.bHasWingControl && this.CT.getWing() > 0.001F && !(this.AP.way.takeoffAirport instanceof AirportCarrier)) this.AS.setWingFold(this.actor, 0);
                } else if (this.canTakeoff) {
                    this.CT.setPowerControl(1.1F);
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.setSpeedMode(11);
                    // TODO: --- TD AI code backport from 4.13 ---
                }
                if (this.CT.FlapsControl == 0.0F && this.CT.getWing() < 0.0010F) this.CT.FlapsControl = 0.4F;
                if (this.EI.engines[0].getStage() == 6 && this.CT.getPower() > f18) {
                    this.CT.BrakeControl = 0.0F;
                    this.brakeShoe = false;
                    // TODO: +++ TD AI code backport from 4.13 +++
                    float f22 = (float) ((this.Vmin + this.VminFLAPS) * 0.5F * Math.sqrt(this.M.getFullMass() / this.M.referenceWeight));
                    float f24 = (this.AOA_Crit - 2.0F) * (this.getSpeed() / f22);
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (this.Gears.bIsSail) f24 *= 2.0F;
                    
                    //TODO: EngineMod 2.8 Backport +++
                    //if (this.Gears.bFrontWheel) f24 = this.Gears.Pitch + 4.0F;
                    //if (f24 < 1.0F) f24 = 1.0F;
                    //if (f24 > 10.0F) f24 = 10.0F;
                    if (this.Gears.bFrontWheel) {
                        if(this.CT.targetDegreeAITakeoffRotation > 0.0F)
                            f24 = this.CT.targetDegreeAITakeoffRotation;
                        else
                            f24 = Math.min(this.Gears.Pitch, 8F) + 4F;
                        if(Alt > f151 + 6F || (float)Loc.z > f18 + 6F || CT.getGearL() == 0.0F && CT.getGearR() == 0.0F && CT.getGearC() == 0.0F) {
                            if (CT.targetDegreeAITakeoffClimb > 0.0F)
                                f24 = this.CT.targetDegreeAITakeoffClimb;
                        }
                    }
                    if (f24 < 1.0F) f24 = 1.0F;
                    if (f24 > AOA_Crit - 2.0F) f24 = AOA_Crit - 2.0F;
                    if (f24 > 10.0F) f24 = 10.0F;
                    //TODO: EngineMod 2.8 Backport ---
                    
                    float f27 = 1.5F;
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (Actor.isAlive(this.AP.way.takeoffAirport) && this.AP.way.takeoffAirport instanceof AirportCarrier) {
                        AirportCarrier airportcarrier1 = (AirportCarrier) this.AP.way.takeoffAirport;
                        if (!(airportcarrier1.ship() instanceof TestRunway) && !this.Gears.isUnderDeck()) {
                            // TODO: --- TD AI code backport from 4.13 ---
                            this.CT.GearControl = 0.0F;
                            if (f151 < 0.0F) {
                                f24 = 18.0F;
                                f27 = 0.05F;
                            } else {
                                f24 = 14.0F;
                                f27 = 0.3F;
                            }
                            // TODO: +++ TD AI code backport from 4.13 +++
                        }
                        // TODO: --- TD AI code backport from 4.13 ---
                    }
                    if (this.Or.getTangage() < f24) this.dA = -0.7F * (this.Or.getTangage() - f24) + f27 * (float) this.getW().y + 0.5F * (float) this.getAW().y;
                    else this.dA = -0.1F * (this.Or.getTangage() - f24) + f27 * (float) this.getW().y + 0.5F * (float) this.getAW().y;
                    this.CT.ElevatorControl += (this.dA - this.CT.ElevatorControl) * 3.0F * f;
                } else this.CT.ElevatorControl = 1.0F;
                // TODO: +++ TD AI code backport from 4.13 +++
                boolean flag11 = true;
                if (this.AP.way.first().waypointType == 2 || this.AP.way.first().waypointType == 3 || this.AP.way.first().waypointType == 5 || this.AP.way.first().waypointType == 4/* || ((Aircraft)actor).stationarySpawnLocSet */) flag11 = false;
                // TODO: --- TD AI code backport from 4.13 ---
                this.AFo.setDeg(this.Or.getAzimut(), this.direction);
                double d1 = this.AFo.getDiffRad();
                if (this.EI.engines[0].getStage() == 6) {
                    this.CT.RudderControl = clamp11(8.0F * (float) d1);
                    if (d1 > -1.0 && d1 < 1.0) {
                        // TODO: +++ TD AI code backport from 4.13 +++
                        if (flag11 && Actor.isAlive(this.AP.way.takeoffAirport) && this.CT.getPower() > 0.3F) {
                            // TODO: --- TD AI code backport from 4.13 ---
                            double d2 = this.AP.way.takeoffAirport.ShiftFromLine(this);
                            double d3 = 3.0 - 3.0 * Math.abs(d1);
                            if (d3 > 1.0) d3 = 1.0;
                            double d5 = 0.25 * d2 * d3;
                            if (d5 > 1.5) d5 = 1.5;
                            if (d5 < -1.5) d5 = -1.5;
                            this.CT.RudderControl += (float) d5;
                        }
                    } else this.CT.BrakeControl = 1.0F;
                }
                this.CT.AileronControl = clamp11(-0.05F * this.Or.getKren() + 0.3F * (float) this.getW().y);
                if (f151 > 5.0F && !this.Gears.isUnderDeck()) this.CT.GearControl = 0.0F;
                float f28 = 1.0F;
                if (this.hasBombs()) f28 *= 1.7F;
                if (f151 > 120.0F * f28 || this.getSpeed() > this.Vmin * 1.8F * f28 || f151 > 80.0F * f28 && this.getSpeed() > this.Vmin * 1.6F * f28
                        || f151 > 40.0F * f28 && this.getSpeed() > this.Vmin * 1.3F * f28 && this.mn_time > 60.0F + ((Aircraft) this.actor).aircIndex() * 3.0F) {
                    this.CT.FlapsControl = 0.0F;
                    this.CT.GearControl = 0.0F;
                    this.rwLoc = null;
                    if (this.actor instanceof TypeGlider) this.push(24);
                    this.maneuver = 0;
                    this.brakeShoe = false;
                    this.turnOffCollisions = false;
                    if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 0);
                    this.pop();
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.Group != null && this.Group.numInGroup((Aircraft) this.actor) == 0 && this.Group.grTask == 7) this.Group.setGroupTask(1);
                    if (this.AP.way.first().waypointType == 4 || this.AP.way.prev().waypointType == 5) {
                        this.AP.way.setCur(this.curAirdromePoi);
                        this.curAirdromePoi = 0;
                        this.wayCurPos = null;
                    }
                    // TODO: --- TD AI code backport from 4.13 ---
                }
                if (this.actor instanceof TypeGlider) {
                    this.CT.BrakeControl = 0.0F;
                    this.CT.ElevatorControl = 0.05F;
                    this.canTakeoff = true;
                }
                break;
            }
            case 69: {
                float f161 = this.Alt;
                float f19 = 0.4F;
                this.CT.BrakeControl = 1.0F;
                this.W.scale(0.0);
                boolean flag4 = Actor.isAlive(this.AP.way.takeoffAirport) && this.AP.way.takeoffAirport instanceof AirportCarrier;
                if (flag4) {
                    f161 -= ((AirportCarrier) this.AP.way.takeoffAirport).height();
                    f19 = 0.8F;
                    if (this.Alt < 9.0F && this.Vwld.z < 0.0) this.Vwld.z *= 0.85;
                    if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 1);
                }
                if (this.Loc.z != 0.0 && ((HE_LERCHE3) this.actor).suka.getPoint().z == 0.0) ((HE_LERCHE3) this.actor).suka.set(this.Loc, this.Or);
                if (this.Loc.z != 0.0) if (this.EI.getPowerOutput() < f19 && !flag4) {
                    this.callSuperUpdate = false;
                    this.Loc.set(((HE_LERCHE3) this.actor).suka.getPoint());
                    this.Or.set(((HE_LERCHE3) this.actor).suka.getOrient());
                } else if (f161 < 100.0F) this.Or.set(((HE_LERCHE3) this.actor).suka.getOrient());
                if (this.Gears.onGround() && this.EI.engines[0].getStage() == 0) {
                    this.CT.setPowerControl(0.0F);
                    this.setSpeedMode(8);
                    if (World.Rnd().nextFloat() < 0.05F && this.mn_time > 1.0F || this.mn_time > 8.0F) {
                        this.push();
                        this.push(64);
                        this.submaneuver = SUB_MAN0;
                        this.maneuver = 0;
                        this.safe_pop();
                        break;
                    }
                }
                if (this.EI.engines[0].getStage() == 6) {
                    this.CT.BrakeControl = 0.0F;
                    this.CT.AirBrakeControl = 1.0F;
                    this.brakeShoe = false;
                    this.CT.setPowerControl(1.1F);
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.setSpeedMode(11);
                    // TODO: --- TD AI code backport from 4.13 ---
                }
                if (f161 > 200.0F) {
                    this.CT.GearControl = 0.0F;
                    this.rwLoc = null;
                    this.CT.ElevatorControl = -1.0F;
                    this.CT.AirBrakeControl = 0.0F;
                    if (this.Or.getTangage() < 25.0F) this.maneuver = 0;
                    this.brakeShoe = false;
                    this.turnOffCollisions = false;
                    if (this.CT.bHasCockpitDoorControl) this.AS.setCockpitDoor(this.actor, 0);
                    this.pop();
                }
                break;
            }
            case 28:
                if (this.first) {
                    this.direction = World.Rnd().nextFloat(-25.0F, 25.0F);
                    this.AP.setStabAll(false);
                    this.setSpeedMode(6);
                    this.CT.RudderControl = World.Rnd().nextFloat(-0.22F, 0.22F);
                    this.submaneuver = SUB_MAN0;
                    if (this.getSpeed() < this.Vmin * 1.5F) this.pop();
                }
                this.CT.AileronControl = clamp11(-0.04F * (this.Or.getKren() - this.direction));
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.dA = 1.0F;
                        this.maxAOA = 12.0F + 1.0F * this.Skill;
                        if (this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.6F * f;
                        else this.CT.ElevatorControl += 3.3F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() > 40.0F + 5.1F * this.Skill) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.direction = World.Rnd().nextFloat(-25.0F, 25.0F);
                        this.CT.RudderControl = World.Rnd().nextFloat(-0.75F, 0.75F);
                        this.submaneuver++;
                        /* fall through */
                    case SUB_MAN2:
                        this.dA = -1.0F;
                        this.maxAOA = 12.0F + 5.0F * this.Skill;
                        if (this.AOA < -this.maxAOA || this.CT.ElevatorControl < this.dA) this.CT.ElevatorControl += 0.6F * f;
                        else this.CT.ElevatorControl -= 3.3F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < -45.0F) this.pop();
                        break;
                }
                if (this.mn_time > 3.0F) this.pop();
                break;
            case 29:
                this.minElevCoeff = 17.0F;
                if (this.first) {
                    this.AP.setStabAll(false);
                    this.setSpeedMode(9);
                    this.sub_Man_Count = 0;
                }
                if (this.danger != null) {
                    if (this.sub_Man_Count == 0) {
                        tmpV3d.set(this.danger.getW());
                        this.danger.Or.transform(tmpV3d);
                        if (tmpV3d.z > 0.0) this.sinKren = -World.Rnd().nextFloat(60.0F, 90.0F);
                        else this.sinKren = World.Rnd().nextFloat(60.0F, 90.0F);
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    float f25 = (this.Energy - this.danger.Energy) * 0.1019F;
                    if (this.Loc.distanceSquared(this.danger.Loc) < 5000D || f25 < -200F) {
                        // TODO: --- TD AI code backport from 4.13 ---
                        this.setSpeedMode(8);
                        this.CT.setPowerControl(0.0F);
                    } else this.setSpeedMode(9);
                } else this.pop();
                this.CT.FlapsControl = 0.2F;
                if (this.getSpeed() < 120.0) this.CT.FlapsControl = 0.33F;
                if (this.getSpeed() < 80.0) this.CT.FlapsControl = 1.0F;
                this.CT.AileronControl = clamp11(-0.08F * (this.Or.getKren() + this.sinKren));
                this.CT.ElevatorControl = 0.9F;
                this.CT.RudderControl = 0.0F;
                // TODO: +++ TD AI code backport from 4.13 +++
                this.nShakeMe(this.flying, this.Skill);
                // TODO: --- TD AI code backport from 4.13 ---
                this.sub_Man_Count++;
                if (this.sub_Man_Count > 15) this.sub_Man_Count = 0;
                if (this.mn_time > 10.0F) this.pop();
                break;
            case 56:
                if (this.first) {
                    this.submaneuver = World.Rnd().nextInt(SUB_MAN0, SUB_MAN1);
                    this.direction = World.Rnd().nextFloat(-20.0F, -10.0F);
                }
                this.CT.AileronControl = clamp11(-0.08F * (this.Or.getKren() - this.direction));
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.dA = 1.0F;
                        this.maxAOA = 10.0F + 2.0F * this.Skill;
                        if (this.getOverload() > 1.0 / Math.abs(Math.cos(Math.toRadians(this.Or.getKren()))) || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        this.CT.RudderControl = -1.0F * (float) Math.sin(Math.toRadians(this.Or.getKren() + 55.0F));
                        if (this.mn_time > 1.5F) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.direction = World.Rnd().nextFloat(10.0F, 20.0F);
                        this.submaneuver++;
                        /* fall through */
                    case SUB_MAN2:
                        this.dA = 1.0F;
                        this.maxAOA = 10.0F + 2.0F * this.Skill;
                        if (this.getOverload() > 1.0 / Math.abs(Math.cos(Math.toRadians(this.Or.getKren()))) || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        this.CT.RudderControl = 1.0F * (float) Math.sin(Math.toRadians(this.Or.getKren() - 55.0F));
                        if (this.mn_time > 4.5F) this.submaneuver++;
                        break;
                    case SUB_MAN3:
                        this.direction = World.Rnd().nextFloat(-20.0F, -10.0F);
                        this.submaneuver++;
                        /* fall through */
                    case SUB_MAN4:
                        this.dA = 1.0F;
                        this.maxAOA = 10.0F + 2.0F * this.Skill;
                        if (this.getOverload() > 1.0 / Math.abs(Math.cos(Math.toRadians(this.Or.getKren()))) || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        this.CT.RudderControl = -1.0F * (float) Math.sin(Math.toRadians(this.Or.getKren() + 55.0F));
                        if (!(this.mn_time > 6.0F)) break;
                        this.pop();
                }
                break;
            case 30:
                this.push(14);
                this.push(18);
                this.pop();
                break;
            case 31:
                this.push(14);
                this.push(19);
                this.pop();
                break;
            case 32:
                if (!this.isCapableOfACM() && World.Rnd().nextInt(-2, 9) < this.Skill) ((Aircraft) this.actor).hitDaSilk();
                if (this.first) {
                    this.AP.setStabAll(false);
                    this.setSpeedMode(6);
                    this.submaneuver = SUB_MAN0;
                    this.direction = 178.0F - World.Rnd().nextFloat(0.0F, 8.0F) * this.Skill;
                }
                this.maxAOA = this.Vwld.z <= 0.0 ? 24.0F : 14.0F;
                if (this.danger != null) {
                    Ve.sub(this.danger.Loc, this.Loc);
                    this.dist = (float) Ve.length();
                    this.Or.transformInv(Ve);
                    Vpl.set(this.danger.Vwld);
                    this.Or.transformInv(Vpl);
                    Vpl.normalize();
                }
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.dA = this.Or.getKren() - 180.0F;
                        if (this.dA < -180.0F) this.dA += 360.0F;
                        this.dA *= -0.08F;
                        this.CT.AileronControl = clamp11(this.dA);
                        this.CT.RudderControl = this.dA <= 0.0F ? -1.0F : 1.0F;
                        this.CT.ElevatorControl = clamp11(0.01111111F * Math.abs(this.Or.getKren()));
                        if (this.mn_time > 2.0F || Math.abs(this.Or.getKren()) > this.direction) {
                            this.submaneuver++;
                            this.CT.RudderControl = World.Rnd().nextFloat(-0.5F, 0.5F);
                            this.direction = World.Rnd().nextFloat(-60.0F, -30.0F);
                            this.mn_time = 0.5F;
                        }
                        break;
                    case SUB_MAN1:
                        this.dA = this.Or.getKren() - 180.0F;
                        if (this.dA < -180.0F) this.dA += 360.0F;
                        this.dA *= -0.04F;
                        this.CT.AileronControl = clamp11(this.dA);
                        if (this.Or.getTangage() > this.direction + 5.0F && this.getOverload() < this.maxG && this.AOA < this.maxAOA) {
                            if (this.CT.ElevatorControl < 0.0F) this.CT.ElevatorControl = 0.0F;
                            this.CT.ElevatorControl += 1.0F * f;
                        } else {
                            if (this.CT.ElevatorControl > 0.0F) this.CT.ElevatorControl = 0.0F;
                            this.CT.ElevatorControl -= 1.0F * f;
                            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        }
                        if (this.mn_time > 2.0F && this.Or.getTangage() < this.direction + 20.0F) this.submaneuver++;
                        if (this.danger != null) {
                            if (this.Skill >= 2 && this.Or.getTangage() < -30.0F && Vpl.x > 0.999D) this.submaneuver++;
                            if (this.Skill >= 3 && Math.abs(this.Or.getTangage()) > 145.0F && Math.abs(this.danger.Or.getTangage()) > 135.0F && this.dist < 400.0F) this.submaneuver++;
                        }
                        break;
                    case SUB_MAN2:
                        this.direction = World.Rnd().nextFloat(-60.0F, 60.0F);
                        this.setSpeedMode(6);
                        this.submaneuver++;
                        // fall through
                    case SUB_MAN3:
                        this.dA = this.Or.getKren() - this.direction;
                        this.CT.AileronControl = clamp11(-0.04F * this.dA);
                        this.CT.RudderControl = this.dA <= 0.0F ? -1.0F : 1.0F;
                        this.CT.ElevatorControl = 0.5F;
                        if (Math.abs(this.dA) < 4.0F + 3.0F * this.Skill) this.submaneuver++;
                        break;
                    case SUB_MAN4:
                        this.direction *= World.Rnd().nextFloat(0.5F, 1.0F);
                        this.setSpeedMode(6);
                        this.submaneuver++;
                        // fall through
                    case SUB_MAN5:
                        this.dA = this.Or.getKren() - this.direction;
                        this.CT.AileronControl = clamp11(-0.04F * this.dA);
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA || this.Or.getTangage() > 40.0F) this.CT.ElevatorControl -= 0.8F * f;
                        else this.CT.ElevatorControl += 1.6F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() > 80.0F || this.mn_time > 4.0F || this.getSpeed() < this.Vmin * 1.5F) this.pop();
                        if (this.danger != null && (Ve.z < -1.0 || this.dist > 600.0F || Vpl.x < 0.788)) this.pop();
                        break;
                }
                if (this.Alt < -7.0 * this.Vwld.z) this.pop();
                break;
            case 33:
                if (this.first) {
                    if (this.Alt < 1000.0F) {
                        this.pop();
                        break;
                    }
                    this.AP.setStabAll(false);
                    this.submaneuver = SUB_MAN0;
                    this.direction = (World.Rnd().nextBoolean() ? 1.0F : -1.0F) * World.Rnd().nextFloat(107.0F, 160.0F);
                }
                this.maxAOA = this.Vwld.z <= 0.0 ? 24.0F : 14.0F;
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        if (Math.abs(this.Or.getKren()) < 45.0F) this.CT.ElevatorControl = clamp11(0.005555556F * Math.abs(this.Or.getKren()));
                        else if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > 1.0F) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 1.2F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        this.dA = this.Or.getKren() - this.direction;
                        this.CT.AileronControl = clamp11(-0.04F * this.dA);
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        if (Math.abs(this.dA) < 4.0F + 1.0F * this.Skill) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.setSpeedMode(7);
                        this.smConstPower = 0.5F;
                        this.CT.AileronControl = 0.0F;
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.2F * f;
                        else this.CT.ElevatorControl += 1.2F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() < -60.0F) this.submaneuver++;
                        break;
                    case SUB_MAN2:
                        if (this.Or.getTangage() > -45.0F) {
                            this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                            this.setSpeedMode(9);
                            this.maxAOA = 7.0F;
                        }
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.8F * f;
                        else this.CT.ElevatorControl += 0.4F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() > this.AOA - 10.0F || this.Vwld.z > -1.0) this.pop();
                        break;
                }
                if (this.Alt < -7.0 * this.Vwld.z) this.pop();
                break;
            case 34:
                if (this.first) {
                    if (this.Alt < 500.0F) {
                        this.pop();
                        break;
                    }
                    this.direction = this.Or.getTangage();
                    this.setSpeedMode(9);
                }
                this.dA = this.Or.getKren() - (this.Or.getKren() <= 0.0F ? -35.0F : 35.0F);
                this.CT.AileronControl = clamp11(-0.04F * this.dA);
                this.CT.RudderControl = this.Or.getKren() <= 0.0F ? -1.0F : 1.0F;
                this.CT.ElevatorControl = -1.0F;
                if (this.direction > this.Or.getTangage() + 45.0F || this.Or.getTangage() < -60.0F || this.mn_time > 4.0F) this.pop();
                break;
            case 36:
            case 37:
                if (this.first) {
                    if (!this.isCapableOfACM()) this.pop();
                    if (this.getSpeed() < this.Vmax * 0.5F) {
                        this.pop();
                        break;
                    }
                    this.AP.setStabAll(false);
                    this.submaneuver = SUB_MAN0;
                    this.direction = World.Rnd().nextFloat(-30.0F, 80.0F);
                    this.setSpeedMode(9);
                }
                this.maxAOA = this.Vwld.z <= 0.0 ? 24.0F : 14.0F;
                switch (this.submaneuver) {
                    case SUB_MAN0:
                        this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                        this.CT.RudderControl = clamp11(-0.1F * this.getAOS());
                        if (Math.abs(this.Or.getKren()) < 45.0F) this.submaneuver++;
                        break;
                    case SUB_MAN1:
                        this.CT.AileronControl = 0.0F;
                        this.dA = 1.0F;
                        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > this.dA) this.CT.ElevatorControl -= 0.4F * f;
                        else this.CT.ElevatorControl += 0.8F * f;
                        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                        if (this.Or.getTangage() > this.direction) this.submaneuver++;
                        if (this.getSpeed() < this.Vmin * 1.25F) this.pop();
                        break;
                    case SUB_MAN2:
                        this.push(this.maneuver != 36 ? 35 : 7);
                        this.pop();
                }
                break;
            case 38:
                if (this.first) this.CT.RudderControl = this.Or.getKren() <= 0.0F ? -1.0F : 1.0F;
                this.CT.AileronControl += -0.02F * this.Or.getKren();
                if (this.CT.AileronControl > 0.1F) this.CT.AileronControl = 0.1F;
                if (this.CT.AileronControl < -0.1F) this.CT.AileronControl = -0.1F;
                this.dA = (this.getSpeedKMH() - 180.0F - this.Or.getTangage() * 10.0F - this.getVertSpeed() * 5.0F) * 0.0040F;
                this.CT.ElevatorControl = clamp11(this.dA);
                if (this.mn_time > 3.5F) this.pop();
                break;
            case 39:
                this.setSpeedMode(6);
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() + 10.0F));
                if (this.CT.RudderControl > 0.1F) this.CT.RudderControl = 0.8F;
                else if (this.CT.RudderControl < -0.1F) this.CT.RudderControl = -0.8F;
                else this.CT.RudderControl = this.Or.getKren() <= 0.0F ? -1.0F : 1.0F;
                if (this.getSpeed() > this.Vmax || this.mn_time > 7.0F) this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case 89:
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                if (this.Alt > 50F) {
                    this.dA = (this.getSpeedKMH() - 180F - this.Or.getTangage() * 10F - this.getVertSpeed() * 5F) * 0.004F;
                    this.CT.ElevatorControl = clamp11(this.dA);
                }
                this.CT.RudderControl += 0.1F;
                if (this.CT.RudderControl > 0.9F) this.CT.RudderControl = 0.9F;
                if (this.mn_time > 0.8F) this.pop();
                if (this.Leader != null) this.Vwld.z = this.Leader.Vwld.z;
                break;

            case 88:
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                if (this.Alt > 50F) {
                    this.dA = (this.getSpeedKMH() - 180F - this.Or.getTangage() * 10F - this.getVertSpeed() * 5F) * 0.004F;
                    this.CT.ElevatorControl = clamp11(this.dA);
                }
                this.CT.RudderControl -= 0.1F;
                this.CT.RudderControl = clamp11(this.CT.RudderControl);
                if (this.CT.RudderControl < -0.9F) this.CT.RudderControl = -0.9F;
                if (this.mn_time > 0.8F) this.pop();
                if (this.Leader != null) this.Vwld.z = this.Leader.Vwld.z;
                break;

            case 90:
                this.setSpeedMode(6);
                this.CT.RudderControl = 1.0F;
                if (Math.abs(this.Or.getRoll() - 360F) > 90F) this.CT.AileronControl = 0.0F;
                else if (Math.abs(this.Or.getRoll() - 360F) > 60F) this.CT.AileronControl *= 0.8F;
                else {
                    this.CT.AileronControl -= 0.03F;
                    this.CT.AileronControl = clamp11(this.CT.AileronControl);
                    //if (this.CT.AileronControl < -1F) this.CT.AileronControl = -1F;
                }
                this.CT.ElevatorControl = clamp11(Math.abs(this.Or.getRoll() - 360F) * -0.002F);
                if (this.mn_time > 0.8F) this.pop();
                if (this.Leader != null) {
                    this.Vwld.z = this.Leader.Vwld.z;
                    this.Vwld.y = this.Leader.Vwld.y;
                }
                break;

            case 91:
                this.setSpeedMode(6);
                this.CT.RudderControl = -1F;
                if (Math.abs(this.Or.getRoll() - 360F) > 90F) this.CT.AileronControl = 0.0F;
                else if (Math.abs(this.Or.getRoll() - 360F) > 60F) this.CT.AileronControl *= 0.8F;
                else {
                    this.CT.AileronControl += 0.03F;
                    if (this.CT.AileronControl > 1.0F) this.CT.AileronControl = 1.0F;
                }
                this.CT.ElevatorControl = clamp11(Math.abs(this.Or.getRoll() - 360F) * -0.002F);
                if (this.mn_time > 0.8F) this.pop();
                if (this.Leader != null) {
                    this.Vwld.z = this.Leader.Vwld.z;
                    this.Vwld.y = this.Leader.Vwld.y;
                }
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case 40:
                this.push(39);
                this.push(57);
                this.pop();
                break;
            case 41:
                this.push(13);
                this.push(18);
                this.pop();
                break;
            case 42:
                this.push(19);
                this.push(57);
                this.pop();
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case BREAK_AWAY:
                this.setSpeedMode(6);
                if (this.getSpeed() < this.Vmax * 0.7F || this.Or.getPitch() > 360F) this.setSpeedMode(11);
                else this.setSpeedMode(8);
                this.minElevCoeff = 20F;
                this.dA = Math.abs(this.Or.getKren()) * this.Group.bracketSideGr;
                if (this.dA > 0.0F) this.dA -= 30F;
                else this.dA -= 330F;
                if (this.dA < -180F) this.dA += 360F;
                if (Math.abs(this.dA) > 60F) this.set_maneuver(2);
                this.dA = 0.03F * this.dA;
                this.CT.AileronControl = clamp11(this.dA);
                this.CT.ElevatorControl = 1.0F;
                if (this.mn_time > 1.5F + World.Rnd().nextFloat(0.0F, 2.0F)) this.pop();
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            case GATTACK_KAMIKAZE:
                if (this.target_ground == null || this.target_ground.pos == null) {
                    if (this.Group != null) {
                        this.dont_change_subm = true;
                        boolean flag5 = this.isBusy();
                        int j11 = this.Group.grTask;
                        this.Group.grTask = 4;
                        this.setBusy(false);
                        this.Group.setTaskAndManeuver(this.Group.numInGroup((Aircraft) this.actor));
                        this.setBusy(flag5);
                        this.Group.grTask = j11;
                    }
                    if (this.target_ground == null || this.target_ground.pos == null) {
                        this.AP.way.first();
                        Airport airport = Airport.nearest(this.AP.way.curr().getP(), this.actor.getArmy(), 7);
                        WayPoint waypoint;
                        if (airport != null) waypoint = new WayPoint(airport.pos.getAbsPoint());
                        else waypoint = new WayPoint(this.AP.way.first().getP());
                        waypoint.set(0.6F * this.Vmax);
                        waypoint.Action = 2;
                        this.AP.way.add(waypoint);
                        this.AP.way.last();
                        this.set_task(3);
                        this.clear_stack();
                        this.maneuver = 21;
                        this.set_maneuver(21);
                        break;
                    }
                }
                this.groundAttackKamikaze(this.target_ground, f);
                break;
            case GATTACK:
            case GATTACK_TINYTIM:
            case GATTACK_DIVE:
            case GATTACK_TORPEDO:
            case GATTACK_CASSETTE:
            case GATTACK_HS293:
            case GATTACK_FRITZX:
            case GATTACK_TORPEDO_TOKG:
                if (this.first && !this.isCapableOfACM()) {
                    this.bombsOut = true;
                    this.setReadyToReturn(true);
                    if (this.Group != null) this.Group.waitGroup(this.Group.numInGroup((Aircraft) this.actor));
                    else {
                        this.AP.way.next();
                        this.set_task(3);
                        this.clear_stack();
                        this.set_maneuver(21);
                    }
                } else {
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (this.CT.dropWithPlayer != null && this.CT.dropWithPlayer.isAlive() && this.hasBombs() && this.maneuver == 43) {
                        this.set_task(2);
                        this.clear_stack();
                        this.set_maneuver(24);
                    }
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (this.target_ground == null || this.target_ground.pos == null || !Actor.isAlive(this.target_ground)) {
                        int i1 = this.maneuver;
                        if (this.Group != null) if (this.Group.grTask == 4) {
                            this.dont_change_subm = true;
                            boolean flag6 = this.isBusy();
                            this.setBusy(false);
                            this.Group.setTaskAndManeuver(this.Group.numInGroup((Aircraft) this.actor));
                            this.setBusy(flag6);
                        }
                        if (this.target_ground == null || this.target_ground.pos == null || !Actor.isAlive(this.target_ground)) {
                            if (i1 == 50) this.bombsOut = true;
                            if (this.Group != null) this.Group.waitGroup(this.Group.numInGroup((Aircraft) this.actor));
                            else {
                                this.AP.way.next();
                                // TODO: GATTACK set_task(3);
                                // TODO: GATTACK clear_stack();
                                this.set_maneuver(21);
                            }
                            this.push(2);
                            this.pop();
                            break;
                        }
                    }
                    // TODO: +++ Skip Ground Attack Waypoints when there's nothing to attack with - by SAS~Storebror +++
                    if (!this.hasBombs() && !this.hasRockets() && !this.hasGunsOrCannons()) {
                        this.bombsOut = true;
                        this.setReadyToReturn(true);
                        if (this.Group != null) {
//                            System.out.println("Skipped Waypoint No. " + this.AP.way.Cur() + " (waiting for Group " + this.Group.hashCode() + ") since it's a GATTACK waypoint and this plane (" + this.actor.name()
//                                    + ") has neither bombs nor rockets nor Gun/Cannon Ammo left");
                            this.Group.waitGroup(this.Group.numInGroup((Aircraft) this.actor));
                        } else {
//                            System.out.println("Skipped Waypoint No. " + this.AP.way.Cur() + " since it's a GATTACK waypoint and this plane (" + this.actor.name() + ") has neither bombs nor rockets nor Gun/Cannon Ammo left");
                            this.AP.way.next();
                            this.set_task(3);
                            this.clear_stack();
                            this.set_maneuver(21);
                        }
                        break;
                    } else // TODO: --- Skip Ground Attack Waypoints when there's nothing to attack with - by SAS~Storebror ---
                        switch (this.maneuver) {
                        default:
                        break;
                        case GATTACK:
                        this.groundAttack(this.target_ground, f);
                        if (this.mn_time > 120.0F) this.set_maneuver(0);
                        break;
                        case GATTACK_DIVE:
                        this.groundAttackDiveBomber(this.target_ground, f);
                        if (this.mn_time > 120.0F) {
                            this.setSpeedMode(6);
                            this.CT.BayDoorControl = 0.0F;
                            this.CT.AirBrakeControl = 0.0F;
                            this.CT.FlapsControl = 0.0F;
                            this.pop();
                            this.sub_Man_Count = 0;
                        }
                        break;
                        case GATTACK_TORPEDO:
                        this.groundAttackTorpedo(this.target_ground, f);
                        break;
                        case GATTACK_TORPEDO_TOKG:
                        this.groundAttackTorpedoToKG(this.target_ground, f);
                        break;
                        case GATTACK_CASSETTE:
                        this.groundAttackCassette(this.target_ground, f);
                        break;
                        case GATTACK_KAMIKAZE:
                        this.groundAttackKamikaze(this.target_ground, f);
                        break;
                        case GATTACK_TINYTIM:
                        this.groundAttackTinyTim(this.target_ground, f);
                        break;
                        case GATTACK_HS293:
                        this.groundAttackHS293(this.target_ground, f);
                        break;
                        case GATTACK_FRITZX:
                        this.groundAttackFritzX(this.target_ground, f);
                        }
                }
        }
        // TODO: +++ Added by SAS~Storebror: Keep Kamikaze Aircraft from pulling up on terminal dive
        if (!this.kamikaze && this.checkGround) this.doCheckGround(f);
        // if (this.checkGround) this.doCheckGround(f);
        // ---
        if (this.checkStrike && this.strikeEmer) this.doCheckStrike();
        this.strikeEmer = false;
        this.setSpeedControl(f);
        // TODO: +++ TD AI code backport from 4.13 +++
        this.CT.AileronControl = this.clampA(this.CT.AileronControl, this.CT.Sensitivity);
        this.CT.ElevatorControl = this.clampA(this.CT.ElevatorControl, this.CT.Sensitivity);
        this.CT.RudderControl = this.clampA(this.CT.RudderControl, this.CT.Sensitivity);
        // TODO: --- TD AI code backport from 4.13 ---
        this.first = false;
        this.mn_time += f;
        if (this.frequentControl) this.AP.update(f);
        else this.AP.update(f * 4.0F);
        // TODO: +++ TD AI code backport from 4.13 +++
        this.wasBusy = this.bBusy;
        // TODO: --- TD AI code backport from 4.13 ---
        if (this.shotAtFriend > 0) this.shotAtFriend--;
    }

    void OutCT(int i) {
        if (this.actor == Main3D.cur3D().viewActor()) {
            TextScr.output(i + 5, 45, "Alt(MSL)  " + (int) this.Loc.z + "    " + (this.CT.BrakeControl <= 0.0F ? "" : "BRAKE"));
            TextScr.output(i + 5, 65, "Alt(AGL)  " + (int) (this.Loc.z - Engine.land().HQ_Air(this.Loc.x, this.Loc.y)));
            int j = 0;
            TextScr.output(i + 225, 140, "---ENGINES (" + this.EI.getNum() + ")---" + this.EI.engines[j].getStage());
            TextScr.output(i + 245, 120, "THTL " + (int) (100.0F * this.EI.engines[j].getControlThrottle()) + "%" + (this.EI.engines[j].getControlAfterburner() ? " (NITROS)" : ""));
            TextScr.output(i + 245, 100, "PROP " + (int) (100.0F * this.EI.engines[j].getControlProp()) + "%" + (this.CT.getStepControlAuto() ? " (AUTO)" : ""));
            TextScr.output(i + 245, 80, "MIX " + (int) (100.0F * this.EI.engines[j].getControlMix()) + "%");
            TextScr.output(i + 245, 60, "RAD " + (int) (100.0F * this.EI.engines[j].getControlRadiator()) + "%" + (this.CT.getRadiatorControlAuto() ? " (AUTO)" : ""));
            TextScr.output(i + 245, 40, "SUPC " + this.EI.engines[j].getControlCompressor() + "x");
            TextScr.output(245, 20, "PropAoA :" + (int) Math.toDegrees(this.EI.engines[j].getPropAoA()));
            TextScr.output(i + 455, 120, "Cyls/Cams " + this.EI.engines[j].getCylindersOperable() + "/" + this.EI.engines[0].getCylinders());
            TextScr.output(i + 455, 100, "Readyness " + (int) (100.0F * this.EI.engines[j].getReadyness()) + "%");
            TextScr.output(i + 455, 80, "PRM " + (int) ((int) (this.EI.engines[j].getRPM() * 0.02F) * 50.0F) + " rpm");
            TextScr.output(i + 455, 60, "Thrust " + (int) this.EI.engines[j].getEngineForce().x + " N");
            TextScr.output(i + 455, 40, "Fuel " + (int) (100.0F * this.M.fuel / this.M.maxFuel) + "% Nitro " + (int) (100.0F * this.M.nitro / this.M.maxNitro) + "%");
            TextScr.output(i + 455, 20, "MPrs " + (int) (1000.0F * this.EI.engines[j].getManifoldPressure()) + " mBar");
            TextScr.output(i + 640, 140, "---Controls---");
            TextScr.output(i + 640, 120, "A/C: " + (this.CT.bHasAileronControl ? "" : "AIL ") + (this.CT.bHasElevatorControl ? "" : "ELEV ") + (this.CT.bHasRudderControl ? "" : "RUD ") + (this.Gears.bIsHydroOperable ? "" : "GEAR "));
            TextScr.output(i + 640, 100, "ENG: " + (this.EI.engines[j].isHasControlThrottle() ? "" : "THTL ") + (this.EI.engines[j].isHasControlProp() ? "" : "PROP ") + (this.EI.engines[j].isHasControlMix() ? "" : "MIX ")
                    + (this.EI.engines[j].isHasControlCompressor() ? "" : "SUPC ") + (this.EI.engines[j].isPropAngleDeviceOperational() ? "" : "GVRNR "));
            TextScr.output(i + 640, 80, "PIL: (" + (int) (this.AS.getPilotHealth(0) * 100.0F) + "%)");
            TextScr.output(i + 5, 105, "V   " + (int) this.getSpeedKMH());
            TextScr.output(i + 5, 125, "AOA " + (int) (this.getAOA() * 1000.0F) / 1000.0F);
            TextScr.output(i + 5, 145, "AOS " + (int) (this.getAOS() * 1000.0F) / 1000.0F);
            TextScr.output(i + 5, 165, "Kr  " + (int) this.Or.getKren());
            TextScr.output(i + 5, 185, "Ta  " + (int) this.Or.getTangage());
            TextScr.output(i + 250, 185, "way.speed  " + this.AP.way.curr().getV() * 3.6F + "way.z " + this.AP.way.curr().z() + "  mn_time = " + this.mn_time);
            TextScr.output(i + 5, 205,
                    "<" + this.actor.name() + ">: " + ManString.tname(this.task) + ":" + ManString.name(this.maneuver) + " , WP=" + this.AP.way.Cur() + "(" + (this.AP.way.size() - 1) + ")-" + ManString.wpname(this.AP.way.curr().Action));
            TextScr.output(i + 7, 225,
                    "======= " + ManString.name(this.program[0]) + "  Sub = " + this.submaneuver + " follOffs.x = " + this.followOffset.x + "  " + (((AutopilotAI) this.AP).bWayPoint ? "Stab WPoint " : "")
                            + (((AutopilotAI) this.AP).bStabAltitude ? "Stab ALT " : "") + (((AutopilotAI) this.AP).bStabDirection ? "Stab DIR " : "") + (((AutopilotAI) this.AP).bStabSpeed ? "Stab SPD " : "   ")
                            + (((Pilot) ((Aircraft) this.actor).FM).isDumb() ? "DUMB " : " ") + (((Pilot) ((Aircraft) this.actor).FM).Gears.lgear ? "L " : " ") + (((Pilot) ((Aircraft) this.actor).FM).Gears.rgear ? "R " : " ")
                            + (((Pilot) ((Aircraft) this.actor).FM).TaxiMode ? "TaxiMode" : ""));
            TextScr.output(i + 7, 245, " ====== " + ManString.name(this.program[1]));
            TextScr.output(i + 7, 265, "  ===== " + ManString.name(this.program[2]));
            TextScr.output(i + 7, 285, "   ==== " + ManString.name(this.program[3]));
            TextScr.output(i + 7, 305, "    === " + ManString.name(this.program[4]));
            TextScr.output(i + 7, 325, "     == " + ManString.name(this.program[5]));
            TextScr.output(i + 7, 345, "      = " + ManString.name(this.program[6]) + "  " + (this.target != null ? "TARGET  " : "") + (this.target_ground != null ? "GROUND  " : "") + (this.danger != null ? "DANGER  " : ""));
            if (this.target != null && Actor.isValid(this.target.actor)) TextScr.output(i + 1, 365, " AT: (" + this.target.actor.name() + ") " + (this.target.actor instanceof Aircraft ? "" : this.target.actor.getClass().getName()));
            if (this.target_ground != null && Actor.isValid(this.target_ground)) TextScr.output(i + 1, 385, " GT: (" + this.target_ground.name() + ") ..." + this.target_ground.getClass().getName());
            TextScr.output(400, 500, "+");
            TextScr.output(400, 400, "|");
            TextScr.output((int) (400.0F + 200.0F * this.CT.AileronControl), (int) (500.0F - 200.0F * this.CT.ElevatorControl), "+");
            TextScr.output((int) (400.0F + 200.0F * this.CT.RudderControl), 400, "|");
            // TODO: +++ TD AI code backport from 4.13 +++
            TextScr.output(250, 750, "followOffset  " + this.followOffset.x + "  " + this.followOffset.y + "  " + this.followOffset.z + "  ");
            if (this.Group != null && this.Group.enemies != null) TextScr.output(250, 760, "Enemies   " + AirGroupList.length(this.Group.enemies[0]));
            TextScr.output(700, 720, "speedMode   " + this.speedMode);
            if (this.Group != null)
                // TextScr.output(700, 760, "Group task " + this.Group.grTaskName());
                TextScr.output(690, 760, "Group task  " + this.Group.grTaskName());
            if (this.AP.way.isLandingOnShip()) TextScr.output(5, 460, "Landing On Carrier");
            TextScr.output(700, 660, "gattackCounter  " + this.gattackCounter);
            // TODO: --- TD AI code backport from 4.13 ---
            TextScr.output(5, 360, "silence = " + this.silence);
        }
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    private static void rotCoord(double d, Vector3d vector3d) {
        double d1 = vector3d.x * (float) Math.cos(d) - vector3d.y * (float) Math.sin(d);
        double d2 = vector3d.x * (float) Math.sin(d) + vector3d.y * (float) Math.cos(d);
        vector3d.x = d1;
        vector3d.y = d2;
    }

    private void groundAttackGuns(Actor actor, float f) {
        if (this.submaneuver == 0 && this.sub_Man_Count == 0 && this.CT.Weapons[1] != null) {
            float f1 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
            if (f1 > 0.01F) this.bullTime = 1.0F / (f1 + this.getSpeed());
        }
        this.maxAOA = 15F;
        this.minElevCoeff = 20F;
        switch (this.submaneuver) {
            case 0:
                this.setCheckGround(true);
                this.rocketsDelay = 0;
                if (this.sub_Man_Count == 0) {
                    this.Vtarg.set(actor.pos.getAbsPoint());
                    this.koeff = (12 - this.flying) * this.M.getFullMass() / (this.Sq.squareWing * this.Wing.CyCritH_0);
                    float f7 = 0.2666667F;
                    if (this.koeff < 800F) this.koeff = 800F;
                    Ve.sub(this.Vtarg, this.Loc);
                    float f2 = (float) Ve.length();
                    float f6 = f2 - this.koeff * 1.75F;
                    if (f2 > 3000F) f2 = 3000F;
                    actor.getSpeed(tmpV3d);
                    tmpV3f.x = tmpV3d.x;
                    tmpV3f.y = tmpV3d.y;
                    tmpV3f.z = 0.0D;
                    double d = tmpV3f.length();
                    double d1 = 0.0D;
                    if (d < 0.0001D || this.Group.getAaaNum() > 8.5F && !(actor instanceof TgtShip)) {
                        tmpV3f.sub(this.Vtarg, this.Loc);
                        tmpV3f.z = 0.0D;
                        tmpV3f.normalize();
                        d1 = Math.toDegrees(Math.atan2(tmpV3f.y, tmpV3f.x));
                        d1 = this.Or.getYaw() - d1;
                        if (d1 > 180D) d1 -= 360D;
                        else if (d1 < -180D) d1 += 360D;
                        d1 = FMMath.DEG2RAD(d1);
                        d1 *= -0.33D;
                        if (f6 < 0.0F) f6 = this.koeff * 0.7F;
                        else f6 = 0.0F;
                        rotCoord(d1, tmpV3f);
                    }
                    tmpV3f.normalize();
                    this.Vtarg.x -= tmpV3f.x * this.koeff;
                    this.Vtarg.y -= tmpV3f.y * this.koeff;
                    this.Vtarg.z += this.koeff * f7;
                    this.constVtarg.set(this.Vtarg);
                    if (d < 0.0001D) {
                        rotCoord(-0.7D * d1, tmpV3f);
                        tmpV3f.normalize();
                        d1 = Math.abs(d1);
                        this.Vtarg.x -= tmpV3f.x * this.koeff * d1 + f6;
                        this.Vtarg.y -= tmpV3f.y * this.koeff * d1 + f6;
                    } else {
                        Ve.sub(this.constVtarg, this.Loc);
                        Ve.normalize();
                        this.Vxy.cross(Ve, tmpV3f);
                        Ve.sub(tmpV3f);
                        if (this.Vxy.z > 0.0D) {
                            this.Vtarg.x += Ve.y * 0.75D * this.koeff;
                            this.Vtarg.y -= Ve.x * 0.75D * this.koeff;
                        } else {
                            this.Vtarg.x -= Ve.y * 0.75D * this.koeff;
                            this.Vtarg.y += Ve.x * 0.75D * this.koeff;
                        }
                    }
                    this.Vtarg.z += this.koeff * f7 * 0.2F;
                    this.constVtarg1.set(this.Vtarg);
                    this.Vtarg.sub(this.constVtarg1, this.constVtarg);
                    if (this.Vtarg.length() < 500D) {
                        this.constVtarg1.add(this.constVtarg);
                        this.constVtarg1.scale(0.5D);
                        this.constVtarg.set(this.constVtarg1);
                    }
                }
                Ve.set(this.constVtarg1);
                Ve.sub(this.Loc);
                float f3 = (float) Ve.length();
                this.Or.transformInv(Ve);
                if (Ve.x < 0.0D && f3 < this.koeff) {
                    this.push(0);
                    this.push(8);
                    this.pop();
                    this.dontSwitch = true;
                } else {
                    Ve.normalize();
                    if (this.Loc.z < this.constVtarg1.z) this.setSpeedMode(6);
                    else {
                        this.setSpeedMode(4);
                        this.smConstSpeed = 100F;
                    }
                    if (f3 > 1000F) this.farTurnToDirection(8F);
                    else this.attackTurnToDirection(f3, f, 4F + this.Skill * 2.0F);
                    this.sub_Man_Count++;
                    if (f3 < 300F) {
                        this.submaneuver++;
                        this.gattackCounter++;
                        this.sub_Man_Count = 0;
                    }
                }
                break;

            case 1:
                Ve.set(this.constVtarg);
                Ve.sub(this.Loc);
                float f4 = (float) Ve.length();
                this.Or.transformInv(Ve);
                Ve.normalize();
                if (this.Loc.z < this.constVtarg.z) this.setSpeedMode(6);
                else {
                    this.setSpeedMode(4);
                    this.smConstSpeed = 100F;
                }
                if (f4 > 1000F) this.farTurnToDirection(8F);
                else this.attackTurnToDirection(f4, f, 4F + this.Skill * 2.0F);
                this.sub_Man_Count++;
                if (f4 < 300F) {
                    this.submaneuver++;
                    this.gattackCounter++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 2:
                if (this.rocketsDelay > 0) this.rocketsDelay--;
                if (this.sub_Man_Count > 100) this.setCheckGround(false);
                P.set(actor.pos.getAbsPoint());
                P.z += 4D;
                Engine.land();
                if (Landscape.rayHitHQ(this.Loc, P, P)) {
                    this.push(0);
                    this.push(38);
                    this.pop();
                    this.gattackCounter--;
                    if (this.gattackCounter < 0) this.gattackCounter = 0;
                }
                P.set(actor.pos.getAbsPoint());
                Ve.sub(P, this.Loc);
                float f5 = (float) Ve.length();
                this.setRSDgrd(this.shootingDeviation, f5 * this.bullTime);
                Ve.add(this.wanderVector);
                actor.getSpeed(tmpV3d);
                tmpV3f.x = (float) tmpV3d.x;
                tmpV3f.y = (float) tmpV3d.y;
                tmpV3f.z = (float) tmpV3d.z;
                tmpV3f.sub(this.Vwld);
                tmpV3f.scale(f5 * this.bullTime * 0.3333F * this.Skill);
                Ve.add(tmpV3f);
                float f8 = 0.3F * (f5 - 1000F);
                if (f8 < 0.0F) f8 = 0.0F;
                if (f8 > 300F) f8 = 300F;
                f8 += f5 * this.getAOA() * 0.005F;
                Ve.z += f8 + this.Group.getAaaNum() * 0.5F;
                this.Or.transformInv(Ve);
                if (f5 < 800F && (this.shotAtFriend <= 0 || this.distToFriend > f5)) {
                    this.shootingDeviation -= f;
                    if (this.shootingDeviation < 6 - this.gunnery) this.shootingDeviation = 6 - this.gunnery;
                    if (Math.abs(Ve.y) < 15D && Math.abs(Ve.z) < 10D) {
                        if (f5 < 800F && this.Group.getAaaNum() > 10F || f5 < 650F) this.CT.WeaponControl[0] = true;
                        if (f5 < 700F && this.Group.getAaaNum() > 10F || f5 < 550F) this.CT.WeaponControl[1] = true;
                        // TODO: Storebror: +++ Bomb Release Bug hunting
//                        if (this.CT.Weapons[2] != null && this.CT.Weapons[2][0] != null && this.CT.Weapons[2][this.CT.Weapons[2].length - 1].countBullets() != 0 && this.rocketsDelay < 1 && f5 < 570F) {
                        if (this.CT.hasBulletsLeftOnTrigger(2) && this.rocketsDelay < 1 && f5 < 570F) {
                            // TODO: Storebror: --- Bomb Release Bug hunting
                            this.CT.WeaponControl[2] = true;
                            Voice.speakAttackByRockets((Aircraft) this.actor);
                            if (this.Group.getAaaNum() > 2.0F) this.rocketsDelay += 30F - this.Group.getAaaNum();
                            else this.rocketsDelay += 30;
                        }
                    }
                }
                if (this.sub_Man_Count > 200 && Ve.x < this.getSpeed() * 2.4F || this.actor instanceof TypeStormovik && this.Alt < 80F || this.Alt < 40F) {
                    if (this.Leader == null || !Actor.isAlive(this.Leader.actor)) Voice.speakEndGattack((Aircraft) this.actor);
                    this.rocketsDelay = 0;
                    this.push(0);
                    this.push(55);
                    this.push(10);
                    if (this.Group.getAaaNum() > 3F) this.push(STRAIGHT_AND_LEVEL);
                    this.push(BREAK_AWAY);
                    this.pop();
                    this.dontSwitch = true;
                    return;
                }
                Ve.normalize();
                this.attackTurnToDirection(f5, f, 10F + this.Skill * 2.0F);
                this.setSpeedMode(4);
                this.smConstSpeed = 100F;
                break;

            default:
                this.submaneuver = 0;
                this.sub_Man_Count = 0;
                break;
        }
    }

    private void groundAttack(Actor actor, float f) {
        this.setSpeedMode(4);
        this.smConstSpeed = 120F;
        float f3 = this.Vwld.z <= 0.0D ? 4F : 3F;
        boolean hasBombs = false;
        boolean isParaTorp = false;
        // TODO: Storebror: +++ Bomb Release Bug hunting
//        if (this.CT.Weapons[3] != null && this.CT.Weapons[3][0] != null && this.CT.Weapons[3][0].countBullets() != 0) {
//            hasBombs = true;
//            if (this.CT.Weapons[3][0] instanceof ParaTorpedoGun)
//                isParaTorp = true;
//        } else if (!(this.actor instanceof TypeStormovik) && !(this.actor instanceof TypeFighter) && !(this.actor instanceof TypeDiveBomber)) {
//            this.set_maneuver(0);
//            return;
//        }
        if (this.CT.hasBulletsLeftOnTrigger(3)) {
            hasBombs = true;
            if (this.CT.firstGunOnTrigger(3) instanceof ParaTorpedoGun) isParaTorp = true;
        } else if (!(this.actor instanceof TypeStormovik) && !(this.actor instanceof TypeFighter) && !(this.actor instanceof TypeDiveBomber)) {
            this.set_maneuver(0);
            return;
        }
        // TODO: Storebror: --- Bomb Release Bug hunting
        Ve.set(actor.pos.getAbsPoint());
        if (isParaTorp)
        // TODO: Storebror: +++ Bomb Release Bug hunting
//            if (this.CT.Weapons[3][0] instanceof BombGunTorp45_36AV_A)
            if (this.CT.firstGunOnTrigger(3) instanceof BombGunTorp45_36AV_A)
                // TODO: Storebror: --- Bomb Release Bug hunting
                Ve.z = this.Loc.z - 100D + World.Rnd().nextDouble() * 50D;
            else Ve.z = this.Loc.z - 200D + World.Rnd().nextDouble() * 50D;
        float f4 = (float) this.Loc.z - (float) Ve.z;
        if (f4 < 0.0F) f4 = 0.0F;
        float f5 = (float) Math.sqrt(2.0F * f4 * 0.1019F) + 0.0017F * f4;
        actor.getSpeed(tmpV3d);
        if (actor instanceof Aircraft && tmpV3d.length() > 20D) {
            this.target = ((Aircraft) actor).FM;
            this.set_task(6);
            this.clear_stack();
            this.set_maneuver(27);
            this.dontSwitch = true;
            return;
        }
        float f6 = 0.5F;
        if (hasBombs) f6 = (f5 + 5F) * 0.33333F;
        this.Vtarg.x = (float) tmpV3d.x * f6 * this.Skill;
        this.Vtarg.y = (float) tmpV3d.y * f6 * this.Skill;
        this.Vtarg.z = (float) tmpV3d.z * f6 * this.Skill;
        Ve.add(this.Vtarg);
        Ve.sub(this.Loc);
        if (hasBombs) this.addWindCorrection();
        Ve.add(0.0D, 0.0D, -0.5F + World.Rnd().nextFloat(-2F, 0.8F));
        Vf.set(Ve);
        float f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y);
        if (hasBombs) {
            float f7 = this.getSpeed() * f5 + 500F;
            if (f1 > f7) Ve.z += 200D;
            else Ve.z = 0.0D;
        }
        this.Vtarg.set(Ve);
        this.Vtarg.normalize();
        this.Or.transformInv(Ve);
        if (!hasBombs) {
            this.groundAttackGuns(actor, f);
            return;
        }
        if (this.actor instanceof TypeFighter || this.actor instanceof TypeStormovik) {
            // TODO: +++ Ground Attack Mod Backport re-implementation by SAS~Storebror +++
            this.passCounter = 0;
            this.bombsOutCounter = 129;
            // TODO: --- Ground Attack Mod Backport re-implementation by SAS~Storebror ---
            this.groundAttackShallowDive(actor, f);
            return;
        }
        Ve.normalize();
        Vpl.set(this.Vwld);
        Vpl.normalize();
        this.CT.BayDoorControl = 1.0F;
        if (f1 + 4F * f5 < this.getSpeed() * f5 && Ve.x > 0.0D && Vpl.dot(this.Vtarg) > 0.98D) {
            if (!this.bombsOut) {
                this.bombsOut = true;
                // TODO: Storebror: +++ Bomb Release Bug hunting
//                if (this.CT.Weapons[3] != null && this.CT.Weapons[3][0] != null && this.CT.Weapons[3][0].countBullets() != 0 && !(this.CT.Weapons[3][0] instanceof BombGunPara))
                if (this.CT.hasBulletsLeftOnTrigger(3) && !(this.CT.firstGunOnTrigger(3) instanceof BombGunPara))
                    // TODO: Storebror: --- Bomb Release Bug hunting
                    Voice.speakAttackByBombs((Aircraft) this.actor);
            }
            this.push(0);
            this.push(55);
            this.push(48);
            this.pop();
            Voice.speakEndGattack((Aircraft) this.actor);
            this.CT.BayDoorControl = 0.0F;
        }
        if (Ve.x < 0.0D) {
            this.push(0);
            this.push(55);
            this.push(10);
            this.pop();
        }
        if (Math.abs(Ve.y) > 0.1D) this.CT.AileronControl = clamp11(-(float) Math.atan2(Ve.y, Ve.z) - 0.016F * this.Or.getKren());
        else this.CT.AileronControl = clamp11(-(float) Math.atan2(Ve.y, Ve.x) - 0.016F * this.Or.getKren());
        if (Math.abs(Ve.y) > 0.001D) this.CT.RudderControl = clamp11(-98F * (float) Math.atan2(Ve.y, Ve.x));
        else this.CT.RudderControl = 0.0F;
        if (this.CT.RudderControl * this.W.z > 0.0D) this.W.z = 0.0D;
        else this.W.z *= 1.04D;
        float f2 = (float) Math.atan2(Ve.z, Ve.x);
        if (Math.abs(Ve.y) < 0.002D && Math.abs(Ve.z) < 0.002D) f2 = 0.0F;
        if (Ve.x < 0.0D) f2 = 1.0F;
        else {
            if (f2 * this.W.y > 0.0D) this.W.y = 0.0D;
            if (f2 < 0.0F) {
                if (this.getOverload() < 0.1F) f2 = 0.0F;
                if (this.CT.ElevatorControl > 0.0F) this.CT.ElevatorControl = 0.0F;
            } else if (this.CT.ElevatorControl < 0.0F) this.CT.ElevatorControl = 0.0F;
        }
        if (this.getOverload() > this.maxG || this.AOA > f3 || this.CT.ElevatorControl > f2) this.CT.ElevatorControl -= 0.2F * f;
        else this.CT.ElevatorControl += 0.2F * f;
        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
    }

    private void groundAttackKamikaze(Actor actor, float f) {
        if (this.submaneuver == 0 && this.sub_Man_Count == 0 && this.CT.Weapons[1] != null) {
            float f1 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
            if (f1 > 0.01F) this.bullTime = 1.0F / f1;
        }
        this.maxAOA = 15F;
        this.minElevCoeff = 20F;
        switch (this.submaneuver) {
            case 0:
                this.setCheckGround(true);
                this.rocketsDelay = 0;
                if (this.sub_Man_Count == 0) {
                    this.Vtarg.set(actor.pos.getAbsPoint());
                    tmpV3f.set(this.Vwld);
                    tmpV3f.x += World.Rnd().nextFloat(-100F, 100F);
                    tmpV3f.y += World.Rnd().nextFloat(-100F, 100F);
                    tmpV3f.z = 0.0D;
                    if (tmpV3f.length() < 0.0001D) {
                        tmpV3f.sub(this.Vtarg, this.Loc);
                        tmpV3f.z = 0.0D;
                    }
                    tmpV3f.normalize();
                    this.Vtarg.x -= tmpV3f.x * 1500D;
                    this.Vtarg.y -= tmpV3f.y * 1500D;
                    this.Vtarg.z += 400D;
                    this.constVtarg.set(this.Vtarg);
                    Ve.sub(this.constVtarg, this.Loc);
                    Ve.normalize();
                    this.Vxy.cross(Ve, tmpV3f);
                    Ve.sub(tmpV3f);
                    this.Vtarg.z += 100D;
                    if (this.Vxy.z > 0.0D) {
                        this.Vtarg.x += Ve.y * 1000D;
                        this.Vtarg.y -= Ve.x * 1000D;
                    } else {
                        this.Vtarg.x -= Ve.y * 1000D;
                        this.Vtarg.y += Ve.x * 1000D;
                    }
                    this.constVtarg1.set(this.Vtarg);
                }
                Ve.set(this.constVtarg1);
                Ve.sub(this.Loc);
                float f2 = (float) Ve.length();
                this.Or.transformInv(Ve);
                if (Ve.x < 0.0D) {
                    this.push(0);
                    this.push(8);
                    this.pop();
                    this.dontSwitch = true;
                } else {
                    Ve.normalize();
                    this.setSpeedMode(6);
                    this.farTurnToDirection();
                    this.sub_Man_Count++;
                    if (f2 < 300F) {
                        this.submaneuver++;
                        this.gattackCounter++;
                        this.sub_Man_Count = 0;
                    }
                    if (this.sub_Man_Count > 1000) this.sub_Man_Count = 0;
                }
                break;

            case 1:
                this.setCheckGround(true);
                Ve.set(this.constVtarg);
                Ve.sub(this.Loc);
                float f3 = (float) Ve.length();
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.setSpeedMode(6);
                this.farTurnToDirection();
                this.sub_Man_Count++;
                if (f3 < 300F) {
                    this.submaneuver++;
                    this.gattackCounter++;
                    this.sub_Man_Count = 0;
                }
                if (this.sub_Man_Count > 700) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 2:
                this.setCheckGround(false);
                if (this.rocketsDelay > 0) this.rocketsDelay--;
                if (this.sub_Man_Count > 100) this.setCheckGround(false);
                Ve.set(actor.pos.getAbsPoint());
                Ve.sub(this.Loc);
                this.Vtarg.set(Ve);
                float f4 = (float) Ve.length();
                if (this.actor instanceof MXY_7) {
                    Ve.z += 0.01D * f4;
                    this.Vtarg.z += 0.01D * f4;
                }
                actor.getSpeed(tmpV3d);
                tmpV3f.x = (float) tmpV3d.x;
                tmpV3f.y = (float) tmpV3d.y;
                tmpV3f.z = (float) tmpV3d.z;
                tmpV3f.sub(this.Vwld);
                tmpV3f.scale(f4 * this.bullTime * 0.3333F * this.Skill);
                Ve.add(tmpV3f);
                float f5 = 0.3F * (f4 - 1000F);
                if (f5 < 0.0F) f5 = 0.0F;
                if (f5 > 300F) f5 = 300F;
                Ve.z += f5 + World.cur().rnd.nextFloat(-3F, 3F) * (3 - this.Skill);
                this.Or.transformInv(Ve);
                if (f4 < 50F && Math.abs(Ve.y) < 40D && Math.abs(Ve.z) < 30D) {
                    this.CT.WeaponControl[0] = true;
                    this.CT.WeaponControl[1] = true;
                    this.CT.WeaponControl[2] = true;
                    this.CT.WeaponControl[3] = true;
                }
                if (Ve.x < -50D) {
                    this.rocketsDelay = 0;
                    this.push(0);
                    this.push(55);
                    this.push(10);
                    this.pop();
                    this.dontSwitch = true;
                    return;
                }
                Ve.normalize();
                this.attackTurnToDirection(f4, f, 4F + this.Skill * 2.0F);
                this.setSpeedMode(4);
                this.smConstSpeed = 130F;
                break;

            default:
                this.submaneuver = 0;
                this.sub_Man_Count = 0;
                break;
        }
    }

    private void groundAttackTinyTim(Actor actor, float f) {
        this.maxG = 5F;
        this.maxAOA = 8F;
        this.setSpeedMode(4);
        this.smConstSpeed = 120F;
        this.minElevCoeff = 20F;
        switch (this.submaneuver) {
            case 0:
                if (this.sub_Man_Count == 0) {
                    this.Vtarg.set(actor.pos.getAbsPoint());
                    actor.getSpeed(tmpV3d);
                    if (tmpV3d.length() < 0.5D) tmpV3d.sub(this.Vtarg, this.Loc);
                    tmpV3d.normalize();
                    this.Vtarg.x -= tmpV3d.x * 3000D;
                    this.Vtarg.y -= tmpV3d.y * 3000D;
                    this.Vtarg.z += 500D;
                }
                Ve.sub(this.Vtarg, this.Loc);
                double d = Ve.length();
                this.Or.transformInv(Ve);
                this.sub_Man_Count++;
                if (d < 1000D) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                Ve.normalize();
                this.farTurnToDirection();
                break;

            case 1:
                this.Vtarg.set(actor.pos.getAbsPoint());
                this.Vtarg.z += 80D;
                Ve.sub(this.Vtarg, this.Loc);
                double d1 = Ve.length();
                this.Or.transformInv(Ve);
                this.sub_Man_Count++;
                if (d1 < 1500D) {
                    if (Math.abs(Ve.y) < 40D && Math.abs(Ve.z) < 30D) this.CT.WeaponControl[2] = true;
                    this.push(0);
                    this.push(10);
                    this.push(48);
                    this.pop();
                    this.dontSwitch = true;
                }
                if (d1 < 500D && Ve.x < 0.0D) {
                    this.push(0);
                    this.push(10);
                    this.pop();
                }
                Ve.normalize();
                if (Ve.x < 0.8D) this.turnToDirection(f);
                else this.attackTurnToDirection((float) d1, f, 2.0F + this.Skill * 1.5F);
                break;

            default:
                this.submaneuver = 0;
                this.sub_Man_Count = 0;
                break;
        }
    }

    private void groundAttackHS293(Actor actor, float f) {
        this.maxG = 5F;
        this.maxAOA = 8F;
        this.setSpeedMode(4);
        this.smConstSpeed = 120F;
        this.minElevCoeff = 20F;
        switch (this.submaneuver) {
            case 0:
                if (this.sub_Man_Count == 0) {
                    this.Vtarg.set(actor.pos.getAbsPoint());
                    actor.getSpeed(tmpV3d);
                    if (tmpV3d.length() < 0.5D) tmpV3d.sub(this.Vtarg, this.Loc);
                    tmpV3d.normalize();
                    this.Vtarg.x -= tmpV3d.x * 3000D;
                    this.Vtarg.y -= tmpV3d.y * 3000D;
                    this.Vtarg.z += 500D;
                }
                Ve.sub(this.Vtarg, this.Loc);
                double d = Ve.length();
                this.Or.transformInv(Ve);
                this.sub_Man_Count++;
                if (d < 10000D) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                Ve.normalize();
                this.farTurnToDirection();
                break;

            case 1:
                this.Vtarg.set(actor.pos.getAbsPoint());
                this.Vtarg.z += 2000D;
                Ve.sub(this.Vtarg, this.Loc);
                double d1 = Ve.angle(this.Vwld);
                Ve.z = 0.0D;
                double d2 = Ve.length();
                this.Or.transformInv(Ve);
                this.sub_Man_Count++;
                TypeGuidedBombCarrier typeguidedbombcarrier = (TypeGuidedBombCarrier) this.actor;
                if (d2 > 2000D && d2 < 6500D && d1 < 0.9D && !typeguidedbombcarrier.typeGuidedBombCgetIsGuiding()) {
                    this.CT.WeaponControl[3] = true;
                    this.push(0);
                    this.push(10);
                    this.pop();
                    this.dontSwitch = true;
                }
                if (d2 < 500D && Ve.x < 0.0D) {
                    this.push(0);
                    this.push(10);
                    this.pop();
                }
                Ve.normalize();
                if (Ve.x < 99999.8D) this.turnToDirection(f);
                else this.attackTurnToDirection((float) d2, f, 2.0F + this.Skill * 1.5F);
                break;

            default:
                this.submaneuver = 0;
                this.sub_Man_Count = 0;
                break;
        }
    }

    private void groundAttackFritzX(Actor actor, float f) {
        this.maxG = 5F;
        this.maxAOA = 8F;
        this.setSpeedMode(4);
        this.smConstSpeed = 140F;
        this.minElevCoeff = 20F;
        switch (this.submaneuver) {
            case 0:
                if (this.sub_Man_Count == 0) {
                    this.Vtarg.set(actor.pos.getAbsPoint());
                    actor.getSpeed(tmpV3d);
                    if (tmpV3d.length() < 0.5D) tmpV3d.sub(this.Vtarg, this.Loc);
                    tmpV3d.normalize();
                    this.Vtarg.x -= tmpV3d.x * 3000D;
                    this.Vtarg.y -= tmpV3d.y * 3000D;
                    this.Vtarg.z += 500D;
                }
                Ve.sub(this.Vtarg, this.Loc);
                double d = Ve.length();
                this.Or.transformInv(Ve);
                this.sub_Man_Count++;
                if (d < 15000D) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                Ve.normalize();
                this.farTurnToDirection();
                break;

            case 1:
                this.Vtarg.set(actor.pos.getAbsPoint());
                this.Vtarg.z += 2000D;
                Ve.sub(this.Vtarg, this.Loc);
                double d1 = Ve.angle(this.Vwld);
                Ve.z = 0.0D;
                double d2 = Ve.length();
                this.Or.transformInv(Ve);
                this.sub_Man_Count++;
                TypeGuidedBombCarrier typeguidedbombcarrier = (TypeGuidedBombCarrier) this.actor;
                if (d2 < 4000D && d1 < 0.9D && (d2 < 2000D || d1 > 0.4D) && !typeguidedbombcarrier.typeGuidedBombCgetIsGuiding()) {
                    this.CT.WeaponControl[3] = true;
                    this.setSpeedMode(5);
                    this.push(0);
                    this.push(10);
                    this.pop();
                    this.dontSwitch = true;
                }
                if (d2 < 500D && Ve.x < 0.0D) {
                    this.push(0);
                    this.push(10);
                    this.pop();
                }
                Ve.normalize();
                if (Ve.x < 99999.8D) this.turnToDirection(f);
                else this.attackTurnToDirection((float) d2, f, 2.0F + this.Skill * 1.5F);
                break;

            default:
                this.submaneuver = 0;
                this.sub_Man_Count = 0;
                break;
        }
    }

    private void groundAttackShallowDive(Actor actor, float f) {
        this.maxAOA = 10F;
        if (!this.hasBombs()) {
            this.set_maneuver(0);
            this.wingman(true);
            return;
        }
        if (this.first) this.RandomVal = 50F - World.cur().rnd.nextFloat(0.0F, 100F);
        this.setSpeedMode(4);
        this.smConstSpeed = 120F;
        Ve.set(actor.pos.getAbsPoint());
        Ve.sub(this.Loc);
        this.addWindCorrection();
        float f1 = (float) -Ve.z;
        if (f1 < 0.0F) f1 = 0.0F;
        Ve.z += 250D;
        float f2 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y) + this.RandomVal * (3 - this.Skill);
        if (Ve.z < -0.1F * f2) Ve.z = -0.1F * f2;
        if (this.Alt + Ve.z < 250D) Ve.z = 250F - this.Alt;
        if (this.Alt < 50F) {
            this.push(10);
            this.pop();
        }
        Vf.set(Ve);
        this.CT.BayDoorControl = 1.0F;
        float f3 = (float) this.Vwld.z * 0.1019F;
        f3 += (float) Math.sqrt(f3 * f3 + 2.0F * f1 * 0.1019F);
        float f4 = (float) Math.sqrt(this.Vwld.x * this.Vwld.x + this.Vwld.y * this.Vwld.y);
        float f5 = f4 * f3 + 10F;
        actor.getSpeed(tmpV3d);
        tmpV3d.scale(f3 * 0.35D * this.Skill);
        Ve.x += (float) tmpV3d.x;
        Ve.y += (float) tmpV3d.y;
        Ve.z += (float) tmpV3d.z;

        // TODO: +++ Ground Attack Mod Backport re-implementation by SAS~Storebror +++
        if (f5 >= f2 && this.passCounter == 0)
        // TODO: --- Ground Attack Mod Backport re-implementation by SAS~Storebror ---
        {
            this.bombsOut = true;
            // TODO: +++ Ground Attack Mod Backport re-implementation by SAS~Storebror +++
            this.bombsOutCounter = 129;
            // TODO: --- Ground Attack Mod Backport re-implementation by SAS~Storebror ---
            Voice.speakAttackByBombs((Aircraft) this.actor);
            this.setSpeedMode(6);
            this.CT.BayDoorControl = 0.0F;
            this.push(0);
            this.push(55);
            this.push(48);
            this.pop();
            this.sub_Man_Count = 0;
            // TODO: +++ Ground Attack Mod Backport re-implementation by SAS~Storebror +++
            // }
            this.passCounter += 1;
        } else if (this.passCounter >= 5) this.passCounter = 0;
        // TODO: --- Ground Attack Mod Backport re-implementation by SAS~Storebror ---
        this.Or.transformInv(Ve);
        Ve.normalize();
        this.turnToDirection(f);
    }

    private void groundAttackDiveBomber(Actor actor, float f) {
        this.maxG = 5F;
        this.maxAOA = 10F;
        this.setSpeedMode(6);
        this.maxAOA = 4F;
        this.minElevCoeff = 20F;
        if (this.CT.Weapons[3] == null || this.CT.getWeaponCount(3) == 0) {
            if (this.AP.way.curr().Action == 3) this.AP.way.next();
            this.set_maneuver(0);
            this.wingman(true);
            return;
        }
        if (this.Alt < 350F) {
            this.bombsOut = true;
            Voice.speakAttackByBombs((Aircraft) this.actor);
            this.setSpeedMode(6);
            this.CT.BayDoorControl = 0.0F;
            this.CT.AirBrakeControl = 0.0F;
            this.AP.way.next();
            this.push(0);
            this.push(8);
            this.push(2);
            this.pop();
            this.sub_Man_Count = 0;
        }
        Ve.set(actor.pos.getAbsPoint());
        Ve.sub(this.Loc);
        float f4 = (float) -Ve.z;
        this.dist = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y + Ve.z * Ve.z);
        float f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y);
        if (f1 > 1000F || this.submaneuver == 3 && this.sub_Man_Count > 100) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            actor.getSpeed(tmpV3d);
            float f5 = 0.0F;
            f5 = this.Alt / 120F;
            f5 *= 0.33333F;
            this.Vtarg.x += (float) tmpV3d.x * f5 * this.Skill;
            this.Vtarg.y += (float) tmpV3d.y * f5 * this.Skill;
            this.Vtarg.z += (float) tmpV3d.z * f5 * this.Skill;
        }
        Ve.set(this.Vtarg);
        Ve.sub(this.Loc);
        float f3 = (float) -Ve.z;
        if (f3 < 0.0F) f3 = 0.0F;
        Ve.add(this.Vxy);
        f4 = (float) -Ve.z;
        this.dist = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y + Ve.z * Ve.z);
        f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y);
        if (this.submaneuver < 2) Ve.z = 0.0D;
        Vf.set(Ve);
        Ve.normalize();
        Vpl.set(this.Vwld);
        Vpl.normalize();
        switch (this.submaneuver) {
            default:
                break;

            case 0:
                this.push();
                this.pop();
                if (f4 < 1200F) this.man1Dist = 400F;
                else if (f4 > 4500F) this.man1Dist = 50F;
                else this.man1Dist = 50F + 350F * (4500F - f4) / 3300F;
                float f6 = 0.01F * f4;
                if (f6 < 10F) f6 = 10F;
                this.Vxy.set(World.Rnd().nextFloat(-10F, 10F), World.Rnd().nextFloat(-10F, 10F), World.Rnd().nextFloat(5F, f6));
                this.Vxy.scale(4F - this.Skill);
                float f7 = 2E-005F * f4 * f4;
                if (f7 < 60F) f7 = 60F;
                if (f7 > 350F) f7 = 350F;
                this.Vxy.z += f7;
                this.submaneuver++;
                break;

            case 1:
                this.setSpeedMode(4);
                this.smConstSpeed = 110F;
                if (f1 >= this.man1Dist) break;
                this.CT.AirBrakeControl = 1.0F;
                if (this.actor instanceof TypeFighter) this.CT.FlapsControl = 1.0F;
                this.push();
                this.push(6);
                this.safe_pop();
                this.submaneuver++;
                break;

            case 2:
                this.setSpeedMode(4);
                this.smConstSpeed = 110F;
                this.sub_Man_Count++;
                this.CT.AirBrakeControl = 1.0F;
                if (this.actor instanceof TypeFighter) this.CT.FlapsControl = 1.0F;
                float f2 = this.Or.getKren();
                if (this.Or.getTangage() > -90F) {
                    f2 -= 180F;
                    if (f2 < -180F) f2 += 360F;
                }
                this.CT.AileronControl = clamp11((float) (-0.04F * f2 - 0.5D * this.getW().x));
                if (this.getOverload() < 4F) this.CT.ElevatorControl += 0.3F * f;
                else this.CT.ElevatorControl -= 0.3F * f;
                this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
                if (this.sub_Man_Count > 30 && this.Or.getTangage() < -90F || this.sub_Man_Count > 150) {
                    this.sub_Man_Count = 0;
                    this.submaneuver++;
                }
                break;

            case 3:
                this.CT.AirBrakeControl = 1.0F;
                if (this.actor instanceof TypeFighter) this.CT.FlapsControl = 1.0F;
                this.CT.BayDoorControl = 1.0F;
                this.setSpeedMode(4);
                this.smConstSpeed = 110F;
                this.sub_Man_Count++;
                if (this.sub_Man_Count > 80) {
                    float f8 = (float) this.Vwld.z * 0.1019F;
                    f8 += (float) Math.sqrt(f8 * f8 + 2.0F * f3 * 0.1019F) + 0.00035F * f3;
                    float f9 = (float) Math.sqrt(this.Vwld.x * this.Vwld.x + this.Vwld.y * this.Vwld.y);
                    float f10 = f9 * f8;
                    float f11 = 0.2F * (f1 - f10);
                    this.Vxy.z += f11;
                    if (this.Vxy.z > 0.7F * f3) this.Vxy.z = 0.7F * f3;
                }
                if (this.sub_Man_Count > 100 && this.Alt < 1000F && Vpl.dot(Ve) > 0.99D || this.Alt < 600F) {
                    this.bombsOut = true;
                    Voice.speakAttackByBombs((Aircraft) this.actor);
                    this.CT.BayDoorControl = 0.0F;
                    this.CT.AirBrakeControl = 0.0F;
                    this.AP.way.next();
                    this.push(0);
                    this.push(8);
                    this.push(2);
                    this.pop();
                }
                break;
        }
        this.Or.transformInv(Ve);
        Ve.normalize();
        if (this.submaneuver == 3) this.attackTurnToDirection(1000F, f, 30F);
        else if (this.submaneuver != 2) this.turnToDirection(f);
    }

    private void groundAttackTorpedo(Actor actor, float f) {
        float f4 = 50F;
        this.maxG = 5F;
        this.maxAOA = 8F;
        float f5 = 0.0F;
        this.setSpeedMode(4);
        Class class1 = null;
        // TODO: Storebror: +++ Bomb Release Bug hunting
//        if (this.CT.Weapons[3][0] instanceof TorpedoGun) {
//            TorpedoGun torpedogun = (TorpedoGun) this.CT.Weapons[3][0];
        if (this.CT.firstGunOnTrigger(3) instanceof TorpedoGun) {
            TorpedoGun torpedogun = (TorpedoGun) this.CT.firstGunOnTrigger(3);
            // TODO: Storebror: --- Bomb Release Bug hunting
            class1 = (Class) Property.value(torpedogun.getClass(), "bulletClass", null);
        }
        this.smConstSpeed = 100F;
        if (class1 != null) {
            this.smConstSpeed = Property.floatValue(class1, "dropSpeed", 100F) / 3.7F;
            f4 = Property.floatValue(class1, "dropAltitude", 50F) + 10F;
        }
        if (this.actor instanceof Swordfish) {
            this.setSpeedMode(11);
            f4 = 20F;
        }
        float f6 = 0.0F;
        this.minElevCoeff = 20F;
        Ve.set(actor.pos.getAbsPoint());
        Ve.sub(this.Loc);
        if (this.first) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            this.submaneuver = 0;
        }
        if (this.submaneuver == 1 && this.sub_Man_Count == 0) {
            tmpV3f.set(actor.pos.getAbsPoint());
            tmpV3f.sub(this.Vtarg);
            actor.getSpeed(tmpV3d);
            if (tmpV3f.length() > 0.0001D) tmpV3f.normalize();
            this.Vxy.set(tmpV3f.y * 3000D, -tmpV3f.x * 3000D, 0.0D);
            this.direc = this.Vxy.dot(Ve) > 0.0D;
            if (this.direc) this.Vxy.scale(-1D);
            this.Vtarg.add(this.Vxy);
            this.Vtarg.x += tmpV3d.x * 80D;
            this.Vtarg.y += tmpV3d.y * 80D;
            this.Vtarg.z = 80D;
            this.TargDevV.set(World.cur().rnd.nextFloat(-16F, 16F) * (3.5D - this.Skill), World.cur().rnd.nextFloat(-16F, 16F) * (3.5D - this.Skill), 0.0D);
        }
        if (this.submaneuver == 2) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            actor.getSpeed(tmpV3d);
            float f7 = 20F;
            if (class1 != null) f7 = Property.floatValue(class1, "velocity", 1.0F);
            float f9 = actor.collisionR();
            if (f9 > 80F) f5 = 50F;
            if (f9 > 130F) f5 = 100F;
            if (f9 < 25F) f5 = -50F;
            float f12 = 950F;
            if (f4 > 110F) {
                f6 = f4;
                f12 += f4 * 0.4F;
            }
            if (this.actor instanceof JU_88NEW) f12 += 90F;
            double d1 = Math.sqrt(0.204D * this.Loc.z);
            double d2 = 1.0D * d1 * this.getSpeed();
            double d3 = (f12 + f5 - d2) / f7;
            this.Vtarg.x += (float) (tmpV3d.x * d3);
            this.Vtarg.y += (float) (tmpV3d.y * d3);
            this.Vtarg.z = f4;
            if (this.Loc.z < 30D) this.Vtarg.z += 3D * (30D - this.Loc.z);
            this.Vtarg.add(this.TargDevV);
        }
        Ve.set(this.Vtarg);
        Ve.sub(this.Loc);
        float f2 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y + Ve.z * Ve.z);
        Vf.set(Ve);
        Vpl.set(this.Vwld);
        Vpl.normalize();
        if (this.Alt < f4 - 5F) {
            if (this.Vwld.z < 0.0D) this.Vwld.z += (f4 - this.Alt) * 0.25F;
            if (this.Alt < 8F) this.set_maneuver(2);
            if (this.Alt < 20F && f2 < 75F) this.set_maneuver(2);
        } else if (this.Alt > f4 + 5F && this.submaneuver == 1 && this.Vwld.z > 0.0D) this.Vwld.z--;
        switch (this.submaneuver) {
            default:
                break;

            case 0:
                this.sub_Man_Count++;
                if (this.sub_Man_Count > 60) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                this.addWindCorrection();
                break;

            case 1:
                this.setSpeedMode(4);
                if (this.actor instanceof Swordfish) this.setSpeedMode(9);
                this.sub_Man_Count++;
                if (f2 < 1200F || f2 < 2000F && ZutiSupportMethods.isStaticActor(actor)) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                this.addWindCorrection();
                break;

            case 2:
                this.setSpeedMode(4);
                if (f2 < 800F + f5 + f6) {
                    if (this.actor instanceof TypeHasToKG && ((TypeHasToKG) this.actor).isSalvo()) {
                        int i = 0;
                        float f10 = actor.collisionR() * 1.8F;
                        i = (int) Math.toDegrees(Math.atan(f10 / 800F));
                        i += World.Rnd().nextInt(-2, 2);
                        if (i < 3) i = 3;
                        this.AS.setSpreadAngle(i);
                    }
                    this.CT.WeaponControl[3] = true;
                    this.setSpeedMode(6);
                    this.AP.way.next();
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                    break;
                }
                if (!ZutiSupportMethods.isStaticActor(actor)) break;
                float f8 = Property.floatValue(class1, "velocity", 20F);
                float f11 = Property.floatValue(class1, "traveltime", 100F);
                float f13 = f8 * f11 - 150F;
                if (f2 < f13 && World.land().isWater(this.actor.pos.getAbsPoint().x, this.actor.pos.getAbsPoint().y)) {
                    this.CT.WeaponControl[3] = true;
                    this.setSpeedMode(6);
                    this.AP.way.next();
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 3:
                this.setSpeedMode(9);
                this.sub_Man_Count++;
                if (this.sub_Man_Count > 150) {
                    this.task = 3;
                    this.push(0);
                    this.push(8);
                    this.pop();
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                }
                break;
        }
        this.Or.transformInv(Ve);
        if (this.submaneuver == 3) {
            if (this.sub_Man_Count < 20) Ve.set(1.0D, 0.09D, 0.03D);
            else Ve.set(1.0D, 0.09D, 0.01D);
            if (!this.direc) Ve.y *= -1D;
        }
        Ve.normalize();
        this.turnToDirection(f);
    }

    private void groundAttackTorpedoToKG(Actor actor, float f) {
        float f2 = 50F;
        this.maxG = 5F;
        this.maxAOA = 8F;
        this.setSpeedMode(4);
        Class class1 = null;
        // TODO: Storebror: +++ Bomb Release Bug hunting
//        if (this.CT.Weapons[3][0] instanceof TorpedoGun) {
//            TorpedoGun torpedogun = (TorpedoGun) this.CT.Weapons[3][0];
        if (this.CT.firstGunOnTrigger(3) instanceof TorpedoGun) {
            TorpedoGun torpedogun = (TorpedoGun) this.CT.firstGunOnTrigger(3);
            // TODO: Storebror: --- Bomb Release Bug hunting
            class1 = (Class) Property.value(torpedogun.getClass(), "bulletClass", null);
        }
        this.smConstSpeed = 100F;
        if (class1 != null) {
            this.smConstSpeed = Property.floatValue(class1, "dropSpeed", 100F) / 3.7F;
            f2 = Property.floatValue(class1, "dropAltitude", 50F) + 10F;
        }
        this.minElevCoeff = 20F;
        Ve.set(actor.pos.getAbsPoint());
        Ve.sub(this.Loc);
        if (this.first) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            this.submaneuver = 0;
        }
        if (this.submaneuver == 1 && this.sub_Man_Count == 0) {
            tmpV3f.set(actor.pos.getAbsPoint());
            tmpV3f.sub(this.Vtarg);
            if (tmpV3f.length() > 0.0001D) tmpV3f.normalize();
            this.Vxy.set(tmpV3f.y * 3000D, -tmpV3f.x * 3000D, 0.0D);
            this.direc = this.Vxy.dot(Ve) > 0.0D;
            if (this.direc) this.Vxy.scale(-1D);
            this.Vtarg.add(this.Vxy);
            this.Vtarg.z = 80D;
        }
        if (this.submaneuver == 2) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            this.Vtarg.z = f2;
            if (this.Loc.z < 30D) this.Vtarg.z += 3D * (30D - this.Loc.z);
        }
        Ve.set(this.Vtarg);
        Ve.sub(this.Loc);
        float f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y + Ve.z * Ve.z);
        Vf.set(Ve);
        Vpl.set(this.Vwld);
        Vpl.normalize();
        if (this.Alt < f2 - 5F) {
            if (this.Vwld.z < 0.0D) this.Vwld.z += (f2 - this.Alt) * 0.25F;
            if (this.Alt < 8F) this.set_maneuver(2);
            if (this.Alt < 20F && f1 < 75F) this.set_maneuver(2);
        } else if (this.Alt > f2 + 5F && this.submaneuver == 1 && this.Vwld.z > 0.0D) this.Vwld.z--;
        switch (this.submaneuver) {
            default:
                break;

            case 0:
                this.sub_Man_Count++;
                if (this.sub_Man_Count > 60) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                this.addWindCorrection();
                break;

            case 1:
                this.setSpeedMode(4);
                this.sub_Man_Count++;
                if (f1 < 4000F) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                this.addWindCorrection();
                break;

            case 2:
                this.setSpeedMode(4);
                double d = Ve.angle(this.Vwld);
                float f3 = 180F - (this.Or.getYaw() - actor.pos.getAbsOrient().getYaw());
                if (f3 < -180F) f3 += 360F;
                else if (f3 > 180F) f3 -= 360F;
                float f4 = Property.floatValue(class1, "velocity", 20F);
                float f5 = Property.floatValue(class1, "traveltime", 100F);
                float f6 = f4 * f5 - 10F;
                if (f6 > 2700F) f6 = 2700F;
                double d1 = (Math.abs(f3) - 90F) * 8F;
                if (d1 < 0.0D) d1 = 0.0D;
                if (this.Skill == 2) d1 += 100D;
                if (f1 < f6 - d1 && f1 > 1800F && d < 0.2D) {
                    actor.getSpeed(tmpV3d);
                    float f7 = 1.0F;
                    if (this.Skill == 2) f7 = World.Rnd().nextFloat(0.8F, 1.2F);
                    else if (this.Skill == 3) f7 = World.Rnd().nextFloat(0.9F, 1.1F);
                    f7 = (float) (f7 + 0.1D);
                    ToKGUtils.setTorpedoGyroAngle(this, f3, (float) (1.95D * tmpV3d.length()) * f7);
                    if (((TypeHasToKG) this.actor).isSalvo()) {
                        int i = 0;
                        float f8 = actor.collisionR() * 1.8F;
                        i = (int) Math.toDegrees(Math.atan(f8 / (f6 - d1)));
                        i += World.Rnd().nextInt(-2, 2);
                        if (i < 1) i = 1;
                        this.AS.setSpreadAngle(i);
                    }
                    this.CT.WeaponControl[3] = true;
                    this.setSpeedMode(6);
                    this.AP.way.next();
                    this.push(8);
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                    break;
                }
                if (f1 < 1500F) {
                    ToKGUtils.setTorpedoGyroAngle(this, 0.0F, 0.0F);
                    this.set_maneuver(51);
                }
                break;

            case 3:
                this.setSpeedMode(9);
                this.push(8);
                this.pop();
                this.task = 61;
                this.sub_Man_Count++;
                boolean flag = false;
                for (int j = 0; j < this.CT.Weapons[3].length; j++)
                    if (this.CT.Weapons[3][j] != null && !(this.CT.Weapons[3][j] instanceof BombGunNull) && this.CT.Weapons[3][j].countBullets() != 0) flag = true;

                if (this.sub_Man_Count > 800 || !flag) {
                    this.task = 3;
                    this.push(21);
                    this.push(8);
                    this.pop();
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                }
                break;
        }
        this.Or.transformInv(Ve);
        if (this.submaneuver == 3) {
            if (this.sub_Man_Count < 20) Ve.set(1.0D, 0.09D, 0.03D);
            else Ve.set(1.0D, 0.09D, 0.01D);
            if (!this.direc) Ve.y *= -1D;
        }
        Ve.normalize();
        this.turnToDirection(f);
    }

    private void groundAttackCassette(Actor actor, float f) {
        this.maxG = 5F;
        this.maxAOA = 8F;
        this.setSpeedMode(4);
        this.smConstSpeed = 120F;
        this.minElevCoeff = 20F;
        Ve.set(actor.pos.getAbsPoint());
        Ve.sub(this.Loc);
        float f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y);
        if (this.submaneuver == 3 && this.sub_Man_Count > 0 && this.sub_Man_Count < 45 && f1 > 200F) {
            tmpV3f.set(this.Vxy);
            float f4 = (float) tmpV3f.dot(Ve);
            tmpV3f.scale(-f4);
            tmpV3f.add(Ve);
            float f7 = (float) tmpV3f.length();
            float f6;
            if (f7 > 150F) f6 = 7.5F / f7;
            else f6 = 0.05F;
            tmpV3f.scale(f6);
            tmpV3f.z = 0.0D;
            this.Vwld.add(tmpV3f);
        }
        if (f1 <= 200F) this.sub_Man_Count = 50;
        if (this.first) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            this.submaneuver = 0;
        }
        if (this.submaneuver == 1 && this.sub_Man_Count == 0) {
            tmpV3f.set(actor.pos.getAbsPoint());
            actor.getSpeed(tmpV3d);
            if (tmpV3d.length() < 0.5D) tmpV3d.set(Ve);
            tmpV3d.normalize();
            this.Vxy.set((float) tmpV3d.x, (float) tmpV3d.y, (float) tmpV3d.z);
            this.Vtarg.x -= tmpV3d.x * 3000D;
            this.Vtarg.y -= tmpV3d.y * 3000D;
            this.Vtarg.z += 250D;
        }
        if (this.submaneuver == 2 && this.sub_Man_Count == 0) {
            this.Vtarg.set(actor.pos.getAbsPoint());
            this.Vtarg.x -= this.Vxy.x * 1000D;
            this.Vtarg.y -= this.Vxy.y * 1000D;
            this.Vtarg.z += 50D;
        }
        if (this.submaneuver == 3 && this.sub_Man_Count == 0) {
            this.checkGround = false;
            this.Vtarg.set(actor.pos.getAbsPoint());
            this.Vtarg.x += this.Vxy.x * 1000D;
            this.Vtarg.y += this.Vxy.y * 1000D;
            this.Vtarg.z += 50D;
        }
        Ve.set(this.Vtarg);
        Ve.sub(this.Loc);
        this.addWindCorrection();
        float f2 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y + Ve.z * Ve.z);
        Vf.set(Ve);
        this.Or.transformInv(Ve);
        Ve.normalize();
        Vpl.set(this.Vwld);
        Vpl.normalize();
        if (this.Alt < 10F) {
            this.push(0);
            this.push(2);
            this.pop();
        }
        switch (this.submaneuver) {
            case 0:
                this.setSpeedMode(9);
                this.sub_Man_Count++;
                if (this.sub_Man_Count > 60) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 1:
                this.setSpeedMode(9);
                this.sub_Man_Count++;
                if (f2 < 1000F) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 2:
                this.setSpeedMode(6);
                this.sub_Man_Count++;
                if (f2 < 155F) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                if (this.sub_Man_Count > 320) {
                    this.push(0);
                    this.push(10);
                    this.pop();
                }
                break;

            case 3:
                this.setSpeedMode(6);
                this.sub_Man_Count++;
                this.Vwld.z *= 0.8D;
                this.Or.transformInv(this.Vwld);
                this.Vwld.y *= 0.9D;
                this.Or.transform(this.Vwld);
                float f5 = this.sub_Man_Count;
                if (f5 < 100F) f5 = 100F;
                if (this.Alt > 45F) this.Vwld.z -= 0.002F * (this.Alt - 45F) * f5;
                else this.Vwld.z -= 0.005F * (this.Alt - 45F) * f5;
                if (this.Alt < 0.0F) this.Alt = 0.0F;
                if (f2 < 1080F + this.getSpeed() * (float) Math.sqrt(2.0F * this.Alt / 9.81F)) this.bombsOut = true;
                if (Ve.x < 0.0D || f2 < 350F || this.sub_Man_Count > 160) {
                    this.push(0);
                    this.push(10);
                    this.push(10);
                    this.pop();
                }
                break;
        }
        if (this.submaneuver == 0) Ve.set(1.0D, 0.0D, 0.0D);
        this.turnToDirection(f);
    }
    // TODO: --- TD AI code backport from 4.13 ---

    public void goodFighterVsFighter(float f) {
        Ve.sub(this.target.Loc, this.Loc);
        float f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y);
        this.Or.transformInv(Ve);
        float f2 = (float) Ve.length();
        float f4 = 1.0F / f2;
        this.Vtarg.set(Ve);
        this.Vtarg.scale(f4);
        float f5 = (this.Energy - this.target.Energy) * 0.1019F;
        tmpV3f.sub(this.target.Vwld, this.Vwld);
        if (this.sub_Man_Count == 0) {
            float f6 = 0.0F;
            if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0].countBullets() > 0) f6 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
            else if (this.CT.Weapons[0] != null) f6 = ((GunGeneric) this.CT.Weapons[0][0]).bulletSpeed();
            if (f6 > 0.01F) this.bullTime = 1.0F / f6;
            this.submanDelay = 0;
        }
        if (f1 < 1500.0F) {
            float f7 = 0.0F;
            float f9 = 0.0F;
            if (this.Vtarg.x > -0.2) {
                f7 = 3.0F * ((float) this.Vtarg.x + 0.2F);
                this.Vxy.set(tmpV3f);
                this.Vxy.scale(1.0);
                this.Or.transformInv(this.Vxy);
                this.Vxy.add(Ve);
                this.Vxy.normalize();
                f9 = 10.0F * (float) (this.Vxy.x - this.Vtarg.x);
                if (f9 < -1.0F) f9 = -1.0F;
                if (f9 > 1.0F) f9 = 1.0F;
            } else f7 = 3.0F * ((float) this.Vtarg.x + 0.2F);
            // TODO: +++ TD AI code backport from 4.13 +++
            if (this.submaneuver == SUB_MAN4 && this.Vtarg.x < 0.93D && f2 < 300D) {
                if (f5 > 500F) this.submaneuver = SUB_MAN5;
                else this.submaneuver = SUB_MAN1;
                this.submanDelay = 30;
            }
            // TODO: --- TD AI code backport from 4.13 ---
            if (this.submaneuver != SUB_MAN4 && f5 > 300.0 && this.Vtarg.x > 0.75) {
                this.submaneuver = SUB_MAN4;
                this.submanDelay = 240;
            }
            float f11 = 0.0015F * f5 + 6.0E-4F * f1 + f7 + 0.5F * f9;
            if (f11 > 0.9F && this.submanDelay == 0) {
                if (this.Vtarg.x > 0.5 || f1 * 2.0 < f2) {
                    this.submaneuver = SUB_MAN4;
                    this.submanDelay = 240;
                } else {
                    this.submaneuver = SUB_MAN3;
                    this.submanDelay = 120;
                }
            } else if (f5 > 800.0F && this.submaneuver == SUB_MAN0 || f5 > 1000.0F) {
                Ve.set(0.0, 0.0, 800.0);
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN0;
                    this.submanDelay = 30;
                }
            } else if (f2 > 450.0F && this.submaneuver == SUB_MAN2 || f2 > 600.0F) {
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN2;
                    this.submanDelay = 60;
                }
            } else if (this.submanDelay == 0) {
                this.submaneuver = SUB_MAN1;
                this.submanDelay = 30;
            }
        } else if (f1 < 3000.0F) {
            if (this.Vtarg.x < 0.5) {
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN3;
                    this.submanDelay = 120;
                }
            } else if (f5 > 600.0F && this.submaneuver == SUB_MAN0 || f5 > 800.0F) {
                Ve.set(0.0, 0.0, 800.0);
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN0;
                    this.submanDelay = 120;
                }
            } else if (f5 > -200.0F && this.submaneuver >= SUB_MAN4 || f5 > -100.0F) {
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN4;
                    this.submanDelay = 120;
                }
            } else {
                Ve.set(0.0, 0.0, this.Loc.z);
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN0;
                    this.submanDelay = 120;
                }
            }
        } else {
            Ve.set(0.0, 0.0, 1000.0);
            if (this.submanDelay == 0) {
                this.submaneuver = SUB_MAN0;
                this.submanDelay = 180;
            }
        }
        switch (this.submaneuver) {
            case SUB_MAN0: {
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                tmpV3f.set(Ve);
                tmpV3f.z = 0.0;
                float f3 = (float) tmpV3f.length();
                tmpV3f.normalize();
                this.Vtarg.set(this.target.Vwld);
                this.Vtarg.z = 0.0;
                float f8 = (float) tmpV3f.dot(this.Vtarg);
                float f10 = this.getSpeed() - f8;
                if (f10 < 10.0F) f10 = 10.0F;
                float f12 = f3 / f10;
                if (f12 < 0.0F) f12 = 0.0F;
                tmpV3f.scale(this.Vtarg, f12);
                Ve.add(tmpV3f);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.setSpeedMode(9);
                this.farTurnToDirection();
                this.sub_Man_Count++;
                break;
            }
            case SUB_MAN1:
                // TODO: +++ TD AI code backport from 4.13 +++
                this.setSpeedMode(11);
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() + 5.0F));
                this.CT.RudderControl = 0.0F;
                break;
            case SUB_MAN2: {
                this.setSpeedMode(9);
                tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
                float f13 = 6.0F;
                if (this.Or.getKren() > 0.0F) Ve.set(100.0, -f13, 10.0);
                else Ve.set(100.0, f13, 10.0);
                tmpOr.transform(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.farTurnToDirection();
                break;
            }
            case SUB_MAN3:
                this.minElevCoeff = 20.0F;
                this.setSpeedMode(9);
                tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
                Ve.sub(this.target.Loc, this.Loc);
                Ve.z = 0.0;
                Ve.normalize();
                Ve.z = 0.4;
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.attackTurnToDirection(1000.0F, f, 15.0F);
                break;
            case SUB_MAN4:
                this.minElevCoeff = 20.0F;
                this.boomAttack(f);
                // TODO: +++ TD AI code backport from 4.13 +++
// this.setSpeedMode(9);
                this.setSpeedMode(11);
                // TODO: --- TD AI code backport from 4.13 ---
                break;

            // TODO: +++ TD AI code backport from 4.13 +++
            case SUB_MAN5:
                this.minElevCoeff = 20F;
                this.setSpeedMode(11);
                tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
                Ve.sub(this.target.Loc, this.Loc);
                Ve.z = 0.0D;
                Ve.normalize();
                Ve.z = 0.95D;
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.attackTurnToDirection(1000F, f, 15F);
                break;
            // TODO: --- TD AI code backport from 4.13 ---

            default:
                this.minElevCoeff = 20.0F;
                this.fighterVsFighter(f);
        }
        if (this.submanDelay > 0) this.submanDelay--;
    }

    public void bNZFighterVsFighter(float f) {
        Ve.sub(this.target.Loc, this.Loc);
        this.Or.transformInv(Ve);
        this.dist = (float) Ve.length();
        float f1 = (float) Math.sqrt(Ve.x * Ve.x + Ve.y * Ve.y);
        float f2 = 1.0F / this.dist;
        this.Vtarg.set(Ve);
        this.Vtarg.scale(f2);
        float f3 = (this.Energy - this.target.Energy) * 0.1019F;
        tmpV3f.sub(this.target.Vwld, this.Vwld);
        if (this.sub_Man_Count == 0) {
            float f4 = 0.0F;
            if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0].countBullets() > 0) f4 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
            else if (this.CT.Weapons[0] != null) f4 = ((GunGeneric) this.CT.Weapons[0][0]).bulletSpeed();
            if (f4 > 0.01F) this.bullTime = 1.0F / f4;
            this.submanDelay = 0;
        }
        if (f1 < 1500.0F) {
            float f5 = 0.0F;
            float f7 = 0.0F;
            if (this.Vtarg.x > -0.2) {
                f5 = 3.0F * ((float) this.Vtarg.x + 0.2F);
                this.Vxy.set(tmpV3f);
                this.Vxy.scale(1.0);
                this.Or.transformInv(this.Vxy);
                this.Vxy.add(Ve);
                this.Vxy.normalize();
                f7 = 20.0F * (float) (this.Vxy.x - this.Vtarg.x);
                if (f7 < -1.0F) f7 = -1.0F;
                if (f7 > 1.0F) f7 = 1.0F;
            }
            float f9 = f3 * 0.0015F + f1 * 6.0E-4F + f5 + f7;
            if (f9 > 0.8F && this.submaneuver >= SUB_MAN3 || f9 > 1.2F) {
                if (tmpV3f.length() < 100.0) {
                    if (this.submanDelay == 0) {
                        this.submaneuver = SUB_MAN4;
                        this.submanDelay = 120;
                    }
                } else if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN3;
                    this.submanDelay = 120;
                }
            } else if (f3 > 800.0F && this.submaneuver == SUB_MAN0 || f3 > 1000.0F) {
                Ve.set(0.0, 0.0, 800.0);
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN0;
                    this.submanDelay = 30;
                }
            } else if (this.dist > 450.0F && this.submaneuver == SUB_MAN2 || this.dist > 600.0F) {
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN2;
                    this.submanDelay = 60;
                }
            } else if (this.submanDelay == 0) {
                this.submaneuver = SUB_MAN1;
                this.submanDelay = 30;
            }
        } else if (f1 < 3000.0F) {
            if (f3 > 600.0F && this.submaneuver == SUB_MAN0 || f3 > 800.0F) {
                Ve.set(0.0, 0.0, 800.0);
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN0;
                    this.submanDelay = 120;
                }
            } else if (f3 > -200.0F && this.submaneuver >= SUB_MAN3 || f3 > -100.0F) {
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN3;
                    this.submanDelay = 120;
                }
            } else {
                Ve.set(0.0, 0.0, this.Loc.z);
                if (this.submanDelay == 0) {
                    this.submaneuver = SUB_MAN0;
                    this.submanDelay = 120;
                }
            }
        } else {
            Ve.set(0.0, 0.0, 1000.0);
            if (this.submanDelay == 0) {
                this.submaneuver = SUB_MAN0;
                this.submanDelay = 180;
            }
        }
        switch (this.submaneuver) {
            case SUB_MAN0: {
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                tmpV3f.set(Ve);
                tmpV3f.z = 0.0;
                this.dist = (float) tmpV3f.length();
                tmpV3f.normalize();
                this.Vtarg.set(this.target.Vwld);
                this.Vtarg.z = 0.0;
                float f6 = (float) tmpV3f.dot(this.Vtarg);
                float f8 = this.getSpeed() - f6;
                if (f8 < 10.0F) f8 = 10.0F;
                float f10 = this.dist / f8;
                if (f10 < 0.0F) f10 = 0.0F;
                tmpV3f.scale(this.Vtarg, f10);
                Ve.add(tmpV3f);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.setSpeedMode(9);
                this.farTurnToDirection();
                this.sub_Man_Count++;
                break;
            }
            case SUB_MAN1:
                // TODO: +++ TD AI code backport from 4.13 +++
                this.setSpeedMode(11);
                // TODO: --- TD AI code backport from 4.13 ---
                this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
                this.CT.ElevatorControl = clamp11(-0.04F * (this.Or.getTangage() + 5.0F));
                this.CT.RudderControl = 0.0F;
                break;
            case SUB_MAN2:
                this.setSpeedMode(9);
                tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
                if (this.Or.getKren() > 0.0F) Ve.set(100.0, -6.0, 10.0);
                else Ve.set(100.0, 6.0, 10.0);
                tmpOr.transform(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.farTurnToDirection();
                break;
            case SUB_MAN3:
                this.minElevCoeff = 20.0F;
                this.fighterVsFighter(f);
                this.setSpeedMode(6);
                break;
            default:
                this.minElevCoeff = 20.0F;
                this.fighterVsFighter(f);
        }
        if (this.submanDelay > 0) this.submanDelay--;
    }

    public void setBomberAttackType(int i) {
        float f;
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.target != null && Actor.isValid(this.target.actor))
            // TODO: --- TD AI code backport from 4.13 ---
            f = this.target.actor.collisionR();
        else f = 15.0F;
        
//        f = 10.0F; // TESTING!
        
        this.setRandomTargDeviation(0.8F);
        switch (i) {
            case 0:
                this.ApproachV.set(-300.0F + World.Rnd().nextFloat(-100.0F, 100.0F), 0.0, 600.0F + World.Rnd().nextFloat(-100.0F, 100.0F));
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 150.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 3.9F * f;
                this.TargYS = 0.43F * f;
                this.TargZS = 0.43F * f;
                break;
            case 1:
                this.ApproachV.set(-300.0F + World.Rnd().nextFloat(-100.0F, 100.0F), 0.0, 600.0F + World.Rnd().nextFloat(-100.0F, 100.0F));
                this.TargV.set(this.target.EI.engines[World.Rnd().nextInt(0, this.target.EI.getNum() - 1)].getEnginePos());
                this.TargV.x--;
                this.ApproachR = 150.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 3.9F * f;
                this.TargYS = 0.43F * f;
                this.TargZS = 0.43F * f;
                break;
            case 2:
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.actor instanceof ME_163B1A) this.ApproachV.set(-1200F + World.Rnd().nextFloat(-200F, 200F), 0.0D, 100F + World.Rnd().nextFloat(-50F, 50F));
                else this.ApproachV.set(-600F + World.Rnd().nextFloat(-200F, 200F), 0.0D, 100F + World.Rnd().nextFloat(-50F, 50F));
                // TODO: --- TD AI code backport from 4.13 ---
                this.TargV.set(this.target.EI.engines[World.Rnd().nextInt(0, this.target.EI.getNum() - 1)].getEnginePos());
                this.TargV.x--;
                this.ApproachR = 300.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 3.9F * f;
                this.TargYS = 0.43F * f;
                this.TargZS = 0.43F * f;
                break;
            case 3:
                this.ApproachV.set(2600.0, 0.0, 300.0F + World.Rnd().nextFloat(-100.0F, 100.0F));
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 800.0F;
                this.TargY = 25.0F;
                this.TargZ = 15.0F;
                this.TargYS = 3.0F;
                this.TargZS = 3.0F;
                break;
            case 4:
                this.ApproachV.set(-400.0F + World.Rnd().nextFloat(-100.0F, 100.0F), 0.0, -200.0F + World.Rnd().nextFloat(-50.0F, 50.0F));
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 300.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 1.3F * f;
                this.TargYS = 0.26F * f;
                this.TargZS = 0.26F * f;
                break;
            case 5:
                this.ApproachV.set(0.0, 0.0, 0.0);
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 600.0F;
                this.TargY = 25.0F;
                this.TargZ = 12.0F;
                this.TargYS = 0.26F * f;
                this.TargZS = 0.26F * f;
                break;
            case 6:
                this.ApproachV.set(600.0, 600 - World.Rnd().nextInt(0, 1) * 1200 + World.Rnd().nextFloat(-100.0F, 100.0F), 300.0);
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 300.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 1.2F * f;
                this.TargYS = 0.26F * f;
                this.TargZS = 0.26F * f;
                break;
            case 7:
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.actor instanceof ME_163B1A) this.ApproachV.set(-1500F + World.Rnd().nextFloat(-200F, 200F), 0.0D, 100F + World.Rnd().nextFloat(-50F, 50F));
                else
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.ApproachV.set(-1000.0F + World.Rnd().nextFloat(-200.0F, 200.0F), 0.0, 100.0F + World.Rnd().nextFloat(-50.0F, 50.0F));
                this.TargV.set(this.target.EI.engines[World.Rnd().nextInt(0, this.target.EI.getNum() - 1)].getEnginePos());
                this.ApproachR = 200.0F;
                this.TargY = 10.0F;
                this.TargZ = 10.0F;
                this.TargYS = 2.0F;
                this.TargZS = 2.0F;
                break;
            case 8:
                this.ApproachV.set(-1000.0F + World.Rnd().nextFloat(-200.0F, 200.0F), 0.0, 100.0F + World.Rnd().nextFloat(-50.0F, 50.0F));
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 200.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 3.9F * f;
                this.TargYS = 0.2F * f;
                this.TargZS = 0.2F * f;
                break;
            case 9:
                this.ApproachV.set(-600.0F + World.Rnd().nextFloat(-100.0F, 100.0F), 0.0, 600.0F + World.Rnd().nextFloat(-100.0F, 100.0F));
                this.TargV.set(0.0, 0.0, 0.0);
                this.ApproachR = 300.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 3.9F * f;
                this.TargYS = 0.2F * f;
                this.TargZS = 0.2F * f;
                break;
            case 10:
                this.ApproachV.set(-600.0F + World.Rnd().nextFloat(-100.0F, 100.0F), 0.0, -300.0F + World.Rnd().nextFloat(-100.0F, 100.0F));
                // TODO: +++ TD AI code backport from 4.13 +++
                this.TargV.set(((TypeJazzPlayer) this.actor).getAttackVector()); // Would be 4.13 code...
                // TODO: --- TD AI code backport from 4.13 ---
                // this.TargV.set(100.0, 0.0, -400.0);

                // New TypeJazzPlayer Implementation
                if (this.actor instanceof TypeJazzPlayer) this.TargV.set(((TypeJazzPlayer) this.actor).getAttackVector());
                else this.TargV.set(100.0, 0.0, -400.0);

                this.ApproachR = 300.0F;
                this.TargY = 2.1F * f;
                this.TargZ = 3.9F * f;
                this.TargYS = 0.43F * f;
                this.TargZS = 0.43F * f;
                break;
            default:
                this.ApproachV.set(-1000.0F + World.Rnd().nextFloat(-200.0F, 200.0F), 0.0, 100.0F + World.Rnd().nextFloat(-50.0F, 50.0F));
                this.TargV.set(this.target.EI.engines[World.Rnd().nextInt(0, this.target.EI.getNum() - 1)].getEnginePos());
                this.ApproachR = 200.0F;
                this.TargY = 10.0F;
                this.TargZ = 10.0F;
                this.TargYS = 2.0F;
                this.TargZS = 2.0F;
        }
        float f1 = 0.0F;
        if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0].countBullets() > 0) f1 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
        else if (this.CT.Weapons[0] != null) f1 = ((GunGeneric) this.CT.Weapons[0][0]).bulletSpeed();
        if (f1 > 0.01F) this.bullTime = 1.0F / f1;
//        System.out.println("### setBomberAttackType(" + i + "), f=" + f + ", ApproachR=" + ApproachR + ", TargY=" + TargY + ", TargZ=" + TargZ + ", TargYS=" + TargYS + ", TargZS=" + TargZS + ", bullTime=" + bullTime);
    }

    public void fighterVsBomber(float f) {
        this.maxAOA = 15.0F;
        tmpOr.setAT0(this.target.Vwld);
        switch (this.submaneuver) {
            case SUB_MAN0: {
                this.minElevCoeff = 4.0F;
                this.rocketsDelay = 0;
                this.bulletDelay = 0;
                double d = 1.0E-4 * this.target.Loc.z;
                Ve.sub(this.target.Loc, this.Loc);
                tmpOr.transformInv(Ve);
                this.scaledApproachV.set(this.ApproachV);
                this.scaledApproachV.x -= 700.0 * d;
                this.scaledApproachV.z += 500.0 * d;
                Ve.add(this.scaledApproachV);
                Ve.normalize();
                tmpV3f.scale(this.scaledApproachV, -1.0);
                tmpV3f.normalize();
                double d1 = Ve.dot(tmpV3f);
                Ve.set(this.Vwld);
                Ve.normalize();
                tmpV3f.set(this.target.Vwld);
                tmpV3f.normalize();
                double d2 = Ve.dot(tmpV3f);
                Ve.set(this.scaledApproachV);
                Ve.x += 60.0 * (2.0 * (1.0 - d1) + 4.0 * (1.0 - d2));
                tmpOr.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                double d3 = Ve.z;
                tmpV3f.set(Ve);
                tmpV3f.z = 0.0;
                float f1 = (float) tmpV3f.length();
                float f4 = 0.55F + 0.15F * this.Skill;
                tmpV3f.normalize();
                this.Vtarg.set(this.target.Vwld);
                this.Vtarg.z = 0.0;
                float f7 = (float) tmpV3f.dot(this.Vtarg);
                float f8 = this.getSpeed() - f7;
                if (f8 < 10.0F) f8 = 10.0F;
                float f9 = f1 / f8;
                if (f9 < 0.0F) f9 = 0.0F;
                tmpV3f.scale(this.Vtarg, f9 * f4);
                Ve.add(tmpV3f);
                this.Or.transformInv(Ve);
                Ve.normalize();
                if (d3 > -200.0) this.setSpeedMode(9);
                else {
                    this.setSpeedMode(4);
                    this.smConstSpeed = 180.0F;
                }
                this.attackTurnToDirection(f1, f, 10.0F * (float) (1.0 + d));
                this.sub_Man_Count++;
                if (f1 < this.ApproachR * (1.0 + d) && d3 < 200.0) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;
            }
            case SUB_MAN1: {
                this.minElevCoeff = 20.0F;
                Ve.set(this.TargV);
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                float f2 = (float) Ve.length();
                float f5 = 0.55F + 0.15F * this.Skill;
                tmpV3f.sub(this.target.Vwld, this.Vwld);
                float f10 = f2 * this.bullTime * 0.0025F;
                if (f10 > 0.05F) f10 = 0.05F;
                tmpV3f.scale(f2 * f10 * f5);
                Ve.add(tmpV3f);
                this.Vtarg.set(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                ((Maneuver) this.target).incDangerAggressiveness(1, (float) Ve.x, f2, this);
                if (f2 > 3200.0F || this.Vtarg.z > 1500.0) {
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                    this.set_maneuver(0);
                } else {
                    if (Ve.x < 0.3) {
                        this.Vtarg.z += 0.012F * this.Skill * (800.0F + f2) * (0.3 - Ve.x);
                        Ve.set(this.Vtarg);
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.attackTurn(f2, f, 10.0F + this.Skill * 8.0F);
                    // TODO: --- TD AI code backport from 4.13 ---
                    this.setSpeedMode(4);
                    this.smConstSpeed = 180.0F;
                    if (f2 < 400.0F) {
                        this.submaneuver++;
                        this.sub_Man_Count = 0;
                    }
                }
                break;
            }
            case SUB_MAN2: {
                this.minElevCoeff = 20.0F;
                if (this.rocketsDelay > 0) this.rocketsDelay--;
                if (this.bulletDelay > 0) this.bulletDelay--;
                if (this.bulletDelay == 120) this.bulletDelay = 0;
                this.setRandomTargDeviation(0.8F);
                Ve.set(this.TargV);
                Ve.add(this.TargDevV);
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                this.Vtarg.set(Ve);
                float f3 = (float) Ve.length();
                float f6 = 0.55F + 0.15F * this.Skill;
                float f11 = 2.0E-4F * this.Skill;
                tmpV3f.sub(this.target.Vwld, this.Vwld);
                Vpl.set(tmpV3f);
                tmpV3f.scale(f3 * (this.bullTime + f11) * f6);
                Ve.add(tmpV3f);
                tmpV3f.set(Vpl);
                tmpV3f.scale(f3 * this.bullTime * f6);
                this.Vtarg.add(tmpV3f);
                if (f3 > 4000.0F || this.Vtarg.z > 2000.0) {
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                    this.set_maneuver(0);
                } else {
                    this.Or.transformInv(this.Vtarg);
                    if (this.Vtarg.x > 0.0 && f3 < 500.0F && (this.shotAtFriend <= 0 || this.distToFriend > f3) && Math.abs(this.Vtarg.y) < this.TargY - this.TargYS * this.Skill && Math.abs(this.Vtarg.z) < this.TargZ - this.TargZS * this.Skill
                            && this.bulletDelay < 120) {
                        // TODO: +++ TD AI code backport from 4.13 +++
                        // if(!(actor instanceof TypeJazzPlayer)) // Would be 4.13 code...
                        // CT.WeaponControl[1] = true;
                        // CT.WeaponControl[0] = true;
                        // TODO: --- TD AI code backport from 4.13 ---
                        if (!(this.actor instanceof KI_46_OTSUHEI)) this.CT.WeaponControl[0] = true;
                        this.CT.WeaponControl[1] = true;
                        this.bulletDelay += 2;
                        if (this.bulletDelay >= 118) {
                            int i = (int) (this.target.getW().length() * 150.0);
                            if (i > 60) i = 60;
                            this.bulletDelay += World.Rnd().nextInt(i * this.Skill, 2 * i * this.Skill);
                        }
                        // TODO: Storebror: +++ Bomb Release Bug hunting
//                        if (this.CT.Weapons[2] != null && this.CT.Weapons[2][0] != null && this.CT.Weapons[2][this.CT.Weapons[2].length - 1].countBullets() != 0 && this.rocketsDelay < 1) {
                        if (this.CT.hasBulletsLeftOnTrigger(2) && this.rocketsDelay < 1) {
                            // TODO: Storebror: --- Bomb Release Bug hunting
                            tmpV3f.sub(this.target.Vwld, this.Vwld);
                            this.Or.transformInv(tmpV3f);
                            if (Math.abs(tmpV3f.y) < this.TargY - this.TargYS * this.Skill && Math.abs(tmpV3f.z) < this.TargZ - this.TargZS * this.Skill) this.CT.WeaponControl[2] = true;
                            Voice.speakAttackByRockets((Aircraft) this.actor);
                            this.rocketsDelay += 120;
                        }
                        ((Maneuver) this.target).incDangerAggressiveness(1, 0.8F, f3, this);
                        ((Pilot) this.target).setAsDanger(this.actor);
                    }
                    this.Vtarg.set(Ve);
                    this.Or.transformInv(Ve);
                    Ve.normalize();
                    ((Maneuver) this.target).incDangerAggressiveness(1, (float) Ve.x, f3, this);
                    if (Ve.x < 0.3) {
                        this.Vtarg.z += 0.01F * this.Skill * (800.0F + f3) * (0.3 - Ve.x);
                        Ve.set(this.Vtarg);
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.attackTurn(f3, f, 10.0F + this.Skill * 8.0F);
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (this.target.getSpeed() > 70.0F) {
                        this.setSpeedMode(2);
                        this.tailForStaying = this.target;
                        this.tailOffset.set(-20.0, 0.0, 0.0);
                    } else {
                        this.setSpeedMode(4);
                        this.smConstSpeed = 70.0F;
                    }
                    if (this.strikeEmer) {
                        Vpl.sub(this.strikeTarg.Loc, this.Loc);
                        tmpV3f.set(Vpl);
                        this.Or.transformInv(tmpV3f);
                        if (tmpV3f.x < 0.0) break;
                        tmpV3f.sub(this.strikeTarg.Vwld, this.Vwld);
                        tmpV3f.scale(0.7);
                        Vpl.add(tmpV3f);
                        this.Or.transformInv(Vpl);
                        this.push();
                        if (Vpl.z < 0.0) {
                            this.push(0);
                            this.push(8);
                            this.push(60);
                        } else {
                            this.push(0);
                            this.push(8);
                            this.push(61);
                        }
                        this.pop();
                        this.strikeEmer = false;
                        this.submaneuver = 0;
                        this.sub_Man_Count = 0;
                    }
                    if (this.sub_Man_Count > 600) {
                        this.push(8);
                        this.pop();
                    }
                }
                break;
            }
            default:
                this.submaneuver = SUB_MAN0;
                this.sub_Man_Count = 0;
                this.set_maneuver(0);
        }
    }

    public void fighterUnderBomber(float f) {
        this.maxAOA = 15.0F;
        switch (this.submaneuver) {
            case SUB_MAN0: {
                this.rocketsDelay = 0;
                this.bulletDelay = 0;
                Ve.set(this.ApproachV);
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                tmpV3f.set(Ve);
                tmpV3f.z = 0.0;
                float f1 = (float) tmpV3f.length();
                float f4 = 0.55F + 0.15F * this.Skill;
                tmpV3f.normalize();
                this.Vtarg.set(this.target.Vwld);
                this.Vtarg.z = 0.0;
                float f7 = (float) tmpV3f.dot(this.Vtarg);
                float f8 = this.getSpeed() - f7;
                if (f8 < 10.0F) f8 = 10.0F;
                float f9 = f1 / f8;
                if (f9 < 0.0F) f9 = 0.0F;
                tmpV3f.scale(this.Vtarg, f9 * f4);
                Ve.add(tmpV3f);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.setSpeedMode(4);
                this.smConstSpeed = 140.0F;
                this.farTurnToDirection();
                this.sub_Man_Count++;
                if (f1 < this.ApproachR) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;
            }
            case SUB_MAN1: {
                Ve.set(this.TargV);
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                float f2 = (float) Ve.length();
                float f5 = 0.55F + 0.15F * this.Skill;
                tmpV3f.sub(this.target.Vwld, this.Vwld);
                float f10 = f2 * this.bullTime * 0.0025F;
                if (f10 > 0.02F) f10 = 0.02F;
                tmpV3f.scale(f2 * f10 * f5);
                Ve.add(tmpV3f);
                this.Vtarg.set(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                ((Maneuver) this.target).incDangerAggressiveness(1, (float) Ve.x, f2, this);
                if (f2 > 3200.0F || this.Vtarg.z > 1500.0) {
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                } else {
                    if (Ve.x < 0.3) {
                        this.Vtarg.z += 0.01F * this.Skill * (800.0F + f2) * (0.3 - Ve.x);
                        Ve.set(this.Vtarg);
                        this.Or.transformInv(Ve);
                        Ve.normalize();
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    this.attackTurn(f2, f, 10.0F);
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (this.target.getSpeed() > 120.0F) {
                        this.setSpeedMode(2);
                        this.tailForStaying = this.target;
                    } else {
                        this.setSpeedMode(4);
                        this.smConstSpeed = 120.0F;
                    }
                    // TODO: +++ TD AI code backport from 4.13 +++
                    if (f2 < 600F) {
                        this.tailForStaying = this.target;
                        this.setSpeedMode(1);
                        this.tailOffset.set(-190D, 0.0D, 0.0D);
                    }
                    // TODO: --- TD AI code backport from 4.13 ---
                    if (f2 < 400.0F) {
                        this.submaneuver++;
                        this.sub_Man_Count = 0;
                    }
                }
                break;
            }
            case SUB_MAN2: {
                this.setCheckStrike(false);
                if (this.rocketsDelay > 0) this.rocketsDelay--;
                if (this.bulletDelay > 0) this.bulletDelay--;
                if (this.bulletDelay == 120) this.bulletDelay = 0;
                this.setRandomTargDeviation(0.8F);
                Ve.set(this.TargV);
                Ve.add(this.TargDevV);
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                float f3 = (float) Ve.length();
                float f6 = 0.55F + 0.15F * this.Skill;
                tmpV3f.sub(this.target.Vwld, this.Vwld);
                tmpV3f.scale(f3 * this.bullTime * f6);
                Ve.add(tmpV3f);
                this.Vtarg.set(Ve);
                this.Or.transformInv(Ve);
                if (f3 > 4000.0F || this.Vtarg.z > 2000.0) {
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                    break;
                }

                // New TypeJazzPlayer Implementation
                if (f3 < 330F && Math.abs(this.Or.getKren()) < 3F) if (this.actor instanceof TypeJazzPlayer) {
                    if (((TypeJazzPlayer) this.actor).hasSlantedWeaponBullets()) this.CT.WeaponControl[1] = true;
                } else {
                    this.CT.WeaponControl[0] = true;
                    this.CT.WeaponControl[1] = true;
                }

//				else {
//					if (this.AS.astatePilotStates[1] < 100 && f3 < 330.0F && Math.abs(this.Or.getKren()) < 3.0F)
//					// TODO: +++ TD AI code backport from 4.13 +++
//					{
//						this.CT.WeaponControl[0] = true;
//						this.CT.WeaponControl[1] = true;
//					}
//					// TODO: --- TD AI code backport from 4.13 ---
                Ve.normalize();
                if (Ve.x < 0.3) {
                    this.Vtarg.z += 0.01F * this.Skill * (800.0F + f3) * (0.3 - Ve.x);
                    Ve.set(this.Vtarg);
                    this.Or.transformInv(Ve);
                    Ve.normalize();
                }
                // TODO: +++ TD AI code backport from 4.13 +++
                this.attackTurn(f3, f, 6.0F + this.Skill * 3.0F);
                // TODO: --- TD AI code backport from 4.13 ---
                this.setSpeedMode(1);
                this.tailForStaying = this.target;
                this.tailOffset.set(-190.0, 0.0, 0.0);
                if (this.sub_Man_Count > 10000 || f3 < 240.0F) {
                    this.push(9);
                    this.pop();
                }
//				}
                break;
            }
            default:
                this.submaneuver = SUB_MAN0;
                this.sub_Man_Count = 0;
        }
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void setWingmanAttackType(int i) {
        this.wAttType = i;
        this.TargV.set(0.0D, 0.0D, 0.0D);
        this.setRandomTargDeviation(8F);
        this.ApproachR = 500F;
        switch (i) {
            case 1:
                this.ApproachV.set(-300F + World.Rnd().nextFloat(-100F, 100F), 0.0D, 600F + World.Rnd().nextFloat(-100F, 100F));
                break;

            case 2:
                this.ApproachV.set(1000D, 0.0D, 300F + World.Rnd().nextFloat(-100F, 100F));
                break;

            case 3:
                this.ApproachV.set(-400F + World.Rnd().nextFloat(-100F, 100F), 0.0D, -200F + World.Rnd().nextFloat(-50F, 50F));
                break;

            case 4:
                this.ApproachV.set(0.0D, 0.0D, 0.0D);
                break;

            case 5:
                this.ApproachV.set(-1000F + World.Rnd().nextFloat(-200F, 200F), 0.0D, 100F + World.Rnd().nextFloat(-50F, 50F));
                break;

            case 6:
                this.ApproachV.set(-600F + World.Rnd().nextFloat(-100F, 100F), 0.0D, 200F + World.Rnd().nextFloat(-100F, 100F));
                break;

            case 7:
                this.ApproachV.set(-400F + World.Rnd().nextFloat(-100F, 100F), 0.0D, -300F + World.Rnd().nextFloat(-100F, 100F));

                // New TypeJazzPlayer Implementation
                if (this.actor instanceof TypeJazzPlayer) this.TargV.set(((TypeJazzPlayer) this.actor).getAttackVector());
                else this.TargV.set(100D, 0.0D, -400D);

                // this.TargV.set(100D, 0.0D, -400D);
                break;

            case 8:
                this.ApproachV.set(900D, World.Rnd().nextFloat(500F, 700F), -300D);
                break;

            case 9:
                this.ApproachV.set(900D, World.Rnd().nextFloat(-500F, -700F), -300D);
                break;

            default:
                this.ApproachV.set(-1000F + World.Rnd().nextFloat(-200F, 200F), 0.0D, 100F + World.Rnd().nextFloat(-50F, 50F));
                break;
        }
        float f = 0.0F;
        if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0].countBullets() > 0) f = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
        else if (this.CT.Weapons[0] != null) f = ((GunGeneric) this.CT.Weapons[0][0]).bulletSpeed();
        if (f > 0.01F) this.bullTime = 1.0F / f;
    }

    public void wingmanVsFighter(float f) {
        this.maxAOA = 20F;
        tmpOr.setAT0(this.target.Vwld);
        switch (this.submaneuver) {
            case 0:
                this.minElevCoeff = 4F;
                this.rocketsDelay = 0;
                this.bulletDelay = 0;
                double d = 0.0001D * this.target.Loc.z;
                Ve.sub(this.target.Loc, this.Loc);
                tmpOr.transformInv(Ve);
                this.scaledApproachV.set(this.ApproachV);
                this.scaledApproachV.x -= 700D * d;
                this.scaledApproachV.z += 500D * d;
                Ve.add(this.scaledApproachV);
                Ve.normalize();
                tmpV3f.scale(this.scaledApproachV, -1D);
                tmpV3f.normalize();
                double d1 = Ve.dot(tmpV3f);
                Ve.set(this.Vwld);
                Ve.normalize();
                tmpV3f.set(this.target.Vwld);
                tmpV3f.normalize();
                double d2 = Ve.dot(tmpV3f);
                Ve.set(this.scaledApproachV);
                Ve.x += 60D * (2D * (1.0D - d1) + 4D * (1.0D - d2));
                tmpOr.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                double d3 = Ve.z;
                tmpV3f.set(Ve);
                tmpV3f.z = 0.0D;
                float f1 = (float) tmpV3f.length();
                float f3 = 0.55F + 0.15F * this.Skill;
                tmpV3f.normalize();
                this.Vtarg.set(this.target.Vwld);
                this.Vtarg.z = 0.0D;
                float f5 = (float) tmpV3f.dot(this.Vtarg);
                float f6 = this.getSpeed() - f5;
                if (f6 < 10F) f6 = 10F;
                float f7 = f1 / f6;
                if (f7 < 0.0F) f7 = 0.0F;
                tmpV3f.scale(this.Vtarg, f7 * f3);
                Ve.add(tmpV3f);
                this.Or.transformInv(Ve);
                Ve.normalize();
                this.setSpeedMode(9);
                this.attackTurnToDirection(f1, f, 10F * (float) (1.0D + d));
                this.sub_Man_Count++;
                if (f1 < this.ApproachR * (1.0D + d) && d3 < 200D) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 1:
                this.minElevCoeff = 20F;
                Ve.set(this.TargV);
                this.target.Or.transform(Ve);
                Ve.add(this.target.Loc);
                Ve.sub(this.Loc);
                float f2 = (float) Ve.length();
                float f4 = 0.55F + 0.15F * this.Skill;
                tmpV3f.sub(this.target.Vwld, this.Vwld);
                float f8 = f2 * this.bullTime * 0.0025F;
                if (f8 > 0.05F) f8 = 0.05F;
                tmpV3f.scale(f2 * f8 * f4);
                Ve.add(tmpV3f);
                this.Vtarg.set(Ve);
                this.Or.transformInv(Ve);
                Ve.normalize();
                ((Maneuver) this.target).incDangerAggressiveness(1, (float) Ve.x, f2, this);
                if (f2 > 3200F || this.Vtarg.z > 1500D) {
                    this.submaneuver = 0;
                    this.sub_Man_Count = 0;
                    this.set_maneuver(0);
                    break;
                }
                if (Ve.x < 0.3D) {
                    this.Vtarg.z += 0.012F * this.Skill * (800F + f2) * (0.3D - Ve.x);
                    Ve.set(this.Vtarg);
                    this.Or.transformInv(Ve);
                    Ve.normalize();
                }
                this.attackTurnToDirection(f2, f, 10F + this.Skill * 8F);
                this.setSpeedMode(9);
                if (f2 < 800F) {
                    this.submaneuver++;
                    this.sub_Man_Count = 0;
                }
                break;

            case 2:
                this.set_maneuver(27);
                break;

            default:
                this.submaneuver = 0;
                this.sub_Man_Count = 0;
                this.set_maneuver(0);
                break;
        }
    }

    public boolean friendCheck(float f, Point3d point3d) {
        Aircraft aircraft = War.getNearestFriendAtPoint(point3d, (Aircraft) this.actor, f - 10F);
        if (aircraft != null && aircraft.FM instanceof RealFlightModel) {
            tmpV3d.sub(this.target.Loc, this.Loc);
            this.Or.transformInv(tmpV3d);
            tmpV3d.normalize();
            if (tmpV3d.x > 0.8D) {
                this.push();
                this.set_maneuver(14);
                return false;
            }
        }
        return true;
    }

    public void fighterVsFighter(float f) {
        if (this.sub_Man_Count == 0) {
            float f1 = 0.0F;
            if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0].countBullets() > 0) f1 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
            else if (this.CT.Weapons[0] != null) f1 = ((GunGeneric) this.CT.Weapons[0][0]).bulletSpeed();
            if (f1 > 0.01F) this.bullTime = 1.0F / (f1 + this.getSpeed());
            this.followType = World.cur().rnd.nextInt(0, 2);
        }
        this.maxAOA = 15F;
        if (this.rocketsDelay > 0) this.rocketsDelay--;
        if (this.bulletDelay > 0) this.bulletDelay--;
        if (this.bulletDelay == 122) this.bulletDelay = 0;
        this.losVector.set(1.0D, 0.0D, 0.0D);
        this.Or.transform(this.losVector);
        if (this.dist < 700F && VisCheck.checkLeadShotBlock((Aircraft) this.actor, (Aircraft) this.target.actor)) {
            if (this.lastKnownTargetLoc.x == -1D) {
                this.lastKnownTargetLoc.set(this.target.Loc);
                this.scaledApproachV.set(this.target.Vwld);
            }
            Ve.sub(this.lastKnownTargetLoc, this.Loc);
            float f2 = (3 - this.Skill) * 0.2F;
            float f4 = World.Rnd().nextFloat(-f2, f2);
            this.lastKnownTargetLoc.x += (this.scaledApproachV.x + f4) * Time.tickConstLenFs();
            this.lastKnownTargetLoc.y += (this.scaledApproachV.y + f4) * Time.tickConstLenFs();
            this.lastKnownTargetLoc.z += (this.scaledApproachV.z + f4) * Time.tickConstLenFs();
        } else {
            this.lastKnownTargetLoc.x = -1D;
            Ve.sub(this.target.Loc, this.Loc);
        }
        float f3 = (float) Ve.length();
        float f5 = 1.0F + 0.15F * this.sp;
        float f6 = 0.00053F * (1.0F + this.AOA * 0.007F);
        tmpV3f.sub(this.target.Vwld, this.Vwld);
        Vpl.set(tmpV3f);
        tmpV3f.scale(f3 * (this.bullTime + f6) * f5);
        Ve.add(tmpV3f);
        this.losVector.scale(Ve.length());
        this.corrVector.sub(Ve, this.losVector);
        this.corrVector.scale(f5);
        this.corrVector.z += this.nSD(this.gunnery, this.flying, 0, Time.current());
        this.corrVector.y += this.nSD(this.gunnery, this.flying, 1, Time.current());
        this.Vtarg.set(Ve);
        Ve.add(this.corrVector);
        this.Or.transformInv(Vpl);
        float f7 = (float) -Vpl.x;
        if (f7 < 0.001F) f7 = 0.001F;
        Vpl.normalize();
        if (Vpl.x < -0.94D && f3 / f7 < 1.5F * (3 - this.Skill)) {
            this.push();
            this.set_maneuver(14);
            return;
        }
        this.Or.transformInv(this.Vtarg);
        if (this.Vtarg.x > 0.0D && f3 < 600F && (this.shotAtFriend <= 0 || this.distToFriend > f3) && this.friendCheck(f3, this.target.Loc)) {
            if (Math.abs(this.Vtarg.y) < 13D && Math.abs(this.Vtarg.z) < 13D) ((Maneuver) this.target).incDangerAggressiveness(1, 0.95F, f3, this);
            if (Math.abs(this.Vtarg.y) < 5.5F + 2.5F * this.gunnery && Math.abs(this.Vtarg.z) < 5.5D + 2.5D * this.gunnery && this.bulletDelay < 120) {
                if (f3 < this.convAI + (4 - this.Skill) * 200 / this.getOverload()) {
                    this.CT.WeaponControl[0] = true;
                    if (this.subSkill > 6) if (this.sp >= 0.0F) this.sp -= f * 0.15F;
                    else this.sp += f * 0.15F;
                }
                this.bulletDelay += 2;
                if (this.bulletDelay >= 118) {
                    int i = (int) (this.target.getW().length() * 130D);
                    if (i > 45) i = 45;
                    this.bulletDelay += World.Rnd().nextInt(i * (this.Skill + 1), 2 * i * (this.Skill + 1));
                }
                if (f3 < this.convAI + (4 - this.Skill) * 75) {
                    this.CT.WeaponControl[1] = true;
                    // TODO: Storebror: +++ Bomb Release Bug hunting
//                    if (f3 < 100F && this.CT.Weapons[2] != null && this.CT.Weapons[2][0] != null && this.CT.Weapons[2][this.CT.Weapons[2].length - 1].countBullets() != 0 && this.rocketsDelay < 1) {
                    if (f3 < 100F && this.CT.hasBulletsLeftOnTrigger(2) && this.rocketsDelay < 1) {
                        // TODO: Storebror: --- Bomb Release Bug hunting
                        tmpV3f.sub(this.target.Vwld, this.Vwld);
                        this.Or.transformInv(tmpV3f);
                        if (Math.abs(tmpV3f.y) < 4D && Math.abs(tmpV3f.z) < 4D) this.CT.WeaponControl[2] = true;
                        Voice.speakAttackByRockets((Aircraft) this.actor);
                        this.rocketsDelay += 120;
                    }
                }
                ((Pilot) this.target).setAsDanger(this.actor);
            }
        }
        this.Vtarg.set(Ve);
        this.Or.transformInv(Ve);
        Ve.normalize();
        ((Maneuver) this.target).incDangerAggressiveness(1, (float) Ve.x, f3, this);
        if (Ve.x < 0.3D) {
            this.Vtarg.z += 0.045D * this.flying * (800F + f3) * (0.3D - Ve.x);
            Ve.set(this.Vtarg);
            this.Or.transformInv(Ve);
            Ve.normalize();
        }
        this.attackTurn(f3, f, 10F + this.Skill * 8F);
        if (this.Alt > 5F + (4 - this.Skill) * 5 && Ve.x > 0.975D && f3 < this.convAI * 1.25F) {
            if (this.followType == 0) this.setSpeedMode(2);
            else if (this.followType == 1) this.setSpeedMode(1);
            else this.setSpeedMode(9);
            this.tailForStaying = this.target;
            this.tailOffset.set(-this.convAI * 0.75F, 0.0D, 0.0D);
        } else this.setSpeedMode(9);
        if (this.sp > 0.0F && this.shootingPoint > this.sp) this.sp += f * 0.05F;
        else if (this.shootingPoint < this.sp) this.sp -= f * 0.05F;
    }

    public void boomAttack(float f) {
        if (this.sub_Man_Count == 0) {
            float f1 = 0.0F;
            if (this.CT.Weapons[1] != null && this.CT.Weapons[1][0].countBullets() > 0) f1 = ((GunGeneric) this.CT.Weapons[1][0]).bulletSpeed();
            else if (this.CT.Weapons[0] != null) f1 = ((GunGeneric) this.CT.Weapons[0][0]).bulletSpeed();
            if (f1 > 0.01F) this.bullTime = 1.0F / f1;
        }
        this.maxAOA = 15F;
        if (this.rocketsDelay > 0) this.rocketsDelay--;
        if (this.bulletDelay > 0) this.bulletDelay--;
        if (this.bulletDelay == 122) this.bulletDelay = 0;
        if (this.dist < 700F && VisCheck.checkLeadShotBlock((Aircraft) this.actor, (Aircraft) this.target.actor)) {
            if (this.lastKnownTargetLoc.x == -1D) {
                this.lastKnownTargetLoc.set(this.target.Loc);
                this.scaledApproachV.set(this.target.Vwld);
            }
            Ve.sub(this.lastKnownTargetLoc, this.Loc);
            float f2 = (3 - this.Skill) * 0.2F;
            float f4 = World.Rnd().nextFloat(-f2, f2);
            this.lastKnownTargetLoc.x += (this.scaledApproachV.x + f4) * Time.tickConstLenFs();
            this.lastKnownTargetLoc.y += (this.scaledApproachV.y + f4) * Time.tickConstLenFs();
            this.lastKnownTargetLoc.z += (this.scaledApproachV.z + f4) * Time.tickConstLenFs();
        } else {
            this.lastKnownTargetLoc.x = -1D;
            Ve.sub(this.target.Loc, this.Loc);
        }
        float f3 = (float) Ve.length();
        float f5 = 1.0F + 0.15F * this.sp;
        tmpV3f.sub(this.target.Vwld, this.Vwld);
        Vpl.set(tmpV3f);
        tmpV3f.scale(f3 * this.bullTime * f5);
        Ve.add(tmpV3f);
        tmpV3f.set(Vpl);
        this.Or.transformInv(Vpl);
        float f6 = (float) -Vpl.x;
        if (f6 < 0.001F) f6 = 0.001F;
        Vpl.normalize();
        if (Vpl.x < -0.94D && f3 / f6 < 1.5F * (3 - this.Skill)) {
            this.push();
            this.set_maneuver(14);
            return;
        }
        this.losVector.set(1.0D, 0.0D, 0.0D);
        this.Or.transform(this.losVector);
        this.losVector.scale(Ve.length());
        this.corrVector.sub(Ve, this.losVector);
        this.corrVector.scale(f5);
        this.corrVector.z += this.nSD(this.gunnery, this.flying, 0, Time.current());
        this.corrVector.y += this.nSD(this.gunnery, this.flying, 1, Time.current());
        this.Vtarg.set(Ve);
        Ve.add(this.corrVector);
        this.Or.transformInv(this.Vtarg);
        if (this.Vtarg.x > 0.0D && f3 < 700F && (this.shotAtFriend <= 0 || this.distToFriend > f3) && this.friendCheck(f3, this.target.Loc) && Math.abs(this.Vtarg.y) < 5.5F + 2.5F * this.gunnery && Math.abs(this.Vtarg.z) < 5.5D + 2.5D * this.gunnery
                && this.bulletDelay < 120) {
            ((Maneuver) this.target).incDangerAggressiveness(1, 0.8F, f3, this);
            this.CT.WeaponControl[0] = true;
            if (this.subSkill > 8) if (this.sp >= 0.0F) this.sp -= f * 0.2F;
            else this.sp += f * 0.2F;
            this.bulletDelay += 2;
            if (this.bulletDelay >= 118) {
                int i = (int) (this.target.getW().length() * 130D);
                if (i > 45) i = 45;
                this.bulletDelay += World.Rnd().nextInt(i * (this.Skill + 1), 2 * i * (this.Skill + 1));
            }
            if (this.maneuver == 98) this.gattackCounter++;
            if (f3 < this.convAI + (4 - this.Skill) * 50) {
                this.CT.WeaponControl[1] = true;
                // TODO: Storebror: +++ Bomb Release Bug hunting
//                if (f3 < 100F && this.CT.Weapons[2] != null && this.CT.Weapons[2][0] != null && this.CT.Weapons[2][this.CT.Weapons[2].length - 1].countBullets() != 0 && this.rocketsDelay < 1) {
                if (f3 < 100F && this.CT.hasBulletsLeftOnTrigger(2) && this.rocketsDelay < 1) {
                    // TODO: Storebror: +++ Bomb Release Bug hunting
                    tmpV3f.sub(this.target.Vwld, this.Vwld);
                    this.Or.transformInv(tmpV3f);
                    if (Math.abs(tmpV3f.y) < 4D && Math.abs(tmpV3f.z) < 4D) this.CT.WeaponControl[2] = true;
                    Voice.speakAttackByRockets((Aircraft) this.actor);
                    this.rocketsDelay += 120;
                }
            }
            ((Pilot) this.target).setAsDanger(this.actor);
        }
        this.Vtarg.set(Ve);
        this.Or.transformInv(Ve);
        Ve.normalize();
        ((Maneuver) this.target).incDangerAggressiveness(1, (float) Ve.x, f3, this);
        if (Ve.x < 0.9D) {
            this.Vtarg.z += 0.03F * this.Skill * (800F + f3) * (0.9D - Ve.x);
            Ve.set(this.Vtarg);
            this.Or.transformInv(Ve);
            Ve.normalize();
        }
        this.attackTurn(f3, f, 10F + this.Skill * 8F);
        if (this.sp > 0.0F && this.shootingPoint > this.sp) this.sp += f * 0.05F;
        else if (this.shootingPoint < this.sp) this.sp -= f * 0.05F;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private void turnToDirection(float f) {
        if (Math.abs(Ve.y) > 0.1) this.CT.AileronControl = clamp11(-(float) Math.atan2(Ve.y, Ve.z) - 0.016F * this.Or.getKren());
        else this.CT.AileronControl = clamp11(-(float) Math.atan2(Ve.y, Ve.x) - 0.016F * this.Or.getKren());
        this.CT.RudderControl = clamp11(-10.0F * (float) Math.atan2(Ve.y, Ve.x));
        if (this.CT.RudderControl * this.W.z > 0.0) this.W.z = 0.0;
        else this.W.z *= 1.04;
        float f1 = (float) Math.atan2(Ve.z, Ve.x);
        if (Math.abs(Ve.y) < 0.002 && Math.abs(Ve.z) < 0.002) f1 = 0.0F;
        if (Ve.x < 0.0) f1 = 1.0F;
        else {
            if (f1 * this.W.y > 0.0) this.W.y = 0.0;
            if (f1 < 0.0F) {
                if (this.getOverload() < 0.1F) f1 = 0.0F;
                if (this.CT.ElevatorControl > 0.0F) this.CT.ElevatorControl = 0.0F;
            } else if (this.CT.ElevatorControl < 0.0F) this.CT.ElevatorControl = 0.0F;
        }
        if (this.getOverload() > this.maxG || this.AOA > this.maxAOA || this.CT.ElevatorControl > f1) this.CT.ElevatorControl -= 0.3F * f;
        else this.CT.ElevatorControl += 0.3F * f;
        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
    }

    private void farTurnToDirection() {
        this.farTurnToDirection(4.0F);
    }

    private void farTurnToDirection(float f) {
        Vpl.set(1.0, 0.0, 0.0);
        tmpV3f.cross(Vpl, Ve);
        this.Or.transform(tmpV3f);
        this.CT.RudderControl = clamp11(-10.0F * (float) Math.atan2(Ve.y, Ve.x) + 1.0F * (float) this.W.y);
        float f7 = this.getSpeed() / this.Vmax * 45.0F;
        if (f7 > 85.0F) f7 = 85.0F;
        float f8 = (float) Ve.x;
        if (f8 < 0.0F) f8 = 0.0F;
        float f1;
        if (tmpV3f.z >= 0.0) f1 = -0.02F * (f7 + this.Or.getKren()) * (1.0F - f8) - 0.05F * this.Or.getTangage() + 1.0F * (float) this.W.x;
        else f1 = -0.02F * (-f7 + this.Or.getKren()) * (1.0F - f8) + 0.05F * this.Or.getTangage() + 1.0F * (float) this.W.x;
        float f2 = -(float) Math.atan2(Ve.y, Ve.x) - 0.016F * this.Or.getKren() + 1.0F * (float) this.W.x;
        float f4 = -0.1F * (this.getAOA() - 10.0F) + 0.5F * (float) this.getW().y;
        float f5;
        if (Ve.z > 0.0) f5 = -0.1F * (this.getAOA() - f) + 0.5F * (float) this.getW().y;
        else f5 = 1.0F * (float) Ve.z + 0.5F * (float) this.getW().y;
        if (Math.abs(Ve.y) < 0.002) {
            f2 = 0.0F;
            this.CT.RudderControl = 0.0F;
        }
        tmpV3f.set(Ve);
        this.Or.transform(tmpV3f);
        float f9 = (float) Math.atan2(tmpV3f.y, tmpV3f.x) * 180.0F / (float) Math.PI;
        float f10 = 1.0F - Math.abs(f9 - this.Or.getYaw()) * 0.01F;
        if (f10 < 0.0F || Ve.x < 0.0) f10 = 0.0F;
        float f3 = f10 * f2 + (1.0F - f10) * f1;
        float f6 = f10 * f5 + (1.0F - f10) * f4;
        this.CT.AileronControl = clamp11(f3);
        this.CT.ElevatorControl = clamp11(f6);
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    private void turnBaby(float f, float f1, float f2) {
        if (Ve.x < 0.01D) Ve.x = 0.01D;
        if (this.sub_Man_Count == 0) this.oldVe.set(Ve);
        this.minElevCoeff = 20F;
        this.CT.RudderControl = clamp11((float) (-3D * Math.atan2(Ve.y, Ve.x) + 0.05D * (Ve.y - this.oldVe.y)));
        float f3 = (float) (10D * Math.atan2(Ve.z, Ve.x) + 6D * (Ve.z - this.oldVe.z));
        this.CT.AileronControl = clamp11(-3F * (float) Math.atan2(Ve.y, Ve.x) - 0.006F * this.Or.getKren() + 0.3F * (float) this.W.x);
        if (Math.abs(this.CT.ElevatorControl - f3) < f2 * f1) this.CT.ElevatorControl = clamp11(f3);
        else if (this.CT.ElevatorControl < f3) this.CT.ElevatorControl += f2 * f1;
        else this.CT.ElevatorControl -= 0.2F * f2 * f1;
        this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
        if (this.AOA < this.AOA_Crit * 0.75F && this.AOA > 0.0F) this.nShakeMe(this.flying, this.Skill);
        this.oldVe.set(Ve);
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private void attackTurnToDirection(float f, float f1, float f2) {
        if (Ve.x < 0.01) Ve.x = 0.01;
        if (this.sub_Man_Count == 0) this.oldVe.set(Ve);
        if (Ve.x > 0.95) {
            this.CT.RudderControl = clamp11((float) (-30.0 * Math.atan2(Ve.y, Ve.x) + 1.5 * (Ve.y - this.oldVe.y)));
            float f3;
            if (Ve.z > 0.0 || this.CT.RudderControl > 0.9F) {
                f3 = (float) (10.0 * Math.atan2(Ve.z, Ve.x) + 6.0 * (Ve.z - this.oldVe.z));
                this.CT.AileronControl = clamp11(-30.0F * (float) Math.atan2(Ve.y, Ve.x) - 0.02F * this.Or.getKren() + 5.0F * (float) this.W.x);
            } else {
                f3 = (float) (5.0 * Math.atan2(Ve.z, Ve.x) + 6.0 * (Ve.z - this.oldVe.z));
                this.CT.AileronControl = clamp11(-5.0F * (float) Math.atan2(Ve.y, Ve.x) - 0.02F * this.Or.getKren() + 5.0F * (float) this.W.x);
            }
            if (Ve.x > 1.0F - 0.0050F * this.Skill) {
                tmpOr.set(this.Or);
                tmpOr.increment((float) Math.toDegrees(Math.atan2(Ve.y, Ve.x)), (float) Math.toDegrees(Math.atan2(Ve.z, Ve.x)), 0.0F);
                this.Or.interpolate(tmpOr, 0.1F);
            }
            if (Math.abs(this.CT.ElevatorControl - f3) < f2 * f1) this.CT.ElevatorControl = clamp11(f3);
            else if (this.CT.ElevatorControl < f3) this.CT.ElevatorControl += f2 * f1;
            else this.CT.ElevatorControl -= f2 * f1;
            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
        } else {
            if (this.Skill >= 2 && Ve.z > 0.5 && f < 600.0F) this.CT.FlapsControl = 0.1F;
            else this.CT.FlapsControl = 0.0F;
            float f5 = 0.6F - (float) Ve.z;
            if (f5 < 0.0F) f5 = 0.0F;
            this.CT.RudderControl = clamp11((float) (-30.0 * Math.atan2(Ve.y, Ve.x) * f5 + 1.0 * (Ve.y - this.oldVe.y) * Ve.x + 0.5 * this.W.z));
            float f4;
            if (Ve.z > 0.0) {
                f4 = (float) (10.0 * Math.atan2(Ve.z, Ve.x) + 6.0 * (Ve.z - this.oldVe.z) + 0.5 * (float) this.W.y);
                if (f4 < 0.0F) f4 = 0.0F;
                this.CT.AileronControl = clamp11((float) (-20.0 * Math.atan2(Ve.y, Ve.z) - 0.05 * this.Or.getKren() + 5.0 * this.W.x));
            } else {
                f4 = (float) (-5.0 * Math.atan2(Ve.z, Ve.x) + 6.0 * (Ve.z - this.oldVe.z) + 0.5 * (float) this.W.y);
                this.CT.AileronControl = clamp11((float) (-20.0 * Math.atan2(Ve.y, Ve.z) - 0.05 * this.Or.getKren() + 5.0 * this.W.x));
            }
            if (f4 < 0.0F) f4 = 0.0F;
            if (Math.abs(this.CT.ElevatorControl - f4) < f2 * f1) this.CT.ElevatorControl = clamp11(f4);
            else if (this.CT.ElevatorControl < f4) this.CT.ElevatorControl += 0.3F * f1;
            else this.CT.ElevatorControl -= 0.3F * f1;
            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
        }
        float f6 = 0.054F * (600.0F - f);
        if (f6 < 4.0F) f6 = 4.0F;
        if (f6 > this.AOA_Crit) f6 = this.AOA_Crit;
        if (this.AOA > f6 - 1.5F) this.Or.increment(0.0F, 0.16F * (f6 - 1.5F - this.AOA), 0.0F);
        if (this.AOA < -5.0F) this.Or.increment(0.0F, 0.12F * (-5.0F - this.AOA), 0.0F);
        this.oldVe.set(Ve);
        this.sub_Man_Count++;
        this.W.x *= 0.95;
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    private void attackTurn(float f, float f1, float f2) {
        if (Ve.x < 0.01D && this.subSkill > 6 && this.maneuver == 27 && f < 800F && this.getMinHeightTurn(100F) < ((Maneuver) this.target).getMinHeightTurn(50F) + this.Skill * 0.33F) this.set_maneuver(ATTACK_HARD);
        else if (Ve.x < 0.01D) Ve.x = 0.01D;
        if (this.sub_Man_Count == 0) this.oldVe.set(Ve);
        if (Ve.x > 0.95D) {
            this.CT.RudderControl = clamp11((float) (-10D * Math.atan2(Ve.y, Ve.x) + 1.5D * (Ve.y - this.oldVe.y)));
            float f3;
            float f5;
            if (Ve.z > 0.0D || this.CT.RudderControl > 0.9F) {
                f3 = (float) (10D * Math.atan2(Ve.z, Ve.x) + 6D * (Ve.z - this.oldVe.z));
                f5 = -10F * (float) Math.atan2(Ve.y, Ve.x);
            } else {
                f3 = (float) (5D * Math.atan2(Ve.z, Ve.x) + 6D * (Ve.z - this.oldVe.z));
                f5 = -5F * (float) Math.atan2(Ve.y, Ve.x) - 0.02F * this.Or.getKren() + 5F * (float) this.W.x;
            }
            this.CT.AileronControl = clamp11(f5);
            if (Ve.x > 1.0F - 0.005F * this.Skill) {
                tmpOr.set(this.Or);
                tmpOr.increment((float) Math.toDegrees(Math.atan2(Ve.y, Ve.x)), (float) Math.toDegrees(Math.atan2(Ve.z, Ve.x)), 0.0F);
                this.Or.interpolate(tmpOr, 0.1F);
            }
            if (Math.abs(this.CT.ElevatorControl - f3) < f2 * f1) this.CT.ElevatorControl = clamp11(f3);
            else if (this.CT.ElevatorControl < f3) this.CT.ElevatorControl += f2 * f1;
            else this.CT.ElevatorControl -= f2 * f1;
            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
        } else {
            if (this.Skill >= 2 && Ve.z > 0.5D && f < 600F) this.CT.FlapsControl = 0.1F;
            else this.CT.FlapsControl = 0.0F;
            float f6 = 0.6F - (float) Ve.z;
            if (f6 < 0.0F) f6 = 0.0F;
            this.CT.RudderControl = clamp11((float) (-30D * Math.atan2(Ve.y, Ve.x) * f6 + 1.0D * (Ve.y - this.oldVe.y) * Ve.x + 0.5D * this.W.z));
            float f4;
            if (Ve.z > 0.0D) {
                f4 = (float) (10D * Math.atan2(Ve.z, Ve.x) + 6D * (Ve.z - this.oldVe.z) + 0.5D * (float) this.W.y);
                if (f4 < 0.0F) f4 = 0.0F;
                this.CT.AileronControl = clamp11((float) (-20D * Math.atan2(Ve.y, Ve.z) - 0.05D * this.Or.getKren() + 5D * this.W.x));
            } else {
                f4 = (float) (-5D * Math.atan2(Ve.z, Ve.x) + 6D * (Ve.z - this.oldVe.z) + 0.5D * (float) this.W.y);
                this.CT.AileronControl = clamp11((float) (-20D * Math.atan2(Ve.y, Ve.z) - 0.05D * this.Or.getKren() + 5D * this.W.x));
            }
            if (f4 < 0.0F) f4 = 0.0F;
            if (Math.abs(this.CT.ElevatorControl - f4) < f2 * f1) this.CT.ElevatorControl = clamp11(f4);
            else if (this.CT.ElevatorControl < f4) this.CT.ElevatorControl += 0.3F * f1;
            else this.CT.ElevatorControl -= 0.3F * f1;
            this.CT.ElevatorControl = clamp11(this.CT.ElevatorControl);
        }
        float f7 = 0.054F * (600F - f);
        if (f7 < 4F) f7 = 4F;
        if (f7 > this.AOA_Crit) f7 = this.AOA_Crit;
        if (this.AOA > f7 - 1.5F) this.Or.increment(0.0F, 0.16F * (f7 - 1.5F - this.AOA), 0.0F);
        if (this.AOA < -5F) this.Or.increment(0.0F, 0.12F * (-5F - this.AOA), 0.0F);
        this.oldVe.set(Ve);
        this.oldAOA = this.AOA;
        this.sub_Man_Count++;
        this.W.x *= 0.95D;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private void doCheckStrike() {
        // TODO: +++ Avoid mid air collision - by SAS~Storebror +++
// if ((this.M.massEmpty > 5000.0F) && (!this.AP.way.isLanding())) {
// return;
// }
        if (this.M.massEmpty > this.strikeTarg.M.massEmpty * 1.5F && !this.AP.way.isLanding()) return;
        // TODO: --- Avoid mid air collision - by SAS~Storebror ---
        if (this.maneuver == 24 && this.strikeTarg == this.Leader) return;
        if (this.actor instanceof TypeDockable && ((TypeDockable) this.actor).typeDockableIsDocked()) return;
        Vpl.sub(this.strikeTarg.Loc, this.Loc);
        tmpV3f.set(Vpl);
        this.Or.transformInv(tmpV3f);
        if (!(tmpV3f.x < 0.0)) {
            tmpV3f.sub(this.strikeTarg.Vwld, this.Vwld);
            tmpV3f.scale(0.7);
            Vpl.add(tmpV3f);
            this.Or.transformInv(Vpl);
            this.push();
            // TODO: +++ Avoid mid air collision - by SAS~Storebror +++
            // TODO: +++ TD AI code backport from 4.13 +++
// if (Vpl.z > 0.0D && this.Alt > 50F)
// TODO: --- TD AI code backport from 4.13 ---
            if (Vpl.z > 10.0D && this.Alt > 300F && World.Rnd().nextFloat() > 0.8F)
                // TODO: --- Avoid mid air collision - by SAS~Storebror ---
                this.push(EVADE_DN);
            else this.push(EVADE_UP);
            this.safe_pop();
        }
    }

    public void setStrikeEmer(FlightModel flightmodel) {
        this.strikeEmer = true;
        this.strikeTarg = flightmodel;
    }

    protected void wingman(boolean flag) {
        if (this.Wingman != null) this.Wingman.Leader = flag ? this : null;
    }

    public float getMnTime() {
        return this.mn_time;
    }

    public void doDumpBombsPassively() {
        boolean flag = false;
        for (int i = 0; i < this.CT.Weapons.length; i++)
            if (this.CT.Weapons[i] != null && this.CT.Weapons[i].length > 0) for (int j = 0; j < this.CT.Weapons[i].length; j++)
                if (this.CT.Weapons[i][j] instanceof BombGun) if (this.CT.Weapons[i][j] instanceof BombGunPara || this.actor instanceof SM79 && this.CT.Weapons[i][j] instanceof TorpedoGun) flag = true;
                else {
                    ((BombGun) this.CT.Weapons[i][j]).setBombDelay(3.3E7F);
                    if (this.actor == World.getPlayerAircraft() && !World.cur().diffCur.Limited_Ammo) this.CT.Weapons[i][j].loadBullets(0);
                    // TODO: +++ TD AI code backport from 4.13 +++
                    // CT.dropExternalStores(true); // would be 4.13 code...
                    // break;
                    // TODO: --- TD AI code backport from 4.13 ---
                }
        if (!flag) this.bombsOut = true;
        CT.dropExternalStores(true); // 4.13 code...
    }

    protected void printDebugFM() {
        // TODO: +++ TD AI code backport from 4.13 +++
// System.out.print("<" + this.actor.name() + "> " + ManString.tname(this.task) + ":" + ManString.name(this.maneuver) + (this.target == null ? "t" : "T") + (this.danger == null ? "d" : "D") + (this.target_ground == null ? "g " : "G "));
        String s = ((Aircraft) this.actor).typedName();
        System.out.print("<" + this.actor.name() + "> (" + s + ") " + ManString.tname(this.task) + ":" + ManString.name(this.maneuver) + (this.target == null ? " t" : " T") + (this.danger == null ? "d" : "D") + (this.airClient == null ? "c" : "C")
                + (this.target_ground == null ? "g" : "G") + (this.targetLost ? "L " : "l "));
        HUD.training("<" + this.actor.name() + "> " + s + " " + ManString.tname(this.task) + ":" + ManString.name(this.maneuver) + (this.target == null ? " t" : " T") + (this.danger == null ? "d" : "D") + (this.airClient == null ? "c" : "C")
                + (this.target_ground == null ? "g" : "G") + (this.targetLost ? "L " : "l "));
        // TODO: --- TD AI code backport from 4.13 ---
//        switch (this.maneuver) {
//            case 21:
//                System.out.println(": WP=" + this.AP.way.Cur() + "(" + (this.AP.way.size() - 1) + ")-" + ManString.wpname(this.AP.way.curr().Action));
//                if (this.target_ground != null) System.out.println(" GT=" + this.target_ground.getClass().getName() + "(" + this.target_ground.name() + ")");
//                break;
//            case 27:
//                if (this.target == null || !Actor.isValid(this.target.actor)) System.out.println(" T=null");
//                else System.out.println(" T=" + this.target.actor.getClass().getName() + "(" + this.target.actor.name() + ")");
//                break;
//            case 43:
//            case 50:
//            case 51:
//            case 52:
//                if (this.target_ground == null || !Actor.isValid(this.target_ground)) System.out.println(" GT=null");
//                else System.out.println(" GT=" + this.target_ground.getClass().getName() + "(" + this.target_ground.name() + ")");
//                break;
//            default:
//                System.out.println("");
//                if (this.target == null || !Actor.isValid(this.target.actor)) System.out.println(" T=null");
//                else {
//                    System.out.println(" T=" + this.target.actor.getClass().getName() + "(" + this.target.actor.name() + ")");
//                    if (this.target_ground == null || !Actor.isValid(this.target_ground))
//                    // TODO: +++ TD AI code backport from 4.13 +++
//                    {
//                        System.out.println(" GT=null");
//                        break;
//                    }
//                    System.out.println(" GT=" + this.target_ground.getClass().getName() + "(" + this.target_ground.name() + ")");
//                    if (this.airClient == null || !Actor.isValid(this.airClient.actor)) System.out.println(" Client=null");
//                    else System.out.println(" Client=" + this.airClient.getClass().getName() + "(" + this.airClient.actor.name() + ")");
//                    // TODO: --- TD AI code backport from 4.13 ---
//                }
//        }
    }

    protected void headTurn(float f) {
        boolean flag = false;
        switch (this.get_task()) {
            case Maneuver.STAY_FORMATION:
                if (this.Leader != null) {
                    Maneuver.Ve.set(this.Leader.Loc);
                    flag = true;
                }
                break;
            case Maneuver.DEFENCE:
                if (this.danger != null) {
                    Maneuver.Ve.set(this.danger.Loc);
                    flag = true;
                }
                break;
            case Maneuver.DEFENDING:
                if (this.airClient != null) {
                    Maneuver.Ve.set(this.airClient.Loc);
                    flag = true;
                }
                break;
            case Maneuver.ATTACK_AIR:
                if (this.target != null) {
                    Maneuver.Ve.set(this.target.Loc);
                    flag = true;
                }
                break;
            case Maneuver.ATTACK_GROUND:
                if (this.target_ground != null) {
                    Maneuver.Ve.set(this.target_ground.pos.getAbsPoint());
                    flag = true;
                }
                break;
        }
        float f1;
        float f2;
        if (flag) {
            Maneuver.Ve.sub(this.Loc);
            this.Or.transformInv(Maneuver.Ve);
            tmpOr.setAT0(Maneuver.Ve);
            f1 = tmpOr.getTangage();
            f2 = tmpOr.getYaw();
            if (f2 > 75.0F) f2 = 75.0F;
            if (f2 < -75.0F) f2 = -75.0F;
            if (f1 < -15.0F) f1 = -15.0F;
            if (f1 > 40.0F) f1 = 40.0F;
        } else {
            switch (this.maneuver) {
                case Maneuver.FISHTAIL_LEFT:
                case Maneuver.LOOKDOWN_LEFT:
                    f1 = 0F;
                    f2 = 75F;
                    break;
                case Maneuver.FISHTAIL_RIGHT:
                case Maneuver.LOOKDOWN_RIGHT:
                    f1 = 0F;
                    f2 = -75F;
                    break;
                case Maneuver.PILOT_DEAD:
                    f2 = -15F;
                    f1 = -15F;
                    break;
                default:
                    f1 = 0.0F;
                    f2 = 0.0F;
                    break;
            }
        }
        if (Math.abs(this.pilotHeadT - f1) > 3.0F) this.pilotHeadT = this.pilotHeadT + 90.0F * (this.pilotHeadT <= f1 ? 1.0F : -1.0F) * f;
        else this.pilotHeadT = f1;
        if (Math.abs(this.pilotHeadY - f2) > 2.0F) this.pilotHeadY = this.pilotHeadY + 60.0F * (this.pilotHeadY <= f2 ? 1.0F : -1.0F) * f;
        else this.pilotHeadY = f2;
        if (actor instanceof NetAircraft && ((NetAircraft)actor).FM.AS.isMaster()) {
            ((NetAircraft)actor).setHeadAngles(this.pilotHeadY, this.pilotHeadT);
        }
    }

//    protected void headTurn(float f) {
//        if (this.actor == Main3D.cur3D().viewActor() && this.AS.astatePilotStates[0] < 90) {
//            boolean flag = false;
//            switch (this.get_task()) {
//                case 2:
//                    if (this.Leader != null) {
//                        Ve.set(this.Leader.Loc);
//                        flag = true;
//                    }
//                    break;
//                case 6:
//                    if (this.target != null) {
//                        Ve.set(this.target.Loc);
//                        flag = true;
//                    }
//                    break;
//                case 5:
//                    if (this.airClient != null) {
//                        Ve.set(this.airClient.Loc);
//                        flag = true;
//                    }
//                    break;
//                case 4:
//                    if (this.danger != null) {
//                        Ve.set(this.danger.Loc);
//                        flag = true;
//                    }
//                    break;
//                case 7:
//                    if (this.target_ground != null) {
//                        Ve.set(this.target_ground.pos.getAbsPoint());
//                        flag = true;
//                    }
//                    break;
//            }
//            float f1;
//            float f2;
//            if (flag) {
//                Ve.sub(this.Loc);
//                this.Or.transformInv(Ve);
//                tmpOr.setAT0(Ve);
//                f1 = tmpOr.getTangage();
//                f2 = tmpOr.getYaw();
//                // TODO: +++ TD AI code backport from 4.13 +++
//                if (f2 > 107F) f2 = 107F;
//                if (f2 < -107F) f2 = -107F;
//                if (f1 < -22F) f1 = -22F;
//                if (f1 > 57F) f1 = 57F;
//            } else if (this.maneuver == 88) {
//                f1 = 0.0F;
//                f2 = 107F;
//            } else if (this.maneuver == 89) {
//                f1 = 0.0F;
//                f2 = -107F;
//            } else if (this.maneuver == 90) {
//                f1 = 0.0F;
//                f2 = 90F;
//            } else if (this.maneuver == 91) {
//                f1 = 0.0F;
//                f2 = -90F;
//            } else if (this.get_maneuver() == 44) {
//                f2 = -15F;
//                f1 = -15F;
//            } else {
//                f1 = 0.0F;
//                f2 = 0.0F;
//            }
//            if (Math.abs(this.pilotHeadT - f1) > 3F) this.pilotHeadT += 90F * (this.pilotHeadT <= f1 ? 1.0F : -1F) * f;
//            else this.pilotHeadT = f1;
//            if (Math.abs(this.pilotHeadY - f2) > 2.0F) this.pilotHeadY += 60F * (this.pilotHeadY <= f2 ? 1.0F : -1F) * f;
//            else this.pilotHeadY = f2;
//            ((NetAircraft)actor).setHeadAngles(pilotHeadY, pilotHeadT);  
////                // ((NetAircraft)actor).setHeadAngles(pilotHeadY, pilotHeadT); // would be 4.13 code...
////                tmpOr.setYPR(0.0F, 0.0F, 0.0F);
////            tmpOr.increment(0.0F, this.pilotHeadY, 0.0F);
////            tmpOr.increment(this.pilotHeadT, 0.0F, 0.0F);
////            tmpOr.increment(0.0F, 0.0F, -0.2F * this.pilotHeadT + 0.05F * this.pilotHeadY);
////            this.headOr[0] = tmpOr.getYaw();
////            this.headOr[1] = tmpOr.getPitch();
////            this.headOr[2] = tmpOr.getRoll();
////            this.headPos[0] = 5.0E-4F * Math.abs(this.pilotHeadY);
////            this.headPos[1] = -1.0E-4F * Math.abs(this.pilotHeadY);
////            this.headPos[2] = 0.0F;
////            ((ActorHMesh) this.actor).hierMesh().chunkSetLocate("Head1_D0", this.headPos, this.headOr);
//            // TODO: --- TD AI code backport from 4.13 ---
//        }
//    }

    protected void turnOffTheWeapon() {
        this.CT.WeaponControl[0] = false;
        this.CT.WeaponControl[1] = false;
        this.CT.WeaponControl[2] = false;
        this.CT.WeaponControl[3] = false;
        if (this.bombsOut) {
            // TODO: Storebror: +++ Bomb Release Bug hunting
            if (!this.CT.isBombReleaseReady()) return;
            // TODO: Storebror: --- Bomb Release Bug hunting
            this.bombsOutCounter++;
            if (this.bombsOutCounter > 128) {
                this.bombsOutCounter = 0;
                this.bombsOut = false;
            }
            if (this.CT.Weapons[3] != null) this.CT.WeaponControl[3] = true;
            else this.bombsOut = false;
            // TODO: Storebror: +++ Bomb Release Bug hunting
//            if (this.CT.Weapons[3] != null && this.CT.Weapons[3][0] != null) {
            if (this.CT.firstGunOnTrigger(3) != null) {
                // TODO: Storebror: --- Bomb Release Bug hunting
                int i = 0;
                for (int j = 0; j < this.CT.Weapons[3].length; j++)
                    i += this.CT.Weapons[3][j].countBullets();
                if (i == 0) {
                    this.bombsOut = false;
                    for (int k = 0; k < this.CT.Weapons[3].length; k++)
                        this.CT.Weapons[3][k].loadBullets(0);
                }
            }
        }
    }

    protected void turnOnCloudShine(boolean flag) {
        if (flag) {
            if (World.Sun().ToSun.z < -0.22F) this.AS.setLandingLightState(true);
        } else this.AS.setLandingLightState(false);
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    private boolean doGroundCheckFront(float f) {
        if (this.maneuver == 2 && (this.Or.getPitch() < 360F || this.Vrel.z < 0.0D)) return false;
        if (this.kamikaze && (this.maneuver == NONE || this.maneuver == DIVING_30_DEG || this.maneuver == DIVING_45_DEG || this.maneuver == GATTACK_KAMIKAZE)) return false; // TODO: Added by SAS~Storebror: Keep Kamikaze Aircraft from pulling up on terminal dive
        float f1 = (float) Math.sqrt(this.Vrel.x * this.Vrel.x + this.Vrel.y * this.Vrel.y);
        float f2 = (float) (this.getW().z * 2000D);
        if (f2 < -500F) f2 = -500F;
        if (f2 > 500F) f2 = 500F;
        float f3 = 1.0F;
        if (this.task == 24) f3 = 0.5F;
        else if (this.task == 3) f3 = 1.5F;
        float f4 = (f1 * 20F * f - Math.abs(f2)) * f3;
        if (f4 < 800F) f4 = 800F;
        if (this.maneuver == 21) {
            float f5 = this.AP.getWayPointDistance();
            f4 = Math.min(f4, f5);
        }
        if (f4 > 2500F) f4 = 2500F;
        Po.set(this.Loc);
        float f6 = -10F;
        if (this.Vrel.z > 0.0D) f6 = (float) (f6 - this.Vrel.z);
        if (f6 < -30F) f6 = -30F;
        if (this.target != null) {
            this.tempV.sub(this.Loc, this.target.Loc);
            this.Or.transformInv(this.tempV);
            this.tempV.normalize();
            if (this.tempV.x < -0.97D) {
                f4 = 50F;
                f6 = 0.0F;
            }
        }
        Vpl.set(f4, f2, f6);
        float f7 = this.Or.getPitch();
        if (f7 < 360F) f7 = 360F;
        tmpOr.setYPR(this.Or.getYaw(), f7, 0.0F);
        tmpOr.transform(Vpl);
        Po.add(Vpl);
        Pd.set(Po);
        if (Landscape.rayHitHQ(this.actor.pos.getAbsPoint(), Pd, this.tempPoint)) {
            this.push();
            this.push(84);
            this.pop();
            return true;
        } else return false;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    protected void doCheckGround(float f) {
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.Gears.getWheelsOnGround() || this.Gears.isUnderDeck()) {
            this.setCheckGround(false);
            this.pop();
            return;
        }
        // TODO: --- TD AI code backport from 4.13 ---
        if (this.CT.AileronControl > 1.0F) this.CT.AileronControl = 1.0F;
        if (this.CT.AileronControl < -1.0F) this.CT.AileronControl = -1.0F;
        if (this.CT.ElevatorControl > 1.0F) this.CT.ElevatorControl = 1.0F;
        if (this.CT.ElevatorControl < -1.0F) this.CT.ElevatorControl = -1.0F;
        // TODO: +++ TD AI code backport from 4.13 +++
        float f4 = 0.0003F * this.M.getFullMass();
        // TODO: --- TD AI code backport from 4.13 ---
        float f5 = 10.0F;
        float f6 = 80.0F;
        if (this.maneuver == 24) {
            f5 = 15.0F;
            f6 = 120.0F;
        }
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.Alt < 2000F && this.isTick(16, 0)) if (this.doGroundCheckFront(f4)) return;
        if (this.maneuver == 57 || this.maneuver == 2) return;
        // TODO: --- TD AI code backport from 4.13 ---
        float f7 = (float) -this.Vwld.z * f5 * f4;
        float f8 = 1.0F + 7.0F * ((this.indSpeed - this.VmaxAllowed) / this.VmaxAllowed);
        if (f8 > 1.0F) f8 = 1.0F;
        if (f8 < 0.0F) f8 = 0.0F;
        float f1;
        float f2;
        float f3;
        if (f7 > this.Alt || this.Alt < f6 || f8 > 0.0F) {
            if (this.Alt < 0.0010F) this.Alt = 0.0010F;
            f1 = (f7 - this.Alt) / this.Alt;
            Math.max(0.01667 * (f6 - this.Alt), f1);
            if (f1 > 1.0F) f1 = 1.0F;
            if (f1 < 0.0F) f1 = 0.0F;
            if (f1 < f8) f1 = f8;
            f2 = -0.12F * (this.Or.getTangage() - 5.0F) + 3.0F * (float) this.W.y;
            f3 = -0.07F * this.Or.getKren() + 3.0F * (float) this.W.x;
            if (f3 > 2.0F) f3 = 2.0F;
            if (f3 < -2.0F) f3 = -2.0F;
            if (f2 > 2.0F) f2 = 2.0F;
            if (f2 < -2.0F) f2 = -2.0F;
        } else {
            f1 = 0.0F;
            f2 = 0.0F;
            f3 = 0.0F;
        }
        float f9 = 0.2F;
        if (this.corrCoeff < f1) this.corrCoeff = f1;
        if (this.corrCoeff > f1) this.corrCoeff *= 1.0 - f9 * f;
        if (f2 < 0.0F) {
            if (this.corrElev > f2) this.corrElev = f2;
            if (this.corrElev < f2) this.corrElev *= 1.0 - f9 * f;
        } else {
            if (this.corrElev < f2) this.corrElev = f2;
            if (this.corrElev > f2) this.corrElev *= 1.0 - f9 * f;
        }
        if (f3 < 0.0F) {
            if (this.corrAile > f3) this.corrAile = f3;
            if (this.corrAile < f3) this.corrAile *= 1.0 - f9 * f;
        } else {
            if (this.corrAile < f3) this.corrAile = f3;
            if (this.corrAile > f3) this.corrAile *= 1.0 - f9 * f;
        }
        this.CT.AileronControl = clamp11(this.corrCoeff * this.corrAile + (1.0F - this.corrCoeff) * this.CT.AileronControl);
        this.CT.ElevatorControl = clamp11(this.corrCoeff * this.corrElev + (1.0F - this.corrCoeff) * this.CT.ElevatorControl);
        if (this.Alt < 15.0F && this.Vwld.z < 0.0) this.Vwld.z *= 0.85;
        // TODO: +++ TD AI code backport from 4.13 +++
        if (-this.Vwld.z * 2.5D > this.Alt || this.Alt < 20F / (this.Skill + 1)) {
            if (this.maneuver == 27 || this.maneuver == 43 || this.maneuver == 21 || this.maneuver == 24 || this.maneuver == 68 || this.maneuver == 53 || this.maneuver == 65 || this.maneuver == 76 || this.maneuver == 74 || this.maneuver == 75) // TODO:
                                                                                                                                                                                                                                                    // --- TD
                                                                                                                                                                                                                                                    // AI code
                                                                                                                                                                                                                                                    // backport
                                                                                                                                                                                                                                                    // from
                                                                                                                                                                                                                                                    // 4.13
                                                                                                                                                                                                                                                    // ---
                this.push();
            this.push(2);
            this.pop();
            this.submaneuver = SUB_MAN0;
            this.sub_Man_Count = 0;
        }
        this.W.x *= 0.95;
    }

    protected void setSpeedControl(float f) {
        float f1 = 0.8F;
        float f4 = 0.02F;
        float f5 = 1.5F;
        this.CT.setAfterburnerControl(false);
        // TODO: +++ TD AI code backport from 4.13 +++
        float f142 = -0.05F;
        boolean flag = false;
        this.CT.setManualfterburnerControl(0.0F);
        // TODO: --- TD AI code backport from 4.13 ---
        switch (this.speedMode) {
            case 1:
                if (this.tailForStaying == null) f1 = 1.0F;
                else {
                    Ve.sub(this.tailForStaying.Loc, this.Loc);
                    this.tailForStaying.Or.transform(this.tailOffset, tmpV3f);
                    Ve.add(tmpV3f);
                    float f14 = (float) Ve.z;
                    float f6 = 0.0050F * (200.0F - (float) Ve.length());
                    this.Or.transformInv(Ve);
                    float f10 = (float) Ve.x;
                    Ve.normalize();
                    f6 = Math.max(f6, (float) Ve.x);
                    if (f6 < 0.0F) f6 = 0.0F;
                    Ve.set(this.Vwld);
                    Ve.normalize();
                    tmpV3f.set(this.tailForStaying.Vwld);
                    tmpV3f.normalize();
                    float f8 = (float) tmpV3f.dot(Ve);
                    if (f8 < 0.0F) f8 = 0.0F;
                    f6 *= f8;
                    if (f6 > 0.0F && f10 < 1000.0F) {
                        if (f10 > 300.0F) f10 = 300.0F;
                        float f17 = 0.6F;
                        if (this.tailForStaying.VmaxH == this.VmaxH) f17 = this.tailForStaying.CT.getPower();
                        if (this.Vmax < 83.0F) {
                            float f19 = f14;
                            if (f19 > 0.0F) {
                                if (f19 > 20.0F) f19 = 20.0F;
                                this.Vwld.z += 0.01F * f19;
                            }
                        }
                        float f12;
                        if (f10 > 0.0F) f12 = 0.0070F * f10 + 0.0050F * f14;
                        else f12 = 0.03F * f10 + 0.0010F * f14;
                        float f20 = this.getSpeed() - this.tailForStaying.getSpeed();
                        float f22 = -0.3F * f20;
                        float f25 = -3.0F * (this.getForwAccel() - this.tailForStaying.getForwAccel());
                        if (f20 > 27.0F) {
                            this.CT.FlapsControl = 1.0F;
                            f1 = 0.0F;
                        } else {
                            this.CT.FlapsControl = clamp11(0.02F * f20 + 0.02F * -f10);
                            f1 = f17 + f12 + f22 + f25;
                        }
                    } else f1 = 1.1F;
                }
                break;
            case 2:
                f1 = (float) (1.0 - 8.0E-5 * (0.5 * this.Vwld.lengthSquared() - 9.8 * Ve.z - 0.5 * this.tailForStaying.Vwld.lengthSquared()));
                break;
            case 3: {
                // TODO: +++ TD AI code backport from 4.13 +++
                f142 = -0.1F;
                // TODO: --- TD AI code backport from 4.13 ---
                f1 = this.CT.PowerControl;
                if (this.AP.way.curr().Speed < 10.0F) this.AP.way.curr().set(1.7F * this.Vmin);
                float f18 = this.AP.way.curr().Speed / this.VmaxH;
                f1 = 0.2F + 0.8F * (float) Math.pow(f18, 1.5);
                f1 += 0.1F * (this.AP.way.curr().Speed - Pitot.Indicator((float) this.Loc.z, this.getSpeed())) - 3.0F * this.getForwAccel();
                // TODO: +++ TD AI code backport from 4.13 +++
                if (this.bSlowDown && f1 > 0.7F) f1 = 0.7F;
                // TODO: --- TD AI code backport from 4.13 ---
                if (this.getAltitude() < this.AP.way.curr().z() - 70.0F) f1 += this.AP.way.curr().z() - 70.0F - this.getAltitude();
                if (f1 > 0.93F) f1 = 0.93F;
                if (f1 < 0.35F && !this.AP.way.isLanding()) f1 = 0.35F;
                break;
            }
            case 4:
                f1 = this.CT.PowerControl;
                f1 += (f4 * (this.smConstSpeed - Pitot.Indicator((float) this.Loc.z, this.getSpeed())) - f5 * this.getLocalAccel().x / 9.81) * f;
                if (f1 > 1.0F) f1 = 1.0F;
                break;
            case 5:
                f1 = this.CT.PowerControl;
                this.CT.FlapsControl = 1.0F;
                f1 += (f4 * (1.3F * this.VminFLAPS - Pitot.Indicator((float) this.Loc.z, this.getSpeed())) - f5 * this.getForwAccel()) * f;
                break;
            case 8:
                f1 = 0.0F;
                break;
            case 6:
                // TODO: +++ TD AI code backport from 4.13 +++
                f142 = 0.0F;
                // TODO: --- TD AI code backport from 4.13 ---
                f1 = 1.0F;
                break;
            case 9:
                f1 = 1.1F;
                // TODO: +++ TD AI code backport from 4.13 +++
                this.doSetBoost();
                f142 = 0.03F;
                // TODO: --- TD AI code backport from 4.13 ---
                break;
            // TODO: +++ TD AI code backport from 4.13 +++
            case 11:
                f1 = 1.1F;
                this.doSetBoost();
                f142 = 0.04F;
                flag = true;
                break;
            // TODO: --- TD AI code backport from 4.13 ---
            case 7:
                f1 = this.smConstPower;
                break;
            case 10:
                if (this.tailForStaying == null) f1 = 1.0F;
                else {
                    Ve.sub(this.tailForStaying.Loc, this.Loc);
                    this.tailForStaying.Or.transform(this.tailOffset, tmpV3f);
                    Ve.add(tmpV3f);
                    float f15 = (float) Ve.z;
                    float f7 = 0.0050F * (200.0F - (float) Ve.length());
                    this.Or.transformInv(Ve);
                    float f11 = (float) Ve.x;
                    Ve.normalize();
                    f7 = Math.max(f7, (float) Ve.x);
                    if (f7 < 0.0F) f7 = 0.0F;
                    Ve.set(this.Vwld);
                    Ve.normalize();
                    tmpV3f.set(this.tailForStaying.Vwld);
                    tmpV3f.normalize();
                    float f9 = (float) tmpV3f.dot(Ve);
                    if (f9 < 0.0F) f9 = 0.0F;
                    f7 *= f9;
                    if (f7 > 0.0F && f11 < 1000.0F) {
                        if (f11 > 300.0F) f11 = 300.0F;
                        float f21 = 0.6F;
                        if (this.tailForStaying.VmaxH == this.VmaxH) f21 = this.tailForStaying.CT.getPower();
                        if (this.Vmax < 83.0F) {
                            float f23 = f15;
                            if (f23 > 0.0F) {
                                if (f23 > 20.0F) f23 = 20.0F;
                                this.Vwld.z += 0.01F * f23;
                            }
                        }
                        float f13;
                        if (f11 > 0.0F) f13 = 0.0070F * f11 + 0.0050F * f15;
                        else f13 = 0.03F * f11 + 0.0010F * f15;
                        float f24 = this.getSpeed() - this.tailForStaying.getSpeed();
                        float f26 = -0.3F * f24;
                        float f27 = -3.0F * (this.getForwAccel() - this.tailForStaying.getForwAccel());
                        if (f24 > 27.0F) {
                            this.Vwld.scale(0.98);
                            f1 = 0.0F;
                        } else {
                            float f28 = 1.0F - 0.02F * (0.02F * f24 + 0.02F * -f11);
                            if (f28 < 0.98F) f28 = 0.98F;
                            if (f28 > 1.0F) f28 = 1.0F;
                            this.Vwld.scale(f28);
                            f1 = f21 + f13 + f26 + f27;
                        }
                    } else f1 = 1.1F;
                }
                break;
            default:
                return;
        }
        // TODO: +++ TD AI code backport from 4.13 +++
        if (this.AS.isEnginesOnFire()) f1 = (float) (f1 * 0.6D);
        // TODO: --- TD AI code backport from 4.13 ---
        if (f1 > 1.1F) f1 = 1.1F;
        else if (f1 < 0.0F) f1 = 0.0F;
        // TODO: +++ TD AI code backport from 4.13 +++
        float f22 = this.EI.getAIOverheatFactor();
        float f26 = f22 * 15F;
        int c = 0x800;
        if (this.Skill >= 2) c = 0x400;
        if (f22 > f142) {
            if (f22 > 0.0F && this.speedMode != 10) f1 *= 1.0F - f26;
            if (!this.EI.isSelectionAllowsAutoRadiator()) {
                float f30 = 0.0F;
                if (f22 > 0.0F) f30 = 0.1F + f26 * 5F;
                else f30 = 0.1F;
                if (f30 > this.CT.getRadiatorControl()) c = 0x20;
                if (this.isTick(c, 0)) this.CT.setRadiatorControl(f30);
            }
            if (f1 > 1.1F) f1 = 1.1F;
            else if (f1 < 0.0F) f1 = 0.0F;
        } else if (!this.EI.isSelectionAllowsAutoRadiator() && this.CT.getRadiatorControl() > 0.0F && (flag || this.isTick(c, 0))) this.CT.setRadiatorControl(0.0F);
        // TODO: --- TD AI code backport from 4.13 ---
        if (Math.abs(this.CT.PowerControl - f1) < 0.5 * f) this.CT.setPowerControl(f1);
        else if (this.CT.PowerControl < f1) this.CT.setPowerControl(this.CT.getPowerControl() + 0.5F * f);
        else this.CT.setPowerControl(this.CT.getPowerControl() - 0.5F * f);
        float f16 = this.EI.engines[0].getCriticalW();
        if (this.EI.engines[0].getw() > 0.9F * f16) {
            float f2 = 10.0F * (f16 - this.EI.engines[0].getw()) / f16;
            if (f2 < this.CT.PowerControl) this.CT.setPowerControl(f2);
        }
        if (this.indSpeed > 0.8F * this.VmaxAllowed) {
            float f3 = 1.0F * (this.VmaxAllowed - this.indSpeed) / this.VmaxAllowed;
            if (f3 < this.CT.PowerControl) this.CT.setPowerControl(f3);
        }
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    private void doSetBoost() {
        if (this.EI.isSelectionHasControlBoost()) {
            float f = this.CT.getManualAfterburnerControl();
            float f1 = this.EI.getManifoldPressure();
            if (f1 < this.EI.engines[0].compressorRPMtoWMaxATA) this.CT.setManualfterburnerControl(f + 0.1F);
            else if (f1 > this.EI.engines[0].compressorRPMtoWMaxATA) this.CT.setManualfterburnerControl(f - 0.1F);
        } else this.CT.setAfterburnerControl(true);
    }
    // TODO: --- TD AI code backport from 4.13 ---

    private void setRandomTargDeviation(float f) {
        if (this.isTick(16, 0)) {
            float f1 = f * (8.0F - 1.5F * this.Skill);
            this.TargDevVnew.set(World.Rnd().nextFloat(-f1, f1), World.Rnd().nextFloat(-f1, f1), World.Rnd().nextFloat(-f1, f1));
        }
        this.TargDevV.interpolate(this.TargDevVnew, 0.01F);
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    private void setRSDgrd(float f, float f1) {
        if (this.isTick(16, 0)) {
            float f2 = 0.65F * f * (10F - (this.gunnery + this.flying) * 0.5F);
            this.wanderVectorNew.set(0.0D, World.Rnd().nextFloat(-1F, 1.0F), World.Rnd().nextFloat(-1F, 1.0F));
            this.wanderVectorNew.normalize();
            this.wanderVectorNew.scale(f2 * f1);
        }
        this.wanderVector.interpolate(this.wanderVectorNew, this.wanderRate * 1.2F);
    }

    private Point_Any lookAtNextTaxiToTakeOffPoint() {
        if (this.taxiToTakeOffWay == null) return new Point_Any(this.AP.way.first().x(), this.AP.way.first().y());
        if (this.taxiToTakeOffWay.size() == 0) return null;
        if (this.curAirdromePoi >= this.taxiToTakeOffWay.size()) return null;
        else return (Point_Any) this.taxiToTakeOffWay.get(this.curAirdromePoi + 1);
    }

    private Point_Any getNextTaxiToTakeOffPoint() {
        if (this.taxiToTakeOffWay == null) return null;
        if (this.taxiToTakeOffWay.size() == 0) return null;
        if (this.curAirdromePoi >= this.taxiToTakeOffWay.size()) return null;
        else return (Point_Any) this.taxiToTakeOffWay.get(this.curAirdromePoi++);
    }
// TODO: --- TD AI code backport from 4.13 ---

    private Point_Any getNextAirdromeWayPoint() { // TODO: Doesn't exist in 4.13 anymore, CHECK TWICE !!!! ++++++++++++++++++++++++++++++++++++++
        if (this.airdromeWay == null) return null;
        if (this.airdromeWay.length == 0) return null;
        if (this.curAirdromePoi >= this.airdromeWay.length) return null;
        return this.airdromeWay[this.curAirdromePoi++];
    }

    private void findPointForEmLanding(float f) {
        Point3d point3d = elLoc.getPoint();
        if (this.radius > 2.0 * this.rmax) if (this.submaneuver != SUB_MAN69) this.initTargPoint(f);
        else return;
        for (int i = 0; i < 32; i++) {
            Po.set(this.Vtarg.x + this.radius * Math.sin(this.phase), this.Vtarg.y + this.radius * Math.cos(this.phase), this.Loc.z);
            this.radius += 0.01 * this.rmax;
            this.phase += 0.3;
            Ve.sub(Po, this.Loc);
            double d = Ve.length();
            this.Or.transformInv(Ve);
            Ve.normalize();
            float f1 = 0.9F - 0.0050F * this.Alt;
            if (f1 < -1.0F) f1 = -1.0F;
            if (f1 > 0.8F) f1 = 0.8F;
            float f2 = 1.5F - 5.0E-4F * this.Alt;
            if (f2 < 1.0F) f2 = 1.0F;
            if (f2 > 1.5F) f2 = 1.5F;
            if (this.rmax > d && d > this.rmin * f2 && Ve.x > f1 && this.isConvenientPoint()) {
                this.submaneuver = SUB_MAN69;
                point3d.set(Po);
                this.pointQuality = this.curPointQuality;
                break;
            }
        }
    }

    private boolean isConvenientPoint() {
        Po.z = Engine.cur.land.HQ_Air(Po.x, Po.y);
        this.curPointQuality = 50;
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                if (Engine.land().isWater(Po.x + 500.0 * i, Po.y + 500.0 * j)) {
                    if (!(this.actor instanceof TypeSailPlane)) this.curPointQuality--;
                } else if (this.actor instanceof TypeSailPlane) this.curPointQuality--;
                if (Engine.cur.land.HQ_ForestHeightHere(Po.x + 500.0 * i, Po.y + 500.0 * j) > 1.0) this.curPointQuality -= 2;
                if (Engine.cur.land.EQN(Po.x + 500.0 * i, Po.y + 500.0 * j, Ve) > 1110.949951171875) this.curPointQuality -= 2;
            }
        if (this.curPointQuality < 0) this.curPointQuality = 0;
        if (this.curPointQuality > this.pointQuality) return true;
        return false;
    }

    private void emergencyTurnToDirection(float f) {
        if (Math.abs(Ve.y) > 0.1) this.CT.AileronControl = clamp11(-(float) Math.atan2(Ve.y, Ve.z) - 0.016F * this.Or.getKren());
        else this.CT.AileronControl = clamp11(-(float) Math.atan2(Ve.y, Ve.x) - 0.016F * this.Or.getKren());
        this.CT.RudderControl = clamp11(-10.0F * (float) Math.atan2(Ve.y, Ve.x));
        if (this.CT.RudderControl * this.W.z > 0.0) this.W.z = 0.0;
        else this.W.z *= 1.04;
    }

    private void initTargPoint(float f) {
        int i = this.AP.way.Cur();
        this.Vtarg.set(this.AP.way.last().x(), this.AP.way.last().y(), 0.0);
        this.AP.way.setCur(i);
        this.Vtarg.sub(this.Loc);
        this.Vtarg.z = 0.0;
        if (this.Vtarg.length() > this.rmax) {
            this.Vtarg.normalize();
            this.Vtarg.scale(this.rmax);
        }
        this.Vtarg.add(this.Loc);
        this.phase = 0.0;
        this.radius = 50.0;
        this.pointQuality = -1;
    }

    private void emergencyLanding(float f) {
        if (this.first) {
            this.Kmax = this.Wing.new_Cya(5.0F) / this.Wing.new_Cxa(5.0F);
            if (this.Kmax > 14.0F) this.Kmax = 14.0F;
            this.Kmax *= 0.8F;
            this.rmax = 1.2F * this.Kmax * this.Alt;
            this.rmin = 0.6F * this.Kmax * this.Alt;
            this.initTargPoint(f);
            this.setReadyToDieSoftly(true);
            this.AP.setStabAll(false);
            if (this.TaxiMode) {
                this.setSpeedMode(8);
                this.smConstPower = 0.0F;
                this.push(44);
                this.pop();
            }
            this.dist = this.Alt;
            if (this.actor instanceof TypeSailPlane) this.EI.setEngineStops(); // FIXME: Does this make sense? Really?
        }
        this.setSpeedMode(4);
        this.smConstSpeed = this.VminFLAPS * 1.25F;
        if (this.Alt < 500.0F && (this.actor instanceof TypeGlider || this.actor instanceof TypeSailPlane)) this.CT.GearControl = 1.0F;
        if (this.Alt < 10.0F) {
            this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
            this.setSpeedMode(4);
            this.smConstSpeed = this.VminFLAPS * 1.1F;
            if (this.Alt < 5.0F) this.setSpeedMode(8);
            this.CT.BrakeControl = 0.2F;
            if (this.Vwld.length() < 0.3 && World.Rnd().nextInt(0, 99) < 4) {
                this.setStationedOnGround(true);

                if (this.actor != World.getPlayerAircraft()) {
                    this.push(44);
                    this.safe_pop();
                    if (this.actor instanceof TypeSailPlane) {
                        this.EI.setCurControlAll(true);
                        this.EI.setEngineStops();
                        if (Engine.land().isWater(this.Loc.x, this.Loc.y)) return;
                    }
                    ((Aircraft) this.actor).hitDaSilk();
                }
                if (this.Group != null) this.Group.delAircraft((Aircraft) this.actor);
                if (this.actor instanceof TypeGlider || this.actor instanceof TypeSailPlane) return;

                if (this.actor != World.getPlayerAircraft()) if (Airport.distToNearestAirport(this.Loc) > 900.0) ((Aircraft) this.actor).postEndAction(60.0, this.actor, 4, null);
                else MsgDestroy.Post(Time.current() + 30000L, this.actor);
            }
        }
        this.dA = 0.2F * (this.getSpeed() - this.Vmin * 1.3F) - 0.8F * (this.getAOA() - 5.0F);
        if (this.Alt < 40.0F) {
            this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
            this.CT.RudderControl = 0.0F;
            if (this.actor instanceof BI_1 || this.actor instanceof ME_163B1A) this.CT.GearControl = 1.0F;
            float f1;
            if (this.Gears.Pitch < 10.0F) f1 = (40.0F * (this.getSpeed() - this.VminFLAPS * 1.15F) - 60.0F * (this.Or.getTangage() + 3.0F) - 240.0F * (this.getVertSpeed() + 1.0F) - 120.0F * ((float) this.getAccel().z - 1.0F)) * 0.0040F;
            else f1 = (40.0F * (this.getSpeed() - this.VminFLAPS * 1.15F) - 60.0F * (this.Or.getTangage() - this.Gears.Pitch + 10.0F) - 240.0F * (this.getVertSpeed() + 1.0F) - 120.0F * ((float) this.getAccel().z - 1.0F)) * 0.0040F;
            if (this.Alt > 0.0F) {
                float f2 = 0.01666F * this.Alt;
                this.dA = this.dA * f2 + f1 * (1.0F - f2);
            } else this.dA = f1;
            this.CT.FlapsControl = 0.33F;
            if (this.Alt < 9.0F && Math.abs(this.Or.getKren()) < 5.0F && this.getVertSpeed() < -0.7F) this.Vwld.z *= 0.87;
        } else {
            this.rmax = 1.2F * this.Kmax * this.Alt;
            this.rmin = 0.6F * this.Kmax * this.Alt;
            if (this.actor instanceof TypeGlider && this.Alt > 200.0F) this.CT.RudderControl = 0.0F;
            else if (this.pointQuality < 50 && this.mn_time > 0.5F) this.findPointForEmLanding(f);
            if (this.submaneuver == SUB_MAN69) {
                Ve.sub(elLoc.getPoint(), this.Loc);
                double d = Ve.length();
                this.Or.transformInv(Ve);
                Ve.normalize();
                float f3 = 0.9F - 0.0050F * this.Alt;
                if (f3 < -1.0F) f3 = -1.0F;
                if (f3 > 0.8F) f3 = 0.8F;
                if (this.rmax * 2.0F < d || d < this.rmin || Ve.x < f3) {
                    this.submaneuver = SUB_MAN0;
                    this.initTargPoint(f);
                }
                if (d > 88.0) {
                    this.emergencyTurnToDirection(f);
                    if (this.Alt > d) this.submaneuver = SUB_MAN0;
                } else this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
            } else this.CT.AileronControl = clamp11(-0.04F * this.Or.getKren());
            if (this.Or.getTangage() > -1.0F) this.dA -= 0.1F * (this.Or.getTangage() + 1.0F);
            if (this.Or.getTangage() < -10.0F) this.dA -= 0.1F * (this.Or.getTangage() + 10.0F);
        }
        if (this.Alt < 2.0F || this.Gears.onGround()) this.dA = -2.0F * (this.Or.getTangage() - this.Gears.Pitch);
        double d1 = this.Vwld.length() / this.Vmin;
        if (d1 > 1.0) d1 = 1.0;
        this.CT.ElevatorControl += (this.dA - this.CT.ElevatorControl) * 3.33F * f + 1.5 * this.getW().y * d1 + 0.5 * this.getAW().y * d1;
    }

    public boolean canITakeoff() {
        Po.set(this.Loc);
        Vpl.set(69.0, 0.0, 0.0);
        this.Or.transform(Vpl);
        Po.add(Vpl);
        Pd.set(Po);
        if (this.actor != War.getNearestFriendAtPoint(Pd, (Aircraft) this.actor, 70.0F)) return false;
        if (this.canTakeoff) return true;
        if (Actor.isAlive(this.AP.way.takeoffAirport)) {
            if (this.AP.way.takeoffAirport.takeoffRequest <= 0) {
                this.AP.way.takeoffAirport.takeoffRequest = 2000;
                this.canTakeoff = true;
                return true;
            }
            return false;
        }
        return true;
    }

    public void set_maneuver_imm(int i) {
        int j = this.maneuver;
        this.maneuver = i;
        if (j != this.maneuver) this.set_flags();
    }

    private void addWindCorrection() {
        do {
            if (World.cur().diffCur.Wind_N_Turbulence) if (!World.wind().noWind) break;
            return;
        } while (false);
        double d = Ve.length();

        World.wind().getVectorAI(this.actor.pos.getAbsPoint(), this.windV);
        this.windV.scale(-1.0);
        Ve.normalize();
        Ve.scale(this.getSpeed());
        Ve.add(this.windV);
        Ve.normalize();
        Ve.scale(d);
    }

    // TODO: CTO Mod altered
    // ---------------------------------------------------
    protected void turnOnChristmasTree(boolean flag) {
        if (flag) {
            if (World.Sun().ToSun.z < -0.22F && !this.bNoNavLightsAI) this.AS.setNavLightsState(true);
        } else this.AS.setNavLightsState(false);
    }
    // ---------------------------------------------------

    // TODO: +++ TD AI code backport from 4.13 +++
    public boolean canHideInClouds() {
        if (Mission.curCloudsType() >= 4) {
            float f = 1.0F;
            if (this.actor instanceof TypeFighter) f = 2.0F;
            float f1 = (float) this.Loc.z - Mission.curCloudsHeight();
            if (f1 >= 0.0F && f1 < 2000F * f) return true;
            if (f1 < 0.0F && f1 > -1000F * f) return true;
        }
        return false;
    }

    private void checkBlindSpots() {
        if (this.maneuver == 21 && this.Group.nOfAirc == 1 || this.maneuver == 24 && this.Group.airc[this.Group.nOfAirc - 1] == this.actor) {
            if (this.formationType == 4) return;
            if (this.actor instanceof TypeFighter && !this.isBusy() && this.crew == 1) {
                float f = this.AP.getWayPointDistance();
                if (f < 1000F) return;
                if (this.AS.getPilotHealth(0) < 0.2D || !this.isCapableOfACM()) return;
                if (this.first) this.lookAroundTime = Time.current() + World.Rnd().nextInt(5000, 0x186a0 - this.Skill * 20000);
                if (this.lookAroundTime < Time.current()) {
                    if (Math.abs(this.Or.getRoll() - 360F) < 10F && Math.abs(this.Or.getPitch() - 360F) < 20F) {
                        byte byte0 = 1;
                        byte byte1 = 7;
                        if (this.Alt < 700F) byte0 = 4;
                        if (byte0 < byte1) switch (World.Rnd().nextInt(byte0, byte1)) {
                            case 2:
                                this.push();
                                this.push(90);
                                this.pop();
                                break;

                            case 3:
                                this.push();
                                this.push(91);
                                this.pop();
                                break;

                            case 4:
                            case 5:
                                this.push();
                                this.push(88);
                                this.pop();
                                break;

                            case 6:
                            case 7:
                                this.push();
                                this.push(89);
                                this.pop();
                                break;
                        }
                    }
                    boolean flag = Front.isCaptured(this.actor);
                    int i = 60000 - this.Skill * 15000;
                    int j = 0x30d40 - this.Skill * 55000;
                    if (!flag && Front.markers().size() > 0) j *= 2;
                    this.lookAroundTime = Time.current() + World.Rnd().nextInt(i, j);
                }
            }
        }
    }

    private void planAlternativeTaxiRoute(Point3d point3d, float f, Actor actor) {
        if (this.curAirdromePoi >= this.taxiToTakeOffWay.size()) return;
        Point_Any point_any = null;
        Point_Any point_any1 = null;
        Point_Any point_any2 = this.wayCurPos;
        int i = 0;
        float f1 = 2.0F;
        int j = 0;
        boolean flag = false;
        for (Iterator iterator = this.taxiToTakeOffWay.iterator(); iterator.hasNext();) {
            Point_Any point_any3 = (Point_Any) iterator.next();
            if (++i >= this.curAirdromePoi) {
                tmpLoc.set(point_any3.x, point_any3.y, point3d.z);
                float f3 = (float) point3d.distance(tmpLoc);
                if (f3 < f) {
                    if (!flag) j = i;
                    flag = true;
                    iterator.remove();
                } else if (point_any1 == null && flag) point_any1 = point_any3;
            }
        }

        if (j != 0) for (Iterator iterator1 = this.taxiToTakeOffWay.iterator(); iterator1.hasNext();) {
            i++;
            iterator1.next();
            if (i > this.curAirdromePoi && i < j) iterator1.remove();
        }
        point_any = new Point_Any((float) this.actor.pos.getAbs().getX(), (float) this.actor.pos.getAbs().getY());
        if (point_any1 == null) point_any1 = point_any2;
        tmpLoc.set(point_any1.x, point_any1.y, point3d.z);
        float f2 = (float) point3d.distance(tmpLoc);
        tmpLoc.set(point_any.x, point_any.y, point3d.z);
        float f4 = (float) tmpLoc.distance(point3d);
        if (f2 < f * f1) {
            Point_Any point_any5 = this.getEvadePoint(new Point2f((float) point3d.x, (float) point3d.y), f, actor);
            this.taxiToTakeOffWay.add(this.curAirdromePoi - 1, point_any5);
            this.wayCurPos = point_any5;
        } else {
            Vector2f vector2f = new Vector2f();
            vector2f.sub(point_any1, point_any);
            vector2f.normalize();
            vector2f.scale(f4 + f * f1);
            Point_Any point_any6 = new Point_Any(point_any.x, point_any.y);
            point_any6.add(vector2f);
            Point_Any point_any7 = this.getEvadePoint(new Point2f((float) point3d.x, (float) point3d.y), f, actor);
            this.taxiToTakeOffWay.add(this.curAirdromePoi - 1, point_any7);
            this.taxiToTakeOffWay.add(this.curAirdromePoi, point_any6);
            this.wayCurPos = point_any7;
        }
    }

    private Point_Any getEvadePoint(Point2f point2f, float f, Actor actor) {
        Vector2f vector2f = new Vector2f();
        vector2f.sub(point2f, Pcur);
        float f1 = vector2f.length();
        float f2 = f + this.Wingspan * 0.5F;
        float f3 = (float) Math.atan(f2 / f1);
        if (evadeFromRightSide(point2f)) f3 *= -1F;
        Point_Any point_any = this.getPoint(vector2f, f3);
        Pd.set(point_any.x, point_any.y, this.Loc.z);
        Actor actor1 = War.getNearestAnyFriendAtPoint(Pd, (Aircraft) this.actor, this.Wingspan * 1.5F);
        if (actor1 != null && this.actor != actor1 && actor1 != actor) {
            if (actor1 instanceof Aircraft && ((Maneuver) ((Aircraft) actor1).FM).Group == this.Group) return point_any;
            else return this.getPoint(vector2f, f3 * -1F);
        } else return point_any;
    }

    private Point_Any getPoint(Vector2f vector2f, float f) {
        float f1 = FMMath.RAD2DEG(f);
        Orient orient = new Orient();
        orient.setYPR(f1, 0.0F, 0.0F);
        tmpV3f.set(vector2f.x, vector2f.y, 0.0D);
        orient.transform(tmpV3f);
        float f2 = (float) (vector2f.length() / Math.cos(f));
        tmpV3f.normalize();
        tmpV3f.scale(f2);
        tmpLoc.set(Pcur.x, Pcur.y, 0.0D);
        tmpLoc.add(tmpV3f);
        return new Point_Any((float) tmpLoc.x, (float) tmpLoc.y);
    }

    private static boolean evadeFromRightSide(Point2f point2f) {
        float f = 360F - (float) FMMath.RAD2DEG(Vcur.direction());
        Vector2f vector2f = new Vector2f();
        vector2f.sub(point2f, Pcur);
        float f1 = 360F - vector2f.direction();
        if (f > f1) return true;
        else return f + 180F < f1;
    }

    private boolean isStopOnStalemate(Aircraft aircraft) {
        if (!aircraft.isAlive() || aircraft.isDestroyed() || !aircraft.FM.isCapableOfTaxiing() || aircraft.FM.isCrashedOnGround()) return false;
        if (this.collisionDangerActor != null && this.taxiToTakeOffWay.size() - this.curAirdromePoi > 1 && collisionMap.containsKey(this.collisionDangerActor)) {
            Object obj = collisionMap.get(this.collisionDangerActor);
            Object aobj[] = (Object[]) obj;
            Point3d point3d = (Point3d) aobj[0];
            if (point3d.x == 0.0D && point3d.y == 0.0D && point3d.z == 0.0D) return false;
        }
        if (this.taxiToTakeOffWay.size() - this.curAirdromePoi > 2) {
            if (this.collisionDangerActor == aircraft && ((Maneuver) aircraft.FM).collisionDangerActor == null) return true;
            if (this.collisionDangerActor == null && ((Maneuver) aircraft.FM).collisionDangerActor == this.actor) return false;
        }
        if (this.Group == ((Maneuver) aircraft.FM).Group) {
            int i = this.Group.numInGroup((Aircraft) this.actor);
            int j = this.Group.numInGroup(aircraft);
            return i > j;
        }
        if (((Maneuver) aircraft.FM).taxiToTakeOffWay == null) return true;
        if (this.taxiToTakeOffWay.size() - this.curAirdromePoi < ((Maneuver) aircraft.FM).taxiToTakeOffWay.size() - ((Maneuver) aircraft.FM).curAirdromePoi) return false;
        if (this.taxiToTakeOffWay.size() - this.curAirdromePoi == ((Maneuver) aircraft.FM).taxiToTakeOffWay.size() - ((Maneuver) aircraft.FM).curAirdromePoi) {
            if (this.Wingspan < aircraft.FM.Wingspan && this.taxiToTakeOffWay.size() - this.curAirdromePoi > 0) return false;
            if (this.Wingspan == aircraft.FM.Wingspan || this.taxiToTakeOffWay.size() - this.curAirdromePoi == 0) {
                if (this.distToTaxiPoint < ((Maneuver) aircraft.FM).distToTaxiPoint) return false;
                if (this.distToTaxiPoint == ((Maneuver) aircraft.FM).distToTaxiPoint) {
                    if (aircraft.pos.getAbs().getX() > this.actor.pos.getAbs().getX()) return true;
                    return aircraft.pos.getAbs().getX() == this.actor.pos.getAbs().getX() && aircraft.pos.getAbs().getY() > this.actor.pos.getAbs().getY();
                } else return true;
            } else return true;
        } else return true;
    }

    private boolean isTaxingCollisionDanger() {
        if (this.isTick(64, 0)) this.cleanCollisionMap();
        if (collisionMap.containsKey(this.actor)) {
            Object obj = collisionMap.get(this.actor);
            Object aobj[] = (Object[]) obj;
            Point3d point3d = (Point3d) aobj[0];
            if (point3d.x == 0.0D && point3d.y == 0.0D && point3d.z == 0.0D) {
                Long long1 = (Long) aobj[1];
                if (Time.current() - long1.longValue() < 5000L) return true;
            }
        }
        this.collisionDangerActor = null;
        float f = FlightModel.cvt(this.Length, 8F, 20F, 30F, 45F);
        float f1 = this.CT.RudderControl * 0.1F;
        float f2 = f1 * -(f * 5F);
        float f3 = Math.abs(f1) * (f * 7F);
        float f4 = f - f3;
        Point3d point3d1 = this.updateCollisionMap(f4, f2);
        Set set = collisionMap.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj1 = iterator.next();
            if (obj1 != this.actor) {
                Object obj2 = collisionMap.get(obj1);
                Object aobj1[] = (Object[]) obj2;
                Point3d point3d2 = (Point3d) aobj1[0];
                float f6 = (float) point3d1.distance(point3d2);
                if (f6 < f) {
                    Aircraft aircraft1 = (Aircraft) obj1;
                    if (this.isStopOnStalemate(aircraft1)) {
                        point3d1.set(0.0D, 0.0D, 0.0D);
                        this.collisionDangerActor = aircraft1;
                        return true;
                    }
                }
            }
        }

        byte byte0 = 3;
        for (int i = 1; i <= byte0; i++) {
            Po.set(this.Loc);
            float f5 = Math.min(f4 * i * 0.666F, this.distToTaxiPoint + this.Wingspan * 0.5F);
            Vpl.set(f5, f2, 0.0D);
            this.Or.transform(Vpl);
            Po.add(Vpl);
            Po.z = this.Loc.z;
            Pd.set(Po);
            Actor actor = War.getNearestAnyFriendAtPoint(Pd, (Aircraft) this.actor, Math.min(this.Wingspan * 1.2F, f5));
            if (actor != null && this.actor != actor && actor != this.ignoredActor) {
                this.collisionDangerActor = actor;
                if (actor instanceof Aircraft) {
                    Aircraft aircraft = (Aircraft) actor;
                    if (!aircraft.isAlive() || aircraft.isDestroyed() || !aircraft.FM.isCapableOfTaxiing() || aircraft.FM.isCrashedOnGround()) {
                        this.planAlternativeTaxiRoute(aircraft.pos.getAbsPoint(), aircraft.FM.Wingspan, actor);
                        this.ignoredActor = actor;
                        return false;
                    }
                    if (((Maneuver) aircraft.FM).collisionDangerActor == this.actor) return this.isStopOnStalemate(aircraft);
                    if (((Maneuver) aircraft.FM).distToTaxiPoint == -1F) continue;
                } else if (!actor.isAlive() || actor.isDestroyed() || actor.getSpeed(Vpl) < 0.1D) {
                    this.planAlternativeTaxiRoute(actor.pos.getAbsPoint(), actor.collisionR() * 5F, actor);
                    this.ignoredActor = actor;
                    return false;
                }
                return true;
            }
            if (i == byte0 && this.isTick(2, 0)) {
                // TODO: +++ TD AI code backport from 4.13 +++
                // List list = ActorCrater.getCraters(); // Use Reflection to avoid changes to ActorCrater class
                List list = (List) Reflection.getValue(ActorCrater.class, "craterList");
                // TODO: --- TD AI code backport from 4.13 ---
                for (int j = 0; j < list.size(); j++) {
                    Actor actor1 = (Actor) list.get(j);
                    if (actor1 != this.ignoredActor) {
                        double d = Pd.distance(actor1.pos.getAbsPoint());
                        if (d < f4 * 0.5F) {
                            this.planAlternativeTaxiRoute(actor1.pos.getAbsPoint(), this.Wingspan * 0.7F, actor1);
                            this.ignoredActor = actor1;
                            return false;
                        }
                    }
                }

            }
        }

        return false;
    }

    public static void updateCollisionMap(FlightModelMain flightmodelmain) {
        float f = FlightModel.cvt(flightmodelmain.Length, 8F, 20F, 25F, 40F);
        float f1 = (float) flightmodelmain.getW().z * -(f * 5F);
        float f2 = Math.abs((float) flightmodelmain.getW().z) * (f * 7F);
        float f3 = f - f2;
        if (f3 < 1.0F) f3 = 1.0F;
        Po.set(flightmodelmain.Loc);
        Vpl.set(f3 * 1.25F, f1, 0.0D);
        flightmodelmain.Or.transform(Vpl);
        Po.add(Vpl);
        Po.z = flightmodelmain.Loc.z;
        Point3d point3d = new Point3d();
        point3d.set(Po);
        Object aobj[] = new Object[2];
        aobj[0] = point3d;
        aobj[1] = new Long(Time.current());
        collisionMap.put(flightmodelmain.actor, aobj);
    }

    private Point3d updateCollisionMap(float f, float f1) {
        Po.set(this.Loc);
        Vpl.set(f * 1.25F, f1, 0.0D);
        this.Or.transform(Vpl);
        Po.add(Vpl);
        Po.z = this.Loc.z;
        Point3d point3d = new Point3d();
        point3d.set(Po);
        Object aobj[] = new Object[2];
        aobj[0] = point3d;
        aobj[1] = new Long(Time.current());
        collisionMap.put(this.actor, aobj);
        return point3d;
    }

    private void cleanCollisionMap() {
        Set set = collisionMap.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            Object obj1 = collisionMap.get(obj);
            Object aobj[] = (Object[]) obj1;
            long l = ((Long) aobj[1]).longValue();
            if (Time.current() - l > 30000L) iterator.remove();
        }

    }

    private void cleanCollisionMap(Actor actor) {
        Set set = collisionMap.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            if (obj == actor) iterator.remove();
        }

    }

    public static void clear() {
        collisionMap.clear();
    }

    private void setTurn(float f, float f1, float f2) {
        this.CT.setPowerControl(1.1F);
        this.setSpeedMode(11);
        tmpOr.setYPR(this.Or.getYaw(), 0.0F, 0.0F);
        Ve.set(f, f1, f2);
        tmpOr.transform(Ve);
        this.Or.transformInv(Ve);
        Ve.normalize();
        this.farTurnToDirection(10F);
    }

    private boolean isEnableToTaxi() {
        if (this.CT.bHasGearControl) return this.CT.getGear() == 1.0F;
        else return true;
    }

    private float clampA(float f, float f1) {
        if (f < -f1) f = -f1;
        else if (f > f1) f = f1;
        return f;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    // TODO: +++ From western EngineMod 2.8.17w +++
    private float clamp11(float f) {
        if (f < -1.0F) f = -1.0F;
        else if (f > 1.0F) f = 1.0F;
        return f;
    }
    // TODO: --- From western EngineMod 2.8.17w ---

    // TODO: +++ Added by SAS~Storebror: Make X-4 / Missile Carriers attack enemy fighters with missiles if no bomber is around
    public boolean isMissileValidForFighterAttack() {
        return (this.actor instanceof Aircraft && War.getNearestEnemyBomber((Aircraft)this.actor, 10000F, 2) == null);
    }
    // ---

}
