/**
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

var docUrlPrefix = 'http://ga4gh.org/documentation/api/v0.5/ga4gh_api.html#/schema/';

registerTest(
  'Search Reference Sets',
  'Fetches reference sets',
  docUrlPrefix + 'org.ga4gh.searchReferenceSets',
  function(runner) {
    // TODO: Test other variations on reference searching
    var body = JSON.stringify({assemblyId: 'GCA_000001405'});

    $.ajax({
      type: 'POST',
      url: getUrl('/referencesets/search'),
      data: body
    }).always(function(json) {
      checkHttpError(runner, json);
      assertArrayObject(runner, json, 'referenceSets', '', [
        'id',
        ['referenceIds', 'array'],
        ['names', 'array'],
        ['lengths', 'array'],
        'md5checksum',
        ['ncbiTaxonId', 'int'],
        'description',
        'assemblyId',
        'sourceURI',
        'sourceAccession',
        ['isDerived', 'boolean']
      ]);

      runner.testFinished();
    });
  }
);

registerTest(
  'Search References',
  'Fetches references',
  docUrlPrefix + 'org.ga4gh.searchReferences',
  function(runner) {
    // TODO: Implement this test
    var body = JSON.stringify({});

    $.ajax({
      type: 'POST',
      url: getUrl('/references/search'),
      data: body
    }).always(function(json) {
      checkHttpError(runner, json);

      assert(runner, json.readGroupSets, 'Test coming soon!');
      runner.testFinished();
    });
  }
);

registerTest(
  'Search Read Group Sets',
  'Fetches read group sets from the specified dataset',
  docUrlPrefix + 'org.ga4gh.searchReadGroupSets',
  function(runner) {
    // TODO: Implement this test
    var body = JSON.stringify({});

    $.ajax({
      type: 'POST',
      url: getUrl('/readgroupsets/search'),
      data: body
    }).always(function(json) {
      checkHttpError(runner, json);

      assert(runner, json.readGroupSets, 'Test coming soon!');
      runner.testFinished();
    });
  }
);

registerTest(
  'Search Reads',
  'Looks up a readset for NA12878 from the specified dataset, then fetches reads.',
  docUrlPrefix + 'org.ga4gh.searchReads',
  function(runner) {
    // TODO: Implement this test
    var body = JSON.stringify({});

    $.ajax({
      type: 'POST',
      url: getUrl('/reads/search'),
      data: body
    }).always(function(json) {
      checkHttpError(runner, json);

      assert(runner, json.readGroupSets, 'Test coming soon!');
      runner.testFinished();
    });
  }
);

function getVariantSetIds(runner, callback) {
  $.ajax({
    type: 'POST',
    url: getUrl('/variantsets/search'),
    data: JSON.stringify({
      datasetIds: [runner.datasetId]
    })
  }).always(function(json) {
    checkHttpError(runner, json);

    var variantSet = _.first(json.variantSets) || {};
    callback(variantSet.id);
  });
}

registerTest(
  'Search Variants',
  'Fetches variants from the specified dataset.',
  docUrlPrefix + 'org.ga4gh.searchVariants',
  function(runner) {
    getVariantSetIds(runner, function(variantSetId) {
      // TODO: Test other variations on variant searching
      $.ajax({
        type: 'POST',
        url: getUrl('/variants/search'),
        data: JSON.stringify({
          variantSetIds: [variantSetId],
          referenceName: '22',
          start: 51005354,
          end: 51015354,
          maxResults: 1
        })
      }).always(function(json) {
        checkHttpError(runner, json);

        // Basic fields
        assertArrayObject(runner, json, 'variants', '', [
          'id',
          'variantSetId',
          ['names', 'array'],
          ['created', 'long'],
          ['updated', 'long'],
          'referenceName',
          ['start', 'long'],
          ['end', 'long'],
          'referenceBases',
          ['alternateBases', 'array'],
          ['info', 'keyvalue']
        ]);

        // Calls
        var variant = _.first(json.variants) || {};
        assertArrayObject(runner, variant, 'calls', 'variants.', [
          'callSetId',
          'callSetName',
          ['genotype', 'array'],
          'phaseset',
          ['genotypeLikelihood', 'array'],
          ['info', 'keyvalue']
        ]);

        runner.testFinished();
      });
    });
  }
);

registerTest(
  'Search Call Sets',
  'Fetches call sets from the specified dataset.',
  docUrlPrefix + 'org.ga4gh.searchCallSets',
  function(runner) {
    getVariantSetIds(runner, function(variantSetId) {
      // TODO: Test other variations on call set searching
      $.ajax({
        type: 'POST',
        url: getUrl('/callsets/search'),
        data: JSON.stringify({variantSetIds: [variantSetId]})
      }).always(function(json) {
        checkHttpError(runner, json);

        assertArrayObject(runner, json, 'callSets', '', [
          'id',
          'name',
          'sampleId',
          ['variantSetIds', 'array'],
          ['created', 'long'],
          ['updated', 'long'],
          ['info', 'keyvalue']
        ]);
        runner.testFinished();
      });
    });
  }
);