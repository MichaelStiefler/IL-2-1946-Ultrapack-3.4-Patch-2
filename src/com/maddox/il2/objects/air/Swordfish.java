package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.Controls;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.sas1946.il2.util.AircraftTools;

public abstract class Swordfish extends Scheme1 implements TypeBomber, TypeStormovik, TypeScout {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.hierMesh().chunkSetAngles("TurrBase_D0", 0.0F, 70F, 0.0F);
        this.hierMesh().chunkSetAngles("TurrBase1_D0", 70F, 0.0F, 0.0F);
        this.FM.Gears.computePlaneLandPose(this.FM);
//        this.mydebuggunnery("H = " + this.FM.Gears.H);
//        this.mydebuggunnery("Pitch = " + this.FM.Gears.Pitch);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Torpedo_Support_D0", thisWeaponsName.startsWith("1_") || thisWeaponsName.startsWith("6_"));
        hierMesh.chunkVisible("500lbWingRackC_D0", thisWeaponsName.startsWith("2_") || thisWeaponsName.startsWith("3_") || thisWeaponsName.startsWith("4_") || thisWeaponsName.startsWith("5_") || thisWeaponsName.startsWith("7_") || thisWeaponsName.startsWith("8_"));
        hierMesh.chunkVisible("500lbWingRackL_D0", thisWeaponsName.startsWith("2_") || thisWeaponsName.startsWith("5_"));
        hierMesh.chunkVisible("500lbWingRackR_D0", thisWeaponsName.startsWith("2_") || thisWeaponsName.startsWith("5_"));
        hierMesh.chunkVisible("250lbWingRackL01_D0", thisWeaponsName.startsWith("3_") || thisWeaponsName.startsWith("7_"));
        hierMesh.chunkVisible("250lbWingRackL02_D0", thisWeaponsName.startsWith("3_") || thisWeaponsName.startsWith("7_"));
        hierMesh.chunkVisible("250lbWingRackR01_D0", thisWeaponsName.startsWith("3_") || thisWeaponsName.startsWith("7_"));
        hierMesh.chunkVisible("250lbWingRackR02_D0", thisWeaponsName.startsWith("3_") || thisWeaponsName.startsWith("7_"));
        hierMesh.chunkVisible("FlareWingRackL_D0", thisWeaponsName.startsWith("4_") || thisWeaponsName.startsWith("5_") || thisWeaponsName.startsWith("6_") || thisWeaponsName.startsWith("7_") || thisWeaponsName.startsWith("8_"));
        hierMesh.chunkVisible("FlareWingRackR_D0", thisWeaponsName.startsWith("4_") || thisWeaponsName.startsWith("5_") || thisWeaponsName.startsWith("6_") || thisWeaponsName.startsWith("7_") || thisWeaponsName.startsWith("8_"));
    }
    
    public Swordfish() {
        this.bPitUnfocused = true;
        this.bIsWingTornOff = false;
        this.airBrakePos = 0.0F;
        this.arrestor = 0.0F;
        this.obsLookoutTimeLeft = 2.0F;
        this.obsLookoutAz = 0.0F;
        this.obsLookoutEl = 0.0F;
        this.obsLookoutPos = new float[3][129];
        this.wheel1 = 0.0F;
        this.wheel2 = 0.0F;
        this.slat = 0.0F;
        this.noenemy = 0;
        this.wait = 0;
        this.obsLookTime = 0;
        this.obsLookAzimuth = 0.0F;
        this.obsLookElevation = 0.0F;
        this.obsAzimuth = 0.0F;
        this.obsElevation = 0.0F;
        this.obsAzimuthOld = 0.0F;
        this.obsElevationOld = 0.0F;
        this.obsMove = 0.0F;
        this.obsMoveTot = 0.0F;
        this.TAGLookTime = 0;
        this.TAGLookAzimuth = 0.0F;
        this.TAGLookElevation = 0.0F;
        this.TAGAzimuth = 0.0F;
        this.TAGElevation = 0.0F;
        this.TAGAzimuthOld = 0.0F;
        this.TAGElevationOld = 0.0F;
        this.TAGMove = 0.0F;
        this.TAGMoveTot = 0.0F;
        this.bTAGKilled = false;
        this.bObserverKilled = false;
    }

    public void sfxAirBrake() {
    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 2:
                this.FM.turret[0].setHealth(f);
                break;
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
                this.hierMesh().chunkVisible("Head2_D0", false);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.bObserverKilled = true;
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("Pilot3up_D0", false);
                this.hierMesh().chunkVisible("Head3_D0", false);
                this.hierMesh().chunkVisible("HMask3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.bTAGKilled = true;
                break;
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (!this.bTAGKilled) {
            Actor actor = War.GetNearestEnemy(this, 16, 6000F);
            Aircraft aircraft = War.getNearestEnemy(this, 5000F);
            if (actor != null && !(actor instanceof BridgeSegment) || aircraft != null) {
                this.noenemy = 0;
                if (this.FM.CT.AirBrakeControl < 0.01F) {
                    this.wait = World.Rnd().nextInt(0, 30);
                    this.FM.CT.AirBrakeControl = 1.0F;
                }
            } else {
                this.noenemy++;
                if (this.noenemy > 30 + this.wait && this.FM.CT.AirBrakeControl > 0.99F) this.FM.CT.AirBrakeControl = 0.0F;
            }
        }
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
        if (!this.bTAGKilled) if (this.TAGLookTime == 0) {
            this.TAGLookTime = 2 + World.Rnd().nextInt(1, 3);
            this.TAGMoveTot = 1.0F + World.Rnd().nextFloat() * 1.5F;
            this.TAGMove = 0.0F;
            this.TAGAzimuthOld = this.TAGAzimuth;
            this.TAGElevationOld = this.TAGElevation;
            if (World.Rnd().nextFloat() > 0.8D) {
                this.TAGAzimuth = 0.0F;
                this.TAGElevation = 0.0F;
            } else {
                this.TAGAzimuth = World.Rnd().nextFloat() * 140F - 70F;
                this.TAGElevation = World.Rnd().nextFloat() * 50F - 20F;
            }
        } else this.TAGLookTime--;
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
            this.hierMesh().chunkVisible("HMask2_D0", false);
            this.hierMesh().chunkVisible("HMask3_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
            this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_D0"));
            this.hierMesh().chunkVisible("HMask3_D0", this.hierMesh().isChunkVisible("Pilot3_D0"));
        }
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.39F) this.FM.AS.hitTank(this, 0, 1);
            if (this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
        }
    }

    public void moveAirBrake(float f) {
        this.airBrakePos = f;
        if (this.bTAGKilled) return;
        this.hierMesh().chunkSetAngles("TurrBase_D0", 0.0F, 70F * (1.0F - f), 0.0F);
        this.hierMesh().chunkSetAngles("TurrBase1_D0", 70F * (1.0F - f), 0.0F, 0.0F);
        this.noenemy = 0;
        this.wait = World.Rnd().nextInt(0, 30);
        if (f > 0.99D) {
            this.resetYPRmodifier();
            this.hierMesh().chunkSetLocate("Pilot3_D0", Aircraft.xyz, Aircraft.ypr);
            this.hierMesh().chunkVisible("Pilot3_D0", false);
            this.hierMesh().chunkVisible("Head3_D0", false);
            this.hierMesh().chunkVisible("Pilot3up_D0", true);
        } else {
            if (!this.hierMesh().isChunkVisible("Pilot3_D0")) {
                this.hierMesh().chunkVisible("Pilot3_D0", true);
                this.hierMesh().chunkVisible("Head3_D0", true);
                this.hierMesh().chunkVisible("Pilot3up_D0", false);
            }
            this.resetYPRmodifier();
            Aircraft.xyz[2] = 0.45F * f;
            this.hierMesh().chunkSetLocate("Pilot3_D0", Aircraft.xyz, Aircraft.ypr);
            this.FM.turret[0].tu[0] = 0.0F;
            this.FM.turret[0].tu[1] = 0.0F;
        }
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC2_D0", 0.0F, -f, 0.0F);
    }

    public void moveWheelSink() {
        this.wheel1 = 0.8F * this.wheel1 + 0.2F * this.FM.Gears.gWheelSinking[0];
        this.wheel2 = 0.8F * this.wheel2 + 0.2F * this.FM.Gears.gWheelSinking[1];
        this.hierMesh().chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(this.wheel1, 0.0F, 0.04F, 0.0F, 9F), 0.0F);
        this.hierMesh().chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(this.wheel1, 0.0F, 0.04F, 0.0F, 5F), 0.0F);
        this.hierMesh().chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(this.wheel2, 0.0F, 0.04F, 0.0F, -9F), 0.0F);
        this.hierMesh().chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(this.wheel2, 0.0F, 0.04F, 0.0F, -5F), 0.0F);
    }

    public void update(float f) {
        Controls controls = this.FM.CT;
        float f2 = controls.getFlap();
        if (this.FM.CT.getArrestor() > 0.2F) if (this.FM.Gears.arrestorVAngle != 0.0F) {
            float f3 = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -26F, 11F, 1.0F, 0.0F);
            this.arrestor = 0.8F * this.arrestor + 0.2F * f3;
            this.moveArrestorHook(this.arrestor);
        } else {
            float f4 = -42F * this.FM.Gears.arrestorVSink / 37F;
            if (f4 < 0.0F && this.FM.getSpeedKMH() > 50F) Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
            if (f4 > 0.0F && this.FM.CT.getArrestor() < 0.95F) f4 = 0.0F;
            if (f4 > 0.0F) this.arrestor = 0.7F * this.arrestor + 0.3F * (this.arrestor + f4);
            else this.arrestor = 0.3F * this.arrestor + 0.7F * (this.arrestor + f4);
            if (this.arrestor < 0.0F) this.arrestor = 0.0F;
            else if (this.arrestor > 1.0F) this.arrestor = 1.0F;
            this.moveArrestorHook(this.arrestor);
        }
        float f5 = controls.getAileron();
        float f6 = -(f5 * 30F + f2 * 17F);
        this.hierMesh().chunkSetAngles("AroneL1_D0", 0.0F, f6, 0.0F);
        this.hierMesh().chunkSetAngles("AroneL2_D0", 0.0F, f6, 0.0F);
        f6 = -(f5 * 30F - f2 * 17F);
        this.hierMesh().chunkSetAngles("AroneR1_D0", 0.0F, f6, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR2_D0", 0.0F, f6, 0.0F);
        this.resetYPRmodifier();
        if (this.FM.EI.engines[0].getRPM() > 100F) this.slat = 0.96F * this.slat + 0.04F * Aircraft.cvt(this.FM.getSpeedKMH(), 80F, 110F, -0.18F, 0.0F);
        else this.slat = 0.995F * this.slat;
        Aircraft.xyz[1] = this.slat;
        this.hierMesh().chunkSetLocate("SlatR_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetLocate("SlatL_D0", Aircraft.xyz, Aircraft.ypr);
        if (this.FM.AS.isPilotParatrooper(2) && this.hierMesh().isChunkVisible("Pilot3up_D0")) this.hierMesh().chunkVisible("Pilot3up_D0", false);
        if (this.obsMove < this.obsMoveTot && !this.bObserverKilled && !this.FM.AS.isPilotParatrooper(1)) {
            if (this.obsMove < 0.2F || this.obsMove > this.obsMoveTot - 0.2F) this.obsMove += 0.3D * f;
            else if (this.obsMove < 0.1F || this.obsMove > this.obsMoveTot - 0.1F) this.obsMove += 0.15F;
            else this.obsMove += 1.2D * f;
            this.obsLookAzimuth = Aircraft.cvt(this.obsMove, 0.0F, this.obsMoveTot, this.obsAzimuthOld, this.obsAzimuth);
            this.obsLookElevation = Aircraft.cvt(this.obsMove, 0.0F, this.obsMoveTot, this.obsElevationOld, this.obsElevation);
            this.hierMesh().chunkSetAngles("Head2_D0", 0.0F, this.obsLookAzimuth, this.obsLookElevation);
        }
        if (this.TAGMove < this.TAGMoveTot && !this.bTAGKilled && !this.FM.AS.isPilotParatrooper(2)) {
            if (this.TAGMove < 0.2F || this.TAGMove > this.TAGMoveTot - 0.2F) this.TAGMove += 0.3D * f;
            else if (this.TAGMove < 0.1F || this.TAGMove > this.TAGMoveTot - 0.1F) this.TAGMove += 0.15F;
            else this.TAGMove += 1.2D * f;
            this.TAGLookAzimuth = Aircraft.cvt(this.TAGMove, 0.0F, this.TAGMoveTot, this.TAGAzimuthOld, this.TAGAzimuth);
            this.TAGLookElevation = Aircraft.cvt(this.TAGMove, 0.0F, this.TAGMoveTot, this.TAGElevationOld, this.TAGElevation);
            this.hierMesh().chunkSetAngles("Head3_D0", 0.0F, this.TAGLookAzimuth, this.TAGLookElevation);
        }
        super.update(f);
    }

    protected void moveAileron(float f) {
        Controls controls = this.FM.CT;
        float f1 = controls.getFlap();
        float f2 = -(f * 30F + f1 * 17F);
        this.hierMesh().chunkSetAngles("AroneL1_D0", 0.0F, f2, 0.0F);
        this.hierMesh().chunkSetAngles("AroneL2_D0", 0.0F, f2, 0.0F);
        f2 = -(f * 30F - f1 * 17F);
        this.hierMesh().chunkSetAngles("AroneR1_D0", 0.0F, f2, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR2_D0", 0.0F, f2, 0.0F);
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
        hiermesh.chunkSetAngles("WingLIn_D0", 0.0F, 85F * f, 0.0F);
        hiermesh.chunkSetAngles("WingRIn_D0", 0.0F, -85F * f, 0.0F);
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
                        case 2:
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
                if (s.startsWith("xxsparlo") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingLMid Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot) > 0.0F) {
                    this.mydebuggunnery("Spar Construction: WingRMid Spar Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxspart") && this.chunkDamageVisible("Tail1") > 2 && this.getEnergyPastArmor(3.86F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                    this.mydebuggunnery("Spar Construction: Tail1 Ribs Hit, Breaking in Half..");
                    this.nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
            } else {
                if (s.startsWith("xxlock")) {
                    this.mydebuggunnery("Lock Construction: Hit..");
                    if (s.startsWith("xxlockr") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                        this.mydebuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                    }
                    if (s.startsWith("xxlockvl") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                        this.mydebuggunnery("Lock Construction: VatorL Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                    }
                    if (s.startsWith("xxlockvr") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                        this.mydebuggunnery("Lock Construction: VatorR Lock Shot Off..");
                        this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                    }
                }
                if (s.startsWith("xxeng")) {
                    if ((s.endsWith("prop") || s.endsWith("pipe")) && this.getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) this.FM.EI.engines[0].setKillPropAngleDevice(shot.initiator);
                    if (s.endsWith("case") || s.endsWith("gear")) {
                        this.mydebuggunnery("*** Engine Crank Case Hit");
                        if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < shot.power / 140000F) {
                                this.FM.AS.setEngineStuck(shot.initiator, 0);
                                this.mydebuggunnery("*** Engine Crank Case Hit - Engine Stucks..");
                            } else if (World.Rnd().nextFloat() < shot.power / 85000F) {
                                this.FM.AS.hitEngine(shot.initiator, 0, 2);
                                this.mydebuggunnery("*** Engine Crank Case Hit - Engine Damaged..");
                            } else {
                                this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - 0.002F);
                                this.mydebuggunnery("*** Engine Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                            }
                        } else if (World.Rnd().nextFloat() < 0.05F) {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, 1);
                            this.mydebuggunnery("*** Engine Cylinders Damaged..");
                        } else {
                            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - 0.002F);
                            this.mydebuggunnery("*** Engine Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                        }
                        this.getEnergyPastArmor(12F, shot);
                    }
                    if (s.endsWith("cyls")) {
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
                    if (s.endsWith("supc") && this.getEnergyPastArmor(0.05F, shot) > 0.0F) {
                        this.mydebuggunnery("*** Engine Compressor Hit ..");
                        this.FM.EI.engines[0].setKillCompressor(shot.initiator);
                        this.getEnergyPastArmor(2.0F, shot);
                    }
                    this.mydebuggunnery("*** Engine state = " + this.FM.AS.astateEngineStates[0]);
                } else if (s.startsWith("xxoil")) {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    this.mydebuggunnery("*** Engine Module: Oil Radiator Hit..");
                } else if (s.startsWith("xxtank")) {
                    int j = s.charAt(6) - 49;
                    if (this.getEnergyPastArmor(0.4F, shot) > 0.0F) if (shot.power < 14100F) {
                        if (this.FM.AS.astateTankStates[j] < 1) this.FM.AS.hitTank(shot.initiator, j, 1);
                        if (this.FM.AS.astateTankStates[j] < 4 && World.Rnd().nextFloat() < 0.15F) this.FM.AS.hitTank(shot.initiator, j, 1);
                        if (shot.powerType == 3 && this.FM.AS.astateTankStates[j] > 1 && World.Rnd().nextFloat() < 0.2F) this.FM.AS.hitTank(shot.initiator, j, 10);
                    } else this.FM.AS.hitTank(shot.initiator, j, World.Rnd().nextInt(0, (int) (shot.power / 35000F)));
                    this.mydebuggunnery("*** Tank " + (j + 1) + " state = " + this.FM.AS.astateTankStates[j]);
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
            }
        } else if (s.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
        } else if (s.startsWith("xarmorp1")) {
            this.getEnergyPastArmor(20.76D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot);
            if (shot.power <= 0.0F) this.doRicochetBack(shot);
        } else if (s.startsWith("xmgun01")) {
            if (this.getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 8F), shot) > 0.0F) {
                this.mydebuggunnery("Armament System: Forward Machine Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 0);
            }
        } else if (s.startsWith("xmgun02")) {
            if (this.getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 8F), shot) > 0.0F) {
                this.mydebuggunnery("Armament System: Rear Machine Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 1);
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
            }
            if (s.startsWith("xwingrin") && this.chunkDamageVisible("WingRIn") < 3) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRIn", shot);
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
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel1")) this.hitChunk("AroneL1", shot);
            if (s.startsWith("xaronel2")) this.hitChunk("AroneL2", shot);
            if (s.startsWith("xaroner1")) this.hitChunk("AroneR1", shot);
            if (s.startsWith("xaroner2")) this.hitChunk("AroneR2", shot);
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

    public abstract void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException;

    public abstract void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException;

    public abstract void typeBomberUpdate(float f);

    public abstract void typeBomberAdjSpeedMinus();

    public abstract void typeBomberAdjSpeedPlus();

    public abstract void typeBomberAdjSpeedReset();

    public abstract void typeBomberAdjAltitudeMinus();

    public abstract void typeBomberAdjAltitudePlus();

    public abstract void typeBomberAdjAltitudeReset();

    public abstract void typeBomberAdjSideslipMinus();

    public abstract void typeBomberAdjSideslipPlus();

    public abstract void typeBomberAdjSideslipReset();

    public abstract void typeBomberAdjDistanceMinus();

    public abstract void typeBomberAdjDistancePlus();

    public abstract void typeBomberAdjDistanceReset();

    public abstract boolean typeBomberToggleAutomation();

//    // TODO: Storebror: Head Movement Bugfix for Swordfish
//    private float         headPos[] = { 0, 0, 0 };
//    private float         headOr[]  = { 0, 0, 0 };
//    private static Orient tmpOrLH   = new Orient();
//    private float         headYp;
//    private float         headTp;
//    private float         headYm;
//    private float         headTm;

    public void movePilotsHead(float f, float f1) {
        super.movePilotsHead(-f, f1);
//        f *= -1F;
//        if (Config.isUSE_RENDER() && (this.headTp < f1 || this.headTm > f1 || this.headYp < f || this.headYm > f)) {
//            this.headTp = f1 + 0.0005F;
//            this.headTm = f1 - 0.0005F;
//            this.headYp = f + 0.0005F;
//            this.headYm = f - 0.0005F;
//
//            tmpOrLH.setYPR(0.0F, 0.0F, 0.0F);
//            tmpOrLH.increment(0.0F, f, 0.0F);
//            tmpOrLH.increment(0.0F, 0.0F, -f1);
//            tmpOrLH.increment(-0.2F * f1 + 0.05F * f1, 0.0F, 0.0F);
//
//            this.headOr[0] = tmpOrLH.getYaw();
//            this.headOr[1] = tmpOrLH.getPitch();
//            this.headOr[2] = tmpOrLH.getRoll();
//
//            this.headPos[0] = 0.0005F * Math.abs(f);
//            this.headPos[1] = -0.0001F * Math.abs(f);
//            this.headPos[2] = 0.0F;
//            this.hierMesh().chunkSetLocate("Head1_D0", this.headPos, this.headOr);
//        }
    }

    public boolean  bPitUnfocused;
    boolean         bIsWingTornOff;
    public float    airBrakePos;
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
    private float   wheel1;
    private float   wheel2;
    private float   slat;
    private int     noenemy;
    private int     wait;
    private int     obsLookTime;
    private float   obsLookAzimuth;
    private float   obsLookElevation;
    private float   obsAzimuth;
    private float   obsElevation;
    private float   obsAzimuthOld;
    private float   obsElevationOld;
    private float   obsMove;
    private float   obsMoveTot;
    private int     TAGLookTime;
    private float   TAGLookAzimuth;
    private float   TAGLookElevation;
    private float   TAGAzimuth;
    private float   TAGElevation;
    private float   TAGAzimuthOld;
    private float   TAGElevationOld;
    private float   TAGMove;
    private float   TAGMoveTot;
    boolean         bTAGKilled;
    boolean         bObserverKilled;

    static {
        Class class1 = Swordfish.class;
        Property.set(class1, "originCountry", PaintScheme.countryBritain);
    }
}
