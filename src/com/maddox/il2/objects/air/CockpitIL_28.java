package com.maddox.il2.objects.air;

import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitIL_28 extends CockpitPilot
{
    private class Variables
    {

        float throttle1;
        float throttle2;
        float starter1;
        float starter2;
        float altimeter;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        AnglesFork compassRim;
        float vspeed;

        private Variables()
        {
            azimuth = new AnglesFork();
            waypointAzimuth = new AnglesFork();
            compassRim = new AnglesFork();
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
                setNew.throttle1 = 0.85F * setOld.throttle1 + fm.EI.engines[0].getControlThrottle() * 0.15F;
                setNew.throttle2 = 0.85F * setOld.throttle2 + fm.EI.engines[1].getControlThrottle() * 0.15F;
                setNew.starter1 = 0.94F * setOld.starter1 + 0.06F * (fm.EI.engines[0].getStage() <= 0 || fm.EI.engines[0].getStage() >= 6 ? 0.0F : 1.0F);
                setNew.starter2 = 0.94F * setOld.starter2 + 0.06F * (fm.EI.engines[1].getStage() <= 0 || fm.EI.engines[1].getStage() >= 6 ? 0.0F : 1.0F);
                setNew.altimeter = fm.getAltitude();
                if(useRealisticNavigationInstruments())
                {
                    setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(1.0F), getBeaconDirection());
                    float f = waypointAzimuth();
                    setNew.compassRim.setDeg(f - 90F);
                    setOld.compassRim.setDeg(f - 90F);
                } else
                {
                    setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), waypointAzimuth() - setOld.azimuth.getDeg(1.0F));
                    setNew.compassRim.setDeg(0.0F);
                    setOld.compassRim.setDeg(0.0F);
                }
                setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
                setNew.vspeed = (199F * setOld.vspeed + fm.getVertSpeed()) / 200F;
            }
            return true;
        }

        Interpolater()
        {
        }
    }

    public CockpitIL_28()
    {
        super("3DO/Cockpit/IL-28/IL-28hier.him", "bf109");
        bNeedSetUp = true;
        setOld = new Variables();
        setNew = new Variables();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        pictETP = 0.0F;
        pictFlap = 0.0F;
        pictGear = 0.0F;
        pictTLck = 0.0F;
        pictMet1 = 0.0F;
        pictMet2 = 0.0F;
        pictETrm = 0.0F;
        gun = new Gun[2];
        this.cockpitNightMats = (new String[] {
            "gauges_01", "gauges_02", "gauges_03", "gauges_04", "gauges_05", "Dgauges_01", "Dgauges_02", "Dgauges_03", "Dgauges_05"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
        if(this.acoustics != null)
            this.acoustics.globFX = new ReverbFXRoom(0.45F);
        this.printCompassHeading = true;
        this.limits6DoF = (new float[] {
            0.7F, 0.012F, -0.03F, 0.11F, 0.15F, -0.11F, 0.03F, -0.03F
        });
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
            bNeedSetUp = false;
        resetYPRmodifier();
        this.mesh.chunkSetAngles("Canopy", 0.0F, cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.8F, 0.0F, 94F), 0.0F);
        this.mesh.chunkSetAngles("CnOpenLvr", cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.08F, 0.0F, -94F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("GearHandle", 0.0F, 0.0F, 50F * (pictGear = 0.82F * pictGear + 0.18F * this.fm.CT.GearControl));
        this.mesh.chunkSetAngles("FlapHandle", 0.0F, 0.0F, 111F * (pictFlap = 0.88F * pictFlap + 0.12F * this.fm.CT.FlapsControl));
        this.mesh.chunkSetAngles("TQHandle1", 0.0F, 0.0F, -40.909F * interp(setNew.throttle1, setOld.throttle1, f));
        this.mesh.chunkSetAngles("TQHandle2", 0.0F, 0.0F, -40.909F * interp(setNew.throttle2, setOld.throttle2, f));
        this.mesh.chunkSetAngles("NossleLvr1", 0.0F, 0.0F, -40.909F * interp(setNew.throttle1, setOld.throttle1, f));
        this.mesh.chunkSetAngles("NossleLvr2", 0.0F, 0.0F, -40.909F * interp(setNew.throttle2, setOld.throttle2, f));
        this.mesh.chunkSetAngles("Lvr1", 0.0F, 0.0F, -25F * (pictTLck = 0.85F * pictTLck + 0.15F * (this.fm.Gears.bTailwheelLocked ? 1.0F : 0.0F)));
        if(this.fm.CT.getTrimElevatorControl() != pictETP)
        {
            if(this.fm.CT.getTrimElevatorControl() - pictETP > 0.0F)
            {
                this.mesh.chunkSetAngles("ElevTrim", 0.0F, -30F, 0.0F);
                pictETrm = Time.current();
            } else
            {
                this.mesh.chunkSetAngles("ElevTrim", 0.0F, 30F, 0.0F);
                pictETrm = Time.current();
            }
            pictETP = this.fm.CT.getTrimElevatorControl();
        } else
        if((float)Time.current() > pictETrm + 500F)
        {
            this.mesh.chunkSetAngles("ElevTrim", 0.0F, 0.0F, 0.0F);
            pictETrm = Time.current() + 0x7a120L;
        }
        if(gun[0] == null)
        {
            gun[0] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON01");
            gun[1] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON02");
        }
        if(bgun[0] == null)
        {
            bgun[0] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn01");
            bgun[1] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn02");
            bgun[2] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn03");
            bgun[3] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn04");
            bgun[4] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn05");
            bgun[5] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn06");
            bgun[6] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn07");
            bgun[7] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn08");
            bgun[8] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn09");
            bgun[9] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn10");
            bgun[10] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn11");
            bgun[11] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn12");
            bgun[12] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn13");
            bgun[13] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn14");
            bgun[14] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn15");
            bgun[15] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn16");
            bgun[16] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn17");
            bgun[17] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn18");
            bgun[18] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn19");
            bgun[19] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn20");
            bgun[20] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn21");
            bgun[21] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn22");
            bgun[22] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn23");
            bgun[23] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn24");
            bgun[24] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn25");
            bgun[25] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn26");
            bgun[26] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn27");
            bgun[27] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_BombSpawn28");
        }
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.CT.getRudder(), -1F, 1.0F, -0.035F, 0.035F);
        this.mesh.chunkSetLocate("Pedal_L", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = -Cockpit.xyz[1];
        this.mesh.chunkSetLocate("Pedal_R", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Columnbase", -8F * (pictElev = 0.65F * pictElev + 0.35F * this.fm.CT.ElevatorControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", -45F * (pictAiler = 0.65F * pictAiler + 0.35F * this.fm.CT.AileronControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Trigger2", this.fm.CT.saveWeaponControl[1] ? -14.5F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedRPM1", 0.0F, floatindex(cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4100F, 0.0F, 14F), rpmScale), 0.0F);
        pictMet1 = 0.96F * pictMet1 + 0.04F * (0.6F * this.fm.EI.engines[0].getThrustOutput() * this.fm.EI.engines[0].getControlThrottle() * (this.fm.EI.engines[0].getStage() != 6 ? 0.02F : 1.0F));
        this.mesh.chunkSetAngles("NeedExhstPress1", 0.0F, cvt(pictMet1, 0.0F, 1.0F, 0.0F, 270F), 0.0F);
        this.mesh.chunkSetAngles("NeedFuelPress1", cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.55F, 0.0F, 1.0F, 0.0F, 284F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedExstT1", 0.0F, cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 1200F, 0.0F, 112F), 0.0F);
        this.mesh.chunkSetAngles("NeedOilP1", 0.0F, cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness(), 0.0F, 6.46F, 0.0F, 270F), 0.0F);
        this.mesh.chunkSetAngles("NeedRPM2", 0.0F, floatindex(cvt(this.fm.EI.engines[1].getRPM(), 0.0F, 4100F, 0.0F, 14F), rpmScale), 0.0F);
        pictMet2 = 0.96F * pictMet2 + 0.04F * (0.6F * this.fm.EI.engines[1].getThrustOutput() * this.fm.EI.engines[1].getControlThrottle() * (this.fm.EI.engines[1].getStage() != 6 ? 0.02F : 1.0F));
        this.mesh.chunkSetAngles("NeedExhstPress2", 0.0F, cvt(pictMet2, 0.0F, 1.0F, 0.0F, 270F), 0.0F);
        this.mesh.chunkSetAngles("NeedFuelPress2", cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.55F, 0.0F, 1.0F, 0.0F, 284F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedExstT2", 0.0F, cvt(this.fm.EI.engines[1].tWaterOut, 0.0F, 1200F, 0.0F, 112F), 0.0F);
        this.mesh.chunkSetAngles("NeedOilP2", 0.0F, cvt(1.0F + 0.05F * this.fm.EI.engines[1].tOilOut * this.fm.EI.engines[1].getReadyness(), 0.0F, 6.46F, 0.0F, 270F), 0.0F);
        this.mesh.chunkSetAngles("NeedFuel1", 0.0F, floatindex(cvt(this.fm.M.fuel, 0.0F, 6400F, 0.0F, 4F), fuelScale), 0.0F);
        this.mesh.chunkSetAngles("NeedFuel2", 0.0F, floatindex(cvt(this.fm.M.fuel, 0.0F, 6400F, 0.0F, 4F), fuelScale), 0.0F);
        this.mesh.chunkSetAngles("NeedAlt_Km", 0.0F, cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 60000F, 0.0F, 2160F), 0.0F);
        this.mesh.chunkSetAngles("NeedAlt_M", 0.0F, cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 60000F, 0.0F, 21600F), 0.0F);
        if(useRealisticNavigationInstruments())
        {
            this.mesh.chunkSetAngles("NeedCompassB", 0.0F, setNew.azimuth.getDeg(f) - setNew.compassRim.getDeg(f), 0.0F);
            this.mesh.chunkSetAngles("NeedCompassA", 0.0F, -setNew.compassRim.getDeg(f), 0.0F);
        } else
        {
            this.mesh.chunkSetAngles("NeedCompassA", 0.0F, -setNew.azimuth.getDeg(f), 0.0F);
            this.mesh.chunkSetAngles("NeedCompassB", 0.0F, 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("NeedSpeed", 0.0F, cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 1200F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("NeedClimb", 0.0F, cvt(setNew.vspeed, -30F, 30F, -180F, 180F), 0.0F);
        this.mesh.chunkSetAngles("NeedAHCyl", 0.0F, -this.fm.Or.getKren() + 180F, 0.0F);
        this.mesh.chunkSetAngles("NeedAHBar", 0.0F, 0.0F, -this.fm.Or.getTangage());
        this.mesh.chunkSetAngles("NeedTurn", 0.0F, cvt(getBall(8D), -8F, 8F, -15F, 15F), 0.0F);
        this.mesh.chunkSetAngles("NeedDF", 0.0F, cvt(setNew.waypointAzimuth.getDeg(f * 0.2F), -90F, 90F, -16.5F, 16.5F), 0.0F);
        this.mesh.chunkSetAngles("NeedHour", 0.0F, cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);
        this.mesh.chunkSetAngles("NeedMin", 0.0F, cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("NeedStarter1", cvt(setNew.starter1, 0.0F, 1.0F, 0.0F, -120F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedStarter2", cvt(setNew.starter2, 0.0F, 1.0F, 0.0F, -120F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedEmrgAirP", -63.5F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedAirSysP", this.fm.Gears.isHydroOperable() ? -133.5F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkVisible("FlareGearUp_R", this.fm.CT.getGear() < 0.01F || !this.fm.Gears.rgear);
        this.mesh.chunkVisible("FlareGearUp_L", this.fm.CT.getGear() < 0.01F || !this.fm.Gears.lgear);
        this.mesh.chunkVisible("FlareGearUp_C", this.fm.CT.getGear() < 0.01F);
        this.mesh.chunkVisible("FlareGearDn_R", this.fm.CT.getGear() > 0.99F && this.fm.Gears.rgear);
        this.mesh.chunkVisible("FlareGearDn_L", this.fm.CT.getGear() > 0.99F && this.fm.Gears.lgear);
        this.mesh.chunkVisible("FlareGearDn_C", this.fm.CT.getGear() > 0.99F);
        if(gun[0] != null)
            this.mesh.chunkVisible("L_Bullet", gun[0].haveBullets());
        if(gun[1] != null)
            this.mesh.chunkVisible("L_Bullet", gun[1].haveBullets());
        this.mesh.chunkVisible("L_NBomb", bgun[0].haveBullets() || bgun[1].haveBullets() || bgun[2].haveBullets() || bgun[3].haveBullets() || bgun[4].haveBullets() || bgun[5].haveBullets() || bgun[6].haveBullets() || bgun[7].haveBullets() || bgun[8].haveBullets() || bgun[9].haveBullets() || bgun[10].haveBullets() || bgun[11].haveBullets() || bgun[12].haveBullets() || bgun[13].haveBullets() || bgun[14].haveBullets() || bgun[15].haveBullets() || bgun[16].haveBullets() || bgun[17].haveBullets() || bgun[18].haveBullets() || bgun[19].haveBullets() || bgun[20].haveBullets() || bgun[21].haveBullets() || bgun[22].haveBullets() || bgun[23].haveBullets() || bgun[24].haveBullets() || bgun[25].haveBullets() || bgun[26].haveBullets() || bgun[27].haveBullets());
        this.mesh.chunkVisible("FlareFuel", this.fm.M.fuel < 640F);
        this.mesh.chunkVisible("L_Flap", this.fm.CT.getFlap() > 0.1F && this.fm.CT.getFlap() < 0.3F);
        this.mesh.chunkVisible("L_Flap2", this.fm.CT.getFlap() > 0.3F && this.fm.CT.getFlap() < 0.9F);
        this.mesh.chunkVisible("L_Flap3", this.fm.CT.getFlap() > 0.9F);
        if(this.fm.CT.getBayDoor() > 0.5F)
            this.mesh.chunkVisible("L_BayDoor", true);
        else
            this.mesh.chunkVisible("L_BayDoor", false);
    }

    protected float waypointAzimuth()
    {
        return waypointAzimuthInvertMinus(10F);
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 2) != 0)
        {
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
        }
        if((this.fm.AS.astateCockpitState & 1) != 0)
        {
            this.mesh.chunkVisible("DamageGlass2", true);
            this.mesh.chunkVisible("DamageGlass3", true);
        }
        if((this.fm.AS.astateCockpitState & 0x80) == 0);
        if((this.fm.AS.astateCockpitState & 0x40) != 0)
        {
            this.mesh.chunkVisible("Gages1_D0", false);
            this.mesh.chunkVisible("Gages1_D1", true);
            this.mesh.chunkVisible("NeedSpeed", false);
            this.mesh.chunkVisible("NeedClimb", false);
            this.mesh.chunkVisible("NeedAlt_Km", false);
            this.mesh.chunkVisible("NeedAlt_M", false);
            this.mesh.chunkVisible("NeedDF", false);
            this.mesh.chunkVisible("NeedCompassA", false);
            this.mesh.chunkVisible("NeedCompassB", false);
            this.mesh.chunkVisible("DamageHull1", true);
        }
        if((this.fm.AS.astateCockpitState & 4) != 0)
        {
            this.mesh.chunkVisible("Gages3_D0", false);
            this.mesh.chunkVisible("Gages3_D1", true);
            this.mesh.chunkVisible("NeedHour", false);
            this.mesh.chunkVisible("NeedMin", false);
            this.mesh.chunkVisible("NeedRPM1", false);
            this.mesh.chunkVisible("NeedExhstPress1", false);
            this.mesh.chunkVisible("DamageHull3", true);
        }
        if((this.fm.AS.astateCockpitState & 8) == 0);
        if((this.fm.AS.astateCockpitState & 0x10) != 0)
        {
            this.mesh.chunkVisible("Gages4_D0", false);
            this.mesh.chunkVisible("Gages4_D1", true);
            this.mesh.chunkVisible("NeedRPM2", false);
            this.mesh.chunkVisible("NeedOilP1", false);
            this.mesh.chunkVisible("NeedFuel1", false);
            this.mesh.chunkVisible("NeedExstT1", false);
            this.mesh.chunkVisible("DamageHull2", true);
        }
        if((this.fm.AS.astateCockpitState & 0x20) != 0)
        {
            this.mesh.chunkVisible("Gages5_D0", false);
            this.mesh.chunkVisible("Gages5_D1", true);
            this.mesh.chunkVisible("NeedOilP2", false);
            this.mesh.chunkVisible("NeedExhstPress2", false);
            this.mesh.chunkVisible("NeedExstT2", false);
            this.mesh.chunkVisible("NeedFuel2", false);
            this.mesh.chunkVisible("", false);
            this.mesh.chunkVisible("", false);
            this.mesh.chunkVisible("", false);
            this.mesh.chunkVisible("", false);
        }
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

    public void doToggleDim()
    {
    }

    private boolean bNeedSetUp;
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private float pictAiler;
    private float pictElev;
    private float pictETP;
    private float pictFlap;
    private float pictGear;
    private float pictTLck;
    private float pictMet1;
    private float pictMet2;
    private float pictETrm;
    private static final float rpmScale[] = {
        0.0F, 8F, 23.5F, 40F, 58.5F, 81F, 104.5F, 130.2F, 158.5F, 187F, 
        217.5F, 251.1F, 281.5F, 289.5F, 295.5F
    };
    private static final float fuelScale[] = {
        0.0F, 18.5F, 49F, 80F, 87F
    };
    private BulletEmitter bgun[] = {
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null
    };
    private Gun gun[];

}
