package com.maddox.rts;

public final class KeyboardDX
    implements MsgTimeOutListener
{

    public final boolean isCreated()
    {
        return bCreated;
    }

    public final void create(int i)
        throws KeyboardDXException
    {
        if(bCreated)
        {
            setCooperativeLevel(i);
            return;
        }
        checkCoopLevel(i);
        if(RTSConf.cur.mainWindow.hWnd() == 0)
        {
            throw new KeyboardDXException("Keyboard DirectX driver: main window not present");
        } else
        {
            nCreateDT(i);
            level = i;
            ticker.post();
            bCreated = true;
            return;
        }
    }

    public final void create()
        throws KeyboardDXException
    {
        create(level);
    }

    public final void setCooperativeLevel(int i)
        throws KeyboardDXException
    {
        checkCoopLevel(i);
        if(bCreated)
            nSetCoopLevelDT(i);
        level = i;
    }

    public final void destroy()
    {
        if(bCreated)
        {
            nDestroyDT();
            RTSConf.cur.queueRealTime.remove(ticker);
            RTSConf.cur.queueRealTimeNextTick.remove(ticker);
            bCreated = false;
        }
    }

    public void msgTimeOut(Object obj)
    {
        if(bCreated)
        {
            while(nGetMsgDT(param)) 
            {
                long l = Time.realFromRawClamp(param[2]);
                switch(param[0])
                {
                case 0: // '\0'
                    RTSConf.cur.keyboard.setPress(l, param[1]);
                    break;

                case 1: // '\001'
                    RTSConf.cur.keyboard.setRelease(l, param[1]);
                    break;
                }
            }
            ticker.post();
        }
    }

    private static final void checkCoopLevel(int i)
        throws KeyboardDXException
    {
        if(i < 0 || i > 1)
            throw new KeyboardDXException("Keyboard DirectX driver: unknown cooperative level = " + i);
        else
            return;
    }

    protected KeyboardDX(int i, int j, boolean flag)
    {
        param = new int[3];
        bCreated = false;
        checkCoopLevel(j);
        level = j;
        ticker = new MsgTimeOut(null);
        ticker.setTickPos(i);
        ticker.setNotCleanAfterSend();
        ticker.setFlags(88);
        ticker.setListener(this);
        if(flag)
            create();
    }

    private static final native void nCreateDT(int i)
        throws KeyboardDXException;

    private static final native void nDestroyDT();

    private static final native void nSetCoopLevelDT(int i);

    private static final native boolean nGetMsgDT(int ai[]);

    public static final int NONEXCLUSIVE_BACKGROUND = 0;
    public static final int NONEXCLUSIVE_FOREGROUND = 1;
    private static final int PRESS = 0;
    private static final int RELEASE = 1;
    private boolean bCreated;
    private int level;
    private int param[];
    private MsgTimeOut ticker;

    static 
    {
        RTS.loadNative();
    }
}
