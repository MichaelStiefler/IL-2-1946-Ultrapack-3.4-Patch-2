package com.maddox.il2.objects.trains;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.MsgExplosionListener;
import com.maddox.il2.ai.MsgShotListener;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.MsgCollisionRequestListener;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.rts.MsgAction;
import com.maddox.rts.Spawn;

public class OilTank extends Wagon implements MsgCollisionRequestListener, MsgExplosionListener, MsgShotListener
{
    
    protected void explode(Actor actor) {
        new MsgAction(0.0) {
            public void doAction() {
                Point3d point3d = new Point3d();
                OilTank.this.pos.getAbs(point3d);
                Explosions.ExplodeVagonFuel(point3d, point3d, 2.0f);
            }
        };
        new MsgAction(1.0, (Object)new Pair(this, actor)) {
            public void doAction(Object o) {
                Actor owner = OilTank.this.getOwner();
                if (owner != null) {
                    ((Train)owner).wagonDied(((Pair)o).victim, ((Pair)o).initiator);
                }
                OilTank.this.life = -1.0f;
                OilTank.this.ActivateMesh();
            }
        };
        new MyMsgAction(1.82, (Object)this, (Object)actor) {
            public void doAction(Object o) {
                Point3d point3d = new Point3d();
                OilTank.this.pos.getAbs(point3d);
                MsgExplosion.send((Actor)o, "Body", point3d, (Actor)this.obj2, 0.0f, 180.0f, 0, 120.0f);
            }
        };
        new MsgAction(2.2, (Object)this) {
            public void doAction(Object o) {
                Wagon wagon = (Wagon)o;
                try {
                    Eff3DActor.New(wagon, new HookNamed(wagon, "Damage"), null, 1.0f, "Effects/Smokes/SmokeOilTank1.eff", 30.0f);
                    Eff3DActor.New(wagon, new HookNamed(wagon, "Damage"), null, 1.0f, "Effects/Smokes/SmokeOilTank3.eff", 35.0f);
                } catch (Exception e) {}
            }
        };
        new MsgAction(34.0, (Object)this) {
            public void doAction(Object o) {
                Wagon wagon = (Wagon)o;
                try {
                    Eff3DActor.New(wagon, new HookNamed(wagon, "Damage"), null, 1.0f, "Effects/Smokes/SmokeOilTank2.eff", 156.0f);
                } catch (Exception e) {}
            }
        };
    }
    
    public OilTank(Train train) {
        super(train, getMeshName(0), getMeshName(1));
        this.life = 0.02f;
        this.ignoreTNT = 0.35f;
        this.killTNT = 2.8f;
        this.bodyMaterial = 2;
    }
    
    private static String getMeshName(int n) {
        String s = null;
        switch (World.cur().camouflage) {
            case 0: {
                s = "summer";
                break;
            }
            case 1: {
                s = "winter";
                break;
            }
            default: {
                s = "summer";
                break;
            }
        }
        return "3do/Trains/OilTank" + ((n != 1) ? "" : "_Dmg") + "/" + s + "/hier.him";
    }
    
    public static String getMeshNameForEditor() {
        return getMeshName(0);
    }
    
    static {
        Spawn.add(OilTank.class, new SPAWN());
    }
    
    public static class SPAWN implements WagonSpawn
    {
        public Wagon wagonSpawn(Train train) {
            return new OilTank(train);
        }
    }
}
