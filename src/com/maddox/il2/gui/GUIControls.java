package com.maddox.il2.gui;

import com.maddox.gwindow.*;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.game.*;
import com.maddox.il2.objects.effects.ForceFeedback;
import com.maddox.rts.*;
import com.maddox.util.*;
import java.util.*;

public class GUIControls extends GameState
{
    public class DialogClient extends GUIDialogClient
    {

        public boolean notify(GWindow gwindow, int i, int j)
        {
            if(i != 2)
                return super.notify(gwindow, i, j);
            if(gwindow == bBack)
            {
                World.cur().userCfg.saveConf();
                Main.stateStack().pop();
                return true;
            }
            if(gwindow == bInput)
            {
                Main.stateStack().push(53);
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
            if(gwindow == bDefault)
            {
                new GWindowMessageBox(root, 20F, true, i18n("ctrl.Reset"), i18n("ctrl.ReallyDefault"), 1, 0.0F) {

                    public void result(int k)
                    {
                        if(k == 3)
                        {
                            IniFile inifile = new IniFile("users/default.ini", 0);
                            String as[] = UserCfg.nameHotKeyEnvs;
                            for(int l = 0; l < as.length; l++)
                            {
                                HotKeyEnv.env(as[l]).all().clear();
                                HotKeyEnv.fromIni(as[l], inifile, "HotKey " + as[l]);
                            }

                            fillItems();
                        } else
                        {
                            client.activateWindow();
                        }
                    }

                }
;
                return true;
            } else
            {
                return super.notify(gwindow, i, j);
            }
        }

        public void render()
        {
            super.render();
            GUISeparate.draw(this, GColor.Gray, x1024(32F), y1024(624F), x1024(962F), 2.0F);
            setCanvasColor(GColor.Gray);
            setCanvasFont(0);
            draw(x1024(90F), y1024(656F), x1024(100F), y1024(48F), 0, i18n("ctrl.Apply"));
            draw(x1024(270F), y1024(656F), x1024(220F), y1024(48F), 0, i18n("ctrl.Reset"));
            draw(x1024(570F), y1024(656F), x1024(170F), y1024(48F), 0, i18n("ctrl.Refresh"));
            if(Main.stateStack().getPrevious() == null || Main.stateStack().getPrevious().id() != 53)
                draw(x1024(754F), y1024(656F), x1024(170F), y1024(48F), 2, i18n("ctrl.Input"));
        }

        public void setPosSize()
        {
            set1024PosSize(0.0F, 32F, 1024F, 736F);
            scrollClient.set1024PosSize(32F, 32F, 962F, 562F);
            fixedClient.setSize(fixedClient.getMinSize().dx, fixedClient.getMinSize().dy);
            setPosItems(((scrollClient.win.dx - scrollClient.vScroll.win.dx) * 1024F) / root.win.dx);
            bBack.setPosC(x1024(50F), y1024(680F));
            bDefault.setPosC(x1024(230F), y1024(680F));
            bRefresh.setPosC(x1024(530F), y1024(680F));
            bInput.setPosC(x1024(964F), y1024(680F));
        }


        public DialogClient()
        {
        }
    }

    public class FixedClient extends GWindowDialogClient
    {

        public void render()
        {
        }

        public void mouseButton(int i, boolean flag, float f, float f1)
        {
            super.mouseButton(i, flag, f, f1);
            if(flag)
                setKeyFocus();
        }

        public boolean doNotify(int i, int j)
        {
            if(i == 17)
                return super.doNotify(i, j);
            else
                return false;
        }

        public GSize getMinSize(GSize gsize)
        {
            gsize.dx = scrollClient.win.dx - scrollClient.vScroll.win.dx - 1.0F;
//            GBevel gbevel = ((GUILookAndFeel)lookAndFeel()).bevelComboDown;
            gsize.dy = y1024(itemsDY_1024);
            return gsize;
        }

        public FixedClient()
        {
        }
    }

    public class ScrollClient extends GWindowScrollingDialogClient
    {

        public void created()
        {
            fixed = fixedClient = (FixedClient)create(new FixedClient());
            fixed.bNotify = true;
            bNotify = true;
        }

        public boolean notify(GWindow gwindow, int i, int j)
        {
            if(super.notify(gwindow, i, j))
            {
                return true;
            } else
            {
                notify(i, j);
                return false;
            }
        }

