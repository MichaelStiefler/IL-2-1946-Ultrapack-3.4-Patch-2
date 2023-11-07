package com.maddox.rts;

public final class MouseWin
    implements MsgTimeOutListener, MouseCursor
{

    public final boolean isCreated()
    {
        return bCreated;
    }

    public final void create()
        throws MouseWinException
    {
        if(bCreated)
            return;
        if(RTSConf.cur.mainWindow.hWnd() == 0)
        {
            throw new MouseWinException("Mouse windows driver: main window not present");
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

    public void setCursor(int i)
    {
        SetCursorDT(i);
    }

    public void msgTimeOut(Object obj)
    {
        if(bCreated)
        {
            while(nGetMsgDT(param)) 
            {
                long l = Time.realFromRawClamp(param[1]);
                switch(param[0])
                {
                case 0: // '\0'
                    RTSConf.cur.mouse.setPress(l, param[2]);
                    break;

                case 1: // '\001'
                    RTSConf.cur.mouse.setRelease(l, param[2]);
                    break;

                case 2: // '\002'
                    int i = param[2];
                    int j = RTSConf.cur.mainWindow.height() - param[3] - 1;
                    int k = param[4];
                    RTSConf.cur.mouse.setAbsMove(l, i, j, k);
                    break;
                }
            }
            ticker.post();
        }
    }

    protected MouseWin(int i, boolean flag)
    {
        bOnlyAbsMove = false;
        param = new int[5];
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

    private static final native void SetCursorDT(int i);

    private static final int PRESS = 0;
    private static final int RELEASE = 1;
    private static final int ABSMOVE = 2;
    private boolean bOnlyAbsMove;
    private boolean bCreated;
    private int param[];
    private MsgTimeOut ticker;

    static 
    {
        RTS.loadNative();
    }
}
