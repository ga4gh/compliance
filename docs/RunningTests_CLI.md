# Testing from the command line

The unzipped `dist/target/ga4gh-ctk-cli.zip` results in the following
directory structure:

    <test_directory>/
      | - ctk-cli-0.5.1-SNAPSHOT.jar
      | - application.properties
      | - ctk
      | - lib/
      |       |- cts-java-0.5.1-SNAPSHOT-tests.jar
      |       |- log4j2.xml
      |       |- antRunTests.xml
      | - target/

The rest of this help file assumes you're in this `<test_directory>`.

### Test results

The tests leave detailed results in the directory `testresults/<server_port>/<run #>/`;
if you have a browser, open `testresults/<server_port>/<run #>/report/html/index.html`.  (Concrete example names for tests run against server `localhost:8000`:
`testresults/localhost_8000/00001/` and `testresults/localhost_8000/00001/report/html/index.html`.)

If you perform multiple runs of the tests in the same directory, look for the highest numbered `<run #>` directory for the latest output.

### To use `ctk` to run the tests:

The `ctk` command has a help function, so you can start with

    ctk -h

and see that you can run with just:

    `./ctk`

This will use defaults and environment variables and property files. If you want to override some properties, add the property and new value to the command line.
For example:

    ./ctk --ctk.tgt.urlRoot=http://myserver:8000

If you set environment variables (or property file properties) those will still get picked up when you run via the `ctk` script;
`ctk` gives you shorthand ways to set a couple of common properties; it runs the same program as if you had used the `java` command,
as in the next section.

### To use `java` to run the tests:

    java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.tgt.urlRoot=<your server URL base>

so, for example,

    java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.tgt.urlRoot=http://myserver:8000

#### Tips

If you're regularly testing against the same server, you can set an operating system environment
variable `CTK_TGT_URLROOT` to avoid having to include that URL on every command line.
How you set environment variables varies with your shell, but a common example would be to add to your `~/.bashrc` a line like:

    export CTK_TGT_URLROOT='http://myserver:8000'

We'll assume you've done this and omit the `ctk.tgt.urlRoot` property from the examples below.

If you want to see a example of a test failing, add another property to your `java` command:

    java -jar ctk-cli-0.5.1-SNAPSHOT.jar --cts.demofail=true

There will be some console output, and you can check in `testresults/<server_port>/<run #>/` for details;
if you have a browser, open `testresults/<server_port>/<run #>/report/html/index.html`.

If you want to attach a debugger to the command-line CTK, use:

    java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n \
         -jar ctk-cli-0.5.1-SNAPSHOT.jar

For help on using the `java` command, refer to your Java vendor's documentation or the help provided with your installation (e.g., `man java` or `java -h`).


## Altering the run

The CTK's behavior is controlled by properties such as the ones you've been setting (`ctk.tgt.urlRoot` etc).
You can see and set these in the `application.properties` file. Properties can also be set in your environment.

In a 'bash' environment, write the varialbe in ALL CAPS, and replace the '.' separator with an underscore '_'. In our example, `ctk.tgt.urlRoot` becomes `export CTK_TGT_URLROOT`.

Generally, properties can be:
- set in the environment (via an explicit `export` command),
- set in a properties file (such as `~/.bashrc`), or
- passed in directly to the CTK using command line "--<name>=<val>" with either `java` or `ctk` launcher

Command line directives override property file settings, which in turn override environment variables.

The above is true for almost all properties except for those controlled during static initialization, such as logging. To control logging behavior, you will need to edit a file the CTK will load early just for this purpose, "`lib/log4j2.xml`" (as discussed in the `Tuning the output` section below.)

If you want to alter which tests get run, you can do that on the command line
(or using environment variables, etc) using the `ctk.matchstr` variable, a regex that is matched against class names:

```

    $ java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.matchstr=.*ReadMethodsEndpointAliveIT.*
    [TESTLOG] 4 failed, 6 passed, 0 skipped, 1068 ms
    [TESTLOG] FAIL: [0] TWO_GOOD, NOT_IMPLEMENTED (multipleReadGroupsNotSupported)(org.ga4gh.cts.api.reads.ReadMethodsEndpointAliveIT):
    ...
```

To make this work, the CTK/CTS has a naming convention for tests:

**Test classes end in "IT"**

So the default for `ctk.matchstr` is ".*IT.class" - that is, any class ending in "IT"

For example, the CTS test suite package includes the Java package

    org.ga4gh.cts.api.reads

