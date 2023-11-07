package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.il2.objects.weapons.MGunBK5_AP;
import com.maddox.il2.objects.weapons.MGunBK5_HE;
import com.maddox.il2.objects.weapons.MGunMK214A;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class ME_410B extends ME_210 implements TypeBNZFighter, TypeStormovik, TypeStormovikArmored, TypeX4Carrier, TypeRadarLiSN2Carrier, TypeRadarFuG200Carrier {

    public ME_410B() {
        this.bToFire = false;
        this.tX4Prev = 0L;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
        this.phase = 0;
        this.disp = 0.0F;
        this.oldbullets = 0;
        this.g1 = null;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        if (this.FM.CT.Weapons[1] != null) {
            this.g1 = this.FM.CT.Weapons[1][0];
        }
        if (thisWeaponsName.toLowerCase().startsWith("nj")) {
            this.FM.M.massEmpty += 60F;
        }
        if (thisWeaponsName.startsWith("SEA")) {
            this.FM.M.massEmpty += 30F;
        }
        if (thisWeaponsName.startsWith("U4") || thisWeaponsName.startsWith("ZFR4_U4")) {
            this.FM.M.massEmpty += 540F;
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.endsWith("DTK")) {
            hierMesh.chunkVisible("Rack_L", true);
            hierMesh.chunkVisible("Rack_R", true);
        } else {
            hierMesh.chunkVisible("Rack_L", false);
            hierMesh.chunkVisible("Rack_R", false);
        }
        if (thisWeaponsName.toLowerCase().startsWith("nj")) {
            hierMesh.chunkVisible("RadarOp_D0", true);
            hierMesh.chunkVisible("Radar_Set_D0", true);
            hierMesh.chunkVisible("Pilot2_D0", false);
            hierMesh.chunkVisible("HMask2_D0", false);
            String thisWeaponsNamePart = thisWeaponsName.substring(thisWeaponsName.toLowerCase().startsWith("nj_smuzik")?10:3);
            if (thisWeaponsNamePart.startsWith("SN2b")) {
                hierMesh.chunkVisible("Fug1_D0", true);
                hierMesh.chunkVisible("Fug200_D0", true);
                hierMesh.chunkVisible("Fug200d_D0", false);
            } else if (thisWeaponsNamePart.startsWith("SN2c")) {
                hierMesh.chunkVisible("Fug1_D0", false);
                hierMesh.chunkVisible("Fug200_D0", true);
                hierMesh.chunkVisible("Fug200d_D0", false);
            } else if (thisWeaponsNamePart.startsWith("SN2d")) {
                hierMesh.chunkVisible("Fug1_D0", false);
                hierMesh.chunkVisible("Fug200_D0", false);
                hierMesh.chunkVisible("Fug200d_D0", true);
            } else {
                hierMesh.chunkVisible("Fug1_D0", false);
                hierMesh.chunkVisible("Fug200_D0", false);
                hierMesh.chunkVisible("Fug200d_D0", false);
            }
        } else {
            hierMesh.chunkVisible("Fug1_D0", false);
            hierMesh.chunkVisible("Fug200_D0", false);
            hierMesh.chunkVisible("Fug200d_D0", false);
            hierMesh.chunkVisible("RadarOp_D0", false);
            hierMesh.chunkVisible("Radar_Set_D0", false);
            hierMesh.chunkVisible("Pilot2_D0", true);
            hierMesh.chunkVisible("HMask2_D0", true);
        }
        if (thisWeaponsName.toLowerCase().startsWith("nj_smuzik")) {
            hierMesh.chunkVisible("Turret1A_D0", false);
            hierMesh.chunkVisible("Turret1B_D0", false);
            hierMesh.chunkVisible("Turret2A_D0", false);
            hierMesh.chunkVisible("Turret2B_D0", false);
        } else {
            hierMesh.chunkVisible("Turret1A_D0", true);
            hierMesh.chunkVisible("Turret1B_D0", true);
            hierMesh.chunkVisible("Turret2A_D0", true);
            hierMesh.chunkVisible("Turret2B_D0", true);
        }
        if (thisWeaponsName.startsWith("SEA")) {
            hierMesh.chunkVisible("Fug1C_D0", true);
            hierMesh.chunkVisible("Fug1L_D0", true);
            hierMesh.chunkVisible("Fug1R_D0", true);
        } else {
            hierMesh.chunkVisible("Fug1C_D0", false);
            hierMesh.chunkVisible("Fug1L_D0", false);
            hierMesh.chunkVisible("Fug1R_D0", false);
        }
        if (thisWeaponsName.startsWith("ZFR4")) {
            hierMesh.chunkVisible("Telescope", true);
        } else {
            hierMesh.chunkVisible("Telescope", false);
        }
        if (thisWeaponsName.startsWith("U4") || thisWeaponsName.startsWith("ZFR4_U4")) {
            hierMesh.chunkVisible("BK5_BARREL", true);
        } else {
            hierMesh.chunkVisible("BK5_BARREL", false);
        }
    }
    
    public void update(float paramFloat) {
        if ((this.g1 != null) && (this.oldbullets != this.g1.countBullets()) && ((this.getGunByHookName("_CANNON03") instanceof MGunBK5_AP) || (this.getGunByHookName("_CANNON03") instanceof MGunBK5_HE))) {
            switch (this.phase) {
                default:
                    break;

                case 0:
                    if (this.g1.isShots()) {
                        this.oldbullets = this.g1.countBullets();
                        this.phase = 1;
                        this.disp = 0.0F;
                    }
                    break;

                case 1:
                    this.disp += 12.6F * paramFloat;
                    this.resetYPRmodifier();
                    Aircraft.xyz[0] = this.disp;
                    this.hierMesh().chunkSetLocate("BK5_BARREL", Aircraft.xyz, Aircraft.ypr);
                    if (this.disp >= 0.7F) {
                        this.phase = 2;
                    }
                    break;

                case 2:
                    this.disp -= 1.2F * paramFloat;
                    this.resetYPRmodifier();
                    Aircraft.xyz[0] = this.disp;
                    this.hierMesh().chunkSetLocate("BK5_BARREL", Aircraft.xyz, Aircraft.ypr);
                    if (this.disp <= 0.0F) {
                        this.phase = 3;
                    }
                    break;

                case 3:
                    this.phase = 0;
                    break;
            }
        }
        if ((this.g1 != null) && (this.oldbullets != this.g1.countBullets()) && (this.getGunByHookName("_CANNON03") instanceof MGunMK214A)) {
            switch (this.phase) {
                default:
                    break;

                case 0:
                    if (this.g1.isShots()) {
                        this.oldbullets = this.g1.countBullets();
                        this.phase = 1;
                        this.disp = 0.0F;
                    }
                    break;

                case 1:
                    this.disp += 31.6F * paramFloat;
                    this.resetYPRmodifier();
                    Aircraft.xyz[0] = this.disp;
                    this.hierMesh().chunkSetLocate("BK5_BARREL", Aircraft.xyz, Aircraft.ypr);
                    if (this.disp >= 0.7F) {
                        this.phase = 2;
                    }
                    break;

                case 2:
                    this.disp -= 5.2F * paramFloat;
                    this.resetYPRmodifier();
                    Aircraft.xyz[0] = this.disp;
                    this.hierMesh().chunkSetLocate("BK5_BARREL", Aircraft.xyz, Aircraft.ypr);
                    if (this.disp <= 0.0F) {
                        this.phase = 3;
                    }
                    break;

                case 3:
                    this.phase = 0;
                    break;
            }
        }
        super.update(paramFloat);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if ((!(super.FM instanceof RealFlightModel) || !((RealFlightModel) super.FM).isRealMode()) && flag && (super.FM instanceof Pilot)) {
            Pilot pilot = (Pilot) super.FM;
            if ((pilot.get_maneuver() == Maneuver.ATTACK_BOMBER || (pilot.get_maneuver() == Maneuver.ATTACK && pilot.isMissileValidForFighterAttack())) && pilot.target != null) {
                Point3d point3d = new Point3d(((Maneuver) (pilot)).target.Loc);
                point3d.sub(this.FM.Loc);
                this.FM.Or.transformInv(point3d);
                if ((((point3d.x > 4000D) && (point3d.x < 5500D)) || ((point3d.x > 100D) && (point3d.x < 5000D) && (World.Rnd().nextFloat() < 0.33F))) && (Time.current() > (this.tX4Prev + 10000L))) {
                    this.bToFire = true;
                    this.tX4Prev = Time.current();
                }
            }
        }
    }

    // +++ X4Carrier +++
    public void typeX4CAdjSidePlus() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = 1.0F;
            return;
        }
        this.radarMode++;
        if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
            this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
        }
    }

    public void typeX4CAdjSideMinus() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = -1F;
            return;
        }
        this.radarMode--;
        if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
            this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
        }
    }

    public void typeX4CAdjAttitudePlus() {
        if (this.curPilot == 1) {
            this.deltaTangage = 1.0F;
            return;
        }
        this.radarGain += 10;
        if (this.radarGain > 100) {
            this.radarGain = 100;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        if (this.curPilot == 1) {
            this.deltaTangage = -1F;
            return;
        }
        this.radarGain -= 10;
        if (this.radarGain < 0) {
            this.radarGain = 0;
        }
    }

    public void typeX4CResetControls() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = this.deltaTangage = 0.0F;
            return;
        }
        this.radarGain = 50;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    private float deltaAzimuth = 0.0F;
    private float deltaTangage = 0.0F;
    // +++ X4Carrier +++

    // +++ RadarLiSN2Carrier +++
    public void setCurPilot(int theCurPilot) {
        this.curPilot = theCurPilot;
    }

    public int getCurPilot() {
        return this.curPilot;
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    protected void nextDMGLevel(String s, int i, Actor actor) {
        super.nextDMGLevel(s, i, actor);
        if (this.FM.isPlayers()) bChangedPit = true;
    }

    protected void nextCUTLevel(String s, int i, Actor actor) {
        super.nextCUTLevel(s, i, actor);
        if (this.FM.isPlayers()) bChangedPit = true;
    }

    private int           curPilot  = 1;
    private int           radarGain = 50;
    private int           radarMode = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    private int           phase;
    private float         disp;
    private int           oldbullets;
    private BulletEmitter g1;
    public boolean        bToFire;
    private long          tX4Prev;
    public static boolean bChangedPit = false;

    static {
        Class class1 = ME_410B.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Me-410");
        Property.set(class1, "meshName", "3DO/Plane/ME-410-B/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1943F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Me-410B.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitME_410B.class, CockpitMe_410_Tubesight.class, CockpitMe_410_Radar.class, CockpitMe_410_Gunner.class });
        Property.set(class1, "LOSElevation", 0.66895F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 1, 1, 10, 10, 3, 3, 3, 9, 1, 9, 1, 9, 1, 9, 9, 9, 1, 1, 9, 1, 1, 9, 1, 1, 9, 9, 9, 9, 9, 3, 3, 3, 3, 3, 9, 9, 3, 3, 2, 2, 2, 2, 9, 9, 9, 9, 2, 2, 2, 2, 9, 9, 1, 1, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_MGUN03", "_MGUN04", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_ExternalDev03", "_CANNON03", "_ExternalDev04", "_CANNON04", "_ExternalDev05", "_CANNON05", "_ExternalDev08", "_ExternalDev09", "_ExternalDev10", "_CANNON08", "_CANNON09", "_ExternalDev11", "_CANNON10", "_CANNON11", "_ExternalDev12", "_CANNON12", "_CANNON13", "_ExternalDev13", "_ExternalDev14", "_ExternalDev15", "_ExternalDev16", "_ExternalDev17", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalDev19", "_ExternalDev20", "_ExternalBomb07", "_ExternalBomb08", "_ExternalRock07", "_ExternalRock07", "_ExternalRock08", "_ExternalRock08", "_ExternalDev21", "_ExternalDev22", "_ExternalDev23", "_ExternalDev24", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalDev25", "_ExternalDev26", "_CANNON14", "_CANNON15", "_ExternalDev27", "_ExternalDev28" });

    }
}
