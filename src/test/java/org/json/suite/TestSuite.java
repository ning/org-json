package org.json.suite;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.JSONStringer;
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
//
//    public void test() throws Exception
//    {
//        //      s = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
//        //      j = XML.toJSONObject(s);
//
//        //      System.out.println(j.toString(2));
//        //      System.out.println(XML.toString(j));
//        //      System.out.println("");
//
//    }
//
//    public void test() throws Exception
//    {
//        //      ja = JSONML.toJSONArray(s);
//        //      System.out.println(ja.toString(4));
//        //      System.out.println(JSONML.toString(ja));
//        //      System.out.println("");
//
//    }
//
//    public void test() throws Exception
//    {
//        //      j = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
//        //      System.out.println(j.toString(2));
//        //      System.out.println(XML.toString(j));
//        //      System.out.println("");
//
//    }
//
//    public void test() throws Exception
//    {
//        //      j = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
//        //      System.out.println(j.toString(2));
//        //      System.out.println(XML.toString(j));
//        //      System.out.println("");
//
//    }
//
//    public void test() throws Exception
//    {
//        //      j = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
//        //      System.out.println(j.toString(2));
//        //      System.out.println(XML.toString(j));
//        //      System.out.println("");
//
//    }
//
//    //     /**
//    //      * Entry point.
//    //      * @param args
//    //      */
//    //     public static void main(String args[]) {
//    //         Iterator it;
//    //         JSONArray a;
//    //         JSONObject j;
//    //         JSONStringer jj;
//    //         Object o;
//    //         String s;
//
//
//
//    //     	Obj obj = new Obj("A beany object", 42, true);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    public void test() throws Exception
//    {
//
//        //             j = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
//        //             System.out.println(j.toString());
//        //             System.out.println(XML.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(XML.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(HTTP.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(HTTP.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
//        //             System.out.println(j.toString(2));
//        //             System.out.println("isNull: " + j.isNull("nix"));
//        //             System.out.println("   has: " + j.has("nix"));
//        //             System.out.println(XML.toString(j));
//        //             System.out.println(HTTP.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>"+"\n\n"+"<SOAP-ENV:Envelope"+
//        //               " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""+
//        //               " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\""+
//        //               " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">"+
//        //               "<SOAP-ENV:Body><ns1:doGoogleSearch"+
//        //               " xmlns:ns1=\"urn:GoogleSearch\""+
//        //               " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
//        //               "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q"+
//        //               " xsi:type=\"xsd:string\">'+search+'</q> <start"+
//        //               " xsi:type=\"xsd:int\">0</start> <maxResults"+
//        //               " xsi:type=\"xsd:int\">10</maxResults> <filter"+
//        //               " xsi:type=\"xsd:boolean\">true</filter> <restrict"+
//        //               " xsi:type=\"xsd:string\"></restrict> <safeSearch"+
//        //               " xsi:type=\"xsd:boolean\">false</safeSearch> <lr"+
//        //               " xsi:type=\"xsd:string\"></lr> <ie"+
//        //               " xsi:type=\"xsd:string\">latin1</ie> <oe"+
//        //               " xsi:type=\"xsd:string\">latin1</oe>"+
//        //               "</ns1:doGoogleSearch>"+
//        //               "</SOAP-ENV:Body></SOAP-ENV:Envelope>");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(XML.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(XML.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(CookieList.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
//        //             System.out.println(j.toString(2));
//        //             System.out.println(Cookie.toString(j));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
//        //             System.out.println(j.toString());
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             JSONTokener jt = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
//        //             j = new JSONObject(jt);
//        //             System.out.println(j.toString());
//        //             System.out.println("pre: " + j.optInt("pre"));
//        //             int i = jt.skipTo('{');
//        //             System.out.println(i);
//        //             j = new JSONObject(jt);
//        //             System.out.println(j.toString());
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             a = CDL.toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");
//
//        //             s = CDL.toString(a);
//        //             System.out.println(s);
//        //             System.out.println("");
//        //             System.out.println(a.toString(4));
//        //             System.out.println("");
//        //             a = CDL.toJSONArray(s);
//        //             System.out.println(a.toString(4));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             a = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
//        //             System.out.println(a.toString());
//        //             System.out.println("");
//        //             System.out.println(XML.toString(a));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
//        //             System.out.println(j.toString(4));
//        //             System.out.println("");
//        //             if (j.getBoolean("true") && !j.getBoolean("false")) {
//        //                 System.out.println("It's all good");
//        //             }
//
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             j = new JSONObject(j, new String[]{"dec", "oct", "hex", "missing"});
//        //             System.out.println(j.toString(4));
//
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//        //             System.out.println(new JSONStringer().array().value(a).value(j).endArray());
//
//        //             j = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
//        //             System.out.println(j.toString(4));
//
//        //             System.out.println("\ngetInt");
//        //             System.out.println("int    " + j.getInt("int"));
//        //             System.out.println("long   " + j.getInt("long"));
//        //             System.out.println("longer " + j.getInt("longer"));
//        //             //System.out.println("double " + j.getInt("double"));
//        //             //System.out.println("string " + j.getInt("string"));
//
//        //             System.out.println("\ngetLong");
//        //             System.out.println("int    " + j.getLong("int"));
//        //             System.out.println("long   " + j.getLong("long"));
//        //             System.out.println("longer " + j.getLong("longer"));
//        //             //System.out.println("double " + j.getLong("double"));
//        //             //System.out.println("string " + j.getLong("string"));
//
//        //             System.out.println("\ngetDouble");
//        //             System.out.println("int    " + j.getDouble("int"));
//        //             System.out.println("long   " + j.getDouble("long"));
//        //             System.out.println("longer " + j.getDouble("longer"));
//        //             System.out.println("double " + j.getDouble("double"));
//        //             System.out.println("string " + j.getDouble("string"));
//
//        //             j.put("good sized", 9223372036854775807L);
//        //             System.out.println(j.toString(4));
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             a = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
//        //             System.out.println(a.toString(4));
//
//        //             System.out.println("\nKeys: ");
//        //             it = j.keys();
//        //             while (it.hasNext()) {
//        //                 s = (String)it.next();
//        //                 System.out.println(s + ": " + j.getString(s));
//        //             }
//
//
//        //             System.out.println("\naccumulate: ");
//        //             j = new JSONObject();
//        //             j.accumulate("stooge", "Curly");
//        //             j.accumulate("stooge", "Larry");
//        //             j.accumulate("stooge", "Moe");
//        //             a = j.getJSONArray("stooge");
//        //             a.put(5, "Shemp");
//        //             System.out.println(j.toString(4));
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             System.out.println("\nwrite:");
//        //             System.out.println(j.write(new StringWriter()));
//
//        //             s = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
//        //             j = XML.toJSONObject(s);
//        //             System.out.println(j.toString(4));
//        //             System.out.println(XML.toString(j));
//
//        //             s = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
//        //             j = XML.toJSONObject(s);
//        //             System.out.println(j.toString(4));
//        //             System.out.println(XML.toString(j));
//
//    }
//
//    public void test() throws Exception
//    {
//
//        //             a = JSONML.toJSONArray(s);
//        //             System.out.println(a.toString(4));
//        //             System.out.println(JSONML.toString(a));
//
//    }
//
//    public void test() throws Exception
//    {
//
//        //             Collection c = null;
//        //             Map m = null;
//
//        //             j = new JSONObject(m);
//        //             a = new JSONArray(c);
//        //             j.append("stooge", "Joe DeRita");
//        //             j.append("stooge", "Shemp");
//        //             j.accumulate("stooges", "Curly");
//        //             j.accumulate("stooges", "Larry");
//        //             j.accumulate("stooges", "Moe");
//        //             j.accumulate("stoogearray", j.get("stooges"));
//        //             j.put("map", m);
//        //             j.put("collection", c);
//        //             j.put("array", a);
//        //             a.put(m);
//        //             a.put(c);
//        //             System.out.println(j.toString(4));
//
//    }
//
//    public void test() throws Exception
//    {
//
//        //             s = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
//        //             j = new JSONObject(s);
//        //             System.out.println(j.toString(4));
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             s = " (\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\")";
//        //             a = new JSONArray(s);
//        //             System.out.println(a.toString());
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             s = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
//        //             j = XML.toJSONObject(s);
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             System.out.println(j.toString(2));
//        //             System.out.println(XML.toString(j));
//        //             System.out.println("");
//        //             ja = JSONML.toJSONArray(s);
//        //             System.out.println(ja.toString(4));
//        //             System.out.println(JSONML.toString(ja));
//        //             System.out.println("");
//    }
//
//    public void test() throws Exception
//    {
//
//
//        //             s = "<Root><MsgType type=\"node\"><BatchType type=\"string\">111111111111111</BatchType></MsgType></Root>";
//        //             j = JSONML.toJSONObject(s);
//        //             System.out.println(j);
//        //             ja = JSONML.toJSONArray(s);
//        //             System.out.println(ja);
//
//
//        //             System.out.println("\nTesting Exceptions: ");
//
//
//    }

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
