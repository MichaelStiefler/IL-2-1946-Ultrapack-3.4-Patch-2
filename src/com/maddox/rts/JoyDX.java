package com.maddox.rts;

import com.maddox.il2.engine.Config;

public final class JoyDX
    implements MsgTimeOutListener
{

    public final boolean isCreated()
    {
        return bCreated;
    }

    public final void create(int i)
        throws JoyDXException
    {
        if(bCreated)
        {
            setCooperativeLevel(i);
            return;
        }
        checkCoopLevel(i);
        if(RTSConf.cur.mainWindow.hWnd() == 0)
            throw new JoyDXException("DirectX joystick driver: window not present");
        amount = nCreateDT(i);
        level = i;
        ticker.setTime(Time.currentReal() + timePool);
        ticker.post();
        bCreated = true;
        RTSConf.cur.joy.setAttached(true);
        RTSConf.cur.joy.setAmount(amount);
        int ai[] = new int[4];
        for(int j = 0; j < amount; j++)
        {
            nCapsDT(j, ai);
            RTSConf.cur.joy.setCaps(j, ai[0], ai[1], ai[2], ai[3]);
            RTSConf.cur.joy.setJoyName(j, nGetJoyNameDT(j));
        }

    }

    public final void create()
        throws JoyDXException
    {
        create(level);
    }

    public final int cooperativeLevel()
    {
        return level;
    }

    public final void setCooperativeLevel(int i)
        throws JoyDXException
    {
        checkCoopLevel(i);
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
            amount = 0;
            RTSConf.cur.joy.setAttached(false);
        }
    }

    public void msgTimeOut(Object obj)
    {
        if(bCreated)
        {
            long l = Time.currentReal();
            for(int i = 0; i < amount; i++)
                for(param[0] = param[1] = 0; nGetMsgDT(i, param); param[0] = param[1] = 0)
                    if(param[0] >= 12 && param[0] <= 139)
                    {
                        if(param[1] == 1)
                            RTSConf.cur.joy.setPress(l, i, param[0] - 12);
                        else
                            RTSConf.cur.joy.setRelease(l, i, param[0] - 12);
                    } else
                    {
                        switch(param[0])
                        {
                        case 0: // '\0'
                            RTSConf.cur.joy.setMove(l, i, 0, param[1]);
                            break;

                        case 1: // '\001'
                            RTSConf.cur.joy.setMove(l, i, 1, param[1]);
                            break;

                        case 2: // '\002'
                            RTSConf.cur.joy.setMove(l, i, 2, param[1]);
                            break;

                        case 3: // '\003'
                            RTSConf.cur.joy.setMove(l, i, 3, param[1]);
                            break;

                        case 4: // '\004'
                            RTSConf.cur.joy.setMove(l, i, 4, param[1]);
                            break;

                        case 5: // '\005'
                            RTSConf.cur.joy.setMove(l, i, 5, param[1]);
                            break;

                        case 6: // '\006'
                            RTSConf.cur.joy.setMove(l, i, 6, param[1]);
                            break;

                        case 7: // '\007'
                            RTSConf.cur.joy.setMove(l, i, 7, param[1]);
                            break;

                        case 8: // '\b'
                            RTSConf.cur.joy.setPov(l, i, 0, param[1]);
                            break;

                        case 9: // '\t'
                            RTSConf.cur.joy.setPov(l, i, 1, param[1]);
                            break;

                        case 10: // '\n'
                            RTSConf.cur.joy.setPov(l, i, 2, param[1]);
                            break;

                        case 11: // '\013'
                            RTSConf.cur.joy.setPov(l, i, 3, param[1]);
                            break;
                        }
                    }


            RTSConf.cur.joy.poll(l);
            ticker.setTime(Time.currentReal() + timePool);
            ticker.post();
        }
    }

    private static final void checkCoopLevel(int i)
        throws JoyDXException
    {
        if(i < 0 || i > 3)
            throw new JoyDXException("DirectX joystick driver: unknown cooperative level = " + i);
        else
            return;
    }

    protected JoyDX(long l, int i, boolean flag)
    {
        timePool = 100L;
        param = new int[3];
        bCreated = false;
        amount = 0;
        checkCoopLevel(i);
        level = i;
        ticker = new MsgTimeOut(null);
        timePool = l;
        ticker.setTime(Time.currentReal() + l);
        ticker.setNotCleanAfterSend();
        ticker.setFlags(64);
        ticker.setListener(this);
        if(flag)
            create();
    }

    public static final void doControlPanel()
    {
        doControlPanelDT();
    }

    private static final native void doControlPanelDT();

    private static final native int nCreateDT(int i)
        throws JoyDXException;

    private static final native void nDestroyDT();

    private static final native void nSetCoopLevelDT(int i);

    private static final native boolean nGetMsgDT(int i, int ai[]);

    private static final native boolean nCapsDT(int i, int ai[]);

    private static final native String nGetJoyNameDT(int i);

    public static final int NONEXCLUSIVE_BACKGROUND = 0;
    public static final int NONEXCLUSIVE_FOREGROUND = 1;
    public static final int EXCLUSIVE_BACKGROUND = 2;
    public static final int EXCLUSIVE_FOREGROUND = 3;
    private static final int MOVE_X = 0;
    private static final int MOVE_Y = 1;
    private static final int MOVE_Z = 2;
    private static final int MOVE_RX = 3;
    private static final int MOVE_RY = 4;
    private static final int MOVE_RZ = 5;
    private static final int MOVE_U = 6;
    private static final int MOVE_V = 7;
    private static final int POV0 = 8;
    private static final int POV1 = 9;
    private static final int POV2 = 10;
    private static final int POV3 = 11;
    private static final int BUTTON0 = 12;
    private static final int BUTTON31 = 43;
    private static final int BUTTON32 = 44;
    private static final int BUTTON127 = 139;
    private static final boolean bR_I18N = false;
    private static boolean bR_RU;
    private boolean bCreated;
    private int amount;
    private int level;
    private int param[];
    private MsgTimeOut ticker;
    private long timePool;

    static 
    {
        bR_RU = "RU".equals(Config.LOCALE);
        RTS.loadNative();
    }
}
