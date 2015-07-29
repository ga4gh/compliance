# Writing Tests

This covers the mechanics of adding tests to the `cts-java` module.

## Prerequisites
- A reachable GA4GH Server; the CTK framework assumes this is available at the URL provided by the `ctk.tgt.urlRoot` property (e.g., `http://192.168.2.115:8000/v0.5.1/`)
- The CTK source installed (see [Installing The CTK](InstallingTheCTK.md))
- You're familiar with the [CTK Test Architecture and Conventions](TestArchAndConventions.md)

You'll find it's much easier if you are also using maven3 as your runner, so we'll mostly assume that, too.

## Overview

Tests follow the normal JUnit pattern: a Test Class contains test methods, each test method sets up a condition, makes some server interaction, then makes some assertions about the results of the interaction (examining the returned status, or the returned object(s)).

The CTK starts with normal JUnit, then provides two major GA4GH-specific facilities: datatyped-communications with the target server, and flexible assertions about the returned objects. 

### JUnit Test Mechanics

In this section we'll go over the JUnit-level issues.

Let's explore some scenarios:

1. adding a simple test (method) to an existing TestClass
1. adding a data-driven/parameterized test method
1. adding a new TestClass to an existing package or protocol

#### Adding a test method to existing test class
- name can be anything, so describe the end goal in domain terms e.g., "allSequencesShouldMatchPattern"
- annotate the method with @Test
- simplest method signature is `@Test public void <testname>() throws Exception {...}`
- use javadoc to explain the test
- use `log` to report behavior of your test class, use `testlog` to report test results if you want (other than simple pass/fail, those are automatic based on whether you fail any assertions)
- normal JUnit structure: try to structure tests as setup, action, assertion
- Usually you'll want to have just one assertion, and most commonly it will start with `org.assertj.core.api.Assertions.assertThat(`*actual result*`).`...; but in some cases you can fruitfully use "`SoftAssertion`" facility to report multiple failures in one test. 

