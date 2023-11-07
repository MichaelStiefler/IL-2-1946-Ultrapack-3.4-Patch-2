package com.maddox.gwindow;

import java.util.ArrayList;

import com.maddox.rts.Time;

public class GWindow
{
    class ClipStackItem
    {

        float orgx;
        float orgy;
        float clipx;
        float clipy;
        float clipdx;
        float clipdy;

        ClipStackItem()
        {
        }
    }


    public boolean notify(GWindow gwindow, int i, int j)
    {
        return false;
    }

    public boolean notify(int i, int j)
    {
        if(!bNotify)
            return false;
        if(notifyWindow != null && notifyWindow.notify(this, i, j))
            return true;
        if(notifyListeners != null)
        {
            for(int k = 0; k < notifyListeners.size(); k++)
            {
                GNotifyListenerDef gnotifylistenerdef = (GNotifyListenerDef)notifyListeners.get(k);
                if(gnotifylistenerdef.notify(this, i, j))
                    return true;
            }

        }
        return false;
    }

    public void addNotifyListener(GNotifyListenerDef gnotifylistenerdef)
    {
        if(notifyListeners == null)
            notifyListeners = new ArrayList();
        int i = notifyListeners.indexOf(gnotifylistenerdef);
        if(i < 0)
            notifyListeners.add(gnotifylistenerdef);
    }

    public void removeNotifyListener(GNotifyListenerDef gnotifylistenerdef)
    {
        if(notifyListeners == null)
            return;
        int i = notifyListeners.indexOf(gnotifylistenerdef);
        if(i >= 0)
            notifyListeners.remove(i);
    }

    public void beforeCreate()
    {
    }

    public void created()
    {
    }

    public void afterCreated()
    {
    }

    public void resolutionChanged()
    {
        resized();
    }

    public void doResolutionChanged()
    {
        if(metricWin != null)
        {
            win.x = lookAndFeel().metric(metricWin.x);
            win.y = lookAndFeel().metric(metricWin.y);
            win.dx = lookAndFeel().metric(metricWin.dx);
            win.dy = lookAndFeel().metric(metricWin.dy);
        }
        if(childWindow != null)
        {
            for(int i = 0; i < childWindow.size(); i++)
            {
                GWindow gwindow = (GWindow)childWindow.get(i);
                gwindow.doResolutionChanged();
            }

        }
        resolutionChanged();
        resolutionChangeCounter = root.resolutionChangeCounter;
    }

    public void setPosSize(float f, float f1, float f2, float f3)
    {
        setPos(f, f1);
        setSize(f2, f3);
    }

    public void setPos(float f, float f1)
    {
        f = Math.round(f);
        f1 = Math.round(f1);
        if(win.x != f || win.y != f1)
        {
            win.x = f;
            win.y = f1;
            if(metricWin != null)
            {
                metricWin.x = win.x / lookAndFeel().metric();
                metricWin.y = win.y / lookAndFeel().metric();
            }
            moved();
        }
    }

    public void setSize(float f, float f1)
    {
        f = Math.round(f);
        f1 = Math.round(f1);
        if(win.dx != f || win.dy != f1)
        {
            win.dx = f;
            win.dy = f1;
            if(metricWin != null)
            {
                metricWin.dx = win.dx / lookAndFeel().metric();
                metricWin.dy = win.dy / lookAndFeel().metric();
            }
            resized();
        }
    }

    public void setMetric(float f, float f1, float f2, float f3)
    {
        if(metricWin == null)
            createMetricWin();
        metricWin.x = f;
        metricWin.y = f1;
        metricWin.dx = f2;
        metricWin.dy = f3;
        setSize(lookAndFeel().metric(f2), lookAndFeel().metric(f3));
        setPos(lookAndFeel().metric(f), lookAndFeel().metric(f1));
    }

    public void setMetricPos(float f, float f1)
    {
        if(metricWin == null)
            createMetricWin();
        metricWin.x = f;
        metricWin.y = f1;
        setPos(lookAndFeel().metric(f), lookAndFeel().metric(f1));
    }

    public void setMetricSize(float f, float f1)
    {
        if(metricWin == null)
            createMetricWin();
        metricWin.dx = f;
        metricWin.dy = f1;
        setSize(lookAndFeel().metric(f), lookAndFeel().metric(f1));
    }

    public void set1024PosSize(float f, float f1, float f2, float f3)
    {
        set1024Pos(f, f1);
        set1024Size(f2, f3);
    }

    public void set1024Pos(float f, float f1)
    {
        setPos((f * root.win.dx) / 1024F, (f1 * root.win.dy) / 768F);
    }

    public void set1024Size(float f, float f1)
    {
        setSize((f * root.win.dx) / 1024F, (f1 * root.win.dy) / 768F);
    }

    public float x1024(float f)
    {
        return (f * root.win.dx) / 1024F;
    }

    public float y1024(float f)
    {
        return (f * root.win.dy) / 768F;
    }

    public float x1024g(float f)
    {
      return f * this.root.win.dx / getScaledWidth();
    }
    
    public float y1024g(float f)
    {
      return f * this.root.win.dy / getScaledHeight();
    }

