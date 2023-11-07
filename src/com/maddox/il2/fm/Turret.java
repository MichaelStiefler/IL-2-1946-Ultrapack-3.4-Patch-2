/*4.10.1 class*/
package com.maddox.il2.fm;

import java.lang.reflect.Method;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Loc;
import com.maddox.sas1946.il2.util.Reflection;

public class Turret {
    public int              indexA;
    public int              indexB;
    public Loc              Lstart                = new Loc();
    public Actor            target;
    public float[]          tu                    = new float[3];
    public float[]          tuLim                 = new float[3];
    public boolean          bIsAIControlled       = true;
    public boolean          bIsNetMirror          = false;
    public boolean          bIsOperable           = true;
    public long             timeNext              = 0L;
    public boolean          bIsShooting           = false;
    public int              tMode                 = 0;
    public float            health                = 1.0F;
    public static final int TU_MO_SLEEP           = 0;
    public static final int TU_MO_TRACKING        = 1;
    public static final int TU_MO_FIRING_TRACKING = 2;
    public static final int TU_MO_FIRING_STOPPED  = 3;
    public static final int TU_MO_PANIC           = 4;
    public static final int TU_MO_STOPPED         = 5;
    
    // TODO: +++ Added by SAS~Storebror: Skip "Blocked by Plane" checks on remote turrets
    private boolean         checkPlaneBlocking    = true;
    
    public boolean isCheckPlaneBlocking() {
        return this.checkPlaneBlocking;
    }
    
    public void initPlaneBlockingChecks(Actor owner, int turretIndex) {
        Class currentClass = owner.getClass();
//        System.out.println("initPlaneBlockingChecks(" + currentClass.getName() + ", " + turretIndex);
        Method method = Reflection.getMethod(currentClass, "checkTurretVisibilityPlaneBlocking", new Class[] {Integer.TYPE});
        while (method == null && currentClass != Object.class) {
            currentClass = currentClass.getSuperclass();
            method = Reflection.getMethod(currentClass, "checkTurretVisibilityPlaneBlocking", new Class[] {Integer.TYPE});
        }
        if (method != null) {
            try {
                this.checkPlaneBlocking = ((Boolean)method.invoke(null, new Object[] {new Integer(turretIndex)})).booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // ---

    // TODO: Lutz mod
    // ----------------------------
    public float gunnerSkill;
    public int   igunnerSkill;
    // ----------------------------

    // TODO: +++ TD AI code backport from 4.13 +++
    public int              obsDir;
    public static final int FRONT = 1;
    public static final int REAR  = 2;
    public static final int LEFT  = 3;
    public static final int RIGHT = 4;

    public void setObservingDirection() {
        float f = this.Lstart.getAzimut();
        if (f > 360F) f -= 360F;
        if (f < 45F || f > 315F) this.obsDir = 1;
        else if (f < 135F && f > 45F) this.obsDir = 4;
        else if (f < 315F && f > 225F) this.obsDir = 3;
        else this.obsDir = 2;
    }
    // TODO: --- TD AI code backport from 4.13 ---

    public Turret() {
        this.Lstart = new Loc();
        this.tu = new float[3];
        this.tuLim = new float[3];
        this.bIsAIControlled = true;
        this.bIsNetMirror = false;
//        bMultiFunction = false;
        this.bIsOperable = true;
        this.timeNext = 0L;
        this.bIsShooting = false;
        this.tMode = 0;
        this.health = 1.0F;
        this.obsDir = 0;
    }

    // TODO: --- TD AI code backport from 4.13 ---

    public void setHealth(float f) {
        this.health = f;
        if (f == 0.0F) this.bIsOperable = false;
    }

    // TODO: |ZUTI| methods and variables
    // ----------------------------------------------------------
    public void zutiDisableTurret() {
        this.bIsAIControlled = false;
    }

    public void zutiEnableTurret() {
        this.bIsAIControlled = true;
    }
    // ----------------------------------------------------------
}
