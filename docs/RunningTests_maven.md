# Testing using Maven goals

# tl;dr

- in `ctk-core`(or whatever you named your root directory) execute `mvn clean install` ... this will execute the maven "integration-test" phase, thus running the `failsafe:integration-test` goal in the `cts-java` module
- (optional) in `ctk-core` (the root directory) you can then also run `mvn site` to generate integrated reports/source/doc under `ctk-core/target/site`

Modify the behavior under maven using:
- properties set in the `cts-java` maven `pom.xml`, or
- on the command line using `mvn -D<property>=<value>`, or
- `application.properties` to alter which tests are run by default (change `cts.matchstr`),
- `defaulttransport.properties` to alter server endpoints

# Introduction

When testing under Maven, there are two test-running plugins to be aware of:
- [`surefire`](https://maven.apache.org/surefire/maven-surefire-plugin/) is the runner that executes normal "unit" tests whenever the "test" phase is reached; these tests are for checking the CTK itself, they should not need any external connection.
- [`failsafe`](https://maven.apache.org/surefire/maven-failsafe-plugin/) is the runner for "integration tests" - it is bound to a different phase, later in the project lifecycle, and these are the actual GA4GH-server tests. These tests are run when you demand it, or when you run a maven late-cycle phase such as "`install`" 

For details on the Maven Lifecycle, refer to [the official introduction](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

Both plugins can run the same tests, so we use our CTK naming convention to split out the tests to the two plugins (see the `cts-java/pom.xml` file, in the `<build><plugins><plugin>` for maven-failsafe-plugin

# WORKING HERE, NOTICED POM PROBLEM

surefire plugin as the test runner, so the main CTK Application isn't involved. Still, stdout and the default TESTLOG should be available in your terminal (this may vary if you elect to run maven under an IDE which itself manages I/O routing).



**Most important property** to check is: `ctk.tgt.urlRoot=http://localhost:8000/v0.5.1`

To alter logging behavior: modify `log4j2.xml` in source/test for a build/run launch in your IDE or in maven, or modify `lib/log4j2.xml` for a command-line launch.

# Details
You can run Maven from a command line, or from a maven runner in your build or development environments. With maven runs you will get JUnit .txt and .xml files and HTML summary files. You can also use maven to generate a complete `site` about the CTK - cross referenced source and javadoc, dependency reports on the CTK/CTS itself, and HTML reports on the most recent test execution.

> **Background**: in [Maven](https://maven.apache.org/), you run a 'goal' defined in a `pom.xml` file. Goals are implemented by `plugin`s. Each module has its own `pom.xml`,and its own plugins, and it can define which plugin 'goals' are attached to which 'phases' in the maven build lifecycles. (Maven has a handful of predefined lifecycles for things like "build a jar" and a lifecycle has phases, which may be used or vacant ... maven just executes whatever is bound to each predefined phase, in order.)
> 
> To run a maven goal, you cd into the directory holding the `pom.xml`, and enter a command which either identifies target phases in one of the maven lifecycles (`mvn clean test` or `mvn site`) or you enter a specific goal (`mvn failsafe:integration-test` runs the integration-test goal of the failsafe plugin ... maven runs all the phases to get to whatever phases that goal is bound to.)
>
The `ctk-core` module is an "aggregator" module, it takes a goal and invokes that goal on each of its submodules, then it aggregates the results of each of the submodules. It is mainly useful in running the `site` goal which tells each aggregated submodule to run its own `site` reports, then the `ctk-cre` combines those reports into a deployable "project website." We use this to create a CTK developer-assistance site (cross-referenced source and javadoc for framework and for test code) and to include the most-recent server test report.
>
The `ctk-parent` module is a dependency management parent - the functional modules (e.g., `transport`, `ctk-cli`, `cts-java`) get plugin and dependency version information from the `parent` to ensure consistent information across the project. It doesn't have any independent goals to run.

The CTS tests are hooked into the maven "integration-test" phase, not the earlier unit-level "test" phase.  This lets you build normal unit-style tests and run them as you want (including using the maven Surefire test runner). But, since the CTS tests are standard JUnit tests, we need to "hide" them from the unit test runner - to do this, the Surefire plugin in `cts-java` (where the server tests are located) is configured to ignore tests in classes with names starting or ending in "IT" - these are "Integration Tests" and will be picked up instead by the [Failsafe plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/) when it scans the test classes directory. Thus, in `cts-java` unit tests are run by the `mvn test` phase, under control of the Surefire plugin; while GA4GH tests are run in the integration-test phase, under control of the Failsafe plugin.

In order to run the server (CTS) integration tests, you need to ensure the test class files are in place, and then run the `failsafe:integration-test` goal, as you'll see in the example below.

If you're going to run the CTK via Maven, you'll be invoking goals in your choice of three different modules:

1. `cts-java` module provides the [failsafe:integration-test](https://maven.apache.org/surefire/maven-failsafe-plugin/) goal as the primary entry point for running the tests; this is a very nice place to start, it usually runs fast and it generates the output reports you probably need in various your IDE for a good summary. However, the integration-test phase requires you have already compiled the test source into test classes, so it won't work immediately after a `mvn clean` ... the best set of goals to run routinely is probably **`mvn clean test failsafe:integration-test`**
. **TODO** set up test-compile bound to integration-test goal
1. `ctk-cli` [spring-boot: run](http://docs.spring.io/spring-boot/docs/current/maven-plugin/run-mojo.html) is essentially equivalent to running the CTK from the command line but since the ctk-cli won't find any tests installed in the `lib` dir it's mainly useful for testing the framework itself. But, it can be a useful hack to start an integration test, just put the code in a package in the `test/` tree of `ctk-cli` and use this goal to run the Application.
1. `ctk-core` has  [failsafe:integration-test](https://maven.apache.org/surefire/maven-failsafe-plugin/) to run integration tests in all the aggregated modules (including `cts-java`) and it has the [site](https://maven.apache.org/plugins/maven-site-plugin/) goal to build a report. So, in this directory a command like `mvn clean test integration-test site` will run and report on the tests and build out the reference site, unattended (it takes just over 2 minutes on a moderate laptop)

When the tests are done, the TAP reports will be in the top of the `cts-core/target` directory, and failsafe reports will be in `cts-core/target/failsafe-reports/` directory. If you elected to build out the site, you'll find it in `ctk-core/target/site`

## Reviewing Results
After the integration tests run, see the `ctk-core/ctk-cli/target/failsafe-reports` directory for reports. Or, there are a couple ways for you to see the nice HTML output report:

- run the report generator standalone, with `mvn surefire-report:report` in the `cts-java` directory, or
- run the `mvn site` command from the `ctk-core` directory; this will take longer but then you can launch the site from `ctk-core/target/site/index.html` navigate to the Project Reports link and then the Surefire Reports link, for easily-readable HTML with cross-linking. (Or, you can navigate directly to `ctk-core/target/site/GA4GH Server CTS Test Results.html` within the generated site).

The generated site report is nice too, in that the HTML page of test results includes a Failure Details section (at the bottom) showing the ignored or failed tests; failed tests show the one-line explanation, and have a link directly into the test sourcecode at the point of the failure (and from there, a link to the javadoc as well).

To demonstrate this, there's an example of an **intentionally-failing** failing test in the `LandingPageIT` test class of the `org.ga4gh.cts.core` package.


## Prerequisites

Java 8, Maven 3, [CTK source Installed](InstallingTheCTK.md)

## Altering the run 

If you want to alter the test selection strings:

**<TODO show altering maven POM to set properties>**


