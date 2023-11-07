/* 4.10.1 class made compatible with clean UP. Contains BombBayDoor code. + TAK brake controls */
package com.maddox.il2.fm;

import java.util.Arrays;
import java.util.LinkedList;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.GuidedMissileInterop;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.ZutiSupportMethods_Multicrew;
import com.maddox.il2.net.ZutiSupportMethods_NetSend;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.SU_26M2;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeEnhancedWeaponReleaseControl;
import com.maddox.il2.objects.air.TypeGuidedMissileCarrier;
import com.maddox.il2.objects.air.TypeLimitedWeaponReleaseControl;
import com.maddox.il2.objects.air.TypeX4Carrier;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.BombGunNull;
import com.maddox.il2.objects.weapons.FuelTank;
import com.maddox.il2.objects.weapons.FuelTankGun;
import com.maddox.il2.objects.weapons.GuidedMissileUtils;
import com.maddox.il2.objects.weapons.MissileGun;
import com.maddox.il2.objects.weapons.RocketBombGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.il2.objects.weapons.RocketGunNull;
import com.maddox.il2.objects.weapons.RocketGunX4;
import com.maddox.il2.objects.weapons.RocketGunX4R;
import com.maddox.il2.objects.weapons.RocketGunX4homing;
import com.maddox.il2.objects.weapons.TorpedoGun;
import com.maddox.rts.Time;

public class Controls {
    public float             Sensitivity;
    public float             PowerControl;
    public boolean           afterburnerControl;
    public float             GearControl;
    public float             wingControl;
    public float             cockpitDoorControl;
    public float             arrestorControl;
    public float             FlapsControl;
    public float             AileronControl;
    public float             ElevatorControl;
    public float             RudderControl;
    public float             BrakeControl;
    private float            StepControl;
    private boolean          bStepControlAuto;
    private float            MixControl;
    private int              MagnetoControl;
    private int              CompressorControl;
    public float             BayDoorControl;
    public float             AirBrakeControl;
    private float            trimAileronControl;
    private float            trimElevatorControl;
    private float            trimRudderControl;
    public float             trimAileron;
    public float             trimElevator;
    public float             trimRudder;
    private float            RadiatorControl;
    private boolean          bRadiatorControlAuto;
    public boolean           StabilizerControl;
    public boolean[]         WeaponControl;
    public boolean[]         saveWeaponControl;
    public boolean           bHasGearControl;
    public boolean           bHasWingControl;
    public boolean           bHasCockpitDoorControl;
    public boolean           bHasArrestorControl;
    public boolean           bHasFlapsControl;
    public boolean           bHasFlapsControlRed;
    public boolean           bHasAileronControl;
    public boolean           bHasElevatorControl;
    public boolean           bHasRudderControl;
    public boolean           bHasBrakeControl;
    public boolean           bHasAirBrakeControl;
    public boolean           bHasLockGearControl;
    public boolean           bHasAileronTrim;
    public boolean           bHasRudderTrim;
    public boolean           bHasElevatorTrim;
    public BulletEmitter[][] Weapons;
    public byte              CTL;
    public byte              WCT;
    public int               TWCT;
    private float            Power;
    private float            Gear;
    private float            wing;
    private float            cockpitDoor;
    private float            arrestor;
    private float            Flaps;
    // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
//    private float            Ailerons;
//    private float            Elevators;
//    private float            Rudder;
    public float            Ailerons;
    public float            Elevators;
    public float            Rudder;
    // ---
    private float           Brake;
    private float           Step;
    private float           radiator;
    private float           airBrake;
    private float           Ev;
    private int             tick;
    public float            AilThr;
    public float            AilThr3;
    public float            RudThr;
    public float            RudThr2;
    public float            ElevThr;
    public float            ElevThr2;
    public float            dvGear;
    public float            dvWing;
    public float            dvCockpitDoor;
    public float            dvAirbrake;
    private FlightModelMain FM;
    private static float    tmpF;
    private static Vector3d tmpV3d        = new Vector3d();
    public boolean          bHasBayDoors  = false;
    private float           fSaveCockpitDoor;
    private float           fSaveCockpitDoorControl;
    private float           fSaveSideDoor;
    private float           fSaveSideDoorControl;
    public boolean          bMoveSideDoor = false;
    // private int COCKPIT_DOOR = 1;
    private int             SIDE_DOOR     = 2;

    public int              electricPropUp;
    public int              electricPropDn;
    public boolean          bUseElectricProp;
    public float            PowerControlArr[];
    public float            StepControlArr[];

    // TODO: Modified by |ZUTI|: changed from private to public
    // --------------------------------------------------------
    public int LEFT              = 1;
    public int RIGHT             = 2;
    public int iToggleRocketSide = this.LEFT;
    public int iToggleBombSide   = this.LEFT;
//    public long              lWeaponTime       = System.currentTimeMillis();
//    public boolean           bIsMustang        = false;

    // --------------------------------------------------------

    // +++++ TODO skylla: enhanced weapon release control +++++
    /**
     * This class got modified for the 'Enhanced Weapon Release Control for UP3' - Mod by SAS~Skylla in 10/17. Other classes affected:
     *
     * @see AircraftHotkeys, AircraftState, ZutiWeaponsManagement, TypeEnhancedWeaponReleaseControl, TypeLimitedWeaponReleaseControl
     **/

    private boolean          hasLetGoBTriggerMeanwhile          = false;
    private boolean          hasLetGoRTriggerMeanwhile          = false;

    private boolean          hasToInitiateEnhancedWeaponOptions = true;

    public static final long releaseDelayMIN                    = 33L;
    public static final long releaseDelayMAX                    = 15000L;

    public static final int  bombGroupDropLimit                 = 50;

    private int[]            bombSalvoSizeOptions;
    private int[]            rocketSalvoSizeOptions;

    private long[]           bombReleaseDelayOptions;
    private long[]           rocketReleaseDelayOptions;

    private int              rocketFireMode                     = defaultFire;
    private int              bombDropMode                       = defaultFire;
    private long             lastRocketTime                     = System.currentTimeMillis();
    private long             lastBombTime                       = System.currentTimeMillis();
    private long             rocketReleaseDelay                 = 250L;
    private long             bombReleaseDelay                   = 250L;

    public boolean           rocketShootLeft                    = true;
    public boolean           bombDropLeft                       = true;

    public static final int  defaultFire                        = -1;
    public static final int  fullSalvo                          = 0;
    public static final int  singleFire                         = 1;

    private int              bombsDropped                       = 0;
    private boolean          isGroupRelease                     = false;

    private LinkedList       availableRockets;
    private LinkedList       availableBombs;

    private Class            selectedRocket                     = RocketGun.class;
    private Class            selectedBomb                       = BombGun.class;
    // ----- todo skylla: enhanced weapon release control -----

    // TODO: Guided Missiles Update
    public int    rocketHookSelected = 2;
    public String rocketNameSelected = null;

    // TODO: Bomb Bay Door Control from fmd file
    public boolean bHasBayDoorControl  = false;
    public boolean bHasSideDoorControl = false;
    
    //TODO: 4.14.1 Backport +++
    public byte pdiLights = -1;
    //TODO: 4.14.1 Backport ---
    
    //TODO: EngineMod 2.8 Backport +++
    public float targetDegreeAITakeoffRotation;
    public float targetDegreeAITakeoffClimb;
    public float targetDegreeAIApproach;
    //TODO: EngineMod 2.8 Backport ---
    
    // TODO: +++ By SAS~Storebror: Additional Checks for X-4 Launch
    private boolean bToFire;
    private long tX4Prev;
    // ---

    public Controls(FlightModelMain flightmodelmain) {
        this.Sensitivity = 1.0F;
        this.PowerControl = 0.0F;
        this.GearControl = 0.0F;
        this.wingControl = 0.0F;
        this.cockpitDoorControl = 0.0F;
        this.arrestorControl = 0.5F;
        this.FlapsControl = 0.0F;
        this.AileronControl = 0.0F;
        this.ElevatorControl = 0.0F;
        this.RudderControl = 0.0F;
        this.BrakeControl = 0.0F;
        this.StepControl = 1.0F;
        this.bStepControlAuto = true;
        this.MixControl = 1.0F;
        this.MagnetoControl = 3;
        this.CompressorControl = 0;
        this.BayDoorControl = 0.0F;
        this.AirBrakeControl = 0.0F;
        this.trimAileronControl = 0.0F;
        this.trimElevatorControl = 0.0F;
        this.trimRudderControl = 0.0F;
        this.trimAileron = 0.0F;
        this.trimElevator = 0.0F;
        this.trimRudder = 0.0F;
        this.RadiatorControl = 0.0F;
        this.bRadiatorControlAuto = true;
        this.StabilizerControl = false;
        this.WeaponControl = new boolean[21];
        this.saveWeaponControl = new boolean[4];
        this.bHasGearControl = true;
        this.bHasWingControl = false;
        this.bHasCockpitDoorControl = false;
        this.bHasArrestorControl = false;
        this.bHasFlapsControl = true;
        this.bHasFlapsControlRed = false;
        this.bHasAileronControl = true;
        this.bHasElevatorControl = true;
        this.bHasRudderControl = true;
        this.bHasBrakeControl = true;
        this.bHasAirBrakeControl = true;
        this.bHasLockGearControl = true;
        this.bHasAileronTrim = true;
        this.bHasRudderTrim = true;
        this.bHasElevatorTrim = true;
        this.Weapons = new BulletEmitter[21][];
        this.Step = 1.0F;
        this.AilThr = 100.0F;
        this.AilThr3 = 1000000.0F;
        this.RudThr = 100.0F;
        this.RudThr2 = 10000.0F;
        this.ElevThr = 112.0F;
        this.ElevThr2 = 12544.0F;
        this.dvGear = 0.2F;
        this.dvWing = 0.1F;
        this.dvCockpitDoor = 0.1F;
        this.dvAirbrake = 0.5F;
        this.electricPropDn = 0;
        this.PowerControlArr = new float[AircraftState.MAX_ENGINES];
        this.StepControlArr = new float[AircraftState.MAX_ENGINES];
        this.FM = flightmodelmain;
        for (int i = 0; i < AircraftState.MAX_ENGINES; i++)
            this.PowerControlArr[i] = 0.0F;
        for (int j = 0; j < AircraftState.MAX_ENGINES; j++)
            this.StepControlArr[j] = 1.0F;
        // TODO: +++ TD AI code backport from 4.13 +++
        this.afterburnerFControl = 0.0F;
        this.bDropWithPlayer = false;
        this.dropWithPlayer = null;
        this.bDropWithMe = false;
        // TODO: --- TD AI code backport from 4.13 ---

        // +++++ TODO skylla: enhanced weapon release control +++++
        // cannot place this here, as my counting of the available bombs does not work here yet.
        // ^irrelevant when using static maximum group drop number.
        // ^true, but I still get no reasonable return value for the methods introduced in the TypeEnhancedWeaponReleaseControl interface. => Therefore, @see update()
        // initiateEnhancedWeaponOptions();
        // ----- todo skylla: enhanced weapon release control -----
        
        //TODO: EngineMod 2.8 Backport +++
        this.targetDegreeAITakeoffRotation = -1.0F;
        this.targetDegreeAITakeoffClimb = -1.0F;
        this.targetDegreeAIApproach = -1.0F;
        //TODO: EngineMod 2.8 Backport ---
        // TODO: +++ By SAS~Storebror: Additional Checks for X-4 Launch
        this.bToFire = false;
        this.tX4Prev = 0L;
        // ---
    }

    public void set(Controls controls_0_) {
        this.PowerControl = controls_0_.PowerControl;
        this.GearControl = controls_0_.GearControl;
        this.arrestorControl = controls_0_.arrestorControl;
        this.FlapsControl = controls_0_.FlapsControl;
        this.AileronControl = controls_0_.AileronControl;
        this.ElevatorControl = controls_0_.ElevatorControl;
        this.RudderControl = controls_0_.RudderControl;
        this.BrakeControl = controls_0_.BrakeControl;
        this.BayDoorControl = controls_0_.BayDoorControl;
        this.AirBrakeControl = controls_0_.AirBrakeControl;
        this.dvGear = controls_0_.dvGear;
        this.dvWing = controls_0_.dvWing;
        this.dvCockpitDoor = controls_0_.dvCockpitDoor;
        this.dvAirbrake = controls_0_.dvAirbrake;
    }

