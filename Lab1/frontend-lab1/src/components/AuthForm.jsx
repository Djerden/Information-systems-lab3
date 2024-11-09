import { useState } from 'react';
import { Form } from 'react-router-dom';

export default function AuthForm() {
    const [isLogin, setIsLogin] = useState(true);

    function switchAuthHandler() {
        setIsLogin((isCurrentlyLogin) => !isCurrentlyLogin);
    }

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <Form method="post" className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
                    {isLogin ? 'Log in' : 'Create a new user'}
                </h1>

                <div className="mb-4">
                    <label htmlFor="email" className="block text-gray-600 font-medium mb-1">Email</label>
                    <input
                        id="email"
                        type="email"
                        name="email"
                        required
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
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring focus:ring-indigo-100"
                    />
                </div>

                <div className="flex justify-between items-center mb-4">
                    <button
                        onClick={switchAuthHandler}
                        type="button"
                        className="text-indigo-500 hover:underline focus:outline-none"
                    >
                        {isLogin ? 'Create new user' : 'Login'}
                    </button>

                    <button
                        type="submit"
                        className="px-4 py-2 bg-indigo-500 text-white font-medium rounded hover:bg-indigo-600 transition duration-200"
                    >
                        Save
                    </button>
                </div>
            </Form>
        </div>
    );
}


