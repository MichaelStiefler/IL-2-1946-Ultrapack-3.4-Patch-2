package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class Breguet_19LV extends Br19xyz {
    
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        boolean rack = !thisWeaponsName.equals("default") && !thisWeaponsName.equals("none");
        hierMesh.chunkVisible("RackL_D0", rack);
        hierMesh.chunkVisible("RackR_D0", rack);
    }
    
    static {
        Class class1 = Breguet_19LV.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "He45");
        Property.set(class1, "meshName", "3DO/Plane/Breguet_19/hierLV.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar00());
        Property.set(class1, "meshName_it", "3DO/Plane/Breguet_19/hierLV.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar00());
        Property.set(class1, "yearService", 1939F);
        Property.set(class1, "yearExpired", 1943F);
        Property.set(class1, "FlightModel", "FlightModels/Breguet19L.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitBreguet_19H.class });
        Property.set(class1, "LOSElevation", 0.742F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 10, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 9, 9, 9, 9, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb47", "_ExternalBomb48", "_ExternalBomb49", "_ExternalBomb50", "_ExternalBomb51", "_ExternalBomb52", "_ExternalBomb53", "_ExternalBomb54", "_ExternalBomb55", "_ExternalBomb56", "_ExternalBomb57", "_ExternalBomb58", "_ExternalBomb59", "_ExternalBomb60", "_ExternalBomb61", "_ExternalBomb62", "_ExternalBomb63", "_ExternalBomb64", "_ExternalBomb65", "_ExternalBomb66", "_ExternalBomb67", "_ExternalBomb68", "_ExternalBomb69", "_ExternalBomb70", "_ExternalBomb71", "_ExternalBomb72", "_ExternalBomb73", "_ExternalBomb74", "_ExternalBomb75", "_ExternalBomb76", "_ExternalBomb77", "_ExternalBomb78", "_ExternalBomb79", "_ExternalBomb80", "_ExternalBomb81", "_ExternalBomb82", "_ExternalBomb83", "_ExternalBomb84", "_ExternalBomb85", "_ExternalBomb86", "_ExternalBomb87", "_ExternalBomb88", "_ExternalBomb89", "_ExternalBomb90",
                "_ExternalBomb91", "_ExternalBomb92", "_ExternalBomb93", "_ExternalBomb94", "_ExternalBomb95", "_ExternalBomb96", "_ExternalBomb97", "_ExternalBomb98", "_ExternalBomb13", "_ExternalBomb14", "_ExternalBomb15", "_ExternalBomb16", "_ExternalBomb17", "_ExternalBomb18", "_ExternalBomb19", "_ExternalBomb20", "_ExternalBomb21", "_ExternalBomb22", "_ExternalBomb23", "_ExternalBomb24", "_ExternalBomb25", "_ExternalBomb26", "_ExternalBomb27", "_ExternalBomb28", "_ExternalBomb29", "_ExternalBomb30", "_ExternalBomb31", "_ExternalBomb32", "_ExternalBomb33", "_ExternalBomb34", "_ExternalBomb35", "_ExternalBomb36", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06" });
    }
}
