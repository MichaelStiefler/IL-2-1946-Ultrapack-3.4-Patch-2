package com.maddox.il2.objects.air;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.Explosion;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.Gear;
import com.maddox.il2.fm.Motor;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.net.NetMissionTrack;
import com.maddox.il2.objects.weapons.BombGunNull;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.HotKeyCmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.sas1946.il2.util.Reflection;
import com.maddox.sas1946.il2.util.TrueRandom;
import com.maddox.util.HashMapExt;

public abstract class B_36X extends Scheme7 implements TypeBomber, TypeTransport {

    public B_36X() {
        this.steering = 0.0F;
        B_36X.bChangedPit = true;
        this.bToFire = false;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
        this.isGuidingBomb = false;
        this.bSightAutomation = false;
        this.bSightBombDump = false;
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
        this.fSightCurAltitude = 3000F;
        this.fSightCurSpeed = 200F;
        this.fSightCurReadyness = 0.0F;
        this.fSightSetForwardAngle = 0.0F;
        this.prevFuel = -1F;
        this.gearAngle = 0F;
        boolean flag = false;
        this.APmode1 = false;
        this.APmode2 = false;
        this.APmode3 = false;
        this.aiStartPropPitchSet = false;
        this.aiStartMixtureSet = false;
        do {
            for (int i = 0; i < this.rndgear.length; i++) {
                this.rndgear[i] = TrueRandom.nextFloat(0.0F, 0.2F);
            }

            flag = false;
            if (Math.abs(this.rndgear[0] - this.rndgear[1]) < 0.05F) {
                flag = true;
            }
            if (Math.abs(this.rndgear[0] - this.rndgear[2]) < 0.05F) {
                flag = true;
            }
            if (Math.abs(this.rndgear[1] - this.rndgear[2]) < 0.05F) {
                flag = true;
            }
        } while (flag);
        this.addCockpitHotkey();
        for (int i=0; i<4; i++) this.lastJetPowerConfirmed[i] = Time.current();
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
        this.FM.CT.bHasBayDoorControl = true;
        this.FM.CT.PowerControlArr = new float[10];
        this.FM.CT.StepControlArr = new float[10];
        this.propPos = new float[10];
        this.oldProp = new int[10];
        for (int i = 0; i < 10; i++) {
            this.FM.CT.PowerControlArr[i] = 0.0F;
            this.FM.CT.StepControlArr[i] = 1.0F;
            this.propPos[i] = TrueRandom.nextFloat(130F);
        }

        this.FM.AS.astateOilStates = new byte[10];
        this.FM.AS.astateEngineStates = new byte[10];
        this.FM.AS.astateSootStates = new byte[10];
        this.FM.AS.astateSootEffects = new Eff3DActor[10][2];
        this.FM.AS.astateCondensateEffects = new Eff3DActor[10];
        Reflection.setValue(this.FM.AS, "astateOilEffects", new Eff3DActor[10][2]);
        Reflection.setValue(this.FM.AS, "astateEngineEffects", new Eff3DActor[10][3]);
        Reflection.setValue(this.FM.AS, "astateEngineBurnLights", new LightPointActor[10]);
        this.FM.Gears.clpEngineEff = new Eff3DActor[10][2];
        this.FM.Sq.dragEngineCx = new float[10];
        this.isEquippedWithTurrets = this.hierMesh().chunkFindCheck("Turret8A_D0") > 0;
        if (this.isEquippedWithTurrets) { // Plane is equipped with remote controlled turrets, retract per default.
            this.animateTurret3(1.0F);
            this.animateTurret4(1.0F);
            this.animateTurret5(1.0F);
            this.animateTurret6(1.0F);
            this.animateTurret7(1.0F);
            this.animateTurret8(1.0F);
            this.FM.turret[2].obsDir = this.FM.turret[4].obsDir = this.FM.turret[6].obsDir = Turret.RIGHT;
            this.FM.turret[3].obsDir = this.FM.turret[5].obsDir = this.FM.turret[7].obsDir = Turret.LEFT;
            String[] doors = {"AD", "PD", "PI"};
            for (int doorsIndex = 0; doorsIndex < doors.length; doorsIndex++) {
                this.hierMesh().chunkVisible("Door_" + doors[doorsIndex] + "L", true);
                this.hierMesh().chunkVisible("Door_" + doors[doorsIndex] + "R", true);
                this.hierMesh().chunkVisible("Door_" + doors[doorsIndex] + "Lo", false);
                this.hierMesh().chunkVisible("Door_" + doors[doorsIndex] + "Ro", false);
            }
        }
        if (this.FM.turret.length > 0) this.FM.turret[0].obsDir = Turret.REAR;
        if (this.FM.turret.length > 1) this.FM.turret[1].obsDir = Turret.FRONT;

        this.lastTurretActivationRequested = Time.current() - B_36X.TURRET_ACTIVATED_DURATION - 1L;
        this.FM.CT.setStepControl(1.0F);
        this.initialMixtureSet = false;
        this.initialCompressorSettingsLeft = 10;
        for (int i=0; i<4; i++) this.lastJetPowerConfirmed[i] = Time.current();
        this.targetBrakePower = 0F;
//        this.FM.AS.astateEffectChunks = new String[38];
//        this.FM.AS.set(this, this.FM.AS.isMaster());
    }
    
    protected void nextDMGLevel(String s, int i, Actor actor) {
        super.nextDMGLevel(s, i, actor);
        if (this.FM.isPlayers()) {
            B_36X.bChangedPit = true;
        }
    }

    protected void nextCUTLevel(String s, int i, Actor actor) {
        super.nextCUTLevel(s, i, actor);
        if (this.FM.isPlayers()) {
            B_36X.bChangedPit = true;
        }
    }

    public boolean typeGuidedBombCisMasterAlive() {
        return this.isMasterAlive;
    }

    public void typeGuidedBombCsetMasterAlive(boolean flag) {
        this.isMasterAlive = flag;
    }

    public boolean typeGuidedBombCgetIsGuiding() {
        return this.isGuidingBomb;
    }

    public void typeGuidedBombCsetIsGuiding(boolean flag) {
        this.isGuidingBomb = flag;
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 0.002F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -0.002F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 0.002F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -0.002F;
    }

