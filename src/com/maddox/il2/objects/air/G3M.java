package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public abstract class G3M extends Scheme2 {

    public G3M() {
        this.fSightCurAltitude = 300F;
        this.fSightCurSpeed = 50F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightSetForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Rudder2_D0", 0.0F, -30F * f, 0.0F);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2) {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.1F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f1, 0.01F, 0.1F, 0.0F, -90F), 0.0F);
    }

    protected void moveGear(float f, float f1, float f2) {
        G3M.moveGear(this.hierMesh(), f, f1, f2);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        G3M.moveGear(hiermesh, f, f, f);
    }

    protected void moveGear(float f) {
        G3M.moveGear(this.hierMesh(), f, f, f);
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC2_D0", f, 0.0F, 0.0F);
    }

    public void moveWheelSink() {
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3) {
                if (World.Rnd().nextFloat() < 0.02F) {
                    this.FM.AS.hitTank(this, 0, 1);
                }
                if (World.Rnd().nextFloat() < 0.02F) {
                    this.FM.AS.hitTank(this, 1, 1);
                }
            }
            if (this.FM.AS.astateEngineStates[1] > 3) {
                if (World.Rnd().nextFloat() < 0.02F) {
                    this.FM.AS.hitTank(this, 2, 1);
                }
                if (World.Rnd().nextFloat() < 0.02F) {
                    this.FM.AS.hitTank(this, 3, 1);
                }
            }
            if ((this.FM.AS.astateTankStates[0] > 5) && (World.Rnd().nextFloat() < 0.02F)) {
                this.FM.AS.hitTank(this, 1, 1);
            }
            if ((this.FM.AS.astateTankStates[1] > 5) && (World.Rnd().nextFloat() < 0.02F)) {
                this.FM.AS.hitTank(this, 0, 1);
            }
            if ((this.FM.AS.astateTankStates[1] > 5) && (World.Rnd().nextFloat() < 0.02F)) {
                this.FM.AS.hitTank(this, 2, 1);
            }
            if ((this.FM.AS.astateTankStates[2] > 5) && (World.Rnd().nextFloat() < 0.02F)) {
                this.FM.AS.hitTank(this, 1, 1);
            }
            if ((this.FM.AS.astateTankStates[2] > 5) && (World.Rnd().nextFloat() < 0.02F)) {
                this.FM.AS.hitTank(this, 3, 1);
            }
            if ((this.FM.AS.astateTankStates[3] > 5) && (World.Rnd().nextFloat() < 0.02F)) {
                this.FM.AS.hitTank(this, 2, 1);
            }
        }
        if (this.FM.getAltitude() < 3000F) {
            for (int i = 1; i < 8; i++) {
                this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            }
        } else {
            for (int j = 1; j < 8; j++) {
                this.hierMesh().chunkVisible("HMask" + j + "_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
            }
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f < -35F) {
                    f = -35F;
                    flag = false;
                }
                if (f > 35F) {
                    f = 35F;
                    flag = false;
                }
                if (f1 < -45F) {
                    f1 = -45F;
                    flag = false;
                }
                if (f1 > 25F) {
                    f1 = 25F;
                    flag = false;
                }
                break;

            case 1:
                if (f < -45F) {
                    f = -45F;
                    flag = false;
                }
                if (f > 45F) {
                    f = 45F;
                    flag = false;
                }
                if (f1 < -45F) {
                    f1 = -45F;
                    flag = false;
                }
                if (f1 > 25F) {
                    f1 = 25F;
                    flag = false;
                }
                break;

            case 2:
                if (f < -50F) {
                    f = -50F;
                    flag = false;
                }
                if (f > 50F) {
                    f = 50F;
                    flag = false;
                }
                if (f1 > Aircraft.cvt(Math.abs(f), 0.0F, 50F, 40F, 25F)) {
                    f1 = Aircraft.cvt(Math.abs(f), 0.0F, 50F, 40F, 25F);
                }
                if (f1 < Aircraft.cvt(Math.abs(f), 0.0F, 50F, -10F, -3.5F)) {
                    f1 = Aircraft.cvt(Math.abs(f), 0.0F, 50F, -10F, -3.5F);
                }
                break;

            case 3:
                if (f < -55F) {
                    f = -55F;
                    flag = false;
                }
                if (f > 30F) {
                    f = 30F;
                    flag = false;
                }
                if (f1 < -40F) {
                    f1 = -40F;
                    flag = false;
                }
                if (f1 > 45F) {
                    f1 = 45F;
                    flag = false;
                }
                break;

            case 4:
                if (f < -30F) {
                    f = -30F;
                    flag = false;
                }
                if (f > 55F) {
                    f = 55F;
                    flag = false;
                }
                if (f1 < -40F) {
                    f1 = -40F;
                    flag = false;
                }
                if (f1 > 45F) {
                    f1 = 45F;
                    flag = false;
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            default:
                break;

            case 33:
                this.hitProp(0, j, actor);
                break;

            case 36:
                this.hitProp(1, j, actor);
                break;

            case 34:
                this.FM.AS.hitEngine(this, 0, 2);
                if (World.Rnd().nextInt(0, 99) < 66) {
                    this.FM.AS.hitEngine(this, 0, 2);
                }
                break;

            case 37:
                this.FM.AS.hitEngine(this, 1, 2);
                if (World.Rnd().nextInt(0, 99) < 66) {
                    this.FM.AS.hitEngine(this, 1, 2);
                }
                break;

            case 19:
                this.killPilot(actor, 6);
                break;
        }
        return super.cutFM(i, j, actor);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxcontrol")) {
                int i = s.charAt(9) - 48;
                switch (i) {
                    default:
                        break;

                    case 1:
                        if (this.getEnergyPastArmor(2.2F, shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < 0.1F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 2);
                            }
                            if (World.Rnd().nextFloat() < 0.1F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 1);
                            }
                        }
                        break;

                    case 2:
                    case 3:
                        if ((this.getEnergyPastArmor(2.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                int j = s.charAt(5) - 49;
                if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(1.7F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat(20000F, 140000F) < shot.power) {
                            this.FM.AS.setEngineStuck(shot.initiator, j);
                            this.debuggunnery("*** Engine" + j + " Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat(10000F, 50000F) < shot.power) {
                            this.FM.AS.hitEngine(shot.initiator, j, 2);
                            this.debuggunnery("*** Engine" + j + " Crank Case Hit - Engine Damaged..");
                        }
                    } else if (World.Rnd().nextFloat() < 0.04F) {
                        this.FM.EI.engines[j].setCyliderKnockOut(shot.initiator, 1);
                    } else {
                        this.FM.EI.engines[j].setReadyness(shot.initiator, this.FM.EI.engines[j].getReadyness() - 0.02F);
                        this.debuggunnery("*** Engine" + j + " Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[j].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(12F, shot);
                }
                if (s.endsWith("cyls")) {
                    if ((this.getEnergyPastArmor(0.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[j].getCylindersRatio() * 0.9878F))) {
                        this.FM.EI.engines[j].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                        this.debuggunnery("*** Engine" + j + " Cylinders Hit, " + this.FM.EI.engines[j].getCylindersOperable() + "/" + this.FM.EI.engines[j].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < (shot.power / 48000F)) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            this.debuggunnery("*** Engine Cylinders Hit - Engine Fires..");
                        }
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("supc")) {
                    if (this.getEnergyPastArmor(0.05F, shot) > 0.0F) {
                        this.FM.EI.engines[j].setKillCompressor(shot.initiator);
                    }
                    this.getEnergyPastArmor(2.0F, shot);
                }
                if (s.endsWith("gear")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                        if ((Aircraft.Pd.y > 0.0D) && (Aircraft.Pd.z < 0.189D) && (World.Rnd().nextFloat(0.0F, 16000F) < shot.power)) {
                            this.FM.EI.engines[j].setMagnetoKnockOut(shot.initiator, 0);
                        }
                        if ((Aircraft.Pd.y < 0.0D) && (Aircraft.Pd.z < 0.189D) && (World.Rnd().nextFloat(0.0F, 16000F) < shot.power)) {
                            this.FM.EI.engines[j].setMagnetoKnockOut(shot.initiator, 1);
                        }
                        if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 4);
                        }
                        if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 0);
                        }
                        if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 6);
                        }
                        if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 1);
                        }
                    }
                    this.getEnergyPastArmor(2.0F, shot);
                }
                if (s.endsWith("oil1") && (this.getEnergyPastArmor(4.21F, shot) > 0.0F)) {
                    this.FM.AS.hitOil(shot.initiator, j);
                    this.getEnergyPastArmor(0.42F, shot);
                }
                return;
            }
            if (s.startsWith("xxfuel")) {
                int k = s.charAt(6) - 48;
                int i1;
                switch (k) {
                    case 1:
                    default:
                        if (World.Rnd().nextFloat() < 0.1F) {
                            this.hitBone("xxfuel2", shot, point3d);
                        }
                        if (World.Rnd().nextFloat() < 0.1F) {
                            this.hitBone("xxfuel3", shot, point3d);
                        }
                        if (World.Rnd().nextFloat() < 0.1F) {
                            this.hitBone("xxfuel5", shot, point3d);
                        }
                        if (World.Rnd().nextFloat() < 0.1F) {
                            this.hitBone("xxfuel6", shot, point3d);
                        }
                        return;

                    case 2:
                        i1 = 1;
                        break;

                    case 3:
                        i1 = World.Rnd().nextInt(0, 1);
                        break;

                    case 4:
                        i1 = 0;
                        break;

                    case 5:
                        i1 = 2;
                        break;

                    case 6:
                        i1 = World.Rnd().nextInt(2, 3);
                        break;

                    case 7:
                        i1 = 3;
                        break;
                }
                if ((this.getEnergyPastArmor(0.8F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.2F)) {
                    if (this.FM.AS.astateTankStates[i1] == 0) {
                        this.FM.AS.hitTank(shot.initiator, i1, 2);
                        this.FM.AS.doSetTankState(shot.initiator, i1, 2);
                    }
                    if (World.Rnd().nextFloat() < 0.15F) {
                        this.FM.AS.hitTank(shot.initiator, i1, 1);
                    }
                    if ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.5F)) {
                        this.FM.AS.hitTank(shot.initiator, i1, 2);
                    }
                }
                return;
            }
            if (s.startsWith("xxgun")) {
                int l = s.charAt(5) - 49;
                if (World.Rnd().nextFloat() < 0.5F) {
                    this.FM.AS.setJamBullets(10 + l, 0);
                }
                this.getEnergyPastArmor(22.7F, shot);
                return;
            }
            if (s.startsWith("xxlock")) {
                if (s.startsWith("xxlockal")) {
                    if (this.getEnergyPastArmor(4.35F, shot) > 0.0F) {
                        this.debuggunnery("*** AroneL Lock Damaged..");
                        this.nextDMGLevels(1, 2, "AroneL_D0", shot.initiator);
                    }
                } else if (s.startsWith("xxlockar")) {
                    if (this.getEnergyPastArmor(4.35F, shot) > 0.0F) {
                        this.debuggunnery("*** AroneR Lock Damaged..");
                        this.nextDMGLevels(1, 2, "AroneR_D0", shot.initiator);
                    }
                } else if (s.startsWith("xxlocksl")) {
                    if (this.getEnergyPastArmor(4.32F, shot) > 0.0F) {
                        this.debuggunnery("*** VatorL Lock Damaged..");
                        this.nextDMGLevels(1, 2, "VatorL_D0", shot.initiator);
                    }
                } else if (s.startsWith("xxlocksr")) {
                    if (this.getEnergyPastArmor(4.32F, shot) > 0.0F) {
                        this.debuggunnery("*** VatorR Lock Damaged..");
                        this.nextDMGLevels(1, 2, "VatorR_D0", shot.initiator);
                    }
                } else if (s.startsWith("xxlockk1") && (this.getEnergyPastArmor(4.32F, shot) > 0.0F)) {
                    this.debuggunnery("*** Rudder1 Lock Damaged..");
                    this.nextDMGLevels(1, 2, "Rudder1_D0", shot.initiator);
                }
                if (s.startsWith("xxlockk4") && (this.getEnergyPastArmor(4.32F, shot) > 0.0F)) {
                    this.debuggunnery("*** Rudder2 Lock Damaged..");
                    this.nextDMGLevels(1, 2, "Rudder2_D0", shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxpar")) {
                if (s.startsWith("xxpark1") && (this.chunkDamageVisible("Keel1") > 1) && (this.getEnergyPastArmor(10.7D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** Keel1 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D2", shot.initiator);
                }
                if (s.startsWith("xxpark3") && (this.chunkDamageVisible("Keel2") > 1) && (this.getEnergyPastArmor(10.7D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** Keel2 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Keel2_D2", shot.initiator);
                }
                if (s.startsWith("xxparli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(12.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxparri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(12.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxparlm") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(10.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxparrm") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(10.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxparlo") && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor(8.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.startsWith("xxparro") && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(8.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if (s.startsWith("xxparrs") && (this.chunkDamageVisible("StabL") > 1) && (this.getEnergyPastArmor(8.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** StabL Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D2", shot.initiator);
                }
                if (s.startsWith("xxparls") && (this.chunkDamageVisible("StabR") > 1) && (this.getEnergyPastArmor(8.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)) {
                    this.debuggunnery("*** StabR Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabR_D2", shot.initiator);
                }
                return;
            } else {
                return;
            }
        }
        if (s.startsWith("xcf")) {
            if (this.chunkDamageVisible("CF") < 3) {
                this.hitChunk("CF", shot);
            }
        } else if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) {
                this.hitChunk("Tail1", shot);
            }
        } else if (s.startsWith("xkeel1")) {
            if (this.chunkDamageVisible("Keel1") < 2) {
                this.hitChunk("Keel1", shot);
            }
        } else if (s.startsWith("xkeel2")) {
            if (this.chunkDamageVisible("Keel2") < 2) {
                this.hitChunk("Keel2", shot);
            }
        } else if (s.startsWith("xrudder1")) {
            if (this.chunkDamageVisible("Rudder1") < 2) {
                this.hitChunk("Rudder1", shot);
            }
        } else if (s.startsWith("xrudder2")) {
            if (this.chunkDamageVisible("Rudder2") < 2) {
                this.hitChunk("Rudder2", shot);
            }
        } else if (s.startsWith("xstabl")) {
            if (this.chunkDamageVisible("StabL") < 2) {
                this.hitChunk("StabL", shot);
            }
        } else if (s.startsWith("xstabr")) {
            if (this.chunkDamageVisible("StabR") < 2) {
                this.hitChunk("StabR", shot);
            }
        } else if (s.startsWith("xvatorl")) {
            if (this.chunkDamageVisible("VatorL") < 2) {
                this.hitChunk("VatorL", shot);
            }
        } else if (s.startsWith("xvatorr")) {
            if (this.chunkDamageVisible("VatorR") < 2) {
                this.hitChunk("VatorR", shot);
            }
        } else if (s.startsWith("xwinglin")) {
            if (this.chunkDamageVisible("WingLIn") < 3) {
                this.hitChunk("WingLIn", shot);
            }
        } else if (s.startsWith("xwingrin")) {
            if (this.chunkDamageVisible("WingRIn") < 3) {
                this.hitChunk("WingRIn", shot);
            }
        } else if (s.startsWith("xwinglmid")) {
            if (this.chunkDamageVisible("WingLMid") < 3) {
                this.hitChunk("WingLMid", shot);
            }
        } else if (s.startsWith("xwingrmid")) {
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
            if (this.chunkDamageVisible("AroneL") < 2) {
                this.hitChunk("AroneL", shot);
            }
        } else if (s.startsWith("xaroner")) {
            if (this.chunkDamageVisible("AroneR") < 2) {
                this.hitChunk("AroneR", shot);
            }
        } else if (s.startsWith("xengine1")) {
            if (this.chunkDamageVisible("Engine1") < 2) {
                this.hitChunk("Engine1", shot);
            }
        } else if (s.startsWith("xengine2")) {
            if (this.chunkDamageVisible("Engine2") < 2) {
                this.hitChunk("Engine2", shot);
            }
        } else if (s.startsWith("xgear")) {
            if (s.endsWith("1") && (World.Rnd().nextFloat() < 0.05F)) {
                this.debuggunnery("Hydro System: Disabled..");
                this.FM.AS.setInternalDamage(shot.initiator, 0);
            }
            if (s.endsWith("2") && (World.Rnd().nextFloat() < 0.1F) && (this.getEnergyPastArmor(World.Rnd().nextFloat(12.88F, 16.96F), shot) > 0.0F)) {
                this.debuggunnery("Undercarriage: Stuck..");
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            }
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int j1;
            if (s.endsWith("a") || s.endsWith("a2")) {
                byte0 = 1;
                j1 = s.charAt(6) - 49;
            } else if (s.endsWith("b") || s.endsWith("b2")) {
                byte0 = 2;
                j1 = s.charAt(6) - 49;
            } else {
                j1 = s.charAt(5) - 49;
            }
            this.hitFlesh(j1, shot, byte0);
        }
    }

//    public void doWoundPilot(int i, float f) {
//        switch (i) {
//            case 2:
//                this.FM.turret[0].setHealth(f);
//                break;
//
//            case 3:
//                this.FM.turret[2].setHealth(f);
//                break;
//
//            case 4:
//                this.FM.turret[4].setHealth(f);
//                break;
//
//            case 5:
//                this.FM.turret[3].setHealth(f);
//                break;
//
//            case 6:
//                this.FM.turret[1].setHealth(f);
//                break;
//        }
//    }

    void setTurretHealth(int turret, float health) {
        if ((turret < 0) || (turret >= this.FM.turret.length)) {
            System.out.println("### " + this.getClass().getName() + " attempt to set Turret Health for Turret " + turret + " failed: Valid Turret indices are: " + (this.FM.turret.length == 0 ? "none" : "0 ~ " + (this.FM.turret.length - 1)));
            return;
        }
        this.FM.turret[turret].setHealth(health);
    }

    public void doMurderPilot(int i) {
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("Head" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
    }

    public boolean typeBomberToggleAutomation() {
        return false;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle += 0.2F;
        if (this.fSightCurForwardAngle > 75F) {
            this.fSightCurForwardAngle = 75F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) (this.fSightCurForwardAngle * 1.0F)) });
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle -= 0.2F;
        if (this.fSightCurForwardAngle < -15F) {
            this.fSightCurForwardAngle = -15F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) (this.fSightCurForwardAngle * 1.0F)) });
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip++;
        if (this.fSightCurSideslip > 45F) {
            this.fSightCurSideslip = 45F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 1.0F)) });
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip--;
        if (this.fSightCurSideslip < -45F) {
            this.fSightCurSideslip = -45F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 1.0F)) });
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
        this.fSightSetForwardAngle = (float) Math.toDegrees(Math.atan(d / this.fSightCurAltitude));
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

    public float fSightCurAltitude;
    public float fSightCurSpeed;
    public float fSightCurForwardAngle;
    public float fSightSetForwardAngle;
    public float fSightCurSideslip;

    static {
        Class class1 = G3M.class;
        Property.set(class1, "originCountry", PaintScheme.countryJapan);
    }
}
