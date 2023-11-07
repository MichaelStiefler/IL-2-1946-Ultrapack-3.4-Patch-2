package com.maddox.il2.objects.sounds;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.Front;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.Squadron;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.ai.ground.TgtShip;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.net.NetUser;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.JU_88MSTL;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeDiveBomber;
import com.maddox.il2.objects.air.TypeFighter;
import com.maddox.il2.objects.air.TypeTransport;
import com.maddox.il2.objects.trains.Train;
import com.maddox.il2.objects.trains.Wagon;
import com.maddox.il2.objects.vehicles.artillery.AAA;
import com.maddox.il2.objects.vehicles.cars.CarGeneric;
import com.maddox.il2.objects.vehicles.tanks.TankGeneric;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetEnv;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Time;

public class Voice extends VoiceBase {
    private int                SpeakRearGunKill;
    private int                SpeakPullUp;
    private int                SpeakRearGunShake;
    private int[][]            SpeakAttackByRockets     = new int[2][4];
    private int[][]            SpeakAttackByBombs       = new int[2][4];
    private int[][]            SpeakTargetDestroyed     = new int[2][4];
    private int[][]            SpeakDanger              = new int[2][4];
    private int[][]            SpeakHelpNeeded          = new int[2][4];
    private int[][]            SpeakClearTail           = new int[2][4];
    private int[][]            SpeakCoverMe             = new int[2][4];
    private int[][]            SpeakCoverProvided       = new int[2][4];
//  private int[][] SpeakHelpFromAir = new int[2][4];
    private int[][]            SpeakNiceKill            = new int[2][4];
    private int[][]            SpeakEndOfAmmo           = new int[2][4];
    private int[][]            SpeakMayday              = new int[2][4];
    private int[][]            SpeakCheckFire           = new int[2][4];
    private int[][]            SpeakHint                = new int[2][4];
    private int[][]            SpeakToReturn            = new int[2][4];
    public int[][]             SpeakBailOut             = new int[2][4];

//  private int[][] SpeakAttackGround = new int[2][4];
//  private int[][] SpeakAttackMyTarget = new int[2][4];
    private int[][]            SpeakAttackBombers       = new int[2][4];
    private int[][]            SpeakTargetAll           = new int[2][4];
//  private int[][] SpeakDropTanks = new int[2][4];
    private int[][]            SpeakBreak               = new int[2][4];
//  private int[][] SpeakRejoin = new int[2][4];
//  private int[][] SpeakTightFormation = new int[2][4];
//  private int[][] SpeakLoosenFormation = new int[2][4];
//  private int[][] SpeakOk = new int[2][4];
    private int[][]            SpeakLandingPermited     = new int[2][4];

    private int[]              SpeakBombing             = new int[2];
    private int[]              SpeakEndBombing          = new int[2];
    private int[]              SpeakEndGattack          = new int[2];
    private int[]              SpeakDeviateSmall        = new int[2];
    private int[]              SpeakDeviateBig          = new int[2];
    private int[]              SpeakHeading             = new int[2];
    private int[]              SpeakAltitude            = new int[2];
    public int[]               SpeakNearBombers         = new int[2];
    private int[]              Speak1minute             = new int[2];
    private int[]              Speak5minutes            = new int[2];
    public int[]               SpeakBombersUnderAttack  = new int[2];
    public int[]               SpeakBombersUnderAttack1 = new int[2];
    public int[]               SpeakBombersUnderAttack2 = new int[2];
    private int[]              SpeakEnemyDetected       = new int[2];
    private static Aircraft    internalAir              = null;
    private static RangeRandom rnd                      = new RangeRandom();

    public static int[]        str                      = new int[8];
    public static int[][]      str2                     = new int[4][8];
    public static final int    afPILOT                  = 0;
    public static final int    afNEARFRIEND             = 1;
    public static final int    afWINGMAN                = 2;
    public static final int    afLEADER                 = 3;
    public static final int    afREARGUN                = 4;
    public static final int    afLAND                   = 5;
    public static final int    afBOMBERS                = 6;
    public static final int    anNONE                   = 0;
    public static final int    anLEADER                 = 1;
    public static final int    anBOMBER1                = 2;
    public static final int    anBOMBER2                = 3;
    public static final int    anLAND                   = 4;
    public static final int    anREARGUN                = 5;
    public static final int    anACTOR6                 = 6;
    public static final int    anACTOR7                 = 7;
    public static final int    anACTOR8                 = 8;
    public static final int    anACTOR9                 = 9;
    private static int[]       headings                 = { 1, 3, 5, 6, 15, 20, 21, 27, 28, 30, 35, 36 };

    private static int[]       altitudes                = { 2, 4, 7, 19, 26, 29, 34, 37, 41, 42, 46, 47, 53, 54, 58, 59, 63, 64, 68, 69, 8 };

    private static final int[] clkstr                   = { 16, 23, 31, 38, 43, 50, 55, 60, 65, 70, 9, 12, 16, 24, 32, 39, 44, 51, 56, 61, 66, 71, 10, 13, 17, 25, 33, 40, 45, 52, 57, 62, 67, 72, 11, 14, 18 };

    private static final int[] aNumber                  = { 285, 326, 331, 287, 284, 329, 327, 281, 297, 330, 282, 338, 332, 286, 283, 328 };

    private static int[]       your_o_clock             = { 206, 173, 182, 185, 188, 191, 194, 197, 200, 203, 176, 179, 206, 174, 183, 186, 189, 192, 195, 198, 201, 204, 177, 180, 207, 175, 184, 187, 190, 193, 196, 199, 202, 205, 178, 181, 208 };

    private static final int[] thisIsNumber             = { 158, 360, 354, 346, 345, 357, 351, 333, 350, 358, 343, 337, 359, 349, 344, 336 };

    private static final int[] thisIsPara               = { 334, 347, 352, 355 };

    private static final int[] thisIsWing               = { 335, 348, 353, 356 };

    private static final int[] thisIsRotte              = { 361, 362, 363, 364 };

    private static final int[] thisIsSwarm              = { 365, 366, 367, 368 };

    private static final int[] pilotVoice               = { 6, 9, 8, 7, 7, 9, 8, 6, 8, 9, 7, 6, 9, 8, 7, 6 };
    public static final int    M_SYNC                   = 0;
    public static final int    M_DELAY                  = 1;
    public static final int    M_IMMED                  = 2;
    protected static int       syncMode;
    private static Point3d     P3d;

    public static Voice cur() {
        return World.cur().voicebase;
    }

    public static boolean isEnableVoices() {
        return VoiceFX.isEnabled();
    }

    public static void setEnableVoices(boolean paramBoolean) {
        VoiceFX.setEnabled(paramBoolean);
    }

    static boolean frequency() {
        return Main3D.cur3D().ordersTree.frequency().booleanValue();
    }

    public static void setSyncMode(int paramInt) {
        Voice.syncMode = paramInt;
    }

    public static void reset() {
        int i = 0;
        for (i = 0; i < Voice.str.length; i++) {
            Voice.str[i] = 0;
        }
        for (i = 0; i < Voice.str2.length; i++) {
            if (Voice.str2[i] != null) {
                for (int j = 0; j < Voice.str2[i].length; j++) {
                    Voice.str2[i][j] = 0;
                }
            }
        }
    }

    public static void endSession() {
        VoiceFX.end();
    }

//  private static boolean isPunctChar(char paramChar) {
//    return (paramChar == '!') || (paramChar == '?') || (paramChar == '.') || (paramChar == ',') || (paramChar == '@');
//  }

