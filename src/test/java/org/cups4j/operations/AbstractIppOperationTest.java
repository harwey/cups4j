/*
 * Copyright (c) 2018-2026 by Oliver Boehm
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
 *
 * (c)reated 23.03.2018 by oboehm (ob@oasd.de)
 */
package org.cups4j.operations;

import ch.ethz.vppserver.ippclient.IppResponse;
import ch.ethz.vppserver.ippclient.IppResult;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.ipp.attributes.Attribute;
import org.cups4j.ipp.attributes.AttributeGroup;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Klasse AbstractIppOperationTest.
 *
 * @author oboehm
 * @since x.x (23.03.2018)
 */
public abstract class AbstractIppOperationTest {
    
    private static final String TEST_PRINTER_NAME = "test-printer";
    protected final String printerName = System.getProperty("printer", TEST_PRINTER_NAME);
    protected final String userName = "admin";
    protected final CupsAuthentication creds = new CupsAuthentication(this.userName, "admin");

    @BeforeClass
    public static void requireDocker() {
        Assume.assumeTrue(
                "Docker is not running",
                DockerClientFactory.instance().isDockerAvailable()
        );
    }

    @Rule
    public GenericContainer cups = new GenericContainer(DockerImageName.parse("ydkn/cups")) //
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("container"))) //
            .withExposedPorts(CupsClient.DEFAULT_PORT) //
            .waitingFor(Wait.forHttp("/"));
    protected CupsClient client;
   
    protected ByteBuffer getIppHeader(IppOperation operation) throws UnsupportedEncodingException {
        URI printerURL = this.getPrinterURL();
        Map<String, String> attributes = setUpAttributes();
        return operation.getIppHeader(printerURL, attributes);
    }

    protected static Map<String, String> setUpAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("requested-attributes", "copies-supported page-ranges-supported printer-name " +
                "printer-info printer-location printer-make-and-model printer-uri-supported media-supported " +
                "media-default sides-supported sides-default orientation-requested-supported " +
                "printer-resolution-supported printer printer-resolution-default number-up-default " +
                "number-up-supported document-format-supported print-color-mode-supported print-color-mode-default " +
                "multiple-operation-time-out multiple-document-jobs-supported multiple-document-handling " +
                "multiple-document-handling-supported");
        attributes.put("job-attributes", "copies:integer:1#orientation-requested:enum:3#output-mode:keyword:monochrome#" +
                "job-state-reasons:keyword:incoming#job-id:integer:815");
        attributes.put("job-name", "testJUCW5V");
        attributes.put("document-name", "test-document");
        return attributes;
    }

    protected URL createURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("not a URL: " + url, ex);
        }
    }
    
    protected URI getPrinterURL() {
        try {
            return new URL("http", cups.getHost(), cups.getFirstMappedPort(), "/printers/" + this.printerName).toURI();
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL: ", ex);
        }
    }

    protected static void checkAttribute(ByteBuffer buffer, String name, String expectedValue) {
        IppResponse ippResponse = new IppResponse();
        try {
            buffer.rewind();
            IppResult ippResult = ippResponse.getResponse(buffer);
            for (AttributeGroup group : ippResult.getAttributeGroupList()) {
                for (Attribute attr : group.getAttribute()) {
                    if (name.equals(attr.getName())) {
                        String value = attr.getValue();
                        assertEquals(expectedValue, value);
                        return;
                    }
                }
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("invalid ByteBuffer " + buffer, ioe);
        }
        fail("Attribute '" + name + "' not found.");
    }
    
    @Before
    public void setUp() throws Exception {
        createTestPrinters();
    
        this.client = new CupsClient(this.cups.getHost(), this.cups.getFirstMappedPort(), this.userName, this.creds);
    }
    
    protected void createTestPrinters() throws IOException, InterruptedException {
        this.cups.execInContainer("lpadmin", "-p", this.printerName, "-E", "-v", "socket://localhost:9100", "-o", "printer-is-shared=true");
        this.cups.execInContainer("lpadmin", "-d", this.printerName);
    }
    
    protected CupsPrinter getPrinter() {
        try {
            for (CupsPrinter printer : this.client.getPrinters()) {
                if (this.printerName.equalsIgnoreCase(printer.getName())) {
                    return printer;
                }
            }
            throw new IllegalArgumentException(String.format("printer %s not found", this.printerName));
        } catch (Exception ex) {
            throw new IllegalStateException("cannot get printers", ex);
        }
    }
}
