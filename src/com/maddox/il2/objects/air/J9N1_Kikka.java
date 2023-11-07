package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class J9N1_Kikka extends Kikka123
{
    static 
    {
        Class class1 = J9N1_Kikka.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Kikka");
        Property.set(class1, "meshName", "3DO/Plane/J9N1-Kikka(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(class1, "yearService", 1943.1F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/J9N1.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            Cockpit_Kikka.class
        });
        Property.set(class1, "LOSElevation", 0.74185F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
                0, 0, 1, 1, 3, 3, 3, 3
            });
            Aircraft.weaponHooksRegister(class1, new String[] {
                "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04"
            });
    }
}