    public static int actorVoice(Aircraft paramAircraft, int paramInt) {
        if ((paramAircraft == null) || (!Actor.isValid(paramAircraft)) || ((paramAircraft instanceof JU_88MSTL))) {
            return 0;
        }
        if ((paramAircraft.FM instanceof Maneuver)) {
            if (((Maneuver) paramAircraft.FM).silence) {
                return 0;
            }
            if (((Maneuver) paramAircraft.FM).kamikaze) {
                return 0;
            }
        }
        Squadron localSquadron = paramAircraft.getSquadron();
        if (localSquadron == null) {
            return 0;
        }
        Wing localWing = null;
        Aircraft localAircraft1 = null;
        if ((paramInt == 3) || (paramInt == 0)) {
            if ((paramAircraft.isNet()) && (!paramAircraft.isNetMaster())) {
                return 0;
            }
            float f = 0.0F;
            for (int j = 0; j < localSquadron.wing.length; j++) {
                if (localSquadron.wing[j] == null) {
                    continue;
                }
                f += localSquadron.wing[j].aircReady();
            }
            if (f < 2.0F) {
                return 0;
            }
        }
        switch (paramInt) {
            case 3:
                for (int i = 0; i < localSquadron.wing.length; i++) {
                    if (localSquadron.wing[i] != null) {
                        localWing = localSquadron.wing[i];
                        break;
                    }
                }
                if (localWing == null) {
                    return 0;
                }
                if ((localWing.airc.length > 0) && (localWing.airc[0] != null)) {
                    localAircraft1 = localWing.airc[0];
                } else {
                    return 0;
                }
                if (!Actor.isAlive(localAircraft1)) {
                    return 0;
                }
                World.cur();
                if ((localAircraft1 == World.getPlayerAircraft()) || (localAircraft1.FM.AS.astatePilotStates[0] > 50)) {
                    return 0;
                }
                return 1;
            case 1:
                Aircraft localAircraft2 = War.getNearestFriend(paramAircraft);
                if (!Actor.isAlive(localAircraft2)) {
                    return 0;
                }
                if (localAircraft2 == World.getPlayerAircraft()) {
                    return 0;
                }
                if (Mission.isDogfight()) {
                    return 0;
                }
                return Voice.pilotVoice[((localAircraft2.getWing().indexInSquadron() * 4) + localAircraft2.aircIndex())];
            case 4:
                if (!Actor.isAlive(paramAircraft)) {
                    return 0;
                }
                if ((paramAircraft == World.getPlayerAircraft()) && (paramAircraft.FM.turret[0].bIsAIControlled)) {
                    return 5;
                }
                return 0;
            case 0:
                if (!Actor.isAlive(paramAircraft)) {
                    return 0;
                }
                if (paramAircraft == World.getPlayerAircraft()) {
                    return 0;
                }
                if (paramAircraft.FM.AS.astatePilotStates[0] > 50) {
                    return 0;
                }

                return Voice.pilotVoice[((paramAircraft.getWing().indexInSquadron() * 4) + paramAircraft.aircIndex())];
            case 6:
                return 2;
            case 5:
                return 4;
            case 2:
        }
        return 0;
    }

    public static void new_speak(int paramInt1, int paramInt2, String paramString, int[] paramArrayOfInt, int paramInt3) {
        Voice.speak(paramInt1, paramInt2, paramString, paramArrayOfInt, paramInt3, true, true);
    }

    public static void speak(int paramInt1, int paramInt2, String paramString, int[] paramArrayOfInt, int paramInt3) {
        Voice.speak(paramInt1, paramInt2, paramString, paramArrayOfInt, paramInt3, true, false);
    }

    public static void speak(int paramInt1, int paramInt2, String paramString, int[] paramArrayOfInt, int paramInt3, boolean paramBoolean1, boolean paramBoolean2) {
        int i = Voice.syncMode;
        Voice.syncMode = 0;

        if (paramInt1 == 0) {
            return;
        }

        if ((paramBoolean1) && (paramInt1 != 5)) {
            ((NetUser) NetEnv.host()).speekVoice(Voice.syncMode, paramInt1, paramInt2, paramString, paramArrayOfInt, paramInt3, paramBoolean2);
        }

        if (!Config.isUSE_RENDER()) {
            return;
        }

        int j = World.getPlayerArmy();
        if (Main3D.cur3D().ordersTree.frequency() == null) {
            return;
        }
        if (Main3D.cur3D().ordersTree.frequency().booleanValue()) {
            if (paramInt2 != j) {
                return;
            }
        } else if (paramInt2 == j) {
            return;
        }

        Voice.play(i, paramInt1, paramInt2, paramString, paramArrayOfInt, paramBoolean2);

        if (World.cur().isDebugFM()) {
            String str1 = "";
            for (int m = 0; (m < paramArrayOfInt.length) && (paramArrayOfInt[m] != 0); m++) {
                str1 = str1 + " " + VoiceBase.vbStr[paramArrayOfInt[m]];
            }
            System.out.print("AN: ");
            System.out.print(paramInt1);
            System.out.print("  Army: ");
            System.out.print(paramInt2);
            System.out.println("  : " + str1);
        }

        for (int k = 0; (k < paramArrayOfInt.length) && (paramArrayOfInt[k] != 0); k++) {
            paramArrayOfInt[k] = 0;
        }
    }

    public static void play(int paramInt1, int paramInt2, int paramInt3, String paramString, int[] paramArrayOfInt, boolean paramBoolean) {
        if (Main3D.cur3D().gameTrackPlay() != null) {
            return;
        }
        if (Main3D.cur3D().gameTrackRecord() != null) {
            try {
                NetMsgGuaranted localNetMsgGuaranted = new NetMsgGuaranted();
                localNetMsgGuaranted.writeByte(4);
                localNetMsgGuaranted.writeInt(paramInt1);
                localNetMsgGuaranted.writeInt(paramInt2);
                localNetMsgGuaranted.writeInt(paramInt3);
                localNetMsgGuaranted.writeBoolean(paramBoolean);
                int i = paramArrayOfInt.length;
                localNetMsgGuaranted.writeInt(i);
                for (int j = 0; j < i; j++) {
                    localNetMsgGuaranted.writeInt(paramArrayOfInt[j]);
                }
                if (paramString != null) {
                    localNetMsgGuaranted.write255(paramString);
                }
                Main3D.cur3D().gameTrackRecord().postTo(Main3D.cur3D().gameTrackRecord().channel(), localNetMsgGuaranted);
            } catch (Exception localException) {
            }
        }
        HUD.message(paramArrayOfInt, paramInt2, paramInt3, paramBoolean);
        VoiceFX.play(paramInt1, paramInt2, paramInt3, paramString, paramArrayOfInt);
    }

    public static boolean netInputPlay(NetMsgInput paramNetMsgInput) throws IOException {
        int i = paramNetMsgInput.readInt();
        int j = paramNetMsgInput.readInt();
        int k = paramNetMsgInput.readInt();
        boolean bool = paramNetMsgInput.readBoolean();
        int m = paramNetMsgInput.readInt();
        int[] arrayOfInt = new int[m];
        for (int n = 0; n < m; n++) {
            arrayOfInt[n] = paramNetMsgInput.readInt();
        }
        String str1 = null;
        if (paramNetMsgInput.available() > 0) {
            str1 = paramNetMsgInput.read255();
        }
        HUD.message(arrayOfInt, j, k, bool);
        VoiceFX.play(i, j, k, str1, arrayOfInt);
        return true;
    }

    public static void speak(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4) {
        Voice.str[0] = paramInt3;
        Voice.str[1] = 0;
        Voice.speak(paramInt1, paramInt2, paramString, Voice.str, paramInt4);
    }

    public static void new_speak(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4) {
        Voice.str[0] = paramInt3;
        Voice.str[1] = 0;
        Voice.new_speak(paramInt1, paramInt2, paramString, Voice.str, paramInt4);
    }

    public static void speakRandom(int paramInt1, int paramInt2, String paramString, int[] paramArrayOfInt, int paramInt3) {
        int i = 0;
        for (i = 0; (i < paramArrayOfInt.length) && (paramArrayOfInt[i] != 0); i++) {
            ;
        }
        if (i < 1) {
            return;
        }
        int j = Voice.rnd.nextInt(0, i - 1);
        Voice.speak(paramInt1, paramInt2, paramString, paramArrayOfInt[j], paramInt3);
    }

    public static void speakNewRandom(int paramInt1, int paramInt2, String paramString, int[] paramArrayOfInt, int paramInt3) {
        int i = 0;
        for (i = 0; (i < paramArrayOfInt.length) && (paramArrayOfInt[i] != 0); i++) {
            ;
        }
        if (i < 1) {
            return;
        }
        int j = Voice.rnd.nextInt(0, i - 1);
        Voice.new_speak(paramInt1, paramInt2, paramString, paramArrayOfInt[j], paramInt3);
    }

