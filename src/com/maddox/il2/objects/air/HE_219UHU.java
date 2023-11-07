package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.rts.Property;

public class HE_219UHU extends HE_219 implements TypeX4Carrier, TypeRadarLiSN2Carrier {
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("MG_FF1", thisWeaponsName.endsWith("R4"));
        hierMesh.chunkVisible("MG_FF2", thisWeaponsName.endsWith("R4"));
        hierMesh.chunkVisible("RadarSN2b", thisWeaponsName.startsWith("SN2b"));
        hierMesh.chunkVisible("RadarSN2c", thisWeaponsName.startsWith("SN2c"));
        hierMesh.chunkVisible("RadarSN2d", thisWeaponsName.startsWith("SN2d"));
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

    private int curPilot  = 1;
    private int radarGain = 50;
    private int radarMode = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    static {
        Class class1 = HE_219UHU.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "He-219");
        Property.set(class1, "meshName", "3DO/Plane/UHU/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar05());
        Property.set(class1, "yearService", 1943F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/He-219.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHE_219.class, CockpitHe_219_Radar.class });
        Property.set(class1, "LOSElevation", 1.00705F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 1, 1, 2, 2, 1, 1, 1, 1, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", "_ExternalDev01", "_ExternalDev02" });
    }
}
