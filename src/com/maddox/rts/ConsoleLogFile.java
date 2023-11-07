package com.maddox.rts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.sas1946.il2.util.CommonTools;

class ConsoleLogFile implements ConsoleOut {

    public void flush() {
        this.f.flush();
        // TODO: +++ Instant Log by SAS~Storebror +++
        if (Config.cur == null || !Config.cur.bInstantLog) return;
        this.f.close();
        try {
            this.f = new PrintWriter(new FileOutputStream(HomePath.toFileSystemName(this.fileName, 0), true));
        } catch (IOException theIOException) {}
        // TODO: --- Instant Log by SAS~Storebror ---
    }

    public void println(String s) {
        this.f.println(s);
        // TODO: +++ Instant Log by SAS~Storebror +++
        if (Config.cur != null && Config.cur.bInstantLog) this.flush();
        // TODO: --- Instant Log by SAS~Storebror ---
    }

    public void type(String s) {
//        PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out));
//        ps.println("ConsoleLogFile type(" + (s.endsWith("\n")?s.substring(0,  s.length()-1):s) + ") +");
        if (!this.isValidLogLine(s)) return; // TODO: Added by SAS~Storebror to avoid excessive logging of useless information
        if (!this.isPiped && Config.cur != null && Config.cur.bPipedLog && this.fileName != null && this.fileName.length() > 0) if (Time.current() > this.lastPipeAttempt + PIPE_ATTEMPT_DELAY) if (this.pipeAttemptsRemain-- > 0) {
            this.lastPipeAttempt = Time.current();
            this.connectToPipe(this.fileName);
            if (this.isPiped) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, CommonTools.getTimeZoneBiasMinutes());
                this.f.println("[" + this.longDate.format(calendar.getTime()) + this.tzOffsetHours() + SWITCH_TO_PIPE_LOG);
            }
        }
        if (Console.bTypeTimeInLogFile) {
            // TODO: +++ Additional Log Settings by SAS~Storebror +++
            this.checkInit();
            // TODO: --- Additional Log Settings by SAS~Storebror ---
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, CommonTools.getTimeZoneBiasMinutes());
            if (this.shortDate == null)
                this.f.print("[" + this.longDate.format(calendar.getTime()) + this.tzOffsetHours() + "]\t");
            else
                this.f.print("[" + this.shortDate.format(calendar.getTime()) + this.tzOffsetHours() + "]\t");
            // TODO: +++ Additional Log Settings by SAS~Storebror +++
            if (Config.cur != null && Config.cur.bLogTicks) {
                this.f.print("dT:" + this.padNumber(Time.tickCounter() - this.lastTick, 5) + "\t");
                this.lastTick = Time.tickCounter();
            }
            // TODO: --- Additional Log Settings by SAS~Storebror ---
        }
        // TODO: Added by SAS~Storebror - Enable identification of "INTERNAL ERROR: Str2FloatClamp()" log errors.
        if (s.startsWith("INTERNAL ERROR: Str2FloatClamp()")) this.f.print(Eff3DActor.getLastEffectFile() + " : ");
        // ---
        // TODO: Added by SAS~Storebror - Enable identification of "INTERNAL ERROR: Str2FloatClamp()" log errors.
        else if (s.startsWith("WARNING: TSmokeTrail::TSmokeTrail() Estimated number of particles")) this.f.print(Eff3DActor.getLastEffectFile() + " : ");
        // ---
        this.f.print(s);
        // TODO: +++ Instant Log by SAS~Storebror +++
        if (Config.cur != null && Config.cur.bInstantLog) this.flush();
        if (this.isPiped) this.f.flush();
        // TODO: --- Instant Log by SAS~Storebror ---
