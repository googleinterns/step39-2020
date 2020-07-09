lint-css:
	npm install stylelint stylelint-config-standard
	npx stylelint "capstone/client/src/components/*.css"

lint-java:
	cd capstone/backend; \
	mvn git-code-format:format-code -Dgcf.globPattern=**/*; \
	mvn package

