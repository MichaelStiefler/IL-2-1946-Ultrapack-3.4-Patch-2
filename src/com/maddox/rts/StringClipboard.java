package com.maddox.rts;

public final class StringClipboard
{

    public StringClipboard()
    {
    }

    public static void copy(String s)
    {
        if(s == null || s.length() == 0)
            return;
        try
        {
            CopyDT(s);
        }
        catch(Exception exception) { }
    }

    public static String paste()
    {
        String s;
        try
        {
            s = PasteDT();
        }
        catch(Exception exception)
        {
            s = "";
        }
        if(s == null)
            s = "";
        return s;
    }

    private static native void CopyDT(String s);

    private static native String PasteDT();

    static 
    {
        RTS.loadNative();
    }
}
