/*
 * UP3 RC4 Class with optional new 4.11+ TrackIR implementation added
 * Backport by SAS~Storebror for TD AI code implementation
 */

package com.maddox.rts;

public final class TrackIRWin implements MsgTimeOutListener {

    public final boolean isCreated() {
        return this.bCreated;
    }

    public final void create() throws MouseWinException {
        if (this.bCreated) return;
        if (RTSConf.cur.mainWindow.hWnd() == 0) throw new RuntimeException("TrackIR windows driver: main window not present");

        // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
        boolean createSuccess = nCreateDT(RTSConf.cur.mainWindow.hWnd(), ProgramID);
        System.out.println("TrackIRWin create(), createSuccess=" + createSuccess);
        if (!createSuccess)
        // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---
        {
            if (SIMULATE_TIR) {
                this.ticker.post();
                this.bCreated = true;
            }
            return;
        } else {
            this.ticker.post();
            this.bCreated = true;
            return;
        }
    }

    public final void destroy() {
        if (this.bCreated) {
            // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
            nDestroyDT();
            // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---
            RTSConf.cur.queueRealTime.remove(this.ticker);
            RTSConf.cur.queueRealTimeNextTick.remove(this.ticker);
            this.bCreated = false;
        }
    }

    public void msgTimeOut(Object obj) {
        if (this.bCreated) {
            // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++

            if (SIMULATE_TIR) {
                RTSConf.cur.trackIR.getAngles(this.nDTangles);
                int index = 3;
                this.nDTangles[index] += 0.1F;
                this.nDTangles[index] %= 360F;
                if (this.nDTangles[index] > 180.0F) this.nDTangles[index] -= 360.0F;
                else if (this.nDTangles[index] < -180.0F) this.nDTangles[index] += 360.0F;
                RTSConf.cur.trackIR.setAngles(this.nDTangles[0], this.nDTangles[1], this.nDTangles[2], this.nDTangles[3], this.nDTangles[4], this.nDTangles[5]);
                this.ticker.post();
                return;
            }

                if (nGetAnglesDT(this.nDTangles))
                    RTSConf.cur.trackIR.setAngles(this.nDTangles[0], this.nDTangles[1], this.nDTangles[2], this.nDTangles[3] * 360F, this.nDTangles[4] * 360F, this.nDTangles[5] * 360F);

            // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---
            this.ticker.post();
        }
    }

    // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
    protected TrackIRWin(int i, boolean flag) {
        this(i, flag, (IniFile) null);
    }

    protected TrackIRWin(int i, boolean flag, IniFile inifile) {

        // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---
        this.nDTangles = new float[6];
        this.bCreated = false;
        this.ticker = new MsgTimeOut(null);
        this.ticker.setTickPos(i);
        this.ticker.setNotCleanAfterSend();
        this.ticker.setFlags(Message.REAL_TIME | Message.CLEAR_TIME | Message.NEXT_TICK);
        this.ticker.setListener(this);
        if (flag) this.create();
    }

    private static final native boolean nCreate(int i);

    private static final native void nDestroy();

    private static final native boolean nGetAngles(float af[]);

    private static int   ProgramID = 1001;
    private boolean      bCreated;
    private static float angles[]  = new float[7];
    private MsgTimeOut   ticker;

    // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
    private static final native boolean nCreateDT(int i, int j);

    private static final native void nDestroyDT();

    private static final native boolean nGetAnglesDT(float af[]);

    private static final boolean SIMULATE_TIR  = false;
    private static boolean       libLoaded     = false;
    private float                nDTangles[];

    public static final void loadNative() {
        if (!libLoaded) {
            System.loadLibrary("DT");
            System.out.println("  *** Library **** DT.dll ****  Loaded *** ");
            libLoaded = true;
        }
    }
    // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---

    public static boolean isUseNewTrackIR()
    {
      return true;
    }

    static {
        RTS.loadNative();
        loadNative();
    }
}
