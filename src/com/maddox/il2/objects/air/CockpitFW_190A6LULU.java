package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.il2.objects.weapons.GunEmpty;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitFW_190A6LULU extends CockpitPilot {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitFW_190A6LULU.this.fm != null) {
                CockpitFW_190A6LULU.this.setTmp = CockpitFW_190A6LULU.this.setOld;
                CockpitFW_190A6LULU.this.setOld = CockpitFW_190A6LULU.this.setNew;
                CockpitFW_190A6LULU.this.setNew = CockpitFW_190A6LULU.this.setTmp;
                CockpitFW_190A6LULU.this.setNew.altimeter = CockpitFW_190A6LULU.this.fm.getAltitude();
                if (CockpitFW_190A6LULU.this.cockpitDimControl) {
                    if (CockpitFW_190A6LULU.this.setNew.dimPosition > 0.0F) {
                        CockpitFW_190A6LULU.this.setNew.dimPosition = CockpitFW_190A6LULU.this.setOld.dimPosition - 0.05F;
                    }
                } else if (CockpitFW_190A6LULU.this.setNew.dimPosition < 1.0F) {
                    CockpitFW_190A6LULU.this.setNew.dimPosition = CockpitFW_190A6LULU.this.setOld.dimPosition + 0.05F;
                }
                CockpitFW_190A6LULU.this.setNew.throttle = ((10F * CockpitFW_190A6LULU.this.setOld.throttle) + CockpitFW_190A6LULU.this.fm.CT.PowerControl) / 11F;
                CockpitFW_190A6LULU.this.setNew.vspeed = ((499F * CockpitFW_190A6LULU.this.setOld.vspeed) + CockpitFW_190A6LULU.this.fm.getVertSpeed()) / 500F;
                float f = CockpitFW_190A6LULU.this.waypointAzimuth();
                if (CockpitFW_190A6LULU.this.useRealisticNavigationInstruments()) {
                    CockpitFW_190A6LULU.this.setNew.waypointAzimuth.setDeg(f - 90F);
                    CockpitFW_190A6LULU.this.setOld.waypointAzimuth.setDeg(f - 90F);
                } else {
                    CockpitFW_190A6LULU.this.setNew.waypointAzimuth.setDeg(CockpitFW_190A6LULU.this.setOld.waypointAzimuth.getDeg(0.1F), f - CockpitFW_190A6LULU.this.setOld.azimuth.getDeg(1.0F));
                }
                CockpitFW_190A6LULU.this.setNew.azimuth.setDeg(CockpitFW_190A6LULU.this.setOld.azimuth.getDeg(1.0F), CockpitFW_190A6LULU.this.fm.Or.azimut());
                CockpitFW_190A6LULU.this.w.set(CockpitFW_190A6LULU.this.fm.getW());
                CockpitFW_190A6LULU.this.fm.Or.transform(CockpitFW_190A6LULU.this.w);
                CockpitFW_190A6LULU.this.setNew.turn = ((12F * CockpitFW_190A6LULU.this.setOld.turn) + CockpitFW_190A6LULU.this.w.z) / 13F;
                CockpitFW_190A6LULU.this.setNew.beaconDirection = ((10F * CockpitFW_190A6LULU.this.setOld.beaconDirection) + CockpitFW_190A6LULU.this.getBeaconDirection()) / 11F;
                CockpitFW_190A6LULU.this.setNew.beaconRange = ((10F * CockpitFW_190A6LULU.this.setOld.beaconRange) + CockpitFW_190A6LULU.this.getBeaconRange()) / 11F;
            }
            return true;
        }

        Interpolater() {
        }
    }

    private class Variables {

        float      altimeter;
        float      throttle;
        float      dimPosition;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float      beaconDirection;
        float      beaconRange;
        float      turn;
        float      vspeed;

        private Variables() {
            this.azimuth = new AnglesFork();
            this.waypointAzimuth = new AnglesFork();
        }
    }

    public CockpitFW_190A6LULU() {
        super("3DO/Cockpit/FW-190A-6R8/hier.him", "bf109");
        this.gun = new Gun[8];
        AircraftLH.printCompassHeading = true;
        ((AircraftLH) this.aircraft()).bWantBeaconKeys = true;
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.w = new Vector3f();
        this.bNeedSetUp = true;
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
        this.cockpitNightMats = (new String[] { "A4GP1", "A4GP2", "A4GP3", "A4GP4", "A4GP5", "A4GP6", "A5GP3Km", "EQpt5" });
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
            this.gun[6] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON05");
            this.gun[7] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON06");
        }
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.5F);
        Cockpit.xyz[2] = this.cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 0.01F);
        Cockpit.ypr[2] = this.cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, -2F);
        this.mesh.chunkSetLocate("CanopyTop", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("EjectatorHandle", 2520F * this.fm.CT.getCockpitDoor(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("CanopyFrameL", -this.cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 2.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("CanopyFrameR", this.cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 2.0F), 0.0F, 0.0F);
        if (!(((Aircraft) this.fm.actor).getGunByHookName("_CANNON05") instanceof GunEmpty) && !(((Aircraft) this.fm.actor).getGunByHookName("_CANNON06") instanceof GunEmpty)) {
            this.pictGunB[2] = (0.8F * this.pictGunB[2]) + (0.2F * (100F * (0.01F * this.gun[6].countBullets())));
            this.pictGunB[3] = (0.8F * this.pictGunB[3]) + (0.2F * (100F * (0.02F * this.gun[7].countBullets())));
            this.mesh.chunkSetAngles("Z_AmmoCounter1", 0.0F, 0.0F, 3.6F * this.pictGunB[2]);
            this.mesh.chunkSetAngles("Z_AmmoCounter2", 0.0F, 0.0F, 3.6F * this.pictGunB[3]);
        } else {
            this.pictGunB[2] = (0.8F * this.pictGunB[2]) + (0.2F * (100F * (0.01F * this.gun[2].countBullets())));
        }
        this.pictGunB[3] = (0.8F * this.pictGunB[3]) + (0.2F * (100F * (0.01F * this.gun[3].countBullets())));
        this.mesh.chunkSetAngles("Z_AmmoCounter1", 0.0F, 0.0F, 3.6F * this.pictGunB[2]);
        this.mesh.chunkSetAngles("Z_AmmoCounter2", 0.0F, 0.0F, 3.6F * this.pictGunB[3]);
        if (((this.fm.AS.astateCockpitState & 0x20) == 0) && ((this.fm.AS.astateCockpitState & 0x80) == 0)) {
            this.mesh.chunkSetAngles("NeedleALT", -this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleALTKm", 0.0F, 0.0F, this.cvt(this.setNew.altimeter, 0.0F, 10000F, 0.0F, -180F));
        }
        this.mesh.chunkSetAngles("NeedleManPress", -this.cvt(this.fm.EI.engines[0].getManifoldPressure() + 0.035F, 0.6F, 1.8F, 0.0F, 336F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleKMH", -this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), CockpitFW_190A6LULU.speedometerScale), 0.0F, 0.0F);
        if (((this.fm.AS.astateCockpitState & 0x40) == 0) && ((this.fm.AS.astateCockpitState & 8) == 0) && ((this.fm.AS.astateCockpitState & 0x10) == 0)) {
            this.mesh.chunkSetAngles("NeedleRPM", -this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), CockpitFW_190A6LULU.rpmScale), 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("NeedleFuel", this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 400F, 0.0F, 4F), CockpitFW_190A6LULU.fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOilTemp", this.floatindex(this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 3F), CockpitFW_190A6LULU.oilTempScale), 0.0F, 0.0F);
        if (((this.fm.AS.astateCockpitState & 2) == 0) && ((this.fm.AS.astateCockpitState & 1) == 0) && ((this.fm.AS.astateCockpitState & 4) == 0)) {
            this.mesh.chunkSetAngles("NeedleFuelPress", this.cvt(this.fm.EI.engines[0].getRPM() / 1500F, 0.0F, 3F, 0.0F, 135F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleOilPress", -this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut) + (this.fm.EI.engines[0].getRPM() / 1500F), 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
        }
        if (((this.fm.AS.astateCockpitState & 0x40) == 0) && ((this.fm.AS.astateCockpitState & 8) == 0) && ((this.fm.AS.astateCockpitState & 0x10) == 0)) {
            this.mesh.chunkSetAngles("NeedleAHTurn", this.cvt(this.setNew.turn, -0.23562F, 0.23562F, -50F, 50F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleAHBank", this.cvt(this.getBall(7D), -7F, 7F, 11F, -11F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleAHCyl", 0.0F, 0.0F, this.fm.Or.getKren());
            this.mesh.chunkSetAngles("NeedleAHBar", 0.0F, 0.0F, this.cvt(this.fm.Or.getTangage(), -45F, 45F, 12F, -12F));
        }
        if (((this.fm.AS.astateCockpitState & 0x40) == 0) && ((this.fm.AS.astateCockpitState & 8) == 0) && ((this.fm.AS.astateCockpitState & 0x10) == 0)) {
            if (this.useRealisticNavigationInstruments()) {
                this.mesh.chunkSetAngles("RepeaterPlane", -this.setNew.azimuth.getDeg(f) + this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
                this.mesh.chunkSetAngles("RepeaterOuter", this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            } else {
                this.mesh.chunkSetAngles("RepeaterOuter", this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
                this.mesh.chunkSetAngles("RepeaterPlane", -this.setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
            }
        }
        if (((this.fm.AS.astateCockpitState & 0x20) == 0) && ((this.fm.AS.astateCockpitState & 0x80) == 0)) {
            this.mesh.chunkSetAngles("NeedleHBSmall", -105F + ((float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 5F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleHBLarge", -270F + ((float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 60F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleTrimmung", this.fm.CT.getTrimElevatorControl() * 25F, 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleHClock", -this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleMClock", -this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleSClock", -this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
            this.resetYPRmodifier();
            if (this.gun[0] != null) {
                Cockpit.xyz[0] = this.cvt(this.gun[0].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
                this.mesh.chunkSetLocate("RC_MG17_L", Cockpit.xyz, Cockpit.ypr);
                this.mesh.chunkVisible("XLampMG17_1", this.gun[0].haveBullets());
            }
            if (this.gun[1] != null) {
                Cockpit.xyz[0] = this.cvt(this.gun[1].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
                this.mesh.chunkSetLocate("RC_MG17_R", Cockpit.xyz, Cockpit.ypr);
                this.mesh.chunkVisible("XLampMG17_2", this.gun[1].haveBullets());
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
                this.mesh.chunkVisible("XLampMG151_1", this.gun[2].haveBullets());
            }
            if (this.gun[3] != null) {
                Cockpit.xyz[0] = this.cvt(this.gun[3].countBullets(), 0.0F, 55F, -0.018F, 0.0F);
                this.mesh.chunkVisible("XLampMG151_2", this.gun[3].haveBullets());
            }
            if (this.gun[6] != null) {
                Cockpit.xyz[0] = this.cvt(this.gun[6].countBullets(), 0.0F, 55F, -0.018F, 0.0F);
                this.mesh.chunkVisible("XLampMG151_1", this.gun[2].haveBullets());
            }
            if (this.gun[7] != null) {
                Cockpit.xyz[0] = this.cvt(this.gun[7].countBullets(), 0.0F, 55F, -0.018F, 0.0F);
                this.mesh.chunkVisible("XLampMG151_2", this.gun[3].haveBullets());
            }
            this.mesh.chunkSetAngles("IgnitionSwitch", 24F * this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 0.0F);
            this.resetYPRmodifier();
            Cockpit.xyz[2] = this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, 0.058F);
            this.mesh.chunkSetLocate("Revi16Tinter", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkSetAngles("Stick", 0.0F, (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)) * 20F, (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)) * 20F);
            this.resetYPRmodifier();
            if (this.fm.CT.saveWeaponControl[1]) {
                Cockpit.xyz[2] = -0.004F;
            }
            this.mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
            float f2 = 0.0F;
            int i = 0;
            if (this.fm.CT.saveWeaponControl[0] || this.fm.CT.saveWeaponControl[1] || this.fm.CT.saveWeaponControl[3]) {
                if (this.fm.CT.saveWeaponControl[0] || this.fm.CT.saveWeaponControl[1]) {
                    f2 = 20F;
                }
                this.tClap = Time.tickCounter() + World.Rnd().nextInt(500, 1000);
                if (this.fm.CT.saveWeaponControl[0] && !this.fm.CT.saveWeaponControl[1]) {
                } else if (!this.fm.CT.saveWeaponControl[0] && this.fm.CT.saveWeaponControl[1]) {
                } else {
                }
            }
            if (Time.tickCounter() < this.tClap) {
                i = 1;
            }
            this.mesh.chunkSetAngles("SecTrigger2", -(240F + f2) * (this.pictClap = (0.85F * this.pictClap) + (0.15F * i)), 0.0F, 0.0F);
            Cockpit.ypr[0] = this.interp(this.setNew.throttle, this.setOld.throttle, f) * 34F * 0.91F;
            Cockpit.xyz[2] = Cockpit.ypr[0] <= 7F ? 0.0F : -0.006F;
            this.mesh.chunkSetLocate("ThrottleQuad", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkSetAngles("RPedalBase", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("RPedalStrut", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("RPedal", 0.0F, 0.0F, (-this.fm.CT.getRudder() * 15F) - (this.fm.CT.getBrake() * 15F));
            this.mesh.chunkSetAngles("LPedalBase", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("LPedalStrut", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
            this.mesh.chunkSetAngles("LPedal", 0.0F, 0.0F, (this.fm.CT.getRudder() * 15F) - (this.fm.CT.getBrake() * 15F));
            this.mesh.chunkVisible("XLampFuelLow", this.fm.M.fuel < 39.4F);
            this.mesh.chunkVisible("XLampFlapLPos_3", this.fm.CT.getFlap() > 0.9F);
            this.mesh.chunkVisible("XLampFlapLPos_2", (this.fm.CT.getFlap() > 0.1F) && (this.fm.CT.getFlap() < 0.5F));
            this.mesh.chunkVisible("XLampFlapLPos_1", this.fm.CT.getFlap() < 0.1F);
            this.mesh.chunkVisible("XLampFlapRPos_3", this.fm.CT.getFlap() > 0.9F);
            this.mesh.chunkVisible("XLampFlapRPos_2", (this.fm.CT.getFlap() > 0.1F) && (this.fm.CT.getFlap() < 0.5F));
            this.mesh.chunkVisible("XLampFlapRPos_1", this.fm.CT.getFlap() < 0.1F);
            this.mesh.chunkVisible("XLampGearL_1", this.fm.CT.getGear() < 0.05F);
            this.mesh.chunkVisible("XLampGearL_2", (this.fm.CT.getGear() > 0.95F) && this.fm.Gears.lgear);
            this.mesh.chunkVisible("XLampGearR_1", this.fm.CT.getGear() < 0.05F);
            this.mesh.chunkVisible("XLampGearR_2", (this.fm.CT.getGear() > 0.95F) && this.fm.Gears.rgear);
            this.mesh.chunkVisible("XLampGearC_1", this.fm.CT.getGear() < 0.05F);
            this.mesh.chunkVisible("XLampGearC_2", this.fm.CT.getGear() > 0.95F);
            this.mesh.chunkSetAngles("NeedleNahe1", this.cvt(this.setNew.beaconDirection, -45F, 45F, 20F, -20F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleNahe2", this.cvt(this.setNew.beaconRange, 0.0F, 1.0F, -20F, 20F), 0.0F, 0.0F);
            this.mesh.chunkVisible("AFN2_RED", this.isOnBlindLandingMarker());
            if (HotKeyCmd.getByRecordedId(142).isActive() && Main3D.cur3D().aircraftHotKeys.isPropAuto()) {
                this.mesh.chunkSetAngles("SwitchProp", 0.0F, -30F, 0.0F);
            }
            if (HotKeyCmd.getByRecordedId(142).isActive() && !Main3D.cur3D().aircraftHotKeys.isPropAuto()) {
                this.mesh.chunkSetAngles("SwitchProp", 0.0F, 30F, 0.0F);
            }
            float f3 = Atmosphere.temperature((float) this.fm.Loc.z) - 273.15F;
            if (f3 < -16F) {
                this.mesh.chunkVisible("XLampPitot", true);
            } else if (f3 > -12F) {
                this.mesh.chunkVisible("XLampPitot", false);
            }
        }
        this.cockpitRadarLiSN2.updateRadar();
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void toggleLight() {
        this.cockpitLightControl = !this.cockpitLightControl;
        if (this.cockpitLightControl) {
            this.light1.light.setEmit(0.0012F, 0.75F);
            this.light2.light.setEmit(0.0012F, 0.75F);
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
        if (((this.fm.AS.astateCockpitState & 4) != 0) || ((this.fm.AS.astateCockpitState & 0x10) != 0) || ((this.fm.AS.astateCockpitState & 2) != 0) || ((this.fm.AS.astateCockpitState & 1) != 0)) {
            if ((this.fm.AS.astateCockpitState & 2) != 0) {
                this.mesh.chunkVisible("Revi16", false);
                this.mesh.chunkVisible("Revi16Tinter", false);
                this.mesh.chunkVisible("Z_Z_MASK", false);
                this.mesh.chunkVisible("Z_Z_RETICLE", false);
                this.mesh.chunkVisible("DRevi16", true);
            }
            this.mesh.chunkVisible("XGlassDamage1", true);
            this.mesh.chunkVisible("XGlassCanopyDamage1", true);
            this.mesh.chunkVisible("HullDamage1", true);
            this.mesh.materialReplace("A4GP1", "DA4GP1");
            this.mesh.materialReplace("A4GP1_night", "DA4GP1_night");
            this.mesh.chunkVisible("NeedleRPM", false);
            this.mesh.chunkVisible("NeedleManPress", false);
        }
        if (((this.fm.AS.astateCockpitState & 8) != 0) || ((this.fm.AS.astateCockpitState & 0x20) != 0) || ((this.fm.AS.astateCockpitState & 0x40) != 0)) {
            this.mesh.chunkVisible("XGlassCanopyDamage2", true);
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

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2        = new RadarLiSN2Equipment(this, 27, 155F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
    // --- RadarLiSN2 ---

    private Gun                gun[];
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    private LightPointActor    light1;
    private LightPointActor    light2;
    private float              pictAiler;
    private float              pictElev;
    public Vector3f            w;
    private float              pictGunB[]         = { 0.0F, 0.0F, 0.0F, 0.0F };
    private static final float speedometerScale[] = { 0.0F, 18.5F, 67F, 117F, 164F, 215F, 267F, 320F, 379F, 427F, 428F };
    private static final float rpmScale[]         = { 0.0F, 11.25F, 53F, 108F, 170F, 229F, 282F, 334F, 342.5F, 342.5F };
    private static final float fuelScale[]        = { 0.0F, 16F, 35F, 52.5F, 72F, 72F };
    private static final float oilTempScale[]     = { 0.0F, 23F, 52F, 81F, 81F };
    private boolean            bNeedSetUp;
    private int                tClap;
    private float              pictClap;
    static {
        Property.set(CockpitFW_190A6LULU.class, "normZN", 0.72F);
        Property.set(CockpitFW_190A6LULU.class, "gsZN", 0.66F);
    }

}
