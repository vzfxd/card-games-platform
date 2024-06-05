const url = 'http://localhost:8080/api';
const register_api = '/auth/register';
const login_api = '/auth/login';
const register_button = document.querySelector(".register-button");
const login_button = document.querySelector(".login-button");
const username_input = document.querySelector(".username-input");
const password_input = document.querySelector(".password-input");
const info = document.querySelector(".info");

register_button.addEventListener("click",(e) => auth(e).then((res) => registerHandler(res)));
login_button.addEventListener("click",(e) => auth(e).then((res) => loginHandler(res)));

if(sessionStorage.getItem("jwt_raw") != null){
    window.location.href =  "index.html";
}

class AuthRequest {
    constructor(username, password){
        this.username = username;
        this.password = password;
    }

    getJson(){
        return {
            method: 'POST',
            headers: { 
                'Accept': '*/*',
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({username: this.username, password: this.password})
        }
    }
}

async function auth(event){
    let api;
    let btn = event['target'].value;
    if(btn == "register"){
        api = register_api;
    }else{
        api = login_api;
    }

    let username = username_input.value;
    let password = password_input.value;
    let req = new AuthRequest(username,password);
    const res = await fetch(url+api,req.getJson());
    return await res.json();
}

function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

function errorHandler(res){
    let status = res['status'];
    if(status != "OK"){
        info.innerHTML = res['info'];
        return true;
    }
    return false;
}

function loginHandler(res){
    if(errorHandler(res)){
        return 
    }
    let info = res['info'];
    let jwt = parseJwt(info);
    window.location.href = "index.html";
    sessionStorage.setItem("jwt_json", JSON.stringify(jwt));
    sessionStorage.setItem("jwt_raw", info);
}

function registerHandler(res){
    if(errorHandler(res)){
        return 
    }
    info.innerHTML =  "registration successful"
}
  