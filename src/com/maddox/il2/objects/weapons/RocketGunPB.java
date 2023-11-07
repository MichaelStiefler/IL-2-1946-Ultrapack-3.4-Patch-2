package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Interpolate;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class RocketGunPB extends Interpolate implements BulletEmitter {

    public RocketGunPB() {
        this.ready = true;
        this.bHide = false;
        this.bRocketPosRel = true;
        this.bulletClass = null;
        this.bCassette = false;
        this.timeLife = -1F;
        this.plusPitch = 0.0F;
        this.plusYaw = 0.0F;
        this.bulletMassa = 0.048F;
    }

    public void doDestroy() {
        this.ready = false;
        if (this.rocket != null) {
            this.rocket.destroy();
            this.rocket = null;
        }
    }

    private boolean nameEQ(HierMesh hiermesh, int i, int j) {
        if (hiermesh == null) {
            return false;
        }
        hiermesh.setCurChunk(i);
        String s = hiermesh.chunkName();
        hiermesh.setCurChunk(j);
        String s1 = hiermesh.chunkName();
        int l = Math.min(s.length(), s1.length());
        for (int k = 0; k < l; k++) {
            char c = s.charAt(k);
            if (c == '_') {
                return true;
            }
            if (c != s1.charAt(k)) {
                return false;
            }
        }

        return true;
    }

    public BulletEmitter detach(HierMesh hiermesh, int i) {
        if (!this.ready) {
            return GunEmpty.get();
        }
        if ((i == -1) || this.nameEQ(hiermesh, i, this.hook.chunkNum())) {
            this.bExecuted = true;
            this.ready = false;
            return GunEmpty.get();
        } else {
            return this;
        }
    }

    protected int bullets() {
        return this.actor != null ? this.bulletss - this.actor.hashCode() : 0;
    }

    protected void bullets(int i) {
        if (this.actor != null) {
            this.bulletss = i + this.actor.hashCode();
        } else {
            this.bulletss = 0;
        }
    }

    public boolean isEnablePause() {
        return false;
    }

    public boolean isPause() {
        return false;
    }

    public void setPause(boolean flag1) {
    }

    public void hide(boolean flag) {
        this.bHide = flag;
        if (this.bHide) {
            if (Actor.isValid(this.rocket)) {
                this.rocket.drawing(false);
            }
        } else if (Actor.isValid(this.rocket)) {
            this.rocket.drawing(true);
        }
    }

    public boolean isHide() {
        return this.bHide;
    }

    public void setRocketTimeLife(float f) {
        this.timeLife = f;
    }

    public float getRocketTimeLife() {
        return this.timeLife;
    }

    public boolean isCassette() {
        return this.bCassette;
    }

    public float bulletMassa() {
        return this.bulletMassa;
    }

    public int countBullets() {
        return this.bullets();
    }

    public boolean haveBullets() {
        return this.bullets() != 0;
    }

    public void loadBullets() {
        this.loadBullets(this.bulletsFull);
    }

    public void _loadBullets(int i) {
        this.loadBullets(i);
    }

    public void loadBullets(int i) {
        this.bullets(i);
        if (this.bullets() != 0) {
            if (!Actor.isValid(this.rocket)) {
                this.newRocket();
            }
        } else if (Actor.isValid(this.rocket)) {
            this.rocket.destroy();
            this.rocket = null;
        }
    }

    public Class bulletClass() {
        return this.bulletClass;
    }

    public void setBulletClass(Class class1) {
        this.bulletClass = class1;
        this.bulletMassa = Property.floatValue(this.bulletClass, "massa", this.bulletMassa);
    }

    public boolean isShots() {
        return this.bExecuted;
    }

    public void shots(int i, float f) {
        if (this.isCassette() && (i != 0) && (this.bullets() != -1)) {
            i = 6;
        } else {
            this.shots(i);
        }
    }

    public void shots(int i) {
        if (this.isHide()) {
            return;
        }
        if (this.isCassette() && (i != 0) && (this.bullets() != -1)) {
            i = this.bullets();
        }
        i /= 2;
        if (!this.bExecuted && (i != 0)) {
            if (this.bullets() == 0) {
                return;
            }
            this.curShotStep = 0;
            this.curShots = i;
            this.bExecuted = true;
        } else if (this.bExecuted && (i != 0)) {
            this.curShots = i;
        } else if (this.bExecuted && (i == 0)) {
            this.bExecuted = false;
        }
    }

    protected void interpolateStep() {
        this.bTickShot = false;
        if (this.curShotStep == 0) {
            if ((this.bullets() == 0) || (this.curShots == 0) || !Actor.isValid(this.actor)) {
                this.shots(0);
                return;
            }
            this.bTickShot = true;
            if (this.rocket != null) {
                this.rocket.pos.setUpdateEnable(true);
                if ((this.plusPitch != 0.0F) || (this.plusYaw != 0.0F)) {
                    this.rocket.pos.getAbs(RocketGunPB._tmpOr0);
                    RocketGunPB._tmpOr1.setYPR(this.plusYaw, this.plusPitch, 0.0F);
                    RocketGunPB._tmpOr1.add(RocketGunPB._tmpOr0);
                    this.rocket.pos.setAbs(RocketGunPB._tmpOr1);
                }
                this.rocket.pos.resetAsBase();
                this.rocket.start(this.timeLife);
                if (Actor.isValid(this.rocket)) {
                    String s = Property.stringValue(this.getClass(), "sound", null);
                    if (s != null) {
                        this.rocket.newSound(s, true);
                    }
                }
                this.rocket = null;
            }
            if (this.curShots > 0) {
                this.curShots--;
            }
            if (this.bullets() > 0) {
                this.bullets(this.bullets() - 1);
            }
            if (this.bullets() != 0) {
                this.newRocket();
            }
            this.curShotStep = this.shotStep;
        }
        this.curShotStep--;
    }

    public boolean tick() {
        this.interpolateStep();
        return this.ready;
    }

    private void newRocket() {
        try {
            this.rocket = (RocketPB) this.bulletClass.newInstance();
            this.rocket.pos.setBase(this.actor, this.hook, false);
            if (this.bRocketPosRel) {
                this.rocket.pos.changeHookToRel();
            }
            this.rocket.pos.resetAsBase();
            this.rocket.visibilityAsBase(true);
            if (this.bRocketPosRel) {
                this.rocket.pos.setUpdateEnable(false);
            }
        } catch (Exception exception) {
        }
    }

    public void setHookToRel(boolean flag) {
        if (this.bRocketPosRel == flag) {
            return;
        }
        if (Actor.isValid(this.rocket)) {
            if (flag) {
                this.rocket.pos.changeHookToRel();
                this.rocket.pos.setUpdateEnable(false);
            } else {
                this.rocket.pos.setRel(RocketGunPB.nullLoc);
                this.rocket.pos.setBase(this.rocket.pos.base(), this.hook, false);
                this.rocket.pos.setUpdateEnable(true);
            }
        }
        this.bRocketPosRel = flag;
    }

    public String getHookName() {
        return this.hook.name();
    }

    public void set(Actor actor, String s, Loc loc) {
        this.set(actor, s);
    }

    public void set(Actor actor, String s, String s1) {
        this.set(actor, s);
    }

    public void set(Actor actor, String s) {
        this.actor = actor;
        Class class1 = this.getClass();
        this.bCassette = Property.containsValue(class1, "cassette");
        this.bulletClass = (Class) Property.value(class1, "bulletClass", null);
        this.bullets(Property.intValue(class1, "bullets", 1));
        this.bulletsFull = this.bullets();
        this.setBulletClass(this.bulletClass);
        float f = Property.floatValue(class1, "shotFreq", 0.5F);
        if (f < 0.001F) {
            f = 0.001F;
        }
        this.shotStep = (int) (((1.0F / f) + (Time.tickConstLenFs() / 2.0F)) / Time.tickConstLenFs());
        if (this.shotStep <= 0) {
            this.shotStep = 1;
        }
        this.hook = (HookNamed) actor.findHook(s);
        this.newRocket();
        this.actor.interpPut(this, null, -1L, null);
    }

    public void setConvDistance(float f, float f1) {
        if (!Actor.isValid(this.rocket)) {
            return;
        } else {
            Point3d point3d = this.rocket.pos.getRelPoint();
            Orient orient = new Orient();
            orient.set(this.rocket.pos.getRelOrient());
            Math.sqrt((point3d.y * point3d.y) + f * f);
            this.plusYaw = (float) Math.toDegrees(Math.atan(-point3d.y / f));
            this.plusPitch = f1;
            return;
        }
    }

    protected boolean     ready;
    protected RocketPB    rocket;
    protected HookNamed   hook;
    protected boolean     bHide;
    protected boolean     bRocketPosRel;
    protected Class       bulletClass;
    protected int         bulletsFull;
    protected int         bulletss;
    protected int         shotStep;
    private boolean       bCassette;
    protected float       timeLife;
    private float         plusPitch;
    private float         plusYaw;
    protected float       bulletMassa;
    private int           curShotStep;
    private int           curShots;
    protected boolean     bTickShot;
    private static Orient _tmpOr0 = new Orient();
    private static Orient _tmpOr1 = new Orient();
    private static Loc    nullLoc = new Loc();

}
