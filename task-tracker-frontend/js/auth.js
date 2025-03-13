$(document).ready(function() {
  // Валидация пароля
  function validatePassword(password) {
    const passwordRegex = /^[a-zA-Z0-9!@#$%^&*_-]*$/;
    return password && password.length >= 4 && password.length <= 20 && passwordRegex.test(password);
  }

  // Проверка токена при загрузке страницы
  const token = localStorage.getItem('jwt');
  if (token) {
    loadUserData();
  }

  // Регистрация
  $('#register-form').submit(function(event) {
    event.preventDefault();

    const email = $('#reg-email').val();
    const password = $('#reg-password').val();
    const confirmPassword = $('#reg-confirm-password').val();
    const $errorMessage = $('#register-modal .message');

    $errorMessage.css('visibility', 'hidden');

    if (!validatePassword(password)) {
      showErrorMessage($errorMessage, 'Password must be 4-20 characters and contain only English letters, numbers, and !@#$%^&*_-');
      return;
    }

    if (password !== confirmPassword) {
      showErrorMessage($errorMessage, 'Passwords do not match');
      return;
    }

    const data = { login: email, password, confirmPassword };

    $.ajax({
      url: `/api/user`,
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: (response, status, xhr) => {
        const token = xhr.getResponseHeader('Authorization');
        localStorage.setItem('jwt', token);
        window.location.hash = '';
        loadUserData();
        showNotification('Registration successful!', 'success');
      },
      error: (xhr) => {
        const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Registration failed';
        showErrorMessage($errorMessage, errorMsg);
      }
    });
  });

  // Логин
  $('#login-form').submit(function(event) {
    event.preventDefault();

    const email = $('#login-email').val();
    const password = $('#login-password').val();
    const $errorMessage = $('#login-modal .message');

    $errorMessage.css('visibility', 'hidden');

    if (!validatePassword(password)) {
      showErrorMessage($errorMessage, 'Password must be 4-20 characters and contain only English letters, numbers, and !@#$%^&*_-');
      return;
    }

    const data = { login: email, password };

    $.ajax({
      url: `/api/auth/login`,
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: (response, status, xhr) => {
        const token = xhr.getResponseHeader('Authorization');
        localStorage.setItem('jwt', token);
        window.location.hash = '';
        loadUserData();
        showNotification('Login successful!', 'success');
      },
      error: (xhr) => {
        const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Login failed';
        showErrorMessage($errorMessage, errorMsg);
      }
    });
  });
});