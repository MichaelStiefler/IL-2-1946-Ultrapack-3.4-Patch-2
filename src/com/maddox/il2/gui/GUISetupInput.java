//package com.maddox.il2.gui;
//
//import java.util.ResourceBundle;
//
//import com.maddox.gwindow.GBevel;
//import com.maddox.gwindow.GColor;
//import com.maddox.gwindow.GTexture;
//import com.maddox.gwindow.GWindow;
//import com.maddox.gwindow.GWindowComboControl;
//import com.maddox.gwindow.GWindowEditControl;
//import com.maddox.gwindow.GWindowHSliderInt;
//import com.maddox.gwindow.GWindowMessageBox;
//import com.maddox.gwindow.GWindowRoot;
//import com.maddox.gwindow.GWindowVSliderInt;
//import com.maddox.il2.ai.UserCfg;
//import com.maddox.il2.ai.World;
//import com.maddox.il2.engine.Config;
//import com.maddox.il2.game.GameState;
//import com.maddox.il2.game.Main;
//import com.maddox.il2.game.Mission;
//import com.maddox.il2.objects.effects.ForceFeedback;
//import com.maddox.rts.HotKeyEnv;
//import com.maddox.rts.Joy;
//import com.maddox.rts.JoyFF;
//import com.maddox.rts.LDRres;
//import com.maddox.rts.Mouse;
//import com.maddox.rts.RTSConf;
//import com.maddox.rts.VK;
//import com.maddox.util.HashMapInt;
//import com.maddox.util.HashMapIntEntry;
//
//public class GUISetupInput extends GameState
//{
//  public GWindowComboControl wJoyProfile;
//  public GUIButton bLoad;
//  public GUIButton bSave;
//  public GUIButton bJoyInfo;
//  public GWindowEditControl wJoyX;
//  public GWindowEditControl wJoyY;
//  public GWindowEditControl wJoyZ;
//  public GWindowEditControl wJoy4;
//  public GUISwitchBox3 sMirrorControl;
//  public static final int MAX_DEAD_BAND = 50;
//  public static final int MAX_FILTER = 10;
//  public GUIClient client;
//  public DialogClient dialogClient;
//  public GUIInfoMenu infoMenu;
//  public GUIInfoName infoName;
//  public GWindowComboControl comboAxe;
//  public GWindowVSliderInt[] slider;
//  public GWindowEditControl[] edit;
//  public GWindowHSliderInt deadSlider;
//  public GWindowHSliderInt filterSlider;
//  public GWindowEditControl wMouse;
//  public GUIButton bBack;
//  public GUIButton bDefault;
//  public GUIButton bRefresh;
//  public GUISwitchBox3 sForceFeedback;
//  public GUISwitchBox3 sUseSmartAxis;
//  public GUIButton bControlPanel;
//  private int[] comboAxeReversed;
//  private int[] comboAxeInts;
//  private int[] comboAxeREA = { -1, -1, -1 };
////  private int[] comboAxeIndx = { 1, 0, 5 };
//  public Joy joy;
//  private int[] js;
//
//  public void _enter()
//  {
//    fillDialogs();
//    if(JoyFF.isAttached())
//    {
//        sForceFeedback.setEnable(true);
//        sForceFeedback.showWindow();
//        sUseSmartAxis.setEnable(true);
//        sUseSmartAxis.showWindow();
//        sForceFeedback.setChecked(JoyFF.isStarted(), false);
//        sUseSmartAxis.setChecked(World.cur().useSmartAxis, false);
//    } else
//    {
//        sForceFeedback.setEnable(false);
//        sForceFeedback.hideWindow();
//        sUseSmartAxis.setEnable(false);
//        sUseSmartAxis.hideWindow();
//    }
//    this.client.activateWindow();
//    if(Main.stateStack().getPrevious() != null && Main.stateStack().getPrevious().id() == 20)
//    {
//        bControlPanel.setEnable(false);
//        bControlPanel.hideWindow();
//    } else
//    {
//        bControlPanel.setEnable(true);
//        bControlPanel.showWindow();
//    }
//  }
//
//  public void _leave()
//  {
//    this.client.hideWindow();
//  }
//
//  private void fillDialogs()
//  {
//      int i = 0;
//    for (i = 0; i < 3; i++) {
//      this.comboAxeREA[i] = -1;
//    }
//    for (i = 0; i < 64; i++) {
//      this.comboAxeReversed[i] = 1;
//    }
//    i = 0;
//    int j = this.comboAxe.getSelected();
//    this.comboAxe.clear(false);
//    String[] arrayOfString = UserCfg.nameHotKeyEnvs;
//    int k = 0;
//    for (k = 0; k < arrayOfString.length; k++)
//    {
//      if (arrayOfString[k].equals("move")) {
//        HotKeyEnv localHotKeyEnv = HotKeyEnv.env(arrayOfString[k]);
//        HashMapInt localHashMapInt = localHotKeyEnv.all();
//        HashMapIntEntry localHashMapIntEntry = localHashMapInt.nextEntry(null);
//
//        while (localHashMapIntEntry != null)
//        {
//          this.comboAxeInts[i] = localHashMapIntEntry.getKey();
//          String str1 = (String)localHashMapIntEntry.getValue();
//          if (str1.startsWith("-")) {
//            str1 = str1.substring(1);
//            this.comboAxeReversed[i] = -1;
//          }String str2;
//          try {
//            str2 = ResourceBundle.getBundle("i18n/controls", RTSConf.cur.locale, LDRres.loader()).getString(str1);
//          }
//          catch (Exception localException) {
//            str2 = str1;
//            System.out.println("Warning: Control" + str1 + "is not present in i18n/controls.properties file");
//          }
//          int m = (this.comboAxeInts[i] & 0xFFFF) - 716;
//          int n = (this.comboAxeInts[i] >> 16) - 535;
//          if (VK.getKeyText(this.comboAxeInts[i] >> 16 & 0xFFFF).startsWith("Joystick")) {
//            this.comboAxeInts[i] = ((this.comboAxeInts[i] >> 16) + ((this.comboAxeInts[i] & 0xFFFF) << 16));
//            m = (this.comboAxeInts[i] & 0xFFFF) - 716;
//            n = (this.comboAxeInts[i] >> 16) - 535;
//          }
//          if (Joy.adapter().isExistAxe(m, n)) {
//            this.comboAxe.add(str2);
//            if (str1.equals("rudder")) {
//              this.comboAxeREA[0] = i;
//            }
//            else if (str1.equals("elevator")) {
//              this.comboAxeREA[1] = i;
//            }
//            else if (str1.equals("aileron")) {
//              this.comboAxeREA[2] = i;
//            }
//
//            i++;
//          }
//          localHashMapIntEntry = localHashMapInt.nextEntry(localHashMapIntEntry);
//        }
//      }
//    }
//
//    if (i == 0)
//    {
//      this.sMirrorControl.setEnable(false);
//      for (k = 0; k < 9; k++)
//      {
//        this.slider[k].setEnable(false);
//        this.edit[k].setEnable(false);
//      }
//
//      this.deadSlider.setEnable(false);
//      this.filterSlider.setEnable(false);
//      this.comboAxe.add(i18n("setupIn.none"));
//      this.comboAxe.setSelected(0, true, false);
//      this.comboAxe.setEnable(false);
//    }
//    else {
//      this.sMirrorControl.setEnable(true);
//      for (k = 0; k < 9; k++)
//      {
//        this.slider[k].setEnable(true);
//        this.edit[k].setEnable(true);
//      }
//
//      this.deadSlider.setEnable(true);
//      this.filterSlider.setEnable(true);
//      this.comboAxe.setEnable(true);
//      if ((j < 0) || (j >= i))
//        j = 0;
//      this.comboAxe.setSelected(-1, false, false);
//      this.comboAxe.setSelected(j, true, true);
//    }
//    float[] arrayOfFloat = Mouse.adapter().getSensitivity();
//    this.wMouse.setValue("" + arrayOfFloat[0], false);
//  }
//
//  private int curAxe()
//  {
//    int i = this.comboAxe.getSelected();
//    int j = this.comboAxeInts[i];
//    if (i < 0)
//      return -1;
//    return j;
//  }
//
//  private void fillSliders(int paramInt)
//  {
//    if (paramInt < 0)
//      return;
//    int i = (paramInt & 0xFFFF) - 716;
//    int j = (paramInt >> 16) - 535;
//    Joy.adapter().getSensitivity(i, j, this.js);
//    if (this.js[12] == 0) {
//      this.sMirrorControl.setChecked(true, false);
//    }
//    else {
//      this.sMirrorControl.setChecked(false, false);
//    }
//    if (this.js[0] < 0)
//      this.js[0] = 0;
//    if (this.js[0] > 50)
//      this.js[0] = 50;
//    this.deadSlider.setPos(this.js[0], false);
//    this.filterSlider.setPos(10 * this.js[11] / 100, false);
//    for (int k = 0; k < 10; k++)
//    {
//      if (this.js[(k + 1)] < 0)
//        this.js[(k + 1)] = 0;
//      if (this.js[(k + 1)] > 100)
//        this.js[(k + 1)] = 100;
//      this.slider[k].setPos(this.js[(k + 1)], false);
//      this.edit[k].setValue("" + this.js[(k + 1)], false);
//    }
//  }
//
//  private void doSetDefault()
//  {
//    this.js[0] = 0;
//    int i = 0;
//    for (i = 1; i < 11; i++) {
//      this.js[i] = (i * 10);
//    }
//    this.js[11] = 0;
//    this.js[12] = 0;
//    for (i = 0; i < 4; i++) {
//      for (int j = 0; j < 8; j++) {
//        if (Joy.adapter().isExistAxe(i, j))
//          Joy.adapter().setSensitivity(i, j, this.js);
//      }
//    }
//    float[] arrayOfFloat = Mouse.adapter().getSensitivity();
//    float tmp109_108 = 1.0F; arrayOfFloat[1] = tmp109_108; arrayOfFloat[0] = tmp109_108;
//    fillDialogs();
//  }
//
//  private void setJoyS(int paramInt1, int paramInt2)
//  {
//    int i = curAxe();
//    int j = (i & 0xFFFF) - 716;
//    int k = (i >> 16) - 535;
//    if (i < 0)
//    {
//      return;
//    }
//
//    Joy.adapter().getSensitivity(j, k, this.js);
//    this.js[paramInt1] = paramInt2;
//    Joy.adapter().setSensitivity(j, k, this.js);
//    fillSliders(i);
//  }
//
//  private float clampValue(GWindowEditControl paramGWindowEditControl, float paramFloat1, float paramFloat2, float paramFloat3)
//  {
//    String str = paramGWindowEditControl.getValue();
//    try
//    {
//      paramFloat1 = Float.parseFloat(str);
//    } catch (Exception localException) {
//    }
//    if (paramFloat1 < paramFloat2)
//      paramFloat1 = paramFloat2;
//    if (paramFloat1 > paramFloat3)
//      paramFloat1 = paramFloat3;
//    paramGWindowEditControl.setValue("" + paramFloat1, false);
//    return paramFloat1;
//  }
//
//  private int clampValue(GWindowEditControl paramGWindowEditControl, int paramInt1, int paramInt2, int paramInt3)
//  {
//    String str = paramGWindowEditControl.getValue();
//    try
//    {
//      paramInt1 = Integer.parseInt(str);
//    } catch (Exception localException) {
//    }
//    if (paramInt1 < paramInt2)
//      paramInt1 = paramInt2;
//    if (paramInt1 > paramInt3)
//      paramInt1 = paramInt3;
//    paramGWindowEditControl.setValue("" + paramInt1, false);
//    return paramInt1;
//  }
//
//  public GUISetupInput(GWindowRoot paramGWindowRoot)
//  {
//    super(53);
//    this.slider = new GWindowVSliderInt[10];
//    this.edit = new GWindowEditControl[10];
//    this.js = new int[13];
//    this.client = ((GUIClient)paramGWindowRoot.create(new GUIClient()));
//    this.dialogClient = ((DialogClient)this.client.create(new DialogClient()));
//    this.infoMenu = ((GUIInfoMenu)this.client.create(new GUIInfoMenu()));
//    this.infoMenu.info = i18n("setupIn.info");
//    this.infoName = ((GUIInfoName)this.client.create(new GUIInfoName()));
//    GTexture localGTexture = ((GUILookAndFeel)paramGWindowRoot.lookAndFeel()).buttons2;
//    this.dialogClient.addControl(this.comboAxe = new GWindowComboControl(this.dialogClient, 0.0F, 0.0F, 1.0F));
//    this.wJoyProfile = ((GWindowComboControl)this.dialogClient.addControl(new GWindowComboControl(this.dialogClient, 2.0F, 2.0F, 20.0F + paramGWindowRoot.lookAndFeel().getHScrollBarW() / paramGWindowRoot.lookAndFeel().metric())));
//    this.bLoad = ((GUIButton)this.dialogClient.addControl(new GUIButton(this.dialogClient, localGTexture, 0.0F, 48.0F, 48.0F, 48.0F)));
//    this.bSave = ((GUIButton)this.dialogClient.addControl(new GUIButton(this.dialogClient, localGTexture, 0.0F, 48.0F, 48.0F, 48.0F)));
//    this.sMirrorControl = ((GUISwitchBox3)this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient)));
//    this.comboAxe.setEditable(false);
//    this.comboAxe.resized();
//    for (int i = 0; i < 10; i++)
//    {
//      this.dialogClient.addControl(this.slider[i] = new GWindowVSliderInt(this.dialogClient));
//      this.slider[i].setRange(0, 101, 10 * (i + 1));
//      this.edit[i] = ((GWindowEditControl)this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null)
//      {
//        public void keyboardKey(int paramInt, boolean paramBoolean)
//        {
//          super.keyboardKey(paramInt, paramBoolean);
//          if ((paramInt == 10) && (paramBoolean))
//            notify(2, 0);
//        }
//      }));
//      this.edit[i].bSelectOnFocus = false;
//      this.edit[i].bDelayedNotify = true;
//      this.edit[i].bNumericOnly = true;
//      this.edit[i].align = 1;
//    }
//
//    this.dialogClient.addControl(this.deadSlider = new GWindowHSliderInt(this.dialogClient));
//    this.deadSlider.setRange(0, 51, 0);
//    this.dialogClient.addControl(this.filterSlider = new GWindowHSliderInt(this.dialogClient));
//    this.filterSlider.setRange(0, 11, 0);
//    this.wMouse = ((GWindowEditControl)this.dialogClient.addControl(new GWindowEditControl(this.dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null)
//    {
//      public void keyboardKey(int paramInt, boolean paramBoolean)
//      {
//        super.keyboardKey(paramInt, paramBoolean);
//        if ((paramInt == 10) && (paramBoolean))
//          notify(2, 0);
//      }
//    }));
//    this.wMouse.bNumericOnly = this.wMouse.bNumericFloat = true;
//    this.wMouse.bDelayedNotify = true;
//    this.wMouse.align = 1;
//    this.wJoyProfile.add(i18n("setupIn.Joystick1"));
//    this.wJoyProfile.add(i18n("setupIn.Joystick2"));
//    this.wJoyProfile.add(i18n("setupIn.Joystick3"));
//    this.wJoyProfile.add(i18n("setupIn.Joystick4"));
//    wJoyProfile.add(i18n("setupIn.Joystick5"));
//    wJoyProfile.add(i18n("setupIn.Joystick6"));
//    wJoyProfile.add(i18n("setupIn.Joystick7"));
//    wJoyProfile.add(i18n("setupIn.Joystick8"));
//    this.wJoyProfile.setEditable(false);
//    this.wJoyProfile.setSelected(Config.cur.ini.get("rts", "JoyProfile", 0), true, false);
//    this.comboAxeInts = new int[64];
//    this.comboAxeReversed = new int[64];
//    this.bControlPanel = ((GUIButton)this.dialogClient.addControl(new GUIButton(this.dialogClient, localGTexture, 0.0F, 48.0F, 48.0F, 48.0F)));
//    this.bDefault = ((GUIButton)this.dialogClient.addControl(new GUIButton(this.dialogClient, localGTexture, 0.0F, 48.0F, 48.0F, 48.0F)));
//    bRefresh = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, localGTexture, 0.0F, 48F, 48F, 48F));
//    this.bBack = ((GUIButton)this.dialogClient.addEscape(new GUIButton(this.dialogClient, localGTexture, 0.0F, 96.0F, 48.0F, 48.0F)));
//      this.sForceFeedback = ((GUISwitchBox3)this.dialogClient.addControl(new GUISwitchBox3(this.dialogClient)));
//      sUseSmartAxis = (GUISwitchBox3)dialogClient.addControl(new GUISwitchBox3(dialogClient));
//      bJoyInfo = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, localGTexture, 0.0F, 48F, 48F, 48F));
//      wJoyX = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {
//      }
//);
//      wJoyX.bCanEdit = false;
//      wJoyX.bSelectOnFocus = false;
//      wJoyX.bDelayedNotify = true;
//      wJoyX.bNumericOnly = true;
//      wJoyX.align = 1;
//      wJoyX.bNotify = false;
//      wJoyY = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {
//
//      }
//);
//      wJoyY.bCanEdit = false;
//      wJoyY.bSelectOnFocus = false;
//      wJoyY.bDelayedNotify = true;
//      wJoyY.bNumericOnly = true;
//      wJoyY.align = 1;
//      wJoyY.bNotify = false;
//      wJoyZ = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {
//
//      }
//);
//      wJoyZ.bCanEdit = false;
//      wJoyZ.bSelectOnFocus = false;
//      wJoyZ.bDelayedNotify = true;
//      wJoyZ.bNumericOnly = true;
//      wJoyZ.align = 1;
//      wJoyZ.bNotify = false;
//      wJoy4 = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {
//
//      }
//);
//      wJoy4.bCanEdit = false;
//      wJoy4.bSelectOnFocus = false;
//      wJoy4.bDelayedNotify = true;
//      wJoy4.bNumericOnly = true;
//      wJoy4.align = 1;
//      wJoy4.bNotify = false;
//    this.dialogClient.activateWindow();
//    this.client.hideWindow();
//  }
//
//  public class DialogClient extends GUIDialogClient
//  {
//    private int[] joyPos;
//    private int[] joyRawPos;
//
//    public boolean notify(GWindow paramGWindow, int paramInt1, int paramInt2)
//    {
//      if (paramInt1 != 2)
//        return super.notify(paramGWindow, paramInt1, paramInt2);
//      if (paramGWindow == GUISetupInput.this.bControlPanel)
//      {
//        if (GUISetupInput.this.wJoyProfile.getSelected() > 0) {
//          Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick" + GUISetupInput.this.wJoyProfile.getSelected());
//        }
//        else {
//          Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick");
//        }
//        Main.stateStack().push(20);
//        GUIControls localGUIControls = (GUIControls)Main.stateStack().peek();
//        localGUIControls.scrollClient.vScroll.setPos(localGUIControls.scrollClient.vScroll.posMax, true);
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.sMirrorControl) {
//        if (GUISetupInput.this.js[12] == 0) {
//          if (GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxe.getSelected()] == -1.0D) {
//            GUISetupInput.this.js[12] = 2;
//          }
//          else {
//            GUISetupInput.this.js[12] = 1;
//          }
//        }
//        else {
//          GUISetupInput.this.js[12] = 0;
//        }
//        GUISetupInput.this.setJoyS(12, GUISetupInput.this.js[12]);
//        setPosSize();
//        return true;
//      }
//
//      if(paramGWindow == bJoyInfo)
//      {
//          String s = i18n("setupIn.Discovered") + "\n";
//          for(int k = 0; k < Joy.adapter().getAmount(); k++)
//              s = s + i18n("setupIn.JD" + k) + " " + Joy.adapter().getJoyName(k) + "\n";
//
//          new GWindowMessageBox(this, 20F, true, i18n("setupIn.JoyInfo"), s, 3, 0.0F);
//          return true;
//      }
//      if (paramGWindow == GUISetupInput.this.bSave)
//      {
//        Mouse.adapter().saveConfig(Config.cur.ini, "rts_mouse");
//        if(sUseSmartAxis != null)
//            World.cur().useSmartAxis = sUseSmartAxis.isChecked();
//        if (GUISetupInput.this.sForceFeedback != null)
//          if (GUISetupInput.this.sForceFeedback.isChecked())
//          {
//            if (!JoyFF.isStarted())
//            {
//              JoyFF.setEnable(true);
//              ForceFeedback.start();
//              if (Mission.isPlaying())
//                ForceFeedback.startMission();
//            }
//          }
//          else if (JoyFF.isStarted())
//          {
//            ForceFeedback.stop();
//            JoyFF.setEnable(false);
//          }
//        if (GUISetupInput.this.wJoyProfile.getSelected() > 0) {
//          Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick" + GUISetupInput.this.wJoyProfile.getSelected());
//        }
//        else {
//          Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick");
//        }
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.bLoad)
//      {
//        if (GUISetupInput.this.wJoyProfile.getSelected() > 0) {
//          Joy.adapter().loadConfig(Config.cur.ini, "rts_joystick" + GUISetupInput.this.wJoyProfile.getSelected());
//        }
//        else {
//          Joy.adapter().loadConfig(Config.cur.ini, "rts_joystick");
//        }
//        GUISetupInput.this.fillDialogs();
//        return true;
//      }
//
//      if (paramGWindow == GUISetupInput.this.comboAxe)
//      {
//          fillSliders(curAxe());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[0])
//      {
//          setJoyS(1, slider[0].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[1])
//      {
//        GUISetupInput.this.setJoyS(2, GUISetupInput.this.slider[1].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[2])
//      {
//        GUISetupInput.this.setJoyS(3, GUISetupInput.this.slider[2].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[3])
//      {
//        GUISetupInput.this.setJoyS(4, GUISetupInput.this.slider[3].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[4])
//      {
//        GUISetupInput.this.setJoyS(5, GUISetupInput.this.slider[4].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[5])
//      {
//        GUISetupInput.this.setJoyS(6, GUISetupInput.this.slider[5].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[6])
//      {
//        GUISetupInput.this.setJoyS(7, GUISetupInput.this.slider[6].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[7])
//      {
//        GUISetupInput.this.setJoyS(8, GUISetupInput.this.slider[7].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[8])
//      {
//        GUISetupInput.this.setJoyS(9, GUISetupInput.this.slider[8].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.slider[9])
//      {
//        GUISetupInput.this.setJoyS(10, GUISetupInput.this.slider[9].pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.deadSlider)
//      {
//        GUISetupInput.this.setJoyS(0, GUISetupInput.this.deadSlider.pos());
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.filterSlider)
//      {
//        GUISetupInput.this.setJoyS(11, 100 * GUISetupInput.this.filterSlider.pos() / 10);
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[0])
//      {
//          setJoyS(1, clampValue(edit[0], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[1])
//      {
//        GUISetupInput.this.setJoyS(2, clampValue(GUISetupInput.this.edit[1], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[2])
//      {
//        GUISetupInput.this.setJoyS(3, clampValue(GUISetupInput.this.edit[2], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[3])
//      {
//        GUISetupInput.this.setJoyS(4, clampValue(GUISetupInput.this.edit[3], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[4])
//      {
//        GUISetupInput.this.setJoyS(5, clampValue(GUISetupInput.this.edit[4], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[5])
//      {
//        GUISetupInput.this.setJoyS(6, clampValue(GUISetupInput.this.edit[5], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[6])
//      {
//        GUISetupInput.this.setJoyS(7, clampValue(GUISetupInput.this.edit[6], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[7])
//      {
//        GUISetupInput.this.setJoyS(8, clampValue(GUISetupInput.this.edit[7], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[8])
//      {
//        GUISetupInput.this.setJoyS(9, clampValue(GUISetupInput.this.edit[8], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.edit[9])
//      {
//        GUISetupInput.this.setJoyS(10, clampValue(GUISetupInput.this.edit[9], 0, 0, 100));
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.wMouse)
//      {
//        float f = GUISetupInput.this.clampValue(GUISetupInput.this.wMouse, 1.0F, 0.1F, 10.0F);
//        float[] arrayOfFloat = Mouse.adapter().getSensitivity();
//        float tmp1452_1450 = f; arrayOfFloat[1] = tmp1452_1450; arrayOfFloat[0] = tmp1452_1450;
//        GUISetupInput.this.wMouse.setValue("" + f, false);
//        return true;
//      }
//      if (paramGWindow == GUISetupInput.this.bDefault)
//      {
//        GUISetupInput.this.doSetDefault();
//        return true;
//      }
//      if(paramGWindow == bRefresh)
//      {
//          RTSConf.cur.stop();
//          RTSConf.cur.start();
//          ForceFeedback.stopMission();
//          ForceFeedback.stop();
//          JoyFF.reattach();
//          if(JoyFF.isEnable() && !JoyFF.isStarted())
//          {
//              ForceFeedback.start();
//              if(Mission.isPlaying())
//                  ForceFeedback.startMission();
//          }
//          _enter();
//          return true;
//      }
//      if (paramGWindow == GUISetupInput.this.bBack)
//      {
//        Mouse.adapter().saveConfig(Config.cur.ini, "rts_mouse");
//        if (GUISetupInput.this.sForceFeedback != null)
//          if (GUISetupInput.this.sForceFeedback.isChecked())
//          {
//            if (!JoyFF.isStarted())
//            {
//              JoyFF.setEnable(true);
//              ForceFeedback.start();
//              if (Mission.isPlaying())
//                ForceFeedback.startMission();
//            }
//          }
//          else if (JoyFF.isStarted())
//          {
//            ForceFeedback.stop();
//            JoyFF.setEnable(false);
//          }
//        Config.cur.ini.set("rts", "JoyProfile", GUISetupInput.this.wJoyProfile.getSelected());
//        if (GUISetupInput.this.wJoyProfile.getSelected() > 0)
//        {
//          Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick" + GUISetupInput.this.wJoyProfile.getSelected());
//        }
//        else Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick");
//        Main.stateStack().pop();
//        return true;
//      }
//
//      return super.notify(paramGWindow, paramInt1, paramInt2);
//    }
//
//    public void render()
//    {
//      super.render();
//      GUISeparate.draw(this, GColor.Gray, x1024(256.0F), y1024(48.0F), x1024(160.0F), 2.0F);
//      GUISeparate.draw(this, GColor.Gray, x1024(336.0F), y1024(490.0F), x1024(528.0F), 2.0F);
//      GUISeparate.draw(this, GColor.Gray, x1024(32.0F), y1024(576.0F), x1024(832.0F), 2.0F);
//      GUISeparate.draw(this, GColor.Gray, x1024(336.0F), y1024(48.0F), 2.0F, y1024(444.0F));
//      setCanvasColor(GColor.Gray);
//      setCanvasFont(0);
//      draw(x1024(48.0F), y1024(32.0F), x1024(192.0F), y1024(32.0F), 2, GUISetupInput.this.i18n("setupIn.JoystickAxes"));
//      draw(x1024(432.0F), y1024(32.0F), x1024(208.0F), y1024(32.0F), 0, GUISetupInput.this.i18n("setupIn.Profile"));
////      draw(x1024(384.0F), y1024(80.0F), x1024(400.0F), y1024(32.0F), 0, GUISetupInput.this.comboAxe.getValue());
//      draw(x1024(341F), y1024(608F), x1024(122F), y1024(48F), 0, i18n("setupIn.JoyInfo"));
//      draw(x1024(368.0F), y1024(364.0F), x1024(208.0F), y1024(32.0F), 2, GUISetupInput.this.i18n("setupIn.DeadBand"));
//      draw(x1024(576.0F), y1024(364.0F), x1024(48.0F), y1024(32.0F), 1, "0");
//      draw(x1024(832.0F), y1024(364.0F), x1024(48.0F), y1024(32.0F), 1, "50");
//      draw(x1024(368.0F), y1024(402.0F), x1024(208.0F), y1024(32.0F), 2, GUISetupInput.this.i18n("setupIn.Filtering"));
//      draw(x1024(576.0F), y1024(402.0F), x1024(48.0F), y1024(32.0F), 1, "0");
//      draw(x1024(832.0F), y1024(402.0F), x1024(48.0F), y1024(32.0F), 1, "1");
//      
////      if (GUISetupInput.this.sForceFeedback != null)
////        draw(x1024(136.0F), y1024(520.0F), x1024(208.0F), y1024(32.0F), 0, GUISetupInput.this.i18n("setupIn.ForceFeedback"));
////      if(sUseSmartAxis != null)
////          draw(x1024(436F), y1024(520F), x1024(208F), y1024(32F), 0, i18n("setupIn.UseSmartAxis"));
////      draw(x1024(416.0F), y1024(520.0F), x1024(336.0F), y1024(32.0F), 2, GUISetupInput.this.i18n("setupIn.MouseSensitivity"));
////      draw(x1024(96.0F), y1024(608.0F), x1024(128.0F), y1024(48.0F), 0, GUISetupInput.this.i18n("setupIn.Back"));
////      draw(x1024(304.0F), y1024(608.0F), x1024(160.0F), y1024(48.0F), 0, GUISetupInput.this.i18n("setupIn.Default"));
////      if(Main.stateStack().getPrevious() == null || Main.stateStack().getPrevious().id() != 20)
////      draw(x1024(528.0F), y1024(608.0F), x1024(272.0F), y1024(48.0F), 2, GUISetupInput.this.i18n("setupIn.ControlPanel"));
//      if(sForceFeedback.isVisible())
//          draw(x1024(106F), y1024(496F), x1024(288F), y1024(32F), 0, i18n("setupIn.ForceFeedback"));
//      if(sUseSmartAxis.isVisible())
//          draw(x1024(106F), y1024(538F), x1024(288F), y1024(32F), 0, i18n("setupIn.UseSmartAxis"));
//      draw(x1024(508F), y1024(520F), x1024(276F), y1024(32F), 2, i18n("setupIn.MouseSensitivity"));
//      draw(x1024(61F), y1024(608F), x1024(100F), y1024(48F), 0, i18n("setupIn.Back"));
//      draw(x1024(186F), y1024(608F), x1024(120F), y1024(48F), 0, i18n("setupIn.Default"));
//      draw(x1024(508F), y1024(608F), x1024(110F), y1024(48F), 0, i18n("setupIn.Refresh"));
//      if(Main.stateStack().getPrevious() == null || Main.stateStack().getPrevious().id() != 20)
//          draw(x1024(623F), y1024(608F), x1024(212F), y1024(48F), 2, i18n("setupIn.ControlPanel"));
//
//      draw(x1024(654.0F), y1024(62.0F), x1024(128.0F), y1024(48.0F), 1, GUISetupInput.this.i18n("setupIn.Load"));
//      draw(x1024(754.0F), y1024(62.0F), x1024(128.0F), y1024(48.0F), 1, GUISetupInput.this.i18n("setupIn.Save"));
//      draw(x1024(368.0F), y1024(444.0F), x1024(208.0F), y1024(32.0F), 2, GUISetupInput.this.i18n("setupIn.Symmetrical"));
//      boolean flag = true;
//      boolean flag1 = true;
//      boolean flag2 = true;
//      if(flag1)
//      {
//          draw(x1024(48F), y1024(80F), x1024(200F), y1024(32F), 2, i18n("setupIn.aileron"));
//          draw(x1024(48F), y1024(118F), x1024(200F), y1024(32F), 2, i18n("setupIn.elevator"));
//          wJoyX.setPosSize(x1024(260F), y1024(80F), x1024(64F), M(1.7F));
//          wJoyY.setPosSize(x1024(260F), y1024(118F), x1024(64F), M(1.7F));
//          wJoyX.bVisible = true;
//          wJoyY.bVisible = true;
//          wJoyX.setEnable(true);
//          wJoyY.setEnable(true);
//      } else
//      {
//          wJoyX.setValue("0", false);
//          wJoyX.setEnable(false);
//          wJoyX.bVisible = false;
//          wJoyY.setValue("0", false);
//          wJoyY.setEnable(false);
//          wJoyY.bVisible = false;
//          wJoyX.setPosSize(x1024(260F), y1024(-80F), x1024(64F), M(1.7F));
//          wJoyY.setPosSize(x1024(260F), y1024(-118F), x1024(64F), M(1.7F));
//      }
//      if(comboAxeREA[0] == -1)
//          flag = false;
//      if(flag)
//      {
//          draw(x1024(48F), y1024(156F), x1024(200F), y1024(32F), 2, i18n("setupIn.rudder"));
//          wJoyZ.bVisible = true;
//          wJoyZ.setEnable(true);
//          wJoyZ.setPosSize(x1024(260F), y1024(156F), x1024(64F), M(1.7F));
//      } else
//      {
//          wJoyZ.setValue("0", false);
//          wJoyZ.setEnable(false);
//          wJoyZ.bVisible = false;
//          wJoyZ.setPosSize(x1024(260F), y1024(-156F), x1024(64F), M(1.7F));
//      }
//      if(comboAxe.getValue().equals(i18n("setupIn.none")) && !flag && !flag1)
//      {
//          wJoy4.setValue("0", false);
//          wJoy4.setEnable(false);
//          wJoy4.bVisible = false;
//          wJoy4.setPosSize(x1024(260F), y1024(-194F), x1024(64F), M(1.7F));
//          return;
//      }
//      for(int i = 0; i < 3; i++)
//          if(comboAxe.getSelected() == comboAxeREA[i])
//              flag2 = false;
//
//      if(flag2)
//      {
//          draw(x1024(48F), y1024(194F), x1024(200F), y1024(32F), 2, comboAxe.getValue());
//          wJoy4.bVisible = true;
//          wJoy4.setEnable(true);
//          wJoy4.setPosSize(x1024(260F), y1024(194F), x1024(64F), M(1.7F));
//      } else
//      {
//          wJoy4.setValue("0", false);
//          wJoy4.setEnable(false);
//          wJoy4.bVisible = false;
//          wJoy4.setPosSize(x1024(260F), y1024(-194F), x1024(64F), M(1.7F));
//      }
//      GUILookAndFeel localGUILookAndFeel = (GUILookAndFeel)lookAndFeel();
//      GBevel localGBevel = localGUILookAndFeel.bevelComboDown;
//      if (flag2) {
//        float f1 = localGBevel.L.dx;
//        float f2 = x1024(32.0F);
//        float f3 = y1024(208.0F) / 2.0F - f1;
//        float f4 = y1024(16.0F);
//        int i4 = GUISetupInput.this.curAxe();
//        int i5 = (i4 & 0xFFFF) - 716;
//        int i6 = (i4 >> 16) - 535;
//        Joy.adapter().getNotFilteredPos(i5, this.joyRawPos);
//        Joy.adapter().getPos(i5, this.joyPos);
//        float f8 = Joy.normal(this.joyRawPos[i6]);
//        float f9 = Joy.normal(this.joyPos[i6]);
//        wJoy4.setValue("" + joyRawPos[i6], false);
//        setCanvasColorWHITE();
//        localGUILookAndFeel.drawBevel(this, x1024(272.0F), y1024(240.0F), f2, y1024(208.0F), localGBevel, localGUILookAndFeel.basicelements);
//        if (GUISetupInput.this.js[12] == 0) {
//          f3 -= f4 / 2.0F;
//          if (GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxe.getSelected()] == -1.0D) {
//            f8 = -f8;
//            f9 = -f9;
//          }
//          GUISeparate.draw(this, GColor.Black, x1024(272.0F) + 2.0F * f1, y1024(344.0F), x1024(32.0F) - 4.0F * f1, 1.0F);
//          GUISeparate.draw(this, GColor.Red, x1024(272.0F) + 2.0F * f1, y1024(448.0F) - (f8 + 1.0F) * f3 - f1 - f4, f2 - 4.0F * f1, f4);
//          GUISeparate.draw(this, GColor.Green, x1024(272.0F) + 2.0F * f1, y1024(448.0F) - (f9 + 1.0F) * f3 - f1 - f4, f2 - 4.0F * f1, f4);
//        }
//        else {
//          if (GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxe.getSelected()] == -1.0D) {
//            f8 = -f8;
//            f9 = -f9;
//          }
//          GUISeparate.draw(this, GColor.Red, x1024(272.0F) + f1, y1024(448.0F) - (f8 + 1.0F) * f3 - f1, x1024(32.0F) - 2.0F * f1, (f8 + 1.0F) * f3);
//          GUISeparate.draw(this, GColor.Green, x1024(272.0F) + 2.0F * f1, y1024(448.0F) - (f9 + 1.0F) * f3 - f1, x1024(32.0F) - 4.0F * f1, (f9 + 1.0F) * f3);
//        }
//      }
//      if (!flag1) {
//        return;
//      }
//      int n = (GUISetupInput.this.comboAxeInts[GUISetupInput.this.comboAxeREA[1]] & 0xFFFF) - 716;
//      int i1 = (GUISetupInput.this.comboAxeInts[GUISetupInput.this.comboAxeREA[1]] >> 16) - 535;
//      int i2 = (GUISetupInput.this.comboAxeInts[GUISetupInput.this.comboAxeREA[2]] & 0xFFFF) - 716;
//      int i3 = (GUISetupInput.this.comboAxeInts[GUISetupInput.this.comboAxeREA[2]] >> 16) - 535;
//      Joy.adapter().getNotFilteredPos(n, this.joyRawPos);
//      Joy.adapter().getPos(n, this.joyPos);
//      float f5 = Joy.normal(GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxeREA[1]] * this.joyPos[i1]);
//      float f6 = Joy.normal(GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxeREA[1]] * this.joyRawPos[i1]);
//      wJoyY.setValue("" + joyRawPos[i1], false);
//      Joy.adapter().getNotFilteredPos(i2, this.joyRawPos);
//      Joy.adapter().getPos(i2, this.joyPos);
//      float f7 = Joy.normal(GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxeREA[2]] * this.joyPos[i3]);
//      float f8 = Joy.normal(GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxeREA[2]] * this.joyRawPos[i3]);
//      wJoyX.setValue("" + joyRawPos[i3], false);
//      float f9 = localGBevel.L.dx;
//      float f10 = x1024(16.0F);
//      setCanvasColorWHITE();
//      localGUILookAndFeel.drawBevel(this, x1024(64.0F), y1024(240.0F), x1024(208.0F), y1024(208.0F), localGBevel, localGUILookAndFeel.basicelements);
//      GUISeparate.draw(this, GColor.Black, x1024(64.0F) + 2.0F * f9, y1024(344.0F), x1024(208.0F) - 4.0F * f9, 1.0F);
//      GUISeparate.draw(this, GColor.Black, x1024(168.0F), y1024(240.0F) + 2.0F * f9, 1.0F, y1024(208.0F) - 4.0F * f9);
//      float f11 = (x1024(208.0F) - f10) / 2.0F - f9;
//      float f12 = x1024(168.0F) + f8 * f11;
//      float f13 = y1024(344.0F) + f6 * f11;
//      GUISeparate.draw(this, GColor.Red, f12 - f10 / 2.0F, f13 - f10 / 2.0F, f10, f10);
//      f12 = x1024(168.0F) + f7 * f11;
//      f13 = y1024(344.0F) + f5 * f11;
//      GUISeparate.draw(this, GColor.Green, f12 - f10 / 2.0F, f13 - f10 / 2.0F, f10, f10);
//      if (!flag) {
//        return;
//      }
//      int i7 = (GUISetupInput.this.comboAxeInts[GUISetupInput.this.comboAxeREA[0]] & 0xFFFF) - 716;
//      int i8 = (GUISetupInput.this.comboAxeInts[GUISetupInput.this.comboAxeREA[0]] >> 16) - 535;
//      Joy.adapter().getNotFilteredPos(i7, this.joyRawPos);
//      Joy.adapter().getPos(i7, this.joyPos);
//      float f14 = Joy.normal(GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxeREA[0]] * this.joyRawPos[i8]);
//      float f15 = Joy.normal(GUISetupInput.this.comboAxeReversed[GUISetupInput.this.comboAxeREA[0]] * this.joyPos[i8]);
//      wJoyZ.setValue("" + joyRawPos[i8], false);
//      setCanvasColorWHITE();
//      localGUILookAndFeel.drawBevel(this, x1024(64.0F), y1024(448.0F), x1024(208.0F), y1024(32.0F), localGBevel, localGUILookAndFeel.basicelements);
//      GUISeparate.draw(this, GColor.Black, x1024(168.0F), y1024(448.0F) + 2.0F * f9, 1.0F, y1024(32.0F) - 4.0F * f9);
//      float f16 = x1024(168.0F) + f14 * f11;
//      GUISeparate.draw(this, GColor.Red, f16 - f10 / 2.0F, y1024(448.0F) + 2.0F * f9, f10, y1024(32.0F) - 4.0F * f9);
//      f16 = x1024(168.0F) + f15 * f11;
//      GUISeparate.draw(this, GColor.Green, f16 - f10 / 2.0F, y1024(448.0F) + 2.0F * f9, f10, y1024(32.0F) - 4.0F * f9);
//    }
//
//    public void setPosSize()
//    {
//      set1024PosSize(64.0F, 48.0F, 896.0F, 688.0F);
//      comboAxe.set1024PosSize(384F, 80F, 244F, 32F);
//      float f1 = lookAndFeel().getVSliderIntW();
//      for (int i = 0; i < 10; i++) {
//        GUISetupInput.this.slider[i].setPosSize(x1024(384 + i * 48) + (x1024(48.0F) - f1) / 2.0F, y1024(128.0F), f1, y1024(176.0F));
//        GUISetupInput.this.edit[i].set1024PosSize(384 + i * 48, 320.0F, 48.0F, 32.0F);
//      }
//      GUISetupInput.this.wJoyProfile.setPosSize(x1024(500.0F), y1024(32.0F), x1024(160.0F), M(1.7F));
//      GUISetupInput.this.bLoad.setPosC(x1024(720.0F), y1024(50.0F));
//      GUISetupInput.this.bSave.setPosC(x1024(820.0F), y1024(50.0F));
//      GUISetupInput.this.sMirrorControl.setPosC(x1024(672.0F), y1024(460.0F));
//      float f2 = lookAndFeel().getHSliderIntH();
//      GUISetupInput.this.deadSlider.setPosSize(x1024(624.0F), y1024(364.0F) + (y1024(32.0F) - f2) / 2.0F, x1024(208.0F), f2);
//      GUISetupInput.this.filterSlider.setPosSize(x1024(624.0F), y1024(402.0F) + (y1024(32.0F) - f2) / 2.0F, x1024(208.0F), f2);
//      float f3 = 16F;
//      float f4 = root.win.dx / root.win.dy;
//      if(f4 < 1.0F)
//          f3 *= 1.5F;
//      bJoyInfo.setPosC(x1024(316F), y1024(632F));
//      wMouse.setPosSize(x1024(800F), y1024(504F + f3), x1024(64F), M(1.7F));
//      GUISetupInput.this.bBack.setPosC(x1024(36.0F), y1024(632.0F));
//      GUISetupInput.this.bDefault.setPosC(x1024(161.0F), y1024(632.0F));
//      bRefresh.setPosC(x1024(483F), y1024(632F));
//      GUISetupInput.this.bControlPanel.setPosC(x1024(860.0F), y1024(632.0F));
//        GUISetupInput.this.sForceFeedback.setPosC(x1024(86.0F), y1024(510.0F));
//          sUseSmartAxis.setPosC(x1024(86F), y1024(552F));
//    }
//
//    public DialogClient()
//    {
//      this.joyPos = new int[8];
//      this.joyRawPos = new int[8];
//    }
//  }
//}



