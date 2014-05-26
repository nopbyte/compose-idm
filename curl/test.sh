printf "create a user\n"

curl -u "composecontroller:composecontrollerpassword"  -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d '{"username":"test2","password":"pass"}' http://localhost:8080/idm/user/


printf "create a user 2 \n"

curl -u "composecontroller:composecontrollerpassword"  -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d '{"username":"test","password":"pass"}' http://localhost:8080/idm/user/


printf "\nAuthenticate the user:\n"

curl   -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d '{"username":"test2","password":"pass"}' -X POST http://localhost:8080/auth/user/

printf "\nPlease copy the token from the previous output\n:"
read TOKEN

printf "\ncreating a group:\n"

curl -H "Authorization: Bearer $TOKEN"  -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d '{"name":"grouptest"}' http://localhost:8080/idm/group/


#printf "\nGetting a group by id\n"

#id="id_of_group"
#curl -H "Authorization: Bearer $TOKEN"  -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8"  http://localhost:8080/idm/group/ID

 printf "\nCreating a sevice Object with token\n"

curl -u "composecontroller:composecontrollerpassword"   -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d  '{"authorization": "'"Bearer $TOKEN"'","id":"someid"'',"requires_token":true, "data_provenance_collection":false,"payment": true}' http://localhost:8080/idm/serviceobject/


printf "\nCreating a sevice Object without token\n"

curl -u "composecontroller:composecontrollerpassword"   -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d  '{"authorization": "'"Bearer $TOKEN"'","id":"second_id+notoken"'',"requires_token":false, "data_provenance_collection":false,"payment": true}' http://localhost:8080/idm/serviceobject/

printf "\nGetting service object data as a user (This doesn't give the token):\n"

curl -H "Authorization: Bearer $TOKEN"  -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8"  http://localhost:8080/idm/serviceobject/someid


printf "\nGetting service object data as a Component (This gives the API_token):\n"

curl -u "composecontroller:composecontrollerpassword"  -H "Transferjson;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8"  http://localhost:8080/idm/serviceobject/someid



