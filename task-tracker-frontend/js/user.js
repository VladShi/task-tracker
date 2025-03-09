function loadUserData() {
  const token = localStorage.getItem('jwt');
  if (!token) {
    $('main .container').html(`
            <p class="main__welcome">Welcome! Please register or log in.</p>
        `);
    return;
  }

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

function loadTasks() {
  const token = localStorage.getItem('jwt');
  $.ajax({
    url: `${Config.backendUrl}/api/tasks`,
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

      // Обработчики событий
      $('.task-card').click(function() {
        const taskId = $(this).data('id');
        console.log(`Edit task ${taskId}`);
      });

      $('.task-card__delete').click(function(e) {
        e.stopPropagation();
        const taskId = $(this).parent().data('id');
        deleteTask(taskId);
      });

      $('.task-card__status').click(function(e) {
        e.stopPropagation();
        const taskId = $(this).parent().data('id');
        const title = $(this).parent().data('title');
        const description = $(this).parent().data('description');
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
    url: `${Config.backendUrl}/api/tasks/${taskId}`,
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

function toggleTaskStatus(taskId, title, description) {
  const token = localStorage.getItem('jwt');
  const $statusButton = $('.task-card[data-id="' + taskId + '"]').find('.task-card__status');
  const isCompleted = $statusButton.hasClass('task-card__status--complete'); // Проверяем текущий статус
  const completed = !isCompleted; // Инвертируем: false -> true, true -> false
  $.ajax({
    url: `${Config.backendUrl}/api/tasks/${taskId}`,
    type: 'PATCH',
    headers: { 'Authorization': token },
    contentType: 'application/json',
    data: JSON.stringify({ title, description, completed }),
    success: () => {
      loadTasks();
      showNotification('Task status updated.', 'success');
    },
    error: (xhr) => {
      showNotification('Failed to update task status.', 'error');
    }
  });
}