/* 4.10.1 file made compatible with UP */
package com.maddox.il2.game;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.GUIWindowManager;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.gui.GUI;
import com.maddox.il2.gui.GUIMenu;
import com.maddox.il2.net.NetMissionTrack;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.ActorViewPoint;
import com.maddox.il2.objects.air.A6M;
import com.maddox.il2.objects.air.A6M5C;
import com.maddox.il2.objects.air.A6M7_62;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.AircraftLH;
import com.maddox.il2.objects.air.Cockpit;
import com.maddox.il2.objects.air.CockpitGunner;
import com.maddox.il2.objects.air.CockpitPilot;
import com.maddox.il2.objects.air.DO_335;
import com.maddox.il2.objects.air.Hurricane;
import com.maddox.il2.objects.air.MOSQUITO;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.P_51;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.air.SPITFIRE;
import com.maddox.il2.objects.air.TEMPEST;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeDiveBomber;
import com.maddox.il2.objects.air.TypeDockable;
import com.maddox.il2.objects.air.TypeFighterAceMaker;
import com.maddox.il2.objects.air.TypeHasToKG;
import com.maddox.il2.objects.air.TypeX4Carrier;
import com.maddox.il2.objects.effects.ForceFeedback;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.Bomb;
import com.maddox.il2.objects.weapons.Rocket;
import com.maddox.il2.objects.weapons.RocketBomb;
import com.maddox.il2.objects.weapons.TorpedoGun;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.HotKeyCmdEnv;
import com.maddox.rts.HotKeyCmdMouseMove;
import com.maddox.rts.HotKeyCmdMove;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Joy;
import com.maddox.rts.LDRres;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Reflection;
import com.maddox.sound.AudioDevice;
import com.maddox.sound.CmdMusic;
import com.maddox.sound.RadioChannel;

// TODO: Disabled for 410 compatibility
/*
 * import com.maddox.il2.objects.air.F84G1;
 * import com.maddox.il2.objects.air.F9F;
 * import com.maddox.il2.objects.air.Halifax;
 * import com.maddox.il2.objects.air.MOSQUITOS;
 * import com.maddox.il2.objects.air.P2V;
 * import com.maddox.il2.objects.air.P_51Mustang;
 * import com.maddox.il2.objects.air.SPITFIRE12;
 * import com.maddox.il2.objects.air.SPITFIRE14C;
 * import com.maddox.il2.objects.air.SPITFIRELF14E;
 */

public class AircraftHotKeys {
    public static boolean   bFirstHotCmd             = false;
    private FlightModel     FM;
    private boolean         bPropAuto;
    private boolean         bAfterburner;
    private float           lastPower;
    private float           lastProp;
    static final int        MOVE_POWER               = 1;
    static final int        MOVE_FLAPS               = 2;
    static final int        MOVE_AILERON             = 3;
    static final int        MOVE_ELEVATOR            = 4;
    static final int        MOVE_RUDDER              = 5;
    static final int        MOVE_BRAKE               = 6;
    static final int        MOVE_STEP                = 7;
    static final int        MOVE_TRIMHOR             = 8;
    static final int        MOVE_TRIMVER             = 9;
    static final int        MOVE_TRIMRUD             = 10;
// private int cptdmg;
    static final int        BRAKE                    = 0;
    static final int        RUDDER_LEFT              = 1;
    static final int        RUDDER_RIGHT             = 2;
    static final int        ELEVATOR_UP              = 3;
    static final int        ELEVATOR_DOWN            = 4;
    static final int        AILERON_LEFT             = 5;
    static final int        AILERON_RIGHT            = 6;
    static final int        RADIATOR                 = 7;
    static final int        GEAR                     = 9;
    static final int        GUNPODS                  = 15;
    static final int        WEAPON0                  = 16;
    static final int        WEAPON1                  = 17;
    static final int        WEAPON2                  = 18;
    static final int        WEAPON3                  = 19;
    static final int        WEAPON01                 = 64;
    static final int        POWER_0                  = 20;
    static final int        POWER_1                  = 21;
    static final int        POWER_2                  = 22;
    static final int        POWER_3                  = 23;
    static final int        POWER_4                  = 24;
    static final int        POWER_5                  = 25;
    static final int        POWER_6                  = 26;
    static final int        POWER_7                  = 27;
    static final int        POWER_8                  = 28;
    static final int        POWER_9                  = 29;
    static final int        POWER_10                 = 30;
    static final int        STEP_0                   = 31;
    static final int        STEP_1                   = 32;
    static final int        STEP_2                   = 33;
    static final int        STEP_3                   = 34;
    static final int        STEP_4                   = 35;
    static final int        STEP_5                   = 36;
    static final int        STEP_6                   = 37;
    static final int        STEP_7                   = 38;
    static final int        STEP_8                   = 39;
    static final int        STEP_9                   = 40;
    static final int        STEP_10                  = 41;
    static final int        STEP_A                   = 42;
    static final int        AIRCRAFT_TRIM_V_0        = 43;
    static final int        AIRCRAFT_TRIM_V_PLUS     = 44;
    static final int        AIRCRAFT_TRIM_V_MINUS    = 45;
    static final int        AIRCRAFT_TRIM_H_0        = 46;
    static final int        AIRCRAFT_TRIM_H_PLUS     = 47;
    static final int        AIRCRAFT_TRIM_H_MINUS    = 48;
    static final int        AIRCRAFT_TRIM_R_0        = 49;
    static final int        AIRCRAFT_TRIM_R_PLUS     = 50;
    static final int        AIRCRAFT_TRIM_R_MINUS    = 51;
    static final int        FLAPS_NOTCH_UP           = 52;
    static final int        FLAPS_NOTCH_DOWN         = 53;
    static final int        GEAR_UP_MANUAL           = 54;
    static final int        GEAR_DOWN_MANUAL         = 55;
    static final int        RUDDER_LEFT_1            = 56;
    static final int        RUDDER_CENTRE            = 57;
    static final int        RUDDER_RIGHT_1           = 58;
    static final int        POWER_PLUS_5             = 59;
    static final int        POWER_MINUS_5            = 60;
    static final int        BOOST                    = 61;
    static final int        AIRCRAFT_DROP_TANKS      = 62;
    static final int        AIRCRAFT_TOGGLE_AIRBRAKE = 63;
    static final int        AIRCRAFT_TOGGLE_ENGINE   = 70;
    static final int        AIRCRAFT_STABILIZER      = 71;
    static final int        AIRCRAFT_TAILWHEELLOCK   = 72;
    static final int        STEP_PLUS_5              = 73;
    static final int        STEP_MINUS_5             = 74;
    static final int        MIX_0                    = 75;
    static final int        MIX_1                    = 76;
    static final int        MIX_2                    = 77;
    static final int        MIX_3                    = 78;
    static final int        MIX_4                    = 79;
    static final int        MIX_5                    = 80;
    static final int        MIX_6                    = 81;
    static final int        MIX_7                    = 82;
    static final int        MIX_8                    = 83;
    static final int        MIX_9                    = 84;
    static final int        MIX_10                   = 85;
    static final int        MIX_PLUS_20              = 86;
    static final int        MIX_MINUS_20             = 87;
    static final int        MAGNETO_PLUS_1           = 88;
    static final int        MAGNETO_MINUS_1          = 89;
    static final int        ENGINE_SELECT_ALL        = 90;
    static final int        ENGINE_SELECT_NONE       = 91;
    static final int        ENGINE_SELECT_LEFT       = 92;
    static final int        ENGINE_SELECT_RIGHT      = 93;
    static final int        ENGINE_SELECT_1          = 94;
    static final int        ENGINE_SELECT_2          = 95;
    static final int        ENGINE_SELECT_3          = 96;
    static final int        ENGINE_SELECT_4          = 97;
    static final int        ENGINE_SELECT_5          = 98;
    static final int        ENGINE_SELECT_6          = 99;
    static final int        ENGINE_SELECT_7          = 100;
    static final int        ENGINE_SELECT_8          = 101;
    static final int        ENGINE_SELECT_9          = 310;
    static final int        ENGINE_SELECT_10         = 311;
    static final int        ENGINE_SELECT_11         = 312;
    static final int        ENGINE_SELECT_12         = 313;
    static final int        ENGINE_TOGGLE_ALL        = 102;
    static final int        ENGINE_TOGGLE_LEFT       = 103;
    static final int        ENGINE_TOGGLE_RIGHT      = 104;
    static final int        ENGINE_TOGGLE_1          = 105;
    static final int        ENGINE_TOGGLE_2          = 106;
    static final int        ENGINE_TOGGLE_3          = 107;
    static final int        ENGINE_TOGGLE_4          = 108;
    static final int        ENGINE_TOGGLE_5          = 109;
    static final int        ENGINE_TOGGLE_6          = 110;
    static final int        ENGINE_TOGGLE_7          = 111;
    static final int        ENGINE_TOGGLE_8          = 112;
    static final int        ENGINE_TOGGLE_9          = 314;
    static final int        ENGINE_TOGGLE_10         = 315;
    static final int        ENGINE_TOGGLE_11         = 316;
    static final int        ENGINE_TOGGLE_12         = 317;
    static final int        ENGINE_EXTINGUISHER      = 113;
    static final int        ENGINE_FEATHER           = 114;
    static final int        COMPRESSORSTEP_PLUS      = 115;
    static final int        COMPRESSORSTEP_MINUS     = 116;
    static final int        SIGHT_DIST_PLUS          = 117;
    static final int        SIGHT_DIST_MINUS         = 118;
    static final int        SIGHT_SIDE_RIGHT         = 119;
    static final int        SIGHT_SIDE_LEFT          = 120;
    static final int        SIGHT_ALT_PLUS           = 121;
    static final int        SIGHT_ALT_MINUS          = 122;
    static final int        SIGHT_SPD_PLUS           = 123;
    static final int        SIGHT_SPD_MINUS          = 124;
    static final int        SIGHT_AUTO_ONOFF         = 125;
    static final int        AIRCRAFT_DOCK_UNDOCK     = 126;
    static final int        WINGFOLD                 = 127;
    static final int        COCKPITDOOR              = 128;
    static final int        AIRCRAFT_CARRIERHOOK     = 129;
    static final int        AIRCRAFT_BRAKESHOE       = 130;
    public static int       hudLogPowerId;
    public static int       hudLogWeaponId;
    private boolean         bAutoAutopilot;
    private int             switchToCockpitRequest;
    private HotKeyCmd[]     cmdFov;
    // TODO: +++ Added by SAS~Storebor: Show Player Aircraft before any AI Actors! +++
    private static Object[] namedAll                 = new Object[1];
    private static Object[] namedAllPlayer           = new Object[1];
    private static Object[] namedAllAI               = new Object[1];
    private static TreeMap  namedAircraftPlayer      = new TreeMap();
    private static TreeMap  namedAircraftAI          = new TreeMap();
// private static TreeMap namedAircraft = new TreeMap();
// TODO: --- Added by SAS~Storebor: Show Player Aircraft before any AI Actors! ---
    private boolean bMissionModsSet            = false;
    private boolean bSpeedbarTAS               = false;
    private boolean bSeparateGearUpDown        = false;
    private boolean bSeparateHookUpDown        = false;
    private boolean bSeparateRadiatorOpenClose = false;
    private boolean bToggleMusic               = false;
    private boolean bViewExternalSelf          = false;
    private boolean bViewExternalOnGround      = false;
    private boolean bViewExternalWhenDead      = false;
    private boolean bViewExternalFriendlies    = false;
    private boolean bBombBayDoors              = true;
    private boolean bMusicOn                   = true;
    private int     iAirShowSmoke              = 0;
// private boolean bAirShowSmokeEnhanced = false;
    private boolean bSideDoor                  = true;
    private int     COCKPIT_DOOR               = 1;
    private int     SIDE_DOOR                  = 2;
    private boolean bAllowDumpFuel             = false;
// private boolean bDumpFuel = false;
    private boolean bExtViewEnemy              = false;
    private boolean bExtViewFriendly           = false;
    private boolean bExtViewSelf               = false;
    private boolean bExtViewGround             = false;
    private boolean bExtViewDead               = false;
    private int     iExtViewGround             = -1;
    private int     iExtViewDead               = -1;
    private boolean bPadlockEnemy              = false;
    private boolean bPadlockFriendly           = false;
    private boolean bExtPadlockEnemy           = false;
    private boolean bExtPadlockFriendly        = false;
// private boolean bMustangCompressorAuto = true;

    private float      lastPower2;
    private float      lastPower1;
    private float      lastPower3;
    private float      lastPower4;
    private float      lastProp1;
    private float      lastProp2;
    private float      lastProp3;
    private float      lastProp4;
    private float      lastRadiator;
    private AircraftLH bAAircraft;
// private com.maddox.il2.fm.FlightModel baFM;
    static final int   MOVE_POWER1   = 15;
    static final int   MOVE_POWER2   = 16;
    static final int   MOVE_POWER3   = 17;
    static final int   MOVE_POWER4   = 18;
    static final int   MOVE_RADIATOR = 19;
    static final int   MOVE_PROP1    = 20;
    static final int   MOVE_PROP2    = 21;
    static final int   MOVE_PROP3    = 22;
    static final int   MOVE_PROP4    = 23;
    static final int   MOVE_ZOOM     = 100;
    private boolean    changeFovEnabled;
    static final int   BEACON_PLUS   = 139;
    static final int   BEACON_MINUS  = 140;
    static final int   AUX1_PLUS     = 149;
    static final int   AUX1_MINUS    = 150;
    static final int   AUX_A         = 157;
    static final int   AUX_B         = 158;
    static final int   AUX_C         = 159;
    static final int   AUX_D         = 160;
    static final int   AUX_E         = 161;
    static final int   MISC_1        = 300;
    static final int   MISC_2        = 301;
    static final int   MISC_3        = 302;
    static final int   MISC_4        = 303;
    static final int   MISC_5        = 304;
    static final int   MISC_6        = 305;
    static final int   MISC_7        = 306;
    static final int   MISC_8        = 307;
    static final int   MISC_9        = 308;
    static final int   MISC_10       = 309;

    // TODO: +++ 4.12.2 adaptations by SAS~Storebror
    /* private */ boolean bombSightFovEnabled = false;
    // TODO: --- 4.12.2 adaptations by SAS~Storebror

    class HotKeyCmdFire extends HotKeyCmd {
        int  cmd;
        long time;

        public void begin() {
            AircraftHotKeys.this.doCmdPilot(this.cmd, true);
            this.time = Time.tick();
        }

        public void tick() {
            if (Time.tick() > this.time + 500L) AircraftHotKeys.this.doCmdPilotTick(this.cmd, true);
        }

        public boolean isTickInTime(boolean bool) {
            return !bool;
        }

        public void end() {
            AircraftHotKeys.this.doCmdPilot(this.cmd, false);
        }

        public boolean isDisableIfTimePaused() {
            return true;
        }

        public HotKeyCmdFire(String string, String string_0_, int i, int i_1_) {
            super(true, string_0_, string);
            this.cmd = i;
            this.setRecordId(i_1_);
        }
    }

    class HotKeyCmdFireMove extends HotKeyCmdMove {
        int     cmd;
        boolean disableIfPaused;

        public void begin() {
            int i = this.name().charAt(0) != '-' ? 1 : -1;
            AircraftHotKeys.this.doCmdPilotMove(this.cmd, Joy.normal(i * this.move()));
        }

        public boolean isDisableIfTimePaused() {
            return this.disableIfPaused;
        }

        public HotKeyCmdFireMove(String string, String string_2_, int i, int i_3_) {
            super(true, string_2_, string);
            this.cmd = i;
            this.setRecordId(i_3_);
            this.disableIfPaused = true;
        }

        public HotKeyCmdFireMove(java.lang.String s, java.lang.String s1, int i, int j, boolean flag) {
            this(s, s1, i, j);
            this.disableIfPaused = flag;
        }
    }

    public boolean isAfterburner() {
        if (!Actor.isValid(World.getPlayerAircraft())) this.bAfterburner = false;
        return this.bAfterburner;
    }

    public void setAfterburner(boolean bool) {
        if (this.FM.EI.isSelectionHasControlAfterburner()) {
            this.bAfterburner = bool;
            if (this.bAfterburner) {
                boolean bool_4_ = false;
                if (this.FM.actor instanceof Hurricane || this.FM.actor instanceof A6M && !(this.FM.actor instanceof A6M7_62) && !(this.FM.actor instanceof A6M5C) || this.FM.actor instanceof P_51 || this.FM.actor instanceof SPITFIRE
                        || this.FM.actor instanceof MOSQUITO || this.FM.actor instanceof TEMPEST)
                    bool_4_ = true;
                try {
                    // TODO: Disabled for 410 compatibility
                    /*
                     * if (FM.actor instanceof MOSQUITOS) bool_4_ = true;
                     */
                } catch (Throwable throwable) {
                    /* empty */
                }
                if (bool_4_) HUD.logRightBottom("BoostWepTP0");
                else HUD.logRightBottom("BoostWepTP" + this.FM.EI.getFirstSelected().getAfterburnerType());
            } else HUD.logRightBottom(null);
        }
        this.FM.CT.setAfterburnerControl(this.bAfterburner);
    }

    public void setAfterburnerForAutoActivation(boolean bool) {
        this.bAfterburner = bool;
    }

    public boolean isPropAuto() {
        if (!Actor.isValid(World.getPlayerAircraft())) this.bPropAuto = false;
        return this.bPropAuto;
    }

    public void setPropAuto(boolean bool) {
        if (!bool || this.FM.EI.isSelectionAllowsAutoProp()) this.bPropAuto = bool;
    }

    public void resetGame() {
        this.FM = null;
        this.bAfterburner = false;
        this.bPropAuto = true;
        this.lastPower = -0.5F;
        this.lastPower1 = -0.5F;
        this.lastPower2 = -0.5F;
        this.lastPower3 = -0.5F;
        this.lastPower4 = -0.5F;
        this.lastProp = 1.5F;
        this.lastProp1 = 1.5F;
        this.lastProp2 = 1.5F;
        this.lastProp3 = 1.5F;
        this.lastProp4 = 1.5F;

// bDumpFuel = false;
        this.bMissionModsSet = false;
// bMustangCompressorAuto = true;
    }

    private boolean setBombAimerAircraft() {
        this.bAAircraft = null;
        if (!com.maddox.il2.engine.Actor.isAlive(com.maddox.il2.ai.World.getPlayerAircraft())) return false;
        if (com.maddox.il2.ai.World.isPlayerParatrooper()) return false;
        if (com.maddox.il2.ai.World.isPlayerDead()) return false;
        com.maddox.il2.fm.FlightModel flightmodel = com.maddox.il2.ai.World.getPlayerFM();
        if (flightmodel == null) return false;
        else {
            this.bAAircraft = (com.maddox.il2.objects.air.AircraftLH) flightmodel.actor;
// baFM = flightmodel;
            return true;
        }
    }

