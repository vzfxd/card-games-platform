class Request {
    constructor(method, body){
        this.method = method;
        this.body = body;
    }

    getJson(){
        let req = {
            method: this.method,
            headers: { 
                'Accept': '*/*',
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + jwt_raw
            },
        }

        if(this.method != "GET"){
            req['body'] = JSON.stringify(this.body);
        }
        
        return req
    }
}

const jwt_raw = sessionStorage.getItem("jwt_raw");
const jwt_json = JSON.parse(sessionStorage.getItem("jwt_json"));
if(jwt_raw == null){
    window.location.href = "auth.html";
}

const api_url = 'http://localhost:8080/api';
const websocket_chat_url = 'ws://localhost:8080/websocket/chat';
const websocket_game_url = 'ws://localhost:8080/websocket/game';
const room_container = document.querySelector(".room-container");
const create_room = document.querySelector(".create-room");
const game_container = document.querySelector('.game-container');
const index_container = document.querySelector('.index-container');
const chat_input = document.querySelector('.chat-input');
const ul = document.querySelector('ul');
const stompGameClient = new StompJs.Client({brokerURL:websocket_game_url});
const stompChatClient = new StompJs.Client({brokerURL:websocket_chat_url});
const chat = document.querySelector(".chat");
const startButton = document.querySelector(".start-button");
const winner = document.querySelector(".winner");
let connected_id;
let join_buttons;

function showMessage(username, msg){
    const li = document.createElement('li');
    li.innerHTML = `${username}:${msg}`
    ul.appendChild(li);
}

function scrollChatToBottom() {
    chat.scrollTop = chat.scrollHeight;
}

function sendMessage(e){
    if(e.keyCode != 13){
        return
    }
    console.log(JSON.stringify(chat_input.value));
    stompChatClient.publish({
        destination: "/app/chat/"+connected_id,
        body: JSON.stringify({msg:chat_input.value})
    });
    chat_input.value = "";
    scrollChatToBottom();
}

async function getRoomList(){
    let end_point = "/room/list";
    let req = new Request('GET',{});
    const res = await fetch(api_url+end_point,req.getJson());
    return await res.json();
}

async function createRoomHandler(){
    let json = {
        "gameType":document.querySelector('input[name="game-type"]:checked').className.toUpperCase().replace("-",""),
        "playerLimit":parseInt(document.querySelector('.player-limit').value) 
    }
    let end_point = "/room/create";
    let req = new Request('POST',json);
    const res = await fetch(api_url+end_point,req.getJson());
    return await res.json();
    
}

function renderRoomList(data){
    data.forEach(room => {
        const roomDiv = document.createElement('div');
        roomDiv.classList.add('room');
        roomDiv.dataset.id = room.id;
        roomDiv.innerHTML = `
            <div class='room-owner'>Owner: ${room.owner}</div>
            <div class='room-game-type'>Game Type: ${room.gameType}</div>
            <div class='room-players-number'>Players number : ${room.noPlayers}/${room.playerLimit}</div>
            <div class='room-player-list'>Players: ${room.players.join(', ')}</div>
            <div class='div-join-button'> <input type="button" value="join" class="join-button"> </div>
        `;
        room_container.appendChild(roomDiv);
    });
    join_buttons = document.querySelectorAll('.join-button');
}

function joinClickHandler(e) {
    const roomDiv = e.target.closest('.room');
    const roomId = roomDiv.dataset.id;
    connectWebSocket(roomId,stompGameClient);
}

function connectWebSocket(roomId, stompClient){
    connected_id = roomId;
    stompClient.brokerURL += `/${roomId}?Authorization=${jwt_raw}`
    stompClient.activate();
}

async function loadWebsite(){
    winner.style.display = "none";
    game_container.style.display =  "none";
    getRoomList().then((res) => renderRoomList(res))
    .then(() => join_buttons.forEach(btn => {btn.addEventListener("click",(e) => joinClickHandler(e))}));
}

function removeAllChildren(parentElement) {
    while (parentElement.firstChild) {
        parentElement.removeChild(parentElement.firstChild);
    }
}

function renderPlayers(json, players) {
    const parentDiv = document.querySelector('.table-container');
    removeAllChildren(parentDiv);
    let h = 100/players.length;

    players.forEach(player => {
        const playerDiv = document.createElement('div');
        playerDiv.classList.add(player.username);
        playerDiv.style.height = h+"%";
        player.cards.forEach(card => {
            const cardImg = document.createElement('img');
            const cardFile = card.value.toLowerCase() + "_of_" + card.suit.toLowerCase() + ".png";
            cardImg.style.height = "100%";
            cardImg.src = `../png/${cardFile}`;
            playerDiv.appendChild(cardImg);
            
        });
        parentDiv.appendChild(playerDiv);
    });

    const playerDiv = document.querySelector("."+jwt_json['sub']);
    if(json.deck != null){
        const cardImg = document.createElement('img');
        const cardFile = "blank_of_back.png";
        cardImg.src = `../png/${cardFile}`;
        cardImg.style.height = "100%";
        playerDiv.appendChild(cardImg);
        cardImg.addEventListener("click",()=>stompChatClient.publish({
            destination: "/app/game/move/"+connected_id,
            body: JSON.stringify({gameMove:"DRAW"})
        }));
    }
    parentDiv.appendChild(playerDiv);
}

stompGameClient.onConnect = (res) => {
    console.log(res)
    index_container.style.display= "none";
    game_container.style.display =  "flex";
    console.log("GAME CONNECTED");
    connectWebSocket(connected_id,stompChatClient);

    stompGameClient.subscribe('/subscribe/game/'+jwt_json['sub'], (res) => {
        json = JSON.parse(res.body);
        console.log(json)
        renderPlayers(json,json.players);

        if(json.cardsOnTable != null){

        }

        if(json.winner != null){
            winner.style.display = "block";
            winner.innerHTML = json.winner;
        }
    });
}

stompChatClient.onConnect = (res) => {
    console.log("CHAT CONNECTED")
    stompChatClient.subscribe('/subscribe/chat/'+connected_id, (msg) => {
        let json = JSON.parse(msg.body);
        showMessage(json['username'], json['msg']);``
    });
}

create_room.addEventListener("click",() => createRoomHandler().then((res) => connectWebSocket(res,stompGameClient)));
chat_input.addEventListener("keydown",(e) => sendMessage(e));
startButton.addEventListener("click",() => stompGameClient.publish({destination: "/app/game/start/"+connected_id,}));
loadWebsite();