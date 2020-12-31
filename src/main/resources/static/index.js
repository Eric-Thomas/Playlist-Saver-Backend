let build_playlist_html = function(response) {
	let html = '<ul>';
	for (let i = 0; i < response.playlists.length; i++) {
		html += '<li>' + response.playlists[i].playlistName + '</li>';
	}
	html += '</ul>';
	return html
}

// get parameters from hash of the url
let get_hash_params = function() {
	let hash_params = {};
	let e, r = /([^&;=]+)=?([^&;]*)/g;
	let q = window.location.hash.substring(1);
	while (e = r.exec(q)) {
		hash_params[e[1]] = decodeURIComponent(e[2]);
	}
	return hash_params;
}

// used when setting state
let get_random_string = function(length) {
	let alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
	let text = '';
	for (var i = 0; i < length; i++) {
		text += alphabet.charAt(Math.floor(Math.random() * alphabet.length));
	}
	return text;
}

let REDIRECT_KEY = 'spotify_redirect_url';
let STATE_KEY = 'spotify_auth_state'; 

let CLIENT_ID;
let REDIRECT_URI = localStorage.getItem(REDIRECT_KEY);
let SCOPE;

let params = get_hash_params();
let access_token = params.access_token;
let state = params.state;
let stored_state = localStorage.getItem(STATE_KEY);

// Load fields from access.properties
$.ajax({
    url: window.location.origin + '/spotify/properties',
    success: function(response) {
		CLIENT_ID = response.clientId;
		REDIRECT_URI = response.redirectUrl;
		SCOPE = response.scope;
		localStorage.setItem(REDIRECT_KEY, REDIRECT_URI);
	}
});

$('#login-button').click(function() {
	let state = get_random_string(16);
	localStorage.setItem(STATE_KEY, state);
	
	let url = 'https://accounts.spotify.com/authorize';
	url += '?response_type=token';
    url += '&client_id=' + encodeURIComponent(CLIENT_ID);
    url += '&scope=' + encodeURIComponent(SCOPE);
    url += '&redirect_uri=' + encodeURIComponent(REDIRECT_URI);
    url += '&state=' + encodeURIComponent(state);
	window.location = url;
});

$('#load-spotify-button').click(function() {
	$.ajax({
		url: window.location.origin + '/spotify/playlists',
		headers: {oauthToken: params.access_token},
		success: function(response) {
			console.log(response);
			$('#playlist-container').empty()
			$('#playlist-container').append(build_playlist_html(response))
		},
		error: function(response) {
			console.log(response);
		}
	});
	$('#loggedin').hide();
	$('#playlist-container').show();
});

// if an access token exists...
if (access_token && (state == null || state !== stored_state)) {
	// alert('State mismatch authentication error!');
	window.location = REDIRECT_URI;
} else {
	localStorage.removeItem(STATE_KEY);
	if (access_token) {
		$('#login').hide();
		$('#loggedin').show();
	} else {
		$('#login').show();
		$('#loggedin').hide();
	}	
}
