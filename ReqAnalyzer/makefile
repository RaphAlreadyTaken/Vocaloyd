
#Compile
.ONESHELL:
analyzer:
	@echo "Compiling..."
	javac -d ./build/ ./src/*.java
	javac -d ./build/ -proc:none Client.java

all: clean analyzer run

#Run client
.ONESHELL:
run:
	cd ./build
	java Client

clean:
	@echo "Cleaning previously generated files..."
	rm -r build/*
	rm -f *.class