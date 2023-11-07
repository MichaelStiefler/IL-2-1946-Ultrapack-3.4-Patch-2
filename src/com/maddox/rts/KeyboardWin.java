package com.maddox.rts;

public final class KeyboardWin
    implements MsgTimeOutListener
{

    public final boolean isCreated()
    {
        return bCreated;
    }

    public final boolean isOnlyChars()
    {
        return bOnlyChars;
    }

    public final void setOnlyChars(boolean flag)
    {
        bOnlyChars = flag;
    }

    public final void create()
        throws KeyboardWinException
    {
        if(bCreated)
            return;
        if(RTSConf.cur.mainWindow.hWnd() == 0)
        {
            throw new KeyboardWinException("Keyboard windows driver: main window not present");
        } else
        {
            nCreateDT();
            ticker.post();
            bCreated = true;
            return;
        }
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
                default:
                    break;

                case 0: // '\0'
                    if(!bOnlyChars)
                        RTSConf.cur.keyboard.setPress(l, param[1]);
                    break;

                case 1: // '\001'
                    if(!bOnlyChars)
                        RTSConf.cur.keyboard.setRelease(l, param[1]);
                    break;

                case 2: // '\002'
                    RTSConf.cur.keyboard.setChar(l, param[1]);
                    break;
                }
            }
            ticker.post();
        }
    }

    protected KeyboardWin(int i, boolean flag)
    {
        bOnlyChars = false;
        param = new int[3];
        bCreated = false;
        ticker = new MsgTimeOut(null);
        ticker.setTickPos(i);
        ticker.setNotCleanAfterSend();
        ticker.setFlags(88);
        ticker.setListener(this);
        if(flag)
            create();
    }

    private static final native void nCreateDT();

    private static final native void nDestroyDT();

    private static final native boolean nGetMsgDT(int ai[]);

    private static final int PRESS = 0;
    private static final int RELEASE = 1;
    private static final int CHAR = 2;
    private boolean bOnlyChars;
    private boolean bCreated;
    private int param[];
    private MsgTimeOut ticker;

    static 
    {
        RTS.loadNative();
    }
}
