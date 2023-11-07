package com.maddox.il2.engine.hotkey;

import com.maddox.JGP.Point3d;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.HotKeyCmdEnv;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Mouse;
import com.maddox.rts.MsgAddListener;
import com.maddox.rts.MsgMouseListener;

public class MouseXYZATK
    implements MsgMouseListener
{

    public void resetGame()
    {
        target = null;
    }

    public void setTarget(Actor actor)
    {
        target = actor;
    }

    public void setCamera(Actor actor)
    {
        camera = actor;
    }

    public boolean use(boolean flag)
    {
        boolean flag1 = bUse;
        bUse = flag;
        if(target != null)
            target.pos.inValidate(true);
        return flag1;
    }

    float DEG2RAD(float f)
    {
        return f * 0.01745329F;
    }

    public void msgMouseButton(int i, boolean flag)
    {
    }

    public void msgMouseAbsMove(int i, int j, int k)
    {
    }

    public void msgMouseMove(int i, int j, int k)
    {
        if(bUse && Actor.isValid(target) && Actor.isValid(camera) && iMode != UNKNOWN)
        {
            camera.pos.getAbs(cameraO);
            target.pos.getRel(p, o);
            switch(iMode)
            {
            case XY:
            case XYmove:
                double d = Math.sin(DEG2RAD(cameraO.azimut()));
                double d1 = Math.cos(DEG2RAD(cameraO.azimut()));
                p.x += -d * (double)i * (double)koof[iKoof] + d1 * (double)j * (double)koof[iKoof];
                p.y += -d1 * (double)i * (double)koof[iKoof] - d * (double)j * (double)koof[iKoof];
                break;

            case Z:
            case Zmove:
                p.z += (float)j * koof[iKoof];
                break;

            case AT:
                o.set(o.azimut() + (float)i * koofAngle, o.tangage() + (float)j * koofAngle, o.kren());
                break;

            case KT:
                o.set(o.azimut(), o.tangage() + (float)j * koofAngle, o.kren() + (float)i * koofAngle);
                break;

            case Amove:
                o.set(o.azimut() + (float)i * koofAngle, o.tangage(), o.kren());
                break;

            case Tmove:
                o.set(o.azimut(), o.tangage() + (float)i * koofAngle, o.kren());
                break;

            case Kmove:
                o.set(o.azimut(), o.tangage(), o.kren() + (float)i * koofAngle);
                break;
            }
            target.pos.setRel(p, o);
        }
    }

    private void initHotKeys(String s)
    {
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_01_SpeedSlow") {

            public void begin()
            {
                if(bUse)
                    iKoof = 0;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_SpeedSlow", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_02_SpeedNormal") {

            public void begin()
            {
                if(bUse)
                    iKoof = 1;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_SpeedNormal", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_03_SpeedFast") {

            public void begin()
            {
                if(bUse)
                    iKoof = 2;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_SpeedFast", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_10_DEFAULT") {

            public void begin()
            {
                if(bUse)
                    iMode = UNKNOWN;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_Default", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_11_XY") {

            public void begin()
            {
                if(bUse)
                    iMode = XY;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_XY", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_12_Z") {

            public void begin()
            {
                if(bUse)
                    iMode = Z;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_Z", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_13_AT") {

            public void begin()
            {
                if(bUse)
                    iMode = AT;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_AT", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MODE_14_KT") {

            public void begin()
            {
                if(bUse)
                    iMode = KT;
                Main3D.cur3D().hud.clearLog();
                Main3D.cur3D();
                HUD.log(0, "MouseXYZATK_KT", false);
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MOVE_11_XYmove") {

            public void begin()
            {
                saveHookUseMouse(XYmove);
            }

            public void end()
            {
                restoreHookUseMouse();
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MOVE_12_Zmove") {

            public void begin()
            {
                saveHookUseMouse(Zmove);
            }

            public void end()
            {
                restoreHookUseMouse();
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MOVE_13_Amove") {

            public void begin()
            {
                saveHookUseMouse(Amove);
            }

            public void end()
            {
                restoreHookUseMouse();
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MOVE_14_Kmove") {

            public void begin()
            {
                saveHookUseMouse(Kmove);
            }

            public void end()
            {
                restoreHookUseMouse();
            }

        }
);
        HotKeyCmdEnv.addCmd(s, new HotKeyCmd(bRealTime, "MOVE_15_Tmove") {

            public void begin()
            {
                saveHookUseMouse(Tmove);
            }

            public void end()
            {
                restoreHookUseMouse();
            }

        }
);
    }

    private void saveHookUseMouse(int i)
    {
        if(!bUse || iMode != UNKNOWN)
            return;
        iMode = i;
        if(HookView.current == null)
        {
            return;
        } else
        {
            bSaveHookUseMouse = HookView.isUseMouse();
            HookView.useMouse(false);
            return;
        }
    }

    private void restoreHookUseMouse()
    {
        if(!bUse || iMode == UNKNOWN)
            return;
        iMode = UNKNOWN;
        if(HookView.current == null)
        {
            return;
        } else
        {
            HookView.useMouse(bSaveHookUseMouse);
            return;
        }
    }

    public MouseXYZATK(String s)
    {
        bRealTime = false;
        koofAngle = 0.1F;
        iKoof = 1;
        iMode = UNKNOWN;
        target = null;
        camera = null;
        bUse = true;
        o = new Orient();
        p = new Point3d();
        cameraO = new Orient();
        String s1 = s + " Config";
        bRealTime = Config.cur.ini.get(s1, "RealTime", bRealTime);
        HotKeyEnv.fromIni(s, Config.cur.ini, s);
        MsgAddListener.post(bRealTime ? 64 : 0, Mouse.adapter(), this, null);
        initHotKeys(s);
    }

    private boolean bRealTime;
    private static final int UNKNOWN = -1;
    private static final int XY = 0;
    private static final int Z = 1;
    private static final int AT = 2;
    private static final int KT = 3;
    private static final int XYmove = 4;
    private static final int Zmove = 5;
    private static final int Amove = 6;
    private static final int Tmove = 7;
    private static final int Kmove = 8;
    private float koofAngle;
    private float koof[] = {
        0.02F, 1.0F, 10F
    };
    private int iKoof;
    private int iMode;
    private Actor target;
    private Actor camera;
    private boolean bUse;
    private Orient o;
    private Point3d p;
    private Orient cameraO;
    private boolean bSaveHookUseMouse;

}
