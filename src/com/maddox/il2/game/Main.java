package com.maddox.il2.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.EffClouds;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.GObj;
import com.maddox.il2.game.campaign.Campaign;
import com.maddox.il2.net.Chat;
import com.maddox.il2.net.GameSpy;
import com.maddox.il2.net.NetChannelListener;
import com.maddox.il2.net.NetFileServerMissProp;
import com.maddox.il2.net.NetFileServerNoseart;
import com.maddox.il2.net.NetFileServerPilot;
import com.maddox.il2.net.NetFileServerReg;
import com.maddox.il2.net.NetFileServerSkin;
import com.maddox.il2.net.NetMissionListener;
import com.maddox.il2.net.NetServerParams;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.humans.Soldier;
import com.maddox.il2.objects.vehicles.artillery.RocketryGeneric;
import com.maddox.il2.objects.weapons.Bomb;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.FuelTank;
import com.maddox.il2.objects.weapons.FuelTankGun;
import com.maddox.il2.objects.weapons.Rocket;
import com.maddox.il2.objects.weapons.RocketBombGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.rts.ConsoleOut;
import com.maddox.rts.Cpu86ID;
import com.maddox.rts.Finger;
import com.maddox.rts.HomePath;
import com.maddox.rts.IniFile;
import com.maddox.rts.KryptoInputFilter;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetEnv;
import com.maddox.rts.Property;
import com.maddox.rts.RTS;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SFSInputStream;
import com.maddox.rts.SectFile;
import com.maddox.rts.Spawn;
import com.maddox.util.SharedTokenizer;
import com.maddox.util.UnicodeTo8bit;

public abstract class Main {
    public static final String   AIR_INI = "com/maddox/il2/objects/air.ini";
    public ArrayList             airClasses;
    public Mission               mission;
    public Mission               missionLoading;
    public int                   missionCounter;
    public Campaign              campaign;
    public DotRange              dotRangeFriendly;
    public DotRange              dotRangeFoe;
    public NetChannelListener    netChannelListener;
    public NetMissionListener    netMissionListener;
    public NetServerParams       netServerParams;
    public Chat                  chat;
    public NetFileServerMissProp netFileServerMissProp;
    public NetFileServerReg      netFileServerReg;
    public NetFileServerSkin     netFileServerSkin;
    public NetFileServerPilot    netFileServerPilot;
    public NetFileServerNoseart  netFileServerNoseart;
    public GameSpy               netGameSpy;
    public GameSpy               netGameSpyListener;
    public SectFile              currentMissionFile;
    public GameStateStack        stateStack;
    protected Object             oCommandSync;
    protected boolean            bCommand;
    protected boolean            bCommandNet;
    protected String             sCommand;
    protected ConsoleServer      consoleServer;
    private static boolean       bRequestExit;
    private static Main          cur;
    public static int            iExitCode;

    public EffClouds             clouds;

    public Main() {
        this.airClasses = new ArrayList();
        this.dotRangeFriendly = new DotRange();
        this.dotRangeFoe = new DotRange();
        this.stateStack = new GameStateStack();
        this.oCommandSync = new Object();
        this.bCommand = false;
        this.bCommandNet = false;
        this.sCommand = null;
        this.consoleServer = null;
    }

    public boolean beginApp(final String paramString1, final String paramString2, final int paramInt) {
        return false;
    }

    public void onBeginApp() {
    }

    public void loopApp() {
    }

    public void endApp() {
    }

    public static GameStateStack stateStack() {
        return Main.cur.stateStack;
    }

    public static GameState state() {
        return Main.cur.stateStack.peek();
    }

    public void resetGameClear() {
        Engine.cur.resetGameClear();
        RTSConf.cur.resetGameClear();
    }

    public void resetGameCreate() {
        RTSConf.cur.resetGameCreate();
        Engine.cur.resetGameCreate();
        Paratrooper.resetGame();
        Soldier.resetGame();
    }

    public void resetGame() {
        RTSConf.cur.setFlagResetGame(true);
        this.resetGameClear();
        RTSConf.cur.setFlagResetGame(false);
        this.resetGameCreate();
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        GObj.DeleteCppObjects();
    }

    public void resetUserClear() {
    }

    public void resetUserCreate() {
    }

    public void resetUser() {
        this.resetUserClear();
        this.resetUserCreate();
    }

    public static void closeAllNetChannels() {
        final List localList = NetEnv.channels();
        for (int i = 0; i < localList.size(); ++i) {
            final NetChannel localNetChannel = (NetChannel) localList.get(i);
            if (!localNetChannel.isDestroyed()) localNetChannel.destroy("Remote user has left the game.");
        }
    }

