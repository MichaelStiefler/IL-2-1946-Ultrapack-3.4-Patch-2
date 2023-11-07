package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class Ar_240C2 extends Ar_240xyz
    implements TypeFighter, TypeBNZFighter
{
    public boolean turretAngles(int i, float af[])
    {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch(i)
        {
        default:
            break;

        case 0:
            if(f < -75F)
            {
                f = -75F;
                flag = false;
            }
            if(f > 75F)
            {
                f = 75F;
                flag = false;
            }
            if(f1 < -5F)
            {
                f1 = -5F;
                flag = false;
            }
            if(f1 > 45F)
            {
                f1 = 45F;
                flag = false;
            }
            break;

        case 1:
            if(f < -75F)
            {
                f = -75F;
                flag = false;
            }
            if(f > 75F)
            {
                f = 75F;
                flag = false;
            }
            if(f1 < -5F)
            {
                f1 = -5F;
                flag = false;
            }
            if(f1 > 45F)
            {
                f1 = 45F;
                flag = false;
            }
            break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public void doKillPilot(int i)
    {
        switch(i)
        {
        case 1:
            FM.turret[0].bIsOperable = false;
            FM.turret[1].bIsOperable = false;
            break;
        }
    }

    static 
    {
        Class class1 = Ar_240C2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ar240");
        Property.set(class1, "meshName", "3DO/Plane/Ar_240C2/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1941.5F);
        Property.set(class1, "yearExpired", 1948.5F);
        Property.set(class1, "FlightModel", "FlightModels/Arado-240C.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitAR_240A.class, CockpitAR_240Radar.class
        });
        Property.set(class1, "LOSElevation", 0.76315F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 1, 1, 1, 1, 10, 10, 
            11, 11, 3, 3, 3, 3, 9, 9, 9, 9, 
            9, 9, 3, 3, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", "_MGUN01", "_MGUN02", 
            "_MGUN03", "_MGUN04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", 
            "_ExternalDev05", "_ExternalDev06", "_ExternalBomb05", "_ExternalBomb06", "_ExternalDev07", "_ExternalDev08"
        });
    }
}
