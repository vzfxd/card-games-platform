const jwt_raw = sessionStorage.getItem("jwt_raw");
const jwt_json = JSON.parse(sessionStorage.getItem("jwt_json"));
const api_url = 'http://localhost:8080/api';
const websocket_url = 'ws://localhost:8080/websocket'

let join_buttons;

if(jwt_raw == null){
    window.location.href = "auth.html";
}

const room_container = document.querySelector(".room-container");
const create_room = document.querySelector(".create-room");
const room_game_container = document.querySelector('.room-game-container');
const index_container = document.querySelector('.index-container');
const chat_input = document.querySelector('.chat-input');
const ul = document.querySelector('ul');

room_game_container.style.display =  "none";

const stompClient = new StompJs.Client({brokerURL:websocket_url});

stompClient.onConnect = function(frame) {
    console.log(frame);
    index_container.style.display= "none";
    room_game_container.style.display =  "grid";
    stompClient.subscribe('/subscribe/chat', (msg) => {
        let json = JSON.parse(msg.body);
        showMessage(json['username'], json['msg']);
    });
}
stompClient.onStompError = function (frame) {
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
  };

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

function showMessage(username, msg){
    const li = document.createElement('li');
    li.innerHTML = `${username}:${msg}`
    ul.appendChild(li);
}

function sendMessage(e){
    if(e.keyCode != 13){
        return
    }
    
    let msg = chat_input.value;
    let username = jwt_json['sub'];

    let json = {username:username,msg:msg};
    console.log(json);
    stompClient.publish({
        destination: "/room/chat",
        body: JSON.stringify(json)
    });
}

async function getRoomList(){
    let end_point = "/room/list";
    let req = new Request('GET',{});
    const res = await fetch(api_url+end_point,req.getJson());
    return await res.json();
}

async function createRoomHandler(){
    let end_point = "/room/create";
    let json = {
        "gameType":document.querySelector('input[name="game-type"]:checked').className.toUpperCase().replace("-",""),
        "playerLimit":parseInt(document.querySelector('.player-limit').value) 
    }
    let req = new Request('POST',json);
    const res = await fetch(api_url+end_point,req.getJson());
    console.log(req.getJson())
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
    stompClient.brokerURL += `/${roomId}?Authorization=${jwt_raw}`
    stompClient.activate();
}


async function loadWebsite(){
    getRoomList().then((res) => renderRoomList(res['info']))
    .then(() => join_buttons.forEach(btn => {btn.addEventListener("click",(e) => joinClickHandler(e))}));
}

create_room.addEventListener("click",() => createRoomHandler().then((res)=>console.log(res)));
chat_input.addEventListener("keydown",(e) => sendMessage(e))

loadWebsite();