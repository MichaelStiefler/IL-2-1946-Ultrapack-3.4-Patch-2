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

public class CockpitMB5 extends CockpitPilot
{
    private class Variables
    {

        float throttle;
        float prop;
        float mix;
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
                setNew.throttle = 0.92F * setOld.throttle + 0.08F * fm.CT.PowerControl;
                setNew.prop = 0.92F * setOld.prop + 0.08F * fm.EI.engines[0].getControlProp();
                setNew.mix = 0.92F * setOld.mix + 0.08F * fm.EI.engines[0].getControlMix();
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
        WayPoint waypoint = fm.AP.way.curr();
        if(waypoint == null)
        {
            return 0.0F;
        } else
        {
            waypoint.getP(tmpP);
            tmpV.sub(tmpP, fm.Loc);
            return (float)(Math.toDegrees(Math.atan2(-tmpV.y, tmpV.x)));
        }
    }

    public CockpitMB5()
    {
        super("3DO/Cockpit/MB5/hier.him", "bf109");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictFlap = 0.0F;
        pictSupc = 0.0F;
        pictLlit = 0.0F;
        pictManf = 1.0F;
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
        Cockpit.xyz[2] = cvt(fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.62F);
        mesh.chunkSetLocate("Canopy", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkVisible("XLampGearUpR", fm.CT.getGear() > 0.01F && fm.CT.getGear() < 0.99F || !fm.Gears.rgear);
        mesh.chunkVisible("XLampGearUpL", fm.CT.getGear() > 0.01F && fm.CT.getGear() < 0.99F || !fm.Gears.lgear);
        mesh.chunkVisible("XLampGearUpC", fm.CT.getGear() > 0.01F && fm.CT.getGear() < 0.99F);
        mesh.chunkVisible("XLampGearDownR", fm.CT.getGear() > 0.99F && fm.Gears.rgear);
        mesh.chunkVisible("XLampGearDownL", fm.CT.getGear() > 0.99F && fm.Gears.lgear);
        mesh.chunkVisible("XLampGearDownC", fm.CT.getGear() > 0.99F);
        mesh.chunkSetAngles("Z_Columnbase", 16F * (pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Column", 45F * (pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Elev", -16F * pictElev, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = cvt(pictAiler, -1F, 1.0F, -0.027F, 0.027F);
        mesh.chunkSetLocate("Z_Shlang01", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -Cockpit.xyz[2];
        mesh.chunkSetLocate("Z_Shlang02", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_Throtle1", 65.45F * interp(setNew.throttle, setOld.throttle, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_BasePedal", 20F * fm.CT.getRudder(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_LeftPedal", 0.0F, 0.0F, -20F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_RightPedal", 0.0F, 0.0F, -20F * fm.CT.getRudder());
        mesh.chunkSetAngles("Z_Gear1", cvt(setNew.gearPhi, 0.2F, 0.8F, 0.0F, 116F), 0.0F, 0.0F);
        if(setNew.gearPhi < 0.5F)
            mesh.chunkSetAngles("Z_Gear2", cvt(setNew.gearPhi, 0.0F, 0.2F, 0.0F, -65F), 0.0F, 0.0F);
        else
            mesh.chunkSetAngles("Z_Gear2", cvt(setNew.gearPhi, 0.8F, 1.0F, -65F, 0.0F), 0.0F, 0.0F);
        float f1;
        if(Math.abs(fm.CT.FlapsControl - fm.CT.getFlap()) > 0.02F)
        {
            if(fm.CT.FlapsControl - fm.CT.getFlap() > 0.0F)
                f1 = 24F;
            else
                f1 = -24F;
        } else
        {
            f1 = 0.0F;
        }
        mesh.chunkSetAngles("Z_Flaps1", pictFlap = 0.8F * pictFlap + 0.2F * f1, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Trim1", 1000F * fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Prop1", 72.5F * setNew.prop, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Surch1", cvt(pictSupc = 0.8F * pictSupc + 0.1F * (float)fm.EI.engines[0].getControlCompressor(), 0.0F, 1.0F, 0.0F, 60F), 0.0F, 0.0F);
        f1 = 0.0F;
        if(fm.AS.bLandingLightOn)
            f1 = 66F;
        mesh.chunkSetAngles("Z_Land1", pictLlit = 0.85F * pictLlit + 0.15F * f1, 0.0F, 0.0F);
        mesh.chunkSetAngles("COMPASS_M", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELK_V_LONG", -floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, this.fm.getSpeed()), 0.0F, 257.2222F, 0.0F, 10F), speedometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("STREL_ALT_LONG", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 9144F, 0.0F, -10800F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STREL_ALT_SHORT", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 9144F, 0.0F, -1080F), 0.0F, 0.0F);
        mesh.chunkSetAngles("SHKALA_DIRECTOR", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_VY", -floatindex(cvt(setNew.vspeed, -20.32F, 20.32F, 0.0F, 8F), variometerScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_BOOST", cvt(pictManf = 0.91F * pictManf + 0.09F * fm.EI.engines[0].getManifoldPressure(), 0.7242097F, 2.103161F, 60F, -240F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_FUEL1", cvt(fm.M.fuel, 88.38F, 350.23F, 0.0F, -306F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_FUEL2", cvt((float)Math.sqrt(fm.M.fuel), 0.0F, (float)Math.sqrt(88.379997253417969D), 0.0F, -317F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_FUEL3", cvt(fm.M.fuel, 88.38F, 170.2F, 0.0F, -311F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_FUEL4", cvt(fm.M.fuel, 88.38F, 170.2F, 0.0F, -311F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_RPM", -floatindex(cvt(fm.EI.engines[0].getRPM(), 0.0F, 5000F, 0.0F, 10F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELK_TEMP_OIL", cvt(fm.EI.engines[0].tOilOut, 0.0F, 160F, 0.0F, -306F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELK_TEMP_RAD", -floatindex(cvt(fm.EI.engines[0].tWaterOut, 0.0F, 140F, 0.0F, 14F), radScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("STR_OIL_LB", cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, -36F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELK_TURN_UP", -cvt(getBall(8D), -8F, 8F, 35F, -35F), 0.0F, 0.0F);
        w.set(this.fm.getW());
        fm.Or.transform(w);
        mesh.chunkSetAngles("STREL_TURN_DOWN", -cvt(w.z, -0.23562F, 0.23562F, -48F, 48F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_GOR", fm.Or.getKren(), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(fm.Or.getTangage(), -45F, 45F, 0.022F, -0.022F);
        mesh.chunkSetLocate("STRELKA_GOS", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("STRELKA_HOUR", -cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_MINUTE", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("STRELKA_SECUND", -cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 0x80) != 0)
            mesh.chunkVisible("Z_OilSplats_D1", true);
        if((fm.AS.astateCockpitState & 4) != 0)
            mesh.chunkVisible("XGlassDamage4", true);
        if((fm.AS.astateCockpitState & 8) != 0)
            mesh.chunkVisible("XGlassDamage2", true);
        if((fm.AS.astateCockpitState & 0x10) != 0)
            mesh.chunkVisible("XGlassDamage3", true);
        if((fm.AS.astateCockpitState & 0x20) != 0)
            mesh.chunkVisible("XGlassDamage2", true);
        if((fm.AS.astateCockpitState & 0x40) != 0)
        {
            mesh.chunkVisible("Panel_D0", false);
            mesh.chunkVisible("Panel_D1", true);
            mesh.chunkVisible("STRELK_V_LONG", false);
            mesh.chunkVisible("STREL_ALT_LONG", false);
            mesh.chunkVisible("STREL_ALT_SHORT", false);
            mesh.chunkVisible("STRELKA_VY", false);
            mesh.chunkVisible("STRELKA_RPM", false);
            mesh.chunkVisible("STRELK_TEMP_RAD", false);
            mesh.chunkVisible("STRELK_TEMP_OIL", false);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
            mesh.chunkVisible("XGlassDamage2", true);
        if((fm.AS.astateCockpitState & 2) != 0)
            mesh.chunkVisible("XGlassDamage1", true);
        retoggleLight();
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        mesh.materialReplace("Gloss1D0o", mat);
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
    private float pictSupc;
    private float pictLlit;
    private float pictManf;
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
