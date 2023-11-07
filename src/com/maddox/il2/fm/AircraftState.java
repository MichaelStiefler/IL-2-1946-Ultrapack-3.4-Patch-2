/* 4.10.1 class, here because of bomb bay door mod */
package com.maddox.il2.fm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.ActorPosMove;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.DServer;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.game.order.OrdersTree;
import com.maddox.il2.net.Chat;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.AircraftLH;
import com.maddox.il2.objects.air.Cockpit;
import com.maddox.il2.objects.air.DO_335;
import com.maddox.il2.objects.air.DO_335A0;
import com.maddox.il2.objects.air.DO_335V13;
import com.maddox.il2.objects.air.FW_190A8MSTL;
import com.maddox.il2.objects.air.GO_229;
import com.maddox.il2.objects.air.HE_162;
import com.maddox.il2.objects.air.JU_88NEW;
import com.maddox.il2.objects.air.MBR_2AM34;
import com.maddox.il2.objects.air.ME_262HGII;
import com.maddox.il2.objects.air.P_39;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.air.Scheme0;
import com.maddox.il2.objects.air.Scheme1;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeDockable;
import com.maddox.il2.objects.air.TypeHasToKG;
import com.maddox.il2.objects.air.TypeRocketBoost;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.vehicles.radios.Beacon;
import com.maddox.il2.objects.vehicles.radios.TypeHasAAFIAS;
import com.maddox.il2.objects.vehicles.radios.TypeHasHayRake;
import com.maddox.il2.objects.vehicles.radios.TypeHasLorenzBlindLanding;
import com.maddox.il2.objects.vehicles.radios.TypeHasRadioStation;
import com.maddox.il2.objects.vehicles.radios.TypeHasYGBeacon;
import com.maddox.il2.objects.weapons.Bomb;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.MGunAircraftGeneric;
import com.maddox.il2.objects.weapons.RocketBombGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.il2.objects.weapons.TorpedoGun;
import com.maddox.rts.IniFileDated;
import com.maddox.rts.MsgDestroy;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetObj;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Reflection;
import com.maddox.sas1946.il2.util.TrueRandom;
import com.maddox.sound.CmdMusic;

public class AircraftState {
    public static final boolean __DEBUG_SPREAD__                       = false;
    private static final float  astateEffectCriticalSpeed              = 10.0F;
    private static final float  astateCondensateCriticalAlt            = 7000.0F;
    private static final Loc    astateCondensateDispVector             = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
    public static final int     _AS_RESERVED                           = 0;
    public static final int     _AS_ENGINE_STATE                       = 1;
    public static final int     _AS_ENGINE_SPECIFIC_DMG                = 2;
    public static final int     _AS_ENGINE_EXPLODES                    = 3;
    public static final int     _AS_ENGINE_STARTS                      = 4;
    public static final int     _AS_ENGINE_RUNS                        = 5;
    public static final int     _AS_ENGINE_STOPS                       = 6;
    public static final int     _AS_ENGINE_DIES                        = 7;
    public static final int     _AS_ENGINE_SOOT_POWERS                 = 8;
    public static final int     _AS_TANK_STATE                         = 9;
    public static final int     _AS_TANK_EXPLODES                      = 10;
    public static final int     _AS_OIL_STATE                          = 11;
    public static final int     _AS_GLIDER_BOOSTER                     = 12;
    public static final int     _AS_GLIDER_BOOSTOFF                    = 13;
    public static final int     _AS_GLIDER_CUTCART                     = 14;
    public static final int     _AS_AIRSHOW_SMOKES_STATE               = 15;
    public static final int     _AS_WINGTIP_SMOKES_STATE               = 16;
    public static final int     _AS_PILOT_STATE                        = 17;
    public static final int     _AS_KILLPILOT                          = 18;
    public static final int     _AS_HEADSHOT                           = 19;
    public static final int     _AS_BAILOUT                            = 20;
    public static final int     _AS_CONTROLS_HURT                      = 21;
    public static final int     _AS_INTERNALS_HURT                     = 22;
    public static final int     _AS_COCKPIT_STATE_BYTE                 = 23;
    public static final int     _AS_JAM_BULLETS                        = 24;
    public static final int     _AS_ENGINE_READYNESS                   = 25;
    public static final int     _AS_ENGINE_STAGE                       = 26;
    public static final int     _AS_ENGINE_CYL_KNOCKOUT                = 27;
    public static final int     _AS_ENGINE_MAG_KNOCKOUT                = 28;
    public static final int     _AS_ENGINE_STUCK                       = 29;
    public static final int     _AS_NAVIGATION_LIGHTS_STATE            = 30;
    public static final int     _AS_LANDING_LIGHT_STATE                = 31;
    public static final int     _AS_TYPEDOCKABLE_REQ_ATTACHTODRONE     = 32;
    public static final int     _AS_TYPEDOCKABLE_REQ_DETACHFROMDRONE   = 33;
    public static final int     _AS_TYPEDOCKABLE_FORCE_ATTACHTODRONE   = 34;
    public static final int     _AS_TYPEDOCKABLE_FORCE_DETACHFROMDRONE = 35;
    public static final int     _AS_FLATTOP_FORCESTRING                = 36;
    public static final int     _AS_FMSFX                              = 37;
    public static final int     _AS_WINGFOLD                           = 38;
    public static final int     _AS_COCKPITDOOR                        = 39;
    public static final int     _AS_ARRESTOR                           = 40;
    public static final int     _AS_COUNT_CODES                        = 41;
    public static final int     _AS_BEACONS                            = 42;
    public static final int     _AS_GYROANGLE                          = 44;
    public static final int     _AS_SPREADANGLE                        = 45;
    public static final int     _AS_PILOT_WOUNDED                      = 46;
    public static final int     _AS_PILOT_BLEEDING                     = 47;

    // +++++ TODO skylla: enhanced weapon release control +++++
    public static final int _AS_ROCKET_SELECTED      = 48;
    public static final int _AS_ROCKET_RELEASE_DELAY = 49;
    public static final int _AS_ROCKET_RELEASE_MODE  = 50;

    public static final int _AS_BOMB_SELECTED        = 51;
    public static final int _AS_BOMB_RELEASE_DELAY   = 52;
    public static final int _AS_BOMB_RELEASE_MODE    = 53;

//    public static final int _AS_LENGTH               = 54;
    // ----- todo skylla: enhanced weapon release control -----
    
    // TODO: By SAS~Storebror: Rocket Booster Net Replication +++
    public static final int _AS_BOOST_STATE          = 54;
    public static final int _AS_LENGTH               = 55;
    
    public static final byte _AS_BOOST_NOBOOST       = 0;
    public static final byte _AS_BOOST_EXISTS        = 0x1;
    public static final byte _AS_BOOST_ACTIVE        = 0x2;
    // TODO: By SAS~Storebror: Rocket Booster Net Replication ---

    public static final int    _AS_COCKPIT_GLASS             = 1;
    public static final int    _AS_COCKPIT_ARMORGLASS        = 2;
    public static final int    _AS_COCKPIT_LEFT1             = 4;
    public static final int    _AS_COCKPIT_LEFT2             = 8;
    public static final int    _AS_COCKPIT_RIGHT1            = 16;
    public static final int    _AS_COCKPIT_RIGHT2            = 32;
    public static final int    _AS_COCKPIT_INSTRUMENTS       = 64;
    public static final int    _AS_COCKPIT_OIL               = 128;
    public static final int    _ENGINE_SPECIFIC_BOOSTER      = 0;
    public static final int    _ENGINE_SPECIFIC_THROTTLECTRL = 1;
    public static final int    _ENGINE_SPECIFIC_HEATER       = 2;
    public static final int    _ENGINE_SPECIFIC_ANGLER       = 3;
    public static final int    _ENGINE_SPECIFIC_ANGLERSPEEDS = 4;
    public static final int    _ENGINE_SPECIFIC_EXTINGUISHER = 5;
    public static final int    _ENGINE_SPECIFIC_PROPCTRL     = 6;
    public static final int    _ENGINE_SPECIFIC_MIXCTRL      = 7;
    public static final int    _CONTROLS_AILERONS            = 0;
    public static final int    _CONTROLS_ELEVATORS           = 1;
    public static final int    _CONTROLS_RUDDERS             = 2;
    public static final int    _INTERNALS_HYDRO_OFFLINE      = 0;
    public static final int    _INTERNALS_PNEUMO_OFFLINE     = 1;
    public static final int    _INTERNALS_MW50_OFFLINE       = 2;
    public static final int    _INTERNALS_GEAR_STUCK         = 3;
    public static final int    _INTERNALS_KEEL_SHUTOFF       = 4;
    public static final int    _INTERNALS_SHAFT_SHUTOFF      = 5;
    protected long             bleedingTime;
    public long[]              astateBleedingTimes;
    public long[]              astateBleedingNext;
    private boolean            legsWounded;
    private boolean            armsWounded;
    public int                 torpedoGyroAngle;
    public int                 torpedoSpreadAngle;
    private static final float gyroAngleLimit                = 50.0F;
    public static final int    spreadAngleLimit              = 30;
    private int                beacon;
    private boolean            bWantBeaconsNet;
    public static final int    MAX_NUMBER_OF_BEACONS         = 32;
    public boolean             listenLorenzBlindLanding;
    public boolean             isAAFIAS;
    public boolean             listenYGBeacon;
    public boolean             listenNDBeacon;
    public boolean             listenRadioStation;
    public Actor               hayrakeCarrier;
    public String              hayrakeCode;
    public static int          hudLogBeaconId                = HUD.makeIdLog();
    public boolean             externalStoresDropped;
    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
    /*
     * private static final String[] astateOilStrings = { "3DO/Effects/Aircraft/BlackMediumSPD.eff", "3DO/Effects/Aircraft/BlackMediumTSPD.eff", null, null };
     *
     * private static final String[] astateTankStrings = { null, null, null, "3DO/Effects/Aircraft/RedLeakTSPD.eff", null, null, "3DO/Effects/Aircraft/RedLeakTSPD.eff", null, null, "3DO/Effects/Aircraft/BlackMediumSPD.eff",
     * "3DO/Effects/Aircraft/BlackMediumTSPD.eff", null, "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", null, "3DO/Effects/Aircraft/FireSPD.eff", "3DO/Effects/Aircraft/BlackHeavySPD.eff",
     * "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", "3DO/Effects/Aircraft/FireSPD.eff", "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", null, null, null, "3DO/Effects/Aircraft/RedLeakGND.eff", null, null,
     * "3DO/Effects/Aircraft/RedLeakGND.eff", null, null, "3DO/Effects/Aircraft/BlackMediumGND.eff", null, null, "3DO/Effects/Aircraft/BlackHeavyGND.eff", null, null, "3DO/Effects/Aircraft/FireGND.eff", "3DO/Effects/Aircraft/BlackHeavyGND.eff", null,
     * "3DO/Effects/Aircraft/FireGND.eff", "3DO/Effects/Aircraft/BlackHeavyGND.eff", null };
     *
     * private static final String[] astateEngineStrings = { null, null, null, "3DO/Effects/Aircraft/GraySmallSPD.eff", "3DO/Effects/Aircraft/GraySmallTSPD.eff", null, "3DO/Effects/Aircraft/BlackMediumSPD.eff", "3DO/Effects/Aircraft/BlackMediumTSPD.eff",
     * null, "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", null, "3DO/Effects/Aircraft/FireSPD.eff", "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", null, null, null,
     * "3DO/Effects/Aircraft/GraySmallGND.eff", null, null, "3DO/Effects/Aircraft/BlackMediumGND.eff", null, null, "3DO/Effects/Aircraft/BlackHeavyGND.eff", null, null, "3DO/Effects/Aircraft/FireGND.eff", "3DO/Effects/Aircraft/BlackHeavyGND.eff", null };
     */
    private static final String astateOilStrings[]    = { "3DO/Effects/Aircraft/OilBlackMediumSPD.eff", "3DO/Effects/Aircraft/OilBlackMediumTSPD.eff", null, null };
    private static final String astateTankStrings[]   = { null, null, null, "3DO/Effects/Aircraft/RedLeakTSPD.eff", null, null, "3DO/Effects/Aircraft/RedLeakTSPD.eff", null, null, "3DO/Effects/Aircraft/TankBlackMediumSPD.eff",
            "3DO/Effects/Aircraft/TankBlackMediumTSPD.eff", null, "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", null, "3DO/Effects/Aircraft/FireSPD.eff", "3DO/Effects/Aircraft/BlackHeavySPD.eff",
            "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", "3DO/Effects/Aircraft/FireSPDLong.eff", "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/BlackHeavyTSPD.eff", null, null, null, "3DO/Effects/Aircraft/RedLeakGND.eff", null, null,
            "3DO/Effects/Aircraft/RedLeakGND.eff", null, null, "3DO/Effects/Aircraft/BlackMediumGND.eff", null, null, "3DO/Effects/Aircraft/BlackHeavyGND.eff", null, null, "3DO/Effects/Aircraft/FireGND.eff", "3DO/Effects/Aircraft/BlackHeavyGND.eff", null,
            "3DO/Effects/Aircraft/FireGND.eff", "3DO/Effects/Aircraft/BlackHeavyGND.eff", null };
    private static final String astateEngineStrings[] = { null, null, null, "3DO/Effects/Aircraft/GraySmallSPD.eff", "3DO/Effects/Aircraft/GraySmallTSPD.eff", null, "3DO/Effects/Aircraft/EngineBlackMediumSPD.eff",
            "3DO/Effects/Aircraft/EngineBlackMediumTSPD.eff", null, "3DO/Effects/Aircraft/BlackHeavySPD.eff", "3DO/Effects/Aircraft/EngineBlackHeavyTSPD.eff", null, "3DO/Effects/Aircraft/FireSPD.eff", "3DO/Effects/Aircraft/BlackHeavySPD.eff",
            "3DO/Effects/Aircraft/EngineBlackHeavyTSPD.eff", null, null, null, "3DO/Effects/Aircraft/GraySmallGND.eff", null, null, "3DO/Effects/Aircraft/BlackMediumGND.eff", null, null, "3DO/Effects/Aircraft/BlackHeavyGND.eff", null, null,
            "3DO/Effects/Aircraft/FireGND.eff", "3DO/Effects/Aircraft/BlackHeavyGND.eff", null };
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---

    private static final String[] astateCondensateStrings        = { null, "3DO/Effects/Aircraft/CondensateTSPD.eff" };

    private static final String[] astateStallStrings             = { null, "3DO/Effects/Aircraft/StallTSPD.eff" };

    public static final String[]  astateHUDPilotHits             = { "Player", "Pilot", "CPilot", "NGunner", "TGunner", "WGunner", "VGunner", "RGunner", "EngMas", "BombMas", "RadMas", "ObsMas" };

    private static boolean        bCriticalStatePassed           = false;
    private boolean               bIsAboveCriticalSpeed;
    private boolean               bIsAboveCondensateAlt;
    private boolean               bIsOnInadequateAOA;
    public boolean                bShowSmokesOn;
    public boolean                bNavLightsOn;
    public boolean                bLandingLightOn;
    public boolean                bWingTipLExists;
    public boolean                bWingTipRExists;
    private boolean               bIsMaster;
    public Actor                  actor;
    public AircraftLH             aircraft;
    public byte[]                 astatePilotStates;
    public byte[]                 astatePilotFunctions;
    public int                    astatePlayerIndex;
    public boolean                bIsAboutToBailout;
    public boolean                bIsEnableToBailout;
    public byte                   astateBailoutStep;
    public int                    astateCockpitState;
    public byte[]                 astateOilStates;
    private Eff3DActor[][]        astateOilEffects;
    public byte[]                 astateTankStates;
    private Eff3DActor[][]        astateTankEffects;
    public byte[]                 astateEngineStates;
    private Eff3DActor[][]        astateEngineEffects;
    public byte[]                 astateSootStates;
    public Eff3DActor[][]         astateSootEffects;
    public Eff3DActor[]           astateCondensateEffects;
    private Eff3DActor[]          astateStallEffects;
    private Eff3DActor[]          astateAirShowEffects;
    private Eff3DActor[]          astateNavLightsEffects;
    private LightPointActor[]     astateNavLightsLights;
    public Eff3DActor[]           astateLandingLightEffects;
    private LightPointActor[]     astateLandingLightLights;
    public String[]               astateEffectChunks;
    public static final int       astateEffectsDispTanks         = 0;
    public static final int       astateEffectsDispEngines       = 20; // was 4
    public static final int       astateEffectsDispLights        = 32; // 28; // was 12
    public static final int       astateEffectsDispLandingLights = 38; // 34; // was 18
    public static final int       astateEffectsDispOilfilters    = 42; // 38; // was 22
    public static boolean         bCheckPlayerAircraft           = true;
    private Item[]                itemsToMaster;
    private Item[]                itemsToMirrors;
    public static final int       MAX_ENGINES = 12;

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
    private LightPointActor[] astateTankBurnLights;
    private LightPointActor[] astateEngineBurnLights;
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---

    // TODO: Cheater Protection ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private static HashMap      cheaters                     = new HashMap();
    private static IniFileDated cheatersIniFile              = null;
    private static final String cheatersIniFileName          = "cheaters.ini";
    private static long         cheatersIniFileLastCheck     = 0L;
    private static final long   cheatersIniFileCheckInterval = 10000L;
    public static boolean       isDServer                    = false;
    private static int          cheaterDebugLevel            = Integer.MIN_VALUE;
    private static final int    DEBUG_CHEATER_DEFAULT        = 0;

    static class Cheater {
        private String  name;
        private int     hitPercentage;
        private boolean fightAces;
        private boolean advancedTransportDefence;
        private boolean advancedAiTurrets;
        private float   smallBulletsPowerFactor;

        public Cheater() {
            this.name = "";
            this.hitPercentage = 0;
            this.fightAces = false;
            this.advancedTransportDefence = false;
            this.advancedAiTurrets = false;
            this.smallBulletsPowerFactor = 1F;
        }

        public Cheater(String name) {
            super();
            this.name = name;
        }

        public Cheater(String name, int hitPercentage, boolean fightAces, boolean advancedTransportDefence, boolean advancedAiTurrets, float smallBulletsPowerFactor) {
            this.name = name;
            this.hitPercentage = hitPercentage;
            this.fightAces = fightAces;
            this.advancedTransportDefence = advancedTransportDefence;
            this.advancedAiTurrets = advancedAiTurrets;
            this.smallBulletsPowerFactor = smallBulletsPowerFactor;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getHitPercentage() {
            return this.hitPercentage;
        }

        public void setHitPercentage(int hitPercentage) {
            this.hitPercentage = hitPercentage;
        }

        public boolean isFightAces() {
            return this.fightAces;
        }

        public void setFightAces(boolean fightAces) {
            this.fightAces = fightAces;
        }

        public boolean isAdvancedTransportDefence() {
            return this.advancedTransportDefence;
        }

        public void setAdvancedTransportDefence(boolean advancedTransportDefence) {
            this.advancedTransportDefence = advancedTransportDefence;
        }

        public boolean isAdvancedAiTurrets() {
            return this.advancedAiTurrets;
        }

        public void setAdvancedAiTurrets(boolean advancedAiTurrets) {
            this.advancedAiTurrets = advancedAiTurrets;
        }

        public float getSmallBulletsPowerFactor() {
            return this.smallBulletsPowerFactor;
        }

        public void setSmallBulletsPowerFactor(float smallBulletsPowerFactor) {
            this.smallBulletsPowerFactor = smallBulletsPowerFactor;
        }
    }

    private static void loadCheaters() {
        if (!isDServer) return;
        if (Time.currentReal() > cheatersIniFileLastCheck && Time.currentReal() < cheatersIniFileLastCheck + cheatersIniFileCheckInterval) return;
        if (!Mission.isPlaying()) return;
        cheatersIniFileLastCheck = Time.currentReal();
        if (cheatersIniFile == null) cheatersIniFile = new IniFileDated(cheatersIniFileName, 0);
        else if (!cheatersIniFile.isFileChanged()) return;
        else {
            cheatersIniFile.loadFile();
            cheatersIniFile.parseLines();
        }

        String[] cheaterNames = cheatersIniFile.getSubjects();
        if (cheaterNames.length < 1 && cheaters.isEmpty()) return; // no log for empty or missing cheaters file

        System.out.println();
        System.out.println("###################################");
        System.out.println("#####      CHEATER UPDATE     #####");

        System.out.println("# cheaters.ini has " + cheaterNames.length + " entries.");
        // cheaters = new Cheater[cheaterNames.length];
        cheaters.clear();
        for (int i = 0; i < cheaterNames.length; i++) {
            int hitPercentage = cheatersIniFile.get(cheaterNames[i], "hitPercentage", 0);
            boolean fightAces = cheatersIniFile.get(cheaterNames[i], "fightAces", 0) != 0;
            boolean advancedTransportDefence = cheatersIniFile.get(cheaterNames[i], "advancedTransportDefence", 0) != 0;
            boolean advancedAiTurrets = cheatersIniFile.get(cheaterNames[i], "advancedAiTurrets", 0) != 0;
            float smallBulletsPowerFactor = cheatersIniFile.get(cheaterNames[i], "smallBulletsPowerFactor", 1F);
//            cheaters.put(cheaterNames[i].toLowerCase(), new Integer(hitPercentage));
            cheaters.put(cheaterNames[i].toLowerCase(), new Cheater(cheaterNames[i].toLowerCase(), hitPercentage, fightAces, advancedTransportDefence, advancedAiTurrets, smallBulletsPowerFactor));
            System.out.println("# Cheater updated: " + cheaterNames[i] + " = " + hitPercentage + "%");
        }

        System.out.println("##### CHEATER UPDATE FINISHED #####");
        System.out.println("###################################");
        System.out.println();
    }

    public static String netUserName(Actor actor) {
        if (!isDServer) return null;
        if (!Actor.isValid(actor)) return null;
//      if (Mission.isNet() && Mission.isDogfight() && actor instanceof Aircraft) {
        if (actor instanceof Aircraft) {
            NetUser netuser = ((Aircraft) actor).netUser();
            if (netuser != null) return netuser.shortName();
            if (actor == World.getPlayerAircraft()) return World.cur().userCfg.callsign;
        }
        return null;
    }

    public static boolean isCheaterHitInactive(Actor actor) {
        if (!isDServer) return false;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return false;
        return isCheaterHitInactive(theNetUserName);
    }

    public static boolean isCheaterHitInactive(String userName) {
        if (!isDServer) return false;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) return TrueRandom.nextInt(100) > ((Cheater) match).getHitPercentage();
        return false;
    }

    public static int getCheaterHitRate(Actor actor) {
        if (!isDServer) return 100;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return 100;
        return getCheaterHitRate(theNetUserName);
    }

