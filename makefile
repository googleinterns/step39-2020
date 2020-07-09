lint: 
	npm install eslint eslint-plugin-react stylelint stylelint-config-standard
	npx eslint "capstone/client/src/components/*.js"
	npx stylelint "capstone/client/src/components/*.css"
package:
	cd capstone/backend; \
	mvn package