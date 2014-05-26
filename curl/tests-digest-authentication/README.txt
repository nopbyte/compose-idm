This set of curl commands interact with IDM in COMPOSE.
They take the TOKEN used to identify the user from a shell variable called $TOKEN. 
As a result it must be set before executing the curl commands with the token for an authenticated user. (see users.txt file)

Additionally, they are all using a default username and password for component authentication with HTTP Digest authentication; however, 
this must be adjusted in the future with the production username and password defined for each COMPOSE component (sdk, compose controller...etc)
