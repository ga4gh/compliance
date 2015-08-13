# Conformance Test Kit (CTK)/Compliance Test Suite (CTS) for a GA4GH Server

----------

## Purpose
A Conformance Test Kit for evaluating a GA4GH Server against the data and messages defined
in the IDL of the [schemas](https://github.com/ga4gh/schemas) repository. CTK tests are currently
written as JUnit/Java tests using a
fluent assertions library ([AssertJ](http://joel-costigliola.github.io/assertj/)) with assertions
customized to the GA4GH domain objects. Here's an example testcase:

```java

	@Test
    @Parameters({{"low-coverage:HG00533.mapped.ILLUMINA.bwa.CHS.low_coverage.20120522"})
    public void readsResponseMatchesACTGNPattern(String rgid) throws Exception {
        // do a readsearch
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setReadGroupIds(Arrays.asList(rgid))
                .build();
        GASearchReadsResponse grtn = client.searchReads(gsrr);

        for (GAReadAlignment gar : grtn.getAlignments()) {
            assertThat(gar.getAlignedSequence()).isNotNull()
                    .matches("[ACTGN]+");
```

The CTK communicates with a running target server, but it does not manage the target server's lifecycle.
The CTK outputs:

- text files: .txt, .xml from Junit, and .tap from [tap4j](http://tap4j.org/)
([Test Anything Protocol](https://testanything.org/))
- console output (in default config this includes logs, but you can re-configure using
[log4j2](https://logging.apache.org/log4j/2.x/manual/configuration.html))
- HTML files of test results (when tests run under Maven, failure reports also to test source/javadoc )
- HTML 'site' of contributor, dependency reports, source code, javadoc, etc (generated under Maven)

The name "CTK" refers to the test framework and transport layers.
The term "CTS" (for "Compliance Test Suite") refers to the actual server-communicating tests. These tests
are in a Maven module specific to the implementation language (so far, we have only "`cts-java`").


### Use Cases
The CTK has two primary use cases:

- helping a server developer **track against the standard APIs**: for this, we build an executable file
(a Java 'jar') and a 'tests' jar file, and package them in a single ZIP distribution; the executable
jar is controlled either from a command line or from properties files or
environment variables. The jar file includes the stable set of schema/tests, so it is easy to script as
a quick server-sanity check. This approach generates test results to log files (defaulting to console out).
It does not require a build tool, just Java 8.
- helping a developer **create new tests** as they develop new APIs and related server implementations:
for this, the developer uses the CTK under the Maven build tool (Maven 3 and java 8). This works
particularly well in an IDE
with Maven test reporting (as, for example, IntelliJ or Eclipse). See the
 [Getting Started as a test writer](##getting-started-as-a-test-writer) document.


## Functional Status
(August 1, 2015) The CTK runs against a reference server using the v6.0.be171b00 schemas.

(June 22 2015) The CTK is able to execute and report on tests written in Java against the v0.5.1 schema. The collection of tests is currently small, and only attempts to
 use the *reads*, *references*, and *variants* endpoints. The tests presume the server has been configured with a data
 set as described in the [GA4GH API Demo
 instructions](http://ga4gh-reference-implementation.readthedocs.org/en/stable/demo.html). A Maven-capable user can
 generate cross-referenced javadoc and source for the framework and for server tests, and for test results.

## Build Status

The CTK/CTS build status is [![Build Status](https://travis-ci.org/ga4gh/compliance.svg?branch=master)](https://travis-ci.org/ga4gh/compliance)

### Docs

There are several docs (in progress):

- [Installing the CTK](InstallingTheCTK.md)
- [Configuring the CTK](ConfigTheCTK.md)
- [Running Tests from CLI](RunningTests_CLI.md)
- [Running Tests from Maven](RunningTests_maven.md)
- [Running Tests from an IDE](RunningTests_IDE.md)
- [Test Architecture and Conventions](TestArchAndConventions.md)
- [Writing A Test](WritingATest.md)
- [Structure And Rationale for the CTK/CTS](StructureAndRationale.md)
- [Future Steps](FutureSteps.md)




