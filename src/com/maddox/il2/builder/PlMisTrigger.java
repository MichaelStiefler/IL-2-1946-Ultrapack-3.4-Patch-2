// TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
// This class is completely new to Triggers
// Rewritten and refactored by SAS~Storebror

package com.maddox.il2.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.maddox.JGP.Point2d;
import com.maddox.JGP.Point3d;
import com.maddox.gwindow.GNotifyListener;
import com.maddox.gwindow.GWindow;
import com.maddox.gwindow.GWindowButton;
import com.maddox.gwindow.GWindowCheckBox;
import com.maddox.gwindow.GWindowComboControl;
import com.maddox.gwindow.GWindowDialogClient;
import com.maddox.gwindow.GWindowEditControl;
import com.maddox.gwindow.GWindowEditText;
import com.maddox.gwindow.GWindowEditTextControl;
import com.maddox.gwindow.GWindowHSliderInt;
import com.maddox.gwindow.GWindowLabel;
import com.maddox.gwindow.GWindowMenuItem;
import com.maddox.gwindow.GWindowTabDialogClient;
import com.maddox.gwindow.GWindowTable;
import com.maddox.il2.ai.Army;
import com.maddox.il2.ai.Trigger;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.engine.Render;
import com.maddox.il2.game.I18N;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;
import com.maddox.util.NumberTokenizer;
import com.maddox.util.UnicodeTo8bit;

public class PlMisTrigger extends Plugin {

    class Table extends GWindowTable {

        public int countRows() {
            return actorTriggerList == null ? 0 : actorTriggerList.size();
        }

        public Object getValueAt(int i, int whatever) {
            if (actorTriggerList == null) return null;
            if (i < 0 || i >= actorTriggerList.size()) return null;
            if (!(actorTriggerList.get(i) instanceof Actor)) return null;
            Actor actor = (Actor) actorTriggerList.get(i);
            String shortClassName = shortClassName(actor.getClass());
            double mapSizeX = Main3D.cur3D().land2D.mapSizeX() / 1000D;
            double posX = (Main3D.cur3D().land2D.worldOfsX() + actor.pos.getAbsPoint().x) / 10000D;
            double posY = (Main3D.cur3D().land2D.worldOfsY() + actor.pos.getAbsPoint().y) / 10000D;
            char cx1 = (char) (int) (65D + Math.floor((posX / 676D - Math.floor(posX / 676D)) * 26D));
            char cx2 = (char) (int) (65D + Math.floor((posX / 26D - Math.floor(posX / 26D)) * 26D));
            String mapGrid = new String();
            if (mapSizeX > 260D) mapGrid = "" + cx1 + cx2;
            else mapGrid = "" + cx2;
            mapGrid += "-" + (int) Math.ceil(posY);
            if (actor instanceof ActorTrigger) {
                switch (((ActorTrigger)actor).getType()) {
                    case Trigger.TYPE_SPAWN:
                        shortClassName = "TriggerSpawn";
                        break;
                    case Trigger.TYPE_ACTIVATE:
                        shortClassName = "TriggerSpawnRelativeAlt";
                        break;
                    case Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE:
                        shortClassName = "TriggerActivate";
                        break;
                    case Trigger.TYPE_MESSAGE:
                        shortClassName = "TriggerMessage";
                        break;
                    default:
                        shortClassName = "Trigger";
                        break;
                }
                return actor.name() + " (" + shortClassName + ") [" + mapGrid + "]";
            }

            Plugin plugin = (Plugin) Property.value((actor instanceof PPoint) ? actor.getOwner() : actor, "builderPlugin");
            if (plugin == null) return actor.name() + " (" + shortClassName + ")";
            String[] strings = plugin.actorInfo(actor);

            return ((actor instanceof PPoint) ? actor.getOwner().name() : actor.name()) + " (" + strings[0] + ") [" + mapGrid + "]";
        }

        public void resolutionChanged() {
            super.vSB.scroll = rowHeight(0);
            super.resolutionChanged();
        }

        public ArrayList actorTriggerList;

        public Table(GWindow gwindow, String string, float x, float y, float width, float height) {
            super(gwindow, x, y, width, height);
            actorTriggerList = new ArrayList();
            super.bColumnsSizable = false;
            addColumn(string, null);
            super.vSB.scroll = rowHeight(0);
            resized();
        }
    }

    public static String shortClassName(Class theClass) {
        if (theClass.isArray()) {
            return shortClassName(theClass.getComponentType()) + "[]";
        }
        String str = theClass.getName();
        return str.substring(str.lastIndexOf(".") + 1);
    }

    static class Item {

        public String name;
        public int    indx;

        public Item(String s, int i) {
            this.name = s;
            this.indx = i;
        }
    }

    public PlMisTrigger() {
        this.allActors = new ArrayList();
        this.line2XYZ = new float[6];
        this.p2d = new Point2d();
        this.p2dt = new Point2d();
        this.p3d = new Point3d();
    }

    private int TriggerColor(boolean flag) {
        if (flag) return Builder.colorSelected();
        else return 0xffc8c800;
    }

