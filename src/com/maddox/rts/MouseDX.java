package com.maddox.rts;

import com.maddox.il2.engine.Config;

public final class MouseDX
    implements MsgTimeOutListener
{

    public final boolean isCreated()
    {
        return bCreated;
    }

    public final void create(int i)
        throws MouseDXException
    {
        if(bCreated)
        {
            setCooperativeLevel(i);
            return;
        }
        checkCoopLevel(i);
        if(RTSConf.cur.mainWindow.hWnd() == 0)
        {
            throw new MouseDXException("DirectX mouse driver: window not present");
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
        throws MouseDXException
    {
        create(level);
    }

    public final int cooperativeLevel()
    {
        return level;
    }

    public final void setCooperativeLevel(int i)
        throws MouseDXException
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
        }
    }

    public void msgTimeOut(Object obj)
    {
        if(bCreated)
        {
            for(param[0] = param[1] = 0; nGetMsgDT(param); param[0] = param[1] = 0)
            {
                long l = Time.realFromRawClamp(param[2]);
                switch(param[0])
                {
                default:
                    break;

                case 3: // '\003'
                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 7: // '\007'
                case 8: // '\b'
                case 9: // '\t'
                case 10: // '\n'
                    if(param[1] == 1)
                        RTSConf.cur.mouse.setPress(l, param[0] - 3);
                    else
                        RTSConf.cur.mouse.setRelease(l, param[0] - 3);
                    break;

                case 0: // '\0'
                    RTSConf.cur.mouse.setMove(l, param[1], 0, 0);
                    break;

                case 1: // '\001'
                    RTSConf.cur.mouse.setMove(l, 0, -param[1], 0);
                    break;

                case 2: // '\002'
                    RTSConf.cur.mouse.setMove(l, 0, 0, param[1]);
                    break;
                }
            }

            RTSConf.cur.mouse.flushMove();
            ticker.post();
        }
    }

    private static final void checkCoopLevel(int i)
        throws MouseDXException
    {
        if(i < 0 || i > 2)
            throw new MouseDXException("DirectX mouse driver: unknown cooperative level = " + i);
        else
            return;
    }

    protected MouseDX(int i, int j, boolean flag)
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
        throws MouseDXException;

    private static final native void nDestroyDT();

    private static final native void nSetCoopLevelDT(int i);

    private static final native boolean nGetMsgDT(int ai[]);

    public static final int NONEXCLUSIVE_BACKGROUND = 0;
    public static final int NONEXCLUSIVE_FOREGROUND = 1;
    public static final int EXCLUSIVE_FOREGROUND = 2;
    private static final int MOVE_X = 0;
    private static final int MOVE_Y = 1;
    private static final int MOVE_Z = 2;
    private static final int BUTTON0 = 3;
    private static final int BUTTON1 = 4;
    private static final int BUTTON2 = 5;
    private static final int BUTTON3 = 6;
    private static final int BUTTON4 = 7;
    private static final int BUTTON5 = 8;
    private static final int BUTTON6 = 9;
    private static final int BUTTON7 = 10;
    private static final boolean bR_I18N = false;
    private static boolean bR_RU;
    private boolean bCreated;
    private int level;
    private int param[];
    private MsgTimeOut ticker;

    static 
    {
        bR_RU = "RU".equals(Config.LOCALE);
        RTS.loadNative();
    }
}
