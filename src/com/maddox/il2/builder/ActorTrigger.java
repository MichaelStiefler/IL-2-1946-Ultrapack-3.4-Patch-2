// TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
// This class is completely new to Triggers
// Rewritten and refactored by SAS~Storebror

package com.maddox.il2.builder;

import java.util.ArrayList;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Trigger;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorPosMove;
import com.maddox.il2.engine.IconDraw;
import com.maddox.il2.objects.ActorAlign;
import com.maddox.il2.objects.buildings.House;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.il2.objects.trains.Wagon;
import com.maddox.il2.objects.vehicles.aeronautics.AeroanchoredGeneric;
import com.maddox.il2.objects.vehicles.artillery.ArtilleryGeneric;
import com.maddox.il2.objects.vehicles.cars.CarGeneric;
import com.maddox.il2.objects.vehicles.lights.SearchlightGeneric;
import com.maddox.il2.objects.vehicles.planes.PlaneGeneric;
import com.maddox.il2.objects.vehicles.stationary.SirenGeneric;
import com.maddox.il2.objects.vehicles.stationary.SmokeGeneric;
import com.maddox.il2.objects.vehicles.stationary.StationaryGeneric;
import com.maddox.il2.objects.vehicles.tanks.TankGeneric;
import com.maddox.rts.Message;

public class ActorTrigger extends Actor implements ActorAlign {

//    public Actor getTriggerActor() {
//        return this.triggerActor;
//    }
//
//    public void setTriggerActor(Actor actor) {
//        if (actor != null && this.getType() != Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE && (actor.getOwner() instanceof PathAir || actor.getOwner() instanceof PathChief || actor instanceof PathAir || actor instanceof PathChief || actor instanceof PlMisRocket.Rocket)) this.triggerActor = actor;
//        else if (actor != null && this.getType() == Trigger.TYPE_SPAWN
//                && (actor instanceof ShipGeneric || actor instanceof BigshipGeneric || actor instanceof ArtilleryGeneric || actor instanceof CarGeneric || actor instanceof AeroanchoredGeneric || actor instanceof Wagon
//                        || actor instanceof SearchlightGeneric || actor instanceof PlaneGeneric || actor instanceof SirenGeneric || actor instanceof SmokeGeneric || actor instanceof StationaryGeneric || actor instanceof TankGeneric
//                        || actor instanceof House /* || (actor instanceof RadarGeneric) */))
//            this.triggerActor = actor;
//        else if (actor != null && this.getType() == Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE && (actor.getOwner() instanceof PathAir || actor instanceof PathAir) ) this.triggerActor = actor;
//        else if (actor != null && this.getType() == Trigger.TYPE_ACTIVATE && actor instanceof ActorTrigger) {
//            this.triggerActor = actor;
//            ((ActorTrigger)actor).setActivated(false);
//        }
//        else this.triggerActor = null;
//    }

    public ArrayList getTriggerActors() {
        return this.triggerActors;
    }

    public Actor addTriggerActor(Actor actor) {
        boolean actorAdded = false;
        if (actor != null && this.getType() != Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE &&
                (actor.getOwner() instanceof PathAir || actor.getOwner() instanceof PathChief
                        || actor instanceof PathAir || actor instanceof PathChief
                        || actor instanceof PlMisRocket.Rocket))
            actorAdded = this.triggerActors.add(actor);
        else if (actor != null && this.getType() == Trigger.TYPE_SPAWN
                && (actor instanceof ShipGeneric || actor instanceof BigshipGeneric || actor instanceof ArtilleryGeneric
                        || actor instanceof CarGeneric || actor instanceof AeroanchoredGeneric || actor instanceof Wagon
                        || actor instanceof SearchlightGeneric || actor instanceof PlaneGeneric || actor instanceof SirenGeneric
                        || actor instanceof SmokeGeneric || actor instanceof StationaryGeneric || actor instanceof TankGeneric
                        || actor instanceof House /* || (actor instanceof RadarGeneric) */))
            actorAdded = this.triggerActors.add(actor);
        else if (actor != null && this.getType() == Trigger.TYPE_SPAWN_AIRCRAFT_RELATIVE_ALTITUDE
                && (actor.getOwner() instanceof PathAir || actor instanceof PathAir) ) actorAdded = this.triggerActors.add(actor);
        else if (actor != null && this.getType() == Trigger.TYPE_ACTIVATE && actor instanceof ActorTrigger) {
            actorAdded = this.triggerActors.add(actor);
            ((ActorTrigger)actor).setActivated(false);
        }
//        else this.triggerActor = null;
        return (actorAdded?actor:null);
    }

    public Actor getLinkActor() {
        return this.linkActor;
    }

    public void setLinkActor(Actor actor) {
        if (actor != null && (actor.getOwner() instanceof PathAir || actor.getOwner() instanceof PathChief || actor instanceof PathAir || actor instanceof PathChief || actor instanceof PlMisRocket.Rocket || actor instanceof ShipGeneric || actor instanceof BigshipGeneric || actor instanceof ArtilleryGeneric
                || actor instanceof CarGeneric || actor instanceof AeroanchoredGeneric || actor instanceof Wagon || actor instanceof SearchlightGeneric || actor instanceof PlaneGeneric || actor instanceof StationaryGeneric || actor instanceof TankGeneric
                || actor instanceof House/* || (actor instanceof RadarGeneric) */))
            this.linkActor = actor;
        else this.linkActor = null;
    }

