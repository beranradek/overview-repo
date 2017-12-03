# overview-repo-mongo

Mongo DB implementation of rich repository with overview (filtering, grouping, ordering and pagination) settings.

## Library maintenance

 * Running tests: gradlew :overview-repo-mongo:clean :overview-repo-mongo:test
 * Publishing artifact: gradlew :overview-repo-mongo:clean :overview-repo-mongo:uploadArchives
 * Releasing artifact: 
   * gradlew :overview-repo-mongo:clean :overview-repo-mongo:release
   * Increase version in gradle.properties of subproject to next version