        public void resized()
        {
            super.resized();
            GBevel gbevel = ((GUILookAndFeel)lookAndFeel()).bevelComboDown;
            if(vScroll.isVisible())
            {
                vScroll.setPos(win.dx - lookAndFeel().getVScrollBarW() - gbevel.R.dx, gbevel.T.dy);
                vScroll.setSize(lookAndFeel().getVScrollBarW(), win.dy - gbevel.T.dy - gbevel.B.dy);
            }
            clipReg.set(0.0F, 0.0F, win.dx - gbevel.R.dx, win.dy - gbevel.B.dy);
        }

        public void render()
        {
            setCanvasColorWHITE();
            GBevel gbevel = ((GUILookAndFeel)lookAndFeel()).bevelComboDown;
            lookAndFeel().drawBevel(this, 0.0F, 0.0F, win.dx, win.dy, gbevel, ((GUILookAndFeel)lookAndFeel()).basicelements, true);
        }

        public void doChildrensRender(boolean flag)
        {
            pushClipRegion(clipReg, true, 0.0F);
            super.doChildrensRender(flag);
            popClip();
        }

        GRegion clipReg;

        public ScrollClient()
        {
            clipReg = new GRegion();
        }
    }

    public class ItemControl extends GWindowLabel
    {

        public void keyFocusExit()
        {
            if(messageBox == null)
                curKey[0] = curKey[1] = 0;
        }

        private boolean findItem(boolean flag)
        {
            boolean flag1 = false;
            Item item = items[indx];
            for(int i = 0; i < items.length; i++)
            {
                Item item1 = items[i];
                if(item1.control == null || item1.cmd.hotKeyCmdEnv().name().equals(mapEnvLink.get(item.cmd.hotKeyCmdEnv().name())))
                    continue;
                if(item1.key[0] == keySum || item1.key[0] == keySum2)
                {
                    iFinded = i;
                    if(!flag)
                        return true;
                    flag1 = true;
                    item1.key[0] = item1.key[1];
                    item1.key[1] = 0;
                } else
                {
                    if(item1.key[1] != keySum && item1.key[1] != keySum2)
                        continue;
                    iFinded = i;
                    if(!flag)
                        return true;
                    flag1 = true;
                    item1.key[1] = 0;
                }
                if(item1.env.all() != null)
                {
                    item1.env.all().remove(keySum);
                    item1.env.all().remove(keySum2);
                }
                item1.control.fillCaption();
            }

            return flag1;
        }

        private void fillItem(boolean flag)
        {
            if(flag)
                findItem(true);
            Item item = items[indx];
            item.env.add(curKey[0], curKey[1], item.cmd.name());
            if(item.key[0] != 0)
            {
                if(item.key[1] != 0)
                {
                    item.env.all().remove(item.key[1]);
                    item.env.all().remove((item.key[1] & 0xffff) << 16 | (item.key[1] & 0xffff0000) >>> 16);
                }
                item.key[1] = item.key[0];
            }
            item.key[0] = keySum;
            curKey[0] = curKey[1] = 0;
            fillCaption();
            parentWindow.setKeyFocus();
        }

        private void requestItem()
        {
            Item item = items[iFinded];
            String s = item.label.cap.caption;
            messageBox = new GWindowMessageBox(client, 24F, true, I18N.gui("ctrl.Warning"), I18N.gui("ctrl.ReplaceCommand0") + s + I18N.gui("ctrl.ReplaceCommand1"), 1, 0.0F) {

                public void result(int i)
                {
                    messageBox = null;
                    if(i == 3)
                    {
                        fillItem(true);
                    } else
                    {
                        curKey[0] = curKey[1] = 0;
                        fillCaption();
                        parentWindow.setKeyFocus();
                    }
                }

            }
;
        }

        protected void doKey(int i, boolean flag)
        {
            doKey(i, flag, false, false);
        }

