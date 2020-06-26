/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.java.spanner;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LibraryFunctionsTest {
  
  @Test
  public void itemTypesCheck() {
    List<String> actual = LibraryFunctions.getItemTypes(0);
    List<String> expected = Arrays.asList("CEREAL", "MILK", "WATER");
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void userListsCheck() {
    List<List<String>> actual = LibraryFunctions.getUserLists(1);
    List<List<String>> expected = Arrays.asList(Arrays.asList("Milk", "Eggs", "Bread"));
    Assert.assertEquals(actual, expected);
  }
}