    public void align() {
        this.alignPosToLand(0.0D, true);
    }

    public Object getSwitchListener(Message message) {
        return this;
    }

    public ActorTrigger(Point3d triggerPoint, int triggerType, int triggeredByArmy, boolean hasTimeout, int timeoutMilliseconds, int radius, int altitudeMin, int altitudeMax, int triggeredBy, boolean triggerOnExit, int noObjectsMin, int probability,
            int deltaAltitude, String displayMessage, float displayTime) {
//        this.setTriggerActor(null);
        this.triggerActors = new ArrayList();
        this.setLinkActor(null);
        this.flags |= 0x2000;
        this.pos = new ActorPosMove(this);
        this.pos.setAbs(triggerPoint);
        this.setType(triggerType);
        this.setTriggeredByArmy(triggeredByArmy);
        this.setHasTimeout(hasTimeout);
        this.setTimeout(timeoutMilliseconds);
        this.setRadius(radius);
        this.setAltitudeMin(altitudeMin);
        this.setAltitudeMax(altitudeMax);
        this.setTriggeredBy(triggeredBy);
        this.setTriggerOnExit(triggerOnExit);
        this.setNoObjectsMin(noObjectsMin);
        this.setProbability(probability);
        this.setDeltaAltitude(deltaAltitude);
        this.setDisplayMessage(displayMessage);
        this.setDisplayTime(displayTime);
        this.setActivated(true);
        this.align();
        this.drawing(true);
        this.icon = IconDraw.get("icons/tdestroyair.mat");
    }

//    public void addActor(String s) {
//        if (s != null && s != "") {
//            this.setTriggerActor(Actor.getByName(s));
//            if (this.getTriggerActor() == null) throw new RuntimeException("trigger (" + s + ") NOT found");
//            if (this.getTriggerActor() instanceof PathAir) this.setTriggerActor(((Path) this.getTriggerActor()).point(0));
//            else if (this.getTriggerActor() instanceof PathChief) this.setTriggerActor(((Path) this.getTriggerActor()).point(0));
//            else if (!(this.getTriggerActor() instanceof ShipGeneric) && !(this.getTriggerActor() instanceof BigshipGeneric) && !(this.getTriggerActor() instanceof ArtilleryGeneric) && !(this.getTriggerActor() instanceof CarGeneric)
//                    && !(this.getTriggerActor() instanceof AeroanchoredGeneric) && !(this.getTriggerActor() instanceof Wagon) && !(this.getTriggerActor() instanceof SearchlightGeneric) && !(this.getTriggerActor() instanceof PlaneGeneric)
//                    && !(this.getTriggerActor() instanceof SirenGeneric) && !(this.getTriggerActor() instanceof SmokeGeneric) && !(this.getTriggerActor() instanceof StationaryGeneric) && !(this.getTriggerActor() instanceof TankGeneric)
//                    && !(this.getTriggerActor() instanceof House)/* && !(trigger instanceof RadarGeneric) */ && !(this.getTriggerActor() instanceof PlMisRocket.Rocket) && !(this.getTriggerActor() instanceof ActorTrigger))
//                throw new RuntimeException("target (" + s + ") NOT found");
//        } else this.setTriggerActor(null);
//    }

    public void addActor(String s) {
        if (s != null && s != "") {
            Actor addedTriggerActor = this.addTriggerActor(Actor.getByName(s));
            if (addedTriggerActor == null) throw new RuntimeException("trigger (" + s + ") NOT found");
            if (addedTriggerActor instanceof PathAir || addedTriggerActor instanceof PathChief)
                this.getTriggerActors().set(this.getTriggerActors().indexOf(addedTriggerActor), ((Path)addedTriggerActor).point(0));
            else if (!(addedTriggerActor instanceof ShipGeneric) && !(addedTriggerActor instanceof BigshipGeneric)
                    && !(addedTriggerActor instanceof ArtilleryGeneric) && !(addedTriggerActor instanceof CarGeneric)
                    && !(addedTriggerActor instanceof AeroanchoredGeneric) && !(addedTriggerActor instanceof Wagon)
                    && !(addedTriggerActor instanceof SearchlightGeneric) && !(addedTriggerActor instanceof PlaneGeneric)
                    && !(addedTriggerActor instanceof SirenGeneric) && !(addedTriggerActor instanceof SmokeGeneric)
                    && !(addedTriggerActor instanceof StationaryGeneric) && !(addedTriggerActor instanceof TankGeneric)
                    && !(addedTriggerActor instanceof House)/* && !(trigger instanceof RadarGeneric) */
                    && !(addedTriggerActor instanceof PlMisRocket.Rocket) && !(addedTriggerActor instanceof ActorTrigger))
                throw new RuntimeException("target (" + s + ") NOT found");
        }
    }

