/*
 * Copyright (c) 2026 by Oliver Boehm
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.cups4j;

import cups4j.TestCups;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Unit tests for {@link CupsClient} class.
 *
 * @author oboehm
 */
public class CupsClientTest {

  private static final Logger LOG = LoggerFactory.getLogger(CupsClientTest.class);

  private static URI cupsURI;
  private static CupsClient client;

  @BeforeAll
  public static void setUpClient() {
    cupsURI = URI.create(System.getProperty("cups.url", "http://localhost:631"));
    client = new CupsClient(cupsURI);
  }

  private static void assumeCupsAvailable() {
    assumeTrue(TestCups.isOnline(cupsURI.getHost(), cupsURI.getPort()), cupsURI + " not available");
  }

  @Test
  public void getPrinters() throws Exception {
    assumeCupsAvailable();
    List<CupsPrinter> printers = client.getPrinters();
    assertFalse(printers.isEmpty());
    for (CupsPrinter printer : printers) {
      LOG.info("printer: {} [isClass={}]", printer.getName(), printer.isPrinterClass());
      assertThat(printer.getPrinterURI().getPath(), startsWith("/printer"));
    }
  }

  @Test
  public void getDefaultPrinter() throws Exception {
    assumeCupsAvailable();
    CupsPrinter defaultPrinter = client.getDefaultPrinter();
    if (defaultPrinter != null) {
      LOG.info("Default printer of {} is {}.", client, defaultPrinter);
      List<CupsPrinter> printers = client.getPrinters();
      assertThat(printers, hasItem(defaultPrinter));
    } else {
      LOG.info("No default printer found for {}.", client);
    }
  }

  @Test
  public void testMakeAndModel() throws Exception {
    assumeCupsAvailable();
    List<CupsPrinter> printers = client.getPrinters();
    for (CupsPrinter printer : printers) {
      LOG.info("printer: " + printer.getName() + "[makeAndModel=" + printer.getMakeAndModel() + "]");
    }
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"../admin", "/printers/../admin", "%2e%2e%2fadmin", "//evil.example/x",
      "http://evil.example/x", "/printers/name?x=1", "/printers/name#x", "/", "printers\\name"})
  void resolvePrinterURIRejectsUnsafePaths(String path) {
    assertThrows(IllegalArgumentException.class, () -> client.resolvePrinterURI(path), "path: " + path);
  }

  @ParameterizedTest
  @ValueSource(strings = {"printers/my-printer", "/printers/my-printer"})
  void resolvePrinterURIAcceptsRelativePaths(String path) {
    assertEquals(URI.create("http://cups.example.com:631/printers/my-printer"),
        new CupsClient(URI.create("http://cups.example.com:631")).resolvePrinterURI(path));
  }

  @Test
  void resolvePrinterURIKeepsHostAndPort() {
    CupsClient otherClient = new CupsClient(URI.create("http://other.example.org:666"));
    assertEquals(URI.create("http://other.example.org:666/printers/my-printer"),
        otherClient.resolvePrinterURI("/printers/my-printer"));
  }

}
