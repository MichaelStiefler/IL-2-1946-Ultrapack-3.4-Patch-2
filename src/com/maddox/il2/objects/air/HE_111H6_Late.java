package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class HE_111H6_Late extends HE_111 {

    public HE_111H6_Late() {
        this.slider = false;
        this.sliderX = 0.0F;
        this.sliderZ = 0.0F;
        this.pilot2kill = false;
        this.pilot4kill = false;
        this.pilot5kill = false;
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xnose")) {
            if (this.chunkDamageVisible("Nose") < 2) this.hitChunk("Nose", shot);
            if (shot.power > 200000F) {
                this.FM.AS.hitPilot(shot.initiator, 0, World.Rnd().nextInt(3, 192));
                this.FM.AS.hitPilot(shot.initiator, 1, World.Rnd().nextInt(3, 192));
            }
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            if (point3d.x > 4.505D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            else if (point3d.y > 0.0D) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            } else {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            }
        } else if (s.equals("xxengine1arm")) this.getEnergyPastArmor(6F, shot);
        else if (s.equals("xxengine2arm")) this.getEnergyPastArmor(6F, shot);
        else if (s.equals("xxarmoro1")) this.getEnergyPastArmor(8F, shot);
        else if (s.equals("xxarmoro2")) this.getEnergyPastArmor(8F, shot);
        else if (s.equals("xoil1") && shot.power > 0.0F) {
            this.FM.AS.hitOil(shot.initiator, 0);
            if (this.chunkDamageVisible("Engine1") < 2) this.hitChunk("Engine1", shot);
        } else if (s.equals("xoil2") && shot.power > 0.0F) {
            this.FM.AS.hitOil(shot.initiator, 1);
            if (this.chunkDamageVisible("Engine2") < 2) this.hitChunk("Engine2", shot);
        } else if (s.startsWith("xxoil")) {
            int i = 0;
            if (s.endsWith("2")) i = 1;
            if (this.getEnergyPastArmor(0.21F, shot) > 0.0F && World.Rnd().nextFloat() < 0.49F) {
                this.FM.AS.hitOil(shot.initiator, i);
                this.getEnergyPastArmor(0.42F, shot);
            }
        } else super.hitBone(s, shot, point3d);
    }

    public void doRemoveBodyFromPlane(int i) {
        super.doRemoveBodyFromPlane(i);
        switch (i) {
            case 1:
                this.hierMesh().chunkVisible("Pilot1_FAK", false);
                this.hierMesh().chunkVisible("Head1_FAK", false);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot2_FAK", false);
                break;

            case 4:
                this.hierMesh().chunkVisible("Pilot4_FAK", false);
                break;
        }
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.crew = 5;
        this.FM.AS.astatePilotFunctions[0] = 1;
        this.FM.AS.astatePilotFunctions[1] = 9;
        this.FM.AS.astatePilotFunctions[2] = 4;
        this.FM.AS.astatePilotFunctions[3] = 6;
        this.FM.AS.astatePilotFunctions[4] = 5;
        this.FM.CT.bHasCockpitDoorControl = true;
        this.FM.CT.dvCockpitDoor = 0.5F;
        this.gun3 = this.getGunByHookName("_MGUN04");
        this.gun4 = this.getGunByHookName("_MGUN05");
        this.gun2 = this.getGunByHookName("_MGUN03");
        this.gun5 = this.getGunByHookName("_MGUN06");
        if (this.thisWeaponsName.startsWith("internal")) {
           this.FM.M.massEmpty -= 40F;
        } else this.FM.CT.bHasBayDoorControl = false;
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("PVC1006_R", !thisWeaponsName.startsWith("internal"));
    }

    public void rareAction(float f, boolean flag) {
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3) {
                if (World.Rnd().nextFloat() < 0.05F) this.FM.AS.hitTank(this, 0, 1);
                if (World.Rnd().nextFloat() < 0.05F) this.FM.AS.hitTank(this, 1, 1);
            }
            if (this.FM.AS.astateEngineStates[1] > 3) {
                if (World.Rnd().nextFloat() < 0.05F) this.FM.AS.hitTank(this, 2, 1);
                if (World.Rnd().nextFloat() < 0.05F) this.FM.AS.hitTank(this, 3, 1);
            }
            if (this.FM.AS.astateTankStates[0] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 5 && World.Rnd().nextFloat() < 0.125F) this.FM.AS.hitTank(this, 2, 1);
            if (this.FM.AS.astateTankStates[2] > 5 && World.Rnd().nextFloat() < 0.125F) this.FM.AS.hitTank(this, 1, 1);
            if (this.FM.AS.astateTankStates[2] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
        }
        Actor actor = War.GetNearestEnemy(this, 16, 6000F);
        Aircraft aircraft = War.getNearestEnemy(this, 5000F);
        if ((actor != null && !(actor instanceof BridgeSegment) || aircraft != null) && this.FM.CT.getCockpitDoor() < 0.01F) this.FM.AS.setCockpitDoor(this, 1);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
            this.hierMesh().chunkVisible("HMask2_D0", false);
            this.hierMesh().chunkVisible("HMask3_D0", false);
            this.hierMesh().chunkVisible("HMask4_D0", false);
            this.hierMesh().chunkVisible("HMask5_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_FAK"));
            this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_FAK"));
            this.hierMesh().chunkVisible("HMask3_D0", this.hierMesh().isChunkVisible("Pilot3_D0"));
            this.hierMesh().chunkVisible("HMask4_D0", this.hierMesh().isChunkVisible("Pilot4_FAK"));
            this.hierMesh().chunkVisible("HMask5_D0", this.hierMesh().isChunkVisible("Pilot5_D0"));
        }
    }

    public void update(float f) {
        if (this.slider) {
            if (this.sliderX < 0.9F) this.sliderX = this.sliderX + 0.01F;
            if (this.sliderZ < 0.05F) this.sliderZ = this.sliderZ + 0.005F;
            Aircraft.xyz[0] = 0.0F;
            Aircraft.xyz[1] = this.sliderX;
            Aircraft.xyz[2] = this.sliderZ;
            this.hierMesh().chunkSetLocate("Window_D0", Aircraft.xyz, Aircraft.ypr);
        }
        if (this.FM.AS.astatePlayerIndex == 1) {
            if (Main3D.cur3D().cockpitCur == Main3D.cur3D().cockpits[1]) this.SturmanBusy(1);
            else if (Main3D.cur3D().cockpitCur == Main3D.cur3D().cockpits[2]) this.SturmanBusy(2);
        } else if (Time.current() > this.tme3) {
            this.tme3 = Time.current() + 100L;
            if (this.FM.turret.length != 0 && !this.pilot2kill) this.FM.turret[0].bIsOperable = true;
        }
        if (this.FM.AS.astatePlayerIndex == 3) {
            if (Main3D.cur3D().cockpitCur == Main3D.cur3D().cockpits[4]) this.SturmanBusy(4);
            else if (Main3D.cur3D().cockpitCur == Main3D.cur3D().cockpits[5]) this.SturmanBusy(5);
        } else if (Time.current() > this.tme2) {
            this.tme2 = Time.current() + 1510L;
            if (this.FM.turret.length != 0) {
                Actor actor = null;
                actor = this.FM.turret[2].target;
                if (actor == null) actor = this.FM.turret[5].target;
                if (actor != null) if (Actor.isValid(actor)) {
                    this.pos.getAbs(Aircraft.tmpLoc2);
                    actor.pos.getAbs(Aircraft.tmpLoc3);
                    Aircraft.tmpLoc2.transformInv(Aircraft.tmpLoc3.getPoint());
                    if (Aircraft.tmpLoc3.getPoint().x > 0.0D) this.SturmanBusy(5);
                    else this.SturmanBusy(4);
                } else if (!this.pilot4kill) {
                    this.FM.turret[2].bIsOperable = true;
                    this.FM.turret[5].bIsOperable = true;
                }
            }
        }
        if (this.FM.AS.astatePlayerIndex == 4) {
            if (Main3D.cur3D().cockpitCur == Main3D.cur3D().cockpits[7]) this.SturmanBusy(7);
            else if (Main3D.cur3D().cockpitCur == Main3D.cur3D().cockpits[6]) this.SturmanBusy(6);
        } else if (Time.current() > this.tme) {
            this.tme = Time.current() + 1500L;
            if (this.FM.turret.length != 0) {
                Actor actor1 = null;
                actor1 = this.FM.turret[3].target;
                if (actor1 == null) actor1 = this.FM.turret[4].target;
                if (actor1 != null) if (Actor.isValid(actor1)) {
                    this.pos.getAbs(Aircraft.tmpLoc2);
                    actor1.pos.getAbs(Aircraft.tmpLoc3);
                    Aircraft.tmpLoc2.transformInv(Aircraft.tmpLoc3.getPoint());
                    if (Aircraft.tmpLoc3.getPoint().y < 0.0D) this.SturmanBusy(7);
                    else this.SturmanBusy(6);
                } else if (!this.pilot5kill) {
                    this.FM.turret[3].bIsOperable = true;
                    this.FM.turret[4].bIsOperable = true;
                }
            }
        }
        if (this.FM.turret[6].tMode == 2) this.FM.turret[6].tMode = 4;
        super.update(f);
    }

    private void SturmanBusy(int i) {
        switch (i) {
            case 3:
            default:
                break;

            case 1:
                if (!this.pilot2kill) this.FM.turret[0].bIsOperable = false;
                break;

            case 2:
                if (!this.pilot2kill) this.FM.turret[0].bIsOperable = true;
                break;

            case 4:
                if (this.pilot4kill) break;
                this.FM.turret[2].bIsOperable = true;
                this.FM.turret[5].bIsOperable = false;
                if (this.FM.turret.length != 0 && World.cur().diffCur.Limited_Ammo) {
                    this.gunmax = Math.max(this.gun2.countBullets(), this.gun5.countBullets());
                    this.gunmin = 0;
                    this.gun2.loadBullets(this.gunmax);
                    this.gun5.loadBullets(this.gunmin);
                }
                break;

            case 5:
                if (this.pilot4kill) break;
                this.FM.turret[2].bIsOperable = false;
                this.FM.turret[5].bIsOperable = true;
                if (this.FM.turret.length != 0 && World.cur().diffCur.Limited_Ammo) {
                    this.gunmax = Math.max(this.gun2.countBullets(), this.gun5.countBullets());
                    this.gunmin = 0;
                    this.gun5.loadBullets(this.gunmax);
                    this.gun2.loadBullets(this.gunmin);
                }
                break;

            case 6:
                if (this.pilot5kill) break;
                this.FM.turret[3].bIsOperable = true;
                this.FM.turret[4].bIsOperable = false;
                this.hierMesh().chunkSetAngles("Pilot5_D0", -180F, 0.0F, 0.0F);
                if (this.FM.turret.length != 0 && World.cur().diffCur.Limited_Ammo) {
                    this.gunmax = Math.max(this.gun3.countBullets(), this.gun4.countBullets());
                    this.gunmin = 0;
                    this.gun3.loadBullets(this.gunmax);
                    this.gun4.loadBullets(this.gunmin);
                }
                break;

            case 7:
                if (this.pilot5kill) break;
                this.FM.turret[3].bIsOperable = false;
                this.FM.turret[4].bIsOperable = true;
                this.hierMesh().chunkSetAngles("Pilot5_D0", 0.0F, 0.0F, 0.0F);
                if (this.FM.turret.length != 0 && World.cur().diffCur.Limited_Ammo) {
                    this.gunmax = Math.max(this.gun3.countBullets(), this.gun4.countBullets());
                    this.gunmin = 0;
                    this.gun4.loadBullets(this.gunmax);
                    this.gun3.loadBullets(this.gunmin);
                }
                break;
        }
    }

    public void moveCockpitDoor(float f) {
        if (f > 0.199F) {
            Aircraft.xyz[0] = 0.0F;
            Aircraft.ypr[0] = 0.0F;
            Aircraft.xyz[1] = 0.0F;
            Aircraft.ypr[1] = 0.0F;
            Aircraft.xyz[2] = 0.0F;
            Aircraft.ypr[2] = 0.0F;
            Aircraft.xyz[1] = (f - 0.199F) * -0.8125F;
            this.hierMesh().chunkSetLocate("Kolpak_D0", Aircraft.xyz, Aircraft.ypr);
        }
        if (f > 0.126F) this.hierMesh().chunkVisible("Kolpak2_D0", false);
        else this.hierMesh().chunkVisible("Kolpak2_D0", true);
        if (f < 0.199F) this.hierMesh().chunkSetAngles("Kolpak3_D0", 0.0F, 0.0F, -315F * f);
    }

    public void hitDaSilk() {
        if (this.FM.AS.astatePilotStates[0] < 95 && !this.slider) this.slider = true;
        if (this.FM.AS.astatePilotStates[2] < 95 && this.FM.CT.getCockpitDoor() < 0.01F) this.FM.AS.setCockpitDoor(this, 1);
        super.hitDaSilk();
    }

    public boolean turretAngles(int i, float af[]) {
        for (int j = 0; j < 2; j++) {
            af[j] = (af[j] + 3600F) % 360F;
            if (af[j] > 180F) af[j] -= 360F;
        }

        af[2] = 0.0F;
        boolean flag = true;
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f < -25F) {
                    f = -25F;
                    flag = false;
                }
                if (f > 15F) {
                    f = 15F;
                    flag = false;
                }
                if (f1 < -40F) {
                    f1 = -40F;
                    flag = false;
                }
                if (f1 > 0.0F) {
                    f1 = 0.0F;
                    flag = false;
                }
                break;

            case 1:
                float f2 = this.FM.CT.getCockpitDoor();
                if (f2 >= 0.99F) {
                    if (f < -75F) {
                        f = -75F;
                        flag = false;
                    }
                    if (f > 75F) {
                        f = 75F;
                        flag = false;
                    }
                    if (f1 < -3F) {
                        f1 = -3F;
                        flag = false;
                    }
                    if (f1 > 63F) {
                        f1 = 63F;
                        flag = false;
                    }
                } else {
                    f = 0.0F;
                    f1 = 0.0F;
                    flag = false;
                }
                break;

            case 2:
                if (f < -35F) {
                    f = -35F;
                    flag = false;
                }
                if (f > 40F) {
                    f = 40F;
                    flag = false;
                }
                if (f1 < -30F) {
                    f1 = -30F;
                    flag = false;
                }
                if (f1 > 46F) {
                    f1 = 46F;
                    flag = false;
                }
                break;

            case 3:
                if (f < -55F) {
                    f = -55F;
                    flag = false;
                }
                if (f > 23F) {
                    f = 23F;
                    flag = false;
                }
                if (f1 < -30F) {
                    f1 = -30F;
                    flag = false;
                }
                if (f1 > 45F) {
                    f1 = 45F;
                    flag = false;
                }
                if (f1 > 55F + 0.6F * f) {
                    f1 = 55F + 0.6F * f;
                    flag = false;
                }
                break;

            case 4:
                if (f < -23F) {
                    f = -23F;
                    flag = false;
                }
                if (f > 55F) {
                    f = 55F;
                    flag = false;
                }
                if (f1 < -30F) {
                    f1 = -30F;
                    flag = false;
                }
                if (f1 > 45F) {
                    f1 = 45F;
                    flag = false;
                }
                if (f1 > 55F - 0.6F * f) {
                    f1 = 55F - 0.6F * f;
                    flag = false;
                }
                break;

            case 5:
                if (f < -40F) {
                    f = -40F;
                    flag = false;
                }
                if (f > 40F) {
                    f = 40F;
                    flag = false;
                }
                if (f1 < -25F) {
                    f1 = -25F;
                    flag = false;
                }
                if (f1 > 40F) {
                    f1 = 40F;
                    flag = false;
                }
                if (f1 <= 15F) break;
                if (f > -1.12F * f1 + 56.8F) {
                    f = -1.12F * f1 + 56.8F;
                    flag = false;
                    break;
                }
                if (f < 1.12F * f1 - 56.8F) {
                    f = 1.12F * f1 - 56.8F;
                    flag = false;
                }
                break;

            case 6:
                f = 0.0F;
                f1 = 0.0F;
                flag = false;
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 1:
                this.FM.turret[0].setHealth(f);
                break;

            case 2:
                this.FM.turret[1].setHealth(f);
                break;

            case 3:
                this.FM.turret[2].setHealth(f);
                this.FM.turret[5].setHealth(f);
                break;

            case 4:
                this.FM.turret[3].setHealth(f);
                this.FM.turret[4].setHealth(f);
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                if (this.bPitUnfocused && !this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot1_FAK", false);
                    this.hierMesh().chunkVisible("Pilot1_FAL", true);
                    this.hierMesh().chunkVisible("Head1_FAK", false);
                }
                if (this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot1_FAK", false);
                    this.hierMesh().chunkVisible("Pilot1_FAL", false);
                    this.hierMesh().chunkVisible("Head1_FAK", false);
                }
                if (this.hierMesh().isChunkVisible("Nose_D0") || this.hierMesh().isChunkVisible("Nose_D1") || this.hierMesh().isChunkVisible("Nose_D2")) this.hierMesh().chunkVisible("Gore1_D0", true);
                if (this.FM.AS.astatePilotStates[2] > 95) this.FM.turret[6].bIsOperable = false;
                break;

            case 1:
                this.pilot2kill = true;
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                if (this.bPitUnfocused && !this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot2_FAK", false);
                    this.hierMesh().chunkVisible("Pilot2_FAL", true);
                    this.hierMesh().chunkVisible("HMask2_D0", false);
                }
                if (this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot2_FAK", false);
                    this.hierMesh().chunkVisible("Pilot2_FAL", false);
                    this.hierMesh().chunkVisible("HMask2_D0", false);
                }
                if (this.hierMesh().isChunkVisible("Nose_D0") || this.hierMesh().isChunkVisible("Nose_D1") || this.hierMesh().isChunkVisible("Nose_D2")) this.hierMesh().chunkVisible("Gore2_D0", true);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.hierMesh().chunkVisible("HMask3_D0", false);
                this.hierMesh().chunkVisible("Gore3_D0", true);
                if (this.FM.AS.astatePilotStates[0] > 95) this.FM.turret[6].bIsOperable = false;
                break;

            case 3:
                this.pilot4kill = true;
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                this.hierMesh().chunkVisible("Pilot4_D1", true);
                this.hierMesh().chunkVisible("HMask4_D0", false);
                if (this.bPitUnfocused && !this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot4_FAK", false);
                    this.hierMesh().chunkVisible("Pilot4_FAL", true);
                    this.hierMesh().chunkVisible("HMask4_D0", false);
                }
                if (this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Pilot4_FAK", false);
                    this.hierMesh().chunkVisible("Pilot4_FAL", false);
                    this.hierMesh().chunkVisible("HMask4_D0", false);
                }
                break;

            case 4:
                this.pilot5kill = true;
                this.hierMesh().chunkVisible("Pilot5_D0", false);
                this.hierMesh().chunkVisible("Pilot5_D1", true);
                this.hierMesh().chunkVisible("HMask5_D0", false);
                break;
        }
    }

    protected void moveBayDoor(float f) {
        this.hierMesh().chunkSetAngles("Bay9_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay10_D0", 0.0F, 94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay11_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay12_D0", 0.0F, 94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay13_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay14_D0", 0.0F, 94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay15_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay16_D0", 0.0F, 94F * f, 0.0F);
    }

    private boolean slider;
    private float   sliderX;
    private float   sliderZ;
    private long    tme;
    private long    tme2;
    private long    tme3;
    private boolean pilot2kill;
    private boolean pilot4kill;
    private boolean pilot5kill;
    private int     gunmax;
    private int     gunmin;
    private Gun     gun3;
    private Gun     gun4;
    private Gun     gun2;
    private Gun     gun5;

    static {
        Class class1 = HE_111H6_Late.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "He-111");
        Property.set(class1, "meshName", "3do/plane/He-111H-6-Late/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1941.7F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/He-111H-6-Late.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHE_111H6_Late.class, CockpitHE_111H6_Late_Bombardier.class, CockpitHE_111H6_Late_NGunner.class, CockpitHE_111H6_Early_TGunner.class, CockpitHE_111H6_Early_BGunner.class,
                CockpitHE_111H6_Early_FGunner.class, CockpitHE_111H6_Early_LGunner.class, CockpitHE_111H6_Early_RGunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 12, 13, 14, 15, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_BombSpawn01", "_BombSpawn02",
                "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06", "_BombSpawn07", "_BombSpawn08" });
    }
}
