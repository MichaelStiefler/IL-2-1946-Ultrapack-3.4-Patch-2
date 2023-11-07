package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.weapons.BombGunPara1;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public abstract class U_2_TD extends U_2xyz
{

    public U_2_TD()
    {
        skiAngleL = 0.0F;
        skiAngleR = 0.0F;
        spring = 0.15F;
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        Class class1 = U_2_TD.class;
        if(Mission.isCoop())
        {
            Property.set(class1, "cockpitClass", new Class[] {
                CockpitU_2.class, CockpitU_2_Copilot.class
            });
        } else
        {
            Property.set(class1, "cockpitClass", new Class[] {
                CockpitU_2.class, CockpitU_2_Bombardier.class
            });
            if(this == World.getPlayerAircraft())
                this.createCockpits();
        }
        FM.CT.bHasBrakeControl = false;
        if(FM.CT.Weapons[3] != null && (FM.CT.Weapons[3][0] instanceof BombGunPara1))
        {
            FM.crew = 1;
            FM.AS.astatePilotFunctions[0] = 1;
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("BombRack_D0", thisWeaponsName.endsWith("Bomb") || thisWeaponsName.startsWith("Cargo"));
        boolean winter = Config.isUSE_RENDER() && World.cur().camouflage == 1;
        hierMesh.chunkVisible("GearL1_D0", !winter);
        hierMesh.chunkVisible("GearR1_D0", !winter);
        hierMesh.chunkVisible("SkiC_D0", winter);
        hierMesh.chunkVisible("SkiL0_D0", winter);
        hierMesh.chunkVisible("SkiL1_D0", winter);
        hierMesh.chunkVisible("SkiL2_D0", winter);
        hierMesh.chunkVisible("SkiL3_D0", winter);
        hierMesh.chunkVisible("SkiL4_D0", winter);
        hierMesh.chunkVisible("SkiL5_D0", winter);
        hierMesh.chunkVisible("SkiR0_D0", winter);
        hierMesh.chunkVisible("SkiR1_D0", winter);
        hierMesh.chunkVisible("SkiR2_D0", winter);
        hierMesh.chunkVisible("SkiR3_D0", winter);
        hierMesh.chunkVisible("SkiR4_D0", winter);
        hierMesh.chunkVisible("SkiR5_D0", winter);
    }
    
    protected void moveFan(float f)
    {
        if(Config.isUSE_RENDER())
        {
            boolean flag = false;
            float f1 = Aircraft.cvt(FM.getSpeed(), 20F, 50F, 1.0F, 0.0F);
            float f2 = Aircraft.cvt(FM.getSpeed(), 0.0F, 20F, 0.0F, 0.5F);
            if(FM.Gears.gWheelSinking[0] > 0.0F)
            {
                flag = true;
                skiAngleL = 0.5F * skiAngleL + 0.5F * FM.Or.getTangage();
                if(skiAngleL > 20F)
                    skiAngleL = skiAngleL - spring;
                hierMesh().chunkSetAngles("SkiL0_D0", World.Rnd().nextFloat(-f2, f2), World.Rnd().nextFloat(-f2 * 2.0F, f2 * 2.0F) - skiAngleL, World.Rnd().nextFloat(f2, f2));
            } else
            {
                if((double)skiAngleL > (double)(f1 * -10F) + 0.01D)
                {
                    skiAngleL = skiAngleL - spring;
                    flag = true;
                } else
                if((double)skiAngleL < (double)(f1 * -10F) - 0.01D)
                {
                    skiAngleL = skiAngleL + spring;
                    flag = true;
                }
                hierMesh().chunkSetAngles("SkiL0_D0", 0.0F, -skiAngleL, 0.0F);
            }
            if(FM.Gears.gWheelSinking[1] > 0.0F)
            {
                flag = true;
                skiAngleR = 0.5F * skiAngleR + 0.5F * FM.Or.getTangage();
                if(skiAngleR > 20F)
                    skiAngleR = skiAngleR - spring;
                hierMesh().chunkSetAngles("SkiR0_D0", World.Rnd().nextFloat(-f2, f2), World.Rnd().nextFloat(-f2 * 2.0F, f2 * 2.0F) - skiAngleR, World.Rnd().nextFloat(f2, f2));
                if(FM.Gears.gWheelSinking[0] == 0.0F && FM.Or.getRoll() < 365F && FM.Or.getRoll() > 355F)
                {
                    skiAngleL = skiAngleR;
                    hierMesh().chunkSetAngles("SkiL0_D0", World.Rnd().nextFloat(-f2, f2), World.Rnd().nextFloat(-f2 * 2.0F, f2 * 2.0F) - skiAngleL, World.Rnd().nextFloat(f2, f2));
                }
            } else
            {
                if((double)skiAngleR > (double)(f1 * -10F) + 0.01D)
                {
                    skiAngleR = skiAngleR - spring;
                    flag = true;
                } else
                if((double)skiAngleR < (double)(f1 * -10F) - 0.01D)
                {
                    skiAngleR = skiAngleR + spring;
                    flag = true;
                }
                hierMesh().chunkSetAngles("SkiR0_D0", 0.0F, -skiAngleR, 0.0F);
            }
            if(!flag && f1 == 0.0F)
            {
                super.moveFan(f);
                return;
            }
            hierMesh().chunkSetAngles("SkiC_D0", 0.0F, (skiAngleL + skiAngleR) / 2.0F, 0.0F);
            float f3 = skiAngleL / 20F;
            float f4 = skiAngleL / 60F;
            if(skiAngleL < 0.0F)
                hierMesh().chunkSetAngles("SkiL2_D0", 0.0F, f3 * 10.4F, 0.0F);
            else
                hierMesh().chunkSetAngles("SkiL2_D0", 0.0F, f3 * -6F, f3 * -8F);
            resetYPRmodifier();
            Aircraft.xyz[2] = -f4;
            hierMesh().chunkSetLocate("SkiL1_D0", Aircraft.xyz, Aircraft.ypr);
            hierMesh().chunkSetAngles("SkiL5_D0", 0.0F, f3 * 8.7F, 0.0F);
            resetYPRmodifier();
            Aircraft.xyz[2] = f4;
            if(skiAngleL > 0.0F)
                hierMesh().chunkSetLocate("SkiL3_D0", Aircraft.xyz, Aircraft.ypr);
            float f5 = skiAngleR / 20F;
            float f6 = skiAngleR / 60F;
            if(skiAngleR < 0.0F)
                hierMesh().chunkSetAngles("SkiR2_D0", 0.0F, f5 * 10.4F, 0.0F);
            else
                hierMesh().chunkSetAngles("SkiR2_D0", 0.0F, f5 * -6F, f5 * 8F);
            resetYPRmodifier();
            Aircraft.xyz[2] = -f6;
            hierMesh().chunkSetLocate("SkiR1_D0", Aircraft.xyz, Aircraft.ypr);
            hierMesh().chunkSetAngles("SkiR5_D0", 0.0F, f5 * 8.7F, 0.0F);
            resetYPRmodifier();
            Aircraft.xyz[2] = -f6;
            if(skiAngleR > 0.0F)
                hierMesh().chunkSetLocate("SkiR3_D0", Aircraft.xyz, Aircraft.ypr);
        }
        super.moveFan(f);
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
                if(i > 0x1287d91)
                    return "43_";
                if(i > 0x1282ea9)
                    return "41_";
                if(i > 0x12805a5)
                    return "40_";
                else
                    return "";
            }
        }
        return "";
    }

    public void update(float f)
    {
        super.update(f);
        if(FM.CT.Weapons[3] != null && (FM.CT.Weapons[3][0] instanceof BombGunPara1) && !FM.CT.Weapons[3][0].haveBullets())
        {
            if(FM.AS.astateBailoutStep == 12)
                FM.AS.astateBailoutStep = 14;
            this.doRemoveBodyFromPlane(2);
            gunnerEjected = true;
        }
        if(hierMesh().isChunkVisible("WingRIn_CAP") || hierMesh().isChunkVisible("WingLIn_CAP"))
            hierMesh().chunkVisible("BombRack_D0", false);
    }

    public void doMurderPilot(int i)
    {
        super.doMurderPilot(i);
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            break;

        case 1:
            hierMesh().chunkVisible("Pilot2_D1", hierMesh().isChunkVisible("Pilot2_D0"));
            hierMesh().chunkVisible("Pilot2_D0", false);
            break;
        }
    }

    public boolean typeBomberToggleAutomation()
    {
        return false;
    }

    public void typeBomberAdjDistanceReset()
    {
    }

    public void typeBomberAdjDistancePlus()
    {
    }

    public void typeBomberAdjDistanceMinus()
    {
    }

    public void typeBomberAdjSideslipReset()
    {
    }

    public void typeBomberAdjSideslipPlus()
    {
    }

    public void typeBomberAdjSideslipMinus()
    {
    }

    public void typeBomberAdjAltitudeReset()
    {
    }

    public void typeBomberAdjAltitudePlus()
    {
    }

    public void typeBomberAdjAltitudeMinus()
    {
    }

    public void typeBomberAdjSpeedReset()
    {
    }

    public void typeBomberAdjSpeedPlus()
    {
    }

    public void typeBomberAdjSpeedMinus()
    {
    }

    public void typeBomberUpdate(float f)
    {
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted)
        throws IOException
    {
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput)
        throws IOException
    {
    }

    private float skiAngleL;
    private float skiAngleR;
    private float spring;

}
