/******** Middleware
*
********/
const express = require('express');
const formidable = require('express-formidable');
var fs = require('fs');
var http = require('http');
const MongoClient = require('mongodb').MongoClient;
const session = require('express-session');
const MongoDBStore = require('connect-mongodb-session')(session);
const path = require('path');

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
var log = require('./VocaloydSTT/business/routes/log');
var transcribe = require('./VocaloydSTT/business/routes/transcribe');

/******** Includes
 *
 ********/
app.use(session(
{
    secret: 'vocaloyd in da place',
    saveUninitialized: false,
    resave: false,
    store: new MongoDBStore(
    {
        uri: "mongodb://localhost:27017/db",
        collection: 'vocaloydSessions',
        touchAfter: 24 * 3600
    }),
    cookie: {maxAge: 24 * 3600 * 1000}
}));

app.use(express.static(path.join(__dirname, './VocaloydSTT/business'))); //Ajout répertoire business dans "path" de l'app
app.use(express.static(path.join(__dirname, './VocaloydSTT/scss'))); //Ajout répertoire scss dans "path" de l'app
app.use(formidable());
app.use('/', index);    //index.js import
app.use('/log', log);    //log.js import
app.use('/transcribe', transcribe); //transcribe.js import


/******** Server config
 *
 ********/
const server = http.createServer(app);

server.listen(port, hostname, function()
{
    console.log(`Server running at http://${hostname}:${port}/`);
});