//        ps.println("ConsoleLogFile type(" + (s.endsWith("\n")?s.substring(0,  s.length()-1):s) + ") -");
    }

    // TODO: Added by SAS~Storebror to avoid excessive logging of useless information
    private boolean isValidLogLine(String s) {
        if (s.startsWith("WARNING: Clear VBuf")) return false;
        if (s.startsWith("WARNING: Clear IBuf")) return false;
        if (s.startsWith("WARNING: TRenderManager::TriangleList_ProjectiveShadow()")) {
            if (timeBetween(this.lastProjectiveShadowWarning, Time.currentReal()) < WARNING_ANTIFLOOD_LOG_FREQUENCY) return false;
            this.lastProjectiveShadowWarning = Time.currentReal();
            return true;
        }
        if (s.startsWith("Too many vertexes or faces per shadow")) {
            if (timeBetween(this.lastShadowVertexWarning, Time.currentReal()) < WARNING_ANTIFLOOD_LOG_FREQUENCY) return false;
            this.lastShadowVertexWarning = Time.currentReal();
            return true;
        }
        if (s.startsWith("WARNING: HW Shadows not works on mesh with scaling")) {
            if (timeBetween(this.lastHwShadowScalingWarning, Time.currentReal()) < WARNING_ANTIFLOOD_LOG_FREQUENCY) return false;
            this.lastHwShadowScalingWarning = Time.currentReal();
            return true;
        }
        if (s.startsWith("WARNING: To many faces") && s.indexOf("or Vertexes") != -1) {
            if (timeBetween(this.lastFacesOrVertexWarning, Time.currentReal()) < WARNING_ANTIFLOOD_LOG_FREQUENCY) return false;
            this.lastFacesOrVertexWarning = Time.currentReal();
            return true;
        }
        if (s.startsWith("INTERNAL ERROR: Str2FloatClamp()") && (Eff3DActor.getLastEffectFile() == null || Eff3DActor.getLastEffectFile().length() < 3)) return false;
        if (s.startsWith("WARNING: TSmokeTrail::TSmokeTrail() Estimated number of particles") && (Eff3DActor.getLastEffectFile() == null || Eff3DActor.getLastEffectFile().length() < 3)) return false;
        return true;
    }

    // TODO: +++ Additional Log Settings by SAS~Storebror +++
    private String padNumber(long number, int len) {
        String retVal = String.valueOf(number);
        int retValLen = retVal.length();
        if (retValLen < len) retVal = new String(new char[len - retValLen]).replace('\0', ' ') + retVal;
        return retVal;
    }
    // TODO: --- Additional Log Settings by SAS~Storebror ---

    public void close() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, CommonTools.getTimeZoneBiasMinutes());
        this.f.println();
        this.f.println("[" + this.longDate.format(calendar.getTime()) + this.tzOffsetHours() + END_LOG);
        this.f.close();
    }

    public ConsoleLogFile(String s) throws FileNotFoundException {
        // TODO: +++ Instant Log by SAS~Storebror +++
        this.fileName = s;
        this.lastPipeAttempt = -9999L;
        this.pipeAttemptsRemain = PIPE_ATTEMPTS;
        
        // TODO: +++ Avoid Log Flooding for non-perfect-mode users (e.g. DirectX ones) +++
        this.lastProjectiveShadowWarning = Long.MIN_VALUE;
        this.lastShadowVertexWarning = Long.MIN_VALUE;
        this.lastFacesOrVertexWarning = Long.MIN_VALUE;
        this.lastHwShadowScalingWarning = Long.MIN_VALUE;
        // TODO: --- Avoid Log Flooding for non-perfect-mode users (e.g. DirectX ones) ---
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, CommonTools.getTimeZoneBiasMinutes());
        this.longDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        // TODO: --- Instant Log by SAS~Storebror ---

        // this.connectToPipe(s);
        this.f = new PrintWriter(new FileOutputStream(HomePath.toFileSystemName(s, 0), true));

        // TODO: +++ Additional Log Settings by SAS~Storebror +++