    public static int getCheaterHitRate(String userName) {
        if (!isDServer) return 100;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) // System.out.println("getCheaterHitRate(" + userName + ")=" + ((Integer) match).intValue());
            return ((Cheater) match).getHitPercentage();
        return 100;
    }

    public static boolean isCheaterFightAces(Actor actor) {
        if (!isDServer) return false;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return false;
        return isCheaterFightAces(theNetUserName);
    }

    public static boolean isCheaterFightAces(String userName) {
        if (!isDServer) return false;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) return ((Cheater) match).isFightAces();
        return false;
    }

    public static boolean isCheaterAdvancedTransportDefence(Actor actor) {
        if (!isDServer) return false;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return false;
        return isCheaterAdvancedTransportDefence(theNetUserName);
    }

    public static boolean isCheaterAdvancedTransportDefence(String userName) {
        if (!isDServer) return false;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) return ((Cheater) match).isAdvancedTransportDefence();
        return false;
    }

    public static boolean isCheaterAdvancedAiTurrets(Actor actor) {
        if (!isDServer) return false;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return false;
        return isCheaterAdvancedAiTurrets(theNetUserName);
    }

    public static boolean isCheaterAdvancedAiTurrets(String userName) {
        if (!isDServer) return false;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) return ((Cheater) match).isAdvancedAiTurrets();
        return false;
    }

    public static boolean isCheaterAdvancedAi(Actor actor) {
        if (!isDServer) return false;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return false;
        return isCheaterAdvancedAi(theNetUserName);
    }

    public static boolean isCheaterAdvancedAi(String userName) {
        if (!isDServer) return false;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) return ((Cheater) match).isAdvancedAiTurrets() || ((Cheater) match).isAdvancedTransportDefence();
        return false;
    }

    public static float getCheaterSmallBulletsPowerFactor(Actor actor) {
        if (!isDServer) return 1F;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return 1F;
        return getCheaterSmallBulletsPowerFactor(theNetUserName);
    }

    public static float getCheaterSmallBulletsPowerFactor(String userName) {
        if (!isDServer) return 1F;
        Object match = cheaters.get(userName.toLowerCase());
        if (match != null && match instanceof Cheater) return ((Cheater) match).getSmallBulletsPowerFactor();
        return 1F;
    }

    public static boolean isCheater(Actor actor) {
        if (!isDServer) return false;
        String theNetUserName = netUserName(actor);
        if (theNetUserName == null) return false;
        return isCheater(theNetUserName);
    }

    public static boolean isCheater(String userName) {
        if (!isDServer) return false;
        return cheaters.containsKey(userName.toLowerCase());
        // return getCheaterHitRate(userName) < 100;
    }

    private static int getCheaterDebugLevel() {
        if (cheaterDebugLevel == Integer.MIN_VALUE) cheaterDebugLevel = Config.cur.ini.get("Mods", "DEBUG_CHEATER", DEBUG_CHEATER_DEFAULT);
//          System.out.println("cheaterDebugLevel = " + cheaterDebugLevel);
        return cheaterDebugLevel;
    }

    public static void printCheaterDebug(String message) {
        if (getCheaterDebugLevel() == 0) return;
        System.out.println(message);
    }
    // -----------------------------------------------------------------------------------------

    public AircraftState() {
        // TODO: Cheater Protection
        isDServer = Main.cur() instanceof DServer;
        // isDServer = true; // TEST!
        // ---
        this.bleedingTime = 0L;
        this.astateBleedingTimes = new long[9];
        this.astateBleedingNext = new long[9];
        this.legsWounded = false;
        this.armsWounded = false;

        this.torpedoGyroAngle = 0;
        this.torpedoSpreadAngle = 0;

        this.beacon = 0;
        this.bWantBeaconsNet = false;

        this.listenLorenzBlindLanding = false;
        this.isAAFIAS = false;
        this.listenYGBeacon = false;
        this.listenNDBeacon = false;
        this.listenRadioStation = false;
        this.hayrakeCarrier = null;
        this.hayrakeCode = null;

        this.externalStoresDropped = false;

        this.bIsAboveCriticalSpeed = false;

        this.bIsAboveCondensateAlt = false;

        this.bIsOnInadequateAOA = false;
        this.bShowSmokesOn = false;
        this.bNavLightsOn = false;
        this.bLandingLightOn = false;
        
        // TODO: Changed by SAS~Storebror, Wingtip hooks might be unexistent.
//        this.bWingTipLExists = true;
//        this.bWingTipRExists = true;
        this.bWingTipLExists = false;
        this.bWingTipRExists = false;

        this.actor = null;
        this.aircraft = null;

        this.astatePilotStates = new byte[9];
        this.astatePilotFunctions = new byte[] { 1, 7, 7, 7, 7, 7, 7, 7, 7 };

        this.astatePlayerIndex = 0;
        this.bIsAboutToBailout = false;
        this.bIsEnableToBailout = true;
        this.astateBailoutStep = 0;
        this.astateCockpitState = 0;

        this.astateOilStates = new byte[MAX_ENGINES];
        this.astateOilEffects = new Eff3DActor[MAX_ENGINES][2];

        this.astateTankStates = new byte[12];
        this.astateTankEffects = new Eff3DActor[MAX_ENGINES][3];

        this.astateEngineStates = new byte[MAX_ENGINES];
        this.astateEngineEffects = new Eff3DActor[MAX_ENGINES][3];

        this.astateSootStates = new byte[MAX_ENGINES];
        this.astateSootEffects = new Eff3DActor[MAX_ENGINES][2];

        this.astateCondensateEffects = new Eff3DActor[MAX_ENGINES];

        this.astateStallEffects = new Eff3DActor[2];

        this.astateAirShowEffects = new Eff3DActor[2];

        this.astateNavLightsEffects = new Eff3DActor[6];

        this.astateNavLightsLights = new LightPointActor[6];

        this.astateLandingLightEffects = new Eff3DActor[4];

        this.astateLandingLightLights = new LightPointActor[4];

        this.astateEffectChunks = new String[54];

        this.itemsToMaster = null;
        this.itemsToMirrors = null;

        // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
        this.astateTankBurnLights = new LightPointActor[20];
        this.astateEngineBurnLights = new LightPointActor[MAX_ENGINES];
        // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---
    }

    public void set(Actor actor1, boolean flag) {
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        Loc loc1 = new Loc();

        this.actor = actor1;
        if (actor1 instanceof Aircraft) this.aircraft = (AircraftLH) actor1;
        else throw new RuntimeException("Can not cast aircraft structure into a non-aircraft entity.");
        this.bIsMaster = flag;

        // TODO: Changed by SAS~Storebror, Wingtip hooks might be unexistent.
        try {
            if (this.aircraft.findHook("_WingTipL") != null) this.bWingTipLExists = true;
        } catch (Exception e) { }
        try {
            if (this.aircraft.findHook("_WingTipR") != null) this.bWingTipRExists = true;
        } catch (Exception e) { }

        // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
        if (this.aircraft == World.getPlayerAircraft()) { // own Aircraft has User Settings
            this.fUserBombFuze = World.cur().userBombFuze;
            this.fUserBombDelay = World.cur().userBombDelay;
            this.fUserRocketDelay = World.cur().userRocketDelay;
        } else if (this.isMaster()) { // locally controlled AI Aircraft have 0.5 seconds bomb delay
            this.fUserBombFuze = AI_BOMB_FUZE;
            this.fUserBombDelay = AI_BOMB_DELAY;
            this.fUserRocketDelay = AI_ROCKET_DELAY;
        }
        // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication

        for (int i = 0; i < this.aircraft.FM.MaxNumFuelTank; i++)
            try {
                this.astateEffectChunks[i + astateEffectsDispTanks] = this.actor.findHook("_Tank" + (i + 1) + "Burn").chunkName();
                this.astateEffectChunks[i + astateEffectsDispTanks] = this.astateEffectChunks[i + astateEffectsDispTanks].substring(0, this.astateEffectChunks[i + astateEffectsDispTanks].length() - 1);
                Aircraft.debugprintln(this.aircraft, "AS: Tank " + i + " FX attached to '" + this.astateEffectChunks[i + astateEffectsDispTanks] + "' substring..");
            } catch (Exception e) {} finally {}
        for (int i = 0; i < this.aircraft.FM.EI.getNum(); i++)
            try {
                this.astateEffectChunks[i + astateEffectsDispEngines] = this.actor.findHook("_Engine" + (i + 1) + "Smoke").chunkName();
                this.astateEffectChunks[i + astateEffectsDispEngines] = this.astateEffectChunks[i + astateEffectsDispEngines].substring(0, this.astateEffectChunks[i + astateEffectsDispEngines].length() - 1);
                Aircraft.debugprintln(this.aircraft, "AS: Engine " + i + " FX attached to '" + this.astateEffectChunks[i + astateEffectsDispEngines] + "' substring..");
            } catch (Exception e) {} finally {}
        for (int i = 0; i < this.astateNavLightsEffects.length; i++)
            try {
                this.astateEffectChunks[i + astateEffectsDispLights] = this.actor.findHook("_NavLight" + i).chunkName();
                this.astateEffectChunks[i + astateEffectsDispLights] = this.astateEffectChunks[i + astateEffectsDispLights].substring(0, this.astateEffectChunks[i + astateEffectsDispLights].length() - 1);
                Aircraft.debugprintln(this.aircraft, "AS: Nav. Lamp #" + i + " attached to '" + this.astateEffectChunks[i + astateEffectsDispLights] + "' substring..");

                HookNamed hooknamed = new HookNamed(this.aircraft, "_NavLight" + i);
                loc1.set(1.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                hooknamed.computePos(this.actor, loc, loc1);
                Point3d point3d = loc1.getPoint();
                this.astateNavLightsLights[i] = new LightPointActor(new LightPoint(), point3d);
                if (i < 2) this.astateNavLightsLights[i].light.setColor(1.0F, 0.1F, 0.1F);
                else if (i < 4) this.astateNavLightsLights[i].light.setColor(0.0F, 1.0F, 0.0F);
                else this.astateNavLightsLights[i].light.setColor(0.7F, 0.7F, 0.7F);
                this.astateNavLightsLights[i].light.setEmit(0.0F, 0.0F);
                this.actor.draw.lightMap().put("_NavLight" + i, this.astateNavLightsLights[i]);
            } catch (Exception e) {} finally {}
        for (int i = 0; i < 4; i++)
            try {
                this.astateEffectChunks[i + astateEffectsDispLandingLights] = this.actor.findHook("_LandingLight0" + i).chunkName();
                this.astateEffectChunks[i + astateEffectsDispLandingLights] = this.astateEffectChunks[i + astateEffectsDispLandingLights].substring(0, this.astateEffectChunks[i + astateEffectsDispLandingLights].length() - 1);
                Aircraft.debugprintln(this.aircraft, "AS: Landing Lamp #" + i + " attached to '" + this.astateEffectChunks[i + astateEffectsDispLandingLights] + "' substring..");

                HookNamed hooknamed = new HookNamed(this.aircraft, "_LandingLight0" + i);
                loc1.set(1.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                hooknamed.computePos(this.actor, loc, loc1);
                Point3d point3d = loc1.getPoint();
                this.astateLandingLightLights[i] = new LightPointActor(new LightPoint(), point3d);
                this.astateLandingLightLights[i].light.setColor(0.4941177F, 0.9098039F, 0.9607843F);
                this.astateLandingLightLights[i].light.setEmit(0.0F, 0.0F);
                this.actor.draw.lightMap().put("_LandingLight0" + i, this.astateLandingLightLights[i]);
            } catch (Exception e) {} finally {}
        for (int i = 0; i < this.aircraft.FM.EI.getNum(); i++)
            try {
                this.astateEffectChunks[i + astateEffectsDispOilfilters] = this.actor.findHook("_Engine" + (i + 1) + "Oil").chunkName();
                this.astateEffectChunks[i + astateEffectsDispOilfilters] = this.astateEffectChunks[i + astateEffectsDispOilfilters].substring(0, this.astateEffectChunks[i + astateEffectsDispOilfilters].length() - 1);
                Aircraft.debugprintln(this.aircraft, "AS: Oilfilter " + i + " FX attached to '" + this.astateEffectChunks[i + astateEffectsDispOilfilters] + "' substring..");
            } catch (Exception e) {} finally {}
        for (int i = 0; i < this.astateEffectChunks.length; i++) {
            if (this.astateEffectChunks[i] != null) continue;
            this.astateEffectChunks[i] = "AChunkNameYouCanNeverFind";
        }
    }

    public boolean isMaster() {
        return this.bIsMaster;
    }

    public void setOilState(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (paramInt2 < 0 || paramInt2 > 1 || this.astateOilStates[paramInt1] == paramInt2) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetOilState(paramInt1, paramInt2);
            int i = 0;
            for (int j = 0; j < this.aircraft.FM.EI.getNum(); j++)
                if (this.astateOilStates[j] == 1) i++;
            if (i == this.aircraft.FM.EI.getNum()) this.setCockpitState(this.actor, this.astateCockpitState | 0x80);

            this.netToMirrors(11, paramInt1, paramInt2);
        } else this.netToMaster(11, paramInt1, paramInt2, paramActor);
    }

    public void hitOil(Actor paramActor, int paramInt) {
        if (this.astateOilStates[paramInt] > 0) return;
        if (this.astateOilStates[paramInt] < 1) this.setOilState(paramActor, paramInt, this.astateOilStates[paramInt] + 1);
    }

    public void repairOil(int paramInt) {
        if (!this.bIsMaster) return;
        if (this.astateOilStates[paramInt] > 0) this.setOilState(this.actor, paramInt, this.astateOilStates[paramInt] - 1);
    }

    private void doSetOilState(int paramInt1, int paramInt2) {
        if (this.astateOilStates[paramInt1] == paramInt2) return;

        Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[paramInt1 + astateEffectsDispOilfilters] + "' visibility..");
        boolean bool = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[paramInt1 + astateEffectsDispOilfilters]);
        Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[paramInt1 + astateEffectsDispOilfilters] + "' is " + (bool ? "visible" : "invisible") + "..");
        Aircraft.debugprintln(this.aircraft, "Stating OilFilter " + paramInt1 + " to state " + paramInt2 + (bool ? ".." : " rejected (missing part).."));
        if (!bool) return;

        Aircraft.debugprintln(this.aircraft, "Stating OilFilter " + paramInt1 + " to state " + paramInt2 + "..");
        this.astateOilStates[paramInt1] = (byte) paramInt2;
        int i = 0;
        if (!this.bIsAboveCriticalSpeed) i = 2;

        if (this.astateOilEffects[paramInt1][0] != null) Eff3DActor.finish(this.astateOilEffects[paramInt1][0]);
        this.astateOilEffects[paramInt1][0] = null;
        if (this.astateOilEffects[paramInt1][1] != null) Eff3DActor.finish(this.astateOilEffects[paramInt1][1]);
        this.astateOilEffects[paramInt1][1] = null;
        switch (this.astateOilStates[paramInt1]) {
            case 1:
                String str = astateOilStrings[i];
                if (str != null) try {
                    this.astateOilEffects[paramInt1][0] = Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt1 + 1) + "Oil"), null, 1.0F, str, -1.0F);
                } catch (Exception localException1) {}
                str = astateOilStrings[i + 1];
                if (str != null) try {
                    this.astateOilEffects[paramInt1][1] = Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt1 + 1) + "Oil"), null, 1.0F, str, -1.0F);
                } catch (Exception localException2) {}
                if (World.Rnd().nextFloat() >= 0.25F) break;
                this.aircraft.FM.setReadyToReturn(true);
        }
    }

    public void changeOilEffectBase(int paramInt, Actor paramActor) {
        if (paramActor == null) return;
        for (int i = 0; i < 2; i++)
            if (this.astateOilEffects[paramInt][i] != null) this.astateOilEffects[paramInt][i].pos.changeBase(paramActor, null, true);
    }

    public void setTankState(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (paramInt2 < 0 || paramInt2 > 6 || this.astateTankStates[paramInt1] == paramInt2) return;
        if (this.bIsMaster) {
            int i = this.astateTankStates[paramInt1];
            if (!this.doSetTankState(paramActor, paramInt1, paramInt2)) return;
            for (int j = i; j < paramInt2; j++) {
                if (j % 2 == 0) this.aircraft.setDamager(paramActor);
                this.doHitTank(paramActor, paramInt1);
            }
            if (this.aircraft.FM.isPlayers() && paramActor != this.actor && paramActor instanceof Aircraft && ((Aircraft) paramActor).isNetPlayer() && paramInt2 > 5 && this.astateTankStates[0] < 5 && this.astateTankStates[1] < 5
                    && this.astateTankStates[2] < 5 && this.astateTankStates[3] < 5 && !this.aircraft.FM.isSentBuryNote())
                Chat.sendLogRnd(3, "gore_lightfuel", (Aircraft) paramActor, this.aircraft);

            this.netToMirrors(9, paramInt1, paramInt2);
        } else this.netToMaster(9, paramInt1, paramInt2, paramActor);
    }

    public void hitTank(Actor paramActor, int paramInt1, int paramInt2) {
        if (this.astateTankStates[paramInt1] == 6) return;
        int i = this.astateTankStates[paramInt1] + paramInt2;
        if (i > 6) i = 6;
        this.setTankState(paramActor, paramInt1, i);
    }

    public void repairTank(int paramInt) {
        if (!this.bIsMaster) return;
        if (this.astateTankStates[paramInt] > 0) this.setTankState(this.actor, paramInt, this.astateTankStates[paramInt] - 1);
    }

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
    /*
     * public boolean doSetTankState(Actor paramActor, int paramInt1, int paramInt2) { boolean bool = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[(paramInt1 + astateEffectsDispTanks)]); Aircraft.debugprintln(this.aircraft,
     * "Stating Tank " + paramInt1 + " to state " + paramInt2 + (bool ? ".." : " rejected (missing part)..")); if (!bool) { return false; }
     *
     * if (World.getPlayerAircraft() == this.actor) { if ((this.astateTankStates[paramInt1] == 0) && ((paramInt2 == 1) || (paramInt2 == 2))) HUD.log("FailedTank"); if ((this.astateTankStates[paramInt1] < 5) && (paramInt2 >= 5))
     * HUD.log("FailedTankOnFire"); } this.astateTankStates[paramInt1] = (byte) paramInt2;
     *
     * if ((this.astateTankStates[paramInt1] < 5) && (paramInt2 >= 5)) { this.aircraft.FM.setTakenMortalDamage(true, paramActor); this.aircraft.FM.setCapableOfACM(false); } if ((paramInt2 < 4) && (this.aircraft.FM.isCapableOfBMP())) {
     * this.aircraft.FM.setTakenMortalDamage(false, paramActor); }
     *
     * int j = 0; if (!this.bIsAboveCriticalSpeed) j = 21; for (int i = 0; i < 3; i++) { if (this.astateTankEffects[paramInt1][i] != null) Eff3DActor.finish(this.astateTankEffects[paramInt1][i]); this.astateTankEffects[paramInt1][i] = null; String str =
     * astateTankStrings[(j + i + paramInt2 * 3)]; if (str != null) { if (paramInt2 > 2) this.astateTankEffects[paramInt1][i] = Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (paramInt1 + 1) + "Burn"), null, 1.0F, str, -1.0F); else
     * this.astateTankEffects[paramInt1][i] = Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (paramInt1 + 1) + "Leak"), null, 1.0F, str, -1.0F); } } this.aircraft.sfxSmokeState(2, paramInt1, paramInt2 > 4); return true; }
     */

    public boolean doSetTankState(Actor tankOwner, int tankNo, int newTankState) {
        boolean isTankDamageVisible = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[tankNo + astateEffectsDispTanks]);
        Aircraft.debugprintln(this.aircraft, "Stating Tank " + tankNo + " to state " + newTankState + (isTankDamageVisible ? ".." : " rejected (missing part).."));
        if (!isTankDamageVisible) return false;
        if (World.getPlayerAircraft() == this.actor) {
            if (this.astateTankStates[tankNo] == 0 && (newTankState == 1 || newTankState == 2)) HUD.log("FailedTank");
            if (this.astateTankStates[tankNo] < 5 && newTankState >= 5) HUD.log("FailedTankOnFire");
        }
        byte oldTankState = this.astateTankStates[tankNo];
        this.astateTankStates[tankNo] = (byte) newTankState;
        boolean isTankSmoking = false;
        boolean isTankBurning = false;
        if (newTankState == 5 && oldTankState < 4) isTankSmoking = true;
        if (newTankState == 4 && oldTankState == 4) isTankBurning = true;
        if (newTankState == 4 && oldTankState < 3) {
            isTankBurning = true;
            isTankSmoking = true;
        }
        if (this.astateTankStates[tankNo] < 5 && newTankState >= 5) {
            this.aircraft.FM.setTakenMortalDamage(true, tankOwner);
            this.aircraft.FM.setCapableOfACM(false);
        }
        if (newTankState < 4 && this.aircraft.FM.isCapableOfBMP()) this.aircraft.FM.setTakenMortalDamage(false, tankOwner);
        byte tankStateStringIndex = 0;
        if (!this.bIsAboveCriticalSpeed) tankStateStringIndex = 21;
        for (int tankStateEffectIndex = 0; tankStateEffectIndex < 3; tankStateEffectIndex++) {
            if (this.astateTankEffects[tankNo][tankStateEffectIndex] != null) Eff3DActor.finish(this.astateTankEffects[tankNo][tankStateEffectIndex]);
            this.astateTankEffects[tankNo][tankStateEffectIndex] = null;
            String tankStateEffectName = astateTankStrings[tankStateStringIndex + tankStateEffectIndex + newTankState * 3];
            if (isTankBurning && tankStateEffectName == null) tankStateEffectName = "3DO/Effects/Aircraft/FireSPDShort.eff";
            if (tankStateEffectName != null) {
                if (newTankState > 2) {
                    boolean isWingTank = false;
                    Hook tankBurnHook = this.actor.findHook("_Tank" + (tankNo + 1) + "Burn");
                    String tankBurnHookChunkName = tankBurnHook.chunkName();
                    if (tankBurnHookChunkName.toLowerCase().startsWith("wing")) {
                        isWingTank = true;
                        if (this.aircraft.hierMesh().isChunkVisible(tankBurnHookChunkName) && newTankState == 5) isTankSmoking = true;
                    }
                    if (tankStateEffectName.equals("3DO/Effects/Aircraft/FireSPD.eff")) {
                        if (isWingTank) tankStateEffectName = "3DO/Effects/Aircraft/FireSPDWing.eff";
                    } else if (tankStateEffectName.equals("3DO/Effects/Aircraft/FireSPDLong.eff")) {
                        if (isWingTank) tankStateEffectName = "3DO/Effects/Aircraft/FireSPDWingLong.eff";
                    } else if (tankStateEffectName.equals("3DO/Effects/Aircraft/BlackHeavySPD.eff")) {
                        if (isTankSmoking) tankStateEffectName = null;
                        else if (isWingTank) tankStateEffectName = "3DO/Effects/Aircraft/BlackHeavySPDWing.eff";
                    } else if (tankStateEffectName.equals("3DO/Effects/Aircraft/BlackHeavyTSPD.eff") && isTankSmoking && !isTankBurning) tankStateEffectName = null;
                    if (tankStateEffectName != null) this.astateTankEffects[tankNo][tankStateEffectIndex] = Eff3DActor.New(this.actor, tankBurnHook, null, 1.0F, tankStateEffectName, -1F);
                } else this.astateTankEffects[tankNo][tankStateEffectIndex] = Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (tankNo + 1) + "Leak"), null, 1.0F, tankStateEffectName, -1F);
                if (newTankState > 4) {
                    Hook tankBurnHook = this.actor.findHook("_Tank" + (tankNo + 1) + "Burn");
                    Point3d tankBurnLightPos = this.aircraft.getTankBurnLightPoint(tankNo, tankBurnHook);
                    boolean showTankBurnLight = true;
                    for (int tankBurnLightsIndex = 0; tankBurnLightsIndex < this.astateTankBurnLights.length; tankBurnLightsIndex++) {
                        if (this.astateTankBurnLights[tankBurnLightsIndex] == null) continue;
                        Point3d tankBurnLightRelPos = this.astateTankBurnLights[tankBurnLightsIndex].relPos;
                        double tankBurnLightDistanceToRel = tankBurnLightPos.distance(tankBurnLightRelPos);
                        if (tankBurnLightDistanceToRel >= 1.0D) continue;
                        showTankBurnLight = false;
                        break;
                    }

                    if (showTankBurnLight) for (int engineBurnLightsIndex = 0; engineBurnLightsIndex < this.astateEngineBurnLights.length; engineBurnLightsIndex++) {
                        if (this.astateEngineBurnLights[engineBurnLightsIndex] == null) continue;
                        Point3d engineBurnLightsPos = this.astateEngineBurnLights[engineBurnLightsIndex].relPos;
                        double tankBurnLightDistanceToEngine = tankBurnLightPos.distance(engineBurnLightsPos);
                        if (tankBurnLightDistanceToEngine >= 1.0D) continue;
                        showTankBurnLight = false;
                        break;
                    }
                    if (showTankBurnLight) {
                        this.astateTankBurnLights[tankNo] = new LightPointActor(new LightPoint(), tankBurnLightPos);
                        this.astateTankBurnLights[tankNo].light.setColor(1.0F, 0.9F, 0.5F);
                        this.astateTankBurnLights[tankNo].light.setEmit(5F, 5F);
                        this.actor.draw.lightMap().put("_TankBurnLight" + tankNo, this.astateTankBurnLights[tankNo]);
                    }
                } else if (this.astateTankBurnLights[tankNo] != null) {
                    this.actor.draw.lightMap().remove("_TankBurnLight" + tankNo);
                    this.astateTankBurnLights[tankNo].destroy();
                    this.astateTankBurnLights[tankNo] = null;
                }
            }
        }

        this.aircraft.sfxSmokeState(2, tankNo, newTankState > 4);
        return true;
    }
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---

    private void doHitTank(Actor paramActor, int paramInt) {
        if ((World.Rnd().nextInt(0, 99) < 75 || this.actor instanceof Scheme1) && this.astateTankStates[paramInt] == 6) {
            this.aircraft.FM.setReadyToDie(true);
            this.aircraft.FM.setTakenMortalDamage(true, paramActor);
            this.aircraft.FM.setCapableOfACM(false);
            Voice.speakMayday(this.aircraft);
            Aircraft.debugprintln(this.aircraft, "I'm on fire, going down!.");
            Explosions.generateComicBulb(this.actor, "OnFire", 12.0F);
            // TODO: Storebror: 4.14 Backport
            // if (World.Rnd().nextInt(0, 99) < 75 && this.aircraft.FM.Skill > 1) {
            if (World.Rnd().nextInt(0, 99) < 75 + this.aircraft.FM.subSkill) {
                // ---
                Aircraft.debugprintln(this.aircraft, "BAILING OUT - Tank " + paramInt + " is on fire!.");
                this.hitDaSilk();
            }
        } else if (World.Rnd().nextInt(0, 99) < 12) {
            this.aircraft.FM.setReadyToReturn(true);
            Aircraft.debugprintln(this.aircraft, "Tank " + paramInt + " hit, RTB..");
            Explosions.generateComicBulb(this.actor, "RTB", 12.0F);
        }
    }

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
    /*
     * public void changeTankEffectBase(int paramInt, Actor paramActor) { for (int i = 0; i < 3; i++) { if (this.astateTankEffects[paramInt][i] != null) { this.astateTankEffects[paramInt][i].pos.changeBase(paramActor, null, true); } }
     * this.aircraft.sfxSmokeState(2, paramInt, false); }
     */

    public void changeTankEffectBase(int tankNo, Actor tankOwner) {
        if (tankOwner != null) for (int tankEffectNo = 0; tankEffectNo < 3; tankEffectNo++)
            if (this.astateTankEffects[tankNo][tankEffectNo] != null) // astateTankEffects[tankNo][tankEffectNo].pos.changeBase(tankOwner, null, true);
                // +++ TODO: Bugfix by SAS~Storebror, check actor presence and matching instance
                if (this.astateTankEffects[tankNo][tankEffectNo].pos instanceof ActorPosMove) {
                    ActorPosMove apm = (ActorPosMove) this.astateTankEffects[tankNo][tankEffectNo].pos;
                    if (apm.actor() != null && tankOwner != null) apm.changeBase(tankOwner, null, true);
                }
        if (this.astateTankBurnLights[tankNo] != null) {
            this.actor.draw.lightMap().remove("_TankBurnLight" + tankNo);
            this.astateTankBurnLights[tankNo].destroy();
            this.astateTankBurnLights[tankNo] = null;
        }
        this.aircraft.sfxSmokeState(2, tankNo, false);
    }
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---

    public void explodeTank(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            if (!this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[paramInt + astateEffectsDispTanks])) return;

            this.netToMirrors(10, paramInt, 0);
            this.doExplodeTank(paramInt);
        } else this.netToMaster(10, paramInt, 0, paramActor);
    }

    private void doExplodeTank(int paramInt) {
        Aircraft.debugprintln(this.aircraft, "Tank " + paramInt + " explodes..");
        Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (paramInt + 1) + "Burn"), null, 1.0F, "3DO/Effects/Fireworks/Tank_Burn.eff", -1.0F);

        Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (paramInt + 1) + "Burn"), null, 1.0F, "3DO/Effects/Fireworks/Tank_SmokeBoiling.eff", -1.0F);

        Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (paramInt + 1) + "Burn"), null, 1.0F, "3DO/Effects/Fireworks/Tank_Sparks.eff", -1.0F);

        Eff3DActor.New(this.actor, this.actor.findHook("_Tank" + (paramInt + 1) + "Burn"), null, 1.0F, "3DO/Effects/Fireworks/Tank_SparksP.eff", -1.0F);

        this.aircraft.msgCollision(this.actor, this.astateEffectChunks[paramInt + astateEffectsDispTanks] + "0", this.astateEffectChunks[paramInt + astateEffectsDispTanks] + "0");
        if (this.actor instanceof Scheme1 && this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[paramInt + astateEffectsDispTanks])) this.aircraft.msgCollision(this.actor, "CF_D0", "CF_D0");

        HookNamed localHookNamed = new HookNamed((ActorMesh) this.actor, "_Tank" + (paramInt + 1) + "Burn");
        Loc localLoc1 = new Loc();
        Loc localLoc2 = new Loc();
        this.actor.pos.getCurrent(localLoc1);
        localHookNamed.computePos(this.actor, localLoc1, localLoc2);

        if (World.getPlayerAircraft() == this.actor) HUD.log("FailedTankExplodes");
    }

    public void setEngineState(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (paramInt2 < 0 || paramInt2 > 4 || this.astateEngineStates[paramInt1] == paramInt2) return;
        if (this.bIsMaster) {
            int i = this.astateEngineStates[paramInt1];
            if (!this.doSetEngineState(paramActor, paramInt1, paramInt2)) return;
            for (int j = i; j < paramInt2; j++) {
                this.aircraft.setDamager(paramActor);
                this.doHitEngine(paramActor, paramInt1);
            }
            if (this.aircraft.FM.isPlayers() && paramActor != this.actor && paramActor instanceof Aircraft && ((Aircraft) paramActor).isNetPlayer() && paramInt2 > 3 && this.astateEngineStates[0] < 3 && this.astateEngineStates[1] < 3
                    && this.astateEngineStates[2] < 3 && this.astateEngineStates[3] < 3 && !this.aircraft.FM.isSentBuryNote())
                Chat.sendLogRnd(3, "gore_lighteng", (Aircraft) paramActor, this.aircraft);
            int j = 0;
            for (int k = 0; k < this.aircraft.FM.EI.getNum(); k++) {
                if (this.astateEngineStates[k] <= 2) continue;
                j++;
            }
            if (j == this.aircraft.FM.EI.getNum()) this.setCockpitState(this.actor, this.astateCockpitState | 0x80);

            this.netToMirrors(1, paramInt1, paramInt2);
        } else this.netToMaster(1, paramInt1, paramInt2, paramActor);
    }

    public void hitEngine(Actor paramActor, int paramInt1, int paramInt2) {
        if (this.astateEngineStates[paramInt1] == 4) return;
        int i = this.astateEngineStates[paramInt1] + paramInt2;
        if (i > 4) i = 4;
        this.setEngineState(paramActor, paramInt1, i);
    }

    public void repairEngine(int engineIndex) {
        if (!this.bIsMaster) return;
        if (this.astateEngineStates[engineIndex] > 0) this.setEngineState(this.actor, engineIndex, this.astateEngineStates[engineIndex] - 1);
        // TODO: +++ By SAS~Storebror: Make sure that the FM's "Operate" flag is set accordingly
        if (!(actor instanceof Aircraft)) return;
        Aircraft aircraft = (Aircraft)this.actor;
        try {
            int part = aircraft.part("Engine" + (engineIndex + 1));
    //        System.out.println("Repair Engine No. " + (engineIndex + 1) + "(Part=" + part + ")");
    //        System.out.println("Operate before: " + Long.toBinaryString(aircraft.FM.Operate));
            aircraft.FM.Operate |= (1L << part);
    //        System.out.println("Operate after: " + Long.toBinaryString(aircraft.FM.Operate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ---
    }

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
    /*
     * public boolean doSetEngineState(Actor paramActor, int paramInt1, int paramInt2) { Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[(paramInt1 + astateEffectsDispEngines)] + "' visibility.."); boolean bool =
     * this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[(paramInt1 + astateEffectsDispEngines)]); Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[(paramInt1 + astateEffectsDispEngines)] + "' is " + (bool ? "visible" :
     * "invisible") + ".."); Aircraft.debugprintln(this.aircraft, "Stating Engine " + paramInt1 + " to state " + paramInt2 + (bool ? ".." : " rejected (missing part)..")); if (!bool) { return false; }
     *
     * if ((this.astateEngineStates[paramInt1] < 4) && (paramInt2 >= 4)) { if (World.getPlayerAircraft() == this.actor) { HUD.log("FailedEngineOnFire"); } this.aircraft.FM.setTakenMortalDamage(true, paramActor); this.aircraft.FM.setCapableOfACM(false);
     * this.aircraft.FM.setCapableOfTaxiing(false); } this.astateEngineStates[paramInt1] = (byte) paramInt2;
     *
     * if ((paramInt2 < 2) && (this.aircraft.FM.isCapableOfBMP())) { this.aircraft.FM.setTakenMortalDamage(false, paramActor); }
     *
     * int j = 0; if (!this.bIsAboveCriticalSpeed) j = 15; for (int i = 0; i < 3; i++) { if (this.astateEngineEffects[paramInt1][i] != null) Eff3DActor.finish(this.astateEngineEffects[paramInt1][i]); this.astateEngineEffects[paramInt1][i] = null; String
     * str = astateEngineStrings[(j + i + paramInt2 * 3)]; if (str != null) { this.astateEngineEffects[paramInt1][i] = Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt1 + 1) + "Smoke"), null, 1.0F, str, -1.0F); } }
     * this.aircraft.sfxSmokeState(1, paramInt1, paramInt2 > 3); return true; }
     */

    public boolean doSetEngineState(Actor engineOwner, int engineIndex, int newEngineState) {
        if (engineIndex >= this.aircraft.FM.EI.getNum()) return false;
        if (engineIndex >= this.astateEngineStates.length) {
            System.out.println("Error in AircraftState.doSetEngineState(" + (engineOwner==null?"null":engineOwner.name()) + ", " + engineIndex + ", " + newEngineState + "): Number of Engines:" + this.aircraft.FM.EI.getNum() + ", EngineStates length:" + this.astateEngineStates.length);
            return false;
        }
        Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[engineIndex + astateEffectsDispEngines] + "' visibility..");
        boolean isEngineDamageVisible = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[engineIndex + astateEffectsDispEngines]);
        Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[engineIndex + astateEffectsDispEngines] + "' is " + (isEngineDamageVisible ? "visible" : "invisible") + "..");
        Aircraft.debugprintln(this.aircraft, "Stating Engine " + engineIndex + " to state " + newEngineState + (isEngineDamageVisible ? ".." : " rejected (missing part).."));
        if (!isEngineDamageVisible) return false;
        if (this.astateEngineStates[engineIndex] < 4 && newEngineState >= 4) {
            if (World.getPlayerAircraft() == this.actor) HUD.log("FailedEngineOnFire");
            if (this.aircraft.isDestroyed() || this.aircraft.FM.Gears.isUnderDeck() || this.aircraft.FM.Gears.getWheelsOnGround() || this.aircraft.FM.Gears.onGround()) {
                this.aircraft.FM.setTakenMortalDamage(true, engineOwner);
                this.aircraft.FM.setCapableOfACM(false);
            }
            if (this.aircraft.FM.EI.getNum() < 4) this.aircraft.FM.setCapableOfACM(false);
            this.aircraft.FM.setCapableOfTaxiing(false);
        }
        this.astateEngineStates[engineIndex] = (byte) newEngineState;
        if (newEngineState < 2 && this.aircraft.FM.isCapableOfBMP()) this.aircraft.FM.setTakenMortalDamage(false, engineOwner);
        byte engineStringOffset = 0;
        if (!this.bIsAboveCriticalSpeed) engineStringOffset = 15;
        for (int engineStateIndex = 0; engineStateIndex < 3; engineStateIndex++) {
            if (this.astateEngineEffects[engineIndex][engineStateIndex] != null) Eff3DActor.finish(this.astateEngineEffects[engineIndex][engineStateIndex]);
            this.astateEngineEffects[engineIndex][engineStateIndex] = null;
            String engineStateEffectName = astateEngineStrings[engineStringOffset + engineStateIndex + newEngineState * 3];
            if (engineStateEffectName != null) {
                Hook engineSmokeHook = this.actor.findHook("_Engine" + (engineIndex + 1) + "Smoke");
                this.astateEngineEffects[engineIndex][engineStateIndex] = Eff3DActor.New(this.actor, engineSmokeHook, null, 1.0F, engineStateEffectName, -1F);
                if (newEngineState > 3) {
                    Loc engineBurnLightLoc = this.aircraft.getEngineBurnLightLoc(engineIndex);
                    Loc engineSmokeLoc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                    engineSmokeHook.computePos(this.actor, engineBurnLightLoc, engineSmokeLoc);
                    Point3d engineSmokePos = engineSmokeLoc.getPoint();
                    boolean showEngineBurnLight = true;
                    for (int tankBurnLightIndex = 0; tankBurnLightIndex < this.astateTankBurnLights.length; tankBurnLightIndex++) {
                        if (this.astateTankBurnLights[tankBurnLightIndex] == null) continue;
                        Point3d tankBurnLightPos = this.astateTankBurnLights[tankBurnLightIndex].relPos;
                        double engineSmokeDistanceToTankBurnLight = engineSmokePos.distance(tankBurnLightPos);
                        if (engineSmokeDistanceToTankBurnLight >= 1.0D) continue;
                        showEngineBurnLight = false;
                        break;
                    }

                    if (showEngineBurnLight) for (int engineBurnLightIndex = 0; engineBurnLightIndex < this.astateEngineBurnLights.length; engineBurnLightIndex++) {
                        if (this.astateEngineBurnLights[engineBurnLightIndex] == null) continue;
                        Point3d engineBurnLightPos = this.astateEngineBurnLights[engineBurnLightIndex].relPos;
                        double engineSmokeDistanceToEngineBurnLight = engineSmokePos.distance(engineBurnLightPos);
                        if (engineSmokeDistanceToEngineBurnLight >= 1.0D) continue;
                        showEngineBurnLight = false;
                        break;
                    }
                    if (showEngineBurnLight) {
                        this.astateEngineBurnLights[engineIndex] = new LightPointActor(new LightPoint(), engineSmokePos);
                        this.astateEngineBurnLights[engineIndex].light.setColor(1.0F, 0.9F, 0.5F);
                        this.astateEngineBurnLights[engineIndex].light.setEmit(5F, 5F);
                        this.actor.draw.lightMap().put("_EngineBurnLight" + engineIndex, this.astateEngineBurnLights[engineIndex]);
                    }
                } else if (this.astateEngineBurnLights[engineIndex] != null) {
                    this.actor.draw.lightMap().remove("_EngineBurnLight" + engineIndex);
                    this.astateEngineBurnLights[engineIndex].destroy();
                    this.astateEngineBurnLights[engineIndex] = null;
                }
            }
        }

        this.aircraft.sfxSmokeState(1, engineIndex, newEngineState > 3);
        return true;
    }
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---

    // TODO: Storebror: 4.14 Backport