        protected void doKey(int i, boolean flag, boolean flag1, boolean flag2)
        {
            if(flag)
            {
                if(curKey[0] == 0)
                    curKey[0] = i;
                else
                if(curKey[1] == 0)
                    curKey[1] = i;
            } else
            {
                if(messageBox != null)
                    return;
                if(curKey[0] == i || curKey[1] == i)
                {
                    if(curKey[0] > curKey[1])
                    {
                        int j = curKey[0];
                        curKey[0] = curKey[1];
                        curKey[1] = j;
                    }
                    keySum = (curKey[0] & 0xffff) << 16 | curKey[1] & 0xffff;
                    keySum2 = (curKey[1] & 0xffff) << 16 | curKey[0] & 0xffff;
                    Item item = items[indx];
                    boolean flag3 = false;
                    if(!flag1)
                        if(item.key[0] == keySum || item.key[0] == keySum2)
                        {
                            item.env.all().remove(item.key[1]);
                            item.env.all().remove((item.key[1] & 0xffff) << 16 | (item.key[1] & 0xffff0000) >>> 16);
                            item.key[1] = 0;
                            flag3 = true;
                        } else
                        if(item.key[1] == keySum || item.key[1] == keySum2)
                        {
                            item.env.all().remove(item.key[0]);
                            item.env.all().remove((item.key[0] & 0xffff) << 16 | (item.key[0] & 0xffff0000) >>> 16);
                            item.key[0] = item.key[1];
                            item.key[1] = 0;
                            flag3 = true;
                        }
                    if(!flag3)
                    {
                        boolean flag4 = findItem(false);
                        if(!flag1)
                        {
                            if(flag4)
                                requestItem();
                            else
                                fillItem(false);
                            return;
                        }
                        if(flag4)
                            findItem(true);
                        ((ItemJoy)item).bMinus = flag2;
                        if(flag2)
                            item.env.add(curKey[0], curKey[1], "-" + item.cmd.name());
                        else
                            item.env.add(curKey[0], curKey[1], item.cmd.name());
                        if(item.key[0] != 0)
                        {
                            item.env.all().remove(item.key[0]);
                            item.env.all().remove((item.key[0] & 0xffff) << 16 | (item.key[0] & 0xffff0000) >>> 16);
                        }
                        item.key[0] = keySum;
                    }
                    curKey[0] = curKey[1] = 0;
                    fillCaption();
                    parentWindow.setKeyFocus();
                }
            }
        }

        public void keyboardKey(int i, boolean flag)
        {
            super.keyboardKey(i, flag);
            if(i == 27)
            {
                parentWindow.setKeyFocus();
                return;
            }
            if(isKeyFocus() && !(items[indx] instanceof ItemJoy) && messageBox == null)
                doKey(i, flag);
        }

        public void mouseButton(int i, boolean flag, float f, float f1)
        {
            super.mouseButton(i, flag, f, f1);
            if(isKeyFocus() && !(items[indx] instanceof ItemJoy) && messageBox == null)
                doKey(i + 527, flag);
        }

        public void joyButton(int i, int j, boolean flag)
        {
            if(isKeyFocus() && !(items[indx] instanceof ItemJoy) && messageBox == null)
            {
                if(flag)
                {
                    curKey[0] = curKey[1] = 0;
                    doKey(i, flag);
                }
                doKey(j, flag);
                if(!flag)
                    doKey(i, flag);
            }
        }

        public void joyPov(int i, int j)
        {
            if(isKeyFocus() && !(items[indx] instanceof ItemJoy) && messageBox == null && j != 543)
            {
                curKey[0] = curKey[1] = 0;
                doKey(i, true);
                doKey(j, true);
                doKey(j, false);
                doKey(i, false);
            }
        }

        public void joyMove(int i, int j, int k)
        {
            int l = i | j << 16;
            int i1 = hashJoyMove.get(l);
            if(i1 == -1)
            {
                hashJoyMove.put(l, k);
                return;
            }
            if((double)Math.abs(Joy.normal(k - i1)) < 0.20000000000000001D)
                return;
            hashJoyMove.put(l, k);
            if(isKeyFocus() && (items[indx] instanceof ItemJoy) && messageBox == null)
            {
                boolean flag = k - i1 < 0;
                curKey[0] = curKey[1] = 0;
                doKey(i, true);
                doKey(j, true);
                doKey(j, false, true, flag);
                doKey(i, false, true, flag);
                int ai[] = new int[13];
                Joy.adapter().getSensitivity(i - 716, j - 535, ai);
                if(ai[12] > 0)
                    if(flag)
                        ai[12] = 2;
                    else
                        ai[12] = 1;
                Joy.adapter().setSensitivity(i - 716, j - 535, ai);
                int j1 = Config.cur.ini.get("rts", "JoyProfile", 0, 0, 7);
                String s = "rts_joystick";
                if(j1 > 0)
                    s = s + j1;
                Joy.adapter().saveConfig(Config.cur.ini, s);
            }
        }

