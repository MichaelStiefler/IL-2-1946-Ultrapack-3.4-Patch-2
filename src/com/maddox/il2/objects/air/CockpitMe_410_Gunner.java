package com.maddox.il2.objects.air;

import com.maddox.JGP.Geom;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Orient;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Property;

public class CockpitMe_410_Gunner extends CockpitGunner {

    private Hook hook1;
    private Hook hook2;

    static {
        Property.set(CockpitMe_410_Gunner.class, "aiTuretNum", 0);
        Property.set(CockpitMe_410_Gunner.class, "weaponControlNum", 10);
        Property.set(CockpitMe_410_Gunner.class, "astatePilotIndx", 1);
    }

    public CockpitMe_410_Gunner() {
        super("3DO/Cockpit/A-20G-TGun/GunnerMe410.him", "he111_gunner");
        this.hook1 = null;
        this.hook2 = null;
    }

    public void clipAnglesGun(Orient orient) {
        if (this.isRealMode()) {
            if (!this.aiTurret().bIsOperable) {
                orient.setYPR(0.0F, 0.0F, 0.0F);
            } else {
                float f = orient.getYaw();
                float f1 = orient.getTangage();
                for (; f < -180F; f += 360F) {
                    ;
                }
                for (; f > 180F; f -= 360F) {
                    ;
                }
                if (f < -45F) {
                    f = -45F;
                }
                if (f > 45F) {
                    f = 45F;
                }
                if (f1 > 85F) {
                    f1 = 85F;
                }
                if (f1 < -45F) {
                    f1 = -45F;
                }
                orient.setYPR(f, f1, 0.0F);
                orient.wrap();
            }
        }
    }

    public void doGunFire(boolean flag) {
        if (this.isRealMode()) {
            if ((this.emitter == null) || !this.emitter.haveBullets() || !this.aiTurret().bIsOperable) {
                this.bGunFire = false;
            } else {
                this.bGunFire = flag;
            }
            this.fm.CT.WeaponControl[this.weaponControlNum()] = this.bGunFire;
        }
    }

    protected void interpTick() {
        if (this.isRealMode()) {
            if ((this.emitter == null) || !this.emitter.haveBullets() || !this.aiTurret().bIsOperable) {
                this.bGunFire = false;
            }
            this.fm.CT.WeaponControl[this.weaponControlNum()] = this.bGunFire;
            if (this.bGunFire) {
                if (this.hook1 == null) {
                    this.hook1 = new HookNamed(this.aircraft(), "_MGUN03");
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook1, "_MGUN03");
                if (this.hook2 == null) {
                    this.hook2 = new HookNamed(this.aircraft(), "_MGUN04");
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook2, "_MGUN04");
            }
        }
    }

    public void moveGun(Orient orient) {
        if (this.isRealMode()) {
// HUD.training("Az=" + orient.getAzimut() + " Ta=" + orient.getTangage());
            float degTa = orient.getTangage();
            float degAz = orient.getAzimut();
// float radTa = Geom.DEG2RAD(degTa);
            float radAz = Geom.DEG2RAD(degAz - 360.0F);
            degTa /= Math.cos(radAz);
// degAz = (float) (360.0F + ((degAz - 360.0F) * Math.cos(radAz)));
            float degAz0 = Math.min(365, degAz);
            float degAz1 = Math.max(355, degAz);

// HUD.training("Az0=" + degAz0 + " Az1=" + degAz1 + " Ta=" + degTa);

            this.fm.turret[0].tu[1] = -degAz0;
            this.fm.turret[0].tu[0] = degTa;
            this.fm.turret[1].tu[1] = -degAz1;
            this.fm.turret[1].tu[0] = degTa;
        }
        this.mesh.chunkSetAngles("Body", -180F, 0.0F, 180F);
        this.mesh.chunkSetAngles("Turret1A", orient.getYaw(), 180F, 180F);
        this.mesh.chunkSetAngles("Turret1B", 180F, -orient.getTangage(), 180F);
    }

    public void setRealMode(boolean flag) {
        if (this.aiTurret().bIsAIControlled != flag) {
            return;
        }
        this.fm.turret[0].bIsAIControlled = (!flag);
        this.fm.turret[0].target = null;
        this.fm.turret[1].bIsAIControlled = (!flag);
        this.fm.turret[1].target = null;
        this.fm.CT.WeaponControl[this.weaponControlNum()] = false;
        this.bGunFire = false;
        if (flag) {
            this.hookGunner().resetMove(0.0F, 0.0F);
        } else {
            this.fm.turret[0].tu[0] = 0.0F;
            this.fm.turret[0].tu[1] = 0.0F;
            this.fm.turret[1].tu[0] = 0.0F;
            this.fm.turret[1].tu[1] = 0.0F;
        }
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
            this.mesh.chunkVisible("Z_Holes1_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            this.mesh.chunkVisible("Z_Holes2_D1", true);
        }
    }

    public void reflectWorldToInstruments(float f) {
    }

    public boolean isEnableFocusing() {
        if (!this.aircraft().thisWeaponsName.toLowerCase().startsWith("nj")) {
            return super.isEnableFocusing();
        }
        HotKeyCmd.exec("aircraftView", "cockpitSwitch3"); // Switch to Pilot Cockpit if Night Fighter
        return false;
    }
}
