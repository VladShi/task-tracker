function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

// Остальные функции остаются в начале файла...

function loadUserData() {
  const token = localStorage.getItem('jwt');
  if (!token) {
    $('main .container').html(`
            <p class="main__welcome">Welcome! Please register or log in.</p>
        `);
    return;
  }

  $.ajax({
    url: `/api/user`,
    type: 'GET',
    headers: { 'Authorization': token },
    success: (data) => {
      const navHtml = `
                <span class="header__user">${data.email}</span>
                <button class="btn btn--error" id="logout-btn">Logout</button>
            `;
      $('header .header__nav').html(navHtml);
      $('main .container').html(`
                <div class="add-task-container">
                    <button class="add-task-btn" id="add-task-btn">Add New Task</button>
                </div>
                <div class="tasks">
                    <div class="tasks__column tasks__column--todo">
                        <h2 class="tasks__title">To Do<span class="tasks__count"></span></h2>
                        <div class="tasks__list" id="todo-list"></div>
                    </div>
                    <div class="tasks__column tasks__column--done">
                        <h2 class="tasks__title">Completed<span class="tasks__count"></span></h2>
                        <div class="tasks__list" id="done-list"></div>
                    </div>
                </div>
            `);

      $('#logout-btn').click(() => {
        localStorage.removeItem('jwt');
        location.reload();
      });
      loadTasks();
      setupEventListeners();
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

function formatDate(timestamp) {
  const date = new Date(timestamp);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `<span class="time"><i class="material-icons">schedule</i> ${hours}:${minutes}</span><span><i class="material-icons">calendar_today</i> ${day}-${month}-${year}</span>`;
}

function setupEventListeners() {
  // Открытие модального окна для добавления задачи
  $('#add-task-btn').click(() => {
    $('#add-task-modal').addClass('modal--active').show();
    $('#add-title').val('');
    $('#add-description').val('');
    window.location.hash = 'add-task-modal';
  });

  // Открытие модального окна для редактирования задачи
  $(document).on('click', '.task-card', function(e) {
    if ($(e.target).closest('.task-card__delete, .task-card__status').length) return; // Игнорируем клики по кнопкам
    const taskId = $(this).data('id');
    if (!taskId) return;
    $('#edit-task-modal').data('task-id', taskId); // Устанавливаем taskId
    $.ajax({
      url: `/api/tasks/${taskId}`,
      type: 'GET',
      headers: { 'Authorization': localStorage.getItem('jwt') },
      success: (task) => {
        $('#edit-title').val(task.title);
        $('#edit-description').val(task.description);
        $('#edit-created-at').html(formatDate(task.createdAt));
        const completedAtHtml = task.completed ? formatDate(task.completedAt) : '';
        $('#edit-status').removeClass('task-card__status--complete task-card__status--todo')
            .addClass(task.completed ? 'task-card__status--complete' : 'task-card__status--todo')
            .html(`Done <i class="material-icons">${task.completed ? 'check_box' : 'check_box_outline_blank'}</i>`);
        $('#edit-completed-at').html(completedAtHtml);
        $('#edit-task-modal').addClass('modal--active').show();
        window.location.hash = 'edit-task-modal';
        $('#edit-task-modal').data('changed', false); // Сбрасываем флаг изменений
      },
      error: (xhr) => {
        showNotification('Failed to load task.', 'error');
      }
    });
  });

  // Закрытие модальных окон
  $(document).on('click', '.modal__close', (e) => {
    e.preventDefault();
    const $modal = $('.modal--active');
    $modal.removeClass('modal--active').hide();
    window.location.hash = '';
    if ($modal.attr('id') === 'edit-task-modal' && $modal.data('changed')) {
      const taskId = $modal.data('task-id');
      const title = $('#edit-title').val().trim();
      const description = $('#edit-description').val().trim();
      const completed = $('#edit-status').hasClass('task-card__status--complete');
      if (taskId) {
        saveTask(taskId, title, description, completed);
        showNotification('Task updated.', 'success');
      }
    }
  });

  // Кнопка Save для создания новой задачи
  $('#save-new-task').click((e) => {
    e.preventDefault();
    const title = $('#add-title').val().trim();
    const description = $('#add-description').val().trim();
    if (title || description) {
      const token = localStorage.getItem('jwt');
      $.ajax({
        url: `/api/tasks`,
        type: 'POST',
        headers: { 'Authorization': token },
        contentType: 'application/json',
        data: JSON.stringify({ title, description }),
        success: (task) => {
          loadTasks();
          $('#add-task-modal').removeClass('modal--active').hide();
          window.location.hash = '';
          showNotification('Task created.', 'success');
        },
        error: (xhr) => {
          showNotification(xhr.responseJSON?.message || 'Failed to create task.', 'error');
        }
      });
    }
  });

  // Автоматическое сохранение с debounce только для редактирования
  const debouncedSave = debounce(saveTaskIfChanged, 1000); // Задержка 1 секунда
  $('#edit-title, #edit-description').on('input', function() {
    $('#edit-task-modal').data('changed', true); // Устанавливаем флаг изменений
    debouncedSave(); // Отложенная отправка
  });

  // Обработчик чекбокса Done в модальном окне редактирования
  $(document).on('click', '#edit-status', function(e) {
    e.stopPropagation();
    const taskId = $('#edit-task-modal').data('task-id');
    if (!taskId) return;
    const title = $('#edit-title').val();
    const description = $('#edit-description').val();
    const completed = !$(this).hasClass('task-card__status--complete');
    $('#edit-task-modal').data('changed', true); // Устанавливаем флаг изменений
    updateTaskStatus(taskId, title, description, completed);
  });
}

function loadTasks() {
  const token = localStorage.getItem('jwt');
  $.ajax({
    url: `/api/tasks`,
    type: 'GET',
    headers: { 'Authorization': token },
    success: (tasks) => {
      const todoList = $('#todo-list');
      const doneList = $('#done-list');
      todoList.empty();
      doneList.empty();

      const todoTasks = tasks
          .filter(task => !task.completed)
          .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      const doneTasks = tasks
          .filter(task => task.completed)
          .sort((a, b) => new Date(b.completedAt) - new Date(a.completedAt));

      $('.tasks__column--todo .tasks__count').text(todoTasks.length > 0 ? todoTasks.length : '');
      $('.tasks__column--done .tasks__count').text(doneTasks.length > 0 ? doneTasks.length : '');

      todoTasks.forEach(task => {
        const cardHtml = `
                    <div class="task-card" data-id="${task.id}" data-title="${task.title}" data-description="${task.description}">
                        <div class="task-card__title">${task.title}</div>
                        <div class="task-card__description">${task.description.slice(0, 50)}${task.description.length > 50 ? '...' : ''}</div>
                        <div class="task-card__date">${formatDate(task.createdAt)}</div>
                        <div class="task-card__status task-card__status--todo">Done <i class="material-icons">check_box_outline_blank</i></div>
                        <div class="task-card__delete"></div>
                    </div>
                `;
        todoList.append(cardHtml);
      });

      doneTasks.forEach(task => {
        const cardHtml = `
                    <div class="task-card" data-id="${task.id}" data-title="${task.title}" data-description="${task.description}">
                        <div class="task-card__title">${task.title}</div>
                        <div class="task-card__description">${task.description.slice(0, 50)}${task.description.length > 50 ? '...' : ''}</div>
                        <div class="task-card__date">${formatDate(task.completedAt)}</div>
                        <div class="task-card__status task-card__status--complete">Done <i class="material-icons">check_box</i></div>
                        <div class="task-card__delete"></div>
                    </div>
                `;
        doneList.append(cardHtml);
      });

      // Управление прокруткой
      if (todoTasks.length <= 4) {
        todoList.css('overflow-y', 'hidden');
      } else {
        todoList.css('overflow-y', 'auto');
      }

      if (doneTasks.length <= 4) {
        doneList.css('overflow-y', 'hidden');
      } else {
        doneList.css('overflow-y', 'auto');
      }

      // Обработчики событий для кнопок удаления и изменения статуса
      $(document).off('click', '.task-card__delete').on('click', '.task-card__delete', function(e) {
        e.stopPropagation();
        const taskId = $(this).parent().data('id');
        if (!taskId) return;
        deleteTask(taskId);
      });

      $(document).off('click', '.task-card__status').on('click', '.task-card__status', function(e) {
        e.stopPropagation();
        const $parentCard = $(this).closest('.task-card');
        if ($parentCard.length === 0) return; // Игнорируем, если клик не внутри карточки
        const taskId = $parentCard.data('id');
        const title = $parentCard.data('title');
        const description = $parentCard.data('description');
        if (!taskId) return;
        toggleTaskStatus(taskId, title, description);
      });
    },
    error: (xhr) => {
      showNotification('Failed to load tasks.', 'error');
    }
  });
}

function deleteTask(taskId) {
  const token = localStorage.getItem('jwt');
  $.ajax({
    url: `/api/tasks/${taskId}`,
    type: 'DELETE',
    headers: { 'Authorization': token },
    success: () => {
      loadTasks();
      showNotification('Task deleted.', 'success');
    },
    error: (xhr) => {
      showNotification('Failed to delete task.', 'error');
    }
  });
}

let isUpdating = false; // Флаг для предотвращения множественных вызовов

function toggleTaskStatus(taskId, title, description) {
  if (isUpdating) return;
  isUpdating = true;
  const token = localStorage.getItem('jwt');
  const $statusButton = $('.task-card[data-id="' + taskId + '"]').find('.task-card__status');
  const isCompleted = $statusButton.hasClass('task-card__status--complete');
  const completed = !isCompleted;
  $.ajax({
    url: `/api/tasks/${taskId}`,
    type: 'PUT',
    headers: { 'Authorization': token },
    contentType: 'application/json',
    data: JSON.stringify({ title, description, completed }),
    success: () => {
      loadTasks();
      showNotification('Task status updated.', 'success');
    },
    error: (xhr) => {
      showNotification('Failed to update task status.', 'error');
    },
    complete: () => {
      isUpdating = false;
    }
  });
}

function saveTaskIfChanged() {
  const token = localStorage.getItem('jwt');
  if (!token) return;

  const isEditModalOpen = $('#edit-task-modal').hasClass('modal--active');
  if (isEditModalOpen) {
    const taskId = $('#edit-task-modal').data('task-id');
    const title = $('#edit-title').val().trim();
    const description = $('#edit-description').val().trim();
    const completed = $('#edit-status').hasClass('task-card__status--complete');
    if (taskId && (title || description)) {
      saveTask(taskId, title, description, completed);
    }
  }
}

function saveTask(taskId, title, description, completed) {
  const token = localStorage.getItem('jwt');
  if (!token || !taskId) return;

  $.ajax({
    url: `/api/tasks/${taskId}`,
    type: 'PUT',
    headers: { 'Authorization': token },
    contentType: 'application/json',
    data: JSON.stringify({ title, description, completed }),
    success: (task) => {
      loadTasks();
    },
    error: (xhr) => {
      showNotification(xhr.responseJSON?.message || 'Failed to update task.', 'error');
    }
  });
}

function updateTaskStatus(taskId, title, description, completed) {
  if (isUpdating) return;
  isUpdating = true;
  const token = localStorage.getItem('jwt');
  $.ajax({
    url: `/api/tasks/${taskId}`,
    type: 'PUT',
    headers: { 'Authorization': token },
    contentType: 'application/json',
    data: JSON.stringify({ title, description, completed }),
    success: (task) => {
      $('#edit-completed-at').html(task.completed ? formatDate(task.completedAt) : '');
      $('#edit-status').removeClass('task-card__status--complete task-card__status--todo')
          .addClass(task.completed ? 'task-card__status--complete' : 'task-card__status--todo')
          .html(`Done <i class="material-icons">${task.completed ? 'check_box' : 'check_box_outline_blank'}</i>`);
      loadTasks();
    },
    error: (xhr) => {
      showNotification(xhr.responseJSON?.message || 'Failed to update task status.', 'error');
    },
    complete: () => {
      isUpdating = false;
    }
  });
}