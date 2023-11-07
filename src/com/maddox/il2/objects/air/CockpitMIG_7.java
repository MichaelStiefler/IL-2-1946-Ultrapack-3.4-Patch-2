package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

/**
 *
 * @author SAS~Skylla, SAS~Storebror.
 * @see MIG_7.java
 *
 *      TODO's: siehe MIG_7.java
 **/

public class CockpitMIG_7 extends CockpitPilot {

    private class Variables {
        float throttle;
        float prop;
        float altimeter;
        float azimuth;
        float vspeed;
        float mix;
        float xyz[] = { 0.0F, 0.0F, 0.0F };
        float ypr[] = { 0.0F, 0.0F, 0.0F };
    }

    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitMIG_7.this.bNeedSetUp) {
                CockpitMIG_7.this.reflectPlaneMats();
                CockpitMIG_7.this.bNeedSetUp = false;
            }
            if (CockpitMIG_7.this.fm != null) {
                CockpitMIG_7.this.setTmp = CockpitMIG_7.this.setOld;
                CockpitMIG_7.this.setOld = CockpitMIG_7.this.setNew;
                CockpitMIG_7.this.setNew = CockpitMIG_7.this.setTmp;
                CockpitMIG_7.this.setNew.throttle = ((10F * CockpitMIG_7.this.setOld.throttle) + CockpitMIG_7.this.fm.CT.PowerControl) / 11F;
                CockpitMIG_7.this.setNew.prop = ((10F * CockpitMIG_7.this.setOld.prop) + CockpitMIG_7.this.fm.EI.engines[0].getControlProp()) / 11F;
                CockpitMIG_7.this.setNew.mix = (0.8F * CockpitMIG_7.this.setOld.mix) + (0.2F * CockpitMIG_7.this.fm.EI.engines[0].getControlMix());
                CockpitMIG_7.this.setNew.altimeter = CockpitMIG_7.this.fm.getAltitude();
                if (Math.abs(CockpitMIG_7.this.fm.Or.getKren()) < 30F) {
                    CockpitMIG_7.this.setNew.azimuth = ((35F * CockpitMIG_7.this.setOld.azimuth) + CockpitMIG_7.this.fm.Or.azimut()) / 36F;
                }
                if ((CockpitMIG_7.this.setOld.azimuth > 270F) && (CockpitMIG_7.this.setNew.azimuth < 90F)) {
                    CockpitMIG_7.this.setOld.azimuth -= 360F;
                }
                if ((CockpitMIG_7.this.setOld.azimuth < 90F) && (CockpitMIG_7.this.setNew.azimuth > 270F)) {
                    CockpitMIG_7.this.setOld.azimuth += 360F;
                }
                CockpitMIG_7.this.setNew.vspeed = ((199F * CockpitMIG_7.this.setOld.vspeed) + CockpitMIG_7.this.fm.getVertSpeed()) / 200F;
            }
            return true;
        }

        Interpolater() {
        }
    }

    public CockpitMIG_7() {
        super("3do/cockpit/MiG-7/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.bNeedSetUp = true;
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictFlap = 0.0F;
        this.pictGear = 0.0F;
        this.pictDoor = 0.0F;
        this.pictRadiator = 0.0F;
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        this.resetYPRmodifier();
        Cockpit.xyz[0] = -this.cvt(this.fm.CT.getCockpitDoor(), 0.11F, 0.99F, 0.0F, 0.6F);
        this.mesh.chunkSetLocate("Blister", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[2]) {
            Cockpit.xyz[2] = -0.004F;
        }
        this.mesh.chunkSetLocate("r_three", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("richag", 0.0F, (this.pictElev = (0.85F * this.pictElev) + (0.12F * this.fm.CT.ElevatorControl)) * 10F, (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 15F);
        this.mesh.chunkSetAngles("Ped_Base", 0.0F, -this.fm.CT.getRudder() * 18F, 0.0F);
        this.mesh.chunkSetAngles("LightK", 0.0F, this.cockpitLightControl ? 60F : 0.0F, 0.0F);
        this.mesh.chunkSetAngles("LightD", 0.0F, this.cockpitLightControl ? 60F : 0.0F, 0.0F);
        this.mesh.chunkSetAngles("PedalL", 0.0F, this.fm.CT.getRudder() * 18F, 0.0F);
        this.mesh.chunkSetAngles("PedalR", 0.0F, this.fm.CT.getRudder() * 18F, 0.0F);
        this.mesh.chunkSetAngles("TrubkaR", 0.0F, this.fm.CT.getRudder() * 18F, 0.0F);
        this.mesh.chunkSetAngles("TrosR", 0.0F, this.fm.CT.getRudder() * 18F, 0.0F);
        this.mesh.chunkSetAngles("TrosL", 0.0F, this.fm.CT.getRudder() * 18F, 0.0F);
        this.mesh.chunkSetAngles("ANO", 0.0F, this.fm.AS.bNavLightsOn ? -40F : 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Fara", 0.0F, this.fm.AS.bLandingLightOn ? -40F : 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ZamokR", -85F * (this.pictDoor = (0.89F * this.pictDoor) + (0.11F * this.fm.CT.getCockpitDoor())), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Flaps", -153F * (this.pictFlap = (0.89F * this.pictFlap) + (0.11F * this.fm.CT.FlapsControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Radiator", 60F * (this.pictRadiator = (0.89F * this.pictRadiator) + (0.11F * this.fm.EI.engines[0].getControlRadiator())), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Drossel", 0.0F, this.interp(this.setNew.throttle, this.setOld.throttle, f) * -45F * 0.91F, 0.0F);
        this.mesh.chunkSetAngles("Forsaj", 0.0F, this.interp(this.setNew.mix, this.setOld.mix, f) * -37F * 0.91F, 0.0F);
        this.mesh.chunkSetAngles("r_two", 0.0F, -20F * (this.fm.CT.WeaponControl[0] ? 1 : 0), 0.0F);
        this.mesh.chunkSetAngles("r_turn", 0.0F, -20F * this.fm.CT.BrakeControl, 0.0F);
        if (this.fm.Gears.isHydroOperable()) {
            this.mesh.chunkSetAngles("Gear", -150F * (this.pictGear = (0.89F * this.pictGear) + (0.11F * this.fm.CT.GearControl)), 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("zAlt1a", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F);
        this.mesh.chunkSetAngles("zAlt1b", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("zAzimuth1b", 0.0F, this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F);
        this.mesh.chunkSetLocate("zHorizon1a", this.setOld.xyz, this.setOld.ypr);
        this.mesh.chunkSetAngles("zHorizon1b", 0.0F, this.fm.Or.getKren(), 0.0F);
        this.mesh.chunkSetAngles("zManifold1a", 0.0F, this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.4F, 2.133F, 0.0F, 334.286F), 0.0F);
        this.mesh.chunkSetAngles("zGas1a", 0.0F, this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 300F, 0.0F, 171F), 0.0F);
        this.mesh.chunkSetAngles("zSpeed1a", 0.0F, this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 800F, 0.0F, 16F), speedometerScale), 0.0F);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("zTurn1a", 0.0F, this.cvt(this.w.z, -0.23562F, 0.23562F, 30F, -30F), 0.0F);
        this.mesh.chunkSetAngles("zSlide1a", 0.0F, this.cvt(this.getBall(8D), -8F, 8F, -28F, 28F), 0.0F);
        this.mesh.chunkSetAngles("zVariometer1a", 0.0F, this.cvt(this.setNew.vspeed, -10F, 10F, -180F, 180F), 0.0F);
        this.mesh.chunkSetAngles("zRPM1a", 0.0F, this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 10000F, 0.0F, 3600F), 0.0F);
        this.mesh.chunkSetAngles("zRPM1b", 0.0F, this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 10000F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("zTOilOut1a", 0.0F, this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 125F, 0.0F, 180F), 0.0F);
        this.mesh.chunkSetAngles("zOilPrs1a", 0.0F, this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut), 0.0F, 15F, 0.0F, 180F), 0.0F);
        this.mesh.chunkSetAngles("zGasPrs1a", 0.0F, this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 3050F, 0.0F, 4F), 0.0F, 8F, 0.0F, -180F), 0.0F);
        this.mesh.chunkSetAngles("zTWater1a", 0.0F, this.cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 120F, 0.0F, 92F), 0.0F);
        this.mesh.chunkSetAngles("zTOilIn1a", 0.0F, this.cvt(this.fm.EI.engines[0].tOilIn, 0.0F, 120F, 0.0F, 92F), 0.0F);
        this.mesh.chunkSetAngles("zClock1a", 0.0F, this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("zClock1b", 0.0F, this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);
        this.mesh.chunkVisible("Z_GearLGreen1", this.fm.CT.getGear() == 1.0F);
        this.mesh.chunkVisible("Z_GearRGreen1", this.fm.CT.getGear() == 1.0F);
        this.mesh.chunkVisible("Z_GearLRed1", this.fm.CT.getGear() == 0.0F);
        this.mesh.chunkVisible("Z_GearRRed1", this.fm.CT.getGear() == 0.0F);
    }

    public void reflectCockpitState() {
        if (((this.fm.AS.astateCockpitState & 4) != 0) || ((this.fm.AS.astateCockpitState & 0x10) != 0)) {
            this.mesh.chunkVisible("prib_D1", false);
            this.mesh.chunkVisible("prib_N1", false);
            this.mesh.chunkVisible("prib_DD1", true);
            this.mesh.chunkVisible("zAzimuth1a", false);
            this.mesh.chunkVisible("zAzimuth1b", false);
            this.mesh.chunkVisible("zHorizon1a", false);
            this.mesh.chunkVisible("zHorizon1b", false);
            this.mesh.chunkVisible("zManifold1a", false);
            this.mesh.chunkVisible("zVariometer1a", false);
            this.mesh.chunkVisible("zSpeed1a", false);
            this.mesh.chunkVisible("zTOilIn1a", false);
        }
        if (((this.fm.AS.astateCockpitState & 8) != 0) || ((this.fm.AS.astateCockpitState & 0x20) != 0)) {
            this.mesh.chunkVisible("prib_D2", false);
            this.mesh.chunkVisible("prib_N2", false);
            this.mesh.chunkVisible("prib_DD2", true);
            this.mesh.chunkVisible("zRPM1a", false);
            this.mesh.chunkVisible("zRPM1b", false);
            this.mesh.chunkVisible("zAlt1a", false);
            this.mesh.chunkVisible("zAlt1b", false);
            this.mesh.chunkVisible("zGas1a", false);
            this.mesh.chunkVisible("zTurn1a", false);
            this.mesh.chunkVisible("zTOilOut1a", false);
            this.mesh.chunkVisible("zOilPrs1a", false);
            this.mesh.chunkVisible("zGasPrs1a", false);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("panel_d", false);
            this.mesh.chunkVisible("panel_n", false);
            this.mesh.chunkVisible("panel_dd", true);
            this.mesh.chunkVisible("zTWater1a", false);
        }
    }

    protected void reflectPlaneMats() {
        this.mesh.materialReplace("Gloss1D0o", this.aircraft().hierMesh().material(this.aircraft().hierMesh().materialFind("Gloss1D0o")));
        if (this.aircraft() instanceof MIG_11)
            this.mesh.materialReplace("RPanel", "RPanel_MiG11");
        else if (this.aircraft() instanceof MIG_7)
            this.mesh.materialReplace("RPanel", "RPanel_MiG7");
    }

    public void toggleLight() {
        this.cockpitLightControl = !this.cockpitLightControl;
        if (((this.fm.AS.astateCockpitState & 4) == 0) && ((this.fm.AS.astateCockpitState & 0x10) == 0)) {
            this.mesh.chunkVisible("prib_D1", !this.cockpitLightControl);
            this.mesh.chunkVisible("prib_N1", this.cockpitLightControl);
        }
        if (((this.fm.AS.astateCockpitState & 8) == 0) && ((this.fm.AS.astateCockpitState & 0x20) == 0)) {
            this.mesh.chunkVisible("prib_D2", !this.cockpitLightControl);
            this.mesh.chunkVisible("prib_N2", this.cockpitLightControl);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) == 0) {
            this.mesh.chunkVisible("panel_d", !this.cockpitLightControl);
            this.mesh.chunkVisible("panel_n", this.cockpitLightControl);
        }
        if (this.cockpitLightControl) {
            this.mesh.materialReplace("Strelki", "Strelki_n");
        } else {
            this.mesh.materialReplace("Strelki", "Strelki");
        }
    }

    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private boolean            bNeedSetUp;
    private float              pictAiler;
    private float              pictElev;
    private float              pictFlap;
    private float              pictGear;
    private float              pictDoor;
    private float              pictRadiator;
    private static final float speedometerScale[] = { 0.0F, 0.0F, 15.5F, 50F, 95.5F, 137F, 182.5F, 212F, 230F, 242F, 254.5F, 267.5F, 279F, 292F, 304F, 317F, 329.5F, 330F };

}
