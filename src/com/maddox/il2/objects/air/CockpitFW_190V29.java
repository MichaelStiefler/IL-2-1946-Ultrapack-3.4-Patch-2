package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitFW_190V29 extends CockpitFW_190V
{
    public CockpitFW_190V29()
    {
        super("3DO/Cockpit/Fw-190V29/hier.him", "bf109");
        gun = new Gun[4];
        this.cockpitNightMats = (new String[] {
            "D9GP1", "D9GP2", "Ta152GP3", "A5GP3Km", "Ta152GP4_MW50", "Ta152GP5", "A4GP6", "Ta152Trans2", "D9GP2", "Trans4", 
            "Ta152EQpt5"
        });
        setNightMats(false);
        this.cockpitDimControl = !this.cockpitDimControl;
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(gun[0] == null)
        {
            gun[0] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON01");
            gun[1] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON03");
            gun[2] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON04");
        }
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.48F);
        Cockpit.xyz[2] = cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 0.01F);
        Cockpit.ypr[2] = cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, -2F);
        this.mesh.chunkSetLocate("CanopyTop", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("EjectatorHandle", 0.0F, 0.0F, 2520F * this.fm.CT.getCockpitDoor());
        this.mesh.chunkSetAngles("CanopyFrameL", -cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 2.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("CanopyFrameR", cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 2.0F), 0.0F, 0.0F);
        resetYPRmodifier();
        if(this.fm.CT.GearControl == 0.0F)
            Cockpit.xyz[2] = 0.02F;
        this.mesh.chunkSetLocate("FahrHandle", Cockpit.xyz, Cockpit.ypr);
        if((this.fm.AS.astateCockpitState & 0x20) == 0 && (this.fm.AS.astateCockpitState & 0x80) == 0)
        {
            this.mesh.chunkSetAngles("NeedleALT", -cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 13000F, 0.0F, 4680F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleALT1", -cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 13000F, 0.0F, 468F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleALTKm", 0.0F, 0.0F, cvt(Atmosphere.pressure((float)this.fm.Loc.z), 0.0F, 100000F, 0.0F, 180F));
        }
        this.mesh.chunkSetAngles("NeedleManPress", -cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 336F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleKMH", -floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), speedometerScale), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
            this.mesh.chunkSetAngles("NeedleRPM", -floatindex(cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleFuel", floatindex(cvt(this.fm.M.fuel / 0.92F, 0.0F, 400F, 0.0F, 4F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleWaterTemp", cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 120F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOilTemp", cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAirPress", cvt(this.fm.EI.engines[0].getRPM() / 1500F, 0.0F, 15F, 0.0F, 200F), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 2) == 0 && (this.fm.AS.astateCockpitState & 1) == 0 && (this.fm.AS.astateCockpitState & 4) == 0)
        {
            this.mesh.chunkSetAngles("NeedleFuelPress", cvt(this.fm.EI.engines[0].getRPM() / 1500F, 0.0F, 3F, 0.0F, 135F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleOilPress", -cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut + this.fm.EI.engines[0].getRPM() / 1500F, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
        }
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
        {
            float f1;
            if(aircraft().isFMTrackMirror())
            {
                f1 = aircraft().fmTrack().getCockpitAzimuthSpeed();
            } else
            {
                f1 = cvt((setNew.azimuth - setOld.azimuth) / Time.tickLenFs(), -6F, 6F, 20F, -20F);
                if(aircraft().fmTrack() != null)
                    aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
            }
            this.mesh.chunkSetAngles("NeedleAHTurn", 0.0F, f1, 0.0F);
            this.mesh.chunkSetAngles("NeedleAHBank", 0.0F, -cvt(getBall(6D), -6F, 6F, 11F, -11F), 0.0F);
            this.mesh.chunkSetAngles("NeedleAHCyl", 0.0F, 0.0F, this.fm.Or.getKren());
            this.mesh.chunkSetAngles("NeedleAHBar", 0.0F, 0.0F, cvt(this.fm.Or.getTangage(), -45F, 45F, 12F, -12F));
        }
        if((this.fm.AS.astateCockpitState & 0x20) == 0 && (this.fm.AS.astateCockpitState & 0x80) == 0)
            this.mesh.chunkSetAngles("NeedleCD", setNew.vspeed >= 0.0F ? -floatindex(cvt(setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale) : floatindex(cvt(-setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
        {
            this.mesh.chunkSetAngles("RepeaterOuter", -interp(setNew.azimuth, setOld.azimuth, f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("RepeaterPlane", interp(setNew.waypointAzimuth, setOld.waypointAzimuth, f), 0.0F, 0.0F);
        }
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
            this.mesh.chunkSetAngles("NeedleTrimmung", this.fm.CT.getTrimElevatorControl() * 25F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAirTemp", cvt(Atmosphere.temperature((float)this.fm.Loc.z), 213.09F, 313.09F, -8.5F, 87F), 0.0F, 0.0F);
        resetYPRmodifier();
        if(gun[0] != null)
        {
            Cockpit.xyz[0] = cvt(gun[0].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG17_R", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG17_2", gun[0].haveBullets());
            if(gun[1] != null)
            {
                Cockpit.xyz[0] = cvt(gun[1].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
                this.mesh.chunkSetLocate("RC_MG17_L", Cockpit.xyz, Cockpit.ypr);
                this.mesh.chunkVisible("XLampMG17_1", gun[1].haveBullets());
            }
            if(gun[2] != null)
            {
                Cockpit.xyz[0] = cvt(gun[2].countBullets(), 0.0F, 200F, -0.017F, 0.0F);
                this.mesh.chunkSetLocate("RC_MG151_R", Cockpit.xyz, Cockpit.ypr);
                this.mesh.chunkVisible("XLampMG151_1", gun[2].haveBullets());
            }
            this.mesh.chunkSetAngles("IgnitionSwitch", 24F * (float)this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Revi16Tinter", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -45F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Stick", 0.0F, (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl) * 20F, (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl) * 20F);
            resetYPRmodifier();
            if(this.fm.CT.saveWeaponControl[1])
                Cockpit.xyz[2] = -0.004F;
            this.mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
            float f2 = 0.0F;
            int i = 0;
            if(this.fm.CT.saveWeaponControl[0] || this.fm.CT.saveWeaponControl[1] || this.fm.CT.saveWeaponControl[3])
            {
                if(this.fm.CT.saveWeaponControl[0] || this.fm.CT.saveWeaponControl[1])
                    f2 = 20F;
                tClap = Time.tickCounter() + World.Rnd().nextInt(500, 1000);
            }
            if(Time.tickCounter() < tClap)
                i = 1;
            this.mesh.chunkSetAngles("SecTrigger2", -(240F + f2) * (pictClap = 0.85F * pictClap + 0.15F * (float)i), 0.0F, 0.0F);
            Cockpit.ypr[0] = interp(setNew.throttle, setOld.throttle, f) * 34F * 0.91F;
            Cockpit.xyz[2] = Cockpit.ypr[0] > 7F ? -0.006F : 0.0F;
            this.mesh.chunkSetLocate("ThrottleQuad", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkSetAngles("RPedalBase", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("RPedalStrut", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("RPedal", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F - this.fm.CT.getBrake() * 15F);
            this.mesh.chunkSetAngles("LPedalBase", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("LPedalStrut", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("LPedal", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F - this.fm.CT.getBrake() * 15F);
            this.mesh.chunkVisible("XLampTankSwitch", (float)this.fm.EI.engines[0].getStage() > 0.0F);
            this.mesh.chunkVisible("XLampFuelLow", this.fm.M.fuel < 43.2F);
            this.mesh.chunkVisible("XLampGearL_1", this.fm.CT.getGear() < 0.05F);
            this.mesh.chunkVisible("XLampGearL_2", this.fm.CT.getGear() > 0.95F && this.fm.Gears.lgear);
            this.mesh.chunkVisible("XLampGearR_1", this.fm.CT.getGear() < 0.05F);
            this.mesh.chunkVisible("XLampGearR_2", this.fm.CT.getGear() > 0.95F && this.fm.Gears.rgear);
            this.mesh.chunkVisible("XLampGearC_1", this.fm.CT.getFlap() < 0.1F);
            this.mesh.chunkVisible("XLampGearC_3", this.fm.CT.getFlap() > 0.1F && this.fm.CT.getFlap() < 0.5F);
            this.mesh.chunkVisible("XLampGearC_2", this.fm.CT.getFlap() > 0.9F);
            resetYPRmodifier();
            Cockpit.xyz[2] = this.fm.CT.GearControl < 0.5F ? 0.02F : 0.0F;
            if(this.fm.getAltitude() > 8990F)
                this.mesh.chunkVisible("XLampAlt", true);
            else
                this.mesh.chunkVisible("XLampAlt", false);
            float f3 = Atmosphere.temperature((float)this.fm.Loc.z) - 273.15F;
            if(f3 < -16F)
                this.mesh.chunkVisible("XLampPitot", true);
            else
            if(f3 > -12F)
                this.mesh.chunkVisible("XLampPitot", false);
        }
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 2) != 0 || (this.fm.AS.astateCockpitState & 1) != 0 || (this.fm.AS.astateCockpitState & 4) != 0)
        {
            this.mesh.chunkVisible("Revi16", false);
            this.mesh.chunkVisible("Revi16Tinter", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("DRevi16", true);
            this.mesh.materialReplace("D9GP1", "DD9GP1");
            this.mesh.materialReplace("D9GP1_night", "DD9GP1_night");
            this.mesh.chunkVisible("NeedleManPress", false);
            this.mesh.chunkVisible("NeedleRPM", false);
            this.mesh.chunkVisible("RepeaterOuter", false);
            this.mesh.chunkVisible("RepeaterPlane", false);
        }
        if((this.fm.AS.astateCockpitState & 0x40) != 0 || (this.fm.AS.astateCockpitState & 8) != 0 || (this.fm.AS.astateCockpitState & 0x10) != 0)
        {
            this.mesh.materialReplace("D9GP2", "DD9GP2");
            this.mesh.materialReplace("D9GP2_night", "DD9GP2_night");
            this.mesh.chunkVisible("NeedleAHCyl", false);
            this.mesh.chunkVisible("NeedleAHBank", false);
            this.mesh.chunkVisible("NeedleAHBar", false);
            this.mesh.chunkVisible("NeedleAHTurn", false);
            this.mesh.chunkVisible("NeedleFuelPress", false);
            this.mesh.chunkVisible("NeedleOilPress", false);
            this.mesh.materialReplace("Ta152GP4_MW50", "DTa152GP4");
            this.mesh.materialReplace("Ta152GP4_MW50_night", "DTa152GP4_night");
            this.mesh.chunkVisible("NeedleFuel", false);
            resetYPRmodifier();
            Cockpit.xyz[0] = -0.001F;
            Cockpit.xyz[1] = 0.008F;
            Cockpit.xyz[2] = 0.025F;
            Cockpit.ypr[0] = 3F;
            Cockpit.ypr[1] = -6F;
            Cockpit.ypr[2] = 1.5F;
            this.mesh.chunkSetLocate("IPCentral", Cockpit.xyz, Cockpit.ypr);
        }
        if((this.fm.AS.astateCockpitState & 0x20) != 0 || (this.fm.AS.astateCockpitState & 0x80) != 0)
        {
            this.mesh.materialReplace("Ta152GP3", "DTa152GP3");
            this.mesh.materialReplace("Ta152GP3_night", "DTa152GP3_night");
            this.mesh.chunkVisible("NeedleKMH", false);
            this.mesh.chunkVisible("NeedleCD", false);
            this.mesh.chunkVisible("NeedleAlt", false);
            this.mesh.chunkVisible("NeedleAltKM", false);
            this.mesh.materialReplace("Ta152Trans2", "DTa152Trans2");
            this.mesh.materialReplace("Ta152Trans2_night", "DTa152Trans2_night");
            this.mesh.chunkVisible("NeedleWaterTemp", false);
            this.mesh.chunkVisible("NeedleOilTemp", false);
        }
        retoggleLight();
    }

    private Gun gun[];

    static 
    {
        Property.set(CockpitFW_190V29.class, "normZN", 0.74F);
        Property.set(CockpitFW_190V29.class, "gsZN", 0.66F);
    }

}
