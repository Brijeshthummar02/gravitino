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

import org.apache.gravitino.CatalogChange;
import org.apache.gravitino.cli.ErrorMessages;
import org.apache.gravitino.client.GravitinoClient;
import org.apache.gravitino.exceptions.NoSuchCatalogException;
import org.apache.gravitino.exceptions.NoSuchMetalakeException;

/** Remove a property of a catalog. */
public class RemoveCatalogProperty extends Command {

  protected final String metalake;
  protected final String catalog;
  protected final String property;

  /**
   * Remove a property of a catalog.
   *
   * @param url The URL of the Gravitino server.
   * @param ignoreVersions If true don't check the client/server versions match.
   * @param metalake The name of the metalake.
   * @param catalog The name of the catalog.
   * @param property The name of the property.
   */
  public RemoveCatalogProperty(
      String url, boolean ignoreVersions, String metalake, String catalog, String property) {
    super(url, ignoreVersions);
    this.metalake = metalake;
    this.catalog = catalog;
    this.property = property;
  }

  /** Remove a property of a catalog. */
  @Override
  public void handle() {
    try {
      GravitinoClient client = buildClient(metalake);
      CatalogChange change = CatalogChange.removeProperty(property);
      client.alterCatalog(catalog, change);
    } catch (NoSuchMetalakeException err) {
      exitWithError(ErrorMessages.UNKNOWN_METALAKE);
    } catch (NoSuchCatalogException err) {
      exitWithError(ErrorMessages.UNKNOWN_CATALOG);
    } catch (Exception exp) {
      exitWithError(exp.getMessage());
    }

    System.out.println(property + " property removed.");
  }
}
