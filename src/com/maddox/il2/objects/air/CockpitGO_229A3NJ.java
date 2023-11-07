package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.rts.Time;

public class CockpitGO_229A3NJ extends CockpitPilot {
    private class Variables {

        float altimeter;
        float throttlel;
        float throttler;
        float azimuth;
        float waypointAzimuth;
        float vspeed;
        float dimPosition;
        float k14wingspan;
        float k14mode;
        float k14x;
        float k14y;
        float k14w;
        float k14Distance;

    }

    class Interpolater extends InterpolateRef {

        public boolean tick() {
            CockpitGO_229A3NJ.this.setTmp = CockpitGO_229A3NJ.this.setOld;
            CockpitGO_229A3NJ.this.setOld = CockpitGO_229A3NJ.this.setNew;
            CockpitGO_229A3NJ.this.setNew = CockpitGO_229A3NJ.this.setTmp;
            CockpitGO_229A3NJ.this.setNew.altimeter = CockpitGO_229A3NJ.this.fm.getAltitude();
            CockpitGO_229A3NJ.this.setNew.throttlel = ((10F * CockpitGO_229A3NJ.this.setOld.throttlel) + CockpitGO_229A3NJ.this.fm.EI.engines[0].getControlThrottle()) / 11F;
            CockpitGO_229A3NJ.this.setNew.throttler = ((10F * CockpitGO_229A3NJ.this.setOld.throttler) + CockpitGO_229A3NJ.this.fm.EI.engines[1].getControlThrottle()) / 11F;
            CockpitGO_229A3NJ.this.setNew.azimuth = CockpitGO_229A3NJ.this.fm.Or.getYaw();
            if ((CockpitGO_229A3NJ.this.setOld.azimuth > 270F) && (CockpitGO_229A3NJ.this.setNew.azimuth < 90F)) {
                CockpitGO_229A3NJ.this.setOld.azimuth -= 360F;
            }
            if ((CockpitGO_229A3NJ.this.setOld.azimuth < 90F) && (CockpitGO_229A3NJ.this.setNew.azimuth > 270F)) {
                CockpitGO_229A3NJ.this.setOld.azimuth += 360F;
            }
            CockpitGO_229A3NJ.this.setNew.waypointAzimuth = ((10F * CockpitGO_229A3NJ.this.setOld.waypointAzimuth) + (CockpitGO_229A3NJ.this.waypointAzimuth() - CockpitGO_229A3NJ.this.setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
            CockpitGO_229A3NJ.this.setNew.vspeed = ((299F * CockpitGO_229A3NJ.this.setOld.vspeed) + CockpitGO_229A3NJ.this.fm.getVertSpeed()) / 300F;
            if (CockpitGO_229A3NJ.this.cockpitDimControl) {
                if (CockpitGO_229A3NJ.this.setNew.dimPosition > 0.0F) {
                    CockpitGO_229A3NJ.this.setNew.dimPosition = CockpitGO_229A3NJ.this.setOld.dimPosition - 0.05F;
                }
            } else if (CockpitGO_229A3NJ.this.setNew.dimPosition < 1.0F) {
                CockpitGO_229A3NJ.this.setNew.dimPosition = CockpitGO_229A3NJ.this.setOld.dimPosition + 0.05F;
            }
            float f = CockpitGO_229A3NJ.this.setNew.k14Distance = ((GO_229A3NJ) CockpitGO_229A3NJ.this.aircraft()).k14Distance;
            CockpitGO_229A3NJ.this.setNew.k14w = (5F * CockpitGO_229A3NJ.k14TargetWingspanScale[((GO_229A3NJ) CockpitGO_229A3NJ.this.aircraft()).k14WingspanType]) / f;
            CockpitGO_229A3NJ.this.setNew.k14w = (0.9F * CockpitGO_229A3NJ.this.setOld.k14w) + (0.1F * CockpitGO_229A3NJ.this.setNew.k14w);
            CockpitGO_229A3NJ.this.setNew.k14wingspan = (0.9F * CockpitGO_229A3NJ.this.setOld.k14wingspan) + (0.1F * CockpitGO_229A3NJ.k14TargetMarkScale[((GO_229A3NJ) CockpitGO_229A3NJ.this.aircraft()).k14WingspanType]);
            CockpitGO_229A3NJ.this.setNew.k14mode = (0.8F * CockpitGO_229A3NJ.this.setOld.k14mode) + (0.2F * ((GO_229A3NJ) CockpitGO_229A3NJ.this.aircraft()).k14Mode);
            Vector3d vector3d = CockpitGO_229A3NJ.this.aircraft().FM.getW();
            double d = 0.00125D * f;
            float f1 = (float) Math.toDegrees(d * vector3d.z);
            float f2 = -(float) Math.toDegrees(d * vector3d.y);
            float f3 = CockpitGO_229A3NJ.this.floatindex((f - 200F) * 0.04F, CockpitGO_229A3NJ.k14BulletDrop) - CockpitGO_229A3NJ.k14BulletDrop[0];
            f2 += (float) Math.toDegrees(Math.atan(f3 / f));
            CockpitGO_229A3NJ.this.setNew.k14x = (0.92F * CockpitGO_229A3NJ.this.setOld.k14x) + (0.08F * f1);
            CockpitGO_229A3NJ.this.setNew.k14y = (0.92F * CockpitGO_229A3NJ.this.setOld.k14y) + (0.08F * f2);
            if (CockpitGO_229A3NJ.this.setNew.k14x > 7F) {
                CockpitGO_229A3NJ.this.setNew.k14x = 7F;
            }
            if (CockpitGO_229A3NJ.this.setNew.k14x < -7F) {
                CockpitGO_229A3NJ.this.setNew.k14x = -7F;
            }
            if (CockpitGO_229A3NJ.this.setNew.k14y > 7F) {
                CockpitGO_229A3NJ.this.setNew.k14y = 7F;
            }
            if (CockpitGO_229A3NJ.this.setNew.k14y < -7F) {
                CockpitGO_229A3NJ.this.setNew.k14y = -7F;
            }
            return true;
        }
    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        } else {
            waypoint.getP(this.tmpP);
            this.tmpV.sub(this.tmpP, this.fm.Loc);
            return (float) (Math.toDegrees(Math.atan2(this.tmpV.y, this.tmpV.x)));
        }
    }

