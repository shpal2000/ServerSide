# ServerSide
Server-side code refers to the code that runs on a server, which is responsible for handling requests from client-side applications, processing data, and generating responses. 

## What this repository actually contains?
1. WebSocket server which is responsible for:
   * handling requests from **ClientSide**.
   * storing local database
   * generating responses back to **ClientSide**.

2. Game model including:
   * all players
   * cards
   * tokens
   * ...

Game logic is being proceeded on server based on responses send by user. That includes adding usere, creating rooms, implementing game changes.
That's what makes our project server-side application.


### What is reaction?
`websocket` package is a storage for whole WebSocket server. That's where we add new `request handlers` similarly named `reactions`. [Websocket reactions are located here](src/main/java/com/githib/splendor_mobile_game/websocket/handlers/reactions).
Each reaction proceedes **JSON data** given by Client. 

#### Example request made from Client:

`{
  "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
  "type": "CreateRoom",
  "data": {
    "userDTO": {
      "id": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454",
      "name": "James"
    },
    "roomDTO": {
      "name": "TajnyPokoj",
      "password": "kjashjkasd"
    }
  }
}`

This request's data is proceeded and then response is generated.

#### Example server response to Client:

'{"messageContextId":"80bdc250-5365-4caf-8dd9-a33e709a0116","type":"CreateRoomResponse","result":"OK","data":{"user":{"id":"f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454","name":"James"},"room":{"name":"TajnyPokoj"}}}'

## How to create new reaction?
If you want to create new reaction then head to the `reactions` package and create new class. Make sure to add this class into ArrayList in App.java so it will be registered as reaction.
Reaction named **CreateRoom** inserts changes to game objects. Their structure is stored [here](src/main/java/com/githib/splendor_mobile_game/game/model).

Basically reactions are handling game logic which structure is implemented in package `game.model`.


## How to commit new code to repository?

There is a protected `main` branch which stores "production" code. 
In order to avoid code destroying, the default branch is `develop`. We don't want to work directly on it, because few users may be editing the same files.
Instead - you must create a new branch which will be a temporal storage for any additional code that you've created.