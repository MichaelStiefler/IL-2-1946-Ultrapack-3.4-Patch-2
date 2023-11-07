package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;
import com.maddox.sas1946.il2.util.AircraftTools;

public abstract class Fulmar extends Scheme1 implements TypeStormovik, TypeFighter {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("WingRackL_D0", thisWeaponsName.startsWith("2x"));
        hierMesh.chunkVisible("WingRackR_D0", thisWeaponsName.startsWith("2x"));
        hierMesh.chunkVisible("catapult_hook_D0", !thisWeaponsName.startsWith("1x"));
    }
    
    public Fulmar() {
        this.bPitUnfocused = true;
        this.airBrakePos = 0.0F;
        this.suspension = 0.0F;
        this.arrestor = 0.0F;
        this.obsLookoutTimeLeft = 2.0F;
        this.obsLookoutAz = 0.0F;
        this.obsLookoutEl = 0.0F;
        this.obsLookoutPos = new float[3][129];
        this.obsLookTime = 0;
        this.obsLookAzimuth = 0.0F;
        this.obsLookElevation = 0.0F;
        this.obsAzimuth = 0.0F;
        this.obsElevation = 0.0F;
        this.obsAzimuthOld = 0.0F;
        this.obsElevationOld = 0.0F;
        this.obsMove = 0.0F;
        this.obsMoveTot = 0.0F;
        this.bObserverKilled = false;
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
                this.hierMesh().chunkVisible("Head2_D0", false);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.bObserverKilled = true;
                break;
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (!this.bObserverKilled) if (this.obsLookTime == 0) {
            this.obsLookTime = 2 + World.Rnd().nextInt(1, 3);
            this.obsMoveTot = 1.0F + World.Rnd().nextFloat() * 1.5F;
            this.obsMove = 0.0F;
            this.obsAzimuthOld = this.obsAzimuth;
            this.obsElevationOld = this.obsElevation;
            if (World.Rnd().nextFloat() > 0.8D) {
                this.obsAzimuth = 0.0F;
                this.obsElevation = 0.0F;
            } else {
                this.obsAzimuth = World.Rnd().nextFloat() * 140F - 70F;
                this.obsElevation = World.Rnd().nextFloat() * 50F - 20F;
            }
        } else this.obsLookTime--;
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
            this.hierMesh().chunkVisible("HMask2_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
            this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_D0"));
        }
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.39F) this.FM.AS.hitTank(this, 0, 1);
            if (this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
        }
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, -84.5F), 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -95F), 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, -140.5F), 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, -180F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, -84.5F), 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -95F), 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, -140.5F), 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.1F, 1.0F, 0.0F, -180F), 0.0F);
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC2_D0", f, 0.0F, 0.0F);
    }

    public void moveWheelSink() {
        if (this.FM.Gears.onGround()) this.suspension = this.suspension + 0.008F;
        else this.suspension = this.suspension - 0.008F;
        if (this.suspension < 0.0F) this.suspension = 0.0F;
        if (this.suspension > 0.1F) this.suspension = 0.1F;
        Aircraft.xyz[0] = 0.0F;
        Aircraft.ypr[0] = 0.0F;
        Aircraft.ypr[1] = 0.0F;
        Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[1] = this.suspension / 10F;
        float f = Aircraft.cvt(this.FM.getSpeed(), 0.0F, 25F, 0.0F, 1.0F);
        float f1 = this.FM.Gears.gWheelSinking[0] * f + this.suspension;
        Aircraft.xyz[0] = Aircraft.cvt(f1, 0.0F, 0.24F, 0.0F, 0.24F);
        this.hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        f1 = this.FM.Gears.gWheelSinking[1] * f + this.suspension;
        Aircraft.xyz[1] = Aircraft.cvt(f1, 0.0F, 0.24F, 0.0F, 0.24F);
        this.hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveFlap(float f) {
        float f1 = -35F * f;
        this.hierMesh().chunkSetAngles("FlapL1_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("FlapR1_D0", 0.0F, f1, 0.0F);
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.67F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (f > 0.02D) {
            this.hierMesh().chunkVisible("Blister2move_D0", true);
            this.hierMesh().chunkVisible("Blister2_D0", false);
        } else {
            this.hierMesh().chunkVisible("Blister2move_D0", false);
            this.hierMesh().chunkVisible("Blister2_D0", true);
        }
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.68F);
        this.hierMesh().chunkSetLocate("Blister2_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(f);
            this.setDoorSnd(f);
        }
    }

    public void update(float f) {
        if (this.FM.CT.getArrestor() > 0.2F) if (this.FM.Gears.arrestorVAngle != 0.0F) {
            float f2 = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -26F, 11F, 1.0F, 0.0F);
            this.arrestor = 0.8F * this.arrestor + 0.2F * f2;
            this.moveArrestorHook(this.arrestor);
        } else {
            float f3 = -42F * this.FM.Gears.arrestorVSink / 37F;
            if (f3 < 0.0F && this.FM.getSpeedKMH() > 50F) Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
            if (f3 > 0.0F && this.FM.CT.getArrestor() < 0.95F) f3 = 0.0F;
            if (f3 > 0.0F) this.arrestor = 0.7F * this.arrestor + 0.3F * (this.arrestor + f3);
            else this.arrestor = 0.3F * this.arrestor + 0.7F * (this.arrestor + f3);
            if (this.arrestor < 0.0F) this.arrestor = 0.0F;
            else if (this.arrestor > 1.0F) this.arrestor = 1.0F;
            this.moveArrestorHook(this.arrestor);
        }
        if (this.obsMove < this.obsMoveTot && !this.bObserverKilled && !this.FM.AS.isPilotParatrooper(1)) {
            if (this.obsMove < 0.2F || this.obsMove > this.obsMoveTot - 0.2F) this.obsMove += 0.3D * f;
            else if (this.obsMove < 0.1F || this.obsMove > this.obsMoveTot - 0.1F) this.obsMove += 0.15F;
            else this.obsMove += 1.2D * f;
            this.obsLookAzimuth = Aircraft.cvt(this.obsMove, 0.0F, this.obsMoveTot, this.obsAzimuthOld, this.obsAzimuth);
            this.obsLookElevation = Aircraft.cvt(this.obsMove, 0.0F, this.obsMoveTot, this.obsElevationOld, this.obsElevation);
            this.hierMesh().chunkSetAngles("Head2_D0", 0.0F, this.obsLookAzimuth, this.obsLookElevation);
        }
        this.hierMesh().chunkSetAngles("Radiator_D0", 0.0F, -25F * this.FM.EI.engines[0].getControlRadiator(), 0.0F);
        super.update(f);
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -f * 30F, 0.0F);
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -f * 30F, 0.0F);
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -25F * f, 0.0F);
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

    public void moveArrestorHook(float f) {
        this.hierMesh().chunkSetAngles("Hook1_D0", 0.0F, -52F * f, 0.0F);
        this.arrestor = f;
    }

    protected void moveWingFold(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("Flap01_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.25F, 0.0F, -170F), 0.0F);
        hiermesh.chunkSetAngles("Flap02_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.25F, 0.0F, -170F), 0.0F);
        hiermesh.chunkSetAngles("WingLMid_D0", 0.0F, Aircraft.cvt(f, 0.28F, 1.0F, 0.0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("WingRMid_D0", 0.0F, Aircraft.cvt(f, 0.28F, 1.0F, 0.0F, -80F), 0.0F);
    }

    public void moveWingFold(float f) {
//        if (f < 0.001F) {
//            this.setGunPodsOn(true);
//            this.hideWingWeapons(false);
//        } else {
//            this.setGunPodsOn(false);
//            this.FM.CT.WeaponControl[0] = false;
//            this.hideWingWeapons(true);
//        }
        this.moveWingFold(this.hierMesh(), f);
        AircraftTools.updateExternalWeaponHooks(this);
    }

    protected void setControlDamage(Shot shot, int i) {
        if (World.Rnd().nextFloat() < 0.002F && this.getEnergyPastArmor(4F, shot) > 0.0F) {
            this.FM.AS.setControlsDamage(shot.initiator, i);
            this.mydebuggunnery(i + " Controls Out... //0 = AILERON, 1 = ELEVATOR, 2 = RUDDER");
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                this.mydebuggunnery("Armor: Hit..");
                if (s.startsWith("xxarmorp")) {
                    int i = s.charAt(8) - 48;
                    switch (i) {
                        case 1:
                            this.getEnergyPastArmor(22.76D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot);
                            if (shot.power <= 0.0F) this.doRicochetBack(shot);
                            break;

                        case 3:
                            this.getEnergyPastArmor(9.366F, shot);
                            break;

                        case 5:
                            this.getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot);
                            break;
                    }
                }
            } else if (s.startsWith("xxspar")) {
                this.mydebuggunnery("Spar Construction: Hit..");
                if (s.startsWith("xxsparli") && this.chunkDamageVisible("WingLIn") > 2 && this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingLIn Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && this.chunkDamageVisible("WingRIn") > 2 && this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingRIn Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlm") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(5.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingLMid Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparrm") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(5.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingRMid Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(4.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingLOut Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(4.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingROut Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxspart") && this.chunkDamageVisible("Tail1") > 2 && this.getEnergyPastArmor(3.86F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                    this.mydebuggunnery("Spar Construction: Tail1 Ribs Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
            } else if (s.startsWith("xxeng")) {
                if ((s.endsWith("prop") || s.endsWith("pipe")) && this.getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) this.FM.EI.engines[0].setKillPropAngleDevice(shot.initiator);
                if (s.endsWith("case") || s.endsWith("gear")) {
                    if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 140000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, 0);
                            this.mydebuggunnery("*** Engine Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 85000F) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            this.mydebuggunnery("*** Engine Crank Case Hit - Engine Damaged..");
                        }
                    } else if (World.Rnd().nextFloat() < 0.01F) this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, 1);
                    else {
                        this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - 0.002F);
                        this.mydebuggunnery("*** Engine Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(12F, shot);
                }
                if (s.endsWith("cyls1") || s.endsWith("cyls2")) {
                    if (this.getEnergyPastArmor(6.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[0].getCylindersRatio() * 0.75F) {
                        this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                        this.mydebuggunnery("*** Engine Cylinders Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < shot.power / 48000F) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            this.mydebuggunnery("*** Engine Cylinders Hit - Engine Fires..");
                        }
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("supc")) {
                    if (this.getEnergyPastArmor(0.05F, shot) > 0.0F) this.FM.EI.engines[0].setKillCompressor(shot.initiator);
                    this.getEnergyPastArmor(2.0F, shot);
                }
                if (s.endsWith("oil1")) {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    this.mydebuggunnery("*** Engine Module: Oil Radiator Hit..");
                }
                this.mydebuggunnery("*** Engine state = " + this.FM.AS.astateEngineStates[0]);
            } else if (s.startsWith("xxtank")) {
                int j = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(0.19F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                    if (this.FM.AS.astateTankStates[j] == 0) {
                        this.mydebuggunnery("Fuel System: Fuel Tank Pierced..");
                        this.FM.AS.hitTank(shot.initiator, j, 1);
                        this.FM.AS.doSetTankState(shot.initiator, j, 1);
                    } else if (this.FM.AS.astateTankStates[j] == 1) {
                        this.mydebuggunnery("Fuel System: Fuel Tank Pierced (2)..");
                        this.FM.AS.hitTank(shot.initiator, j, 1);
                        this.FM.AS.doSetTankState(shot.initiator, j, 2);
                    }
                    if (shot.powerType == 3 && World.Rnd().nextFloat() < 0.5F) {
                        this.FM.AS.hitTank(shot.initiator, j, 2);
                        this.mydebuggunnery("Fuel System: Fuel Tank Pierced, State Shifted..");
                    }
                }
                this.mydebuggunnery("Tank State: " + this.FM.AS.astateTankStates[j]);
            } else if (s.startsWith("xxmgun")) {
                if (s.endsWith("01")) {
                    this.mydebuggunnery("Armament System: Forward Machine Gun: Disabled..");
                    this.FM.AS.setJamBullets(0, 0);
                }
                if (s.endsWith("02")) {
                    this.mydebuggunnery("Armament System: Rear Machine Gun: Disabled..");
                    this.FM.AS.setJamBullets(1, 0);
                }
                this.getEnergyPastArmor(World.Rnd().nextFloat(0.3F, 12.6F), shot);
            }
        } else if (s.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
            if (point3d.x > -2.2D) {
                if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
                if (Aircraft.v1.x < -0.8D && World.Rnd().nextFloat() < 0.2F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                if (Aircraft.v1.x < -0.9D && World.Rnd().nextFloat() < 0.2F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                if (Math.abs(Aircraft.v1.x) < 0.8D) if (point3d.y > 0.0D) {
                    if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                    if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                } else {
                    if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                    if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                }
            }
        } else if (s.startsWith("xeng")) {
            if (this.chunkDamageVisible("Engine1") < 2) this.hitChunk("Engine1", shot);
        } else if (s.startsWith("xtail")) {
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", shot);
        } else if (s.startsWith("xkeel")) this.hitChunk("Keel1", shot);
        else if (s.startsWith("xrudder")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Rudder1") < 1) this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xstab")) {
            if (s.startsWith("xstabl")) this.hitChunk("StabL", shot);
            if (s.startsWith("xstabr")) this.hitChunk("StabR", shot);
        } else if (s.startsWith("xvator")) {
            if (s.startsWith("xvatorl") && this.chunkDamageVisible("VatorL") < 1) this.hitChunk("VatorL", shot);
            if (s.startsWith("xvatorr") && this.chunkDamageVisible("VatorR") < 1) this.hitChunk("VatorR", shot);
        } else if (s.startsWith("xwing")) {
            if (s.startsWith("xwinglin") && this.chunkDamageVisible("WingLIn") < 3) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingLIn", shot);
                this.hitChunk("Flap01", shot);
            }
            if (s.startsWith("xwingrin") && this.chunkDamageVisible("WingRIn") < 3) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRIn", shot);
                this.hitChunk("Flap02", shot);
            }
            if (s.startsWith("xwinglmid") && this.chunkDamageVisible("WingLMid") < 3) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingLMid", shot);
            }
            if (s.startsWith("xwingrmid") && this.chunkDamageVisible("WingRMid") < 3) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRMid", shot);
            }
            if (s.startsWith("xwinglout") && this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", shot);
            if (s.startsWith("xwingrout") && this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", shot);
        } else if (s.startsWith("xflap01") && this.chunkDamageVisible("Flap01") < 3) {
            this.setControlDamage(shot, 0);
            this.hitChunk("WingLIn", shot);
            this.hitChunk("Flap01", shot);
        } else if (s.startsWith("xflap02") && this.chunkDamageVisible("Flap02") < 3) {
            this.setControlDamage(shot, 0);
            this.hitChunk("WingRIn", shot);
            this.hitChunk("Flap02", shot);
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel") && this.chunkDamageVisible("AroneL") < 1) this.hitChunk("AroneL", shot);
            if (s.startsWith("xaroner") && this.chunkDamageVisible("AroneR") < 1) this.hitChunk("AroneR", shot);
        } else if (s.startsWith("xgearr")) {
            if (World.Rnd().nextFloat() < 0.1F && this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), shot) > 0.0F) {
                this.mydebuggunnery("Undercarriage: Stuck..");
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            }
            this.hitChunk("GearR2", shot);
        } else if (s.startsWith("xgearl")) {
            if (World.Rnd().nextFloat() < 0.1F && this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), shot) > 0.0F) {
                this.mydebuggunnery("Undercarriage: Stuck..");
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            }
            this.hitChunk("GearL2", shot);
        } else if (s.startsWith("xradiator")) {
            if (World.Rnd().nextFloat() < 0.12F) {
                this.FM.AS.hitOil(shot.initiator, 0);
                this.mydebuggunnery("*** Engine Module: Oil Radiator Hit..");
            }
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int k;
            if (s.endsWith("a")) {
                byte0 = 1;
                k = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                k = s.charAt(6) - 49;
            } else k = s.charAt(5) - 49;
            this.hitFlesh(k, shot, byte0);
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = af[0];
        float f1 = af[1];
        switch (i) {
            case 0:
                if (f < -70F) {
                    f = -70F;
                    flag = false;
                }
                if (f > 70F) {
                    f = 70F;
                    flag = false;
                }
                if (f1 < -45F) {
                    f1 = -45F;
                    flag = false;
                }
                if (f1 > 70F) {
                    f1 = 70F;
                    flag = false;
                }
                if ((f > -30F || f < 30F) && f1 < -10F) {
                    f1 = -10F;
                    flag = false;
                }
                break;
        }
        af[0] = f;
        af[1] = f1;
        return flag;
    }

    protected void mydebuggunnery(String s) {
    }

    public boolean  bPitUnfocused;
    public float    airBrakePos;
    private float   suspension;
    protected float arrestor;
    float           obsLookoutTimeLeft;
    float           obsLookoutAz;
    float           obsLookoutEl;
    float           obsLookoutAnim;
    float           obsLookoutMax;
    float           obsLookoutAzSpd;
    float           obsLookoutElSpd;
    int             obsLookoutIndex;
    float           obsLookoutPos[][];
    private int     obsLookTime;
    private float   obsLookAzimuth;
    private float   obsLookElevation;
    private float   obsAzimuth;
    private float   obsElevation;
    private float   obsAzimuthOld;
    private float   obsElevationOld;
    private float   obsMove;
    private float   obsMoveTot;
    boolean         bTAGKilled;
    boolean         bObserverKilled;

    static {
        Class class1 = Fulmar.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "originCountry", PaintScheme.countryBritain);
    }
}