    public void CalcTresholds() {
        this.AilThr3 = this.AilThr * this.AilThr * this.AilThr;
        this.RudThr2 = this.RudThr * this.RudThr;
        this.ElevThr2 = this.ElevThr * this.ElevThr;
    }

    public void setLanded() {
        if (this.bHasGearControl) this.GearControl = this.Gear = 1.0F;
        else this.Gear = 1.0F;
        this.FlapsControl = this.Flaps = 0.0F;
        this.StepControl = 1.0F;
        this.bStepControlAuto = true;
        this.bRadiatorControlAuto = true;
        this.BayDoorControl = 0.0F;
        this.AirBrakeControl = 0.0F;
    }

    public void setFixedGear(boolean bool) {
        if (bool) {
            this.Gear = 1.0F;
            this.GearControl = 0.0F;
        }
    }

    public void setGearAirborne() {
        if (this.bHasGearControl) this.GearControl = this.Gear = 0.0F;
    }

    public void setGear(float f) {
        this.Gear = f;
    }

    public void setGearBraking() {
        this.Brake = 1.0F;
    }

    public void forceFlaps(float f) {
        this.Flaps = f;
    }

    public void forceGear(float f) {
        if (this.bHasGearControl) this.Gear = this.GearControl = f;
        else this.setFixedGear(true);
    }

    public void forceWing(float f) {
        this.wing = f;
        this.FM.doRequestFMSFX(1, (int) (100.0F * f));
        ((Aircraft) this.FM.actor).moveWingFold(f);
    }

    public void forceArrestor(float f) {
        this.arrestor = f;
        ((Aircraft) this.FM.actor).moveArrestorHook(f);
    }