//	private void doHitEngine(Actor paramActor, int paramInt) {
//		if (World.Rnd().nextInt(0, 99) < 12) {
//			this.aircraft.FM.setReadyToReturn(true);
//			Aircraft.debugprintln(this.aircraft, "Engines out, RTB..");
//			Explosions.generateComicBulb(this.actor, "RTB", 12.0F);
//		}
//		if (this.astateEngineStates[paramInt] >= 2 && !(this.actor instanceof Scheme1) && World.Rnd().nextInt(0, 99) < 25) {
//			this.aircraft.FM.setReadyToReturn(true);
//			Aircraft.debugprintln(this.aircraft, "One of the engines out, RTB..");
//		}
//
//		if (this.astateEngineStates[paramInt] == 4) if (this.actor instanceof Scheme1) {
//			if (World.Rnd().nextBoolean()) {
//				Aircraft.debugprintln(this.aircraft, "BAILING OUT - Engine " + paramInt + " is on fire..");
//				this.aircraft.hitDaSilk();
//			}
//			this.aircraft.FM.setReadyToDie(true);
//			this.aircraft.FM.setTakenMortalDamage(true, paramActor);
//		} else if (World.Rnd().nextInt(0, 99) < 50) {
//			this.aircraft.FM.setReadyToDie(true);
//			if (World.Rnd().nextInt(0, 99) < 25) this.aircraft.FM.setTakenMortalDamage(true, paramActor);
//			this.aircraft.FM.setCapableOfACM(false);
//
//			Aircraft.debugprintln(this.aircraft, "Engines on fire, ditching..");
//			Explosions.generateComicBulb(this.actor, "OnFire", 12.0F);
//		}
//	}
//

    private void doHitEngine(Actor actor1, int i) {
        float f = 0.0F;
        int j = this.aircraft.FM.EI.getNum();
        for (int k = 0; k < j; k++)
            f += this.astateEngineStates[k];

        f = f / j * 25F;
        if (this.astateEngineStates[i] == 3) {
            if (World.Rnd().nextFloat(0.0F, 100F - f) < 15F - this.aircraft.FM.subSkill * 3F) {
                this.aircraft.FM.setReadyToReturn(true);
                Aircraft.debugprintln(this.aircraft, "Engines out, RTB..");
                Explosions.generateComicBulb(this.actor, "RTB", 12F);
            }
        } else if (this.astateEngineStates[i] == 4) {
            if (World.Rnd().nextFloat(0.0F, 100F - f) < 30F - this.aircraft.FM.subSkill * 3F) {
                this.aircraft.FM.setReadyToReturn(true);
                Aircraft.debugprintln(this.aircraft, "Engines out, RTB..");
                Explosions.generateComicBulb(this.actor, "RTB", 12F);
            }
            if (this.actor instanceof Scheme1) {
                if (World.Rnd().nextInt(0, 99) < 50 - this.aircraft.FM.subSkill * 3) {
                    Aircraft.debugprintln(this.aircraft, "BAILING OUT - Engine " + i + " is on fire..");
                    this.aircraft.hitDaSilk();
                }
                this.aircraft.FM.setReadyToDie(true);
                this.aircraft.FM.setTakenMortalDamage(true, actor1);
                this.aircraft.FM.setCapableOfACM(false);
            } else if (World.Rnd().nextFloat(0.0F, 100F - f) < 50F - this.aircraft.FM.subSkill * 3F) {
                this.aircraft.FM.setReadyToDie(true);
                this.aircraft.FM.setTakenMortalDamage(true, actor1);
                this.aircraft.FM.setCapableOfACM(false);
                Aircraft.debugprintln(this.aircraft, "Engines on fire, ditching..");
                Explosions.generateComicBulb(this.actor, "OnFire", 12F);
            }
        }
    }
    // ---

    // TODO: +++ Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects +++
    /*
     * public void changeEngineEffectBase(int paramInt, Actor paramActor) { for (int i = 0; i < 3; i++) { if (this.astateEngineEffects[paramInt][i] != null) { this.astateEngineEffects[paramInt][i].pos.changeBase(paramActor, null, true); } }
     * this.aircraft.sfxSmokeState(1, paramInt, false); }
     */
    public void changeEngineEffectBase(int engineIndex, Actor engineOwner) {
        if (engineOwner != null) for (int engineEffectIndex = 0; engineEffectIndex < 3; engineEffectIndex++)
            if (this.astateEngineEffects[engineIndex][engineEffectIndex] != null) this.astateEngineEffects[engineIndex][engineEffectIndex].pos.changeBase(engineOwner, null, true);
        if (this.astateEngineBurnLights[engineIndex] != null) {
            this.actor.draw.lightMap().remove("_EngineBurnLight" + engineIndex);
            this.astateEngineBurnLights[engineIndex].destroy();
            this.astateEngineBurnLights[engineIndex] = null;
        }
        this.aircraft.sfxSmokeState(1, engineIndex, false);
        this.astateEngineStates[engineIndex] = 0;
    }
    // TODO: --- Backport from 4.13.4: "Semi Self Illuminating" Engine and Tank Burn Effects ---

    public void explodeEngine(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            if (!this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[paramInt + astateEffectsDispEngines])) return;

            this.netToMirrors(3, paramInt, 0);
            this.doExplodeEngine(paramInt);
        } else this.netToMaster(3, paramInt, 0, paramActor);
    }

    private void doExplodeEngine(int paramInt) {
        Aircraft.debugprintln(this.aircraft, "Engine " + paramInt + " explodes..");
        Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt + 1) + "Smoke"), null, 1.0F, "3DO/Effects/Fireworks/Tank_Burn.eff", -1.0F);

        Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt + 1) + "Smoke"), null, 1.0F, "3DO/Effects/Fireworks/Tank_SmokeBoiling.eff", -1.0F);

        Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt + 1) + "Smoke"), null, 1.0F, "3DO/Effects/Fireworks/Tank_Sparks.eff", -1.0F);

        Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt + 1) + "Smoke"), null, 1.0F, "3DO/Effects/Fireworks/Tank_SparksP.eff", -1.0F);

        this.aircraft.msgCollision(this.aircraft, this.astateEffectChunks[paramInt + astateEffectsDispEngines] + "0", this.astateEffectChunks[paramInt + astateEffectsDispEngines] + "0");
        Actor localActor;
        if (this.aircraft.getDamager() != null) localActor = this.aircraft.getDamager();
        else localActor = this.actor;

        HookNamed localHookNamed = new HookNamed((ActorMesh) this.actor, "_Engine" + (paramInt + 1) + "Smoke");
        Loc localLoc1 = new Loc();
        Loc localLoc2 = new Loc();
        this.actor.pos.getCurrent(localLoc1);
        localHookNamed.computePos(this.actor, localLoc1, localLoc2);

        // TODO: Storebror: 4.14 Backport
        if (this.aircraft.FM.EI.engines[paramInt].getType() != 1 && this.aircraft.FM.EI.engines[paramInt].getType() != 0)
            // ---
            MsgExplosion.send(null, this.astateEffectChunks[astateEffectsDispEngines + paramInt] + "0", localLoc2.getPoint(), localActor, 1.248F, 0.026F, 1, 75.0F);
    }

    public void setEngineStarts(int paramInt) {
        if (!this.bIsMaster) return;
        this.doSetEngineStarts(paramInt);

        this.netToMirrors(4, paramInt, 96);
    }

    public void setEngineRunning(int paramInt) {
        if (!this.bIsMaster) return;
        this.doSetEngineRunning(paramInt);
        this.netToMirrors(5, paramInt, 81);
    }

    public void setEngineStops(int paramInt) {
        if (!this.bIsMaster) return;
        this.doSetEngineStops(paramInt);
        this.netToMirrors(6, paramInt, 2);
    }

    public void setEngineDies(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetEngineDies(paramInt);

            this.netToMirrors(7, paramInt, 77);
        } else this.netToMaster(7, paramInt, 67, paramActor);
    }

    public void setEngineStuck(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.doSetEngineStuck(paramInt);
            this.aircraft.setDamager(paramActor);

            this.netToMirrors(29, paramInt, 77);
        } else this.netToMaster(29, paramInt, 67, paramActor);
    }

    public void setEngineSpecificDamage(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetEngineSpecificDamage(paramInt1, paramInt2);

            this.netToMirrors(2, paramInt1, paramInt2);
        } else this.netToMaster(2, paramInt1, paramInt2, paramActor);
    }

    public void setEngineReadyness(Actor paramActor, int paramInt1, int paramInt2) {
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetEngineReadyness(paramInt1, paramInt2);

            this.netToMirrors(25, paramInt1, paramInt2);
        } else this.netToMaster(25, paramInt1, paramInt2, paramActor);
    }

    public void setEngineStage(Actor paramActor, int paramInt1, int paramInt2) {
//        if (paramActor instanceof FI_103RIV || this.actor instanceof FI_103RIV) {
//            Exception test = new Exception ("setEngineStage(FI_103RIV, " + paramInt1 + ", " + paramInt2 + ")");
//            test.printStackTrace();
//        }
        if (this.bIsMaster) {
            this.doSetEngineStage(paramInt1, paramInt2);

            this.netToMirrors(26, paramInt1, paramInt2);
        } else this.netToMaster(26, paramInt1, paramInt2, paramActor);
    }

    public void setEngineCylinderKnockOut(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetEngineCylinderKnockOut(paramInt1, paramInt2);

            this.netToMirrors(27, paramInt1, paramInt2);
        } else this.netToMaster(27, paramInt1, paramInt2, paramActor);
    }

    public void setEngineMagnetoKnockOut(Actor paramActor, int paramInt1, int paramInt2) {
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetEngineMagnetoKnockOut(paramInt1, paramInt2);

            this.netToMirrors(28, paramInt1, paramInt2);
        } else this.netToMaster(28, paramInt1, paramInt2, paramActor);
    }

    private void doSetEngineStarts(int paramInt) {
        if (paramInt < 0 || paramInt >= this.aircraft.FM.EI.engines.length) {
            System.out.println("AircraftState.doSetEngineStarts(" + paramInt + ") ERROR: Engine No. " + (paramInt + 1) + " doesn't exist on Aircraft " + this.aircraft.typedName());
            return;
        }
        this.aircraft.FM.EI.engines[paramInt].doSetEngineStarts();
    }

    private void doSetEngineRunning(int paramInt) {
        if (paramInt < 0 || paramInt >= this.aircraft.FM.EI.engines.length) {
            System.out.println("AircraftState.doSetEngineRunning(" + paramInt + ") ERROR: Engine No. " + (paramInt + 1) + " doesn't exist on Aircraft " + this.aircraft.typedName());
            return;
        }
        this.aircraft.FM.EI.engines[paramInt].doSetEngineRunning();
    }

    private void doSetEngineStops(int paramInt) {
        if (paramInt < 0 || paramInt >= this.aircraft.FM.EI.engines.length) {
            System.out.println("AircraftState.doSetEngineStops(" + paramInt + ") ERROR: Engine No. " + (paramInt + 1) + " doesn't exist on Aircraft " + this.aircraft.typedName());
            return;
        }
        this.aircraft.FM.EI.engines[paramInt].doSetEngineStops();
    }

    private void doSetEngineDies(int paramInt) {
        if (paramInt < 0 || paramInt >= this.aircraft.FM.EI.engines.length) {
            System.out.println("AircraftState.doSetEngineDies(" + paramInt + ") ERROR: Engine No. " + (paramInt + 1) + " doesn't exist on Aircraft " + this.aircraft.typedName());
            return;
        }
        this.aircraft.FM.EI.engines[paramInt].doSetEngineDies();
    }

    private void doSetEngineStuck(int paramInt) {
        if (paramInt < 0 || paramInt >= this.aircraft.FM.EI.engines.length) {
            System.out.println("AircraftState.doSetEngineStuck(" + paramInt + ") ERROR: Engine No. " + (paramInt + 1) + " doesn't exist on Aircraft " + this.aircraft.typedName());
            return;
        }
        this.aircraft.FM.EI.engines[paramInt].doSetEngineStuck();
    }

    private void doSetEngineSpecificDamage(int paramInt1, int paramInt2) {
        if (paramInt1 < 0 || paramInt1 >= this.aircraft.FM.EI.engines.length) {
            System.out.println("AircraftState.doSetEngineSpecificDamage(" + paramInt1 + ", " + paramInt2 + ") ERROR: Engine No. " + (paramInt1 + 1) + " doesn't exist on Aircraft " + this.aircraft.typedName());
            return;
        }
        switch (paramInt2) {
            case 0:
                this.aircraft.FM.EI.engines[paramInt1].doSetKillCompressor();
                break;
            case 3:
                this.aircraft.FM.EI.engines[paramInt1].doSetKillPropAngleDevice();
                break;
            case 4:
                this.aircraft.FM.EI.engines[paramInt1].doSetKillPropAngleDeviceSpeeds();
                break;
            case 5:
                this.aircraft.FM.EI.engines[paramInt1].doSetExtinguisherFire();
                break;
            case 2:
                System.out.println("AircraftState.doSetEngineSpecificDamage(" + paramInt1 + ", " + paramInt2 + ") ERROR: Feature " + paramInt2 + " not implemented on Aircraft " + this.aircraft.typedName());
                break;
//                throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unimplemented feature (E.S.D./H.)");
            case 1:
                if (!this.aircraft.FM.EI.engines[paramInt1].isHasControlThrottle()) break;
                this.aircraft.FM.EI.engines[paramInt1].doSetKillControlThrottle();
                break;
            case 6:
                if (!this.aircraft.FM.EI.engines[paramInt1].isHasControlProp()) break;
                this.aircraft.FM.EI.engines[paramInt1].doSetKillControlProp();
                break;
            case 7:
                if (!this.aircraft.FM.EI.engines[paramInt1].isHasControlMix()) break;
                this.aircraft.FM.EI.engines[paramInt1].doSetKillControlMix();
                break;
            default:
                System.out.println("AircraftState.doSetEngineSpecificDamage(" + paramInt1 + ", " + paramInt2 + ") ERROR: Feature " + paramInt2 + " not implemented on Aircraft " + this.aircraft.typedName());
                break;
//                throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Corrupt data in (E.S.D./null)");
        }
    }

    private void doSetEngineReadyness(int paramInt1, int paramInt2) {
        // +++ TODO: Storebror: Array boundary Checks added +++
        if (paramInt1 < 0 || paramInt1 >= this.aircraft.FM.EI.engines.length)
            System.out.println("Cannot set Engine Readyness for Engine No." + (paramInt1 + 1) + " of aircraft " + this.actor.name() + " to " + paramInt2 + "% since it only has " + this.aircraft.FM.EI.engines.length + " Engines.");
        else
            // --- TODO: Storebror: Array boundary Checks added ---
            this.aircraft.FM.EI.engines[paramInt1].doSetReadyness(0.01F * paramInt2);
    }

    private void doSetEngineStage(int paramInt1, int paramInt2) {
        // +++ TODO: Storebror: Array boundary Checks added +++
        if (paramInt1 < 0 || paramInt1 >= this.aircraft.FM.EI.engines.length)
            System.out.println("Cannot set Engine Stage for Engine No." + (paramInt1 + 1) + " of aircraft " + this.actor.name() + " to " + paramInt2 + " since it only has " + this.aircraft.FM.EI.engines.length + " Engines.");
        else
            // --- TODO: Storebror: Array boundary Checks added ---
            this.aircraft.FM.EI.engines[paramInt1].doSetStage(paramInt2);
    }

    private void doSetEngineCylinderKnockOut(int paramInt1, int paramInt2) {
        // +++ TODO: Storebror: Array boundary Checks added +++
        if (paramInt1 < 0 || paramInt1 >= this.aircraft.FM.EI.engines.length)
            System.out.println("Cannot set Engine Cylinder Knock Out for Engine No." + (paramInt1 + 1) + " of aircraft " + this.actor.name() + " to " + paramInt2 + "% since it only has " + this.aircraft.FM.EI.engines.length + " Engines.");
        else
            // --- TODO: Storebror: Array boundary Checks added ---
            this.aircraft.FM.EI.engines[paramInt1].doSetCyliderKnockOut(paramInt2);
    }

    private void doSetEngineMagnetoKnockOut(int paramInt1, int paramInt2) {
        // +++ TODO: Storebror: Array boundary Checks added +++
        if (paramInt1 < 0 || paramInt1 >= this.aircraft.FM.EI.engines.length)
            System.out.println("Cannot set Engine Magneto Knock Out for Engine No." + (paramInt1 + 1) + " of aircraft " + this.actor.name() + " to " + paramInt2 + "% since it only has " + this.aircraft.FM.EI.engines.length + " Engines.");
        else
            // --- TODO: Storebror: Array boundary Checks added ---
            this.aircraft.FM.EI.engines[paramInt1].doSetMagnetoKnockOut(paramInt2);
    }

    public void doSetEngineExtinguisherVisuals(int paramInt) {
        Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[paramInt + astateEffectsDispEngines] + "' visibility..");
        boolean bool = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[paramInt + astateEffectsDispEngines]);
        Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[paramInt + astateEffectsDispEngines] + "' is " + (bool ? "visible" : "invisible") + "..");
        Aircraft.debugprintln(this.aircraft, "Firing Extinguisher on Engine " + paramInt + (bool ? ".." : " rejected (missing part).."));
        if (!bool) return;
        Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (paramInt + 1) + "Smoke"), null, 1.0F, "3DO/Effects/Aircraft/EngineExtinguisher1.eff", 3.0F);
    }

    public void setSootState(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (this.astateSootStates[paramInt1] == paramInt2) return;
        if (this.bIsMaster) {
            if (!this.doSetSootState(paramInt1, paramInt2)) return;

            this.netToMirrors(8, paramInt1, paramInt2);
        } else this.netToMaster(8, paramInt1, paramInt2, paramActor);
    }

    public boolean doSetSootState(int paramInt1, int paramInt2) {
        Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[paramInt1 + astateEffectsDispEngines] + "' visibility..");
        boolean bool = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[paramInt1 + astateEffectsDispEngines]);
        Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[paramInt1 + astateEffectsDispEngines] + "' is " + (bool ? "visible" : "invisible") + "..");
        Aircraft.debugprintln(this.aircraft, "Stating Engine " + paramInt1 + " to state " + paramInt2 + (bool ? ".." : " rejected (missing part).."));
        if (!bool) return false;

        this.astateSootStates[paramInt1] = (byte) paramInt2;

        this.aircraft.doSetSootState(paramInt1, paramInt2);
        return true;
    }

    public void changeSootEffectBase(int paramInt, Actor paramActor) {
        if (paramActor == null) return;
        for (int i = 0; i < 2; i++)
            if (this.astateSootEffects[paramInt][i] != null) this.astateSootEffects[paramInt][i].pos.changeBase(paramActor, null, true);
    }

    public void setCockpitState(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.astateCockpitState == paramInt) return;

        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetCockpitState(paramInt);

            this.netToMirrors(23, 0, paramInt);
        } else this.netToMaster(23, 0, paramInt, paramActor);
    }

    public void doSetCockpitState(int paramInt) {
        if (this.astateCockpitState == paramInt) return;

        this.astateCockpitState = paramInt;
        this.aircraft.setCockpitState(paramInt);
    }

    public void setControlsDamage(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            if (this.aircraft.FM.isPlayers() && paramActor != this.actor && paramActor instanceof Aircraft && ((Aircraft) paramActor).isNetPlayer() && !this.aircraft.FM.isSentControlsOutNote() && !this.aircraft.FM.isSentBuryNote()) {
                Chat.sendLogRnd(3, "gore_hitctrls", (Aircraft) paramActor, this.aircraft);
                this.aircraft.FM.setSentControlsOutNote(true);
            }
            this.doSetControlsDamage(paramInt, paramActor);
        } else this.netToMaster(21, 0, paramInt, paramActor);
    }

    public void setInternalDamage(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetInternalDamage(paramInt);

            this.netToMirrors(22, 0, paramInt);
        } else this.netToMaster(22, 0, paramInt, paramActor);
    }

    public void setGliderBoostOn() {
        if (!this.bIsMaster) return;
        this.netToMirrors(12, 5, 5);
    }

    public void setGliderBoostOff() {
        if (!this.bIsMaster) return;
        this.netToMirrors(13, 7, 7);
    }

    public void setGliderCutCart() {
        if (!this.bIsMaster) return;
        this.netToMirrors(14, 9, 9);
    }

    private void doSetControlsDamage(int paramInt, Actor paramActor) {
        switch (paramInt) {
            case 0:
                this.aircraft.FM.CT.resetControl(0);
                if (this.aircraft.FM.isPlayers() && this.aircraft.FM.CT.bHasAileronControl) HUD.log("FailedAroneAU");
                this.aircraft.FM.CT.bHasAileronControl = false;
                this.aircraft.FM.setCapableOfACM(false);
                break;
            case 1:
                this.aircraft.FM.CT.resetControl(1);
                if (this.aircraft.FM.isPlayers() && this.aircraft.FM.CT.bHasElevatorControl) HUD.log("FailedVatorAU");
                this.aircraft.FM.CT.bHasElevatorControl = false;
                this.aircraft.FM.setCapableOfACM(false);
                if (Math.abs(this.aircraft.FM.Vwld.z) <= 7.0D) break;
                this.aircraft.FM.setCapableOfBMP(false, paramActor);
                break;
            case 2:
                this.aircraft.FM.CT.resetControl(2);
                if (this.aircraft.FM.isPlayers() && this.aircraft.FM.CT.bHasRudderControl) HUD.log("FailedRudderAU");
                this.aircraft.FM.CT.bHasRudderControl = false;
                break;
            default:
                throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Corrupt data in (C.A.D./null)");
        }
    }

    private void doSetInternalDamage(int paramInt) {
        switch (paramInt) {
            case 0:
                this.aircraft.FM.Gears.setHydroOperable(false);
                break;
            case 1:
                this.aircraft.FM.CT.bHasFlapsControl = false;
                break;
            case 2:
                this.aircraft.FM.M.nitro = 0.0F;
                break;
            case 3:
                this.aircraft.FM.Gears.setHydroOperable(false);
                this.aircraft.FM.Gears.setOperable(false);
                break;
            case 4:
                if (this.aircraft instanceof DO_335A0) ((DO_335A0) this.aircraft).doKeelShutoff();
                else if (this.aircraft instanceof DO_335V13) ((DO_335V13) this.aircraft).doKeelShutoff();
                else throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (I.K.S.)");

            case 5:
                this.aircraft.FM.EI.engines[1].setPropReductorValue(0.007F);
                break;
            default:
                throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Corrupt data in (I.D./null)");
        }
    }

    private void doSetGliderBoostOn() {
//		if ((this.actor instanceof Scheme0))
//			((Scheme0) this.actor).doFireBoosters();
//		else if ((this.actor instanceof AR_234B2))
//			((AR_234B2) this.actor).doFireBoosters();
//		else
//			throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (G.B./On not in Nul)");

        // TODO: New code to detect availability of "doFireBoosters" method
        // including invocation
        Method doFireBoostersMethod = null;
        try {
            doFireBoostersMethod = this.actor.getClass().getMethod("doFireBoosters", null);
            doFireBoostersMethod.invoke(this.actor, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void doSetGliderBoostOff() {
//		if ((this.actor instanceof Scheme0))
//			((Scheme0) this.actor).doCutBoosters();
//		else if ((this.actor instanceof AR_234B2))
//			((AR_234B2) this.actor).doCutBoosters();
//		else
//			throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (G.B./Off not in Nul)");

        // TODO: New code to detect availability of "doCutBoosters" method
        // including invocation
        Method doCutBoostersMethod = null;
        try {
            doCutBoostersMethod = this.actor.getClass().getMethod("doCutBoosters", null);
            doCutBoostersMethod.invoke(this.actor, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void doSetGliderCutCart() {
        if (this.actor instanceof Scheme0) ((Scheme0) this.actor).doCutCart();
        else throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (G.C.C./Off not in Nul)");
    }

    private void doSetCondensateState(boolean paramBoolean) {
        for (int i = 0; i < this.aircraft.FM.EI.getNum(); i++) {
            if (this.astateCondensateEffects[i] != null) Eff3DActor.finish(this.astateCondensateEffects[i]);
            this.astateCondensateEffects[i] = null;
            if (paramBoolean) {
                String str = astateCondensateStrings[1];
                if (str == null) continue;
                try {
                    this.astateCondensateEffects[i] = Eff3DActor.New(this.actor, this.actor.findHook("_Engine" + (i + 1) + "Smoke"), astateCondensateDispVector, 1.0F, str, -1.0F);
                } catch (Exception localException) {
                    Aircraft.debugprintln(this.aircraft, "Above condensate failed - probably a glider..");
                }
            }
        }
    }

    public void setStallState(boolean paramBoolean) {
        if (this.bIsMaster) {
            this.doSetStallState(paramBoolean);

            this.netToMirrors(16, paramBoolean ? 1 : 0, paramBoolean ? 1 : 0);
        }
    }

    private void doSetStallState(boolean paramBoolean) {
        for (int i = 0; i < 2; i++) {
            if (this.astateStallEffects[i] == null) continue;
            Eff3DActor.finish(this.astateStallEffects[i]);
        }
        if (paramBoolean) {
            String str = astateStallStrings[1];
            if (str != null) {
                if (this.bWingTipLExists) this.astateStallEffects[0] = Eff3DActor.New(this.actor, this.actor.findHook("_WingTipL"), null, 1.0F, str, -1.0F);
                if (this.bWingTipRExists) this.astateStallEffects[1] = Eff3DActor.New(this.actor, this.actor.findHook("_WingTipR"), null, 1.0F, str, -1.0F);
            }
        }
    }

    public void setAirShowState(boolean paramBoolean) {
        if (this.bShowSmokesOn == paramBoolean) return;
        if (this.bIsMaster) {
            this.doSetAirShowState(paramBoolean);

            this.netToMirrors(15, paramBoolean ? 1 : 0, paramBoolean ? 1 : 0);
        }
    }

    private void doSetAirShowState(boolean paramBoolean) {
        this.bShowSmokesOn = paramBoolean;
        for (int i = 0; i < 2; i++) {
            if (this.astateAirShowEffects[i] == null) continue;
            Eff3DActor.finish(this.astateAirShowEffects[i]);
        }
        if (paramBoolean) {
            if (this.bWingTipLExists) this.astateAirShowEffects[0] = Eff3DActor.New(this.actor, this.actor.findHook("_WingTipL"), null, 1.0F, "3DO/Effects/Aircraft/AirShowRedTSPD.eff", -1.0F);

            if (this.bWingTipRExists) this.astateAirShowEffects[1] = Eff3DActor.New(this.actor, this.actor.findHook("_WingTipR"), null, 1.0F, "3DO/Effects/Aircraft/AirShowGreenTSPD.eff", -1.0F);
        }
    }

    public void wantBeaconsNet(boolean paramBoolean) {
        if (paramBoolean == this.bWantBeaconsNet) return;
        this.bWantBeaconsNet = paramBoolean;
        if (World.getPlayerAircraft() != this.actor) return;
        if (this.bWantBeaconsNet) this.setBeacon(this.actor, this.beacon, 0, false);
    }

    public int getBeacon() {
        return this.beacon;
    }

    public void beaconPlus() {
        if (Main.cur().mission.hasBeacons(this.actor.getArmy())) {
            int i = Main.cur().mission.getBeacons(this.actor.getArmy()).size();
            if (this.beacon < i) this.setBeacon(this.beacon + 1);
            else this.setBeacon(0);
        }
    }

    public void beaconMinus() {
        if (Main.cur().mission.hasBeacons(this.actor.getArmy())) {
            int i = Main.cur().mission.getBeacons(this.actor.getArmy()).size();
            if (this.beacon >= 1) this.setBeacon(this.beacon - 1);
            else this.setBeacon(i);
        }
    }

    public void setBeacon(int paramInt) {
        while (paramInt < 0)
            paramInt += 32;
        while (paramInt > 32)
            paramInt -= 32;
        if (paramInt == this.beacon) return;
        this.setBeacon(this.actor, paramInt, 0, false);
    }

    private void setBeacon(Actor paramActor, int paramInt1, int paramInt2, boolean paramBoolean) {
        this.doSetBeacon(paramActor, paramInt1, paramInt2);
        if (Mission.isSingle() || !this.bWantBeaconsNet) return;
        if (!Actor.isValid(paramActor)) return;

        if (this.bIsMaster) this.netToMirrors(42, paramInt1, paramInt2, paramActor);
        else if (!paramBoolean) this.netToMaster(42, paramInt1, paramInt2, paramActor);
    }

    private void doSetBeacon(Actor paramActor, int paramInt1, int paramInt2) {
        if (this.actor != paramActor) return;
        if (World.getPlayerAircraft() != this.actor) return;
        if (paramInt1 > 0) {
            Actor localActor = (Actor) Main.cur().mission.getBeacons(this.actor.getArmy()).get(paramInt1 - 1);
            boolean bool = Aircraft.hasPlaneZBReceiver(this.aircraft);

            if (!bool && (localActor instanceof TypeHasYGBeacon || localActor instanceof TypeHasHayRake)) {
                int i = paramInt1 - this.beacon;
                this.beacon = paramInt1;
                if (i > 0) this.beaconPlus();
                else this.beaconMinus();
                return;
            }

            String str1 = Beacon.getBeaconID(paramInt1 - 1);

            if (this.aircraft.getPilotsCount() == 1 || this.aircraft.FM.EI.engines.length == 1) Main3D.cur3D().ordersTree.setFrequency(null);
            if (localActor instanceof TypeHasYGBeacon) {
                Main3D.cur3D().ordersTree.setFrequency(OrdersTree.FREQ_FRIENDLY);

                HUD.log(hudLogBeaconId, "BeaconYG", new Object[] { str1 });
                this.startListeningYGBeacon();
            } else {
                Object localObject;
                if (localActor instanceof TypeHasHayRake) {
                    Main3D.cur3D().ordersTree.setFrequency(OrdersTree.FREQ_FRIENDLY);

                    HUD.log(hudLogBeaconId, "BeaconYE", new Object[] { str1 });
                    localObject = Main.cur().mission.getHayrakeCodeOfCarrier(localActor);
                    this.startListeningHayrake(localActor, (String) localObject);
                } else if (localActor instanceof TypeHasLorenzBlindLanding) {
                    Main3D.cur3D().ordersTree.setFrequency(OrdersTree.FREQ_FRIENDLY);

                    HUD.log(hudLogBeaconId, "BeaconBA", new Object[] { str1 });
                    this.startListeningLorenzBlindLanding();
                    this.isAAFIAS = false;
                    if (localActor instanceof TypeHasAAFIAS) this.isAAFIAS = true;
                } else if (localActor instanceof TypeHasRadioStation) {
                    localObject = localActor;
                    str1 = ((TypeHasRadioStation) localObject).getStationID();
                    String str2 = Property.stringValue(localActor.getClass(), "i18nName", str1);
                    HUD.log(hudLogBeaconId, "BeaconRS", new Object[] { str2 });
                    this.startListeningRadioStation(str1);
                } else {
                    HUD.log(hudLogBeaconId, "BeaconND", new Object[] { str1 });
                    this.startListeningNDBeacon();
                }
            }
        } else {
            HUD.log(hudLogBeaconId, "BeaconNONE");
            this.stopListeningBeacons();
            Main3D.cur3D().ordersTree.setFrequency(OrdersTree.FREQ_FRIENDLY);
        }
        this.beacon = paramInt1;
    }

    public void startListeningYGBeacon() {
        this.stopListeningLorenzBlindLanding();
        this.stopListeningHayrake();
        this.listenYGBeacon = true;
        this.listenNDBeacon = false;
        this.listenRadioStation = false;
        this.aircraft.playBeaconCarrier(false, 0.0F);
        CmdMusic.setCurrentVolume(0.001F);
    }

    public void startListeningNDBeacon() {
        this.stopListeningLorenzBlindLanding();
        this.listenYGBeacon = false;
        this.listenNDBeacon = true;
        this.listenRadioStation = false;
        this.stopListeningHayrake();
        CmdMusic.setCurrentVolume(0.001F);
    }

    public void startListeningRadioStation(String paramString) {
        this.stopListeningLorenzBlindLanding();
        this.listenYGBeacon = false;
        this.listenNDBeacon = false;
        this.listenRadioStation = true;
        this.stopListeningHayrake();
        String str1 = paramString.replace(' ', '_');

        int i = Mission.curYear();
        int j = Mission.curMonth();
        int k = Mission.curDay();

        String str2 = "" + i;
        String str3 = "";
        String str4 = "";

        if (j < 10) str3 = "0" + j;
        else str3 = "" + j;
        if (k < 10) str4 = "0" + k;
        else str4 = "" + k;
        String[] arrayOfString = { str2 + str3 + str4, str2 + str3 + "XX", str2 + "XX" + str4, str2 + "XXXX" };

        for (int m = 0; m < arrayOfString.length; m++) {
            File localFile = new File("./samples/Music/Radio/" + str1 + "/" + arrayOfString[m]);
            if (!localFile.exists()) continue;
            CmdMusic.setPath("Music/Radio/" + str1 + "/" + arrayOfString[m], true);
            CmdMusic.play();
            return;
        }

        CmdMusic.setPath("Music/Radio/" + str1, true);
        CmdMusic.play();
    }

    public void stopListeningBeacons() {
        this.stopListeningLorenzBlindLanding();
        this.listenYGBeacon = false;
        this.listenNDBeacon = false;
        this.listenRadioStation = false;
        this.stopListeningHayrake();
        this.aircraft.stopMorseSounds();
        CmdMusic.setCurrentVolume(0.001F);
    }

    public void startListeningHayrake(Actor paramActor, String paramString) {
        this.stopListeningLorenzBlindLanding();
        this.hayrakeCarrier = paramActor;
        this.hayrakeCode = paramString;
        this.listenYGBeacon = false;
        this.listenNDBeacon = false;
        this.listenRadioStation = false;
        this.aircraft.stopMorseSounds();
        CmdMusic.setCurrentVolume(0.001F);
    }

    public void stopListeningHayrake() {
        this.hayrakeCarrier = null;
        this.hayrakeCode = null;
    }

    public void startListeningLorenzBlindLanding() {
        this.listenLorenzBlindLanding = true;
        this.stopListeningHayrake();
        this.listenYGBeacon = false;
        this.listenNDBeacon = false;
        this.listenRadioStation = false;
        this.aircraft.stopMorseSounds();
        CmdMusic.setCurrentVolume(0.001F);
    }

    public void stopListeningLorenzBlindLanding() {
        this.listenLorenzBlindLanding = false;
        this.isAAFIAS = false;
        this.aircraft.stopMorseSounds();
    }

    public void preLoadRadioStation(Actor paramActor) {
        TypeHasRadioStation localTypeHasRadioStation = (TypeHasRadioStation) paramActor;
        String str = localTypeHasRadioStation.getStationID();
        this.startListeningRadioStation(str);
    }

    public void setNavLightsState(boolean paramBoolean) {
        if (this.bNavLightsOn == paramBoolean) return;
        if (this.bIsMaster) {
            this.doSetNavLightsState(paramBoolean);

            this.netToMirrors(30, paramBoolean ? 1 : 0, paramBoolean ? 1 : 0);
        }
    }

    private void doSetNavLightsState(boolean paramBoolean) {
        for (int i = 0; i < this.astateNavLightsEffects.length; i++) {
            if (this.astateNavLightsEffects[i] != null) {
                Eff3DActor.finish(this.astateNavLightsEffects[i]);
                this.astateNavLightsLights[i].light.setEmit(0.0F, 0.0F);
            }
            this.astateNavLightsEffects[i] = null;
        }
        if (paramBoolean) for (int i = 0; i < this.astateNavLightsEffects.length; i++) {
            Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[i + astateEffectsDispLights] + "' visibility..");
            boolean bool = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[i + astateEffectsDispLights]);
            Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[i + astateEffectsDispLights] + "' is " + (bool ? "visible" : "invisible") + "..");
            if (bool) {
                this.bNavLightsOn = paramBoolean;
                String str = "3DO/Effects/Fireworks/Flare" + (i <= 1 ? "Red" : i <= 3 ? "Green" : "White") + ".eff";
                this.astateNavLightsEffects[i] = Eff3DActor.New(this.actor, this.actor.findHook("_NavLight" + i), null, 1.0F, str, -1.0F, false);
                this.astateNavLightsLights[i].light.setEmit(0.35F, 8.0F);
            }
        }
        else this.bNavLightsOn = paramBoolean;
    }

    public void changeNavLightEffectBase(int paramInt, Actor paramActor) {
        if (this.astateNavLightsEffects[paramInt] != null) {
            Eff3DActor.finish(this.astateNavLightsEffects[paramInt]);
            this.astateNavLightsLights[paramInt].light.setEmit(0.0F, 0.0F);
            this.astateNavLightsEffects[paramInt] = null;
        }
    }

    public void setLandingLightState(boolean paramBoolean) {
        if (this.bLandingLightOn == paramBoolean) return;
        if (this.bIsMaster) {
            this.doSetLandingLightState(paramBoolean);
            int i = 0;
            for (int j = 0; j < this.astateLandingLightEffects.length; j++)
                if (this.astateLandingLightEffects[j] == null) i++;
            if (i == this.astateLandingLightEffects.length) {
                this.bLandingLightOn = false;
                paramBoolean = false;
            }

            this.netToMirrors(31, paramBoolean ? 1 : 0, paramBoolean ? 1 : 0);
        }
    }

    private void doSetLandingLightState(boolean paramBoolean) {
        this.bLandingLightOn = paramBoolean;
        for (int i = 0; i < this.astateLandingLightEffects.length; i++) {
            if (this.astateLandingLightEffects[i] != null) {
                Eff3DActor.finish(this.astateLandingLightEffects[i]);
                this.astateLandingLightLights[i].light.setEmit(0.0F, 0.0F);
            }
            this.astateLandingLightEffects[i] = null;
        }
        if (paramBoolean) for (int i = 0; i < this.astateLandingLightEffects.length; i++) {
            Aircraft.debugprintln(this.aircraft, "AS: Checking '" + this.astateEffectChunks[i + astateEffectsDispLandingLights] + "' visibility..");
            boolean bool = this.aircraft.isChunkAnyDamageVisible(this.astateEffectChunks[i + astateEffectsDispLandingLights]);
            Aircraft.debugprintln(this.aircraft, "AS: '" + this.astateEffectChunks[i + astateEffectsDispLandingLights] + "' is " + (bool ? "visible" : "invisible") + "..");
            if (bool) {
                String str = "3DO/Effects/Fireworks/FlareWhiteWide.eff";
                this.astateLandingLightEffects[i] = Eff3DActor.New(this.actor, this.actor.findHook("_LandingLight0" + i), null, 1.0F, str, -1.0F);
                this.astateLandingLightLights[i].light.setEmit(1.2F, 8.0F);
            }
        }
    }

    public void changeLandingLightEffectBase(int paramInt, Actor paramActor) {
        if (this.astateLandingLightEffects[paramInt] != null) {
            Eff3DActor.finish(this.astateLandingLightEffects[paramInt]);
            this.astateLandingLightLights[paramInt].light.setEmit(0.0F, 0.0F);
            this.astateLandingLightEffects[paramInt] = null;
            this.bLandingLightOn = false;
        }
    }

    public void setPilotState(Actor paramActor, int paramInt1, int paramInt2) {
        this.setPilotState(paramActor, paramInt1, paramInt2, true);
    }

    public void setPilotState(Actor paramActor, int paramInt1, int paramInt2, boolean paramBoolean) {
        if (!Actor.isValid(paramActor)) return;
        if (paramInt2 > 95) paramInt2 = 100;
        if (paramInt2 < 0) paramInt2 = 0;
        if (this.astatePilotStates[paramInt1] >= paramInt2) return;
        if (this.bIsMaster) {
            this.aircraft.setDamager(paramActor);
            this.doSetPilotState(paramInt1, paramInt2, paramActor);
            if (paramBoolean && this.aircraft.FM.isPlayers() && paramInt1 == this.astatePlayerIndex && !World.isPlayerDead() && paramActor != this.actor && paramActor instanceof Aircraft && ((Aircraft) paramActor).isNetPlayer() && paramInt2 == 100)
                Chat.sendLogRnd(1, "gore_pk", (Aircraft) paramActor, this.aircraft);
            if (paramInt2 > 0 && paramBoolean) this.netToMirrors(17, paramInt1, paramInt2);

        } else if (paramBoolean) this.netToMaster(17, paramInt1, paramInt2, paramActor);
    }

    public void hitPilot(Actor paramActor, int paramInt1, int paramInt2) {
        this.setPilotState(paramActor, paramInt1, this.astatePilotStates[paramInt1] + paramInt2);
    }

    public void setBleedingPilot(Actor paramActor, int paramInt1, int paramInt2) {
        this.setBleedingPilot(paramActor, paramInt1, paramInt2, true);
    }

    public void setBleedingPilot(Actor paramActor, int paramInt1, int paramInt2, boolean paramBoolean) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.doSetBleedingPilot(paramInt1, paramInt2, paramActor);
            if (paramBoolean) this.netToMirrors(47, paramInt1, paramInt2);

        } else if (paramBoolean) this.netToMaster(47, paramInt1, paramInt2, paramActor);
    }

    private void doSetBleedingPilot(int paramInt1, int paramInt2, Actor paramActor) {
        if (Mission.isSingle() || this.aircraft.FM.isPlayers() && !Mission.isServer() || Mission.isNet() && Mission.isServer() && !this.aircraft.isNetPlayer()) {
            // +++ MDS Hotfix by Storebror
            if (paramInt2 == 0) return;
            long l1 = 120000 / paramInt2;
            int i;
            if (this.astateBleedingNext[paramInt1] == 0L) {
                this.astateBleedingNext[paramInt1] = l1;

                if (World.getPlayerAircraft() == this.actor && Config.isUSE_RENDER() && (!this.bIsAboutToBailout || this.astateBailoutStep <= 11) && this.astatePilotStates[paramInt1] < 100) {
                    i = paramInt1 == this.astatePlayerIndex && !World.isPlayerDead() ? 1 : 0;
                    if (paramInt2 > 10) HUD.log(astateHUDPilotHits[this.astatePilotFunctions[paramInt1]] + "BLEED0");
                    else HUD.log(astateHUDPilotHits[this.astatePilotFunctions[paramInt1]] + "BLEED1");
                }
            } else {
                // TODO: Implement 4.10.1 Codechanges +++
//                i = 30000 / (int) this.astateBleedingNext[paramInt1];
//                long l2 = 30000 / (i + paramInt2);
                i = 120000 / (int) this.astateBleedingNext[paramInt1];
                long l2 = 120000 / (i + paramInt2);
                // TODO: Implement 4.10.1 Codechanges ---
                if (l2 < 100L) l2 = 100L;
                this.astateBleedingNext[paramInt1] = l2;
            }
            this.setBleedingTime(paramInt1);
        }
    }

    public boolean bleedingTest(int paramInt) {
        return Time.current() > this.astateBleedingTimes[paramInt];
    }

    public void setBleedingTime(int paramInt) {
        this.astateBleedingTimes[paramInt] = Time.current() + this.astateBleedingNext[paramInt];
    }

    public void doSetWoundPilot(int paramInt) {
        switch (paramInt) {
            case 1:
                this.aircraft.FM.SensRoll *= 0.6F;
                this.aircraft.FM.SensPitch *= 0.6F;
                if (!this.aircraft.FM.isPlayers() || !Config.isUSE_RENDER()) break;
                HUD.log("PlayerArmHit");
                break;
            case 2:
                this.aircraft.FM.SensYaw *= 0.2F;
                if (!this.aircraft.FM.isPlayers() || !Config.isUSE_RENDER()) break;
                HUD.log("PlayerLegHit");
                break;
        }
    }

    public void setPilotWound(Actor paramActor, int paramInt1, int paramInt2) {
        this.setPilotWound(paramActor, paramInt2, true);
    }

    public void setPilotWound(Actor paramActor, int paramInt, boolean paramBoolean) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) {
            this.doSetWoundPilot(paramInt);
            if (paramBoolean) this.netToMirrors(46, 0, paramInt);

        } else if (paramBoolean) this.netToMaster(46, 0, paramInt, paramActor);
    }

    public void woundedPilot(Actor paramActor, int paramInt1, int paramInt2) {
        switch (paramInt1) {
            case 1:
                if (World.Rnd().nextFloat() >= 0.18F || paramInt2 <= 4 || this.armsWounded) break;
                this.setPilotWound(paramActor, paramInt1, true);
                this.armsWounded = true;
                break;
            case 2:
                if (paramInt2 <= 4 || this.legsWounded) break;
                this.setPilotWound(paramActor, paramInt1, true);
                this.legsWounded = true;
                break;
        }
    }

    private void doSetPilotState(int paramInt1, int paramInt2, Actor paramActor) {
        if (paramInt2 > 95) paramInt2 = 100;

        if (World.getPlayerAircraft() == this.actor && Config.isUSE_RENDER() && (!this.bIsAboutToBailout || this.astateBailoutStep <= 11)) {
            int i = paramInt1 == this.astatePlayerIndex && !World.isPlayerDead() ? 1 : 0;
            if (this.astatePilotStates[paramInt1] < 100 && paramInt2 == 100) {
                HUD.log(astateHUDPilotHits[this.astatePilotFunctions[paramInt1]] + "HIT2");
                if (i != 0) {
                    World.setPlayerDead();
                    if (Mission.isNet()) Chat.sendLog(0, "gore_killed", (NetUser) NetEnv.host(), (NetUser) NetEnv.host());
                    if (Main3D.cur3D().cockpits != null && !World.isPlayerGunner()) {
                        int j = Main3D.cur3D().cockpits.length;
                        for (int k = 0; k < j; k++) {
                            Cockpit localCockpit = Main3D.cur3D().cockpits[k];
                            if (!Actor.isValid(localCockpit) || localCockpit.astatePilotIndx() == paramInt1 || this.isPilotDead(localCockpit.astatePilotIndx()) || Mission.isNet() || !AircraftHotKeys.isCockpitRealMode(k)) continue;
                            AircraftHotKeys.setCockpitRealMode(k, false);
                        }
                    }
                }
            } else if (this.astatePilotStates[paramInt1] < 66 && paramInt2 > 66) HUD.log(astateHUDPilotHits[this.astatePilotFunctions[paramInt1]] + "HIT1");
            else if (this.astatePilotStates[paramInt1] < 25 && paramInt2 > 25) HUD.log(astateHUDPilotHits[this.astatePilotFunctions[paramInt1]] + "HIT0");

        }

        int i = this.astatePilotStates[paramInt1];
        this.astatePilotStates[paramInt1] = (byte) paramInt2;
        if (this.bIsAboutToBailout && this.astateBailoutStep > paramInt1 + 11) {
            this.aircraft.doWoundPilot(paramInt1, 0.0F);
            return;
        }
        if (paramInt2 > 99) {
            this.aircraft.doWoundPilot(paramInt1, 0.0F);
            if (World.cur().isHighGore()) this.aircraft.doMurderPilot(paramInt1);
            if (paramInt1 == 0) {
                if (!this.bIsAboutToBailout) Explosions.generateComicBulb(this.actor, "PK", 9.0F);
                FlightModel localFlightModel = this.aircraft.FM;
                if (localFlightModel instanceof Maneuver) {
                    ((Maneuver) localFlightModel).set_maneuver(44);
                    ((Maneuver) localFlightModel).set_task(2);
                    localFlightModel.setCapableOfTaxiing(false);
                }
            }
            if (paramInt1 > 0 && !this.bIsAboutToBailout) Explosions.generateComicBulb(this.actor, "GunnerDown", 9.0F);

            EventLog.onPilotKilled(this.aircraft, paramInt1, paramActor == this.aircraft ? null : paramActor);
        } else if (i < 66 && paramInt2 > 66) EventLog.onPilotHeavilyWounded(this.aircraft, paramInt1);
        else if (i < 25 && paramInt2 > 25) EventLog.onPilotWounded(this.aircraft, paramInt1);
        if (paramInt2 <= 99 && paramInt1 > 0 && World.cur().diffCur.RealisticPilotVulnerability) this.aircraft.doWoundPilot(paramInt1, this.getPilotHealth(paramInt1));
    }

    private void doRemoveBodyFromPlane(int paramInt) {
        this.aircraft.doRemoveBodyFromPlane(paramInt);
    }

    public float getPilotHealth(int paramInt) {
        if (paramInt < 0 || paramInt > this.aircraft.FM.crew - 1) return 0.0F;
        return 1.0F - this.astatePilotStates[paramInt] * 0.01F;
    }

    public boolean isPilotDead(int paramInt) {
        if (paramInt < 0 || paramInt > this.aircraft.FM.crew - 1) return true;
        return this.astatePilotStates[paramInt] == 100;
    }

    public boolean isPilotParatrooper(int paramInt) {
        if (paramInt < 0 || paramInt > this.aircraft.FM.crew - 1) return true;
        return this.astatePilotStates[paramInt] == 100 && this.astateBailoutStep > 11 + paramInt;
    }

    public void setJamBullets(int paramInt1, int paramInt2) {
        if (this.aircraft.FM.CT.Weapons[paramInt1] == null || this.aircraft.FM.CT.Weapons[paramInt1].length <= paramInt2 || this.aircraft.FM.CT.Weapons[paramInt1][paramInt2] == null) return;
        if (this.bIsMaster) {
            this.doSetJamBullets(paramInt1, paramInt2);

            this.netToMirrors(24, paramInt1, paramInt2);
        } else this.netToMaster(24, paramInt1, paramInt2);
    }

    private void doSetJamBullets(int paramInt1, int paramInt2) {
        if (this.aircraft.FM.CT.Weapons != null && this.aircraft.FM.CT.Weapons[paramInt1] != null && this.aircraft.FM.CT.Weapons[paramInt1][paramInt2] != null && this.aircraft.FM.CT.Weapons[paramInt1][paramInt2].haveBullets()) {
            if (this.actor == World.getPlayerAircraft() && this.aircraft.FM.CT.Weapons[paramInt1][paramInt2].haveBullets())
                if (this.aircraft.FM.CT.Weapons[paramInt1][paramInt2].bulletMassa() < 0.095F) HUD.log(paramInt1 > 9 ? "FailedTMGun" : "FailedMGun");
                else HUD.log("FailedCannon");
            this.aircraft.FM.CT.Weapons[paramInt1][paramInt2].loadBullets(0);
        }
    }

    public boolean hitDaSilk() {
        if (!this.bIsMaster) return false;
        if (this.bIsAboutToBailout) return false;
        if (bCheckPlayerAircraft && this.actor == World.getPlayerAircraft()) return false;
        if (!this.bIsEnableToBailout) return false;
        this.bIsAboutToBailout = true;
        FlightModel localFlightModel = this.aircraft.FM;
        Aircraft.debugprintln(this.aircraft, "I've had it, bailing out..");
        Explosions.generateComicBulb(this.actor, "Bailing", 5.0F);
        if (localFlightModel instanceof Maneuver) {
            ((Maneuver) localFlightModel).set_maneuver(44);
            ((Maneuver) localFlightModel).set_task(2);
            localFlightModel.setTakenMortalDamage(true, null);
        }
        return true;
    }

    public void setFlatTopString(Actor paramActor, int paramInt) {
        if (this.bIsMaster) this.netToMirrors(36, paramInt, paramInt, paramActor);
    }

    private void doSetFlatTopString(Actor paramActor, int paramInt) {
        if (Actor.isValid(paramActor) && paramActor instanceof BigshipGeneric && ((BigshipGeneric) paramActor).getAirport() != null) {
            BigshipGeneric localBigshipGeneric = (BigshipGeneric) paramActor;
            if (paramInt >= 0 && paramInt < 255) localBigshipGeneric.forceTowAircraft(this.aircraft, paramInt);
            else localBigshipGeneric.requestDetowAircraft(this.aircraft);
        }
    }

    public void setFMSFX(Actor paramActor, int paramInt1, int paramInt2) {
        if (!Actor.isValid(paramActor)) return;
        if (this.bIsMaster) this.doSetFMSFX(paramInt1, paramInt2);
        else this.netToMaster(37, paramInt1, paramInt2, paramActor);
    }

    private void doSetFMSFX(int paramInt1, int paramInt2) {
        this.aircraft.FM.doRequestFMSFX(paramInt1, paramInt2);
    }

    public void setWingFold(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (paramActor != this.aircraft) return;
        if (!this.aircraft.FM.CT.bHasWingControl) return;
        if (this.aircraft.FM.CT.wingControl == paramInt) return;
        if (!this.bIsMaster) return;
        this.doSetWingFold(paramInt);

        this.netToMirrors(38, paramInt, paramInt);
    }

    private void doSetWingFold(int paramInt) {
        this.aircraft.FM.CT.wingControl = paramInt;
    }

    public void setCockpitDoor(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (paramActor != this.aircraft) return;
        if (!this.aircraft.FM.CT.bHasCockpitDoorControl) return;
        if (this.aircraft.FM.CT.cockpitDoorControl == paramInt) return;
        if (!this.bIsMaster) return;
        this.doSetCockpitDoor(paramInt);

        this.netToMirrors(39, paramInt, paramInt);
    }

    private void doSetCockpitDoor(int paramInt) {
        this.aircraft.FM.CT.cockpitDoorControl = paramInt;
        if (paramInt == 0 && Main.cur() instanceof Main3D && this.aircraft == World.getPlayerAircraft() && HookPilot.current != null) HookPilot.current.doUp(false);
    }

    public void setArrestor(Actor paramActor, int paramInt) {
        if (!Actor.isValid(paramActor)) return;
        if (paramActor != this.aircraft) return;
        if (!this.aircraft.FM.CT.bHasArrestorControl) return;
        if (this.aircraft.FM.CT.arrestorControl == paramInt) return;
        if (!this.bIsMaster) return;
        this.doSetArrestor(paramInt);

        this.netToMirrors(40, paramInt, paramInt);
    }

    private void doSetArrestor(int paramInt) {
        this.aircraft.FM.CT.arrestorControl = paramInt;
    }

    public void update(float paramFloat) {
        // TODO: Cheater Protection
        loadCheaters();
        // ---
        int i;
        if (World.cur().diffCur.RealisticPilotVulnerability) for (i = 0; i < this.aircraft.FM.crew; i++) {
            if (this.astateBleedingNext[i] > 0L && this.bleedingTest(i)) {
                this.hitPilot(this.actor, i, 1);
                if (this.astatePilotStates[i] > 96) this.astateBleedingNext[i] = 0L;
                this.setBleedingTime(i);
            }

            if (this.astatePilotStates[0] > 60) this.aircraft.FM.setCapableOfBMP(false, this.actor);

        }

        bCriticalStatePassed = this.bIsAboveCriticalSpeed != this.aircraft.getSpeed(null) > 10.0D;

        if (bCriticalStatePassed) {
            this.bIsAboveCriticalSpeed = this.aircraft.FM.getSpeed() > astateEffectCriticalSpeed;
            for (i = 0; i < this.aircraft.FM.MaxNumFuelTank; i++)
                this.doSetTankState(null, i, this.astateTankStates[i]);
            for (i = 0; i < this.aircraft.FM.EI.getNum(); i++)
                this.doSetEngineState(null, i, this.astateEngineStates[i]);
            for (i = 0; i < this.aircraft.FM.EI.getNum(); i++)
                this.doSetOilState(i, this.astateOilStates[i]);
        }

        bCriticalStatePassed = this.bIsAboveCondensateAlt != this.aircraft.FM.getAltitude() > astateCondensateCriticalAlt;

        if (bCriticalStatePassed) {
            this.bIsAboveCondensateAlt = this.aircraft.FM.getAltitude() > astateCondensateCriticalAlt;
            this.doSetCondensateState(this.bIsAboveCondensateAlt);
        }

        bCriticalStatePassed = this.bIsOnInadequateAOA != (this.aircraft.FM.getSpeed() > 17.0F && this.aircraft.FM.getAOA() > 15.0F - this.aircraft.FM.getAltitude() * 0.001F);

        if (bCriticalStatePassed) {
            this.bIsOnInadequateAOA = this.aircraft.FM.getSpeed() > 17.0F && this.aircraft.FM.getAOA() > 15.0F - this.aircraft.FM.getAltitude() * 0.001F;
            this.setStallState(this.bIsOnInadequateAOA);
        }

        if (this.bIsMaster) {
            float f = 0.0F;
            for (i = 0; i < this.aircraft.FM.MaxNumFuelTank; i++)
                // TODO: Storebror: 4.14 Backport
                f += (float) 4 / (float) this.aircraft.FM.MaxNumFuelTank * this.astateTankStates[i] * this.astateTankStates[i];
//				f += this.astateTankStates[i] * this.astateTankStates[i];
            // ---

            if (f > 144F) f = 144F;

            this.aircraft.FM.M.requestFuel(f * 0.12F * paramFloat);

            for (i = 0; i < this.aircraft.FM.MaxNumFuelTank; i++)
                switch (this.astateTankStates[i]) {
                    case 1:
                        if (World.Rnd().nextFloat(0.0F, 1.0F) >= 0.0125F) break;
                        this.repairTank(i);
                        Aircraft.debugprintln(this.aircraft, "Tank " + i + " protector clothes the hole - leak stops..");
                    case 2:
                        if (this.aircraft.FM.M.fuel <= 0.0F) {
                            this.repairTank(i);
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " runs out of fuel - leak stops..");
                        }
                        break;
                    case 4:
                        // TODO: Storebror: 4.14 Backport
                        if (World.Rnd().nextFloat(0.0F, 1.0F) < 0.06F) this.hitTank(this.aircraft, i, 0);
                        // ---
                        if (World.Rnd().nextFloat(0.0F, 1.0F) < 0.00333F) {
                            this.hitTank(this.aircraft, i, 1);
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " catches fire..");
                        }
                        break;
                    case 5:
                        // TODO: Storebror: 4.14 Backport
//						if (this.aircraft.FM.getSpeed() > 111.0F && World.Rnd().nextFloat(0.0F, 1.0F) < 0.2F) {
                        if (this.aircraft.FM.getSpeed() > 90F && World.Rnd().nextFloat() < Aircraft.cvt(this.aircraft.FM.getSpeed(), 90F, 200F, 0.0F, 0.2F)) {
                            // ---
                            this.repairTank(i);
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " cuts fire..");
                        }
                        if (World.Rnd().nextFloat() < 0.0048F) {
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " fires up to the next stage..");
                            this.hitTank(this.aircraft, i, 1);
                        }
                        // TODO: Storebror: 4.14 Backport
