# Writing Tests

This covers the mechanics of adding tests to the `cts-java` module.

## Prerequisites
- A reachable GA4GH Server; the CTK framework assumes this is available at the URL provided by the `ctk.tgt.urlRoot`
property (e.g., `http://192.168.2.115:8000/v0.5.1/`)
- The CTK source installed (see [Installing The CTK](InstallingTheCTK.md))
- You're familiar with the [CTK Test Architecture and Conventions](TestArchAndConventions.md)

You'll find it's much easier if you are also using Maven 3 as your runner, so we'll mostly assume that, too.

## Overview

Tests follow the normal JUnit pattern: a test class contains test methods, each test method sets up a condition, makes
some server interaction, then makes some assertions about the results of the interaction (examining the returned status,
or the returned object(s)).

The CTK starts with normal JUnit, then provides two major GA4GH-specific facilities: datatyped-communications with the
target server, and flexible assertions about the returned objects.

### JUnit Test Mechanics

In this section we'll go over the JUnit-level issues.

Let's explore some scenarios:

1. adding a simple test (method) to an existing test class
1. adding a data-driven/parameterized test method
1. adding a new test class to an existing package or protocol

#### Adding a test method to existing test class
- name can be anything, so describe the end goal in domain terms e.g., "allSequencesShouldMatchPattern"
- annotate the method with @Test
- simplest method signature is `@Test public void <testname>() throws Exception {...}`
- use javadoc to explain the test
- use `log` to report behavior of your test class, use `testlog` to report test results if you want (other than simple
pass/fail, those are automatic based on whether you fail any assertions)
- normal JUnit structure: try to structure tests as setup, action, assertion
- Usually you'll want to have just one assertion, and most commonly it will start with
`org.assertj.core.api.Assertions.assertThat(`*actual result*`).`...; but in some cases you can fruitfully use
"`SoftAssertion`" facility to report multiple failures in one test.

