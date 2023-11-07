package com.maddox.rts;

public class MainWindow
    implements MsgAddListenerListener, MsgRemoveListenerListener
{

    public boolean isCreated()
    {
        return bCreated;
    }

    public boolean isFullScreen()
    {
        return bFullScreen;
    }

    public boolean isFocused()
    {
        return bFocused;
    }

    public int width()
    {
        return cx;
    }

    public int height()
    {
        return cy;
    }

    public int widthFull()
    {
        return cxFull;
    }

    public int heightFull()
    {
        return cyFull;
    }

    public int posX()
    {
        return posX;
    }

    public int posY()
    {
        return posY;
    }

    public static native int componentWndDT(Object obj);

    public int hWnd()
    {
        return hWnd;
    }

    public int hContextWnd()
    {
        return hWnd;
    }

    public void setFocus()
    {
    }

    public void setSize(int i, int j)
    {
    }

    public void setPosSize(int i, int j, int k, int l)
    {
    }

    public void setTitle(String s)
    {
    }

    public boolean isIconic()
    {
        return false;
    }

    public void showIconic()
    {
    }

    public void showNormal()
    {
    }

    public static MainWindow adapter()
    {
        return RTSConf.cur.mainWindow;
    }

    public Object[] getListeners()
    {
        return listeners.get();
    }

    public void msgAddListener(Object obj, Object obj1)
    {
        listeners.addListener(obj);
    }

    public void msgRemoveListener(Object obj, Object obj1)
    {
        listeners.removeListener(obj);
    }

    public boolean isMessagesEnable()
    {
        return bEnableMessages;
    }

    public void setMessagesEnable(boolean flag)
    {
        bEnableMessages = flag;
    }

    public void SendAction(int i)
    {
        if(!bEnableMessages)
            return;
        if(i == 2)
            RTSConf.setRequestExitApp(true);
        Object aobj[] = listeners.get();
        if(aobj == null)
            return;
        if(msg == null)
            msg = new MsgMainWindow();
        for(int j = 0; j < aobj.length; j++)
            msg.Send(i, aobj[j]);

    }

    protected MainWindow()
    {
        hWnd = 0;
        bEnableMessages = true;
        listeners = new Listeners();
        bCreated = false;
        bFullScreen = false;
    }

    protected int hWnd;
    protected int cx;
    protected int cy;
    protected int cxFull;
    protected int cyFull;
    protected int posX;
    protected int posY;
    protected boolean bFocused;
    protected boolean bFullScreen;
    protected boolean bCreated;
    private boolean bEnableMessages;
    private Listeners listeners;
    private MsgMainWindow msg;
}
