# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Changed

- at least Java 11 required
- URL parameters are now deprecated and replaced by URI parameters

### Added

- changelog added
  ([issue #81](https://github.com/harwey/cups4j/issues/81))
- testcontainers is used for unit testing
  ([PR #65](https://github.com/harwey/cups4j/pull/65), [PR #90](https://github.com/harwey/cups4j/pull/90))
- PrintJob.Builder has now an attribute method to build attributes
  (added with [issue #73](https://github.com/harwey/cups4j/issues/73))

### Fixed

- the following job operations can now handle non-standard IPP ports
  - CupsMoveJobOperation
  - IppHoldJobOperation
  - IppReleaseJobOperation


## [0.8.0] (20-Mar-2026)

### Changed

- Java 8 required
- switched from simple-xml to JAXB
  ([issue #30](https://github.com/harwey/cups4j/issues/30))


### Added

- HTTPS/IPPS support added
  ([issue #45](https://github.com/harwey/cups4j/issues/45),
   [issue #76](https://github.com/harwey/cups4j/issues/76),
   [issue #79](https://github.com/harwey/cups4j/issues/79))

### Fixed

- conflict with Apache HTTP library fixed, update to `org.apache.httpcomponents.client5:httpclient5`
  ([issue #32](https://github.com/harwey/cups4j/issues/32),
   [issue #74](https://github.com/harwey/cups4j/issues/74),
   [issue #75](https://github.com/harwey/cups4j/issues/75))
- PrinterStateEnum.getState() returning null
  ([issue #52](https://github.com/harwey/cups4j/issues/52), [issue #62](https://github.com/harwey/cups4j/issues/62))
- duplex setting can be switched off
  ([issue #60](https://github.com/harwey/cups4j/issues/60))
