$('#login-button').click(function() {
	$.ajax({
		url: window.location.origin + '/spotify/login',
		success: function(response) {
			console.log(response);
			$('#loggedin').show();
		}
	})
	$('#login').hide();
});

$('#playlists-button').click(function() {
	$.ajax({
		url: window.location.origin + '/spotify/playlists',
		headers: {oauthToken: ACCESS_TOKEN},
		success: function(response) {
			console.log(response);
			$('#loggedin').append('<h6>' + response + '</h6>')
		},
		error: function(response) {
			$('#playlists-button').hide();
			
		}
	});
	$('#playlists-button').hide();
	$('#loggedin').append($('#playlists-template').show());
});