#### Adding a data-driven/parameterized test method
- your test class needs to be run with `@RunWith(JUnitParamsRunner.class)`
- annotate test method with one of the forms of `@Parameter`, and add the parameters to the test method signature (for
lots of options here, see the doc at https://github.com/Pragmatists/JUnitParams/wiki/Quickstart)
- consider using `@TestCaseName(...)` to make better test case names (see doc in source at
https://github.com/Pragmatists/JUnitParams/blob/master/src/main/java/junitparams/naming/TestCaseName.java)

#### Adding a new test class to an existing package or protocol
- Add the class in some subpackage of `org.ga4gh.cts` in the **`src/test/java`** tree of the `cts-java` module
- I repeat, add to the **test** tree, not the **main** tree.
- Ensure the class name either starts or ends with "IT" ("Integration Test"),
e.g. `org.ga4gh.cts.api.reads.MyReadsAreBetterIT.java`.
- Add "implements CtkLogs" (or directly establish the logger static vars, see the JavaDoc for `org.ga4gh.ctk.CtkLogs`)
- Add the `@BeforeClass` method to set up a protocol client, and to re-initialize the URLMAPPER
- You'll probably want to add `import static org.assertj.core.api.Assertions.*;` to your class.

##### Choosing a TestRunner
Your test class is going to be run by a JUnit Runner; all the methods in the class will be run by the same Runner.
Different runners provide different capabilities, so you need to know the possibilities, and you might need to
split your tests into different test classes to run under different Runners.

You select the Runner with a `@RunWith` annotation at the class level. The CTK-available Runners are:
- (default, no @RunWith) BlockJUnit4ClassRunner
- `@RunWith(WildcardPatternSuite.class)` allows selecting tests, is used to build a TestSuite (see, e.g.,
`org.ga4gh.cts.api.reads.ReadsTestSuite`)
- `@RunWith(JUnitParams.class)` has flexible ways to pass parameters to you test methods; see the [JUnitParams github
site](https://github.com/Pragmatists/JUnitParams)

### Datatyped Communications

Creating a new API "Foo" has two major steps - creating the protocol client to communicate with the new endpoints,
and creating the classes and/or marker interfaces that we can use to control which tests get executed (the test-control
infrastructure). Then we just write normal JUnit tests.

#### Creating a Protocol Client

See the document (Test Architecture and Conventions)[TestArchAndConventions.md] for details.

#### Creating Infrastructure for Foo

Tests (and infrastructure) go in the **`test/`** subtree of the `cts-java` module. Here is the suggested (but not
mandatory) pattern:

- Add a java package `org.ga4gh.ctk.systests.api.Foo` in the **test** tree of the `cts-java` module.
- Add a test class `FooMethodsEndpointAliveIT.java` in that package (see examples) to verify the Foo endpoint is
reachable and responsive.
- Optionally create marker interface for test control, in the **test** tree of `ctk-cli` at
`org.ga4gh.ctk.control.API.FooTests.java`.
- Optionally create `org.ga4gh.ctk.systests.FooTestSuite.java`.

### Assertions

1. Using core AssertJ (the fluent assertions library)
1. Adding and using domain object custom assertions

#### Using core AssertJ (the fluent assertions library)
Though you can use plain old JUnit assertions, we include the [`AssertJ`](http://joel-costigliola.github.io/assertj/ library
to help you write more readable, and perhaps more concise, tests.  As part of the build process, we also generate custom
assertion libraries for the GA4GH objects.  See the
[next section](#adding-and-using-domain-object-custom-assertions).

Begin with the `org.assertj.core.api.Assertions.assertThat()` method (usually you'll want to `static import` that,
so you can just write `assertThat(...)` in your code.

The argument you pass to `assertThat` is the object you want to make assertions about.  The assertions machinery (and your
IDE) will look at the Java type of that argument to make sure you're making assertions that are applicable to it.

#### Adding and using domain object custom assertions
One of the key ways the CTK will be useful is as we build up libraries of domain-specific assertions and encode them in
reusable Java 8 `Predicate`s or `Condition`s to power the assertions. Keep this in mind as you write tests: can you extract
and contribute a core `Predicate` or `Condition`?

There are more details in [`Predicate`s and `Condition`s for GA4GH](PredicatesAndConditions.md).

Although the custom `Predicate`s and `Condition`s will add great reusability and conciseness over time, even as we start
we have a lot of tools in the core generated assertions. These start from each domain object (the `org.ga4gh.GA*`
classes) and a couple of the CTK support classes (`org.ga4gh.ctk.transport.WireTracker` and `RespCode`). The generated
assertions are named after the object they assert on, with an `Assert` suffix - so, for example, `WireTrackerAssert`
provides custom assertions about a `WireTracker`.

The Asserts are generated by a Maven plugin, but once they were generated the Maven plugin was disabled and the Asserts
were checked into the CTK source; so, we can edit these to add more customized assertions. If you need to re-generate
the Assertions, you can re-enable the Maven plugin (`assertj-assertions-generator-maven-plugin`) in `ctk-transport`
and/or `ctk-domain`.

The generated Assertions let us make test assertions based on the fields of the objects.  Using the basic `AssertJ` assertions,
we can test the response code in a `WireTracker` object like this:

```java

    WireTracker wt = ...;
    assertThat(wt.getResponseStatus()).isEqualTo(RespCode.NOT_FOUND);
    ...

```

Using the generated `WireTrackerAssert` assertion object, we can state this more clearly using `hasResponseStatus`:

```java

    WireTracker wt = ...;
    WireTrackerAssert.assertThat(wt).hasResponseStatus(RespCode.NOT_FOUND);

```

We also generate custom assertion classes for the fields of the GA4GH classes.

These generated assertions only add some naming and datatyping fluency, not any logic. To add a logical custom
assertion, you edit a new method on the Assert.

