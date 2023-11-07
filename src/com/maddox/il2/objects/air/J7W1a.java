package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.Property;

public class J7W1a extends J7Wx
{
    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        if(thisWeaponsName.startsWith("light"))
            FM.M.massEmpty -= 70F;
    }

    public void update(float f)
    {
        super.update(f);
        if(FM.AS.isMaster() && FM.AS.astateBailoutStep == 2)
            doRemoveProp();
    }

    private final void doRemoveProp()
    {
        oldProp[1] = 99;
        if(hierMesh().isChunkVisible("PropRot1_D0"))
        {
            Wreckage wreckage = new Wreckage(this, hierMesh().chunkFind("Prop1_D0"));
            Wreckage wreckage1 = new Wreckage(this, hierMesh().chunkFind("Fan"));
            Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.SMOKE, 3F);
            Eff3DActor.New(wreckage1, null, null, 1.0F, Wreckage.SMOKE, 3F);
            Vector3d vector3d = new Vector3d();
            vector3d.set(FM.Vwld);
            wreckage.setSpeed(vector3d);
            wreckage1.setSpeed(vector3d);
            hierMesh().chunkVisible("Prop1_D0", false);
            hierMesh().chunkVisible("Prop1_D1", false);
            hierMesh().chunkVisible("PropRot1_D0", false);
            hierMesh().chunkVisible("Fan", false);
            this.FM.EI.engines[0].setEngineDies(this);
        }
    }

    static 
    {
        Class class1 = J7W1a.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J7W");
        Property.set(class1, "meshName", "3DO/Plane/J7W1a/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1949F);
        Property.set(class1, "FlightModel", "FlightModels/J7W1a_Vasya.fmd");
//        Property.set(class1, "FlightModel", "FlightModels/J7W1a.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitJ7W.class
        });
        Property.set(class1, "LOSElevation", 1.0151F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 3, 3, 3, 3, 9, 9, 
            9, 9, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_Cannon01", "_Cannon02", "_Cannon03", "_Cannon04", "_Externalbomb01", "_Externalbomb02", "_Externalbomb03", "_Externalbomb04", "_Externaldev01", "_Externaldev02", 
            "_Externaldev03", "_Externaldev04", "_Externaldev05", "_Externaldev06"
        });
    }
}
