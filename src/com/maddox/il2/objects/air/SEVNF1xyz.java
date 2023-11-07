package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;

public abstract class SEVNF1xyz extends P_35family {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.Gears.computePlaneLandPose(this.FM);
//        this.mydebuggunnery("H = " + this.FM.Gears.H);
//        this.mydebuggunnery("Pitch = " + this.FM.Gears.Pitch);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("RackL_D0", thisWeaponsName.endsWith("bombs"));
        hierMesh.chunkVisible("RackR_D0", thisWeaponsName.endsWith("bombs"));
        hierMesh.chunkVisible("barrelL_D0", thisWeaponsName.endsWith("4xMG"));
        hierMesh.chunkVisible("barrelR_D0", thisWeaponsName.endsWith("4xMG"));
        hierMesh.chunkVisible("RackC_D0", thisWeaponsName.endsWith("bomb"));
    }
    
    public void doRemoveBodyFromPlane(int i) {
        super.doRemoveBodyFromPlane(i);
        this.hierMesh().chunkVisible("Pilot1_D0", false);
        this.hierMesh().chunkVisible("pilot1a_D0", false);
        this.hierMesh().chunkVisible("pilot1b_D0", false);
        this.hierMesh().chunkVisible("pilot1c_D0", false);
        this.hierMesh().chunkVisible("pilot1d_D0", false);
        this.hierMesh().chunkVisible("pilot1e_D0", false);
        this.hierMesh().chunkVisible("Head1_D0", false);
        this.hierMesh().chunkVisible("Head1a_D0", false);
        this.hierMesh().chunkVisible("Head1b_D0", false);
        this.hierMesh().chunkVisible("Head1c_D0", false);
        this.hierMesh().chunkVisible("Head1d_D0", false);
        this.hierMesh().chunkVisible("HMask1_D0", false);
        this.hierMesh().chunkVisible("HMask1a_D0", false);
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("pilot1a_D0", false);
                this.hierMesh().chunkVisible("pilot1b_D0", false);
                this.hierMesh().chunkVisible("pilot1c_D0", false);
                this.hierMesh().chunkVisible("pilot1d_D0", false);
                this.hierMesh().chunkVisible("pilot1e_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Head1a_D0", false);
                this.hierMesh().chunkVisible("Head1b_D0", false);
                this.hierMesh().chunkVisible("Head1c_D0", false);
                this.hierMesh().chunkVisible("Head1d_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("HMask1a_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.hierMesh().chunkVisible("pilot1a_D1", true);
                this.hierMesh().chunkVisible("pilot1b_D1", true);
                this.hierMesh().chunkVisible("pilot1c_D1", true);
                this.hierMesh().chunkVisible("pilot1d_D1", true);
                this.hierMesh().chunkVisible("pilot1e_D1", true);
                this.hierMesh().chunkVisible("Head1_D1", true);
                this.hierMesh().chunkVisible("Head1a_D1", true);
                this.hierMesh().chunkVisible("Head1b_D1", true);
                this.hierMesh().chunkVisible("Head1c_D1", true);
                this.hierMesh().chunkVisible("Head1d_D1", true);
                break;
        }
    }

}
