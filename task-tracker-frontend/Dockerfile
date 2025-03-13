# Используем легковесный образ Nginx на основе Alpine
FROM nginx:alpine

# Копируем шаблон конфига
COPY nginx.conf.template /etc/nginx/conf.d/default.conf.template

# Копируем статические файлы фронтенда в директорию Nginx
COPY index.html /usr/share/nginx/html/
COPY css/ /usr/share/nginx/html/css/
COPY js/ /usr/share/nginx/html/js/

# Указываем, что нужно подставить переменные при запуске
CMD ["/bin/sh", "-c", "envsubst '${BACKEND_API_URL}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]

# Указываем порт, который будет открыт (по умолчанию Nginx слушает 80)
EXPOSE 80