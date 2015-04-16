compliance
==========

A compliance test suite for the APIs defined in the schemas repository.

This is a work in progress, see [this issue](https://github.com/ga4gh/schemas/issues/113) for more context.


## Running the tests

* Run a simple HTTP server locally using python:
  ```
  cd compliance
  python -m SimpleHTTPServer 8000
  ```
  
  If you get an error message such as `No module named SimpleHTTPServer` you can try the following instead:
 
  ```
  python3 -m http.server
  ```

* This will start a local server. Visit `http://localhost:8000/compliance.html`
  to see the running tests.

* Once loaded, select either one of the known API providers
  (currently EBI, Google or NCBI) or enter in your own API base url and a
  valid dataset ID.

* Click the Test button and your selected API provider will get a score.
