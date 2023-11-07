package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public abstract class ME_210 extends Scheme2 {

    public ME_210() {
        this.kangle0 = 0.0F;
        this.kangle1 = 0.0F;
        this.slpos = 0.0F;
        this.llpos = 0.0F;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.hierMesh().chunkVisible("Oil1_D0", true);
        this.hierMesh().chunkVisible("Oil2_D0", true);
    }

    public void update(float f) {
        if (this.FM.getSpeed() > 5F) {
            this.slpos = 0.7F * this.slpos + 0.13F * (this.FM.getAOA() <= 6.6F ? 0.0F : 0.07F);
            this.resetYPRmodifier();
            xyz[0] = this.slpos;
            this.hierMesh().chunkSetLocate("SlatL_D0", xyz, ypr);
            xyz[0] = -this.slpos;
            this.hierMesh().chunkSetLocate("SlatR_D0", xyz, ypr);
        }
        this.hierMesh().chunkSetAngles("WaterL_D0", 0.0F, -17F * this.kangle0, 0.0F);
        this.hierMesh().chunkSetAngles("WaterL1_D0", 0.0F, -17F * this.kangle0, 0.0F);
        this.hierMesh().chunkSetAngles("OilL_D0", 0.0F, -30F * this.kangle0, 0.0F);
        this.kangle0 = 0.95F * this.kangle0 + 0.05F * this.FM.EI.engines[0].getControlRadiator();
        this.hierMesh().chunkSetAngles("WaterR_D0", 0.0F, -17F * this.kangle1, 0.0F);
        this.hierMesh().chunkSetAngles("WaterR1_D0", 0.0F, 17F * this.kangle1, 0.0F);
        this.hierMesh().chunkSetAngles("OilR_D0", 0.0F, -30F * this.kangle1, 0.0F);
        this.kangle1 = 0.95F * this.kangle1 + 0.05F * this.FM.EI.engines[1].getControlRadiator();
        this.FM.turret[1].target = this.FM.turret[0].target;
        if (this.FM.AS.bLandingLightOn) {
            if (this.llpos < 1.0F) {
                this.llpos += 0.5F * f;
                this.hierMesh().chunkSetAngles("LLamp_D0", 0.0F, 0.0F, 90F * this.llpos);
            }
        } else if (this.llpos > 0.0F) {
            this.llpos -= 0.5F * f;
            this.hierMesh().chunkSetAngles("LLamp_D0", 0.0F, 0.0F, 90F * this.llpos);
        }
        super.update(f);
    }

    protected void moveAirBrake(float f) {
        if (f > 0.05F) {
            for (int i = 1; i < 23; i++)
                this.hierMesh().chunkVisible("Brake" + (i >= 10 ? "" + i : "0" + i) + "_D0", true);

            this.hierMesh().chunkSetAngles("Brake01_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake02_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake03_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake04_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake05_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake06_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake07_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake08_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake09_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake10_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake12_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake13_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake14_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake15_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake16_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake17_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake18_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake19_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake20_D0", 0.0F, 90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("Brake21_D0", 0.0F, 90F * f, 0.0F);
        } else for (int j = 1; j < 23; j++)
            this.hierMesh().chunkVisible("Brake" + (j >= 10 ? "" + j : "0" + j) + "_D0", false);
    }

    protected void moveBayDoor(float f) {
        this.hierMesh().chunkSetAngles("Bay1_D0", 0.0F, -90F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay2_D0", 0.0F, -90F * f, 0.0F);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                this.debuggunnery("Armor: Hit..");
                if (s.endsWith("p1")) this.getEnergyPastArmor(World.Rnd().nextFloat(0.0F, 45.2F) / (Math.abs(v1.x) + 0.0001D), shot);
                // TODO: Fixed by SAS~Storebror, fixing repeated conditional test
//                else if (s.endsWith("p2") || s.endsWith("p2")) {
                else if (s.endsWith("p2")) {
                    this.getEnergyPastArmor(World.Rnd().nextFloat(8F, 12F), shot);
                    if (shot.power <= 0.0F) {
                        debugprintln(this, "*** Armor: Nose Armor: Bullet Reflected..");
                        this.doRicochetBack(shot);
                    }
                } else if (s.endsWith("p4")) {
                    this.getEnergyPastArmor(World.Rnd().nextFloat(20F, 30F) / (Math.abs(v1.x) + 0.0001D), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    this.debuggunnery("Armor: Armor Glass: Hit..");
                    if (shot.power <= 0.0F) {
                        this.debuggunnery("Armor: Armor Glass: Bullet Stopped..");
                        this.doRicochetBack(shot);
                    }
                } else if (s.endsWith("p5")) this.getEnergyPastArmor(10.1D / (Math.abs(v1.z) + 0.0001D), shot);
                else if (s.endsWith("p6")) this.getEnergyPastArmor(12D + World.Rnd().nextFloat(10F, 30F) / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.endsWith("p7")) this.getEnergyPastArmor(8.08D / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.endsWith("p8")) {
                    this.getEnergyPastArmor(1.01D / (Math.abs(v1.x) + 0.0001D), shot);
                    if (shot.powerType == 3) shot.powerType = 0;
                } else if (s.endsWith("p9")) this.getEnergyPastArmor(5.05D / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.endsWith("p10")) this.getEnergyPastArmor(9.09D / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.endsWith("p11")) {
                    this.getEnergyPastArmor(64.64D / (Math.abs(v1.x) + 0.0001D), shot);
                    if (shot.power <= 0.0F) this.doRicochetBack(shot);
                } else if (s.startsWith("xxarmore")) this.getEnergyPastArmor(5.05D / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.startsWith("xxarmorw")) this.getEnergyPastArmor(5.05D / (Math.abs(v1.z) + 0.0001D), shot);
                return;
            }
            if (s.startsWith("xxcontrols")) {
                this.debuggunnery("Controls: Hit..");
                int i = s.charAt(10) - 48;
                switch (i) {
                    default:
                        break;

                    case 1:
                        if (this.getEnergyPastArmor(2.45F, shot) > 0.0F) {
                            this.debuggunnery("Controls: Barbette Controls: Disabled..");
                            this.FM.turret[0].bIsOperable = false;
                            if (this.FM.CT.Weapons[10] != null) {
                                if (this.FM.CT.Weapons[10][0] != null) this.FM.AS.setJamBullets(10, 0);
                                if (this.FM.CT.Weapons[10][1] != null) this.FM.AS.setJamBullets(10, 1);
                            }
                        }
                        break;

                    case 2:
                    case 3:
                        if (this.getEnergyPastArmor(0.002F, shot) > 0.0F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            this.debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                        }
                        break;

                    case 4:
                        if (this.getEnergyPastArmor(World.Rnd().nextFloat(6.8F, 12F), shot) > 0.0F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            this.debuggunnery("Controls: Elevator Controls: Destroyed..");
                        }
                        break;

                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        if (this.getEnergyPastArmor(0.12F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            this.debuggunnery("Controls: Aileron Controls: Disabled..");
                        }
                        break;
                }
                return;
            }
            if (s.startsWith("xxspar")) {
                this.debuggunnery("Spar Construction: Hit..");
                if (s.startsWith("xxsparli") && this.chunkDamageVisible("WingLIn") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && this.chunkDamageVisible("WingRIn") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlm") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparrm") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && this.chunkDamageVisible("WingLOut") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && this.chunkDamageVisible("WingROut") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxlock")) {
                this.debuggunnery("Lock Construction: Hit..");
                if (s.startsWith("xxlockr") && this.getEnergyPastArmor(6.56F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if (s.startsWith("xxlockvl") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F) {
                    this.debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                }
                if (s.startsWith("xxlockvr") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F) {
                    this.debuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                }
                if (s.startsWith("xxlockal") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F) {
                    this.debuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
                }
                if (s.startsWith("xxlockar") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F) {
                    this.debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                int j = s.charAt(5) - 49;
                this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Hit..");
                if (s.endsWith("prop")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F) if (World.Rnd().nextFloat() < 0.5F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 3);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Prop Governor Hit, Disabled..");
                    } else {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 4);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Prop Governor Hit, Damaged..");
                    }
                } else if (s.endsWith("gear")) {
                    if (this.getEnergyPastArmor(4.6F, shot) > 0.0F) if (World.Rnd().nextFloat() < 0.5F) {
                        this.FM.EI.engines[j].setEngineStuck(shot.initiator);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Bullet Jams Reductor Gear..");
                    } else {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 3);
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 4);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Reductor Gear Damaged, Prop Governor Failed..");
                    }
                } else if (s.endsWith("supc")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 0);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Supercharger Disabled..");
                    }
                } else if (s.endsWith("feed")) {
                    if (this.getEnergyPastArmor(3.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F && this.FM.EI.engines[j].getPowerOutput() > 0.7F) {
                        this.FM.AS.hitEngine(shot.initiator, j, 100);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Pressurized Fuel Line Pierced, Fuel Flamed..");
                    }
                } else if (s.endsWith("fuel")) {
                    if (this.getEnergyPastArmor(1.1F, shot) > 0.0F) {
                        this.FM.EI.engines[j].setEngineStops(shot.initiator);
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Fuel Line Stalled, Engine Stalled..");
                    }
                } else if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(2.1F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 175000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, j);
                            this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Bullet Jams Crank Ball Bearing..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 50000F) {
                            this.FM.AS.hitEngine(shot.initiator, j, 2);
                            this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[j].getReadyness() + "..");
                        }
                        this.FM.EI.engines[j].setReadyness(shot.initiator, this.FM.EI.engines[j].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[j].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(World.Rnd().nextFloat(22.5F, 33.6F), shot);
                } else if (s.endsWith("cyl1") || s.endsWith("cyl2")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[j].getCylindersRatio() * 1.75F) {
                        this.FM.EI.engines[j].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 4800F)));
                        this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Cylinders Hit, " + this.FM.EI.engines[j].getCylindersOperable() + "/" + this.FM.EI.engines[j].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < shot.power / 24000F) {
                            this.FM.AS.hitEngine(shot.initiator, j, 3);
                            this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Cylinders Hit, Engine Fires..");
                        }
                        if (World.Rnd().nextFloat() < 0.01F) {
                            this.FM.AS.setEngineStuck(shot.initiator, j);
                            this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Bullet Jams Piston Head..");
                        }
                        this.getEnergyPastArmor(22.5F, shot);
                    }
                } else if (s.endsWith("mag1") || s.endsWith("mag2")) {
                    int l1 = s.charAt(9) - 49;
                    this.FM.EI.engines[j].setMagnetoKnockOut(shot.initiator, l1);
                    this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Magneto " + l1 + " Destroyed..");
                } else if (s.endsWith("oil1")) {
                    this.FM.AS.hitOil(shot.initiator, j);
                    this.debuggunnery("Engine Module (" + (j != 0 ? "Right" : "Left") + "): Oil Radiator Hit..");
                }
                return;
            }
            if (s.startsWith("xxoil")) {
                if (this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 6.879F), shot) > 0.0F) {
                    int k = s.charAt(5) - 49;
                    this.FM.AS.hitOil(shot.initiator, k);
                    this.getEnergyPastArmor(0.22F, shot);
                    this.debuggunnery("Engine Module (" + (k != 0 ? "Right" : "Left") + "): Oil Tank Pierced..");
                }
                return;
            }
            if (s.startsWith("xxw")) {
                if (this.getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 4.27F), shot) > 0.0F) {
                    int l = s.charAt(3) - 49;
                    if (this.FM.AS.astateEngineStates[l] == 0) {
                        this.debuggunnery("Engine Module (" + (l != 0 ? "Right" : "Left") + "): Water Radiator Pierced..");
                        this.FM.AS.hitEngine(shot.initiator, l, 2);
                        this.FM.AS.doSetEngineState(shot.initiator, l, 2);
                    }
                    this.getEnergyPastArmor(2.22F, shot);
                }
                return;
            }
            if (s.startsWith("xxtank")) {
                int i1 = s.charAt(6) - 48;
                switch (i1) {
                    case 1:
                        i1 = 0;
                        break;

                    case 2:
                    case 3:
                        i1 = 1;
                        break;

                    case 4:
                    case 5:
                        i1 = 2;
                        break;

                    case 6:
                        i1 = 3;
                        break;
                }
                if (this.getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 2.23F), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                    if (this.FM.AS.astateTankStates[i1] == 0) {
                        this.debuggunnery("Fuel Tank " + (i1 + 1) + ": Pierced..");
                        this.FM.AS.hitTank(shot.initiator, i1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i1, 1);
                    } else if (this.FM.AS.astateTankStates[i1] == 1) {
                        this.debuggunnery("Fuel Tank " + (i1 + 1) + ": Pierced..");
                        this.FM.AS.hitTank(shot.initiator, i1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i1, 2);
                    }
                    if (shot.powerType == 3 && World.Rnd().nextFloat() < 0.5F) {
                        this.FM.AS.hitTank(shot.initiator, 2, 2);
                        this.debuggunnery("Fuel Tank " + (i1 + 1) + ": Hit..");
                    }
                }
                return;
            }
            if (s.startsWith("xxmgun")) {
                if (s.endsWith("01")) {
                    this.debuggunnery("Nose Gun: Disabled..");
                    this.FM.AS.setJamBullets(0, 0);
                }
                if (s.endsWith("02")) {
                    this.debuggunnery("Nose Gun: Disabled..");
                    this.FM.AS.setJamBullets(0, 1);
                }
                this.getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 12.96F), shot);
            }
            if (s.startsWith("xxcannon")) {
                if (s.endsWith("01")) {
                    this.debuggunnery("Nose Cannon: Disabled..");
                    this.FM.AS.setJamBullets(1, 0);
                }
                if (s.endsWith("02")) {
                    this.debuggunnery("Nose Cannon: Disabled..");
                    this.FM.AS.setJamBullets(1, 1);
                }
                this.getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
            }
            if (s.startsWith("xxbarbette")) {
                if (this.getEnergyPastArmor(World.Rnd().nextFloat(2.58F, 28.37F), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                    this.FM.turret[0].bIsOperable = false;
                    if (this.FM.CT.Weapons[10] != null) {
                        if (this.FM.CT.Weapons[10][0] != null) this.FM.AS.setJamBullets(10, 0);
                        if (this.FM.CT.Weapons[10][1] != null) this.FM.AS.setJamBullets(10, 1);
                    }
                }
                this.getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
            }
            if (s.startsWith("xxbomb") && World.Rnd().nextFloat() < 0.01F && this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0].haveBullets()) {
                this.debuggunnery("Bomb Payload Detonates..");
                this.FM.AS.hitTank(shot.initiator, 0, 10);
                this.FM.AS.hitTank(shot.initiator, 1, 10);
                this.FM.AS.hitTank(shot.initiator, 2, 10);
                this.FM.AS.hitTank(shot.initiator, 3, 10);
                this.nextDMGLevels(3, 2, "CF_D0", shot.initiator);
            }
            return;
        }
        if (s.startsWith("xcf") || s.startsWith("xcockpit") || s.startsWith("xnose")) {
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
            if (!s.startsWith("xcockpit"));
        } else if (s.startsWith("xengine")) {
            int j1 = s.charAt(7) - 48;
            if (this.chunkDamageVisible("Engine" + j1) < 2) this.hitChunk("Engine" + j1, shot);
        } else if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", shot);
        } else if (s.startsWith("xkeel")) {
            if (this.chunkDamageVisible("Keel1") < 3) this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xrudder")) {
            if (this.chunkDamageVisible("Rudder1") < 1) this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xstab")) {
            if (s.startsWith("xstabl") && this.chunkDamageVisible("StabL") < 3) this.hitChunk("StabL", shot);
            if (s.startsWith("xstabr") && this.chunkDamageVisible("StabR") < 3) this.hitChunk("StabR", shot);
        } else if (s.startsWith("xvator")) {
            if (s.startsWith("xvatorl") && this.chunkDamageVisible("VatorL") < 1) this.hitChunk("VatorL", shot);
            if (s.startsWith("xvatorr") && this.chunkDamageVisible("VatorR") < 1) this.hitChunk("VatorR", shot);
        } else if (s.startsWith("xwing")) {
            if (s.startsWith("xwinglin") && this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", shot);
            if (s.startsWith("xwingrin") && this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", shot);
            if (s.startsWith("xwinglmid") && this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", shot);
            if (s.startsWith("xwingrmid") && this.chunkDamageVisible("WingRMid") < 3) this.hitChunk("WingRMid", shot);
            if (s.startsWith("xwinglout") && this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", shot);
            if (s.startsWith("xwingrout") && this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", shot);
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel") && this.chunkDamageVisible("AroneL") < 1) this.hitChunk("AroneL", shot);
            if (s.startsWith("xaroner") && this.chunkDamageVisible("AroneR") < 1) this.hitChunk("AroneR", shot);
        } else if (s.startsWith("xgear")) {
            if (s.endsWith("1") && World.Rnd().nextFloat() < 0.05F) {
                this.debuggunnery("Hydro System: Disabled..");
                this.FM.AS.setInternalDamage(shot.initiator, 0);
            }
            if (s.endsWith("2")) {
                if (World.Rnd().nextFloat() < 0.1F && this.getEnergyPastArmor(World.Rnd().nextFloat(6.8F, 29.35F), shot) > 0.0F) {
                    this.debuggunnery("Undercarriage: Stuck..");
                    this.FM.AS.setInternalDamage(shot.initiator, 3);
                }
                String s1 = "" + s.charAt(5);
                this.hitChunk("Gear" + s1.toUpperCase() + "2", shot);
            }
        } else if (s.startsWith("xturret")) {
            int k1 = s.charAt(7) - 49;
            if (this.getEnergyPastArmor(0.25F, shot) > 0.0F) {
                this.debuggunnery("Armament System: Turret (" + (k1 + 1) + ") Machine Gun: Disabled..");
                this.FM.AS.setJamBullets(10, k1);
                this.getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 26.35F), shot);
            }
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int i2;
            if (s.endsWith("a")) {
                byte0 = 1;
                i2 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                i2 = s.charAt(6) - 49;
            } else i2 = s.charAt(5) - 49;
            this.hitFlesh(i2, shot, byte0);
        }
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        float f1 = 10F * Math.min(f, 0.1F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC9_D0", 0.0F, 80F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, 130F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, 130F * f1, 0.0F);
        f1 = 10F * (f >= 0.5F ? Math.min(1.0F - f, 0.1F) : Math.min(f, 0.1F));
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -115F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL9_D0", 0.0F, -85F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, -140F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, -55F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, -55F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -115F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR9_D0", 0.0F, -85F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, -140F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, -55F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, -55F * f1, 0.0F);
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    public void moveSteering(float f) {
        if (this.FM.CT.getGear() < 0.99F) return;
        else {
            this.hierMesh().chunkSetAngles("GearC2_D0", f, 0.0F, 0.0F);
            return;
        }
    }

    public void moveWheelSink() {
        if (this.FM.CT.getGear() < 0.99F) return;
        else {
            this.resetYPRmodifier();
            xyz[1] = cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.35F, 0.0F, 0.35F);
            ypr[1] = -85F;
            this.hierMesh().chunkSetLocate("GearL9_D0", xyz, ypr);
            xyz[1] = -cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.35F, 0.0F, 0.35F);
            ypr[1] = -85F;
            this.hierMesh().chunkSetLocate("GearR9_D0", xyz, ypr);
            return;
        }
    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 1:
                this.FM.turret[0].setHealth(f);
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                if (!this.FM.AS.bIsAboutToBailout) this.hierMesh().chunkVisible("Gore1_D0", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                if (!this.FM.AS.bIsAboutToBailout) this.hierMesh().chunkVisible("Gore2_D0", true);
                break;
        }
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

            case 11:
            case 19:
                this.hierMesh().chunkVisible("Wire_D0", false);
                break;

            case 34:
                this.FM.AS.hitEngine(this, 0, 2);
                if (World.Rnd().nextFloat() < 0.66F) this.FM.AS.hitEngine(this, 0, 2);
                break;

            case 37:
                this.FM.AS.hitEngine(this, 1, 2);
                if (World.Rnd().nextFloat() < 0.66F) this.FM.AS.hitEngine(this, 1, 2);
                break;
        }
        return super.cutFM(i, j, actor);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.06F) this.FM.AS.hitTank(this, 1, 1);
            if (this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.06F) this.FM.AS.hitTank(this, 2, 1);
            if (this.FM.AS.astateTankStates[0] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
            if (this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.08F) this.FM.AS.hitTank(this, 1, 1);
            if (this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.08F) this.FM.AS.hitTank(this, 0, 1);
            if (this.FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.08F) this.FM.AS.hitTank(this, 3, 1);
            if (this.FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.08F) this.FM.AS.hitTank(this, 2, 1);
        }
        for (int i = 1; i < 3; i++)
            if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f < -65F) {
                    f = -65F;
                    flag = false;
                }
                if (f > 65F) {
                    f = 65F;
                    flag = false;
                }
                if (f1 < -10F) {
                    f1 = -10F;
                    flag = false;
                }
                if (f1 > 40F) {
                    f1 = 40F;
                    flag = false;
                }
                break;

            case 1:
                if (f < -65F) {
                    f = -65F;
                    flag = false;
                }
                if (f > 65F) {
                    f = 65F;
                    flag = false;
                }
                if (f1 < -40F) {
                    f1 = -40F;
                    flag = false;
                }
                if (f1 > 10F) {
                    f1 = 10F;
                    flag = false;
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    protected void moveFlap(float f) {
        float f1 = -45F * f;
        this.hierMesh().chunkSetAngles("Flap1_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("Flap2_D0", 0.0F, f1, 0.0F);
    }

    public static boolean checkTurretVisibilityPlaneBlocking(int turretIndex) {
        return false;
    }

    private float kangle0;
    private float kangle1;
    private float slpos;
    private float llpos;

    static {
        Class class1 = ME_210.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
