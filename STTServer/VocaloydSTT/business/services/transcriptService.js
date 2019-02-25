/**
 * Service d'authentification
 * @param {?} $http - requête http
 */
function transcriptService($http)
{
	this.transcripts = [];

	/**
	 * Gets all processed transcriptions
	 * @returns {Promise} Server response
	 */
	this.getAllTranscripts = function()
	{
		var _this = this;

		return $http
		.post('http://localhost:3131/transcribe/getAllTranscripts')
		.then(function(response)
		{
			angular.copy(response.data, _this.transcripts);
		});
	};

	this.exportTranscripts = function()
	{
		return "Export done";
	}
};