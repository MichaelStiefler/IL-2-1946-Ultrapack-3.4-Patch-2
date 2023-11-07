package com.maddox.rts;

public final class JoyWin
    implements MsgTimeOutListener
{

    public final boolean isCreated()
    {
        return bCreated;
    }

    public final void create()
        throws JoyWinException
    {
        if(RTSConf.cur.mainWindow.hWnd() == 0)
            throw new JoyWinException("Windows joystick driver: window not present");
        amount = nCreateDT();
        if(amount == 0)
            throw new JoyWinException("Windows joystick not found");
        ticker.setTime(Time.currentReal() + timePool);
        ticker.post();
        bCreated = true;
        RTSConf.cur.joy.setAttached(true);
        for(int i = 0; i < amount; i++)
        {
            RTSConf.cur.joy.setCaps(i, 4, 2, 0, 3);
            RTSConf.cur.joy.setJoyName(i, GetJoyName(i));
        }

    }

    public final void destroy()
    {
        if(bCreated)
        {
            RTSConf.cur.queueRealTime.remove(ticker);
            RTSConf.cur.queueRealTimeNextTick.remove(ticker);
            bCreated = false;
            amount = 0;
            RTSConf.cur.joy.setAttached(false);
        }
    }

    private void checkButton(long l, int i, int j, boolean flag)
    {
        if(bButtons[i][j] != flag)
        {
            bButtons[i][j] = flag;
            if(flag)
                RTSConf.cur.joy.setPress(l, i, j);
            else
                RTSConf.cur.joy.setRelease(l, i, j);
        }
    }

    public void msgTimeOut(Object obj)
    {
        if(bCreated)
        {
            long l = Time.currentReal();
            for(int i = 0; i < amount; i++)
                if(nGetMsgDT(i, param))
                {
                    checkButton(l, i, 0, (param[0] & 1) != 0);
                    checkButton(l, i, 1, (param[0] & 2) != 0);
                    checkButton(l, i, 2, (param[0] & 4) != 0);
                    checkButton(l, i, 3, (param[0] & 8) != 0);
                    if((param[0] & 0x10) != 0)
                        RTSConf.cur.joy.setMove(l, i, 0, param[1]);
                    if((param[0] & 0x20) != 0)
                        RTSConf.cur.joy.setMove(l, i, 1, param[2]);
                }

            RTSConf.cur.joy.poll(l);
            ticker.setTime(Time.currentReal() + timePool);
            ticker.post();
        }
    }

    protected JoyWin(long l, boolean flag)
    {
        timePool = 100L;
        param = new int[3];
        bCreated = false;
        amount = 0;
        ticker = new MsgTimeOut(null);
        timePool = l;
        ticker.setTime(Time.currentReal() + l);
        ticker.setNotCleanAfterSend();
        ticker.setFlags(64);
        ticker.setListener(this);
        if(flag)
            create();
    }

    private String GetJoyName(int i)
    {
        switch(i)
        {
        case 0: // '\0'
            return "Windows joystick 1";

        case 1: // '\001'
            return "Windows joystick 2";
        }
        return "";
    }

    private static final native int nCreateDT();

    private static final native boolean nGetMsgDT(int i, int ai[]);

    private static final int BUTTON0 = 1;
    private static final int BUTTON1 = 2;
    private static final int BUTTON2 = 4;
    private static final int BUTTON3 = 8;
    private static final int MOVE_X = 16;
    private static final int MOVE_Y = 32;
    private static boolean bButtons[][] = new boolean[2][4];
    private boolean bCreated;
    private int amount;
    private int param[];
    private MsgTimeOut ticker;
    private long timePool;

    static 
    {
        RTS.loadNative();
    }
}
