package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class ME_155B4 extends ME_155xyz
    implements TypeFighter, TypeBNZFighter
{
    public void update(float f)
    {
        if(this.FM.Loc.z > 9000D)
        {
            if(!this.FM.EI.engines[0].getControlAfterburner())
                this.FM.EI.engines[0].setAfterburnerType(2);
        } else
        if(!this.FM.EI.engines[0].getControlAfterburner())
            this.FM.EI.engines[0].setAfterburnerType(9);
        super.update(f);
    }

    static 
    {
        Class class1 = ME_155B4.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Me-155B-4");
        Property.set(class1, "meshName", "3do/plane/ME155B2(multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1945.3F);
        Property.set(class1, "yearExpired", 1955F);
        Property.set(class1, "FlightModel", "FlightModels/ME-155B-4.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitME_155B2.class
        });
        Property.set(class1, "LOSElevation", 0.7498F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 1, 1, 1, 1, 1, 9, 
            9, 9, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_CANNON07", "_ExternalDev01", 
            "_ExternalDev02", "_ExternalDev03", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05"
        });
    }
}
