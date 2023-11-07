/*
 * 4.13.1 Class, backported to UP3 by SAS~Storebror
 * 4.11+ TrackIR implementation by SAS~Storebror
 */

package com.maddox.rts;

public class HotKey implements MsgKeyboardListener, MsgMouseListener, MsgJoyListener, MsgTrackIRListener, MsgTimeOutListener {

    protected HotKey() {
        this.povState = new int[32];
        this.msgTimeOut = new MsgTimeOut();
        this.msgTimeOut.setListener(this);
        this.msgTimeOut.setTickPos(MsgKeyboard.UNKNOWN);
        this.msgTimeOut.setNotCleanAfterSend();
        this.msgTimeOut.setFlags(Message.NEXT_TICK);
        this.msgTimeOut.post();
        this.msgRealTimeOut = new MsgTimeOut();
        this.msgRealTimeOut.setListener(this);
        this.msgRealTimeOut.setNotCleanAfterSend();
        this.msgRealTimeOut.setFlags(Message.REAL_TIME | Message.NEXT_TICK);
        this.msgRealTimeOut.post();
        MsgAddListener.post(0, RTSConf.cur.keyboard, this, null);
        MsgAddListener.post(Message.REAL_TIME, RTSConf.cur.keyboard, this, null);
        MsgAddListener.post(0, RTSConf.cur.mouse, this, null);
        MsgAddListener.post(Message.REAL_TIME, RTSConf.cur.mouse, this, null);
        MsgAddListener.post(0, RTSConf.cur.joy, this, null);
        MsgAddListener.post(Message.REAL_TIME, RTSConf.cur.joy, this, null);
        MsgAddListener.post(0, RTSConf.cur.trackIR, this, null);
        MsgAddListener.post(Message.REAL_TIME, RTSConf.cur.trackIR, this, null);
    }

    protected void resetGameClear() {
        for (int i = 0; i < this.povState.length; i++)
            this.povState[i] = 0;

    }

    protected void resetGameCreate() {
        if (!this.msgTimeOut.busy()) this.msgTimeOut.post();
    }

    public void msgUserKey(boolean flag, int i, boolean flag1) {
        HotKeyEnv.keyPress(flag, VK.ALL_KEYS + i & 0xffff, flag1);
    }

    public void msgJoyButton(int i, int j, boolean flag) {
        HotKeyEnv.keyPress(Message.current().isRealTime(), i, j, flag);
    }

    public void msgJoyMove(int i, int j, int k) {
        HotKeyEnv.joyMove(Message.current().isRealTime(), i, j, k);
    }

    public void msgJoyPov(int povIndex, int povState) {
        boolean flag = Message.current().isRealTime();
        int oldPovState = this.povState[povIndex - VK.JOYPOV0];
        if (oldPovState == povState) return;
        if (oldPovState != 0) {
            this.povState[povIndex - VK.JOYPOV0] = 0;
            HotKeyEnv.keyPress(flag, povIndex, oldPovState, false);
        }
        if (povState != VK.POV_1) {
            this.povState[povIndex - VK.JOYPOV0] = povState;
            HotKeyEnv.keyPress(flag, povIndex, povState, true);
        }
    }

    public void msgJoyPoll() {
    }

    public void msgMouseButton(int mouseButton, boolean flag) {
        HotKeyEnv.keyPress(Message.current().isRealTime(), VK.MOUSE0 + mouseButton, flag);
    }

    public void msgMouseMove(int dX, int dY, int dZ) {
        HotKeyEnv.mouseMove(Message.current().isRealTime(), dX, dY, dZ);
    }

    public void msgMouseAbsMove(int absX, int absY, int absZ) {
        HotKeyEnv.mouseAbsMove(Message.current().isRealTime(), absX, absY, absZ);
    }

    public void msgKeyboardKey(int key, boolean flag) {
        HotKeyEnv.keyPress(Message.current().isRealTime(), 0 + key, flag);
    }

    public void msgKeyboardChar(char c) {
    }

    // TODO: +++ 4.11+ TrackIR implementation by SAS~Storebror +++
    public void msgTrackIRAngles(float yaw, float pitch, float roll, float headX, float headY, float headZ) {
        HotKeyEnv.trackIRAngles(Message.current().isRealTime(), yaw, pitch, roll, headX, headY, headZ);
    }
    // TODO: --- 4.11+ TrackIR implementation by SAS~Storebror ---

    // Original 4.10/UP3 method for backward compatibility
    public void msgTrackIRAngles(float yaw, float pitch, float roll) {
        this.msgTrackIRAngles(yaw, pitch, roll, 0F, 0F, 0F);
    }

    public void msgTimeOut(Object obj) {
        if (Message.current().isRealTime()) {
            HotKeyEnv.tick(true);
            this.msgRealTimeOut.post();
        } else {
            HotKeyEnv.tick(false);
            this.msgTimeOut.post();
        }
    }

    private int        povState[];
    private MsgTimeOut msgTimeOut;
    private MsgTimeOut msgRealTimeOut;
}