    public void renderMap2DBefore() {
        if (Plugin.builder.isFreeView()) return;
        if (!this.wShowTrigger.bChecked) return;
        Actor actor = Plugin.builder.selectedActor();
        Render.drawBeginLines(-1);
        for (int j = 0; j < this.allActors.size(); j++) {
            ActorTrigger actortrigger = (ActorTrigger) this.allActors.get(j);
//            if (Actor.isValid(actortrigger.getTriggerActor()) && Plugin.builder.project2d(actortrigger.pos.getAbsPoint(), this.p2d) && Plugin.builder.project2d(actortrigger.getTriggerActor().pos.getAbsPoint(), this.p2dt)
//                    && this.p2d.distance(this.p2dt) > 4D) {
//                int k = this.TriggerColor(actortrigger == actor);
//                this.line2XYZ[0] = (float) this.p2d.x;
//                this.line2XYZ[1] = (float) this.p2d.y;
//                this.line2XYZ[2] = 0.0F;
//                this.line2XYZ[3] = (float) this.p2dt.x;
//                this.line2XYZ[4] = (float) this.p2dt.y;
//                this.line2XYZ[5] = 0.0F;
//                Render.drawBeginLines(-1);
//                Render.drawLines(this.line2XYZ, 2, 1.25F, k, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE | Mat.BLEND, 3);
//                Render.drawEnd();
//            }

            for (int actorTriggerIndex = 0; actorTriggerIndex < actortrigger.getTriggerActors().size(); actorTriggerIndex++) {
                Actor triggerActor = (Actor) actortrigger.getTriggerActors().get(actorTriggerIndex);
                if (Actor.isValid(triggerActor) && Plugin.builder.project2d(actortrigger.pos.getAbsPoint(), this.p2d) && Plugin.builder.project2d(triggerActor.pos.getAbsPoint(), this.p2dt) && this.p2d.distance(this.p2dt) > 4D) {
                    int k = this.TriggerColor(actortrigger == actor);
                    this.line2XYZ[0] = (float) this.p2d.x;
                    this.line2XYZ[1] = (float) this.p2d.y;
                    this.line2XYZ[2] = 0.0F;
                    this.line2XYZ[3] = (float) this.p2dt.x;
                    this.line2XYZ[4] = (float) this.p2dt.y;
                    this.line2XYZ[5] = 0.0F;
//                    Render.drawBeginLines(-1);
                    Render.drawLines(this.line2XYZ, 2, 1.25F, k, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE | Mat.BLEND, 3);
//                    Render.drawEnd();
                }
            }

            if (Actor.isValid(actortrigger.getLinkActor()) && Plugin.builder.project2d(actortrigger.pos.getAbsPoint(), this.p2d) && Plugin.builder.project2d(actortrigger.getLinkActor().pos.getAbsPoint(), this.p2dt) && this.p2d.distance(this.p2dt) > 4D) {
                int k = 0xff00ffff;
                this.line2XYZ[0] = (float) this.p2d.x;
                this.line2XYZ[1] = (float) this.p2d.y;
                this.line2XYZ[2] = 0.0F;
                this.line2XYZ[3] = (float) this.p2dt.x;
                this.line2XYZ[4] = (float) this.p2dt.y;
                this.line2XYZ[5] = 0.0F;
//                Render.drawBeginLines(-1);
                Render.drawLines(this.line2XYZ, 2, 1.25F, k, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE | Mat.BLEND, 3);
//                Render.drawEnd();
            }
        }
        Render.drawEnd();
    }

    public void renderMap2DAfter() {
        if (Plugin.builder.isFreeView()) return;
        if (!this.wShowTrigger.bChecked) return;
        Actor actor = Plugin.builder.selectedActor();
        for (int j = 0; j < this.allActors.size(); j++) {
            ActorTrigger actortrigger = (ActorTrigger) this.allActors.get(j);
            if (Plugin.builder.project2d(actortrigger.pos.getAbsPoint(), this.p2d)) {
                int k = this.TriggerColor(actortrigger == actor);
                IconDraw.setColor(k);

                for (int actorTriggerIndex = 0; actorTriggerIndex < actortrigger.getTriggerActors().size(); actorTriggerIndex++) {
                    Actor triggerActor = (Actor) actortrigger.getTriggerActors().get(actorTriggerIndex);
                    if (Actor.isValid(triggerActor) && Plugin.builder.project2d(triggerActor.pos.getAbsPoint(), this.p2dt) && this.p2d.distance(this.p2dt) > 4D) Render.drawTile((float) (this.p2dt.x - Plugin.builder.conf.iconSize / 2),
                            (float) (this.p2dt.y - Plugin.builder.conf.iconSize / 2), Plugin.builder.conf.iconSize, Plugin.builder.conf.iconSize, 0.0F, Plugin.targetIcon, k, 0.0F, 1.0F, 1.0F, -1F);
                }
//                if (Actor.isValid(actortrigger.getTriggerActor()) && Plugin.builder.project2d(actortrigger.getTriggerActor().pos.getAbsPoint(), this.p2dt) && this.p2d.distance(this.p2dt) > 4D)
//                    Render.drawTile((float) (this.p2dt.x - Plugin.builder.conf.iconSize / 2), (float) (this.p2dt.y - Plugin.builder.conf.iconSize / 2), Plugin.builder.conf.iconSize, Plugin.builder.conf.iconSize, 0.0F, Plugin.targetIcon, k, 0.0F, 1.0F,
//                            1.0F, -1F);
                IconDraw.render(actortrigger, this.p2d.x, this.p2d.y);
                actortrigger.pos.getAbs(this.p3d);
                this.p3d.x += actortrigger.getRadius();
                if (Plugin.builder.project2d(this.p3d, this.p2dt)) {
                    double d = this.p2dt.x - this.p2d.x;
                    if (d > Plugin.builder.conf.iconSize / 3) this.drawCircle(this.p2d.x, this.p2d.y, d, k);
                }
            }
        }

    }

    private void drawCircle(double d, double d1, double d2, int i) {
//        System.out.println("drawCircle(" + d + ", " + d1 + ", " + d2 + ", " + i + ")");
        int j = 48;
        double d3 = Math.PI * 2D / j;
        double d4 = 0.0D;
        for (int k = 0; k < j; k++) {
            _circleXYZ[k * 3 + 0] = (float) (d + d2 * Math.cos(d4));
            _circleXYZ[k * 3 + 1] = (float) (d1 + d2 * Math.sin(d4));
            _circleXYZ[k * 3 + 2] = 0.0F;
            d4 += d3;
        }

        Render.drawBeginLines(-1);
        Render.drawLines(_circleXYZ, j, 1.0F, i, Mat.NOWRITEZ | Mat.MODULATE | Mat.NOTEXTURE, 4);
        Render.drawEnd();
    }

    public boolean save(SectFile sectfile) {
        int i = this.allActors.size();
        if (i == 0) return true;
        int j = sectfile.sectionAdd("Trigger");
        sectfile.lineAdd(j, "Version " + Trigger.VERSION);
        for (int k = 0; k < i; k++) {
            ActorTrigger actortrigger = (ActorTrigger) this.allActors.get(k);
            String triggerTargets = "";
            String triggerLinks = "";

            
            if (actortrigger.getType() != Trigger.TYPE_MESSAGE) {
                if (actortrigger.getTriggerActors() != null && actortrigger.getTriggerActors().size() > 0) {
                    for (int actorTriggerIndex = 0; actorTriggerIndex < actortrigger.getTriggerActors().size(); actorTriggerIndex++) {
                        if (actorTriggerIndex > 0) triggerTargets += "|";
                        Actor triggerActor = (Actor) actortrigger.getTriggerActors().get(actorTriggerIndex);
                        if (triggerActor instanceof PPoint) triggerTargets += triggerActor.getOwner().name();
                        else triggerTargets += triggerActor.name();
                    }
                } else {
                    triggerTargets = "none";
                }
            }

//            if (actortrigger.getType() != Trigger.TYPE_MESSAGE) {
//                if (actortrigger.getType() != Trigger.TYPE_MESSAGE && !Actor.isValid(actortrigger.getTriggerActor())) continue;
//                if (actortrigger.getTriggerActor() instanceof PPoint) s = actortrigger.getTriggerActor().getOwner().name();
//                else s = actortrigger.getTriggerActor().name();
//            }
            boolean bLink = false;
            if (Actor.isValid(actortrigger.getLinkActor())) {
                if (actortrigger.getLinkActor() instanceof PPoint) triggerLinks = actortrigger.getLinkActor().getOwner().name();
                else triggerLinks = actortrigger.getLinkActor().name();
                bLink = true;
            }
            
            String s3 = actortrigger.getDisplayMessage();
            sectfile.lineAdd(j, actortrigger.name(),
                    actortrigger.getType() + " " + actortrigger.getTriggeredByArmy() + " " + (actortrigger.isHasTimeout() ? "1 " : "0 ") + actortrigger.getTimeout() + " " + (int) actortrigger.pos.getAbsPoint().x + " "
                            + (int) actortrigger.pos.getAbsPoint().y + " " + actortrigger.getRadius() + " " + actortrigger.getAltitudeMin() + " " + actortrigger.getAltitudeMax() + (actortrigger.getType() == Trigger.TYPE_MESSAGE ? "" : " " + triggerTargets) + " "
                            + actortrigger.getTriggeredBy() + " " + (actortrigger.isTriggerOnExit() ? "1" : "0") + " " + actortrigger.getNoObjectsMin() + " " + actortrigger.getProbability()
                            + (actortrigger.getType() != Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE ? "" : " " + actortrigger.getDeltaAltitude()) + (bLink ? " 1 " + triggerLinks : " 0")
                            + (s3 == null || s3.length() <= 0 ? "" : " " + actortrigger.getDisplayTime() + " " + s3));
        }

        return true;
    }

