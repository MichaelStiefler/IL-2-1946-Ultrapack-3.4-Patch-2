package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.rts.Property;

public abstract class CantZ506 extends Scheme6 {

    public CantZ506() {
        this.fSightCurAltitude = 300F;
        this.fSightCurSpeed = 50F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightSetForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
        this.bPitUnfocused = true;
        this.bPilot1Killed = false;
        this.bPilot2Killed = false;
        this.bPilot3Killed = false;
        this.bPilot4Killed = false;
        this.bPilot5Killed = false;
        this.bPilot5KilledInBombPos = false;
        this.bayDoorAngle = 0.0F;
        this.bDynamoOperational = true;
        this.dynamoOrient = 0.0F;
        this.bDynamoRotary = false;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("250kgWingRackL_D0", thisWeaponsName.endsWith("wing"));
        hierMesh.chunkVisible("250kgWingRackR_D0", thisWeaponsName.endsWith("wing"));
    }

    public void rareAction(float f, boolean flag) {
        if (this.hierMesh().isChunkVisible("Prop2_D1") && this.hierMesh().isChunkVisible("Prop3_D1") && (this.hierMesh().isChunkVisible("Prop1_D0") || this.hierMesh().isChunkVisible("PropRot1_D0"))) {
            this.mydebuggunnery("!!!!!!!!!!!!!!!!!!! HIT PROP 1 !!!!!!!!!!!!!!!!!!!!!");
            this.hitProp(0, 0, Engine.actorLand());
        }
        if (this.hierMesh().isChunkVisible("Prop2_D1") && this.hierMesh().isChunkVisible("Prop1_D1") && (this.hierMesh().isChunkVisible("Prop3_D0") || this.hierMesh().isChunkVisible("PropRot3_D0"))) {
            this.mydebuggunnery("!!!!!!!!!!!!!!!!!!! HIT PROP 3 !!!!!!!!!!!!!!!!!!!!!");
            this.hitProp(2, 0, Engine.actorLand());
        }
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
        War war = War.cur();
        if (war == null) {
            ;
        }
        Actor actor = War.GetNearestEnemy(this, 16, 6000F);
        if (war == null) {
            ;
        }
        Aircraft aircraft = War.getNearestEnemy(this, 5000F);
        if ((((actor != null) && !(actor instanceof BridgeSegment)) || (aircraft != null)) && (this.FM.CT.getCockpitDoor() < 0.01F)) {
            this.FM.AS.setCockpitDoor(this, 1);
        }
        for (int i = 1; i <= 5; i++) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("hmask" + i + "_d0", false);
            } else {
                this.hierMesh().chunkVisible("hmask" + i + "_d0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));
            }
        }

    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("hmask1_d0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.bPilot1Killed = true;
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("hmask2_d0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.bPilot2Killed = true;
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("hmask3_d0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.bPilot3Killed = true;
                break;

            case 3:
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                this.hierMesh().chunkVisible("hmask4_d0", false);
                this.hierMesh().chunkVisible("Pilot4_D1", true);
                this.bPilot4Killed = true;
                break;

            case 4:
                if (this.hierMesh().isChunkVisible("Pilot5_D0")) {
                    this.bPilot5KilledInBombPos = false;
                    this.hierMesh().chunkVisible("hmask5_d0", false);
                    this.hierMesh().chunkVisible("Pilot5_D0", false);
                    this.hierMesh().chunkVisible("Pilot5_D1", true);
                }
                if (this.hierMesh().isChunkVisible("Pilot5bomb_D0")) {
                    this.bPilot5KilledInBombPos = true;
                    this.hierMesh().chunkVisible("Pilot5bomb_D0", false);
                    this.hierMesh().chunkVisible("Pilot5bomb_D1", true);
                }
                this.bPilot5Killed = true;
                break;
        }
    }

    public void update(float f) {
        this.hierMesh().chunkSetAngles("Turret1C_D0", 0.0F, this.FM.turret[0].tu[1], 0.0F);
        if (this.bayDoorAngle > 0.5D) {
            if (!this.bPilot5Killed && !this.FM.AS.isPilotParatrooper(4)) {
                this.hierMesh().chunkVisible("Pilot5_D0", false);
                this.hierMesh().chunkVisible("Pilot5bomb_D0", true);
                this.FM.turret[2].bIsOperable = false;
                this.FM.turret[3].bIsOperable = false;
            }
        } else if (!this.bPilot5Killed && !this.FM.AS.isPilotParatrooper(4)) {
            this.hierMesh().chunkVisible("Pilot5_D0", true);
            this.hierMesh().chunkVisible("Pilot5bomb_D0", false);
            this.FM.turret[2].bIsOperable = true;
            this.FM.turret[3].bIsOperable = true;
        }
        super.update(f);
    }

    protected void moveElevator(float f) {
        if (f < 0.0F) {
            this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 23.5F * f, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 23.5F * f, 0.0F);
        } else {
            this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 15.5F * f, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 15.5F * f, 0.0F);
        }
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -23F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Rudder2_D0", 0.0F, -23F * f, 0.0F);
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -20F * f, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -20F * f, 0.0F);
    }

    protected void moveFlap(float f) {
        float f1 = -55F * f;
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1, 0.0F);
    }

    public void moveCockpitDoor(float f) {
        this.hierMesh().chunkSetAngles("TurDoorL_D0", 0.0F, 70F * f, 0.0F);
        this.hierMesh().chunkSetAngles("TurDoorR_D0", 0.0F, 70F * f, 0.0F);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    public void hitDaSilk() {
        if (this.FM.CT.getCockpitDoor() < 0.01F) {
            this.FM.AS.setCockpitDoor(this, 1);
        }
        super.hitDaSilk();
    }

    protected void mydebuggunnery(String s) {
//        System.out.println(s);
    }

    protected void setControlDamage(Shot shot, int i) {
        if ((World.Rnd().nextFloat() < 0.002F) && (this.getEnergyPastArmor(4.2F, shot) > 0.0F)) {
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
                    } else {
                        this.getEnergyPastArmor((8D / Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                    }
                } else if (s.endsWith("p2")) {
                    this.getEnergyPastArmor(4D / Math.abs(Aircraft.v1.z), shot);
                } else if (s.endsWith("p3")) {
                    this.getEnergyPastArmor((7D / Math.abs(Aircraft.v1.x)) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                } else if (s.endsWith("p4")) {
                    if (Aircraft.v1.x > 0.70710676908493042D) {
                        this.getEnergyPastArmor((7D / Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                    } else {
                        this.getEnergyPastArmor(5F, shot);
                    }
                } else if (s.endsWith("a1") || s.endsWith("a3") || s.endsWith("a4")) {
                    this.getEnergyPastArmor(0.6F, shot);
                }
            }
            if (s.startsWith("xxspar")) {
                this.getEnergyPastArmor(4F, shot);
                if ((s.endsWith("cf1") || s.endsWith("cf2")) && (World.Rnd().nextFloat() < 0.1F) && (this.chunkDamageVisible("CF") > 1) && (this.getEnergyPastArmor(15.9F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F)) {
                    this.mydebuggunnery("*** CF Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                    this.msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                    this.msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                }
                if ((s.endsWith("t1") || s.endsWith("t2")) && (World.Rnd().nextFloat() < 0.1F) && (this.chunkDamageVisible("Tail1") > 1) && (this.getEnergyPastArmor(15.9F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Tail1 Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                }
                if ((s.endsWith("li1") || s.endsWith("li2")) && (World.Rnd().nextFloat() < (1.0D - (0.92D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingLIn") > 1) && (this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D2", shot.initiator);
                }
                if ((s.endsWith("ri1") || s.endsWith("ri2")) && (World.Rnd().nextFloat() < (1.0D - (0.92D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingRIn") > 1) && (this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D2", shot.initiator);
                }
                if ((s.endsWith("lm1") || s.endsWith("lm2")) && (World.Rnd().nextFloat() < (1.0D - (0.86D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingLMid") > 1) && (this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D2", shot.initiator);
                }
                if ((s.endsWith("rm1") || s.endsWith("rm2")) && (World.Rnd().nextFloat() < (1.0D - (0.86D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingRMid") > 1) && (this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D2", shot.initiator);
                }
                if ((s.endsWith("lo1") || s.endsWith("lo2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingLOut") > 1) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D2", shot.initiator);
                }
                if ((s.endsWith("ro1") || s.endsWith("ro2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingROut") > 1) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
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
                if ((s.endsWith("k1") || s.endsWith("k2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Keel spars damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D0", shot.initiator);
                }
                if ((s.endsWith("sr1") || s.endsWith("sr2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Right Stab spars damaged..");
                    this.nextDMGLevels(1, 2, "StabR_D0", shot.initiator);
                }
                if ((s.endsWith("sl1") || s.endsWith("sl2")) && (World.Rnd().nextFloat() < (1.0D - (0.79D * Math.abs(Aircraft.v1.x)))) && (this.chunkDamageVisible("WingROut") > 1) && (this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                    this.mydebuggunnery("*** Left Stab spars damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D0", shot.initiator);
                }
            }
            if (s.startsWith("xxbomb") && (World.Rnd().nextFloat() < 0.01F) && (this.FM.CT.Weapons[3] != null) && this.FM.CT.Weapons[3][0].haveBullets()) {
                this.mydebuggunnery("*** Bomb Payload Detonates..");
                this.FM.AS.hitTank(shot.initiator, 0, 100);
                this.FM.AS.hitTank(shot.initiator, 1, 100);
                this.FM.AS.hitTank(shot.initiator, 2, 100);
                this.FM.AS.hitTank(shot.initiator, 3, 100);
                this.msgCollision(this, "CF_D0", "CF_D0");
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
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < (shot.power / 200000F)) {
                            this.FM.AS.setEngineStuck(shot.initiator, byte1);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < (shot.power / 50000F)) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 2);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Crank Case Hit - Engine Damaged..");
                        }
                    }
                } else if (s.endsWith("cyls1") || s.endsWith("cyls2")) {
                    if ((this.getEnergyPastArmor(1.3F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[byte1].getCylindersRatio() * 0.6F))) {
                        this.FM.EI.engines[byte1].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 4800F)));
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
                } else if (s.endsWith("supc") && (this.getEnergyPastArmor(0.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.89F)) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, byte1, 0);
                    this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Supercharger Out..");
                }
                if (s.endsWith("oil1") || s.endsWith("oil2") || s.endsWith("oil3")) {
                    if (this.getEnergyPastArmor(0.6F, shot) > 0.0F) {
                        this.FM.AS.hitOil(shot.initiator, byte1);
                    }
                    this.getEnergyPastArmor(0.42F, shot);
                }
                this.mydebuggunnery("*** Engine" + (byte1 + 1) + " state = " + this.FM.AS.astateEngineStates[byte1]);
            }
            if (s.startsWith("xxtank")) {
                int i = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(0.9F, shot) > 0.0F) {
                    if (shot.power < 14100F) {
                        if (this.FM.AS.astateTankStates[i] < 1) {
                            this.FM.AS.hitTank(shot.initiator, i, 1);
                        }
                        if ((this.FM.AS.astateTankStates[i] < 4) && (World.Rnd().nextFloat() < 0.1F)) {
                            this.FM.AS.hitTank(shot.initiator, i, 1);
                        }
                        if ((shot.powerType == 3) && (this.FM.AS.astateTankStates[i] > 1) && (World.Rnd().nextFloat() < 0.07F)) {
                            this.FM.AS.hitTank(shot.initiator, i, 10);
                        }
                    } else {
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
        if (s.startsWith("xmgun")) {
            if (s.endsWith("01")) {
                if ((this.getEnergyPastArmor(5F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.75F)) {
                    this.FM.AS.setJamBullets(0, 0);
                    this.getEnergyPastArmor(11.98F, shot);
                }
            } else if (s.endsWith("02")) {
                if ((this.getEnergyPastArmor(4.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.75F)) {
                    this.FM.AS.setJamBullets(0, 1);
                    this.getEnergyPastArmor(11.98F, shot);
                }
            } else if (s.endsWith("03")) {
                if ((this.getEnergyPastArmor(4.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.75F)) {
                    this.FM.AS.setJamBullets(0, 2);
                    this.getEnergyPastArmor(11.98F, shot);
                }
            } else if (s.endsWith("04") && (this.getEnergyPastArmor(4.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.75F)) {
                this.FM.AS.setJamBullets(0, 3);
                this.getEnergyPastArmor(11.98F, shot);
            }
        }
        if (s.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 2) {
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
        } else if (s.startsWith("xtail")) {
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Tail1") < 2) {
                this.hitChunk("Tail1", shot);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            }
            if (World.Rnd().nextFloat() < 0.1F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            }
        } else if (s.startsWith("xkeel1")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Keel1") < 2) {
                this.hitChunk("Keel1", shot);
            }
        } else if (s.startsWith("xkeel2")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Keel2") < 2) {
                this.hitChunk("Keel2", shot);
            }
        } else if (s.startsWith("xrudder1")) {
            this.setControlDamage(shot, 2);
            this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xrudder2")) {
            this.setControlDamage(shot, 2);
            this.hitChunk("Rudder2", shot);
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
            if (this.chunkDamageVisible("WingLIn") < 2) {
                this.hitChunk("WingLIn", shot);
            }
        } else if (s.startsWith("xwingrin")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingRIn") < 2) {
                this.hitChunk("WingRIn", shot);
            }
        } else if (s.startsWith("xwinglmid")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingLMid") < 2) {
                this.hitChunk("WingLMid", shot);
            }
        } else if (s.startsWith("xwingrmid")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingRMid") < 2) {
                this.hitChunk("WingRMid", shot);
            }
        } else if (s.startsWith("xwinglout")) {
            if (this.chunkDamageVisible("WingLOut") < 2) {
                this.hitChunk("WingLOut", shot);
            }
        } else if (s.startsWith("xwingrout")) {
            if (this.chunkDamageVisible("WingROut") < 2) {
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
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte2 = 0;
            int j;
            if (s.endsWith("a") || s.endsWith("abomb")) {
                byte2 = 1;
                j = s.charAt(6) - 49;
            } else if (s.endsWith("b") || s.endsWith("bbomb")) {
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

    protected void moveFan(float f) {
        if (this.bDynamoOperational) {
            this.pk = Math.abs((int) (this.FM.Vwld.length() / 14D));
            if (this.pk >= 1) {
                this.pk = 1;
            }
        }
        if (this.bDynamoRotary != (this.pk == 1)) {
            this.bDynamoRotary = this.pk == 1;
            this.hierMesh().chunkVisible("Dina_D0", !this.bDynamoRotary);
            this.hierMesh().chunkVisible("PropDina_D0", this.bDynamoRotary);
        }
        this.dynamoOrient = this.bDynamoRotary ? (this.dynamoOrient - 17.987F) % 360F : (float) (this.dynamoOrient - (this.FM.Vwld.length() * 1.5444015264511108D)) % 360F;
        this.hierMesh().chunkSetAngles("Dina_D0", 0.0F, this.dynamoOrient, 0.0F);
        super.moveFan(f);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        if (i == 19) {
            this.FM.Gears.hitCentreGear();
        }
        return super.cutFM(i, j, actor);
    }

    protected void moveBayDoor(float f) {
        this.hierMesh().chunkSetAngles("DoorL_D0", 0.0F, 70F * f, 0.0F);
        this.hierMesh().chunkSetAngles("DoorR_D0", 0.0F, 70F * f, 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -0.26F);
        this.hierMesh().chunkSetLocate("LegFairing_D0", Aircraft.xyz, Aircraft.ypr);
        this.bayDoorAngle = f;
    }

    public float    fSightCurAltitude;
    public float    fSightCurSpeed;
    public float    fSightCurForwardAngle;
    public float    fSightSetForwardAngle;
    public float    fSightCurSideslip;
    public boolean  bPitUnfocused;
    public boolean  bPilot1Killed;
    public boolean  bPilot2Killed;
    public boolean  bPilot3Killed;
    public boolean  bPilot4Killed;
    public boolean  bPilot5Killed;
    public boolean  bPilot5KilledInBombPos;
    public float    bayDoorAngle;
    private boolean bDynamoOperational;
    private float   dynamoOrient;
    private boolean bDynamoRotary;
    private int     pk;

    static {
        Property.set(CantZ506.class, "originCountry", PaintScheme.countryItaly);
    }
}
