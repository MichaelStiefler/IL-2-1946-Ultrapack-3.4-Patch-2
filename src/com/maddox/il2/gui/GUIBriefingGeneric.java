/* 4.10.1 class */
package com.maddox.il2.gui;

import java.util.ResourceBundle;

import com.maddox.JGP.Vector3f;
import com.maddox.gwindow.GBevel;
import com.maddox.gwindow.GColor;
import com.maddox.gwindow.GTexture;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowDialogClient;
import com.maddox.gwindow.GWindowMessageBox;
import com.maddox.gwindow.GWindowRoot;
import com.maddox.gwindow.GWindowScrollingDialogClient;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.CameraOrtho2D;
import com.maddox.il2.engine.GUIRenders;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Land2DText;
import com.maddox.il2.engine.Land2Dn;
import com.maddox.il2.engine.LightEnvXY;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Render;
import com.maddox.il2.engine.Renders;
import com.maddox.il2.engine.TTFont;
import com.maddox.il2.game.GameState;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.rts.HomePath;
import com.maddox.rts.RTSConf;
import com.maddox.rts.SectFile;
import com.maddox.util.NumberTokenizer;

public class GUIBriefingGeneric extends GameState {
    public GUIClient        client;
    public DialogClient     dialogClient;
    public GUIInfoMenu      infoMenu;
    public GUIInfoName      infoName;
    public ScrollDescript   wScrollDescription;
    public Descript         wDescript;
    public GUIButton        bLoodout;
    public GUIButton        bDifficulty;
    public GUIButton        bPrev;
    public GUIButton        bNext;
    public String           textDescription;
    public String[]         textArmyDescription;
    public String           curMissionName;
    public int              curMissionNum  = -1;
    public String           curMapName;
    protected String        briefSound     = null;
    protected Main3D        main;
    protected GUIRenders    renders;
    protected RenderMap2D   renderMap2D;
    protected CameraOrtho2D cameraMap2D;
    protected TTFont        gridFont;
    protected TTFont waypointFont;
    protected float bigFontMultip;
    protected Mat           emptyMat;
    protected float[]       scale          = { 0.064F, 0.032F, 0.016F, 0.0080F, 0.0040F, 0.0020F, 0.0010F, 0.0005F, 0.00025F };
    protected int           scales         = this.scale.length;
    protected int           curScale       = this.scales - 1;
    protected int           curScaleDirect = -1;
    protected float         landDX;
    protected float         landDY;
    private float[]         line2XYZ       = new float[6];
    private int             _gridCount;
    private int[]           _gridX         = new int[20];
    private int[]           _gridY         = new int[20];
    private int[]           _gridVal       = new int[20];
    protected boolean       bLPressed      = false;
    protected boolean       bRPressed      = false;

    // TODO: Added by |ZUTI|
    // -----------------------------------
    public GUIButton bZutiAcPositions;

    // -----------------------------------

    public class DialogClient extends GUIDialogClient {
        public boolean notify(GWindow gwindow, int i, int i1) {
            if (i != 2) return super.notify(gwindow, i, i1);
            if (gwindow == GUIBriefingGeneric.this.bPrev) {
                GUIBriefingGeneric.this.doBack();
                return true;
            }
            if (gwindow == GUIBriefingGeneric.this.bNext) {
                GUIBriefingGeneric.this.doNext();
                return true;
            }
            if (gwindow == GUIBriefingGeneric.this.bDifficulty) {
                GUIBriefingGeneric.this.doDiff();
                return true;
            }
            if (gwindow == GUIBriefingGeneric.this.bLoodout) {
                GUIBriefingGeneric.this.doLoodout();
                return true;
            }
            // TODO: Added by |ZUTI|
            // --------------------------------------
            if (gwindow == GUIBriefingGeneric.this.bZutiAcPositions) {
                com.maddox.il2.game.Main.stateStack().change(44);

                if (Main.cur().netServerParams != null && Main.cur().netServerParams.isMaster())
                    new GWindowMessageBox(Main3D.cur3D().guiManager.root, 20.0F, true, GUIBriefingGeneric.this.i18n("mds.info.crewPositions"), GUIBriefingGeneric.this.i18n("mds.info.crewHostLimit"), 3, 0.0F);

                return true;
            }
            // --------------------------------------

            return super.notify(gwindow, i, i1);
        }