    private void setPowerControl(float f, int i) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.1F) f = 1.1F;
        this.FM.CT.setPowerControl(f, i - 1);
        this.hudPower(f);
    }

    private void setPropControl(float f, int i) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.0F) f = 1.0F;
        this.FM.CT.setStepControl(f, i - 1);
    }

    public void resetUser() {
        this.resetGame();
    }

    private void setRadiatorControl(float f) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.0F) f = 1.0F;
        if (!this.FM.EI.isSelectionHasControlRadiator()) return;
        if (this.FM.CT.getRadiatorControlAuto()) {
            if (f > 0.8F) return;
            this.FM.CT.setRadiatorControlAuto(false, this.FM.EI);
            if (com.maddox.il2.ai.World.cur().diffCur.ComplexEManagement) {
                this.FM.CT.setRadiatorControl(f);
                com.maddox.il2.game.HUD.log(hudLogPowerId, "Radiator {0}%", new java.lang.Object[] { new Integer(java.lang.Math.round(this.FM.CT.getRadiatorControl() * 100F)) });
            } else {
                this.FM.CT.setRadiatorControl(1.0F);
                com.maddox.il2.game.HUD.log("RadiatorON");
            }
            return;
        }
        if (com.maddox.il2.ai.World.cur().diffCur.ComplexEManagement) {
            if (this.FM.actor instanceof com.maddox.il2.objects.air.MOSQUITO) {
                if (f > 0.5F) {
                    this.FM.CT.setRadiatorControl(1.0F);
                    com.maddox.il2.game.HUD.log("RadiatorON");
                } else {
                    this.FM.CT.setRadiatorControl(0.0F);
                    com.maddox.il2.game.HUD.log("RadiatorOFF");
                }
            } else {
                this.FM.CT.setRadiatorControl(f);
                com.maddox.il2.game.HUD.log(hudLogPowerId, "Radiator {0}%", new java.lang.Object[] { new Integer(java.lang.Math.round(this.FM.CT.getRadiatorControl() * 100F)) });
            }
        } else {
            this.FM.CT.setRadiatorControlAuto(true, this.FM.EI);
            com.maddox.il2.game.HUD.log("RadiatorOFF");
        }
    }

    private boolean setPilot() {
        this.FM = null;
        if (!Actor.isAlive(World.getPlayerAircraft())) // System.out.println("setPilot returning false - 1");
            return false;
        if (World.isPlayerParatrooper()) // System.out.println("setPilot returning false - 2");
            return false;
        if (World.isPlayerDead()) // System.out.println("setPilot returning false - 3");
            return false;
        com.maddox.il2.fm.FlightModel flightmodel = World.getPlayerFM();
        if (flightmodel == null) // System.out.println("setPilot returning false - 4");
            return false;
        if (flightmodel instanceof RealFlightModel) {
            this.FM = flightmodel;
            return ((RealFlightModel) flightmodel).isRealMode();
        }
        // TODO: Added by |ZUTI| - changed to enable gunners as bombardier.
        // -----------------------------------------------------------------
        else if (ZutiSupportMethods.IS_ACTING_INSTRUCTOR || Main3D.cur3D().cockpitCur instanceof CockpitPilot) {
            // System.out.println("AicraftHotKeys - player can operate the plane he is in!");
            this.FM = flightmodel;
            return true;
        } else return false;
    }

    private void setPowerControl(float f) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.1F) f = 1.1F;
        this.lastPower = f;
        this.FM.CT.setPowerControl(f);
        this.hudPower(this.FM.CT.PowerControl);
    }

    private void setPropControl(float f) {
        if (World.cur().diffCur.ComplexEManagement) {
            if (f < 0.0F) f = 0.0F;
            if (f > 1.0F) f = 1.0F;
            this.lastProp = f;
            if (!this.FM.EI.isSelectionAllowsAutoProp()) this.bPropAuto = false;
            if (!this.bPropAuto) {
                this.FM.CT.setStepControlAuto(false);
                this.FM.CT.setStepControl(f);
                HUD.log(hudLogPowerId, "PropPitch", new Object[] { new Integer(Math.round(this.FM.CT.getStepControl() * 100.0F)) });
            }
        }
    }

    private void setMixControl(float f) {
        if (World.cur().diffCur.ComplexEManagement) {
            if (f < 0.0F) f = 0.0F;
            if (f > 1.2F) f = 1.2F;
            if (this.FM.EI.getFirstSelected() != null) {
                this.FM.EI.setMix(f);
                f = this.FM.EI.getFirstSelected().getControlMix();
                this.FM.CT.setMixControl(f);
                HUD.log(hudLogPowerId, "PropMix", new Object[] { new Integer(Math.round(this.FM.CT.getMixControl() * 100.0F)) });
            }
        }
    }

    private void hudPower(float f) {
        HUD.log(hudLogPowerId, "Power", new Object[] { new Integer(Math.round(f * 100.0F)) });
    }

    private void hudWeapon(boolean bool, int weaponId) {
        String weaponName = "";

        boolean hasBullets = false;
        BulletEmitter[] bulletemitters = this.FM.CT.Weapons[weaponId];
        if (bulletemitters != null) {
            for (int i = 0; i < bulletemitters.length; i++)
                if (bulletemitters[i] != null && bulletemitters[i].haveBullets()) {
                    hasBullets = true;

                    // TODO: Added by |ZUTI|
                    weaponName = bulletemitters[i].toString();

                    break;
                }
            if (!bool) ForceFeedback.fxTriggerShake(weaponId, false);
            else if (hasBullets) {
                ForceFeedback.fxTriggerShake(weaponId, true);

                // TODO: Added by |ZUTI|: 3 = bombs or other ordinance
                if (weaponId == 3) ZutiWeaponsManagement.processDropCargoEvent(weaponName);
            } else HUD.log(hudLogWeaponId, "OutOfAmmo");
        }
    }

    private void setMissionMods() {
        this.bExtViewEnemy = false;
        this.bExtViewFriendly = false;
        this.bExtViewSelf = false;
        this.bExtViewGround = false;
        this.bExtViewDead = false;
        this.iExtViewGround = -1;
        this.iExtViewDead = -1;
        this.bPadlockEnemy = false;
        this.bPadlockFriendly = false;
        this.bExtPadlockEnemy = false;
        this.bExtPadlockFriendly = false;
        this.bViewExternalOnGround = false;
        this.bViewExternalWhenDead = false;
        this.bViewExternalFriendlies = false;
        if (Mission.cur().sectFile() != null) {
            
            // TODO: +++ Changed by SAS~Storebror, default to enabling external views on ground +++
//            if (Mission.cur().sectFile().get("Mods", "ViewExternalSelf", 0) > 0) this.bViewExternalSelf = true;
//            if (Mission.cur().sectFile().get("Mods", "ViewExternalOnGround", 0) > 0) this.bViewExternalOnGround = true;
//            if (Mission.cur().sectFile().get("Mods", "ViewExternalWhenDead", 0) > 0) this.bViewExternalWhenDead = true;
//            if (Mission.cur().sectFile().get("Mods", "ViewExternalFriendlies", 0) > 0) this.bViewExternalFriendlies = true;
//            int i = Mission.cur().sectFile().get("Mods", "ExternalViewLevel", -1);
            if (Mission.cur().sectFile().get("Mods", "ViewExternalSelf", 0) > 0) this.bViewExternalSelf = true;
            if (Mission.cur().sectFile().get("Mods", "ViewExternalOnGround", 1) > 0) this.bViewExternalOnGround = true;
            if (Mission.cur().sectFile().get("Mods", "ViewExternalWhenDead", 1) > 0) this.bViewExternalWhenDead = true;
            if (Mission.cur().sectFile().get("Mods", "ViewExternalFriendlies", 0) > 0) this.bViewExternalFriendlies = true;
            int i = Mission.cur().sectFile().get("Mods", "ExternalViewLevel", -1);
            // TODO: --- Changed by SAS~Storebror, default to enabling external views on ground ---
            if (i == 2) {
                this.bExtViewEnemy = true;
                this.bExtViewFriendly = true;
                this.bExtViewSelf = true;
            } else if (i == 1 || this.bViewExternalFriendlies) {
                this.bExtViewFriendly = true;
                this.bExtViewSelf = true;
            } else if (i == 0 || this.bViewExternalSelf) this.bExtViewSelf = true;
            this.iExtViewGround = Mission.cur().sectFile().get("Mods", "ExternalViewGround", -1);
            if (this.iExtViewGround == 1 || this.bViewExternalOnGround) this.bExtViewGround = true;
            this.iExtViewDead = Mission.cur().sectFile().get("Mods", "ExternalViewDead", -1);
            if (this.iExtViewDead == 1 || this.bViewExternalWhenDead) this.bExtViewDead = true;
            int i_10_ = Mission.cur().sectFile().get("Mods", "PadlockLevel", -1);
            if (i_10_ == 2) {
                this.bPadlockEnemy = true;
                this.bPadlockFriendly = true;
            } else if (i_10_ == 1) this.bPadlockFriendly = true;
            int i_11_ = Mission.cur().sectFile().get("Mods", "ExternalPadlockLevel", -1);
            if (i_11_ == 2) {
                this.bExtPadlockEnemy = true;
                this.bExtPadlockFriendly = true;
            } else if (i_11_ == 1) this.bExtPadlockFriendly = true;
            this.bMissionModsSet = true;
        }
    }

    public boolean viewAllowed(boolean bool) {
        if (!World.cur().diffCur.No_Outside_Views) return true;
//        if (!bool) return false;
//        if (bool && this.iExtViewGround == -1 && this.iExtViewDead == -1) return true;
        if (this.bExtViewGround && Actor.isValid(World.getPlayerAircraft()) && this.FM != null && this.FM.Gears.onGround()) return true;
        return this.bExtViewDead && (World.isPlayerParatrooper() || World.isPlayerDead() || this.FM == null || this.FM.isReadyToDie() || !Actor.isValid(World.getPlayerAircraft()));
    }

    private boolean hasBayDoors() {
//        boolean bool = false;
//        if ((Aircraft) this.FM.actor instanceof A_20 || (Aircraft) this.FM.actor instanceof B_17 || (Aircraft) this.FM.actor instanceof B_24 || (Aircraft) this.FM.actor instanceof B_25 || (Aircraft) this.FM.actor instanceof B_29X || (Aircraft) this.FM.actor instanceof Halifax || (Aircraft) this.FM.actor instanceof BLENHEIM || (Aircraft) this.FM.actor instanceof DO_335 || (Aircraft) this.FM.actor instanceof MOSQUITO || (Aircraft) this.FM.actor instanceof HE_111H2 || (Aircraft) this.FM.actor instanceof IL_10 || (Aircraft) this.FM.actor instanceof JU_88A4 || ((Aircraft) this.FM.actor instanceof ME_210 && !((Aircraft) this.FM.actor instanceof ME_210CA1ZSTR)) || (Aircraft) this.FM.actor instanceof PE_2 || (Aircraft) this.FM.actor instanceof PE_8 || (Aircraft) this.FM.actor instanceof R_10 || (Aircraft) this.FM.actor instanceof SB || (Aircraft) this.FM.actor instanceof SU_2 || (Aircraft) this.FM.actor instanceof TB_3 || (Aircraft) this.FM.actor instanceof IL_2 || (Aircraft) this.FM.actor instanceof IL_4
//                || (Aircraft) this.FM.actor instanceof FW_200 || (Aircraft) this.FM.actor instanceof KI_21 || (Aircraft) this.FM.actor instanceof YAK_9B || (Aircraft) this.FM.actor instanceof TU_2S || (Aircraft) this.FM.actor instanceof TBF || (Aircraft) this.FM.actor instanceof CantZ1007 || (Aircraft) this.FM.actor instanceof Do217_K1 || (Aircraft) this.FM.actor instanceof Halifax || (Aircraft) this.FM.actor instanceof JU_88C6 || (Aircraft) this.FM.actor instanceof MOSQUITO || (Aircraft) this.FM.actor instanceof LANCASTER || (Aircraft) this.FM.actor instanceof PZL37 || (Aircraft) this.FM.actor instanceof SM79 || (Aircraft) this.FM.actor instanceof CantZ1007bis || (Aircraft) this.FM.actor instanceof CantZ1007 || (Aircraft) this.FM.actor instanceof Do217_K1 || (Aircraft) this.FM.actor instanceof Do217 || (Aircraft) this.FM.actor instanceof Do217_K2)
//            bool = true;
//        try {
//            if ((Aircraft) this.FM.actor instanceof SM79)
//                bool = true;
//        } catch (Throwable throwable) {
//        }
//        try {
//            if ((Aircraft) this.FM.actor instanceof P2V)
//                bool = true;
//        } catch (Throwable throwable) {
//        }
//        return bool;
        return this.FM.CT.bHasBayDoorControl;
    }

    // TODO: Changed by |ZUTI|: from private to public.
    public void doCmdPilot(int i, boolean bool) {
        if (!this.setBombAimerAircraft()) return;
        if (bool) switch (i) {
            case 139:
            case 140:
            case 149:
            case 150:
                this.doCmdPilotTick(i, bool);
                return;

            case AUX_A:
                this.bAAircraft.auxPressed(1);
                return;
            case AUX_B:
                this.bAAircraft.auxPressed(2);
                return;
            case AUX_C:
                this.bAAircraft.auxPressed(3);
                return;
            case AUX_D:
                this.bAAircraft.auxPressed(4);
                return;
            case AUX_E:
                this.bAAircraft.auxPressed(5);
                return;
                
            case MISC_1:
                this.bAAircraft.auxPressed(20);
                return;
            case MISC_2:
                this.bAAircraft.auxPressed(21);
                return;
            case MISC_3:
                this.bAAircraft.auxPressed(22);
                return;
            case MISC_4:
                this.bAAircraft.auxPressed(23);
                return;
            case MISC_5:
                this.bAAircraft.auxPressed(24);
                return;
            case MISC_6:
                this.bAAircraft.auxPressed(25);
                return;
            case MISC_7:
                this.bAAircraft.auxPressed(26);
                return;
            case MISC_8:
                this.bAAircraft.auxPressed(27);
                return;
            case MISC_9:
                this.bAAircraft.auxPressed(28);
                return;
            case MISC_10:
                this.bAAircraft.auxPressed(29);
                return;
                
        }
        if (this.setPilot()) {
            Aircraft aircraft = (Aircraft) this.FM.actor;

            // TODO: Added by |ZUTI|
            // ---------------------------------------------------------------------------
            if (!ZutiSupportMethods_Multicrew.canExecutePilotOrBombardierAction(aircraft, i, bool)) return;
            // ---------------------------------------------------------------------------

// boolean bool_12_ = false;
            boolean bool_13_ = false;
            switch (i) {
                case 16:
                    this.FM.CT.WeaponControl[0] = bool;
                    this.hudWeapon(bool, 0);
                    break;
                case 17:
                    this.FM.CT.WeaponControl[1] = bool;
                    this.hudWeapon(bool, 1);
                    break;
                case 18:
                    this.FM.CT.WeaponControl[2] = bool;
                    this.hudWeapon(bool, 2);
                    break;
                case 19:
                    if (this.bBombBayDoors && this.hasBayDoors()) this.FM.CT.bHasBayDoors = true;

                    if (aircraft instanceof TypeHasToKG && this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0] instanceof TorpedoGun && this.FM.CT.Weapons[3][0].haveBullets()) {
                        this.FM.AS.replicateGyroAngleToNet();
                        this.FM.AS.replicateSpreadAngleToNet();
                    }

                    // TODO: Added by |ZUTI|
                    // ----------------------------------------------------------------
                    // Can we drop bombs at all? If not, send data to the one that can!
                    if (ZutiSupportMethods_Multicrew.mustSyncACOperation((Aircraft) this.FM.actor)) {
                        // Each key press executes this command twice. That means that on the client end
                        // user receives TWO bomb release commands which is not desirable.
                        if (this.zutiBombReleaseCounter > 0) {
                            Aircraft ac = (Aircraft) this.FM.actor;
                            // Bomb drop must be synced
                            ZutiSupportMethods_NetSend.bombardierReleasedOrdinance_ToServer(ac.name(), true, this.FM.CT.bHasBayDoors);
                            this.zutiBombReleaseCounter = 0;
                            return;
                        }
                        this.zutiBombReleaseCounter++;
                    }
                    // ----------------------------------------------------------------
                    else this.FM.CT.WeaponControl[3] = bool;

                    this.hudWeapon(bool, 3);
                    break;
                case 64:
                    this.FM.CT.WeaponControl[0] = bool;
                    this.hudWeapon(bool, 0);
                    this.FM.CT.WeaponControl[1] = bool;
                    this.hudWeapon(bool, 1);
                    break;
                case 73: // 'I'
                    this.FM.CT.setElectricPropUp(bool);
                    break;

                case 74: // 'J'
                    this.FM.CT.setElectricPropDn(bool);
                    break;
            }
            if (!bool) switch (i) {
                default:
                    break;
                case 71:
                    // TODO: Storebror, get rid of "instanceof" name references crashing for missing dependencies,
                    // add conf.ini selectable "stabilizers for all aircraft"

// //TODO: Modified to ensure 410 compatibility
// if (aircraft instanceof TypeBomber || aircraft instanceof DO_335 /*
// * ||
// * aircraft
// * instanceof
// * F84G1
// * ||
// * aircraft
// * instanceof
// * F84G1
// */)

                    boolean canUseStabs = false;
                    do {
                        if (aircraft instanceof TypeBomber) {
                            canUseStabs = true;
                            break;
                        }
                        if (aircraft instanceof DO_335) {
                            canUseStabs = true;
                            break;
                        }
                        String aircraftClassName = aircraft.getClass().getName();
                        // Remove "com.maddox.il2.objects.air." from classname
                        if (aircraftClassName.length() > 27) {
                            aircraftClassName = aircraftClassName.substring(27);
                            if (aircraftClassName.startsWith("F84G")) {
                                canUseStabs = true;
                                break;
                            }
                        }
                        if (Config.cur.bStabs4All) {
                            canUseStabs = true;
                            break;
                        }
                    } while (false);

                    if (canUseStabs) {
                        this.FM.CT.StabilizerControl = !this.FM.CT.StabilizerControl;
                        HUD.log("Stabilizer" + (this.FM.CT.StabilizerControl ? "On" : "Off"));
                    }
                    break;
                case 15:
                    if (aircraft.isGunPodsExist()) {
                        if (aircraft.isGunPodsOn()) {
                            aircraft.setGunPodsOn(false);
                            HUD.log("GunPodsOff");
                        } else {
                            aircraft.setGunPodsOn(true);
                            HUD.log("GunPodsOn");
                        }
                        break;
                    }
                    break;
                case 1:
                case 2:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.RudderControl = 0.0F;
                    break;
                case 0:
                    this.FM.CT.BrakeControl = 0.0F;
                    break;
                case 3:
                case 4:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.ElevatorControl = 0.0F;
                    break;
                case 5:
                case 6:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.AileronControl = 0.0F;
                    break;
                case 54:
                    if (!this.FM.Gears.onGround() && !(this.FM.CT.GearControl <= 0.0F) && this.FM.Gears.isOperable() && !this.FM.Gears.isHydroOperable()) {
                        this.FM.CT.GearControl -= 0.02F;
                        if (this.FM.CT.GearControl <= 0.0F) {
                            this.FM.CT.GearControl = 0.0F;
                            HUD.log("GearUp");
                        }
                    }
                    break;
                case 55:
                    if (!this.FM.Gears.onGround() && !(this.FM.CT.GearControl >= 1.0F) && this.FM.Gears.isOperable() && !this.FM.Gears.isHydroOperable()) {
                        this.FM.CT.GearControl += 0.02F;
                        if (this.FM.CT.GearControl >= 1.0F) {
                            this.FM.CT.GearControl = 1.0F;
                            HUD.log("GearDown");
                        }
                    }
                    break;
                case 63:
                    if (this.FM.CT.bHasAirBrakeControl) {
                        this.FM.CT.AirBrakeControl = this.FM.CT.AirBrakeControl <= 0.5F ? 1.0F : 0.0F;
                        HUD.log("Divebrake" + (this.FM.CT.AirBrakeControl != 0.0F ? "ON" : "OFF"));
                    }
                    break;
                case 70:
                    if (!World.cur().diffCur.SeparateEStart || this.FM.EI.getNumSelected() <= 1 || this.FM.EI.getFirstSelected().getStage() != 0) {
                        this.FM.EI.toggle();
                        break;
                    }
                    break;
                case 126:
                    if (this.FM.actor instanceof TypeDockable) if (((TypeDockable) this.FM.actor).typeDockableIsDocked()) ((TypeDockable) this.FM.actor).typeDockableAttemptDetach();
                    else((TypeDockable) this.FM.actor).typeDockableAttemptAttach();
            }
            else switch (i) {
                default:
                    break;
                case 7:
                    if (this.bSeparateRadiatorOpenClose) {
                        if (this.FM.EI.isSelectionHasControlRadiator()) if (this.FM.CT.getRadiatorControlAuto()) {
                            this.FM.CT.setRadiatorControlAuto(false, this.FM.EI);
                            if (World.cur().diffCur.ComplexEManagement) {
                                this.FM.CT.setRadiatorControl(0.0F);
                                HUD.log("RadiatorControl" + (int) (this.FM.CT.getRadiatorControl() * 10.0F));
                            } else {
                                this.FM.CT.setRadiatorControl(1.0F);
                                HUD.log("RadiatorON");
                            }
                        } else if (World.cur().diffCur.ComplexEManagement) {
                            if (this.FM.CT.getRadiatorControl() != 1.0F) {
                                if (this.FM.actor instanceof MOSQUITO) this.FM.CT.setRadiatorControl(1.0F);
                                else this.FM.CT.setRadiatorControl(this.FM.CT.getRadiatorControl() + 0.2F);
                                HUD.log("RadiatorControl" + (int) (this.FM.CT.getRadiatorControl() * 10.0F));
                            }
                        } else {
                            this.FM.CT.setRadiatorControlAuto(true, this.FM.EI);
                            HUD.log("RadiatorOFF");
                        }
                    } else if (this.FM.EI.isSelectionHasControlRadiator()) if (this.FM.CT.getRadiatorControlAuto()) {
                        this.FM.CT.setRadiatorControlAuto(false, this.FM.EI);
                        if (World.cur().diffCur.ComplexEManagement) {
                            this.FM.CT.setRadiatorControl(0.0F);
                            HUD.log("RadiatorControl" + (int) (this.FM.CT.getRadiatorControl() * 10.0F));
                        } else {
                            this.FM.CT.setRadiatorControl(1.0F);
                            HUD.log("RadiatorON");
                        }
                    } else if (World.cur().diffCur.ComplexEManagement) {
                        if (this.FM.CT.getRadiatorControl() == 1.0F) {
                            if (this.FM.EI.isSelectionAllowsAutoRadiator()) {
                                this.FM.CT.setRadiatorControlAuto(true, this.FM.EI);
                                HUD.log("RadiatorOFF");
                            } else {
                                this.FM.CT.setRadiatorControl(0.0F);
                                HUD.log("RadiatorControl" + (int) (this.FM.CT.getRadiatorControl() * 10.0F));
                            }
                        } else {
                            if (this.FM.actor instanceof MOSQUITO) this.FM.CT.setRadiatorControl(1.0F);
                            else this.FM.CT.setRadiatorControl(this.FM.CT.getRadiatorControl() + 0.2F);
                            HUD.log("RadiatorControl" + (int) (this.FM.CT.getRadiatorControl() * 10.0F));
                        }
                    } else {
                        this.FM.CT.setRadiatorControlAuto(true, this.FM.EI);
                        HUD.log("RadiatorOFF");
                    }
                    break;
                case 0:
                    this.FM.CT.BrakeControl = 1.0F;
                    break;
                case 3:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.ElevatorControl = -1.0F;
                    break;
                case 4:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.ElevatorControl = 1.0F;
                    break;
                case 5:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.AileronControl = -1.0F;
                    break;
                case 6:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.AileronControl = 1.0F;
                    break;
                case 72:
                    if (this.FM.CT.bHasLockGearControl) {
                        this.FM.Gears.bTailwheelLocked = !this.FM.Gears.bTailwheelLocked;
                        HUD.log("TailwheelLock" + (this.FM.Gears.bTailwheelLocked ? "ON" : "OFF"));
                    }
                    break;
                case 1:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.RudderControl = -1.0F;
                    break;
                case 56:
                    if (!this.FM.CT.StabilizerControl && this.FM.CT.RudderControl > -1.0F) this.FM.CT.RudderControl -= 0.1F;
                    break;
                case 57:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.RudderControl = 0.0F;
                    break;
                case 2:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.RudderControl = 1.0F;
                    break;
                case 58:
                    if (!this.FM.CT.StabilizerControl && this.FM.CT.RudderControl < 1.0F) this.FM.CT.RudderControl += 0.1F;
                    break;
                case 20:
                    this.setPowerControl(0.0F);
                    break;
                // TODO: Added by |ZUTI| for Bomb bay door mod:
                // ----------------------------------------------
                case 131: {
                    if (this.bBombBayDoors) if (this.hasBayDoors()) {
                        this.FM.CT.bHasBayDoors = true;
                        if (this.FM.CT.BayDoorControl != 0.0F) {
                            // TODO: Added by |ZUTI| - notify users that AC closed it's bomb bay doors
                            if (ZutiSupportMethods_Multicrew.mustSyncACOperation(aircraft)) // Bomb drop must be synced
                                ZutiSupportMethods_NetSend.bombBayDoorsStatus(0, World.getPlayerAircraft().name());
                            // System.out.println("AircraftHotKeys - synced bomb bay doors to 0");
                            else this.FM.CT.BayDoorControl = 0.0F;

                            HUD.log("BayDoorsClosed");
                        } else {
                            // TODO: Added by |ZUTI| - notify users that AC opened it's bomb bay doors
                            if (ZutiSupportMethods_Multicrew.mustSyncACOperation(aircraft)) // Bomb drop must be synced
                                ZutiSupportMethods_NetSend.bombBayDoorsStatus(1, World.getPlayerAircraft().name());
                            // System.out.println("AircraftHotKeys - synced bomb bay doors to 1");
                            else this.FM.CT.BayDoorControl = 1.0F;

                            HUD.log("BayDoorsOpen");
                        }
                    }
                    break;
                }
                // ----------------------------------------------

                // +++++ TODO skylla: enhanced weapon release control ++++++
                // select rocket:
                case 132: {
                    this.FM.CT.toggleRocketSelectedHUD(hudLogWeaponId);
                    this.replicateEWRCSettingsToNet();
                    break;
                }
                // rocket salvo size:
                case 133: {
                    this.FM.CT.toggleRocketFireMode(hudLogWeaponId);
                    this.replicateEWRCSettingsToNet();
                    break;
                }
                // rocket release delay:
                case 134: {
                    this.FM.CT.toggleRocketReleaseDelayHUD(hudLogWeaponId);
                    this.replicateEWRCSettingsToNet();
                    break;
                }
                // select bomb:
                case 135: {
                    this.FM.CT.toggleBombSelectedHUD(hudLogWeaponId);
                    this.replicateEWRCSettingsToNet();
                    break;
                }
                // bomb salvo size:
                case 136: {
                    this.FM.CT.toggleBombDropMode(hudLogWeaponId);
                    this.replicateEWRCSettingsToNet();
                    break;
                }
                // bomb release delay:
                case 137: {
                    this.FM.CT.toggleBombReleaseDelayHUD(hudLogWeaponId);
                    this.replicateEWRCSettingsToNet();
                    break;
                }
                // ----- todo skylla: enhanced weapon release control -----

                case 21:
                    this.setPowerControl(0.1F);
                    break;
                case 22:
                    if (this.bToggleMusic) {
                        if (this.bMusicOn) {
                            CmdMusic.setCurrentVolume(0.0F);
                            this.bMusicOn = false;
                        } else {
                            CmdMusic.setCurrentVolume(1.0F);
                            this.bMusicOn = true;
                        }
                    } else this.setPowerControl(0.2F);
                    break;
                case 23:
                    if (this.bSeparateRadiatorOpenClose) {
                        if (this.FM.EI.isSelectionHasControlRadiator() && !this.FM.CT.getRadiatorControlAuto()) if (World.cur().diffCur.ComplexEManagement) {
                            if (this.FM.CT.getRadiatorControl() == 0.0F) {
                                if (this.FM.EI.isSelectionAllowsAutoRadiator()) {
                                    this.FM.CT.setRadiatorControlAuto(true, this.FM.EI);
                                    HUD.log("RadiatorOFF");
                                }
                            } else {
                                if (this.FM.actor instanceof MOSQUITO) this.FM.CT.setRadiatorControl(0.0F);
                                else this.FM.CT.setRadiatorControl(this.FM.CT.getRadiatorControl() - 0.2F);
                                HUD.log("RadiatorControl" + (int) (this.FM.CT.getRadiatorControl() * 10.0F));
                            }
                        } else {
                            this.FM.CT.setRadiatorControlAuto(true, this.FM.EI);
                            HUD.log("RadiatorOFF");
                        }
                    } else this.setPowerControl(0.3F);
                    break;
                case 24:
                    if (this.bSideDoor) {
//                            boolean bool_14_ = false;
//                            try {
//                                // TODO: Disabled for 410 compatibility
//                                /*
//                                 * if ((Aircraft)FM.actor instanceof
//                                 * SPITFIRELF14E)
//                                 * bool_14_ = true;
//                                 */
//                            } catch (Throwable throwable) {
//                                /* empty */
//                            }
//                            try {
//                                // TODO: Disabled for 410 compatibility
//                                /*
//                                 * if ((Aircraft)FM.actor instanceof
//                                 * SPITFIRE14C)
//                                 * bool_14_ = true;
//                                 */
//                            } catch (Throwable throwable) {
//                                /* empty */
//                            }
//                            try {
//                                // TODO: Disabled for 410 compatibility
//                                /*
//                                 * if ((Aircraft)FM.actor instanceof SPITFIRE12)
//                                 * bool_14_ = true;
//                                 */
//                            } catch (Throwable throwable) {
//                                /* empty */
//                            }
//                            if (bool_14_) {

//                       		System.out.println("AircraftHotKeys bHasSideDoorControl=" + this.FM.CT.bHasSideDoorControl + " (1)!");
                        if (this.FM.CT.bHasSideDoorControl) {
                            this.FM.CT.setActiveDoor(this.SIDE_DOOR);
                            if (this.FM.CT.cockpitDoorControl < 0.5F && this.FM.CT.getCockpitDoor() < 0.01F) this.FM.AS.setCockpitDoor(aircraft, 1);
                            else if (this.FM.CT.cockpitDoorControl > 0.5F && this.FM.CT.getCockpitDoor() > 0.99F) this.FM.AS.setCockpitDoor(aircraft, 0);
                        }
                    } else this.setPowerControl(0.4F);
                    break;
                case 25:
                    if (this.bAllowDumpFuel) {
                        // TODO: Disabled for 410 compatibility
                        /*
                         * if ((Aircraft)FM.actor instanceof F9F) { if (bDumpFuel) bDumpFuel = false; else bDumpFuel = true; //TODO: Not available!? //FM.AS.setDumpFuelState(bDumpFuel); }
                         */
                    } else this.setPowerControl(0.5F);
                    break;
                case 26:
                    this.setPowerControl(0.6F);
                    break;
                case 27:
                    this.setPowerControl(0.7F);
                    break;
                case 28:
                    this.setPowerControl(0.8F);
                    break;
                case 29:
                    this.setPowerControl(0.9F);
                    break;
                case 30:
                    this.setPowerControl(1.0F);
                    break;
                case 59:
                    this.setPowerControl(this.FM.CT.PowerControl + 0.05F);
                    break;
                case 60:
                    this.setPowerControl(this.FM.CT.PowerControl - 0.05F);
                    break;
                case 61:
                    this.setAfterburner(!this.bAfterburner);
                    break;
                case 31:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.0F);
                    break;
                case 32:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.1F);
                    break;
                case 33:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.2F);
                    break;
                case 34:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.3F);
                    break;
                case 35:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.4F);
                    break;
                case 36:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.5F);
                    break;
                case 37:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.6F);
                    break;
                case 38:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.7F);
                    break;
                case 39:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.8F);
                    break;
                case 40:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(0.9F);
                    break;
                case 41:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(1.0F);
                    break;
                case 73:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(this.lastProp + 0.05F);
                    break;
                case 74:
                    if (this.FM.EI.isSelectionHasControlProp()) this.setPropControl(this.lastProp - 0.05F);
                    break;
                case 42:
                    if (this.FM.EI.isSelectionHasControlProp() && World.cur().diffCur.ComplexEManagement) {
                        this.setPropAuto(!this.bPropAuto);
                        if (this.bPropAuto) {
                            HUD.log("PropAutoPitch");
                            this.lastProp = this.FM.CT.getStepControl();
                            this.FM.CT.setStepControlAuto(true);
                        } else {
                            this.FM.CT.setStepControlAuto(false);
                            this.setPropControl(this.lastProp);
                        }
                    }
                    break;
                case 114:
                    if (this.FM.EI.isSelectionHasControlFeather() && World.cur().diffCur.ComplexEManagement && this.FM.EI.getFirstSelected() != null) this.FM.EI.setFeather(this.FM.EI.getFirstSelected().getControlFeather() != 0 ? 0 : 1);
                    break;
                case 75:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.0F);
                    break;
                case 76:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.1F);
                    break;
                case 77:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.2F);
                    break;
                case 78:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.3F);
                    break;
                case 79:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.4F);
                    break;
                case 80:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.5F);
                    break;
                case 81:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.6F);
                    break;
                case 82:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.7F);
                    break;
                case 83:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.8F);
                    break;
                case 84:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(0.9F);
                    break;
                case 85:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(1.0F);
                    break;
                case 86:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(this.FM.CT.getMixControl() + 0.2F);
                    break;
                case 87:
                    if (this.FM.EI.isSelectionHasControlMix()) this.setMixControl(this.FM.CT.getMixControl() - 0.2F);
                    break;
                case 89:
                    if (!this.bSeparateGearUpDown) {
                        if (this.FM.EI.isSelectionHasControlMagnetos() && this.FM.EI.getFirstSelected() != null && this.FM.EI.getFirstSelected().getControlMagnetos() > 0) {
                            this.FM.CT.setMagnetoControl(this.FM.EI.getFirstSelected().getControlMagnetos() - 1);
                            HUD.log("MagnetoSetup" + this.FM.CT.getMagnetoControl());
                        }
                    } else if (this.FM.CT.bHasGearControl && !this.FM.Gears.onGround() && this.FM.Gears.isHydroOperable()) {
                        if (this.FM.CT.GearControl > 0.5F && this.FM.CT.getGear() > 0.99F) {
                            this.FM.CT.GearControl = 0.0F;
                            HUD.log("GearUp");
                        }
                        if (this.FM.Gears.isAnyDamaged()) HUD.log("GearDamaged");
                    }
                    break;
                case 88:
                    if (!this.bSeparateHookUpDown) {
                        if (this.FM.EI.isSelectionHasControlMagnetos() && this.FM.EI.getFirstSelected() != null && this.FM.EI.getFirstSelected().getControlMagnetos() < 3) {
                            this.FM.CT.setMagnetoControl(this.FM.EI.getFirstSelected().getControlMagnetos() + 1);
                            HUD.log("MagnetoSetup" + this.FM.CT.getMagnetoControl());
                        }
                    } else if (this.FM.CT.bHasArrestorControl && this.FM.CT.arrestorControl > 0.5F) {
                        this.FM.AS.setArrestor(this.FM.actor, 0);
                        HUD.log("HookUp");
                    }
                    break;
                case 116:
                    try {
                        // TODO: Disabled for 410 compatibility
                        /*
                         * if (FM.actor instanceof P_51Mustang && World.cur().diffCur.ComplexEManagement) { if (!bMustangCompressorAuto) { if (FM.CT.getCompressorControl() == 0) { FM.EI.getFirstSelected().bHasCompressorControl = false;
                         * bMustangCompressorAuto = true; HUD.log("Supercharger: Auto"); } else {
                         *
                         * FM.CT.setCompressorControl(FM.EI.getFirstSelected( ).getControlCompressor() - 1); HUD.log("CompressorSetup" + FM.CT.getCompressorControl()); } } bool_13_ = true; }
                         */
                    } catch (Throwable throwable) {
                        /* empty */
                    }
                    if (!bool_13_ && this.FM.EI.isSelectionHasControlCompressor() && this.FM.EI.getFirstSelected() != null && World.cur().diffCur.ComplexEManagement) {
                        this.FM.CT.setCompressorControl(this.FM.EI.getFirstSelected().getControlCompressor() - 1);
                        HUD.log("CompressorSetup" + this.FM.CT.getCompressorControl());
                    }
                    break;
                case 115:
                    try {
                        // TODO: Disabled for 410 compatibility
                        /*
                         * if (FM.actor instanceof P_51Mustang && World.cur().diffCur.ComplexEManagement) { if (bMustangCompressorAuto) { FM.EI.getFirstSelected().bHasCompressorControl = true; FM.CT.setCompressorControl(0); bMustangCompressorAuto = false;
                         * HUD.log("CompressorSetup0"); } else {
                         *
                         * FM.CT.setCompressorControl(FM.EI.getFirstSelected( ).getControlCompressor() + 1); HUD.log("CompressorSetup" + FM.CT.getCompressorControl()); } bool_13_ = true; }
                         */
                    } catch (Throwable throwable) {
                        /* empty */
                    }
                    if (!bool_13_ && this.FM.EI.isSelectionHasControlCompressor() && this.FM.EI.getFirstSelected() != null && World.cur().diffCur.ComplexEManagement) {
                        this.FM.CT.setCompressorControl(this.FM.EI.getFirstSelected().getControlCompressor() + 1);
                        HUD.log("CompressorSetup" + this.FM.CT.getCompressorControl());
                    }
                    break;
                case 90:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControlAll(true);
                        HUD.log("EngineSelectAll");
                        break;
                    }
                    break;
                case 91:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControlAll(false);
                        HUD.log("EngineSelectNone");
                        break;
                    }
                    break;
                case 92:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControlAll(false);
                        int[] is = this.FM.EI.getSublist(this.FM.Scheme, 1);
                        for (int i_15_ = 0; i_15_ < is.length; i_15_++)
                            this.FM.EI.setCurControl(is[i_15_], true);
                        HUD.log("EngineSelectLeft");
                        break;
                    }
                    break;
                case 93:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControlAll(false);
                        int[] is = this.FM.EI.getSublist(this.FM.Scheme, 2);
                        for (int i_16_ = 0; i_16_ < is.length; i_16_++)
                            this.FM.EI.setCurControl(is[i_16_], true);
                        HUD.log("EngineSelectRight");
                        break;
                    }
                    break;
                case 94:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(0, true);
                        HUD.log("EngineSelect1");
                        break;
                    }
                    break;
                case 95:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(1, true);
                        HUD.log("EngineSelect2");
                        break;
                    }
                    break;
                case 96:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 3) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(2, true);
                        HUD.log("EngineSelect3");
                        break;
                    }
                    break;
                case 97:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 4) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(3, true);
                        HUD.log("EngineSelect4");
                        break;
                    }
                    break;
                case 98:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 5) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(4, true);
                        HUD.log("EngineSelect5");
                        break;
                    }
                    break;
                case 99:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 6) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(5, true);
                        HUD.log("EngineSelect6");
                        break;
                    }
                    break;
                case 100:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 7) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(6, true);
                        HUD.log("EngineSelect7");
                        break;
                    }
                    break;
                case 101:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 8) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(7, true);
                        HUD.log("EngineSelect8");
                        break;
                    }
                    break;
                case ENGINE_SELECT_9:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 9) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(8, true);
                        HUD.log("EngineSelect9");
                        break;
                    }
                    break;
                case ENGINE_SELECT_10:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 10) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(9, true);
                        HUD.log("EngineSelect10");
                        break;
                    }
                    break;
                case ENGINE_SELECT_11:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 11) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(10, true);
                        HUD.log("EngineSelect11");
                        break;
                    }
                    break;
                case ENGINE_SELECT_12:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1 && this.FM.EI.getNum() >= 12) {
                        this.FM.EI.setCurControlAll(false);
                        this.FM.EI.setCurControl(11, true);
                        HUD.log("EngineSelect12");
                        break;
                    }
                    break;
                case 102:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        for (int i_17_ = 0; i_17_ < this.FM.EI.getNum(); i_17_++)
                            this.FM.EI.setCurControl(i_17_, !this.FM.EI.getCurControl(i_17_));
                        HUD.log("EngineToggleAll");
                        break;
                    }
                    break;
                case 103:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        int[] is = this.FM.EI.getSublist(this.FM.Scheme, 1);
                        for (int i_18_ = 0; i_18_ < is.length; i_18_++)
                            this.FM.EI.setCurControl(is[i_18_], !this.FM.EI.getCurControl(is[i_18_]));
                        HUD.log("EngineToggleLeft");
                        break;
                    }
                    break;
                case 104:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        int[] is = this.FM.EI.getSublist(this.FM.Scheme, 2);
                        for (int i_19_ = 0; i_19_ < is.length; i_19_++)
                            this.FM.EI.setCurControl(is[i_19_], !this.FM.EI.getCurControl(is[i_19_]));
                        HUD.log("EngineToggleRight");
                        break;
                    }
                    break;
                case 105:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(0, !this.FM.EI.getCurControl(0));
                        HUD.log("EngineSelect1" + (this.FM.EI.getCurControl(0) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 106:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(1, !this.FM.EI.getCurControl(1));
                        HUD.log("EngineSelect2" + (this.FM.EI.getCurControl(1) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 107:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(2, !this.FM.EI.getCurControl(2));
                        HUD.log("EngineSelect3" + (this.FM.EI.getCurControl(2) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 108:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(3, !this.FM.EI.getCurControl(3));
                        HUD.log("EngineSelect4" + (this.FM.EI.getCurControl(3) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 109:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(4, !this.FM.EI.getCurControl(4));
                        HUD.log("EngineSelect5" + (this.FM.EI.getCurControl(4) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 110:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(5, !this.FM.EI.getCurControl(5));
                        HUD.log("EngineSelect6" + (this.FM.EI.getCurControl(5) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 111:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(6, !this.FM.EI.getCurControl(6));
                        HUD.log("EngineSelect7" + (this.FM.EI.getCurControl(6) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 112:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(7, !this.FM.EI.getCurControl(7));
                        HUD.log("EngineSelect8" + (this.FM.EI.getCurControl(7) ? "" : "OFF"));
                        break;
                    }
                    break;
                case ENGINE_TOGGLE_9:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(8, !this.FM.EI.getCurControl(8));
                        HUD.log("EngineSelect9" + (this.FM.EI.getCurControl(8) ? "" : "OFF"));
                        break;
                    }
                    break;
                case ENGINE_TOGGLE_10:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(9, !this.FM.EI.getCurControl(9));
                        HUD.log("EngineSelect10" + (this.FM.EI.getCurControl(9) ? "" : "OFF"));
                        break;
                    }
                    break;
                case ENGINE_TOGGLE_11:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(10, !this.FM.EI.getCurControl(10));
                        HUD.log("EngineSelect11" + (this.FM.EI.getCurControl(10) ? "" : "OFF"));
                        break;
                    }
                    break;
                case ENGINE_TOGGLE_12:
                    if (this.FM.Scheme != 0 && this.FM.Scheme != 1) {
                        this.FM.EI.setCurControl(11, !this.FM.EI.getCurControl(11));
                        HUD.log("EngineSelect12" + (this.FM.EI.getCurControl(11) ? "" : "OFF"));
                        break;
                    }
                    break;
                case 113:
                    if (this.FM.EI.isSelectionHasControlExtinguisher()) for (int i_20_ = 0; i_20_ < this.FM.EI.getNum(); i_20_++)
                        if (this.FM.EI.getCurControl(i_20_)) this.FM.EI.engines[i_20_].setExtinguisherFire();
                    break;
                case 53:
                    if (this.FM.CT.bHasFlapsControl) if (!this.FM.CT.bHasFlapsControlRed) {
                        if (this.FM.CT.FlapsControl < 0.2F) {
                            this.FM.CT.FlapsControl = 0.2F;
                            HUD.log("FlapsCombat");
                        } else if (this.FM.CT.FlapsControl < 0.33F) {
                            this.FM.CT.FlapsControl = 0.33F;
                            HUD.log("FlapsTakeOff");
                        } else if (this.FM.CT.FlapsControl < 1.0F) {
                            this.FM.CT.FlapsControl = 1.0F;
                            HUD.log("FlapsLanding");
                        }
                    } else if (this.FM.CT.FlapsControl < 0.5F) {
                        this.FM.CT.FlapsControl = 1.0F;
                        HUD.log("FlapsLanding");
                    }
                    break;
                case 52:
                    if (this.FM.CT.bHasFlapsControl) if (!this.FM.CT.bHasFlapsControlRed) {
                        if (this.FM.CT.FlapsControl > 0.33F) {
                            this.FM.CT.FlapsControl = 0.33F;
                            HUD.log("FlapsTakeOff");
                        } else if (this.FM.CT.FlapsControl > 0.2F) {
                            this.FM.CT.FlapsControl = 0.2F;
                            HUD.log("FlapsCombat");
                        } else if (this.FM.CT.FlapsControl > 0.0F) {
                            this.FM.CT.FlapsControl = 0.0F;
                            HUD.log("FlapsRaised");
                        }
                    } else if (this.FM.CT.FlapsControl > 0.5F) {
                        this.FM.CT.FlapsControl = 0.0F;
                        HUD.log("FlapsRaised");
                    }
                    break;
                case 9:
                    if (this.FM.CT.bHasGearControl && !this.FM.Gears.onGround() && this.FM.Gears.isHydroOperable()) {
                        if (this.FM.CT.GearControl > 0.5F && this.FM.CT.getGear() > 0.99F && !this.bSeparateGearUpDown) {
                            this.FM.CT.GearControl = 0.0F;
                            HUD.log("GearUp");
                        } else if (this.FM.CT.GearControl < 0.5F && this.FM.CT.getGear() < 0.01F) {
                            this.FM.CT.GearControl = 1.0F;
                            HUD.log("GearDown");
                        }
                        if (this.FM.Gears.isAnyDamaged()) HUD.log("GearDamaged");
                    }
                    break;
                case 129:
                    if (this.FM.CT.bHasArrestorControl) if (this.FM.CT.arrestorControl > 0.5F && !this.bSeparateHookUpDown) {
                        this.FM.AS.setArrestor(this.FM.actor, 0);
                        HUD.log("HookUp");
                    } else {
                        this.FM.AS.setArrestor(this.FM.actor, 1);
                        HUD.log("HookDown");
                    }
                    break;
                case 130:
                    if (this.FM.canChangeBrakeShoe) if (this.FM.brakeShoe) {
                        this.FM.brakeShoe = false;
                        HUD.log("BrakeShoeOff");
                    } else {
                        this.FM.brakeShoe = true;
                        HUD.log("BrakeShoeOn");
                    }
                    break;
                case 127:
                    if (this.FM.CT.bHasWingControl) if (this.FM.CT.wingControl < 0.5F && this.FM.CT.getWing() < 0.01F) {
                        this.FM.AS.setWingFold(aircraft, 1);
                        HUD.log("WingFold");
                    } else if (this.FM.CT.wingControl > 0.5F && this.FM.CT.getWing() > 0.99F) {
                        this.FM.AS.setWingFold(aircraft, 0);
                        HUD.log("WingExpand");
                    }
                    break;
                case 128:
                    if (this.FM.CT.bHasCockpitDoorControl) {
                        if (this.bSideDoor) // System.out.println("AircraftHotKeys bHasSideDoorControl=" + this.FM.CT.bHasSideDoorControl + " (2)!");
                            if (this.FM.CT.bHasSideDoorControl) this.FM.CT.setActiveDoor(this.COCKPIT_DOOR);
                        if (this.FM.CT.cockpitDoorControl < 0.5F && this.FM.CT.getCockpitDoor() < 0.01F) {
                            this.FM.AS.setCockpitDoor(aircraft, 1);
                            HUD.log("CockpitDoorOPN");
                        } else if (this.FM.CT.cockpitDoorControl > 0.5F && this.FM.CT.getCockpitDoor() > 0.99F) {
                            this.FM.AS.setCockpitDoor(aircraft, 0);
                            HUD.log("CockpitDoorCLS");
                        }
                    }
                    break;
                case 43:
                    if (this.FM.CT.bHasElevatorTrim) this.FM.CT.setTrimElevatorControl(0.0F);
                    break;
                case 44:
                    this.doCmdPilotTick(i, false);
                    break;
                case 45:
                    this.doCmdPilotTick(i, false);
                    break;
                case 46:
                    if (this.FM.CT.bHasAileronTrim) this.FM.CT.setTrimAileronControl(0.0F);
                    break;
                case 47:
                    this.doCmdPilotTick(i, false);
                    break;
                case 48:
                    this.doCmdPilotTick(i, false);
                    break;
                case 49:
                    if (this.FM.CT.bHasRudderTrim) this.FM.CT.setTrimRudderControl(0.0F);
                    break;
                case 50:
                    this.doCmdPilotTick(i, false);
                    break;
                case 51:
                    this.doCmdPilotTick(i, false);
                    break;
                case 125:
                    if (aircraft instanceof TypeBomber) {
                        // TODO: Added by |ZUTI|: assigning result to designated variable
                        aircraft.FM.CT.zutiSetBombsightAutomationEngaged(((TypeBomber) aircraft).typeBomberToggleAutomation());
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberToggleAutomation();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeFighterAceMaker) {
                        ((TypeFighterAceMaker) aircraft).typeFighterAceMakerToggleAutomation();
                        this.toTrackSign(i);
                    }
                    break;
                case 117:
                    this.doCmdPilotTick(i, false);
                    break;
                case 118:
                    this.doCmdPilotTick(i, false);
                    break;
                case 119:
                    this.doCmdPilotTick(i, false);
                    break;
                case 120:
                    this.doCmdPilotTick(i, false);
                    break;
                case 121:
                    this.doCmdPilotTick(i, false);
                    break;
                case 122:
                    this.doCmdPilotTick(i, false);
                    break;
                case 123:
                    this.doCmdPilotTick(i, false);
                    break;
                case 124:
                    this.doCmdPilotTick(i, false);
                    break;
                case 62:
                    this.FM.CT.dropFuelTanks();
            }
        }
    }

    // TODO: Added by |ZUTI|: added boolean zutiCheckCommand to avoid doubling of
    // canExecuteBombardierAction check...
    private void doCmdPilotTick(int i, boolean zutiCheckCommand) {
        // System.out.println("AircraftHotKeys - key pressed: " + i);
        if (!this.setBombAimerAircraft()) return;
        switch (i) {
            case 149:
                this.bAAircraft.auxPlus(1);
                this.toTrackSign(i);
                return;

            case 150:
                this.bAAircraft.auxMinus(1);
                this.toTrackSign(i);
                return;

            case 139:
                // TODO: Implement 4.10.1 Codechanges +++
                if (!World.cur().diffCur.RealisticNavigationInstruments) return;
                // TODO: Implement 4.10.1 Codechanges ---
                this.bAAircraft.beaconPlus();
                this.toTrackSign(i);
                return;

            case 140:
                // TODO: Implement 4.10.1 Codechanges +++
                if (!World.cur().diffCur.RealisticNavigationInstruments) return;
                // TODO: Implement 4.10.1 Codechanges ---
                this.bAAircraft.beaconMinus();
                this.toTrackSign(i);
                return;
        }

        if (this.setPilot()) {
            Aircraft aircraft = (Aircraft) this.FM.actor;

            // TODO: Added by |ZUTI|: if checkCommand parameter is true then check it and process
            if (zutiCheckCommand && !ZutiSupportMethods_Multicrew.canExecutePilotOrBombardierAction(aircraft, i, false)) return;

            switch (i) {
                default:
                    break;
                case 44:
                    if (this.FM.CT.bHasElevatorTrim && this.FM.CT.getTrimElevatorControl() < 0.5F) this.FM.CT.setTrimElevatorControl(this.FM.CT.getTrimElevatorControl() + 0.00625F);
                    break;
                case 45:
                    if (this.FM.CT.bHasElevatorTrim && this.FM.CT.getTrimElevatorControl() > -0.5F) this.FM.CT.setTrimElevatorControl(this.FM.CT.getTrimElevatorControl() - 0.00625F);
                    break;
                case 47:
                    if (this.FM.CT.bHasAileronTrim && this.FM.CT.getTrimAileronControl() < 0.5F) this.FM.CT.setTrimAileronControl(this.FM.CT.getTrimAileronControl() + 0.00625F);
                    break;
                case 48:
                    if (this.FM.CT.bHasAileronTrim && this.FM.CT.getTrimAileronControl() > -0.5F) this.FM.CT.setTrimAileronControl(this.FM.CT.getTrimAileronControl() - 0.00625F);
                    break;
                case 50:
                    if (this.FM.CT.bHasRudderTrim && this.FM.CT.getTrimRudderControl() < 0.5F) this.FM.CT.setTrimRudderControl(this.FM.CT.getTrimRudderControl() + 0.00625F);
                    break;
                case 51:
                    if (this.FM.CT.bHasRudderTrim && this.FM.CT.getTrimRudderControl() > -0.5F) this.FM.CT.setTrimRudderControl(this.FM.CT.getTrimRudderControl() - 0.00625F);
                    break;
                case 117:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjDistancePlus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeFighterAceMaker) {
                        ((TypeFighterAceMaker) aircraft).typeFighterAceMakerAdjDistancePlus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberAdjDiveAnglePlus();
                        this.toTrackSign(i);
                    }
                    break;
                case 118:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjDistanceMinus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeFighterAceMaker) {
                        ((TypeFighterAceMaker) aircraft).typeFighterAceMakerAdjDistanceMinus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberAdjDiveAngleMinus();
                        this.toTrackSign(i);
                    }
                    break;
                case 119:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjSideslipPlus();
                        this.toTrackSign(i);
                    }
                    if (aircraft instanceof TypeFighterAceMaker) {
                        ((TypeFighterAceMaker) aircraft).typeFighterAceMakerAdjSideslipPlus();
                        this.toTrackSign(i);
                    }
                    if (aircraft instanceof TypeX4Carrier) {
                        if (Config.isUSE_RENDER()&& (Main3D.cur3D().camera3D.pos.base() instanceof Bomb || Main3D.cur3D().camera3D.pos.base() instanceof Rocket || Main3D.cur3D().camera3D.pos.base() instanceof RocketBomb)) break;
                        ((TypeX4Carrier) aircraft).typeX4CAdjSidePlus();
                        this.toTrackSign(i);
                    }
                    break;
                case 120:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjSideslipMinus();
                        this.toTrackSign(i);
                    }
                    if (aircraft instanceof TypeFighterAceMaker) {
                        ((TypeFighterAceMaker) aircraft).typeFighterAceMakerAdjSideslipMinus();
                        this.toTrackSign(i);
                    }
                    if (aircraft instanceof TypeX4Carrier) {
                        if (Config.isUSE_RENDER()&& (Main3D.cur3D().camera3D.pos.base() instanceof Bomb || Main3D.cur3D().camera3D.pos.base() instanceof Rocket || Main3D.cur3D().camera3D.pos.base() instanceof RocketBomb)) break;
                        ((TypeX4Carrier) aircraft).typeX4CAdjSideMinus();
                        this.toTrackSign(i);
                    }
                    break;
                case 121:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjAltitudePlus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberAdjAltitudePlus();
                        this.toTrackSign(i);
                    }
                    if (aircraft instanceof TypeX4Carrier) {
                        if (Config.isUSE_RENDER()&& (Main3D.cur3D().camera3D.pos.base() instanceof Bomb || Main3D.cur3D().camera3D.pos.base() instanceof Rocket || Main3D.cur3D().camera3D.pos.base() instanceof RocketBomb)) break;
                        ((TypeX4Carrier) aircraft).typeX4CAdjAttitudePlus();
                        this.toTrackSign(i);
                    }
                    break;
                case 122:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjAltitudeMinus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberAdjAltitudeMinus();
                        this.toTrackSign(i);
                    }
                    if (aircraft instanceof TypeX4Carrier) {
                        if (Config.isUSE_RENDER()&& (Main3D.cur3D().camera3D.pos.base() instanceof Bomb || Main3D.cur3D().camera3D.pos.base() instanceof Rocket || Main3D.cur3D().camera3D.pos.base() instanceof RocketBomb)) break;
                        ((TypeX4Carrier) aircraft).typeX4CAdjAttitudeMinus();
                        this.toTrackSign(i);
                    }
                    break;
                case 123:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjSpeedPlus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberAdjVelocityPlus();
                        this.toTrackSign(i);
                    }
                    break;
                case 124:
                    if (aircraft instanceof TypeBomber) {
                        ((TypeBomber) aircraft).typeBomberAdjSpeedMinus();
                        this.toTrackSign(i);
                    } else if (aircraft instanceof TypeDiveBomber) {
                        ((TypeDiveBomber) aircraft).typeDiveBomberAdjVelocityMinus();
                        this.toTrackSign(i);
                    }
            }
        }
    }

    public void fromTrackSign(NetMsgInput netmsginput) throws IOException {
        if (Actor.isAlive(World.getPlayerAircraft()) && !World.isPlayerParatrooper() && !World.isPlayerDead()) {
            if (World.getPlayerAircraft() instanceof TypeBomber) {
                TypeBomber typebomber = (TypeBomber) World.getPlayerAircraft();
                int i = netmsginput.readUnsignedShort();
                switch (i) {
                    case 125:
                        // TODO: Added by |ZUTI|: assigning result to designated variable
                        World.getPlayerAircraft().FM.CT.zutiSetBombsightAutomationEngaged(typebomber.typeBomberToggleAutomation());
                        break;
                    case 117:
                        typebomber.typeBomberAdjDistancePlus();
                        break;
                    case 118:
                        typebomber.typeBomberAdjDistanceMinus();
                        break;
                    case 119:
                        typebomber.typeBomberAdjSideslipPlus();
                        break;
                    case 120:
                        typebomber.typeBomberAdjSideslipMinus();
                        break;
                    case 121:
                        typebomber.typeBomberAdjAltitudePlus();
                        break;
                    case 122:
                        typebomber.typeBomberAdjAltitudeMinus();
                        break;
                    case 123:
                        typebomber.typeBomberAdjSpeedPlus();
                        break;
                    case 124:
                        typebomber.typeBomberAdjSpeedMinus();
                        break;
                    default:
                        return;
                }
            }
            if (World.getPlayerAircraft() instanceof TypeDiveBomber) {
                TypeDiveBomber typedivebomber = (TypeDiveBomber) World.getPlayerAircraft();
                int i = netmsginput.readUnsignedShort();
                switch (i) {
                    case 125:
                        typedivebomber.typeDiveBomberToggleAutomation();
                        break;
                    case 121:
                        typedivebomber.typeDiveBomberAdjAltitudePlus();
                        break;
                    case 122:
                        typedivebomber.typeDiveBomberAdjAltitudeMinus();
                        break;
                    default:
                        return;
                }
            }
            if (World.getPlayerAircraft() instanceof TypeFighterAceMaker) {
                TypeFighterAceMaker typefighteracemaker = (TypeFighterAceMaker) World.getPlayerAircraft();
                int i = netmsginput.readUnsignedShort();
                switch (i) {
                    case 125:
                        typefighteracemaker.typeFighterAceMakerToggleAutomation();
                        break;
                    case 117:
                        typefighteracemaker.typeFighterAceMakerAdjDistancePlus();
                        break;
                    case 118:
                        typefighteracemaker.typeFighterAceMakerAdjDistanceMinus();
                        break;
                    case 119:
                        typefighteracemaker.typeFighterAceMakerAdjSideslipPlus();
                        break;
                    case 120:
                        typefighteracemaker.typeFighterAceMakerAdjSideslipMinus();
                        break;
                }
            }
        }
    }

    private void toTrackSign(int i) {
        if (Main3D.cur3D().gameTrackRecord() != null) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(5);
            netmsgguaranted.writeShort(i);
            Main3D.cur3D().gameTrackRecord().postTo(Main3D.cur3D().gameTrackRecord().channel(), netmsgguaranted);
        } catch (Exception exception) {
            /* empty */
        }
    }

    // TODO: Changed by |ZUTI|: changed from private to public
    public void doCmdPilotMove(int i, float f) {
        if (this.setPilot()) {
            switch (i) {
                case 1: {
                    float f_22_ = f * 0.55F + 0.55F;
                    if (Math.abs(f_22_ - this.lastPower) >= 0.01F) this.setPowerControl(f_22_);
                    break;
                }
                case 7: {
                    float f_23_ = f * 0.5F + 0.5F;
                    if (Math.abs(f_23_ - this.lastProp) >= 0.02F && this.FM.EI.isSelectionHasControlProp()) this.setPropControl(f_23_);
                    break;
                }
                case 2:
                    if (!this.FM.CT.bHasFlapsControl) break;
                    if (!this.FM.CT.bHasFlapsControlRed) {
                        this.FM.CT.FlapsControl = f * 0.5F + 0.5F;
                        break;
                    }
                    if (f < 0.0F) {
                        this.FM.CT.FlapsControl = 0.0F;
                        com.maddox.il2.game.HUD.log("FlapsRaised");
                    } else {
                        this.FM.CT.FlapsControl = 1.0F;
                        com.maddox.il2.game.HUD.log("FlapsLanding");
                    }
                    break;
                case 3:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.AileronControl = f;
                    break;
                case 4:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.ElevatorControl = f;
                    break;
                case 5:
                    if (!this.FM.CT.StabilizerControl) this.FM.CT.RudderControl = f;
                    break;
                case 6:
                    this.FM.CT.BrakeControl = f * 0.5F + 0.5F;
                    break;
                case 8:
                    if (this.FM.CT.bHasAileronTrim) this.FM.CT.setTrimAileronControl(f * 0.5F);
                    break;
                case 9:
                    if (this.FM.CT.bHasElevatorTrim) this.FM.CT.setTrimElevatorControl(f * 0.5F);
                    break;
                case 10:
                    if (this.FM.CT.bHasRudderTrim) this.FM.CT.setTrimRudderControl(f * 0.5F);
                    break;
                // TAK++
                case 11: // BrakeRight
                    this.FM.CT.BrakeRightControl = f * 0.5F + 0.5F;
                    this.FM.CT.BrakeControl = (this.FM.CT.BrakeRightControl + this.FM.CT.BrakeLeftControl) / 2.0F;
                    break;
                case 12: // BrakeLeft
                    this.FM.CT.BrakeLeftControl = f * 0.5F + 0.5F;
                    this.FM.CT.BrakeControl = (this.FM.CT.BrakeRightControl + this.FM.CT.BrakeLeftControl) / 2.0F;
                    break;
                // TAK--
                case 100: // 'd'
                    if (this.changeFovEnabled) {
                        f = (f * 0.5F + 0.5F) * 60F + 30F;
                        com.maddox.rts.CmdEnv.top().exec("fov " + f);
                    }
                    break;

                case 15: // '\017'
                    float f3 = f * 0.55F + 0.55F;
                    if (java.lang.Math.abs(f3 - this.lastPower1) >= 0.01F) {
                        this.lastPower1 = f3;
                        this.setPowerControl(f3, 1);
                    }
                    break;

                case 16: // '\020'
                    float f4 = f * 0.55F + 0.55F;
                    if (java.lang.Math.abs(f4 - this.lastPower2) >= 0.01F) {
                        this.lastPower2 = f4;
                        this.setPowerControl(f4, 2);
                    }
                    break;

                case 17: // '\021'
                    float f5 = f * 0.55F + 0.55F;
                    if (java.lang.Math.abs(f5 - this.lastPower3) >= 0.01F) {
                        this.lastPower3 = f5;
                        this.setPowerControl(f5, 3);
                    }
                    break;

                case 18: // '\022'
                    float f6 = f * 0.55F + 0.55F;
                    if (java.lang.Math.abs(f6 - this.lastPower4) >= 0.01F) {
                        this.lastPower4 = f6;
                        this.setPowerControl(f6, 4);
                    }
                    break;

                case 19: // '\023'
                    float f7 = f * 0.5F + 0.5F;
                    if (java.lang.Math.abs(f7 - this.lastRadiator) >= 0.02F) {
                        this.lastRadiator = f7;
                        this.setRadiatorControl(f7);
                    }
                    break;

                case 20: // '\024'
                    float f8 = f * 0.5F + 0.5F;
                    if (java.lang.Math.abs(f8 - this.lastProp1) >= 0.02F && 0 < this.FM.EI.getNum() && this.FM.EI.engines[0].isHasControlProp()) this.setPropControl(f8, 1);
                    break;

                case 21: // '\025'
                    float f9 = f * 0.5F + 0.5F;
                    if (java.lang.Math.abs(f9 - this.lastProp2) >= 0.02F && 1 < this.FM.EI.getNum() && this.FM.EI.engines[1].isHasControlProp()) this.setPropControl(f9, 2);
                    break;

                case 22: // '\026'
                    float f10 = f * 0.5F + 0.5F;
                    if (java.lang.Math.abs(f10 - this.lastProp3) >= 0.02F && 2 < this.FM.EI.getNum() && this.FM.EI.engines[2].isHasControlProp()) this.setPropControl(f10, 3);
                    break;

                case 23: // '\027'
                    float f11 = f * 0.5F + 0.5F;
                    if (java.lang.Math.abs(f11 - this.lastProp4) >= 0.02F && 3 < this.FM.EI.getNum() && this.FM.EI.engines[3].isHasControlProp()) this.setPropControl(f11, 4);
                    break;
                default:
                    return;
            }

            // TODO: Added by |ZUTI|: send data over to the new only if mover is not owner of RealFlightModel
            if (!(this.FM instanceof RealFlightModel)) ZutiSupportMethods_NetSend.aircraftControlsMoved_ToServer(i, f);
        }
    }

    public void createPilotHotMoves() {
        String string = "move";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("01", "power", 1, 1));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("02", "flaps", 2, 2));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("03", "aileron", 3, 3));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("04", "elevator", 4, 4));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("05", "rudder", 5, 5));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("06", "brakes", 6, 6));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("07", "pitch", 7, 7));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("08", "trimaileron", 8, 8));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("09", "trimelevator", 9, 9));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("10", "trimrudder", 10, 10));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-power", 1, 11));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-flaps", 2, 12));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-aileron", 3, 13));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-elevator", 4, 14));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-rudder", 5, 15));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-brakes", 6, 16));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-pitch", 7, 17));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-trimaileron", 8, 18));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-trimelevator", 9, 19));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-trimrudder", 10, 20));

        // TAK++
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("11", "BrakeRight", 11, 21));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("12", "BrakeLeft", 12, 22));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-BrakeRight", 11, 23));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-BrakeLeft", 12, 24));
        // TAK--

        // TODO: +++ Hotkey Fix by SAS~Storebror