package com.maddox.il2.gui;

import java.util.ResourceBundle;

import com.maddox.gwindow.GBevel;
import com.maddox.gwindow.GColor;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowComboControl;
import com.maddox.gwindow.GWindowEditControl;
import com.maddox.gwindow.GWindowHSliderInt;
import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.gwindow.GWindowRoot;
import com.maddox.gwindow.GWindowVSliderInt;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.game.GameState;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.effects.ForceFeedback;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Joy;
import com.maddox.rts.JoyFF;
import com.maddox.rts.LDRres;
import com.maddox.rts.Mouse;
import com.maddox.rts.RTSConf;
import com.maddox.rts.VK;
import com.maddox.util.HashMapInt;
import com.maddox.util.HashMapIntEntry;

public class GUISetupInput extends GameState
{
    public class DialogClient extends GUIDialogClient
    {

        public boolean notify(GWindow gwindow, int i, int j)
        {
            if(i != 2)
                return super.notify(gwindow, i, j);
            if(gwindow == bControlPanel)
            {
                if(wJoyProfile.getSelected() > 0)
                    Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick" + wJoyProfile.getSelected());
                else
                    Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick");
                Main.stateStack().push(20);
                GUIControls guicontrols = (GUIControls)Main.stateStack().peek();
                guicontrols.scrollClient.vScroll.setPos(guicontrols.scrollClient.vScroll.posMax, true);
                return true;
            }
            if(gwindow == sMirrorControl)
            {
                if(js[12] == 0)
                {
                    if((double)comboAxeReversed[comboAxe.getSelected()] == -1D)
                        js[12] = 2;
                    else
                        js[12] = 1;
                } else
                {
                    js[12] = 0;
                }
                setJoyS(12, js[12]);
                setPosSize();
                return true;
            }
            if(gwindow == bJoyInfo)
            {
                String s = i18n("setupIn.Discovered") + "\n";
                for(int k = 0; k < Joy.adapter().getAmount(); k++)
                    s = s + i18n("setupIn.JD" + k) + " " + Joy.adapter().getJoyName(k) + "\n";

                new GWindowMessageBox(this, 20F, true, i18n("setupIn.JoyInfo"), s, 3, 0.0F);
                return true;
            }
            if(gwindow == bSave)
            {
                Mouse.adapter().saveConfig(Config.cur.ini, "rts_mouse");
                if(sForceFeedback.isChecked())
                {
                    if(!JoyFF.isStarted())
                    {
                        JoyFF.setEnable(true);
                        ForceFeedback.start();
                        if(Mission.isPlaying())
                            ForceFeedback.startMission();
                    }
                } else
                if(JoyFF.isStarted())
                {
                    ForceFeedback.stop();
                    JoyFF.setEnable(false);
                }
                if(wJoyProfile.getSelected() > 0)
                    Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick" + wJoyProfile.getSelected());
                else
                    Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick");
                return true;
            }
            if(gwindow == bLoad)
            {
                if(wJoyProfile.getSelected() > 0)
                    Joy.adapter().loadConfig(Config.cur.ini, "rts_joystick" + wJoyProfile.getSelected());
                else
                    Joy.adapter().loadConfig(Config.cur.ini, "rts_joystick");
                fillDialogs();
                return true;
            }
            if(gwindow == comboAxe)
            {
                fillSliders(curAxe());
                return true;
            }
            if(gwindow == slider[0])
            {
                setJoyS(1, slider[0].pos());
                return true;
            }
            if(gwindow == slider[1])
            {
                setJoyS(2, slider[1].pos());
                return true;
            }
            if(gwindow == slider[2])
            {
                setJoyS(3, slider[2].pos());
                return true;
            }
            if(gwindow == slider[3])
            {
                setJoyS(4, slider[3].pos());
                return true;
            }
            if(gwindow == slider[4])
            {
                setJoyS(5, slider[4].pos());
                return true;
            }
            if(gwindow == slider[5])
            {
                setJoyS(6, slider[5].pos());
                return true;
            }
            if(gwindow == slider[6])
            {
                setJoyS(7, slider[6].pos());
                return true;
            }
            if(gwindow == slider[7])
            {
                setJoyS(8, slider[7].pos());
                return true;
            }
            if(gwindow == slider[8])
            {
                setJoyS(9, slider[8].pos());
                return true;
            }
            if(gwindow == slider[9])
            {
                setJoyS(10, slider[9].pos());
                return true;
            }
            if(gwindow == deadSlider)
            {
                setJoyS(0, deadSlider.pos());
                return true;
            }
            if(gwindow == filterSlider)
            {
                setJoyS(11, (100 * filterSlider.pos()) / 10);
                return true;
            }
            if(gwindow == edit[0])
            {
                setJoyS(1, clampValue(edit[0], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[1])
            {
                setJoyS(2, clampValue(edit[1], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[2])
            {
                setJoyS(3, clampValue(edit[2], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[3])
            {
                setJoyS(4, clampValue(edit[3], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[4])
            {
                setJoyS(5, clampValue(edit[4], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[5])
            {
                setJoyS(6, clampValue(edit[5], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[6])
            {
                setJoyS(7, clampValue(edit[6], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[7])
            {
                setJoyS(8, clampValue(edit[7], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[8])
            {
                setJoyS(9, clampValue(edit[8], 0, 0, 100));
                return true;
            }
            if(gwindow == edit[9])
            {
                setJoyS(10, clampValue(edit[9], 0, 0, 100));
                return true;
            }
            if(gwindow == wMouse)
            {
                float f = clampValue(wMouse, 1.0F, 0.1F, 10F);
                float af[] = Mouse.adapter().getSensitivity();
                af[0] = af[1] = f;
                wMouse.setValue("" + f, false);
                return true;
            }
            if(gwindow == bDefault)
            {
                doSetDefault();
                return true;
            }
            if(gwindow == bRefresh)
            {
                RTSConf.cur.stop();
                RTSConf.cur.start();
                ForceFeedback.stopMission();
                ForceFeedback.stop();
                JoyFF.reattach();
                if(JoyFF.isEnable() && !JoyFF.isStarted())
                {
                    ForceFeedback.start();
                    if(Mission.isPlaying())
                        ForceFeedback.startMission();
                }
                _enter();
                return true;
            }
            if(gwindow == bBack)
            {
                Mouse.adapter().saveConfig(Config.cur.ini, "rts_mouse");
                World.cur().useSmartAxis = sUseSmartAxis.isChecked();
                if(sForceFeedback.isChecked())
                {
                    if(!JoyFF.isStarted())
                    {
                        JoyFF.setEnable(true);
                        ForceFeedback.start();
                        if(Mission.isPlaying())
                            ForceFeedback.startMission();
                    }
                } else
                if(JoyFF.isStarted())
                {
                    ForceFeedback.stop();
                    JoyFF.setEnable(false);
                }
                Config.cur.ini.set("rts", "JoyProfile", wJoyProfile.getSelected());
                if(wJoyProfile.getSelected() > 0)
                    Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick" + wJoyProfile.getSelected());
                else
                    Joy.adapter().saveConfig(Config.cur.ini, "rts_joystick");
                Main.stateStack().pop();
                return true;
            } else
            {
                return super.notify(gwindow, i, j);
            }
        }

        public void render()
        {
            super.render();
            GUISeparate.draw(this, GColor.Gray, x1024(256F), y1024(48F), x1024(160F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, x1024(336F), y1024(490F), x1024(528F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, x1024(32F), y1024(576F), x1024(832F), 2.0F);
            GUISeparate.draw(this, GColor.Gray, x1024(336F), y1024(48F), 2.0F, y1024(444F));
            setCanvasColor(GColor.Gray);
            setCanvasFont(0);
            draw(x1024(48F), y1024(32F), x1024(192F), y1024(32F), 2, i18n("setupIn.JoystickAxes"));
            draw(x1024(432F), y1024(32F), x1024(208F), y1024(32F), 0, i18n("setupIn.Profile"));
            draw(x1024(341F), y1024(608F), x1024(122F), y1024(48F), 0, i18n("setupIn.JoyInfo"));
            draw(x1024(368F), y1024(364F), x1024(208F), y1024(32F), 2, i18n("setupIn.DeadBand"));
            draw(x1024(576F), y1024(364F), x1024(48F), y1024(32F), 1, "0");
            draw(x1024(832F), y1024(364F), x1024(48F), y1024(32F), 1, "50");
            draw(x1024(368F), y1024(402F), x1024(208F), y1024(32F), 2, i18n("setupIn.Filtering"));
            draw(x1024(576F), y1024(402F), x1024(48F), y1024(32F), 1, "0");
            draw(x1024(832F), y1024(402F), x1024(48F), y1024(32F), 1, "1");
            if(sForceFeedback.isVisible())
                draw(x1024(106F), y1024(496F), x1024(288F), y1024(32F), 0, i18n("setupIn.ForceFeedback"));
            if(sUseSmartAxis.isVisible())
                draw(x1024(106F), y1024(538F), x1024(288F), y1024(32F), 0, i18n("setupIn.UseSmartAxis"));
            draw(x1024(508F), y1024(520F), x1024(276F), y1024(32F), 2, i18n("setupIn.MouseSensitivity"));
            draw(x1024(61F), y1024(608F), x1024(100F), y1024(48F), 0, i18n("setupIn.Back"));
            draw(x1024(186F), y1024(608F), x1024(120F), y1024(48F), 0, i18n("setupIn.Default"));
            draw(x1024(508F), y1024(608F), x1024(110F), y1024(48F), 0, i18n("setupIn.Refresh"));
            if(Main.stateStack().getPrevious() == null || Main.stateStack().getPrevious().id() != 20)
                draw(x1024(623F), y1024(608F), x1024(212F), y1024(48F), 2, i18n("setupIn.ControlPanel"));
            draw(x1024(654F), y1024(62F), x1024(128F), y1024(48F), 1, i18n("setupIn.Load"));
            draw(x1024(754F), y1024(62F), x1024(128F), y1024(48F), 1, i18n("setupIn.Save"));
            draw(x1024(368F), y1024(444F), x1024(208F), y1024(32F), 2, i18n("setupIn.Symmetrical"));
            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            if(comboAxeREA[1] == -1 || comboAxeREA[2] == -1)
                flag1 = false;
            if(flag1)
            {
                draw(x1024(48F), y1024(80F), x1024(200F), y1024(32F), 2, i18n("setupIn.aileron"));
                draw(x1024(48F), y1024(118F), x1024(200F), y1024(32F), 2, i18n("setupIn.elevator"));
                wJoyX.setPosSize(x1024(260F), y1024(80F), x1024(64F), M(1.7F));
                wJoyY.setPosSize(x1024(260F), y1024(118F), x1024(64F), M(1.7F));
                wJoyX.bVisible = true;
                wJoyY.bVisible = true;
                wJoyX.setEnable(true);
                wJoyY.setEnable(true);
            } else
            {
                wJoyX.setValue("0", false);
                wJoyX.setEnable(false);
                wJoyX.bVisible = false;
                wJoyY.setValue("0", false);
                wJoyY.setEnable(false);
                wJoyY.bVisible = false;
                wJoyX.setPosSize(x1024(260F), y1024(-80F), x1024(64F), M(1.7F));
                wJoyY.setPosSize(x1024(260F), y1024(-118F), x1024(64F), M(1.7F));
            }
            if(comboAxeREA[0] == -1)
                flag = false;
            if(flag)
            {
                draw(x1024(48F), y1024(156F), x1024(200F), y1024(32F), 2, i18n("setupIn.rudder"));
                wJoyZ.bVisible = true;
                wJoyZ.setEnable(true);
                wJoyZ.setPosSize(x1024(260F), y1024(156F), x1024(64F), M(1.7F));
            } else
            {
                wJoyZ.setValue("0", false);
                wJoyZ.setEnable(false);
                wJoyZ.bVisible = false;
                wJoyZ.setPosSize(x1024(260F), y1024(-156F), x1024(64F), M(1.7F));
            }
            if(comboAxe.getValue().equals(i18n("setupIn.none")) && !flag && !flag1)
            {
                wJoy4.setValue("0", false);
                wJoy4.setEnable(false);
                wJoy4.bVisible = false;
                wJoy4.setPosSize(x1024(260F), y1024(-194F), x1024(64F), M(1.7F));
                return;
            }
            for(int i = 0; i < 3; i++)
                if(comboAxe.getSelected() == comboAxeREA[i])
                    flag2 = false;

            if(flag2)
            {
                draw(x1024(48F), y1024(194F), x1024(200F), y1024(32F), 2, comboAxe.getValue());
                wJoy4.bVisible = true;
                wJoy4.setEnable(true);
                wJoy4.setPosSize(x1024(260F), y1024(194F), x1024(64F), M(1.7F));
            } else
            {
                wJoy4.setValue("0", false);
                wJoy4.setEnable(false);
                wJoy4.bVisible = false;
                wJoy4.setPosSize(x1024(260F), y1024(-194F), x1024(64F), M(1.7F));
            }
            GUILookAndFeel guilookandfeel = (GUILookAndFeel)lookAndFeel();
            GBevel gbevel = guilookandfeel.bevelComboDown;
            if(flag2)
            {
                float f = gbevel.L.dx;
                float f1 = x1024g(32F);
                float f2 = y1024g(208F) / 2.0F - f;
                float f3 = y1024g(16F);
                int j1 = curAxe();
                int k1 = (j1 & 0xffff) - 716;
                int l1 = (j1 >> 16) - 535;
                Joy.adapter().getNotFilteredPos(k1, joyRawPos);
                Joy.adapter().getPos(k1, joyPos);
                float f7 = Joy.normal(joyRawPos[l1]);
                float f9 = Joy.normal(joyPos[l1]);
                wJoy4.setValue("" + joyRawPos[l1], false);
                setCanvasColorWHITE();
                guilookandfeel.drawBevel(this, x1024g(272F), y1024g(240F), f1, y1024g(208F), gbevel, guilookandfeel.basicelements);
                if(js[12] == 0)
                {
                    f2 -= f3 / 2.0F;
                    if((double)comboAxeReversed[comboAxe.getSelected()] == -1D)
                    {
                        f7 = -f7;
                        f9 = -f9;
                    }
                    GUISeparate.draw(this, GColor.Black, x1024g(272F) + 2.0F * f, y1024g(344F), x1024g(32F) - 4F * f, 1.0F);
                    GUISeparate.draw(this, GColor.Red, x1024g(272F) + 2.0F * f, y1024g(448F) - (f7 + 1.0F) * f2 - f - f3, f1 - 4F * f, f3);
                    GUISeparate.draw(this, GColor.Green, x1024g(272F) + 2.0F * f, y1024g(448F) - (f9 + 1.0F) * f2 - f - f3, f1 - 4F * f, f3);
                } else
                {
                    if((double)comboAxeReversed[comboAxe.getSelected()] == -1D)
                    {
                        f7 = -f7;
                        f9 = -f9;
                    }
                    GUISeparate.draw(this, GColor.Red, x1024g(272F) + f, y1024g(448F) - (f7 + 1.0F) * f2 - f, x1024g(32F) - 2.0F * f, (f7 + 1.0F) * f2);
                    GUISeparate.draw(this, GColor.Green, x1024g(272F) + 2.0F * f, y1024g(448F) - (f9 + 1.0F) * f2 - f, x1024g(32F) - 4F * f, (f9 + 1.0F) * f2);
                }
            }
            if(!flag1)
                return;
            int j = (comboAxeInts[comboAxeREA[1]] & 0xffff) - 716;
            int k = (comboAxeInts[comboAxeREA[1]] >> 16) - 535;
            int l = (comboAxeInts[comboAxeREA[2]] & 0xffff) - 716;
            int i1 = (comboAxeInts[comboAxeREA[2]] >> 16) - 535;
            Joy.adapter().getNotFilteredPos(j, joyRawPos);
            Joy.adapter().getPos(j, joyPos);
            float f4 = Joy.normal(comboAxeReversed[comboAxeREA[1]] * joyPos[k]);
            float f5 = Joy.normal(comboAxeReversed[comboAxeREA[1]] * joyRawPos[k]);
            wJoyY.setValue("" + joyRawPos[k], false);
            Joy.adapter().getNotFilteredPos(l, joyRawPos);
            Joy.adapter().getPos(l, joyPos);
            float f6 = Joy.normal(comboAxeReversed[comboAxeREA[2]] * joyPos[i1]);
            float f8 = Joy.normal(comboAxeReversed[comboAxeREA[2]] * joyRawPos[i1]);
            wJoyX.setValue("" + joyRawPos[i1], false);
            float f10 = gbevel.L.dx;
            float f11 = x1024g(16F);
            setCanvasColorWHITE();
            guilookandfeel.drawBevel(this, x1024g(64F), y1024g(240F), x1024g(208F), y1024g(208F), gbevel, guilookandfeel.basicelements);
            GUISeparate.draw(this, GColor.Black, x1024g(64F) + 2.0F * f10, y1024g(344F), x1024g(208F) - 4F * f10, 1.0F);
            GUISeparate.draw(this, GColor.Black, x1024g(168F), y1024g(240F) + 2.0F * f10, 1.0F, y1024g(208F) - 4F * f10);
            float f12 = (x1024g(208F) - f11) / 2.0F - f10;
            float f13 = x1024g(168F) + f8 * f12;
            float f14 = y1024g(344F) + f5 * f12;
            GUISeparate.draw(this, GColor.Red, f13 - f11 / 2.0F, f14 - f11 / 2.0F, f11, f11);
            f13 = x1024g(168F) + f6 * f12;
            f14 = y1024g(344F) + f4 * f12;
            GUISeparate.draw(this, GColor.Green, f13 - f11 / 2.0F, f14 - f11 / 2.0F, f11, f11);
            if(!flag)
            {
                return;
            } else
            {
                int i2 = (comboAxeInts[comboAxeREA[0]] & 0xffff) - 716;
                int j2 = (comboAxeInts[comboAxeREA[0]] >> 16) - 535;
                Joy.adapter().getNotFilteredPos(i2, joyRawPos);
                Joy.adapter().getPos(i2, joyPos);
                float f15 = Joy.normal(comboAxeReversed[comboAxeREA[0]] * joyRawPos[j2]);
                float f16 = Joy.normal(comboAxeReversed[comboAxeREA[0]] * joyPos[j2]);
                wJoyZ.setValue("" + joyRawPos[j2], false);
                setCanvasColorWHITE();
                guilookandfeel.drawBevel(this, x1024g(64F), y1024g(448F), x1024g(208F), y1024g(32F), gbevel, guilookandfeel.basicelements);
                GUISeparate.draw(this, GColor.Black, x1024g(168F), y1024g(448F) + 2.0F * f10, 1.0F, y1024g(32F) - 4F * f10);
                float f17 = x1024g(168F) + f15 * f12;
                GUISeparate.draw(this, GColor.Red, f17 - f11 / 2.0F, y1024g(448F) + 2.0F * f10, f11, y1024g(32F) - 4F * f10);
                f17 = x1024g(168F) + f16 * f12;
                GUISeparate.draw(this, GColor.Green, f17 - f11 / 2.0F, y1024g(448F) + 2.0F * f10, f11, y1024g(32F) - 4F * f10);
                return;
            }
        }

        public void setPosSize()
        {
            set1024PosSize(64F, 48F, 896F, 688F);
            comboAxe.set1024PosSize(384F, 80F, 244F, 32F);
            float f = lookAndFeel().getVSliderIntW();
            for(int i = 0; i < 10; i++)
            {
                slider[i].setPosSize(x1024(384 + i * 48) + (x1024(48F) - f) / 2.0F, y1024(128F), f, y1024(176F));
                edit[i].set1024PosSize(384 + i * 48, 320F, 48F, 32F);
            }

            wJoyProfile.setPosSize(x1024(500F), y1024(32F), x1024(160F), M(1.7F));
            bLoad.setPosC(x1024(720F), y1024(50F));
            bSave.setPosC(x1024(820F), y1024(50F));
            sMirrorControl.setPosC(x1024(672F), y1024(460F));
            float f1 = lookAndFeel().getHSliderIntH();
            deadSlider.setPosSize(x1024(624F), y1024(364F) + (y1024(32F) - f1) / 2.0F, x1024(208F), f1);
            filterSlider.setPosSize(x1024(624F), y1024(402F) + (y1024(32F) - f1) / 2.0F, x1024(208F), f1);
            float f2 = 16F;
            float f3 = root.win.dx / root.win.dy;
            if(f3 < 1.0F)
                f2 *= 1.5F;
            bJoyInfo.setPosC(x1024(316F), y1024(632F));
            wMouse.setPosSize(x1024(800F), y1024(504F + f2), x1024(64F), M(1.7F));
            bBack.setPosC(x1024(36F), y1024(632F));
            bDefault.setPosC(x1024(161F), y1024(632F));
            bRefresh.setPosC(x1024(483F), y1024(632F));
            bControlPanel.setPosC(x1024(860F), y1024(632F));
            sForceFeedback.setPosC(x1024(86F), y1024(510F));
            sUseSmartAxis.setPosC(x1024(86F), y1024(552F));
        }

        private int joyPos[];
        private int joyRawPos[];

        public DialogClient()
        {
            joyPos = new int[8];
            joyRawPos = new int[8];
        }
    }


    public void _enter()
    {
        fillDialogs();
        if(JoyFF.isAttached())
        {
            sForceFeedback.setEnable(true);
            sForceFeedback.showWindow();
            sUseSmartAxis.setEnable(true);
            sUseSmartAxis.showWindow();
            sForceFeedback.setChecked(JoyFF.isStarted(), false);
            sUseSmartAxis.setChecked(World.cur().useSmartAxis, false);
        } else
        {
            sForceFeedback.setEnable(false);
            sForceFeedback.hideWindow();
            sUseSmartAxis.setEnable(false);
            sUseSmartAxis.hideWindow();
        }
        client.activateWindow();
        if(Main.stateStack().getPrevious() != null && Main.stateStack().getPrevious().id() == 20)
        {
            bControlPanel.setEnable(false);
            bControlPanel.hideWindow();
        } else
        {
            bControlPanel.setEnable(true);
            bControlPanel.showWindow();
        }
    }

    public void _leave()
    {
        client.hideWindow();
    }

    private void fillDialogs()
    {
        for(int i = 0; i < 3; i++)
            comboAxeREA[i] = -1;

        for(int j = 0; j < 64; j++)
            comboAxeReversed[j] = 1;

        int k = 0;
        int l = comboAxe.getSelected();
        comboAxe.clear(false);
        String as[] = UserCfg.nameHotKeyEnvs;
        for(int i1 = 0; i1 < as.length; i1++)
            if(as[i1].equals("move"))
            {
                HotKeyEnv hotkeyenv = HotKeyEnv.env(as[i1]);
                HashMapInt hashmapint = hotkeyenv.all();
                for(HashMapIntEntry hashmapintentry = hashmapint.nextEntry(null); hashmapintentry != null; hashmapintentry = hashmapint.nextEntry(hashmapintentry))
                {
                    comboAxeInts[k] = hashmapintentry.getKey();
                    String s = (String)hashmapintentry.getValue();
                    if(s.startsWith("-"))
                    {
                        s = s.substring(1);
                        comboAxeReversed[k] = -1;
                    }
                    String s1;
                    try
                    {
                        s1 = ResourceBundle.getBundle("i18n/controls", RTSConf.cur.locale, LDRres.loader()).getString(s);
                    }
                    catch(Exception exception)
                    {
                        s1 = s;
                        System.out.println("Warning: Control" + s + "is not present in i18n/controls.properties file");
                    }
                    int l1 = (comboAxeInts[k] & 0xffff) - 716;
                    int i2 = (comboAxeInts[k] >> 16) - 535;
                    if(VK.getKeyText(comboAxeInts[k] >> 16 & 0xffff).startsWith("Joystick"))
                    {
                        comboAxeInts[k] = (comboAxeInts[k] >> 16) + ((comboAxeInts[k] & 0xffff) << 16);
                        l1 = (comboAxeInts[k] & 0xffff) - 716;
                        i2 = (comboAxeInts[k] >> 16) - 535;
                    }
                    if(Joy.adapter().isExistAxe(l1, i2))
                    {
                        comboAxe.add(s1);
                        if(s.equals("rudder"))
                            comboAxeREA[0] = k;
                        else
                        if(s.equals("elevator"))
                            comboAxeREA[1] = k;
                        else
                        if(s.equals("aileron"))
                            comboAxeREA[2] = k;
                        k++;
                    }
                }

            }

        if(k == 0)
        {
            sMirrorControl.setEnable(false);
            for(int j1 = 0; j1 < 9; j1++)
            {
                slider[j1].setEnable(false);
                edit[j1].setEnable(false);
            }

            deadSlider.setEnable(false);
            filterSlider.setEnable(false);
            comboAxe.add(i18n("setupIn.none"));
            comboAxe.setSelected(0, true, false);
            comboAxe.setEnable(false);
        } else
        {
            sMirrorControl.setEnable(true);
            for(int k1 = 0; k1 < 9; k1++)
            {
                slider[k1].setEnable(true);
                edit[k1].setEnable(true);
            }

            deadSlider.setEnable(true);
            filterSlider.setEnable(true);
            comboAxe.setEnable(true);
            if(l < 0 || l >= k)
                l = 0;
            comboAxe.setSelected(-1, false, false);
            comboAxe.setSelected(l, true, true);
        }
        float af[] = Mouse.adapter().getSensitivity();
        wMouse.setValue("" + af[0], false);
    }

    private int curAxe()
    {
        int i = comboAxe.getSelected();
        int j = comboAxeInts[i];
        if(i < 0)
            return -1;
        else
            return j;
    }

    private void fillSliders(int i)
    {
        if(i < 0)
            return;
        int j = (i & 0xffff) - 716;
        int k = (i >> 16) - 535;
        Joy.adapter().getSensitivity(j, k, js);
        if(js[12] == 0)
            sMirrorControl.setChecked(true, false);
        else
            sMirrorControl.setChecked(false, false);
        if(js[0] < 0)
            js[0] = 0;
        if(js[0] > 50)
            js[0] = 50;
        deadSlider.setPos(js[0], false);
        filterSlider.setPos((10 * js[11]) / 100, false);
        for(int l = 0; l < 10; l++)
        {
            if(js[l + 1] < 0)
                js[l + 1] = 0;
            if(js[l + 1] > 100)
                js[l + 1] = 100;
            slider[l].setPos(js[l + 1], false);
            edit[l].setValue("" + js[l + 1], false);
        }

    }

    private void doSetDefault()
    {
        js[0] = 0;
        for(int i = 1; i < 11; i++)
            js[i] = i * 10;

        js[11] = 0;
        js[12] = 0;
        for(int j = 0; j < 8; j++)
        {
            for(int k = 0; k < 8; k++)
                if(Joy.adapter().isExistAxe(j, k))
                    Joy.adapter().setSensitivity(j, k, js);

        }

        float af[] = Mouse.adapter().getSensitivity();
        af[0] = af[1] = 1.0F;
        fillDialogs();
    }

    private void setJoyS(int i, int j)
    {
        int k = curAxe();
        int l = (k & 0xffff) - 716;
        int i1 = (k >> 16) - 535;
        if(k < 0)
        {
            return;
        } else
        {
            Joy.adapter().getSensitivity(l, i1, js);
            js[i] = j;
            Joy.adapter().setSensitivity(l, i1, js);
            fillSliders(k);
            return;
        }
    }

    private float clampValue(GWindowEditControl gwindoweditcontrol, float f, float f1, float f2)
    {
        String s = gwindoweditcontrol.getValue();
        try
        {
            f = Float.parseFloat(s);
        }
        catch(Exception exception) { }
        if(f < f1)
            f = f1;
        if(f > f2)
            f = f2;
        gwindoweditcontrol.setValue("" + f, false);
        return f;
    }

    private int clampValue(GWindowEditControl gwindoweditcontrol, int i, int j, int k)
    {
        String s = gwindoweditcontrol.getValue();
        try
        {
            i = Integer.parseInt(s);
        }
        catch(Exception exception) { }
        if(i < j)
            i = j;
        if(i > k)
            i = k;
        gwindoweditcontrol.setValue("" + i, false);
        return i;
    }

    public GUISetupInput(GWindowRoot gwindowroot)
    {
        super(53);
        slider = new GWindowVSliderInt[10];
        edit = new GWindowEditControl[10];
        js = new int[13];
        client = (GUIClient)gwindowroot.create(new GUIClient());
        dialogClient = (DialogClient)client.create(new DialogClient());
        infoMenu = (GUIInfoMenu)client.create(new GUIInfoMenu());
        infoMenu.info = i18n("setupIn.info");
        infoName = (GUIInfoName)client.create(new GUIInfoName());
        com.maddox.gwindow.GTexture gtexture = ((GUILookAndFeel)gwindowroot.lookAndFeel()).buttons2;
        dialogClient.addControl(comboAxe = new GWindowComboControl(dialogClient, 0.0F, 0.0F, 1.0F));
        wJoyProfile = (GWindowComboControl)dialogClient.addControl(new GWindowComboControl(dialogClient, 2.0F, 2.0F, 20F + gwindowroot.lookAndFeel().getHScrollBarW() / gwindowroot.lookAndFeel().metric()));
        bLoad = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bSave = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        sMirrorControl = (GUISwitchBox3)dialogClient.addControl(new GUISwitchBox3(dialogClient));
        comboAxe.setEditable(false);
        comboAxe.resized();
        for(int i = 0; i < 10; i++)
        {
            dialogClient.addControl(slider[i] = new GWindowVSliderInt(dialogClient));
            slider[i].setRange(0, 101, 10 * (i + 1));
            edit[i] = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

                public void keyboardKey(int j, boolean flag)
                {
                    super.keyboardKey(j, flag);
                    if(j == 10 && flag)
                        notify(2, 0);
                }

            }
);
            edit[i].bSelectOnFocus = false;
            edit[i].bDelayedNotify = true;
            edit[i].bNumericOnly = true;
            edit[i].align = 1;
        }

        dialogClient.addControl(deadSlider = new GWindowHSliderInt(dialogClient));
        deadSlider.setRange(0, 51, 0);
        dialogClient.addControl(filterSlider = new GWindowHSliderInt(dialogClient));
        filterSlider.setRange(0, 11, 0);
        wMouse = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

            public void keyboardKey(int j, boolean flag)
            {
                super.keyboardKey(j, flag);
                if(j == 10 && flag)
                    notify(2, 0);
            }

        }
);
        wMouse.bNumericOnly = wMouse.bNumericFloat = true;
        wMouse.bDelayedNotify = true;
        wMouse.align = 1;
        wJoyProfile.add(i18n("setupIn.Joystick1"));
        wJoyProfile.add(i18n("setupIn.Joystick2"));
        wJoyProfile.add(i18n("setupIn.Joystick3"));
        wJoyProfile.add(i18n("setupIn.Joystick4"));
        wJoyProfile.add(i18n("setupIn.Joystick5"));
        wJoyProfile.add(i18n("setupIn.Joystick6"));
        wJoyProfile.add(i18n("setupIn.Joystick7"));
        wJoyProfile.add(i18n("setupIn.Joystick8"));
        wJoyProfile.setEditable(false);
        wJoyProfile.setSelected(Config.cur.ini.get("rts", "JoyProfile", 0), true, false);
        comboAxeInts = new int[64];
        comboAxeReversed = new int[64];
        bControlPanel = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bDefault = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bRefresh = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bBack = (GUIButton)dialogClient.addEscape(new GUIButton(dialogClient, gtexture, 0.0F, 96F, 48F, 48F));
        sForceFeedback = (GUISwitchBox3)dialogClient.addControl(new GUISwitchBox3(dialogClient));
        sUseSmartAxis = (GUISwitchBox3)dialogClient.addControl(new GUISwitchBox3(dialogClient));
        bJoyInfo = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        wJoyX = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

        }
);
        wJoyX.bCanEdit = false;
        wJoyX.bSelectOnFocus = false;
        wJoyX.bDelayedNotify = true;
        wJoyX.bNumericOnly = true;
        wJoyX.align = 1;
        wJoyX.bNotify = false;
        wJoyY = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

        }
);
        wJoyY.bCanEdit = false;
        wJoyY.bSelectOnFocus = false;
        wJoyY.bDelayedNotify = true;
        wJoyY.bNumericOnly = true;
        wJoyY.align = 1;
        wJoyY.bNotify = false;
        wJoyZ = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

        }
);
        wJoyZ.bCanEdit = false;
        wJoyZ.bSelectOnFocus = false;
        wJoyZ.bDelayedNotify = true;
        wJoyZ.bNumericOnly = true;
        wJoyZ.align = 1;
        wJoyZ.bNotify = false;
        wJoy4 = (GWindowEditControl)dialogClient.addControl(new GWindowEditControl(dialogClient, 0.0F, 0.0F, 1.0F, 2.0F, null) {

        }
);
        wJoy4.bCanEdit = false;
        wJoy4.bSelectOnFocus = false;
        wJoy4.bDelayedNotify = true;
        wJoy4.bNumericOnly = true;
        wJoy4.align = 1;
        wJoy4.bNotify = false;
        dialogClient.activateWindow();
        client.hideWindow();
    }

    public GWindowComboControl wJoyProfile;
    public GUIButton bLoad;
    public GUIButton bSave;
    public GUIButton bJoyInfo;
    public GWindowEditControl wJoyX;
    public GWindowEditControl wJoyY;
    public GWindowEditControl wJoyZ;
    public GWindowEditControl wJoy4;
    public GUISwitchBox3 sMirrorControl;
    public static final int MAX_DEAD_BAND = 50;
    public static final int MAX_FILTER = 10;
    public GUIClient client;
    public DialogClient dialogClient;
    public GUIInfoMenu infoMenu;
    public GUIInfoName infoName;
    public GWindowComboControl comboAxe;
    public GWindowVSliderInt slider[];
    public GWindowEditControl edit[];
    public GWindowHSliderInt deadSlider;
    public GWindowHSliderInt filterSlider;
    public GWindowEditControl wMouse;
    public GUIButton bBack;
    public GUIButton bDefault;
    public GUIButton bRefresh;
    public GUISwitchBox3 sForceFeedback;
    public GUISwitchBox3 sUseSmartAxis;
    public GUIButton bControlPanel;
    private int comboAxeReversed[];
    private int comboAxeInts[];
    private int comboAxeREA[] = {
        -1, -1, -1
    };
    public Joy joy;
    private int js[];
}