        public void render() {
            super.render();

            GUISeparate.draw(this, GColor.Gray, this.x1024(32.0F), this.y1024(624.0F), this.x1024(924.0F), 2.5F);
            if(bNext.isVisible())
            {
                GUISeparate.draw(this, GColor.Gray, this.x1024(457.0F), this.y1024(686.0F), this.x1024(30.0F), 2.0F);
                GUISeparate.draw(this, GColor.Gray, this.x1024(537.0F), this.y1024(686.0F), this.x1024(30.0F), 2.0F);
//                GUISeparate.draw(this, GColor.Gray, this.x1024(457.0F), this.y1024(640.0F), 1.0F, this.x1024(46.0F));
//                GUISeparate.draw(this, GColor.Gray, this.x1024(567.0F), this.y1024(640.0F), 1.0F, this.x1024(46.0F));
                GUISeparate.draw(this, GColor.Gray, this.x1024(457.0F), this.y1024(640.0F), 1.0F, this.y1024(47.0F));
                GUISeparate.draw(this, GColor.Gray, this.x1024(567.0F), this.y1024(640.0F), 1.0F, this.y1024(47.0F));
            }
            this.setCanvasColorWHITE();
            GUILookAndFeel guilookandfeel = (GUILookAndFeel) this.lookAndFeel();
            guilookandfeel.drawBevel(this, this.x1024(32.0F), this.y1024(32.0F), this.x1024(528.0F), this.y1024(560.0F), guilookandfeel.bevelComboDown, guilookandfeel.basicelements);
            this.setCanvasFont(0);
            this.setCanvasColor(GColor.Gray);

            // TODO: Added by |ZUTI|: render Crew text
            // --------------------------------------------------------
            if (Mission.isDogfight()) {
                float x1 = GUIBriefingGeneric.this.dialogClient.x1024(755.0F);
                float y1 = GUIBriefingGeneric.this.dialogClient.y1024(633.0F);
                float x2 = GUIBriefingGeneric.this.dialogClient.x1024(170.0F);
                float y2 = GUIBriefingGeneric.this.dialogClient.y1024(48.0F);
                GUIBriefingGeneric.this.dialogClient.draw(x1, y1, x2, y2, 2, GUIBriefingGeneric.this.i18n("brief.Crew"));
            }
            // --------------------------------------------------------

            GUIBriefingGeneric.this.clientRender();
        }

        public void resized() {
            super.resized();
            if (GUIBriefingGeneric.this.renders != null) {
                GUILookAndFeel guilookandfeel = (GUILookAndFeel) this.lookAndFeel();
                GBevel gbevel = guilookandfeel.bevelComboDown;
//                System.out.println("x=" + (this.x1024(528.0F) - this.x1024(32.0F)) + ", y=" + (this.y1024(560.0F) - this.y1024(32.0F)) + " --- rel=" + ( (this.x1024(528.0F) - this.x1024(32.0F)) / (this.y1024(560.0F) - this.y1024(32.0F)) )  );
//                System.out.println("xg=" + (this.x1024g(528.0F) - this.x1024g(32.0F)) + ", yg=" + (this.y1024g(560.0F) - this.y1024g(32.0F)) + " --- rel=" + ( (this.x1024g(528.0F) - this.x1024g(32.0F)) / (this.y1024g(560.0F) - this.y1024g(32.0F)) )  );
                GUIBriefingGeneric.this.renders.setPosSize(this.x1024(32.0F) + gbevel.L.dx, this.y1024(32.0F) + gbevel.T.dy, this.x1024(528.0F) - gbevel.L.dx - gbevel.R.dx, this.y1024(560.0F) - gbevel.T.dy - gbevel.B.dy);
//                float x = this.x1024(32.0F) + gbevel.L.dx;
////                float xs = (this.x1024g(528.0F) - gbevel.L.dx - gbevel.R.dx) * 1.2F;
//                float xs = (this.x1024(528.0F) - gbevel.L.dx - gbevel.R.dx);
//                float y = this.y1024(32.0F) + gbevel.T.dy;
////                float ys = (this.y1024g(560.0F) - gbevel.T.dy - gbevel.B.dy) * 1.2F;
//                float ys = (this.y1024(560.0F) - gbevel.T.dy - gbevel.B.dy);
//                System.out.println("x=" + x + ", y=" + y + ", xs=" + xs + ", ys=" + ys + " --- rel=" + ( xs / ys ) + ", root.win.dx=" + this.root.win.dx + ", root.win.dy=" + this.root.win.dy);
//                GUIBriefingGeneric.this.renders.setPosSize(x, y, xs, ys);
            }
        }

