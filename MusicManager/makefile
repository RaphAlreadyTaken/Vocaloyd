shell=powershell

all: clean slicePy javaClient

#Server
slicePy:
	@echo "Generating skeleton..."
	slice2py --output-dir server ../shared/Discotheque.ice

#Client
javaClient:
	@echo "Generating proxy..."
	@echo "Compiling client..."
	${shell} cd business/discotheque ;\
	./gradlew :client:build

#Run server
runServer:
	python server/server.py

#Run client
runClient:
	java -jar business/discotheque/client/build/libs/client.jar

#Class and Ice files
clean:
	@echo "Cleaning previously generated files..."
	${shell} if (Test-Path __pycache__) {rmdir -r __pycache__}
	${shell} if (Test-Path server/discotheque) { rmdir -r server/discotheque }
	${shell} if (Test-Path server/Discotheque_ice) { rmdir -r server/Discotheque_ice }
	${shell} if (Test-Path business/discotheque/client/build) { rmdir -r business/discotheque/client/build }
	${shell} if (Test-Path business/discotheque/client/bin) { rmdir -r business/discotheque/client/bin }