    private void makeName(Actor actor, String s) {
        if (s != null && Actor.getByName(s) == null) {
            actor.setName(s);
            return;
        }
        int i = 0;
        do {
            s = i + "_Trigger";
            if (Actor.getByName(s) != null) i++;
            else {
                actor.setName(s);
                return;
            }
        } while (true);
    }

    public void load(SectFile sectfile) {
        int i = sectfile.sectionIndex("Trigger");
        HashMap pendingActorsForAdding = new HashMap();
        if (i >= 0) {
            int j = sectfile.vars(i);
            Point3d point3d = new Point3d();
            int k = 0;
            if (sectfile.line(i, k).indexOf("Version ") >= 0) k = 1;
            for (; k < j; k++) {
                NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.line(i, k));
                String name = numbertokenizer.next(null);
                int type = Trigger.TYPE_SPAWN;
                if (name.toLowerCase().indexOf("_trigger") > 0) type = numbertokenizer.next(0);
                else {
                    type = Integer.parseInt(name);
                    name = null;
                }
                int army = numbertokenizer.next(1, 1, 2);
                boolean btimeout = numbertokenizer.next(0) == 1;
                int timeout = numbertokenizer.next(0, 0, 720);
                point3d.x = numbertokenizer.next(0);
                point3d.y = numbertokenizer.next(0);
                int radius = numbertokenizer.next(1000);
                if (radius < 2) radius = 2;
                if (radius > 75000) radius = 75000;
                int altitudeMin = numbertokenizer.next(0, 0, 20000);
                int altitudeMax = numbertokenizer.next(10000, 0, 20000);
                String triggerTargetName = null;
                if (type != Trigger.TYPE_MESSAGE) triggerTargetName = numbertokenizer.next(null);
                int appliesFor = numbertokenizer.next(0, 0, 11);
                boolean bSortie = numbertokenizer.next(0) == 1;
                int noObjectMin = numbertokenizer.next(1, 1, 1000);
                int probability = numbertokenizer.next(100, 0, 100);
                int altitudeDifference = 0;
                if (type == Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE) altitudeDifference = numbertokenizer.next(0, -10000, 10000);
                boolean hasLink = numbertokenizer.next(0) == 1;
                String triggerLinkName = null;
                if (hasLink) triggerLinkName = numbertokenizer.next(null);
                int messageTimeout = numbertokenizer.next(5, 1, 60);
                String hudMessage = "";
                while (numbertokenizer.hasMoreElements()) {
                    hudMessage = hudMessage + numbertokenizer.next(null);
                    if (numbertokenizer.hasMoreElements()) hudMessage = hudMessage + " ";
                }
                ActorTrigger actorTrigger = this.insert(name, point3d, type, false, army, btimeout, timeout, radius, altitudeMin, altitudeMax, appliesFor, bSortie, noObjectMin, probability, altitudeDifference, hudMessage, messageTimeout);
                // System.out.println("ActorTrigger " + name + " inserted!");
                if (actorTrigger != null) {
                    pendingActorsForAdding.put(actorTrigger, triggerTargetName);
                    // actorTrigger.addActor(s);
                    if (hasLink) actorTrigger.addLinkActor(triggerLinkName);
                }
            }

            // add Trigger Actors now...
            Iterator iterator = pendingActorsForAdding.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if ((entry.getKey() instanceof ActorTrigger) && (entry.getValue() instanceof String)) {
                    StringTokenizer tokenizer = new StringTokenizer((String) entry.getValue(), "|");
                    while (tokenizer.hasMoreTokens()) {
                        String nextToken = tokenizer.nextToken();
                        if (!nextToken.equals("none")) ((ActorTrigger) entry.getKey()).addActor(nextToken);
                    }
                }
            }
        }
    }

    public void deleteAll() {
        int i = this.allActors.size();
        for (int j = 0; j < i; j++) {
            ActorTrigger actorTrigger = (ActorTrigger) this.allActors.get(j);
            actorTrigger.destroy();
        }

        this.allActors.clear();
    }

    public void delete(Actor actor) {
        this.allActors.remove(actor);
        actor.destroy();
    }

