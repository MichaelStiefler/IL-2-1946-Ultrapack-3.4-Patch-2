/*
 * 4.13.1 Class, backported to UP3 by SAS~Storebror
 * 4.11+ TrackIR implementation by SAS~Storebror
 */

package com.maddox.rts;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.maddox.util.HashMapExt;
import com.maddox.util.HashMapInt;
import com.maddox.util.HashMapIntEntry;

public class HotKeyEnv {

    public final String name() {
        return this.name;
    }

    public final boolean isEnabled() {
        return this.bEnabled;
    }

    public static boolean isEnabled(String name) {
        HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.envs.get(name);
        if (hotkeyenv == null) return false;
        else return hotkeyenv.bEnabled;
    }

    public final void enable(boolean isEnabled) {
        this.bEnabled = isEnabled;
    }

    public static void enable(String name, boolean isEnabled) {
        HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.envs.get(name);
        if (hotkeyenv == null) return;
        else {
            hotkeyenv.bEnabled = isEnabled;
            return;
        }
    }

    private int hotKeys(int hiWord, int loWord) {
        return (hiWord & 0xffff) << 16 | loWord & 0xffff;
    }

    public void add(int hiWord, int loWord, String hotKeyName) {
        this.keys.put(this.hotKeys(hiWord, loWord), hotKeyName);
    }

    public static void addHotKey(int hiWord, int loWord, String hotKeyName) {
        RTSConf.cur.hotKeyEnvs.cur.add(hiWord, loWord, hotKeyName);
    }

    public static void addHotKey(String envName, int hiWord, int loWord, String hotKeyName) {
        HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.envs.get(envName);
        if (hotkeyenv == null) hotkeyenv = new HotKeyEnv(envName);
        hotkeyenv.add(hiWord, loWord, hotKeyName);
    }

    public void remove(int hiWord, int loWord) {
        this.keys.remove(this.hotKeys(hiWord, loWord));
    }

    public void remove(String hotKeyName) {
        if (hotKeyName == null) return;
        boolean hotKeyFound = true;
        while (hotKeyFound) {
            hotKeyFound = false;
            for (HashMapIntEntry hashmapintentry = this.keys.nextEntry(null); hashmapintentry != null; hashmapintentry = this.keys.nextEntry(hashmapintentry)) {
                int keyHashEntryWord = hashmapintentry.getKey();
                int hiWord = keyHashEntryWord >> 16 & 0xffff;
                int loWord = keyHashEntryWord & 0xffff;
                String foundHotKeyName = (String) hashmapintentry.getValue();
                if (!hotKeyName.equals(foundHotKeyName)) continue;
                this.remove(hiWord, loWord);
                hotKeyFound = true;
                break;
            }

        }
    }

    public int find(String hotKeyName) {
        for (HashMapIntEntry hashmapintentry = this.keys.nextEntry(null); hashmapintentry != null; hashmapintentry = this.keys.nextEntry(hashmapintentry)) {
            String foundHotKeyName = (String) hashmapintentry.getValue();
            if (hotKeyName.equals(foundHotKeyName)) {
                int i = hashmapintentry.getKey();
                return i;
            }
        }

        return 0;
    }

    public String get(int hiWord, int loWord) {
        return (String) this.keys.get(this.hotKeys(hiWord, loWord));
    }

    public final HashMapInt all() {
        return this.keys;
    }

    public static final void setCurrentEnv(String hotKeyEnvName) {
        HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.envs.get(hotKeyEnvName);
        if (hotkeyenv == null) hotkeyenv = new HotKeyEnv(hotKeyEnvName);
        RTSConf.cur.hotKeyEnvs.cur = hotkeyenv;
    }

    public static final HotKeyEnv currentEnv() {
        return RTSConf.cur.hotKeyEnvs.cur;
    }

    public static final List allEnv() {
        return RTSConf.cur.hotKeyEnvs.lst;
    }