    public void addLinkActor(String s) {
        if (s != null && s != "") {
            this.setLinkActor(Actor.getByName(s));
            if (this.getLinkActor() == null) throw new RuntimeException("link (" + s + ") NOT found");
            if (this.getLinkActor() instanceof PathAir) this.setLinkActor(((Path) this.getLinkActor()).point(0));
            else if (this.getLinkActor() instanceof PathChief) this.setLinkActor(((Path) this.getLinkActor()).point(0));
            else if (!(this.getLinkActor() instanceof ShipGeneric) && !(this.getLinkActor() instanceof BigshipGeneric) && !(this.getLinkActor() instanceof ArtilleryGeneric) && !(this.getLinkActor() instanceof CarGeneric)
                    && !(this.getLinkActor() instanceof AeroanchoredGeneric) && !(this.getLinkActor() instanceof Wagon) && !(this.getLinkActor() instanceof SearchlightGeneric) && !(this.getLinkActor() instanceof PlaneGeneric)
                    && !(this.getLinkActor() instanceof SirenGeneric) && !(this.getLinkActor() instanceof SmokeGeneric) && !(this.getLinkActor() instanceof StationaryGeneric) && !(this.getLinkActor() instanceof TankGeneric)
                    && !(this.getLinkActor() instanceof House)/* && !(link instanceof RadarGeneric) */ && !(this.getLinkActor() instanceof PlMisRocket.Rocket))
                throw new RuntimeException("link (" + s + ") NOT found");
        } else this.setLinkActor(null);
    }

    protected void createActorHashCode() {
        this.makeActorRealHashCode();
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isHasTimeout() {
        return this.hasTimeout;
    }

    public void setHasTimeout(boolean hasTimeout) {
        this.hasTimeout = hasTimeout;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getTriggeredByArmy() {
        return this.triggeredByArmy;
    }

    public void setTriggeredByArmy(int triggeredByArmy) {
        this.triggeredByArmy = triggeredByArmy;
    }

    public int getAltitudeMin() {
        return this.altitudeMin;
    }

    public void setAltitudeMin(int altitudeMin) {
        this.altitudeMin = altitudeMin;
    }

    public int getAltitudeMax() {
        return this.altitudeMax;
    }

    public void setAltitudeMax(int altitudeMax) {
        this.altitudeMax = altitudeMax;
    }

    public int getTriggeredBy() {
        return this.triggeredBy;
    }

    public void setTriggeredBy(int triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public boolean isTriggerOnExit() {
        return this.triggerOnExit;
    }

    public void setTriggerOnExit(boolean triggerOnExit) {
        this.triggerOnExit = triggerOnExit;
    }

    public int getNoObjectsMin() {
        return this.noObjectsMin;
    }

    public void setNoObjectsMin(int noObjectsMin) {
        this.noObjectsMin = noObjectsMin;
    }

    public int getDeltaAltitude() {
        return this.deltaAltitude;
    }

    public void setDeltaAltitude(int deltaAltitude) {
        this.deltaAltitude = deltaAltitude;
    }

    public int getProbability() {
        return this.probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public String getDisplayMessage() {
        return this.displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public float getDisplayTime() {
        return this.displayTime;
    }

    public void setDisplayTime(float displayTime) {
        this.displayTime = displayTime;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    private int             type;
//    private Actor           triggerActor;
    private ArrayList       triggerActors;
    private Actor           linkActor;
    private int             timeout;
    private boolean         hasTimeout;
    private int             radius;
    private int             triggeredByArmy;
    private int             altitudeMin;
    private int             altitudeMax;
    private int             triggeredBy;
    private boolean         triggerOnExit;
    private int             noObjectsMin;
    private int             deltaAltitude;
    private int             probability;
    private String          displayMessage;
    private float           displayTime;
    private boolean         activated;

//    public static final int TRIGGER_TYPE_SPAWN                      = 0;
//    public static final int TRIGGER_TYPE_ACTIVATE                   = 1;
//    public static final int TRIGGER_TYPE_AIRSPAWN_RELATIVE_ALTITUDE = 2;
//    public static final int TRIGGER_TYPE_MESSAGE                    = 3;

    public static final int TRIGGERED_BY_ALL                        = 0;
    public static final int TRIGGERED_BY_AI_AIRCRAFT_ONLY           = 1;
    public static final int TRIGGERED_BY_HUMAN_AIRCRAFT_ONLY        = 2;
    public static final int TRIGGERED_BY_GROUND_OBJECTS_ONLY        = 3;
    public static final int TRIGGERED_BY_MOVING_AIRCRAFTS_ONLY      = 4;
    public static final int TRIGGERED_BY_STATIC_AIRCRAFTS_ONLY      = 5;
    public static final int TRIGGERED_BY_ARMOUR_ONLY                = 6;
    public static final int TRIGGERED_BY_ARTILLERY_ONLY             = 7;
    public static final int TRIGGERED_BY_INFANTRY_ONLY              = 8;
    public static final int TRIGGERED_BY_SHIPS_ONLY                 = 9;
    public static final int TRIGGERED_BY_TRAINS_ONLY                = 10;
    public static final int TRIGGERED_BY_VEHICLES_ONLY              = 11;
}
