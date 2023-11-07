package com.maddox.il2.objects.air;

import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public class SeaHornet_F20 extends DH103
{

    public SeaHornet_F20()
    {
        arrestor = 0.0F;
        RSOKilled = false;
    }

    protected void moveWingFold(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("WingLOut_D0", 0.0F, 150F * f, 0.0F);
        hiermesh.chunkSetAngles("WingROut_D0", 0.0F, -150F * f, 0.0F);
    }

    public void moveWingFold(float f)
    {
        if(f < 0.001F)
        {
            setGunPodsOn(true);
            hideWingWeapons(false);
        } else
        {
            setGunPodsOn(false);
            FM.CT.WeaponControl[0] = false;
            hideWingWeapons(true);
        }
        moveWingFold(hierMesh(), f);
    }

    public void moveArrestorHook(float f)
    {
        hierMesh().chunkSetAngles("TailHook_D0", 0.0F, 35F * f, 0.0F);
        arrestor = f;
    }

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

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName)
    {
        hierMesh.chunkVisible("S1ASR", false);
        hierMesh.chunkVisible("S2FAB", false);
        hierMesh.chunkVisible("S2PTB", false);
        hierMesh.chunkVisible("S2ASM", false);
        hierMesh.chunkVisible("S3FAB", false);
        hierMesh.chunkVisible("S4ASM", false);
        hierMesh.chunkVisible("S4KAB", false);
        hierMesh.chunkVisible("S5FAB", false);
        hierMesh.chunkVisible("S6ASM", false);
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
        if(thisWeaponsName.startsWith("08"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S4ASM", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
        if(thisWeaponsName.startsWith("09"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S3FAB", true);
            hierMesh.chunkVisible("S4ASM", true);
            hierMesh.chunkVisible("S5FAB", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
        if(thisWeaponsName.startsWith("10"))
            hierMesh.chunkVisible("S4KAB", true);
        if(thisWeaponsName.startsWith("11"))
        {
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S4KAB", true);
            hierMesh.chunkVisible("S6PTB", true);
        }
        if(thisWeaponsName.startsWith("12"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S4KAB", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        SeaHornet_F20.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if((this.FM.Gears.nearGround() || this.FM.Gears.onGround()) && this.FM.CT.getCockpitDoor() == 1.0F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
    }

    public void update(float f)
    {
        super.update(f);
        if(!(this.FM instanceof Pilot))
            return;
        if(this.FM.CT.getArrestor() > 0.2F)
            if(this.FM.Gears.arrestorVAngle != 0.0F)
            {
                float f1 = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -50F, 7F, 1.0F, 0.0F);
                arrestor = 0.8F * arrestor + 0.2F * f1;
                moveArrestorHook(arrestor);
            } else
            {
                float f2 = (-33F * this.FM.Gears.arrestorVSink) / 57F;
                if(f2 < 0.0F && this.FM.getSpeedKMH() > 60F)
                    Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
                if(f2 > 0.0F && this.FM.CT.getArrestor() < 0.95F)
                    f2 = 0.0F;
                if(f2 > 0.2F)
                    f2 = 0.2F;
                if(f2 > 0.0F)
                    arrestor = 0.7F * arrestor + 0.3F * (arrestor + f2);
                else
                    arrestor = 0.3F * arrestor + 0.7F * (arrestor + f2);
                if(arrestor < 0.0F)
                    arrestor = 0.0F;
                else
                if(arrestor > 1.0F)
                    arrestor = 1.0F;
                moveArrestorHook(arrestor);
            }
    }

    private float arrestor;

    static 
    {
        Class class1 = SeaHornet_F20.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "SeaHornet F.20");
        Property.set(class1, "meshName", "3DO/Plane/SeaHornetF20(Multi1)/Hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1946.8F);
        Property.set(class1, "yearExpired", 1957.7F);
        Property.set(class1, "FlightModel", "FlightModels/DH103-F20.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitDH103.class
        });
        Property.set(class1, "LOSElevation", 0.6731F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 9, 9, 3, 3, 3, 3, 
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb01", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb02", 
            "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock09", 
            "_ExternalRock10", "_ExternalRock10", "_ExternalRock11", "_ExternalRock11", "_ExternalBomb03", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb04", "_ExternalBomb05"
        });
    }
}
