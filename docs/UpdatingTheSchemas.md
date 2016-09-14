# Updating the Schemas

This software uses [jitpack](https://jitpack.io) to allow the GA4GH schemas repository to be added as a maven dependency. By default, the current schemas on the GA4GH github `master` branch.

This setting can be overridden by editing `parent/pom.xml`. To point to a personal fork, simply change the line for the dependencies `groupId`.

        <ga4gh.schemas.groupId>com.github.ga4gh</ga4gh.schemas.groupId>

Will then become:

        <ga4gh.schemas.groupId>com.github.david4096</ga4gh.schemas.groupId>

Which points to the user `david4096`, instead of the GA4GH organization's fork. To use a specific branch from that fork, change the next property, `schemas.version`, to point at the branch name appended by the string "-SNAPSHOT".

        <ga4gh.schemas.version>dev-SNAPSHOT</ga4gh.schemas.version>

This functionality can also be used to point to specific commit hashes or releases. For more information visit [jitpack](https://jitpack.io).