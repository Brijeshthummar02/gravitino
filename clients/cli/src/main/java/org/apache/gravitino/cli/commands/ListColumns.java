/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.gravitino.cli.commands;

import org.apache.gravitino.NameIdentifier;
import org.apache.gravitino.cli.CommandContext;
import org.apache.gravitino.cli.ErrorMessages;
import org.apache.gravitino.exceptions.NoSuchTableException;
import org.apache.gravitino.rel.Column;
import com.google.common.base.Joiner;

/** Displays the details of a table's columns. */
public class ListColumns extends TableCommand {

  protected final String schema;
  protected final String table;

  /**
   * Displays the details of a table's columns.
   *
   * @param context The command context.
   * @param metalake The name of the metalake.
   * @param catalog The name of the catalog.
   * @param schema The name of the schenma.
   * @param table The name of the table.
   */
  public ListColumns(
      CommandContext context, String metalake, String catalog, String schema, String table) {
    super(context, metalake, catalog);
    this.schema = schema;
    this.table = table;
  }

    /** Displays the details of a table's columns. */  
    @Override
    public void handle() {
        try {
            NameIdentifier name = NameIdentifier.of(schema, table);
            Column[] columns = tableCatalog().loadTable(name).columns();

            if (columns != null && columns.length > 0) {
                StringBuilder all = new StringBuilder();
                boolean hasAutoIncrement = false;

                // Check if any column supports auto-increment
                for (Column column : columns) {
                    if (column != null && column.autoIncrement()) {
                        hasAutoIncrement = true;
                        break;
                    }
                }

                all.append("name,datatype,comment,nullable");
                if (hasAutoIncrement) {
                    all.append(",auto_increment");
                }
                all.append(System.lineSeparator());

                for (Column column : columns) {
                    if (column == null) {
                        continue;
                    }

                    StringBuilder columnDetails = new StringBuilder();
                    columnDetails.append(column.name()).append(",");
                    columnDetails.append(column.dataType() != null ? column.dataType().simpleString() : "UNKNOWN").append(",");
                    columnDetails.append(column.comment() != null ? column.comment() : "N/A").append(",");
                    columnDetails.append(column.nullable() ? "true" : "false");

                    if (hasAutoIncrement) {
                        columnDetails.append(",").append(column.autoIncrement() ? "true" : "");
                    }

                    all.append(columnDetails).append(System.lineSeparator());
                }

                printResults(all.toString());
            } else {
                exitWithError("No columns found for the specified table.");
            }
        } catch (NoSuchTableException noSuchTableException) {
            exitWithError(ErrorMessages.UNKNOWN_TABLE + " " + Joiner.on(".").join(metalake, catalog, schema, table));
        } catch (Exception exp) {
            exitWithError("An error occurred while retrieving column details: " + exp.getMessage());
        }
    }
}
