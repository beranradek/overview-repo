# overview-repo-sql-scala

JDBC implementation of rich generic SQL repository with overview (filtering, grouping, ordering and pagination) settings, 
including support for one-to-one or one-to-many joins which can be declared in simple functional manner.
Adapted for Scala language.

## Library maintenance

### Release

 * Fill in CHANGELOG.md.
 * Create tag REL-x.y.z (with version to release) and push it.
 * Just run: gradlew :overview-repo-sql-scala:clean :overview-repo-sql-scala:test :overview-repo-sql-scala:assemble to see all is ok and ready for release.
 * Publish to Maven Central: gradlew :overview-repo-sql-scala:clean :overview-repo-sql-scala:uploadArchives 
 * Login to https://oss.sonatype.org/, "Close" the Staging repository for library, "Refresh" it and "Release" it.

See http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central and http://central.sonatype.org/pages/gradle.html for details.  
