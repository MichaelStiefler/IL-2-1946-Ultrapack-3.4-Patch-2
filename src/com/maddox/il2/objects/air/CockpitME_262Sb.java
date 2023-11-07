package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Tuple3f;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;

public class CockpitME_262Sb extends CockpitPilot {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            CockpitME_262Sb.this.setTmp = CockpitME_262Sb.this.setOld;
            CockpitME_262Sb.this.setOld = CockpitME_262Sb.this.setNew;
            CockpitME_262Sb.this.setNew = CockpitME_262Sb.this.setTmp;
            CockpitME_262Sb.this.setNew.altimeter = CockpitME_262Sb.this.fm.getAltitude();
            CockpitME_262Sb.this.setNew.throttlel = ((10F * CockpitME_262Sb.this.setOld.throttlel) + CockpitME_262Sb.this.fm.EI.engines[0].getControlThrottle()) / 11F;
            CockpitME_262Sb.this.setNew.throttler = ((10F * CockpitME_262Sb.this.setOld.throttler) + CockpitME_262Sb.this.fm.EI.engines[1].getControlThrottle()) / 11F;
            CockpitME_262Sb.this.setNew.azimuth = CockpitME_262Sb.this.fm.Or.getYaw();
            if ((CockpitME_262Sb.this.setOld.azimuth > 270F) && (CockpitME_262Sb.this.setNew.azimuth < 90F)) {
                CockpitME_262Sb.this.setOld.azimuth -= 360F;
            }
            if ((CockpitME_262Sb.this.setOld.azimuth < 90F) && (CockpitME_262Sb.this.setNew.azimuth > 270F)) {
                CockpitME_262Sb.this.setOld.azimuth += 360F;
            }
            CockpitME_262Sb.this.setNew.waypointAzimuth = ((10F * CockpitME_262Sb.this.setOld.waypointAzimuth) + (CockpitME_262Sb.this.waypointAzimuth() - CockpitME_262Sb.this.setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
            CockpitME_262Sb.this.setNew.vspeed = ((299F * CockpitME_262Sb.this.setOld.vspeed) + CockpitME_262Sb.this.fm.getVertSpeed()) / 300F;
            if (CockpitME_262Sb.this.cockpitDimControl) {
                if (CockpitME_262Sb.this.setNew.dimPosition > 0.0F) {
                    CockpitME_262Sb.this.setNew.dimPosition = CockpitME_262Sb.this.setOld.dimPosition - 0.05F;
                }
            } else if (CockpitME_262Sb.this.setNew.dimPosition < 1.0F) {
                CockpitME_262Sb.this.setNew.dimPosition = CockpitME_262Sb.this.setOld.dimPosition + 0.05F;
            }
            return true;
        }

    }

    private class Variables {

        float altimeter;
        float throttlel;
        float throttler;
        float azimuth;
        float waypointAzimuth;
        float vspeed;
        float dimPosition;

