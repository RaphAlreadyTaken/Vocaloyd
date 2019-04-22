
all: clean javaClient run

#Client
.ONESHELL:
javaClient:
	@echo "Compiling client..."
	javac -d ./build/ ./src/*.java
	javac -d ./build/ -proc:none Analyze.java

#Run client
.ONESHELL:
run:
	cd ./build
	java Analyze $(arg1)

clean:
	@echo "Cleaning previously generated files..."
	rm -r build/*
	rm -f *.class