package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.Polares;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.MsgAction;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.BaseGameVersion;
import com.maddox.sas1946.il2.util.Reflection;

public class XF5U1 extends Scheme2a implements TypeFighter, TypeBNZFighter, TypeStormovik {

    public XF5U1() {
        this.bChangedPit = true;
        this.steera = 0F;
        this.overrideBailout = false;
        this.ejectComplete = false;
        this.ejectTime = 0L;
        this.blisterRemoved = false;
        this.xf5uInit();
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.xf5uOnAircraftLoaded();
    }

    public float getEyeLevelCorrection() {
        return -0.1F;
    }

    protected void nextDMGLevel(String s, int i, Actor actor) {
        super.nextDMGLevel(s, i, actor);
        if (this.FM.isPlayers()) {
            this.bChangedPit = true;
        }
    }

    protected void nextCUTLevel(String s, int i, Actor actor) {
        super.nextCUTLevel(s, i, actor);
        if (this.FM.isPlayers()) {
            this.bChangedPit = true;
        }
    }

    protected void moveFlap(float f) {
    }

    protected void moveAirBrake(float f) {
    }

    protected void moveRudder(float f) {
        this.xf5uMoveRudder(f);
    }

    protected void moveElevator(float f) {
        this.xf5uMoveElevator(f);
    }

    protected void moveAileron(float f) {
        this.xf5uMoveAileron(f);
    }

