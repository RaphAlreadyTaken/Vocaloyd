shell=powershell

all: clean analyzer

#Compile
analyzer:
	@echo "Compiling..."
	${shell} cd ./business ;\
	javac -d ./build/ ./src/*.java ;\
	javac -d ./build/ -proc:none ./Client.java	

#Run server
.ONESHELL:
runServer:
	cd ./server/bin
	gateway.start.bat --broker jms


#Run listener
runList:
	@echo "Running listener..."
	${shell} cd ./business/build ;\
	java src.Listener

#Run client
.ONESHELL:
runClient:
	@echo "Running client..."
	${shell} cd ./business/build ;\
	java Client

clean:
	@echo "Cleaning previously generated files..."
	${shell} if (Test-Path business/build) {rm -r business/build/*}
	${shell} if (Test-Path .) {rm *.class}