    private float getScaledWidth()
    {
      float f = this.root.win.dx / this.root.win.dy;
      if (f >= 1.0F) {
        return f * 768.0F;
      }
      return f * 768.0F * 2.0F;
    }
    
    public float getScaledHeight()
    {
      float f = this.root.win.dx / this.root.win.dy;
      if (f >= 1.0F) {
        return 768.0F;
      }
      return 1536.0F;
    }
    
    public void createMetricWin()
    {
        metricWin = new GRegion(win.x / lookAndFeel().metric(), win.y / lookAndFeel().metric(), win.dx / lookAndFeel().metric(), win.dy / lookAndFeel().metric());
    }

    public void moved()
    {
        notify(0, 0);
    }

    public void resized()
    {
        notify(1, 0);
    }

    public GSize getMinSize()
    {
        return getMinSize(_minSize);
    }

    public GSize getMinSize(GSize gsize)
    {
        float f = 0.0F;
        float f1 = 0.0F;
        if(childWindow != null)
        {
            for(int i = 0; i < childWindow.size(); i++)
            {
                GWindow gwindow = (GWindow)childWindow.get(i);
                gwindow.getMinSize(gsize);
                if(gsize.dx > f)
                    f = gsize.dx;
                if(gsize.dy > f1)
                    f1 = gsize.dy;
            }

        }
        gsize.set(f, f1);
        return gsize;
    }

    public GRegion getClientRegion(float f)
    {
        return getClientRegion(_clientRegion, f);
    }

    public GRegion getClientRegion()
    {
        return getClientRegion(_clientRegion, 0.0F);
    }

    public GRegion getClientRegion(GRegion gregion, float f)
    {
        gregion.x = gregion.y = f;
        gregion.dx = win.dx - 2.0F * f;
        gregion.dy = win.dy - 2.0F * f;
        return gregion;
    }

    public void preRender()
    {
    }

    public void render()
    {
    }

    public void keyFocusEnter()
    {
        notify(15, 0);
    }

    public void keyboardKey(int i, boolean flag)
    {
        notify(flag ? 10 : 11, i);
    }

    public void keyboardChar(char c)
    {
        notify(12, c);
    }

    public void keyFocusExit()
    {
        notify(16, 0);
    }

    public boolean isKeyFocus()
    {
        return this == root.keyFocusWindow;
    }

    public void setKeyFocus()
    {
        activeWindow = null;
        activateWindow(false);
        root.doCheckKeyFocusWindow();
    }

    public void setAcceptsKeyFocus()
    {
        if(bAcceptsKeyFocus)
            return;
        bAcceptsKeyFocus = true;
        if(this != root)
            parentWindow.setAcceptsKeyFocus();
    }

    public void cancelAcceptsKeyFocus()
    {
        if(childWindow != null)
        {
            for(int i = 0; i < childWindow.size(); i++)
            {
                GWindow gwindow = (GWindow)childWindow.get(i);
                gwindow.cancelAcceptsKeyFocus();
            }

        }
        bAcceptsKeyFocus = false;
    }

    public boolean hotKey(int i, boolean flag)
    {
        return false;
    }

    public boolean hotKeyChar(char c)
    {
        return false;
    }

    public void setAcceptsHotKeys(boolean flag)
    {
        if(flag && !bAcceptsHotKeys && bVisible)
            root.registerKeyListener(this);
        if(!flag && bAcceptsHotKeys && bVisible)
            root.unRegisterKeyListener(this);
        bAcceptsHotKeys = flag;
    }

    public void joyButton(int i, int j, boolean flag)
    {
    }

    public void joyMove(int i, int j, int k)
    {
    }

    public void joyPov(int i, int j)
    {
    }

    public void joyPoll()
    {
    }

    public void mouseCapture(boolean flag)
    {
        if(flag)
        {
            if(root.mouseWindow == this && root.bMouseCapture)
                return;
            root.bMouseCapture = true;
            root.mouseWindow = this;
            root.doMouseAbsMove(root.mousePos.x, root.mousePos.y, root.mousePosZ);
        } else
        if(root.mouseWindow == this && root.bMouseCapture)
        {
            root.bMouseCapture = false;
            root.doMouseAbsMove(root.mousePos.x, root.mousePos.y, root.mousePosZ);
        }
    }

    public boolean isMouseOver()
    {
        return root.mouseWindow == this;
    }

    public boolean isMouseCaptured()
    {
        return root.bMouseCapture && root.mouseWindow == this;
    }

    public boolean isMouseCapturedAny()
    {
        return root.bMouseCapture;
    }

    public boolean isMouseDown(int i)
    {
        return root.mouseWindowDown[i] == this;
    }

    public boolean isMouseDownAny(int i)
    {
        return root.mouseWindowDown[i] != null;
    }

    public void mouseEnter()
    {
        notify(7, 0);
    }

    public void mouseMove(float f, float f1)
    {
        notify(8, 0);
    }

    public void mouseRelMove(float f, float f1, float f2)
    {
        notify(17, 0);
    }

    public void mouseClick(int i, float f, float f1)
    {
        notify(5, i);
    }

