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
const router = express.Router();
const dsnMongoDB = "mongodb://127.0.0.1:27017/";

//POST request. Route: /transcribe/newTranscript. Allows to convert an audio file to text
router.post('/newTranscript', function (request, response)
{
    const client = new speech.SpeechClient();
    const audioFile = request.files.inputAudio;
    const audio64 = audioFile.toString('base64');
    var transcription = ""; //Construct from available data (origin / input / output / date / processTime)
    
    const audio = 
    {
        content: audioFile,
    };

    const config =
    {
        encoding: 'LINEAR16',
        sampleRateHertz: 44100,
        languageCode: 'fr-FR',
    };

    const req =
    {
        audio: audio,
        config: config,
    };

    client
        .recognize(req)
        .then(data = function()
        {
            const response = data[0];
            transcription = response.results
                .map(result => result.alternatives[0].transcript)
                .join('\n');
            console.log(`Transcription : ${transcription}`);
        })
        .catch(err = function()
        {
            console.error('ERROR : ', err);
        })

    response.send(transcription);


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