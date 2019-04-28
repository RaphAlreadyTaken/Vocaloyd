
all: clean slicePy javaClient

#Server
slicePy:
	@echo "Generating skeleton..."
	slice2py --output-dir discotheque/server Discotheque.ice

#Client
.ONESHELL:
javaClient:
	@echo "Generating proxy..."
	@echo "Compiling client..."
	cd discotheque
	gradlew :client:build

#Run server
runServer:
	# python discotheque/server/server.py
	python3 discotheque/server/server.py

#Run client
runClient:
	java -jar discotheque/client/build/libs/client.jar

#Class and Ice files
clean:
	@echo "Cleaning previously generated files..."
	rm -rf discotheque/server/__pycache__
	rm -rf discotheque/server/discotheque
	rm -rf discotheque/server/Discotheque_ice.*
	rm -rf discotheque/client/build/*
	rm -rf discotheque/client/bin/*