/* 4.10.1 class */
package com.maddox.il2.net;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Tuple3d;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.Trigger;
import com.maddox.il2.ai.UserCfg;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Point_Stay;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.ActorPos;
import com.maddox.il2.engine.BmpUtils;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.game.GameState;
import com.maddox.il2.game.Main;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.game.Selector;
import com.maddox.il2.game.ZutiAircraftCrewManagement;
import com.maddox.il2.game.ZutiSupportMethods;
import com.maddox.il2.game.order.OrdersTree;
import com.maddox.il2.gui.GUIAirArming;
import com.maddox.il2.gui.GUINetAircraft;
import com.maddox.il2.gui.GUINetClientCBrief;
import com.maddox.il2.gui.GUINetClientDBrief;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.NetGunner;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.Finger;
import com.maddox.rts.HomePath;
import com.maddox.rts.MsgAction;
import com.maddox.rts.MsgInvokeMethod_Object;
import com.maddox.rts.MsgNet;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetChannelInStream;
import com.maddox.rts.NetChannelOutStream;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetFilter;
import com.maddox.rts.NetHost;
import com.maddox.rts.NetMsgFiltered;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetMsgSpawn;
import com.maddox.rts.NetObj;
import com.maddox.rts.NetSpawn;
import com.maddox.rts.NetUpdate;
import com.maddox.rts.RTSConf;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;
import com.maddox.rts.net.NetFileClient;
import com.maddox.rts.net.NetFileRequest;
import com.maddox.sas1946.il2.util.BaseGameVersion;
import com.maddox.util.IntHashtable;

public class NetUser extends NetHost implements NetFileClient, NetUpdate {
    public static final int MSG_READY              = 1;
    public static final int MSG_BORNPLACE          = 2;
    public static final int MSG_AIRDROMESTAY       = 3;
    public static final int MSG_STAT               = 4;
    public static final int MSG_STAT_INC           = 5;
    public static final int MSG_CURSTAT            = 6;
    public static final int MSG_CURSTAT_INC        = 7;
    public static final int MSG_PING               = 8;
    public static final int MSG_PING_INC           = 9;
    public static final int MSG_REGIMENT           = 10;
    public static final int MSG_SKIN               = 11;
    public static final int MSG_PILOT              = 12;
    public static final int MSG_REQUEST_PLACE      = 13;
    public static final int MSG_PLACE              = 14;
    public static final int MSG_REQUEST_WAIT_START = 15;
    public static final int MSG_WAIT_START         = 16;
    public static final int MSG_KICK               = 17;
    public static final int MSG_MISSION_COMPLETE   = 18;
    public static final int MSG_CAMERA             = 19;
    public static final int MSG_ORDER_CMD          = 20;
    public static final int MSG_RADIO              = 21;
    public static final int MSG_VOICE              = 22;
    public static final int MSG_TASK_COMPLETE      = 23;
    public static final int MSG_HOUSE_DIE          = 24;
    public static final int MSG_HOUSE_SYNC         = 25;
    public static final int MSG_BRIDGE_RDIE        = 26;
    public static final int MSG_BRIDGE_DIE         = 27;
    public static final int MSG_BRIDGE_SYNC        = 28;
    public static final int MSG_DOT_RANGE_FRIENDLY = 29;
    public static final int MSG_DOT_RANGE_FOE      = 30;
    public static final int MSG_EVENTLOG           = 31;
    public static final int MSG_NOISEART           = 32;
    // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
    public static final int MSG_TRIGGER            = 51;
//    public static final int MSG_TRIGGER_SG         = 51;
    // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---

    // TODO: Storebror: Track Users hitting refly where this shouldn't be possible
    private long      lastSuspiciousPreRefly      = 0L;
    private ArrayList suspiciousPreReflyAddonInfo = new ArrayList();

    public ArrayList getSuspiciousPreReflyAddonInfo() {
        return this.suspiciousPreReflyAddonInfo;
    }

    public void addSuspiciousPreReflyAddonInfo(String addonInfo) {
        this.suspiciousPreReflyAddonInfo.add(addonInfo);
    }

    public void clearSuspiciousPreReflyAddonInfo() {
        this.suspiciousPreReflyAddonInfo.clear();
    }

    public long getLastSuspiciousPreRefly() {
        return this.lastSuspiciousPreRefly;
    }

    public void setLastSuspiciousPreRefly(long lastSuspiciousPreRefly) {
        this.lastSuspiciousPreRefly = lastSuspiciousPreRefly;
    }
    // ---

    // TODO: +++ New "slap" command implementation by SAS~Storebror +++
    private static int       remainingSlaps    = 0;
    private static int       lastSlapTickCount = 0;
    private static NetUser   slapUser          = null;
    private static final int TICK_DIVISOR      = 33;
    // TODO: --- New "slap" command implementation by SAS~Storebror ---

    // TODO: Storebror: Implement Patch Level Replication
    public static final byte MSG_PATCHLEVEL        = 101;
    public static final byte MSG_SELECTOR_VERSION  = 102;
    public static final byte MSG_ULTRAPACK_VERSION = 103;
    public static final byte MSG_HOTFIX_VERSION    = 104;
//    public static final String MIN_PATCH_LEVEL        = Config.MIN_PATCH_LEVEL;
//    public static final String MAX_PATCH_LEVEL        = Config.MAX_PATCH_LEVEL;
//    public static final String UP_VERSION             = Config.VERSION_NUMBER;
//    public static final String PATCH_LEVEL            = Config.PATCH_LEVEL;
    public static final String PATCH_LEVEL_TEST = "102b1";
//    public static String[]     PATCHLEVEL_G           = Config.PATCHLEVEL_G;
//    public static String[]     PATCHLEVEL_Y           = Config.PATCHLEVEL_Y;
    private String           patchLevel               = "none";
    private String           hotfixVersion            = "none";
    private String           selectorVersion          = "unknown";
    private String           ultrapackVersion         = "3 RC4";
    public static final long UPDATE_CHAT_INTERVAL     = 10000L;
    private long             lastUpdateChatMessages   = 0L;
    private boolean          updateChatMessagesLogged = false;
    private static int       minPatchLevel            = Integer.MIN_VALUE;
    private static int       maxPatchLevel            = Integer.MAX_VALUE;
    private static int[]     greenPatchLevels;
    private static int[]     yellowPatchLevels;

    public String getPatchLevel() {
        return this.patchLevel;
    }

    public void setPatchLevel(String patchLevel) {
        if (!this.patchLevel.equalsIgnoreCase(patchLevel)) System.out.println("Player " + this.uniqueName() + " uses Patch Level " + patchLevel + "!");
        this.patchLevel = patchLevel;
    }

    public String getSelectorVersion() {
        return this.selectorVersion;
    }

    public void setSelectorVersion(String selectorVersion) {
        if (!this.selectorVersion.equalsIgnoreCase(selectorVersion)) System.out.println("Player " + this.uniqueName() + " uses Selector Version " + selectorVersion + "!");
        this.selectorVersion = selectorVersion;
    }

    public String getUltrapackVersion() {
        return this.ultrapackVersion;
    }

    public void setUltrapackVersion(String ultrapackVersion) {
        if (this.ultrapackVersion.equalsIgnoreCase(ultrapackVersion)) System.out.println("Player " + this.uniqueName() + " uses Ultrapack Version " + ultrapackVersion + "!");
        this.ultrapackVersion = ultrapackVersion;
    }

    public String getHotfixVersion() {
        return this.hotfixVersion;
    }

    public void setHotfixVersion(String hotfixVersion) {
        if (!this.hotfixVersion.equalsIgnoreCase(hotfixVersion)) System.out.println("Player " + this.uniqueName() + " uses Hotfix Version " + hotfixVersion + "!");
        this.hotfixVersion = hotfixVersion;
    }

    public boolean needsUpdate(String patchLevel) {
        int curPatchLevel;
        try {
            curPatchLevel = Integer.parseInt(patchLevel);
        } catch (NumberFormatException e) {
            return true;
        }
        return curPatchLevel < minPatchLevel || curPatchLevel > maxPatchLevel;
    }