    public static void airSpeaksArray(Aircraft paramAircraft, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
        int i = Voice.actorVoice(paramAircraft, paramInt1);
        if (i == 0) {
            return;
        }
        int j = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.speak(i, j, str1, paramArrayOfInt, paramInt2);
    }

    public static void airSpeaksNewArray(Aircraft paramAircraft, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
        int i = Voice.actorVoice(paramAircraft, paramInt1);
        if (i == 0) {
            return;
        }
        int j = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.new_speak(i, j, str1, paramArrayOfInt, paramInt2);
    }

    public static void airSpeaks(Aircraft paramAircraft, int paramInt1, int paramInt2, int paramInt3) {
        Voice.str[0] = paramInt2;
        Voice.str[1] = 0;
        Voice.airSpeaksArray(paramAircraft, paramInt1, Voice.str, paramInt3);
    }

    public static void airSpeaks(Aircraft paramAircraft, int paramInt1, int paramInt2) {
        Voice.str[0] = paramInt1;
        Voice.str[1] = 0;
        Voice.airSpeaksArray(paramAircraft, 0, Voice.str, paramInt2);
    }

    public static void airSpeaksNew(Aircraft paramAircraft, int paramInt1, int paramInt2) {
        Voice.str[0] = paramInt1;
        Voice.str[1] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 0, Voice.str, paramInt2);
    }

    public static void speakRandomArray(Aircraft paramAircraft, int[][] paramArrayOfInt, int paramInt) {
        for (int i = 0; (i < paramArrayOfInt.length) && (paramArrayOfInt[i] != null); i++) {
            if (paramArrayOfInt[i][0] != 0) {
                if (paramArrayOfInt[i][1] == 0) {
                    Voice.airSpeaks(paramAircraft, paramArrayOfInt[i][0], paramInt);
                } else {
                    int j;
                    if (paramArrayOfInt[i][2] == 0) {
                        j = 2;
                    } else {
                        j = 3;
                    }
                    Voice.airSpeaks(paramAircraft, paramArrayOfInt[i][Voice.rnd.nextInt(0, j - 1)], paramInt);
                }
            }
        }
        Voice.reset();
    }

    public static void speakRandom(Aircraft paramAircraft, int[] paramArrayOfInt, int paramInt) {
        int i = 0;
        for (i = 0; (i < paramArrayOfInt.length) && (paramArrayOfInt[i] != 0); i++) {
            ;
        }
        if (i < 1) {
            return;
        }
        int j = Voice.rnd.nextInt(0, i - 1);
        Voice.airSpeaks(paramAircraft, paramArrayOfInt[j], paramInt);
    }

    public static void speakNewRandom(Aircraft paramAircraft, int[] paramArrayOfInt, int paramInt) {
        int i = 0;
        for (i = 0; (i < paramArrayOfInt.length) && (paramArrayOfInt[i] != 0); i++) {
            ;
        }
        if (i < 1) {
            return;
        }
        int j = Voice.rnd.nextInt(0, i - 1);
        Voice.airSpeaksNew(paramAircraft, paramArrayOfInt[j], paramInt);
    }

    public static void speakAltitude(Aircraft paramAircraft, int paramInt) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakAltitude.length) return;
        // ---
        if (i < Voice.cur().SpeakAltitude[j]) {
            return;
        }
        Voice.cur().SpeakAltitude[j] = (i + 20);
        Voice.str[0] = 118;
        if (paramInt > 10000) {
            paramInt = 10000;
        }
        if (paramInt < 1) {
            paramInt = 1;
        }
        Voice.str[1] = Voice.altitudes[(paramInt / 500)];
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 3);
    }

    public static void speakHeading(Aircraft paramAircraft, Vector3f paramVector3f) {
        float f = 57.324841F * (float) Math.atan2(paramVector3f.x, paramVector3f.y);
        int i = (int) f;
        i = (i + 180) % 360;
        Voice.speakHeading(paramAircraft, i);
    }

    public static void speakHeading(Aircraft paramAircraft) {
        Vector3d localVector3d = paramAircraft.FM.Vwld;
        float f = 57.324841F * (float) Math.atan2(localVector3d.x, localVector3d.y);
        int i = (int) f;
        i = (i + 180) % 360;
        Voice.speakHeading(paramAircraft, i);
    }

    public static void speakHeadingToHome(Aircraft paramAircraft, Point3d paramPoint3d) {
        float f = 57.324841F * (float) Math.atan2(paramPoint3d.x, paramPoint3d.y);
        int i = (int) f;
        i = (i + 180) % 360;
        while (i < 0) {
            i += 360;
        }
        while (i >= 360) {
            i -= 360;
        }
        i /= 30;
        int j = Voice.actorVoice(paramAircraft, 5);
        int k = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.new_speak(j, k, str1, 235, 2);
        Voice.str[0] = 165;
        Voice.str[1] = Voice.headings[i];
        Voice.str[2] = 0;
        Voice.airSpeaksArray(paramAircraft, 5, Voice.str, 2);
        Voice.speak(j, k, str1, 252, 2);
    }

    public static void speakHeadingToTarget(Aircraft paramAircraft, Point3d paramPoint3d) {
        float f = 57.324841F * (float) Math.atan2(paramPoint3d.x, paramPoint3d.y);
        int i = (int) f;
        i = (i + 180) % 360;
        while (i < 0) {
            i += 360;
        }
        while (i >= 360) {
            i -= 360;
        }
        i /= 30;
        int j = Voice.actorVoice(paramAircraft, 5);
        int k = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.str[0] = 165;
        Voice.str[1] = Voice.headings[i];
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 5, Voice.str, 2);
        Voice.speak(j, k, str1, 252, 2);
    }

    public static void speakHeading(Aircraft paramAircraft, int paramInt) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakHeading.length) return;
        // ---
        if (i < Voice.cur().SpeakHeading[j]) {
            return;
        }
        Voice.cur().SpeakHeading[j] = (i + 20);
        while (paramInt < 0) {
            paramInt += 360;
        }
        while (paramInt >= 360) {
            paramInt -= 360;
        }
        paramInt /= 30;
        Voice.str[0] = 165;
        Voice.str[1] = Voice.headings[paramInt];
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 3);
    }

    public static void speak5minutes(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        if (i < Voice.cur().Speak5minutes[j]) {
            return;
        }
        Voice.cur().Speak5minutes[j] = (i + 40);
        Voice.str[0] = 81;
        if (Voice.rnd.nextFloat() < 0.5F) {
            Voice.str[1] = 49;
        } else {
            Voice.str[1] = 74;
        }
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 1);
    }

    public static void speak1minute(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        if (i < Voice.cur().Speak1minute[j]) {
            return;
        }
        Voice.cur().Speak1minute[j] = (i + 40);
        Voice.str[0] = 81;
        if (Voice.rnd.nextFloat() < 0.5F) {
            Voice.str[1] = 22;
        } else {
            Voice.str[1] = 73;
        }
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 1);
    }

    public static void speakBeginGattack(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft) || (paramAircraft.aircNumber() < 2)) {
            return;
        }
        int i = Voice.actorVoice(paramAircraft, 3);
        if (i == 0) {
            return;
        }

        int j = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.new_speak(i, j, str1, 81, 1);
        Voice.speak(i, j, str1, 169, 1);
        if ((paramAircraft.FM instanceof Pilot)) {
            Pilot localPilot = (Pilot) paramAircraft.FM;
            if (Actor.isValid(localPilot.target_ground)) {
                Actor target = localPilot.target_ground;
                if ((target instanceof CarGeneric)) {
                    Voice.str[0] = 162;
                } else if ((target instanceof TankGeneric)) {
                    Voice.str[0] = 152;
                } else if ((target instanceof AAA)) {
                    Voice.str[0] = 111;
                } else if ((target instanceof Wagon)) {
                    Voice.str[0] = 161;
                } else if ((target instanceof Train)) {
                    Voice.str[0] = 161;
                } else if ((target instanceof TgtShip)) {
                    Voice.str[0] = 99;
                }
                Voice.str[1] = 0;
                Voice.speak(i, j, str1, Voice.str[0], 1);
                Voice.speak(i, j, str1, 75, 1);
            }
            Squadron squadron = paramAircraft.getSquadron();
            Wing wing1 = paramAircraft.getWing();
            Wing wing2 = null;

            int k = 0;
            for (k = 0; k < squadron.wing.length; k++) {
                if (squadron.wing[k] != null) {
                    wing2 = squadron.wing[k];
                    break;
                }
            }
            if (wing1.airc.length > 0) {
                for (k = 0; k < wing1.airc.length; k++) {
                    if (((wing2 != wing1) || (k != 0)) && (wing1.airc[k] != null)) {
                        Voice.speakAttackGround(wing1.airc[k]);
                    }
                }
            }
        }
    }

    public static void speakLeaderEndGattack() {
        if (!Actor.isValid(Voice.internalAir)) {
            return;
        }
        Voice.str[0] = 81;
        Voice.str[1] = 153;
        Voice.str[2] = 136;
        Voice.str[3] = 0;
        Voice.airSpeaksNewArray(Voice.internalAir, 3, Voice.str, 1);
    }

    public static void speakLeaderRepeatGattack() {
        if (!Actor.isValid(Voice.internalAir)) {
            return;
        }
        Voice.str[0] = 81;
        Voice.str[1] = 138;
        Voice.str[2] = 136;
        Voice.str[3] = 0;
        Voice.airSpeaksNewArray(Voice.internalAir, 3, Voice.str, 1);
    }

    public static void speakEndGattack(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft) || (paramAircraft.aircIndex() > 0) || (paramAircraft instanceof TypeBomber) || (paramAircraft.aircNumber() < 2)) {
            return;
        }

        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakEndGattack.length) return;
        // ---
        if (i < Voice.cur().SpeakEndGattack[j]) {
            return;
        }
        Voice.cur().SpeakEndGattack[j] = (i + 60);

        if ((paramAircraft.FM instanceof Pilot)) {
            Pilot localPilot = (Pilot) paramAircraft.FM;
            Voice.internalAir = paramAircraft;
            if (Actor.isValid(localPilot.target_ground)) {
                new MsgAction(10.0D) {
                    public void doAction() {
                        Voice.speakLeaderRepeatGattack();
                    }
                };
            } else {
                new MsgAction(10.0D) {
                    public void doAction() {
                        Voice.speakLeaderEndGattack();
                    }
                };
            }
        }
    }

    public static void speakAttackByRockets(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakAttackByRockets.length) return;
        if (k < 0 || k >= Voice.cur().SpeakAttackByRockets[j].length) return;
        // ---
        if (i < Voice.cur().SpeakAttackByRockets[j][k]) {
            return;
        }
        Voice.cur().SpeakAttackByRockets[j][k] = (i + 60);

        Voice.setSyncMode(2);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 108;
        Voice.str[1] = 79;
        Voice.str[2] = 141;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakAttackByBombs(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakAttackByBombs.length) return;
        if (k < 0 || k >= Voice.cur().SpeakAttackByBombs[j].length) return;
        // ---
        if (i < Voice.cur().SpeakAttackByBombs[j][k]) {
            return;
        }
        Voice.cur().SpeakAttackByBombs[j][k] = (i + 60);

        Voice.setSyncMode(2);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 85;
        Voice.str[1] = 0;
        Voice.airSpeaksArray(paramAircraft, 0, Voice.str, 1);
    }

    public static void speakNearBombers(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakNearBombers.length) return;
        // ---
        if (i < Voice.cur().SpeakNearBombers[j]) {
            return;
        }
        Voice.cur().SpeakNearBombers[j] = (i + 300);

