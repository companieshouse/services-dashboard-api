service_name  := services-dashboard-api
version       := unversioned
artifact_name := $(service_name)-$(version)

profile       ?= lambda
shadedClassifierName := -lambda

.PHONY: clean
clean:
	@echo "Running clean"
	mvn clean
	rm -f ./$(artifact_name)*.jar
	rm -rf ./build-*
	rm -f ./build.log
	@echo "Finished clean"

.PHONY: build
build:
	@echo "Running build"
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dmaven.test.skip=true
	cp ./target/$(artifact_name)${shadedClassifierName}.jar .
	@echo "Finished build"

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: test-integration
test-integration: clean
	# mvn verify -P $(profile) -Dskip.unit.tests=true

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set  -P $(profile) -DnewVersion=$(version) -DgenerateBackupPoms=false
	@test -s ./$(artifact_name)$(shadedClassifierName).jar || { echo "ERROR: Service JAR not found"; exit 1; }
	cp ./$(artifact_name)$(shadedClassifierName).jar ./$(artifact_name).zip
	@echo "Finished package"

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: build-image
build-image:
	@echo "Running build-image"
	docker build --build-arg JAR_FILE=$(artifact_jar) -t $(artifact_name) .
	@echo "Finished build-image"

.PHONY: all
all: clean build build-image
	@echo "Running all"

.PHONY: run
run:
	docker run -it --rm $(artifact_name)
