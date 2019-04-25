/******** Middleware
*
********/
const express = require('express');
const MongoClient = require('mongodb').MongoClient;
const util = require('vocaloyd-util');

/******** Variables
 *
 ********/
const router = express.Router();
const dsnMongoDB = "mongodb://127.0.0.1:27017/";

//POST request. Route: /transcribe/newTranscript. Allows to convert an audio file to text
router.post('/newTranscript', function (request, response)
{
    console.log("Transcription request");

    start = Date.now();

    var result = util.transcribe(request.files.inputAudio); //Transcription (Promise)

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

        var res = {"transcription": transcription, "confidence": confidence};
    
        const origin = request.fields.device;
        const file = request.files.inputAudio.path.substring(request.files.inputAudio.path.lastIndexOf("/") + 1);
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