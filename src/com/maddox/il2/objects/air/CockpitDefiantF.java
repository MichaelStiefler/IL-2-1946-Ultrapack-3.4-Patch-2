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
import com.maddox.sas1946.il2.util.CommonTools;

public class CockpitDefiantF extends CockpitPilot {
    private class Variables {

        float throttle;
        float altimeter;
        float azimuth;
        float vspeed;
        float waypointAzimuth;

        private Variables() {
        }

    }

    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitDefiantF.this.bNeedSetUp) {
                CockpitDefiantF.this.reflectPlaneMats();
                CockpitDefiantF.this.bNeedSetUp = false;
            }
            if (CockpitDefiantF.this.fm != null) {
                CockpitDefiantF.this.setTmp = CockpitDefiantF.this.setOld;
                CockpitDefiantF.this.setOld = CockpitDefiantF.this.setNew;
                CockpitDefiantF.this.setNew = CockpitDefiantF.this.setTmp;
                CockpitDefiantF.this.setNew.throttle = ((10F * CockpitDefiantF.this.setOld.throttle) + CockpitDefiantF.this.fm.CT.PowerControl) / 11F;
                CockpitDefiantF.this.setNew.altimeter = CockpitDefiantF.this.fm.getAltitude();
                if (Math.abs(CockpitDefiantF.this.fm.Or.getKren()) < 30F) {
                    CockpitDefiantF.this.setNew.azimuth = ((35F * CockpitDefiantF.this.setOld.azimuth) + -CockpitDefiantF.this.fm.Or.getYaw()) / 36F;
                }
                if ((CockpitDefiantF.this.setOld.azimuth > 270F) && (CockpitDefiantF.this.setNew.azimuth < 90F)) {
                    CockpitDefiantF.this.setOld.azimuth -= 360F;
                }
                if ((CockpitDefiantF.this.setOld.azimuth < 90F) && (CockpitDefiantF.this.setNew.azimuth > 270F)) {
                    CockpitDefiantF.this.setOld.azimuth += 360F;
                }
                CockpitDefiantF.this.setNew.waypointAzimuth = ((10F * CockpitDefiantF.this.setOld.waypointAzimuth) + (CockpitDefiantF.this.waypointAzimuth() - CockpitDefiantF.this.setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
                CockpitDefiantF.this.setNew.vspeed = ((199F * CockpitDefiantF.this.setOld.vspeed) + CockpitDefiantF.this.fm.getVertSpeed()) / 200F;
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

    public CockpitDefiantF() {
        super("3DO/Cockpit/DefiantF/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.bNeedSetUp = true;
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.cockpitNightMats = (new String[] { "BORT2", "BORT4", "COMPASS", "prib_five_fin", "prib_five", "prib_four", "prib_one_fin", "prib_one", "prib_six", "prib_three", "prib_two", "pricel" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        this.resetYPRmodifier();
        Cockpit.xyz[0] = CommonTools.smoothCvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, -0.55F);
        Cockpit.xyz[2] = CommonTools.smoothCvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.03F, 0.0F, 0.01F);
        this.mesh.chunkSetLocate("FONAR_GLASS", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkVisible("XLampEngHeat", (this.fm.EI.engines[0].tOilOut > 105.5F) | (this.fm.EI.engines[0].tWaterOut > 135.5F));
        this.mesh.chunkVisible("XLampGearUpL", (this.fm.CT.getGear() == 0.0F) && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearUpR", (this.fm.CT.getGear() == 0.0F) && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearDownL", (this.fm.CT.getGear() == 1.0F) && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearDownR", (this.fm.CT.getGear() == 1.0F) && this.fm.Gears.rgear);
        this.resetYPRmodifier();
        this.mesh.chunkSetAngles("RUSBase", 0.0F, 10F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)), 0.0F);
        this.mesh.chunkSetAngles("RUS", -35F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 0.0F, 0.0F);
        this.mesh.chunkVisible("RUS_GUN", !this.fm.CT.WeaponControl[0]);
        this.mesh.chunkSetAngles("RUS_TORM", 0.0F, 0.0F, -40F * this.fm.CT.getBrake());
        Cockpit.xyz[2] = 0.01625F * this.fm.CT.getAileron();
        this.mesh.chunkSetLocate("RUS_TAND_L", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = -Cockpit.xyz[2];
        this.mesh.chunkSetLocate("RUS_TAND_R", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("RUD", 0.0F, -81.81F * this.interp(this.setNew.throttle, this.setOld.throttle, f), 0.0F);
        this.resetYPRmodifier();
        if ((this.fm.CT.GearControl == 0.0F) && (this.fm.CT.getGear() != 0.0F)) {
            Cockpit.xyz[0] = 0.05F;
            Cockpit.xyz[2] = 0.0F;
        } else if ((this.fm.CT.GearControl == 1.0F) && (this.fm.CT.getGear() != 1.0F)) {
            Cockpit.xyz[0] = -0.05F;
            Cockpit.xyz[2] = 0.0F;
        } else if (Math.abs(this.fm.CT.FlapsControl - this.fm.CT.getFlap()) > 0.02F) {
            if ((this.fm.CT.FlapsControl - this.fm.CT.getFlap()) > 0.0F) {
                Cockpit.xyz[0] = -0.05F;
                Cockpit.xyz[2] = 0.0345F;
            } else {
                Cockpit.xyz[0] = 0.05F;
                Cockpit.xyz[2] = 0.0345F;
            }
        }
        this.mesh.chunkSetLocate("SHAS_RUCH_T", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("PROP_CONTR", (this.fm.CT.getStepControl() >= 0.0F ? this.fm.CT.getStepControl() : 1.0F - this.interp(this.setNew.throttle, this.setOld.throttle, f)) * -60F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("PEDALY", 9F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("COMPASS_M", -this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("SHKALA_DIRECTOR", this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("STREL_ALT_LONG", 0.0F, 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, -10800F));
        this.mesh.chunkSetAngles("STREL_ALT_SHORT", 0.0F, 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, -1080F));
        this.mesh.chunkSetAngles("STREL_ALT_SHRT1", 0.0F, 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, -108F));
        this.mesh.chunkSetAngles("STRELKA_BOOST", 0.0F, 0.0F, -this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.5173668F, 2.72369F, -70F, 250F));
        this.mesh.chunkSetAngles("STRELKA_FUEL", 0.0F, 0.0F, this.cvt(this.fm.M.fuel, 0.0F, 307.7F, 0.0F, 60F));
        this.mesh.chunkSetAngles("STRELK_FUEL_LB", 0.0F, -this.cvt(this.fm.M.fuel > 1.0F ? 10F * this.fm.EI.engines[0].getPowerOutput() : 0.0F, 0.0F, 10F, 0.0F, 10F), 0.0F);
        this.mesh.chunkSetAngles("STRELKA_RPM", 0.0F, 0.0F, -this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM(), 1000F, 5000F, 2.0F, 10F), CockpitDefiantF.rpmScale));
        this.mesh.chunkSetAngles("STRELK_TEMP_OIL", 0.0F, 0.0F, -this.cvt(this.fm.EI.engines[0].tOilOut, 40F, 100F, 0.0F, 270F));
        this.mesh.chunkSetAngles("STRELK_TEMP_RAD", 0.0F, 0.0F, -this.floatindex(this.cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 140F, 0.0F, 14F), CockpitDefiantF.radScale));
        this.resetYPRmodifier();
        Cockpit.xyz[2] = 0.05865F * this.fm.EI.engines[0].getControlRadiator();
        this.mesh.chunkSetLocate("zRadFlap", Cockpit.xyz, Cockpit.ypr);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("STREL_TURN_DOWN", 0.0F, 0.0F, -this.cvt(this.w.z, -0.23562F, 0.23562F, -48F, 48F));
        this.mesh.chunkSetAngles("STRELK_TURN_UP", 0.0F, 0.0F, -this.cvt(this.getBall(8D), -8F, 8F, 35F, -35F));
        this.mesh.chunkVisible("STRELK_V_SHORT", false);
        if (this.bFAF) {
            this.mesh.chunkSetAngles("STRELK_V_LONG", 0.0F, 0.0F, -this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeed()), 0.0F, 143.0528F, 0.0F, 32F), CockpitDefiantF.speedometerScaleFAF));
        } else {
            this.mesh.chunkSetAngles("STRELK_V_LONG", 0.0F, 0.0F, -this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeed()), 0.0F, 180.0555F, 0.0F, 35F), CockpitDefiantF.speedometerScale));
        }
        this.mesh.chunkSetAngles("STRELKA_VY", 0.0F, 0.0F, -this.floatindex(this.cvt(this.setNew.vspeed, -20.32F, 20.32F, 0.0F, 8F), CockpitDefiantF.variometerScale));
        this.mesh.chunkSetAngles("STRELKA_GOR_FAF", 0.0F, 0.0F, this.fm.Or.getKren());
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.032F, -0.032F);
        this.mesh.chunkSetLocate("STRELKA_GOR_RAF", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetLocate("STRELKA_GOS_FAF", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("STRELKA_HOUR", 0.0F, this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);
        this.mesh.chunkSetAngles("STRELKA_MINUTE", 0.0F, this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("STRELKA_SECUND", 0.0F, this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
            this.mesh.chunkVisible("HullDamage3", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.materialReplace("prib_four", "prib_four_damage");
            this.mesh.materialReplace("prib_four_night", "prib_four_damage_night");
            this.mesh.materialReplace("prib_three", "prib_three_damage");
            this.mesh.materialReplace("prib_three_night", "prib_three_damage_night");
            this.mesh.chunkVisible("STRELK_TEMP_OIL", false);
            this.mesh.chunkVisible("STRELK_TEMP_RAD", false);
            this.mesh.chunkVisible("STRELKA_BOOST", false);
            this.mesh.chunkVisible("STRELKA_FUEL", false);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
            this.mesh.chunkVisible("HullDamage4", true);
            this.mesh.chunkVisible("HullDamage2_RAF", true);
            this.mesh.chunkVisible("HullDamage2_FAF", true);
            this.mesh.materialReplace("prib_three", "prib_three_damage");
            this.mesh.materialReplace("prib_three_night", "prib_three_damage_night");
            this.mesh.chunkVisible("STRELK_TEMP_OIL", false);
            this.mesh.chunkVisible("STRELK_TEMP_RAD", false);
            this.mesh.chunkVisible("STRELKA_BOOST", false);
            this.mesh.chunkVisible("STRELKA_FUEL", false);
        }
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            this.mesh.chunkVisible("HullDamage3", true);
            this.mesh.chunkVisible("HullDamage6", true);
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.materialReplace("prib_two", "prib_two_damage");
            this.mesh.materialReplace("prib_two_night", "prib_two_damage_night");
            this.mesh.chunkVisible("STREL_ALT_LONG", false);
            this.mesh.chunkVisible("STREL_ALT_SHORT", false);
            this.mesh.chunkVisible("STRELKA_RPM", false);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            this.mesh.chunkVisible("HullDamage5", true);
            this.mesh.chunkVisible("HullDamage6", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("HullDamage1", true);
            if (this.bFAF) {
                this.mesh.chunkVisible("HullDamage2_FAF", true);
            } else {
                this.mesh.chunkVisible("HullDamage2_RAF", true);
            }
            this.mesh.materialReplace("prib_one", "prib_one_damage");
            this.mesh.materialReplace("prib_one_fin", "prib_one_fin_damage");
            this.mesh.materialReplace("prib_one_night", "prib_one_damage_night");
            this.mesh.materialReplace("prib_one_fin_night", "prib_one_fin_damage_night");
            this.mesh.chunkVisible("STRELK_V_LONG", false);
            this.mesh.chunkVisible("STRELK_V_SHORT", false);
            this.mesh.chunkVisible("STRELKA_GOR_FAF", false);
            this.mesh.chunkVisible("STRELKA_GOR_RAF", false);
            this.mesh.chunkVisible("STRELKA_VY", false);
            this.mesh.chunkVisible("STREL_TURN_DOWN", false);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
        }
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("XGlassDamage2", true);
        }
        this.retoggleLight();
    }

    protected void reflectPlaneMats() {
        if (this.aircraft().getRegiment().country().equals("fi")) {
            this.bFAF = true;
            this.mesh.chunkVisible("PRIBORY_RAF", false);
            this.mesh.chunkVisible("PRIBORY_FAF", true);
            this.mesh.chunkVisible("STRELKA_GOR_RAF", false);
            this.mesh.chunkVisible("STRELKA_GOR_FAF", true);
            this.mesh.chunkVisible("STRELKA_GOS_FAF", true);
        } else {
            this.bFAF = false;
            this.mesh.chunkVisible("PRIBORY_RAF", true);
            this.mesh.chunkVisible("PRIBORY_FAF", false);
            this.mesh.chunkVisible("STRELKA_GOR_RAF", true);
            this.mesh.chunkVisible("STRELKA_GOR_FAF", false);
            this.mesh.chunkVisible("STRELKA_GOS_FAF", false);
        }
        HierMesh hiermesh = this.aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
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
    private boolean            bFAF;
    private float              pictAiler;
    private float              pictElev;
    private boolean            bNeedSetUp;
    private static final float speedometerScale[]    = { 0.0F, 0.0F, 1.0F, 3F, 7.5F, 34.5F, 46F, 63F, 76F, 94F, 112.5F, 131F, 150F, 168.5F, 187F, 203F, 222F, 242.5F, 258.5F, 277F, 297F, 315.5F, 340F, 360F, 376.5F, 392F, 407F, 423.5F, 442F, 459F, 476F, 492.5F, 513F, 534.5F, 552F, 569.5F };
    private static final float speedometerScaleFAF[] = { 0.0F, 0.0F, 2.0F, 6F, 21F, 40F, 56F, 72.5F, 89.5F, 114F, 135.5F, 157F, 182.5F, 205F, 227.5F, 246.5F, 265.5F, 286F, 306F, 326F, 345.5F, 363F, 385F, 401F, 414.5F, 436.5F, 457F, 479F, 496.5F, 515F, 539F, 559.5F, 576.5F };
    private static final float radScale[]            = { 0.0F, 3F, 7F, 13.5F, 21.5F, 27F, 34.5F, 50.5F, 71F, 94F, 125F, 161F, 202.5F, 253F, 315.5F };
    private static final float rpmScale[]            = { 0.0F, 0.0F, 0.0F, 22F, 58F, 103.5F, 152.5F, 193.5F, 245F, 281.5F, 311.5F };
    private static final float variometerScale[]     = { -158F, -110F, -63.5F, -29F, 0.0F, 29F, 63.5F, 110F, 158F };
    private Point3d            tmpP;
    private Vector3d           tmpV;

}
