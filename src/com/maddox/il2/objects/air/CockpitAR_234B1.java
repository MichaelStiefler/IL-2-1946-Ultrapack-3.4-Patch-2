package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.BaseGameVersion;
import com.maddox.sas1946.il2.util.CrossVersion;

public class CockpitAR_234B1 extends CockpitPilot
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
                setNew.throttle1 = 0.92F * setOld.throttle1 + 0.08F * fm.EI.engines[0].getControlThrottle();
                setNew.throttle2 = 0.92F * setOld.throttle2 + 0.08F * fm.EI.engines[1].getControlThrottle();
                setNew.vspeed = (499F * setOld.vspeed + fm.getVertSpeed()) / 500F;
                setNew.tankSelector1 = 0.92F * setOld.tankSelector1 + 0.08F * (float)((AR_234B1)aircraft()).tankSelectors[0];
                setNew.tankSelector2 = 0.92F * setOld.tankSelector2 + 0.08F * (float)((AR_234B1)aircraft()).tankSelectors[1];
                setNew.throttleLockLever = 0.92F * setOld.throttleLockLever + 0.08F * (((AR_234B1)aircraft()).throttleLocked ? 1.0F : 0.0F);
                if(BaseGameVersion.is410orLater())
                {
                    float f = waypointAzimuth();
                    if(useRealisticNavigationInstruments())
                    {
                        setNew.waypointAzimuth.setDeg(f - 90F);
                        setOld.waypointAzimuth.setDeg(f - 90F);
                    } else
                    {
                        setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), f - setOld.azimuth.getDeg(1.0F));
                    }
                    setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
                    w.set(fm.getW());
                    fm.Or.transform(w);
                    setNew.turn = (12F * setOld.turn + w.z) / 13F;
                    setNew.beaconDirection = (10F * setOld.beaconDirection + getBeaconDirection()) / 11F;
                    setNew.beaconRange = (10F * setOld.beaconRange + getBeaconRange()) / 11F;
                } else
                {
                    setNew.azimuth.setDeg(fm.Or.getYaw());
                    if(setOld.azimuth.getDstDeg() > 270F && setNew.azimuth.getDstDeg() < 90F)
                        setOld.azimuth.setDstDeg(setOld.azimuth.getDstDeg() - 360F);
                    if(setOld.azimuth.getDstDeg() < 90F && setNew.azimuth.getDstDeg() > 270F)
                        setOld.azimuth.setDstDeg(setOld.azimuth.getDstDeg() + 360F);
                    setNew.waypointAzimuth.setDstDeg((10F * setOld.waypointAzimuth.getDstDeg() + (waypointAzimuth() - setOld.azimuth.getDstDeg()) + World.Rnd().nextFloat(-30F, 30F)) / 11F);
                }
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
        float throttle1;
        float throttle2;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float turn;
        float beaconDirection;
        float beaconRange;
        float vspeed;
        float tankSelector1;
        float tankSelector2;
        float throttleLockLever;

        private Variables()
        {
            azimuth = new AnglesFork();
            waypointAzimuth = new AnglesFork();
        }
    }

    protected float waypointAzimuth()
    {
        if(BaseGameVersion.is410orLater())
            return waypointAzimuthInvertMinus(30F);
        WayPoint waypoint = this.fm.AP.way.curr();
        if(waypoint == null)
        {
            return 0.0F;
        } else
        {
            waypoint.getP(Cockpit.P1);
            Cockpit.V.sub(Cockpit.P1, this.fm.Loc);
            return (float)(Math.toDegrees(Math.atan2(Cockpit.V.x, Cockpit.V.y)));
        }
    }

    public CockpitAR_234B1()
    {
        super("3DO/Cockpit/Ar-234B-1/hier.him", "bf109");
        bNeedSetUp = true;
        setOld = new Variables();
        setNew = new Variables();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictGear = 0.0F;
        w = new Vector3f();
        this.cockpitNightMats = (new String[] {
            "D_gauges_1_TR", "D_gauges_2_TR", "D_gauges_3_TR", "D_gauges_4_TR", "D_gauges_6_TR", "gauges_1_TR", "gauges_2_TR", "gauges_3_TR", "gauges_4_TR", "gauges_5_TR", 
            "gauges_6_TR", "gauges_7_TR", "gauges_8_TR", "gauges_9_TR"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
        CrossVersion.setPrintCompassHeading(this, true);
        CrossVersion.setLimits6DoF(this, new float[] {
            0.7F, 0.055F, -0.07F, 0.08F, 0.1F, -0.09F, 0.03F, -0.03F
        });
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.CT.getRudder(), -1F, 1.0F, -0.03F, 0.03F);
        if(BaseGameVersion.is413orLater())
            Cockpit.ypr[0] = this.fm.CT.getBrakeL() * 15F;
        else
            Cockpit.ypr[0] = this.fm.CT.getBrake() * 15F;
        this.mesh.chunkSetLocate("PedalL", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = -Cockpit.xyz[1];
        if(BaseGameVersion.is413orLater())
            Cockpit.ypr[0] = this.fm.CT.getBrakeR() * 15F;
        else
            Cockpit.ypr[0] = this.fm.CT.getBrake() * 15F;
        this.mesh.chunkSetLocate("PedalR", Cockpit.xyz, Cockpit.ypr);
        Cockpit.ypr[1] = 0.0F;
        pictGear = 0.8F * pictGear + 0.2F * this.fm.CT.GearControl;
        this.mesh.chunkSetAngles("ruchkaShassi", cvt(pictGear, 0.0F, 1.0F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ruchkaGaza1", cvt(interp(setNew.throttle1, setOld.throttle1, f), 0.0F, 1.0F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ruchkaGaza2", cvt(interp(setNew.throttle2, setOld.throttle2, f), 0.0F, 1.0F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ruchkaSopla", cvt(interp(setNew.throttleLockLever, setOld.throttleLockLever, f), 0.0F, 1.0F, 0.0F, 50F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ruchkaFuel1", cvt(interp(setNew.tankSelector1, setOld.tankSelector1, f), 0.0F, 2.0F, 0.0F, -36F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ruchkaFuel2", cvt(interp(setNew.tankSelector2, setOld.tankSelector2, f), 0.0F, 2.0F, 0.0F, -36F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ETrim", -30F * this.fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RTrim", -300F * this.fm.CT.getTrimRudderControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("os_V", -15F * (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Srul", 60F * (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl), 0.0F, 0.0F);
        resetYPRmodifier();
        if(this.cockpitLightControl)
            Cockpit.xyz[2] = 0.00365F;
        this.mesh.chunkSetLocate("Z_lightSW", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = this.fm.CT.saveWeaponControl[0] ? 0.0059F : 0.0F;
        this.mesh.chunkSetLocate("r_one", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = this.fm.CT.saveWeaponControl[3] ? 0.0059F : 0.0F;
        this.mesh.chunkSetLocate("r_two", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(-this.fm.CT.trimElevator, -0.5F, 0.5F, -0.0475F, 0.0475F);
        this.mesh.chunkSetLocate("Need_ETrim", Cockpit.xyz, Cockpit.ypr);
        resetYPRmodifier();
        Cockpit.xyz[0] = cvt(-this.fm.CT.trimRudder, -0.5F, 0.5F, -0.029F, 0.029F);
        this.mesh.chunkSetLocate("Need_RTrim", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("zClock1a", cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zClock1b", cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zClock1c", cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAirTemp", cvt(Atmosphere.temperature((float)this.fm.Loc.z), 213.09F, 313.09F, -30F, 20F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zHydropress", this.fm.Gears.isHydroOperable() ? 120F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zMCompc", 0.0F, 0.0F, cvt(this.fm.Or.getTangage(), -22.2F, 22.2F, -22.2F, 22.2F));
        this.mesh.chunkSetAngles("zMCompa", cvt(this.fm.Or.getKren(), -22.2F, 22.2F, -22.2F, 22.2F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zMCompb", -90F - setNew.azimuth.getDeg(f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSpeed1a", floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt1a", cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 20000F, 0.0F, -7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt1b", cvt(setNew.altimeter, 0.0F, 14000F, 0.0F, 315F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM1", 22.5F + floatindex(cvt(this.fm.EI.engines[0].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM2", 22.5F + floatindex(cvt(this.fm.EI.engines[1].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasPrs1a", cvt(this.fm.M.fuel > 1.0F ? this.fm.EI.engines[0].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, 270F) * cvt(this.fm.EI.engines[0].tOilOut, 110F, 130F, 1.0F, 1.1F) * cvt(this.fm.EI.engines[0].tWaterOut, 630F, 650F, 1.0F, 1.1F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasPrs1b", cvt(this.fm.M.fuel > 1.0F ? this.fm.EI.engines[0].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, 130F) * cvt(this.fm.EI.engines[0].tOilOut, 110F, 130F, 1.0F, 1.15F) * cvt(this.fm.EI.engines[0].tWaterOut, 630F, 650F, 1.0F, 1.15F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasPrs2a", cvt(this.fm.M.fuel > 1.0F ? this.fm.EI.engines[1].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, 270F) * cvt(this.fm.EI.engines[1].tOilOut, 110F, 130F, 1.0F, 1.1F) * cvt(this.fm.EI.engines[1].tWaterOut, 630F, 650F, 1.0F, 1.1F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasPrs2b", cvt(this.fm.M.fuel > 1.0F ? this.fm.EI.engines[1].getPowerOutput() : 0.0F, 0.0F, 1.0F, 0.0F, -130F) * cvt(this.fm.EI.engines[1].tOilOut, 110F, 130F, 1.0F, 1.15F) * cvt(this.fm.EI.engines[1].tWaterOut, 630F, 650F, 1.0F, 1.15F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPrs1a", this.fm.EI.engines[0].tOilOut >= 110F ? cvt(this.fm.EI.engines[0].tOilOut, 110F, 138F, 135F, 270F) : cvt(this.fm.EI.engines[0].tOilOut, 20F, 110F, 0.0F, 135F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPrs2a", this.fm.EI.engines[1].tOilOut >= 110F ? cvt(this.fm.EI.engines[1].tOilOut, 110F, 138F, 135F, 270F) : cvt(this.fm.EI.engines[1].tOilOut, 20F, 110F, 0.0F, 135F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPrs1a", this.fm.M.fuel > 1.0F ? cvt(interp(setNew.throttle1, setOld.throttle1, f), 0.0F, 1.0F, 0.0F, 135F) * cvt(this.fm.EI.engines[0].tOilOut, 110F, 138F, 1.0F, 2.0F) : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPrs2a", this.fm.M.fuel > 1.0F ? cvt(interp(setNew.throttle2, setOld.throttle2, f), 0.0F, 1.0F, 0.0F, 135F) * cvt(this.fm.EI.engines[1].tOilOut, 110F, 138F, 1.0F, 2.0F) : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasTemp1a", cvt(this.fm.EI.engines[0].getThrustOutput() * cvt(this.fm.EI.engines[0].tWaterOut, 630F, 650F, 1.0F, 2.0F), 0.0F, 2.0F, 0.0F, 61F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGasTemp2a", cvt(this.fm.EI.engines[1].getThrustOutput() * cvt(this.fm.EI.engines[1].tWaterOut, 630F, 650F, 1.0F, 2.0F), 0.0F, 2.0F, 0.0F, 61F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zHorizon1b", -this.fm.Or.getKren(), 0.0F, 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.Or.getTangage(), -45F, 45F, 0.025F, -0.025F);
        this.mesh.chunkSetLocate("zHorizon1a", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("zSlide1a", cvt(getBall(6D), -6F, 6F, -15F, 15F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRoll1a", cvt(setNew.turn, -0.23562F, 0.23562F, 26.5F, -26.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zVariometer1a", setNew.vspeed >= 0.0F ? floatindex(cvt(setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale) : -floatindex(cvt(-setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale), 0.0F, 0.0F);
        if(BaseGameVersion.is410orLater() && useRealisticNavigationInstruments())
        {
            this.mesh.chunkSetAngles("zAzimuth1b", setNew.azimuth.getDeg(f) - setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("zAzimuth1a", -setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
        } else
        {
            this.mesh.chunkSetAngles("zAzimuth1a", -setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("zAzimuth1b", setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("Z_Course1a", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Course1b", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Air1", 135F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelQ1", cvt(this.fm.M.fuel, 0.0F, 2400F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelQ2", cvt(this.fm.M.fuel, 0.0F, 4000F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkVisible("Z_Red1", this.fm.getSpeedKMH() < 160F);
        this.mesh.chunkVisible("Z_Red2", this.fm.M.fuel < 600F);
        this.mesh.chunkVisible("Z_Red3", this.fm.M.fuel < 250F);
        this.mesh.chunkVisible("Z_Red4", this.fm.CT.getFlap() < 0.1F);
        if(BaseGameVersion.is412orLater())
        {
            this.mesh.chunkVisible("Z_Red5", this.fm.CT.getGearL() < 0.01F);
            this.mesh.chunkVisible("Z_Red6", this.fm.CT.getGearC() < 0.01F);
            this.mesh.chunkVisible("Z_Red7", this.fm.CT.getGearR() < 0.01F);
            this.mesh.chunkVisible("Z_Green2", this.fm.CT.getGearL() > 0.99F && this.fm.Gears.lgear);
            this.mesh.chunkVisible("Z_Green3", this.fm.CT.getGearC() > 0.99F && this.fm.Gears.cgear);
            this.mesh.chunkVisible("Z_Green4", this.fm.CT.getGearR() > 0.99F && this.fm.Gears.rgear);
        } else
        {
            this.mesh.chunkVisible("Z_Red5", this.fm.CT.getGear() < 0.01F);
            this.mesh.chunkVisible("Z_Red6", this.fm.CT.getGear() < 0.01F);
            this.mesh.chunkVisible("Z_Red7", this.fm.CT.getGear() < 0.01F);
            this.mesh.chunkVisible("Z_Green2", this.fm.CT.getGear() > 0.99F && this.fm.Gears.lgear);
            this.mesh.chunkVisible("Z_Green3", this.fm.CT.getGear() > 0.99F && this.fm.Gears.cgear);
            this.mesh.chunkVisible("Z_Green4", this.fm.CT.getGear() > 0.99F && this.fm.Gears.rgear);
        }
        this.mesh.chunkVisible("Z_Red8", this.fm.CT.getFlap() < 0.1F);
        this.mesh.chunkVisible("Z_Green1", this.fm.CT.getFlap() > 0.665F);
        this.mesh.chunkVisible("Z_Green5", this.fm.CT.getFlap() > 0.665F);
        this.mesh.chunkVisible("Z_White1", this.fm.CT.getFlap() > 0.265F);
        this.mesh.chunkVisible("Z_White2", this.fm.CT.getFlap() > 0.265F);
        this.mesh.chunkSetAngles("Z_Course1b", cvt(setNew.beaconDirection, -45F, 45F, -20F, 20F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Course1a", cvt(setNew.beaconRange, 0.0F, 1.0F, 20F, -20F), 0.0F, 0.0F);
        this.mesh.chunkVisible("AFN2_RED", BaseGameVersion.is410orLater() && isOnBlindLandingMarker());
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

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 2) != 0)
        {
            this.mesh.chunkVisible("Z_Holes1_D1", true);
            this.mesh.chunkVisible("Z_Holes2_D1", true);
            this.mesh.chunkVisible("Z_Holes3_D1", true);
            this.mesh.chunkVisible("Z_Holes4_D1", true);
        }
        if((this.fm.AS.astateCockpitState & 0x40) != 0)
        {
            this.mesh.chunkVisible("pribors2_d1", true);
            this.mesh.chunkVisible("pribors2", false);
            this.mesh.chunkVisible("zAlt1a", false);
            this.mesh.chunkVisible("zAlt1b", false);
            this.mesh.chunkVisible("zHorizon1a", false);
            this.mesh.chunkVisible("zHorizon1b", false);
            this.mesh.chunkVisible("zSlide1a", false);
            this.mesh.chunkVisible("zRoll1a", false);
            this.mesh.chunkVisible("zRPM1", false);
            this.mesh.chunkVisible("zVariometer1a", false);
        }
        if((this.fm.AS.astateCockpitState & 4) != 0)
        {
            this.mesh.chunkVisible("Z_Holes5_D1", true);
            this.mesh.chunkVisible("pribors1_d1", true);
            this.mesh.chunkVisible("pribors1", false);
            this.mesh.chunkVisible("zGasPrs1b", false);
            this.mesh.chunkVisible("zGasPrs2b", false);
            this.mesh.chunkVisible("zSpeed1a", false);
        }
        if((this.fm.AS.astateCockpitState & 8) != 0)
        {
            this.mesh.chunkVisible("pribors4_d1", true);
            this.mesh.chunkVisible("pribors4", false);
            this.mesh.chunkVisible("zGasPrs2a", false);
            this.mesh.chunkVisible("zOilPrs1a", false);
            this.mesh.chunkVisible("zOilPrs2a", false);
            this.mesh.chunkVisible("zFuelPrs1a", false);
            this.mesh.chunkVisible("zFuelPrs2a", false);
            this.mesh.chunkVisible("zGasTemp1a", false);
            this.mesh.chunkVisible("zGasTemp2a", false);
            this.mesh.chunkVisible("zFuelQ1", false);
            this.mesh.chunkVisible("zFuelQ2", false);
        }
        if((this.fm.AS.astateCockpitState & 0x10) != 0)
        {
            this.mesh.chunkVisible("Z_Holes6_D1", true);
            this.mesh.chunkVisible("pribors3_d1", true);
            this.mesh.chunkVisible("pribors3", false);
            this.mesh.chunkVisible("Z_Course1a", false);
            this.mesh.chunkVisible("Z_Course1b", false);
            this.mesh.chunkVisible("zRPM2", false);
            this.mesh.chunkVisible("zGasPrs1a", false);
        }
        retoggleLight();
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
    }

    public void doToggleAim(boolean flag)
    {
    }

    public boolean isToggleAim()
    {
        return false;
    }

    public boolean isToggleUp()
    {
        return false;
    }

    private boolean bNeedSetUp;
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private float pictAiler;
    private float pictElev;
    private float pictGear;
    public Vector3f w;
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
    public float limits6DoF[] = {
        0.7F, 0.055F, -0.07F, 0.08F, 0.1F, -0.09F, 0.03F, -0.03F
    };

    static 
    {
        Property.set(CockpitAR_234B1.class, "normZN", 1.12F);
    }

}
