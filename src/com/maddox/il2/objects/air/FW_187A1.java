package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class FW_187A1 extends FW_187
    implements TypeFighter, TypeBNZFighter, TypeStormovik
{
    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("HMask1_D0", false);
            break;
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(this.FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.75F);
        hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
    }

    public void typeX4CAdjSidePlus()
    {
        if(curPilot == 1)
        {
            deltaAzimuth = 1.0F;
            return;
        }
        radarMode++;
        if(radarMode > 1)
            radarMode = 0;
    }

    public void typeX4CAdjSideMinus()
    {
        if(curPilot == 1)
        {
            deltaAzimuth = -1F;
            return;
        }
        radarMode--;
        if(radarMode < 0)
            radarMode = 1;
    }

    public void typeX4CAdjAttitudePlus()
    {
        if(curPilot == 1)
        {
            deltaTangage = 1.0F;
            return;
        }
        radarGain += 10;
        if(radarGain > 100)
            radarGain = 100;
    }

    public void typeX4CAdjAttitudeMinus()
    {
        if(curPilot == 1)
        {
            deltaTangage = -1F;
            return;
        }
        radarGain -= 10;
        if(radarGain < 0)
            radarGain = 0;
    }

    public void typeX4CResetControls()
    {
        if(curPilot == 1)
        {
            deltaAzimuth = deltaTangage = 0.0F;
            return;
        } else
        {
            radarGain = 50;
            return;
        }
    }

    public float typeX4CgetdeltaAzimuth()
    {
        return deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage()
    {
        return deltaTangage;
    }

    public void setCurPilot(int i)
    {
        curPilot = i;
    }

    public int getCurPilot()
    {
        return curPilot;
    }

    public int getRadarGain()
    {
        return radarGain;
    }

    public int getRadarMode()
    {
        return radarMode;
    }

    private float deltaAzimuth;
    private float deltaTangage;
    private int curPilot;
    private int radarGain;
    private int radarMode;

    static 
    {
        Class class1 = FW_187A1.class;
        Property.set(class1, "iconFar_shortClassName", "FW-187A1");
        Property.set(class1, "meshName", "3DO/Plane/FW-187A1/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(class1, "yearService", 1940F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/FW-187A-1.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitFW_187A.class });
        Property.set(class1, "LOSElevation", 0.66895F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            1, 1, 0, 0, 0, 0, 10, 10, 9, 9, 
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 
            9, 9, 3, 3, 3, 3, 3, 3, 3, 3, 
            2, 2, 2, 2, 1, 1, 1, 1, 9, 9, 
            9, 9, 3, 3, 3, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_MGUN05", "_MGUN06", "_ExternalDev01", "_ExternalDev02", 
            "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08", "_ExternalDev09", "_ExternalDev10", "_ExternalDev11", "_ExternalDev12", 
            "_ExternalDev13", "_ExternalDev14", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", 
            "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", "_ExternalDev15", "_ExternalDev16", 
            "_ExternalDev17", "_ExternalDev18", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb13", "_ExternalBomb14", "_ExternalBomb15", "_ExternalBomb16"
        });
    }
}