//						if (!(this.actor instanceof Scheme1) || this.astateTankEffects[i][0] == null || Math.abs(this.astateTankEffects[i][0].pos.getRelPoint().y) >= 1.9D
//								|| this.astateTankEffects[i][0].pos.getRelPoint().x <= -2.6D)
//							continue;
//						if (this.astatePilotStates[0] < 96) {
//							this.hitPilot(this.actor, 0, 5);
//							if (this.astatePilotStates[0] >= 96) {
//								this.hitPilot(this.actor, 0, 101 - this.astatePilotStates[0]);
//								if (this.aircraft.FM.isPlayers() && Mission.isNet() && !this.aircraft.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_burnedcpt", this.aircraft, null);
//							}
//						}
                        if (World.Rnd().nextFloat() < 0.002F) {
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " EXPLODES!.");
                            this.explodeTank(this.aircraft, i);
                        }
                        if (!(this.actor instanceof Scheme1)) break;
                        Hook hook = this.actor.findHook("_Tank" + (i + 1) + "Burn");
                        if (!hook.chunkName().toLowerCase().startsWith("cf") || this.astatePilotStates[0] >= 96) break;
                        this.hitPilot(this.actor, 0, 5);
                        if (this.astatePilotStates[0] < 96) break;
                        this.hitPilot(this.actor, 0, 101 - this.astatePilotStates[0]);
                        if (this.aircraft.FM.isPlayers() && Mission.isNet() && !this.aircraft.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_burnedcpt", this.aircraft, null);

                        // ---
                        break;
                    case 6:
                        // TODO: Storebror: 4.14 Backport
//						if (this.aircraft.FM.getSpeed() > 140.0F && World.Rnd().nextFloat(0.0F, 1.0F) < 0.05F) {
//							this.repairTank(i);
//							Aircraft.debugprintln(this.aircraft, "Tank " + i + " cuts fire..");
//						}
//						if (World.Rnd().nextFloat() < 0.02F) {
//							Aircraft.debugprintln(this.aircraft, "Tank " + i + " EXPLODES!.");
//							this.explodeTank(this.aircraft, i);
//						}
//						if (!(this.actor instanceof Scheme1) || this.astateTankEffects[i][0] == null || Math.abs(this.astateTankEffects[i][0].pos.getRelPoint().y) >= 1.9D
//								|| this.astateTankEffects[i][0].pos.getRelPoint().x <= -2.6D)
//							continue;
//						if (this.astatePilotStates[0] < 96) {
//							this.hitPilot(this.actor, 0, 7);
//							if (this.astatePilotStates[0] >= 96) {
//								this.hitPilot(this.actor, 0, 101 - this.astatePilotStates[0]);
//								if (this.aircraft.FM.isPlayers() && Mission.isNet() && !this.aircraft.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_burnedcpt", this.aircraft, null);
//							}
//						}
                        if (this.aircraft.FM.getSpeed() > 90F && World.Rnd().nextFloat() < Aircraft.cvt(this.aircraft.FM.getSpeed(), 90F, 200F, 0.0F, 0.1F)) {
                            this.repairTank(i);
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " cuts fire..");
                        }
                        if (World.Rnd().nextFloat() < 0.0075F) {
                            Aircraft.debugprintln(this.aircraft, "Tank " + i + " EXPLODES!.");
                            this.explodeTank(this.aircraft, i);
                        }
                        if (!(this.actor instanceof Scheme1)) break;
                        Hook hook1 = this.actor.findHook("_Tank" + (i + 1) + "Burn");
                        if (!hook1.chunkName().toLowerCase().startsWith("cf") || this.astatePilotStates[0] >= 96) break;
                        this.hitPilot(this.actor, 0, 7);
                        if (this.astatePilotStates[0] < 96) break;
                        this.hitPilot(this.actor, 0, 101 - this.astatePilotStates[0]);
                        if (this.aircraft.FM.isPlayers() && Mission.isNet() && !this.aircraft.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_burnedcpt", this.aircraft, null);
                        // ---
                        break;
                    case 3:
                    default:
                        break;
                }

            for (i = 0; i < this.aircraft.FM.EI.getNum(); i++) {

                // TODO: Storebror: 4.14 Backport
                int iEngineStage = this.aircraft.FM.EI.engines[i].getStage();
                //

                if (this.astateEngineStates[i] > Motor._E_STAGE_WAKE_UP) {
                    // TODO: Storebror: 4.14 Backport
                    float f2 = 1.0F;
                    if (this.astateEngineStates[i] > Motor._E_STAGE_CATCH_UP) f2 = 15F;
                    this.aircraft.FM.EI.engines[i].setReadyness(this.actor, this.aircraft.FM.EI.engines[i].getReadyness() - this.astateEngineStates[i] * f2 * 0.00025F * paramFloat);
//                    this.aircraft.FM.EI.engines[i].setReadyness(this.actor, this.aircraft.FM.EI.engines[i].getReadyness() - this.astateEngineStates[i] * 0.00025F * paramFloat);
                    // ---
                    if (this.aircraft.FM.EI.engines[i].getReadyness() < 0.2F && this.aircraft.FM.EI.engines[i].getReadyness() != 0.0F) this.aircraft.FM.EI.engines[i].setEngineDies(this.actor);
                }

                switch (this.astateEngineStates[i]) {
                    case 3:
                        // TODO: Storebror: 4.14 Backport
                        // if (World.Rnd().nextFloat(0.0F, 1.0F) >= 0.01F) break;
                        if (iEngineStage == Motor._E_STAGE_NOMINAL && World.Rnd().nextFloat() < Aircraft.cvt(this.aircraft.FM.EI.engines[i].getEngineLoad(), 1.0F, 3F, 0.015F, 0.0F)) {
                            // ---
                            this.hitEngine(this.aircraft, i, 1);
                            Aircraft.debugprintln(this.aircraft, "Engine " + i + " catches fire..");
                            // TODO: Storebror: 4.14 Backport
                        } else if (iEngineStage > Motor._E_STAGE_NULL && iEngineStage < Motor._E_STAGE_NOMINAL) {
                            if (World.Rnd().nextFloat() < 0.2F) this.aircraft.FM.EI.engines[i].setEngineDies(this.actor);
                        } else if (iEngineStage != Motor._E_STAGE_NOMINAL && World.Rnd().nextFloat() < 0.0075D) this.repairEngine(i);
                        // ---
                        break;
                    case 4:
                        // TODO: Storebror: 4.14 Backport
//						if (this.aircraft.FM.getSpeed() > 111.0F && World.Rnd().nextFloat(0.0F, 1.0F) < 0.15F) {
                        if (this.aircraft.FM.getSpeed() > 80F && World.Rnd().nextFloat() < Aircraft.cvt(this.aircraft.FM.getSpeed(), 80F, 200F, 0.0F, 0.3F)) {
                            // ---
                            this.repairEngine(i);
                            Aircraft.debugprintln(this.aircraft, "Engine " + i + " cuts fire..");
                        }
                        // TODO: Storebror: 4.14 Backport
//						if (this.actor instanceof Scheme1 && World.Rnd().nextFloat() < 0.06F) {
//							Aircraft.debugprintln(this.aircraft, "Engine 0 detonates and explodes, fatal damage level forced..");
//							this.aircraft.msgCollision(this.actor, "CF_D0", "CF_D0");
//						}
//						if (!(this.actor instanceof Scheme1) || this.astatePilotStates[0] >= 96) break;
//						this.hitPilot(this.actor, 0, 4);
//						if (this.astatePilotStates[0] < 96) break;
//						this.hitPilot(this.actor, 0, 101 - this.astatePilotStates[0]);
//						if (!this.aircraft.FM.isPlayers() || !Mission.isNet() || this.aircraft.FM.isSentBuryNote()) break;
//						Chat.sendLogRnd(3, "gore_burnedcpt", this.aircraft, null);

                        if (this.actor instanceof Scheme1 && !(this.actor instanceof P_39) && !(this.actor instanceof MBR_2AM34) && this.astatePilotStates[0] < 96) {
                            this.hitPilot(this.actor, 0, 4);
                            if (this.astatePilotStates[0] >= 96) {
                                this.hitPilot(this.actor, 0, 101 - this.astatePilotStates[0]);
                                if (this.aircraft.FM.isPlayers() && Mission.isNet() && !this.aircraft.FM.isSentBuryNote()) Chat.sendLogRnd(3, "gore_burnedcpt", this.aircraft, null);
                            }
                        }
                        if (this.aircraft.FM.EI.engines[i].getType() == Motor._E_TYPE_RADIAL || this.aircraft.FM.EI.engines[i].getType() == Motor._E_TYPE_INLINE) {
                            if (iEngineStage == Motor._E_STAGE_NOMINAL) {
                                float f3 = Aircraft.cvt(this.aircraft.FM.EI.engines[i].getEngineLoad(), 1.0F, 5F, 0.01F, 0.0F);
                                if (this.aircraft.FM.EI.engines[i].getControlAfterburner()) f3 *= 8F;
                                if (World.Rnd().nextFloat() < f3) if (this.actor instanceof Scheme1) {
                                    if (World.Rnd().nextFloat() < 0.05D) {
                                        Aircraft.debugprintln(this.aircraft, "Engine 0 detonates and explodes, fatal damage level forced..");
                                        this.aircraft.msgCollision(this.actor, "CF_D0", "CF_D0");
                                    } else {
                                        this.aircraft.FM.EI.engines[i].setEngineDies(this.actor);
                                        this.explodeEngine(this.actor, i);
                                    }
                                } else {
                                    this.aircraft.FM.EI.engines[i].setEngineDies(this.actor);
                                    this.explodeEngine(this.actor, i);
                                }
                            } else if (iEngineStage > Motor._E_STAGE_NULL && iEngineStage < Motor._E_STAGE_NOMINAL) {
                                if (World.Rnd().nextFloat() < 0.3F) this.aircraft.FM.EI.engines[i].setEngineDies(this.actor);
                            } else if (iEngineStage != Motor._E_STAGE_NOMINAL && World.Rnd().nextFloat() < 0.15F) {
                                this.repairEngine(i);
                                Aircraft.debugprintln(this.aircraft, "Engine " + i + " cuts fire..");
                            }
                        } else if (this.actor instanceof Scheme1 && World.Rnd().nextFloat() < 0.05F) {
                            Aircraft.debugprintln(this.aircraft, "Engine 0 detonates and explodes, fatal damage level forced..");
                            this.aircraft.msgCollision(this.actor, "CF_D0", "CF_D0");
                        }
                        break;
                    // ---
                }

                if (this.astateOilStates[i] > 0) this.aircraft.FM.EI.engines[i].setReadyness(this.aircraft, this.aircraft.FM.EI.engines[i].getReadyness() - 0.001875F * paramFloat);

            }

            // TODO: Storebror: 4.14 Backport
