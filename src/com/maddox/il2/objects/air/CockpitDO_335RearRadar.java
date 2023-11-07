package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitDO_335RearRadar extends CockpitPilot {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitDO_335RearRadar.this.fm != null) {
                CockpitDO_335RearRadar.this.setTmp = CockpitDO_335RearRadar.this.setOld;
                CockpitDO_335RearRadar.this.setOld = CockpitDO_335RearRadar.this.setNew;
                CockpitDO_335RearRadar.this.setNew = CockpitDO_335RearRadar.this.setTmp;
                CockpitDO_335RearRadar.this.setNew.throttle1 = (0.85F * CockpitDO_335RearRadar.this.setOld.throttle1) + (CockpitDO_335RearRadar.this.fm.EI.engines[0].getControlThrottle() * 0.15F);
                CockpitDO_335RearRadar.this.setNew.prop1 = (0.85F * CockpitDO_335RearRadar.this.setOld.prop1) + (CockpitDO_335RearRadar.this.fm.EI.engines[0].getControlProp() * 0.15F);
                CockpitDO_335RearRadar.this.setNew.mix1 = (0.85F * CockpitDO_335RearRadar.this.setOld.mix1) + (CockpitDO_335RearRadar.this.fm.EI.engines[0].getControlMix() * 0.15F);
                CockpitDO_335RearRadar.this.setNew.throttle2 = (0.85F * CockpitDO_335RearRadar.this.setOld.throttle2) + (CockpitDO_335RearRadar.this.fm.EI.engines[1].getControlThrottle() * 0.15F);
                CockpitDO_335RearRadar.this.setNew.prop2 = (0.85F * CockpitDO_335RearRadar.this.setOld.prop2) + (CockpitDO_335RearRadar.this.fm.EI.engines[1].getControlProp() * 0.15F);
                CockpitDO_335RearRadar.this.setNew.mix2 = (0.85F * CockpitDO_335RearRadar.this.setOld.mix2) + (CockpitDO_335RearRadar.this.fm.EI.engines[1].getControlMix() * 0.15F);
                CockpitDO_335RearRadar.this.setNew.altimeter = CockpitDO_335RearRadar.this.fm.getAltitude();
                float f = CockpitDO_335RearRadar.this.waypointAzimuth();
                if (CockpitDO_335RearRadar.this.useRealisticNavigationInstruments()) {
                    CockpitDO_335RearRadar.this.setNew.waypointAzimuth.setDeg(f - 90F);
                    CockpitDO_335RearRadar.this.setOld.waypointAzimuth.setDeg(f - 90F);
                } else {
                    CockpitDO_335RearRadar.this.setNew.waypointAzimuth.setDeg(CockpitDO_335RearRadar.this.setOld.waypointAzimuth.getDeg(0.1F), f - CockpitDO_335RearRadar.this.setOld.azimuth.getDeg(1.0F));
                }
                CockpitDO_335RearRadar.this.setNew.azimuth.setDeg(CockpitDO_335RearRadar.this.setOld.azimuth.getDeg(1.0F), CockpitDO_335RearRadar.this.fm.Or.azimut());
                CockpitDO_335RearRadar.this.setNew.vspeed = ((199F * CockpitDO_335RearRadar.this.setOld.vspeed) + CockpitDO_335RearRadar.this.fm.getVertSpeed()) / 200F;
                if (CockpitDO_335RearRadar.this.cockpitDimControl) {
                    if (CockpitDO_335RearRadar.this.setNew.dimPosition > 0.0F) {
                        CockpitDO_335RearRadar.this.setNew.dimPosition = CockpitDO_335RearRadar.this.setOld.dimPosition - 0.05F;
                    }
                } else if (CockpitDO_335RearRadar.this.setNew.dimPosition < 1.0F) {
                    CockpitDO_335RearRadar.this.setNew.dimPosition = CockpitDO_335RearRadar.this.setOld.dimPosition + 0.05F;
                }
                CockpitDO_335RearRadar.this.setNew.radioalt = (0.9F * CockpitDO_335RearRadar.this.setOld.radioalt) + (0.1F * (CockpitDO_335RearRadar.this.fm.getAltitude() - Landscape.HQ_Air((float) CockpitDO_335RearRadar.this.fm.Loc.x, (float) CockpitDO_335RearRadar.this.fm.Loc.y)));
                CockpitDO_335RearRadar.this.setNew.beaconDirection = ((10F * CockpitDO_335RearRadar.this.setOld.beaconDirection) + CockpitDO_335RearRadar.this.getBeaconDirection()) / 11F;
                CockpitDO_335RearRadar.this.setNew.beaconRange = ((10F * CockpitDO_335RearRadar.this.setOld.beaconRange) + CockpitDO_335RearRadar.this.getBeaconRange()) / 11F;
            }
            return true;
        }
    }

    private class Variables {

        float      throttle1;
        float      prop1;
        float      mix1;
        float      throttle2;
        float      prop2;
        float      mix2;
        float      altimeter;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float      vspeed;
        float      dimPosition;
        float      radioalt;
        float      beaconDirection;
        float      beaconRange;

        private Variables() {
            this.azimuth = new AnglesFork();
            this.waypointAzimuth = new AnglesFork();
        }
    }

    protected void setCameraOffset() {
        this.cameraCenter.add(0.1D, 0.0D, 0.01D);
    }

    public CockpitDO_335RearRadar() {
        super("3DO/Cockpit/Do-335-RearRadar/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictManf1 = 1.0F;
        this.pictManf2 = 1.0F;
        this.cockpitNightMats = (new String[] { "1", "2", "3", "4", "5", "8", "9", "11", "alt_km", "kompass", "ok42", "D1", "D2", "D3", "D4", "D5", "D8", "D9" });
        this.setNightMats(false);
        this.setNew.dimPosition = 1.0F;
        this.cockpitDimControl = !this.cockpitDimControl;
        this.interpPut(new Interpolater(), null, Time.current(), null);
        if (this.acoustics != null) {
            this.acoustics.globFX = new ReverbFXRoom(0.45F);
        }
        this.printCompassHeading = true;
        this.limits6DoF = (new float[] { 0.7F, 0.055F, -0.07F, 0.11F, 0.15F, -0.11F, 0.03F, -0.03F });
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void reflectWorldToInstruments(float f) {
        if (this.enteringAim) {
            HookPilot localHookPilot = HookPilot.current;
            if (localHookPilot.isAimReached()) {
                this.enteringAim = false;
                this.enter();
            } else if (!localHookPilot.isAim()) {
                this.enteringAim = false;
            }
        }
        if (this.gun[0] == null) {
            this.gun[0] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN02");
            this.gun[1] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN03");
            this.gun[2] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN01");
        }
// this.mesh.chunkSetAngles("Revisun", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -45F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Columnbase", 12F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", -45F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Columnbutton1", this.fm.CT.saveWeaponControl[0] ? -14.5F : 0.0F, 0.0F, 0.0F);
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[1]) {
            xyz[2] = -0.005F;
        }
        this.mesh.chunkSetLocate("Z_Columnbutton2", xyz, ypr);
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[3]) {
            xyz[2] = -0.0035F;
        }
        this.mesh.chunkSetLocate("Z_Columnbutton3", xyz, ypr);
        this.resetYPRmodifier();
        this.mesh.chunkSetLocate("Z_Columnbutton4", xyz, ypr);
        this.resetYPRmodifier();
        this.mesh.chunkSetLocate("Z_Columnbutton5", xyz, ypr);
        this.resetYPRmodifier();
        xyz[2] = 0.12F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("Z_LeftPedal", xyz, ypr);
        this.mesh.chunkSetAngles("Z_LeftPedal1", -32.7F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        xyz[2] = -0.12F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("Z_RightPedal", xyz, ypr);
        this.mesh.chunkSetAngles("Z_RightPedal1", -32.7F * this.fm.CT.getRudder(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throttle1", 37.28F * this.interp(this.setNew.throttle1, this.setOld.throttle1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throttle2", 37.28F * this.interp(this.setNew.throttle2, this.setOld.throttle2, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_ThrottleLock", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture1", 34.37F * this.interp(this.setNew.mix1, this.setOld.mix1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mixture2", 34.37F * this.interp(this.setNew.mix2, this.setOld.mix2, f), 0.0F, 0.0F);
        if (this.fm.EI.engines[0].getControlFeather() == 0) {
            this.mesh.chunkSetAngles("Z_Pitch1", 23F + (46.5F * this.interp(this.setNew.prop1, this.setOld.prop1, f)), 0.0F, 0.0F);
        } else {
            this.mesh.chunkSetAngles("Z_Pitch1", 0.0F, 0.0F, 0.0F);
        }
        if (this.fm.EI.engines[1].getControlFeather() == 0) {
            this.mesh.chunkSetAngles("Z_Pitch2", 23F + (46.5F * this.interp(this.setNew.prop2, this.setOld.prop2, f)), 0.0F, 0.0F);
        } else {
            this.mesh.chunkSetAngles("Z_Pitch2", 0.0F, 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("Z_Radiat1", 30F * this.fm.EI.engines[0].getControlRadiator(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Radiat2", 30F * this.fm.EI.engines[1].getControlRadiator(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Gear", 50F * this.fm.CT.GearControl, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Flap", 50F * this.fm.CT.FlapsControl, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Magneto1", this.cvt(this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 3F, 0.0F, 75F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Magneto2", this.cvt(this.fm.EI.engines[1].getControlMagnetos(), 0.0F, 3F, 0.0F, 75F), 0.0F, 0.0F);
        this.resetYPRmodifier();
        xyz[1] = 0.065F * this.fm.CT.BayDoorControl;
// this.mesh.chunkSetLocate("Z_BombBay", xyz, ypr);
        this.resetYPRmodifier();
        xyz[2] = this.cvt(this.gun[0].countBullets(), 0.0F, 500F, -0.00465F, 0.04175F);
        this.mesh.chunkSetLocate("Z_AmmoCounter1", xyz, ypr);
        xyz[2] = this.cvt(this.gun[2].countBullets(), 0.0F, 100F, -0.00465F, 0.04175F);
        this.mesh.chunkSetLocate("Z_AmmoCounter2", xyz, ypr);
        xyz[2] = this.cvt(this.gun[1].countBullets(), 0.0F, 500F, -0.00465F, 0.04175F);
        this.mesh.chunkSetLocate("Z_AmmoCounter3", xyz, ypr);
        if (this.useRealisticNavigationInstruments()) {
            this.mesh.chunkSetAngles("Z_Compass2", this.setNew.azimuth.getDeg(f) - this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass4", 90F + this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass1", -this.setNew.waypointAzimuth.getDeg(0.1F), 0.0F, 0.0F);
        } else {
            this.mesh.chunkSetAngles("Z_Compass1", -this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass4", 90F - this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass2", this.setNew.waypointAzimuth.getDeg(0.1F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass3", -this.setNew.waypointAzimuth.getDeg(0.1F), 0.0F, 0.0F);
        }
        this.mesh.chunkSetAngles("Z_Hour1", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Second1", this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.cvt(this.w.z, -0.23562F, 0.23562F, 33F, -33F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank2", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        xyz[1] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.024F, -0.024F);
        this.mesh.chunkSetLocate("Z_TurnBank2a", xyz, ypr);
        this.mesh.chunkSetAngles("Z_TurnBank3", this.cvt(this.getBall(8D), -8F, 8F, -15F, 15F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speed1", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), speedometerScale), 0.0F, 0.0F);
        float f1 = this.setNew.vspeed <= 0.0F ? -1F : 1.0F;
        this.mesh.chunkSetAngles("Z_Climb1", f1 * this.floatindex(this.cvt(Math.abs(this.setNew.vspeed), 0.0F, 30F, 0.0F, 6F), variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RPM1", this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RPM2", this.floatindex(this.cvt(this.fm.EI.engines[1].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA1", this.pictManf1 = (0.9F * this.pictManf1) + (0.1F * this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 329F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA2", this.pictManf2 = (0.9F * this.pictManf2) + (0.1F * this.cvt(this.fm.EI.engines[1].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 329F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp1", this.cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 120F, 0.0F, 57F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp2", this.cvt(this.fm.EI.engines[1].tWaterOut, 0.0F, 120F, 0.0F, 57F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oiltemp1", this.cvt(this.fm.EI.engines[0].tOilIn, 0.0F, 120F, 0.0F, 57F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oiltemp2", this.cvt(this.fm.EI.engines[1].tOilIn, 0.0F, 120F, 0.0F, 57F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel1", this.cvt(this.fm.M.fuel, 0.0F, 864F, 0.0F, 80F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuelpress1", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.32F, 0.0F, 1.0F, 0.0F, 58F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuelpress2", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.32F, 0.0F, 1.0F, 0.0F, 58F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oilpress1", this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilIn * this.fm.EI.engines[0].getReadyness()), 0.0F, 7.45F, 0.0F, 58F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Oilpress2", this.cvt(1.0F + (0.05F * this.fm.EI.engines[1].tOilIn * this.fm.EI.engines[1].getReadyness()), 0.0F, 7.45F, 0.0F, 58F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_NeedPitch1", 270F - ((float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_NeedPitch2", 105F - ((float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_NeedPitch3", 270F - ((float) Math.toDegrees(this.fm.EI.engines[1].getPropPhi() - this.fm.EI.engines[1].getPropPhiMin()) * 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_NeedPitch4", 105F - ((float) Math.toDegrees(this.fm.EI.engines[1].getPropPhi() - this.fm.EI.engines[1].getPropPhiMin()) * 5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Temp3", this.cvt(Atmosphere.temperature((float) this.fm.Loc.z), 213.09F, 313.09F, -45F, 30F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("z_Alt1", this.cvt(this.interp(this.setNew.radioalt, this.setOld.radioalt, f), 0.0F, 750F, 0.0F, 232F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("z_Alt2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("z_Alt3", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 14000F, 0.0F, 315F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("z_HydroPress1", this.fm.Gears.isHydroOperable() ? 130F : 0.0F, 0.0F, 0.0F);
        this.mesh.chunkVisible("Z_GearLGreen", (this.fm.CT.getGearL() > 0.99F) && this.fm.Gears.lgear);
        this.mesh.chunkVisible("Z_GearRGreen", (this.fm.CT.getGearR() > 0.99F) && this.fm.Gears.rgear);
        this.mesh.chunkVisible("Z_GearCGreen", this.fm.CT.getGearC() > 0.99F);
        this.mesh.chunkVisible("Z_GearLRed", (this.fm.CT.getGearL() < 0.01F) || !this.fm.Gears.lgear);
        this.mesh.chunkVisible("Z_GearRRed", (this.fm.CT.getGearR() < 0.01F) || !this.fm.Gears.rgear);
        this.mesh.chunkVisible("Z_GearCRed", this.fm.CT.getGearC() < 0.01F);
        this.mesh.chunkVisible("Z_FlapCombat", this.fm.CT.getFlap() > 0.1F);
        this.mesh.chunkVisible("Z_FlapTakeOff", this.fm.CT.getFlap() > 0.265F);
        this.mesh.chunkVisible("Z_FlapLanding", this.fm.CT.getFlap() > 0.665F);
        this.mesh.chunkVisible("Z_Ammo1", this.gun[0].countBullets() == 0);
        this.mesh.chunkVisible("Z_Ammo2", this.gun[2].countBullets() == 0);
        this.mesh.chunkVisible("Z_Ammo3", this.gun[1].countBullets() == 0);
        this.mesh.chunkVisible("Z_Stall", this.fm.getSpeedKMH() < 145F);
        this.mesh.chunkVisible("Z_FuelWhite", true);
        this.mesh.chunkVisible("Z_FuelRed", this.fm.M.fuel < 256.08F);
        this.mesh.chunkVisible("Z_Warning1", false);
        this.mesh.chunkVisible("Z_Warning2", false);
        this.mesh.chunkVisible("Z_Warning3", false);
        this.mesh.chunkVisible("Z_Warning4", false);
        this.mesh.chunkVisible("Z_Warning5", false);
        this.mesh.chunkVisible("Z_Warning6", false);
        this.mesh.chunkVisible("Z_Warning7", false);
        this.mesh.chunkVisible("Z_Warning8", false);
        this.mesh.chunkVisible("Z_Warning9", false);
        this.mesh.chunkSetAngles("Z_AFN22", this.cvt(this.setNew.beaconDirection, -45F, 45F, -20F, 20F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AFN21", this.cvt(this.setNew.beaconRange, 0.0F, 1.0F, 20F, -20F), 0.0F, 0.0F);
        this.mesh.chunkVisible("AFN2_RED", this.isOnBlindLandingMarker());

        // +++ RadarLiSN2 +++
        this.cockpitRadarLiSN2.updateRadar();
        // --- RadarLiSN2 ---
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2 = new RadarLiSN2Equipment(this, 28, 70F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
    // --- RadarLiSN2 ---

    protected float waypointAzimuth() {
        return super.waypointAzimuthInvertMinus(15F);
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 0x80) != 0) {
            this.mesh.chunkVisible("Z_OilSplats_D1", true);
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

    protected boolean doFocusEnter() {
        ((TypeRadarLiSN2Carrier) this.aircraft()).setCurPilot(2);
        if (super.doFocusEnter()) {
            HookPilot localHookPilot = HookPilot.current;
            localHookPilot.doAim(false);

            Point3d localPoint3d = new Point3d();
            localPoint3d.set(0.25D, 0.02D, -0.3D);
            localHookPilot.setTubeSight(localPoint3d);

            return true;
        }
        return false;
    }

    protected void doFocusLeave() {
        if (!this.isFocused()) {
            return;
        }
        this.leave();
        super.doFocusLeave();
    }

    public void doToggleAim(boolean paramBoolean) {
        if (!this.isFocused()) {
            return;
        }
        if (this.isToggleAim() == paramBoolean) {
            return;
        }
        if (paramBoolean) {
            this.prepareToEnter();
        } else {
            this.leave();
        }
    }

    private void prepareToEnter() {
        HookPilot localHookPilot = HookPilot.current;
        if (localHookPilot.isPadlock()) {
            localHookPilot.stopPadlock();
        }
        localHookPilot.doAim(true);
// localHookPilot.setSimpleAimOrient(0.0F, 0.0F, 0.0F);
        this.enteringAim = true;
    }

    private void enter() {
        HookPilot localHookPilot = HookPilot.current;
        localHookPilot.setSimpleUse(true);
        localHookPilot.setSimpleAimOrient(0.0F, -25.0F, 0.0F);
        localHookPilot.setInstantOrient(0.0F, -25.0F, 0.0F);
        this.doSetSimpleUse(true);
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
        this.bEntered = true;
        this.mesh.chunkVisible("Z_Columnbase", false);
        this.mesh.chunkVisible("Z_Column", false);
        this.mesh.chunkVisible("Z_Columnbutton1", false);
        this.mesh.chunkVisible("Z_Columnbutton2", false);
        this.mesh.chunkVisible("Z_Columnbutton3", false);
        this.mesh.chunkVisible("Z_Columnbutton4", false);
        this.mesh.chunkVisible("Z_Columnbutton5", false);
    }

    public void doSetSimpleUse(boolean paramBoolean) {
        super.doSetSimpleUse(paramBoolean);
        if (paramBoolean) {
            this.saveFov = Main3D.FOVX;
            CmdEnv.top().exec("fov 75");
            Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(false);
        }
    }

    private void leave() {
        if (this.enteringAim) {
            HookPilot localHookPilot = HookPilot.current;
            localHookPilot.doAim(false);
            localHookPilot.setSimpleAimOrient(0.0F, 0.0F, 0.0F);
            return;
        }
        if (!this.bEntered) {
            return;
        }
        Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(true);
        CmdEnv.top().exec("fov " + this.saveFov);
        HookPilot localHookPilot = HookPilot.current;
        localHookPilot.doAim(false);
        localHookPilot.setSimpleAimOrient(0.0F, 0.0F, 0.0F);
        localHookPilot.setSimpleUse(false);
        this.doSetSimpleUse(false);
        boolean bool = HotKeyEnv.isEnabled("aircraftView");
        HotKeyEnv.enable("PanView", bool);
        HotKeyEnv.enable("SnapView", bool);
        this.bEntered = false;
        this.mesh.chunkVisible("Z_Columnbase", true);
        this.mesh.chunkVisible("Z_Column", true);
        this.mesh.chunkVisible("Z_Columnbutton1", true);
        this.mesh.chunkVisible("Z_Columnbutton2", true);
        this.mesh.chunkVisible("Z_Columnbutton3", true);
        this.mesh.chunkVisible("Z_Columnbutton4", true);
        this.mesh.chunkVisible("Z_Columnbutton5", true);
    }

    private boolean            bEntered           = false;
    private float              saveFov;
    private boolean            enteringAim        = false;

    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
    private float              pictManf1;
    private float              pictManf2;
    private BulletEmitter      gun[]              = { null, null, null };
    private static final float speedometerScale[] = { 0.0F, 21F, 69.5F, 116F, 163F, 215.5F, 266.5F, 318.5F, 378F, 430.5F };
    private static final float variometerScale[]  = { 0.0F, 47F, 82F, 97F, 112F, 111.7F, 132F };
    private static final float rpmScale[]         = { 0.0F, 2.5F, 19F, 50.5F, 102.5F, 173F, 227F, 266.5F, 297F };

    static {
        Property.set(CockpitDO_335RearRadar.class, "astatePilotIndx", 1);
    }

}
