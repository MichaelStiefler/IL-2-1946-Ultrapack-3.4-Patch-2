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
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.BaseGameVersion;

public class CockpitME_262HG_V extends CockpitPilot
{
    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            setTmp = setOld;
            setOld = setNew;
            setNew = setTmp;
            setNew.altimeter = fm.getAltitude();
            setNew.throttlel = (10F * setOld.throttlel + fm.EI.engines[0].getControlThrottle()) / 11F;
            setNew.throttler = (10F * setOld.throttler + fm.EI.engines[1].getControlThrottle()) / 11F;
            float f = waypointAzimuth();
            if(BaseGameVersion.is410orLater() && useRealisticNavigationInstruments())
            {
                setNew.waypointAzimuth.setDeg(f - 90F);
                setOld.waypointAzimuth.setDeg(f - 90F);
            } else
            {
                setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), f - setOld.azimuth.getDeg(1.0F));
            }
            setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
            setNew.vspeed = (299F * setOld.vspeed + fm.getVertSpeed()) / 300F;
            if(cockpitDimControl)
            {
                if(setNew.dimPosition > 0.0F)
                    setNew.dimPosition = setOld.dimPosition - 0.05F;
            } else
            if(setNew.dimPosition < 1.0F)
                setNew.dimPosition = setOld.dimPosition + 0.05F;
            if(BaseGameVersion.is410orLater())
            {
                setNew.beaconDirection = (10F * setOld.beaconDirection + getBeaconDirection()) / 11F;
                setNew.beaconRange = (10F * setOld.beaconRange + getBeaconRange()) / 11F;
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
        float throttlel;
        float throttler;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float vspeed;
        float dimPosition;
        float beaconDirection;
        float beaconRange;

        private Variables()
        {
            azimuth = new AnglesFork();
            waypointAzimuth = new AnglesFork();
        }
    }

    protected float waypointAzimuth()
    {
        if(BaseGameVersion.is410orLater())
            return waypointAzimuthInvert(30F);
        WayPoint waypoint = this.fm.AP.way.curr();
        if(waypoint == null)
            return 0.0F;
        waypoint.getP(tmpP);
        tmpV.sub(tmpP, this.fm.Loc);
        float fWP = (float)(Math.toDegrees(Math.atan2(tmpV.x, tmpV.y)));
        for(fWP += World.Rnd().nextFloat(-20F, 20F); fWP < -180F; fWP += 180F);
        while (fWP > 180F) fWP -= 180F;
        return fWP;
    }

    protected void setCameraOffset()
    {
        this.cameraCenter.add(0.1D, 0.0D, -0.05D);
    }

    public CockpitME_262HG_V()
    {
        super("3DO/Cockpit/Me-262HG-V/hier.him", "he111");
        gun = new Gun[4];
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        tmpP = new Point3d();
        tmpV = new Vector3d();
        bNeedSetUp = true;
        pictAiler = 0.0F;
        pictElev = 0.0F;
        bU4 = false;
        this.cockpitNightMats = (new String[] {
            "2petitsb_d1", "2petitsb", "aiguill1", "badinetm_d1", "badinetm", "baguecom", "brasdele", "comptemu_d1", "comptemu", "petitfla_d1", 
            "petitfla", "turnbank"
        });
        setNightMats(false);
        setNew.dimPosition = 1.0F;
        interpPut(new Interpolater(), null, Time.current(), null);
        if(BaseGameVersion.is411orLater())
        {
            this.limits6DoF = (new float[] {
                0.7F, 0.055F, -0.07F, 0.13F, 0.15F, -0.11F, 0.01F, -0.05F
            });
            this.printCompassHeading = true;
        } else
        if(BaseGameVersion.is410orLater())
            try
            {
                AircraftLH.class.getField("printCompassHeading").setBoolean(null, true);
            }
            catch(Exception exception) { }
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        if(gun[0] == null)
        {
            gun[0] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON03");
            gun[1] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON01");
            gun[2] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON02");
            gun[3] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON04");
        }
        if(this.fm.isTick(44, 0))
        {
            if(BaseGameVersion.is412orLater())
            {
                this.mesh.chunkVisible("Z_GearLGreen1", this.fm.CT.getGearL() == 1.0F && this.fm.Gears.lgear);
                this.mesh.chunkVisible("Z_GearRGreen1", this.fm.CT.getGearR() == 1.0F && this.fm.Gears.rgear);
                this.mesh.chunkVisible("Z_GearCGreen1", this.fm.CT.getGearC() == 1.0F);
                this.mesh.chunkVisible("Z_GearLRed1", this.fm.CT.getGearL() == 0.0F || this.fm.Gears.isAnyDamaged());
                this.mesh.chunkVisible("Z_GearRRed1", this.fm.CT.getGearR() == 0.0F || this.fm.Gears.isAnyDamaged());
                this.mesh.chunkVisible("Z_GearCRed1", this.fm.CT.getGearC() == 0.0F);
            } else
            {
                this.mesh.chunkVisible("Z_GearLGreen1", this.fm.CT.getGear() == 1.0F && this.fm.Gears.lgear);
                this.mesh.chunkVisible("Z_GearRGreen1", this.fm.CT.getGear() == 1.0F && this.fm.Gears.rgear);
                this.mesh.chunkVisible("Z_GearCGreen1", this.fm.CT.getGear() == 1.0F);
                this.mesh.chunkVisible("Z_GearLRed1", this.fm.CT.getGear() == 0.0F || this.fm.Gears.isAnyDamaged());
                this.mesh.chunkVisible("Z_GearRRed1", this.fm.CT.getGear() == 0.0F || this.fm.Gears.isAnyDamaged());
                this.mesh.chunkVisible("Z_GearCRed1", this.fm.CT.getGear() == 0.0F);
            }
            if(!bU4)
            {
                this.mesh.chunkVisible("Z_GunLamp01", !gun[0].haveBullets());
                this.mesh.chunkVisible("Z_GunLamp02", !gun[1].haveBullets());
                this.mesh.chunkVisible("Z_GunLamp03", !gun[2].haveBullets());
                this.mesh.chunkVisible("Z_GunLamp04", !gun[3].haveBullets());
            }
            this.mesh.chunkVisible("Z_MachLamp", this.fm.getSpeed() / Atmosphere.sonicSpeed((float)this.fm.Loc.z) > 0.8F);
            this.mesh.chunkVisible("Z_CabinLamp", this.fm.Loc.z > 12000D);
            this.mesh.chunkVisible("Z_FuelLampV", this.fm.M.fuel < 300F);
            this.mesh.chunkVisible("Z_FuelLampIn", this.fm.M.fuel < 300F);
        }
        this.mesh.chunkSetAngles("Z_ReviTint", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -45F), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[1] = this.fm.CT.GearControl == 0.0F && this.fm.CT.getGear() != 0.0F ? -0.0107F : 0.0F;
        this.mesh.chunkSetLocate("Z_GearEin", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = this.fm.CT.GearControl == 1.0F && this.fm.CT.getGear() != 1.0F ? -0.0107F : 0.0F;
        this.mesh.chunkSetLocate("Z_GearAus", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[1] = this.fm.CT.FlapsControl < this.fm.CT.getFlap() ? -0.0107F : 0.0F;
        this.mesh.chunkSetLocate("Z_FlapEin", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = this.fm.CT.FlapsControl > this.fm.CT.getFlap() ? -0.0107F : 0.0F;
        this.mesh.chunkSetLocate("Z_FlapAus", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Column", 10F * (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl), 0.0F, 10F * (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl));
        resetYPRmodifier();
        if(this.fm.CT.saveWeaponControl[0])
            Cockpit.xyz[2] = -0.0025F;
        this.mesh.chunkSetLocate("Z_Columnbutton1", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        if(this.fm.CT.saveWeaponControl[2] || this.fm.CT.saveWeaponControl[3])
            Cockpit.xyz[2] = -0.00325F;
        this.mesh.chunkSetLocate("Z_Columnbutton2", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_PedalStrut", 20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", -20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal", -20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_ThrottleL", 0.0F, -75F * interp(setNew.throttlel, setOld.throttlel, f), 0.0F);
        this.mesh.chunkSetAngles("Z_ThrottleR", 0.0F, -75F * interp(setNew.throttler, setOld.throttler, f), 0.0F);
        this.mesh.chunkSetAngles("Z_FuelLeverL", this.fm.EI.engines[0].getControlMagnetos() == 3 ? 6.5F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelLeverR", this.fm.EI.engines[1].getControlMagnetos() == 3 ? 6.5F : 0.0F, 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[1] = 0.03675F * this.fm.CT.getTrimElevatorControl();
        this.mesh.chunkSetLocate("Z_TailTrim", Cockpit.xyz, Cockpit.ypr);
        if(this.fm.CT.Weapons[3] != null && !this.fm.CT.Weapons[3][0].haveBullets())
            this.mesh.chunkSetAngles("Z_Bombbutton", 0.0F, 53F, 0.0F);
        this.mesh.chunkSetAngles("Z_AmmoCounter1", cvt(gun[1].countBullets(), 0.0F, 100F, 0.0F, -7F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AmmoCounter2", cvt(gun[2].countBullets(), 0.0F, 100F, 0.0F, -7F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer1", floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 100F, 400F, 2.0F, 8F), speedometerIndScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer2", floatindex(cvt(this.fm.getSpeedKMH(), 100F, 1000F, 1.0F, 10F), speedometerTruScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter1", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 16000F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter2", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Second1", cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.fm.Or.getTangage(), 0.0F, this.fm.Or.getKren());
        this.mesh.chunkSetAngles("Z_TurnBank2", 0.0F, 0.0F, -cvt(getBall(6D), -6F, 6F, -7.5F, 7.5F));
        w.set(this.fm.getW());
        this.fm.Or.transform(w);
        this.mesh.chunkSetAngles("Z_TurnBank3", 0.0F, 0.0F, cvt(w.z, -0.23562F, 0.23562F, -50F, 50F));
        this.mesh.chunkSetAngles("Z_Climb1", floatindex(cvt(setNew.vspeed, -20F, 50F, 0.0F, 14F), variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPML", floatindex(cvt(this.fm.EI.engines[0].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPMR", floatindex(cvt(this.fm.EI.engines[1].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        if(BaseGameVersion.is410orLater() && useRealisticNavigationInstruments())
        {
            this.mesh.chunkSetAngles("Z_Compass2", setNew.azimuth.getDeg(f) - setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass1", -setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
        } else
        {
            this.mesh.chunkSetAngles("Z_Compass1", -setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass2", setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("Z_GasPressureL", cvt(this.fm.M.fuel > 1.0F ? 0.6F * this.fm.EI.engines[0].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, 273.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasPressureR", cvt(this.fm.M.fuel > 1.0F ? 0.6F * this.fm.EI.engines[1].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, 273.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasTempL", cvt(this.fm.EI.engines[0].tWaterOut, 300F, 1000F, -2F, 92.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasTempR", cvt(this.fm.EI.engines[1].tWaterOut, 300F, 1000F, -2F, 92.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPressureL", cvt(1.0F + 0.005F * this.fm.EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPressureR", cvt(1.0F + 0.005F * this.fm.EI.engines[1].tOilOut, 0.0F, 10F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelPressL", cvt(this.fm.M.fuel > 1.0F ? 80F * this.fm.EI.engines[0].getPowerOutput() * this.fm.EI.engines[0].getReadyness() : 0.0F, 0.0F, 160F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelPressR", cvt(this.fm.M.fuel > 1.0F ? 80F * this.fm.EI.engines[1].getPowerOutput() * this.fm.EI.engines[1].getReadyness() : 0.0F, 0.0F, 160F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelRemainV", floatindex(cvt(this.fm.M.fuel / 0.72F, 0.0F, 1000F, 0.0F, 5F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelRemainIn", floatindex(cvt(this.fm.M.fuel / 0.72F, 0.0F, 1000F, 0.0F, 5F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AFN1", cvt(setNew.beaconDirection, -45F, 45F, -20F, 20F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AFN2", cvt(setNew.beaconRange, 0.0F, 1.0F, 20F, -20F), 0.0F, 0.0F);
        if(BaseGameVersion.is410orLater())
            this.mesh.chunkVisible("AFN2_RED", isOnBlindLandingMarker());
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 4) != 0)
        {
            this.mesh.chunkVisible("HullDamage2", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("Speedometer1", false);
            this.mesh.chunkVisible("Speedometer1_D1", true);
            this.mesh.chunkVisible("Z_Speedometer1", false);
            this.mesh.chunkVisible("Z_Speedometer2", false);
            this.mesh.chunkVisible("RPML", false);
            this.mesh.chunkVisible("RPML_D1", true);
            this.mesh.chunkVisible("Z_RPML", false);
            this.mesh.chunkVisible("FuelRemainV", false);
            this.mesh.chunkVisible("FuelRemainV_D1", true);
            this.mesh.chunkVisible("Z_FuelRemainV", false);
        }
        if((this.fm.AS.astateCockpitState & 8) != 0)
        {
            this.mesh.chunkVisible("HullDamage4", true);
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.chunkVisible("Altimeter1", false);
            this.mesh.chunkVisible("Altimeter1_D1", true);
            this.mesh.chunkVisible("Z_Altimeter1", false);
            this.mesh.chunkVisible("Z_Altimeter2", false);
            this.mesh.chunkVisible("GasPressureL", false);
            this.mesh.chunkVisible("GasPressureL_D1", true);
            this.mesh.chunkVisible("Z_GasPressureL", false);
        }
        if((this.fm.AS.astateCockpitState & 0x10) != 0)
        {
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("RPMR", false);
            this.mesh.chunkVisible("RPMR_D1", true);
            this.mesh.chunkVisible("Z_RPMR", false);
            this.mesh.chunkVisible("FuelPressR", false);
            this.mesh.chunkVisible("FuelPressR_D1", true);
            this.mesh.chunkVisible("Z_FuelPressR", false);
        }
        if((this.fm.AS.astateCockpitState & 0x20) != 0)
        {
            this.mesh.chunkVisible("HullDamage3", true);
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.chunkVisible("GasPressureR", false);
            this.mesh.chunkVisible("GasPressureR_D1", true);
            this.mesh.chunkVisible("Z_GasPressureR", false);
        }
        if((this.fm.AS.astateCockpitState & 1) != 0)
        {
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("XGlassDamage2", true);
            this.mesh.chunkVisible("Climb", false);
            this.mesh.chunkVisible("Climb_D1", true);
            this.mesh.chunkVisible("Z_Climb1", false);
            this.mesh.chunkVisible("FuelPressR", false);
            this.mesh.chunkVisible("FuelPressR_D1", true);
            this.mesh.chunkVisible("Z_FuelPressR", false);
        }
        if((this.fm.AS.astateCockpitState & 2) != 0)
        {
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.chunkVisible("HullDamage2", true);
            this.mesh.chunkVisible("Revi_D0", false);
            this.mesh.chunkVisible("Revi_D1", true);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("FuelPressL", false);
            this.mesh.chunkVisible("FuelPressL_D1", true);
            this.mesh.chunkVisible("Z_FuelPressL", false);
        }
        if((this.fm.AS.astateCockpitState & 0x40) != 0)
        {
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.chunkVisible("Altimeter1", false);
            this.mesh.chunkVisible("Altimeter1_D1", true);
            this.mesh.chunkVisible("Z_Altimeter1", false);
            this.mesh.chunkVisible("Z_Altimeter2", false);
            this.mesh.chunkVisible("Climb", false);
            this.mesh.chunkVisible("Climb_D1", true);
            this.mesh.chunkVisible("Z_Climb1", false);
            this.mesh.chunkVisible("AFN", false);
            this.mesh.chunkVisible("AFN_D1", true);
            this.mesh.chunkVisible("Z_AFN1", false);
            this.mesh.chunkVisible("Z_AFN2", false);
            this.mesh.chunkVisible("FuelPressL", false);
            this.mesh.chunkVisible("FuelPressL_D1", true);
            this.mesh.chunkVisible("Z_FuelPressL", false);
            this.mesh.chunkVisible("FuelRemainIn", false);
            this.mesh.chunkVisible("FuelRemainIn_D1", true);
            this.mesh.chunkVisible("Z_FuelRemainIn", false);
        }
        retoggleLight();
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
        if(aircraft() instanceof ME_262A1AU4)
        {
            this.mesh.chunkVisible("Z_Ammo262U4", true);
            bU4 = true;
        }
    }

    public void toggleDim()
    {
        this.cockpitDimControl = !this.cockpitDimControl;
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

    protected boolean doFocusEnter()
    {
        if(!super.doFocusEnter())
        {
            return false;
        } else
        {
            aircraft().hierMesh().chunkVisible("CF_D0", false);
            aircraft().hierMesh().chunkVisible("Pilot1_D0", false);
            aircraft().hierMesh().chunkVisible("Pilot1_D1", false);
            aircraft().hierMesh().chunkVisible("Head1_D0", false);
            aircraft().hierMesh().chunkVisible("Blister1_D0", false);
            aircraft().hierMesh().chunkVisible("Blister2_D0", false);
            return true;
        }
    }

    protected void doFocusLeave()
    {
        aircraft().hierMesh().chunkVisible("CF_D0", true);
        if(!this.fm.AS.isPilotParatrooper(0))
        {
            aircraft().hierMesh().chunkVisible("Pilot1_D0", !this.fm.AS.isPilotDead(0));
            aircraft().hierMesh().chunkVisible("Head1_D0", !this.fm.AS.isPilotDead(0));
            aircraft().hierMesh().chunkVisible("Pilot1_D1", this.fm.AS.isPilotDead(0));
        }
//        aircraft().hierMesh().chunkVisible("Blister1_D0", !((ME_262HG_V)aircraft()).blisterRemoved[0]);
//        aircraft().hierMesh().chunkVisible("Blister2_D0", !((ME_262HG_V)aircraft()).blisterRemoved[1]);
        aircraft().hierMesh().chunkVisible("Blister1_D0", ((ME_262HG_V)aircraft()).bHasBlister);
        aircraft().hierMesh().chunkVisible("Blister2_D0", true);
        super.doFocusLeave();
    }

    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    public Vector3f w;
    private Point3d tmpP;
    private Vector3d tmpV;
    private Gun gun[];
    private boolean bNeedSetUp;
    private float pictAiler;
    private float pictElev;
    private boolean bU4;
    private static final float speedometerIndScale[] = {
        0.0F, 0.0F, 0.0F, 17F, 35.5F, 57.5F, 76F, 95F, 112F
    };
    private static final float speedometerTruScale[] = {
        0.0F, 32.75F, 65.5F, 98.25F, 131F, 164F, 200F, 237F, 270.5F, 304F, 
        336F
    };
    private static final float variometerScale[] = {
        0.0F, 13.5F, 27F, 43.5F, 90F, 142.5F, 157F, 170.5F, 184F, 201.5F, 
        214.5F, 226F, 239.5F, 253F, 266F
    };
    private static final float rpmScale[] = {
        0.0F, 0.0F, 0.0F, 16.5F, 34.5F, 55F, 77.5F, 104F, 133.5F, 162.5F, 
        192F, 224F, 254F, 255.5F, 260F
    };
    private static final float fuelScale[] = {
        0.0F, 11F, 31F, 57F, 84F, 103.5F
    };

}
