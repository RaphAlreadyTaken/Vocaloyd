/******** Middlewares
*
********/
const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const session = require('express-session');
const MongoDBStore = require('connect-mongodb-session')(session);
const MongoClient = require('mongodb').MongoClient;
var http = require('http');
var fs = require('fs');

/******** Variables
 *
 ********/
var app = express();
const hostname = 'localhost';
const port = 3131;

/******** Routes
 *
 ********/
var index = require('./VocaloydSTT/business/routes/index');
var log = require('./VocaloydSTT/business/routes/log')
var transcribe = require('./VocaloydSTT/business/routes/transcribe');

/******** Includes
 *
 ********/
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json({limit: '10mb'}));
app.use('/', index);    //index.js import
app.use('/transcribe', transcribe); //transcribe.js import

/******** Server config
 *
 ********/
const server = http.createServer(app);

server.listen(port, hostname, function()
{
    console.log(`Server running at http://${hostname}:${port}/`);
});