        public void setPosSize() {
            this.set1024PosSize(0.0F, 32.0F, 1024.0F, 736.0F);
            GUIBriefingGeneric.this.bPrev.setPosC(this.x1024(85.0F), this.y1024(689.0F));
            GUIBriefingGeneric.this.bDifficulty.setPosC(this.x1024(298.0F), this.y1024(689.0F));
            GUIBriefingGeneric.this.bLoodout.setPosC(this.x1024(768.0F), this.y1024(689.0F));

            // TODO: Added by |ZUTI|: add crew button
            // --------------------------------------------------------------------------------------
            if (Mission.isDogfight()) GUIBriefingGeneric.this.bZutiAcPositions.setPosC(this.x1024(900.0F), this.y1024(689.0F));
            else GUIBriefingGeneric.this.bZutiAcPositions.setPosC(this.x1024(2000.0F), this.y1024(2000.0F));
            // --------------------------------------------------------------------------------------

            GUIBriefingGeneric.this.bNext.setPosC(this.x1024(512.0F), this.y1024(689.0F));
            GUIBriefingGeneric.this.wScrollDescription.setPosSize(this.x1024(592.0F), this.y1024(32.0F), this.x1024(400.0F), this.y1024(560.0F));
            GUIBriefingGeneric.this.clientSetPosSize();
        }
    }

    public class ScrollDescript extends GWindowScrollingDialogClient {
        public void created() {
            this.fixed = GUIBriefingGeneric.this.wDescript = GUIBriefingGeneric.this.createDescript(this);
            this.fixed.bNotify = true;
            this.bNotify = true;
        }

        public boolean notify(GWindow gwindow, int i, int i1) {
            if (super.notify(gwindow, i, i1)) return true;
            this.notify(i, i1);
            return false;
        }

        public void resized() {
            if (GUIBriefingGeneric.this.wDescript != null) GUIBriefingGeneric.this.wDescript.computeSize();
            super.resized();
            if (this.vScroll.isVisible()) {
                GBevel gbevel = ((GUILookAndFeel) this.lookAndFeel()).bevelComboDown;
                this.vScroll.setPos(this.win.dx - this.lookAndFeel().getVScrollBarW() - gbevel.R.dx, gbevel.T.dy);
                this.vScroll.setSize(this.lookAndFeel().getVScrollBarW(), this.win.dy - gbevel.T.dy - gbevel.B.dy);
            }
        }

        public void render() {
            this.setCanvasColorWHITE();
            GBevel gbevel = ((GUILookAndFeel) this.lookAndFeel()).bevelComboDown;
            this.lookAndFeel().drawBevel(this, 0.0F, 0.0F, this.win.dx, this.win.dy, gbevel, ((GUILookAndFeel) this.lookAndFeel()).basicelements, true);
        }
    }

    public class Descript extends GWindowDialogClient {
        public void render() {
            String string = GUIBriefingGeneric.this.textDescription();
            if (string != null) {
                GBevel gbevel = ((GUILookAndFeel) this.lookAndFeel()).bevelComboDown;
                this.setCanvasFont(0);
                this.setCanvasColorBLACK();
                this.root.C.clip.y += gbevel.T.dy;
                this.root.C.clip.dy -= gbevel.T.dy + gbevel.B.dy;
                this.drawLines(gbevel.L.dx + 2.0F, gbevel.T.dy + 2.0F, string, 0, string.length(), this.win.dx - gbevel.L.dx - gbevel.R.dx - 4.0F, this.root.C.font.height);
            }
        }

        public void computeSize() {
            String string = GUIBriefingGeneric.this.textDescription();
            if (string != null) {
                this.win.dx = this.parentWindow.win.dx;
                GBevel gbevel = ((GUILookAndFeel) this.lookAndFeel()).bevelComboDown;
                this.setCanvasFont(0);
                int i = this.computeLines(string, 0, string.length(), this.win.dx - gbevel.L.dx - gbevel.R.dx - 4.0F);
                this.win.dy = this.root.C.font.height * i + gbevel.T.dy + gbevel.B.dy + 4.0F;
                if (this.win.dy > this.parentWindow.win.dy) {
                    this.win.dx = this.parentWindow.win.dx - this.lookAndFeel().getVScrollBarW();
                    i = this.computeLines(string, 0, string.length(), this.win.dx - gbevel.L.dx - gbevel.R.dx - 4.0F);
                    this.win.dy = this.root.C.font.height * i + gbevel.T.dy + gbevel.B.dy + 4.0F;
                }
            } else {
                this.win.dx = this.parentWindow.win.dx;
                this.win.dy = this.parentWindow.win.dy;
            }
        }
    }

    public class RenderMap2D extends Render {
        public void preRender() {
            if (GUIBriefingGeneric.this.main.land2D != null) Front.preRender(false);
        }

