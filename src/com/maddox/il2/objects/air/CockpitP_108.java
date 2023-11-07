package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitP_108 extends CockpitPilot
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
                setNew.throttle1 = 0.9F * setOld.throttle1 + 0.1F * fm.EI.engines[0].getControlThrottle();
                setNew.throttle2 = 0.9F * setOld.throttle2 + 0.1F * fm.EI.engines[1].getControlThrottle();
                setNew.throttle3 = 0.9F * setOld.throttle3 + 0.1F * fm.EI.engines[2].getControlThrottle();
                setNew.throttle4 = 0.9F * setOld.throttle4 + 0.1F * fm.EI.engines[3].getControlThrottle();
                setNew.prop1 = 0.9F * setOld.prop1 + 0.1F * fm.EI.engines[0].getControlProp();
                setNew.prop2 = 0.9F * setOld.prop2 + 0.1F * fm.EI.engines[1].getControlProp();
                setNew.prop3 = 0.9F * setOld.prop3 + 0.1F * fm.EI.engines[2].getControlProp();
                setNew.prop4 = 0.9F * setOld.prop4 + 0.1F * fm.EI.engines[3].getControlProp();
                setNew.mix1 = 0.6F * setOld.mix1 + 0.2F * fm.EI.engines[0].getControlMix();
                setNew.mix2 = 0.6F * setOld.mix2 + 0.2F * fm.EI.engines[1].getControlMix();
                setNew.mix3 = 0.6F * setOld.mix3 + 0.2F * fm.EI.engines[2].getControlMix();
                setNew.mix4 = 0.6F * setOld.mix4 + 0.2F * fm.EI.engines[3].getControlMix();
                setNew.man1 = 0.92F * setOld.man1 + 0.08F * fm.EI.engines[0].getManifoldPressure();
                setNew.man2 = 0.92F * setOld.man2 + 0.08F * fm.EI.engines[1].getManifoldPressure();
                setNew.man3 = 0.92F * setOld.man3 + 0.08F * fm.EI.engines[2].getManifoldPressure();
                setNew.man4 = 0.92F * setOld.man4 + 0.08F * fm.EI.engines[3].getManifoldPressure();
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

    private class Variables
    {

        float throttle1;
        float throttle2;
        float throttle3;
        float throttle4;
        float prop1;
        float prop2;
        float prop3;
        float prop4;
        float mix1;
        float mix2;
        float mix3;
        float mix4;
        float man1;
        float man2;
        float man3;
        float man4;
        float altimeter;
        AnglesFork azimuth;
        float vspeed;

        private Variables()
        {
            azimuth = new AnglesFork();
        }
    }

    protected float waypointAzimuth()
    {
        WayPoint waypoint = this.fm.AP.way.curr();
        if(waypoint == null)
            return 0.0F;
        waypoint.getP(tmpP);
        tmpV.sub(tmpP, this.fm.Loc);
        float f = (float)(Math.toDegrees(Math.atan2(-tmpV.y, tmpV.x)));
        while (f <= -180F) f += 360F;
        while (f > 180F) f -= 360F;
        return f;
    }

    public CockpitP_108()
    {
        super("3DO/Cockpit/P108/hier.him", "he111");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        tmpP = new Point3d();
        tmpV = new Vector3d();
        this.cockpitNightMats = (new String[] {
            "gauges5", "GP1", "GP2", "GP3", "GP4", "GP5", "GP6", "GP7", "GP9", "throttle", 
            "Volt_Tacho"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
        if(this.acoustics != null)
            this.acoustics.globFX = new ReverbFXRoom(0.45F);
    }

    public void reflectWorldToInstruments(float f)
    {
        this.mesh.chunkSetAngles("Z_Columnbase", 0.0F, 0.0F, 10F * (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl));
        this.mesh.chunkSetAngles("Z_ColumnL", 0.0F, 0.0F, -60.6F * (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl));
        this.mesh.chunkSetAngles("Z_ColumnR", 0.0F, 0.0F, -60.6F * pictAiler);
        this.mesh.chunkSetAngles("Z_Throtle1", 40F * interp(setNew.throttle1, setOld.throttle1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throtle2", 40F * interp(setNew.throttle2, setOld.throttle2, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throtle3", 40F * interp(setNew.throttle3, setOld.throttle3, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throtle4", 40F * interp(setNew.throttle4, setOld.throttle4, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop1", 75.5F * interp(setNew.prop1, setOld.prop1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop2", 75.5F * interp(setNew.prop2, setOld.prop2, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop3", 75.5F * interp(setNew.prop3, setOld.prop3, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop4", 75.5F * interp(setNew.prop4, setOld.prop4, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture1", 50F * interp(setNew.mix1, setOld.mix1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture2", 50F * interp(setNew.mix2, setOld.mix2, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture3", 50F * interp(setNew.mix3, setOld.mix3, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture4", 50F * interp(setNew.mix4, setOld.mix4, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPedal", 0.0F, 0.0F, -22.2F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_RPedalStep1", 0.0F, 0.0F, -22.2F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_RPedalStep2", 0.0F, 0.0F, -22.2F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_LPedal", 0.0F, 0.0F, -22.2F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_LPedalStep1", 0.0F, 0.0F, -22.2F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_LPedalStep2", 0.0F, 0.0F, -22.2F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_Compass1", 0.0F, -setNew.azimuth.getDeg(f), 0.0F);
        this.mesh.chunkSetAngles("Z_Compass2", 0.0F, setNew.azimuth.getDeg(f), 0.0F);
        this.mesh.chunkSetAngles("Z_Compass3", 0.0F, setNew.azimuth.getDeg(f), 0.0F);
        this.mesh.chunkSetAngles("Z_Compass4", 0.0F, setNew.azimuth.getDeg(f), 0.0F);
        this.mesh.chunkSetAngles("Z_Compass5", -setNew.azimuth.getDeg(f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AH1", 0.0F, 0.0F, this.fm.Or.getKren());
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(this.fm.Or.getTangage(), -45F, 45F, -0.03F, 0.03F);
        this.mesh.chunkSetLocate("Z_AH2", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_AH3", 0.0F, 0.0F, this.fm.Or.getKren());
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(this.fm.Or.getTangage(), -45F, 45F, -0.03F, 0.03F);
        this.mesh.chunkSetLocate("Z_AH4", Cockpit.xyz, Cockpit.ypr);
        w.set(this.fm.getW());
        this.fm.Or.transform(w);
        float f1 = getBall(7D);
        this.mesh.chunkSetAngles("Z_TurnBank1", cvt(f1, -5F, 5F, 8.5F, -8.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank2", cvt(w.z, -0.23562F, 0.23562F, 22F, -22F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank3", cvt(f1, -7F, 7F, 16F, -16F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank4", cvt(w.z, -0.23562F, 0.23562F, 22F, -22F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank5", cvt(f1, -7F, 7F, 16F, -16F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer1", floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 300F, 0.0F, 15F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer2", floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 300F, 0.0F, 15F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter1", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 1440F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 1440F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp1w", cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp2w", cvt(this.fm.EI.engines[1].tWaterOut, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp3w", cvt(this.fm.EI.engines[2].tWaterOut, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp4w", cvt(this.fm.EI.engines[3].tWaterOut, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp1o", cvt(this.fm.EI.engines[0].tOilIn, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp2o", cvt(this.fm.EI.engines[1].tOilIn, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp4o", cvt(this.fm.EI.engines[2].tOilIn, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp5o", cvt(this.fm.EI.engines[3].tOilIn, 0.0F, 350F, 0.0F, 90F), 0.0F, 0.0F);
        float f2 = (Atmosphere.temperature((float)this.fm.Loc.z) - 273.09F) + 44.4F * this.fm.EI.engines[0].getPowerOutput();
        if(f2 < 0.0F)
            this.mesh.chunkSetAngles("Z_Temp5", cvt(f2, -40F, 0.0F, 0.0F, 45F), 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("Z_Temp5", cvt(f2, 0.0F, 60F, 45F, 90F), 0.0F, 0.0F);
        float f22 = (Atmosphere.temperature((float)this.fm.Loc.z) - 273.09F) + 44.4F * this.fm.EI.engines[2].getPowerOutput();
        if(f22 < 0.0F)
            this.mesh.chunkSetAngles("Z_Temp52", cvt(f22, -40F, 0.0F, 0.0F, 45F), 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("Z_Temp52", cvt(f22, 0.0F, 60F, 45F, 90F), 0.0F, 0.0F);
        float f21 = (Atmosphere.temperature((float)this.fm.Loc.z) - 273.09F) + 44.4F * this.fm.EI.engines[1].getPowerOutput();
        if(f21 < 0.0F)
            this.mesh.chunkSetAngles("Z_Temp6", cvt(f21, -40F, 0.0F, 0.0F, 45F), 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("Z_Temp6", cvt(f21, 0.0F, 60F, 45F, 90F), 0.0F, 0.0F);
        float f24 = (Atmosphere.temperature((float)this.fm.Loc.z) - 273.09F) + 44.4F * this.fm.EI.engines[3].getPowerOutput();
        if(f24 < 0.0F)
            this.mesh.chunkSetAngles("Z_Temp64", cvt(f24, -40F, 0.0F, 0.0F, 45F), 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("Z_Temp64", cvt(f24, 0.0F, 60F, 45F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp7o", floatindex(cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 12F), oilTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp8o", floatindex(cvt(this.fm.EI.engines[1].tOilOut, 0.0F, 120F, 0.0F, 12F), oilTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp9o", floatindex(cvt(this.fm.EI.engines[2].tOilOut, 0.0F, 120F, 0.0F, 12F), oilTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp10o", floatindex(cvt(this.fm.EI.engines[3].tOilOut, 0.0F, 120F, 0.0F, 12F), oilTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Flap1", cvt(this.fm.CT.getFlap(), 0.0F, 0.75F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Flap2", 57F * this.fm.CT.getFlap(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel1", cvt((float)Math.sqrt(this.fm.M.fuel), 0.0F, 34.641F, 0.0F, 225F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel2", cvt((float)Math.sqrt(this.fm.M.fuel), 0.0F, 34.641F, 0.0F, 225F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel3", cvt((float)Math.sqrt(this.fm.M.fuel), 0.0F, 38.729F, 0.0F, 225F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel4", cvt((float)Math.sqrt(this.fm.M.fuel), 26.925F, 38.729F, 0.0F, 225F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel5", cvt((float)Math.sqrt(this.fm.M.fuel), 26.925F, 38.729F, 0.0F, 225F), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(setNew.vspeed, -15F, 15F, -0.055F, 0.055F);
        this.mesh.chunkSetLocate("Z_Climb1", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Climb2", cvt(setNew.vspeed, -10F, 10F, -180F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oil1", cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness(), 0.0F, 7.45F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oil2", cvt(1.0F + 0.05F * this.fm.EI.engines[1].tOilOut * this.fm.EI.engines[1].getReadyness(), 0.0F, 7.45F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oil3", cvt(1.0F + 0.05F * this.fm.EI.engines[2].tOilOut * this.fm.EI.engines[2].getReadyness(), 0.0F, 7.45F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oil4", cvt(1.0F + 0.05F * this.fm.EI.engines[3].tOilOut * this.fm.EI.engines[3].getReadyness(), 0.0F, 7.45F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_fuelpress1", cvt(this.fm.M.fuel > 1.0F ? 0.26F : 0.0F, 0.0F, 0.5F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_fuelpress2", cvt(this.fm.M.fuel > 1.0F ? 0.26F : 0.0F, 0.0F, 0.5F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_fuelpress3", cvt(this.fm.M.fuel > 1.0F ? 0.26F : 0.0F, 0.0F, 0.5F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_fuelpress4", cvt(this.fm.M.fuel > 1.0F ? 0.26F : 0.0F, 0.0F, 0.5F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM1", floatindex(cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 3500F, 0.0F, 7F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM2", floatindex(cvt(this.fm.EI.engines[1].getRPM(), 0.0F, 3500F, 0.0F, 7F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM3", floatindex(cvt(this.fm.EI.engines[2].getRPM(), 0.0F, 3500F, 0.0F, 7F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM4", floatindex(cvt(this.fm.EI.engines[3].getRPM(), 0.0F, 3500F, 0.0F, 7F), rpmScale), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 0x40) == 0)
        {
            this.mesh.chunkVisible("Z_GearGreen2", this.fm.CT.getGear() > 0.99F && this.fm.Gears.rgear);
            this.mesh.chunkVisible("Z_GearGreen3", this.fm.CT.getGear() > 0.99F && this.fm.Gears.lgear);
            this.mesh.chunkVisible("Z_GearRed2", this.fm.CT.getGear() < 0.01F || !this.fm.Gears.rgear);
            this.mesh.chunkVisible("Z_GearRed3", this.fm.CT.getGear() < 0.01F || !this.fm.Gears.lgear);
        }
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 1) != 0)
            this.mesh.chunkVisible("XGlassDamage1", true);
        if((this.fm.AS.astateCockpitState & 2) != 0)
        {
            this.mesh.chunkVisible("XGlassDamage2", true);
            this.mesh.chunkVisible("XGlassDamage3", true);
        }
        if((this.fm.AS.astateCockpitState & 4) != 0)
            this.mesh.chunkVisible("XGlassDamage4", true);
        if((this.fm.AS.astateCockpitState & 8) != 0)
            this.mesh.chunkVisible("XGlassDamage5", true);
        retoggleLight();
    }

    public void toggleLight()
    {
        this.cockpitLightControl = !this.cockpitLightControl;
        if(this.cockpitLightControl)
            setNightMats(true);
        else
            setNightMats(false);
    }

    private void retoggleLight()
    {
        if(this.cockpitLightControl)
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
