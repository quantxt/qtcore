package com.quantxt.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by matin on 3/31/17.
 */
public class NamedEntity {

    final public static String ENTITY = "Entity";

    @JsonIgnore
    private transient Entity entity;
    private String name;
    private String type;
    private boolean isParent = false;
    private TreeSet<String> alts;

    public NamedEntity(String n , List<String> p){
        name = n;
        if (p != null) {
            alts = new TreeSet<>();
            alts.addAll(p);
        }
    }

    public void setEntity(Entity e){
        type = ENTITY;
        entity = e;
    }

    public void setEntity(String t, Entity e){
        type = t;
        entity = e;
    }

    public void addAlts(Collection<String> newAlts){
        if (alts == null){
            alts = new TreeSet<>();
        }
        alts.addAll(newAlts);
    }

    public Entity getEntity(){return entity;}
    public String getType(){return type;}
    public String getName(){return name;}
    public Set<String> getAlts(){
        return alts;
    }
    public boolean isParent(){
        return isParent;
    }

    public void setParent(boolean s){
        isParent = s;
    }
}
