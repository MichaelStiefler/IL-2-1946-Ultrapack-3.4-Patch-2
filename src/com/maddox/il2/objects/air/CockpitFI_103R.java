package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Message;
import com.maddox.rts.Time;

public class CockpitFI_103R extends CockpitPilot {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitFI_103R.this.fm != null) {
                CockpitFI_103R.this.setTmp = CockpitFI_103R.this.setOld;
                CockpitFI_103R.this.setOld = CockpitFI_103R.this.setNew;
                CockpitFI_103R.this.setNew = CockpitFI_103R.this.setTmp;
                CockpitFI_103R.this.setNew.throttle = ((10F * CockpitFI_103R.this.setOld.throttle) + CockpitFI_103R.this.fm.CT.PowerControl) / 11F;
                CockpitFI_103R.this.setNew.altimeter = CockpitFI_103R.this.fm.getAltitude();
            }
            return true;
        }

        Interpolater() {
        }
    }

    private class Variables {

        float throttle;
        float altimeter;

        private Variables() {
        }

    }

    public CockpitFI_103R() {
        super("3DO/Cockpit/BI-1/CockpitFI_103R.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.cockpitNightMats = (new String[] { "ONE", "TWO", "THREE_FI-103" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), (Object) null, Time.current(), (Message) null);
    }

    public void reflectWorldToInstruments(float f) {
        this.mesh.chunkSetAngles("Stick", 0.0F, (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 15F, (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 10F);
        this.mesh.chunkSetAngles("Ped_Base", this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("PedalL", 0.0F, this.fm.CT.getRudder() * 15F, 0.0F);
        this.mesh.chunkSetAngles("PedalR", 0.0F, this.fm.CT.getRudder() * 15F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.ypr[1] = -80.08F * this.interp(this.setNew.throttle, this.setOld.throttle, f);
        Cockpit.xyz[1] = Cockpit.ypr[1] < -33F ? -0.0065F : 0.0F;
        this.mesh.chunkSetLocate("Throttle", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("zAlt1a", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 360F), 0.0F);
        this.mesh.chunkSetAngles("zAlt1b", 0.0F, this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F);
        this.mesh.chunkSetAngles("zSpeed1a", 0.0F, this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 800F, 0.0F, 16F), CockpitFI_103R.speedometerScale), 0.0F);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("zTurn1a", 0.0F, this.cvt(this.w.z, -0.47124F, 0.47124F, 40F, -40F), 0.0F);
        this.mesh.chunkSetAngles("zSlide1a", this.cvt(this.getBall(8D), -8F, 8F, 22.5F, -22.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zGas1a", 0.0F, this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 300F, 0.0F, 180F), 0.0F);
        this.mesh.chunkVisible("Z_Red8", (this.fm.CT.getGear() > 0.05F) && (this.fm.CT.getGear() < 0.95F));
        this.mesh.chunkVisible("Z_Red5", this.fm.CT.getGear() == 0.0F);
        this.mesh.chunkVisible("Z_Red7", this.fm.CT.getGear() == 0.0F);
        this.mesh.chunkVisible("Z_Red4", this.fm.CT.getGear() == 1.0F);
        this.mesh.chunkVisible("Z_Red6", this.fm.CT.getGear() == 1.0F);
        this.mesh.chunkSetAngles("zMinute", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zHour", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSecond", -this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
    }

    public void reflectCockpitState() {
        if (((this.fm.AS.astateCockpitState & 0x40) != 0) || ((this.fm.AS.astateCockpitState & 4) != 0) || ((this.fm.AS.astateCockpitState & 0x10) != 0) || ((this.fm.AS.astateCockpitState & 2) != 0)) {
            this.mesh.materialReplace("ONE", "ONE_D1");
            this.mesh.materialReplace("ONE_night", "ONE_D1_night");
            this.mesh.materialReplace("Dash", "Dash_D1");
            this.mesh.chunkVisible("zAlt1a", false);
            this.mesh.chunkVisible("zAlt1b", false);
            this.mesh.chunkVisible("zSpeed1a", false);
            this.mesh.chunkVisible("zSecond", false);
            this.mesh.chunkVisible("zMinute", false);
            this.mesh.chunkVisible("zHour", false);
        }
        if (((this.fm.AS.astateCockpitState & 8) != 0) || ((this.fm.AS.astateCockpitState & 0x20) != 0) || ((this.fm.AS.astateCockpitState & 1) != 0) || ((this.fm.AS.astateCockpitState & 0x80) != 0)) {
            this.mesh.materialReplace("THREE_FI-103", "THREE_D1_FI-103");
            this.mesh.materialReplace("THREE_night_FI-103", "THREE_D1_night_FI-103");
            this.mesh.chunkVisible("zSlide1a", false);
        }
    }

    private static final float speedometerScale[] = { 0.0F, -12.33333F, 18.5F, 37F, 62.5F, 90F, 110.5F, 134F, 158.5F, 186F, 212.5F, 238.5F, 265F, 289.5F, 315F, 339.5F, 346F, 346F };
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
}