and in that package we find:
```
    ReadGroupSetsSearchIT.java
    ReadMethodsEndpointAliveIT.java
    ReadsSearchIT.java
    ReadsTests.java
    ReadsTestSuite.java
```

The first three of these classes end in IT, so they're test classes.

A test class can hold multiple "test methods" so there can be several tests executed when, for example, the `ReadGroupSetsSearchIT` test is run.
Each "test method" may get executed multiple times with different parameters, depending on what the
test writer set up. The CTK does not (presently) have a mechanism to select a subset of test methods;
selection is at the class level.

The other two classes are for a near-future capability that isn't quite ready for command-line use yet, but we'll describe it nonetheless:
- `ReadsTests.java` is a "marker" we use when writing a test to mark the test method as "one of the Reads tests" ... then,
- ReadsTestSuite is a TestSuite that groups all of those, so we can run the ReadsTestSuite to get a pre-defined set of tests to execute.


To run a selected test, we just match its name as an Ant-style regex to the `ctk.matchstr` property:

` java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.matchstr=**/*ReadMethods.class`

 or

 `ctk --ctk.matchstr=**/*ReadMethods.class`


Actually, `ctk.matchstr` allows a comma-separated string of regex'es so you could select a couple tests,
but between the regex-ness and the command-line escaping it's usually better to set anything complicated
in the `lib/application.properties` file.


## Tuning the output

CTK output is to logs, not to "stdout" and such. The logging framework in the default configuration is Apache's `log4j2` (see http://logging.apache.org/log4j/2.x/) so you can use `lib/log4j2.xml` to adjust logger levels, direct the output to log files, etc.

The important thing to know is that there is a specific logger called "TESTLOG" used in all test classes to report on test progress, as well as the usual per-class loggers named after the class's full hierarchical name.
You may want to send "TESTLOG" to a file, and leave the `org.ga4gh.*` loggers as console output, but the CTK default is to route all the loggers to the console, so you may see some duplicated information.

If any tests fail, you'll get additional failure-specific logging at a WARN level. To demonstrate, we'll use the `propertyCanCauseTestFail` test case
in the `CanForceFailIT` test class.
This test just passes or fails based on a property; here's the example code:

```java

 @Test
    public void propertyCanCauseTestFail() throws Exception {

        if (Boolean.getBoolean("cts.demofail")) {
            testlog.warn("Dummying failure because cts.demofail is true");
            assertThat(false).isTrue();
        }
        else
            assertThat(false).isFalse();
    }

```

We'll compile and package it (not shown).  Then let's run it:

```
$ java -jar ctk-cli-0.5.1-SNAPSHOT.jar --cts.demofail=true --ctk.matchstr=**/*Landing*.class
[TESTLOG] Suite start org.ga4gh.cts.api.DatasetIdPropertyIT
[TESTLOG] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.134 sec
[TESTLOG] Suite start org.ga4gh.cts.api.datasets.DatasetsPagingIT
[TESTLOG] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.124 sec
...
[TESTLOG] Suite start org.ga4gh.cts.core.CanForceFailIT
[TESTLOG] Forcing failure because cts.demofail is true
[TESTLOG] FAILED propertyCanCauseTestFail(org.ga4gh.cts.core.CanForceFailIT) due to expected:<[tru]e> but was:<[fals]e>
[TESTLOG] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.005 sec
[o.g.c.Application ] task [junit] tgt [tests] msg: Test org.ga4gh.cts.core.CanForceFailIT FAILED
[TESTLOG] task [junit] tgt [tests] msg: Test org.ga4gh.cts.core.CanForceFailIT FAILED
```

The `TESTLOG` output tells us:

* the name of the failing method ("`propertyCanCauseTestFail`")
* the name of the class that test case comes from ("`org.ga4gh.cts.core.CanForceFailIT`")
* what assertion didn't pass ("`expected:<[tru]e> but was:<[fals]e`")

(We get the odd-looking assertion message because we're doing a string compare rather than a
boolean compare ... can you fix that? :)

You might want to have your `TESTLOG` routed to a network log-receiver, such
as [Chainsaw](https://logging.apache.org/chainsaw/), [Logstash](https://www.elastic.co/products/logstash),
or [Graylog](https://www.graylog.org/) - since we're using the common log4j2 framework you'll find a lot
of Internet tutorials and examples on how to set up these configurations.

## What's Next

If you have Maven installed, you will want to look into using it as the environment for running
and developing tests - you'll get cross-referenced/cross-linked source and test code and javadoc in
HTML, and the HTML test reports have links from the failure messages directly into that source tree.
See [here](RunningTests_maven.md).

