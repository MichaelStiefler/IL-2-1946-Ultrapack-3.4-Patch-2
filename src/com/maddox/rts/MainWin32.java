package com.maddox.rts;

public class MainWin32 extends MainWindow
    implements MsgTimeOutListener
{

    public boolean isLoop()
    {
        return bLoop;
    }

    public void loop(boolean flag)
    {
        if(flag != bLoop)
            if(flag)
            {
                ticker.post();
                bLoop = true;
            } else
            {
                RTSConf.cur.queueRealTime.remove(ticker);
                RTSConf.cur.queueRealTimeNextTick.remove(ticker);
                bLoop = false;
            }
    }

    public void msgTimeOut(Object obj)
    {
        if(bLoop)
        {
            ticker.post();
            loopMsgs();
        }
    }

    public void loopMsgs()
    {
        int i = LoopMsgsDT();
        if(i != 0)
        {
            if((i & 2) != 0 && hWnd != 0)
            {
                hWnd = 0;
                bCreated = false;
                SendAction(2);
            }
            if((i & 4) != 0)
            {
                cx = WidthDT();
                cy = HeightDT();
                cxFull = WidthFullDT();
                cyFull = HeightFullDT();
                SendAction(4);
            }
            if((i & 8) != 0)
            {
                posX = PosXDT();
                posY = PosYDT();
                SendAction(8);
            }
            if((i & 0x10) != 0)
            {
                bFocused = IsFocusedDT();
                SendAction(16);
            }
        }
    }

    public boolean create(String s, int i, int j)
    {
        if(hWnd != 0)
            destroy();
        hWnd = CreateDT(s, i, j);
        if(hWnd == 0)
        {
            return false;
        } else
        {
            bCreated = true;
            bFullScreen = true;
            cx = WidthDT();
            cy = HeightDT();
            cxFull = WidthFullDT();
            cyFull = HeightFullDT();
            posX = PosXDT();
            posY = PosYDT();
            bFocused = IsFocusedDT();
            SendAction(1);
            return true;
        }
    }

    public boolean create(String s, boolean flag, boolean flag1, int i, int j)
    {
        if(hWnd != 0)
            destroy();
        hWnd = CreateDT(s, flag, flag1, i, j);
        if(hWnd == 0)
        {
            return false;
        } else
        {
            bCreated = true;
            bFullScreen = false;
            cx = WidthDT();
            cy = HeightDT();
            cxFull = WidthFullDT();
            cyFull = HeightFullDT();
            posX = PosXDT();
            posY = PosYDT();
            bFocused = IsFocusedDT();
            SendAction(1);
            return true;
        }
    }

    public void destroy()
    {
        if(hWnd == 0)
        {
            return;
        } else
        {
            DestroyDT();
            hWnd = 0;
            bCreated = false;
            SendAction(2);
            return;
        }
    }

    public void setTitle(String s)
    {
        if(hWnd == 0)
        {
            return;
        } else
        {
            SetTitleDT(s);
            return;
        }
    }

    public boolean isIconic()
    {
        return IsIconicDT();
    }

    public void showIconic()
    {
        ShowIconicDT();
    }

    public void showNormal()
    {
        ShowNormalDT();
    }

    public void setFocus()
    {
        setFocusDT();
    }

    public void setSize(int i, int j)
    {
        setSizeDT(i, j);
    }

    public void setPosSize(int i, int j, int k, int l)
    {
        setPosSizeDT(i, j, k, l);
    }

    public void SetTitle(String s)
    {
        SetTitleDT(s);
    }

    public int Width()
    {
        return WidthDT();
    }

    public int Height()
    {
        return HeightDT();
    }

    public boolean IsFocused()
    {
        return IsFocusedDT();
    }

    public static String RegistryGetAppPath(String s)
    {
        return RegistryGetAppPathDT(s);
    }

    public static String RegistryGetStringLM(String s, String s1)
    {
        return RegistryGetStringLMDT(s, s1);
    }

    public static String GetAppPath()
    {
        return GetAppPathDT();
    }

    public static String GetCDDrive(String s)
    {
        return GetCDDriveDT(s);
    }

    public static void SetAppPath(String s)
    {
        SetAppPathDT(s);
    }

    public static void ImmDisableIME()
    {
        ImmDisableIMEDT();
    }

    private native void setFocusDT();

    private native void setSizeDT(int i, int j);

    private native void setPosSizeDT(int i, int j, int k, int l);

    private native void SetTitleDT(String s);

    private native int CreateDT(String s, int i, int j);

    private native int CreateDT(String s, boolean flag, boolean flag1, int i, int j);

    private native void DestroyDT();

    private native int LoopMsgsDT();

    private native int WidthDT();

    private native int HeightDT();

    private native int WidthFullDT();

    private native int HeightFullDT();

    private native int PosXDT();

    private native int PosYDT();

    private native boolean IsFocusedDT();

    private native boolean IsIconicDT();

    private native void ShowIconicDT();

    private native void ShowNormalDT();

    private static native String RegistryGetAppPathDT(String s);

    private static native String RegistryGetStringLMDT(String s, String s1);

    private static native String GetAppPathDT();

    private static native String GetCDDriveDT(String s);

    private static native void SetAppPathDT(String s);

    private static native void ImmDisableIMEDT();

    protected MainWin32(int i, boolean flag)
    {
        bLoop = false;
        ticker = new MsgTimeOut(null);
        ticker.setTickPos(i);
        ticker.setNotCleanAfterSend();
        ticker.setFlags(88);
        ticker.setListener(this);
        loop(flag);
    }

    private MsgTimeOut ticker;
    private boolean bLoop;

    static 
    {
        RTS.loadNative();
    }
}
