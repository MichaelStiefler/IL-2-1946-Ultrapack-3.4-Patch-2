package com.maddox.rts;

public class Mouse
    implements MsgAddListenerListener, MsgRemoveListenerListener
{

    public static Mouse adapter()
    {
        return RTSConf.cur.mouse;
    }

    public void setMouseCursorAdapter(MouseCursor mousecursor)
    {
        mouseCursor = mousecursor;
    }

    public boolean isExistMouseCursorAdapter()
    {
        return mouseCursor != null;
    }

    public void setMouseCursor(int i)
    {
        if(mouseCursor != null)
            mouseCursor.setCursor(i);
    }

    public boolean isPressed(int i)
    {
        if(i < 0 || i >= 11)
            return false;
        else
            return buttons[i];
    }

    public float[] getSensitivity()
    {
        return sensitivity;
    }

    public Object[] getListeners()
    {
        return listeners.get();
    }

    public boolean isInvert()
    {
        return invert;
    }

    public void setInvert(boolean flag)
    {
        invert = flag;
    }

    public Object[] getRealListeners()
    {
        return realListeners.get();
    }

    public void getPos(int ai[])
    {
        ai[0] = X;
        ai[1] = Y;
        ai[2] = Z;
    }

    public void msgAddListener(Object obj, Object obj1)
    {
        if(obj1 != null)
        {
            if(Message.current().isRealTime())
                realListeners.insListener(obj);
            else
                listeners.insListener(obj);
        } else
        if(Message.current().isRealTime())
            realListeners.addListener(obj);
        else
            listeners.addListener(obj);
    }

    public void msgRemoveListener(Object obj, Object obj1)
    {
        if(Message.current().isRealTime())
            realListeners.removeListener(obj);
        else
            listeners.removeListener(obj);
    }

    private void msgSet(MsgMouse msgmouse, boolean flag, boolean flag1, long l, boolean flag2, int i, 
            boolean flag3, boolean flag4, int j, int k, int i1)
    {
        msgmouse.setTickPos(0x7ffffffe);
        if(flag1)
        {
            msgmouse.setFlags(64);
            if(!flag)
                l = Time.toReal(l);
            msgmouse.setTime(l);
        } else
        {
            msgmouse.setFlags(0);
            if(flag)
                l = Time.current();
            msgmouse.setTime(l);
        }
        if(flag2)
        {
            if(flag3)
                msgmouse.setPress(i);
            else
                msgmouse.setRelease(i);
        } else
        if(flag4)
            msgmouse.setAbsMove(j, k, i1);
        else
            msgmouse.setMove(j, k, i1);
    }

    private void post(boolean flag, long l, boolean flag1, int i, boolean flag2, boolean flag3, 
            int j, int k, int i1)
    {
        if(focus != null)
        {
            MsgMouse msgmouse = (MsgMouse)cache.get();
            msgSet(msgmouse, flag, true, l, flag1, i, flag2, flag3, j, k, i1);
            msgmouse.post(focus);
            return;
        }
        Object aobj[] = realListeners.get();
        if(aobj != null)
        {
            MsgMouse msgmouse1 = (MsgMouse)cache.get();
            msgSet(msgmouse1, flag, true, l, flag1, i, flag2, flag3, j, k, i1);
            msgmouse1.post(((Object) (aobj)));
        }
        if(!Time.isPaused())
        {
            Object aobj1[] = listeners.get();
            if(aobj1 != null)
            {
                MsgMouse msgmouse2 = (MsgMouse)cache.get();
                msgSet(msgmouse2, flag, false, l, flag1, i, flag2, flag3, j, k, i1);
                msgmouse2.post(((Object) (aobj1)));
            }
        }
    }

    public Object focus()
    {
        return focus;
    }

    public void setFocus(Object obj)
    {
        focus = obj;
    }

    public void setEnable(boolean flag)
    {
        bEnabled = flag;
    }

    public boolean isEnable()
    {
        return bEnabled;
    }

    public void setPress(long l, int i)
    {
        if(!bEnabled)
        {
            return;
        } else
        {
            _setPress(true, l, i);
            return;
        }
    }

    protected void _setPress(boolean flag, long l, int i)
    {
        flushMove();
        if(i < 0 || i >= 11)
            return;
        if(buttons[i])
        {
            return;
        } else
        {
            buttons[i] = true;
            post(flag, l, true, i, true, false, 0, 0, 0);
            return;
        }
    }

    public void setRelease(long l, int i)
    {
        if(!bEnabled)
        {
            return;
        } else
        {
            _setRelease(true, l, i);
            return;
        }
    }

    protected void _setRelease(boolean flag, long l, int i)
    {
        flushMove();
        if(i < 0 || i >= 11)
            return;
        if(!buttons[i])
        {
            return;
        } else
        {
            buttons[i] = false;
            post(flag, l, true, i, false, false, 0, 0, 0);
            return;
        }
    }

    public void clear()
    {
        if(!bEnabled)
        {
            return;
        } else
        {
            _clear();
            return;
        }
    }

    protected void _clear()
    {
        for(int i = 0; i < 11; i++)
            if(buttons[i])
                _setRelease(true, Time.currentReal(), i);

        bResetComputeRelPos = true;
        clearMove();
    }

    public void setMove(long l, int i, int j, int k)
    {
        if(!bEnabled)
        {
            return;
        } else
        {
            _setMove(true, l, i, j, k);
            return;
        }
    }

    protected void _setMove(boolean flag, long l, int i, int j, int k)
    {
        if(l != lastTimeMove || flag != bLastRealTimeMove)
            _flushMove();
        k = (k * 5) / 120;
        lastTimeMove = l;
        bLastRealTimeMove = flag;
        dx += i;
        dy += j;
        dz += k;
    }

    public void setAbsMove(long l, int i, int j, int k)
    {
        if(!bEnabled)
        {
            return;
        } else
        {
            _setAbsMove(true, l, i, j, k);
            return;
        }
    }

    protected void _setAbsMove(boolean flag, long l, int i, int j, int k)
    {
        X = i;
        Y = j;
        Z += (k * 5) / 120;
        if(Z < -1023)
            Z = -1023;
        if(Z > 1023)
            Z = 1023;
        post(flag, l, false, 0, false, true, X, Y, Z);
        if(bComputeRelPos)
        {
            if(!bResetComputeRelPos)
            {
                i = X - prevX;
                j = Y - prevY;
                _setMove(flag, l, i, j, k);
            } else
            {
                bResetComputeRelPos = false;
            }
            prevX = X;
            prevY = Y;
        }
    }

    public void flushMove()
    {
        if(!bEnabled)
        {
            return;
        } else
        {
            _flushMove();
            return;
        }
    }

    protected void _flushMove()
    {
        if(lastTimeMove != -1L)
        {
            if(dx != 0 || dy != 0 || dz != 0)
            {
                int i = dx < 0 ? (int)((float)dx * sensitivity[0] - 0.5F) : (int)((float)dx * sensitivity[0] + 0.5F);
                int j = dy < 0 ? (int)((float)dy * sensitivity[1] - 0.5F) : (int)((float)dy * sensitivity[1] + 0.5F);
                int k = dz < 0 ? (int)((float)dz * sensitivity[2] - 0.5F) : (int)((float)dz * sensitivity[2] + 0.5F);
                post(bLastRealTimeMove, lastTimeMove, false, 0, false, false, i, j, k);
                if(bComputeAbsPos)
                {
                    int l = RTSConf.cur.mainWindow.width();
                    int i1 = RTSConf.cur.mainWindow.height();
                    X += i;
                    if(X < 0)
                        X = 0;
                    if(X >= l)
                        X = l - 1;
                    Y += j;
                    if(Y < 0)
                        Y = 0;
                    if(Y >= i1)
                        Y = i1 - 1;
                    Z += k;
                    if(Z < -1023)
                        Z = -1023;
                    if(Z > 1023)
                        Z = 1023;
                    post(bLastRealTimeMove, lastTimeMove, false, 0, false, true, X, Y, Z);
                }
            }
            clearMove();
        }
    }

    private void clearMove()
    {
        lastTimeMove = -1L;
        bLastRealTimeMove = true;
        dx = 0;
        dy = 0;
        dz = 0;
    }

    protected void setComputePos(boolean flag, boolean flag1)
    {
        bComputeAbsPos = flag;
        bComputeRelPos = flag1;
        prevX = X = RTSConf.cur.mainWindow.width() / 2;
        prevY = Y = RTSConf.cur.mainWindow.height() / 2;
        Z = 0;
        dx = 0;
        dy = 0;
        dz = 0;
    }

    protected boolean isComputeRelPos()
    {
        return bComputeRelPos;
    }

    protected boolean isComputeAbsPos()
    {
        return bComputeAbsPos;
    }

    public void saveConfig(IniFile inifile, String s)
    {
        if(inifile == null || s == null)
        {
            return;
        } else
        {
            inifile.set(s, "SensitivityX", sensitivity[0]);
            inifile.set(s, "SensitivityY", sensitivity[1]);
            inifile.set(s, "SensitivityZ", sensitivity[2]);
            inifile.set(s, "Invert", invert);
            return;
        }
    }

    public void loadConfig(IniFile inifile, String s)
    {
        if(inifile == null || s == null)
        {
            return;
        } else
        {
            sensitivity[0] = inifile.get(s, "SensitivityX", 1.0F);
            sensitivity[1] = inifile.get(s, "SensitivityY", 1.0F);
            sensitivity[2] = inifile.get(s, "SensitivityZ", 1.0F);
            invert = inifile.get(s, "Invert", invert);
            return;
        }
    }

    protected Mouse(IniFile inifile, String s)
    {
        bComputeAbsPos = false;
        bComputeRelPos = false;
        bResetComputeRelPos = true;
        invert = false;
        listeners = new Listeners();
        realListeners = new Listeners();
        cache = new MessageCache(com.maddox.rts.MsgMouse.class);
        clearMove();
        buttons = new boolean[11];
        for(int i = 0; i < 11; i++)
            buttons[i] = false;

        sensitivity = new float[3];
        for(int j = 0; j < 3; j++)
            sensitivity[j] = 1.0F;

        X = Y = Z = 0;
        bEnabled = true;
        loadConfig(inifile, s);
    }

    public static final int PRESS = 0;
    public static final int RELEASE = 1;
    public static final int MOVE = 2;
    public static final int UNKNOWN = -1;
    public static final int BUTTONS = 11;
    private MouseCursor mouseCursor;
    private boolean bEnabled;
    private Listeners listeners;
    private Listeners realListeners;
    private Object focus;
    private long lastTimeMove;
    private boolean bLastRealTimeMove;
    private int dx;
    private int dy;
    private int dz;
    private int X;
    private int Y;
    private int Z;
    private int prevX;
    private int prevY;
    private boolean bComputeAbsPos;
    private boolean bComputeRelPos;
    private boolean bResetComputeRelPos;
    private float sensitivity[];
    private boolean buttons[];
    private boolean invert;
    private MessageCache cache;
}
