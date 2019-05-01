/******** Middleware
*
********/
const express = require('express');
const MongoClient = require('mongodb').MongoClient;
const vocaloyd = require('vocaloyd-util');
const amrToMp3 = require('amrToMp3');

/******** Variables
 *
 ********/
const router = express.Router();
const dsnMongoDB = "mongodb://192.168.1.15:27017/";

//POST request. Route: /transcribe/newTranscript. Allows to convert an audio file to text
router.post('/newTranscript', function (request, response)
{
    console.log("Transcription request");

    start = Date.now();

    var result = vocaloyd.transcribe(request.files.inputAudio); //Transcription (Promise)

    result
    .then(data =>
    {
        const end = Date.now();
        const resp = data[0];
        transcription = resp.results
            .map(result => result.alternatives[0].transcript)
            .join('\n');

        response.send(transcription);   //Earliest reponse
            
        confidence = resp.results
            .map(result => result.alternatives[0].confidence)
            .join('\n');

        //Check if transcript relevant
        if (transcription.length > 0)
        {
            var res = {"transcription": transcription, "confidence": confidence};

            var file = request.files.inputAudio.path.substring(request.files.inputAudio.path.lastIndexOf("/") + 1);

            amrToMp3(request.files.inputAudio.path, './upload')
                .then(function (data)
                {
                    console.log(data);
                    vocaloyd.deleteAfterConversion(request.files.inputAudio.path);
                })
                .catch(function (err)
                {
                    console.log(err);
                });

            file = file.substring(0, file.lastIndexOf(".")) + ".mp3";

            const origin = request.fields.device;
            var date = new Date();
            date = date.toLocaleString("fr-FR", {hour12: false});
            const processTime = (end - start);
            const confid = res.confidence.substring(0, 4);

            var transcript = {"origin": origin, "file": file, "output": res.transcription, "date": date, "processTime": processTime, "confidence": confid};

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
                    dbo.collection("transcriptions").insertOne(transcript, function(err)
                    {
                        mongoClient.close();
        
                        if (err)
                        {
                            return console.log('Erreur conversion données Mongo');
                        }
                    });
                }
            });
        }
        else
        {
            console.log("Erreur contenu vide")
        }
    })
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