package com.maddox.rts;

import java.util.Locale;

import com.maddox.util.HashMapExt;

public class RTSConf
{

    public static Object lockObject()
    {
        return lockObject;
    }

    public void loopMsgs()
    {
        time.loopMessages();
        profile.endFrame();
    }

    public static boolean isRequestExitApp()
    {
        return cur.bRequestExitApp;
    }

    public static void setRequestExitApp(boolean flag)
    {
        cur.bRequestExitApp = flag;
    }

    public static boolean isResetGame()
    {
        return cur.bResetGame;
    }

    public void setFlagResetGame(boolean flag)
    {
        bResetGame = flag;
    }

    public void resetGameClear()
    {
        keyboard._clear();
        mouse._clear();
        joy._clear();
        trackIR.clear();
        cur.hotKeyEnvs.resetGameClear();
        cur.timer.resetGameClear();
        cur.queue.clear();
        cur.queueNextTick.clear();
        cur.time.resetGameClear();
    }

    public void resetGameCreate()
    {
        cur.time.resetGameCreate();
        cur.timer.resetGameCreate();
        cur.hotKeyEnvs.resetGameCreate();
    }

    public boolean isStarted()
    {
        return bStarted;
    }

    public void start()
    {
        bStarted = true;
    }

    public void stop()
    {
        bStarted = false;
    }

    public int getUseMouse()
    {
        return useMouse;
    }

    public void setUseMouse(int i)
    {
        useMouse = i;
    }

    public boolean isUseJoy()
    {
        return bUseJoy;
    }

    public void useJoy(boolean flag)
    {
        bUseJoy = flag;
    }

    public boolean isUseTrackIR()
    {
        return bUseTrackIR;
    }

    public void useTrackIR(boolean flag)
    {
        bUseTrackIR = flag;
    }

    private void setNetProperty()
    {
        Property.set("netProtocol", "udp", 1);
        Property.set("udp", "className", "UdpSocket");
    }

    public static boolean OSisWIN32()
    {
        return true;
    }

    public RTSConf()
    {
        this(new MainWindow());
    }

    public RTSConf(MainWindow mainwindow)
    {
        queue = new MessageQueue();
        queueNextTick = new MessageQueue();
        queueRealTime = new MessageQueue();
        queueRealTimeNextTick = new MessageQueue();
        cfgMap = new HashMapExt();
        backgroundLoop = new BackgroundLoop();
        profile = new RTSProfile();
        bRequestExitApp = false;
        bResetGame = false;
        bStarted = false;
        useMouse = 1;
        bUseJoy = true;
        bUseTrackIR = true;
        cur = this;
        locale = Locale.getDefault();
        time = new Time(30, 20);
        timer = new Timer(false, -1000);
        realTimer = new Timer(true, -1000);
        mainWindow = new MainWindow();
        keyboard = new Keyboard();
        mouse = new Mouse(null, null);
        joy = new Joy(null, null);
        trackIR = new TrackIR(null, null);
        netEnv = new NetEnv();
        cmdEnv = new CmdEnv();
        hotKeyCmdEnvs = new HotKeyCmdEnvs();
        hotKeyEnvs = new HotKeyEnvs();
        HotKeyCmdEnv.setCurrentEnv("default");
        console = new Console(0);
        setNetProperty();
    }

    public RTSConf(IniFile inifile, String s, int i)
    {
        this(new MainWindow(), inifile, s, i);
    }

    public RTSConf(MainWindow mainwindow, IniFile inifile, String s, int i)
    {
        queue = new MessageQueue();
        queueNextTick = new MessageQueue();
        queueRealTime = new MessageQueue();
        queueRealTimeNextTick = new MessageQueue();
        cfgMap = new HashMapExt();
        backgroundLoop = new BackgroundLoop();
        profile = new RTSProfile();
        bRequestExitApp = false;
        bResetGame = false;
        bStarted = false;
        useMouse = 1;
        bUseJoy = true;
        bUseTrackIR = true;
        cur = this;
        int j = inifile.get(s, "ProcessAffinityMask", 0);
        if(j != 0)
            SetProcessAffinityMask(j);
        locale = Locale.getDefault();
        time = new Time(30, 20);
        timer = new Timer(false, -1000);
        realTimer = new Timer(true, -1000);
        mainWindow = mainwindow;
        keyboard = new Keyboard();
        mouse = new Mouse(inifile, s + "_mouse");
        int k = inifile.get("rts", "JoyProfile", 0, 0, 7);
        if(k > 0)
            joy = new Joy(inifile, s + "_joystick" + k);
        else
            joy = new Joy(inifile, s + "_joystick");
        trackIR = new TrackIR(inifile, s + "_trackIR");
        netEnv = new NetEnv();
        cmdEnv = new CmdEnv();
        hotKeyCmdEnvs = new HotKeyCmdEnvs();
        hotKeyEnvs = new HotKeyEnvs();
        HotKeyCmdEnv.setCurrentEnv("default");
        HotKeyEnv.setCurrentEnv("default");
        console = new Console(i);
        setNetProperty();
    }

    private static native void SetProcessAffinityMask(int i);

    public static final boolean RELEASE = true;
    public static final int MESSAGE_INPUT_TICK_POS = 0x7ffffffe;
    public static final int MESSAGE_KEYRECORD_TICK_POS = 0x80000001;
    public static RTSConf cur = null;
    public Locale locale;
    public static String charEncoding = "Cp1252";
    public Time time;
    public Timer timer;
    public Timer realTimer;
    public MainWindow mainWindow;
    public Keyboard keyboard;
    public Mouse mouse;
    public Joy joy;
    public TrackIR trackIR;
    public CmdEnv cmdEnv;
    public HotKeyCmdEnvs hotKeyCmdEnvs;
    public HotKeyEnvs hotKeyEnvs;
    public Console console;
    public NetEnv netEnv;
    public MessageQueue queue;
    public MessageQueue queueNextTick;
    public MessageQueue queueRealTime;
    public MessageQueue queueRealTimeNextTick;
    public Message message;
    public HashMapExt cfgMap;
    public BackgroundLoop backgroundLoop;
    public static Object lockObject = new Object();
    protected RTSProfile profile;
    public String execPostProcessCmd;
    private boolean bRequestExitApp;
    private boolean bResetGame;
    protected boolean bStarted;
    protected int useMouse;
    protected boolean bUseJoy;
    protected boolean bUseTrackIR;

}
