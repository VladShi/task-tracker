$(document).ready(function() {
  // Закрытие модалок при клике вне
  $(window).click((event) => {
    if ($(event.target).hasClass('modal')) {
      $('.modal').removeClass('modal--active').hide();
      window.location.hash = '';
      if (typeof saveTaskIfChanged === 'function') {
        saveTaskIfChanged(); // Сохраняем изменения при закрытии
      }
    }
  });

  // Очистка форм при открытии модалок
  $('#register-btn').click(() => clearForm('#register-modal'));
  $('#login-btn').click(() => clearForm('#login-modal'));
});

// Очистка формы и сообщения
function clearForm(modalSelector) {
  $(`${modalSelector} form`)[0].reset();
  $(`${modalSelector} .message`).css('visibility', 'hidden').text('');
}

// Показать уведомление
function showNotification(message, type = 'success', callback) {
  const $notification = $('.notification');
  $notification
      .text(message)
      .removeClass('notification--error')
      .addClass(type === 'error' ? 'notification--error' : '')
      .css('opacity', 0)
      .show()
      .animate({ opacity: 1 }, 300);

  setTimeout(() => {
    $notification.fadeOut(600, callback);
  }, 1400);
}

// Показать сообщение об ошибке в модалке
function showErrorMessage($element, message) {
  $element
      .text(message)
      .removeClass('message--success')
      .addClass('message--error')
      .css('visibility', 'visible');
}