//    public void afterDelete() {
//        for (int i = 0; i < this.allActors.size();) {
//            ActorTrigger actortrigger = (ActorTrigger) this.allActors.get(i);
//            if (actortrigger.getTriggerActors() != null) {
//                boolean validForDeletion = true;
//                for (int actorTriggerIndex = 0; actorTriggerIndex < actortrigger.getTriggerActors().size(); actorTriggerIndex++) {
//                    if (actortrigger.getTriggerActors().get(actorTriggerIndex) == null || Actor.isValid((Actor) actortrigger.getTriggerActors().get(actorTriggerIndex))) {
//                        validForDeletion = false;
//                        break;
//                    }
//                }
//                if (validForDeletion) {
//                    actortrigger.destroy();
//                    this.allActors.remove(i);
//                    continue;
//                }
//            }
//            i++;
////            if (actortrigger.getTriggerActor() != null && !Actor.isValid(actortrigger.getTriggerActor())) {
////                actortrigger.destroy();
////                this.allActors.remove(i);
////            } else i++;
//        }
//
//    }
    
    public void afterDelete() {
        for (int i = 0; i < this.allActors.size(); i++) {
            ActorTrigger actortrigger = (ActorTrigger) this.allActors.get(i);
            if (actortrigger.getTriggerActors() != null) {
                                                
                for (int actorTriggerIndex = 0; actorTriggerIndex < actortrigger.getTriggerActors().size();) {
                    if (actortrigger.getTriggerActors().get(actorTriggerIndex) != null
                            && actortrigger.getTriggerActors().get(actorTriggerIndex) instanceof ActorTrigger
                            && !this.allActors.contains(actortrigger.getTriggerActors().get(actorTriggerIndex))) {
                        actortrigger.getTriggerActors().remove(actorTriggerIndex);
                    } else actorTriggerIndex++;
                }
            }
        }
    }


    private ActorTrigger insert(String name, Point3d point3d, int type, boolean flag, int army, boolean btimeout, int timeout, int radius, int altitudeMin, int altitudeMax, int appliesFor, boolean bSortie, int noObjectMin, int probability,
            int altitudeDifference, String hudMessage, int messageTimeout) {
        try {
            if (name != null) {
                if ("NONAME".equals(name)) name = null;
                if (Actor.getByName(name) != null) name = null;
            }
            ActorTrigger actortrigger = new ActorTrigger(point3d, type, army, btimeout, timeout, radius, altitudeMin, altitudeMax, appliesFor, bSortie, noObjectMin, probability, altitudeDifference, hudMessage, messageTimeout);
            this.makeName(actortrigger, name);

            // No need to search for duplicate ActorTrigger Instances because... why not?

//            for (int actorTriggerIndex = 0; actorTriggerIndex < actortrigger.getTriggerActors().size(); actorTriggerIndex++) {
//                Actor triggerActor = (Actor)actortrigger.getTriggerActors().get(actorTriggerIndex);
//                if (Actor.isValid(triggerActor)) {
//                    for (int k = 0; k < this.allActors.size(); k++) {
//                        ActorTrigger actortrigger1 = (ActorTrigger) this.allActors.get(k);
//                        for (int actorTriggerIndex1 = 0; actorTriggerIndex1 < actortrigger1.getTriggerActors().size(); actorTriggerIndex1++) {
//                            Actor triggerActor1 = (Actor)actortrigger1.getTriggerActors().get(actorTriggerIndex1);
//                            if (actortrigger.getType() == actortrigger1.getType() && Actor.isValid(triggerActor1)
//                                    && (triggerActor1 == triggerActor || triggerActor instanceof PPoint && triggerActor.getOwner() == triggerActor1.getOwner())) {
//                                actortrigger.destroy();
//                                return null;
//                            }              
//                        }
//                    }
//                }
//            }

//            if (Actor.isValid(actortrigger.getTriggerActor())) for (int k = 0; k < this.allActors.size(); k++) {
//                ActorTrigger actortrigger1 = (ActorTrigger) this.allActors.get(k);
//                if (actortrigger.getType() == actortrigger1.getType() && Actor.isValid(actortrigger1.getTriggerActor())
//                        && (actortrigger1.getTriggerActor() == actortrigger.getTriggerActor() || actortrigger.getTriggerActor() instanceof PPoint && actortrigger.getTriggerActor().getOwner() == actortrigger1.getTriggerActor().getOwner())) {
//                    actortrigger.destroy();
//                    return null;
//                }
//            }
            Plugin.builder.align(actortrigger);
            Property.set(actortrigger, "builderSpawn", "");
            Property.set(actortrigger, "builderPlugin", this);
            this.allActors.add(actortrigger);
            if (flag) Plugin.builder.setSelected(actortrigger);
            PlMission.setChanged();
            return actortrigger;
        } catch (Exception exception) {
            exception.getMessage();
            exception.printStackTrace();
        }
        return null;
    }

    public void insert(Loc loc, boolean flag) {
        int i = Plugin.builder.wSelect.comboBox1.getSelected();
        int j = Plugin.builder.wSelect.comboBox2.getSelected();
        if (i != this.startComboBox1) return;
        if (j < 0 || j >= this.item.length) return;
        else {
            this.insert(null, loc.getPoint(), this.item[j].indx, flag, 1, false, 0, 1000, 0, 10000, 0, false, 1, 100, 0, "", 5);
            return;
        }
    }

    public void changeType() {
        Plugin.builder.setSelected(null);
    }

    private void updateView() {
        int i = this.allActors.size();
        for (int j = 0; j < i; j++) {
            ActorTrigger actorTrigger = (ActorTrigger) this.allActors.get(j);
            actorTrigger.drawing(this.wShowTrigger.bChecked);
        }

    }

    public void configure() {
        if (Plugin.getPlugin("Mission") == null) throw new RuntimeException("PlMisTrigger: plugin 'Mission' not found");
        else {
            this.pluginMission = (PlMission) Plugin.getPlugin("Mission");
            return;
        }
    }

    private void fillComboBox2(int i) {
        if (i != this.startComboBox1) return;
        if (Plugin.builder.wSelect.curFilledType != i) {
            Plugin.builder.wSelect.curFilledType = i;
            Plugin.builder.wSelect.comboBox2.clear(false);
            for (int j = 0; j < this.item.length; j++)
                Plugin.builder.wSelect.comboBox2.add(Plugin.i18n(this.item[j].name));

            Plugin.builder.wSelect.comboBox1.setSelected(i, true, false);
        }
        Plugin.builder.wSelect.comboBox2.setSelected(0, true, false);
        Plugin.builder.wSelect.setMesh(null, true);
    }

    public void viewTypeAll(boolean flag) {
        this.wShowTrigger.bChecked = flag;
        this.updateView();
    }

    private void fillEditor(GWindowEditText gwindowedittext, String s) {
        String s1 = UnicodeTo8bit.load(s);
        ArrayList arraylist = new ArrayList();
        int i = 0;
        int j = 0;
        for (int k = s1.length(); j < k; j++) {
            char c = s1.charAt(j);
            if (c == '\n') {
                if (i < j) arraylist.add(s1.substring(i, j));
                else arraylist.add("");
                i = j + 1;
            }
        }

        if (i < j) arraylist.add(s1.substring(i, j));
        gwindowedittext.clear();
        gwindowedittext.insert(arraylist, true);
        gwindowedittext.clearUnDo();
    }

    private String getEditor(GWindowEditText gwindowedittext) {
        if (gwindowedittext.isEmpty()) return "";
        ArrayList arraylist = gwindowedittext.text;
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < arraylist.size(); i++) {
            StringBuffer stringbuffer1 = (StringBuffer) arraylist.get(i);
            if (stringbuffer1 != null && stringbuffer1.length() > 0) stringbuffer.append(stringbuffer1.toString());
            if (i < arraylist.size() - 1) stringbuffer.append('\n');
        }

        return UnicodeTo8bit.save(stringbuffer.toString(), false);
    }

    public void syncSelector() {
        // System.out.println("syncSelector");
        ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
        this.lstTriggerTarget.actorTriggerList = actortrigger.getTriggerActors();
        this.fillComboBox2(this.startComboBox1);
        Plugin.builder.wSelect.comboBox2.setSelected(actortrigger.getType(), true, false);
        Plugin.builder.wSelect.tabsClient.addTab(1, this.tabTrigger);
//        Plugin.builder.wSelect.tabsClient.addTab(2, this.tabTargets);
//        Plugin.builder.wSelect.tabsClient.addTab(3, this.tabMessage);
        this.wTriggerType.cap.set(Plugin.i18n(this.item[actortrigger.getType()].name));
        this.wHasTimeout.setChecked(actortrigger.isHasTimeout(), false);
        this.wTimeoutH.setEnable(actortrigger.isHasTimeout());
        this.wTimeoutM.setEnable(actortrigger.isHasTimeout());
        this.wTimeoutH.setValue("" + actortrigger.getTimeout() / 60 % 24, false);
        this.wTimeoutM.setValue("" + actortrigger.getTimeout() % 60, false);
        this.wRadius.setPos(actortrigger.getRadius() / 500, false);
//        this.wRadiusLabel.cap.set(Plugin.i18n("Rayon") + " : " + actortrigger.getRadius() + " m");
        this.wRadiusEdit.setValue("" + actortrigger.getRadius());
        this.wProbability.setValue("" + actortrigger.getProbability(), false);
        this.wAltitudeMin.setValue("" + actortrigger.getAltitudeMin(), false);
        this.wAltitudeMax.setValue("" + actortrigger.getAltitudeMax(), false);
        this.wNoObjectsMin.setValue("" + actortrigger.getNoObjectsMin(), false);
        this.wTriggeredByObjectType.list.set(0, Plugin.i18n("TAll"));
        for (int i = 0; i < 12; i++)
            this.wTriggeredByObjectType.posEnable[i] = true;

        if (actortrigger.getType() == Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE) {
            this.wTriggeredByObjectType.list.set(0, Plugin.i18n("TAll2"));
            for (int i = 3; i < 12; i++)
                this.wTriggeredByObjectType.posEnable[i] = false;

        }
        this.wTriggeredByObjectType.setSelected(actortrigger.getTriggeredBy(), true, false);
        this.wTriggerOnExit.setChecked(actortrigger.isTriggerOnExit(), false);
//        if (actortrigger.getTriggeredByArmy() == 2) this.wTriggeredByArmy.setSelected(0, true, false);
//        else this.wTriggeredByArmy.setSelected(1, true, false);
        this.wTriggeredByArmy.setSelected(actortrigger.getTriggeredByArmy() - 1, true, false);
        if (actortrigger.getType() == Trigger.TYPE_MESSAGE) {
//            this.wTriggerTargetClear.hideWindow();
//            this.wTriggerTargetLabel.hideWindow();
//            this.wTriggerTargetSet.hideWindow();
//            this.wTriggerTargetTitle.hideWindow();
            this.lstTriggerTarget.hideWindow();
            this.wTriggerTargetListAdd.hideWindow();
            this.wTriggerTargetListDelete.hideWindow();
            this.wTriggerTargetListClear.hideWindow();
        } else {
//            this.wTriggerTargetClear.showWindow();
//            this.wTriggerTargetLabel.showWindow();
//            this.wTriggerTargetSet.showWindow();
//            this.wTriggerTargetTitle.showWindow();
            this.lstTriggerTarget.showWindow();
            this.wTriggerTargetListAdd.showWindow();
            this.wTriggerTargetListDelete.showWindow();
            this.wTriggerTargetListClear.showWindow();
//            if (actortrigger.getTriggerActor() != null) {
//                if (actortrigger.getTriggerActor() instanceof PPoint) this.wTriggerTargetLabel.cap.set(actortrigger.getTriggerActor().getOwner().name());
//                else this.wTriggerTargetLabel.cap.set(actortrigger.getTriggerActor().name());
//            } else this.wTriggerTargetLabel.cap.set(Plugin.i18n("NoObject"));
        }
        if (actortrigger.getLinkActor() != null) {
            if (actortrigger.getLinkActor() instanceof PPoint) this.wTriggerLinkLabel.cap.set(actortrigger.getLinkActor().getOwner().name());
            else this.wTriggerLinkLabel.cap.set(actortrigger.getLinkActor().name());
        } else this.wTriggerLinkLabel.cap.set(Plugin.i18n("NoLink"));
        if (actortrigger.getType() == Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE) {
            this.wDeltaAltitude.showWindow();
            this.wDeltaAltitudeLabel.showWindow();
            this.wDeltaAltitude.setValue("" + actortrigger.getDeltaAltitude(), false);
        } else {
            this.wDeltaAltitude.hideWindow();
            this.wDeltaAltitudeLabel.hideWindow();
        }
        this.fillEditor(this.wDisplayMessage.edit, actortrigger.getDisplayMessage());
        this.wDisplayTime.setValue("" + actortrigger.getDisplayTime(), false);
    }

    public void updateSelector() {
        // System.out.println("updateSelector 1");

    }

    public void updateSelector(ActorTrigger actortrigger, Actor target) {
        // System.out.println("updateSelector 2 : " + actortrigger.getClass().getName() + ", " + target.getClass().getName());

    }

    public void createGUI() {
//        Plugin.builder.wSelect.metricWin = new GRegion(2.0F, 2.0F, 60.0F, 38.0F);
        this.startComboBox1 = Plugin.builder.wSelect.comboBox1.size();
        Plugin.builder.wSelect.comboBox1.add(Plugin.i18n("tTrigger"));
        Plugin.builder.wSelect.comboBox1.addNotifyListener(new GNotifyListener() {

            public boolean notify(GWindow gwindow, int j, int k) {
                int l = Plugin.builder.wSelect.comboBox1.getSelected();
                if (l >= 0 && j == 2) PlMisTrigger.this.fillComboBox2(l);
                return false;
            }

        });
        int i;
        for (i = Plugin.builder.mDisplayFilter.subMenu.size() - 1; i >= 0 && this.pluginMission.viewBridge != Plugin.builder.mDisplayFilter.subMenu.getItem(i); i--)
            ;
        if (--i >= 0) {
            this.wShowTrigger = Plugin.builder.mDisplayFilter.subMenu.addItem(i, new GWindowMenuItem(Plugin.builder.mDisplayFilter.subMenu, Plugin.i18n("showTrigger"), null) {

                public void execute() {
                    this.bChecked = !this.bChecked;
                    PlMisTrigger.this.updateView();
                }

            });
            this.wShowTrigger.bChecked = true;
        }
        float f = 1.0F;
        GWindowDialogClient triggerMainDialog = (GWindowDialogClient) Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient());
