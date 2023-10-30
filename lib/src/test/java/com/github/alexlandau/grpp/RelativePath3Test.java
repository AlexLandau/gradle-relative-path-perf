/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.alexlandau.grpp;

import org.junit.Test;
// import spock.lang.Issue;

import java.io.File;

import static com.github.alexlandau.grpp.Matchers.strictlyEqual;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RelativePath3Test {

    private void assertPathContains(RelativePath3 path, boolean isFile, String... expectedSegments) {
        String[] actualPaths = path.getSegments();
        assertArrayEquals(expectedSegments, actualPaths);
        assertEquals(isFile, path.isFile());
    }

    @Test
    public void testConstructors() {
        RelativePath3 path;
        path = new RelativePath3(true, "one");
        assertPathContains(path, true, "one");

        path = new RelativePath3(false, "one", "two");
        assertPathContains(path, false, "one", "two");
    }

    @Test
    public void appendPath() {
        RelativePath3 childPath = new RelativePath3(false, "one", "two").append(new RelativePath3(true, "three", "four"));
        assertPathContains(childPath, true, "one", "two", "three", "four");

        childPath = new RelativePath3(false, "one", "two").append(new RelativePath3(true));
        assertPathContains(childPath, true, "one", "two");

        childPath = new RelativePath3(false, "one", "two").plus(new RelativePath3(true, "three"));
        assertPathContains(childPath, true, "one", "two", "three");
    }

    @Test
    public void appendNames() {
        RelativePath3 childPath = new RelativePath3(false, "one", "two").append(true, "three", "four");
        assertPathContains(childPath, true, "one", "two", "three", "four");

        childPath = new RelativePath3(false, "one", "two").append(true);
        assertPathContains(childPath, true, "one", "two");
    }

    @Test
    public void prependNames() {
        RelativePath3 childPath = new RelativePath3(false, "one", "two").prepend("three", "four");
        assertPathContains(childPath, false, "three", "four", "one", "two");

        childPath = new RelativePath3(false, "one", "two").prepend();
        assertPathContains(childPath, false, "one", "two");
    }

    @Test
    public void hasWellBehavedEqualsAndHashCode() {
        assertThat(new RelativePath3(true), strictlyEqual(new RelativePath3(true)));
        assertThat(new RelativePath3(true, "one"), strictlyEqual(new RelativePath3(true, "one")));
        assertThat(new RelativePath3(false, "one", "two"), strictlyEqual(new RelativePath3(false, "one", "two")));

        assertThat(new RelativePath3(true, "one"), not(equalTo(new RelativePath3(true, "two"))));
        assertThat(new RelativePath3(true, "one"), not(equalTo(new RelativePath3(true, "one", "two"))));
        assertThat(new RelativePath3(true, "one"), not(equalTo(new RelativePath3(false, "one"))));
    }

    @Test
    public void canParsePathIntoRelativePath3() {
        RelativePath3 path;

        path = RelativePath3.parse(true, "one");
        assertPathContains(path, true, "one");

        path = RelativePath3.parse(true, "one/two");
        assertPathContains(path, true, "one", "two");

        path = RelativePath3.parse(true, "one/two/");
        assertPathContains(path, true, "one", "two");

        path = RelativePath3.parse(true, String.format("one%stwo%s", File.separator, File.separator));
        assertPathContains(path, true, "one", "two");

        path = RelativePath3.parse(false, "");
        assertPathContains(path, false);

        path = RelativePath3.parse(false, "/");
        assertPathContains(path, false);

        path = RelativePath3.parse(true, "/one");
        assertPathContains(path, true, "one");

        path = RelativePath3.parse(true, "/one/two");
        assertPathContains(path, true, "one", "two");
    }

    // @Issue("https://github.com/gradle/gradle/issues/5748")
    @Test
    public void ignoresSingleDotSegments() {
        RelativePath3 path;

        path = new RelativePath3(false, "one", ".");
        assertPathContains(path, false, "one");

        path = new RelativePath3(true, ".", "one");
        assertPathContains(path, true, "one");

        path = new RelativePath3(true, "one", ".", ".", "two");
        assertPathContains(path, true, "one", "two");

        path = new RelativePath3(false, ".");
        assertPathContains(path, false);

        // Test with append()
        path = new RelativePath3(false, "one").append(false, ".");
        assertPathContains(path, false, "one");
    }

    // @Issue("https://github.com/gradle/gradle/issues/5748")
    @Test
    public void canonicalizesDoubleDotSegments() {
        RelativePath3 path;

        // Leading ".." entries are left intact
        path = new RelativePath3(false, "..");
        assertPathContains(path, false, "..");

        path = new RelativePath3(false, "..", "one");
        assertPathContains(path, false, "..", "one");

        path = new RelativePath3(false, "..", "..");
        assertPathContains(path, false, "..", "..");

        // Non-leading ".." entries "cancel out" parent directories
        path = new RelativePath3(false, "one", "..");
        assertPathContains(path, false);

        path = new RelativePath3(false, "..", "one", "..", "..");
        assertPathContains(path, false, "..", "..");

        path = new RelativePath3(false, "one", "..", "two");
        assertPathContains(path, false, "two");

        path = new RelativePath3(false, "one", "two", "..");
        assertPathContains(path, false, "one");

        path = new RelativePath3(false, "one", "..", "..", "two");
        assertPathContains(path, false, "..", "two");

        path = new RelativePath3(false, "..", "one", "two", "..");
        assertPathContains(path, false, "..", "one");

        path = new RelativePath3(false, "one", "two", "three", "..", "four", "..", "..", "five");
        assertPathContains(path, false, "one", "five");

        path = new RelativePath3(false, "one", ".", "..", "two");
        assertPathContains(path, false, "two");

        path = new RelativePath3(false, ".", "..", "one");
        assertPathContains(path, false, "..", "one");

        // Test with append()
        path = new RelativePath3(false, "one", "two").append(false, "..");
        assertPathContains(path, false, "one");
    }

    @Test
    public void canGetParentOfPath() {
        assertThat(new RelativePath3(true, "a", "b").getParent(), equalTo(new RelativePath3(false, "a")));
        assertThat(new RelativePath3(false, "a", "b").getParent(), equalTo(new RelativePath3(false, "a")));
        assertThat(new RelativePath3(false, "a").getParent(), equalTo(new RelativePath3(false)));
        assertThat(new RelativePath3(false).getParent(), nullValue());
    }

    @Test
    public void canReplaceLastName() {
        assertPathContains(new RelativePath3(true, "old").replaceLastName("new"), true, "new");
        assertPathContains(new RelativePath3(false, "old").replaceLastName("new"), false, "new");
        assertPathContains(new RelativePath3(true, "a", "b", "old").replaceLastName("new"), true, "a", "b", "new");
    }

    @Test
    public void testLength() {
        assertEquals(0, RelativePath3.parse(true, "").length());
        assertEquals(7, RelativePath3.parse(true, "/one/two").length());
    }

    @Test
    public void testExistingCharAt() {
        RelativePath3 path = RelativePath3.parse(true, "/one/two");
        assertEquals('o', path.charAt(0));
        assertEquals('/', path.charAt(3));
        assertEquals('t', path.charAt(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeCharAt() {
        RelativePath3 path = RelativePath3.parse(true, "/one/two");
        path.charAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testTooLargeCharAt() {
        RelativePath3 path = RelativePath3.parse(true, "/one/two");
        path.charAt(25);
    }
}
