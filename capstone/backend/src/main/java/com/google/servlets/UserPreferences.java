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

package com.google.servlets;

import java.util.Arrays;
import java.util.List;

public class UserPreferences {
  private double latitude;
  private double longitude;
  private int distancePreference;
  private List<String> selectedItemTypes;

  public List<String> getSelectedItemTypes() {
    return selectedItemTypes;
  }

  public int getDistancePreference() {
    return distancePreference;
  }

  public List<Double> getLocation() {
    return Arrays.asList(longitude, latitude);
  }

  public List<String> getSelectedItems() {
    return selectedItemTypes;
  }
}
