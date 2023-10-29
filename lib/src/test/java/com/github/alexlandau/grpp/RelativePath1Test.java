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

import java.io.File;

import static com.github.alexlandau.grpp.Matchers.strictlyEqual;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class RelativePath1Test {

    private void assertPathContains(RelativePath1 path, boolean isFile, String... expectedSegments) {
        String[] actualPaths = path.getSegments();
        assertArrayEquals(expectedSegments, actualPaths);
        assertEquals(isFile, path.isFile());
    }

    @Test
    public void testConstructors() {
        RelativePath1 path;
        path = new RelativePath1(true, "one");
        assertPathContains(path, true, "one");

        path = new RelativePath1(false, "one", "two");
        assertPathContains(path, false, "one", "two");
    }

    @Test
    public void appendPath() {
        RelativePath1 childPath = new RelativePath1(false, "one", "two").append(new RelativePath1(true, "three", "four"));
        assertPathContains(childPath, true, "one", "two", "three", "four");

        childPath = new RelativePath1(false, "one", "two").append(new RelativePath1(true));
        assertPathContains(childPath, true, "one", "two");

        childPath = new RelativePath1(false, "one", "two").plus(new RelativePath1(true, "three"));
        assertPathContains(childPath, true, "one", "two", "three");
    }

    @Test
    public void appendNames() {
        RelativePath1 childPath = new RelativePath1(false, "one", "two").append(true, "three", "four");
        assertPathContains(childPath, true, "one", "two", "three", "four");

        childPath = new RelativePath1(false, "one", "two").append(true);
        assertPathContains(childPath, true, "one", "two");
    }

    @Test
    public void prependNames() {
        RelativePath1 childPath = new RelativePath1(false, "one", "two").prepend("three", "four");
        assertPathContains(childPath, false, "three", "four", "one", "two");

        childPath = new RelativePath1(false, "one", "two").prepend();
        assertPathContains(childPath, false, "one", "two");
    }

    @Test
    public void hasWellBehavedEqualsAndHashCode() {
        assertThat(new RelativePath1(true), strictlyEqual(new RelativePath1(true)));
        assertThat(new RelativePath1(true, "one"), strictlyEqual(new RelativePath1(true, "one")));
        assertThat(new RelativePath1(false, "one", "two"), strictlyEqual(new RelativePath1(false, "one", "two")));

        assertThat(new RelativePath1(true, "one"), not(equalTo(new RelativePath1(true, "two"))));
        assertThat(new RelativePath1(true, "one"), not(equalTo(new RelativePath1(true, "one", "two"))));
        assertThat(new RelativePath1(true, "one"), not(equalTo(new RelativePath1(false, "one"))));
    }

    @Test
    public void canParsePathIntoRelativePath1() {
        RelativePath1 path;

        path = RelativePath1.parse(true, "one");
        assertPathContains(path, true, "one");

        path = RelativePath1.parse(true, "one/two");
        assertPathContains(path, true, "one", "two");

        path = RelativePath1.parse(true, "one/two/");
        assertPathContains(path, true, "one", "two");

        path = RelativePath1.parse(true, String.format("one%stwo%s", File.separator, File.separator));
        assertPathContains(path, true, "one", "two");

        path = RelativePath1.parse(false, "");
        assertPathContains(path, false);

        path = RelativePath1.parse(false, "/");
        assertPathContains(path, false);

        path = RelativePath1.parse(true, "/one");
        assertPathContains(path, true, "one");

        path = RelativePath1.parse(true, "/one/two");
        assertPathContains(path, true, "one", "two");
    }

    @Test
    public void canGetParentOfPath() {
        assertThat(new RelativePath1(true, "a", "b").getParent(), equalTo(new RelativePath1(false, "a")));
        assertThat(new RelativePath1(false, "a", "b").getParent(), equalTo(new RelativePath1(false, "a")));
        assertThat(new RelativePath1(false, "a").getParent(), equalTo(new RelativePath1(false)));
        assertThat(new RelativePath1(false).getParent(), nullValue());
    }

    @Test
    public void canReplaceLastName() {
        assertPathContains(new RelativePath1(true, "old").replaceLastName("new"), true, "new");
        assertPathContains(new RelativePath1(false, "old").replaceLastName("new"), false, "new");
        assertPathContains(new RelativePath1(true, "a", "b", "old").replaceLastName("new"), true, "a", "b", "new");
    }

    @Test
    public void testLength() {
        assertEquals(0, RelativePath1.parse(true, "").length());
        assertEquals(7, RelativePath1.parse(true, "/one/two").length());
    }

    @Test
    public void testExistingCharAt() {
        RelativePath1 path = RelativePath1.parse(true, "/one/two");
        assertEquals('o', path.charAt(0));
        assertEquals('/', path.charAt(3));
        assertEquals('t', path.charAt(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeCharAt() {
        RelativePath1 path = RelativePath1.parse(true, "/one/two");
        path.charAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testTooLargeCharAt() {
        RelativePath1 path = RelativePath1.parse(true, "/one/two");
        path.charAt(25);
    }
}