        public void mouseRelMove(float f, float f1, float f2)
        {
            super.mouseRelMove(f, f1, f2);
            if(f2 != 0.0F && isKeyFocus() && (items[indx] instanceof ItemJoy) && messageBox == null)
            {
                doKey(526, true);
                doKey(526, false, true, f2 < 0.0F);
            }
        }

        public void keyFocusEnter()
        {
            super.keyFocusEnter();
            hashJoyMove.clear();
            for(int i = 0; i < povState.length; i++)
                povState[i] = 0;

        }

        public void fillCaption()
        {
            Item item = items[indx];
            String s;
            if(item.key[0] == 0 && item.key[1] == 0)
                s = "";
            else
            if(item.key[0] != 0 && item.key[1] != 0)
                s = " " + keyToStr(item.key[0]) + ", " + keyToStr(item.key[1]);
            else
            if((item instanceof ItemJoy) && ((ItemJoy)item).bMinus)
                s = " -" + keyToStr(item.key[0]);
            else
                s = " " + keyToStr(item.key[0]);
            cap = new GCaption(s);
        }

        private String keyToStr(int i)
        {
            if(i == 0)
                return "";
            if((i & 0xffff0000) == 0)
                return resName(VK.getKeyText(i));
            else
                return resName(VK.getKeyText(i >> 16 & 0xffff)) + " " + resName(VK.getKeyText(i & 0xffff));
        }

        public boolean isMousePassThrough(float f, float f1)
        {
            return false;
        }

        public boolean notify(int i, int j)
        {
            if(i == 17 && isActivated())
                return false;
            else
                return super.notify(i, j);
        }

        public void render()
        {
            if(isActivated())
            {
                setCanvasColorWHITE();
                GBevel gbevel = ((GUILookAndFeel)lookAndFeel()).bevelComboDown;
                lookAndFeel().drawBevel(this, 0.0F, 0.0F, win.dx, win.dy, gbevel, ((GUILookAndFeel)lookAndFeel()).basicelements, true);
            }
            super.render();
        }

        public int indx;



        public ItemControl(GWindow gwindow, String s, String s1)
        {
            super(gwindow, 0.0F, 0.0F, 1.0F, 1.0F, s, s1);
        }
    }

    public class ItemLabel extends GWindowLabel
    {

        public boolean isMousePassThrough(float f, float f1)
        {
            return true;
        }

        public ItemLabel(GWindow gwindow, float f, float f1, float f2, float f3, String s, 
                String s1)
        {
            super(gwindow, f, f1, f2, f3, s, s1);
        }
    }

    public class ItemJoy extends Item
    {

        boolean bMinus;

        public ItemJoy()
        {
        }
    }

    public class Item
    {

        GWindowDialogControl label;
        ItemControl control;
        HotKeyCmd cmd;
        HotKeyEnv env;
        float y;
        int indx;
        int key[];

        public Item()
        {
            key = new int[2];
        }
    }


    public void _enter()
    {
        fillItems();
        client.showWindow();
        client.doResolutionChanged();
        client.doResolutionChanged();
        GUI.activateJoy();
        if(Main.stateStack().getPrevious() != null && Main.stateStack().getPrevious().id() == 53)
        {
            bInput.setEnable(false);
            bInput.hideWindow();
        } else
        {
            bInput.setEnable(true);
            bInput.showWindow();
        }
    }

    public void _leave()
    {
        client.hideWindow();
    }

