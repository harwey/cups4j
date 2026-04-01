[![License](https://img.shields.io/badge/License-GNU%20LGPL-blue.svg)](https://github.com/harwey/cups4j/blob/master/LICENSE)
[![Maven Central](https://maven-badges.sml.io/maven-central/org.cups4j/cups4j/badge.svg)](https://maven-badges.sml.io/maven-central/org.cups4j/cups4j)

# Cups4j

``Cups4j`` — the Java printing library for CUPS.

## Table of Contents

* [cups4j](#cups4j)
* [Importing](#importing)
* [Usage](#usage)
  * [Basic usage](#basic-usage)
  * [Connect to custom host](#connect-to-custom-host)
  * [Get specific printer by URL](#get-specific-printer-by-url)
  * [Add extra PrintJob attributes](#add-extra-printjob-attributes)
* [More Infos](#more-infos)
* [License](LICENSE)

## Importing

This library is available on the Maven Central repository, [here](https://mvnrepository.com/artifact/org.cups4j/cups4j).  
To import and use **cups4j** in your project, add the following dependency in your `pom.xml` file:

```maven-pom
<dependency>
    <groupId>org.cups4j</groupId>
    <artifactId>cups4j</artifactId>
    <version>0.8.0</version>
</dependency>
```

## Usage

### Basic usage
```java
CupsClient cupsClient = new CupsClient();
CupsPrinter cupsPrinter = cupsClient.getDefaultPrinter();
InputStream inputStream = new FileInputStream("test-file.pdf");
PrintJob printJob = new PrintJob.Builder(inputStream).build();
PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
```

### Connect to custom host
```java
CupsClient cupsClient = new CupsClient(URI.create("http://127.0.0.1:631"));
```

### Get specific printer by URL
```java
URL printerURL = new URL("http://127.0.0.1:631/printers/printer-name");
CupsPrinter cupsPrinter = cupsClient.getPrinter(printerURL);
```

### Add extra PrintJob attributes
```java
Map<String, String> attributes = new HashMap<>();
attributes.put("compression", "none");
attributes.put("job-attributes", "print-quality:enum:3#fit-to-page:boolean:true#sheet-collate:keyword:collated");

PrintJob printJob = new PrintJob.Builder(bytes)
                                .jobName("job-name")
                                .userName("user-name")
                                .copies(2)
                                .pageRanges("1-3")
                                .duplex(false)
                                .portrait(false)
                                .color(true)
                                .pageFormat("iso-a4")
                                .resolution("300dpi")
                                .attributes(attributes)
                                .build();
```

## More Infos

* Changelog: [CHANGELOG](CHANGELOG.md)
* RFC IPP: [RFC 2910](https://tools.ietf.org/html/rfc2910) and [RFC 8011](https://tools.ietf.org/html/rfc8011)
