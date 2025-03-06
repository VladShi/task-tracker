function loadUserData() {
  const token = localStorage.getItem('jwt');
  if (token) {
    $.ajax({
      url: Config.backendUrl + '/api/user',
      type: 'GET',
      headers: { 'Authorization': token },
      success: function(data) {
        $('header nav').html(`
                    <span>Hello, ${data.email}!</span>
                    <button id="logout-btn">Logout</button>
                `);
        $('main .container').html('<p>You are logged in. Tasks will appear here.</p>');

        $('#logout-btn').click(function() {
          localStorage.removeItem('jwt');
          location.reload();
        });
      },
      error: function(xhr) {
        if (xhr.status === 401) {
          localStorage.removeItem('jwt');
          showErrorNotification('Session expired, please log in again.');
          setTimeout(function() {
            location.reload();
          }, 2000);
        }
      }
    });
  }
}

function showErrorNotification(message) {
  const $notification = $('.notification');
  $notification.text(message).addClass('error').css('opacity', 0).show().animate({ opacity: 1 }, 300);
  setTimeout(function() {
    $notification.fadeOut(600);
  }, 1400);
}
