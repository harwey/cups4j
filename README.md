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
* [License](#license)

## Importing

This library is available on the Maven Central repository, [here](https://mvnrepository.com/artifact/org.cups4j/cups4j).  
To import and use **cups4j** in your project, add the following dependency in your `pom.xml` file:

```maven-pom
<dependency>
    <groupId>org.cups4j</groupId>
    <artifactId>cups4j</artifactId>
    <version>0.7.6</version>
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
CupsClient cupsClient = new CupsClient("127.0.0.1", 631);
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

## License
[LGPL](https://github.com/harwey/cups4j/blob/master/LICENSE)