    public void setPowerControl(float f) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.1F) f = 1.1F;
        this.PowerControl = f;
        for (int i = 0; i < AircraftState.MAX_ENGINES; i++)
            if (i < this.FM.EI.getNum() && this.FM.EI.bCurControl[i]) this.PowerControlArr[i] = f;

    }

    public void setPowerControl(float f, int i) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.1F) f = 1.1F;
        this.PowerControlArr[i] = f;
        if (i == 0) this.PowerControl = f;
    }

    public float getPowerControl() {
        return this.PowerControl;
    }

    public void setStepControl(float f) {
        if (!this.bUseElectricProp) {
            if (f > 1.0F)

                f = 1.0F;
            if (f < 0.0F) f = 0.0F;
            this.StepControl = f;
            for (int i = 0; i < AircraftState.MAX_ENGINES; i++)
                if (i < this.FM.EI.getNum() && this.FM.EI.bCurControl[i]) this.StepControlArr[i] = f;

            com.maddox.il2.game.HUD.log(com.maddox.il2.game.AircraftHotKeys.hudLogPowerId, "PropPitch", new java.lang.Object[] { new Integer(java.lang.Math.round(this.getStepControl() * 100F)) });
        }
    }

    public void setStepControl(float f, int i) {
        if (!this.bUseElectricProp) {
            if (f > 1.0F) f = 1.0F;
            if (f < 0.0F) f = 0.0F;
            this.StepControlArr[i] = f;
            if (!this.getStepControlAuto(i)) com.maddox.il2.game.HUD.log(com.maddox.il2.game.AircraftHotKeys.hudLogPowerId, "PropPitch", new java.lang.Object[] { new Integer(java.lang.Math.round(this.getStepControl(i) * 100F)) });
        }
    }

    public boolean getStepControlAuto(int i) {
        if (i < this.FM.EI.getNum()) return !this.FM.EI.engines[i].isHasControlProp() || this.FM.EI.engines[i].getControlPropAuto();
        else return true;
    }

    public float getStepControl(int i) {
        return this.StepControlArr[i];
    }

    public void setElectricPropUp(boolean flag) {
        if (this.bUseElectricProp) if (flag) this.electricPropUp = 1;
        else this.electricPropUp = 0;
    }

    public void setElectricPropDn(boolean flag) {
        if (this.bUseElectricProp) if (flag) this.electricPropDn = 1;
        else this.electricPropDn = 0;
    }

    public void setStepControlAuto(boolean bool) {
        this.bStepControlAuto = bool;
    }

    public float getStepControl() {
        return this.StepControl;
    }

    public boolean getStepControlAuto() {
        return this.bStepControlAuto;
    }

    public void setRadiatorControl(float f) {
        if (f > 1.0F) f = 1.0F;
        if (f < 0.0F) f = 0.0F;
        this.RadiatorControl = f;
    }

    public void setRadiatorControlAuto(boolean bool, EnginesInterface enginesinterface) {
        this.bRadiatorControlAuto = bool;
        if (enginesinterface.getFirstSelected() != null) this.radiator = enginesinterface.getFirstSelected().getControlRadiator();
    }

    public float getRadiatorControl() {
        return this.RadiatorControl;
    }

    public boolean getRadiatorControlAuto() {
        return this.bRadiatorControlAuto;
    }

    public float getRadiator() {
        return this.radiator;
    }

    public void setMixControl(float f) {
        if (f < 0.0F) f = 0.0F;
        if (f > 1.2F) f = 1.2F;
        this.MixControl = f;
    }

    public float getMixControl() {
        return this.MixControl;
    }

    public void setAfterburnerControl(boolean bool) {
        this.afterburnerControl = bool;
    }

    public boolean getAfterburnerControl() {
        return this.afterburnerControl;
    }

    public void setMagnetoControl(int i) {
        if (i < 0) i = 0;
        if (i > 3) i = 3;
        this.MagnetoControl = i;
    }

    public int getMagnetoControl() {
        return this.MagnetoControl;
    }

    public void setCompressorControl(int i) {
        if (i < 0) i = 0;
        if (i > this.FM.EI.engines[0].compressorMaxStep) i = this.FM.EI.engines[0].compressorMaxStep;
        this.CompressorControl = i;
    }

    public int getCompressorControl() {
        return this.CompressorControl;
    }

    public void setTrimAileronControl(float f) {
        this.trimAileronControl = f;
    }

    public float getTrimAileronControl() {
        return this.trimAileronControl;
    }

    public void setTrimElevatorControl(float f) {
        this.trimElevatorControl = f;
    }

    public float getTrimElevatorControl() {
        return this.trimElevatorControl;
    }

    public void setTrimRudderControl(float f) {
        this.trimRudderControl = f;
    }

    public float getTrimRudderControl() {
        return this.trimRudderControl;
    }

    public void interpolate(Controls controls, float f) {
        this.PowerControl = FMMath.interpolate(this.PowerControl, controls.PowerControl, f);
        this.FlapsControl = FMMath.interpolate(this.FlapsControl, controls.FlapsControl, f);
        this.AileronControl = FMMath.interpolate(this.AileronControl, controls.AileronControl, f);
        this.ElevatorControl = FMMath.interpolate(this.ElevatorControl, controls.ElevatorControl, f);
        this.RudderControl = FMMath.interpolate(this.RudderControl, controls.RudderControl, f);
        this.BrakeControl = FMMath.interpolate(this.BrakeControl, controls.BrakeControl, f);

        // TAK++
        this.BrakeRightControl = FMMath.interpolate(this.BrakeRightControl, controls.BrakeRightControl, f);
        this.BrakeLeftControl = FMMath.interpolate(this.BrakeLeftControl, controls.BrakeLeftControl, f);
        // TAK--
    }

    public float getGear() {
        return this.Gear;
    }

    //TODO: 4.14.1 Backport +++
    public float getGearC() {
        return this.Gear;
    }

    public float getGearL() {
        return this.Gear;
    }

    public float getGearR() {
        return this.Gear;
    }
    //TODO: 4.14.1 Backport ---

    public float getWing() {
        return this.wing;
    }

    public float getCockpitDoor() {
        return this.cockpitDoor;
    }

    public void forceCockpitDoor(float f) {
        this.cockpitDoor = f;
    }

    public float getArrestor() {
        return this.arrestor;
    }

    public float getFlap() {
        return this.Flaps;
    }

    public float getAileron() {
        return this.Ailerons;
    }

    public float getElevator() {
        return this.Elevators;
    }

    public float getRudder() {
        return this.Rudder;
    }

    public float getBrake() {
        return this.Brake;
    }

    //TODO: 4.14.1 Backport +++
    public float getBrakeL() {
        return this.Brake;
    }

    public float getBrakeR() {
        return this.Brake;
    }
    //TODO: 4.14.1 Backport ---

    public float getPower() {
        return this.Power;
    }

    public float getStep() {
        return this.Step;
    }

    public float getBayDoor() {
        return this.BayDoorControl;
    }

    public float getAirBrake() {
        return this.airBrake;
    }

    private float filter(float f, float f_2_, float f_3_, float f_4_, float f_5_) {
        float f_6_ = (float) Math.exp(-f / f_4_);
        float f_7_ = f_2_ + (f_3_ - f_2_) * f_6_;
        if (f_7_ < f_2_) {
            f_7_ += f_5_ * f;
            if (f_7_ > f_2_) f_7_ = f_2_;
        } else if (f_7_ > f_2_) {
            f_7_ -= f_5_ * f;
            if (f_7_ < f_2_) f_7_ = f_2_;
        }
        return f_7_;
    }

    private float clamp01(float f) {
        if (f < 0.0F) f = 0.0F;
        else if (f > 1.0F) f = 1.0F;
        return f;
    }

    private float clamp0115(float f) {
        if (f < 0.0F) f = 0.0F;
        else if (f > 1.1F) f = 1.1F;
        return f;
    }

    private float clamp11(float f) {
        if (f < -1.0F) f = -1.0F;
        else if (f > 1.0F) f = 1.0F;
        return f;
    }

    private float clampA(float f, float f_8_) {
        if (f < -f_8_) f = -f_8_;
        else if (f > f_8_) f = f_8_;
        return f;
    }

    public void setActiveDoor(int i) {
        if (i != this.SIDE_DOOR && this.bMoveSideDoor) {
            this.fSaveSideDoor = this.cockpitDoor;
            this.fSaveSideDoorControl = this.cockpitDoorControl;
            this.cockpitDoor = this.fSaveCockpitDoor;
            this.cockpitDoorControl = this.fSaveCockpitDoorControl;
            this.bMoveSideDoor = false;
        } else if (i == this.SIDE_DOOR && !this.bMoveSideDoor) {
            this.fSaveCockpitDoor = this.cockpitDoor;
            this.fSaveCockpitDoorControl = this.cockpitDoorControl;
            this.cockpitDoor = this.fSaveSideDoor;
            this.cockpitDoorControl = this.fSaveSideDoorControl;
            this.bMoveSideDoor = true;
        }
    }

    public void update(float f, float f_9_, EnginesInterface enginesinterface, boolean bool) {
        this.update(f, f_9_, enginesinterface, bool, false);
    }

    public void update(float f, float f_10_, EnginesInterface enginesinterface, boolean bool, boolean bool_11_) {

        // TODO: +++ By SAS~Storebror: Additional Checks for X-4 Launch
        this.updateX4();
        // ---
        // +++++ TODO skylla: enhanced weapon release control +++++
        // not needed anymore with static bomb group limit.
        // ^true, but it _is_ needed in order to get reasonable return values from the methods introduced with the TypeEnhancedWeaponReleaseControl interface!
        if (this.hasToInitiateEnhancedWeaponOptions) {
            this.initiateEnhancedWeaponOptions();
            this.hasToInitiateEnhancedWeaponOptions = false;
        }
        // ----- todo skylla: enhanced weapon release control -----

        float f_12_ = 1.0F;
        float f_13_ = 1.0F;
        float f_14_ = 1.0F;
        float f_15_ = f_10_ * f_10_;
        if (f_10_ > this.AilThr) f_12_ = Math.max(0.2F, this.AilThr3 / (f_15_ * f_10_));
        if (f_15_ > this.RudThr2) f_13_ = Math.max(0.2F, this.RudThr2 / f_15_);
        if (f_15_ > this.ElevThr2) f_14_ = Math.max(0.4F, this.ElevThr2 / f_15_);
        f_12_ *= this.Sensitivity;
        f_13_ *= this.Sensitivity;
        f_14_ *= this.Sensitivity;
        if (this.Elevators >= 0.0F && !(this.FM.actor instanceof SU_26M2)) f_14_ = f_13_;
        if (!bool_11_) if (this.FM instanceof com.maddox.il2.fm.RealFlightModel) {
            float f6 = 0.0F;
            for (int j1 = 0; j1 < enginesinterface.getNum(); j1++) {
                this.PowerControlArr[j1] = this.clamp0115(this.PowerControlArr[j1]);
                enginesinterface.engines[j1].setControlThrottle(this.PowerControlArr[j1]);
                if (this.PowerControlArr[j1] > f6) f6 = this.PowerControlArr[j1];
            }

            if (bool) this.Power = f6;
            else {
                this.Power = this.filter(f, f6, this.Power, 5F, 0.01F * f);
                enginesinterface.setThrottle(this.Power);
            }
        } else {
            this.PowerControl = this.clamp0115(this.PowerControl);
            if (bool) this.Power = this.PowerControl;

            else this.Power = this.filter(f, this.PowerControl, this.Power, 5F, 0.01F * f);
            enginesinterface.setThrottle(this.Power);

        }
        // TODO: +++ TD AI code backport from 4.13 +++
//        if (!bool_11_)
//            enginesinterface.setAfterburnerControl(this.afterburnerControl);
        if (!bool_11_) {
            enginesinterface.setAfterburnerControl(this.afterburnerControl);
            enginesinterface.setManualAfterburnerControl(this.afterburnerFControl);
            // TODO: --- TD AI code backport from 4.13 ---
            this.StepControl = this.clamp01(this.StepControl);
            if (this.bUseElectricProp && this.FM instanceof com.maddox.il2.fm.RealFlightModel) {
                enginesinterface.setPropAuto(this.bStepControlAuto);
                int i = this.electricPropUp - this.electricPropDn;
                if (i < 0) com.maddox.il2.game.HUD.log(com.maddox.il2.game.AircraftHotKeys.hudLogPowerId, "elPropDn");
                else if (i > 0) com.maddox.il2.game.HUD.log(com.maddox.il2.game.AircraftHotKeys.hudLogPowerId, "elPropUp");
                enginesinterface.setPropDelta(i);
            }
            if (this.bStepControlAuto && enginesinterface.getFirstSelected() != null) {
                if (enginesinterface.isSelectionAllowsAutoProp()) enginesinterface.setPropAuto(true);
                else {
                    enginesinterface.setPropAuto(false);
                    this.bStepControlAuto = false;
                }
            } else if (this.FM instanceof com.maddox.il2.fm.RealFlightModel) {
                if (!this.bUseElectricProp) for (int j = 0; j < enginesinterface.getNum(); j++) {
                    this.StepControlArr[j] = this.clamp01(this.StepControlArr[j]);
                    enginesinterface.engines[j].setControlPropAuto(false);
                    enginesinterface.engines[j].setControlProp(this.StepControlArr[j]);
                }
            } else {
                this.Step = this.filter(f, this.StepControl, this.Step, 0.2F, 0.02F);
                enginesinterface.setPropAuto(false);
                enginesinterface.setProp(this.Step);
            }
        }
        this.RadiatorControl = this.clamp01(this.RadiatorControl);
        this.radiator = this.filter(f, this.RadiatorControl, this.radiator, 999.9F, 0.2F);
        if (this.bRadiatorControlAuto && enginesinterface.getFirstSelected() != null) {
            if (enginesinterface.isSelectionAllowsAutoRadiator()) enginesinterface.updateRadiator(f);
            else {
                enginesinterface.setRadiator(this.radiator);
                this.bRadiatorControlAuto = false;
            }
        } else enginesinterface.setRadiator(this.radiator);
        if (!bool_11_) enginesinterface.setMagnetos(this.MagnetoControl);
        if (!bool_11_ && bool) enginesinterface.setCompressorStep(this.CompressorControl);
        if (!bool_11_) enginesinterface.setMix(this.MixControl);
        if (this.bHasGearControl || bool_11_) {
            this.GearControl = this.clamp01(this.GearControl);
            this.Gear = this.filter(f, this.GearControl, this.Gear, 999.9F, this.dvGear);
        }
        if (this.bHasAirBrakeControl || bool_11_) this.airBrake = this.filter(f, this.AirBrakeControl, this.airBrake, 999.9F, this.dvAirbrake);
        if (this.bHasWingControl) {
            this.wing = this.filter(f, this.wingControl, this.wing, 999.9F, this.dvWing);
            if (this.wing > 0.01F && this.wing < 0.99F) this.FM.doRequestFMSFX(1, (int) (100.0F * this.wing));
        }
        if (this.bHasCockpitDoorControl) this.cockpitDoor = this.filter(f, this.cockpitDoorControl, this.cockpitDoor, 999.9F, this.dvCockpitDoor);
        if ((this.bHasArrestorControl || bool_11_) && (this.arrestorControl == 0.0F || this.arrestorControl == 1.0F)) this.arrestor = this.filter(f, this.arrestorControl, this.arrestor, 999.9F, 0.2F);
        if (this.bHasFlapsControl || bool_11_) {
            this.FlapsControl = this.clamp01(this.FlapsControl);
            if (this.Flaps > this.FlapsControl) this.Flaps = this.filter(f, this.FlapsControl, this.Flaps, 999.0F, Aircraft.cvt(this.FM.getSpeedKMH(), 150.0F, 280.0F, 0.15F, 0.25F));
            else this.Flaps = this.filter(f, this.FlapsControl, this.Flaps, 999.0F, Aircraft.cvt(this.FM.getSpeedKMH(), 150.0F, 280.0F, 0.15F, 0.02F));
        }
        if (this.StabilizerControl) {
            this.AileronControl = -0.2F * this.FM.Or.getKren() - 2.0F * (float) this.FM.getW().x;
            tmpV3d.set(this.FM.Vwld);
            tmpV3d.normalize();
            float f_16_ = (float) (-500.0 * (tmpV3d.z - 0.0010));
            if (f_16_ > 0.8F) f_16_ = 0.8F;
            if (f_16_ < -0.8F) f_16_ = -0.8F;
            this.ElevatorControl = f_16_ - 0.2F * this.FM.Or.getTangage() - 0.05F * this.FM.AOA + 25.0F * (float) this.FM.getW().y;
            this.RudderControl = -0.2F * this.FM.AOS + 20.0F * (float) this.FM.getW().z;
        }
        if (this.bHasAileronControl || bool_11_) {
            this.trimAileron = this.filter(f, this.trimAileronControl, this.trimAileron, 999.9F, 0.25F);
            this.AileronControl = this.clamp11(this.AileronControl);
            tmpF = this.clampA(this.AileronControl, f_12_);
            this.Ailerons = this.filter(f, (1.0F + (this.trimAileron * tmpF <= 0.0F ? 1.0F : -1.0F) * Math.abs(this.trimAileron)) * tmpF + this.trimAileron, this.Ailerons, 0.2F * (1.0F + 0.3F * Math.abs(this.AileronControl)), 0.025F);
        }
        if (this.bHasElevatorControl || bool_11_) {
            this.trimElevator = this.filter(f, this.trimElevatorControl, this.trimElevator, 999.9F, 0.25F);
            this.ElevatorControl = this.clamp11(this.ElevatorControl);
            tmpF = this.clampA(this.ElevatorControl, f_14_);
            this.Ev = this.filter(f, (1.0F + (this.trimElevator * tmpF <= 0.0F ? 1.0F : -1.0F) * Math.abs(this.trimElevator)) * tmpF + this.trimElevator, this.Ev, 0.3F * (1.0F + 0.3F * Math.abs(this.ElevatorControl)), 0.022F);
            if (this.FM.actor instanceof SU_26M2) this.Elevators = this.clamp11(this.Ev);
            else this.Elevators = this.clamp11(this.Ev - 0.25F * (1.0F - f_14_));
        }
        if (this.bHasRudderControl || bool_11_) {
            this.trimRudder = this.filter(f, this.trimRudderControl, this.trimRudder, 999.9F, 0.25F);
            this.RudderControl = this.clamp11(this.RudderControl);
            tmpF = this.clampA(this.RudderControl, f_13_);
            this.Rudder = this.filter(f, (1.0F + (this.trimRudder * tmpF <= 0.0F ? 1.0F : -1.0F) * Math.abs(this.trimRudder)) * tmpF + this.trimRudder, this.Rudder, 0.35F * (1.0F + 0.3F * Math.abs(this.RudderControl)), 0.025F);
        }
        this.BrakeControl = this.clamp01(this.BrakeControl);
        if (this.bHasBrakeControl || bool_11_) {
            if (this.BrakeControl > this.Brake) this.Brake = this.Brake + 0.3F * f;
            else this.Brake = this.BrakeControl;
        } else this.Brake = 0.0F;
        // TAK++
        if (this.bHasBrakeControl || bool_11_) {
            if (this.BrakeRightControl > this.BrakeRight) this.BrakeRight = this.BrakeRight + 0.3F * f;
            else this.BrakeRight = this.BrakeRightControl;
        } else this.BrakeRight = 0.0F;
        if (this.bHasBrakeControl || bool_11_) {
            if (this.BrakeLeftControl > this.BrakeLeft) this.BrakeLeft = this.BrakeLeft + 0.3F * f;
            else this.BrakeLeft = this.BrakeLeftControl;
        } else this.BrakeLeft = 0.0F;
        // TAK--
        if (this.tick != Time.tickCounter()) {
            this.tick = Time.tickCounter();
            this.CTL = (byte) ((this.GearControl <= 0.5F ? 0 : 1) | (this.FlapsControl <= 0.2F ? 0 : 2) | (this.BrakeControl <= 0.2F ? 0 : 4) | (this.RadiatorControl <= 0.5F ? 0 : 8) | (this.BayDoorControl <= 0.5F ? 0 : 16)
                    | (this.AirBrakeControl <= 0.5F ? 0 : 32));
            this.WCT &= 0xfc;
            this.TWCT &= 0xfc;

            int wctIndex = 0;
            for (int wctBitMask = 1; wctIndex < this.WeaponControl.length && wctBitMask < 256; wctBitMask <<= 1) {
                if (this.WeaponControl[wctIndex]) {
                    // TODO: Storebror: +++ Bomb Release Bug hunting
                    if ((wctIndex == 2 || wctIndex == 3) && this.FM.actor instanceof NetAircraft && this.hasBulletsLeftOnTrigger(wctIndex)) {
                        NetAircraft netaircraft = (NetAircraft) this.FM.actor;

                        // The following Trigger Checks only apply to the sender aka the "Master" of Net Replication.
                        // Receivers aka "Mirrors" just process the Trigger messages as they are received instead.
                        if (netaircraft.isNetMaster()) {
                            NetAircraft.printDebugMessage(netaircraft, "Controls update() NetMaster " + NetAircraft.TRIGGER_NAMES[wctIndex - 2] + " Trigger pressed!");

                            // If this plane is an AI plane controlled by the Server, make sure it doesn't release bombs faster than they could be replicated across the net!
                            if (!(this.FM instanceof RealFlightModel)) if (!this.isReleaseReady(wctIndex - 2)) { // Trigger has been pressed too quick again!
                                NetAircraft.printDebugMessage(this.FM.actor, "Controls " + NetAircraft.TRIGGER_NAMES[wctIndex - 2] + " Trigger forcibly released, current Tick is " + Time.tickCounter() + ", next possible Trigger is at Tick No."
                                        + this.getNextReleaseReady(wctIndex - 2) + " !");
                                this.WeaponControl[wctIndex] = false; // Unset Trigger
                                // Clear Trigger from Weapon Control Bitmask used for Net Replication.
                                // This is just to be on the safe side, it should not be set at this point anyway.
                                this.WCT &= ~wctBitMask;
                                // Clear Trigger from Weapon Control Bitmask used for Track Recording.
                                // This is just to be on the safe side, it should not be set at this point anyway.
                                this.TWCT &= ~wctBitMask;
                                continue; // Skip further processing of this Trigger.
                            }
                            netaircraft.incUpdatePending(NetAircraft.UPDATE_MASTER);
                        }
                    }
                    // TODO: Storebror: --- Bomb Release Bug hunting
                    this.WCT |= wctBitMask;
                    this.TWCT |= wctBitMask;
                }
                wctIndex++;
            }
            for (wctIndex = 0; wctIndex < 4; wctIndex++)
                this.saveWeaponControl[wctIndex] = this.WeaponControl[wctIndex];

            // +++++ TODO skylla: enhanced weapon release control +++++
            for (wctIndex = 0; wctIndex < this.Weapons.length; wctIndex++)
                if (this.Weapons[wctIndex] != null) switch (wctIndex) {
                    case 2: {
                        long delay = this.rocketReleaseDelay;
                        delay = (long) (delay / Time.speed());
                        if ((this.lastRocketTime + delay < System.currentTimeMillis() || this.rocketFireMode == defaultFire) && this.WeaponControl[wctIndex] && this.hasBulletsLeftOnTrigger(wctIndex)) {
                            NetAircraft.printDebugMessage(this.FM.actor, "Controls Weapon Trigger " + wctIndex + " pressed!");
                            if (this.bDropWithPlayer) {
                                this.bDropWithMe = true;
                                this.bDropWithPlayer = false;
                            }
                            boolean weaponReleasedL = this.doNextRocketRelease(0);
                            boolean weaponReleasedR = this.doNextRocketRelease(1);
                            this.toggleRocketSide();
                            this.lastRocketTime = System.currentTimeMillis();
                            if (this.rocketFireMode == defaultFire) this.WeaponControl[wctIndex] = false;
                            if (!weaponReleasedL && !weaponReleasedR) NetAircraft.printDebugMessage(this.FM.actor, "Controls Weapon Trigger " + wctIndex + " pressed but no weapon released!");
                            else /*
                                  * this is here in order to circumvent a bug that will occur when using singleFire and only one type of rocket is selected (Type 1). If the rocket of Type 1 runs out of ammo, we must set WeaponControl[2] to false as we do
                                  * not want the player to waste any rockets of Type 2 (as they're not selected they can't be fired!). If the player fires the rockets with continuous fire - that means without releasing the trigger - then this works fine,
                                  * but if the player does press the trigger button again for every rocket there will be one press that shows no effect at all after firing the last rocket of Type 1. As we must suggest that player wants to fire the next
                                  * rocket, this is a no-go.
                                  */
                                this.hasLetGoRTriggerMeanwhile = false;
                        }
                        break;
                    }
                    case 3: {
                        long delay = this.bombReleaseDelay;
                        delay = (long) (delay / Time.speed());
                        if (this.lastBombTime + delay < System.currentTimeMillis() || this.bombDropMode == defaultFire) // TODO: Storebror: +++ Bomb Release Bug hunting
//                            	if (this.WeaponControl[wctIndex]) {
                            if ((this.WeaponControl[wctIndex] || this.bombDropMode > 1 && (this.bombDropMode > this.bombsDropped || this.bombDropMode == this.bombSalvoSizeOptions.length - 2) && this.isGroupRelease || this.bombDropMode == fullSalvo && this.isGroupRelease) && this.hasBulletsLeftOnTrigger(wctIndex)) {
                                // boolean weaponReleased = false;
                                NetAircraft.printDebugMessage(this.FM.actor, "Controls Weapon Trigger " + wctIndex + " pressed!");
                                // TODO: Storebror: --- Bomb Release Bug hunting

                                // TODO: +++ TD AI code backport from 4.13 +++
                                if (this.bDropWithPlayer) {
                                    this.bDropWithMe = true;
                                    this.bDropWithPlayer = false;
                                }
                                // TODO: --- TD AI code backport from 4.13 ---
                                // TODO: Added by |ZUTI|
                                // ----------------------------------------------------------------
                                Aircraft ac = (Aircraft) this.FM.actor;
                                if (wctIndex == 3 && ac instanceof TypeBomber && this.zutiBombsightAutomationStatus && ZutiSupportMethods_Multicrew.mustSyncACOperation((Aircraft) this.FM.actor)) {
                                    // Can we drop bombs at all? If not, send data to the one that can!
                                    // This check is put here also because us gunsight automation.
                                    ZutiSupportMethods_NetSend.bombardierReleasedOrdinance_ToServer(ac.name(), true, this.FM.CT.bHasBayDoors);
                                    // TODO: Storebror: +++ Bomb Release Bug hunting
                                    NetAircraft.printDebugMessage(this.FM.actor, "Multicrew.mustSyncACOperation == true ! Skipping Bomb Release...");
                                    // TODO: Storebror: --- Bomb Release Bug hunting
                                    return;
                                }
                                // ----------------------------------------------------------------
                                boolean weaponReleasedL = this.doNextBombRelease(0);
                                boolean weaponReleasedR = this.doNextBombRelease(1);
                                this.toggleBombSide();
                                this.lastBombTime = System.currentTimeMillis();
                                // System.out.println("SKYLLA: weaponReleasedL=" + weaponReleasedL + "; weaponReleasedR=" + weaponReleasedR + "; isGroupRelease=" + isGroupRelease + "; bombsDropped=" + bombsDropped + "; bombReleaseDelay=" +
                                // bombReleaseDelay);
                                if (this.bombDropMode >= singleFire) {
                                    if (this.bombsDropped == 0 && (weaponReleasedR || weaponReleasedL)) this.isGroupRelease = true;
                                    this.bombsDropped += (weaponReleasedL ? 1 : 0) + (weaponReleasedR ? 1 : 0);
                                } else if (this.bombDropMode == defaultFire) this.WeaponControl[wctIndex] = false;
                                else if (this.bombDropMode == fullSalvo && this.hasBulletsLeftOnTrigger(wctIndex) && (weaponReleasedR || weaponReleasedL)) {
                                    // System.out.println("SKYLLA: Full Salvo active, but we didn't release all bombs yet. Retry in next update call!");
                                    this.bombReleaseDelay = 33L;
                                    this.isGroupRelease = true;
                                }
                                if (this.isGroupRelease && (!this.hasBulletsLeftOnTrigger(wctIndex) || this.bombDropMode >= singleFire && this.bombsDropped >= this.bombDropMode && this.bombDropMode != this.bombSalvoSizeOptions.length - 2)) {
                                    this.resetGroupDrop();
                                    int bombs = this.countBombsAvailable(this.selectedBomb);
                                    if (bombs == 0) {
                                        this.toggleBombSelected();
                                        this.bombDropMode = defaultFire;
                                    } else if (this.bombDropMode > bombs) this.bombDropMode = bombs;
                                    if (ac instanceof TypeBomber && this.zutiBombsightAutomationStatus) ((TypeBomber) ac).typeBomberToggleAutomation();
                                    this.WeaponControl[wctIndex] = false;
                                }
                                if (!weaponReleasedL && !weaponReleasedR) NetAircraft.printDebugMessage(this.FM.actor, "Controls Weapon Trigger " + wctIndex + " pressed but no weapon released!");
                                else this.hasLetGoBTriggerMeanwhile = false;
                            }
                        break;
                    }
                    default:
                        boolean flag2 = false;
                        for (int i2 = 0; i2 < this.Weapons[wctIndex].length; i2++) {
                            this.Weapons[wctIndex][i2].shots(this.WeaponControl[wctIndex] ? -1 : 0);
                            flag2 = flag2 || this.Weapons[wctIndex][i2].haveBullets();
                        }

                        if (this.WeaponControl[wctIndex] && !flag2 && this.FM.isPlayers()) com.maddox.il2.objects.effects.ForceFeedback.fxTriggerShake(wctIndex, false);
                        break;
                }
        }
//        // TODO: Storebror: +++ Bomb Release Bug hunting
//        else {
//            if (this.WeaponControl[2])
//                NetAircraft.printDebugMessage(this.FM.actor, "CONTROLS: Rocket Trigger pressed, but this.tick == Time.tickCounter()");
//            else if (this.WeaponControl[3])
//                NetAircraft.printDebugMessage(this.FM.actor, "CONTROLS: Bomb Trigger pressed, but this.tick == Time.tickCounter()");
//            else return;
//            if (Time.currentReal() > lastTimeControlTickSkip + 5000L) {
//                Exception e = new Exception("CONTROLS: Trigger pressed, but this.tick == Time.tickCounter()");
//                e.printStackTrace();
//                System.out.println("### PREVIOUS Update's Stack Trace:");
//                System.out.println(lastTimeControlTickSkipStackTrace);
//                lastTimeControlTickSkip = Time.currentReal();
//            }
//        }
//        // TODO: Storebror: --- Bomb Release Bug hunting

        // +++++ TODO skylla: enhanced weapon release control +++++
        if (!this.WeaponControl[2] && !this.hasLetGoRTriggerMeanwhile) this.hasLetGoRTriggerMeanwhile = true;
        if (!this.WeaponControl[3] && !this.hasLetGoBTriggerMeanwhile) this.hasLetGoBTriggerMeanwhile = true;

        /*
         * if(!this.hasBulletsLeftOnTrigger(2) && WeaponControl[2]) { WeaponControl[2] = false; } if(!this.hasBulletsLeftOnTrigger(3) && WeaponControl[3]) { WeaponControl[3] = false; isGroupRelease = false; }
         */
        // ----- todo skylla: enhanced weapon release control -----
    }
