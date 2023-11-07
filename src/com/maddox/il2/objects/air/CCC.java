package com.maddox.il2.objects.air;

import com.maddox.il2.ai.Regiment;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.game.Mission;
import com.maddox.rts.Property;

public class CCC extends R_5Bomber
    implements TypeBomber, TypeStormovik
{

    public CCC()
    {
        strafeWithGuns = true;
        bDynamoOperational = false;
    }

    public void missionStarting()
    {
        super.missionStarting();
        customization();
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        if(FM.turret.length != 0)
            this.CCCmoveGunner();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("DER31L_D0", thisWeaponsName.endsWith("4xFAB50") || thisWeaponsName.endsWith("4xFAB100"));
        hierMesh.chunkVisible("DER31R_D0", thisWeaponsName.endsWith("4xFAB50") || thisWeaponsName.endsWith("4xFAB100"));
        hierMesh.chunkVisible("GearL4_D0", !thisWeaponsName.startsWith("nospats_"));
        hierMesh.chunkVisible("GearR4_D0", !thisWeaponsName.startsWith("nospats_"));
    }

    private void customization()
    {
        if(!Config.isUSE_RENDER())
            return;
        int i = hierMesh().chunkFindCheck("cf_D0");
        int j = hierMesh().materialFindInChunk("Gloss1D0o", i);
        Mat mat = hierMesh().material(j);
        String s = mat.Name();
//        customini = false;
        try
        {
            s = s.toLowerCase();
            if(s.startsWith("paintschemes/cache"))
            {
                s = s.substring(19);
                s = s.substring(0, s.indexOf("/"));
            } else
            if(s.startsWith("3do/plane/ccc/summer"))
                s = "summer";
            else
            if(s.startsWith("3do/plane/ccc/winter"))
                s = "winter";
            else
            if(s.startsWith("3do/plane/ccc/41_summer"))
                s = "summer";
            else
            if(s.startsWith("3do/plane/ccc/41_winter"))
                s = "winter";
//            String s1 = Main.cur().netFileServerSkin.primaryPath();
//            File file = new File(HomePath.toFileSystemName(s1 + "/C.C.C/Customization.ini", 0));
//            BufferedReader bufferedreader = new BufferedReader(new FileReader(file));
////            Object obj = null;
//            boolean flag = false;
//            do
//            {
//                String s2;
//                if((s2 = bufferedreader.readLine()) == null)
//                    break;
//                if(s2.equals("[WheelSpatsHide]"))
//                {
//                    flag = true;
////                    customini = true;
//                } else
//                if(s2.equals(s) && flag)
//                {
//                    hierMesh().chunkVisible("GearL4_D0", false);
//                    hierMesh().chunkVisible("GearR4_D0", false);
//                }
//            } while(true);
//            bufferedreader.close();
        }
        catch(Exception exception)
        {
            System.out.println(exception);
        }
    }

    public boolean hasIntervalometer()
    {
        return true;
    }

    public int[] getBombTrainDelayArray()
    {
        return (new int[] {
            0
        });
    }

    public int getBombTrainMaxAmount()
    {
        return 4;
    }

    public boolean turretAngles(int i, float af[])
    {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        if(f < -95F)
        {
            f = -95F;
            flag = false;
        }
        if(f > 95F)
        {
            f = 95F;
            flag = false;
        }
        if(f1 > 70F)
        {
            f1 = 70F;
            flag = false;
        }
        if(f1 < -3F && f > -12F && f < 12F)
        {
            f1 = -3F;
            flag = false;
        } else
        if(f > -37F && f <= -12F && f1 < Aircraft.cvt(f, -37F, -12F, -25F, -3F))
        {
            f1 = Aircraft.cvt(f, -37F, -12F, -25F, -3F);
            flag = false;
        } else
        if(f >= 12F && f < 37F && f1 < Aircraft.cvt(f, 12F, 37F, -3F, -25F))
        {
            f1 = Aircraft.cvt(f, 12F, 37F, -3F, -25F);
            flag = false;
        } else
        if(f1 < -25F)
        {
            f1 = -25F;
            flag = false;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public void update(float f)
    {
        updateRadiator();
        super.update(f);
        if(FM.turret.length != 0)
        {
            CCCgunnerAiming();
            gunnerTarget2();
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(FM.getAltitude() < 3000F)
        {
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
            hierMesh().chunkVisible("HMask3_D0", false);
        } else
        {
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
            hierMesh().chunkVisible("HMask2_D0", hierMesh().isChunkVisible("Pilot2_D0"));
            hierMesh().chunkVisible("HMask3_D0", hierMesh().isChunkVisible("Pilot3_D0"));
        }
    }

    public void doWoundPilot(int i, float f)
    {
        switch(i)
        {
        case 1:
            FM.turret[0].setHealth(f);
            if(f <= 0.0F)
                gunnerDead = true;
            break;
        }
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("HMask1_D0", false);
            break;

        case 1:
            hierMesh().chunkVisible("Pilot2_D1", hierMesh().isChunkVisible("Pilot2_D0"));
            hierMesh().chunkVisible("Pilot3_D1", hierMesh().isChunkVisible("Pilot3_D0"));
            hierMesh().chunkVisible("Pilot2_D0", false);
            hierMesh().chunkVisible("Pilot3_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
            hierMesh().chunkVisible("HMask3_D0", false);
            gunnerDead = true;
            break;
        }
    }

    public void doRemoveBodyFromPlane(int i)
    {
        super.doRemoveBodyFromPlane(i);
        if(i == 2)
        {
            super.doRemoveBodyFromPlane(3);
            gunnerEjected = true;
        }
    }

    protected void moveBayDoor(float f)
    {
        hierMesh().chunkSetAngles("BayDoor01_D0", 0.0F, 92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor02_D0", 0.0F, -92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor03_D0", 0.0F, 92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor04_D0", 0.0F, -92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor05_D0", 0.0F, 92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor06_D0", 0.0F, -92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor07_D0", 0.0F, 92F * f, 0.0F);
        hierMesh().chunkSetAngles("BayDoor08_D0", 0.0F, -92F * f, 0.0F);
    }

    public static String getSkinPrefix(String s, Regiment regiment)
    {
        if(regiment == null || regiment.country() == null)
            return "";
        if(regiment.country().equals(PaintScheme.countryRussia))
        {
            int i = Mission.getMissionDate(true);
            if(i > 0)
                if(i > 0x1282cb5)
                    return "41_";
                else
                    return "";
        }
        return "";
    }

//    private boolean customini;

    static 
    {
        Class class1 = CCC.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "CCC");
        Property.set(class1, "meshName", "3do/plane/CCC/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFCSPar08());
        Property.set(class1, "yearService", 1936F);
        Property.set(class1, "yearExpired", 1944F);
        Property.set(class1, "FlightModel", "FlightModels/CCC.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitCCC.class, CockpitR_5_OP1.class, CockpitCCC_Bombardier.class, CockpitCCC_TGunner.class
        });
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 1, 1, 0, 10, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
            3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUNCCC01", "_MGUNCCC02", "_MGUNCCC03", "_MGUNCCC04", "_MGUNCCC05", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", 
            "_BombSpawn06", "_BombSpawn07", "_BombSpawn08", "_BombSpawn09", "_BombSpawn10", "_BombSpawn11", "_BombSpawn12", "_BombSpawn13", "_BombSpawn14", "_BombSpawn15", 
            "_BombSpawn16", "_BombSpawn17", "_BombSpawn18", "_BombSpawn19", "_BombSpawn20", "_ExternalBomb21", "_ExternalBomb22", "_ExternalBomb23", "_ExternalBomb24", "_ExternalBomb25", 
            "_ExternalBomb26", "_ExternalBomb27", "_ExternalBomb28"
        });
    }
}