    public static void doGameExit() {
        if (Main.bRequestExit) return;
        Main.bRequestExit = true;
        final List localList = NetEnv.channels();
        if (localList.size() == 0) RTSConf.setRequestExitApp(true);
        else {
            closeAllNetChannels();
            new MsgAction(64, 2.0, "") {
                public void doAction(final Object paramObject) {
                    RTSConf.setRequestExitApp(true);
                }
            };
        }
    }

    public void preloadNetClasses() {
        Spawn.get("com.maddox.rts.NetControl");
        Spawn.get("com.maddox.il2.net.NetUser");
        Spawn.get("com.maddox.il2.net.NetServerParams");
        Spawn.get("com.maddox.il2.net.Chat");
        Spawn.get("com.maddox.il2.game.Mission");
        this.netFileServerMissProp = new NetFileServerMissProp(254);
        this.netFileServerReg = new NetFileServerReg(253);
        this.netFileServerSkin = new NetFileServerSkin(252);
        this.netFileServerPilot = new NetFileServerPilot(251);
        this.netFileServerNoseart = new NetFileServerNoseart(249);
        Spawn.get("com.maddox.il2.objects.air.Paratrooper");
        Spawn.get("com.maddox.il2.objects.air.NetGunner");
    }

    public void preloadAirClasses() {
        final SectFile localSectFile = new SectFile("com/maddox/il2/objects/air.ini", 0);
        for (int i = localSectFile.sections(), j = 0; j < i; ++j)
            for (int k = localSectFile.vars(j), m = 0; m < k; ++m) {
                final String str1 = localSectFile.value(j, m);
                final StringTokenizer localStringTokenizer = new StringTokenizer(str1);
                if (localStringTokenizer.hasMoreTokens()) {
                    final String str2 = "com.maddox.il2.objects." + localStringTokenizer.nextToken();
                    Spawn.get(str2);
                    try {
                        final Class localClass = Class.forName(str2);
                        final String str3 = localSectFile.var(j, m).intern();
                        Property.set(localClass, "keyName", str3);
                        Property.set(str3, "airClass", (Object) localClass);
                        Aircraft.weapons(localClass);
                        this.airClasses.add(localClass);
                    } catch (Exception ex) {}
                }
            }
    }

    public void preloadChiefClasses() {
        final SectFile localSectFile = new SectFile("com/maddox/il2/objects/chief.ini", 0);
        for (int i = localSectFile.sections(), j = 0; j < i; ++j)
            if (localSectFile.sectionName(j).indexOf(46) >= 0) for (int k = localSectFile.vars(j), m = 0; m < k; ++m) {
                final String str = localSectFile.var(j, m);
                final StringTokenizer localStringTokenizer = new StringTokenizer(str);
                if (localStringTokenizer.hasMoreTokens()) Spawn.get_WithSoftClass(localStringTokenizer.nextToken());
            }
    }

    public void preloadStationaryClasses() {
        final SectFile localSectFile = new SectFile("com/maddox/il2/objects/stationary.ini", 0);
        for (int i = localSectFile.sections(), j = 0; j < i; ++j)
            for (int k = localSectFile.vars(j), m = 0; m < k; ++m) {
                final String str = localSectFile.value(j, m);
                final StringTokenizer localStringTokenizer = new StringTokenizer(str);
                if (localStringTokenizer.hasMoreTokens()) Spawn.get_WithSoftClass("com.maddox.il2.objects." + localStringTokenizer.nextToken());
            }
        RocketryGeneric.PreLoad("com/maddox/il2/objects/rockets.ini");
    }

    private static int[] getSwTbl2(int paramInt) {
        if (paramInt < 0) paramInt = -paramInt;
        int i = paramInt % 16 + 21;
        int j = paramInt % Finger.kTable.length;
        if (i < 0) i = -i % 16;
        if (i < 10) i = 10;
        if (j < 0) j = -j % Finger.kTable.length;
        final int[] arrayOfInt = new int[i];
        for (int k = 0; k < i; ++k)
            arrayOfInt[k] = Finger.kTable[(j + k) % Finger.kTable.length];
        return arrayOfInt;
    }

