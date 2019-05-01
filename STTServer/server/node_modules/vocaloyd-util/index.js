const speech = require('@google-cloud/speech');
const fs = require('fs');

exports.transcribe = function(audioFile)
{
    const client = new speech.SpeechClient();

    const config =
    {
        encoding: 'AMR_WB',
        sampleRateHertz: 16000,
        audioChannelCount: 1,
        languageCode: 'fr-FR',
        alternativeLanguageCodes: ['en-US'],
        model: 'command_and_search'
    };
    
    const audio = 
    {
        content: fs.readFileSync(audioFile.path)
    };

    const req =
    {
        audio: audio,
        config: config,
    };

    return client.recognize(req);
}

exports.deleteAfterConversion = function(filePath)
{
    fs.unlink(filePath, function(err)
    {
        if(err && err.code == 'ENOENT')
        {
            console.info("File not found");
        }
        else if (err)
        {
            console.error("Unknown error. File not removed.");
        }
        else
        {
            console.info("File " + filePath + " deleted");
        }
    });
}