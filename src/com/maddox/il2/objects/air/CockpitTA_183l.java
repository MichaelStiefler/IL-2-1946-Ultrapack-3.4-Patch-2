package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;

public class CockpitTA_183l extends CockpitPilot
{
    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if(fm != null)
            {
                setTmp = setOld;
                setOld = setNew;
                setNew = setTmp;
                setNew.altimeter = fm.getAltitude();
                if(cockpitDimControl)
                {
                    if(setNew.dimPosition > 0.0F)
                        setNew.dimPosition = setOld.dimPosition - 0.05F;
                } else
                if(setNew.dimPosition < 1.0F)
                    setNew.dimPosition = setOld.dimPosition + 0.05F;
                setNew.throttle = 0.92F * setOld.throttle + 0.08F * fm.CT.PowerControl;
                setNew.vspeed = (499F * setOld.vspeed + fm.getVertSpeed()) / 500F;
                setNew.azimuth = fm.Or.getYaw();
                if(setOld.azimuth > 270F && setNew.azimuth < 90F)
                    setOld.azimuth -= 360F;
                if(setOld.azimuth < 90F && setNew.azimuth > 270F)
                    setOld.azimuth += 360F;
                setNew.waypointAzimuth = (10F * setOld.waypointAzimuth + (waypointAzimuth() - setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
                float f = ((TA_183l)aircraft()).k14Distance;
                setNew.k14w = (5F * CockpitTA_183l.k14TargetWingspanScale[((TA_183l)aircraft()).k14WingspanType]) / f;
                setNew.k14w = 0.9F * setOld.k14w + 0.1F * setNew.k14w;
                setNew.k14wingspan = 0.9F * setOld.k14wingspan + 0.1F * CockpitTA_183l.k14TargetMarkScale[((TA_183l)aircraft()).k14WingspanType];
                setNew.k14mode = 0.8F * setOld.k14mode + 0.2F * (float)((TA_183l)aircraft()).k14Mode;
                Vector3d vector3d = aircraft().FM.getW();
                double d = 0.00125D * (double)f;
                float f1 = (float)Math.toDegrees(d * vector3d.z);
                float f2 = -(float)Math.toDegrees(d * vector3d.y);
                float f3 = floatindex((f - 200F) * 0.04F, CockpitTA_183l.k14BulletDrop) - CockpitTA_183l.k14BulletDrop[0];
                f2 += (float)Math.toDegrees(Math.atan(f3 / f));
                setNew.k14x = 0.92F * setOld.k14x + 0.08F * f1;
                setNew.k14y = 0.92F * setOld.k14y + 0.08F * f2;
                if(setNew.k14x > 7F)
                    setNew.k14x = 7F;
                if(setNew.k14x < -7F)
                    setNew.k14x = -7F;
                if(setNew.k14y > 7F)
                    setNew.k14y = 7F;
                if(setNew.k14y < -7F)
                    setNew.k14y = -7F;
            }
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
        float vspeed;
        float k14wingspan;
        float k14mode;
        float k14x;
        float k14y;
        float k14w;

        private Variables()
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

    public CockpitTA_183l()
    {
        super("3DO/Cockpit/Ta-183/hierLight.him", "bf109");
        bNeedSetUp = true;
        setOld = new Variables();
        setNew = new Variables();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictGear = 0.0F;
        pictFlap = 0.0F;
        pictAftb = 0.0F;
        setNew.dimPosition = 0.0F;
        cockpitNightMats = (new String[] {
            "D_gauges1_TR", "D_gauges2_TR", "D_gauges3_TR", "gauges1_TR", "gauges2_TR", "gauges3_TR", "gauges4_TR", "gauges5_TR", "gauges6_TR", "gauges7_TR", 
            "gauges8_TR", "gauges9_TR"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        mesh.materialReplace("Gloss1D0o", mat);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        int i = ((TA_183l)aircraft()).k14Mode;
        boolean flag = i < 2;
        mesh.chunkVisible("Z_Z_RETICLE", flag);
        flag = i > 0;
        mesh.chunkVisible("Z_Z_RETICLE1", flag);
        mesh.chunkSetAngles("Z_Z_RETICLE1", 0.0F, setNew.k14x, setNew.k14y);
        resetYPRmodifier();
        Cockpit.xyz[0] = setNew.k14w;
        for(int j = 1; j < 7; j++)
        {
            mesh.chunkVisible("Z_Z_AIMMARK" + j, flag);
            mesh.chunkSetLocate("Z_Z_AIMMARK" + j, Cockpit.xyz, Cockpit.ypr);
        }

        if(gun[0] == null)
        {
            gun[0] = ((Aircraft)fm.actor).getGunByHookName("_MGUN01");
            gun[1] = ((Aircraft)fm.actor).getGunByHookName("_MGUN02");
            gun[2] = ((Aircraft)fm.actor).getGunByHookName("_MGUN03");
            gun[3] = ((Aircraft)fm.actor).getGunByHookName("_MGUN04");
        }
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -0.07115F);
        mesh.chunkSetLocate("EZ42Dimm", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("EZ42Filter", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -85F), 0.0F, 0.0F);
        mesh.chunkSetAngles("EZ42FLever", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("EZ42Range", 0.0F, 0.0F, 0.0F);
        mesh.chunkSetAngles("EZ42Size", 0.0F, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.ypr[0] = interp(setNew.throttle, setOld.throttle, f) * 59.09091F;
        Cockpit.xyz[2] = cvt(Cockpit.ypr[0], 7.5F, 12.5F, 0.0F, -0.0054F);
        mesh.chunkSetLocate("ThrottleQuad", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("RPedalBase", 0.0F, 0.0F, fm.CT.getRudder() * 15F);
        mesh.chunkSetAngles("RPedalStrut", 0.0F, 0.0F, -fm.CT.getRudder() * 15F);
        mesh.chunkSetAngles("RPedal", 0.0F, 0.0F, -fm.CT.getRudder() * 15F - fm.CT.getBrake() * 15F);
        mesh.chunkSetAngles("LPedalBase", 0.0F, 0.0F, -fm.CT.getRudder() * 15F);
        mesh.chunkSetAngles("LPedalStrut", 0.0F, 0.0F, fm.CT.getRudder() * 15F);
        mesh.chunkSetAngles("LPedal", 0.0F, 0.0F, fm.CT.getRudder() * 15F - fm.CT.getBrake() * 15F);
        mesh.chunkSetAngles("Stick", 0.0F, (pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl) * 20F, (pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl) * 20F);
        resetYPRmodifier();
        Cockpit.xyz[2] = fm.CT.WeaponControl[1] ? -0.004F : 0.0F;
        mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
        pictGear = 0.91F * pictGear + 0.09F * fm.CT.GearControl;
        mesh.chunkSetAngles("ZGear", cvt(pictGear, 0.0F, 1.0F, -26.5F, 26.5F), 0.0F, 0.0F);
        pictFlap = 0.91F * pictFlap + 0.09F * fm.CT.FlapsControl;
        mesh.chunkSetAngles("ZFlaps", cvt(pictFlap, 0.0F, 1.0F, -26.5F, 26.5F), 0.0F, 0.0F);
        pictAftb = 0.91F * pictAftb + 0.09F * (fm.CT.PowerControl > 1.0F ? 1.0F : 0.0F);
        mesh.chunkSetAngles("ZLever", cvt(pictAftb, 0.0F, 1.0F, -29.5F, 29.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_X4Stick", 0.0F, 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleVSI", setNew.vspeed >= 0.0F ? floatindex(cvt(setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale) : -floatindex(cvt(-setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleKMH", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleRPM", 55F + floatindex(cvt(fm.EI.engines[0].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleALT", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleALTKm", cvt(setNew.altimeter, 0.0F, 10000F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("RepeaterOuter", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("RepeaterPlane", -interp(setNew.waypointAzimuth, setOld.waypointAzimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleGasPress", cvt(fm.M.fuel > 1.0F ? 0.6F * fm.EI.engines[0].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, 270F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleOilPress", cvt(1.0F + 0.005F * fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 270F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleOxPress", 135F, 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleGasTemp", cvt(fm.EI.engines[0].tWaterOut, 300F, 1000F, 0.0F, 51F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleFuel", cvt(fm.M.fuel / 0.72F, 0.0F, 1200F, 0.0F, 48F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleNahe1", 0.0F, 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleNahe2", 0.0F, 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleTrimmung", fm.CT.getTrimElevatorControl() * 25F * 2.0F, 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleAHCyl", -fm.Or.getKren(), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleAHBar", 0.0F, 0.0F, cvt(fm.Or.getTangage(), -45F, 45F, -14F, 14F));
        mesh.chunkSetAngles("NeedleAHTurn", cvt(getBall(6D), -6F, 6F, -12.5F, 12.5F), 0.0F, 0.0F);
        float f1;
        if(aircraft().isFMTrackMirror())
        {
            f1 = aircraft().fmTrack().getCockpitAzimuthSpeed();
        } else
        {
            f1 = cvt((setNew.azimuth - setOld.azimuth) / Time.tickLenFs(), -6F, 6F, -26.5F, 26.5F);
            if(aircraft().fmTrack() != null)
                aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
        }
        mesh.chunkSetAngles("NeedleAHBank", -f1, 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedleAirPress", 135F, 0.0F, 0.0F);
        mesh.chunkSetAngles("zClock1a", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zClock1b", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zClock1c", cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(gun[0].countBullets(), 0.0F, 500F, -0.039F, 0.0F);
        mesh.chunkSetLocate("RC_MG17_L", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = cvt(gun[2].countBullets(), 0.0F, 500F, -0.039F, 0.0F);
        mesh.chunkSetLocate("RC_MG17_R", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = cvt(gun[1].countBullets(), 0.0F, 500F, -0.039F, 0.0F);
        mesh.chunkSetLocate("RC_MG151_L", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = cvt(gun[3].countBullets(), 0.0F, 500F, -0.039F, 0.0F);
        mesh.chunkSetLocate("RC_MG151_R", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkVisible("XLampFuelLow", fm.M.fuel < 43.2F);
        mesh.chunkVisible("XLampSpeedWorn", fm.getSpeedKMH() < 200F);
        mesh.chunkVisible("XLampGearL_1", fm.CT.getGear() < 0.05F);
        mesh.chunkVisible("XLampGearL_2", fm.CT.getGear() > 0.95F && fm.Gears.lgear);
        mesh.chunkVisible("XLampGearR_1", fm.CT.getGear() < 0.05F);
        mesh.chunkVisible("XLampGearR_2", fm.CT.getGear() > 0.95F && fm.Gears.rgear);
        mesh.chunkVisible("XLampGearC_1", fm.CT.getGear() < 0.05F);
        mesh.chunkVisible("XLampGearC_2", fm.CT.getGear() > 0.95F && fm.Gears.cgear);
        mesh.chunkVisible("XLampMissileL_1", aircraft().getGunByHookName("_ExternalRock25").haveBullets());
        mesh.chunkVisible("XLampMissileL_2", aircraft().getGunByHookName("_ExternalRock27").haveBullets());
        mesh.chunkVisible("XLampMissileR_1", aircraft().getGunByHookName("_ExternalRock28").haveBullets());
        mesh.chunkVisible("XLampMissileR_2", aircraft().getGunByHookName("_ExternalRock26").haveBullets());
        mesh.chunkVisible("XLampMissile", false);
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

    private void retoggleLight()
    {
        if(cockpitLightControl)
        {
            setNightMats(false);
            setNightMats(true);
        } else
        {
            setNightMats(true);
            setNightMats(false);
        }
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 2) != 0)
        {
            mesh.chunkVisible("XGlassDamage2", true);
            mesh.chunkVisible("EZ42", false);
            mesh.chunkVisible("EZ42Dimm", false);
            mesh.chunkVisible("EZ42Filter", false);
            mesh.chunkVisible("EZ42FLever", false);
            mesh.chunkVisible("EZ42Range", false);
            mesh.chunkVisible("EZ42Size", false);
            mesh.chunkVisible("Z_Z_MASK", false);
            mesh.chunkVisible("Z_Z_RETICLE", false);
            mesh.chunkVisible("DEZ42", true);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
        {
            mesh.chunkVisible("XGlassDamage3", true);
            mesh.chunkVisible("XGlassDamage4", true);
        }
        if((fm.AS.astateCockpitState & 4) == 0);
        if((fm.AS.astateCockpitState & 0x40) != 0)
        {
            mesh.chunkVisible("IPCGages", false);
            mesh.chunkVisible("IPCGages_D1", true);
            mesh.chunkVisible("NeedleALT", false);
            mesh.chunkVisible("NeedleALTKm", false);
            mesh.chunkVisible("NeedleAHCyl", false);
            mesh.chunkVisible("NeedleAHBank", false);
            mesh.chunkVisible("NeedleAHBar", false);
            mesh.chunkVisible("NeedleAHTurn", false);
            mesh.chunkVisible("zClock1a", false);
            mesh.chunkVisible("zClock1b", false);
            mesh.chunkVisible("zClock1c", false);
            mesh.chunkVisible("RepeaterOuter", false);
            mesh.chunkVisible("RepeaterInner", false);
            mesh.chunkVisible("RepeaterPlane", false);
            mesh.chunkVisible("NeedleNahe1", false);
            mesh.chunkVisible("NeedleNahe2", false);
            mesh.chunkVisible("NeedleFuel", false);
            mesh.chunkVisible("NeedleGasPress", false);
            mesh.chunkVisible("NeedleGasTemp", false);
            mesh.chunkVisible("NeedleOilPress", false);
            mesh.chunkVisible("NeedleRPM", false);
            mesh.chunkVisible("NeedleKMH", false);
            mesh.chunkVisible("NeedleVSI", false);
            mesh.chunkVisible("XHullDamage3", true);
            mesh.chunkVisible("XGlassDamage2", true);
        }
        if((fm.AS.astateCockpitState & 8) != 0)
        {
            mesh.chunkVisible("XGlassDamage1", true);
            mesh.chunkVisible("XHullDamage2", true);
            mesh.chunkVisible("XHullDamage3", true);
        }
        if((fm.AS.astateCockpitState & 0x20) != 0)
        {
            mesh.chunkVisible("XGlassDamage4", true);
            mesh.chunkVisible("XHullDamage1", true);
            mesh.chunkVisible("XHullDamage3", true);
        }
        retoggleLight();
    }

    private boolean bNeedSetUp;
    private Gun gun[] = {
        null, null, null, null
    };
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private float pictAiler;
    private float pictElev;
    private float pictGear;
    private float pictFlap;
    private float pictAftb;
    private static final float speedometerScale[] = {
        0.0F, 18.5F, 67F, 117F, 164F, 215F, 267F, 320F, 379F, 427F, 
        428F
    };
    private static final float rpmScale[] = {
        0.0F, 0.0F, 0.0F, 16.5F, 34.5F, 55F, 77.5F, 104F, 133.5F, 162.5F, 
        192F, 224F, 254F, 255.5F, 260F
    };
    private static final float vsiNeedleScale[] = {
        0.0F, 48F, 82F, 96.5F, 111F, 120.5F, 130F, 130F
    };
    private static final float k14TargetMarkScale[] = {
        -0F, -4.5F, -27.5F, -42.5F, -56.5F, -61.5F, -70F, -95F, -102.5F, -106F
    };
    private static final float k14TargetWingspanScale[] = {
        9.9F, 10.52F, 13.8F, 16.34F, 19F, 20F, 22F, 29.25F, 30F, 32.85F
    };
    private static final float k14BulletDrop[] = {
        5.812F, 6.168F, 6.508F, 6.978F, 7.24F, 7.576F, 7.849F, 8.108F, 8.473F, 8.699F, 
        8.911F, 9.111F, 9.384F, 9.554F, 9.787F, 9.928F, 9.992F, 10.282F, 10.381F, 10.513F, 
        10.603F, 10.704F, 10.739F, 10.782F, 10.789F
    };

}
