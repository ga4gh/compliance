# Future Steps

The CTK isn't done - not only does the CTS need actual server tests, but there are also improvements to consider for the CTK. This doc captures some thoughts, not yet well-formed enough to become "issues."

1. Allow tests written in [Groovy/Spock](https://code.google.com/p/spock/). This should slot in directly with the existing java/junit tests and provide a very powerful alternative test capability (particularly pleasant for python-minded developer who might find java to be restrictive and/or verbose).
2. Allow testcases to be defined and evaluated directly in JSON
2. Allow multiple test modules and/or java test packages
2. Allow tests written in non-java/groovy languages on JVM (jython, nashorn javascript, etc). Needs an xUnit report-generating capability in those languages, plus alterations to test driver to run scripting engines.
2. Enable logging control using command-line properties as well as via log4j2.xml
3. Enable script running before and after the test, for e.g., server and data setup.
4. Enable 'server modes'
	1. CTK runs in file-watcher mode, runs some part of CTS when target files change
	2. CTK runs in split test/sender vs results-receiver mode; central CTS gets test reports from registered servers, stores in DB, and makes available real-time worldwide dashboard of multiple servers' conformance status 
