import { Link } from "react-router-dom";

export default function ErrorPage() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 text-gray-800">
            <div className="text-center px-6 py-10">
                <h1 className="text-9xl font-bold text-indigo-500 mb-4">404</h1>
                <h2 className="text-3xl font-semibold mb-2">Oops! Page not found</h2>
                <p className="text-lg mb-8">
                    The page you’re looking for doesn’t exist or has been moved.
                </p>
                <Link to="/" className="inline-block bg-indigo-500 text-white px-6 py-3 rounded-lg hover:bg-indigo-600 transition duration-300">
                    Go Home
                </Link>
            </div>
        </div>
    );
}