//    int k = paramAircraft.getArmy();
//    String str1 = paramAircraft.getRegiment().speech();
        Voice.str[0] = 219;
        Voice.str[1] = 220;
        Voice.str[2] = 221;
        Voice.str[3] = 136;
        Voice.str[4] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 6, Voice.str, 3);
    }

    public static void speakCheckFire(Aircraft paramAircraft1, Aircraft paramAircraft2) {
        if (!Actor.isAlive(paramAircraft1)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft1.getArmy() - 1) & 0x1;
        int k = paramAircraft1.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakCheckFire.length) return;
        if (k < 0 || k >= Voice.cur().SpeakCheckFire[j].length) return;
        // ---
        if (i < Voice.cur().SpeakCheckFire[j][k]) {
            return;
        }
        Voice.cur().SpeakCheckFire[j][k] = (i + 15);

        int m = (paramAircraft2.getWing().indexInSquadron() * 4) + paramAircraft2.aircIndex();
        if (m > 15) {
            return;
        }
        Voice.str[0] = Voice.aNumber[m];
        Voice.str[1] = 0;
        Voice.setSyncMode(2);
        Voice.airSpeaksNewArray(paramAircraft1, 0, Voice.str, 1);

        Voice.str[0] = 87;
        Voice.str[1] = 166;
        Voice.str[2] = 0;
        Voice.setSyncMode(2);
        Voice.speakRandom(paramAircraft1, Voice.str, 1);
    }

    public static void speakBombersUnderAttack(Aircraft paramAircraft, boolean paramBoolean) {
        int i = (int) (Time.current() / 60000L);
        if (!Actor.isValid(paramAircraft)) {
            return;
        }
        int j = (paramAircraft.FM.AP.way.curr().Action == 3) && (paramAircraft.FM.AP.getWayPointDistance() < 20000.0F) ? 1 : 0;

        int k = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int m = (k - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (m < 0 || m >= Voice.cur().SpeakBombersUnderAttack.length) return;
        // ---
        if (!paramBoolean) {
            if (i < Voice.cur().SpeakBombersUnderAttack[m]) {
                return;
            }
            Voice.cur().SpeakBombersUnderAttack[m] = (i + 25);
            if (j != 0) {
                Voice.new_speak(2, k, str1, 225, 2);
            } else {
                Voice.new_speak(2, k, str1, 226, 2);
            }
        } else {
            if (Voice.actorVoice(paramAircraft, 1) == 0) {
                return;
            }
            if (i < Voice.cur().SpeakBombersUnderAttack1[m]) {
                if (i < Voice.cur().SpeakBombersUnderAttack2[m]) {
                    Voice.cur().SpeakBombersUnderAttack2[m] = (i + 5);
                    Voice.str[0] = 119;
                    Voice.str[1] = 215;
                    Voice.str[2] = 213;
                    Voice.str[3] = 216;
                    Voice.str[4] = 0;
                    Voice.speakNewRandom(2, k, str1, Voice.str, 2);

                    Voice.str[0] = 226;
                    Voice.str[1] = 0;
                    Voice.speak(2, k, str1, Voice.str, 2);
                }
                return;
            }
            Voice.cur().SpeakBombersUnderAttack1[m] = (i + 25);
            if (j != 0) {
                Voice.new_speak(2, k, str1, 226, 2);
            } else {
                Voice.new_speak(2, k, str1, 228, 2);
            }
        }
    }

    public static void speakDanger(Aircraft paramAircraft, int paramInt) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakDanger.length) return;
        if (k < 0 || k >= Voice.cur().SpeakDanger[j].length) return;
        // ---
        if (i < Voice.cur().SpeakDanger[j][k]) {
            return;
        }
        Voice.cur().SpeakDanger[j][k] = (i + 27);

        Voice.setSyncMode(2);
        if (paramInt == 4) {
            int m = Voice.actorVoice(paramAircraft, 4);
            if (m == 0) {
                return;
            }
            int n = paramAircraft.getArmy();
            String str1 = paramAircraft.getRegiment().speech();
            Voice.str[0] = 260;
            Voice.str[1] = 255;
            Voice.str[2] = 254;
            Voice.speakNewRandom(m, n, str1, Voice.str, 1);
        } else {
            Voice.speakClearTail(paramAircraft);
        }
    }

    public static void speakClearTail(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = paramAircraft.aircIndex();
        int k = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (k < 0 || k >= Voice.cur().SpeakClearTail.length) return;
        if (j < 0 || j>= Voice.cur().SpeakClearTail[k].length) return;
        // ---
        if (i < Voice.cur().SpeakClearTail[k][j]) {
            return;
        }
        Voice.cur().SpeakClearTail[k][j] = (i + 37);

        Voice.setSyncMode(2);
        Voice.speakThisIs(paramAircraft);
        float f = Voice.rnd.nextFloat();
        if (f < 0.33F) {
            Voice.str[0] = 90;
        } else if (f < 0.66F) {
            Voice.str[0] = 218;
        } else {
            Voice.str[0] = 146;
        }
        f = Voice.rnd.nextFloat();
        if (f < 0.5F) {
            Voice.str[1] = 115;
        } else {
            Voice.str[1] = 89;
        }
        Voice.str[2] = 0;
        Voice.airSpeaksArray(paramAircraft, 0, Voice.str, 1);
    }

    public static void speakBombing(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakBombing.length) return;
        // ---
        if (i < Voice.cur().SpeakBombing[j]) {
            return;
        }
        Voice.cur().SpeakBombing[j] = (i + 1);

        Voice.reset();
        Voice.str2[0][0] = 81;
        Voice.str2[1][0] = 232;
        Voice.str2[1][1] = 231;
        Voice.str2[1][2] = 85;
        Voice.speakRandomArray(paramAircraft, Voice.str2, 1);
    }

    public static void speakEndBombing(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakEndBombing.length) return;
        // ---
        if (i < Voice.cur().SpeakEndBombing[j]) {
            return;
        }
        Voice.cur().SpeakEndBombing[j] = (i + 300);

        int k = paramAircraft.getRegiment().diedBombers;
        Voice.str[0] = 107;
        if (k > 1) {
            Voice.str[1] = 222;
        } else if (k == 1) {
            Voice.str[1] = 223;
        } else {
            Voice.str[1] = 224;
        }
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 6, Voice.str, 2);
    }

    public static void speakDeviateSmall(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakDeviateSmall.length) return;
        // ---
        if (i < Voice.cur().SpeakDeviateSmall[j]) {
            return;
        }
        Voice.cur().SpeakDeviateSmall[j] = (i + 4);

        Voice.str[0] = 170;
        Voice.str[1] = 150;
        Voice.str[2] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 2);
        Wing localWing = paramAircraft.getWing();
        if (localWing.airc.length > 0) {
            Voice.speakHeading(localWing.airc[0]);
            Voice.speakAltitude(paramAircraft, (int) localWing.airc[0].FM.Loc.z);
            Voice.str[0] = 136;
            Voice.str[1] = 0;
            Voice.airSpeaksArray(paramAircraft, 3, Voice.str, 2);
        }
    }

    public static void speakDeviateBig(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakDeviateBig.length) return;
        // ---
        if (i < Voice.cur().SpeakDeviateBig[j]) {
            return;
        }
        Voice.cur().SpeakDeviateBig[j] = (i + 10);

        if (Voice.rnd.nextFloat() < 0.5F) {
            if (Voice.rnd.nextFloat() < 0.5F) {
                Voice.str[0] = 90;
            } else {
                Voice.str[0] = 214;
            }
        } else if (Voice.rnd.nextFloat() < 0.5F) {
            Voice.str[0] = 170;
        } else {
            Voice.str[0] = 217;
        }
        Voice.str[1] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 2);

        Voice.str[0] = 171;
        Voice.str[1] = 149;
        Voice.str[2] = 0;
        Voice.airSpeaksArray(paramAircraft, 3, Voice.str, 2);
        Wing localWing = paramAircraft.getWing();
        if (localWing.airc.length > 0) {
            Voice.speakHeading(localWing.airc[0]);
            Voice.speakAltitude(paramAircraft, (int) localWing.airc[0].FM.Loc.z);
        }
    }

    public static void speakEnemyDetected(Aircraft paramAircraft1, Aircraft paramAircraft2) {
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft1.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakEnemyDetected.length) return;
        // ---
        if (i < Voice.cur().SpeakEnemyDetected[j]) {
            return;
        }
        Voice.cur().SpeakEnemyDetected[j] = (i + 40);
        if (!(paramAircraft1.FM instanceof Pilot) || (paramAircraft1.aircNumber() < 2)) {
            return;
        }

        Aircraft localAircraft = paramAircraft2;
        if (localAircraft == null) {
            return;
        }
        Voice.str[0] = 81;
        if ((localAircraft instanceof TypeFighter)) {
            Voice.str[1] = 107;
        } else if ((localAircraft instanceof TypeBomber)) {
            Voice.str[1] = 84;
        } else if ((localAircraft instanceof TypeDiveBomber)) {
            Voice.str[1] = 84;
        } else {
            Voice.str[1] = 83;
        }
        Voice.str[2] = Voice.speakTarget_O_Clock(paramAircraft1, localAircraft);
        Voice.str[3] = 0;
        int k = Voice.actorVoice(paramAircraft1, 3);
        if (k == 0) {
            return;
        }
        Voice.airSpeaksNewArray(paramAircraft1, 3, Voice.str, 1);
    }

    private static int speakTarget_O_Clock(Aircraft paramAircraft, Actor paramActor) {
        int i = paramAircraft.target_O_Clock(paramActor);
        if ((i < 1) || (i > 36)) {
            return 0;
        }
        return Voice.clkstr[i];
    }

    public static void speakCheckYour6(Aircraft paramAircraft1, Aircraft paramAircraft2) {
        if ((!Actor.isAlive(paramAircraft1)) || (paramAircraft1.getWing() == null)) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft1.getArmy() - 1) & 0x1;
        int k = paramAircraft1.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakHint.length) return;
        if (k < 0 || k >= Voice.cur().SpeakHint[j].length) return;
        // ---
        if (i < Voice.cur().SpeakHint[j][k]) {
            return;
        }
        Voice.cur().SpeakHint[j][k] = (i + 2);
        if (paramAircraft1.aircNumber() < 2) {
            return;
        }
        int m = (paramAircraft1.getWing().indexInSquadron() * 4) + paramAircraft1.aircIndex();
        if (m > 15) {
            return;
        }
        Voice.str[0] = Voice.aNumber[m];
        Voice.str[1] = 88;
        Voice.str[2] = Voice.your_o_clock[paramAircraft1.target_O_Clock(paramAircraft2)];
        Voice.airSpeaksNewArray(paramAircraft1, 1, Voice.str, 1);
    }

    public static void speakToReturn(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakToReturn.length) return;
        if (k < 0 || k >= Voice.cur().SpeakToReturn[j].length) return;
        // ---
        try {
            if (i < Voice.cur().SpeakToReturn[j][k]) {
                return;
            }
            Voice.cur().SpeakToReturn[j][k] = (i + 5);
        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
            return;
        }
        if (paramAircraft.aircNumber() < 2) {
            return;
        }
        int m = (paramAircraft.getWing().indexInSquadron() * 4) + paramAircraft.aircIndex();
        if (m > 15) {
            return;
        }
        Voice.str[0] = Voice.aNumber[m];
        Voice.str[1] = 140;
        Voice.str[2] = 136;
        Voice.str[3] = 0;
        Voice.airSpeaksNewArray(paramAircraft, 3, Voice.str, 1);
    }

    public static void speakBailOut(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakBailOut.length) return;
        if (k < 0 || k >= Voice.cur().SpeakBailOut[j].length) return;
        // ---
        if (i < Voice.cur().SpeakBailOut[j][k]) {
            return;
        }
        Voice.cur().SpeakBailOut[j][k] = (i + 1);
        if (paramAircraft.aircNumber() < 2) {
            return;
        }
        int m = (paramAircraft.getWing().indexInSquadron() * 4) + paramAircraft.aircIndex();
        if (m > 15) {
            return;
        }