    private void fillItems()
    {
        for(int i = 0; i < items.length; i++)
        {
            Item item = items[i];
            item.key[0] = item.key[1] = 0;
        }

        ArrayList arraylist = new ArrayList();
        String as[] = UserCfg.nameHotKeyEnvs;
        for(int j = 0; j < as.length; j++)
        {
            HotKeyEnv hotkeyenv = HotKeyEnv.env(as[j]);
            boolean flag = JOYENV.equals(as[j]);
            HashMapInt hashmapint = hotkeyenv.all();
            for(HashMapIntEntry hashmapintentry = hashmapint.nextEntry(null); hashmapintentry != null; hashmapintentry = hashmapint.nextEntry(hashmapintentry))
            {
                int l = hashmapintentry.getKey();
                String s = (String)hashmapintentry.getValue();
                Item item2 = (Item)mapItems.get(s);
                if(item2 != null)
                {
                    if(item2.key[0] == 0)
                        item2.key[0] = l;
                    else
                    if(item2.key[1] == 0)
                        item2.key[1] = l;
                    else
                        arraylist.add(new Integer(l));
                    if(flag)
                    {
                        ((ItemJoy)item2).bMinus = false;
                        if(item2.key[1] != 0)
                        {
                            arraylist.add(new Integer(item2.key[1]));
                            item2.key[1] = 0;
                        }
                    }
                } else
                if(flag && s.charAt(0) == '-')
                {
                    ItemJoy itemjoy = (ItemJoy)mapItems.get(s.substring(1));
                    if(itemjoy != null)
                    {
                        itemjoy.bMinus = true;
                        if(itemjoy.key[0] == 0)
                            itemjoy.key[0] = l;
                        else
                            arraylist.add(new Integer(l));
                        if(itemjoy.key[1] != 0)
                        {
                            arraylist.add(new Integer(itemjoy.key[1]));
                            itemjoy.key[1] = 0;
                        }
                    }
                }
            }

            if(arraylist.size() > 0)
            {
                for(int i1 = 0; i1 < arraylist.size(); i1++)
                {
                    Integer integer = (Integer)arraylist.get(i1);
                    hashmapint.remove(integer.intValue());
                }

                arraylist.clear();
            }
        }

        for(int k = 0; k < items.length; k++)
        {
            Item item1 = items[k];
            if(item1.control != null)
                item1.control.fillCaption();
        }

    }

    private void initResource()
    {
        try
        {
            resource = ResourceBundle.getBundle("i18n/controls", RTSConf.cur.locale, LDRres.loader());
        }
        catch(Exception exception) { }
    }

    private String resName(String s)
    {
        if(resource == null)
            return s;
        try
        {
            return resource.getString(s);
        }
        catch(Exception exception)
        {
            return s;
        }
    }

    private void createItems()
    {
        initResource();
        mapEnvLink.put("PanView", "SnapView");
        mapEnvLink.put("SnapView", "PanView");
        ArrayList arraylist = new ArrayList();
        mapItems = new HashMapExt();
        String as[] = UserCfg.nameHotKeyEnvs;
        float f = 0.0F;
        for(int i = 0; i < as.length; i++)
        {
            Item item = new Item();
            f += 32F;
            item.y = f;
            if(as[i].startsWith("$$$"))
                item.label = fixedClient.addLabel(new ItemLabel(fixedClient, 0.0F, 0.0F, 1.0F, 1.0F, "", null));
            else
                item.label = fixedClient.addLabel(new ItemLabel(fixedClient, 0.0F, 0.0F, 1.0F, 1.0F, resName(as[i]), null));
            item.label.color = 0xff0235b6;
            arraylist.add(item);
            HotKeyCmdEnv hotkeycmdenv = HotKeyCmdEnv.env(as[i]);
            HotKeyEnv hotkeyenv = HotKeyEnv.env(as[i]);
            item.env = hotkeyenv;
            HashMapExt hashmapext = hotkeycmdenv.all();
            for(Iterator iterator = hashmapext.keySet().iterator(); iterator.hasNext();)
            {
                HotKeyCmd hotkeycmd = (HotKeyCmd)hashmapext.get(iterator.next());
                if(hotkeycmd.sortingName != null)
                    _sortMap.put(hotkeycmd.sortingName, hotkeycmd);
            }

            boolean flag = JOYENV.equals(as[i]);
            for(Iterator iterator1 = _sortMap.keySet().iterator(); iterator1.hasNext();)
            {
                HotKeyCmd hotkeycmd1 = (HotKeyCmd)_sortMap.get(iterator1.next());
                if(hotkeycmd1.name().startsWith("$$$"))
                {
                    Item item1 = new Item();
                    f += 32F;
                    item1.y = f;
                    item1.label = fixedClient.addLabel(new ItemLabel(fixedClient, 0.0F, 0.0F, 1.0F, 1.0F, "", null));
                    arraylist.add(item1);
                } else
                if(hotkeycmd1.name().startsWith("$$+"))
                {
                    Item item2 = new Item();
                    f += 32F;
                    item2.y = f;
                    item2.label = fixedClient.addLabel(new ItemLabel(fixedClient, 0.0F, 0.0F, 1.0F, 1.0F, resName(hotkeycmd1.name().substring(3)), null));
                    item2.label.color = 0xff0235b6;
                    arraylist.add(item2);
                } else
                {
                    Object obj;
                    if(flag)
                    {
                        ItemJoy itemjoy = new ItemJoy();
                        obj = itemjoy;
                    } else
                    {
                        obj = new Item();
                    }
                    f += 32F;
                    ((Item)obj).y = f;
                    ((Item)obj).cmd = hotkeycmd1;
                    ((Item)obj).env = hotkeyenv;
                    ((Item)obj).label = fixedClient.addLabel(new ItemLabel(fixedClient, 0.0F, 0.0F, 1.0F, 1.0F, resName(hotkeycmd1.name()), null));
                    ((Item)obj).control = (ItemControl)fixedClient.addControl(new ItemControl(fixedClient, hotkeycmd1.name(), null));
                    arraylist.add(obj);
                    mapItems.put(hotkeycmd1.name(), obj);
                }
            }

            _sortMap.clear();
        }

        items = new Item[arraylist.size()];
        for(int j = 0; j < arraylist.size(); j++)
        {
            items[j] = (Item)arraylist.get(j);
            items[j].indx = j;
            if(items[j].control != null)
                items[j].control.indx = j;
        }

        arraylist.clear();
        f += 64F;
        itemsDY_1024 = f;
    }

