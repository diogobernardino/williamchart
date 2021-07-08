# Sometimes maven "Close" operation fails due to public key issues. If so, do:
# gpg --keyserver hkp://keyserver.ubuntu.com --send-keys 0D88855B
release-maven:
	./gradlew clean build uploadArchives --no-daemon --no-parallel

release-play: # Make sure to update the release version before releasing
	./gradlew publishReleaseApk

check:
	./gradlew lint
	./gradlew detekt

test:
	./gradlew test

stop:
	./gradlew --stop