    // New Gear Animation Code,
    public static void moveGear(HierMesh hiermesh, float leftGear, float rightGear, float tailGear) {
        ypr[0] = ypr[1] = ypr[2] = xyz[0] = xyz[1] = xyz[2] = 0.0F;
        ypr[1] = cvt(tailGear, 0.20F, 0.99F, 0.0F, -75F);
        xyz[0] = cvt(tailGear, 0.20F, 0.99F, 0.0F, 0.5F);
        hiermesh.chunkSetLocate("GearC2_D0", xyz, ypr);
        hiermesh.chunkSetAngles("GearC6_D0", 0.0F, cvt(tailGear, 0.01F, 0.25F, 0.0F, -95F), 0.0F);
        hiermesh.chunkSetAngles("GearC8_D0", 0.0F, cvt(tailGear, 0.01F, 0.25F, 0.0F, -95F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, cvt(leftGear, 0.22F, 0.93F, 0.0F, -100F), 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, cvt(leftGear, 0.22F, 0.93F, 0.0F, -40F), 0.0F);
        hiermesh.chunkSetAngles("GearL8_D0", 0.0F, cvt(leftGear, 0.01F, 0.29F, 0.0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("GearL9_D0", 0.0F, cvt(leftGear, 0.01F, 0.29F, 0.0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, cvt(rightGear, 0.29F, 0.99F, 0.0F, -100F), 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, cvt(rightGear, 0.29F, 0.99F, 0.0F, -40F), 0.0F);
        hiermesh.chunkSetAngles("GearR8_D0", 0.0F, cvt(rightGear, 0.08F, 0.36F, 0.0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("GearR9_D0", 0.0F, cvt(rightGear, 0.08F, 0.36F, 0.0F, -80F), 0.0F);
    }

    protected void moveGear(float f, float f1, float f2) {
        this.steera = 0.0F;
        this.moveWheelSink();
        moveGear(this.hierMesh(), f, f1, f2);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float gearPos) {
        moveGear(hiermesh, gearPos, gearPos, gearPos); // re-route old style function calls to new code
    }

    protected void moveGear(float gearPos) {
        moveGear(this.hierMesh(), gearPos);
    }
    // ************************************************************************************************

    public void moveSteering(float f) {
        this.steera = (0.9F * this.steera) + (0.1F * cvt(f, -50F, 50F, 50F, -50F));
        this.moveWheelSink();
    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        xyz[1] = cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.24F, 0.0F, 0.2406F);
        ypr[1] = this.steera;
        this.hierMesh().chunkSetLocate("GearC3_D0", xyz, ypr);
        this.hierMesh().chunkSetAngles("GearC4_D0", 0.0F, cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.24F, 0.0F, -50F), 0.0F);
        this.hierMesh().chunkSetAngles("GearC5_D0", 0.0F, cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.24F, 0.0F, -105F), 0.0F);
        this.resetYPRmodifier();
        xyz[1] = cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.32F, 0.0F, -0.3206F);
        this.hierMesh().chunkSetLocate("GearL3_D0", xyz, ypr);
        this.hierMesh().chunkSetAngles("GearL4_D0", 0.0F, cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.32F, 0.0F, -60F), 0.0F);
        this.hierMesh().chunkSetAngles("GearL5_D0", 0.0F, cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.32F, 0.0F, -117.5F), 0.0F);
        this.resetYPRmodifier();
        xyz[1] = cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.32F, 0.0F, 0.3206F);
        this.hierMesh().chunkSetLocate("GearR3_D0", xyz, ypr);
        this.hierMesh().chunkSetAngles("GearR4_D0", 0.0F, cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.32F, 0.0F, -60F), 0.0F);
        this.hierMesh().chunkSetAngles("GearR5_D0", 0.0F, cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.32F, 0.0F, -117.5F), 0.0F);
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[0] = f * 0.6F;
        Aircraft.xyz[2] = f * 0.1F;
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    protected void moveFan(float f) {
        this.xf5uMoveFan(f);
    }

    public void hitProp(int i, int j, Actor actor) {
        this.xf5uHitProp(i, j, actor);
    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                if (!this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot1_D0", false);
                    this.hierMesh().chunkVisible("Head1_D0", false);
                    this.hierMesh().chunkVisible("Pilot1_D1", true);
                }
                break;
        }
    }

    public void update(float f) {
        this.xf5uUpdatePrefix(f);
        super.update(f);
        this.xf5uUpdatePostfix(f);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.endsWith("1")) {
                    this.getEnergyPastArmor(World.Rnd().nextFloat(20F, 30F), shot);
                    debugprintln(this, "*** Armor Glass: Hit..");
                    if (shot.power <= 0.0F) {
                        debugprintln(this, "*** Armor Glass: Bullet Stopped..");
                        if (World.Rnd().nextFloat() < 0.5F) {
                            this.doRicochetBack(shot);
                        }
                    } else {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    }
                }
                if (s.endsWith("2")) {
                    this.getEnergyPastArmor(12.7F / (1E-005F + (float) Math.abs(v1.x)), shot);
                }
                if (s.endsWith("3")) {
                    this.getEnergyPastArmor(12.7F / (1E-005F + (float) Math.abs(v1.x)), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
                }
                if (s.endsWith("4")) {
                    this.getEnergyPastArmor(8.9F / (1E-005F + (float) Math.abs(v1.x)), shot);
                }
                if (s.endsWith("5")) {
                    this.getEnergyPastArmor(8.9F / (1E-005F + (float) Math.abs(v1.z)), shot);
                }
                return;
            }
            if (s.startsWith("xxarcon")) {
                if ((World.Rnd().nextFloat() < 0.5F) && (this.getEnergyPastArmor(0.1F, shot) > 0.0F)) {
                    this.FM.AS.setControlsDamage(shot.initiator, 0);
                    debugprintln(this, "*** Ailerones Controls Out..");
                }
                return;
            }
            if (s.startsWith("xxvatcon")) {
                if ((World.Rnd().nextFloat() < 0.5F) && (this.getEnergyPastArmor(0.1F, shot) > 0.0F)) {
                    this.FM.AS.setControlsDamage(shot.initiator, 1);
                    debugprintln(this, "*** Elevators Controls Out..");
                }
                return;
            }
            if (s.startsWith("xxrudcon")) {
                if ((World.Rnd().nextFloat() < 0.5F) && (this.getEnergyPastArmor(0.1F, shot) > 0.0F)) {
                    this.FM.AS.setControlsDamage(shot.initiator, 2);
                    debugprintln(this, "*** Rudder Controls Out..");
                }
                return;
            }
            if (s.startsWith("xxeng1")) {
                if ((this.getEnergyPastArmor(4.45F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[0].getCylindersRatio() * 0.75F))) {
                    this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 6800F)));
                    debugprintln(this, "*** Engine 0 Cylinders Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                    if (this.FM.AS.astateEngineStates[0] < 1) {
                        this.FM.AS.hitEngine(shot.initiator, 0, 1);
                        this.FM.AS.doSetEngineState(shot.initiator, 0, 1);
                    }
                    if (World.Rnd().nextFloat() < (shot.power / 48000F)) {
                        this.FM.AS.hitEngine(shot.initiator, 0, 3);
                        debugprintln(this, "*** Engine 0 Cylinders Hit - Engine Fires..");
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                return;
            }
            if (s.startsWith("xxeng2")) {
                if ((this.getEnergyPastArmor(4.45F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[1].getCylindersRatio() * 0.75F))) {
                    this.FM.EI.engines[1].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 6800F)));
                    debugprintln(this, "*** Engine 1 Cylinders Hit, " + this.FM.EI.engines[1].getCylindersOperable() + "/" + this.FM.EI.engines[1].getCylinders() + " Left..");
                    if (this.FM.AS.astateEngineStates[1] < 1) {
                        this.FM.AS.hitEngine(shot.initiator, 1, 1);
                        this.FM.AS.doSetEngineState(shot.initiator, 1, 1);
                    }
                    if (World.Rnd().nextFloat() < (shot.power / 48000F)) {
                        this.FM.AS.hitEngine(shot.initiator, 1, 3);
                        debugprintln(this, "*** Engine 1 Cylinders Hit - Engine Fires..");
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                return;
            }
            if (s.startsWith("xxoilradiat1")) {
                if ((this.getEnergyPastArmor(0.45F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    debugprintln(this, "*** Engine Module 0: Oil Radiator Hit..");
                }
                return;
            }
            if (s.startsWith("xxoilradiat2")) {
                if ((this.getEnergyPastArmor(0.45F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                    this.FM.AS.hitOil(shot.initiator, 1);
                    debugprintln(this, "*** Engine Module 1: Oil Radiator Hit..");
                }
                return;
            }
            if (s.startsWith("xxoiltank1")) {
                if ((this.getEnergyPastArmor(2.38F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    debugprintln(this, "*** Engine Module 0: Oil Radiator Hit..");
                }
                return;
            }
            if (s.startsWith("xxoiltank2")) {
                if ((this.getEnergyPastArmor(2.38F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                    this.FM.AS.hitOil(shot.initiator, 1);
                    debugprintln(this, "*** Engine Module 1: Oil Radiator Hit..");
                }
                return;
            }
            if (s.startsWith("xxmagneto1")) {
                int i = World.Rnd().nextInt(0, 1);
                this.FM.EI.engines[0].setMagnetoKnockOut(shot.initiator, i);
                debugprintln(this, "*** Engine Module 0: Magneto " + i + " Destroyed..");
                return;
            }
            if (s.startsWith("xxmagneto2")) {
                int j = World.Rnd().nextInt(0, 1);
                this.FM.EI.engines[1].setMagnetoKnockOut(shot.initiator, j);
                debugprintln(this, "*** Engine Module 1: Magneto " + j + " Destroyed..");
                return;
            }
            if (s.startsWith("xxturbo1")) {
                if (this.getEnergyPastArmor(1.23F, shot) > 0.0F) {
                    this.FM.EI.engines[0].setKillCompressor(shot.initiator);
                    debugprintln(this, "*** Engine Module 0: Supercharger Destroyed..");
                }
                return;
            }
            if (s.startsWith("xxturbo2")) {
                if (this.getEnergyPastArmor(1.23F, shot) > 0.0F) {
                    this.FM.EI.engines[1].setKillCompressor(shot.initiator);
                    debugprintln(this, "*** Engine Module 1: Supercharger Destroyed..");
                }
                return;
            }
            if (s.startsWith("xxradiat")) {
                int k = 0;
                if (s.endsWith("3") || s.endsWith("4")) {
                    k = 1;
                }
                if (World.Rnd().nextFloat() < 0.25F) {
                    if (this.FM.AS.astateEngineStates[k] == 0) {
                        this.debuggunnery("Engine Module: Water Radiator Pierced..");
                        this.FM.AS.hitEngine(shot.initiator, k, 1);
                        this.FM.AS.doSetEngineState(shot.initiator, k, 1);
                    } else if (this.FM.AS.astateEngineStates[k] == 1) {
                        this.debuggunnery("Engine Module: Water Radiator Pierced..");
                        this.FM.AS.hitEngine(shot.initiator, k, 1);
                        this.FM.AS.doSetEngineState(shot.initiator, k, 2);
                    }
                }
                return;
            }
            if (s.startsWith("xxtank")) {
                byte byte0 = 0;
                int j1 = s.charAt(6) - 48;
                switch (j1) {
                    case 1:
                        byte0 = 1;
                        break;

                    case 2:
                        byte0 = 1;
                        break;

                    case 3:
                        byte0 = 0;
                        break;

                    case 4:
                        byte0 = 2;
                        break;

                    case 5:
                        byte0 = 2;
                        break;

                    case 6:
                        byte0 = 3;
                        break;
                }
                if ((this.getEnergyPastArmor(0.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                    this.FM.AS.hitTank(shot.initiator, byte0, 1);
                    if ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.11F)) {
                        this.FM.AS.hitTank(shot.initiator, byte0, 2);
                    }
                }
                return;
            }
            if (s.startsWith("xxgun")) {
                int l = s.charAt(5) - 49;
                this.FM.AS.setJamBullets(0, l);
                this.getEnergyPastArmor(23.5F, shot);
                return;
            }
            if (s.startsWith("xxcannon")) {
                this.FM.AS.setJamBullets(1, 0);
                this.getEnergyPastArmor(48.6F, shot);
                return;
            }
            if (s.startsWith("xxammogun")) {
                int i1 = World.Rnd().nextInt(0, 3);
                this.FM.AS.setJamBullets(0, i1);
                this.getEnergyPastArmor(23.5F, shot);
                return;
            }
            if (s.startsWith("xxammocan")) {
                this.FM.AS.setJamBullets(1, 0);
                this.getEnergyPastArmor(23.5F, shot);
                return;
            }
            if (s.startsWith("xxspar")) {
                debugprintln(this, "*** Spar Construction: Hit..");
                if (s.startsWith("xxsparli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(19.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(19.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlm") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparrm") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if (s.startsWith("xxpark1") && (this.chunkDamageVisible("Keel1") > 1) && (this.getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** Keel1 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D2", shot.initiator);
                }
                if (s.startsWith("xxpark2") && (this.chunkDamageVisible("Keel2") > 1) && (this.getEnergyPastArmor(9.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** Keel2 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Keel2_D2", shot.initiator);
                }
                if (s.startsWith("xxsparsl") && (this.chunkDamageVisible("StabL") > 1) && (this.getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** Stab Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D2", shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxlock")) {
                debugprintln(this, "*** Lock Construction: Hit..");
                if ((s.startsWith("xxlockk1") || s.startsWith("xxlockk2")) && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** Rudder1 Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if ((s.startsWith("xxlockk3") || s.startsWith("xxlockk4")) && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** Rudder2 Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "Rudder2_D" + this.chunkDamageVisible("Rudder2"), shot.initiator);
                }
                if (s.startsWith("xxlocksl") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** Vator Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                }
                if (s.startsWith("xxlockal") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** AroneL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
                }
                if (s.startsWith("xxlockar") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    debugprintln(this, "*** AroneR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
                }
                return;
            } else {
                return;
            }
        }
        if (s.startsWith("xcf") || s.startsWith("xcockpit")) {
            this.hitChunk("CF", shot);
            if ((point3d.x > 0.0D) && (point3d.x < 1.0D)) {
                if (point3d.y > 0.0D) {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                } else {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                }
            }
        }
        if (s.startsWith("xblister")) {
            if (point3d.y > 0.0D) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            } else {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            }
        } else if (s.startsWith("xengine1")) {
            this.hitChunk("Engine1", shot);
        } else if (s.startsWith("xengine2")) {
            this.hitChunk("Engine2", shot);
        } else if (s.startsWith("xtail1")) {
            this.hitChunk("Tail1", shot);
        } else if (s.startsWith("xtail2")) {
            this.hitChunk("Tail2", shot);
        } else if (s.startsWith("xkeel1")) {
            this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xkeel2")) {
            this.hitChunk("Keel2", shot);
        } else if (s.startsWith("xrudder1")) {
            this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xrudder2")) {
            this.hitChunk("Rudder2", shot);
        } else if (s.startsWith("xstabl")) {
            this.hitChunk("StabL", shot);
        } else if (s.startsWith("xvatorl")) {
            if (this.chunkDamageVisible("VatorL") < 1) {
                this.hitChunk("VatorL", shot);
            }
        } else if (s.startsWith("xwing")) {
            if (s.startsWith("xwinglin") && (this.chunkDamageVisible("WingLIn") < 3)) {
                this.hitChunk("WingLIn", shot);
            }
            if (s.startsWith("xwingrin") && (this.chunkDamageVisible("WingRIn") < 3)) {
                this.hitChunk("WingRIn", shot);
            }
            if (s.startsWith("xwinglmid") && (this.chunkDamageVisible("WingLMid") < 3)) {
                this.hitChunk("WingLMid", shot);
            }
            if (s.startsWith("xwingrmid") && (this.chunkDamageVisible("WingRMid") < 3)) {
                this.hitChunk("WingRMid", shot);
            }
            if (s.startsWith("xwinglout") && (this.chunkDamageVisible("WingLOut") < 3)) {
                this.hitChunk("WingLOut", shot);
            }
            if (s.startsWith("xwingrout") && (this.chunkDamageVisible("WingROut") < 3)) {
                this.hitChunk("WingROut", shot);
            }
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel")) {
                this.hitChunk("AroneL", shot);
            }
            if (s.startsWith("xaroner")) {
                this.hitChunk("AroneR", shot);
            }
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte1 = 0;
            int k1;
            if (s.endsWith("a")) {
                byte1 = 1;
                k1 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte1 = 2;
                k1 = s.charAt(6) - 49;
            } else {
                k1 = s.charAt(5) - 49;
            }
            this.hitFlesh(k1, shot, byte1);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 17:
                this.FM.cut(17, j, actor);
                this.FM.cut(18, j, actor);
                break;

            case 31:
                this.FM.cut(31, j, actor);
                this.FM.cut(32, j, actor);
                break;

        }
        return super.cutFM(i, j, actor);
    }

    // Overridden "cut" method to avoid cutting inner wing meshes
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public boolean cut(String partName) {
        return this.xf5uCut(partName);
    }
    // --------------------------------------------------------------------------------------------------

    private final void doRemoveBlister() {
        if (this.blisterRemoved) {
            return;
        }
        if ((this.hierMesh().chunkFindCheck("Blister1_D0") != -1) && (this.FM.AS.getPilotHealth(0) > 0.0F)) {
            this.hierMesh().hideSubTrees("Blister1_D0");
            Wreckage localWreckage = new Wreckage(this, this.hierMesh().chunkFind("Blister1_D0"));
            localWreckage.collide(false);
            Vector3d localVector3d = new Vector3d();
            localVector3d.set(this.FM.Vwld);
            localWreckage.setSpeed(localVector3d);
        }
        this.blisterRemoved = true;
    }

    private void checkBailout(float f) {
        if ((this.FM.AS.bIsAboutToBailout || this.overrideBailout) && !this.ejectComplete && !this.FM.isStationedOnGround()) {
            this.overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            this.xf5uBailout();
        }
    }

    /*
     * Vectoring Code
     * Includes Animations for:
     * - Elevator / Aileron (Elevon)
     * - Prop Blades
     * - Prop Base
     * Includes Auto Prop Shaft Clutch for single engine operation
     * Includes Auto Feather Prop
     */

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void xf5uInit() {
        this.fElevator = 0F;
        this.fAileron = 0F;
        this.fAileronLeftFactor = 1F;
        this.fAileronRightFactor = 1F;
        this.fAileronLeft = 0F;
        this.fAileronRight = 0F;
        this.fRudder = 0F;
        this.propShaftClutchEngaged = false;
        this.propShaftClutchArmed = false;
        this.masterEngineIndex = 0;
        this.fPropAngle = new float[2];
        this.fPropFactor = new float[2];
        this.lastPropPitch = new float[2];
        this.lastPropW = new float[2];
        this.propPosSeparate = new Point3f[2];
        this.propPosClutched = new Point3f[2];
        this.engineVector = new Vector3f();
        this.engineVector.set(1.0F, 0.0F, 0.0F);
        for (int i = 0; i < 2; i++) {
            this.fPropAngle[i] = 0F;
            this.fPropFactor[i] = 0F;
            this.lastPropPitch[i] = 0F;
            this.lastPropW[i] = 0F;
            this.propPosSeparate[i] = new Point3f();
            this.propPosClutched[i] = new Point3f();
        }
    }

    private void xf5uUpdatePrefix(float f) {
        this.checkBailout(f);
        this.xf5uUpdateAI(f);
    }

    private void xf5uUpdatePostfix(float f) {
        this.xf5uUpdatePitchSens(f);
        this.xf5uMovePropVector(f);
        this.xf5uMovePropPitch(f);
        this.xf5uUpdatePropShaftClutchState(f);
    }

    private void xf5uUpdateAI(float f) {
        boolean isControlledByAI = true;
        if (this.FM.isPlayers() && (this.FM instanceof RealFlightModel)) {
            isControlledByAI = !((RealFlightModel) this.FM).RealMode;
        }
        this.FM.Vmin = 0.27778F * (isControlledByAI ? 150F : 20F);
        this.FM.VminFLAPS = this.FM.Vmin;
        Polares wing = null;
        if (BaseGameVersion.is411orLater())
            wing = this.FM.getWing();
        else
            wing = (Polares)Reflection.getValue(this.FM, "Wing");
        wing.V_min = this.FM.Vmin;
        wing.V_land = this.FM.Vmin;
        if (isControlledByAI) {
            Maneuver maneuver = (Maneuver) this.FM;
           if (maneuver.get_maneuver() == Maneuver.TAKEOFF || (maneuver.get_maneuver() == Maneuver.LANDING && this.FM.getSpeedKMH() > 50F)) {
                if (!this.FM.Gears.bTailwheelLocked) {
                    this.FM.Gears.bTailwheelLocked = true;
                    if (this == World.getPlayerAircraft()) {
                        HUD.log("TailwheelLockON");
                    }
                }
            } else {
                if (this.FM.Gears.bTailwheelLocked) {
                    this.FM.Gears.bTailwheelLocked = false;
                    if (this == World.getPlayerAircraft()) {
                        HUD.log("TailwheelLockOFF");
                    }
                }
            }
        }
    }

    private void xf5uUpdatePitchSens(float f) {
        if (this.FM.isPlayers() && (this.FM.Sq.squareElevators > 0.0F)) {
            RealFlightModel realflightmodel = (RealFlightModel) this.FM;
            float mach = Reflection.getFloat(realflightmodel, "Mach");
            if (realflightmodel.RealMode && (mach < 0.15F)) {
                this.FM.SensPitch = cvt(mach, 0F, 0.15F, 2.0F, 0.63F);
            } else {
                this.FM.SensPitch = 0.63F;
            }
        }
    }

    private void xf5uOnAircraftLoaded() {
        if ((this.FM.EI.engines[0].getStage() == 6) && (this.FM.EI.engines[1].getStage() == 6)) {
            this.propShaftClutchArmed = true;
        }
        for (int i = 0; i < this.FM.EI.getNum(); i++) {
            this.propPosSeparate[i].set(this.FM.EI.engines[i].getPropPos());
            this.propPosClutched[i].set(this.propPosSeparate[i].x, 0.0F, this.propPosSeparate[i].z);
        }
    }

    private void xf5uMoveFan(float f) {
        int i = 0;
        for (int j = 0; j < this.FM.EI.getNum(); j++) {
            float engineW = this.FM.EI.engines[j].getw();
            if (this.propShaftClutchEngaged && (j != this.masterEngineIndex)) {
                engineW = this.FM.EI.engines[this.masterEngineIndex].getw();
            }

            if (Math.abs(this.lastPropW[j] - engineW) < PROP_W_DIFF_MAX) {
                this.lastPropW[j] = engineW;
            } else if (this.lastPropW[j] > engineW) {
                this.lastPropW[j] -= PROP_W_DIFF_MAX;
            } else {
                this.lastPropW[j] += PROP_W_DIFF_MAX;
            }

            if (this.oldProp[j] < 2) {
                i = Math.abs((int) (this.lastPropW[j] * 0.06F));
                if (i >= 1) {
                    i = 1;
                }
                if ((i != this.oldProp[j]) && this.hierMesh().isChunkVisible(Props[j][this.oldProp[j]][0])) {
                    for (int k = 0; k < Props[j][this.oldProp[j]].length; k++) {
                        this.hierMesh().chunkVisible(Props[j][this.oldProp[j]][k], false);
                    }
                    this.oldProp[j] = i;
                    for (int k = 0; k < Props[j][i].length; k++) {
                        this.hierMesh().chunkVisible(Props[j][i][k], true);
                    }
                }
            }
            if (i == 0) {
                this.propPos[j] = (this.propPos[j] + (57.3F * this.lastPropW[j] * f)) % 360F;
            } else {
                float f1 = 57.3F * this.lastPropW[j];
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
            for (int k = 0; k < 2; k++) {
                this.hierMesh().chunkSetAngles(Props[j][0][k], 0.0F, -this.propPos[j], 0.0F);
            }
        }

    }

    private void xf5uHitProp(int i, int j, Actor actor) {
        if ((i > (this.FM.EI.getNum() - 1)) || (this.oldProp[i] == 2)) {
            return;
        }
        super.hitProp(i, j, actor);
        this.FM.cut(this.part("Engine" + (i + 1)), j, actor);
        if (this.isChunkAnyDamageVisible("Prop" + (i + 1)) || this.isChunkAnyDamageVisible("PropRot" + (i + 1))) {
            for (int k = 0; j < Props[i][0].length; k++) {
                this.hierMesh().chunkVisible(Props[i][0][k], false);
            }
            for (int k = 0; j < Props[i][1].length; k++) {
                this.hierMesh().chunkVisible(Props[i][1][k], false);
            }
            for (int k = 0; j < Props[i][2].length; k++) {
                this.hierMesh().chunkVisible(Props[i][2][k], true);
            }
        }
        this.FM.EI.engines[i].setFricCoeffT(1.0F);
        this.oldProp[i] = 2;
    }

    private void xf5uMovePropPitch(float f) {
        for (int i = 0; i < this.FM.EI.getNum(); i++) {
            if (!this.FM.EI.engines[i].isPropAngleDeviceOperational()) {
                continue;
            }
            float propPitch = this.FM.EI.engines[this.propShaftClutchEngaged ? this.masterEngineIndex : i].getControlProp();
            if (this.FM.EI.engines[this.propShaftClutchEngaged ? this.masterEngineIndex : i].getControlFeather() == 1) {
                propPitch = -0.2F;
            }
            float propPitchDiff = propPitch - this.lastPropPitch[i];
            if (Math.abs(propPitchDiff) > PROP_PITCH_DIFF_MAX) {
                if (propPitchDiff > 0.0F) {
                    propPitch = this.lastPropPitch[i] + PROP_PITCH_DIFF_MAX;
                } else {
                    propPitch = this.lastPropPitch[i] - PROP_PITCH_DIFF_MAX;
                }
            }
            for (int j = 2; j < Props[i][0].length; j++) {
                float curBladeAngleRad = (float) Math.toRadians(((PropCyclicFactors[i][0] * this.propPos[i]) + PropCyclicFactors[i][j - 1]) % 360F);
                this.hierMesh().chunkSetAngles(Props[i][0][j], cvt(propPitch + ((this.FM.EI.engines[i].getControlFeather() == 1) ? 0F : (((float) Math.cos(curBladeAngleRad) * this.fElevator) + ((float) Math.sin(curBladeAngleRad) * -1F * this.fRudder)) * PROP_PITCH_CYCLIC_FACTOR), PropAngles[i][0], PropAngles[i][1], PropAngles[i][2], PropAngles[i][3]), 0.0F, 0.0F);
            }
            this.lastPropPitch[i] = propPitch;
        }
    }

    private void xf5uSetNewPropPos(boolean clutched) {
        for (int i = 0; i < this.FM.EI.getNum(); i++) {
            this.FM.EI.engines[i].setPropPos(new Point3d(clutched ? this.propPosClutched[i] : this.propPosSeparate[i]));
        }
    }

    private void xf5uUpdatePropShaftClutchState(float f) {
        boolean oldPropShaftClutchEngaged = this.propShaftClutchEngaged;
        boolean oldPropShaftClutchArmed = this.propShaftClutchArmed;
        boolean engineRunning[] = { this.FM.EI.engines[0].getStage() == 6, this.FM.EI.engines[1].getStage() == 6 };
        if (engineRunning[0] && engineRunning[1]) {
            this.propShaftClutchArmed = true;
        } else if (!engineRunning[0] && !engineRunning[1]) {
// if (this.propShaftClutchEngaged) {
// Reflection.setInt(this.FM.EI.engines[this.masterEngineIndex == 0 ? 1 : 0], "controlFeather", 1);
// }
            this.propShaftClutchArmed = false;
            this.propShaftClutchEngaged = false;
        }
        if (oldPropShaftClutchArmed != this.propShaftClutchArmed) {
            if (this == World.getPlayerAircraft()) {
                HUD.log("Prop Shaft Clutch " + (!this.propShaftClutchArmed ? "un" : "") + "armed!");
            }
        }
        if (!this.propShaftClutchArmed) {
            if (oldPropShaftClutchEngaged != this.propShaftClutchEngaged) {
                if (this == World.getPlayerAircraft()) {
                    HUD.log("Prop Shaft Clutch " + (!this.propShaftClutchEngaged ? "dis" : "") + "engaged!");
                }
                this.xf5uSetNewPropPos(this.propShaftClutchEngaged);
            }
            return;
        }
        if (engineRunning[0] == engineRunning[1]) {
            this.propShaftClutchEngaged = false;
        } else {
            this.propShaftClutchEngaged = true;
            this.masterEngineIndex = engineRunning[0] ? 0 : 1;
        }
        if (oldPropShaftClutchEngaged != this.propShaftClutchEngaged) {
            if (this == World.getPlayerAircraft()) {
                HUD.log("Prop Shaft Clutch " + (!this.propShaftClutchEngaged ? "dis" : "") + "engaged!");
            }
            this.xf5uSetNewPropPos(this.propShaftClutchEngaged);
        }
    }

    private void xf5uMovePropVector(float f) {
        float fPropNew = 0F;
        for (int i = 0; i < 2; i++) {
            switch (i) {
                case 0:
                    fPropNew = this.fElevator;
                    break;
                case 1:
                    fPropNew = this.fRudder;
                    break;
            }
            if (Math.abs(this.fPropAngle[i] - fPropNew) < PROP_DIFF_MAX) {
                this.fPropAngle[i] = fPropNew;
            } else if (this.fPropAngle[i] > fPropNew) {
                this.fPropAngle[i] -= PROP_DIFF_MAX;
            } else {
                this.fPropAngle[i] += PROP_DIFF_MAX;
            }
        }

        float fY = (float) Math.toRadians(PROP_ANGLE_MAX * this.fPropAngle[1]);
        float fZ = (float) Math.toRadians(PROP_ANGLE_MAX * this.fPropAngle[0]);
        for (int i = 0; i < this.FM.EI.getNum(); i++) {
            float fPropNewFactor = Math.min(1.0F, Math.abs(this.lastPropW[this.propShaftClutchEngaged ? this.masterEngineIndex : i] * 0.1F)); // Prop pitch vectoring effect only works when RPM reaches a certain level
            if (Math.abs(this.fPropFactor[i] - fPropNewFactor) < PROP_FACTOR_DIFF_MAX) {
                this.fPropFactor[i] = fPropNewFactor;
            } else if (this.fPropFactor[i] > fPropNewFactor) {
                this.fPropFactor[i] -= PROP_FACTOR_DIFF_MAX;
            } else {
                this.fPropFactor[i] += PROP_FACTOR_DIFF_MAX;
            }
            this.engineVector.set((float) (Math.cos(fY * this.fPropFactor[i]) * Math.cos(fZ * this.fPropFactor[i])), (float) -Math.sin(fY * this.fPropFactor[i]), (float) Math.sin(fZ * this.fPropFactor[i]));
            this.FM.EI.engines[i].setVector(this.engineVector);
            this.hierMesh().chunkSetAngles("Prop" + (i + 1) + "_Base", 0.0F, PROP_ANGLE_MAX * this.fPropAngle[1] * this.fPropFactor[i], PROP_ANGLE_MAX * this.fPropAngle[0] * this.fPropFactor[i]);
        }
    }

    private void xf5uMoveElevon(float fElevator, float fAileron) {
        this.fElevator = fElevator;
        this.fAileron = fAileron;
        this.fAileronLeftFactor = (fElevator * fAileron) > 0 ? cvt(Math.abs(fElevator + fAileron), 1.0F, 2.0F, 1.0F, 1.5F) : 1.0F;
        this.fAileronRightFactor = (fElevator * fAileron) < 0 ? cvt(Math.abs(fElevator - fAileron), 1.0F, 2.0F, 1.0F, 1.5F) : 1.0F;
        this.fAileronLeft = Math.min(Math.max(this.fElevator - (fAileron * this.fAileronLeftFactor), -1.0F), 1.0F);
        this.fAileronRight = Math.min(Math.max(this.fElevator + (fAileron * this.fAileronRightFactor), -1.0F), 1.0F);
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 30F * this.fAileronLeft, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -30F * this.fAileronRight, 0.0F);
    }

    private void xf5uMoveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30.0F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Rudder2_D0", 0.0F, -30.0F * f, 0.0F);
        this.fRudder = f;
    }

    private void xf5uMoveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
        this.xf5uMoveElevon(f, this.fAileron);
    }

    private void xf5uMoveAileron(float f) {
        this.xf5uMoveElevon(this.fElevator, f);
    }

    public boolean xf5uCut(String partName) {
        if (!partName.equalsIgnoreCase("WingLIn") && !partName.equalsIgnoreCase("WingLIn")) {
            return super.cut(partName);
        }
        System.out.println("" + partName + " goes off..");
        this.debugprintln("" + partName + " goes off..");
        if (World.Rnd().nextFloat() < this.bailProbabilityOnCut(partName)) {
            this.debugprintln("BAILING OUT - " + partName + " gone, can't keep on..");
            this.hitDaSilk();
        }
        if (!this.isChunkAnyDamageVisible(partName)) {
            this.debugprintln("" + partName + " is already cut off - operation rejected..");
            return false;
        }
        int[] arrayOfInt = this.hideSubTrees(partName + "_D");
        if (arrayOfInt == null) {
            return false;
        }

        for (int i = 0; i < arrayOfInt.length; i++) {
            this.hierMesh().setCurChunk(arrayOfInt[i]);
            String partNameLowercase = this.hierMesh().chunkName().toLowerCase();
            if (partNameLowercase.startsWith("winglin") || partNameLowercase.startsWith("wingrin")) {
                continue;
            }
            Actor wreckage = new Wreckage(this, arrayOfInt[i]);
            for (int j = 0; j < 4; j++) {
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[(j + 0)])) {
                    this.FM.AS.changeTankEffectBase(j, wreckage);
                }
            }
            for (int j = 0; j < this.FM.EI.getNum(); j++) {
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[(j + 4)])) {
                    this.FM.AS.changeEngineEffectBase(j, wreckage);
                    this.FM.AS.changeSootEffectBase(j, wreckage);
                }
            }
            for (int j = 0; j < 6; j++) {
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[(j + 12)])) {
                    this.FM.AS.changeNavLightEffectBase(j, wreckage);
                }
            }
            for (int j = 0; j < 4; j++) {
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[(j + 18)])) {
                    this.FM.AS.changeLandingLightEffectBase(j, wreckage);
                }
            }
            for (int j = 0; j < this.FM.EI.getNum(); j++) {
                if (this.hierMesh().chunkName().startsWith(this.FM.AS.astateEffectChunks[(j + 22)])) {
                    this.FM.AS.changeOilEffectBase(j, wreckage);
                }
            }
            if ((this.hierMesh().chunkName().startsWith(partName)) && (World.Rnd().nextInt(0, 99) < 50)) {
                Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.SMOKE, 3.0F);
            }
            Vector3d Vd = new Vector3d();
            Vd.set(this.FM.Vwld);
            ((Wreckage) wreckage).setSpeed(Vd);
        }
        String partCAP = partName + "_CAP";
        if (this.hierMesh().chunkFindCheck(partCAP) >= 0) {
            this.hierMesh().chunkVisible(partCAP, true);
        }
        for (int i = 0; i < arrayOfInt.length; i++) {
            for (int k = 3; k < this.FM.Gears.pnti.length; k++) {
                try {
                    if ((this.FM.Gears.pnti[k] != -1) && (arrayOfInt[i] == this.hierMesh().chunkByHookNamed(this.FM.Gears.pnti[k]))) {
                        this.FM.Gears.pnti[k] = -1;
                    }
                } catch (Exception localException) {
                    System.out.println("FATAL ERROR: Gear pnti[] cut failed on tt[] = " + k + " - " + this.FM.Gears.pnti.length);
                }
            }
        }
        this.hierMesh().setCurChunk(arrayOfInt[0]);
        this.hierMesh().getChunkLocObj(tmpLoc1);
        this.sfxCrash(tmpLoc1.getPoint());

        return true;
    }

    /*
     * Catapult bailout code
     */
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void xf5uDoEjectCatapult() {
        new MsgAction(false, this) {

            public void doAction(Object paramObject) {
                Aircraft localAircraft = (Aircraft) paramObject;
                if (Actor.isValid(localAircraft)) {
                    Loc localLoc1 = new Loc();
                    Loc localLoc2 = new Loc();
                    Vector3d localVector3d = new Vector3d(0.0, 0.0, 10.0);
                    HookNamed localHookNamed = new HookNamed(localAircraft, "_ExternalSeat01");
                    localAircraft.pos.getAbs(localLoc2);
                    localHookNamed.computePos(localAircraft, localLoc2, localLoc1);
                    localLoc1.transform(localVector3d);
                    localVector3d.x += localAircraft.FM.Vwld.x;
                    localVector3d.y += localAircraft.FM.Vwld.y;
                    localVector3d.z += localAircraft.FM.Vwld.z;
                    new EjectionSeat(6, localLoc1, localVector3d, localAircraft);
                }
            }
        };
        this.FM.setTakenMortalDamage(true, null);
        this.FM.CT.WeaponControl[0] = false;
        this.FM.CT.WeaponControl[1] = false;
        this.FM.CT.WeaponControl[2] = false;
        this.FM.CT.WeaponControl[3] = false;
        this.FM.CT.bHasAileronControl = false;
        this.FM.CT.bHasRudderControl = false;
        this.FM.CT.bHasElevatorControl = false;
    }

    private void xf5uBailout() {
        if (this.overrideBailout) {
            if ((this.FM.AS.astateBailoutStep >= 0) && (this.FM.AS.astateBailoutStep < 2)) {
                if ((this.FM.CT.cockpitDoorControl > 0.5F) && (this.FM.CT.getCockpitDoor() > 0.5F)) {
                    this.FM.AS.astateBailoutStep = (byte) 11;
                    this.doRemoveBlister();
                    this.ejectTime = Time.current() + 100L;
                } else {
                    this.FM.AS.astateBailoutStep = (byte) 2;
                }
            } else if ((this.FM.AS.astateBailoutStep >= 2) && (this.FM.AS.astateBailoutStep <= 3)) {
                switch (this.FM.AS.astateBailoutStep) {
                    case 2:
                        if (this.FM.CT.cockpitDoorControl < 0.5F) {
                            this.doRemoveBlister();
                            this.ejectTime = Time.current() + 1000L;
                        }
                        break;
                    case 3:
                        this.doRemoveBlister();
                        this.ejectTime = Time.current() + 1000L;
                        break;
                }
                if (this.FM.AS.isMaster()) {
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                }
                this.FM.AS.astateBailoutStep = (byte) (this.FM.AS.astateBailoutStep + 1);
                if (this.FM.AS.astateBailoutStep == 4) {
                    this.FM.AS.astateBailoutStep = (byte) 11;
                }
            } else if ((this.FM.AS.astateBailoutStep >= 11) && (this.FM.AS.astateBailoutStep <= 19) && (Time.current() >= this.ejectTime)) {
                int i = this.FM.AS.astateBailoutStep;
                if (this.FM.AS.isMaster()) {
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                }
                this.FM.AS.astateBailoutStep = (byte) (this.FM.AS.astateBailoutStep + 1);
                if (i == 11) {
                    this.FM.setTakenMortalDamage(true, null);
                    if ((this.FM instanceof Maneuver) && (((Maneuver) this.FM).get_maneuver() != 44)) {
                        if (this.FM.AS.actor != World.getPlayerAircraft()) {
                            ((Maneuver) this.FM).set_maneuver(44);
                        }
                    }
                }
                if (this.FM.AS.astatePilotStates[i - 11] < 99) {
                    this.doRemoveBodyFromPlane(i - 10);
                    if (i == 11) {
                        this.xf5uDoEjectCatapult();
                        this.FM.setTakenMortalDamage(true, null);
                        this.FM.CT.WeaponControl[0] = false;
                        this.FM.CT.WeaponControl[1] = false;
                        this.FM.AS.astateBailoutStep = (byte) -1;
                        this.overrideBailout = false;
                        this.FM.AS.bIsAboutToBailout = true;
                        this.ejectComplete = true;
                        if ((i > 10) && (i <= 19)) {
                            EventLog.onBailedOut(this, i - 11);
                        }
                    }
                }
            }
        }
    }
    // --------------------------------------------------------------------------------------------------

    private float                     fElevator;
    private float                     fAileron;
    private float                     fAileronLeftFactor;
    private float                     fAileronRightFactor;
    private float                     fAileronLeft;
    private float                     fAileronRight;
    private float                     fRudder;
    private float                     fPropAngle[];
    private float                     fPropFactor[];
    private Vector3f                  engineVector;
    private float                     lastPropPitch[];
    private float                     lastPropW[];
    private boolean                   propShaftClutchEngaged;
    private boolean                   propShaftClutchArmed;
    private int                       masterEngineIndex;
    private Point3f                   propPosSeparate[];
    private Point3f                   propPosClutched[];
    private static final float        PROP_DIFF_MAX            = 0.025F;
    private static final float        PROP_ANGLE_MAX           = 10F;
    private static final float        PROP_FACTOR_DIFF_MAX     = 0.025F;
    private static final float        PROP_PITCH_DIFF_MAX      = 0.01F;
    private static final float        PROP_PITCH_CYCLIC_FACTOR = 0.3F;
    private static final float        PROP_W_DIFF_MAX          = 4.26F;
    private static final String[][][] Props                    = { { { "Prop1_D0", "Prop1_Hub_D0", "Prop1_Blade1_D0", "Prop1_Blade2_D0", "Prop1_Blade3_D0", "Prop1_Blade4_D0" }, { "PropRot1_Hub_D0", "PropRot1_Blade1_D0", "PropRot1_Blade2_D0", "PropRot1_Blade3_D0", "PropRot1_Blade4_D0" }, { "Prop1_D1" } }, { { "Prop2_D0", "Prop2_Hub_D0", "Prop2_Blade1_D0", "Prop2_Blade2_D0", "Prop2_Blade3_D0", "Prop2_Blade4_D0" }, { "PropRot2_Hub_D0", "PropRot2_Blade1_D0", "PropRot2_Blade2_D0", "PropRot2_Blade3_D0", "PropRot2_Blade4_D0" }, { "Prop2_D1" } } };
    private static final float[][]    PropAngles               = { { -0.2F, 1.0F, -65F, 20F }, { -0.2F, 1.0F, 65F, -20F } };
    private static final float[][]    PropCyclicFactors        = { { 1F, 0F, 90F, 180F, 270F }, { -1F, 0F, -90F, -180F, -270F } };
    // --------------------------------------------------------------------------------------------------

    public boolean                    bChangedPit;
    private float                     steera;
    private boolean                   overrideBailout;
    private boolean                   ejectComplete;
    private boolean                   blisterRemoved;
    private long                      ejectTime;

    static {
        Class class1 = XF5U1.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "XF5U");
        Property.set(class1, "meshName", "3DO/Plane/XF5U/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar04());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/XF5U1.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitXF5U1.class });
        Property.set(class1, "LOSElevation", 0.69215F);
        weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0, 0, 0, 9, 9, 9, 9, 3, 3, 2, 2 });
        weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalRock01", "_ExternalRock02" });
    }
}
