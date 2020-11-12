release-bintray: # Make sure to update the release version before releasing
	./gradlew clean build bintrayUpload

release-play: # Make sure to update the release version before releasing
	./gradlew publishReleaseApk

check:
	./gradlew lint
	./gradlew detekt

test:
	./gradlew test

stop:
	./gradlew --stop

