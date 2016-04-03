/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.ben

/**
 * Simple Parser object, you ciuld do anything here.
 * Needs to be instantiated once for each Partition 
 * SimpleDateFormat is too costly to instantiate for each row
 */
class Parser {
  
    val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    def parse(row:String): Row =
    {
      var parsed = new Row();
      var columns = row.split("[|]");
      parsed.id = columns(0).toInt;
      parsed.date = dateFormat.parse(columns(1));
      parsed.firstName = columns(2);
      parsed.lastName = columns(3);
      parsed.product = columns(4);
      parsed.amount = columns(5).toDouble;
      return parsed;
    }
}