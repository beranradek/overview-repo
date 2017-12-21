# overview-repo-mongo

Mongo DB implementation of rich repository with overview (filtering, grouping, ordering and pagination) settings.

## Library maintenance

### Release

 * Fill in CHANGELOG.md.
 * Create tag REL-x.y.z (with version to release) and push it.
 * Just run: gradlew :overview-repo-mongo:clean :overview-repo-mongo:test :overview-repo-mongo:assemble to see all is ok and ready for release.
 * Publish to Maven Central: gradlew :overview-repo-mongo:clean :overview-repo-mongo:uploadArchives 
 * Login to https://oss.sonatype.org/, "Close" the Staging repository for library, "Refresh" it and "Release" it.

See http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central and http://central.sonatype.org/pages/gradle.html for details.  
