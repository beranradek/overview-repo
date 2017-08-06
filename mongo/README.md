# overview-repo-mongo

Mongo DB implementation of full-featured generic repository with overview (filtering, ordering and pagination settings).

## Library maintenance

 * Running tests: gradlew :mongo:clean :mongo:test
 * Publishing artifact: gradlew :mongo:clean :mongo:uploadArchives
 * Releasing artifact: gradlew :mongo:clean :mongo:release; Increase version in gradle.properties of subproject to next version
