package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public abstract class RE_2001xyz extends Scheme1 implements TypeFighter, TypeStormovik {

    public RE_2001xyz() {
        this.bRaiseTailWheel = false;
        this.counter = 0;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.Gears.computePlaneLandPose(this.FM);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.startsWith("2x100kg") || thisWeaponsName.endsWith("2x100kg")) {
            hierMesh.chunkVisible("RackL_D0", true);
            hierMesh.chunkVisible("RackR_D0", true);
        }
        if (thisWeaponsName.equals("1x100kg") || thisWeaponsName.equals("1x160kg") || thisWeaponsName.startsWith("1x250kg")) {
            hierMesh.chunkVisible("LeverBase_d0", true);
            hierMesh.chunkVisible("BombLever_d0", true);
            hierMesh.chunkVisible("BombHook_d0", true);
            hierMesh.chunkVisible("BombRack_d0", true);
        }
        if (thisWeaponsName.equals("1x630kg") || thisWeaponsName.equals("1x630kg") || thisWeaponsName.startsWith("1x630kg")) {
            hierMesh.chunkVisible("MGun03_d0", false);
            hierMesh.chunkVisible("MGun04_d0", false);
            hierMesh.chunkVisible("BombRack_d0", true);
        }
        if (thisWeaponsName.equals("1x240lTank") || thisWeaponsName.equals("1x240lTank") || thisWeaponsName.startsWith("1x240lTank")) {
            hierMesh.chunkVisible("BombRack_d0", true);
        }
        if (thisWeaponsName.equals("CN") || thisWeaponsName.equals("CN") || thisWeaponsName.startsWith("CN")) {
            hierMesh.chunkVisible("MGun03_d0", false);
            hierMesh.chunkVisible("MGun04_d0", false);
            hierMesh.chunkVisible("GondolaLeft", true);
            hierMesh.chunkVisible("GondolaRight", true);
            hierMesh.chunkVisible("FlameDumper", true);
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
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 19:
                this.FM.Gears.hitCentreGear();
                break;

            case 3:
                if (World.Rnd().nextInt(0, 99) < 1) {
                    this.FM.AS.hitEngine(this, 0, 4);
                    this.hitProp(0, j, actor);
                    this.FM.EI.engines[0].setEngineStuck(actor);
                    return this.cut("engine1");
                } else {
                    this.FM.AS.setEngineDies(this, 0);
                    return false;
                }
        }
        return super.cutFM(i, j, actor);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        }
        if (flag) {
            if ((this.FM.AS.astateEngineStates[0] > 3) && (World.Rnd().nextFloat() < 0.39F)) {
                this.FM.AS.hitTank(this, 0, 1);
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
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.625F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 100F * f, 0.0F);
        if (f < 0.25F) {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, (-95F * f) / 0.25F, 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, -95F, 0.0F);
        }
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, -100F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 100F * f, 0.0F);
        if (f < 0.25F) {
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, (95F * f) / 0.25F, 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, 95F, 0.0F);
        }
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, -100F * f, 0.0F);
    }

    protected void moveGear(float f) {
        RE_2001xyz.moveGear(this.hierMesh(), f);
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC3_D0", 0.0F, f, 0.0F);
    }

    public void moveWheelSink() {
        float f = 95F * this.FM.CT.getGear();
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.2085F, 0.0F, -0.2F);
        Aircraft.ypr[1] = -f;
        this.hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.ypr[1] = f;
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.2085F, 0.0F, -0.2F);
        this.hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveFlap(float f) {
        float f1 = -35F * f;
        this.hierMesh().chunkSetAngles("FlapL1_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("FlapR1_D0", 0.0F, f1, 0.0F);
        f1 = -50F * f;
        this.hierMesh().chunkSetAngles("FlapL2_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("FlapR2_D0", 0.0F, f1, 0.0F);
    }

    public void update(float f) {
        if (this.bRaiseTailWheel) {
            this.counter++;
            this.resetYPRmodifier();
            Aircraft.xyz[1] = (-0.2F * this.counter) / 50F;
            this.hierMesh().chunkSetLocate("Gearc2_D0", Aircraft.xyz, Aircraft.ypr);
            if (this.counter >= 50) {
                this.bRaiseTailWheel = false;
            }
        }
        for (int i = 1; i < 6; i++) {
            this.hierMesh().chunkSetAngles("RadiatorL1_D0", 0.0F, -30F * this.FM.EI.engines[0].getControlRadiator(), 0.0F);
            this.hierMesh().chunkSetAngles("RadiatorR1_D0", 0.0F, -30F * this.FM.EI.engines[0].getControlRadiator(), 0.0F);
        }

        super.update(f);
    }

    protected void setControlDamage(Shot shot, int i) {
        if ((World.Rnd().nextFloat() < 0.002F) && (this.getEnergyPastArmor(4F, shot) > 0.0F)) {
            this.FM.AS.setControlsDamage(shot.initiator, i);
        }
    }

    protected void moveAileron(float f) {
        float f1 = -(f * 23F);
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, f1, 0.0F);
        f1 = -(f * 23F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, f1, 0.0F);
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -27F * f, 0.0F);
    }

    protected void moveElevator(float f) {
        if (f < 0.0F) {
            this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -20F * f, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -20F * f, 0.0F);
        } else {
            this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -30F * f, 0.0F);
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        String s1 = s.toLowerCase();
        if (s1.startsWith("xx")) {
            if (s1.startsWith("xxarmor")) {
                if (s1.startsWith("xxarmorp")) {
                    int i = s1.charAt(8) - 48;
                    switch (i) {
                        case 1:
                            this.getEnergyPastArmor(22.76D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                            if (shot.power <= 0.0F) {
                                this.doRicochetBack(shot);
                            }
                            break;

                        case 2:
                            this.getEnergyPastArmor(9.366F, shot);
                            break;

                        case 3:
                            this.getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                            break;
                    }
                }
            } else if (s1.startsWith("xxcontrols")) {
                int j = s1.charAt(10) - 48;
                switch (j) {
                    case 1:
                    case 2:
                        if (this.getEnergyPastArmor(0.25F / ((float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)) + 0.0001F), shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < 0.05F) {
                                this.mydebuggunnery("Controls: Elevator Wiring Hit, Elevator Controls Disabled..");
                                this.FM.AS.setControlsDamage(shot.initiator, 1);
                            }
                            if (World.Rnd().nextFloat() < 0.75F) {
                                this.mydebuggunnery("Controls: Rudder Wiring Hit, Rudder Controls Disabled..");
                                this.FM.AS.setControlsDamage(shot.initiator, 2);
                            }
                        }
                        break;

                    case 3:
                        if (this.getEnergyPastArmor(3.2F, shot) > 0.0F) {
                            this.mydebuggunnery("*** Control Column: Hit, Controls Destroyed..");
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;

                    case 4:
                        if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                            this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                            this.mydebuggunnery("Quadrant: Hit, Engine Controls Disabled..");
                        }
                        break;

                    case 5:
                    case 7:
                        if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            this.mydebuggunnery("*** Aileron Controls: Control Crank Destroyed..");
                        }
                        break;

                    case 6:
                    case 8:
                        if ((this.getEnergyPastArmor(4D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.mydebuggunnery("Controls: Aileron Wiring Hit, Aileron Controls Disabled..");
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;
                }
            } else if (s1.startsWith("xxspar")) {
                this.mydebuggunnery("Spar Construction: Hit..");
                if (s1.startsWith("xxspartli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.mydebuggunnery("Spar Construction: WingLIn Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s1.startsWith("xxspartri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.mydebuggunnery("Spar Construction: WingRIn Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s1.startsWith("xxspartlo") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.mydebuggunnery("Spar Construction: WingLMid Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s1.startsWith("xxspartro") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.mydebuggunnery("Spar Construction: WingRMid Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s1.startsWith("xxspart") && (this.chunkDamageVisible("Tail1") > 2) && (this.getEnergyPastArmor(3.86F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                    this.mydebuggunnery("Spar Construction: Tail1 Ribs Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
            } else {
                if (s1.startsWith("xxlock")) {
                    this.mydebuggunnery("Lock Construction: Hit..");
                    if (s1.startsWith("xxlockr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.mydebuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                    }
                    if (s1.startsWith("xxlockvl") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.mydebuggunnery("Lock Construction: VatorL Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                    }
                    if (s1.startsWith("xxlockvr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.mydebuggunnery("Lock Construction: VatorR Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                    }
                    if (s1.startsWith("xxlockall") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.mydebuggunnery("Lock Construction: AroneL Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
                    }
                    if (s1.startsWith("xxlockalr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.mydebuggunnery("Lock Construction: AroneR Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
                    }
                }
                if (s1.startsWith("xxeng")) {
                    if ((s1.endsWith("prop") || s1.endsWith("pipe")) && (this.getEnergyPastArmor(0.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                        this.FM.EI.engines[0].setKillPropAngleDevice(shot.initiator);
                    }
                    if (s1.endsWith("case")) {
                        if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < (shot.power / 140000F)) {
                                this.FM.AS.setEngineStuck(shot.initiator, 0);
                                this.mydebuggunnery("*** Engine Crank Case Hit - Engine Stucks..");
                            }
                            if (World.Rnd().nextFloat() < (shot.power / 85000F)) {
                                this.FM.AS.hitEngine(shot.initiator, 0, 2);
                                this.mydebuggunnery("*** Engine Crank Case Hit - Engine Damaged..");
                            }
                        } else if (World.Rnd().nextFloat() < 0.01F) {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, 1);
                        } else {
                            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - 0.002F);
                            this.mydebuggunnery("*** Engine Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                        }
                        this.getEnergyPastArmor(12F, shot);
                    }
                    if (s1.endsWith("cyls")) {
                        if ((this.getEnergyPastArmor(6.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[0].getCylindersRatio() * 0.75F))) {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                            this.mydebuggunnery("*** Engine Cylinders Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                            if (World.Rnd().nextFloat() < (shot.power / 48000F)) {
                                this.FM.AS.hitEngine(shot.initiator, 0, 2);
                                this.mydebuggunnery("*** Engine Cylinders Hit - Engine Fires..");
                            }
                        }
                        this.getEnergyPastArmor(25F, shot);
                    }
                    if (s1.endsWith("supc")) {
                        if (this.getEnergyPastArmor(0.05F, shot) > 0.0F) {
                            this.FM.EI.engines[0].setKillCompressor(shot.initiator);
                        }
                        this.getEnergyPastArmor(2.0F, shot);
                    }
                    if (s1.startsWith("xxeng1oil")) {
                        this.FM.AS.hitOil(shot.initiator, 0);
                        this.mydebuggunnery("*** Engine Module: Oil Radiator Hit..");
                    }
                    this.mydebuggunnery("*** Engine state = " + this.FM.AS.astateEngineStates[0]);
                } else if (s1.startsWith("xxtank")) {
                    int k = s1.charAt(6) - 49;
                    if ((this.getEnergyPastArmor(0.15F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                        if (this.FM.AS.astateTankStates[k] == 0) {
                            this.mydebuggunnery("Fuel System: Fuel Tank Pierced..");
                            this.FM.AS.hitTank(shot.initiator, k, 1);
                            this.FM.AS.doSetTankState(shot.initiator, k, 1);
                        } else if (this.FM.AS.astateTankStates[k] == 1) {
                            this.mydebuggunnery("Fuel System: Fuel Tank Pierced (2)..");
                            this.FM.AS.hitTank(shot.initiator, k, 1);
                            this.FM.AS.doSetTankState(shot.initiator, k, 2);
                        }
                        if ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.hitTank(shot.initiator, k, 2);
                            this.mydebuggunnery("Fuel System: Fuel Tank Pierced, State Shifted..");
                        }
                    }
                    this.mydebuggunnery("Tank State: " + this.FM.AS.astateTankStates[k]);
                } else if (s1.startsWith("xxmgun")) {
                    if (s1.endsWith("01")) {
                        this.mydebuggunnery("Armament System: Left Machine Gun: Disabled..");
                        this.FM.AS.setJamBullets(0, 0);
                    }
                    if (s1.endsWith("02")) {
                        this.mydebuggunnery("Armament System: Right Machine Gun: Disabled..");
                        this.FM.AS.setJamBullets(1, 0);
                    }
                    this.getEnergyPastArmor(World.Rnd().nextFloat(0.3F, 12.6F), shot);
                }
            }
        } else if (s1.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 3) {
                this.hitChunk("CF", shot);
            }
        } else if (s1.startsWith("xeng")) {
            if (this.chunkDamageVisible("Engine1") < 2) {
                this.hitChunk("Engine1", shot);
            }
        } else if (s1.startsWith("xtail")) {
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Tail1") < 3) {
                this.hitChunk("Tail1", shot);
            }
        } else if (s1.startsWith("xkeel")) {
            this.hitChunk("Keel1", shot);
        } else if (s1.startsWith("xrudder")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Rudder1") < 1) {
                this.hitChunk("Rudder1", shot);
            }
        } else if (s1.startsWith("xstab")) {
            if (s1.startsWith("xstabl")) {
                this.hitChunk("StabL", shot);
            }
            if (s1.startsWith("xstabr")) {
                this.hitChunk("StabR", shot);
            }
        } else if (s1.startsWith("xvator")) {
            if (s1.startsWith("xvatorl") && (this.chunkDamageVisible("VatorL") < 1)) {
                this.hitChunk("VatorL", shot);
            }
            if (s1.startsWith("xvatorr") && (this.chunkDamageVisible("VatorR") < 1)) {
                this.hitChunk("VatorR", shot);
            }
        } else if (s1.startsWith("xwing")) {
            if (s1.startsWith("xwinglin") && (this.chunkDamageVisible("WingLIn") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingLIn", shot);
            }
            if (s1.startsWith("xwingrin") && (this.chunkDamageVisible("WingRIn") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRIn", shot);
            }
            if (s1.startsWith("xwinglmid") && (this.chunkDamageVisible("WingLMid") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingLMid", shot);
            }
            if (s1.startsWith("xwingrmid") && (this.chunkDamageVisible("WingRMid") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRMid", shot);
            }
            if (s1.startsWith("xwinglout") && (this.chunkDamageVisible("WingLOut") < 3)) {
                this.hitChunk("WingLOut", shot);
            }
            if (s1.startsWith("xwingrout") && (this.chunkDamageVisible("WingROut") < 3)) {
                this.hitChunk("WingROut", shot);
            }
        } else if (s1.startsWith("xarone")) {
            if (s1.startsWith("xaronel") && (this.chunkDamageVisible("AroneL") < 1)) {
                this.hitChunk("AroneL", shot);
            }
            if (s1.startsWith("xaroner") && (this.chunkDamageVisible("AroneR") < 1)) {
                this.hitChunk("AroneR", shot);
            }
        } else if (s1.startsWith("xgear")) {
            if ((World.Rnd().nextFloat() < 0.1F) && (this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), shot) > 0.0F)) {
                this.mydebuggunnery("Undercarriage: Stuck..");
                this.FM.Gears.setHydroOperable(false);
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            }
        } else if (s1.startsWith("xoil")) {
            if (World.Rnd().nextFloat() < 0.12F) {
                this.FM.AS.hitOil(shot.initiator, 0);
                this.mydebuggunnery("*** Engine Module: Oil Radiator Hit..");
            }
        } else if (!s1.startsWith("xblister") && (s1.startsWith("xpilot") || s1.startsWith("xhead"))) {
            byte byte0 = 0;
            int l;
            if (s1.endsWith("a")) {
                byte0 = 1;
                l = s1.charAt(6) - 49;
            } else if (s1.endsWith("b")) {
                byte0 = 2;
                l = s1.charAt(6) - 49;
            } else {
                l = s1.charAt(5) - 49;
            }
            this.hitFlesh(l, shot, byte0);
        }
    }

    protected void mydebuggunnery(String s) {
    }

    boolean bRaiseTailWheel;
    int     counter;

    static {
        Class class1 = RE_2001xyz.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "originCountry", PaintScheme.countryItaly);
    }
}
