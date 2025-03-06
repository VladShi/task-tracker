$(document).ready(function() {
  // Close modals on click outside
  $(window).click(function(event) {
    if ($(event.target).hasClass('modal')) {
      window.location.hash = '';
    }
  });

  // Clear forms and messages when opening modals
  $('#register-btn').click(function() {
    $('#register-modal form')[0].reset(); // Очищаем поля формы
    $('#register-modal .message').css('visibility', 'hidden').text(''); // Скрываем и очищаем сообщение
  });

  $('#login-btn').click(function() {
    $('#login-modal form')[0].reset(); // Очищаем поля формы
    $('#login-modal .message').css('visibility', 'hidden').text(''); // Скрываем и очищаем сообщение
  });

  // Check user on page load
  loadUserData();
});