//    // TODO: Storebror: +++ Bomb Release Bug hunting
//    private static long lastTimeControlTickSkip = 0L;
//    private static String lastTimeControlTickSkipStackTrace="";
//    // TODO: Storebror: --- Bomb Release Bug hunting

    public boolean dropExternalStores(boolean flag) {
        boolean flag1 = ((com.maddox.il2.objects.air.Aircraft) this.FM.actor).dropExternalStores(flag);
        if (flag1) {
            this.FM.AS.externalStoresDropped = true;
            ((com.maddox.il2.objects.air.Aircraft) this.FM.actor).replicateDropExternalStores();
        }
        return flag1;
    }

    public float getWeaponMass() {
        int numTriggers = this.Weapons.length;
        float totalMass = 0.0F;
        for (int trigger = 0; trigger < numTriggers; trigger++)
            if (this.Weapons[trigger] != null) {
                int slotLen = this.Weapons[trigger].length;
                for (int slotIndex = 0; slotIndex < slotLen; slotIndex++) {
                    BulletEmitter bulletemitter = this.Weapons[trigger][slotIndex];
                    if (bulletemitter != null && !(bulletemitter instanceof FuelTankGun)) {
                        int numBullets = bulletemitter.countBullets();
                        if (numBullets < 0) {
                            numBullets = 1;
                            if (bulletemitter instanceof BombGun && ((BombGun) bulletemitter).isCassette()) numBullets = 10;
                        }
                        totalMass += bulletemitter.bulletMassa() * numBullets;
                    }
                }
            }
        return totalMass;
    }

    // +++ TODO: By SAS~Storebror: When deciding whether or not to attack as a fighter, take bombs/torps into account only!
    public float getBombMass() {
        int numTriggers = this.Weapons.length;
        float totalMass = 0.0F;
        for (int trigger = 0; trigger < numTriggers; trigger++)
            if (this.Weapons[trigger] != null) {
                int slotLen = this.Weapons[trigger].length;
                for (int slotIndex = 0; slotIndex < slotLen; slotIndex++) {
                    BulletEmitter bulletemitter = this.Weapons[trigger][slotIndex];
                    if (bulletemitter != null && ((bulletemitter instanceof BombGun) || (bulletemitter instanceof TorpedoGun)) && !(bulletemitter instanceof FuelTankGun) && !(bulletemitter instanceof RocketGun) && !(bulletemitter instanceof MissileGun)) {
                        int numBullets = bulletemitter.countBullets();
                        if (numBullets < 0) {
                            numBullets = 1;
                            if (bulletemitter instanceof BombGun && ((BombGun) bulletemitter).isCassette()) numBullets = 10;
                        }
                        totalMass += bulletemitter.bulletMassa() * numBullets;
//                        if (this.FM.actor instanceof Mig_17) System.out.println("bulletemitter=" + bulletemitter.getClass().getName() + ", bulletMassa=" + bulletemitter.bulletMassa() + ", numBullets=" + numBullets);
                    }
                }
            }
        return totalMass;
    }
    // ---
    
    // TODO: +++ By SAS~Storebror: Additional Checks for X-4 Launch
    public boolean hasX4() {
        if (!(this.FM.actor instanceof TypeX4Carrier)) return false;
        int numTriggers = this.Weapons.length;
        for (int trigger = 0; trigger < numTriggers; trigger++)
            if (this.Weapons[trigger] != null) {
                int slotLen = this.Weapons[trigger].length;
                for (int slotIndex = 0; slotIndex < slotLen; slotIndex++) {
                    BulletEmitter bulletemitter = this.Weapons[trigger][slotIndex];
                    if (bulletemitter != null && ((bulletemitter instanceof RocketGunX4) || (bulletemitter instanceof RocketGunX4homing) || (bulletemitter instanceof RocketGunX4R))) {
                        int numBullets = bulletemitter.countBullets();
                        if (numBullets != 0) return true;
                    }
                }
            }
        return false;
    }
    
    private void updateX4() {
        if (!(this.FM.actor instanceof TypeX4Carrier)) return;
        if (!this.FM.isTick(44, 0)) return;
        if (!this.FM.AS.isMaster()) return;
        if (this.FM instanceof RealFlightModel && ((RealFlightModel)this.FM).isRealMode()) return;
        if (!(this.FM instanceof Pilot)) return;
        
          Pilot pilot = (Pilot)this.FM;
          if ((pilot.get_maneuver() == 63) && (pilot.target != null))
          {
            Point3d targetLoc = new Point3d(pilot.target.Loc);
            targetLoc.sub(this.FM.Loc);
            this.FM.Or.transformInv(targetLoc);
            if (((targetLoc.x > 4000.0D) && (targetLoc.x < 5500.0D)) || ((targetLoc.x > 100.0D) && (targetLoc.x < 5000.0D) && (World.Rnd().nextFloat() < 0.33F) && (Time.current() > this.tX4Prev + 10000L)))
            {
              this.bToFire = true;
              this.tX4Prev = Time.current();
            }
          }
    }
    
    public boolean getbToFire() {
        return this.bToFire;
    }
    
    public void setbToFire(boolean value) {
        this.bToFire = value;
    }
    
    public long gettX4Prev() {
        return this.tX4Prev;
    }
    
    public void settX4Prev(long value) {
        this.tX4Prev = value;
    }
    // ---

    public int getWeaponCount(int i) {
        if (i >= this.Weapons.length || this.Weapons[i] == null) return 0;
        int i_29_ = this.Weapons[i].length;
        int i_31_;
        int i_30_ = i_31_ = 0;
        for (/**/; i_31_ < i_29_; i_31_++) {
            BulletEmitter bulletemitter = this.Weapons[i][i_31_];
            if (bulletemitter != null && !(bulletemitter instanceof FuelTankGun)) i_30_ += bulletemitter.countBullets();
        }
        return i_30_;
    }

    public boolean dropFuelTanks() {
        boolean bool = false;
        for (int i = 0; i < this.Weapons.length; i++)
            if (this.Weapons[i] != null) for (int i_32_ = 0; i_32_ < this.Weapons[i].length; i_32_++)
                if (this.Weapons[i][i_32_] instanceof FuelTankGun && this.Weapons[i][i_32_].haveBullets()) {
                    this.Weapons[i][i_32_].shots(1);
                    bool = true;
                }
        if (bool) {
            ((Aircraft) this.FM.actor).replicateDropFuelTanks();
            this.FM.M.onFuelTanksChanged();
        }
        return bool;
    }

    public FuelTank[] getFuelTanks() {
        int i = 0;
        for (int i_33_ = 0; i_33_ < this.Weapons.length; i_33_++)
            if (this.Weapons[i_33_] != null) for (int i_34_ = 0; i_34_ < this.Weapons[i_33_].length; i_34_++)
                if (this.Weapons[i_33_][i_34_] instanceof FuelTankGun) i++;
        FuelTank[] fueltanks = new FuelTank[i];
        int i_35_;
        for (int i_36_ = i_35_ = 0; i_36_ < this.Weapons.length; i_36_++)
            if (this.Weapons[i_36_] != null) for (int i_37_ = 0; i_37_ < this.Weapons[i_36_].length; i_37_++)
                if (this.Weapons[i_36_][i_37_] instanceof FuelTankGun) fueltanks[i_35_++] = ((FuelTankGun) this.Weapons[i_36_][i_37_]).getFuelTank();
        return fueltanks;
    }

    public void resetControl(int i) {
        switch (i) {
            case 0:
                this.AileronControl = 0.0F;
                this.Ailerons = 0.0F;
                this.trimAileron = 0.0F;
                break;
            case 1:
                this.ElevatorControl = 0.0F;
                this.Elevators = 0.0F;
                this.trimElevator = 0.0F;
                break;
            case 2:
                this.RudderControl = 0.0F;
                this.Rudder = 0.0F;
                this.trimRudder = 0.0F;
                break;
        }
    }

    // TODO: |ZUTI| methods and variables
    // ------------------------------------------------------------
    public String   zutiOwnerAircraftName         = null;

    private boolean zutiBombsightAutomationStatus = false;

    /**
     * Sets bombsight automation status to on or off.
     *
     * @return
     */
    public void zutiSetBombsightAutomationEngaged(boolean value) {
        this.zutiBombsightAutomationStatus = value;
    }

    // TAK Methods
    // ------------------------------------------------------------
    public float  BrakeRightControl;
    public float  BrakeLeftControl;
    private float BrakeRight;
    private float BrakeLeft;

    public float getBrakeRight() {
        return this.BrakeRight;
    }

    public float getBrakeLeft() {
        return this.BrakeLeft;
    }
    // ------------------------------------------------------------

    // TODO: ++ Added Code for Net Replication ++
    // functions to apply settings off- and online follow here
    public boolean doSetRocketHook(int theRocketHook) {
        this.rocketHookSelected = theRocketHook;
        if (this.Weapons[this.rocketHookSelected] == null) return false;

        // TODO: ++ Changed Code to make Engine Mod independent of Guided Missiles Mod ++
        if (!GuidedMissileInterop.getGuidedMissileModExists()) return false;
        if (GuidedMissileInterop.getMissileGunClass().isInstance(this.Weapons[this.rocketHookSelected][0])) {
            if (this.FM.actor instanceof TypeGuidedMissileCarrier) {
                GuidedMissileUtils theGuidedMissileUtils = ((TypeGuidedMissileCarrier) this.FM.actor).getGuidedMissileUtils();
                theGuidedMissileUtils.changeMissileClass(((RocketGun) this.Weapons[this.rocketHookSelected][0]).bulletClass());
                this.rocketNameSelected = theGuidedMissileUtils.getMissileName();
            }
        } else
            // TODO: -- Added/changed Code Multiple Missile Type Selection --
            this.rocketNameSelected = "Rocket";

        // TODO: -- Changed Code to make Engine Mod independent of Guided Missiles Mod --
        return true;
    }

    public boolean toggleRocketHook() {
        int[] tryHooks = null;
        switch (this.rocketHookSelected) {
            case 2:
                tryHooks = new int[] { 4, 5, 6 };
                break;
            case 4:
                tryHooks = new int[] { 5, 6, 2 };
                break;
            case 5:
                tryHooks = new int[] { 6, 2, 4 };
                break;
            case 6:
                tryHooks = new int[] { 2, 4, 5 };
                break;
        }
        if (tryHooks != null) for (int i = 0; i < tryHooks.length; i++)
            if (this.Weapons[tryHooks[i]] != null) return this.doSetRocketHook(tryHooks[i]);
        return false;
    }

    // TODO: Storebror: +++ Bomb Release Bug hunting
    public boolean hasBulletsLeftOnTrigger(int Trigger) {
        BulletEmitter[] be = this.FM.CT.Weapons[Trigger];
        if (be == null) return false;
        for (int i = 0; i < be.length; i++)
            if (be[i] != null && be[i].haveBullets()) return true;
        return false;
    }

    public BulletEmitter firstGunOnTrigger(int Trigger) {
        BulletEmitter[] be = this.FM.CT.Weapons[Trigger];
        if (be == null) return null;
        for (int i = 0; i < be.length; i++)
            if (be[i] != null && be[i].haveBullets()) return be[i];
        return null;
    }

    private int[]            nextReleaseReady = { 0, 0 };
    private static final int RELEASE_INTERVAL = 3;

    public boolean isReleaseReady(int trigger) {
        if (!(this.FM.actor instanceof NetAircraft)) return true;
        NetAircraft netaircraft = (NetAircraft) this.FM.actor;
        if (!netaircraft.isNetMaster()) return true;
//        if (!this.hasBulletsLeftOnTrigger(trigger)) return false;
        return Time.tickCounter() >= this.getNextReleaseReady(trigger);
    }

    public boolean isRocketReleaseReady() {
        return this.isReleaseReady(0);
    }

    public boolean isBombReleaseReady() {
        return this.isReleaseReady(1);
    }

    public int getNextReleaseReady(int trigger) {
        return this.nextReleaseReady[trigger] + 1;
    }

    public int getNextRocketReleaseReady() {
        return this.getNextReleaseReady(0);
    }

    public int getNextBombReleaseReady() {
        return this.getNextReleaseReady(1);
    }

    public void setNextReleaseReady(int trigger) {
        this.nextReleaseReady[trigger] = Time.tickCounter() + RELEASE_INTERVAL + 1;
    }

    public void setNextRocketReleaseReady(int nextReleaseReady) {
        this.setNextReleaseReady(0);
    }

    public void setNextBombReleaseReady(int nextReleaseReady) {
        this.setNextReleaseReady(1);
    }
    // TODO: Storebror: --- Bomb Release Bug hunting

    // TODO: +++ TD AI code backport from 4.13 +++
    public boolean isShooting() {
        for (int i = 0; i < this.WeaponControl.length; i++)
            if (i != 3 && i != 20 && this.WeaponControl[i]) return true;

        return false;
    }

    public void setManualfterburnerControl(float f) {
        this.afterburnerFControl = f;
    }

    public float getManualAfterburnerControl() {
        return this.afterburnerFControl;
    }

    public float    afterburnerFControl;
    public boolean  bDropWithPlayer;
    public Aircraft dropWithPlayer;
    boolean         bDropWithMe;
    // TODO: --- TD AI code backport from 4.13 ---

    // +++++ TODO skylla: enhanced weapon release control +++++

    // +++ getter and setter methods for field index net replication +++
    public int getSelectedBombReleaseDelayIndex() {
        return Arrays.binarySearch(this.bombReleaseDelayOptions, this.bombReleaseDelay);
    }

    public void setBombReleaseDelayByIndex(int index) {
        if (index < 0 || index >= this.bombReleaseDelayOptions.length) {
            System.out.println(this.getClass() + ".setBombReleaseDelayByIndex() received an off value (" + index + ") and will ignore it. This may be responsible for weird occurances involving ordnance releases!");
            return;
        }
        this.bombReleaseDelay = this.bombReleaseDelayOptions[index];
    }

    public int getSelectedRocketReleaseDelayIndex() {
        return Arrays.binarySearch(this.rocketReleaseDelayOptions, this.rocketReleaseDelay);
    }

    public void setRocketReleaseDelayByIndex(int index) {
        if (index < 0 || index >= this.rocketReleaseDelayOptions.length) {
            System.out.println(this.getClass() + ".setRocketReleaseDelayByIndex() received an off value (" + index + ") and will ignore it. This may be responsible for weird occurances involving ordnance releases!");
            return;
        }
        this.rocketReleaseDelay = this.rocketReleaseDelayOptions[index];
    }

    public int getSelectedBombSalvoSizeIndex() {
        return Arrays.binarySearch(this.bombSalvoSizeOptions, this.bombDropMode);
    }

    public void setBombSalvoSizeByIndex(int index) {
        if (index >= this.bombSalvoSizeOptions.length || index < 0) {
            System.out.println(this.getClass() + ".setBombSalvoSizeByIndex() received an off value (" + index + ") and will ignore it. This may be responsible for weird occurances involving ordnance releases!");
            return;
        }
        this.bombDropMode = this.bombSalvoSizeOptions[index];
    }

    public int getSelectedRocketSalvoSizeIndex() {
        return Arrays.binarySearch(this.rocketSalvoSizeOptions, this.rocketFireMode);
    }

    public void setRocketSalvoSizeByIndex(int index) {
        if (index >= this.rocketSalvoSizeOptions.length || index < 0) {
            System.out.println(this.getClass() + ".setRocketSalvoSizeByIndex() received an off value (" + index + ") and will ignore it. This may be responsible for weird occurances involving ordnance releases!");
            return;
        }
        this.rocketFireMode = this.rocketSalvoSizeOptions[index];
    }

    public void setBombSelected(int index) {
        if (index < 0 || index >= this.availableBombs.size()) {
            System.out.println(this.getClass() + ".setRocketSelected() received an off value (" + index + ") and will ignore it. This may be responsible for weird occurances involving ordnance releases!");
            return;
        }
        Class tmp = (Class) this.availableBombs.get(index);
        if (this.selectedBomb == tmp) return;
//        System.out.println("SKYLLA: Apply selected Rocket");
        this.selectedBomb = tmp;
        this.checkSelectedBombAvailable();
    }

    public int getBombIndexSelected() {
        int i = this.availableBombs.indexOf(this.selectedBomb);
        if (i == -1) {
            this.checkSelectedBombAvailable();
            i = 0;
        }
        return i;
    }

    public void setRocketSelected(int index) {
        if (index < 0 || index >= this.availableRockets.size()) {
            System.out.println(this.getClass() + ".setRocketSelected() received an off value (" + index + ") and will ignore it. This may be responsible for weird occurances involving ordnance releases!");
            return;
        }
        Class tmp = (Class) this.availableRockets.get(index);
        if (this.selectedRocket == tmp) return;
//        System.out.println("SKYLLA: Apply selected Rocket");
        this.selectedRocket = tmp;
        this.checkSelectedRocketAvailable();
    }

    public int getRocketIndexSelected() {
        int i = this.availableRockets.indexOf(this.selectedRocket);
        if (i == -1) {
            this.checkSelectedRocketAvailable();
            i = 0;
        }
        return i;
    }
    // --- getter and setter methods for field index net replication ---

    // reset method to be used after executing RRR
    public void resetAvailableBombs() {
        this.WeaponControl[3] = false;
        this.isGroupRelease = false;

        this.availableBombs.clear();
        this.initiateEnhancedWeaponOptions();
        this.listBombs();
    }

    // reset method to be used after executing RRR
    public void resetAvailableRockets() {
        this.WeaponControl[2] = false;

        this.availableRockets.clear();
        this.initiateEnhancedWeaponOptions();
        this.listRockets();
    }

    /*
     * This method must be called before any other. It will sanitize input from possible TypeEnhancedWeaponOptionControl-Planes or initialize default values for weapon salvo size & release delay.
     */
    private void initiateEnhancedWeaponOptions() {
        Aircraft _ac = (Aircraft) this.FM.actor;
        int[] tmpBs = null;
        int[] tmpRs = null;
        long[] tmpBd = null;
        long[] tmpRd = null;
        if (_ac instanceof TypeEnhancedWeaponReleaseControl) {
            TypeEnhancedWeaponReleaseControl ac = (TypeEnhancedWeaponReleaseControl) _ac;
            tmpBs = ac.getPossibleBombSalvoSizeOptions();
            tmpRs = ac.getPossibleRocketSalvoSizeOptions();
            tmpBd = ac.getPossibleBombReleaseDelayOptions();
            tmpRd = ac.getPossibleRocketReleaseDelayOptions();
            if (tmpBs != null) Arrays.sort(tmpBs);
            if (tmpRs != null) Arrays.sort(tmpRs);
            if (tmpBd != null) Arrays.sort(tmpBd);
            if (tmpRd != null) Arrays.sort(tmpRd);
        }

        int validB = 0;
        int validR = 0;

        // not needed anymore, using static level to prevent RRR issues.
        // final int bombs = countBombsAvailable(BombGun.class);
        // System.out.println("SKYLLA: Number of bombs available on this sortie: " + bombs);

        if (tmpBs != null) {
            // System.out.println("SKYLLA: entered check clause for bomb salvo size options");
            boolean b = Arrays.binarySearch(tmpBs, -1) < 0;
            this.bombSalvoSizeOptions = new int[tmpBs.length];
            for (int i = 0; i < tmpBs.length; i++)
                if (tmpBs[i] >= -1 && tmpBs[i] <= bombGroupDropLimit) if (i - 1 > -1) {
                    if (tmpBs[i] > tmpBs[i - 1]) {
                        this.bombSalvoSizeOptions[i - (i - validB)] = tmpBs[i];
                        validB++;
                    }
                } else {
                    this.bombSalvoSizeOptions[i - (i - validB)] = tmpBs[i];
                    validB++;
                }
            if (validB > 0) {
                tmpBs = this.bombSalvoSizeOptions;
                if (b) {
                    this.bombSalvoSizeOptions = new int[validB + 1];
                    this.bombSalvoSizeOptions[0] = -1;
                    for (int i = 0; i < validB; i++)
                        this.bombSalvoSizeOptions[i + 1] = tmpBs[i];
                } else {
                    this.bombSalvoSizeOptions = new int[validB];
                    for (int i = 0; i < validB; i++)
                        this.bombSalvoSizeOptions[i] = tmpBs[i];
                }
            }
        }
        if (tmpBs == null || validB == 0) {
            this.bombSalvoSizeOptions = new int[bombGroupDropLimit];
            for (int i = 0; i < this.bombSalvoSizeOptions.length; i++)
                this.bombSalvoSizeOptions[i] = i - 1;
        }
        if (this.bombSalvoSizeOptions.length > 256) throw new IllegalArgumentException(
                this.getClass() + ".bombSalvoSizeOptions.length must not be greater than 256! In order to fix this error, please modify the field returned by " + _ac.getClass() + ".getPossibleBombSalvoSizeOptions() accordingly!");

        if (tmpRs != null) {
            boolean b = Arrays.binarySearch(tmpRs, -1) < 0;
            this.rocketSalvoSizeOptions = new int[b ? tmpRs.length + 1 : tmpRs.length];
            this.rocketSalvoSizeOptions[0] = -1;
            if (this.rocketSalvoSizeOptions.length > 2) {
                this.rocketSalvoSizeOptions[1] = 0;
                this.rocketSalvoSizeOptions[2] = 1;
                validR += 2;
            } else if (this.rocketSalvoSizeOptions.length == 2) {
                this.rocketSalvoSizeOptions[1] = Arrays.binarySearch(tmpRs, 0) >= 0 ? 0 : 1;
                validR++;
            }
        }
        if (tmpRs == null || validR == 0) {
            this.rocketSalvoSizeOptions = new int[3];
            for (int i = 0; i < 3; i++)
                this.rocketSalvoSizeOptions[i] = i - 1;
        }
        if (this.rocketSalvoSizeOptions.length > 256) throw new IllegalArgumentException(
                this.getClass() + ".rocketSalvoSizeOptions.length must not be greater than 256! In order to fix this error, please modify the field returned by " + _ac.getClass() + ".getPossibleRocketSalvoSizeOptions() accordingly!");

        validB = 0;
        if (tmpBd != null) {
            this.bombReleaseDelayOptions = new long[tmpBd.length];
            for (int i = 0; i < tmpBd.length; i++)
                if (tmpBd[i] > releaseDelayMIN && tmpBd[i] < releaseDelayMAX) if (i - 1 > -1) {
                    if (tmpBd[i] > tmpBd[i - 1]) {
                        this.bombReleaseDelayOptions[i - (i - validB)] = tmpBd[i];
                        validB++;
                    }
                } else {
                    this.bombReleaseDelayOptions[i - (i - validB)] = tmpBd[i];
                    validB++;
                }
            // I didn't know that Arrays.copy() requires at least Java 1.6 :(
            if (validB > 0) {
                tmpBd = this.bombReleaseDelayOptions;
                this.bombReleaseDelayOptions = new long[validB];
                for (int i = 0; i < validB; i++)
                    this.bombReleaseDelayOptions[i] = tmpBd[i];
            }
        }
        if (tmpBd == null || validB == 0) {
            this.bombReleaseDelayOptions = new long[6];
            this.bombReleaseDelayOptions[0] = 33L;
            this.bombReleaseDelayOptions[1] = 125L;
            this.bombReleaseDelayOptions[2] = 250L;
            this.bombReleaseDelayOptions[3] = 500L;
            this.bombReleaseDelayOptions[4] = 1000L;
            this.bombReleaseDelayOptions[5] = 2000L;
        }
        if (this.bombReleaseDelayOptions.length > 256) throw new IllegalArgumentException(
                this.getClass() + ".bombReleaseDelayOptions.length must not be greater than 256! In order to fix this error, please modify the field returned by " + _ac.getClass() + ".getPossibleBombReleaseDelayOptions() accordingly!");

        validR = 0;
        if (tmpRd != null) {
            this.rocketReleaseDelayOptions = new long[tmpRd.length];
            for (int i = 0; i < tmpRd.length; i++)
                if (tmpRd[i] > releaseDelayMIN && tmpRd[i] < releaseDelayMAX) if (i - 1 >= 0) {
                    if (tmpRd[i] > tmpRd[i - 1]) {
                        this.rocketReleaseDelayOptions[i - (i - validR)] = tmpRd[i];
                        validR++;
                    }
                } else {
                    this.rocketReleaseDelayOptions[i - (i - validR)] = tmpRd[i];
                    validR++;
                }
            // I didn't know that Arrays.copy() requires at least Java 1.6 :(
            if (validR > 0) {
                tmpRd = this.rocketReleaseDelayOptions;
                this.rocketReleaseDelayOptions = new long[validR];
                for (int i = 0; i < validR; i++)
                    this.rocketReleaseDelayOptions[i] = tmpRd[i];
            }
        }
        if (tmpRd == null || validR == 0) {
            this.rocketReleaseDelayOptions = new long[6];
            this.rocketReleaseDelayOptions[0] = 33L;
            this.rocketReleaseDelayOptions[1] = 125L;
            this.rocketReleaseDelayOptions[2] = 190L;
            this.rocketReleaseDelayOptions[3] = 250L;
            this.rocketReleaseDelayOptions[4] = 500L;
            this.rocketReleaseDelayOptions[5] = 1000L;
        }
        if (this.rocketReleaseDelayOptions.length > 256) throw new IllegalArgumentException(
                this.getClass() + ".rocketReleaseDelayOptions.length must not be greater than 256! In order to fix this error, please modify the field returned by " + _ac.getClass() + ".getPossibleRocketReleaseDelayOptions() accordingly!");
        this.listRockets();
        this.listBombs();
    }

    // to be used after all bombs of a group drop are released.
    private void resetGroupDrop() {
        this.bombsDropped = 0;
        this.isGroupRelease = false;
    }

    // checks if any ordnance is available
    private boolean ordnanceAvailable(Class ordnanceClass) {
        for (int i = 0; i < this.Weapons.length; i++) {
            if (this.Weapons[i] == null) continue;
            for (int j = 0; j < this.Weapons[i].length; j++)
                if (this.Weapons[i][j] != null) if (this.Weapons[i][j].getClass() == ordnanceClass && this.Weapons[i][j].haveBullets()) return true;
        }
        return false;
    }

    // counts all instances of BombGun, RocketGun and RocketBombGun on Trigger 3.
    private int countBombsAvailable(Class bombClass) {
        int num = 0;
        if (this.Weapons[3] == null) return num;
        for (int i = 0; i < this.Weapons[3].length; i++) {
            BulletEmitter e = this.Weapons[3][i];
            if (e == null) continue;
            else if ((e.getClass() == bombClass || bombClass == BombGun.class && (e instanceof BombGun || e instanceof RocketGun || e instanceof RocketBombGun) && !(e instanceof BombGunNull) && !(e instanceof RocketGunNull)) && e.countBullets() > 0) {
                if (e instanceof BombGun) {
                    if (((BombGun) e).isCassette()) {
                        num++;
                        continue;
                    }
                } else if (e instanceof RocketGun) {
                    if (((RocketGun) e).isCassette()) {
                        num++;
                        continue;
                    }
                } else if (e instanceof RocketBombGun) {

                }
                num += e.countBullets();
            }
        }
        return num;
    }

    private void checkSelectedBombAvailable() {
        if (this.selectedBomb == BombGun.class) return;
        if (!this.ordnanceAvailable(this.selectedBomb)) {
            if (this.availableBombs.indexOf(this.selectedBomb) > 0) this.availableBombs.remove(this.selectedBomb);
            this.selectedBomb = (Class) this.availableBombs.get(0);
            if (this.bombDropMode >= fullSalvo && !this.hasLetGoBTriggerMeanwhile) {
                this.WeaponControl[3] = false;
                this.isGroupRelease = false;
            }
        }
    }

    private void checkSelectedRocketAvailable() {
        if (this.selectedRocket == RocketGun.class) return;
        if (!this.ordnanceAvailable(this.selectedRocket)) {
            if (this.availableRockets.indexOf(this.selectedRocket) > 0) this.availableRockets.remove(this.selectedRocket);
            this.selectedRocket = (Class) this.availableRockets.get(0);
            if (this.rocketFireMode >= fullSalvo && !this.hasLetGoRTriggerMeanwhile) this.WeaponControl[2] = false;
        }
    }

    private void listBombs() {
        if (this.availableBombs == null) {

        } else if (this.availableBombs.size() > 0) return;
        this.availableBombs = new LinkedList();
        this.availableBombs.add(BombGun.class);
        if (this.Weapons[3] == null) return;
        for (int i = 0; i < this.Weapons[3].length; i++) {
            BulletEmitter e = this.Weapons[3][i];
            boolean isCandidate = (e instanceof BombGun || e instanceof RocketGun || e instanceof RocketBombGun) && !(e instanceof RocketGunNull || e instanceof BombGunNull) && e.haveBullets();
            if (isCandidate && !this.availableBombs.contains(e.getClass())) this.availableBombs.add(e.getClass());
        }
    }

    private void listRockets() {
        if (this.availableRockets == null) {

        } else if (this.availableRockets.size() > 0) return;
        this.availableRockets = new LinkedList();
        this.availableRockets.addFirst(RocketGun.class);
        if (this.Weapons[2] == null) return;
        for (int i = 0; i < this.Weapons[2].length; i++) {
            BulletEmitter e = this.Weapons[2][i];
            boolean isCandidate = e instanceof RocketGun && !(e instanceof RocketGunNull) && e.haveBullets();
            if (isCandidate && !this.availableRockets.contains(e.getClass())) this.availableRockets.add(e.getClass());
        }
    }

    // silent worker method; gets called within toggleBombtSelectedHUD()
    public void toggleBombSelected() {
        Aircraft ac = (Aircraft) this.FM.actor;
        if (ac instanceof TypeLimitedWeaponReleaseControl) if (!((TypeLimitedWeaponReleaseControl) ac).canSelectBomb()) return;
        this.listBombs();
        int i = this.availableBombs.indexOf(this.selectedBomb);
        if (i == -1) {
            this.selectedBomb = BombGun.class;
            this.toggleBombSelected();
            return;
        } else if (i + 1 >= this.availableBombs.size()) i = 0;
        else i++;
        this.selectedBomb = (Class) this.availableBombs.get(i);
        if (this.bombDropMode != defaultFire && this.availableBombs.size() > 2) {
            this.bombDropMode = defaultFire;
            this.resetGroupDrop();
        }
        this.checkSelectedBombAvailable();
    }

    // silent worker method; gets called within toggleRocketSelectedHUD()
    public void toggleRocketSelected() {
        Aircraft ac = (Aircraft) this.FM.actor;
        if (ac instanceof TypeLimitedWeaponReleaseControl) if (!((TypeLimitedWeaponReleaseControl) ac).canSelectRocket()) return;
        this.listRockets();
        int i = this.availableRockets.indexOf(this.selectedRocket);
        if (i == -1) {
            this.selectedRocket = RocketGun.class;
            this.toggleRocketSelected();
            return;
        } else if (i + 1 >= this.availableRockets.size()) // selectedRocket = (Class) ar.getFirst();
            i = 0;
        else i++;
        this.selectedRocket = (Class) this.availableRockets.get(i);
        this.checkSelectedRocketAvailable();
    }

    // called from AircraftHotKeys
    public void toggleBombSelectedHUD(int hudLogWeaponId) {
        this.toggleBombSelected();
        String name = this.selectedBomb.getName();
        if (name.endsWith("RocketGun") || name.endsWith("BombGun")) name = "Default / All";
        else if (name.startsWith("com.maddox.il2.objects.weapons.RocketGun") && name.length() > 40) name = name.substring(40);
        else if (name.startsWith("com.maddox.il2.objects.weapons.BombGun") && name.length() > 38) name = name.substring(38);
        if (this.availableBombs.size() > 2) HUD.log(hudLogWeaponId, "Bomb Selected: " + name);
    }

    // called from AircraftHotkeys
    public void toggleRocketSelectedHUD(int hudLogWeaponId) {
        this.toggleRocketSelected();
        String name = this.selectedRocket.getName();
        if (name.endsWith("RocketGun")) name = "Default / All";
        else if (name.startsWith("com.maddox.il2.objects.weapons.RocketGun") && name.length() > 40) name = name.substring(40);
        if (this.availableRockets.size() > 2) HUD.log(hudLogWeaponId, "Rocket Selected: " + name);
    }

    // must be called after each single side bomb drop
    public void toggleBombSide() {
        // don't toggle ordnance side when not needed, as that would f**** up one sided drops if seleced thereafter
        if (this.bombDropMode < singleFire /* || bombDropMode%2 == 0 */) return;
        this.bombDropLeft = !this.bombDropLeft;
    }

    // must be called after each single side rocket release
    public void toggleRocketSide() {
        if (this.rocketFireMode < singleFire) return;
        this.rocketShootLeft = !this.rocketShootLeft;
    }

    /*
     * setter method for external access (for instance: net replication): has to sanitize the input, but will falsify the input as little as possible
     */
    public void setBombDropMode(int theBombDropMode) {
        if (theBombDropMode < defaultFire) theBombDropMode = defaultFire;
        this.bombDropMode = theBombDropMode;
    }

    public int getBombDropMode() {
        return this.bombDropMode;
    }

    /*
     * setter method for external access (for instance: net replication): has to sanitize the input, but will falsify the input as little as possible
     */
    public void setRocketFireMode(int theRocketFireMode) {
        if (theRocketFireMode > singleFire || theRocketFireMode < defaultFire) theRocketFireMode = defaultFire;
        this.rocketFireMode = theRocketFireMode;
    }

    public int getRocketFireMode() {
        return this.rocketFireMode;
    }

    // gets called from AircraftHotKeys and produces HUD log output. Net Replication etc should use the setter!
    public void toggleBombDropMode(int hudLogWeaponId) {
        if (this.Weapons[3] == null) return;
        Aircraft ac = (Aircraft) this.FM.actor;
        if (ac instanceof TypeLimitedWeaponReleaseControl) if (!((TypeLimitedWeaponReleaseControl) ac).canSelectBombSalvoSize()) return;
//        for (int i=0; i<this.bombSalvoSizeOptions.length; i++) System.out.print(", " + this.bombSalvoSizeOptions[i]);
//        System.out.println();
//        int num = this.countBombsAvailable(this.selectedBomb);
//        if (this.bombDropMode == num) this.bombDropMode = this.bombSalvoSizeOptions.length - 2;
        switch (this.bombDropMode) {
            case defaultFire:
//                System.out.println("binarySearch 1 bombSalvoSizeOptions for " + singleFire + " returns " + Arrays.binarySearch(this.bombSalvoSizeOptions, singleFire));
                if (Arrays.binarySearch(this.bombSalvoSizeOptions, singleFire) >= 0) {
                    this.setBombDropMode(singleFire);
                    HUD.log(hudLogWeaponId, "Bombs: Single Drop Selected");
                    break;
                }
            default:
                int num = this.countBombsAvailable(this.selectedBomb);
                boolean isSet = false;
                /*
                 * works only if bombSalvoSizeOptions elements increment by one! Therefore remove; see new implementation below! if((bombDropMode+1) <= num && Arrays.binarySearch(bombSalvoSizeOptions, (bombDropMode+1)) >= 0) {
                 * setBombDropMode(bombDropMode+1); HUD.log(hudLogWeaponId, "Bomb Salvo Size: " + bombDropMode + (bombDropMode == num?" (All)":"")); isSet = true; } else if(Arrays.binarySearch(bombSalvoSizeOptions, fullSalvo) >= 0) {
                 * setBombDropMode(fullSalvo); HUD.log(hudLogWeaponId, "Bombs: Full Salvo Selected"); isSet = true; }
                 */
                int cIndex = Arrays.binarySearch(this.bombSalvoSizeOptions, this.bombDropMode);
//                System.out.println("binarySearch 2 bombSalvoSizeOptions for " + this.bombDropMode + " returns " + cIndex);
                if (cIndex + 1 < this.bombSalvoSizeOptions.length) if (this.bombSalvoSizeOptions[cIndex + 1] <= num) {
                    this.bombDropMode = this.bombSalvoSizeOptions[cIndex + 1];
                    if (this.bombDropMode == this.bombSalvoSizeOptions.length - 2) //this.bombDropMode = num; // FIXME: !!!!
                        HUD.log(hudLogWeaponId, "Bomb Salvo Size: " + num + " (All)");
                    else 
                        HUD.log(hudLogWeaponId, "Bomb Salvo Size: " + this.bombDropMode + (this.bombDropMode == num ? " (All)" : ""));
                    isSet = true;
                }
                if (!isSet && Arrays.binarySearch(this.bombSalvoSizeOptions, fullSalvo) >= 0) {
                    this.setBombDropMode(fullSalvo);
                    HUD.log(hudLogWeaponId, "Bombs: Full Salvo Selected");
                    isSet = true;
                }
                if (isSet) break;
            case fullSalvo:
                this.setBombDropMode(defaultFire);
                HUD.log(hudLogWeaponId, "Bombs: Default Drop Selected");
                break;
        }
    }

    // gets called from within AircraftHotKeys and produces HUD log output. Net Replication etc should use the regarding setter!
    public void toggleRocketFireMode(int hudLogWeaponId) {
        if (this.Weapons[2] == null) return;
        Aircraft ac = (Aircraft) this.FM.actor;
        if (ac instanceof TypeLimitedWeaponReleaseControl) if (!((TypeLimitedWeaponReleaseControl) ac).canSelectRocketSalvoSize()) return;
        switch (this.rocketFireMode) {
            case defaultFire:
                if (Arrays.binarySearch(this.rocketSalvoSizeOptions, singleFire) >= 0) {
                    this.setRocketFireMode(singleFire);
                    HUD.log(hudLogWeaponId, "Rockets: Single Fire Selected");
                    break;
                }
            default:
                if (Arrays.binarySearch(this.rocketSalvoSizeOptions, fullSalvo) >= 0) {
                    this.setRocketFireMode(fullSalvo);
                    HUD.log(hudLogWeaponId, "Rockets: Full Salvo Selected");
                    break;
                }
            case fullSalvo:
                this.setRocketFireMode(defaultFire);
                HUD.log(hudLogWeaponId, "Rockets: Default Fire Selected");
                break;
        }
    }

    /*
     * setter method for external access (for instance: net replication): has to sanitize the input, but will falsify the input as little as possible
     */
    public void setBombReleaseDelay(long delay) {
        if (delay < releaseDelayMIN) delay = releaseDelayMIN;
        else if (delay > releaseDelayMAX) delay = releaseDelayMAX;
        this.bombReleaseDelay = delay;
    }

    public long getBombReleaseDelay() {
        return this.bombReleaseDelay;
    }

    /*
     * setter method for external access (for instance: net replication): has to sanitize the input, but will falsify the input as little as possible
     */
    public void setRocketReleaseDelay(long delay) {
        if (delay < releaseDelayMIN) delay = releaseDelayMIN;
        else if (delay > releaseDelayMAX) delay = releaseDelayMAX;
        this.rocketReleaseDelay = delay;
    }

    public long getRocketReleaseDelay() {
        return this.rocketReleaseDelay;
    }

    // silent worker method
    public boolean toggleBombReleaseDelay() {
        Aircraft ac = (Aircraft) this.FM.actor;
        if (ac instanceof TypeLimitedWeaponReleaseControl) if (!((TypeLimitedWeaponReleaseControl) ac).canSelectBombReleaseDelay()) return false;
        if (this.bombReleaseDelay >= this.bombReleaseDelayOptions[this.bombReleaseDelayOptions.length - 1]) {
            this.bombReleaseDelay = this.bombReleaseDelayOptions[0];
            return true;
        }
        for (int i = 0; i < this.bombReleaseDelayOptions.length; i++)
            if (this.bombReleaseDelay < this.bombReleaseDelayOptions[i]) {
                this.bombReleaseDelay = this.bombReleaseDelayOptions[i];
                break;
            }
        return true;
    }

    // silent worker method
    public boolean toggleRocketReleaseDelay() {
        Aircraft ac = (Aircraft) this.FM.actor;
        if (ac instanceof TypeLimitedWeaponReleaseControl) if (!((TypeLimitedWeaponReleaseControl) ac).canSelectRocketReleaseDelay()) return false;
        if (this.rocketReleaseDelay >= this.rocketReleaseDelayOptions[this.rocketReleaseDelayOptions.length - 1]) {
            this.rocketReleaseDelay = this.rocketReleaseDelayOptions[0];
            return true;
        }
        for (int i = 0; i < this.rocketReleaseDelayOptions.length; i++)
            if (this.rocketReleaseDelay < this.rocketReleaseDelayOptions[i]) {
                this.rocketReleaseDelay = this.rocketReleaseDelayOptions[i];
                break;
            }
        return true;
    }

    // gets called from AircraftHotKeys
    public void toggleBombReleaseDelayHUD(int hudLogWeaponId) {
        if (this.Weapons[3] == null) return;
        if (this.toggleBombReleaseDelay()) HUD.log(hudLogWeaponId, "Bomb Release Delay: " + this.bombReleaseDelay / 1000F + " sec");
    }

    // gets called from AircraftHotKeys
    public void toggleRocketReleaseDelayHUD(int hudLogWeaponId) {
        if (this.Weapons[2] == null) return;
        if (this.toggleRocketReleaseDelay()) HUD.log(hudLogWeaponId, "Rocket Release Delay: " + this.rocketReleaseDelay / 1000F + " sec");
    }

    // main bomb release method.
    private boolean doNextBombRelease(int side) {
        boolean bombReleased = false;
        int shot;
        switch (side) {
            case 0:
                shot = this.bombDropLeft || this.bombDropMode < singleFire ? 1 : 0;
                break;
            case 1:
                shot = !this.bombDropLeft || this.bombDropMode < singleFire ? 1 : 0;
                break;
            default: {
                System.out.println(this.getClass() + ".doNextBombRelase(int side) received illegal Argument: " + side + ". Will now try to drop the next bomb in the queue.");
                return this.doNextBombRelease(this.bombDropLeft ? 0 : 1);
            }
        }
        // System.out.println("SKYLLA: doNextBombRelease(): Input Value=" + side + "; (bombDropMode < singleFire)=" + (bombDropMode < singleFire) + "; bombDropLeft=" + bombDropLeft + "; (bombDropMode%2 == 0)=" + (bombDropMode%2 == 0) + "; shot=" + shot);
        for (int i = side; i < this.Weapons[3].length; i += 2) {
            this.checkSelectedBombAvailable();
            if (!this.WeaponControl[3] && !this.isGroupRelease) break;
            BulletEmitter e = this.Weapons[3][i];
            if (e == null) continue;
            else if (!e.haveBullets()) continue;
            else if (this.bHasBayDoors && e.getHookName().startsWith("_BombSpawn") && this.BayDoorControl != 1.0F) {
                this.toggleBombSide();
                // continue in order to find a bomb that may not be within bomb bay and can be released.
                continue;
            } else if (e instanceof RocketGunNull || e instanceof BombGunNull) {
                if (e.isShots()) // System.out.println("SKYLLA: Entered isShots() clause for BombGunNull .. aborting release!");
                    if (this.bombDropMode != fullSalvo) continue;
                else return false;
                this.setNextReleaseReady(1);
                e.shots(1);
                // System.out.println("SKYLLA: Dropped BombGunNull / RocketGunNull!");
                ZutiSupportMethods_FM.executeOnbombDropped(this.zutiOwnerAircraftName, 3, i, 1);
                if (this.bombDropMode == singleFire) {
                    if (shot == 1) this.toggleBombSide();
                    bombReleased = this.doNextBombRelease(side == 0 ? 1 : 0);
                } else if (this.bombDropMode == fullSalvo) continue;
                break;
            } else if (!(e instanceof RocketGun) && !(e instanceof BombGun) && !(e instanceof RocketBombGun) || this.selectedBomb != BombGun.class && e.getClass() != this.selectedBomb) // System.out.println("SKYLLA: Skipping bomb release; index=" + i + ";
                                                                                                                                                                                         // drop candidate = " + e.getClass());
                continue;
            else if (shot == 1) {
                // System.out.println("SKYLLA: side=" + side + "; shot=" + shot);
                if (e.isShots()) // System.out.println("SKYLLA: Entered isShots() clause for valid bomb release .. aborting release!");
                    if (this.bombDropMode != fullSalvo) continue;
                else return false;
                this.setNextReleaseReady(1);
                bombReleased = true;
                e.shots(shot);
                ZutiSupportMethods_FM.executeOnbombDropped(this.zutiOwnerAircraftName, 3, i, 1);
                boolean bombbay = e.getHookName().startsWith("_BombSpawn");
                // System.out.println("SKYLLA: Released ordnance of type '" + e.getClass() + "' from " + (side==0?"left":"right") + " side" + (bombbay?" through bomb bay doors" : ""));
                if (bombbay && !this.bHasBayDoors) this.BayDoorControl = 1.0F;
                NetAircraft.printDebugMessage(this.FM.actor, "\"" + (side == 0 ? "Left" : "Right") + " " + (bombbay ? "In" : "Ex") + "ternal\" " + NetAircraft.TRIGGER_NAMES[3 - 2] + " from Emitter No. " + i + "= " + NetAircraft.simpleClassName(e)
                        + " release" + (bombbay ? " through open Baydoor" : "") + "!");
                if (this.bombDropMode != fullSalvo) break;
                else if (bombbay && i + 2 < this.Weapons[3].length) if (this.Weapons[3][i + 2].getHookName().startsWith("_BombSpawn")) break;
            }
            /*
             * if(i+2 >= Weapons[3].length && !bombReleased && selectedBomb != BombGun.class && shot == 1) { //checkSelectedBombAvailable(); i = side; }
             */
        }
        return bombReleased;
    }

    // main rocket release method
    private boolean doNextRocketRelease(int side) {
//		System.out.println("Controls 1 doNextRocketRelease(" + side + ") rocketFireMode=" + rocketFireMode);
        boolean rocketReleased = false;
        int shot;
        switch (side) {
            case 0:
                shot = this.rocketShootLeft || this.rocketFireMode < singleFire ? 1 : 0;
                break;
            case 1:
                shot = !this.rocketShootLeft || this.rocketFireMode < singleFire ? 1 : 0;
                break;
            default: {
                System.out.println(this.getClass() + ".doNextRocketRelase(int side) received illegal Argument: " + side + ". Will now try to fire the next rocket in the queue.");
                return this.doNextRocketRelease(this.rocketShootLeft ? 0 : 1);
            }
        }
        for (int i = side; i < this.Weapons[2].length; i += 2) {
            this.checkSelectedRocketAvailable();
            if (!this.WeaponControl[2]) break;
            BulletEmitter e = this.Weapons[2][i];
            if (e == null) continue;
            else if (!e.haveBullets() || this.bHasBayDoors && e.getHookName().startsWith("_BombSpawn") && this.BayDoorControl != 1.0F) continue;
            else if (e instanceof RocketGunNull || e instanceof BombGunNull) {
                this.setNextReleaseReady(0);
                e.shots(1);
                ZutiSupportMethods_FM.executeOnbombDropped(this.zutiOwnerAircraftName, 2, i, 1);
                // System.out.print("SKYLLA: fired a BombGunNull/RocketGunNull from " + ((side == 0)?"left ":"right ") + "side, weaponFireMode = " + weaponFireMode + " and will now ");
                if (this.rocketFireMode == singleFire) {
                    // System.out.print("fire a rocket from the other side and then ");
                    if (shot == 1) this.toggleRocketSide();
                    rocketReleased = this.doNextRocketRelease(side == 0 ? 1 : 0);
                } else if (this.rocketFireMode == fullSalvo) // System.out.println("continue!");
                    continue;
                break;
                // TODO: Fix by SAS~Storebror: "selectedRocket != RocketGun.class" isn't a valid inheritance check!
//			} else if(!(e instanceof RocketGun) || (selectedRocket != RocketGun.class && e.getClass() != selectedRocket)) {
            } else if (!(e instanceof RocketGun) || !RocketGun.class.isAssignableFrom(this.selectedRocket) && e.getClass() != this.selectedRocket) continue;
            else if (shot == 1) {
                if (e.isShots()) return false;
                this.setNextReleaseReady(0);
                rocketReleased = true;
                e.shots(shot);
                ZutiSupportMethods_FM.executeOnbombDropped(this.zutiOwnerAircraftName, 2, i, 1);
                boolean bombbay = e.getHookName().startsWith("_BombSpawn");
                if (bombbay && !this.bHasBayDoors) this.BayDoorControl = 1.0F;
                // more ternary operators .. hell yes!
                NetAircraft.printDebugMessage(this.FM.actor, "\"" + (side == 0 ? "Left" : "Right") + " " + (bombbay ? "In" : "Ex") + "ternal\" " + NetAircraft.TRIGGER_NAMES[2 - 2] + " from Emitter No. " + i + "= " + NetAircraft.simpleClassName(e)
                        + " release" + (bombbay ? " through open Baydoor" : "") + "!");
                if (this.rocketFireMode != fullSalvo) break;
            }
            // System.out.println("SKYLLA: did NOT break after firing "+ ((side == 0)?"left ":"right ") + " side " + e.getClass() + ", weaponFireMode = " + weaponFireMode);
            if (i + 2 >= this.Weapons[2].length) {
                // System.out.println("SKYLLA: depleted all rockets on the "+ ((side == 0)?"left":"right") + " side!");
            }
            /*
             * if(i+2 >= Weapons[2].length && !rocketReleased && selectedRocket != RocketGun.class && shot == 1) { //checkSelectedRocketAvailable(); i = side; }
             */
        }
        return rocketReleased;
    }

    // ----- todo skylla: enhanced weapon release control -----

}