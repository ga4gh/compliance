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
  'Reference Sets',
  'Searches for reference set GRCh37 by accession (GCA_000001405.15) and ' +
    'then fetches that same reference set by ID',
  docUrlPrefix + 'org.ga4gh.searchReferenceSets',
  function(runner) {
    // TODO: Test other variations on reference set searching
    var accession = 'GCA_000001405.15';
    var body = JSON.stringify({accessions: [accession], pageSize: 1});

    $.ajax({
      type: 'POST',
      url: getUrl('/referencesets/search'),
      data: body
    }).always(function(json) {
      checkHttpError(runner, json, this);
      assertArrayObject(runner, json, 'referenceSets', '', [
        'id',
        ['referenceIds', 'array'],
        'md5checksum',
        ['ncbiTaxonId', 9606 /* human */],
        'description',
        ['assemblyId', 'GRCh38'],
        'sourceURI',
        ['sourceAccessions', 'array'],
        ['isDerived', 'boolean']
      ]);


      // Test getReferenceSet
      var referenceSet = _.first(json.referenceSets) || {};

      $.ajax({
        url: getUrl('/referencesets/' + referenceSet.id)
      }).always(function(json) {
        checkHttpError(runner, json, this);

        // We already checked the object structure, this assert just makes
        // sure the get method works.
        assert(runner, json.id == referenceSet.id,
          'Get reference set returned a valid result');
        runner.testFinished();
      });
    });
  }
);

registerTest(
  'References',
  'Searches for chr1 of GRCh37 by MD5 checksum and then fetches that same ' +
    'reference by ID',
  docUrlPrefix + 'org.ga4gh.searchReferences',
  function(runner) {
    // TODO: Test other variations on reference searching
    var md5checksum = '1b22b98cdeb4a9304cb5d48026a85128';
    var body = JSON.stringify({md5checksums: [md5checksum], pageSize: 1});

    // chr 1 of GRCh37. See http://www.ncbi.nlm.nih.gov/nuccore/NC_000001.10
    $.ajax({
      type: 'POST',
      url: getUrl('/references/search'),
      data: body
    }).always(function(json) {
      checkHttpError(runner, json, this);
      assertArrayObject(runner, json, 'references', '', [
        'id',
        ['length', '249250621'],
        ['md5checksum', md5checksum],
        'name',
        'sourceURI',
        ['sourceAccessions', 'array'],
        ['isDerived', 'boolean'],
        ['sourceDivergence', 'string'],
        ['ncbiTaxonId', 9606 /* human */]
      ]);


      // Test getReference
      var reference = _.first(json.references) || {};

      $.ajax({
        url: getUrl('/references/' + reference.id)
      }).always(function(json) {
        checkHttpError(runner, json, this);

        // We already checked the object structure, this assert just makes
        // sure the get method works.
        assert(runner, json.id == reference.id,
          'Get reference returned a valid result');
        runner.testFinished();
      });
    });
  }
);

registerTest(
  'Reference bases',
  'Searches for chr1 of GRCh37 by MD5 checksum and then fetches 10 bases for ' +
      'that reference at offset 15000',
  docUrlPrefix + 'org.ga4gh.getReferenceBases',
  function(runner) {
    // chr 1 of GRCh37. See http://www.ncbi.nlm.nih.gov/nuccore/NC_000001.10
    var md5checksum = '1b22b98cdeb4a9304cb5d48026a85128';
    $.ajax({
      type: 'POST',
      url: getUrl('/references/search'),
      data: JSON.stringify({md5checksums: [md5checksum], pageSize: 1})
    }).always(function(json) {
      checkHttpError(runner, json, this);
      var reference = _.first(json.references) || {};

      $.ajax({
        url: getUrl('/references/' + reference.id + '/bases'),
        data: {start: 15000, end: 15010}
      }).always(function(json) {
        checkHttpError(runner, json, this);

        assertFields(runner, json, '', [
          ['offset', '15000'],
          ['sequence', 'ATCCGACATC']
        ]);

        runner.testFinished();
      });
    });
  }
);

registerTest(
  'Search Read Group Sets',
  'Fetches read group sets from the specified dataset',
  docUrlPrefix + 'org.ga4gh.searchReadGroupSets',
  function(runner) {
    $.ajax({
      type: 'POST',
      url: getUrl('/readgroupsets/search'),
      data: JSON.stringify({datasetIds: [runner.datasetId]})
    }).always(function(json) {
      checkHttpError(runner, json, this);

      assertArrayObject(runner, json, 'readGroupSets', '', [
        'id',
        ['datasetId', runner.datasetId],
        'name'
      ]);

      // ReadGroups
      var readGroupSet = _.first(json.readGroupSets) || {};
      var prefix = 'readGroupSets.';
      assertArrayObject(runner, readGroupSet, 'readGroups', prefix, [
        'id',
        ['datasetId', runner.datasetId],
        'name',
        'description',
        'sampleId',
        ['predictedInsertSize', 'int'],
        ['created', 'date'],
        ['updated', 'date'],
        'referenceSetId', // TODO: Check that the referenceSetId is valid
        ['info', 'keyvalue']
      ]);

      var readGroup = _.first(readGroupSet.readGroups) || {};
      prefix += 'readGroups.';

      // Experiment
      assertFields(runner, readGroup.experiment || {}, prefix + 'experiment.', [
        'libraryId',
        'platformUnit',
        'sequencingCenter',
        'instrumentModel'
      ]);

      // Programs
      assertArrayObject(runner, readGroup, 'programs', prefix, [
        'commandLine',
        'id',
        'name',
        'prevProgramId',
        'version'
      ]);

      runner.testFinished();
    });
  }
);