    public static int patchLevelState(String patchLevel) {
        int curPatchLevel;
        try {
            curPatchLevel = Integer.parseInt(patchLevel);
            for (int i = 0; i < greenPatchLevels.length; i++)
                if (curPatchLevel == greenPatchLevels[i]) return 1;
            for (int i = 0; i < yellowPatchLevels.length; i++)
                if (curPatchLevel == yellowPatchLevels[i]) return 0;
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }
    // ---

    private NetUserStat        stat;
    private NetUserStat        curstat;
    private static NetUserStat _st            = new NetUserStat();
    private static NetUserStat __st           = new NetUserStat();
    private int                idChannelFirst;
    public int                 ping;
    private int                army;
    private boolean            bTrackWriter;
    private int                bornPlace;
    private int                airdromeStay;
    private String             uniqueName;
    private int                place;
    private boolean            bWaitStartCoopMission;
    public int                 syncCoopStart;
    private int                localRequestPlace;
    private static int         armyCoopWinner = 0;
    public NetUserRegiment     netUserRegiment;
    protected NetMaxLag        netMaxLag;
    public OrdersTree          ordersTree;
    private NetFileRequest     netFileRequestMissProp;
    private NetFileRequest     netFileRequestMissPropLocale;
    private String             ownerSkinBmp;
    private String             localSkinBmp;
    private String             skinDir;
    private NetFileRequest     netSkinRequest;
    private Mat                cacheSkinMat[];
    private String             ownerPilotBmp;
    private String             localPilotBmp;
    private String             localPilotTga;
    private NetFileRequest     netPilotRequest;
    private Mat                cachePilotMat[];
    private String             ownerNoseartBmp;
    private String             localNoseartBmp;
    private String             localNoseartTga;
    private NetFileRequest     netNoseartRequest;
    private Mat                cacheNoseartMat[];
    private String             radio;
    private int                curCodec;
    private Actor              viewActor;
    private AircraftNetFilter  airNetFilter;
    private long               lastTimeUpdate;
    private boolean            bPingUpdateStarted;

    class AircraftNetFilter implements NetFilter {
        public float filterNetMessage(NetChannel netchannel, NetMsgFiltered netmsgfiltered) {
            Object obj = netmsgfiltered.filterArg();
            if (obj == null) return -1F;
            IntHashtable inthashtable = null;
            ActorPos actorpos = null;
            if (obj instanceof NetAircraft) {
                NetAircraft netaircraft = (NetAircraft) obj;
                inthashtable = ((com.maddox.il2.objects.air.NetAircraft.AircraftNet) netaircraft.net).filterTable;
                actorpos = netaircraft.pos;
            } else if (obj instanceof NetGunner) {
                NetGunner netgunner = (NetGunner) obj;
                inthashtable = netgunner.getFilterTable();
                actorpos = netgunner.pos;
            } else return -1F;
            if (Time.isPaused()) return 0.0F;
            if (!Actor.isValid(NetUser.this.viewActor)) return 0.5F;
            int i = inthashtable.get(netchannel.id());
            if (i == -1) {
                inthashtable.put(netchannel.id(), (int) (Time.current() & 0x7ffffffL));
                return 1.0F;
            }
            double d = (int) (Time.current() & 0x7ffffffL) - i;
            if (d < 0.0D) {
                inthashtable.put(netchannel.id(), (int) (Time.current() & 0x7ffffffL));
                return 1.0F;
            }
            // +++ TODO: Storebror: null Checks added +++
            double d1 = 10000D;
            if (NetUser.this.viewActor != null && actorpos != null) d1 = NetUser.this.viewActor.pos.getAbsPoint().distance(actorpos.getAbsPoint());
// double d1 = NetUser.this.viewActor.pos.getAbsPoint().distance(actorpos.getAbsPoint());
// --- TODO: Storebror: null Checks added ---
            if (d1 > 10000D) d1 = 10000D;
            if (d1 < 1.0D) d1 = 1.0D;
            double d2 = d1 * 5000D / 10000D;
            float f = (float) (d / d2);
            if (f >= 1.0F) return 1.0F;
            else return f * f;
        }

        public void filterNetMessagePosting(NetChannel netchannel, NetMsgFiltered netmsgfiltered) {
            Object obj = netmsgfiltered.filterArg();
            if (obj == null) return;
            IntHashtable inthashtable = null;
            if (obj instanceof NetAircraft) {
                NetAircraft netaircraft = (NetAircraft) obj;
                inthashtable = ((com.maddox.il2.objects.air.NetAircraft.AircraftNet) netaircraft.net).filterTable;
            } else if (obj instanceof NetGunner) {
                NetGunner netgunner = (NetGunner) obj;
                inthashtable = netgunner.getFilterTable();
            } else return;
            inthashtable.put(netchannel.id(), (int) (Time.current() & 0x7ffffffL));
        }

        public boolean filterEnableAdd(NetChannel netchannel, NetFilter netfilter) {
            return true;
        }

        AircraftNetFilter() {
        }
    }

    static class SPAWN implements NetSpawn {

        public void netSpawn(int i, NetMsgInput netmsginput) {
            try {
                String s = netmsginput.read255();
                int j = netmsginput.readUnsignedByte();
                int k = netmsginput.readUnsignedByte();
                if (k == 255) k = -1;
                int l = netmsginput.readUnsignedShort();
                boolean flag = netmsginput.readBoolean();
                NetHost anethost[] = null;
                int i1 = netmsginput.available() / NetMsgInput.netObjReferenceLen();
                if (i1 > 0) {
                    anethost = new NetHost[i1];
                    for (int j1 = 0; j1 < i1; j1++)
                        anethost[j1] = (NetHost) netmsginput.readNetObj();

                } else l = netmsginput.channel().id();
                NetUser netuser = new NetUser(netmsginput.channel(), i, s, anethost);
                netuser.bornPlace = j;
                netuser.place = k;
                netuser.idChannelFirst = l;
                netuser.bTrackWriter = flag;
                netuser.bWaitStartCoopMission = false;
                if (Main.cur().netServerParams != null && Main.cur().netServerParams.isMaster() && Main.cur().netServerParams.isCoop()) netuser.requestPlace(-1);
                if (i1 == 0 && netmsginput.channel() instanceof NetChannelInStream) netuser.bTrackWriter = true;
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
        }

        SPAWN() {
        }
    }

    public NetUserStat stat() {
        return this.stat;
    }

    public NetUserStat curstat() {
        return this.curstat;
    }

    public void reset() {
        this.army = 0;
        this.stat.clear();
        this.curstat.clear();
        this.netMaxLag = null;
    }

    public String uniqueName() {
        return this.uniqueName;
    }

    public boolean isTrackWriter() {
        return this.bTrackWriter;
    }

    public static NetUser findTrackWriter() {
        List list = NetEnv.hosts();
        int i = list.size();
        for (int j = 0; j < i; j++) {
            NetUser netuser = (NetUser) list.get(j);
            if (netuser.isTrackWriter()) return netuser;
        }

        return null;
    }

    public void setShortName(String s) {
        if (s == null) s = "";
        super.setShortName(s);
        if (this.isMaster() && !this.isMirrored()) this.makeUniqueName();
    }

    private void makeUniqueName() {
        String s = this.shortName();
        ArrayList arraylist = new ArrayList(NetEnv.hosts());
        arraylist.add(NetEnv.host());
        int i = arraylist.size();
        int j = 0;
        do {
            boolean flag = false;
            for (int k = 0; k < i; k++) {
                NetUser netuser = (NetUser) arraylist.get(k);
                String s1 = netuser.uniqueName();
                if (!s.equals(s1) || netuser == this) continue;
                flag = true;
                break;
            }

            if (flag) {
                s = this.shortName() + j;
                j++;
            } else {
                this.uniqueName = s;
//                System.out.println("uniqueName = \"" + this.uniqueName + "\"");
                return;
            }
        } while (true);
    }

    private void pingUpdateInc() {
        new MsgAction(64, 10D, this) {

            public void doAction(Object obj) {
                NetUser netuser = (NetUser) obj;
                if (netuser.isDestroyed()) return;
                if (Main.cur().netServerParams != null && !Main.cur().netServerParams.isDestroyed() && !Main.cur().netServerParams.isMaster() && !(Main.cur().netServerParams.masterChannel() instanceof NetChannelInStream)) try {
                    NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted(1);
                    netmsgguaranted.writeByte(9);
                    NetUser.this.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
                } catch (Exception exception) {
                    NetObj.printDebug(exception);
                }
                NetUser.this.pingUpdateInc();
            }

        };
    }

    public int getArmy() {
        return this.army;
    }

    public void setArmy(int i) {
        this.army = i;
        this.radio_onArmyChanged();
    }

    public void sendStatInc() {
        if (!this.isMaster()) return;
        if (Main.cur().netServerParams == null) return;
        _st.clear();
        _st.fillFromScoreCounter(true);
        if (_st.isEmpty()) return;
        else {
            this._sendStatInc(false);
            return;
        }
    }

    private void sendCurStatInc() {
        if (!this.isMaster()) return;
        if (Main.cur().netServerParams == null) return;
        _st.clear();
        _st.fillFromScoreCounter(false);
        if (_st.isEmpty()) return;
        __st.set(this.stat);
        __st.inc(_st);
        if (__st.isEqualsCurrent(this.curstat)) return;
        else {
            this._sendStatInc(true);
            return;
        }
    }

    public void netUpdate() {
        if (!this.isMaster()) return;
        // TODO: +++ New "slap" command implementation by SAS~Storebror +++
        if (remainingSlaps > 0) doRemainingSlaps(slapUser);
        // TODO: --- New "slap" command implementation by SAS~Storebror ---
        this.checkCameraBaseChanged();
        // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
        this.checkUpdateChatMessages();
        // ---
        long l = Time.real();
        if (this.lastTimeUpdate + 20000L > l) return;
        this.lastTimeUpdate = l;
        if (!Mission.isNet()) return;
        if (!Mission.isPlaying()) return;
        if (Main.cur().netServerParams == null) return;
        if (Main.cur().netServerParams.masterChannel() instanceof NetChannelInStream) return;
        this.sendCurStatInc();
        return;
    }

    private void _sendStatInc(boolean flag) {
        if (Main.cur().netServerParams.isMaster()) {
            if (Main.cur().netServerParams.isCoop()) {
                if (flag) this.curstat.set(_st);
                else {
                    this.stat.set(_st);
                    this.curstat.set(_st);
                }
            } else if (flag) {
                this.curstat.set(this.stat);
                this.curstat.inc(_st);
                _st.set(this.curstat);
            } else {
                this.stat.inc(_st);
                this.curstat.set(this.stat);
                _st.set(this.stat);
            }
            ((NetUser) NetEnv.host()).replicateStat(this, flag);
        } else try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(flag ? 7 : 5);
            _st.write(netmsgguaranted);
            this.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private void replicateStat(NetUser netuser, boolean flag) {
        this.replicateStat(netuser, flag, null);
    }

    private void replicateStat(NetUser netuser, boolean flag, NetChannel netchannel) {
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(flag ? 6 : 4);
            netmsgguaranted.writeNetObj(netuser);
            _st.write(netmsgguaranted);
            if (netchannel != null) this.postTo(netchannel, netmsgguaranted);
            else this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private void getIncStat(NetMsgInput netmsginput, boolean flag) throws IOException {
        _st.read(netmsginput);
        this._sendStatInc(flag);
    }

    private void getStat(NetMsgInput netmsginput, boolean flag) throws IOException {
        _st.read(netmsginput);
        NetUser netuser = (NetUser) netmsginput.readNetObj();
        if (netuser == null) return;
        if (flag) netuser.curstat.set(_st);
        else {
            netuser.stat.set(_st);
            netuser.curstat.set(_st);
        }
        this.replicateStat(netuser, flag);
    }

    public int getAirdromeStay() {
        return this.airdromeStay;
    }

    public int getBornPlace() {
        return this.bornPlace;
    }

    public void setBornPlace(int i) {
        if (this.bornPlace == i) return;
        this.bornPlace = i;
        this.airdromeStay = -1;
        if (this.isMirrored()) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted(2);
            netmsgguaranted.writeByte(2);
            netmsgguaranted.writeByte(this.bornPlace);
            this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
        if (this.bornPlace >= 0 && World.cur().bornPlaces != null && this.bornPlace < World.cur().bornPlaces.size() && Main.cur().netServerParams != null) {
            BornPlace bornplace = (BornPlace) World.cur().bornPlaces.get(this.bornPlace);
            this.setArmy(bornplace.army);
            if (Main.cur().netServerParams.isMaster()) {
                double d = bornplace.r * bornplace.r;
                Point_Stay apoint_stay[][] = World.cur().airdrome.stay;
                for (int j = 0; j < apoint_stay.length; j++)
                    // TODO: Comment by |ZUTI|: Here begin logic for assigning
                    // of free spawn place
                    if (apoint_stay[j] != null) {
                        Point_Stay point_stay = apoint_stay[j][apoint_stay[j].length - 1];
                        double d1 = (point_stay.x - bornplace.place.x) * (point_stay.x - bornplace.place.x) + (point_stay.y - bornplace.place.y) * (point_stay.y - bornplace.place.y);
                        if (d1 <= d && ((NetUser) NetEnv.host()).airdromeStay != j) {
                            List list = NetEnv.hosts();
                            boolean flag = false;
                            for (int k = 0; k < list.size(); k++) {
                                NetUser netuser = (NetUser) list.get(k);
                                if (netuser.airdromeStay != j) continue;
                                flag = true;
                                break;
                            }

                            if (!flag) {
                                this.airdromeStay = j;
                                d = d1;
                            }
                        }
                    }
                if (this.isMirror()) this.sendAirdromeStay(this.bornPlace, this.airdromeStay);
            }
        }
    }

    private void sendAirdromeStay(int i, int j) {
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted(6);
            netmsgguaranted.writeByte(3);
            netmsgguaranted.writeByte(i);
            netmsgguaranted.writeInt(j);
            this.postTo(this.masterChannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void onConnectReady(NetChannel netchannel) {
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted(1);
            netmsgguaranted.writeByte(1);
            this.postTo(netchannel, netmsgguaranted);
            netchannel.userState = 1;
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public int getPlace() {
        if (this.isMaster() && this.localRequestPlace != this.place) return -1;
        else return this.place;
    }

    private int _getPlace() {
        return this.place;
    }

    public void requestPlace(int i) {
        armyCoopWinner = 0;
        if (this.isMaster()) {
            if (this.localRequestPlace == i) return;
            this.localRequestPlace = i;
            this.place = -1;
        }
        NetServerParams netserverparams = Main.cur().netServerParams;

        // TODO: Added by |ZUTI|: failsafe check
        // --------------------------------------------
        if (netserverparams == null) return;
        // --------------------------------------------

        if (netserverparams.isMaster()) {
            if (i != -1) {
                NetUser netuser = (NetUser) NetEnv.host();
                if (netuser._getPlace() == i) i = -1;
                else {
                    List list1 = NetEnv.hosts();
                    for (int k = 0; k < list1.size(); k++) {
                        NetUser netuser1 = (NetUser) list1.get(k);
                        if (netuser1._getPlace() != i) continue;
                        i = -1;
                        break;
                    }

                }
            }
            this.place = i;

            // TODO: Storebror: Temporary "fix", don't change army at all when changing ac position!

// if (this.place >= 0) {
// // TODO: Added by |ZUTI|: don't change army if user is changing position in own ac!
// // TODO: Storebror: Gunner Switch TEST!
//// if (!NetEnv.isServer() && !ZutiSupportMethods_Net.isInOwnAircraft(this, World.getPlayerAircraft())) {
// if (!ZutiSupportMethods_Net.isInOwnAircraft(this, World.getPlayerAircraft())) {
// try {
// // TODO: Storebror: Debugging Army switch on gunner pos switch bug
//// int iLogArmy = this.getArmy();
//// try {
////// iLogArmy = GUINetAircraft.getItem(this.place).reg.getArmy();
//// GUINetAircraft.Item tempItem = GUINetAircraft.getItem(this.place);
//// if (tempItem == null) {
//// System.out.println("NetUser requestPlace(" + i + ") GUINetAircraft.getItem(" + this.place + ")==null");
//// } else {
//// Regiment tempRegiment = tempItem.reg;
//// if (tempRegiment == null) {
//// System.out.println("NetUser requestPlace(" + i + ") GUINetAircraft.getItem(" + this.place + ").reg==null");
//// } else {
//// iLogArmy = tempRegiment.getArmy();
//// }
//// }
//// } catch (Exception ex) {
//// ex.printStackTrace();
//// }
//// System.out.println("### GUNNER POS SWITCH BUG DEBUG: NetUser.requestPlace NetUser " + this.uniqueName() + " place " + this.place + " setArmy(" + iLogArmy + ")");
// this.setArmy(GUINetAircraft.getItem(this.place).reg.getArmy());
//// this.setArmy(iLogArmy);
//// String armyName = iLogArmy >= Army.amountNet()?"!!!NOT DEFINED!!!":Army.name(iLogArmy);
//// System.out.println("### GUNNER POS SWITCH BUG DEBUG: NetUser " + this.uniqueName() + " changed to place " + this.place + ", which belongs to Army " + armyName + " (possible side switch SKIPPED in this Patch Pack!) ###");
// // ...
// } catch (Exception ex) {
// // TODO: Storebror: Debugging Army switch on gunner pos switch bug
//// System.out.println("### GUNNER POS SWITCH BUG DEBUG: NetUser.requestPlace NetUser " + this.uniqueName() + " place " + this.place + " ERROR ###");
//// ex.printStackTrace();
// // ...
// }
// }
// }
            this.bWaitStartCoopMission = false;
            if (NetEnv.host().isMirrored()) {
                List list = NetEnv.channels();
                for (int j = 0; j < list.size(); j++) {
                    NetChannel netchannel = (NetChannel) list.get(j);
                    if (netchannel.isMirrored(this) || netchannel == this.masterChannel()) try {
                        NetMsgGuaranted netmsgguaranted1 = new NetMsgGuaranted();
                        netmsgguaranted1.writeByte(MSG_PLACE);
                        netmsgguaranted1.writeByte(this.place);
                        netmsgguaranted1.writeNetObj(this);
                        NetEnv.host().postTo(netchannel, netmsgguaranted1);
                    } catch (Exception exception1) {
                        NetObj.printDebug(exception1);
                    }
                }

            }
        } else try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(13);
            netmsgguaranted.writeByte(i);
            this.postTo(netserverparams.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void resetAllPlaces() {
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null || !netserverparams.isMaster()) return;
        ((NetUser) NetEnv.host()).requestPlace(-1);
        List list = NetEnv.hosts();
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            NetUser netuser = (NetUser) list.get(i);
            netuser.requestPlace(-1);
        }

    }

    public void missionLoaded() {
        if (!Mission.isCoop()) return;
        if (!(Mission.cur().netObj().masterChannel() instanceof NetChannelInStream)) {
            List list = NetEnv.hosts();
            if (list == null) return;
            for (int i = 0; i < list.size(); i++) {
                NetUser netuser = (NetUser) list.get(i);
                if (netuser.place >= 0) netuser.setArmy(GUINetAircraft.getItem(netuser.place).reg.getArmy());
            }

        }
    }

    public boolean isWaitStartCoopMission() {
        return this.bWaitStartCoopMission && this.getPlace() >= 0;
    }

    public void doWaitStartCoopMission() {
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams.isMaster()) {
            this.bWaitStartCoopMission = true;
            if (NetEnv.host().isMirrored()) try {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(16);
                netmsgguaranted.writeNetObj(this);
                NetEnv.host().post(netmsgguaranted);
            } catch (Exception exception) {
                NetObj.printDebug(exception);
            }
        } else try {
            NetMsgGuaranted netmsgguaranted1 = new NetMsgGuaranted();
            netmsgguaranted1.writeByte(15);
            this.postTo(netserverparams.masterChannel(), netmsgguaranted1);
        } catch (Exception exception1) {
            NetObj.printDebug(exception1);
        }
    }

    public void kick(NetUser netuser) {
        if (netuser == null || netuser.isDestroyed()) return;
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (!netserverparams.isMaster()) return;
        if (netuser.isMaster()) return;
        else {
            this._kick(netuser);
            return;
        }
    }

    private void _kick(NetUser netuser) {
        if (netuser == null || netuser.isDestroyed()) return;
        else {
            // TODO: Modified by |ZUTI|
            // ---------------------------------------------
            ZutiSupportMethods_NetSend.releaseCarrierSpawnPlace(netuser);
            // ---------------------------------------------

            new MsgAction(72, 0.0D, netuser) {

                public void doAction(Object obj) {
                    NetUser netuser1 = (NetUser) obj;
                    if (netuser1 == null || netuser1.isDestroyed()) return;
                    if (netuser1.path() == null) netuser1.masterChannel().destroy("You have been kicked from the server.");
                    else try {
                        NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                        netmsgguaranted.writeByte(17);
                        netmsgguaranted.writeNetObj(netuser1);
                        NetEnv.host().postTo(netuser1.masterChannel(), netmsgguaranted);
                    } catch (Exception exception) {
                        NetObj.printDebug(exception);
                    }
                }

            };
            return;
        }
    }

    public void coopMissionComplete(boolean flag) {
        if (this.isMirrored()) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(18);
            netmsgguaranted.writeByte(flag ? 1 : 0);
            this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
        this.setArmyCoopWinner(flag);
    }

    private void setClientMissionComplete(boolean flag) {
        this.coopMissionComplete(flag);
        World.cur().targetsGuard.doMissionComplete();
    }

    private void setArmyCoopWinner(boolean flag) {
        armyCoopWinner = World.getMissionArmy();
        if (!flag) armyCoopWinner = armyCoopWinner % 2 + 1;
    }

    public static int getArmyCoopWinner() {
        return armyCoopWinner;
    }

    public void speekVoice(int i, int j, int k, String s, int ai[], int l, boolean flag) {
        if (this.isMirrored()) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(22);
            netmsgguaranted.writeShort(i);
            netmsgguaranted.writeShort(j);
            if (s != null && s.length() == 2) k |= 0x8000;
            netmsgguaranted.writeShort(k);
            if (s != null && s.length() == 2) netmsgguaranted.write255(s);
            netmsgguaranted.writeBoolean(flag);
            int i1 = ai.length;
            for (int j1 = 0; j1 < i1; j1++) {
                int k1 = ai[j1];
                if (k1 == 0) break;
                netmsgguaranted.writeShort(k1);
            }

            this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private void getVoice(NetMsgInput paramNetMsgInput) {
        try {
            int i = paramNetMsgInput.readUnsignedShort();
            int j = paramNetMsgInput.readUnsignedShort();
            int k = paramNetMsgInput.readUnsignedShort();
            String str = null;
            if ((k & 0x8000) != 0) {
                k &= -32769;
                str = paramNetMsgInput.read255();
            }
            boolean bool = paramNetMsgInput.readBoolean();
            int l = paramNetMsgInput.available() / 2;
            if (l == 0) return;
            int[] arrayOfInt = new int[l + 1];
            for (int i1 = 0; i1 < l; ++i1)
                arrayOfInt[i1] = paramNetMsgInput.readUnsignedShort();
            arrayOfInt[l] = 0;
            this.speekVoice(i, j, k, str, arrayOfInt, 1, bool);

            Voice.setSyncMode(i);
            Voice.speak(j, k, str, arrayOfInt, 1, false, bool);
        } catch (Exception localException) {
            NetObj.printDebug(localException);
        }
    }

    private void checkCameraBaseChanged() {
        if (!Config.isUSE_RENDER()) return;
        if (!Mission.isNet()) return;
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null || netserverparams.isMaster()) return;
        Actor actor = Main3D.cur3D().viewActor();
        if (this.viewActor == actor) return;
        this.viewActor = actor;
        ActorNet actornet = null;
        if (Actor.isValid(actor)) actornet = actor.net;
        this.replicateCameraBaseChanged(actornet);
    }

    private void replicateCameraBaseChanged(NetObj netobj) {
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams.isMaster()) {
            if (netobj != null) {
                Object obj = netobj.superObj();
                if (obj != null && obj instanceof Actor) {
                    this.viewActor = (Actor) obj;
                    return;
                }
            }
            this.viewActor = null;
        } else this.doReplicateCameraBaseChanged(netobj);
    }

    public void doReplicateCameraBaseChanged(Object obj) {
        if (this.isDestroyed()) return;
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null) return;
        NetObj netobj = null;
        if (obj != null) {
            netobj = (NetObj) obj;
            if (netobj.isDestroyed()) netobj = null;
            else if (netobj.masterChannel() != netserverparams.masterChannel() && !netserverparams.masterChannel().isMirrored(netobj)) {
                new MsgInvokeMethod_Object("doReplicateCameraBaseChanged", netobj).post(72, this);
                return;
            }
        }
        if (netserverparams.masterChannel() instanceof NetChannelInStream) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(19);
            netmsgguaranted.writeNetObj(netobj);
            this.postTo(netserverparams.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void orderCmd(int i) {
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams.isMaster()) {
            if (this.ordersTree == null) {
                System.out.println("### ERROR: ordersTree is null!");
                return;
            }
            if (i == -1) this.ordersTree.activate();
            else if (i == -2) this.ordersTree.unactivate();
            else this.ordersTree.execCmd(i);
        } else try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(20);
            netmsgguaranted.writeByte(i);
            this.postTo(netserverparams.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void orderCmd(int i, Actor actor) {
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams.isMaster()) {
            if (this.ordersTree == null) {
                System.out.println("### ERROR: ordersTree is null!");
                return;
            }
            if (i == -1) this.ordersTree.activate();
            else if (i == -2) this.ordersTree.unactivate();
            else {
                Actor actor1 = Selector.getTarget();
                Selector.setTarget(actor);
                this.ordersTree.execCmd(i);
                Selector.setTarget(actor1);
            }
        } else try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(20);
            netmsgguaranted.writeByte(i);
            netmsgguaranted.writeNetObj(actor.net);
            this.postTo(netserverparams.masterChannel(), netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void postTaskComplete(Actor actor) {
        if (!Actor.isValid(actor)) return;
        World.onTaskComplete(actor);
        if (actor.net.countMirrors() == 0) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(23);
            netmsgguaranted.writeNetObj(actor.net);
            this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void replicateDotRange() {
        this.replicateDotRange(true);
        this.replicateDotRange(false);
    }

    public void replicateDotRange(boolean flag) {
        this.replicateDotRange(flag, null);
    }

    private void replicateDotRange(NetChannel netchannel) {
        this.replicateDotRange(true, netchannel);
        this.replicateDotRange(false, netchannel);
    }

    private void replicateDotRange(boolean flag, NetChannel netchannel) {
        if (Main.cur().netServerParams == null) return;
        if (this != Main.cur().netServerParams.host()) return;
        if (this.isMirrored() || netchannel != null) try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(flag ? 29 : 30);
            if (flag) Main.cur().dotRangeFriendly.netOutput(netmsgguaranted);
            else Main.cur().dotRangeFoe.netOutput(netmsgguaranted);
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void replicateEventLog(int i, float f, String s, String s1, int j, float f1, float f2) {
        if (Main.cur().netServerParams == null) return;
        if (!this.isMirrored()) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(31);
            netmsgguaranted.writeByte(i);
            netmsgguaranted.writeFloat(f);
            netmsgguaranted.write255(s);
            netmsgguaranted.write255(s1);
            netmsgguaranted.writeByte(j);
            netmsgguaranted.writeFloat(f1);
            netmsgguaranted.writeFloat(f2);
            this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private void getEventLog(NetMsgInput netmsginput) {
        try {
            byte byte0 = netmsginput.readByte();
            // TODO: Storebror: Track Users hitting refly where this shouldn't be possible
            if (ZutiAircraftCrewManagement.isSuspiciousRefly(byte0, this)) {
                this.suspiciousPreReflyAddonInfo.clear();
                Chat.sendLog(0, "user_cheatkick2", this, null);
                this.kick(this);
                return;
            }
            this.suspiciousPreReflyAddonInfo.clear();
            // ---
            float f = netmsginput.readFloat();
            String s = netmsginput.read255();
            s = this.getCleanedString(s);
            String s1 = netmsginput.read255();
            s1 = this.getCleanedString(s1);
            byte byte1 = netmsginput.readByte();
            float f1 = netmsginput.readFloat();
            float f2 = netmsginput.readFloat();
            EventLog.type(byte0, f, s, s1, byte1, f1, f2, false);
            this.replicateEventLog(byte0, f, s, s1, byte1, f1, f2);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private String getCleanedString(String s) {
        if (Main.cur().netServerParams == null || !Main.cur().netServerParams.filterUserNames) return s;

        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (this.isCharValid(c)) stringbuffer.append(s.charAt(i));
        }

        return stringbuffer.toString();
    }

    private boolean isCharValid(int i) {
        if (i >= 33 && i <= 160) return true;

        if (i >= 1025 && i <= 1119) return true;
        return i >= 1168 && i <= 1257;
    }

    public boolean netInput(NetMsgInput netmsginput) throws IOException {
        netmsginput.reset();
        if (netmsginput.available() < 1) return false; // TODO: By SAS~Storebror - This is to counter log flooding from net desync
        if (super.netInput(netmsginput)) return true;

        netmsginput.reset();
        byte byte0 = netmsginput.readByte();

        // TODO: Added by |ZUTI|
        // -----------------------------------------------
        if (ZutiSupportMethods_NetReceive.processReceivedMessage(this, netmsginput, byte0)) return true;
        // -----------------------------------------------

        if (this.isMirror() && netmsginput.channel() == this.masterChannel) switch (byte0) {
            case MSG_READY:
                if (netmsginput.channel().userState == -1) {
                    netmsginput.channel().userState = 1;
                    if (Mission.cur() != null && Mission.cur().netObj() != null) MsgNet.postRealNewChannel(Mission.cur().netObj(), this.masterChannel);
                }
                return true;

            case MSG_PLACE:
                int i = netmsginput.readUnsignedByte();
                if (i == 255) i = -1;
                NetUser netuser2 = (NetUser) netmsginput.readNetObj();
                if (netuser2 == null) return true;
                netuser2.place = i;
                if (i >= 0 && Mission.cur() != null && Main.cur().missionLoading == null) try {
                    // TODO: Storebror: Debugging Army switch on gunner pos switch bug
                    // netuser2.setArmy(GUINetAircraft.getItem(i).reg.getArmy());
                    // Set Army according to aircraft instead!
                    Aircraft aircraft = netuser2.findAircraft();
                    if (aircraft != null) netuser2.setArmy(aircraft.getArmy());
                } catch (Exception ex) {}
                netuser2.bWaitStartCoopMission = false;
                if (this.isMirrored()) try {
                    NetMsgGuaranted netmsgguaranted4 = new NetMsgGuaranted();
                    netmsgguaranted4.writeByte(MSG_PLACE);
                    netmsgguaranted4.writeByte(i);
                    netmsgguaranted4.writeNetObj(netuser2);
                    this.post(netmsgguaranted4);
                } catch (Exception exception2) {
                    NetObj.printDebug(exception2);
                }
                return true;

            case MSG_WAIT_START:
                NetUser netuser = (NetUser) netmsginput.readNetObj();
                if (netuser == null) return true;
                netuser.bWaitStartCoopMission = true;
                if (this.isMirrored()) try {
                    NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                    netmsgguaranted.writeByte(16);
                    netmsgguaranted.writeNetObj(netuser);
                    this.post(netmsgguaranted);
                } catch (Exception exception) {
                    NetObj.printDebug(exception);
                }
                return true;

            case MSG_KICK:
                NetUser netuser1 = (NetUser) netmsginput.readNetObj();
                if (netuser1 == null) return true;
                else {
                    this._kick(netuser1);
                    return true;
                }

            case MSG_ORDER_CMD:
                byte byte1 = netmsginput.readByte();
                if (netmsginput.available() > 0) {
                    Actor actor = null;
                    NetObj netobj1 = netmsginput.readNetObj();
                    if (netobj1 != null) actor = (Actor) netobj1.superObj();
                    this.orderCmd(byte1, actor);
                } else this.orderCmd(byte1);
                return true;

            case MSG_MISSION_COMPLETE:
                boolean flag = netmsginput.readByte() != 0;
                if (this.isMirrored()) try {
                    NetMsgGuaranted netmsgguaranted1 = new NetMsgGuaranted();
                    netmsgguaranted1.writeByte(18);
                    netmsgguaranted1.writeByte(flag ? 1 : 0);
                    this.post(netmsgguaranted1);
                } catch (Exception exception1) {
                    NetObj.printDebug(exception1);
                }
                this.setClientMissionComplete(flag);
                return true;

            case MSG_BORNPLACE:
                int j = netmsginput.readUnsignedByte();
                if (j == 255) j = -1;
                this.setBornPlace(j);
                return true;

            case MSG_REGIMENT:
                String s = netmsginput.read255();
                char ac[] = new char[2];
                ac[0] = netmsginput.readChar();
                ac[1] = netmsginput.readChar();
                int i2 = netmsginput.readUnsignedByte();
                String s5 = netmsginput.read255();
                this.setUserRegiment(s, s5, ac, i2);
                this.replicateNetUserRegiment();
                return true;

            case MSG_SKIN:
                String s1 = netmsginput.read255();
                this.setSkin(s1);
                this.replicateSkin();
                return true;

            case MSG_PILOT:
                String s2 = netmsginput.read255();
                this.setPilot(s2);
                this.replicatePilot();
                return true;

            case MSG_NOISEART: // ' '
                String s3 = netmsginput.read255();
                this.setNoseart(s3);
                this.replicateNoseart();
                return true;

            case MSG_RADIO:
                String s4 = null;
                int k1 = 0;
                if (netmsginput.available() > 0) {
                    s4 = netmsginput.read255();
                    if (netmsginput.available() > 0) k1 = netmsginput.readInt();
                    else {
                        k1 = -1;
                        System.out.println("ERROR: Radio channel message has old format");
                    }
                }
                if (k1 != -1) this.replicateRadio(s4, k1);
                return true;

            case MSG_VOICE:
                this.getVoice(netmsginput);
                return true;

            case MSG_CAMERA:
                this.replicateCameraBaseChanged(netmsginput.readNetObj());
                return true;

            case MSG_TASK_COMPLETE:
                NetObj netobj = netmsginput.readNetObj();
                if (netobj == null) return true;
                else {
                    this.postTaskComplete((Actor) netobj.superObj());
                    return true;
                }

            case MSG_DOT_RANGE_FRIENDLY:
//                    System.out.println("MSG_DOT_RANGE_FRIENDLY 01");
                Main.cur().dotRangeFriendly.netInput(netmsginput);
                this.replicateDotRange(true);
                return true;

            case MSG_DOT_RANGE_FOE:
//                    System.out.println("MSG_DOT_RANGE_FOE 01");
                Main.cur().dotRangeFoe.netInput(netmsginput);
                this.replicateDotRange(false);
                return true;

            // TODO: Storebror: Implement Patch Level Replication
            case MSG_PATCHLEVEL: // 101
                this.readPatchLevel(netmsginput, true);
                return true;

            case MSG_SELECTOR_VERSION: // 102
                this.readSelectorVersion(netmsginput, true);
                return true;

            case MSG_ULTRAPACK_VERSION: // 103
                this.readUltrapackVersion(netmsginput, true);
                return true;
                
            case MSG_HOTFIX_VERSION: // 104
                this.readHotfixVersion(netmsginput, true);
                return true;
            // ---

        }
        switch (byte0) {
            case MSG_REQUEST_PLACE:
                int k = netmsginput.readUnsignedByte();
                if (k == 255) k = -1;
                this.requestPlace(k);
                return true;

            case MSG_REQUEST_WAIT_START:
                this.doWaitStartCoopMission();
                return true;

            case MSG_AIRDROMESTAY:
                int l = netmsginput.readUnsignedByte();
                if (l == 255) l = -1;
                int l1 = netmsginput.readInt();
                if (l == this.bornPlace) if (this.isMirror()) this.sendAirdromeStay(l, l1);
                else this.airdromeStay = l1;
                return true;

            case MSG_STAT:
            case MSG_CURSTAT:
                this.getStat(netmsginput, byte0 == 6);
                return true;

            case MSG_STAT_INC:
            case MSG_CURSTAT_INC:
                this.getIncStat(netmsginput, byte0 == 7);
                return true;

            case MSG_PING_INC:
                int i1 = 0;
                if (netmsginput.available() == 4) i1 = netmsginput.readInt();
                i1 += netmsginput.channel().ping();
                if (Main.cur().netServerParams.isMaster()) {
                    this.ping = i1;
                    NetMsgGuaranted netmsgguaranted2 = new NetMsgGuaranted();
                    netmsgguaranted2.writeByte(8);
                    netmsgguaranted2.writeInt(i1);
                    netmsgguaranted2.writeNetObj(this);
                    ((NetUser) NetEnv.host()).post(netmsgguaranted2);
                } else {
                    NetMsgGuaranted netmsgguaranted3 = new NetMsgGuaranted();
                    netmsgguaranted3.writeByte(9);
                    netmsgguaranted3.writeInt(i1);
                    this.postTo(Main.cur().netServerParams.masterChannel(), netmsgguaranted3);
                }
                return true;

            case MSG_PING:
                int j1 = netmsginput.readInt();
                NetUser netuser3 = (NetUser) netmsginput.readNetObj();
                if (netuser3 != null) {
                    netuser3.ping = j1;
                    NetMsgGuaranted netmsgguaranted5 = new NetMsgGuaranted();
                    netmsgguaranted5.writeByte(8);
                    netmsgguaranted5.writeInt(j1);
                    netmsgguaranted5.writeNetObj(netuser3);
                    this.post(netmsgguaranted5);
                }
                return true;

            case MSG_HOUSE_DIE:
                if (World.cur().statics != null) World.cur().statics.netMsgHouseDie(this, netmsginput);
                return true;

            case MSG_HOUSE_SYNC:
                if (World.cur().statics != null) World.cur().statics.netMsgHouseSync(netmsginput);
                return true;

            case MSG_BRIDGE_RDIE:
                if (World.cur().statics != null) World.cur().statics.netMsgBridgeRDie(netmsginput);
                return true;

            case MSG_BRIDGE_DIE:
                if (World.cur().statics != null) World.cur().statics.netMsgBridgeDie(this, netmsginput);
                return true;

            case MSG_BRIDGE_SYNC:
                if (World.cur().statics != null) World.cur().statics.netMsgBridgeSync(netmsginput);
                return true;

            case MSG_EVENTLOG:
                this.getEventLog(netmsginput);
                return true;

                // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
            case MSG_TRIGGER:
//                getTriggerMsg(netmsginput);
                Trigger.getReplicatedTrigger(netmsginput);
                return true;
                
//            case MSG_TRIGGER_SG:
//                getTriggerStartGround(netmsginput);
//                return true;
                // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
               
//            case 10:
//            case 11:
//            case 12:
//            case 14:
//            case 16:
//            case 17:
//            case 18:
//            case 19:
//            case 20:
//            case 21:
//            case 22:
//            case 23:
//            case 29:
//            case 30:

//            case MSG_DOT_RANGE_FRIENDLY:
//                System.out.println("MSG_DOT_RANGE_FRIENDLY 02");
//                return false;
//
//            case MSG_DOT_RANGE_FOE:
//                System.out.println("MSG_DOT_RANGE_FOE 02");
//                return false;

            // TODO: Storebror: Implement Patch Level Replication
            case MSG_PATCHLEVEL:
                this.readPatchLevel(netmsginput, false);
                return true;
            // ---

            default:
                return false;
        }
    }

    public void netFileAnswer(NetFileRequest netfilerequest) {
        if (this.netUserRegiment.netFileRequest == netfilerequest) {
            this.netUserRegiment.netFileRequest = null;
            if (netfilerequest.state() != 0 && !NetFilesTrack.existFile(netfilerequest)) return;
            this.netUserRegiment.setLocalFileNameBmp(netfilerequest.localFileName());
            if (this.netUserRegiment.localFileNameBmp != null) NetFilesTrack.recordFile(Main.cur().netFileServerReg, this, this.netUserRegiment.ownerFileNameBmp, this.netUserRegiment.localFileNameBmp);
            if (!this.netUserRegiment.isEmpty()) {
                Aircraft aircraft = this.findAircraft();
                if (aircraft != null) {
                    String s2 = aircraft.netName();
                    int k = s2.length();
                    int l = 0;
                    try {
                        l = Integer.parseInt(s2.substring(k - 2, k));
                    } catch (Exception exception) {}
                    aircraft.preparePaintScheme(l);
                }
            }
        } else if (this.netSkinRequest == netfilerequest) {
            this.netSkinRequest = null;
            if (netfilerequest.state() != 0 && !NetFilesTrack.existFile(netfilerequest)) return;
            this.localSkinBmp = netfilerequest.localFileName();
            if (this.localSkinBmp != null && this.localSkinBmp.length() == 0) this.localSkinBmp = null;
            else {
                this.tryPrepareSkin(this.findAircraft());
                NetFilesTrack.recordFile(Main.cur().netFileServerSkin, this, this.ownerSkinBmp, this.localSkinBmp);
            }
        } else if (this.netNoseartRequest == netfilerequest) {
            this.netNoseartRequest = null;
            if (netfilerequest.state() != 0 && !NetFilesTrack.existFile(netfilerequest)) return;
            this.localNoseartBmp = netfilerequest.localFileName();
            if (this.localNoseartBmp.length() == 0) this.localNoseartBmp = null;
            else {
                this.tryPrepareNoseart(this.findAircraft());
                NetFilesTrack.recordFile(Main.cur().netFileServerNoseart, this, this.ownerNoseartBmp, this.localNoseartBmp);
            }
        } else if (this.netPilotRequest == netfilerequest) {
            this.netPilotRequest = null;
            if (netfilerequest.state() != 0 && !NetFilesTrack.existFile(netfilerequest)) return;
            this.localPilotBmp = netfilerequest.localFileName();
            if (this.localPilotBmp.length() == 0) this.localPilotBmp = null;
            else {
                NetGunner netgunner = this.findGunner();
                Aircraft aircraft1 = this.findAircraft();
                if (netgunner == null) this.tryPreparePilot(aircraft1);
                else if (Actor.isValid(aircraft1)) this.tryPreparePilot(aircraft1, aircraft1.netCockpitAstatePilotIndx(netgunner.getCockpitNum()));
                NetFilesTrack.recordFile(Main.cur().netFileServerPilot, this, this.ownerPilotBmp, this.localPilotBmp);
            }
        } else if (this.netFileRequestMissProp == netfilerequest) {
            if (netfilerequest.state() != 0) {
                this.netFileRequestMissProp = null;
                return;
            }
            String s = netfilerequest.localFileName();
            if (s.equals(netfilerequest.ownerFileName())) s = Main.cur().netFileServerMissProp.primaryPath() + "/" + s;
            else s = Main.cur().netFileServerMissProp.alternativePath() + "/" + s;
            this.netFileRequestMissProp = null;
            int i = s.lastIndexOf(".properties");
            if (i < 0) return;
            s = s.substring(0, i);
            if (Main.cur().netServerParams.isCoop()) {
                GUINetClientCBrief guinetclientcbrief = (GUINetClientCBrief) GameState.get(46);
                if (!guinetclientcbrief.isExistTextDescription()) guinetclientcbrief.setTextDescription(s);
            } else if (Main.cur().netServerParams.isDogfight()) {
                GUINetClientDBrief guinetclientdbrief = (GUINetClientDBrief) GameState.get(40);
                if (!guinetclientdbrief.isExistTextDescription()) guinetclientdbrief.setTextDescription(s);
            }
        } else if (this.netFileRequestMissPropLocale == netfilerequest) {
            if (netfilerequest.state() != 0) {
                this.netFileRequestMissPropLocale = null;
                return;
            }
            String s1 = netfilerequest.localFileName();
            if (s1.equals(netfilerequest.ownerFileName())) s1 = Main.cur().netFileServerMissProp.primaryPath() + "/" + s1;
            else s1 = Main.cur().netFileServerMissProp.alternativePath() + "/" + s1;
            this.netFileRequestMissPropLocale = null;
            int j = s1.lastIndexOf(".properties");
            if (j < 0) return;
            s1 = s1.substring(0, j);
            j = s1.lastIndexOf("_" + RTSConf.cur.locale.getLanguage());
            if (j > 0) s1 = s1.substring(0, j);
            if (Main.cur().netServerParams.isCoop()) {
                GUINetClientCBrief guinetclientcbrief1 = (GUINetClientCBrief) GameState.get(46);
                guinetclientcbrief1.setTextDescription(s1);
            } else if (Main.cur().netServerParams.isDogfight()) {
                GUINetClientDBrief guinetclientdbrief1 = (GUINetClientDBrief) GameState.get(40);
                guinetclientdbrief1.setTextDescription(s1);
            }
        }
    }

    public void recordNetFiles() {
        if (this.netUserRegiment.localFileNameBmp != null) NetFilesTrack.recordFile(Main.cur().netFileServerReg, this, this.netUserRegiment.ownerFileNameBmp, this.netUserRegiment.localFileNameBmp);
        if (this.localSkinBmp != null) NetFilesTrack.recordFile(Main.cur().netFileServerSkin, this, this.ownerSkinBmp, this.localSkinBmp);
        if (this.localPilotBmp != null) NetFilesTrack.recordFile(Main.cur().netFileServerPilot, this, this.ownerPilotBmp, this.localPilotBmp);
        if (this.localNoseartBmp != null) NetFilesTrack.recordFile(Main.cur().netFileServerNoseart, this, this.ownerNoseartBmp, this.localNoseartBmp);
    }

    public void setMissProp(String s) {
        if (!Config.isUSE_RENDER()) return;
        if (Main.cur().netServerParams.isMaster()) return;
        if (Main.cur().netServerParams.isCoop()) {
            GUINetClientCBrief guinetclientcbrief = (GUINetClientCBrief) GameState.get(46);
            guinetclientcbrief.clearTextDescription();
        } else if (Main.cur().netServerParams.isDogfight()) {
            GUINetClientDBrief guinetclientdbrief = (GUINetClientDBrief) GameState.get(40);
            guinetclientdbrief.clearTextDescription();
        }
        if (!s.startsWith("missions/")) return;
        s = s.substring("missions/".length());
        for (int i = s.length() - 1; i > 0; i--) {
            char c = s.charAt(i);
            if (c == '\\' || c == '/') break;
            if (c != '.') continue;
            s = s.substring(0, i);
            break;
        }

        if (this.netFileRequestMissProp != null) {
            this.netFileRequestMissProp.doCancel();
            this.netFileRequestMissProp = null;
        }
        if (this.netFileRequestMissPropLocale != null) {
            this.netFileRequestMissPropLocale.doCancel();
            this.netFileRequestMissPropLocale = null;
        }
        if (!RTSConf.cur.locale.equals(Locale.US)) {
            this.netFileRequestMissPropLocale = new NetFileRequest(this, Main.cur().netFileServerMissProp, 220, Main.cur().netServerParams, s + "_" + RTSConf.cur.locale.getLanguage() + ".properties");
            this.netFileRequestMissPropLocale.doRequest();
        }
        this.netFileRequestMissProp = new NetFileRequest(this, Main.cur().netFileServerMissProp, 210, Main.cur().netServerParams, s + ".properties");
        this.netFileRequestMissProp.doRequest();
    }

    public void setUserRegiment(String s, String s1, char ac[], int i) {
        if (this.netUserRegiment.equals(s, s1, ac, i)) return;
        this.netUserRegiment.set(s, s1, ac, i);
        if (this.netUserRegiment.netFileRequest != null) {
            this.netUserRegiment.netFileRequest.doCancel();
            this.netUserRegiment.netFileRequest = null;
        }
        if (this.isMaster()) {
            this.netUserRegiment.setLocalFileNameBmp(this.netUserRegiment.ownerFileNameBmp());
            if (NetMissionTrack.isRecording()) NetFilesTrack.recordFile(Main.cur().netFileServerReg, this, this.netUserRegiment.ownerFileNameBmp, this.netUserRegiment.localFileNameBmp);
        } else if (this.netUserRegiment.ownerFileNameBmp().length() > 0 && (Config.cur.netSkinDownload || this.masterChannel() instanceof NetChannelInStream)) {
            this.netUserRegiment.netFileRequest = new NetFileRequest(this, Main.cur().netFileServerReg, 200, this, this.netUserRegiment.ownerFileNameBmp());
            this.netUserRegiment.netFileRequest.doRequest();
        }
    }

    public void replicateNetUserRegiment() {
        this.replicateNetUserRegiment(null);
    }

    private void replicateNetUserRegiment(NetChannel netchannel) {
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted(1);
            netmsgguaranted.writeByte(10);
            netmsgguaranted.write255(this.netUserRegiment.branch());
            netmsgguaranted.writeChar(this.netUserRegiment.aid()[0]);
            netmsgguaranted.writeChar(this.netUserRegiment.aid()[1]);
            netmsgguaranted.writeByte(this.netUserRegiment.gruppeNumber());
            netmsgguaranted.write255(this.netUserRegiment.ownerFileNameBmp);
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void tryPrepareSkin(NetAircraft netaircraft) {
        if (!Config.isUSE_RENDER()) return;
        if (!Actor.isValid(netaircraft)) return;
        if (this.localSkinBmp == null) return;
        Aircraft aircraft = (Aircraft) netaircraft;
        Class class1 = aircraft.getClass();
        Regiment regiment = aircraft.getRegiment();
        String s = regiment.country();
        String s1 = Aircraft.getPropertyMesh(class1, s);
        if (this.skinDir == null) {
            String s2 = s1;
            int i = s2.lastIndexOf('/');
            if (i >= 0) s2 = s2.substring(0, i + 1) + "summer";
            else s2 = s2 + "summer";
            NetFileServerSkin netfileserverskin = Main.cur().netFileServerSkin;
            String s3;
            if (this.ownerSkinBmp.equals(this.localSkinBmp)) {
                s3 = netfileserverskin.primaryPath() + "/" + this.localSkinBmp;
                this.skinDir = "" + Finger.file(0L, s3, -1);
            } else {
                s3 = netfileserverskin.alternativePath() + "/" + this.localSkinBmp;
                int j = this.localSkinBmp.lastIndexOf('.');
                if (j >= 0) this.skinDir = this.localSkinBmp.substring(0, j);
                else this.skinDir = this.localSkinBmp;
            }
            this.skinDir = "PaintSchemes/Cache/" + this.skinDir;
            try {
                File file = new File(HomePath.toFileSystemName(this.skinDir, 0));
                if (!file.isDirectory()) file.mkdir();
            } catch (Exception exception) {
                this.skinDir = null;
            }
            if (!BmpUtils.bmp8PalTo4TGA4(s3, s2, this.skinDir)) this.skinDir = null;
        }
        if (this.skinDir == null) return;
        else {
            Aircraft.prepareMeshCamouflage(s1, aircraft.hierMesh(), this.skinDir, this.cacheSkinMat);
            return;
        }
    }

    public void setSkin(String s) {
        if (s == null) s = "";
        if (s.equals(this.ownerSkinBmp)) return;
        this.ownerSkinBmp = s;
        this.localSkinBmp = null;
        this.skinDir = null;
        if (this.netSkinRequest != null) {
            this.netSkinRequest.doCancel();
            this.netSkinRequest = null;
        }
        if (this.isMaster()) {
            if (s.length() > 0) {
                this.localSkinBmp = this.ownerSkinBmp;
                if (NetMissionTrack.isRecording()) NetFilesTrack.recordFile(Main.cur().netFileServerSkin, this, this.ownerSkinBmp, this.localSkinBmp);
            } else this.localSkinBmp = null;
        } else if (s.length() > 0 && (Config.cur.netSkinDownload || this.masterChannel() instanceof NetChannelInStream)) {
            this.netSkinRequest = new NetFileRequest(this, Main.cur().netFileServerSkin, 100, this, this.ownerSkinBmp);
            this.netSkinRequest.doRequest();
        }
    }

    public void replicateSkin() {
        this.replicateSkin(null);
    }

    private void replicateSkin(NetChannel netchannel) {
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(11);
            netmsgguaranted.write255(this.ownerSkinBmp);
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void checkReplicateSkin(String s) {
        if (!this.isMaster()) return;
        if (!"".equals(this.ownerSkinBmp)) return;
        UserCfg usercfg = World.cur().userCfg;
        String s1 = usercfg.getSkin(s);
        if (s1 == null) return;
        else {
            this.setSkin(GUIAirArming.validateFileName(s) + "/" + s1);
            this.replicateSkin();
            return;
        }
    }

    public void tryPreparePilot(NetAircraft netaircraft) {
        this.tryPreparePilotSkin(netaircraft, 0);
    }

    public void tryPreparePilot(NetAircraft netaircraft, int i) {
        this.tryPreparePilotSkin(netaircraft, i);
    }

    public void tryPreparePilot(Paratrooper paratrooper) {
        this.tryPreparePilotSkin(paratrooper, 0);
    }

    public void tryPreparePilotSkin(Actor actor, int i) {
        if (!Config.isUSE_RENDER()) return;
        if (!Actor.isValid(actor)) return;
        if (i < 0) return;
        if (this.localPilotBmp == null) return;
        if (this.localPilotTga == null) {
            NetFileServerPilot netfileserverpilot = Main.cur().netFileServerPilot;
            String s1;
            if (this.ownerPilotBmp.equals(this.localPilotBmp)) s1 = netfileserverpilot.primaryPath() + "/" + this.localPilotBmp;
            else s1 = netfileserverpilot.alternativePath() + "/" + this.localPilotBmp;
            this.localPilotTga = this.localPilotBmp.substring(0, this.localPilotBmp.length() - 4);
            if (!BmpUtils.bmp8PalToTGA3(s1, "PaintSchemes/Cache/Pilot" + this.localPilotTga + ".tga")) {
                this.localPilotTga = null;
                return;
            }
        }
        if (this.localPilotTga == null) return;
        String s = "PaintSchemes/Cache/Pilot" + this.localPilotTga + ".mat";
        String s2 = "PaintSchemes/Cache/Pilot" + this.localPilotTga + ".tga";
        if (actor instanceof NetAircraft) Aircraft.prepareMeshPilot(((NetAircraft) actor).hierMesh(), i, s, s2, this.cachePilotMat);
        else if (actor instanceof Paratrooper) ((Paratrooper) actor).prepareSkin(s, s2, this.cachePilotMat);
    }

    public void tryPreparePilotDefaultSkin(Aircraft aircraft, int i) {
        if (!Config.isUSE_RENDER()) return;
        if (!Actor.isValid(aircraft)) return;
        if (i < 0) return;
        else {
            String s = Aircraft.getPropertyMesh(aircraft.getClass(), aircraft.getRegiment().country());
            String s1 = HomePath.concatNames(s, "pilot" + (1 + i) + ".mat");
            Aircraft.prepareMeshPilot(aircraft.hierMesh(), i, s1, "3do/plane/textures/pilot" + (1 + i) + ".tga");
            return;
        }
    }

    public void setPilot(String s) {
        if (s == null) s = "";
        if (s.equals(this.ownerPilotBmp)) return;
        this.ownerPilotBmp = s;
        this.localPilotBmp = null;
        this.localPilotTga = null;
        if (this.netPilotRequest != null) {
            this.netPilotRequest.doCancel();
            this.netPilotRequest = null;
        }
        if (this.isMaster()) {
            if (s.length() > 0) {
                this.localPilotBmp = this.ownerPilotBmp;
                if (NetMissionTrack.isRecording()) NetFilesTrack.recordFile(Main.cur().netFileServerPilot, this, this.ownerPilotBmp, this.localPilotBmp);
            } else this.localPilotBmp = null;
        } else if (s.length() > 0 && (Config.cur.netSkinDownload || this.masterChannel() instanceof NetChannelInStream)) {
            this.netPilotRequest = new NetFileRequest(this, Main.cur().netFileServerPilot, 150, this, this.ownerPilotBmp);
            this.netPilotRequest.doRequest();
        }
    }

    public void replicatePilot() {
        this.replicatePilot(null);
    }

    private void replicatePilot(NetChannel netchannel) {
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(12);
            netmsgguaranted.write255(this.ownerPilotBmp);
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
    public void replicatePatchLevel(NetUser theNetUser) {
        if (theNetUser.getPatchLevel().equalsIgnoreCase("none")) return;
// replicatePatchLevel(null);
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null) return;
        if (!netserverparams.isMaster()) return;
        if (!NetEnv.host().isMirrored()) return;
        List list = NetEnv.channels();
        for (int j = 0; j < list.size(); j++) {
            NetChannel netchannel = (NetChannel) list.get(j);
            if (netchannel.isMirrored(this) || netchannel == this.masterChannel()) this.replicatePatchLevel(netchannel, theNetUser);
        }

    }

    private void replicatePatchLevel(NetChannel netchannel, NetUser theNetUser) {
        if (theNetUser.getPatchLevel().equalsIgnoreCase("none")) return;
// System.out.println("replicatePatchLevel, " + (netchannel == null ? "netchannel == null" : "netchannel != null"));
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(MSG_PATCHLEVEL);
            netmsgguaranted.write255(theNetUser.getPatchLevel());
// if (!Config.isUSE_RENDER())
            netmsgguaranted.writeNetObj(theNetUser);
// System.out.println("Replicating Patchlevel " + theNetUser.getPatchLevel() + " for user " + theNetUser.uniqueName() + " to " + (netchannel == null ? "all" : "Channel " + netchannel.id()));
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void replicateSelectorVersion(NetUser theNetUser) {
        if (theNetUser.getSelectorVersion().equalsIgnoreCase("unknown")) return;
// replicateSelectorVersion(null);
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null) return;
        if (!netserverparams.isMaster()) return;
        if (!NetEnv.host().isMirrored()) return;
        List list = NetEnv.channels();
        for (int j = 0; j < list.size(); j++) {
            NetChannel netchannel = (NetChannel) list.get(j);
            if (netchannel.isMirrored(this) || netchannel == this.masterChannel()) this.replicateSelectorVersion(netchannel, theNetUser);
        }

    }

    private void replicateSelectorVersion(NetChannel netchannel, NetUser theNetUser) {
        if (theNetUser.getSelectorVersion().equalsIgnoreCase("unknown")) return;
// System.out.println("replicateSelectorVersion, " + (netchannel == null ? "netchannel == null" : "netchannel != null"));
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(MSG_SELECTOR_VERSION);
            netmsgguaranted.write255(theNetUser.getSelectorVersion());
// if (!Config.isUSE_RENDER())
            netmsgguaranted.writeNetObj(theNetUser);
// System.out.println("Replicating Selector Version " + theNetUser.getSelectorVersion() + " for user " + theNetUser.uniqueName() + " to " + (netchannel == null ? "all" : "Channel " + netchannel.id()));
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void replicateUltrapackVersion(NetUser theNetUser) {
        if (theNetUser.getUltrapackVersion().equalsIgnoreCase("3 RC4")) return;
// replicateSelectorVersion(null);
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null) return;
        if (!netserverparams.isMaster()) return;
        if (!NetEnv.host().isMirrored()) return;
        List list = NetEnv.channels();
        for (int j = 0; j < list.size(); j++) {
            NetChannel netchannel = (NetChannel) list.get(j);
            if (netchannel.isMirrored(this) || netchannel == this.masterChannel()) this.replicateUltrapackVersion(netchannel, theNetUser);
        }
    }

    private void replicateUltrapackVersion(NetChannel netchannel, NetUser theNetUser) {
        if (theNetUser.getUltrapackVersion().equalsIgnoreCase("3 RC4")) return;
// System.out.println("replicateSelectorVersion, " + (netchannel == null ? "netchannel == null" : "netchannel != null"));
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(MSG_ULTRAPACK_VERSION);
            netmsgguaranted.write255(theNetUser.getUltrapackVersion());
// if (!Config.isUSE_RENDER())
            netmsgguaranted.writeNetObj(theNetUser);
// System.out.println("Replicating Selector Version " + theNetUser.getSelectorVersion() + " for user " + theNetUser.uniqueName() + " to " + (netchannel == null ? "all" : "Channel " + netchannel.id()));
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void replicateHotfixVersion(NetUser theNetUser) {
        if (theNetUser.getHotfixVersion().equalsIgnoreCase("none")) return;
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null) return;
        if (!netserverparams.isMaster()) return;
        if (!NetEnv.host().isMirrored()) return;
        List list = NetEnv.channels();
        for (int j = 0; j < list.size(); j++) {
            NetChannel netchannel = (NetChannel) list.get(j);
            if (netchannel.isMirrored(this) || netchannel == this.masterChannel()) this.replicateHotfixVersion(netchannel, theNetUser);
        }

    }

    private void replicateHotfixVersion(NetChannel netchannel, NetUser theNetUser) {
        if (theNetUser.getHotfixVersion().equalsIgnoreCase("none")) return;
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(MSG_HOTFIX_VERSION);
            netmsgguaranted.write255(theNetUser.getHotfixVersion());
            netmsgguaranted.writeNetObj(theNetUser);

            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private void checkUpdateChatMessages() {
        long l = Time.real();
        if (this.lastUpdateChatMessages + UPDATE_CHAT_INTERVAL > l) return;
        this.lastUpdateChatMessages = l;
        if (!Mission.isNet()) return;
        if (!Mission.isPlaying()) return;
        if (Main.cur().netServerParams == null) return;
        if (Main.cur().netServerParams.masterChannel() instanceof NetChannelInStream) return;
        if (!Main.cur().netServerParams.isMaster()) return;
        if (!NetEnv.host().isMirrored()) return;
        List list = NetEnv.hosts();
        for (int j = 0; j < list.size(); j++) {
            NetUser netuser = (NetUser) list.get(j);
            if (netuser.getUltrapackVersion().equalsIgnoreCase(Config.getVersionNumber()) && netuser.getPatchLevel().equalsIgnoreCase(Config.getPatchLevel())) continue;
            if (!this.needsUpdate(netuser.getPatchLevel())) continue;
            if (!netuser.updateChatMessagesLogged) {
                System.out.println("User " + netuser.uniqueName() + " gets notified of available update. UP Version is " + netuser.getUltrapackVersion() + ", Patch Level is " + netuser.getPatchLevel() + ".");
                netuser.updateChatMessagesLogged = true;
            }
            ArrayList arraylist = new ArrayList(1);
            arraylist.add(netuser);
// System.out.println(this.uniqueName() + " sends Patch Update Message to " + netuser.uniqueName() + " (using Patch Level " + netuser.getPatchLevel() + ")");
            if (netuser.getUltrapackVersion().equalsIgnoreCase("unknown") || netuser.getPatchLevel().equalsIgnoreCase("none"))
                Main.cur().chat.send(NetEnv.host(), "!! " + netuser.uniqueName() + " !! You've got an outdated Ultrapack Version installed !!", arraylist, (byte) 0, false);
            else Main.cur().chat.send(NetEnv.host(), "!! " + netuser.uniqueName() + " !! You've got Ultrapack " + netuser.getUltrapackVersion() + " Patch " + netuser.getPatchLevel() + " installed !!", arraylist, (byte) 0, false);
            Main.cur().chat.send(NetEnv.host(), "!! " + netuser.uniqueName() + " !! New Version/Patch " + Config.getVersionNumber() + "/" + Config.getPatchLevel() + " is available at <<< https://j.mp/sas-up3 >>> !!", arraylist, (byte) 0, false);
        }
    }

    // ---

    public void checkReplicatePilot() {
        if (!this.isMaster()) return;
        if (!"".equals(this.ownerPilotBmp)) return;
        UserCfg usercfg = World.cur().userCfg;
        String s = usercfg.netPilot;
        if (s == null) return;
        else {
            this.setPilot(s);
            this.replicatePilot();
            return;
        }
    }

    public void tryPrepareNoseart(NetAircraft netaircraft) {
        if (!Config.isUSE_RENDER()) return;
        if (!Actor.isValid(netaircraft)) return;
        if (this.localNoseartBmp == null) return;
        if (this.localNoseartTga == null) {
            NetFileServerNoseart netfileservernoseart = Main.cur().netFileServerNoseart;
            String s;
            if (this.ownerNoseartBmp.equals(this.localNoseartBmp)) s = netfileservernoseart.primaryPath() + "/" + this.localNoseartBmp;
            else s = netfileservernoseart.alternativePath() + "/" + this.localNoseartBmp;
            this.localNoseartTga = this.localNoseartBmp.substring(0, this.localNoseartBmp.length() - 4);
            if (!BmpUtils.bmp8PalTo2TGA4(s, "PaintSchemes/Cache/Noseart0" + this.localNoseartTga + ".tga", "PaintSchemes/Cache/Noseart1" + this.localNoseartTga + ".tga")) {
                this.localNoseartTga = null;
                return;
            }
        }
        if (this.localNoseartTga == null) return;
        else {
            Aircraft.prepareMeshNoseart(netaircraft.hierMesh(), "PaintSchemes/Cache/Noseart0" + this.localNoseartTga + ".mat", "PaintSchemes/Cache/Noseart1" + this.localNoseartTga + ".mat", "PaintSchemes/Cache/Noseart0" + this.localNoseartTga + ".tga",
                    "PaintSchemes/Cache/Noseart1" + this.localNoseartTga + ".tga", this.cacheNoseartMat);
            return;
        }
    }

    public void setNoseart(String s) {
        if (s == null) s = "";
        if (s.equals(this.ownerNoseartBmp)) return;
        this.ownerNoseartBmp = s;
        this.localNoseartBmp = null;
        this.localNoseartTga = null;
        if (this.netNoseartRequest != null) {
            this.netNoseartRequest.doCancel();
            this.netNoseartRequest = null;
        }
        if (this.isMaster()) {
            if (s.length() > 0) {
                this.localNoseartBmp = this.ownerNoseartBmp;
                if (NetMissionTrack.isRecording()) NetFilesTrack.recordFile(Main.cur().netFileServerNoseart, this, this.ownerNoseartBmp, this.localNoseartBmp);
            } else this.localNoseartBmp = null;
        } else if (s.length() > 0 && (Config.cur.netSkinDownload || this.masterChannel() instanceof NetChannelInStream)) {
            this.netNoseartRequest = new NetFileRequest(this, Main.cur().netFileServerNoseart, 175, this, this.ownerNoseartBmp);
            this.netNoseartRequest.doRequest();
        }
    }

    public void replicateNoseart() {
        this.replicateNoseart(null);
    }

    private void replicateNoseart(NetChannel netchannel) {
        if (!this.isMirrored() && netchannel == null) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(32);
            netmsgguaranted.write255(this.ownerNoseartBmp);
            if (netchannel == null) this.post(netmsgguaranted);
            else this.postTo(netchannel, netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public void checkReplicateNoseart(String s) {
        if (!this.isMaster()) return;
        if (!"".equals(this.ownerNoseartBmp)) return;
        UserCfg usercfg = World.cur().userCfg;
        String s1 = usercfg.getNoseart(s);
        if (s1 == null) return;
        else {
            this.setNoseart(s1);
            this.replicateNoseart();
            return;
        }
    }

    public String radio() {
        return this.radio;
    }

    public int curCodec() {
        return this.curCodec;
    }

    public boolean isRadioNone() {
        return this.radio == null;
    }

    public boolean isRadioCommon() {
        return " 0".equals(this.radio);
    }

    public boolean isRadioArmy() {
        if (this.radio == null) return false;
        if (this.radio.length() < 2) return false;
        if (this.radio.charAt(0) != ' ') return false;
        return this.radio.charAt(1) != '0';
    }

    public boolean isRadioPrivate() {
        return !this.isRadioNone() && !this.isRadioCommon() && !this.isRadioArmy();
    }

    public void setRadio(String s, int i) {
        this.replicateRadio(s, i);
    }

    public void radio_onCreated(String s) {
        if (!Chat.USE_NET_PHONE) return;
        if (this.radio != null && this.radio.equals(s)) Chat.radioSpawn.set(this.radio);
    }

    private void radio_onArmyChanged() {
        if (this.isMirror()) return;
        if (!this.isRadioArmy()) return;
        else {
            this.replicateRadio(" " + this.getArmy(), 1);
            return;
        }
    }

    private void replicateRadio(String s, int i) {
        if (this.radio == s) return;
        if (s != null && s.equals(this.radio)) return;
        this.radio = s;
        this.curCodec = i;
        if (!Chat.USE_NET_PHONE) return;
        if (Main.cur().netServerParams == null) return;
        if (Main.cur().netServerParams.isMaster() && this.radio != null && !Chat.radioSpawn.isExistChannel(this.radio)) Chat.radioSpawn.create(this.radio, this.curCodec);
        if (this.isMaster()) if (this.radio == null) Chat.radioSpawn.set(null);
        else if (Chat.radioSpawn.isExistChannel(this.radio)) Chat.radioSpawn.set(this.radio);
        if (Main.cur().netServerParams.isMaster()) {
            ArrayList arraylist = null;
            int j = Chat.radioSpawn.getNumChannels();
            for (int k = 0; k < j; k++) {
                String s1 = Chat.radioSpawn.getChannelName(k);
                if (!s1.equals(((NetUser) NetEnv.host()).radio())) {
                    boolean flag = false;
                    List list = NetEnv.hosts();
                    for (int i1 = 0; i1 < list.size(); i1++) {
                        NetUser netuser = (NetUser) list.get(i1);
                        if (!s1.equals(netuser.radio)) continue;
                        flag = true;
                        break;
                    }

                    if (!flag) {
                        if (arraylist == null) arraylist = new ArrayList();
                        arraylist.add(s1);
                    }
                }
            }

            if (arraylist != null) for (int l = 0; l < arraylist.size(); l++)
                Chat.radioSpawn.kill((String) arraylist.get(l));
        }
        if (!this.isMirrored()) return;
        try {
            NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
            netmsgguaranted.writeByte(21);
            if (this.radio != null) {
                netmsgguaranted.write255(this.radio);
                netmsgguaranted.writeInt(this.curCodec);
            }
            this.post(netmsgguaranted);
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    private void tryPostChatTimeSpeed() {
        if (!NetChannel.bCheckServerTimeSpeed) return;
        if (this.isDestroyed()) return;
        if (Main.cur().chat == null) return;
        if (this.masterChannel() == null) return;
        try {
            if (this.masterChannel().isMirrored(Main.cur().chat)) {
                ArrayList arraylist = new ArrayList(1);
                arraylist.add(this);
                Main.cur().chat.send(null, "checkTimeSpeed " + NetChannel.checkTimeSpeedInterval + "sec" + " " + (int) Math.round(NetChannel.checkTimeSpeedDifferense * 100D) + "%", arraylist, (byte) 0, false);
            } else new MsgAction(64, 1.0D, this) {

                public void doAction(Object obj) {
                    NetUser netuser = (NetUser) obj;
                    netuser.tryPostChatTimeSpeed();
                }

            };
        } catch (Exception exception) {
            NetObj.printDebug(exception);
        }
        return;
    }

    public Aircraft findAircraft() {
        for (java.util.Map.Entry entry = Engine.name2Actor().nextEntry(null); entry != null; entry = Engine.name2Actor().nextEntry(entry)) {
            Actor actor = (Actor) entry.getValue();
            if (actor instanceof Aircraft && ((Aircraft) actor).netUser() == this) return (Aircraft) actor;
        }

        return null;
    }

    public NetGunner findGunner() {
        for (java.util.Map.Entry entry = Engine.name2Actor().nextEntry(null); entry != null; entry = Engine.name2Actor().nextEntry(entry)) {
            Actor actor = (Actor) entry.getValue();
            if (actor instanceof NetGunner && ((NetGunner) actor).getUser() == this) return (NetGunner) actor;
        }

        return null;
    }

    public void destroy() {
        // TODO: Brought here by SAS~Storebror
        // ---------------------------------------------
        ZutiSupportMethods_NetSend.releaseCarrierSpawnPlace(this);
        // ---------------------------------------------
        super.destroy();
        if (this.isMirror() && NetEnv.isServer() && !((Connect) NetEnv.cur().connect).banned.isExist(this.shortName)) Chat.sendLog(0, "user_leaves", this.shortName(), null);
        if (Actor.isValid(this.netUserRegiment)) this.netUserRegiment.destroy();
        if (this.airNetFilter != null) {
            NetChannel netchannel = this.masterChannel();
            if (netchannel != null && !netchannel.isDestroying()) netchannel.filterRemove(this.airNetFilter);
            this.airNetFilter = null;
        }
        if (Mission.isPlaying()) {
            if (Mission.isCoop() && Time.current() > 1L) new NetUserLeft(this.uniqueName(), this.army, this.curstat);
            EventLog.onDisconnected(this.uniqueName());
        }

        // TODO: Removed here by SAS~Storebror
        // TODO: Modified by |ZUTI|
        // ---------------------------------------------
//        ZutiSupportMethods_NetSend.releaseCarrierSpawnPlace(this);
        // ---------------------------------------------
    }

    public void msgNetNewChannel(NetChannel netchannel) {
        try {
            if (netchannel.isMirrored(this)) return;
            int j = 0;
            if (Main.cur() instanceof Main3D && Main3D.cur3D().isDemoPlaying()) if (this.isMaster()) if (NetMissionTrack.isPlaying()) return;
            else j = 1;

            NetMsgSpawn netmsgspawn = new NetMsgSpawn(this);
            netmsgspawn.write255(this.shortName);
            netmsgspawn.writeByte(this.bornPlace);
            netmsgspawn.writeByte(this.place);
            netmsgspawn.writeShort(this.idChannelFirst);
            if (this.isMirror()) {
                if (this.path != null) for (int i = 0; i < this.path.length; i++)
                    netmsgspawn.writeNetObj(this.path[i]);
                if (j == 0) netmsgspawn.writeNetObj(NetEnv.host());
            } else if (!NetEnv.isServer() && !this.bPingUpdateStarted) {
                this.bPingUpdateStarted = true;
                this.pingUpdateInc();
            }
            if (netchannel instanceof NetChannelOutStream && Main.cur() instanceof Main3D && Main3D.cur3D().isDemoPlaying() && this.bTrackWriter) netmsgspawn.writeBoolean(true);
            else netmsgspawn.writeBoolean(false);
            this.postTo(netchannel, netmsgspawn);
            if (!"".equals(this.netUserRegiment.ownerFileNameBmp)) this.replicateNetUserRegiment(netchannel);
            if (!"".equals(this.ownerSkinBmp)) this.replicateSkin(netchannel);
            if (!"".equals(this.ownerPilotBmp)) this.replicatePilot(netchannel);
            if (!"".equals(this.ownerNoseartBmp)) this.replicateNoseart(netchannel);
            if (this.radio != null) {
                NetMsgGuaranted netmsgguaranted = new NetMsgGuaranted();
                netmsgguaranted.writeByte(21);
                netmsgguaranted.write255(this.radio);
                netmsgguaranted.writeInt(this.curCodec);
                this.postTo(netchannel, netmsgguaranted);
            }
            this.replicateDotRange(netchannel);
            // TODO: Storebror: Implement Patch Level Replication
// System.out.println("msgNetNewChannel replicate patch level");
            this.replicatePatchLevel(netchannel, this); // Replicate own patch level
            this.replicateSelectorVersion(netchannel, this); // Replicate own selector version
            this.replicateUltrapackVersion(netchannel, this); // Replicate own ultrapack version
            this.replicateHotfixVersion(netchannel, this); // Replicate own patch level
// replicatePatchLevel(this.masterChannel()); // Replicate own patch level to server
// ---

        }

        catch (Exception exception) {
            NetObj.printDebug(exception);
        }
    }

    public NetUser(String s) {
        super(s);
        this.stat = new NetUserStat();
        this.curstat = new NetUserStat();
        this.ping = 0;
        this.army = 0;
        this.bTrackWriter = false;
        this.bornPlace = -1;
        this.airdromeStay = -1;
        this.uniqueName = null;
        this.place = -1;
        this.bWaitStartCoopMission = false;
        this.localRequestPlace = -1;
        this.ownerSkinBmp = "";
        this.cacheSkinMat = new Mat[3];
        this.ownerPilotBmp = "";
        this.cachePilotMat = new Mat[1];
        this.ownerNoseartBmp = "";
        this.cacheNoseartMat = new Mat[2];
        this.radio = null;
        this.curCodec = 0;
        this.lastTimeUpdate = 0L;
        this.bPingUpdateStarted = false;
        this.makeUniqueName();
        this.netUserRegiment = new NetUserRegiment();
        // TODO: Storebror: Implement Patch Level Replication
        if (this.isMaster()) {
            this.patchLevel = Config.getPatchLevel();
            this.hotfixVersion = Config.getHotfixVersion();
            this.selectorVersion = BaseGameVersion.selectorInfo(BaseGameVersion.SELECTOR_INFO_PRODUCT_VERSION);
            if (this.selectorVersion.equalsIgnoreCase("N/A")) this.selectorVersion = "unknown";
            this.ultrapackVersion = Config.getVersionNumber();
        }
// if (Config.isUSE_RENDER())
// this.patchLevel = PATCH_LEVEL_TEST;
        this.lastUpdateChatMessages = Time.real() - UPDATE_CHAT_INTERVAL + 10000L;
        // ---
    }

    public NetUser(NetChannel netchannel, int i, String s, NetHost anethost[]) {
        super(netchannel, i, s, anethost);
        this.stat = new NetUserStat();
        this.curstat = new NetUserStat();
        this.ping = 0;
        this.army = 0;
        this.bTrackWriter = false;
        this.bornPlace = -1;
        this.airdromeStay = -1;
        this.uniqueName = null;
        this.place = -1;
        this.bWaitStartCoopMission = false;
        this.localRequestPlace = -1;
        this.ownerSkinBmp = "";
        this.cacheSkinMat = new Mat[3];
        this.ownerPilotBmp = "";
        this.cachePilotMat = new Mat[1];
        this.ownerNoseartBmp = "";
        this.cacheNoseartMat = new Mat[2];
        this.radio = null;
        this.curCodec = 0;
        this.lastTimeUpdate = 0L;
        this.bPingUpdateStarted = false;
        if (NetEnv.isServer()) {
            // TODO: Added by |ZUTI|: check if user has any refly time
            // penalties...
            // ----------------------------------------------------------------------
            String IP = netchannel.remoteAddress().getHostAddress();
// System.out.println("NetUser - Checking if user >" + s + " [" + IP + "]< has any refly/KIA time penalties...");
            if (ZutiSupportMethods.isUserPenalized(s, IP)) {
                this.kick(this);
                System.out.println("User '" + s + "' [" + netchannel.remoteAddress().getHostAddress() + "] has refly time penalties pending!");
            }
            // ----------------------------------------------------------------------

            if (((Connect) NetEnv.cur().connect).banned.isExist(s)) {
                this.kick(this);
                System.out.println("User '" + s + "' [" + netchannel.remoteAddress().getHostAddress() + "] banned");
            } else Chat.sendLog(0, "user_joins", this.shortName(), null);
            this.airNetFilter = new AircraftNetFilter();
            netchannel.filterAdd(this.airNetFilter);
            _st.clear();
            ((NetUser) NetEnv.host()).replicateStat(this, false, netchannel);
        }
        this.makeUniqueName();
        this.netUserRegiment = new NetUserRegiment();
        if (NetEnv.isServer()) {
            System.out.println("socket channel '" + netchannel.id() + "', ip " + netchannel.remoteAddress().getHostAddress() + ":" + netchannel.remotePort() + ", " + this.uniqueName() + ", is complete created");
            // TODO: +++ NetBoost by SAS~Storebror +++
            System.out.println("maxSpeed=" + netchannel.getMaxSpeed() + "kBit/s, maxDataSize=" + netchannel.socket().getMaxDataSize());
            // TODO: --- NetBoost by SAS~Storebror ---

            new MsgAction(64, 1.0D, this) {

                public void doAction(Object obj) {
                    NetUser netuser = (NetUser) obj;
                    netuser.tryPostChatTimeSpeed();
                }

            };
        }
        // TODO: Storebror: Implement Patch Level Replication
        if (this.isMaster()) {
            this.patchLevel = Config.getPatchLevel();
            this.hotfixVersion = Config.getHotfixVersion();
            this.selectorVersion = BaseGameVersion.selectorInfo(BaseGameVersion.SELECTOR_INFO_PRODUCT_VERSION);
            if (this.selectorVersion.equalsIgnoreCase("N/A")) this.selectorVersion = "unknown";
            this.ultrapackVersion = Config.getVersionNumber();
        }
// if (Config.isUSE_RENDER())
// this.patchLevel = PATCH_LEVEL_TEST;
        this.lastUpdateChatMessages = Time.real() - UPDATE_CHAT_INTERVAL + 10000L;
        // ---
        EventLog.onConnected(this.uniqueName());
    }

    static {
        Spawn.add(NetUser.class, new SPAWN());
    }

    // TODO: |ZUTI| methods
    // -----------------------------------------
    public void zutiSetAirdomeStay(int value) {
        this.airdromeStay = value;
    }

    // TODO: Storebror: Implement Aircraft Control Surfaces and Pilot View Replication
    private void readPatchLevel(NetMsgInput netmsginput, boolean isMasterChannel) {
        if (netmsginput.available() > 0) {
            String thePatchLevel = "none";
            try {
                thePatchLevel = netmsginput.read255();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            NetUser theNetUser = null;
            if (netmsginput.available() > 0) theNetUser = (NetUser) netmsginput.readNetObj();

            if (theNetUser == null) {
                List list = NetEnv.hosts();
                for (int nui = 0; nui < list.size(); nui++)
                    if (((NetUser) list.get(nui)).masterChannel() == netmsginput.channel()) {
                        theNetUser = (NetUser) list.get(nui);
                        if (theNetUser.getPatchLevel().equalsIgnoreCase("none")) break;
                        theNetUser = null;
                    }
            }

            if (theNetUser != null) {
                theNetUser.setPatchLevel(thePatchLevel);
// System.out.println("MSG_PATCHLEVEL received (Master = " + isMasterChannel + ") for netuser " + theNetUser.uniqueName() + ", Patch Level is " + thePatchLevel);
                this.replicatePatchLevel(theNetUser);
            } else {
// System.out.println("MSG_PATCHLEVEL received (Master = " + isMasterChannel + ") for netuser null, Patch Level is " + thePatchLevel);
            }
            // this.setPatchLevel(netmsginput.read255());
        }
    }

    private void readSelectorVersion(NetMsgInput netmsginput, boolean isMasterChannel) {
        if (netmsginput.available() > 0) {
            String theSelectorVersion = "unknown";
            try {
                theSelectorVersion = netmsginput.read255();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            NetUser theNetUser = null;
            if (netmsginput.available() > 0) theNetUser = (NetUser) netmsginput.readNetObj();

            if (theNetUser == null) {
                List list = NetEnv.hosts();
                for (int nui = 0; nui < list.size(); nui++)
                    if (((NetUser) list.get(nui)).masterChannel() == netmsginput.channel()) {
                        theNetUser = (NetUser) list.get(nui);
                        if (theNetUser.getSelectorVersion().equalsIgnoreCase("unknown")) break;
                        theNetUser = null;
                    }
            }

            if (theNetUser != null) {
                theNetUser.setSelectorVersion(theSelectorVersion);
// System.out.println("MSG_SELECTOR_VERSION received (Master = " + isMasterChannel + ") for netuser " + theNetUser.uniqueName() + ", Selector Version is " + theSelectorVersion);
                this.replicateSelectorVersion(theNetUser);
            } else {
// System.out.println("MSG_SELECTOR_VERSION received (Master = " + isMasterChannel + ") for netuser null, Selector Version is " + theSelectorVersion);
            }
        }
    }

    private void readUltrapackVersion(NetMsgInput netmsginput, boolean isMasterChannel) {
        if (netmsginput.available() > 0) {
            String theUltrapackVersion = "UP3 RC4";
            try {
                theUltrapackVersion = netmsginput.read255();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            NetUser theNetUser = null;
            if (netmsginput.available() > 0) theNetUser = (NetUser) netmsginput.readNetObj();

            if (theNetUser == null) {
                List list = NetEnv.hosts();
                for (int nui = 0; nui < list.size(); nui++)
                    if (((NetUser) list.get(nui)).masterChannel() == netmsginput.channel()) {
                        theNetUser = (NetUser) list.get(nui);
                        if (theNetUser.getSelectorVersion().equalsIgnoreCase("unknown")) break;
                        theNetUser = null;
                    }
            }

            if (theNetUser != null) {
                theNetUser.setUltrapackVersion(theUltrapackVersion);
// System.out.println("MSG_ULTRAPACK_VERSION received (Master = " + isMasterChannel + ") for netuser " + theNetUser.uniqueName() + ", Selector Version is " + theSelectorVersion);
                this.replicateUltrapackVersion(theNetUser);
            } else {
// System.out.println("MSG_ULTRAPACK_VERSION received (Master = " + isMasterChannel + ") for netuser null, Ultrapack Version is " + theUltrapackVersion);
            }
        }
    }

    private void readHotfixVersion(NetMsgInput netmsginput, boolean isMasterChannel) {
        if (netmsginput.available() > 0) {
            String theHotfixVersion = "none";
            try {
                theHotfixVersion = netmsginput.read255();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            NetUser theNetUser = null;
            if (netmsginput.available() > 0) theNetUser = (NetUser) netmsginput.readNetObj();

            if (theNetUser == null) {
                List list = NetEnv.hosts();
                for (int nui = 0; nui < list.size(); nui++)
                    if (((NetUser) list.get(nui)).masterChannel() == netmsginput.channel()) {
                        theNetUser = (NetUser) list.get(nui);
                        if (theNetUser.getHotfixVersion().equalsIgnoreCase("none")) break;
                        theNetUser = null;
                    }
            }

            if (theNetUser != null) {
                theNetUser.setHotfixVersion(theHotfixVersion);
                this.replicateHotfixVersion(theNetUser);
            } else {
            }
        }
    }

    // TODO: +++ New "slap" command implementation by SAS~Storebror +++
    public static void slap(NetUser netuser, int numSlaps) {
        String netUserName = netuser == null ? "null" : netuser.uniqueName();
        SlapDebug("Slap Command received for User " + netUserName + ", numSlaps=" + numSlaps);
        if (netuser == null) {
            SlapDebug("Can't slap null");
            return;
        }
        if (netuser.isDestroyed()) {
            SlapDebug(netUserName + " is destroyed already!");
            return;
        }
        NetServerParams netserverparams = Main.cur().netServerParams;
        if (netserverparams == null) {
            SlapDebug("Main.cur().netServerParams is null!");
            return;
        }
        if (!netserverparams.isMaster()) {
            SlapDebug("Main.cur().netServerParams is not Master!");
            return;
        }
        if (netuser.isMaster()) {
            SlapDebug(netUserName + " is Master!");
            return;
        }
        Aircraft aircraft = netuser.findAircraft();
        if (aircraft == null) {
            SlapDebug(netUserName + " has no Aircraft!");
            return;
        }
        if (!Actor.isValid(aircraft)) {
            SlapDebug(netUserName + " Aircraft is not valid!");
            return;
        }
        if (!Actor.isAlive(aircraft)) {
            SlapDebug(netUserName + " Aircraft is not alive!");
            return;
        }
        slapUser = netuser;
        remainingSlaps = numSlaps;
    }

    private static void doRemainingSlaps(NetUser netuser) {
        String netUserName = netuser == null ? "null" : netuser.uniqueName();
        SlapDebug("doRemainingSlaps for User " + netUserName + ", remainingSlaps=" + remainingSlaps);
        do {
            int ticksElapsed = 0;
//            if (Time.tickCounter() >= lastSlapTickCount) ticksElapsed = Time.tickCounter() - lastSlapTickCount;
//            else ticksElapsed = Integer.MAX_VALUE - lastSlapTickCount + Time.tickCounter() - Integer.MIN_VALUE + 1;

            ticksElapsed = Time.tickCounter() - lastSlapTickCount;
            if (ticksElapsed < TICK_DIVISOR && ticksElapsed > 0) // SlapDebug("Need to wait a bit more. ticksElapsed=" + ticksElapsed + ", TICK_DIVISOR=" + TICK_DIVISOR);
                return;
            if (netuser.isDestroyed()) {
                SlapDebug(netUserName + " is destroyed already!");
                break;
            }
            Aircraft aircraft = netuser.findAircraft();
            if (aircraft == null) {
                SlapDebug(netUserName + " has no Aircraft!");
                break;
            }
            if (!Actor.isValid(aircraft)) {
                SlapDebug(netUserName + " Aircraft is not valid!");
                break;
            }
            if (!Actor.isAlive(aircraft)) {
                SlapDebug(netUserName + " Aircraft is not alive!");
                break;
            }
            lastSlapTickCount = Time.tickCounter();
            Point3d p = new Point3d(aircraft.net.actor().pos.getAbsPoint());
            Tuple3d t = slapPoint(aircraft, 20, 5);
            p.add(t);
            DecimalFormat twoDigits = new DecimalFormat("00.00");
            MsgExplosion.send(null, null, p, null, 100, 100, 1, 30, 0);
            System.out.println("Slapping " + netuser.uniqueName() + " (aircraft:" + aircraft.netName() + "), slap point is " + twoDigits.format(Math.abs(t.x)) + "m " + (t.x > 0 ? "in front of" : "behind     ") + ", " + twoDigits.format(Math.abs(t.y))
                    + "m " + (t.y > 0 ? "right" : " left") + " of and " + twoDigits.format(Math.abs(t.z)) + "m " + (t.z > 0 ? "above" : "below") + " the aircraft. Remaining slaps: " + --remainingSlaps);
            return;
        } while (false);
        remainingSlaps = 0;
    }

    private static Tuple3d slapPoint(Aircraft aircraft, double distance, double minHeightAboveGround) {
        double maxTheta = Math.PI;
        Point3d aircraftPos = aircraft.net.actor().pos.getAbsPoint();
        double aircraftAlt = aircraftPos.z - Engine.land().HQ_Air(aircraftPos.x, aircraftPos.y);
        double remainingHeight = aircraftAlt - minHeightAboveGround;
        if (remainingHeight < distance) maxTheta = Math.acos(-remainingHeight / distance);
        double theta = World.Rnd().nextDouble(0D, maxTheta);
        double phi = World.Rnd().nextDouble(0D, Math.PI * 2D);
        double dx = distance * Math.sin(theta) * Math.cos(phi);
        double dy = distance * Math.sin(theta) * Math.sin(phi);
        double dz = distance * Math.cos(theta);
        return new Point3d(dx, dy, dz);
    }

    private static void SlapDebug(String logLine) {
        if (SLAP_DEBUG) System.out.println("### SLAP DEBUG ### " + logLine);
    }

    private static boolean SLAP_DEBUG = false;
    // TODO: --- New "slap" command implementation by SAS~Storebror ---

    static {
        try {
            minPatchLevel = Integer.parseInt(Config.getMinPatchLevel());
            maxPatchLevel = Integer.parseInt(Config.getMaxPatchLevel());
            greenPatchLevels = new int[Config.getPatchlevelG().length];
            yellowPatchLevels = new int[Config.getPatchlevelY().length];
            for (int i = 0; i < Config.getPatchlevelG().length; i++)
                greenPatchLevels[i] = Integer.parseInt(Config.getPatchlevelG()[i]);
            for (int i = 0; i < Config.getPatchlevelY().length; i++)
                yellowPatchLevels[i] = Integer.parseInt(Config.getPatchlevelY()[i]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
