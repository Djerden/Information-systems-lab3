import { NavLink, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa"; // Если не установлен, добавьте библиотеку react-icons
import { useState, useEffect } from "react";

export default function Header() {
    const [userName, setUserName] = useState(null); // Начальное состояние: пользователь не авторизован
    const navigate = useNavigate();

    // Получение имени пользователя из токена
    useEffect(() => {
        const token = sessionStorage.getItem('jwt');
        updateUserNameFromToken(token);
    }, []);

    const updateUserNameFromToken = (token) => {
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1])); // Декодирование токена JWT
                setUserName(payload.sub || "User"); // Установка имени пользователя из токена
            } catch (error) {
                console.error("Ошибка декодирования токена:", error);
                setUserName(null); // Если ошибка, устанавливаем заглушку
            }
        } else {
            setUserName(null); // Если токена нет, пользователь не авторизован
        }
    };

    // Обновляем компонент при каждом изменении токена в sessionStorage
    useEffect(() => {
        const interval = setInterval(() => {
            const token = sessionStorage.getItem('jwt');
            updateUserNameFromToken(token);
        }, 500); // Проверяем каждые 500 мс

        return () => clearInterval(interval); // Очищаем интервал при размонтировании
    }, []);

    // Функция для выхода из аккаунта
    const handleLogout = () => {
        sessionStorage.removeItem('jwt'); // Удаляем токен из sessionStorage
        setUserName(null); // Сбрасываем имя пользователя
        navigate('/sign-in'); // Перенаправляем пользователя на страницу входа
    };

    return (
        <header className="flex items-center justify-between px-6 py-4 bg-gray-800 text-white shadow-md">
            {/* Левое меню с названием сайта */}
            <div className="flex items-center space-x-6">
                <span className="text-xl font-bold text-indigo-400">Lab1</span>
                <nav className="flex space-x-4">
                    <NavLink
                        to="/groups"
                        className={({ isActive }) =>
                            isActive ? "text-indigo-400 font-semibold" : "text-white hover:text-indigo-400"
                        }
                    >
                        All Groups
                    </NavLink>
                    <NavLink
                        to="/admin"
                        className={({ isActive }) =>
                            isActive ? "text-indigo-400 font-semibold" : "text-white hover:text-indigo-400"
                        }
                    >
                        Admin Panel
                    </NavLink>
                </nav>
            </div>

            {/* Правая сторона с именем пользователя, иконкой и кнопками входа/выхода */}
            <div className="flex items-center space-x-4">
                {userName ? (
                    <>
                        <span className="font-medium">{userName}</span>
                        <FaUserCircle className="text-3xl text-indigo-400" />
                        <button
                            onClick={handleLogout}
                            className="px-4 py-2 bg-red-500 text-white font-medium rounded hover:bg-red-600 transition duration-200"
                        >
                            Log out
                        </button>
                    </>
                ) : (
                    <>
                        <NavLink
                            to="/sign-in"
                            className="px-4 py-2 bg-indigo-500 text-white font-medium rounded hover:bg-indigo-600 transition duration-200"
                        >
                            Sign In
                        </NavLink>
                        <NavLink
                            to="/sign-up"
                            className="px-4 py-2 bg-green-500 text-white font-medium rounded hover:bg-green-600 transition duration-200"
                        >
                            Sign Up
                        </NavLink>
                    </>
                )}
            </div>
        </header>
    );
}
