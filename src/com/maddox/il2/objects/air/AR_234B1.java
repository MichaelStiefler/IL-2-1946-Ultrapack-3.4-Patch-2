package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Reflection;

public class AR_234B1 extends AR_234B2
    implements TypeScout
{

    public AR_234B1()
    {
        tankStates = new int[2];
        nextTankToggleTime = 0L;
        nextEngineFireActionTime = new long[2];
        nextEngineFireAction = new int[2];
        engineStates = new int[2];
        throttleLocked = false;
        lastThrustChange = 0L;
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

    public void update(float f)
    {
        super.update(f);
        for(int i = 0; i <= 1; i++)
        {
            if(Math.abs(FM.EI.engines[i].getControlThrottle() - lastLockedThrust[i]) > 0.03F || FM.EI.engines[i].getStage() > 0 && FM.EI.engines[i].getStage() < 6)
            {
                lastLockedThrust[i] = FM.EI.engines[i].getControlThrottle();
                lastThrustChange = Time.current();
            }
            throttleLocked = Time.current() - lastThrustChange > 5000L;
        }

    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(this != World.getPlayerAircraft())
        {
            return;
        } else
        {
            checkEngineStates();
            checkTankState(0, 1);
            checkTankState(1, 0);
            return;
        }
    }

    private void checkEngineStates()
    {
        for(int i = 0; i <= 1; i++)
        {
            if(!engineIsUp(i))
                tankSelectors[i] = 2;
            if(FM.AS.astateEngineStates[i] >= 4)
                if(nextEngineFireActionTime[i] == 0L)
                {
                    System.out.println("ENGINE " + (i + 1) + " caught fire !!!");
                    nextEngineFireActionTime[i] = Time.current() + 5000L;
                } else
                if(Time.current() > nextEngineFireActionTime[i])
                    switch(nextEngineFireAction[i])
                    {
                    default:
                        break;

                    case 0:
                        FM.AS.setEngineDies(this, i);
                        tankSelectors[i] = 2;
                        nextEngineFireActionTime[i] = Time.current() + 5000L;
                        nextEngineFireAction[i] = 1;
                        break;

                    case 1:
                        FM.AS.setEngineState(this, i, 2);
                        Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "Smoke"), null, 1.0F, "3DO/Effects/Aircraft/EngineExtinguisher1.eff", 3F);
                        FM.EI.engines[i].tOilIn -= 20F;
                        FM.EI.engines[i].tOilOut -= 20F;
                        FM.EI.engines[i].tWaterOut -= 40F;
                        LightPointActor alightpointactor[] = (LightPointActor[])Reflection.getValue(FM.AS, "astateEngineBurnLights");
                        for(int j = 0; j < alightpointactor.length; j++)
                            if(alightpointactor[i] != null)
                            {
                                draw.lightMap().remove("_EngineBurnLight" + i);
                                alightpointactor[i].destroy();
                                alightpointactor[i] = null;
                            }

                        nextEngineFireActionTime[i] = 0L;
                        break;
                    }
        }

    }

    private boolean engineIsUp(int i)
    {
        return FM.EI.engines[i].getStage() > 0 && FM.EI.engines[i].getStage() < 7;
    }

    private void checkTankState(int i, int j)
    {
        tankStates[i] = FM.AS.astateTankStates[i];
        if(tankStates[i] != 0 && tankStates[j] == 0 || tankStates[i] == 6 && tankStates[j] < 6)
        {
            for(int k = 0; k <= 1; k++)
                if(engineIsUp(k))
                    tankSelectors[k] = j;

        } else
        if(tankStates[i] != 0 && tankStates[j] != 0 || tankStates[i] == tankStates[j])
        {
            if(!engineIsUp(0) && !engineIsUp(1))
                return;
            if(engineIsUp(0) && engineIsUp(1))
            {
                tankSelectors[0] = 0;
                tankSelectors[1] = 1;
                return;
            }
            if(Time.current() < nextTankToggleTime)
                return;
            int l = engineIsUp(0) ? 0 : 1;
            if(tankSelectors[l] == 0)
                tankSelectors[l] = 1;
            else
                tankSelectors[l] = 0;
            nextTankToggleTime = Time.current() + 30000L;
        }
    }

    protected int tankSelectors[] = {
        0, 1
    };
    protected int tankStates[];
    private long nextTankToggleTime;
    protected long nextEngineFireActionTime[];
    protected int nextEngineFireAction[];
    protected int engineStates[];
    protected static final int LEFT_ENGINE = 0;
    protected static final int RIGHT_ENGINE = 1;
    protected boolean throttleLocked;
    private float lastLockedThrust[] = {
        0.0F, 0.0F
    };
    private long lastThrustChange;

    static 
    {
        Class class1 = AR_234B1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ar 234");
        Property.set(class1, "meshName", "3DO/Plane/Ar-234B-1/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar05());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1948.8F);
        Property.set(class1, "FlightModel", "FlightModels/Ar-234B-1.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitAR_234B1.class
        });
        Property.set(class1, "LOSElevation", 1.14075F);
        Aircraft.weaponTriggersRegister(class1, new int[0]);
        Aircraft.weaponHooksRegister(class1, new String[0]);
    }
}
