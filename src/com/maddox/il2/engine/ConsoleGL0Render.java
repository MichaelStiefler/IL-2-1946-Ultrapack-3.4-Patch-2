package com.maddox.il2.engine;

import java.util.List;

import com.maddox.rts.MainWin32;
import com.maddox.rts.RTSConf;
import com.maddox.rts.RTSConfWin;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.BaseGameVersion;

class ConsoleGL0Render extends Render {

    public void exlusiveDraw() {
        if (RTSConf.cur instanceof RTSConfWin) ((MainWin32) RTSConf.cur.mainWindow).loopMsgs();
        GObj.DeleteCppObjects();
        this.renders().paint(this);
    }

    public void exlusiveDrawStep(String s, int i) {
        this.sstep = s;
        this.renders().paint(this);
    }

    public void render() {
        if (ConsoleGL0.backgroundMat != null) {
            drawTile(0.0F, 0.0F, RendersMain.getViewPortWidth(), RendersMain.getViewPortHeight(), 0.0F, ConsoleGL0.backgroundMat, -1, 0.0F, 1.0F, 1.0F, -1F);
            if (this.sstep != null) {
                int i = 0xff0000ff;
                String selectorInfo = BaseGameVersion.selectorInfo(BaseGameVersion.SELECTOR_INFO_PRODUCT_VERSION);
                if (selectorInfo.equalsIgnoreCase("N/A")) selectorInfo = "";
                else selectorInfo = " \u00B7 Selector Version " + selectorInfo;
//                if ("ru".equalsIgnoreCase(RTSConf.cur.locale.getLanguage())) {
//                    TTFont.font[2].output(i, RendersMain.getViewPortWidth() * 0.083F, RendersMain.getViewPortHeight() * 0.12F, 0.0F, this.sstep);
//                    TTFont.font[2].output(i, RendersMain.getViewPortWidth() * 0.083F, (RendersMain.getViewPortHeight() * 0.12F + TTFont.font[2].height()) - TTFont.font[2].descender(), 0.0F, "UP 3.0 RC4 Patch Pack " + NetUser.PATCH_LEVEL + selectorInfo);
//                } else {
//                    TTFont.font[2].output(i, RendersMain.getViewPortWidth() * 0.02F, RendersMain.getViewPortHeight() * 0.17F, 0.0F, this.sstep);
//                    TTFont.font[2].output(i, RendersMain.getViewPortWidth() * 0.02F, (RendersMain.getViewPortHeight() * 0.17F + TTFont.font[2].height()) - TTFont.font[2].descender(), 0.0F, "UP 3.0 RC4 Patch Pack " + NetUser.PATCH_LEVEL + selectorInfo);
//                }

                String versionString = VERSION_STRING2 + Config.getPatchLevel() + " \u00B7 Hotfix " + Config.getHotfixVersion() + selectorInfo;
                
                float posX = 0.11F;
                float posY = 0.02F;

                for (int offset = 5; offset > 0; offset--) {
                    TTFont.font[2].output(0x33000000 * (6 - offset), RendersMain.getViewPortWidth() * posX + offset, RendersMain.getViewPortHeight() * posY - offset, 0.0F, this.sstep);
                    TTFont.font[2].output(0x33000000 * (6 - offset), RendersMain.getViewPortWidth() * posX + offset, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() - TTFont.font[2].descender() - offset, 0.0F, versionString);
                    TTFont.font[2].output(0x33000000 * (6 - offset), RendersMain.getViewPortWidth() * posX + offset, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() * 2 - TTFont.font[2].descender() * 2 - offset, 0.0F, VERSION_STRING1);
                }
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX + 1F, RendersMain.getViewPortHeight() * posY + 1F, 0.0F, this.sstep);
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX + 1F, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() - TTFont.font[2].descender() + 1F, 0.0F, versionString);
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX + 1F, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() * 2 - TTFont.font[2].descender() * 2 + 1F, 0.0F, VERSION_STRING1);

                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX - 1F, RendersMain.getViewPortHeight() * posY - 1F, 0.0F, this.sstep);
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX - 1F, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() - TTFont.font[2].descender() - 1F, 0.0F, versionString);
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX - 1F, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() * 2 - TTFont.font[2].descender() * 2 - 1F, 0.0F, VERSION_STRING1);

                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX - 1F, RendersMain.getViewPortHeight() * posY + 1F, 0.0F, this.sstep);
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX - 1F, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() - TTFont.font[2].descender() + 1F, 0.0F, versionString);
                TTFont.font[2].output(0xff000000, RendersMain.getViewPortWidth() * posX - 1F, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() * 2 - TTFont.font[2].descender() * 2 + 1F, 0.0F, VERSION_STRING1);

                TTFont.font[2].output(i, RendersMain.getViewPortWidth() * posX, RendersMain.getViewPortHeight() * posY, 0.0F, this.sstep);
                TTFont.font[2].output(i, RendersMain.getViewPortWidth() * posX, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() - TTFont.font[2].descender(), 0.0F, versionString);
                TTFont.font[2].output(i, RendersMain.getViewPortWidth() * posX, RendersMain.getViewPortHeight() * posY + TTFont.font[2].height() * 2 - TTFont.font[2].descender() * 2, 0.0F, VERSION_STRING1);

            }
            return;
        }
        if (ConsoleGL0.bActive || ConsoleGL0.consoleListener != null) {
            List list = RTSConf.cur.console.historyOut();
            int j = RTSConf.cur.console.startHistoryOut();
            if (!RTSConf.cur.console.isShowHistoryOut()) {
                list = RTSConf.cur.console.historyCmd();
                j = RTSConf.cur.console.startHistoryCmd();
            }
            int k = this.getViewPortHeight() / ConsoleGL0.font.height() - 1;
            if (ConsoleGL0.bActive) {
                int l = RTSConf.cur.console.editBuf.length();
                String s = RTSConf.cur.console.getPrompt();
                int l1 = s.length();
                int i2 = 0;
                if (l1 != 0) s.getChars(0, l1, this.buf, 0);
                if (l + l1 > 0) {
                    if (l + l1 > this.buf.length) this.buf = new char[l + l1 + 16];
                    if (l != 0) {
                        RTSConf.cur.console.editBuf.getChars(0, l, this.buf, l1);
                        float f2 = this.getViewPortWidth() - ConsoleGL0.typeOffset;
                        do {
                            float f4 = ConsoleGL0.font.width(this.buf, 0, RTSConf.cur.console.editPos - i2 + l1);
                            if (f4 < f2) break;
                            i2++;
                            RTSConf.cur.console.editBuf.getChars(i2, l, this.buf, l1);
                        } while (true);
                    }
                    ConsoleGL0.font.output(-1, ConsoleGL0.typeOffset, -ConsoleGL0.font.descender(), 0.0F, this.buf, 0, l - i2 + l1);
                }
                if (Time.endReal() / 500L % 3L != 0L) {
                    float f3 = 0.0F;
                    if (RTSConf.cur.console.editPos + l1 > 0) f3 = ConsoleGL0.font.width(this.buf, 0, RTSConf.cur.console.editPos - i2 + l1);
                    this.buf[0] = '|';
                    ConsoleGL0.font.output(-1, ConsoleGL0.typeOffset + f3 - 1.0F, -ConsoleGL0.font.descender(), 0.0F, this.buf, 0, 1);
                }
            }
            if (RTSConf.cur.console.bWrap) {
                int i1 = j;
                int k1 = 1;
                for (; k > 0 && i1 < list.size(); i1++) {
                    String s1 = (String) list.get(i1);
                    int j2;
                    for (j2 = s1.length() - 1; j2 >= 0 && s1.charAt(j2) < ' '; j2--)
                        ;
                    if (j2 > 0) {
                        j2++;
                        int l2 = 0;
                        int i3 = 0;
                        int j3 = j2;
                        this.ofs[l2] = i3;
                        do {
                            if (j3 <= 0 || ConsoleGL0.font.width(s1, i3, j3) <= 0.0F) break;
                            int k3 = j3;
                            do {
                                if (ConsoleGL0.font.width(s1, i3, j3) + ConsoleGL0.typeOffset <= this.getViewPortWidth()) break;
                                while (--j3 > 0 && s1.charAt(i3 + j3) != ' ')
                                    ;
                            } while (j3 != 0);
                            if (j3 == 0) for (j3 = k3; ConsoleGL0.font.width(s1, i3, j3) + ConsoleGL0.typeOffset > this.getViewPortWidth() && --j3 != 0;)
                                ;
                            if (l2 + 1 >= this.ofs.length) break;
                            l2++;
                            if (j3 == 0) {
                                this.ofs[l2] = j2;
                                break;
                            }
                            i3 += j3;
                            j3 = j2 - i3;
                            this.ofs[l2] = i3;
                        } while (true);
                        for (; l2 > 0 && k > 0; k--) {
                            ConsoleGL0.font.output(-1, ConsoleGL0.typeOffset, ConsoleGL0.font.height() * k1 - ConsoleGL0.font.descender(), 0.0F, s1, this.ofs[l2 - 1], this.ofs[l2] - this.ofs[l2 - 1]);
                            k1++;
                            l2--;
                        }

                    }
                }

            } else {
                if (k > list.size() - j) k = list.size() - j;
                for (int j1 = 0; j1 < k; j1++) {
                    float f1 = ConsoleGL0.font.height() * (j1 + 1) - ConsoleGL0.font.descender();
                    String s2 = (String) list.get(j1 + j);
                    int k2;
                    for (k2 = s2.length() - 1; k2 >= 0 && s2.charAt(k2) < ' '; k2--)
                        ;
                    if (k2 > 0) ConsoleGL0.font.output(-1, ConsoleGL0.typeOffset, f1, 0.0F, s2, 0, k2 + 1);
                }

            }
        }
    }

    public ConsoleGL0Render(float f) {
        super(f);
        VERSION_STRING1 = "IL-2 1946 v" + Config.getCoreVersionNumber() + "m";
        VERSION_STRING2 = Config.getVersionString() + " " + Config.getVersionNumber() + " \"" + Config.getVersionName() + "\" \u00B7 Patch ";
        this.buf = new char[128];
        this.ofs = new int[128];
        this.sstep = null;
        this.useClearDepth(false);
        this.useClearColor(false);
    }

    public static final int COLOR = -1;
    private char            buf[];
    private int             ofs[];
    public String           sstep;

    private static String   VERSION_STRING1;// = "IL-2 1946 v" + Config.CORE_VERSION_NUMBER + "m";
    private static String   VERSION_STRING2;// = Config.VERSION_STRING + " " + Config.VERSION_NUMBER + " \"" + Config.VERSION_NAME + "\" \u00B7 Patch ";

}
