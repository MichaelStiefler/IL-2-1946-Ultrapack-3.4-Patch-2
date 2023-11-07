package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.rts.Property;

public class JU_88G6 extends JU_88NEW implements TypeStormovik, TypeFighter, TypeScout, TypeX4Carrier, TypeRadarLiSN2Carrier {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.startsWith("SN2c")) {
            hierMesh.chunkVisible("RadarSN2b", false);
            hierMesh.chunkVisible("RadarSN2c", true);
            hierMesh.chunkVisible("RadarSN2d", false);
        }
        else if (thisWeaponsName.startsWith("SN2d")) {
            hierMesh.chunkVisible("RadarSN2b", false);
            hierMesh.chunkVisible("RadarSN2c", false);
            hierMesh.chunkVisible("RadarSN2d", true);
        } else {
            hierMesh.chunkVisible("RadarSN2b", true);
            hierMesh.chunkVisible("RadarSN2c", false);
            hierMesh.chunkVisible("RadarSN2d", false);
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

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 1:
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
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.hierMesh().chunkVisible("HMask3_D0", false);
                break;
        }
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC2_D0", 0.0F, -f, 0.0F);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        for (int i = 1; i < 3; i++) {
            if (this.FM.getAltitude() < 3000F) {
                this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            } else {
                this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));
            }
        }

    }

    // +++ X4Carrier +++
    public void typeX4CAdjSidePlus() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = 1.0F;
            return;
        }
        if (this.curPilot == 2) {
            this.radarMode++;
            if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
                this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
            }
            return;
        }
    }

    public void typeX4CAdjSideMinus() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = -1F;
            return;
        }
        if (this.curPilot == 2) {
            this.radarMode--;
            if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
                this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
            }
            return;
        }
    }

    public void typeX4CAdjAttitudePlus() {
        if (this.curPilot == 1) {
            this.deltaTangage = 1.0F;
            return;
        }
        if (this.curPilot == 2) {
            this.radarGain += 10;
            if (this.radarGain > 100) {
                this.radarGain = 100;
            }
            return;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        if (this.curPilot == 1) {
            this.deltaTangage = -1F;
            return;
        }
        if (this.curPilot == 2) {
            this.radarGain -= 10;
            if (this.radarGain < 0) {
                this.radarGain = 0;
            }
            return;
        }
    }

    public void typeX4CResetControls() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = this.deltaTangage = 0.0F;
            return;
        }
        if (this.curPilot == 2) {
            this.radarGain = 50;
            return;
        }
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

    private int           curPilot    = 1;
    private int           radarGain   = 50;
    private int           radarMode   = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    public static boolean bChangedPit = false;

    static {
        Class class1 = JU_88G6.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ju-88");
        Property.set(class1, "meshName", "3DO/Plane/Ju-88G-6/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar04());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1945.5F);
//        Property.set(class1, "FlightModel", "FlightModels/Ju-88G-6.fmd:JU88G6");
        Property.set(class1, "FlightModel", "FlightModels/Ju-88G-6.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitJU_88G6.class, CockpitJU_88G6_Radar.class, CockpitJU_88G6_RGunner.class });
        Property.set(class1, "LOSElevation", 1.0976F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 0, 0, 0, 0, 1, 1 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_MGUN06", "_MGUN07" });
    }
}
