package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class P1Y1 extends P1Y implements TypeBomber, TypeDiveBomber {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.bombBayDoorsRemoved = this.thisWeaponsName.startsWith("1xtyp91");
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Bay1_D0", !thisWeaponsName.startsWith("1xtyp91"));
        hierMesh.chunkVisible("Bay2_D0", !thisWeaponsName.startsWith("1xtyp91"));
        hierMesh.chunkVisible("Bay3_D0", !thisWeaponsName.startsWith("1xtyp91"));
        hierMesh.chunkVisible("Bay4_D0", !thisWeaponsName.startsWith("1xtyp91"));
    }
    
    public boolean needsOpenBombBay() {
        return !this.bombBayDoorsRemoved;
    }

    public boolean canOpenBombBay() {
        return !this.bombBayDoorsRemoved;
    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
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
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3) {
                if (this.FM.AS.astateTankStates[1] < 4 && World.Rnd().nextFloat() < 0.025F) this.FM.AS.hitTank(this, 1, 1);
                if (this.FM.getSpeedKMH() > 200F && World.Rnd().nextFloat() < 0.025F) this.nextDMGLevel("Keel1_D0", 0, this);
                if (this.FM.getSpeedKMH() > 200F && World.Rnd().nextFloat() < 0.025F) this.nextDMGLevel("StabL_D0", 0, this);
                if (World.Rnd().nextFloat() < 0.25F) this.nextDMGLevel("WingLIn_D0", 0, this);
            }
            if (this.FM.AS.astateEngineStates[1] > 3) {
                if (this.FM.AS.astateTankStates[2] < 4 && World.Rnd().nextFloat() < 0.025F) this.FM.AS.hitTank(this, 2, 1);
                if (this.FM.getSpeedKMH() > 200F && World.Rnd().nextFloat() < 0.025F) this.nextDMGLevel("Keel2_D0", 0, this);
                if (this.FM.getSpeedKMH() > 200F && World.Rnd().nextFloat() < 0.025F) this.nextDMGLevel("StabR_D0", 0, this);
                if (World.Rnd().nextFloat() < 0.25F) this.nextDMGLevel("WingRIn_D0", 0, this);
            }
        }
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
            this.hierMesh().chunkVisible("HMask2_D0", false);
            this.hierMesh().chunkVisible("HMask3_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
            this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_D0"));
            this.hierMesh().chunkVisible("HMask3_D0", this.hierMesh().isChunkVisible("Pilot3_D0"));
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
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
                if (f1 > 25F) {
                    f1 = 25F;
                    flag = false;
                }
                break;

            case 1:
                if (f < -33F) {
                    f = -33F;
                    flag = false;
                }
                if (f > 33F) {
                    f = 33F;
                    flag = false;
                }
                if (f1 < 62F) {
                    f1 = 62F;
                    flag = false;
                }
                if (f1 > -3.0F) {
                    f1 = -3.0F;
                    flag = false;
                }
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
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xxeng1")) {
            this.hitBoneEngine(s, shot, point3d, 0);
            return;
        }
        if (s.startsWith("xxeng2")) {
            this.hitBoneEngine(s, shot, point3d, 1);
            return;
        } else {
            super.hitBone(s, shot, point3d);
            return;
        }
    }

    protected void hitBoneEngine(String s, Shot shot, Point3d point3d, int i) {
        if (s.endsWith("prop") && this.getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) this.FM.EI.engines[i].setKillPropAngleDevice(shot.initiator);
        if (s.endsWith("base")) {
            if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                if (World.Rnd().nextFloat() < shot.power / 140000F) {
                    this.FM.AS.setEngineStuck(shot.initiator, i);
                    Aircraft.debugprintln(this, "*** Engine Crank Case Hit - Engine Stucks..");
                }
                if (World.Rnd().nextFloat() < shot.power / 85000F) {
                    this.FM.AS.hitEngine(shot.initiator, i, 2);
                    Aircraft.debugprintln(this, "*** Engine Crank Case Hit - Engine Damaged..");
                }
            } else if (World.Rnd().nextFloat() < 0.01F) this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, 1);
            else {
                this.FM.EI.engines[i].setReadyness(shot.initiator, this.FM.EI.engines[i].getReadyness() - 0.002F);
                Aircraft.debugprintln(this, "*** Engine Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[i].getReadyness() + "..");
            }
            this.getEnergyPastArmor(12F, shot);
        }
        if (s.endsWith("cyls")) {
            if (this.getEnergyPastArmor(6.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[i].getCylindersRatio() * 0.75F) {
                this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                Aircraft.debugprintln(this, "*** Engine Cylinders Hit, " + this.FM.EI.engines[i].getCylindersOperable() + "/" + this.FM.EI.engines[i].getCylinders() + " Left..");
                if (World.Rnd().nextFloat() < shot.power / 48000F) {
                    this.FM.AS.hitEngine(shot.initiator, i, 2);
                    Aircraft.debugprintln(this, "*** Engine Cylinders Hit - Engine Fires..");
                }
            }
            this.getEnergyPastArmor(25F, shot);
        }
        if (s.endsWith("supc")) {
            if (this.getEnergyPastArmor(0.05F, shot) > 0.0F) this.FM.EI.engines[i].setKillCompressor(shot.initiator);
            this.getEnergyPastArmor(2.0F, shot);
        }
        if (s.endsWith("eqpt")) {
            if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                if (Aircraft.Pd.y > 0.0D && Aircraft.Pd.z < 0.189D && World.Rnd().nextFloat(0.0F, 16000F) < shot.power) this.FM.EI.engines[i].setMagnetoKnockOut(shot.initiator, 0);
                if (Aircraft.Pd.y < 0.0D && Aircraft.Pd.z < 0.189D && World.Rnd().nextFloat(0.0F, 16000F) < shot.power) this.FM.EI.engines[i].setMagnetoKnockOut(shot.initiator, 1);
                if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 4);
                if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 0);
                if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 6);
                if (World.Rnd().nextFloat(0.0F, 26700F) < shot.power) this.FM.AS.setEngineSpecificDamage(shot.initiator, i, 1);
            }
            this.getEnergyPastArmor(2.0F, shot);
        }
    }

    protected boolean bombBayDoorsRemoved = false;

    static {
        Class class1 = P1Y1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "P1Y1");
        Property.set(class1, "meshName", "3DO/Plane/P1Y1/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar04());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/P1Y1.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitP1Y1.class, CockpitP1Y1_Bombardier.class, CockpitP1Y1_NGunner.class, CockpitP1Y1_Gunner.class });
        Property.set(class1, "LOSElevation", 0.76315F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04" });
    }
}
