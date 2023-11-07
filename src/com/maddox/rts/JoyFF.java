package com.maddox.rts;

import java.util.ArrayList;

public class JoyFF
{
    public static class Effect
        implements MsgTimeOutListener
    {

        public void msgTimeOut(Object obj)
        {
            if(cppObj == 0)
                return;
            if(obj != lock)
                return;
            iterations--;
            if(iterations == 0)
            {
                return;
            } else
            {
                JoyFF.StartEffectDT(cppObj);
                MsgTimeOut.post(64, Time.currentReal() + (long)duration, this, lock);
                return;
            }
        }

        public void start(int i)
        {
            if(cppObj == 0)
                return;
            if(i < 0)
                i = -1;
            if(i == 0)
                return;
            iterations = i;
            lock = null;
            JoyFF.StartEffectDT(cppObj);
            if(iterations != 1 && duration > 0)
            {
                lock = new Object();
                MsgTimeOut.post(64, Time.currentReal() + (long)duration, this, lock);
            }
        }

        public void stop()
        {
            iterations = 0;
            lock = null;
            if(cppObj == 0)
            {
                return;
            } else
            {
                JoyFF.StopEffectDT(cppObj);
                return;
            }
        }

        public boolean isPlaying()
        {
            if(cppObj == 0)
                return false;
            if(iterations == 0)
                return false;
            if(iterations == 1 && lock == null)
                return JoyFF.EffectIsPlayingDT(cppObj);
            else
                return true;
        }

        public void setDuration(int i)
        {
            if(cppObj == 0)
            {
                return;
            } else
            {
                duration = i;
                JoyFF.SetEffectDurationDT(cppObj, i);
                return;
            }
        }

        public void setSamplePeriod(int i)
        {
            if(cppObj == 0)
            {
                return;
            } else
            {
                JoyFF.SetEffectSamplePeriodDT(cppObj, i);
                return;
            }
        }

        public void setGain(float f)
        {
            if(cppObj == 0)
                return;
            if(f < 0.0F)
                f = 0.0F;
            if(f > 1.0F)
                f = 1.0F;
            JoyFF.SetEffectGainDT(cppObj, (int)(f * 10000F));
        }

        public void setDirection(float f, float f1)
        {
            if(cppObj == 0)
                return;
            if(f < -1F)
                f = -1F;
            if(f > 1.0F)
                f = 1.0F;
            if(f1 < -1F)
                f1 = -1F;
            if(f1 > 1.0F)
                f1 = 1.0F;
            JoyFF.SetEffectDirectionDT(cppObj, (int)(f * 10000F), (int)(f1 * 10000F));
        }

        public void setOffset(float f, float f1)
        {
            if(cppObj == 0)
                return;
            if(f < -1F)
                f = -1F;
            if(f > 1.0F)
                f = 1.0F;
            if(f1 < -1F)
                f1 = -1F;
            if(f1 > 1.0F)
                f1 = 1.0F;
            JoyFF.SetEffectOffsetDT(cppObj, (int)(f * 10000F), (int)(f1 * 10000F));
        }

        public void setCoefficient(float f, float f1)
        {
            if(cppObj == 0)
                return;
            if(f < -1F)
                f = -1F;
            if(f > 1.0F)
                f = 1.0F;
            if(f1 < -1F)
                f1 = -1F;
            if(f1 > 1.0F)
                f1 = 1.0F;
            JoyFF.SetEffectCoefficientDT(cppObj, (int)(f * 10000F), (int)(f1 * 10000F));
        }

        public void destroy()
        {
            if(cppObj == 0)
            {
                return;
            } else
            {
                JoyFF.DelEffectDT(cppObj);
                cppObj = 0;
                JoyFF.lst.remove(this);
                return;
            }
        }

        public int cppObj;
        String fileName;
        int duration;
        int iterations;
        Object lock;

        public Effect(String s)
        {
            fileName = s;
            if(!JoyFF.bAttached)
                return;
            if(!JoyFF.bStarted)
                return;
            cppObj = JoyFF.NewEffectDT(HomePath.toFileSystemName(fileName, 0));
            if(cppObj == 0)
            {
                return;
            } else
            {
                duration = JoyFF.GetEffectDurationDT(cppObj);
                JoyFF.lst.add(this);
                return;
            }
        }
    }


    public static void reattach()
    {
        bAttached = IsAttachedDT();
    }

    public static boolean isAttached()
    {
        return bAttached;
    }

    public static boolean isEnable()
    {
        return bEnable;
    }

    public static void setEnable(boolean flag)
    {
        bEnable = flag;
    }

    public static boolean isStarted()
    {
        return bStarted;
    }

    public static void start()
    {
        if(!bAttached)
            return;
        if(bStarted)
            return;
        bStarted = StartDT(3);
        if(bStarted)
        {
            setAutoCenter(bAutoCenter);
            int i = lst.size();
            for(int j = 0; j < i; j++)
            {
                Effect effect = (Effect)lst.get(j);
                effect.cppObj = NewEffectDT(HomePath.toFileSystemName(effect.fileName, 0));
                if(effect.cppObj == 0)
                    return;
                effect.duration = GetEffectDurationDT(effect.cppObj);
            }

        }
    }

    public static void stop()
    {
        if(!bAttached)
            return;
        if(!bStarted)
            return;
        int i = lst.size();
        for(int j = 0; j < i; j++)
        {
            Effect effect = (Effect)lst.get(j);
            if(effect.cppObj != 0)
            {
                DelEffectDT(effect.cppObj);
                effect.cppObj = 0;
                effect.iterations = 0;
                effect.lock = null;
            }
        }

        bStarted = false;
        StopDT();
    }

    public static void setAutoCenter(boolean flag)
    {
        if(!bAttached)
            return;
        bAutoCenter = flag;
        if(!bStarted)
        {
            return;
        } else
        {
            SetAutoCenterDT(flag);
            return;
        }
    }

    private JoyFF()
    {
        bAttached = IsAttachedDT();
    }

    private static final native boolean IsAttachedDT();

    private static final native boolean StartDT(int i);

    private static final native void StopDT();

    private static final native void SetAutoCenterDT(boolean flag);

    private static final native int NewEffectDT(String s);

    private static final native void DelEffectDT(int i);

    private static final native int GetEffectDurationDT(int i);

    private static final native void StartEffectDT(int i);

    private static final native void StopEffectDT(int i);

    private static final native boolean EffectIsPlayingDT(int i);

    private static final native void SetEffectDurationDT(int i, int j);

    private static final native void SetEffectSamplePeriodDT(int i, int j);

    private static final native void SetEffectGainDT(int i, int j);

    private static final native void SetEffectDirectionDT(int i, int j, int k);

    private static final native void SetEffectOffsetDT(int i, int j, int k);

    private static final native void SetEffectCoefficientDT(int i, int j, int k);

    public static final int NONEXCLUSIVE_BACKGROUND = 0;
    public static final int NONEXCLUSIVE_FOREGROUND = 1;
    public static final int EXCLUSIVE_BACKGROUND = 2;
    public static final int EXCLUSIVE_FOREGROUND = 3;
    private static JoyFF cur = new JoyFF();
    private static ArrayList lst = new ArrayList();
    private static boolean bAttached = false;
    private static boolean bEnable = false;
    private static boolean bStarted = false;
    private static boolean bAutoCenter = true;

    static 
    {
        RTS.loadNative();
    }















}
