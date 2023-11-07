package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public abstract class P_35xyz extends RE_2000xyz {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("RackL_D0", thisWeaponsName.indexOf("2x") != -1);
        hierMesh.chunkVisible("RackR_D0", thisWeaponsName.indexOf("2x") != -1);
    }

    protected void moveFan(float f) {
        if (!Config.isUSE_RENDER()) return;
        int i = this.FM.EI.engines[0].getStage();
        if (i > 0 && i < 6) f = 0.005F * i;
        super.moveFan(f);
        this.hierMesh().chunkSetAngles(Aircraft.Props[0][0], 0.0F, -this.propPos[0] + 45F, 0.0F);
    }

    protected static void printDebugMessage(String theMessage) {
        if (_DEBUG) System.out.println(theMessage);
    }

    private static boolean _DEBUG = false;

    static {
        Property.set(P_35xyz.class, "originCountry", PaintScheme.countryUSA);
    }
}
