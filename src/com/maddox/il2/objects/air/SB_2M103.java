package com.maddox.il2.objects.air;

import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Mission;
import com.maddox.rts.Property;

public class SB_2M103 extends SB
{

    public SB_2M103()
    {
        kangle0 = 0.0F;
        kangle1 = 0.0F;
    }

    public int[] getBombTrainDelayArray()
    {
        return (new int[] {
            130, 200, 500, 1000, 1500, 2000
        });
    }

    public int getBombTrainMaxAmount()
    {
        return 20;
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("KMB_D0", thisWeaponsName.startsWith("KMB") || thisWeaponsName.startsWith("6xsab15"));
    }

    public void update(float f)
    {
        kangle0 = 0.95F * kangle0 + 0.05F * FM.EI.engines[0].getControlRadiator();
        hierMesh().chunkSetAngles("RadiatorL_D0", 0.0F, 0.0F, -30F * kangle0);
        kangle1 = 0.95F * kangle1 + 0.05F * FM.EI.engines[1].getControlRadiator();
        hierMesh().chunkSetAngles("RadiatorR_D0", 0.0F, 0.0F, -30F * kangle1);
        super.update(f);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag)
        {
            for(int i = 0; i < 4; i++)
                if(FM.AS.astateTankStates[i] > 0 && FM.AS.astateTankStates[i] < 5 && World.Rnd().nextFloat() < 0.05F)
                    FM.AS.repairTank(i);

        }
    }

    public static String getSkinPrefix(String s, Regiment regiment)
    {
        if(regiment == null || regiment.country() == null)
            return "";
        if(regiment.country().equals(PaintScheme.countryRussia))
        {
            int i = Mission.getMissionDate(true);
            if(i > 0)
            {
                if(i > 19420101)
                    return "1942_";
                if(i > 19410101)
                    return "1941_";
            }
            return "";
        }
        if(regiment.country().equals(PaintScheme.countryFrance) || regiment.country().equals(PaintScheme.countryBritain) || regiment.country().equals(PaintScheme.countryNetherlands) || regiment.country().equals(PaintScheme.countryPoland) || regiment.country().equals(PaintScheme.countryNewZealand) || regiment.country().equals(PaintScheme.countryUSA) || regiment.country().equals(PaintScheme.countryNoName))
            return "";
        else
            return "fi_";
    }

    private float kangle0;
    private float kangle1;

    static 
    {
        Class class1 = SB_2M103.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "SB");
        Property.set(class1, "meshNameDemo", "3DO/Plane/SB-2M-103(Russian)/hier-201.him");
        Property.set(class1, "meshName", "3DO/Plane/SB-2M-103(Multi1)/hier-201.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar00());
        Property.set(class1, "meshName_ru", "3DO/Plane/SB-2M-103(Russian)/hier-201.him");
        Property.set(class1, "PaintScheme_ru", new PaintSchemeBCSPar01());
        Property.set(class1, "yearService", 1939F);
        Property.set(class1, "yearExpired", 1944F);
        Property.set(class1, "FlightModel", "FlightModels/SB-2M-103.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitSB103.class, CockpitSB103_Bombardier.class, CockpitSB103_NGunner.class, CockpitSB103_TGunner.class, CockpitSB103_BGunner.class
        });
        Aircraft.weaponTriggersRegister(class1, new int[] {
            10, 10, 11, 12, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06", 
            "_BombSpawn07", "_BombSpawn08", "_BombSpawn09", "_BombSpawn10", "_BombSpawn11", "_ExternalBomb01", "_ExternalBomb02"
        });
    }
}
