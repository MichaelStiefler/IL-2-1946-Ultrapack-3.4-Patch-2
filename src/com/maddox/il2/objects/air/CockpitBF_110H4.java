package com.maddox.il2.objects.air;

import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;

public class CockpitBF_110H4 extends CockpitPilot
{
    private class Variables
    {

        float altimeter;
        float throttle1;
        float throttle2;
        float dimPosition;
        float azimuth;
        float waypointAzimuth;
        float mix1;
        float mix2;
        float vspeed;
        float radioalt;

        private Variables()
        {
        }

    }

    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if((BF_110H4)aircraft() == null);
            if(BF_110H4.bChangedPit)
            {
                reflectPlaneToModel();
                if((BF_110H4)aircraft() == null);
                BF_110H4.bChangedPit = false;
            }
            setTmp = setOld;
            setOld = setNew;
            setNew = setTmp;
            setNew.altimeter = fm.getAltitude();
            if(cockpitDimControl)
            {
                if(setNew.dimPosition > 0.0F)
                    setNew.dimPosition = setNew.dimPosition - 0.05F;
            } else
            if(setNew.dimPosition < 1.0F)
                setNew.dimPosition = setNew.dimPosition + 0.05F;
            setNew.throttle1 = 0.91F * setOld.throttle1 + 0.09F * fm.EI.engines[0].getControlThrottle();
            setNew.throttle2 = 0.91F * setOld.throttle2 + 0.09F * fm.EI.engines[1].getControlThrottle();
            setNew.mix1 = 0.88F * setOld.mix1 + 0.12F * fm.EI.engines[0].getControlMix();
            setNew.mix2 = 0.88F * setOld.mix2 + 0.12F * fm.EI.engines[1].getControlMix();
            setNew.azimuth = fm.Or.getYaw();
            if(setOld.azimuth > 270F && setNew.azimuth < 90F)
                setOld.azimuth -= 360F;
            if(setOld.azimuth < 90F && setNew.azimuth > 270F)
                setOld.azimuth += 360F;
            setNew.waypointAzimuth = (10F * setOld.waypointAzimuth + (waypointAzimuth() - setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
            Variables variables = setNew;
            float f = 0.9F * setOld.radioalt;
            float f1 = 0.1F;
            float f2 = fm.getAltitude();
            World.cur();
            World.land();
            variables.radioalt = f + f1 * (f2 - Landscape.HQ_Air((float)fm.Loc.x, (float)fm.Loc.y));
            setNew.vspeed = (199F * setOld.vspeed + fm.getVertSpeed()) / 200F;
            return true;
        }

        Interpolater()
        {
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
            waypoint.getP(Cockpit.P1);
            Cockpit.V.sub(Cockpit.P1, fm.Loc);
            return (float)(Math.toDegrees(Math.atan2(Cockpit.V.y, Cockpit.V.x)));
        }
    }

