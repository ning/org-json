package org.json.suite;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.json.CDL;
import org.json.Cookie;
import org.json.CookieList;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONWriter;
import org.json.XML;
import org.json.test.JsonAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class. This file is not formally a member of the org.json library.
 * It is just a casual test tool.
 */
@Test
public class TestSuite
{

    @BeforeMethod(alwaysRun=true)
    public void setUp()
    {
        XMLUnit.setNormalize(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
    }

    public void testDoubleArray() throws Exception
    {
        final String s = "[0.1]";
        final JSONArray a = new JSONArray(s);
        Assert.assertEquals(a.toString(), "[0.1]");
        Assert.assertEquals(0.1, a.getDouble(0), 0.00001);
    }

    public void testCDATAContent() throws Exception
    {
        final JSONObject j = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ");
        Assert.assertTrue(j.has("content"));
        Assert.assertEquals(j.toString(), "{\"content\":\"This is a collection of test patterns and examples for org.json.\"}");
    }

    public void testNullHandling() throws Exception
    {
        final JSONObject j = new JSONObject();
        final Object o = null;
        j.put("booga", o);
        j.put("wooga", JSONObject.NULL);
        Assert.assertFalse(j.has("booga"));
        Assert.assertTrue(j.has("wooga"));
        Assert.assertEquals(j.toString(), "{\"wooga\":null}");
    }

    public void testIncrement() throws Exception
    {
        final JSONObject j = new JSONObject();
        j.increment("two");
        j.increment("two");

        Assert.assertTrue(j.has("two"));
        Assert.assertEquals(j.getInt("two"), 2);
        Assert.assertEquals(j.toString(), "{\"two\":2}");
    }

    public void testXMLRoundtrip() throws Exception
    {
        final String s = "<test><blank></blank><empty/></test>";
        final JSONObject j = XML.toJSONObject(s);

        Assert.assertNotNull(j);
        Assert.assertTrue(j.has("test"));
        Assert.assertTrue(j.get("test") instanceof JSONObject);
        Assert.assertEquals(j.getJSONObject("test").getString("blank"), "");
        Assert.assertEquals(j.getJSONObject("test").getString("empty"), "");
        Assert.assertEquals(XML.toString(j), "<test><blank/><empty/></test>");
    }

    public void testListOfLists() throws Exception
    {
        final String s = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
        final JSONObject j = new JSONObject(s);

        Assert.assertTrue(j.has("list of lists"));
        Assert.assertTrue(j.get("list of lists") instanceof JSONArray);
        Assert.assertEquals(j.getJSONArray("list of lists").length(), 2);
        Assert.assertTrue(j.getJSONArray("list of lists").get(0) instanceof JSONArray);
        Assert.assertTrue(j.getJSONArray("list of lists").get(1) instanceof JSONArray);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(0).length(), 3);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(1).length(), 3);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(0).getInt(0), 1);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(0).getInt(1), 2);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(0).getInt(2), 3);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(1).getInt(0), 4);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(1).getInt(1), 5);
        Assert.assertEquals(j.getJSONArray("list of lists").getJSONArray(1).getInt(2), 6);

        Assert.assertEquals(XML.toString(j), "<list of lists><array>1</array><array>2</array><array>3</array></list of lists><list of lists><array>4</array><array>5</array><array>6</array></list of lists>");
    }

    public void testComplexXML() throws Exception
    {
        final String s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
        final JSONObject j = XML.toJSONObject(s);
        Assert.assertNotNull(j);
        Assert.assertTrue(j.has("recipe"));
        Assert.assertTrue(j.get("recipe") instanceof JSONObject);

        JSONObject r = j.getJSONObject("recipe");
        Assert.assertEquals(r.getString("cook_time"), "3 hours");
        Assert.assertEquals(r.getString("name"), "bread");
        Assert.assertEquals(r.getString("prep_time"), "5 mins");
        Assert.assertEquals(r.getString("title"), "Basic bread");

        Assert.assertTrue(r.has("ingredient"));
        Assert.assertTrue(r.has("instructions"));

        Assert.assertTrue(r.get("ingredient") instanceof JSONArray);
        Assert.assertTrue(r.get("instructions") instanceof JSONObject);

        JSONArray ingredient = r.getJSONArray("ingredient");

        Assert.assertEquals(ingredient.length(), 4);

        Assert.assertEquals(ingredient.getJSONObject(0).length(), 3);
        Assert.assertEquals(ingredient.getJSONObject(0).getInt("amount"), 8);
        Assert.assertEquals(ingredient.getJSONObject(0).getString("content"), "Flour");
        Assert.assertEquals(ingredient.getJSONObject(0).getString("unit"), "dL");

        Assert.assertEquals(ingredient.getJSONObject(1).length(), 3);
        Assert.assertEquals(ingredient.getJSONObject(1).getInt("amount"), 10);
        Assert.assertEquals(ingredient.getJSONObject(1).getString("content"), "Yeast");
        Assert.assertEquals(ingredient.getJSONObject(1).getString("unit"), "grams");

        Assert.assertEquals(ingredient.getJSONObject(2).length(), 4);
        Assert.assertEquals(ingredient.getJSONObject(2).getInt("amount"), 4);
        Assert.assertEquals(ingredient.getJSONObject(2).getString("content"), "Water");
        Assert.assertEquals(ingredient.getJSONObject(2).getString("state"), "warm");
        Assert.assertEquals(ingredient.getJSONObject(2).getString("unit"), "dL");

        Assert.assertEquals(ingredient.getJSONObject(3).length(), 3);
        Assert.assertEquals(ingredient.getJSONObject(3).getInt("amount"), 1);
        Assert.assertEquals(ingredient.getJSONObject(3).getString("content"), "Salt");
        Assert.assertEquals(ingredient.getJSONObject(3).getString("unit"), "teaspoon");

        Assert.assertEquals(r.getJSONObject("instructions").getJSONArray("step").length(), 7);
    }


    public void testJSONMLObject() throws Exception
    {
        final String s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
        final JSONObject j = JSONML.toJSONObject(s);

        Assert.assertNotNull(j);
        Assert.assertEquals(j.getString("tagName"), "recipe");
        Assert.assertEquals(j.getString("name"), "bread");
        Assert.assertEquals(j.getString("prep_time"), "5 mins");
        Assert.assertEquals(j.getString("cook_time"), "3 hours");

        Assert.assertTrue(j.has("childNodes"));
        Assert.assertTrue(j.get("childNodes") instanceof JSONArray);
        Assert.assertEquals(j.getJSONArray("childNodes").length(), 6);
    }

    public void testJsonObjectRoundtrip() throws Exception
    {
        final String s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
        final JSONObject j = JSONML.toJSONObject(s);
        final String xml = JSONML.toString(j);

        XMLUnit.setNormalize(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(xml, s);
    }

    public void testJsonArrayRoundtrip() throws Exception
    {
        final String s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
        final JSONArray a = JSONML.toJSONArray(s);
        final String xml = JSONML.toString(a);

        XMLAssert.assertXMLEqual(xml, s);
    }

    public void testObjectPreserveOrder() throws Exception
    {
        final String s = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
        final JSONObject j = JSONML.toJSONObject(s);

        Assert.assertTrue(j.get("childNodes") instanceof JSONArray);
        Assert.assertEquals(j.getString("class"), "JSONML");
        Assert.assertEquals(j.getString("id"), "demo");
        Assert.assertEquals(j.getString("tagName"), "div");
        Assert.assertEquals(j.getJSONArray("childNodes").length(), 3);

        JSONObject t1 = j.getJSONArray("childNodes").getJSONObject(0);
        Assert.assertTrue(t1.get("childNodes") instanceof JSONArray);
        Assert.assertEquals(t1.getJSONArray("childNodes").length(), 5);
        Assert.assertEquals(t1.getString("tagName"), "p");

        JSONObject t2 = j.getJSONArray("childNodes").getJSONObject(1);
        Assert.assertTrue(t2.get("childNodes") instanceof JSONArray);
        Assert.assertEquals(t2.getJSONArray("childNodes").length(), 1);
        Assert.assertEquals(t2.getString("tagName"), "p");

        JSONObject t3 = j.getJSONArray("childNodes").getJSONObject(2);
        Assert.assertTrue(t3.get("childNodes") instanceof JSONArray);
        Assert.assertEquals(t3.getJSONArray("childNodes").length(), 5);
        Assert.assertEquals(t3.getString("tagName"), "p");

    }

    public void testArrayPreserveOrder() throws Exception
    {
        final String s = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
        final JSONArray a = JSONML.toJSONArray(s);

        Assert.assertEquals(a.length(), 5);
        Assert.assertEquals(a.getString(0), "div");
        Assert.assertEquals(a.getJSONObject(1).getString("class"), "JSONML");
        Assert.assertEquals(a.getJSONObject(1).getString("id"), "demo");

        Assert.assertEquals(a.getJSONArray(2).length(), 6);
        Assert.assertEquals(a.getJSONArray(2).getString(0), "p");
        Assert.assertEquals(a.getJSONArray(2).getJSONArray(2).length(), 2);
        Assert.assertEquals(a.getJSONArray(2).getString(3), "and");
        Assert.assertEquals(a.getJSONArray(2).getJSONArray(4).length(), 2);

        Assert.assertEquals(a.getJSONArray(3).length(), 2);
        Assert.assertEquals(a.getJSONArray(3).getString(0), "p");

        Assert.assertEquals(a.getJSONArray(4).length(), 6);
        Assert.assertEquals(a.getJSONArray(4).getString(0), "p");
        Assert.assertEquals(a.getJSONArray(4).getString(1), "Three");
        Assert.assertEquals(a.getJSONArray(4).getJSONArray(2).length(), 1);
        Assert.assertEquals(a.getJSONArray(4).getJSONArray(2).getString(0), "br");
        Assert.assertEquals(a.getJSONArray(4).getString(3), "little");
        Assert.assertEquals(a.getJSONArray(4).getJSONArray(4).length(), 1);
        Assert.assertEquals(a.getJSONArray(4).getJSONArray(4).getString(0), "br");
        Assert.assertEquals(a.getJSONArray(4).getString(5), "words");
    }

    public void testComplexObject() throws Exception
    {
        JSONObject o = new JSONObject("{\"person\": { " +
                "\"address\": { " +
                "\"city\": \"Anytown\", " +
                "\"postalCode\": \"98765-4321\", " +
                "\"state\": \"CA\", " +
                "\"street\": \"12345 Sixth Ave\", " +
                "\"type\": \"home\" " +
                "}, " +
                "\"created\": \"2006-11-11T19:23\", " +
                "\"firstName\": \"Robert\", " +
                "\"lastName\": \"Smith\", " +
                "\"modified\": \"2006-12-31T23:59\" " +
                "}} " +
                "{\"number\":42,\"string\":\"A beany object\",\"boolean\":true,\"BENT\":\"All uppercase key\",\"x\":\"x\"} " +
                "{\"entity\": { " +
                "\"averageRating\": null, " +
                "\"id\": 12336, " +
                "\"imageURL\": \"\", " +
                "\"name\": \"IXXXXXXXXXXXXX\", " +
                "\"ratingCount\": null " +
        "}}");

        final String s = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>";
        final JSONObject j = XML.toJSONObject(s);

        JsonAssert.assertJsonEquals(o, j);

    }

    public void testJsonBean() throws Exception
    {
        final JSONObject o = new JSONObject("{\"number\":42,\"string\":\"A beany object\",\"boolean\":true,\"BENT\":\"All uppercase key\",\"x\":\"x\"}");
        final Obj obj = new Obj("A beany object", 42, true);
        final JSONObject j = new JSONObject(obj);

        JsonAssert.assertJsonEquals(o, j);
    }

    public void testStringToJson() throws Exception
    {
        final String  s = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
        final JSONObject j = new JSONObject(s);

        Assert.assertTrue(j.has("entity"));
        Assert.assertEquals(1, j.length());
        Assert.assertTrue(j.get("entity") instanceof JSONObject);
        JSONObject entity = j.getJSONObject("entity");
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.length(), 5);
        Assert.assertTrue(entity.isNull("averageRating"));
        Assert.assertEquals(entity.getInt("id"), 12336);
        Assert.assertEquals(entity.getString("imageURL"), "");
        Assert.assertEquals(entity.getString("name"), "IXXXXXXXXXXXXX");
        Assert.assertTrue(entity.isNull("ratingCount"));
    }

    public void testJSONStringerObject() throws Exception
    {
        final Obj obj = new Obj("A beany object", 42, true);
        final JSONStringer jj = new JSONStringer();

        final String s = jj
            .object()
            .key("single")
            .value("MARIE HAA'S")
            .key("Johnny")
            .value("MARIE HAA\\'S")
            .key("foo")
            .value("bar")
            .key("baz")
            .array()
            .object()
            .key("quux")
            .value("Thanks, Josh!")
            .endObject()
            .endArray()
            .key("obj keys")
            .value(JSONObject.getNames(obj))
            .endObject()
            .toString();

        final String c = "{\"single\":\"MARIE HAA'S\",\"Johnny\":\"MARIE HAA\\\\'S\",\"foo\":\"bar\",\"baz\":[{\"quux\":\"Thanks, Josh!\"}],\"obj keys\":[\"aString\",\"aNumber\",\"aBoolean\"]}";

        Assert.assertEquals(s, c);
    }

    public void testNestedObjectArray() throws Exception
    {
        final JSONObject o = new JSONObject("{\"a\":[[[\"b\"]]]}");

        final String s = new JSONStringer()
            .object()
            .key("a")
            .array()
            .array()
            .array()
            .value("b")
            .endArray()
            .endArray()
            .endArray()
            .endObject()
            .toString();

        JsonAssert.assertJsonEquals(o, new JSONObject(s));
    }

    public void testJSONStringerArray() throws Exception
    {
        final Obj obj = new Obj("A beany object", 42, true);
        final JSONStringer jj = new JSONStringer();
        jj.array();
        jj.value(1);
        jj.array();
        jj.value(null);
        jj.array();
        jj.object();
        jj.key("empty-array").array().endArray();
        jj.key("answer").value(42);
        jj.key("null").value(null);
        jj.key("false").value(false);
        jj.key("true").value(true);
        jj.key("big").value(123456789e+88);
        jj.key("small").value(123456789e-88);
        jj.key("empty-object").object().endObject();
        jj.key("long");
        jj.value(9223372036854775807L);
        jj.endObject();
        jj.value("two");
        jj.endArray();
        jj.value(true);
        jj.endArray();
        jj.value(98.6);
        jj.value(-100.0);
        jj.object();
        jj.endObject();
        jj.object();
        jj.key("one");
        jj.value(1.00);
        jj.endObject();
        jj.value(obj);
        jj.endArray();

        final JSONArray a = new JSONArray("[1,[null,[{\"empty-array\":[],\"answer\":42,\"null\":null,\"false\":false,\"true\":true,\"big\":1.23456789E96,\"small\":1.23456789E-80,\"empty-object\":{},\"long\":9223372036854775807},\"two\"],true],98.6,-100,{},{\"one\":1},{\"A beany object\":42}]");

        JsonAssert.assertJsonEquals(new JSONArray(jj.toString()), a);
    }

    public void testSmallArray() throws Exception
    {
        int ar[] = {1, 2, 3};
        JSONArray ja = new JSONArray(ar);

        Assert.assertEquals(ja.toString(), "[1,2,3]");

    }

    public void testJSONStringInterface() throws Exception
    {
        final Obj obj = new Obj("A beany object", 42, true);
        final String sa[] = {"aString", "aNumber", "aBoolean"};
        final JSONObject j = new JSONObject(obj, sa);
        j.put("Testing JSONString interface", obj);
        final String s = j.toString(4);

        final String c =
            "{\n" +
            "    \"Testing JSONString interface\": {\"A beany object\":42},\n" +
            "    \"aBoolean\": true,\n" +
            "    \"aNumber\": 42,\n" +
            "    \"aString\": \"A beany object\"\n" +
            "}";

        Assert.assertEquals(s, c);
    }

    public void testSlashSlashSlash() throws Exception
    {
        final JSONObject j = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");

        final String c1 = "{\n" +
        "  \"backslash\": \"\\\\\",\n" +
        "  \"closetag\": \"<\\/script>\",\n" +
        "  \"ei\": {\"quotes\": \"\\\"'\"},\n" +
        "  \"eo\": {\n" +
        "    \"a\": \"\\\"quoted\\\"\",\n"+
        "    \"b\": \"don't\"\n" +
        "  },\n" +
        "  \"quotes\": [\n" +
        "    \"'\",\n" +
        "    \"\\\"\"\n" +
        "  ],\n" +
        "  \"slashes\": \"///\"\n" +
        "}";

        final String c2 = "<slashes>///</slashes><closetag>&lt;/script&gt;</closetag><backslash>\\</backslash><ei><quotes>&quot;'</quotes></ei><eo><a>&quot;quoted&quot;</a><b>don't</b></eo><quotes>'</quotes><quotes>&quot;</quotes>";

        Assert.assertEquals(j.toString(2), c1);
        Assert.assertEquals(XML.toString(j), c2);
    }

    public void testLargeAndComplicatedObject() throws Exception
    {
        JSONObject j = new JSONObject(
                "{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001," +
                " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], " +
                "  to   : null, op : 'Good'," +
        "ten:10} postfix comment");
        j.put("String", "98.6");
        j.put("JSONObject", new JSONObject());
        j.put("JSONArray", new JSONArray());
        j.put("int", 57);
        j.put("double", 123456789012345678901234567890.);
        j.put("true", true);
        j.put("false", false);
        j.put("null", JSONObject.NULL);
        j.put("bool", "true");
        j.put("zero", -0.0);
        j.put("\\u2028", "\u2028");
        j.put("\\u2029", "\u2029");
        JSONArray a = j.getJSONArray("foo");
        a.put(666);
        a.put(2001.99);
        a.put("so \"fine\".");
        a.put("so <fine>.");
        a.put(true);
        a.put(false);
        a.put(new JSONArray());
        a.put(new JSONObject());
        j.put("keys", JSONObject.getNames(j));

        final String c1 = "{\n" +
            "    \"JSONArray\": [],\n" +
            "    \"JSONObject\": {},\n" +
            "    \"String\": \"98.6\",\n" +
            "    \"\\\\u2028\": \"\\u2028\",\n" +
            "    \"\\\\u2029\": \"\\u2029\",\n" +
            "    \"bool\": \"true\",\n" +
            "    \"double\": 1.2345678901234568E29,\n" +
            "    \"false\": false,\n" +
            "    \"foo\": [\n" +
            "        true,\n" +
            "        false,\n" +
            "        9876543210,\n" +
            "        0,\n" +
            "        1.00000001,\n" +
            "        1.000000000001,\n" +
            "        1,\n" +
            "        1.0E-17,\n" +
            "        2,\n" +
            "        0.1,\n" +
            "        2.0E100,\n" +
            "        -32,\n" +
            "        [],\n" +
            "        {},\n" +
            "        \"string\",\n" +
            "        666,\n" +
            "        2001.99,\n" +
            "        \"so \\\"fine\\\".\",\n" +
            "        \"so <fine>.\",\n" +
            "        true,\n" +
            "        false,\n" +
            "        [],\n" +
            "        {}\n" +
            "    ],\n" +
            "    \"int\": 57,\n" +
            "    \"keys\": [\n" +
            "        \"foo\",\n" +
            "        \"to\",\n" +
            "        \"op\",\n" +
            "        \"ten\",\n" +
            "        \"String\",\n" +
            "        \"JSONObject\",\n" +
            "        \"JSONArray\",\n" +
            "        \"int\",\n" +
            "        \"double\",\n" +
            "        \"true\",\n" +
            "        \"false\",\n" +
            "        \"null\",\n" +
            "        \"bool\",\n" +
            "        \"zero\",\n" +
            "        \"\\\\u2028\",\n" +
            "        \"\\\\u2029\"\n" +
            "    ],\n" +
            "    \"null\": null,\n" +
            "    \"op\": \"Good\",\n" +
            "    \"ten\": 10,\n" +
            "    \"to\": null,\n" +
            "    \"true\": true,\n" +
            "    \"zero\": -0\n" +
            "}";

        final String c2 = "<foo>true</foo><foo>false</foo><foo>9876543210</foo><foo>0.0</foo><foo>1.00000001</foo><foo>1.000000000001</foo><foo>1.0</foo><foo>1.0E-17</foo><foo>2.0</foo><foo>0.1</foo><foo>2.0E100</foo><foo>-32</foo>" +
            "<foo></foo><foo></foo><foo>string</foo><foo>666</foo><foo>2001.99</foo><foo>so &quot;fine&quot;.</foo><foo>so &lt;fine&gt;.</foo><foo>true</foo><foo>false</foo><foo></foo><foo></foo><to>null</to><op>Good</op>" +
            "<ten>10</ten><String>98.6</String><JSONObject></JSONObject><int>57</int><double>1.2345678901234568E29</double><true>true</true><false>false</false><null>null</null><bool>true</bool><zero>-0.0</zero>";

        Assert.assertEquals(j.toString(4), c1);
        Assert.assertTrue(XML.toString(j).startsWith(c2));

        Assert.assertEquals(j.getDouble("String"), 98.6, 0.00000001);
        Assert.assertTrue(j.getBoolean("bool"));
        Assert.assertEquals(j.getString("to"), "null");
        Assert.assertEquals(j.getString("true"), "true");
        JsonAssert.assertJsonEquals(new JSONArray("[true,false,9876543210,0,1.00000001,1.000000000001,1,1.0E-17,2,0.1,2.0E100,-32,[],{},\"string\",666,2001.99,\"so \\\"fine\\\".\",\"so <fine>.\",true,false,[],{}]"), j.getJSONArray("foo"));
        Assert.assertEquals(j.getString("op"), "Good");
        Assert.assertEquals(j.getInt("ten"), 10);
        Assert.assertFalse(j.optBoolean("oops"));

    }

    public void testXMLtoJson() throws Exception
    {
        final String s = "<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
        final JSONObject j = XML.toJSONObject(s);

        final String c1 =
            "{\"xml\": {\n" +
            "  \"content\": [\n" +
            "    \"First \\t<content>\",\n" +
            "    \"This is \\\"content\\\".\",\n" +
            "    \"JSON does not preserve the sequencing of elements and contents.\",\n" +
            "    \"Content text is an implied structure in XML.\",\n" +
            "    \"JSON does not have implied structure:\",\n" +
            "    \"everything is explicit.\",\n" +
            "    \"CDATA blocks<are><supported>!\"\n" +
            "  ],\n" +
            "  \"five\": [\n" +
            "    \"\",\n" +
            "    \"\"\n" +
            "  ],\n" +
            "  \"four\": \"\",\n" +
            "  \"one\": 1,\n" +
            "  \"seven\": 7,\n" +
            "  \"six\": \"\",\n" +
            "  \"three\": [\n" +
            "    3,\n" +
            "    \"III\",\n" +
            "    \"T H R E E\"\n" +
            "  ],\n" +
            "  \"two\": \" \\\"2\\\" \"\n" +
            "}}";

        final String c2 = "<xml><one>1</one><two> &quot;2&quot; </two><five/><five/>First \t&lt;content&gt;\n" +
            "This is &quot;content&quot;.\n" +
            "JSON does not preserve the sequencing of elements and contents.\n" +
            "Content text is an implied structure in XML.\n" +
            "JSON does not have implied structure:\n" +
            "everything is explicit.\n" +
            "CDATA blocks&lt;are&gt;&lt;supported&gt;!<three>3</three><three>III</three><three>T H R E E</three><four/><six/><seven>7</seven></xml>";


        Assert.assertEquals(j.toString(2), c1);
        Assert.assertEquals(XML.toString(j), c2);

        JSONArray ja = JSONML.toJSONArray(s);


        final String c3 =
        "[\n" +
        "    \"xml\",\n" +
        "    {\n" +
        "        \"one\": 1,\n" +
        "        \"two\": \" \\\"2\\\" \"\n" +
        "    },\n" +
        "    [\"five\"],\n" +
        "    \"First \\t<content>\",\n" +
        "    [\"five\"],\n" +
        "    \"This is \\\"content\\\".\",\n" +
        "    [\n" +
        "        \"three\",\n" +
        "        3\n" +
        "    ],\n" +
        "    \"JSON does not preserve the sequencing of elements and contents.\",\n" +
        "    [\n" +
        "        \"three\",\n" +
        "        \"III\"\n" +
        "    ],\n" +
        "    [\n" +
        "        \"three\",\n" +
        "        \"T H R E E\"\n" +
        "    ],\n" +
        "    [\"four\"],\n" +
        "    \"Content text is an implied structure in XML.\",\n" +
        "    [\n" +
        "        \"six\",\n" +
        "        {\"content\": 6}\n" +
        "    ],\n" +
        "    \"JSON does not have implied structure:\",\n" +
        "    [\n" +
        "        \"seven\",\n" +
        "        7\n" +
        "    ],\n" +
        "    \"everything is explicit.\",\n" +
        "    \"CDATA blocks<are><supported>!\"\n" +
        "]";

        final String c4 = "<xml one=\"1\" two=\" &quot;2&quot; \"><five/>First \t&lt;content&gt;<five/>This is &quot;content&quot;.<three></three>JSON does not preserve the sequencing of elements and contents.<three>III</three><three>T H R E E</three><four/>Content text is an implied structure in XML.<six content=\"6\"/>JSON does not have implied structure:<seven></seven>everything is explicit.CDATA blocks&lt;are&gt;&lt;supported&gt;!</xml>";

        Assert.assertEquals(ja.toString(4), c3);
        Assert.assertEquals(JSONML.toString(ja), c4);
    }

    public void testStringToJsonXML() throws Exception
    {
        final String s = "<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>";
        final JSONArray ja = JSONML.toJSONArray(s);

        final JSONArray c = new JSONArray("[\"xml\",{\"do\": 0},\"uno\",[\"a\",{\"mi\":2,\"re\":1},\"dos\",[\"b\",{\"fa\":3}],\"tres\",[\"c\",true],\"quatro\"],\"cinqo\",[\"d\",\"seis\",[\"e\"]]]");

        JsonAssert.assertJsonEquals(ja, c);
        Assert.assertEquals(ja.toString(4), c.toString(4));

        final String c2 = "<xml do=\"0\">uno<a re=\"1\" mi=\"2\">dos<b fa=\"3\"/>tres<c></c>quatro</a>cinqo<d>seis<e/></d></xml>";

        XMLAssert.assertXMLEqual(c2, JSONML.toString(ja));
    }

    public void testEmptyObjectFields() throws Exception
    {
        final String  s = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
        final JSONObject j = XML.toJSONObject(s);

        final JSONObject c1 = new JSONObject(
                "{\"mapping\": {\n" +
                "  \"class\": [\n" +
                "    {\n" +
                "      \"field\": [\n" +
                "        {\n" +
                "          \"bind-xml\": \"\",\n" +
                "          \"name\": \"ID\",\n" +
                "          \"type\": \"string\"\n" +
                "        },\n" +
                "        \"\",\n" +
                "        \"\",\n" +
                "        \"\"\n" +
                "      ],\n" +
                "      \"name\": \"Customer\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": {\n" +
                "        \"bind-xml\": \"\",\n" +
                "        \"name\": \"text\"\n" +
                "      },\n" +
                "      \"name\": \"FirstName\"},{\"field\": {\"bind-xml\": \"\",\"name\": \"text\"},\"name\": \"MI\"},{\"field\": {\"bind-xml\": \"\",\"name\": \"text\"},\"name\": \"LastName\"}],\"empty\": \"\"}}");

        final String c2 = "<mapping><empty/><class><name>Customer</name><field><name>ID</name><type>string</type><bind-xml/></field><field/><field/><field/></class><class><name>FirstName</name><field><name>text</name><bind-xml/></field></class><class><name>MI</name><field><name>text</name><bind-xml/></field></class><class><name>LastName</name><field><name>text</name><bind-xml/></field></class></mapping>";

        JsonAssert.assertJsonEquals(c1, j);
        XMLAssert.assertXMLEqual(c2, XML.toString(j));
    }

    public void testEmptyArrayFields() throws Exception
    {
        final String  s = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
        final JSONArray ja = JSONML.toJSONArray(s);


        final JSONArray c1 = new JSONArray("[" +
                "    \"mapping\"," +
                "    [\"empty\"]," +
                "    [" +
                "        \"class\"," +
                "        {\"name\": \"Customer\"}," +
                "        [" +
                "            \"field\"," +
                "            {" +
                "                \"name\": \"ID\"," +
                "                \"type\": \"string\"" +
                "            }," +
                "            [" +
                "                \"bind-xml\"," +
                "                {" +
                "                    \"name\": \"ID\"," +
                "                    \"node\": \"attribute\"" +
                "                }" +
                "            ]" +
                "        ]," +
                "        [" +
                "            \"field\"," +
                "            {" +
                "                \"name\": \"FirstName\"," +
                "                \"type\": \"FirstName\"" +
                "            }" +
                "        ]," +
                "        [" +
                "            \"field\"," +
                "            {" +
                "                \"name\": \"MI\"," +
                "                \"type\": \"MI\"" +
                "            }" +
                "        ]," +
                "        [" +
                "            \"field\"," +
                "            {" +
                "                \"name\": \"LastName\"," +
                "                \"type\": \"LastName\"" +
                "            }" +
                "        ]" +
                "    ]," +
                "    [" +
                "        \"class\"," +
                "        {\"name\": \"FirstName\"}," +
                "        [" +
                "            \"field\"," +
                "            {\"name\": \"text\"}," +
                "            [" +
                "                \"bind-xml\"," +
                "                {" +
                "                    \"name\": \"text\"," +
                "                    \"node\": \"text\"" +
                "                }" +
                "            ]" +
                "        ]" +
                "    ]," +
                "    [" +
                "        \"class\"," +
                "        {\"name\": \"MI\"}," +
                "        [" +
                "            \"field\"," +
                "            {\"name\": \"text\"}," +
                "            [" +
                "                \"bind-xml\"," +
                "                {" +
                "                    \"name\": \"text\"," +
                "                    \"node\": \"text\"" +
                "                }" +
                "            ]" +
                "        ]" +
                "    ]," +
                "    [" +
                "        \"class\"," +
                "        {\"name\": \"LastName\"}," +
                "        [" +
                "            \"field\"," +
                "            {\"name\": \"text\"}," +
                "            [" +
                "                \"bind-xml\"," +
                "                {" +
                "                    \"name\": \"text\"," +
                "                    \"node\": \"text\"" +
                "                }" +
                "            ]" +
                "        ]" +
                "    ]" +
        "]");

        final String c2 = "<mapping><empty/><class name=\"Customer\"><field name=\"ID\" type=\"string\"><bind-xml name=\"ID\" node=\"attribute\"/></field><field name=\"FirstName\" type=\"FirstName\"/><field name=\"MI\" type=\"MI\"/><field name=\"LastName\" type=\"LastName\"/></class><class name=\"FirstName\"><field name=\"text\"><bind-xml name=\"text\" node=\"text\"/></field></class><class name=\"MI\"><field name=\"text\"><bind-xml name=\"text\" node=\"text\"/></field></class><class name=\"LastName\"><field name=\"text\"><bind-xml name=\"text\" node=\"text\"/></field></class></mapping>";

        JsonAssert.assertJsonEquals(c1, ja);
        XMLAssert.assertXMLEqual(c2, JSONML.toString(ja));
    }

    public void testXMLToJsonFromString() throws Exception
    {
        final JSONObject j = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");

        final JSONObject c1 = new JSONObject("{\"Book\": {" +
                "  \"Author\": \"Anonymous\"," +
                "  \"Chapter\": [" +
                "    {" +
                "      \"content\": \"This is chapter 1. It is not very long or interesting.\"," +
                "      \"id\": 1" +
                "    }," +
                "    {" +
                "      \"content\": \"This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.\"," +
                "      \"id\": 2" +
                "    }" +
                "  ]," +
                "  \"Title\": \"Sample Book\"" +
        "}}");
        final String c2 = "<Book><Author>Anonymous</Author><Title>Sample Book</Title><Chapter><id>1</id>This is chapter 1. It is not very long or interesting.</Chapter><Chapter><id>2</id>This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>";

        JsonAssert.assertJsonEquals(c1, j);
        XMLAssert.assertXMLEqual(c2, XML.toString(j));
    }

    public void testXmlFromDoctype() throws Exception
    {
        final JSONObject j = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");

        final JSONObject c1 = new JSONObject("{\"bCard\": {\"bCard\": [" +
                "  \"\"," +
                "  \"\"" +
        "]}}");

        final String c2 = "<bCard><bCard/><bCard/></bCard>";

        JsonAssert.assertJsonEquals(c1, j);
        XMLAssert.assertXMLEqual(c2, XML.toString(j));
    }


    public void testXmlToJsonWithSpaces() throws Exception
    {
        final JSONObject j = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");

        final JSONObject c1 = new JSONObject("{\"customer\": {" +
                "  \"ID\": \"fbs0001\"," +
                "  \"MI\": {\"text\": \"B\"}," +
                "  \"firstName\": {\"text\": \"Fred\"}," +
                "  \"lastName\": {\"text\": \"Scerbo\"}" +
        "}}");
        final String c2 = "<customer><firstName><text>Fred</text></firstName><ID>fbs0001</ID><lastName><text>Scerbo</text></lastName><MI><text>B</text></MI></customer>";

        JsonAssert.assertJsonEquals(c1, j);
        XMLAssert.assertXMLEqual(c2, XML.toString(j));
    }

   public void testEmbeddedEntity() throws Exception
   {
       final JSONObject j = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");

       final JSONObject c1 = new JSONObject("{\"list\":{\"type\":\"simple\",\"head\":\"Repository Address\",\"item\":[\"Special Collections Library\",\"ABC University\",\"Main Library, 40 Circle Drive\",\"Ourtown, Pennsylvania\",\"17654 USA\"]}}");
       final String c2 = "<list><type>simple</type><head>Repository Address</head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>";

        JsonAssert.assertJsonEquals(c1, j);
        XMLAssert.assertXMLEqual(c2, XML.toString(j));
   }

   public void testIntertags() throws Exception
   {
       final JSONObject j = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");

       final JSONObject c1 = new JSONObject("{\"test\": {" +
                                           "  \"blip\": {" +
                                           "            \"content\": \"&\\\"toot\\\"&toot;&#x41;\"," +
                                           "    \"sweet\": true" +
                                           "  }," +
                                           "  \"content\": \"deluxe\"," +
                                           "  \"empty\": \"\"," +
                                           "  \"intertag\": \"\"," +
                                           "  \"status\": \"ok\"," +
                                           "  \"w\": [" +
                                           "    \"bonus\"," +
                                           "    \"bonus2\"" +
                                           "  ]," +
                                           "  \"x\": \"eks\"" +
                                           "}}");

       final String c2 = "<test><intertag/><status>ok</status><empty/>deluxe<blip><sweet>true</sweet>&amp;&quot;toot&quot;&amp;toot;&amp;#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>";

       JsonAssert.assertJsonEquals(c1, j);
       XMLAssert.assertXMLEqual(c2, XML.toString(j));
   }

   public void testHTTPGet() throws Exception
   {
       final JSONObject j = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");

       final JSONObject c1 = new JSONObject("{" +
           "  \"Accept\": \"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\"," +
           "  \"Accept-Language\": \"en-us\"," +
           "  \"Accept-encoding\": \"gzip, deflate\"," +
           "  \"Connection\": \"keep-alive\"," +
           "  \"HTTP-Version\": \"HTTP/1.0\"," +
           "  \"Host\": \"www.nokko.com\"," +
           "  \"Method\": \"GET\"," +
           "  \"Request-URI\": \"/\"," +
           "  \"User-Agent\": \"Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\"" +
           "}");

    final String c2 = "GET \"/\" HTTP/1.0\r\n" +
        "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\r\n" +
        "Accept-Language: en-us\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\r\n" +
        "Host: www.nokko.com\r\n" +
        "Connection: keep-alive\r\n" +
        "Accept-encoding: gzip, deflate\r\n\r\n";

    JsonAssert.assertJsonEquals(c1, j);
    Assert.assertEquals(c2, HTTP.toString(j));
   }

   public void testHTTPResponse() throws Exception
   {
       final JSONObject j = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");

       final JSONObject c1 = new JSONObject("{" +
                                            "  \"Connection\": \"Keep-Alive\"," +
                                            "  \"Content-Type\": \"text/html\"," +
                                            "  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\"," +
                                            "  \"HTTP-Version\": \"HTTP/1.1\"," +
                                            "  \"Keep-Alive\": \"timeout=15, max=100\"," +
                                            "  \"Reason-Phrase\": \"Oki Doki\"," +
                                            "  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\"," +
                                            "  \"Status-Code\": \"200\"," +
                                            "  \"Transfer-Encoding\": \"chunked\"" +
                                            "}");

       final String c2 =  "HTTP/1.1 200 Oki Doki\r\n" +
           "Date: Sun, 26 May 2002 17:38:52 GMT\r\n" +
           "Server: Apache/1.3.23 (Unix) mod_perl/1.26\r\n" +
           "Keep-Alive: timeout=15, max=100\r\n" +
           "Connection: Keep-Alive\r\n" +
           "Transfer-Encoding: chunked\r\n" +
           "Content-Type: text/html\r\n\r\n";

       JsonAssert.assertJsonEquals(c1, j);
       Assert.assertEquals(c2, HTTP.toString(j));
   }

   public void testRetrieveHTTPFields() throws Exception
   {
       final JSONObject j = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");

       final JSONObject c1 = new JSONObject("{" +
                                           "  \"HTTP-Version\": \"HTTP/1.0\"," +
                                           "  \"Method\": \"GET\"," +
                                           "  \"Request-URI\": \"/\"," +
                                           "  \"nix\": null," +
                                           "  \"null\": \"null\"," +
                                           "  \"nux\": false" +
                                           "}");

       final String c2 = "<foo><nix>null</nix><nux>false</nux><null>null</null><Request-URI>/</Request-URI><Method>GET</Method><HTTP-Version>HTTP/1.0</HTTP-Version></foo>";

       final String c3 = "GET \"/\" HTTP/1.0\r\n" +
           "nux: false\r\n" +
           "null: null\r\n\r\n";

       JsonAssert.assertJsonEquals(c1, j);
       Assert.assertTrue(j.isNull("nix"));
       Assert.assertTrue(j.has("nix"));
       // It is not XML. It is tag soup. ;-P
       XMLAssert.assertXMLEqual(c2, "<foo>" + XML.toString(j) + "</foo>");
       Assert.assertEquals(c3, HTTP.toString(j));
   }

   public void testXMLSoapHeader() throws Exception
   {

       final JSONObject j = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>"+"\n\n"+"<SOAP-ENV:Envelope"+
                     " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""+
                     " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\""+
                     " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">"+
                     "<SOAP-ENV:Body><ns1:doGoogleSearch"+
                     " xmlns:ns1=\"urn:GoogleSearch\""+
                     " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
                     "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q"+
                     " xsi:type=\"xsd:string\">'+search+'</q> <start"+
                     " xsi:type=\"xsd:int\">0</start> <maxResults"+
                     " xsi:type=\"xsd:int\">10</maxResults> <filter"+
                     " xsi:type=\"xsd:boolean\">true</filter> <restrict"+
                     " xsi:type=\"xsd:string\"></restrict> <safeSearch"+
                     " xsi:type=\"xsd:boolean\">false</safeSearch> <lr"+
                     " xsi:type=\"xsd:string\"></lr> <ie"+
                     " xsi:type=\"xsd:string\">latin1</ie> <oe"+
                     " xsi:type=\"xsd:string\">latin1</oe>"+
                     "</ns1:doGoogleSearch>"+
                     "</SOAP-ENV:Body></SOAP-ENV:Envelope>");

       final JSONObject c1 = new JSONObject("{\"SOAP-ENV:Envelope\": {" +
                                                        "  \"SOAP-ENV:Body\": {\"ns1:doGoogleSearch\": {" +
                                                        "    \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\"," +
                                                        "    \"filter\": {" +
                                                        "      \"content\": true," +
                                                        "      \"xsi:type\": \"xsd:boolean\"" +
                                                        "    }," +
                                                        "    \"ie\": {" +
                                                        "      \"content\": \"latin1\"," +
                                                        "      \"xsi:type\": \"xsd:string\"" +
                                                        "    }," +
                                                        "    \"key\": {" +
                                                        "      \"content\": \"GOOGLEKEY\"," +
                                                        "      \"xsi:type\": \"xsd:string\"" +
                                                        "    }," +
                                                        "    \"lr\": {\"xsi:type\": \"xsd:string\"}," +
                                                        "    \"maxResults\": {" +
                                                        "      \"content\": 10," +
                                                        "      \"xsi:type\": \"xsd:int\"" +
                                                        "    }," +
                                                        "    \"oe\": {" +
                                                        "      \"content\": \"latin1\"," +
                                                        "      \"xsi:type\": \"xsd:string\"" +
                                                        "    }," +
                                                        "    \"q\": {" +
                                                        "      \"content\": \"'+search+'\"," +
                                                        "      \"xsi:type\": \"xsd:string\"" +
                                                        "    }," +
                                                        "    \"restrict\": {\"xsi:type\": \"xsd:string\"}," +
                                                        "    \"safeSearch\": {" +
                                                        "      \"content\": false," +
                                                        "      \"xsi:type\": \"xsd:boolean\"" +
                                                        "    }," +
                                                        "    \"start\": {" +
                                                        "      \"content\": 0," +
                                                        "      \"xsi:type\": \"xsd:int\"" +
                                                        "    }," +
                                                        "    \"xmlns:ns1\": \"urn:GoogleSearch\"" +
                                                        "  }}," +
                                                        "  \"xmlns:SOAP-ENV\": \"http://schemas.xmlsoap.org/soap/envelope/\"," +
                                                        "  \"xmlns:xsd\": \"http://www.w3.org/1999/XMLSchema\"," +
                                                        "\"xmlns:xsi\": \"http://www.w3.org/1999/XMLSchema-instance\"" +
                                                        "}}");

                   final String c2 = "<SOAP-ENV:Envelope><xmlns:SOAP-ENV>http://schemas.xmlsoap.org/soap/envelope/</xmlns:SOAP-ENV><xmlns:xsi>http://www.w3.org/1999/XMLSchema-instance</xmlns:xsi><xmlns:xsd>http://www.w3.org/1999/XMLSchema</xmlns:xsd><SOAP-ENV:Body><ns1:doGoogleSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><key><xsi:type>xsd:string</xsi:type>GOOGLEKEY</key><q><xsi:type>xsd:string</xsi:type>'+search+'</q><start><xsi:type>xsd:int</xsi:type>0</start><maxResults><xsi:type>xsd:int</xsi:type>10</maxResults><filter><xsi:type>xsd:boolean</xsi:type>true</filter><restrict><xsi:type>xsd:string</xsi:type></restrict><safeSearch><xsi:type>xsd:boolean</xsi:type>false</safeSearch><lr><xsi:type>xsd:string</xsi:type></lr><ie><xsi:type>xsd:string</xsi:type>latin1</ie><oe><xsi:type>xsd:string</xsi:type>latin1</oe></ns1:doGoogleSearch></SOAP-ENV:Body></SOAP-ENV:Envelope>";

                   JsonAssert.assertJsonEquals(c1, j);
                   Assert.assertEquals(c2, XML.toString(j));
   }

   public void testSoapEnvelope() throws Exception
   {
       final JSONObject j = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");

       final String c1 = "{\"Envelope\": {\"Body\": {\"ns1:doGoogleSearch\": {\n" +
           "  \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n" +
           "  \"filter\": true,\n" +
           "  \"ie\": \"latin1\",\n" +
           "  \"key\": \"GOOGLEKEY\",\n" +
           "  \"maxResults\": 10,\n" +
           "  \"oe\": \"latin1\",\n" +
           "  \"q\": \"'+search+'\",\n" +
           "  \"safeSearch\": false,\n" +
           "  \"start\": 0,\n" +
           "  \"xmlns:ns1\": \"urn:GoogleSearch\"\n" +
           "}}}}";
       final String c2 = "<Envelope><Body><ns1:doGoogleSearch><oe>latin1</oe><filter>true</filter><q>'+search+'</q><key>GOOGLEKEY</key><maxResults>10</maxResults><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><start>0</start><ie>latin1</ie><safeSearch>false</safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1></ns1:doGoogleSearch></Body></Envelope>";

       Assert.assertEquals(c1, j.toString(2));
       Assert.assertEquals(c2, XML.toString(j));
   }

   public void testReadCookies() throws Exception
   {
       final JSONObject j = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");

       final JSONObject c1 = new JSONObject("{" +
                                            "  \"f%oo\": \"b l=ah\"," +
                                            "  \"o;n@e\": \"t.wo\"" +
                                            "}");

       final String c2 = "f%25oo=b l%3dah;o%3bn@e=t.wo";

       JsonAssert.assertJsonEquals(c1, j);
       Assert.assertEquals(c2, CookieList.toString(j));
   }

   public void testExpiresCookie() throws Exception
   {
       final JSONObject j = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");

       final JSONObject c1 = new JSONObject("{" +
                                            "  \"expires\": \"April 24, 2002\"," +
                                            "  \"name\": \"f%oo\"," +
                                            "  \"secure\": true," +
                                            "  \"value\": \"blah\"" +
                                            "}");

       final String c2 = "f%25oo=blah;expires=April 24, 2002;secure";

       JsonAssert.assertJsonEquals(c1, j);
       Assert.assertEquals(c2, Cookie.toString(j));
   }

   public void testBackslash() throws Exception
   {

       final JSONObject j = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");

       final String c1 = "{\"script\":\"It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers<\\/script>so we insert a backslash before the /\"}";

       Assert.assertEquals(c1, j.toString());
   }

   public void testTokenerSession() throws Exception
   {
       final JSONTokener jt = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
       JSONObject j = new JSONObject(jt);

       Assert.assertEquals("{\"op\":\"test\",\"to\":\"session\",\"pre\":1}", j.toString());

       Assert.assertEquals(1, j.optInt("pre"));

       Assert.assertEquals('{', jt.skipTo('{'));

       j = new JSONObject(jt);
       Assert.assertEquals("{\"op\":\"test\",\"to\":\"session\",\"pre\":2}", j.toString());
   }

   public void testCLD() throws Exception
   {
       final JSONArray a = CDL.toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");

       final String c1 = "Comma delimited list test,\"StripQuotes\",\"quote, comma\"\n" +
           "1,2,3\n" +
           ",\"It is good,\",It works.\n";

       final JSONArray c2 = new JSONArray("[" +
               "    {" +
               "        \"StripQuotes\": \"2\"," +
               "        \"Comma delimited list test\": \"1\"," +
               "        \"quote, comma\": \"3\"" +
               "    }," +
               "    {" +
               "        \"StripQuotes\": \"It is good,\"," +
               "        \"Comma delimited list test\": \"\"," +
               "        \"quote, comma\": \"It works.\"" +
               "    }" +
               "]");

       final JSONArray c3 = new JSONArray("[" +
               "    {" +
               "        \"Comma delimited list test\": \"1\"," +
               "        \"StripQuotes\": \"2\"," +
               "        \"quote, comma\": \"3\"" +
               "    }," +
               "    {" +
               "        \"Comma delimited list test\": \"\"," +
               "        \"StripQuotes\": \"It is good,\"," +
               "        \"quote, comma\": \"It works.\"" +
               "    }" +
               "]");

       final String s = CDL.toString(a);
       Assert.assertEquals(c1, s);
       JsonAssert.assertJsonEquals(c2, CDL.toJSONArray(s));

       final JSONArray a2 = CDL.toJSONArray(s);
       JsonAssert.assertJsonEquals(c3, a2);
   }

   public void testImpliedNull() throws Exception
   {
       final JSONArray a = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");

       Assert.assertEquals("[\"<escape>\",\"next is an implied null\",null,\"ok\"]", a.toString());
       Assert.assertEquals("<array>&lt;escape&gt;</array><array>next is an implied null</array><array>null</array><array>ok</array>", XML.toString(a));
   }

   public void testNonstandardForm() throws Exception
   {
       final JSONObject j = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");

       final JSONObject c1 = new JSONObject("{" +
                                            "    \"+\": 6.0E66," +
                                            "    \"[true]\": [[" +
                                            "        \"!\"," +
                                            "        \"@\"," +
                                            "        \"*\"" +
                                            "    ]]," +
                                            "    \"dec\": 666," +
                                            "    \"double\": 0.666," +
                                            "    \"empty\": \"\"," +
                                            "    \"false\": false," +
                                            "    \"forgiving\": \"This package can be used to parse formats that are similar to but not stricting conforming to JSON\"," +
                                            "    \"fun\": \"with non-standard forms\"," +
                                            "    \"hex\": 1638," +
                                            "    \"noh\": \"0x0x\"," +
                                            "    \"null\": null," +
                                            "    \"o\": 999," +
                                            "    \"oct\": 666," +
                                            "    \"one\": [[1]]," +
                                            "    \"pluses\": \"+++\"," +
                                            "    \"string\": \"o. k.\"," +
                                            "    \"true\": true," +
                                            "    \"uno\": [[{\"1\": 1}]]," +
                                            "    \"why\": \"To make it easier to migrate existing data to JSON\"" +
                                            "}");

       JsonAssert.assertJsonEquals(c1, j);

       Assert.assertTrue(j.getBoolean("true"));
       Assert.assertFalse(j.getBoolean("false"));
   }

   public void testDHO() throws Exception
   {
       JSONObject j = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");

       j = new JSONObject(j, new String[]{"dec", "oct", "hex", "missing"});

       JSONObject c1 = new JSONObject("{" +
                                      "    \"dec\": 666," +
                                      "    \"hex\": 1638," +
                                      "    \"oct\": 666" +
                                      "}");

       JsonAssert.assertJsonEquals(c1, j);
   }

   public void testArrayObjectStringer() throws Exception
   {
       final JSONArray a = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
       final JSONObject j = new JSONObject("{" +
                                      "    \"dec\": 666," +
                                      "    \"hex\": 1638," +
                                      "    \"oct\": 666" +
                                      "}");

       final JSONWriter jw = new JSONStringer().array().value(a).value(j).endArray();

       final String c1 = "[[\"<escape>\",\"next is an implied null\",null,\"ok\"],{\"dec\":666,\"hex\":1638,\"oct\":666}]";

       Assert.assertEquals(c1, jw.toString());
   }

   public void testNumberTypes() throws Exception
   {
       final JSONObject j = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");

       final String c1 = "{\n" +
           "    \"double\": \"9223372036854775808\",\n" +
           "    \"int\": 2147483647,\n" +
           "    \"long\": 2147483648,\n" +
           "    \"longer\": 9223372036854775807,\n" +
           "    \"string\": \"98.6\"\n" +
           "}";

       Assert.assertEquals(c1, j.toString(4));

       Assert.assertEquals(2147483647, j.getInt("int"));
       Assert.assertEquals(-2147483648, j.getInt("long"));
       Assert.assertEquals(-1, j.getInt("longer"));

       Assert.assertEquals(2147483647, j.getLong("int"));
       Assert.assertEquals(2147483648L, j.getLong("long"));
       Assert.assertEquals(9223372036854775807L, j.getLong("longer"));

       Assert.assertEquals(2.147483647E9, j.getDouble("int"));
       Assert.assertEquals(2.147483648E9, j.getDouble("long"));
       Assert.assertEquals(9.223372036854776E18, j.getDouble("longer"));
       Assert.assertEquals(9.223372036854776E18, j.getDouble("double"));
       Assert.assertEquals(98.6, j.getDouble("string"));

       j.put("good sized", 9223372036854775807L);

       final JSONObject c2 = new JSONObject("{" +
                                            "    \"double\": \"9223372036854775808\"," +
                                            "    \"good sized\": 9223372036854775807," +
                                            "    \"int\": 2147483647," +
                                            "    \"long\": 2147483648," +
                                            "    \"longer\": 9223372036854775807," +
                                            "    \"string\": \"98.6\"" +
                                            "}");

       JsonAssert.assertJsonEquals(c2, j);
   }

   public void testNumberArray() throws Exception
   {
       final JSONArray a = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");


       final String c1 = "[\n" +
           "    2147483647,\n" +
           "    2147483648,\n" +
           "    9223372036854775807,\n" +
           "    \"9223372036854775808\"\n" +
           "]";

       Assert.assertEquals(c1, a.toString(4));
   }


   public void testNumberKeys() throws Exception
   {
       final JSONObject j = new JSONObject("{" +
                                           "    \"double\": \"9223372036854775808\"," +
                                           "    \"good sized\": 9223372036854775807," +
                                           "    \"int\": 2147483647," +
                                           "    \"long\": 2147483648," +
                                           "    \"longer\": 9223372036854775807," +
                                           "    \"string\": \"98.6\"" +
                                           "}");

       Assert.assertEquals("2147483647", j.getString("int"));
       Assert.assertEquals("2147483648", j.getString("long"));
       Assert.assertEquals("9223372036854775807", j.getString("longer"));
       Assert.assertEquals("9223372036854775808", j.getString("double"));
       Assert.assertEquals("98.6", j.getString("string"));
       Assert.assertEquals("9223372036854775807", j.getString("good sized"));
   }

    public void testAccumulate() throws Exception
    {
        final JSONObject j = new JSONObject();
        j.accumulate("stooge", "Curly");
        j.accumulate("stooge", "Larry");
        j.accumulate("stooge", "Moe");
        JSONArray a = j.getJSONArray("stooge");
        a.put(5, "Shemp");

        final JSONObject c1 = new JSONObject("{\"stooge\": [" +
                                             "    \"Curly\"," +
                                             "    \"Larry\"," +
                                             "    \"Moe\"," +
                                             "    null," +
                                             "    null," +
                                             "    \"Shemp\"" +
                                             "]}");

        JsonAssert.assertJsonEquals(c1, j);
   }

   public void testWriter() throws Exception
   {

       final JSONObject j = new JSONObject("{\"stooge\": [" +
                                           "    \"Curly\"," +
                                           "    \"Larry\"," +
                                           "    \"Moe\"," +
                                           "    null," +
                                           "    null," +
                                           "    \"Shemp\"" +
                                           "]}");

       final String c1 = "{\"stooge\":[\"Curly\",\"Larry\",\"Moe\",null,null,\"Shemp\"]}";

       Assert.assertEquals(c1, j.write(new StringWriter()).toString());
   }

    public void testStrangeXmlTag() throws JSONException
    {
       final String s = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";

       final JSONObject j = XML.toJSONObject(s);

       final JSONObject c1 = new JSONObject("{\"xml\": {" +
                                            "    \"a\": [" +
                                            "        \"\"," +
                                            "        1," +
                                            "        22," +
                                            "        333" +
                                            "    ]," +
                                            "    \"empty\": \"\"" +
                                            "}}");

       final String c2 = "<xml><empty/><a/><a>1</a><a>22</a><a>333</a></xml>";

       JsonAssert.assertJsonEquals(c1, j);
       Assert.assertEquals(c2, XML.toString(j));
    }


    public void testBookChapterXml() throws JSONException
    {
        final String s = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";

        final JSONObject j = XML.toJSONObject(s);

        final JSONObject c1 = new JSONObject("{\"book\": {\"chapter\": [" +
                                             "    \"Content of the first chapter\"," +
                                             "    {" +
                                             "        \"chapter\": [" +
                                             "            \"Content of the first subchapter\"," +
                                             "            \"Content of the second subchapter\"" +
                                             "        ]," +
                                             "        \"content\": \"Content of the second chapter\"" +
                                             "    }," +
                                             "    \"Third Chapter\"" +
                                             "]}}");

        final String c2 = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";

        JsonAssert.assertJsonEquals(c1, j);
        Assert.assertEquals(c2, XML.toString(j));
    }


   public void testBookChapterJsonML() throws Exception
   {
       final String s = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";

       final JSONArray a = JSONML.toJSONArray(s);

       final JSONArray c1 = new JSONArray("[" +
                                          "    \"book\"," +
                                          "    [" +
                                          "        \"chapter\"," +
                                          "        \"Content of the first chapter\"" +
                                          "    ]," +
                                          "    [" +
                                          "        \"chapter\"," +
                                          "        \"Content of the second chapter\"," +
                                          "        [" +
                                          "            \"chapter\"," +
                                          "            \"Content of the first subchapter\"" +
                                          "        ]," +
                                          "        [" +
                                          "            \"chapter\"," +
                                          "            \"Content of the second subchapter\"" +
                                          "        ]" +
                                          "    ]," +
                                          "    [" +
                                          "        \"chapter\"," +
                                          "        \"Third Chapter\"" +
                                          "    ]" +
                                          "]");

       final String c2 = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";

        JsonAssert.assertJsonEquals(c1, a);
        Assert.assertEquals(c2, JSONML.toString(a));
   }

   public void testAccumulateCollections() throws Exception
   {
       final Collection<String> c = null;
       final Map<String, String> m = null;

       final JSONObject j = new JSONObject(m);
       final JSONArray a = new JSONArray(c);

       j.append("stooge", "Joe DeRita");
       j.append("stooge", "Shemp");
       j.accumulate("stooges", "Curly");
       j.accumulate("stooges", "Larry");
       j.accumulate("stooges", "Moe");
       j.accumulate("stoogearray", j.get("stooges"));
       j.put("map", m);
       j.put("collection", c);
       j.put("array", a);
       a.put(m);
       a.put(c);

       final JSONObject c1 = new JSONObject("{" +
                                            "    \"array\": [" +
                                            "        {}," +
                                            "        []" +
                                            "    ]," +
                                            "    \"collection\": []," +
                                            "    \"map\": {}," +
                                            "    \"stooge\": [" +
                                            "        \"Joe DeRita\"," +
                                            "        \"Shemp\"" +
                                            "    ]," +
                                            "    \"stoogearray\": [[" +
                                            "        \"Curly\"," +
                                            "        \"Larry\"," +
                                            "        \"Moe\"" +
                                            "    ]]," +
                                            "    \"stooges\": [" +
                                            "        \"Curly\"," +
                                            "        \"Larry\"," +
                                            "        \"Moe\"" +
                                            "    ]" +
                                            "}");

       JsonAssert.assertJsonEquals(c1, j);
   }

    public void testSemicolons() throws Exception
    {

        final String s = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
        final JSONObject j = new JSONObject(s);

        final JSONObject c1 = new JSONObject("{" +
                                             "    \"AnimalColors\": {" +
                                             "        \"lamb\": \"black\"," +
                                             "        \"pig\": \"pink\"," +
                                             "        \"worm\": \"pink\"" +
                                             "    }," +
                                             "    \"AnimalSmells\": {" +
                                             "        \"lamb\": \"lambish\"," +
                                             "        \"pig\": \"piggish\"," +
                                             "        \"worm\": \"wormy\"" +
                                             "    }," +
                                             "    \"AnimalSounds\": {" +
                                             "        \"Lisa\": \"Why is the worm talking like a lamb?\"," +
                                             "        \"lamb\": \"baa\"," +
                                             "        \"pig\": \"oink\"," +
                                             "        \"worm\": \"baa\"" +
                                             "    }," +
                                             "    \"plist\": \"Apple\"" +
                                             "}");

        JsonAssert.assertJsonEquals(c1, j);
   }

   public void testBraces() throws Exception
   {
       final String s = " (\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\")";
       final JSONArray a = new JSONArray(s);

       final JSONArray c1 = new JSONArray("[\"San Francisco\",\"New York\",\"Seoul\",\"London\",\"Seattle\",\"Shanghai\"]");

        JsonAssert.assertJsonEquals(c1, a);
   }

   public void testXmlSingleTicksObject() throws Exception
   {
       final String s = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
       final JSONObject j = XML.toJSONObject(s);


       final JSONObject c1 = new JSONObject("{\"a\": {" +
                                            "  \"b\": \"The content of b\"," +
                                            "  \"c\": {" +
                                            "    \"content\": \"The content of c\"," +
                                            "    \"san\": 3" +
                                            "  }," +
                                            "  \"content\": \"and\"," +
                                            "  \"d\": [" +
                                            "    \"do\"," +
                                            "    \"re\"," +
                                            "    \"mi\"" +
                                            "  ]," +
                                            "  \"e\": \"\"," +
                                            "  \"f\": \"\"," +
                                            "  \"ichi\": 1," +
                                            "  \"ni\": 2" +
                                            "}}");

       final String c2 = "<a><ichi>1</ichi><ni>2</ni><b>The content of b</b>and<c><san>3</san>The content of c</c><d>do</d><d>re</d><d>mi</d><e/><f/></a>";

       JsonAssert.assertJsonEquals(c1, j);
       Assert.assertEquals(c2, XML.toString(j));
   }

   public void testXmlSingleTicksArray() throws Exception
   {
       final String s = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
       final JSONArray ja = JSONML.toJSONArray(s);

       final JSONArray c1 = new JSONArray("[" +
                                          "    \"a\"," +
                                          "    {" +
                                          "        \"ichi\": 1," +
                                          "        \"ni\": 2" +
                                          "    }," +
                                          "    [" +
                                          "        \"b\"," +
                                          "        \"The content of b\"" +
                                          "    ]," +
                                          "    \"and\"," +
                                          "    [" +
                                          "        \"c\"," +
                                          "        {\"san\": 3}," +
                                          "        \"The content of c\"" +
                                          "    ]," +
                                          "    [" +
                                          "        \"d\"," +
                                          "        \"do\"" +
                                          "    ]," +
                                          "    [\"e\"]," +
                                          "    [" +
                                          "        \"d\"," +
                                          "        \"re\"" +
                                          "    ]," +
                                          "    [\"f\"]," +
                                          "    [" +
                                          "        \"d\"," +
                                          "        \"mi\"" +
                                          "    ]" +
                                          "]");

       final String c2 = "<a ichi=\"1\" ni=\"2\"><b>The content of b</b>and<c san=\"3\">The content of c</c><d>do</d><e/><d>re</d><f/><d>mi</d></a>";

       JsonAssert.assertJsonEquals(c1, ja);
       Assert.assertEquals(c2, JSONML.toString(ja));
   }

   public void testXmlToJsonML() throws Exception
   {
       final String s = "<Root><MsgType type=\"node\"><BatchType type=\"string\">111111111111111</BatchType></MsgType></Root>";
       final JSONObject j = JSONML.toJSONObject(s);
       final JSONArray ja = JSONML.toJSONArray(s);

       final JSONObject c1 = new JSONObject("{\"tagName\":\"Root\",\"childNodes\":[{\"tagName\":\"MsgType\",\"type\":\"node\",\"childNodes\":[{\"tagName\":\"BatchType\",\"type\":\"string\",\"childNodes\":[111111111111111]}]}]}");
       final JSONArray c2 = new JSONArray("[\"Root\",[\"MsgType\",{\"type\":\"node\"},[\"BatchType\",{\"type\":\"string\"},111111111111111]]]");

       JsonAssert.assertJsonEquals(c1, j);
       JsonAssert.assertJsonEquals(c2, ja);
   }


    @Test(expectedExceptions = JSONException.class)
    public void testMissingValue() throws Exception
    {
        new JSONArray("[\n\r\n\r}");
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadChar() throws Exception
    {
        new JSONArray("<\n\r\n\r      ");
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadNumber() throws Exception
    {
        final JSONArray a = new JSONArray();
        a.put(Double.NEGATIVE_INFINITY);
        a.put(Double.NaN);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testMissingField() throws Exception
    {
        final JSONObject j = new JSONObject();
        System.out.println(j.getDouble("stooge"));
    }

    @Test(expectedExceptions = JSONException.class)
    public void testNullValue() throws Exception
    {
        final JSONObject j = new JSONObject();
        j.put("howard", (Double) null);
        System.out.println(j.getDouble("howard"));
    }

    @Test(expectedExceptions = JSONException.class)
    public void testNullField() throws Exception
    {
        final JSONObject j = new JSONObject();
        System.out.println(j.put(null, "howard"));
    }

    @Test(expectedExceptions = JSONException.class)
    public void testMissingFieldArray() throws Exception
    {
        final JSONArray a = new JSONArray();
        System.out.println(a.getDouble(0));
    }

    @Test(expectedExceptions = JSONException.class)
    public void testNegativeIndex() throws Exception
    {
        final JSONArray a = new JSONArray();
        System.out.println(a.get(-1));
    }

    @Test(expectedExceptions = JSONException.class)
    public void testInvalidNumber() throws Exception
    {
        final JSONArray a = new JSONArray();
        System.out.println(a.put(Double.NaN));
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadXML() throws Exception
    {
        XML.toJSONObject("<a><b>    ");
    }

    @Test(expectedExceptions = JSONException.class)
    public void testUnmatchedXml() throws Exception
    {
        XML.toJSONObject("<a></b>    ");
    }

    @Test(expectedExceptions = JSONException.class)
    public void testUnclosedXML() throws Exception
    {
        XML.toJSONObject("<a></a    ");
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadArrayCtor() throws Exception
    {
        final JSONArray ja = new JSONArray(new Object());
        System.out.println(ja.toString());
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadArrayString() throws Exception
    {
        final String s = "[)";
        new JSONArray(s);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadJSONML() throws Exception
    {
        final String s = "<xml";
        JSONML.toJSONArray(s);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testUnmatchedJSONML() throws Exception
    {
        final String s = "<right></wrong>";
        JSONML.toJSONArray(s);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadJson() throws Exception
    {
        final String s = "{\"koda\": true, \"koda\": true}";
        new JSONObject(s);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testBadJsonStringer() throws Exception
    {
        final JSONStringer jj = new JSONStringer();
        final String s = jj
        .object()
        .key("bosanda")
        .value("MARIE HAA'S")
        .key("bosanda")
        .value("MARIE HAA\\'S")
        .endObject()
        .toString();
        System.out.println(s);
    }

}
