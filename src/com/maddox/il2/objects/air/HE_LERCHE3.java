package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class HE_LERCHE3 extends Scheme2 implements TypeFighter, TypeX4Carrier {

    public HE_LERCHE3() {
        this.suka = new Loc();
        this.pictVBrake = 0.0F;
        this.pictAileron = 0.0F;
        this.pictVator = 0.0F;
        this.pictRudder = 0.0F;
        this.pictBlister = 0.0F;
        this.bToFire = false;
        this.tX4Prev = 0L;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 3:
            case 4:
                return false;

            case 13:
                this.killPilot(this, 0);
                break;

            case 19:
                this.hitProp(1, j, actor);
                this.FM.Gears.cgear = false;
                this.FM.Gears.lgear = false;
                this.FM.Gears.rgear = false;
                break;

            case 11:
            case 12:
                this.FM.Gears.cgear = false;
                break;

            case 17:
                this.FM.Gears.lgear = false;
                break;

            case 18:
                this.FM.Gears.rgear = false;
                break;

            case 7:
            case 9:
            case 10:
                return false;
        }
        return super.cutFM(i, j, actor);
    }

    protected void moveAileron(float f) {
    }

    protected void moveElevator(float f) {
    }

    protected void moveFlap(float f) {
    }

    protected void moveRudder(float f) {
    }

    public static void moveGear(HierMesh hiermesh, float f) {
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    public void moveSteering(float f) {
    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        xyz[2] = cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.63F, 0.0F, 0.63F);
        this.hierMesh().chunkSetLocate("GearL2_D0", xyz, ypr);
        xyz[2] = cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.63F, 0.0F, 0.63F);
        this.hierMesh().chunkSetLocate("GearR2_D0", xyz, ypr);
        xyz[2] = cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.63F, 0.0F, 0.63F);
        this.hierMesh().chunkSetLocate("GearC2_D0", xyz, ypr);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.endsWith("p1")) {
                    this.getEnergyPastArmor(65.989997863769531D / (Math.abs(v1.x) + 9.9999997473787516E-005D), shot);
                    if (shot.power <= 0.0F) this.doRicochetBack(shot);
                } else if (s.endsWith("p2")) this.getEnergyPastArmor(16.21F, shot);
                else if (s.endsWith("g1")) {
                    this.getEnergyPastArmor(34.21D / (Math.abs(v1.x) + 9.9999997473787516E-005D), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                }
                return;
            }
            if (s.startsWith("xxcannon")) {
                if (s.endsWith("1")) {
                    this.debuggunnery("Armament System: Left Cannon: Disabled..");
                    this.FM.AS.setJamBullets(0, 0);
                }
                if (s.endsWith("2")) {
                    this.debuggunnery("Armament System: Right Cannon: Disabled..");
                    this.FM.AS.setJamBullets(0, 1);
                }
                this.getEnergyPastArmor(World.Rnd().nextFloat(6.98F, 24.35F), shot);
                return;
            }
            if (s.startsWith("xxcontrols")) {
                if (this.getEnergyPastArmor(1.0F, shot) > 0.0F) {
                    if (World.Rnd().nextFloat() < 0.12F) {
                        this.FM.AS.setControlsDamage(shot.initiator, 1);
                        this.debuggunnery("Evelator Controls Out..");
                    }
                    if (World.Rnd().nextFloat() < 0.12F) {
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                        this.debuggunnery("Rudder Controls Out..");
                    }
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                int i = s.charAt(5) - 49;
                debugprintln(this, "*** Engine Module (" + i + "): Hit..");
                if (s.endsWith("prop")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F) if (World.Rnd().nextFloat() < 0.5F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 3);
                        debugprintln(this, "*** Engine Module: Prop Governor Hit, Disabled..");
                    } else {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 4);
                        debugprintln(this, "*** Engine Module: Prop Governor Hit, Damaged..");
                    }
                } else if (s.endsWith("pipe")) {
                    if (this.getEnergyPastArmor(4.6F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F) {
                        this.FM.AS.setInternalDamage(shot.initiator, 5);
                        debugprintln(this, "*** Engine Module: Drive Shaft Damaged..");
                    }
                } else if (s.endsWith("gear")) {
                    if (this.getEnergyPastArmor(4.6F, shot) > 0.0F) if (World.Rnd().nextFloat() < 0.5F) {
                        this.FM.EI.engines[i].setEngineStuck(shot.initiator);
                        debugprintln(this, "*** Engine Module: Bullet Jams Reductor Gear..");
                    } else {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 3);
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 4);
                        debugprintln(this, "*** Engine Module: Reductor Gear Damaged, Prop Governor Failed..");
                    }
                } else if (s.endsWith("supc")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 0);
                        debugprintln(this, "*** Engine Module: Supercharger Disabled..");
                    }
                } else if (s.endsWith("feed")) {
                    if (this.getEnergyPastArmor(3.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F && this.FM.EI.engines[i].getPowerOutput() > 0.7F) {
                        this.FM.AS.hitEngine(shot.initiator, i, 100);
                        debugprintln(this, "*** Engine Module: Pressurized Fuel Line Pierced, Fuel Flamed..");
                    }
                } else if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(2.1F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 175000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, i);
                            debugprintln(this, "*** Engine Module: Bullet Jams Crank Ball Bearing..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 50000F) {
                            this.FM.AS.hitEngine(shot.initiator, i, 2);
                            debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[i].getReadyness() + "..");
                        }
                        this.FM.EI.engines[i].setReadyness(shot.initiator, this.FM.EI.engines[i].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[i].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(22.5F, shot);
                } else if (s.startsWith("xxeng1cyl") || s.startsWith("xxeng2cyl")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[i].getCylindersRatio() * 1.75F) {
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 4800F)));
                        debugprintln(this, "*** Engine Module: Cylinders Hit, " + this.FM.EI.engines[i].getCylindersOperable() + "/" + this.FM.EI.engines[i].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < shot.power / 24000F) {
                            this.FM.AS.hitEngine(shot.initiator, i, 3);
                            debugprintln(this, "*** Engine Module: Cylinders Hit, Engine Fires..");
                        }
                        if (World.Rnd().nextFloat() < 0.01F) {
                            this.FM.AS.setEngineStuck(shot.initiator, i);
                            debugprintln(this, "*** Engine Module: Bullet Jams Piston Head..");
                        }
                        this.getEnergyPastArmor(22.5F, shot);
                    }
                } else if (s.startsWith("xxeng1mag") || s.startsWith("xxeng2mag")) {
                    int l = s.charAt(9) - 49;
                    this.FM.EI.engines[i].setMagnetoKnockOut(shot.initiator, l);
                    debugprintln(this, "*** Engine Module: Magneto " + l + " Destroyed..");
                }
                return;
            }
            if (s.startsWith("xxtank")) {
                int j = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) if (shot.power < 14100F) {
                    if (this.FM.AS.astateTankStates[j] == 0) {
                        this.FM.AS.hitTank(shot.initiator, j, 1);
                        this.FM.AS.doSetTankState(shot.initiator, j, 1);
                    }
                    if (World.Rnd().nextFloat() < 0.02F) this.FM.AS.hitTank(shot.initiator, j, 1);
                    if (shot.powerType == 3 && this.FM.AS.astateTankStates[j] > 2 && World.Rnd().nextFloat() < 0.4F) this.FM.AS.hitTank(shot.initiator, j, 10);
                } else this.FM.AS.hitTank(shot.initiator, j, World.Rnd().nextInt(0, (int) (shot.power / 56000F)));
                return;
            }
            if (s.startsWith("xxrad")) {
                if (this.getEnergyPastArmor(2.2F, shot) > 0.0F) {
                    int k = s.charAt(5) - 49;
                    if (k < 3) {
                        this.FM.AS.hitOil(shot.initiator, 0);
                        debugprintln(this, "*** Engine Module A: Oil Radiator Hit..");
                    } else {
                        this.FM.AS.hitOil(shot.initiator, 1);
                        debugprintln(this, "*** Engine Module B: Oil Radiator Hit..");
                    }
                }
                return;
            }
            if (s.startsWith("xxinst1")) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
                return;
            }
            if (s.startsWith("xxinst2")) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                return;
            } else return;
        }
        if (s.startsWith("xcf")) {
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
        } else if (s.startsWith("xnose")) {
            if (this.chunkDamageVisible("Nose") < 2) this.hitChunk("CF", shot);
            this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            if (point3d.y > 0.0D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            else this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
        } else if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", shot);
        } else if (s.startsWith("xkeel1")) {
            if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xrudder1")) {
            if (this.chunkDamageVisible("Rudder1") < 1) this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xstabl")) {
            if (this.chunkDamageVisible("StabL") < 2) this.hitChunk("StabL", shot);
        } else if (s.startsWith("xstabr")) {
            if (this.chunkDamageVisible("StabR") < 2) this.hitChunk("StabR", shot);
        } else if (s.startsWith("xvatorl")) {
            if (this.chunkDamageVisible("VatorL") < 1) this.hitChunk("VatorL", shot);
        } else if (s.startsWith("xvatorr")) {
            if (this.chunkDamageVisible("VatorR") < 1) this.hitChunk("VatorR", shot);
        } else if (s.startsWith("xwinglin")) {
            if (this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", shot);
        } else if (s.startsWith("xwingrin")) {
            if (this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", shot);
        } else if (s.startsWith("xwinglmid")) {
            if (this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", shot);
        } else if (s.startsWith("xwingrmid")) {
            if (this.chunkDamageVisible("WingRMid") < 3) this.hitChunk("WingRMid", shot);
        } else if (s.startsWith("xwinglout")) {
            if (this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", shot);
        } else if (s.startsWith("xwingrout")) {
            if (this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", shot);
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
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
        }
    }

    public void update(float f) {
        boolean flag = false;
        super.update(f);
        this.FM.setGCenter(cvt(this.FM.getSpeedKMH(), 0.0F, 240F, -10.5F, 0.0F));
        float f1 = this.FM.CT.getAirBrake();
        if (Math.abs(this.pictVBrake - f1) > 0.001F) {
            this.pictVBrake = f1;
            this.resetYPRmodifier();
            xyz[2] = cvt(this.pictVBrake, 0.0F, 1.0F, 0.0F, 0.525F);
            for (int i = 1; i < 10; i++)
                this.hierMesh().chunkSetLocate("Flap0" + i + "A_D0", xyz, ypr);

            flag = true;
        }
        f1 = this.FM.CT.getAileron();
        if (Math.abs(this.pictAileron - f1) > 0.01F) {
            this.pictAileron = f1;
            flag = true;
        }
        f1 = this.FM.CT.getRudder();
        if (Math.abs(this.pictRudder - f1) > 0.01F) {
            this.pictRudder = f1;
            flag = true;
        }
        f1 = this.FM.CT.getElevator();
        if (Math.abs(this.pictVator - f1) > 0.01F) {
            this.pictVator = f1;
            flag = true;
        }
        if (flag) {
            for (int j = 0; j < 9; j++) {
                float f2 = -60F * this.pictVBrake * (fcA[j] * this.pictAileron + fcE[j] * this.pictVator + fcR[j] * this.pictRudder);
                this.hierMesh().chunkSetAngles("Flap0" + (j + 1) + "B_D0", f2, 0.0F, 0.0F);
            }

            this.hierMesh().chunkSetAngles("AroneC_D0", 0.0F, 45F * this.pictAileron, 0.0F);
            this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 45F * this.pictAileron, 0.0F);
            this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 45F * this.pictAileron, 0.0F);
            this.hierMesh().chunkSetAngles("Rudder1_D0", 34F * this.pictRudder, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("VatorL_D0", -34F * this.pictVator, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 34F * this.pictVator, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("RFlap01_D0", 60F - 60F * this.pictVBrake, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("RFlap02_D0", -60F + 60F * this.pictVBrake, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("RFlap03_D0", -60F + 60F * this.pictVBrake, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("RFlap04_D0", 60F - 60F * this.pictVBrake, 0.0F, 0.0F);
        }
        if (this.FM.AS.astateBailoutStep > 1) {
            if (this.pictBlister < 1.0F) this.pictBlister += 3F * f;
            this.hierMesh().chunkSetAngles("Blister2_D0", -110F * this.pictBlister, 0.0F, 0.0F);
        }
        float f4 = this.FM.EI.getPowerOutput() * cvt(this.FM.getSpeedKMH(), 0.0F, 600F, 2.0F, 0.0F);
        if (this.FM.CT.getAirBrake() > 0.5F) {
            if (this.FM.Or.getTangage() > 5F) {
                this.FM.getW().scale(cvt(this.FM.Or.getTangage(), 45F, 90F, 1.0F, 0.1F));
                float f3 = this.FM.Or.getTangage();
                if (Math.abs(this.FM.Or.getKren()) > 90F) f3 = 90F + (90F - f3);
                float f5 = f3 - 90F;
                this.FM.CT.trimElevator = cvt(f5, -20F, 20F, 0.5F, -0.5F);
                f5 = this.FM.Or.getKren();
                if (Math.abs(f5) > 90F) if (f5 > 0.0F) f5 = 180F - f5;
                else f5 = -180F - f5;
                this.FM.CT.trimAileron = cvt(f5, -20F, 20F, 0.5F, -0.5F);
                this.FM.CT.trimRudder = cvt(f5, -15F, 15F, 0.04F, -0.04F);
            }
        } else {
            this.FM.CT.trimAileron = 0.0F;
            this.FM.CT.trimElevator = 0.0F;
            this.FM.CT.trimRudder = 0.0F;
        }
        this.FM.Or.increment(f4 * (this.FM.CT.getRudder() + this.FM.CT.getTrimRudderControl()), f4 * (this.FM.CT.getElevator() + this.FM.CT.getTrimElevatorControl()), f4 * (this.FM.CT.getAileron() + this.FM.CT.getTrimAileronControl()));
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM instanceof RealFlightModel && ((RealFlightModel) this.FM).isRealMode() || !flag || !(this.FM instanceof Pilot)) return;
        Pilot pilot = (Pilot) this.FM;
        if ((pilot.get_maneuver() == Maneuver.ATTACK_BOMBER || (pilot.get_maneuver() == Maneuver.ATTACK && pilot.isMissileValidForFighterAttack())) && pilot.target != null) {
            Point3d point3d = new Point3d(pilot.target.Loc);
            point3d.sub(this.FM.Loc);
            this.FM.Or.transformInv(point3d);
            if ((point3d.x > 4000D && point3d.x < 5500D || point3d.x > 100D && point3d.x < 5000D && World.Rnd().nextFloat() < 0.33F) && Time.current() > this.tX4Prev + 10000L) {
                this.bToFire = true;
                this.tX4Prev = Time.current();
            }
        }
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 1.0F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -1F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 1.0F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -1F;
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

    public Loc                 suka;
    private float              pictVBrake;
    private float              pictAileron;
    private float              pictVator;
    private float              pictRudder;
    private float              pictBlister;
    public boolean             bToFire;
    private long               tX4Prev;
    private static final float fcA[] = { 0.0F, 0.04F, 0.1F, 0.04F, 0.02F, -0.02F, -0.04F, -0.1F, -0.04F };
    private static final float fcE[] = { 0.98F, 0.48F, 0.1F, -0.48F, -0.7F, -0.7F, -0.48F, 0.1F, 0.48F };
    private static final float fcR[] = { 0.02F, 0.48F, 0.8F, 0.48F, 0.28F, -0.28F, -0.48F, -0.8F, -0.48F };
    private float              deltaAzimuth;
    private float              deltaTangage;

    static {
        Class class1 = HE_LERCHE3.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Lerche");
        Property.set(class1, "meshName", "3DO/Plane/He-LercheIIIb(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/He-LercheIIIB2.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHE_LIIIB.class });
        Property.set(class1, "LOSElevation", 1.00705F);
        weaponTriggersRegister(class1, new int[] { 1, 1, 2, 2, 2, 2, 2, 2 });
        weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_ExternalRock01", "_ExternalRock01", "_ExternalRock02", "_ExternalRock02", "_ExternalRock03", "_ExternalRock03" });
    }
}
