package com.maddox.rts;

public class RTSConfWin extends RTSConf
{

    private int doUseMouse(int i)
    {
        mouse.setMouseCursorAdapter(null);
        if(i == 0)
        {
            mouseDX.destroy();
            mouseWin.destroy();
            mouse.setComputePos(false, false);
        } else
        if(i == 1)
        {
            if(mainWindow.hWnd() != 0)
            {
                mouseDX.destroy();
                mouseWin.create();
                mouse.setComputePos(false, true);
                mouse.setMouseCursorAdapter(mouseWin);
            }
        } else
        if(mainWindow.hWnd() != 0)
        {
            mouseDX.create(2);
            mouseWin.destroy();
            mouse.setComputePos(true, false);
        }
        mouse._clear();
        return i;
    }

    public void setUseMouse(int i)
    {
        if(useMouse == i)
        {
            return;
        } else
        {
            super.setUseMouse(doUseMouse(i));
            return;
        }
    }

    private boolean doUseJoy(boolean flag)
    {
        if(flag)
        {
            if(mainWindow.hWnd() != 0)
                try
                {
                    joyDX.create();
                }
                catch(Exception exception)
                {
                    System.out.println("DirectX Joystick NOT created: " + exception.getMessage());
                    try
                    {
                        joyWin.create();
                    }
                    catch(Exception exception1) { }
                }
            joy._clear();
            return true;
        } else
        {
            joyDX.destroy();
            joyWin.destroy();
            joy._clear();
            return false;
        }
    }

    public void useJoy(boolean flag)
    {
        if(bUseJoy == flag)
        {
            return;
        } else
        {
            super.useJoy(doUseJoy(flag));
            return;
        }
    }

    public void start()
    {
        if(!bStarted)
        {
            useMouse = doUseMouse(useMouse);
            if(mainWindow.hWnd() != 0)
            {
                keyboardDX.create();
                keyboardWin.create();
            }
            keyboard._clear();
            bUseJoy = doUseJoy(bUseJoy);
            if(bUseTrackIR)
            {
                trackIRWin.create();
                trackIR.setExist(trackIRWin.isCreated());
            }
            RTSConf.cur.hotKeyEnvs.resetGameCreate();
            super.start();
        }
    }

    public void stop()
    {
        if(bStarted)
        {
            doUseMouse(0);
            keyboardDX.destroy();
            keyboardWin.destroy();
            keyboard._clear();
            doUseJoy(false);
            if(bUseTrackIR)
            {
                trackIRWin.destroy();
                trackIR.setExist(false);
            }
            RTSConf.cur.hotKeyEnvs.resetGameClear();
            super.stop();
        }
    }

    public RTSConfWin()
    {
        super(null);
        mainWindow = new MainWin32(-10005, true);
        mouseDX = new MouseDX(-10004, 2, false);
        mouseWin = new MouseWin(-10003, false);
        keyboardDX = new KeyboardDX(-10002, 1, false);
        keyboardWin = new KeyboardWin(-10001, false);
        keyboardWin.setOnlyChars(true);
        joyDX = new JoyDX(100L, 3, false);
        joyWin = new JoyWin(100L, false);
        trackIRWin = new TrackIRWin(-10010, false);
    }

    public RTSConfWin(IniFile inifile, String s, int i)
    {
        super(null, inifile, s, i);
        mainWindow = new MainWin32(-10005, true);
        useMouse = inifile.get(s, "mouseUse", useMouse, 0, 2);
        mouseDX = new MouseDX(-10004, 2, false);
        mouseWin = new MouseWin(-10003, false);
        keyboardDX = new KeyboardDX(-10002, 1, false);
        keyboardWin = new KeyboardWin(-10001, false);
        keyboardWin.setOnlyChars(true);
        bUseJoy = inifile.get(s, "joyUse", bUseJoy);
        long l = inifile.get(s, "joyTick", 100);
        if(l < 1L)
            l = 1L;
        else
        if(l > 1000L)
            l = 1000L;
        joyDX = new JoyDX(l, 3, false);
        joyWin = new JoyWin(l, false);
        bUseTrackIR = inifile.get(s, "trackIRUse", bUseTrackIR);
        trackIRWin = new TrackIRWin(-10010, false);
    }

    public MouseDX mouseDX;
    public MouseWin mouseWin;
    public KeyboardDX keyboardDX;
    public KeyboardWin keyboardWin;
    public JoyDX joyDX;
    public JoyWin joyWin;
    public TrackIRWin trackIRWin;
}
