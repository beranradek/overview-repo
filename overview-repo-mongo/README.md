# overview-repo-mongo

Mongo DB implementation of full-featured generic repository with overview (filtering, ordering and pagination) settings.

## Library maintenance

 * Running tests: gradlew :overview-repo-mongo:clean :overview-repo-mongo:test
 * Publishing artifact: gradlew :overview-repo-mongo:clean :overview-repo-mongo:uploadArchives
 * Releasing artifact: 
   * gradlew :overview-repo-mongo:clean :overview-repo-mongo:release
   * Increase version in gradle.properties of subproject to next version
