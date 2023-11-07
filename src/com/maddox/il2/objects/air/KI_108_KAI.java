package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class KI_108_KAI extends KI_108abc
    implements TypeFighter, TypeBNZFighter
{
    static 
    {
        Class class1 = KI_108_KAI.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ki-108");
        Property.set(class1, "meshName", "3do/plane/Ki-108_KAI(multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFCSPar05());
        Property.set(class1, "yearService", 1941F);
        Property.set(class1, "yearExpired", 1945F);
        Property.set(class1, "FlightModel", "FlightModels/Ki-108-KAI.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitKI_108KAI.class
        });
        Property.set(class1, "LOSElevation", 0.74185F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01"
        });
    }
}
