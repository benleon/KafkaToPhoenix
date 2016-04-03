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
import java.util.Date;

/**
 * A parsed row object. 
 * Creating a proper object for Scala objects adds a bit of work to the Spark 
 * applications but results in TypeSafety and is good best practice.
 * Note that it has to be Serializable since it will be sent over the network 
 * during repartitions collects etc.
 */
class Row extends Serializable{
  var id:Int = 0;
  var date:Date = null;
  var firstName:String = null;
  var lastName:String = null;
  var product:String = null
  var amount:Double = 0.0d;

  override def toString():String =
  {
    return id.toString + "|" + date + "|" + firstName + "|" + lastName + "|" + amount.toString;
    
  }
}