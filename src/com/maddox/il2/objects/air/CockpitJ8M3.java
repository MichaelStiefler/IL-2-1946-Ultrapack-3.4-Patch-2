package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitJ8M3 extends CockpitPilot
{
    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if(bNeedSetUp)
            {
                reflectPlaneMats();
                bNeedSetUp = false;
            }
            if(fm != null)
            {
                setTmp = setOld;
                setOld = setNew;
                setNew = setTmp;
                if(Math.abs(fm.Or.getKren()) < 80F)
                    setNew.azimuth = (35F * setOld.azimuth + -fm.Or.getYaw()) / 36F;
                setNew.vspeed = (98F * setOld.vspeed + fm.getVertSpeed()) / 99F;
                setNew.altimeter = fm.getAltitude();
                setNew.fuel = fm.M.fuel;
                if(cockpitDimControl)
                {
                    if(setNew.dimPosition > 0.0F)
                        setNew.dimPosition = setOld.dimPosition - 0.05F;
                } else
                if(setNew.dimPosition < 1.0F)
                    setNew.dimPosition = setOld.dimPosition + 0.05F;
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
        float azimuth;
        float vspeed;
        float fuel;
        float dimPosition;

        private Variables()
        {
        }

    }

    public CockpitJ8M3()
    {
        super("3DO/Cockpit/J8M/hier.him", "bf109");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictThtl = 0.0F;
        pictFlap = 0.0F;
        pictTurbo = 0.0F;
        pictTout = 0.0F;
        pictCons = 0.0F;
        bNeedSetUp = true;
        cockpitNightMats = (new String[] {
            "2petitsb_d1", "2petitsb", "aiguill1", "comptemu_d1", "comptemu", "petitfla_d1", "petitfla", "turnbank", "oxyregul", "oxygaug", 
            "pompeco"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
        setNew.dimPosition = 1.0F;
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        mesh.chunkSetAngles("Z_ReviTint", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, 40F), 0.0F, 0.0F);
        mesh.chunkVisible("Z_GearLGreen1", fm.CT.getGear() > 0.95F && fm.Gears.lgear);
        mesh.chunkVisible("Z_GearLRed1", fm.CT.getGear() < 0.05F && fm.Gears.lgear);
        mesh.chunkVisible("Z_GearCGreen1", fm.CT.getGear() > 0.95F);
        mesh.chunkVisible("Z_GearCRed1", fm.CT.getGear() < 0.05F);
        mesh.chunkVisible("Z_GunLamp01", !aircraft().getGunByHookName("_CANNON01").haveBullets());
        mesh.chunkVisible("Z_GunLamp02", !aircraft().getGunByHookName("_CANNON02").haveBullets());
        mesh.chunkVisible("Z_FuelLampV", fm.M.fuel < 0.25F * fm.M.maxFuel);
        mesh.chunkSetAngles("zCompassOil1", cvt(fm.Or.getTangage(), -45F, 45F, -45F, 45F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zCompassOil2", 0.0F, 0.0F, cvt(fm.Or.getKren(), -45F, 45F, 45F, -45F));
        mesh.chunkSetAngles("zCompassOil3", 0.0F, -setNew.azimuth, 0.0F);
        float f1;
        if(fm.CT.getGear() - fm.CT.GearControl > 0.02F)
            f1 = 30F;
        else
        if(fm.CT.getGear() - fm.CT.GearControl < -0.02F)
            f1 = -30F;
        else
            f1 = 0.0F;
        mesh.chunkSetAngles("Z_Gear", f1, 0.0F, 0.0F);
        if(!((J8M3)aircraft()).bCartAttached)
            mesh.chunkSetAngles("Z_Gearskid", 0.0F, 90F, 0.0F);
        mesh.chunkSetAngles("Z_Column", (pictAiler = 0.85F * pictAiler + 0.15F * fm.CT.AileronControl) * 18F, 0.0F, (pictElev = 0.85F * pictElev + 0.15F * fm.CT.ElevatorControl) * 18F);
        resetYPRmodifier();
        if(fm.CT.WeaponControl[1])
            Cockpit.xyz[2] = -0.0025F;
        mesh.chunkSetLocate("Z_Columnbutton1", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_PedalStrutL", -25F * fm.CT.getRudder(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_PedalStrutR", -25F * fm.CT.getRudder(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_LeftPedal", -25F * fm.CT.getRudder(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_RightPedal", -25F * fm.CT.getRudder(), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Throttle", -30F + 60F * (pictThtl = 0.72F * pictThtl + 0.21F * fm.CT.PowerControl), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Throttle1", -30F + 60F * pictThtl, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Trimlever", -6200F * fm.CT.trimElevator, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = -0.01825F * fm.CT.trimElevator;
        mesh.chunkSetLocate("Z_Trimposit", Cockpit.xyz, Cockpit.ypr);
        mesh.chunkSetAngles("Z_Flaplever", 150F * (pictFlap = 0.6F * pictFlap + 0.4F * fm.CT.FlapsControl), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AroneleverL", 30F * pictAiler, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AroneleverR", -30F * pictAiler, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AroneL", (pictAiler >= 0.0F ? 35F : 25F) * pictAiler, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AroneR", (pictAiler >= 0.0F ? -25F : -35F) * pictAiler, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AmmoCounter1", cvt(aircraft().getGunByHookName("_CANNON01").countBullets(), 0.0F, 100F, 0.0F, -7F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_AmmoCounter2", cvt(aircraft().getGunByHookName("_CANNON02").countBullets(), 0.0F, 100F, 0.0F, -7F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Second1", cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_TurnBank1", fm.Or.getTangage(), 0.0F, fm.Or.getKren());
        mesh.chunkSetAngles("Z_TurnBank2", 0.0F, 0.0F, -cvt(getBall(6D), -6F, 6F, -7.5F, 7.5F));
        w.set(fm.getW());
        fm.Or.transform(w);
        mesh.chunkSetAngles("Z_TurnBank3", 0.0F, 0.0F, cvt(w.z, -0.23562F, 0.23562F, -50F, 50F));
        if(setNew.vspeed < 0.0F)
            f1 = cvt(setNew.vspeed, -25F, 0.0F, -45F, 0.0F);
        else
        if(setNew.vspeed < 100F)
            f1 = cvt(setNew.vspeed, 0.0F, 100F, 0.0F, 180F);
        else
        if(setNew.vspeed < 125F)
            f1 = cvt(setNew.vspeed, 100F, 125F, 180F, 223F);
        else
            f1 = cvt(setNew.vspeed, 125F, 150F, 223F, 258F);
        mesh.chunkSetAngles("Z_Climb1", f1, 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Speed1", floatindex(cvt(Pitot.Indicator((float)fm.Loc.z, fm.getSpeedKMH()), 100F, 400F, 2.0F, 8F), speedometerIndScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Speed2", floatindex(cvt(fm.getSpeedKMH(), 100F, 1000F, 1.0F, 10F), speedometerTruScale), 0.0F, 0.0F);
        if(fm.EI.engines[0].getReadyness() > 0.0F)
        {
            f1 = 6022F;
            if(fm.EI.engines[0].getPowerOutput() > 0.0F)
                f1 = 8200F;
        } else
        {
            f1 = 0.0F;
        }
        mesh.chunkSetAngles("RPM", floatindex(cvt(pictTurbo = 0.92F * pictTurbo + 0.08F * f1, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_FuelPress1", cvt(pictTurbo, 0.0F, 10000F, 0.0F, 270F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_FuelPress2", cvt(pictTout = 0.92F * pictTout + 0.08F * fm.EI.engines[0].getPowerOutput(), 0.0F, 1.1F, 0.0F, 270F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zAlt2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 16000F, 0.0F, 360F), 0.0F, 0.0F);
        mesh.chunkSetAngles("zAltCtr2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        f1 = ((setOld.fuel - setNew.fuel) / Time.tickLenFs()) * 60F;
        mesh.chunkSetAngles("Z_Fuelconsum1", cvt(pictCons = 0.75F * pictCons + 0.25F * f1, 0.0F, 20F, 0.0F, 282F), 0.0F, 0.0F);
        mesh.chunkSetAngles("Z_Temp1", cvt(fm.EI.engines[0].tOilOut, 0.0F, 1000F, 0.0F, 85F), 0.0F, 0.0F);
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        mesh.materialReplace("Gloss1D0o", mat);
    }

    public void reflectCockpitState()
    {
        if((fm.AS.astateCockpitState & 2) != 0)
        {
            mesh.chunkVisible("XGlassDamage4", true);
            mesh.chunkVisible("XGlassDamage5", true);
        }
        if((fm.AS.astateCockpitState & 1) != 0)
        {
            mesh.chunkVisible("XGlassDamage1", true);
            mesh.chunkVisible("XGlassDamage2", true);
            mesh.chunkVisible("XGlassDamage3", true);
        }
        if((fm.AS.astateCockpitState & 0x40) == 0);
        if((fm.AS.astateCockpitState & 4) != 0)
            mesh.chunkVisible("HullDamage1", true);
        if((fm.AS.astateCockpitState & 8) != 0)
            mesh.chunkVisible("HullDamage2", true);
        if((fm.AS.astateCockpitState & 0x80) == 0);
        if((fm.AS.astateCockpitState & 0x10) != 0)
            mesh.chunkVisible("HullDamage3", true);
        if((fm.AS.astateCockpitState & 0x20) != 0)
            mesh.chunkVisible("HullDamage4", true);
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
    private float pictThtl;
    private float pictFlap;
    private float pictTurbo;
    private float pictTout;
    private float pictCons;
    private boolean bNeedSetUp;
    private static final float speedometerIndScale[] = {
        0.0F, 0.0F, 0.0F, 17F, 35.5F, 57.5F, 76F, 95F, 112F
    };
    private static final float speedometerTruScale[] = {
        0.0F, 32.75F, 65.5F, 98.25F, 131F, 164F, 200F, 237F, 270.5F, 304F, 
        336F
    };
    private static final float rpmScale[] = {
        0.0F, 0.0F, 0.0F, 16.5F, 34.5F, 55F, 77.5F, 104F, 133.5F, 162.5F, 
        192F, 224F, 254F, 255.5F, 260F
    };

}
