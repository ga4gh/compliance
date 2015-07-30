# Test Data
In order to be able to test the full capabilities of an arbitrary GA4GH server, the compliance tests
must be able to make some assumptions about the server's data.

This directory contains the test data on which we've based the compliance tests.

We make no assumptions about how the data gets installed in the server you wish to test - that depends
on the details of the server's implementation.

(For the GA4GH Reference Server implementation,
you can simply copy the _contents of_ the `compliance-dataset-and-references` directory hierarchy
to the location of your existing data, possibly merging the
contents of the `test-data/compliance-dataset-and-references/references` and destination `references`
directories.  Assuming you don't already have
a dataset named `compliance-dataset1`, you should be all set.)

## Dataset

The reads and variants are stored in subdirectories of the `compliance-dataset1` directory.  Reference data
is stored in the `references` directory.

## Updating the test data

We welcome contributions of test data that help to expose new test cases or correct problems
with the existing data.

The class `org.ga4gh.cts.api.TestData` (`TestData.java` in the `cts-java` module) describes the server's
data as the compliance tests expect it to be.  If you make any additions or changes to the existing
compliance test data, you will almost certainly need to make corresponding changes to the `TestData`
class.
