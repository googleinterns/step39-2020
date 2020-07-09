CLANG_FORMAT=node_modules/clang-format/bin/linux_x64/clang-format --style=Google

node_modules:
	npm install clang-format 

pretty: node_modules
	find capstone/backend/src/main/java -iname *.java | xargs $(CLANG_FORMAT) -i

package:
	mvn package
