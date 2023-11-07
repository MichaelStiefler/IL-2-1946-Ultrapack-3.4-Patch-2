package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitME_P1101 extends CockpitPilot {
    class Interpolater extends InterpolateRef {
        Interpolater() {
        }

        public boolean tick() {
            if (CockpitME_P1101.this.fm != null) {
                CockpitME_P1101.this.setTmp = CockpitME_P1101.this.setOld;
                CockpitME_P1101.this.setOld = CockpitME_P1101.this.setNew;
                CockpitME_P1101.this.setNew = CockpitME_P1101.this.setTmp;
                CockpitME_P1101.this.setNew.altimeter = CockpitME_P1101.this.fm.getAltitude();
                if (CockpitME_P1101.this.cockpitDimControl) {
                    if (CockpitME_P1101.this.setNew.dimPosition > 0.0F) {
                        CockpitME_P1101.this.setNew.dimPosition = (CockpitME_P1101.this.setOld.dimPosition - 0.05F);
                    }
                } else if (CockpitME_P1101.this.setNew.dimPosition < 1.0F) {
                    CockpitME_P1101.this.setNew.dimPosition = (CockpitME_P1101.this.setOld.dimPosition + 0.05F);
                }
                CockpitME_P1101.this.setNew.throttle = ((0.92F * CockpitME_P1101.this.setOld.throttle) + (0.08F * CockpitME_P1101.this.fm.CT.PowerControl));

                CockpitME_P1101.this.setNew.vspeed = (((499.0F * CockpitME_P1101.this.setOld.vspeed) + CockpitME_P1101.this.fm.getVertSpeed()) / 500.0F);

                CockpitME_P1101.this.setNew.azimuth = CockpitME_P1101.this.fm.Or.getYaw();
                if ((CockpitME_P1101.this.setOld.azimuth > 270.0F) && (CockpitME_P1101.this.setNew.azimuth < 90.0F)) {
                    CockpitME_P1101.this.setOld.azimuth -= 360.0F;
                }
                if ((CockpitME_P1101.this.setOld.azimuth < 90.0F) && (CockpitME_P1101.this.setNew.azimuth > 270.0F)) {
                    CockpitME_P1101.this.setOld.azimuth += 360.0F;
                }
                CockpitME_P1101.this.setNew.waypointAzimuth = (((10.0F * CockpitME_P1101.this.setOld.waypointAzimuth) + (CockpitME_P1101.this.waypointAzimuth() - CockpitME_P1101.this.setOld.azimuth) + World.Rnd().nextFloat(-30.0F, 30.0F)) / 11.0F);
                float f1 = ((ME_P1101) CockpitME_P1101.this.aircraft()).k14Distance;
                CockpitME_P1101.this.setNew.k14w = ((5.0F * CockpitME_P1101.k14TargetWingspanScale[((ME_P1101) CockpitME_P1101.this.aircraft()).k14WingspanType]) / f1);
                CockpitME_P1101.this.setNew.k14w = ((0.9F * CockpitME_P1101.this.setOld.k14w) + (0.1F * CockpitME_P1101.this.setNew.k14w));
                CockpitME_P1101.this.setNew.k14wingspan = ((0.9F * CockpitME_P1101.this.setOld.k14wingspan) + (0.1F * CockpitME_P1101.k14TargetMarkScale[((ME_P1101) CockpitME_P1101.this.aircraft()).k14WingspanType]));
                CockpitME_P1101.this.setNew.k14mode = ((0.8F * CockpitME_P1101.this.setOld.k14mode) + (0.2F * ((ME_P1101) CockpitME_P1101.this.aircraft()).k14Mode));
                Vector3d vector3d = CockpitME_P1101.this.aircraft().FM.getW();
                double d = 0.00125D * f1;
                float f2 = (float) Math.toDegrees(d * vector3d.z);
                float f3 = -(float) Math.toDegrees(d * vector3d.y);
                float f4 = CockpitME_P1101.this.floatindex((f1 - 200.0F) * 0.04F, CockpitME_P1101.k14BulletDrop) - CockpitME_P1101.k14BulletDrop[0];
                f3 += (float) Math.toDegrees(Math.atan(f4 / f1));
                CockpitME_P1101.this.setNew.k14x = ((0.92F * CockpitME_P1101.this.setOld.k14x) + (0.08F * f2));
                CockpitME_P1101.this.setNew.k14y = ((0.92F * CockpitME_P1101.this.setOld.k14y) + (0.08F * f3));
                if (CockpitME_P1101.this.setNew.k14x > 7.0F) {
                    CockpitME_P1101.this.setNew.k14x = 7.0F;
                }
                if (CockpitME_P1101.this.setNew.k14x < -7.0F) {
                    CockpitME_P1101.this.setNew.k14x = -7.0F;
                }
                if (CockpitME_P1101.this.setNew.k14y > 7.0F) {
                    CockpitME_P1101.this.setNew.k14y = 7.0F;
                }
                if (CockpitME_P1101.this.setNew.k14y < -7.0F) {
                    CockpitME_P1101.this.setNew.k14y = -7.0F;
                }
            }
            return true;
        }
    }

    private class Variables {
        float altimeter;
        float throttle;
        float dimPosition;
        float azimuth;
        float waypointAzimuth;
        float vspeed;
        float k14wingspan;
        float k14mode;
        float k14x;
        float k14y;
        float k14w;

        private Variables() {
        }
    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        } else {
            waypoint.getP(Cockpit.P1);
            Cockpit.V.sub(Cockpit.P1, this.fm.Loc);
            return (float) (57.295779513082323D * Math.atan2(Cockpit.V.y, Cockpit.V.x));
        }
    }

    public CockpitME_P1101() {
        super("3DO/Cockpit/Me-P1101/hier.him", "bf109");
        this.bNeedSetUp = true;
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictGear = 0.0F;
        this.pictFlap = 0.0F;
        this.pictAftb = 0.0F;
        this.setNew.dimPosition = 0.0F;
        this.cockpitNightMats = (new String[] { "D_gauges1_TR", "D_gauges2_TR", "D_gauges3_TR", "gauges1_TR", "gauges2_TR", "gauges3_TR", "gauges4_TR", "gauges5_TR", "gauges6_TR", "gauges7_TR", "gauges8_TR", "gauges9_TR" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
        if (this.aircraft().thisWeaponsName.endsWith("NF")) {
            this.hasRadar = true;
            this.mesh.chunkVisible("FahrWerk", false);
        } else {
            this.mesh.hideSubTrees("RadarHook");
        }
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        int i = ((ME_P1101) this.aircraft()).k14Mode;
        boolean flag = i < 2;
        this.mesh.chunkVisible("Z_Z_RETICLE", flag);
        flag = i > 0;
        this.mesh.chunkVisible("Z_Z_RETICLE1", flag);
        this.mesh.chunkSetAngles("Z_Z_RETICLE1", 0.0F, this.setNew.k14x, this.setNew.k14y);
        this.resetYPRmodifier();
        Cockpit.xyz[0] = this.setNew.k14w;
        for (int j = 1; j < 7; j++) {
            this.mesh.chunkVisible("Z_Z_AIMMARK" + j, flag);
            this.mesh.chunkSetLocate("Z_Z_AIMMARK" + j, Cockpit.xyz, Cockpit.ypr);
        }

        if (this.gun[0] == null) {
            this.gun[0] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON01");
            this.gun[1] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON02");
            this.gun[2] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON03");
            this.gun[3] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON04");
        }
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -0.07115F);
        this.mesh.chunkSetLocate("EZ42Dimm", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("EZ42Filter", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -85F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42FLever", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Range", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Size", 0.0F, 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.ypr[0] = this.interp(this.setNew.throttle, this.setOld.throttle, f) * 59.09091F;
        Cockpit.xyz[2] = this.cvt(Cockpit.ypr[0], 7.5F, 12.5F, 0.0F, -0.0054F);
        this.mesh.chunkSetLocate("ThrottleQuad", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("RPedalBase", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("RPedalStrut", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("RPedal", 0.0F, 0.0F, (-this.fm.CT.getRudder() * 15F) - (this.fm.CT.getBrake() * 15F));
        this.mesh.chunkSetAngles("LPedalBase", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("LPedalStrut", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("LPedal", 0.0F, 0.0F, (this.fm.CT.getRudder() * 15F) - (this.fm.CT.getBrake() * 15F));
        this.mesh.chunkSetAngles("Stick", 0.0F, (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 20F, (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 20F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.fm.CT.WeaponControl[1] ? -0.004F : 0.0F;
        this.mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
        this.pictGear = (0.91F * this.pictGear) + (0.09F * this.fm.CT.GearControl);
        this.mesh.chunkSetAngles("ZGear", this.cvt(this.pictGear, 0.0F, 1.0F, -26.5F, 26.5F), 0.0F, 0.0F);
        this.pictFlap = (0.91F * this.pictFlap) + (0.09F * this.fm.CT.FlapsControl);
        this.mesh.chunkSetAngles("ZFlaps", this.cvt(this.pictFlap, 0.0F, 1.0F, -26.5F, 26.5F), 0.0F, 0.0F);
        this.pictAftb = (0.91F * this.pictAftb) + (0.09F * (this.fm.CT.PowerControl <= 1.0F ? 0.0F : 1.0F));
        this.mesh.chunkSetAngles("ZLever", this.cvt(this.pictAftb, 0.0F, 1.0F, -29.5F, 29.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_X4Stick", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleVSI", this.setNew.vspeed < 0.0F ? -this.floatindex(this.cvt(-this.setNew.vspeed, 0.0F, 30F, 0.0F, 6F), CockpitME_P1101.vsiNeedleScale) : this.floatindex(this.cvt(this.setNew.vspeed, 0.0F, 30F, 0.0F, 6F), CockpitME_P1101.vsiNeedleScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleKMH", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), CockpitME_P1101.speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleRPM", 55F + this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), CockpitME_P1101.rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleALT", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleALTKm", this.cvt(this.setNew.altimeter, 0.0F, 10000F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RepeaterOuter", this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RepeaterPlane", -this.interp(this.setNew.waypointAzimuth, this.setOld.waypointAzimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleGasPress", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.6F * this.fm.EI.engines[0].getPowerOutput(), 0.0F, 1.0F, 0.0F, 270F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOilPress", this.cvt(1.0F + (0.005F * this.fm.EI.engines[0].tOilOut), 0.0F, 10F, 0.0F, 270F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOxPress", 135F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleGasTemp", this.cvt(this.fm.EI.engines[0].tWaterOut, 300F, 1000F, 0.0F, 51F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleFuel", this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 1200F, 0.0F, 48F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleNahe1", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleNahe2", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleTrimmung", this.fm.CT.getTrimElevatorControl() * 25F * 2.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAHCyl", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAHBar", 0.0F, 0.0F, this.cvt(this.fm.Or.getTangage(), -45F, 45F, -14F, 14F));
        this.mesh.chunkSetAngles("NeedleAHTurn", this.cvt(this.getBall(6D), -6F, 6F, -12.5F, 12.5F), 0.0F, 0.0F);
        float f1;
        if (this.aircraft().isFMTrackMirror()) {
            f1 = this.aircraft().fmTrack().getCockpitAzimuthSpeed();
        } else {
            f1 = this.cvt((this.setNew.azimuth - this.setOld.azimuth) / Time.tickLenFs(), -6F, 6F, -26.5F, 26.5F);
            if (this.aircraft().fmTrack() != null) {
                this.aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
            }
        }
        this.mesh.chunkSetAngles("NeedleAHBank", -f1, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAirPress", 135F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zClock1a", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zClock1b", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zClock1c", this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.cvt(this.gun[0].countBullets(), 0.0F, 50F, -0.039F, 0.0F);
        this.mesh.chunkSetLocate("RC_MG17_L", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = this.cvt(this.gun[2].countBullets(), 0.0F, 50F, -0.039F, 0.0F);
        this.mesh.chunkSetLocate("RC_MG17_R", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = this.cvt(this.gun[1].countBullets(), 0.0F, 50F, -0.039F, 0.0F);
        this.mesh.chunkSetLocate("RC_MG151_L", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = this.cvt(this.gun[3].countBullets(), 0.0F, 50F, -0.039F, 0.0F);
        this.mesh.chunkSetLocate("RC_MG151_R", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkVisible("XLampFuelLow", this.fm.M.fuel < 43.2F);
        this.mesh.chunkVisible("XLampSpeedWorn", this.fm.getSpeedKMH() < 200F);
        this.mesh.chunkVisible("XLampGearL_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearL_2", (this.fm.CT.getGear() > 0.95F) && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearR_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearR_2", (this.fm.CT.getGear() > 0.95F) && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearC_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearC_2", (this.fm.CT.getGear() > 0.95F) && this.fm.Gears.cgear);
        this.mesh.chunkVisible("XLampMissileL_1", this.aircraft().getGunByHookName("_ExternalRock25").haveBullets());
        this.mesh.chunkVisible("XLampMissileL_2", this.aircraft().getGunByHookName("_ExternalRock27").haveBullets());
        this.mesh.chunkVisible("XLampMissileR_1", this.aircraft().getGunByHookName("_ExternalRock28").haveBullets());
        this.mesh.chunkVisible("XLampMissileR_2", this.aircraft().getGunByHookName("_ExternalRock26").haveBullets());
        this.mesh.chunkVisible("XLampMissile", false);
        // +++ RadarLiSN2 +++
        if (this.hasRadar) {
            this.cockpitRadarLiSN2.updateRadar();
            // --- RadarLiSN2 ---
        }
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
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

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("XGlassDamage2", true);
            this.mesh.chunkVisible("EZ42", false);
            this.mesh.chunkVisible("EZ42Dimm", false);
            this.mesh.chunkVisible("EZ42Filter", false);
            this.mesh.chunkVisible("EZ42FLever", false);
            this.mesh.chunkVisible("EZ42Range", false);
            this.mesh.chunkVisible("EZ42Size", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("DEZ42", true);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
        }
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("IPCGages", false);
            this.mesh.chunkVisible("IPCGages_D1", true);
            this.mesh.chunkVisible("NeedleALT", false);
            this.mesh.chunkVisible("NeedleALTKm", false);
            this.mesh.chunkVisible("NeedleAHCyl", false);
            this.mesh.chunkVisible("NeedleAHBank", false);
            this.mesh.chunkVisible("NeedleAHBar", false);
            this.mesh.chunkVisible("NeedleAHTurn", false);
            this.mesh.chunkVisible("zClock1a", false);
            this.mesh.chunkVisible("zClock1b", false);
            this.mesh.chunkVisible("zClock1c", false);
            this.mesh.chunkVisible("RepeaterOuter", false);
            this.mesh.chunkVisible("RepeaterInner", false);
            this.mesh.chunkVisible("RepeaterPlane", false);
            this.mesh.chunkVisible("NeedleNahe1", false);
            this.mesh.chunkVisible("NeedleNahe2", false);
            this.mesh.chunkVisible("NeedleFuel", false);
            this.mesh.chunkVisible("NeedleGasPress", false);
            this.mesh.chunkVisible("NeedleGasTemp", false);
            this.mesh.chunkVisible("NeedleOilPress", false);
            this.mesh.chunkVisible("NeedleRPM", false);
            this.mesh.chunkVisible("NeedleKMH", false);
            this.mesh.chunkVisible("NeedleVSI", false);
            this.mesh.chunkVisible("XHullDamage3", true);
            this.mesh.chunkVisible("XGlassDamage2", true);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("XHullDamage2", true);
            this.mesh.chunkVisible("XHullDamage3", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("XHullDamage1", true);
            this.mesh.chunkVisible("XHullDamage3", true);
        }
        this.retoggleLight();
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2        = new RadarLiSN2Equipment(this, 30, 155F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
    // --- RadarLiSN2 ---

    private boolean             hasRadar;
    private boolean             bNeedSetUp;
    private Gun                 gun[]                    = { null, null, null, null };
    private Variables           setOld;
    private Variables           setNew;
    private Variables           setTmp;
    private float               pictAiler;
    private float               pictElev;
    private float               pictGear;
    private float               pictFlap;
    private float               pictAftb;
    private static final float  speedometerScale[]       = { 0.0F, 18.5F, 67F, 117F, 164F, 215F, 267F, 320F, 379F, 427F, 428F };
    private static final float  rpmScale[]               = { 0.0F, 0.0F, 0.0F, 16.5F, 34.5F, 55F, 77.5F, 104F, 133.5F, 162.5F, 192F, 224F, 254F, 255.5F, 260F };
    private static final float  vsiNeedleScale[]         = { 0.0F, 48F, 82F, 96.5F, 111F, 120.5F, 130F, 130F };
    private static final float  k14TargetMarkScale[]     = { -0F, -4.5F, -27.5F, -42.5F, -56.5F, -61.5F, -70F, -95F, -102.5F, -106F };
    private static final float  k14TargetWingspanScale[] = { 9.9F, 10.52F, 13.8F, 16.34F, 19F, 20F, 22F, 29.25F, 30F, 32.85F };
    private static final float  k14BulletDrop[]          = { 5.812F, 6.168F, 6.508F, 6.978F, 7.24F, 7.576F, 7.849F, 8.108F, 8.473F, 8.699F, 8.911F, 9.111F, 9.384F, 9.554F, 9.787F, 9.928F, 9.992F, 10.282F, 10.381F, 10.513F, 10.603F, 10.704F, 10.739F, 10.782F, 10.789F };

    static {
        Property.set(CockpitME_P1101.class, "normZN", 0.8F);
    }
}
