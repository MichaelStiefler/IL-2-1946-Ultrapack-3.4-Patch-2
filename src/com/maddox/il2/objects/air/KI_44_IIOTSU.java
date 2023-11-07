package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class KI_44_IIOTSU extends KI_44_II {

    public KI_44_IIOTSU() {
        this.flapps = 0.0F;
    }

    public void update(float f) {
        super.update(f);
        float f1 = this.FM.EI.engines[0].getControlRadiator();
        if (Math.abs(this.flapps - f1) > 0.01F) {
            this.flapps = f1;
            for (int i = 1; i < 13; i++) {
                this.hierMesh().chunkSetAngles("Cowflap" + i + "_D0", -26F * f1, 0.0F, 0.0F);
            }

        }
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.Gears.computePlaneLandPose(this.FM);
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        boolean altWing = thisWeaponsName.startsWith("2xMG_2x40mm") || thisWeaponsName.endsWith("2xMG_2x40mm") || thisWeaponsName.startsWith("2xDpt") || thisWeaponsName.endsWith("2xDpt");
        hierMesh.chunkVisible("CWingLIn_D0", altWing);
        hierMesh.chunkVisible("CWingRIn_D0", altWing);
        hierMesh.chunkVisible("WingLIn_D0", !altWing);
        hierMesh.chunkVisible("WingRIn_D0", !altWing);
    }
    
    private float flapps;

    static {
        Class class1 = KI_44_IIOTSU.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ki-44");
        Property.set(class1, "meshName", "3DO/Plane/KI-44_IIOTSU(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "meshName_ja", "3DO/Plane/KI-44_IIOTSU(ja)/hier.him");
        Property.set(class1, "PaintScheme_ja", new PaintSchemeFCSPar05());
        Property.set(class1, "yearService", 1942F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Ki-44-IIko.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitKI_44_II_ko.class });
        Property.set(class1, "LOSElevation", 0.4252F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 1, 1, 9, 9, 3, 3, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb01", "_ExternalBomb02", "_ExternalDev03", "_ExternalDev04" });
    }
}
