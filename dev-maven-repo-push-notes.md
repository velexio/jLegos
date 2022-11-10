# Pushing to Maven Central Repo

These instructions are summary from instructions found
at the following site. If issues arise, review reference for changes

[OSSRH Guide](https://central.sonatype.org/publish/publish-maven/#nexus-staging-maven-plugin-for-deployment-and-release)

## Snapshot Version deploy

1. Make sure that the version in pom.xml is x-SNAPSHOT
2. Perform a deploy   
   `mvn clean deploy`

## Release Snapshot

Once the snapshot is confirmed ready for release perform the following

1. Set the release version
2.

# mvn clean deploy

# this will upload to stage

# after testing
