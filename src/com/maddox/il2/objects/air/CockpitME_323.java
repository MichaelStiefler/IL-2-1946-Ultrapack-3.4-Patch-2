package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitME_323 extends CockpitPilot {
    class Interpolater extends InterpolateRef {
        public boolean tick() {
            if (CockpitME_323.this.fm != null) {
                CockpitME_323.this.setTmp = CockpitME_323.this.setOld;
                CockpitME_323.this.setOld = CockpitME_323.this.setNew;
                CockpitME_323.this.setNew = CockpitME_323.this.setTmp;
                CockpitME_323.this.setNew.throttle1 = (0.85F * CockpitME_323.this.setOld.throttle1) + (CockpitME_323.this.fm.EI.engines[0].getControlThrottle() * 0.15F);
                CockpitME_323.this.setNew.throttle2 = (0.85F * CockpitME_323.this.setOld.throttle2) + (CockpitME_323.this.fm.EI.engines[1].getControlThrottle() * 0.15F);
                CockpitME_323.this.setNew.throttle3 = (0.85F * CockpitME_323.this.setOld.throttle3) + (CockpitME_323.this.fm.EI.engines[2].getControlThrottle() * 0.15F);
                CockpitME_323.this.setNew.throttle4 = (0.85F * CockpitME_323.this.setOld.throttle4) + (CockpitME_323.this.fm.EI.engines[3].getControlThrottle() * 0.15F);
                CockpitME_323.this.setNew.throttle5 = (0.85F * CockpitME_323.this.setOld.throttle5) + (CockpitME_323.this.fm.EI.engines[4].getControlThrottle() * 0.15F);
                CockpitME_323.this.setNew.throttle6 = (0.85F * CockpitME_323.this.setOld.throttle6) + (CockpitME_323.this.fm.EI.engines[5].getControlThrottle() * 0.15F);
                CockpitME_323.this.setNew.prop1 = (0.85F * CockpitME_323.this.setOld.prop1) + (CockpitME_323.this.fm.EI.engines[0].getControlProp() * 0.15F);
                CockpitME_323.this.setNew.prop2 = (0.85F * CockpitME_323.this.setOld.prop2) + (CockpitME_323.this.fm.EI.engines[1].getControlProp() * 0.15F);
                CockpitME_323.this.setNew.prop3 = (0.85F * CockpitME_323.this.setOld.prop3) + (CockpitME_323.this.fm.EI.engines[2].getControlProp() * 0.15F);
                CockpitME_323.this.setNew.prop4 = (0.85F * CockpitME_323.this.setOld.prop4) + (CockpitME_323.this.fm.EI.engines[3].getControlProp() * 0.15F);
                CockpitME_323.this.setNew.prop5 = (0.85F * CockpitME_323.this.setOld.prop5) + (CockpitME_323.this.fm.EI.engines[4].getControlProp() * 0.15F);
                CockpitME_323.this.setNew.prop6 = (0.85F * CockpitME_323.this.setOld.prop6) + (CockpitME_323.this.fm.EI.engines[5].getControlProp() * 0.15F);
                CockpitME_323.this.setNew.altimeter = CockpitME_323.this.fm.getAltitude();
                float f = CockpitME_323.this.waypointAzimuth();
                CockpitME_323.this.setNew.azimuth.setDeg(CockpitME_323.this.setOld.azimuth.getDeg(1.0F), CockpitME_323.this.fm.Or.azimut());
                CockpitME_323.this.setNew.waypointAzimuth.setDeg(CockpitME_323.this.setOld.waypointAzimuth.getDeg(1.0F), f);
                CockpitME_323.this.setNew.waypointDeviation.setDeg(CockpitME_323.this.setOld.waypointDeviation.getDeg(0.1F), (f - CockpitME_323.this.setOld.azimuth.getDeg(1.0F)) + World.Rnd().nextFloat(-10F, 10F));
                CockpitME_323.this.setNew.vspeed = ((199F * CockpitME_323.this.setOld.vspeed) + CockpitME_323.this.fm.getVertSpeed()) / 200F;
            }
            return true;
        }

        Interpolater() {
        }
    }

    private class Variables {

        float      throttle1;
        float      throttle2;
        float      throttle3;
        float      throttle4;
        float      throttle5;
        float      throttle6;
        float      prop1;
        float      prop2;
        float      prop3;
        float      prop4;
        float      prop5;
        float      prop6;
        float      altimeter;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float      vspeed;
        AnglesFork waypointDeviation;

        private Variables() {
            this.azimuth = new AnglesFork();
            this.waypointAzimuth = new AnglesFork();
            this.waypointDeviation = new AnglesFork();
        }
    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        }
        waypoint.getP(this.tmpP);
        this.tmpV.sub(this.tmpP, this.fm.Loc);
        return (float) (Math.toDegrees(Math.atan2(this.tmpV.y, this.tmpV.x)));
    }

    public CockpitME_323() {
        super("3DO/Cockpit/Me323/CockpitME323.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictFlap = 0.0F;
        this.pictManf1 = 1.0F;
        this.pictManf2 = 1.0F;
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        super.cockpitNightMats = (new String[] { "texture01_dmg", "texture01", "texture02_dmg", "texture02", "texture03_dmg", "texture03", "texture04_dmg", "texture04", "texture05_dmg", "texture05", "texture06_dmg", "texture06", "texture21_dmg", "texture21", "texture25" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f) {
        super.mesh.chunkSetAngles("doorL", 0.0F, -35F * this.fm.CT.getCockpitDoor(), 0.0F);
        super.mesh.chunkSetAngles("doorR", 0.0F, -35F * this.fm.CT.getCockpitDoor(), 0.0F);
        super.mesh.chunkSetAngles("Z_Column", 0.0F, -(this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 8F, 0.0F);
        super.mesh.chunkSetAngles("Z_AroneL", 0.0F, -(this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 68F, 0.0F);
        super.mesh.chunkSetAngles("Z_AroneR", 0.0F, -(this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 68F, 0.0F);
        super.mesh.chunkSetAngles("Z_RightPedal", 0.0F, -10F * this.fm.CT.getRudder(), 0.0F);
        super.mesh.chunkSetAngles("Z_LeftPedal", 0.0F, 10F * this.fm.CT.getRudder(), 0.0F);
        super.mesh.chunkSetAngles("Z_RPedalStep", 0.0F, -10F * this.fm.CT.getRudder(), 0.0F);
        super.mesh.chunkSetAngles("Z_LPedalStep", 0.0F, 10F * this.fm.CT.getRudder(), 0.0F);
        super.mesh.chunkSetAngles("zFlaps1", 0.0F, 38F * (this.pictFlap = (0.75F * this.pictFlap) + (0.25F * this.fm.CT.FlapsControl)), 0.0F);
        super.mesh.chunkSetAngles("zThrottle1", 0.0F, -49F * this.interp(this.setNew.throttle1, this.setOld.throttle1, f), 0.0F);
        super.mesh.chunkSetAngles("zThrottle2", 0.0F, -49F * this.interp(this.setNew.throttle2, this.setOld.throttle2, f), 0.0F);
        super.mesh.chunkSetAngles("zThrottle3", 0.0F, -49F * this.interp(this.setNew.throttle3, this.setOld.throttle3, f), 0.0F);
        super.mesh.chunkSetAngles("zThrottle4", 0.0F, -49F * this.interp(this.setNew.throttle4, this.setOld.throttle4, f), 0.0F);
        super.mesh.chunkSetAngles("zThrottle5", 0.0F, -49F * this.interp(this.setNew.throttle5, this.setOld.throttle5, f), 0.0F);
        super.mesh.chunkSetAngles("zThrottle6", 0.0F, -49F * this.interp(this.setNew.throttle6, this.setOld.throttle6, f), 0.0F);
        super.mesh.chunkSetAngles("zFlaps2", 0.0F, 90F * this.fm.CT.getFlap(), 0.0F);
        super.mesh.chunkSetAngles("zHour", 0.0F, this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);
        super.mesh.chunkSetAngles("zMinute", 0.0F, this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        super.mesh.chunkSetAngles("zSecond", 0.0F, this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        super.mesh.chunkSetAngles("zAH1", 0.0F, -this.fm.Or.getKren(), 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.0365F, -0.0365F);
        super.mesh.chunkSetLocate("zAH2", Cockpit.xyz, Cockpit.ypr);
        if ((this.fm.AS.astateCockpitState & 0x40) == 0) {
            super.mesh.chunkSetAngles("Z_Climb1", 0.0F, this.floatindex(this.cvt(this.setNew.vspeed, -30.48F, 30.48F, 0.0F, 12F), CockpitME_323.variometerScale), 0.0F);
        }
        this.w.set(super.fm.getW());
        this.fm.Or.transform(this.w);
        super.mesh.chunkSetAngles("zTurnBank", 0.0F, this.cvt(this.w.z, -0.23562F, 0.23562F, 23F, -23F), 0.0F);
        super.mesh.chunkSetAngles("zBall", 0.0F, this.cvt(this.getBall(6D), -6F, 6F, -11.5F, 11.5F), 0.0F);
        super.mesh.chunkSetAngles("zSpeed", 0.0F, this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, super.fm.getSpeedKMH()), 0.0F, 836.859F, 0.0F, 13F), CockpitME_323.speedometerScale), 0.0F);
        super.mesh.chunkSetAngles("zAlt1", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, 10800F), 0.0F);
        super.mesh.chunkSetAngles("zAlt2", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 9144F, 0.0F, 1080F), 0.0F);
        super.mesh.chunkSetAngles("zCompass1", 0.0F, -this.setNew.azimuth.getDeg(f), 0.0F);
        super.mesh.chunkSetAngles("zCompass2", 0.0F, -0.5F * this.setNew.azimuth.getDeg(f), 0.0F);
        if ((this.fm.AS.astateCockpitState & 0x40) == 0) {
            super.mesh.chunkSetAngles("zMagnetic", 0.0F, this.setNew.azimuth.getDeg(f), 0.0F);
            super.mesh.chunkSetAngles("zNavP", 0.0F, this.setNew.waypointAzimuth.getDeg(f), 0.0F);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) == 0) {
            super.mesh.chunkSetAngles("zRPM1", 0.0F, this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4500F, 0.0F, 323F), 0.0F);
            super.mesh.chunkSetAngles("zRPM2", 0.0F, this.cvt(this.fm.EI.engines[1].getRPM(), 0.0F, 4500F, 0.0F, 323F), 0.0F);
            super.mesh.chunkSetAngles("zRPM3", 0.0F, this.cvt(this.fm.EI.engines[2].getRPM(), 0.0F, 4500F, 0.0F, 323F), 0.0F);
            super.mesh.chunkSetAngles("zRPM4", 0.0F, this.cvt(this.fm.EI.engines[3].getRPM(), 0.0F, 4500F, 0.0F, 323F), 0.0F);
            super.mesh.chunkSetAngles("zRPM5", 0.0F, this.cvt(this.fm.EI.engines[4].getRPM(), 0.0F, 4500F, 0.0F, 323F), 0.0F);
            super.mesh.chunkSetAngles("zRPM6", 0.0F, this.cvt(this.fm.EI.engines[5].getRPM(), 0.0F, 4500F, 0.0F, 323F), 0.0F);
            super.mesh.chunkSetAngles("Z_Pres1", 0.0F, this.pictManf1 = (0.9F * this.pictManf1) + (0.1F * this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.3386378F, 2.539784F, 0.0F, 346.5F)), 0.0F);
            super.mesh.chunkSetAngles("Z_Pres2", 0.0F, this.pictManf2 = (0.9F * this.pictManf2) + (0.1F * this.cvt(this.fm.EI.engines[1].getManifoldPressure(), 0.3386378F, 2.539784F, 0.0F, 346.5F)), 0.0F);
            super.mesh.chunkSetAngles("Z_Pres3", 0.0F, this.pictManf3 = (0.9F * this.pictManf3) + (0.1F * this.cvt(this.fm.EI.engines[2].getManifoldPressure(), 0.3386378F, 2.539784F, 0.0F, 346.5F)), 0.0F);
            super.mesh.chunkSetAngles("Z_Pres4", 0.0F, this.pictManf4 = (0.9F * this.pictManf4) + (0.1F * this.cvt(this.fm.EI.engines[3].getManifoldPressure(), 0.3386378F, 2.539784F, 0.0F, 346.5F)), 0.0F);
            super.mesh.chunkSetAngles("Z_Pres5", 0.0F, this.pictManf5 = (0.9F * this.pictManf5) + (0.1F * this.cvt(this.fm.EI.engines[4].getManifoldPressure(), 0.3386378F, 2.539784F, 0.0F, 346.5F)), 0.0F);
            super.mesh.chunkSetAngles("Z_Pres6", 0.0F, this.pictManf6 = (0.9F * this.pictManf6) + (0.1F * this.cvt(this.fm.EI.engines[5].getManifoldPressure(), 0.3386378F, 2.539784F, 0.0F, 346.5F)), 0.0F);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) == 0) {
            super.mesh.chunkSetAngles("zHydPres", 0.0F, this.fm.Gears.bIsHydroOperable ? 165.5F : 0.0F, 0.0F);
        }
        float f1 = (0.5F * this.fm.EI.engines[0].getRPM()) + (0.5F * this.fm.EI.engines[1].getRPM());
        f1 = 2.5F * (float) Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(f1))));
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 0x80) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            super.mesh.chunkVisible("Pricel_D0", false);
            super.mesh.chunkVisible("Pricel_D1", true);
            super.mesh.chunkVisible("Z_Z_RETICLE", false);
            super.mesh.chunkVisible("Z_Z_MASK", false);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            super.mesh.chunkVisible("XGlassDamage", true);
            super.mesh.chunkVisible("XGlassDamage1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            super.mesh.chunkVisible("Panel_D0", false);
            super.mesh.chunkVisible("Panel_D1", true);
            super.mesh.chunkVisible("zCompass3", false);
            super.mesh.chunkVisible("Z_FuelPres1", false);
            super.mesh.chunkVisible("Z_FuelPres2", false);
            super.mesh.chunkVisible("Z_Oilpres1", false);
            super.mesh.chunkVisible("Z_Oilpres2", false);
            super.mesh.chunkVisible("zOilTemp1", false);
            super.mesh.chunkVisible("zOilTemp2", false);
            super.mesh.chunkVisible("Z_Brkpres1", false);
            super.mesh.chunkVisible("zHydPres", false);
        }
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
            super.mesh.chunkVisible("XGlassDamage", true);
            super.mesh.chunkVisible("XHullDamage2", true);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
            super.mesh.chunkVisible("XGlassDamage2", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            super.mesh.chunkVisible("XHullDamage1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            super.mesh.chunkVisible("XGlassDamage2", true);
        }
        this.retoggleLight();
    }

    public void toggleLight() {
        super.cockpitLightControl = !super.cockpitLightControl;
        if (super.cockpitLightControl) {
            this.setNightMats(true);
        } else {
            this.setNightMats(false);
        }
    }

    private void retoggleLight() {
        if (super.cockpitLightControl) {
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
    private float              pictAiler;
    private float              pictElev;
    private float              pictFlap;
    private float              pictManf1;
    private float              pictManf2;
    private float              pictManf3;
    private float              pictManf4;
    private float              pictManf5;
    private float              pictManf6;
    private static final float speedometerScale[] = { 0.0F, 2.5F, 59F, 126F, 155.5F, 223.5F, 240F, 254.5F, 271F, 285F, 296.5F, 308.5F, 324F, 338.5F };
    private static final float variometerScale[]  = { -180F, -157F, -130F, -108F, -84F, -46.5F, 0.0F, 46.5F, 84F, 108F, 130F, 157F, 180F };
    private Point3d            tmpP;
    private Vector3d           tmpV;
}
