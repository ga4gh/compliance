# Updating the Schemas

When the [schemas](http://github.com/ga4gh/schemas) change, so must the The Compliance Test Kit.  It does not automatically
sync with the [Schemas repo](http://github.com/ga4gh/schemas); we do that by hand.

This is a quick description of the steps to follow to update the schemas contained in the test kit, and then to
update the tests and test infrastructure to match.

## Assumptions

We assume the following:

- You have a local copy of the `ga4gh/schemas` repository stored in a local directory called
`schemas`.

- Your working copy of the Compliance Test Kit is stored in the directory `compliance`.

- You're using a Unixesque shell (command interpreter).

- You have the [Maven](https://maven.apache.org/) command line program `mvn` installed.

## Procedure

### Install the Maven command line program

If necessary, install Maven.  Execute this command to see if you already have it:

    $ which mvn

If the `which` program produces any output (e.g. `/usr/local/bin/mvn`), it's installed.  Otherwise...

On OS X, you can use Brew to do it:

    $ brew install maven

On Ubuntu:

    $ sudo apt-get install maven

On Fedora/Redhat:

    $ sudo yum install maven

### Replace the Schema Files

Remove all old schema (`.avdl`) files from the compliance project:

    $ rm compliance/ctk-schemas/src/main/resources/avro/*.avdl

Copy the new schema files from `schemas` to `compliance`:

    $ cp schemas/src/main/resources/avro/*.avdl compliance/ctk-schemas/src/main/resources/avro

### Clean the `compliance` project

Change directories to the root of the `compliance` project and remove all compiled output.

    $ cd compliance
    $ mvn clean

### Pull in project dependencies

    $ (cd parent; mvn install)

### Remove the source code generated for the previous Schemas version and generate it again

    $ (cd ctk-schemas; mvn clean && mvn install)

### Build everything!

If everything went well in the previous steps, you're ready to compile the tests.  This is when you discover
how the changes have affected the tests.

    $ mvn package

## Summary

We detailed the steps required to update the schemas and compile the Compliance Test Kit.

We did __not__ discuss how to handle the effects of the many different types of schema changes, as that's well
beyond the scope of this document.