//    int n = actorVoice(paramAircraft, 1);
        Aircraft localAircraft = War.getNearestFriend(paramAircraft);

        Voice.setSyncMode(2);
        if ((localAircraft != null) && ((Voice.rnd.nextFloat() > 0.5F) || (paramAircraft == World.getPlayerAircraft()))) {
            Voice.airSpeaksNew(localAircraft, Voice.aNumber[m], 1);
            Voice.str[0] = 82;
            Voice.str[1] = 116;
            Voice.str[2] = 120;
            Voice.str[3] = 0;
            Voice.setSyncMode(2);
            Voice.speakRandom(localAircraft, Voice.str, 1);
        } else {
            if (paramAircraft == World.getPlayerAircraft()) {
                return;
            }
            Voice.speakThisIs(paramAircraft);
            Voice.str[0] = 121;
            Voice.str[1] = 123;
            Voice.str[2] = 125;
            Voice.str[3] = 0;
            Voice.setSyncMode(2);
            Voice.speakRandom(paramAircraft, Voice.str, 1);
        }
    }

    public static void speakMayday(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakMayday.length) return;
        if (k < 0 || k >= Voice.cur().SpeakMayday[j].length) return;
        // ---
        try {
            if (i < Voice.cur().SpeakMayday[j][k]) {
                return;
            }
            Voice.cur().SpeakMayday[j][k] = (i + 1);
        } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
        }

        Voice.setSyncMode(2);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 122;
        Voice.str[1] = 91;
        Voice.str[2] = 126;
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakMissionAccomplished(Aircraft paramAircraft) {
        int i = Voice.actorVoice(paramAircraft, 3);
        if (i == 0) {
            return;
        }
        int j = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.new_speak(i, j, str1, 81, 1);
        if (paramAircraft.getRegiment().diedAircrafts == 0) {
            Voice.speak(i, j, str1, 128, 1);
        } else {
            Voice.speak(i, j, str1, 127, 1);
        }
        Voice.str[0] = 139;
        Voice.str[1] = 105;
        Voice.str[2] = 168;
        Voice.str[3] = 0;
        Voice.speakRandom(i, j, str1, Voice.str, 1);
        Voice.speakHeading(paramAircraft);
        Voice.speakAltitude(paramAircraft, (int) paramAircraft.FM.Loc.z);

        if (!(paramAircraft instanceof TypeFighter)) {
            Aircraft localAircraft = War.getNearestFriendlyFighter(paramAircraft, 50000.0F);
            if (localAircraft != null) {
                Voice.speakEndBombing(paramAircraft);
            }
        }
    }

    public static void speakThisIs(Aircraft paramAircraft) {
        if ((paramAircraft == null) || (paramAircraft.getWing() == null)) {
            return;
        }
        int i = paramAircraft.getWing().indexInSquadron();
        int j = paramAircraft.aircIndex();
//    int k = paramAircraft.aircNumber();
        int m = (i * 4) + j;
        if (m > 15) {
            return;
        }
        Voice.str[0] = Voice.thisIsNumber[m];
        Voice.str[1] = 0;
        int n = Voice.syncMode;
        Voice.airSpeaksNewArray(paramAircraft, 0, Voice.str, 1);
        Voice.setSyncMode(n);
    }

    public static void speakIAm(Aircraft paramAircraft) {
        if ((paramAircraft == null) || (paramAircraft.getWing() == null)) {
            return;
        }
        int i = paramAircraft.getWing().indexInSquadron();
        int j = paramAircraft.aircIndex();
        int k = paramAircraft.aircNumber();
        int m = (i * 4) + j;
        if (m > 15) {
            return;
        }
        Voice.str[0] = Voice.thisIsNumber[m];
        int n = (paramAircraft.getArmy() - 1) & 0x1;
        if (n == 0) {
            if (j == 0) {
                if (k == 2) {
                    Voice.str[0] = Voice.thisIsPara[i];
                } else if (k > 1) {
                    Voice.str[0] = Voice.thisIsWing[i];
                }
            }
        } else if (j == 0) {
            if (k == 2) {
                Voice.str[0] = Voice.thisIsRotte[i];
            } else if (k > 1) {
                Voice.str[0] = Voice.thisIsSwarm[i];
            }
        }

        Voice.str[1] = 0;
        int i1 = Voice.syncMode;
        Voice.airSpeaksNewArray(paramAircraft, 0, Voice.str, 1);
        Voice.setSyncMode(i1);
    }

    public static void speakNumber_same_str(int paramInt, Aircraft paramAircraft) {
        if ((paramAircraft == null) || (paramAircraft.getWing() == null)) {
            return;
        }
        int i = paramAircraft.getWing().indexInSquadron();
        int j = paramAircraft.aircIndex();
        int k = (i * 4) + j;
        if (k > 15) {
            return;
        }
        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int n = Voice.syncMode;
        Voice.speak(paramInt, m, str1, Voice.aNumber[k], 2);
        Voice.setSyncMode(n);
    }

    public static void speakNumber(int paramInt, Aircraft paramAircraft) {
        if ((paramAircraft == null) || (paramAircraft.getWing() == null)) {
            return;
        }
        int i = paramAircraft.getWing().indexInSquadron();
        int j = paramAircraft.aircIndex();
        int k = (i * 4) + j;
        if (k > 15) {
            return;
        }
        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int n = Voice.syncMode;
        Voice.new_speak(paramInt, m, str1, Voice.aNumber[k], 2);
        Voice.setSyncMode(n);
    }

    public static void speakCoverMe(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakCoverMe.length) return;
        if (k < 0 || k >= Voice.cur().SpeakCoverMe[j].length) return;
        // ---
        if (i < Voice.cur().SpeakCoverMe[j][k]) {
            return;
        }
        Voice.cur().SpeakCoverMe[j][k] = (i + 15);

        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int n = Voice.actorVoice(paramAircraft, 0);

        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 310;
        Voice.str[1] = 309;
        Voice.str[2] = 268;
        Voice.str[3] = 0;
        Voice.speakRandom(n, m, str1, Voice.str, 1);
    }

    public static void speakYouAreClear(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakCoverMe.length) return;
        if (k < 0 || k >= Voice.cur().SpeakCoverMe[j].length) return;
        // ---
        if (i < Voice.cur().SpeakCoverMe[j][k]) {
            return;
        }
        Voice.cur().SpeakCoverMe[j][k] = (i + 15);

        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 341;
        Voice.str[1] = 0;
        Voice.airSpeaksArray(paramAircraft, 0, Voice.str, 1);
    }

    public static void speakTargetAll(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakTargetAll.length) return;
        if (k < 0 || k >= Voice.cur().SpeakTargetAll[j].length) return;
        // ---
        if (i < Voice.cur().SpeakTargetAll[j][k]) {
            return;
        }
        Voice.cur().SpeakTargetAll[j][k] = (i + 1);

        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int n = Voice.actorVoice(paramAircraft, 0);

        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 324;
        Voice.str[1] = 320;
        Voice.str[2] = 277;
        Voice.str[3] = 0;
        Voice.speakRandom(n, m, str1, Voice.str, 1);
    }

    public static void speakAttackFighters(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakTargetAll.length) return;
        if (k < 0 || k >= Voice.cur().SpeakTargetAll[j].length) return;
        // ---
        if (i < Voice.cur().SpeakTargetAll[j][k]) {
            return;
        }
        Voice.cur().SpeakTargetAll[j][k] = (i + 30);

        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int n = Voice.actorVoice(paramAircraft, 0);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 323;
        Voice.str[1] = 306;
        Voice.str[2] = 267;
        Voice.str[3] = 0;
        Voice.speakRandom(n, m, str1, Voice.str, 1);
    }

    public static void speakAttackBombers(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakAttackBombers.length) return;
        if (k < 0 || k >= Voice.cur().SpeakAttackBombers[j].length) return;
        // ---
        if (i < Voice.cur().SpeakAttackBombers[j][k]) {
            return;
        }
        Voice.cur().SpeakAttackBombers[j][k] = (i + 30);

        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int n = Voice.actorVoice(paramAircraft, 0);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 307;
        Voice.str[1] = 264;
        Voice.str[2] = 0;
        Voice.speakRandom(n, m, str1, Voice.str, 1);
    }

    public static void speakAttackMyTarget(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }

        Voice.speakIAm(paramAircraft);
        Voice.str2[0][0] = 278;
        Voice.str2[0][1] = 264;
        Voice.speakRandomArray(paramAircraft, Voice.str2, 1);
    }

    public static void speakAttackGround(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }

        Voice.speakIAm(paramAircraft);
        Voice.str2[0][0] = 262;
        Voice.str2[0][1] = 264;
        Voice.speakRandomArray(paramAircraft, Voice.str2, 1);
    }

    public static void speakDropTanks(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str2[0][0] = 322;
        Voice.str2[0][1] = 275;
        Voice.speakRandomArray(paramAircraft, Voice.str2, 1);
    }

    public static void speakHelpNeeded(Aircraft paramAircraft, int paramInt) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakHelpNeeded.length) return;
        if (k < 0 || k >= Voice.cur().SpeakHelpNeeded[j].length) return;
        // ---
        if (i < Voice.cur().SpeakHelpNeeded[j][k]) {
            return;
        }
        Voice.cur().SpeakHelpNeeded[j][k] = (i + 30);
    }

    public static void speakCoverProvided(Aircraft paramAircraft1, Aircraft paramAircraft2) {
        if ((!Actor.isAlive(paramAircraft1)) || (!Actor.isAlive(paramAircraft2))) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = paramAircraft1.aircIndex();
        int k = (paramAircraft1.getArmy() - 1) & 0x1;
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (k < 0 || k >= Voice.cur().SpeakCoverProvided.length) return;
        if (j < 0 || j >= Voice.cur().SpeakCoverProvided[k].length) return;
        // ---
        if (i < Voice.cur().SpeakCoverProvided[k][j]) {
            return;
        }
        Voice.cur().SpeakCoverProvided[k][j] = (i + 30);
        int m = paramAircraft1.getArmy();
        String str1 = paramAircraft1.getRegiment().speech();
        int n = Voice.actorVoice(paramAircraft1, 0);

        Voice.speakThisIs(paramAircraft1);
        if (World.Rnd().nextBoolean()) {
            Voice.speakNumber_same_str(n, paramAircraft2);
        }
        Voice.str[0] = 310;
        Voice.str[1] = 309;
        Voice.str[2] = 268;
        Voice.str[3] = 0;
        Voice.speakRandom(n, m, str1, Voice.str, 1);
    }

    public static void speakHelpNeededFromBase(Aircraft paramAircraft, boolean paramBoolean) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
