function loadUserData() {
  const token = localStorage.getItem('jwt');
  if (!token) return;

  $.ajax({
    url: `${Config.backendUrl}/api/user`,
    type: 'GET',
    headers: { 'Authorization': token },
    success: (data) => {
      const navHtml = `
                <span class="header__user">${data.email}</span>
                <button class="btn btn--error" id="logout-btn">Logout</button>
            `;
      $('header .header__nav').html(navHtml);
      $('main .container').html('<p class="main__status">You are logged in. Tasks will appear here.</p>');

      $('#logout-btn').click(() => {
        localStorage.removeItem('jwt');
        location.reload();
      });
    },
    error: (xhr) => {
      if (xhr.status === 401) {
        localStorage.removeItem('jwt');
        showNotification('Session expired, please log in again.', 'error', () => {
          location.reload();
        });
      }
    }
  });
}