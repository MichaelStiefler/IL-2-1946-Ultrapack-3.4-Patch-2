package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public class CockpitJU_288_Gunner extends CockpitGunner {

    public void setRealMode(boolean isPlayerControlled) {
        boolean wasAIControlled = this.aiTurret().bIsAIControlled;
        super.setRealMode(isPlayerControlled);
        if (wasAIControlled != isPlayerControlled) {
            return;
        }
        boolean isAutoAutopilot = (Main3D.cur3D() == null || Main3D.cur3D().aircraftHotKeys == null)? false : Main3D.cur3D().aircraftHotKeys.isAutoAutopilot();
        
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
        return !this.aiTurret().bIsAIControlled;
    }

    public void moveGun(Orient orient) {
        if (this._aiTuretNum == -1) this._aiTuretNum = Property.intValue(this.getClass(), "aiTuretNum", 0);
        if (!this.fm.turret[this._aiTuretNum].bIsOperable) {
            System.out.println("### moveGun(), bIsOperable=false!!! ###");
            return;
        }
        this.mesh.chunkSetAngles("Turret1A", 180F - orient.getYaw(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Turret1B", 0.0F, orient.getTangage(), 0.0F);
        if (this.isRealMode()) {
            this.aiTurret().tu[0] = 360F - this.turretYaw;
            this.aiTurret().tu[1] = this.turretPitch;
        }
    }

    protected void interpTick() {
        if (this.isRealMode()) {
            if (this.saveGunFire) {
                this.hookGunner().gunFire(true);
            }
            if ((this.emitter == null) || !this.emitter.haveBullets() || !this.aiTurret().bIsOperable || !this.bGunfireEnabled) {
                this.bGunFire = false;
            }
            this.fm.CT.WeaponControl[this.weaponControlNum()] = this.bGunFire;
            if (this.bGunFire) {
                if (this.hook1 == null) {
                    this.hook1 = new HookNamed(this.aircraft(), this.firstGunHook);
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook1, this.firstGunHook);
                if (this.hook2 == null) {
                    this.hook2 = new HookNamed(this.aircraft(), this.secondGunHook);
                }
                this.doHitMasterAircraft(this.aircraft(), this.hook2, this.secondGunHook);
            }
        }
    }

    public void doGunFire(boolean flag) {
        if (this.isRealMode()) {
            if ((this.emitter == null) || !this.emitter.haveBullets() || !this.aiTurret().bIsOperable || !this.bGunfireEnabled) {
                this.bGunFire = false;
            } else {
                this.bGunFire = flag;
            }
            this.fm.CT.WeaponControl[this.weaponControlNum()] = this.bGunFire;
            this.saveGunFire = flag;
        }
    }

    public CockpitJU_288_Gunner(String hier, String name, int firstGunHookIndex) {
        super(hier, name);
        this.hook1 = null;
        this.hook2 = null;
        this.bGunfireEnabled = false;
        this.firstGunHook = "_MGUN" + (firstGunHookIndex > 9 ? "" : "0") + firstGunHookIndex;
        this.secondGunHook = "_MGUN" + ((firstGunHookIndex + 1) > 9 ? "" : "0") + (firstGunHookIndex + 1);
        this.turretPitch = this.turretYaw = 0F;
        this.saveGunFire = false;
    }

    private Hook    hook1;
    private Hook    hook2;
    boolean         bGunfireEnabled;
    private String  firstGunHook;
    private String  secondGunHook;
    float           turretPitch;
    float           turretYaw;
    private boolean saveGunFire;
}
