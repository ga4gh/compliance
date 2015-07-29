# Configuring the CTK

The CTK is controlled through properties to set up the target server information, and to control the test finding/selection mechanism. Logging is controlled by a standard `log4j2.xml` file.

You configure the CTK via the properties used by the:

- `transport` module to control target server URLS (see `/transport/src/main/resources/defaulttransport.properties/`
- `ctk-cli` module to control:
	- test selection and some infrequently-changed items general operation (loader paths etc) (see `/ctk-cli/src/main/resources/application.properties`)
	- logging behavior (see `/ctk-cli/src/main/resources/log4j2.xml`)
- `cts-java` has its own copy of `defaulttransport.properties` to support running stand-alone tests under IDEs, this is still experimental and may be removed.


If you're working with CTK/CTS source (in an IDE or for a maven build) it's easiest to just edit `ctk-cli/src/main/resources/application.properties` and `ctk-cli/src/main/resources/application.properties` but these changes will have no effect until you rebuild (because the rebuild copies the files from `src/main/resources/` into `target/` which is where the code runs from). But, you can make temporary changes to the text properties files directly in the output build `target` tree.

If you're working with the CTK/CTS at the command line, you can extract that file from the packaged jar file and have it in the dir where the jar runs from
(`jar xvf ctk-cli-v.0.5.1-SNAPSHOT.jar application.properties`) ... if you're using the ZIP distribution, it will already have extracted that properties file (and other control files) for you.

Note that individual test suites (`cts-java` etc) might have individual configuration mechanisms or properties files - refer to their documentation.

## Configuring Logging

The logging subsystem is provided by `log4j2.xml`, so the [Apache Documentation](https://logging.apache.org/log4j/2.x/manual/configuration.html) applies. In a build or development environment it is easy to edit the default `log4j2.xml` file (`ctk-cli/src/main/resources/log4j2.xml`) before you recompile, or to directly edit the `log4j2.xml` file in your `target/` tree if you want to make temporary changes that affect logging without having to recompile/repackage; in a command-line environment you can override the default at runtime by editing the `log4j2.xml` file in the `lib/` dir (see  [RunningTests_CLI](RunningTests_CLI.md)). The default `log4j2.xml` file includes some individual loggers that you may want to specifically configure, but you can add your own appenders, set your own logger levels, etc as usual for log4j2.

The CTK logs to loggers configured by the log-using class' name (e.g., the class `org.ga4gh.ctk.transport.AvroJson` would log to a logger named "`org.ga4gh.ctk.transport.AvroJson`").

The CTK also sends test-specific data to special logs so you can redirect that output to test-specific logs:

- `TESTLOG` gets basic test information (how many tests ran, what were the test selection criteria, how long the test session took, etc); most of this data is at "info" level, but any test failures are logged at "warn" level.
- `TESTLOG.TRAFFIC` gets information intended to support easy post-test session analysis of the coverage of the test run based on the traffic exchanged with the target server: what was sent, and type of object was received (not the entire body, just the data type), and what status was reported.

## What Properties exist

The Properties list is available by looking at the javadoc for the `transport/src/main/java/org.ga4gh/ctk/config/Props.java` class. 

> **NOTE**: The target server endpoints are controlled by a class `transport/src/main/java/org/ga4gh/ctk/transport/URLMAPPING.java` which loads the target server URL from the `ctk.tgt.urlRoot` property, which is set in the `transport/src/main/resources/defaulttransport.properties` file and can be overridden by replacing the properties file or with an external config element like an environment variable or on a command line.

### Debugging URLMAPPER Initialization

Because URLMAPPING initialization is a static action which might happen without logs being available, the URLMAPPING class has a special java system property property to cause it to dump all the static initialization actions directly to stdout:

`java -Dctk.tgt.urlmapper.dump=true -jar ctk-cli-0.5.1-SNAPSHOT.jar`

Note that many tests reinitialize the URLMAPPER in a @BeforeClass, so you may see the initialization get dumped multiple times!

## How a property is set
Properties can be set on the command line, from a properties file (in various locations), or from environment variables. The mechanism is provided by Spring, so all the alternatives described in Spring documentation on [Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) are available. The important mechanisms for the CTS, in order of descending priority, are:

1. Command line arguments when running the `ctk-cli` jar from the command line: anything starting with "--" becomes a system property, so `--ctk.testpackage=...` will set the property "`ctk.testpackage`" (this works with the bash script, as
	- `./ctk --ctk.tgt.urlRoot=http://localhost:8000/v0.5.1"` or,
	- from a java launch line as `java -jar ... --ctk.tgt.urlRoot=...`
1. Operating System/shell environment variables
1. Application properties files outside of the packaged jar (`application.properties` and YAML variants); the highest priority would be a `/config` subdir of the current directory, and next would be in the current directory when the test is launched. (The Spring documentation describes other locations, and even ways to change the properties files names if you want.) 
1. Application properties packaged inside the jar to provide defaults (`application.properties` and YAML variants).

The easiest technique depends on whether you're using the CTK from a development environment, from Maven, or from a command line (the executable jar file). 

### Configuring in an IDE

xxx

### Configuring Maven

To set properties as a command line variable (the highest priority) for a maven invocation from a command line, just use the normal Maven invocation model of `-Dprop_name=prop_value` before the goal name, like this:

    mvn -Dctk.tgt.urlRoot=http://localhost:8000/v0.5.1/ install 

### Configuring a Command Line invocation
From the directory where the executable jar is, you can edit `application.properties`. The zip distribution has this file pre-extracted, but if you don't already have it, just extract it from the executable jar like this

    jar xvf ctk-cli-0.5.1-SNAPSHOT.jar application.properties

Edit the properties file, and leave it in the launch directory or put it in a `config` subdirectory.

## How a property is accessed

Most properties are read in at  launch and used to set fields on the Java object at `transport/src/main/java/org.ga4gh/ctk/config/Props.java` and this Props object is then used as the run-time source of property values. The Props object has a field (with a setter) for each property, and these fields are set by the Spring framework's @Value annotation; e.g., the property named "`ctk.testpackage`" is injected as:

```java

    @Value("${ctk.testpackage}")
    public String ctk_testpackage;

```

Putting the properties on the Props object like this allows an IDE do auto-complete on the properties, so if you add new properties to the CTK, it's good to at least consider adding them to the Props object. But, you don't have to - as we saw in the "cts.demofail" use, you can declare a property and simply access it directly (in that case, from inside a test).