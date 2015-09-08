var express = require('express'),
    app = express();
idm_url = 'http://idm4.147.83.30.133.xip.io/';
 

 
var oauth2 = require('simple-oauth2')({
  clientID: "test_example",
  clientSecret: "test_pass",
  site: idm_url,
  tokenPath: '/oauth/token'
});
 
// Authorization uri definition 
var authorization_uri = oauth2.authCode.authorizeURL({
  redirect_uri: 'http://localhost:3000/callback',
  scope: '',
  state: ''
});
 
// Initial page redirecting to idm
app.get('/auth', function (req, res) {
    res.redirect(authorization_uri);
});
 
// Callback service parsing the authorization token and asking for the access token 
app.get('/callback', function (req, res) {
  var code = req.query.code;
  
  oauth2.authCode.getToken({
    code: code,
    redirect_uri: 'http://localhost:3000/callback'
  }, saveToken);
 
  function saveToken(error, result) {
	  if (error) { console.log('Access Token Error', error.message); }
	  token = oauth2.accessToken.create(result);
	  res.send("The token to work with :" + JSON.stringify(token,null,2));
  }
});
 
app.get('/', function (req, res) {
  res.send('Hello World');
});
 
app.listen(3000);
 
console.log('Express server started on port 3000');