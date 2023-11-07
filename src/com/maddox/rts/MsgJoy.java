package com.maddox.rts;

public class MsgJoy extends Message
{

    public void setButton(int i, int j, boolean flag)
    {
        id = flag ? 0 : 1;
        idDev = 716 + i;
        data0 = 547 + j;
    }

    public void setMove(int i, int j, int k)
    {
        id = 2;
        idDev = 716 + i;
        data0 = 535 + j;
        data1 = k;
    }

    public void setPov(int i, int j)
    {
        id = 3;
        idDev = 683 + i;
        data0 = j;
    }

    public void setPoll()
    {
        id = 4;
    }

    public MsgJoy()
    {
        id = -1;
    }

    public boolean invokeListener(Object obj)
    {
        if(obj instanceof MsgJoyListener)
        {
            switch(id)
            {
            case 0: // '\0'
            case 1: // '\001'
                ((MsgJoyListener)obj).msgJoyButton(idDev, data0, id == 0);
                return true;

            case 2: // '\002'
                ((MsgJoyListener)obj).msgJoyMove(idDev, data0, data1);
                return true;

            case 3: // '\003'
                ((MsgJoyListener)obj).msgJoyPov(idDev, data0);
                return true;

            case 4: // '\004'
                ((MsgJoyListener)obj).msgJoyPoll();
                return true;
            }
            return true;
        } else
        {
            return false;
        }
    }

    public static final int PRESS = 0;
    public static final int RELEASE = 1;
    public static final int MOVE = 2;
    public static final int POV = 3;
    public static final int POLL = 4;
    public static final int UNKNOWN = -1;
    protected int id;
    protected int idDev;
    protected int data0;
    protected int data1;
}
