package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitF_72A5 extends CockpitPilot {
    private class Variables {

        float throttle;
        float prop;
        float mix;
        float stage;
        float altimeter;
        float azimuth;
        float vspeed;
        float waypointAzimuth;

        private Variables() {
        }

    }

    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitF_72A5.this.fm != null) {
                CockpitF_72A5.this.setTmp = CockpitF_72A5.this.setOld;
                CockpitF_72A5.this.setOld = CockpitF_72A5.this.setNew;
                CockpitF_72A5.this.setNew = CockpitF_72A5.this.setTmp;
                CockpitF_72A5.this.setNew.throttle = (0.85F * CockpitF_72A5.this.setOld.throttle) + (CockpitF_72A5.this.fm.CT.PowerControl * 0.15F);
                CockpitF_72A5.this.setNew.prop = (0.85F * CockpitF_72A5.this.setOld.prop) + (CockpitF_72A5.this.fm.CT.getStepControl() * 0.15F);
                CockpitF_72A5.this.setNew.stage = (0.85F * CockpitF_72A5.this.setOld.stage) + (CockpitF_72A5.this.fm.EI.engines[0].getControlCompressor() * 0.15F);
                CockpitF_72A5.this.setNew.mix = (0.85F * CockpitF_72A5.this.setOld.mix) + (CockpitF_72A5.this.fm.EI.engines[0].getControlMix() * 0.15F);
                CockpitF_72A5.this.setNew.altimeter = CockpitF_72A5.this.fm.getAltitude();
                if (Math.abs(CockpitF_72A5.this.fm.Or.getKren()) < 45F) {
                    CockpitF_72A5.this.setNew.azimuth = ((35F * CockpitF_72A5.this.setOld.azimuth) + -CockpitF_72A5.this.fm.Or.getYaw()) / 36F;
                }
                if ((CockpitF_72A5.this.setOld.azimuth > 270F) && (CockpitF_72A5.this.setNew.azimuth < 90F)) {
                    CockpitF_72A5.this.setOld.azimuth -= 360F;
                }
                if ((CockpitF_72A5.this.setOld.azimuth < 90F) && (CockpitF_72A5.this.setNew.azimuth > 270F)) {
                    CockpitF_72A5.this.setOld.azimuth += 360F;
                }
                CockpitF_72A5.this.setNew.waypointAzimuth = ((10F * CockpitF_72A5.this.setOld.waypointAzimuth) + (CockpitF_72A5.this.waypointAzimuth() - CockpitF_72A5.this.setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
                CockpitF_72A5.this.setNew.vspeed = ((199F * CockpitF_72A5.this.setOld.vspeed) + CockpitF_72A5.this.fm.getVertSpeed()) / 200F;
                CockpitF_72A5.this.pictTurba = (0.97F * CockpitF_72A5.this.pictTurba) + (0.03F * ((0.5F * CockpitF_72A5.this.fm.EI.engines[0].getPowerOutput()) + (0.5F * CockpitF_72A5.this.cvt(CockpitF_72A5.this.fm.EI.engines[0].getRPM(), 0.0F, 2000F, 0.0F, 1.0F))));
            }
            return true;
        }

        Interpolater() {
        }
    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        } else {
            waypoint.getP(this.tmpP);
            this.tmpV.sub(this.tmpP, this.fm.Loc);
            return (float) (Math.toDegrees(Math.atan2(-this.tmpV.y, this.tmpV.x)));
        }
    }

    public CockpitF_72A5() {
        super("3DO/Cockpit/F-72A5-P/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.bNeedSetUp = true;
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.cockpitNightMats = (new String[] { "prib1", "prib2", "prib3", "prib4", "prib5", "prib6", "shkala", "prib1_d1", "prib2_d1", "prib3_d1", "prib4_d1", "prib5_d1", "prib6_d1" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.bNeedSetUp = false;
            this.reflectPlaneMats();
        }
        Cockpit.xyz[1] = this.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.85F);
        this.mesh.chunkSetLocate("canopy", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("armPedalL", 0.0F, -15F * this.fm.CT.getRudder(), 0.0F);
        this.mesh.chunkSetAngles("armPedalR", 0.0F, 15F * this.fm.CT.getRudder(), 0.0F);
        this.mesh.chunkSetAngles("PedalL", 0.0F, 15F * this.fm.CT.getRudder(), 0.0F);
        this.mesh.chunkSetAngles("PedalR", 0.0F, -15F * this.fm.CT.getRudder(), 0.0F);
        this.mesh.chunkSetAngles("Stick", 0.0F, (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 20F, (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 16F);
        this.mesh.chunkSetAngles("supercharge", 0.0F, 70F * this.setNew.stage, 0.0F);
        this.mesh.chunkSetAngles("throtle", 0.0F, 62.7F * this.setNew.throttle, 0.0F);
        this.mesh.chunkSetAngles("prop", 0.0F, 70F * this.setNew.prop, 0.0F);
        this.mesh.chunkSetAngles("mixtura", 0.0F, 55F * this.setNew.mix, 0.0F);
        this.mesh.chunkSetAngles("flaplever", 0.0F, 0.0F, 70F * this.fm.CT.FlapsControl);
        this.mesh.chunkSetAngles("zfuelR", 0.0F, this.floatindex(this.cvt(this.fm.M.fuel, 0.0F, 981F, 0.0F, 6F), CockpitF_72A5.fuelGallonsScale), 0.0F);
        this.mesh.chunkSetAngles("zfuelL", 0.0F, -this.floatindex(this.cvt(this.fm.M.fuel, 0.0F, 981F, 0.0F, 4F), CockpitF_72A5.fuelGallonsAuxScale), 0.0F);
        this.mesh.chunkSetAngles("zacceleration", 0.0F, this.cvt(this.fm.getOverload(), -4F, 12F, -77F, 244F), 0.0F);
        this.mesh.chunkSetAngles("zSpeed1a", 0.0F, this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 1126.541F, 0.0F, 14F), CockpitF_72A5.speedometerScale), 0.0F);
        this.mesh.chunkSetAngles("zclimb", 0.0F, this.floatindex(this.cvt(this.setNew.vspeed, -30.48F, 30.48F, 0.0F, 12F), CockpitF_72A5.variometerScale), 0.0F);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("zTurn1a", 0.0F, this.cvt(this.w.z, -0.23562F, 0.23562F, 25F, -25F), 0.0F);
        this.mesh.chunkSetAngles("zSlide1a", 0.0F, this.cvt(this.getBall(7D), -7F, 7F, -16F, 16F), 0.0F);
        this.mesh.chunkSetAngles("zManifold1a", 0.0F, this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.3386378F, 2.370465F, 0.0F, 320F), 0.0F);
        this.mesh.chunkSetAngles("zAlt1a", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, 10800F), 0.0F);
        this.mesh.chunkSetAngles("zAlt1b", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, 1080F), 0.0F);
        this.mesh.chunkSetAngles("zRPM1a", 0.0F, this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4500F, 0.0F, 316F), 0.0F);
        this.mesh.chunkSetAngles("zoiltemp1a", 0.0F, this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 300F, 0.0F, 84F), 0.0F);
        this.mesh.chunkSetAngles("zClock1b", 0.0F, this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);
        this.mesh.chunkSetAngles("zClock1a", 0.0F, this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("zhorizont1a", 0.0F, -this.fm.Or.getKren(), 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.0328F, -0.0328F);
        this.mesh.chunkSetLocate("zhorizont1b", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("zturborpm1a", 0.0F, this.cvt(this.pictTurba, 0.0F, 2.0F, 0.0F, 207.5F), 0.0F);
        this.mesh.chunkSetAngles("zpressfuel1a", 0.0F, this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 0.4F, 0.0F, -154F), 0.0F);
        this.mesh.chunkSetAngles("zpressoil1a", 0.0F, this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness()), 0.0F, 7.45F, 0.0F, 180F), 0.0F);
        this.mesh.chunkSetAngles("zAzimuth1a", 0.0F, this.cvt(this.fm.Or.getTangage(), -5F, 5F, -5F, 5F), 0.0F);
        this.mesh.chunkSetAngles("zAzimuth1b", 0.0F, 90F - this.setNew.azimuth, 0.0F);
        this.mesh.chunkSetAngles("zMagAzimuth1a", 0.0F, this.cvt(this.fm.Or.getTangage(), -65F, 65F, -65F, 65F), 0.0F);
        this.mesh.chunkSetAngles("zMagAzimuth1b", -90F + this.setNew.azimuth, 0.0F, 0.0F);
        this.mesh.chunkVisible("Z_Red1", (this.fm.CT.getGear() < 0.05F) || !this.fm.Gears.lgear || !this.fm.Gears.rgear);
        this.mesh.chunkVisible("Z_Green1", this.fm.CT.getGear() > 0.95F);
        this.mesh.chunkVisible("Z_Red2", (this.fm.M.fuel / this.fm.M.maxFuel) < 0.15F);
        this.mesh.chunkVisible("Z_Red3", this.pictTurba > 1.0485F);
        this.mesh.chunkVisible("Z_Green2", this.fm.AS.bNavLightsOn);
        this.mesh.chunkVisible("Z_Red4", this.fm.AS.bNavLightsOn);
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("Z_Holes1_D1", true);
            this.mesh.chunkVisible("pricel", false);
            this.mesh.chunkVisible("pricel_d1", true);
            this.mesh.chunkVisible("Z_Z_RETIC27", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("GSlip", false);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            this.mesh.chunkVisible("Z_Holes1_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("pribors1", false);
            this.mesh.chunkVisible("pribors1_d1", true);
            this.mesh.chunkVisible("zamper", false);
            this.mesh.chunkVisible("zAzimuth1a", false);
            this.mesh.chunkVisible("zAzimuth1b", false);
            this.mesh.chunkVisible("zSpeed1a", false);
            this.mesh.chunkVisible("zacceleration", false);
            this.mesh.chunkVisible("zMagAzimuth1a", false);
            this.mesh.chunkVisible("zMagAzimuth1b", false);
            this.mesh.chunkVisible("zpresswater1a", false);
            this.mesh.chunkVisible("zclimb", false);
            this.mesh.chunkVisible("zRPM1a", false);
            this.mesh.chunkVisible("zoiltemp1a", false);
            this.mesh.chunkVisible("zturbormp1a", false);
            this.mesh.chunkVisible("zfas1a", false);
            this.mesh.chunkVisible("zoxipress1a", false);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
            this.mesh.chunkVisible("Z_Holes2_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            this.mesh.chunkVisible("Z_OilSplats_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            this.mesh.chunkVisible("pribors2", false);
            this.mesh.chunkVisible("pribors2_d1", true);
            this.mesh.chunkVisible("zClock1b", false);
            this.mesh.chunkVisible("zClock1a", false);
            this.mesh.chunkVisible("zfuelR", false);
            this.mesh.chunkVisible("zfuelL", false);
            this.mesh.chunkVisible("zsuction1a", false);
            this.mesh.chunkVisible("zTurn1a", false);
            this.mesh.chunkVisible("zSlide1a", false);
            this.mesh.chunkVisible("zhorizont1a", false);
            this.mesh.chunkVisible("zAlt1a", false);
            this.mesh.chunkVisible("zAlt1b", false);
            this.mesh.chunkVisible("zpressfuel1a", false);
            this.mesh.chunkVisible("zpressoil1a", false);
            this.mesh.chunkVisible("ztempoil1a", false);
            this.mesh.chunkVisible("zManifold1a", false);
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

    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    private boolean            bNeedSetUp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
    private float              pictTurba;
    private static final float fuelGallonsScale[]    = { 0.0F, 8.25F, 17.5F, 36.5F, 54F, 90F, 108F };
    private static final float fuelGallonsAuxScale[] = { 0.0F, 38F, 62.5F, 87F, 104F };
    private static final float speedometerScale[]    = { 0.0F, 5F, 47.5F, 92F, 134F, 180F, 227F, 241F, 255F, 262.5F, 270F, 283F, 296F, 312F, 328F };
    private static final float variometerScale[]     = { -170F, -147F, -124F, -101F, -78F, -48F, 0.0F, 48F, 78F, 101F, 124F, 147F, 170F };
    private Point3d            tmpP;
    private Vector3d           tmpV;

}
