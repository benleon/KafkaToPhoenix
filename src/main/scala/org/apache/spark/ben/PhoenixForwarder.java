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
package org.apache.spark.ben;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.phoenix.jdbc.PhoenixDriver;

/**
 * @author bleonhardi
 * Simple Phoenix forwarder class using Prepared Statements.
 * Commit is set after each partition to increase performance.
 */
public class PhoenixForwarder implements Serializable {


	private static final long serialVersionUID = 1L;
	String connectionString = "jdbc:phoenix:sandbox.hortonworks.com:2181:/hbase-unsecure:hbase";
	Connection con = null;

	PreparedStatement prepSales = null;
	PreparedStatement prepAgg = null;

	public PhoenixForwarder() {
		createConnection();
	}

	public void createConnection() {
		try {
			Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
			DriverManager.registerDriver(new PhoenixDriver());
			con = DriverManager.getConnection(connectionString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendSales(Row row) {
		try {

			if (prepSales == null) {

				String statement = "UPSERT INTO SALES VALUES ( ?, ? , ? , ?, ? , ? )";
				this.prepSales = this.con.prepareStatement(statement);

			}
			prepSales.setInt(1, row.id());
			prepSales.setDate(2, new java.sql.Date(row.date().getTime()));
			prepSales.setString(3, row.firstName());
			prepSales.setString(4, row.lastName());
			prepSales.setString(5, row.product());
			prepSales.setDouble(6, row.amount());
			prepSales.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void sendAgg(String product, Date date, double amount) {
		try {
			if (prepAgg == null) {
				String statement = "UPSERT INTO PRODUCTSALES VALUES ( ?, ?, ? )";
				this.prepAgg = this.con.prepareStatement(statement);
			}
			prepAgg.setString(1, product);
			prepAgg.setDate(2, new java.sql.Date(date.getTime()));
			prepAgg.setDouble(3, amount);
			prepAgg.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public synchronized void closeCon() {
		try {
			if (con != null)
				con.commit();
				con.close();
			if (prepSales != null)
				prepSales.close();
			if (prepAgg != null)
				prepAgg.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
