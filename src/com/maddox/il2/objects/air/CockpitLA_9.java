package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.GunEmpty;
import com.maddox.rts.Time;

public class CockpitLA_9 extends CockpitPilot
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
                setNew.throttle = (10F * setOld.throttle + fm.CT.PowerControl) / 11F;
                setNew.prop = (8F * setOld.prop + fm.CT.getStepControl()) / 9F;
                setNew.altimeter = fm.getAltitude();
                if(Math.abs(fm.Or.getKren()) < 30F)
                    setNew.azimuth = (35F * setOld.azimuth + -fm.Or.getYaw()) / 36F;
                setNew.trueAzimuth = (35F * setOld.trueAzimuth + -fm.Or.getYaw()) / 36F;
                if(setOld.azimuth > 270F && setNew.azimuth < 90F)
                    setOld.azimuth -= 360F;
                if(setOld.azimuth < 90F && setNew.azimuth > 270F)
                    setOld.azimuth += 360F;
                if(setOld.trueAzimuth > 270F && setNew.trueAzimuth < 90F)
                    setOld.trueAzimuth -= 360F;
                if(setOld.trueAzimuth < 90F && setNew.trueAzimuth > 270F)
                    setOld.trueAzimuth += 360F;
                if(useRealisticNavigationInstruments())
                    setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(1.0F), getBeaconDirection());
                else
                    setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), waypointAzimuth() - setOld.trueAzimuth);
                setNew.vspeed = (199F * setOld.vspeed + fm.getVertSpeed()) / 200F;
                if(fm.getAltitude() > 3000F)
                {
                    float f = (float)Math.sin(1.0F * cvt(fm.getOverload(), 1.0F, 8F, 1.0F, 0.45F) * cvt(fm.AS.astatePilotStates[0], 0.0F, 100F, 1.0F, 0.1F) * (0.001F * (float)Time.current()));
                    if(f > 0.0F)
                    {
                        pictBlinker = 0.3F;
                        if(pictBlinker > 1.0F)
                            pictBlinker = 1.0F;
                    } else
                    {
                        pictBlinker = 0.3F;
                        if(pictBlinker < 0.0F)
                            pictBlinker = 0.0F;
                    }
                }
                pictStage = 0.8F * pictStage + 0.1F * (float)fm.EI.engines[0].getControlCompressor();
            }
            return true;
        }

        Interpolater()
        {
        }
    }

    private class Variables
    {

        float throttle;
        float prop;
        float altimeter;
        float trueAzimuth;
        float azimuth;
        float vspeed;
        AnglesFork waypointAzimuth;
        float xyz[] = {
            0.0F, 0.0F, 0.0F
        };
        float ypr[] = {
            0.0F, 0.0F, 0.0F
        };

        private Variables()
        {
            waypointAzimuth = new AnglesFork();
        }
    }

    protected float waypointAzimuth()
    {
        return this.waypointAzimuthInvertMinus(5F);
    }

    public CockpitLA_9()
    {
        super("3DO/Cockpit/La-9/hier.him", "bf109");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        bNeedSetUp = true;
        t1 = 0L;
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictBlinker = 0.0F;
        pictStage = 0.0F;
        this.cockpitNightMats = (new String[] {
            "Prib_One", "Prib_Two", "Prib_Three", "Prib_Four", "Prib_Five", "Shkala128"
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
        Cockpit.xyz[0] = -cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.58F);
        this.mesh.chunkSetLocate("Blister", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.58F);
        this.mesh.chunkSetLocate("Z_Holes1_D1", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetLocate("Z_Holes2_D1", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        if(this.fm.CT.getStepControlAuto())
            this.mesh.chunkSetAngles("PropPitchHandle", -70F + 70F * interp(setNew.throttle, setOld.throttle, f), 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("PropPitchHandle", -70F + 70F * interp(setNew.prop, setOld.prop, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("TQHandle", -54.54546F * interp(setNew.throttle, setOld.throttle, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("MixtureHandle", 50F * this.fm.EI.engines[0].getControlMix(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ChargerHandle", this.fm.EI.engines[0].getStage() >= 4 ? 70F - 60F * pictStage : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("CStick", 0.0F, (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl) * 15F, (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl) * 10F);
        this.mesh.chunkSetAngles("Ped_Base", this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("PedalL", -this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("PedalR", -this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Tross_L", 0.0F, this.fm.CT.getRudder() * 15.65F, 0.0F);
        this.mesh.chunkSetAngles("Tross_R", 0.0F, this.fm.CT.getRudder() * 15.65F, 0.0F);
        this.mesh.chunkSetAngles("IgnitionSwitch", 0.0F, -40F * (float)this.fm.EI.engines[0].getControlMagnetos(), 0.0F);
        this.mesh.chunkSetAngles("SW_LandLight", this.fm.AS.bLandingLightOn ? 60F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("SW_UVLight", this.cockpitLightControl ? 60F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("SW_Radio", 60F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("SW_NavLight", this.fm.AS.bNavLightsOn ? 60F : 0.0F, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[2] = 0.01F * pictBlinker;
        this.mesh.chunkSetLocate("zBlinkerUp", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetLocate("zBlinkerDn", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        if(this.fm.CT.GearControl == 0.0F && this.fm.CT.getGear() != 0.0F)
            this.mesh.chunkSetAngles("GearHandle", -45F, 0.0F, 0.0F);
        else
        if(this.fm.CT.GearControl == 1.0F && this.fm.CT.getGear() != 1.0F)
            this.mesh.chunkSetAngles("GearHandle", 45F, 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("GearHandle", 0.0F, 0.0F, 0.0F);
        if(Math.abs(this.fm.CT.FlapsControl - this.fm.CT.getFlap()) > 0.02F)
        {
            if(this.fm.CT.FlapsControl - this.fm.CT.getFlap() > 0.0F)
                this.mesh.chunkSetAngles("FlapHandle", 30F, 0.0F, 0.0F);
            else
                this.mesh.chunkSetAngles("FlapHandle", -30F, 0.0F, 0.0F);
        } else
        {
            this.mesh.chunkSetAngles("FlapHandle", 0.0F, 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("zAlt1a", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt1b", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 10000F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAzimuth1a", 0.0F, 0.0F, cvt(this.fm.Or.getTangage(), -40F, 40F, 40F, -40F));
        this.mesh.chunkSetAngles("zAzimuth1b", interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
        setOld.xyz[1] = cvt(this.fm.Or.getTangage(), -40F, 40F, 0.03F, -0.03F);
        this.mesh.chunkSetLocate("zHorizon1a", setOld.xyz, setOld.ypr);
        this.mesh.chunkSetAngles("zHorizon1b", this.fm.Or.getKren(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zManifold1a", floatindex(cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.399966F, 2.133152F, 3F, 16F), manifoldScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGas1a", cvt(this.fm.M.fuel / 0.725F, 0.0F, 300F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSpeed1a", floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 800F, 0.0F, 16F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zCylHead", cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 300F, 0.0F, 70F), 0.0F, 0.0F);
        w.set(this.fm.getW());
        this.fm.Or.transform(w);
        this.mesh.chunkSetAngles("zTurn1a", cvt(w.z, -0.23562F, 0.23562F, 25F, -25F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSlide1a", -cvt(getBall(8D), -8F, 8F, 25F, -25F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zTOilOut1a", cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 125F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPrs1a", cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut, 0.0F, 15F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasPrs1a", cvt(this.fm.M.fuel <= 1.0F ? 0.0F : cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 3050F, 0.0F, 4F), 0.0F, 8F, 0.0F, -180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zVariometer1a", floatindex(cvt(setNew.vspeed, -30F, 30F, 0.0F, 6F), variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM1a", cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM1b", cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 10000F, 0.0F, 360F), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 0x40) == 0)
        {
            this.mesh.chunkSetAngles("zClock1a", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("zClock1b", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("zRPK10", cvt(setNew.waypointAzimuth.getDeg(f * 0.2F), -25F, 25F, -35F, 35F), 0.0F, 0.0F);
        }
        this.mesh.chunkVisible("XGearUP_L", this.fm.CT.getGear() == 0.0F && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XGearUP_R", this.fm.CT.getGear() == 0.0F && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XGearDown_L", this.fm.CT.getGear() == 1.0F && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XGearDown_R", this.fm.CT.getGear() == 1.0F && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XGearDown_C", this.fm.CT.getGear() == 1.0F);
        if(t1 < Time.current())
        {
            BulletEmitter bulletemitter = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb01");
            if(bulletemitter != GunEmpty.get())
                this.mesh.chunkVisible("XBombOnboard_L", bulletemitter.haveBullets());
            else
                this.mesh.chunkVisible("XBombOnboard_L", false);
            bulletemitter = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb02");
            if(bulletemitter != GunEmpty.get())
                this.mesh.chunkVisible("XBombOnboard_R", bulletemitter.haveBullets());
            else
                this.mesh.chunkVisible("XBombOnboard_R", false);
            t1 = Time.current() + 500L;
        }
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 4) != 0 || (this.fm.AS.astateCockpitState & 0x10) != 0)
        {
            this.mesh.materialReplace("Prib_One", "DPrib_One");
            this.mesh.materialReplace("Prib_One_night", "DPrib_One_night");
            this.mesh.chunkVisible("zAlt1b", false);
            this.mesh.chunkVisible("zAlt1a", false);
            this.mesh.chunkVisible("zSpeed1a", false);
            this.mesh.chunkVisible("zRPM1a", false);
            this.mesh.chunkVisible("zRPM1b", false);
            this.mesh.chunkVisible("zTOilOut1a", false);
            this.mesh.chunkVisible("zOilPrs1a", false);
            this.mesh.chunkVisible("zGasPrs1a", false);
        }
        if((this.fm.AS.astateCockpitState & 8) != 0 || (this.fm.AS.astateCockpitState & 0x20) != 0)
        {
            this.mesh.materialReplace("Prib_Two", "DPrib_Two");
            this.mesh.materialReplace("Prib_Two_night", "DPrib_Two_night");
            this.mesh.chunkVisible("zManifold1a", false);
            this.mesh.chunkVisible("zVariometer1a", false);
            this.mesh.chunkVisible("zGas1a", false);
            this.mesh.chunkVisible("zTurn1a", false);
            this.mesh.chunkVisible("zSlide1a", false);
        }
        if((this.fm.AS.astateCockpitState & 0x40) != 0)
        {
            this.mesh.materialReplace("Prib_Three", "DPrib_Three");
            this.mesh.materialReplace("Prib_Three_night", "DPrib_Three_night");
            this.mesh.chunkVisible("zHorizon1a", false);
            this.mesh.chunkVisible("zHorizon1b", false);
            this.mesh.materialReplace("Prib_Four", "DPrib_Four");
            this.mesh.materialReplace("Prib_Four_night", "DPrib_Four_night");
            this.mesh.chunkVisible("zAzimuth1a", false);
            this.mesh.chunkVisible("zAzimuth1b", false);
        }
        if((this.fm.AS.astateCockpitState & 2) != 0 || (this.fm.AS.astateCockpitState & 1) != 0)
        {
            this.mesh.chunkVisible("PBP-1b", false);
            this.mesh.chunkVisible("PBP-1b_D0", true);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("Z_Holes1_D0", true);
            this.mesh.chunkVisible("Z_Holes1_D1", true);
            this.mesh.chunkVisible("Z_Holes2_D0", true);
            this.mesh.chunkVisible("Z_Holes2_D1", true);
        }
        if((this.fm.AS.astateCockpitState & 0x80) != 0)
            this.mesh.chunkVisible("Z_OilSplats_D0", true);
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
    private boolean bNeedSetUp;
    private long t1;
    private float pictAiler;
    private float pictElev;
    private float pictBlinker;
    private float pictStage;
    private static final float speedometerScale[] = {
        0.0F, 0.0F, 15.5F, 50F, 95.5F, 137F, 182.5F, 212F, 230F, 242F, 
        254.5F, 267.5F, 279F, 292F, 304F, 317F, 329.5F, 330F
    };
    private static final float manifoldScale[] = {
        0.0F, 0.0F, 0.0F, 0.0F, 26F, 52F, 79F, 106F, 132F, 160F, 
        185F, 208F, 235F, 260F, 286F, 311F, 336F
    };
    private static final float variometerScale[] = {
        -180F, -90F, -45F, 0.0F, 45F, 90F, 180F
    };
}
