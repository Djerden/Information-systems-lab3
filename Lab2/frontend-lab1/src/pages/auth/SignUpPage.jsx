import { useState } from 'react';
import { Form, useNavigate } from 'react-router-dom';

export default function SignUpPage() {

    const [user, setUser] = useState({
        username: null,
        password: null,
        confirmPassword: null
    });
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // Функция переключения на страницу аутентификации
    function switchAuthHandler() {
        navigate('/sign-in');
    }

    function toMainPage() {
        navigate('/groups');
    }

    // Функция обновления параметров username, password и confirmPassword, когда пользователь изменяет поля ввода
    function handleChange(param, value) {
        setUser(prevUser => ({
            ...prevUser,
            [param]: value
        }));
    }

    // Функция для отправки данных при регистрации
    function handleSignUp(e) {
        e.preventDefault();

        if (user.password !== user.confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        fetch('http://localhost:8080/auth/sign-up', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: user.username ? user.username.trim() : null, // Удаляем пробелы из username
                password: user.password
            })
        })
            .then(async (response) => {
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'An error occurred during registration');
                }
                return response.json();
            })
            .then(responseData => {
                const jwtToken = responseData.token; // JWT токен находится в теле ответа
                if (jwtToken) {
                    sessionStorage.setItem('jwt', jwtToken); // Сохраняем токен в sessionStorage
                    toMainPage(); // Переход на главную страницу
                } else {
                    setError("Failed to receive token");
                }
            })
            .catch(err => {
                console.error(err);
                setError(err.message || "An error occurred during registration");
            });
    }


    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <Form method="post" className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
                    Sign Up
                </h1>

                {error && <p className="text-red-500 text-center mb-4">{error}</p>}

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

                <div className="mb-4">
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

                <div className="mb-6">
                    <label htmlFor="confirmPassword" className="block text-gray-600 font-medium mb-1">Confirm Password</label>
                    <input
                        id="confirmPassword"
                        type="password"
                        name="confirmPassword"
                        required
                        onChange={(e) => handleChange('confirmPassword', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring focus:ring-indigo-100"
                    />
                </div>

                <div className="flex justify-between items-center mb-4">
                    <button
                        onClick={switchAuthHandler}
                        type="button"
                        className="text-indigo-500 hover:underline focus:outline-none"
                    >
                        Already have an account?
                    </button>

                    <button
                        type="submit"
                        onClick={handleSignUp}
                        className="px-4 py-2 bg-indigo-500 text-white font-medium rounded hover:bg-indigo-600 transition duration-200"
                    >
                        Sign Up
                    </button>
                </div>
            </Form>
        </div>
    );
}
