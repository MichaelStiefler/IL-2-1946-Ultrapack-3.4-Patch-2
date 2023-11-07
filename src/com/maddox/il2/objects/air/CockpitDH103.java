package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitDH103 extends CockpitPilot
{
    private class Variables
    {

        float throttle0;
        float throttle1;
        float prop0;
        float prop1;
        float mix0;
        float mix1;
        float altimeter;
        float azimuth;
        float vspeed;
        float gearPhi;
        float waypointAzimuth;

        private Variables()
        {
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
                setNew.throttle0 = 0.92F * setOld.throttle0 + 0.08F * fm.EI.engines[0].getControlThrottle();
                setNew.prop0 = 0.92F * setOld.prop0 + 0.08F * fm.EI.engines[0].getControlProp();
                setNew.mix0 = 0.92F * setOld.mix0 + 0.08F * fm.EI.engines[0].getControlMix();
                setNew.throttle1 = 0.92F * setOld.throttle1 + 0.08F * fm.EI.engines[1].getControlThrottle();
                setNew.prop1 = 0.92F * setOld.prop1 + 0.08F * fm.EI.engines[1].getControlProp();
                setNew.mix1 = 0.92F * setOld.mix1 + 0.08F * fm.EI.engines[1].getControlMix();
                setNew.altimeter = fm.getAltitude();
                setNew.azimuth = 0.97F * setOld.azimuth + 0.03F * -fm.Or.getYaw();
                if(setOld.azimuth > 270F && setNew.azimuth < 90F)
                    setOld.azimuth -= 360F;
                if(setOld.azimuth < 90F && setNew.azimuth > 270F)
                    setOld.azimuth += 360F;
                setNew.waypointAzimuth = 0.91F * setOld.waypointAzimuth + 0.09F * (waypointAzimuth() - setOld.azimuth) + World.Rnd().nextFloat(-10F, 10F);
                setNew.vspeed = 0.99F * setOld.vspeed + 0.01F * fm.getVertSpeed();
                if(fm.CT.GearControl < 0.5F)
                {
                    if(setNew.gearPhi > 0.0F)
                        setNew.gearPhi = setOld.gearPhi - 0.021F;
                } else
                if(setNew.gearPhi < 1.0F)
                    setNew.gearPhi = setOld.gearPhi + 0.021F;
            }
            return true;
        }

        Interpolater()
        {
        }
    }

    protected float waypointAzimuth()
    {
        WayPoint waypoint = this.fm.AP.way.curr();
        if(waypoint == null)
        {
            return 0.0F;
        } else
        {
            waypoint.getP(tmpP);
            tmpV.sub(tmpP, this.fm.Loc);
            return (float)(Math.toDegrees(Math.atan2(-tmpV.y, tmpV.x)));
        }
    }

    public CockpitDH103()
    {
        super("3DO/Cockpit/DH-103/hier.him", "he111");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictFlap = 0.0F;
        pictSupc0 = 0.0F;
        pictSupc1 = 0.0F;
        pictLlit = 0.0F;
        pictManf0 = 1.0F;
        pictManf1 = 1.0F;
        bNeedSetUp = true;
        tmpP = new Point3d();
        tmpV = new Vector3d();
        this.cockpitNightMats = (new String[] {
            "TEMPPIT5-op", "TEMPPIT6-op", "TEMPPIT14-op", "TEMPPIT18-op", "TEMPPIT22-op", "TEMPPIT28-op", "TEMPPIT38-op", "TEMPPIT1-tr", "TEMPPIT2-tr", "TEMPPIT3-tr", 
            "TEMPPIT4-tr", "TEMPPIT5-tr", "TEMPPIT6-tr", "TEMPPIT14-tr", "TEMPPIT18-tr", "TEMPPIT22-tr", "TEMPPIT28-tr", "TEMPPIT38-tr", "TEMPPIT1_damage", "TEMPPIT3_damage"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.62F);
        this.mesh.chunkSetLocate("Canopy", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkVisible("XLampGearUpR", this.fm.CT.getGear() > 0.01F && this.fm.CT.getGear() < 0.99F || !this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearUpL", this.fm.CT.getGear() > 0.01F && this.fm.CT.getGear() < 0.99F || !this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearUpC", this.fm.CT.getGear() > 0.01F && this.fm.CT.getGear() < 0.99F);
        this.mesh.chunkVisible("XLampGearDownR", this.fm.CT.getGear() > 0.99F && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearDownL", this.fm.CT.getGear() > 0.99F && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearDownC", this.fm.CT.getGear() > 0.99F);
        this.mesh.chunkSetAngles("Z_Columnbase", 16F * (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", 45F * (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Elev", -16F * pictElev, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(pictAiler, -1F, 1.0F, -0.027F, 0.027F);
        this.mesh.chunkSetLocate("Z_Shlang01", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -Cockpit.xyz[2];
        this.mesh.chunkSetLocate("Z_Shlang02", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Throtle1", 65.45F * interp(setNew.throttle0, setOld.throttle0, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throtle2", 65.45F * interp(setNew.throttle1, setOld.throttle1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_BasePedal", 20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", 0.0F, 0.0F, -20F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_RightPedal", 0.0F, 0.0F, -20F * this.fm.CT.getRudder());
        this.mesh.chunkSetAngles("Z_Gear1", cvt(setNew.gearPhi, 0.2F, 0.8F, 0.0F, 116F), 0.0F, 0.0F);
        if(setNew.gearPhi < 0.5F)
            this.mesh.chunkSetAngles("Z_Gear2", cvt(setNew.gearPhi, 0.0F, 0.2F, 0.0F, -65F), 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("Z_Gear2", cvt(setNew.gearPhi, 0.8F, 1.0F, -65F, 0.0F), 0.0F, 0.0F);
        float f1;
        if(Math.abs(this.fm.CT.FlapsControl - this.fm.CT.getFlap()) > 0.02F)
        {
            if(this.fm.CT.FlapsControl - this.fm.CT.getFlap() > 0.0F)
                f1 = 24F;
            else
                f1 = -24F;
        } else
        {
            f1 = 0.0F;
        }
        this.mesh.chunkSetAngles("Z_Flaps1", pictFlap = 0.8F * pictFlap + 0.2F * f1, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = 0.667F - 0.047F * this.fm.CT.getFlap();
        this.mesh.chunkSetLocate("FlapPos", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Trim1", 1000F * this.fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop1", 72.5F * setNew.prop0, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop2", 72.5F * setNew.prop1, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Surch1", cvt(pictSupc0 = 0.8F * pictSupc0 + 0.1F * (float)this.fm.EI.engines[0].getControlCompressor(), 0.0F, 1.0F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Surch2", cvt(pictSupc1 = 0.8F * pictSupc1 + 0.1F * (float)this.fm.EI.engines[1].getControlCompressor(), 0.0F, 1.0F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Rad1", this.fm.CT.getRadiatorControl() * 70F, 0.0F, 0.0F);
        this.mesh.chunkVisible("HookLight", this.fm.CT.getArrestor() > 0.99F);
        f1 = 0.0F;
        if(this.fm.AS.bLandingLightOn)
            f1 = 66F;
        this.mesh.chunkSetAngles("Z_Land1", pictLlit = 0.85F * pictLlit + 0.15F * f1, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("COMPASS_M", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELK_V_LONG", -floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeed()), 0.0F, 257.2222F, 0.0F, 10F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STREL_ALT_LONG", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 9144F, 0.0F, -10800F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STREL_ALT_SHORT", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 9144F, 0.0F, -1080F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("SHKALA_DIRECTOR", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_VY", -floatindex(cvt(setNew.vspeed, -20.32F, 20.32F, 0.0F, 8F), variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_FUEL1", cvt(this.fm.M.fuel / 1600F, 0.0F, 1.0F, 154F, -154F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_FUEL2", cvt(this.fm.M.fuel / 900F, 0.0F, 1.0F, 154F, -154F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_FUEL3", cvt(this.fm.M.fuel / 850F, 0.0F, 1.0F, 154F, -154F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_FUEL4", cvt(this.fm.M.fuel / 800F, 0.0F, 1.0F, 154F, -154F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_RPM0", -floatindex(cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 5000F, 0.0F, 10F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_BOOST0", cvt(pictManf0 = 0.91F * pictManf0 + 0.09F * this.fm.EI.engines[0].getManifoldPressure(), 0.7242097F, 2.103161F, 65F, -305F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELK_TEMP_OIL0", cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 160F, 0.0F, -306F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELK_TEMP_RAD0", -floatindex(cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 140F, 0.0F, 14F), radScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STR_OIL_LB0", cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, -36F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_RPM1", -floatindex(cvt(this.fm.EI.engines[1].getRPM(), 0.0F, 5000F, 0.0F, 10F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_BOOST1", cvt(pictManf1 = 0.91F * pictManf1 + 0.09F * this.fm.EI.engines[1].getManifoldPressure(), 0.7242097F, 2.103161F, 65F, -305F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELK_TEMP_OIL1", cvt(this.fm.EI.engines[1].tOilOut, 0.0F, 160F, 0.0F, -306F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELK_TEMP_RAD1", -floatindex(cvt(this.fm.EI.engines[1].tWaterOut, 0.0F, 140F, 0.0F, 14F), radScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STR_OIL_LB1", cvt(1.0F + 0.05F * this.fm.EI.engines[1].tOilOut, 0.0F, 10F, 0.0F, -36F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELK_TURN_UP", -cvt(getBall(8D), -8F, 8F, 35F, -35F), 0.0F, 0.0F);
        w.set(this.fm.getW());
        this.fm.Or.transform(w);
        this.mesh.chunkSetAngles("STREL_TURN_DOWN", -cvt(w.z, -0.23562F, 0.23562F, -48F, 48F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_GOR", this.fm.Or.getKren(), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.Or.getTangage(), -45F, 45F, 0.022F, -0.022F);
        this.mesh.chunkSetLocate("STRELKA_GOS", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("STRELKA_HOUR", -cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_MINUTE", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STRELKA_SECUND", -cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 0x80) != 0)
            this.mesh.chunkVisible("Z_OilSplats_D1", true);
        if((this.fm.AS.astateCockpitState & 4) != 0)
            this.mesh.chunkVisible("XGlassDamage4", true);
        if((this.fm.AS.astateCockpitState & 8) != 0)
            this.mesh.chunkVisible("XGlassDamage2", true);
        if((this.fm.AS.astateCockpitState & 0x10) != 0)
            this.mesh.chunkVisible("XGlassDamage3", true);
        if((this.fm.AS.astateCockpitState & 0x20) != 0)
            this.mesh.chunkVisible("XGlassDamage2", true);
        if((this.fm.AS.astateCockpitState & 0x40) != 0)
        {
            this.mesh.chunkVisible("Panel_D0", false);
            this.mesh.chunkVisible("Panel_D1", true);
            this.mesh.chunkVisible("STRELK_V_LONG", false);
            this.mesh.chunkVisible("STREL_ALT_LONG", false);
            this.mesh.chunkVisible("STREL_ALT_SHORT", false);
            this.mesh.chunkVisible("STRELKA_VY", false);
            this.mesh.chunkVisible("STRELKA_RPM", false);
            this.mesh.chunkVisible("STRELK_TEMP_RAD", false);
            this.mesh.chunkVisible("STRELK_TEMP_OIL", false);
        }
        if((this.fm.AS.astateCockpitState & 1) != 0)
            this.mesh.chunkVisible("XGlassDamage2", true);
        if((this.fm.AS.astateCockpitState & 2) != 0)
            this.mesh.chunkVisible("XGlassDamage1", true);
        retoggleLight();
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
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
    private float pictFlap;
    private float pictSupc0;
    private float pictSupc1;
    private float pictLlit;
    private float pictManf0;
    private float pictManf1;
    private boolean bNeedSetUp;
    private static final float speedometerScale[] = {
        0.0F, 15.5F, 76F, 153.5F, 234F, 304F, 372.5F, 440F, 504F, 566F, 
        630F
    };
    private static final float radScale[] = {
        0.0F, 3F, 7F, 13.5F, 30.5F, 40.5F, 51.5F, 68F, 89F, 114F, 
        145.5F, 181F, 222F, 270.5F, 331.5F
    };
    private static final float rpmScale[] = {
        0.0F, 15F, 32F, 69.5F, 106.5F, 143F, 180F, 217.5F, 253F, 290F, 
        327.5F
    };
    private static final float variometerScale[] = {
        -158F, -111F, -65.5F, -32.5F, 0.0F, 32.5F, 65.5F, 111F, 158F
    };
    private Point3d tmpP;
    private Vector3d tmpV;

}