//        GWindowDialogClient triggerTargetDialog = (GWindowDialogClient) Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient());
//        GWindowDialogClient triggerMessageDialog = (GWindowDialogClient) Plugin.builder.wSelect.tabsClient.create(new GWindowDialogClient());
        this.tabTrigger = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("tTrigger"), triggerMainDialog);
//        this.tabTargets = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("tTargets"), triggerTargetDialog);
//        this.tabMessage = Plugin.builder.wSelect.tabsClient.createTab(Plugin.i18n("tMessage"), triggerMessageDialog);

        triggerMainDialog.addLabel(this.wTriggerType = new GWindowLabel(triggerMainDialog, 1.0F, f, 20F, 1.3F, Plugin.i18n("lType"), null));
        f += 2.0F;
        triggerMainDialog.addControl(this.wHasTimeout = new GWindowCheckBox(triggerMainDialog, 1.0F, f, null) {

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                else {
                    ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                    actortrigger.setHasTimeout(this.isChecked());
                    PlMisTrigger.this.wTimeoutH.setEnable(actortrigger.isHasTimeout());
                    PlMisTrigger.this.wTimeoutM.setEnable(actortrigger.isHasTimeout());
                    PlMission.setChanged();
                    return false;
                }
            }

        });
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 3F, f, 5F, 1.3F, Plugin.i18n("TimeOut"), null));
        triggerMainDialog.addControl(this.wTimeoutH = new GWindowEditControl(triggerMainDialog, 9F, f, 2.0F, 1.3F, "") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                else {
                    PlMisTrigger.this.getTimeOut();
                    return false;
                }
            }

        });
        triggerMainDialog.addControl(this.wTimeoutM = new GWindowEditControl(triggerMainDialog, 12F, f, 2.0F, 1.3F, "") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                else {
                    PlMisTrigger.this.getTimeOut();
                    return false;
                }
            }

        });
        triggerMainDialog.addLabel(this.wDeltaAltitudeLabel = new GWindowLabel(triggerMainDialog, 23F, f, 8F, 1.3F, Plugin.i18n("AltiDiff"), null));
        triggerMainDialog.addControl(this.wDeltaAltitude = new GWindowEditControl(triggerMainDialog, 32F, f, 5F, 1.3F, "0") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                int i = 0;
                try {
                    i = (int) Double.parseDouble(PlMisTrigger.this.wDeltaAltitude.getValue());
                } catch (Exception exception) {}
                actortrigger.setDeltaAltitude(i);
                if (actortrigger.getDeltaAltitude() > 10000) actortrigger.setDeltaAltitude(10000);
                if (actortrigger.getDeltaAltitude() < -10000) actortrigger.setDeltaAltitude(-10000);
                PlMission.setChanged();
                return false;
            }

        });
        f += 2.0F;
        triggerMainDialog.addControl(this.wRadius = new GWindowHSliderInt(triggerMainDialog, 0, 501, 15, 1.0F, f, 20F) {

            public void afterCreated() {
                super.afterCreated();
                super.bSlidingNotify = true;
            }

            public boolean notify(int j, int k) {
//                System.out.println("Slider wRadius notify(" + j + ", " + k + ")");
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                actortrigger.setRadius(this.pos() * 200);
                if (actortrigger.getRadius() < 2) actortrigger.setRadius(2);
                PlMission.setChanged();
//                PlMisTrigger.this.wRadiusLabel.cap.set(Plugin.i18n("Rayon") + " : " + actortrigger.getRadius() + " m");
                PlMisTrigger.this.wRadiusEdit.setValue("" + actortrigger.getRadius());
                return false;
            }

        });
        f += 2.0F;
