package org.json.suite;

import org.json.JSONObject;
import org.json.JSONString;

/**
 *  Obj is a typical class that implements JSONString. It also
 *  provides some beanie methods that can be used to
 *  construct a JSONObject. It also demonstrates constructing
 *  a JSONObject with an array of names.
 */
public class Obj implements JSONString {
    public String aString;
    public double aNumber;
    public boolean aBoolean;

    public Obj(String string, double n, boolean b) {
        this.aString = string;
        this.aNumber = n;
        this.aBoolean = b;
    }

    public double getNumber() {
        return this.aNumber;
    }

    public String getString() {
        return this.aString;
    }

    public boolean isBoolean() {
        return this.aBoolean;
    }

    public String getBENT() {
        return "All uppercase key";
    }

    public String getX() {
        return "x";
    }

    public String toJSONString() {
        return "{" + JSONObject.quote(this.aString) + ":" +
            JSONObject.doubleToString(this.aNumber) + "}";
    }
    @Override
    public String toString() {
        return this.getString() + " " + this.getNumber() + " " +
            this.isBoolean() + "." + this.getBENT() + " " + this.getX();
    }
}
