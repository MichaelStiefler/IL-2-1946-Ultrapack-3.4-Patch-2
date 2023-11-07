package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Time;

public class CockpitP_51D20N9 extends CockpitPilot {
    class Interpolater extends InterpolateRef {
        Interpolater() {
        }

        public boolean tick() {
            if (CockpitP_51D20N9.this.fm != null) {
                CockpitP_51D20N9.this.setTmp = CockpitP_51D20N9.this.setOld;
                CockpitP_51D20N9.this.setOld = CockpitP_51D20N9.this.setNew;
                CockpitP_51D20N9.this.setNew = CockpitP_51D20N9.this.setTmp;

                CockpitP_51D20N9.this.setNew.throttle = ((0.85F * CockpitP_51D20N9.this.setOld.throttle) + (CockpitP_51D20N9.this.fm.CT.PowerControl * 0.15F));
                CockpitP_51D20N9.this.setNew.prop = ((0.85F * CockpitP_51D20N9.this.setOld.prop) + (CockpitP_51D20N9.this.fm.CT.getStepControl() * 0.15F));
                CockpitP_51D20N9.this.setNew.stage = ((0.85F * CockpitP_51D20N9.this.setOld.stage) + (CockpitP_51D20N9.this.fm.EI.engines[0].getControlCompressor() * 0.15F));
                CockpitP_51D20N9.this.setNew.mix = ((0.85F * CockpitP_51D20N9.this.setOld.mix) + (CockpitP_51D20N9.this.fm.EI.engines[0].getControlMix() * 0.15F));
                CockpitP_51D20N9.this.setNew.altimeter = CockpitP_51D20N9.this.fm.getAltitude();

                float f = CockpitP_51D20N9.this.waypointAzimuth(10.0F);
                CockpitP_51D20N9.this.setNew.azimuth.setDeg(CockpitP_51D20N9.this.setOld.azimuth.getDeg(1.0F), CockpitP_51D20N9.this.fm.Or.azimut());
                CockpitP_51D20N9.this.setNew.waypointAzimuth.setDeg(CockpitP_51D20N9.this.setOld.waypointAzimuth.getDeg(0.1F), f);

                CockpitP_51D20N9.this.setNew.vspeed = (((199.0F * CockpitP_51D20N9.this.setOld.vspeed) + CockpitP_51D20N9.this.fm.getVertSpeed()) / 200.0F);
            }

            return true;
        }
    }

    private class Variables {
        float      throttle;
        float      prop;
        float      mix;
        float      stage;
        float      altimeter;
        float      vspeed;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;

        private Variables() {
            this.azimuth = new AnglesFork();
            this.waypointAzimuth = new AnglesFork();
        }
    }

    protected float waypointAzimuth() {
        return this.waypointAzimuthInvertMinus(30.0F);
    }

    public CockpitP_51D20N9() {
        super("3DO/Cockpit/P-51D-20(N9)/hier.him", "bf109");

        this.cockpitNightMats = new String[] { "Fuel", "Textur1", "Textur2", "Textur3", "Textur4", "Textur5", "Textur6", "Textur8" };

        this.setNightMats(false);

        this.interpPut(new Interpolater(), null, Time.current(), null);
        AircraftLH.printCompassHeading = true;
    }

