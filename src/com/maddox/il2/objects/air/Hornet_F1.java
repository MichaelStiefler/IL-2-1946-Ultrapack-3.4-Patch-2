package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class Hornet_F1 extends DH103
{
    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            radarmode = 5;
            break;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        default:
            return super.cutFM(i, j, actor);
        }
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("S1ASR", false);
        hierMesh.chunkVisible("S2FAB", false);
        hierMesh.chunkVisible("S2PTB", false);
        hierMesh.chunkVisible("S6PTB", false);
        hierMesh.chunkVisible("S6FAB", false);
        hierMesh.chunkVisible("S7ASR", false);
        if(thisWeaponsName.startsWith("01"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("02"))
        {
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
        }
        if(thisWeaponsName.startsWith("03"))
        {
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
        }
        if(thisWeaponsName.startsWith("04"))
        {
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S6PTB", true);
        }
        if(thisWeaponsName.startsWith("05"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("06"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("07"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S6PTB", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        Hornet_F1.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if((this.FM.Gears.nearGround() || this.FM.Gears.onGround()) && this.FM.CT.getCockpitDoor() == 1.0F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
    }

    static 
    {
        Class class1 = Hornet_F1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Hornet F.1");
        Property.set(class1, "meshName", "3DO/Plane/HornetF1(Multi1)/Hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1946.8F);
        Property.set(class1, "yearExpired", 1957.7F);
        Property.set(class1, "FlightModel", "FlightModels/DH103-F1.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitDH103.class
        });
        Property.set(class1, "LOSElevation", 0.6731F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 9, 9, 3, 3, 3, 3, 
            2, 2, 2, 2, 2, 2, 2, 2
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb01", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb02", 
            "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08"
        });
    }
}
