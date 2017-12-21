# overview-repo-mongo

Mongo DB implementation of rich repository with overview (filtering, grouping, ordering and pagination) settings.

## Library maintenance

 * Running tests: gradlew :overview-repo-mongo:clean :overview-repo-mongo:test
 * Publishing artifact: gradlew :overview-repo-mongo:clean :overview-repo-mongo:uploadArchives
 * Releasing artifact:
   * Fill in CHANGELOG.md, create tag REL-x.y.z (with version to release) and push it 
   * gradlew -Prelease overview-repo-mongo:clean overview-repo-mongo:uploadArchives overview-repo-mongo:closeAndPromoteRepository
   * Increase version in build.gradle of subproject to next version
