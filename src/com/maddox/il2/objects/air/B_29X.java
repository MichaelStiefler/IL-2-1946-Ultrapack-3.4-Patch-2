package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Explosion;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.Property;

public abstract class B_29X extends Scheme4 implements TypeTransport {

    public B_29X() {
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxammo0")) {
                int i = s.charAt(7) - 48;
                if (this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.15F) switch (i) {
                    case 3:
                    default:
                        break;

                    case 1:
                        if (World.Rnd().nextFloat() < 0.347F) this.FM.AS.setJamBullets(12, 0);
                        if (World.Rnd().nextFloat() < 0.347F) this.FM.AS.setJamBullets(12, 1);
                        break;

                    case 2:
                        if (World.Rnd().nextFloat() < 0.347F) this.FM.AS.setJamBullets(13, 0);
                        if (World.Rnd().nextFloat() < 0.347F) this.FM.AS.setJamBullets(13, 1);
                        break;

                    case 4:
                        if (World.Rnd().nextFloat() < 0.223F) this.FM.AS.setJamBullets(10, 0);
                        if (World.Rnd().nextFloat() < 0.223F) this.FM.AS.setJamBullets(10, 1);
                        if (World.Rnd().nextFloat() < 0.223F) this.FM.AS.setJamBullets(10, 2);
                        if (World.Rnd().nextFloat() < 0.223F) this.FM.AS.setJamBullets(10, 3);
                        break;

                    case 5:
                        if (World.Rnd().nextFloat() < 0.347F) this.FM.AS.setJamBullets(11, 0);
                        if (World.Rnd().nextFloat() < 0.347F) this.FM.AS.setJamBullets(11, 1);
                        break;
                }
                return;
            }
            if (s.startsWith("xxcontrols")) {
                int j = s.charAt(10) - 48;
                switch (j) {
                    default:
                        break;

                    case 1:
                    case 2:
                    case 3:
                        if (this.getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            debugprintln(this, "*** Aileron Controls Out..");
                        }
                        break;

                    case 4:
                        if (this.getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) this.FM.AS.setControlsDamage(shot.initiator, 1);
                        break;

                    case 5:
                    case 6:
                        if (this.getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) this.FM.AS.setControlsDamage(shot.initiator, 2);
                        break;
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                int k = s.charAt(5) - 49;
                if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 140000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, k);
                            debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 85000F) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Engine Damaged..");
                        }
                    } else if (World.Rnd().nextFloat() < 0.005F) this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, 1);
                    else {
                        this.FM.EI.engines[k].setReadyness(shot.initiator, this.FM.EI.engines[k].getReadyness() - 0.00082F);
                        debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[k].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(12F, shot);
                }
                if (s.endsWith("cyls")) {
                    if (this.getEnergyPastArmor(5.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[k].getCylindersRatio() * 0.75F) {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                        debugprintln(this, "*** Engine (" + k + ") Cylinders Hit, " + this.FM.EI.engines[k].getCylindersOperable() + "/" + this.FM.EI.engines[k].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < shot.power / 18000F) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            debugprintln(this, "*** Engine (" + k + ") Cylinders Hit - Engine Fires..");
                        }
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("mag1")) {
                    this.FM.EI.engines[k].setMagnetoKnockOut(shot.initiator, 0);
                    debugprintln(this, "*** Engine (" + k + ") Module: Magneto #0 Destroyed..");
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("prop") && this.getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                    if (World.Rnd().nextFloat() < 0.5F) this.FM.EI.engines[k].setKillPropAngleDevice(shot.initiator);
                    else this.FM.EI.engines[k].setKillPropAngleDeviceSpeeds(shot.initiator);
                    this.getEnergyPastArmor(15.1F, shot);
                    debugprintln(this, "*** Engine (" + k + ") Module: Prop Governor Fails..");
                }
                if (s.endsWith("oil") && this.getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                    this.FM.AS.setOilState(shot.initiator, k, 1);
                    debugprintln(this, "*** Engine (" + k + ") Module: Oil Filter Pierced..");
                }
                return;
            }
            if (s.startsWith("xxspar")) {
                if (s.startsWith("xxspare1") && this.chunkDamageVisible("Engine1") > 2 && this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** Engine1 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine1_D3", shot.initiator);
                }
                if (s.startsWith("xxspare2") && this.chunkDamageVisible("Engine2") > 2 && this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** Engine2 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine2_D3", shot.initiator);
                }
                if (s.startsWith("xxspare3") && this.chunkDamageVisible("Engine3") > 2 && this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** Engine3 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine3_D3", shot.initiator);
                }
                if (s.startsWith("xxspare4") && this.chunkDamageVisible("Engine4") > 2 && this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** Engine4 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Engine4_D3", shot.initiator);
                }
                if (s.startsWith("xxsparli") && this.chunkDamageVisible("WingLIn") > 2 && this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && this.chunkDamageVisible("WingRIn") > 2 && this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlm") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparrm") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && this.chunkDamageVisible("WingLOut") > 2 && this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && this.chunkDamageVisible("WingROut") > 2 && this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    debugprintln(this, "*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if (s.startsWith("xxspark") && this.chunkDamageVisible("Keel1") > 1 && this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F) {
                    debugprintln(this, "*** Keel1 Spars Damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D" + this.chunkDamageVisible("Keel1"), shot.initiator);
                }
                if (s.startsWith("xxsparsl") && this.chunkDamageVisible("StabL") > 2 && this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F) {
                    debugprintln(this, "*** StabL: Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D" + this.chunkDamageVisible("StabL"), shot.initiator);
                }
                if (s.startsWith("xxsparsr") && this.chunkDamageVisible("StabR") > 2 && this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F) {
                    debugprintln(this, "*** StabR: Spars Damaged..");
                    this.nextDMGLevels(1, 2, "StabR_D" + this.chunkDamageVisible("StabR"), shot.initiator);
                }
//                if (s.startsWith("xxspart") && this.chunkDamageVisible("Tail1") > 2) if (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) <= 0.125F);
                return;
            }
            if (s.startsWith("xxtank")) {
                int l = s.charAt(6) - 49;
                l /= 2;
                if (this.getEnergyPastArmor(0.06F, shot) > 0.0F) {
                    if (this.FM.AS.astateTankStates[l] == 0) {
                        this.FM.AS.hitTank(shot.initiator, l, 1);
                        this.FM.AS.doSetTankState(shot.initiator, l, 1);
                    }
                    if (shot.powerType == 3) {
                        if (shot.power < 16100F) {
                            if (this.FM.AS.astateTankStates[l] < 4 && World.Rnd().nextFloat() < 0.21F) this.FM.AS.hitTank(shot.initiator, l, 1);
                        } else this.FM.AS.hitTank(shot.initiator, l, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100F)));
                    } else if (shot.power > 16100F) this.FM.AS.hitTank(shot.initiator, l, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100F)));
                }
                return;
            } else return;
        }
        if (s.startsWith("xcf")) {
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
            return;
        }
        if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", shot);
            return;
        }
        if (s.startsWith("xkeel")) {
            if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", shot);
            return;
        }
        if (s.startsWith("xrudder")) {
            if (this.chunkDamageVisible("Rudder1") < 2) this.hitChunk("Rudder1", shot);
            return;
        }
        if (s.startsWith("xstabl")) {
            if (this.chunkDamageVisible("StabL") < 2) this.hitChunk("StabL", shot);
            return;
        }
        if (s.startsWith("xstabr")) {
            if (this.chunkDamageVisible("StabR") < 2) this.hitChunk("StabR", shot);
            return;
        }
        if (s.startsWith("xvatorl")) {
            if (this.chunkDamageVisible("VatorL") < 2) this.hitChunk("VatorL", shot);
            return;
        }
        if (s.startsWith("xvatorr")) {
            if (this.chunkDamageVisible("VatorR") < 2) this.hitChunk("VatorR", shot);
            return;
        }
        if (s.startsWith("xwinglin")) {
            if (this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", shot);
            return;
        }
        if (s.startsWith("xwingrin")) {
            if (this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", shot);
            return;
        }
        if (s.startsWith("xwinglmid")) {
            if (this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", shot);
            return;
        }
        if (s.startsWith("xwingrmid")) {
            if (this.chunkDamageVisible("WingRMid") < 3) this.hitChunk("WingRMid", shot);
            return;
        }
        if (s.startsWith("xwinglout")) {
            if (this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", shot);
            return;
        }
        if (s.startsWith("xwingrout")) {
            if (this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", shot);
            return;
        }
        if (s.startsWith("xaronel")) {
            if (this.chunkDamageVisible("AroneL") < 2) this.hitChunk("AroneL", shot);
            return;
        }
        if (s.startsWith("xaroner")) {
            if (this.chunkDamageVisible("AroneR") < 2) this.hitChunk("AroneR", shot);
            return;
        }
        if (s.startsWith("xengine1")) {
            if (this.chunkDamageVisible("Engine1") < 2) this.hitChunk("Engine1", shot);
            return;
        }
        if (s.startsWith("xengine2")) {
            if (this.chunkDamageVisible("Engine2") < 2) this.hitChunk("Engine2", shot);
            return;
        }
        if (s.startsWith("xengine3")) {
            if (this.chunkDamageVisible("Engine3") < 2) this.hitChunk("Engine3", shot);
            return;
        }
        if (s.startsWith("xengine4")) {
            if (this.chunkDamageVisible("Engine4") < 2) this.hitChunk("Engine4", shot);
            return;
        }
        if (s.startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.05F) {
                debugprintln(this, "*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
            return;
        }
        if (s.startsWith("xturret")) return;
        if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int i1;
            if (s.endsWith("a")) {
                byte0 = 1;
                i1 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                i1 = s.charAt(6) - 49;
            } else i1 = s.charAt(5) - 49;
            this.hitFlesh(i1, shot, byte0);
            return;
        } else return;
    }

    public void msgExplosion(Explosion explosion) {
        this.setExplosion(explosion);
        if (explosion.chunkName != null && explosion.power > 0.0F) {
            if (explosion.chunkName.equals("Tail1_D3")) return;
            if (explosion.chunkName.equals("WingLIn_D3")) return;
            if (explosion.chunkName.equals("WingRIn_D3")) return;
            if (explosion.chunkName.equals("WingLMid_D3")) return;
            if (explosion.chunkName.equals("WingRMid_D3")) return;
            if (explosion.chunkName.equals("WingLOut_D3")) return;
            if (explosion.chunkName.equals("WingROut_D3")) return;
        }
        super.msgExplosion(explosion);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 91F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, 0.0F, cvt(f, 0.01F, 0.06F, 0.0F, 93F));
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, 0.0F, cvt(f, 0.01F, 0.06F, 0.0F, 93F));
        hiermesh.chunkSetAngles("GearC6_D0", 0.0F, 54F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC7_D0", 0.0F, 108F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC8_D0", 0.0F, 59F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -81F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, cvt(f, 0.01F, 0.12F, 0.0F, 67F), 0.0F);
        hiermesh.chunkSetAngles("GearL8_D0", 0.0F, cvt(f, 0.01F, 0.12F, 0.0F, -67F), 0.0F);
        hiermesh.chunkSetAngles("GearL9_D0", 0.0F, cvt(f, 0.01F, 0.12F, 0.0F, 84F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -81F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, cvt(f, 0.01F, 0.12F, 0.0F, -67F), 0.0F);
        hiermesh.chunkSetAngles("GearR8_D0", 0.0F, cvt(f, 0.01F, 0.12F, 0.0F, 67F), 0.0F);
        hiermesh.chunkSetAngles("GearR9_D0", 0.0F, cvt(f, 0.01F, 0.12F, 0.0F, -84F), 0.0F);
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        xyz[1] = cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.325F, 0.0F, 0.325F);
        this.hierMesh().chunkSetLocate("GearC3_D0", xyz, ypr);
        this.resetYPRmodifier();
        xyz[0] = cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.325F, 0.0F, -0.325F);
        this.hierMesh().chunkSetLocate("GearL3_D0", xyz, ypr);
        this.resetYPRmodifier();
        xyz[0] = cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.325F, 0.0F, -0.325F);
        this.hierMesh().chunkSetLocate("GearR3_D0", xyz, ypr);
    }

    protected void moveRudder(float f) {
        super.moveRudder(f);
        if (this.FM.CT.getGear() > 0.9F) this.hierMesh().chunkSetAngles("GearC33_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveFlap(float f) {
        this.resetYPRmodifier();
        xyz[0] = -0.4436F * f;
        xyz[2] = 0.063F * f;
        ypr[1] = 30F * f;
        this.hierMesh().chunkSetLocate("Flap01_D0", xyz, ypr);
        this.hierMesh().chunkSetLocate("Flap02_D0", xyz, ypr);
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
                this.FM.turret[0].bIsOperable = false;
                return false;

            case 26:
                this.FM.turret[1].bIsOperable = false;
                return false;

            case 27:
                this.FM.turret[2].bIsOperable = false;
                return false;

            case 28:
                this.FM.turret[3].bIsOperable = false;
                return false;

            case 29:
                this.FM.turret[4].bIsOperable = false;
                return false;

            case 30:
                this.FM.turret[5].bIsOperable = false;
                return false;

            case 19:
                this.killPilot(this, 5);
                this.killPilot(this, 6);
                this.killPilot(this, 7);
                this.killPilot(this, 8);
                break;
        }
        return super.cutFM(i, j, actor);
    }

    protected void moveBayDoor(float f) {
        this.hierMesh().chunkSetAngles("Bay01_D0", 0.0F, 0.0F, -90F * f);
        this.hierMesh().chunkSetAngles("Bay02_D0", 0.0F, 0.0F, -90F * f);
        this.hierMesh().chunkSetAngles("Bay03_D0", 0.0F, 0.0F, -90F * f);
        this.hierMesh().chunkSetAngles("Bay04_D0", 0.0F, 0.0F, -90F * f);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 0, 1);
            if (this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 1, 1);
            if (this.FM.AS.astateEngineStates[2] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 2, 1);
            if (this.FM.AS.astateEngineStates[3] > 3 && World.Rnd().nextFloat() < 0.0023F) this.FM.AS.hitTank(this, 3, 1);
            if (this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.04F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.04F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.04F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.04F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
            if (!(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) for (int i = 0; i < this.FM.EI.getNum(); i++)
                if (this.FM.AS.astateEngineStates[i] > 3 && World.Rnd().nextFloat() < 0.2F) this.FM.EI.engines[i].setExtinguisherFire();
        }
        for (int j = 1; j < 7; j++)
            if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask" + j + "_D0", false);
            else this.hierMesh().chunkVisible("HMask" + j + "_D0", this.hierMesh().isChunkVisible("Pilot" + j + "_D0"));

    }

    public void update(float f) {
        super.update(f);
        for (int i = 0; i < 4; i++) {
            float f1 = this.FM.EI.engines[i].getControlRadiator();
            if (Math.abs(this.flapps[i] - f1) <= 0.01F) continue;
            this.flapps[i] = f1;
            this.hierMesh().chunkSetAngles("Water" + (i + 1) + "_D0", 0.0F, -10F * f1, 0.0F);
            for (int j = 0; j < 8; j++)
                this.hierMesh().chunkSetAngles("Water" + (i * 8 + j + 5) + "_D0", 0.0F, 0.0F, -20F * f1);

        }

    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 3:
                this.FM.turret[0].setHealth(f);
                this.FM.turret[1].setHealth(f);
                break;

            case 4:
                this.FM.turret[2].setHealth(f);
                this.FM.turret[3].setHealth(f);
                break;

            case 5:
                this.FM.turret[4].setHealth(f);
                break;
        }
    }

    public void doMurderPilot(int i) {
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("HMask" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
        this.hierMesh().chunkVisible("Head" + (i + 1) + "_D0", false);
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f1 < -1F) {
                    f1 = -1F;
                    flag = false;
                }
                if (f1 > 80F) {
                    f1 = 80F;
                    flag = false;
                }
                break;

            case 1:
                if (f1 < -80F) {
                    f1 = -80F;
                    flag = false;
                }
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                    flag = false;
                }
                break;

            case 2:
                if (f1 < -1F) {
                    f1 = -1F;
                    flag = false;
                }
                if (f1 > 80F) {
                    f1 = 80F;
                    flag = false;
                }
                break;

            case 3:
                if (f1 < -80F) {
                    f1 = -80F;
                    flag = false;
                }
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                    flag = false;
                }
                break;

            case 4:
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

    public static boolean checkTurretVisibilityPlaneBlocking(int turretIndex) {
        return false;
    }

    private float flapps[] = { 0.0F, 0.0F, 0.0F, 0.0F };

    static {
        Class class1 = B_29X.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
    }
}
