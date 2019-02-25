/******** Middleware
*
********/
const express = require('express');
const fs = require('fs');
const MongoClient = require('mongodb').MongoClient;
const speech = require('@google-cloud/speech');

/******** Variables
 *
 ********/
var router = express.Router();
var dsnMongoDB = "mongodb://localhost:27017/";

//POST request. Route: /transcribe/newTranscript. Allows to convert an audio file to text
router.post('/newTranscript', function (request, response)
{
    var audio = request.files.inputAudio;



    response.send();

    var transcript = ""; //Construct from available data (origin / input / output / date / processTime)

    //MongoDB connection
    // MongoClient.connect(dsnMongoDB, { useNewUrlParser: true }, function(err, mongoClient) 
    // {
    //     if(err) 
    //     {
    //         return console.log('Erreur connexion base de données mongo'); 
    //     }

    //     if(mongoClient)
    //     {
    //         //Database to use
    //         var dbo = mongoClient.db("db");

    //         //Transcript addition to database
    //         dbo.collection("transcriptions").insertOne(transcript, function(err)
    //         {
    //             mongoClient.close();

    //             if (err)
    //             {
    //                 return console.log('Erreur conversion données Mongo');
    //             }
    //         });
    //     }
    // });
});

//POST request. Route: /transcribe/getAllTranscripts. Allows to retrieve all processed transcriptions
router.post('/getAllTranscripts', function (request, response)
{
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

            //Transcript addition to database
            dbo.collection("transcriptions").find({}).toArray(function(err, arrayResult)
            {
                mongoClient.close();

                if (err)
                {
                    return console.log('Erreur conversion données Mongo');
                }
                else
                {
                    response.send(arrayResult);
                }
            });
        };
    });
});

module.exports = router;