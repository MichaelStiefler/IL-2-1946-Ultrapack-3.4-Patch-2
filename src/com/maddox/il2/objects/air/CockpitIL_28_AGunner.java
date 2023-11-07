package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Orient;
import com.maddox.rts.Property;

public class CockpitIL_28_AGunner extends CockpitGunner
{

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            reflectPlaneToModel();
            bNeedSetUp = false;
        }
        float f1 = this.fm.CT.getElevator();
        this.mesh.chunkSetAngles("VatorL_D0", 0.0F, -30F * f1, 0.0F);
        this.mesh.chunkSetAngles("VatorR_D0", 0.0F, -30F * f1, 0.0F);
    }

    protected void reflectPlaneMats()
    {
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 8) != 0)
            this.mesh.chunkVisible("XGlassDamage1", true);
        if((this.fm.AS.astateCockpitState & 0x20) != 0)
            this.mesh.chunkVisible("XGlassDamage2", true);
    }

    protected void reflectPlaneToModel()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        this.mesh.chunkVisible("VatorL_D0", hiermesh.isChunkVisible("VatorL_D0"));
        this.mesh.chunkVisible("VatorL_D1", hiermesh.isChunkVisible("VatorL_D1"));
        this.mesh.chunkVisible("VatorL_CAP", hiermesh.isChunkVisible("VatorL_CAP"));
        this.mesh.chunkVisible("VatorR_D0", hiermesh.isChunkVisible("VatorR_D0"));
        this.mesh.chunkVisible("VatorR_D1", hiermesh.isChunkVisible("VatorR_D1"));
        this.mesh.chunkVisible("VatorR_CAP", hiermesh.isChunkVisible("VatorR_CAP"));
    }

    public void moveGun(Orient orient)
    {
        super.moveGun(orient);
        this.mesh.chunkSetAngles("TurretA", 0.0F, -orient.getYaw(), 0.0F);
        this.mesh.chunkSetAngles("TurretB", 0.0F, orient.getTangage(), 0.0F);
        this.mesh.chunkSetAngles("TurretC", 0.0F, cvt(orient.getYaw(), -38F, 38F, -15F, 15F), 0.0F);
        this.mesh.chunkSetAngles("TurretD", 0.0F, cvt(orient.getTangage(), -43F, 43F, -10F, 10F), 0.0F);
        this.mesh.chunkSetAngles("TurretE", -orient.getYaw(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("TurretF", 0.0F, orient.getTangage(), 0.0F);
        this.mesh.chunkSetAngles("TurretG", -cvt(orient.getYaw(), -33F, 33F, -33F, 33F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("TurretH", 0.0F, cvt(orient.getTangage(), -10F, 32F, -10F, 32F), 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[0] = cvt(Math.max(Math.abs(orient.getYaw()), Math.abs(orient.getTangage())), 0.0F, 20F, 0.0F, 0.3F);
        this.mesh.chunkSetLocate("TurretI", Cockpit.xyz, Cockpit.ypr);
    }

    public void clipAnglesGun(Orient orient)
    {
        if(isRealMode())
            if(!aiTurret().bIsOperable)
            {
                orient.setYPR(0.0F, 0.0F, 0.0F);
            } else
            {
                float f = orient.getYaw();
                float f1 = orient.getTangage();
                if(f < -33F)
                    f = -33F;
                if(f > 33F)
                    f = 33F;
                if(f1 > 43F)
                    f1 = 43F;
                if(f1 < -42F)
                    f1 = -42F;
                orient.setYPR(f, f1, 0.0F);
                orient.wrap();
            }
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
                    hook1 = new HookNamed(aircraft(), "_CANNON03");
                doHitMasterAircraft(aircraft(), hook1, "_CANNON03");
                if(hook2 == null)
                    hook2 = new HookNamed(aircraft(), "_CANNON04");
                doHitMasterAircraft(aircraft(), hook2, "_CANNON04");
            }
        }
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

    public CockpitIL_28_AGunner()
    {
        super("3do/Cockpit/IL-28-AGun/AGunnerIL28.him", "bf109");
        bNeedSetUp = true;
        hook1 = null;
        hook2 = null;
    }

    private boolean bNeedSetUp;
    private Hook hook1;
    private Hook hook2;

    static 
    {
        Property.set(CockpitIL_28_AGunner.class, "aiTuretNum", 0);
        Property.set(CockpitIL_28_AGunner.class, "weaponControlNum", 10);
        Property.set(CockpitIL_28_AGunner.class, "astatePilotIndx", 2);
    }
}