//			if (World.Rnd().nextFloat() < 0.25F && !this.aircraft.FM.CT.saveWeaponControl[3] && (!(this.actor instanceof com.maddox.il2.objects.air.TypeBomber) || this.aircraft.FM.isReadyToReturn()
//					|| this.aircraft.FM.isPlayers() && this.aircraft.FM instanceof RealFlightModel && ((RealFlightModel) this.aircraft.FM).isRealMode()) &&
//			// TODO: Added by |ZUTI| for bomb bay door mod
//			// -------------------------------------------
//					!this.aircraft.FM.CT.bHasBayDoors
//			// -------------------------------------------
//			) this.aircraft.FM.CT.BayDoorControl = 0.0F;
            if (World.Rnd().nextFloat() < 0.25F && !this.aircraft.FM.CT.saveWeaponControl[3]) if (this.aircraft.FM.isPlayers() && this.aircraft.FM instanceof RealFlightModel && ((RealFlightModel) this.aircraft.FM).isRealMode()) {
                if (!this.aircraft.FM.CT.bHasBayDoorControl) this.aircraft.FM.CT.BayDoorControl = 0.0F;
            } else if (!(this.actor instanceof TypeBomber) || this.aircraft.FM.isReadyToReturn()) this.aircraft.FM.CT.BayDoorControl = 0.0F;
            // ---

            this.bailout();
        } else {
            // TODO: Storebror: 4.14 Backport
            if (this.aircraft.FM instanceof RealFlightModel && ((RealFlightModel) this.aircraft.FM).isRealMode()) return;
            if (this.aircraft.isNet() && this.aircraft.isNetPlayer()) return;
            // ---
            if (World.Rnd().nextFloat() < 0.125F && !this.aircraft.FM.CT.saveWeaponControl[3] && (!(this.actor instanceof TypeBomber) || this.aircraft.FM.AP.way.curr().Action != 3) &&
            // TODO: Added by |ZUTI| for bomb bay door mod
            // -------------------------------------------
                    !this.aircraft.FM.CT.bHasBayDoors
            // -------------------------------------------
            ) this.aircraft.FM.CT.BayDoorControl = 0.0F;
        }
    }

    private void bailout() {
        if (this.bIsAboutToBailout) if (this.astateBailoutStep >= 0 && this.astateBailoutStep < 2) {
            if (this.aircraft.FM.CT.cockpitDoorControl > 0.5F && this.aircraft.FM.CT.getCockpitDoor() > 0.5F) {
                this.astateBailoutStep = 11;
                this.doRemoveBlisters();
            } else this.astateBailoutStep = 2;
        } else if (this.astateBailoutStep >= 2 && this.astateBailoutStep <= 3) {
            switch (this.astateBailoutStep) {
                case 2:
                    if (this.aircraft.FM.CT.cockpitDoorControl >= 0.5F) break;
                    this.doRemoveBlister1();
                    break;
                case 3:
                    this.doRemoveBlisters();
            }

            if (this.bIsMaster) this.netToMirrors(20, this.astateBailoutStep, 1);
            this.astateBailoutStep = (byte) (this.astateBailoutStep + 1);
            if (this.astateBailoutStep == 3 && this.actor instanceof P_39) this.astateBailoutStep = (byte) (this.astateBailoutStep + 1);
            if (this.astateBailoutStep == 4) this.astateBailoutStep = 11;
        } else if (this.astateBailoutStep >= 11 && this.astateBailoutStep <= 19) {
            float f1 = this.aircraft.FM.getSpeed();
            float f2 = (float) this.aircraft.FM.Loc.z;
            float f3 = 140.0F;
            if (this.aircraft instanceof HE_162 || this.aircraft instanceof GO_229 || this.aircraft instanceof ME_262HGII || this.aircraft instanceof DO_335) f3 = 9999.9004F;
            else {
                float hurtProbability = Aircraft.cvt(Pitot.Indicator(f2, f1) / f3, 1.0F, 2.0F, 0.0F, 1.0F);
                if (hurtProbability > World.Rnd().nextFloat()) this.setPilotWound(this.actor, World.Rnd().nextInt(1, 2), true);
                f3 = 9999.9004F;
            }
            if (Pitot.Indicator(f2, f1) < f3 && this.aircraft.FM.getOverload() < 2.0F || !this.bIsMaster) {
                int i = this.astateBailoutStep;
                if (this.bIsMaster) this.netToMirrors(20, this.astateBailoutStep, 1);
                this.astateBailoutStep = (byte) (this.astateBailoutStep + 1);
                if (i == 11) {
                    this.aircraft.FM.setTakenMortalDamage(true, null);
                    if (this.aircraft.FM instanceof Maneuver && ((Maneuver) this.aircraft.FM).get_maneuver() != 44) {
                        World.cur();
                        if (this.actor != World.getPlayerAircraft()) ((Maneuver) this.aircraft.FM).set_maneuver(44);
                    }
                }
                if (this.astatePilotStates[i - 11] < 99) {
                    this.doRemoveBodyFromPlane(i - 10);
                    if (i == 11) {
                        if (this.aircraft instanceof HE_162) {
                            ((HE_162) this.aircraft).doEjectCatapult();
                            this.astateBailoutStep = 51;
                            this.aircraft.FM.setTakenMortalDamage(true, null);
                            this.aircraft.FM.CT.WeaponControl[0] = false;
                            this.aircraft.FM.CT.WeaponControl[1] = false;
                            this.astateBailoutStep = -1;
                            return;
                        }
                        if (this.aircraft instanceof GO_229) {
                            ((GO_229) this.aircraft).doEjectCatapult();
                            this.astateBailoutStep = 51;
                            this.aircraft.FM.setTakenMortalDamage(true, null);
                            this.aircraft.FM.CT.WeaponControl[0] = false;
                            this.aircraft.FM.CT.WeaponControl[1] = false;
                            this.astateBailoutStep = -1;
                            return;
                        }
                        if (this.aircraft instanceof DO_335) {
                            ((DO_335) this.aircraft).doEjectCatapult();
                            this.astateBailoutStep = 51;
                            this.aircraft.FM.setTakenMortalDamage(true, null);
                            this.aircraft.FM.CT.WeaponControl[0] = false;
                            this.aircraft.FM.CT.WeaponControl[1] = false;
                            this.astateBailoutStep = -1;
                            return;
                        }
                        if (this.aircraft instanceof ME_262HGII) {
                            ((ME_262HGII) this.aircraft).doEjectCatapult();
                            this.astateBailoutStep = 51;
                            this.aircraft.FM.setTakenMortalDamage(true, null);
                            this.aircraft.FM.CT.WeaponControl[0] = false;
                            this.aircraft.FM.CT.WeaponControl[1] = false;
                            this.astateBailoutStep = -1;
                            return;
                        }
                    }
                    this.setPilotState(this.aircraft, i - 11, 100, false);
                    if (!this.actor.isNet() || this.actor.isNetMaster()) {
                        try {
                            Hook localHook = this.actor.findHook("_ExternalBail0" + (i - 10));
                            if (localHook != null) {
                                Loc localLoc = new Loc(0.0D, 0.0D, 0.0D, World.Rnd().nextFloat(-45.0F, 45.0F), 0.0F, 0.0F);
                                localHook.computePos(this.actor, this.actor.pos.getAbs(), localLoc);
                                new Paratrooper(this.actor, this.actor.getArmy(), i - 11, localLoc, this.aircraft.FM.Vwld);
                                this.aircraft.FM.setTakenMortalDamage(true, null);
                                if (i == 11) {
                                    this.aircraft.FM.CT.WeaponControl[0] = false;
                                    this.aircraft.FM.CT.WeaponControl[1] = false;
                                }
                                if (i > 10 && i <= 19) EventLog.onBailedOut(this.aircraft, i - 11);
                            }
                        } catch (Exception localException) {} finally {}
                        if (this.astateBailoutStep == 19 && this.actor == World.getPlayerAircraft() && !World.isPlayerGunner() && this.aircraft.FM.brakeShoe) MsgDestroy.Post(Time.current() + 1000L, this.aircraft);
                    }
                }
            }
        }
    }

    private final void doRemoveBlister1() {
        if (this.aircraft.hierMesh().chunkFindCheck("Blister1_D0") != -1 && this.getPilotHealth(0) > 0.0F) {
            if (this.aircraft instanceof JU_88NEW) {
                float f = this.aircraft.FM.getAltitude() - Landscape.HQ_Air((float) this.aircraft.FM.Loc.x, (float) this.aircraft.FM.Loc.y);
                if (f < 0.3F) {
                    this.aircraft.blisterRemoved(1);
                    return;
                }
            }

            this.aircraft.hierMesh().hideSubTrees("Blister1_D0");
            Wreckage localWreckage = new Wreckage((ActorHMesh) this.actor, this.aircraft.hierMesh().chunkFind("Blister1_D0"));
            localWreckage.collide(false);
            Vector3d localVector3d = new Vector3d();
            localVector3d.set(this.aircraft.FM.Vwld);
            localWreckage.setSpeed(localVector3d);
            this.aircraft.blisterRemoved(1);
        }
    }

    private final void doRemoveBlisters() {
        for (int i = 2; i < 10; i++)
            if (this.aircraft.hierMesh().chunkFindCheck("Blister" + i + "_D0") != -1 && this.getPilotHealth(i - 1) > 0.0F) {
                this.aircraft.hierMesh().hideSubTrees("Blister" + i + "_D0");
                Wreckage localWreckage = new Wreckage((ActorHMesh) this.actor, this.aircraft.hierMesh().chunkFind("Blister" + i + "_D0"));
                localWreckage.collide(false);
                Vector3d localVector3d = new Vector3d();
                localVector3d.set(this.aircraft.FM.Vwld);
                localWreckage.setSpeed(localVector3d);
                this.aircraft.blisterRemoved(i);
            }
    }

    public void netUpdate(boolean paramBoolean1, boolean paramBoolean2, NetMsgInput paramNetMsgInput) throws IOException {
        int i;
        int j;
        int k;
        Actor localActor;
        NetObj localNetObj;
        if (paramBoolean2) {
            if (paramBoolean1) {
                i = paramNetMsgInput.readUnsignedByte();
                j = paramNetMsgInput.readUnsignedByte();
                k = paramNetMsgInput.readUnsignedByte();
                localActor = null;
                if (paramNetMsgInput.available() > 0) {
                    localNetObj = paramNetMsgInput.readNetObj();
                    if (localNetObj != null) localActor = (Actor) localNetObj.superObj();
                }
                switch (i) {
                    case 1:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineState(localActor, j, k);
                        break;
                    case 2:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineSpecificDamage(localActor, j, k);
                        break;
                    case 3:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.explodeEngine(localActor, j);
                        break;
                    case 4:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (E.S.)");
                    case 5:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (E.R.)");
                    case 6:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (E.E.)");
                    case 7:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineDies(localActor, j);
                        break;
                    case 29:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineStuck(localActor, j);
                        break;
                    case 27:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineCylinderKnockOut(localActor, j, k);
                        break;
                    case 28:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineMagnetoKnockOut(localActor, j, k);
                        break;
                    case 8:
                        this.setSootState(localActor, j, k);
                        break;
                    case 25:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setEngineReadyness(localActor, j, k);
                        break;
                    case 26:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (EN.ST.)");
                    case 9:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setTankState(localActor, j, k);
                        break;
                    case 10:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.explodeTank(localActor, j);
                        break;
                    case 11:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setOilState(localActor, j, k);
                        break;
                    case 12:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (G.B./On)");
                    case 13:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (G.B./Off)");
                    case 14:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (G.C.C./Off)");
                    case 15:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (A.S.S.)");
                    case 30:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (NV.LT.ST.)");
                    case 31:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (LA.LT.ST.)");
                    case 16:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (W.T.S.)");
                    case 17:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setPilotState(localActor, j, k);
                        break;
                    case 46:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setPilotWound(localActor, j, k);
                        break;
                    case 47:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setBleedingPilot(localActor, j, k);
                        break;
                    case 18:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unimplemented feature (K.P.)");
                    case 19:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unimplemented feature (H.S.)");
                    case 20:
                        throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unexpected command (B.O.)");
                    case 21:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setControlsDamage(localActor, k);
                        break;
                    case 22:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setInternalDamage(localActor, k);
                        break;
                    case 23:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setCockpitState(localActor, k);
                        break;
                    case 24:
                        // TODO: Cheater Protection
                        if (isCheaterHitInactive(localActor)) break;
                        // ---
                        this.setJamBullets(j, k);
                        break;
                    case 34:
                        ((TypeDockable) this.aircraft).typeDockableRequestAttach(localActor, j, true);
                        break;
                    case 35:
                        ((TypeDockable) this.aircraft).typeDockableRequestDetach(localActor, j, true);
                        break;
                    case 32:
                        ((TypeDockable) this.aircraft).typeDockableRequestAttach(localActor, j, k > 0);
                        break;
                    case 33:
                        ((TypeDockable) this.aircraft).typeDockableRequestDetach(localActor, j, k > 0);
                        break;
                    case 37:
                        this.setFMSFX(localActor, j, k);
                        break;
                    case 42:
                        this.setBeacon(localActor, j, k, true);
                        break;
                    case 44:
                        this.setGyroAngle(localActor, j, k, true);
                        break;
                    case 45:
                        this.setSpreadAngle(localActor, j, k, true);
                        break;

                    // +++++ TODO skylla: enhanced weapon release control +++++
                    case _AS_ROCKET_SELECTED: {
                        // System.out.println("SKYLLA: received index '" + j + "' for 'Rocket Selected'!");
                        this.setRocketSelected(localActor, j, true);
                        break;
                    }
                    case _AS_ROCKET_RELEASE_DELAY: {
                        // System.out.println("SKYLLA: received index '" + j + "' for 'Rocket Release Delay'!");
                        this.setRocketReleaseDelay(localActor, j, true);
                        break;
                    }
                    case _AS_ROCKET_RELEASE_MODE: {
                        // System.out.println("SKYLLA: received index '" + j + "' for 'Rocket Release Mode'!");
                        this.setRocketReleaseMode(localActor, j, true);
                        break;
                    }
                    case _AS_BOMB_SELECTED: {
                        // System.out.println("SKYLLA: received index '" + j + "' for 'Bomb Selected'!");
                        this.setBombSelected(localActor, j, true);
                        break;
                    }
                    case _AS_BOMB_RELEASE_DELAY: {
                        // System.out.println("SKYLLA: received index '" + j + "' for 'Bomb Release Delay'!");
                        this.setBombReleaseDelay(localActor, j, true);
                        break;
                    }
                    case _AS_BOMB_RELEASE_MODE: {
                        // System.out.println("SKYLLA: received index '" + j + "' for 'Bomb Release Mode'!");
                        this.setBombReleaseMode(localActor, j, true);
                        break;
                    }
                    // ----- todo skylla: enhanced weapon release control -----
                    
                    // TODO: By SAS~Storebror: Rocket Booster Net Replication +++
                    case _AS_BOOST_STATE: {
                        this.setBoostState(localActor, j);
                    }
                    // TODO: By SAS~Storebror: Rocket Booster Net Replication ---

                    case 36:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 43:
                        break;
                }
            } else this.aircraft.net.postTo(this.aircraft.net.masterChannel(), new NetMsgGuaranted(paramNetMsgInput, paramNetMsgInput.available() > 3 ? 1 : 0));
        } else {
            if (this.aircraft.net.isMirrored()) this.aircraft.net.post(new NetMsgGuaranted(paramNetMsgInput, paramNetMsgInput.available() > 3 ? 1 : 0));

            i = paramNetMsgInput.readUnsignedByte();
            j = paramNetMsgInput.readUnsignedByte();
            k = paramNetMsgInput.readUnsignedByte();
            localActor = null;
            if (paramNetMsgInput.available() > 0) {
                localNetObj = paramNetMsgInput.readNetObj();
                if (localNetObj != null) localActor = (Actor) localNetObj.superObj();
            }
            switch (i) {
                case 1:
                    this.doSetEngineState(localActor, j, k);
                    break;
                case 2:
                    this.doSetEngineSpecificDamage(j, k);
                    break;
                case 3:
                    this.doExplodeEngine(j);
                    break;
                case 4:
                    this.doSetEngineStarts(j);
                    break;
                case 5:
                    this.doSetEngineRunning(j);
                    break;
                case 6:
                    this.doSetEngineStops(j);
                    break;
                case 7:
                    this.doSetEngineDies(j);
                    break;
                case 29:
                    this.doSetEngineStuck(j);
                    break;
                case 27:
                    this.doSetEngineCylinderKnockOut(j, k);
                    break;
                case 28:
                    this.doSetEngineMagnetoKnockOut(j, k);
                    break;
                case 8:
                    this.doSetSootState(j, k);
                    break;
                case 25:
                    this.doSetEngineReadyness(j, k);
                    break;
                case 26:
                    this.doSetEngineStage(j, k);
                    break;
                case 9:
                    this.doSetTankState(localActor, j, k);
                    break;
                case 10:
                    this.doExplodeTank(j);
                    break;
                case 11:
                    this.doSetOilState(j, k);
                    break;
                case 12:
                    if (k == 5) this.doSetGliderBoostOn();
                    else throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Corrupt data in signal (G.S.B./On): (" + i + ", " + j + ", " + k + ")");
                    break;
                case 13:
                    if (k == 7) this.doSetGliderBoostOff();
                    else throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Corrupt data in signal (G.S.B./Off): (" + i + ", " + j + ", " + k + ")");
                    break;
                case 14:
                    if (k == 9) this.doSetGliderCutCart();
                    else throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Corrupt data in signal (G.C.C./Off): (" + i + ", " + j + ", " + k + ")");
                    break;
                case 15:
                    this.doSetAirShowState(k == 1);
                    break;
                case 30:
                    this.doSetNavLightsState(k == 1);
                    break;
                case 31:
                    this.doSetLandingLightState(k == 1);
                    break;
                case 16:
                    this.doSetStallState(k == 1);
                    break;
                case 17:
                    this.doSetPilotState(j, k, localActor);
                    break;
                case 46:
                    this.doSetWoundPilot(k);
                    break;
                case 47:
                    this.doSetBleedingPilot(j, k, localActor);
                    break;
                case 18:
                    throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unimplemented signal (K.P.)");
                case 19:
                    throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Unimplemented signal (H.S.)");
                case 20:
                    this.bIsAboutToBailout = true;
                    this.astateBailoutStep = (byte) j;
                    this.bailout();
                    break;
                case 21:
                    throw new RuntimeException("(" + this.aircraft.typedName() + ") A.S.: Uniexpected signal (C.A.D.)");
                case 22:
                    this.doSetInternalDamage(k);
                    break;
                case 23:
                    this.doSetCockpitState(k);
                    break;
                case 24:
                    this.doSetJamBullets(j, k);
                    break;
                case 34:
                    ((TypeDockable) this.aircraft).typeDockableDoAttachToDrone(localActor, j);
                    break;
                case 35:
                    ((TypeDockable) this.aircraft).typeDockableDoDetachFromDrone(j);
                    break;
                case 32:
                    break;
                case 33:
                    break;
                case 36:
                    this.doSetFlatTopString(localActor, k);
                    break;
                case 37:
                    this.doSetFMSFX(j, k);
                    break;
                case 38:
                    this.doSetWingFold(k);
                    break;
                case 39:
                    this.doSetCockpitDoor(k);
                    break;
                case 40:
                    this.doSetArrestor(k);
                    break;
                case 42:
                    this.doSetBeacon(localActor, j, k);
                    break;
                case 44:
                    this.doSetGyroAngle(localActor, j, k);
                    break;
                case 45:
                    this.doSetSpreadAngle(localActor, j, k);
                    break;

                // +++++ TODO skylla: enhanced weapon release control +++++
                case _AS_ROCKET_SELECTED: {
                    // System.out.println("SKYLLA: received index '" + j + "' for 'Rocket Selected'!");
                    this.doSetRocketSelected(localActor, j);
                    break;
                }
                case _AS_ROCKET_RELEASE_DELAY: {
                    // System.out.println("SKYLLA: received index '" + j + "' for 'Rocket Release Delay'!");
                    this.doSetRocketReleaseDelay(localActor, j);
                    break;
                }
                case _AS_ROCKET_RELEASE_MODE: {
                    // System.out.println("SKYLLA: received index '" + j + "' for 'Rocket Release Mode'!");
                    this.doSetRocketReleaseMode(localActor, j);
                    break;
                }
                case _AS_BOMB_SELECTED: {
                    // System.out.println("SKYLLA: received index '" + j + "' for 'Bomb Selected'!");
                    this.doSetBombSelected(localActor, j);
                    break;
                }
                case _AS_BOMB_RELEASE_DELAY: {
                    // System.out.println("SKYLLA: received index '" + j + "' for 'Bomb Release Delay'!");
                    this.doSetBombReleaseDelay(localActor, j);
                    break;
                }
                case _AS_BOMB_RELEASE_MODE: {
                    // System.out.println("SKYLLA: received index '" + j + "' for 'Bomb Release Mode'!");
                    this.doSetBombReleaseMode(localActor, j);
                    break;
                }
                // ----- todo skylla: enhanced weapon release control -----

                // TODO: By SAS~Storebror: Rocket Booster Net Replication +++
                case _AS_BOOST_STATE: {
                    this.doSetBoostState(j);
                }
                // TODO: By SAS~Storebror: Rocket Booster Net Replication ---

                case 41:
                case 43:
            }
        }
    }

    public void netReplicate(NetMsgGuaranted netMsgGuaranted) throws IOException {
        int j;
        if (this.aircraft instanceof FW_190A8MSTL) j = 1;
        else j = this.aircraft.FM.EI.getNum();
        for (int i = 0; i < j; i++) {
            this.aircraft.FM.EI.engines[i].replicateToNet(netMsgGuaranted);
            netMsgGuaranted.writeByte(this.astateEngineStates[i]);
        }

        for (int i = 0; i < this.aircraft.FM.MaxNumFuelTank; i++)
            netMsgGuaranted.writeByte(this.astateTankStates[i]);

        for (int i = 0; i < j; i++)
            netMsgGuaranted.writeByte(this.astateOilStates[i]);

        netMsgGuaranted.writeByte((this.bShowSmokesOn ? 1 : 0) | (this.bNavLightsOn ? 2 : 0) | (this.bLandingLightOn ? 4 : 0));

        netMsgGuaranted.writeByte(this.astateCockpitState);

        netMsgGuaranted.writeByte(this.astateBailoutStep);

        if (this.aircraft instanceof TypeBomber) ((TypeBomber) this.aircraft).typeBomberReplicateToNet(netMsgGuaranted);
        if (this.aircraft instanceof TypeDockable) ((TypeDockable) this.aircraft).typeDockableReplicateToNet(netMsgGuaranted);

        if (this.aircraft.FM.CT.bHasWingControl) {
            netMsgGuaranted.writeByte((int) this.aircraft.FM.CT.wingControl);
            netMsgGuaranted.writeByte((int) (this.aircraft.FM.CT.getWing() * 255.0F));
        }

        // TODO: +++ Get rid of this "rich mixture smoke" bug just because Cockpit Door Controls are mismatching among clients.
//        if (this.aircraft.FM.CT.bHasCockpitDoorControl) {
        netMsgGuaranted.writeByte((int) this.aircraft.FM.CT.cockpitDoorControl);
//        }
        // TODO: --- Get rid of this "rich mixture smoke" bug just because Cockpit Door Controls are mismatching among clients.

        netMsgGuaranted.writeByte(this.bIsEnableToBailout ? 1 : 0);

        if (this.aircraft.FM.CT.bHasArrestorControl) {
            netMsgGuaranted.writeByte((int) this.aircraft.FM.CT.arrestorControl);
            netMsgGuaranted.writeByte((int) (this.aircraft.FM.CT.getArrestor() * 255.0F));
        }

        for (int i = 0; i < j; i++)
            netMsgGuaranted.writeByte(this.astateSootStates[i]);

        if (this.bWantBeaconsNet) netMsgGuaranted.writeByte(this.beacon);

        if (this.aircraft instanceof TypeHasToKG) {
            netMsgGuaranted.writeByte(this.torpedoGyroAngle);
            netMsgGuaranted.writeByte(this.torpedoSpreadAngle);
        }

        netMsgGuaranted.writeShort(this.aircraft.armingSeed);

        this.setArmingSeeds(this.aircraft.armingSeed);

        if (this.aircraft.isNetPlayer()) {
            int k = (int) Math.sqrt(World.cur().userCoverMashineGun - 100.0F) / 6;
            k += 6 * ((int) Math.sqrt(World.cur().userCoverCannon - 100.0F) / 6);
            k += 36 * ((int) Math.sqrt(World.cur().userCoverRocket - 100.0F) / 6);
            netMsgGuaranted.writeByte(k);
        }

        if (this.externalStoresDropped) netMsgGuaranted.writeByte(1);
        else netMsgGuaranted.writeByte(0);

        // TODO: Added by |ZUTI|: added for multicrew
        // -------------------------------------------------
        netMsgGuaranted.writeBoolean(this.bzutiIsMultiCrew);
        netMsgGuaranted.writeBoolean(this.bzutiIsMultiCrewAnytime);
        // -------------------------------------------------

        // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
        if (this.aircraft.isNetPlayer() || this.isMaster()) {
            short sUserBombFuze = (short) ((this.fUserBombFuze - UserCfg.BOMB_FUZE_MIN) / (UserCfg.BOMB_FUZE_MAX - UserCfg.BOMB_FUZE_MIN) * 255F);
            short sUserBombDelay = (short) ((this.fUserBombDelay - UserCfg.BOMB_DELAY_MIN) / (UserCfg.BOMB_DELAY_MAX - UserCfg.BOMB_DELAY_MIN) * 255F);
            short sUserRocketDelay = (short) ((this.fUserRocketDelay - UserCfg.ROCKET_DELAY_MIN) / (UserCfg.ROCKET_DELAY_MAX - UserCfg.ROCKET_DELAY_MIN) * 255F);
            Bomb.printDebug(this.aircraft, "Replicating Weapon Timing: Bomb Fuze: " + this.fUserBombFuze + " seconds (~Byte:" + sUserBombFuze + "~), Bomb Delay: " + this.fUserBombDelay + " seconds (~Byte:" + sUserBombDelay + "~), Rocket Life Time: "
                    + this.fUserRocketDelay + " seconds (~Byte:" + sUserRocketDelay + "~).");
            netMsgGuaranted.writeByte(sUserBombFuze);
            netMsgGuaranted.writeByte(sUserBombDelay);
            netMsgGuaranted.writeByte(sUserRocketDelay);
        }
        // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication
        
        // TODO: Storebror: +++ Rocket Booster Net Replication
        if (this.actor instanceof TypeRocketBoost) netMsgGuaranted.writeByte(((TypeRocketBoost)this.actor).getBoostState());
        // TODO: Storebror: --- Rocket Booster Net Replication
    }

    public void netFirstUpdate(NetMsgInput netMsgInput) throws IOException {
        int k;
        if (this.aircraft instanceof FW_190A8MSTL) k = 1;
        else k = this.aircraft.FM.EI.getNum();
        for (int i = 0; i < k; i++) {
            this.aircraft.FM.EI.engines[i].replicateFromNet(netMsgInput);
            int j = netMsgInput.readUnsignedByte();
            this.doSetEngineState(null, i, j);
        }

        for (int i = 0; i < this.aircraft.FM.MaxNumFuelTank; i++) {
            int j = netMsgInput.readUnsignedByte();
            this.doSetTankState(null, i, j);
        }

        for (int i = 0; i < k; i++) {
            int j = netMsgInput.readUnsignedByte();
            this.doSetOilState(i, j);
        }

        int j = netMsgInput.readUnsignedByte();
        this.doSetAirShowState((j & 0x1) != 0);
        this.doSetNavLightsState((j & 0x2) != 0);
        this.doSetLandingLightState((j & 0x4) != 0);

        j = netMsgInput.readUnsignedByte();
        this.doSetCockpitState(j);

        j = netMsgInput.readUnsignedByte();
        if (j != 0) {
            this.bIsAboutToBailout = true;
            this.astateBailoutStep = (byte) j;
            for (int i = 1; i <= Math.min(this.astateBailoutStep, 3); i++)
                if (this.aircraft.hierMesh().chunkFindCheck("Blister" + (i - 1) + "_D0") != -1) this.aircraft.hierMesh().hideSubTrees("Blister" + (i - 1) + "_D0");
            if (this.astateBailoutStep >= 11 && this.astateBailoutStep <= 20) {
                int m = this.astateBailoutStep;
                if (this.astateBailoutStep == 20) m = 19;
                m -= 11;
                for (int i = 0; i <= m; i++)
                    this.doRemoveBodyFromPlane(i + 1);
            }
        }
        
        try {
            if (this.aircraft instanceof TypeBomber) ((TypeBomber) this.aircraft).typeBomberReplicateFromNet(netMsgInput);
            if (this.aircraft instanceof TypeDockable) ((TypeDockable) this.aircraft).typeDockableReplicateFromNet(netMsgInput);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (netMsgInput.available() == 0) return;

        if (this.aircraft.FM.CT.bHasWingControl) {
            this.aircraft.FM.CT.wingControl = netMsgInput.readUnsignedByte();
            this.aircraft.FM.CT.forceWing(netMsgInput.readUnsignedByte() / 255.0F);
            this.aircraft.wingfold_ = this.aircraft.FM.CT.getWing();
        }
        if (netMsgInput.available() == 0) return;

        // TODO: +++ Get rid of this "rich mixture smoke" bug just because Cockpit Door Controls are mismatching among clients.
//        if (this.aircraft.FM.CT.bHasCockpitDoorControl) {
//            this.aircraft.FM.CT.cockpitDoorControl = paramNetMsgInput.readUnsignedByte();
//            this.aircraft.FM.CT.forceCockpitDoor(this.aircraft.FM.CT.cockpitDoorControl);
//        }
        int cockpitDoorControl = netMsgInput.readUnsignedByte();
        if (this.aircraft.FM.CT.bHasCockpitDoorControl) {
            this.aircraft.FM.CT.cockpitDoorControl = cockpitDoorControl;
            this.aircraft.FM.CT.forceCockpitDoor(this.aircraft.FM.CT.cockpitDoorControl);
        }
        // TODO: --- Get rid of this "rich mixture smoke" bug just because Cockpit Door Controls are mismatching among clients.
        if (netMsgInput.available() == 0) return;
        this.bIsEnableToBailout = netMsgInput.readUnsignedByte() == 1;
        if (netMsgInput.available() == 0) return;
        if (this.aircraft.FM.CT.bHasArrestorControl) {
            this.aircraft.FM.CT.arrestorControl = netMsgInput.readUnsignedByte();
            this.aircraft.FM.CT.forceArrestor(netMsgInput.readUnsignedByte() / 255.0F);
            this.aircraft.arrestor_ = this.aircraft.FM.CT.getArrestor();
        }
        if (netMsgInput.available() == 0) return;
        for (int i = 0; i < k; i++) {
            j = netMsgInput.readUnsignedByte();
            this.doSetSootState(i, j);
        }

        if (netMsgInput.available() == 0) return;

        if (this.bWantBeaconsNet) this.beacon = netMsgInput.readUnsignedByte();

        if (this.aircraft instanceof TypeHasToKG) {
            this.torpedoGyroAngle = netMsgInput.readUnsignedByte();
            this.torpedoSpreadAngle = netMsgInput.readUnsignedByte();
        }

        this.aircraft.armingSeed = netMsgInput.readUnsignedShort();

        this.setArmingSeeds(this.aircraft.armingSeed);

        if (this.aircraft.isNetPlayer()) {
            int n = netMsgInput.readUnsignedByte();

            int i1 = n % 6;
            float f = Property.floatValue(this.aircraft.getClass(), "LOSElevation", 0.75F);

            this.updConvDist(i1, 0, f);
            n = (n - i1) / 6;
            i1 = n % 6;

            this.updConvDist(i1, 1, f);
            n = (n - i1) / 6;

            this.updConvDist(n, 2, f);
        }

        j = netMsgInput.readUnsignedByte();

        if (!this.externalStoresDropped && j > 0) {
            this.externalStoresDropped = true;
            this.aircraft.dropExternalStores(false);
        }

        // +++ MDS Hotfix by Storebror
        if (netMsgInput.available() < 1) return;
        try {
            // TODO: Added by |ZUTI|: multicrew related code
            // ----------------------------------------------------
            this.bzutiIsMultiCrew = netMsgInput.readBoolean();
            this.bzutiIsMultiCrewAnytime = netMsgInput.readBoolean();
        } catch (Exception e) {
            System.out.println("Player aircraft " + this.aircraft.name() + " has no MultiCrew net parameters!");
        }

        // Request user new net place if plane is multi crew plane because
        // removing it
        // would shuffle AC positions and as a result also netusers net places.
        if (Mission.isDogfight() && this.bzutiIsMultiCrew) ZutiSupportMethods_Multicrew.updateNetUsersCrewPlaces();

        // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
        if (netMsgInput.available() < 1) {
            Bomb.printDebug(this.aircraft, "No Weapon Timing Data available");
            return;
        }
        if (this.aircraft.isNetPlayer() || !this.isMaster()) {
            short sUserBombFuze = 0;
            if (netMsgInput.available() >= 2) sUserBombFuze = (short) (netMsgInput.readByte() & 0xFF);
            short sUserBombDelay = 0;
            if (netMsgInput.available() >= 2) sUserBombDelay = (short) (netMsgInput.readByte() & 0xFF);
            short sUserRocketDelay = 0;
            if (netMsgInput.available() >= 2) sUserRocketDelay = (short) (netMsgInput.readByte() & 0xFF);
            this.fUserBombFuze = sUserBombFuze / 255F * (UserCfg.BOMB_FUZE_MAX - UserCfg.BOMB_FUZE_MIN) + UserCfg.BOMB_FUZE_MIN;
            this.fUserBombDelay = sUserBombDelay / 255F * (UserCfg.BOMB_DELAY_MAX - UserCfg.BOMB_DELAY_MIN) + UserCfg.BOMB_DELAY_MIN;
            this.fUserRocketDelay = sUserRocketDelay / 255F * (UserCfg.ROCKET_DELAY_MAX - UserCfg.ROCKET_DELAY_MIN) + UserCfg.ROCKET_DELAY_MIN;

            Bomb.printDebug(this.aircraft, "Bomb Fuze: " + this.fUserBombFuze + " seconds, Bomb Delay: " + this.fUserBombDelay + " seconds, Rocket Life Time: " + this.fUserRocketDelay + " seconds.");
            this.updWeaponTiming(this.fUserBombFuze, this.fUserBombDelay, this.fUserRocketDelay);
        }
        // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication
        
        // TODO: Storebror: +++ Rocket Booster Net Replication
        if (this.actor instanceof TypeRocketBoost && netMsgInput.available() > 0) {
            ((TypeRocketBoost)this.actor).setBoostState(netMsgInput.readByte());
        }
        // TODO: Storebror: --- Rocket Booster Net Replication
    }

    void updConvDist(int i, int j, float f) {
        float f1 = (float) i * (float) i * 36F + 100F;
        try {
            if (this.aircraft.FM.CT.Weapons[j] != null) if (j == 2) {
                for (int k = 0; k < this.aircraft.FM.CT.Weapons[j].length; k++)
                    if (this.aircraft.FM.CT.Weapons[j][k] instanceof RocketGun) ((RocketGun) this.aircraft.FM.CT.Weapons[j][k]).setConvDistance(f1, f);

            } else for (int l = 0; l < this.aircraft.FM.CT.Weapons[j].length; l++)
                if (this.aircraft.FM.CT.Weapons[j][l] instanceof MGunAircraftGeneric) ((MGunAircraftGeneric) this.aircraft.FM.CT.Weapons[j][l]).setConvDistance(f1, f);
        } catch (java.lang.Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
    void updWeaponTiming(float bombFuze, float bombDelay, float rocketDelay) {
        try {
            if (this.aircraft.FM.CT.Weapons[2] != null) for (int weaponsIndex = 0; weaponsIndex < this.aircraft.FM.CT.Weapons[2].length; weaponsIndex++)
                if (this.aircraft.FM.CT.Weapons[2][weaponsIndex] instanceof RocketGun) ((RocketGun) this.aircraft.FM.CT.Weapons[2][weaponsIndex]).setRocketTimeLife(rocketDelay);
                else if (this.aircraft.FM.CT.Weapons[2][weaponsIndex] instanceof RocketBombGun) ((RocketBombGun) this.aircraft.FM.CT.Weapons[2][weaponsIndex]).setRocketTimeLife(rocketDelay);

            if (this.aircraft.FM.CT.Weapons[3] != null) for (int weaponsIndex = 0; weaponsIndex < this.aircraft.FM.CT.Weapons[3].length; weaponsIndex++)
                if (this.aircraft.FM.CT.Weapons[3][weaponsIndex] instanceof BombGun) {
                    ((BombGun) this.aircraft.FM.CT.Weapons[3][weaponsIndex]).setBombDelay(bombDelay);
                    ((BombGun) this.aircraft.FM.CT.Weapons[3][weaponsIndex]).setArmingTime((long) (bombFuze * 1000F));
                }
        } catch (java.lang.Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
    // TODO: Storebror: -- Bomb/Rocket Fuze/Delay Replication

    public void setArmingSeeds(int armingSeed) {
//        System.out.println("setArmingSeeds(" + armingSeed + ")");
        this.aircraft.armingRnd = new RangeRandom(this.aircraft.armingSeed);
        try {
            if (this.aircraft.FM.CT.Weapons[2] != null) for (int i = 0; i < this.aircraft.FM.CT.Weapons[2].length; i++) {
                // +++ MDS Hotfix by Storebror
                if (!(this.aircraft.FM.CT.Weapons[2][i] instanceof RocketGun)) continue;
                ((RocketGun) this.aircraft.FM.CT.Weapons[2][i]).setSpreadRnd(this.aircraft.armingRnd.nextInt());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        // TODO: Storebror: Torpedo Failure Rate and Bomb Spread Replication
        // ------------------------------------
        try {
            if (this.aircraft.FM.CT.Weapons[3] != null) for (int i = 0; i < this.aircraft.FM.CT.Weapons[3].length; i++)
                if (this.aircraft.FM.CT.Weapons[3][i] instanceof TorpedoGun) ((TorpedoGun) this.aircraft.FM.CT.Weapons[3][i]).setLimits(this.aircraft.armingRnd.nextFloat(), this.aircraft.armingRnd.nextFloat(), this.aircraft.armingRnd.nextFloat());
                else if (this.aircraft.FM.CT.Weapons[3][i] instanceof BombGun) ((BombGun) this.aircraft.FM.CT.Weapons[3][i]).setRnd(this.aircraft.armingRnd.nextInt());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        // ------------------------------------
    }

    private void netToMaster(int paramInt1, int paramInt2, int paramInt3) {
        this.netToMaster(paramInt1, paramInt2, paramInt3, null);
    }

    public void netToMaster(int paramInt1, int paramInt2, int paramInt3, Actor paramActor) {
        if (!this.bIsMaster) {
            if (!this.aircraft.netNewAState_isEnable(true)) return;
            if (this.itemsToMaster == null) this.itemsToMaster = new Item[_AS_LENGTH];
            if (this.sendedMsg(this.itemsToMaster, paramInt1, paramInt2, paramInt3, paramActor)) return;
            try {
                NetMsgGuaranted localNetMsgGuaranted = this.aircraft.netNewAStateMsg(true);
                if (localNetMsgGuaranted != null) {
                    localNetMsgGuaranted.writeByte((byte) paramInt1);
                    localNetMsgGuaranted.writeByte((byte) paramInt2);
                    localNetMsgGuaranted.writeByte((byte) paramInt3);
                    ActorNet localActorNet = null;
                    if (Actor.isValid(paramActor)) localActorNet = paramActor.net;
                    if (localActorNet != null) localNetMsgGuaranted.writeNetObj(localActorNet);
                    this.aircraft.netSendAStateMsg(true, localNetMsgGuaranted);
                    return;
                }
            } catch (Exception localException) {
                System.out.println(localException.getMessage());
                localException.printStackTrace();
            }
        }
    }

    private void netToMirrors(int paramInt1, int paramInt2, int paramInt3) {
        this.netToMirrors(paramInt1, paramInt2, paramInt3, null);
    }

    public void netToMirrors(int paramInt1, int paramInt2, int paramInt3, Actor paramActor) {
        if (!this.aircraft.netNewAState_isEnable(false)) return;
        if (this.itemsToMirrors == null) this.itemsToMirrors = new Item[_AS_LENGTH];
        if (this.sendedMsg(this.itemsToMirrors, paramInt1, paramInt2, paramInt3, paramActor)) return;
        try {
            NetMsgGuaranted localNetMsgGuaranted = this.aircraft.netNewAStateMsg(false);
            if (localNetMsgGuaranted != null) {
                localNetMsgGuaranted.writeByte((byte) paramInt1);
                localNetMsgGuaranted.writeByte((byte) paramInt2);
                localNetMsgGuaranted.writeByte((byte) paramInt3);
                ActorNet localActorNet = null;
                if (Actor.isValid(paramActor)) localActorNet = paramActor.net;
                if (localActorNet != null) localNetMsgGuaranted.writeNetObj(localActorNet);
                this.aircraft.netSendAStateMsg(false, localNetMsgGuaranted);
                return;
            }
        } catch (Exception localException) {
            System.out.println(localException.getMessage());
            localException.printStackTrace();
        }
    }

    private boolean sendedMsg(Item[] paramArrayOfItem, int paramInt1, int paramInt2, int paramInt3, Actor paramActor) {
        if (paramInt1 < 0 || paramInt1 >= paramArrayOfItem.length) return false;
        Item localItem = paramArrayOfItem[paramInt1];
        if (localItem == null) {
            localItem = new Item();
            localItem.set(paramInt2, paramInt3, paramActor);
            paramArrayOfItem[paramInt1] = localItem;
            return false;
        }
        if (localItem.equals(paramInt2, paramInt3, paramActor)) return true;
        localItem.set(paramInt2, paramInt3, paramActor);
        return false;
    }

    private int cvt(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt1, int paramInt2) {
        paramFloat1 = Math.min(Math.max(paramFloat1, paramFloat2), paramFloat3);
        return (int) (paramInt1 + (paramInt2 - paramInt1) * (paramFloat1 - paramFloat2) / (paramFloat3 - paramFloat2));
    }

    private float cvt(int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2) {
        paramInt1 = Math.min(Math.max(paramInt1, paramInt2), paramInt3);
        return paramFloat1 + (paramFloat2 - paramFloat1) * (paramInt1 - paramInt2) / (paramInt3 - paramInt2);
    }

    public float getGyroAngle() {
        if (this.torpedoGyroAngle == 0) return 0.0F;
        return this.cvt(this.torpedoGyroAngle, 1, 65535, -gyroAngleLimit, gyroAngleLimit);
    }

    public void setGyroAngle(float paramFloat) {
        int i = 0;
        if (paramFloat != 0.0F) i = this.cvt(paramFloat, -gyroAngleLimit, gyroAngleLimit, 1, 65535);
        this.torpedoGyroAngle = i;
    }

    public void replicateGyroAngleToNet() {
        int i = this.cvt(this.getGyroAngle(), -gyroAngleLimit, gyroAngleLimit, 1, 65535);
        int j = i & 0xFF00;
        j >>= 8;
        int k = i & 0xFF;
        this.setGyroAngle(this.actor, j, k, false);
    }

    public void setGyroAngle(Actor paramActor, int paramInt1, int paramInt2, boolean paramBoolean) {
        if (!Actor.isValid(paramActor)) return;
        if (paramBoolean) this.doSetGyroAngle(paramActor, paramInt1, paramInt2);
        if (this.bIsMaster) this.netToMirrors(44, paramInt1, paramInt2);
        else this.netToMaster(44, paramInt1, paramInt2, paramActor);
    }

    private void doSetGyroAngle(Actor paramActor, int paramInt1, int paramInt2) {
        this.torpedoGyroAngle = paramInt1 << 8 | paramInt2;
    }

    public int getSpreadAngle() {
        return this.torpedoSpreadAngle;
    }

    public void setSpreadAngle(int paramInt) {
        this.torpedoSpreadAngle = paramInt;
        if (this.torpedoSpreadAngle < 0) this.torpedoSpreadAngle = 0;
        if (this.torpedoSpreadAngle > 30) this.torpedoSpreadAngle = 30;
    }

    public void replicateSpreadAngleToNet() {
        this.setSpreadAngle(this.actor, this.getSpreadAngle(), 0, false);
    }

    private void setSpreadAngle(Actor paramActor, int paramInt1, int paramInt2, boolean paramBoolean) {
        if (!Actor.isValid(paramActor)) return;
        if (paramBoolean) this.doSetSpreadAngle(paramActor, paramInt1, 0);
        if (this.bIsMaster) this.netToMirrors(45, paramInt1, 0);
        else this.netToMaster(45, paramInt1, 0, paramActor);
    }

    private void doSetSpreadAngle(Actor paramActor, int paramInt1, int paramInt2) {
        this.setSpreadAngle(paramInt1);
    }

    static class Item {
        int   msgDestination;
        int   msgContext;
        Actor initiator;

        void set(int paramInt1, int paramInt2, Actor paramActor) {
            this.msgDestination = paramInt1;
            this.msgContext = paramInt2;
            this.initiator = paramActor;
        }

        boolean equals(int paramInt1, int paramInt2, Actor paramActor) {
            return this.msgDestination == paramInt1 && this.msgContext == paramInt2 && this.initiator == paramActor;
        }
    }

    // TODO: |ZUTI| variables
    // --------------------------------------------
    private boolean bzutiIsMultiCrew        = false;
    private boolean bzutiIsMultiCrewAnytime = false;

    /**
     * Set plane multi crew readiness.
     *
     * @param value
     */
    public void zutiSetMultiCrew(boolean value) {
        this.bzutiIsMultiCrew = value;
    }

    /**
     * Set plane multi crew readiness.
     *
     * @param value
     */
    public void zutiSetMultiCrewAnytime(boolean value) {
        this.bzutiIsMultiCrewAnytime = value;
    }

    /**
     * Is plane multi crew enabled or not?
     *
     * @return
     */
    public boolean zutiIsPlaneMultiCrew() {
        return this.bzutiIsMultiCrew;
    }

    /**
     * Can users join multi crew enabled plane at any time or not.
     *
     * @return
     */
    public boolean zutiIsPlaneMultiCrewAnytime() {
        if (!this.bzutiIsMultiCrew) return false;

        return this.bzutiIsMultiCrewAnytime;
    }
    // --------------------------------------------

    // TODO: +++ TD AI code backport from 4.13 +++
    public boolean isEnginesOnFire() {
        for (int i = 0; i < this.aircraft.FM.EI.getNum(); i++)
            if (this.astateEngineStates[i] > 3) return true;

        return false;
    }

    public boolean isAllPilotsDead() {
        if (this.aircraft.FM.crew <= 1) return this.isPilotDead(0);
        // TODO: Fixed by SAS~Storebror: Replace short-circuit logic (&) by nsc logic (&&)
//        if (this.astatePilotFunctions[1] == 2) return this.isPilotDead(0) & this.isPilotDead(1);
        if (this.astatePilotFunctions[1] == 2) return this.isPilotDead(0) && this.isPilotDead(1);
        else return this.isPilotDead(0);
    }
    // TODO: --- TD AI code backport from 4.13 ---

    // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
    private static final float AI_BOMB_FUZE     = 0F;
    private static final float AI_BOMB_DELAY    = 0.5F;
    private static final float AI_ROCKET_DELAY  = 60F;
    private float              fUserBombFuze    = 0F;
    private float              fUserBombDelay   = 0F;
    private float              fUserRocketDelay = 60F;
    // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication

    // +++++ TODO skylla: enhanced weapon release control +++++

    public void replicateRocketSelectedToNet(int listIndex) {
        this.setRocketSelected(this.actor, listIndex, false);
    }

    /**
     * @param listIndex value range: 0 to 255
     **/
    private void setRocketSelected(Actor actor, int listIndex, boolean applySetting) {
        if (listIndex > 255 || listIndex < 0) System.out.println(this.getClass() + ".setRocketSelected() received a off 'listIndex' value (" + listIndex + "). This may be responsible for weird occurances when firing rockets!");
        if (applySetting) this.doSetRocketSelected(actor, listIndex);
        if (!Actor.isValid(actor) /* || Mission.isSingle() */) {
            System.out.println("SKYLLA: setRocketSelected() returns because of " + (!Actor.isValid(actor) ? "invalid Actor!" : "single Mission!"));
            return;
        }
        // System.out.println("SKYLLA: setRocketSelected " + (applySetting?"received":"sent") + " the following listIndex: " + listIndex);
        if (this.bIsMaster) {
            this.ClearSendedItems(_AS_ROCKET_SELECTED, this.itemsToMirrors);
            this.netToMirrors(_AS_ROCKET_SELECTED, listIndex, 0, actor);
        } else {
            this.ClearSendedItems(_AS_ROCKET_SELECTED, this.itemsToMaster);
            this.netToMaster(_AS_ROCKET_SELECTED, listIndex, 0, actor);
        }
    }

    private void doSetRocketSelected(Actor actor, int listIndex) {
        if (this.actor != actor) return;
        // System.out.println("SKYLLA: setting 'Rocket Selected' to the value indexed with " + listIndex);
        this.aircraft.FM.CT.setRocketSelected(listIndex);
    }

    public void replicateRocketReleaseDelayToNet(int delayIndex) {
        this.setRocketReleaseDelay(this.actor, delayIndex, false);
    }

    /**
     * @param delayIndex value range: 0 to 255
     */
    private void setRocketReleaseDelay(Actor actor, int delayIndex, boolean applySetting) {
        if (delayIndex > 255 || delayIndex < 0) System.out.println(this.getClass() + ".setRocketReleaseDelay() received a off 'delayIndex' value (" + delayIndex + "). This may be responsible for weird occurances when firing rockets!");
        if (applySetting) this.doSetRocketReleaseDelay(actor, delayIndex);
        if (!Actor.isValid(actor) /* || Mission.isSingle() */) {
            System.out.println("SKYLLA: setRocketReleaseDelay() returns because of " + (!Actor.isValid(actor) ? "invalid Actor!" : "single Mission!"));
            return;
        }
        // System.out.println("SKYLLA: setRocketReleaseDelay " + (applySetting?"received":"sent") + " the following listIndex: " + delayIndex);
        if (this.bIsMaster) {
            this.ClearSendedItems(_AS_ROCKET_RELEASE_DELAY, this.itemsToMirrors);
            this.netToMirrors(_AS_ROCKET_RELEASE_DELAY, delayIndex, 0, actor);
        } else {
            this.ClearSendedItems(_AS_ROCKET_RELEASE_DELAY, this.itemsToMaster);
            this.netToMaster(_AS_ROCKET_RELEASE_DELAY, delayIndex, 0, actor);
        }
    }

    private void doSetRocketReleaseDelay(Actor actor, int delayIndex) {
        if (this.actor != actor) return;
        // System.out.println("SKYLLA: setting 'Rocket Release Delay' to the value indexed with " + delayIndex);
        this.aircraft.FM.CT.setRocketReleaseDelayByIndex(delayIndex);
    }

    public void replicateRocketReleaseModeToNet(int releaseModeIndex) {
        this.setRocketReleaseMode(this.actor, releaseModeIndex, false);
    }

    /**
     * @param releaseModeIndex value range: 0 to 2
     */
    private void setRocketReleaseMode(Actor actor, int releaseModeIndex, boolean applySetting) {
        if (releaseModeIndex > 255 || releaseModeIndex < 0)
            System.out.println(this.getClass() + ".setRocketReleaseMode() received a off 'releaseModeIndex' value (" + releaseModeIndex + "). This may be responsible for weird occurances when firing rockets!");
        if (applySetting) this.doSetRocketReleaseMode(actor, releaseModeIndex);
        if (!Actor.isValid(actor) /* || Mission.isSingle() */) {
            System.out.println("SKYLLA: setRocketReleaseMode() returns because of " + (!Actor.isValid(actor) ? "invalid Actor!" : "single Mission!"));
            return;
        }
        // System.out.println("SKYLLA: setRocketReleaseMode " + (applySetting?"received":"sent") + " the following listIndex: " + releaseModeIndex);
        if (this.bIsMaster) {
            this.ClearSendedItems(_AS_ROCKET_RELEASE_MODE, this.itemsToMirrors);
            this.netToMirrors(_AS_ROCKET_RELEASE_MODE, releaseModeIndex, 0, actor);
        } else {
            this.ClearSendedItems(_AS_ROCKET_RELEASE_MODE, this.itemsToMaster);
            this.netToMaster(_AS_ROCKET_RELEASE_MODE, releaseModeIndex, 0, actor);
        }
    }

    private void doSetRocketReleaseMode(Actor actor, int releaseModeIndex) {
        if (this.actor != actor) return;
        // System.out.println("SKYLLA: setting 'Rocket Release Mode' to the value indexed with " + releaseModeIndex);
        this.aircraft.FM.CT.setRocketSalvoSizeByIndex(releaseModeIndex);
    }

    public void replicateBombSelectedToNet(int listIndex) {
        this.setBombSelected(this.actor, listIndex, false);
    }

    /**
     * @param listIndex value range: 0 to 255
     **/
    private void setBombSelected(Actor actor, int listIndex, boolean applySetting) {
        if (listIndex > 255 || listIndex < 0) System.out.println(this.getClass() + ".setBombSelected() received a off 'listIndex' value (" + listIndex + "). This may be responsible for weird occurances when releasing bombs!");
        if (applySetting) this.doSetBombSelected(actor, listIndex);
        if (!Actor.isValid(actor) /* || Mission.isSingle() */) {
            System.out.println("SKYLLA: setBombSelected() returns because of " + (!Actor.isValid(actor) ? "invalid Actor!" : "single Mission!"));
            return;
        }
        // System.out.println("SKYLLA: setBombSelected " + (applySetting?"received":"sent") + " the following listIndex: " + listIndex);
        if (this.bIsMaster) {
            this.ClearSendedItems(_AS_BOMB_SELECTED, this.itemsToMirrors);
            this.netToMirrors(_AS_BOMB_SELECTED, listIndex, 0, actor);
        } else {
            this.ClearSendedItems(_AS_BOMB_SELECTED, this.itemsToMaster);
            this.netToMaster(_AS_BOMB_SELECTED, listIndex, 0, actor);
        }
    }

    private void doSetBombSelected(Actor actor, int listIndex) {
        if (this.actor != actor) return;
        // System.out.println("SKYLLA: setting 'Bomb Selected' to the value indexed with " + listIndex);
        this.aircraft.FM.CT.setBombSelected(listIndex);
    }

    public void replicateBombReleaseDelayToNet(int delayIndex) {
        this.setBombReleaseDelay(this.actor, delayIndex, false);
    }

    /**
     * @param delayIndex value range: 0 to 255
     */
    private void setBombReleaseDelay(Actor actor, int delayIndex, boolean applySetting) {
        if (applySetting) this.doSetBombReleaseDelay(actor, delayIndex);
        if (delayIndex > 255 || delayIndex < 0) System.out.println(this.getClass() + ".setBombReleaseDelay() received a off 'delayIndex' value (" + delayIndex + "). This may be responsible for weird occurances when releasing bombs!");
        if (!Actor.isValid(actor) /* || Mission.isSingle() */) {
            System.out.println("SKYLLA: setBombReleaseDelay() returns because of " + (!Actor.isValid(actor) ? "invalid Actor!" : "single Mission!"));
            return;
        }
        // System.out.println("SKYLLA: setBombReleaseDelay " + (applySetting?"received":"sent") + " the following listIndex: " + delayIndex);
        if (this.bIsMaster) {
            this.ClearSendedItems(_AS_BOMB_RELEASE_DELAY, this.itemsToMirrors);
            this.netToMirrors(_AS_BOMB_RELEASE_DELAY, delayIndex, 0, actor);
        } else {
            this.ClearSendedItems(_AS_BOMB_RELEASE_DELAY, this.itemsToMaster);
            this.netToMaster(_AS_BOMB_RELEASE_DELAY, delayIndex, 0, actor);
        }
    }

    private void doSetBombReleaseDelay(Actor actor, int delayIndex) {
        if (this.actor != actor) return;
        // System.out.println("SKYLLA: setting 'Bomb Release Delay' to the value indexed with " + delayIndex);
        this.aircraft.FM.CT.setBombReleaseDelayByIndex(delayIndex);
    }

    public void replicateBombReleaseModeToNet(int releaseModeIndex) {
        this.setBombReleaseMode(this.actor, releaseModeIndex, false);
    }

    /**
     * @param releaseModeIndex value range: 0 to 255
     */
    private void setBombReleaseMode(Actor actor, int releaseModeIndex, boolean applySetting) {
        if (applySetting) this.doSetBombReleaseMode(actor, releaseModeIndex);
        if (releaseModeIndex > 255 || releaseModeIndex < 0)
            System.out.println(this.getClass() + ".setBombReleaseMode() received a off 'releaseModeIndex' value (" + releaseModeIndex + "). This may be responsible for weird occurances when releasing bombs!");
        if (!Actor.isValid(actor) /* || Mission.isSingle() */) {
            System.out.println("SKYLLA: setBombReleaseMode() returns because of " + (!Actor.isValid(actor) ? "invalid Actor!" : "single Mission!"));
            return;
        }
        // System.out.println("SKYLLA: setBombReleaseMode " + (applySetting?"received":"sent") + " the following listIndex: " + releaseModeIndex);
        if (this.bIsMaster) {
            this.ClearSendedItems(_AS_BOMB_RELEASE_MODE, this.itemsToMirrors);
            this.netToMirrors(_AS_BOMB_RELEASE_MODE, releaseModeIndex, 0, actor);
        } else {
            this.ClearSendedItems(_AS_BOMB_RELEASE_MODE, this.itemsToMaster);
            this.netToMaster(_AS_BOMB_RELEASE_MODE, releaseModeIndex, 0, actor);
        }
    }

    private void doSetBombReleaseMode(Actor actor, int releaseModeIndex) {
        if (this.actor != actor) return;
        // System.out.println("SKYLLA: setting 'Bomb Release Mode' to the value indexed with " + releaseModeIndex);
        this.aircraft.FM.CT.setBombSalvoSizeByIndex(releaseModeIndex);
    }

    // ----- todo skylla: enhanced weapon release control -----

    // +++++ TODO Storebror: fixing the loss of fast repeated net replication messages +++++

    public void ClearSendedItems(int asMessageIndex, Item[] items) {
        if (items == null) return;
        if (items.length <= asMessageIndex) return;
        if (items[asMessageIndex] == null) return;
        items[asMessageIndex] = null;
    }

    protected static void AircraftStateClearSendedItemsToMaster(AircraftState as, int asMessageIndex) {
        AircraftStateClearSendedItems(as, asMessageIndex, "itemsToMaster");
    }

    protected static void AircraftStateClearSendedItemsToMirrors(AircraftState as, int asMessageIndex) {
        AircraftStateClearSendedItems(as, asMessageIndex, "itemsToMirrors");
    }

    protected static void AircraftStateClearSendedItems(AircraftState as, int asMessageIndex, String fieldName) {
        Object[] itemsToMaster = (Object[]) Reflection.getValue(as, fieldName);
        if (itemsToMaster == null) return;
        if (itemsToMaster.length <= asMessageIndex) {
            System.out.println("[AircraftState Helper] cannot clear " + fieldName + "[" + asMessageIndex + "], " + fieldName + " length is " + itemsToMaster.length);
            return;
        }
        if (itemsToMaster[asMessageIndex] == null) return;
        itemsToMaster[asMessageIndex] = null;
    }

    // ----- todo Storebror: fixing the loss of fast repeated net replication messages -----

    // TODO Storebror: Backport from 4.14
    public void disablePilot(int i) {
        this.astatePilotStates[i] = 100;
    }
    // ---
    
    // TODO: By SAS~Storebror: Rocket Booster Net Replication +++
    public void setBoostState(Actor actor, int state)
    {
        if(!Actor.isValid(actor)) return;
        if (!(actor instanceof TypeRocketBoost)) return;
        TypeRocketBoost boostAircraft = (TypeRocketBoost)actor;
        if (boostAircraft.getBoostState() == state) return;
        if(bIsMaster)
        {
            if(!doSetBoostState(state))
                return;
            netToMirrors(_AS_BOOST_STATE, state, state);
        } else
        {
            netToMaster(_AS_BOOST_STATE, state, state, actor);
        }
    }

    public boolean doSetBoostState(int state)
    {
        if (!(aircraft instanceof TypeRocketBoost)) return false;
        ((TypeRocketBoost)aircraft).setBoostState(state);
        return true;
    }
    // TODO: By SAS~Storebror: Rocket Booster Net Replication ---


}