    public void mouseDoubleClick(int i, float f, float f1)
    {
        notify(6, i);
    }

    public void mouseLeave()
    {
        notify(9, 0);
    }

    public boolean isActivated()
    {
        if(parentWindow.activeWindow != this)
            return false;
        else
            return parentWindow.isActivated();
    }

    public void mouseButton(int i, boolean flag, float f, float f1)
    {
        if(flag)
            notify(3, i);
        else
            notify(4, i);
    }

    protected void _mouseButton(int i, boolean flag, float f, float f1)
    {
      GWindow gwindow;
      GPoint gpoint;
      if (flag) {
        activateWindow(false);
        if (this.root.mouseWindowDown[i] != null) {
          gwindow = this.root.mouseWindowDown[i];
          gpoint = gwindow.getMouseXY();
          gwindow.mouseButton(i, false, gpoint.x, gpoint.y);
        }
        this.root.mouseWindowDown[i] = this;
      } else {
        if (this.root.mouseWindowDown[i] == this) {
          if ((f >= 0.0F) && (f < this.win.dx) && (f1 >= 0.0F) && (f1 < this.win.dy)) {
            if ((this.bEnableDoubleClick[i]) && (this.root.mouseWindowUp[i] == this) && (Time.currentReal() < this.root.mouseTimeUp[i] + GWindowRoot.TIME_DOUBLE_CLICK))
            {
              this.root.mouseTimeUp[i] = 0L;
              mouseDoubleClick(i, f, f1);
            } else {
              this.root.mouseWindowUp[i] = this;
              this.root.mouseTimeUp[i] = Time.currentReal();
              mouseClick(i, f, f1);
            }
          }
          else this.root.mouseWindowUp[i] = null;
        }
        else if (this.root.mouseWindowDown[i] != null) {
          gwindow = this.root.mouseWindowDown[i];
          gpoint = gwindow.getMouseXY();
          gwindow.mouseButton(i, false, gpoint.x, gpoint.y);
        }
        this.root.mouseWindowDown[i] = null;
      }

      mouseButton(i, flag, f, f1);
    }

    public void msgMouseButton(boolean flag, int i, boolean flag1, float f, float f1)
    {
    }

    public void msgMouseMove(boolean flag, float f, float f1)
    {
    }

    public void setMouseListener(boolean flag)
    {
        if(flag && !bMouseListener && bVisible)
            root.registerMouseListener(this);
        if(!flag && bMouseListener && bVisible)
            root.unRegisterMouseListener(this);
        bMouseListener = flag;
    }

    public boolean isMousePassThrough(float f, float f1)
    {
        return false;
    }

    public final GPoint getMouseXY()
    {
        GWindow gwindow = this;
        _mousePos.set(root.mousePos);
        for(; gwindow != root; gwindow = gwindow.parentWindow)
            _mousePos.sub(gwindow.win.x, gwindow.win.y);

        return _mousePos;
    }

    public final GPoint globalToWindow(float f, float f1)
    {
        GWindow gwindow = this;
        _winPos.set(f, f1);
        for(; gwindow != root; gwindow = gwindow.parentWindow)
            _winPos.sub(gwindow.win.x, gwindow.win.y);

        return _winPos;
    }

    public final GPoint windowToGlobal(float f, float f1)
    {
        GWindow gwindow = this;
        _globalPos.set(f, f1);
        for(; gwindow != root; gwindow = gwindow.parentWindow)
            _globalPos.add(gwindow.win.x, gwindow.win.y);

        return _globalPos;
    }

    public final GWindow getParent(Class class1, boolean flag)
    {
        return getParent(this, class1, flag, false);
    }

    public final GWindow getParent(GWindow gwindow, Class class1, boolean flag, boolean flag1)
    {
        gwindow = gwindow.parentWindow;
        if(gwindow == null)
            return null;
        for(; gwindow != root; gwindow = gwindow.parentWindow)
        {
            if(flag ? class1 != gwindow.getClass() : !class1.isInstance(gwindow))
                continue;
            if(flag1)
            {
                if(isFindTestOk(gwindow, true))
                    return gwindow;
            } else
            {
                return gwindow;
            }
        }

        return null;
    }

    public final GWindow getChild(Class class1, boolean flag)
    {
        return getChild(this, class1, flag, false);
    }

    public final GWindow getChild(GWindow gwindow, Class class1, boolean flag, boolean flag1)
    {
        if(gwindow.childWindow == null)
            return null;
        for(int i = gwindow.childWindow.size() - 1; i >= 0; i--)
        {
            GWindow gwindow1 = (GWindow)gwindow.childWindow.get(i);
            if(flag ? class1 == gwindow1.getClass() : class1.isInstance(gwindow1))
                if(flag1)
                {
                    if(isFindTestOk(gwindow1, true))
                        return gwindow1;
                } else
                {
                    return gwindow1;
                }
            gwindow1 = getChild(gwindow1, class1, flag, flag1);
            if(gwindow1 == null)
                continue;
            if(flag1)
            {
                if(isFindTestOk(gwindow1, false))
                    return gwindow1;
            } else
            {
                return gwindow1;
            }
        }

        return null;
    }

