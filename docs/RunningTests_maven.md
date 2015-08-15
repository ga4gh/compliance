# Testing using Maven goals
You might use an IDE with a maven-runner, but Maven can be always be launched from a command line, so that's what we'll discuss here). You do need to understand the basics [Maven](https://maven.apache.org/), which the CTK uses as its basic build tool and which you can use while developing or running GA4GH Compliance tests.

# tl;dr

- in `compliance`(or whatever you named your root directory) execute `mvn clean install -DskipITs=false` ... this will execute the Maven "integration-test" phase, thus running the `failsafe:integration-test` goal in the `cts-java` module.  The reason for the command line "`skipITs`" variable is that the `cts-java` module is configured to **NOT** run the server tests by default as part of the build process (you can reverse this with the property `cts.skipITs` in the parent/pom.xml <properties> section.) So if you run maven without the `cts.skipITs=false` set, then everything except actual test-running happens.
- (optional) in `compliance` (the root directory) you can then also run `mvn site` to generate integrated reports/source/doc under `compliance/target/site`

This will use the properties you have configured for e.g. the target server (`ctk.tgt.urlRoot`), so you may want to set those ; in general, you can modify the behavior for the CTK running under maven using: 
- properties set in the `parent/pom.xml` or in submodule `pom.xml` files (particularly the `cts-java/pom.xml`), or
- on the command line using `mvn -D<property>=<value>`, or
- via system/environment properties (e.g., set `ctk.tgt.urlRoot` as a bash environment variable)
- `ctk-transport/src/main/resources/defaulttransport.properties` to alter server endpoints

# Introduction

When testing under Maven, you need to be aware of the Maven lifecycle phases, since building, unit-testing, integration-testing, packaging, and so forth happen in different phases in a specified order. For details on the Maven Lifecycle, refer to [the official introduction](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html). The CTK runs the server tests in the `integration-test` phase of the `cts-java` module's build. 

There are two test-running plugins to be aware of:
- [`surefire`](https://maven.apache.org/surefire/maven-surefire-plugin/) is the runner that executes normal "unit" tests whenever the "test" phase is reached; these tests are for checking the CTK itself, they should not need any external connection.
- [`failsafe`](https://maven.apache.org/surefire/maven-failsafe-plugin/) is the runner for "integration tests" - it is bound to two Maven lifecycle phases:
	- `integration-test` (these are the actual GA4GH-server tests),
	- `verify` (this runs `integration-test` and then checks that all the tests passed)

Both plugins can run the same tests, so we use a naming convention for CTK integration test classes to ensure they are run only by the Failsafe plugin, in the integration-test phase. The convention is that GA4GH server tests are name with "IT" at the beginning or end of the test class name. (See the `cts-java/pom.xml` file, in the `<build><plugins><plugin>` In `cts-java` Surefire is configured to *not* run the tests so-named,and Failsafe is configured to *only* run the tests so named. This configuration only applies to the cts-java module;. 

The target-server tests are run when you:

- explicitly run the `failsafe` goals in the `cts-java`module (which won't invoke earlier Maven life-cycle steps, so this only works if you have previously run maven goals to test-compile and install the modules `cts-java` needs), or
- run a Maven late-cycle phase such as "`install`" in the top-level "aggregator" module. Running a lifecycle phase in the top level which  invokes all the Maven phases up to the phase you identified, in each submodule (with an order determined by dependency analysis performed by Maven) ... so, running `mvn install` in the top level does compile, unit testing, and then integration testing in each module. If the `cts.skipITs` property is set to false, then the CTS tests will run.
- run the command-line (`ctk-cli`) or server (`ctk-server`) applications, as discussed later

# Details
You can run Maven from a command line, or from a Maven runner in your build or development environments. With Maven integration test executions you will get JUnit .txt and .xml files and HTML summary files. You can also use Maven to generate a complete `site` about the CTK - cross referenced source and javadoc, dependency reports on the CTK/CTS itself, and HTML reports on the most recent test execution.

> **Background**: in [Maven](https://maven.apache.org/), you run a 'goal' defined in a `pom.xml` file. Goals are implemented by `plugin`s. Each module has its own `pom.xml`,and its own plugins, and it can define which plugin 'goals' are attached to which 'phases' in the Maven build lifecycles. (Maven has a handful of predefined lifecycles for things like "build a jar" and a lifecycle has phases, which may be used or vacant ... Maven just executes whatever is bound to each predefined phase, in order.)
> 
> To run a Maven goal, you cd into the directory holding the `pom.xml`, and enter a command which either identifies target phases in one of the Maven lifecycles (`mvn clean test` or `mvn site`) or you enter a specific goal (`mvn failsafe:integration-test` runs the integration-test goal of the failsafe plugin ... Maven runs all the phases to get to whatever phases that goal is bound to.)
>
The `ctk-core` module in your top directory is an "aggregator" module, it takes a goal and invokes that goal on each of its submodules (as identified in the top-level `pom.xml`); then it aggregates the results of each of the submodules. From that module you execute the CTK `clean`, `test`, `package`, `install`, `site`, and `deploy` phases. The `site` phase tells each aggregated submodule to run its own `site` phase (which generate reports), then the `ctk-core` combines those reports into a deployable "project website." We use this to create a CTK developer-assistance site (cross-referenced source and javadoc for framework and for test code) and to include the most-recent server test report.
>
The `ctk-parent` module is a dependency management parent - the functional modules (e.g., `transport`, `ctk-cli`, `cts-java`) get plugin and dependency version information from the `parent` to ensure consistent information across the project. It doesn't have any independent goals to run.

The CTS tests are hooked into the Maven "`integration-test`" phase, not the unit-level "test" phase. This lets you build normal unit-style tests for CTK development and run them as you want without invoking the CTS target-server tests. But, since the CTS tests are also standard JUnit4 tests, we need to "hide" them from the unit test runner - to do this, the Surefire plugin in `cts-java` (where the server tests are located) is configured to ignore tests in classes with names starting or ending in "IT" - these are "Integration Tests" and will be picked up instead by the [Failsafe plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/) when it scans the test classes directory. Thus, in `cts-java` unit tests are run by the `mvn test` phase, under control of the Surefire plugin; while GA4GH tests are run in the integration-test phase, under control of the Failsafe plugin. Howver, the Failsafe plugin is configured to not run the integration tests unless you also set a system property `skipITs` to true; this way, you can do a complete maven build and all the other modules' integrations will run, but the Failsafe plugin won't take up time trying to test a separate target server during CTK build/test. 

In order to run the server (CTS) integration tests via maven without running a complete build, you need to ensure the test class files are in place, and then run the `failsafe:integration-test` goal with `skipITs` set, as you'll see in the example below.

If you're going to run the CTK via Maven, you'll be invoking goals in your choice of four different modules:

1. `cts-java` module provides the [failsafe:integration-test](https://maven.apache.org/surefire/maven-failsafe-plugin/) goal as the primary entry point for running the tests; this is a very nice place to start, it usually runs fast and it generates the output reports you probably need in various your IDE for a good summary. However, the integration-test phase requires you have already compiled the test source into test classes, so it won't work immediately after a `mvn clean` ... the best set of goals to run routinely is probably **`mvn clean test failsafe:integration-test -Dcts.skipITs=true -Dctk.tgt.urlRoot=`...**
.
1. `ctk-cli` provides a Spring Boot application (`org.ga4gh.ctk.Application`) you can run under maven using the `mvn spring-boot:run` goal (see the [spring-boot: run](http://docs.spring.io/spring-boot/docs/current/maven-plugin/run-mojo.html) documentation.) This is essentially equivalent to running the CTK from the command line. However, the `ctk-cli` launcher looks for `application.properties` in its launch working directory and looks for log control and test file jars in a `lib` subdirectory, and these don't exist in the checked-in source; they are assembled by the build process. So, directly launching the `ctk-cli` is most easily done if you do a one-time setup to mimic the build output environment (these won't be affected by the maven build processes):
	1. manually copy `ctk-cli/src/main/resources/application.properties` into your ctk-cli/ directory
	2. edit `application-properties` (or set launch-environment properties) to indicate the in-source location of files, for example:
		1. `ctk.antfile=../ctk-testrunner/src/main/resources/antRunTests.xml`
		2. `ctk.defaulttransportfile=../ctk-transport/src/main/resources/defaulttransport.properties/defaulttransport.properties`
		3. `ctk.testjar=../cts-java/target/cts-java-0.5.1-SNAPSHOT-tests.jar`
		4. `ctk.testclassroots=../cts-java/target/test-classes`
		5. `ctk.domaintypesfile=../ctk-domain/src/main/resources/avro-types.json`
	2. manually create a `ctk-cli/lib` directory and copy your logging control file(s) there from `ctk-cli/src/main/resources`
1. `ctk-server` provides a Spring Boot webserver-based application, run similarly to `ctk-cli` app. However, after you launch it, you'll need to use a browser or cURL etc to GET from e.g., `http://localhost:8080/servertest?urlRoot=http://192.168.2.214:8000/v0.5.1` You'll get log output to maven's terminal, and your browser will be redirected to the HTML test results.
1. `ctk-core` (the top level) has  [failsafe:integration-test](https://maven.apache.org/surefire/maven-failsafe-plugin/) to run integration tests in all the aggregated modules (including `cts-java`) and it has the [site](https://maven.apache.org/plugins/maven-site-plugin/) goal to build a report. So, in this directory a command like `mvn clean install site -Dcts.skipITs=false -Dctk.tgt.urlRoot=...` will run and report on all of the tests and build out the reference site, unattended (it takes just over 2 minutes on a moderate laptop)

When the tests are done, the individual JUnit test reports will be in the testresults/<target url>/<sequence number> tree. If you elected to build out the site, you'll find it in `ctk-core/target/site`

## Reviewing Results
After the integration tests run, you can look at per-test files (both summary .txt files, or complete-report .xml), or styesheet-generated HTML reports at levels ranging from per-test, to per-package, to entire-CTS. The location of the output depends on how you executed the tests:

- if you ran the `ctk-cli` or `ctk-server` applications, then the results are in the `testresults/` tree of that application; the txt/html reports are at the top level of the `testresults/<tgt url>/<sequence number>`, and the HTML reports are below that directory in `reports/html`.
- if you ran the `ctk-core$ mvn install` (or similar) then the CTS reports are available in `cts/target/failsafe-reports`
- if you ran the `mvn site` command from the `ctk-core` directory then you have the Surefire Reports including cross-linked test-class source and javadoc; starting from `ctk-core/target/site/index.html` you can navigate to the Project Reports link and then the Surefire Reports link. (Or, you can navigate directly to `ctk-core/target/site/GA4GH Server CTS Test Results.html` within the generated site). Note that a `site` generated locally doesn't have individual URLs for the various submodules (that comes when you `deploy` to a website) so the module-oriented links in the left nav panel don't work.

There's an example of an **intentionally-failing** test in the `LandingPageIT` test class of the `org.ga4gh.cts.core` package, if you set the `cts.demofail` property to true.


## Prerequisites

Java 8, Maven 3, [CTK source Installed](InstallingTheCTK.md)


