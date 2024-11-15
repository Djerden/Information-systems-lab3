import { useState } from 'react';
import { Form, useNavigate } from 'react-router-dom';

export default function SignInPage() {

    const [user, setUser] = useState({
        username: null,
        password: null
    });
    const [error, setError] = useState(null); // состояние для хранения ошибки

    const navigate = useNavigate()

    // функция обновления параметров username и password, когда пользователь изменяет поля ввода
    function handleChange(param, value) {
        setUser(prevUser => ({
            ...prevUser,
            [param]: value
        }));
    }

    // функция переключения страниц аутентификации и регистрации
    function switchAuthHandler() {
        navigate('/sign-up')
    }
    function toMainPage() {
        navigate('/groups')
    }

    // Запрос для аутентификации
    function handleLogin(e) {
        e.preventDefault();
        setError(null); // Сброс ошибки перед запросом

        fetch('http://localhost:8080/auth/sign-in', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
            .then(async (res) => {
                const responseBody = await res.json();
                if (!res.ok) {
                    throw new Error(responseBody.message || 'Ошибка авторизации');
                }

                // Извлекаем токен из тела ответа
                const jwtToken = responseBody.token;

                if (!jwtToken) {
                    throw new Error('Токен не получен');
                }

                sessionStorage.setItem('jwt', jwtToken); // Сохраняем токен в sessionStorage

                toMainPage(); // Переход на главную страницу
            })
            .catch(err => setError(err.message)); // Отображаем сообщение об ошибке
    }


    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <Form method="post" className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
                    Log in
                </h1>

                {error && ( // отображаем сообщение об ошибке, если оно есть
                    <div className="mb-4 text-red-500 text-center">
                        {error}
                    </div>
                )}

                <div className="mb-4">
                    <label htmlFor="username" className="block text-gray-600 font-medium mb-1">Username</label>
                    <input
                        id="username"
                        type="text"
                        name="username"
                        required
                        onChange={(e) => handleChange('username', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring focus:ring-indigo-100"
                    />
                </div>

                <div className="mb-6">
                    <label htmlFor="password" className="block text-gray-600 font-medium mb-1">Password</label>
                    <input
                        id="password"
                        type="password"
                        name="password"
                        required
                        onChange={(e) => handleChange('password', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring focus:ring-indigo-100"
                    />
                </div>

                <div className="flex justify-between items-center mb-4">
                    <button
                        onClick={switchAuthHandler}
                        type="button"
                        className="text-indigo-500 hover:underline focus:outline-none"
                    >
                        Create new user
                    </button>

                    <button
                        type="submit"
                        onClick={handleLogin}
                        className="px-4 py-2 bg-indigo-500 text-white font-medium rounded hover:bg-indigo-600 transition duration-200"
                    >
                        Log in
                    </button>
                </div>
            </Form>
        </div>
    );
}