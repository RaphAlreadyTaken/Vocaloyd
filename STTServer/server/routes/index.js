const express = require('express'); //Import Express
const path = require('path');
const router = express.Router();

router.get('/', function(request, response)
{
    if (request.session.connected === true)	//Utilisateur connecté
	{
		console.log("Index page");
		response.sendFile(path.resolve('./index.html'));   //Page app
	}
	else	//Utilisateur non connecté
	{
		console.log("Login page");
		response.sendFile(path.resolve('./login.html'));	//Page login
	}
});

module.exports = router;