import axios from 'axios';

let server = {};
server.baseUrl = 'http://localhost:8082';

server.getAllEntities = async (state) =>{
    const token = localStorage.getItem('token')
    const options ={
        method: 'GET',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
        },
        url: server.baseUrl+`/entity/`+state,

    };
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.registerClient = async (client) =>{
    const options ={
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
        },
        data: client,
        url: server.baseUrl+`/auth/signup`,
    };
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.getUserById = async (id) =>{
    const token = localStorage.getItem('token')
    const options ={
        method: 'GET',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
            
        },
        url: server.baseUrl+`/user/getById/`+id,
    };
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.getLoggedUser = async (token) =>{
    const options ={
        method: 'GET',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
        },
        url: server.baseUrl+`/user/getLoggedUser/`,
    }; 
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.getSubscriptions= async (token) =>{
    const options ={
        method: 'GET',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
        },
        url: server.baseUrl+`/entity/subscriptions/`,
    }; 
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.saveDeleteRequest= async (content,token) =>{
    const options ={
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
        },
        url: server.baseUrl+`/user/deleteRequest/`,
        data : content
    }; 
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.updateUser = async(data,token)=>{
    const options ={
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
        },
        url: server.baseUrl+`/user/update/`,
        data : data
    }; 
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};

server.changePassword = async(password,token)=>{
    const options ={
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Accept: 'application/json',
            'Authorization': `Bearer ${token}`
        },
        url: server.baseUrl+`/user/changePassword/` + password,
    }; 
    return axios(options)
    .then(response => handleSuccess(response))
    .catch((error) => handleError(error));
};


function handleError(error) {
	console.log('Error');
	return { success: false, data: error.response.data };
}
function handleSuccess(response) {
	return { success: true, data: response.data || response };
}

export default server;