package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;

public class D37X extends ParasolX {
    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveFlap(float f) {
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, -45F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -45F * f, 0.0F);
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -30F * f, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -30F * f, 0.0F);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.equals("2xVickers+2PV1")) {
            hierMesh.chunkVisible("WGun1_D0", true);
            hierMesh.chunkVisible("WGun2_D0", true);
            hierMesh.chunkVisible("WGun3_D0", true);
            hierMesh.chunkVisible("WGun4_D0", true);
            hierMesh.chunkVisible("WCannon1_D0", false);
            hierMesh.chunkVisible("WCannon2_D0", false);
            return;
        }
        if (thisWeaponsName.equals("2xVickers")) {
            hierMesh.chunkVisible("WGun1_D0", true);
            hierMesh.chunkVisible("WGun2_D0", true);
            hierMesh.chunkVisible("WGun3_D0", false);
            hierMesh.chunkVisible("WGun4_D0", false);
            hierMesh.chunkVisible("WCannon1_D0", false);
            hierMesh.chunkVisible("WCannon2_D0", false);
            return;
        }
        if (thisWeaponsName.equals("2xOerlikon")) {
            hierMesh.chunkVisible("WGun1_D0", false);
            hierMesh.chunkVisible("WGun2_D0", false);
            hierMesh.chunkVisible("WGun3_D0", false);
            hierMesh.chunkVisible("WGun4_D0", false);
            hierMesh.chunkVisible("WCannon1_D0", true);
            hierMesh.chunkVisible("WCannon2_D0", true);
            return;
        }
    }
    
}
