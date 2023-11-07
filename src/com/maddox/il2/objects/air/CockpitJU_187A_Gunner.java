package com.maddox.il2.objects.air;

import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Orient;
import com.maddox.rts.Property;

public class CockpitJU_187A_Gunner extends CockpitGunner
{

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
    }

    public void moveGun(Orient orient)
    {
        super.moveGun(orient);
        this.mesh.chunkSetAngles("Body", 0.0F, 0.0F, -1F);
        this.mesh.chunkSetAngles("Turret1A", -orient.getYaw(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Turret1B", 0.0F, orient.getTangage(), 0.0F);
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
                if(f < -70F)
                    f = -70F;
                if(f > 70F)
                    f = 70F;
                if(f1 > 40F)
                    f1 = 40F;
                if(f1 < -5F)
                    f1 = -5F;
                orient.setYPR(f, f1, 0.0F);
                orient.wrap();
            }
    }

    protected void interpTick()
    {
        if(!isRealMode())
            return;
        if(emitter == null || !emitter.haveBullets() || !aiTurret().bIsOperable)
            bGunFire = false;
        fm.CT.WeaponControl[weaponControlNum()] = bGunFire;
        if(bGunFire)
        {
            if(hook1 == null)
                hook1 = new HookNamed(aircraft(), "_MGUN05");
            doHitMasterAircraft(aircraft(), hook1, "_MGUN05");
            if(hook2 == null)
                hook2 = new HookNamed(aircraft(), "_MGUN06");
            doHitMasterAircraft(aircraft(), hook2, "_MGUN06");
        }
    }

    public void doGunFire(boolean flag)
    {
        if(!isRealMode())
            return;
        if(emitter == null || !emitter.haveBullets() || !aiTurret().bIsOperable)
            bGunFire = false;
        else
            bGunFire = flag;
        fm.CT.WeaponControl[weaponControlNum()] = bGunFire;
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        mesh.materialReplace("Gloss1D0o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Matt1D0o"));
        mesh.materialReplace("Matt1D0o", mat);
    }

    public void reflectCockpitState()
    {
    }

    public CockpitJU_187A_Gunner()
    {
        super("3DO/Cockpit/JU187A-Gunner/hier.him", "he111_gunner");
        bNeedSetUp = true;
        hook1 = null;
        hook2 = null;
    }

    private boolean bNeedSetUp;
    private Hook hook1;
    private Hook hook2;

    static 
    {
        Property.set(CockpitJU_187A_Gunner.class, "aiTuretNum", 0);
        Property.set(CockpitJU_187A_Gunner.class, "weaponControlNum", 10);
        Property.set(CockpitJU_187A_Gunner.class, "astatePilotIndx", 1);
    }
}
