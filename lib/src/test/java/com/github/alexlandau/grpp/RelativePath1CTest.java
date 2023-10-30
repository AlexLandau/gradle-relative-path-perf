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

public class RelativePath1CTest {

    private void assertPathContains(RelativePath1C path, boolean isFile, String... expectedSegments) {
        String[] actualPaths = path.getSegments();
        assertArrayEquals(expectedSegments, actualPaths);
        assertEquals(isFile, path.isFile());
    }

    @Test
    public void testConstructors() {
        RelativePath1C path;
        path = new RelativePath1C(true, "one");
        assertPathContains(path, true, "one");

        path = new RelativePath1C(false, "one", "two");
        assertPathContains(path, false, "one", "two");
    }

    @Test
    public void appendPath() {
        RelativePath1C childPath = new RelativePath1C(false, "one", "two").append(new RelativePath1C(true, "three", "four"));
        assertPathContains(childPath, true, "one", "two", "three", "four");

        childPath = new RelativePath1C(false, "one", "two").append(new RelativePath1C(true));
        assertPathContains(childPath, true, "one", "two");

        childPath = new RelativePath1C(false, "one", "two").plus(new RelativePath1C(true, "three"));
        assertPathContains(childPath, true, "one", "two", "three");
    }

    @Test
    public void appendNames() {
        RelativePath1C childPath = new RelativePath1C(false, "one", "two").append(true, "three", "four");
        assertPathContains(childPath, true, "one", "two", "three", "four");

        childPath = new RelativePath1C(false, "one", "two").append(true);
        assertPathContains(childPath, true, "one", "two");
    }

    @Test
    public void prependNames() {
        RelativePath1C childPath = new RelativePath1C(false, "one", "two").prepend("three", "four");
        assertPathContains(childPath, false, "three", "four", "one", "two");

        childPath = new RelativePath1C(false, "one", "two").prepend();
        assertPathContains(childPath, false, "one", "two");
    }

    @Test
    public void hasWellBehavedEqualsAndHashCode() {
        assertThat(new RelativePath1C(true), strictlyEqual(new RelativePath1C(true)));
        assertThat(new RelativePath1C(true, "one"), strictlyEqual(new RelativePath1C(true, "one")));
        assertThat(new RelativePath1C(false, "one", "two"), strictlyEqual(new RelativePath1C(false, "one", "two")));

        assertThat(new RelativePath1C(true, "one"), not(equalTo(new RelativePath1C(true, "two"))));
        assertThat(new RelativePath1C(true, "one"), not(equalTo(new RelativePath1C(true, "one", "two"))));
        assertThat(new RelativePath1C(true, "one"), not(equalTo(new RelativePath1C(false, "one"))));
    }

    @Test
    public void canParsePathIntoRelativePath1C() {
        RelativePath1C path;

        path = RelativePath1C.parse(true, "one");
        assertPathContains(path, true, "one");

        path = RelativePath1C.parse(true, "one/two");
        assertPathContains(path, true, "one", "two");

        path = RelativePath1C.parse(true, "one/two/");
        assertPathContains(path, true, "one", "two");

        path = RelativePath1C.parse(true, String.format("one%stwo%s", File.separator, File.separator));
        assertPathContains(path, true, "one", "two");

        path = RelativePath1C.parse(false, "");
        assertPathContains(path, false);

        path = RelativePath1C.parse(false, "/");
        assertPathContains(path, false);

        path = RelativePath1C.parse(true, "/one");
        assertPathContains(path, true, "one");

        path = RelativePath1C.parse(true, "/one/two");
        assertPathContains(path, true, "one", "two");
    }

    @Test
    public void canGetParentOfPath() {
        assertThat(new RelativePath1C(true, "a", "b").getParent(), equalTo(new RelativePath1C(false, "a")));
        assertThat(new RelativePath1C(false, "a", "b").getParent(), equalTo(new RelativePath1C(false, "a")));
        assertThat(new RelativePath1C(false, "a").getParent(), equalTo(new RelativePath1C(false)));
        assertThat(new RelativePath1C(false).getParent(), nullValue());
    }

    @Test
    public void canReplaceLastName() {
        assertPathContains(new RelativePath1C(true, "old").replaceLastName("new"), true, "new");
        assertPathContains(new RelativePath1C(false, "old").replaceLastName("new"), false, "new");
        assertPathContains(new RelativePath1C(true, "a", "b", "old").replaceLastName("new"), true, "a", "b", "new");
    }

    @Test
    public void testLength() {
        assertEquals(0, RelativePath1C.parse(true, "").length());
        assertEquals(7, RelativePath1C.parse(true, "/one/two").length());
    }

    @Test
    public void testExistingCharAt() {
        RelativePath1C path = RelativePath1C.parse(true, "/one/two");
        assertEquals('o', path.charAt(0));
        assertEquals('/', path.charAt(3));
        assertEquals('t', path.charAt(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeCharAt() {
        RelativePath1C path = RelativePath1C.parse(true, "/one/two");
        path.charAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testTooLargeCharAt() {
        RelativePath1C path = RelativePath1C.parse(true, "/one/two");
        path.charAt(25);
    }
}
