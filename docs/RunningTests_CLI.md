# Testing from the command line

In this mode you are simply executing the CTS tests - not writing tests. (Writing tests means writing code, so that will take place in your dev environment, under Maven.)

## Quickstart

Get the `ga4gh-ctk-cli.zip` distribution ZIP from github (the [repository's releases page](https://github.com/wstidolph/ctk-core/releases)) and just unzip in the directory you want to run from.

The unzip will place a jar file(ctk-cli-*.jar) and a runnable bash script ('ctk') in this directory, and create `lib/` and `target/`directories; the tests jar and a couple control files will already be in the `lib/`.

There are two ways to run the CTK you just unzipped from the command line: you can use the '`java`' command, or you can use the '`ctk`' script. The `ctk` script will run the java command for you, and do a little support work to avoid overwriting test results when you run the tests multiple times.

When the tests are done, the reports will be in the `target/` directory. There are normal ant-junit reports in txt and XML in `target`, and HTML versions in `target/report/html/`

### To use 'java' to run the tests:

    java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.tgt.urlRoot=<your server URL base>

so, for example,

    java -jar ctk-cli-0.5.1-SNAPSHOT.jar -Dctk.tgt.urlRoot=http://myserver:8000/v0.5.1

Tip - if you're regularly testing against the same server, you can set an operating system environment variable "`ctk_tgt_urlRoot`" (or "`ctk.tgt.urlRoot`" if your environment prefers that) to avoid having to re-enter that URL all the time on the command line. How you set environment variables varies with your shell, but a common example would be to add to your ~/.bashrc a line like"

    export ctk_tgt_urlRoot='http://myserver:8000/v0.5.1/'

We'll stop adding that `ctk.tgt.urlRoot` property to the example command lines now.

If you want to see a failure example, add another property to your java command:

    java -jar ctk-cli-0.5.1-SNAPSHOT.jar --cts.demofail=true

There will be some console output, and you can check in `target/report` for details;
if you have a browser, check out `target/report/html/index.html`.

#### Tips
If you want to attach a debugger to the command-line CTK, use:

    java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n \
         -jar ctk-cli-0.5.1-SNAPSHOT.jar

For help on using the 'java' command refer to your java vendor's documentation or the help provided with your installation (e.g., `man java` or `java -h`)

### To use 'ctk' to run the tests:

The `ctk` command has a help function, so you can start with

    ctk -h

and see that you can run with just:
    `./ctk`

This will use defaults and environment variables and property files. If you want to override some properties, add the property and new value to the command line:

    ./ctk --ctk.tgt.urRoot=http://myserver:8000/v0.5.1/

Just as when running using the 'java' command, there will be some console output, and you can check in `target/report` for details; if you have a browser, check out `target/report/html/index.html`

If you set environment variables (or property file properties) those will still get picked up when you run via the `ctk` script; all `ctk` does about properties is give you a shortcut to setting a couple common properties and then run the same java program you get running the `java` command. 

#### Previous Results
If you run the CTK using `java` and then run it again using `java`, the new results overwrite the previous results in the `target/` directory. But if you're on bash and using the `ctk` script, it will rename your previous test results `target` directory so you don't lose any data. The new name will depend on what created the `target/` directory in the first place:
- if the `ctk` script creates it, the script places a file `.testinfo` in the directory in which the first line is the time the test begins, and so the new name will be something like `target-2015-07-14_12_08_22/`
- if target is created by the unzipping or some process which doesn't create a `.testinfo` then the `ctk` script will suffix the name with the number of seconds since Unix Epoch of the `target/`'s last access, so the new name for the previous results will be something like `target-1436900900/`

## Details

### Installation Details

In this section we describe the steps you'd take to get the environment provided by the zipped distribution..

The first generation CTK packages the CTS tests in a jar file which is separate from the main CTK framework jar.  These two jars are named:

- `ctk-cli-0.5.1-SNAPSHOT.jar`
- `cts-java-0.5.1-SNAPSHOT-tests.jar` 

So, choose or create a directory to run the test from, and put the two jars there to start.

The CTK test runners assume the test jar(s) are in a `lib` subdirectory. Create a subdirectory named `lib` and move the CTS ("-tests") jar there. (If you have multiple test jars for some reason, just put them all in that directory.)

Create a directory `target` for the test reports to go into.

Now let's make sure you have easy access to the controlling properties files:

- `jar xf ctk-cli-0.5.1-SNAPSHOT.jar application.properties`
- `jar xf ctk-cli-0.5.1-SNAPSHOT.jar log4j2.xml`
- `jar xf ctk-cli-0.5.1-SNAPSHOT.jar antRunTests.xml`
- `mv *.xml lib/`
- `jar xf ctk-cli-0.5.1-SNAPSHOT.jar ctk` (if you're at a bash prompt)
- `chmod +c ctk`  (if you're at a bash prompt)

So you end up with:

```
    <launch dir>/
      | - ctk-cli-0.5.1-SNAPSHOT.jar
      | - application.properties
      | - ctk
      | - lib/
      |       |- cts-java-0.5.1-SNAPSHOT-tests.jar
      |       |- log4j2.xml
      |       |- antRunTests.xml
      | - target/

```

## Altering the run

The CTK primary control is via properties such as the ones you've been setting (`ctk.tgt.urlRoot` etc).
You can see and set these in the `application.properties` file. Properties can also be set in your environment.

NOTE: in a 'bash' environment, you need to replace the '.' in variable names with underscore '_' when you set the variables in the environment; so, do something like:

`export ctk_tgt_urlRoot='http://localhost:8000/v0.5.1/'`

at a command line or in your .bashrc or as appropriate for your system.

Generally, properties can be:
- set in the environment, or
- set in the properties files, or can be
- passed in directly to the CTK using command line "--<name>=<val>" with either `java` or `ctk` launcher

Command line overrides properties files, which override environment vars.

You can do this for almost any property; the exception is items which are controlled during static initialization, such as logging. So, for logging control you will need to edit a file the CTK will load early just for this purpose, "`lib/log4j2.xml`" (We'll discuss that a bit more in the `Tuning the output` section below.)

If you want to alter which tests get run, you can do that on the command line
(or using environment variables, etc) using the `ctk.matchstr` variable, which is a regex that is matched against class names:

```

    ~/temp>java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.matchstr=.*ReadMethodsEndpointAliveIT.* --ctk.tgt.urlRoot=http://192.168.2.115:8000/v0.5.1/
    [TESTLOG] 4 failed, 6 passed, 0 skipped, 1068 ms
    [TESTLOG] FAIL: [0] TWO_GOOD, NOT_IMPLEMENTED (multipleReadGroupsNotSupported)(org.ga4gh.cts.api.reads.ReadMethodsEndpointAliveIT):
    ...
```

To make this work, the CTK/CTS  has a naming convention for tests:

**Test classes end in "IT"**

So the default for `ctk.matchstr` is ".*IT.class" - that is, any class ending in "IT"

For example, the CTS test suite package includes the java package

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
And, each "test method" may get executed multiple times with different parameters, depending on what the
test writer set up. The CTK does not (presently) have a mechanism to select a subset of test methods,
selection is at the Class level.

The other two classes are for a near-future capability that isn't quite ready for command-line use yet, but we'll describe it nonetheless:
- ReadsTests.java is a "marker" we use when writing a test to mark the test method as "one of the Reads tests" ... then,
- ReadsTestSuite is a TestSuite that groups all of those, so we can run the ReadsTestSuite to get a pre-defined set of tests to execute.


To run a selected test, we just match its name as an Ant-style regex to the `ctk.matchstr` property:

` java -jar ctk-cli-0.5.1-SNAPSHOT.jar --ctk.matchstr=**/*ReadMethods.class`

 or

 `ctk --ctk.matchstr=**/*ReadMethods.class`


Actually, `ctk.matchstr` allows a comma-separated string of regex'es so you could select a couple tests,
but between the regex-ness and the command-line escaping it's usually better to set anything complicated
in the `lib/application.properties file`.


## Tuning the output

CTK output is to logs, not to "stdout" and such. The logging framework in the default configuration is Apache's `log4j2` (see http://logging.apache.org/log4j/2.x/) so you can use `lib/log4j2.xml` to adjust logger levels, direct the output to log files, etc.

The important thing to know is that there is a specific logger called "TESTLOG" used in all test classes to report on test progress, as well as the usual per-class loggers named after the class' full hierarchical name. You may want to send TESTLOG to a file, and leave the `org.ga4gh.*` loggers as console output; but,  the CTK default is to route all the loggers at the console, so you may see some duplicated information.

If any tests fail, you'll get additional failure-specific logging at a WARN level. To demonstrate, we'll use the "propertyCanCauseTestFail" test case in the LandingPage test class. This test just passes or fails based
on a property; here's the example code:

```java

 @Test
    public void propertyCanCauseTestFail() throws Exception {

        if(Boolean.getBoolean("cts.demofail")) {
            testlog.warn("Dummying failure because cts.demofail is true");
            assertThat(false).isTrue();
        }
        else
            assertThat(false).isFalse();
    }

```

Let's trigger it:

```
cmd_prompt>java -jar ctk-cli-0.5.1-SNAPSHOT.jar --cts.demofail=true --ctk.matchstr=**/*Landing*.class
[TESTLOG] Suite start org.ga4gh.cts.core.LandingPageIT
[TESTLOG] Dummying failure because cts.demofail is true
[TESTLOG] FAILED propertyCanCauseTestFail(org.ga4gh.cts.core.LandingPageIT) due to expected:<[tru]e> but was:<[fals]e>
[TESTLOG] Tests run: 2, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.752 sec
[o.g.c.AntExecListener ] task [junit] tgt [tests] msg: Test org.ga4gh.cts.core.LandingPageIT FAILED
[TESTLOG] task [junit] tgt [tests] msg: Test org.ga4gh.cts.core.LandingPageIT FAILED
[TESTLOG] Overall: Tests run: 0, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.000 sec
```

The TESTLOG tells us:

* the name of the failing method ("`propertyCanCauseTestFail`")
* the name of the class that test case comes from ("`org.ga4gh.cts.core.LandingPageIT`")
* what assertion didn't pass ("`expected:<[tru]e> but was:<[fals]e`")

(We get the odd-looking assertion message because we're doing a string compare rather than a boolean compare ... can you fix that? :)

You might want to have your TESTLOG routed to a network log-receiver, such as [Chainsaw](https://logging.apache.org/chainsaw/), [Logstash](https://www.elastic.co/products/logstash), or [Graylog](https://www.graylog.org/) - since we're using the common log4j2 framework you'll find a lot of internet tutorials and examples on how to set up these configurations.

## What's Next

If you have Maven installed, you will want to look into using it as the environment for running and developing tests - you'll get cross-referenced/cross-linked source and test code and javadoc in HTML, and the HTML test reports have links from the failure messages directly into that source tree.