//        this.shortDate = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        // TODO: --- Additional Log Settings by SAS~Storebror ---
        this.f.println();

        this.f.println("[" + this.longDate.format(calendar.getTime()) + this.tzOffsetHours() + BEGIN_LOG);
        this.f.println();
        this.f.flush();
    }

    private void connectToPipe(String s) {
        try {
            if (this.f != null) {
                this.f.flush();
                this.f.close();
                this.f = null;
            }
            this.f = new PrintWriter(new FileOutputStream(new File(PIPE_URL)));
            this.f.print(HomePath.toFileSystemName(s, 0) + PIPE_FLUSH_TIMEOUT + Config.cur.iLogFlushTimeout); // \u0000 = Separator, Flush Timeout = 1000ms default
//            this.f.print(HomePath.toFileSystemName(s, 0) + PIPE_FLUSH_TIMEOUT_1000); // \u0000 = Separator, Flush Timeout = 1000ms default
            this.f.flush();
            this.isPiped = true;
        } catch (FileNotFoundException fnfe1) {
            this.isPiped = false;
            this.f = null;
        }
        if (this.f == null) try {
            this.f = new PrintWriter(new FileOutputStream(HomePath.toFileSystemName(s, 0), true));
        } catch (FileNotFoundException fnfe2) {
            fnfe2.printStackTrace();
            // this.isPiped = false;
            System.exit(-1);
        }
    }

    private String tzOffsetHours() {
        float fTzOffset = CommonTools.getTimeZoneBiasHours();
        String retVal = " UTC";
        if (CommonTools.equals(fTzOffset, 0F)) return retVal;
        if (fTzOffset >= 0F) retVal += " +";
        else retVal += " -";
        retVal += dfTzOffset.format(Math.abs(fTzOffset));
        return retVal;
    }

    // TODO: +++ Additional Log Settings by SAS~Storebror +++
    public void checkInit() {
        if (Config.cur == null || this.shortDate != null) return;
        String shortDateFormat = "HH:mm:ss";
        if (Config.cur.bLogDate) shortDateFormat = "yyyy-MM-dd " + shortDateFormat;
        if (Config.cur.bLogMilliseconds) shortDateFormat = shortDateFormat + ".SSS";
        this.shortDate = new SimpleDateFormat(shortDateFormat);
        if (Config.cur.bLogTicks) this.lastTick = Time.tickCounter();
    }
    // TODO: --- Additional Log Settings by SAS~Storebror ---

    // TODO: +++ Avoid Log Flooding for non-perfect-mode users (e.g. DirectX ones) +++
    private static long timeBetween(long millisStart, long millisEnd) {
        long elapsed = millisEnd - millisStart;
        if (elapsed < 0) {
            elapsed = Long.MAX_VALUE - millisStart + millisEnd;
        }
        return elapsed;
    }
    // TODO: --- Avoid Log Flooding for non-perfect-mode users (e.g. DirectX ones) ---

    private PrintWriter                f;
    private DateFormat                 longDate;
    private DateFormat                 shortDate;
    // TODO: +++ Instant Log by SAS~Storebror +++
    private String                     fileName;
    // TODO: --- Instant Log by SAS~Storebror ---
    // TODO: +++ Additional Log Settings by SAS~Storebror +++
    private long                       lastTick;
    private long                       lastPipeAttempt;
    private int                        pipeAttemptsRemain;
    private boolean                    isPiped            = false;
    private static final int           PIPE_ATTEMPTS      = 60;
    private static final long          PIPE_ATTEMPT_DELAY = 1000;
    private static final String        PIPE_URL           = "\\\\.\\pipe\\SAS_PIPE_LOGGER";
    private static final String        PIPE_FLUSH_TIMEOUT = "\u0000"; // \u0000 = Separator
//    private static final String PIPE_FLUSH_TIMEOUT_1000 = "\u00001000"; // \u0000 = Separator, Flush Timeout = 1000ms
    private static final String        BEGIN_LOG          = "] ------------ BEGIN log session -------------";
    private static final String        END_LOG            = "] -------------- END log session -------------";
    private static final String        SWITCH_TO_PIPE_LOG = "] ----------- Switched to pipe mode ----------";
    private static final DecimalFormat dfTzOffset         = new DecimalFormat("0.#");
    // TODO: --- Additional Log Settings by SAS~Storebror ---
    
    // TODO: +++ Avoid Log Flooding for non-perfect-mode users (e.g. DirectX ones) +++
    private static final long          WARNING_ANTIFLOOD_LOG_FREQUENCY = 60000;
    private long                       lastProjectiveShadowWarning;
    private long                       lastShadowVertexWarning;
    private long                       lastFacesOrVertexWarning;
    private long                       lastHwShadowScalingWarning;
    // TODO: --- Avoid Log Flooding for non-perfect-mode users (e.g. DirectX ones) ---
}