    public static final HotKeyEnv env(String hotKeyEnvName) {
        return (HotKeyEnv) RTSConf.cur.hotKeyEnvs.envs.get(hotKeyEnvName);
    }

    public static String key2Text(int hotKeyIndex) {
        if (hotKeyIndex >= VK.ALL_KEYS) return "User" + (hotKeyIndex - VK.ALL_KEYS);
        else return VK.getKeyText(hotKeyIndex);
    }

    public static int text2Key(String hotKeyName) {
        if (hotKeyName.startsWith("User")) {
            String s1 = hotKeyName.substring("User".length());
            return Integer.parseInt(s1) + VK.ALL_KEYS;
        } else return VK.getKeyFromText(hotKeyName);
    }

    public static void fromIni(String hotKeyEnvName, IniFile inifile, String hotKeyName) {
        String hotKeyCandidates[] = inifile.getVariables(hotKeyName);
        setCurrentEnv(hotKeyEnvName);
        if (hotKeyCandidates == null) return;
        for (int candidateIndex = 0; candidateIndex < hotKeyCandidates.length; candidateIndex++) {
            StringTokenizer stringtokenizer = new StringTokenizer(hotKeyCandidates[candidateIndex]);
            String candidateName = inifile.getValue(hotKeyName, hotKeyCandidates[candidateIndex]);
            if (candidateName.length() <= 0 || !stringtokenizer.hasMoreTokens()) continue;
            String candidateText = stringtokenizer.nextToken();
            int foundHotKey = text2Key(candidateText);
            if (foundHotKey == 0) System.err.println("INI: HotKey '" + hotKeyCandidates[candidateIndex] + "' is unknown");
            else if (stringtokenizer.hasMoreTokens()) {
                String candidateText2 = stringtokenizer.nextToken();
                int foundHotKey2 = text2Key(candidateText2);
                if (foundHotKey2 == 0) System.err.println("INI: HotKey '" + hotKeyCandidates[candidateIndex] + "' is unknown");
                else addHotKey(foundHotKey, foundHotKey2, candidateName);
            } else addHotKey(0, foundHotKey, candidateName);
        }

    }

    public static void toIni(String hotKeyEnvName, IniFile inifile, String s1) {
        HotKeyEnv hotkeyenv = env(hotKeyEnvName);
        if (hotkeyenv == null) {
            System.err.println("INI: HotKey environment '" + hotKeyEnvName + "' not present");
            return;
        }
        for (HashMapIntEntry hashmapintentry = hotkeyenv.keys.nextEntry(null); hashmapintentry != null; hashmapintentry = hotkeyenv.keys.nextEntry(hashmapintentry)) {
            int i = hashmapintentry.getKey();
            int j = i >> 16 & 0xffff;
            int k = i & 0xffff;
            String s2 = (String) hashmapintentry.getValue();
            if (k >= VK.ALL_KEYS) inifile.setValue(s1, "User" + (k - VK.ALL_KEYS), s2);
            else if (j != 0) inifile.setValue(s1, key2Text(j) + " " + key2Text(k), s2);
            else inifile.setValue(s1, key2Text(k), s2);
        }

    }

