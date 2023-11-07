package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitJU_88G6_Radar extends CockpitPilot {
    private class Variables {

        float      throttle1;
        float      prop1;
        float      throttle2;
        float      prop2;
        float      altimeter;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float      vspeed;
        float      dimPosition;
        float      cons;
        float      alpha;
        AnglesFork radioCompassAzimuth;
        float      beaconDirection;
        float      beaconRange;

        private Variables() {
            this.azimuth = new AnglesFork();
            this.waypointAzimuth = new AnglesFork();
            this.radioCompassAzimuth = new AnglesFork();
        }

    }

    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (JU_88G6.bChangedPit) {
                JU_88G6.bChangedPit = false;
            }
            if (CockpitJU_88G6_Radar.this.fm != null) {
                CockpitJU_88G6_Radar.this.setTmp = CockpitJU_88G6_Radar.this.setOld;
                CockpitJU_88G6_Radar.this.setOld = CockpitJU_88G6_Radar.this.setNew;
                CockpitJU_88G6_Radar.this.setNew = CockpitJU_88G6_Radar.this.setTmp;
                CockpitJU_88G6_Radar.this.setNew.throttle1 = (0.85F * CockpitJU_88G6_Radar.this.setOld.throttle1) + (CockpitJU_88G6_Radar.this.fm.EI.engines[0].getControlThrottle() * 0.15F);
                CockpitJU_88G6_Radar.this.setNew.prop1 = (0.85F * CockpitJU_88G6_Radar.this.setOld.prop1) + (CockpitJU_88G6_Radar.this.fm.EI.engines[0].getControlProp() * 0.15F);
                CockpitJU_88G6_Radar.this.setNew.throttle2 = (0.85F * CockpitJU_88G6_Radar.this.setOld.throttle2) + (CockpitJU_88G6_Radar.this.fm.EI.engines[1].getControlThrottle() * 0.15F);
                CockpitJU_88G6_Radar.this.setNew.prop2 = (0.85F * CockpitJU_88G6_Radar.this.setOld.prop2) + (CockpitJU_88G6_Radar.this.fm.EI.engines[1].getControlProp() * 0.15F);
                CockpitJU_88G6_Radar.this.setNew.altimeter = CockpitJU_88G6_Radar.this.fm.getAltitude();
                float f = CockpitJU_88G6_Radar.this.waypointAzimuth();
                if (CockpitJU_88G6_Radar.this.useRealisticNavigationInstruments()) {
                    CockpitJU_88G6_Radar.this.setNew.waypointAzimuth.setDeg(f - 90F);
                    CockpitJU_88G6_Radar.this.setOld.waypointAzimuth.setDeg(f - 90F);
                    CockpitJU_88G6_Radar.this.setNew.radioCompassAzimuth.setDeg(CockpitJU_88G6_Radar.this.setOld.radioCompassAzimuth.getDeg(0.02F), CockpitJU_88G6_Radar.this.radioCompassAzimuthInvertMinus() - CockpitJU_88G6_Radar.this.setOld.azimuth.getDeg(1.0F) - 90F);
                } else {
                    CockpitJU_88G6_Radar.this.setNew.waypointAzimuth.setDeg(CockpitJU_88G6_Radar.this.setOld.waypointAzimuth.getDeg(0.1F), f - CockpitJU_88G6_Radar.this.setOld.azimuth.getDeg(1.0F));
                }
                CockpitJU_88G6_Radar.this.setNew.azimuth.setDeg(CockpitJU_88G6_Radar.this.setOld.azimuth.getDeg(1.0F), CockpitJU_88G6_Radar.this.fm.Or.azimut());
                CockpitJU_88G6_Radar.this.setNew.vspeed = ((199F * CockpitJU_88G6_Radar.this.setOld.vspeed) + CockpitJU_88G6_Radar.this.fm.getVertSpeed()) / 200F;
                if (CockpitJU_88G6_Radar.this.cockpitDimControl) {
                    if (CockpitJU_88G6_Radar.this.setNew.dimPosition > 0.0F) {
                        CockpitJU_88G6_Radar.this.setNew.dimPosition = CockpitJU_88G6_Radar.this.setNew.dimPosition - 0.05F;
                    }
                } else if (CockpitJU_88G6_Radar.this.setNew.dimPosition < 1.0F) {
                    CockpitJU_88G6_Radar.this.setNew.dimPosition = CockpitJU_88G6_Radar.this.setNew.dimPosition + 0.05F;
                }
                float f1 = CockpitJU_88G6_Radar.this.prevFuel - CockpitJU_88G6_Radar.this.fm.M.fuel;
                CockpitJU_88G6_Radar.this.prevFuel = CockpitJU_88G6_Radar.this.fm.M.fuel;
                f1 /= 0.72F;
                f1 /= Time.tickLenFs();
                f1 *= 3600F;
                CockpitJU_88G6_Radar.this.setNew.cons = (0.91F * CockpitJU_88G6_Radar.this.setOld.cons) + (0.09F * f1);
            }
            CockpitJU_88G6_Radar.this.setNew.beaconDirection = ((10F * CockpitJU_88G6_Radar.this.setOld.beaconDirection) + CockpitJU_88G6_Radar.this.getBeaconDirection()) / 11F;
            CockpitJU_88G6_Radar.this.setNew.beaconRange = ((10F * CockpitJU_88G6_Radar.this.setOld.beaconRange) + CockpitJU_88G6_Radar.this.getBeaconRange()) / 11F;
            return true;
        }

        Interpolater() {
        }
    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            ((TypeRadarLiSN2Carrier) this.aircraft()).setCurPilot(2);
            Hook cameraHook = new HookNamed(this.mesh, "CAMERA");
            Loc loc1 = new Loc(0D, 0D, 0D, 90F, 0F, 0F);
            Loc loc2 = new Loc();
            cameraHook.computePos(this.aircraft(), loc1, loc2);
            HookPilot.current.setInstantOrient(loc2.getOrient().getAzimut(), loc2.getOrient().getTangage(), loc2.getOrient().getRoll());
            this.aircraft().hierMesh().chunkVisible("fakeNose_D0", false);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D1", false);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D2", false);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D3", false);
            this.aircraft().hierMesh().chunkVisible("Turret1B_D0", false);
            this.aircraft().hierMesh().chunkVisible("BlisterTop_D0", false);
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        if (this.isFocused()) {
            this.aircraft().hierMesh().chunkVisible("fakeNose_D0", this.aircraft().hierMesh().isChunkVisible("Nose_D0"));
            this.aircraft().hierMesh().chunkVisible("fakeNose_D1", this.aircraft().hierMesh().isChunkVisible("Nose_D1"));
            this.aircraft().hierMesh().chunkVisible("fakeNose_D2", this.aircraft().hierMesh().isChunkVisible("Nose_D2"));
            this.aircraft().hierMesh().chunkVisible("fakeNose_D3", this.aircraft().hierMesh().isChunkVisible("Nose_D3"));
            this.aircraft().hierMesh().chunkVisible("Turret1B_D0", true);
            this.aircraft().hierMesh().chunkVisible("BlisterTop_D0", true);
            super.doFocusLeave();
        }
    }

    public CockpitJU_88G6_Radar() {
        super("3DO/Cockpit/Ju-88G-6/G6Radarhier.him", "he111");
        this.bNeedSetUp = true;
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictFlap = 0.0F;
        this.pictGear = 0.0F;
        this.pictManf1 = 1.0F;
        this.pictManf2 = 1.0F;
        this.prevFuel = 0.0F;
        this.cockpitNightMats = (new String[] { "88a4_I_Set1", "88a4_I_Set2", "88a4_I_Set3", "88a4_I_Set4", "88a4_I_Set5", "88a4_I_Set6", "88a4_I_SetEng", "88a4_SlidingGlass", "88gardinen", "88InstrGenrl", "Peil1", "skala", "Pedal", "Peil2", "PeilNeedle", "radar_glass" });
        this.setNightMats(false);
        this.setNew.dimPosition = this.setOld.dimPosition = 0.0F;
        this.cockpitDimControl = !this.cockpitDimControl;
        this.interpPut(new Interpolater(), null, Time.current(), null);
        if (this.acoustics != null) {
            this.acoustics.globFX = new ReverbFXRoom(0.45F);
        }
        AircraftLH.printCompassHeading = true;
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        this.resetYPRmodifier();
        Cockpit.xyz[2] = 0.06815F * this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f);
        this.mesh.chunkSetAngles("Z_Trim1", this.cvt(this.fm.CT.getTrimElevatorControl(), -0.5F, 0.5F, -750F, 750F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zColumn1", 7F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zColumn2", 52.2F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = -0.1F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("zPedalL", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.1F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("zPedalR", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("zTurret1A", 0.0F, this.fm.turret[0].tu[0], 0.0F);
        this.mesh.chunkSetAngles("zTurret1B", 0.0F, this.fm.turret[0].tu[1], 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.pictFlap = (0.85F * this.pictFlap) + (0.00948F * this.fm.CT.FlapsControl);
        this.mesh.chunkSetLocate("zFlaps1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = this.pictGear = (0.85F * this.pictGear) + (0.007095F * this.fm.CT.GearControl);
        this.mesh.chunkSetLocate("zGear1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -0.1134F * this.setNew.prop1;
        this.mesh.chunkSetLocate("zPitch1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -0.1134F * this.setNew.prop2;
        this.mesh.chunkSetLocate("zPitch2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -0.1031F * this.setNew.throttle1;
        this.mesh.chunkSetLocate("zThrottle1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -0.1031F * this.setNew.throttle2;
        this.mesh.chunkSetLocate("zThrottle2", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("TempMeter", this.floatindex(this.cvt(Atmosphere.temperature((float) this.fm.Loc.z), 213.09F, 293.09F, 0.0F, 8F), frAirTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Radiator_Sw1", this.cvt(this.fm.EI.engines[0].getControlRadiator(), 0.0F, 1.0F, 0.0F, -120F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Radiator_Sw2", this.cvt(this.fm.EI.engines[1].getControlRadiator(), 0.0F, 1.0F, 0.0F, -120F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_MagnetoSw1", this.cvt(this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 3F, 100F, 0.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_MagnetoSw2", this.cvt(this.fm.EI.engines[1].getControlMagnetos(), 0.0F, 3F, 100F, 0.0F), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = -(10.27825F * (float) Math.tan(Math.toRadians(this.setNew.alpha)));
        if (Cockpit.xyz[2] < -2.2699F) {
            Cockpit.xyz[2] = -2.2699F;
        }
        this.mesh.chunkSetAngles("zHour1", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zMinute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zBall", 0.0F, this.cvt(this.getBall(4D), -4F, 4F, -9F, 9F), 0.0F);
        this.mesh.chunkSetAngles("zAlt1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 6000F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSpeed", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 50F, 750F, 0.0F, 14F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Climb1", this.cvt(this.setNew.vspeed, -15F, 15F, -151F, 151F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zWaterTemp1", this.cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 120F, 0.0F, 64F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zWaterTemp2", this.cvt(this.fm.EI.engines[1].tWaterOut, 0.0F, 120F, 0.0F, 64F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPress1", this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness()), 0.0F, 7.47F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPress2", this.cvt(1.0F + (0.05F * this.fm.EI.engines[1].tOilOut * this.fm.EI.engines[1].getReadyness()), 0.0F, 7.47F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPress1", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.32F, 0.0F, 0.5F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPress2", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.32F, 0.0F, 0.5F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM1", this.cvt(this.fm.EI.engines[0].getRPM(), 600F, 3600F, 0.0F, 331F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM2", this.cvt(this.fm.EI.engines[1].getRPM(), 600F, 3600F, 0.0F, 331F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA1", this.pictManf1 = (0.9F * this.pictManf1) + (0.1F * this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 325.5F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA2", this.pictManf2 = (0.9F * this.pictManf2) + (0.1F * this.cvt(this.fm.EI.engines[1].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 325.5F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuel1", this.cvt(this.fm.M.fuel, 0.0F, 1008F, 0.0F, 77F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuel2", this.cvt(this.fm.M.fuel, 0.0F, 1008F, 0.0F, 77F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPress", this.cvt(this.setNew.cons, 100F, 500F, 0.0F, 240F), 0.0F, 0.0F);
        if (this.useRealisticNavigationInstruments()) {
            this.mesh.chunkSetAngles("Z_Compass3", this.setNew.azimuth.getDeg(f) - this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass4", -this.setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass5", this.setNew.radioCompassAzimuth.getDeg(f * 0.02F) + 90F, 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass7", this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass8", this.setNew.waypointAzimuth.getDeg(f * 0.1F) + 90F, 0.0F, 0.0F);
        } else {
            this.mesh.chunkSetAngles("Z_Compass3", this.setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass4", -this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass5", 0.0F, 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass7", this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass8", this.setNew.waypointAzimuth.getDeg(f * 0.1F) + 90F, 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("zHORIZ1", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.045F, -0.045F);
        this.mesh.chunkSetLocate("zHORIZ2", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = 0.02125F * this.fm.CT.getTrimElevatorControl();
        this.mesh.chunkSetLocate("zTRIM1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.02125F * this.fm.CT.getTrimAileronControl();
        this.mesh.chunkSetLocate("zTRIM2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.02125F * this.fm.CT.getTrimRudderControl();
        this.mesh.chunkSetLocate("zTRIM3", Cockpit.xyz, Cockpit.ypr);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.cvt(this.w.z, -0.23562F, 0.23562F, 25F, -25F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank2", 0.0F, this.cvt(this.getBall(4D), -4F, 4F, -9F, 9F), 0.0F);
        this.mesh.chunkVisible("XLampGearUpL", (this.fm.CT.getGear() < 0.01F) || !this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearDownL", (this.fm.CT.getGear() > 0.99F) && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearUpR", (this.fm.CT.getGear() < 0.01F) || !this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearDownR", (this.fm.CT.getGear() > 0.99F) && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearUpC", this.fm.CT.getGear() < 0.01F);
        this.mesh.chunkVisible("XLampGearDownC", this.fm.CT.getGear() > 0.99F);
        this.mesh.chunkVisible("XLampFlap1", this.fm.CT.getFlap() < 0.1F);
        this.mesh.chunkVisible("XLampFlap2", (this.fm.CT.getFlap() > 0.1F) && (this.fm.CT.getFlap() < 0.5F));
        this.mesh.chunkVisible("XLampFlap3", this.fm.CT.getFlap() > 0.5F);
        this.mesh.chunkSetAngles("zAH1", 0.0F, this.cvt(this.setNew.beaconDirection, -45F, 45F, 14F, -14F), 0.0F);
        this.mesh.chunkSetAngles("zAH2", this.cvt(this.setNew.beaconRange, 0.0F, 1.0F, 26.5F, -26.5F), 0.0F, 0.0F);
        this.mesh.chunkVisible("AFN1_RED", this.isOnBlindLandingMarker());
        // +++ RadarLiSN2 +++
        this.cockpitRadarLiSN2.updateRadar();
        // --- RadarLiSN2 ---
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2 = new RadarLiSN2Equipment(this, 28, 70F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
    // --- RadarLiSN2 ---

    protected float waypointAzimuth() {
        return super.waypointAzimuthInvertMinus(20F);
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("XGlassDamage1", true);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            this.mesh.chunkVisible("XGlassDamage5", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
            this.mesh.chunkVisible("XGlassDamage3", true);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("XGlassDamage5", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            this.mesh.chunkVisible("XGlassDamage2", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            this.mesh.chunkVisible("XGlassDamage6", true);
        }
        this.retoggleLight();
    }

    public void toggleLight() {
        this.cockpitLightControl = !this.cockpitLightControl;
        if (this.cockpitLightControl) {
            this.setNightMats(true);
        } else {
            this.setNightMats(false);
        }
    }

    private void retoggleLight() {
        if (this.cockpitLightControl) {
            this.setNightMats(false);
            this.setNightMats(true);
        } else {
            this.setNightMats(true);
            this.setNightMats(false);
        }
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
    }

    public boolean isEnableFocusing() {
        if (this.aircraft().thisWeaponsName.startsWith("SN")) {
            return super.isEnableFocusing();
        }
        HotKeyCmd.exec("aircraftView", "cockpitSwitch2"); // Switch to Gunner Cockpit if not Night Fighter
        return false;
    }

    private boolean            bNeedSetUp;
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
    private float              pictFlap;
    private float              pictGear;
    private float              pictManf1;
    private float              pictManf2;
    private float              prevFuel;
    private static final float speedometerScale[] = { 0.0F, 16F, 35.5F, 60.5F, 88F, 112.5F, 136F, 159.5F, 186.5F, 211.5F, 240F, 268F, 295.5F, 321F, 347F };
    private static final float frAirTempScale[]   = { 76.5F, 68F, 57F, 44.5F, 29.5F, 14.5F, 1.5F, -10F, -19F };
}
