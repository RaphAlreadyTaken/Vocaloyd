/**
 * Contrôleur de connexion
 * @param {?} $scope - Variable de contexte
 * @param {*} transcript - Transcription service
 */
function transcriptController($scope, $filter, transcript)
{
    $scope.transcriptS = transcript;

    $scope.getAllTranscripts = function()
    {
        if ($scope.showTranscripts !== true)
        {
            transcript.getAllTranscripts();
        }
    };

    //TODO
    $scope.exportTranscripts = function()
    {
        $scope.exportStatus = transcript.exportTranscripts();
    };

    //Transcript sort order ("-" => reverse)
    $scope.toggleOrder = function()
    {
        if ($scope.order !== "-")
        {
            $scope.order = "-";
        }
        else
        {
            $scope.order = "";
        }
    };

    //Modifies text color according to confidence value
    $scope.confidLvl = function(value)
    {
        if (parseFloat(value) < 0.5)
        {
            return 'text-danger';
        }

        if (parseFloat(value) < 0.75)
        {
            return 'text-warning';
        }

        return 'text-success';
    };
};