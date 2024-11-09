import { NavLink } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa"; // Если не установлен, добавьте библиотеку react-icons
import { useState } from "react";

export default function Header() {
    const [userName] = useState("John Doe");

    return (
        <header className="flex items-center justify-between px-6 py-4 bg-gray-800 text-white shadow-md">
            {/* Левое меню */}
            <nav className="flex space-x-4">
                <NavLink
                    to="/"
                    className={({ isActive }) =>
                        isActive ? "text-indigo-400 font-semibold" : "text-white hover:text-indigo-400"
                    }
                >
                    Home
                </NavLink>
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

            {/* Правая сторона с именем пользователя и иконкой */}
            {/* Правая сторона с именем пользователя и иконкой */}
            <NavLink to="/auth" className="flex items-center space-x-2 hover:text-indigo-400">
                <span className="font-medium">{userName}</span>
                <FaUserCircle className="text-3xl text-indigo-400" />
            </NavLink>
        </header>
    );
}
