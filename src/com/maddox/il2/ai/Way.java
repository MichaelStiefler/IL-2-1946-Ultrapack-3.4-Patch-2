/*4.10.1 class*/
package com.maddox.il2.ai;

import java.util.ArrayList;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.engine.Orient;
import com.maddox.rts.SectFile;
import com.maddox.util.NumberTokenizer;

public class Way {
    private ArrayList       WList;
    private int             Cur;
    private boolean         landing;
    private boolean         landingOnShip;
    public Airport          landingAirport;
    public Airport          takeoffAirport;
    private double          prevdist2;
    private double          prevdistToNextWP2;
    private double          curDist;
    private static Vector3d V = new Vector3d();

    // TODO: Modified by |ZUTI|: Changed from private to public
    public static Point3d   P         = new Point3d();

    private static Point3d  tmpP      = new Point3d();
    private static WayPoint defaultWP = new WayPoint();

    public int Cur() {
        return this.Cur;
    }

    public Way() {
        this.WList = new ArrayList();
        this.prevdist2 = 1.0E8;
        this.prevdistToNextWP2 = 3.4028234663852886E38;
        this.WList.clear();
        this.Cur = 0;
        this.landing = false;
        this.landingOnShip = false;
        this.landingAirport = null;
        this.takeoffAirport = null;
    }

    public Way(Way way_0_) {
        this.WList = new ArrayList();
        this.prevdist2 = 1.0E8;
        this.prevdistToNextWP2 = 3.4028234663852886E38;
        this.set(way_0_);
    }

    public void set(Way way_1_) {
        this.WList.clear();
        this.Cur = 0;
        for (int i = 0; i < way_1_.WList.size(); i++) {
            WayPoint waypoint = new WayPoint();
            waypoint.set(way_1_.get(i));
            this.WList.add(waypoint);
            if (waypoint.Action == 2 && waypoint.sTarget != null) this.landingOnShip = true;
        }
        this.landing = way_1_.landing;
        this.landingAirport = way_1_.landingAirport;
        if (this.takeoffAirport == null) this.takeoffAirport = way_1_.takeoffAirport;
    }

    public WayPoint first() {
        this.Cur = 0;
        return this.curr();
    }

    public WayPoint last() {
        this.Cur = Math.max(0, this.WList.size() - 1);
        return this.curr();
    }

    public WayPoint next() {
        int i = this.WList.size();
        this.Cur++;
        if (this.Cur >= i) {
            this.Cur = Math.max(0, i - 1);
            WayPoint waypoint = this.curr();
            return waypoint;
        }
        return this.curr();
    }

    public WayPoint look_at_point(int i) {
        int i_2_ = this.WList.size();
        if (i_2_ == 0) return defaultWP;
        if (i < 0) i = 0;
        if (i > i_2_ - 1) i = i_2_ - 1;
        return (WayPoint) this.WList.get(i);
    }

    public void setCur(int i) {
        if (i < this.WList.size() && i >= 0) this.Cur = i;
    }

    public WayPoint prev() {
        this.Cur--;
        if (this.Cur < 0) this.Cur = 0;
        return this.curr();
    }

    public WayPoint curr() {
        if (this.WList.size() == 0) return defaultWP;
        return (WayPoint) this.WList.get(this.Cur);
    }

    public WayPoint auto(Point3d point3d) {
        if (this.Cur == 0 || this.isReached(point3d)) return this.next();
        return this.curr();
    }

    public double getCurDist() {
        return Math.sqrt(this.curDist);
    }

    public boolean isReached(Point3d point3d) {
        this.curr().getP(P);
        V.sub(point3d, P);
        if (this.curr().timeout == -1 && !this.isLast()) {
            ((WayPoint) this.WList.get(this.Cur + 1)).getP(tmpP);
            V.sub(point3d, tmpP);
            this.curDist = V.x * V.x + V.y * V.y;
            if (this.curDist < 1.0E8 && this.curDist > this.prevdistToNextWP2) {
                this.curr().setTimeout(0);
                this.prevdistToNextWP2 = 3.4028234663852886E38;
                return true;
            }
            this.prevdistToNextWP2 = this.curDist;
        } else {
            this.curDist = V.x * V.x + V.y * V.y;
            if (this.curDist < 1000000.0 && this.curDist > this.prevdist2) {
                this.prevdist2 = 1.0E8;
                return true;
            }
            this.prevdist2 = this.curDist;
        }
        return false;
    }

