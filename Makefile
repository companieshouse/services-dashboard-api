artifact_name := services-dashboard-api
version       := unversioned
profile       ?= lambda

.PHONY: clean
clean:
	@echo "Running clean"
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log
	@echo "Finished clean"

.PHONY: build
build:
	@echo "Running build"
	$(MAKE) package
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
	mvn package  -P $(profile) -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name)-$(version).zip

.PHONY: dist
dist: clean package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: security-check
security-check:
	# mvn org.owasp:dependency-check-maven:update-only
	# mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=4 -DassemblyAnalyzerEnabled=false
	mvn org.owasp:dependency-check-maven:check -DassemblyAnalyzerEnabled=false

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
