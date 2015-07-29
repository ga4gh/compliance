# Installing The CTK/CTS

- `git clone https://github.com/wstidolph/ctk-core.git` (checkout this project)
- `cd ctk-core`
- `mvn clean install` (this will run the `clean` and `install` maven goals from the aggregator POM in
`ctk-core` to puts the resulting artifacts into your local Maven repository. When those goals run
they will pick up dependency and plugin information from the `parent` module's `pom.xml` file.)

Suggestion: You might want to create an environment variable "ctk.tgt.urlRoot"
to point to your available GA4GH server, to save yourself having to add it to
command lines and module POMs.

## Installing your Schema Version
The project as it sits in https://github.com/wstidolph/ctk-core.git loads schema
from a particular version of schemas. This version of the schemas has some minor non-semantic changes
(like using String instead of CharSequence for the strings, and swapping the order
of some union fields to meet Avro/Java requirements). 

**This version of the Schema is not tracking changes to the real v0.5.1 Schema!**

Nor, of course, does it track to your own version of the schema if you were changing that.
So you may need to convert the `schemas` module to be a git submodule which is tracking
the version/branch of Schema you care about. If you do this (or otherwise edit the schema)
 you'll need to run the assertions generator so your test assertThat() functions match your new schema
 classes.

- `cd ../ctk-domain`
- `mvn assertj:generate-assertions`

(Or just run the `ctk-domain` modules assertj:generate-assertions goal using your IDE's maven runner)

You now have custom assertions, as source, in `ctk-domain/src/main/assertj-assertions` - you
manage these in your local git branch, or push them to your remotes like any other source.

If you want the build process to regenerate the assertions for you (say you're changing the
domain classes frequently) then uncomment the binds in `ctk-domain/pm.xml` and you won't have
to do this by hand - BUT, you stand a chance of the generator overwriting any editing
you've done on the domain-specific assertions.
