This is an experimental web-server wrapper for the GA4GH Conformance Test Kit (CTK).

Run it using the included 'ctk' script, or simply as
    java -jar ctk-server-0.5.1-SNAPSHOT.jar

Once it is running, it exposes a server at port 8080. (Being a Spring Boot app,
you can modify the port number with the normal --server.port= ... setting, like this:

   ./ctk --server.port=8088
   or
   java -jar ctk-server-0.5.1-SNAPSHOT.jar --server.port=8088

To run the tests, you just browse to that server using a web browser; it
will run using the defaults and values set in the application properties file.
The server will run the tests, build the static HTML report, and redirect
your web browser to the results.

Results are generated in the testresults/<urlRoot>/<test sequence number>/ directory.

You can attach 'urlRoot' and/or 'matchstr' properties to the URL to override
the contents of the application.properties file (or to overide properties pass
on the launch line). So, for example:

    http://localhost:8080/servertest?urlRoot=http://192.168.2.214:8000

The sequence number is padded to 5 digits, and sequence numbers are assigned
in increasing order so the most-recent test is highest-numbered.

If you just want to browse the local files using your file system tools,
the HTML results are in:

testresults/<urlRoot>/<test sequence number>/report/html/