    public void reflectWorldToInstruments(float paramFloat) {
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.55F);
        this.mesh.chunkSetLocate("Blister_D0", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Trim1", 722.0F * this.fm.CT.getTrimAileronControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Trim2", 722.0F * this.fm.CT.getTrimRudderControl(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Trim3", -722.0F * this.fm.CT.getTrimElevatorControl(), 0.0F, 0.0F);

        this.mesh.chunkSetAngles("Z_Flaps1", 21.0F * this.fm.CT.FlapsControl, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Gear1", -30.0F + (30.0F * this.fm.CT.GearControl), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throtle1", 77.0F * this.setNew.throttle, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Prop1", 83.3F * this.setNew.prop, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture1", 66.0F * this.setNew.mix, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal", 15.0F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPedalStep", 15.0F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal2", 15.0F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", -15.0F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LPedalStep", -15.0F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal2", -15.0F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Columnbase", (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 20.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 16.0F, 0.0F, 0.0F);

        this.mesh.chunkSetAngles("Z_TurnBank2", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.Or.getTangage(), -45.0F, 45.0F, 0.0362F, -0.0362F);
        this.mesh.chunkSetLocate("Z_TurnBank2a", Cockpit.xyz, Cockpit.ypr);

        this.mesh.chunkSetAngles("Z_Altimeter1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, paramFloat), 0.0F, 30480.0F, 0.0F, 36000.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, paramFloat), 0.0F, 30480.0F, 0.0F, 3600.0F), 0.0F, 0.0F);

        this.mesh.chunkSetAngles("Z_Speedometer1", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 1126.5409F, 0.0F, 14.0F), CockpitP_51D20N9.speedometerScale), 0.0F, 0.0F);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.cvt(this.w.z, -0.23562F, 0.23562F, 22.5F, -22.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank3", this.cvt(this.getBall(7.0D), -7.0F, 7.0F, 14.0F, -14.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank4", this.cvt(this.getBall(7.0D), -7.0F, 7.0F, 14.0F, -14.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Climb1", this.floatindex(this.cvt(this.setNew.vspeed, -30.48F, 30.48F, 0.0F, 12.0F), CockpitP_51D20N9.variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Heading1", this.setNew.azimuth.getDeg(paramFloat), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass1", 90.0F + this.setNew.azimuth.getDeg(paramFloat), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass2", this.setNew.waypointAzimuth.getDeg(paramFloat * 0.1F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM1", this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4500.0F, 0.0F, 316.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Hour1", this.cvt(World.getTimeofDay(), 0.0F, 24.0F, 0.0F, 720.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel1", this.cvt(this.fm.M.fuel > 1.0F ? 0.26F : 0.0F, 0.0F, 0.35F, 0.0F, 180.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel2", this.cvt(this.fm.M.fuel, 0.0F, 245.2F, 0.0F, 180.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel3", this.cvt(this.fm.M.fuel, 245.2F, 490.4F, 0.0F, 180.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel4", this.cvt(this.fm.M.fuel, 245.2F, 490.4F, 0.0F, 180.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp1", this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 100.0F, 0.0F, 180.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Pres1", this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.3386378F, 2.370465F, 0.0F, 320.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oil1", this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness()), 0.0F, 14.0F, 0.0F, 180.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Coolant1", this.cvt(this.fm.EI.engines[0].tWaterOut, 40.0F, 150.0F, 0.0F, 130.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Carbair1", this.cvt(Atmosphere.temperature((float) this.fm.Loc.z) - 273.14999F, -50.0F, 50.0F, -60.0F, 60.0F), 0.0F, 0.0F);
        float f = this.fm.EI.engines[0].getRPM();
        f = 2.5F * (float) Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(f))));
        this.mesh.chunkSetAngles("Z_Suction1", this.cvt(f, 0.0F, 10.0F, 0.0F, 300.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oxypres1", 142.5F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oilpres1", this.cvt(this.fm.EI.engines[0].getReadyness(), 0.0F, 2.0F, 0.0F, 180.0F), 0.0F, 0.0F);

        this.mesh.chunkVisible("Z_GearGreen1", this.fm.CT.getGear() > 0.95F);
        this.mesh.chunkVisible("Z_GearRed1", (this.fm.CT.getGear() < 0.05F) || (!this.fm.Gears.lgear) || (!this.fm.Gears.rgear));
        this.mesh.chunkVisible("Z_Supercharger1", this.fm.EI.engines[0].getControlCompressor() > 0);
    }

    public void reflectCockpitState() {
        if (((this.fm.AS.astateCockpitState & 0x2) == 0) || (((this.fm.AS.astateCockpitState & 0x1) == 0) || ((this.fm.AS.astateCockpitState & 0x40) != 0))) {
            this.mesh.chunkVisible("Panel_D0", false);
            this.mesh.chunkVisible("Panel_D1", true);
        }
        if (((this.fm.AS.astateCockpitState & 0x4) == 0) || (((this.fm.AS.astateCockpitState & 0x8) == 0) || ((this.fm.AS.astateCockpitState & 0x80) != 0))) {
            this.mesh.chunkVisible("Z_OilSplats_D1", true);
        }
        if (((this.fm.AS.astateCockpitState & 0x10) == 0) || ((this.fm.AS.astateCockpitState & 0x20) != 0)) {
            ;
        }
        this.retoggleLight();
    }

    public void toggleLight() {
        this.cockpitLightControl = (!this.cockpitLightControl);
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

    private Variables            setOld           = new Variables();
    private Variables            setNew           = new Variables();
    private Variables            setTmp;
    public Vector3f              w                = new Vector3f();
    private float                pictAiler        = 0.0F;
    private float                pictElev         = 0.0F;

    private static final float[] speedometerScale = { 0.0F, 5.0F, 47.5F, 92.0F, 134.0F, 180.0F, 227.0F, 241.0F, 255.0F, 272.5F, 287.0F, 299.5F, 312.5F, 325.5F, 338.5F };

    private static final float[] variometerScale  = { -170.0F, -147.0F, -124.0F, -101.0F, -78.0F, -48.0F, 0.0F, 48.0F, 78.0F, 101.0F, 124.0F, 147.0F, 170.0F };

}