    public void typeX4CResetControls() {
        this.deltaAzimuth = this.deltaTangage = 0.0F;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    private static final float toMeters(float f) {
        return 0.3048F * f;
    }

    private static final float toMetersPerSecond(float f) {
        return 0.4470401F * f;
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
//        DecimalFormat df = new DecimalFormat("0.00");
//        System.out.println("hitBone(" + s + ", " + shot.chunkName + ", " + df.format(point3d.x) + ":" + df.format(point3d.y) + ":" + df.format(point3d.z) + ")");
//        if (true) return;
        if (s.toLowerCase().startsWith("xx")) {
            if (s.toLowerCase().startsWith("xxammo0")) {
                int i = s.charAt(7) - 48;
                if ((this.getEnergyPastArmor(0.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.15F)) {
                    switch (i) {
                        case 3:
                        default:
                            break;

                        case 1:
                            if (World.Rnd().nextFloat() < 0.347F) {
                                this.FM.AS.setJamBullets(12, 0);
                            }
                            if (World.Rnd().nextFloat() < 0.347F) {
                                this.FM.AS.setJamBullets(12, 1);
                            }
                            break;

                        case 2:
                            if (World.Rnd().nextFloat() < 0.347F) {
                                this.FM.AS.setJamBullets(13, 0);
                            }
                            if (World.Rnd().nextFloat() < 0.347F) {
                                this.FM.AS.setJamBullets(13, 1);
                            }
                            break;

                        case 4:
                            if (World.Rnd().nextFloat() < 0.223F) {
                                this.FM.AS.setJamBullets(10, 0);
                            }
                            if (World.Rnd().nextFloat() < 0.223F) {
                                this.FM.AS.setJamBullets(10, 1);
                            }
                            if (World.Rnd().nextFloat() < 0.223F) {
                                this.FM.AS.setJamBullets(10, 2);
                            }
                            if (World.Rnd().nextFloat() < 0.223F) {
                                this.FM.AS.setJamBullets(10, 3);
                            }
                            break;

                        case 5:
                            if (World.Rnd().nextFloat() < 0.347F) {
                                this.FM.AS.setJamBullets(11, 0);
                            }
                            if (World.Rnd().nextFloat() < 0.347F) {
                                this.FM.AS.setJamBullets(11, 1);
                            }
                            break;
                    }
                }
                return;
            }
            if (s.toLowerCase().startsWith("xxcontrols")) {
                int j = s.charAt(10) - 48;
                switch (j) {
                    default:
                        break;

                    case 1:
                    case 2:
                    case 3:
                        if ((this.getEnergyPastArmor(1.0F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Aileron Controls Out..");
                        }
                        break;

                    case 4:
                        if ((this.getEnergyPastArmor(1.0F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                        }
                        break;

                    case 5:
                    case 6:
                        if ((this.getEnergyPastArmor(1.0F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                        }
                        break;
                }
                return;
            }
            if (s.toLowerCase().startsWith("xxcockpit")) {
                if (World.Rnd().nextFloat() < 0.1F) {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
                }
                if (point3d.x > 4.505D) {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                } else if (point3d.y > 0.0D) {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                    if (World.Rnd().nextFloat() < 0.1F) {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                    }
                } else {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                    if (World.Rnd().nextFloat() < 0.1F) {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                    }
                }
            } else if (s.toLowerCase().startsWith("xxeng")) {
                int k = s.charAt(5) - 49;
                if (k == 0 && s.charAt(6) == '0') k = 9;
//                System.out.println("### Part of Engine No. " + (k+1) + " got hit! ###");
                if (s.toLowerCase().endsWith("case")) {
                    if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < (shot.power / 140000F)) {
                            this.FM.AS.setEngineStuck(shot.initiator, k);
                            Aircraft.debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < (shot.power / 85000F)) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            Aircraft.debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Engine Damaged..");
                        }
                    } else if (World.Rnd().nextFloat() < 0.005F) {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, 1);
                    } else {
                        this.FM.EI.engines[k].setReadyness(shot.initiator, this.FM.EI.engines[k].getReadyness() - 0.00082F);
                        Aircraft.debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[k].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(12F, shot);
                }
                if (s.toLowerCase().endsWith("cyls")) {
                    if ((this.getEnergyPastArmor(5.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[k].getCylindersRatio() * 0.75F))) {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                        Aircraft.debugprintln(this, "*** Engine (" + k + ") Cylinders Hit, " + this.FM.EI.engines[k].getCylindersOperable() + "/" + this.FM.EI.engines[k].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < (shot.power / 18000F)) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            Aircraft.debugprintln(this, "*** Engine (" + k + ") Cylinders Hit - Engine Fires..");
                        }
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.toLowerCase().endsWith("mag1")) {
                    this.FM.EI.engines[k].setMagnetoKnockOut(shot.initiator, 0);
                    Aircraft.debugprintln(this, "*** Engine (" + k + ") Module: Magneto #0 Destroyed..");
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.toLowerCase().endsWith("prop") && (this.getEnergyPastArmor(0.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                    if (World.Rnd().nextFloat() < 0.5F) {
                        this.FM.EI.engines[k].setKillPropAngleDevice(shot.initiator);
                    } else {
                        this.FM.EI.engines[k].setKillPropAngleDeviceSpeeds(shot.initiator);
                    }
                    this.getEnergyPastArmor(15.1F, shot);
                    Aircraft.debugprintln(this, "*** Engine (" + k + ") Module: Prop Governor Fails..");
                }
                if (s.toLowerCase().endsWith("oil") && (this.getEnergyPastArmor(0.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                    this.FM.AS.setOilState(shot.initiator, k, 1);
                    Aircraft.debugprintln(this, "*** Engine (" + k + ") Module: Oil Filter Pierced..");
                }
                return;
            }
            if (s.toLowerCase().startsWith("xxspar")) {
                if (s.toLowerCase().startsWith("xxspare1") && (this.chunkDamageVisible("Engine1") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** Engine1 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine1_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxspare2") && (this.chunkDamageVisible("Engine2") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** Engine2 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine2_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxspare3") && (this.chunkDamageVisible("Engine3") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** Engine3 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine3_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxspare4") && (this.chunkDamageVisible("Engine4") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** Engine4 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine4_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparlm") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparrm") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparlo") && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparro") && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    Aircraft.debugprintln(this, "*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxspark") && (this.chunkDamageVisible("Keel1") > 1) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {
                    Aircraft.debugprintln(this, "*** Keel1 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D" + this.chunkDamageVisible("Keel1"), shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparsl") && (this.chunkDamageVisible("StabL") > 2) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {
                    Aircraft.debugprintln(this, "*** StabL: Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D" + this.chunkDamageVisible("StabL"), shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxsparsr") && (this.chunkDamageVisible("StabR") > 2) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {
                    Aircraft.debugprintln(this, "*** StabR: Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabR_D" + this.chunkDamageVisible("StabR"), shot.initiator);
                }
                if (s.toLowerCase().startsWith("xxspart") && (this.chunkDamageVisible("Tail1") > 2)) {
                    if (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F) {

                    }
                }
                return;
            }
            if (s.toLowerCase().startsWith("xxtank")) {
//                System.out.println("powerType=" + shot.powerType + ", power=" + shot.power);
                int l = s.charAt(6) - 49;
//                l /= 2;
                if (this.getEnergyPastArmor(0.06F, shot) > 0.0F) {
                    if (this.FM.AS.astateTankStates[l] == 0) {
                        this.FM.AS.hitTank(shot.initiator, l, 1);
                        this.FM.AS.doSetTankState(shot.initiator, l, 1);
                    }
                    if (shot.powerType == 3) {
                        if (shot.power < 16100F) {
                            if ((this.FM.AS.astateTankStates[l] < 4) && (World.Rnd().nextFloat() < 0.21F)) {
                                this.FM.AS.hitTank(shot.initiator, l, 1);
                            }
                        } else {
                            this.FM.AS.hitTank(shot.initiator, l, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100F)));
                        }
                    } else if (shot.power > 16100F) {
                        this.FM.AS.hitTank(shot.initiator, l, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100F)));
                    }
                }
                return;
            } else {
                return;
            }
        }
        if (s.toLowerCase().startsWith("xcf")) {
            if (this.chunkDamageVisible("CF") < 3) {
                this.hitChunk("CF", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) {
                this.hitChunk("Tail1", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xkeel")) {
            if (this.chunkDamageVisible("Keel1") < 2) {
                this.hitChunk("Keel1", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xrudder")) {
            if (this.chunkDamageVisible("Rudder1") < 2) {
                this.hitChunk("Rudder1", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xstabl")) {
            if (this.chunkDamageVisible("StabL") < 2) {
                this.hitChunk("StabL", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xstabr")) {
            if (this.chunkDamageVisible("StabR") < 2) {
                this.hitChunk("StabR", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xvatorl")) {
            if (this.chunkDamageVisible("VatorL") < 2) {
                this.hitChunk("VatorL", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xvatorr")) {
            if (this.chunkDamageVisible("VatorR") < 2) {
                this.hitChunk("VatorR", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwinglin")) {
            if (this.chunkDamageVisible("WingLIn") < 3) {
                this.hitChunk("WingLIn", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwingrin")) {
            if (this.chunkDamageVisible("WingRIn") < 3) {
                this.hitChunk("WingRIn", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwinglmid")) {
            if (this.chunkDamageVisible("WingLMid") < 3) {
                this.hitChunk("WingLMid", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwinglnacelles")) {
            if (this.chunkDamageVisible("WingLNacelles") < 3) {
                this.hitChunk("WingLNacelles", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwingrmid")) {
            if (this.chunkDamageVisible("WingRMid") < 3) {
                this.hitChunk("WingRMid", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwingrnacelles")) {
            if (this.chunkDamageVisible("WingRNacelles") < 3) {
                this.hitChunk("WingRNacelles", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwinglout")) {
            if (this.chunkDamageVisible("WingLOut") < 3) {
                this.hitChunk("WingLOut", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xwingrout")) {
            if (this.chunkDamageVisible("WingROut") < 3) {
                this.hitChunk("WingROut", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xaronel")) {
            if (this.chunkDamageVisible("AroneL") < 2) {
                this.hitChunk("AroneL", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xaroner")) {
            if (this.chunkDamageVisible("AroneR") < 2) {
                this.hitChunk("AroneR", shot);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xengine")) {
            int engineNumber = s.charAt(7) - 48;
            if (engineNumber == 1 && s.charAt(8) == '0') engineNumber = 10;

            if (this.chunkDamageVisible("Engine" + engineNumber) < 2) {
                this.hitChunk("Engine" + engineNumber, shot);
            }
            return;
        }

//        if (s.startsWith("xengine1")) {
//            if (this.chunkDamageVisible("Engine1") < 2) {
//                this.hitChunk("Engine1", shot);
//            }
//            return;
//        }
//        if (s.startsWith("xengine2")) {
//            if (this.chunkDamageVisible("Engine2") < 2) {
//                this.hitChunk("Engine2", shot);
//            }
//            return;
//        }
//        if (s.startsWith("xengine3")) {
//            if (this.chunkDamageVisible("Engine3") < 2) {
//                this.hitChunk("Engine3", shot);
//            }
//            return;
//        }
//        if (s.startsWith("xengine4")) {
//            if (this.chunkDamageVisible("Engine4") < 2) {
//                this.hitChunk("Engine4", shot);
//            }
//            return;
//        }
        if (s.toLowerCase().startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.05F) {
                Aircraft.debugprintln(this, "*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
            return;
        }
        if (s.toLowerCase().startsWith("xturret")) {
            return;
        }
        if (s.toLowerCase().startsWith("xpilot") || s.toLowerCase().startsWith("xhead")) {
            byte byte0 = 0;
            int i1;
            if (s.endsWith("a")) {
                byte0 = 1;
                i1 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                i1 = s.charAt(6) - 49;
            } else {
                i1 = s.charAt(5) - 49;
            }
            this.hitFlesh(i1, shot, byte0);
            return;
        } else {
            return;
        }
    }

    public void msgExplosion(Explosion explosion) {
        this.setExplosion(explosion);
        if ((explosion.chunkName != null) && (explosion.power > 0.0F)) {
            if (explosion.chunkName.equals("Tail1_D3")) {
                return;
            }
            if (explosion.chunkName.equals("WingLIn_D3")) {
                return;
            }
            if (explosion.chunkName.equals("WingRIn_D3")) {
                return;
            }
            if (explosion.chunkName.equals("WingLMid_D3")) {
                return;
            }
            if (explosion.chunkName.equals("WingRMid_D3")) {
                return;
            }
            if (explosion.chunkName.equals("WingLOut_D3")) {
                return;
            }
            if (explosion.chunkName.equals("WingROut_D3")) {
                return;
            }
        }
        super.msgExplosion(explosion);
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float noseGearPos, float randoms[], float steering) {
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, CommonTools.smoothCvt(noseGearPos, randoms[2] + 0.01F, randoms[2] + 0.45F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, CommonTools.smoothCvt(noseGearPos, randoms[2] + 0.01F, randoms[2] + 0.45F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, CommonTools.smoothCvt(noseGearPos, randoms[2] + 0.25F, randoms[2] + 0.79F, 0.0F, 110F), 0.0F);
        hiermesh.chunkSetAngles("GearC10_D0", 0.0F, CommonTools.smoothCvt(noseGearPos, randoms[2] + 0.25F, randoms[2] + 0.79F, 0.0F, 110F), 0.0F);
        
        Aircraft.xyz[0] = Aircraft.xyz[2] = Aircraft.ypr[0] = 0F;
        Aircraft.xyz[1] = CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.45F, randoms[0] + 0.65F, -0.5F, 0F);
        Aircraft.ypr[1] = CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0F, -25F);
        Aircraft.ypr[2] = CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0.0F, -91F);

        hiermesh.chunkSetLocate("GearL2_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetLocate("GearL9_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[1] = -Aircraft.xyz[1];
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
        hiermesh.chunkSetLocate("GearL7_D0", Aircraft.xyz, Aircraft.ypr);
        
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 0.0F, CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0.0F, -30F));
        hiermesh.chunkSetAngles("GearL4Anchor_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0.0F, -90F), CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0.0F, -10F));
        Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
        Aircraft.xyz[0] = CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0.0F, -0.5F);
        Aircraft.ypr[0] = CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.25F, randoms[0] + 0.79F, 0.0F, -25F);
        hiermesh.chunkSetLocate("GearL4_D0", Aircraft.xyz, Aircraft.ypr);
        if (leftGearPos <= (randoms[0] + 0.65F)) {
            hiermesh.chunkSetAngles("GearL5_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.02F, randoms[0] + 0.30F, 0.0F, 90F), CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.02F, randoms[0] + 0.30F, 0F, -10F));
        } else {
            hiermesh.chunkSetAngles("GearL5_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.70F, randoms[0] + 0.79F, 90F, 0F), CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.70F, randoms[0] + 0.79F, -10F, 0F));
        }
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.02F, randoms[0] + 0.65F, 0.0F, 165F), 0.0F);
        hiermesh.chunkSetAngles("GearL8_D0", CommonTools.smoothCvt(leftGearPos, randoms[0] + 0.35F, randoms[0] + 0.79F, 0F, 25F), 0.0F, 0.0F);

        Aircraft.xyz[0] = Aircraft.xyz[2] = Aircraft.ypr[0] = 0F;
        Aircraft.xyz[1] = CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.45F, randoms[1] + 0.65F, -0.5F, 0F);
        Aircraft.ypr[1] = CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0F, 25F);
        Aircraft.ypr[2] = CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0.0F, -91F);

        hiermesh.chunkSetLocate("GearR2_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetLocate("GearR9_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[1] = -Aircraft.xyz[1];
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
        hiermesh.chunkSetLocate("GearR7_D0", Aircraft.xyz, Aircraft.ypr);
        
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, 0.0F, CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0.0F, -30F));

        hiermesh.chunkSetAngles("GearR4Anchor_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0.0F, 90F), CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0.0F, -10F));
        Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
        Aircraft.xyz[0] = CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0.0F, 0.5F);
        Aircraft.ypr[0] = CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.25F, randoms[1] + 0.79F, 0.0F, -25F);
        hiermesh.chunkSetLocate("GearR4_D0", Aircraft.xyz, Aircraft.ypr);
        if (rightGearPos <= (randoms[1] + 0.65F)) {
            hiermesh.chunkSetAngles("GearR5_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.02F, randoms[1] + 0.30F, 0.0F, 90F), CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.02F, randoms[1] + 0.30F, 0F, 10F));
        } else {
            hiermesh.chunkSetAngles("GearR5_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.70F, randoms[1] + 0.79F, 90F, 0F), CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.70F, randoms[1] + 0.79F, 10F, 0F));
        }
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.02F, randoms[1] + 0.65F, 0.0F, 165F), 0.0F);
        hiermesh.chunkSetAngles("GearR8_D0", CommonTools.smoothCvt(rightGearPos, randoms[1] + 0.35F, randoms[1] + 0.79F, 0F, -25F), 0.0F, 0.0F);
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2) {
        B_36X.moveGear(hiermesh, f, f1, f2, B_36X.rndgearnull, 0.0F);
    }

    protected void moveGear(float f, float f1, float f2) {
        B_36X.moveGear(this.hierMesh(), f, f1, f2, this.rndgear, this.steering);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        B_36X.moveGear(hiermesh, f, f, f, B_36X.rndgearnull, 0.0F);
    }

    protected void moveGear(float f) {
        B_36X.moveGear(this.hierMesh(), f, f, f, this.rndgear, this.steering);
    }

    public void moveSteering(float f) {
    }

    public void moveWheelSink() {
        // This is the gear's suspension code.
        // The maximum wheelsink we accept is 0.6m (out of 0.0m ... 1.0m). Above this, the gear gets stiff.
        // The suspension will sink max. 0.5m, which means at full gear pressure, the tire will be flattened by 10cm.

        //HUD.training("WS=" + FM.Gears.gWheelSinking[0] + "#" + FM.Gears.gWheelSinking[1] + "#" + FM.Gears.gWheelSinking[2] + "#" + FM.Gears.springsStiffness);
        this.resetYPRmodifier();
        // Calculate sink value for left gear.
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.85F, 0.0F, 0.75F);
        Aircraft.xyz[1] = CommonTools.smoothCvt(this.FM.CT.getGearL(), this.rndgear[0] + 0.45F, this.rndgear[0] + 0.65F, -0.5F, 0F);
        Aircraft.ypr[1] = CommonTools.smoothCvt(this.FM.CT.getGearL(), this.rndgear[0] + 0.25F, this.rndgear[0] + 0.79F, 0.0F, -25F);
        Aircraft.ypr[2] = CommonTools.smoothCvt(this.FM.CT.getGearL(), this.rndgear[0] + 0.25F, this.rndgear[0] + 0.79F, 0.0F, -91F);
        
        this.hierMesh().chunkSetLocate("GearL2_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[0] = 0F;
        Aircraft.xyz[1] = -Aircraft.xyz[1];
        Aircraft.xyz[2] = -Aircraft.xyz[2];
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
        this.hierMesh().chunkSetLocate("GearL7_D0", Aircraft.xyz, Aircraft.ypr);
        
        this.resetYPRmodifier();
        Aircraft.xyz[0] = CommonTools.smoothCvt(this.FM.CT.getGearL(), this.rndgear[0] + 0.25F, this.rndgear[0] + 0.79F, 0.0F, -0.5F - Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.6F, 0.0F, 0.5F));
        Aircraft.ypr[0] = CommonTools.smoothCvt(this.FM.CT.getGearL(), this.rndgear[0] + 0.25F, this.rndgear[0] + 0.79F, 0.0F, -25F);
        this.hierMesh().chunkSetLocate("GearL4_D0", Aircraft.xyz, Aircraft.ypr);

        this.resetYPRmodifier();
        // Calculate sink value for right gear.
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.85F, 0.0F, 0.75F);
        Aircraft.xyz[1] = CommonTools.smoothCvt(this.FM.CT.getGearR(), this.rndgear[1] + 0.45F, this.rndgear[1] + 0.65F, -0.5F, 0F);
        Aircraft.ypr[1] = CommonTools.smoothCvt(this.FM.CT.getGearR(), this.rndgear[1] + 0.25F, this.rndgear[1] + 0.79F, 0.0F, 25F);
        Aircraft.ypr[2] = CommonTools.smoothCvt(this.FM.CT.getGearR(), this.rndgear[1] + 0.25F, this.rndgear[1] + 0.79F, 0.0F, -91F);

        this.hierMesh().chunkSetLocate("GearR2_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[0] = 0F;
        Aircraft.xyz[1] = -Aircraft.xyz[1];
        Aircraft.xyz[2] = -Aircraft.xyz[2];
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
        this.hierMesh().chunkSetLocate("GearR7_D0", Aircraft.xyz, Aircraft.ypr);

        this.resetYPRmodifier();
        Aircraft.xyz[0] = CommonTools.smoothCvt(this.FM.CT.getGearR(), this.rndgear[1] + 0.25F, this.rndgear[1] + 0.79F, 0.0F, 0.5F + Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.6F, 0.0F, 0.5F));
        Aircraft.ypr[0] = CommonTools.smoothCvt(this.FM.CT.getGearR(), this.rndgear[1] + 0.25F, this.rndgear[1] + 0.79F, 0.0F, -25F);
        this.hierMesh().chunkSetLocate("GearR4_D0", Aircraft.xyz, Aircraft.ypr);

        this.resetYPRmodifier();
        // Calculate sink value for nose gear.
        Aircraft.xyz[0] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.8F, 0.0F, -0.8F);
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.8F, 0.0F, -0.4F);
        Aircraft.ypr[1] = CommonTools.smoothCvt(this.FM.CT.getGearC(), this.rndgear[2] + 0.25F, this.rndgear[2] + 0.79F, 0.0F, 110F);
        this.hierMesh().chunkSetLocate("GearC2_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveRudder(float f) {
        super.moveRudder(f);
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -16F * f, 0.0F);
        if (this.FM.CT.getGear() > 0.9F) {
            this.hierMesh().chunkSetAngles("GearC3_D0", 30F * f, 0.0F, 0.0F);
        }
    }

    protected void moveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -25F * f, 0.0F);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -25F * f, 0.0F);
    }

    public void moveCockpitDoor(float f) {
        if (f < 0.95F) {
            this.lastTurretActivationRequested = Time.current() - B_36X.TURRET_ACTIVATED_DURATION - 1L;
        }
    }

    private void animateTurret3(float f) {
        this.hierMesh().chunkSetAngles("Turret3Base_D0", 0.0F, Aircraft.cvt(f, 0F, 0.75F, 0F, 100F), 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.75F, 1F, 0.03F, 0F);
        Aircraft.ypr[1] = Aircraft.cvt(f, 0.25F, 0.75F, -39.5F, 0F);
        this.hierMesh().chunkSetLocate("Door_ADL", Aircraft.xyz, Aircraft.ypr);
    }

    private void animateTurret4(float f) {
        this.hierMesh().chunkSetAngles("Turret4Base_D0", 0.0F, Aircraft.cvt(f, 0F, 0.75F, 0F, -100F), 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.75F, 1F, 0.03F, 0F);
        Aircraft.ypr[1] = Aircraft.cvt(f, 0.25F, 0.75F, 39.5F, 0F);
        this.hierMesh().chunkSetLocate("Door_ADR", Aircraft.xyz, Aircraft.ypr);
    }

    private void animateTurret5(float f) {
        this.hierMesh().chunkSetAngles("Turret5Base_D0", 0.0F, Aircraft.cvt(f, 0F, 0.75F, 0F, 100F), 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.75F, 1F, 0.03F, 0F);
        Aircraft.ypr[1] = Aircraft.cvt(f, 0.25F, 0.75F, -34.5F, 0F);
        this.hierMesh().chunkSetLocate("Door_PDL", Aircraft.xyz, Aircraft.ypr);
    }

    private void animateTurret6(float f) {
        this.hierMesh().chunkSetAngles("Turret6Base_D0", 0.0F, Aircraft.cvt(f, 0F, 0.75F, 0F, -100F), 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.75F, 1F, 0.03F, 0F);
        Aircraft.ypr[1] = Aircraft.cvt(f, 0.25F, 0.75F, 34.5F, 0F);
        this.hierMesh().chunkSetLocate("Door_PDR", Aircraft.xyz, Aircraft.ypr);
    }

    private void animateTurret7(float f) {
        this.hierMesh().chunkSetAngles("Turret7Base_D0", 0.0F, 0.0F, Aircraft.cvt(f, 0F, 0.75F, 0F, 100F));
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.75F, 1F, -0.03F, 0F);
        Aircraft.ypr[1] = Aircraft.cvt(f, 0.25F, 0.75F, 35F, 0F);
        this.hierMesh().chunkSetLocate("Door_PIL", Aircraft.xyz, Aircraft.ypr);
    }

    private void animateTurret8(float f) {
        this.hierMesh().chunkSetAngles("Turret8Base_D0", 0.0F, 0.0F, Aircraft.cvt(f, 0F, 0.75F, 0F, -100F));
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.75F, 1F, -0.03F, 0F);
        Aircraft.ypr[1] = Aircraft.cvt(f, 0.25F, 0.75F, -35F, 0F);
        this.hierMesh().chunkSetLocate("Door_PIR", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveFlap(float f) {
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, 0.0F, f * 30F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, 0.0F, f * 30F);
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -20F * f, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 20F * f, 0.0F);
    }
    
    public boolean cut(String s) {
        if (s.toLowerCase().startsWith("nose")) {
            this.hierMesh().chunkVisible("Nose_D0", false);
            this.hierMesh().chunkVisible("Nose_D1", false);
            this.hierMesh().chunkVisible("Nose_D2", true);
            return false;
        }
        else if (s.toLowerCase().startsWith("winglmid"))
            super.cut("WingLNacelles");
        else if (s.toLowerCase().startsWith("wingrmid"))
            super.cut("WingRNacelles");
        return super.cut(s);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                this.hitProp(1, j, actor);
                this.FM.EI.engines[1].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 1, World.Rnd().nextInt(0, 9));
                // fall through

            case 34:
                this.hitProp(0, j, actor);
                this.FM.EI.engines[0].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 0, World.Rnd().nextInt(2, 8));
                this.FM.AS.hitTank(actor, 1, World.Rnd().nextInt(0, 5));
                // fall through

            case 35:
                this.FM.AS.hitTank(actor, 0, World.Rnd().nextInt(0, 4));
                break;

            case 36:
                this.hitProp(2, j, actor);
                this.FM.EI.engines[2].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 2, World.Rnd().nextInt(0, 9));
                // fall through

            case 37:
                this.hitProp(3, j, actor);
                this.FM.EI.engines[3].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 2, World.Rnd().nextInt(0, 5));
                this.FM.AS.hitTank(actor, 3, World.Rnd().nextInt(2, 8));
                // fall through

            case 38:
                this.FM.AS.hitTank(actor, 3, World.Rnd().nextInt(0, 4));
                break;

            case 25:
                if (this.FM.turret != null && this.FM.turret.length > 0) this.FM.turret[0].bIsOperable = false;
                return false;

            case 26:
                if (this.FM.turret != null && this.FM.turret.length > 1) this.FM.turret[1].bIsOperable = false;
                return false;

            case 27:
                if (this.FM.turret != null && this.FM.turret.length > 2) this.FM.turret[2].bIsOperable = false;
                return false;

            case 28:
                if (this.FM.turret != null && this.FM.turret.length > 3) this.FM.turret[3].bIsOperable = false;
                return false;

            case 29:
                if (this.FM.turret != null && this.FM.turret.length > 4) this.FM.turret[4].bIsOperable = false;
                return false;

            case 30:
                if (this.FM.turret != null && this.FM.turret.length > 5) this.FM.turret[5].bIsOperable = false;
                return false;

            case 31:
                if (this.FM.turret != null && this.FM.turret.length > 6) this.FM.turret[6].bIsOperable = false;
                return false;

            case 32:
                if (this.FM.turret != null && this.FM.turret.length > 7) this.FM.turret[7].bIsOperable = false;
                return false;

            case 19:
                this.killPilot(this, 4);
                this.killPilot(this, 5);
                this.killPilot(this, 6);
                this.killPilot(this, 7);
                this.killPilot(this, 8);
                //this.killPilot(this, 9);
                break;
        }
        return super.cutFM(i, j, actor);
    }

    protected void moveBayDoor(float f) {
        this.hierMesh().chunkSetAngles("Bay1_D0", 0.0F, -90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay2_D0", 0.0F, 90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay3_D0", 0.0F, -90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay4_D0", 0.0F, 90F * f, 0.0F);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if ((this.FM.AS.astateEngineStates[0] > 3) && (World.Rnd().nextFloat() < 0.0023F)) {
                this.FM.AS.hitTank(this, 0, 1);
            }
            if ((this.FM.AS.astateEngineStates[1] > 3) && (World.Rnd().nextFloat() < 0.0023F)) {
                this.FM.AS.hitTank(this, 1, 1);
            }
            if ((this.FM.AS.astateEngineStates[2] > 3) && (World.Rnd().nextFloat() < 0.0023F)) {
                this.FM.AS.hitTank(this, 2, 1);
            }
            if ((this.FM.AS.astateEngineStates[3] > 3) && (World.Rnd().nextFloat() < 0.0023F)) {
                this.FM.AS.hitTank(this, 3, 1);
            }
            if ((this.FM.AS.astateTankStates[0] > 4) && (World.Rnd().nextFloat() < 0.04F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            }
            if ((this.FM.AS.astateTankStates[1] > 4) && (World.Rnd().nextFloat() < 0.04F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            }
            if ((this.FM.AS.astateTankStates[2] > 4) && (World.Rnd().nextFloat() < 0.04F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            }
            if ((this.FM.AS.astateTankStates[3] > 4) && (World.Rnd().nextFloat() < 0.04F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
            }
            if (!(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) {
                for (int i = 0; i < this.FM.EI.getNum(); i++) {
                    if ((this.FM.AS.astateEngineStates[i] > 3) && (World.Rnd().nextFloat() < 0.2F)) {
                        this.FM.EI.engines[i].setExtinguisherFire();
                    }
                }

            }
        }
        for (int j = 1; j < 3; j++) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask" + j + "_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask" + j + "_D0", this.hierMesh().isChunkVisible("Pilot" + j + "_D0"));
            }
        }

        // Reduce Fuel consumption to 20% of what has been configured in flight model,
        // in order to counter flight model limitations regarding minimum fuel consumption
        // and to avoid overload from fuel levels
        if (this.prevFuel < 0F) {
            this.prevFuel = this.FM.M.fuel;
        } else {
            if ((this.prevFuel > this.FM.M.fuel) && (this.FM.M.fuel > 100F)) {
                this.FM.M.fuel += (this.prevFuel - this.FM.M.fuel) * 0.8F;
            }
            this.prevFuel = this.FM.M.fuel;
        }
    }
    
    //###############################################################
    // +++ Helper Methods to ensure AI is dropping the full bomb load
    private int countBombs() {
        int totalBombs = 0;
        if (this.FM.CT != null && this.FM.CT.Weapons != null && this.FM.CT.Weapons[3] != null) {
            for (int i=0; i<this.FM.CT.Weapons[3].length; i++) {
                if (this.FM.CT.Weapons[3][i] != null && !(this.FM.CT.Weapons[3][i] instanceof BombGunNull) && this.FM.CT.Weapons[3][i].haveBullets()) {
                    totalBombs += this.FM.CT.Weapons[3][i].countBullets();
                }
            }
        }
        return totalBombs;
    }
    
//    private boolean dropNextBomb() {
//        if (this.FM.CT != null && this.FM.CT.Weapons != null && this.FM.CT.Weapons[3] != null) {
//            for (int i=0; i<this.FM.CT.Weapons[3].length; i++) {
//                if (this.FM.CT.Weapons[3][i] != null && this.FM.CT.Weapons[3][i].haveBullets()) {
//                    this.FM.CT.Weapons[3][i].shots(1);
//                    return !(this.FM.CT.Weapons[3][i] instanceof BombGunNull);
//                }
//            }
//        }
//        return false;
//    }
    // ---
    //###############################################################
    
    private boolean isAiBombDrop = false;
    private long closeAiBombBay = -1L;

    public void update(float f) {
        super.update(f);
        
//        DecimalFormat df = new DecimalFormat("0.00");
//        HUD.training("R:" + (int)this.FM.EI.engines[0].getRPM());

        if (!this.FM.Gears.onGround() && (this.steering > 0.01F)) {
            this.steering *= 0.99F;
            this.hierMesh().chunkSetAngles("GearC3_D0", 0.0F, this.steering * this.FM.CT.getGear(), 0.0F);
        }

//        HUD.training("Pitch=" + (this.pos.getAbsOrient().getPitch() - 360F));
        float newGearAngle = this.FM.Gears.onGround() ? this.pos.getAbsOrient().getPitch() - 360F : 0F;
        this.gearAngle = ((this.gearAngle * 9F) + newGearAngle) / 10F;

        if (this.FM.CT.getGearL() > 0.99F && (this.FM.Gears.onGround() || this.FM.Gears.nearGround())) {
            this.hierMesh().chunkSetAngles("GearL8_D0", 25F - this.gearAngle, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("GearL1a_D0", 0.0F, -this.FM.Gears.gWheelAngles[0], 0.0F);
        }
        if (this.FM.CT.getGearR() > 0.99F && (this.FM.Gears.onGround() || this.FM.Gears.nearGround())) {
            this.hierMesh().chunkSetAngles("GearR8_D0", -25F + this.gearAngle, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("GearR1a_D0", 0.0F, -this.FM.Gears.gWheelAngles[1], 0.0F);
        }

        // The following code serves the purpose to avoid tailstrikes on touchdown when AI is controlling the airplane.
        // Usually AI will land with flaps fully deployed, attempting to reach about 17� AoA.
        // That's too much for a plane like this, so we limit the AoA to 8� here.
        // Note that this only affects AI. A player can pull as much AoA as he likes, up and until the tailstrike happens.
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            Maneuver maneuver = (Maneuver) this.FM;
//            HUD.training("M:" + maneuver.get_maneuver() + " T:" + maneuver.get_task());
            this.updateAIJetUsage();
            this.setOptimalMixture();
            this.setBestPistonCompressorStep();
            for (int engineIndex=0; engineIndex<6; engineIndex++)
                if (this.FM.EI.engines[engineIndex].getEngineVector().x < 0F) {
                    Vector3f engineVector = this.FM.EI.engines[engineIndex].getEngineVector();
                    engineVector.x *= -1F;
                    this.FM.EI.engines[engineIndex].setVector(engineVector);
                }
            
            // FIXME: Landing behaviour!
 
//            // When plane is landing and main gear is on runway, apply brakes accordingly.
//            if (maneuver.get_maneuver() == Maneuver.LANDING) {
//                if ((maneuver.Gears.nOfGearsOnGr >= 2) && (!maneuver.AP.way.isLandingOnShip())) {
//                    if (this.brakingtimer == 0L) {
//                        this.brakingtimer = Time.current();
//                    }
//                    Reflection.setFloat(this.FM.CT, "Elevators", this.FM.CT.ElevatorControl = CommonTools.cvt(Time.current() - this.brakingtimer, 0L, 1000L, this.FM.CT.ElevatorControl, 0F));
//                    if (maneuver.getSpeedKMH() > 25.0F) {
//                        if (maneuver.CT.BrakeControl < 0.9F && maneuver.Gears.nOfGearsOnGr > 2 && Time.current() > (this.brakingtimer + 2500L)) maneuver.CT.BrakeControl += 0.03F;
//                        else if (maneuver.CT.BrakeControl < 0.1F && maneuver.Gears.nOfGearsOnGr > 1 && Time.current() > (this.brakingtimer + 500L)) maneuver.CT.BrakeControl += 0.02F;
//                        Reflection.setFloat(maneuver.CT, "Brake", (maneuver.CT.getBrake() * 0.99F) + 0.01F);
//                    }
//                }
//            }
            
            
            
//            float smoothFactor = 50F;

            if (maneuver.get_maneuver() == Maneuver.LANDING /* && !maneuver.AP.way.isLandingOnShip() */) {
                if (maneuver.Gears.nOfGearsOnGr > 2) {
                    if (this.brakingtimer == 0L)
                        this.brakingtimer = Time.current();
                    if (maneuver.getSpeedKMH() > 40.0F) {
                        if (maneuver.CT.BrakeControl < AI_LANDING_BRAKE_POWER && Time.current() > (this.brakingtimer + 500L)) {
                            this.targetBrakePower += 0.002F;
                            if (this.targetBrakePower > AI_LANDING_BRAKE_POWER) this.targetBrakePower = AI_LANDING_BRAKE_POWER;
                            maneuver.CT.BrakeControl = this.targetBrakePower;
                        }
//                        Reflection.setFloat(maneuver.CT, "Brake", (maneuver.CT.getBrake() * (smoothFactor - 1F) + maneuver.CT.BrakeControl) / smoothFactor);
                        Reflection.setFloat(maneuver.CT, "Brake", maneuver.CT.BrakeControl);
//                        try {
//                            Reflection.setFloat(maneuver.CT, "BrakeRightControl", maneuver.CT.BrakeControl);
//                            Reflection.setFloat(maneuver.CT, "BrakeLeftControl", maneuver.CT.BrakeControl);
//                            Reflection.setFloat(maneuver.CT, "BrakeRight", maneuver.CT.BrakeControl);
//                            Reflection.setFloat(maneuver.CT, "BrakeLeft", maneuver.CT.BrakeControl);
//                        } catch (Exception e) { }
                        needsBrakeRelease = true;
                    } else if (needsBrakeRelease) {
                        needsBrakeRelease = false;
                        this.targetBrakePower = 0F;
                        maneuver.CT.BrakeControl = 0F;
                        Reflection.setFloat(maneuver.CT, "Brake", 0F);
//                        try {
//                            Reflection.setFloat(maneuver.CT, "BrakeRightControl", maneuver.CT.BrakeControl);
//                            Reflection.setFloat(maneuver.CT, "BrakeLeftControl", maneuver.CT.BrakeControl);
//                            Reflection.setFloat(maneuver.CT, "BrakeRight", maneuver.CT.BrakeControl);
//                            Reflection.setFloat(maneuver.CT, "BrakeLeft", maneuver.CT.BrakeControl);
//                        } catch (Exception e) { }
                    }
                }
                if (maneuver.Gears.nOfGearsOnGr > 1) {
                    if (this.FM.getAOA() > 0F) {
                        float deltaAoA = this.lastAoA - this.FM.getAOA();
                        targetElevator = CommonTools.cvt(deltaAoA, -5F, 5F, -1F, 1F);
                    } else {
                        targetElevator = 0F;
                    }
                    Reflection.setFloat(this.FM.CT, "Elevators", this.FM.CT.ElevatorControl = targetElevator);
                } else {
                    targetElevator = this.FM.CT.ElevatorControl;
                }
                this.lastAoA = this.FM.getAOA();
            }
            
//            if (this == World.getPlayerAircraft()) HUD.training("M:" + maneuver.get_maneuver() + " T:" + maneuver.get_task() + " B:" + maneuver.CT.BrakeControl);

//            DecimalFormat df = new DecimalFormat("0.00");
//            HUD.training("Maneuver=" + maneuver.get_maneuver() + " GoG=" + maneuver.Gears.nOfGearsOnGr + " BC=" + df.format(this.FM.CT.BrakeControl) + " B=" + df.format(this.FM.CT.getBrake()));
            if ((maneuver.get_maneuver() == Maneuver.LANDING) && (maneuver.Alt < 60.0F)) { // Plane is in landing pattern and near ground
                this.FM.CT.StabilizerControl = false;
                //HUD.training("Landing config!");
                if (maneuver.Or.getTangage() > 6F) { // Limit nose up attitude to 6 degrees on touchdown to avoid tail strike (only for AI)!
                    maneuver.Or.increment(0.0F, -(maneuver.Or.getTangage() - 6F), 0.0F); // apply AoA limit
                }
            } else if ((maneuver.get_maneuver() == Maneuver.TAKEOFF) && (this.FM.getSpeedKMH() > 10F) && (this.FM.getSpeedKMH() < 210F) && (this.FM.Gears.onGround() || this.FM.Gears.nearGround())) { // Plane is in takeoff run but well below rotate speed
                Reflection.setFloat(this.FM.CT, "Elevators", this.FM.CT.ElevatorControl = CommonTools.cvt(this.FM.getSpeedKMH(), 180F, 210F, 0F, this.FM.CT.ElevatorControl));
            }            // Make sure Prop Pitch is 100% and Mixture is 120% when taking off (bug seen in BAT 4.1.2 where Prop Pitch of Player Plane in Autopilot Mode would be way below 100%)
            if (maneuver.get_maneuver() == Maneuver.TAKEOFF) {
                if ((this.FM.CT.getStepControl() < 1.0F) || !this.aiStartPropPitchSet) {
                    this.aiStartPropPitchSet = true;
                    this.FM.CT.setStepControl(1.0F);
                }
                if ((this.FM.CT.getMixControl() < 1.2F) || !this.aiStartMixtureSet) {
                    this.aiStartMixtureSet = true;
                    this.FM.CT.setMixControl(1.2F);
                }
                if (this.FM.EI.isSelectionHasControlFeather() && World.cur().diffCur.ComplexEManagement && this.FM.EI.getFirstSelected() != null) {
                    if (this.FM.EI.getFirstSelected().getControlFeather() != 0) this.FM.EI.setFeather(0);
                    for (int engineIndex = 0; engineIndex < this.FM.EI.getNum(); engineIndex++)
                        if (this.FM.EI.engines[engineIndex].getControlFeather() != 0)
                            this.FM.EI.engines[engineIndex].setControlFeather(0);
                }
                this.FM.CT.StabilizerControl = false;
            }
            this.checkAiBombDrop();
            
//            // +++ Make sure AI always drops the full bomb load
//            if (this.FM.CT.WeaponControl[3] || this.FM.CT.saveWeaponControl[3]) {
//                isAiBombDrop = true;
//            } else if (isAiBombDrop) {
//                if (this.countBombs() == 0) {
//                    isAiBombDrop = false;
//                    this.closeAiBombBay = Time.current() + 3000L;
//                } else {
//                    if (this.dropNextBomb()) {
//                        this.FM.CT.BayDoorControl = 1F;
//                    } else {
//                        isAiBombDrop = false;
//                        this.closeAiBombBay = Time.current() + 3000L;
//                    }
//                }
//            }
//            if (this.closeAiBombBay > 0 && Time.current() > this.closeAiBombBay) {
//                this.FM.CT.BayDoorControl = 0F;
//                this.closeAiBombBay = -1L;
//            }
            // ---
        }

//        // Increase brake power!
//        if (this.FM.Gears.nOfGearsOnGr >= 2) {
//            float brake = this.FM.CT.getBrake();
//            if (brake < 0.5F) {
//                brake += (this.FM.CT.getBrakeLeft() + this.FM.CT.getBrakeRight()) / 2F;
//            }
//            //HUD.training("Brake = " + brake);
//            this.FM.Vwld.scale(Aircraft.cvt(brake, 0F, 1F, 1F, Aircraft.cvt(this.FM.getSpeedKMH(), 20F, 150F, 0.985F, 0.994F)));
//        }

        this.scheme7PropCollisionFix();
        this.updateTurretStatus();
        if (!this.initialMixtureSet) {
            this.setOptimalMixture();
        }
        if (this.initialCompressorSettingsLeft > 0) this.setBestPistonCompressorStep();
        this.FM.M.nitro = 1F; // unlimited water injection time
//        DecimalFormat df = new DecimalFormat("0.00");
//        HUD.training("W(P):" + df.format(this.FM.EI.engines[0].tWaterOut) + "/" + df.format(this.FM.EI.engines[0].tWaterCritMax) +
//                " O(P):" + df.format(this.FM.EI.engines[0].tOilOut) + "/" + df.format(this.FM.EI.engines[0].tOilCritMax) +
//                "W(J):" + df.format(this.FM.EI.engines[this.FM.EI.engines.length-1].tWaterOut) + "/" + df.format(this.FM.EI.engines[this.FM.EI.engines.length-1].tWaterCritMax) +
//                " O(J):" + df.format(this.FM.EI.engines[this.FM.EI.engines.length-1].tOilOut) + "/" + df.format(this.FM.EI.engines[this.FM.EI.engines.length-1].tOilCritMax));
        this.condensateAltStoppedEnginesFix();
        
//        DecimalFormat df = new DecimalFormat("0.00");
//        HUD.training("T:" + df.format(this.FM.EI.getThrustOutput()));
    }
    
    private void checkAiBombDrop() {
        if (this.FM.isPlayers() && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
            return;
        }
        if (!(this.FM instanceof Maneuver)) {
            return;
        }
        if (this.thisWeaponsName.toLowerCase().endsWith("xfatman")) return;
        if (this.thisWeaponsName.toLowerCase().endsWith("xmk4")) return;
        if (this.thisWeaponsName.toLowerCase().endsWith("xmk5")) return;
        if (this.thisWeaponsName.toLowerCase().endsWith("xmk6")) return;
        if (this.thisWeaponsName.toLowerCase().endsWith("xmk17")) return;
        if (this.thisWeaponsName.toLowerCase().endsWith("xmk15")) return;
        if (this.thisWeaponsName.toLowerCase().endsWith("xt12")) return;
        // +++ Make sure AI always drops the full bomb load
        if (this.FM.CT.WeaponControl[3] || this.FM.CT.saveWeaponControl[3]) {
            this.isAiBombDrop = true;
        } else if (this.isAiBombDrop) {
            if (this.countBombs() == 0) {
                this.isAiBombDrop = false;
                this.closeAiBombBay = Time.current() + 3000L;
            } else {
                this.FM.CT.BayDoorControl = 1F;
                ((Maneuver) this.FM).bombsOut = true;
            }
        }
        if ((this.closeAiBombBay > 0) && (Time.current() > this.closeAiBombBay)) {
            this.FM.CT.BayDoorControl = 0F;
            this.closeAiBombBay = -1L;
        }
        // ---
    }


    private void setOptimalMixture() {
        if (Reflection.getFloat(this.FM.EI.engines[0], "pressureExtBar") > 2.0F) return; // initially, pressureExtBar doesn't contain any valid value.
        this.initialMixtureSet = true;
        if (!World.cur().diffCur.ComplexEManagement) return;
        boolean needsMixtureChange = false;
        float bestControlMix = 1.0F;
        for (int engineIndex = 0; engineIndex < this.FM.EI.engines.length; engineIndex++) {
            if (!this.FM.EI.engines[engineIndex].isHasControlMix()) continue;
            if (Reflection.getInt(this.FM.EI.engines[engineIndex], "mixerType") != Motor._E_MIXER_LIMITED_PRESSURE) continue;
            float mixerLowPressureBar = Reflection.getFloat(this.FM.EI.engines[engineIndex], "mixerLowPressureBar");
            float pressureExtBar = Reflection.getFloat(this.FM.EI.engines[engineIndex], "pressureExtBar");
//            float pressureExtBar = Atmosphere.pressure(this.FM.getAltitude()) + Reflection.getFloat(this.FM.EI.engines[engineIndex], "compressorSpeedManifold") * 0.5F * Atmosphere.density(this.FM.getAltitude());
            bestControlMix = (float)Math.floor(pressureExtBar *10F / mixerLowPressureBar) / 10F;
//            if (engineIndex == 0) System.out.println("mixerLowPressureBar=" + mixerLowPressureBar + ", pressureExtBar=" + pressureExtBar + ", bestControlMix=" + bestControlMix);
            if (bestControlMix < 0.0F) bestControlMix = 0.0F;
            if (bestControlMix > 1.2F) bestControlMix = 1.2F;
            if (Math.abs(this.FM.EI.engines[engineIndex].getControlMix() - bestControlMix) >= 0.1F) {
                needsMixtureChange = true;
                break;
            }
        }
        if (needsMixtureChange) {
            FM.EI.setMix(bestControlMix);
            FM.CT.setMixControl(bestControlMix);
            if (this == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogPowerId, "PropMix", new Object[] {
                new Integer(Math.round(FM.CT.getMixControl() * 100F))
            });
        }
    }
    
    private void setBestPistonCompressorStep() {
//        System.out.println("this.FM.Gears.nOfGearsOnGr=" + this.FM.Gears.nOfGearsOnGr);
        if (--this.initialCompressorSettingsLeft > 0) return;
        int newControlCompressor = 0;
        if (this.FM.Gears.nOfGearsOnGr < 2) {
            float controlCompressor = 0F;
            for (int i=0; i<6; i++) controlCompressor += (float)getOptimalCompressorStep(i);
            newControlCompressor = Math.round(controlCompressor / 6F);
            if (newControlCompressor < 0) newControlCompressor = 0;
            if (newControlCompressor > this.FM.EI.engines[0].compressorMaxStep) newControlCompressor = this.FM.EI.engines[0].compressorMaxStep;
        }
        if (this.FM.CT.getCompressorControl() == newControlCompressor) return;
        this.FM.CT.setCompressorControl(newControlCompressor);
        if (this == World.getPlayerAircraft()) HUD.log("CompressorSetup" + FM.CT.getCompressorControl());
    }
    
    private int getOptimalCompressorStep(int engineIndex) {
        boolean oldHasCompressorControl = this.FM.EI.engines[engineIndex].isHasControlCompressor();
        this.FM.EI.engines[engineIndex].fastATA = true;
//        float test = ((Float)Reflection.invokeMethod(this.FM.EI.engines[engineIndex], "getCompressorMultiplier", new Class[] {float.class}, new Object[] {new Float(0.033F)})).floatValue();
//        if (engineIndex == 0) System.out.println("moment=" + test + ", controlCompressor=" + Reflection.getInt(this.FM.EI.engines[engineIndex], "controlCompressor"));
        Reflection.invokeMethod(this.FM.EI.engines[engineIndex], "getCompressorMultiplier", new Class[] {float.class}, new Object[] {new Float(0.033F)});
        Reflection.setBoolean(this.FM.EI.engines[engineIndex], "bHasCompressorControl", oldHasCompressorControl);
        return Reflection.getInt(this.FM.EI.engines[engineIndex], "controlCompressor");
    }
    
    private void scheme7PropCollisionFix() {
//        if (this.oldProp[0] == 2 || this.oldProp[1] == 2 ||this.oldProp[2] == 2) {
//            this.FM.setCapableOfTaxiing(false);
//            if (this.FM.EI.engines.length < 10) return;
//            this.hitProp(6, 0, Engine.actorLand());
//            this.hitProp(7, 0, Engine.actorLand());
//            this.FM.EI.engines[6].doSetEngineStuck();
//            this.FM.EI.engines[7].doSetEngineStuck();
//        }
//        if (this.oldProp[3] == 2) {
//            this.hitProp(4, 0, Engine.actorLand());
//            this.hitProp(5, 0, Engine.actorLand());
//            this.FM.EI.engines[4].doSetEngineStuck();
//            this.FM.EI.engines[5].doSetEngineStuck();
//            if (this.FM.EI.engines.length < 10) return;
//            this.hitProp(8, 0, Engine.actorLand());
//            this.hitProp(9, 0, Engine.actorLand());
//            this.FM.EI.engines[8].doSetEngineStuck();
//            this.FM.EI.engines[9].doSetEngineStuck();
//        }
//    }
        
        
        Vector3d normal = new Vector3d();
        Point3d pn = new Point3d();
        pn.set(this.FM.Loc);
        pn.z = Engine.cur.land.HQ(pn.x, pn.y);
        double d1 = pn.z;
        if ((this.FM.Loc.z - d1 > 50.0D) && (!this.FM.Gears.bFlatTopGearCheck))
          return;
        Engine.cur.land.EQN(pn.x, pn.y, normal);
        this.FM.Or.transformInv(normal);
        pn.x = 0.0D;
        pn.y = 0.0D;
        pn.z -= this.FM.Loc.z;
        this.FM.Or.transformInv(pn);
        double D = -normal.dot(pn);
        if (D > 50D) {
            return;
        }
        Point3f[] Pnt = (Point3f[]) Reflection.getValue(Gear.class, "Pnt");
        for (int i = 3; i < 9; i++) {
            Point3d PnT = new Point3d();
            PnT.set(Pnt[i]);
            double dot = normal.dot(PnT);
            if (dot == 0D) continue;
            double d = dot + D;
            if (d < 0.0D) {
//                System.out.println("B-36 Prop No. " + (i - 2) + " Collision detected: d=" + d + ", pn.z=" + pn.z + ", normal.dot(PnT)=" + normal.dot(PnT) + ", D=" + D);
                this.scheme7PropCollisionFix(i);
            }
//            else {
//                if (Time.tick() % 30 == 0)
//                    System.out.println("B-36 Prop No. " + (i - 2) + " d=" + d + ", pn.z=" + pn.z + ", normal.dot(PnT)=" + normal.dot(PnT) + ", D=" + D);
//            }
        }
    }

    private boolean scheme7PropCollisionFix(int i) {
        if (Reflection.getBoolean(this.FM.Gears, "bIsMaster") && (i >= 3) && (i <= 8)) {
            if ((this == World.getPlayerAircraft()) && !World.cur().diffCur.Realistic_Landings) {
                return false;
            }
            this.FM.setCapableOfTaxiing(false);
            this.hitProp(i - 3, 0, Engine.actorLand());
            this.FM.EI.engines[i - 3].doSetEngineStuck();
            if (this.FM.EI.engines.length > 6)
                if (i==3) {
                    this.FM.EI.engines[6].doSetEngineStuck();
                    this.FM.EI.engines[7].doSetEngineStuck();
                } else if (i==8) {
                    this.FM.EI.engines[8].doSetEngineStuck();
                    this.FM.EI.engines[9].doSetEngineStuck();
                }
            return false;
        } else {
            return true;
        }
    }

    private void updateTurretStatus() {
        if (!this.isEquippedWithTurrets) {
            return;
        }
        // Player needs to manually open CockpitDoor to activate Turrets
        if (this == World.getPlayerAircraft() && this.FM.isPlayers() && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode() && (this.FM.CT.cockpitDoorControl > 0.5F)) {
            this.lastTurretActivationRequested = Time.current();
        } else if (War.getNearestEnemy(this, 20000F) != null) { // AI automatically activates Turret when enemy is near
            this.lastTurretActivationRequested = Time.current();
        }
        float targetAnimation = Time.current() < (this.lastTurretActivationRequested + B_36X.TURRET_ACTIVATED_DURATION) ? 0F : 1F;
        for (int i = 0; i < this.turretReadyToAnimate.length; i++) {
            boolean retract = targetAnimation > 0.5F;
            if (i > 3) {
                if (this.FM.Gears.onGround() || this.FM.Gears.nearGround()) {
                    retract = true;
                }
                if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
                    Maneuver maneuver = (Maneuver) this.FM;
                    if (maneuver.get_maneuver() == Maneuver.LANDING) {
                        retract = true;
                    }
                }
            }
            if (retract) {
                this.FM.turret[i + 2].bIsOperable = false;
                this.turretReadyToAnimate[i] = ((Math.abs(this.FM.turret[i + 2].tu[0]) < 0.01F) || (Math.abs(this.FM.turret[i + 2].tu[0]) > 359.99F)) && ((Math.abs(this.FM.turret[i + 2].tu[1]) < 0.01F) || (Math.abs(this.FM.turret[i + 2].tu[1]) > 359.99F));
                if (!this.turretReadyToAnimate[i]) {
                    for (int j = 0; j < 2; j++) {
                        this.FM.turret[i + 2].tu[j] %= 360F;
                        if ((this.FM.turret[i + 2].tu[j] < 0F) || (this.FM.turret[i + 2].tu[j] >= 180F)) {
                            this.FM.turret[i + 2].tu[j] += Math.min(1.8F, Math.abs(this.FM.turret[i + 2].tu[j]));
                        } else {
                            this.FM.turret[i + 2].tu[j] -= Math.min(1.8F, Math.abs(this.FM.turret[i + 2].tu[j]));
                        }
                    }
                    this.resetYPRmodifier();
                    Aircraft.ypr[1] = this.FM.turret[i + 2].tu[0];
                    this.hierMesh().setCurChunk(this.FM.turret[i + 2].indexA);
                    this.hierMesh().chunkSetAngles(Aircraft.ypr);
                    Aircraft.ypr[1] = this.FM.turret[i + 2].tu[1];
                    this.hierMesh().setCurChunk(this.FM.turret[i + 2].indexB);
                    this.hierMesh().chunkSetAngles(Aircraft.ypr);
                } else {
                    if ((targetAnimation - this.turretAnimatedPosition[i]) > 0.01F) {
                        this.turretAnimatedPosition[i] += Math.min(0.01F, targetAnimation - this.turretAnimatedPosition[i]);
                        switch (i) {
                            case 0:
                                this.animateTurret3(this.turretAnimatedPosition[i]);
                                break;
                            case 1:
                                this.animateTurret4(this.turretAnimatedPosition[i]);
                                break;
                            case 2:
                                this.animateTurret5(this.turretAnimatedPosition[i]);
                                break;
                            case 3:
                                this.animateTurret6(this.turretAnimatedPosition[i]);
                                break;
                            case 4:
                                this.animateTurret7(this.turretAnimatedPosition[i]);
                                break;
                            case 5:
                                this.animateTurret8(this.turretAnimatedPosition[i]);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                this.turretReadyToAnimate[i] = true;
                if (this.turretAnimatedPosition[i] > 0.01F) {
                    this.FM.turret[i + 2].bIsOperable = false;
                    this.turretAnimatedPosition[i] -= Math.min(0.01F, this.turretAnimatedPosition[i]);
                    for (int j = 0; j < 2; j++) {
                        this.FM.turret[i + 2].tu[j] = 0F;
                    }
                    this.hierMesh().setCurChunk(this.FM.turret[i + 2].indexA);
                    this.hierMesh().chunkSetAngles(this.FM.turret[i + 2].tu);
                    this.hierMesh().setCurChunk(this.FM.turret[i + 2].indexB);
                    this.hierMesh().chunkSetAngles(this.FM.turret[i + 2].tu);
                    switch (i) {
                        case 0:
                            this.animateTurret3(this.turretAnimatedPosition[i]);
                            break;
                        case 1:
                            this.animateTurret4(this.turretAnimatedPosition[i]);
                            break;
                        case 2:
                            this.animateTurret5(this.turretAnimatedPosition[i]);
                            break;
                        case 3:
                            this.animateTurret6(this.turretAnimatedPosition[i]);
                            break;
                        case 4:
                            this.animateTurret7(this.turretAnimatedPosition[i]);
                            break;
                        case 5:
                            this.animateTurret8(this.turretAnimatedPosition[i]);
                            break;
                        default:
                            break;
                    }
                } else {
                    this.FM.turret[i + 2].bIsOperable = true;
                }
            }
        }
//        this.testTurrets(); // TODO: For Testing purpose only!
    }
    
//    float yawTest[] = new float[8];
//    float pitchTest[] = new float[8];
//    private void testTurrets() {
//        // Just for testing purpose
//        for (int i = 0; i < 8; i++) {
//            if (this.FM.turret[i].bIsOperable) {
//                this.FM.turret[i].bIsAIControlled = false;
//                float yaw = this.FM.turret[i].tu[0];
//                float pitch = this.FM.turret[i].tu[1];
//
////              DecimalFormat df = new DecimalFormat("0.##");
////              HUD.training("y=" + df.format(yaw) + " (" + df.format(yawTest[i]) + "), p=" + df.format(pitch) + " (" + df.format(pitchTest[i]) + ")");
//
//                if (yawTest[i] != yaw || pitchTest[i] != pitch) {
//                    this.FM.turret[i].tuLim[0] = yawTest[i];
//                    this.FM.turret[i].tuLim[1] = pitchTest[i];
//                    Reflection.invokeMethod(this.FM, "updateRotation", new Class[] { Turret.class, float.class }, new Object[] { this.FM.turret[i], new Float(0.03F) });
//                    continue;
//                }
//
//                float maxPitch = 90F;
//                float minPitch = -90F;
//                switch (i) {
//                    case 0:
//                        yaw = TrueRandom.nextFloat(-45F, 45F);
//                        pitch = TrueRandom.nextFloat(-36.5F, 37.5F);
//                        break;
//                    case 1:
//                        yaw = TrueRandom.nextFloat(-30F, 30F);
//                        pitch = TrueRandom.nextFloat(0F, 70.5F);
//                        break;
//                    case 2:
//                        yaw = TrueRandom.nextFloat(-10F, 190F);
//                        if (yaw < 90F && yaw > -90F) {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * -24.5F + 4.5F;
//                        } else {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * -30F + 10F;
//                        }
//                        pitch = TrueRandom.nextFloat(minPitch, 85F);
//                        break;
//                    case 3:
//                        yaw = TrueRandom.nextFloat(-190F, 10F);
//                        if (yaw > -90F && yaw < 90F) {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * 24.5F + 4.5F;
//                        } else {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * 30F + 10F;
//                        }
//                        pitch = TrueRandom.nextFloat(minPitch, 85F);
//                        break;
//                    case 4:
//                        yaw = TrueRandom.nextFloat(-10F, 190F);
//                        if (yaw < 90F && yaw > -90F) {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * -26F + 6F;
//                        } else {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * -32F + 12F;
//                        }
//                        pitch = TrueRandom.nextFloat(minPitch, 85F);
//                        break;
//                    case 5:
//                        yaw = TrueRandom.nextFloat(-190F, 10F);
//                        if (yaw > -90F && yaw < 90F) {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * 26F + 6F;
//                        } else {
//                            minPitch = (float) Math.sin(Math.toRadians(yaw)) * 32F + 12F;
//                        }
//                        pitch = TrueRandom.nextFloat(minPitch, 85F);
//                        break;
//                    case 6:
//                        yaw = TrueRandom.nextFloat(-10F, 190F);
//                        maxPitch = (float) Math.sin(Math.toRadians(yaw)) * 25.5F - 5.5F;
//                        pitch = TrueRandom.nextFloat(-85F, maxPitch);
//                        break;
//                    case 7:
//                        yaw = TrueRandom.nextFloat(-190F, 10F);
//                        maxPitch = (float) Math.sin(Math.toRadians(yaw)) * -25.5F - 5.5F;
//                        pitch = TrueRandom.nextFloat(-85F, maxPitch);
//                        break;
//                }
//                this.FM.turret[i].tuLim[0] = yawTest[i] = -yaw;
//                this.FM.turret[i].tuLim[1] = pitchTest[i] = pitch;
//            }
//        }
//    }
    
    private void condensateAltStoppedEnginesFix() {
        if (!Reflection.getBoolean(this.FM.AS, "bIsAboveCondensateAlt")) return;
        for(int i = 0; i < this.FM.EI.getNum(); i++) {
            if (this.FM.AS.astateCondensateEffects.length <= i) continue;
            if (this.FM.EI.engines[i].getStage() == Motor._E_STAGE_CATCH_UP
                    || this.FM.EI.engines[i].getStage() == Motor._E_STAGE_CATCH_ROLL
                    || this.FM.EI.engines[i].getStage() == Motor._E_STAGE_CATCH_FIRE
                    || this.FM.EI.engines[i].getStage() == Motor._E_STAGE_NOMINAL) {
                if (this.FM.AS.astateCondensateEffects[i] != null) continue;
                String[] astateCondensateStrings = (String[])Reflection.getValue(AircraftState.class, "astateCondensateStrings");
                if (astateCondensateStrings[1] == null) continue;
                Loc astateCondensateDispVector = (Loc)Reflection.getValue(AircraftState.class, "astateCondensateDispVector");
                this.FM.AS.astateCondensateEffects[i] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "Smoke"), astateCondensateDispVector, 1.0F, astateCondensateStrings[1], -1F);
            } else {
                if (this.FM.AS.astateCondensateEffects[i] == null) continue;
                Eff3DActor.finish(this.FM.AS.astateCondensateEffects[i]);
                this.FM.AS.astateCondensateEffects[i] = null;
            }
        }
    }
    
    private void updateAIJetUsage() {
        Maneuver maneuver = (Maneuver) this.FM;
//        HUD.training("M=" + maneuver.get_maneuver() + " S=" + Reflection.getInt(maneuver, "submaneuver") + " Sp=" + (maneuver.AP.way.curr().getV() * 3.6F) + " CT=" + maneuver.CT.getPowerControl() + " ST=" + this.FM.EI.engines[6].getStage());
//        System.out.println("M=" + maneuver.get_maneuver() + " S=" + Reflection.getInt(maneuver, "submaneuver") + " Sp=" + (maneuver.AP.way.curr().getV() * 3.6F) + " CT=" + maneuver.CT.getPowerControl());
        if (this.FM.EI.engines.length < 10) return;
        for (int i=0; i<maneuverDontTouchJets.length; i++) if (maneuver.get_maneuver() == maneuverDontTouchJets[i]) return;
        
        boolean validForJetStartAndShutdown = false;
        for (int i=0; i<maneuverValidForJetStartAndShutdown.length; i++) if (maneuver.get_maneuver() == maneuverValidForJetStartAndShutdown[i]) {
            validForJetStartAndShutdown = true;
            break;
        }
        if (!validForJetStartAndShutdown) return;
        
        for (int i=6; i<10; i++) {
            if (maneuver.CT.getPowerControl() < 0.75F) {
                if (this.FM.EI.engines[i].getStage() > Motor._E_STAGE_NULL
                        && this.FM.EI.engines[i].getStage() < Motor._E_STAGE_DEAD
                        && (this.lastJetPowerConfirmed[i-6] + JET_POWER_TOGGLE_TIMEOUT) < Time.current()) {
                    if (this == World.getPlayerAircraft()) HUD.log("EngineI0");
                    this.FM.EI.engines[i].setEngineStops(this);
//                    System.out.println("#### AI stopped Jet Engine " + (i-5));
                    this.jetPowerOn[i-6] = false;
                    this.lastJetPowerConfirmed[i-6] = Time.current();
                } else if (this.FM.EI.engines[i].getStage() == Motor._E_STAGE_NULL) {
                    this.lastJetPowerConfirmed[i-6] = Time.current();
                }
            } else if (maneuver.CT.getPowerControl() >= 0.8999F) {
                if (this.FM.EI.engines[i].getStage() == Motor._E_STAGE_NULL
                        && (this.lastJetPowerConfirmed[i-6] + JET_POWER_TOGGLE_TIMEOUT) < Time.current()) {
                    if (this == World.getPlayerAircraft()) HUD.log("EngineI1");
                    this.FM.EI.engines[i].setStage(this, Motor._E_STAGE_NOMINAL);
//                    System.out.println("#### AI started Jet Engine " + (i-5));
                    this.jetPowerOn[i-6] = true;
                    this.lastJetPowerConfirmed[i-6] = Time.current();
                } else if (this.FM.EI.engines[i].getStage() > Motor._E_STAGE_NULL
                            && this.FM.EI.engines[i].getStage() < Motor._E_STAGE_DEAD) {
                    this.lastJetPowerConfirmed[i-6] = Time.current();
                }
            } else {
                this.lastJetPowerConfirmed[i-6] = Time.current();
            }
        }
    }
    
    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 4:
                if (this.FM.turret != null && this.FM.turret.length > 0) this.FM.turret[0].setHealth(f);
                break;

            case 5:
                if (this.FM.turret != null && this.FM.turret.length > 1) this.FM.turret[1].setHealth(f);
                break;

            case 6:
                if (!this.isEquippedWithTurrets) {
                    break;
                }
                if (this.FM.turret != null && this.FM.turret.length > 2) this.FM.turret[2].setHealth(f);
                if (this.FM.turret != null && this.FM.turret.length > 3) this.FM.turret[3].setHealth(f);
                break;

            case 7:
                if (!this.isEquippedWithTurrets) {
                    break;
                }
                if (this.FM.turret != null && this.FM.turret.length > 4) this.FM.turret[4].setHealth(f);
                if (this.FM.turret != null && this.FM.turret.length > 5) this.FM.turret[5].setHealth(f);
                break;

            case 8:
                if (!this.isEquippedWithTurrets) {
                    break;
                }
                if (this.FM.turret != null && this.FM.turret.length > 6) this.FM.turret[6].setHealth(f);
                if (this.FM.turret != null && this.FM.turret.length > 7) this.FM.turret[7].setHealth(f);
                break;

        }
    }

    public void doMurderPilot(int i) {
        if (i<8) this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
        if (i<2) { 
            this.hierMesh().chunkVisible("HMask" + (i + 1) + "_D0", false);
            this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
            this.hierMesh().chunkVisible("Head" + (i + 1) + "_D0", false);
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float yaw = -af[0];
        float pitch = af[1];
        float minPitch = 0F;
        float maxPitch = 0F;
        switch (i) {
            default:
                break;

            case 0:
                if (yaw < -45F) {
                    yaw = -45F;
                    flag = false;
                }
                if (yaw > 45F) {
                    yaw = 45F;
                    flag = false;
                }
                if (pitch < -36.5F) {
                    pitch = -36.5F;
                    flag = false;
                }
                if (pitch > 37.5F) {
                    pitch = 37.5F;
                    flag = false;
                }
                break;

            case 1:
                if (yaw < -30F) {
                    yaw = -30F;
                    flag = false;
                }
                if (yaw > 30F) {
                    yaw = 30F;
                    flag = false;
                }
                
                if (pitch < 0F) {
                    pitch = 0F;
                    flag = false;
                }
                if (pitch > 70.5F) {
                    pitch = 70.5F;
                    flag = false;
                }
                break;

            case 2:
                if (yaw < -90F) yaw += 360F;
                if (yaw < -10F && yaw > -90F) {
                    yaw = -10F;
                    flag = false;
                }
                if (yaw > 190F) {
                    yaw = 190F;
                    flag = false;
                }
                
                if (pitch > 85F) {
                    pitch = 85F;
                    flag = false;
                }
                
                minPitch = -20F;
                if (yaw < 90F && yaw > -90F) {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * -24.5F + 4.5F;
                } else {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * -30F + 10F;
                }
                if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                  }  
                if (yaw > 100F && yaw < 120F) {
                    minPitch = CommonTools.smoothCvt(yaw, 100F, 120F, minPitch, 0F);
                } else if (yaw >= 120F && yaw < 170F) {
                    minPitch = 0F;
                } else {
                    minPitch = CommonTools.smoothCvt(Math.abs(yaw), 170F, 180F, minPitch, 15F);
                }
                if (pitch < minPitch) {
                    flag = false;
                }        
                break;

            case 3:
                if (yaw > 90F) yaw -= 360F;
                if (yaw > 10F && yaw < 90F) {
                    yaw = 10F;
                    flag = false;
                }
                if (yaw < -190F) {
                    yaw = -190F;
                    flag = false;
                }
                
                if (pitch > 85F) {
                    pitch = 85F;
                    flag = false;
                }
                
                minPitch = -20F;
                if (yaw > -90F && yaw < 90F) {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * 24.5F + 4.5F;
                } else {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * 30F + 10F;
                }
                if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                  }  
                if (yaw < -100F && yaw > -120F) {
                    minPitch = CommonTools.smoothCvt(yaw, -100F, -120F, minPitch, 0F);
                } else if (yaw <= -120F && yaw > -170F) {
                    minPitch = 0F;
                } else {
                    minPitch = CommonTools.smoothCvt(Math.abs(yaw), 170F, 180F, minPitch, 15F);
                }
                if (pitch < minPitch) {
                    flag = false;
                }        
                break;

            case 4:
                if (yaw < -90F) yaw += 360F;
                if (yaw < -10F && yaw > -90F) {
                    yaw = -10F;
                    flag = false;
                }
                if (yaw > 190F) {
                    yaw = 190F;
                    flag = false;
                }
                
                if (pitch > 85F) {
                    pitch = 85F;
                    flag = false;
                }
                
                minPitch = -20F;
                if (yaw < 90F && yaw > -90F) {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * -26F + 6F;
                } else {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * -32F + 12F;
                }
                if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                  }  
                if (yaw > -90F && yaw < 86F) {
                    minPitch = 0F;
                } else if (yaw > 138F && yaw < 175F) {
                    minPitch = -2F;
                } else if (Math.abs(yaw) > 175F) {
                    minPitch = 30F;
                }
                if (pitch < minPitch) {
                    flag = false;
                }  
                break;

            case 5:
                if (yaw > 90F) yaw -= 360F;
                if (yaw > 10F && yaw < 90F) {
                    yaw = 10F;
                    flag = false;
                }
                if (yaw < -190F) {
                    yaw = -190F;
                    flag = false;
                }
                
                if (pitch > 85F) {
                    pitch = 85F;
                    flag = false;
                }
                
                minPitch = -20F;
                if (yaw > -90F && yaw < 90F) {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * 26F + 6F;
                } else {
                    minPitch = (float)Math.sin(Math.toRadians(yaw)) * 32F + 12F;
                }
                if (pitch < minPitch) {
                  pitch = minPitch;
                  flag = false;
                }  
                if (yaw < 90F && yaw > -86F) {
                    minPitch = 0F;
                } else if (yaw < -138F && yaw > -175F) {
                    minPitch = -2F;
                } else if (Math.abs(yaw) > 175F) {
                    minPitch = 30F;
                }
                if (pitch < minPitch) {
                    flag = false;
                }  
                break;

            case 6:
                if (yaw < -90F) yaw += 360F;
                if (yaw < -10F && yaw > -90F) {
                    yaw = -10F;
                    flag = false;
                }
                if (yaw > 190F) {
                    yaw = 190F;
                    flag = false;
                }
                
                if (pitch < -85F) {
                    pitch = -85F;
                    flag = false;
                }
                
                maxPitch = (float)Math.sin(Math.toRadians(yaw)) * 25.5F - 5.5F;
                if (pitch > maxPitch) {
                    pitch = maxPitch;
                    flag = false;
                  }
                if (yaw > 0F) {
                    if (this.FM.Gears.rgear && yaw > 25F && yaw < 50F) {
                        maxPitch = -10F;
                    } else if (yaw >= 50F && yaw < 62F) {
                        maxPitch = 7F;
                    } else if (yaw >= 62F && yaw < 78F) {
                        maxPitch = 2F;
                    } else if (yaw >= 78F && yaw < 86F) {
                        maxPitch = 5F;
                    }
                }
                if (pitch > maxPitch) {
                    flag = false;
                }
                break;

            case 7:
                if (yaw > 90F) yaw -= 360F;
                if (yaw > 10F && yaw < 90F) {
                    yaw = 10F;
                    flag = false;
                }
                if (yaw < -190F) {
                    yaw = -190F;
                    flag = false;
                }
                
                if (pitch < -85F) {
                    pitch = -85F;
                    flag = false;
                }
                
                maxPitch = (float)Math.sin(Math.toRadians(yaw)) * -25.5F - 5.5F;
                if (pitch > maxPitch) {
                  pitch = maxPitch;
                  flag = false;
                }
                if (yaw < 0F) {
                    if (this.FM.Gears.rgear && yaw < -25F && yaw > -50F) {
                        maxPitch = -10F;
                    } else if (yaw <= -50F && yaw > -62F) {
                        maxPitch = 7F;
                    } else if (yaw <= -62F && yaw > -78F) {
                        maxPitch = 2F;
                    } else if (yaw <= -78F && yaw > -86F) {
                        maxPitch = 5F;
                    }
                }
                if (pitch > maxPitch) {
                    flag = false;
                }
                break;

        }
        af[0] = -yaw;
        af[1] = pitch;
        return flag;
    }

    public void doKillPilot(int i) {
        switch (i) {
            case 4:
                this.FM.turret[0].bIsOperable = false;
                break;

            case 5:
                this.FM.turret[1].bIsOperable = false;
                break;

            case 6:
                if (!this.isEquippedWithTurrets) {
                    break;
                }
                this.FM.turret[2].bIsOperable = false;
                this.FM.turret[3].bIsOperable = false;
                break;

            case 7:
                if (!this.isEquippedWithTurrets) {
                    break;
                }
                this.FM.turret[4].bIsOperable = false;
                this.FM.turret[5].bIsOperable = false;
                break;

            case 8:
                if (!this.isEquippedWithTurrets) {
                    break;
                }
                this.FM.turret[6].bIsOperable = false;
                this.FM.turret[7].bIsOperable = false;
                break;
        }
    }

    protected void moveFan(float f) {
        int i = 0;
        for (int j = 0; j < this.FM.EI.getNum(); j++) {
            if (this.oldProp[j] < 2) {
                i = Math.abs((int) (this.FM.EI.engines[j].getw() * 0.06F));
                if (i >= 1) {
                    i = 1;
                }
                if ((i != this.oldProp[j]) && this.hierMesh().isChunkVisible(B_36X.B36Props[j][this.oldProp[j]])) {
                    this.hierMesh().chunkVisible(B_36X.B36Props[j][this.oldProp[j]], false);
                    this.oldProp[j] = i;
                    this.hierMesh().chunkVisible(B_36X.B36Props[j][i], true);
                }
            }
            if (i == 0) {
                this.propPos[j] = (this.propPos[j] + (57.3F * this.FM.EI.engines[j].getw() * f)) % 360F;
            } else {
                float f1 = 57.3F * this.FM.EI.engines[j].getw();
                f1 %= 2880F;
                f1 /= 2880F;
                if (f1 <= 0.5F) {
                    f1 *= 2.0F;
                } else {
                    f1 = (f1 * 2.0F) - 2.0F;
                }
                f1 *= 1200F;
                this.propPos[j] = (this.propPos[j] + (f1 * f)) % 360F;
            }
            this.hierMesh().chunkSetAngles(B_36X.B36Props[j][0], 0.0F, this.propPos[j] * ((j > 2) && (j < 6) ? 1F : -1F), 0.0F);
//            this.hierMesh().chunkSetAngles(B_36X.B36Props[j][0], 0.0F, this.propPos[j], 0.0F);
        }

    }

    public boolean typeBomberToggleAutomation() {
        this.bSightAutomation = !this.bSightAutomation;
        this.bSightBombDump = false;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAutomation" + (this.bSightAutomation ? "ON" : "OFF"));
        return this.bSightAutomation;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle++;
        if (this.fSightCurForwardAngle > 85F) {
            this.fSightCurForwardAngle = 85F;
        }
        this.fSightCurDistance = B_36X.toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) {
            this.typeBomberToggleAutomation();
        }
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle--;
        if (this.fSightCurForwardAngle < 0.0F) {
            this.fSightCurForwardAngle = 0.0F;
        }
        this.fSightCurDistance = B_36X.toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) {
            this.typeBomberToggleAutomation();
        }
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip += 0.1F;
        if (this.fSightCurSideslip > 3F) {
            this.fSightCurSideslip = 3F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10F)) });
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip -= 0.1F;
        if (this.fSightCurSideslip < -3F) {
            this.fSightCurSideslip = -3F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10F)) });
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 3000F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 50F;
        if (this.fSightCurAltitude > 50000F) {
            this.fSightCurAltitude = 50000F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitudeft", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = B_36X.toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 50F;
        if (this.fSightCurAltitude < 1000F) {
            this.fSightCurAltitude = 1000F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitudeft", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = B_36X.toMeters(this.fSightCurAltitude) * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 200F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 10F;
        if (this.fSightCurSpeed > 450F) {
            this.fSightCurSpeed = 450F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeedMPH", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 10F;
        if (this.fSightCurSpeed < 100F) {
            this.fSightCurSpeed = 100F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeedMPH", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if (Math.abs(this.FM.Or.getKren()) > 4.5D) {
            this.fSightCurReadyness -= 0.0666666F * f;
            if (this.fSightCurReadyness < 0.0F) {
                this.fSightCurReadyness = 0.0F;
            }
        }
        if (this.fSightCurReadyness < 1.0F) {
            this.fSightCurReadyness += 0.0333333F * f;
        } else if (this.bSightAutomation) {
            this.fSightCurDistance -= B_36X.toMetersPerSecond(this.fSightCurSpeed) * f;
            if (this.fSightCurDistance < 0.0F) {
                this.fSightCurDistance = 0.0F;
                this.typeBomberToggleAutomation();
            }
            this.fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(this.fSightCurDistance / B_36X.toMeters(this.fSightCurAltitude)));
            if (this.fSightCurDistance < (B_36X.toMetersPerSecond(this.fSightCurSpeed) * Math.sqrt(B_36X.toMeters(this.fSightCurAltitude) * 0.2038736F))) {
                this.bSightBombDump = true;
            }
            if (this.bSightBombDump) {
                if (this.FM.isTick(3, 0)) {
                    if ((this.FM.CT.Weapons[3] != null) && (this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1] != null) && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                        this.FM.CT.WeaponControl[3] = true;
                        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                    }
                } else {
                    this.FM.CT.WeaponControl[3] = false;
                }
            }
        }
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte((this.bSightAutomation ? 1 : 0) | (this.bSightBombDump ? 2 : 0));
        netmsgguaranted.writeFloat(this.fSightCurDistance);
        netmsgguaranted.writeByte((int) this.fSightCurForwardAngle);
        netmsgguaranted.writeByte((int) ((this.fSightCurSideslip + 3F) * 33.33333F));
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeByte((int) (this.fSightCurSpeed / 2.5F));
        netmsgguaranted.writeByte((int) (this.fSightCurReadyness * 200F));
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        int i = netmsginput.readUnsignedByte();
        this.bSightAutomation = (i & 1) != 0;
        this.bSightBombDump = (i & 2) != 0;
        this.fSightCurDistance = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readUnsignedByte();
        this.fSightCurSideslip = -3F + (netmsginput.readUnsignedByte() / 33.33333F);
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readUnsignedByte() * 2.5F;
        this.fSightCurReadyness = netmsginput.readUnsignedByte() / 200F;
    }

    public void auxPressed(int i) {
        super.auxPressed(i);
        switch (i) {
            case 20:
                if (!this.APmode1) {
                    this.APmode1 = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: Altitude ON");
                    this.FM.AP.setStabAltitude(1000F);
                } else if (this.APmode1) {
                    this.APmode1 = false;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: Altitude OFF");
                    this.FM.AP.setStabAltitude(false);
                }
                break;
            case 21:
                if (!this.APmode2) {
                    this.APmode2 = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: Direction ON");
                    this.FM.AP.setStabDirection(true);
                    this.FM.CT.bHasRudderControl = false;
                } else if (this.APmode2) {
                    this.APmode2 = false;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: Direction OFF");
                    this.FM.AP.setStabDirection(false);
                    this.FM.CT.bHasRudderControl = true;
                }
                break;
            case 22:
                if (!this.APmode3) {
                    this.APmode3 = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: Route ON");
                    this.FM.AP.setWayPoint(true);
                } else if (this.APmode3) {
                    this.APmode3 = false;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: Route OFF");
                    this.FM.AP.setWayPoint(false);
                    this.FM.CT.AileronControl = 0.0F;
                    this.FM.CT.ElevatorControl = 0.0F;
                    this.FM.CT.RudderControl = 0.0F;
                }
                break;
            case 23:
                this.FM.CT.AileronControl = 0.0F;
                this.FM.CT.ElevatorControl = 0.0F;
                this.FM.CT.RudderControl = 0.0F;
                this.FM.AP.setWayPoint(false);
                this.FM.AP.setStabDirection(false);
                this.FM.AP.setStabAltitude(false);
                this.APmode1 = false;
                this.APmode2 = false;
                this.APmode3 = false;
                HUD.log(AircraftHotKeys.hudLogWeaponId, "Autopilot Mode: All Off");
                break;
            case 24: // Select all Prop Engines
                for (int engineIndex=0; engineIndex<this.FM.EI.engines.length; engineIndex++) this.FM.EI.setCurControl(engineIndex, engineIndex < 6);
                HUD.log(AircraftHotKeys.hudLogWeaponId, "Prop Engines Selected");
                break;
            case 25: // Select all Jet Engines
                for (int engineIndex=0; engineIndex<this.FM.EI.engines.length; engineIndex++) this.FM.EI.setCurControl(engineIndex, engineIndex > 5);
                HUD.log(AircraftHotKeys.hudLogWeaponId, "Jet Engines Selected");
                break;
            case 26:
                {
                    ArrayList propsReversed = new ArrayList();
                    ArrayList propsNormal = new ArrayList();
                    for (int engineIndex=0; engineIndex<3; engineIndex++) {
                        int engineLeftIndex = engineIndex;
                        int engineRightIndex = 5 - engineIndex;
                        if (!this.FM.EI.getCurControl(engineLeftIndex) && !this.FM.EI.getCurControl(engineRightIndex)) continue;
                        if (this.FM.EI.engines[engineLeftIndex].getControlFeather() != 0) engineLeftIndex = -1;
                        if (this.FM.EI.engines[engineRightIndex].getControlFeather() != 0) engineRightIndex = -1;
                        if (engineLeftIndex != -1 && !this.FM.EI.engines[engineLeftIndex].isHasControlProp()) engineLeftIndex = -1;
                        if (engineRightIndex != -1 && !this.FM.EI.engines[engineRightIndex].isHasControlProp()) engineRightIndex = -1;
                        if (engineLeftIndex != -1 && !this.FM.EI.engines[engineLeftIndex].isPropAngleDeviceOperational()) engineLeftIndex = -1;
                        if (engineRightIndex != -1 && !this.FM.EI.engines[engineRightIndex].isPropAngleDeviceOperational()) engineRightIndex = -1;
                        int engineIndices[] = {engineLeftIndex,engineRightIndex};
                        for (int curEngineIndex = 0; curEngineIndex<2; curEngineIndex++) {
                            if (engineIndices[curEngineIndex] == -1) continue;
                            Vector3f engineVector = this.FM.EI.engines[engineIndices[curEngineIndex]].getEngineVector();
                            engineVector.x *= -1F;
                            engineVector.normalize();
                            if (engineVector.x < 0F) {
                                engineVector.scale(0.15F);
                                propsReversed.add(new Integer(engineIndices[curEngineIndex] + 1));
                            } else {
                                propsNormal.add(new Integer(engineIndices[curEngineIndex] + 1));
                            }
                            Reflection.setValue(this.FM.EI.engines[engineIndices[curEngineIndex]], "engineVector", engineVector);
                        }
                    }
                    if (propsReversed.size() == 0 && propsNormal.size() == 0) {
                        HUD.log(AircraftHotKeys.hudLogPowerId, "Selected Prop(s) can't revert!");
                    } else {
                        Collections.sort(propsReversed);
                        Collections.sort(propsNormal);
                        String hudMessage = "Prop ";
//                        System.out.println("propsReversed.size()=" + propsReversed.size() + ", propsNormal.size()=" + propsNormal.size());
                        if (propsReversed.size() > 0) {
                            for (int propsIndex=0; propsIndex<propsReversed.size();propsIndex++) {
                                if (propsIndex>0) hudMessage += "+";
                                hudMessage += propsReversed.get(propsIndex);
                            }
                            hudMessage += " reversed";
                        }
                        if (propsNormal.size() > 0) {
                            if (propsReversed.size() > 0) hudMessage += ", ";
                            for (int propsIndex=0; propsIndex<propsNormal.size();propsIndex++) {
                                if (propsIndex>0) hudMessage += "+";
                                hudMessage += propsNormal.get(propsIndex);
                            }
                            hudMessage += " normal";
                        }
                        HUD.log(AircraftHotKeys.hudLogPowerId, hudMessage);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void addCockpitHotkey() {
        int i = 10;
        HotKeyCmdEnv.setCurrentEnv("misc");
        HotKeyCmdEnv hotKeyCmdEnv = (HotKeyCmdEnv) Reflection.getValue(RTSConf.cur.hotKeyCmdEnvs, "cur");
        HashMapExt cmds = (HashMapExt) Reflection.getValue(hotKeyCmdEnv, "cmds");
        if (cmds.containsKey("cockpitRealOn" + i)) {
            return;
        }

        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitRealOn" + i, null) {
            int indx;

            public void created() {
                this.indx = 10 + (Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0'));
                this.setRecordId(600 + this.indx);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
                if (!((Boolean) Reflection.invokeMethod(Main3D.cur3D().aircraftHotKeys, "isMiscValid")).booleanValue()) {
                    return;
                }
                AircraftHotKeys.setCockpitRealMode(this.indx, true);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitRealOff" + i, null) {
            int indx;

            public void created() {
                this.indx = 10 + (Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0'));
                this.setRecordId(610 + this.indx);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
                if (!((Boolean) Reflection.invokeMethod(Main3D.cur3D().aircraftHotKeys, "isMiscValid")).booleanValue()) {
                    return;
                }
                AircraftHotKeys.setCockpitRealMode(this.indx, false);
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitEnter" + i, null) {
            int indx;

            public void created() {
                this.indx = 10 + (Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0'));
                this.setRecordId(620 + this.indx);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
                if (!((Boolean) Reflection.invokeMethod(Main3D.cur3D().aircraftHotKeys, "isMiscValid")).booleanValue()) {
                    return;
                }
                if ((Main3D.cur3D().cockpits != null) && (this.indx < Main3D.cur3D().cockpits.length)) {
                    World.getPlayerAircraft().FM.AS.astatePlayerIndex = Main3D.cur3D().cockpits[this.indx].astatePilotIndx();
                    if (!NetMissionTrack.isPlaying()) {
                        Aircraft localAircraft = World.getPlayerAircraft();
                        if (World.isPlayerGunner()) {
                            localAircraft.netCockpitEnter(World.getPlayerGunner(), this.indx);
                        } else {
                            localAircraft.netCockpitEnter(localAircraft, this.indx);
                        }
                    }
                }
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitLeave" + i, null) {
            int indx;

            public void created() {
                this.indx = 10 + (Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0'));
                this.setRecordId(630 + this.indx);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
                if (!((Boolean) Reflection.invokeMethod(Main3D.cur3D().aircraftHotKeys, "isMiscValid")).booleanValue()) {
                    return;
                }
                if ((Main3D.cur3D().cockpits != null) && (this.indx < Main3D.cur3D().cockpits.length) && ((Main3D.cur3D().cockpits[this.indx] instanceof CockpitGunner)) && (AircraftHotKeys.isCockpitRealMode(this.indx))) {
                    ((CockpitGunner) Main3D.cur3D().cockpits[this.indx]).hookGunner().gunFire(false);
                }
            }
        });
        HotKeyCmdEnv.setCurrentEnv("aircraftView");
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitView" + i, "" + i) {
            int indx;

            public void created() {
                this.indx = 10 + (Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0'));
            }

            public void begin() {
                if (!((Boolean) Reflection.invokeMethod(Main3D.cur3D().aircraftHotKeys, "isMiscValid")).booleanValue()) {
                    return;
                }
                new MsgAction(true, new Integer(this.indx)) {
                    public void doAction(Object paramAnonymous2Object) {
                        int i = ((Integer) paramAnonymous2Object).intValue();
                        HotKeyCmd.exec("aircraftView", "cockpitSwitch" + i);
                    }
                };
            }
        });
        HotKeyCmdEnv.addCmd(new HotKeyCmd(true, "cockpitSwitch" + i, null) {
            int indx;

            public void created() {
                this.indx = 10 + (Character.getNumericValue(this.name().charAt(this.name().length() - 1)) - Character.getNumericValue('0'));
                this.setRecordId(640 + this.indx);
                HotKeyEnv.currentEnv().remove(this.sName);
            }

            public void begin() {
                if ((Main3D.cur3D().cockpitCurIndx() == this.indx) && (!Main3D.cur3D().isViewOutside())) {
                    return;
                }
                Reflection.invokeMethod(Main3D.cur3D().aircraftHotKeys, "switchToCockpit", new Class[] { int.class }, new Object[] { new Integer(this.indx) });
            }
        });
    }
    
    public static boolean checkTurretVisibilityPlaneBlocking(int turretIndex) {
        return false;
    }

    public boolean            APmode1;
    public boolean            APmode2;
    public boolean            APmode3;
    public static boolean     bChangedPit                   = false;
    public boolean            bToFire;
    protected float           deltaAzimuth;
    protected float           deltaTangage;
    protected boolean         isGuidingBomb;
    protected boolean         isMasterAlive;
    private boolean           bSightAutomation;
    private boolean           bSightBombDump;
    private float             fSightCurDistance;
    public float              fSightCurForwardAngle;
    public float              fSightCurSideslip;
    public float              fSightCurAltitude;
    public float              fSightCurSpeed;
    public float              fSightCurReadyness;
    static final float        calibrationScale[]            = { 0.0F, 0.2F, 0.4F, 0.66F, 0.86F, 1.05F, 1.2F, 1.6F };
    public float              fSightSetForwardAngle;
    static String             B36Props[][];
    private float             steering;
    float                     rndgear[]                     = { 0.0F, 0.0F, 0.0F };
    static float              rndgearnull[]                 = { 0.0F, 0.0F, 0.0F };
    private float             prevFuel;
    private boolean           aiStartPropPitchSet;
    private boolean           aiStartMixtureSet;
    private float             gearAngle;
    private long              brakingtimer;
    boolean                   isEquippedWithTurrets;
    private boolean           turretReadyToAnimate[]        = { true, true, true, true, true, true };
    private float             turretAnimatedPosition[]      = { 1F, 1F, 1F, 1F, 1F, 1F };
    private long              lastTurretActivationRequested = 0L;
    private static final long TURRET_ACTIVATED_DURATION     = 30000L;
    private boolean           initialMixtureSet;
    private int               initialCompressorSettingsLeft;
    private static int[] maneuverValidForJetStartAndShutdown = {0,1,3,9,11,12,13,14,21,24,25,45,48,49,53,54,86,105};
    private static int[] maneuverDontTouchJets = {26,64,66,102};
    private boolean jetPowerOn[] = {true, true, true, true};
    private long lastJetPowerConfirmed[] = {-1L, -1L, -1L, -1L};
    private static long JET_POWER_TOGGLE_TIMEOUT = 60000L;
    private float             lastAoA = 0F;
    private float             targetElevator = 0F;
    private float             targetBrakePower = 0F;
    private boolean           needsBrakeRelease = false;
    private static final float AI_LANDING_BRAKE_POWER = 0.2F;
    
    static {
        B_36X.B36Props = new String[10][3];
        for (int i = 0; i < 10; i++) {
            B_36X.B36Props[i][0] = "Prop" + (i + 1) + "_D0";
            B_36X.B36Props[i][1] = "PropRot" + (i + 1) + "_D0";
            B_36X.B36Props[i][2] = "Prop" + (i + 1) + "_D1";
        }

        Class class1 = B_36X.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
    }
}