        private Variables() {
        }

    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        } else {
            waypoint.getP(this.tmpP);
            this.tmpV.sub(this.tmpP, this.fm.Loc);
            return (float) (57.295779513082323D * Math.atan2(((Tuple3d) (this.tmpV)).y, ((Tuple3d) (this.tmpV)).x));
        }
    }

    public CockpitME_262Sb() {
        super("3DO/Cockpit/Me-262/hierSb.him", "he111");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.gun = new Gun[4];
        this.bNeedSetUp = true;
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.cockpitNightMats = (new String[] { "2petitsb_d1", "2petitsb", "aiguill1", "badinetm_d1", "badinetm", "baguecom", "brasdele", "comptemu_d1", "comptemu", "petitfla_d1", "petitfla", "turnbank" });
        this.setNightMats(false);
        this.setNew.dimPosition = 1.0F;
        this.interpPut(new Interpolater(), null, Time.current(), null);
        this.hasRadar = false;
    }

    public void removeCanopy() {
        this.mesh.chunkVisible("Canopy", false);
        this.mesh.chunkVisible("Z_Holes2_D1", false);
        this.mesh.chunkVisible("Z_Holes1_D1", false);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        if (this.gun[0] == null) {
            this.gun[0] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON03");
            this.gun[1] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON01");
            this.gun[2] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON02");
            this.gun[3] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON04");
        }
        if (this.fm.isTick(44, 0)) {
            this.mesh.chunkVisible("Z_GearLGreen1", (this.fm.CT.getGear() == 1.0F) && this.fm.Gears.lgear);
            this.mesh.chunkVisible("Z_GearRGreen1", (this.fm.CT.getGear() == 1.0F) && this.fm.Gears.rgear);
            this.mesh.chunkVisible("Z_GearCGreen1", this.fm.CT.getGear() == 1.0F);
            this.mesh.chunkVisible("Z_GearLRed1", (this.fm.CT.getGear() == 0.0F) || this.fm.Gears.isAnyDamaged());
            this.mesh.chunkVisible("Z_GearRRed1", (this.fm.CT.getGear() == 0.0F) || this.fm.Gears.isAnyDamaged());
            this.mesh.chunkVisible("Z_GearCRed1", this.fm.CT.getGear() == 0.0F);
            this.mesh.chunkVisible("Z_MachLamp", (this.fm.getSpeed() / Atmosphere.sonicSpeed((float) this.fm.Loc.z)) > 0.8F);
            this.mesh.chunkVisible("Z_CabinLamp", this.fm.Loc.z > 12000D);
            this.mesh.chunkVisible("Z_FuelLampV", this.fm.M.fuel < 300F);
            this.mesh.chunkVisible("Z_FuelLampIn", this.fm.M.fuel < 300F);
        }
        this.mesh.chunkSetAngles("Z_ReviTint", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -45F), 0.0F, 0.0F);
        this.resetYPRmodifier();
        this.mesh.chunkSetAngles("Canopy", 0.0F, 0.0F, -100F * this.fm.CT.getCockpitDoor());
        Cockpit.xyz[1] = (this.fm.CT.GearControl != 0.0F) || (this.fm.CT.getGear() == 0.0F) ? 0.0F : -0.0107F;
        this.mesh.chunkSetLocate("Z_GearEin", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = (this.fm.CT.GearControl != 1.0F) || (this.fm.CT.getGear() == 1.0F) ? 0.0F : -0.0107F;
        this.mesh.chunkSetLocate("Z_GearAus", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.fm.CT.FlapsControl >= this.fm.CT.getFlap() ? 0.0F : -0.0107F;
        this.mesh.chunkSetLocate("Z_FlapEin", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[1] = this.fm.CT.FlapsControl <= this.fm.CT.getFlap() ? 0.0F : -0.0107F;
        this.mesh.chunkSetLocate("Z_FlapAus", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Column", 10F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 0.0F, 10F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)));
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[0]) {
            Cockpit.xyz[2] = -0.0025F;
        }
        this.mesh.chunkSetLocate("Z_Columnbutton1", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[2] || this.fm.CT.saveWeaponControl[3]) {
            Cockpit.xyz[2] = -0.00325F;
        }
        this.mesh.chunkSetLocate("Z_Columnbutton2", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_PedalStrut", 20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", -20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal", -20F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_ThrottleL", 0.0F, -75F * this.interp(this.setNew.throttlel, this.setOld.throttlel, f), 0.0F);
        this.mesh.chunkSetAngles("Z_ThrottleR", 0.0F, -75F * this.interp(this.setNew.throttler, this.setOld.throttler, f), 0.0F);
        this.mesh.chunkSetAngles("Z_FuelLeverL", this.fm.EI.engines[0].getControlMagnetos() != 3 ? 0.0F : 6.5F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelLeverR", this.fm.EI.engines[1].getControlMagnetos() != 3 ? 0.0F : 6.5F, 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = 0.03675F * this.fm.CT.getTrimElevatorControl();
        this.mesh.chunkSetLocate("Z_TailTrim", Cockpit.xyz, Cockpit.ypr);
        if ((this.fm.CT.Weapons[3] != null) && !this.fm.CT.Weapons[3][0].haveBullets()) {
            this.mesh.chunkSetAngles("Z_Bombbutton", 0.0F, 53F, 0.0F);
        }
        this.mesh.chunkSetAngles("Z_AmmoCounter1", this.cvt(this.gun[1].countBullets(), 0.0F, 100F, 0.0F, -7F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AmmoCounter2", this.cvt(this.gun[2].countBullets(), 0.0F, 100F, 0.0F, -7F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer1", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 100F, 400F, 2.0F, 8F), speedometerIndScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer2", this.floatindex(this.cvt(this.fm.getSpeedKMH(), 100F, 1000F, 1.0F, 10F), speedometerTruScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 16000F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Hour1", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Second1", this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.fm.Or.getTangage(), 0.0F, this.fm.Or.getKren());
        this.mesh.chunkSetAngles("Z_TurnBank2", 0.0F, 0.0F, -this.cvt(this.getBall(6D), -6F, 6F, -7.5F, 7.5F));
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank3", 0.0F, 0.0F, this.cvt(((Tuple3f) (this.w)).z, -0.23562F, 0.23562F, -50F, 50F));
        this.mesh.chunkSetAngles("Z_Climb1", this.floatindex(this.cvt(this.setNew.vspeed, -20F, 50F, 0.0F, 14F), variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPML", this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPMR", this.floatindex(this.cvt(this.fm.EI.engines[1].getRPM() * 10F * 0.25F, 2000F, 14000F, 2.0F, 14F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass1", this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass2", -this.interp(this.setNew.waypointAzimuth, this.setOld.waypointAzimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasPressureL", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.6F * this.fm.EI.engines[0].getPowerOutput(), 0.0F, 1.0F, 0.0F, 273.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasPressureR", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.6F * this.fm.EI.engines[1].getPowerOutput(), 0.0F, 1.0F, 0.0F, 273.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasTempL", this.cvt(this.fm.EI.engines[0].tWaterOut, 300F, 1000F, 0.0F, 96F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_GasTempR", this.cvt(this.fm.EI.engines[1].tWaterOut, 300F, 1000F, 0.0F, 96F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPressureL", this.cvt(1.0F + (0.005F * this.fm.EI.engines[0].tOilOut), 0.0F, 10F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPressureR", this.cvt(1.0F + (0.005F * this.fm.EI.engines[1].tOilOut), 0.0F, 10F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelPressL", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 80F * this.fm.EI.engines[0].getPowerOutput() * this.fm.EI.engines[0].getReadyness(), 0.0F, 160F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelPressR", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 80F * this.fm.EI.engines[1].getPowerOutput() * this.fm.EI.engines[1].getReadyness(), 0.0F, 160F, 0.0F, 278F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelRemainV", this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 1000F, 0.0F, 5F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelRemainIn", this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 1000F, 0.0F, 5F), fuelScale), 0.0F, 0.0F);

        // +++ RadarLiSN2 +++
        if (this.hasRadar) {
            this.cockpitRadarLiSN2.updateRadar();
            // --- RadarLiSN2 ---
        }
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2 = new RadarLiSN2Equipment(this, 27, 155F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
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
            this.mesh.chunkVisible("Revi_D1", true);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
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
        this.fm.AS.getClass();
        this.retoggleLight();
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
        if ((this.aircraft().thisWeaponsName.toLowerCase().indexOf("wfrgr") >= 0) || (this.aircraft().thisWeaponsName.toLowerCase().indexOf("r4m") >= 0)) {
            this.hasRadar = true;
        } else {
            this.mesh.hideSubTrees("RadarHook");
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

    private boolean            hasRadar;
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private Point3d            tmpP;
    private Vector3d           tmpV;
    private Gun                gun[];
    private boolean            bNeedSetUp;
    private float              pictAiler;
    private float              pictElev;
    private static final float speedometerIndScale[] = { 0.0F, 0.0F, 0.0F, 17F, 35.5F, 57.5F, 76F, 95F, 112F };
    private static final float speedometerTruScale[] = { 0.0F, 32.75F, 65.5F, 98.25F, 131F, 164F, 200F, 237F, 270.5F, 304F, 336F };
    private static final float variometerScale[]     = { 0.0F, 13.5F, 27F, 43.5F, 90F, 142.5F, 157F, 170.5F, 184F, 201.5F, 214.5F, 226F, 239.5F, 253F, 266F };
    private static final float rpmScale[]            = { 0.0F, 0.0F, 0.0F, 16.5F, 34.5F, 55F, 77.5F, 104F, 133.5F, 162.5F, 192F, 224F, 254F, 255.5F, 260F };
    private static final float fuelScale[]           = { 0.0F, 11F, 31F, 57F, 84F, 103.5F };
}
