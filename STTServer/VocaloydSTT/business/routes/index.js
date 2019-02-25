const express = require('express'); //Import Express
const path = require('path');
const router = express.Router();

router.get('/', function(request, response)
{
    if (request.session.connected === true)	//Utilisateur connecté
	{
		response.sendFile(path.resolve('./VocaloydSTT/index.html'));   //Page app
	}
	else	//Utilisateur non connecté
	{
		response.sendFile(path.resolve('./VocaloydSTT/login.html'));	//Page login
	}
});

module.exports = router;