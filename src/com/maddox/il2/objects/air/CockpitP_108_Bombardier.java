package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Time;

public class CockpitP_108_Bombardier extends CockpitPilot
{
    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if(fm != null)
            {
                setTmp = setOld;
                setOld = setNew;
                setNew = setTmp;
                setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), 90F + fm.Or.azimut());
            }
            float f = ((P_108xyz)aircraft()).fSightCurForwardAngle;
            float f1 = 0.0F;
            if(bEntered)
            {
                HookPilot hookpilot = HookPilot.current;
                hookpilot.setSimpleAimOrient(aAim + f1, tAim + f, 0.0F);
            }
            return true;
        }

        Interpolater()
        {
        }
    }

    private class Variables
    {

        AnglesFork azimuth;

        private Variables()
        {
            azimuth = new AnglesFork();
        }
    }

    protected boolean doFocusEnter()
    {
        if(super.doFocusEnter())
        {
            HookPilot hookpilot = HookPilot.current;
            hookpilot.doAim(false);
            aircraft().hierMesh().chunkVisible("Nose_D0", false);
            aircraft().hierMesh().chunkVisible("Nose_D1", false);
            aircraft().hierMesh().chunkVisible("Nose_D2", false);
            aircraft().hierMesh().chunkVisible("Blister", false);
            aircraft().hierMesh().chunkVisible("Pitot", false);
            aircraft().hierMesh().chunkVisible("Int1", false);
            aircraft().hierMesh().chunkVisible("BlackBox", false);
            aircraft().hierMesh().chunkVisible("Turret1B_D0", false);
            enter();
            return true;
        } else
        {
            return false;
        }
    }

    protected void doFocusLeave()
    {
        if(!isFocused())
        {
            return;
        } else
        {
            aircraft().hierMesh().chunkVisible("Nose_D0", true);
            aircraft().hierMesh().chunkVisible("Nose_D1", true);
            aircraft().hierMesh().chunkVisible("Nose_D2", true);
            aircraft().hierMesh().chunkVisible("Blister", true);
            aircraft().hierMesh().chunkVisible("Pitot", true);
            aircraft().hierMesh().chunkVisible("Int1", true);
            aircraft().hierMesh().chunkVisible("BlackBox", true);
            aircraft().hierMesh().chunkVisible("Turret1B_D0", true);
            leave();
            super.doFocusLeave();
            return;
        }
    }

    private void enter()
    {
        HookPilot hookpilot = HookPilot.current;
        if(hookpilot.isPadlock())
            hookpilot.stopPadlock();
        hookpilot.doAim(true);
        hookpilot.setSimpleUse(true);
        hookpilot.setSimpleAimOrient(aAim, tAim, 0.0F);
        HotKeyEnv.enable("PanView", false);
        HotKeyEnv.enable("SnapView", false);
        bEntered = true;
    }

    private void leave()
    {
        if(bEntered)
        {
            HookPilot hookpilot = HookPilot.current;
            hookpilot.doAim(false);
            hookpilot.setSimpleUse(false);
            boolean flag = HotKeyEnv.isEnabled("aircraftView");
            HotKeyEnv.enable("PanView", flag);
            HotKeyEnv.enable("SnapView", flag);
            bEntered = false;
        }
    }

    public void destroy()
    {
        super.destroy();
        leave();
    }

    public void doToggleAim(boolean flag1)
    {
    }

    public CockpitP_108_Bombardier()
    {
        super("3DO/Cockpit/P108_B/hier.him", "he111");
        bEntered = false;
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        try
        {
            Loc loc = new Loc();
            HookNamed hooknamed1 = new HookNamed(this.mesh, "CAMERAAIM");
            hooknamed1.computePos(this, this.pos.getAbs(), loc);
            aAim = loc.getOrient().getAzimut();
            tAim = loc.getOrient().getTangage();
        }
        catch(Exception exception)
        {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f)
    {
        resetYPRmodifier();
        float f8 = this.fm.Or.getPitch();
        if(f8 > 360F)
            f8 -= 360F;
        f8 *= 0.00872664F;
        float f10 = ((P_108xyz)aircraft()).fSightSetForwardAngle - (float)Math.toRadians(f8);
        float f11 = (float)(0.16915999352931976D * Math.tan(f10));
        if(f11 < 0.032F)
            f11 = 0.032F;
        else
        if(f11 > 0.21F)
            f11 = 0.21F;
        float f12 = f11 * 0.667F;
        Cockpit.xyz[0] = f11;
        this.mesh.chunkSetLocate("ZCursor1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[0] = f12;
        this.mesh.chunkSetLocate("ZCursor2", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("Cylinder", 0.0F, ((P_108xyz)aircraft()).fSightCurSideslip, 0.0F);
    }

    private float aAim;
    private float tAim;
    private boolean bEntered;
    public Vector3f w;
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;

}
