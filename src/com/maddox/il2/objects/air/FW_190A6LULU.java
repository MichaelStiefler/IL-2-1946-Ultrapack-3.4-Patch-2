package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.rts.Property;

public class FW_190A6LULU extends FW_190NEW implements TypeRadarLiSN2Carrier, TypeX4Carrier {
    
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        FW_190A6LULU.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        FW_190NEW.prepareWeapons(aircraftClass, hierMesh, thisWeaponsName);
        _WeaponSlot[] weaponSlotsRegistered = Aircraft.getWeaponSlotsRegistered(aircraftClass, thisWeaponsName);
        if ((weaponSlotsRegistered == null) || (weaponSlotsRegistered.length < 27)) {
            return;
        }
        hierMesh.chunkVisible("FuG125L_D0", weaponSlotsRegistered[25] != null);
        hierMesh.chunkVisible("FuG125R_D0", weaponSlotsRegistered[25] != null);
        hierMesh.chunkVisible("FuG1251_D0", weaponSlotsRegistered[25] != null);
        hierMesh.chunkVisible("FuG1252_D0", weaponSlotsRegistered[25] != null);
        hierMesh.chunkVisible("Deflect_D0", weaponSlotsRegistered[25] != null);
        hierMesh.chunkVisible("FuG217L_D0", weaponSlotsRegistered[25] == null);
        hierMesh.chunkVisible("FuG217R_D0", weaponSlotsRegistered[25] == null);
    }

    // +++ RadarLiSN2Carrier +++
    public void setCurPilot(int theCurPilot) {
        System.out.println("### Attempt to set Pilot Index on single crew plane!!! ###");
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

    public float typeX4CgetdeltaAzimuth() {
        return 0.0F;
    }

    public float typeX4CgetdeltaTangage() {
        return 0.0F;
    }

    private int                    radarGain       = 50;
    private int                    radarMode       = RadarLiSN2.RADAR_MODE_NORMAL;
    // --- RadarLiSN2Carrier ---

    static {
        Class class1 = FW_190A6LULU.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "FW190");
        Property.set(class1, "meshName", "3do/plane/Fw-190A-6R8/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "yearService", 1943.1F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "cockpitClass", new Class[] { CockpitFW_190A6LULU.class });
        Property.set(class1, "FlightModel", "FlightModels/Fw-190A-6-165 (Ultrapack).fmd");
        Property.set(class1, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0, 1, 1, 9, 9, 9, 9, 9, 9, 2, 2, 9, 9, 3, 3, 3, 3, 9, 9, 3, 9, 9, 1, 1 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08", "_ExternalRock01", "_ExternalRock02", "_ExternalDev09", "_ExternalDev10", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb05", "_ExternalDev11", "_ExternalDev12", "_CANNON05", "_CANNON06" });
    }
}
