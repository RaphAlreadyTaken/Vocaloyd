/******** Middleware
*
********/
const MongoClient = require('mongodb').MongoClient;
const express = require('express');

/******** Variables
 *
 ********/
var router = express.Router();
var dsnMongoDB = "mongodb://127.0.0.1:27017/";

//POST request. Route: /log/login. Allows a user to log in
router.post('/login', function (request, response)
{
    var log = request.body.login;   //Login
    var pass = request.body.password;   //Password

    //MongoDB connection
    MongoClient.connect(dsnMongoDB, { useNewUrlParser: true }, function(err, mongoClient) 
    {
        if(err) 
        {
            return console.log('Erreur connexion base de données mongo'); 
        }

        if(mongoClient) 
        {
            //Database to use
            var dbo = mongoClient.db("db");

            //User lookup (authorizedUsers collection, result stored in arrayResult)
            dbo.collection("authorizedUsers").find({'login': log, 'password': pass}).toArray(function(err, arrayResult)
            {
                mongoClient.close();

                if (err)
                {
                    return console.log('Erreur conversion données Mongo');
                }
                else if (arrayResult.length > 0)
                {
                    request.session.user = log;
                    request.session.connected = true;
                }

                response.send();
            });
        }
    });
});

//POST request. Route: /log/logout. Allows a user to log out
router.post('/logout', function (request, response) 
{
    request.session.connected = false;
    response.send();
});

/******** Export
 *
 ********/
module.exports = router;    //L'objet router est transmis lorsque le fichier log.js est importé
