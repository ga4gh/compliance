# CTK/CTS Design - Structure and Rationale

## Design Goals

The intent of the CTK is to make it easy to run and write GA4GH API-centric tests, and to use the test results in various reporting environments.

## Layout Overview

The CTK is structured as a set of Maven modules:
```
                                                             +-----------------------------+
ctk bash script, raw java invoke +--------------------------->ctk-cli                      |
                                                             |(command line 'java' app runs|
                           +-----------------------------+   |Ant, generates JUnit reports)|
                           | ctk-core (Maven)            |   |                             |
command line 'mvn'+--------> (build env, run tests as    |   +--------+--------------^-----+
                           | 'integration test', makes   |            |              |      
IDE with Maven runner+-----> 'site' of code, javadoc)    |            |              |
                           +-----------------------------+            |              |      
                                                                      |              |      
                                                  +-------------------v----+    +----+-----+
IDE with JUnit runner +--------------------------->                        |    |dist      |
                                                  |                        |    |(build zip|
              +-----------------------------------+      cts-java          |    |for CLI)  |
              |                                   |      (JUnit tests)     |    +----------+
              |                        +----------+                        |                
              |                        |          |                        |    +----------+            
              |                        |           +-----------------+-----+    |cts-demo  |            
              |                        |                             |          |(show test|            
              |                        |                             |          |techniques|            
              |                        |                             |          +----------+            
              |                        |                             |                      
              |                        |                             |                      
        +-----v--------+     +---------v------------+       +--------v------+               
        |cts-schemas   |     | ctk-domain           |       | ctk-transport |         
        |(Java classes)|     | (asserts, predicates,|       | to/from JSON, /-------\ target
IDL <---+              <-----+ test support)        <-------+ logging       \-------/ server       
avro    |              |     |                      |       |               |               
        +--------------+     +----------------------+       +---------------+               


[drawn using cool online tool, http://asciiflow.com]
```

- **parent** (not shown) is the common dependency management POM module
- **ctk-core** is an aggregator POM which provides common lifecycle and cross-module operations (such as building out a 'site')
- **ctk-schemas** is some variation of the GA4GH [schemas](https://github.com/ga4gh/schemas) repository
- **ctk-domain** is the domain assertions, predicates, or test conditions, and some general framework dependencies (test categories, suites, etc)
- **ctk-transport** is the JSON/HTTP/Avro connection module
- **ctk-cli** is the command-line runner 
- **cts-java** is the tests of the server (all in the src/test tree, treated as integration tests)
- **cts-demo--java** is a module of examples test techniques (using the custom asserts, etc)
- **dist** is a Maven assembly module, to build the distributions of the CTK (22 June 2015
builds a single ZIP which unpacks to be the command-line tool)

The design intent is that the central value (the tests) be independent of the launchers so that
tests can be run from command lines, build processes, IDEs, web servers or other environments. The
test framework itself should be stable and well-documented (hence JUnit 4).

Therefore, while the launchers have sophisticated backing software such as Maven, or Spring Framework,
the tests themselves are organized as Ant-runnable JUnit with output to JUnit listeners and normal
Java logging.

The tests intentionally do not use dependency injection, aspect orientation, or other support
mechanisms which may themselves increase the mental or maintenance workload for test writers,
or limit future migration of the tests to improved environments.

The simple environment for the tests (environment properties and normal JUnit environment, with
server-specific communications delegated to the ctk-transport module) should help when the team
starts using different JVM languages (Groovy, Javascript, Jython, etc.); as long as each
language delivers a JAR file of test classes that are JUnit-invocable, the tests should mix into the
CTK reasonably well.

(7/13/2015 Wayne S: This has not yet been tried, and it isn't clear how the test documentation and
source get knotted together across languages.)