    private void setPosItems(float f)
    {
        for(int i = 0; i < items.length; i++)
        {
            Item item = items[i];
            if(item.control == null)
            {
                item.label.set1024PosSize(16F, item.y, f - 16F - 16F, 32F);
            } else
            {
                item.label.set1024PosSize(48F, item.y, f - 48F - 320F - 32F, 32F);
                item.control.set1024PosSize(f - 320F - 48F, item.y, 352F, 32F);
            }
        }

    }

    public GUIControls(GWindowRoot gwindowroot)
    {
        super(20);
        itemsDY_1024 = 1000F;
        hashJoyMove = new IntHashtable();
        mapEnvLink = new HashMap();
        povState = new int[32];
        _sortMap = new TreeMap();
        curKey = new int[2];
        messageBox = null;
        client = (GUIClient)gwindowroot.create(new GUIClient());
        dialogClient = (DialogClient)client.create(new DialogClient());
        infoMenu = (GUIInfoMenu)client.create(new GUIInfoMenu());
        infoMenu.info = i18n("ctrl.info");
        infoName = (GUIInfoName)client.create(new GUIInfoName());
        com.maddox.gwindow.GTexture gtexture = ((GUILookAndFeel)gwindowroot.lookAndFeel()).buttons2;
        bBack = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bDefault = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bRefresh = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        bInput = (GUIButton)dialogClient.addControl(new GUIButton(dialogClient, gtexture, 0.0F, 48F, 48F, 48F));
        scrollClient = (ScrollClient)dialogClient.create(new ScrollClient());
        createItems();
        dialogClient.setPosSize();
        client.hideWindow();
    }

    private static final String JOYENV = "move";
    public GUIClient client;
    public DialogClient dialogClient;
    public GUIInfoMenu infoMenu;
    public GUIInfoName infoName;
    public ScrollClient scrollClient;
    public FixedClient fixedClient;
    private Item items[];
    private HashMapExt mapItems;
//    private HashMapExt mapItemsJoy;
    private float itemsDY_1024;
    private IntHashtable hashJoyMove;
    private HashMap mapEnvLink;
    private int povState[];
    public GUIButton bBack;
    public GUIButton bDefault;
    public GUIButton bRefresh;
    public GUIButton bInput;
    ResourceBundle resource;
    TreeMap _sortMap;
    int curKey[];
    int keySum;
    int keySum2;
    int iFinded;
    GWindowMessageBox messageBox;
}
