package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.rts.Property;

public class BF_110G4 extends BF_110 implements TypeX4Carrier, TypeRadarLiSN2Carrier {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.startsWith("SN2b")) {
            hierMesh.chunkVisible("Fug1_D0", true);
            hierMesh.chunkVisible("Fug200_D0", true);
            hierMesh.chunkVisible("Fug200d_D0", false);
        }
        else if (thisWeaponsName.startsWith("SN2c")) {
            hierMesh.chunkVisible("Fug1_D0", false);
            hierMesh.chunkVisible("Fug200_D0", true);
            hierMesh.chunkVisible("Fug200d_D0", false);
        }
        else if (thisWeaponsName.startsWith("SN2d")) {
            hierMesh.chunkVisible("Fug1_D0", false);
            hierMesh.chunkVisible("Fug200_D0", false);
            hierMesh.chunkVisible("Fug200d_D0", true);
        } else {
            hierMesh.chunkVisible("Fug1_D0", false);
            hierMesh.chunkVisible("Fug200_D0", false);
            hierMesh.chunkVisible("Fug200d_D0", false);
        }
    }

    protected void nextDMGLevel(String s, int i, Actor actor) {
        super.nextDMGLevel(s, i, actor);
        if (this.FM.isPlayers()) {
            bChangedPit = true;
        }
    }

    protected void nextCUTLevel(String s, int i, Actor actor) {
        super.nextCUTLevel(s, i, actor);
        if (this.FM.isPlayers()) {
            bChangedPit = true;
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            case 0:
                if (f1 < -19F) {
                    f1 = -19F;
                    flag = false;
                }
                if (f1 > 30F) {
                    f1 = 30F;
                    flag = false;
                }
                float f2;
                if (f1 < 0.0F) {
                    f2 = Aircraft.cvt(f1, -19F, 0.0F, 20F, 30F);
                } else if (f1 < 12F) {
                    f2 = Aircraft.cvt(f1, 0.0F, 12F, 30F, 35F);
                } else {
                    f2 = Aircraft.cvt(f1, 12F, 30F, 35F, 40F);
                }
                if (f < 0.0F) {
                    if (f < -f2) {
                        f = -f2;
                        flag = false;
                    }
                } else if (f > f2) {
                    f = f2;
                    flag = false;
                }
                if ((Math.abs(f) > 17.8F) && (Math.abs(f) < 25F) && (f1 < -12F)) {
                    flag = false;
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    // +++ X4Carrier +++
    public void typeX4CAdjSidePlus() {
        this.radarMode++;
        if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
            this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
        }
    }

    public void typeX4CAdjSideMinus() {
        this.radarMode--;
        if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
            this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
        }
    }

    public void typeX4CAdjAttitudePlus() {
        this.radarGain += 10;
        if (this.radarGain > 100) {
            this.radarGain = 100;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        this.radarGain -= 10;
        if (this.radarGain < 0) {
            this.radarGain = 0;
        }
    }

    public void typeX4CResetControls() {
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
    }

    public int getCurPilot() {
        return 1;
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    private int           radarGain   = 50;
    private int           radarMode   = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    public static boolean bChangedPit = false;

    static {
        Class class1 = BF_110G4.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Bf-110");
        Property.set(class1, "meshName", "3DO/Plane/Bf-110G-4/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(class1, "yearService", 1940F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Bf-110G-4.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitBF_110G4.class, CockpitBF_110NJ_RadarOp.class, CockpitBF_110G4_Gunner.class });
        Property.set(class1, "LOSElevation", 0.66895F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 1, 1, 1, 1, 0, 0, 10, 10, 9, 1, 1, 9, 9, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_MGUN05", "_MGUN06", "_ExternalDev03", "_CANNON06", "_CANNON07", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08" });
    }
}
