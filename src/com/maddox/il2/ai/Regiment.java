package com.maddox.il2.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyResourceBundle;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.game.I18N;
import com.maddox.rts.Message;
import com.maddox.rts.SFSInputStream;
import com.maddox.rts.SectFile;
import com.maddox.util.HashMapExt;

public class Regiment extends Actor
{

    public String country()
    {
        return country;
    }

    public String branch()
    {
        return branch;
    }

    public String fileName()
    {
        return "PaintSchemes/" + shortFileName;
    }

    public String fileNameTga()
    {
        return "../" + shortFileName + ".tga";
    }

    public String id()
    {
        return sid;
    }

    public char[] aid()
    {
        return id;
    }

    public int gruppeNumber()
    {
        return gruppeNumber;
    }

    public String shortInfo()
    {
        return shortInfo;
    }

    public String info()
    {
        return info != null ? info : shortInfo;
    }

    public String speech()
    {
        return speech;
    }

    public static String getCountryFromBranch(String s)
    {
        if(branchMap.containsKey(s))
            return (String)branchMap.get(s);
        else
            return s;
    }

    public boolean isUserDefined()
    {
        return false;
    }

    public static void resetGame()
    {
        int i = all.size();
        for(int j = 0; j < i; j++)
        {
            Regiment regiment = (Regiment)all.get(j);
            regiment.diedBombers = 0;
            regiment.diedAircrafts = 0;
        }

    }

    public Object getSwitchListener(Message message)
    {
        return this;
    }

    public Regiment(String s, String s1, int i)
    {
        diedBombers = 0;
        diedAircrafts = 0;
        id = new char[2];
        gruppeNumber = 1;
        flags |= 0x4000;
        setArmy(i);
        setName(s);
        shortFileName = s1;
        try
        {
            PropertyResourceBundle propertyresourcebundle = new PropertyResourceBundle(new SFSInputStream(fileName()));
            country = propertyresourcebundle.getString("country");
            country = country.toLowerCase().intern();
            branch = country;
            if(branchMap.containsKey(country))
                country = (String)branchMap.get(branch);
            String s2 = propertyresourcebundle.getString("id");
            id[0] = s2.charAt(0);
            id[1] = s2.charAt(1);
            if((id[0] < '0' || id[0] > '9') && (id[0] < 'A' || id[0] > 'Z') && id[0] != '_')
                throw new RuntimeException("Bad regiment id[0]");
            if((id[1] < '0' || id[1] > '9') && (id[1] < 'A' || id[1] > 'Z') && id[1] != '_')
                throw new RuntimeException("Bad regiment id[1]");
            sid = new String(id);
            speech = country;
            try
            {
                speech = propertyresourcebundle.getString("speech");
                speech = speech.toLowerCase().intern();
            }
            catch(Exception exception1) { }
            shortInfo = I18N.regimentShort(s);
            info = I18N.regimentInfo(s);
            try
            {
                String s3 = propertyresourcebundle.getString("gruppeNumber");
                if(s3 != null)
                {
                    try
                    {
                        gruppeNumber = Integer.parseInt(s3);
                    }
                    catch(Exception exception3) { }
                    if(gruppeNumber < 1)
                        gruppeNumber = 1;
                    if(gruppeNumber > 5)
                        gruppeNumber = 5;
                }
            }
            catch(Exception exception2) { }
        }
        catch(Exception exception)
        {
            System.out.println("Regiment load failed: " + exception.getMessage());
            exception.printStackTrace();
            destroy();
            return;
        }
        all.add(this);
    }

    public Regiment(String s, int i)
    {
        this(s, makeShortFileName(s, i), i);
    }

    private static String makeShortFileName(String s, int i)
    {
        if(i < Army.amountSingle())
            return Army.name(i) + "/" + s;
        else
            return s;
    }

    protected Regiment()
    {
        diedBombers = 0;
        diedAircrafts = 0;
        id = new char[2];
        gruppeNumber = 1;
    }

    protected void createActorHashCode()
    {
        makeActorRealHashCode();
    }

    public static List getAll()
    {
        return all;
    }
    
    public static Regiment findFirst(String s, int i) {
        return findFirst(s, null, i);
    }

    public static Regiment findFirst(String s, String s1, int i)
    {
        String s2 = null;
        Regiment regiment = null;
        if(s != null)
        {
//            s2 = s + "_" + s1 + "_" + i;
            s2 = s + "_" + (s1==null?"":s1 + "_") + i;
            regiment = (Regiment)firstMap.get(s2);
            if(regiment != null)
                return regiment;
            int j = 0;
            do
            {
                if(j >= all.size())
                    break;
                Regiment regiment1 = (Regiment)all.get(j);
                if(s.equals(regiment1.country()) && (s1 == null || s1.equals(regiment1.branch())) && i == regiment1.getArmy())
                {
                    regiment = regiment1;
                    break;
                }
                j++;
            } while(true);
        }
        if(regiment == null)
        {
            String s3 = "NoNe";
            switch(i)
            {
            case 1: // '\001'
                s3 = "r01";
                break;

            case 2: // '\002'
                s3 = "g01";
                break;
            }
            regiment = (Regiment)Actor.getByName(s3);
        }
//        if(regiment != null && s != null && s1 != null)
        if(regiment != null && s != null)
            firstMap.put(s2, regiment);
        return regiment;
    }

    public static void loadAll()
    {
        SectFile sectfile = new SectFile("PaintSchemes/regiments.ini", 0);
        int i = sectfile.sectionIndex("branch");
        if(i >= 0)
        {
            int j = sectfile.vars(i);
            for(int l = 0; l < j; l++)
            {
                String s = sectfile.var(i, l);
                String s1 = sectfile.value(i, l);
                if(s != null && s1 != null)
                    branchMap.put(s.toLowerCase().intern(), s1.toLowerCase().intern());
            }

        }
        for(int k = 1; k < Army.amountSingle(); k++)
            loadSection(sectfile, Army.name(k), k);

        loadSection(sectfile, "NoNe", 0);
    }

    private static void loadSection(SectFile sectfile, String s, int i)
    {
        int j = sectfile.sectionIndex(s);
        if(j < 0)
            return;
        int k = sectfile.vars(j);
        for(int l = 0; l < k; l++)
            new Regiment(sectfile.var(j, l), i);

    }

    public static final String prefixPath = "PaintSchemes/";
    public int diedBombers;
    public int diedAircrafts;
    protected String country;
    protected String branch;
    protected String shortFileName;
    protected char id[];
    protected String sid;
    protected String shortInfo;
    protected String info;
    protected int gruppeNumber;
    protected String speech;
    private static ArrayList all = new ArrayList();
    protected static HashMapExt branchMap = new HashMapExt();
    private static HashMap firstMap = new HashMap();

}
