# Compliance Quickstart README

The [GA4GH compliance test suite (CTS)](https://github.com/ga4gh/compliance) is written in Java, and while it’s probably best to use an IDE (such as Eclipse or IntelliJ) to manage the project and run the tests, it is also designed to be run on the command line, eliminating dependency on any single IDE platform.

Not all dependencies can be eliminated. This quickstart guide assumes that you have an active GitHub developer account, and that the following are installed on your development system:

* [JDK 1.8 (aka J2SE 8)](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
* [Maven](https://maven.apache.org) (version 3.2.5 or newer)
* [git](https://git-scm.com)
* `unzip` or similar zip decompressor

All CLI commands are displayed in POSIX style (as tested on my MacOSX laptop). Translate as necessary when running on a Windows platform.

## Install & build compliance suite

### [Create a fork](https://help.github.com/articles/fork-a-repo/) of the main compliance repository and clone that fork into your development environment:

     > git clone https://github.com/<your_git_account_here>/compliance.git
     > cd compliance

If you omit this step and just clone the original GA4GH compliance git project into your development environment, you’ll be unable to issue pull requests back to the original repository via github.

**Register the GA4GH compliance git project** as an [upstream remote source](https://help.github.com/articles/configuring-a-remote-for-a-fork/):

     > git remote add upstream https://github.com/ga4gh/compliance.git

**Build & run all compliance tests** from the command line.
_In the compliance base directory_, run Maven to build the project:

    >  mvn clean install

Before running any tests, you may want to set an environment variable to record the server URL you’re testing against:

     > export CTK_TGT_URLROOT="<your GA4GH-compatible server URL>"

You may also wish to set the dataset ID for the test dataset as an environment variable. Note that the ID given below is specific to the GA4GH reference server running on the compliance dataset, and will likely be different on another
server implementation.

     > export CTK_TGT_DATASET_ID="WyJicmNhMSJd"

It is also possible to set these parameters on a per-run basis.

Once the project is built, unzip the contents of `<compliance_base_directory>/dist/target/ga4gh-ctk-cli.zip` into a test directory of your choice. You can now run the tests from that directory, and view the test output as webpages:

     > unzip -ou dist/target/ga4gh-ctk-cli.zip -d <test_directory>
     >  cd <test_directory>
     > ./ctk

Once the test is finished running, open `<test_directory>/testresults/<server_port>/<run#>/report/html/index.html` in your browser to see
the results.

where `<server_port>` is an abbreviated form of the full server URL, and a new `<run#>` is generated for every test. 

For example, the fourth run of the tests on
my local server would display its results here:

     <test_directory>/testresults/localhost_8000/00004/report/html/index.html

## Prepare compliance dataset

A compliance dataset is provided as part of this `ga4gh/compliance` repository, in the subdirectory `test-data`.
The data are all stored in human-readable files, and will likely need to be converted to equivalent binary files
for ingestion in a working server. An example best-practice recipe for converting each file type into its binary
equivalent is provided in the shell script `test-data/convert_to_binary.sh`.

## Write, compile & run a test

The Java source code files containing the actual tests are all located in:

     <compliance_base_directory>/cts-java/src/test/java/org/ga4gh/cts/api/<package>

where `<package>` is one of `datasets`, `endpoints`, `reads`, `references` or `variants`.
Each package contains tests in files ending with `*IT.java` (IT stands for Integration Test).

For example, tests of the search endpoint for `VariantSet`s are contained in

     <compliance_base_directory>/cts-java/src/test/java/org/ga4gh/cts/api/variants/VariantsSearchIT.java

The coding style established in this project is pseudo-functional: Most test objects are declared `final`, 
are built via [method chaining](https://en.wikipedia.org/wiki/Method_chaining), and collections are
processed via lambda-functions passed into `stream().foreach` calls.

Take a look at a typical test, such as contained in `VariantSetsSearchIT.java` for inspiration:

    /**
     * Fetch variant sets and make sure we get some.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingVariantSetsReturnsSome() throws AvroRemoteException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder().setDatasetId(TestData.getDatasetId()).build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> sets = resp.getVariantSets();
        assertThat(sets).isNotEmpty();
        sets.stream().forEach(vs -> assertThat(vs.getMetadata()).isNotNull());
    }

Once you’ve written your test file, you can compile and run just that one test file as follows:

     > cd <compliance_base_directory>
     > mvn -q install
     > unzip -ou dist/target/ga4gh-ctk-cli.zip -d <test_directory>
     > cd <test_directory>
     > ./ctk --ctk.matchstr=**/<Test_filename>.class
 
The `-q` (“quiet”) option on Maven minimizes the amount diagnostic output to the command line, so that any errors in your compilation will stand out more clearly.

_For more information about writing a new test, refer to the [test writing README](WritingATest.md)._

## Contribute your code to the ga4gh compliance repository

Please first ensure that any test you write can be successfully passed by a matching version of the
[reference server](https://github.com/ga4gh/server). Pull requests will only be accepted if they result
in a consistent state across all three GA4GH code repositories ([schemas](https://github.com/ga4gh/schemas),
[reference server](https://github.com/ga4gh/server) and [compliance](https://github.com/ga4gh/compliance)).
A change in schemas, for example, would typically trigger updates to all three repositories.
A change or bugfix in the server need not entail a schemas change, but would likely result in added or
modified compliance tests. A bugfix or addition to the compliance suite may be standalone.

The typical workflow is to `git push` the working branch with your code to your fork of the repository
(the `origin`), then issue a pull request against `ga4gh/compliance` (the `upstream` repository) from your
Github fork.

_Please refer to the [CONTRIBUTING](https://github.com/ga4gh/compliance/blob/master/CONTRIBUTING.md)
document for further details._
