package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;

public abstract class SE_2PA123 extends P_35family {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("barrelL_D0", thisWeaponsName.endsWith("4G") || thisWeaponsName.endsWith("6P"));
        hierMesh.chunkVisible("barrelR_D0", thisWeaponsName.endsWith("4G") || thisWeaponsName.endsWith("6P"));
        hierMesh.chunkVisible("RackL3_D0", thisWeaponsName.endsWith("2P") || thisWeaponsName.endsWith("4P") || thisWeaponsName.endsWith("6P"));
        hierMesh.chunkVisible("RackR3_D0", thisWeaponsName.endsWith("2P") || thisWeaponsName.endsWith("4P") || thisWeaponsName.endsWith("6P"));
        hierMesh.chunkVisible("RackL2_D0", thisWeaponsName.endsWith("4P") || thisWeaponsName.endsWith("6P"));
        hierMesh.chunkVisible("RackR2_D0", thisWeaponsName.endsWith("4P") || thisWeaponsName.endsWith("6P"));
    }
    
    public void doKillPilot(int i) {
        switch (i) {
            case 1:
                this.FM.turret[0].bIsOperable = false;
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
                this.hierMesh().chunkVisible("HMask2_D0", false);
                this.hierMesh().chunkVisible("Head2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                break;
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask2_D0", false);
            this.hierMesh().chunkVisible("HMask2a_D0", false);
        }
    }

}