//        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("11", "zoom", 100, 30, true));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("13", "zoom", 100, 30, true));
        // TODO: --- Hotkey Fix by SAS~Storebror
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-zoom", 100, 31, true));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("15", "power1", 15, 32));
        
        // TODO: +++ Hotkey Fix by SAS~Storebror
//        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-power1", 15, 23));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-power1", 15, 33));
        // TODO: --- Hotkey Fix by SAS~Storebror
        
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("16", "power2", 16, 34));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-power2", 16, 35));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("17", "power3", 17, 170));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-power3", 17, 171));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("18", "power4", 18, 172));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-power4", 18, 174));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("25", "radiator", 19, 36, true));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-radiator", 19, 37, true));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("26", "prop1", 20, 38));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-prop1", 20, 39));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("27", "prop2", 21, 40));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-prop2", 21, 41));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("28", "prop3", 22, 175));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-prop3", 22, 176));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove("29", "prop4", 23, 177));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFireMove(null, "-prop4", 23, 178));
    }

    private void createCommonHotKeys() {
        java.lang.String s = "misc";
        HotKeyCmdEnv.setCurrentEnv(s);
        com.maddox.rts.HotKeyEnv.fromIni(s, com.maddox.il2.engine.Config.cur.ini, "HotKey pilot");
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ18", "BEACON_PLUS", 139, 359));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ19", "BEACON_MINUS", 140, 360));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ40", "AUX1_PLUS", 149, 369));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ41", "AUX1_MINUS", 150, 370));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ60", "AUX_A", 157, 377));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ61", "AUX_B", 158, 378));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ62", "AUX_C", 159, 379));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ63", "AUX_D", 160, 380));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ64", "AUX_E", 161, 381));
        // TODO: New misc keys
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ65", "Misc_1", 300, 382));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ66", "Misc_2", 301, 383));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ67", "Misc_3", 302, 384));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ68", "Misc_4", 303, 385));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ69", "Misc_5", 304, 386));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ70", "Misc_6", 305, 387));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ71", "Misc_7", 306, 388));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ72", "Misc_8", 307, 389));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ73", "Misc_9", 308, 390));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("ZZZ74", "Misc_10", 309, 391));
    }

    public void createPilotHotKeys() {
        String string = "pilot";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic01", "ElevatorUp", 3, 103));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic02", "ElevatorDown", 4, 104));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic03", "AileronLeft", 5, 105));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic04", "AileronRight", 6, 106));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic05", "RudderLeft", 1, 101));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic06", "RudderRight", 2, 102));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic07", "Stabilizer", 71, 165));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic08", "AIRCRAFT_RUDDER_LEFT_1", 56, 156));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic09", "AIRCRAFT_RUDDER_CENTRE", 57, 157));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic10", "AIRCRAFT_RUDDER_RIGHT_1", 58, 158));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic11", "AIRCRAFT_TRIM_V_PLUS", 44, 144));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic12", "AIRCRAFT_TRIM_V_0", 43, 143));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic13", "AIRCRAFT_TRIM_V_MINUS", 45, 145));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic14", "AIRCRAFT_TRIM_H_MINUS", 48, 148));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic15", "AIRCRAFT_TRIM_H_0", 46, 146));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic16", "AIRCRAFT_TRIM_H_PLUS", 47, 147));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic17", "AIRCRAFT_TRIM_R_MINUS", 51, 151));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic18", "AIRCRAFT_TRIM_R_0", 49, 149));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("1basic19", "AIRCRAFT_TRIM_R_PLUS", 50, 150));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$1", "1basic20") {});
        hudLogPowerId = HUD.makeIdLog();
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine01", "AIRCRAFT_TOGGLE_ENGINE", 70, 164));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine02", "AIRCRAFT_POWER_PLUS_5", 59, 159));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine03", "AIRCRAFT_POWER_MINUS_5", 60, 160));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine04", "Boost", 61, 161));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine05", "Power0", 20, 120));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine06", "Power10", 21, 121));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine07", "Power20", 22, 122));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine08", "Power30", 23, 123));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine09", "Power40", 24, 124));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine10", "Power50", 25, 125));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine11", "Power60", 26, 126));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine12", "Power70", 27, 127));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine13", "Power80", 28, 128));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine14", "Power90", 29, 129));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine15", "Power100", 30, 130));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$2", "2engine16") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine17", "Step0", 31, 131));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine18", "Step10", 32, 132));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine19", "Step20", 33, 133));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine20", "Step30", 34, 134));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine21", "Step40", 35, 135));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine22", "Step50", 36, 136));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine23", "Step60", 37, 137));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine24", "Step70", 38, 138));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine25", "Step80", 39, 139));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine26", "Step90", 40, 140));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine27", "Step100", 41, 141));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine28", "StepAuto", 42, 142));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine29", "StepPlus5", 73, 290));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine30", "StepMinus5", 74, 291));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$3", "2engine31") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine32", "Mix0", 75, 292));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine33", "Mix10", 76, 293));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine34", "Mix20", 77, 294));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine35", "Mix30", 78, 295));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine36", "Mix40", 79, 296));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine37", "Mix50", 80, 297));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine38", "Mix60", 81, 298));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine39", "Mix70", 82, 299));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine40", "Mix80", 83, 300));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine41", "Mix90", 84, 301));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine42", "Mix100", 85, 302));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine43", "MixPlus20", 86, 303));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine44", "MixMinus20", 87, 304));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$4", "2engine45") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine46", "MagnetoPlus", 88, 305));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine47", "MagnetoMinus", 89, 306));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$5", "2engine48") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine49", "CompressorPlus", 115, 334));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine50", "CompressorMinus", 116, 335));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$6", "2engine51") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine52", "EngineSelectAll", 90, 307));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine53", "EngineSelectNone", 91, 318));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine54", "EngineSelectLeft", 92, 316));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine55", "EngineSelectRight", 93, 317));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine56", "EngineSelect1", 94, 308));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine57", "EngineSelect2", 95, 309));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine58", "EngineSelect3", 96, 310));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine59", "EngineSelect4", 97, 311));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine60", "EngineSelect5", 98, 312));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine61", "EngineSelect6", 99, 313));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine62", "EngineSelect7", 100, 314));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine63", "EngineSelect8", 101, 315));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine63a", "EngineSelect9", ENGINE_SELECT_9, 407));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine63b", "EngineSelect10", ENGINE_SELECT_10, 408));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine63c", "EngineSelect11", ENGINE_SELECT_11, 409));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine63d", "EngineSelect12", ENGINE_SELECT_12, 410));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine64", "EngineToggleAll", 102, 319));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine65", "EngineToggleLeft", 103, 328));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine66", "EngineToggleRight", 104, 329));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine67", "EngineToggle1", 105, 320));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine68", "EngineToggle2", 106, 321));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine69", "EngineToggle3", 107, 322));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine70", "EngineToggle4", 108, 323));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine71", "EngineToggle5", 109, 324));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine72", "EngineToggle6", 110, 325));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine73", "EngineToggle7", 111, 326));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine74", "EngineToggle8", 112, 327));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine74a", "EngineToggle9", ENGINE_TOGGLE_9, 411));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine74b", "EngineToggle10", ENGINE_TOGGLE_10, 412));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine74c", "EngineToggle11", ENGINE_TOGGLE_11, 413));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine74d", "EngineToggle12", ENGINE_TOGGLE_12, 414));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$7", "2engine75") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine76", "EngineExtinguisher", 113, 330));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("2engine77", "EngineFeather", 114, 333));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$8", "2engine78") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced01", "AIRCRAFT_FLAPS_NOTCH_UP", 52, 152));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced02", "AIRCRAFT_FLAPS_NOTCH_DOWN", 53, 153));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced03", "Gear", 9, 109));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced04", "AIRCRAFT_GEAR_UP_MANUAL", 54, 154));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced05", "AIRCRAFT_GEAR_DOWN_MANUAL", 55, 155));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced06", "Radiator", 7, 107));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced07", "AIRCRAFT_TOGGLE_AIRBRAKE", 63, 163));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced08", "Brake", 0, 100));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced09", "AIRCRAFT_TAILWHEELLOCK", 72, 166));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced10", "AIRCRAFT_DROP_TANKS", 62, 162));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$9", "3advanced11") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced12", "AIRCRAFT_DOCK_UNDOCK", 126, 346));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced13", "WINGFOLD", 127, 347));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced14", "AIRCRAFT_CARRIERHOOK", 129, 349));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced15", "AIRCRAFT_BRAKESHOE", 130, 350));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced16", "COCKPITDOOR", 128, 348));

        // TODO: Added by |ZUTI|: Bomb Bay Door mod keys
        // -----------------------------------------------------------------------------
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("3advanced17", "BombBayDoor", 131, 400));
        // -----------------------------------------------------------------------------

        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$10", "3advanced18") {});
        hudLogWeaponId = HUD.makeIdLog();
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic0", "Weapon0", 16, 116));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic1", "Weapon1", 17, 117));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic2", "Weapon2", 18, 118));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic3", "Weapon3", 19, 119));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic4", "Weapon01", 64, 173));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic5", "GunPods", 15, 115));

        // +++++ TODO skylla: enhanced weapon release control +++++
        ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/controls", RTSConf.cur.locale, LDRres.loader());
        String selectRocketName = "SelectMissile";
        String rocketSalvoSizeName = "RocketSalvoSize";
        String rocketReleaseDelayName = "RocketReleaseDelay";
        String selectBombName = "SelectBomb";
        String bombSalvoSizeName = "BombSalvoSize";
        String bombReleaseDelayName = "BombReleaseDelay";
        if (resourceBundle != null) {
            try {
                selectRocketName = resourceBundle.getString(selectRocketName);
            } catch (Exception e) {}
            try {
                rocketSalvoSizeName = resourceBundle.getString(rocketSalvoSizeName);
            } catch (Exception e) {}
            try {
                rocketReleaseDelayName = resourceBundle.getString(rocketReleaseDelayName);
            } catch (Exception e) {}
            try {
                selectBombName = resourceBundle.getString(selectRocketName);
            } catch (Exception e) {}
            try {
                bombSalvoSizeName = resourceBundle.getString(rocketSalvoSizeName);
            } catch (Exception e) {}
            try {
                bombReleaseDelayName = resourceBundle.getString(rocketReleaseDelayName);
            } catch (Exception e) {}
        }
        if (selectRocketName.equalsIgnoreCase("SelectMissile")) selectRocketName = "Select Missile/Rocket";
        if (rocketSalvoSizeName.equalsIgnoreCase("RocketSalvoSize")) rocketSalvoSizeName = "Select Rocket Salvo Size";
        if (rocketReleaseDelayName.equalsIgnoreCase("RocketReleaseDelay")) rocketReleaseDelayName = "Select Rocket Release Delay";
        if (selectBombName.equalsIgnoreCase("SelectBomb")) selectBombName = "Select Bomb";
        if (bombSalvoSizeName.equalsIgnoreCase("BombSalvoSize")) bombSalvoSizeName = "Select Bomb Salvo Size";
        if (bombReleaseDelayName.equalsIgnoreCase("BombReleaseDelay")) bombReleaseDelayName = "Select Bomb Release Delay";
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$$11", "4basic6") {});
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic61", selectRocketName, 132, 401));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic62", rocketSalvoSizeName, 133, 402));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic63", rocketReleaseDelayName, 134, 403));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic64", selectBombName, 135, 404));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic65", bombSalvoSizeName, 136, 405));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("4basic66", bombReleaseDelayName, 137, 406));
        HotKeyCmdEnv.addCmd(new HotKeyCmd(false, "$$+SIGHTCONTROLS", "4basic7") {});
        // ----- todo skylla: enhanced weapon release control -----

        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced01", "SIGHT_AUTO_ONOFF", 125, 344));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced02", "SIGHT_DIST_PLUS", 117, 336));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced03", "SIGHT_DIST_MINUS", 118, 337));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced04", "SIGHT_SIDE_RIGHT", 119, 338));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced05", "SIGHT_SIDE_LEFT", 120, 339));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced06", "SIGHT_ALT_PLUS", 121, 340));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced07", "SIGHT_ALT_MINUS", 122, 341));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced08", "SIGHT_SPD_PLUS", 123, 342));
        HotKeyCmdEnv.addCmd(new HotKeyCmdFire("5advanced09", "SIGHT_SPD_MINUS", 124, 343));
    }

    private CockpitGunner getActiveCockpitGuner() {
        if (!Actor.isAlive(World.getPlayerAircraft())) return null;
        if (World.isPlayerParatrooper()) return null;
        if (World.isPlayerDead()) return null;
        if (Main3D.cur3D().cockpits == null) return null;
        int i = World.getPlayerAircraft().FM.AS.astatePlayerIndex;
        for (int i_208_ = 0; i_208_ < Main3D.cur3D().cockpits.length; i_208_++)
            if (Main3D.cur3D().cockpits[i_208_] instanceof CockpitGunner) {
                CockpitGunner cockpitgunner = (CockpitGunner) Main3D.cur3D().cockpits[i_208_];
                if (i == cockpitgunner.astatePilotIndx() && cockpitgunner.isRealMode()) {
                    Turret turret = cockpitgunner.aiTurret();
                    if (!turret.bIsNetMirror) return cockpitgunner;
                }
            }
        return null;
    }

    public void createGunnerHotKeys() {
        String string = "gunner";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);

        HotKeyCmdEnv.addCmd(string, new HotKeyCmdMouseMove(true, "Mouse") {
            public void created() {
                this.setRecordId(51);
                this.sortingName = null;
            }

            public boolean isDisableIfTimePaused() {
                return true;
            }

            public void move(int i, int i_211_, int i_212_) {
                CockpitGunner cockpitgunner = AircraftHotKeys.this.getActiveCockpitGuner();
                if (cockpitgunner != null) cockpitgunner.hookGunner().mouseMove(i, i_211_, i_212_);
            }
        });
        HotKeyCmdEnv.addCmd(string, new HotKeyCmd(true, "Fire") {
            CockpitGunner coc = null;

            public void created() {
                this.setRecordId(52);
            }

            public boolean isDisableIfTimePaused() {
                return true;
            }

            private boolean isExistAmmo(CockpitGunner cockpitgunner) {
                com.maddox.il2.fm.FlightModel flightmodel = World.getPlayerFM();
                BulletEmitter[] bulletemitters = flightmodel.CT.Weapons[cockpitgunner.weaponControlNum()];
                if (bulletemitters == null) return false;
                for (int i = 0; i < bulletemitters.length; i++)
                    if (bulletemitters[i] != null && bulletemitters[i].haveBullets()) return true;
                return false;
            }

            public void begin() {
                this.coc = AircraftHotKeys.this.getActiveCockpitGuner();
                if (this.coc != null) if (this.isExistAmmo(this.coc)) this.coc.hookGunner().gunFire(true);
                else HUD.log(AircraftHotKeys.hudLogWeaponId, "OutOfAmmo");
            }

            public void end() {
                if (this.coc != null) {
                    if (Actor.isValid(this.coc)) this.coc.hookGunner().gunFire(false);
                    this.coc = null;
                }
            }
        });
    }

    public boolean isAutoAutopilot() {
        return this.bAutoAutopilot;
    }

    public void setAutoAutopilot(boolean bool) {
        this.bAutoAutopilot = bool;
    }

    public static boolean isCockpitRealMode(int i) {
        if (Main3D.cur3D().cockpits[i] instanceof CockpitPilot) // TODO: Added by |ZUTI|: try/catch block
            try {
                RealFlightModel realflightmodel = (RealFlightModel) World.getPlayerFM();
                return realflightmodel.isRealMode();
            } catch (ClassCastException ex) {
                return false;
            }
        if (Main3D.cur3D().cockpits[i] instanceof CockpitGunner) {
            CockpitGunner cockpitgunner = (CockpitGunner) Main3D.cur3D().cockpits[i];
            return cockpitgunner.isRealMode();
        }
        return false;
    }

    public static void setCockpitRealMode(int i, boolean bool) {
        if (Main3D.cur3D().cockpits[i] instanceof CockpitPilot) {
            if (!Mission.isNet()) {
                RealFlightModel realflightmodel = (RealFlightModel) World.getPlayerFM();
                if (realflightmodel.get_maneuver() != 44 && realflightmodel.isRealMode() != bool) {
                    if (realflightmodel.isRealMode()) Main3D.cur3D().aircraftHotKeys.bAfterburner = false;
                    realflightmodel.CT.resetControl(0);
                    realflightmodel.CT.resetControl(1);
                    realflightmodel.CT.resetControl(2);
                    realflightmodel.EI.setCurControlAll(true);
                    realflightmodel.setRealMode(bool);
                    HUD.log("PilotAI" + (realflightmodel.isRealMode() ? "OFF" : "ON"));
                }
            }
        } else if (Main3D.cur3D().cockpits[i] instanceof CockpitGunner) {
            CockpitGunner cockpitgunner = (CockpitGunner) Main3D.cur3D().cockpits[i];
            if (cockpitgunner.isRealMode() != bool) {
                cockpitgunner.setRealMode(bool);
                if (!NetMissionTrack.isPlaying()) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (World.isPlayerGunner()) aircraft.netCockpitAuto(World.getPlayerGunner(), i, !cockpitgunner.isRealMode());
                    else aircraft.netCockpitAuto(aircraft, i, !cockpitgunner.isRealMode());
                }
                com.maddox.il2.fm.FlightModel flightmodel = World.getPlayerFM();
// AircraftState aircraftstate = flightmodel.AS;
                String string = AircraftState.astateHUDPilotHits[flightmodel.AS.astatePilotFunctions[cockpitgunner.astatePilotIndx()]];
                HUD.log(string + (cockpitgunner.isRealMode() ? "AIOFF" : "AION"));
            }
        }
    }

    private boolean isMiscValid() {
        if (!Actor.isAlive(World.getPlayerAircraft())) return false;
        if (World.isPlayerParatrooper()) return false;
        if (World.isPlayerDead()) return false;
        return Mission.isPlaying();
    }

    public void createMiscHotKeys() {
        String string = "misc";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "autopilot", "00") {
            public void created() {
                this.setRecordId(270);
            }

            public void begin() {
                if (AircraftHotKeys.this.isMiscValid() && !Main3D.cur3D().isDemoPlaying() && !World.getPlayerFM().AS.isPilotDead(Main3D.cur3D().cockpitCur.astatePilotIndx())) {
                    int i = Main3D.cur3D().cockpitCurIndx();
                    if (isCockpitRealMode(i)) new MsgAction(true, new Integer(i)) {
                        public void doAction(Object object) {
                            int i_219_ = ((Integer) object).intValue();
                            HotKeyCmd.exec("misc", "cockpitRealOff" + i_219_);
                        }
                    };
                    else new MsgAction(true, new Integer(i)) {
                        public void doAction(Object object) {
                            int i_221_ = ((Integer) object).intValue();
                            HotKeyCmd.exec("misc", "cockpitRealOn" + i_221_);
                        }
                    };
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "autopilotAuto", "01") {
            public void begin() {
                if (AircraftHotKeys.this.isMiscValid() && !Main3D.cur3D().isDemoPlaying()) new MsgAction(true) {
                    public void doAction() {
                        HotKeyCmd.exec("misc", "autopilotAuto_");
                    }
                };
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "autopilotAuto_", null) {
            public void created() {
                this.setRecordId(271);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
                if (AircraftHotKeys.this.isMiscValid()) {
                    AircraftHotKeys.this.setAutoAutopilot(!AircraftHotKeys.this.isAutoAutopilot());
                    HUD.log("AutopilotAuto" + (AircraftHotKeys.this.isAutoAutopilot() ? "ON" : "OFF"));
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "target_", null) {
            public void created() {
                this.setRecordId(278);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
// Object object = null;
                Actor actor;
                if (Main3D.cur3D().isDemoPlaying()) actor = Selector._getTrackArg0();
                else actor = HookPilot.cur().getEnemy();
                Selector.setTarget(Selector.setCurRecordArg0(actor));
            }
        });
        for (int i = 0; i < 10; i++) {
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitRealOn" + i, null) {
                int indx;

                public void created() {
                    this.indx = Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0');
                    this.setRecordId(500 + this.indx);
                    HotKeyEnv.currentEnv().remove(this.sName);
                }

                public void begin() {
                    if (AircraftHotKeys.this.isMiscValid()) setCockpitRealMode(this.indx, true);
                }
            });
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitRealOff" + i, null) {
                int indx;

                public void created() {
                    this.indx = Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0');
                    this.setRecordId(510 + this.indx);
                    HotKeyEnv.currentEnv().remove(this.sName);
                }

                public void begin() {
                    if (AircraftHotKeys.this.isMiscValid()) setCockpitRealMode(this.indx, false);
                }
            });
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitEnter" + i, null) {
                int indx;

                public void created() {
                    this.indx = Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0');
                    this.setRecordId(520 + this.indx);
                    HotKeyEnv.currentEnv().remove(this.sName);
                }

                public void begin() {
                    if (AircraftHotKeys.this.isMiscValid()) if (Main3D.cur3D().cockpits != null && this.indx < Main3D.cur3D().cockpits.length) {
                        World.getPlayerAircraft().FM.AS.astatePlayerIndex = Main3D.cur3D().cockpits[this.indx].astatePilotIndx();
                        if (!NetMissionTrack.isPlaying()) {
                            Aircraft aircraft = World.getPlayerAircraft();
                            // TODO: Added by |ZUTI|: once player spawns as gunner isPlayeGunner function is useless.
                            // If/when reseting PlayerGunner variable in World class engine also destroys
                            // aircraft to which gunner is assigned. That is NOT desired so I have included gunner
                            // cockpit number because I reset that to -1 after player ejects/leaves it.
                            if (World.isPlayerGunner() && World.getPlayerGunner().zutiGetCockpitNum() > 0) aircraft.netCockpitEnter(World.getPlayerGunner(), this.indx);
                            else aircraft.netCockpitEnter(aircraft, this.indx);
                        }
                    }
                }
            });
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitLeave" + i, null) {
                int indx;

                public void created() {
                    this.indx = Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0');
                    this.setRecordId(530 + this.indx);
                    HotKeyEnv.currentEnv().remove(this.sName);
                }

                public void begin() {
                    if (AircraftHotKeys.this.isMiscValid()) if (Main3D.cur3D().cockpits != null && this.indx < Main3D.cur3D().cockpits.length && Main3D.cur3D().cockpits[this.indx] instanceof CockpitGunner && isCockpitRealMode(this.indx))
                        ((CockpitGunner) Main3D.cur3D().cockpits[this.indx]).hookGunner().gunFire(false);
                }
            });
        }
        // ZUTI: multicrew ejecting
        // -------------------------------------------------------------------------------------
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ejectPilot", "02") {
            public void created() {
                this.setRecordId(272);
            }

            public void begin() {
                if (AircraftHotKeys.this.isMiscValid() && !World.isPlayerGunner() && World.getPlayerFM() instanceof RealFlightModel) {
                    RealFlightModel realflightmodel = (RealFlightModel) World.getPlayerFM();
                    if (realflightmodel.isRealMode() && !realflightmodel.AS.bIsAboutToBailout && realflightmodel.AS.bIsEnableToBailout) {
                        AircraftState.bCheckPlayerAircraft = false;
                        ((Aircraft) realflightmodel.actor).hitDaSilk();
                        AircraftState.bCheckPlayerAircraft = true;
                        Voice.cur().SpeakBailOut[realflightmodel.actor.getArmy() - 1 & 0x1][((Aircraft) realflightmodel.actor).aircIndex()] = (int) (Time.current() / 60000L) + 1;
                        new MsgAction(true) {
                            public void doAction() {
                                if (!Main3D.cur3D().isDemoPlaying() || !HotKeyEnv.isEnabled("aircraftView")) HotKeyCmd.exec("aircraftView", "OutsideView");
                            }
                        };
                    }
                }
                // TODO: Added by |ZUTI|: enable bailout if player is gunner
                else if (World.isPlayerGunner() && !World.isPlayerDead() && !World.isPlayerParatrooper()) ZutiSupportMethods_NetSend.ejectPlayer((NetUser) NetEnv.host());
            }
        });
        // -------------------------------------------------------------------------------------
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitDim", "03") {
            public void created() {
                this.setRecordId(274);
            }

            public void begin() {
                if (!Main3D.cur3D().isViewOutside() && AircraftHotKeys.this.isMiscValid() && Actor.isValid(Main3D.cur3D().cockpitCur)) Main3D.cur3D().cockpitCur.doToggleDim();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitLight", "04") {
            public void created() {
                this.setRecordId(275);
            }

            public void begin() {
                if (!Main3D.cur3D().isViewOutside() && AircraftHotKeys.this.isMiscValid() && Actor.isValid(Main3D.cur3D().cockpitCur)) Main3D.cur3D().cockpitCur.doToggleLight();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "toggleNavLights", "05") {
            public void created() {
                this.setRecordId(331);
            }

            public void begin() {
                if (AircraftHotKeys.this.isMiscValid()) {
                    com.maddox.il2.fm.FlightModel flightmodel = World.getPlayerFM();
                    if (flightmodel != null) {
                        boolean bool = flightmodel.AS.bNavLightsOn;
                        flightmodel.AS.setNavLightsState(!flightmodel.AS.bNavLightsOn);
                        if (bool || flightmodel.AS.bNavLightsOn) HUD.log("NavigationLights" + (flightmodel.AS.bNavLightsOn ? "ON" : "OFF"));
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "toggleLandingLight", "06") {
            public void created() {
                this.setRecordId(345);
            }

            public void begin() {
                if (AircraftHotKeys.this.isMiscValid()) {
                    com.maddox.il2.fm.FlightModel flightmodel = World.getPlayerFM();
                    if (flightmodel != null) {
                        boolean bool = flightmodel.AS.bLandingLightOn;
                        flightmodel.AS.setLandingLightState(!flightmodel.AS.bLandingLightOn);
                        if (bool || flightmodel.AS.bLandingLightOn) {
                            HUD.log("LandingLight" + (flightmodel.AS.bLandingLightOn ? "ON" : "OFF"));
                            EventLog.onToggleLandingLight(flightmodel.actor, flightmodel.AS.bLandingLightOn);
                        }
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "toggleSmokes", "07") {
            public void created() {
                this.setRecordId(273);
            }

            public void begin() {
                if (AircraftHotKeys.this.isMiscValid()) {
                    com.maddox.il2.fm.FlightModel flightmodel = World.getPlayerFM();
                    if (flightmodel != null) {
                        // TODO: Not available?!
                        // flightmodel.AS.setAirShowSmokeType(iAirShowSmoke);
                        // flightmodel.AS.setAirShowSmokeEnhanced(bAirShowSmokeEnhanced);
                        flightmodel.AS.setAirShowState(!flightmodel.AS.bShowSmokesOn);
                        EventLog.onToggleSmoke(flightmodel.actor, flightmodel.AS.bShowSmokesOn);
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "pad", "08") {
            public void end() {
                int i = Main.state().id();
                boolean bool = i == 5 || i == 29 || i == 63 || i == 49 || i == 50 || i == 42 || i == 43;
                if (GUI.pad.isActive()) GUI.pad.leave(!bool);
                else if (bool && !Main3D.cur3D().guiManager.isMouseActive()) GUI.pad.enter();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "chat", "09") {
            public void end() {
                GUI.chatActivate();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "onlineRating", "10") {
            public void begin() {
                Main3D.cur3D().hud.startNetStat();
            }

            public void end() {
                Main3D.cur3D().hud.stopNetStat();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "onlineRatingPage", "11") {
            public void end() {
                Main3D.cur3D().hud.pageNetStat();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "showPositionHint", "12") {
            public void begin() {
                if (!AircraftHotKeys.this.bSpeedbarTAS) HUD.setDrawSpeed((HUD.drawSpeed() + 1) % 4);
                else HUD.setDrawSpeed((HUD.drawSpeed() + 1) % 7);
            }

            public void created() {
                this.setRecordId(277);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "iconTypes", "13") {
            public void end() {
                Main3D.cur3D().changeIconTypes();
            }

            public void created() {
                this.setRecordId(279);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "showMirror", "14") {
            public void end() {
                Main3D.cur3D().viewMirror = (Main3D.cur3D().viewMirror + 1) % 3;
            }

            public void created() {
                this.setRecordId(280);
            }
        });
    }

    public void create_MiscHotKeys() {
        String string = "$$$misc";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "quickSaveNetTrack", "01") {
            public void end() {
                GUIWindowManager guiwindowmanager = Main3D.cur3D().guiManager;
                if (!guiwindowmanager.isKeyboardActive()) if (NetMissionTrack.isQuickRecording()) NetMissionTrack.stopRecording();
                else NetMissionTrack.startQuickRecording();
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "radioMuteKey", "02") {
            public void begin() {
                AudioDevice.setPTT(true);
            }

            public void end() {
                AudioDevice.setPTT(false);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "radioChannelSwitch", "03") {
            public void end() {
                if (GUI.chatDlg != null && Main.cur().chat != null && GUI.chatDlg.mode() != 2 && !RadioChannel.tstLoop && AudioDevice.npFlags.get(0) && !NetMissionTrack.isPlaying()) {
                    NetUser netuser = (NetUser) NetEnv.host();
                    String string_293_ = null;
                    String string_294_ = null;
                    if (netuser.isRadioPrivate()) {
                        string_293_ = "radio NONE";
                        string_294_ = "radioNone";
                    } else if (netuser.isRadioArmy()) {
                        string_293_ = "radio NONE";
                        string_294_ = "radioNone";
                    } else if (netuser.isRadioCommon()) {
                        if (netuser.getArmy() != 0) {
                            string_293_ = "radio ARMY";
                            string_294_ = "radioArmy";
                        } else {
                            string_293_ = "radio NONE";
                            string_294_ = "radioNone";
                        }
                    } else if (netuser.isRadioNone()) {
                        string_293_ = "radio COMMON";
                        string_294_ = "radioCommon";
                    }
                    System.out.println(RTSConf.cur.console.getPrompt() + string_293_);
                    RTSConf.cur.console.getEnv().exec(string_293_);
                    RTSConf.cur.console.addHistoryCmd(string_293_);
                    RTSConf.cur.console.curHistoryCmd = -1;
                    if (!Time.isPaused()) HUD.log(string_294_);
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "soundMuteKey", "04") {
            public void end() {
                AudioDevice.toggleMute();
            }
        });
    }

    private void switchToAIGunner() {
        if (!Main3D.cur3D().isDemoPlaying() && Main3D.cur3D().cockpitCur instanceof CockpitGunner && Main3D.cur3D().isViewOutside() && this.isAutoAutopilot()) {
            CockpitGunner cockpitgunner = (CockpitGunner) Main3D.cur3D().cockpitCur;
            if (cockpitgunner.isRealMode()) new MsgAction(true, new Integer(Main3D.cur3D().cockpitCurIndx())) {
                public void doAction(Object object) {
                    int i = ((Integer) object).intValue();
                    HotKeyCmd.exec("misc", "cockpitRealOff" + i);
                }
            };
        }
    }

    private boolean isValidCockpit(int i) {
        if (!Actor.isValid(World.getPlayerAircraft())) return false;
        if (!Mission.isPlaying()) return false;
        if (World.isPlayerParatrooper()) return false;
        if (Main3D.cur3D().cockpits == null) return false;
        if (i >= Main3D.cur3D().cockpits.length) return false;
        if (World.getPlayerAircraft().isUnderWater()) return false;
        Cockpit cockpit = Main3D.cur3D().cockpits[i];
        if (!cockpit.isEnableFocusing()) return false;
        int i_141_ = cockpit.astatePilotIndx();
        if (World.getPlayerFM().AS.isPilotParatrooper(i_141_)) return false;
        if (World.getPlayerFM().AS.isPilotDead(i_141_)) return false;
        if (Mission.isNet()) {
            if (Mission.isCoop()) {
                if (World.isPlayerGunner()) {
                    if (cockpit instanceof CockpitPilot) return false;
                } else if (cockpit instanceof CockpitPilot) return true;
                if (Time.current() == 0L) return false;
                if (Main3D.cur3D().isDemoPlaying()) return true;
                if (Actor.isValid(World.getPlayerAircraft().netCockpitGetDriver(i)) && !World.isPlayerDead()) return false;
                return true;
            }
            if (Mission.isDogfight()) return true;
        }
        return true;
    }

    // TODO: Changed by |ZUTI|: from private to public
    public void switchToCockpit(int i) {
        if (Mission.isCoop() && Main3D.cur3D().cockpits[i] instanceof CockpitGunner && !Main3D.cur3D().isDemoPlaying() && !World.isPlayerDead()) {
            Actor actor = World.getPlayerAircraft();
            if (World.isPlayerGunner()) actor = World.getPlayerGunner();
            Actor actor_142_ = World.getPlayerAircraft().netCockpitGetDriver(i);
            if (actor != actor_142_) {
                if (!Actor.isValid(actor_142_)) {
                    this.switchToCockpitRequest = i;
                    World.getPlayerAircraft().netCockpitDriverRequest(actor, i);
                    return;
                }
                return;
            }
        }
        this.doSwitchToCockpit(i);

        // TODO: Added by |ZUTI|: NEEDS TO BE CALLED!!!!!
        String acName = World.getPlayerAircraft().name();
        int cockpitNum = Main3D.cur3D().cockpitCurIndx();
        int newNetPlace = ZutiSupportMethods_Multicrew.getNetPlaceFromAircraftCockpit(acName, cockpitNum);
        ((NetUser) NetEnv.host()).requestPlace(newNetPlace);
    }

    // ------------------------------------------------------------------------------

    public void netSwitchToCockpit(int i) {
        if (!Main3D.cur3D().isDemoPlaying()) if (i == this.switchToCockpitRequest) new MsgAction(true, new Integer(i)) {
            public void doAction(Object object) {
                int i_302_ = ((Integer) object).intValue();
                HotKeyCmd.exec("aircraftView", "cockpitSwitch" + i_302_);
            }
        };
    }

    private void doSwitchToCockpit(int i) {
        Selector.setCurRecordArg0(World.getPlayerAircraft());
        if (!World.isPlayerDead() && !World.isPlayerParatrooper() && !Main3D.cur3D().isDemoPlaying()) {
            boolean bool = true;
            if (Main3D.cur3D().cockpitCur instanceof CockpitPilot && Main3D.cur3D().cockpits[i] instanceof CockpitPilot) bool = false;
            if (bool && this.isAutoAutopilot()) new MsgAction(true, new Integer(Main3D.cur3D().cockpitCurIndx())) {
                public void doAction(Object object) {
                    int i_305_ = ((Integer) object).intValue();
                    HotKeyCmd.exec("misc", "cockpitRealOff" + i_305_);
                }
            };
            new MsgAction(true, new Integer(Main3D.cur3D().cockpitCurIndx())) {
                public void doAction(Object object) {
                    int i_308_ = ((Integer) object).intValue();
                    HotKeyCmd.exec("misc", "cockpitLeave" + i_308_);
                }
            };
            new MsgAction(true, new Integer(i)) {
                public void doAction(Object object) {
                    int i_311_ = ((Integer) object).intValue();
                    HotKeyCmd.exec("misc", "cockpitEnter" + i_311_);
                }
            };
            if (bool && this.isAutoAutopilot()) new MsgAction(true, new Integer(i)) {
                public void doAction(Object object) {
                    int i_314_ = ((Integer) object).intValue();
                    HotKeyCmd.exec("misc", "cockpitRealOn" + i_314_);
                }
            };
        }
        Main3D.cur3D().cockpitCur.focusLeave();
        Main3D.cur3D().cockpitCur = Main3D.cur3D().cockpits[i];
        Main3D.cur3D().cockpitCur.focusEnter();
    }

    private int nextValidCockpit() {
        int i = Main3D.cur3D().cockpitCurIndx();
        if (i < 0) return -1;
        int cockpits = Main3D.cur3D().cockpits.length;
        if (cockpits < 2) return -1;
        for (int x = 0; x < cockpits - 1; x++) {
            int cockpitNr = (i + x + 1) % cockpits;
            
            // TODO: Changed by SAS~Storebror
            // ++++++++ Attempting to fix "stuck behind killed gunner pit" bug! +++++++++++
            
//            // TODO: Added by |ZUTI|: added new check for dogfight mode!
//            // --------------------------------------------
//            if (Mission.isDogfight()) {
//                // System.out.print("Can control cockpit id: " + cockpitNr);
//                ZutiSupportMethods_Multicrew.requestNextFreeCockpit(World.getPlayerAircraft().name(), cockpitNr);
//                // Return -1 because we have send request about cockpit change to server!
//                // It determins if cockpit is free or not!
//                return -1;
//            }
//            // --------------------------------------------
//            else if (this.isValidCockpit(cockpitNr)) return cockpitNr;
            
            if (!this.isValidCockpit(cockpitNr)) continue;
            if (!Mission.isDogfight()) return cockpitNr;
            ZutiSupportMethods_Multicrew.requestNextFreeCockpit(World.getPlayerAircraft().name(), cockpitNr);
            // Return -1 because we have send request about cockpit change to server!
            // It determins if cockpit is free or not!
            return -1;
            
            // ----------------------------------------------------------------------------
        }
        return -1;
    }

    public void setEnableChangeFov(boolean bool) {
        for (int i = 0; i < this.cmdFov.length; i++)
            this.cmdFov[i].enable(bool);

        this.changeFovEnabled = bool;
    }

    public void createViewHotKeys() {
        String string = "aircraftView";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "changeCockpit", "0") {
            public void begin() {
                int i = AircraftHotKeys.this.nextValidCockpit();
                if (i >= 0) new MsgAction(true, new Integer(i)) {
                    public void doAction(Object object) {
                        int i_322_ = ((Integer) object).intValue();
                        HotKeyCmd.exec("aircraftView", "cockpitSwitch" + i_322_);
                    }
                };
            }
        });
        for (int i = 0; i < 10; i++) {
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitView" + i, "0" + i) {
                int indx;

                public void created() {
                    this.indx = Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0');
                }

                public void begin() {
                    // TODO: Altered by |ZUTI|: disabled because this option bypasses security locks to prevent two
                    // users sitting in the same cockpit on a multi-crew plane! But only for dogfight missions.
                    if (Main.cur().mission != null && !Mission.isDogfight()) {
                        if (AircraftHotKeys.this.isValidCockpit(this.indx)) new MsgAction(true, new Integer(this.indx)) {
                            public void doAction(Object object) {
                                int i_327_ = ((Integer) object).intValue();
                                HotKeyCmd.exec("aircraftView", "cockpitSwitch" + i_327_);
                            }
                        };
                    } else // System.out.println("Can jump to cockpit num: " + indx);
                        ZutiSupportMethods_Multicrew.requestNextFreeCockpit(World.getPlayerAircraft().name(), this.indx);
                }
            });
            HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitSwitch" + i, null) {
                int indx;

                public void created() {
                    this.indx = Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0');
                    this.setRecordId(230 + this.indx);

                    HotKeyEnv.currentEnv().remove(this.sName);
                }

                public void begin() {
                    if (Main3D.cur3D().cockpitCurIndx() != this.indx || Main3D.cur3D().isViewOutside()) AircraftHotKeys.this.switchToCockpit(this.indx);
                }
            });
        }
        HotKeyCmdEnv.addCmd(this.cmdFov[0] = new HotKeyCmd(true, "fov90", "11") {
            public void begin() {
                CmdEnv.top().exec("fov 90");
            }

            public void created() {
                this.setRecordId(216);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[1] = new HotKeyCmd(true, "fov85", "12") {
            public void begin() {
                CmdEnv.top().exec("fov 85");
            }

            public void created() {
                this.setRecordId(244);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[2] = new HotKeyCmd(true, "fov80", "13") {
            public void begin() {
                CmdEnv.top().exec("fov 80");
            }

            public void created() {
                this.setRecordId(243);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[3] = new HotKeyCmd(true, "fov75", "14") {
            public void begin() {
                CmdEnv.top().exec("fov 75");
            }

            public void created() {
                this.setRecordId(242);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[4] = new HotKeyCmd(true, "fov70", "15") {
            public void begin() {
                CmdEnv.top().exec("fov 70");
            }

            public void created() {
                this.setRecordId(215);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[5] = new HotKeyCmd(true, "fov65", "16") {
            public void begin() {
                CmdEnv.top().exec("fov 65");
            }

            public void created() {
                this.setRecordId(241);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[6] = new HotKeyCmd(true, "fov60", "17") {
            public void begin() {
                CmdEnv.top().exec("fov 60");
            }

            public void created() {
                this.setRecordId(240);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[7] = new HotKeyCmd(true, "fov55", "18") {
            public void begin() {
                CmdEnv.top().exec("fov 55");
            }

            public void created() {
                this.setRecordId(229);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[8] = new HotKeyCmd(true, "fov50", "19") {
            public void begin() {
                CmdEnv.top().exec("fov 50");
            }

            public void created() {
                this.setRecordId(228);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[9] = new HotKeyCmd(true, "fov45", "20") {
            public void begin() {
                CmdEnv.top().exec("fov 45");
            }

            public void created() {
                this.setRecordId(227);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[10] = new HotKeyCmd(true, "fov40", "21") {
            public void begin() {
                CmdEnv.top().exec("fov 40");
            }

            public void created() {
                this.setRecordId(226);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[11] = new HotKeyCmd(true, "fov35", "22") {
            public void begin() {
                CmdEnv.top().exec("fov 35");
            }

            public void created() {
                this.setRecordId(225);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[12] = new HotKeyCmd(true, "fov30", "23") {
            public void begin() {
                CmdEnv.top().exec("fov 30");
            }

            public void created() {
                this.setRecordId(214);
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[13] = new HotKeyCmd(true, "fovSwitch", "24") {
            public void begin() {
                float f = (Main3D.FOVX - 30.0F) * (Main3D.FOVX - 30.0F);
                float f_373_ = (Main3D.FOVX - 70.0F) * (Main3D.FOVX - 70.0F);
                float f_374_ = (Main3D.FOVX - 90.0F) * (Main3D.FOVX - 90.0F);
// boolean bool = false;
                int i;
                if (f <= f_373_) i = 70;
                else if (f_373_ <= f_374_) i = 90;
                else i = 30;
                new MsgAction(true, new Integer(i)) {
                    public void doAction(Object object) {
                        int i_377_ = ((Integer) object).intValue();
                        HotKeyCmd.exec("aircraftView", "fov" + i_377_);
                    }
                };
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[14] = new HotKeyCmd(true, "fovInc", "25") {
            public void begin() {
                int i = (int) (Main3D.FOVX + 2.5) / 5 * 5;
                if (i < 30) i = 30;
                if (i > 85) i = 85;
                i += 5;
                new MsgAction(true, new Integer(i)) {
                    public void doAction(Object object) {
                        int i_382_ = ((Integer) object).intValue();
                        HotKeyCmd.exec("aircraftView", "fov" + i_382_);
                    }
                };
            }
        });
        HotKeyCmdEnv.addCmd(this.cmdFov[15] = new HotKeyCmd(true, "fovDec", "26") {
            public void begin() {
                int i = (int) (Main3D.FOVX + 2.5) / 5 * 5;
                if (i < 35) i = 35;
                if (i > 90) i = 90;
                i -= 5;
                new MsgAction(true, new Integer(i)) {
                    public void doAction(Object object) {
                        int i_387_ = ((Integer) object).intValue();
                        HotKeyCmd.exec("aircraftView", "fov" + i_387_);
                    }
                };
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "CockpitView", "27") {
            public void begin() {
                if (Actor.isValid(World.getPlayerAircraft()) && !World.isPlayerParatrooper() && !World.getPlayerAircraft().isUnderWater()) {
                    Main3D.cur3D().setViewInside();
                    Selector.setCurRecordArg0(World.getPlayerAircraft());
                    if (!Main3D.cur3D().isDemoPlaying() && World.getPlayerAircraft().netCockpitGetDriver(Main3D.cur3D().cockpitCurIndx()) == null) new MsgAction(true, new Integer(Main3D.cur3D().cockpitCurIndx())) {
                        public void doAction(Object object) {
                            int i = ((Integer) object).intValue();
                            HotKeyCmd.exec("misc", "cockpitEnter" + i);
                        }
                    };
                    if (!Main3D.cur3D().isDemoPlaying() && !Main3D.cur3D().isViewOutside() && AircraftHotKeys.this.isAutoAutopilot() && !isCockpitRealMode(Main3D.cur3D().cockpitCurIndx()))
                        new MsgAction(true, new Integer(Main3D.cur3D().cockpitCurIndx())) {
                            public void doAction(Object object) {
                                int i = ((Integer) object).intValue();
                                HotKeyCmd.exec("misc", "cockpitRealOn" + i);
                            }
                        };
                }
            }

            public void created() {
                this.setRecordId(212);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "CockpitShow", "28") {
            public void created() {
                this.setRecordId(213);
            }

            public void begin() {
                if (!World.cur().diffCur.Cockpit_Always_On && !Main3D.cur3D().isViewOutside() && Main3D.cur3D().cockpitCur instanceof CockpitPilot) if (Main3D.cur3D().isViewInsideShow()) {
                    Main3D.cur3D().hud.bDrawDashBoard = true;
                    Main3D.cur3D().setViewInsideShow(false);
                    Main3D.cur3D().cockpitCur.setEnableRenderingBall(true);
                } else if (Main3D.cur3D().hud.bDrawDashBoard && Main3D.cur3D().cockpitCur.isEnableRenderingBall()) Main3D.cur3D().cockpitCur.setEnableRenderingBall(false);
                else if (Main3D.cur3D().hud.bDrawDashBoard && !Main3D.cur3D().cockpitCur.isEnableRenderingBall()) {
                    Main3D.cur3D().hud.bDrawDashBoard = false;
                    Main3D.cur3D().cockpitCur.setEnableRenderingBall(true);
                } else if (Main3D.cur3D().isEnableRenderingCockpit() && Main3D.cur3D().cockpitCur.isEnableRenderingBall()) Main3D.cur3D().cockpitCur.setEnableRenderingBall(false);
                else if (Main3D.cur3D().isEnableRenderingCockpit() && !Main3D.cur3D().cockpitCur.isEnableRenderingBall()) Main3D.cur3D().setEnableRenderingCockpit(false);
                else {
                    Main3D.cur3D().setEnableRenderingCockpit(true);
                    Main3D.cur3D().setViewInsideShow(true);
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "OutsideView", "29") {
            public void created() {
                this.setRecordId(205);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (AircraftHotKeys.this.viewAllowed(AircraftHotKeys.this.bExtViewSelf)) {
                    Actor actor = World.getPlayerAircraft();
                    Selector.setCurRecordArg0(actor);
                    if (!Actor.isValid(actor)) actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFlow10(actor, false);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "NextView", "30") {
            public void created() {
                this.setRecordId(206);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (AircraftHotKeys.this.viewAllowed(AircraftHotKeys.this.bExtViewFriendly)) {
                    Actor actor = AircraftHotKeys.this.nextViewActor(false);
                    if (Actor.isValid(actor)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFlow10(actor, false);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "NextViewEnemy", "31") {
            public void created() {
                this.setRecordId(207);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (AircraftHotKeys.this.viewAllowed(AircraftHotKeys.this.bExtViewEnemy)) {
                    Actor actor = AircraftHotKeys.this.nextViewActor(true);
                    if (Actor.isValid(actor)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFlow10(actor, false);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "OutsideViewFly", "32") {
            public void created() {
                this.setRecordId(200);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (AircraftHotKeys.this.viewAllowed(AircraftHotKeys.this.bExtViewSelf)) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof ActorViewPoint) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFly(actor);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockView", "33") {
            public void created() {
                this.setRecordId(217);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockEnemy) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) if (Main3D.cur3D().isViewPadlock()) {
                        Main3D.cur3D().setViewEndPadlock();
                        Selector.setCurRecordArg1(aircraft);
                    } else {
                        if (AircraftHotKeys.bFirstHotCmd && Actor.isValid(World.getPlayerAircraft()) && !World.isPlayerParatrooper()) Main3D.cur3D().setViewInside();
                        Main3D.cur3D().setViewPadlock(false, false);
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockViewFriend", "34") {
            public void created() {
                this.setRecordId(218);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockFriendly) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) if (Main3D.cur3D().isViewPadlock()) {
                        Main3D.cur3D().setViewEndPadlock();
                        Selector.setCurRecordArg1(aircraft);
                    } else {
                        if (AircraftHotKeys.bFirstHotCmd && Actor.isValid(World.getPlayerAircraft()) && !World.isPlayerParatrooper()) Main3D.cur3D().setViewInside();
                        Main3D.cur3D().setViewPadlock(true, false);
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockViewGround", "35") {
            public void created() {
                this.setRecordId(221);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockEnemy) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) if (Main3D.cur3D().isViewPadlock()) {
                        Main3D.cur3D().setViewEndPadlock();
                        Selector.setCurRecordArg1(aircraft);
                    } else {
                        if (AircraftHotKeys.bFirstHotCmd && Actor.isValid(World.getPlayerAircraft()) && !World.isPlayerParatrooper()) Main3D.cur3D().setViewInside();
                        Main3D.cur3D().setViewPadlock(false, true);
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockViewFriendGround", "36") {
            public void created() {
                this.setRecordId(222);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockFriendly) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) if (Main3D.cur3D().isViewPadlock()) {
                        Main3D.cur3D().setViewEndPadlock();
                        Selector.setCurRecordArg1(aircraft);
                    } else {
                        if (AircraftHotKeys.bFirstHotCmd && Actor.isValid(World.getPlayerAircraft()) && !World.isPlayerParatrooper()) Main3D.cur3D().setViewInside();
                        Main3D.cur3D().setViewPadlock(true, true);
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockViewNext", "37") {
            public void created() {
                this.setRecordId(223);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockFriendly || AircraftHotKeys.this.bExtPadlockFriendly) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) if (AircraftHotKeys.bFirstHotCmd) {
                        Main3D.cur3D().setViewInside();
                        if (Actor.isValid(Main3D.cur3D().cockpitCur) && Main3D.cur3D().cockpitCur.existPadlock()) Main3D.cur3D().cockpitCur.startPadlock(Selector._getTrackArg1());
                    } else if (Main3D.cur3D().isViewPadlock()) Main3D.cur3D().setViewNextPadlock(true);
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockViewPrev", "38") {
            public void created() {
                this.setRecordId(224);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockFriendly || AircraftHotKeys.this.bExtPadlockFriendly) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) if (AircraftHotKeys.bFirstHotCmd) {
                        Main3D.cur3D().setViewInside();
                        if (Actor.isValid(Main3D.cur3D().cockpitCur) && Main3D.cur3D().cockpitCur.existPadlock()) Main3D.cur3D().cockpitCur.startPadlock(Selector._getTrackArg1());
                    } else if (Main3D.cur3D().isViewPadlock()) Main3D.cur3D().setViewNextPadlock(false);
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "PadlockViewForward", "39") {
            public void created() {
                this.setRecordId(220);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Padlock || AircraftHotKeys.this.bPadlockFriendly || AircraftHotKeys.this.bExtPadlockFriendly) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) Main3D.cur3D().setViewPadlockForward(true);
                }
            }

            public void end() {
                if (!World.cur().diffCur.No_Padlock) {
                    Aircraft aircraft = World.getPlayerAircraft();
                    if (Actor.isValid(aircraft) && !World.isPlayerDead() && !World.isPlayerParatrooper()) Main3D.cur3D().setViewPadlockForward(false);
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ViewEnemyAir", "40") {
            public void created() {
                this.setRecordId(203);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Outside_Views || AircraftHotKeys.this.bExtPadlockEnemy) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewEnemy(actor, false, false);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ViewFriendAir", "41") {
            public void created() {
                this.setRecordId(198);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Outside_Views || AircraftHotKeys.this.bExtPadlockFriendly) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFriend(actor, false, false);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ViewEnemyDirectAir", "42") {
            public void created() {
                this.setRecordId(201);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Outside_Views || AircraftHotKeys.this.bExtPadlockEnemy) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewEnemy(actor, true, false);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ViewEnemyGround", "43") {
            public void created() {
                this.setRecordId(204);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Outside_Views || AircraftHotKeys.this.bExtPadlockEnemy) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewEnemy(actor, false, true);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ViewFriendGround", "44") {
            public void created() {
                this.setRecordId(199);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Outside_Views || AircraftHotKeys.this.bExtPadlockFriendly) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFriend(actor, false, true);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "ViewEnemyDirectGround", "45") {
            public void created() {
                this.setRecordId(202);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (!World.cur().diffCur.No_Outside_Views || AircraftHotKeys.this.bExtPadlockEnemy) {
                    Actor actor = AircraftHotKeys.this.getViewActor();
                    if (Actor.isValid(actor) && !(actor instanceof BigshipGeneric)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewEnemy(actor, true, true);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "OutsideViewFollow", "46") {
            public void created() {
                this.setRecordId(208);
            }

            public void begin() {
                if (World.cur().diffCur.No_Outside_Views) return;
                Actor viewActor = World.getPlayerAircraft();
                Selector.setCurRecordArg0(viewActor);
                if (!Actor.isValid(viewActor)) viewActor = AircraftHotKeys.this.getViewActor();
                if (Actor.isValid(viewActor)) {
                    boolean flag = !Main3D.cur3D().isViewOutside();
                    Main3D.cur3D().setViewFlow10(viewActor, true);
                    if (flag) AircraftHotKeys.this.switchToAIGunner();
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "NextViewFollow", "47") {
            public void created() {
                this.setRecordId(209);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (AircraftHotKeys.this.viewAllowed(AircraftHotKeys.this.bExtViewFriendly)) {
                    Actor actor = AircraftHotKeys.this.nextViewActor(false);
                    if (Actor.isValid(actor)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFlow10(actor, true);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "NextViewEnemyFollow", "48") {
            public void created() {
                this.setRecordId(210);
            }

            public void begin() {
                if (!AircraftHotKeys.this.bMissionModsSet) AircraftHotKeys.this.setMissionMods();
                if (AircraftHotKeys.this.viewAllowed(AircraftHotKeys.this.bExtViewEnemy)) {
                    Actor actor = AircraftHotKeys.this.nextViewActor(true);
                    if (Actor.isValid(actor)) {
                        boolean bool = !Main3D.cur3D().isViewOutside();
                        Main3D.cur3D().setViewFlow10(actor, true);
                        if (bool) AircraftHotKeys.this.switchToAIGunner();
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitAim", "49") {
            public void created() {
                this.setRecordId(276);
            }

            public void begin() {
                if (!Main3D.cur3D().isViewOutside() && AircraftHotKeys.this.isMiscValid() && Actor.isValid(Main3D.cur3D().cockpitCur) && !Main3D.cur3D().cockpitCur.isToggleUp())
                    Main3D.cur3D().cockpitCur.doToggleAim(!Main3D.cur3D().cockpitCur.isToggleAim());
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitUp", "50") {
            public void created() {
                this.setRecordId(281);
            }

            public void begin() {
                if (!Main3D.cur3D().isViewOutside() && AircraftHotKeys.this.isMiscValid() && Actor.isValid(Main3D.cur3D().cockpitCur) && !Main3D.cur3D().cockpitCur.isToggleAim() && World.getPlayerFM().CT.bHasCockpitDoorControl
                        && (Main3D.cur3D().cockpitCur.isToggleUp() || !(World.getPlayerFM().CT.cockpitDoorControl < 0.5F) && !(World.getPlayerFM().CT.getCockpitDoor() < 0.99F)))
                    Main3D.cur3D().cockpitCur.doToggleUp(!Main3D.cur3D().cockpitCur.isToggleUp());
            }
        });

        // TODO: +++ Stationary Camera and Ordnance View Backport from 4.13.4 by SAS~Storebror +++
        ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/controls", RTSConf.cur.locale, LDRres.loader());
        String stationaryCameraViewName = "StationaryCameraView";
        String ordinanceViewName = "OrdinanceView";
        if (resourceBundle != null) {
            try {
                stationaryCameraViewName = resourceBundle.getString("StationaryCameraView");
            } catch (Exception e) {}
            try {
                ordinanceViewName = resourceBundle.getString("OrdinanceView");
            } catch (Exception e) {}
        }
        if (stationaryCameraViewName.equalsIgnoreCase("StationaryCameraView")) stationaryCameraViewName = "Stationary Camera View";

        if (ordinanceViewName.equalsIgnoreCase("OrdinanceView")) ordinanceViewName = "Ordnance Camera View";

        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, stationaryCameraViewName, "51") {

            public void created() {
                this.setRecordId(245);
            }

            public void begin() {
                if (World.cur().diffCur.No_Outside_Views) return;
                Actor actor = AircraftHotKeys.this.nextStationaryCameraActor();
                if (Actor.isValid(actor)) {
                    boolean flag = !Main3D.cur3D().isViewOutside();
                    Main3D.cur3D().setViewFlow10(actor, false);
                    if (flag) AircraftHotKeys.this.switchToAIGunner();
                }
            }

        });

        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, ordinanceViewName, "52") {

            public void created() {
                this.setRecordId(246);
            }

            public void begin() {
                if (World.cur().diffCur.No_Outside_Views) return;
                // Actor actor = myLatestReleasedOrdinance();
                Actor actor = AircraftHotKeys.this.getOrdnanceToFollow();
//                System.out.println("OrdinanceView actor=" + (actor==null?"null":actor.getClass().getName()));
                if (Actor.isValid(actor)) {
                    boolean flag = !Main3D.cur3D().isViewOutside();
                    Main3D.cur3D().setViewFlow10(actor, false);
                    if (flag) AircraftHotKeys.this.switchToAIGunner();
                }
            }

        });
        // TODO: --- Stationary Camera and Ordnance View Backport from 4.13.4 by SAS~Storebror ---
        
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "GameMenu", "53") {

            public void end()
            {
                if(!GUI.pad.isActive())
                    GUIMenu.doToggleMenu();
            }

            public void created()
            {
            }

        }
);
    }

    // TODO: +++ Stationary Camera and Ordnance View Backport from 4.13.4 by SAS~Storebror +++
    private Actor getOrdnanceToFollow() {
        Actor viewActor = Main3D.cur3D().viewActor();
        if (!Actor.isValid(viewActor)) viewActor = Actor.isValid(lastViewAircraft) ? lastViewAircraft : World.getPlayerAircraft();
        else if (!(viewActor instanceof Aircraft)) if (Actor.isValid(lastViewAircraft) && viewActor.getOwner() instanceof Aircraft && viewActor.getOwner() == lastViewAircraft) viewActor = lastViewAircraft;
        else viewActor = World.getPlayerAircraft();
//        System.out.println("viewActor=" + (viewActor==null?"null":viewActor.getClass().getName()));
        if (!Actor.isValid(viewActor)) return null;
        Aircraft viewAircraft = (Aircraft) viewActor;
        if (lastViewAircraft != viewAircraft) viewAircraft.setLastSelectedOrdnance(null);
        lastViewAircraft = viewAircraft;
        return viewAircraft.getNextOrdnance();
    }

//    private Actor myLatestReleasedOrdinance()
//    {
//        if(Selector.isEnableTrackArgs()) {
//            return Selector.setCurRecordArg0(Selector.getTrackArg0());
//        }
//        Aircraft aircraft = World.getPlayerAircraft();
//        for(int i = Engine.ordinances().size() - 1; i >= 0; i--)
//        {
//            Actor actor = (Actor)Engine.ordinances().get(i);
//            if(actor != null && actor.getOwner() == aircraft)
//                return Selector.setCurRecordArg0(actor);
//        }
//
//        return Selector.setCurRecordArg0(null);
//    }

    private static Actor   lastViewAircraft = null;
    private static TreeMap namedAircraft    = new TreeMap();

    private Actor nextStationaryCameraActor() {
        if (Selector.isEnableTrackArgs()) return Selector.setCurRecordArg0(Selector.getTrackArg0());
//        int i = World.getPlayerArmy();
        namedAircraft.clear();
        Actor actor = Main3D.cur3D().viewActor();
        if (this.isViewed(actor)) namedAircraft.put(actor.name(), null);
        for (java.util.Map.Entry entry = Engine.name2Actor().nextEntry(null); entry != null; entry = Engine.name2Actor().nextEntry(entry)) {
            Actor actor1 = (Actor) entry.getValue();
            if (actor1 instanceof ActorViewPoint) // ActorViewPoint actorviewpoint = (ActorViewPoint)actor1;
//                int k = World.getPlayerAircraft().getArmy();
                namedAircraft.put(actor1.name(), null);
//                if(actorviewpoint.getArmy() == 0)
//                    namedAircraft.put(actor1.name(), null);
//                else
//                if(actorviewpoint.getArmy() == k && !World.cur().diffCur.NoFriendlyViews)
//                    namedAircraft.put(actor1.name(), null);
//                else
//                if(actorviewpoint.getArmy() != k && !World.cur().diffCur.NoEnemyViews)
//                    namedAircraft.put(actor1.name(), null);
        }

        if (namedAircraft.size() == 0) return Selector.setCurRecordArg0(null);
        if (!this.isViewed(actor)) return Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAircraft.firstKey()));
        if (namedAircraft.size() == 1 && this.isViewed(actor)) return Selector.setCurRecordArg0(null);
        namedAll = namedAircraft.keySet().toArray(namedAll);
        int j = 0;
        String s = actor.name();
        for (; namedAll[j] != null; j++)
            if (s.equals(namedAll[j])) break;

        if (namedAll[j] == null) return Selector.setCurRecordArg0(null);
        j++;
        if (namedAll.length == j || namedAll[j] == null) j = 0;
        return Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAll[j]));
    }
    // TODO: --- Stationary Camera and Ordnance View Backport from 4.13.4 by SAS~Storebror ---

    public void createTimeHotKeys() {
        String string = "timeCompression";
        HotKeyCmdEnv.setCurrentEnv(string);
        HotKeyEnv.fromIni(string, Config.cur.ini, "HotKey " + string);
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "timeSpeedUp", "0") {
            public void begin() {
                if (!TimeSkip.isDo()) if (Time.isEnableChangeSpeed()) {
                    float f = Time.nextSpeed() * 2.0F;
                    if (f <= 8.0F) {
                        Time.setSpeed(f);
                        AircraftHotKeys.this.showTimeSpeed(f);
                    }
                }
            }

            public void created() {
                this.setRecordId(25);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "timeSpeedNormal", "1") {
            public void begin() {
                if (!TimeSkip.isDo()) if (Time.isEnableChangeSpeed()) {
                    Time.setSpeed(1.0F);
                    AircraftHotKeys.this.showTimeSpeed(1.0F);
                }
            }

            public void created() {
                this.setRecordId(24);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "timeSpeedDown", "2") {
            public void begin() {
                if (!TimeSkip.isDo()) if (Time.isEnableChangeSpeed()) {
                    float f = Time.nextSpeed() / 2.0F;
                    if (f >= 0.25F) {
                        Time.setSpeed(f);
                        AircraftHotKeys.this.showTimeSpeed(f);
                    }
                }
            }

            public void created() {
                this.setRecordId(26);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "timeSpeedPause", "3") {
            public void begin() {
                /* empty */
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "timeSkip", "4") {
            public void begin() {
                if (TimeSkip.isDo()) Main3D.cur3D().timeSkip.stop();
                else Main3D.cur3D().timeSkip.start();
            }
        });
    }

    private void showTimeSpeed(float f) {
        int i = Math.round(f * 4.0F);
        switch (i) {
            case 4:
                Main3D.cur3D().hud._log(0, "TimeSpeedNormal");
                break;
            case 8:
                Main3D.cur3D().hud._log(0, "TimeSpeedUp2");
                break;
            case 16:
                Main3D.cur3D().hud._log(0, "TimeSpeedUp4");
                break;
            case 32:
                Main3D.cur3D().hud._log(0, "TimeSpeedUp8");
                break;
            case 2:
                Main3D.cur3D().hud._log(0, "TimeSpeedDown2");
                break;
            case 1:
                Main3D.cur3D().hud._log(0, "TimeSpeedDown4");
                break;
        }
    }

    public AircraftHotKeys() {
        this.lastPower2 = -0.5F;
        this.lastPower1 = -0.5F;
        this.lastPower3 = -0.5F;
        this.lastPower4 = -0.5F;
        this.lastProp1 = 1.5F;
        this.lastProp2 = 1.5F;
        this.lastProp3 = 1.5F;
        this.lastProp4 = 1.5F;
        this.lastRadiator = -0.5F;
        this.changeFovEnabled = true;

        this.bPropAuto = true;
        this.bAfterburner = false;
        this.lastPower = -0.5F;
        this.lastProp = 1.5F;
// cptdmg = 1;
        this.bAutoAutopilot = false;
        this.switchToCockpitRequest = -1;
        this.cmdFov = new HotKeyCmd[16];
        this.createPilotHotKeys();
        this.createPilotHotMoves();
        this.createGunnerHotKeys();
        this.createMiscHotKeys();
        this.create_MiscHotKeys();
        this.createViewHotKeys();
        this.createTimeHotKeys();

        this.createCommonHotKeys();

        if (Config.cur.ini.get("Mods", "SpeedbarTAS", 0) > 0) this.bSpeedbarTAS = true;
        if (Config.cur.ini.get("Mods", "SeparateGearUpDown", 0) > 0) this.bSeparateGearUpDown = true;
        if (Config.cur.ini.get("Mods", "SeparateHookUpDown", 0) > 0) this.bSeparateHookUpDown = true;
        if (Config.cur.ini.get("Mods", "SeparateRadiatorOpenClose", 0) > 0) this.bSeparateRadiatorOpenClose = true;
        if (Config.cur.ini.get("Mods", "ToggleMusic", 0) > 0) this.bToggleMusic = true;
        if (Config.cur.ini.get("Mods", "BombBayDoors", 1) == 0) this.bBombBayDoors = false;
        if (Config.cur.ini.get("Mods", "SideDoor", 1) == 0) this.bSideDoor = false;
        this.iAirShowSmoke = Config.cur.ini.get("Mods", "AirShowSmoke", 0);
        if (this.iAirShowSmoke < 1 || this.iAirShowSmoke > 3) this.iAirShowSmoke = 0;
// if (Config.cur.ini.get("Mods", "AirShowSmokeEnhanced", 0) > 0)
// bAirShowSmokeEnhanced = true;
        if (Config.cur.ini.get("Mods", "DumpFuel", 0) > 0) this.bAllowDumpFuel = true;
        
  // Just for testing... get Windows UUID      
//        try {
//            String command = "wmic csproduct get UUID";
//            StringBuffer output = new StringBuffer();
//    
//            Process SerNumProcess;
//                SerNumProcess = Runtime.getRuntime().exec(command);
//            BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));
//    
//            String line = "";
//            while ((line = sNumReader.readLine()) != null) {
//                output.append(line + "\n");
//            }
//            String MachineID=output.toString().substring(output.toString().indexOf('\n'), output.length()).trim();;
//            System.out.println(MachineID);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
       
        
        
    }

    private Actor getViewActor() {
        if (Selector.isEnableTrackArgs()) return Selector.setCurRecordArg0(Selector.getTrackArg0());
        Actor actor = Main3D.cur3D().viewActor();
        if (this.isViewed(actor)) return Selector.setCurRecordArg0(actor);
        return Selector.setCurRecordArg0(World.getPlayerAircraft());
    }

// private Actor nextViewActorOld(boolean bool) {
// if (Selector.isEnableTrackArgs())
// return Selector.setCurRecordArg0(Selector.getTrackArg0());
// int i = World.getPlayerArmy();
// namedAircraft.clear();
// Actor actor = Main3D.cur3D().viewActor();
// if (this.isViewed(actor))
// namedAircraft.put(actor.name(), null);
// for (Map.Entry entry = Engine.name2Actor().nextEntry(null); entry != null; entry = Engine.name2Actor().nextEntry(entry)) {
// Actor actor_319_ = (Actor) entry.getValue();
//
// // TODO: Added by |ZUTI|: Disable showing of paratroopers! New difficulty setting?
// // -------------------------------------------------
// if (actor_319_ instanceof Paratrooper)
// continue;
// // -------------------------------------------------
//
// if (this.isViewed(actor_319_) && actor_319_ != actor) {
// if (bool) {
// if (actor_319_.getArmy() != i)
// namedAircraft.put(actor_319_.name(), null);
// } else if (actor_319_.getArmy() == i)
// namedAircraft.put(actor_319_.name(), null);
// }
// }
// if (namedAircraft.size() == 0)
// return Selector.setCurRecordArg0(null);
// if (!this.isViewed(actor))
// return (Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAircraft.firstKey())));
// if (namedAircraft.size() == 1 && this.isViewed(actor))
// return Selector.setCurRecordArg0(null);
// namedAll = namedAircraft.keySet().toArray(namedAll);
// int i_320_ = 0;
// String string = actor.name();
// for (/**/; namedAll[i_320_] != null; i_320_++) {
// if (string.equals(namedAll[i_320_]))
// break;
// }
// if (namedAll[i_320_] == null)
// return Selector.setCurRecordArg0(null);
// i_320_++;
// if (namedAll.length == i_320_ || namedAll[i_320_] == null)
// i_320_ = 0;
// return Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAll[i_320_]));
// }

    // TODO: +++ Added by SAS~Storebor: Show Player Aircraft before any AI Actors! +++
    private Actor nextViewActor(boolean showEnemy) {
        if (Selector.isEnableTrackArgs()) return Selector.setCurRecordArg0(Selector.getTrackArg0());
        int playerArmyIndex = World.getPlayerArmy();
        namedAircraftPlayer.clear();
        namedAircraftAI.clear();
        Actor viewActor = Main3D.cur3D().viewActor();
        TreeMap curTreeMap = null;
        if (this.isViewed(viewActor)) {
            NetUser netuser = null;
            if (viewActor instanceof Aircraft) netuser = ((NetAircraft) viewActor).netUser();
            else if (viewActor instanceof Paratrooper) netuser = (NetUser) Reflection.getValue(viewActor, "driver");
            curTreeMap = netuser == null ? namedAircraftAI : namedAircraftPlayer;
            curTreeMap.put(viewActor.name(), null);
// System.out.println("Adding " + viewActor.name() + " to list of " + (curTreeMap == namedAircraftAI ? "AI" : "player") + " actors!");
        }
        for (Map.Entry actorEntry = Engine.name2Actor().nextEntry(null); actorEntry != null; actorEntry = Engine.name2Actor().nextEntry(actorEntry)) {
            Actor curActor = (Actor) actorEntry.getValue();
            NetUser netuser = null;
            if (curActor instanceof Aircraft) netuser = ((NetAircraft) curActor).netUser();
            else if (curActor instanceof Paratrooper) netuser = (NetUser) Reflection.getValue(curActor, "driver");
            curTreeMap = netuser == null ? namedAircraftAI : namedAircraftPlayer;

            // Don't show AI Paratroopers, show only player Paratroopers.
            if (curTreeMap == namedAircraftAI && curActor instanceof Paratrooper) continue;

            if (this.isViewed(curActor) && curActor != viewActor) if (showEnemy) {
                if (curActor.getArmy() != playerArmyIndex) // System.out.println("Adding " + curActor.name() + " to list of enemy " + (curTreeMap == namedAircraftAI ? "AI" : "player") + " actors!");
                    curTreeMap.put(curActor.name(), null);
            } else if (curActor.getArmy() == playerArmyIndex) // System.out.println("Adding " + curActor.name() + " to list of friendly " + (curTreeMap == namedAircraftAI ? "AI" : "player") + " actors!");
                curTreeMap.put(curActor.name(), null);
        }

        if (namedAircraftAI.size() + namedAircraftPlayer.size() == 0) return Selector.setCurRecordArg0(null);
        if (!this.isViewed(viewActor) && namedAircraftPlayer.size() != 0 && namedAircraftPlayer.firstKey() != null) return Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAircraftPlayer.firstKey()));
        if (!this.isViewed(viewActor) && namedAircraftAI.size() != 0 && namedAircraftAI.firstKey() != null) return Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAircraftAI.firstKey()));
        if (namedAircraftAI.size() + namedAircraftPlayer.size() == 1 && this.isViewed(viewActor)) return Selector.setCurRecordArg0(null);

// System.out.println("Initializing Arrays");
// for (int i = 0; i < namedAllPlayer.length; i++) namedAllPlayer[i] = null;
// for (int i = 0; i < namedAllAI.length; i++) namedAllAI[i] = null;
// for (int i = 0; i < namedAll.length; i++) namedAll[i] = null;

        namedAllPlayer = namedAircraftPlayer.keySet().toArray(namedAllPlayer);
        namedAllAI = namedAircraftAI.keySet().toArray(namedAllAI);
        namedAll = this.concat(namedAllPlayer, namedAllAI, namedAll);

// // TEST
// for (int i = 0; i < namedAll.length; i++) {
// if (namedAll[i] != null)
// System.out.println("namedAll[" + i + "]=" + namedAll[i].toString());
// else
// break;
// }
// // ---

        int viewIndex = 0;
        String string = viewActor.name();
// System.out.println("viewActor.name()=" + string);
        for (; namedAll[viewIndex] != null; viewIndex++)
            if (string.equals(namedAll[viewIndex])) break;
        if (namedAll[viewIndex] == null) // System.out.println("viewIndex=" + viewIndex + ", namedAll[viewIndex]=null");
            return Selector.setCurRecordArg0(null);
// System.out.println("viewIndex=" + viewIndex + ", namedAll[viewIndex]=" + namedAll[viewIndex].toString());
        viewIndex++;
        if (namedAll.length == viewIndex || namedAll[viewIndex] == null) viewIndex = 0;
// System.out.println("viewIndex++=" + viewIndex + ", namedAll[viewIndex++]=" + namedAll[viewIndex].toString());
        return Selector.setCurRecordArg0((Actor) Engine.name2Actor().get(namedAll[viewIndex]));
    }

    private Object[] concat(Object[] a, Object[] b, Object[] c) {
        int aLen = 0;
        for (aLen = 0; aLen < a.length; aLen++)
            if (a[aLen] == null) break;
        int bLen = 0;
        for (bLen = 0; bLen < b.length; bLen++)
            if (b[bLen] == null) break;
        if (c.length < aLen + bLen + 1) c = new Object[aLen + bLen + 1];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        c[aLen + bLen] = null;
        return c;
    }
    // TODO: --- Added by SAS~Storebor: Show Player Aircraft before any AI Actors! ---

    private boolean isViewed(Actor actor) {
        if (!Actor.isValid(actor)) return false;
        return actor instanceof Aircraft || actor instanceof Paratrooper || actor instanceof ActorViewPoint || actor instanceof BigshipGeneric && ((BigshipGeneric) actor).getAirport() != null;
    }

    // TODO: |ZUTI| methods and variables
    // ------------------------------------------------
    private int zutiBombReleaseCounter = 0;

    public FlightModel zutiGetFM() {
        return this.FM;
    }

    // +++++ TODO skylla: enhanced weapon release control ++++++

    // must replicate all EWRC settings at once, as switching one setting may affect the others as well!
    private void replicateEWRCSettingsToNet() {
        this.FM.AS.replicateRocketSelectedToNet(this.FM.CT.getRocketIndexSelected());
        this.FM.AS.replicateRocketReleaseModeToNet(this.FM.CT.getSelectedRocketSalvoSizeIndex());
        this.FM.AS.replicateRocketReleaseDelayToNet(this.FM.CT.getSelectedRocketReleaseDelayIndex());
        this.FM.AS.replicateBombSelectedToNet(this.FM.CT.getBombIndexSelected());
        this.FM.AS.replicateBombReleaseModeToNet(this.FM.CT.getSelectedBombSalvoSizeIndex());
        this.FM.AS.replicateBombReleaseDelayToNet(this.FM.CT.getSelectedBombReleaseDelayIndex());
    }

    // ----- todo skylla: enhanced weapon release control ------

    // TODO: +++ 4.12.2 adaptations by SAS~Storebror
    public void enableBombSightFov() {
        this.setEnableChangeFov(true);
        this.bombSightFovEnabled = true;
    }
    // TODO: --- 4.12.2 adaptations by SAS~Storebror

    public boolean isbExtViewEnemy() {
        return bExtViewEnemy;
    }

    public boolean isbExtViewFriendly() {
        return bExtViewFriendly;
    }

    public boolean isbExtViewSelf() {
        return bExtViewSelf;
    }

    public boolean isbExtViewGround() {
        return bExtViewGround;
    }

    public boolean isbExtViewDead() {
        return bExtViewDead;
    }

    public boolean isbExtPadlockEnemy() {
        return bExtPadlockEnemy;
    }

    public boolean isbExtPadlockFriendly() {
        return bExtPadlockFriendly;
    }

    // +++ By SAS~Storebror: Enter Cockpit if External Views are allowed on ground only! +++
    public void checkViewOnAirborne() {
        if (!Main3D.cur3D().isViewOutside()) return;
        if (!World.cur().diffCur.No_Outside_Views || this.bExtViewSelf || this.bExtViewEnemy || this.bExtViewFriendly) return;
        HotKeyCmd.exec("aircraftView", "CockpitView");
    }       
    // --- By SAS~Storebror: Enter Cockpit if External Views are allowed on ground only! ---


}
