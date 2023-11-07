package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public abstract class Beaufort extends Scheme2 implements TypeBomber {

    public Beaufort() {
        this.bombBayDoorsRemoved = false;
        this.bSightAutomation = false;
        this.bSightBombDump = false;
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
        this.fSightCurAltitude = 850F;
        this.fSightCurSpeed = 150F;
        this.fSightCurReadyness = 0.0F;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("BayDoorL_D0", !thisWeaponsName.startsWith("1x"));
        hierMesh.chunkVisible("BayDoorR_D0", !thisWeaponsName.startsWith("1x"));
    }

    public boolean needsOpenBombBay() {
        return !this.bombBayDoorsRemoved;
    }

    public boolean canOpenBombBay() {
        return !this.bombBayDoorsRemoved;
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.4F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(f);
            this.setDoorSnd(f);
        }
    }

    protected void moveBayDoor(float f) {
        if (!this.thisWeaponsName.startsWith("1xt")) {
            this.hierMesh().chunkSetAngles("BayDoorL_D0", 0.0F, -90F * f, 0.0F);
            this.hierMesh().chunkSetAngles("BayDoorR_D0", 0.0F, -90F * f, 0.0F);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 13:
                this.killPilot(this, 1);
                break;

            case 33:
            case 34:
                this.hitProp(0, j, actor);
                this.cut("Engine1");
                break;

            case 36:
            case 37:
                this.hitProp(1, j, actor);
                this.cut("Engine2");
                break;
        }
        return super.cutFM(i, j, actor);
    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.2F, 0.0F, 0.165F);
        this.hierMesh().chunkSetLocate("GearL25_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.2F, 0.0F, 0.165F);
        this.hierMesh().chunkSetLocate("GearR25_D0", Aircraft.xyz, Aircraft.ypr);
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC2_D0", 0.0F, -f, 0.0F);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.endsWith("e1")) this.getEnergyPastArmor(12.1D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot);
                if (s.endsWith("e2")) this.getEnergyPastArmor(12.1D / (Math.abs(Aircraft.v1.x) + 9.9999997473787516E-005D), shot);
                if (s.endsWith("p1")) this.getEnergyPastArmor(12.7F, shot);
                if (s.endsWith("p2")) this.getEnergyPastArmor(12.7F, shot);
                return;
            }
            if (s.startsWith("xxcontrols")) {
                int i = s.charAt(10) - 48;
                switch (i) {
                    default:
                        break;

                    case 1:
                        if (this.getEnergyPastArmor(1.5F, shot) > 0.0F && World.Rnd().nextFloat() < 0.15F) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                            if (World.Rnd().nextFloat() < 0.15F) this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                            if (World.Rnd().nextFloat() < 0.15F) this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 7);
                        }
                        break;

                    case 2:
                        if (this.getEnergyPastArmor(1.5F, shot) <= 0.0F) break;
                        if (World.Rnd().nextFloat() < 0.15F) this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 1);
                        if (World.Rnd().nextFloat() < 0.15F) this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 6);
                        if (World.Rnd().nextFloat() < 0.15F) this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 7);
                        break;

                    case 3:
                    case 4:
                        if (this.getEnergyPastArmor(6F, shot) <= 0.0F) break;
                        if (World.Rnd().nextFloat() < 0.5F) this.FM.AS.setControlsDamage(shot.initiator, 1);
                        if (World.Rnd().nextFloat() < 0.5F) this.FM.AS.setControlsDamage(shot.initiator, 2);
                        break;

                    case 5:
                    case 6:
                        if (this.getEnergyPastArmor(1.5F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) this.FM.AS.setControlsDamage(shot.initiator, 0);
                        break;
                }
                return;
            }
            if (s.startsWith("xxengine")) {
                int j = 0;
                if (s.startsWith("xxengine2")) j = 1;
                if (this.getEnergyPastArmor(World.Rnd().nextFloat(0.2F, 0.55F), shot) > 0.0F) {
                    if (World.Rnd().nextFloat() < shot.power / 280000F) this.FM.AS.setEngineStuck(shot.initiator, j);
                    if (World.Rnd().nextFloat() < shot.power / 100000F) this.FM.AS.hitEngine(shot.initiator, j, 2);
                }
                if (this.getEnergyPastArmor(0.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[j].getCylindersRatio() * 0.75F) {
                    this.FM.EI.engines[j].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 25200F)));
                    if (World.Rnd().nextFloat() < shot.power / 80000F) this.FM.AS.hitEngine(shot.initiator, j, 2);
                }
                this.getEnergyPastArmor(25F, shot);
                return;
            }
            if (s.startsWith("xxmgun")) {
                int k = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(4.85F, shot) > 0.0F && World.Rnd().nextFloat() < 0.75F) {
                    this.FM.AS.setJamBullets(0, k);
                    this.getEnergyPastArmor(11.98F, shot);
                }
                return;
            }
            if (s.startsWith("xxoil")) {
                int l = 0;
                if (s.endsWith("2")) l = 1;
                if (this.getEnergyPastArmor(0.21F, shot) > 0.0F && World.Rnd().nextFloat() < 0.2435F) this.FM.AS.hitOil(shot.initiator, l);
                Aircraft.debugprintln(this, "*** Engine (" + l + ") Module: Oil Tank Pierced..");
                return;
            }
            if (s.startsWith("xxprop1") && this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F) if (World.Rnd().nextFloat() < 0.5F) {
                this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                Aircraft.debugprintln(this, "*** Engine1 Module: Prop Governor Hit, Disabled..");
            } else {
                this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                Aircraft.debugprintln(this, "*** Engine1 Module: Prop Governor Hit, Damaged..");
            }
            if (s.startsWith("xxprop2") && this.getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F) if (World.Rnd().nextFloat() < 0.5F) {
                this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 3);
                Aircraft.debugprintln(this, "*** Engine2 Module: Prop Governor Hit, Disabled..");
            } else {
                this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 4);
                Aircraft.debugprintln(this, "*** Engine2 Module: Prop Governor Hit, Damaged..");
            }
            if (s.startsWith("xxspar")) {
                if (s.startsWith("xxsparli") && this.chunkDamageVisible("WingLIn") > 2 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                if (s.startsWith("xxsparri") && this.chunkDamageVisible("WingRIn") > 2 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                if (s.startsWith("xxsparlm") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                if (s.startsWith("xxsparrm") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                if (s.startsWith("xxsparlo") && this.chunkDamageVisible("WingLOut") > 2 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                if (s.startsWith("xxsparro") && this.chunkDamageVisible("WingROut") > 2 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                if (s.startsWith("xxspark") && this.chunkDamageVisible("Keel1") > 1 && this.getEnergyPastArmor(9.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) this.nextDMGLevels(1, 2, "Keel1_D2", shot.initiator);
                return;
            }
            if (s.startsWith("xxstruts")) {
                if (s.startsWith("xxstruts1") && this.chunkDamageVisible("Engine1") > 1 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 8F), shot) > 0.0F) this.nextDMGLevels(1, 2, "Engine1_D2", shot.initiator);
                if (s.startsWith("xxstruts2") && this.chunkDamageVisible("Engine2") > 1 && this.getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 8F), shot) > 0.0F) this.nextDMGLevels(1, 2, "Engine2_D2", shot.initiator);
                return;
            }
            if (s.startsWith("xxtank")) {
                int i1 = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(0.9F, shot) > 0.0F && World.Rnd().nextFloat() < 11.25F) if (shot.power < 12000F) {
                    if (this.FM.AS.astateTankStates[i1] == 0) {
                        this.FM.AS.hitTank(shot.initiator, i1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i1, 1);
                    }
                    if (this.FM.AS.astateTankStates[i1] < 4 && World.Rnd().nextFloat() < 0.1F) this.FM.AS.hitTank(shot.initiator, i1, 1);
                    if (shot.powerType == 3 && World.Rnd().nextFloat() < 0.15F) this.FM.AS.hitTank(shot.initiator, i1, 10);
                } else this.FM.AS.hitTank(shot.initiator, i1, World.Rnd().nextInt(0, (int) (shot.power / 40000F)));
                return;
            } else return;
        }
        if (s.startsWith("xcf")) {
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
            if (point3d.x > 0.5D) {
                if (point3d.z > 0.913D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                if (point3d.z > 0.341D) {
                    if (point3d.x < 1.402D) {
                        if (point3d.y > 0.0D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                        else this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                    } else this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                } else if (point3d.y > 0.0D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                else this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                if (point3d.x > 1.691D && point3d.x < 1.98D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            }
        } else if (s.startsWith("xnose")) this.hitChunk("Nose", shot);
        else if (s.startsWith("xtail")) this.hitChunk("Tail1", shot);
        else if (s.startsWith("xkeel1")) {
            if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xrudder1")) this.hitChunk("Rudder1", shot);
        else if (s.startsWith("xstabl")) this.hitChunk("StabL", shot);
        else if (s.startsWith("xstabr")) this.hitChunk("StabR", shot);
        else if (s.startsWith("xvatorl")) this.hitChunk("VatorL", shot);
        else if (s.startsWith("xvatorr")) this.hitChunk("VatorR", shot);
        else if (s.startsWith("xwinglin")) {
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
        } else if (s.startsWith("xaronel")) this.hitChunk("AroneL", shot);
        else if (s.startsWith("xaroner")) this.hitChunk("AroneR", shot);
        else if (s.startsWith("xengine1")) {
            if (this.chunkDamageVisible("Engine1") < 2) this.hitChunk("Engine1", shot);
            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 45000F));
        } else if (s.startsWith("xengine2")) {
            if (this.chunkDamageVisible("Engine2") < 2) this.hitChunk("Engine2", shot);
            this.FM.EI.engines[1].setReadyness(shot.initiator, this.FM.EI.engines[1].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 45000F));
        } else if (s.startsWith("xgearl")) this.hitChunk("GearL2", shot);
        else if (s.startsWith("xgearr")) this.hitChunk("GearR2", shot);
        else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int j1;
            if (s.endsWith("a")) {
                byte0 = 1;
                j1 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                j1 = s.charAt(6) - 49;
            } else j1 = s.charAt(5) - 49;
            this.hitFlesh(j1, shot, byte0);
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.hierMesh().chunkVisible("Head1_D0", false);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("HMask3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                break;

            case 3:
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                this.hierMesh().chunkVisible("HMask4_D0", false);
                this.hierMesh().chunkVisible("Pilot4_D1", true);
                break;
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (!this.bombBayDoorsRemoved) if (this.hierMesh().isChunkVisible("BaydoorL_D0") && this.hierMesh().isChunkVisible("CF_D1")) {
            this.hierMesh().chunkVisible("BaydoorL_D0", false);
            this.hierMesh().chunkVisible("BaydoorR_D0", false);
            this.hierMesh().chunkVisible("BaydoorL_D1", true);
            this.hierMesh().chunkVisible("BaydoorR_D1", true);
        } else if (this.hierMesh().isChunkVisible("BaydoorL_D1") && this.hierMesh().isChunkVisible("CF_D2")) {
            this.hierMesh().chunkVisible("BaydoorL_D1", false);
            this.hierMesh().chunkVisible("BaydoorR_D1", false);
            this.hierMesh().chunkVisible("BaydoorL_D2", true);
            this.hierMesh().chunkVisible("BaydoorR_D2", true);
        }
        for (int i = 1; i < 5; i++)
            if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    public void update(float f) {
        super.update(f);
        if (this.FM.isPlayers()) {
            if (!this.isChunkAnyDamageVisible("Nose_D") && !this.hierMesh().isChunkVisible("Nose_CAP") && Main3D.cur3D().isViewOutside()) this.hierMesh().chunkVisible("Nose_CAP", true);
            else if (!Main3D.cur3D().isViewOutside()) this.hierMesh().chunkVisible("Nose_CAP", false);
        }
    }

    public boolean typeBomberToggleAutomation() {
        this.bSightAutomation = !this.bSightAutomation;
        this.bSightBombDump = false;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAutomation" + (this.bSightAutomation ? "ON" : "OFF"));
        return this.bSightAutomation;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle++;
        if (this.fSightCurForwardAngle > 85F) this.fSightCurForwardAngle = 85F;
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle--;
        if (this.fSightCurForwardAngle < 0.0F) this.fSightCurForwardAngle = 0.0F;
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip += 0.05F;
        if (this.fSightCurSideslip > 3F) this.fSightCurSideslip = 3F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(this.fSightCurSideslip * 10F) });
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip -= 0.05F;
        if (this.fSightCurSideslip < -3F) this.fSightCurSideslip = -3F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(this.fSightCurSideslip * 10F) });
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 850F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 10F;
        if (this.fSightCurAltitude > 10000F) this.fSightCurAltitude = 10000F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 10F;
        if (this.fSightCurAltitude < 850F) this.fSightCurAltitude = 850F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 150F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 10F;
        if (this.fSightCurSpeed > 600F) this.fSightCurSpeed = 600F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 10F;
        if (this.fSightCurSpeed < 150F) this.fSightCurSpeed = 150F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if (Math.abs(this.FM.Or.getKren()) > 4.5D) {
            this.fSightCurReadyness -= 0.0666666F * f;
            if (this.fSightCurReadyness < 0.0F) this.fSightCurReadyness = 0.0F;
        }
        if (this.fSightCurReadyness < 1.0F) this.fSightCurReadyness += 0.0333333F * f;
        else if (this.bSightAutomation) {
            this.fSightCurDistance -= this.fSightCurSpeed / 3.6F * f;
            if (this.fSightCurDistance < 0.0F) {
                this.fSightCurDistance = 0.0F;
                this.typeBomberToggleAutomation();
            }
            this.fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(this.fSightCurDistance / this.fSightCurAltitude));
            if (this.fSightCurDistance < this.fSightCurSpeed / 3.6F * Math.sqrt(this.fSightCurAltitude * (2F / 9.81F))) this.bSightBombDump = true;
            if (this.bSightBombDump) if (this.FM.isTick(3, 0)) {
                if (this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                    this.FM.CT.WeaponControl[3] = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                }
            } else this.FM.CT.WeaponControl[3] = false;
        }
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte((this.bSightAutomation ? 1 : 0) | (this.bSightBombDump ? 2 : 0));
        netmsgguaranted.writeFloat(this.fSightCurDistance);
        netmsgguaranted.writeByte((int) this.fSightCurForwardAngle);
        netmsgguaranted.writeByte((int) ((this.fSightCurSideslip + 3F) * 33.33333F));
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeByte((int) (this.fSightCurSpeed / 2.5F));
        netmsgguaranted.writeByte((int) (this.fSightCurReadyness * 200F));
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        int i = netmsginput.readUnsignedByte();
        this.bSightAutomation = (i & 1) != 0;
        this.bSightBombDump = (i & 2) != 0;
        this.fSightCurDistance = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readUnsignedByte();
        this.fSightCurSideslip = -3F + netmsginput.readUnsignedByte() / 33.33333F;
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readUnsignedByte() * 2.5F;
        this.fSightCurReadyness = netmsginput.readUnsignedByte() / 200F;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.CT.bHasCockpitDoorControl = true;
        this.FM.CT.dvCockpitDoor = 0.65F;
        this.FM.CT.bHasBayDoorControl = true;
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    protected boolean bombBayDoorsRemoved;
    protected float   flapps[] = { 0.0F, 0.0F };
    public boolean    bSightAutomation;
    private boolean   bSightBombDump;
    public float      fSightCurDistance;
    public float      fSightCurForwardAngle;
    public float      fSightCurSideslip;
    public float      fSightCurAltitude;
    public float      fSightCurSpeed;
    public float      fSightCurReadyness;

    static {
        Class class1 = Beaufort.class;
        Property.set(class1, "originCountry", PaintScheme.countryBritain);
    }
}
