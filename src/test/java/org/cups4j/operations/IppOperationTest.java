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
package org.cups4j.operations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the central printer URI creation in {@link IppOperation}.
 */
class IppOperationTest {

  private static final URI CUPS_URI = URI.create("http://cups.example.com:631");

  private static final class TestOperation extends IppOperation {
    URI printerURI(URI cupsURI, String printerName) {
      return toPrinterURI(cupsURI, printerName);
    }
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"bad/name", "../admin", "bad name", "bad#name", "bad?name"})
  void toPrinterURIRejectsInvalidPrinterName(String invalidName) {
    TestOperation operation = new TestOperation();
    assertThrows(IllegalArgumentException.class,
        () -> operation.printerURI(CUPS_URI, invalidName), "invalid printer name: " + invalidName);
  }

  @Test
  void toPrinterURIRejectsTooLongPrinterName() {
    TestOperation operation = new TestOperation();
    assertThrows(IllegalArgumentException.class,
        () -> operation.printerURI(CUPS_URI, "x".repeat(128)));
  }

  @Test
  void toPrinterURIAcceptsMaximumLengthPrinterName() {
    TestOperation operation = new TestOperation();
    String name = "x".repeat(127);
    assertEquals(URI.create("ipp://cups.example.com:631/printers/" + name),
        operation.printerURI(CUPS_URI, name));
  }

  @Test
  void toPrinterURIBuildsExpectedURI() {
    TestOperation operation = new TestOperation();
    assertEquals(URI.create("ipp://cups.example.com:631/printers/my-printer"),
        operation.printerURI(CUPS_URI, "my-printer"));
  }

  @Test
  void toPrinterURIUsesIppsForHttps() {
    TestOperation operation = new TestOperation();
    assertEquals(URI.create("ipps://cups.example.com:631/printers/my-printer"),
        operation.printerURI(URI.create("https://cups.example.com:631"), "my-printer"));
  }

  @Test
  void toPrinterURIEscapesPercentInPrinterName() {
    TestOperation operation = new TestOperation();
    assertEquals(URI.create("ipp://cups.example.com:631/printers/%252e%252e%252fadmin"),
        operation.printerURI(CUPS_URI, "%2e%2e%2fadmin"));
    assertEquals(URI.create("ipp://cups.example.com:631/printers/50%25"),
        operation.printerURI(CUPS_URI, "50%"));
  }

}
