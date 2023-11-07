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
import com.maddox.sound.ReverbFXRoom;

public class CockpitH4 extends CockpitPilot
{
    private class Variables
    {

        float throttle1;
        float throttle2;
        float prop1;
        float prop2;
        float mix1;
        float mix2;
        float man1;
        float man2;
        float altimeter;
        AnglesFork azimuth;
        float vspeed;

        private Variables()
        {
            azimuth = new AnglesFork();
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
                setNew.throttle1 = 0.9F * setOld.throttle1 + 0.1F * fm.EI.engines[0].getControlThrottle();
                setNew.prop1 = 0.9F * setOld.prop1 + 0.1F * fm.EI.engines[0].getControlProp();
                setNew.mix1 = 0.8F * setOld.mix1 + 0.2F * fm.EI.engines[0].getControlMix();
                setNew.throttle2 = 0.9F * setOld.throttle2 + 0.1F * fm.EI.engines[1].getControlThrottle();
                setNew.prop2 = 0.9F * setOld.prop2 + 0.1F * fm.EI.engines[1].getControlProp();
                setNew.mix2 = 0.8F * setOld.mix2 + 0.2F * fm.EI.engines[1].getControlMix();
                setNew.man1 = 0.92F * setOld.man1 + 0.08F * fm.EI.engines[0].getManifoldPressure();
                setNew.man2 = 0.92F * setOld.man2 + 0.08F * fm.EI.engines[1].getManifoldPressure();
                setNew.altimeter = fm.getAltitude();
                setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
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

    public CockpitH4()
    {
        super("3DO/Cockpit/H-4/hier.him", "he111");
        bNeedSetUp = true;
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        tmpP = new Point3d();
        tmpV = new Vector3d();
        cockpitNightMats = (new String[] {
            "gauges5", "GP1_d1", "GP1", "GP2_d1", "GP2", "GP3", "GP4_d1", "GP4", "GP5_d1", "GP5", 
            "GP6_d1", "GP6", "GP7", "GP9", "throttle", "Volt_Tacho"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
        if(acoustics != null)
            acoustics.globFX = new ReverbFXRoom(0.45F);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        mesh.chunkSetAngles("Z_Columnbase", 0.0F, 0.0F, 10F * (pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl));
        mesh.chunkSetAngles("Z_ColumnL", 0.0F, 0.0F, -60.6F * (pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl));
        mesh.chunkSetAngles("Z_ColumnR", 0.0F, 0.0F, -60.6F * pictAiler);
        mesh.chunkSetAngles("Z_Throtle1", 40F * interp(setNew.throttle1, setOld.throttle1, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Throtle2", 40F * interp(setNew.throttle2, setOld.throttle2, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Prop1", 75.5F * interp(setNew.prop1, setOld.prop1, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Prop2", 75.5F * interp(setNew.prop2, setOld.prop2, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Mixture1", 62.9F * interp(setNew.mix1, setOld.mix1, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Mixture2", 62.9F * interp(setNew.mix2, setOld.mix2, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_RPedal", 0.0F, 0.0F, -22.2F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_RPedalStep1", 0.0F, 0.0F, -22.2F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_RPedalStep2", 0.0F, 0.0F, -22.2F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_LPedal", 0.0F, 0.0F, -22.2F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_LPedalStep1", 0.0F, 0.0F, -22.2F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_LPedalStep2", 0.0F, 0.0F, -22.2F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_Compass1", 0.0F, -setNew.azimuth.getDeg(f), 0.0F);
        mesh.chunkSetAngles("Z_Compass2", 0.0F, setNew.azimuth.getDeg(f), 0.0F);
        mesh.chunkSetAngles("Z_Compass3", 0.0F, setNew.azimuth.getDeg(f), 0.0F);
        mesh.chunkSetAngles("Z_Compass4", 0.0F, setNew.azimuth.getDeg(f), 0.0F);
        mesh.chunkSetAngles("Z_Compass5", -setNew.azimuth.getDeg(f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AH1", 0.0F, 0.0F, fm.Or.getKren());
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -45F, 45F, -0.03F, 0.03F);
        mesh.chunkSetLocate("Z_AH2", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_AH3", 0.0F, 0.0F, fm.Or.getKren());
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -45F, 45F, -0.03F, 0.03F);
        mesh.chunkSetLocate("Z_AH4", Cockpit.xyz, Cockpit.ypr);
        w.set(fm.getW());
        fm.Or.transform(w);
        float f1 = getBall(7D);
        mesh.chunkSetAngles("Z_TurnBank1", cvt(f1, -5F, 5F, 8.5F, -8.5F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank2", cvt(w.z, -0.23562F, 0.23562F, 22F, -22F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank3", cvt(f1, -7F, 7F, 16F, -16F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank4", cvt(w.z, -0.23562F, 0.23562F, 22F, -22F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank5", cvt(f1, -7F, 7F, 16F, -16F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Speedometer1", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 0.0F, 300F, 0.0F, 15F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Speedometer2", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 0.0F, 300F, 0.0F, 15F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Altimeter1", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 1440F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Altimeter2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 1440F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp1", cvt(fm.EI.engines[1].tWaterOut, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp2", cvt(fm.EI.engines[0].tWaterOut, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp3", cvt(fm.EI.engines[1].tOilIn, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp4", cvt(fm.EI.engines[0].tOilIn, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        float f2 = (Atmosphere.temperature((float)fm.Loc.z) - 273.09F) + 44.4F * fm.EI.engines[0].getPowerOutput();
        if(f2 < 0.0F)
            mesh.chunkSetAngles("Z_Temp5", cvt(f2, -40F, 0.0F, 0.0F, 45F), 0.0F, 0.0F);
        else
            mesh.chunkSetAngles("Z_Temp5", cvt(f2, 0.0F, 60F, 45F, 90F), 0.0F, 0.0F);
        f2 = (Atmosphere.temperature((float)fm.Loc.z) - 273.09F) + 44.4F * fm.EI.engines[1].getPowerOutput();
        if(f2 < 0.0F)
            mesh.chunkSetAngles("Z_Temp6", cvt(f2, -40F, 0.0F, 0.0F, 45F), 0.0F, 0.0F);
        else
            mesh.chunkSetAngles("Z_Temp6", cvt(f2, 0.0F, 60F, 45F, 90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp7", floatindex(cvt(fm.EI.engines[1].tOilOut, 0.0F, 120F, 0.0F, 12F), oilTempScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp8", floatindex(cvt(fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 12F), oilTempScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Flap1", cvt(fm.CT.getFlap(), 0.0F, 0.75F, 0.0F, 90F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Flap2", 57F * fm.CT.getFlap(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuel1", cvt((float)Math.sqrt(fm.M.fuel), 0.0F, 34.641F, 0.0F, 225F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuel2", cvt((float)Math.sqrt(fm.M.fuel), 0.0F, 34.641F, 0.0F, 225F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuel3", cvt((float)Math.sqrt(fm.M.fuel), 0.0F, 38.729F, 0.0F, 225F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuel4", cvt((float)Math.sqrt(fm.M.fuel), 26.925F, 38.729F, 0.0F, 225F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Fuel5", cvt((float)Math.sqrt(fm.M.fuel), 26.925F, 38.729F, 0.0F, 225F), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(setNew.vspeed, -15F, 15F, -0.055F, 0.055F);
        mesh.chunkSetLocate("Z_Climb1", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_Climb2", cvt(setNew.vspeed, -10F, 10F, -180F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Manifold1", cvt(setNew.man1, 0.33339F, 1.66661F, -162F, 162F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Manifold2", cvt(setNew.man2, 0.33339F, 1.66661F, -162F, 162F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Oil1", cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut * fm.EI.engines[0].getReadyness(), 0.0F, 7.45F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Oil2", cvt(1.0F + 0.05F * fm.EI.engines[1].tOilOut * fm.EI.engines[1].getReadyness(), 0.0F, 7.45F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_fuelpress1", cvt(fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 0.5F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_fuelpress2", cvt(fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 0.5F, 0.0F, 180F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_RPM1", floatindex(cvt(fm.EI.engines[0].getRPM(), 0.0F, 3500F, 0.0F, 7F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_RPM2", floatindex(cvt(fm.EI.engines[1].getRPM(), 0.0F, 3500F, 0.0F, 7F), rpmScale), 0.0F, 0.0F);
        if((fm.AS.astateCockpitState & 0x40) == 0)
        {
            mesh.chunkVisible("Z_GearGreen1", fm.CT.getGear() > 0.99F);
            mesh.chunkVisible("Z_GearGreen2", fm.CT.getGear() > 0.99F && fm.Gears.rgear);
            mesh.chunkVisible("Z_GearGreen3", fm.CT.getGear() > 0.99F && fm.Gears.lgear);
            mesh.chunkVisible("Z_GearRed1", fm.CT.getGear() < 0.01F);
            mesh.chunkVisible("Z_GearRed2", fm.CT.getGear() < 0.01F || !fm.Gears.rgear);
            mesh.chunkVisible("Z_GearRed3", fm.CT.getGear() < 0.01F || !fm.Gears.lgear);
        }
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 2) != 0)
        {
            mesh.chunkVisible("XGlassDamage1", true);
            mesh.chunkVisible("XHullDamage5", true);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
        {
            mesh.chunkVisible("XGlassDamage2", true);
            mesh.chunkVisible("XGlassDamage5", true);
            mesh.chunkVisible("XHullDamage4", true);
        }
        if((fm.AS.astateCockpitState & 0x40) != 0)
        {
            mesh.chunkVisible("Panel_D0", false);
            mesh.chunkVisible("Panel_D1", true);
            mesh.chunkVisible("Z_Fuel1", false);
            mesh.chunkVisible("Z_Fuel2", false);
            mesh.chunkVisible("Z_TurnBank2", false);
            mesh.chunkVisible("Z_TurnBank3", false);
            mesh.chunkVisible("Z_TurnBank4", false);
            mesh.chunkVisible("Z_TurnBank5", false);
            mesh.chunkVisible("Z_Flap2", false);
            mesh.chunkVisible("Z_GearGreen1", false);
            mesh.chunkVisible("Z_GearGreen2", false);
            mesh.chunkVisible("Z_GearGreen3", false);
            mesh.chunkVisible("Z_GearRed1", false);
            mesh.chunkVisible("Z_GearRed2", false);
            mesh.chunkVisible("Z_GearRed3", false);
            mesh.chunkVisible("Z_RPM1", false);
            mesh.chunkVisible("Z_RPM2", false);
            mesh.chunkVisible("Z_Oil1", false);
            mesh.chunkVisible("Z_Oil2", false);
            mesh.chunkVisible("Z_fuelpress1", false);
            mesh.chunkVisible("Z_fuelpress2", false);
            mesh.chunkVisible("Z_Temp1", false);
            mesh.chunkVisible("Z_Temp2", false);
        }
        if((fm.AS.astateCockpitState & 4) != 0)
        {
            mesh.chunkVisible("XGlassDamage3", true);
            mesh.chunkVisible("XHullDamage2", true);
        }
        if((fm.AS.astateCockpitState & 8) != 0)
        {
            mesh.chunkVisible("XHullDamage1", true);
            mesh.chunkVisible("XHullDamage4", true);
        }
        if((fm.AS.astateCockpitState & 0x10) != 0)
        {
            mesh.chunkVisible("XGlassDamage4", true);
            mesh.chunkVisible("XHullDamage3", true);
            mesh.chunkVisible("XHullDamage5", true);
        }
        if((fm.AS.astateCockpitState & 0x20) != 0)
        {
            mesh.chunkVisible("XGlassDamage5", true);
            mesh.chunkVisible("XHullDamage1", true);
        }
        retoggleLight();
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

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        mesh.materialReplace("Gloss1D0o", mat);
    }

    private boolean bNeedSetUp;
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    public Vector3f w;
    private float pictAiler;
    private float pictElev;
    private static final float speedometerScale[] = {
        0.0F, 6.5F, 30F, 66F, 105F, 158.5F, 212F, 272.5F, 333F, 384F, 
        432.5F, 479.5F, 526.5F, 573.5F, 624.5F, 674F
    };
    private static final float oilTempScale[] = {
        0.0F, 4F, 17.5F, 38F, 63F, 90.5F, 115F, 141.3F, 180F, 221.7F, 
        269.5F, 311F, 357F
    };
    private static final float rpmScale[] = {
        0.0F, 10F, 75F, 126.5F, 179.5F, 232F, 284.5F, 336F
    };
    private Point3d tmpP;
    private Vector3d tmpV;

}
