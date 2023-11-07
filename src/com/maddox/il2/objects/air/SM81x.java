package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public abstract class SM81x extends Scheme2 {

    public SM81x() {
        this.airBrakePos = false;
        this.bGunKilled = false;
        this.tGunKilled = false;
        this.noenemy = 0;
        this.bPitUnfocused = true;
        this.bSightAutomation = false;
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
        this.fSightCurAltitude = 300F;
        this.fSightCurSpeed = 50F;
        this.fSightCurReadyness = 0.0F;
        this.fSightSetForwardAngle = 0.0F;
        this.wasInTorpedoAttack = false;
        this.numEvasive = 0;
        this.timeEvasive = 0;
        this.timeTorpedoDrop = 0;
        this.numTurrets = 0;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (hierMesh.getMeshName().indexOf("hierS73.him") != -1) {
            return;
        }
        boolean showRack250 = thisWeaponsName.toLowerCase().startsWith("2x500") || thisWeaponsName.toLowerCase().startsWith("4x250");
        boolean showRack100 = thisWeaponsName.toLowerCase().startsWith("16x");
        boolean showRack50 = thisWeaponsName.toLowerCase().startsWith("28x");
        boolean showRack31 = thisWeaponsName.toLowerCase().startsWith("56x");
        boolean showRackSpezzoN = thisWeaponsName.toLowerCase().startsWith("6xspezzo");
        boolean showDoor = !thisWeaponsName.toLowerCase().startsWith("18xpara");
        if (hierMesh.getMeshName().indexOf("hierT.him") != -1 || hierMesh.getMeshName().indexOf("hierGR.him") != -1) {
            hierMesh.chunkVisible("Door_D0", showDoor);
        }
        if (hierMesh.getMeshName().indexOf("hierT.him") != -1) {
            return;
        }
        hierMesh.chunkVisible("Rack250_L_D0", showRack250);
        hierMesh.chunkVisible("Rack250_R_D0", showRack250);
        hierMesh.chunkVisible("RackM_D0", showRack250);
        hierMesh.chunkVisible("Rack100_4_L_D0", showRack100);
        hierMesh.chunkVisible("Rack100_4_Ln_D0", showRack100);
        hierMesh.chunkVisible("Rack100_4_R_D0", showRack100);
        hierMesh.chunkVisible("Rack100_4_Rn_D0", showRack100);
        hierMesh.chunkVisible("Rack50_14_L_D0", showRack50);
        hierMesh.chunkVisible("Rack50_14_R_D0", showRack50);
        hierMesh.chunkVisible("Rack31_28_L_D0", showRack31);
        hierMesh.chunkVisible("Rack31_28B_L_D0", showRack31);
        hierMesh.chunkVisible("Rack31_28_R_D0", showRack31);
        hierMesh.chunkVisible("Rack31_28B_R_D0", showRack31);
        hierMesh.chunkVisible("Spezzoniera14_6_L_D0", false);
        hierMesh.chunkVisible("Spezzoniera14_6_Ln_D0", showRackSpezzoN);
        hierMesh.chunkVisible("Spezzoniera14_6_R_D0", false);
        hierMesh.chunkVisible("Spezzoniera14_6_Rn_D0", showRackSpezzoN);
    }
    
    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        if (i > (this.numTurrets - 1)) {
            return flag;
        }
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f1 < 0.0F) {
                    f1 = 0.0F;
                    flag = false;
                }
                if (f1 > 70F) {
                    f1 = 70F;
                    flag = false;
                }
                break;

            case 1:
                if (f1 < -45F) {
                    f1 = -45F;
                    flag = false;
                }
                if (f1 > -2F) {
                    f1 = -2F;
                    flag = false;
                }
                break;

            case 2:
                if (f < -45F) {
                    f = -45F;
                    flag = false;
                }
                if (f > 45F) {
                    f = 45F;
                    flag = false;
                }
                if (f1 < -10F) {
                    f1 = -10F;
                    flag = false;
                }
                if (f1 > 30F) {
                    f1 = 30F;
                    flag = false;
                }
                break;

            case 3:
                if (f < -45F) {
                    f = -45F;
                    flag = false;
                }
                if (f > 45F) {
                    f = 45F;
                    flag = false;
                }
                if (f1 < -10F) {
                    f1 = -10F;
                    flag = false;
                }
                if (f1 > 30F) {
                    f1 = 30F;
                    flag = false;
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    protected void moveFlap(float f) {
        float f1 = -40F * f;
        this.hierMesh().chunkSetAngles("FlapL_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("FlapR_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("FlapLn_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("FlapRn_D0", 0.0F, f1, 0.0F);
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.2F, 0.0F, -0.3F);
        this.hierMesh().chunkSetLocate("Turret1nBase_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("Turret1Base_D0", 0.0F, -90F * f, 0.0F);
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("GunMount1n_D0", 0.0F, -87F * f, 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.2F, 0.0F, -0.4F);
        this.hierMesh().chunkSetLocate("Turret2nBase_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("Turret2Base_D0", 0.0F, -90F * f, 0.0F);
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("GunMount2n_D0", 0.0F, 80F * f, 0.0F);
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("BomberBay_D0", 0.0F, -3F * f, 0.0F);
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("DoorWindowL_D0", 0.0F, -90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("DoorWindowR_D0", 0.0F, 90F * f, 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 1.0F, 0.0F, -0.5F);
        this.hierMesh().chunkSetLocate("GunRail3_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 1.0F, 0.0F, -0.5F);
        this.hierMesh().chunkSetLocate("GunRail4_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("HMask3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.FM.turret[0].bIsOperable = false;
                this.tGunKilled = true;
                break;

            case 3:
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                this.hierMesh().chunkVisible("HMask4_D0", false);
                this.hierMesh().chunkVisible("Pilot4_D1", true);
                this.FM.turret[1].bIsOperable = false;
                this.bGunKilled = true;
                break;

            case 4:
                this.hierMesh().chunkVisible("Pilot5_D0", false);
                this.hierMesh().chunkVisible("HMask5_D0", false);
                this.hierMesh().chunkVisible("Pilot5_D1", true);
                this.FM.turret[2].bIsOperable = false;
                break;

            case 5:
                this.hierMesh().chunkVisible("Pilot6_D0", false);
                this.hierMesh().chunkVisible("HMask6_D0", false);
                this.hierMesh().chunkVisible("Pilot6_D1", true);
                this.FM.turret[3].bIsOperable = false;
                break;
        }
    }

    protected void mydebuggunnery(String s) {
    }

    protected void setControlDamage(Shot shot, int i) {
        if ((World.Rnd().nextFloat() < 0.002F) && (this.getEnergyPastArmor(4F, shot) > 0.0F)) {
            this.FM.AS.setControlsDamage(shot.initiator, i);
            this.mydebuggunnery(i + " Controls Out... //0 = AILERON, 1 = ELEVATOR, 2 = RUDDER");
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        this.mydebuggunnery("HitBone called! " + s);
        this.mydebuggunnery("IN: " + shot.power);
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.endsWith("p1")) {
                    if (Aircraft.v1.z > 0.5D) {
                        this.getEnergyPastArmor(4D / Aircraft.v1.z, shot);
                    } else if (Aircraft.v1.x > 0.93969261646270752D) {
                        this.getEnergyPastArmor((8D / Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                    }
                } else if (s.endsWith("p2")) {
                    this.getEnergyPastArmor(4D / Math.abs(Aircraft.v1.z), shot);
                } else if (s.endsWith("p3")) {
                    this.getEnergyPastArmor((7D / Math.abs(Aircraft.v1.x)) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                } else if (s.endsWith("p4")) {
                    if (Aircraft.v1.x > 0.70710676908493042D) {
                        this.getEnergyPastArmor((7D / Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                    } else if (Aircraft.v1.x > -0.70710676908493042D) {
                        this.getEnergyPastArmor(5F, shot);
                    }
                } else if (s.endsWith("a1") || s.endsWith("a3") || s.endsWith("a4")) {
                    if (Aircraft.v1.x > 0.70710676908493042D) {
                        this.getEnergyPastArmor((0.8D / Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                    } else {
                        this.getEnergyPastArmor(0.6F, shot);
                    }
                }
            }
            if (s.startsWith("xxspar")) {
                this.getEnergyPastArmor(4F, shot);
                if ((s.endsWith("cf1") || s.endsWith("cf2")) && (World.Rnd().nextFloat() < 0.1F) && (this.chunkDamageVisible("CF") > 2) && (this.getEnergyPastArmor(15.9F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F)) {
                    this.mydebuggunnery("*** CF Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                    this.msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                    this.msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                }
                if ((s.endsWith("t1") || s.endsWith("t2")) && (World.Rnd().nextFloat() < 0.1F) && (this.chunkDamageVisible("Tail1") > 2) && (this.getEnergyPastArmor(15.9F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Tail1 Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                }
                if ((s.endsWith("li1") || s.endsWith("li2")) && (World.Rnd().nextFloat() < (1.0D - (0.92D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D2", shot.initiator);
                }
                if ((s.endsWith("ri1") || s.endsWith("ri2")) && (World.Rnd().nextFloat() < (1.0D - (0.92D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D2", shot.initiator);
                }
                if ((s.endsWith("lm1") || s.endsWith("lm2")) && (World.Rnd().nextFloat() < (1.0D - (0.86D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D2", shot.initiator);
                }
                if ((s.endsWith("rm1") || s.endsWith("rm2")) && (World.Rnd().nextFloat() < (1.0D - (0.86D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D2", shot.initiator);
                }
                if ((s.endsWith("lo1") || s.endsWith("lo2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D2", shot.initiator);
                }
                if ((s.endsWith("ro1") || s.endsWith("ro2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D2", shot.initiator);
                }
                if (s.endsWith("e1") && ((point3d.y > 2.79D) || (point3d.y < 2.32D)) && (this.getEnergyPastArmor(17F, shot) > 0.0F)) {
                    this.mydebuggunnery("*** Engine1 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine1_D0", shot.initiator);
                }
                if (s.endsWith("e2") && ((point3d.y < -2.79D) || (point3d.y > -2.32D)) && (this.getEnergyPastArmor(17F, shot) > 0.0F)) {
                    this.mydebuggunnery("*** Engine2 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine2_D0", shot.initiator);
                }
                if (s.endsWith("e3") && ((point3d.y < -2.79D) || (point3d.y > -2.32D)) && (this.getEnergyPastArmor(17F, shot) > 0.0F)) {
                    this.mydebuggunnery("*** Engine3 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine3_D0", shot.initiator);
                }
                if ((s.endsWith("k1") || s.endsWith("k2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Keel spars damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D0", shot.initiator);
                }
                if ((s.endsWith("sr1") || s.endsWith("sr2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Right Stab spars damaged..");
                    this.nextDMGLevels(1, 2, "StabR_D0", shot.initiator);
                }
                if ((s.endsWith("sl1") || s.endsWith("sl2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Left Stab spars damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D0", shot.initiator);
                }
            }
            if (s.startsWith("xxprop")) {
                byte byte0 = 0;
                if (s.endsWith("2")) {
                    byte0 = 1;
                }
                if (s.endsWith("3")) {
                    byte0 = 2;
                }
                if ((this.getEnergyPastArmor(2.0F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.35F)) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, byte0, 3);
                    this.mydebuggunnery("*** Engine" + (byte0 + 1) + " Governor Failed..");
                }
            }
            if (s.startsWith("xxeng")) {
                byte byte1 = 0;
                if (s.startsWith("xxeng2")) {
                    byte1 = 1;
                }
                if (s.startsWith("xxeng3")) {
                    byte1 = 2;
                }
                if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(0.11F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < (shot.power / 200000F)) {
                            this.FM.AS.setEngineStuck(shot.initiator, byte1);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < (shot.power / 50000F)) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 2);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Crank Case Hit - Engine Damaged..");
                        }
                    }
                } else if (s.endsWith("cyls")) {
                    this.mydebuggunnery("*** Engine" + (byte1 + 1) + " RATIO " + this.FM.EI.engines[byte1].getCylindersRatio());
                    if ((this.getEnergyPastArmor(1.4F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[byte1].getCylindersRatio() * 0.6F))) {
                        this.FM.EI.engines[byte1].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 5000F)));
                        this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Cylinders Hit, " + this.FM.EI.engines[byte1].getCylindersOperable() + "/" + this.FM.EI.engines[byte1].getCylinders() + " Left..");
                        if (this.FM.AS.astateEngineStates[byte1] < 1) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 1);
                            this.FM.AS.doSetEngineState(shot.initiator, byte1, 1);
                        }
                        if (World.Rnd().nextFloat() < (shot.power / 960000F)) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 3);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Cylinders Hit - Engine Fires..");
                        }
                        this.mydebuggunnery("*** Engine" + (byte1 + 1) + " state " + this.FM.AS.astateEngineStates[byte1]);
                        this.getEnergyPastArmor(25F, shot);
                    }
                } else if (s.endsWith("supc") && (this.getEnergyPastArmor(0.3F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.79F)) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, byte1, 0);
                    this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Supercharger Out..");
                }
                if (s.endsWith("oil1") || s.endsWith("oil2") || s.endsWith("oil3")) {
                    if (this.getEnergyPastArmor(0.65F, shot) > 0.0F) {
                        this.FM.AS.hitOil(shot.initiator, byte1);
                    }
                    this.getEnergyPastArmor(0.42F, shot);
                }
                this.mydebuggunnery("*** Engine" + (byte1 + 1) + " state = " + this.FM.AS.astateEngineStates[byte1]);
            }
            if (s.startsWith("xxtank")) {
                int i = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(1.3F, shot) > 0.0F) {
                    if (shot.power < 14100F) {
                        if (this.FM.AS.astateTankStates[i] < 1) {
                            this.mydebuggunnery("deterministic damage !!! ");
                            this.FM.AS.hitTank(shot.initiator, i, 1);
                        }
                        if ((this.FM.AS.astateTankStates[i] < 4) && (World.Rnd().nextFloat() < 0.1F)) {
                            this.mydebuggunnery("random damage !!! ");
                            this.FM.AS.hitTank(shot.initiator, i, 1);
                        }
                        if ((shot.powerType == 3) && (this.FM.AS.astateTankStates[i] > 0) && (World.Rnd().nextFloat() < 0.07F)) {
                            this.mydebuggunnery("API round !!! ");
                            this.FM.AS.hitTank(shot.initiator, i, 10);
                        }
                    } else {
                        this.mydebuggunnery("big shot !!! ");
                        this.FM.AS.hitTank(shot.initiator, i, World.Rnd().nextInt(0, (int) (shot.power / 40000F)));
                    }
                }
                this.mydebuggunnery("*** Tank " + (i + 1) + " state = " + this.FM.AS.astateTankStates[i]);
            }
            if (s.startsWith("xxlock")) {
                this.mydebuggunnery("Lock Construction: Hit..");
                if (s.startsWith("xxlockr") && (s.startsWith("xxlockr1") || s.startsWith("xxlockr2")) && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                    this.mydebuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if (s.startsWith("xxlockvl") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                    this.mydebuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                }
                if (s.startsWith("xxlockvr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                    this.mydebuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                }
                if (s.startsWith("xxlockal") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                    this.mydebuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
                }
                if (s.startsWith("xxlockar") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                    this.debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
                }
            }
        }
        if (s.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 3) {
                this.hitChunk("CF", shot);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            }
            if (World.Rnd().nextFloat() > 0.8F) {
                this.getEnergyPastArmor(5F, shot);
            }
        } else if (s.startsWith("xtail")) {
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Tail1") < 3) {
                this.hitChunk("Tail1", shot);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            }
            if (World.Rnd().nextFloat() > 0.8F) {
                this.getEnergyPastArmor(3F, shot);
            }
        } else if (s.startsWith("xkeel")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Keel1") < 2) {
                this.hitChunk("Keel1", shot);
            }
        } else if (s.startsWith("xrudder")) {
            this.setControlDamage(shot, 2);
            this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xstabl")) {
            this.hitChunk("StabL", shot);
        } else if (s.startsWith("xstabr")) {
            this.hitChunk("StabR", shot);
        } else if (s.startsWith("xvatorl")) {
            this.hitChunk("VatorL", shot);
        } else if (s.startsWith("xvatorr")) {
            this.hitChunk("VatorR", shot);
        } else if (s.startsWith("xwinglin")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingLIn") < 3) {
                this.hitChunk("WingLIn", shot);
            }
            if (World.Rnd().nextFloat() > 0.7F) {
                this.getEnergyPastArmor(5F, shot);
            }
        } else if (s.startsWith("xwingrin")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingRIn") < 3) {
                this.hitChunk("WingRIn", shot);
            }
            if (World.Rnd().nextFloat() > 0.7F) {
                this.getEnergyPastArmor(5F, shot);
            }
        } else if (s.startsWith("xwinglmid")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingLMid") < 3) {
                this.hitChunk("WingLMid", shot);
            }
        } else if (s.startsWith("xwingrmid")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingRMid") < 3) {
                this.hitChunk("WingRMid", shot);
            }
        } else if (s.startsWith("xwinglout")) {
            if (this.chunkDamageVisible("WingLOut") < 3) {
                this.hitChunk("WingLOut", shot);
            }
        } else if (s.startsWith("xwingrout")) {
            if (this.chunkDamageVisible("WingROut") < 3) {
                this.hitChunk("WingROut", shot);
            }
        } else if (s.startsWith("xaronel")) {
            this.hitChunk("AroneL", shot);
        } else if (s.startsWith("xaroner")) {
            this.hitChunk("AroneR", shot);
        } else if (s.startsWith("xengine1")) {
            if (this.chunkDamageVisible("Engine1") < 2) {
                this.hitChunk("Engine1", shot);
            }
        } else if (s.startsWith("xengine2")) {
            if (this.chunkDamageVisible("Engine2") < 2) {
                this.hitChunk("Engine2", shot);
            }
        } else if (s.startsWith("xengine3")) {
            if (this.chunkDamageVisible("Engine3") < 2) {
                this.hitChunk("Engine3", shot);
            }
        } else if (s.startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.1F) {
                this.mydebuggunnery("*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
        } else if (s.startsWith("xturret")) {
            if (s.startsWith("xturret1")) {
                this.FM.AS.setJamBullets(10, 0);
            }
            if (s.startsWith("xturret2")) {
                this.FM.AS.setJamBullets(11, 0);
            }
            if (s.startsWith("xturret3")) {
                this.FM.AS.setJamBullets(12, 0);
            }
            if (s.startsWith("xturret4")) {
                this.FM.AS.setJamBullets(13, 0);
            }
            if (s.startsWith("xturret5")) {
                this.FM.AS.setJamBullets(14, 0);
            }
        } else if (s.startsWith("xpilot") || s.startsWith("xhead") || s.startsWith("xpilox") || s.startsWith("xheax")) {
            byte byte2 = 0;
            int j;
            if (s.endsWith("a")) {
                byte2 = 1;
                j = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte2 = 2;
                j = s.charAt(6) - 49;
            } else {
                j = s.charAt(5) - 49;
            }
            this.mydebuggunnery("call HitFlesh:  " + j + " " + byte2 + " " + shot.power);
            this.hitFlesh(j, shot, byte2);
        }
        this.mydebuggunnery("out:  " + shot.power);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if ((this.FM.AS.astateEngineStates[0] > 3) && (World.Rnd().nextFloat() < 0.39F)) {
                this.FM.AS.hitTank(this, 0, 1);
            }
            if ((this.FM.AS.astateEngineStates[1] > 3) && (World.Rnd().nextFloat() < 0.39F)) {
                this.FM.AS.hitTank(this, 1, 1);
            }
            if ((this.FM.AS.astateEngineStates[2] > 3) && (World.Rnd().nextFloat() < 0.39F)) {
                this.FM.AS.hitTank(this, 2, 1);
            }
            if ((this.FM.AS.astateTankStates[0] > 4) && (World.Rnd().nextFloat() < 0.1F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            }
            if ((this.FM.AS.astateTankStates[1] > 4) && (World.Rnd().nextFloat() < 0.1F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            }
            if ((this.FM.AS.astateTankStates[2] > 4) && (World.Rnd().nextFloat() < 0.1F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            }
            if ((this.FM.AS.astateTankStates[3] > 4) && (World.Rnd().nextFloat() < 0.1F)) {
                this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
            }
        }
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        }
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask2_D0", false);
            this.hierMesh().chunkVisible("HMask3_D0", false);
            this.hierMesh().chunkVisible("HMask4_D0", false);
            this.hierMesh().chunkVisible("HMask5_D0", false);
            this.hierMesh().chunkVisible("HMask6_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_D0"));
            this.hierMesh().chunkVisible("HMask3_D0", this.hierMesh().isChunkVisible("Pilot3_D0"));
            this.hierMesh().chunkVisible("HMask4_D0", this.hierMesh().isChunkVisible("Pilot4_D0"));
            this.hierMesh().chunkVisible("HMask5_D0", this.hierMesh().isChunkVisible("Pilot5_D0"));
            this.hierMesh().chunkVisible("HMask6_D0", this.hierMesh().isChunkVisible("Pilot6_D0"));
        }
        if (this.hierMesh().chunkFindCheck("HMask3_D0") > 0) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask3_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask3_D0", this.hierMesh().isChunkVisible("Pilot3_D0"));
            }
        }
        if (this.hierMesh().chunkFindCheck("HMask2_D0") > 0) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask2_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_D0"));
            }
        }
        if (this.hierMesh().chunkFindCheck("HMask4_D0") > 0) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask4_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask4_D0", this.hierMesh().isChunkVisible("Pilot4_D0"));
            }
        }
        if (this.hierMesh().chunkFindCheck("HMask5_D0") > 0) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask5_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask5_D0", this.hierMesh().isChunkVisible("Pilot4_D0"));
            }
        }
        if (this.hierMesh().chunkFindCheck("HMask6_D0") > 0) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask6_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask6_D0", this.hierMesh().isChunkVisible("Pilot4_D0"));
            }
        }
        if (!this.bGunKilled && (this.FM.getAltitude() < 250F)) {
            Actor actor = War.GetNearestEnemy(this, 16, 6000F);
            Aircraft aircraft = War.getNearestEnemy(this, 5000F);
            if (((actor != null) && !(actor instanceof BridgeSegment)) || (aircraft != null)) {
                this.noenemy = 0;
                if (this.FM.CT.getCockpitDoor() < 0.01F) {
                    this.wait = World.Rnd().nextInt(0, 30);
                    this.FM.AS.setCockpitDoor(this, 1);
                }
            } else {
                this.noenemy++;
                if ((this.noenemy > (30 + this.wait)) && (this.FM.CT.getCockpitDoor() > 0.99F)) {
                    this.FM.AS.setCockpitDoor(this, 0);
                }
            }
        }
        if (!this.bGunKilled && (this.FM.getAltitude() > 250F)) {
            Actor actor1 = War.GetNearestEnemy(this, 16, 6000F);
            Aircraft aircraft1 = War.getNearestEnemy(this, 5000F);
            if (((actor1 != null) && !(actor1 instanceof BridgeSegment)) || (aircraft1 != null)) {
                this.noenemy = 0;
                if (this.FM.CT.AirBrakeControl < 0.01F) {
                    this.wait = World.Rnd().nextInt(0, 30);
                    this.FM.CT.AirBrakeControl = 1.0F;
                }
                if (this.FM.CT.getCockpitDoor() < 0.01F) {
                    this.wait = World.Rnd().nextInt(0, 30);
                    this.FM.AS.setCockpitDoor(this, 1);
                }
            } else {
                this.noenemy++;
                if ((this.noenemy > (30 + this.wait)) && (this.FM.CT.AirBrakeControl > 0.99F)) {
                    this.FM.CT.AirBrakeControl = 0.0F;
                }
                if ((this.noenemy > (30 + this.wait)) && (this.FM.CT.getCockpitDoor() > 0.99F)) {
                    this.FM.AS.setCockpitDoor(this, 0);
                }
            }
        }
        if (!this.tGunKilled && (this.FM.getAltitude() > 250F)) {
            Actor actor2 = War.GetNearestEnemy(this, 16, 6000F);
            Aircraft aircraft2 = War.getNearestEnemy(this, 5000F);
            if (((actor2 != null) && !(actor2 instanceof BridgeSegment)) || (aircraft2 != null)) {
                this.noenemy = 0;
                if (this.FM.CT.AirBrakeControl < 0.01F) {
                    this.wait = World.Rnd().nextInt(0, 30);
                    this.FM.CT.AirBrakeControl = 1.0F;
                }
                if (this.FM.CT.getCockpitDoor() < 0.01F) {
                    this.wait = World.Rnd().nextInt(0, 30);
                    this.FM.AS.setCockpitDoor(this, 1);
                }
            } else {
                this.noenemy++;
                if ((this.noenemy > (30 + this.wait)) && (this.FM.CT.AirBrakeControl > 0.99F)) {
                    this.FM.CT.AirBrakeControl = 0.0F;
                }
                if ((this.noenemy > (30 + this.wait)) && (this.FM.CT.getCockpitDoor() > 0.99F)) {
                    this.FM.AS.setCockpitDoor(this, 0);
                }
            }
        }
    }

    public void doRemoveBodyFromPlane(int i) {
        super.doRemoveBodyFromPlane(i);
        if (i >= 3) {
            this.doRemoveBodyChunkFromPlane("Pilot4");
        }
    }

    protected void moveBayDoor(float f) {
        this.hierMesh().chunkSetAngles("BayR_D0", 0.0F, -90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("BayL_D0", 0.0F, 90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("BayRn_D0", 0.0F, -90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("BayLn_D0", 0.0F, 90F * f, 0.0F);
    }

    protected void moveFan(float f) {
        if (this.bDynamoOperational) {
            this.pk = Math.abs((int) (this.FM.Vwld.length() / 14D));
            if (this.pk >= 1) {
                this.pk = 1;
            }
        }
        if (this.bDynamoRotary != (this.pk == 1)) {
            this.bDynamoRotary = this.pk == 1;
            this.hierMesh().chunkVisible("PropDynamo_D0", !this.bDynamoRotary);
            this.hierMesh().chunkVisible("PropRotDynamo_D0", this.bDynamoRotary);
        }
        this.dynamoOrient = this.bDynamoRotary ? (this.dynamoOrient - 17.987F) % 360F : (float) (this.dynamoOrient - (this.FM.Vwld.length() * 1.5444015264511108D)) % 360F;
        this.hierMesh().chunkSetAngles("PropDynamo_D0", 0.0F, this.dynamoOrient, 0.0F);
        super.moveFan(f);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                return super.cutFM(34, j, actor);

            case 36:
                return super.cutFM(37, j, actor);

            case 3:
                this.FM.AS.setEngineState(this, 0, 0);
                break;

            case 4:
                this.FM.AS.setEngineState(this, 1, 0);
                break;

            case 13:
                return false;
        }
        return super.cutFM(i, j, actor);
    }

    public void update(float f) {
        super.update(f);
        if (this.FM.isPlayers()) {
            if (!Main3D.cur3D().isViewOutside()) {
                this.hierMesh().chunkVisible("CF_D0", false);
                this.hierMesh().chunkVisible("Blister1_D0", false);
                this.hierMesh().chunkVisible("Cockpit_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("HMask2_D0", false);
            } else {
                this.hierMesh().chunkVisible("CF_D0", true);
                this.hierMesh().chunkVisible("Blister1_D0", true);
                this.hierMesh().chunkVisible("Cockpit_D0", true);
                this.hierMesh().chunkVisible("Head1_D0", true);
                this.hierMesh().chunkVisible("HMask1_D0", true);
                this.hierMesh().chunkVisible("Pilot1_D0", true);
                this.hierMesh().chunkVisible("HMask2_D0", true);
                this.hierMesh().chunkVisible("Pilot2_D0", true);
            }
        }
        if (this.FM.isPlayers()) {
            if (!Main3D.cur3D().isViewOutside()) {
                this.hierMesh().chunkVisible("CF_D1", false);
            }
            this.hierMesh().chunkVisible("CF_D2", false);
            this.hierMesh().chunkVisible("CF_D3", false);
            this.hierMesh().chunkVisible("Pilot1_D1", false);
            this.hierMesh().chunkVisible("Pilot2_D1", false);
            this.hierMesh().chunkVisible("Blister1_D1", false);
            this.hierMesh().chunkVisible("Cockpit_D1", false);
            this.hierMesh().chunkVisible("Blister1_D2", false);
            this.hierMesh().chunkVisible("Cockpit_D2", false);
            this.hierMesh().chunkVisible("Blister1_D3", false);
            this.hierMesh().chunkVisible("Cockpit_D3", false);
        }
    }

    public boolean typeBomberToggleAutomation() {
        return false;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle += 0.4F;
        if (this.fSightCurForwardAngle > 75F) {
            this.fSightCurForwardAngle = 75F;
        }
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle -= 0.4F;
        if (this.fSightCurForwardAngle < -15F) {
            this.fSightCurForwardAngle = -15F;
        }
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip += 0.5D;
        if (this.thisWeaponsName.startsWith("1x")) {
            if (this.fSightCurSideslip > 40F) {
                this.fSightCurSideslip = 40F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "Torpedo Angle  " + this.fSightCurSideslip);
        } else {
            if (this.fSightCurSideslip > 10F) {
                this.fSightCurSideslip = 10F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip " + this.fSightCurSideslip);
        }
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip -= 0.5D;
        if (this.thisWeaponsName.startsWith("1x")) {
            if (this.fSightCurSideslip < -40F) {
                this.fSightCurSideslip = -40F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "Torpedo Angle  " + this.fSightCurSideslip);
        } else {
            if (this.fSightCurSideslip < -10F) {
                this.fSightCurSideslip = -10F;
            }
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip " + this.fSightCurSideslip);
        }
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 300F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 10F;
        if (this.fSightCurAltitude > 6000F) {
            this.fSightCurAltitude = 6000F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 10F;
        if (this.fSightCurAltitude < 300F) {
            this.fSightCurAltitude = 300F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 50F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 5F;
        if (this.fSightCurSpeed > 650F) {
            this.fSightCurSpeed = 650F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 5F;
        if (this.fSightCurSpeed < 50F) {
            this.fSightCurSpeed = 50F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        double d = (this.fSightCurSpeed / 3.6D) * Math.sqrt(this.fSightCurAltitude * 0.203873598D);
        d -= this.fSightCurAltitude * this.fSightCurAltitude * 1.419E-005D;
        this.fSightSetForwardAngle = (float) Math.atan(d / this.fSightCurAltitude);
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeFloat(this.fSightCurSpeed);
        netmsgguaranted.writeFloat(this.fSightCurForwardAngle);
        netmsgguaranted.writeFloat(this.fSightCurSideslip);
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readFloat();
        this.fSightCurSideslip = netmsginput.readFloat();
    }

    public boolean  bPitUnfocused;
    public boolean  bSightAutomation;
    private boolean bDynamoOperational;
    private int     pk;
    private float   dynamoOrient;
    private boolean bDynamoRotary;
    public float    fSightCurDistance;
    public float    fSightCurForwardAngle;
    public float    fSightCurSideslip;
    public float    fSightCurAltitude;
    public float    fSightCurSpeed;
    public float    fSightCurReadyness;
    public float    fSightSetForwardAngle;
    public boolean  airBrakePos;
    private int     wait;
    private int     noenemy;
    boolean         bGunKilled;
    boolean         tGunKilled;
    boolean         wasInTorpedoAttack;
    int             numEvasive;
    int             timeEvasive;
    int             timeTorpedoDrop;
    int             numTurrets;

    static {
        Class class1 = SM81x.class;
        Property.set(class1, "originCountry", PaintScheme.countryItaly);
    }
}