    protected void reflectPlaneToModel()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        boolean flag = hiermesh.isChunkVisible("Engine1_D0") || hiermesh.isChunkVisible("Engine1_D1") || hiermesh.isChunkVisible("Engine1_D2");
        mesh.chunkVisible("EnginLeft", flag);
        mesh.chunkVisible("Z_Temp4", flag);
        mesh.chunkVisible("Z_Temp6", flag);
        mesh.chunkVisible("Z_Fuelpress1", flag);
        mesh.chunkVisible("Z_OilPress1", flag);
        mesh.chunkVisible("Z_Oiltemp1", flag);
        flag = hiermesh.isChunkVisible("Engine2_D0") || hiermesh.isChunkVisible("Engine2_D1") || hiermesh.isChunkVisible("Engine2_D2");
        mesh.chunkVisible("EnginRight", flag);
        mesh.chunkVisible("Z_Temp5", flag);
        mesh.chunkVisible("Z_Temp7", flag);
        mesh.chunkVisible("Z_Fuelpress2", flag);
        mesh.chunkVisible("Z_OilPress2", flag);
        mesh.chunkVisible("Z_Oiltemp2", flag);
    }

    public CockpitBF_110H4()
    {
        super("3DO/Cockpit/Bf-110H4/hier.him", "bf109");
        setOld = new Variables();
        setNew = new Variables();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictManifold1 = 0.0F;
        pictManifold2 = 0.0F;
        setNew.dimPosition = 0.0F;
        cockpitDimControl = !cockpitDimControl;
        cockpitNightMats = (new String[] {
            "bague1", "bague2", "boitier", "cadran1", "cadran2", "cadran3", "cadran4", "cadran5", "cadran6", "cadran7", 
            "cadran8", "consoledr2", "enggauge", "fils", "gauche", "skala"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(gun[0] == null)
        {
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_CANNON01");
            gun[1] = ((Aircraft)fm.actor).getGunByHookName("_CANNON02");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_MGUN01");
        }
        resetYPRmodifier();
        Cockpit.xyz[2] = 0.06815F * interp(setNew.dimPosition, setOld.dimPosition, f);
        mesh.chunkSetLocate("Revisun", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkVisible("Z_GearLGreen", fm.CT.getGear() == 1.0F && fm.Gears.lgear);
        mesh.chunkVisible("Z_GearRGreen", fm.CT.getGear() == 1.0F && fm.Gears.lgear);
        mesh.chunkVisible("Z_GearLRed", fm.CT.getGear() == 0.0F);
        mesh.chunkVisible("Z_GearRRed", fm.CT.getGear() == 0.0F);
        mesh.chunkVisible("Z_FuelL1", fm.M.fuel < 36F);
        mesh.chunkVisible("Z_FuelL2", fm.M.fuel < 102F);
        mesh.chunkVisible("Z_FuelR1", fm.M.fuel < 36F);
        mesh.chunkVisible("Z_FuelR2", fm.M.fuel < 102F);
        if(gun[0] != null)
            mesh.chunkVisible("Z_AmmoL", gun[0].countBullets() == 0);
        if(gun[1] != null)
            mesh.chunkVisible("Z_AmmoR", gun[1].countBullets() == 0);
        mesh.chunkSetAngles("Z_Columnbase", 0.0F, -(pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl) * 10F, 0.0F);
        mesh.chunkSetAngles("Z_Column", 0.0F, -(pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl) * 15F, 0.0F);
        resetYPRmodifier();
        if(fm.CT.saveWeaponControl[1])
            Cockpit.xyz[2] = 0.00545F;
        mesh.chunkSetLocate("Z_Columnbutton1", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[2] = -0.05F * fm.CT.getRudder();
        mesh.chunkSetLocate("Z_LeftPedal", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.05F * fm.CT.getRudder();
        mesh.chunkSetLocate("Z_RightPedal", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_Throttle1", interp(setNew.throttle1, setOld.throttle1, f) * 52.2F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Throttle2", interp(setNew.throttle2, setOld.throttle2, f) * 52.2F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Mixture1", interp(setNew.mix1, setOld.mix2, f) * 52.2F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Mixture2", interp(setNew.mix1, setOld.mix2, f) * 52.2F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Pitch1", fm.EI.engines[0].getControlProp() * 60F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Pitch2", fm.EI.engines[1].getControlProp() * 60F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Radiat1", 0.0F, 0.0F, fm.EI.engines[0].getControlRadiator() * 15F);
        mesh.chunkSetAngles("Z_Radiat2", 0.0F, 0.0F, fm.EI.engines[1].getControlRadiator() * 15F);
        mesh.chunkSetAngles("Z_Compass1", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Azimuth1", -interp(setNew.waypointAzimuth, setOld.waypointAzimuth, f), 0.0F, 0.0F);
        if(gun[0] != null)
            mesh.chunkSetAngles("Z_AmmoCounter1", cvt(gun[0].countBullets(), 0.0F, 500F, 13F, 0.0F), 0.0F, 0.0F);
        if(gun[2] != null)
            mesh.chunkSetAngles("Z_AmmoCounter2", cvt(gun[2].countBullets(), 0.0F, 500F, 13F, 0.0F), 0.0F, 0.0F);
        if(gun[1] != null)
            mesh.chunkSetAngles("Z_AmmoCounter3", cvt(gun[1].countBullets(), 0.0F, 500F, 13F, 0.0F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Second1", cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        float f1;
        if(aircraft().isFMTrackMirror())
        {
            f1 = aircraft().fmTrack().getCockpitAzimuthSpeed();
        } else
        {
            f1 = cvt((setNew.azimuth - setOld.azimuth) / Time.tickLenFs(), -3F, 3F, 21F, -21F);
            if(aircraft().fmTrack() != null)
                aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
        }
        mesh.chunkSetAngles("Z_TurnBank1", f1, 0.0F, 0.0F);
        f1 = getBall(4D);
        mesh.chunkSetAngles("Z_TurnBank2", cvt(f1, -4F, 4F, 10F, -10F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank3", cvt(f1, -3.8F, 3.8F, 9F, -9F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank4", cvt(f1, -3.3F, 3.3F, 7.5F, -7.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Horizon1", 0.0F, 0.0F, fm.Or.getKren());
        mesh.chunkSetAngles("Z_Horizon2", cvt(fm.Or.getTangage(), -45F, 45F, -23F, 23F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Climb1", floatindex(cvt(setNew.vspeed, -30F, 30F, 0.0F, 12F), variometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Speed1", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 0.0F, 800F, 0.0F, 16F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("RPM1", floatindex(cvt(fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("RPM2", floatindex(cvt(fm.EI.engines[1].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("ATA1", cvt(pictManifold1 = 0.75F * pictManifold1 + 0.25F * fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 332.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("ATA2", cvt(pictManifold2 = 0.75F * pictManifold2 + 0.25F * fm.EI.engines[1].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 332.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zAlt1", cvt(interp(setNew.radioalt, setOld.radioalt, f), 0.0F, 750F, 0.0F, 228.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zAlt2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zAlt3", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 14000F, 0.0F, 313F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuel1", cvt(fm.M.fuel / 0.72F, 0.0F, 400F, 0.0F, 66.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp1", cvt(Atmosphere.temperature((float)fm.Loc.z), 233.09F, 313.09F, -42.5F, 42.4F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp2", cvt(fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 68F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp3", cvt(fm.EI.engines[1].tOilOut, 0.0F, 120F, 0.0F, 68F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AirPressure1", 170F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Autopilot1", -interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Autopilot2", interp(setNew.waypointAzimuth, setOld.waypointAzimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp4", -(float)Math.toDegrees(fm.EI.engines[0].getPropPhi() - fm.EI.engines[0].getPropPhiMin()) * 60F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp6", -(float)Math.toDegrees(fm.EI.engines[0].getPropPhi() - fm.EI.engines[0].getPropPhiMin()) * 5F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp5", -(float)Math.toDegrees(fm.EI.engines[1].getPropPhi() - fm.EI.engines[1].getPropPhiMin()) * 60F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp7", -(float)Math.toDegrees(fm.EI.engines[1].getPropPhi() - fm.EI.engines[1].getPropPhiMin()) * 5F, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuelpress1", cvt(fm.M.fuel <= 1.0F ? 0.0F : 0.77F, 0.0F, 2.0F, 0.0F, 160F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuelpress2", cvt(fm.M.fuel <= 1.0F ? 0.0F : 0.77F, 0.0F, 2.0F, 0.0F, 160F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_OilPress1", cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 160F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_OilPress2", cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 160F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Oiltemp1", floatindex(cvt(fm.EI.engines[0].tOilOut, 40F, 120F, 0.0F, 8F), oilTScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Oiltemp2", floatindex(cvt(fm.EI.engines[1].tOilOut, 40F, 120F, 0.0F, 8F), oilTScale), 0.0F, 0.0F);
    }

    public void toggleDim()
    {
        cockpitDimControl = !cockpitDimControl;
    }

    public void toggleLight()
    {
        cockpitLightControl = !cockpitLightControl;
        if(cockpitLightControl)
            setNightMats(true);
        else
            setNightMats(false);
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 2) != 0)
        {
            mesh.chunkVisible("HullDamage3", true);
            mesh.chunkVisible("Revi_D0", false);
            mesh.chunkVisible("ReviSun", false);
            mesh.chunkVisible("Z_Z_RETICLE", false);
            mesh.chunkVisible("Z_Z_MASK", false);
            mesh.chunkVisible("Revi_D1", true);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
            mesh.chunkVisible("HullDamage2", true);
        if((fm.AS.astateCockpitState & 0x40) != 0)
            mesh.chunkVisible("XGlassDamage1", true);
        if((fm.AS.astateCockpitState & 4) != 0)
            mesh.chunkVisible("HullDamage1", true);
        if((fm.AS.astateCockpitState & 8) != 0)
            mesh.chunkVisible("XGlassDamage3", true);
        if((fm.AS.astateCockpitState & 0x80) != 0)
            mesh.chunkVisible("XGlassDamage2", true);
        if((fm.AS.astateCockpitState & 0x10) != 0)
            mesh.chunkVisible("HullDamage1", true);
        if((fm.AS.astateCockpitState & 0x20) != 0)
            mesh.chunkVisible("XGlassDamage4", true);
    }

    private Gun gun[] = {
        null, null, null
    };
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private float pictAiler;
    private float pictElev;
    private float pictManifold1;
    private float pictManifold2;
    private static final float speedometerScale[] = {
        0.0F, -12.33333F, 18.5F, 37F, 62.5F, 90F, 110.5F, 134F, 158.5F, 186F, 
        212.5F, 238.5F, 265F, 289.5F, 315F, 339.5F, 346F, 346F
    };
    private static final float rpmScale[] = {
        0.0F, 36.5F, 70F, 111F, 149.5F, 186.5F, 233.5F, 282.5F, 308F, 318.5F
    };
    private static final float oilTScale[] = {
        0.0F, 24.5F, 47.5F, 74F, 102.5F, 139F, 188F, 227.5F, 290.5F
    };
    private static final float variometerScale[] = {
        -130.5F, -119.5F, -109.5F, -96F, -83F, -49.5F, 0.0F, 49.5F, 83F, 96F, 
        109.5F, 119.5F, 130.5F
    };
}
