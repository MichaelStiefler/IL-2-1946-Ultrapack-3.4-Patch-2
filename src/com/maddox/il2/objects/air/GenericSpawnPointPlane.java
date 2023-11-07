package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.engine.Actor;
import com.maddox.rts.Property;

public class GenericSpawnPointPlane extends Scheme1
{

    public GenericSpawnPointPlane()
    {
    }

    public void doMurderPilot(int i)
    {
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        return false;
    }

    protected void moveAileron(float f)
    {
    }

    protected void moveElevator(float f)
    {
    }

    protected void moveFlap(float f)
    {
    }

    protected void moveRudder(float f)
    {
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
    }

    public void msgShot(Shot shot)
    {
    }

    static 
    {
        Class class1 = com.maddox.il2.objects.air.GenericSpawnPointPlane.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "");
        Property.set(class1, "meshName", "3DO/Plane/SpawnPointPlane/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "originCountry", PaintScheme.countryRussia);
        Property.set(class1, "yearService", 0.0F);
        Property.set(class1, "yearExpired", 0.0F);
        Property.set(class1, "FlightModel", "FlightModels/G-11.fmd");
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            null
        });
        Aircraft.weaponsRegister(class1, "default", new String[] {
            null
        });
    }
}