    public boolean isFindTestOk(GWindow gwindow, boolean flag)
    {
        return true;
    }

    public void doRender(boolean flag) {
        if (this.resolutionChangeCounter != this.root.resolutionChangeCounter) {
          doResolutionChanged();
          this.resolutionChangeCounter = this.root.resolutionChangeCounter;
        }

        GPoint gpoint = this.root.C.org;
        GRegion gregion = this.root.C.clip;
        float f1 = gpoint.x;
        float f2 = gpoint.y;
        gpoint.add(this.win.x, this.win.y);
        float f3 = gregion.x;
        float f4 = gregion.y;
        float f5 = gregion.dx;
        float f6 = gregion.dy;

        do {
            if (this.bClip) {
              float f7 = gregion.x - gpoint.x;
              if (f7 < 0.0F) {
                gregion.dx += f7;
                if (gregion.dx <= 0.0F) break;
                gregion.x = gpoint.x;
                f7 = 0.0F;
              }
              f7 = gregion.dx + f7 - this.win.dx;
              if (f7 > 0.0F) {
                gregion.dx -= f7;
                if (gregion.dx <= 0.0F) break; 
              }
              f7 = gregion.y - gpoint.y;
              if (f7 < 0.0F) {
                gregion.dy += f7;
                if (gregion.dy <= 0.0F) break;
                gregion.y = gpoint.y;
                f7 = 0.0F;
              }
              f7 = gregion.dy + f7 - this.win.dy;
              if (f7 > 0.0F) {
                gregion.dy -= f7;
                if (gregion.dy <= 0.0F) break; 
              }
            } else {
              gregion.set(this.root.win);
            }
    
            if (flag) render(); else
              preRender();
            if (this.childWindow != null) {
              doChildrensRender(flag);
            }
        } while (false);

        gregion.set(f3, f4, f5, f6);
        gpoint.set(f1, f2);
      }

    public void doChildrensRender(boolean flag)
    {
        for(int i = 0; i < childWindow.size(); i++)
        {
            GWindow gwindow = (GWindow)childWindow.get(i);
            gwindow.doRender(flag);
        }

    }

    public void popClip()
    {
        if(clipStackPtr <= 0)
        {
            System.err.println("GWindow clip stack underflow");
            return;
        } else
        {
            ClipStackItem clipstackitem = (ClipStackItem)clipStack.get(--clipStackPtr);
            GPoint gpoint = root.C.org;
            GRegion gregion = root.C.clip;
            gregion.set(clipstackitem.clipx, clipstackitem.clipy, clipstackitem.clipdx, clipstackitem.clipdy);
            gpoint.set(clipstackitem.orgx, clipstackitem.orgy);
            return;
        }
    }

    protected void pushClip()
    {
        if(clipStack.size() == clipStackPtr)
            clipStack.add(new ClipStackItem());
        ClipStackItem clipstackitem = (ClipStackItem)clipStack.get(clipStackPtr++);
        GPoint gpoint = root.C.org;
        GRegion gregion = root.C.clip;
        clipstackitem.orgx = gpoint.x;
        clipstackitem.orgy = gpoint.y;
        clipstackitem.clipx = gregion.x;
        clipstackitem.clipy = gregion.y;
        clipstackitem.clipdx = gregion.dx;
        clipstackitem.clipdy = gregion.dy;
    }

    public boolean pushClipRegion(GRegion gregion, boolean flag, float f) {
        pushClip();
        GPoint gpoint = this.root.C.org;
        GRegion gregion1 = this.root.C.clip;
        gpoint.add(gregion.x + f, gregion.y + f);

        do {
            if (flag) {
              float f1 = gregion1.x - gpoint.x;
              if (f1 < 0.0F) {
                gregion1.dx += f1;
                if (gregion1.dx <= 0.0F) break;
                gregion1.x = gpoint.x;
                f1 = 0.0F;
              }
              f1 = gregion1.dx + f1 - (gregion.dx - 2.0F * f);
              if (f1 > 0.0F) {
                gregion1.dx -= f1;
                if (gregion1.dx <= 0.0F) break; 
              }
              f1 = gregion1.y - gpoint.y;
              if (f1 < 0.0F) {
                gregion1.dy += f1;
                if (gregion1.dy <= 0.0F) break;
                gregion1.y = gpoint.y;
                f1 = 0.0F;
              }
              f1 = gregion1.dy + f1 - (gregion.dy - 2.0F * f);
              if (f1 > 0.0F) {
                gregion1.dy -= f1;
                if (gregion1.dy <= 0.0F) break; 
              }
            } else {
              gregion1.set(this.root.win);
            }
            return true;
        } while (false);

        popClip();
        return false;
      }

    public boolean pushClipRegion(GRegion gregion, float f)
    {
        return pushClipRegion(gregion, bClip, f);
    }

    public boolean pushClipRegion(GRegion gregion, boolean flag)
    {
        return pushClipRegion(gregion, flag, 0.0F);
    }

    public boolean pushClipRegion(GRegion gregion)
    {
        return pushClipRegion(gregion, bClip, 0.0F);
    }

