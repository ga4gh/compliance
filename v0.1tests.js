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

registerTest(
  'Search Readsets (v0.1)',
  'Fetches readsets from the specified dataset and tests their fields.',
  'http://ga4gh.org/#/apis/reads/v0.1/readsets',
  function(runner) {
    $.ajax({
      type: 'POST',
      url: getUrl('/readsets/search'),
      data: JSON.stringify({datasetIds: [runner.datasetId]})
    }).always(function(json) {
      checkHttpError(runner, json, this);
      assertArrayObject(runner, json, 'readsets', '', [
        'id',
        'name',
        ['datasetId', runner.datasetId],
        ['created', 'date'],
        ['readCount', 'long']
      ]);

      var readset = _.first(json.readsets) || {};
      var prefix = 'readsets.';
      assertArrayObject(runner, readset, 'fileData', prefix, [
        'fileUri',
        ['comments', 'array']
      ]);

      var data = _.first(readset.fileData) || {};
      prefix += 'fileData.';

      assertArrayObject(runner, data, 'headers', prefix, [
        'version',
        'sortingOrder'
      ]);

      assertArrayObject(runner, data, 'refSequences', prefix, [
        'name',
        ['length', 'int'],
        'assemblyId',
        'md5Checksum',
        'species',
        'uri'
      ]);

      assertArrayObject(runner, data, 'readGroups', prefix, [
        'id',
        'sequencingCenterName',
        'description',
        'date',
        'flowOrder',
        'keySequence',
        'library',
        'processingProgram',
        ['predictedInsertSize', 'int'],
        'sequencingTechnology',
        'platformUnit',
        'sample'
      ]);

      assertArrayObject(runner, data, 'programs', prefix, [
        'id',
        'name'
      ]);

      runner.testFinished();
    });
  });

registerTest(
  'Search Reads (v0.1)',
  'Looks up a readset for NA12878 from the specified dataset, then fetches reads.',
  'http://ga4gh.org/#/apis/reads/v0.1/reads',
  function(runner) {

    $.ajax({
      type: 'POST',
      url: getUrl('/readsets/search'),
      data: JSON.stringify({datasetIds: [runner.datasetId], name: 'NA12878'})
    }).always(function(json) {
      checkHttpError(runner, json, this);
      assertArrayObject(runner, json, 'readsets', '', [
          ['name', 'NA12878']
      ]);

      var na12878 = (_.first(json.readsets) || {}).id;

      $.ajax({
        type: 'POST',
        url: getUrl('/reads/search'),
        data: JSON.stringify({
          readsetIds: [na12878],
          sequenceName: '22',
          sequenceStart: 51005354,
          sequenceEnd: 51005354
        })
      }).always(function(json) {
        checkHttpError(runner, json, this);
        assertArrayObject(runner, json, 'reads', '', [
          'id',
          'name',
          ['readsetId', na12878],
          ['flags', 'int'],
          ['referenceSequenceName', '22'],
          ['position', 'int'],
          ['mappingQuality', 'int'],
          'cigar',
          'mateReferenceSequenceName',
          ['matePosition', 'int'],
          ['templateLength', 'int'],
          'originalBases',
          'alignedBases',
          'baseQuality'
        ]);

        var read = _.first(json.reads) || {};
        assert(runner, _.every(read.tags, function(value, key) {
          return typeof key == 'string' && typeof value[0] == 'string';
        }), 'Field reads.tags is a map from string to array of strings');

        runner.testFinished();
      });
    });
  });