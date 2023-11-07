package com.maddox.il2.objects.air;

import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Tuple3f;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitHE_219 extends CockpitPilot {
    class Interpolater extends InterpolateRef {
        public boolean tick() {
            if (CockpitHE_219.this.fm != null) {
                CockpitHE_219.this.setTmp = CockpitHE_219.this.setOld;
                CockpitHE_219.this.setOld = CockpitHE_219.this.setNew;
                CockpitHE_219.this.setNew = CockpitHE_219.this.setTmp;
                CockpitHE_219.this.setNew.throttle1 = ((0.85F * CockpitHE_219.this.setOld.throttle1) + (CockpitHE_219.this.fm.EI.engines[0].getControlThrottle() * 0.15F));
                CockpitHE_219.this.setNew.prop1 = ((0.85F * CockpitHE_219.this.setOld.prop1) + (CockpitHE_219.this.fm.EI.engines[0].getControlProp() * 0.15F));
                CockpitHE_219.this.setNew.mix1 = ((0.85F * CockpitHE_219.this.setOld.mix1) + (CockpitHE_219.this.fm.EI.engines[0].getControlMix() * 0.15F));
                CockpitHE_219.this.setNew.throttle2 = ((0.85F * CockpitHE_219.this.setOld.throttle2) + (CockpitHE_219.this.fm.EI.engines[1].getControlThrottle() * 0.15F));
                CockpitHE_219.this.setNew.prop2 = ((0.85F * CockpitHE_219.this.setOld.prop2) + (CockpitHE_219.this.fm.EI.engines[1].getControlProp() * 0.15F));
                CockpitHE_219.this.setNew.mix2 = ((0.85F * CockpitHE_219.this.setOld.mix2) + (CockpitHE_219.this.fm.EI.engines[1].getControlMix() * 0.15F));
                float f = CockpitHE_219.this.waypointAzimuth();
                CockpitHE_219.this.setNew.waypointAzimuth.setDeg(CockpitHE_219.this.setOld.waypointAzimuth.getDeg(0.1F), (f - CockpitHE_219.this.setOld.azimuth.getDeg(1.0F)) + World.Rnd().nextFloat(-2.0F, 2.0F));
                CockpitHE_219.this.setNew.azimuth.setDeg(CockpitHE_219.this.setOld.azimuth.getDeg(1.0F), CockpitHE_219.this.fm.Or.azimut());
                CockpitHE_219.this.setNew.vspeed = (((199.0F * CockpitHE_219.this.setOld.vspeed) + CockpitHE_219.this.fm.getVertSpeed()) / 200.0F);
                if (CockpitHE_219.this.cockpitDimControl) {
                    if (CockpitHE_219.this.setNew.dimPosition > 0.0F) {
                        CockpitHE_219.this.setNew.dimPosition = (CockpitHE_219.this.setOld.dimPosition - 0.05F);
                    }
                } else if (CockpitHE_219.this.setNew.dimPosition < 1.0F) {
                    CockpitHE_219.this.setNew.dimPosition = (CockpitHE_219.this.setOld.dimPosition + 0.05F);
                }
                World.land();
                CockpitHE_219.this.setNew.radioalt = ((0.9F * CockpitHE_219.this.setOld.radioalt) + (0.1F * (CockpitHE_219.this.fm.getAltitude() - Landscape.HQ((float) CockpitHE_219.this.fm.Loc.x, (float) CockpitHE_219.this.fm.Loc.y))));
            }
            return true;
        }

        Interpolater() {
        }
    }

    private class Variables {
        float      throttle1;
        float      prop1;
        float      mix1;
        float      throttle2;
        float      prop2;
        float      mix2;
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

    public CockpitHE_219() {
        super("3DO/Cockpit/He-219Uhu/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictManf1 = 1.0F;
        this.pictManf2 = 1.0F;
        this.cockpitNightMats = (new String[] { "1", "2", "3", "4", "5", "8", "9", "11", "alt_km", "kompass", "ok42", "D1", "D2", "D3", "D4", "D5", "D8", "D9" });
        this.setNightMats(true);
        this.setNew.dimPosition = 1.0F;
        this.cockpitDimControl = !this.cockpitDimControl;
        this.interpPut(new Interpolater(), null, Time.current(), null);
        AircraftLH.printCompassHeading = true;
        if (this.acoustics != null) {
            this.acoustics.globFX = new ReverbFXRoom(0.45F);
        }
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void reflectWorldToInstruments(float f) {
        if (this.gun[0] == null) {
            this.gun[0] = this.aircraft().getGunByHookName("_MGUN02");
            this.gun[1] = this.aircraft().getGunByHookName("_MGUN03");
            this.gun[2] = this.aircraft().getGunByHookName("_MGUN01");
            this.gun[3] = this.aircraft().getGunByHookName("_MGUN04");
            this.gun[4] = this.aircraft().getGunByHookName("_MGUN05");
        }
        this.mesh.chunkSetAngles("Z_Columnbase", 12F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", -45F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Columnbutton1", this.fm.CT.saveWeaponControl[0] ? -14.5F : 0.0F, 0.0F, 0.0F);
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[1]) {
            Cockpit.xyz[2] = -0.005F;
        }
        this.mesh.chunkSetLocate("Z_Columnbutton2", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        if (this.fm.CT.saveWeaponControl[3]) {
            Cockpit.xyz[2] = -0.0035F;
        }
        this.mesh.chunkSetLocate("Z_Columnbutton3", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        this.mesh.chunkSetLocate("Z_Columnbutton4", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        this.mesh.chunkSetLocate("Z_Columnbutton5", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = -0.05F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("PedalL", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.05F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("PedalR", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_Throttle1", 37.28F * this.interp(this.setNew.throttle1, this.setOld.throttle1, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Throttle2", 37.28F * this.interp(this.setNew.throttle2, this.setOld.throttle2, f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Gear", 50F * this.fm.CT.GearControl, 0.0F, 0.0F);
        if (this.useRealisticNavigationInstruments()) {
            this.mesh.chunkSetAngles("Z_Compass2", this.setNew.azimuth.getDeg(f) - this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass1", -this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
        } else {
            this.mesh.chunkSetAngles("Z_Compass1", -this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("Z_Compass2", this.setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
        }
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.cvt(((Tuple3f) (this.w)).z, -0.23562F, 0.23562F, 33F, -33F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank2", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.024F, -0.024F);
        this.mesh.chunkSetLocate("Z_TurnBank2a", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Z_TurnBank3", this.cvt(this.getBall(8D), -8F, 8F, -15F, 15F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speed1", this.floatindex(this.cvt(Pitot.Indicator((float) ((Tuple3d) (this.fm.Loc)).z, this.fm.getSpeedKMH()), 0.0F, 1000F, 0.0F, 9F), CockpitHE_219.speedometerScale), 0.0F, 0.0F);
        float f1 = this.setNew.vspeed <= 0.0F ? -1F : 1.0F;
        this.mesh.chunkSetAngles("Z_Climb1", f1 * this.floatindex(this.cvt(Math.abs(this.setNew.vspeed), 0.0F, 30F, 0.0F, 6F), CockpitHE_219.variometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RPM1", this.floatindex(this.cvt(this.fm.EI.engines[1].getRPM(), 0.0F, 4000F, 0.0F, 8F), CockpitHE_219.rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("RPM2", this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), CockpitHE_219.rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA1", this.pictManf1 = (0.9F * this.pictManf1) + (0.1F * this.cvt(this.fm.EI.engines[1].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 329F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA2", this.pictManf2 = (0.9F * this.pictManf2) + (0.1F * this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 329F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Fuel1", this.cvt(this.fm.M.fuel, 0.0F, 864F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("z_Alt1", this.cvt(this.interp(this.setNew.radioalt, this.setOld.radioalt, f), 0.0F, 750F, 0.0F, 232F), 0.0F, 0.0F);
        this.mesh.chunkVisible("Z_Gear", (this.fm.CT.getGear() > 0.99F) && this.fm.Gears.lgear && this.fm.Gears.rgear);
        this.mesh.chunkSetAngles("Z_AFN1", this.cvt(this.setNew.beaconDirection, -45F, 45F, -20F, 20F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_AFN2", this.cvt(this.setNew.beaconRange, 0.0F, 1.0F, 20F, -20F), 0.0F, 0.0F);
        this.mesh.chunkVisible("AFN2_RED", this.isOnBlindLandingMarker());
    }

    protected float waypointAzimuth() {
        return this.waypointAzimuthInvertMinus(30F);
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
        if (super.doFocusEnter()) {
            this.aircraft().hierMesh().chunkVisible("Windscreen_D0", false);
            this.aircraft().hierMesh().chunkVisible("Blister1_D0", false);
            this.aircraft().hierMesh().chunkVisible("Blister2_D0", false);
            this.mesh.chunkVisible("Z_Z_RETICLE3", this.aircraft().thisWeaponsName.endsWith("R4"));
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        if (!this.isFocused()) {
            return;
        } else {
            this.aircraft().hierMesh().chunkVisible("Windscreen_D0", true);
            this.aircraft().hierMesh().chunkVisible("Blister1_D0", true);
            this.aircraft().hierMesh().chunkVisible("Blister2_D0", true);
            super.doFocusLeave();
            return;
        }
    }

    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
    private float              pictManf1;
    private float              pictManf2;
    private BulletEmitter      gun[]              = { null, null, null, null, null };
    private static final float speedometerScale[] = { 0.0F, 21F, 69.5F, 116F, 163F, 215.5F, 266.5F, 318.5F, 378F, 430.5F };
    private static final float variometerScale[]  = { 0.0F, 47F, 82F, 97F, 112F, 111.7F, 132F };
    private static final float rpmScale[]         = { 0.0F, 2.5F, 19F, 50.5F, 102.5F, 173F, 227F, 266.5F, 297F };
    
    static {
        Property.set(CockpitHE_219.class, "normZN", 0.4F);
    }
}