//    int i = paramAircraft.getArmy() - 1 & 0x1;
        int j = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int k = Voice.actorVoice(paramAircraft, 5);

        Voice.new_speak(k, j, str1, 235, 1);
        if (paramBoolean) {
            Voice.str[0] = 237;
            Voice.str[1] = 239;
            Voice.str[2] = 0;
            Voice.speakRandom(k, j, str1, Voice.str, 1);
        } else {
            Voice.str[0] = 234;
            Voice.str[1] = 233;
            Voice.str[2] = 0;
            Voice.speakRandom(k, j, str1, Voice.str, 1);
        }
    }

    public static void speakHelpFromAir(Aircraft paramAircraft, int paramInt) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakCoverProvided.length) return;
        if (k < 0 || k >= Voice.cur().SpeakCoverProvided[j].length) return;
        // ---
        if (i < Voice.cur().SpeakCoverProvided[j][k]) {
            return;
        }
        Voice.cur().SpeakCoverProvided[j][k] = (i + 45);

        Voice.speakThisIs(paramAircraft);
        switch (paramInt) {
            case 1:
                Voice.str[0] = 291;
                Voice.str[1] = 294;
                Voice.str[2] = 291;
                break;
            case 2:
                Voice.str[0] = 341;
                Voice.str[1] = 342;
                Voice.str[2] = 342;
                break;
            default:
                Voice.str[0] = 295;
                Voice.str[1] = 339;
                Voice.str[2] = 340;
        }
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakRearGunKill() {
        int i = (int) (Time.current() / 1000L);
        if (i < Voice.cur().SpeakRearGunKill) {
            return;
        }
        Voice.cur().SpeakRearGunKill = (i + 20);
        int j = World.getPlayerArmy();
        String str1 = World.getPlayerAircraft().getRegiment().speech();
        Voice.setSyncMode(2);
        Voice.new_speak(5, j, str1, 258, 1);
    }

    public static void speakRearGunShake() {
        int i = (int) (Time.current() / 1000L);
        if (i < Voice.cur().SpeakRearGunShake) {
            return;
        }
        Voice.cur().SpeakRearGunShake = (i + 20);
        int j = World.getPlayerArmy();
        String str1 = World.getPlayerAircraft().getRegiment().speech();
        Voice.setSyncMode(2);
        if (Voice.rnd.nextFloat() < 0.5F) {
            Voice.new_speak(5, j, str1, 256, 1);
        } else {
            Voice.new_speak(5, j, str1, 259, 1);
        }
    }

    public static void speakNiceKill(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakNiceKill.length) return;
        if (k < 0 || k >= Voice.cur().SpeakNiceKill[j].length) return;
        // ---
        if (i < Voice.cur().SpeakNiceKill[j][k]) {
            return;
        }
        Voice.cur().SpeakNiceKill[j][k] = (i + 5);

