# Test Architecture and Conventions

The CTS tests are in the `src/test/` tree under the cts-java module. There is nothing in the `src\main` subtree, since the infrastructure to support and execute the tests is in the `ctk-cli` module. The CTS tests are controlled by command line invocation of the  `ctk-cli` jar, or by maven execution of `cts-java` goals, or directly as JUnit tests from your IDE.

Tests are JUnit tests - there are test classes, and in each class there are one or more test methods. The test methods are executed in an unspecified order, so the classes can (optionally) annotate methods to run `@Before` and `@After` each method to set up or tear down any test fixtures. Each test class also has a `@BeforeClass` and `@AfterClass` annotation available to distinguish a method to run once, before any tests or after all tests complete.



## Test grouping into Test Classes

Tests are grouped by "API" - e.g., the Reads, Variants, or References API. As a convention, each API gets a specific java test package, with a couple markers for test management. So, for the Reads API we have
`org.ga4gh.cts.api.reads` and in that we have a marker interfaces for the tests, and a class to bring together the ReadsTestSuite:

```java

    ReadsTests.java:
    public interface ReadsTests { /* category marker */ }

    ReadsTestSuite.java:
    @RunWith(WildcardPatternSuite.class)
    @IncludeCategories({CoreTests.class, ReadsTests.class})
    @SuiteClasses({"**/*IT.class", "**/*Test.class"})
    public class ReadsTestSuite {}

```

Note that here the `ReadsTestSuite` is configured to select all the test classes with an IT or Test name suffix, and from that group to include the tests marked with the `CoreTests` or the `ReadsTests` markers. You define markers and Suites in code, to suit yourself.

Each API has a ProtocolClient class which is responsible for getting requests to the endpoints in the API and getting responses back. So, for example, there is a ReadsProtocolClient, which exposes methods such as `searchReads()` taking a `GASearchReadsRequest` and returning a `GASearchReadsResponse` object. A normal CTS test uses a @BeforeClass method to acquire the appropriate ProtocolClient as a class variable named 'client.'

```java

    private static ReadsProtocolClient client;

    @BeforeClass
    public static void
    setupTransport() throws Exception {client = new ReadsProtocolClient();}

```

The methods such as `searchRead(...) can generally take an optional WireTracker object; if it included, then when the method returns the provided WireTracker will be filled out with the actual "on the wire" JSON and the RespCode, for detailed evaluation.

**TODO** add the entire HttpResponse to the WireTracker for future assertions about returned header data, cookies, etc.

Each test class also hooks into the logging framework, with a class static:

    private static org.slf4j.Logger log = getLogger(ReadGroupSetsSearchIT.class);

Test classes provide the normal object-oriented coherence/coupling tradeoffs: put methods in the same test class if they illuminate aspects of the same feature of the test server. For example, the CTS presently has a <api>MethodsEndpointAlive class to hold the basic liveness tests for each endpoint in an API (can we send a default request to each defined endpoint and get back a sane response?) while the detailed validation of data content in response to specific queries would go into other classes.

### Runners

When JUnit is told to run a class as a test class, it uses a "Runner" to do this; there is a default Runner, but there are alternatives which give us varying extra capabilities ... but not all Runners have all capabilities. So, one way that test methods get grouped into different test classes is by the Runners they use. For example, the default JUnit Runner doesn't support parameterizing test methods with externally-supplied data, so we use a special runner by annotating the test class:

```java
    @RunWith(JUnitParamsRunner.class)
    public class ReadGroupSetsSearchIT { // ... }
```

The CTS currently uses these Runners:

- [JUnitParams](https://github.com/Pragmatists/junitparams)
- [BlockJUnit4ClassRunner](http://junit.sourceforge.net/javadoc/org/junit/runners/BlockJUnit4ClassRunner.html) (default, used if no `@RunWith()` annotation)

### Marking Test methods

Within a test class, methods to be run as actual tests are marked with a @Test annotation; the standard JUnit runner requires the method take no parameters, while the JUnitParamsRunner allows parameters, e.g.,

```java

    @Test
    @Parameters({
        // "In the testdataset 1kg-phase1, a query for all variants on chr22
        // between coordinates 16050408 and 16052159 should have exactly 16 results" -- Jeltje
        //
        "1kg-phase1, 22, 16050408, 16052159, 16"
    })
    public void searchVariantsRequestResultSizeAsExpected(String vsetIds, String refName, long start, long end, int expLength) throws Exception { /* ... */ }

```