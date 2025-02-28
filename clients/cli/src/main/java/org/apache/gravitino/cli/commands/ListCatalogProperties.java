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

import java.util.Map;

import org.apache.gravitino.Catalog;
import org.apache.gravitino.cli.CommandContext;
import org.apache.gravitino.cli.ErrorMessages;
import org.apache.gravitino.client.GravitinoClient;
import org.apache.gravitino.exceptions.NoSuchCatalogException;
import org.apache.gravitino.exceptions.NoSuchMetalakeException;

/** List the properties of a catalog. */
public class ListCatalogProperties extends ListProperties {

  protected final String metalake;
  protected final String catalog;

  /**
   * List the properties of a catalog.
   *
   * @param context The command context.
   * @param metalake The name of the metalake.
   * @param catalog The name of the catalog.
   */
  public ListCatalogProperties(CommandContext context, String metalake, String catalog) {
    super(context);
    this.metalake = metalake;
    this.catalog = catalog;
  }

  /** List the properties of a catalog. */
  @Override
  public void handle() {
    Catalog gCatalog = null;

    try (GravitinoClient client = buildClient(metalake)) { // Ensures client is closed
        gCatalog = client.loadCatalog(catalog);
    } catch (NoSuchMetalakeException err) {
        exitWithError(ErrorMessages.UNKNOWN_METALAKE);
        return;
    } catch (NoSuchCatalogException err) {
        exitWithError(ErrorMessages.UNKNOWN_CATALOG);
        return;
    } catch (Exception exp) {
        exitWithError(exp.getMessage());
        return;
    }

    if (gCatalog == null) { // Null check before accessing properties
      exitWithError("Failed to load catalog.");
      return;
  }

  Map<String, String> properties = gCatalog.properties();
  if (properties == null || properties.isEmpty()) {
      exitWithError("No properties found for the catalog.");
      return;
  }
    printProperties(properties);
  }
}