    public void preload() {
        Map.Entry localEntry = Property.propertyHoldMap.nextEntry(null);
        final ArrayList localArrayList = new ArrayList();
        while (localEntry != null) {
            final Object localObject = localEntry.getKey();
            if (localObject instanceof Class) {
                final Class localClass = (Class) localObject;
                if (Bomb.class.isAssignableFrom(localClass) || BombGun.class.isAssignableFrom(localClass) || RocketBombGun.class.isAssignableFrom(localClass) || FuelTank.class.isAssignableFrom(localClass) || FuelTankGun.class.isAssignableFrom(localClass)
                        || Rocket.class.isAssignableFrom(localClass) || RocketGun.class.isAssignableFrom(localClass)) {
                    localArrayList.clear();
                    if (Property.vars(localArrayList, localObject)) try {
                        final int i = Finger.Int("wp" + localClass.getName() + "mr");
                        final BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new KryptoInputFilter(new SFSInputStream(Finger.LongFN(0L, "cod/" + Finger.incInt(i, "slg"))), getSwTbl2(i))));
                        while (true) {
                            final String str1 = localBufferedReader.readLine();
                            if (str1 == null) break;
                            SharedTokenizer.set(str1);
                            final String str2 = SharedTokenizer.next();
                            final String str3 = SharedTokenizer.next();
                            switch (str3.charAt(0)) {
                                case 'S': {
                                    final String str4 = SharedTokenizer.next();
                                    Property.set(localObject, str2, str4);
                                    continue;
                                }
                                case 'D': {
                                    final double d = SharedTokenizer.next(0.0);
                                    Property.set(localObject, str2, d);
                                    continue;
                                }
                                case 'L': {
                                    final long l = SharedTokenizer.next(0);
                                    Property.set(localObject, str2, l);
                                    continue;
                                }
                            }
                        }
                        localBufferedReader.close();
                    } catch (Exception ex) {}
                }
            }
            localEntry = Property.propertyHoldMap.nextEntry(localEntry);
        }
    }

    private static void clearDir(final File paramFile, final boolean paramBoolean) {
        final File[] arrayOfFile = paramFile.listFiles();
        if (arrayOfFile != null) for (int i = 0; i < arrayOfFile.length; ++i) {
            final File localFile = arrayOfFile[i];
            final String str = localFile.getName();
            if (!".".equals(str)) if (!"..".equals(str)) if (localFile.isDirectory()) clearDir(localFile, true);
            else localFile.delete();
        }
        if (paramBoolean) paramFile.delete();
    }

    public static void clearCache() {
        if (Config.cur == null) return;
        if (!Config.cur.clear_cache) return;
        try {
            final File localFile = new File(HomePath.toFileSystemName("PaintSchemes/Cache", 0));
            if (localFile.exists()) clearDir(localFile, false);
            else localFile.mkdirs();
        } catch (Exception localException) {
            System.out.println(localException.getMessage());
            localException.printStackTrace();
        }
    }

    public static Main cur() {
        return Main.cur;
    }

    public static void exec(Main main, String confIniFileName, String mainSectionName, int consoleIndex) {
        IniFile confIniFile = new IniFile(confIniFileName, 0);
        String str = confIniFile.get("rts", "locale", "us").toLowerCase();
        Locale.setDefault(Locale.US);
        if (Config.LOCALE.equals("RU")) {
            Locale.setDefault(new Locale("ru", "RU"));
            try {
                Cpu86ID.getMask();
            } catch (Throwable localThrowable1) {}
        } else if (Config.LOCALE.equals("JP")) Locale.setDefault(new Locale("ja", "JP"));
        else {
            str = Locale.getDefault().getLanguage();
            if (!"de".equals(str) && !"fr".equals(str) && !"cs".equals(str) && !"pl".equals(str) && !"hu".equals(str) && !"lt".equals(str) && !"us".equals(str)) Locale.setDefault(Locale.US);
        }
        try {
            if (str.length() != 2) str = System.getProperty("user.language").toLowerCase();
            Locale.setDefault(new Locale(str, ""));
        } catch (Exception ex) {}
        if ("ru".equals(Locale.getDefault().getLanguage())) RTSConf.charEncoding = "Cp1251";
        else if ("cs".equals(Locale.getDefault().getLanguage())) RTSConf.charEncoding = "Cp1250";
        else if ("pl".equals(Locale.getDefault().getLanguage())) RTSConf.charEncoding = "Cp1250";
        else if ("hu".equals(Locale.getDefault().getLanguage())) RTSConf.charEncoding = "Cp1250";
        else if ("lt".equals(Locale.getDefault().getLanguage())) RTSConf.charEncoding = "Cp1257";
        else if ("ja".equals(Locale.getDefault().getLanguage())) RTSConf.charEncoding = "SJIS";
        if (cur != null) throw new RuntimeException("Traying recurse execute main method");
        Runtime.getRuntime().traceInstructions(false);
        Runtime.getRuntime().traceMethodCalls(false);
        Main.cur = main;
        if (!main.isLifeLimitted()) {
            try {
//                System.out.println("confIniFileName=" + confIniFileName + ", mainSectionName=" + mainSectionName + ", consoleIndex=" + consoleIndex);
//                System.out.println("isMainWin3D=" + (Main.cur instanceof MainWin3D) + ", isMain3D=" + (Main.cur instanceof Main3D) + ", isMain=" + (Main.cur instanceof Main));
                if (main.beginApp(confIniFileName, mainSectionName, consoleIndex)) {
                    clearCache();
                    if (Config.LOCALE.equals("RU")) try {
                        if (Config.isUSE_RENDER()) Cpu86ID.getMask();
                    } catch (final Throwable localThrowable2) {
                        for (int i = 0; i < 10; ++i)
                            new MsgAction(64, 1.0 + i * 10 + Math.random() * 10.0) {
                                public void doAction() {
                                    System.out.println("Main loop Exception: " + localThrowable2.getMessage());
                                    localThrowable2.printStackTrace();
                                    Main.doGameExit();
                                }
                            };
                    }
                    try {
                        main.lifeLimitted();
                        main.loopApp();
                    } catch (Exception localException2) {
                        System.out.println("Main loop: " + localException2.getMessage());
                        localException2.printStackTrace();
                    }
                }
            } catch (Exception localException3) {
                System.out.println("Main begin: " + localException3.getMessage());
                localException3.printStackTrace(System.out);
                localException3.printStackTrace();
            }
            clearCache();
            try {
                main.endApp();
            } catch (Exception localException4) {
                System.out.println("Main end: " + localException4.getMessage());
                localException4.printStackTrace();
            }
            if (RTSConf.cur != null && RTSConf.cur.console != null) RTSConf.cur.console.log(false);
            if (RTSConf.cur != null && RTSConf.cur.execPostProcessCmd != null) try {
                RTS.setPostProcessCmd(RTSConf.cur.execPostProcessCmd);
            } catch (Exception localException5) {
                System.out.println("Exec cmd (" + RTSConf.cur.execPostProcessCmd + ") error: " + localException5.getMessage());
                localException5.printStackTrace();
            }
        }
        System.exit(Main.iExitCode);
    }

    private boolean isLifeLimitted() {
        return false;
    }

    private void lifeLimitted() {
    }

    protected void createConsoleServer() {
        final int i = Config.cur.ini.get("Console", "IP", 0, 0, 65000);
        if (i == 0) return;
        final ArrayList localArrayList = new ArrayList();
        final String str1 = Config.cur.ini.get("Console", "IPS", (String) null);
        if (str1 != null) {
            final StringTokenizer localStringTokenizer = new StringTokenizer(str1);
            while (localStringTokenizer.hasMoreTokens()) {
                final String str2 = localStringTokenizer.nextToken();
                localArrayList.add(str2);
            }
        }
        (this.consoleServer = new ConsoleServer(i, localArrayList)).setPriority(Thread.currentThread().getPriority() + 1);
        this.consoleServer.start();
        this.consoleServer.outWriter.setPriority(Thread.currentThread().getPriority() + 1);
        this.consoleServer.outWriter.start();
    }

    private static int[] getSwTbl(int paramInt) {
        if (paramInt < 0) paramInt = -paramInt;
        int i = paramInt % 16 + 14;
        int j = paramInt % Finger.kTable.length;
        if (i < 0) i = -i % 16;
        if (i < 10) i = 10;
        if (j < 0) j = -j % Finger.kTable.length;
        final int[] arrayOfInt = new int[i];
        for (int k = 0; k < i; ++k)
            arrayOfInt[k] = Finger.kTable[(j + k) % Finger.kTable.length];
        return arrayOfInt;
    }

    static {
        Main.bRequestExit = false;
        Main.iExitCode = 0;
        try {
            final BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new KryptoInputFilter(new SFSInputStream(Finger.LongFN(0L, "cod/" + Finger.Int("allc"))), getSwTbl(Finger.Int("alls")))));
            while (true) {
                final String str = localBufferedReader.readLine();
                if (str == null) break;
                Class.forName("com.maddox." + str);
            }
            localBufferedReader.close();
        } catch (Exception ex) {}
    }

    class ConsoleServer extends Thread implements ConsoleOut {
        ArrayList       clientAdr;
        ServerSocket    serverSocket;
        PrintWriter     out;
        ConsoleWriter   outWriter;
        ConsoleOutQueue outQueue;
        int             port;
        boolean         bEnableType;

        public void type(final String paramString) {
            if (!this.bEnableType) return;
            if (this.out != null && paramString != null) this.outQueue.put(paramString);
        }

        public void flush() {
        }

        public void typeNum() {
            if (this.out != null) this.outQueue.put("<consoleN><" + (RTSConf.cur.console.getEnv().curNumCmd() + 1) + ">");
        }

        public void run() {
            InetAddress localInetAddress = null;
            try {
                if (Config.cur.netLocalHost != null && Config.cur.netLocalHost.length() > 0) localInetAddress = InetAddress.getByName(Config.cur.netLocalHost);
                else localInetAddress = InetAddress.getLocalHost();
                this.serverSocket = new ServerSocket(this.port, 1, localInetAddress);
            } catch (Exception localException1) {
                return;
            }
            Socket localSocket = null;
            while (RTSConf.cur != null && !RTSConf.isRequestExitApp()) {
                try {
                    localSocket = this.serverSocket.accept();
                } catch (Exception localException2) {
                    localSocket = null;
                    break;
                }
                int i = 0;
                for (int j = 0; j < this.clientAdr.size(); ++j) {
                    final String str = (String) this.clientAdr.get(j);
                    if (localSocket.getInetAddress().getHostAddress().equals(str)) {
                        i = 1;
                        break;
                    }
                }
                if (i == 0 && !localInetAddress.equals(localSocket.getInetAddress())) {
                    try {
                        localSocket.close();
                    } catch (Exception ex) {}
                    localSocket = null;
                } else {
                    try {
                        this.out = new PrintWriter(localSocket.getOutputStream(), true);
                        final BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
                        String str;
                        while ((str = localBufferedReader.readLine()) != null) {
                            str = UnicodeTo8bit.load(str);
                            if (str.startsWith("<QUIT QUIT>")) break;
                            while (true) {
                                synchronized (Main.this.oCommandSync) {
                                    if (RTSConf.isRequestExitApp()) break;
                                    if (!Main.this.bCommand) {
                                        Main.this.sCommand = str;
                                        Main.this.bCommandNet = true;
                                        Main.this.bCommand = true;
                                        break;
                                    }
                                }
                                try {
                                    Thread.sleep(10L);
                                } catch (Exception localException3) {}
                            }
                            if (!RTSConf.isRequestExitApp()) continue;
                        }
                        this.out.close();
                        localBufferedReader.close();
                    } catch (Exception ex2) {}
                    this.out = null;
                }
            }
            if (localSocket != null) try {
                localSocket.close();
            } catch (Exception ex3) {}
            try {
                this.serverSocket.close();
            } catch (Exception ex4) {}
        }

        public ConsoleServer(final int paramArrayList, final ArrayList arg3) {
            this.outQueue = new ConsoleOutQueue();
            this.bEnableType = true;
            this.port = paramArrayList;
            this.clientAdr = arg3;
            RTSConf.cur.console.addType(false, this);
            RTSConf.cur.console.addType(true, this);
            this.outWriter = new ConsoleWriter();
        }
    }

    class ConsoleWriter extends Thread {
        public void run() {
            while (RTSConf.cur != null && !RTSConf.isRequestExitApp()) {
                String str = Main.this.consoleServer.outQueue.get();
                str = UnicodeTo8bit.save(str, false);
                if (Main.this.consoleServer.out != null) try {
                    Main.this.consoleServer.out.print(str);
                    Main.this.consoleServer.out.println();
                    if (!Main.this.consoleServer.outQueue.isEmpty()) continue;
                    Main.this.consoleServer.out.flush();
                } catch (Exception ex) {}
            }
        }
    }

    class ConsoleOutQueue {
        LinkedList outQueue;
        int        truncated;

        ConsoleOutQueue() {
            this.outQueue = new LinkedList();
            this.truncated = 0;
        }

        public synchronized void put(final String paramString) {
            final int i = this.outQueue.size();
            if (i >= 1024) ++this.truncated;
            else if (this.truncated > 0) {
                if (i >= 768) ++this.truncated;
                else {
                    this.outQueue.add("!!! Output truncated ( " + this.truncated + " lines )\n");
                    this.outQueue.add(paramString);
                    this.truncated = 0;
                }
            } else this.outQueue.add(paramString);
            this.notifyAll();
        }

        public synchronized String get() {
            while (this.outQueue.isEmpty())
                try {
                    this.wait();
                } catch (InterruptedException localInterruptedException) {}
            return (String) this.outQueue.removeFirst();
        }

        public synchronized boolean isEmpty() {
            return this.outQueue.isEmpty();
        }
    }
}
