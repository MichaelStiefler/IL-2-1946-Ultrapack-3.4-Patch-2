package com.maddox.il2.objects.air;

import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitFW_190A1 extends CockpitPilot {
    private class Variables {

        float altimeter;
        float throttle;
        float dimPosition;
        float azimuth;
        float waypointAzimuth;
        float vspeed;

        private Variables() {
        }

    }

    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitFW_190A1.this.fm != null) {
                CockpitFW_190A1.this.setTmp = CockpitFW_190A1.this.setOld;
                CockpitFW_190A1.this.setOld = CockpitFW_190A1.this.setNew;
                CockpitFW_190A1.this.setNew = CockpitFW_190A1.this.setTmp;
                CockpitFW_190A1.this.setNew.altimeter = CockpitFW_190A1.this.fm.getAltitude();
                if (CockpitFW_190A1.this.cockpitDimControl) {
                    if (CockpitFW_190A1.this.setNew.dimPosition > 0.0F) CockpitFW_190A1.this.setNew.dimPosition = CockpitFW_190A1.this.setOld.dimPosition - 0.05F;
                } else if (CockpitFW_190A1.this.setNew.dimPosition < 1.0F) CockpitFW_190A1.this.setNew.dimPosition = CockpitFW_190A1.this.setOld.dimPosition + 0.05F;
                CockpitFW_190A1.this.setNew.throttle = (10F * CockpitFW_190A1.this.setOld.throttle + CockpitFW_190A1.this.fm.CT.PowerControl) / 11F;
                CockpitFW_190A1.this.setNew.vspeed = (499F * CockpitFW_190A1.this.setOld.vspeed + CockpitFW_190A1.this.fm.getVertSpeed()) / 500F;
                CockpitFW_190A1.this.setNew.azimuth = CockpitFW_190A1.this.fm.Or.getYaw();
                if (CockpitFW_190A1.this.setOld.azimuth > 270F && CockpitFW_190A1.this.setNew.azimuth < 90F) CockpitFW_190A1.this.setOld.azimuth -= 360F;
                if (CockpitFW_190A1.this.setOld.azimuth < 90F && CockpitFW_190A1.this.setNew.azimuth > 270F) CockpitFW_190A1.this.setOld.azimuth += 360F;
                CockpitFW_190A1.this.setNew.waypointAzimuth = (10F * CockpitFW_190A1.this.setOld.waypointAzimuth + (CockpitFW_190A1.this.waypointAzimuth() - CockpitFW_190A1.this.setOld.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
            }
            return true;
        }

        Interpolater() {
        }
    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) return 0.0F;
        else {
            waypoint.getP(Cockpit.P1);
            Cockpit.V.sub(Cockpit.P1, this.fm.Loc);
            return (float) (57.295779513082323D * Math.atan2(Cockpit.V.y, Cockpit.V.x));
        }
    }

    public CockpitFW_190A1() {
        super("3DO/Cockpit/FW-190A-1/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.setNew.dimPosition = 1.0F;
        HookNamed hooknamed = new HookNamed(this.mesh, "LIGHTHOOK_L");
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        this.light1 = new LightPointActor(new LightPoint(), loc.getPoint());
        this.light1.light.setColor(126F, 232F, 245F);
        this.light1.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK1", this.light1);
        hooknamed = new HookNamed(this.mesh, "LIGHTHOOK_R");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        this.light2 = new LightPointActor(new LightPoint(), loc.getPoint());
        this.light2.light.setColor(126F, 232F, 245F);
        this.light2.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK2", this.light2);
        this.cockpitNightMats = new String[] { "A4GP1", "A4GP2", "A4GP3", "A4GP4", "A4GP5", "A4GP6", "A5GP3Km" };
        this.setNightMats(false);
        this.cockpitDimControl = !this.cockpitDimControl;
        this.interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.gun[0] == null) {
            this.gun[0] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN01");
            this.gun[1] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN02");
            this.gun[2] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON03");
            this.gun[3] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON04");
            this.gun[4] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON01");
            this.gun[5] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON02");
        }
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.5F);
        Cockpit.xyz[2] = this.cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 0.01F);
        Cockpit.ypr[2] = this.cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, -2F);
        this.mesh.chunkSetLocate("Canopy", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("NeedleALT", -this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleALTKm", 0.0F, 0.0F, this.cvt(this.setNew.altimeter, 0.0F, 10000F, 0.0F, -180F));
        this.mesh.chunkSetAngles("NeedleManPress", -this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 336F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleKMH", -this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleRPM", -this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleFuel", this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 400F, 0.0F, 4F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOilTemp", this.floatindex(this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 3F), oilTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleFuelPress", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 3F, 0.0F, 135F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOilPress", -this.cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
        float f1;
        if (this.aircraft().isFMTrackMirror()) f1 = this.aircraft().fmTrack().getCockpitAzimuthSpeed();
        else {
            f1 = this.cvt((this.setNew.azimuth - this.setOld.azimuth) / Time.tickLenFs(), -6F, 6F, -50F, 50F);
            if (this.aircraft().fmTrack() != null) this.aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
        }
        this.mesh.chunkSetAngles("NeedleAHTurn", f1, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAHBank", this.cvt(this.getBall(7D), -7F, 7F, 11F, -11F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAHCyl", 0.0F, 0.0F, this.fm.Or.getKren());
        this.mesh.chunkSetAngles("NeedleAHBar", 0.0F, 0.0F, this.cvt(this.fm.Or.getTangage(), -45F, 45F, 12F, -12F));
        this.mesh.chunkSetAngles("NeedleCD", this.setNew.vspeed < 0.0F ? this.floatindex(this.cvt(-this.setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale) : -this.floatindex(this.cvt(this.setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale), 0.0F,
                0.0F);
        this.mesh.chunkSetAngles("RepeaterOuter", -this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RepeaterPlane", this.interp(this.setNew.waypointAzimuth, this.setOld.waypointAzimuth, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleHBSmall", -105F + (float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 5F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleHBLarge", -270F + (float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 60F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleTrimmung", this.fm.CT.getTrimElevatorControl() * 25F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleHClock", -this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleMClock", -this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleSClock", -this.cvt(World.getTimeofDay() % 1.0F * 60F % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.resetYPRmodifier();
        if (this.gun[0] != null) {
            Cockpit.xyz[0] = this.cvt(this.gun[0].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG17_L", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG17_1", !this.gun[0].haveBullets());
        }
        if (this.gun[1] != null) {
            Cockpit.xyz[0] = this.cvt(this.gun[1].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG17_R", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG17_2", !this.gun[1].haveBullets());
        }
        if (this.gun[4] != null) {
            Cockpit.xyz[0] = this.cvt(this.gun[4].countBullets(), 0.0F, 200F, -0.017F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG151_L", Cockpit.xyz, Cockpit.ypr);
        }
        if (this.gun[5] != null) {
            Cockpit.xyz[0] = this.cvt(this.gun[5].countBullets(), 0.0F, 200F, -0.017F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG151_R", Cockpit.xyz, Cockpit.ypr);
        }
        if (this.gun[2] != null) {
            Cockpit.xyz[0] = this.cvt(this.gun[2].countBullets(), 0.0F, 55F, -0.018F, 0.0F);
            this.mesh.chunkSetLocate("RC_MGFF_WingL", Cockpit.xyz, Cockpit.ypr);
        }
        if (this.gun[3] != null) {
            Cockpit.xyz[0] = this.cvt(this.gun[3].countBullets(), 0.0F, 55F, -0.018F, 0.0F);
            this.mesh.chunkSetLocate("RC_MGFF_WingR", Cockpit.xyz, Cockpit.ypr);
        }
        this.mesh.chunkSetAngles("IgnitionSwitch", 24F * this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, 0.058F);
        this.mesh.chunkSetLocate("Revi16Tinter", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Stick", 0.0F, (this.pictAiler = 0.85F * this.pictAiler + 0.15F * this.fm.CT.AileronControl) * 20F, (this.pictElev = 0.85F * this.pictElev + 0.15F * this.fm.CT.ElevatorControl) * 20F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = this.fm.CT.WeaponControl[1] ? -0.004F : 0.0F;
        this.mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
        Cockpit.ypr[0] = this.interp(this.setNew.throttle, this.setOld.throttle, f) * 34F * 0.91F;
        Cockpit.xyz[2] = Cockpit.ypr[0] <= 7F ? 0.0F : -0.006F;
        this.mesh.chunkSetLocate("ThrottleQuad", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("RPedalBase", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("RPedalStrut", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("RPedal", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F - this.fm.CT.getBrake() * 15F);
        this.mesh.chunkSetAngles("LPedalBase", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("LPedalStrut", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("LPedal", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F - this.fm.CT.getBrake() * 15F);
        this.mesh.chunkVisible("XLampTankSwitch", this.fm.M.fuel > 144F);
        this.mesh.chunkVisible("XLampFuelLow", this.fm.M.fuel < 43.2F);
        this.mesh.chunkVisible("XLampFlapLPos_3", this.fm.CT.getFlap() > 0.9F);
        this.mesh.chunkVisible("XLampFlapLPos_2", this.fm.CT.getFlap() > 0.1F && this.fm.CT.getFlap() < 0.5F);
        this.mesh.chunkVisible("XLampFlapLPos_1", this.fm.CT.getFlap() < 0.1F);
        this.mesh.chunkVisible("XLampFlapRPos_3", this.fm.CT.getFlap() > 0.9F);
        this.mesh.chunkVisible("XLampFlapRPos_2", this.fm.CT.getFlap() > 0.1F && this.fm.CT.getFlap() < 0.5F);
        this.mesh.chunkVisible("XLampFlapRPos_1", this.fm.CT.getFlap() < 0.1F);
        this.mesh.chunkVisible("XLampGearL_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearL_2", this.fm.CT.getGear() > 0.95F && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearR_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearR_2", this.fm.CT.getGear() > 0.95F && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearC_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearC_2", this.fm.CT.getGear() > 0.95F);
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void toggleLight() {
        this.cockpitLightControl = !this.cockpitLightControl;
        if (this.cockpitLightControl) {
            this.light1.light.setEmit(0.005F, 0.75F);
            this.light2.light.setEmit(0.005F, 0.75F);
            this.setNightMats(true);
        } else {
            this.light1.light.setEmit(0.0F, 0.0F);
            this.light2.light.setEmit(0.0F, 0.0F);
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
        if ((this.fm.AS.astateCockpitState & 4) != 0 || (this.fm.AS.astateCockpitState & 0x10) != 0 || (this.fm.AS.astateCockpitState & 2) != 0 || (this.fm.AS.astateCockpitState & 1) != 0) {
            if ((this.fm.AS.astateCockpitState & 2) != 0) {
                this.mesh.chunkVisible("Revi16", false);
                this.mesh.chunkVisible("Revi16Tinter", false);
                this.mesh.chunkVisible("Z_Z_MASK", false);
                this.mesh.chunkVisible("Z_Z_RETICLE", false);
                this.mesh.chunkVisible("DRevi16", true);
            }
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.materialReplace("A4GP1", "DA4GP1");
            this.mesh.materialReplace("A4GP1_night", "DA4GP1_night");
            this.mesh.chunkVisible("NeedleRPM", false);
            this.mesh.chunkVisible("NeedleManPress", false);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0 || (this.fm.AS.astateCockpitState & 0x20) != 0 || (this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("XGlassDamage2", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("HullDamage3", true);
            this.mesh.materialReplace("A4GP2", "DA4GP2");
            this.mesh.materialReplace("A4GP2_night", "DA4GP2_night");
            this.mesh.chunkVisible("NeedleAHBank", false);
            this.resetYPRmodifier();
            Cockpit.xyz[0] = -0.01F;
            Cockpit.xyz[1] = -0.01F;
            Cockpit.ypr[0] = -5F;
            Cockpit.ypr[2] = -5F;
            this.mesh.chunkSetLocate("IPCentral", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("NeedleOilPress", false);
            this.mesh.chunkVisible("NeedleFuelPress", false);
        }
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.chunkVisible("HullDamage2", true);
            this.mesh.materialReplace("A4GP3", "DA4GP3");
            this.mesh.materialReplace("A4GP3_night", "DA4GP3_night");
            this.mesh.chunkVisible("NeedleALT", false);
            this.mesh.chunkVisible("NeedleKMH", false);
        }
        this.retoggleLight();
    }

    private Gun                gun[]              = { null, null, null, null, null, null };
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    private LightPointActor    light1;
    private LightPointActor    light2;
    private float              pictAiler;
    private float              pictElev;
    private static final float speedometerScale[] = { 0.0F, 18.5F, 67F, 117F, 164F, 215F, 267F, 320F, 379F, 427F, 428F };
    private static final float rpmScale[]         = { 0.0F, 11.25F, 53F, 108F, 170F, 229F, 282F, 334F, 342.5F, 342.5F };
    private static final float fuelScale[]        = { 0.0F, 16F, 35F, 52.5F, 72F, 72F };
    private static final float vsiNeedleScale[]   = { 0.0F, 48F, 82F, 96.5F, 111F, 120.5F, 130F, 130F };
    private static final float oilTempScale[]     = { 0.0F, 23F, 52F, 81F, 81F };

    static {
        Property.set(CockpitFW_190A1.class, "normZN", 0.72F);
        Property.set(CockpitFW_190A1.class, "gsZN", 0.66F);
    }
}
