import { NavLink, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import { useState, useEffect } from "react";

export default function Header() {
    const [userName, setUserName] = useState(null); // Имя пользователя
    const [userRole, setUserRole] = useState(null); // Роль пользователя
    const [isModalOpen, setIsModalOpen] = useState(false); // Состояние модального окна
    const [isSubmitting, setIsSubmitting] = useState(false); // Состояние отправки
    const [errorMessage, setErrorMessage] = useState(null); // Сообщение об ошибке в модальном окне
    const navigate = useNavigate();

    // Функция обновления пользователя из токена
    const updateUserFromToken = (token) => {
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split(".")[1])); // Декодирование токена JWT
                setUserName(payload.sub || "User");
                setUserRole(payload.role || "ROLE_USER");
            } catch (error) {
                console.error("Ошибка декодирования токена:", error);
                setUserName(null);
                setUserRole(null);
            }
        } else {
            setUserName(null);
            setUserRole(null);
        }
    };

    // Проверка токена и установка пользователя
    useEffect(() => {
        const token = sessionStorage.getItem("jwt");
        updateUserFromToken(token);

        const interval = setInterval(() => {
            const currentToken = sessionStorage.getItem("jwt");
            if (currentToken !== token) {
                updateUserFromToken(currentToken);
            }
        }, 500);

        return () => clearInterval(interval);
    }, []);

    // Логика выхода пользователя
    const handleLogout = () => {
        sessionStorage.removeItem("jwt");
        setUserName(null);
        setUserRole(null);
        navigate("/sign-in");
    };

    // Логика отправки заявки на роль администратора
    const applyForAdmin = async () => {
        setIsSubmitting(true);
        setErrorMessage(null);
        try {
            const token = sessionStorage.getItem("jwt");
            console.log(token)
            const response = await fetch("http://localhost:8080/admin/apply", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log(response)
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Не удалось отправить заявку");
            }

            alert("Заявка успешно отправлена!");
            setIsModalOpen(false);
        } catch (error) {
            setErrorMessage(error.message);
        } finally {
            setIsSubmitting(false);
        }
    };

    function closeModalWindow() {
        setErrorMessage(null);
        setIsModalOpen(false);
    }

    return (
        <header className="flex items-center justify-between px-6 py-4 bg-gray-800 text-white shadow-md">
            {/* Левое меню */}
            <div className="flex items-center space-x-6">
                <span className="text-xl font-bold text-indigo-400">
                    <NavLink to="/groups" className="hover:text-indigo-300 transition duration-200">
                        Lab1
                    </NavLink>
                </span>
                <nav className="flex space-x-4">
                    <NavLink
                        to="/groups"
                        className={({isActive}) =>
                            isActive
                                ? "text-indigo-400 font-semibold"
                                : "text-white hover:text-indigo-400"
                        }
                    >
                        All Groups
                    </NavLink>
                    <NavLink
                        to="/special"
                        className={({isActive}) =>
                            isActive
                                ? "text-indigo-400 font-semibold"
                                : "text-white hover:text-indigo-400"
                        }
                    >
                        Special Functions
                    </NavLink>
                    <NavLink
                        to="/admin"
                        className={({isActive}) =>
                            isActive
                                ? "text-indigo-400 font-semibold"
                                : "text-white hover:text-indigo-400"
                        }
                    >
                        Admin Panel
                    </NavLink>
                </nav>
            </div>

            {/* Правая сторона */}
            <div className="flex items-center space-x-4">
                {userName ? (
                    <>
                        <span
                            className="font-medium cursor-pointer hover:text-indigo-300 transition duration-200"
                            onClick={() => setIsModalOpen(true)}
                        >
                            {userName}
                        </span>
                        <FaUserCircle
                            className="text-3xl text-indigo-400 cursor-pointer hover:text-indigo-300 transition duration-200"
                            onClick={() => setIsModalOpen(true)}
                        />
                        <button
                            onClick={handleLogout}
                            className="px-4 py-2 bg-red-500 text-white font-medium rounded hover:bg-red-600 transition duration-200"
                        >
                            Sign out
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

            {/* Модальное окно */}
            {isModalOpen && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
                    <div className="bg-white p-6 rounded shadow-lg w-96">
                        <h2 className="text-lg font-bold mb-4">Ваш профиль</h2>
                        <p className="text-gray-800 mb-4">
                            Имя пользователя: <span className="font-medium">{userName}</span>
                        </p>
                        <p className="text-gray-800 mb-4">
                            Ваша роль: <span className="font-medium">{userRole}</span>
                        </p>
                        {userRole === "ROLE_USER" ? (
                            <>
                                {errorMessage && (
                                    <div className="text-red-500 mb-4">{errorMessage}</div>
                                )}
                                <button
                                    onClick={applyForAdmin}
                                    className="px-4 py-2 bg-indigo-500 text-white font-medium rounded hover:bg-indigo-600 transition duration-200 mb-4 w-full"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? "Отправка..." : "Отправить заявку на админа"}
                                </button>
                            </>
                        ) : (
                            <p className="text-green-500 font-medium">
                                Вы уже являетесь администратором
                            </p>
                        )}
                        <button
                            onClick={() => closeModalWindow()}
                            className="px-4 py-2 bg-gray-300 text-gray-700 font-medium rounded hover:bg-gray-400 transition duration-200 w-full"
                        >
                            Закрыть
                        </button>
                    </div>
                </div>
            )}
        </header>
    );
}
