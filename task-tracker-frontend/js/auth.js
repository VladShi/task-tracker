$(document).ready(function() {
  $('#register-modal form').submit(function(event) {
    event.preventDefault();

    const email = $('#reg-email').val();
    const password = $('#reg-password').val();
    const confirmPassword = $('#reg-confirm-password').val();

    const $errorMessage = $('#register-modal .message');
    $errorMessage.css('visibility', 'hidden');

    const passwordRegex = /^[a-zA-Z0-9!@#$%^&*_-]*$/;
    if (!password || password.length < 4 || password.length > 20 || !passwordRegex.test(password)) {
      $errorMessage.text('Password must be 4-20 characters and contain only English letters, numbers, and !@#$%^&*_-').removeClass('success').addClass('error').css('visibility', 'visible');
      return;
    }

    if (password !== confirmPassword) {
      $errorMessage.text('Passwords do not match').removeClass('success').addClass('error').css('visibility', 'visible');
      return;
    }

    const data = {
      login: email,
      password: password,
      confirmPassword: confirmPassword
    };

    $.ajax({
      url: Config.backendUrl + '/api/user',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: function(response, status, xhr) {
        const token = xhr.getResponseHeader('Authorization');
        localStorage.setItem('jwt', token);
        window.location.hash = '';
        loadUserData();
        showSuccessMessage('Registration successful!');
      },
      error: function(xhr) {
        const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Registration failed';
        $errorMessage.text(errorMsg).removeClass('success').addClass('error').css('visibility', 'visible');
      }
    });
  });

  $('#login-modal form').submit(function(event) {
    event.preventDefault();

    const email = $('#login-email').val();
    const password = $('#login-password').val();

    const $errorMessage = $('#login-modal .message');
    $errorMessage.css('visibility', 'hidden');

    const passwordRegex = /^[a-zA-Z0-9!@#$%^&*_-]*$/;
    if (!password || password.length < 4 || password.length > 20 || !passwordRegex.test(password)) {
      $errorMessage.text('Password must be 4-20 characters and contain only English letters, numbers, and !@#$%^&*_-').removeClass('success').addClass('error').css('visibility', 'visible');
      return;
    }

    const data = {
      login: email,
      password: password
    };

    $.ajax({
      url: Config.backendUrl + '/api/auth/login',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: function(response, status, xhr) {
        const token = xhr.getResponseHeader('Authorization');
        localStorage.setItem('jwt', token);
        window.location.hash = '';
        loadUserData();
        showSuccessMessage('Login successful!');
      },
      error: function(xhr) {
        const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Login failed';
        $errorMessage.text(errorMsg).removeClass('success').addClass('error').css('visibility', 'visible');
      }
    });
  });
});

function showSuccessMessage(message) {
  const $notification = $('.notification');
  $notification.text(message).css('opacity', 0).show().animate({ opacity: 1 }, 300);
  setTimeout(function() {
    $notification.fadeOut(600);
  }, 1400);
}