//        gwindowdialogclient.addLabel(this.wRadiusLabel = new GWindowLabel(gwindowdialogclient, 1.0F, f, 20F, 1.3F, Plugin.i18n("Rayon"), null));
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 3.5F, 1.3F, Plugin.i18n("Rayon"), null));
        triggerMainDialog.addControl(this.wRadiusEdit = new GWindowEditControl(triggerMainDialog, 5.0F, f, 5F, 1.3F, "") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
//                System.out.println("EditControl wRadiusEdit notify(" + j + ", " + k + ")");
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                int radius = 0;
                try {
                    radius = (int) Double.parseDouble(PlMisTrigger.this.wRadiusEdit.getValue());
                } catch (Exception exception) {}
                actortrigger.setRadius(radius);
                if (actortrigger.getRadius() > 100000) actortrigger.setDeltaAltitude(100000);
                if (actortrigger.getRadius() < 2) actortrigger.setDeltaAltitude(2);
                PlMisTrigger.this.wRadius.setPos(actortrigger.getRadius() / 200, true);
                PlMission.setChanged();
                return false;
            }

        });

        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 10.0F, f, 3F, 1.3F, "m", null));
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 20F, 1.3F, Plugin.i18n("TiggerLink"), null));
        f += 2.0F;
        triggerMainDialog.addControl(new GWindowButton(triggerMainDialog, 10F, f - 0.5F, 5F, 1.6F, Plugin.i18n("&Set"), null) {

            public boolean notify(int i, int j) {
                if (i == 2) Plugin.builder.beginSelectTriggerLink();
                return false;
            }

        });
        triggerMainDialog.addControl(new GWindowButton(triggerMainDialog, 16F, f - 0.5F, 5F, 1.6F, Plugin.i18n("&Clear"), null) {

            public boolean notify(int i, int j) {
                if (i == 2) {
                    ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                    actortrigger.setLinkActor(null);
                    PlMisTrigger.this.wTriggerLinkLabel.cap.set(Plugin.i18n("NotSet"));
                    PlMission.setChanged();
                }
                return false;
            }

        });
        triggerMainDialog.addLabel(this.wTriggerLinkLabel = new GWindowLabel(triggerMainDialog, 1.0F, f - 0.5F, 8F, 1.3F, Plugin.i18n("NotSet"), null));
        f += 2.0F;
        triggerMainDialog.addControl(this.wTriggerOnExit = new GWindowCheckBox(triggerMainDialog, 1.0F, f, null) {

            public boolean notify(int j, int k) {
//                System.out.println("wTriggerTarget notify(" + j + ", " + k + ")");
                if (j != 2) return false;
                else {
                    ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                    actortrigger.setTriggerOnExit(this.isChecked());
                    PlMission.setChanged();
                    return false;
                }
            }

        });
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 3F, f, 18F, 1.3F, Plugin.i18n("TExit"), null));
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 7F, 1.3F, Plugin.i18n("TProbability"), null));
        triggerMainDialog.addControl(this.wProbability = new GWindowEditControl(triggerMainDialog, 9F, f, 3F, 1.3F, "100") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
//                System.out.println("EditControl wProbability notify(" + j + ", " + k + ")");
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                int i = 0;
                try {
                    i = (int) Double.parseDouble(PlMisTrigger.this.wProbability.getValue());
                } catch (Exception exception) {}
                actortrigger.setProbability(i);
                if (actortrigger.getProbability() > 100) actortrigger.setProbability(100);
                if (actortrigger.getProbability() < 0) actortrigger.setProbability(0);
                PlMission.setChanged();
                return false;
            }

        });
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 13F, f, 1.0F, 1.3F, "%", null));
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 7F, 1.3F, Plugin.i18n("AltiMin"), null));
        triggerMainDialog.addControl(this.wAltitudeMin = new GWindowEditControl(triggerMainDialog, 9F, f, 5F, 1.3F, "0") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                int i = 0;
                try {
                    i = (int) Double.parseDouble(PlMisTrigger.this.wAltitudeMin.getValue());
                } catch (Exception exception) {}
                actortrigger.setAltitudeMin(i);
                if (actortrigger.getAltitudeMin() > actortrigger.getAltitudeMax()) actortrigger.setAltitudeMin(actortrigger.getAltitudeMax());
                PlMission.setChanged();
                return false;
            }

        });
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 7F, 1.3F, Plugin.i18n("AltiMax"), null));
        triggerMainDialog.addControl(this.wAltitudeMax = new GWindowEditControl(triggerMainDialog, 9F, f, 5F, 1.3F, "10000") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                int i = 10000;
                try {
                    i = (int) Double.parseDouble(PlMisTrigger.this.wAltitudeMax.getValue());
                } catch (Exception exception) {}
                actortrigger.setAltitudeMax(i);
                if (actortrigger.getAltitudeMin() > actortrigger.getAltitudeMax()) actortrigger.setAltitudeMax(actortrigger.getAltitudeMin());
                PlMission.setChanged();
                return false;
            }

        });
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 7F, 1.3F, Plugin.i18n("TAvionsMIN"), null));
        triggerMainDialog.addControl(this.wNoObjectsMin = new GWindowEditControl(triggerMainDialog, 9F, f, 5F, 1.3F, "1") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                int i = 1;
                try {
                    i = (int) Double.parseDouble(PlMisTrigger.this.wNoObjectsMin.getValue());
                } catch (Exception exception) {}
                if (i < 1) i = 1;
                actortrigger.setNoObjectsMin(i);
                PlMission.setChanged();
                return false;
            }

        });
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 7F, 1.3F, Plugin.i18n("AppliesArmy"), null));
        triggerMainDialog.addControl(this.wTriggeredByObjectType = new GWindowComboControl(triggerMainDialog, 9F, f, 12F) {

            public void afterCreated() {
                super.afterCreated();
                this.setEditable(false);
                this.add(Plugin.i18n("TAll"));
                this.add(Plugin.i18n("TIAOnly"));
                this.add(Plugin.i18n("THumansOnly"));
                this.add(Plugin.i18n("TObject"));
                this.add(Plugin.i18n("TAirOnly"));
                this.add(Plugin.i18n("TAirGroundOnly"));
                this.add(Plugin.i18n("TArmourOnly"));
                this.add(Plugin.i18n("TArtilleryOnly"));
                this.add(Plugin.i18n("TInfantryOnly"));
                this.add(Plugin.i18n("TShipsOnly"));
                this.add(Plugin.i18n("TTrainsOnly"));
                this.add(Plugin.i18n("TVehiclesOnly"));
                this.posEnable = new boolean[12];
                for (int i = 0; i < 12; i++)
                    this.posEnable[i] = true;

            }

            public boolean notify(int i, int j) {
                if (i == 2) {
                    ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                    actortrigger.setTriggeredBy(this.getSelected());
                    PlMission.setChanged();
                }
                return false;
            }

        });
        f += 2.0F;
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 1.0F, f, 14F, 1.3F, Plugin.i18n("TriggerArmy"), null));
        triggerMainDialog.addControl(this.wTriggeredByArmy = new GWindowComboControl(triggerMainDialog, 16F, f, 5F) {

            public void afterCreated() {
                super.afterCreated();
                this.setEditable(false);
                this.add(I18N.army(Army.name(1)));
                this.add(I18N.army(Army.name(2)));
                this.add(Plugin.i18n("TriggerArmyBoth"));
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
//                if (this.getSelected() == 0) actortrigger.setTriggeredByArmy(2);
//                else actortrigger.setTriggeredByArmy(1);
                actortrigger.setTriggeredByArmy(this.getSelected() + 1);
                PlMission.setChanged();
                return false;
            }

        });

