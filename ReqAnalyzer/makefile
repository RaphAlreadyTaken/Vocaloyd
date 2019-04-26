
#Compile
.ONESHELL:
analyzer:
	@echo "Compiling..."
	javac -d ./build/ -classpath "./lib/apache-activemq-4.1.1.jar:./lib/javax.jms-api.jar" ./src/*.java
	javac -d ./build/ -proc:none Client.java

all: clean analyzer

#Run listener
.ONESHELL:
runList:
	cd ./build
	java -classpath ".:../lib/apache-activemq-4.1.1.jar:../lib/javax.jms-api.jar" src.Listener

#Run client
.ONESHELL:
runClient:
	cd ./build
	java Client

clean:
	@echo "Cleaning previously generated files..."
	rm -r build/*
	rm -f *.class