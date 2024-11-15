import {Navigate, Outlet} from "react-router-dom";

export default function ProtectedRoute({allowedRole, children}) {

    // Получаем токен и роль пользователя из sessionStorage или другого подходящего места
    function getAuthToken() {
        return sessionStorage.getItem('jwt');
    }

    function getUserRole() {
        const token = getAuthToken();
        if (token) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.role;
        }
        return null;
    }

    const token = getAuthToken();
    const userRole = getUserRole();

    // Проверка авторизации и роли пользователя
    if (!token) {
        // Если нет токена, перенаправляем на страницу входа
        return <Navigate to="/sign-in" />;
    } else if (allowedRole && userRole !== allowedRole) {
        // Если роль не совпадает с требуемой, перенаправляем на главную страницу
        return <Navigate to="/groups" />;
    }


    // Если авторизация и роль совпадают, рендерим дочерние компоненты
    return children;
}