        public void render() {
            if (GUIBriefingGeneric.this.main.land2D != null) {
                GUIBriefingGeneric.this.main.land2D.render();
                if (GUIBriefingGeneric.this.main.land2DText != null) GUIBriefingGeneric.this.main.land2DText.render();
                GUIBriefingGeneric.this.drawGrid2D();
                Front.render(false);
                int i = (int) Math.round(32.0 * GUIBriefingGeneric.this.renders.root.win.dx / 1024.0);
                float f = client.root.win.dx / client.root.win.dy;
                float f1 = 1.333333F / f;
                i = (int)((float)i * f1);
                IconDraw.setScrSize(i, i);
                GUIBriefingGeneric.this.doRenderMap2D();
                SquareLabels.draw(GUIBriefingGeneric.this.cameraMap2D, Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.worldOfsX(), Main3D.cur3D().land2D.mapSizeX());
            }
        }

        public RenderMap2D(Renders renders, float f) {
            super(renders, f);
            this.useClearDepth(false);
            this.useClearColor(false);
        }
    }

    public void _enter() {
        this.client.activateWindow();
        try {
            SectFile sectfile = Main.cur().currentMissionFile;
            this.briefSound = sectfile.get("MAIN", "briefSound");
            String string = Main.cur().currentMissionFile.fileName();
            String string_2_ = sectfile.get("MAIN", "MAP");
            if (!string.equals(this.curMissionName) || !string_2_.equals(this.curMapName) || this.curMissionNum != Main.cur().missionCounter || this.main.land2D == null) {
                this.dialogClient.resized();
                this.fillTextDescription();
                this.fillMap();
                Front.loadMission(sectfile);
                this.curMissionName = string;
                this.curMapName = string_2_;
                this.curMissionNum = Main.cur().missionCounter;
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        Front.setMarkersChanged();
        this.wScrollDescription.resized();
        if (this.wScrollDescription.vScroll.isVisible()) this.wScrollDescription.vScroll.setPos(0.0F, true);
    }

    public void _leave() {
        this.client.hideWindow();
    }

    private void setPosCamera(float x, float y) {
        float x1 = (float) ((this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale);
        this.cameraMap2D.worldXOffset = x - x1 / 2.0F;
        float y1 = (float) ((this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale);
        this.cameraMap2D.worldYOffset = y - y1 / 2.0F;
        this.clipCamera();
    }

    private void scaleCamera() {
        this.cameraMap2D.worldScale = this.scale[this.curScale] * this.renders.root.win.dx / 1024.0F;
    }

    private void clipCamera() {
        if (this.cameraMap2D.worldXOffset < -Main3D.cur3D().land2D.worldOfsX()) this.cameraMap2D.worldXOffset = -Main3D.cur3D().land2D.worldOfsX();
        float x = (float) ((this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale);
        if (this.cameraMap2D.worldXOffset > Main3D.cur3D().land2D.mapSizeX() - Main3D.cur3D().land2D.worldOfsX() - x) this.cameraMap2D.worldXOffset = Main3D.cur3D().land2D.mapSizeX() - Main3D.cur3D().land2D.worldOfsX() - x;
        if (this.cameraMap2D.worldYOffset < -Main3D.cur3D().land2D.worldOfsY()) this.cameraMap2D.worldYOffset = -Main3D.cur3D().land2D.worldOfsY();
        float y = (float) ((this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale);
        if (this.cameraMap2D.worldYOffset > Main3D.cur3D().land2D.mapSizeY() - Main3D.cur3D().land2D.worldOfsY() - y) this.cameraMap2D.worldYOffset = Main3D.cur3D().land2D.mapSizeY() - Main3D.cur3D().land2D.worldOfsY() - y;
    }

    private void computeScales() {
        float scaleX = this.renders.win.dx * 1024.0F / this.renders.root.win.dx;
        float scaleY = this.renders.win.dy * 768.0F / this.renders.root.win.dy;
        int i;
        float factor = 0.064F;
        for (i = 0; i < this.scale.length; i++) {
            this.scale[i] = factor;
            float curLandDX = this.landDX * factor;
            if (curLandDX < scaleX) break;
            float curLandDY = this.landDY * factor;
            if (curLandDY < scaleY) break;
            factor /= 2.0F;
        }
        this.scales = i;
        if (this.scales < this.scale.length) {
            float f_11_ = scaleX / this.landDX;
            float f_12_ = scaleY / this.landDY;
            this.scale[i] = f_11_;
            if (f_12_ > f_11_) this.scale[i] = f_12_;
            this.scales = i + 1;
        }
        this.curScale = this.scales - 1;
        this.curScaleDirect = -1;
    }

    private void drawGrid2D() {
        int i = this.gridStep();
        int i_13_ = (int) ((this.cameraMap2D.worldXOffset + Main3D.cur3D().land2D.worldOfsX()) / i);
        int i_14_ = (int) ((this.cameraMap2D.worldYOffset + Main3D.cur3D().land2D.worldOfsY()) / i);
        double d = (this.cameraMap2D.right - this.cameraMap2D.left) / this.cameraMap2D.worldScale;
        double d_15_ = (this.cameraMap2D.top - this.cameraMap2D.bottom) / this.cameraMap2D.worldScale;
        int i_16_ = (int) (d / i) + 2;
        int i_17_ = (int) (d_15_ / i) + 2;
        float f = (float) ((i_13_ * i - this.cameraMap2D.worldXOffset - Main3D.cur3D().land2D.worldOfsX()) * this.cameraMap2D.worldScale + 0.5);
        float f_18_ = (float) ((i_14_ * i - this.cameraMap2D.worldYOffset - Main3D.cur3D().land2D.worldOfsY()) * this.cameraMap2D.worldScale + 0.5);
        float f_19_ = (float) (i_16_ * i * this.cameraMap2D.worldScale);
        float f_20_ = (float) (i_17_ * i * this.cameraMap2D.worldScale);
        float f_21_ = (float) (i * this.cameraMap2D.worldScale);
        this._gridCount = 0;
        Render.drawBeginLines(-1);
        for (int i_22_ = 0; i_22_ <= i_17_; i_22_++) {
            float f_23_ = f_18_ + i_22_ * f_21_;
            int i_24_ = (i_22_ + i_14_) % 10 == 0 ? 192 : 127;
            this.line2XYZ[0] = f;
            this.line2XYZ[1] = f_23_;
            this.line2XYZ[2] = 0.0F;
            this.line2XYZ[3] = f + f_19_;
            this.line2XYZ[4] = f_23_;
            this.line2XYZ[5] = 0.0F;
            Render.drawLines(this.line2XYZ, 2, 1.0F, ~0xffffff | i_24_ << 16 | i_24_ << 8 | i_24_, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
            if (i_24_ == 192) this.drawGridText(0, (int) f_23_, (i_14_ + i_22_) * i);
        }
        for (int i_25_ = 0; i_25_ <= i_16_; i_25_++) {
            float f_26_ = f + i_25_ * f_21_;
            int i_27_ = (i_25_ + i_13_) % 10 == 0 ? 192 : 127;
            this.line2XYZ[0] = f_26_;
            this.line2XYZ[1] = f_18_;
            this.line2XYZ[2] = 0.0F;
            this.line2XYZ[3] = f_26_;
            this.line2XYZ[4] = f_18_ + f_20_;
            this.line2XYZ[5] = 0.0F;
            Render.drawLines(this.line2XYZ, 2, 1.0F, ~0xffffff | i_27_ << 16 | i_27_ << 8 | i_27_, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 0);
            if (i_27_ == 192) this.drawGridText((int) f_26_, 0, (i_13_ + i_25_) * i);
        }
        Render.drawEnd();
        this.drawGridText();
    }

    private int gridStep() {
        float f = this.cameraMap2D.right - this.cameraMap2D.left;
        float f_28_ = this.cameraMap2D.top - this.cameraMap2D.bottom;
        double d = f;
        if (f_28_ < f) d = f_28_;
        d /= this.cameraMap2D.worldScale;
        int i = 100000;
        for (int i_29_ = 0; i_29_ < 5; i_29_++) {
            if (i * 3 <= d) break;
            i /= 10;
        }
        return i;
    }

    private void drawGridText(int i, int i_30_, int i_31_) {
        if (i >= 0 && i_30_ >= 0 && i_31_ > 0 && this._gridCount != 20) {
            this._gridX[this._gridCount] = i;
            this._gridY[this._gridCount] = i_30_;
            this._gridVal[this._gridCount] = i_31_;
            this._gridCount++;
        }
    }

    private void drawGridText() {
        for (int i = 0; i < this._gridCount; i++)
            this.gridFont.output(-4144960, this._gridX[i] + 2, this._gridY[i] + 2, 0.0F, this._gridVal[i] / 1000 + "." + this._gridVal[i] % 1000 / 100);
        this._gridCount = 0;
    }

    protected void doRenderMap2D() {
    }

    protected void doMouseButton(int i, boolean flag, float f, float f1) {
        int i1 = i;
        if (i1 == 0) {
            this.bLPressed = flag;
            GUIRenders guirenders = this.renders;
            int cursorType;
            if (this.bLPressed) {
                cursorType = 7;
            } else {
                cursorType = 3;
            }
            guirenders.mouseCursor = cursorType;
        } else {
            int i2 = i;
            if (i2 == 1 && this.scales > 1) {
                this.bRPressed = flag;
                if (this.bRPressed && !this.bLPressed) {
                    float f2 = (float) (this.cameraMap2D.worldXOffset + f / this.cameraMap2D.worldScale);
                    float f3 = (float) (this.cameraMap2D.worldYOffset + (this.renders.win.dy - f1 - 1.0F) / this.cameraMap2D.worldScale);
                    this.curScale += this.curScaleDirect;
                    if (this.curScaleDirect < 0) {
                        if (this.curScale < 0) {
                            this.curScale = 1;
                            this.curScaleDirect = 1;
                        }
                    } else if (this.curScale == this.scales) {
                        this.curScale = this.scales - 2;
                        this.curScaleDirect = -1;
                    }
                    this.scaleCamera();
                    f2 -= (f - this.renders.win.dx / 2.0F) / this.cameraMap2D.worldScale;
                    f3 += (f1 - this.renders.win.dy / 2.0F) / this.cameraMap2D.worldScale;
                    this.setPosCamera(f2, f3);
                }
            }
        }
    }

    protected void doMouseMove(float f, float f1) {
//        System.out.println("GUIBriefingGeneric doMouseMove(" + f + ", " + f1 + ")");
        if (this.bLPressed && this.renders.mouseCursor == 7) {
            this.cameraMap2D.worldXOffset -= this.renders.root.mouseStep.dx / this.cameraMap2D.worldScale;
            this.cameraMap2D.worldYOffset += this.renders.root.mouseStep.dy / this.cameraMap2D.worldScale;
            this.clipCamera();
        }
    }

    protected void doMouseRelMove(float f, float f1, float f2)
    {
//        System.out.println("GUIBriefingGeneric doMouseRelMove(" + f + ", " + f1 + ", " + f2 + ")");
        if((double)f2 < 0.001D && (double)f2 > -0.001D)
            return;
        if((double)f2 < 0.0D)
            curScaleDirect = 1;
        if((double)f2 > 0.0D)
            curScaleDirect = -1;
        float f3 = renders.root.mousePos.x - renders.win.x - renders.parentWindow.win.x;
        float f4 = renders.root.mousePos.y - renders.win.y - renders.parentWindow.win.y;
        float f5 = (float)(cameraMap2D.worldXOffset + (double)f3 / cameraMap2D.worldScale);
        float f6 = (float)(cameraMap2D.worldYOffset + (double)(renders.win.dy - f4 - 1.0F) / cameraMap2D.worldScale);
        curScale += curScaleDirect;
        if(curScaleDirect < 0)
        {
            if(curScale < 0)
                curScale = 0;
        } else
        if(curScale == scales)
            curScale = scales - 1;
        scaleCamera();
        f5 = (float)((double)f5 - (double)(f3 - renders.win.dx / 2.0F) / cameraMap2D.worldScale);
        f6 = (float)((double)f6 + (double)(f4 - renders.win.dy / 2.0F) / cameraMap2D.worldScale);
        setPosCamera(f5, f6);
    }

    protected void createRenderWindow(GWindow gwindow) {
        this.renders = new GUIRenders(gwindow, 0.0F, 0.0F, 1.0F, 1.0F, false) {
            public void mouseButton(int i, boolean bool, float f, float f1) {
                GUIBriefingGeneric.this.doMouseButton(i, bool, f, f1);
            }

            public void mouseMove(float f, float f1) {
                GUIBriefingGeneric.this.doMouseMove(f, f1);
            }
            
            public void mouseRelMove(float f, float f1, float f2)
            {
                doMouseRelMove(f, f1, f2);
            }
        };
        this.renders.mouseCursor = 3;
        this.renders.bNotify = true;
        this.cameraMap2D = new CameraOrtho2D();
        this.cameraMap2D.worldScale = this.scale[this.curScale];
        this.renderMap2D = new RenderMap2D(this.renders.renders, 1.0F);
        this.renderMap2D.setCamera(this.cameraMap2D);
        this.renderMap2D.setShow(true);
        LightEnvXY lightenvxy = new LightEnvXY();
        this.renderMap2D.setLightEnv(lightenvxy);
        lightenvxy.sun().setLight(0.5F, 0.5F, 1.0F, 1.0F, 1.0F, 0.8F);
        Vector3f vector3f = new Vector3f(1.0F, -2.0F, -1.0F);
        vector3f.normalize();
        lightenvxy.sun().set(vector3f);
        this.gridFont = TTFont.font[1];
        if(World.cur().smallMapWPLabels)
        {
            waypointFont = TTFont.font[0];
            bigFontMultip = 1.0F;
        } else
        {
            waypointFont = TTFont.font[1];
            bigFontMultip = 2.0F;
        }
        this.emptyMat = Mat.New("icons/empty.mat");
        this.main = Main3D.cur3D();
    }

    protected void fillMap() throws Exception {
        SectFile sectfile = Main.cur().currentMissionFile;
        String s = sectfile.get("MAIN", "MAP");
        if (s == null) throw new Exception("No MAP in mission file ");
        SectFile sectfile1 = new SectFile("maps/" + s);
        String s1 = sectfile1.get("MAP", "TypeMap", (String) null);
        if (s1 == null) throw new Exception("Bad MAP description in mission file ");
        NumberTokenizer numbertokenizer = new NumberTokenizer(s1);
        if (numbertokenizer.hasMoreTokens()) {
            numbertokenizer.next();
            if (numbertokenizer.hasMoreTokens()) s1 = numbertokenizer.next();
        }
        s1 = HomePath.concatNames("maps/" + s, s1);
        int[] is = new int[3];
        if (!Mat.tgaInfo(s1, is)) throw new Exception("Bad MAP description in mission file ");
        this.landDX = is[0] * 200.0F;
        this.landDY = is[1] * 200.0F;
        if (this.main.land2D != null) {
            if (!this.main.land2D.isDestroyed()) this.main.land2D.destroy();
            this.main.land2D = null;
        }
        int i = sectfile1.sectionIndex("MAP2D");
        if (i >= 0) {
            int i1 = sectfile1.vars(i);
            if (i1 > 0) {
                this.main.land2D = new Land2Dn(s, this.landDX, this.landDY);
                this.landDX = (float) this.main.land2D.mapSizeX();
                this.landDY = (float) this.main.land2D.mapSizeY();
            }
        }
        if (this.main.land2DText == null) this.main.land2DText = new Land2DText();
        else this.main.land2DText.clear();
        int i2 = sectfile1.sectionIndex("text");
        if (i2 >= 0 && sectfile1.vars(i2) > 0) {
            String s2 = sectfile1.var(i2, 0);
            this.main.land2DText.load(HomePath.concatNames("maps/" + s, s2));
        }
        this.computeScales();
        this.scaleCamera();
        this.setPosCamera(this.landDX / 2.0F, this.landDY / 2.0F);
    }

    protected void prepareTextDescription(int i) {
        if (this.textDescription != null) {
            if (this.textArmyDescription == null || this.textArmyDescription.length != i) this.textArmyDescription = new String[i];
            for (int i_51_ = 0; i_51_ < i; i_51_++) {
                this.textArmyDescription[i_51_] = null;
                this.prepareTextDescriptionArmy(i_51_);
            }
        }
    }

    private void prepareTextDescriptionArmy(int i) {
        String string = (Army.name(i) + ">").toUpperCase();
        int i_52_ = 0;
        int i_53_ = this.textDescription.length();
        StringBuffer stringbuffer = new StringBuffer();
        while (i_52_ < i_53_) {
            int i_54_ = this.textDescription.indexOf("<ARMY", i_52_);
            if (i_54_ >= i_52_) {
                if (i_54_ > i_52_) this.subString(stringbuffer, this.textDescription, i_52_, i_54_);
                int i_55_ = this.textDescription.indexOf("</ARMY>", i_54_);
                if (i_55_ == -1) i_55_ = i_53_;
                for (i_54_ += "<ARMY".length(); i_54_ < i_53_ && Character.isSpaceChar(this.textDescription.charAt(i_54_)); i_54_++);
                if (i_54_ == i_53_) {
                    i_52_ = i_53_;
                    break;
                }
                if (this.textDescription.startsWith(string, i_54_)) {
                    i_54_ += string.length();
                    if (i_54_ < i_55_ && this.textDescription.charAt(i_54_) == '\n') i_54_++;
                    this.subString(stringbuffer, this.textDescription, i_54_, i_55_);
                }
                i_52_ = i_55_ + "</ARMY>".length();
                if (i_52_ < i_53_ && this.textDescription.charAt(i_52_) == '\n') i_52_++;
            } else {
                this.subString(stringbuffer, this.textDescription, i_52_, i_53_);
                i_52_ = i_53_;
            }
        }
        this.textArmyDescription[i] = new String(stringbuffer);
    }

    private void subString(StringBuffer stringbuffer, String string, int i, int i_56_) {
        while (i < i_56_)
            stringbuffer.append(string.charAt(i++));
    }

    private void setMissionDate(String s)
    {
        String s1 = s;
        if(Main.cur().currentMissionFile == null)
            return;
        if(Main.cur().currentMissionFile.sectionExist("SEASON"))
            return;
        try
        {
            s = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("."));
            int i = Integer.parseInt(s);
            int j = i / 10000 + 1940;
            i %= 10000;
            int k = i / 100;
            i %= 100;
            int l = i;
            Main.cur().currentMissionFile.sectionAdd("SEASON");
            int i1 = Main.cur().currentMissionFile.sectionIndex("SEASON");
            Main.cur().currentMissionFile.lineAdd(i1, "Year", "" + j);
            Main.cur().currentMissionFile.lineAdd(i1, "Month", "" + k);
            Main.cur().currentMissionFile.lineAdd(i1, "Day", "" + l);
            Main.cur().currentMissionFile.saveFile(s1);
        }
        catch(Exception exception) { }
    }

    protected void fillTextDescription() {
        try {
            String s = Main.cur().currentMissionFile.fileName();
            if(Main.cur().campaign != null && Main.cur().campaign.isDGen())
                setMissionDate(s);
            for (int i = s.length() - 1; i > 0; i--) {
                char c = s.charAt(i);
                if (c == '\\' || c == '/') break;
                if (c == '.') {
                    s = s.substring(0, i);
                    break;
                }
            }
            ResourceBundle resourcebundle = ResourceBundle.getBundle(s, RTSConf.cur.locale);
            this.textDescription = resourcebundle.getString("Description");
        } catch (Exception exception) {
            this.textDescription = null;
            this.textArmyDescription = null;
        }
    }

    protected String textDescription() {
        return this.textDescription;
    }

    protected Descript createDescript(GWindow gwindow) {
        return (Descript) gwindow.create(new Descript());
    }

    protected void doNext() {

    }

    protected void doDiff() {

    }

    protected void doBack() {

    }

    protected void doLoodout() {

    }

    protected void clientRender() {

    }

    protected void clientSetPosSize() {

    }

    protected void clientInit(GWindowRoot gwindowroot) {

    }

    protected String infoMenuInfo() {
        return "Info Menu";
    }

    protected void init(GWindowRoot gwindowroot) {
        this.client = (GUIClient) gwindowroot.create(new GUIClient());
        this.dialogClient = (DialogClient) this.client.create(new DialogClient());
        this.infoMenu = (GUIInfoMenu) this.client.create(new GUIInfoMenu());
        this.infoMenu.info = this.infoMenuInfo();
        this.infoName = (GUIInfoName) this.client.create(new GUIInfoName());
        this.createRenderWindow(this.dialogClient);
        this.dialogClient.create(this.wScrollDescription = new ScrollDescript());
        GTexture gtexture = ((GUILookAndFeel) gwindowroot.lookAndFeel()).buttons2;
        this.bPrev = (GUIButton) this.dialogClient.addEscape(new GUIButton(this.dialogClient, gtexture, 0.0F, 96.0F, 48.0F, 48.0F));
        this.bDifficulty = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F));
        this.bLoodout = (GUIButton) this.dialogClient.addControl(new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F));

        // TODO: Added by |ZUTI|
        // -----------------------------------
        this.bZutiAcPositions = new GUIButton(this.dialogClient, gtexture, 0.0F, 48.0F, 48.0F, 48.0F);
        this.dialogClient.addControl(this.bZutiAcPositions);
        // -----------------------------------

        this.bNext = (GUIButton) this.dialogClient.addDefault(new GUIButton(this.dialogClient, gtexture, 0.0F, 192.0F, 48.0F, 48.0F));
        this.clientInit(gwindowroot);
        this.dialogClient.activateWindow();
        this.client.hideWindow();
    }

    public GUIBriefingGeneric(int i) {
        super(i);
        curMissionNum = -1;
        briefSound = null;
        bigFontMultip = 1.0F;
        scales = scale.length;
        curScale = scales - 1;
        curScaleDirect = -1;
        line2XYZ = new float[6];
        _gridX = new int[20];
        _gridY = new int[20];
        _gridVal = new int[20];
        bLPressed = false;
        bRPressed = false;
    }
}