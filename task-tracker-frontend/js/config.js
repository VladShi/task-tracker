const Config = {
    backendUrl: 'http://localhost:8080'
};

if (typeof window.BACKEND_URL !== 'undefined') {
    Config.backendUrl = window.BACKEND_URL;
}