
all: clean install

#Install
.ONESHELL:
install:
	cd server
	npm install
	cp -r vocaloyd-util ./node_modules
#Run
.ONESHELL:
runServer:
	mongod --bind_ip 192.168.1.15 &
	cd server
	npm run devstart

#Kill background Mongo
.ONESHELL:
kill:
	pkill mongod

.ONESHELL:
clean:
	cd server
	rm -rf node_modules