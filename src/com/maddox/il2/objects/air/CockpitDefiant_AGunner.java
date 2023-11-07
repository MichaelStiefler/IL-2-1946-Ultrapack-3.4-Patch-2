package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;
import com.maddox.sas1946.il2.util.CommonTools;

public class CockpitDefiant_AGunner extends CockpitGunner {

    public void setRealMode(boolean isPlayerControlled) {
        if (((BP_Defiant) this.aircraft()).getTurretMode() != BP_Defiant.TURRET_MODE_NORMAL) {
            return;
        }
        boolean wasAIControlled = this.aiTurret().bIsAIControlled;
        super.setRealMode(isPlayerControlled);
        if (wasAIControlled != isPlayerControlled) {
            return;
        }
        boolean isAutoAutopilot = ((Main3D.cur3D() == null) || (Main3D.cur3D().aircraftHotKeys == null)) ? false : Main3D.cur3D().aircraftHotKeys.isAutoAutopilot();
        if (!isPlayerControlled && !isAutoAutopilot) {
            this.aircraft().hierMesh().setCurChunk(this.aiTurret().indexA);
            this.aircraft().hierMesh().chunkSetAngles(this.aiTurret().tu);
            this.aircraft().hierMesh().setCurChunk(this.aiTurret().indexB);
            this.aircraft().hierMesh().chunkSetAngles(this.aiTurret().tu);
        }
    }

    public boolean isRealMode() {
        if (Config.isUSE_RENDER() && (this.aircraft() == World.getPlayerAircraft()) && (Main3D.cur3D() != null) && (Main3D.cur3D().cockpits[Main3D.cur3D().cockpitCurIndx()] != this)) {
            return false;
        }
        return (((BP_Defiant) this.aircraft()).getTurretMode() == BP_Defiant.TURRET_MODE_NORMAL) && !this.aiTurret().bIsAIControlled;
    }

