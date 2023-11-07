package com.maddox.il2.game;

import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.ConsoleGL0;
import com.maddox.il2.engine.RendersMain;
import com.maddox.il2.objects.effects.ForceFeedback;
import com.maddox.opengl.GLContext;
import com.maddox.rts.BackgroundLoop;
import com.maddox.rts.BackgroundTask;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.Console;
import com.maddox.rts.IniFile;
import com.maddox.rts.MainWin32;
import com.maddox.rts.MainWindow;
import com.maddox.rts.RTSConf;
import com.maddox.rts.RTSConfWin;
import com.maddox.rts.ScreenMode;
import com.maddox.rts.Time;

public class MainWin3D extends Main3D
{
  private ScreenMode saveMode;
  private int saveMouseMode;
  private boolean bChangedScreenMode;
  private boolean bTryChangedScreenMode;
  private boolean bChangeTimerPause;

  public MainWin3D()
  {
    this.bChangedScreenMode = false;
    this.bTryChangedScreenMode = false;
    this.bChangeTimerPause = false;
  }

  public static MainWin3D curWin3D()
  {
    return (MainWin3D)cur();
  }

  private void checkFocus()
  {
    if ((this.bDrawIfNotFocused) || (((MainWin32)RTSConf.cur.mainWindow).IsFocused())) {
      if (!RendersMain.isShow()) {
        if ((Time.isEnableChangePause()) && 
          (this.bChangeTimerPause)) {
          Time.setPause(false);
          this.bChangeTimerPause = false;
        }

        if (this.bChangedScreenMode) {
          ((MainWin32)MainWindow.adapter()).loopMsgs();
          MainWindow.adapter().setMessagesEnable(false);

          ScreenMode.set(this.saveMode);
          if (MainWindow.adapter().isIconic())
            MainWindow.adapter().showNormal();
          ((MainWin32)MainWindow.adapter()).loopMsgs();
          MainWindow.adapter().setFocus();
          ((MainWin32)MainWindow.adapter()).loopMsgs();
          ScreenMode localScreenMode = ScreenMode.readCurrent();
          int i = (this.saveMode.width() != localScreenMode.width()) || (this.saveMode.height() != localScreenMode.height()) ? 1 : 0;
          if (i != 0) MainWindow.adapter().setMessagesEnable(true);
          MainWindow.adapter().setPosSize(0, 0, localScreenMode.width(), localScreenMode.height());
          ((MainWin32)MainWindow.adapter()).loopMsgs();
          if (i == 0) MainWindow.adapter().setMessagesEnable(true);

          RTSConf.cur.setUseMouse(this.saveMouseMode);
          this.bChangedScreenMode = false;
        }
        RendersMain.setShow(true);
        RendersMain.bSwapBuffersResult = true;
        this.bTryChangedScreenMode = false;
      } else if ((!RendersMain.bSwapBuffersResult) && (!RTSConf.isRequestExitApp()) && (Config.cur.windowChangeScreenRes) && (Config.cur.windowFullScreen))
      {
        if (this.bTryChangedScreenMode) {
          Main.doGameExit();
        } else {
          this.bTryChangedScreenMode = true;
          CmdEnv.top().exec("window " + Config.cur.windowWidth + " " + Config.cur.windowHeight + " " + Config.cur.windowColourBits + " " + Config.cur.windowDepthBits + " " + Config.cur.windowStencilBits + " PROVIDER " + Config.cur.glLib + " FULL");

          RendersMain.bSwapBuffersResult = true;
        }
      }

    }
    else if (RendersMain.isShow()) {
      if ((Time.isEnableChangePause()) && 
        (!Time.isPaused())) {
        Time.setPause(true);
        this.bChangeTimerPause = true;
      }
      RendersMain.setShow(false);
      if ((!this.bChangedScreenMode) && (Config.cur.windowChangeScreenRes)) {
        this.saveMouseMode = RTSConf.cur.getUseMouse();
        RTSConf.cur.setUseMouse(0);
        this.saveMode = ScreenMode.readCurrent();
        if (!MainWindow.adapter().isIconic())
          MainWindow.adapter().showIconic();
        ScreenMode.restore();
        this.bChangedScreenMode = true;
      }
      RendersMain.bSwapBuffersResult = true;
      this.bTryChangedScreenMode = false;
    }
  }

  public void loopApp()
  {
    if (this.bUseStartLog) {
      ConsoleGL0.exclusiveDraw(false);
    }
    while (!RTSConf.isRequestExitApp())
      synchronized (RTSConf.lockObject()) {
        if (BackgroundTask.isExecuted()) {
          BackgroundTask.doRun();
        } else {
          checkFocus();
          boolean bool;
          String str;
          synchronized (this.oCommandSync) {
            bool = this.bCommand;
            str = this.sCommand;
            this.bCommand = false;
          }
          if (bool) {
            if (this.consoleServer != null) this.consoleServer.bEnableType = false;
            System.out.println(RTSConf.cur.console._getPrompt() + str);
            if (this.consoleServer != null) this.consoleServer.bEnableType = true;

            RTSConf.cur.console.getEnv().exec(str);

            if (this.consoleServer != null) this.consoleServer.typeNum();
          }

          RTSConf.cur.loopMsgs();
        }
      }
  }

  public void endApp()
  {
    if (Config.cur != null) {
      viewSet_Save();
      Config.cur.save();
      World.cur().save();
    }
    if (Config.cur != null)
      Config.cur.endSound();
    ForceFeedback.stop();

    GLContext localGLContext = RendersMain.glContext();
    if (GLContext.isValid(localGLContext)) {
      localGLContext.destroy();
    }
    if (ScreenMode.current() != ScreenMode.startup())
      ScreenMode.restore();
    if (RTSConf.cur != null) {
      RTSConf.cur.stop();
      if ((RTSConf.cur.mainWindow.isCreated()) && ((RTSConf.cur instanceof RTSConfWin))) {
        ((MainWin32)RTSConf.cur.mainWindow).destroy();
      }
    }
    EventLog.close();
  }

  public boolean beginApp(String paramString1, String paramString2, int paramInt)
  {
    IniFile localIniFile = new IniFile(paramString1);
    RTSConf.cur = new RTSConfWin(localIniFile, "rts", paramInt);
    RTSConf.cur.console.exec = new ConsoleExec();
    Config.cur = new Config(localIniFile, true);
    new Background();

    if ("RU".equals(Config.LOCALE)) {
      MainWin32.GetAppPath();
    }

    if (!super.beginApp(paramString1, paramString2, paramInt))
      return false;
    ForceFeedback.start();

    return true;
  }

  private class Background extends BackgroundLoop
  {
    protected void step()
    {
      MainWin3D.this.checkFocus();
      RTSConf.cur.loopMsgs();
      try { Thread.sleep(1L); } catch (Exception localException) {
      }RTSConf.cur.loopMsgs();
    }
    public Background() { setThisAsCurrent();
    }
  }

  class ConsoleExec extends Console.Exec
  {
    ConsoleExec()
    {
    }

    public void doExec(String paramString)
    {
      RTSConf.cur.console.getEnv().exec(paramString);
      if (MainWin3D.this.consoleServer != null) MainWin3D.this.consoleServer.typeNum(); 
    }

    public String getPrompt() {
      return RTSConf.cur.console._getPrompt();
    }
  }
}