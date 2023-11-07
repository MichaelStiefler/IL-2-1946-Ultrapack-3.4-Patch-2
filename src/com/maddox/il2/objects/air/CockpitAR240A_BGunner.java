package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Property;

public class CockpitAR240A_BGunner extends CockpitGunner
{

    protected boolean doFocusEnter()
    {
        if(!super.doFocusEnter())
            return false;
        saveFov = Main3D.FOVX;
        if(aiTurret().bIsOperable)
        {
            CmdEnv.top().exec("fov 67.5");
            Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(false);
        }
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
        bEntered = true;
        aircraft().FM.turret[1].bIsAIControlled = true;
        aircraft().FM.turret[0].bIsAIControlled = onAuto;
        return true;
    }

    protected void doFocusLeave()
    {
        Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(true);
        CmdEnv.top().exec("fov " + saveFov);
        boolean flag = HotKeyEnv.isEnabled("aircraftView");
        HotKeyEnv.enable("PanView", flag);
        HotKeyEnv.enable("SnapView", flag);
        bEntered = false;
        onAuto = aircraft().FM.turret[0].bIsAIControlled;
        super.doFocusLeave();
    }

    public void moveGun(Orient orient)
    {
        super.moveGun(orient);
        this.mesh.chunkSetAngles("Turret1A", orient.getYaw(), 180F, 180F);
        this.mesh.chunkSetAngles("Turret1B", 180F, -orient.getTangage(), 180F);
        this.mesh.chunkSetAngles("Body", 180F, 0.0F, 180F);
    }

    public void clipAnglesGun(Orient orient)
    {
        if(!isRealMode())
            return;
        if(!aiTurret().bIsOperable)
        {
            orient.setYPR(0.0F, 0.0F, 0.0F);
            return;
        }
        float f = orient.getYaw();
        float f1 = orient.getTangage();
        if(f < -66F)
            f = -66F;
        if(f > 66F)
            f = 66F;
        if(f1 < -70F)
            f1 = -70F;
        float f2 = (Math.abs(f) / 66F) * 2.5F;
        if(f1 > f2)
            f1 = f2;
        orient.setYPR(f, f1, 0.0F);
        orient.wrap();
    }

    protected void interpTick()
    {
        if(isRealMode())
        {
            if(this.emitter == null || !this.emitter.haveBullets() || !aiTurret().bIsOperable)
                this.bGunFire = false;
            this.fm.CT.WeaponControl[weaponControlNum()] = this.bGunFire;
            if(this.bGunFire)
            {
                if(hook1 == null)
                    hook1 = new HookNamed(aircraft(), "_MGUN03");
                doHitMasterAircraft(aircraft(), hook1, "_MGUN03");
                if(hook2 == null)
                    hook2 = new HookNamed(aircraft(), "_MGUN04");
                doHitMasterAircraft(aircraft(), hook2, "_MGUN04");
            }
        }
        if(!bEntered)
            return;
        float af[] = {
            0.0F, 0.0F, 0.0F
        };
        float af1[] = {
            0.0F, 0.0F, 0.0F
        };
        if(!aiTurret().bIsOperable)
        {
            Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(true);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Peribox", false);
            af[1] = 6.9F;
            af[2] = 1.75F;
            af1[0] = 180F;
            af1[1] = 0.0F;
            af1[2] = 180F;
        } else
        {
            CmdEnv.top().exec("fov 67.5");
            Main3D.cur3D().aircraftHotKeys.setEnableChangeFov(false);
            this.mesh.chunkVisible("Z_Z_RETICLE", true);
            this.mesh.chunkVisible("Peribox", true);
            af1[0] = 180F;
            af1[1] = 0.0F;
            af1[2] = 180F;
        }
        this.mesh.chunkSetLocate("Body", af, af1);
    }

    public void doGunFire(boolean flag)
    {
        if(isRealMode())
        {
            if(this.emitter == null || !this.emitter.haveBullets() || !aiTurret().bIsOperable)
                this.bGunFire = false;
            else
                this.bGunFire = flag;
            this.fm.CT.WeaponControl[weaponControlNum()] = this.bGunFire;
        }
    }

    public void reflectCockpitState()
    {
    }

    public CockpitAR240A_BGunner()
    {
        super("3DO/Cockpit/Ar240A-TGun/Ar240AB.him", "bf109");
        bEntered = false;
        hook1 = null;
        hook2 = null;
        onAuto = true;
    }

    private float saveFov;
    private boolean bEntered;
    private Hook hook1;
    private Hook hook2;
    private boolean onAuto;

    static 
    {
        Class class1 = CockpitAR240A_BGunner.class;
        Property.set(class1, "aiTuretNum", 1);
        Property.set(class1, "weaponControlNum", 11);
        Property.set(class1, "astatePilotIndx", 2);
    }
}
