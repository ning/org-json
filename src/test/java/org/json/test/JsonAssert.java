package org.json.test;

import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonAssert
{
    private JsonAssert()
    {
    }

    /**
     * Deep-compare two JSON Objects for equality.
     */
    public static final void assertJsonEquals(final String expected, final String actual) throws JSONException
    {
        jsonCompare(new JSONObject(expected), new JSONObject(actual), "");
    }

    /**
     * Deep-compare two JSON Objects for equality.
     */
    public static final void assertJsonEquals(final String expected, final String actual, final String message) throws JSONException
    {
        jsonCompare(new JSONObject(expected), new JSONObject(actual), message);
    }

    /**
     * Deep-compare two JSON Objects for equality.
     */
    public static final void assertJsonEquals(final JSONObject expected, final JSONObject actual) throws JSONException
    {
        jsonCompare(expected, actual, "");
    }

    /**
     * Deep-compare two JSON Objects for equality.
     */
    public static final void assertJsonEquals(final JSONObject expected, final JSONObject actual, final String message) throws JSONException
    {
        jsonCompare(expected, actual, message);
    }

    /**
     * Deep-compare two JSON Objects for equality.
     */
    public static final void assertJsonEquals(final JSONArray expected, final JSONArray actual) throws JSONException
    {
        jsonCompare(expected, actual, "");
    }

    public static final void assertJsonEquals(final JSONArray expected, final JSONArray actual, final String message) throws JSONException
    {
        jsonCompare(expected, actual, message);
    }

    protected static final void jsonCompare(final Object leftObj, final Object rightObj, final String message) throws JSONException
    {
        if (leftObj == null) {
            Assert.assertNull(message + " (<null> vs. <non-null>)", rightObj);
            return;
        }
        else {
            Assert.assertNotNull(message + " (<non-null> vs. <null>)", rightObj);

            if (leftObj instanceof JSONObject) {
                Assert.assertTrue(message + " (<JSONObject> vs. <non-JSONObject>)", rightObj instanceof JSONObject);

                final JSONObject left = (JSONObject) leftObj;
                final JSONObject right = (JSONObject) rightObj;

                Assert.assertEquals(message + "(JSONObject size is different)", left.length(), right.length());

                for (Iterator<String> actIt = left.sortedKeys(),expIt = right.sortedKeys() ; actIt.hasNext() && expIt.hasNext(); ) {
                    String leftKey = actIt.next();
                    String rightKey = expIt.next();

                    Assert.assertEquals(message + " (Keys differ)", leftKey, rightKey);

                    if (left.isNull(leftKey)) {
                        Assert.assertTrue(message + " (" + left + ": <null> vs. <non-null>)", right.isNull(rightKey));
                    }
                    else {
                        Assert.assertFalse(message + " (" + left + ": <non-null> vs. <null>)", right.isNull(rightKey));
                    }

                    jsonCompare(left.get(leftKey), right.get(rightKey), message);
                }
            }
            else if (leftObj instanceof JSONArray) {
                Assert.assertTrue(message + " (<JSONArray> vs. <non-JSONArray>)", rightObj instanceof JSONArray);

                final JSONArray left = (JSONArray) leftObj;
                final JSONArray right = (JSONArray) rightObj;

                Assert.assertEquals(message + "(JSONArray size is different)", left.length(), right.length());

                final boolean [] marked = new boolean[left.length()];

                //
                // Deep-compare two JSON Array for equality. This is harder than it looks.
                //
                for (int i = 0 ; i < left.length(); i++) {
                    final Object leftValue = left.get(i);
                    final boolean leftIsNull = left.isNull(i);
                    boolean found = false;
                    for (int j = 0; j < right.length(); j++) {

                        // This field has already been marked as tested.
                        if (marked[j]) {
                            continue;
                        }

                        if (leftIsNull) {
                            Assert.assertTrue(message + " ([" + i + "]: <null> vs. <non-null>)", right.isNull(j));
                        }
                        else {
                            Assert.assertFalse(message + " ([" + i + "]: <non-null> vs. <null>)", right.isNull(j));
                        }

                        if (leftIsNull) {
                            // left and right isNull is the same, and
                            // left is null. So right is null, too.
                            marked[j] = true;
                            found = true;
                            break; // for(int j ...
                        }

                        final Object rightValue = right.get(j);

                        try {
                            jsonCompare(leftValue, rightValue, message);
                        } catch (AssertionFailedError afe) {
                            // They are not equal, next try.
                            continue;
                        }
                        marked[j] = true;
                        found = true;
                        break;
                    }
                    Assert.assertTrue(message + " ([" + i + "] not found)", found);
                }
            }
            else if (leftObj instanceof Number) {
                Assert.assertTrue(message + " (<number> vs. <non-number>)", rightObj instanceof Number);

                if ((leftObj instanceof Integer) || (leftObj instanceof Long)) {
                    Assert.assertEquals(message, ((Number) leftObj).longValue(), ((Number) rightObj).longValue());
                } else {
                    Assert.assertEquals(message, ((Number) leftObj).doubleValue(), ((Number) rightObj).doubleValue(), 0.000001);
                }

            }
            else if (leftObj instanceof CharSequence) {
                Assert.assertTrue(message + " (<string> vs. <non-string>)", rightObj instanceof CharSequence);
                Assert.assertEquals(message, leftObj.toString(), rightObj.toString());
            }
            else  {
                Assert.assertEquals(message, leftObj, rightObj);
            }
        }
    }
}

