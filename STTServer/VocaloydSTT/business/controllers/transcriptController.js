/**
 * Contrôleur de connexion
 * @param {?} $scope - Variable de contexte
 * @param {*} transcript - Transcription service
 */
function transcriptController($scope, transcript)
{
    $scope.transcriptS = transcript;

    $scope.getAllTranscripts = function()
    {
        $scope.transcripts = transcript.getAllTranscripts();
    };

    $scope.exportTranscripts = function()
    {
        $scope.exportStatus = transcript.exportTranscripts();
    };
};