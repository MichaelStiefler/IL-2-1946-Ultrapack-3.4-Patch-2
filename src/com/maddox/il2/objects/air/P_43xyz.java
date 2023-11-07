package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public class P_43xyz extends Scheme1 implements TypeFighter, TypeBNZFighter, TypeStormovik {

    public P_43xyz() {
        P_43xyz.kl = 1.0F;
        P_43xyz.kr = 1.0F;
        P_43xyz.kc = 1.0F;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("RackC_D0", thisWeaponsName.startsWith("1x250lbs"));
        hierMesh.chunkVisible("RackL_D0", thisWeaponsName.startsWith("2x100lbs") || thisWeaponsName.startsWith("2xdpt"));
        hierMesh.chunkVisible("RackR_D0", thisWeaponsName.startsWith("2x100lbs") || thisWeaponsName.startsWith("2xdpt"));
    }
    
    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        if (this.FM.isPlayers()) {
            this.FM.CT.bHasCockpitDoorControl = true;
            this.FM.CT.dvCockpitDoor = 0.5F;
            this.FM.CT.cockpitDoorControl = 1.0F;
        }
    }

    public void update(float f) {
        if (this.FM.isPlayers() && (this.FM.Sq.squareElevators > 0.0F)) {
            RealFlightModel realflightmodel = (RealFlightModel) this.FM;
            if (realflightmodel.RealMode && (realflightmodel.indSpeed > 120F)) {
                float f3 = 1.0F + (0.005F * (120F - realflightmodel.indSpeed));
                if (f3 < 0.0F) {
                    f3 = 0.0F;
                }
                this.FM.SensPitch = 0.45F * f3;
                if (realflightmodel.indSpeed > 120F) {
                    this.FM.producedAM.y -= 1720F * (120F - realflightmodel.indSpeed);
                }
            } else {
                this.FM.SensPitch = 0.6F;
            }
        }
        if (!this.FM.isPlayers() && this.FM.Gears.onGround()) {
            if (this.FM.EI.engines[0].getRPM() < 100F) {
                this.FM.CT.cockpitDoorControl = 1.0F;
            } else {
                this.FM.CT.cockpitDoorControl = 0.0F;
            }
        }
        super.update(f);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        float f1 = Math.max(-f * 800F, -95F);
        float f2 = Math.max(-f * 800F, -130F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, f1 * P_43xyz.kl, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, -f1 * P_43xyz.kr, 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, f2 * P_43xyz.kc, 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, -f2 * P_43xyz.kc, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 0.0F, 40F * f * P_43xyz.kc);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 82F * f * P_43xyz.kl, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -82F * f * P_43xyz.kr, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 30F * f * P_43xyz.kl, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", -30F * f * P_43xyz.kr, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, 94F * f * P_43xyz.kl, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, -94F * f * P_43xyz.kr, 0.0F);
    }

    protected void moveGear(float f) {
        P_43xyz.moveGear(this.hierMesh(), f);
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 30F * f, 0.0F, 0.0F);
    }

    protected void moveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 0.0F, -25F * f);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 0.0F, -25F * f);
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 0.0F, 25F * f);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 0.0F, -25F * f);
    }

    protected void moveFlap(float f) {
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, 0.0F, 40F * f);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, 0.0F, 40F * f);
    }

    public void doMurderPilot(int i) {
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("Head" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("HMask" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            for (int i = 1; i < 2; i++) {
                if (this.FM.getAltitude() < 3000F) {
                    this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
                } else {
                    this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));
                }
            }

        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.endsWith("p1")) {
                    this.getEnergyPastArmor(8.26F / (Math.abs((float) Aircraft.v1.x) + 1E-005F), shot);
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                int i = s.charAt(5) - 49;
                if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(1.7F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat(20000F, 140000F) < shot.power) {
                            this.FM.AS.setEngineStuck(shot.initiator, i);
                        }
                        if (World.Rnd().nextFloat(10000F, 50000F) < shot.power) {
                            this.FM.AS.hitEngine(shot.initiator, i, 2);
                        }
                    } else if (World.Rnd().nextFloat() < 0.04F) {
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, 1);
                    } else {
                        this.FM.EI.engines[i].setReadyness(shot.initiator, this.FM.EI.engines[i].getReadyness() - 0.02F);
                    }
                    this.getEnergyPastArmor(12F, shot);
                }
                if (s.endsWith("cyls")) {
                    if ((this.getEnergyPastArmor(0.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[i].getCylindersRatio() * 0.9878F))) {
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                        if (World.Rnd().nextFloat() < (shot.power / 48000F)) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                        }
                    }
                    this.getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("supc")) {
                    if (this.getEnergyPastArmor(0.05F, shot) > 0.0F) {
                        this.FM.EI.engines[i].setKillCompressor(shot.initiator);
                    }
                    this.getEnergyPastArmor(2.0F, shot);
                }
                if (s.endsWith("oil1") && (World.Rnd().nextFloat() < 0.5F) && (this.getEnergyPastArmor(0.25F, shot) > 0.0F)) {
                    this.FM.AS.hitOil(shot.initiator, 0);
                }
            }
            return;
        }
        if (s.startsWith("xxmgun")) {
            if (s.endsWith("1")) {
                this.debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 0);
            }
            if (s.endsWith("2")) {
                this.debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 1);
            }
            if (s.endsWith("3")) {
                this.debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 2);
            }
            if (s.endsWith("4")) {
                this.debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 3);
            }
            return;
        }
        if (s.startsWith("xxtank")) {
            int j = s.charAt(6) - 49;
            if ((this.getEnergyPastArmor(1.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.45F)) {
                if (this.FM.AS.astateTankStates[j] == 0) {
                    this.FM.AS.hitTank(shot.initiator, j, 1);
                    this.FM.AS.doSetTankState(shot.initiator, j, 1);
                }
                if ((World.Rnd().nextFloat() < 0.02F) || ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.45F))) {
                    this.FM.AS.hitTank(shot.initiator, j, 4);
                }
            }
            return;
        }
        if (s.startsWith("xxoil1")) {
            if ((this.getEnergyPastArmor(0.25F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                this.FM.AS.hitOil(shot.initiator, 0);
                this.getEnergyPastArmor(0.22F, shot);
                this.debuggunnery("Engine Module: Oil Radiator 1 Pierced..");
            }
            return;
        }
        if (s.startsWith("xxcontrols")) {
            this.debuggunnery("Controls: Hit..");
            int k = s.charAt(10) - 48;
            switch (k) {
                default:
                    break;

                case 1:
                    if ((this.getEnergyPastArmor(4.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                        this.debuggunnery("Controls: Elevator Controls: Disabled..");
                        this.FM.AS.setControlsDamage(shot.initiator, 1);
                    }
                    if ((this.getEnergyPastArmor(0.002F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.11F)) {
                        this.debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                    }
                    break;

                case 2:
                    if ((this.getEnergyPastArmor(1.0F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.12F)) {
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                        this.debuggunnery("Ailerons Controls Out..");
                    }
                    break;

                case 3:
                    if (this.getEnergyPastArmor(0.1F, shot) <= 0.0F) {
                        break;
                    }
                    if (World.Rnd().nextFloat() < 0.25F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                        this.debuggunnery("*** Engine1 Throttle Controls Out..");
                    }
                    if (World.Rnd().nextFloat() < 0.15F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                        this.debuggunnery("*** Engine1 Prop Controls Out..");
                    }
                    break;
            }
            return;
        }
        if (s.startsWith("xxlock")) {
            this.debuggunnery("Lock Construction: Hit..");
            if (s.startsWith("xxlockr1") && (this.getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                this.debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
            }
            if (s.startsWith("xxlockvl") && (this.getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                this.debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
            }
            if (s.startsWith("xxlockvr") && (this.getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                this.debuggunnery("Lock Construction: VatorR Lock Shot Off..");
                this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
            }
            if (s.startsWith("xxlockal") && (this.getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                this.debuggunnery("Lock Construction: AroneL Lock Shot Off..");
                this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
            }
            if (s.startsWith("xxlockar") && (this.getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                this.debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
            }
            return;
        }
        if (s.startsWith("xxspar")) {
            if (s.startsWith("xxsparli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                this.debuggunnery("*** WingLIn Spars Damaged..");
                this.nextDMGLevels(1, 2, "WingLIn_D2", shot.initiator);
            }
            if (s.startsWith("xxsparri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                this.debuggunnery("*** WingRIn Spars Damaged..");
                this.nextDMGLevels(1, 2, "WingRIn_D2", shot.initiator);
            }
            if (s.startsWith("xxsparlo") && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                this.debuggunnery("*** WingLOut Spars Damaged..");
                this.nextDMGLevels(1, 2, "WingLOut_D2", shot.initiator);
            }
            if (s.startsWith("xxsparro") && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                this.debuggunnery("*** WingROut Spars Damaged..");
                this.nextDMGLevels(1, 2, "WingROut_D2", shot.initiator);
            }
            if (s.startsWith("xxspark1") && (this.chunkDamageVisible("Keel1") > 1) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                this.debuggunnery("*** Keel1 Spars Damaged..");
                this.nextDMGLevels(1, 2, "Keel1_D2" + this.chunkDamageVisible("Keel1"), shot.initiator);
            }
            if (s.startsWith("xxsparsl") && (this.chunkDamageVisible("StabL") > 1) && (this.getEnergyPastArmor(11.2F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                this.debuggunnery("*** StabL Spars Damaged..");
                this.nextDMGLevels(1, 2, "StabL_D2", shot.initiator);
            }
            if (s.startsWith("xxsparsr") && (this.chunkDamageVisible("StabR") > 1) && (this.getEnergyPastArmor(11.2F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                this.debuggunnery("*** StabR Spars Damaged..");
                this.nextDMGLevels(1, 2, "StabR_D2", shot.initiator);
            }
            if (s.startsWith("xxspart") && (this.chunkDamageVisible("Tail1") > 1) && (this.getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)) {
                this.debuggunnery("*** Tail1 Spars Damaged..");
                this.nextDMGLevels(1, 2, "Tail1_D2" + this.chunkDamageVisible("Taill1"), shot.initiator);
            }
            return;
        }
        if (s.startsWith("xcf")) {
            if (this.chunkDamageVisible("CF") < 2) {
                this.hitChunk("CF", shot);
            }
            if (World.Rnd().nextFloat() < 0.19F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            }
            if (World.Rnd().nextFloat() < 0.19F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
            }
            if (World.Rnd().nextFloat() < 0.19F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            }
            if (World.Rnd().nextFloat() < 0.19F) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            }
        } else if (s.startsWith("xeng")) {
            if (this.chunkDamageVisible("Engine1") < 2) {
                this.hitChunk("Engine1", shot);
            }
        } else if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 2) {
                this.hitChunk("Tail1", shot);
            }
        } else if (s.startsWith("xkeel")) {
            if (this.chunkDamageVisible("Keel1") < 2) {
                this.hitChunk("Keel1", shot);
            }
        } else if (s.startsWith("xrudder")) {
            if (this.chunkDamageVisible("Rudder1") < 2) {
                this.hitChunk("Rudder1", shot);
            }
        } else if (s.startsWith("xstab")) {
            if (s.startsWith("xstabl") && (this.chunkDamageVisible("StabL") < 2)) {
                this.hitChunk("StabL", shot);
            }
            if (s.startsWith("xstabr") && (this.chunkDamageVisible("StabR") < 2)) {
                this.hitChunk("StabR", shot);
            }
        } else if (s.startsWith("xvator")) {
            if (s.startsWith("xvatorl") && (this.chunkDamageVisible("VatorL") < 2)) {
                this.hitChunk("VatorL", shot);
            }
            if (s.startsWith("xvatorr") && (this.chunkDamageVisible("VatorR") < 2)) {
                this.hitChunk("VatorR", shot);
            }
        } else if (s.startsWith("xwing")) {
            if (s.startsWith("xwinglin") && (this.chunkDamageVisible("WingLIn") < 2)) {
                this.hitChunk("WingLIn", shot);
            }
            if (s.startsWith("xwingrin") && (this.chunkDamageVisible("WingRIn") < 2)) {
                this.hitChunk("WingRIn", shot);
            }
            if (s.startsWith("xwinglout") && (this.chunkDamageVisible("WingLOut") < 2)) {
                this.hitChunk("WingLOut", shot);
            }
            if (s.startsWith("xwingrout") && (this.chunkDamageVisible("WingROut") < 2)) {
                this.hitChunk("WingROut", shot);
            }
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel") && (this.chunkDamageVisible("AroneL") < 2)) {
                this.hitChunk("AroneL", shot);
            }
            if (s.startsWith("xaroner") && (this.chunkDamageVisible("AroneR") < 2)) {
                this.hitChunk("AroneR", shot);
            }
        } else if (s.startsWith("xflap")) {
            if (s.endsWith("01") && (this.chunkDamageVisible("Flap01") < 2)) {
                this.hitChunk("Flap01", shot);
            }
            if (s.startsWith("02") && (this.chunkDamageVisible("Flap02") < 2)) {
                this.hitChunk("Flap02", shot);
            }
        } else if (s.startsWith("xgear")) {
            this.gearDamageFX(s);
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int l;
            if (s.endsWith("a")) {
                byte0 = 1;
                l = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                l = s.charAt(6) - 49;
            } else {
                l = s.charAt(5) - 49;
            }
            this.hitFlesh(l, shot, byte0);
        }
    }

    private void gearDamageFX(String s) {
        if (s.startsWith("xgearl")) {
            if (this.FM.isPlayers()) {
                HUD.log("Left Gear:  Hydraulic system Failed");
            }
            P_43xyz.kl = World.Rnd().nextFloat();
            P_43xyz.kr = World.Rnd().nextFloat() * P_43xyz.kl;
            P_43xyz.kc = 0.1F;
        } else if (s.startsWith("xgearr")) {
            if (this.FM.isPlayers()) {
                HUD.log("Right Gear:  Hydraulic system Failed");
            }
            P_43xyz.kr = World.Rnd().nextFloat();
            P_43xyz.kl = World.Rnd().nextFloat() * P_43xyz.kr;
            P_43xyz.kc = 0.1F;
        } else if (s.endsWith("gear")) {
            if (this.FM.isPlayers()) {
                HUD.log("All Gears:  Hydraulic system Failed");
            }
            P_43xyz.kr = World.Rnd().nextFloat();
            P_43xyz.kl = World.Rnd().nextFloat();
            P_43xyz.kc = World.Rnd().nextFloat();
        }
        P_43xyz.kc = 0.0F;
        this.FM.CT.GearControl = 0.4F;
        this.FM.Gears.setHydroOperable(false);
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.58F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    private static float kl = 1.0F;
    private static float kr = 1.0F;
    private static float kc = 1.0F;

    static {
        Class class1 = P_43xyz.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
    }
}
