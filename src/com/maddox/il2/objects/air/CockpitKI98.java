package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitKI98 extends CockpitPilot
{
    private class Variables
    {

        float flap;
        float throttle;
        float pitch;
        float mix;
        float gear;
        float tlock;
        float altimeter;
        float manifold;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float vspeed;
        float dimPosition;
        float stbyPosition;

        private Variables()
        {
            azimuth = new AnglesFork();
            waypointAzimuth = new AnglesFork();
        }

    }

    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if(fm != null)
            {
                setTmp = setOld;
                setOld = setNew;
                setNew = setTmp;
                setNew.flap = 0.88F * setOld.flap + 0.12F * fm.CT.FlapsControl;
                setNew.tlock = 0.7F * setOld.tlock + 0.3F * (fm.Gears.bTailwheelLocked ? 1.0F : 0.0F);
                if(cockpitDimControl)
                {
                    if(setNew.dimPosition > 0.0F)
                        setNew.dimPosition = setOld.dimPosition - 0.05F;
                } else
                if(setNew.dimPosition < 1.0F)
                    setNew.dimPosition = setOld.dimPosition + 0.05F;
                if((fm.AS.astateCockpitState & 2) != 0)
                {
                    if(setNew.stbyPosition > 0.0F)
                        setNew.stbyPosition = setOld.stbyPosition - 0.025F;
                } else
                if(setNew.stbyPosition < 1.0F)
                    setNew.stbyPosition = setOld.stbyPosition + 0.025F;
                if(fm.CT.GearControl < 0.5F && setNew.gear < 1.0F)
                    setNew.gear = setOld.gear + 0.02F;
                if(fm.CT.GearControl > 0.5F && setNew.gear > 0.0F)
                    setNew.gear = setOld.gear - 0.02F;
                setNew.throttle = 0.9F * setOld.throttle + 0.1F * fm.CT.PowerControl;
                setNew.manifold = 0.8F * setOld.manifold + 0.2F * fm.EI.engines[0].getManifoldPressure();
                setNew.pitch = 0.9F * setOld.pitch + 0.1F * fm.EI.engines[0].getControlProp();
                setNew.mix = 0.9F * setOld.mix + 0.1F * fm.EI.engines[0].getControlMix();
                setNew.altimeter = fm.getAltitude();
                setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
                float f = waypointAzimuth();
                setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), (f - setOld.azimuth.getDeg(1.0F)) + World.Rnd().nextFloat(-3F, 3F));
                setNew.vspeed = (199F * setOld.vspeed + fm.getVertSpeed()) / 200F;
            }
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
            return 0.0F;
        waypoint.getP(tmpP);
        tmpV.sub(tmpP, fm.Loc);
        float f = (float)(Math.toDegrees(Math.atan2(-tmpV.y, tmpV.x)));
        while (f <= -180F) f += 360F;
        while (f > 180F) f -= 360F;
        return f;
    }

    public CockpitKI98()
    {
        super("3DO/Cockpit/ki-98/hier.him", "bf109");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictGear = 0.0F;
        bNeedSetUp = true;
        pictTE = 0.0F;
        pictTR = 0.0F;
        pictTA = 0.0F;
        tmpP = new Point3d();
        tmpV = new Vector3d();
        cockpitNightMats = (new String[] {
            "Gauges_01", "Gauges_02", "Gauges_03", "Gauges_04", "Gauges_05", "Gauges_06", "Gauges_07", "DGauges_01", "DGauges_02", "DGauges_03", 
            "DGauges_04", "DGauges_05", "DGauges_06"
        });
        setNightMats(false);
        cockpitDimControl = !cockpitDimControl;
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        mesh.materialReplace("Gloss1D0o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Matt1D0o"));
        mesh.materialReplace("Matt1D0o", mat);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        mesh.chunkSetAngles("CanopyOpenhnd", -720F * fm.CT.getCockpitDoor(), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[1] = Aircraft.cvt(fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.5F);
        Cockpit.xyz[2] = Aircraft.cvt(fm.CT.getCockpitDoor(), 0.01F, 0.5F, 0.0F, 0.02F);
        Cockpit.ypr[2] = Aircraft.cvt(fm.CT.getCockpitDoor(), 0.01F, 0.1F, 0.0F, 0.0F);
        mesh.chunkSetLocate("Canopy", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.CT.getCockpitDoor(), 0.0F, 0.1F, 0.0F, 0.0132F);
        mesh.chunkSetLocate("CanopyOpenRodL", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = cvt(fm.CT.getCockpitDoor(), 0.0F, 0.1F, 0.0F, -0.0128F);
        mesh.chunkSetLocate("CanopyOpenRodR", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("GSDimmArm", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 64F, 0.0F), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, -0.0594F, 0.0F);
        mesh.chunkSetLocate("GSDimmBase", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("IgnitionSwitch", cvt(fm.EI.engines[0].getControlMagnetos(), 0.0F, 3F, 0.0F, 113F), 0.0F, 0.0F);
        mesh.chunkSetAngles("GearHandle", cvt(pictGear = 0.9F * pictGear + 0.1F * fm.CT.GearControl, 0.0F, 1.0F, 0.0F, -31F), 0.0F, 0.0F);
        mesh.chunkSetAngles("GearHandleMan", 0.0F, 0.0F, 0.0F);
        if(fm.CT.FlapsControl != 0.19F)
            mesh.chunkSetAngles("FlapHandle", -48F * setNew.flap, 0.0F, 0.0F);
        mesh.chunkSetAngles("TQHandle", 60F * setNew.throttle, 0.0F, 0.0F);
        mesh.chunkSetAngles("meto", 20F * setNew.throttle, 0.0F, 0.0F);
        mesh.chunkSetAngles("TRigger", fm.CT.saveWeaponControl[1] ? -12F : 0.0F, 0.0F, 0.0F);
        mesh.chunkSetAngles("PropPitchLvr", 60F * setNew.pitch, 0.0F, 0.0F);
        mesh.chunkSetAngles("MixLvr", 45F * setNew.mix, 0.0F, 0.0F);
        mesh.chunkSetAngles("ChargerLvr", cvt(fm.EI.engines[0].getControlCompressor(), 0.0F, 2.0F, 0.0F, 60F), 0.0F, 0.0F);
        mesh.chunkSetAngles("ChargerAutoLvr", cvt(fm.EI.engines[0].getControlCompressor(), 0.0F, 2.0F, 0.0F, 49F), 0.0F, 0.0F);
        mesh.chunkSetAngles("CowlFlapLvr", 60F * fm.EI.engines[0].getControlRadiator(), 0.0F, 0.0F);
        mesh.chunkSetAngles("OilCoolerLvr", 60F * fm.EI.engines[0].getControlRadiator(), 0.0F, 0.0F);
        mesh.chunkSetAngles("PedalCrossBar", 25F * fm.CT.getRudder(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Pedal_L", 35F * fm.CT.getBrake(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Pedal_R", 35F * fm.CT.getBrake(), 0.0F, 0.0F);
        mesh.chunkSetAngles("FLCS", (pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl) * 20F, 0.0F, 20F * (pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl));
        mesh.chunkSetAngles("FLCSRod", 0.0F, 0.0F, -20F * pictElev);
        mesh.chunkSetAngles("ElevTrim", -450F * (pictTE = 0.9F * pictTE + 0.1F * fm.CT.trimElevator), 0.0F, 0.0F);
        mesh.chunkSetAngles("AronTrim", 450F * (pictTA = 0.9F * pictTA + 0.1F * fm.CT.trimAileron), 0.0F, 0.0F);
        if(cockpitLightControl)
            mesh.chunkSetAngles("SW_UVLight", 0.0F, 0.0F, 0.0F);
        else
            mesh.chunkSetAngles("SW_UVLight", 0.0F, 0.0F, -54F);
        resetYPRmodifier();
        float f1 = pictTE;
        if(f1 < 0.0F)
            Cockpit.xyz[1] = cvt(f1, -0.25F, 0.0F, -0.02305F, 0.0F);
        else
            Cockpit.xyz[1] = cvt(f1, 0.0F, 0.5F, 0.0F, 0.04985F);
        mesh.chunkSetLocate("NeedElevTrim", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("NeedRuddrTrim", cvt(pictTR, -0.5F, 0.5F, 90F, -90F), 0.0F, 0.0F);
        if((fm.AS.astateCockpitState & 0x40) == 0 || (fm.AS.astateCockpitState & 0x10) == 0 || (fm.AS.astateCockpitState & 4) == 0)
        {
            mesh.chunkSetAngles("NeedAHCyl", 0.0F, -fm.Or.getKren(), 0.0F);
            mesh.chunkSetAngles("NeedAHBar", 0.0F, 0.0F, cvt(fm.Or.getTangage(), -45F, 45F, 20F, -20F));
        }
        if((fm.AS.astateCockpitState & 0x40) == 0 || (fm.AS.astateCockpitState & 8) == 0 || (fm.AS.astateCockpitState & 0x20) == 0)
        {
            mesh.chunkSetAngles("NeedCompass_A", 0.0F, 0.0F, cvt(fm.Or.getTangage(), -20F, 20F, 20F, -20F));
            mesh.chunkSetAngles("Compasst", -setNew.azimuth.getDeg(f) - 90F, 0.0F, 0.0F);
        }
        mesh.chunkSetAngles("NeedAlt_Km", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedAlt_M", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        mesh.chunkSetAngles("oxyalt", cvt(interp(setNew.altimeter, setOld.altimeter, f), 3000F, 11000F, 0.0F, 175F), 0.0F, 0.0F);
        mesh.chunkSetAngles("oxypress", cvt(fm.M.nitro, 0.0F, 3F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("slipb", cvt(getBall(8D), -8F, 8F, -10F, 10F), 0.0F, 0.0F);
        w.set(fm.getW());
        fm.Or.transform(w);
        mesh.chunkSetAngles("NeedTurn", cvt(w.z, -0.23562F, 0.23562F, 25F, -25F), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -12F, 15F, -0.104F, 0.0F);
        mesh.chunkSetLocate("Z_Climb1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -12F, 15F, 0.018F, 0.0F);
        mesh.chunkSetLocate("Z_Climb2", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -12F, 15F, 0.017F, 0.0F);
        mesh.chunkSetLocate("Z_Climb3", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -12F, 15F, 0.018F, 0.0F);
        mesh.chunkSetLocate("Z_Climb4", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -12F, 15F, 0.017F, 0.0F);
        mesh.chunkSetLocate("Z_Climb5", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -12F, 15F, 0.018F, 0.0F);
        mesh.chunkSetLocate("Z_Climb6", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("NeedClimb", cvt(setNew.vspeed, -10F, 10F, -180F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedCylTemp", cvt(fm.EI.engines[0].tWaterOut, 0.0F, 360F, 0.0F, 90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedExhTemp", cvt(fm.EI.engines[0].tWaterOut, 0.0F, 324F, 0.0F, 90F), 0.0F, 0.0F);
        if(fm.M.fuel > 400F)
            mesh.chunkSetAngles("NeedFuel", cvt(fm.M.fuel, 400F, 800F, 0.0F, 120F), 0.0F, 0.0F);
        else
            mesh.chunkSetAngles("NeedFuel", cvt(fm.M.fuel, 0.0F, 400F, 0.0F, 270F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedFuelPress", cvt(fm.M.fuel > 1.0F ? 0.26F : 0.0F, 0.0F, 0.6F, 0.0F, -180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedHour", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedMin", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedManPress", cvt(setNew.manifold, 0.33339F, 1.6579F, -125.5F, 125.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedOilPress", cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedVcm", cvt(3F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 175F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedOilTemp", -floatindex(cvt(fm.EI.engines[0].tOilOut, 30F, 110F, 0.0F, 8F), oilTScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedRPM", floatindex(cvt(fm.EI.engines[0].getRPM(), 0.0F, 3500F, 0.0F, 7F), revolutionsScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedSpeed", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 0.0F, 740.7998F, 0.0F, 20F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedVMPressA", cvt(fm.M.nitro, 0.0F, 3F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedVMPressB", cvt((float)Math.sqrt(fm.M.nitro), 0.0F, 8F, 0.0F, -180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("NeedExternalT", floatindex(cvt(Atmosphere.temperature((float)fm.Loc.z), 233.09F, 333.09F, 0.0F, 5F), frAirTempScale), 0.0F, 0.0F);
        if((fm.AS.astateCockpitState & 0x10) == 0)
            mesh.chunkSetAngles("NeedDF", cvt(setNew.waypointAzimuth.getDeg(f), -90F, 90F, -33F, 33F), 0.0F, 0.0F);
        mesh.chunkVisible("FlareGearDn_L", fm.CT.getGear() > 0.98F && fm.Gears.lgear);
        mesh.chunkVisible("FlareGearDn_R", fm.CT.getGear() > 0.98F && fm.Gears.rgear);
        mesh.chunkVisible("FlareGearDn_C", fm.CT.getGear() > 0.98F);
        mesh.chunkVisible("FlareGearUp_L", fm.CT.getGear() < 0.02F || !fm.Gears.lgear);
        mesh.chunkVisible("FlareGearUp_R", fm.CT.getGear() < 0.02F || !fm.Gears.rgear);
        mesh.chunkVisible("FlareGearUp_C", fm.CT.getGear() < 0.02F);
        mesh.chunkVisible("FlareBoostRed", fm.M.fuel > 52.5F);
        mesh.chunkVisible("FlareBoostGreen", fm.M.fuel < 52.5F);
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 4) != 0)
        {
            mesh.chunkVisible("DamageHull2", true);
            mesh.chunkVisible("Gages5_D0", false);
            mesh.chunkVisible("Gages5_D1", true);
            mesh.chunkVisible("NeedFuelA", false);
            mesh.chunkVisible("NeedFuelB", false);
        }
        if((fm.AS.astateCockpitState & 8) != 0)
        {
            mesh.chunkVisible("DamageHull3", true);
            mesh.chunkVisible("Gages2_D0", false);
            mesh.chunkVisible("Gages2_D1", true);
            mesh.chunkVisible("NeedCylTemp", false);
            mesh.chunkVisible("NeedFuelPress", false);
            mesh.chunkVisible("NeedOilPress", false);
            mesh.chunkVisible("NeedOilTemp", false);
        }
        if((fm.AS.astateCockpitState & 0x10) != 0)
        {
            mesh.chunkVisible("DamageHull2", true);
            mesh.chunkVisible("Gages4_D0", false);
            mesh.chunkVisible("Gages4_D1", true);
            mesh.chunkVisible("NeedSpeed", false);
        }
        if((fm.AS.astateCockpitState & 0x20) != 0)
        {
            mesh.chunkVisible("DamageHull3", true);
            mesh.chunkVisible("Gages3_D0", false);
            mesh.chunkVisible("Gages3_D1", true);
            mesh.chunkVisible("NeedAlt_Km", false);
            mesh.chunkVisible("NeedAlt_M", false);
            mesh.chunkVisible("NeedClimb", false);
            mesh.chunkVisible("NeedVMPressA", false);
            mesh.chunkVisible("NeedVMPressB", false);
        }
        if((fm.AS.astateCockpitState & 0x80) != 0)
            mesh.chunkVisible("OilSplats", true);
        if((fm.AS.astateCockpitState & 2) != 0)
        {
            mesh.chunkVisible("GunSight_T3", false);
            mesh.chunkVisible("DGS_Lenz", true);
            mesh.chunkVisible("GSGlassMain", false);
            mesh.chunkVisible("GSDimmArm", false);
            mesh.chunkVisible("GSDimmBase", false);
            mesh.chunkVisible("GSGlassDimm", false);
            mesh.chunkVisible("DGunSight_T3", true);
            mesh.chunkVisible("DGS_Lenz", true);
            mesh.chunkVisible("Z_Z_RETICLE", false);
            mesh.chunkVisible("Z_Z_MASK", false);
            mesh.chunkVisible("DamageGlass1", true);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
        {
            mesh.chunkVisible("DamageGlass2", true);
            mesh.chunkVisible("DamageGlass3", true);
        }
        if((fm.AS.astateCockpitState & 0x40) != 0)
        {
            mesh.chunkVisible("DamageHull1", true);
            mesh.chunkVisible("Gages1_D0", false);
            mesh.chunkVisible("Gages1_D1", true);
            mesh.chunkVisible("NeedRPM", false);
            mesh.chunkVisible("NeedManPress", false);
            mesh.chunkVisible("NeedExhTemp", false);
            mesh.chunkVisible("NeedTurn", false);
            mesh.chunkVisible("NeedBank", false);
        }
        retoggleLight();
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

    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    public Vector3f w;
    private float pictAiler;
    private float pictElev;
    private float pictGear;
    private float pictTE;
    private float pictTR;
    private float pictTA;
    private boolean bNeedSetUp;
    private static final float speedometerScale[] = {
        0.0F, 30F, 75F, 90F, 135F, 180F, 221F, 270F, 315F, 360F, 
        405F, 450F, 490F, 520F, 545F, 580F, 605.5F, 635F, 675F, 698F, 
        725F
    };
    private static final float revolutionsScale[] = {
        0.0F, 20F, 75F, 125F, 180F, 220F, 280F, 330F
    };
    private static final float oilTScale[] = {
        0.0F, 20F, 70.5F, 122.5F, 175F, 237.5F, 290.5F, 338F
    };
    private static final float frAirTempScale[] = {
        0.0F, 20.5F, 37F, 48.5F, 60.5F, 75.5F
    };
    private Point3d tmpP;
    private Vector3d tmpV;

}