    public void moveGun(Orient orient) {
        this.mesh.chunkSetAngles("TurretA", 0.0F, -orient.getYaw(), 0.0F);
        this.mesh.chunkSetAngles("TurretB", 0.0F, orient.getTangage(), 0.0F);
        this.mesh.chunkSetAngles("TurretC", 0.0F, orient.getYaw(), 0.0F);
        this.mesh.chunkSetAngles("TurretE", -orient.getYaw(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("TurretF", 0.0F, orient.getTangage(), 0.0F);
        this.mesh.chunkSetAngles("TurretG", -orient.getYaw(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("TurretH", 0.0F, orient.getTangage(), 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[0] = this.cvt(Math.max(Math.abs(orient.getYaw()), Math.abs(orient.getTangage())), 0.0F, 20F, 0.0F, 0.3F);
        this.mesh.chunkSetLocate("TurretI", Cockpit.xyz, Cockpit.ypr);
        if (this.isRealMode()) {
            this.aiTurret().tu[0] = 360F - this.turretYaw;
            this.aiTurret().tu[1] = this.turretPitch;
        }
    }

    public void clipAnglesGun(Orient orient) {
        if (this.isRealMode()) {
            if (!this.aiTurret().bIsOperable) {
                orient.setYPR(0.0F, 0.0F, 0.0F);
            } else {
                this.turretYaw = orient.getYaw();
                this.turretPitch = orient.getTangage();
                if (this.turretPitch > 84F) {
                    this.turretPitch = 84F;
                }
                if (this.turretPitch < 0F) {
                    this.turretPitch = 0F;
                }
                if ((Math.abs(this.turretYaw) > 100F) && (Math.abs(this.turretYaw) <= 175F)) {
                    this.turretPitch = Math.max(this.turretPitch, CommonTools.smoothCvt(Math.abs(this.turretYaw), 100F, 115F, 0F, Aircraft.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 19F, 60F)));
                } else if (Math.abs(this.turretYaw) > 175F) {
                    this.turretPitch = Math.max(this.turretPitch, CommonTools.smoothCvt(Math.abs(this.turretYaw), 175F, 179F, Aircraft.cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 19F, 60F), 19F));
                }
                orient.setYPR(this.turretYaw, this.turretPitch, 0.0F);
                orient.wrap();
            }
        }
    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            this.aircraft().hierMesh().chunkVisible("Turret1B_D0", false);
            this.aircraft().hierMesh().chunkVisible("Turret2B_D0", false);
            this.aircraft().hierMesh().chunkVisible("Turret3A_D0", false);
            this.aircraft().hierMesh().chunkVisible("Turret3B_D0", false);
            this.aircraft().hierMesh().chunkVisible("Turret3B2_D0", false);
            this.aircraft().hierMesh().chunkVisible("Turret6B_D0", false);
            if (((BP_Defiant) this.aircraft()).getTurretMode() != BP_Defiant.TURRET_MODE_NORMAL) {
                this.hookGunner().resetMove(this.aircraft().FM.turret[0].tu[0], this.aircraft().FM.turret[0].tu[1]);
            }
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        if (this.isFocused()) {
            this.aircraft().hierMesh().chunkVisible("Turret1B_D0", true);
            this.aircraft().hierMesh().chunkVisible("Turret2B_D0", true);
            this.aircraft().hierMesh().chunkVisible("Turret3A_D0", true);
            this.aircraft().hierMesh().chunkVisible("Turret3B_D0", true);
            this.aircraft().hierMesh().chunkVisible("Turret3B2_D0", true);
            this.aircraft().hierMesh().chunkVisible("Turret6B_D0", true);
            super.doFocusLeave();
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
                    this.hook1 = new HookNamed(this.aircraft(), "_MGUN01");
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook1, "_MGUN01");
                if (this.hook2 == null) {
                    this.hook2 = new HookNamed(this.aircraft(), "_MGUN02");
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook2, "_MGUN02");
                if (this.hook3 == null) {
                    this.hook3 = new HookNamed(this.aircraft(), "_MGUN03");
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook3, "_MGUN03");
                if (this.hook4 == null) {
                    this.hook4 = new HookNamed(this.aircraft(), "_MGUN04");
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook4, "_MGUN04");
            }
        }
        this.animateFairings(((BP_Defiant) this.aircraft()).getTurretLockPosition());
    }

    private void animateFairings(float lockPos) {
        this.mesh.chunkSetAngles("Fairing1_D0", 0.0F, 0.0F, CommonTools.smoothCvt(lockPos, 0F, 1F, -6F, 0F));
        this.mesh.chunkSetAngles("Fairing2_D0", 0.0F, 0.0F, CommonTools.smoothCvt(lockPos, 0F, 1F, 23F, 0F));
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

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
    }

    protected void reflectPlaneMats() {
        this.mesh.materialReplace("Gloss1D0o", this.aircraft().hierMesh().material(this.aircraft().hierMesh().materialFind("Gloss1D0o")));
        this.mesh.materialReplace("Gloss2D0o", this.aircraft().hierMesh().material(this.aircraft().hierMesh().materialFind("Gloss2D0o")));
    }

    public CockpitDefiant_AGunner() {
        super("3DO/Cockpit/Defiant-AGun/hier.him", "bf109");
        this.hook1 = null;
        this.hook2 = null;
        this.hook3 = null;
        this.hook4 = null;
        this.turretPitch = this.turretYaw = 0F;
        this.bNeedSetUp = true;
    }

    private Hook    hook1;
    private Hook    hook2;
    private Hook    hook3;
    private Hook    hook4;
    float           turretPitch;
    float           turretYaw;
    private boolean bNeedSetUp;

    static {
        Property.set(CockpitDefiant_AGunner.class, "aiTuretNum", 0);
        Property.set(CockpitDefiant_AGunner.class, "weaponControlNum", 10);
        Property.set(CockpitDefiant_AGunner.class, "astatePilotIndx", 1);
    }
}
