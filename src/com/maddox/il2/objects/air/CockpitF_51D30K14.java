package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitF_51D30K14 extends CockpitPilot {
    class Interpolater extends com.maddox.il2.engine.InterpolateRef {

        public boolean tick() {
            if (CockpitF_51D30K14.this.fm != null) {
                CockpitF_51D30K14.this.setTmp = CockpitF_51D30K14.this.setOld;
                CockpitF_51D30K14.this.setOld = CockpitF_51D30K14.this.setNew;
                CockpitF_51D30K14.this.setNew = CockpitF_51D30K14.this.setTmp;
                CockpitF_51D30K14.this.setNew.throttle = (0.85F * CockpitF_51D30K14.this.setOld.throttle) + (CockpitF_51D30K14.this.fm.CT.PowerControl * 0.15F);
                CockpitF_51D30K14.this.setNew.prop = (0.85F * CockpitF_51D30K14.this.setOld.prop) + (CockpitF_51D30K14.this.fm.CT.getStepControl() * 0.15F);
                CockpitF_51D30K14.this.setNew.stage = (0.85F * CockpitF_51D30K14.this.setOld.stage) + (CockpitF_51D30K14.this.fm.EI.engines[0].getControlCompressor() * 0.15F);
                CockpitF_51D30K14.this.setNew.mix = (0.85F * CockpitF_51D30K14.this.setOld.mix) + (CockpitF_51D30K14.this.fm.EI.engines[0].getControlMix() * 0.15F);
                CockpitF_51D30K14.this.setNew.altimeter = CockpitF_51D30K14.this.fm.getAltitude();
                CockpitF_51D30K14.this.setNew.azimuth = ((35F * CockpitF_51D30K14.this.setOld.azimuth) + -CockpitF_51D30K14.this.fm.Or.getYaw()) / 36F;
                if ((CockpitF_51D30K14.this.setOld.azimuth > 270F) && (CockpitF_51D30K14.this.setNew.azimuth < 90F)) {
                    CockpitF_51D30K14.this.setOld.azimuth -= 360F;
                }
                if ((CockpitF_51D30K14.this.setOld.azimuth < 90F) && (CockpitF_51D30K14.this.setNew.azimuth > 270F)) {
                    CockpitF_51D30K14.this.setOld.azimuth += 360F;
                }
                CockpitF_51D30K14.this.setNew.waypointAzimuth = ((10F * CockpitF_51D30K14.this.setOld.waypointAzimuth) + CockpitF_51D30K14.this.waypointAzimuth() + World.Rnd().nextFloat(-30F, 30F)) / 11F;
                CockpitF_51D30K14.this.setNew.vspeed = ((199F * CockpitF_51D30K14.this.setOld.vspeed) + CockpitF_51D30K14.this.fm.getVertSpeed()) / 200F;
                float f = ((F_51D30NA) CockpitF_51D30K14.this.aircraft()).k14Distance;
                CockpitF_51D30K14.this.setNew.k14w = (5F * CockpitF_51D30K14.k14TargetWingspanScale[((F_51D30NA) CockpitF_51D30K14.this.aircraft()).k14WingspanType]) / f;
                CockpitF_51D30K14.this.setNew.k14w = (0.9F * CockpitF_51D30K14.this.setOld.k14w) + (0.1F * CockpitF_51D30K14.this.setNew.k14w);
                CockpitF_51D30K14.this.setNew.k14wingspan = (0.9F * CockpitF_51D30K14.this.setOld.k14wingspan) + (0.1F * CockpitF_51D30K14.k14TargetMarkScale[((F_51D30NA) CockpitF_51D30K14.this.aircraft()).k14WingspanType]);
                CockpitF_51D30K14.this.setNew.k14mode = (0.8F * CockpitF_51D30K14.this.setOld.k14mode) + (0.2F * ((F_51D30NA) CockpitF_51D30K14.this.aircraft()).k14Mode);
                Vector3d vector3d = CockpitF_51D30K14.this.aircraft().FM.getW();
                double d = 0.00125D * f;
                float f1 = (float) Math.toDegrees(d * vector3d.z);
                float f2 = -(float) Math.toDegrees(d * vector3d.y);
                float f3 = CockpitF_51D30K14.this.floatindex((f - 200F) * 0.04F, CockpitF_51D30K14.k14BulletDrop) - CockpitF_51D30K14.k14BulletDrop[0];
                f2 += (float) Math.toDegrees(Math.atan(f3 / f));
                CockpitF_51D30K14.this.setNew.k14x = (0.92F * CockpitF_51D30K14.this.setOld.k14x) + (0.08F * f1);
                CockpitF_51D30K14.this.setNew.k14y = (0.92F * CockpitF_51D30K14.this.setOld.k14y) + (0.08F * f2);
                if (CockpitF_51D30K14.this.setNew.k14x > 7F) {
                    CockpitF_51D30K14.this.setNew.k14x = 7F;
                }
                if (CockpitF_51D30K14.this.setNew.k14x < -7F) {
                    CockpitF_51D30K14.this.setNew.k14x = -7F;
                }
                if (CockpitF_51D30K14.this.setNew.k14y > 7F) {
                    CockpitF_51D30K14.this.setNew.k14y = 7F;
                }
                if (CockpitF_51D30K14.this.setNew.k14y < -7F) {
                    CockpitF_51D30K14.this.setNew.k14y = -7F;
                }
            }
            return true;
        }

        Interpolater() {
        }
    }

    private class Variables {

        float throttle;
        float prop;
        float mix;
        float stage;
        float altimeter;
        float azimuth;
        float vspeed;
        float waypointAzimuth;
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
            waypoint.getP(this.tmpP);
            this.tmpV.sub(this.tmpP, this.fm.Loc);
            return (float) (57.295779513082323D * Math.atan2(-this.tmpV.y, this.tmpV.x));
        }
    }

    public CockpitF_51D30K14() {
        super("3DO/Cockpit/F-51D-30(K14)/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.cockpitNightMats = (new String[] { "Fuel", "Textur1", "Textur2", "Textur3", "Textur4", "Textur5", "Textur6", "Textur8" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void removeCanopy() {
        this.mesh.chunkVisible("Blister_D0", false);
    }

    public void reflectWorldToInstruments(float f) {
        int i = ((F_51D30NA) this.aircraft()).k14Mode;
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

        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.55F);
        this.mesh.chunkSetLocate("Blister_D0", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Trim1", 722F * this.fm.CT.getTrimAileronControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Trim2", 722F * this.fm.CT.getTrimRudderControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Trim3", -722F * this.fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Flaps1", 21F * this.fm.CT.FlapsControl, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Gear1", -30F + (30F * this.fm.CT.GearControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throtle1", 77F * this.setNew.throttle, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop1", 83.3F * this.setNew.prop, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture1", 66F * this.setNew.mix, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal", 15F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPedalStep", 15F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal2", 15F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", -15F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LPedalStep", -15F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal2", -15F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Columnbase", (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 20F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 16F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Target1", this.setNew.k14wingspan, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mode1", -90F * this.setNew.k14mode, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank2", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.0362F, -0.0362F);
        this.mesh.chunkSetLocate("Z_TurnBank2a", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Altimeter1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 30480F, 0.0F, 36000F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 30480F, 0.0F, 3600F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter3", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 30480F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer1", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 1126.541F, 0.0F, 14F), CockpitF_51D30K14.speedometerScale), 0.0F, 0.0F);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.cvt(this.w.z, -0.23562F, 0.23562F, 22.5F, -22.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank3", this.cvt(this.getBall(7D), -7F, 7F, 14F, -14F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Climb1", this.floatindex(this.cvt(this.setNew.vspeed, -30.48F, 30.48F, 0.0F, 12F), CockpitF_51D30K14.variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Heading1", this.setNew.azimuth, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass2", 90F + this.setNew.azimuth, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass1", 90F + this.interp(this.setNew.waypointAzimuth, this.setOld.waypointAzimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM1", this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4500F, 0.0F, 316F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Hour1", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel1", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 0.35F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel2", this.cvt(this.fm.M.fuel, 0.0F, 245.2F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel3", this.cvt(this.fm.M.fuel, 245.2F, 490.4F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel4", this.cvt(this.fm.M.fuel, 245.2F, 490.4F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp1", this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 100F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Pres1", this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.3386378F, 2.5395F, 0.0F, 345F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oil1", this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness()), 0.0F, 14F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Coolant1", this.cvt(this.fm.EI.engines[0].tWaterOut, 40F, 150F, 0.0F, 130F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Carbair1", this.cvt(Atmosphere.temperature((float) this.fm.Loc.z) - 273.15F, -50F, 50F, -60F, 60F), 0.0F, 0.0F);
        float f1 = this.fm.EI.engines[0].getRPM();
        f1 = 2.5F * (float) Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(f1))));
        this.mesh.chunkSetAngles("Z_Suction1", this.cvt(f1, 0.0F, 10F, 0.0F, 300F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oxypres1", 142.5F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oilpres1", this.cvt(this.fm.EI.engines[0].getReadyness(), 0.0F, 2.0F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkVisible("Z_GearGreen1", this.fm.CT.getGear() > 0.95F);
        this.mesh.chunkVisible("Z_GearRed1", (this.fm.CT.getGear() < 0.05F) || !this.fm.Gears.lgear || !this.fm.Gears.rgear);
        this.mesh.chunkVisible("Z_Supercharger1", this.fm.EI.engines[0].getControlCompressor() > 0);
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 2) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 1) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("Panel_D0", false);
            this.mesh.chunkVisible("Panel_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 4) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 8) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            this.mesh.chunkVisible("Z_OilSplats_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x10) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 0x20) == 0) {
            ;
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

    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
    private static final float speedometerScale[]       = { 0.0F, 5F, 47.5F, 92F, 134F, 180F, 227F, 241F, 255F, 272.5F, 287F, 299.5F, 312.5F, 325.5F, 338.5F };
    private static final float variometerScale[]        = { -170F, -147F, -124F, -101F, -78F, -48F, 0.0F, 48F, 78F, 101F, 124F, 147F, 170F };
    private static final float k14TargetMarkScale[]     = { -0F, -4.5F, -27.5F, -42.5F, -56.5F, -61.5F, -70F, -95F, -102.5F, -106F };
    private static final float k14TargetWingspanScale[] = { 9.9F, 10.52F, 13.8F, 16.34F, 19F, 20F, 22F, 29.25F, 30F, 32.85F };
    private static final float k14BulletDrop[]          = { 5.812F, 6.168F, 6.508F, 6.978F, 7.24F, 7.576F, 7.849F, 8.108F, 8.473F, 8.699F, 8.911F, 9.111F, 9.384F, 9.554F, 9.787F, 9.928F, 9.992F, 10.282F, 10.381F, 10.513F, 10.603F, 10.704F, 10.739F, 10.782F, 10.789F };
    private Point3d            tmpP;
    private Vector3d           tmpV;

}