    public CockpitGO_229A3NJ() {
        super("3DO/Cockpit/Go-229A3NJ/hier.him", "he111");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.bNeedSetUp = true;
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.cockpitNightMats = (new String[] { "2petitsb_d1", "2petitsb", "aiguill1", "badinetm_d1", "badinetm", "baguecom", "brasdele", "comptemu_d1", "comptemu", "petitfla_d1", "petitfla", "turnbank" });
        this.setNightMats(false);
        this.setNew.dimPosition = 1.0F;
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        int i = ((GO_229A3NJ) this.aircraft()).k14Mode;
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

        if (this.fm.isTick(44, 0)) {
            this.mesh.chunkVisible("Z_GearLGreen1", (this.fm.CT.getGear() == 1.0F) && this.fm.Gears.lgear);
            this.mesh.chunkVisible("Z_GearRGreen1", (this.fm.CT.getGear() == 1.0F) && this.fm.Gears.rgear);
            this.mesh.chunkVisible("Z_GearCGreen1", this.fm.CT.getGear() == 1.0F);
            this.mesh.chunkVisible("Z_GearLRed1", (this.fm.CT.getGear() == 0.0F) || this.fm.Gears.isAnyDamaged());
            this.mesh.chunkVisible("Z_GearCRed1", this.fm.CT.getGear() == 0.0F);
            this.mesh.chunkVisible("Z_FuelLampL", this.fm.M.fuel < 300F);
            this.mesh.chunkVisible("Z_FuelLampR", this.fm.M.fuel < 300F);
            this.mesh.chunkVisible("Z_Fire", false);
        }
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -0.07115F);
        this.mesh.chunkSetLocate("EZ42Dimm", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("EZ42Filter", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -85F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42FLever", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Range", this.cvt(this.interp((this.setNew.k14Distance - 100F) / 800F, (this.setOld.k14Distance - 100F) / 800F, f), 0.0F, 1.0F, 3.5F, -90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Size", this.cvt(this.interp(-this.setNew.k14wingspan / 151.252F, -this.setOld.k14wingspan / 151.252F, f), 0.0F, 1.0F, 36.5F, -81.619F), 0.0F, 0.0F);
        this.resetYPRmodifier();
        this.mesh.chunkVisible("Z_FlapEin", this.fm.CT.getFlap() < 0.05F);
        this.mesh.chunkVisible("Z_FlapStart", (this.fm.CT.getFlap() > 0.28F) && (this.fm.CT.getFlap() < 0.38F));
        this.mesh.chunkVisible("Z_FlapAus", this.fm.CT.getFlap() > 0.95F);
        this.mesh.chunkSetAngles("zColumn1", 0.0F, 10F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 10F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)));
        this.mesh.chunkSetAngles("zColumn2", 0.0F, -10F * this.pictAiler, 0.0F);
        this.mesh.chunkSetAngles("Z_PedalStrut", 10F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", -10F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal", -10F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFlaps1", 0.0F, 0.0F, -32F + (42.5F * this.fm.CT.FlapsControl));
        this.mesh.chunkSetAngles("zThrottle1", 0.0F, 0.0F, 20.5F - (32F * this.interp(this.setNew.throttlel, this.setOld.throttlel, f)));
        this.mesh.chunkSetAngles("zThrottle2", 0.0F, 0.0F, 20.5F - (32F * this.interp(this.setNew.throttler, this.setOld.throttler, f)));
        this.mesh.chunkSetAngles("zGear1", 0.0F, 0.0F, -35.5F + (35.5F * this.fm.CT.GearControl));
        this.mesh.chunkSetAngles("zAirBrake1", 0.0F, 0.0F, 32F * this.fm.CT.AirBrakeControl);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.fm.Or.getTangage(), 0.0F, this.fm.Or.getKren());
        this.mesh.chunkSetAngles("Z_TurnBank2", 0.0F, 0.0F, -this.cvt(this.getBall(6D), -6F, 6F, -7.5F, 7.5F));
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank3", 0.0F, 0.0F, this.cvt(this.w.z, -0.23562F, 0.23562F, -50F, 50F));
        this.mesh.chunkSetAngles("zSpeed1", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 100F, 400F, 2.0F, 8F), speedometerIndScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSpeed2", this.floatindex(this.cvt(this.fm.getSpeedKMH(), 100F, 1000F, 1.0F, 10F), speedometerTruScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 16000F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zCompass", this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRepeater", -this.interp(this.setNew.waypointAzimuth, this.setOld.waypointAzimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM1", this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM2", this.floatindex(this.cvt(this.fm.EI.engines[1].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuel1", this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 1000F, 0.0F, 5F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuel2", this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 1000F, 0.0F, 5F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zExtT", this.cvt(Atmosphere.temperature((float) this.fm.Loc.z), 273.09F, 373.09F, -26F, 144.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasTempL", this.cvt(this.fm.EI.engines[0].tWaterOut, 300F, 1000F, 0.0F, 96F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasTempR", this.cvt(this.fm.EI.engines[1].tWaterOut, 300F, 1000F, 0.0F, 96F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPressureL", this.cvt(1.0F + (0.005F * this.fm.EI.engines[0].tOilOut), 0.0F, 10F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPressureR", this.cvt(1.0F + (0.005F * this.fm.EI.engines[1].tOilOut), 0.0F, 10F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelPressure", this.cvt(this.fm.M.fuel > 1.0F ? 80F * this.fm.EI.engines[0].getPowerOutput() * this.fm.EI.engines[0].getReadyness() : 0.0F, 0.0F, 160F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Climb1", this.floatindex(this.cvt(this.setNew.vspeed, -20F, 50F, 0.0F, 14F), variometerScale), 0.0F, 0.0F);

        // +++ RadarLiSN2 +++
        this.cockpitRadarLiSN2.updateRadar();
        // --- RadarLiSN2 ---
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2 = new RadarLiSN2Equipment(this, 27, 70F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
    // --- RadarLiSN2 ---

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
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
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
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
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("RPMR", false);
            this.mesh.chunkVisible("RPMR_D1", true);
            this.mesh.chunkVisible("Z_RPMR", false);
            this.mesh.chunkVisible("FuelPressR", false);
            this.mesh.chunkVisible("FuelPressR_D1", true);
            this.mesh.chunkVisible("Z_FuelPressR", false);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            this.mesh.chunkVisible("HullDamage3", true);
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.chunkVisible("GasPressureR", false);
            this.mesh.chunkVisible("GasPressureR_D1", true);
            this.mesh.chunkVisible("Z_GasPressureR", false);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("XGlassDamage2", true);
            this.mesh.chunkVisible("Climb", false);
            this.mesh.chunkVisible("Climb_D1", true);
            this.mesh.chunkVisible("Z_Climb1", false);
            this.mesh.chunkVisible("FuelPressR", false);
            this.mesh.chunkVisible("FuelPressR_D1", true);
            this.mesh.chunkVisible("Z_FuelPressR", false);
        }
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.chunkVisible("HullDamage2", true);
            this.mesh.chunkVisible("Revi_D0", false);
            this.mesh.chunkVisible("Z_ReViTint", false);
            this.mesh.chunkVisible("Revi_D1", true);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("EZ42", false);
            this.mesh.chunkVisible("EZ42Dimm", false);
            this.mesh.chunkVisible("EZ42Filter", false);
            this.mesh.chunkVisible("EZ42FLever", false);
            this.mesh.chunkVisible("EZ42Range", false);
            this.mesh.chunkVisible("EZ42Size", false);
            this.mesh.chunkVisible("DEZ42", true);
            this.mesh.chunkVisible("FuelPressL", false);
            this.mesh.chunkVisible("FuelPressL_D1", true);
            this.mesh.chunkVisible("Z_FuelPressL", false);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
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
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            ;
        }
        this.retoggleLight();
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        com.maddox.il2.engine.Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
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

    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private Point3d            tmpP;
    private Vector3d           tmpV;
    private boolean            bNeedSetUp;
    private float              pictAiler;
    private float              pictElev;
    private static final float speedometerIndScale[]    = { 0.0F, 0.0F, 0.0F, 17F, 35.5F, 57.5F, 76F, 95F, 112F };
    private static final float speedometerTruScale[]    = { 0.0F, 32.75F, 65.5F, 98.25F, 131F, 164F, 200F, 237F, 270.5F, 304F, 336F };
    private static final float variometerScale[]        = { 0.0F, 13.5F, 27F, 43.5F, 90F, 142.5F, 157F, 170.5F, 184F, 201.5F, 214.5F, 226F, 239.5F, 253F, 266F };
    private static final float rpmScale[]               = { 0.0F, 0.0F, 0.0F, 16.5F, 34.5F, 55F, 77.5F, 104F, 133.5F, 162.5F, 192F, 224F, 254F, 255.5F, 260F };
    private static final float fuelScale[]              = { 0.0F, 11F, 31F, 57F, 84F, 103.5F };
    private static final float k14TargetMarkScale[]     = { 0.0F, -0.2377F, -5.7991F, -21.9606F, -35.46F, -44.539F, -48.5318F, -84.2771F, -96.9686F, -106F, -151.252F };
    private static final float k14TargetWingspanScale[] = { 11.23F, 11.28F, 12.45F, 15.85F, 18.69F, 20.6F, 21.44F, 28.96F, 31.65F, 33.53F, 43.05F };
    private static final float k14BulletDrop[]          = { 5.812F, 6.168F, 6.508F, 6.978F, 7.24F, 7.576F, 7.849F, 8.108F, 8.473F, 8.699F, 8.911F, 9.111F, 9.384F, 9.554F, 9.787F, 9.928F, 9.992F, 10.282F, 10.381F, 10.513F, 10.603F, 10.704F, 10.739F, 10.782F, 10.789F };
}
