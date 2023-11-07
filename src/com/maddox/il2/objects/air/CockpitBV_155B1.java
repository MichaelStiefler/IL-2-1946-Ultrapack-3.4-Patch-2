package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.GunGeneric;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.il2.objects.weapons.GunEmpty;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitBV_155B1 extends CockpitPilot
{
    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            setTmp = setOld;
            setOld = setNew;
            setNew = setTmp;
            setNew.altimeter = fm.getAltitude();
            boolean flag = fm.EI.engines[0].getStage() > 0 && fm.EI.engines[0].getStage() < 7;
            boolean flag1 = fm.EI.engines[0].getStage() == 1 || fm.EI.engines[0].getStage() == 2;
            if(flag)
            {
                if(setNew.masterPosition < 1.0F)
                    setNew.masterPosition = setOld.masterPosition + 0.1F;
                if(setNew.reviPosition > 0.0F)
                    setNew.reviPosition = setOld.reviPosition - 0.5F;
                if(setNew.fuelPump0Position < 1.0F)
                    setNew.fuelPump0Position = setOld.fuelPump0Position + 0.1F;
                if(setNew.fuelSelectorPosition > 0.0F)
                    setNew.fuelSelectorPosition = setOld.fuelSelectorPosition - 0.03F;
                if(setNew.masterSwitchPosition < 1.0F)
                    setNew.masterSwitchPosition = setOld.masterSwitchPosition + 0.1F;
                if(setNew.oxyPressurePosition < 1.0F)
                    setNew.oxyPressurePosition = setOld.oxyPressurePosition + 0.01F;
                if(setNew.circuitBreakers0Position < 1.0F)
                    setNew.circuitBreakers0Position = setOld.circuitBreakers0Position + 0.1F;
                if(setNew.generator0Position < 1.0F)
                    setNew.generator0Position = setOld.generator0Position + 0.1F;
                if(setNew.radioSwitch0Position < 1.0F)
                    setNew.radioSwitch0Position = setOld.radioSwitch0Position + 0.1F;
                if(setNew.circuitBreakers2Position < 1.0F)
                    setNew.circuitBreakers2Position = setOld.circuitBreakers2Position + 0.1F;
                if(setNew.radioSwitch2Position < 1.0F)
                    setNew.radioSwitch2Position = setOld.radioSwitch2Position + 0.1F;
                if(setNew.generator2Position < 1.0F)
                    setNew.generator2Position = setOld.generator2Position + 0.1F;
            } else
            {
                if(setNew.masterPosition > 0.0F)
                    setNew.masterPosition = setOld.masterPosition - 0.1F;
                if(setNew.reviPosition < 1.0F)
                    setNew.reviPosition = setOld.reviPosition + 0.5F;
                if(setNew.fuelPump0Position > 0.0F)
                    setNew.fuelPump0Position = setOld.fuelPump0Position - 0.1F;
                if(setNew.fuelSelectorPosition < 1.0F)
                    setNew.fuelSelectorPosition = setOld.fuelSelectorPosition + 0.03F;
                if(setNew.masterSwitchPosition > 0.0F)
                    setNew.masterSwitchPosition = setOld.masterSwitchPosition - 0.1F;
                if(setNew.oxyPressurePosition > 0.0F)
                    setNew.oxyPressurePosition = setOld.oxyPressurePosition - 0.01F;
                if(setNew.circuitBreakers0Position > 0.0F)
                    setNew.circuitBreakers0Position = setOld.circuitBreakers0Position - 0.1F;
                if(setNew.generator0Position > 0.0F)
                    setNew.generator0Position = setOld.generator0Position - 0.1F;
                if(setNew.radioSwitch0Position > 0.0F)
                    setNew.radioSwitch0Position = setOld.radioSwitch0Position - 0.1F;
                if(setNew.circuitBreakers2Position > 0.0F)
                    setNew.circuitBreakers2Position = setOld.circuitBreakers2Position - 0.1F;
                if(setNew.fuelPump2Position < 1.0F)
                    setNew.fuelPump2Position = setOld.fuelPump2Position + 0.1F;
                if(setNew.fuelPump2Position > 0.0F)
                    setNew.fuelPump2Position = setOld.fuelPump2Position - 0.1F;
                if(setNew.radioSwitch2Position > 0.0F)
                    setNew.radioSwitch2Position = setOld.radioSwitch2Position - 0.1F;
                if(setNew.generator2Position > 0.0F)
                    setNew.generator2Position = setOld.generator2Position - 0.1F;
            }
            if(flag1)
            {
                if(setNew.starterPosition < 1.0F)
                    setNew.starterPosition = setOld.starterPosition + 0.1F;
                if(setNew.starter2Position < 1.0F)
                    setNew.starter2Position = setOld.starter2Position + 0.1F;
            } else
            {
                if(setNew.starterPosition > 0.0F)
                    setNew.starterPosition = setOld.starterPosition - 0.1F;
                if(setNew.starter2Position > 0.0F)
                    setNew.starter2Position = setOld.starter2Position - 0.1F;
            }
            if(setNew.masterArmPosition < 1.0F)
                setNew.masterArmPosition = setOld.masterArmPosition + 0.5F;
            if(flag)
            {
                for(int i = 0; i < NUM_COUNTERS; i++)
                    if(!fm.CT.WeaponControl[triggerForBreechControl[i]])
                    {
                        if(setNew.shotPosition[i] < 1.0F)
                            setNew.shotPosition[i] += 0.5F;
                    } else
                    if(gun[i] != null && gun[i].haveBullets())
                    {
                        boolean flag3 = false;
                        long l = Time.current();
                        if(timePerShot[i] < l - lastBreechTime[i])
                        {
                            flag3 = true;
                            lastBreechTime[i] = l;
                        }
                        if(flag3)
                        {
                            setNew.shotPosition[i] = setNew.shotPosition[i] > 0.5F ? 0.0F : 1.0F;
                            setOld.shotPosition[i] = setNew.shotPosition[i];
                        }
                    } else
                    if(setNew.shotPosition[i] > 0.0F)
                        setNew.shotPosition[i] -= 0.5F;

            } else
            {
                for(int j = 0; j < NUM_COUNTERS; j++)
                    if(setNew.shotPosition[j] > 0.0F)
                        setNew.shotPosition[j] = setOld.shotPosition[j] - 0.5F;

            }
            if(fm.CT.GearControl == 1.0F)
            {
                if(setNew.gearLeverPosition > 0.0F)
                    setNew.gearLeverPosition = setOld.gearLeverPosition - 0.5F;
            } else
            if(setNew.gearLeverPosition < 1.0F)
                setNew.gearLeverPosition = setOld.gearLeverPosition + 0.5F;
            if(fm.CT.GearControl == 0.0F && fm.CT.getGear() > 0.5F)
            {
                if(setNew.gearUpPosition > 0.0F)
                    setNew.gearUpPosition = setOld.gearUpPosition - 0.1F;
            } else
            if(setNew.gearUpPosition < 1.0F)
                setNew.gearUpPosition = setOld.gearUpPosition + 0.1F;
            if(fm.CT.GearControl == 1.0F && fm.CT.getGear() < 0.5F)
            {
                if(setNew.gearDownPosition > 0.0F)
                    setNew.gearDownPosition = setOld.gearDownPosition - 0.1F;
            } else
            if(setNew.gearDownPosition < 1.0F)
                setNew.gearDownPosition = setOld.gearDownPosition + 0.1F;
            if(fm.CT.BayDoorControl == 0.0F)
            {
                if(setNew.kg13TriggerPosition > 0.0F)
                    setNew.kg13TriggerPosition = setOld.kg13TriggerPosition - 0.05F;
            } else
            if(setNew.kg13TriggerPosition < 1.0F)
                setNew.kg13TriggerPosition = setOld.kg13TriggerPosition + 0.05F;
            if(!fm.CT.getRadiatorControlAuto())
            {
                if(setNew.radiatorPosition > 0.0F)
                    setNew.radiatorPosition = setOld.radiatorPosition - 0.05F;
            } else
            if(setNew.radiatorPosition < 1.0F)
                setNew.radiatorPosition = setOld.radiatorPosition + 0.05F;
            if(setNew.quickBrakePosition1 < 1.0F)
                setNew.quickBrakePosition1 = setOld.quickBrakePosition1 + 0.05F;
            if(setNew.quickBrakePosition2 < 1.0F)
                setNew.quickBrakePosition2 = setOld.quickBrakePosition2 + 0.05F;
            if(!EjectCanopyState)
            {
                if(setNew.ejectSystemPosition > 0.0F)
                    setNew.ejectSystemPosition = setOld.ejectSystemPosition - 0.5F;
            } else
            if(setNew.ejectSystemPosition < 1.0F)
                setNew.ejectSystemPosition = setOld.ejectSystemPosition + 0.5F;
            if(fm.AS.bIsAboutToBailout && setNew.ejectCanopyPosition < 1.0F)
                setNew.ejectCanopyPosition = setOld.ejectCanopyPosition + 0.5F;
            if(UnLockedTState)
            {
                if(setNew.lockTailWheel0Position > 0.0F)
                    setNew.lockTailWheel0Position = setOld.lockTailWheel0Position - 0.07F;
            } else
            if(setNew.lockTailWheel0Position < 1.0F)
                setNew.lockTailWheel0Position = setOld.lockTailWheel0Position + 0.07F;
            if(!LockedTDelayState)
            {
                if(setNew.lockTailWheelPosition1 > 0.0F)
                    setNew.lockTailWheelPosition1 = setOld.lockTailWheelPosition1 - 0.07F;
            } else
            if(setNew.lockTailWheelPosition1 < 1.0F)
                setNew.lockTailWheelPosition1 = setOld.lockTailWheelPosition1 + 0.07F;
            if(fm.CT.cockpitDoorControl == 1.0F)
            {
                if(setNew.topCablePosition1 > 0.0F)
                    setNew.topCablePosition1 = setOld.topCablePosition1 - 0.022F;
            } else
            if(setNew.topCablePosition1 < 1.0F)
                setNew.topCablePosition1 = setOld.topCablePosition1 + 0.022F;
            if(fm.getAltitude() > 3000F)
            {
                if(setNew.oxyButtonPosition < 1.0F)
                    setNew.oxyButtonPosition = setOld.oxyButtonPosition + 0.05F;
            } else
            if(setNew.oxyButtonPosition > 0.0F)
                setNew.oxyButtonPosition = setOld.oxyButtonPosition - 0.05F;
            if(fm.getAltitude() > 5000F)
            {
                if(setNew.glassCleanerPosition < 1.0F)
                    setNew.glassCleanerPosition = setOld.glassCleanerPosition + 0.05F;
            } else
            if(setNew.glassCleanerPosition > 0.0F)
                setNew.glassCleanerPosition = setOld.glassCleanerPosition - 0.05F;
            if(!CanopyClosedState)
            {
                if(setNew.canopyHandlePosition > 0.0F)
                    setNew.canopyHandlePosition = setOld.canopyHandlePosition - 0.05F;
            } else
            if(setNew.canopyHandlePosition < 1.0F)
                setNew.canopyHandlePosition = setOld.canopyHandlePosition + 0.05F;
            if(UnLockedTState)
            {
                if(setNew.lockTailWheel3Position > 0.0F)
                    setNew.lockTailWheel3Position = setOld.lockTailWheel3Position - 0.07F;
            } else
            if(setNew.lockTailWheel3Position < 1.0F)
                setNew.lockTailWheel3Position = setOld.lockTailWheel3Position + 0.07F;
            if(UnLockedTState)
            {
                if(setNew.lockTailWheelPosition2 > 0.0F)
                    setNew.lockTailWheelPosition2 = setOld.lockTailWheelPosition2 - 0.07F;
            } else
            if(setNew.lockTailWheelPosition2 < 1.0F)
                setNew.lockTailWheelPosition2 = setOld.lockTailWheelPosition2 + 0.07F;
            if(UnLockedTState)
            {
                if(setNew.lockTailWheel5Position > 0.0F)
                    setNew.lockTailWheel5Position = setOld.lockTailWheel5Position - 0.07F;
            } else
            if(setNew.lockTailWheel5Position < 1.0F)
                setNew.lockTailWheel5Position = setOld.lockTailWheel5Position + 0.07F;
            if(!cockpitLightControl)
            {
                if(setNew.cockpitLights0Position > 0.0F)
                    setNew.cockpitLights0Position = setOld.cockpitLights0Position - 0.1F;
            } else
            if(setNew.cockpitLights0Position < 1.0F)
                setNew.cockpitLights0Position = setOld.cockpitLights0Position + 0.1F;
            if(!fm.AS.bNavLightsOn)
            {
                if(setNew.navLights0Position > 0.0F)
                    setNew.navLights0Position = setOld.navLights0Position - 0.1F;
            } else
            if(setNew.navLights0Position < 1.0F)
                setNew.navLights0Position = setOld.navLights0Position + 0.1F;
            if(fm.EI.engines[0].isPropAngleDeviceOperational())
            {
                if(setNew.propPitchPosition < 1.0F)
                    setNew.propPitchPosition = setOld.propPitchPosition + 0.1F;
                if(setNew.propPitch4Position < 1.0F)
                    setNew.propPitch4Position = setOld.propPitch4Position + 0.1F;
            } else
            {
                if(setNew.propPitchPosition > 0.0F)
                    setNew.propPitchPosition = setOld.propPitchPosition - 0.1F;
                if(setNew.propPitch4Position > 0.0F)
                    setNew.propPitch4Position = setOld.propPitch4Position - 0.1F;
            }
            if(!cockpitLightControl)
            {
                if(setNew.cockpitLights2Position > 0.0F)
                    setNew.cockpitLights2Position = setOld.cockpitLights2Position - 0.1F;
            } else
            if(setNew.cockpitLights2Position < 1.0F)
                setNew.cockpitLights2Position = setOld.cockpitLights2Position + 0.1F;
            if(!fm.AS.bNavLightsOn)
            {
                if(setNew.navLights2Position > 0.0F)
                    setNew.navLights2Position = setOld.navLights2Position - 0.1F;
            } else
            if(setNew.navLights2Position < 1.0F)
                setNew.navLights2Position = setOld.navLights2Position + 0.1F;
            if(!AccelerationState1)
            {
                if(setNew.cockpit0Position > 0.0F)
                    setNew.cockpit0Position = setOld.cockpit0Position - 0.02F;
            } else
            if(setNew.cockpit0Position < 1.0F)
                setNew.cockpit0Position = setOld.cockpit0Position + 0.02F;
            if(fm.AS.astateSootStates[0] > 0)
            {
                if(setNew.smoke1Position < 1.0F)
                    setNew.smoke1Position = setOld.smoke1Position + 1E-005F;
                if(setNew.smoke2Position < 1.0F)
                    setNew.smoke2Position = setOld.smoke2Position + 2E-005F;
            } else
            {
                if(setNew.smoke1Position > 0.0F)
                    setNew.smoke1Position = setOld.smoke1Position - 1E-005F;
                if(setNew.smoke2Position > 0.0F)
                    setNew.smoke2Position = setOld.smoke2Position - 2E-005F;
            }
            if(fm.EI.engines[0].getReadyness() < 0.8F)
            {
                if(setNew.smoke3Position < 1.0F)
                    setNew.smoke3Position = setOld.smoke3Position + 1E-005F;
                if(setNew.smoke4Position < 1.0F)
                    setNew.smoke4Position = setOld.smoke4Position + 2E-005F;
            } else
            {
                if(setNew.smoke3Position > 0.0F)
                    setNew.smoke3Position = setOld.smoke3Position - 1E-005F;
                if(setNew.smoke4Position > 0.0F)
                    setNew.smoke4Position = setOld.smoke4Position - 2E-005F;
            }
            if(fm.CT.GearControl == 1.0F)
            {
                if(setNew.gearIndicatorPosition > 0.0F)
                    setNew.gearIndicatorPosition = setOld.gearIndicatorPosition - 0.005F;
            } else
            if(setNew.gearIndicatorPosition < 1.0F)
                setNew.gearIndicatorPosition = setOld.gearIndicatorPosition + 0.005F;
            boolean flag2 = false;
            if(((Aircraft)fm.actor).thisWeaponsName.toLowerCase().startsWith("u5") && fm.CT.Weapons[9][0].haveBullets())
                flag2 = true;
            if(flag2)
            {
                if(setNew.dropTankPosition < 1.0F)
                    setNew.dropTankPosition = setOld.dropTankPosition + 0.1F;
            } else
            if(setNew.dropTankPosition > 0.0F)
                setNew.dropTankPosition = setOld.dropTankPosition - 0.1F;
            if(!GunsightMove)
            {
                if(setNew.revi16SupportPosition > 0.0F)
                    setNew.revi16SupportPosition = setOld.revi16SupportPosition - 0.05F;
            } else
            if(setNew.revi16SupportPosition < 1.0F)
                setNew.revi16SupportPosition = setOld.revi16SupportPosition + 0.05F;
            if(!RotGunsightDelay)
            {
                if(setNew.revi16Support2Position > 0.0F)
                    setNew.revi16Support2Position = setOld.revi16Support2Position - 0.05F;
            } else
            if(setNew.revi16Support2Position < 1.0F)
                setNew.revi16Support2Position = setOld.revi16Support2Position + 0.05F;
            if(fm.EI.engines[0].getControlAfterburner())
            {
                if(setNew.mw50Position < 1.0F)
                    setNew.mw50Position = setOld.mw50Position + 0.5F;
            } else
            if(setNew.mw50Position > 0.0F)
                setNew.mw50Position = setOld.mw50Position - 0.5F;
            if(!RotGunsightDelay)
            {
                if(setNew.revi16Support3Position > 0.0F)
                    setNew.revi16Support3Position = setOld.revi16Support3Position - 0.05F;
            } else
            if(setNew.revi16Support3Position < 1.0F)
                setNew.revi16Support3Position = setOld.revi16Support3Position + 0.05F;
            if(fm.CT.cockpitDoorControl == 1.0F)
            {
                if(setNew.topCablePosition2 > 0.0F)
                    setNew.topCablePosition2 = setOld.topCablePosition2 - 0.022F;
            } else
            if(setNew.topCablePosition2 < 1.0F)
                setNew.topCablePosition2 = setOld.topCablePosition2 + 0.022F;
            if(fm.EI.engines[0].getControlAfterburner() && setNew.mw50CountPosition < 1.0F)
                setNew.mw50CountPosition += 5E-005F;
            boolean flag4 = false;
            if(((Aircraft)fm.actor).thisWeaponsName.toLowerCase().startsWith("r1") && fm.CT.Weapons[3][0].haveBullets())
                flag4 = true;
            if(flag4)
            {
                if(setNew.bombsContact1Position < 1.0F)
                    setNew.bombsContact1Position = setOld.bombsContact1Position + 0.5F;
                if(setNew.bombsContact2Position < 1.0F)
                    setNew.bombsContact2Position = setOld.bombsContact2Position + 0.5F;
                if(setNew.bombsPanel1Position < 1.0F)
                    setNew.bombsPanel1Position = setOld.bombsPanel1Position + 1.0F;
            } else
            if(setNew.bombsPanel1Position > 0.0F)
                setNew.bombsPanel1Position = setOld.bombsPanel1Position - 1.0F;
            if(setNew.leftPedalBase1Position < 1.0F)
                setNew.leftPedalBase1Position = setOld.leftPedalBase1Position + 1.0F;
            if(setNew.rightPedalBase1Position < 1.0F)
                setNew.rightPedalBase1Position = setOld.rightPedalBase1Position + 1.0F;
            if(!cockpitDimControl)
            {
                if(setNew.dimPosition > 0.0F)
                    setNew.dimPosition = setOld.dimPosition - 0.05F;
            } else
            if(setNew.dimPosition < 1.0F)
                setNew.dimPosition = setOld.dimPosition + 0.05F;
            setNew.throttle = (10F * setOld.throttle + fm.CT.PowerControl) / 11F;
            setNew.azimuth = fm.Or.getYaw();
            if(setOld.azimuth > 270F && setNew.azimuth < 90F)
                setOld.azimuth -= 360F;
            if(setOld.azimuth < 90F && setNew.azimuth > 270F)
                setOld.azimuth += 360F;
            setNew.waypointAzimuth = (10F * setOld.waypointAzimuth + (waypointAzimuth() - setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
            buzzerFX(fm.CT.getGear() < 0.999999F && fm.CT.getFlap() > 0.1F);
            return true;
        }

        Interpolater()
        {
        }
    }

    private class Variables
    {

        float altimeter;
        float throttle;
        float dimPosition;
        float azimuth;
        float waypointAzimuth;
        float masterPosition;
        float starterPosition;
        float starter2Position;
        float reviPosition;
        float masterArmPosition;
        float shotPosition[];
        float gearLeverPosition;
        float gearUpPosition;
        float gearDownPosition;
        float kg13TriggerPosition;
        float radiatorPosition;
        float fuelPump0Position;
        float quickBrakePosition1;
        float quickBrakePosition2;
        float fuelSelectorPosition;
        float oilContactPosition;
        float ejectSystemPosition;
        float primer0Position;
        float primer1Position;
        float primer2Position;
        float primer3Position;
        float primer4Position;
        float primer5Position;
        float ejectCanopyPosition;
        float lockTailWheel0Position;
        float lockTailWheelPosition1;
        float masterSwitchPosition;
        float topCablePosition1;
        float oxyButtonPosition;
        float oxyPressurePosition;
        float glassCleanerPosition;
        float canopyHandlePosition;
        float lockTailWheel3Position;
        float lockTailWheelPosition2;
        float lockTailWheel5Position;
        float circuitBreakers0Position;
        float cockpitLights0Position;
        float navLights0Position;
        float propPitchPosition;
        float generator0Position;
        float radioSwitch0Position;
        float circuitBreakers2Position;
        float cockpitLights2Position;
        float navLights2Position;
        float fuelPump2Position;
        float propPitch4Position;
        float radioSwitch2Position;
        float generator2Position;
        float cockpit0Position;
        float smoke1Position;
        float smoke2Position;
        float smoke3Position;
        float smoke4Position;
        float gearIndicatorPosition;
        float dropTankPosition;
        float revi16SupportPosition;
        float revi16Support2Position;
        float mw50Position;
        float revi16Support3Position;
        float topCablePosition2;
        float mw50CountPosition;
        float bombsContact1Position;
        float bombsPanel1Position;
        float bombsContact2Position;
        float leftPedalBase1Position;
        float rightPedalBase1Position;
        float leftPedalBase2Position;
        float rightPedalBase2Position;

        public Variables()
        {
            shotPosition = new float[NUM_COUNTERS];
        }
    }

    protected float waypointAzimuth()
    {
        WayPoint waypoint = fm.AP.way.curr();
        if(waypoint == null)
        {
            return 0.0F;
        } else
        {
            waypoint.getP(tmpP);
            tmpV.sub(tmpP, fm.Loc);
            return (float)(Math.toDegrees(Math.atan2(tmpV.y, tmpV.x)));
        }
    }

    public CockpitBV_155B1()
    {
        super("3do/cockpit/BV-155B1-P/hier.him", "bf109");
        setOld = new Variables();
        setNew = new Variables();
        oldctl = -1F;
        curctl = -1F;
        AccelerationState1 = false;
        IceState1 = false;
        IceState2 = false;
        IceState3 = false;
        CleanIceState1 = false;
        CanopyClosedState = false;
        EjectCanopyState = false;
        timeCounterIce1 = 0.0F;
        timeIce1 = 60F;
        timeCounterIce2 = 0.0F;
        timeIce2 = 120F;
        timeCounterIce3 = 0.0F;
        timeIce3 = 180F;
        timeCounterCleanIce1 = 0.0F;
        timeCleanIce1 = 60F;
        LockedTDelayState = false;
        UnLockedTState = false;
        timeCounterLockT = 0.0F;
        timeLockT = 10F;
        timeCounterUnLockT = 0.0F;
        timeUnLockT = 10F;
        GunsightMove = false;
        RotGunsightDelay = false;
        timeCounterGunsightMove2 = 0.0F;
        timeGunsightMove2 = 20F;
        ReticleCut = false;
        timeCounterReticle = 0.0F;
        timeReticle = 34F;
        pictManifold = 0.0F;
        setOld = new Variables();
        setNew = new Variables();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        tmpP = new Point3d();
        tmpV = new Vector3d();
        setNew.dimPosition = 1.0F;
        HookNamed hooknamed = new HookNamed(mesh, "LAMPHOOK1");
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light1 = new LightPointActor(new LightPoint(), loc.getPoint());
        light1.light.setColor(126F, 232F, 245F);
        light1.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK1", light1);
        hooknamed = new HookNamed(mesh, "LAMPHOOK2");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light2 = new LightPointActor(new LightPoint(), loc.getPoint());
        light2.light.setColor(126F, 232F, 245F);
        light2.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK2", light2);
        this.cockpitNightMats = (new String[] {
            "ZClocks1", "ZClocksDMG", "ZClocks2", "ZClocks3", "Needles", "ZClocks4"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
        loadBuzzerFX();
        bNeedSetUp = true;
        hasCanopy = true;
        interpPut(new Interpolater(), null, Time.current(), null);
        loadBuzzerFX();
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        boolean flag = fm.EI.engines[0].getStage() > 0 && fm.EI.engines[0].getStage() < 7;
        boolean flag1 = false;
        if(((Aircraft)fm.actor).thisWeaponsName.toLowerCase().startsWith("r1"))
            flag1 = true;
        boolean flag2 = false;
        if(((Aircraft)fm.actor).thisWeaponsName.toLowerCase().startsWith("r1") && fm.CT.Weapons[3][0].haveBullets())
            flag2 = true;
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.73F);
        this.mesh.chunkSetLocate("Top", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_TrimIndicator", 330F * fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TrimWheel", 720F * fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_LeftPedal2", -25F * fm.CT.getBrake(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_RightPedal2", -25F * fm.CT.getBrake(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_FlapsWheel", -360F * fm.CT.FlapsControl, 0.0F, 0.0F);
        mesh.chunkSetAngles("Revi16Tinter", 0.0F, cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, 45F), 0.0F);
        mesh.chunkSetAngles("Starter", 0.0F, cvt(interp(setNew.starterPosition, setOld.starterPosition, f), 0.0F, 1.0F, 0.0F, 45F), 0.0F);
        mesh.chunkSetAngles("Revi16Contact", 0.0F, cvt(interp(setNew.reviPosition, setOld.reviPosition, f), 0.0F, 1.0F, 0.0F, -46F), 0.0F);
        mesh.chunkSetAngles("Z_MasterArm", 0.0F, cvt(interp(setNew.masterArmPosition, setOld.masterArmPosition, f), 0.0F, 1.0F, 0.0F, -35F), 0.0F);
        for(int i = 0; i < 3; i++)
            mesh.chunkSetAngles("Z_Shot" + (i + 1), 0.0F, cvt(interp(setNew.shotPosition[i], setOld.shotPosition[i], f), 0.0F, 1.0F, 0.0F, -60F), 0.0F);

        mesh.chunkSetAngles("Z_GearLever", 0.0F, cvt(interp(setNew.gearLeverPosition, setOld.gearLeverPosition, f), 0.0F, 1.0F, 0.0F, -65F), 0.0F);
        mesh.chunkSetAngles("Z_KG13Trigger", 0.0F, cvt(interp(setNew.kg13TriggerPosition, setOld.kg13TriggerPosition, f), 0.0F, 1.0F, 0.0F, -270F), 0.0F);
        mesh.chunkSetAngles("Z_RadiatorSelector", 0.0F, cvt(interp(setNew.radiatorPosition, setOld.radiatorPosition, f), 0.0F, 1.0F, 0.0F, -90F), 0.0F);
        mesh.chunkSetAngles("Z_RadiatorSelector2", cvt(fm.CT.getRadiatorControl(), 0.0F, 1.0F, -180F, 0.0F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_QuickBrake", 0.0F, cvt(interp(setNew.quickBrakePosition1, setOld.quickBrakePosition1, f), 0.0F, 1.0F, 0.0F, 38F), 0.0F);
        mesh.chunkSetAngles("Z_QuickBrake2", 0.0F, cvt(interp(setNew.quickBrakePosition2, setOld.quickBrakePosition2, f), 0.0F, 1.0F, 0.0F, -15F), 0.0F);
        mesh.chunkSetAngles("Z_FuelSelector", 0.0F, cvt(interp(setNew.fuelSelectorPosition, setOld.fuelSelectorPosition, f), 0.0F, 1.0F, 0.0F, -58F), 0.0F);
        mesh.chunkSetAngles("Z_OilContact", 0.0F, cvt(interp(setNew.oilContactPosition, setOld.oilContactPosition, f), 0.0F, 1.0F, 0.0F, -45F), 0.0F);
        mesh.chunkSetAngles("Z_EjectSystem", 0.0F, cvt(interp(setNew.ejectSystemPosition, setOld.ejectSystemPosition, f), 0.0F, 1.0F, 0.0F, -42F), 0.0F);
        mesh.chunkSetAngles("Z_LockTWheel2", 0.0F, cvt(interp(setNew.lockTailWheelPosition1, setOld.lockTailWheelPosition1, f), 0.0F, 1.0F, 0.0F, 135F), 0.0F);
        mesh.chunkSetAngles("Z_LockTWheel4", 0.0F, cvt(interp(setNew.lockTailWheelPosition2, setOld.lockTailWheelPosition2, f), 0.0F, 1.0F, 0.0F, -25F), 0.0F);
        mesh.chunkSetAngles("Z_TopCable", 0.0F, cvt(interp(setNew.topCablePosition1, setOld.topCablePosition1, f), 0.0F, 1.0F, 0.0F, 75F), 0.0F);
        mesh.chunkSetAngles("Z_TopCable2", 0.0F, cvt(interp(setNew.topCablePosition2, setOld.topCablePosition2, f), 0.0F, 1.0F, 0.0F, -80F), 0.0F);
        mesh.chunkSetAngles("Z_OxyButton", 0.0F, cvt(interp(setNew.oxyButtonPosition, setOld.oxyButtonPosition, f), 0.0F, 1.0F, 0.0F, 180F), 0.0F);
        mesh.chunkSetAngles("Z_OxyPressure", 0.0F, cvt(interp(setNew.oxyPressurePosition, setOld.oxyPressurePosition, f), 0.0F, 1.0F, -258F, 0.0F), 0.0F);
        mesh.chunkSetAngles("Z_GlassCleaner", 0.0F, cvt(interp(setNew.glassCleanerPosition, setOld.glassCleanerPosition, f), 0.0F, 1.0F, 0.0F, -88F), 0.0F);
        mesh.chunkSetAngles("Z_CanopyHandle", 0.0F, cvt(interp(setNew.canopyHandlePosition, setOld.canopyHandlePosition, f), 0.0F, 1.0F, 0.0F, 120F), 0.0F);
        mesh.chunkSetAngles("Smoke1", 0.0F, cvt(interp(setNew.smoke1Position, setOld.smoke1Position, f), 0.0F, 1.0F, 0.0F, 18000F), 0.0F);
        mesh.chunkSetAngles("Smoke2", 0.0F, cvt(interp(setNew.smoke2Position, setOld.smoke2Position, f), 0.0F, 1.0F, 0.0F, 18000F), 0.0F);
        mesh.chunkSetAngles("Smoke3", 0.0F, cvt(interp(setNew.smoke3Position, setOld.smoke3Position, f), 0.0F, 1.0F, 0.0F, 18000F), 0.0F);
        mesh.chunkSetAngles("Smoke4", 0.0F, cvt(interp(setNew.smoke4Position, setOld.smoke4Position, f), 0.0F, 1.0F, 0.0F, 18000F), 0.0F);
        mesh.chunkSetAngles("Revi16Support1", 0.0F, cvt(interp(setNew.revi16SupportPosition, setOld.revi16SupportPosition, f), 0.0F, 1.0F, 0.0F, 91F), 0.0F);
        mesh.chunkSetAngles("Z_MW50", 0.0F, cvt(interp(setNew.mw50Position, setOld.mw50Position, f), 0.0F, 1.0F, 0.0F, -40F), 0.0F);
        mesh.chunkSetAngles("Z_MW50Count", 0.0F, cvt(interp(setNew.mw50CountPosition, setOld.mw50CountPosition, f), 0.0F, 1.0F, 0.0F, 300F), 0.0F);
        mesh.chunkSetAngles("Z_Altimeter1", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Altimeter2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 10000F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_ATA1", cvt(pictManifold = 0.75F * pictManifold + 0.25F * fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 329F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Speedometer1", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 0.0F, 800F, 0.0F, 16F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_RPM1", floatindex(cvt(fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_FuelQuantity1", -44.5F + floatindex(cvt(fm.M.fuel / 0.72F, 0.0F, 400F, 0.0F, 8F), fuelScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_EngTemp1", cvt(fm.EI.engines[0].tOilOut, 0.0F, 160F, 0.0F, 58.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_FuelPress1", cvt(fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 3F, 0.0F, 135F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_OilPress1", cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Compass1", 0.0F, interp(setNew.azimuth, setOld.azimuth, f), 0.0F);
        if(fm.EI.engines[0].getStage() == 6)
        {
            mesh.chunkSetAngles("Z_Azimuth1", -interp(setNew.waypointAzimuth, setOld.waypointAzimuth, f), 0.0F, 0.0F);
            float f1 = cvt((setNew.azimuth - setOld.azimuth) / Time.tickLenFs(), -5F, 5F, 18F, -18F);
            if(aircraft().fmTrack() != null)
                aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
            mesh.chunkSetAngles("Z_TurnBank1", f1, 0.0F, 0.0F);
            mesh.chunkSetAngles("Z_Horizon1", 0.0F, 0.0F, fm.Or.getKren());
            mesh.chunkSetAngles("Z_Horizon2", cvt(fm.Or.getTangage(), -45F, 45F, -13F, 13F), 0.0F, 0.0F);
        }
        mesh.chunkSetAngles("Z_TurnBank2", -cvt(getBall(6D), -6F, 6F, -4.5F, 4.5F), 0.0F, 0.0F);
        if(flag)
        {
            mesh.chunkSetAngles("Z_PropPitch1", 270F - (float)Math.toDegrees(fm.EI.engines[0].getPropPhi() - fm.EI.engines[0].getPropPhiMin()) * 60F, 0.0F, 0.0F);
            mesh.chunkSetAngles("Z_PropPitch2", 105F - (float)Math.toDegrees(fm.EI.engines[0].getPropPhi() - fm.EI.engines[0].getPropPhiMin()) * 5F, 0.0F, 0.0F);
        }
        mesh.chunkVisible("BombsPanel1", flag1);
        mesh.chunkVisible("BombsPanel2", flag1);
        mesh.chunkVisible("BombsPanel3", flag1);
        mesh.chunkVisible("Z_BombsContact1", flag1);
        mesh.chunkVisible("Z_BombsContact2", flag1);
        mesh.chunkVisible("Z_BombsSelector", flag1);
        mesh.chunkVisible("RocketsPanel", false);
        mesh.chunkVisible("Z_RFire1", false);
        mesh.chunkVisible("Z_RFire2", false);
        if(GunsightMove)
        {
            this.cockpitDimControl = true;
            mesh.chunkVisible("Revi16Cable2", false);
        } else
        if(!GunsightMove)
            mesh.chunkVisible("Revi16Cable2", true);
        if(fm.getAltitude() > 5000F && !CleanIceState1)
        {
            timeCounterIce1 += f;
            timeCounterIce2 += f;
            timeCounterIce3 += f;
            if(timeCounterIce1 >= timeIce1)
            {
                timeCounterIce1 = 0.0F;
                timeIce1 = 0.0F;
                IceState1 = true;
            }
            if(timeCounterIce2 >= timeIce2)
            {
                timeCounterIce2 = 0.0F;
                timeIce2 = 0.0F;
                IceState2 = true;
            }
            if(timeCounterIce3 >= timeIce3)
            {
                timeCounterIce3 = 0.0F;
                timeIce3 = 0.0F;
                IceState3 = true;
            }
        } else
        if(fm.getAltitude() <= 300F || CleanIceState1)
        {
            timeCounterIce1 = 0.0F;
            timeIce1 = 60F;
            timeCounterIce2 = 0.0F;
            timeIce2 = 120F;
            timeCounterIce3 = 0.0F;
            timeIce3 = 180F;
        }
        if(fm.AS.astateEngineStates[0] == 1)
            mesh.chunkVisible("Smoke1", true);
        else
            mesh.chunkVisible("Smoke1", false);
        if(fm.AS.astateEngineStates[0] == 2)
            mesh.chunkVisible("Smoke2", true);
        else
            mesh.chunkVisible("Smoke2", false);
        if(fm.AS.astateEngineStates[0] == 3)
            mesh.chunkVisible("Smoke3", true);
        else
            mesh.chunkVisible("Smoke3", false);
        if(fm.AS.astateEngineStates[0] > 3)
            mesh.chunkVisible("Smoke4", true);
        else
            mesh.chunkVisible("Smoke4", false);
        if(aircraft().chunkDamageVisible("CF") > 0)
            mesh.chunkVisible("Z_HitArmor1", true);
        else
            mesh.chunkVisible("Z_HitArmor1", false);
        if(aircraft().chunkDamageVisible("Cockpit") > 0)
            mesh.chunkVisible("Z_HitArmor2", true);
        else
            mesh.chunkVisible("Z_HitArmor2", false);
        mesh.chunkVisible("Z_Blood", false);
        if(IceState1)
        {
            mesh.chunkVisible("GlassIce1", true);
            mesh.chunkVisible("TopIce1", true);
        } else
        if(!IceState1)
        {
            mesh.chunkVisible("GlassIce1", false);
            mesh.chunkVisible("TopIce1", false);
        }
        if(IceState2)
        {
            mesh.chunkVisible("GlassIce2", true);
            mesh.chunkVisible("TopIce2", true);
        } else
        if(!IceState2)
        {
            mesh.chunkVisible("GlassIce2", false);
            mesh.chunkVisible("TopIce2", false);
        }
        if(IceState3)
        {
            mesh.chunkVisible("GlassIce3", true);
            mesh.chunkVisible("TopIce3", true);
        } else
        if(!IceState3)
        {
            mesh.chunkVisible("GlassIce3", false);
            mesh.chunkVisible("TopIce3", false);
        }
        if(fm.getAltitude() > 5000F)
        {
            timeCounterCleanIce1 += f;
            if(timeCounterCleanIce1 >= timeCleanIce1)
            {
                timeCounterCleanIce1 = 0.0F;
                timeCleanIce1 = 0.0F;
                CleanIceState1 = true;
            }
        } else
        {
            timeCounterCleanIce1 = 0.0F;
            timeCounterCleanIce1 = 60F;
        }
        if(CleanIceState1)
        {
            IceState1 = false;
            IceState2 = false;
            IceState3 = false;
        }
        mesh.chunkVisible("Z_FuelTube", false);
        if(fm.CT.getCockpitDoor() == 1.0F)
            mesh.chunkVisible("Z_TopCable2", false);
        else
            mesh.chunkVisible("Z_TopCable2", false);
        timeCounterReticle += f;
        RotGunsightDelay = false;
        timeCounterGunsightMove2 += f;
        if(timeCounterReticle > timeReticle)
        {
            timeCounterReticle = 0.0F;
            ReticleCut = false;
        }
        if(timeCounterGunsightMove2 > timeGunsightMove2)
        {
            timeCounterGunsightMove2 = 0.0F;
            GunsightMove = false;
        }
        if(fm.Gears.bTailwheelLocked)
        {
            timeCounterLockT += f;
            if(timeCounterLockT > timeLockT)
            {
                timeCounterLockT = 0.0F;
                LockedTDelayState = true;
            }
        } else
        {
            timeCounterLockT = 0.0F;
            LockedTDelayState = false;
        }
        if(!fm.Gears.bTailwheelLocked)
        {
            timeCounterUnLockT += f;
            if(timeCounterUnLockT > timeUnLockT)
            {
                timeCounterUnLockT = 0.0F;
                UnLockedTState = true;
            }
        } else
        {
            timeCounterUnLockT = 0.0F;
            UnLockedTState = false;
        }
        if(fm.CT.getCockpitDoor() == 0.0F)
            CanopyClosedState = true;
        else
            CanopyClosedState = false;
        if(!fm.CT.getRadiatorControlAuto())
            mesh.chunkVisible("Z_RadiatorSelector", false);
        else
            mesh.chunkVisible("Z_RadiatorSelector", true);
        if(fm.CT.getRadiatorControlAuto())
            mesh.chunkVisible("Z_RadiatorSelector2", false);
        else
            mesh.chunkVisible("Z_RadiatorSelector2", true);
        if(flag)
        {
            if(fm.M.fuel < 36F)
                mesh.chunkVisible("Z_FuelWarning1", true);
            else
                mesh.chunkVisible("Z_FuelWarning1", false);
            if(fm.CT.getGear() == 0.0F)
            {
                mesh.chunkVisible("Z_GearLRed1", true);
                mesh.chunkVisible("Z_GearRRed1", true);
                mesh.chunkVisible("Z_GearEin", false);
            } else
            {
                mesh.chunkVisible("Z_GearLRed1", false);
                mesh.chunkVisible("Z_GearRRed1", false);
                mesh.chunkVisible("Z_GearEin", true);
            }
            if(fm.CT.getGear() == 1.0F)
            {
                mesh.chunkVisible("Z_GearLGreen1", true);
                mesh.chunkVisible("Z_GearRGreen1", true);
                mesh.chunkVisible("Z_GearAus", false);
            } else
            {
                mesh.chunkVisible("Z_GearLGreen1", false);
                mesh.chunkVisible("Z_GearRGreen1", false);
                mesh.chunkVisible("Z_GearAus", true);
            }
            if(flag2)
                mesh.chunkVisible("Z_BombsLight", true);
        } else
        {
            mesh.chunkVisible("Z_FuelWarning1", false);
            mesh.chunkVisible("Z_GearLRed1", false);
            mesh.chunkVisible("Z_GearRRed1", false);
            mesh.chunkVisible("Z_GearLGreen1", false);
            mesh.chunkVisible("Z_GearRGreen1", false);
            mesh.chunkVisible("Z_GearEin", true);
            mesh.chunkVisible("Z_GearAus", true);
            mesh.chunkVisible("Z_BombsLight", false);
        }
        if(gun[0] != null)
            mesh.chunkSetAngles("Z_AmmoCounter1", cvt(gun[0].countBullets(), 0.0F, ammoCounterMax[0], 15F, 0.0F), 0.0F, 0.0F);
        if(gun[1] != null)
            mesh.chunkSetAngles("Z_AmmoCounter2", cvt(gun[1].countBullets(), 0.0F, ammoCounterMax[1], 15F, 0.0F), 0.0F, 0.0F);
        if(gun[2] != null)
            mesh.chunkSetAngles("Z_AmmoCounter3", cvt(gun[2].countBullets(), 0.0F, ammoCounterMax[2], 15F, 0.0F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Second1", cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_KG13a", (pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl) * 15F, 0.0F, (pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl) * 9F);
        if((fm.AS.astateCockpitState & 8) == 0)
            mesh.chunkSetAngles("Z_Throttle", interp(setNew.throttle, setOld.throttle, f) * 68.18182F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_MagnetoSwitch", -45F + 28.333F * (float)fm.EI.engines[0].getControlMagnetos(), 0.0F, 0.0F);
        if(flag)
        {
            for(int j = 3; j < MAX_GUNS; j++)
            {
                String s = j == 3 ? "Z_WhiteLampL" : "Z_WhiteLampR";
                if(gun[j] instanceof GunEmpty)
                    mesh.chunkVisible(s, false);
                else
                if(fm.CT.WeaponControl[triggerForBreechControl[j]])
                {
                    if(gun[j] != null && gun[j].haveBullets())
                    {
                        boolean flag3 = false;
                        long l = Time.current();
                        if(timePerShot[j] < l - lastBreechTime[j])
                        {
                            flag3 = true;
                            lastBreechTime[j] = l;
                        }
                        if(flag3)
                            mesh.chunkVisible(s, !mesh.isChunkVisible(s));
                    } else
                    {
                        mesh.chunkVisible(s, false);
                    }
                } else
                {
                    mesh.chunkVisible(s, true);
                }
            }

        }
        if(fm.AS.astateBailoutStep > 3)
            removeCanopy();
        if(fm.AS.bIsAboutToBailout)
            EjectCanopyState = true;
        if(!flag)
            setNightMats(false);
        if(flag && !ReticleCut)
            mesh.chunkVisible("Z_Z_RETICLE", true);
        else
            mesh.chunkVisible("Z_Z_RETICLE", false);
        if(curctl == -1F)
        {
            curctl = oldctl = fm.EI.engines[0].getControlThrottle();
        } else
        {
            curctl = fm.EI.engines[0].getControlThrottle();
            if(curctl > 0.5F && (curctl - oldctl) / f > 0.0F && fm.EI.engines[0].getRPM() > 300F && fm.getSpeedKMH() > 0.0F && fm.getSpeedKMH() <= 90F && fm.EI.engines[0].getStage() == 6)
                AccelerationState1 = true;
            oldctl = curctl;
        }
        if(fm.getSpeedKMH() > 100F)
            AccelerationState1 = false;
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(interp(setNew.masterPosition, setOld.masterPosition, f), 0.0F, 1.0F, 0.0F, -0.005F);
        mesh.chunkSetLocate("Z_Master", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.starter2Position, setOld.starter2Position, f), 0.0F, 1.0F, 0.0F, 0.02F);
        mesh.chunkSetLocate("Starter2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.gearUpPosition, setOld.gearUpPosition, f), 0.0F, 1.0F, 0.0F, 0.013F);
        mesh.chunkSetLocate("Z_GearUp", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.gearDownPosition, setOld.gearDownPosition, f), 0.0F, 1.0F, 0.0F, 0.013F);
        mesh.chunkSetLocate("Z_GearDown", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_RightPedal", -fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        Cockpit.xyz[1] = cvt(fm.CT.getRudder(), -1F, 1.0F, -0.045F, 0.045F);
        mesh.chunkSetLocate("Z_LeftPedal", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] *= -1F;
        mesh.chunkSetLocate("Z_RightPedal", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.fuelPump0Position, setOld.fuelPump0Position, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_FuelPomp", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.primer0Position, setOld.primer0Position, f), 0.0F, 1.0F, 0.0F, 0.035F);
        mesh.chunkSetLocate("Z_Primer", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.primer1Position, setOld.primer1Position, f), 0.0F, 1.0F, 0.0F, -0.035F);
        mesh.chunkSetLocate("Z_Primer1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.primer2Position, setOld.primer2Position, f), 0.0F, 1.0F, 0.0F, 0.035F);
        mesh.chunkSetLocate("Z_Primer2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.primer3Position, setOld.primer3Position, f), 0.0F, 1.0F, 0.0F, -0.035F);
        mesh.chunkSetLocate("Z_Primer3", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.primer4Position, setOld.primer4Position, f), 0.0F, 1.0F, 0.0F, 0.035F);
        mesh.chunkSetLocate("Z_Primer4", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.primer5Position, setOld.primer5Position, f), 0.0F, 1.0F, 0.0F, -0.035F);
        mesh.chunkSetLocate("Z_Primer5", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.ejectCanopyPosition, setOld.ejectCanopyPosition, f), 0.0F, 1.0F, 0.0F, 0.015F);
        mesh.chunkSetLocate("Z_EjectCanop", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.lockTailWheel0Position, setOld.lockTailWheel0Position, f), 0.0F, 1.0F, 0.0F, -0.06F);
        mesh.chunkSetLocate("Z_LockTWheel", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.masterSwitchPosition, setOld.masterSwitchPosition, f), 0.0F, 1.0F, 0.0F, -0.01F);
        mesh.chunkSetLocate("Z_MasterSwitch", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.lockTailWheel3Position, setOld.lockTailWheel3Position, f), 0.0F, 1.0F, 0.0F, -0.015F);
        mesh.chunkSetLocate("Z_LockTWheel3", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.lockTailWheel5Position, setOld.lockTailWheel5Position, f), 0.0F, 1.0F, 0.0F, -0.01F);
        mesh.chunkSetLocate("Z_LockTWheel5", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.circuitBreakers0Position, setOld.circuitBreakers0Position, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_CircuitBreakers", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.cockpitLights0Position, setOld.cockpitLights0Position, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_CockpitLights", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.navLights0Position, setOld.navLights0Position, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_NavLights", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.propPitchPosition, setOld.propPitchPosition, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_PropPitch", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.generator0Position, setOld.generator0Position, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_Generator", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.radioSwitch0Position, setOld.radioSwitch0Position, f), 0.0F, 1.0F, 0.0F, -0.009F);
        mesh.chunkSetLocate("Z_RadioSwitch", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.circuitBreakers2Position, setOld.circuitBreakers2Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_CircuitBreakers2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.cockpitLights2Position, setOld.cockpitLights2Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_CockpitLights2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.navLights2Position, setOld.navLights2Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_NavLights2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.fuelPump2Position, setOld.fuelPump2Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_FuelPomp2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.propPitch4Position, setOld.propPitch4Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_PropPitch4", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.radioSwitch2Position, setOld.radioSwitch2Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_RadioSwitch2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.generator2Position, setOld.generator2Position, f), 0.0F, 1.0F, 0.0F, 0.004F);
        mesh.chunkSetLocate("Z_Generator2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.cockpit0Position, setOld.cockpit0Position, f), 0.0F, 1.0F, 0.0F, -0.03F);
        mesh.chunkSetLocate("Cockpit", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.gearIndicatorPosition, setOld.gearIndicatorPosition, f), 0.0F, 1.0F, 0.0F, -0.04F);
        mesh.chunkSetLocate("GearIndicator", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.dropTankPosition, setOld.dropTankPosition, f), 0.0F, 1.0F, 0.0F, 0.03F);
        mesh.chunkSetLocate("Z_DropTank", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.revi16Support2Position, setOld.revi16Support2Position, f), 0.0F, 1.0F, 0.0F, -0.0675F);
        mesh.chunkSetLocate("Revi16Support2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.revi16Support3Position, setOld.revi16Support3Position, f), 0.0F, 1.0F, 0.0F, 0.01F);
        mesh.chunkSetLocate("Revi16Support3", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.bombsContact1Position, setOld.bombsContact1Position, f), 0.0F, 1.0F, 0.0F, -0.003F);
        mesh.chunkSetLocate("Z_BombsContact1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.bombsPanel1Position, setOld.bombsPanel1Position, f), 0.0F, 1.0F, 0.0F, -0.016F);
        mesh.chunkSetLocate("BombsPanel1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.bombsContact2Position, setOld.bombsContact2Position, f), 0.0F, 1.0F, 0.0F, 0.003F);
        mesh.chunkSetLocate("Z_BombsContact2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.leftPedalBase1Position, setOld.leftPedalBase1Position, f), 0.0F, 1.0F, 0.0F, -0.02F);
        mesh.chunkSetLocate("Z_LeftPedalBase1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.rightPedalBase1Position, setOld.rightPedalBase1Position, f), 0.0F, 1.0F, 0.0F, -0.023F);
        mesh.chunkSetLocate("Z_RightPedalBase1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.leftPedalBase2Position, setOld.leftPedalBase2Position, f), 0.0F, 1.0F, 0.0F, -0.011F);
        mesh.chunkSetLocate("Z_LeftPedalBase2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = cvt(interp(setNew.rightPedalBase2Position, setOld.rightPedalBase2Position, f), 0.0F, 1.0F, 0.0F, -0.013F);
        mesh.chunkSetLocate("Z_RightPedalBase2", Cockpit.xyz, Cockpit.ypr);
    }

    public void toggleDim()
    {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void toggleLight()
    {
        this.cockpitLightControl = !this.cockpitLightControl;
        if(this.cockpitLightControl)
        {
            light1.light.setEmit(0.004F, 0.4F);
            light2.light.setEmit(0.004F, 0.4F);
            setNightMats(true);
        } else
        {
            light1.light.setEmit(0.0F, 0.0F);
            light2.light.setEmit(0.0F, 0.0F);
            setNightMats(false);
        }
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 2) != 0)
        {
            mesh.chunkVisible("Z_Holes1", true);
            mesh.chunkVisible("Z_Holes3", true);
            mesh.chunkVisible("Z_Holes4", true);
            mesh.chunkVisible("Z_Z_RETICLE", false);
            mesh.chunkVisible("Z_Z_MASK", false);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
            mesh.chunkVisible("Z_Holes2", true);
        if((fm.AS.astateCockpitState & 0x40) != 0)
        {
            mesh.chunkVisible("DMGInstruments", true);
            mesh.chunkVisible("Z_Altimeter1", false);
            mesh.chunkVisible("Z_Altimeter2", false);
            mesh.chunkVisible("Z_Speedometer1", false);
            mesh.chunkVisible("Z_ATA1", false);
            mesh.chunkVisible("Z_FuelQuantity1", false);
        }
        if((fm.AS.astateCockpitState & 4) != 0)
        {
            mesh.chunkVisible("Z_Holes1", true);
            mesh.chunkVisible("Z_Holes2", true);
            mesh.chunkVisible("DMGInstruments", true);
            mesh.chunkVisible("Z_Altimeter1", false);
            mesh.chunkVisible("Z_Altimeter2", false);
            mesh.chunkVisible("Z_Speedometer1", false);
            mesh.chunkVisible("Z_ATA1", false);
            mesh.chunkVisible("Z_FuelQuantity1", false);
        }
        if((fm.AS.astateCockpitState & 0x80) != 0)
            mesh.chunkVisible("Z_OilSplats", true);
        if((fm.AS.astateCockpitState & 0x20) != 0)
            mesh.chunkVisible("Z_Holes1", true);
    }

    protected boolean doFocusEnter()
    {
        if(!super.doFocusEnter())
            return false;
        aircraft().hierMesh().chunkVisible("Blister1_D0", false);
        if(fm.AS.bIsAboutToBailout)
            hasCanopy = false;
        return true;
    }

    protected void doFocusLeave()
    {
        if(!isFocused())
            return;
        if(hasCanopy)
            aircraft().hierMesh().chunkVisible("Blister1_D0", true);
        super.doFocusLeave();
    }

    public void removeCanopy()
    {
        hasCanopy = false;
        mesh.chunkVisible("Top", false);
        mesh.chunkVisible("Top2", false);
        mesh.chunkVisible("Top3", false);
        mesh.chunkVisible("Z_TopCable", false);
        mesh.chunkVisible("Z_TopCable2", false);
        mesh.chunkVisible("TopIce1", false);
        mesh.chunkVisible("TopIce2", false);
        mesh.chunkVisible("TopIce3", false);
        mesh.chunkVisible("Z_Holes2", false);
        mesh.chunkVisible("Z_Holes3", false);
    }

    protected void reflectPlaneMats()
    {
        loadoutType = getLoadoutType();
        switch(loadoutType)
        {
        case 5:
            triggerForBreechControl[0] = 1;
            triggerForBreechControl[1] = 1;
            triggerForBreechControl[2] = 1;
            triggerForBreechControl[3] = 9;
            triggerForBreechControl[4] = 9;
            ammoCounterMax[0] = 100;
            ammoCounterMax[1] = 100;
            ammoCounterMax[2] = 100;
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_CANNON02");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_CANNON03");
            break;

        case 6:
            triggerForBreechControl[0] = 1;
            triggerForBreechControl[1] = 1;
            triggerForBreechControl[2] = 1;
            triggerForBreechControl[3] = 0;
            triggerForBreechControl[4] = 0;
            ammoCounterMax[0] = 100;
            ammoCounterMax[1] = 100;
            ammoCounterMax[2] = 100;
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_CANNON02");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_CANNON03");
            break;

        case 7:
            triggerForBreechControl[0] = 1;
            triggerForBreechControl[1] = 1;
            triggerForBreechControl[2] = 1;
            triggerForBreechControl[3] = 1;
            triggerForBreechControl[4] = 1;
            ammoCounterMax[0] = 100;
            ammoCounterMax[1] = 100;
            ammoCounterMax[2] = 100;
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_CANNON02");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_CANNON03");
            break;

        case 1:
            triggerForBreechControl[0] = 0;
            triggerForBreechControl[1] = 1;
            triggerForBreechControl[2] = 0;
            triggerForBreechControl[3] = 9;
            triggerForBreechControl[4] = 9;
            ammoCounterMax[0] = 500;
            ammoCounterMax[1] = 100;
            ammoCounterMax[2] = 500;
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_MGUN01");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_MGUN02");
            break;

        case 2:
            triggerForBreechControl[0] = 0;
            triggerForBreechControl[1] = 1;
            triggerForBreechControl[2] = 0;
            triggerForBreechControl[3] = 0;
            triggerForBreechControl[4] = 0;
            ammoCounterMax[0] = 500;
            ammoCounterMax[1] = 100;
            ammoCounterMax[2] = 500;
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_MGUN01");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_MGUN02");
            break;

        case 3:
        case 4:
        default:
            triggerForBreechControl[0] = 0;
            triggerForBreechControl[1] = 1;
            triggerForBreechControl[2] = 0;
            triggerForBreechControl[3] = 1;
            triggerForBreechControl[4] = 1;
            ammoCounterMax[0] = 500;
            ammoCounterMax[1] = 100;
            ammoCounterMax[2] = 500;
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_MGUN01");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_MGUN02");
            break;
        }
        String s = "RearPanelA_Type_0" + loadoutType;
        mesh.materialReplace("RearPanelA", s);
        gun[1] = ((Aircraft)fm.actor).getGunByHookName("_CANNON01");
        String s1 = aircraft().thisWeaponsName.toUpperCase();
        if(s1.startsWith("U2") || s1.startsWith("U4") || s1.startsWith("U7") || s1.startsWith("U9"))
        {
            gun[3] = ((Aircraft)fm.actor).getGunByHookName("_CANNON04");
            gun[4] = ((Aircraft)fm.actor).getGunByHookName("_CANNON05");
        } else
        if(s1.startsWith("U1_R3") || s1.startsWith("U1_R4"))
        {
            gun[3] = ((Aircraft)fm.actor).getGunByHookName("_CANNON06");
            gun[4] = ((Aircraft)fm.actor).getGunByHookName("_CANNON07");
        } else
        if(s1.startsWith("U1_R5"))
        {
            gun[3] = ((Aircraft)fm.actor).getGunByHookName("_CANNON06");
            gun[4] = ((Aircraft)fm.actor).getGunByHookName("_CANNON07");
        } else
        {
            gun[3] = GunEmpty.get();
            gun[4] = GunEmpty.get();
        }
        for(int i = 0; i < MAX_GUNS; i++)
            if(gun[i] instanceof GunEmpty)
                timePerShot[i] = 10000L;
            else
                timePerShot[i] = (long)(1000F / GunGeneric.getProperties(gun[i].getClass()).shotFreq);

    }

    private int getLoadoutType()
    {
        String s = aircraft().thisWeaponsName.toUpperCase();
        if(s.startsWith("U7_R2") || s.startsWith("U9_R2"))
            return 7;
        if(s.startsWith("U7") || s.startsWith("U9"))
            return 6;
        if(s.startsWith("U6") || s.startsWith("U8"))
            return 5;
        if(s.startsWith("U1_R5"))
            return 4;
        if(s.startsWith("U1_R4") || s.startsWith("U2_R2") || s.startsWith("U4_R2"))
            return 3;
        else
            return s.startsWith("U1_R3") || s.startsWith("U2") || s.startsWith("U4") ? 2 : 1;
    }

    private boolean bNeedSetUp;
    private boolean hasCanopy;
    private int loadoutType;
    private static final int NUM_COUNTERS = 3;
    private static final int MAX_GUNS = 5;
    private int triggerForBreechControl[] = {
        0, 1, 0, 9, 9
    };
    private long lastBreechTime[] = {
        0L, 0L, 0L, 0L, 0L
    };
    private long timePerShot[] = {
        0L, 0L, 0L, 0L, 0L
    };
    private int ammoCounterMax[] = {
        500, 100, 500
    };
    private Gun gun[] = {
        null, null, null, null, null
    };
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private float oldctl;
    private float curctl;
    private boolean AccelerationState1;
    private boolean IceState1;
    private boolean IceState2;
    private boolean IceState3;
    private boolean CleanIceState1;
    private boolean CanopyClosedState;
    private boolean EjectCanopyState;
    private float timeCounterIce1;
    private float timeIce1;
    private float timeCounterIce2;
    private float timeIce2;
    private float timeCounterIce3;
    private float timeIce3;
    private float timeCounterCleanIce1;
    private float timeCleanIce1;
    private boolean UnLockedTState;
    private boolean LockedTDelayState;
    private float timeCounterLockT;
    private float timeLockT;
    private float timeCounterUnLockT;
    private float timeUnLockT;
    private boolean GunsightMove;
    private boolean RotGunsightDelay;
    private float timeGunsightMove2;
    private float timeCounterGunsightMove2;
    private boolean ReticleCut;
    private float timeCounterReticle;
    private float timeReticle;
    private LightPointActor light1;
    private LightPointActor light2;
    private float pictAiler;
    private float pictElev;
    private float pictManifold;
    private static final float speedometerScale[] = {
        0.0F, -12.33333F, 18.5F, 37F, 62.5F, 90F, 110.5F, 134F, 158.5F, 186F, 
        212.5F, 238.5F, 265F, 289.5F, 315F, 339.5F, 346F, 346F
    };
    private static final float rpmScale[] = {
        0.0F, 11.25F, 54F, 111F, 171.5F, 229.5F, 282.5F, 334F, 342.5F, 342.5F
    };
    private static final float fuelScale[] = {
        0.0F, 9F, 21F, 29.5F, 37F, 48F, 61.5F, 75.5F, 92F, 92F
    };
    private Point3d tmpP;
    private Vector3d tmpV;
    public float limits6DoF[];

    static 
    {
        Property.set(CockpitBV_155B1.class, "normZNs", new float[] {
            0.72F, 0.47F, 0.47F, 0.47F
        });
    }

}
