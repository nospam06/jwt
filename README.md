# jwt

1. build
    in project root folder, ./gradlew assemble
2. test
   in project root folder, ./gradlew build
    docker is required
3. run
    in project root folder, java -jar microservice/jwtapp/build/libs/jwtapp-${version}.jar
    please substitute version before running.
    defaults to mysql running at localhost
4. docker image
    docker build -t jwtapp microserice/jwtapp
5. docker-compose 
    docker-compose up -d jwtapp
    mysql8 will be pull also as a dependency
    start mysql8 separately by 
   docker-compose up -d mysql8
6. testing
   1. obtain one time token - curl localhost:8080/api/jwt/token?email=foo.bar@gmail.com
   2. use one time token from response and email to create user - curl -XPOST -H content-type:application/json 'http://localhost:8080/api/jwt/token' -d '{"onetimeToken": "583063c7-1ae2-417b-bc66-8fdbe28f96e5", "email":"foo.bar@gmail.com", "firstName": "foo", "lastName": "bar", "password": "foobar"}'
   3. use email and password to login and obtain jwt token - curl -XPATCH -H content-type:application/json 'http://localhost:8080/api/jwt/token' -d '{ "email":"foo.bar@gmail.com", "password": "foobar"}'
   4. use jwt token to find user uuid - curl -H 'authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiZDkxODBhNzMtYTQ0MS00ZTQ5LWIwYzgtODk2MmE3OTY5NmFkIiwidXNlclV1aWQiOiIwN2RkYzA4Mi1hN2VmLTRiY2EtOGU4NC1mOWUwNzgxNDZhYWMiLCJjcmVhdGVEYXRlIjoiMjAyMi0wNS0xN1QwMDo1NjoyOS45OTA2MjBaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIyLTA1LTE4VDAwOjU2OjI5Ljk5MDYyMFoifQ.QirEQTmxSR0RWw4_D6sHhy0fJLf4WjuUIfHdEtCfn5c' 'http://localhost:8080/api/jwt/user?email=foo.bar@gmail.com'
   5. use user uuid to lookup user - curl -H 'authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiNThjY2ZjMjQtZWE5YS00ZTA4LTg1MzItMTZhMjgwYjk0NWU2IiwidXNlclV1aWQiOiJmZDcyOGVlYi02NGM4LTRiZWEtOGEwZS1hMGJiNjQwZjRlMDYiLCJjcmVhdGVEYXRlIjoiMjAyMi0wNS0xN1QwMTozMjo1OS4zMTMxNTlaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIyLTA1LTE4VDAxOjMyOjU5LjMxMzE1OVoifQ.2OwBogEULTPGA-shdr9TK3aFatYS7huu9jLnK1HNdzM' 'http://localhost:8080/api/jwt/user/fd728eeb-64c8-4bea-8a0e-a0bb640f4e06'