#### Adding a data-driven/parameterized test method
- your test class needs to be run with `@RunWith(JUnitParamsRunner.class)`
- annotate test method with one of the forms of `@Parameter`, and add the parameters to the test method signature (for lots of options here, see the doc at https://github.com/Pragmatists/JUnitParams/wiki/Quickstart)
- consider using `@TestCaseName(...)` to make better test case names (see doc in source at https://github.com/Pragmatists/JUnitParams/blob/master/src/main/java/junitparams/naming/TestCaseName.java)

#### Adding a new TestClass to an existing package or protocol
- Add the class in the **`src/test/java`** tree of the `cts-java` module
- I repeat, add to the **TEST** tree.
- Put test in some subpackage of `org.ga4gh.cts`.
- Ensure the class name either starts or ends with "IT" (e.g., `org.ga4gh.cts.api.reads.MyReadsAreBetterIT.java`
- Add "implements CtkLogs" (or else, directly establish the logger static vars, see the `org.ga4gh.ctk.CtkLogs` javadoc)
- Add the @BeforeClass to set up a protocol client, and to re-initialize the URLMAPPER
- `import static org.assertj.core.api.Assertions.*;`

##### Choosing a TestRunner
Your test class is going to be run by a junit Runner; all the methods in the class will be run by the same Runner. Different runners provide different capabilities, so you need to know about your possibilities, and you might need to split your tests into different test classes to run under different Runners.

You select the Runner with a `@RunWith` annotation at the class level. The CTK-available Runners are:
- (default, no @RunWith) BlockJUnit4ClassRunner
- `@RunWith(WildcardPatternSuite.class)` allows selecting tests, is used to build a TestSuite (see, e.g., `org.ga4gh.cts.api.reads.ReadsTestSuite`)
- `@RunWith(JUnitParams.class)` has flexible ways to pass parameters to you test methods; see the [JUnitParams github site](https://github.com/Pragmatists/JUnitParams)
- `@RunWith(Theories.class)` - not used yet, but expected soon, see [Theories javadoc](http://junit.org/apidocs/org/junit/experimental/theories/Theories.html) or the [JavaCodeGeeks writeup](http://www.javacodegeeks.com/2013/12/introduction-to-junit-theories.html)

### Datatyped Communications

Creating a new API "Foo" has two major steps - creating the FooProtocolClient which communicates with the new endpoints, and creating the classes and/or marker interfaces that we can use to control which tests get executed (the test-control infrastructure). Then we just write normal JUnit tests.

#### Creating a FooProtocolClient
>**Design note**: doing this is going to be pretty much a cut/paste, but making a code-gen facility doesn't seem worth the overhead for the few clients we'll need... and concrete inheritance or even genericizing seems a bit premature for the payoff, since we won't make many ProtocolClients and we don't know that the evolving GA4GH APIs will follow the pattern we have used to date. So we'll leave these as disconnected classes for now.

The FooProtocolClient goes in the `transport` package:

- add the Foo endpoints (often specified in comments in the IDL) to `transport/src/main/java/org/ga4gh/ctk/transport/URLMAPPING.java`
- add the Foo endpoints to `transport/src/main/resources/defaulttransport.properties`
- Create a new `org.ga4gh.ctk.transport.FooProtocolClient` in `transport` module, in `org.ga4gh.ctk.transport.protocols` (don't forget to add description to `package-info.java`)
- FooProtocolClient should `implements org.ga4gh.GAFooMethods` - a protocol is specified by the IDL!
(Hint - your IDE will probably offer implement the defined methods to get messages stubbed; after you fill these in, don't forget to create an overload method for each that takes the additional WireAssert parameter, as you see in `ReadsProtocolClient` or `VariantsProtocolClient`)
- for each method in the FooProtocolClient, repeat a pattern like this from the ReadsProtocolClient:

```java
    @Override
    public GASearchReadsResponse searchReads(GASearchReadsRequest request) throws AvroRemoteException, GAException {
        String path = URLMAPPING.getSearchReads(); // get endpoint path
        GASearchReadsResponse response = new GASearchReadsResponse(); // create the expected Response 
        AvroJson aj = // create teh communications interaction object
                new AvroJson<>(request, response, URLMAPPING.getUrlRoot(), path, wireTracker);
        response = (GASearchReadsResponse) aj.doPostResp(); // doGetResp() or doPostResp()
        return response;
    }

```

#### Creating Infrastructure for Foo

Tests (and infrastructure) go in the **`test/`** subtree of the `cts-java` module. Here is the suggested (but not mandatory) pattern:

- Add a java package `org.ga4gh.ctk.systests.api.Foo` in the **test** tree of the `cts-java` module
- Add a test class `FooMethodsEndpointAliveIT.java` in that package (see examples) to verify the Foo endpoint is reachable and responsive
- optionally create marker interface for test control, in the **test** tree of `ctk-cli` at `org.ga4gh.ctk.control.API.FooTests.java`
- optionally create `org.ga4gh.ctk.systests.FooTestSuite.java`

### Assertions

1. using core AssertJ (the fluent assertions library)
1. adding (and using) a domain object custom assertion
1. adding (and using) a new domain Predicate or Condition

#### Using core AssertJ (the fluent assertions library)
Motivational - see the [AssertJ website](http://joel-costigliola.github.io/assertj/), we're using Version 3.1.0 (or later) of that (and generated custom assertions for the GA4GH IDL-defined classes).

The core begins with the org.assertj.core.api.Assertions.assertThat() method (usually you'll want to static import that, so you can just write `assertThat(...)`

The argument you pass to `assertThat` is the thing you want to make assertions about, and the machinery will look at the Java type of that argument to make sure you're calling applicable assertions on it.

**NEED EXAMPLES HERE **

#### Adding (and using) a domain object custom assertion
One of the key ways the CTK will be useful is as we build up libraries of domain-specific assertions and encode them in reusable Java 8 Predicates or Conditions to power the assertions. Keep this in mind as you write tests: can you extract and contribute a core Predicate or Condition?

There will be more details in [Predicates and Conditions for GA4GH](PredicatesAndConditions.md)

Although the custom Predicates and Conditions will add great reusability and conciseness over time, even as we start we have a lot of tools in the core generated assertions. These start from each domain object (the `org.ga4gh.GA*` classes) and a couple of the CTK support classes (`org.ga4gh.ctk.transport.WireTracker` and `RespCode`). The generated assertions are named after the object they assert on, with an Assert suffix - so, for example, WireTrackerAssert provides custom assertions about a WireTracker.

The Asserts are generated by a maven plugin, but once they were generated the maven plugin was disabled and the Asserts were checked into the CTK source; so, we can edit these to add more customized assertions. If you need to re-generate the Assertions, you can re-enable the maven plugin (`assertj-assertions-generator-maven-plugin`) in `ctk-transport` and/or `ctk-domain`.

The generated Asserts let us make test assertions based on the fields of the objects - so, for a WireTrackerAssert object we might have a test case that says:

```java

 WireTrackerAssert.assertThat(mywt)
                .hasResponseStatus(RespCode.NOT_FOUND);

```

The custom assertion here is `hasResponseStatus` based on the WireTracker's responseStatus field. Similarly there are custom assertions for the fields of the GA* classes.

These generated assertions only add some naming and datatyping fluency, not any logic. To add a logical custom assertion, you edit a new method on the Assert.

 WORKING HERE

#### Adding (and using) a new domain Predicate or Condition

WORKING HERE