    public static void tick(boolean flag) {
        HashMapExt hashmapext = RTSConf.cur.hotKeyEnvs.active;
        for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
            HotKeyCmd hotkeycmd = (HotKeyCmd) entry.getKey();
            if (hotkeycmd.isTickInTime(flag)) hotkeycmd.play();
        }

    }

    private final boolean startCmd(boolean flag, int i, int j) {
        HashMapExt hashmapext = RTSConf.cur.hotKeyEnvs.active;
        int k = this.hotKeys(i, j);
        String s = (String) this.keys.get(k);
        if (s == null) return false;
        HotKeyCmd hotkeycmd = this.hotKeyCmdEnv.get(s);
        if (hotkeycmd == null || hotkeycmd.isActive() || !hotkeycmd.isEnabled() || hotkeycmd.isRealTime() != flag || Time.isPaused() && hotkeycmd.isDisableIfTimePaused()) return false;
        hashmapext.put(hotkeycmd, null);
        RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd, true, true);
        hotkeycmd.start(i, j);
        RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd, true, false);
        return true;
    }

    public static final void keyPress(boolean flag, int i, boolean flag1) {
        int j = flag ? 1 : 0;
        HashMapInt hashmapint = RTSConf.cur.hotKeyEnvs.keyState[j];
        HashMapExt hashmapext = RTSConf.cur.hotKeyEnvs.active;
        boolean flag2 = hashmapint.containsKey(i);
        if (flag1 && !flag2) {
            hashmapint.put(i, null);
            boolean flag3 = false;
            for (int k = 0; k < RTSConf.cur.hotKeyEnvs.lst.size(); k++) {
                HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.lst.get(k);
                if (!hotkeyenv.bEnabled || !hotkeyenv.hotKeyCmdEnv.isEnabled()) continue;
                for (HashMapIntEntry hashmapintentry = hashmapint.nextEntry(null); hashmapintentry != null; hashmapintentry = hashmapint.nextEntry(hashmapintentry))
                    for (HashMapIntEntry hashmapintentry1 = hashmapint.nextEntry(hashmapintentry); hashmapintentry1 != null; hashmapintentry1 = hashmapint.nextEntry(hashmapintentry1))
                        if (hotkeyenv.startCmd(flag, hashmapintentry.getKey(), hashmapintentry1.getKey()) || hotkeyenv.startCmd(flag, hashmapintentry1.getKey(), hashmapintentry.getKey())) flag3 = true;

            }

            if (!flag3) for (int i1 = 0; i1 < RTSConf.cur.hotKeyEnvs.lst.size(); i1++) {
                HotKeyEnv hotkeyenv1 = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.lst.get(i1);
                if (!hotkeyenv1.bEnabled || !hotkeyenv1.hotKeyCmdEnv.isEnabled()) continue;
                for (HashMapIntEntry hashmapintentry2 = hashmapint.nextEntry(null); hashmapintentry2 != null; hashmapintentry2 = hashmapint.nextEntry(hashmapintentry2))
                    hotkeyenv1.startCmd(flag, 0, hashmapintentry2.getKey());

            }
        } else if (!flag1 && flag2) {
            for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
                HotKeyCmd hotkeycmd = (HotKeyCmd) entry.getKey();
                if ((hotkeycmd.modifierKey == i || hotkeycmd.key == i) && hotkeycmd.isRealTime() == flag) removed.add(hotkeycmd);
            }

            for (int l = 0; l < removed.size(); l++) {
                HotKeyCmd hotkeycmd1 = (HotKeyCmd) removed.get(l);
                hashmapext.remove(hotkeycmd1);
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd1, false, true);
                hotkeycmd1.stop();
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd1, false, false);
            }

            removed.clear();
            hashmapint.remove(i);
        }
    }

    public static final void keyPress(boolean flag, int i, int j, boolean flag1) {
        int k = flag ? 1 : 0;
        HashMapInt hashmapint = RTSConf.cur.hotKeyEnvs.keyState[k];
        HashMapExt hashmapext = RTSConf.cur.hotKeyEnvs.active;
        int l = i | j << 16;
        boolean flag2 = hashmapint.containsKey(l);
        if (flag1 && !flag2) {
            hashmapint.put(l, null);
            for (int i1 = 0; i1 < RTSConf.cur.hotKeyEnvs.lst.size(); i1++) {
                HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.lst.get(i1);
                if (hotkeyenv.bEnabled && hotkeyenv.hotKeyCmdEnv.isEnabled()) {
                    hotkeyenv.startCmd(flag, i, j);
                    hotkeyenv.startCmd(flag, j, i);
                }
            }

        } else if (!flag1 && flag2) {
            for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
                HotKeyCmd hotkeycmd = (HotKeyCmd) entry.getKey();
                if ((hotkeycmd.modifierKey == i && hotkeycmd.key == j || hotkeycmd.modifierKey == j && hotkeycmd.key == i) && hotkeycmd.isRealTime() == flag) removed.add(hotkeycmd);
            }

            for (int j1 = 0; j1 < removed.size(); j1++) {
                HotKeyCmd hotkeycmd1 = (HotKeyCmd) removed.get(j1);
                hashmapext.remove(hotkeycmd1);
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd1, false, true);
                hotkeycmd1.stop();
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd1, false, false);
            }

            removed.clear();
            hashmapint.remove(l);
        }
    }

    public static final void mouseMove(boolean flag, int i, int j, int k) {
        for (int l = 0; l < RTSConf.cur.hotKeyCmdEnvs.lst.size(); l++) {
            HotKeyCmdEnv hotkeycmdenv = (HotKeyCmdEnv) RTSConf.cur.hotKeyCmdEnvs.lst.get(l);
            if (!hotkeycmdenv.isEnabled() || !hotkeycmdenv.hotKeyEnv().bEnabled) continue;
            HashMapExt hashmapext = hotkeycmdenv.all();
            for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
                HotKeyCmd hotkeycmd = (HotKeyCmd) entry.getValue();
                if (!(hotkeycmd instanceof HotKeyCmdMouseMove) || !hotkeycmd.isEnabled() || hotkeycmd.isRealTime() != flag || Time.isPaused() && hotkeycmd.isDisableIfTimePaused()) continue;
                HotKeyCmdMouseMove hotkeycmdmousemove = (HotKeyCmdMouseMove) hotkeycmd;
                hotkeycmdmousemove.setMove(i, j, k);
                if (Mouse.adapter().isInvert()) hotkeycmdmousemove.prepareInvert();
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmdmousemove, true, true);
                hotkeycmdmousemove.doMove();
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmdmousemove, true, false);
            }
        }

    }

    private final void doCmdMove(boolean flag, int i, int j, int k) {
        int l = this.hotKeys(i, j);
        String s = (String) this.keys.get(l);
        if (s == null) return;
        HotKeyCmd hotkeycmd = this.hotKeyCmdEnv.get(s);
        if (hotkeycmd == null || !(hotkeycmd instanceof HotKeyCmdMove) || !hotkeycmd.isEnabled() || hotkeycmd.isRealTime() != flag || Time.isPaused() && hotkeycmd.isDisableIfTimePaused()) return;
        ((HotKeyCmdMove) hotkeycmd).setMove(k);
        RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd, true, true);
        hotkeycmd.start(i, j);
        RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd, true, false);
        RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd, false, true);
        hotkeycmd.stop();
        RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmd, false, false);
    }

    public static final void joyMove(boolean flag, int i, int j, int k) {
        for (int l = 0; l < RTSConf.cur.hotKeyEnvs.lst.size(); l++) {
            HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.lst.get(l);
            if (hotkeyenv.bEnabled && hotkeyenv.hotKeyCmdEnv.isEnabled()) {
                hotkeyenv.doCmdMove(flag, i, j, k);
                hotkeyenv.doCmdMove(flag, j, i, k);
            }
        }

    }

    public static final void mouseAbsMove(boolean flag, int i, int j, int k) {
        if (k == 0) return;
        for (int l = 0; l < RTSConf.cur.hotKeyEnvs.lst.size(); l++) {
            HotKeyEnv hotkeyenv = (HotKeyEnv) RTSConf.cur.hotKeyEnvs.lst.get(l);
            if (hotkeyenv.bEnabled && hotkeyenv.hotKeyCmdEnv.isEnabled()) {
                hotkeyenv.doCmdMove(flag, VK.MOUSEAXE_Z, 0, k);
                hotkeyenv.doCmdMove(flag, 0, VK.MOUSEAXE_Z, k);
            }
        }

    }

    public static final void keyRedirect(boolean flag, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
        for (int j2 = 0; j2 < RTSConf.cur.hotKeyCmdEnvs.lst.size(); j2++) {
            HotKeyCmdEnv hotkeycmdenv = (HotKeyCmdEnv) RTSConf.cur.hotKeyCmdEnvs.lst.get(j2);
            HashMapExt hashmapext = hotkeycmdenv.all();
            for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
                HotKeyCmd hotkeycmd = (HotKeyCmd) entry.getValue();
                if (!(hotkeycmd instanceof HotKeyCmdRedirect) || !hotkeycmd.isEnabled() || hotkeycmd.isRealTime() != flag || Time.isPaused() && hotkeycmd.isDisableIfTimePaused()) continue;
                HotKeyCmdRedirect hotkeycmdredirect = (HotKeyCmdRedirect) hotkeycmd;
                if (i != hotkeycmdredirect.idRedirect()) continue;
                hotkeycmdredirect.setRedirect(j, k, l, i1, j1, k1, l1, i2);
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmdredirect, true, true);
                hotkeycmdredirect.doRedirect();
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmdredirect, true, false);
            }

        }

    }

    // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
    public static final void trackIRAngles(boolean flag, float yaw, float pitch, float roll, float headX, float headY, float headZ) {
        for (int i = 0; i < RTSConf.cur.hotKeyCmdEnvs.lst.size(); i++) {
            HotKeyCmdEnv hotkeycmdenv = (HotKeyCmdEnv) RTSConf.cur.hotKeyCmdEnvs.lst.get(i);
            if (!hotkeycmdenv.isEnabled() || !hotkeycmdenv.hotKeyEnv().bEnabled) continue;
            HashMapExt hashmapext = hotkeycmdenv.all();
            for (java.util.Map.Entry entry = hashmapext.nextEntry(null); entry != null; entry = hashmapext.nextEntry(entry)) {
                HotKeyCmd hotkeycmd = (HotKeyCmd) entry.getValue();
                if (!(hotkeycmd instanceof HotKeyCmdTrackIRAngles) || !hotkeycmd.isEnabled() || hotkeycmd.isRealTime() != flag || Time.isPaused() && hotkeycmd.isDisableIfTimePaused()) continue;
                HotKeyCmdTrackIRAngles hotkeycmdtrackirangles = (HotKeyCmdTrackIRAngles) hotkeycmd;
                hotkeycmdtrackirangles.setAngles(yaw, pitch, roll, headX, headY, headZ);
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmdtrackirangles, true, true);
                hotkeycmdtrackirangles.doAngles();
                RTSConf.cur.hotKeyCmdEnvs.post(hotkeycmdtrackirangles, true, false);
            }

        }

    }
    // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---

    // Original 4.10/UP3 method for backward compatibility
    public static final void trackIRAngles(boolean flag, float yaw, float pitch, float roll) {
        trackIRAngles(flag, yaw, pitch, roll, 0F, 0F, 0F);
    }

    public HotKeyCmdEnv hotKeyCmdEnv() {
        return this.hotKeyCmdEnv;
    }

    protected HotKeyEnv(String s) {
        this.bEnabled = true;
        this.name = s;
        this.keys = new HashMapInt();
        RTSConf.cur.hotKeyEnvs.envs.put(s, this);
        RTSConf.cur.hotKeyEnvs.lst.add(this);
        this.hotKeyCmdEnv = (HotKeyCmdEnv) RTSConf.cur.hotKeyCmdEnvs.envs.get(s);
        if (this.hotKeyCmdEnv == null) this.hotKeyCmdEnv = new HotKeyCmdEnv(s);
    }

    public static final String DEFAULT = "default";
    private static ArrayList   removed = new ArrayList();
    private String             name;
    private boolean            bEnabled;
    private HashMapInt         keys;
    private HotKeyCmdEnv       hotKeyCmdEnv;

}