//        gwindowdialogclient.addLabel(new GWindowLabel(gwindowdialogclient, 23F, 18F, 14F, 1.3F, Plugin.i18n("TiggerListTitle"), null));
        this.lstTriggerTarget = new Table(triggerMainDialog, Plugin.i18n("TiggerListTitle"), 23F, 18F, 34F, 12F);

        triggerMainDialog.addControl(this.wTriggerTargetListAdd = new GWindowButton(triggerMainDialog, 23F, 30.5F, 6F, 1.6F, Plugin.i18n("TriggerTargetAdd"), null) {

            public boolean notify(int i, int j) {
                // System.out.println("PlMisTrigger wTriggerTargetListAdd notify(" + i + ", " + j + ")");
                if (i == 2) Plugin.builder.beginSelectTrigger();
                return false;
            }

        });
        triggerMainDialog.addControl(this.wTriggerTargetListDelete = new GWindowButton(triggerMainDialog, 35F, 30.5F, 6F, 1.6F, Plugin.i18n("TriggerTargetDelete"), null) {

            public boolean notify(int i, int j) {
                if (i == 2) {
                    if (PlMisTrigger.this.lstTriggerTarget.selectRow < 0) return false;
                    String selectedRow = (String)PlMisTrigger.this.lstTriggerTarget.getValueAt(PlMisTrigger.this.lstTriggerTarget.selectRow, 0);
                    if (selectedRow != null) {
                        PlMisTrigger.this.lstTriggerTarget.actorTriggerList.remove(PlMisTrigger.this.lstTriggerTarget.selectRow);
                        // System.out.println("Trigger Target element \"" + selectedRow + "\" removed!");
                    }
                    PlMission.setChanged();
                }
                return false;
            }

        });
        triggerMainDialog.addControl(this.wTriggerTargetListClear = new GWindowButton(triggerMainDialog, 47F, 30.5F, 6F, 1.6F, Plugin.i18n("TriggerTargetClear"), null) {

            public boolean notify(int i, int j) {
                if (i == 2) {
                    PlMisTrigger.this.lstTriggerTarget.actorTriggerList.clear();
                    // System.out.println("Trigger Target list cleared!");
                    PlMission.setChanged();
                }
                return false;
            }

        });

        f += 2.0F;