//    if (paramAircraft == null) return;
        float f = Voice.rnd.nextFloat();
        int m = Voice.actorVoice(paramAircraft, 1);

        Voice.setSyncMode(2);
        if (paramAircraft == World.getPlayerAircraft()) {
            f = 0.0F;
        } else if (m == 0) {
            f = 1.0F;
        }
        if (f > 0.5F) {
            Voice.speakThisIs(paramAircraft);
            Voice.str[0] = 293;
            Voice.str[1] = 290;
            Voice.str[2] = 0;
            Voice.speakRandom(paramAircraft, Voice.str, 2);
        } else {
            int n = paramAircraft.getArmy();
            String str1 = paramAircraft.getRegiment().speech();
            Voice.speakNumber(m, paramAircraft);
            Voice.str[0] = 289;
            Voice.str[1] = 288;
            Voice.str[2] = 296;
            Voice.str[3] = 0;
            Voice.speakRandom(m, n, str1, Voice.str, 1);
        }
    }

    public static void speakRearGunTargetDestroyed() {
        Aircraft localAircraft = World.getPlayerAircraft();
        if (!Actor.isAlive(localAircraft)) {
            return;
        }
        int i = localAircraft.getArmy();
        String str1 = localAircraft.getRegiment().speech();
        Voice.setSyncMode(2);
        Voice.str[0] = 153;
        Voice.str[1] = 93;
        Voice.str[2] = 154;
        Voice.str[3] = 0;
        Voice.speakNewRandom(5, i, str1, Voice.str, 2);
        Voice.str[0] = 257;
        Voice.str[1] = 261;
        Voice.str[2] = 0;
        Voice.speakRandom(5, i, str1, Voice.str, 2);
    }

    public static void speakTargetDestroyed(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 1000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakTargetDestroyed.length) return;
        if (k < 0 || k >= Voice.cur().SpeakTargetDestroyed[j].length) return;
        // ---
        if (i < Voice.cur().SpeakTargetDestroyed[j][k]) {
            return;
        }
        Voice.cur().SpeakTargetDestroyed[j][k] = (i + 60);

        if ((paramAircraft == World.getPlayerAircraft()) && (paramAircraft.FM.turret.length > 0) && (paramAircraft.FM.AS.astatePilotStates[paramAircraft.FM.turret.length] < 90) && (paramAircraft.FM.turret[0].bIsAIControlled)) {
            new MsgAction(5.5D) {
                public void doAction() {
                    Voice.speakRearGunTargetDestroyed();
                }
            };
        }
        if (Actor.isAlive(paramAircraft)) {
            Voice.speakThisIs(paramAircraft);
            Voice.str[0] = 153;
            Voice.str[1] = 93;
            Voice.str[2] = 154;
            Voice.str[3] = 0;
            Voice.speakRandom(paramAircraft, Voice.str, 1);
        }
    }

    public static void speakEndOfAmmo(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft) || ((paramAircraft instanceof TypeBomber)) || ((paramAircraft instanceof TypeTransport))) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakEndOfAmmo.length) return;
        if (k < 0 || k >= Voice.cur().SpeakEndOfAmmo[j].length) return;
        // ---
        if (i < Voice.cur().SpeakEndOfAmmo[j][k]) {
            return;
        }
        Voice.cur().SpeakEndOfAmmo[j][k] = (i + 5);

        Voice.setSyncMode(2);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 292;
        Voice.str[1] = 124;
        Voice.str[2] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 2);
    }

    public static void speakBreak(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakBreak.length) return;
        if (k < 0 || k >= Voice.cur().SpeakBreak[j].length) return;
        // ---
        if (i < Voice.cur().SpeakBreak[j][k]) {
            return;
        }
        Voice.cur().SpeakBreak[j][k] = (i + 1);

        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 302;
        Voice.str[1] = 298;
        Voice.str[2] = 266;
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 2);
    }

    public static void speakRejoin(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 318;
        Voice.str[1] = 317;
        Voice.str[2] = 274;
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 2);
    }

    public static void speakTightFormation(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 300;
        Voice.str[1] = 301;
        Voice.str[2] = 279;
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 2);
    }

    public static void speakLoosenFormation(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.str[0] = 299;
        Voice.str[1] = 299;
        Voice.str[2] = 265;
        Voice.str[3] = 0;
        Voice.speakNewRandom(paramAircraft, Voice.str, 2);
    }

    public static void speakOk(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.airSpeaks(paramAircraft, 298, 1);
    }

    public static void speakUnable(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 339;
        Voice.str[1] = 340;
        Voice.str[2] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakNextWayPoint(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 314;
        Voice.str[1] = 271;
        Voice.str[2] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakPrevWayPoint(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 316;
        Voice.str[1] = 319;
        Voice.str[2] = 272;
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakReturnToBase(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 325;
        Voice.str[1] = 305;
        Voice.str[2] = 276;
        Voice.str[3] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakHangOn(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        Voice.speakIAm(paramAircraft);
        Voice.str[0] = 308;
        Voice.str[1] = 269;
        Voice.str[2] = 0;
        Voice.speakRandom(paramAircraft, Voice.str, 1);
    }

    public static void speakEchelonRight(Aircraft paramAircraft) {
        Voice.speakIAm(paramAircraft);
        Voice.airSpeaks(paramAircraft, 304, 2);
    }

    public static void speakEchelonLeft(Aircraft paramAircraft) {
        Voice.speakIAm(paramAircraft);
        Voice.airSpeaks(paramAircraft, 303, 2);
    }

    public static void speakLineAbreast(Aircraft paramAircraft) {
        Voice.speakIAm(paramAircraft);
        Voice.airSpeaks(paramAircraft, 312, 2);
    }

    public static void speakLineAstern(Aircraft paramAircraft) {
        Voice.speakIAm(paramAircraft);
        Voice.airSpeaks(paramAircraft, 313, 1);
    }

    public static void speakVic(Aircraft paramAircraft) {
        Voice.speakIAm(paramAircraft);
        Voice.airSpeaks(paramAircraft, 321, 1);
    }

    public static void speakPullUp(Aircraft paramAircraft) {
        int i = (int) (Time.current() / 1000L);
        if (i < Voice.cur().SpeakPullUp) {
            return;
        }
        Voice.cur().SpeakPullUp = (i + 30);
        int j = Voice.actorVoice(paramAircraft, 1);
        int k = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.setSyncMode(2);
        Voice.speakNumber(j, paramAircraft);
        Voice.str[0] = 137;
        Voice.str[1] = 172;
        Voice.str[2] = 167;
        Voice.str[3] = 0;
        Voice.speakRandom(j, k, str1, Voice.str, 3);
    }

    public static void speakLandingPermited(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = (int) (Time.current() / 60000L);
        int j = (paramAircraft.getArmy() - 1) & 0x1;
        int k = paramAircraft.aircIndex();
        // TODO: +++ Added by SAS~Storebror: Add Array Bounds Check!
        if (j < 0 || j >= Voice.cur().SpeakLandingPermited.length) return;
        if (k < 0 || k >= Voice.cur().SpeakLandingPermited[j].length) return;
        // ---
        if ((paramAircraft != World.getPlayerAircraft()) && (i < Voice.cur().SpeakLandingPermited[j][k])) {
            return;
        }
        Voice.cur().SpeakLandingPermited[j][k] = (i + 60);

        int m = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.speakNumber(4, paramAircraft);
        Voice.str[0] = 240;
        Voice.str[1] = 243;
        Voice.str[2] = 248;
        Voice.str[3] = 0;
        Voice.speakRandom(4, m, str1, Voice.str, 2);
    }

    public static void speakLandingDenied(Aircraft paramAircraft) {
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.speakNumber(4, paramAircraft);
        Voice.str[0] = 250;
        Voice.str[1] = 236;
        Voice.str[2] = 0;
        Voice.speakRandom(4, i, str1, Voice.str, 2);
    }

    public static void speakWaveOff(Aircraft paramAircraft) {
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.speakNumber(4, paramAircraft);
        Voice.str[0] = 236;
        Voice.str[1] = 238;
        Voice.str[2] = 0;
        Voice.speakRandom(4, i, str1, Voice.str, 2);
    }

    public static void speakLanding(Aircraft paramAircraft) {
        Voice.speakThisIs(paramAircraft);
        Voice.airSpeaks(paramAircraft, 134, 1);
    }

    public static void speakGoAround(Aircraft paramAircraft) {
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        int j = Voice.actorVoice(paramAircraft, 0);
        Voice.speakThisIs(paramAircraft);
        Voice.str[0] = 135;
        Voice.str[1] = 117;
        Voice.str[2] = 0;
        Voice.speakRandom(j, i, str1, Voice.str, 1);
    }

    public static void speakGoingIn(Aircraft paramAircraft) {
        Voice.airSpeaks(paramAircraft, 130, 1);
    }

    public static void testTargDestr(Actor paramActor1, Actor paramActor2) {
        if ((Actor.isValid(paramActor2)) && ((!paramActor2.isNet()) || (paramActor2.isNetMaster()))) {
            try {
                if ((paramActor1 instanceof Aircraft)) {
                    if (!(paramActor1 instanceof TypeFighter)) {
                        ((Wing) paramActor1.getOwner()).regiment().diedBombers += 1;
                        if ((paramActor2 instanceof TypeFighter)) {
                            Voice.speakBombersUnderAttack((Aircraft) paramActor1, true);
                        }
                    }
                    ((Wing) paramActor1.getOwner()).regiment().diedAircrafts += 1;

                    if (((paramActor2 instanceof Aircraft)) && (paramActor1.getArmy() != paramActor2.getArmy()) && (!((Aircraft) paramActor1).buried)) {
                        Voice.speakNiceKill((Aircraft) paramActor2);
                    }

                } else if ((paramActor2 instanceof Aircraft)) {
                    int i = paramActor1.getArmy();
                    if (i == 0) {
                        paramActor1.pos.getAbs(Voice.P3d);
                        i = Front.army(Voice.P3d.x, Voice.P3d.x);
                    }
                    if (i != paramActor2.getArmy()) {
                        Voice.speakTargetDestroyed((Aircraft) paramActor2);
                    }
                }
            } catch (Exception localException) {
                System.out.println(localException.getMessage());
                localException.printStackTrace();
            }
        }
    }

    public static void speakTakeoffPermited(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.speakNumber(4, paramAircraft);
        Voice.str[0] = 241;
        Voice.str[1] = 0;
        Voice.speakRandom(4, i, str1, Voice.str, 1);
    }

    public static void speakTakeoffDenied(Aircraft paramAircraft) {
        if (!Actor.isAlive(paramAircraft)) {
            return;
        }
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.speakNumber(4, paramAircraft);
        Voice.str[0] = 253;
        Voice.str[1] = 0;
        Voice.speakRandom(4, i, str1, Voice.str, 1);
    }

    public static void speakNegative(Aircraft paramAircraft) {
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.str[0] = 295;
        Voice.str[2] = 0;
        Voice.speakRandom(6, i, str1, Voice.str, 1);
    }

    public static void speakRooger(Aircraft paramAircraft) {
        int i = paramAircraft.getArmy();
        String str1 = paramAircraft.getRegiment().speech();
        Voice.str[0] = 298;
        Voice.str[2] = 0;
        Voice.speakRandom(6, i, str1, Voice.str, 1);
    }

    static {
        Voice.reset();

        Voice.syncMode = 0;

        Voice.P3d = new Point3d();
    }
}
