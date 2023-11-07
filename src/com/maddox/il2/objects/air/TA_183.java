package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class TA_183 extends Scheme1 implements TypeFighter, TypeBNZFighter, TypeX4Carrier, TypeFighterAceMaker {

    public boolean bToFire         = false;
    private long   tX4Prev         = 0L;
    private float  deltaAzimuth    = 0.0F;
    private float  deltaTangage    = 0.0F;
    public int     k14Mode         = 0;
    public int     k14WingspanType = 0;
    public float   k14Distance     = 200.0F;
// private float kangle = 0.0F;

    public void doMurderPilot(int var1) {
        switch (var1) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
            default:
        }
    }

    public static void moveGear(HierMesh var0, float var1) {
        var0.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(var1, 0.2F, 0.9F, 0.0F, -90.0F), 0.0F);
        var0.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(var1, 0.1F, 0.2F, 0.0F, -90.0F), 0.0F);
        var0.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(var1, 0.1F, 0.2F, 0.0F, -90.0F), 0.0F);
        var0.chunkSetAngles("GearC6_D0", 0.0F, 0.0F, 0.0F);
        var0.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(var1, 0.2F, 0.6F, 0.0F, -60.0F), 0.0F);
        var0.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(var1, 0.2F, 0.6F, 0.0F, -65.0F), 0.0F);
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.xyz[1] = Aircraft.cvt(var1, 0.2F, 0.6F, 0.0F, 0.65F);
        var0.chunkSetLocate("GearL5_D0", Aircraft.xyz, Aircraft.ypr);
        var0.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(var1, 0.1F, 0.2F, 0.0F, -40.0F) + Aircraft.cvt(var1, 0.8F, 0.9F, 0.0F, 20.0F), 0.0F);
        var0.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(var1, 0.2F, 0.6F, 0.0F, -60.0F), 0.0F);
        var0.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(var1, 0.2F, 0.6F, 0.0F, -65.0F), 0.0F);
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.xyz[1] = Aircraft.cvt(var1, 0.2F, 0.6F, 0.0F, 0.65F);
        var0.chunkSetLocate("GearR5_D0", Aircraft.xyz, Aircraft.ypr);
        var0.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(var1, 0.1F, 0.2F, 0.0F, -40.0F) + Aircraft.cvt(var1, 0.8F, 0.9F, 0.0F, 20.0F), 0.0F);
    }

    protected void moveGear(float var1) {
        moveGear(this.hierMesh(), var1);
    }

    public void moveSteering(float var1) {
        if (this.FM.CT.getGear() > 0.8F) this.hierMesh().chunkSetAngles("GearC6_D0", 0.0F, var1, 0.0F);

    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.28F, 0.0F, 0.295F);
        this.hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetAngles("GearL7_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.28F, 0.0F, -48.0F), 0.0F);
        this.hierMesh().chunkSetAngles("GearL8_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.28F, 0.0F, -96.0F), 0.0F);
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.28F, 0.0F, 0.295F);
        this.hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetAngles("GearR7_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.28F, 0.0F, -48.0F), 0.0F);
        this.hierMesh().chunkSetAngles("GearR8_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.28F, 0.0F, -96.0F), 0.0F);
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.1795F, 0.0F, 0.1795F);
        this.hierMesh().chunkSetLocate("GearC3_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveFlap(float var1) {
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, -45.0F * var1, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -45.0F * var1, 0.0F);
    }

    protected void hitBone(String var1, Shot var2, Point3d var3) {
        if (var1.startsWith("xx")) {
            if (var1.startsWith("xxarmor")) {
                this.debuggunnery("Armor: Hit..");
                if (var1.endsWith("p1")) this.getEnergyPastArmor(World.Rnd().nextFloat(12.0F, 19.0F) / (Math.abs(Aircraft.v1.x) + 9.999999747378752E-5D), var2);
                else if (var1.endsWith("p2")) this.getEnergyPastArmor(World.Rnd().nextFloat(12.7F, 12.7F) / (Math.abs(Aircraft.v1.x) + 9.999999747378752E-5D), var2);
                else if (var1.endsWith("g1")) {
                    this.getEnergyPastArmor(World.Rnd().nextFloat(20.0F, 60.0F) / (Math.abs(Aircraft.v1.x) + 9.999999747378752E-5D), var2);
                    this.FM.AS.setCockpitState(var2.initiator, this.FM.AS.astateCockpitState | 2);
                    if (var2.power <= 0.0F) this.doRicochetBack(var2);
                }
            } else {
                int var4;
                if (var1.startsWith("xxcontrols")) {
                    this.debuggunnery("Controls: Hit..");
                    var4 = var1.charAt(10) - 48;
                    switch (var4) {
                        case 1:
                        case 2:
                            if (this.getEnergyPastArmor(0.99F, var2) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                                this.debuggunnery("Controls: Ailerones Controls: Out..");
                                this.FM.AS.setControlsDamage(var2.initiator, 0);
                            }
                            break;
                        case 3:
                        case 4:
                            if (this.getEnergyPastArmor(1.22F, var2) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                                this.debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                                this.FM.AS.setControlsDamage(var2.initiator, 2);
                            }

                            if (this.getEnergyPastArmor(1.22F, var2) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                                this.debuggunnery("Controls: Elevator Controls: Disabled..");
                                this.FM.AS.setControlsDamage(var2.initiator, 1);
                            }
                    }
                } else if (var1.startsWith("xxeng1")) {
                    this.debuggunnery("Engine Module: Hit..");
                    if (var1.endsWith("bloc")) this.getEnergyPastArmor(World.Rnd().nextFloat(0.0F, 60.0F) / (Math.abs(Aircraft.v1.x) + 9.999999747378752E-5D), var2);

                    if (var1.endsWith("cams") && this.getEnergyPastArmor(0.45F, var2) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[0].getCylindersRatio() * 20.0F) {
                        this.FM.EI.engines[0].setCyliderKnockOut(var2.initiator, World.Rnd().nextInt(1, (int) (var2.power / 4800.0F)));
                        this.debuggunnery("Engine Module: Engine Cams Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < var2.power / 24000.0F) {
                            this.FM.AS.hitEngine(var2.initiator, 0, 2);
                            this.debuggunnery("Engine Module: Engine Cams Hit - Engine Fires..");
                        }

                        if (var2.powerType == 3 && World.Rnd().nextFloat() < 0.75F) {
                            this.FM.AS.hitEngine(var2.initiator, 0, 1);
                            this.debuggunnery("Engine Module: Engine Cams Hit (2) - Engine Fires..");
                        }
                    }

                    if (var1.endsWith("eqpt") && World.Rnd().nextFloat() < var2.power / 24000.0F) {
                        this.FM.AS.hitEngine(var2.initiator, 0, 3);
                        this.debuggunnery("Engine Module: Hit - Engine Fires..");
                    }

//                    if (!var1.endsWith("exht")) {
//
//                    }
                } else if (var1.startsWith("xxhyd")) this.FM.AS.setInternalDamage(var2.initiator, 3);
                else if (var1.startsWith("xxmgun0")) {
                    var4 = var1.charAt(7) - 49;
                    if (this.getEnergyPastArmor(0.5F, var2) > 0.0F) {
                        this.debuggunnery("Armament: Machine Gun (" + var4 + ") Disabled..");
                        this.FM.AS.setJamBullets(0, var4);
                        this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), var2);
                    }
                } else if (var1.startsWith("xxpnm")) this.FM.AS.setInternalDamage(var2.initiator, 1);
                else if (var1.startsWith("xxspar")) {
                    Aircraft.debugprintln(this, "*** Spar Construction: Hit..");
                    if (var1.startsWith("xxsparli") && World.Rnd().nextFloat(0.0F, 0.115F) < var2.mass && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** WingLIn Spar Damaged..");
                        this.nextDMGLevels(1, 2, "WingLIn_D" + this.chunkDamageVisible("WingLIn"), var2.initiator);
                    }

                    if (var1.startsWith("xxsparri") && World.Rnd().nextFloat(0.0F, 0.115F) < var2.mass && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** WingRIn Spar Damaged..");
                        this.nextDMGLevels(1, 2, "WingRIn_D" + this.chunkDamageVisible("WingRIn"), var2.initiator);
                    }

                    if (var1.startsWith("xxsparlm") && World.Rnd().nextFloat(0.0F, 0.115F) < var2.mass && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** WingLMid Spar Damaged..");
                        this.nextDMGLevels(1, 2, "WingLMid_D" + this.chunkDamageVisible("WingLMid"), var2.initiator);
                    }

                    if (var1.startsWith("xxsparrm") && World.Rnd().nextFloat(0.0F, 0.115F) < var2.mass && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** WingRMid Spar Damaged..");
                        this.nextDMGLevels(1, 2, "WingRMid_D" + this.chunkDamageVisible("WingRMid"), var2.initiator);
                    }

                    if (var1.startsWith("xxsparlo") && World.Rnd().nextFloat(0.0F, 0.115F) < var2.mass && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** WingLOut Spar Damaged..");
                        this.nextDMGLevels(1, 2, "WingLOut_D" + this.chunkDamageVisible("WingLOut"), var2.initiator);
                    }

                    if (var1.startsWith("xxsparro") && World.Rnd().nextFloat(0.0F, 0.115F) < var2.mass && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** WingROut Spar Damaged..");
                        this.nextDMGLevels(1, 2, "WingROut_D" + this.chunkDamageVisible("WingROut"), var2.initiator);
                    }

                    if (var1.startsWith("xxspark") && World.Rnd().nextFloat() < 0.5F && this.getEnergyPastArmor(6.8F * World.Rnd().nextFloat(1.0F, 1.5F) / (Math.abs(Aircraft.v1.x) + 9.999999747378752E-5D), var2) > 0.0F) {
                        Aircraft.debugprintln(this, "*** Keel Spars Damaged..");
                        this.nextDMGLevels(1, 2, "Keel1_D" + this.chunkDamageVisible("Keel1"), var2.initiator);
                    }

                    if (var1.startsWith("xxspart") && this.chunkDamageVisible("Tail1") > 2 && this.getEnergyPastArmor(3.86F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), var2) > 0.0F
                            && World.Rnd().nextFloat() < 0.25F) {
                        this.debuggunnery("Spar Construction: Tail1 Ribs Hit, Breaking in Half..");
                        this.nextDMGLevels(1, 2, "Tail1_D3", var2.initiator);
                    }
                } else if (var1.startsWith("xxtank")) {
                    var4 = var1.charAt(6) - 49;
                    if (this.getEnergyPastArmor(0.1F, var2) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                        if (this.FM.AS.astateTankStates[var4] == 0) {
                            this.debuggunnery("Fuel Tank (" + var4 + "): Pierced..");
                            this.FM.AS.hitTank(var2.initiator, var4, 1);
                            this.FM.AS.doSetTankState(var2.initiator, var4, 1);
                        }

                        if (var2.powerType == 3 && World.Rnd().nextFloat() < 0.1F) {
                            this.FM.AS.hitTank(var2.initiator, var4, 2);
                            this.debuggunnery("Fuel Tank (" + var4 + "): Hit..");
                        }
                    }
                }
            }
        } else if (!var1.startsWith("xcf") && !var1.startsWith("xcockpit")) {
            if (var1.startsWith("xtail")) {
                if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", var2);
            } else if (var1.startsWith("xnose")) {
                if (this.chunkDamageVisible("Nose") < 2) this.hitChunk("Nose", var2);
            } else if (var1.startsWith("xkeel")) {
                if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", var2);
            } else if (var1.startsWith("xrudder")) {
                if (this.chunkDamageVisible("Rudder1") < 1) this.hitChunk("Rudder1", var2);
            } else if (var1.startsWith("xstab")) {
                if (var1.startsWith("xstabl")) this.hitChunk("StabL", var2);

                if (var1.startsWith("xstabr")) this.hitChunk("StabR", var2);
            } else if (var1.startsWith("xvator")) {
                if (var1.startsWith("xvatorl") && this.chunkDamageVisible("VatorL") < 1) this.hitChunk("VatorL", var2);

                if (var1.startsWith("xvatorr") && this.chunkDamageVisible("VatorR") < 1) this.hitChunk("VatorR", var2);
            } else if (var1.startsWith("xwing")) {
                if (var1.startsWith("xwinglin") && this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", var2);

                if (var1.startsWith("xwingrin") && this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", var2);

                if (var1.startsWith("xwinglmid") && this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", var2);

                if (var1.startsWith("xwingrmid") && this.chunkDamageVisible("WingRMid") < 3) this.hitChunk("WingRMid", var2);

                if (var1.startsWith("xwinglout") && this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", var2);

                if (var1.startsWith("xwingrout") && this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", var2);
            } else if (var1.startsWith("xarone")) {
                if (var1.startsWith("xaronel") && this.chunkDamageVisible("AroneL") < 1) this.hitChunk("AroneL", var2);

                if (var1.startsWith("xaroner") && this.chunkDamageVisible("AroneR") < 1) this.hitChunk("AroneR", var2);
            } else if (var1.startsWith("xgear")) {
                if (var1.endsWith("1") && World.Rnd().nextFloat() < 0.05F) {
                    this.debuggunnery("Hydro System: Disabled..");
                    this.FM.AS.setInternalDamage(var2.initiator, 0);
                }

                if (var1.endsWith("2") && World.Rnd().nextFloat() < 0.1F && this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), var2) > 0.0F) {
                    this.debuggunnery("Undercarriage: Stuck..");
                    this.FM.AS.setInternalDamage(var2.initiator, 3);
                }
            } else if (var1.startsWith("xpilot") || var1.startsWith("xhead")) {
                byte var6 = 0;
                int var5;
                if (var1.endsWith("a")) {
                    var6 = 1;
                    var5 = var1.charAt(6) - 49;
                } else if (var1.endsWith("b")) {
                    var6 = 2;
                    var5 = var1.charAt(6) - 49;
                } else var5 = var1.charAt(5) - 49;

                this.hitFlesh(var5, var2, var6);
            }
        } else {
            this.hitChunk("CF", var2);
            if (var1.startsWith("xcockpit")) {
                if (var3.x > 2.0D) {
                    if (World.Rnd().nextFloat() < 0.2F) this.FM.AS.setCockpitState(var2.initiator, this.FM.AS.astateCockpitState | 4);
                } else this.FM.AS.setCockpitState(var2.initiator, this.FM.AS.astateCockpitState | 1);
            } else if (var3.x > 2.5D) {
                if (World.Rnd().nextFloat() < 0.2F) this.FM.AS.setCockpitState(var2.initiator, this.FM.AS.astateCockpitState | 64);
            } else if (var3.y > 0.0D) this.FM.AS.setCockpitState(var2.initiator, this.FM.AS.astateCockpitState | 8);
            else this.FM.AS.setCockpitState(var2.initiator, this.FM.AS.astateCockpitState | 32);
        }

    }

    public void update(float f) {
        if (Config.isUSE_RENDER() && this.FM.AS.isMaster()) if (this.FM.EI.engines[0].getPowerOutput() > 0.8F && this.FM.EI.engines[0].getStage() == 6) {
            if (this.FM.EI.engines[0].getPowerOutput() > 0.95F) this.FM.AS.setSootState(this, 0, 3);
            else this.FM.AS.setSootState(this, 0, 2);
        } else this.FM.AS.setSootState(this, 0, 0);
        super.update(f);
    }

    protected boolean cutFM(int var1, int var2, Actor var3) {
        switch (var1) {
            case 11:
                this.cut("StabL");
                this.cut("StabR");
                this.FM.cut(17, var2, var3);
                this.FM.cut(18, var2, var3);
            case 13:
                return false;
            case 19:
                this.FM.EI.engines[0].setEngineDies(var3);
                return super.cutFM(var1, var2, var3);
            default:
                return super.cutFM(var1, var2, var3);
        }
    }

    public void rareAction(float var1, boolean var2) {
        super.rareAction(var1, var2);
        if (this.FM.getAltitude() < 3000.0F) this.hierMesh().chunkVisible("HMask1_D0", false);
        else this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));

        if ((!(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && var2 && this.FM instanceof Pilot) {
            Pilot pilot = (Pilot) this.FM;
            if ((pilot.get_maneuver() == Maneuver.ATTACK_BOMBER || (pilot.get_maneuver() == Maneuver.ATTACK && pilot.isMissileValidForFighterAttack())) && pilot.target != null) {
                Point3d var4 = new Point3d(pilot.target.Loc);
                var4.sub(this.FM.Loc);
                this.FM.Or.transformInv(var4);
                if ((var4.x > 4000.0D && var4.x < 5500.0D || var4.x > 100.0D && var4.x < 5000.0D && World.Rnd().nextFloat() < 0.33F) && Time.current() > this.tX4Prev + 10000L) {
                    this.bToFire = true;
                    this.tX4Prev = Time.current();
                }
            }
        }

    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 1.0F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -1.0F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 1.0F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -1.0F;
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

    public boolean typeFighterAceMakerToggleAutomation() {
        ++this.k14Mode;
        if (this.k14Mode > 2) this.k14Mode = 0;

        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerMode" + this.k14Mode);
        return true;
    }

    public void typeFighterAceMakerAdjDistanceReset() {
    }

    // -------------------------------------------------------------------------------------------------------
    // TODO: skylla: gyro-gunsight distance HUD log (for details please see
    // P_51D25NA.class):

    public void typeFighterAceMakerAdjDistancePlus() {
        this.adjustK14AceMakerDistance(+10.0f);
    }

    public void typeFighterAceMakerAdjDistanceMinus() {
        this.adjustK14AceMakerDistance(-10.0f);
    }

    private void adjustK14AceMakerDistance(float f) {
        this.k14Distance += f;
        if (this.k14Distance > 1000.0f) this.k14Distance = 1000.0f;
        else if (this.k14Distance < 160.0f) this.k14Distance = 160.0f;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "Sight Distance: " + (int) this.k14Distance + "m");
    }
    /*
     * public void typeFighterAceMakerAdjDistancePlus() { this.k14Distance += 10.0F; if(this.k14Distance > 800.0F) { this.k14Distance = 800.0F; }
     *
     * HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerInc"); }
     *
     * public void typeFighterAceMakerAdjDistanceMinus() { this.k14Distance -= 10.0F; if(this.k14Distance < 200.0F) { this.k14Distance = 200.0F; }
     *
     * HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerDec"); }
     */

    // Allied plane wingspans (approximately):
    public void typeFighterAceMakerAdjSideslipPlus() {
        try {
            this.adjustAceMakerSideSlip(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        try {
            this.adjustAceMakerSideSlip(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void adjustAceMakerSideSlip(int i) throws IOException {
        if (!(i == 1 || i == -1)) throw new IOException("Wrong input value! Only +1 and -1 allowed!");
        this.k14WingspanType += i;
        String s = "Wingspan Selected: ";
        String s1 = "Yak-3/Yak-9/La-5/P-39/MiG-3";
        String s2 = "B-24";

        switch (this.k14WingspanType) {
            // case 0: s += s1; break; //like Bf-109
            case 1:
                s += "P-51/P-47/P-80/Spitfire/Typhoon/Hurricane";
                break; // like Fw-190
            case 2:
                s += "P-38";
                break; // like Ju-87
            case 3:
                s += "Mosquito/IL-2/Beaufighter";
                break; // like Me-210
            case 4:
                s += "A-20/Pe-2";
                break; // like Do-217
            case 5:
                s += "20m";
                break; // like Ju-88
            case 6:
                s += "B-25/A-26";
                break; // like Ju-188
            case 7:
                s += "DC-3";
                break; // like Ju-52
            case 8:
                s += "B-17/Halifax/Lancaster";
                break; // like He-177
            case 9:
                s += s2;
                break; // like Fw-200
            case 10:
                this.adjustAceMakerSideSlip(-1);
                s += s2;
                break;
            case -1:
                this.adjustAceMakerSideSlip(1);
                s += s1;
                break;
            default:
                this.k14WingspanType = 0;
                s += s1;
                break;
        }

        HUD.log(AircraftHotKeys.hudLogWeaponId, s);
    }

    /*
     * old code:
     *
     * public void typeFighterAceMakerAdjSideslipPlus() { --this.k14WingspanType; if(this.k14WingspanType < 0) { this.k14WingspanType = 0; }
     *
     * HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + this.k14WingspanType); }
     *
     * public void typeFighterAceMakerAdjSideslipMinus() { ++this.k14WingspanType; if(this.k14WingspanType > 9) { this.k14WingspanType = 9; }
     *
     * HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + this.k14WingspanType); }
     */
    // -------------------------------------------------------------------------------------------------------

    public void typeFighterAceMakerAdjSideslipReset() {
    }

    public void typeFighterAceMakerReplicateToNet(NetMsgGuaranted var1) throws IOException {
        var1.writeByte(this.k14Mode);
        var1.writeByte(this.k14WingspanType);
        var1.writeFloat(this.k14Distance);
    }

    public void typeFighterAceMakerReplicateFromNet(NetMsgInput var1) throws IOException {
        this.k14Mode = var1.readByte();
        this.k14WingspanType = var1.readByte();
        this.k14Distance = var1.readFloat();
    }

    static {
        Class class1 = TA_183.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ta-183");
        Property.set(class1, "meshName", "3DO/Plane/Ta-183(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
        Property.set(class1, "yearService", 1946.0F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/Ta-183.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitTA_183.class });
        Property.set(class1, "LOSElevation", 1.2158F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
                0, 0, 0, 0, 9, 9, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 3, 3,
                3, 3, 9, 9});
        Aircraft.weaponHooksRegister(class1,
                new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalDev01", "_ExternalDev02", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04",
                        "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14",
                        "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24",
                        "_ExternalRock25", "_ExternalRock25", "_ExternalRock26", "_ExternalRock26", "_ExternalRock27", "_ExternalRock27", "_ExternalRock28", "_ExternalRock28", "_ExternalBomb01", "_ExternalBomb01",
                        "_ExternalBomb02", "_ExternalBomb02", "_ExternalDev03", "_ExternalDev04"});
    }
}