//        gwindowdialogclient.addLabel(this.wTriggerTargetTitle = new GWindowLabel(gwindowdialogclient, 1.0F, f, 20F, 1.3F, Plugin.i18n("TiggerCible"), null));
        f += 2.0F;
//        gwindowdialogclient.addControl(this.wTriggerTargetSet = new GWindowButton(gwindowdialogclient, 10F, f - 0.5F, 5F, 1.6F, Plugin.i18n("&Set"), null) {
//
//            public boolean notify(int i, int j) {
//                if (i == 2) Plugin.builder.beginSelectTrigger();
////                    return true;
//                return false;
//            }
//
//        });
//        gwindowdialogclient.addControl(this.wTriggerTargetClear = new GWindowButton(gwindowdialogclient, 16F, f - 0.5F, 5F, 1.6F, Plugin.i18n("&Clear"), null) {
//
//            public boolean notify(int i, int j) {
//                if (i == 2) {
////                    ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
////                    actortrigger.setTriggerActor(null);
//                    PlMisTrigger.this.wTriggerTargetLabel.cap.set(Plugin.i18n("NotSet"));
//                    PlMission.setChanged();
////                    return true;
//                }
//                return false;
//            }
//
//        });
//        gwindowdialogclient.addLabel(this.wTriggerTargetLabel = new GWindowLabel(gwindowdialogclient, 1.0F, f - 0.5F, 8F, 1.3F, Plugin.i18n("NotSet"), null));

        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 23F, 1F, 14F, 1.3F, Plugin.i18n("TTextToShow"), null));
        triggerMainDialog.addControl(this.wDisplayMessage = new GWindowEditTextControl(triggerMainDialog, 23F, 2.5F, 34F, 12F, "") {

            public boolean notify(GWindow gwindow, int i, int j) {
                if (gwindow == this.edit && i == 2 && j == 0) {
                    ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                    actortrigger.setDisplayMessage(PlMisTrigger.this.getEditor(this.edit));
                    PlMission.setChanged();
                }
                return super.notify(gwindow, i, j);
            }

        });
        triggerMainDialog.addLabel(new GWindowLabel(triggerMainDialog, 23F, 15.5F, 8F, 1.3F, Plugin.i18n("TTextDuree"), null));
        triggerMainDialog.addControl(this.wDisplayTime = new GWindowEditControl(triggerMainDialog, 32F, 15.5F, 5F, 1.3F, "5") {

            public void afterCreated() {
                super.afterCreated();
                this.bNumericOnly = true;
                this.bNumericFloat = true;
                this.bDelayedNotify = true;
            }

            public boolean notify(int j, int k) {
                if (j != 2) return false;
                ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
                float f = 1;
                try {
                    f = Float.parseFloat(PlMisTrigger.this.wDisplayTime.getValue());
                } catch (Exception exception) {}
                if (f < Time.tickLenFs()) f = Time.tickLenFs();
                actortrigger.setDisplayTime(f);
                PlMission.setChanged();
                return false;
            }

        });
    }

    private void getTimeOut() {
        ActorTrigger actortrigger = (ActorTrigger) Plugin.builder.selectedActor();
        String s = this.wTimeoutH.getValue();
        double d = 0.0D;
        try {
            d = Double.parseDouble(s);
        } catch (Exception exception) {}
        if (d < 0.0D) d = 0.0D;
        if (d > 12D) d = 12D;
        s = this.wTimeoutM.getValue();
        double d1 = 0.0D;
        try {
            d1 = Double.parseDouble(s);
        } catch (Exception exception1) {}
        if (d1 < 0.0D) d1 = 0.0D;
        if (d1 > 59D) d1 = 59D;
        actortrigger.setTimeout((int) (d * 60D + d1));
        this.wTimeoutH.setValue("" + actortrigger.getTimeout() / 60 % 24, false);
        this.wTimeoutM.setValue("" + actortrigger.getTimeout() % 60, false);
        PlMission.setChanged();
    }

    protected ArrayList                allActors;
    Item                               item[]       = {
            new Item("NewAircraftAir", Trigger.TYPE_SPAWN),
            new Item("NewAircraftSol", Trigger.TYPE_ACTIVATE),
            new Item("NewAircraftAirLevel",Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE),
            new Item("NewMessage", Trigger.TYPE_MESSAGE) };
    private float                      line2XYZ[];
    private Point2d                    p2d;
    private Point2d                    p2dt;
    private Point3d                    p3d;
    private static float               _circleXYZ[] = new float[144];
    private int                        startComboBox1;
    private GWindowMenuItem            wShowTrigger;
    private PlMission                  pluginMission;
    private GWindowTabDialogClient.Tab tabTrigger;
//    private GWindowTabDialogClient.Tab tabTargets;
//    private GWindowTabDialogClient.Tab tabMessage;
    private GWindowLabel               wTriggerType;
    private GWindowCheckBox            wHasTimeout;
    private GWindowEditControl         wTimeoutH;
    private GWindowEditControl         wTimeoutM;
    private GWindowHSliderInt          wRadius;
//    private GWindowLabel               wRadiusLabel;
    private GWindowEditControl         wRadiusEdit;
    private GWindowComboControl        wTriggeredByArmy;
//    private GWindowButton              wTriggerTargetSet;
//    private GWindowButton              wTriggerTargetClear;
//    private GWindowLabel               wTriggerTargetLabel;
//    private GWindowLabel               wTriggerTargetTitle;
    private GWindowEditControl     wAltitudeMin;
    private GWindowEditControl     wAltitudeMax;
    private GWindowComboControl    wTriggeredByObjectType;
    private GWindowCheckBox        wTriggerOnExit;
    private GWindowEditControl     wNoObjectsMin;
    private GWindowLabel           wDeltaAltitudeLabel;
    private GWindowEditControl     wDeltaAltitude;
    private GWindowEditControl     wProbability;
    private GWindowLabel           wTriggerLinkLabel;
    private GWindowEditTextControl wDisplayMessage;
    private GWindowEditControl     wDisplayTime;

    private Table                  lstTriggerTarget;
    private GWindowButton          wTriggerTargetListAdd;
    private GWindowButton          wTriggerTargetListDelete;
    private GWindowButton          wTriggerTargetListClear;

    static {
        Property.set(PlMisTrigger.class, "name", "MisTrigger");
    }
}
