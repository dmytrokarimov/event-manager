Event-manager provides easy way to exchange messages.
Endpoints:
* GET /publisher - returns list with ID of all publishers
* POST /publisher/register?appId=:appId&exchangeName=:exchangeName - register new publisher. Returns entity with token that should be used for /send
	{"state":"SUCCESS","body":{"id":"f24a0af2de8-54e27bbfae1-e0dd474a10"}}
* DELETE /publisher/:id - removes client

* GET /consumer - returns list with ID of all consumers
* POST /consumer/subscribe?appId=:appId&exchangeName=:exchangeName - subscribe new consumer. Returns entity with token that can be used for /receive
	{"state":"SUCCESS","body":{"id":"f24a0af2de8-54e27bbfae1-e0dd474a10"}}
* POST /consumer/subscribe?target=dlq&appId=:appId&exchangeName=:exchangeName - subscribe new consumer to DLQ queue of :exchange and queue :appId. Returns entity with token that can be used for /receive
	{"state":"SUCCESS","body":{"id":"f24a0af2de8-54e27bbfae1-e0dd474a10"}}
* DELETE /consumer/:id - removes client

* GET /event/receive/{clientId} - waits until message wont be send
* POST /event/send/{clientId} - send message to exchange. Body - message bytes