registerTest(
  'Search Reads',
  'Looks up a read group set for NA12878 from the specified dataset, ' +
    'then fetches reads.',
  docUrlPrefix + 'org.ga4gh.searchReads',
  function(runner) {
    $.ajax({
      type: 'POST',
      url: getUrl('/readgroupsets/search'),
      data: JSON.stringify({datasetIds: [runner.datasetId],
        name: 'NA12878', pageSize: 1})
    }).always(function(json) {
      checkHttpError(runner, json, this);
      assertArrayObject(runner, json, 'readGroupSets', '', [
          ['name', 'NA12878']
      ]);

      var na12878 = _.first(json.readGroupSets) || {};
      var readGroupIds = _.pluck(na12878.readGroups, 'id');

      $.ajax({
        type: 'POST',
        url: getUrl('/reads/search'),
        data: JSON.stringify({
          readGroupIds: readGroupIds,
          referenceName: '22',
          start: 51005353,
          end: 51005354
        })
      }).always(function(json) {
        checkHttpError(runner, json, this);
        assertArrayObject(runner, json, 'alignments', '', [
          'id',
          'readGroupId',
          'fragmentName',
          ['properPlacement', 'boolean'],
          ['duplicateFragment', 'boolean'],
          ['numberReads', 'int'],
          ['fragmentLength', 'int'],
          ['readNumber', 'int'],
          ['failedVendorQualityChecks', 'boolean'],
          ['secondaryAlignment', 'boolean'],
          ['supplementaryAlignment', 'boolean'],
          'alignedSequence',
          ['alignedQuality', 'array'],
          ['info', 'keyvalue']
        ]);

        var alignment = _.first(json.alignments) || {};
        var prefix = 'alignments.';

        // Mate position
        assertFields(runner, alignment.nextMatePosition || {},
          prefix + 'nextMatePosition.', [
          ['referenceName', '22'],
          'position',
          ['reverseStrand', 'boolean']
        ]);

        // Linear alignment
        var la = alignment.alignment || {};
        prefix += 'alignment.';

        assertFields(runner, la.position || {}, prefix + 'position.', [
          ['referenceName', '22'],
          'position',
          ['reverseStrand', 'boolean']
        ]);

        assertFields(runner, la, prefix, [
          ['mappingQuality', 'int'],
        ]);

        assertArrayObject(runner, la, 'cigar', prefix, [
          'operation', // TODO: Check cigar operation enum values
          ['operationLength', 'long'],
          'referenceSequence'
        ]);

        runner.testFinished();
      });
    });
  }
);

registerTest(
  'Search Variant Sets',
  'Fetches variant sets from the specified dataset.',
  docUrlPrefix + 'org.ga4gh.searchVariantSets',
  function(runner) {
    $.ajax({
      type: 'POST',
      url: getUrl('/variantsets/search'),
      data: JSON.stringify({datasetIds: [runner.datasetId]})
    }).always(function(json) {
      checkHttpError(runner, json, this);
      assertArrayObject(runner, json, 'variantSets', '', [
        'id',
        'datasetId'
      ]);

      var variantSet = _.first(json.variantSets) || {};

      assertArrayObject(runner, variantSet, 'metadata', 'variantSets', [
        'key',
        'value',
        'id',
        'type',
        'number',
        'description',
        ['info', 'keyvalue']
      ]);

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
    checkHttpError(runner, json, this);

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
          start: 51005353,
          end: 51015354,
          pageSize: 1
        })
      }).always(function(json) {
        checkHttpError(runner, json, this);

        // Basic fields
        assertArrayObject(runner, json, 'variants', '', [
          'id',
          ['variantSetId', variantSetId],
          ['names', 'array'],
          ['created', 'date'],
          ['updated', 'date'],
          ['referenceName', '22'],
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
        checkHttpError(runner, json, this);

        assertArrayObject(runner, json, 'callSets', '', [
          'id',
          'name',
          'sampleId',
          ['variantSetIds', 'array'],
          ['created', 'date'],
          ['updated', 'date'],
          ['info', 'keyvalue']
        ]);
        runner.testFinished();
      });
    });
  }
);
