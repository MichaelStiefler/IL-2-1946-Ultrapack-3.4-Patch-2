package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorLand;
import com.maddox.rts.Time;

public class BombStarthilfeSolfuel extends Bomb
{

    public BombStarthilfeSolfuel()
    {
//        chute = null;
//        bOnChute = false;
    }

    protected boolean haveSound()
    {
        return false;
    }

    public void startSound() {
        if (!Config.isUSE_RENDER()) {
            return;
        }
        if (this.sound == null) {
            this.sound = this.newSound("weapon.rato", true);
        }
        this.sound.setVolume(1.0F);
    }

    public void stopSound() {
        if (!Config.isUSE_RENDER()) {
            return;
        }
        if (this.sound != null) {
            this.sound.cancel();
        }
    }

//    public void start()
//    {
//        super.start();
//        ttcurTM = World.Rnd().nextFloat(0.5F, 3.5F);
//    }
    
    public void start()
    {
        postDestroy();
    }
    
    public void interpolateTick()
    {
        float f1 = Time.tickLenFs();
        float f3 = (float)this.v.length();
        
        float f2 = 10.0F / (f3 + 0.1F);
        if (f2 > 1.0F) {
          f2 = 1.0F;
        }
        f2 *= dv[this.lr] * f1;
        
        this.pos.getAbs(p, o);
        o.increment(this.W.z * f1, this.W.y * f1, -this.W.x * f1);
        oh.set(f2, 0.0F, 0.0F);
        oh.transform(this.v);
        o.setYaw(o.getYaw() - f2);
        float f5 = this.A * f1;
        this.v.x = deceleron(this.v.x, f5);
        this.v.y = deceleron(this.v.y, f5);
        this.v.z = deceleron(this.v.z, f5);
        this.v.z -= World.g() * f1;
        p.scaleAdd(f1, this.v, p);

    }
    
    private double deceleron(double d, float f)
    {
      if (d > 0.0D) {
        d -= f * d * d;
      } else {
        d += f * d * d;
      }
      return d;
    }

//    public void interpolateTick()
//    {
//        super.interpolateTick();
//        if(bOnChute)
//        {
//            this.pos.getAbs(or);
//            or.interpolate(or_, 0.4F);
//            this.pos.setAbs(or);
//            getSpeed(v3d);
//            v3d.scale(0.997D);
//            if(v3d.z < -5D)
//                v3d.z += 1.1F * Time.tickConstLenFs();
//            setSpeed(v3d);
//        } else
//        if(this.curTm > ttcurTM)
//        {
//            bOnChute = true;
//            chute = new Chute(this);
//            chute.collide(false);
//        }
//    }

    public void msgCollision(Actor actor, String s, String s_0_)
    {
        if(actor instanceof ActorLand)
        {
//            if(chute != null)
//                chute.landing();
            postDestroy();
        } else
        {
            super.msgCollision(actor, s, s_0_);
        }
    }

//    private Chute chute;
//    boolean bOnChute;
//    private static Orient or = new Orient();
//    private static Orient or_ = new Orient(0.0F, 0.0F, 0.0F);
//    private static Vector3d v3d = new Vector3d();
//    float ttcurTM;

    private static float[] dv = { -80.0F, -60.0F, -40.0F, -20.0F, 20.0F, 40.0F, 60.0F, 80.0F, 0.0F };
    private float A;
    private int lr;
    private Vector3d v = new Vector3d();
    private Vector3f W = new Vector3f();
    private static Point3d p = new Point3d();
    private static Orient o = new Orient();
    private static Orient oh = new Orient();
}