    public boolean isLanding() {
        return this.landing;
    }

    public boolean isLandingOnShip() {
        return this.landingOnShip;
    }

    public boolean isLast() {
        return this.Cur == this.WList.size() - 1;
    }

    public void setLanding(boolean bool) {
        this.landing = bool;
    }

    public void add(WayPoint waypoint) {
        this.WList.add(waypoint);
        if (waypoint.Action == 2 && waypoint.sTarget != null) this.landingOnShip = true;
    }

    public WayPoint get(int i) {
        if (i < 0 || i >= this.WList.size()) return null;
        return (WayPoint) this.WList.get(i);
    }

    public void insert(int i, WayPoint waypoint) {
        if (i < 0) i = 0;
        else if (i > this.WList.size()) {
            this.add(waypoint);
            return;
        }
        this.WList.add(i, waypoint);
    }

    public int size() {
        return this.WList.size();
    }

    public void load(SectFile sectfile, String string) throws Exception {
        int i = sectfile.sectionIndex(string);
        int j = sectfile.vars(i);
        // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
        double altiTrigger = -1D;
        double diffAltiFist = -999999D;
        if(World.cur().triggersGuard.getListTriggerAircraftAirSpawnRelativeAltitude().contains(string.substring(0, string.length() - 4)))
            altiTrigger = World.cur().triggersGuard.getTriggerTarget(string.substring(0, string.length() - 4)).getAltitude();
        // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
        for (int k = 0; k < j; k++) {
            String s1 = sectfile.var(i, k);
            WayPoint waypoint = new WayPoint();

            // TODO: +++ TD AI code backport from 4.13 +++

//			if (s1.equalsIgnoreCase("TAKEOFF"))
//				waypoint.Action = 1;
//			else if (s1.equalsIgnoreCase("LANDING"))
//				waypoint.Action = 2;
//			else if (s1.equalsIgnoreCase("GATTACK"))
//				waypoint.Action = 3;
//			else
//				waypoint.Action = 0;

            s1 = s1.toUpperCase();
            waypoint.resetWayPointExtra();
            if (s1.startsWith("NORMFLY")) {
                waypoint.Action = 0;
                if (s1.endsWith("_401")) waypoint.waypointType = 401;
                else if (s1.endsWith("_402")) waypoint.waypointType = 402;
                else if (s1.endsWith("_403")) waypoint.waypointType = 403;
                else if (s1.endsWith("_404")) waypoint.waypointType = 404;
                else if (s1.endsWith("_405")) waypoint.waypointType = 405;
                else if (s1.endsWith("_407")) waypoint.waypointType = 407;
            } else if (s1.startsWith("TAKEOFF")) {
                waypoint.Action = 1;
                if (s1.endsWith("_001")) waypoint.waypointType = 1;
                else if (s1.endsWith("_002")) waypoint.waypointType = 2;
                else if (s1.endsWith("_003")) waypoint.waypointType = 3;
                else if (s1.endsWith("_004")) waypoint.waypointType = 4;
                else if (s1.endsWith("_005")) waypoint.waypointType = 5;
            } else if (s1.startsWith("LANDING")) {
                waypoint.Action = 2;
                if (s1.endsWith("_101")) waypoint.waypointType = 101;
                else if (s1.endsWith("_102")) waypoint.waypointType = 102;
                else if (s1.endsWith("_103")) waypoint.waypointType = 103;
                else if (s1.endsWith("_104")) waypoint.waypointType = 104;
            } else if (s1.startsWith("GATTACK")) waypoint.Action = 3;

            // TODO: --- TD AI code backport from 4.13 ---

            NumberTokenizer numbertokenizer = new NumberTokenizer(sectfile.value(i, k));
            P.x = numbertokenizer.next(0.0F, -1000000.0F, 1000000.0F);
            P.y = numbertokenizer.next(0.0F, -1000000.0F, 1000000.0F);
            P.z = numbertokenizer.next(0.0F, -1000000.0F, 1000000.0F) + World.land().HQ(P.x, P.y);
            // TODO: +++ Trigger backport from HSFX 7.0.3 by SAS~Storebror +++
            if(altiTrigger >= 0.0D && (waypoint.Action != 1 || waypoint.Action != 2))
            {
                if(diffAltiFist == -999999D)
                    diffAltiFist = altiTrigger - P.z;
                P.z += diffAltiFist;
                if(P.z - World.land().HQ(P.x, P.y) < 100D)
                    P.z = World.land().HQ(P.x, P.y) + 100D;
            }
            // TODO: --- Trigger backport from HSFX 7.0.3 by SAS~Storebror ---
            float f = numbertokenizer.next(0.0F, 0.0F, 2800.0F);
            waypoint.set(P);
            waypoint.set(f / 3.6F);
            String s2 = numbertokenizer.next((String) null);
            if (s2 != null) if (s2.equals("&0")) {
                waypoint.bRadioSilence = false;
                s2 = null;
            } else if (s2.equals("&1")) {
                waypoint.bRadioSilence = true;
                s2 = null;
            } else {
                numbertokenizer.next(0);
                String s3 = numbertokenizer.next((String) null);
                if (s3 != null && s3.equals("&1")) waypoint.bRadioSilence = true;
            }
            String s4 = numbertokenizer.next(null);
            if (s4 != null && s4.startsWith("F")) {
                String s5 = s4.substring(1);
                waypoint.formation = Integer.parseInt(s5);
//                System.out.println("#### formation=" + waypoint.formation);
            }
            if (s2 != null && s2.startsWith("Bridge")) s2 = " " + s2;
            waypoint.setTarget(s2);
            // TODO: +++ TD AI code backport from 4.13 +++
            if (waypoint.waypointType > 0 && waypoint.Action != 2) {
                String s6 = sectfile.value(i, k + 1);
                NumberTokenizer numbertokenizer1 = new NumberTokenizer(s6);
                switch (waypoint.waypointType) {
                    case 401:
                    case 402:
                    case 403:
                    case 404:
                    case 405:
                        waypoint.cycles = numbertokenizer1.next(0);
                        waypoint.delayTimer = numbertokenizer1.next(0);
                        waypoint.orient = numbertokenizer1.next(0);
                        waypoint.baseSize = numbertokenizer1.next(0);
                        waypoint.altDifference = numbertokenizer1.next(0);
                        break;

                    case 407:
                        waypoint.cycles = numbertokenizer1.next(0);
                        waypoint.delayTimer = numbertokenizer1.next(0);
                        waypoint.orient = numbertokenizer1.next(0);
                        waypoint.baseSize = numbertokenizer1.next(0);
                        break;

                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        waypoint.targetTrigger = numbertokenizer1.next(0);
                        waypoint.delayTimer = numbertokenizer1.next(0);
                        waypoint.takeoffSpacing = numbertokenizer1.next(0);
                        waypoint.ignoreAlt = numbertokenizer1.next(0);
                        break;
                }
                k++;
            }
            // TODO: --- TD AI code backport from 4.13 ---
            this.add(waypoint);
        }
    }

    // TODO: +++ TD AI code backport from 4.13 +++
    public void setNearestTaxingPoint(Point3d point3d, Orient orient) {
        double d = Double.MAX_VALUE;
        int i = 0;
        for (int j = this.Cur; j < this.WList.size(); j++) {
            WayPoint waypoint = (WayPoint) this.WList.get(j);
            if (waypoint.waypointType != 4 && waypoint.waypointType != 5) break;
            waypoint.getP(tmpP);
            V.sub(tmpP, point3d);
            double d1 = V.x * V.x + V.y * V.y;
            if (d1 < d) {
                i = j;
                d = d1;
            }
        }

        WayPoint waypoint1 = (WayPoint) this.WList.get(i);
        waypoint1.getP(tmpP);
        V.sub(tmpP, point3d);
        orient.transformInv(V);
        V.normalize();
        if (V.x < -0.5D && i < this.WList.size() - 1) {
            WayPoint waypoint2 = (WayPoint) this.WList.get(i + 1);
            if (waypoint2.waypointType == 4 || waypoint2.waypointType == 5) i++;
        }
        this.Cur = i;
    }
    // TODO: --- TD AI code backport from 4.13 ---

}