    public GWindowLookAndFeel lookAndFeel()
    {
        return root.lookAndFeel;
    }

    public GWindowLookAndFeel lAF()
    {
        return root.lookAndFeel;
    }

    public void setCanvasColor(GColor gcolor)
    {
        root.C.color.color = gcolor.color;
    }

    public void setCanvasColor(int i)
    {
        root.C.color.color = i;
    }

    public void setCanvasColorWHITE()
    {
        root.C.color.color = 0xffffff;
    }

    public void setCanvasColorBLACK()
    {
        root.C.color.color = 0;
    }

    public void draw(float f, float f1, GTexture gtexture)
    {
        draw(f, f1, gtexture.size.dx, gtexture.size.dy, gtexture, 0.0F, 0.0F, gtexture.size.dx, gtexture.size.dy);
    }

    public void draw(float f, float f1, float f2, float f3, GTexture gtexture)
    {
        draw(f, f1, f2, f3, gtexture, 0.0F, 0.0F, gtexture.size.dx, gtexture.size.dy);
    }

    public void draw(float f, float f1, float f2, float f3, GTexture gtexture, float f4, float f5, 
            float f6, float f7)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        gcanvas.draw(gtexture, f2, f3, f4, f5, f6, f7);
    }

    public void draw(float f, float f1, GMesh gmesh)
    {
        draw(f, f1, gmesh.size.dx, gmesh.size.dy, gmesh);
    }

    public void draw(float f, float f1, float f2, float f3, GMesh gmesh)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        gcanvas.draw(gmesh, f2, f3);
    }

    public void draw(float f, float f1, GTexture gtexture, GRegion gregion)
    {
        draw(f, f1, gregion.dx, gregion.dy, gtexture, gregion.x, gregion.y, gregion.dx, gregion.dy);
    }

    public void draw(float f, float f1, float f2, float f3, GTexture gtexture, GRegion gregion)
    {
        draw(f, f1, f2, f3, gtexture, gregion.x, gregion.y, gregion.dx, gregion.dy);
    }

    public void draw(float f, float f1, GTexRegion gtexregion)
    {
        draw(f, f1, gtexregion.dx, gtexregion.dy, gtexregion.texture, gtexregion.x, gtexregion.y, gtexregion.dx, gtexregion.dy);
    }

    public void draw(float f, float f1, float f2, float f3, GTexRegion gtexregion)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        gcanvas.draw(gtexregion.texture, f2, f3, gtexregion.x, gtexregion.y, gtexregion.dx, gtexregion.dy);
    }

    public void setCanvasFont(int i)
    {
        root.C.font = root.textFonts[i];
    }

    public void draw(float f, float f1, String s)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        gcanvas.draw(s);
    }

    public void draw(float f, float f1, String s, int i, int j)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        gcanvas.draw(s, i, j);
    }

    public void draw(float f, float f1, char ac[], int i, int j)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        gcanvas.draw(ac, i, j);
    }

    public void draw(float f, float f1, float f2, float f3, String s, int i)
    {
        draw(f, f1, f2, f3, s, 0, s.length(), i);
    }

    public void draw(float f, float f1, float f2, float f3, String s, int i, int j, 
            int k)
    {
        int l = computeLines(s, i, j, f2);
        if(l > k)
            l = k;
        GFont gfont = root.C.font;
        float f4 = gfont.height;
        f1 += (f3 - f4 * (float)l) / 2.0F;
        drawLines(f, f1, s, i, j, f2, f4, l);
    }

    public void draw(float f, float f1, float f2, float f3, int i, String s)
    {
        draw(f, f1, f2, f3, i, s, 0, s.length());
    }

    public void draw(float f, float f1, float f2, float f3, int i, String s, int j, 
            int k)
    {
        if(k == 0)
            return;
        GFont gfont = root.C.font;
        GSize gsize = gfont.size(s, j, k);
        f1 += (f3 - gsize.dy) / 2.0F;
        if(i == 2)
            f += f2 - gsize.dx;
        else
        if(i == 1)
            f += (f2 - gsize.dx) / 2.0F;
        draw(f, f1, s, j, k);
    }

    public void draw(float f, float f1, float f2, float f3, int i, char ac[], int j, 
            int k)
    {
        if(k == 0)
            return;
        GFont gfont = root.C.font;
        GSize gsize = gfont.size(ac, j, k);
        f1 += (f3 - gsize.dy) / 2.0F;
        if(i == 2)
            f += f2 - gsize.dx;
        else
        if(i == 1)
            f += (f2 - gsize.dx) / 2.0F;
        draw(f, f1, ac, j, k);
    }

    public int computeLines(String as[], float f)
    {
        int i = 0;
        int j = 0;
        do
        {
            if(j >= as.length)
                break;
            String s = as[j];
            if(s == null)
                break;
            i += computeLines(s, 0, s.length(), f);
            j++;
        } while(true);
        return i;
    }

    public int computeLines(String s, int i, int j, float f)
    {
        GFont gfont = root.C.font;
        int k = 0;
        do
        {
            if(j <= 0)
                break;
            int l = j;
            int i1 = s.indexOf('\n', i);
            if(i1 >= 0)
                l = i1 - i;
            if(l > 0)
            {
                while(l > 0) 
                {
                    int j1 = gfont.len(s, i, l, f, true, true);
                    if(j1 == 0)
                        j1 = gfont.len(s, i, l, f, true, false);
                    if(j1 == 0)
                        return k;
                    k++;
                    j -= j1;
                    i += j1;
                    l -= j1;
                    while(l > 0 && s.charAt(i) == ' ') 
                    {
                        i++;
                        j--;
                        l--;
                    }
                }
                if(i1 >= 0)
                {
                    i++;
                    j--;
                }
            } else
            {
                k++;
                j--;
                i++;
            }
        } while(true);
        return k;
    }

    public int drawLines(float f, float f1, String as[], float f2, float f3)
    {
        return drawLines(f, f1, as, f2, f3, -1);
    }

    public int drawLines(float f, float f1, String as[], float f2, float f3, int i)
    {
        int j = 0;
        int k = 0;
        do
        {
            if(k >= as.length || i == 0)
                break;
            String s = as[k];
            if(s == null)
                break;
            int l = drawLines(f, f1, s, 0, s.length(), f2, f3, i);
            j += l;
            i -= l;
            f1 += (float)l * f3;
            k++;
        } while(true);
        return j;
    }

    public int drawLines(float f, float f1, String s, int i, int j, float f2, float f3)
    {
        return drawLines(f, f1, s, i, j, f2, f3, -1);
    }

    public int drawLines(float f, float f1, String s, int i, int j, float f2, float f3, 
            int k)
    {
        GCanvas gcanvas = root.C;
        gcanvas.cur.x = f + gcanvas.org.x;
        gcanvas.cur.y = f1 + gcanvas.org.y;
        GFont gfont = gcanvas.font;
        int l = 0;
        do
        {
            if(j <= 0 || k == 0)
                break;
            int i1 = j;
            int j1 = s.indexOf('\n', i);
            if(j1 >= 0)
                i1 = j1 - i;
            if(i1 > 0)
            {
                while(i1 > 0 && k != 0) 
                {
                    int k1 = gfont.len(s, i, i1, f2, true, true);
                    if(k1 == 0)
                        k1 = gfont.len(s, i, i1, f2, true, false);
                    if(k1 == 0)
                        return l;
                    gcanvas.draw(s, i, k1);
                    gcanvas.cur.y += f3;
                    l++;
                    j -= k1;
                    i += k1;
                    i1 -= k1;
                    k--;
                    while(i1 > 0 && s.charAt(i) == ' ') 
                    {
                        i++;
                        j--;
                        i1--;
                    }
                }
                if(j1 >= 0)
                {
                    i++;
                    j--;
                }
            } else
            {
                gcanvas.cur.y += f3;
                l++;
                j--;
                i++;
                k--;
            }
        } while(true);
        return l;
    }

    public void toolTip(String s)
    {
        if(this != root)
            parentWindow.toolTip(s);
    }

    public void windowShown()
    {
        notify(13, 0);
        if(childWindow != null)
        {
            for(int i = 0; i < childWindow.size(); i++)
            {
                GWindow gwindow = (GWindow)childWindow.get(i);
                gwindow.windowShown();
            }

        }
    }

    public void windowHidden()
    {
        notify(14, 0);
        if(childWindow != null)
        {
            for(int i = 0; i < childWindow.size(); i++)
            {
                GWindow gwindow = (GWindow)childWindow.get(i);
                gwindow.windowHidden();
            }

        }
    }

    public boolean isVisible()
    {
        if(this == root)
            return true;
        if(!bVisible)
            return false;
        else
            return parentWindow.isVisible();
    }

    public boolean isWaitModal()
    {
        GWindow gwindow = root.modalWindow;
        if(gwindow == null || !gwindow.bVisible)
            return false;
        for(GWindow gwindow1 = this; gwindow1 != root; gwindow1 = gwindow1.parentWindow)
            if(gwindow1 == gwindow)
                return false;

        return true;
    }

    public final void bringToFront()
    {
        if(this == root)
            return;
        if(!bAlwaysBehind && !isWaitModal())
        {
            boolean flag = parentWindow.activeWindow == this;
            parentWindow.hideChildWindow(this);
            parentWindow.showChildWindow(this, false);
            if(flag)
                parentWindow.activeWindow = this;
        }
        parentWindow.bringToFront();
    }

    public void showModal()
    {
        root.modalWindow = null;
        showWindow();
        bringToFront();
        activateWindow(false);
        root.modalWindow = this;
    }

    public void activateWindow()
    {
        if(!bVisible)
            showWindow();
        activateWindow(false);
    }

    public void showWindow()
    {
        parentWindow.showChildWindow(this, false);
        windowShown();
    }

    public void hideWindow()
    {
        windowHidden();
        parentWindow.hideChildWindow(this);
    }

    public void close(boolean flag)
    {
        if(childWindow != null)
        {
            for(int i = 0; i < childWindow.size(); i++)
            {
                GWindow gwindow = (GWindow)childWindow.get(i);
                gwindow.close(true);
            }

        }
        if(!flag)
            hideWindow();
        if(this == root.modalWindow)
            root.modalWindow = null;
        if(root.bMouseCapture && root.mouseWindow == this)
        {
            root.bMouseCapture = false;
            root.doMouseAbsMove(root.mousePos.x, root.mousePos.y, root.mousePosZ);
        }
    }

    public void setParent(GWindow gwindow)
    {
        boolean flag = false;
        if(bVisible)
        {
            flag = parentWindow.activeWindow == this;
            parentWindow.hideChildWindow(this);
        }
        parentWindow = gwindow;
        if(bVisible)
        {
            parentWindow.showChildWindow(this, false);
            if(flag)
                parentWindow.activeWindow = this;
        }
    }

    protected void activateWindow(boolean flag)
    {
        root.bDoCheckKeyFocusWindow = true;
        if(this == root)
            return;
        if(isWaitModal())
            return;
        if(!bAlwaysBehind)
        {
            boolean flag1 = parentWindow.activeWindow == this;
            parentWindow.hideChildWindow(this);
            parentWindow.showChildWindow(this, false);
            if(flag1)
                parentWindow.activeWindow = this;
        }
        if(bTransient || flag)
        {
            parentWindow.activateWindow(true);
        } else
        {
            parentWindow.activeWindow = this;
            parentWindow.activateWindow(false);
        }
    }

    protected GWindow checkKeyFocusWindow()
    {
        if(childWindow == null)
            return this;
        if(activeWindow != null && activeWindow.bAcceptsKeyFocus)
            return activeWindow.checkKeyFocusWindow();
        else
            return this;
    }

    protected void showChildWindow(GWindow gwindow, boolean flag)
    {
        if(gwindow.bVisible)
            return;
        gwindow.bVisible = true;
        if(gwindow.bAcceptsHotKeys)
            root.registerKeyListener(gwindow);
        if(gwindow.bMouseListener)
            root.registerMouseListener(gwindow);
        if(flag)
            childWindow.add(0, gwindow);
        else
        if(childWindow.size() == 0)
        {
            childWindow.add(gwindow);
        } else
        {
            for(int i = childWindow.size() - 1; i >= 0; i--)
            {
                GWindow gwindow1 = (GWindow)childWindow.get(i);
                if(gwindow.bAlwaysOnTop || !gwindow1.bAlwaysOnTop)
                {
                    childWindow.add(i + 1, gwindow);
                    return;
                }
            }

            childWindow.add(0, gwindow);
        }
    }

    protected void hideChildWindow(GWindow gwindow)
    {
        if(!gwindow.bVisible)
            return;
        gwindow.bVisible = false;
        if(gwindow.bAcceptsHotKeys)
            root.unRegisterKeyListener(gwindow);
        if(gwindow.bMouseListener)
            root.unRegisterMouseListener(gwindow);
        int i = childWindow.indexOf(gwindow);
        if(i >= 0)
            childWindow.remove(i);
        if(activeWindow == gwindow)
        {
            activeWindow = null;
            root.bDoCheckKeyFocusWindow = true;
        }
    }

    protected GWindow doNew(GWindow gwindow, float f, float f1, float f2, float f3, boolean flag)
    {
        if(flag)
        {
            metricWin = new GRegion(f, f1, f2, f3);
            win = new GRegion(gwindow.lookAndFeel().metric(metricWin.x), gwindow.lookAndFeel().metric(metricWin.y), gwindow.lookAndFeel().metric(metricWin.dx), gwindow.lookAndFeel().metric(metricWin.dy));
        } else
        {
            win = new GRegion((int)(f + 0.5F), (int)(f1 + 0.5F), (int)(f2 + 0.5F), (int)(f3 + 0.5F));
        }
        root = gwindow.root;
        parentWindow = gwindow;
        if(gwindow.childWindow == null)
            gwindow.childWindow = new ArrayList();
        if(notifyWindow == null)
            notifyWindow = gwindow;
        activeWindow = null;
        created();
        gwindow.showChildWindow(this, false);
        afterCreated();
        return this;
    }

    protected GWindow doNew(GWindow gwindow)
    {
        doNew(gwindow, 0.0F, 0.0F, gwindow.win.dx, gwindow.win.dy, false);
        return this;
    }

    public GWindow create(float f, float f1, float f2, float f3, boolean flag, GWindow gwindow)
    {
        return gwindow.doNew(this, f, f1, f2, f3, flag);
    }

    public GWindow create(GWindow gwindow)
    {
        return gwindow.doNew(this, 0.0F, 0.0F, win.dx, win.dy, false);
    }

    public GWindow(GWindow gwindow, float f, float f1, float f2, float f3, boolean flag)
    {
        bVisible = false;
        bClip = true;
        bNotify = false;
        bAlwaysOnTop = false;
        bAlwaysBehind = false;
        bAcceptsKeyFocus = true;
        bAcceptsHotKeys = false;
        bTransient = false;
        bMouseListener = false;
        bEnableDoubleClick = new boolean[7];
        mouseCursor = 1;
        doNew(gwindow, f, f1, f2, f3, flag);
    }

    public GWindow(GWindow gwindow)
    {
        bVisible = false;
        bClip = true;
        bNotify = false;
        bAlwaysOnTop = false;
        bAlwaysBehind = false;
        bAcceptsKeyFocus = true;
        bAcceptsHotKeys = false;
        bTransient = false;
        bMouseListener = false;
        bEnableDoubleClick = new boolean[7];
        mouseCursor = 1;
        doNew(gwindow, 0.0F, 0.0F, gwindow.win.dx, gwindow.win.dy, false);
    }

    public GWindow()
    {
        bVisible = false;
        bClip = true;
        bNotify = false;
        bAlwaysOnTop = false;
        bAlwaysBehind = false;
        bAcceptsKeyFocus = true;
        bAcceptsHotKeys = false;
        bTransient = false;
        bMouseListener = false;
        bEnableDoubleClick = new boolean[7];
        mouseCursor = 1;
        beforeCreate();
    }

    protected static void printDebug(Exception exception)
    {
        System.out.println(exception.getMessage());
        exception.printStackTrace();
    }

    public static final float metricConst = 12F;
    public static final int MOUSE_CURSOR_NONE = 0;
    public static final int MOUSE_CURSOR_NORMAL = 1;
    public static final int MOUSE_CURSOR_CROSS = 2;
    public static final int MOUSE_CURSOR_HAND = 3;
    public static final int MOUSE_CURSOR_HELP = 4;
    public static final int MOUSE_CURSOR_IBEAM = 5;
    public static final int MOUSE_CURSOR_NO = 6;
    public static final int MOUSE_CURSOR_SIZEALL = 7;
    public static final int MOUSE_CURSOR_SIZENESW = 8;
    public static final int MOUSE_CURSOR_SIZENS = 9;
    public static final int MOUSE_CURSOR_SIZENWSE = 10;
    public static final int MOUSE_CURSOR_SIZEWE = 11;
    public static final int MOUSE_CURSOR_UP = 12;
    public static final int MOUSE_CURSOR_WAIT = 13;
    public static final int MOUSE_LEFT = 0;
    public static final int MOUSE_RIGHT = 1;
    public static final int MOUSE_MIDDLE = 2;
    public static final int MOUSE_FOUR = 3;
    public static final int FONT_NORMAL = 0;
    public static final int FONT_BOLD = 1;
    public static final int FONT_LARGE = 2;
    public static final int FONT_LARGEBOLD = 3;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int CONTROL_TAB_STOP = 1;
    public static final int CONTROL_DEFAULT = 2;
    public static final int NOTIFY_MOVED = 0;
    public static final int NOTIFY_RESIZED = 1;
    public static final int NOTIFY_CHANGE = 2;
    public static final int NOTIFY_MOUSEDOWN = 3;
    public static final int NOTIFY_MOUSEUP = 4;
    public static final int NOTIFY_CLICK = 5;
    public static final int NOTIFY_DOUBLECLICK = 6;
    public static final int NOTIFY_MOUSEENTER = 7;
    public static final int NOTIFY_MOUSEMOVE = 8;
    public static final int NOTIFY_MOUSELEAVE = 9;
    public static final int NOTIFY_KEYDOWN = 10;
    public static final int NOTIFY_KEYUP = 11;
    public static final int NOTIFY_KEYCHAR = 12;
    public static final int NOTIFY_SHOWN = 13;
    public static final int NOTIFY_HIDDEN = 14;
    public static final int NOTIFY_KEYFOCUSENTER = 15;
    public static final int NOTIFY_KEYFOCUSEXIT = 16;
    public static final int NOTIFY_MOUSERELMOVE = 17;
    public static final int NOTIFY_USER = 20;
    public static final int HITTEST_NONE = 0;
    public static final int HITTEST_NW = 1;
    public static final int HITTEST_N = 2;
    public static final int HITTEST_NE = 3;
    public static final int HITTEST_W = 4;
    public static final int HITTEST_E = 5;
    public static final int HITTEST_SW = 6;
    public static final int HITTEST_S = 7;
    public static final int HITTEST_SE = 8;
    public static final int HITTEST_TITLE = 9;
    public boolean bVisible;
    public boolean bClip;
    public boolean bNotify;
    public boolean bAlwaysOnTop;
    public boolean bAlwaysBehind;
    public boolean bAcceptsKeyFocus;
    public boolean bAcceptsHotKeys;
    public boolean bTransient;
    public boolean bMouseListener;
    public boolean bEnableDoubleClick[];
    public GRegion win;
    public GRegion metricWin;
    public GWindow parentWindow;
    public ArrayList childWindow;
    public GWindow notifyWindow;
    public ArrayList notifyListeners;
    public GWindow activeWindow;
    public GWindowRoot root;
    public int resolutionChangeCounter;
    public int mouseCursor;
    public static GSize _minSize = new GSize();
    public static GRegion _clientRegion = new GRegion();
    private static GPoint _mousePos = new GPoint();
    private static GPoint _winPos = new GPoint();
    private static GPoint _globalPos = new GPoint();
    private static ArrayList clipStack = new ArrayList();
    private static int clipStackPtr = 0;

}
