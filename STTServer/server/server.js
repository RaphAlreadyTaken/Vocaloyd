/******** Environment
*
********/
require('./env');   //Removing this breaks Google Speech to Text (credentials)

/******** Middleware
*
********/
const express = require('express');
const formidable = require('express-formidable');
var http = require('http');
const session = require('express-session');
const MongoDBStore = require('connect-mongodb-session')(session);
const path = require('path');

/******** Variables
 *
 ********/
var app = express();
const hostname = '192.168.1.15';
const port = 3131;

/******** Routes
 *
 ********/
var index = require('./routes/index');
var log = require('./routes/log');
var transcribe = require('./routes/transcribe');

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
        uri: "mongodb://192.168.1.15:27017/db",
        collection: 'vocaloydSessions',
        touchAfter: 24 * 3600
    }),
    cookie: {maxAge: 24 * 3600 * 1000}
}));

app.use(express.static(path.join(__dirname, '../business'))); //Ajout répertoire business dans "path" de l'app)
app.use(express.static(path.join(__dirname, '../business/scss'))); //Ajout répertoire scss dans "path" de l'app
app.use(express.static(path.join(__dirname, './upload'))); //Ajout répertoire upload dans "path" de l'app

app.use(formidable(
{
    uploadDir: './upload',
    keepExtensions: true
}));

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